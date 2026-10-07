# Greedy Problem Solving 2 — Level 3
## Detailed Self-Study Notes — Preliminaries + Proofs + Dry Runs + C++17

> **Goal:** understand how the observation is discovered and why the greedy choice is correct, instead of memorizing implementation.
>
> **Problem flow used throughout this note:**
>
> ```text
> prerequisites
> → what the problem asks
> → story → variables
> → tiny example first
> → brute-force / obvious approach
> → key observation
> → greedy claim
> → proof with actual numbers
> → general mathematical proof
> → full dry run
> → visual model
> → algorithm
> → C++17
> → complexity
> → edge cases
> → recognition model
> → don't-memorize model
> ```
>
> **Proof style:** understand the numbers first; then generalize to symbols.
>
> **Math rendering:** display equations use fenced `math` blocks only.

---

# Clickable Table of Contents

- [0. How to Use This Note](#0-how-to-use-this-note)
- [1. Shared Preliminaries](#1-shared-preliminaries)
  - [1.1 Constraint vs Objective](#11-constraint-vs-objective)
  - [1.2 Greedy / Forced Choice / Dominance](#12-greedy--forced-choice--dominance)
  - [1.3 Frequency Counting](#13-frequency-counting)
  - [1.4 Powers of Two](#14-powers-of-two)
  - [1.5 Carry / Pairing Model](#15-carry--pairing-model)
  - [1.6 Floor Division](#16-floor-division)
  - [1.7 Pigeonhole / Average Upper Bound](#17-pigeonhole--average-upper-bound)
  - [1.8 Deficit and Surplus](#18-deficit-and-surplus)
  - [1.9 Periodic Strings](#19-periodic-strings)
  - [1.10 Palindrome Constraint](#110-palindrome-constraint)
  - [1.11 Equivalence Classes / Must-Be-Equal Groups](#111-equivalence-classes--must-be-equal-groups)
  - [1.12 Majority Minimizes Replacements](#112-majority-minimizes-replacements)
  - [1.13 Universal Proof Checklist](#113-universal-proof-checklist)
- [2. Problem 1 — Duff and Weight Lifting](#2-problem-1--duff-and-weight-lifting)
- [3. Problem 2 — Polycarp at the Radio](#3-problem-2--polycarp-at-the-radio)
- [4. Problem 3 — K-Complete Word](#4-problem-3--k-complete-word)
- [5. Pattern Comparison](#5-pattern-comparison)
- [6. Final Recognition Checklist](#6-final-recognition-checklist)
- [7. Compact Revision Card](#7-compact-revision-card)

---

# 0. How to Use This Note

For every problem:

```text
1. Read the prerequisites.
2. Understand one tiny concrete example.
3. Try to predict the observation yourself.
4. Read the proof.
5. Reproduce the proof without looking.
6. Implement from the algorithm steps.
7. Re-solve later using only the Recognition Model.
```

The three lecture problems train three different patterns:

```text
Duff and Weight Lifting
→ frequency carry / pair equal powers

Polycarp at the Radio
→ maximize minimum by average bound
→ fill deficits using replaceable positions

K-Complete Word
→ equality groups
→ choose majority character in each group
```

---

# 1. Shared Preliminaries

## 1.1 Constraint vs Objective

Every optimization problem has:

```text
CONSTRAINT
→ what must remain valid

OBJECTIVE
→ what we maximize or minimize
```

Example — Polycarp at the Radio:

```text
Constraint:
we may replace playlist values

Objective 1:
maximize minimum frequency among 1..m

Objective 2:
among those optimal answers,
minimize number of changes
```

Example — K-Complete Word:

```text
Constraint:
final string must be palindrome
and have period k

Objective:
minimum character replacements
```

---

## 1.2 Greedy / Forced Choice / Dominance

Three useful ideas:

### Greedy choice

```text
Choose what looks locally best,
then prove it cannot hurt the optimum.
```

### Forced choice

```text
The structure leaves no useful alternative.
```

### Dominance

```text
One choice/state is always at least as good
as another, so the worse one can be ignored.
```

The problems in this lecture mostly use:

```text
forced pairing
+
counting bounds
+
local majority choice
```

---

## 1.3 Frequency Counting

When values repeat, store:

```text
freq[x]
=
number of occurrences of x
```

Example:

```text
values:
1 1 2 3 3
```

Frequency:

```text
1 → 2
2 → 1
3 → 2
```

Frequency arrays/maps are useful when:

```text
only the count matters,
not the original order
```

---

## 1.4 Powers of Two

Powers of two:

```text
2^0 = 1
2^1 = 2
2^2 = 4
2^3 = 8
2^4 = 16
...
```

Important identity:

```math
2^i+2^i=2^{i+1}
```

Actual examples:

```text
2 + 2 = 4

8 + 8 = 16

32 + 32 = 64
```

This identity is the core of Duff and Weight Lifting.

---

## 1.5 Carry / Pairing Model

Two equal powers can be merged:

```text
2^i + 2^i
→ 2^(i+1)
```

This behaves exactly like a binary carry.

Example:

```text
two 2^1
→ one 2^2
```

Counts:

```text
freq[1] = 5
```

Pair as much as possible:

```text
5 / 2 = 2 pairs
5 % 2 = 1 leftover
```

So:

```text
2 virtual items carry to exponent 2
1 item remains at exponent 1
```

Formula:

```math
freq[i+1]
=
freq[i+1]
+
\left\lfloor\frac{freq[i]}{2}\right\rfloor
```

and:

```math
freq[i]
\leftarrow
freq[i]\bmod2
```

---

## 1.6 Floor Division

```math
\left\lfloor\frac{x}{2}\right\rfloor
```

means:

```text
number of complete pairs inside x items
```

Example:

```text
x = 5

floor(5/2)
= 2 complete pairs

5 % 2
= 1 leftover
```

---

## 1.7 Pigeonhole / Average Upper Bound

Suppose:

```text
n total items
m groups
```

If every group must contain at least `x` items:

```math
m\cdot x\le n
```

Therefore:

```math
x\le\left\lfloor\frac{n}{m}\right\rfloor
```

Example:

```text
n = 8
m = 3
```

Could every group have at least 3?

```text
3 groups × 3
= 9 items required
```

But:

```text
only 8 exist
```

Impossible.

Therefore maximum possible minimum frequency is at most:

```text
floor(8/3)
= 2
```

This is the key upper bound in Polycarp at the Radio.

---

## 1.8 Deficit and Surplus

Suppose every desired group needs at least:

```text
target = x
```

For band `i` with frequency `f[i]`:

### Deficit

If:

```text
f[i] < x
```

missing amount:

```math
deficit_i=x-f[i]
```

Example:

```text
target = 2
freq[3] = 0

deficit
= 2-0
= 2
```

### Surplus

If:

```text
f[i] > x
```

extra copies beyond what we need to preserve:

```math
surplus_i=f[i]-x
```

Example:

```text
target = 2
freq[1] = 5

surplus
= 5-2
= 3
```

Those surplus positions may be changed.

Also:

```text
any value > m
```

is outside the desired band range and may be changed.

---

## 1.9 Periodic Strings

A string has period `k` when:

```text
character i
=
character i+k
```

where both positions exist.

So positions with the same remainder modulo `k` must match.

Example:

```text
n = 6
k = 3

positions:
0 1 2 | 3 4 5
```

Period 3 requires:

```text
s[0] = s[3]
s[1] = s[4]
s[2] = s[5]
```

Meaning every block of length `k` is identical.

---

## 1.10 Palindrome Constraint

Palindrome means:

```math
s[i]=s[n-1-i]
```

Example:

```text
abccba
```

Pairs:

```text
0 ↔ 5
1 ↔ 4
2 ↔ 3
```

---

## 1.11 Equivalence Classes / Must-Be-Equal Groups

Sometimes several positions are connected by constraints:

```text
period constraint
+
palindrome constraint
```

If:

```text
A must equal B
B must equal C
```

then:

```text
A, B, C
```

must all become the same character.

Treat them as one group.

For K-Complete Word, a group is formed from positions corresponding to:

```text
offset i
and
offset k-1-i
```

across **all** length-`k` blocks.

---

## 1.12 Majority Minimizes Replacements

Suppose one must-be-equal group contains:

```text
[a, a, b, a, c]
```

Group size:

```text
5
```

Frequencies:

```text
a → 3
b → 1
c → 1
```

If we make everything `a`:

```text
changes = 2
```

If everything `b`:

```text
changes = 4
```

If everything `c`:

```text
changes = 4
```

So choose the most frequent character.

Formula:

```math
changes
=
groupSize-maxFrequency
```

Actual:

```text
5 - 3
= 2
```

This is Problem 3's local greedy choice.

---

## 1.13 Universal Proof Checklist

For each problem ask:

```text
1. What is the objective?
2. What information actually matters?
3. Can I compress the state into frequencies?
4. Is there an unavoidable upper/lower bound?
5. Is some pairing forced?
6. Can I merge equal states?
7. What are the deficits?
8. What positions are safely replaceable?
9. Which positions are forced equal?
10. If one group must become equal,
    which final value minimizes changes?
```

---

# 2. Problem 1 — Duff and Weight Lifting

**Platform:** Codeforces  
**Problem:** 587A — Duff and Weight Lifting  
**Link:** https://codeforces.com/problemset/problem/587/A

---

## 2.1 What the Problem Asks

Input gives exponents:

```text
a1, a2, ..., an
```

The actual weights are:

```math
2^{a_1},2^{a_2},\ldots,2^{a_n}
```

In one step, Duff may throw away a group of weights whose **total weight is a power of two**.

Goal:

```text
minimum number of steps
```

---

## 2.2 Story → Mathematical Model

One step may remove a subset whose sum is:

```math
2^k
```

for some integer `k`.

So we want to:

```text
combine as many weights as possible
into power-of-two groups
```

Each final group costs:

```text
1 step
```

Therefore:

```text
minimize steps
=
minimize number of final groups
```

---

## 2.3 Tiny Example

Input exponents:

```text
1 1 2 3 3
```

Actual weights:

```text
2 2 4 8 8
```

Pair:

```text
2 + 2
= 4
```

Now conceptually:

```text
4 + 4
= 8
```

Then:

```text
8 + 8
= 16
```

But we must account for all copies carefully through frequencies.

The lecture's frequency-carry method gives the minimum final number of groups.

---

## 2.4 Key Observation 1 — All Weights Are Powers of Two

This means equal powers combine perfectly:

```math
2^i+2^i=2^{i+1}
```

Actual:

```text
8+8
=16
```

So:

```text
two equal groups
can always be merged into one larger power-of-two group
```

That strictly reduces final step count:

```text
2 groups → 1 group
```

Therefore:

```text
pair equal powers whenever possible
```

---

## 2.5 Key Observation 2 — Why the Smallest Power Needs a Partner

Suppose a valid group contains several powers of two.

Let the smallest weight be:

```math
2^a
```

Example group:

```text
2^a, 2^b, 2^c, ...
```

with:

```text
a <= b <= c
```

If this group contains more than one item and sums to a larger power of two, the smallest `2^a` cannot appear only once.

Why?

All larger weights are multiples of:

```math
2^{a+1}
```

But one single:

```math
2^a
```

leaves an unmatched lower bit.

So another:

```math
2^a
```

is needed.

---

## 2.6 Understand the Proof With Numbers First

Suppose smallest weight is:

```text
2^2 = 4
```

and all other weights are larger powers:

```text
8,16,32,...
```

Every larger weight is divisible by:

```text
8
```

Suppose there is only one `4`.

Then the total looks like:

```text
4 + multiple of 8
```

Examples:

```text
4 + 8  = 12
4 + 16 = 20
4 + 24 = 28
```

All are:

```text
4 mod 8
```

But any power of two larger than `4` is divisible by `8`:

```text
8,16,32,64,...
```

So:

```text
one lone 4 cannot participate
in a larger power-of-two sum
```

We need another `4`:

```text
4+4
=8
```

Now the low bit carries.

---

## 2.7 General Mathematical Proof

Smallest weight:

```math
2^a
```

All larger powers are divisible by:

```math
2^{a+1}
```

Suppose the group contains exactly one `2^a`.

Then total sum is:

```math
2^a + q\cdot2^{a+1}
```

for some integer `q`.

Factor:

```math
2^a(1+2q)
```

The factor:

```text
1+2q
```

is odd.

For the whole sum to be a larger power of two, there can be no odd factor greater than 1.

So one lone smallest power cannot form a larger power-of-two total with only larger powers.

Therefore:

```text
if 2^a belongs to a multi-item group,
another 2^a must exist
```

This justifies pairing smallest equal powers.

---

## 2.8 The Carry Operation

Suppose:

```text
freq[i] = x
```

meaning:

```text
x copies of weight 2^i
```

Every pair:

```text
2^i + 2^i
```

becomes:

```text
one virtual 2^(i+1) group
```

Number of pairs:

```math
\left\lfloor\frac{x}{2}\right\rfloor
```

Carry them:

```math
freq[i+1]
=
\left\lfloor\frac{x}{2}\right\rfloor
```

Leftover:

```math
freq[i]
\leftarrow
x\bmod2
```

So after processing exponent `i`:

```text
freq[i] is only 0 or 1
```

---

## 2.9 Actual Carry Example

Suppose:

```text
freq[3] = 5
```

This means:

```text
five weights of 2^3 = 8
```

Weights:

```text
8 8 8 8 8
```

Pair:

```text
8+8 = 16
8+8 = 16
```

So:

```text
2 carries to exponent 4
1 leftover 8
```

Mathematically:

```text
5 / 2 = 2
5 % 2 = 1
```

Therefore:

```text
freq[4] += 2
freq[3]  = 1
```

---

## 2.10 Why Pairing As Much As Possible Is Optimal

Suppose two equal power groups remain separate:

```text
2^i
2^i
```

They require:

```text
2 separate final steps
```

But together:

```math
2^i+2^i=2^{i+1}
```

requires:

```text
1 step
```

So leaving an available pair unmerged is never optimal.

Therefore:

```text
pair every possible equal power
```

This is a local improvement that always reduces the number of groups.

---

## 2.11 Full Dry Run

Input exponents:

```text
1 1 2 3 3
```

Initial frequencies:

```text
f[1] = 2
f[2] = 1
f[3] = 2
```

### Exponent 1

```text
f[1] = 2
```

Pairs:

```text
2/2 = 1
```

Carry:

```text
f[2] += 1
```

So:

```text
f[2] = 2
```

Remainder:

```text
f[1] = 0
```

---

### Exponent 2

```text
f[2] = 2
```

Pair:

```text
2^2 + 2^2
= 2^3
```

Carry:

```text
f[3] += 1
```

Now:

```text
f[3] = 3
```

Remainder:

```text
f[2] = 0
```

---

### Exponent 3

```text
f[3] = 3
```

One pair carries:

```text
3/2 = 1
```

So:

```text
f[4] += 1
```

One leftover:

```text
f[3] = 1
```

Now:

```text
f[4] = 1
```

No pair at exponent 4.

Final leftovers:

```text
f[3] = 1
f[4] = 1
```

So:

```text
answer = 2 groups
```

Therefore:

```text
minimum steps = 2
```

---

## 2.12 Visual Carry Model

```text
exponents:
1   1   2   3   3

weights:
2   2   4   8   8

2+2
 ↓
 4

now:
4   4   8   8

4+4
 ↓
 8

now:
8   8   8

two 8s
 ↓
16

left:
8,16

answer = 2
```

This is essentially:

```text
binary carrying on frequency counts
```

---

## 2.13 Algorithm

```text
1. Count frequency of every exponent.

2. Process exponents from smallest to largest.

3. At exponent i:
      pairs = freq[i] / 2

4. Carry pairs:
      freq[i+1] += pairs

5. Keep only remainder:
      freq[i] %= 2

6. Add final leftovers to answer.

7. Return answer.
```

---

## 2.14 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    const int MAXA = 100000 + 100;

    vector<long long> freq(MAXA, 0);

    int maxA = 0;

    for (int i = 0; i < n; ++i) {
        int a;
        cin >> a;

        ++freq[a];
        maxA = max(maxA, a);
    }

    long long answer = 0;

    // Extra positions are needed because carries can move upward.
    for (int i = 0; i < MAXA - 1; ++i) {
        freq[i + 1] += freq[i] / 2;

        freq[i] %= 2;

        answer += freq[i];
    }

    answer += freq[MAXA - 1];

    cout << answer << '\n';
}
```

---

## 2.15 Complexity

If exponents are bounded by `A`:

```text
frequency construction:
O(N)

carry scan:
O(A)
```

Total:

```text
O(N + A)
```

A sorting implementation is possible, but frequency carry is simpler for bounded exponents.

---

## 2.16 Recognition Model

When you see:

```text
values are powers of two
+
two equal values combine into next power
+
want minimum number of final groups
```

think:

```text
frequency
→ pair equal powers
→ carry upward
→ count remainders
```

---

## 2.17 Don't-Memorize Model

Do not memorize:

```text
f[i+1] += f[i]/2
f[i] %= 2
```

Remember why:

```text
2^i + 2^i
= 2^(i+1)

two groups
→ one group

so pair every possible equal power
```

---

# 3. Problem 2 — Polycarp at the Radio

**Platform:** Codeforces  
**Problem:** 723C — Polycarp at the Radio  
**Link:** https://codeforces.com/problemset/problem/723/C

---

## 3.1 What the Problem Asks

We have:

```text
n playlist positions
```

Each position contains a band number.

Polycarp likes only bands:

```text
1,2,...,m
```

Let:

```text
b_i
=
frequency of band i
```

for:

```text
1 <= i <= m
```

We want to:

```text
maximize:
min(b_1,b_2,...,b_m)
```

Among all ways achieving that maximum minimum frequency, minimize:

```text
number of replacements
```

---

## 3.2 Story → Mathematical Variables

Let:

```text
x
=
minimum frequency we want every band 1..m to have
```

Then requirement:

```math
b_i\ge x
```

for every:

```text
1 <= i <= m
```

Therefore total number of required positions is at least:

```math
m\cdot x
```

But only:

```text
n
```

positions exist.

So:

```math
m\cdot x\le n
```

---

## 3.3 First Key Observation — Upper Bound

From:

```math
m\cdot x\le n
```

we get:

```math
x\le\left\lfloor\frac{n}{m}\right\rfloor
```

Therefore the minimum frequency can never exceed:

```math
\left\lfloor\frac{n}{m}\right\rfloor
```

This is the average/pigeonhole bound.

---

## 3.4 Understand the Bound With Numbers First

Suppose:

```text
n = 8
m = 3
```

Can every band appear at least:

```text
3 times?
```

That would require:

```text
band 1 → 3
band 2 → 3
band 3 → 3

total:
3+3+3
= 9
```

But:

```text
n = 8
```

Impossible.

So:

```text
minimum frequency <= 2
```

And:

```text
floor(8/3)
= 2
```

---

## 3.5 Why the Upper Bound Is Achievable

Let:

```math
x=\left\lfloor\frac{n}{m}\right\rfloor
```

Then:

```math
m\cdot x\le n
```

So there are enough positions in the playlist to allocate:

```text
x copies
```

to each desired band.

Because any position can be replaced with any band number, we can fill every deficit.

Therefore the maximum possible minimum frequency is exactly:

```math
x=\left\lfloor\frac{n}{m}\right\rfloor
```

For compatibility with markdown renderers, remember simply:

```text
optimal minimum frequency
=
floor(n/m)
```

---

## 3.6 Now the Real Task — Minimize Changes

Once:

```text
x = floor(n/m)
```

is known, we want every band:

```text
1..m
```

to appear **at least x times**.

Do not change useful copies unnecessarily.

Preserve:

```text
up to x copies
```

of every desired band.

Only change:

```text
1. values > m
2. extra copies of a band beyond x
```

These are the **replaceable positions**.

---

## 3.7 Tiny Example First

Suppose:

```text
n = 8
m = 3
```

Playlist:

```text
[1,1,1,2,2,4,7,2]
```

Frequencies among desired bands:

```text
band 1 → 3
band 2 → 3
band 3 → 0
```

Optimal minimum:

```text
x
= floor(8/3)
= 2
```

Need:

```text
band 1 >= 2
band 2 >= 2
band 3 >= 2
```

Band 3 deficit:

```text
2
```

Replace outside-range values:

```text
4 → 3
7 → 3
```

Final:

```text
[1,1,1,2,2,3,3,2]
```

Counts:

```text
1 → 3
2 → 3
3 → 2
```

Minimum:

```text
2
```

Changes:

```text
2
```

---

## 3.8 Why These Two Changes Are Minimum

Band 3 initially has:

```text
0
```

but must reach:

```text
2
```

Each replacement can increase band 3's count by at most:

```text
1
```

Therefore at least:

```text
2 changes
```

are necessary.

We achieved exactly:

```text
2
```

So it is optimal.

---

## 3.9 Deficit Formula

For desired band `i`:

```text
current frequency = f[i]
target = x
```

Missing amount:

```math
d_i
=
\max(0,x-f[i])
```

Total minimum required changes:

```math
D
=
\sum_{i=1}^{m}
\max(0,x-f[i])
```

Why is this a lower bound?

Because every missing occurrence needs one position changed into that band.

---

## 3.10 Actual Deficit Example

Using:

```text
x = 2
```

Counts:

```text
f[1] = 3
f[2] = 3
f[3] = 0
```

Deficits:

```text
d1 = max(0,2-3) = 0

d2 = max(0,2-3) = 0

d3 = max(0,2-0) = 2
```

Total:

```text
D = 0+0+2
= 2
```

So at least:

```text
2 changes
```

are required.

---

## 3.11 Which Positions Can We Change Safely?

### Type 1 — Value Outside `1..m`

Example:

```text
7
```

when:

```text
m = 3
```

This value contributes nothing to:

```text
b_1,b_2,b_3
```

So changing it cannot decrease any desired band's protected count.

---

### Type 2 — Surplus Copy

Suppose:

```text
target x = 2
```

and:

```text
band 1 appears 5 times
```

We only need to preserve:

```text
2 copies
```

So:

```text
5-2
= 3
```

copies are surplus and may be replaced.

---

## 3.12 Why There Are Always Enough Replaceable Positions

Protected useful copies:

```math
\sum_{i=1}^{m}\min(f[i],x)
```

So replaceable positions:

```math
C
=
n-
\sum_{i=1}^{m}\min(f[i],x)
```

Total deficit:

```math
D
=
mx-
\sum_{i=1}^{m}\min(f[i],x)
```

Subtract:

```math
C-D
=
n-mx
```

Since:

```math
x=\left\lfloor\frac{n}{m}\right\rfloor
```

we know:

```math
mx\le n
```

Therefore:

```math
C-D\ge0
```

So:

```math
C\ge D
```

Meaning:

```text
there are always enough replaceable positions
to fill every deficit
```

---

## 3.13 Same Proof With Numbers

Example:

```text
n = 8
m = 3
x = 2
```

Counts:

```text
f1 = 3
f2 = 3
f3 = 0
```

Protected:

```text
min(3,2)
+
min(3,2)
+
min(0,2)

= 2+2+0
= 4
```

Replaceable count:

```text
C
= 8-4
= 4
```

Required total:

```text
m*x
= 3×2
= 6
```

Deficit:

```text
D
= 6-4
= 2
```

So:

```text
C = 4
D = 2

C >= D
```

Enough candidates exist.

We only need to use:

```text
2 of the 4 replaceable positions
```

because minimizing changes is secondary objective.

---

## 3.14 Greedy Construction

Prepare a list of deficient bands.

Example:

```text
target x = 2

freq:
1 → 3
2 → 3
3 → 0
```

Need list:

```text
[3,3]
```

Now scan the array.

Change a position only if:

```text
value > m
```

or:

```text
current frequency of this value > x
```

and deficits still remain.

Each change:

```text
old value → needed band
```

Then update both frequencies.

---

## 3.15 Important Implementation Detail

Suppose:

```text
value = 1
freq[1] = 3
target = 2
```

One copy of `1` is surplus.

If we change it:

```text
freq[1] becomes 2
```

Now remaining `1`s are protected.

So while scanning:

```text
change only while freq[a[i]] > x
```

not every occurrence of a surplus-valued band.

---

## 3.16 Full Dry Run

```text
n = 8
m = 3

a =
[1,1,1,2,2,4,7,2]
```

Target:

```text
x
= floor(8/3)
= 2
```

Initial desired counts:

```text
1 → 3
2 → 3
3 → 0
```

Deficits:

```text
need two 3s
```

Need list:

```text
[3,3]
```

Scan:

### Position 0 — value 1

```text
freq[1] = 3 > 2
```

This copy is technically surplus.

We could change it, but we do not have to prefer it over outside values. Any valid candidate is okay.

Suppose we leave it.

---

### Position 5 — value 4

```text
4 > m
```

Definitely replaceable.

Change:

```text
4 → 3
```

Now:

```text
freq[3] = 1
```

Need one more `3`.

---

### Position 6 — value 7

```text
7 > m
```

Change:

```text
7 → 3
```

Now:

```text
freq[3] = 2
```

All deficits filled.

Stop changing.

Final:

```text
[1,1,1,2,2,3,3,2]
```

Changes:

```text
2
```

---

## 3.17 Why Binary Search Is Unnecessary Here

A monotonic feasibility idea exists:

```text
if minimum frequency x is achievable,
every smaller value is also achievable
```

So binary search on answer is possible.

But the direct counting bound gives:

```text
x = floor(n/m)
```

immediately.

So:

```text
direct mathematical observation
beats binary search
```

---

## 3.18 Algorithm

```text
1. Read n, m and array.

2. target = n / m.

3. Count frequencies for values 1..m.

4. Build a list "need":
      for each i in 1..m
      add i exactly target-freq[i] times
      if freq[i] < target.

5. Scan array while need is not empty.

6. A position is replaceable if:
      a[i] > m
      OR
      freq[a[i]] > target.

7. Replace it by one needed band.

8. Update frequencies.

9. Number of replacements = initial need size.

10. Output:
      target
      changes
      final array
```

---

## 3.19 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<int> a(n);
    vector<int> freq(m + 1, 0);

    for (int i = 0; i < n; ++i) {
        cin >> a[i];

        if (1 <= a[i] && a[i] <= m)
            ++freq[a[i]];
    }

    int target = n / m;

    vector<int> need;

    for (int value = 1; value <= m; ++value) {
        while (freq[value] < target) {
            need.push_back(value);
            ++freq[value];
        }
    }

    int changes = (int)need.size();

    // Restore original desired-band frequencies.
    fill(freq.begin(), freq.end(), 0);

    for (int x : a) {
        if (1 <= x && x <= m)
            ++freq[x];
    }

    int ptr = 0;

    for (int i = 0; i < n && ptr < changes; ++i) {
        int x = a[i];

        if (x > m) {
            a[i] = need[ptr++];
        } else if (freq[x] > target) {
            --freq[x];
            a[i] = need[ptr++];
        }
    }

    cout << target << ' ' << changes << '\n';

    for (int i = 0; i < n; ++i) {
        cout << a[i]
             << (i + 1 == n ? '\n' : ' ');
    }
}
```

---

## 3.20 Complexity

Frequency building:

```text
O(N)
```

Need construction:

```text
O(N + M)
```

Final scan:

```text
O(N)
```

Total:

```text
O(N + M)
```

Space:

```text
O(N + M)
```

---

## 3.21 Recognition Model

When you see:

```text
maximize the minimum frequency
among m categories
+
total items fixed at n
+
arbitrary replacements allowed
```

think:

```text
average upper bound
→ floor(n/m)

then:
preserve useful copies
→ fill deficits
→ change outside/surplus positions
```

---

## 3.22 Don't-Memorize Model

Do not memorize:

```text
target = n/m
then some replacement code
```

Understand:

```text
To have minimum frequency x:

m*x <= n

therefore:
x <= floor(n/m)

This bound is achievable.

Then every missing required occurrence
forces one replacement.

So:
minimum changes = total deficit.
```

---

# 4. Problem 3 — K-Complete Word

**Platform:** Codeforces  
**Problem:** 1332C — K-Complete Word  
**Link:** https://codeforces.com/problemset/problem/1332/C

---

## 4.1 What the Problem Asks

A string is `k`-complete if:

```text
1. it is a palindrome
2. it has period k
```

Given:

```text
n
k
string s
```

where `n` is divisible by `k`, find:

```text
minimum character replacements
```

needed to make `s` k-complete.

---

## 4.2 Problem-Specific Prerequisite — Period k

Period `k` means:

```math
s[i]=s[i+k]
```

whenever both indices exist.

Example:

```text
n = 6
k = 3

positions:
0 1 2 | 3 4 5
```

Must have:

```text
s0 = s3
s1 = s4
s2 = s5
```

So every length-3 block is identical.

---

## 4.3 Problem-Specific Prerequisite — Palindrome

Palindrome requires:

```math
s[i]=s[n-1-i]
```

For:

```text
n = 6
```

pairs:

```text
0 ↔ 5
1 ↔ 4
2 ↔ 3
```

---

## 4.4 Combine Period + Palindrome

Consider one block of length:

```text
k
```

Offsets:

```text
0,1,2,...,k-1
```

Palindrome inside the repeating structure links:

```text
0 ↔ k-1
1 ↔ k-2
2 ↔ k-3
...
```

Periodicity links the same offsets across all blocks.

Therefore:

```text
all positions at offset i
AND
all positions at offset k-1-i
```

must become the same character.

That forms one equivalence group.

---

## 4.5 Concrete Position Example

Take:

```text
n = 12
k = 4
```

Blocks:

```text
0  1  2  3
4  5  6  7
8  9 10 11
```

Period constraint:

```text
0 = 4 = 8
1 = 5 = 9
2 = 6 = 10
3 = 7 = 11
```

Palindrome symmetry inside each block pairs offsets:

```text
0 ↔ 3
1 ↔ 2
```

So final groups are:

```text
Group A:
0,3,4,7,8,11

Group B:
1,2,5,6,9,10
```

Every character inside each group must become identical.

---

## 4.6 Why Only `(k+1)/2` Groups?

Offsets:

```text
0 ↔ k-1
1 ↔ k-2
...
```

Each pair is processed once.

Number of unique pairs/groups:

```math
\left\lceil\frac{k}{2}\right\rceil
```

In integer C++ arithmetic:

```text
(k+1)/2
```

For odd `k`, the middle offset pairs with itself.

Example:

```text
k = 5

0 ↔ 4
1 ↔ 3
2 ↔ 2
```

So:

```text
3 groups
```

---

## 4.7 Tiny Example — `abaaba`, k = 2

String:

```text
abaaba
```

Positions:

```text
0 1 | 2 3 | 4 5
a b | a a | b a
```

With:

```text
k = 2
```

offset pair:

```text
0 ↔ 1
```

Since every period block must be identical and palindromic, all positions fall into one equality group:

```text
0,1,2,3,4,5
```

Characters:

```text
a b a a b a
```

Frequencies:

```text
a → 4
b → 2
```

Best final character:

```text
a
```

Changes:

```text
6 - 4
= 2
```

Answer:

```text
2
```

---

## 4.8 Why Majority Character Is Optimal

Suppose a must-be-equal group contains:

```text
a a a b b c
```

Group size:

```text
6
```

Counts:

```text
a → 3
b → 2
c → 1
```

If final group is all `a`:

```text
keep 3
change 3
```

Cost:

```text
3
```

If all `b`:

```text
keep 2
change 4
```

Cost:

```text
4
```

If all `c`:

```text
keep 1
change 5
```

Cost:

```text
5
```

So choose:

```text
max-frequency character
```

---

## 4.9 General Mathematical Derivation

Let one equality group have size:

```math
S
```

Suppose character `c` occurs:

```math
f_c
```

times.

If we make the whole group character `c`:

```text
already-correct positions:
f_c
```

Need to change:

```math
S-f_c
```

To minimize changes:

```math
\min_c(S-f_c)
```

Since `S` is constant:

```text
minimize S-f_c
=
maximize f_c
```

Therefore:

```math
minimumChanges
=
S-\max_c f_c
```

This is the entire local greedy proof.

---

## 4.10 Same Derivation With Numbers

Group:

```text
[a,a,b,a,c]
```

Size:

```text
S = 5
```

Frequencies:

```text
a = 3
b = 1
c = 1
```

If choose `a`:

```text
changes
= S - f_a

= 5 - 3

= 2
```

If choose `b`:

```text
5 - 1
= 4
```

If choose `c`:

```text
5 - 1
= 4
```

Maximum frequency:

```text
3
```

So minimum:

```text
5-3
= 2
```

---

## 4.11 Why Groups Can Be Solved Independently

Each position belongs to exactly one must-be-equal group.

Changing a character in one group:

```text
does not affect constraints of another group
```

Therefore total cost is:

```math
\sum_{\text{groups}}
(
groupSize-maxFrequency
)
```

So we can optimize every group independently and add the costs.

This is a classic:

```text
independent local optimization
```

structure.

---

## 4.12 Build a Group

For offset:

```text
i
```

mirror offset:

```text
j = k-1-i
```

For every block start:

```text
base = 0, k, 2k, ...
```

collect:

```text
base+i
```

and:

```text
base+j
```

If:

```text
i == j
```

do not count the same position twice.

---

## 4.13 Detailed Example — n = 6, k = 3

Positions:

```text
0 1 2 | 3 4 5
```

Offset mirror pairs:

```text
0 ↔ 2
1 ↔ 1
```

### Group 1 — offsets 0 and 2

Positions:

```text
0,2,3,5
```

because:

```text
block 0:
0 and 2

block 1:
3 and 5
```

### Group 2 — middle offset 1

Positions:

```text
1,4
```

Only once per block because:

```text
1 mirrors itself
```

Now count character frequencies separately in each group.

---

## 4.14 Example — `abaaba`, k = 3

String:

```text
a b a | a b a
```

### Group offsets 0 and 2

Positions:

```text
0,2,3,5
```

Characters:

```text
a,a,a,a
```

Cost:

```text
0
```

### Middle group offset 1

Positions:

```text
1,4
```

Characters:

```text
b,b
```

Cost:

```text
0
```

Total:

```text
0
```

So the string is already 3-complete.

---

## 4.15 Visual Model

For:

```text
k = 5
```

one period:

```text
offset:
0   1   2   3   4
|   |   |   |   |
+---------------+
```

Palindrome links:

```text
0 <------------> 4
1 <------> 3
2 <--> 2
```

Periodicity repeats each offset across all blocks:

```text
block 1: 0 1 2 3 4
block 2: 0 1 2 3 4
block 3: 0 1 2 3 4
```

So equality groups are:

```text
all 0/4 positions
all 1/3 positions
all 2 positions
```

For each group:

```text
count letters
→ keep majority
→ change the rest
```

---

## 4.16 Algorithm

```text
answer = 0

for i from 0 to (k-1)/2:

    j = k-1-i

    count[26] = 0
    groupSize = 0

    for base = 0; base < n; base += k:

        add s[base+i]

        if i != j:
            add s[base+j]

    maxFreq = maximum letter frequency

    answer += groupSize - maxFreq

return answer
```

---

## 4.17 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int minChangesKComplete(
    int n,
    int k,
    const string& s
) {
    int answer = 0;

    for (int i = 0; i <= (k - 1) / 2; ++i) {
        int j = k - 1 - i;

        array<int, 26> freq{};
        int groupSize = 0;

        for (int base = 0; base < n; base += k) {
            ++freq[s[base + i] - 'a'];
            ++groupSize;

            if (i != j) {
                ++freq[s[base + j] - 'a'];
                ++groupSize;
            }
        }

        int maxFreq = 0;

        for (int f : freq)
            maxFreq = max(maxFreq, f);

        answer += groupSize - maxFreq;
    }

    return answer;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int T;
    cin >> T;

    while (T--) {
        int n, k;
        string s;

        cin >> n >> k >> s;

        cout << minChangesKComplete(n, k, s)
             << '\n';
    }
}
```

---

## 4.18 Complexity

There are roughly:

```text
k/2 groups
```

Each group scans:

```text
n/k blocks
```

and usually reads 2 positions per block.

So total visited characters:

```text
O(N)
```

Letter-frequency scan:

```text
26 per group
```

Total:

```text
O(N + 26K)
```

Usually written:

```text
O(N)
```

because alphabet size is constant and `k <= n`.

Space:

```text
O(26)
```

apart from input.

---

## 4.19 Edge Cases

### k = 1

Every block has one character.

Period 1 means:

```text
all positions equal
```

So choose global majority character.

---

### Odd k

Middle offset:

```text
i = k-1-i
```

must be counted only once per block.

---

### Already k-complete

Every equality group already consists of one character.

Cost:

```text
0
```

---

### Multiple Majority Characters

If two characters tie for max frequency:

```text
either one gives same minimum changes
```

Only the number of changes matters.

---

## 4.20 Recognition Model

When you see:

```text
string constraints force
many positions to be equal
+
you may replace characters
+
want minimum changes
```

think:

```text
build equivalence groups
→ count characters per group
→ keep majority
→ change all others
```

---

## 4.21 Don't-Memorize Model

Do not memorize:

```text
loop i < (k+1)/2
count 26 letters
answer += size-maxFreq
```

Understand:

```text
period constraint
+
palindrome constraint
        |
        v
certain positions MUST be equal
        |
        v
each equality class is independent
        |
        v
keep the most common character
        |
        v
change the rest
```

---

# 5. Pattern Comparison

| Problem | Core Signal | Pattern | Proof Style |
|---|---|---|---|
| Duff and Weight Lifting | powers of two | frequency carry | forced pairing / local merge |
| Polycarp at the Radio | maximize minimum frequency | average bound + deficits | counting lower/upper bound |
| K-Complete Word | multiple equality constraints | equivalence groups + majority | local majority minimization |

---

## 5.1 Duff Pattern

```text
two equal powers
      |
      v
merge into next power
      |
      v
frequency carry
      |
      v
count final leftovers
```

---

## 5.2 Polycarp Pattern

```text
maximize min frequency
        |
        v
m*x <= n
        |
        v
x = floor(n/m)
        |
        v
find deficits
        |
        v
replace outside/surplus positions
```

---

## 5.3 K-Complete Pattern

```text
period
+
palindrome
    |
    v
must-be-equal groups
    |
    v
frequency per group
    |
    v
keep max-frequency character
    |
    v
cost = size-maxFreq
```

---

# 6. Final Recognition Checklist

Ask:

```text
1. Are values powers of two?
2. Can two equal states merge into one?
3. Does this resemble carrying in binary?
4. Am I maximizing the minimum among m groups?
5. Does average give an immediate upper bound?
6. Is the upper bound achievable?
7. Which groups are deficient?
8. Which positions are surplus / safely replaceable?
9. Do constraints imply positions must be equal?
10. Can I form equality classes?
11. For one equality class, can I keep the majority?
12. Can I prove minimum changes as:
       size - maxFrequency?
```

---

# 7. Compact Revision Card

```text
GREEDY PROBLEM SOLVING 2
========================


1. DUFF AND WEIGHT LIFTING
--------------------------
weights:
2^a

identity:
2^i + 2^i = 2^(i+1)

frequency carry:

f[i+1] += f[i]/2
f[i] %= 2

why safe?
two equal groups can always
become one larger group

minimum answer:
sum final leftovers


2. POLYCARP AT THE RADIO
------------------------
want:
maximize min frequency
among bands 1..m

if minimum is x:

m*x <= n

therefore:

x <= floor(n/m)

achievable target:

x = floor(n/m)

deficit:

sum max(0, x-f[i])

replaceable:
value > m
or
surplus copies where freq > x

minimum changes:
total deficit


3. K-COMPLETE WORD
------------------
period k:
same offsets across blocks equal

palindrome:
offset i
must equal
offset k-1-i

therefore:
build equality groups

for each group:

changes
=
groupSize
-
maxCharacterFrequency

sum over groups
```

---

# Final Mental Model

```text
              GREEDY PROBLEM SOLVING 2
                       |
        +--------------+--------------+
        |              |              |
   powers of 2     balanced counts   equality constraints
        |              |              |
        v              v              v
    pair/carry      average bound     build groups
        |              |              |
        v              v              v
   leftovers       fill deficits      keep majority
```

> **Core lesson:** compress the problem into the right mathematical object first — **frequency carry, deficit counts, or equality groups** — and the greedy choice becomes almost forced.
