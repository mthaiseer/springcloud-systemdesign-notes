# CP Mathematical Modeling Mini-Course

## 19. Constructive Mathematical Modeling

> **Goal:** Learn how to solve problems where you do not need to find a unique answer — you need to **build any valid answer** satisfying the constraints.
>
> Core mindset:
>
> ```text
> DON'T ask:
> "What is the answer?"
>
> ASK:
> "What properties must my output have,
> and how can I deliberately build them?"
> ```

---

# Chapter Tree

```text
19. CONSTRUCTIVE MATHEMATICAL MODELING
│
├── 19.1 What is a constructive problem?
├── 19.2 Output conditions → mathematical conditions
├── 19.3 Necessary vs sufficient conditions
├── 19.4 Work backward from the target
├── 19.5 Start from the easiest valid structure
├── 19.6 Build one object at a time
├── 19.7 Maintain an invariant while constructing
├── 19.8 Track remaining resources / deficit
├── 19.9 Local repair
├── 19.10 Pairing and grouping constructions
├── 19.11 Permutation constructions
├── 19.12 Parity / modulo constructions
├── 19.13 Extremal constructions
├── 19.14 Reverse construction
├── 19.15 Impossible vs constructible
├── 19.16 Proving the construction
└── 19.17 CF-style constructive workflow
```

---

# 19.1 What Is a Constructive Problem?

Normal problem:

```text
Input
 ↓
calculate one answer
 ↓
print number
```

Constructive problem:

```text
Input
 ↓
find ANY object satisfying conditions
 ↓
print that object
```

The object may be:

```text
array
permutation
string
sequence of operations
graph
pairing
partition
matrix
```

There may be thousands of correct outputs.

Your job is not:

```text
find THE answer
```

but:

```text
find ONE easy-to-prove answer
```

---

# 19.2 Recognizing Constructive Statements

Typical phrases:

```text
construct...
print any...
find any...
output one possible...
if possible, print...
otherwise print -1
```

These tell you:

```text
many outputs may be accepted
```

That freedom is extremely important.

Do not unnecessarily search for:

```text
lexicographically smallest
minimum
maximum
```

unless the statement asks for it.

---

# 19.3 First Step — Rewrite the Output Conditions

Suppose:

> Construct an array of `n` positive integers whose sum is `S`.

Remove the story.

Need:

```text
a1 + a2 + ... + an = S
```

with:

```text
ai >= 1
```

Now the problem is pure mathematics.

Before constructing anything, write:

```text
WHAT MUST BE TRUE?
```

---

# 19.4 Necessary Conditions

For:

```text
a1 + ... + an = S
ai >= 1
```

minimum possible sum is:

```text
n
```

Therefore:

```text
S >= n
```

is necessary.

If:

```text
S < n
```

construction is impossible.

This is the first constructive habit:

```text
Before building,
derive when building is even possible.
```

---

# 19.5 Sufficient Conditions

Now suppose:

```text
S >= n
```

Can we always construct?

Yes.

Start:

```text
1 1 1 ... 1
```

Current sum:

```text
n
```

Remaining:

```text
S-n
```

Put all remaining amount into one element:

```text
1 1 ... 1  [1 + (S-n)]
```

Now sum is exactly:

```text
S
```

Therefore:

```text
S >= n
```

is not only necessary.

It is sufficient.

So:

```text
construction exists
iff
S >= n
```

---

# 19.6 Necessary vs Sufficient

This distinction is critical.

```text
NECESSARY:
Every valid construction must satisfy it.

SUFFICIENT:
If it holds, we know how to build a valid construction.
```

A common mistake:

```text
find an impossibility condition
then assume everything else is possible
```

Not enough.

You need either:

```text
proof of sufficiency
```

or preferably:

```text
explicit construction
```

The construction itself often proves sufficiency.

---

# 19.7 Work Backward From the Required Output

Suppose target requires:

```text
a[i] != a[i+1]
```

Instead of generating arbitrary values and checking them, ask:

```text
What simple pattern automatically satisfies this?
```

One answer:

```text
1 2 1 2 1 2 ...
```

Visual:

```text
1 2 1 2 1 2
 \ / \ / \ /
 different
```

