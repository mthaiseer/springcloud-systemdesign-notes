# Part 6. Counting & Combinatorics

> **Core idea:** Count **how many ways** without explicitly generating every possibility. In Codeforces, first ask whether choices are **separate cases, sequential choices, ordered, unordered, repeated, complementary, overlapping, or contribution-based**.

## Table of Contents

- [6.1 Two Basic Rules](#61-two-basic-rules)
- [6.2 Factorial, Permutation, Combination](#62-factorial-permutation-combination)
- [6.3 Pairs — The Most Useful Formula in CF](#63-pairs--the-most-useful-formula-in-cf)
- [6.4 Complement Counting](#64-complement-counting)
- [6.5 Stars and Bars — Distribute Identical Objects](#65-stars-and-bars--distribute-identical-objects)
- [6.6 Inclusion-Exclusion](#66-inclusion-exclusion)
- [6.7 Pigeonhole and Contribution](#67-pigeonhole-and-contribution)
- [Section Summary](#section-summary)
- [30-Second Revision](#30-second-revision)

---

## 6.1 Two Basic Rules

### ASCII / Structural Visual

```text
ADDITION RULE
choose ONE separate case

        ┌─ shirt A
choice ─┼─ shirt B       3 shirt choices
        └─ shirt C

OR

        ┌─ hat X
choice ─└─ hat Y         2 hat choices

total = 3 + 2 = 5


MULTIPLICATION RULE
choices happen in stages

shirt        pants
  3     ×      2
  ↓            ↓
first       second
choice      choice

total outfits = 3 × 2 = 6
```

### Core Formula / Rule

| Situation | Rule |
|---|---|
| disjoint alternatives: case A **or** case B | add |
| sequential independent choices: A **then** B | multiply |

### Short Derivation

```text
3 shirts:
S1 S2 S3

For EACH shirt there are 2 pants:
P1 P2

S1P1 S1P2
S2P1 S2P2
S3P1 S3P2

3 groups × 2 choices = 6
```

### Visual Dry Run

```text
API login supports:

3 password providers
OR
2 passkey providers

mutually exclusive provider choice:
3 + 2 = 5 choices

After provider selection:
4 regions available

provider × region:
5 × 4 = 20 configurations
```

### Statement → Mathematical Model

```text
"choose from A OR B"
        ↓
separate disjoint cases
        ↓
ADD

"choose A AND then choose B"
        ↓
sequential choices
        ↓
MULTIPLY
```

### Codeforces Recognition

```text
"either ... or ..."      → addition rule
"for each choice..."     → multiplication rule
"choose one from each"   → multiplication rule
```

### Minimal C++

```cpp
long long separate = waysA + waysB;
long long sequential = waysA * waysB;
```

### Common Traps / Edge Cases

The addition rule directly adds counts only when the cases do not overlap. If they overlap, inclusion-exclusion may be needed.

> **Real-World Engineering Case:** Suppose deployment can use 3 AWS regions or 2 Azure regions, with no overlap: there are `3+2=5` location choices. If each location supports 4 independent database tiers, there are `5×4=20` deployment configurations.

---

## 6.2 Factorial, Permutation, Combination

### ASCII / Structural Visual

```text
4 distinct items: A B C D

Arrange ALL:
first slot  = 4 choices
second      = 3
third       = 2
fourth      = 1

4! = 4×3×2×1 = 24


Choose 2 from {A,B,C,D}

ORDER MATTERS:
AB BA AC CA ...
→ permutation

ORDER DOES NOT MATTER:
AB AC AD BC BD CD
→ combination
```

### Core Formula / Rule

```text
n!      = n(n-1)...1
P(n,k)  = n! / (n-k)!
C(n,k)  = n! / (k!(n-k)!)
```

| Question | Use |
|---|---|
| arrange all distinct items | `n!` |
| choose `k`, order matters | `P(n,k)` |
| choose `k`, order irrelevant | `C(n,k)` |

### Short Derivation — `C(n,k)`

```text
Ordered selections:
P(n,k)

But every chosen set of k elements
appears in k! different orders.

Therefore:

C(n,k) = P(n,k) / k!
       = n! / (k!(n-k)!)
```

### Visual Dry Run — Choose 2 from 5

```text
People:
A B C D E

Ordered:
5 choices for first
4 choices for second

5×4 = 20

But:
AB and BA represent same team.

Every pair counted 2! times.

20 / 2
= 10

C(5,2)=10
```

### Statement → Mathematical Model

```text
"choose 3 winners with gold/silver/bronze"
        ↓
order matters
        ↓
P(n,3)

"choose 3 people for a team"
        ↓
order does NOT matter
        ↓
C(n,3)
```

### Codeforces Recognition

```text
arrange             → factorial
rank / positions    → permutation
team / subset       → combination
```

### Minimal C++

```cpp
long long C2(long long n) { return n * (n - 1) / 2; }
```

### Common Traps / Edge Cases

- Decide **ordered vs unordered** before writing a formula.
- Factorials overflow quickly; `20!` fits in signed 64-bit, `21!` does not.
- For modular combinations, the source later recommends factorial and inverse-factorial precomputation.

> **Real-World Engineering Case:** Choosing 3 engineers for an incident-response team is a combination because order does not matter; assigning commander, investigator, and communicator is a permutation because the roles make order meaningful.

---

## 6.3 Pairs — The Most Useful Formula in CF

### ASCII / Structural Visual

```text
4 items:

A ─ B
├── C
└── D

B ─ C
└── D

C ─ D

Pairs:
AB AC AD BC BD CD

count = 6
```

### Core Formula / Rule

```text
C(n,2)
= n(n-1)/2
```

If one value appears `f` times:

```text
equal pairs from that value
= f(f-1)/2
```

### Short Derivation

```text
Choose first element:  n ways
Choose second:         n-1 ways

n(n-1)

But pair (A,B) and (B,A)
are the SAME unordered pair.

divide by 2:

n(n-1)/2
```

### Visual Dry Run — Frequencies

```text
A = [1,1,2,2,2]

frequency:
1 → 2 copies
2 → 3 copies

value 1:
C(2,2) = 1

value 2:
C(3,2) = 3

total equal pairs:
1 + 3 = 4
```

### Statement → Mathematical Model

```text
"count pairs i<j with A[i]=A[j]"
                ↓
group equal values
                ↓
for each frequency f
add C(f,2)
                ↓
Σ f(f-1)/2
```

### Codeforces Recognition

```text
"number of pairs"
"i < j"
"two equal values"
"frequency f"
        ↓
C(f,2)
```

### Minimal C++

```cpp
ans += 1LL * f * (f - 1) / 2;
```

### Common Traps / Edge Cases

`C(200000,2)` is about `2×10^10`, so use `long long`. Also distinguish unordered pairs from ordered pairs.

> **Real-World Engineering Case:** If 1,000 requests share the same cache key, the number of unordered request pairs that collide on that key is `1000×999/2`, useful when reasoning about pairwise contention or duplicate comparisons.

---

## 6.4 Complement Counting

### ASCII / Structural Visual

```text
ALL possibilities
┌────────────────────────────┐
│                            │
│      GOOD      BAD         │
│                            │
└────────────────────────────┘

TOTAL = GOOD + BAD

therefore:

GOOD = TOTAL - BAD
```

### Core Formula / Rule

```text
desired = total - bad
```

Especially useful when:

```text
"at least one ..."
```

is complicated, but:

```text
"none ..."
```

is easy.

### Short Derivation

If every outcome is either desired or bad, and the two groups do not overlap:

```text
total = desired + bad

desired = total - bad
```

### Visual Dry Run

Suppose 4 independent servers can each be UP or DOWN. Count states with **at least one server DOWN**.

```text
total states:
2^4 = 16

bad for our goal:
NO server is down
= all 4 are UP
= 1 state

desired:
16 - 1 = 15
```

### Statement → Mathematical Model

```text
"at least one failure"
        ↓
direct counting has many cases
        ↓
count complement:
"no failures"
        ↓
answer = total - noFailure
```

### Codeforces Recognition

```text
"at least one"
"not all"
"everything except"
        ↓
TRY COMPLEMENT
```

### Minimal C++

```cpp
long long desired = total - bad;
```

### Common Traps / Edge Cases

The complement must cover exactly everything excluded from the desired set. Do not subtract unrelated or overlapping cases incorrectly.

> **Real-World Engineering Case:** For 10 independent binary health checks, counting configurations with at least one failed check is easier as `2^10 - 1`: all possible health states minus the single state where every service is healthy.

---

## 6.5 Stars and Bars — Distribute Identical Objects

### ASCII / Structural Visual

Distribute 5 identical objects into 3 boxes:

```text
* * * | * | *

      ↓

box1 = 3
box2 = 1
box3 = 1

(3,1,1)
```

We arrange:

```text
5 stars + 2 bars

******* positions conceptually

choose positions of 2 bars
from 7 total symbols
```

### Core Formula / Rule

For:

```text
x1+x2+...+xk = n
```

| Constraint | Number of Solutions |
|---|---|
| `xi >= 0` | `C(n+k-1, k-1)` |
| `xi >= 1` | `C(n-1, k-1)` |

### Short Derivation — Non-Negative

```text
n identical stars
k-1 separators

total symbols:
n+k-1

Choose separator positions:
C(n+k-1, k-1)
```

Positive case:

```text
xi >= 1

give 1 object to every box first

remaining:
n-k objects

now distribute non-negatively

C((n-k)+k-1, k-1)
= C(n-1,k-1)
```

### Visual Dry Run

```text
n=5 identical jobs
k=3 workers
xi >= 1

Give one job each:

[1] [1] [1]

used = 3
remaining = 2

Distribute remaining 2 freely:

C(2+3-1, 3-1)
= C(4,2)
= 6

Equivalent:
C(5-1,3-1)
= C(4,2)
= 6
```

### Statement → Mathematical Model

```text
"distribute n identical items
among k labeled groups"
        ↓
x1+x2+...+xk=n
        ↓
check:
xi >= 0 or xi >= 1
        ↓
stars and bars
```

### Codeforces Recognition

```text
identical objects
+
labeled boxes
+
sum fixed
        ↓
STARS AND BARS
```

### Minimal C++

```cpp
// xi >= 0: C(n+k-1, k-1)
// xi >= 1: C(n-1, k-1)
```

### Common Traps / Edge Cases

- The objects must be treated as identical and boxes as distinguishable for this standard formula.
- For `xi >= 1`, there are no solutions when `n < k`.
- Do not use stars and bars for arbitrary upper bounds without additional work.

> **Real-World Engineering Case:** If 20 identical batch jobs must be assigned by count to 4 labeled worker pools, the vector `(x1,x2,x3,x4)` is a stars-and-bars model. If every pool must receive at least one job, reserve one job per pool before counting the remaining distribution.

---

## 6.6 Inclusion-Exclusion

### ASCII / Structural Visual

```text
       A               B
   ┌────────┐      ┌────────┐
   │        ├──────┤        │
   │        │ A∩B  │        │
   └────────┴──────┴────────┘

|A| + |B|

counts A∩B TWICE

so subtract it once:

|A∪B| = |A| + |B| - |A∩B|
```

### Core Formula / Rule

```text
2 sets:

|A∪B|
= |A| + |B| - |A∩B|
```

For three sets:

```text
|A∪B∪C|
= singles
- pair intersections
+ triple intersection
```

| Stage | Sign |
|---|---|
| individual sets | `+` |
| pair intersections | `-` |
| triple intersection | `+` |

### Short Derivation

Adding `|A|+|B|` counts an element in `A∩B` twice. The union needs it once, so subtract the intersection once.

### Visual Dry Run — Numbers `1..10`

Count numbers divisible by `2` or `3`.

```text
A = divisible by 2:
2,4,6,8,10
|A| = 5

B = divisible by 3:
3,6,9
|B| = 3

A∩B:
divisible by 6
6
|A∩B| = 1

answer:
5 + 3 - 1
= 7
```

### Statement → Mathematical Model

```text
"divisible by 2 OR 3"
        ↓
A = multiples of 2
B = multiples of 3
        ↓
overlap = multiples of lcm(2,3)
        ↓
|A∪B| = |A|+|B|-|A∩B|
```

### Codeforces Recognition

```text
"A or B"
+
"some objects satisfy both"
        ↓
INCLUSION-EXCLUSION
```

### Minimal C++

```cpp
long long ans = cntA + cntB - cntBoth;
```

### Common Traps / Edge Cases

The intersection is usually where mistakes occur. For divisibility by `a` and `b`, the overlap is divisible by `lcm(a,b)`, not necessarily `a*b`.

> **Real-World Engineering Case:** To count users who enabled email or SMS notifications, adding both subscriber counts double-counts users who enabled both. Inclusion-exclusion subtracts that overlap once to obtain unique users.

---

## 6.7 Pigeonhole and Contribution

This section contains two closely related **count-without-enumerating** techniques.

### ASCII / Structural Visual — Pigeonhole

```text
4 objects → 3 boxes

objects:
● ● ● ●

boxes:
[ ] [ ] [ ]

No matter how placed:

[●●] [●] [●]

at least one box
contains >= 2 objects
```

### Core Formula / Rule — Pigeonhole

```text
n+1 objects
placed into n boxes
        ↓
some box contains at least 2
```

More generally:

```text
N objects into K boxes
        ↓
some box has at least ceil(N/K)
```

### Short Derivation

If every box held at most one object, `n` boxes could hold at most `n` objects. But there are `n+1`, contradiction.

### Visual Dry Run — Remainders

```text
6 integers
remainder modulo 5

possible remainder boxes:
0 1 2 3 4
→ only 5 boxes

6 values → 5 boxes

Therefore:
two values have same remainder
```

### Statement → Mathematical Model — Pigeonhole

```text
"more objects than categories"
        ↓
objects = N
categories = K
        ↓
N > K
        ↓
some category repeats
```

### ASCII / Structural Visual — Contribution

For array length `n`, element at 1-indexed position `i`:

```text
A1 A2 ... Ai ... An
           ↑

choose subarray LEFT endpoint:
1..i
→ i choices

choose RIGHT endpoint:
i..n
→ n-i+1 choices

subarrays containing Ai:

i × (n-i+1)
```

### Core Formula / Rule — Contribution

```text
element Ai appears in:

i(n-i+1)

subarrays

Therefore:

sum of all subarray sums
=
Σ Ai × i × (n-i+1)
```

### Short Derivation

For `Ai` to be inside `[L,R]`:

```text
L <= i <= R

L has i choices
R has n-i+1 choices

independent choices:

i × (n-i+1)
```

### Visual Dry Run

```text
A = [1,2,3]
n = 3

i=1:
contribution count = 1×3 = 3
value contribution = 1×3 = 3

i=2:
count = 2×2 = 4
contribution = 2×4 = 8

i=3:
count = 3×1 = 3
contribution = 3×3 = 9

total = 3+8+9 = 20
```

Direct verification:

```text
subarrays:
[1]       sum 1
[2]       sum 2
[3]       sum 3
[1,2]     sum 3
[2,3]     sum 5
[1,2,3]   sum 6
                  ──
                  20 ✓
```

### Statement → Mathematical Model — Contribution

```text
"sum something over ALL subarrays"
        ↓
enumerating subarrays looks O(n²)
        ↓
reverse the viewpoint:
for each element,
how many structures contain it?
        ↓
value × contribution count
```

### Codeforces Recognition

```text
more objects than states/categories → PIGEONHOLE

"sum over all pairs/subarrays"
        ↓
ask:
"how many times does each element contribute?"
        ↓
CONTRIBUTION TECHNIQUE
```

### Minimal C++

```cpp
ans += 1LL * a[i] * (i + 1) * (n - i); // 0-indexed i
```

### Common Traps / Edge Cases

- Be consistent about 0-indexed versus 1-indexed formulas.
- Contribution totals grow quickly; use `long long`.
- Pigeonhole proves existence but does not automatically tell you which object pair unless you also track representatives.
- The source also notes multiset permutations `n!/(c1!c2!...)` and modular combinations via precomputed factorials/inverse factorials.

> **Real-World Engineering Case:** In telemetry, if 1,001 requests are assigned to only 1,000 hash buckets, some bucket must receive at least two requests—pigeonhole. Separately, when estimating the total effect of every time-window query, contribution counting asks how many windows contain each sample instead of enumerating every window.

---

## Section Summary

| Statement Clue | Pattern | Formula / Model |
|---|---|---|
| separate cases | addition rule | `A+B` |
| sequential choices | multiplication rule | `A*B` |
| arrange all | factorial | `n!` |
| ordered selection | permutation | `P(n,k)` |
| unordered selection | combination | `C(n,k)` |
| unordered pairs | choose 2 | `n(n-1)/2` |
| equal-value pairs | frequency pairs | `f(f-1)/2` |
| at least one | complement | `total-bad` |
| identical objects into boxes | stars and bars | `C(n+k-1,k-1)` |
| overlapping sets | inclusion-exclusion | add singles, subtract overlaps |
| more objects than boxes | pigeonhole | repetition guaranteed |
| all subarrays/pairs | contribution | count appearances per element |

## 30-Second Revision

```text
┌──────────────────────────────────────────────────────────────┐
│        COUNTING & COMBINATORICS — 30 SECOND REVISION         │
├──────────────────────────────────────────────────────────────┤
│ separate OR cases          → ADD                             │
│ sequential choices         → MULTIPLY                        │
│ order matters              → PERMUTATION                     │
│ order doesn't matter       → COMBINATION                     │
│ unordered pairs            → n(n-1)/2                        │
│ equal pairs frequency f    → f(f-1)/2                        │
│ "at least one"             → COMPLEMENT                      │
│ identical items / boxes    → STARS AND BARS                  │
│ overlapping cases          → INCLUSION-EXCLUSION             │
│ more objects than boxes    → PIGEONHOLE                      │
│ all subarrays / pairs      → CONTRIBUTION                    │
└──────────────────────────────────────────────────────────────┘
```
