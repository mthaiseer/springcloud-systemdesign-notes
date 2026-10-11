# Greedy Pattern-Wise Problem Index — 800 → 1900

> **Goal:** master greedy the same way as the Prefix Sum pattern sheet:
>
> ```text
> learn one form
> → understand the greedy choice
> → understand why it is safe
> → solve 3–5 direct problems
> → move to mixed A/B/C practice
> ```
>
> This sheet is built around the greedy topics covered in the TLE + AlgoZenith notes:
>
> ```text
> sorting / exchange
> matching
> interval scheduling
> running best
> custom comparator
> reverse greedy
> median / Manhattan
> bottleneck + Top-K
> heap replacement
> constructive greedy
> sweep line / active set
> ```
>
> **CF ratings** are shown where applicable. Codeforces occasionally re-rates problems.

---

# How to Use This Sheet

```text
For each form:

1. Read only the Recognition Clue
2. Try to name the greedy choice
3. Ask: "Why is this local choice safe?"
4. Solve first 3 problems without editorial
5. Solve the final 1–2 stretch problems
6. Write one-line proof / invariant
7. Move to mixed practice
```

### Suggested progression

| Phase | Rating | Target |
|---|---:|---|
| Foundation | 800–1100 | See the obvious greedy choice quickly |
| Pattern Recognition | 1200–1400 | Recognize sorting / two pointers / reverse / median |
| Proof + Data Structure | 1500–1700 | Exchange proofs, heaps, bottlenecks, sweep line |
| Advanced Greedy | 1800–1900 | Greedy + invariant + contribution + ordered structures |

---

# Clickable TOC