The target condition suggests the structure.

This is **backward construction**:

```text
required property
      ↓
simple structure guaranteeing it
```

---

# 19.8 Prefer Structures That Make Conditions Automatic

Suppose you need:

```text
all elements distinct
```

Do not randomly choose numbers and maintain a set unless necessary.

Use:

```text
1,2,3,...,n
```

Distinctness is automatic.

Suppose you need increasing values:

```text
a1 < a2 < ... < an
```

Use:

```text
1,2,3,...,n
```

Again automatic.

Constructive principle:

```text
Choose a representation where
the hardest condition becomes impossible to violate.
```

---

# 19.9 Start From the Simplest Valid Baseline

Suppose each:

```text
ai >= L
```

and total sum must be:

```text
S
```

Start every element at its minimum:

```text
L L L ... L
```

Baseline sum:

```text
nL
```

Remaining resource:

```text
R = S - nL
```

Now the original problem becomes:

```text
distribute R extra units
```

This is much simpler.

---

# 19.10 Baseline + Extra

General pattern:

```text
required minimum
      ↓
give everyone minimum
      ↓
calculate remaining resource
      ↓
distribute remaining safely
```

Formula:

```text
a[i] = L + extra[i]
```

where:

```text
extra[i] >= 0
```

and:

```text
sum extra[i] = S - nL
```

This transformation appears constantly in resource constructions.

---

# 19.11 Add Upper Bounds

Now suppose:

```text
L <= ai <= U
```

and:

```text
sum ai = S
```

Minimum possible total:

```text
nL
```

Maximum possible total:

```text
nU
```

Therefore necessary:

```text
nL <= S <= nU
```

Is it sufficient?

Yes.

Start:

```text
L L ... L
```

Remaining:

```text
R = S-nL
```

Each position can receive at most:

```text
U-L
```

extra.

Process positions:

```text
add = min(R, U-L)
a[i] += add
R -= add
```

until:

```text
R = 0
```

Because:

```text
S <= nU
```

there is enough total capacity.

Thus:

```text
nL <= S <= nU
```

is necessary and sufficient.

---

# 19.12 Visual Resource Construction

Example:

```text
n = 4
L = 2
U = 5
S = 15
```

Baseline:

```text
2 2 2 2
```

sum:

```text
8
```

remaining:

```text
7
```

Each box has capacity:

```text
U-L = 3
```

Fill:

```text
2 2 2 2
↓ +3

5 2 2 2
remaining = 4

5 2 2 2
  ↓ +3

5 5 2 2
remaining = 1

5 5 2 2
    ↓ +1

5 5 3 2
remaining = 0
```

Final:

```text
5 5 3 2
```

sum:

```text
15
```

All bounds hold.

---

# 19.13 Build One Object at a Time

Another major strategy:

```text
construct prefix
      ↓
choose next object
      ↓
preserve validity
      ↓
repeat
```

At step `i`, ask:

```text
What choices are legal now?

What resources must remain
for the unfinished suffix?
```

This leads naturally to greedy constructions.

---

# 19.14 Future Feasibility While Constructing

Suppose you need `n` numbers:

```text
L <= ai <= U
```

with remaining sum:

```text
R
```

and there are:

```text
k
```

positions left.

For the remaining positions to be feasible:

```text
kL <= R <= kU
```

This gives a powerful rule.

When choosing the current value `x`, the new remaining sum:

```text
R-x
```

must satisfy:

```text
(k-1)L <= R-x <= (k-1)U
```

This is constructive feasibility modeling.

---

# 19.15 Maintain an Invariant

Construction often works because after every step you preserve a statement.

Example invariant:

```text
The already-built prefix is valid,
and the remaining resource can still
complete the suffix.
```

Initially:

```text
true
```

After choosing each element:

```text
prove it remains true
```

At the end:

```text
no suffix remains
```

so the whole object is valid.

This is a clean way to prove constructive algorithms.

---

# 19.16 Pairing Construction

Suppose numbers:

```text
1,2,...,n
```

must be paired to create equal pair sums.

For even `n`, pair extremes:

```text
1 with n
2 with n-1
3 with n-2
...
```

