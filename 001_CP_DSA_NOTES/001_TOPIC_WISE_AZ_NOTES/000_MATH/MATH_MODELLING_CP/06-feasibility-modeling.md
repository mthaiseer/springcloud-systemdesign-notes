# CP Mathematical Modeling Mini-Course

## 6. Feasibility Modeling

> **Goal:** Learn to turn questions like **maximum/minimum** into a simpler yes/no question:
>
> **"Can this candidate answer `k` work?"**
>
> This is the mathematical foundation behind many greedy checks and **binary search on answer** problems.

---

# Chapter Tree

```text
6. FEASIBILITY MODELING
│
├── 6.1 What is feasibility?
├── 6.2 Candidate answer k
├── 6.3 Build can(k)
├── 6.4 Optimization -> decision problem
├── 6.5 Valid range of answers
├── 6.6 Monotonicity
├── 6.7 TRUE...FALSE pattern
├── 6.8 FALSE...TRUE pattern
├── 6.9 Maximum feasible answer
├── 6.10 Minimum feasible answer
├── 6.11 Derive a direct answer when possible
├── 6.12 When binary search becomes useful
├── 6.13 Designing a correct check
├── 6.14 Finding search bounds
└── 6.15 Complete CF-style examples
```

Central engine:

```text
OPTIMIZATION
    ↓
suppose answer = k
    ↓
CAN k WORK?
    ↓
build can(k)
    ↓
study TRUE/FALSE pattern
    ↓
monotonic?
    ↓
direct formula OR binary search
```

---

# 6.1 What Is Feasibility?

Feasibility means:

```text
Is this candidate valid?
```

Example:

> You have 20 candies.
> Each child needs 3 candies.

Candidate:

```text
k = 5 children
```

Required:

```text
3 * 5 = 15
```

Available:

```text
20
```

Therefore:

```text
15 <= 20
```

So:

```text
k = 5 is feasible
```

Candidate:

```text
k = 7
```

requires:

```text
21 candies
```

But:

```text
21 > 20
```

So:

```text
k = 7 is infeasible
```

---

# 6.2 Candidate Answer k

For optimization problems, stop asking:

```text
What is the maximum immediately?
```

Instead ask:

```text
If the answer were k,
could I satisfy all conditions?
```

Example:

```text
available resource = R
cost per item = c
```

For `k` items:

```text
required = c*k
```

Feasibility:

```text
c*k <= R
```

So define mentally:

```text
can(k) = (c*k <= R)
```

---

# 6.3 Build can(k)

`can(k)` is not special C++ syntax.

It is a mathematical idea:

```text
can(k) = true
```

if candidate `k` satisfies all requirements.

Otherwise:

```text
can(k) = false
```

Example with two resources:

```text
each product needs:
2 red
3 blue

available:
R red
B blue
```

For `k` products:

```text
2*k <= R
3*k <= B
```

Therefore:

```text
can(k) =
    2*k <= R
    AND
    3*k <= B
```

ASCII:

```text
candidate k
    │
    ├── needs 2k red  <= R ?
    │
    └── needs 3k blue <= B ?
              │
              ▼
        both conditions?
          /        \
        YES        NO
         │          │
       TRUE       FALSE
```

---

# 6.4 Optimization -> Decision Problem

Optimization:

```text
Find maximum k.
```

Decision version:

```text
Can k work?
```

This transformation is extremely important.

Example:

```text
Find maximum products.
```

becomes:

```text
Can we make 10 products?
Can we make 20?
Can we make 15?
...
```

Instead of directly constructing the maximum, we create a yes/no test.

```text
MAX/MIN problem
      ↓
YES/NO problem for candidate k
```

This is called a **decision version** of the optimization problem.

---

# 6.5 Valid Range of Answers

Suppose:

```text
can(k) iff 3*k <= 20
```

Check:

```text
k = 0 -> TRUE
k = 1 -> TRUE
k = 2 -> TRUE
k = 3 -> TRUE
k = 4 -> TRUE
k = 5 -> TRUE
k = 6 -> TRUE
k = 7 -> FALSE
k = 8 -> FALSE
...
```

Pattern:

```text
T T T T T T T F F F F ...
              ^
        boundary
```

The maximum feasible answer is:

```text
6
```

The interesting part is not the exact values.

It is the **shape** of the answers.

---

# 6.6 Monotonicity

A predicate `can(k)` is monotonic when its truth value changes in only one direction.

Typical maximum problem:

```text
TRUE TRUE TRUE TRUE FALSE FALSE FALSE
```

Once it becomes false:

```text
it stays false
```

Why?

If you cannot support `k` products, you usually cannot support even more products.

Example:

```text
cost per product = 3
resource = 20
```

If:

```text
7 products are impossible
```

then:

```text
8, 9, 10, ...
```

are also impossible.

This is monotonicity.

---

# 6.7 TRUE -> FALSE Pattern

For maximum feasible answer problems:

```text
k:

0 1 2 3 4 5 6 7 8 9 ...

can(k):

T T T T T T T F F F ...
            ^
         answer
```

We want:

```text
last TRUE
```

This is the classic:

```text
maximum feasible k
```

pattern.

Mental trigger:

```text
If k works,
do all smaller values work?
```

If yes, you may have:

```text
TRUE...TRUE FALSE...FALSE
```

---

# 6.8 FALSE -> TRUE Pattern

Minimum problems often have the opposite pattern.

Example:

> `n = 20` people.
> Each bus holds 6.
> Is `k` buses enough?

Feasibility:

```text
6*k >= 20
```

Check:

```text
k = 0 -> FALSE
k = 1 -> FALSE
k = 2 -> FALSE
k = 3 -> FALSE
k = 4 -> TRUE
k = 5 -> TRUE
...
```

Pattern:

```text
F F F F T T T T T ...
        ^
      answer
```

We want:

```text
first TRUE
```

This is the classic:

```text
minimum sufficient k
```

pattern.

Mental trigger:

```text
If k is enough,
will every larger value also be enough?
```

If yes:

```text
FALSE...FALSE TRUE...TRUE
```

---

# 6.9 Maximum Feasible Answer

General maximum pattern:

```text
can(k):

TRUE  for small k
FALSE for large k
```

We want:

```text
largest k such that can(k) = true
```

Example:

```text
2*k <= A
3*k <= B
```

Then:

```text
can(k) =
2*k <= A AND 3*k <= B
```

Because larger `k` consumes more resources:

```text
can(k) is monotonic
```

The boundary is the maximum number of products.

---

# 6.10 Minimum Feasible Answer

General minimum pattern:

```text
can(k):

FALSE for small k
TRUE  for large k
```

We want:

```text
smallest k such that can(k) = true
```

Example:

```text
capacity per server = c
required load = n
```

Check:

```text
can(k) = (k*c >= n)
```

More servers never reduce capacity.

So:

```text
F F F F T T T T ...
```

We want the first true.

---

# 6.11 Direct Formula Before Binary Search

Very important:

> Monotonicity does NOT automatically mean you should binary search.

Example:

```text
can(k) = 3*k <= 20
```

We can solve algebraically:

```text
k <= 20/3
```

Therefore:

```text
answer = 6
```

Binary search would work, but it is unnecessary.

Preferred thinking:

```text
can(k)
   ↓
Can I solve the inequality directly?
   │
 YES ──> formula
   │
  NO
   ↓
Can I exploit monotonicity?
   ↓
binary search may help
```

Use the simplest correct method.

---

# 6.12 When Binary Search Becomes Useful

Suppose `can(k)` is easy to test, but solving directly for `k` is difficult.

For example:

> `n` machines have different production speeds.
> Machine `i` produces one item every `t[i]` time units.
> Find the minimum time needed to produce at least `K` items.

