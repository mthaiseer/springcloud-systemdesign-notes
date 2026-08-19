# CP Mathematical Modeling Mini-Course

## 17. State Compression & Equivalence Modeling

> **Goal:** Learn to recognize when the exact value is unnecessary.
>
> Many Codeforces problems look large because values may be huge:
>
> ```text
> a[i] <= 10^18
> ```
>
> but the operation/condition may care only about:
>
> ```text
> parity
> remainder
> sign
> zero/non-zero
> greater/equal/smaller
> frequency
> difference
> ```
>
> The modeling skill is:
>
> ```text
> full state
>    ↓
> what information can affect the future?
>    ↓
> keep only that information
> ```

---

# Chapter Tree

```text
17. STATE COMPRESSION & EQUIVALENCE MODELING
│
├── 17.1 What is a state?
├── 17.2 Relevant vs irrelevant information
├── 17.3 Equivalent states
├── 17.4 Parity compression
├── 17.5 Remainder compression
├── 17.6 Sign / zero-state compression
├── 17.7 Comparison-state compression
├── 17.8 Difference instead of two values
├── 17.9 Balance instead of two counts
├── 17.10 Frequency instead of positions
├── 17.11 Boolean masks
├── 17.12 Small finite-state transitions
├── 17.13 Compression from invariants
├── 17.14 Compression from the target
├── 17.15 When compression loses information
└── 17.16 CF-style modeling workflow
```

Central engine:

```text
FULL PROBLEM STATE
        │
        ▼
What can future operations/tests observe?
        │
        ▼
Which properties actually matter?
        │
   ┌────┼─────┬─────┐
   │    │     │     │
 parity mod   sign  difference
   │    │     │     │
   └────┴─────┴─────┘
        │
        ▼
COMPRESSED STATE
        │
        ▼
smaller reasoning space
```

---

# 17.1 What Is a State?

A **state** is the information required to describe the current situation.

Example:

```text
x = 1,000,000,007
```

Full state:

```text
x
```

But suppose every operation and condition cares only whether `x` is odd or even.

Then the useful state is only:

```text
x % 2
```

So:

```text
1,000,000,007
```

and:

```text
3
```

are equivalent for this problem because both are odd.

---

# 17.2 Relevant Information

Ask:

```text
If I replace the current value with another
value having property P,
can the rest of the problem distinguish them?
```

If not, the exact value is unnecessary.

Example operation:

```text
x -> x + 2
```

Question:

```text
Can x ever become even?
```

Adding `2` preserves parity.

Therefore all odd values behave alike:

```text
1,3,5,7,...
```

and all even values behave alike:

```text
0,2,4,6,...
```

Compressed state space:

```text
{EVEN, ODD}
```

instead of potentially billions of integers.

---

# 17.3 Equivalence Classes

Two states are equivalent if, for the purpose of the problem, they have identical future behavior.

For modulo `m`:

```text
x ≡ y (mod m)
```

means:

```text
x % m = y % m
```

Example modulo 3:

```text
class 0:
0,3,6,9,...

class 1:
1,4,7,10,...

class 2:
2,5,8,11,...
```

If all relevant operations/conditions depend only on `%3`, every integer belongs to one of only:

```text
3 states
```

---

# 17.4 Parity Compression

Parity means:

```text
x % 2
```

Only two states:

```text
0 = even
1 = odd
```

Operations can be modeled as transitions.

### Add an even number

```text
E -> E
O -> O
```

### Add an odd number

```text
E -> O
O -> E
```

ASCII:

```text
+ even:
EVEN ───> EVEN
ODD  ───> ODD

+ odd:
EVEN ───> ODD
ODD  ───> EVEN
```

Instead of tracking the integer, track only the parity state.

---

# 17.5 Example — Sum Parity

Suppose the problem asks whether:

```text
a+b+c
```

is even.

You do not need `a`, `b`, `c`.

Only:

```text
a%2
b%2
c%2
```

matter.

Because:

```text
(a+b+c)%2
=
(a%2+b%2+c%2)%2
```

Huge numbers collapse to three bits of information.

---

# 17.6 Count Odd Values Instead of Values

Suppose you need parity of an array sum.

Each even number contributes:

```text
0 mod 2
```

Each odd contributes:

```text
1 mod 2
```

Therefore:

```text
sum parity
=
(number of odd elements) % 2
```

So sometimes state compresses further:

```text
entire array
   ↓
countOdd
   ↓
countOdd % 2
```