Every pair sum:

```text
n+1
```

Example:

```text
n=8

1 + 8 = 9
2 + 7 = 9
3 + 6 = 9
4 + 5 = 9
```

ASCII:

```text
1  2  3  4  5  6  7  8
|  |  |  |  |  |  |  |
└─────────────────────┘
   └────────────────┘
      └───────────┘
         └─────┘
```

This is an extremal construction:

```text
smallest + largest
```

---

# 19.17 Why Pairing Extremes Works

Sorted:

```text
1 2 3 ... n
```

Pair:

```text
i
```

with:

```text
n+1-i
```

Then:

```text
i + (n+1-i)
=
n+1
```

The desired equality is built directly into the formula.

This is stronger than trial-and-error pairing.

---

# 19.18 Permutation Construction

A permutation of `1..n` must use every number exactly once.

The simplest starting construction:

```text
1 2 3 ... n
```

Then modify order to satisfy additional conditions.

Example target:

```text
adjacent values should be far apart
```

Possible idea:

```text
small, large, second-small, second-large...
```

Example `n=8`:

```text
1 8 2 7 3 6 4 5
```

The important modeling question:

```text
What ordering makes the desired
local relation easy to guarantee?
```

---

# 19.19 Construct From Categories

Sometimes exact values matter less than categories.

Example:

```text
odd / even
```

Suppose adjacent values must have alternating parity.

Then first construct the parity pattern:

```text
O E O E O E ...
```

Only afterward assign actual unused numbers from each category.

This separates:

```text
STRUCTURE
```

from:

```text
VALUES
```

Very useful in constructive problems.

---

# 19.20 Two-Layer Construction

General pattern:

```text
Layer 1:
construct abstract categories

Layer 2:
replace categories with actual values
```

Examples:

```text
odd/even
positive/negative
color A/B
small/large
remainder classes
```

This is state compression used in reverse:

```text
compress to discover structure
then expand into a concrete answer
```

---

# 19.21 Parity Construction

Suppose you need `k` positive integers summing to `n`.

### Want all odd

Minimum sum of `k` positive odd numbers:

```text
k
```

Parity of sum of `k` odd numbers:

```text
k % 2
```

Therefore necessary:

```text
n >= k
```

and:

```text
n % 2 = k % 2
```

Construction:

```text
1 1 ... 1
```

for first `k-1` numbers.

Last:

```text
n-(k-1)
```

Because parity matches, the last number is odd.

Because:

```text
n >= k
```

it is positive.

So the conditions are sufficient.

---

# 19.22 Example — Sum Into Odd Parts

```text
n = 15
k = 5
```

Check:

```text
15 >= 5
```

and:

```text
15 % 2 = 1
5 % 2  = 1
```

Construct:

```text
1 1 1 1 11
```

All positive.

All odd.

Sum:

```text
15
```

---

# 19.23 Even-Part Construction

Need `k` positive even integers summing to `n`.

Minimum:

```text
2k
```

Sum must be even.

Necessary:

```text
n >= 2k
n % 2 = 0
```

Construction:

```text
2 2 ... 2
```

for first `k-1`.

Last:

```text
n - 2(k-1)
```

It is even and at least `2`.

Again:

```text
necessary conditions
      +
explicit construction
      =
sufficiency proof
```

---

# 19.24 Remainder-Class Construction

Suppose every element must satisfy:

```text
ai % m = r
```

Write:

```text
ai = r + m*ki
```

with appropriate adjustment when positive values and `r=0` are involved.

Then sum:

```text
S = nr + m * sum(ki)
```

So a necessary remainder condition is:

```text
S % m = (nr) % m
```

The construction problem becomes distributing:

```text
(S-nr)/m
```

units among the `ki`.

This converts a modulo requirement into a resource-distribution problem.

---

# 19.25 Constructive Thinking From Equations

Suppose target equation is:

```text
x + y = S
```

Do not search every pair.

Choose one variable conveniently:

```text
x = 1
y = S-1
```

Then verify constraints.

If:

```text
x,y > 0
```

this works whenever:

```text
S >= 2
```

Constructive principle:

```text
When many solutions exist,
fix variables to convenient values
and solve for the remaining ones.
```

---

# 19.26 Degrees of Freedom

Equation:

```text
x+y+z=S
```

has many solutions.

You may choose:

```text
x = simplest legal value
y = simplest legal value
```

then:

```text
z = S-x-y
```

This is essentially variable elimination used constructively.

The free variables are your **degrees of freedom**.

Use them to make the construction easy.

---

# 19.27 Work Backward From the Last Element

A very common construction:

```text
choose first n-1 values simply
```

then force the last value:

```text
last = target - current
```

Example sum target:

```text
a[n-1] = S - sum(first n-1)
```

Then verify:

```text
Is last legal?
```

Many constructive solutions reduce to choosing a prefix that guarantees the final forced value lies within bounds.

---

# 19.28 Reverse Construction

Sometimes forward choices are awkward because the final target is highly constrained.

Then start from the target.

Example:

```text
target state T
```

If forward operation is:

```text
x -> f(x)
```

ask:

```text
What states could immediately precede T?
```

Then repeat backward.

This is useful when reverse operations are:

```text
more deterministic
```

than forward operations.

---

# 19.29 Sequence of Operations as the Output

Sometimes you must print operations, not the final object.

Then construction becomes:

```text
current state
      ↓
desired state
      ↓
find a measure of error
      ↓
choose operation reducing error
      ↓
record operation
      ↓
repeat
```

The error might be:

```text
number of misplaced elements
difference from target
inversions
wrong parity positions
unmatched objects
```

---

# 19.30 Repair Construction

Instead of building from scratch:

```text
start with easy almost-valid object
```

then:

```text
identify violations
```

and:

```text
repair them locally
```

Pattern:

```text
easy baseline
     ↓
find bad positions
     ↓
pair / swap / modify them
     ↓
all violations disappear
```

This is common with permutations and strings.

---

# 19.31 Example — Fix Wrong Parity Positions

Suppose a permutation requires:

```text
value parity = index parity
```

Find positions:

```text
Ebad = even indices containing odd values
Obad = odd indices containing even values
```

A swap between:

```text
one Ebad
and
one Obad
```

fixes both positions.

Therefore feasibility requires:

```text
|Ebad| = |Obad|
```

Construction:

```text
pair bad positions
swap each pair
```

This combines:

```text
classification
+ counting
+ local repair
+ constructive pairing
```

---

# 19.32 Repair Must Have Progress

For every repair operation, define a measure:

```text
bad = number of violations
```

Then prove:

```text
each repair decreases bad
```

and does not create uncontrolled new violations.

Example:

```text
bad -> bad-2
```

Then termination is obvious.

This is constructive proof via a decreasing potential.

---

# 19.33 Build Using Extremes

Sometimes easiest valid object uses:

```text
smallest available
largest available
```

Example arrangement:

```text
1 n 2 n-1 3 n-2 ...
```

Why useful?

It creates controlled:

```text
large differences
alternation
pair sums
```

Extremes are not only for optimization.

They are powerful building blocks.

---

# 19.34 Symmetry Construction

If conditions are symmetric, try a symmetric output.

Examples:

```text
palindrome:
a b c b a

equal pair sums:
1 n
2 n-1
...

balanced signs:
+x -x
```

Symmetry can make global constraints cancel automatically.

Ask:

```text
Can I pair terms so unwanted effects cancel?
```

---

# 19.35 Cancellation Construction

Suppose sum must be zero.

Easy building blocks:

```text
+x, -x
```

Each pair contributes:

```text
0
```

Example:

```text
1 -1 2 -2 3 -3
```

Instead of controlling the whole sum globally, build local zero-sum blocks.

This is a major constructive idea:

```text
global property
      ↓
repeat small blocks
that already satisfy it
```

---

# 19.36 Block Construction

Suppose a property can be guaranteed for a small block.

Example valid block:

```text
A B B A
```

If concatenating valid blocks preserves the global condition, construct:

```text
BLOCK + BLOCK + BLOCK + ...
```

This reduces arbitrary `n` to:

```text
n mod block_size
```

Then only leftover cases require special handling.

This connects constructive modeling with casework.

