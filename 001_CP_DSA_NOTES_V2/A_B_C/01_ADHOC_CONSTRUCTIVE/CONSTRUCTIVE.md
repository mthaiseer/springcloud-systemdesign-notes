# Codeforces Div2 A/B/C --- Constructive Algorithms Pattern Handbook

**Separated from the original combined handbook.**

**Primary goal:** recognize when Codeforces asks you to build *any*
valid object, then choose a construction whose shape makes the
constraints true by design.

## Table of Contents

1.  Constructive mental model
2.  Constructive 60-second scanner
3.  Simplest valid construction
4.  Mostly same + one correction
5.  Alternating / cyclic / block construction
6.  Greedy constructive + local repair
7.  Construction templates to memorize
8.  Standard impossibility proofs
9.  Constructive learning order
10. Constructive contest checklist

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

# 3. Div2 B Constructive Foundation

# B1. Simplest Valid Construction

## 60-second signal

The statement says:

``` text
print any valid...
construct an array...
find any permutation...
```

First thought:

> What is the dumbest possible structure that satisfies everything?

Try:

``` text
all same
identity permutation
reverse permutation
pair swap
constant difference
```

------------------------------------------------------------------------

## Example --- build sum `n`

Allowed:

``` text
1 ... k
except x
```

Need numbers summing to `n`.

The simplest possible building block is `1`.

If `1` is allowed:

``` text
n = 7

1 + 1 + 1 + 1 + 1 + 1 + 1
```

No need to optimize number of terms.

ASCII:

``` text
target 7
│
├─1
├─1
├─1
├─1
├─1
├─1
└─1
```

This is a key constructive principle:

> If the problem asks for **any** solution, do not optimize something it
> did not ask you to optimize.

------------------------------------------------------------------------

## Code

``` cpp
if (x != 1) {
    cout << "YES\n";
    cout << n << '\n';
    for (int i = 0; i < n; ++i)
        cout << 1 << ' ';
    cout << '\n';
}
```

------------------------------------------------------------------------

## Construction search order

Use this mental order:

``` text
1. all minimum value
2. all maximum value
3. increasing sequence
4. decreasing sequence
5. alternating two values
6. pair swap
7. mostly same + one correction
8. blocks
```

------------------------------------------------------------------------

## Recognition sentence

> "Any-answer constructive problem → try the simplest repetitive
> structure before searching."

------------------------------------------------------------------------

## Practice --- simplest construction

