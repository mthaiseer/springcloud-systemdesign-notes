# CP Mathematical Modeling Mini-Course

## 5. Resource & Optimization Modeling

> **Goal:** Learn to model Codeforces problems that ask for a **maximum**, **minimum**, or the largest number of actions/items possible under limited resources.
>
> The main technique is:
>
> **Suppose the answer is `k`. What must be true for `k` to be possible?**

---

# Chapter Tree

```text
5. RESOURCE & OPTIMIZATION MODELING
│
├── 5.1 Recognize a resource problem
├── 5.2 Define k = answer
├── 5.3 Model resource consumption
├── 5.4 Convert resources into inequalities
├── 5.5 Derive upper bounds
├── 5.6 Derive lower bounds
├── 5.7 Limiting resource -> min(...)
├── 5.8 Multiple requirements -> max(...)
├── 5.9 Leftovers and remaining resources
├── 5.10 Shared resources
├── 5.11 Maximum vs minimum modeling
├── 5.12 Optimization through bounds
├── 5.13 Tight bounds
├── 5.14 When a direct formula is not enough
└── 5.15 Complete CF-style modeling
```

Central engine:

```text
Problem asks MAX/MIN
        ↓
Let k = answer
        ↓
If answer were k,
what would be required?
        ↓
RESOURCE REQUIREMENTS
        ↓
INEQUALITIES
        ↓
BOUNDS ON k
        ↓
combine bounds
        ↓
MAX / MIN / feasibility
```

---

# 5.1 Recognize a Resource Problem

Common story words:

```text
coins
food
energy
workers
machines
red/blue balls
time
capacity
fuel
materials
operations
days
items
```

Common questions:

```text
maximum number of teams?
maximum operations?
minimum containers?
minimum time?
how many products can be made?
```

Strip the story.

Example:

> A factory has `A` metal and `B` plastic.
> Each product requires 3 metal and 2 plastic.
> Find the maximum products.

Reduced:

```text
resources:
A metal
B plastic

cost per product:
3 metal
2 plastic

maximize:
number of products
```

This is a resource optimization problem.

---

# 5.2 Define k = Answer

This is the most useful first move.

Instead of immediately trying to find the maximum, say:

```text
k = number of products
```

Then ask:

> If I want to make exactly `k` products, what resources are required?

This converts:

```text
"find maximum"
```

into:

```text
"what conditions must k satisfy?"
```

Visual:

```text
MAXIMUM?
   │
   ▼
pretend answer = k
   │
   ▼
what does k require?
```

---

# 5.3 Model Resource Consumption

If one product requires:

```text
3 metal
```

then `k` products require:

```text
3 * k metal
```

If one product requires:

```text
2 plastic
```

then `k` products require:

```text
2 * k plastic
```

General rule:

```text
resource per item * number of items
=
total resource required
```

So:

```text
cost_per_item * k
```

is one of the most common resource formulas in CP.

---

# 5.4 Convert Resources Into Inequalities

Suppose available metal is:

```text
A
```

Required for `k` products:

```text
3*k
```

You cannot consume more than available:

```text
3*k <= A
```

Similarly:

```text
2*k <= B
```

So the story becomes:

```text
3*k <= A
2*k <= B
```

Visual:

```text
             k products
                 │
       ┌─────────┴─────────┐
       │                   │
   need 3*k metal      need 2*k plastic
       │                   │
   available A          available B
       │                   │
       ▼                   ▼
   3*k <= A            2*k <= B
```

---

# 5.5 Derive Upper Bounds

From:

```text
3*k <= A
```

we get:

```text
k <= floor(A / 3)
```

From:

```text
2*k <= B
```

we get:

```text
k <= floor(B / 2)
```

So there are two upper bounds:

```text
k <= A/3
k <= B/2
```

using integer division for non-negative integer resources.

Meaning:

```text
metal allows at most A/3 products
plastic allows at most B/2 products
```

---

# 5.6 Limiting Resource -> min(...)

Suppose:

```text
metal allows 8 products
plastic allows 5 products
```

Can you make 8?

No, because plastic only supports 5.

Can you make 5?

Yes, assuming these are the only requirements.

Therefore:

```text
answer = min(8,5)
       = 5
```

General:

```text
answer = min(A/3, B/2)
```

Visual:

```text
Metal limit   = 8 ────────┐
                          ├── answer = 5
Plastic limit = 5 ────────┘
```

Core pattern:

```text
many upper bounds
       ↓
take minimum
```

Why?

Because the smallest upper bound blocks everything above it.

---

# 5.7 Three or More Resources

Suppose one machine requires:

```text
2 units A
3 units B
5 units C
```

Available:

```text
A, B, C
```

For `k` machines:

```text
2*k <= A
3*k <= B
5*k <= C
```

Therefore:

```text
k <= A/2
k <= B/3
k <= C/5
```

Maximum:

```text
answer = min(A/2, B/3, C/5)
```

The number of resources does not change the idea.

```text
resource 1 -> upper bound
resource 2 -> upper bound
resource 3 -> upper bound
...
              ↓
minimum of all upper bounds
```

---

# 5.8 Lower Bounds

Optimization problems can also produce lower bounds.

Example:

> Each bus carries at most `c` people.
> There are `n` people.
> Find the minimum buses.

Let:

```text
k = number of buses
```

Capacity of `k` buses:

```text
k*c
```

To carry everyone:

```text
k*c >= n
```

Therefore:

```text
k >= ceil(n / c)
```

Since we want the minimum `k`:

```text
answer = ceil(n / c)
```

For positive integers:

```cpp
int answer = (n + c - 1) / c;
```

Compare:

```text
MAXIMUM under limited resource:
cost*k <= available
       ↓
upper bound on k
```

```text
MINIMUM needed for enough capacity:
capacity*k >= required
       ↓
lower bound on k
```

---

# 5.9 Multiple Lower Bounds -> max(...)

Suppose `k` must satisfy:

```text
k >= 4
k >= 7
k >= 3
```

To satisfy all:

```text
k >= 7
```

Therefore the smallest valid `k` is:

```text
max(4,7,3)
```

Core pattern:

```text
many lower bounds
       ↓
take maximum
```

Compare the two patterns:

```text
MAXIMIZE k
with many upper bounds
        ↓
      min(...)
```

```text
MINIMIZE k
with many lower bounds
        ↓
      max(...)
```

This is a very important modeling pattern.

---

# 5.10 Leftovers

Sometimes the problem asks about what remains after making `k` items.

General:

```text
remaining
=
available - consumed
```

Example:

```text
A metal available
3 metal per product
k products
```

Remaining metal:

```text
A - 3*k
```

Validity:

```text
A - 3*k >= 0
```

which is equivalent to:

```text
3*k <= A
```

This gives two equivalent ways to model a resource:

```text
consumed <= available
```

or:

```text
remaining >= 0
```

Use whichever feels clearer.

---

# 5.11 Shared Resources

This is where resource modeling becomes more interesting.

Suppose:

> Type A product needs 2 units.
> Type B product needs 3 units.
> Both use the same resource pool of `R`.

Let:

```text
x = number of type A
y = number of type B
```

Total consumption:

```text
2*x + 3*y
```

Resource condition:

```text
2*x + 3*y <= R
```

Visual:

```text
              shared R
                 │
        ┌────────┴────────┐
        │                 │
     x type A          y type B
      cost 2            cost 3
        │                 │
        └────────┬────────┘
                 ▼
            2*x + 3*y
                 │
                 ▼
          2*x + 3*y <= R
```

Do not write separate independent limits if resources come from the SAME pool.

---

# 5.12 Independent vs Shared Resources

This distinction is crucial.

### Independent resources

```text
A red balls
B blue balls

each product needs:
2 red
3 blue
```

Conditions:

```text
2*k <= A
3*k <= B
```

Separate limits.

---

### Shared resource

```text
R total material

type X uses 2
type Y uses 3
```

Condition:

```text
2*x + 3*y <= R
```

One combined inequality.

Mental question:

> Are these costs taken from separate pools or the same pool?

---

# 5.13 Maximum Modeling

When the statement says:

> Find maximum `k`.

Use:

```text
Step 1:
Let k = answer.

Step 2:
Assume we want k.

Step 3:
Write everything k requires.

Step 4:
Convert requirements into:
k <= something

Step 5:
Combine upper bounds.

Step 6:
Take the tightest/smallest upper bound.
```

Visual:

```text
MAX k
 │
 ▼
requirements for k
 │
 ▼
k <= U1
k <= U2
k <= U3
 │
 ▼
k <= min(U1,U2,U3)
```