---

# 19.37 Periodic Construction

If constraints depend only on nearby positions, a repeating pattern may work:

```text
1 2 3 1 2 3 1 2 3 ...
```

or:

```text
A B A B A B ...
```

Check:

```text
Does every local window satisfy the condition?
```

If yes, the infinite periodic pattern can solve every length, possibly with a short suffix.

---

# 19.38 Construction by Sorting

Sometimes the input objects already contain what you need, but in the wrong order.

Then:

```text
sort
```

and construct from:

```text
smallest / largest / neighboring
```

Examples:

```text
pair extremes
alternate low/high
group equal values
place rare categories first
```

Sorting creates predictable structure.

---

# 19.39 Impossible vs Possible Split

Many constructive problems have the shape:

```text
if condition fails:
    print NO / -1
else:
    print construction
```

Derive this in the correct order:

```text
1. Find necessary conditions.
2. Try to construct whenever they hold.
3. If construction always works,
   conditions are also sufficient.
4. Implement the split.
```

Do not guess the impossible condition from examples alone.

---

# 19.40 A Complete Constructive Proof

A good proof has three parts.

### 1. Existence condition

Explain why impossible cases cannot work.

Example:

```text
S < nL
```

is impossible because every one of `n` elements is at least `L`.

### 2. Construction

Describe exactly what you output.

Example:

```text
start all at L
distribute S-nL extra
without exceeding U
```

### 3. Verification

Prove:

```text
number of elements = n
every value is in [L,U]
sum = S
```

That is enough.

---

# 19.41 Construction Proof Template

Use:

```text
NECESSITY:
Any valid answer must satisfy ______.

CONSTRUCTION:
When that condition holds, build ______.

VALIDITY:
Property 1 holds because ______.
Property 2 holds because ______.
...
Therefore the construction is valid.

COMPLEXITY:
O(...)
```

This template works for many CF constructive editorials.

---

# 19.42 Do Not Over-Optimize

If statement says:

```text
print any valid permutation
```

and you found a simple `O(n)` construction, stop.

Do not search for:

```text
best-looking
smallest
most balanced
```

unless required.

Constructive freedom is an advantage.

Use it.

---

# 19.43 Tiny Cases Are Essential

Try:

```text
n=1
n=2
n=3
n=4
```

Why?

Constructive patterns often fail at small sizes.

Example repeating block of size 2:

```text
A B A B...
```

may behave differently for:

```text
n=1
```

Likewise pairing requires even counts.

Tiny cases reveal:

```text
leftovers
parity restrictions
minimum-size exceptions
```

---

# 19.44 Pattern Discovery Table

When searching for a construction, manually write:

```text
n=1: ?
n=2: ?
n=3: ?
n=4: ?
n=5: ?
n=6: ?
```

Then look for:

```text
repeat every 2?
repeat every 3?
odd/even split?
small impossible prefix?
pairing?
symmetry?
```

Do not generate random large examples first.

---

# 19.45 Constraint Budget During Construction

Suppose you are choosing current value `x`.

Do not only ask:

```text
Is x legal now?
```

Also ask:

```text
After choosing x,
can the remaining positions still be completed?
```

This is the difference between:

```text
local validity
```

and:

```text
extendability
```

A constructive greedy choice must preserve extendability.

---

# 19.46 Example — Preserve Remaining Sum

Need `k` remaining values each in:

```text
[L,U]
```

Remaining total:

```text
R
```

Before choosing `x`:

```text
kL <= R <= kU
```

After choosing `x`, require:

```text
(k-1)L <= R-x <= (k-1)U
```

Rearrange:

```text
R-(k-1)U <= x <= R-(k-1)L
```

Also:

```text
L <= x <= U
```

Therefore valid current choices satisfy:

```text
max(L, R-(k-1)U)
<= x <=
min(U, R-(k-1)L)
```

This is a beautiful example of deriving constructive choices mathematically.

---

# 19.47 Construction Can Become Greedy

Once you know the safe interval:

```text
LOW <= x <= HIGH
```

you may choose:

```text
x = LOW
```

or:

```text
x = HIGH
```

because the inequality guarantees future feasibility.

