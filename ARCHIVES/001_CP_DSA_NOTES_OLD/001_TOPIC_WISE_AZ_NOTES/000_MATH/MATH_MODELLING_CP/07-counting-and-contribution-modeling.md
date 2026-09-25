# CP Mathematical Modeling Mini-Course

## 7. Counting & Contribution Modeling

> **Goal:** Learn to turn counting problems into mathematics without blindly generating every pair, subarray, or interaction.
>
> This chapter is about **modeling counts**, not memorizing combinatorics formulas. The central questions are:
>
> **"What am I counting?"**
>
> **"How many times does each object contribute?"**

---

# Chapter Tree

```text
7. COUNTING & CONTRIBUTION MODELING
│
├── 7.1 Define exactly what is counted
├── 7.2 Count choices instead of objects
├── 7.3 Ordered vs unordered counting
├── 7.4 Fix one variable, count the rest
├── 7.5 Complement counting
├── 7.6 Frequency modeling
├── 7.7 Count equal-value pairs
├── 7.8 Contribution technique
├── 7.9 Position contribution
├── 7.10 Subarray contribution
├── 7.11 Pair contribution
├── 7.12 Double-counting intuition
├── 7.13 Prefix/suffix counts
├── 7.14 Avoiding double counting
└── 7.15 Complete CF-style examples
```

Central engine:

```text
COUNTING PROBLEM
      ↓
What exactly is one valid object?
      ↓
pair / triple / index / subarray / event
      ↓
Can I FIX part of it?
      ↓
How many completions remain?
      ↓
OR
How much does each element contribute?
      ↓
SUM THE COUNTS
```

---

# 7.1 Define Exactly What Is Counted

Before writing loops, complete this sentence:

```text
One thing I am counting is __________.
```

Examples:

```text
one ordered pair (i,j)
one unordered pair {i,j}
one triple (x,y,z)
one subarray [L,R]
one index satisfying a condition
one occurrence of value x
```

This prevents many mistakes.

Example:

> Count pairs `i < j` such that `a[i] = a[j]`.

One counted object is:

```text
a pair of indices (i,j)

with:
i < j
a[i] = a[j]
```

Not:

```text
a value
```

Not:

```text
an element
```

You are counting **index pairs**.

---

# 7.2 Count Choices Instead of Simulating

Suppose:

```text
x can be chosen in 4 ways
```

and for every `x`:

```text
y can be chosen in 3 ways
```

Then total ordered choices:

```text
4 * 3 = 12
```

Visual:

```text
x1 -> y1 y2 y3
x2 -> y1 y2 y3
x3 -> y1 y2 y3
x4 -> y1 y2 y3

4 groups
3 choices each

total = 4*3
```

The modeling principle:

```text
number of first choices
*
number of second choices per first choice
```

You will learn deeper combinatorics separately; here we care about recognizing the structure.

---

# 7.3 Ordered vs Unordered Counting

This distinction is essential.

Suppose objects are:

```text
A B C
```

Ordered pairs of different objects:

```text
(A,B)
(A,C)
(B,A)
(B,C)
(C,A)
(C,B)
```

Count:

```text
6
```

Unordered pairs:

```text
{A,B}
{A,C}
{B,C}
```

Count:

```text
3
```

Why?

Because:

```text
(A,B)
```

and:

```text
(B,A)
```

are different when order matters, but the same when it does not.

Mental question:

```text
If I swap the two chosen objects,
is it a NEW answer?
```

If yes:

```text
ordered
```

If no:

```text
unordered
```

---

# 7.4 Fix One Variable, Count the Rest

This is one of the strongest counting techniques.

Instead of counting everything simultaneously:

```text
fix one part
      ↓
count how many valid completions it has
      ↓
sum over all fixed choices
```

Example:

> Count ordered pairs `(x,y)` satisfying:

```text
0 <= x,y <= K
x + y <= S
```

Fix `x`.

Then:

```text
y <= S-x
```

Also:

```text
0 <= y <= K
```

Therefore valid `y` values are:

```text
0 <= y <= min(K, S-x)
```

If the upper bound is non-negative, number of choices is:

```text
min(K, S-x) + 1
```

Then:

```text
answer =
sum over x of
(number of valid y for this x)
```

Visual:

```text
choose x
   │
   ▼
derive valid y range
   │
   ▼
count y values
   │
   ▼
add to answer
```

This often replaces a nested loop.

---

# 7.5 Exact Sum: Fix and Calculate

Suppose:

```text
x + y = S
```

Fix `x`.

Then:

```text
y = S-x
```

There is not a range of possible `y`.

There is exactly:

```text
one candidate y
```

So contribution of each `x` is:

```text
1 if S-x is valid
0 otherwise
```

Then:

```text
answer = sum of these 0/1 contributions
```

This connects Chapter 4's variable elimination with counting.

---

# 7.6 Complement Counting

Sometimes valid cases are difficult to count directly, while invalid cases are easy.

Use:

```text
valid = total - invalid
```

Example:

> Count pairs of different indices.

Total ordered index pairs:

```text
n*n
```

Invalid pairs where:

```text
i = j
```

Count:

```text
n
```

Therefore ordered pairs with:

```text
i != j
```

are:

```text
n*n - n
```

Equivalent:

```text
n*(n-1)
```

Mental trigger:

```text
DIRECT COUNT HARD?
      ↓
Can I count ALL?
      ↓
Can I count BAD?
      ↓
GOOD = ALL - BAD
```

---

# 7.7 Frequency Modeling

Suppose an array is:

```text
1 2 1 3 2 1
```

Instead of tracking positions individually, count occurrences:

```text
freq[1] = 3
freq[2] = 2
freq[3] = 1
```

This transforms:

```text
ARRAY OF MANY ELEMENTS
        ↓
VALUE -> FREQUENCY
```

Useful when the condition depends mainly on values rather than positions.

Example:

> Count equal-value pairs.

For value `1`:

```text
3 occurrences
```

For value `2`:

```text
2 occurrences
```

For value `3`:

```text
1 occurrence
```

Now solve the problem group by group.

---

# 7.8 Equal-Value Pair Modeling

Suppose a value appears:

```text
f
```

times.

How many unordered pairs can be formed from its occurrences?

Think sequentially:

```text
first occurrence can pair with f-1 later occurrences
second with f-2
...
```

Total:

```text
(f-1) + (f-2) + ... + 1
```

This equals:

```text
f*(f-1)/2
```

So if each distinct value `v` occurs `freq[v]` times:

```text
answer += freq[v] * (freq[v]-1) / 2
```

Visual:

```text
same value appears at:

p1 p2 p3 p4

pairs:

p1-p2
p1-p3
p1-p4
p2-p3
p2-p4
p3-p4
```

Count:

```text
4*3/2 = 6
```

The important modeling step is:

```text
group equal values
      ↓
count pairs inside each group
```

---

# 7.9 Online Contribution Counting

There is another way to derive the same result.

Process elements left to right.

Suppose current value is `x`.

If `x` has already appeared:

```text
f
```

times, then the current occurrence forms:

```text
f new pairs
```

with earlier occurrences.

Example:

```text
array:
1 2 1 1
```

Process:

```text
first 1:
previous 1s = 0
new pairs = 0

2:
previous 2s = 0
new pairs = 0

second 1:
previous 1s = 1
new pairs = 1

third 1:
previous 1s = 2
new pairs = 2
```

Total:

```text
0 + 0 + 1 + 2 = 3
```

Same as:

```text
3*2/2 = 3
```

This is **contribution thinking**:

```text
When this element arrives,
how many NEW valid objects does it create?
```

---

# 7.10 Contribution Technique

Instead of:

```text
generate every object
and inspect it
```

ask:

```text
How much does each element contribute
to the final answer?
```

General structure:

```text
answer =
contribution(element 1)
+
contribution(element 2)
+
...
```

Or:

```text
answer = sum(contribution(i))
```

Examples of contribution:

```text
number of valid partners
number of subarrays containing i
number of later equal elements
number of left choices * right choices
value * number of times it appears
```

This is one of the most important modeling ideas for 1200-1900 CF problems.

---

# 7.11 Position Contribution to Subarrays

Consider an array of length `n`.

Fix index `i` using 0-based indexing.

How many subarrays contain `i`?

A subarray containing `i` must choose:

```text
left endpoint L <= i
right endpoint R >= i
```

Choices for `L`:

```text
i + 1
```

because:

```text
L = 0,1,...,i
```