---

# 5.14 Minimum Modeling

When the statement says:

> Find minimum `k`.

Try:

```text
Step 1:
Let k = answer.

Step 2:
Assume we use k.

Step 3:
What must k be large enough to accomplish?

Step 4:
derive:
k >= something

Step 5:
combine lower bounds

Step 6:
take largest lower bound
```

Visual:

```text
MIN k
 │
 ▼
requirements
 │
 ▼
k >= L1
k >= L2
k >= L3
 │
 ▼
k >= max(L1,L2,L3)
```

---

# 5.15 Tight Bounds

A bound is useful when it is close to the true answer.

Example:

```text
k <= 100
k <= 8
k <= 50
```

All are correct upper bounds.

But:

```text
k <= 8
```

is the tightest.

So:

```text
min(100,8,50) = 8
```

The tightest upper bound determines the maximum.

Similarly:

```text
k >= 2
k >= 10
k >= 4
```

Tightest lower bound:

```text
k >= 10
```

This explains why:

```text
upper bounds -> min
lower bounds -> max
```

---

# 5.16 Exact Requirement vs Capacity

Be careful with wording.

### "At most"

> Each box can contain at most 5 objects.

For `k` boxes:

```text
total capacity = 5*k
```

To hold `n`:

```text
5*k >= n
```

---

### "Exactly"

> Each complete package must contain exactly 5 objects.

Maximum complete packages:

```text
5*k <= n
```

So:

```text
k <= floor(n/5)
```

Same numbers, different question, different inequality.

---

# 5.17 Optimization by Defining the Answer

This is one of the most transferable habits in this course.

Suppose a problem asks:

> What is the maximum number of days?

Do not immediately search for a formula.

Write:

```text
d = number of days
```

Then ask:

```text
If survival for d days is possible,
what resources are needed?
```

Example:

```text
food needed per day = 3
water needed per day = 2

available:
F food
W water
```

For `d` days:

```text
3*d <= F
2*d <= W
```

Therefore:

```text
d <= F/3
d <= W/2
```

Maximum:

```text
answer = min(F/3, W/2)
```

The phrase:

```text
"Suppose the answer is k"
```

turns optimization into mathematics.

---

# 5.18 When Direct min(...) Is NOT Enough

Consider:

> You have `R` units of one shared resource.
>
> Product A uses 2.
> Product B uses 3.
>
> Maximize total products `x+y`.

Condition:

```text
2*x + 3*y <= R
```

You cannot simply write:

```text
min(R/2, R/3)
```

because `x` and `y` are different choices sharing the same resource.

The optimization is:

```text
maximize x+y

subject to:

2*x + 3*y <= R
x,y >= 0
```

Now you need more reasoning.

Important lesson:

```text
min(...) works naturally when
the SAME answer k
is independently bounded
by multiple resource pools.
```

Do not mechanically use `min`.

---

# 5.19 Bottleneck Resource

The resource giving the smallest upper bound is the bottleneck.

Example:

```text
food supports 10 days
water supports 7 days
medicine supports 20 days
```

Then:

```text
answer = 7 days
```

Water is the bottleneck.

Visual:

```text
food      10 ────────────┐
water      7 ───────┐    │
medicine  20 ───────┼────┤
                    ▼
               bottleneck = 7
```

This concept appears in:

```text
production
teams
packing
scheduling
capacity
resource allocation
```

---

# 5.20 Complete CF-Style Example 1 — Teams

Problem:

> There are `R` red shirts and `B` blue shirts.
> Every team needs 2 red shirts and 1 blue shirt.
> Find the maximum teams.

Define:

```text
k = teams
```

Requirements:

```text
2*k <= R
k <= B
```

Bounds:

```text
k <= R/2
k <= B
```

Therefore:

```text
answer = min(R/2, B)
```

for non-negative integer quantities.

---

# 5.21 Complete CF-Style Example 2 — Minimum Buses

Problem:

> `n` people must travel.
> Each bus carries at most `c`.
> Find minimum buses.

Define:

```text
k = buses
```

Capacity:

```text
k*c
```

Need:

```text
k*c >= n
```

Therefore:

```text
k >= ceil(n/c)
```

Minimum:

```text
answer = ceil(n/c)
```

