# Competitive Programming Greedy Pattern Master Guide
## Codeforces / CSES / CodeChef A-B-C Pattern Recognition Handbook

**Goal:** make common greedy patterns recognizable during contests, especially CF/CodeChef A/B/C.

Greedy is not “always take the biggest” or “always take the smallest.”

A correct greedy solution needs:

```text
1. a local choice,
2. a reason that choice never hurts the global optimum,
3. a proof idea such as exchange argument / dominance / invariant / earliest finish.
```

---

# Table of Contents

1. [How to Recognize Greedy](#1-how-to-recognize-greedy)
2. [Sort + Take Best First](#2-sort--take-best-first)
3. [Sort + Pair Extremes](#3-sort--pair-extremes)
4. [Earliest Finish Time](#4-earliest-finish-time)
5. [Take Smallest Cost / Cheapest First](#5-take-smallest-cost--cheapest-first)
6. [Take Largest Gain / Biggest Benefit First](#6-take-largest-gain--biggest-benefit-first)
7. [Greedy by Difference / Opportunity Cost](#7-greedy-by-difference--opportunity-cost)
8. [Prefix Feasibility Greedy](#8-prefix-feasibility-greedy)
9. [Maintain Best Candidates with Heap](#9-maintain-best-candidates-with-heap)
10. [Interval Covering Greedy](#10-interval-covering-greedy)
11. [Two Pointers as Greedy](#11-two-pointers-as-greedy)
12. [Greedy Construction](#12-greedy-construction)
13. [Greedy with Counts / Frequencies](#13-greedy-with-counts--frequencies)
14. [Exchange Argument](#14-exchange-argument)
15. [Dominance and “Never Worse” Choices](#15-dominance-and-never-worse-choices)
16. [Greedy + Sorting by Custom Comparator](#16-greedy--sorting-by-custom-comparator)
17. [Greedy + Invariant](#17-greedy--invariant)
18. [Greedy + Binary Search](#18-greedy--binary-search)
19. [Greedy + Math](#19-greedy--math)
20. [When Greedy Fails](#20-when-greedy-fails)
21. [A/B/C Recognition Table](#abc-recognition-table)
22. [60-Second Greedy Checklist](#60-second-greedy-checklist)
23. [Practice Strategy](#practice-strategy)

---

# 1. How to Recognize Greedy

## Typical statement clues

Think greedy when you see:

```text
minimum operations
maximum number of items
schedule as many as possible
pair elements
choose tasks/items
remove something repeatedly
process in some order
construct any valid sequence
choose locally among available options
```

But do **not** conclude greedy only from wording.

Ask:

```text
If I make the locally best choice now,
can a better global solution require undoing it later?
```

If no, greedy may work.

## Core proof styles

### Exchange argument

Show:

```text
optimal solution
    |
replace its first choice with greedy choice
    |
solution is no worse
```

Then greedy can be part of some optimal solution.

### Staying ahead

Show greedy is at least as good after every prefix.

### Dominance

Choice A is never worse than choice B under all future continuations.

### Invariant

After every greedy step, maintain a property guaranteeing eventual optimality.

---

# 2. Sort + Take Best First

## Core idea

If items are independent and you want the best `k` contributions, sort by value.

Example:

```text
maximize sum of k elements
```

obvious greedy:

```text
take k largest
```

## Why it works

Suppose solution contains `x` but excludes larger `y`.

Swap:

```text
remove x
add y
```

sum never decreases.

Therefore an optimal solution exists using the largest elements.

## Dry run

```text
values = [3, 8, 2, 9, 5]
k = 3

sorted:
9 8 5 3 2

take:
9 + 8 + 5 = 22
```

## C++ pattern

```cpp
sort(a.rbegin(), a.rend());

long long ans = 0;
for (int i = 0; i < k; ++i)
    ans += a[i];
```

## Common trap

If selecting one item changes the value/availability of others, this may fail.

## Practice

1. CF 1353B — Two Arrays And Swaps  
   https://codeforces.com/problemset/problem/1353/B  
   Hint: sort one ascending, one descending; improve only when beneficial.

2. CF 1606A — AB Balance  
   https://codeforces.com/problemset/problem/1606/A  
   Hint: tiny construction after observing endpoint effect.

3. CF 1538B — Friends and Candies  
   https://codeforces.com/problemset/problem/1538/B  
   Hint: average first, then count excess contributors.

4. CSES — Apartments  
   https://cses.fi/problemset/task/1084  
   Hint: sorting enables local matching decisions.

5. CSES — Ferris Wheel  
   https://cses.fi/problemset/task/1090  
   Hint: sort before pairing extremes.

6. CSES — Movie Festival  
   https://cses.fi/problemset/task/1629  
   Hint: sort by finish time, not start time.

---

# 3. Sort + Pair Extremes

## Core idea

A very common greedy pattern:

```text
smallest with largest
```

or sometimes:

```text
largest with second largest
```

depending on objective.

## Recognition clues

```text
pair people/items
capacity
sum <= X
minimize number of groups
maximize valid pairs
```

## Worked example — Ferris Wheel style

Weights:

```text
2 3 7 9
limit = 10
```

Try heaviest `9`.

Can it pair with lightest `2`?

```text
9+2 = 11 > 10
```

Then `9` cannot pair with anyone, because everyone else is heavier.

So:

```text
9 rides alone
```

Now `7+2=9`, so pair them.

### Key greedy proof

If:

```text
heaviest + lightest > limit
```

then:

```text
heaviest + anyone >= heaviest + lightest > limit
```

Thus heaviest must be alone.

That is a **dominance proof**.

## C++

```cpp
sort(a.begin(), a.end());

int i = 0, j = n - 1;
int ans = 0;

while (i <= j) {
    if (i < j && a[i] + a[j] <= x)
        ++i;

    --j;
    ++ans;
}
```

## Practice

1. CSES — Ferris Wheel  
   https://cses.fi/problemset/task/1090

2. CSES — Apartments  
   https://cses.fi/problemset/task/1084

3. CF 1399C — Boat Competition  
   https://codeforces.com/problemset/problem/1399/C

4. CF 1538C — Challenging Cliffs  
   https://codeforces.com/problemset/problem/1538/C

5. CF 1353B — Two Arrays And Swaps  
   https://codeforces.com/problemset/problem/1353/B

6. CF 1535B — Array Reodering  
   https://codeforces.com/problemset/problem/1535/B

---

# 4. Earliest Finish Time

## Core idea

For maximum number of non-overlapping intervals:

```text
sort by finishing time
take the interval that finishes earliest
```

## Why not earliest start?

Example:

```text
A: [1,10]
B: [2,3]
C: [4,5]
D: [6,7]
```

Earliest start picks `A` and blocks everything.

Earliest finish picks:

```text
B, C, D
```

## Exchange argument

Suppose optimal first interval is `O`, greedy first is `G`.

Because:

```text
finish(G) <= finish(O)
```

replace `O` by `G`.

Every later interval that starts after `O` ends also starts after `G` ends.

So replacement does not reduce the number of intervals.

Therefore greedy is safe.

## ASCII

```text
time --->

A: [----------]
B:   [-]
C:      [-]
D:         [-]

Earliest finish:
B -> C -> D
```

## C++

```cpp
sort(v.begin(), v.end(), [](auto &a, auto &b) {
    return a.second < b.second;
});

int ans = 0;
long long lastEnd = LLONG_MIN;

for (auto [s,e] : v) {
    if (s >= lastEnd) {
        ++ans;
        lastEnd = e;
    }
}
```

## Practice

1. CSES — Movie Festival  
   https://cses.fi/problemset/task/1629

2. CSES — Movie Festival II  
   https://cses.fi/problemset/task/1632

3. CF 1249C2 — Good Numbers (Hard Version)  
   https://codeforces.com/problemset/problem/1249/C2  
   Hint: different domain, but similar “take next safe representation” greedy.

4. CF 1428C — ABBB  
   https://codeforces.com/problemset/problem/1428/C  
   Hint: remove immediately when a valid local cancellation appears.

5. CF 1352D — Alice, Bob and Candies  
   https://codeforces.com/problemset/problem/1352/D

---

# 5. Take Smallest Cost / Cheapest First

## Core idea

When each chosen item gives the same benefit and costs differ:

```text
take cheapest first
```

## Proof

If solution chooses cost `c2` but skips cheaper `c1 <= c2`, swapping:

```text
c2 -> c1
```

keeps benefit and never increases total cost.

## Recognition clues

```text
same reward
minimum cost
buy as many as possible
budget
each item contributes one unit
```

## Dry run

Budget:

```text
10
```

Costs:

```text
6 1 4 2
```

Sort:

```text
1 2 4 6
```

Take:

```text
1+2+4 = 7
```

cannot add 6.

Count = 3.

## Practice

1. CSES — Tasks and Deadlines  
   https://cses.fi/problemset/task/1630  
   Hint: process shorter durations first for reward objective.

2. CF 1499B — Binary Removals  
   https://codeforces.com/problemset/problem/1499/B

3. CF 1519A — Red and Blue Beans  
   https://codeforces.com/problemset/problem/1519/A

4. CF 1530A — Binary Decimal  
   https://codeforces.com/problemset/problem/1530/A

5. CF 1703C — Cypher  
   https://codeforces.com/problemset/problem/1703/C

---

# 6. Take Largest Gain / Biggest Benefit First

## Core idea

If each action has independent gain and a fixed number of actions is allowed:

```text
take largest positive gains first
```

## Gain transformation

Often original statement is complicated, but compute:

```text
gain = new_value - old_value
```

Then sort gains.

Example:

```text
current -> upgraded

4 -> 9 gain 5
8 -> 10 gain 2
3 -> 20 gain 17
```

If only two upgrades allowed:

```text
17 + 5
```

## This is a crucial contest transformation

Instead of sorting original values, sort **marginal improvements**.

## Practice

1. CF 1353B — Two Arrays And Swaps  
   https://codeforces.com/problemset/problem/1353/B

2. CF 1519B — The Cake Is a Lie  
   https://codeforces.com/problemset/problem/1519/B

3. CF 1374D — Zero Remainder Array  
   https://codeforces.com/problemset/problem/1374/D  
   Hint: transform each element into required adjustment.

4. CSES — Tasks and Deadlines  
   https://cses.fi/problemset/task/1630

5. CSES — Stick Lengths  
   https://cses.fi/problemset/task/1074  
   Hint: objective transformation leads to median, an extremal greedy fact.

---

# 7. Greedy by Difference / Opportunity Cost

## Core idea

Sometimes each item can go to one of two choices.

For item `i`:

```text
option A gives a[i]
option B gives b[i]
```

What matters is not the absolute values but:

```text
difference = a[i] - b[i]
```

Items with largest preference for A should go to A first.

## Why

If one item loses much more by assigning it to B, protect it from that loss.

## Example

```text
item 1: A=10 B=9  diff=1
item 2: A=20 B=3  diff=17
```

If only one item can choose A:

Choose item 2 for A.

Total:

```text
20 + 9 = 29
```

instead of:

```text
10 + 3 = 13
```

## Practice

1. CF 1353B — Two Arrays And Swaps  
   https://codeforces.com/problemset/problem/1353/B

2. CF 1538C — Challenging Cliffs  
   https://codeforces.com/problemset/problem/1538/C

3. CSES — Tasks and Deadlines  
   https://cses.fi/problemset/task/1630

4. CSES — Stick Lengths  
   https://cses.fi/problemset/task/1074

5. CF 1428D — Bouncing Boomerangs  
   https://codeforces.com/problemset/problem/1428/D  
   Hint: process categories in an order where scarce compatibility matters.

---

# 8. Prefix Feasibility Greedy

## Core idea

Sometimes processing left-to-right is natural because earlier decisions affect what remains.

Typical pattern:

```text
while scanning:
if current prefix becomes invalid,
fix it immediately using the best available local action
```

## Recognition clues

```text
prefix must satisfy
balance cannot become negative
avoid forbidden prefix
construct sequence left to right
```

## Example — bracket-like idea

If a prefix becomes invalid:

```text
balance < 0
```

you must repair now because future characters cannot change the fact that this prefix was invalid.

That “must repair now” creates greedy safety.

## Practice

1. CF 1515A — Phoenix and Gold  
   https://codeforces.com/problemset/problem/1515/A

2. CF 1428C — ABBB  
   https://codeforces.com/problemset/problem/1428/C

3. CF 1506C — Double-ended Strings  
   https://codeforces.com/problemset/problem/1506/C

4. CSES — Restaurant Customers  
   https://cses.fi/problemset/task/1619

5. CSES — Traffic Lights  
   https://cses.fi/problemset/task/1163

---

# 9. Maintain Best Candidates with Heap

## Core idea

When scanning items in order, sometimes you tentatively take items, but if capacity/constraint is violated, remove the worst selected item.

Pattern:

```text
take current
push into heap
if invalid:
    remove worst
```

This is one of the most powerful greedy templates.

## Why it works

At every prefix, maintain the best feasible subset among items seen so far.

## Typical clue

```text
maximize number selected
deadlines
capacity
prefix constraints
can replace earlier choice
```

## Generic template

```cpp
priority_queue<long long> pq;
long long sum = 0;

for (auto x : items) {
    pq.push(x);
    sum += x;

    if (sum > limit) {
        sum -= pq.top();
        pq.pop();
    }
}
```

Here max-heap removes the largest cost.

## Practice

1. CSES — Movie Festival II  
   https://cses.fi/problemset/task/1632

2. CSES — Tasks and Deadlines  
   https://cses.fi/problemset/task/1630

3. CF 1526C2 — Potions (Hard Version)  
   https://codeforces.com/problemset/problem/1526/C2  
   Hint: accept everything, remove the worst potion when health goes negative.

4. CF 1526C1 — Potions (Easy Version)  
   https://codeforces.com/problemset/problem/1526/C1

5. CSES — Concert Tickets  
   https://cses.fi/problemset/task/1091

---

# 10. Interval Covering Greedy

## Core idea

For covering a line or target interval:

```text
among intervals starting before current position,
choose the one reaching farthest right
```

## Why

If several intervals can cover the current uncovered point, the one extending farthest leaves the easiest remaining suffix.

## ASCII

```text
current = x

I1: [x----]
I2: [x----------]
I3: [x------]

choose I2
```

Any solution must choose some interval covering `x`.
Replacing it with farther-reaching `I2` cannot hurt.

## Recognition clues

```text
cover whole range
minimum intervals
minimum sprinklers
minimum segments
```

## Practice

1. CSES — Movie Festival  
   https://cses.fi/problemset/task/1629  
   (scheduling cousin of covering)

2. CSES — Restaurant Customers  
   https://cses.fi/problemset/task/1619

3. CF 1537C — Challenging Cliffs  
   https://codeforces.com/problemset/problem/1537/C

4. CF 1428D — Bouncing Boomerangs  
   https://codeforces.com/problemset/problem/1428/D

5. CF 1343C — Alternating Subsequence  
   https://codeforces.com/problemset/problem/1343/C  
   Hint: each sign-block is an interval-like independent region; take its best representative.

---

# 11. Two Pointers as Greedy

## Core idea

After sorting, move pointers based on a locally forced decision.

Typical layout:

```text
L = smallest
R = largest
```

Ask whether they can be combined.

## Forced-choice logic

If largest cannot pair with smallest:

```text
largest cannot pair with anyone
```

therefore decision is forced.

That is greedy.

## Practice

1. CSES — Ferris Wheel  
   https://cses.fi/problemset/task/1090

2. CSES — Apartments  
   https://cses.fi/problemset/task/1084

3. CSES — Sum of Two Values  
   https://cses.fi/problemset/task/1640

4. CF 1399C — Boat Competition  
   https://codeforces.com/problemset/problem/1399/C

5. CF 1538C — Challenging Cliffs  
   https://codeforces.com/problemset/problem/1538/C

6. CF 1353B — Two Arrays And Swaps  
   https://codeforces.com/problemset/problem/1353/B

---

# 12. Greedy Construction

## Core idea

Construct step by step using the safest legal choice.

Unlike optimization greedy, the goal may just be:

```text
produce any valid answer
```

## Typical questions

```text
What choice creates the fewest restrictions for later?
What value is easiest to place now?
Can I postpone the difficult item?
```

## Example pattern

If one prefix sum value is forbidden:

```text
sort values
scan
if next addition hits forbidden sum:
    swap with a later different value
```

Local repair works because only that prefix is problematic.

## Practice

1. CF 1515A — Phoenix and Gold  
   https://codeforces.com/problemset/problem/1515/A

2. CF 1537C — Challenging Cliffs  
   https://codeforces.com/problemset/problem/1537/C

3. CF 1428D — Bouncing Boomerangs  
   https://codeforces.com/problemset/problem/1428/D

4. CF 1095C — Powers Of Two  
   https://codeforces.com/problemset/problem/1095/C

5. CF 1325A — EhAb AnD gCd  
   https://codeforces.com/problemset/problem/1325/A

6. CSES — Two Sets  
   https://cses.fi/problemset/task/1092

---

# 13. Greedy with Counts / Frequencies

## Core idea

When only counts of categories matter, avoid tracking individual elements.

Example:

```text
positive block
negative block
parity class
frequency of value
```

Within each category, choose the best representative.

## Worked pattern — alternating sign subsequence

Suppose sequence:

```text
2 5 1 | -3 -2 | 4 7 | -8
```

If subsequence must alternate signs and preserve order, from each same-sign block choose maximum:

```text
max(2,5,1) = 5
max(-3,-2) = -2
max(4,7)   = 7
max(-8)    = -8
```

Answer uses:

```text
5, -2, 7, -8
```

Why?

You need exactly one representative per sign block for maximum length; once length is fixed, take maximum value from each block.

## Practice

1. CF 1343C — Alternating Subsequence  
   https://codeforces.com/problemset/problem/1343/C

2. CF 1353B — Two Arrays And Swaps  
   https://codeforces.com/problemset/problem/1353/B

3. CF 1538B — Friends and Candies  
   https://codeforces.com/problemset/problem/1538/B

4. CF 1490A — Dense Array  
   https://codeforces.com/problemset/problem/1490/A

5. CF 1367B — Even Array  
   https://codeforces.com/problemset/problem/1367/B

6. CSES — Distinct Numbers  
   https://cses.fi/problemset/task/1621

---

# 14. Exchange Argument

## This is the main greedy proof technique

Suppose greedy chooses `G`.

Take any optimal solution `OPT`.

If `OPT` already chooses `G`, done.

Otherwise it chooses `O`.

Show:

```text
replace O with G
```

and the solution stays feasible and no worse.

Then there exists an optimal solution beginning with `G`.

Repeat inductively.

## Example — interval scheduling

Greedy chooses earliest-finishing interval `G`.

Optimal chooses first interval `O`.

Since:

```text
finish(G) <= finish(O)
```

replace `O` with `G`.

All later optimal intervals still fit.

Therefore greedy first choice is safe.

## Exchange argument checklist

```text
1. Define greedy choice.
2. Take arbitrary optimal solution.
3. Locate conflicting choice.
4. Swap/replace.
5. Prove feasibility remains.
6. Prove objective not worse.
```

## Practice

Use this proof explicitly on:

1. CSES — Movie Festival  
2. CSES — Ferris Wheel  
3. CF 1353B — Two Arrays And Swaps  
4. CSES — Tasks and Deadlines  
5. CF 1343C — Alternating Subsequence  

---

# 15. Dominance and “Never Worse” Choices

## Core idea

Choice A dominates choice B if:

```text
for every possible future,
A leaves a state at least as good as B
```

Then B never needs to be chosen.

## Example — Ferris Wheel

If heaviest `H` cannot pair with lightest `L`:

```text
H+L > X
```

then because every other person is >= L:

```text
H+anyone > X
```

So `H` must ride alone.

This is stronger than intuition; it is a proof.

## Practice

1. CSES — Ferris Wheel
2. CSES — Apartments
3. CSES — Movie Festival
4. CF 1343C — Alternating Subsequence
5. CF 1537C — Challenging Cliffs

---

# 16. Greedy + Sorting by Custom Comparator

## Core idea

Sometimes sorting ascending/descending by one field is not enough.

Need comparator derived from pairwise order.

If two items `A,B` can appear in either order, compare:

```text
cost(A then B)
vs
cost(B then A)
```

Whichever is better defines ordering.

This is a classic derivation technique.

## Example concept

For scheduling penalties, compare two jobs:

```text
A then B
B then A
```

derive inequality.

Then sort using that inequality.

## Important warning

Comparator must be transitive / form a strict weak ordering for `std::sort`.

## Practice

1. CSES — Tasks and Deadlines  
   https://cses.fi/problemset/task/1630

2. CSES — Movie Festival  
   https://cses.fi/problemset/task/1629

3. CF 1537C — Challenging Cliffs  
   https://codeforces.com/problemset/problem/1537/C

4. CF 1353B — Two Arrays And Swaps  
   https://codeforces.com/problemset/problem/1353/B

5. CF 1399C — Boat Competition  
   https://codeforces.com/problemset/problem/1399/C

---

# 17. Greedy + Invariant

## Core idea

The greedy action is chosen so an invariant remains true.

Example:

```text
after processing first i items,
selected set is the best feasible set for this prefix
```

This is the invariant behind many heap-based greedy solutions.

## Potions-style invariant

Process left to right.

Tentatively drink potion.

If health becomes negative:

```text
remove the most harmful potion taken so far
```

Invariant:

```text
among all choices using this many potions from prefix,
our health is as large as possible
```

Thus future feasibility is maximized.

## Practice

1. CF 1526C1 — Potions (Easy Version)
2. CF 1526C2 — Potions (Hard Version)
3. CSES — Movie Festival II
4. CSES — Concert Tickets
5. CSES — Tasks and Deadlines

---

# 18. Greedy + Binary Search

## Core idea

Greedy often serves as the feasibility test:

```text
can(X)
```

Then binary search finds minimum/maximum `X`.

Example:

```text
minimum maximum segment sum
```

For candidate `X`, greedily form each segment as long as adding next element keeps sum <= X.

Why greedy?

Using the longest possible current segment leaves the fewest elements for later segments.

## CSES Array Division pattern

```text
can(X):
segments = 1
current = 0

for x:
    if current+x <= X:
        current += x
    else:
        segments++
        current = x

return segments <= k
```

Then binary search smallest feasible `X`.

## Practice

1. CSES — Array Division  
   https://cses.fi/problemset/task/1085

2. CSES — Factory Machines  
   https://cses.fi/problemset/task/1620

3. CSES — Maximum Subarray Sum II  
   https://cses.fi/problemset/task/1644  
   (not pure greedy, good contrast)

4. CF 1490A — Dense Array  
   https://codeforces.com/problemset/problem/1490/A

5. CF 1374D — Zero Remainder Array  
   https://codeforces.com/problemset/problem/1374/D

---

# 19. Greedy + Math

## Why this appears often in A/B/C

Many short contest problems look like math but the final step is greedy.

Examples:

```text
derive required adjustments
then process largest adjustment first

derive block structure
then choose maximum per block

derive divisibility requirement
then construct smallest number of pieces
```

## Example — Alternating Subsequence

Math observation:

```text
maximum length = number of sign blocks
```

Greedy:

```text
take maximum value from each block
```

## Example — Zero Remainder Array style

Math transformation:

```text
need_i = (k - a[i]%k)%k
```

Then frequency / greedy scheduling determines final time.

## Practice

1. CF 1343C — Alternating Subsequence  
   https://codeforces.com/problemset/problem/1343/C

2. CF 1374D — Zero Remainder Array  
   https://codeforces.com/problemset/problem/1374/D

3. CF 1475A — Odd Divisor  
   https://codeforces.com/problemset/problem/1475/A

4. CF 1837A — Grasshopper on a Line  
   https://codeforces.com/problemset/problem/1837/A

5. CF 1353B — Two Arrays And Swaps  
   https://codeforces.com/problemset/problem/1353/B

6. CSES — Stick Lengths  
   https://cses.fi/problemset/task/1074

---

# 20. When Greedy Fails

This section is essential.

## Failure sign 1 — local gain can destroy future value

Example:

```text
take largest coin/value now
```

may block a better combination later.

## Failure sign 2 — choices interact globally

If selecting one item changes many later options, simple greedy may fail.

## Failure sign 3 — no exchange proof

If you cannot explain why the local choice can replace the first choice of an optimal solution, be suspicious.

## Classic counterexample mindset

Suppose greedy says:

```text
always take biggest
```

Try constructing:

```text
one huge item
vs
two medium items whose total is better
```

Suppose greedy says:

```text
always take earliest start
```

Try:

```text
one long early interval
vs
many short later intervals
```

## Greedy-vs-DP diagnostic

Ask:

```text
Does optimal choice depend on how much resource remains?
```

If yes, DP may be required.

Example clues:

```text
capacity
exact sum
choose subset
multiple interacting constraints
```

---

# A/B/C Recognition Table

| Problem shape | First greedy thought | Proof idea |
|---|---|---|
| choose best `k` independent values | sort descending | exchange |
| minimum groups with pair capacity | pair extremes | dominance |
| max non-overlapping intervals | earliest finish | exchange |
| equal benefit, varying cost | cheapest first | exchange |
| fixed actions, varying gain | largest gain | exchange |
| two destinations per item | sort by difference | opportunity cost |
| prefix must remain valid | repair immediately | forced choice |
| can replace earlier picks | heap, remove worst | prefix invariant |
| cover interval | farthest reach | dominance |
| sorted pair matching | two pointers | forced choice |
| build any valid answer | safest local construction | invariant |
| same-category runs | best per block | decomposition |
| optimization with candidate X | greedy `can(X)` + BS | monotonicity |

---

# 60-Second Greedy Checklist

When you suspect greedy:

```text
1. What is my local choice?
2. Why is this choice better than alternatives?
3. Can I swap it into an optimal solution?
4. Does it leave maximum flexibility for future?
5. Is some choice forced?
6. Can I sort to expose the structure?
7. Is there a "worst selected item" I can remove?
8. Can values be transformed into gain/cost/difference?
9. Does the problem decompose into independent blocks?
10. Can I state the proof in one sentence?
```

If you cannot answer #3, #4, or #5, do not trust the greedy yet.

---

# Contest Scratch Template

```text
GOAL:
min / max / construct

CANDIDATE GREEDY:
________________________________

WHY THIS ITEM FIRST?
________________________________

WHAT IF OPTIMAL CHOOSES SOMETHING ELSE?
________________________________

EXCHANGE:
replace __________________ with __________________

FEASIBILITY PRESERVED BECAUSE:
________________________________

OBJECTIVE NOT WORSE BECAUSE:
________________________________

COUNTEREXAMPLE SEARCH:
[ ] biggest-first failure
[ ] smallest-first failure
[ ] long interval vs many short
[ ] equal values
[ ] duplicates
[ ] boundary capacity
```

---

# Practice Strategy

## Phase 1 — A level

Master:

```text
sort + choose
pair extremes
take best per block
simple constructive greedy
```

Target:

```text
recognize in <= 60 sec
```

## Phase 2 — B level

Master:

```text
earliest finish
two pointers
difference/opportunity cost
prefix greedy
frequency greedy
```

## Phase 3 — B/C level

Master:

```text
heap replacement
exchange proof
custom comparator
greedy + invariant
greedy feasibility + binary search
```

## After every problem

Write only:

```text
Problem:
Greedy choice:
Why safe:
Proof type:
Trigger clue:
Counterexample that would break wrong greedy:
```

Example:

```text
Ferris Wheel
Choice: try heaviest with lightest
Why safe: if they cannot pair, heaviest cannot pair with anyone
Proof: dominance
Trigger: pair + capacity + minimize groups
```

---

# Recommended 4-Week Greedy Training

## Week 1

```text
Sort + choose
Pair extremes
Two pointers
Block greedy
```

## Week 2

```text
Intervals
Earliest finish
Constructive greedy
Frequency/count greedy
```

## Week 3

```text
Exchange arguments
Heap replacement
Prefix feasibility
Difference/opportunity cost
```

## Week 4

```text
Greedy + binary search
Greedy + math
Mixed CF Div2 A/B/C
Virtual contests
```

Daily target:

```text
4–6 focused problems
+
1 mixed problem with tags hidden
```

---



---

# Expanded Intuition Lab — Core Ideas, Real-World Mapping, and ASCII Reasoning

This part is deliberately more visual. For every major greedy family, learn four things:

```text
REAL WORLD STORY
      ↓
CONTEST MODEL
      ↓
GREEDY CHOICE
      ↓
WHY THAT CHOICE IS SAFE
```

The real-world story is only a mental model. In a contest, always return to the mathematical proof.

---

## E1. Sort + Take Best First — "Limited Shopping Cart"

### Real-world mapping

You have room for exactly 3 free products. Every product takes one slot. Their values are:

```text
Phone case       30
Headphones       80
Keyboard         50
Mouse            40
Monitor          90
```

Every slot has identical cost: one slot.

Goal:

```text
maximize total value
```

So take the three largest values:

```text
90, 80, 50
```

### Contest abstraction

```text
n independent items
each selected item consumes 1 identical slot
select exactly/at most k
maximize sum
```

Diagram:

```text
UNSORTED

30   80   50   40   90
 |    |    |    |    |
 v    v    v    v    v
independent contributions

SORT

90 > 80 > 50 > 40 > 30
^    ^    ^
|____|____|
  k = 3
```

### Why safe?

Suppose a claimed optimal solution uses `40` but leaves out `50`.

```text
OPT = [90,80,40]

unused = 50
```

Exchange:

```text
remove 40
insert 50

old = 210
new = 220
```

So the old solution was not optimal.

### Recognition sentence

> If all selected items consume the same resource and contribute independently, sort by contribution.

### When this mapping breaks

If products have different sizes:

```text
value  size
90      10
80       2
```

then “largest value first” may fail. This starts looking like knapsack.

---

## E2. Pair Extremes — "Two People per Gondola"

### Real-world mapping

A gondola carries at most 10 kg in this toy example and at most two objects.

Weights:

```text
2  3  7  9
```

Goal:

```text
minimum gondolas
```

Sort:

```text
L                 R
|                 |
2   3   7   9
```

Look at the heaviest `9`.

Try the lightest possible partner:

```text
9 + 2 = 11 > 10
```

ASCII proof:

```text
partners for 9:

2   -> too heavy together
3   -> even heavier
7   -> even heavier

therefore:

[ 9 ] must be alone
```

This decision is **forced**.

Next:

```text
2 + 7 = 9 <= 10

[2,7]
```

Then:

```text
[3]
```

### Why lightest + heaviest?

For the heaviest person there are only two cases:

```text
              heaviest H
                  |
          H + lightest L
             /         \
          <= X          > X
           |             |
 pair H with L       H must be alone
```

If `H+L > X`, no other partner can work.

If `H+L <= X`, pairing them uses the otherwise hardest-to-place item `H`, while consuming the least useful capacity with `L`.

### Recognition sentence

> After sorting, ask whether the most constrained extreme can be paired with the easiest possible partner.

---

## E3. Earliest Finish — "Meeting Room Scheduling"

### Real-world mapping

One meeting room. Requests:

```text
A: 09:00 ---------------- 17:00
B:       10:00--11:00
C:             11:30--12:30
D:                   13:00--14:00
```

Goal:

```text
host maximum number of meetings
```

### Wrong greedy: earliest start

Pick A because it starts first:

```text
09 -------------------------------- 17
```

Result:

```text
1 meeting
```

### Correct greedy: earliest finish

Pick B:

```text
10--11
```

Then C:

```text
      11:30--12:30
```

Then D:

```text
                 13--14
```

Result:

```text
3 meetings
```

### Why finish time matters

Future opportunities exist **after** the current interval.

So we want to free the resource as early as possible.

```text
current choice
      |
      v
finish time
      |
      v
free space for future
```

Comparison:

```text
choice X: [-------------]
future:                  [??]

choice G: [---]
future:      [????????????????]
```

Earlier finish leaves a superset of future time.

### Exchange proof visually

Suppose optimal starts with O:

```text
O: [----------]
G: [-----]

finish(G) <= finish(O)
```

Replace O with G:

```text
OPT:
[ O ][next][next]...

SWAP:
[ G ]     [next][next]...
```

Anything that starts after O finishes also starts after G finishes.

### Recognition sentence

> When maximizing non-overlapping intervals on one resource, preserve the largest possible future time by finishing earliest.

---

## E4. Cheapest First — "Buying Identical Bottles"

### Real-world mapping

You need as many bottles of water as possible. Every bottle gives the same benefit: one bottle.

Prices:

```text
8, 2, 6, 1, 4
budget = 10
```

Sort:

```text
1,2,4,6,8
```

Buy:

```text
1 -> remaining 9
2 -> remaining 7
4 -> remaining 3
```

3 bottles.

### Mathematical model

If benefit is identical:

```text
benefit(item) = 1
```

then only cost matters.

To maximize count under budget, minimize cost consumed per selected item.

### Exchange

If a solution buys price `6` while skipping price `4`:

```text
replace 6 by 4
```

Same number of items, more remaining budget.

### Recognition sentence

> Equal benefit + limited budget → cheapest feasible items first.

### Warning

If benefits differ, cheapest-first can fail:

```text
item A: cost 2, value 1
item B: cost 3, value 100
```

Now value/cost interaction matters.

---

## E5. Largest Gain — "Upgrade Coupons"

### Real-world mapping

You may apply two upgrade coupons.

```text
Item      old    new    gain
A          10     14      4
B          20     40     20
C           8      9      1
D          15     25     10
```

Instead of thinking about old/new totals, compute:

```text
gain = new-old
```

Now problem becomes:

```text
choose 2 values from [4,20,1,10]
maximize sum
```

Take:

```text
20 + 10
```

### Transformation diagram

```text
COMPLICATED ORIGINAL
(old price, new price, coupon)
          |
          | subtract baseline
          v
MARGINAL GAINS
4 20 1 10
          |
          v
SORT + TAKE BEST
20 10
```

This “subtract a baseline” transformation is extremely useful in B problems.

### Recognition sentence

> If every choice modifies an existing score, compute the marginal gain and greedily optimize the gains.

---

## E6. Opportunity Cost / Difference — "Assign Engineers to Two Teams"

### Real-world mapping

Each engineer can work on Backend or Frontend.

```text
Engineer   Backend   Frontend
A             10        9
B             20        3
```

Only one Backend slot.

Absolute Backend scores say:

```text
B=20 > A=10
```

But the deeper reason is loss:

```text
A moved to Frontend loses: 10-9 = 1
B moved to Frontend loses: 20-3 = 17
```

Protect B from the huge loss.

Assignment:

```text
B -> Backend = 20
A -> Frontend = 9
total = 29
```

Opposite:

```text
A -> Backend = 10
B -> Frontend = 3
total = 13
```

### General model

```text
A-score = a[i]
B-score = b[i]

preference for A:
d[i] = a[i]-b[i]
```

Large positive difference:

```text
strongly wants A
```

Large negative difference:

```text
strongly wants B
```

ASCII number line:

```text
strong B                         strong A
<--------------------------------------->
 -20  -10   0    5    10    20
```

### Recognition sentence

> When every item has two possible destinations, sort by the cost of sending it to the “wrong” destination.

---

## E7. Prefix Greedy — "Bank Balance Must Never Go Negative"

### Real-world mapping

Transactions arrive in fixed order.

```text
+5, -3, -8, +10
```

Rule:

```text
balance may never become negative
```

Walk left to right:

```text
start = 0

+5 -> 5
-3 -> 2
-8 -> -6   INVALID
```

Once this prefix is negative, future `+10` cannot retroactively make the earlier prefix valid.

```text
past invalid prefix | future
--------------------+--------
       -6           | +10
```

So repair must happen **now**.

This is why prefix constraints often create greedy decisions.

### Recognition sentence

> If validity is required for every prefix, a broken prefix must be fixed immediately; future elements cannot repair history.

---

## E8. Heap Replacement — "Choose Maximum Courses Before Deadlines"

### Real-world mapping

You process opportunities in order and tentatively accept them.

Suppose selected durations are:

```text
2, 4, 7
```

but current time budget only allows total 10:

```text
2+4+7 = 13 > 10
```

You need to remove one selected task.

Which?

```text
remove 2 -> remaining 11
remove 4 -> remaining 9
remove 7 -> remaining 6
```

Removing the largest duration leaves the most free capacity.

### Heap idea

```text
seen item
   |
   v
accept tentatively
   |
   v
constraint broken?
 /           \
no           yes
|             |
continue   remove WORST selected
```

Use max-heap for “worst = largest cost”.

### Prefix invariant

After each prefix:

```text
we have a feasible selected set,
and among sets of this size,
we have minimized consumed resource
```

That gives the best chance to accept future items.

### Recognition sentence

> If you may regret an earlier selected item, accept optimistically and remove the worst selected candidate when feasibility breaks.

---

## E9. Interval Covering — "Security Cameras Covering a Corridor"

### Real-world mapping

Need to cover corridor from 0 to 20.

At current uncovered point `x=5`, available cameras cover:

```text
A: [3------8]
B: [4-------------14]
C: [5---------11]
```

All cover point 5.

Choose B because it reaches farthest:

```text
current=5

A reaches 8
C reaches 11
B reaches 14  <-- greedy
```

Diagram:

```text
0----5---------------------------20
     ^
     current uncovered point

A:   [---]
C:   [------]
B:   [---------]
                ^
             farthest
```

Any solution must select some interval covering 5. Replacing that interval with B cannot uncover anything before 5 and covers at least as far into the future.

### Recognition sentence

> For minimum interval covering, at the first uncovered point choose the available interval that pushes the frontier farthest.

---

## E10. Two-Pointer Greedy — "Matching Apartment Sizes"

### Real-world mapping

Applicants want:

```text
40, 60, 80
```

Apartments:

```text
42, 65, 100
```

Suppose tolerance is 5.

Compare smallest unmatched pair:

```text
want 40
apt  42
difference 2 -> match
```

Next:

```text
want 60
apt 65
difference 5 -> match
```

Next:

```text
want 80
apt 100
apartment too large
```

### Forced pointer moves

If:

```text
apartment < desired-k
```

this apartment is too small for the current applicant, and because future applicants want even larger sizes, it is useless forever:

```text
move apartment pointer
```

If:

```text
apartment > desired+k
```

current applicant cannot use this or any later, larger apartment:

```text
move applicant pointer
```

Diagram:

```text
sorted A: 40 60 80
          ^
sorted B: 42 65 100
          ^

comparison tells which side can NEVER work later.
```

### Recognition sentence

> Sorted two-pointer greedy works when a failed comparison proves one extreme can never match anything later.

---

## E11. Greedy Construction — "Arrange Boxes Without Hitting Forbidden Weight"

### Real-world mapping

You stack boxes one by one. Prefix weight must never equal forbidden `X`.

Weights:

```text
1 2 3
X = 3
```

Naive:

```text
1
1+2 = 3  <-- forbidden
```

Instead swap:

```text
1,3,2

prefix:
1
4
6
```

### Greedy repair

```text
scan construction
     |
would next step violate?
     |
    yes
     |
swap/use another available item
```

Why is this attractive in contests?

Because “print any valid arrangement” means you can deliberately choose a structure that is easy to prove.

### Recognition sentence

> In constructive greedy, choose the next legal value that leaves the remaining instance easiest—not necessarily numerically smallest/largest.

---

## E12. Best Representative per Block — "One Employee from Each Department"

### Real-world mapping

Suppose you must select exactly one employee from each department.

Scores:

```text
Backend:   5  8  6
Frontend:  3  7
QA:        4  9  2
```

Departments are independent.

Take:

```text
Backend  -> 8
Frontend -> 7
QA       -> 9
```

### Alternating-subsequence mapping

Same-sign consecutive elements form forced blocks:

```text
+ + + | - - | + + | -
```

For maximum-length alternating subsequence:

```text
one element per block
```

Once number selected per block is fixed, choose maximum value in each block.

```text
[2 5 1] [-3 -2] [4 7] [-8]
    ^        ^       ^    ^
    5       -2       7   -8
```

### Recognition sentence

> If constraints force exactly one choice from each independent block/category, take the locally best representative from every block.

---

## E13. Custom Comparator — "Which Job Goes First?"

### Core idea

Do not guess the sorting key.

For two items A and B, calculate both possible orders:

```text
A -> B
B -> A
```

If:

```text
cost(A,B) <= cost(B,A)
```

then A should precede B.

### Derivation diagram

```text
          two jobs
          /      \
       A then B   B then A
          |          |
       formula 1   formula 2
          \          /
           compare
              |
              v
       ordering inequality
```

That inequality becomes the comparator.

This technique is much stronger than memorizing “sort by x/y”.

### Recognition sentence

> When order changes total cost, derive the comparator by comparing two neighboring items in both possible orders.

---

## E14. Greedy + Binary Search — "Minimum Truck Capacity"

### Real-world mapping

Packages must stay in order:

```text
4 2 5 3 6
```

You have at most 3 trucks/trips.

Question:

```text
is capacity X enough?
```

For fixed `X`, greedily fill current truck as much as possible.

Example `X=8`:

```text
truck 1: 4+2 = 6
next 5 would exceed 8

truck 2: 5+3 = 8

truck 3: 6

3 trucks -> feasible
```

ASCII:

```text
[4 2] [5 3] [6]
  6     8     6
```

Why fill each truck maximally?

Stopping earlier cannot reduce the number of trucks needed for the remaining suffix.

Then feasibility is monotonic:

```text
capacity:
5 6 7 8 9 10 11...
F F F T T  T  T
      ^
first feasible
```

Binary search that boundary.

### Recognition sentence

> If a candidate answer can be checked greedily and “larger is always easier,” use greedy feasibility + binary search.

---

## E15. Greedy + Math — "First Derive, Then Choose"

### Core idea

Many CF B/C greedy problems hide greedy behind a mathematical transformation.

Example shape:

```text
array values
need each value divisible by k
operations have timing/order constraints
```

First compute each element's deficit:

```text
need[i] = (k-a[i]%k)%k
```

Original numbers disappear.

```text
a[] -> remainder -> required adjustment -> frequency/order greedy
```

Diagram:

```text
RAW VALUES
17 21 26 30
    |
   mod 5
    v
2 1 1 0
    |
distance to next multiple
    v
3 4 4 0
    |
greedy/counting problem
```

### Recognition sentence

> Before applying greedy, ask whether every item can be converted to a simpler cost, gain, deficit, deadline, remainder, or interval.

---

# Expanded Greedy Proof Map

When you find a candidate greedy choice, classify the proof:

```text
Candidate choice
      |
      +--> Is another choice replaceable?
      |        |
      |        +--> EXCHANGE ARGUMENT
      |
      +--> Is another choice never better?
      |        |
      |        +--> DOMINANCE
      |
      +--> Is the decision forced?
      |        |
      |        +--> FORCED CHOICE
      |
      +--> Does it maintain best prefix state?
      |        |
      |        +--> INVARIANT / STAYING AHEAD
      |
      +--> Does problem split into independent blocks?
               |
               +--> LOCAL OPTIMUM PER BLOCK
```

Examples:

```text
Movie Festival   -> exchange
Ferris Wheel     -> dominance / forced choice
Potions          -> prefix invariant
Alternating Subsequence -> independent blocks
Array Division   -> greedy feasibility + monotonicity
```

---

# Expanded Wrong-Greedy Counterexample Lab

Before submitting, actively attack your greedy.

## "Always take largest"

Try:

```text
capacity = 10

largest item = 9
medium = 5,5

If objective rewards combining:
9 may lose to 5+5.
```

## "Always take smallest"

Try:

```text
small item gives almost no benefit
slightly larger item gives huge benefit
```

## "Earliest start interval"

```text
A: [1----------------10]
B:   [2-3]
C:       [4-5]
D:           [6-7]

earliest start -> 1
earliest finish -> 3
```

## "Pair two smallest"

For minimizing groups under capacity, ask what happens to the heaviest items you postpone.

## "Take current positive gain"

Ask whether consuming a limited resource now prevents a much larger gain later.

The habit is:

```text
GREEDY CLAIM
    |
    v
construct 3-5 item adversarial example
    |
    +--> survives -> seek proof
    |
    +--> fails -> abandon/change greedy
```

---

# Real Contest Greedy Decoder

When reading a CF/CodeChef A/B/C problem:

```text
STEP 1
Remove story.

"What exactly am I selecting / ordering / pairing?"

STEP 2
Identify scarce resource.

slots?
time?
capacity?
number of operations?
prefix health?
available partners?

STEP 3
Identify "most constrained" object.

heaviest?
earliest deadline?
current prefix?
first uncovered point?
largest loss?
worst selected item?

STEP 4
Ask what choice preserves most future freedom.

earliest finish
smallest cost
farthest coverage
remove largest cost
pair hardest item safely

STEP 5
Try to break it with a tiny counterexample.

STEP 6
Name the proof.

exchange / dominance / forced / invariant / independent blocks

STEP 7
Only then code.
```

---

# Greedy Recognition Notebook Template

For every solved problem, record:

```text
Problem:
Rating:
Visible story:
Abstract model:
Scarce resource:
Greedy choice:
Why this object is most constrained:
Real-world mapping:
Proof type:
One-sentence proof:
Wrong greedy I considered:
Counterexample to wrong greedy:
Trigger phrase for next contest:
```

Example:

```text
Problem: Ferris Wheel
Abstract model: pair weights, sum <= X, minimize groups
Scarce resource: second seat + weight capacity
Greedy choice: process heaviest, try lightest partner
Why constrained: heaviest has fewest possible partners
Proof: if H+L>X, H cannot pair with anyone
Type: dominance / forced choice
Trigger: "pair + capacity + minimize groups"
```

This is the level of understanding that builds contest recognition.


# Final Goal

Do not memorize:

```text
"this problem is greedy"
```

Train this:

```text
statement
   |
   v
identify choice
   |
   v
find ordering / transformation
   |
   v
prove local choice is safe
   |
   v
implement
```

For CF / CodeChef A-B-C, the biggest leap comes when you can recognize:

```text
forced choice
exchangeable choice
dominant choice
best representative
best marginal gain
earliest finishing choice
```

without trying random sorting orders.

A greedy solution is complete only when you can answer:

```text
Why can no globally optimal solution benefit from making a different choice here?
```
