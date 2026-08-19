# CP Mathematical Modeling Mini-Course

## 13. Greedy Mathematical Modeling

> **Goal:** Learn how to **derive** a greedy choice instead of memorizing greedy algorithms.
>
> A greedy solution repeatedly makes a locally best-looking choice. The important part is not the choice itself—it is proving:
>
> **"Making this choice now cannot make the final answer worse."**

---

# Chapter Tree

```text
13. GREEDY MATHEMATICAL MODELING
│
├── 13.1 What greedy really means
├── 13.2 Model the objective first
├── 13.3 Identify the scarce resource
├── 13.4 Sorting reveals decision order
├── 13.5 Cheapest-first modeling
├── 13.6 Largest-benefit-first modeling
├── 13.7 Pairing extremes
├── 13.8 Earliest finishing / boundary choices
├── 13.9 Bottleneck modeling
├── 13.10 Exchange argument
├── 13.11 Stay-ahead reasoning
├── 13.12 Forced greedy choices
├── 13.13 Greedy with frequencies
├── 13.14 Greedy with intervals
├── 13.15 When greedy fails
├── 13.16 Greedy vs DP / binary search
└── 13.17 CF-style modeling workflow
```

Central engine:

```text
OPTIMIZATION STORY
       │
       ▼
What exactly is minimized/maximized?
       │
       ▼
What choice affects future freedom?
       │
       ▼
Can sorting expose a useful order?
       │
       ▼
Candidate local choice
       │
       ▼
Can any optimal solution be modified
to use my choice without becoming worse?
       │
      YES
       ▼
GREEDY CHOICE IS SAFE
```

---

# 13.1 Greedy Is Not "Take the Biggest"

Greedy does **not** mean:

```text
always take largest
```

or:

```text
always take smallest
```

Depending on the objective, the correct choice may be:

```text
smallest cost
largest gain
earliest finishing
latest starting
smallest endpoint
largest deficit
closest value
farthest value
forced boundary
```

So the first question is never:

```text
Which greedy pattern do I remember?
```

It is:

```text
What am I optimizing?
```

---

# 13.2 Write the Objective Explicitly

Story:

> Buy as many items as possible with budget `B`.

Translate:

```text
maximize count
subject to total_cost <= B
```

Story:

> Minimize total cost to obtain at least `K` units.

Translate:

```text
minimize total_cost
subject to obtained_units >= K
```

Story:

> Schedule maximum number of non-overlapping intervals.

Translate:

```text
maximize selected_count
subject to no overlap
```

Writing the objective often reveals what kind of choice preserves future options.

---

# 13.3 Greedy Often Protects a Scarce Resource

Suppose you want maximum number of items under budget.

Every selected item consumes:

```text
money
```

Money is the scarce resource.

For the same benefit:

```text
1 selected item
```

choosing the cheaper item consumes less resource.

Therefore it leaves at least as much budget for future choices.

This suggests:

```text
sort costs ascending
take cheapest while possible
```

The reasoning is:

```text
same immediate benefit
+
less resource consumed
=
more future freedom
```

---

# 13.4 Example — Maximum Number of Purchases

Costs:

```text
[6,2,4,3]
```

Budget:

```text
9
```

Sort:

```text
[2,3,4,6]
```

Take:

```text
2 -> remaining 7
3 -> remaining 4
4 -> remaining 0
```

Count:

```text
3
```

Why is cheapest-first safe?

If a solution selects an item costing `6` while an unselected item costs `2`, replacing:

```text
6 -> 2
```

does not reduce number of selected items and cannot increase cost.

This is an **exchange argument**.

---

# 13.5 Exchange Argument — Core Proof Tool

Suppose greedy chooses:

```text
G
```

but some optimal solution chooses:

```text
O
```

instead.

Try replacing:

```text
O -> G
```

If the solution remains valid and does not become worse, then there exists an optimal solution containing `G`.

Therefore choosing `G` first is safe.

Template:

```text
1. Consider an optimal solution.

2. If it already uses greedy choice G:
   fine.

3. Otherwise it uses some alternative O.

4. Replace O with G.

5. Show:
   validity remains,
   objective does not worsen.

6. Therefore an optimal solution exists
   with greedy choice G.
```

This is one of the most useful greedy proofs in CP.

---

# 13.6 Sorting Is Not the Greedy Proof

A common mistake:

```text
"I sorted, therefore greedy works."
```

Sorting only exposes an order.

You still need to understand why processing in that order is safe.

Ask:

```text
What property becomes easier after sorting?
```

Examples:

```text
smallest costs first
earliest endpoints first
adjacent differences
pair smallest with largest
process requirements in increasing order
```

Sorting is a modeling tool, not a correctness proof.

---

# 13.7 Largest Benefit First

Suppose every operation costs exactly:

```text
1 move
```

and operation `i` reduces the remaining problem by:

```text
gain[i]
```

If there are no interactions and you need maximum reduction using at most `K` operations, taking largest gains may be optimal.

Sort:

```text
gain descending
```

Take first `K`.

Why?

All choices have equal cost.

So for each slot:

```text
larger benefit dominates smaller benefit
```

Exchange:

```text
if optimal selected smaller gain
while larger unselected gain exists,
swap them.
```

Objective cannot worsen.

---

# 13.8 Cost-Benefit Warning

Suppose choices have:

```text
different costs
different benefits
```

Then:

```text
largest benefit first
```

may fail.

And:

```text
best benefit/cost ratio
```

may also fail for indivisible 0/1 choices.

Example:

```text
capacity = 4

item A:
cost 3, value 5

item B:
cost 2, value 3

item C:
cost 2, value 3
```

Ratio:

```text
A = 5/3 ≈ 1.67
B = 3/2 = 1.5
C = 3/2 = 1.5
```

Ratio greedy takes A:

```text
value = 5
```

But B+C fits:

```text
cost = 4
value = 6
```

This is why 0/1 knapsack generally needs DP rather than simple ratio greedy.

Lesson:

```text
A plausible local score is not enough.
You need a proof.
```

---

# 13.9 Pairing Extremes

Sorted values:

```text
a1 <= a2 <= ... <= an
```

Many pairing problems become easier when considering:

```text
smallest with largest
```

or:

```text
adjacent values
```

But which one is correct depends on the objective.

Example objective:

> Minimize the maximum pair sum.

Pairing:

```text
smallest + largest
second-smallest + second-largest
...
```

often balances the extreme values.

Why?

The largest value must be paired with someone.

Giving it the smallest available partner minimizes the sum involving that unavoidable largest element.

Then recurse on remaining values.

This is a bottleneck argument.

---

# 13.10 Example — Minimize Maximum Pair Sum

Sorted:

```text
1 2 7 10
```

Pair extremes:

```text
1+10 = 11
2+7  = 9
```

Maximum:

```text
11
```

Alternative:

```text
1+2  = 3
7+10 = 17
```

Maximum:

```text
17
```

The largest element `10` must be paired.

To keep its pair as small as possible:

```text
pair it with 1
```

Then solve remaining values similarly.

---

# 13.11 Adjacent Pairing

Different objective:

> Minimize total absolute difference between pairs.

For sorted values, pairing nearby elements is natural:

```text
(a1,a2)
(a3,a4)
...
```

Why?

Crossing/far-apart pairings waste distance.

Example:

```text
1,3,8,10
```

Adjacent:

```text
|1-3| + |8-10|
= 2+2
= 4
```

Extreme:

```text
|1-10| + |3-8|
= 9+5
= 14
```

Again, derive from the objective rather than memorizing "pair extremes."

---

# 13.12 Interval Greedy — Preserve Future Space

Classic model:

> Select maximum number of non-overlapping intervals.

Suppose intervals:

```text
[L,R]
```

Which selected interval leaves maximum room for future intervals?

The one finishing earliest.

So:

```text
sort by right endpoint
```

and repeatedly select the first compatible interval.

Mental model:

```text
current choice
       ↓
how much future timeline does it consume?
       ↓
earlier finish leaves more future space
```