Choices for `R`:

```text
n - i
```

because:

```text
R = i,i+1,...,n-1
```

Each left choice can combine with each right choice.

Therefore:

```text
number of subarrays containing i
=
(i+1) * (n-i)
```

ASCII:

```text
0 ... L ... i ... R ... n-1
      <----[i]---->

left endpoint choices  = i+1
right endpoint choices = n-i

contribution count =
(i+1)*(n-i)
```

This is a classic contribution model.

---

# 7.12 Sum of All Subarray Sums

Suppose we want:

```text
sum of sums of every subarray
```

Brute force might enumerate all subarrays and add their elements.

Contribution view:

> How many subarrays contain `a[i]`?

From above:

```text
(i+1)*(n-i)
```

Every such subarray contributes `a[i]` once.

Therefore contribution of `a[i]` is:

```text
a[i] * (i+1) * (n-i)
```

Total:

```text
answer =
sum over i:
a[i] * (i+1) * (n-i)
```

Visual:

```text
a[i]
  │
  │ appears in
  ▼
(i+1)*(n-i) subarrays
  │
  ▼
total contribution
=
a[i]*(i+1)*(n-i)
```

The transformation:

```text
iterate subarrays
      ↓
reverse perspective
      ↓
iterate elements
      ↓
count how many subarrays use each element
```

---

# 7.13 Reverse the Perspective

This is the essence of contribution technique.

Original viewpoint:

```text
For every subarray,
which elements does it contain?
```

Reverse viewpoint:

```text
For every element,
which subarrays contain it?
```

Original:

```text
OBJECT -> MEMBERS
```

Contribution:

```text
MEMBER -> OBJECTS IT CONTRIBUTES TO
```

Whenever enumeration looks expensive, ask:

> Can I reverse who is iterating over whom?

---

# 7.14 Left Choices * Right Choices

Many counting problems reduce to:

```text
choose something on the left
*
choose something on the right
```

Example:

Fix a middle index `i`.

Suppose there are:

```text
L valid choices before i
R valid choices after i
```

Then triples with `i` as the middle may be:

```text
L * R
```

because every valid left choice combines with every valid right choice.

Visual:

```text
left choices        fixed i       right choices

L1 ───────────────┐       ┌──── R1
L2 ───────────────┤   i   ├──── R2
L3 ───────────────┘       └──── R3

each left can pair with each right

contribution of i = L * R
```

This pattern appears constantly in subsequence, triple, and index-counting problems.

---

# 7.15 Prefix and Suffix Counts

Suppose for each index `i`, you need:

```text
number of special elements before i
```

and:

```text
number of special elements after i
```

Do not rescan the array each time.

Build:

```text
prefix count
suffix count
```

Example:

```text
a = [1, 0, 1, 1, 0]
```

Suppose "special" means value `1`.

Prefix number of ones before/through positions can be maintained incrementally.

Then for a fixed middle position:

```text
left choices = count of required type on left
right choices = count of required type on right
```

Contribution:

```text
left * right
```

Mathematics:

```text
count valid triples
=
sum over middle positions
(left choices * right choices)
```

The data structure is simple.

The important part is recognizing the contribution equation.

---

# 7.16 Count Pairs by Fixing the Right Endpoint

Suppose:

> Count pairs `(i,j)` with:

```text
i < j
```

and some condition.

Instead of trying every pair, process `j` as the fixed right endpoint.

Ask:

```text
For this j,
how many earlier i are valid?
```

Then:

```text
answer += valid_left_count(j)
```

Every pair has exactly one right endpoint, so every pair is counted exactly once.

Visual:

```text
i1 i2 i3 ... j
 \  |  /
 valid earlier partners

contribution of j
=
number of valid i < j
```

This perspective leads naturally to:

```text
frequency maps
prefix counts
Fenwick trees
segment trees
```

in harder problems.

Again, math model first.

---

# 7.17 Avoid Double Counting

Whenever using contributions, ask:

```text
Does every valid object get counted exactly once?
```

Example:

Count unordered pairs.

If you process each pair from both endpoints:

```text
A contributes pair AB
B also contributes pair BA
```

you counted it twice.

A common fix:

```text
only count earlier partners
```

or:

```text
enforce i < j
```

Then every unordered pair has one unique right endpoint.

