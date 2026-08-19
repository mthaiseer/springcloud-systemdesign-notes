# CP Mathematical Modeling Mini-Course

## 10. Rate, Time & Work Modeling

> **Goal:** Translate stories about machines, workers, speed, production, travel, filling, draining, catching, and deadlines into equations.
>
> The main habit is:
>
> **Identify the rate → choose a time → calculate how much work happens.**

---

# Chapter Tree

```text
10. RATE, TIME & WORK MODELING
│
├── 10.1 The core model
├── 10.2 Work = rate × time
├── 10.3 Distance = speed × time
├── 10.4 One job every t units
├── 10.5 Discrete vs continuous production
├── 10.6 Multiple independent workers
├── 10.7 Parallel production
├── 10.8 Minimum time as feasibility
├── 10.9 Relative speed
├── 10.10 Catching and meeting
├── 10.11 Opposite-direction motion
├── 10.12 Filling and draining
├── 10.13 Changing rates
├── 10.14 Units and conversions
├── 10.15 Bounds and overflow
└── 10.16 Complete CF-style examples
```

Central engine:

```text
STORY
  ↓
What changes over time?
  ↓
What is the RATE?
  ↓
Choose candidate time T
  ↓
amount produced/moved
  ↓
combine contributions
  ↓
enough?
```

---

# 10.1 The Core Relationship

The basic model is:

```text
amount = rate * time
```

Depending on the story, `amount` may mean:

```text
work
distance
water
items
data
money
progress
```

Examples:

```text
distance = speed * time

work = work_rate * time

water = flow_rate * time
```

Before looking for an algorithm, identify these three quantities:

```text
RATE
TIME
AMOUNT
```

---

# 10.2 Rearranging the Equation

From:

```text
amount = rate * time
```

we also get:

```text
time = amount / rate
```

and:

```text
rate = amount / time
```

ASCII triangle:

```text
        AMOUNT
       /      \
    RATE      TIME
```

But in CP, do not blindly use floating point. First ask whether the process is **continuous** or **discrete**.

---

# 10.3 Continuous Work

Suppose a pipe fills:

```text
3 liters / minute
```

After:

```text
T minutes
```

water added:

```text
3*T liters
```

For:

```text
T = 2.5
```

it can add:

```text
7.5 liters
```

if the model is continuous.

So:

```text
amount = rate*T
```

works directly.

---

# 10.4 Discrete Production

Now suppose:

> A machine completes **one whole product every 3 minutes**.

At:

```text
T = 8 minutes
```

it completes:

```text
floor(8/3) = 2
```

whole products.

Not:

```text
8/3 = 2.666 products
```

because unfinished products do not count.

So when:

```text
one item takes t time
```

number of completed items by time `T` is:

```text
floor(T/t)
```

In C++ integer division:

```cpp
T / t
```

when both are integers.

This distinction is fundamental:

```text
continuous rate:
amount = rate*T

whole jobs:
completed = floor(T/time_per_job)
```

---

# 10.5 Rate vs Time Per Item

Two statements can describe the same process differently.

### Form A

```text
machine makes 4 items per minute
```

Rate:

```text
4 items/minute
```

Production in `T`:

```text
4*T
```

### Form B

```text
machine takes 4 minutes per item
```

This is NOT rate `4`.

Its rate is:

```text
1/4 item per minute
```

Completed whole items:

```text
floor(T/4)
```

Common mistake:

```text
"4 minutes/item"
```

is reciprocal to:

```text
"items/minute"
```

---

# 10.6 Multiple Independent Machines

Suppose machines take:

```text
t1, t2, t3, ..., tn
```

time per item.

In candidate time `T`:

```text
machine 1 -> floor(T/t1)
machine 2 -> floor(T/t2)
...
machine n -> floor(T/tn)
```

If they work independently in parallel:

```text
total(T)
=
floor(T/t1)
+
floor(T/t2)
+
...
+
floor(T/tn)
```

ASCII:

```text
             candidate time T
                    │
       ┌────────────┼────────────┐
       ▼            ▼            ▼
   machine 1    machine 2    machine 3
    T/t1         T/t2         T/t3
       │            │            │
       └────────────┼────────────┘
                    ▼
              total output
```

Key modeling principle:

```text
independent contributions
        ↓
       SUM
```

---

# 10.7 Sequential vs Parallel Work

This distinction changes the equation.

Suppose two workers need:

```text
3 hours
5 hours
```

for their own assigned jobs.

If jobs happen sequentially:

```text
total time = 3 + 5 = 8
```

If both start simultaneously and we wait for both:

```text
total time = max(3,5) = 5
```

Visual:

```text
SEQUENTIAL

worker/job A: [---3---]
worker/job B:         [-----5-----]

total = 8
```

```text
PARALLEL

A: [---3---]
B: [-----5-----]

total = 5
```

Mental question:

```text
Do their times add,
or do their outputs happen simultaneously?
```

---

# 10.8 Minimum Time to Produce K Items

Classic problem:

> `n` machines.
> Machine `i` takes `t[i]` time per item.
> Find minimum time to produce at least `K` items.

Do not try to determine which machine makes which product.

Choose candidate time:

```text
T
```

Production:

```text
produced(T)
=
sum floor(T/t[i])
```

Then ask:

```text
produced(T) >= K ?
```

Define:

```text
can(T) =
sum floor(T/t[i]) >= K
```

Now the story is a feasibility problem.

---

# 10.9 Why This Is Monotonic

If `T` minutes are enough to produce `K` items, then:

```text
T+1
T+2
...
```

cannot produce fewer items.

Therefore:

```text
can(T)
```

looks like:

```text
F F F F F T T T T T ...
          ^
     minimum time
```

We want:

```text
first TRUE
```

This connects directly to Chapter 6.

```text
rate story
   ↓
production(T)
   ↓
can(T)
   ↓
monotonicity
   ↓
binary search on time
```

---

# 10.10 Small Example

Machines:

```text
t = [2,3]
```

Need:

```text
K = 5
```

Check:

```text
T = 3
```

Production:

```text
3/2 = 1
3/3 = 1

total = 2
```

Not enough.

Check:

```text
T = 6
```

Production:

```text
6/2 = 3
6/3 = 2

total = 5
```

Enough.

So:

```text
can(3) = false
can(6) = true
```

Try smaller:

```text
T = 5

5/2 = 2
5/3 = 1

total = 3
```

Not enough.

Therefore:

```text
minimum T = 6
```

---

# 10.11 Travel Modeling

Basic equation:

```text
distance = speed * time
```

Suppose:

```text
speed = v
time = T
```

Then position after `T`, starting at `x0` and moving right:

```text
position = x0 + v*T
```

Moving left:

```text
position = x0 - v*T
```

More generally, give velocity a sign:

```text
right -> positive
left  -> negative
```

Then:

```text
position(T) = initial_position + velocity*T
```

---

# 10.12 Meeting Equation

Person A:

```text
initial position = x1
speed = v1
```

Person B:

```text
initial position = x2
speed = v2
```

If moving in the same coordinate direction:

```text
A(T) = x1 + v1*T
B(T) = x2 + v2*T
```

They meet when:

```text
x1 + v1*T
=
x2 + v2*T
```

Rearrange:

```text
(v1-v2)*T
=
x2-x1
```

This is the algebraic model.

But often **relative speed** gives a cleaner interpretation.

---

# 10.13 Relative Speed — Same Direction

Suppose A is behind B.

```text
A -----> vA

       B -----> vB
```

Initial gap:

```text
D
```

If:

```text
vA > vB
```

A closes the gap at:

```text
vA-vB
```

distance units per time.

So:

```text
catch time
=
D / (vA-vB)
```

Why subtraction?

Each unit of time:

```text
A gains vA
B escapes by vB
```

Net gain:

```text
vA-vB
```

If:

```text
vA <= vB
```