1.  [CF 1845A --- Forbidden
    Integer](https://codeforces.com/problemset/problem/1845/A)
2.  [CF 1741B --- Funny
    Permutation](https://codeforces.com/problemset/problem/1741/B)
3.  [CF 1831A --- Twin
    Permutations](https://codeforces.com/problemset/problem/1831/A)
4.  [CF 1794B --- Not
    Dividing](https://codeforces.com/problemset/problem/1794/B)
5.  [CF 1814A ---
    Coins](https://codeforces.com/problemset/problem/1814/A)

------------------------------------------------------------------------

# B2. Mostly Same + One Correction

This is one of the highest-value constructive patterns.

## 60-second signal

You can satisfy most of the target using one building block, but:

``` text
parity
remainder
forbidden value
final sum
```

does not fit exactly.

Think:

``` text
BASE CONSTRUCTION
      +
SMALL CORRECTION
```

------------------------------------------------------------------------

## Dry run

Need:

``` text
n = 11
```

`1` is forbidden, but `2` and `3` are available.

Using only 2:

``` text
2 + 2 + 2 + 2 + 2 = 10
```

We cannot reach odd 11 with only even values.

Correction:

``` text
3 + 2 + 2 + 2 + 2 = 11
```

ASCII:

``` text
all 2s
2 2 2 2 2
─────────
   10

replace one parity block

3 2 2 2 2
─────────
   11
```

------------------------------------------------------------------------

## Algebra

For even `n`:

``` text
n = 2q
```

For odd `n >= 3`:

``` text
n = 3 + 2q
```

So:

``` text
if n is even:
    use n/2 copies of 2

if n is odd:
    use one 3
    use (n-3)/2 copies of 2
```

------------------------------------------------------------------------

## Generic remainder correction

Need sum `S`, base value `b`.

``` text
S = qb + r
```

Try to handle the small remainder `r`.

This is why `%` and constructive algorithms frequently appear together.

------------------------------------------------------------------------

## Code skeleton

``` cpp
if (n % 2 == 0) {
    for (int i = 0; i < n / 2; ++i)
        cout << 2 << ' ';
}
else {
    cout << 3 << ' ';
    for (int i = 0; i < (n - 3) / 2; ++i)
        cout << 2 << ' ';
}
```

------------------------------------------------------------------------

## Recognition sentence

> "If one repeated value almost works, inspect only the leftover
> parity/remainder and patch it with one small element."

------------------------------------------------------------------------

## Practice --- base + correction

1.  [CF 1845A --- Forbidden
    Integer](https://codeforces.com/problemset/problem/1845/A)
2.  [CF 1814A ---
    Coins](https://codeforces.com/problemset/problem/1814/A)
3.  [CF 1794B --- Not
    Dividing](https://codeforces.com/problemset/problem/1794/B)
4.  [CF 1367B --- Even
    Array](https://codeforces.com/problemset/problem/1367/B)
5.  [CF 1352B --- Same Parity
    Summands](https://codeforces.com/problemset/problem/1352/B)

------------------------------------------------------------------------

# B3. Alternating / Cyclic / Block Construction

## 60-second signal

The constraints mention:

``` text
adjacent elements
repeat every k
different neighbors
colors
balanced placement
permutation with local constraints
```

Try a periodic structure.

------------------------------------------------------------------------

## Alternating

``` text
A B A B A B
```

Index view:

``` text
i:  0 1 2 3 4 5
    A B A B A B
```

Formula:

``` cpp
a[i] = (i % 2 == 0 ? A : B);
```

------------------------------------------------------------------------

## Cyclic

For `k = 3`:

``` text
1 2 3 1 2 3 1 2 3
```

Formula:

``` cpp
a[i] = i % k;
```

ASCII:

``` text
remainder class:

i      0 1 2 3 4 5 6 7
i%3    0 1 2 0 1 2 0 1
       └─────┘
        repeat
```

------------------------------------------------------------------------

## Pair-swap permutation

Identity:

``` text
1 2 3 4 5 6
```

Pair swapped:

``` text
2 1 4 3 6 5
```

Why useful?

``` text
p[i] != i
```

becomes automatically true for every paired position.

ASCII:

``` text
1 ↔ 2
3 ↔ 4
5 ↔ 6
```

------------------------------------------------------------------------

## Blocks

Sometimes constraints should be satisfied inside chunks.

``` text
1 2 3 | 4 5 6 | 7 8 9

reverse each block

3 2 1 | 6 5 4 | 9 8 7
```

------------------------------------------------------------------------

## Code snippets

Alternating:

``` cpp
for (int i = 0; i < n; ++i)
    cout << (i % 2 ? 2 : 1) << ' ';
```

Pair swap:

``` cpp
for (int i = 1; i <= n; i += 2)
    cout << i + 1 << ' ' << i << ' ';
```

Cyclic:

``` cpp
for (int i = 0; i < n; ++i)
    cout << (i % k) + 1 << ' ';
```

------------------------------------------------------------------------

## Recognition sentence

> "Local/adjacent constraints often become globally easy if I choose a
> periodic pattern."

------------------------------------------------------------------------

## Practice --- alternating/cyclic/permutation

1.  [CF 1335B --- Construct the
    String](https://codeforces.com/problemset/problem/1335/B)
2.  [CF 1741B --- Funny
    Permutation](https://codeforces.com/problemset/problem/1741/B)
3.  [CF 1831A --- Twin
    Permutations](https://codeforces.com/problemset/problem/1831/A)
4.  [CF 1822D ---
    Super-Permutation](https://codeforces.com/problemset/problem/1822/D)
5.  [CF 1353C --- K-th Not Divisible by
    n](https://codeforces.com/problemset/problem/1353/C)

------------------------------------------------------------------------

------------------------------------------------------------------------

# 4. Div2 C Constructive Bridge

# C4. Greedy Constructive + Local Repair

## 60-second signal

You need to construct/process left-to-right, and once a prefix is valid
you would like never to revisit it.

Think:

``` text
find first violation
     ↓
make smallest safe correction
     ↓
continue
```

------------------------------------------------------------------------

## Example

Suppose required:

``` text
a[i] should not be divisible by a[i-1]
```

You scan:

``` text
2 4 5 10 ...
```

At each bad value, apply the smallest permitted correction rather than
rebuilding the entire array.

ASCII:

``` text
valid prefix
[ ✓ ✓ ✓ ✓ ] [BAD] ? ? ?
              ^
          repair here

then continue

[ ✓ ✓ ✓ ✓ ✓ ] ? ? ?
```

------------------------------------------------------------------------

## Greedy principle

A local repair is promising when:

1.  the prefix already satisfies all constraints;
2.  changing the current element cannot invalidate the prefix;
3.  a minimal change leaves maximum flexibility for the suffix.

------------------------------------------------------------------------

## Pairwise comparator derivation

When unsure what order is greedy-best, compare two objects `A` and `B`.

``` text
Order 1: A then B
Order 2: B then A
```

Compute objective for both.

If:

``` text
cost(A,B) <= cost(B,A)
```

derive the inequality.

ASCII:

``` text
DON'T KNOW SORT ORDER
        ↓
take only A and B
        ↓
objective(A,B)
objective(B,A)
        ↓
compare
        ↓
cancel common terms
        ↓
comparator
```

This is a major path from ad-hoc reasoning into stronger Div2 C greedy.

------------------------------------------------------------------------

## Code skeleton --- local repair

``` cpp
for (int i = 1; i < n; ++i) {
    if (bad(a[i - 1], a[i])) {
        a[i] = smallestSafeValue(a[i - 1], a[i]);
    }
}
```

------------------------------------------------------------------------

## Recognition sentence

> "If a prefix can be finalized forever, repair the first violation with
> the smallest safe change."

------------------------------------------------------------------------

## Practice --- greedy constructive/local repair

1.  [CF 1794B --- Not
    Dividing](https://codeforces.com/problemset/problem/1794/B)
2.  [CF 1833B --- Restore the
    Weather](https://codeforces.com/problemset/problem/1833/B)
3.  [CF 1624C --- Division by Two and
    Permutation](https://codeforces.com/problemset/problem/1624/C)
4.  [CF 1772D --- Absolute
    Sorting](https://codeforces.com/problemset/problem/1772/D)
5.  [CF 1798A ---
    Showstopper](https://codeforces.com/problemset/problem/1798/A)

------------------------------------------------------------------------

------------------------------------------------------------------------

# 6. Construction Templates to Memorize

Do not memorize solutions.

Memorize **shapes**.

------------------------------------------------------------------------

## Template 1 --- all same

``` text
1 1 1 1 1 1
```

Use when one safe value alone satisfies the target.

Trigger:

``` text
unlimited supply
any valid answer
sum construction
```

------------------------------------------------------------------------

## Template 2 --- mostly same + one correction

``` text
2 2 2 2 3
```

Trigger:

``` text
parity mismatch
remainder mismatch
```

------------------------------------------------------------------------

## Template 3 --- alternating

``` text
1 2 1 2 1 2
```

Trigger:

``` text
adjacent different
two classes
balanced layout
```

------------------------------------------------------------------------

## Template 4 --- cycle

``` text
1 2 3 1 2 3
```

Trigger:

``` text
period k
colors
modulo classes
```

------------------------------------------------------------------------

## Template 5 --- reverse

``` text
n n-1 n-2 ... 1
```

Trigger:

``` text
identity fails
need large-small inversion
```

------------------------------------------------------------------------

## Template 6 --- pair swap

``` text
2 1 4 3 6 5
```

Trigger:

``` text
avoid fixed points
local permutation property
```

------------------------------------------------------------------------

## Template 7 --- rotate

``` text
2 3 4 5 1
```

Trigger:

``` text
avoid p[i] = i
cyclic relation
```

------------------------------------------------------------------------

## Template 8 --- zig-zag extremes

Sorted numbers:

``` text
1 2 3 4 5 6
```

Construct:

``` text
1 6 2 5 3 4
```

Trigger:

``` text
large adjacent difference
balance extremes
```

------------------------------------------------------------------------

## Template 9 --- blocks

``` text
1 2 3 | 4 5 6 | 7 8 9
```

Transform:

``` text
3 2 1 | 6 5 4 | 9 8 7
```

Trigger:

``` text
constraint local to windows/chunks
```

------------------------------------------------------------------------

------------------------------------------------------------------------

# 7. Standard Impossibility Proofs

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

# 9. Constructive Learning Order

``` text
LEVEL 1
all same
identity / reverse
base + correction
        ↓
LEVEL 2
alternating
cyclic
pair swap
rotation
blocks
        ↓
LEVEL 3
parity/modulo construction
permutation construction
zig-zag/extreme arrangement
        ↓
LEVEL 4
greedy construction
first violation + local repair
reverse construction
invariant-guided construction
        ↓
LEVEL 5
combine constructive with:
math
greedy
bits/XOR
number theory
prefix/suffix
sorting
```

# 10. Constructive Contest Checklist

``` text
[ ] Does the problem ask for ANY valid answer?
[ ] Am I accidentally optimizing something it never asked me to optimize?
[ ] Can every element be the same?
[ ] Can I use a repeating base value?
[ ] Is only parity/remainder preventing the base construction?
[ ] Can one small correction fix it?
[ ] Would alternating/cyclic structure make local rules automatic?
[ ] Would identity/reverse/pair-swap/rotation solve a permutation condition?
[ ] Can I divide the answer into independent blocks?
[ ] Is there a first violation I can repair locally?
[ ] What are the impossible cases?
[ ] Can I prove my construction in one sentence?
```

## Final recognition rule

``` text
Don't search all possible answers.

DESIGN an answer whose STRUCTURE
makes the required property automatic.
```