- [0. Prerequisites — Greedy Toolkit](#0-prerequisites--greedy-toolkit)
- [Form 1 — Sort + Take Best / Extremes](#form-1--sort--take-best--extremes)
- [Form 2 — Greedy Matching + Two Pointers](#form-2--greedy-matching--two-pointers)
- [Form 3 — Rearrangement / Pairing / Exchange](#form-3--rearrangement--pairing--exchange)
- [Form 4 — Interval Scheduling / Earliest Finish](#form-4--interval-scheduling--earliest-finish)
- [Form 5 — Interval Cover / Farthest Reach](#form-5--interval-cover--farthest-reach)
- [Form 6 — Running Best / Cheapest So Far](#form-6--running-best--cheapest-so-far)
- [Form 7 — Custom Comparator / Exchange Scheduling](#form-7--custom-comparator--exchange-scheduling)
- [Form 8 — Deadline / Feasibility + Heap](#form-8--deadline--feasibility--heap)
- [Form 9 — Reverse Greedy / Forced Previous Operation](#form-9--reverse-greedy--forced-previous-operation)
- [Form 10 — Median / Absolute Distance / Meeting Point](#form-10--median--absolute-distance--meeting-point)
- [Form 11 — Fix Bottleneck + Top-K Heap](#form-11--fix-bottleneck--top-k-heap)
- [Form 12 — String / Digit Construction Greedy](#form-12--string--digit-construction-greedy)
- [Form 13 — Frequency / Group Formation Greedy](#form-13--frequency--group-formation-greedy)
- [Form 14 — Constructive Greedy + Maintain Invariant](#form-14--constructive-greedy--maintain-invariant)
- [Form 15 — Resource Allocation / Replace Worst Choice](#form-15--resource-allocation--replace-worst-choice)
- [Form 16 — Sweep Line / Overlap / Coverage](#form-16--sweep-line--overlap--coverage)
- [Form 17 — Offline Interval Queries + Active Set](#form-17--offline-interval-queries--active-set)
- [Form 18 — Advanced Greedy Transformation](#form-18--advanced-greedy-transformation)
- [Final Practice Order](#final-practice-order)

---

# 0. Prerequisites — Greedy Toolkit

> You do **not** need to master every advanced greedy proof before starting the problem list.
>
> You should be comfortable with the ideas below because almost every later form is built from them.

## 0.1 What Greedy Actually Means

Greedy does **not** mean:

```text
take the biggest thing
take the smallest thing
sort and hope
```

Greedy means:

```text
At the current state,
make one locally optimal choice

AND

prove that this choice
cannot destroy an optimal final answer.
```

The most important question is:

```text
WHY is this choice safe?
```

### Tiny dry run

Suppose you may choose exactly 2 values and want the maximum sum.

```text
values = [3, 10, 7, 2]
```

The two largest are:

```text
10 and 7
```

So:

```text
answer = 17
```

Why is taking `10` safe?

Suppose an alleged optimal solution does not contain `10`:

```text
chosen = [7, 3]
sum    = 10
```

Replace `3` by `10`:

```text
chosen = [7, 10]
sum    = 17
```

The answer cannot become worse.

That replacement argument is the basic greedy idea.

### Contest mental model

```text
local choice
    |
    v
can any optimal solution be changed
to include my choice
without becoming worse?
    |
   YES
    |
    v
greedy choice is safe
```

---

## 0.2 Local Best Does Not Automatically Mean Global Best

Before using greedy, check whether the local choice can damage the future.

### Counterexample

Coins:

```text
[1, 3, 4]
```

Target:

```text
6
```

"Take the largest coin first" gives:

```text
6
↓ take 4
2
↓ take 1
1
↓ take 1
0

3 coins
```

But:

```text
3 + 3 = 6

2 coins
```

So:

```text
locally largest
!=
globally optimal
```

### What this teaches

Do not write:

```text
sort descending
take greedily
```

until you have one of these:

```text
exchange proof
forced move
invariant
dominance argument
earliest-finish argument
farthest-reach argument
bottleneck fixing
replace-worst argument
```

---

## 0.3 Exchange / Swapping Proof

This is the most reusable greedy proof.

General idea:

```text
Assume an optimal solution does NOT follow our greedy order.

Find a bad adjacent pair.

Swap the pair.

Show:
cost does not increase
or
score does not decrease.

Repeat swaps.

Eventually an optimal solution
has the greedy order.
```

### Tiny dry run

Suppose smaller values should appear earlier.

Current local order:

```text
8, 3
```

Greedy order:

```text
3, 8
```

If swapping them never hurts the objective, then:

```text
8, 3
```

is an inversion and can be removed.

For a longer sequence:

```text
8  3  5  1

swap 8,3
3  8  5  1

swap 8,5
3  5  8  1

swap 8,1
3  5  1  8

...
```

If every such swap is safe, sorting is globally safe.

### Recognition clue

```text
problem asks for an ordering
+
objective changes depending on order
```

Think:

```text
compare only A,B
vs
B,A
```

---

## 0.4 Why Comparing Two Items Is Often Enough

You usually do **not** need to compare all permutations.

Suppose the order is:

```text
prefix | A | B | suffix
```

Compare:

```text
prefix | A | B | suffix
```

with:

```text
prefix | B | A | suffix
```

If the prefix contribution is unchanged and the suffix starts from the same final state, only the contribution of `A` and `B` matters.

That gives a local inequality.

### Generic derivation

```text
costAB <= costBA
```

Simplify until you get something like:

```text
key(A) <= key(B)
```

Then:

```text
sort by key
```

This is exactly the mindset needed for custom greedy comparators.

---

## 0.5 Inversions

An inversion is a pair that violates the desired greedy order.

Example:

```text
desired: ascending

array:
2  7  4  9

7,4 is an inversion
```

If swapping every inversion never makes the answer worse:

```text
2  7  4  9
      ↓ swap
2  4  7  9
```

then the fully sorted order is optimal.

### Why this matters

Many greedy proofs are really:

```text
show every inversion can be removed safely
```

---

## 0.6 Sorting — Know What the Sort Is Achieving

Sorting is not the greedy idea by itself.

Sorting usually creates one of these useful structures:

```text
smallest available first
largest available first
earliest ending first
bottleneck values become monotonic
eligible candidates become a prefix
two-pointer matching becomes possible
```

### Tiny dry run — matching

```text
boys  = [4, 1, 6]
girls = [5, 2, 7]

allowed difference <= 1
```

Sort:

```text
boys  = [1, 4, 6]
girls = [2, 5, 7]
```

Now matching becomes monotonic:

```text
1 ↔ 2   yes
4 ↔ 5   yes
6 ↔ 7   yes
```

Without sorting, it is harder to know which candidate can safely be discarded.

---

## 0.7 Two Pointers After Sorting

Use two pointers when two sorted sequences must be matched.

### Basic template

```cpp
sort(first.begin(), first.end());
sort(second.begin(), second.end());

int left = 0;
int right = 0;
int matches = 0;

while (left < (int)first.size() &&
       right < (int)second.size()) {

    if (canMatch(first[left], second[right])) {
        ++matches;
        ++left;
        ++right;
    }
    else if (first[left] < second[right]) {
        ++left;
    }
    else {
        ++right;
    }
}
```

### Dry run

```text
A = [1, 4, 8]
B = [2, 5, 9]

condition:
|A[i] - B[j]| <= 1
```

Start:

```text
1 and 2
|1-2| = 1
match

4 and 5
|4-5| = 1
match

8 and 9
|8-9| = 1
match
```

Answer:

```text
3
```

### Important question

If current pair cannot match, ask:

```text
Which smaller value can never match
any future larger value?
```

That tells you which pointer moves.

---

## 0.8 Running Minimum / Maximum

Sometimes all earlier items are valid candidates, but you need only one best value.

Then do **not** store the whole prefix.

Store:

```text
minimumSeen
```

or:

```text
maximumSeen
```

### Dry run — cheapest price so far

Prices:

```text
[8, 5, 7, 3]
```

Running minimum:

```text
position       0   1   2   3
price          8   5   7   3
minimumSeen    8   5   5   3
```

At position 2:

```text
valid previous prices = [8,5,7]
best                   = 5
```

We do not care that the other values were `8` and `7`.

Only:

```text
minimumSeen = 5
```

matters.

### Template

```cpp
long long minimumSeen = values[0];

for (int index = 1; index < (int)values.size(); ++index) {
    // use minimumSeen for current decision

    minimumSeen = min(minimumSeen, values[index]);
}
```

---

## 0.9 Priority Queue / Heap

Know these two C++ forms.

### Max-heap

```cpp
priority_queue<long long> maximumHeap;
```

Top:

```text
largest value
```

### Min-heap

```cpp
priority_queue<
    long long,
    vector<long long>,
    greater<long long>
> minimumHeap;
```

Top:

```text
smallest value
```

---

## 0.10 Maintain Top-K Values With a Min-Heap

If you want to retain the largest `K` values seen so far:

```text
keep a MIN-heap of size K
```

Why a min-heap?

Because among the selected Top-K values, the smallest selected value is the first one we want to remove when something better arrives.

### Dry run

Need largest:

```text
K = 3
```

Values arrive:

```text
8, 2, 10, 4, 12
```

Process:

```text
8
heap = [8]

2
heap = [2,8]

10
heap = [2,8,10]

4
insert 4
heap = [2,4,10,8]

size > 3
remove smallest = 2

heap contains:
[4,8,10]
```

Next:

```text
insert 12

temporary:
[4,8,10,12]

remove smallest = 4

remaining:
[8,10,12]
```

These are exactly the largest 3 values.

### Heap + running sum

Very common pattern:

```cpp
priority_queue<
    long long,
    vector<long long>,
    greater<long long>
> topValues;

long long selectedSum = 0;

for (long long value : values) {
    topValues.push(value);
    selectedSum += value;

    if ((int)topValues.size() > k) {
        selectedSum -= topValues.top();
        topValues.pop();
    }
}
```

Now:

```text
selectedSum
=
sum of largest K values seen so far
```

---

## 0.11 Fix the Bottleneck, Optimize the Rest

This is essential for:

```text
SUM * MIN
```

problems.

Suppose each person has:

```text
strength
efficiency
```

Team score:

```text
sumStrength
*
minimumEfficiency
```

The difficulty is that:

```text
sum
and
minimum
```

change together.

Fix one part.

### Dry run

Students:

```text
(strength, efficiency)

(5, 8)
(9, 6)
(4, 4)

K = 2
```

Sort efficiency descending:

```text
(5,8)
(9,6)
(4,4)
```

Consider current efficiency `6` as the bottleneck.

Eligible students:

```text
efficiency >= 6

(5,8)
(9,6)
```

Need 2 strengths:

```text
5 + 9 = 14
```

Score:

```text
14 * 6 = 84
```

When current efficiency becomes `4`:

```text
all three are eligible
```

Need top 2 strengths:

```text
9 and 5
```

Score:

```text
(9 + 5) * 4
= 56
```

So the best among these candidates is:

```text
84
```

### Transformation

```text
sort by bottleneck
        ↓
current item fixes MIN
        ↓
previous items are eligible
        ↓
keep best K-1 contributions
        ↓
heap
```

---

## 0.12 Custom Comparator — Derive, Do Not Guess

Suppose two jobs have:

```text
time
penalty
```

and order affects total cost.

Do not sort by:

```text
time
penalty
penalty/time
```

just because it "looks right."

Compare two jobs.

### Generic setup

```text
A before B
cost = costAB

B before A
cost = costBA
```

Require:

```text
costAB <= costBA
```

Simplify.

Often you reach:

```text
firstPenalty * secondTime
>=
secondPenalty * firstTime
```

Then the comparator is derived from the proof.

### Cross multiplication

Avoid floating point:

```text
firstPenalty / firstTime
>
secondPenalty / secondTime
```

Use:

```text
firstPenalty * secondTime
>
secondPenalty * firstTime
```

### C++ comparator template

```cpp
struct Job {
    long long time;
    long long penalty;
};

bool comesBefore(const Job& firstJob,
                 const Job& secondJob) {

    return firstJob.penalty * secondJob.time
         > secondJob.penalty * firstJob.time;
}
```

Use `long long` when the product fits inside 64-bit.

---

## 0.13 Interval Basics

An interval is usually:

```text
[L, R]
```

You must understand what the endpoints mean.

For closed intervals:

```text
[L,R]
```

both endpoints belong to the interval.

For half-open intervals:

```text
[L,R)
```

`R` does not belong.

This matters when two events happen at the same coordinate.

### Example

Intervals:

```text
[1,4]
[4,7]
```

If endpoint `4` belongs to both:

```text
they overlap at 4
```

If intervals are interpreted as:

```text
[1,4)
[4,7)
```

they do not overlap.

Always read the problem's endpoint semantics before deciding tie ordering.

---

## 0.14 Interval Scheduling vs Interval Covering

These two greedy patterns are easy to confuse.

### A. Maximum number of non-overlapping intervals

Think:

```text
finish as early as possible
```

Example:

```text
A = [1,3]
B = [2,8]
C = [4,6]
```

Choose `A` first because:

```text
end(A) = 3
```

leaves more future room than choosing `B`.

Then choose `C`.

Result:

```text
A, C
```

### B. Minimum intervals to cover a target

Think:

```text
among intervals starting before current reach,
take the one reaching farthest
```

Example:

```text
current coverage ends at 3

available:
[1,5]
[2,8]
[3,6]
```

Choose:

```text
[2,8]
```

because it extends coverage farthest.

### Memorize the distinction

```text
SELECT MANY NON-OVERLAPPING
→ earliest finish

COVER EVERYTHING WITH FEW CHOICES
→ farthest reach
```

---

## 0.15 Sweep Line Events

Sweep line converts intervals into events.

Example intervals:

```text
[1,5]
[3,7]
```

Create:

```text
1 : +1
5 : -1

3 : +1
7 : -1
```

Sorted:

```text
position:  1   3   5   7
event:    +1  +1  -1  -1
```

Sweep:

```text
before 1: active = 0

at 1:
active = 1

at 3:
active = 2

at 5:
active = 1

at 7:
active = 0
```

ASCII:

```text
1-------5
    3-------7

----|---|---|---|---->
    1   3   5   7

cnt 1   2   1   0
```

### Core idea

Between two consecutive event coordinates:

```text
active state does not change
```

So if:

```text
active >= K
```

from coordinate `x` until next coordinate `y`:

```text
contribution = y - x
```

---

## 0.16 Offline Queries

If all queries are known before answering, you may reorder them.

That is an enormous advantage.

### Example

Intervals:

```text
[2,8]
[5,10]
[12,14]
```

Queries:

```text
13, 3, 7
```

Online order:

```text
13, 3, 7
```

Offline:

```text
3, 7, 13
```

Now sweep from left to right.

As query position increases:

```text
new intervals become active
expired intervals can be removed
```

### Recognition clue

```text
many intervals
+
many point queries
+
all queries given in advance
```

Think:

```text
sort queries
+
sort intervals/events
+
maintain active candidates
```

---

## 0.17 `set` / `multiset` Basics Needed for Greedy + Sweep

Use `multiset` when duplicates matter.

### Insert

```cpp
multiset<long long> activeValues;

activeValues.insert(value);
```

### Remove exactly one copy

```cpp
auto it = activeValues.find(value);

if (it != activeValues.end()) {
    activeValues.erase(it);
}
```

Do **not** use:

```cpp
activeValues.erase(value);
```

when you want to remove only one duplicate, because that can erase all copies.

### Minimum / maximum

```cpp
long long minimumValue = *activeValues.begin();
long long maximumValue = *activeValues.rbegin();
```

### Lower bound

```cpp
auto it = activeValues.lower_bound(target);
```

This gives the first value:

```text
>= target
```

These operations are commonly needed in active-set sweep problems.

---

## 0.18 Reverse Greedy

Forward process may branch badly.

Example operations:

```text
x = x + 1
x = 2 * x
```

Start:

```text
0
```

Target:

```text
11
```

Forward direction has many possibilities.

Reverse from `11`.

### Dry run

```text
11 is odd
→ it could not have been produced by doubling
→ previous step must be 10

11 → 10
```

Now:

```text
10 is even
→ reverse doubling

10 → 5
```

Then:

```text
5 odd
→ 4

4 even
→ 2

2 even
→ 1

1 odd
→ 0
```

Path:

```text
11
→ 10
→ 5
→ 4
→ 2
→ 1
→ 0
```

The key is not "work backward."

The key is:

```text
backward move becomes FORCED
```

---

## 0.19 Median and Absolute Distance

If the objective is:

```text
sum |meetingPoint - position[i]|
```

the optimum is a median.

### Dry run

Positions:

```text
1, 3, 8
```

Try meeting at `1`:

```text
|1-1| + |1-3| + |1-8|
= 0 + 2 + 7
= 9
```

Try `3`:

```text
|3-1| + |3-3| + |3-8|
= 2 + 0 + 5
= 7
```

Try `8`:

```text
7 + 5 + 0
= 12
```

Best:

```text
3
```

which is the median.

### Why?

Moving the meeting point one step right:

```text
distance to every point on the left
increases by 1

distance to every point on the right
decreases by 1
```

So the cost stops decreasing when the left/right counts balance.

### Manhattan distance

```text
|meetingX - pointX|
+
|meetingY - pointY|
```

separates into:

```text
x problem
+
y problem
```

Therefore:

```text
meetingX = median of X coordinates
meetingY = median of Y coordinates
```

---

## 0.20 Frequency Compression

Sometimes the actual order of equal values does not matter.

Example:

```text
values:
1 1 1 2 2 5
```

Compress to:

```text
value 1 → frequency 3
value 2 → frequency 2
value 5 → frequency 1
```

Now the problem may become:

```text
[3,2,1]
```

instead of six individual items.

### Typical greedy signals

```text
form groups
make frequencies distinct
consume most frequent type
pair different values
```

Then think:

```text
count frequencies
→ sort counts / heap counts
→ greedily consume
```

---

## 0.21 Replace the Worst Chosen Item

This is a powerful heap pattern.

General structure:

```text
process candidates in useful order

take current candidate

if constraints break:
    remove the worst selected candidate
```

### Dry run

Suppose current selected durations are:

```text
[4, 2, 7]
```

and the deadline allows total time only:

```text
10
```

Current total:

```text
4 + 2 + 7 = 13
```

We need to discard one selected task.

Which one?

```text
remove largest duration = 7
```

Remaining:

```text
4 + 2 = 6
```

Why remove the largest?

Because among solutions keeping the same number of tasks, removing the largest duration leaves the most spare time for future tasks.

This leads naturally to:

```text
max-heap of selected durations
```

---

## 0.22 Constructive Greedy and Invariants

Constructive problems ask you to output a valid sequence.

The key is usually not a formula.

The key is:

```text
after every choice,
keep one property true
```

That property is the invariant.

### Tiny example

Suppose you build a sequence that must remain strictly increasing.

Current:

```text
2 5 9
```

Candidates:

```text
4, 10
```

`4` is unusable now because:

```text
4 <= 9
```

`10` preserves:

```text
2 < 5 < 9 < 10
```

The invariant is:

```text
constructed prefix is valid
```

### Contest question

Before coding, write:

```text
After processing i items,
what exactly is guaranteed to be true?
```

If you cannot answer that, the constructive greedy is not yet clear.

---

## 0.23 `long long` and Overflow

For greedy problems, products frequently appear:

```text
time * penalty
sum * minimum
coordinate * count
strengthSum * efficiency
```

Use:

```cpp
long long
```

for values and accumulated answers when constraints can exceed `int`.

Example:

```text
1e9 * 1e9
=
1e18
```

`int` cannot hold this.

`long long` can hold values up to roughly:

```text
9e18
```

### Recommended default

```cpp
long long answer = 0;
long long runningSum = 0;
long long currentCost = 0;
```

If a comparator multiplies values whose product can exceed 64-bit, use a wider intermediate type. Otherwise keep the normal code in `long long`.

---

## 0.24 Greedy Proof Toolbox

When you think a problem is greedy, classify the proof.

| Proof style | Question to ask | Typical forms |
|---|---|---|
| Exchange | Can I replace/swap a worse choice? | sorting, comparator, pairing |
| Dominance | Is one candidate always at least as good? | running min/max |
| Forced choice | Is only one move sensible/possible? | reverse greedy |
| Invariant | What stays true after each choice? | constructive greedy |
| Earliest finish | Which choice leaves most future room? | interval scheduling |
| Farthest reach | Which choice extends coverage most? | interval covering |
| Bottleneck fixing | Can I fix MIN/MAX and optimize the rest? | `SUM * MIN` |
| Replace worst | If infeasible, which chosen item should leave? | heap scheduling |
| Balance | Where do left/right costs balance? | median |
| Event sweep | Where can the state actually change? | interval overlap |

---

## 0.25 What to Write Before Coding

For every problem in this sheet, write these five lines first:

```text
1. STATE
What information matters right now?

2. CHOICE
What exactly am I choosing greedily?

3. ORDER
Do I need sorting / reverse processing / sweep order?

4. PROOF
Why can this choice not hurt the optimum?

5. DATA STRUCTURE
Do I need:
min/max
two pointers
heap
multiset
frequency array
events
?
```

### Example

For a Top-K bottleneck problem:

```text
STATE:
current bottleneck value

CHOICE:
best K-1 previous contributions

ORDER:
bottleneck descending

PROOF:
once current bottleneck is fixed,
only contribution sum remains to maximize

DATA STRUCTURE:
min-heap + running sum
```

This is enough to convert the greedy idea into code.

---

## 0.26 60-Second Recognition Checklist

When reading a new problem, ask in this order:

```text
1. Can sorting make the choices monotonic?

2. Can I prove an adjacent swap is safe?

3. Are two sorted groups being matched?

4. Is this interval selection?
   earliest finish?

5. Is this interval covering?
   farthest reach?

6. Does the current position need
   only min/max from the past?

7. Do I need Top-K from eligible candidates?
   heap?

8. Does the objective contain:
   SUM * MIN
   SUM * MAX
   ?
   Fix the bottleneck.

9. Is forward simulation branching?
   Reverse it.

10. Is the cost:
    sum |x-ai|?
    Median.

11. Are only frequencies relevant?

12. Is this a construction?
    What invariant must stay true?

13. Are there interval endpoints / huge coordinates?
    Sweep line.

14. Are all queries known beforehand?
    Offline sorting + active set.

15. If feasibility breaks,
    can I remove the worst chosen item?
```

---

## 0.27 Minimum C++ Toolkit for This Sheet

You should be comfortable writing these without looking them up:

```cpp
sort(values.begin(), values.end());

sort(values.rbegin(), values.rend());

min(currentMinimum, value);

max(currentMaximum, value);
```

Min-heap:

```cpp
priority_queue<
    long long,
    vector<long long>,
    greater<long long>
> minimumHeap;
```

Max-heap:

```cpp
priority_queue<long long> maximumHeap;
```

Multiset:

```cpp
multiset<long long> activeValues;
```

Custom sort:

```cpp
bool compare(const Item& firstItem,
             const Item& secondItem) {

    return firstItem.key < secondItem.key;
}
```

Events:

```cpp
vector<pair<long long, long long>> events;

events.push_back({left, +1});
events.push_back({right, -1});

sort(events.begin(), events.end());
```

Running best:

```cpp
long long minimumSeen = INF;

for (long long value : values) {
    minimumSeen = min(minimumSeen, value);
}
```

Top-K running sum:

```cpp
priority_queue<
    long long,
    vector<long long>,
    greater<long long>
> topValues;

long long topSum = 0;

for (long long value : values) {
    topValues.push(value);
    topSum += value;

    if ((int)topValues.size() > k) {
        topSum -= topValues.top();
        topValues.pop();
    }
}
```

---

## 0.28 Prerequisite Readiness Test

Before starting Form 1, you are ready if you can explain these without notes:

```text
Why greedy needs a proof.

What an exchange argument is.

Why sorting can remove inversions.

How two pointers work after sorting.

Difference between:
earliest finish
and
farthest reach.

How to maintain:
running minimum
Top-K values

Why SUM * MIN suggests fixing MIN.

Why reverse greedy can become forced.

Why median minimizes absolute distance.

How +1/-1 events create a sweep line.

How to remove one copy from a multiset.

What invariant means in a constructive problem.
```

You do **not** need advanced theory beyond this before solving the pattern list.

The problems themselves are where these ideas become automatic.

---

# Form 1 — Sort + Take Best / Extremes

**Recognition clue:**

```text
Each item has one useful value.
Taking a locally better item can never hurt later choices.

Think:
sort
→ take cheapest / largest / smallest first
```

**Proof model:** exchange a worse chosen item with a better available item.

**Problems:** 5

| # | Problem | Platform | Rating |
|---:|---|:---:|---:|
| 1 | [Sale](https://codeforces.com/problemset/problem/34/B) | CF | 800 |
| 2 | [Twins](https://codeforces.com/problemset/problem/160/A) | CF | 900 |
| 3 | [Dragons](https://codeforces.com/problemset/problem/230/A) | CF | 1000 |
| 4 | [Monsters](https://codeforces.com/problemset/problem/1849/B) | CF | 1000 |
| 5 | [Queue](https://codeforces.com/problemset/problem/545/D) | CF | 1300 |

**Recognition model:**

```text
"pick best available repeatedly"
+
no future dependency on which equivalent item was skipped
→ sort first
```

↑ [Back to TOC](#clickable-toc)

---

# Form 2 — Greedy Matching + Two Pointers

**Recognition clue:**

```text
Two sorted groups must be paired.

If the smallest item cannot match the current opposite item,
decide immediately which pointer must move.
```

**Proof model:** leaving a smaller feasible item unused cannot create a better future matching.

**Problems:** 5

| # | Problem | Platform | Rating |
|---:|---|:---:|---:|
| 1 | [BerSU Ball](https://codeforces.com/problemset/problem/489/B) | CF | 1200 |
| 2 | [Boats Competition](https://codeforces.com/problemset/problem/1399/C) | CF | 1200 |
| 3 | [Dress'em in Vests!](https://codeforces.com/problemset/problem/161/A) | CF | 1300 |
| 4 | [Apartments](https://cses.fi/problemset/task/1084/) | CSES | — |
| 5 | [Boats to Save People](https://leetcode.com/problems/boats-to-save-people/) | LC | Medium |

**Recognition model:**

```text
sort both sides
→ compare smallest / largest candidates
→ match or discard one side
```

↑ [Back to TOC](#clickable-toc)

---

# Form 3 — Rearrangement / Pairing / Exchange

**Recognition clue:**

```text
You may permute / reorder values.

Objective contains:
pair contribution
dot product
weighted positions
min/max after pairing
```

Think:

```text
compare two positions
→ swap them
→ derive which relative order is better
```

**Problems:** 5

| # | Problem | Platform | Rating |
|---:|---|:---:|---:|
| 1 | [Array Balancing](https://codeforces.com/problemset/problem/1661/A) | CF | 800 |
| 2 | [Challenging Cliffs](https://codeforces.com/problemset/problem/1537/C) | CF | 1200 |
| 3 | [RationalLee](https://codeforces.com/problemset/problem/1369/C) | CF | 1400 |
| 4 | [Little Girl and Maximum Sum](https://codeforces.com/problemset/problem/276/C) | CF | 1500 |
| 5 | [Advantage Shuffle](https://leetcode.com/problems/advantage-shuffle/) | LC | Medium |

**Core proof model:**

```text
current arrangement
vs
swapped arrangement

subtract
→ factor
→ determine sign
→ derive correct order
```

↑ [Back to TOC](#clickable-toc)

---

# Form 4 — Interval Scheduling / Earliest Finish

**Recognition clue:**

```text
Choose maximum number of non-overlapping intervals.

The interval finishing earliest leaves
the maximum remaining space for future intervals.
```

**Greedy choice:**

```text
sort by end time
→ take next compatible interval
```

**Problems:** 5

| # | Problem | Platform | Rating |
|---:|---|:---:|---:|
| 1 | [Movie Festival](https://cses.fi/problemset/task/1629/) | CSES | — |
| 2 | [Non-overlapping Intervals](https://leetcode.com/problems/non-overlapping-intervals/) | LC | Medium |
| 3 | [Minimum Number of Arrows to Burst Balloons](https://leetcode.com/problems/minimum-number-of-arrows-to-burst-balloons/) | LC | Medium |
| 4 | [Maximum Length of Pair Chain](https://leetcode.com/problems/maximum-length-of-pair-chain/) | LC | Medium |
| 5 | [Woodcutters](https://codeforces.com/problemset/problem/545/C) | CF | 1500 |

**Recognition model:**

```text
maximize number selected
+
interval conflicts
→ earliest finishing feasible interval
```

↑ [Back to TOC](#clickable-toc)

---

# Form 5 — Interval Cover / Farthest Reach

**Recognition clue:**

```text
Need minimum number of moves / intervals
to reach or cover a target.

Among choices available now:
take the one extending reach farthest.
```

**Difference from Form 4:**

```text
Activity selection:
finish as EARLY as possible.

Covering:
extend as FAR as possible.
```

**Problems:** 5

| # | Problem | Platform | Rating |
|---:|---|:---:|---:|
| 1 | [Jump Game](https://leetcode.com/problems/jump-game/) | LC | Medium |
| 2 | [Jump Game II](https://leetcode.com/problems/jump-game-ii/) | LC | Medium |
| 3 | [Video Stitching](https://leetcode.com/problems/video-stitching/) | LC | Medium |
| 4 | [Minimum Number of Taps to Open to Water a Garden](https://leetcode.com/problems/minimum-number-of-taps-to-open-to-water-a-garden/) | LC | Hard |
| 5 | [Social Distance](https://codeforces.com/problemset/problem/1367/C) | CF | 1300 |

**Recognition model:**

```text
current covered prefix
→ inspect everything reachable now
→ choose maximum next reach
```

↑ [Back to TOC](#clickable-toc)

---

# Form 6 — Running Best / Cheapest So Far

**Recognition clue:**

```text
At position i,
any earlier option may be used.

Only the best earlier value matters.
```

Think:

```text
minimum required?
→ prefix minimum

maximum required?
→ prefix maximum
```

**Problems:** 5

| # | Problem | Platform | Rating |
|---:|---|:---:|---:|
| 1 | [Helmets in Night Light](https://codeforces.com/problemset/problem/1876/A) | CF | 1000 |
| 2 | [Quests](https://codeforces.com/problemset/problem/1914/C) | CF | 1100 |
| 3 | [Strange Birthday Party](https://codeforces.com/problemset/problem/1470/A) | CF | 1300 |
| 4 | [Best Time to Buy and Sell Stock](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/) | LC | Easy |
| 5 | [Gas Station](https://leetcode.com/problems/gas-station/) | LC | Medium |

**Recognition model:**

```text
future cannot repair an already-paid past cost
→ summarize previous candidates by one best value
```

↑ [Back to TOC](#clickable-toc)

---

# Form 7 — Custom Comparator / Exchange Scheduling

**Recognition clue:**

```text
Order changes total score / penalty.

Each item has 2 properties:
time + penalty
cost + duration
a + b contribution
```

Do not guess the comparator.

Use:

```text
A before B
vs
B before A
```

then simplify.

**Problems:** 5

| # | Problem | Platform | Rating |
|---:|---|:---:|---:|
| 1 | [Planning](https://codeforces.com/problemset/problem/853/A) | CF | 1500 |
| 2 | [Stas and the Queue at the Buffet](https://codeforces.com/problemset/problem/1151/D) | CF | 1600 |
| 3 | [Petya and Exam](https://codeforces.com/problemset/problem/1282/C) | CF | 1800 |
| 4 | [Tasks and Deadlines](https://cses.fi/problemset/task/1630/) | CSES | — |
| 5 | [Minimum Initial Energy to Finish Tasks](https://leetcode.com/problems/minimum-initial-energy-to-finish-tasks/) | LC | Hard |

**Recognition model:**

```text
ordering matters
→ isolate two neighboring items
→ compare AB with BA
→ derive comparator
```

↑ [Back to TOC](#clickable-toc)

---

# Form 8 — Deadline / Feasibility + Heap

**Recognition clue:**

```text
Process tasks in some natural order.

Tentatively accept tasks.

If feasibility breaks:
remove the worst chosen task.
```

This is one of the most important heap-greedy forms.

**Problems:** 5

| # | Problem | Platform | Rating |
|---:|---|:---:|---:|
| 1 | [Potions — Hard Version](https://codeforces.com/problemset/problem/1526/C2) | CF | 1600 |
| 2 | [Contest Balloons](https://codeforces.com/problemset/problem/725/D) | CF | 1800 |
| 3 | [Course Schedule III](https://leetcode.com/problems/course-schedule-iii/) | LC | Hard |
| 4 | [Maximum Number of Events That Can Be Attended](https://leetcode.com/problems/maximum-number-of-events-that-can-be-attended/) | LC | Medium |
| 5 | [Movie Festival II](https://cses.fi/problemset/task/1632/) | CSES | — |

**Recognition model:**

```text
accept everything possible
→ constraint violated
→ remove worst selected contribution
```

Typical structure:

```text
sort
+
running state
+
heap
```

↑ [Back to TOC](#clickable-toc)

---

# Form 9 — Reverse Greedy / Forced Previous Operation

**Recognition clue:**

```text
Forward direction has many choices.

Backward direction has a forced move.
```

Think:

```text
target → start
```

instead of:

```text
start → target
```

**Problems:** 5

| # | Problem | Platform | Rating |
|---:|---|:---:|---:|
| 1 | [Divide it!](https://codeforces.com/problemset/problem/1176/A) | CF | 800 |
| 2 | [Transformation: from A to B](https://codeforces.com/problemset/problem/727/A) | CF | 1000 |
| 3 | [Getting Zero](https://codeforces.com/problemset/problem/1661/B) | CF | 1300 |
| 4 | [Two Buttons](https://codeforces.com/problemset/problem/520/B) | CF | 1400 |
| 5 | [Broken Calculator](https://leetcode.com/problems/broken-calculator/) | LC | Medium |

**Recognition model:**

```text
forward:
branching

reverse:
odd/even/divisibility/digit
forces previous step
```

↑ [Back to TOC](#clickable-toc)

---

# Form 10 — Median / Absolute Distance / Meeting Point

**Recognition clue:**

```text
minimize:

sum |x - ai|
```

Think:

```text
median
```

For Manhattan:

```text
sum(
    |x-xi|
    +
    |y-yi|
)

separate x and y
→ median x
→ median y
```

**Problems:** 5

| # | Problem | Platform | Rating |
|---:|---|:---:|---:|
| 1 | [Friends Meeting](https://codeforces.com/problemset/problem/931/A) | CF | 800 |
| 2 | [Optimal Point on a Line](https://codeforces.com/problemset/problem/710/B) | CF | 1400 |
| 3 | [Arranging The Sheep](https://codeforces.com/problemset/problem/1520/E) | CF | 1400 |
| 4 | [Stick Lengths](https://cses.fi/problemset/task/1074/) | CSES | — |
| 5 | [Best Meeting Point](https://leetcode.com/problems/best-meeting-point/) | LC | Hard |

**Recognition model:**

```text
absolute distance
→ balance mass on left and right
→ median
```

↑ [Back to TOC](#clickable-toc)

---

# Form 11 — Fix Bottleneck + Top-K Heap

**Recognition clue:**

Objective looks like:

```text
SUM(selected values)
*
MIN(selected bottleneck)
```

or a similar:

```text
aggregate
×
bottleneck
```

Fix the bottleneck first.

Then optimize the remaining part.

Typical solution:

```text
sort bottleneck descending
+
maintain Top-K additive values
using min-heap
```

**Problems:** 4

| # | Problem | Platform | Rating |
|---:|---|:---:|---:|
| 1 | [Playlist](https://codeforces.com/problemset/problem/1140/C) | CF | 1600 |
| 2 | [Maximum Performance of a Team](https://leetcode.com/problems/maximum-performance-of-a-team/) | LC | Hard |
| 3 | [Maximum Subsequence Score](https://leetcode.com/problems/maximum-subsequence-score/) | LC | Medium |
| 4 | [Minimum Cost to Hire K Workers](https://leetcode.com/problems/minimum-cost-to-hire-k-workers/) | LC | Hard |

**Recognition model:**

```text
score = SUM * MIN

fix MIN
→ eligible candidates become a prefix
→ maximize SUM with Top-K heap
```

↑ [Back to TOC](#clickable-toc)

---

# Form 12 — String / Digit Construction Greedy

**Recognition clue:**

```text
Build lexicographically smallest/largest answer
or optimize digits/characters.

At each position:
choose the best character that cannot make
the remaining suffix impossible.
```

**Problems:** 5

| # | Problem | Platform | Rating |
|---:|---|:---:|---:|
| 1 | [Chewbacca and Number](https://codeforces.com/problemset/problem/514/A) | CF | 1200 |
| 2 | [Given Length and Sum of Digits...](https://codeforces.com/problemset/problem/489/C) | CF | 1400 |
| 3 | [Binary String Minimizing](https://codeforces.com/problemset/problem/1256/D) | CF | 1500 |
| 4 | [Make Palindrome](https://codeforces.com/problemset/problem/600/C) | CF | 1800 |
| 5 | [Dense Subsequence](https://codeforces.com/problemset/problem/724/D) | CF | 1900 |

**Recognition model:**

```text
construct answer left → right
+
smallest / largest valid next choice
+
preserve feasibility of suffix
```

↑ [Back to TOC](#clickable-toc)

---

# Form 13 — Frequency / Group Formation Greedy

**Recognition clue:**

```text
Items are interchangeable inside a category.

Only frequencies / counts matter.

Need:
maximum groups
distinct group sizes
consume dominant frequency safely
```

**Problems:** 5

| # | Problem | Platform | Rating |
|---:|---|:---:|---:|
| 1 | [Young Explorers](https://codeforces.com/problemset/problem/1355/B) | CF | 1200 |
| 2 | [A and B and Team Training](https://codeforces.com/problemset/problem/519/C) | CF | 1300 |
| 3 | [Candy Box — Easy Version](https://codeforces.com/problemset/problem/1183/D) | CF | 1400 |
| 4 | [Epic Transformation](https://codeforces.com/problemset/problem/1506/D) | CF | 1400 |
| 5 | [Table Decorations](https://codeforces.com/problemset/problem/478/C) | CF | 1800 |

**Recognition model:**

```text
identity of item does not matter
→ compress to frequency
→ consume counts in greedy order
```

↑ [Back to TOC](#clickable-toc)

---

# Form 14 — Constructive Greedy + Maintain Invariant

**Recognition clue:**

```text
Need to construct:
array
permutation
sequence
bracket string
placement

Many answers are valid.

Maintain one invariant after every greedy placement.
```

**Problems:** 5

| # | Problem | Platform | Rating |
|---:|---|:---:|---:|
| 1 | [Increasing Subsequence — Easy Version](https://codeforces.com/problemset/problem/1157/C1) | CF | 1300 |
| 2 | [Constructing the Array](https://codeforces.com/problemset/problem/1353/D) | CF | 1600 |
| 3 | [Platforms Jumping](https://codeforces.com/problemset/problem/1256/C) | CF | 1700 |
| 4 | [Recover an RBS](https://codeforces.com/problemset/problem/1709/C) | CF | 1800 |
| 5 | [Permutation Restoration](https://codeforces.com/problemset/problem/1701/D) | CF | 1900 |

**Recognition model:**

```text
not asking for one numeric optimum only
+
must OUTPUT a valid construction
→ greedy placement + invariant
```

↑ [Back to TOC](#clickable-toc)

---

# Form 15 — Resource Allocation / Replace Worst Choice

**Recognition clue:**

```text
You have limited resources.

Use cheap resources first.

When expensive resource is unavoidable:
spend it only on the largest / most valuable gap.

Or:
keep chosen candidates
and replace the worst one when better information arrives.
```

**Problems:** 5

| # | Problem | Platform | Rating |
|---:|---|:---:|---:|
| 1 | [Box Fitting](https://codeforces.com/problemset/problem/1498/B) | CF | 1300 |
| 2 | [Exam in BerSU — Hard Version](https://codeforces.com/problemset/problem/1185/C2) | CF | 1700 |
| 3 | [IPO](https://leetcode.com/problems/ipo/) | LC | Hard |
| 4 | [Furthest Building You Can Reach](https://leetcode.com/problems/furthest-building-you-can-reach/) | LC | Medium |
| 5 | [Minimum Number of Refueling Stops](https://leetcode.com/problems/minimum-number-of-refueling-stops/) | LC | Hard |

**Recognition model:**

```text
limited resource
+
many candidate costs
→ heap tracks which choices deserve the expensive resource
```

↑ [Back to TOC](#clickable-toc)

---

# Form 16 — Sweep Line / Overlap / Coverage

> Sweep line is not "pure greedy" in every problem, but it belongs directly beside the greedy/interval material in your current study sequence.

**Recognition clue:**

```text
Large coordinates
+
interval start/end
+
need active count / union / overlap
```

Convert:

```text
L → +1
R → -1
```

then sort events.

**Problems:** 5

| # | Problem | Platform | Rating |
|---:|---|:---:|---:|
| 1 | [Restaurant Customers](https://cses.fi/problemset/task/1619/) | CSES | — |
| 2 | [Covered Points Count](https://codeforces.com/problemset/problem/1000/C) | CF | 1700 |
| 3 | [Meeting Rooms II](https://leetcode.com/problems/meeting-rooms-ii/) | LC | Medium |
| 4 | [My Calendar III](https://leetcode.com/problems/my-calendar-iii/) | LC | Hard |
| 5 | [The Skyline Problem](https://leetcode.com/problems/the-skyline-problem/) | LC | Hard |

**Recognition model:**

```text
state changes only at endpoints
→ events
→ sort
→ maintain active state
```

↑ [Back to TOC](#clickable-toc)

---

# Form 17 — Offline Interval Queries + Active Set

**Recognition clue:**

```text
Intervals + point queries are all known beforehand.

For query Y:
need information about intervals satisfying L <= Y <= R.
```

Think:

```text
offline sorting
+
active heap / multiset / ordered set
```

**Problems:** 5

| # | Problem | Platform | Rating |
|---:|---|:---:|---:|
| 1 | [Minimum Interval to Include Each Query](https://leetcode.com/problems/minimum-interval-to-include-each-query/) | LC | Hard |
| 2 | [Number of Flowers in Full Bloom](https://leetcode.com/problems/number-of-flowers-in-full-bloom/) | LC | Hard |
| 3 | [Closest Room](https://leetcode.com/problems/closest-room/) | LC | Hard |
| 4 | [Room Allocation](https://cses.fi/problemset/task/1164/) | CSES | — |
| 5 | [In Love](https://codeforces.com/problemset/problem/1883/D) | CF | 1400 |

**Recognition model:**

```text
queries can be reordered
→ sort events / queries

active candidates change as sweep moves
→ heap / multiset / ordered set
```

↑ [Back to TOC](#clickable-toc)

---

# Form 18 — Advanced Greedy Transformation

**Recognition clue:**

These problems usually do **not** look greedy immediately.

Typical signals:

```text
operation affects a suffix / future
process from constrained end

objective can be rewritten as contributions

current deficit forces minimum operations

track transition positions instead of full string

only one structural break is allowed
```

**Problems:** 5

| # | Problem | Platform | Rating |
|---:|---|:---:|---:|
| 1 | [Powered Addition](https://codeforces.com/problemset/problem/1338/A) | CF | 1500 |
| 2 | [Mark and Lightbulbs](https://codeforces.com/problemset/problem/1705/D) | CF | 1800 |
| 3 | [Shift and Reverse](https://codeforces.com/problemset/problem/1907/F) | CF | 1800 |
| 4 | [Array Splitting](https://codeforces.com/problemset/problem/1175/D) | CF | 1900 |
| 5 | [Progressions Covering](https://codeforces.com/problemset/problem/1661/D) | CF | 1900 |

**Recognition model:**

```text
story looks global
        ↓
rewrite state / contribution / deficit
        ↓
a local action becomes forced
        ↓
greedy
```

↑ [Back to TOC](#clickable-toc)

---

# Final Practice Order

Do **not** solve all 18 forms completely before doing mixed problems.

Use this sequence:

```text
PHASE 1 — CORE GREEDY
Forms 1–4
sort / matching / exchange / intervals

        ↓

PHASE 2 — DECISION GREEDY
Forms 5–9
cover / running best / comparator / heap / reverse

        ↓

PHASE 3 — CLASSICAL OPTIMIZATION
Forms 10–11
median / Manhattan / bottleneck + Top-K

        ↓

PHASE 4 — CF CONSTRUCTION
Forms 12–15
strings / frequencies / constructive / resources

        ↓

PHASE 5 — SWEEP + ADVANCED
Forms 16–18
events / active set / 1800–1900 transformations
```

### Recommended solving rule

For each form:

```text
Problem 1
→ understand form

Problem 2
→ reproduce idea yourself

Problem 3
→ solve without looking at form name

Problem 4
→ stretch

Problem 5
→ advanced / transfer problem
```

After every **3–4 forms**, stop pattern-wise practice and solve:

```text
5–10 mixed Codeforces A/B/C/D problems
```

without looking at the topic label.

---

# Master Recognition Map

```text
GREEDY
│
├── choose best item now
│   └── sort / extremes
│
├── pair two ordered groups
│   └── two pointers
│
├── rearrange positions
│   └── exchange proof
│
├── choose intervals
│   ├── earliest finish
│   └── farthest reach
│
├── previous options become eligible
│   ├── running min/max
│   └── heap / multiset
│
├── order affects cost
│   └── two-item comparator
│
├── forward branching is hard
│   └── reverse greedy
│
├── absolute distance
│   └── median
│
├── SUM × MIN / bottleneck
│   └── sort bottleneck + Top-K heap
│
├── output a valid construction
│   └── maintain invariant
│
├── intervals + huge coordinates
│   └── sweep line
│
└── global-looking 1800–1900 task
    └── transform until local choice becomes forced
```

---

# One Rule to Remember

Do not memorize:

```text
"sort ascending"
"take minimum"
"use heap"
```

Instead ask:

```text
What is my greedy choice?

Why can choosing it now never make
the final answer worse?

Can I prove that by:
- exchange?
- forced move?
- invariant?
- bottleneck fixing?
- earliest finish?
- farthest reach?
- replacing worst chosen item?
```

That is the skill this sheet is designed to train.