Always ask how far compression can safely go.

---

# 17.7 Remainder Compression

Suppose question asks:

```text
Is sum divisible by m?
```

Exact sum may be huge.

Track:

```text
sum % m
```

When adding `x`:

```text
new_rem
=
(old_rem + x%m) % m
```

State space:

```text
0,1,...,m-1
```

This is one of the most common forms of mathematical state compression.

---

# 17.8 Example — Divisibility by 5

Sequence:

```text
12,18,27
```

Remainders mod 5:

```text
2,3,2
```

Sum remainder:

```text
(2+3+2)%5
=
7%5
=
2
```

No need to retain:

```text
12+18+27 = 57
```

if divisibility by 5 is the only question.

---

# 17.9 Operations on Remainder States

Suppose operation:

```text
x -> x+4
```

and only `x % 6` matters.

Compressed transition:

```text
r -> (r+4)%6
```

State graph:

```text
0 -> 4
1 -> 5
2 -> 0
3 -> 1
4 -> 2
5 -> 3
```

A problem over arbitrarily large integers can become a graph over six states.

This is a powerful modeling idea.

---

# 17.10 Sign Compression

Sometimes only sign matters:

```text
NEGATIVE
ZERO
POSITIVE
```

For multiplication:

```text
positive * positive -> positive
positive * negative -> negative
negative * negative -> positive
anything * zero     -> zero
```

If the question asks only:

```text
sign of product
```

you need:

```text
Is there a zero?
How many negative factors?
```

because:

```text
zero exists -> product zero

otherwise:
even number negatives -> positive
odd number negatives  -> negative
```

The actual magnitudes are irrelevant.

---

# 17.11 Zero / Non-Zero Compression

Sometimes even sign is too much information.

Example:

> Is the product zero?

Only need:

```text
does any factor equal 0?
```

Compressed state:

```text
ZERO_SEEN = false/true
```

The right compression depends on the exact question.

Do not keep information merely because it is easy to keep.

---

# 17.12 Comparison-State Compression

Suppose two values `a` and `b` change together and the only condition asks whether:

```text
a < b
a = b
a > b
```

Then useful state may be only:

```text
LESS
EQUAL
GREATER
```

For some operations, exact values disappear completely.

Example:

```text
a += 5
b += 5
```

The relation never changes:

```text
a-b
```

is invariant.

So comparison state is preserved too.

---

# 17.13 Difference Instead of Two Values

Suppose problem tracks:

```text
a
b
```

but all operations/questions depend on their separation.

Define:

```text
d = a-b
```

Now instead of state:

```text
(a,b)
```

use:

```text
d
```

Example:

```text
a += 3
b += 1
```

Then:

```text
d' = (a+3)-(b+1)
   = d+2
```

A 2-variable problem becomes a 1-variable problem.

This is one of the most useful compressions in operation problems.

---

# 17.14 Example — Equalization

Goal:

```text
a = b
```

Equivalent:

```text
a-b = 0
```

So define:

```text
d = a-b
```

Now the target is simply:

```text
d = 0
```

If one operation does:

```text
a += 2
b += 1
```

then:

```text
d += 1
```

The entire problem can often be reasoned about using only `d`.

---

# 17.15 Balance Instead of Two Counts

Suppose sequence contains:

```text
A
B
```

and you care whether a segment has equal counts.

Instead of storing:

```text
countA
countB
```

store:

```text
balance = countA-countB
```

Contribution:

```text
A -> +1
B -> -1
```

Equal counts:

```text
balance = 0
```

This connects directly with prefix modeling:

```text
equal prefix balances
→ equal A/B count in between
```

Two counters compressed to one difference.

---

# 17.16 More General Balance

Suppose target relation is:

```text
2*countA = 3*countB
```

Move everything to one side:

```text
2*countA - 3*countB = 0
```

Assign contributions:

```text
A -> +2
B -> -3
```

Then target becomes:

```text
balance = 0
```

This is a general modeling technique:

```text
linear relation
      ↓
weighted balance
```

---

# 17.17 Frequency Instead of Positions

Suppose order does not matter and values are small categories:

```text
1,2,3
```

Instead of array:

```text
[1,3,1,2,3,1,...]
```

store:

```text
cnt[1]
cnt[2]
cnt[3]
```

If operations depend only on choosing value types, positions are irrelevant.

State compresses from:

```text
entire arrangement
```

to:

```text
frequency vector
```

