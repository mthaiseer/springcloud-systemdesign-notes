# Part 18 — Greedy Through Mathematical Proofs

> **Goal:** stop choosing greedy actions because they “look best.” Convert a greedy idea into a mathematical claim and prove that the local choice can be part of an optimal solution.
>
> **Core workflow:** `Objective → Candidate local choice → Mathematical proof → Greedy order → Implementation`
>
> **Recognition question:** **If I make the locally best choice now, can I prove that replacing any competing choice with mine never makes the final answer worse?**

## Table of Contents

- [18.0 Greedy Mental Model](#180-greedy-mental-model)
- [18.1 What Greedy Actually Requires](#181-what-greedy-actually-requires)
- [18.2 Exchange Argument](#182-exchange-argument)
- [18.3 Staying-Ahead Proof](#183-staying-ahead-proof)
- [18.4 Lower Bound plus Construction](#184-lower-bound-plus-construction)
- [18.5 Extremal Principle](#185-extremal-principle)
- [18.6 Sorting as the Gateway to Greedy](#186-sorting-as-the-gateway-to-greedy)
- [18.7 Choose Cheapest First](#187-choose-cheapest-first)
- [18.8 Choose Earliest Finishing Interval](#188-choose-earliest-finishing-interval)
- [18.9 Pair Extremes](#189-pair-extremes)
- [18.10 Rearrangement and Pairing Proof](#1810-rearrangement-and-pairing-proof)
- [18.11 Greedy by Marginal Gain](#1811-greedy-by-marginal-gain)
- [18.12 Greedy by Marginal Cost](#1812-greedy-by-marginal-cost)
- [18.13 Greedy with Deadlines](#1813-greedy-with-deadlines)
- [18.14 Greedy with Limited Capacity](#1814-greedy-with-limited-capacity)
- [18.15 Frequency Greedy](#1815-frequency-greedy)
- [18.16 Greedy on Positive and Negative Contributions](#1816-greedy-on-positive-and-negative-contributions)
- [18.17 Greedy with Invariants](#1817-greedy-with-invariants)
- [18.18 Greedy Through Monotonicity](#1818-greedy-through-monotonicity)
- [18.19 Greedy Construction](#1819-greedy-construction)
- [18.20 When Greedy Fails](#1820-when-greedy-fails)
- [18.21 How to Search for a Counterexample](#1821-how-to-search-for-a-counterexample)
- [18.22 Greedy vs Dynamic Programming](#1822-greedy-vs-dynamic-programming)
- [18.23 60-Second Greedy Discovery Workflow](#1823-60-second-greedy-discovery-workflow)
- [18.24 Codeforces Recognition Map](#1824-codeforces-recognition-map)
- [18.25 Greedy Proof Templates](#1825-greedy-proof-templates)
- [18.26 Common Mistakes](#1826-common-mistakes)
- [18.27 Fast Revision Card](#1827-fast-revision-card)

---

## 18.0 Greedy Mental Model

Greedy means:

```text
make one locally best choice
        ↓
never undo it
        ↓
continue on the remaining problem
```

But the important part is not the choice.

The important part is:

```text
WHY is that choice safe?
```

### Real-world scenario — buying maximum items

You have:

```text
budget = 20
prices = [9,2,7,4,6]
```

Goal:

```text
buy maximum number of items
```

Sort:

```text
[2,4,6,7,9]
```

Buy cheapest first:

```text
2              spent=2   items=1
2+4            spent=6   items=2
2+4+6          spent=12  items=3
2+4+6+7        spent=19  items=4
next +9 > 20
```

Answer:

```text
4 items
```

### Why cheapest first?

For any fixed `k`, the cheapest possible set of `k` items is the first `k` sorted prices.

So if even that set costs more than the budget, **no other `k` items can fit**.

This is a proof, not a heuristic.

### Core model

```text
GREEDY CHOICE
      +
SAFETY PROOF
      =
GREEDY ALGORITHM
```

---

## 18.1 What Greedy Actually Requires

A greedy algorithm usually needs two ideas.

### 1. Greedy-choice property

There exists an optimal solution containing the choice we want to make now.

### 2. Optimal substructure

After fixing that choice, the remaining decisions form the same kind of optimization problem.

### Example

Choose maximum non-overlapping meetings.

If the earliest-finishing meeting is safe:

```text
choose it
```

Then discard all overlapping meetings.

What remains is again:

```text
choose maximum non-overlapping meetings
```

### Real-world memory hook

```text
SAFE FIRST CHOICE
       ↓
SAME PROBLEM REMAINS
       ↓
repeat
```

---

## 18.2 Exchange Argument

This is one of the most useful greedy proofs.

### Template

Suppose:

```text
G = greedy choice
O = choice used by some optimal solution
```

Show:

```text
replace O with G
```

without making the solution worse.

Then an optimal solution exists that starts with `G`.

### Real-world scenario — meetings

Suppose greedy selects meeting:

```text
G = [1,3]
```

because it finishes earliest.

An optimal schedule starts with:

```text
O = [1,5]
```

Replace `O` with `G`.

Since:

```text
finish(G)=3 <= finish(O)=5
```

every meeting that could start after `O` can also start after `G`.

Therefore:

```text
replacement loses nothing
```

So some optimal schedule starts with `G`.

### Proof skeleton

```text
Take an optimal solution O.
If O already uses G -> done.

Otherwise:
replace O's first competing choice with G.

Show:
feasibility preserved
objective not worse

Therefore:
there exists an optimal solution containing G.
```

---

## 18.3 Staying-Ahead Proof

Sometimes compare the greedy solution with any competing solution after every step.

Show:

```text
greedy is never behind
```

### Example — interval scheduling

Let greedy finishing times be:

```text
g1, g2, g3, ...
```

and an optimal schedule's finishing times:

```text
o1, o2, o3, ...
```

Prove inductively:

```text
finish(gk) <= finish(ok)
```

for every `k`.

Why useful?

If greedy finishes its first `k` meetings no later than another solution, greedy leaves at least as much room for future meetings.

### Real-world analogy — delivery route milestones

If after every completed delivery your schedule is no later than a competing schedule:

```text
you never have less remaining time
```

This is the “stays ahead” idea.

---

## 18.4 Lower Bound plus Construction

A powerful CP proof pattern:

```text
1. prove nobody can beat X
2. construct a solution achieving X
3. therefore X is optimal
```

### Real-world scenario — split 11 tasks between two workers

Goal:

```text
minimize maximum workload
```

Lower bound:

```text
at least one worker must receive
ceil(11/2)=6
```

because two workers cannot both have at most `5`:

```text
5+5=10 < 11
```

Construction:

```text
5 and 6
```

Maximum is:

```text
6
```

Thus:

```text
optimal = 6
```

### CF mental trigger

When you think:

```text
answer cannot be smaller/larger than X
```

immediately ask:

```text
Can I construct exactly X?
```

---

## 18.5 Extremal Principle

Focus on an extreme object:

```text
smallest
largest
leftmost
rightmost
earliest
latest
```

Extreme objects often have fewer possible interactions.

### Example

Suppose people stand on a line and must be paired to minimize certain crossing behavior.

The leftmost person has no one further left.

This can simplify the first forced/safe decision.

### Real-world scenario — queue

If you must decide who can be served before a deadline, the person with the earliest deadline is often the most constrained.

The extreme constraint can guide the greedy order.

### Modeling question

```text
Which object has the fewest future options?
```

That object is often a good candidate for the next greedy decision.

---

## 18.6 Sorting as the Gateway to Greedy

Many greedy algorithms are:

```text
sort by the quantity that defines "best next"
then scan
```

Possible keys:

```text
smallest cost
largest gain
earliest finish
earliest deadline
largest requirement
smallest right endpoint
```

### Example

Unsorted costs:

```text
8 3 5 1
```

After sorting:

```text
1 3 5 8
```

The order exposes a monotone structure:

```text
every next choice is at least as expensive
```

### Important warning

Sorting is not itself the proof.

You still need:

```text
Why is this ordering safe?
```

---

## 18.7 Choose Cheapest First

Goal:

```text
maximize number of purchases under budget B
```

Sort costs:

```text
c1 <= c2 <= ... <= cn
```

For any `k` chosen items:

```text
their total cost >= c1+c2+...+ck
```

Therefore the cheapest `k` items are the minimum-cost way to buy `k` items.

### Example

```text
B=15
costs=[8,1,6,3,5]
```

Sort:

```text
1,3,5,6,8
```

Prefix:

```text
1
4
9
15
23
```

Maximum affordable count:

```text
4
```

### Proof

If another solution buys `k` items and contains an item more expensive than an unchosen cheaper item, swap them.

Cost cannot increase.

Repeat until the chosen set is the cheapest `k`.

This is an exchange argument.

---

## 18.8 Choose Earliest Finishing Interval

Classic interval scheduling:

```text
maximize number of non-overlapping intervals
```

Greedy:

```text
sort by ending time
take the earliest finishing compatible interval
```

### Example

```text
A=[1,4]
B=[2,3]
C=[3,5]
D=[5,7]
```

Sort by finish:

```text
B [2,3]
A [1,4]
C [3,5]
D [5,7]
```

Choose:

```text
B -> C -> D
```

Count:

```text
3
```

### Why earliest finish?

If an optimal solution begins with an interval ending later than greedy's first interval, replace it with greedy's.

Greedy leaves at least as much remaining timeline.

So the replacement cannot reduce future options.

### Real-world scenario — meeting room

Finishing earlier frees the room sooner.

```text
earlier finish
      ↓
maximum remaining room availability
```

---

## 18.9 Pair Extremes

Sorted values:

```text
a1 <= a2 <= ... <= an
```

Many pairing problems become:

```text
smallest with largest
```

or:

```text
adjacent with adjacent
```

depending on the objective.

### Example — balance pair sums

```text
[1,2,8,9]
```

Pair extremes:

```text
1+9=10
2+8=10
```

Maximum pair sum:

```text
10
```

Alternative:

```text
1+2=3
8+9=17
```

Maximum:

```text
17
```

### Exchange intuition

Pairing two large elements together concentrates cost.

Moving one large element to a smaller partner tends to balance extremes.

### Real-world scenario — balancing luggage

Weights:

```text
1,2,8,9
```

Two carts, two bags each.

Pair heavy with light:

```text
9+1
8+2
```

produces balanced loads.

---

## 18.10 Rearrangement and Pairing Proof

For sorted:

```text
a <= b
x <= y
```

Compare two pairings.

Same-direction product:

```text
ax + by
```

cross pairing:

```text
ay + bx
```

Difference:

```text
(ax+by)-(ay+bx)

= ax-ay+by-bx
= a(x-y)+b(y-x)
= (b-a)(y-x)
>= 0
```

Therefore:

```text
ax+by >= ay+bx
```

### Meaning

To maximize sum of products:

```text
pair small with small
pair large with large
```

To minimize:

```text
pair small with large
```

### Real-world scenario — assigning efficiency to machine power

Workers:

```text
efficiency 2,5
```

Machines:

```text
power 3,8
```

Same order:

```text
2*3 + 5*8 = 46
```

Cross:

```text
2*8 + 5*3 = 31
```

The algebra proves the greedy pairing.

---

## 18.11 Greedy by Marginal Gain

Sometimes each action gives a benefit.

If actions are independent and each costs the same resource, choose largest gains first.

### Example

You may perform exactly `3` upgrades.

Benefits:

```text
[4,10,2,7,6]
```

Sort descending:

```text
10,7,6,4,2
```

Take:

```text
10+7+6=23
```

### Exchange proof

Suppose a chosen gain is:

```text
x
```

and an unchosen gain is:

```text
y > x
```

Swap:

```text
new total = old total - x + y
```

Since:

```text
y-x > 0
```

the objective improves.

Therefore an optimum cannot exclude a larger independent gain while including a smaller one.

### Warning

This fails if actions interact or have different costs/constraints.

---

## 18.12 Greedy by Marginal Cost

Dual idea:

```text
when every selected action gives equal required progress,
take smallest cost first
```

### Example

Need to complete any `3` independent jobs.

Costs:

```text
9,2,6,3,8
```

Sort:

```text
2,3,6,8,9
```

Take:

```text
2+3+6=11
```

### Proof

Any other set of three has total cost at least the sum of the three smallest costs.

### Real-world scenario — three mandatory errands

If every errand counts equally toward the target and there are no dependencies, choose the three cheapest errands.

---

## 18.13 Greedy with Deadlines

Deadlines often suggest ordering by:

```text
earliest deadline
```

but the exact greedy depends on the problem.

### Unit-time job example

Each job takes one slot and must be done by its deadline.

Jobs:

```text
A deadline 1
B deadline 2
C deadline 2
```

If all are required, schedule the most constrained deadline first:

```text
slot1 A
slot2 B
```

Only two slots exist by deadline 2, so all three cannot fit.

### Feasibility condition

After sorting deadlines:

```text
d1 <= d2 <= ... <= dn
```

for unit jobs, a schedule is feasible when:

```text
i <= di
```

for every 1-based position `i`.

### Real-world scenario

A task expiring sooner has fewer possible future slots.

### Important

Do not memorize “deadline => sort by deadline” blindly. Profit/deadline variants may need a heap or another proof.

---

## 18.14 Greedy with Limited Capacity

When resources are limited, identify what should occupy scarce capacity.

### Example — keep best `k`

Values arrive:

```text
4,9,2,8,7
```

Capacity:

```text
k=3
```

If the goal is maximum total value and items are independent/equal size:

```text
keep 9,8,7
```

Sum:

```text
24
```

### Online implementation idea

Maintain selected values.

If more than `k`:

```text
remove smallest selected value
```

### Proof

If selected set contains `x` while an unselected `y>x` exists:

```text
replace x by y
```

Capacity stays the same and total improves.

### Real-world scenario — three display slots

Only three products fit on a featured page. If each uses one slot and value is independent, keep the three highest-value products.

---

## 18.15 Frequency Greedy

Sometimes individual elements are irrelevant; only frequencies matter.

### Example — remove minimum elements to make all remaining values equal

```text
[1,1,1,2,2,3]
```

Frequencies:

```text
1 -> 3
2 -> 2
3 -> 1
```

Keep the most frequent value:

```text
3 copies of 1
```

Remove:

```text
6-3=3
```

### Mathematical proof

If final value is `v`, removals are:

```text
n-freq[v]
```

Minimize:

```text
n-freq[v]
```

Equivalent to maximize:

```text
freq[v]
```

Therefore:

```text
answer = n-maxFrequency
```

### Real-world scenario — standardizing devices

If every device must end with the same existing configuration and changing/removing a device costs equally, preserve the configuration already used most often.

---

## 18.16 Greedy on Positive and Negative Contributions

If choices contribute independently to a sum and selection is optional:

```text
positive contribution -> take
negative contribution -> reject
zero -> neutral
```

### Example

Optional projects have profits:

```text
[8,-3,5,-10,2]
```

Choose any subset to maximize total profit.

Take:

```text
8+5+2=15
```

### Proof

For a negative `x`:

```text
S+x < S
```

so including it worsens the objective.

For positive `x`:

```text
S+x > S
```

so excluding it wastes gain.

### Important

This only works when choices are independent. Constraints like “choose exactly k” change the model.

---

## 18.17 Greedy with Invariants

An invariant can make a greedy move safe because all valid solutions must preserve the same quantity.

### Example pattern

Suppose every operation reduces total remaining work by exactly one.

Then:

```text
number of operations
```

may be fixed regardless of order.

If order only affects feasibility, greedy can focus on preserving future feasibility rather than minimizing operation count.

### Real-world analogy — packing fixed-count orders

If exactly `n` orders must be processed, no ordering changes the number of orders.

The optimization may instead be:

```text
avoid deadline violations
```

The invariant removes one dimension of the problem.

### Contest question

Before choosing greedily ask:

```text
Which quantities are already fixed for every solution?
```

Then optimize only what actually varies.

---

## 18.18 Greedy Through Monotonicity

A greedy action is often safe because it moves the state monotonically toward the goal.

Examples:

```text
remaining budget only decreases
current rightmost covered point only increases
chosen finish times only increase
unprocessed set only shrinks
```

### Example — cover points with fixed-length intervals

Sorted points:

```text
1,2,3,8,9
```

Suppose one interval covers length `2`.

Take the leftmost uncovered point:

```text
1
```

Place interval as far right as possible while covering it:

```text
[1,3]
```

This covers:

```text
1,2,3
```

Next uncovered:

```text
8
```

Place:

```text
[8,10]
```

Total:

```text
2 intervals
```

### Proof intuition

The leftmost uncovered point must be covered by some interval.

Pushing that interval rightward as far as allowed cannot lose any point to its left that still needs coverage, because none exists.

It can only help cover more future points.

---

## 18.19 Greedy Construction

Constructive problems often allow choosing any valid answer.

Greedy can maintain an invariant after every placement.

### Example — build a binary string with no adjacent `1`

Suppose you have:

```text
zeros=4
ones=3
```

One safe arrangement:

```text
1 0 1 0 1 0 0
```

### Slot model

Place zeros first:

```text
_ 0 _ 0 _ 0 _ 0 _
```

There are:

```text
zeros+1 = 5
```

slots where ones can be placed without adjacency if at most one goes into each slot.

Condition:

```text
ones <= zeros+1
```

### Greedy construction

Place each `1` into a separate available gap.

### Proof

The slot bound is necessary, and the construction achieves it.

Again:

```text
bound + construction = proof
```

---

## 18.20 When Greedy Fails

A locally best-looking action may block a better future.

### Counterexample — largest value first under capacity

Capacity:

```text
10
```

Items `(weight,value)`:

```text
A=(10,10)
B=(6,9)
C=(4,9)
```

Greedy by largest individual value chooses:

```text
A -> value 10
```

But:

```text
B+C
weight=10
value=18
```

is better.

### Why greedy failed

Choices interact through capacity.

Taking one item changes which combinations remain possible.

### Warning signs

```text
different costs
future compatibility
exact totals
choices interact
local action destroys options
```

These often suggest:

```text
DP
search
matching
flow
```

rather than simple greedy.

---

## 18.21 How to Search for a Counterexample

Before trusting a greedy rule, attack it.

### Method

Try tiny cases with:

```text
3-5 elements
ties
one huge value
many medium values
one very cheap item
one expensive high-gain item
tight capacity
```

### Example candidate rule

```text
"always choose largest value first"
```

Try:

```text
capacity=10

(10,10)
(6,9)
(4,9)
```

Greedy:

```text
10
```

Optimal:

```text
18
```

Rule disproved.

### Contest habit

Spend 30–60 seconds trying to break your greedy before coding it.

A counterexample is cheaper than a wrong submission.

---

## 18.22 Greedy vs Dynamic Programming

Ask:

```text
Does the locally optimal choice eliminate the need to reconsider alternatives?
```

If yes and provable:

```text
greedy
```

If the best future depends on which earlier choice was made:

```text
DP may be needed
```

### Greedy-style state

```text
one canonical best partial state is enough
```

### DP-style state

```text
multiple partial states must survive
because each may lead to a different future optimum
```

### Example

Buying maximum count with budget and equal value per item:

```text
cheapest first -> greedy
```

0/1 knapsack with different values:

```text
(weight,value) interactions -> DP
```

---

## 18.23 60-Second Greedy Discovery Workflow

```text
PROBLEM
   |
   v
Optimization / construction?
   |
   v
What is the locally attractive choice?
   |
   v
Can I prove it is SAFE?
   |
   +-----------------------------+
   |                             |
exchange?                    lower bound?
stays ahead?                 extremal?
monotonicity?                invariant?
   |                             |
   +-------------+---------------+
                 |
                 v
Can any optimal solution be transformed
to use my greedy choice without worsening?
                 |
             +---+---+
            YES     NO/UNKNOWN
             |         |
             v         v
        GREEDY      search for
        candidate   counterexample /
                    DP / other model
```

### Fast contest questions

```text
1. What exactly am I optimizing?
2. What is the candidate local choice?
3. Why this order?
4. Can I exchange an optimal choice with mine?
5. Does my choice leave at least as many future options?
6. Can I derive a lower/upper bound?
7. Can greedy construct that bound?
8. Is an extreme element forced or safest?
9. Are choices independent?
10. Can I break the rule with 3-5 elements?
```

---

## 18.24 Codeforces Recognition Map

| Statement clue | Candidate greedy model |
|---|---|
| maximize count under budget | cheapest first |
| maximize independent gains with fixed number chosen | largest gain first |
| maximum non-overlapping intervals | earliest finish |
| balance pair extremes | sort + pair extremes |
| maximize sum of products | same-order pairing |
| minimize sum of products | opposite-order pairing |
| limited `k` slots | keep best `k` |
| make all values equal by removals | keep max frequency |
| optional independent contributions | take positive contributions |
| leftmost uncovered point | cover it as far right as possible |
| earliest deadline / most constrained item | process constrained extreme first |
| prove local choice safe | exchange argument |
| prove greedy never falls behind | staying-ahead |
| derive unavoidable bound and attain it | bound + construction |
| local rule seems plausible but interactions exist | counterexample / DP check |

---

## 18.25 Greedy Proof Templates

### Template A — Exchange argument

```text
Let G be greedy's next choice.

Take an optimal solution O.

If O already contains G in the required position:
    done.

Otherwise:
    replace O's competing choice X with G.

Prove:
1. solution remains feasible
2. objective does not worsen

Therefore an optimal solution containing G exists.
Repeat.
```

### Template B — Staying ahead

```text
After k decisions:

prove greedy_state(k)
is at least as good as
optimal_state(k)

Base case: k=1
Induction: assume true for k
prove for k+1

Therefore greedy cannot finish worse.
```

### Template C — Lower bound + construction

```text
Every solution must have answer >= X.
(or <= X for maximization)

Greedy constructs a solution with answer = X.

Therefore X is optimal.
```

### Template D — Extremal choice

```text
Take the most constrained/extreme object E.

Show:
any valid solution must handle E.

Choose the way of handling E that preserves
the maximum freedom for remaining objects.

Reduce to smaller instance.
```

### Template E — Swap inversion

```text
Suppose solution contains adjacent choices
in the "wrong" order.

Compare:

cost(wrong order)
vs
cost(swapped order)

derive:
cost(swapped) <= cost(wrong)

Therefore inversions can be removed
without worsening the answer.

Eventually the greedy sorted order remains.
```

---

## 18.26 Common Mistakes

### 1. “It seems optimal” is not a proof

Always identify:

```text
exchange
staying ahead
bound + construction
extremal
monotonicity
invariant
```

### 2. Sorting without explaining the key

```text
sort ascending
```

is an implementation step, not mathematical justification.

### 3. Optimizing the wrong local quantity

Largest value is not necessarily best when costs differ.

### 4. Ignoring future feasibility

A local gain may destroy many future choices.

### 5. Assuming independence

Check whether choosing `x` changes the value or feasibility of choosing `y`.

### 6. Not testing tiny counterexamples

Try adversarial small inputs before coding.

### 7. Confusing a necessary condition with sufficient proof

Showing:

```text
greedy choice looks necessary
```

is not enough unless you show a complete optimal construction or exchange.

### 8. Using a known greedy from a different variant

Small changes such as:

```text
weighted intervals
different job durations
exactly k selections
```

can invalidate the original proof.

---

## 18.27 Fast Revision Card

```text
========================================================
PART 18 — GREEDY THROUGH MATHEMATICAL PROOFS
========================================================

GREEDY

locally best choice
        +
proof choice is safe
        +
solve remaining smaller problem

--------------------------------------------

MAIN PROOF TOOLS

1. EXCHANGE

optimal uses X
greedy uses G

replace X -> G

if:
feasible remains
answer not worse

then G is safe

--------------------------------------------

2. STAYING AHEAD

after every k choices:

greedy state >= competitor state
(or <= for minimization)

--------------------------------------------

3. BOUND + CONSTRUCTION

prove:
answer cannot beat X

construct:
greedy achieves X

therefore:
optimal = X

--------------------------------------------

4. EXTREMAL PRINCIPLE

focus on:
smallest/largest
leftmost/rightmost
earliest/latest
most constrained

--------------------------------------------

COMMON GREEDY FORMS

maximum count under budget
-> cheapest first

maximum independent gain
-> largest gain first

interval scheduling
-> earliest finish

balanced pair sums
-> pair extremes

product pairing:
max -> same order
min -> opposite order

keep k items
-> keep best k

equalize by removals
-> keep maximum frequency

--------------------------------------------

PAIRING ALGEBRA

a <= b
x <= y

(ax+by)-(ay+bx)
=
(b-a)(y-x)
>= 0

therefore same order
maximizes pair-product sum

--------------------------------------------

FAILURE TEST

Ask:

"Can this locally best choice
destroy a combination that is globally better?"

If YES:
greedy may fail.

--------------------------------------------

CONTEST WORKFLOW

objective
   ↓
candidate greedy order
   ↓
try to BREAK it
   ↓
choose proof:
exchange / staying ahead /
bound+construction / extremal
   ↓
only then code

--------------------------------------------

CORE QUESTION

"Can I transform ANY optimal solution
to follow my greedy choice
without making it worse?"

If YES, you probably have the proof.
========================================================
```