Let:

```text
T = candidate time
```

In `T` time, machine `i` produces:

```text
floor(T / t[i])
```

Total production:

```text
sum floor(T / t[i])
```

Feasibility:

```text
can(T) =
sum floor(T / t[i]) >= K
```

Can we isolate `T` with simple algebra?

Usually not cleanly because of:

```text
many floor divisions
```

But monotonicity is clear:

```text
if T time is enough,
more time is also enough
```

Pattern:

```text
F F F F F T T T T ...
```

We need:

```text
first TRUE
```

This is a strong binary-search-on-answer signal.

---

# 6.13 Visualizing Binary Search on Answer

Suppose:

```text
candidate answer k
```

has:

```text
0 1 2 3 4 5 6 7 8 9 10
T T T T T T T F F F F
            ^
         last true
```

Instead of checking every `k`, binary search repeatedly asks about the middle.

```text
search interval
0 ---------------- 10
         ^
        mid
```

If:

```text
can(mid) = TRUE
```

and we want maximum feasible:

```text
answer may be mid or larger
```

Search right.

If:

```text
can(mid) = FALSE
```

then:

```text
mid and larger are impossible
```

Search left.

The algorithm works because of the monotonic structure.

---

# 6.14 Maximum Feasible Binary Search Shape

We want:

```text
last TRUE
```

Pattern:

```text
T T T T T T F F F F
          ↑
        answer
```

Conceptual algorithm:

```text
lo = smallest candidate
hi = largest candidate

while search remains:
    mid = middle

    if can(mid):
        record mid
        search larger values
    else:
        search smaller values
```

Do not memorize code before understanding this picture.

---

# 6.15 Minimum Feasible Binary Search Shape

We want:

```text
first TRUE
```

Pattern:

```text
F F F F T T T T T
        ↑
      answer
```

Conceptual algorithm:

```text
lo = smallest candidate
hi = largest candidate

while search remains:
    mid = middle

    if can(mid):
        record mid
        search smaller values
    else:
        search larger values
```

The direction comes directly from the TRUE/FALSE pattern.

---

# 6.16 Designing a Correct can(k)

A feasibility function must answer exactly:

```text
Is candidate k possible?
```

It should NOT try to find the final answer itself.

Example:

```text
R red
B blue

each team needs:
2 red
3 blue
```

Correct:

```text
can(k):

return
    2*k <= R
    AND
    3*k <= B
```

Bad conceptual design:

```text
can(k):
    calculate maximum answer
    compare with k
```

That defeats the purpose.

A good check usually:

```text
takes candidate
simulates/calculates requirements
returns true/false
```

---

# 6.17 Feasibility With Multiple Conditions

Suppose candidate `k` must satisfy:

```text
2*k <= A
3*k <= B
k >= C
```

Then:

```text
can(k) =
    (2*k <= A)
    AND
    (3*k <= B)
    AND
    (k >= C)
```

But now inspect the pattern carefully.

The first two prefer smaller `k`.

The last prefers larger `k`.

So feasible values may form an interval:

```text
FALSE FALSE TRUE TRUE TRUE FALSE FALSE
```

Example:

```text
k >= 3
k <= 6
```

gives:

```text
0 1 2 3 4 5 6 7 8
F F F T T T T F F
```

This is NOT the standard one-boundary binary-search predicate over the whole range.

Important lesson:

```text
Do not see can(k) and immediately binary search.

First inspect its monotonicity.
```

---

# 6.18 Feasible Interval

If:

```text
k >= L
k <= R
```

then valid values are:

```text
L <= k <= R
```

A solution exists iff:

```text
L <= R
```

Visual:

```text
---------L================R---------
         feasible region
```

If:

```text
L > R
```

there is no overlap:

```text
------R---------L------
       no valid k
```

This connects feasibility to range intersection.

---

# 6.19 Search Bounds

