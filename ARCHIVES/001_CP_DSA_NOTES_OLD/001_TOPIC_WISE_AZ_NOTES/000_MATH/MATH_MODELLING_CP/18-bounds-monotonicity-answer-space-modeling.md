# CP Mathematical Modeling Mini-Course

## 18. Bounds, Monotonicity & Answer-Space Modeling

> **Goal:** Learn to recognize optimization problems where finding the answer directly is difficult, but checking a proposed answer `X` is easy.
>
> Core transformation:
>
> ```text
> "What is the minimum/maximum answer?"
>                 ↓
> "If I give you X, can you tell me
>  whether X is possible?"
>                 ↓
> find a monotone YES/NO boundary
> ```

---

# Chapter Tree

```text
18. BOUNDS, MONOTONICITY & ANSWER-SPACE MODELING
│
├── 18.1 Decision vs optimization
├── 18.2 Candidate answer X
├── 18.3 Feasibility predicate
├── 18.4 What monotonicity means
├── 18.5 False→True answer space
├── 18.6 True→False answer space
├── 18.7 Lower and upper bounds
├── 18.8 Minimize the maximum
├── 18.9 Maximize the minimum
├── 18.10 Capacity modeling
├── 18.11 Distance modeling
├── 18.12 Time modeling
├── 18.13 Operations-count modeling
├── 18.14 Integer answer spaces
├── 18.15 Proving monotonicity
├── 18.16 Common traps
└── 18.17 CF-style workflow
```

---

# 18.1 Optimization vs Decision

Optimization asks:

```text
What is the minimum X?
```

Decision asks:

```text
Can I succeed with X?
```

Example:

> Find the minimum truck capacity needed to transport all goods.

Direct question:

```text
minimum capacity = ?
```

Transform it:

```text
Can capacity X transport everything?
```

For a fixed `X`, simulation/counting may be easy.

That is the key idea.

---

# 18.2 Candidate Answer X

Treat the answer as a variable:

```text
X
```

Then temporarily pretend:

```text
"The answer is X."
```

Ask:

```text
Can I satisfy all constraints?
```

Define:

```text
possible(X)
```

which returns:

```text
true / false
```

Now the optimization problem becomes a sequence of yes/no questions.

---

# 18.3 Example — Capacity

Weights:

```text
[4,7,2,5]
```

Suppose one container must hold every individual item.

Question:

```text
Is capacity X sufficient?
```

Necessary condition:

```text
X >= max(weight)
```

For:

```text
X = 6
```

false because item `7` does not fit.

For:

```text
X = 7
```

true.

For:

```text
X = 8,9,10,...
```

also true.

Pattern:

```text
X:
1 2 3 4 5 6 7 8 9 10 ...

possible:
F F F F F F T T T T ...
            ^
         boundary
```

Once sufficient, more capacity cannot hurt.

That is monotonicity.

---

# 18.4 What Is Monotonicity?

For answer-space searching, monotonicity means the feasibility result changes in only one direction.

Typical pattern A:

```text
F F F F T T T T
```

There is a first feasible answer.

Typical pattern B:

```text
T T T T F F F F
```

There is a last feasible answer.

What must NOT happen:

```text
F T F T T F
```

because there is no single boundary.

---

# 18.5 Minimize X

Suppose:

```text
larger X makes the task easier
```

Then feasibility often looks like:

```text
F F F F T T T T
```

Goal:

```text
find first T
```

Examples:

```text
minimum capacity
minimum allowed maximum
minimum time needed
minimum speed required
minimum radius
```

Mental template:

```text
minimize X
+
larger X helps
=
find first feasible X
```

---

# 18.6 Maximize X

Suppose:

```text
larger X makes the requirement harder
```

Then:

```text
T T T T F F F F
```

Goal:

```text
find last T
```

Examples:

```text
maximum minimum distance
maximum guaranteed value
largest threshold that can still be satisfied
```

Mental template:

```text
maximize X
+
larger X is harder
=
find last feasible X
```

---

# 18.7 Bounds

Before searching answer space, determine:

```text
LOW <= answer <= HIGH
```

