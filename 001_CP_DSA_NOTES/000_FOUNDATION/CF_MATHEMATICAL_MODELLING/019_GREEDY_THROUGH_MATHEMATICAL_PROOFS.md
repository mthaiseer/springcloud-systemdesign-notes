# Part 18 --- Greedy Through Mathematical Proofs

> **Goal:** stop choosing greedy actions because they "look best."
> Convert a greedy idea into a mathematical claim and prove that the
> local choice can be part of an optimal solution.
>
> **Core workflow:**
> `Objective → Candidate local choice → Mathematical proof → Greedy order → Implementation`
>
> **Recognition question:** **If I make the locally best choice now, can
> I prove that replacing any competing choice with mine never makes the
> final answer worse?**

## Table of Contents

-   [18.0 Greedy Mental Model](#180-greedy-mental-model)
-   [18.1 What Greedy Actually
    Requires](#181-what-greedy-actually-requires)
-   [18.2 Exchange Argument](#182-exchange-argument)
-   [18.3 Staying-Ahead Proof](#183-staying-ahead-proof)
-   [18.4 Lower Bound plus
    Construction](#184-lower-bound-plus-construction)
-   [18.5 Extremal Principle](#185-extremal-principle)
-   [18.6 Sorting as the Gateway to
    Greedy](#186-sorting-as-the-gateway-to-greedy)
-   [18.7 Choose Cheapest First](#187-choose-cheapest-first)
-   [18.8 Choose Earliest Finishing
    Interval](#188-choose-earliest-finishing-interval)
-   [18.9 Pair Extremes](#189-pair-extremes)
-   [18.10 Rearrangement and Pairing
    Proof](#1810-rearrangement-and-pairing-proof)
-   [18.11 Greedy by Marginal Gain](#1811-greedy-by-marginal-gain)
-   [18.12 Greedy by Marginal Cost](#1812-greedy-by-marginal-cost)
-   [18.13 Greedy with Deadlines](#1813-greedy-with-deadlines)
-   [18.14 Greedy with Limited
    Capacity](#1814-greedy-with-limited-capacity)
-   [18.15 Frequency Greedy](#1815-frequency-greedy)
-   [18.16 Greedy on Positive and Negative
    Contributions](#1816-greedy-on-positive-and-negative-contributions)
-   [18.17 Greedy with Invariants](#1817-greedy-with-invariants)
-   [18.18 Greedy Through
    Monotonicity](#1818-greedy-through-monotonicity)
-   [18.19 Greedy Construction](#1819-greedy-construction)
-   [18.20 When Greedy Fails](#1820-when-greedy-fails)
-   [18.21 How to Search for a
    Counterexample](#1821-how-to-search-for-a-counterexample)
-   [18.22 Greedy vs Dynamic
    Programming](#1822-greedy-vs-dynamic-programming)
-   [18.23 60-Second Greedy Discovery
    Workflow](#1823-60-second-greedy-discovery-workflow)
-   [18.24 Codeforces Recognition Map](#1824-codeforces-recognition-map)
-   [18.25 Greedy Proof Templates](#1825-greedy-proof-templates)
-   [18.26 Common Mistakes](#1826-common-mistakes)
-   [18.27 Fast Revision Card](#1827-fast-revision-card)

------------------------------------------------------------------------

## 18.0 Greedy Mental Model

Greedy means:

``` text
make one locally best choice
        ↓
never undo it
        ↓
continue on the remaining problem
```

But the important part is not the choice.

The important part is:

``` text
WHY is that choice safe?
```

### Why cheapest first?

For any fixed `k`, the cheapest possible set of `k` items is the first
`k` sorted prices.

So if even that set costs more than the budget, **no other `k` items can
fit**.

This is a proof, not a heuristic.

### Core model

``` text
GREEDY CHOICE
      +
SAFETY PROOF
      =
GREEDY ALGORITHM
```

### Real-World Example --- Buying groceries

You have `20` lei and want the maximum number of items.

``` text
prices = 9, 2, 7, 4, 6
sort   = 2, 4, 6, 7, 9

buy:
2 + 4 + 6 + 7 = 19  -> 4 items
```

**Idea:** cheapest choices preserve the most budget for future
purchases.

------------------------------------------------------------------------

## 18.1 What Greedy Actually Requires

A greedy algorithm usually needs two ideas.

### 1. Greedy-choice property

There exists an optimal solution containing the choice we want to make
now.

### 2. Optimal substructure

After fixing that choice, the remaining decisions form the same kind of
optimization problem.

### Example

Choose maximum non-overlapping meetings.

If the earliest-finishing meeting is safe:

``` text
choose it
```

Then discard all overlapping meetings.

What remains is again:

``` text
choose maximum non-overlapping meetings
```

### Real-World Example --- Booking appointments

A clinic wants to fit as many appointments as possible into one room.

``` text
safe first appointment
        ↓
room becomes free
        ↓
same scheduling problem remains
```

**Idea:** make a safe first choice, then solve the same smaller problem.

------------------------------------------------------------------------

## 18.2 Exchange Argument

This is one of the most useful greedy proofs.

### Template

Suppose:

``` text
G = greedy choice
O = choice used by some optimal solution
```

Show:

``` text
replace O with G
```

without making the solution worse.

Then an optimal solution exists that starts with `G`.

### Proof skeleton

``` text
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

### Real-World Example --- Meeting room swap

Suppose an optimal schedule starts with a meeting ending at `5`, but
greedy chooses one ending at `3`.

``` text
Optimal: [1------5]
Greedy : [1--3]

exchange:
[1------5]  ->  [1--3]
```

The room becomes free earlier, so no future meeting is lost.

``` text
same/better future options
        ↓
greedy choice is safe
```

------------------------------------------------------------------------

## 18.3 Staying-Ahead Proof

Sometimes compare the greedy solution with any competing solution after
every step.

Show:

``` text
greedy is never behind
```

### Example --- interval scheduling

Let greedy finishing times be:

``` text
g1, g2, g3, ...
```

and an optimal schedule's finishing times:

``` text
o1, o2, o3, ...
```

Prove inductively:

``` text
finish(gk) <= finish(ok)
```

for every `k`.

Why useful?

If greedy finishes its first `k` meetings no later than another
solution, greedy leaves at least as much room for future meetings.

### Real-World Example --- Delivery milestones

Two drivers make the same sequence of deliveries.

``` text
Delivery       Greedy     Other
1              10:00      10:30
2              11:15      12:00
3              12:30      13:30
```

After every delivery, Greedy is no later.

**Idea:** if you are never behind at any milestone, you never have less
remaining time.

------------------------------------------------------------------------

## 18.4 Lower Bound plus Construction

A powerful CP proof pattern:

``` text
1. prove nobody can beat X
2. construct a solution achieving X
3. therefore X is optimal
```

### CF mental trigger

When you think:

``` text
answer cannot be smaller/larger than X
```

immediately ask:

``` text
Can I construct exactly X?
```

### Real-World Example --- Splitting boxes between workers

There are `11` identical boxes and `2` workers. Minimize the largest
workload.

``` text
lower bound:
ceil(11 / 2) = 6

construction:
Worker A = 5
Worker B = 6
```

Nobody can achieve maximum load below `6`, and we can achieve exactly
`6`.

**Therefore:** optimum = `6`.

------------------------------------------------------------------------

## 18.5 Extremal Principle

Focus on an extreme object:

``` text
smallest
largest
leftmost
rightmost
earliest
latest
```

Extreme objects often have fewer possible interactions.

### Example

Suppose people stand on a line and must be paired to minimize certain
crossing behavior.

The leftmost person has no one further left.

This can simplify the first forced/safe decision.

### Modeling question

``` text
Which object has the fewest future options?
```

That object is often a good candidate for the next greedy decision.

### Real-World Example --- Airport check-in

Passengers have flights:

``` text
A -> 10:00
B -> 12:00
C -> 15:00
```

Passenger `A` has the earliest flight and the fewest future
opportunities.

``` text
most constrained
      ↓
handle first
```

**Idea:** inspect the extreme object because it often has the fewest
choices.

------------------------------------------------------------------------

## 18.6 Sorting as the Gateway to Greedy

Many greedy algorithms are:

``` text
sort by the quantity that defines "best next"
then scan
```

Possible keys:

``` text
smallest cost
largest gain
earliest finish
earliest deadline
largest requirement
smallest right endpoint
```

### Example

Unsorted costs:

``` text
8 3 5 1
```

After sorting:

``` text
1 3 5 8
```

The order exposes a monotone structure:

``` text
every next choice is at least as expensive
```

### Important warning

Sorting is not itself the proof.

You still need:

``` text
Why is this ordering safe?
```

### Real-World Example --- Shopping on a budget

You want the maximum number of snacks.

``` text
prices = 8, 3, 5, 1

sort:
1, 3, 5, 8
```

Sorting exposes the candidate greedy order.

**Important:** sorting is only the setup; you still prove why choosing
cheaper first is safe.

------------------------------------------------------------------------

## 18.7 Choose Cheapest First

Goal:

``` text
maximize number of purchases under budget B
```

Sort costs:

``` text
c1 <= c2 <= ... <= cn
```

For any `k` chosen items:

``` text
their total cost >= c1+c2+...+ck
```

Therefore the cheapest `k` items are the minimum-cost way to buy `k`
items.

### Example

``` text
B=15
costs=[8,1,6,3,5]
```

Sort:

``` text
1,3,5,6,8
```

Prefix:

``` text
1
4
9
15
23
```

Maximum affordable count:

``` text
4
```

### Proof

If another solution buys `k` items and contains an item more expensive
than an unchosen cheaper item, swap them.

Cost cannot increase.

Repeat until the chosen set is the cheapest `k`.

This is an exchange argument.

### Real-World Example --- Buying school supplies

Budget:

``` text
B = 15
prices = 1, 3, 5, 6, 8
```

Suppose you chose `8` while `6` is unchosen:

``` text
8 -> 6

same number of items
less money spent
```

Repeat such exchanges until the chosen items are the cheapest ones.

------------------------------------------------------------------------

## 18.8 Choose Earliest Finishing Interval

Classic interval scheduling:

``` text
maximize number of non-overlapping intervals
```

Greedy:

``` text
sort by ending time
take the earliest finishing compatible interval
```

### Example

``` text
A=[1,4]
B=[2,3]
C=[3,5]
D=[5,7]
```

Sort by finish:

``` text
B [2,3]
A [1,4]
C [3,5]
D [5,7]
```

Choose:

``` text
B -> C -> D
```

Count:

``` text
3
```

### Why earliest finish?

If an optimal solution begins with an interval ending later than
greedy's first interval, replace it with greedy's.

Greedy leaves at least as much remaining timeline.

So the replacement cannot reduce future options.

### Real-World Example --- Scheduling a meeting room

Meetings:

``` text
A = [1,5]
B = [1,3]
C = [3,4]
D = [4,6]
```

Choose the meeting finishing earliest:

``` text
B -> C -> D
```

**Idea:** finishing earlier frees the room sooner and leaves maximum
room for future meetings.

------------------------------------------------------------------------

## 18.9 Pair Extremes

Sorted values:

``` text
a1 <= a2 <= ... <= an
```

Many pairing problems become:

``` text
smallest with largest
```

or:

``` text
adjacent with adjacent
```

depending on the objective.

### Example --- balance pair sums

``` text
[1,2,8,9]
```

Pair extremes:

``` text
1+9=10
2+8=10
```

Maximum pair sum:

``` text
10
```

Alternative:

``` text
1+2=3
8+9=17
```

Maximum:

``` text
17
```

### Exchange intuition

Pairing two large elements together concentrates cost.

Moving one large element to a smaller partner tends to balance extremes.

### Real-World Example --- Balancing luggage carts

Bag weights:

``` text
1, 2, 8, 9
```

Two bags per cart. Pair light with heavy:

``` text
1 + 9 = 10
2 + 8 = 10
```

Instead of:

``` text
1 + 2 = 3
8 + 9 = 17
```

**Idea:** pairing extremes prevents heavy items from concentrating
together.

------------------------------------------------------------------------

## 18.10 Rearrangement and Pairing Proof

For sorted:

``` text
a <= b
x <= y
```

Compare two pairings.

Same-direction product:

``` text
ax + by
```

cross pairing:

``` text
ay + bx
```

Difference:

``` text
(ax+by)-(ay+bx)

= ax-ay+by-bx
= a(x-y)+b(y-x)
= (b-a)(y-x)
>= 0
```

Therefore:

``` text
ax+by >= ay+bx
```

### Meaning

To maximize sum of products:

``` text
pair small with small
pair large with large
```

To minimize:

``` text
pair small with large
```

### Real-World Example --- Workers and machines

Workers have efficiencies `2,5`; machines have powers `3,8`.

``` text
same order:
2*3 + 5*8 = 46

cross:
2*8 + 5*3 = 31
```

**Idea:** when maximizing product contribution, match strong with strong
and weak with weak.

------------------------------------------------------------------------

## 18.11 Greedy by Marginal Gain

Sometimes each action gives a benefit.

If actions are independent and each costs the same resource, choose
largest gains first.

### Example

You may perform exactly `3` upgrades.

Benefits:

``` text
[4,10,2,7,6]
```

Sort descending:

``` text
10,7,6,4,2
```

Take:

``` text
10+7+6=23
```

### Exchange proof

Suppose a chosen gain is:

``` text
x
```

and an unchosen gain is:

``` text
y > x
```

Swap:

``` text
new total = old total - x + y
```

Since:

``` text
y-x > 0
```

the objective improves.

Therefore an optimum cannot exclude a larger independent gain while
including a smaller one.

### Warning

This fails if actions interact or have different costs/constraints.

### Real-World Example --- Choosing advertisements

You have exactly `3` advertising slots.

``` text
gains = 4, 10, 2, 7, 6
```

Choose:

``` text
10 + 7 + 6 = 23
```

If `4` is selected while `7` is not:

``` text
4 -> 7
```

Same number of slots, larger total gain.

------------------------------------------------------------------------

## 18.12 Greedy by Marginal Cost

Dual idea:

``` text
when every selected action gives equal required progress,
take smallest cost first
```

### Example

Need to complete any `3` independent jobs.

Costs:

``` text
9,2,6,3,8
```

Sort:

``` text
2,3,6,8,9
```

Take:

``` text
2+3+6=11
```

### Proof

Any other set of three has total cost at least the sum of the three
smallest costs.

### Real-World Example --- Choosing three errands

You must complete any `3` independent errands.

``` text
costs = 9, 2, 6, 3, 8
```

Choose:

``` text
2 + 3 + 6 = 11
```

**Idea:** when every completed errand counts equally, use the cheapest
ones.

------------------------------------------------------------------------

## 18.13 Greedy with Deadlines

Deadlines often suggest ordering by:

``` text
earliest deadline
```

but the exact greedy depends on the problem.

### Unit-time job example

Each job takes one slot and must be done by its deadline.

Jobs:

``` text
A deadline 1
B deadline 2
C deadline 2
```

If all are required, schedule the most constrained deadline first:

``` text
slot1 A
slot2 B
```

Only two slots exist by deadline 2, so all three cannot fit.

### Feasibility condition

After sorting deadlines:

``` text
d1 <= d2 <= ... <= dn
```

for unit jobs, a schedule is feasible when:

``` text
i <= di
```

for every 1-based position `i`.

### Important

Do not memorize "deadline =\> sort by deadline" blindly. Profit/deadline
variants may need a heap or another proof.

### Real-World Example --- Airport passengers with deadlines

Each check-in takes one equal time slot.

``` text
A -> deadline 10:00
B -> deadline 14:00
C -> deadline 11:00
D -> deadline 16:00
E -> deadline 12:00
```

Candidate order:

``` text
A -> C -> E -> B -> D
```

**Idea:** the earliest deadline is the most constrained. This rule
depends on the exact scheduling objective.

------------------------------------------------------------------------

## 18.14 Greedy with Limited Capacity

When resources are limited, identify what should occupy scarce capacity.

### Example --- keep best `k`

Values arrive:

``` text
4,9,2,8,7
```

Capacity:

``` text
k=3
```

If the goal is maximum total value and items are independent/equal size:

``` text
keep 9,8,7
```

Sum:

``` text
24
```

### Online implementation idea

Maintain selected values.

If more than `k`:

``` text
remove smallest selected value
```

### Proof

If selected set contains `x` while an unselected `y>x` exists:

``` text
replace x by y
```

Capacity stays the same and total improves.

### Real-World Example --- Featured products

A website has only `3` featured-product slots.

``` text
values = 4, 9, 2, 8, 7
capacity = 3
```

Keep:

``` text
9, 8, 7
```

If selected `4` while unselected `7` exists:

``` text
4 -> 7
```

Capacity stays unchanged while value increases.

------------------------------------------------------------------------

## 18.15 Frequency Greedy

Sometimes individual elements are irrelevant; only frequencies matter.

### Example --- remove minimum elements to make all remaining values equal

``` text
[1,1,1,2,2,3]
```

Frequencies:

``` text
1 -> 3
2 -> 2
3 -> 1
```

Keep the most frequent value:

``` text
3 copies of 1
```

Remove:

``` text
6-3=3
```

### Mathematical proof

If final value is `v`, removals are:

``` text
n-freq[v]
```

Minimize:

``` text
n-freq[v]
```

Equivalent to maximize:

``` text
freq[v]
```

Therefore:

``` text
answer = n-maxFrequency
```

### Real-World Example --- Standardizing office laptops

Laptop configurations:

``` text
Windows  -> 6
Linux    -> 2
macOS    -> 1
```

If all remaining laptops must use one existing configuration and
removals cost equally:

``` text
keep Windows
remove 2 + 1 = 3
```

**Idea:** preserve the most frequent state to minimize removals.

------------------------------------------------------------------------

## 18.16 Greedy on Positive and Negative Contributions

If choices contribute independently to a sum and selection is optional:

``` text
positive contribution -> take
negative contribution -> reject
zero -> neutral
```

### Example

Optional projects have profits:

``` text
[8,-3,5,-10,2]
```

Choose any subset to maximize total profit.

Take:

``` text
8+5+2=15
```

### Proof

For a negative `x`:

``` text
S+x < S
```

so including it worsens the objective.

For positive `x`:

``` text
S+x > S
```

so excluding it wastes gain.

### Important

This only works when choices are independent. Constraints like "choose
exactly k" change the model.

### Real-World Example --- Optional freelance jobs

Independent jobs have profits:

``` text
+800, -300, +500, -1000, +200
```

Choose:

``` text
+800 +500 +200 = 1500
```

Skip negative-profit jobs.

**Idea:** when choices are independent and optional, positive
contributions help and negative contributions hurt.

------------------------------------------------------------------------

## 18.17 Greedy with Invariants

An invariant can make a greedy move safe because all valid solutions
must preserve the same quantity.

### Example pattern

Suppose every operation reduces total remaining work by exactly one.

Then:

``` text
number of operations
```

may be fixed regardless of order.

If order only affects feasibility, greedy can focus on preserving future
feasibility rather than minimizing operation count.

### Contest question

Before choosing greedily ask:

``` text
Which quantities are already fixed for every solution?
```

Then optimize only what actually varies.

### Real-World Example --- Delivering five packages

A driver must deliver exactly `5` packages.

``` text
A B C D E

number of deliveries = 5  <- invariant
```

Changing the order cannot reduce `5`.

``` text
fixed    -> number of deliveries
variable -> order / deadline violations
```

**Idea:** stop optimizing what cannot change; optimize only what varies.

------------------------------------------------------------------------

## 18.18 Greedy Through Monotonicity

A greedy action is often safe because it moves the state monotonically
toward the goal.

Examples:

``` text
remaining budget only decreases
current rightmost covered point only increases
chosen finish times only increase
unprocessed set only shrinks
```

### Example --- cover points with fixed-length intervals

Sorted points:

``` text
1,2,3,8,9
```

Suppose one interval covers length `2`.

Take the leftmost uncovered point:

``` text
1
```

Place interval as far right as possible while covering it:

``` text
[1,3]
```

This covers:

``` text
1,2,3
```

Next uncovered:

``` text
8
```

Place:

``` text
[8,10]
```

Total:

``` text
2 intervals
```

### Proof intuition

The leftmost uncovered point must be covered by some interval.

Pushing that interval rightward as far as allowed cannot lose any point
to its left that still needs coverage, because none exists.

It can only help cover more future points.

### Real-World Example --- Wi-Fi routers along a hallway

Rooms are located at:

``` text
1, 2, 3, 8, 9
```

One router covers an interval of length `2`.

``` text
leftmost uncovered = 1
place coverage      = [1,3]

next uncovered      = 8
place coverage      = [8,10]
```

Progress:

``` text
rightmost covered:
0 -> 3 -> 10

only moves RIGHT
```

**Idea:** once earlier rooms are covered, pushing coverage rightward
cannot hurt them and can only help future rooms.

------------------------------------------------------------------------

## 18.19 Greedy Construction

Constructive problems often allow choosing any valid answer.

Greedy can maintain an invariant after every placement.

### Example --- build a binary string with no adjacent `1`

Suppose you have:

``` text
zeros=4
ones=3
```

One safe arrangement:

``` text
1 0 1 0 1 0 0
```

### Slot model

Place zeros first:

``` text
_ 0 _ 0 _ 0 _ 0 _
```

There are:

``` text
zeros+1 = 5
```

slots where ones can be placed without adjacency if at most one goes
into each slot.

Condition:

``` text
ones <= zeros+1
```

### Greedy construction

Place each `1` into a separate available gap.

### Proof

The slot bound is necessary, and the construction achieves it.

Again:

``` text
bound + construction = proof
```

### Real-World Example --- Seating groups with separators

Suppose noisy groups `N` cannot sit next to each other. Place quiet
groups `Q` first:

``` text
_ Q _ Q _ Q _ Q _
```

Then place each `N` in a different gap:

``` text
N Q N Q N Q Q
```

After every placement:

``` text
no adjacent N
```

**Idea:** each greedy construction step preserves the required
invariant.

------------------------------------------------------------------------

## 18.20 When Greedy Fails

A locally best-looking action may block a better future.

### Counterexample --- largest value first under capacity

Capacity:

``` text
10
```

Items `(weight,value)`:

``` text
A=(10,10)
B=(6,9)
C=(4,9)
```

Greedy by largest individual value chooses:

``` text
A -> value 10
```

But:

``` text
B+C
weight=10
value=18
```

is better.

### Why greedy failed

Choices interact through capacity.

Taking one item changes which combinations remain possible.

### Warning signs

``` text
different costs
future compatibility
exact totals
choices interact
local action destroys options
```

These often suggest:

``` text
DP
search
matching
flow
```

rather than simple greedy.

### Real-World Example --- Loading a van

Van capacity = `10`.

``` text
A = (weight 10, value 10)
B = (weight  6, value  9)
C = (weight  4, value  9)
```

Largest-value-first chooses:

``` text
A -> value 10
```

But:

``` text
B + C -> weight 10, value 18
```

**Idea:** the locally attractive choice can destroy a better
combination.

------------------------------------------------------------------------

## 18.21 How to Search for a Counterexample

Before trusting a greedy rule, attack it.

### Method

Try tiny cases with:

``` text
3-5 elements
ties
one huge value
many medium values
one very cheap item
one expensive high-gain item
tight capacity
```

### Example candidate rule

``` text
"always choose largest value first"
```

Try:

``` text
capacity=10

(10,10)
(6,9)
(4,9)
```

Greedy:

``` text
10
```

Optimal:

``` text
18
```

Rule disproved.

### Contest habit

Spend 30--60 seconds trying to break your greedy before coding it.

A counterexample is cheaper than a wrong submission.

### Real-World Example --- Testing a shopping rule

Candidate rule:

``` text
"Always take the most valuable item first."
```

Attack it with a tiny case:

``` text
capacity = 10

(10,10)
(6,9)
(4,9)
```

Greedy gets `10`; the last two together give `18`.

**Idea:** a tiny counterexample is enough to kill a false greedy rule.

------------------------------------------------------------------------

## 18.22 Greedy vs Dynamic Programming

Ask:

``` text
Does the locally optimal choice eliminate the need to reconsider alternatives?
```

If yes and provable:

``` text
greedy
```

If the best future depends on which earlier choice was made:

``` text
DP may be needed
```

### Greedy-style state

``` text
one canonical best partial state is enough
```

### DP-style state

``` text
multiple partial states must survive
because each may lead to a different future optimum
```

### Example

Buying maximum count with budget and equal value per item:

``` text
cheapest first -> greedy
```

0/1 knapsack with different values:

``` text
(weight,value) interactions -> DP
```

### Real-World Example --- Choosing meals under a budget

If every meal gives equal benefit and you want maximum count:

``` text
cheapest first -> Greedy
```

If meals have different prices and nutrition values:

``` text
(price, nutrition)
```

one cheap choice may block a better combination.

``` text
many useful partial choices survive
        ↓
DP may be needed
```

**Idea:** greedy keeps one canonical state; DP keeps multiple competing
states.

------------------------------------------------------------------------

## 18.23 60-Second Greedy Discovery Workflow

``` text
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

``` text
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

### Real-World Example --- Planning meeting-room bookings

You want the maximum number of meetings.

``` text
objective        -> maximize count
candidate        -> earliest finish
try to break it  -> tiny overlaps
proof            -> exchange / staying ahead
then             -> implement
```

**Idea:** use this as a contest checklist before coding a greedy
solution.

------------------------------------------------------------------------

## 18.24 Codeforces Recognition Map

  -----------------------------------------------------------------------
  Statement clue                      Candidate greedy model
  ----------------------------------- -----------------------------------
  maximize count under budget         cheapest first

  maximize independent gains with     largest gain first
  fixed number chosen                 

  maximum non-overlapping intervals   earliest finish

  balance pair extremes               sort + pair extremes

  maximize sum of products            same-order pairing

  minimize sum of products            opposite-order pairing

  limited `k` slots                   keep best `k`

  make all values equal by removals   keep max frequency

  optional independent contributions  take positive contributions

  leftmost uncovered point            cover it as far right as possible

  earliest deadline / most            process constrained extreme first
  constrained item                    

  prove local choice safe             exchange argument

  prove greedy never falls behind     staying-ahead

  derive unavoidable bound and attain bound + construction
  it                                  

  local rule seems plausible but      counterexample / DP check
  interactions exist                  
  -----------------------------------------------------------------------

### Real-World Example --- Recognizing a shopping story

Statement:

``` text
"You have limited money and want
to buy as many tickets as possible."
```

Remove the story:

``` text
maximize count under budget
        ↓
candidate pattern
        ↓
cheapest first
```

**Idea:** translate narrative clues into a known mathematical greedy
form.

------------------------------------------------------------------------

## 18.25 Greedy Proof Templates

### Template A --- Exchange argument

``` text
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

### Template B --- Staying ahead

``` text
After k decisions:

prove greedy_state(k)
is at least as good as
optimal_state(k)

Base case: k=1
Induction: assume true for k
prove for k+1

Therefore greedy cannot finish worse.
```

### Template C --- Lower bound + construction

``` text
Every solution must have answer >= X.
(or <= X for maximization)

Greedy constructs a solution with answer = X.

Therefore X is optimal.
```

### Template D --- Extremal choice

``` text
Take the most constrained/extreme object E.

Show:
any valid solution must handle E.

Choose the way of handling E that preserves
the maximum freedom for remaining objects.

Reduce to smaller instance.
```

### Template E --- Swap inversion

``` text
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

### Real-World Example --- Choosing the proof for meetings

Candidate rule: choose the earliest-finishing meeting.

``` text
Swap a later meeting with it?
-> Exchange

After kth meeting, always finish earlier?
-> Staying Ahead

Prove unavoidable bound and attain it?
-> Bound + Construction
```

**Idea:** the greedy rule tells you *what* to do; the proof template
tells you *why it works*.

------------------------------------------------------------------------

## 18.26 Common Mistakes

### 1. "It seems optimal" is not a proof

Always identify:

``` text
exchange
staying ahead
bound + construction
extremal
monotonicity
invariant
```

### 2. Sorting without explaining the key

``` text
sort ascending
```

is an implementation step, not mathematical justification.

### 3. Optimizing the wrong local quantity

Largest value is not necessarily best when costs differ.

### 4. Ignoring future feasibility

A local gain may destroy many future choices.

### 5. Assuming independence

Check whether choosing `x` changes the value or feasibility of choosing
`y`.

### 6. Not testing tiny counterexamples

Try adversarial small inputs before coding.

### 7. Confusing a necessary condition with sufficient proof

Showing:

``` text
greedy choice looks necessary
```

is not enough unless you show a complete optimal construction or
exchange.

### 8. Using a known greedy from a different variant

Small changes such as:

``` text
weighted intervals
different job durations
exactly k selections
```

can invalidate the original proof.

### Real-World Example --- Nearest-delivery mistake

A driver proposes:

``` text
"Always deliver the nearest package next."
```

But the nearest package may send the driver away from several urgent
packages.

``` text
looks best now
      !=
provably best overall
```

**Idea:** check future feasibility and interactions before trusting a
local rule.

------------------------------------------------------------------------

## 18.27 Fast Revision Card

``` text
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

### Real-World Example --- Everyday greedy memory card

When planning errands:

``` text
CHEAPEST?      -> preserve budget
EARLIEST?      -> free time sooner
EXTREME?       -> handle most constrained
SWAP SAFE?     -> exchange proof
NEVER BEHIND?  -> staying ahead
FIXED?         -> invariant
ONLY FORWARD?  -> monotonicity
```

**Idea:** identify both the greedy action and the reason that makes it
safe.