Rule:

```text
Give every counted object
a UNIQUE OWNER.
```

Examples:

```text
pair -> right endpoint
triple -> middle index
subarray -> chosen special position
event -> last element
```

Unique ownership is a powerful way to prove no double counting.

---

# 7.18 Double-Counting Intuition

Sometimes the same set can be counted in two ways.

Example:

Suppose every person shakes hands with every other person once.

View 1:

```text
count handshakes directly
```

View 2:

```text
each of n people participates in n-1 handshakes
```

Total participations:

```text
n*(n-1)
```

But every handshake has two participants.

Therefore:

```text
handshakes = n*(n-1)/2
```

This illustrates:

```text
count incidences/contributions
then divide by how many times
each real object was counted
```

You will encounter this logic often even without advanced combinatorics.

---

# 7.19 Frequency + Complement

Suppose:

> Count pairs of values whose sum is `S`.

For current value:

```text
x
```

needed partner:

```text
S-x
```

If processing left to right and:

```text
freq[v]
```

stores earlier occurrences, then current `x` creates:

```text
freq[S-x]
```

new pairs.

Algorithmic model:

```text
answer = 0

for x in array:
    needed = S-x

    answer += freq[needed]

    freq[x]++
```

Why does this avoid double counting?

Because current `x` is always treated as the right endpoint.

Only earlier partners are counted.

Every pair:

```text
i < j
```

is counted exactly when processing `j`.

This combines:

```text
complement thinking
+
frequency modeling
+
contribution
+
unique ownership
```

---

# 7.20 Counting Triples by Fixing One Position

Suppose valid triples satisfy:

```text
i < j < k
```

For each possible middle `j`:

```text
L[j] = number of valid i < j
R[j] = number of valid k > j
```

Then:

```text
triples with middle j
=
L[j] * R[j]
```

Total:

```text
answer =
sum over j:
L[j] * R[j]
```

Why?

Each triple has exactly one middle index.

So:

```text
j
```

is the unique owner of that triple.

This is a general 1400-1900-style modeling pattern.

---

# 7.21 Count by Cases

Sometimes valid objects fall into disjoint categories.

If cases do not overlap:

```text
answer =
count(case 1)
+
count(case 2)
+
count(case 3)
```

Example:

```text
valid pair is either:
both even
OR
both odd
```

These cases are disjoint.

So:

```text
answer =
pairs among evens
+
pairs among odds
```

If:

```text
E = number of even elements
O = number of odd elements
```

then:

```text
answer =
E*(E-1)/2
+
O*(O-1)/2
```

The key modeling step:

```text
classify elements by the property
that determines validity
```

---

# 7.22 State Compression for Counting

Suppose the condition depends only on parity.

You do not need exact values.

Instead of storing all values:

```text
2,4,8,10,...
1,3,5,7,...
```

compress into:

```text
E = count of even
O = count of odd
```

Likewise, if the condition depends only on remainder modulo `m`:

```text
freq[0]
freq[1]
...
freq[m-1]
```

This is mathematical state compression:

```text
exact values
    ↓
only relevant property
    ↓
small frequency state
```

This will connect strongly with the later chapter on modulo/remainder modeling.

---

# 7.23 Total Number of Subarrays

An array of length `n`.

A subarray is determined by:

```text
left endpoint L
right endpoint R
```

with:

```text
L <= R
```

Count by left endpoint:

```text
L = 0 -> n choices for R
L = 1 -> n-1
L = 2 -> n-2
...
```

Total:

```text
n + (n-1) + ... + 1
```

which is:

```text
n*(n+1)/2
```

Alternative viewpoint:

```text
choose two boundaries
```

The important lesson is that a subarray is defined by its endpoints.

When counting structures, identify:

```text
What choices uniquely determine one structure?
```

---

# 7.24 Count Bad, Subtract From Total

Suppose:

> Count subarrays that contain at least one `1`.

Direct counting may be awkward.

Instead:

```text
valid
=
all subarrays
-
subarrays containing no 1
```

All subarrays:

```text
n*(n+1)/2
```

A subarray containing no `1` lies entirely inside a consecutive block of zeros.

If zero-block lengths are:

```text
L1, L2, L3, ...
```

then bad subarrays:

```text
L1*(L1+1)/2
+
L2*(L2+1)/2
+
...
```

Therefore:

```text
answer =
n*(n+1)/2
-
sum over zero blocks:
L*(L+1)/2
```

This combines:

```text
complement counting
+
group/block modeling
```

---

# 7.25 Complete CF-Style Example 1 — Equal Pairs

Problem:

> Count pairs `i < j` with:

```text
a[i] = a[j]
```

Model by frequency.

For each value `v`:

```text
f = freq[v]
```

Pairs contributed:

```text
f*(f-1)/2
```

Therefore:

```text
answer =
sum over values v:
freq[v]*(freq[v]-1)/2
```

Alternative online model:

```text
for current x:
    answer += previous_count[x]
    previous_count[x]++
```

Both come from the same counting structure.

---

# 7.26 Complete CF-Style Example 2 — Pair Sum

Problem:

> Count index pairs `i < j` such that:

```text
a[i] + a[j] = S
```

Fix right endpoint `j`.

Current:

```text
x = a[j]
```

Required left value:

```text
S-x
```

Number of valid earlier indices:

```text
freq[S-x]
```

Contribution of `j`:

```text
freq[S-x]
```

Then:

```text
freq[x]++
```

Every valid pair is counted exactly once.

---

# 7.27 Complete CF-Style Example 3 — Sum of All Subarray Sums

For each index `i`:

```text
left endpoint choices  = i+1
right endpoint choices = n-i
```

Number of subarrays containing `i`:

```text
(i+1)*(n-i)
```

Contribution:

```text
a[i]*(i+1)*(n-i)
```

Total:

```text
answer =
sum a[i]*(i+1)*(n-i)
```

Complexity:

```text
O(n)
```

instead of explicitly processing all subarrays and all their elements.

---

# 7.28 Complete CF-Style Example 4 — Triples

Suppose we need triples:

```text
i < j < k
```

where:

```text
a[i] < a[j]
a[k] < a[j]
```

For each middle `j`, define:

```text
L[j] =
number of i < j with a[i] < a[j]

R[j] =
number of k > j with a[k] < a[j]
```

Then:

```text
contribution of j = L[j] * R[j]
```

Total:

```text
sum L[j]*R[j]
```

This does not yet specify the fastest way to compute `L` and `R`.

That is a separate algorithmic question.

The modeling breakthrough is:

```text
every triple has one middle
      ↓
fix middle
      ↓
left choices * right choices
```

---

# 7.29 Counting Checklist

When a problem says **count**, ask:

```text
1. WHAT EXACTLY IS ONE COUNTED OBJECT?

2. ORDERED OR UNORDERED?

3. CAN I FIX ONE PART?
   If yes:
   how many completions?

4. CAN I GROUP BY VALUE/PROPERTY?
   frequency?

5. CAN I COUNT COMPLEMENT?
   valid = total - invalid

6. CAN I REVERSE THE PERSPECTIVE?
   object -> members
   becomes
   member -> objects

7. WHAT DOES EACH ELEMENT CONTRIBUTE?

8. DOES EACH VALID OBJECT HAVE A UNIQUE OWNER?

9. AM I DOUBLE COUNTING?

10. CAN LEFT CHOICES * RIGHT CHOICES HELP?
```

---

# 7.30 Common Mistakes

## Mistake 1 — Not defining the counted object

"Count pairs" is incomplete mentally.

Write:

```text
pair of indices?
pair of values?
ordered?
unordered?
i < j?
```

---

## Mistake 2 — Double counting pairs

If unordered:

```text
(i,j)
```

and:

```text
(j,i)
```

must not both be counted.

Use:

```text
i < j
```

or process only previous elements.

---

## Mistake 3 — Using exact values when only a property matters

If validity depends only on parity:

```text
count evens and odds
```

Do not keep unnecessary information.

---

## Mistake 4 — Enumerating all structures

Before generating all:

```text
pairs
triples
subarrays
```

ask:

```text
Can each element tell me
how many structures contain/use it?
```

---

## Mistake 5 — Multiplying choices that are not independent

You may write:

```text
L * R
```

only when every valid left choice can combine with every valid right choice under the fixed state.

If additional cross-dependencies exist, more reasoning is needed.

---

# 7.31 Translation Drills

Do not code.

---

## Drill 1