Do not choose arbitrary huge bounds when simple mathematical bounds exist.

Example: partition positive task sizes while minimizing maximum group sum.

Lower bound:

```text
max(a)
```

because the largest single task must belong somewhere.

Upper bound:

```text
sum(a)
```

because putting everything into one group is always a valid extreme case.

Thus:

```text
max(a) <= answer <= sum(a)
```

---

# 18.8 Deriving Lower Bounds

Ask:

```text
What value can the answer NEVER go below?
```

Common lower bounds:

```text
0
1
max single requirement
minimum necessary distance
ceil(total/resources)
largest element
```

Example:

Total work:

```text
100
```

Workers:

```text
4
```

Even with perfect distribution, maximum worker load cannot be below:

```text
ceil(100/4) = 25
```

So:

```text
answer >= 25
```

There may also be:

```text
answer >= largest indivisible job
```

Combine lower bounds with `max`.

---

# 18.9 Deriving Upper Bounds

Ask:

```text
What simple strategy definitely works?
```

The cost of any valid construction gives an upper bound.

Example:

Put all positive tasks into one group:

```text
cost = sum(a)
```

Therefore:

```text
answer <= sum(a)
```

This is a powerful habit:

```text
impossibility argument → lower bound
simple valid construction → upper bound
```

---

# 18.10 Minimize the Maximum

This pattern appears constantly.

Suppose groups have costs:

```text
c1,c2,...,ck
```

Objective:

```text
minimize max(ci)
```

Introduce:

```text
X = allowed maximum cost
```

Decision problem:

```text
Can I construct a solution
where every ci <= X?
```

Then:

```text
small X = difficult
large X = easier
```

So:

```text
F F F T T T
```

Find first true.

---

# 18.11 Maximize the Minimum

Suppose selected objects must have some minimum quality/distance:

```text
min(d1,d2,...)
```

Objective:

```text
maximize that minimum
```

Introduce:

```text
X = required minimum
```

Check:

```text
Can I build a solution
where every relevant value >= X?
```

Now:

```text
small X = easy
large X = harder
```

Pattern:

```text
T T T T F F
```

Find last true.

---

# 18.12 Example — Place Objects Far Apart

Positions:

```text
1 2 4 8 9
```

Suppose place 3 objects and maximize the minimum distance.

Instead of directly asking:

```text
best minimum distance = ?
```

ask:

```text
Can I place 3 objects
with every consecutive chosen distance >= X?
```

For fixed `X`, a greedy check can be:

```text
place first object
then repeatedly choose earliest position
at least X away
```

Possible results:

```text
X=1 -> YES
X=2 -> YES
X=3 -> YES
X=4 -> NO
X=5 -> NO
```

So answer is last YES:

```text
3
```

Notice the architecture:

```text
optimization
    ↓
binary search answer X
    ↓
greedy feasibility check
```

Binary search and greedy are solving different layers.

---

# 18.13 Why Greedy Check Can Work

For fixed minimum distance `X`, choosing the earliest possible next position leaves the most room for future placements.

So the feasibility check itself may use a greedy proof.

This is common in CF:

```text
outer layer:
answer-space search

inner layer:
greedy / counting / simulation
```

Do not confuse the two.

---

# 18.14 Time as an Answer

A common story:

> Machines produce items. Find minimum time to produce at least K items.

Direct simulation over time may be impossible.

Candidate:

```text
T = proposed time
```

Machine `i` taking `a[i]` time per item produces:

```text
floor(T / a[i])
```

items by time `T`.

Total:

```text
produced(T)
=
sum floor(T/a[i])
```

Feasible when:

```text
produced(T) >= K
```

As `T` increases:

```text
produced(T)
```

never decreases.

Therefore:

```text
possible(T)
```

is monotone:

```text
F F F T T T ...
```

Find first true.

---

# 18.15 Example — Machines

Machine times:

```text
2,3
```

Need:

```text
K = 5
```

At:

```text
T=3
```

production:

```text
floor(3/2)+floor(3/3)
=
1+1
=
2
```

Not enough.

At:

```text
T=6
```

production:

```text
3+2=5
```

enough.

At any larger time, production cannot decrease.

So minimum time is found at the false/true boundary.

---

# 18.16 Speed as an Answer

Story:

> Find minimum integer speed to finish work before deadline H.

Candidate:

```text
speed = X
```

If task sizes are:

```text
a[i]
```

and each takes:

```text
ceil(a[i]/X)
```

time units, then:

```text
time(X)
=
sum ceil(a[i]/X)
```

As speed increases:

```text
time(X)
```

does not increase.

Feasible:

```text
time(X) <= H
```

So:

```text
small speed -> false
large speed -> true
```

Again:

```text
first feasible X
```

---

# 18.17 Ceiling Division

When modeling:

```text
how many groups/units of size X
are needed for amount A?
```

use:

```text
ceil(A/X)
```

For positive integers:

```text
(A + X - 1) / X
```

with integer division.

This appears frequently inside feasibility functions.

Example:

```text
A=10
X=3
```

Need:

```text
4
```

groups.

```text
(10+3-1)/3
=
12/3
=
4
```

---

# 18.18 Operations Count as a Function of X

Suppose each operation can reduce a value by at most `X`.

For amount:

```text
a[i]
```

number of operations may be:

```text
ceil(a[i]/X)
```

Total required operations:

```text
ops(X)
=
sum ceil(a[i]/X)
```

As `X` increases:

```text
ops(X)
```

usually decreases or stays equal.

If allowed operations are at most `K`:

```text
ops(X) <= K
```

becomes a monotone predicate.

---

# 18.19 Capacity as a Function of X

Suppose sequential items must be packed into at most `K` containers, preserving order, each with capacity `X`.

For fixed `X`:

```text
scan left to right
accumulate current load

if next item exceeds X:
    start new container
```

Count required containers:

```text
containers(X)
```

As capacity increases:

```text
containers(X)
```

cannot increase.

Feasible:

```text
containers(X) <= K
```

So minimum capacity is first feasible `X`.

---

# 18.20 Why Monotonicity Must Be Proven

Do not think:

```text
"optimization → binary search"
```

That is wrong.

You need:

```text
candidate X
+
cheap feasibility check
+
monotone feasibility
```

Ask:

```text
If X works, why must X+1 work?
```

or:

```text
If X fails, why must every larger X fail?
```

If you cannot explain the direction, do not binary search yet.

---

# 18.21 Proof Template — Larger Helps

Suppose `X` is a capacity.

If capacity `X` works, then capacity:

```text
Y >= X
```

also works because we can reuse exactly the same solution.

Every load that was:

```text
<= X
```

is also:

```text
<= Y
```

Therefore feasibility is monotone.

This "reuse the same solution" proof is extremely useful.

---

# 18.22 Proof Template — Larger Requirement Hurts

Suppose `X` is required minimum distance.

If requirement `X` fails, then any:

```text
Y > X
```

also fails.

Why?

Any arrangement satisfying distance `Y` would automatically satisfy the weaker requirement `X`.

But no arrangement satisfies `X`.

Contradiction.

Therefore:

```text
failure propagates upward
```

---

# 18.23 Answer Space vs Input Space

Normal binary search:

```text
search sorted array for a value
```

Binary search on answer:

```text
search possible answers
```

Example answer space:

```text
capacity:
1 2 3 4 5 6 7 8 ...
```

We are not searching for an element stored in an array.

We are searching for the boundary where:

```text
possible(X)
```

changes.

---

# 18.24 Integer Answer Space

If answer is integer, think in discrete states:

```text
L, L+1, L+2, ..., R
```

You want either:

```text
first true
```

or:

```text
last true
```

Keep this conceptual target clear before writing binary-search code.

Most binary-search bugs come from not defining the boundary precisely.

---

# 18.25 First True Template

Predicate:

```text
F F F F T T T T
```

Want:

```text
first T
```

Conceptual invariant:

```text
answer lies inside [lo,hi]
```

Typical implementation:

```cpp
while (lo < hi) {
    long long mid = lo + (hi - lo) / 2;

    if (possible(mid))
        hi = mid;
    else
        lo = mid + 1;
}

cout << lo;
```

Use this only after the mathematical predicate and bounds are correct.

---

# 18.26 Last True Template

Predicate:

```text
T T T T F F F
```

Want:

```text
last T
```

Use upper midpoint to avoid infinite loops:

```cpp
while (lo < hi) {
    long long mid = lo + (hi - lo + 1) / 2;

    if (possible(mid))
        lo = mid;
    else
        hi = mid - 1;
}

cout << lo;
```

Again, the code is secondary.

The modeling is:

```text
What does possible(X) mean?
Why is it monotone?
What boundary do I want?
```

---

# 18.27 Search Complexity

If answer range is:

```text
[0, 10^18]
```

binary search needs only about:

```text
60
```

iterations because:

```text
2^60 > 10^18
```

So huge numeric answer spaces can be manageable.

If feasibility costs:

```text
O(n)
```

total complexity is roughly:

```text
O(n log R)
```

where `R` is answer-space size.

---

# 18.28 Overflow Inside Feasibility

Suppose:

```text
produced += T / a[i]
```

and both `T` and `n` are large.

The sum can overflow even if you only need to know whether:

```text
produced >= K
```

Safe pattern:

```text
stop once produced >= K
```

You often do not need the exact enormous value.

This is another form of state compression.

---

# 18.29 Choosing a High Bound

Sometimes no obvious upper bound is given.

A common strategy is exponential expansion:

```text
hi = 1

while (!possible(hi)):
    hi *= 2
```

Then binary search within the discovered range.

Conceptually:

```text
1
2
4
8
16
32
...
```

until a feasible point is found.

Use overflow-safe logic.

---

# 18.30 Example — Minimum Production Time

Machines:

```text
[2,5,7]
```

Need:

```text
10 items
```

Model:

```text
possible(T):
    floor(T/2)
  + floor(T/5)
  + floor(T/7)
  >= 10
```

Bounds:

```text
lo = 0
```

Simple upper bound:

```text
fastest machine alone:
hi = min(a) * K
   = 2*10
   = 20
```

Why valid?

The fastest machine alone can produce all 10 by time 20.

So:

```text
0 <= answer <= 20
```

---

# 18.31 Example — Minimum Maximum Segment Sum

Positive array:

```text
[7,2,5,10,8]
```

Split into at most 2 contiguous groups.

Minimize largest group sum.

Bounds:

```text
lo = max(a) = 10
hi = sum(a) = 32
```

For candidate:

```text
X
```

greedily make each group as large as possible without exceeding `X`.

Count groups.

Feasible if:

```text
groups <= 2
```

Larger `X` cannot require more groups.

Therefore monotone.

---

# 18.32 Test Candidate X Manually

Before binary search, test tiny candidate answers.

For:

```text
[7,2,5,10,8]
```

Try:

```text
X = 10
```

Groups forced roughly:

```text
[7,2]
[5]
[10]
[8]
```

Too many.

Try:

```text
X = 18
```

Possible:

```text
[7,2,5]
[10,8]
```

2 groups.

Then you can visually see:

```text
10 -> false
18 -> true
```

This helps validate the predicate before implementation.

---

# 18.33 Example — Maximize Minimum Distance

Positions:

```text
[1,2,4,8,9]
```

Choose 3.

Candidate:

```text
X = 3
```

Greedy:

```text
choose 1
next >= 4 -> choose 4
next >= 7 -> choose 8
```

3 chosen:

```text
YES
```

Candidate:

```text
X = 4
```

```text
choose 1
next >= 5 -> choose 8
next >= 12 -> none
```

Only 2:

```text
NO
```

Predicate:

```text
T T T F F...
```

Answer:

```text
last true = 3
```

---

# 18.34 Monotonic Quantity vs Monotonic Predicate

Sometimes an underlying quantity is monotonic.

Example:

```text
requiredGroups(X)
```

as capacity `X` increases:

```text
requiredGroups(X)
```

never increases.

Then predicate:

```text
requiredGroups(X) <= K
```