A cannot catch B under this simple constant-speed model.

---

# 10.14 Relative Speed — Opposite Directions

Suppose two people move toward each other:

```text
A ----->       <----- B
```

Speeds:

```text
vA
vB
```

Gap decreases by:

```text
vA + vB
```

per unit time.

Meeting time:

```text
D / (vA+vB)
```

Mental model:

```text
same direction:
closing speed often = difference

toward each other:
closing speed = sum
```

Do not memorize only the formula; ask:

```text
How much does the gap change each unit of time?
```

---

# 10.15 Gap Modeling

This is often easier than tracking both positions.

Define:

```text
gap = position_B - position_A
```

Then ask:

```text
How does gap change each second?
```

Example same direction:

```text
gap_next
=
gap - (vA-vB)
```

If:

```text
vA-vB > 0
```

gap shrinks.

Meeting occurs when:

```text
gap = 0
```

This is a powerful modeling technique:

```text
two moving objects
       ↓
track their difference
       ↓
one variable: gap
```

---

# 10.16 Filling and Draining

Tank:

```text
inflow = A liters/minute
outflow = B liters/minute
```

If both operate simultaneously:

```text
net rate = A-B
```

After time `T`:

```text
net change = (A-B)*T
```

Example:

```text
A = 10
B = 4
```

Net:

```text
6 liters/minute
```

If target increase is:

```text
30 liters
```

time:

```text
30/6 = 5 minutes
```

Again, track the quantity that matters:

```text
net change
```

rather than simulating inflow and outflow separately.

---

# 10.17 Opposing Rates

Many stories reduce to:

```text
positive contribution
-
negative contribution
```

Examples:

```text
fill - drain
income - spending
production - consumption
climb - slide
attack - healing
download - upload competition
```

Net rate:

```text
net = gain_rate - loss_rate
```

But be careful: this works directly only if both rates operate over the same period and under the same rules.

---

# 10.18 Changing Rates

Suppose:

```text
first 5 seconds: rate = 3
next 4 seconds: rate = 7
```

Do not use one constant rate unless justified.

Total:

```text
3*5 + 7*4
```

General piecewise model:

```text
total =
rate1*time1
+
rate2*time2
+
...
```

Visual:

```text
time:

0---------5---------9

rate:
   3          7

work:
3*5        + 7*4
```

Break the timeline into phases where the rule is constant.

---

# 10.19 Repeated Cycles

Suppose a process repeats every:

```text
C
```

time units.

Each complete cycle contributes:

```text
W
```

work.

For total time `T`:

```text
full_cycles = T / C
remaining   = T % C
```

Then:

```text
work =
full_cycles * W
+
work_during_remaining_time
```

This connects rate/time modeling with modulo modeling.

ASCII:

```text
T
│
├── full cycles: T/C
│
└── leftover:    T%C
```

---

# 10.20 Units Must Match

Suppose:

```text
speed = 60 km/hour
time = 30 minutes
```

Do NOT calculate:

```text
60 * 30
```

Units conflict.

Convert:

```text
30 minutes = 0.5 hours
```

Then:

```text
distance = 60 * 0.5 = 30 km
```

In integer CP problems, often convert everything into the smallest convenient unit:

```text
hours -> minutes
minutes -> seconds
```

Mental check:

```text
Are my units compatible?
```

---

# 10.21 Units as a Debugging Tool

Equation:

```text
distance = speed * time
```

Units:

```text
km = (km/hour) * hour
```

Correct.

But:

```text
distance = speed / time
```

would produce:

```text
km/hour^2
```

which is not distance.

Units can reveal incorrect equations before coding.

---

# 10.22 Ceil for Whole Time Units

Suppose:

```text
rate = 3 items/minute
```

Need:

```text
10 items
```

If production is continuous but answer must be a whole number of minutes:

```text
time >= 10/3
```

Minimum integer time:

```text
ceil(10/3) = 4
```

Integer formula for positive integers:

```text
(a+b-1)/b
```

So:

```text
(10+3-1)/3 = 4
```

But distinguish this from:

```text
one item every 3 minutes
```

where production by time `T` is:

```text
T/3
```

The story determines the model.

---

# 10.23 Completion Times

If one machine takes:

```text
t
```

time per product and starts at time `0`, products complete at:

```text
t
2t
3t
4t
...
```

By time `T`, number completed:

```text
floor(T/t)
```

Visual:

```text
time:
0----t----2t----3t----4t

     ✓     ✓     ✓     ✓
```

This picture makes `T/t` intuitive.

---

# 10.24 Starting-Time Variants

Suppose a machine first produces at time:

```text
s
```

then every:

```text
t
```

time units.

Completion times:

```text
s
s+t
s+2t
...
```

For candidate time `T`:

If:

```text
T < s
```

production:

```text
0
```

Otherwise:

```text
1 + floor((T-s)/t)
```

This is a good example of deriving a formula from the timeline rather than memorizing one.

---

# 10.25 Minimum / Maximum Rate Bottleneck

Suppose a pipeline has sequential stages with capacities:

```text
10 items/s
6 items/s
20 items/s
```

If every item must pass through all stages and the system reaches a steady pipeline, throughput may be limited by the slowest stage:

```text
min(10,6,20) = 6 items/s
```

This is a bottleneck model.

But do not apply `min(rate)` blindly: startup delay, batching, dependencies, and non-pipelined processing can change the model.

First understand how stages interact.

---

# 10.26 Work Sharing

Suppose a total workload is:

```text
W
```

and workers have continuous rates:

```text
r1, r2, ..., rn
```

If they can independently work on divisible portions simultaneously:

```text
combined rate =
r1+r2+...+rn
```

Then:

```text
time =
W / combined_rate
```

But for indivisible jobs with different completion times, you may instead need:

```text
sum floor(T/t[i])
```

This distinction is crucial.

```text
DIVISIBLE CONTINUOUS WORK
      -> add rates

DISCRETE WHOLE JOBS
      -> count completions by T
```

---

# 10.27 Rate Problems and Binary Search

A common CF pattern:

```text
Find minimum T
such that enough work is completed.
```

Translation:

```text
candidate = T

can(T):
    calculate work completed by T
    return work >= target
```

Then prove:

```text
if T works,
all larger times work
```

Pattern:

```text
F F F F T T T T
        ^
     first TRUE
```

This is one of the most common ways mathematical modeling leads to binary search on answer.

---

# 10.28 Search Bounds for Production Time

Suppose fastest machine needs:

```text
min_t
```

per product.

To make `K` products, even using only this machine:

```text
K * min_t
```

time is enough.

Therefore a safe upper bound can be:

```text
hi = K * min_t
```

Lower bound:

```text
0
```

So:

```text
answer in [0, K*min_t]
```

Watch for overflow when values are large.

---

# 10.29 Overflow During Production Checks

Suppose:

```text
K <= 10^18
```

and many machines produce huge counts.

You only care whether:

```text
produced >= K
```

So while summing:

```text
produced += T/t[i]
```

stop when:

```text
produced >= K
```

There is no benefit in calculating a much larger total.

Mental rule:

```text
Feasibility checks need enough information
to answer YES/NO — no more.
```

---

# 10.30 Complete CF-Style Example 1 — Machines

Problem:

> Machines take `2`, `3`, and `7` seconds per item.
> Can they produce at least `10` items in `T = 8` seconds?

Production:

```text
8/2 = 4
8/3 = 2
8/7 = 1
```

Total:

```text
4+2+1 = 7
```

Therefore:

```text
can(8) = false
```

The calculation is:

```text
sum floor(T/t[i])
```

---

# 10.31 Complete CF-Style Example 2 — Catching

A starts at:

```text
0
```

B starts at:

```text
100
```

Both move right.

Speeds:

```text
A = 15
B = 10
```

Initial gap:

```text
100
```

Closing speed:

```text
15-10 = 5
```

Catch time:

```text
100/5 = 20
```

Check positions:

```text
A:
0 + 15*20 = 300

B:
100 + 10*20 = 300
```

Correct.

---

# 10.32 Complete CF-Style Example 3 — Moving Toward Each Other

Distance:

```text
120
```

Speeds:

```text
40
20
```

toward each other.

Gap closes at:

```text
40+20 = 60
```

Meeting time:

```text
120/60 = 2
```

---

# 10.33 Complete CF-Style Example 4 — Tank

Tank currently has:

```text
20 liters
```

Target:

```text
80 liters
```

Inflow:

```text
9 L/min
```

Drain:

```text
3 L/min
```

Net:

```text
6 L/min
```

Needed increase:

```text
80-20 = 60
```

Time:

```text
60/6 = 10 minutes
```

Model:

```text
initial + net_rate*T = target
```

---

# 10.34 Complete CF-Style Example 5 — Repeating Work Cycle

A worker follows:

```text
3 minutes working
2 minutes resting
```

During each working minute, completes:

```text
4 units
```

Cycle:

```text
5 minutes
```

Work per full cycle:

```text
3*4 = 12
```

For candidate time `T`:

```text
full = T/5
rem  = T%5
```

Full-cycle work:

```text
full*12
```

Working time inside remainder:

```text
min(rem,3)
```

Extra work:

```text
min(rem,3)*4
```

Total:

```text
(T/5)*12 + min(T%5,3)*4
```

This combines:

```text
rate
+
cycle
+
modulo
+
min
```

---

# 10.35 Complete CF-Style Example 6 — First Production Delay

Machine:

```text
first item at time 5
then one every 3 seconds
```

At:

```text
T = 13
```

Completion times:

```text
5,8,11
```

and next:

```text
14
```

So completed:

```text
3
```

Formula:

```text
1 + (13-5)/3
=
1 + 8/3
=
3
```

provided:

```text
T >= 5
```

---

# 10.36 Translation Checklist

When a story contains time/work/speed, write:

```text
1. WHAT QUANTITY CHANGES?
   distance?
   jobs?
   water?
   gap?

2. WHAT IS THE RATE?

3. IS RATE GIVEN AS:
   amount/time
   OR
   time/item?

4. CONTINUOUS OR DISCRETE?

5. PARALLEL OR SEQUENTIAL?

6. IF MULTIPLE SOURCES:
   do contributions add?

7. CAN I TRACK A DIFFERENCE?
   gap?
   net flow?

8. DOES THE RATE CHANGE IN PHASES?

9. IS THERE A REPEATING CYCLE?

10. IF FINDING MINIMUM TIME:
    can(T) = enough by T?

11. IS can(T) MONOTONIC?

12. ARE UNITS CONSISTENT?
```

---

# 10.37 Common Mistakes

## Mistake 1 — Confusing rate with time per item

```text
5 items/min
```

is different from:

```text
5 min/item
```

They are reciprocals.

---

## Mistake 2 — Adding parallel completion times

If two independent tasks happen simultaneously:

```text
completion time
```

may be governed by `max`, not sum.

---

## Mistake 3 — Using fractional products

If products are indivisible:

```text
T/t
```

means integer completed jobs.

---

## Mistake 4 — Wrong relative speed

Same direction:

```text
difference
```

when one is catching another.

Toward each other:

```text
sum
```

because both shrink the gap.

---

## Mistake 5 — Ignoring units

Do not mix:

```text
hours
minutes
seconds
```

inside one equation without conversion.

---

## Mistake 6 — Binary searching before building can(T)

First derive:

```text
what exactly happens by time T?
```

Then prove monotonicity.

Binary search is the final algorithmic consequence, not the starting point.

---

# 10.38 Translation Drills

Do not code.

---

## Drill 1

Machine takes:

```text
4 seconds/item
```

Time:

```text
T = 19
```

Completed:

```text
19/4 = 4
```

whole items.

---

## Drill 2

Two machines:

```text
3 sec/item
5 sec/item
```