A value occurs 5 times.

Number of equal unordered index pairs:

```text
5*4/2 = 10
```

---

## Drill 2

Array length:

```text
n = 6
```

0-based index:

```text
i = 2
```

Subarrays containing `i`:

```text
(i+1)*(n-i)

= 3*4

= 12
```

---

## Drill 3

Current value:

```text
x = 7
```

Target pair sum:

```text
S = 10
```

Required partner:

```text
3
```

If three earlier `3`s exist:

```text
current 7 contributes 3 pairs
```

---

## Drill 4

For a fixed middle index:

```text
4 valid left choices
5 valid right choices
```

If all combinations are valid:

```text
contribution = 4*5 = 20
```

---

# 7.32 Practice Set

For each problem write:

```text
COUNTED OBJECT:
UNIQUE OWNER:
FIXED PART:
CONTRIBUTION:
DOUBLE COUNTING RISK:
FINAL MODEL:
```

---

## Practice A

> Count pairs `i < j` with `a[i] = a[j]`.

---

## Practice B

> Count pairs `i < j` with `a[i] + a[j] = S`.

---

## Practice C

> Find the sum of all subarray sums.

---

## Practice D

> Count triples `i < j < k` where `i` must satisfy one condition relative to `j`, and `k` another independent condition relative to `j`.

---

## Practice E

> Count subarrays containing at least one `1` in a binary array.

---

# 7.33 Practice Answers

## A

```text
group by value

for frequency f:
contribution = f*(f-1)/2

answer = sum contributions
```

---

## B

Fix right endpoint.

For current:

```text
x = a[j]
```

need:

```text
S-x
```

Contribution:

```text
number of earlier occurrences of S-x
```

---

## C

For index `i`:

```text
subarrays containing i
=
(i+1)*(n-i)
```

Contribution:

```text
a[i]*(i+1)*(n-i)
```

Sum over all indices.

---

## D

Fix middle `j`.

Let:

```text
L[j] = valid left choices
R[j] = valid right choices
```

If left/right choices are independent once `j` is fixed:

```text
contribution = L[j]*R[j]
```

Sum over `j`.

---

## E

Use complement:

```text
answer =
all subarrays
-
all-zero subarrays
```

All:

```text
n*(n+1)/2
```

For each consecutive zero block of length `L`:

```text
bad += L*(L+1)/2
```

---

# 7.34 Chapter Mastery Test

You are ready for the next chapter when a counting problem makes you ask:

```text
What exactly is one object I count?
```

Then:

```text
Can I give every object
a unique owner?
```

You should recognize:

```text
fix right endpoint
      ↓
count valid left endpoints
```

and:

```text
fix middle
      ↓
left choices * right choices
```

and:

```text
for each element
      ↓
how many structures contain it?
```

You should also naturally consider:

```text
frequency
complement
contribution
state compression
```

instead of immediately writing nested loops.

---

# 7.35 Final Mental Engine

```text
                 COUNT SOMETHING
                       │
                       ▼
           DEFINE ONE COUNTED OBJECT
                       │
             ┌─────────┴─────────┐
             │                   │
       FIX PART OF IT       GROUP BY PROPERTY
             │                   │
             ▼                   ▼
     COUNT COMPLETIONS        FREQUENCY
             │                   │
             └─────────┬─────────┘
                       ▼
             CAN I REVERSE VIEW?
                       │
                       ▼
          CONTRIBUTION PER ELEMENT
                       │
             ┌─────────┴─────────┐
             │                   │
        left * right        total - bad
             │                   │
             └─────────┬─────────┘
                       ▼
            COUNT EACH OBJECT ONCE
                       │
                       ▼
                  FINAL SUM
```

The core habit:

```text
Do not ask only:

"How do I generate all valid objects?"

Also ask:

"How many valid objects
does this element/state create?"
```

---

# Next Chapter

```text
7. COUNTING & CONTRIBUTION MODELING
                 ↓
8. REMAINDER / MODULO MODELING
```

Chapter 8 will focus on using remainders as **states**, not on repeating the number-theory material from TLE/AZ:

```text
same remainder classes
divisibility as remainder 0
(a-b) divisible by m
pair remainder complements
prefix remainder modeling
cyclic states
parity as modulo 2
compress huge values into m states
```
