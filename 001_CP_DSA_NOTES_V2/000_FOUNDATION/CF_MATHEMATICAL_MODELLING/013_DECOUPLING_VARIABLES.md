# Part 12 — Decoupling Variables

> **Goal:** turn problems where several quantities appear to interact into smaller independent equations, counts, coordinates, contributions, or constraints.
>
> **Contest workflow:** `Story → Variables → Combined Conditions → Independent Effects → Decouple → Solve Parts → Recombine`
>
> **Core question:** **Can I solve these effects independently instead of tracking everything together?**

## Table of Contents

- [12.0 Decoupling Mental Model](#120-decoupling-mental-model)
- [12.1 Separate Independent Operation Counts](#121-separate-independent-operation-counts)
- [12.2 Sum and Difference Decoupling](#122-sum-and-difference-decoupling)
- [12.3 Positive and Negative Contributions](#123-positive-and-negative-contributions)
- [12.4 Horizontal and Vertical Movement](#124-horizontal-and-vertical-movement)
- [12.5 Per-Coordinate Decoupling](#125-per-coordinate-decoupling)
- [12.6 Per-Element Contribution](#126-per-element-contribution)
- [12.7 Frequency Instead of Order](#127-frequency-instead-of-order)
- [12.8 Quotient and Remainder](#128-quotient-and-remainder)
- [12.9 Odd and Even Groups](#129-odd-and-even-groups)
- [12.10 Prefix and Suffix Decoupling](#1210-prefix-and-suffix-decoupling)
- [12.11 Independent Constraints](#1211-independent-constraints)
- [12.12 Resource-by-Resource Decoupling](#1212-resource-by-resource-decoupling)
- [12.13 Objective Decoupling](#1213-objective-decoupling)
- [12.14 Decoupling by Symmetry](#1214-decoupling-by-symmetry)
- [12.15 When Decoupling Is Invalid](#1215-when-decoupling-is-invalid)
- [12.16 Decoupling vs Simulation](#1216-decoupling-vs-simulation)
- [12.17 60-Second Discovery Workflow](#1217-60-second-discovery-workflow)
- [12.18 Codeforces Recognition Map](#1218-codeforces-recognition-map)
- [12.19 Common Mistakes](#1219-common-mistakes)
- [12.20 Fast Revision Card](#1220-fast-revision-card)

---

## 12.0 Decoupling Mental Model

### Plain-English intuition

A story can make several quantities look connected:

```text
A changes
B changes
C changes
```

but the answer may actually be:

```text
solve A's contribution ─┐
solve B's contribution ─┼─> combine
solve C's contribution ─┘
```

### Real-world example — household budget

```text
rent      = 3000
food      = 1500
transport = 500
```

Instead of simulating every purchase chronologically:

```text
total
= rent + food + transport
= 3000 + 1500 + 500
= 5000
```

Each category contributes independently.

### Mathematical form

```text
F(x,y,z)=f(x)+g(y)+h(z)
```

Compute:

```text
f(x), g(y), h(z)
```

separately, then combine.

### Memory hook

```text
"Do I need the interaction, or only each part's contribution?"
```

---

## 12.1 Separate Independent Operation Counts

When order does not affect the final state, count operation types.

Let:

```text
p = number of +1 moves
m = number of -1 moves
```

Then:

```text
p+m = total moves
p-m = net displacement
```

### Real-world example — exactly 7 walking steps

Start at `0`; finish at `3`; each step is `+1` or `-1`.

Do not enumerate sequences such as:

```text
+ + - + - + +
```

Instead:

```text
p+m=7
p-m=3
```

Add:

```text
2p=10
p=5
```

Then:

```text
m=2
```

Check:

```text
5-2=3
```

### Complexity intuition

```text
many possible sequences
        ↓
two independent counts
        ↓
two equations
        ↓
O(1)
```

### Impossibility example

Exactly `7` steps, target `4`:

```text
p+m=7
p-m=4
------
2p=11
p=5.5
```

Counts must be integers, so impossible.

---

## 12.2 Sum and Difference Decoupling

Given:

```text
x+y=S
x-y=D
```

Add equations:

```text
2x=S+D
x=(S+D)/2
```

Subtract:

```text
2y=S-D
y=(S-D)/2
```

### Real-world example — two bank balances

Together:

```text
A+B=1400
```

Alice has `200` more:

```text
A-B=200
```

So:

```text
A=(1400+200)/2
 =1600/2
 =800

B=(1400-200)/2
 =1200/2
 =600
```

### Visual

```text
A+B = 1400 ─┐
             ├─> decouple ─> A=800
A-B =  200 ─┘              └> B=600
```

### Integer condition

```text
S+D must be even
S-D must be even
```

This is why parity often appears in these problems.

---

## 12.3 Positive and Negative Contributions

For changes `d_i`, separate gains and losses:

```text
P = Σ max(0,d_i)
N = Σ max(0,-d_i)
```

Then:

```text
net change       = P-N
absolute movement = P+N
```

### Real-world example — cash flow

Transactions:

```text
+500
-200
+300
-100
```

Income:

```text
P=500+300=800
```

Expenses:

```text
N=200+100=300
```

Net:

```text
800-300=500
```

Total money moved:

```text
800+300=1100
```

### CF trigger

```text
surplus / deficit
increase / decrease
supply / demand
positive / negative differences
```

---

## 12.4 Horizontal and Vertical Movement

For independent horizontal/vertical unit moves:

```text
dx=x2-x1
dy=y2-y1
```

Solve each axis separately:

```text
horizontal moves=|dx|
vertical moves  =|dy|
```

Recombine:

```text
minimum moves=|dx|+|dy|
```

### Real-world example — city blocks

```text
home   = (2,3)
office = (7,1)
```

Horizontal:

```text
|7-2|=5
```

Vertical:

```text
|1-3|=2
```

Total:

```text
5+2=7
```

### Visual

```text
H (2,3) ----------- 5 blocks -----------+
                                         |
                                         | 2 blocks
                                         v
                                      O (7,1)
```

No route enumeration is needed because the axis requirements are independent.

---

## 12.5 Per-Coordinate Decoupling

For vectors:

```text
A=(a1,...,an)
B=(b1,...,bn)
```

If one operation changes one coordinate by `±1` independently:

```text
cost_i=|ai-bi|
```

Therefore:

```text
total cost=Σ|ai-bi|
```

### Real-world example — independent thermostats

Current:

```text
[18,25,20]
```

Target:

```text
[21,22,20]
```

Room 1:

```text
|18-21|=3
```

Room 2:

```text
|25-22|=3
```

Room 3:

```text
|20-20|=0
```

Total:

```text
3+3+0=6 button presses
```

### Validity requirement

Changing one coordinate must not force another coordinate to change.

---

## 12.6 Per-Element Contribution

Instead of enumerating every global object, count how much each element contributes.

### Real-world example — electricity bill

```text
heater = 4 kWh
TV     = 2 kWh
PC     = 3 kWh
rate   = 1.5 lei/kWh
```

Independent costs:

```text
heater -> 4*1.5=6
TV     -> 2*1.5=3
PC     -> 3*1.5=4.5
```

Total:

```text
13.5 lei
```

### CP example — sum of all subarray sums

For 1-based index `i`, `A_i` is included when:

```text
left endpoint  ∈ [1..i] -> i choices
right endpoint ∈ [i..n] -> n-i+1 choices
```

Thus:

```text
number of subarrays containing A_i
= i(n-i+1)
```

Contribution:

```text
A_i * i * (n-i+1)
```

Final:

```text
sum of all subarray sums
= Σ A_i*i*(n-i+1)
```

### Visual

```text
1 ... L ... i ... R ... n
            ^
            Ai

L choices = i
R choices = n-i+1

Ai contribution
= Ai * i * (n-i+1)
```

---

## 12.7 Frequency Instead of Order

If order is irrelevant, replace the sequence by counts.

### Real-world example — cafeteria orders

```text
pizza, burger, pizza, salad, burger, pizza
```

Kitchen needs:

```text
pizza  = 3
burger = 2
salad  = 1
```

Order no longer matters.

### Mathematical transformation

```text
sequence
   |
   v
frequency[value]
```

### Equal-pair example

If a value appears `f` times:

```text
pairs=C(f,2)
     =f(f-1)/2
```

Each distinct value can be processed independently:

```text
answer=Σ f_v(f_v-1)/2
```

---

## 12.8 Quotient and Remainder

Every non-negative integer:

```text
n=q*k+r
```

with:

```text
q=floor(n/k)
0<=r<k
```

This decouples:

```text
complete groups
leftovers
```

### Real-world example — packing bottles

`23` bottles, `5` per box:

```text
23=4*5+3
```

So:

```text
full boxes=4
leftover bottles=3
```

### Visual

```text
[5] [5] [5] [5] [3]
 |   |   |   |    |
 full groups      remainder
```

### CF trigger

```text
full rounds + leftover
cycles + remainder
groups of k
periodic operations
```

---

## 12.9 Odd and Even Groups

Parity divides values into two classes:

```text
even
odd
```

### Real-world-style grouping example

Suppose items can only be paired with items from the same compatibility class. Instead of checking every pair, first separate the classes.

Array:

```text
[1,4,7,8,10,13]
```

Decouple:

```text
odd  = [1,7,13] -> 3
even = [4,8,10] -> 3
```

Useful rules:

```text
even+even = even
odd+odd   = even
even+odd  = odd
```

### CP intuition

If legality depends only on parity, replace actual values with:

```text
countOdd
countEven
```

whenever magnitudes are irrelevant.

---

## 12.10 Prefix and Suffix Decoupling

For every split:

```text
left  = [0..i]
right = [i+1..n-1]
```

precompute reusable summaries.

### Real-world example — load before/after checkpoint

Loads:

```text
[4,2,7,3,5]
```

Prefix sums:

```text
[4,6,13,16,21]
```

At split after index `2`:

```text
left=13
total=21
right=21-13=8
```

### Transformation

```text
recompute left/right each time
          ↓
precompute prefix once
          ↓
left(i)=prefix[i]
right(i)=total-prefix[i]
```

### Complexity

```text
naive many splits: O(n^2)
prefix model:      O(n)
```

---

## 12.11 Independent Constraints

Lower constraints can be summarized separately from upper constraints:

```text
lower=max(all lower bounds)
upper=min(all upper bounds)
```

### Real-world example — room temperature

```text
guest      : temperature >= 20
equipment  : temperature >= 18
hotel      : temperature <= 26
energy rule: temperature <= 24
```

Lower side:

```text
max(20,18)=20
```

Upper side:

```text
min(26,24)=24
```

Recombine:

```text
20 <= temperature <= 24
```

Feasible because:

```text
20<=24
```

---

## 12.12 Resource-by-Resource Decoupling

Compute how many products each resource independently supports, then take the bottleneck.

### Real-world example — sandwiches

One sandwich needs:

```text
2 bread
1 cheese
3 tomato
```

Available:

```text
20 bread
7 cheese
30 tomato
```

Independent capacities:

```text
bread  -> 20/2=10
cheese -> 7/1 =7
tomato -> 30/3=10
```

Recombine:

```text
answer=min(10,7,10)=7
```

### Visual

```text
bread  -> 10 ─┐
cheese ->  7  ├─> min -> 7 sandwiches
tomato -> 10 ─┘
```

---

## 12.13 Objective Decoupling

If:

```text
F(x1,...,xn)=Σ f_i(x_i)
```

and each `x_i` has an independent domain, then:

```text
min F = Σ min f_i
```

### Real-world example — independent delivery choices

Three packages independently choose the cheaper valid shipping method.

If there is no shared capacity/budget:

```text
package 1 -> optimize separately
package 2 -> optimize separately
package 3 -> optimize separately
                 |
                 v
              sum costs
```

### Critical condition

This does **not** work if there is a shared constraint such as:

```text
x1+x2+...+xn <= B
```

because choosing one variable changes the choices available to others.

---

## 12.14 Decoupling by Symmetry

If objects are interchangeable, their names may be irrelevant.

### Real-world example — identical checkout counters

Suppose three identical counters have loads:

```text
counter A=5
counter B=2
counter C=3
```

If counter identity does not matter, canonicalize:

```text
[5,2,3]
   ↓ sort
[2,3,5]
```

Many labeled states collapse to the same state.

### CP question

```text
"If I rename these objects, does the problem change?"
```

If no, exploit symmetry.

### Benefit

```text
many equivalent cases
       ↓
one canonical representation
```

---

## 12.15 When Decoupling Is Invalid

Variables are **coupled** if choosing one changes the legal choices for another.

### Real-world example — shared budget

Budget:

```text
100 lei
```

Choices:

```text
food=70
clothes=60
```

Individually:

```text
70<=100
60<=100
```

But together:

```text
70+60=130>100
```

You cannot optimize them independently.

### Warning signs

```text
shared budget
shared operation count
choose exactly k total
matching constraints
ordering dependencies
shared capacity
one choice disables another
```

### Sanity test

Ask:

```text
"If I solve A independently,
can that make B's chosen solution illegal?"
```

If yes, do not decouple them.

---

## 12.16 Decoupling vs Simulation

### Real-world example — monthly savings

Every month:

```text
money += 500
```

Initial:

```text
1000
```

After `T=12` months:

```text
final
= initial + 500*T
= 1000 + 500*12
= 1000 + 6000
= 7000
```

### Simulation

```text
1000 -> 1500 -> 2000 -> 2500 -> ... -> 7000
               repeat 12 times
                      |
                      v
                     O(T)
```

### Mathematical modeling — jump directly

```text
state_T = state_0 + change*T

1000 + 500*12
      |
      v
    7000

O(1)
```

### Why this belongs to decoupling

Separate:

```text
initial state
+
(number of repetitions * independent per-step contribution)
```

No need to track intermediate states.

### CF intuition

Before simulating, ask:

```text
Can I count effects instead of executing them?
Can I remove order?
Can I solve independent components?
```

---

## 12.17 60-Second Discovery Workflow

```text
COMPLICATED STORY
       |
       v
1. Name variables
       |
       v
2. Write equations
       |
       v
3. Remove irrelevant order/labels
       |
       v
4. Ask what can be independent
     /      |       \
    v       v        v
 counts   coords  contributions
     \      |       /
       v
5. Solve each part
       |
       v
6. Recombine with + / min / max / AND
```

### Questions to ask

```text
Does order matter?
Can operation types be counted?
Can axes be separated?
Can elements contribute independently?
Can sequence become frequencies?
Can constraints be grouped?
Is there a hidden shared constraint?
```

---

## 12.18 Codeforces Recognition Map

| Statement clue | Decoupling idea |
|---|---|
| forward/backward operations | count each operation type |
| total + difference | solve two equations |
| increments/decrements | positive/negative contributions |
| grid movement | x/y axes |
| independent coordinates | per-coordinate gap |
| sum over many objects | per-element contribution |
| order irrelevant | frequency |
| groups of `k` | quotient/remainder |
| parity-only condition | odd/even counts |
| split at `i` | prefix/suffix |
| many bounds | lower/upper summaries |
| several required resources | capacities then `min` |
| independent additive cost | optimize each term |
| interchangeable objects | symmetry/canonical state |
| repeated identical effect | count * effect |

---

## 12.19 Common Mistakes

### 1. Decoupling variables with a shared constraint

```text
x+y<=B
```

couples them.

### 2. Keeping sequence order unnecessarily

If only `p` and `m` matter, do not enumerate arrangements.

### 3. Forgetting integer feasibility

```text
x=(S+D)/2
```

requires correct parity.

### 4. Double-counting contributions

Assign each object/pair/subarray contribution exactly once.

### 5. Assuming axes are always independent

Diagonal-only moves change both coordinates simultaneously.

### 6. Forgetting the recombination operator

Independent parts may recombine using:

```text
sum
max
min
AND
OR
```

not always addition.

---

## 12.20 Fast Revision Card

```text
========================================================
PART 12 — DECOUPLING VARIABLES
========================================================

CORE
Break one coupled-looking problem into independent
counts, coordinates, contributions, or constraints.

OPERATION COUNTS
p+m=k
p-m=d

p=(k+d)/2
m=(k-d)/2

SUM / DIFFERENCE
x+y=S
x-y=D

x=(S+D)/2
y=(S-D)/2

POSITIVE / NEGATIVE
P=Σmax(0,di)
N=Σmax(0,-di)

net=P-N

2D INDEPENDENT MOVES
cost=|dx|+|dy|

PER COORDINATE
Σ|Ai-Bi|

CONTRIBUTION
answer=Σ contribution(i)

FREQUENCY
order irrelevant -> count values

QUOTIENT / REMAINDER
n=q*k+r

PREFIX / SUFFIX
solve left/right summaries separately

BOUNDS
lower=max(lower bounds)
upper=min(upper bounds)

RESOURCES
capacity_i=have_i/need_i
answer=min(capacity_i)

VALIDITY TEST
Can solving A independently make B illegal?
YES -> coupled
NO  -> decoupling may work

CORE QUESTION
"Can I solve these effects independently
and then combine them?"
========================================================
```