becomes:

```text
F ... F T ... T
```

A useful approach is:

```text
first identify a monotonic numeric function
then convert it to a boolean predicate
```

---

# 18.35 Step Functions Are Fine

Feasibility does not need to change at every X.

Example:

```text
required operations:
10 10 8 8 8 5 5 4 ...
```

It may stay constant for many values.

Binary search only requires monotonicity:

```text
never reverses direction
```

not strict increase/decrease.

---

# 18.36 Binary Search Is Not Always Needed

Suppose after modeling you derive a direct formula:

```text
answer = ceil(total/k)
```

Use the formula.

Do not binary search merely because a monotone predicate exists.

Preference:

```text
direct formula
    ↓ if unavailable
greedy/direct optimization
    ↓ if difficult
monotone feasibility + answer search
```

Use the simplest correct model.

---

# 18.37 Non-Monotone Trap

Suppose property is:

```text
possible(X) = "X is divisible by 3"
```

Then:

```text
F F T F F T F F T ...
```

No boundary.

Binary search cannot find "the first divisible value" using this predicate alone.

Likewise, exact parity conditions often produce alternating truth values.

Always write several hypothetical predicate values:

```text
X = 1,2,3,4,5,...
```

If the pattern flips repeatedly, it is not answer-space binary search.

---

# 18.38 Equality Trap

Predicate:

```text
sum produced at time T == K
```

may not be monotone.

Production could jump:

```text
8,9,11,13...
```

and never equal 10.

Instead optimization usually uses:

```text
produced(T) >= K
```

which is monotone.

This is an important modeling choice:

```text
use threshold inequalities
rather than exact equality
```

when searching boundaries.

---

# 18.39 Constraints Can Reveal Answer Search

Clues:

```text
n <= 2*10^5
values <= 10^9
answer may be huge
```

and a candidate `X` can be checked in:

```text
O(n)
```

Then:

```text
O(n log 10^9)
```

may be intended.

But constraints are only a clue.

You still need to derive monotonicity.

---

# 18.40 Full Modeling Checklist

When you see:

```text
minimum possible...
maximum possible...
smallest X such that...
largest X such that...
```

run:

```text
1. WHAT EXACTLY IS THE ANSWER?

2. CALL IT X.

3. IF X IS GIVEN,
   CAN I CHECK FEASIBILITY EASILY?

4. WRITE possible(X) IN ONE SENTENCE.

5. TRY SMALL X VALUES MANUALLY.

6. DOES TRUTH LOOK LIKE:
   FFFF TTTT
   or
   TTTT FFFF?

7. PROVE THE DIRECTION.

8. WHAT IS A NECESSARY LOWER BOUND?

9. WHAT SIMPLE CONSTRUCTION
   GIVES AN UPPER BOUND?

10. DO I WANT:
    FIRST TRUE
    or
    LAST TRUE?

11. WHAT IS THE COST OF possible(X)?

12. IS THERE A DIRECT FORMULA
    THAT MAKES SEARCH UNNECESSARY?

13. CHECK OVERFLOW.
```

---

# 18.41 Common Mistakes

## Mistake 1 — "Minimum/maximum means binary search"

No.

You need a monotone feasibility predicate.

---

## Mistake 2 — Not defining possible(X)

Before coding, finish:

```text
possible(X) means __________.
```

in plain English.

---

## Mistake 3 — Wrong inequality

For minimum production time, use:

```text
produced >= target
```

not necessarily:

```text
produced == target
```

---

## Mistake 4 — Weak/random bounds

Derive bounds from the problem whenever possible.

---

## Mistake 5 — Mixing first-true and last-true code

Know your predicate shape first.

---

## Mistake 6 — Assuming feasibility check is correct

The inner greedy/simulation needs its own proof.

---

## Mistake 7 — Overflow

`mid`, sums, products, and `hi` may require `long long` or early stopping.

---

# 18.42 Translation Drills

Do not code.

### Drill 1

> Minimum capacity.

Write:

```text
possible(X):
Can capacity X complete the task?
```

Expected pattern:

```text
F F F T T T
```