Binary search needs:

```text
lo
hi
```

You must justify them.

Example:

> Maximum products using `R` resources, at least one resource per product.

Then:

```text
0 <= answer <= R
```

So:

```text
lo = 0
hi = R
```

Example:

> Minimum time for `K` items.
> Slowest safe upper estimate is `K * min_time_per_item` using one fastest machine.

Possible bound:

```text
hi = K * min(t[i])
```

if overflow is handled.

The exact bound depends on the problem.

Mental question:

```text
What is definitely too small?
What is definitely large enough?
```

---

# 6.20 Doubling to Find an Upper Bound

Sometimes a convenient upper bound is not obvious.

Conceptually:

```text
hi = 1

while can(hi) is false:
    hi *= 2
```

For a minimum-feasible problem:

```text
1
2
4
8
16
32
...
```

Eventually find a true point, assuming one exists and values are bounded appropriately.

Then binary search inside the discovered range.

This is useful when the answer magnitude is unknown.

---

# 6.21 Overflow in Feasibility Checks

Suppose:

```text
k <= 10^18
cost <= 10^18
```

Checking:

```cpp
cost * k <= R
```

may overflow `long long`.

Mathematically equivalent when quantities are positive:

```text
k <= R / cost
```

can avoid multiplication overflow.

Similarly, in production sums:

```text
total += T / t[i]
```

if you only care whether:

```text
total >= K
```

you can stop once total reaches `K`.

General habit:

```text
can(k) only needs YES/NO.

Do not calculate huge values
that are irrelevant after the answer is already known.
```

---

# 6.22 Complete CF-Style Example 1 — Maximum Teams

Problem:

> `R` red and `B` blue.
> Each team needs 2 red and 3 blue.
> Find maximum teams.

Define:

```text
can(k):
    2*k <= R
    AND
    3*k <= B
```

Pattern:

```text
T T T T ... F F F
```

because if `k` teams work, fewer teams work.

Could binary search.

But algebra gives:

```text
k <= R/2
k <= B/3
```

Therefore:

```text
answer = min(R/2, B/3)
```

Lesson:

```text
monotonic check exists
but direct formula is simpler
```

---

# 6.23 Complete CF-Style Example 2 — Minimum Buses

Problem:

> `n` people.
> Each bus holds `c`.
> Minimum buses.

Define:

```text
can(k) = (k*c >= n)
```

Pattern:

```text
F F F ... T T T
```

Need first true.

But algebra gives:

```text
k >= ceil(n/c)
```

So:

```text
answer = ceil(n/c)
```

Again no binary search needed.

---

# 6.24 Complete CF-Style Example 3 — Production Time

Problem:

> There are `n` machines.
> Machine `i` takes `t[i]` time for one product.
> Find minimum time to produce at least `K` products.

Candidate:

```text
T = time
```

Machine `i` produces:

```text
T / t[i]
```

using integer division.

Total:

```text
produced(T)
=
sum(T / t[i])
```

Feasibility:

```text
can(T) =
produced(T) >= K
```

As `T` increases:

```text
production never decreases
```

Therefore:

```text
can(T)
=
F F F F T T T T ...
```

Need:

```text
first TRUE
```

This is binary search on answer.

---

# 6.25 Complete CF-Style Example 4 — Minimum Capacity

Problem:

> Items must be processed in order.
> A machine has candidate capacity `C`.
> Determine whether all work can be completed within `D` days.

The exact check depends on the problem rules, but the modeling pattern is:

```text
can(C):
    simulate using capacity C
    count required days
    return required_days <= D
```

Now ask:

```text
If capacity C works,
will a larger capacity also work?
```

Usually:

```text
YES
```

because more capacity cannot require more days.

So:

```text
F F F F T T T T
```

Need minimum feasible capacity.

This shows that `can(k)` can contain an `O(n)` simulation.

Binary search can still reduce the outer answer search.

---