This is extremely common in CF constructive/operation problems.

---

# 17.18 When Can Order Be Discarded?

Ask:

```text
If I permute the array,
does anything relevant change?
```

If no:

```text
order is irrelevant
```

and frequencies/multiset may be sufficient.

If operations mention:

```text
adjacent
subarray
prefix
position i
```

then order probably matters.

This simple question can radically reduce the state.

---

# 17.19 Presence Mask

Suppose values are from a tiny universe:

```text
0..4
```

and only presence matters.

Instead of counts:

```text
cnt[0..4]
```

store bits:

```text
bit i = 1 if value i exists
```

Example present values:

```text
{0,2,4}
```

mask:

```text
10101
```

This is a bitmask state.

Useful when:

```text
number of categories is small
```

and the problem cares about subsets rather than multiplicities.

---

# 17.20 Boolean Property Vector

Sometimes each object has a few yes/no properties.

Example:

```text
hasA?
hasB?
hasC?
```

There are only:

```text
2^3 = 8
```

possible property combinations.

Instead of storing complex descriptions, classify each object by one of 8 masks.

This turns complicated categories into a tiny finite state space.

---

# 17.21 Small Finite-State Transition

Suppose only:

```text
x % 3
```

matters.

Operation A:

```text
x += 1
```

Operation B:

```text
x *= 2
```

Transitions:

```text
A:
0 -> 1
1 -> 2
2 -> 0

B:
0 -> 0
1 -> 2
2 -> 1
```

Now operation sequences can be studied on:

```text
3 states
```

instead of all integers.

ASCII:

```text
FULL:
..., -100, -99, ..., 1000000000, ...

COMPRESSED mod 3:
       {0,1,2}
```

---

# 17.22 Compression From the Target

Sometimes the target tells you what information matters.

Target:

```text
make sum even
```

Immediately think:

```text
sum % 2
```

Target:

```text
make value divisible by 7
```

Think:

```text
value % 7
```

Target:

```text
make counts equal
```

Think:

```text
difference of counts
```

Target:

```text
make positions coincide
```

Think:

```text
relative displacement
```

A powerful question is:

```text
"What expression equals zero / has a specific remainder
when the goal is achieved?"
```

Track that expression.

---

# 17.23 Compression From Operations

Operations can also reveal the state.

Suppose:

```text
x -> x+6
```

Then:

```text
x % 6
```

never changes.

If the target asks reachability under repeated `+6`, remainder class is immediately important.

Suppose:

```text
a += 3
b += 3
```

Then:

```text
a-b
```

never changes.

So inspect:

```text
what does one operation preserve?
```

That often tells you the compressed state.

---

# 17.24 Compression From Invariants

Chapter 11 asked:

```text
What stays unchanged?
```

Chapter 17 asks:

```text
If this is all that matters,
can I discard everything else?
```

Example:

```text
operation changes x by multiples of 4
```

Invariant:

```text
x % 4
```

If reachability only depends on remainder plus direction, you may reason mostly in terms of:

```text
remainder
```

and:

```text
relative size
```

rather than exact intermediate values.

---

# 17.25 Compression Can Be Multiple Properties

Sometimes one property is insufficient.

Example:

Operation:

```text
x += 6
```

Target reachability.

Remainder mod 6 is necessary:

```text
x%6 = y%6
```

but if only `+6` is allowed, direction also matters:

```text
y >= x
```

So useful information may be:

```text
remainder + ordering/difference sign
```

Do not over-compress.

---

# 17.26 The Safety Test for Compression

Before discarding information, ask:

```text
Can two states with the same compressed representation
ever produce different answers later?
```

If yes:

```text
compression is invalid
```

If no:

```text
compression is safe
```

This is the most important correctness test in this chapter.

---

# 17.27 Bad Compression Example

Suppose target asks:

```text
Can x become exactly 20
using x += 4?
```

If you store only:

```text
x % 4
```

then:

```text
x = 4
```

and:

```text
x = 24
```

both have remainder 0.

But:

```text
4 -> 20
```

is reachable.

```text
24 -> 20
```

is not, because moves only increase.

So remainder alone loses essential direction information.

---

# 17.28 Good Compression Example

Suppose operation allows:

```text
x += 4
x -= 4
```

with no bounds.

Now any two integers with the same remainder mod 4 can reach each other.

Then remainder is sufficient for reachability.

Same compressed state really does imply same reachability behavior.

---

# 17.29 Compression and DP