Find:

```text
first true
```

### Drill 2

> Maximum minimum distance.

Write:

```text
possible(X):
Can I maintain distance at least X?
```

Expected:

```text
T T T F F
```

Find:

```text
last true
```

### Drill 3

> Minimum time to make K products.

```text
possible(T):
sum floor(T/a[i]) >= K
```

### Drill 4

> Minimum maximum group sum.

```text
possible(X):
Can I partition using at most K groups,
each group sum <= X?
```

### Drill 5

> Largest threshold everyone can satisfy.

```text
possible(X):
Can every required object satisfy threshold X?
```

Then determine whether larger `X` is easier or harder.

---

# 18.43 Practice Set

For every problem, fill:

```text
ANSWER VARIABLE:
OPTIMIZE: MIN / MAX

possible(X):

SMALL X:
LARGE X:

MONOTONE DIRECTION:

SEARCH FOR:
FIRST TRUE / LAST TRUE

LOWER BOUND:
WHY?

UPPER BOUND:
WHY?

CHECK COMPLEXITY:
```

### Practice A

Find minimum eating/processing speed to finish within H hours.

### Practice B

Find minimum time for machines to produce K objects.

### Practice C

Split a positive array into at most K segments minimizing maximum segment sum.

### Practice D

Choose K positions maximizing minimum distance.

### Practice E

Find smallest capacity that allows sequential items to be shipped in at most D days.

---

# 18.44 Practice Answers

## A

```text
possible(X):
sum ceil(a[i]/X) <= H

pattern:
F -> T

find:
first true
```

## B

```text
possible(T):
sum floor(T/a[i]) >= K

pattern:
F -> T

find:
first true
```

## C

```text
possible(X):
greedily partition with each segment sum <= X
and count segments <= K

bounds:
max(a) ... sum(a)

find:
first true
```

## D

```text
possible(X):
greedily choose positions with gaps >= X
and choose at least K

pattern:
T -> F

find:
last true
```

## E

```text
possible(X):
simulate sequential loading with capacity X
requiredDays <= D

pattern:
F -> T

find:
first true
```

---

# 18.45 Chapter Mastery Test

You are ready for the next chapter when a problem says:

```text
minimum possible X
```

and your first reaction is not:

```text
"binary search!"
```

but:

```text
"If someone gives me X,
what exactly can I check?"
```

Then:

```text
Does increasing X only make
that condition easier/harder?
```

You should naturally produce:

```text
optimization
     ↓
candidate X
     ↓
possible(X)
     ↓
monotonicity proof
     ↓
bounds
     ↓
first true / last true
```

---

# 18.46 Final Mental Engine

```text
                OPTIMIZATION STORY
                       │
                       ▼
                NAME ANSWER X
                       │
                       ▼
             TURN INTO YES/NO CHECK
                       │
                       ▼
                  possible(X)
                       │
                       ▼
             TEST SMALL X VALUES
                       │
                       ▼
               IS IT MONOTONE?
                 /           \
               NO             YES
               │               │
        find another model     ▼
                        determine shape
                        /             \
                 F...F T...T       T...T F...F
                      │                 │
                      ▼                 ▼
                  first true         last true
                      │                 │
                      └────────┬────────┘
                               ▼
                         derive bounds
                               │
                               ▼
                         search boundary
```

The core habit:

```text
Do not search the answer first.

Turn the answer into a condition.

Then search for the boundary
where that condition changes.
```

---

# Next Chapter

```text
18. BOUNDS, MONOTONICITY & ANSWER-SPACE MODELING
                    ↓
19. FINAL INTEGRATION — THE COMPLETE CP MATH-MODELING ENGINE
```

Chapter 19 will combine the entire mini-course into one reusable problem-solving system:

```text
story
→ variables
→ constraints
→ equation/inequality
→ operation delta
→ invariant
→ contribution
→ prefix/balance
→ coordinate model
→ extremal bound
→ compressed state
→ monotonicity
→ algorithm
```

It will focus on deciding **which modeling tool to try and in what order** when you first open an unfamiliar Codeforces problem.