So:

```text
mathematical feasibility
      ↓
safe choice interval
      ↓
greedy construction
```

---

# 19.48 Constructive Modeling + Invariants

Suppose you are constructing a permutation incrementally.

Possible invariant:

```text
1. Prefix contains no duplicate.
2. Prefix satisfies local condition.
3. Remaining unused values are enough
   to complete the suffix.
```

At each step:

```text
choose value
```

that preserves all three.

This is a general constructive design method.

---

# 19.49 Constructive Modeling + Modulo

Suppose desired total:

```text
sum % m = r
```

An easy approach may be:

```text
build arbitrary legal first n-1 elements
```

then choose last element to repair the remainder.

Current remainder:

```text
cur
```

Need last remainder:

```text
need = (r-cur+m)%m
```

Then choose a legal value from that remainder class.

Again:

```text
build easy prefix
      ↓
use final degree of freedom
to repair the global condition
```

---

# 19.50 Constructive Modeling + Extremes

Suppose pair sum must be constant.

Pairing extremes gives:

```text
min + max
```

Suppose differences should be large.

Alternate extremes:

```text
min, max, second-min, second-max...
```

Suppose you want balance.

Pair complementary values.

The extremal chapter is therefore also a construction toolkit.

---

# 19.51 Constructive Modeling + State Compression

Suppose values themselves are complicated but condition depends only on parity.

First solve:

```text
which positions need O/E?
```

Then assign actual values.

Example:

```text
compressed construction:
O E E O O E

expand:
1 2 4 3 5 6
```

This often makes the reasoning much simpler.

---

# 19.52 Constructive Modeling + Reverse Thinking

Forward question:

```text
"What should I do now?"
```

may have too many possibilities.

Reverse question:

```text
"What must the final step look like?"
```

may be forced.

Then determine:

```text
second-last state
third-last state
...
```

Use whichever direction has fewer choices.

---

# 19.53 Common Construction Families

When stuck, test these:

```text
1. identity:
   1 2 3 ... n

2. reverse:
   n ... 3 2 1

3. alternating:
   A B A B ...

4. extremes:
   1 n 2 n-1 ...

5. pairs:
   (1,n), (2,n-1), ...

6. blocks:
   repeat a small valid pattern

7. baseline + leftover:
   minimum values + distribute extra

8. all simple except last:
   force final value from equation

9. category first:
   parity/remainder/sign pattern

10. repair:
    build easy state then fix violations

11. reverse:
    construct backward from target
```

These are starting points, not formulas to memorize blindly.

---

# 19.54 Common Mistakes

## Mistake 1 — Searching for a unique answer

If any valid output is accepted, exploit the freedom.

---

## Mistake 2 — Proving only necessity

You also need to show how to construct when conditions hold.

---

## Mistake 3 — Random trial-and-error output

Build conditions into the structure.

---

## Mistake 4 — Greedy choice destroys future feasibility

Track remaining resource/capacity.

---

## Mistake 5 — Ignoring tiny cases

Pair/block patterns often have small exceptions.

---

## Mistake 6 — Construction works on examples but has no proof

Verify every required property.

---

## Mistake 7 — Repair operation creates new violations forever

Define a progress measure.

---

## Mistake 8 — Overcomplicating the output

"Any valid answer" usually rewards simple structures.

---

# 19.55 Constructive Scratchpad

Before coding, fill:

```text
OUTPUT MUST SATISFY:
1. __________________
2. __________________
3. __________________

NECESSARY CONDITIONS:
______________________

EASIEST BASELINE:
______________________

REMAINING DEFICIT / RESOURCE:
______________________

WHAT CAN I CHOOSE FREELY?
______________________

WHAT VALUE IS FORCED?
______________________

INVARIANT WHILE BUILDING:
______________________

SMALL EXCEPTIONS:
______________________

WHY CONSTRUCTION ALWAYS WORKS:
______________________
```

---

# 19.56 The Constructive Decision Tree