---

# 13.13 Why Earliest Finish Is Safe

Let greedy choose interval:

```text
G
```

with smallest finishing time.

Suppose an optimal solution starts with:

```text
O
```

where:

```text
end(G) <= end(O)
```

Replace:

```text
O -> G
```

Any interval that can come after `O` also starts after `end(O)`.

Since:

```text
end(G) <= end(O)
```

it can also come after `G`.

So replacement does not reduce the number of future choices.

Therefore an optimal solution exists starting with `G`.

That is an exchange argument.

---

# 13.14 Bottleneck Modeling

Some objectives are controlled by the worst component.

Examples:

```text
minimize maximum load
minimize maximum pair sum
maximize minimum distance
maximize minimum value
```

These are bottleneck objectives.

When you see:

```text
minimize the maximum
```

ask:

```text
Which unavoidable extreme currently controls the answer?
```

Example:

```text
largest element must belong somewhere
```

so handle it in the safest possible way.

This can lead to greedy, or sometimes binary search on answer.

---

# 13.15 Greedy From a Forced Boundary

Sometimes a choice is not merely "best"; it is forced.

Example:

> You must fix an array from left to right, and operation at position `i` is the last operation capable of changing `a[i]`.

Then when you reach `i`:

```text
target[i] - current[i]
```

may uniquely determine how many times operation `i` must be used.

So:

```text
leftmost unresolved position
        ↓
only one way to fix it
        ↓
choice is forced
        ↓
process forward
```

Many greedy operation problems work this way.

---

# 13.16 Example — Forced Greedy

Suppose operation at `i` adds `1` to:

```text
a[i]
a[i+1]
```

Target:

```text
b
```

At index `0`, only operation `0` affects it.

Therefore:

```text
x[0] = b[0]-a[0]
```

is forced.

After applying that contribution, index `1` determines `x[1]`.

```text
position 0
   ↓
force x0
   ↓
position 1
   ↓
force x1
   ↓
position 2
...
```

This is greedy-looking, but its correctness comes from **no future operation can repair the past boundary**.

---

# 13.17 Greedy With Frequencies

Suppose values belong to only a few categories.

Instead of sorting every value, frequency counts may be enough.

Example:

```text
costs are only 1,2,3
```

To maximize item count under budget:

```text
take cost-1 items
then cost-2
then cost-3
```

You can process:

```text
freq[1], freq[2], freq[3]
```

The greedy principle remains:

```text
cheapest first
```

but state representation is compressed.

---

# 13.18 Greedy and Resource Modeling

Suppose action `i` consumes:

```text
cost[i]
```

and all actions provide equal benefit.

Objective:

```text
maximize number of actions
```

This is exactly:

```text
maximize count
subject to sum(cost) <= budget
```

Greedy:

```text
smallest cost first
```

because for a fixed number of selected actions, the cheapest `k` actions have minimum possible total cost.

This is another useful proof:

```text
If even the k cheapest do not fit,
no set of k actions can fit.
```

---

# 13.19 Prefix View After Sorting

Costs sorted:

```text
c1 <= c2 <= ... <= cn
```

Prefix sums:

```text
P[k] = c1+c2+...+ck
```

Then:

```text
maximum purchasable count
=
largest k such that P[k] <= B
```

The greedy choice becomes a mathematical statement:

```text
minimum cost of selecting k items
=
sum of k smallest costs
```

This is stronger than "I think cheapest first works."

---

# 13.20 Stay-Ahead Reasoning

Another proof style:

Show that after every step, greedy is at least as good as any alternative according to some measure.

Example:

```text
after selecting k intervals,
greedy's finishing time
<=
finishing time of any solution
with k compatible selected intervals
```

Therefore greedy always leaves at least as much future space.

This is called a stay-ahead argument.

You do not need the name in contests.

Think:

```text
After each decision,
is my greedy state never worse
than another possible state?
```

---

# 13.21 Greedy by Removing the Worst

Sometimes it is easier to:

```text
take everything
```

then remove bad choices.

Example pattern:

```text
process items in some order
temporarily include current item

if constraint breaks:
    remove the selected item
    that hurts us most
```

Why can this work?

Because among currently selected choices, removing the most expensive/harmful one preserves the largest amount of future flexibility.

This appears in scheduling and capacity problems.

The proof still needs exchange/stay-ahead reasoning.

---

# 13.22 Local Dominance

Suppose two available choices A and B have exactly the same effect on future structure, but:

```text
cost(A) <= cost(B)
```

Then B is dominated.

There is no reason to choose B instead of A.

Similarly, if costs are equal and:

```text
benefit(A) >= benefit(B)
```

then B may be dominated.

Greedy often comes from repeatedly eliminating dominated choices.

---

# 13.23 Lexicographically Smallest Construction

Greedy also appears when output must be lexicographically smallest.

At each position:

```text
try smallest possible value
```

but only commit if the remaining suffix is still feasible.

Pattern:

```text
for each position:
    for candidate from smallest upward:
        if completion remains possible:
            choose candidate
            break
```

The key is the feasibility check.

Without it, "smallest now" may make completion impossible.

So greedy can combine with:

```text
feasibility modeling
```

---

# 13.24 Greedy + Feasibility

General pattern:

```text
choose best local candidate
        ↓
will a valid completion still exist?
        ↓
YES -> commit
NO  -> try next candidate
```

This is especially useful for constructive problems.

It connects:

```text
greedy
+
Chapter 6 feasibility
+
Chapter 12 transformations
```

---

# 13.25 When Greedy Fails: Future Interaction

Greedy is dangerous when today's choice changes tomorrow's opportunities in a complicated way.

Warning signs:

```text
choices have different costs AND benefits
choice can unlock/disable other choices
you may need to save a resource for later
local optimum can block a better combination
state depends on several dimensions
```

Then consider:

```text
DP
graph search
binary search on answer
matching
```

depending on structure.

---

# 13.26 Tiny Counterexample Testing

Before trusting a greedy rule, actively try to break it.

Suppose candidate rule:

```text
always take largest value first
```

Test tiny cases:

```text
n = 2
n = 3
small capacity
one large item vs two medium items
ties
extreme values
```

Ask:

```text
Can sacrificing the locally best choice
create a better total answer?
```

If yes, greedy fails.

This should be part of your contest workflow.

---

# 13.27 Greedy vs DP

Think greedy when:

```text
one choice dominates another
exchange is possible
past can be finalized safely
one-dimensional resource/order
boundary choice is forced
```

Think DP when:

```text
different choices create genuinely different
future states that cannot be dominated easily
```

Example:

```text
0/1 knapsack
```

Choosing one item changes remaining capacity, and neither choice may dominate the other.

So we keep multiple states:

```text
DP
```

instead of committing greedily.

---

# 13.28 Greedy vs Binary Search on Answer

Problem:

```text
maximize minimum distance
```

A tempting greedy may construct placements.

But the optimization itself may be easier as:

```text
Can I achieve minimum distance D?
```

For a fixed `D`, a greedy placement may answer feasibility.

Then binary search `D`.

So architecture becomes:

```text
optimization
    ↓
binary search answer D
    ↓
greedy feasibility check
```

Algorithms can combine.

---

# 13.29 Greedy With Two Pointers

After sorting, a greedy pairing rule often becomes two pointers.

Example:

```text
smallest with largest
```

Use:

```text
L = 0
R = n-1
```

Each decision consumes one or both endpoints.

Visual:

```text
1  2  4  7  10  13
^                 ^
L                 R
```

The two-pointer implementation is secondary.

First prove why the endpoint choice is correct.

---

# 13.30 Complete CF-Style Example 1 — Maximum Count

Costs:

```text
[5,1,4,2,8]
```

Budget:

```text
8
```

Objective:

```text
maximize selected count
```

All items give equal benefit:

```text
+1 count
```

Sort:

```text
1,2,4,5,8
```

Prefix:

```text
1
3
7
12
20
```

Largest prefix <= 8:

```text
7
```

which uses:

```text
3 items
```

Why optimal?

Any 4 items cost at least the sum of the 4 cheapest:

```text
1+2+4+5 = 12 > 8
```

So 4 is impossible.

---

# 13.31 Complete CF-Style Example 2 — Maximum Non-Overlapping Intervals

Intervals:

```text
[1,4]
[2,3]
[3,5]
[5,7]
```

Sort by end:

```text
[2,3]
[1,4]
[3,5]
[5,7]
```

Depending on the problem's exact overlap convention, select the earliest finishing compatible interval each time.

Core reasoning:

```text
earlier finish
=
no less future space
```

Always check whether touching endpoints count as compatible.

---

# 13.32 Complete CF-Style Example 3 — Pair Extremes

Values:

```text
2,3,8,11
```

Goal:

```text
minimize maximum pair sum
```

Largest `11` must be paired.

Best available partner for controlling its pair sum:

```text
2
```

Pair:

```text
2+11 = 13
```

Remaining:

```text
3+8 = 11
```

Maximum:

```text
13
```

This is a bottleneck greedy.

---

# 13.33 Complete CF-Style Example 4 — Remove Most Expensive

Suppose while processing jobs you may keep a subset under total resource limit.

If adding a new job violates the limit, and all kept jobs contribute equally to count, removing the most expensive currently selected job is locally dominant:

```text
same number removed = 1
```

but removing the largest cost frees maximum resource.

This preserves the best chance of keeping more jobs later.

The exact correctness still depends on the full problem constraints, but this is the mathematical reason behind the candidate greedy.

---

# 13.34 Complete CF-Style Example 5 — Forced Left Boundary

Suppose operation at index `i` is the final opportunity to correct position `i`.

Then when processing `i`:

```text
current[i]
```

must be made equal to:

```text
target[i]
```

now.

You cannot say:

```text
"I'll fix it later."
```

because later operations do not affect it.

Thus the decision is forced.

This is one of the safest greedy structures to recognize.

---

# 13.35 Greedy Modeling Checklist

When optimization appears:

```text
1. WRITE THE OBJECTIVE.
   maximize what?
   minimize what?

2. WRITE THE CONSTRAINT.

3. WHAT RESOURCE IS SCARCE?

4. WHAT DOES ONE CHOICE CONSUME?

5. WHAT DOES ONE CHOICE GAIN?

6. ARE BENEFITS EQUAL?
   -> cheapest may dominate.

7. ARE COSTS EQUAL?
   -> largest benefit may dominate.

8. DOES SORTING REVEAL AN ORDER?

9. WHICH CHOICE LEAVES MOST FUTURE FREEDOM?

10. IS A BOUNDARY CHOICE FORCED?

11. CAN I EXCHANGE AN OPTIMAL CHOICE
    WITH MY GREEDY CHOICE?

12. AFTER EACH STEP, DOES GREEDY
    STAY AT LEAST AS GOOD?

13. TRY TO BUILD A TINY COUNTEREXAMPLE.

14. IF GREEDY FAILS:
    DP / BS / graph / other structure?
```

---

# 13.36 Common Mistakes

## Mistake 1 — "Take largest" without an objective

Largest what?

Why does it help?

What future opportunity does it preserve?

---

## Mistake 2 — Sorting and assuming correctness

Sorting is preprocessing.

The greedy rule still requires justification.

---

## Mistake 3 — Using ratio greedy for 0/1 choices

Fractional knapsack and 0/1 knapsack are different models.

---

## Mistake 4 — Ignoring ties/boundaries

For intervals and scheduling, equal endpoints can affect implementation.

Understand the mathematical compatibility rule first.

---

## Mistake 5 — Proving only that greedy looks good

You need something stronger:

```text
exchange
dominance
stay-ahead
forced choice
bottleneck
```

---

## Mistake 6 — Not searching for counterexamples

Before coding, attack your own greedy rule with tiny cases.

---

# 13.37 Translation Drills

Do not code.

---

## Drill 1

> Maximize number of items under budget. Every item counts as one.

Think:

```text
equal benefit
different costs
→ cheapest first
```

---

## Drill 2

> Choose exactly K independent actions. Every action costs one move and has different gain.

Think:

```text
equal cost
different benefit
→ largest gains
```

provided actions do not interact.

---

## Drill 3

> Maximum number of non-overlapping intervals.

Think:

```text
preserve future timeline
→ earliest finish
```

---

## Drill 4

> Largest element must be paired; minimize maximum pair sum.

Think:

```text
protect bottleneck
→ give largest the smallest partner
```

---

## Drill 5

> Later operations cannot change the current leftmost position.

Think:

```text
current decision is forced
```

---

# 13.38 Practice Set

For each problem write:

```text
OBJECTIVE:
CONSTRAINT:
SCARCE RESOURCE:
CANDIDATE GREEDY:
WHY IT PRESERVES FUTURE OPTIONS:
PROOF STYLE:
COUNTEREXAMPLE ATTEMPT:
```

---

## Practice A

> Costs `c[i]`, budget `B`. Buy maximum number of items.

---

## Practice B

> Select maximum number of mutually non-overlapping intervals.

---

## Practice C

> Pair all numbers to minimize the largest pair sum.

---

## Practice D

> Pick K independent rewards, each pick has equal cost.

---

## Practice E

> Candidate rule for 0/1 knapsack: sort by value/weight ratio. Should you trust it?

---

# 13.39 Practice Answers

## A

```text
sort costs ascending
take while total <= B
```

Proof:

```text
for any k,
the k cheapest items have
minimum possible total cost
among all k-item subsets.
```

---

## B

```text
sort by finishing endpoint
take earliest finishing compatible interval
```

Proof:

```text
exchange / future-space argument
```

with compatibility based on the statement's endpoint convention.

---

## C

Sort.

Pair:

```text
smallest with largest
```

then move inward.

Reason:

```text
largest is unavoidable;
smallest partner minimizes the bottleneck
created by that largest element.
```

---

## D

If picks are truly independent:

```text
take K largest rewards
```

because all costs are equal.

---

## E

No.

For indivisible 0/1 items, ratio greedy can fail.

You need another structure, commonly DP.

---

# 13.40 Chapter Mastery Test

You are ready for the next chapter when you no longer think:

```text
greedy = sort and pick
```

Instead:

```text
objective
   ↓
scarce resource / bottleneck
   ↓
candidate choice
   ↓
why does it leave no worse future?
   ↓
exchange / dominance /
stay-ahead / forced choice
```

You should also automatically try:

```text
Can I construct a 3-5 element
counterexample to my greedy?
```

before implementation.

---

# 13.41 Final Mental Engine

```text
              OPTIMIZATION PROBLEM
                       │
                       ▼
               WRITE OBJECTIVE
                       │
                       ▼
             WHAT LIMITS THE ANSWER?
                       │
          ┌────────────┼────────────┐
          │            │            │
        cost        bottleneck    boundary
          │            │            │
          ▼            ▼            ▼
      cheapest?    protect worst?  forced?
          │            │            │
          └────────────┼────────────┘
                       ▼
                SORT IF USEFUL
                       │
                       ▼
              CANDIDATE CHOICE
                       │
                       ▼
         CAN OPTIMAL SOLUTION EXCHANGE
          ITS CHOICE FOR MINE SAFELY?
                  /             \
                YES             NO
                 │               │
              GREEDY         find counterexample
                 │               │
                 ▼               ▼
             implement       DP / BS / other
```

The core habit:

```text
Never accept a greedy rule
because it feels natural.

Ask why choosing it now
cannot damage the best possible future.
```

---

# Next Chapter

```text
13. GREEDY MATHEMATICAL MODELING
                 ↓
14. PREFIX & DIFFERENCE MODELING
```

Chapter 14 will focus on translating global/range questions into accumulated states:

```text
prefix sum
range = prefix difference
subarray conditions
prefix frequencies
difference arrays
range updates
local changes → global reconstruction
```

The focus will be on understanding **why** prefix/difference representations work, not memorizing formulas.