At:

```text
T = 15
```

Production:

```text
15/3 + 15/5
=
5+3
=
8
```

---

## Drill 3

Two objects move toward each other.

Speeds:

```text
7
11
```

Closing rate:

```text
18
```

---

## Drill 4

Inflow:

```text
12
```

Outflow:

```text
5
```

Net rate:

```text
7
```

---

## Drill 5

Cycle:

```text
work 4 seconds
rest 3 seconds
```

Cycle length:

```text
7
```

For time `T`:

```text
full cycles = T/7
remainder   = T%7
```

---

# 10.39 Practice Set

For each problem write:

```text
CHANGING QUANTITY:
RATE:
TIME VARIABLE:
CONTINUOUS/DISCRETE:
PARALLEL/SEQUENTIAL:
EQUATION:
FEASIBILITY CHECK:
```

---

## Practice A

> One machine takes `t` seconds per item. How many items are complete by time `T`?

---

## Practice B

> `n` machines have times `t[i]`. Can they produce `K` items by time `T`?

---

## Practice C

> A is behind B by distance `D`. They move in the same direction at speeds `vA` and `vB`. When can A catch B?

---

## Practice D

> Two people distance `D` apart move toward each other at speeds `v1` and `v2`.

---

## Practice E

> A tank gains `A` liters/minute and loses `B` liters/minute simultaneously.

---

# 10.40 Practice Answers

## A

```text
completed = floor(T/t)
```

---

## B

```text
produced(T)
=
sum floor(T/t[i])

can(T):
produced(T) >= K
```

---

## C

If:

```text
vA <= vB
```

A cannot catch B under the basic model.

Otherwise:

```text
relative speed = vA-vB

time = D/(vA-vB)
```

---

## D

```text
closing speed = v1+v2

time = D/(v1+v2)
```

---

## E

```text
net rate = A-B
```

After time `T`:

```text
change = (A-B)*T
```

with additional case reasoning if the net rate is zero/negative or tank bounds matter.

---

# 10.41 Chapter Mastery Test

You are ready for the next chapter when a machine problem makes you immediately ask:

```text
If I choose time T,
how many jobs are complete?
```

You should distinguish:

```text
r items/time
    -> r*T

t time/item
    -> floor(T/t)
```

You should see multiple independent machines and think:

```text
sum their production by T
```

You should see catching/meeting and think:

```text
track the GAP
```

You should see filling and draining and think:

```text
NET RATE
```

And for minimum-time problems:

```text
candidate T
   ↓
work(T)
   ↓
enough?
   ↓
monotonic?
```

---

# 10.42 Final Mental Engine

```text
                 TIME STORY
                     │
                     ▼
             IDENTIFY QUANTITY
                     │
                     ▼
               IDENTIFY RATE
                     │
          ┌──────────┴──────────┐
          │                     │
      continuous             discrete
          │                     │
       rate*T              floor(T/t)
          │                     │
          └──────────┬──────────┘
                     ▼
              MULTIPLE SOURCES?
                     │
          ┌──────────┴──────────┐
          │                     │
       parallel             opposing
          │                     │
    sum production         net / gap rate
          │                     │
          └──────────┬──────────┘
                     ▼
              FIND MIN TIME?
                     │
                     ▼
                 can(T)
                     │
                     ▼
                monotonic?
                     │
                     ▼
        direct equation / binary search
```

The core habit:

```text
Do not simulate time first.

Freeze time at T and ask:

"What must have happened by now?"
```

That single question converts many story problems into clean equations.

---

# Next Chapter

```text
10. RATE, TIME & WORK MODELING
                 ↓
11. INVARIANT & CONSERVATION MODELING
```

Chapter 11 will focus on recognizing quantities/properties that do not change, or change predictably, under operations:

```text
sum
parity
difference
gcd-like properties
counts
color/state classes
operation effect tables
necessary impossibility conditions
```

The focus will be mathematical modeling rather than repeating number theory from TLE/AZ.