DP state design is fundamentally a compression problem.

Naive state may include:

```text
everything that happened before
```

But DP asks:

```text
What information from the past
is sufficient to make future decisions?
```

Examples:

```text
index
remaining capacity
last value
mask
remainder
```

Although this mini-course is about mathematical modeling, this exact habit directly improves DP state design.

---

# 17.30 Compression and BFS

Suppose values are huge but only remainder matters.

Instead of BFS over integers:

```text
0,1,2,...,10^18
```

BFS over:

```text
0..m-1
```

if transitions and target truly depend only on remainder.

Again, first prove equivalent states have equivalent future behavior.

---

# 17.31 Compression and Counting

Suppose there are `n` numbers and only parity matters.

Instead of array values, store:

```text
E = number of evens
O = number of odds
```

Now pair types are:

```text
E+E
E+O
O+O
```

Their sum parity:

```text
E+E -> even
O+O -> even
E+O -> odd
```

Counting even-sum pairs:

```text
C(E,2) + C(O,2)
```

Huge input values collapse to two frequencies.

---

# 17.32 Complete CF-Style Example 1 — Even-Sum Pairs

Array:

```text
[2,5,7,8,10]
```

Parity classes:

```text
even: 2,8,10 -> E=3
odd:  5,7    -> O=2
```

Even-sum pair requires same parity.

Count:

```text
C(3,2) + C(2,2)
=
3 + 1
=
4
```

Exact values were irrelevant.

---

# 17.33 Complete CF-Style Example 2 — Equal Counts

String:

```text
A B A A B B
```

Map:

```text
A -> +1
B -> -1
```

Running balance:

```text
0
1
0
1
2
1
0
```

Whenever two prefix balances are equal, the segment between them contains equal numbers of A and B.

State:

```text
(countA,countB)
```

compressed to:

```text
countA-countB
```

---

# 17.34 Complete CF-Style Example 3 — Product Sign

Values:

```text
[-2,4,-7,3]
```

No zero.

Number of negatives:

```text
2
```

even.

Therefore product:

```text
positive
```

No multiplication needed.

State compressed to:

```text
zeroExists?
negativeCount % 2
```

---

# 17.35 Complete CF-Style Example 4 — Modulo State

Start:

```text
x
```

Operations add:

```text
3 or 6
```

For divisibility by 3, both operations add:

```text
0 mod 3
```

So:

```text
x % 3
```

never changes.

If initial:

```text
x % 3 != 0
```

you can never reach a multiple of 3.

Exact size is irrelevant for this impossibility proof.

---

# 17.36 Complete CF-Style Example 5 — Relative Difference

Two scores:

```text
A = 100
B = 73
```

Operation:

```text
A += 2
B += 5
```

Difference:

```text
d = A-B = 27
```

After one operation:

```text
d' = d - 3
```

After `k`:

```text
d_k = 27 - 3k
```

Equality means:

```text
d_k = 0
```

Thus:

```text
27-3k = 0
k = 9
```

Tracking both large scores was unnecessary.

---

# 17.37 Complete CF-Style Example 6 — Category Frequencies

Suppose board contains only:

```text
1,2,3
```

and operations choose types, not positions.

State can be:

```text
(c1,c2,c3)
```

rather than the entire board arrangement.

Example:

```text
1 3 1 2 3 1
```

becomes:

```text
(3,1,2)
```

If order never appears in the rules, this is often the natural mathematical state.

---

# 17.38 How to Discover the Compressed State

Use these questions:

```text
1. WHAT DOES THE TARGET CHECK?

2. WHAT DOES ONE OPERATION CHANGE?

3. WHAT DOES ONE OPERATION PRESERVE?

4. DO EXACT MAGNITUDES MATTER?

5. DOES ORDER MATTER?

6. DO POSITIONS MATTER?

7. DO I NEED ONLY:
   parity?
   remainder?
   sign?
   zero/non-zero?
   relative order?
   difference?
   frequency?
   presence?

8. IF TWO FULL STATES MAP TO
   THE SAME COMPRESSED STATE,
   CAN THEIR FUTURES DIFFER?
```

The last question validates the model.

---

# 17.39 Common Compression Patterns

```text
FULL INFORMATION              COMPRESSED INFORMATION
----------------------------------------------------------------
integer x                     x % 2
integer x                     x % m
many integers                 parity counts
product values                zero? + negative parity
(a,b)                         a-b
(countA,countB)               countA-countB
positions A,B                 B-A
small-category array          frequency vector
subset of small universe      bitmask
all prefix sums               prefix remainder
large graph of values         small remainder states
```

