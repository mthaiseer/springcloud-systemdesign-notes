# Part 11 — Operation Modeling

> **Goal:** convert a Codeforces operation written in story language into an exact mathematical state transition.
>
> **Contest workflow:** `Story → State Variables → One Legal Operation → Delta / Transition → Repeat t Times → Constraints → Invariant / Reachability → Algorithm`
>
> **Core question:** **What exactly does ONE operation do mathematically?**

## Table of Contents

- [11.0 Operation Modeling Mental Model](#110-operation-modeling-mental-model)
- [11.1 Story Operation to State Transition](#111-story-operation-to-state-transition)
- [11.2 Add and Subtract Operations](#112-add-and-subtract-operations)
- [11.3 Transfer Operations](#113-transfer-operations)
- [11.4 Repeated Operations and Closed Forms](#114-repeated-operations-and-closed-forms)
- [11.5 Exact-k Operations](#115-exact-k-operations)
- [11.6 At-Most-k and At-Least-k Operations](#116-at-most-k-and-at-least-k-operations)
- [11.7 Replace and Assignment Operations](#117-replace-and-assignment-operations)
- [11.8 Swap and Permutation Operations](#118-swap-and-permutation-operations)
- [11.9 Flip and Toggle Operations](#119-flip-and-toggle-operations)
- [11.10 Increment-Decrement Pair Modeling](#1110-increment-decrement-pair-modeling)
- [11.11 Operation Cost and Minimum Moves](#1111-operation-cost-and-minimum-moves)
- [11.12 Reverse Modeling from Target to Start](#1112-reverse-modeling-from-target-to-start)
- [11.13 Reachability from Operation Equations](#1113-reachability-from-operation-equations)
- [11.14 Operation Modeling with Arrays](#1114-operation-modeling-with-arrays)
- [11.15 Range Operations and Difference Modeling](#1115-range-operations-and-difference-modeling)
- [11.16 Greedy Choice from Operation Effect](#1116-greedy-choice-from-operation-effect)
- [11.17 Operation Modeling vs Simulation](#1117-operation-modeling-vs-simulation)
- [11.18 How to Decode a CF Operation in 60 Seconds](#1118-how-to-decode-a-cf-operation-in-60-seconds)
- [11.19 Codeforces Recognition Map](#1119-codeforces-recognition-map)
- [11.20 Common Mistakes](#1120-common-mistakes)
- [11.21 Fast Revision Card](#1121-fast-revision-card)

---

## 11.0 Operation Modeling Mental Model

An operation is a **state transition**.

```text
current state
     |
     | one legal operation
     v
next state
```

Instead of remembering the story:

```text
"give x coins from Alice to Bob"
```

write:

```text
A' = A-x
B' = B+x
```

### Daily-life scenario — bank transfer

Before:

```text
Alice = 100
Bob   = 40
```

Transfer `25` from Alice to Bob:

```text
A' = 100-25 = 75
B' = 40+25  = 65
```

Mathematical operation:

```text
(A,B) -> (A-x,B+x)
```

Now properties become visible:

```text
A'+B'
=A-x+B+x
=A+B
```

So the operation also exposes a sum invariant.

### Core habit

```text
DO NOT:
story -> immediately code simulation

DO:
story
 -> variables
 -> exact one-step equation
 -> mathematical consequences
 -> algorithm
```

---

## 11.1 Story Operation to State Transition

### Translation template

For every operation identify:

```text
1. Which variables change?
2. By how much?
3. Which variables do not change?
4. What conditions allow the operation?
5. Is the choice fixed or selectable?
6. Can the operation be repeated?
```

### Example — move one token

Story:

```text
Move one token from pile A to pile B.
```

Variables:

```text
a = tokens in A
b = tokens in B
```

Precondition:

```text
a >= 1
```

Transition:

```text
a' = a-1
b' = b+1
```

Vector form:

```text
(a,b) -> (a,b)+(-1,+1)
```

### Delta vector

```text
Delta = (-1,+1)
```

This compact representation is extremely useful:

```text
state' = state + Delta
```

---

## 11.2 Add and Subtract Operations

### Standard model

If one move adds `d`:

```text
x' = x+d
```

If one move subtracts `d`:

```text
x' = x-d
```

### Daily-life scenario — elevator floors

Elevator moves exactly `3` floors upward per operation.

Start:

```text
x=4
```

One operation:

```text
x'=4+3=7
```

Two:

```text
10
```

After `t` operations:

```text
x_t = x_0 + 3t
```

Target `19`:

```text
4+3t=19
3t=15
t=5
```

Target `20`:

```text
4+3t=20
3t=16
```

No integer `t`, so it is unreachable using only `+3`.

### Recognition

```text
increase by d
decrease by d
move fixed distance
gain/lose fixed amount
```

Translate immediately to `x' = x ± d`.

---

## 11.3 Transfer Operations

Transfer `x` units from `i` to `j`:

```text
ai' = ai-x
aj' = aj+x
```

Everything else stays unchanged.

### Daily-life scenario — water tanks

```text
Tank A=8 L
Tank B=3 L
```

Transfer `2 L`:

```text
A'=8-2=6
B'=3+2=5
```

### Operation vector

```text
(A,B) -> (A,B)+(-x,+x)
```

### Mathematical consequence

```text
Delta(A+B)=-x+x=0
```

So operation modeling naturally reveals invariants.

### Common CF form

```text
choose i,j
decrease ai
increase aj
```

Before thinking greedy or simulation, write the delta vector.

---

## 11.4 Repeated Operations and Closed Forms

Simulation:

```text
x0
x1=x0+d
x2=x0+2d
x3=x0+3d
...
```

Closed form:

```text
x_t=x_0+t*d
```

### Derivation

```text
x1=x0+d
x2=x1+d=x0+2d
x3=x2+d=x0+3d

therefore
xt=x0+t*d
```

### Daily-life scenario — monthly savings

Initial savings:

```text
1000
```

Add `250` every month.

After `t` months:

```text
S_t=1000+250t
```

After 12 months:

```text
1000+250*12
=4000
```

### Why this matters in CF

A problem may allow `10^18` operations. Never simulate if the repeated effect has a closed form.

```text
repetition -> multiplication
```

---

## 11.5 Exact-k Operations

"Perform exactly `k` operations" is stronger than "at most `k`."

If each operation adds either `+1` or `-1`, let:

```text
p = number of +1 operations
m = number of -1 operations
```

Exactly `k` operations means:

```text
p+m=k
```

Net change:

```text
p-m = target-start
```

Solve:

```text
p = (k + target-start)/2
m = (k - target+start)/2
```

Both must be non-negative integers.

### Daily-life scenario — walking exactly 7 steps

Start at `0`, each step is `+1` or `-1`.

Can you finish at `3` after exactly `7` steps?

```text
p+m=7
p-m=3
```

Add:

```text
2p=10
p=5
m=2
```

Yes.

Can you finish at `4`?

```text
p+m=7
p-m=4

2p=11
```

Not integer -> impossible.

### Derived conditions

For distance:

```text
D=|target-start|
```

Reachability requires:

```text
D <= k
(k-D) is even
```

The second condition represents wasting extra moves in canceling pairs.

---

## 11.6 At-Most-k and At-Least-k Operations

### At most k

If one move changes position by at most one toward the target:

```text
minimum moves = |target-start|
```

Reachable within `k` if:

```text
|target-start| <= k
```

### At least k

"At least" often allows extra operations, but only if harmless/canceling moves exist.

### Daily-life scenario — delivery attempts

Need `6` successful unit-progress operations.

If allowed at most `8`:

```text
6 <= 8
```

possible.

If required exactly `8`, ask whether the extra `2` operations can cancel or be wasted legally.

### Contest lesson

Never erase quantifiers:

```text
exactly k
at most k
at least k
```

They create different mathematics.

---

## 11.7 Replace and Assignment Operations

Not every operation is additive.

### Assignment

```text
x := y
```

means:

```text
x' = y
```

not:

```text
x' = x+y
```

### Replace by function

```text
x' = f(x)
```

Examples:

```text
x' = floor(x/2)
x' = x mod m
x' = gcd(x,y)
x' = min(x,c)
```

### Daily-life scenario — discount cap

Rule:

```text
price := min(price,100)
```

If:

```text
price=140
```

then:

```text
price'=100
```

Applying again:

```text
min(100,100)=100
```

This operation is **idempotent**:

```text
f(f(x))=f(x)
```

### CF insight

For replacement operations, test whether the function is:

```text
idempotent
monotone
shrinking
periodic
```

This can eliminate unnecessary simulation.

---

## 11.8 Swap and Permutation Operations

Swap positions `i,j`:

```text
ai' = aj
aj' = ai
```

### Daily-life scenario — people changing seats

```text
before:
seat 1 = Alice
seat 2 = Bob

swap

after:
seat 1 = Bob
seat 2 = Alice
```

Changed:

```text
positions
```

Preserved:

```text
multiset of people
```

### Adjacent swaps

If only adjacent swaps are allowed:

```text
... a[i],a[i+1] ...
        |
        v
... a[i+1],a[i] ...
```

One adjacent swap changes inversion parity.

### CF modeling question

Do not merely write "swap." Ask:

```text
any two positions?
adjacent only?
same parity positions?
limited number of swaps?
```

The operation domain matters as much as the update.

---

## 11.9 Flip and Toggle Operations

Binary flip:

```text
0 -> 1
1 -> 0
```

Algebraic forms:

```text
b' = 1-b
```

or:

```text
b' = b XOR 1
```

### Repeated flips

```text
1 flip  -> opposite
2 flips -> original
3 flips -> opposite
```

Therefore:

```text
after k flips:
b' = b XOR (k mod 2)
```

### Daily-life scenario — light switch

Initial:

```text
OFF = 0
```

Press switch `5` times:

```text
5 mod 2=1
```

Final:

```text
ON
```

Press `100` times:

```text
100 mod 2=0
```

Final equals initial.

### Recognition

Repeated toggle problems almost always invite parity.

---

## 11.10 Increment-Decement Pair Modeling

A frequent operation:

```text
choose i,j
ai += 1
aj -= 1
```

Vector:

```text
Delta_i=+1
Delta_j=-1
```

### Immediate consequences

```text
sum delta=0
```

So total sum is preserved.

If transforming array `A` to `B`, define:

```text
needIncrease = sum max(0, Bi-Ai)
needDecrease = sum max(0, Ai-Bi)
```

A necessary condition:

```text
needIncrease = needDecrease
```

When arbitrary transfer between positions is allowed, this is also sufficient.

### Daily-life scenario — redistribute inventory

Start:

```text
A=[5,1,4]
```

Target:

```text
B=[2,4,4]
```

Deficits:

```text
index 2 needs +3
```

Surplus:

```text
index 1 has 3 extra
```

Thus:

```text
needIncrease=3
needDecrease=3
```

Three unit transfers solve it.

---

## 11.11 Operation Cost and Minimum Moves

If one operation can reduce a non-negative gap by at most `d`, then:

```text
moves >= ceil(gap/d)
```

If that maximum progress can always be achieved:

```text
minimum moves = ceil(gap/d)
```

Integer form:

```text
(gap+d-1)/d
```

### Daily-life scenario — carrying boxes

Need to move `23` boxes. One trip carries at most `5`.

Lower bound:

```text
5*moves >= 23
moves >= 23/5
moves >= ceil(4.6)
moves >= 5
```

Construction:

```text
5+5+5+5+3 = 23
```

Therefore:

```text
minimum trips=5
```

### Proof pattern

```text
1. Lower bound: no operation gives >d progress.
2. Construction: show ceil(gap/d) operations suffice.
3. Lower bound == construction -> optimal.
```

---

## 11.12 Reverse Modeling from Target to Start

Sometimes forward operations branch heavily while reverse operations are nearly forced.

### Example operation

Forward:

```text
x -> x+1
or
x -> 2x
```

Going from small `x` to large target can branch.

Reverse target `y`:

```text
if y is odd:
    previous must be y-1

if y is even:
    previous might be y/2
```

### Daily-life analogy — undoing document edits

If forward actions create many possible states, an "undo" view may reveal exactly what the previous state must have been.

### Contest heuristic

Ask:

```text
Is the inverse operation simpler?
Does the target expose the last operation?
```

This often turns simulation/search into greedy mathematics.

---

## 11.13 Reachability from Operation Equations

Suppose one operation changes:

```text
x -> x+d
```

After `t` operations:

```text
target = start+t*d
```

Thus:

```text
target-start=t*d
```

Reachability requires:

```text
(target-start) divisible by d
```

with a legal `t`.

### Daily-life scenario — bus stops every 4 km

Start at kilometer `3`.

Reachable stops:

```text
3,7,11,15,19,...
```

Target `19`:

```text
19-3=16
16 mod 4=0
```

reachable.

Target `18`:

```text
18-3=15
15 mod 4!=0
```

not reachable.

### General idea

Operation equations create arithmetic constraints:

```text
divisibility
parity
bounds
non-negativity
invariants
```

Solve those before considering brute force.

---

## 11.14 Operation Modeling with Arrays

An array operation usually affects only a few coordinates.

Example:

```text
choose i
a[i] += d
```

Vector form:

```text
A' = A + d*e_i
```

where `e_i` is `1` at index `i`, `0` elsewhere.

### Two-coordinate operation

```text
ai += x
aj -= x
```

Delta vector:

```text
[0 ... +x ... -x ... 0]
```

### Daily-life scenario — warehouse stock

Warehouses:

```text
[10,4,8,2]
```

Move `3` units from warehouse 1 to warehouse 4:

```text
[-3,0,0,+3]
```

New state:

```text
[7,4,8,5]
```

### Why vector thinking helps

You can inspect:

```text
sum of delta
xor effect
which coordinates change
whether operations commute
```

instead of tracing the whole story.

---

## 11.15 Range Operations and Difference Modeling

Operation:

```text
add x to every a[L..R]
```

Directly touching all elements costs `O(R-L+1)`.

Use a difference array:

```text
diff[L]   += x
diff[R+1] -= x
```

Then reconstruct by prefix sum.

### Why this models the operation

Adding `x` at `L` means:

```text
effect starts here
```

Subtracting `x` at `R+1` means:

```text
effect stops here
```

### Daily-life scenario — salary bonus interval

Employees `2..5` receive `+100`.

```text
diff[2] += 100
diff[6] -= 100
```

Prefix accumulation produces:

```text
0, +100,+100,+100,+100, 0
```

### Mathematical transformation

```text
range update
     |
     v
two boundary events
     |
     v
prefix reconstruction
```

This is operation modeling rather than repeated element simulation.

---

## 11.16 Greedy Choice from Operation Effect

Sometimes modeling one operation reveals the only useful greedy move.

### Example

Goal:

```text
reduce x to 0
```

Allowed:

```text
subtract any value <= d
```

To minimize operations, each operation should make maximum legal progress:

```text
subtract d whenever possible
```

Result:

```text
ceil(x/d)
```

### Daily-life scenario — elevator capacity

Move `23` people, capacity `5`.

Taking fewer than `5` while at least `5` remain cannot reduce the number of trips.

So:

```text
5,5,5,5,3
```

is optimal.

### Proof language

```text
Any operation gives <= d progress.
Therefore at least ceil(x/d) operations are necessary.
Taking d whenever possible attains that bound.
```

---

## 11.17 Operation Modeling vs Simulation

### Simulation

Execute every operation:

```text
state0 -> state1 -> state2 -> ... -> stateT
```

Cost:

```text
O(T)
```

### Mathematical modeling

Derive:

```text
stateT = f(state0,T)
```

Potential cost:

```text
O(1)
```

### Example

```text
x=7
repeat 10^18 times:
    x += 3
```

Simulation is impossible.

Model:

```text
x_T=7+3*10^18
```

### When simulation is still appropriate

Use it when:

```text
T is small
state transition depends heavily on current state
no useful closed form exists
you are validating a mathematical conjecture
```

### Contest habit

Before writing a loop, ask:

```text
Can I model t operations directly?
```

---

## 11.18 How to Decode a CF Operation in 60 Seconds

### Step 1 — delete story nouns

```text
Alice gives Bob x coins
```

becomes:

```text
A-=x
B+=x
```

### Step 2 — write preconditions

```text
A>=x
```

### Step 3 — write delta

```text
Delta=(-x,+x)
```

### Step 4 — test preserved quantities

```text
Delta sum=0
```

### Step 5 — model repetition

If repeated `t` times with fixed `x`:

```text
A_t=A_0-tx
B_t=B_0+tx
```

### Step 6 — impose target

```text
A_t=targetA
B_t=targetB
```

### Step 7 — solve constraints

Check:

```text
integer t?
t>=0?
enough resources?
parity/divisibility?
operation count bound?
```

### Visual checklist

```text
STORY
  |
  v
VARIABLES
  |
  v
ONE OPERATION
  |
  v
DELTA / TRANSITION
  |
  +--> invariant?
  +--> repeated formula?
  +--> min moves?
  +--> reachability?
  |
  v
ALGORITHM
```

---

## 11.19 Codeforces Recognition Map

| Statement wording | Mathematical model |
|---|---|
| increase by `d` | `x'=x+d` |
| decrease by `d` | `x'=x-d` |
| move `x` from `i` to `j` | `ai-=x, aj+=x` |
| repeat `t` times | derive `state_t` |
| exactly `k` moves | equation + parity/count constraints |
| at most `k` moves | `minimumMoves<=k` |
| flip/toggle | XOR / parity |
| replace `x` by ... | `x'=f(x)` |
| swap | permutation transition |
| add to range | boundary events / difference array |
| transform A into B | delta + invariant + reachability |
| minimum operations | per-operation progress bound |
| huge number of repetitions | closed form / cycle / invariant |
| forward branches heavily | try reverse modeling |

### Rapid questions

```text
1. What are the state variables?
2. What exactly changes in ONE move?
3. What is the delta?
4. What is the precondition?
5. What remains unchanged?
6. What happens after t moves?
7. Is t integer and non-negative?
8. Exactly / at most / at least?
9. Can operations cancel?
10. Can I avoid simulation?
```

---

## 11.20 Common Mistakes

### 1. Coding before defining the operation

Story ambiguity creates implementation bugs.

Write:

```text
before -> after
```

first.

### 2. Ignoring preconditions

Operation:

```text
A-=x
```

may require:

```text
A>=x
```

An algebraically valid transition may be legally impossible.

### 3. Confusing exact and maximum operation counts

```text
minimum moves <= k
```

proves "at most k," not necessarily "exactly k."

### 4. Forgetting integer constraints

Equation:

```text
t=(target-start)/d
```

requires integer `t`.

### 5. Simulating huge repetitions

If the same delta repeats:

```text
Delta repeated t times = t*Delta
```

derive a closed form.

### 6. Assuming operations commute

`f(g(x))` may differ from `g(f(x))`.

Check operation order before reordering operations.

### 7. Proving only a lower bound

For minimum moves:

```text
lower bound
+
construction achieving it
=
optimality proof
```

---

## 11.21 Fast Revision Card

```text
========================================================
PART 11 — OPERATION MODELING
========================================================

CORE
Story -> State -> One Operation -> Delta -> Repetition

STATE TRANSITION
S' = f(S)

ADDITIVE
x' = x+d

REPEATED ADDITIVE
x_t = x_0+t*d

TRANSFER
ai' = ai-x
aj' = aj+x
Delta sum = 0

EXACTLY k ±1 MOVES
p+m=k
p-m=target-start

requires:
distance <= k
(k-distance) even

TOGGLE
b' = b XOR 1

after k toggles:
b' = b XOR (k mod 2)

MINIMUM MOVES
if max progress/move = d:

moves >= ceil(gap/d)

if achievable:
moves = ceil(gap/d)

REACHABILITY
target = start+t*d

check:
divisibility
t >= 0
bounds
preconditions

RANGE ADD
diff[L] += x
diff[R+1] -= x

REVERSE MODELING
if forward branches:
ask whether target reveals previous state

INVARIANT DISCOVERY
inspect Delta

SIMULATION QUESTION
Can t repeated operations be collapsed into a formula?

60-SECOND HABIT
"What exactly does ONE operation do mathematically?"
========================================================
```