```text
                  NEED TO PRINT AN OBJECT
                           │
                           ▼
                 WRITE ALL CONDITIONS
                           │
                           ▼
              DERIVE IMPOSSIBLE CASES
                           │
                           ▼
          Can a standard simple structure work?
                           │
       ┌───────────────────┼───────────────────┐
       │                   │                   │
    baseline            pairing            periodic
   + leftover          extremes             blocks
       │                   │                   │
       └───────────────────┼───────────────────┘
                           ▼
                Need more flexibility?
                           │
                ┌──────────┴──────────┐
                │                     │
          choose prefix           build categories
          force suffix            then expand
                │                     │
                └──────────┬──────────┘
                           ▼
                   violations remain?
                           │
                    YES → REPAIR
                           │
                           ▼
                prove progress/termination
                           │
                           ▼
                  VERIFY EVERY CONDITION
                           │
                           ▼
                          CODE
```

---

# 19.57 CF-Style Workflow

When you see:

```text
Construct / print any / output a sequence
```

run:

```text
STEP 1
Ignore algorithms.

STEP 2
Write exact output constraints.

STEP 3
Derive obvious necessary conditions.

STEP 4
Try n=1,2,3,4.

STEP 5
Ask for the simplest structure:
identity?
alternation?
pairing?
blocks?
extremes?
baseline + leftover?

STEP 6
Use degrees of freedom:
fix easy values,
force remaining values.

STEP 7
Check future feasibility
after every greedy choice.

STEP 8
If nearly valid,
classify violations and repair.

STEP 9
Prove:
necessary + construction + validity.

STEP 10
Implement exactly that construction.
```

---

# 19.58 Five-Second Recognition Triggers

```text
"print any"
    ↓
exploit freedom

"sum exactly S"
    ↓
baseline + remaining deficit

"all values in [L,U]"
    ↓
nL <= S <= nU

"equal pair property"
    ↓
pair complements / extremes

"adjacent pattern"
    ↓
alternating / periodic block

"parity requirement"
    ↓
construct parity classes first

"permutation"
    ↓
identity / reverse / extreme ordering

"sequence of operations"
    ↓
define error + repeatedly repair

"possible or -1"
    ↓
necessary conditions + explicit sufficiency
```

---

# 19.59 Final Mental Engine

```text
                  REQUIRED OUTPUT
                         │
                         ▼
                WHAT MUST BE TRUE?
                         │
                         ▼
               NECESSARY CONDITIONS
                         │
                         ▼
             CHOOSE SIMPLEST BASELINE
                         │
       ┌─────────────────┼──────────────────┐
       │                 │                  │
    minimum           pattern            pairing
    values            / blocks           / extremes
       │                 │                  │
       └─────────────────┼──────────────────┘
                         ▼
               WHAT IS STILL MISSING?
                         │
                         ▼
               DEFICIT / VIOLATIONS
                         │
             ┌───────────┴───────────┐
             │                       │
         distribute                repair
         remaining                locally
             │                       │
             └───────────┬───────────┘
                         ▼
              PRESERVE EXTENDABILITY
                         │
                         ▼
                 VERIFY CONDITIONS
                         │
                         ▼
                   PROVE IT WORKS
                         │
                         ▼
                       OUTPUT
```

The core constructive habit is:

```text
Do not generate candidates
and hope one works.

Design the output so the required
properties are true by construction.
```

---

# 19.60 Chapter Mastery Test

You are ready for the next chapter when you see:

```text
"print any valid ..."
```

and automatically think:

```text
1. What conditions must the output satisfy?

2. What conditions make it impossible?

3. What is the simplest valid baseline?

4. Can I use:
   baseline + leftover?
   pairs?
   blocks?
   alternation?
   extremes?
   categories?

5. What freedom do I have?

6. Can I force the last value?

7. If I choose now, can I still finish later?

8. If I repair locally, does a badness
   measure strictly decrease?

9. Why does my construction satisfy
   EVERY required condition?
```

---

# Next Chapter

```text
19. CONSTRUCTIVE MATHEMATICAL MODELING
                  ↓
20. CASEWORK & PIECEWISE MODELING
```

Chapter 20 will focus on problems where one global formula is not enough:

```text
split by parity
split by sign
split by ordering
split by boundary cases
small finite-state casework
derive one formula per region
then merge/simplify cases
```