---

# 17.40 Common Mistakes

## Mistake 1 — Keeping exact values automatically

Ask whether future logic can actually observe them.

---

## Mistake 2 — Over-compressing

Same remainder does not imply same reachability when direction/bounds matter.

---

## Mistake 3 — Keeping positions when order is irrelevant

Use counts/frequencies if permutations do not change the problem.

---

## Mistake 4 — Keeping two counters when only their difference matters

Move the target equation to one side.

---

## Mistake 5 — Confusing invariant with sufficient state

An invariant can give a necessary condition without completely determining the answer.

---

## Mistake 6 — Compressing before reading operations

The legal operations determine which information remains relevant.

---

# 17.41 Translation Drills

Do not code.

### Drill 1

Question:

```text
Is sum even?
```

Keep:

```text
sum % 2
```

or:

```text
oddCount % 2
```

### Drill 2

Question:

```text
Is product positive/negative/zero?
```

Keep:

```text
zeroExists
negativeCount % 2
```

### Drill 3

Goal:

```text
A = B
```

Try state:

```text
A-B
```

### Drill 4

Goal:

```text
countX = countY
```

Try:

```text
countX-countY
```

### Drill 5

Only values `1,2,3` exist and order is irrelevant.

Keep:

```text
(cnt1,cnt2,cnt3)
```

---

# 17.42 Practice Set

For each problem write:

```text
FULL STATE:
WHAT TARGET OBSERVES:
WHAT OPERATIONS OBSERVE:
CANDIDATE COMPRESSED STATE:
WHY TWO EQUIVALENT STATES BEHAVE THE SAME:
WHAT INFORMATION MUST NOT BE LOST:
```

### Practice A

Determine parity of the sum of `10^5` huge integers.

### Practice B

Determine sign of product without calculating product.

### Practice C

Two counters change every operation; target is equality.

### Practice D

Operations change integer by multiples of `m`; ask whether target remainder is reachable.

### Practice E

Array contains only four possible values and operations ignore positions.

---

# 17.43 Practice Answers

## A

Keep:

```text
sum % 2
```

or parity of odd count.

## B

Keep:

```text
hasZero
negativeCount % 2
```

## C

Try:

```text
difference = A-B
```

Then derive how each operation changes the difference.

## D

Track:

```text
x % m
```

but also inspect direction/bounds if reachability asks for an exact target rather than only a remainder class.

## E

Keep four frequencies:

```text
(cnt0,cnt1,cnt2,cnt3)
```

assuming order truly has no effect.

---

# 17.44 Chapter Mastery Test

You are ready for the next chapter when you see huge values or complicated state and automatically ask:

```text
"What can the future actually distinguish?"
```

You should recognize:

```text
even/odd condition
    -> parity

divisibility
    -> remainder

equal two quantities
    -> difference = 0

equal category counts
    -> balance

order irrelevant
    -> frequencies

small set of properties
    -> bitmask
```

But you must also ask:

```text
"Am I throwing away something
needed for direction, bounds,
or future transitions?"
```

---

# 17.45 Final Mental Engine

```text
                 FULL STATE
                     │
                     ▼
         TARGET + OPERATIONS + RULES
                     │
                     ▼
          WHAT CAN THEY ACTUALLY SEE?
                     │
       ┌─────────────┼─────────────┐
       │             │             │
     parity       remainder     relation
       │             │             │
       ▼             ▼             ▼
      2 states      m states     difference
       │             │             │
       └─────────────┼─────────────┘
                     ▼
             COMPRESSED STATE
                     │
                     ▼
              SAFETY QUESTION
                     │
        same compressed state
        => same future behavior?
              /             \
            YES              NO
             │                │
          use it         restore missing
                         information
```

The core habit:

```text
Do not ask:
"How do I handle values up to 10^18?"

First ask:
"Do I actually need to know the value?"
```

---

# Next Chapter

```text
17. STATE COMPRESSION & EQUIVALENCE MODELING
                    ↓
18. BOUNDS, MONOTONICITY & ANSWER-SPACE MODELING
```

Chapter 18 will focus on turning optimization into a yes/no question:

```text
lower bound
upper bound
candidate answer X
feasibility(X)
false false false true true
binary-searchable answer space
```

The emphasis will be on deriving monotonicity mathematically rather than memorizing "binary search on answer."