For positive integers:

```cpp
int answer = (n + c - 1) / c;
```

---

# 5.22 Complete CF-Style Example 3 — Operations

Problem:

> Start with `A` energy.
> Each operation costs 4 energy.
> Find maximum legal operations.

Define:

```text
k = operations
```

Required:

```text
4*k
```

Condition:

```text
4*k <= A
```

Therefore:

```text
answer = A/4
```

for non-negative integer `A`.

Equivalent remaining-resource model:

```text
A - 4*k >= 0
```

---

# 5.23 Complete CF-Style Example 4 — Two Requirements

Problem:

> A server job needs:
>
> - 3 CPU units
> - 5 memory units
>
> Available:
>
> - `C` CPU
> - `M` memory
>
> Find maximum simultaneous jobs.

Let:

```text
k = jobs
```

CPU:

```text
3*k <= C
```

Memory:

```text
5*k <= M
```

So:

```text
k <= C/3
k <= M/5
```

Answer:

```text
min(C/3, M/5)
```

This is exactly the same mathematics as balls, food, or factory products.

Story changes.

Model does not.

---

# 5.24 Complete CF-Style Example 5 — Lower Bounds From Multiple Requirements

Suppose a system needs enough machines to satisfy two independent capacity requirements.

Each machine provides:

```text
4 units of service A
3 units of service B
```

Required:

```text
at least A units of service A
at least B units of service B
```

For `k` machines:

```text
4*k >= A
3*k >= B
```

Therefore:

```text
k >= ceil(A/4)
k >= ceil(B/3)
```

To satisfy both:

```text
answer = max(
    ceil(A/4),
    ceil(B/3)
)
```

This is the minimum-version counterpart of the bottleneck pattern.

---

# 5.25 Optimization + Leftover Condition

Suppose:

> Make maximum packages.
> Each package uses 3 items.
> At least 2 items must remain unused.

Available:

```text
n
```

For `k` packages:

```text
used = 3*k
remaining = n - 3*k
```

Requirement:

```text
remaining >= 2
```

Therefore:

```text
n - 3*k >= 2
```

Rearrange:

```text
3*k <= n - 2
```

So:

```text
k <= floor((n-2)/3)
```

The extra condition changes the bound.

Always translate ALL requirements before optimizing.

---

# 5.26 Optimization With a Range

Sometimes `k` gets both lower and upper bounds.

Example:

```text
k >= L
k <= R
```

A valid `k` exists iff:

```text
L <= R
```

Visual:

```text
number line

---------L================R---------

         valid region
```

If:

```text
L > R
```

then:

```text
no possible k
```

This idea becomes central in the next chapter on feasibility.

---

# 5.27 Resource Modeling Checklist

When you see a maximum/minimum resource problem:

```text
1. WHAT IS THE ANSWER?
   define k

2. WHAT DOES ONE UNIT OF k REQUIRE?

3. WHAT DOES k UNITS REQUIRE?
   multiply by k

4. WHAT IS AVAILABLE / REQUIRED?

5. WRITE INEQUALITIES

6. SOLVE FOR k

7. ARE THESE:
   upper bounds?
   lower bounds?

8. COMBINE:
   upper bounds -> min
   lower bounds -> max

9. CHECK:
   integer rounding?
   leftovers?
   shared resource?
   extra conditions?
```

---

# 5.28 Common Mistakes

## Mistake 1 — Using min mechanically

Correct situation:

```text
2*k <= A
3*k <= B

answer = min(A/2, B/3)
```

But not every optimization problem has this form.

If variables differ:

```text
2*x + 3*y <= R
```

you need more reasoning.

---

## Mistake 2 — Wrong inequality direction

Maximum complete products:

```text
cost*k <= available
```

Minimum capacity needed:

```text
capacity*k >= required
```

Do not confuse them.

---

## Mistake 3 — Forgetting integer rounding

From:

```text
3*k <= 10
```

maximum integer `k`:

```text
3
```

not:

```text
3.333...
```

This is floor.

From:

```text
3*k >= 10
```

minimum integer `k`:

```text
4
```

This is ceil.

---

## Mistake 4 — Ignoring leftovers

If the problem says:

```text
at least x must remain
```

include it:

```text
remaining >= x
```

---

## Mistake 5 — Treating shared resources as independent

If A and B both consume from the same pool:

```text
costA*x + costB*y <= total
```

not two unrelated inequalities against the full total.

---

# 5.29 Translation Drills

Do not code.

---

## Drill 1

> `n` candies.
> 4 candies per child.
> Maximum fully served children.

Model:

```text
4*k <= n

answer = n/4
```

---

## Drill 2

> `n` people.
> Capacity 6 per car.
> Minimum cars.

Model:

```text
6*k >= n

answer = ceil(n/6)
```

---

## Drill 3

> Each product requires 2 A and 5 B.
> Available `A`, `B`.
> Maximum products.

Model:

```text
2*k <= A
5*k <= B

answer = min(A/2, B/5)
```

---

## Drill 4

> Each machine supplies 3 units X and 4 units Y.
> Need at least `X` and `Y`.
> Minimum machines.

Model:

```text
3*k >= X
4*k >= Y

answer = max(
    ceil(X/3),
    ceil(Y/4)
)
```

---

# 5.30 Practice Set

For each problem write:

```text
ANSWER VARIABLE:
REQUIREMENTS FOR k:
INEQUALITIES:
BOUNDS:
LIMITING CONDITION:
FINAL MODEL:
```

---

## Practice A

> A restaurant has `R` rice portions and `C` chicken portions.
> Each meal uses 2 rice portions and 1 chicken portion.
> Find maximum complete meals.

---

## Practice B

> `n` files must be stored.
> Each disk stores at most `c` files.
> Find minimum disks.

---

## Practice C

> Each operation consumes 3 energy and 2 tokens.
> Available energy `E`, tokens `T`.
> Find maximum operations.

---

## Practice D

> Each worker can process at most 5 jobs.
> There are `n` jobs.
> Find minimum workers.

---

## Practice E

> There are `R` units of one material.
> Type A costs 2 and Type B costs 5.
> Write the resource model if we produce `x` A and `y` B.

---

# 5.31 Practice Answers

## A

```text
k = meals

2*k <= R
k <= C

answer = min(R/2, C)
```

---

## B

```text
k = disks

c*k >= n

answer = ceil(n/c)
```

---

## C

```text
k = operations

3*k <= E
2*k <= T

answer = min(E/3, T/2)
```

---

## D

```text
k = workers

5*k >= n

answer = ceil(n/5)
```

---

## E

```text
2*x + 5*y <= R
```

This is a shared-resource model.

---

# 5.32 Chapter Mastery Test

You are ready for Chapter 6 when you automatically transform:

```text
"maximum number of products"
```

into:

```text
let k = products

what does k require?
```

and then:

```text
resource1:
cost1*k <= available1

resource2:
cost2*k <= available2
```

and recognize:

```text
k <= U1
k <= U2
      ↓
maximum k = min(U1,U2)
```

You should also recognize:

```text
minimum containers
```

as:

```text
capacity*k >= required
      ↓
k >= lower bound
```

and:

```text
k >= L1
k >= L2
      ↓
minimum k = max(L1,L2)
```

Most important question:

```text
Suppose the answer is k.

What MUST be true
for k to work?
```

---

# 5.33 Final Mental Engine

```text
              MAXIMUM / MINIMUM?
                       │
                       ▼
                 DEFINE k
                       │
                       ▼
             ASSUME ANSWER = k
                       │
                       ▼
          WHAT DOES k REQUIRE?
                       │
                       ▼
              WRITE INEQUALITIES
                       │
              ┌────────┴────────┐
              │                 │
         upper bounds      lower bounds
              │                 │
              ▼                 ▼
            min(...)           max(...)
              │                 │
              └────────┬────────┘
                       ▼
            CHECK INTEGER ROUNDING
                       │
                       ▼
       CHECK SHARED RESOURCE / LEFTOVERS
                       │
                       ▼
                 FINAL ANSWER
```

The main habit:

```text
Do not attack "maximum" directly.

Turn it into:

"Can k units be supported?"
```

That idea leads directly into the next chapter.

---

# Next Chapter

```text
5. RESOURCE & OPTIMIZATION MODELING
                 ↓
6. FEASIBILITY MODELING
```

Chapter 6 asks:

```text
Can k work?
```

and develops:

```text
optimization -> yes/no check
valid range of k
monotonicity
false false false true true...
true true true false false...
binary search on answer connection
```