# 6.26 Complexity of Binary Search on Answer

Suppose answer range size is roughly:

```text
R
```

Binary search performs approximately:

```text
O(log R)
```

checks.

If each:

```text
can(k)
```

takes:

```text
O(n)
```

then total:

```text
O(n log R)
```

This is often far better than testing every candidate:

```text
O(n * R)
```

Visual:

```text
candidate range:
1 ... 1,000,000,000

linear search:
up to 1,000,000,000 checks

binary search:
about 30 checks
```

This is why a monotonic feasibility model is so powerful.

---

# 6.27 How to Detect Binary Search on Answer

Look for:

```text
maximum/minimum answer
```

Then ask:

```text
1. Can I guess a candidate k?

2. Can I check whether k works
   without already knowing the answer?

3. Is the check reasonably fast?

4. Is can(k) monotonic?

5. Is there no simpler direct formula?
```

If all are yes:

```text
binary search on answer
```

is a strong candidate.

---

# 6.28 The Monotonicity Test

Before binary searching, explicitly say one of these:

### Maximum problem

```text
If k works,
every k' < k also works.
```

Therefore:

```text
T T T T F F F
```

### Minimum problem

```text
If k works,
every k' > k also works.
```

Therefore:

```text
F F F T T T T
```

If you cannot justify one of these patterns:

```text
do not binary search yet
```

---

# 6.29 Non-Monotonic Example

Suppose:

```text
can(k) = true iff k is even
```

Pattern:

```text
k:
1 2 3 4 5 6 7 8

can:
F T F T F T F T
```

This is not monotonic.

Binary search cannot locate "the boundary" because there is no single boundary.

Another example:

```text
3 <= k <= 7
```

Pattern:

```text
F F T T T T T F F
```

Again, not the standard monotonic form over the whole domain.

---

# 6.30 Feasibility vs Construction

A check may only answer:

```text
YES / NO
```

It does not necessarily construct the solution.

Example:

```text
can(k) = enough resources for k teams?
```

This may be enough if the problem only asks for maximum count.

But if the problem asks:

```text
print the actual teams
```

then after finding the answer you may need a separate construction step.

Keep these concepts separate:

```text
FEASIBILITY:
Does a solution exist?

CONSTRUCTION:
What exactly is the solution?
```

---

# 6.31 Common Mistakes

## Mistake 1 — Binary searching without monotonicity

Always prove:

```text
works -> all smaller work
```

or:

```text
works -> all larger work
```

---

## Mistake 2 — Binary searching a direct formula

If:

```text
3*k <= n
```

just derive:

```text
k <= n/3
```

Do not add unnecessary complexity.

---

## Mistake 3 — Wrong direction

For:

```text
T T T F F F
```

want maximum:

```text
last TRUE
```

For:

```text
F F F T T T
```

want minimum:

```text
first TRUE
```

Draw the pattern if confused.

---

## Mistake 4 — Incorrect can(k)

Your check must preserve every original condition.

If the problem has:

```text
resource limit
ordering rule
number of days
capacity rule
```

all relevant rules must be reflected in the feasibility check.

---

## Mistake 5 — Weak or unsafe bounds

Make sure:

```text
answer lies inside [lo, hi]
```

Do not invent a small upper bound without proof.

---

# 6.32 Translation Drills

Do not code.

---

## Drill 1

> `R` resource.
> Each item costs 5.
> Can we make `k` items?

Model:

```text
can(k) = 5*k <= R
```

Pattern:

```text
T...T F...F
```

---

## Drill 2

> `n` people.
> Each bus holds 4.
> Are `k` buses enough?

Model:

```text
can(k) = 4*k >= n
```

Pattern:

```text
F...F T...T
```

---

## Drill 3

> Machine `i` produces one item every `t[i]`.
> Are `T` time units enough for `K` items?

Model:

```text
produced = sum(T / t[i])

can(T) = produced >= K
```

Pattern:

```text
F...F T...T
```

---

## Drill 4

```text
can(k) = (k % 2 == 0)
```

Monotonic?

```text
NO
```

Pattern alternates.

---

# 6.33 Practice Set

For each problem write:

```text
CANDIDATE:
can(candidate):
TRUE/FALSE PATTERN:
MONOTONIC?:
LOOKING FOR:
DIRECT FORMULA OR SEARCH?:
```

---

## Practice A

> `A` apples.
> Each package uses 3 apples.
> Maximum packages.

---

## Practice B

> `n` files.
> One server handles at most `c` files.
> Minimum servers.

---

## Practice C

> `n` workers have speeds `t[i]`.
> Worker `i` completes one job every `t[i]` minutes.
> Minimum time for `K` jobs.

---

## Practice D

> Candidate `k` is valid only when `k` is divisible by 3.
> Is this predicate suitable for standard binary search on answer?

---

## Practice E

> Candidate `k` must satisfy:

```text
k >= L
k <= R
```

Describe the feasible region.

---

# 6.34 Practice Answers

## A

```text
candidate = k packages

can(k):
3*k <= A

pattern:
T...T F...F

looking for:
last TRUE

direct formula:
A/3
```

---

## B

```text
candidate = k servers

can(k):
c*k >= n

pattern:
F...F T...T

looking for:
first TRUE

direct formula:
ceil(n/c)
```

---

## C

```text
candidate = T minutes

can(T):
sum(T / t[i]) >= K

pattern:
F...F T...T

looking for:
first TRUE

direct simple formula:
generally no

binary search:
yes
```

---

## D

```text
F F T? 
```

More precisely the truth value repeats according to divisibility:

```text
k:
0 1 2 3 4 5 6 ...

can:
T F F T F F T ...
```

Not monotonic.

Therefore:

```text
standard binary search on answer is not applicable
```

---

## E

Feasible:

```text
L <= k <= R
```

If:

```text
L <= R
```

there is a feasible interval.

If:

```text
L > R
```

no feasible candidate exists.

---

# 6.35 Chapter Mastery Test

You are ready for the next chapter when you see:

```text
"Find maximum/minimum..."
```

and naturally ask:

```text
Can I define:

can(k) = "is k possible?"
```

Then you should ask:

```text
If k works, what about smaller k?
If k works, what about larger k?
```

You should recognize:

```text
T T T T F F F
        ↑
last TRUE
```

as maximum feasible.

And:

```text
F F F T T T T
      ↑
first TRUE
```

as minimum feasible.

Most importantly:

```text
MONOTONIC
does not automatically mean
BINARY SEARCH.

First ask whether algebra
gives the answer directly.
```

---

# 6.36 Final Mental Engine

```text
              MAX / MIN PROBLEM
                     │
                     ▼
            DEFINE CANDIDATE k
                     │
                     ▼
                BUILD can(k)
                     │
                     ▼
             IS IT MONOTONIC?
                  /      \
                NO        YES
                │          │
         need another      ▼
            idea       DRAW PATTERN
                          │
                 ┌────────┴────────┐
                 │                 │
              T...F             F...T
                 │                 │
              last T            first T
                 │                 │
                 └────────┬────────┘
                          ▼
                DIRECT FORMULA?
                    /          \
                  YES          NO
                   │            │
                algebra      binary search
```

The core habit is:

```text
Optimization is often easier
when converted into:

"Can this answer work?"
```

---

# Next Chapter

```text
6. FEASIBILITY MODELING
            ↓
7. COUNTING & CONTRIBUTION MODELING
```

Chapter 7 will focus on translating counting problems into:

```text
What does each element contribute?
How many times is something counted?
count pairs/triples without generating them
frequency modeling
contribution technique
double counting intuition
prefix/suffix contribution
```

This is mathematical modeling for counting problems, separate from the combinatorics formulas covered in your TLE/AZ material.
