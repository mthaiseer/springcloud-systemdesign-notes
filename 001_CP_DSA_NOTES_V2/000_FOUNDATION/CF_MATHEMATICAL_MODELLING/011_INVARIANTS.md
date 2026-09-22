# Part 10 — Invariants

> **Goal:** recognize properties that remain unchanged while legal operations transform a state.
>
> **Contest workflow:** `State → Operation → Compute Delta → Find Preserved Quantity → Compare Start/Target → Proof`
>
> **Core question:** **What changes, and what cannot change?**

## Table of Contents

- [10.0 Invariant Mental Model](#100-invariant-mental-model)
- [10.1 Operation-Delta Analysis](#101-operation-delta-analysis)
- [10.2 Sum Invariant](#102-sum-invariant)
- [10.3 Sum Modulo k Invariant](#103-sum-modulo-k-invariant)
- [10.4 Parity Invariant](#104-parity-invariant)
- [10.5 Difference Invariant](#105-difference-invariant)
- [10.6 GCD Invariant](#106-gcd-invariant)
- [10.7 XOR Invariant](#107-xor-invariant)
- [10.8 Frequency and Multiset Invariants](#108-frequency-and-multiset-invariants)
- [10.9 Coloring Invariants](#109-coloring-invariants)
- [10.10 Reachability with Invariants](#1010-reachability-with-invariants)
- [10.11 Invariant vs Monovariant](#1011-invariant-vs-monovariant)
- [10.12 Combining Invariants](#1012-combining-invariants)
- [10.13 How to Discover an Invariant](#1013-how-to-discover-an-invariant)
- [10.14 Codeforces Recognition Map](#1014-codeforces-recognition-map)
- [10.15 Common Mistakes](#1015-common-mistakes)
- [10.16 Fast Revision Card](#1016-fast-revision-card)

---

## 10.0 Invariant Mental Model

### Plain-English intuition

An **invariant** is a property unchanged after every legal operation.

```text
STATE 0 --op--> STATE 1 --op--> STATE 2 --op--> STATE 3

I(S0) = I(S1) = I(S2) = I(S3)
```

### Daily-life scenario — moving water

```text
Bottle A = 3 L
Bottle B = 7 L
total    = 10 L
```

Move `2 L` from B to A:

```text
A' = A+2 = 5
B' = B-2 = 5

A'+B'
= (A+2)+(B-2)
= A+B
= 10
```

The individual amounts change; total water does not.

### Mathematical model

```text
operation:
A' = A+x
B' = B-x

I(A,B)=A+B

I(A',B')
=A+x+B-x
=A+B
```

**Memory hook:** do not ask only *what changes?* Ask *what survives every change?*

---

## 10.1 Operation-Delta Analysis

The most reusable discovery technique is:

```text
Delta F = F(after)-F(before)
```

If:

```text
Delta F = 0
```

for every legal operation, `F` is invariant.

If:

```text
Delta F ≡ 0 (mod k)
```

then `F mod k` is invariant.

### Daily-life scenario — bank transfer

Transfer `x` lei from A to B:

```text
A'=A-x
B'=B+x
```

Check total:

```text
Delta(A+B)
=(A-x+B+x)-(A+B)
=0
```

### Contest procedure

```text
1. Write ONE operation algebraically.
2. Pick a candidate: sum/parity/gcd/xor/...
3. Compute it before.
4. Compute it after.
5. Subtract.
6. Simplify Delta.
7. Delta=0 -> invariant.
```

```text
Operation
   |
   v
Before / After equations
   |
   v
Compute Delta
   |
   +-- 0 --------> invariant
   +-- 0 mod k --> modulo invariant
   +-- fixed sign -> monovariant candidate
```

---

## 10.2 Sum Invariant

Operation:

```text
ai += x
aj -= x
```

Let:

```text
S=a1+a2+...+an
```

Then:

```text
S'=S+x-x=S
```

### Daily-life scenario — moving coins between boxes

```text
start = [5,8,2]
sum   = 15
```

Move `3` coins from box 2 to box 3:

```text
[5,8,2] -> [5,5,5]

sum=15
```

Target:

```text
[5,4,5]
sum=14
```

If operations only transfer coins:

```text
15 != 14
=> impossible
```

### Critical logic

```text
different invariant -> definitely impossible
same invariant      -> maybe reachable; more proof may be needed
```

---

## 10.3 Sum Modulo k Invariant

If every operation changes a quantity by a multiple of `k`:

```text
S'=S+q*k
```

then:

```text
S' mod k = S mod k
```

### Daily-life scenario — stock arrives in packs of 6

Start:

```text
S=20
```

Allowed changes:

```text
+6 or -6
```

Possible values include:

```text
20,26,32,14,8,...
```

All have:

```text
S mod 6 = 2
```

Target `31`:

```text
31 mod 6 = 1
```

Therefore unreachable.

### CF trigger

```text
add/subtract k
add/subtract multiples of k
reach target after repeated operations
```

Test residues immediately.

---

## 10.4 Parity Invariant

Parity is modulo `2`.

If:

```text
Delta F is always even
```

then:

```text
F mod 2
```

is invariant.

### Daily-life scenario — people enter in pairs

Start with `7` people. Only `+2` or `-2` is allowed.

```text
7 -> 9 -> 11 -> 9 -> 7 -> 5
```

Every reachable count is odd.

Target `8` is even, so it is impossible.

### Proof

```text
N'=N+2q

N' mod 2
=(N+2q) mod 2
=N mod 2
```

### CF recognition

Try parity when operations involve pairs, `±2`, two flips, checkerboards, or even-distance changes.

---

## 10.5 Difference Invariant

If both quantities receive the same change:

```text
A'=A+x
B'=B+x
```

then:

```text
A'-B'
=(A+x)-(B+x)
=A-B
```

### Daily-life scenario — age gap

```text
Alice=30
Bob=24
gap=6
```

Ten years later:

```text
Alice=40
Bob=34
gap=6
```

### Reachability example

```text
start  = (10,4)  -> difference=6
target = (20,15) -> difference=5
```

If only equal additions are allowed, target is impossible.

**Memory hook:** equal movement cancels under subtraction.

---

## 10.6 GCD Invariant

Classic identity:

```text
gcd(a,b)=gcd(a,b-q*a)
```

for integer `q`.

### Proof idea

If `d` divides `a` and `b`, then it divides:

```text
b-q*a
```

Conversely, if `d` divides `a` and `b-q*a`, it also divides:

```text
(b-q*a)+q*a=b
```

So the common divisors are identical.

### Daily-life scenario — tile size

Lengths:

```text
18 cm and 30 cm
```

Largest common tile:

```text
gcd(18,30)=6
```

Replace the longer length by its difference:

```text
30-18=12

gcd(18,12)=6
```

Again:

```text
18-12=6
gcd(6,12)=6
```

Numbers change; GCD survives.

### CF trigger

```text
replace b with b-a
add/subtract a multiple of another value
Euclidean-style operations
```

---

## 10.7 XOR Invariant

Core identities:

```text
x XOR x = 0
x XOR 0 = x
```

If the same mask `v` is XORed into two positions:

```text
ai'=ai XOR v
aj'=aj XOR v
```

then global XOR:

```text
X'
=X XOR v XOR v
=X
```

### Numerical dry run

```text
a=[3,5]
X=3 XOR 5=6

v=7

a'=[3 XOR 7,5 XOR 7]
  =[4,2]

X'=4 XOR 2=6
```

### Intuition — paired light toggles

Think of each bit as an ON/OFF lamp. Applying the same toggle mask twice cancels in the combined XOR signature.

### CF trigger

```text
toggle
flip bits
paired XOR operation
duplicate cancellation
```

---

## 10.8 Frequency and Multiset Invariants

If operations only rearrange elements, frequencies remain unchanged.

### Daily-life scenario — colored balls

Before:

```text
R B R G B R

R=3, B=2, G=1
```

Rearrange:

```text
G R B R R B

R=3, B=2, G=1
```

### Swap example

```text
before=[4,1,4,2]
after =[2,1,4,4]
```

Preserved:

```text
frequency
multiset
sorted array
sum
xor
```

Not necessarily preserved:

```text
positions
adjacency
inversion count
```

### Canonical representation

If only rearrangement matters:

```text
sort(A)==sort(B)
```

tests whether the multisets match.

---

## 10.9 Coloring Invariants

Coloring maps a large state space to a few classes.

For a grid:

```text
color(r,c)=(r+c) mod 2
```

### Daily-life analogy — alternating street tiles

Imagine tiles alternating black/white. A move changing `r+c` by an even number keeps the same color.

Example:

```text
start=(2,3)
r+c=5 -> odd

move (+1,+1)

target=(3,4)
r+c=7 -> odd

Delta(r+c)=2
```

Color is preserved.

### CF recognition

For grid reachability try:

```text
(x+y)%2
(x-y)%2
coordinate parity
checkerboard coloring
```

---

## 10.10 Reachability with Invariants

If invariant `I` is preserved:

```text
I(start) != I(target)
```

implies:

```text
target is impossible
```

### Standard proof template

```text
1. Define I(state).
2. Prove every legal operation preserves I.
3. Compute I(start).
4. Compute I(target).
5. They differ.
6. Therefore target is unreachable.
```

### Daily-life scenario — pair-only elevator

Passenger count changes only by `±2`.

```text
start=5 -> odd
target=8 -> even
```

Parity is invariant, so no legal sequence reaches 8.

### Important

```text
I(start)!=I(target) -> impossible

I(start)==I(target) -> NOT automatically possible
```

The second direction may require a construction or another invariant.

---

## 10.11 Invariant vs Monovariant

### Invariant

Never changes:

```text
F(next)=F(current)
```

Example:

```text
combined money during internal transfers
```

### Monovariant

Moves in one direction:

```text
F(next)<F(current)
```

or:

```text
F(next)>F(current)
```

Example:

```text
delete one item repeatedly

size:
10 -> 9 -> 8 -> 7
```

### Use

```text
Invariant  -> reachability / impossibility
Monovariant -> progress / termination / operation bounds
```

### Visual

```text
INVARIANT:
7 -> 7 -> 7 -> 7

MONOVARIANT:
7 -> 6 -> 4 -> 3
```

---

## 10.12 Combining Invariants

One invariant may be too weak.

A target may need to satisfy:

```text
I1(target)=I1(start)
AND
I2(target)=I2(start)
AND
...
```

### Daily-life analogy — airport baggage

A bag may need both:

```text
correct owner tag
correct flight tag
```

Matching only one does not establish identity.

### Common combinations

```text
sum + parity
sum mod k + frequency
gcd + parity
xor + count
color + distance
```

### CP lesson

If many obviously different states share your first invariant, search for another independent preserved property.

---

## 10.13 How to Discover an Invariant

### Method 1 — algebraize one operation

Story:

```text
"move x coins from box i to box j"
```

Math:

```text
ai'=ai-x
aj'=aj+x
```

Test sum:

```text
Delta S=-x+x=0
```

### Method 2 — try the standard candidates

```text
sum
sum mod k
parity
difference
gcd
xor
frequency
multiset
coordinate color
```

### Method 3 — tiny-state table

Manually generate reachable states:

```text
state    sum  parity  gcd  xor
S0        12     0     2    4
S1        12     0     2    6
S2        12     0     2    1
```

Candidates `sum`, parity and gcd deserve proof; XOR clearly fails.

### 60-second discovery flow

```text
Read operation
     |
     v
What exactly changes?
     |
     v
Write before/after equations
     |
     v
Try common candidates
     |
     v
Compute Delta
     |
     +-- 0 -------> invariant
     +-- 0 mod k -> modulo invariant
     +-- one sign -> monovariant
```

**Never stop at examples. Prove the candidate for every legal operation.**

---

## 10.14 Codeforces Recognition Map

| Statement clue | First candidate |
|---|---|
| move value between positions | sum |
| add/subtract multiples of `k` | modulo `k` |
| operations happen in pairs / by 2 | parity |
| add same amount to both | difference |
| subtract one value from another | GCD |
| paired toggles | XOR |
| only swap/rearrange | multiset/frequency |
| diagonal/even-step grid movement | coloring/parity |
| transform A into B? | reachability invariant |
| prove process terminates | monovariant |

### 10-second contest questions

```text
1. What changes in ONE operation?
2. Do additions and removals cancel?
3. Is every Delta divisible by k?
4. Does parity survive?
5. Does difference survive?
6. Does GCD survive?
7. Does XOR cancel?
8. Are values only rearranged?
9. Can states be colored into classes?
10. Is something only increasing/decreasing?
```

---

## 10.15 Common Mistakes

### Mistake 1 — guessing from examples

Three examples preserving the sum are not a proof.

Required:

```text
Delta sum=0
```

for every legal operation.

### Mistake 2 — treating invariant equality as sufficient

Wrong:

```text
same parity -> reachable
```

Correct:

```text
different parity -> impossible
same parity      -> investigate further
```

### Mistake 3 — rejecting a quantity too early

Exact sum may change while:

```text
sum mod k
```

remains fixed.

### Mistake 4 — confusing invariant and monovariant

```text
always decreases
```

means monovariant, not invariant.

### Mistake 5 — checking only one legal operation

If the statement provides several operation types, the claimed invariant must survive **all** of them.

---

## 10.16 Fast Revision Card

```text
========================================================
PART 10 — INVARIANTS
========================================================

DEFINITION
I(after)=I(before)
for every legal operation

DISCOVERY
Delta I=I(after)-I(before)

Delta I=0
-> invariant

Delta I % k=0
-> I mod k invariant

SUM
(ai,aj)->(ai+x,aj-x)
sum unchanged

PARITY
Delta even
-> value mod 2 unchanged

MODULO
Delta=q*k
-> value mod k unchanged

DIFFERENCE
(A,B)->(A+x,B+x)
-> A-B unchanged

GCD
gcd(a,b)=gcd(a,b-q*a)

XOR
v XOR v=0
-> paired XOR cancels

MULTISET
swaps/reordering preserve frequencies

COLORING
(r+c)%2 often separates grid states

REACHABILITY
I(start)!=I(target)
-> impossible

WARNING
I(start)==I(target)
does NOT prove reachable

INVARIANT
constant

MONOVARIANT
only increases/decreases

CORE QUESTION
"What does this operation make impossible to change?"
========================================================
```
