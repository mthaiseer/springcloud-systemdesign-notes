# ICPSC / AlgoZenith Greedy Classical Problems — Study Notes

These notes collect the greedy problems discussed in class and in our walkthroughs.

The goal is not only to memorize the solution, but to understand:

- what the greedy choice is,
- why it is safe,
- how to prove it,
- how to map it to a real-world situation,
- how to dry-run it,
- and how to implement it in C++.

---

# 0. Greedy Proof Mental Model

A greedy algorithm makes the best-looking choice **right now**.

The important question is:

> Why does this local choice not destroy the global optimum?

A very common proof is the **exchange argument**.

## Exchange argument in simple words

Imagine someone already has a perfect optimal solution.

```text
OPTIMAL PLAN

O -> A -> B -> C -> ...
^
|
optimal chose O
```

Your greedy algorithm wants to choose `G`.

Try replacing `O` with `G`:

```text
G -> A -> B -> C -> ...
```

Now prove:

```text
1. The solution is still valid.
2. The answer is not worse.
```

If both are true, then your greedy choice is safe.

### Short real-world mapping

Suppose you are scheduling meetings.

```text
OPT:
[----- O -----] [Meeting 2] [Meeting 3]

GREEDY:
[-- G --]      [Meeting 2] [Meeting 3]
```

If `G` ends earlier than `O`, replacing `O` with `G` does not hurt later meetings.

So:

> Take a perfect plan, insert the greedy choice into it, and prove the plan does not get worse.

---

# 1. Pick Two Integers With Maximum Sum


## Exchange / Mathematical Proof

Let the greedy pair be the two largest values:

```text
M1 >= M2 >= every other candidate X
G = M1 + M2
```

Assume another solution replaces `M2` by `X`:

```text
A = M1 + X
```

Compare:

```text
A - G
= (M1 + X) - (M1 + M2)
= X - M2
```

Since:

```text
X <= M2
```

therefore:

```text
A - G <= 0
=> A <= G
```

So exchanging `M2` for any smaller value cannot improve the answer.

**Mental proof:** replacing one of the top two values by a smaller value cannot increase the sum.


## Problem

Given an array of `N` integers, choose two different elements such that their sum is maximum.

Example:

```text
Array = [2, 5, 8, 11, 15]
```

## Greedy observation

Choose:

```text
largest + second_largest
```

For the example:

```text
2   5   8   11   15
            ↑     ↑
           2nd   1st
           max   max

Answer = 11 + 15 = 26
```

## Why is this correct?

Let:

```text
M1 = largest
M2 = second largest
```

Greedy answer:

```text
G = M1 + M2
```

Suppose another solution uses some other value `X` instead of `M2`.

Since `M2` is the second largest:

```text
X <= M2
```

Therefore:

```text
M1 + X <= M1 + M2
```

So no alternative can beat the greedy answer.

## Difference proof

Suppose an alternative uses `fourth_max` instead of `second_max`.

```text
G = first_max + second_max
A = first_max + fourth_max
```

Compare:

```text
A - G

= (first_max + fourth_max)
  - (first_max + second_max)

= fourth_max - second_max
```

Since:

```text
fourth_max <= second_max
```

we get:

```text
A - G <= 0
```

Hence:

```text
A <= G
```

## Real-world mapping

Imagine choosing two employees with the highest individual scores to maximize the sum of their scores.

```text
Scores: 40, 55, 70, 90, 95
```

To maximize total score, choose:

```text
95 + 90
```

Any replacement must use a score `<= 90`, so the sum cannot improve.

## C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {2, 5, 8, 11, 15};

    int firstMax = INT_MIN;
    int secondMax = INT_MIN;

    for (int x : a) {
        if (x > firstMax) {
            secondMax = firstMax;
            firstMax = x;
        } else if (x > secondMax) {
            secondMax = x;
        }
    }

    cout << firstMax + secondMax << '\n';
}
```

Time:

```text
O(N)
```

---

# 2. Split N Into Two Numbers With Maximum Product


## Exchange / Mathematical Proof

Let the balanced greedy solution be:

```text
x + x = N
G = x^2
```

Any other split with the same sum can be represented as:

```text
x + delta
x - delta
```

Alternative product:

```text
A = (x + delta)(x - delta)
  = x^2 - delta^2
```

Compare:

```text
A - G
= (x^2 - delta^2) - x^2
= -delta^2
<= 0
```

Therefore:

```text
A <= G
```

So moving weight from one side to the other can never improve the product.

For integer odd `N`, equality is impossible, so use the two closest integers:

```text
floor(N/2), ceil(N/2)
```


## Problem

Split `N` into two numbers:

```text
a + b = N
```

such that:

```text
a * b
```

is maximum.

Example:

```text
N = 10
```

Possible splits:

```text
1 + 9 -> 1 * 9 = 9
2 + 8 -> 2 * 8 = 16
3 + 7 -> 3 * 7 = 21
4 + 6 -> 4 * 6 = 24
5 + 5 -> 5 * 5 = 25  <- maximum
```

## Greedy observation

For a fixed sum:

> Product is maximum when the two numbers are as equal as possible.

For even `N`:

```text
a = N / 2
b = N / 2
```

For odd `N`:

```text
a = floor(N / 2)
b = ceil(N / 2)
```

## Proof

Let:

```text
x = N / 2
```

Balanced split:

```text
x, x
```

Product:

```text
x^2
```

Now move `delta` from one side to the other:

```text
x + delta
x - delta
```

The sum remains unchanged:

```text
(x + delta) + (x - delta) = 2x = N
```

New product:

```text
(x + delta)(x - delta)
= x^2 - delta^2
```

Since:

```text
delta^2 >= 0
```

we get:

```text
x^2 - delta^2 <= x^2
```

So moving away from equality never improves the product.

## ASCII intuition

```text
Balanced:

      x        x
      |        |
      +--------+
          N

Product = x^2
```

Move away from balance:

```text
     x+d      x-d

Product
= (x+d)(x-d)
= x^2 - d^2
<= x^2
```

## Real-world mapping

You have `10` meters of fencing to divide into two side lengths.

If the product represents area, then:

```text
1 x 9 = 9
2 x 8 = 16
3 x 7 = 21
4 x 6 = 24
5 x 5 = 25
```

The most balanced split gives the largest area.

## C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long N = 11;

    long long a = N / 2;
    long long b = N - a;

    cout << a * b << '\n';
}
```

---

# 3. Maximum Number of Non-Overlapping Jobs


## Exchange Argument + Mathematical Condition

Let:

```text
G = greedy first job (earliest finishing)
O = first job of an optimal schedule
```

By definition of `G`:

```text
end(G) <= end(O)
```

Suppose OPT is:

```text
[ O ] -> J2 -> J3 -> ... -> Jk
```

Since `J2` was valid after `O`:

```text
start(J2) >= end(O)
```

and:

```text
end(G) <= end(O)
```

we also have:

```text
start(J2) >= end(G)
```

Therefore replace:

```text
O -> G
```

without removing any later job:

```text
Before: [ O ] -> J2 -> J3 -> ...
After : [ G ] -> J2 -> J3 -> ...
```

The number of selected jobs remains the same.

So there exists an optimal schedule beginning with the greedy job. Apply the same argument to the remaining jobs.

**Mathematical heart:**

```text
end(G) <= end(O) <= start(next)
```

therefore:

```text
end(G) <= start(next)
```

So the rest of the optimal schedule still fits.


Also called:

- Activity Selection
- Interval Scheduling

## Problem

You are given `N` jobs.

Each job has:

```text
start time
end time
```

Choose the maximum number of jobs such that no selected jobs overlap.

Example:

```text
A = [1,4]
B = [3,5]
C = [0,6]
D = [5,7]
E = [8,9]
F = [5,9]
```

Timeline:

```text
time -> 0 1 2 3 4 5 6 7 8 9

A       [------]
B           [----]
C     [----------]
D               [----]
E                     [--]
F               [--------]
```

## Greedy observation

Choose the job that **finishes earliest**.

Why?

Because finishing earlier leaves maximum room for future jobs.

## Algorithm

1. Sort jobs by end time.
2. Take the first job.
3. For every next job:
   - if `start >= previous_end`, take it.
4. Repeat.

## Dry run

Sorted by end:

```text
A [1,4]
B [3,5]
C [0,6]
D [5,7]
E [8,9]
F [5,9]
```

Take A:

```text
A = [1,4]
```

B starts at `3`:

```text
3 < 4
```

Skip.

C starts at `0`:

```text
0 < 4
```

Skip.

D starts at `5`:

```text
5 >= 4
```

Take.

E starts at `8`:

```text
8 >= 7
```

Take.

Final:

```text
A -> D -> E
```

Answer:

```text
3 jobs
```

## Exchange argument proof

Let:

```text
G = earliest finishing job
O = first job in some optimal solution
```

Since `G` finishes earliest:

```text
end(G) <= end(O)
```

Optimal schedule:

```text
[------ O ------] [A] [B] [C]
```

Replace `O` with `G`:

```text
[--- G ---]       [A] [B] [C]
```

Because `G` ends no later, everything that fit after `O` still fits after `G`.

So the number of jobs does not decrease.

Therefore the greedy first choice is safe.

## Real-world mapping

You want to attend the maximum number of meetings.

If one meeting ends at 10:00 and another ends at 12:00, choosing the one ending at 10:00 leaves more of the day free for more meetings.

## C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<pair<int,int>> jobs = {
        {1,4}, {3,5}, {0,6}, {5,7}, {8,9}, {5,9}
    };

    sort(jobs.begin(), jobs.end(),
         [](auto &a, auto &b) {
             return a.second < b.second;
         });

    int count = 0;
    int lastEnd = INT_MIN;

    for (auto [start, finish] : jobs) {
        if (start >= lastEnd) {
            count++;
            lastEnd = finish;
        }
    }

    cout << count << '\n';
}
```

Time:

```text
O(N log N)
```

---

# 4. Can All Jobs Finish Before Their Deadlines?


## Exchange Argument + Mathematical Proof

Assume two adjacent jobs are in this order:

```text
B -> A
```

but:

```text
deadline(A) <= deadline(B)
```

So `A` is more urgent but appears later.

Let:

```text
T  = time before these jobs
pA = duration of A
pB = duration of B
dA = deadline of A
dB = deadline of B
```

Because the original schedule is feasible:

```text
T + pB + pA <= dA
```

Now swap them:

```text
A -> B
```

A finishes at:

```text
T + pA
```

and:

```text
T + pA
<= T + pB + pA
<= dA
```

So A is safe.

B finishes at:

```text
T + pA + pB
```

This is the same total finishing time that A had before. Since:

```text
T + pA + pB <= dA
```

and:

```text
dA <= dB
```

we get:

```text
T + pA + pB <= dB
```

So B is also safe.

Therefore:

```text
later-deadline -> earlier-deadline
```

can safely be exchanged into:

```text
earlier-deadline -> later-deadline
```

Repeating this removes all deadline inversions and produces deadline-sorted order.

**Mental proof:** the more urgent job can safely move left.


## Problem

You are given `N` jobs.

Each job has:

```text
processing time
deadline
```

Only one job can run at a time.

You start at time `0`.

Question:

> Is there an ordering of all jobs such that every job finishes on or before its own deadline?

Example:

```text
deadline = [2, 1, 5, 2]
time     = [2, 1, 1, 3]
```

Jobs:

```text
A: time 2, deadline 2
B: time 1, deadline 1
C: time 1, deadline 5
D: time 3, deadline 2
```

## Greedy observation

Sort by **earliest deadline first**.

Why?

```text
Due sooner -> do sooner
Due later  -> can wait longer
```

## Prefix-sum condition

After sorting by deadline:

```text
J1 <= d1
J1 + J2 <= d2
J1 + J2 + J3 <= d3
...
J1 + ... + Jn <= dn
```

This is simply checking the accumulated completion time after each job.

## Dry run

Sort by deadline:

```text
Job    Time    Deadline
B       1        1
A       2        2
D       3        2
C       1        5
```

Start:

```text
currentTime = 0
```

Job B:

```text
currentTime = 1
1 <= 1  ✓
```

Job A:

```text
currentTime = 1 + 2 = 3
3 <= 2  ✗
```

So answer:

```text
NO
```

Also notice Job D alone needs `3` units but has deadline `2`.

Even if it starts at `0`:

```text
0---------2---3
          ^   ^
      deadline finish
```

So it can never finish in time.

## Logical proof

Suppose two jobs are:

```text
A: earlier deadline
B: later deadline
```

If the schedule has:

```text
B -> A
```

then the more urgent job `A` is being delayed behind a less urgent job.

Swap them:

```text
A -> B
```

`A` finishes earlier.

`B` finishes at the same final total time for the pair.

Therefore moving the earlier-deadline job forward does not break a feasible schedule.

Simple rule:

> The most urgent job should not wait behind a less urgent job.

## Real-world mapping

You have office tasks:

```text
Send report  -> due 10 AM
Fix bug      -> due 1 PM
Make slides  -> due 5 PM
```

Do the task due first.

## C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> deadline = {2, 1, 5, 2};
    vector<int> duration = {2, 1, 1, 3};

    int n = deadline.size();

    vector<pair<int,int>> jobs;

    for (int i = 0; i < n; i++) {
        jobs.push_back({deadline[i], duration[i]});
    }

    sort(jobs.begin(), jobs.end());

    long long currentTime = 0;

    for (auto [d, t] : jobs) {
        currentTime += t;

        if (currentTime > d) {
            cout << "NO\n";
            return 0;
        }
    }

    cout << "YES\n";
}
```

Time:

```text
O(N log N)
```

---

# 5. Job Sequencing With Deadline and Profit


## Exchange / Mathematical Safety Argument

There are two greedy decisions here:

```text
1. Prefer higher-profit jobs.
2. Put an accepted job in the latest free slot <= its deadline.
```

### Why place it as late as possible?

Suppose job `J` with deadline `d` is currently placed in an earlier free slot `s`, while a later slot `t` is also free:

```text
s < t <= d
```

Move `J` from `s` to `t`:

```text
Before:
slot s = J
slot t = EMPTY

After:
slot s = EMPTY
slot t = J
```

Validity is unchanged because:

```text
t <= d
```

Profit is unchanged:

```text
newProfit = oldProfit
```

But now the earlier slot `s` is free, which is at least as useful for future jobs because any job that can only meet an early deadline may need it.

Therefore an accepted job can always be pushed to its latest available valid slot without hurting profit.

### Why process higher profit first?

Suppose one available slot can hold either:

```text
H: profit = PH
L: profit = PL
```

with:

```text
PH >= PL
```

If a feasible schedule uses `L` in that contested slot while `H` can legally occupy it, exchanging:

```text
L -> H
```

changes profit by:

```text
new - old
= PH - PL
>= 0
```

So the exchange does not reduce profit.

Combined with latest-slot placement, this gives the classical greedy strategy.

**Mental proof:**

```text
higher profit gets priority
+
push chosen job right as far as its deadline allows
```


## Problem

Each job has:

```text
deadline
profit
```

Every job takes exactly **1 unit of time**.

You may skip jobs.

Goal:

> Find the maximum total profit.

Lecture example:

```text
profit   = [10, 15, 8, 7, 5]
deadline = [ 0,  1, 2, 4, 1]
```

Time starts from `0`.

## Greedy observation

1. Sort jobs by profit descending.
2. For each job, place it in the **latest available slot <= deadline**.
3. If no slot exists, skip it.

Why latest slot?

Because earlier slots may be required by jobs with tighter deadlines.

## Sort by profit

```text
(15,1)
(10,0)
( 8,2)
( 7,4)
( 5,1)
```

## Dry run

Initial slots:

```text
slot:   0     1     2     3     4
       [ ]   [ ]   [ ]   [ ]   [ ]
```

Take `(15,1)`:

```text
latest free slot <= 1 -> slot 1

[ ] [15] [ ] [ ] [ ]
```

Take `(10,0)`:

```text
slot 0

[10] [15] [ ] [ ] [ ]
```

Take `(8,2)`:

```text
slot 2

[10] [15] [8] [ ] [ ]
```

Take `(7,4)`:

```text
slot 4

[10] [15] [8] [ ] [7]
```

Take `(5,1)`:

```text
slot 1 occupied
slot 0 occupied

skip
```

Final:

```text
slot:    0     1     2      3      4
        [10]  [15]  [8]   [EMPTY] [7]
```

Profit:

```text
10 + 15 + 8 + 7 = 40
```

## Why latest possible slot?

Suppose:

```text
A: profit 100, deadline 2
B: profit  50, deadline 1
```

Bad:

```text
slot 1   slot 2
[A]      [ ]
```

Then B cannot fit.

Better:

```text
slot 1   slot 2
[B]      [A]
```

Now both fit.

So:

```text
profit decides WHICH job first
deadline decides WHERE to place it
```

## Real-world mapping

You are a freelancer.

Every job takes one hour.

Some jobs pay more, but each has a latest completion time.

Take the highest-paying jobs first, while pushing them as late as safely possible so early time remains free for urgent jobs.

## C++ code — O(N^2)

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> profit   = {10, 15, 8, 7, 5};
    vector<int> deadline = {0,  1, 2, 4, 1};

    int n = profit.size();

    vector<pair<int,int>> jobs; // {profit, deadline}

    for (int i = 0; i < n; i++) {
        jobs.push_back({profit[i], deadline[i]});
    }

    sort(jobs.rbegin(), jobs.rend());

    int maxDeadline = *max_element(deadline.begin(), deadline.end());

    vector<int> slot(maxDeadline + 1, -1);

    int totalProfit = 0;

    for (auto [p, d] : jobs) {

        for (int s = d; s >= 0; s--) {

            if (slot[s] == -1) {
                slot[s] = p;
                totalProfit += p;
                break;
            }
        }
    }

    cout << "Schedule: ";

    for (int x : slot) {
        if (x == -1)
            cout << "EMPTY ";
        else
            cout << x << ' ';
    }

    cout << "\nMaximum Profit: " << totalProfit << '\n';
}
```

## Complexity

Sorting:

```text
O(N log N)
```

Backward slot search for every job:

```text
O(N^2)
```

Overall:

```text
O(N^2)
```

There is a faster DSU-based version, but this implementation is best for learning the greedy idea.

---

# 6. Fractional Knapsack


## Exchange Argument + Mathematical Proof

Let item `A` have a higher value density than item `B`:

```text
vA / wA >= vB / wB
```

Suppose a candidate solution contains some amount `x` of B while some amount of A is still available.

Exchange the same weight `x`:

```text
remove x weight of B
add    x weight of A
```

Value removed:

```text
x * (vB / wB)
```

Value added:

```text
x * (vA / wA)
```

Change in total value:

```text
NEW - OLD

= x * (vA/wA) - x * (vB/wB)

= x * [(vA/wA) - (vB/wB)]
```

Because:

```text
vA/wA >= vB/wB
```

and:

```text
x >= 0
```

therefore:

```text
NEW - OLD >= 0
```

So replacing lower-density weight with equal weight of higher-density material never decreases value.

That is why the optimal fractional solution takes:

```text
highest value/weight first
```

until the bag is full.

**Real-world interpretation:** if gold pays more per kg than silver, carrying silver while unused gold is available cannot be optimal.


## Problem

You have `N` items.

Each item has:

```text
weight
value
```

Knapsack capacity:

```text
W
```

You may take **any fraction** of an item.

Goal:

> Maximize total value.

Lecture example:

```text
weight = [2, 3, 4, 5]
value  = [3, 4, 5, 6]
W = 5
```

## Greedy observation

Calculate:

```text
value / weight
```

This means:

> How much value do I get for 1 unit of bag capacity?

Example:

```text
A: 3/2 = 1.50
B: 4/3 = 1.33
C: 5/4 = 1.25
D: 6/5 = 1.20
```

Take items in descending ratio.

## Real-world mapping

Imagine the items are materials you can cut:

```text
Gold
Silver
Rice
Metal
```

If one material gives more money per kilogram, fill the bag with that material first.

## Simple fractional example

```text
A: weight 3, value 12
B: weight 4, value 12
Capacity W = 5
```

Ratios:

```text
A: 12/3 = 4 per kg
B: 12/4 = 3 per kg
```

Take A fully:

```text
[ A ][ A ][ A ][ _ ][ _ ]

Used = 3
Remaining = 2
Value = 12
```

B weighs `4`, but only `2` capacity remains.

Fraction that fits:

```text
remaining / weight
= 2 / 4
= 1/2
```

So take half of B.

Half of B's value:

```text
12 * 1/2 = 6
```

Final:

```text
[ A ][ A ][ A ][ B ][ B ]

Total value
= 12 + 6
= 18
```

## Two important formulas

### Value density

```text
value / weight
```

Meaning:

> value obtained from 1 unit of capacity.

### Fraction that fits

```text
remaining_capacity / item_weight
```

Meaning:

> what fraction of the item can still fit.

## Why greedy works

Suppose:

```text
Gold   = 100 value/kg
Silver = 50 value/kg
```

If your bag contains silver while some gold is still available, replace the same weight of silver with gold.

Weight stays the same.

Value increases.

Therefore using a lower-ratio item before a higher-ratio item can never be optimal.

## C++ code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> weight = {3, 4};
    vector<int> value  = {12, 12};

    int W = 5;
    int n = weight.size();

    // {ratio, weight, value}
    vector<tuple<double,int,int>> items;

    for (int i = 0; i < n; i++) {
        double ratio = (double)value[i] / weight[i];

        items.push_back({ratio, weight[i], value[i]});
    }

    sort(items.rbegin(), items.rend());

    double totalValue = 0;
    int remaining = W;

    for (auto [ratio, wt, val] : items) {

        if (remaining == 0)
            break;

        if (wt <= remaining) {

            totalValue += val;
            remaining -= wt;

        } else {

            double fraction = (double)remaining / wt;

            totalValue += val * fraction;

            remaining = 0;
        }
    }

    cout << "Maximum value = " << totalValue << '\n';
}
```

Time:

```text
O(N log N)
```

## Important distinction

```text
Can take fraction?

YES -> Fractional Knapsack -> Greedy

NO  -> 0/1 Knapsack -> usually Dynamic Programming
```

---

# 7. Minimum Dot Product of Two Arrays


## Exchange Argument — Full Algebra

Assume:

```text
a[i] <= a[k]
b[i] <= b[k]
```

The same-direction pairing is:

```text
OLD = a[i] * b[i] + a[k] * b[k]
```

Swap the two `b` values:

```text
NEW = a[i] * b[k] + a[k] * b[i]
```

Now compare step by step:

```text
NEW - OLD

= a[i]b[k] + a[k]b[i]
  - (a[i]b[i] + a[k]b[k])

= a[i]b[k] + a[k]b[i]
  - a[i]b[i] - a[k]b[k]

= a[i]b[k] - a[i]b[i]
  + a[k]b[i] - a[k]b[k]

= a[i](b[k] - b[i])
  + a[k](b[i] - b[k])

= a[i](b[k] - b[i])
  - a[k](b[k] - b[i])

= (a[i] - a[k]) * (b[k] - b[i])
```

Now inspect the signs:

```text
a[i] <= a[k]
=> a[i] - a[k] <= 0

b[i] <= b[k]
=> b[k] - b[i] >= 0
```

Therefore:

```text
NEW - OLD <= 0
```

Hence:

```text
NEW <= OLD
```

So swapping a same-direction pair into an opposite-direction pair cannot increase the dot product.

Repeatedly perform these exchanges until:

```text
A: ascending
B: descending
```

### Numeric example

```text
a[i] = 2
a[k] = 5
b[i] = 3
b[k] = 8
```

Old:

```text
2*3 + 5*8
= 46
```

New:

```text
2*8 + 5*3
= 31
```

Formula:

```text
NEW - OLD
= (2 - 5)(8 - 3)
= (-3)(5)
= -15
```

Therefore:

```text
31 = 46 - 15
```


## Problem

Two arrays of `N` elements are given.

You may rearrange the second array.

Find:

```text
sum(a[i] * b[i])
```

such that the sum is minimum.

Lecture example:

```text
a = [1, 2, 3, 4, 5]
b = [4, 5, 1, 3, 1]
```

The first array is already non-decreasing.

## Greedy observation

For minimum dot product:

```text
A ascending
B descending
```

Pair:

```text
smallest with largest
largest with smallest
```

Sort B:

```text
B = [5, 4, 3, 1, 1]
```

Then:

```text
A: 1   2   3   4   5
   |   |   |   |   |
B: 5   4   3   1   1
```

Dot product:

```text
1*5 = 5
2*4 = 8
3*3 = 9
4*1 = 4
5*1 = 5

Total = 31
```

## Real-world mapping

Suppose:

```text
A = quantities
B = prices
```

To minimize total cost:

```text
large quantity -> cheap price
small quantity -> expensive price
```

Avoid:

```text
large quantity * expensive price
```

## Simple two-number example

```text
A = [2, 10]
B = [3, 8]
```

Same direction:

```text
2*3 + 10*8
= 6 + 80
= 86
```

Opposite direction:

```text
2*8 + 10*3
= 16 + 30
= 46
```

Opposite pairing is smaller.

## Exchange argument proof

Suppose:

```text
a[i] <= a[k]
b[i] <= b[k]
```

This is the bad same-direction pairing:

```text
a[i] with b[i]    small * small
a[k] with b[k]    big   * big
```

Old contribution:

```text
OLD = a[i] * b[i] + a[k] * b[k]
```

Swap `b[i]` and `b[k]`:

```text
a[i] with b[k]
a[k] with b[i]
```

New contribution:

```text
NEW = a[i] * b[k] + a[k] * b[i]
```

Now compare.

```text
NEW - OLD
```

Substitute:

```text
= a[i]b[k] + a[k]b[i]
  - (a[i]b[i] + a[k]b[k])
```

Remove brackets:

```text
= a[i]b[k]
+ a[k]b[i]
- a[i]b[i]
- a[k]b[k]
```

Group terms:

```text
= a[i]b[k] - a[i]b[i]
  +
  a[k]b[i] - a[k]b[k]
```

Factor:

```text
= a[i](b[k] - b[i])
  +
  a[k](b[i] - b[k])
```

Since:

```text
b[i] - b[k]
= -(b[k] - b[i])
```

we get:

```text
= a[i](b[k] - b[i])
  -
  a[k](b[k] - b[i])
```

Factor again:

```text
= (a[i] - a[k]) * (b[k] - b[i])
```

Now use the ordering:

```text
a[i] <= a[k]
```

therefore:

```text
a[i] - a[k] <= 0
```

Also:

```text
b[i] <= b[k]
```

therefore:

```text
b[k] - b[i] >= 0
```

So:

```text
negative-or-zero * positive-or-zero
<= 0
```

Hence:

```text
NEW - OLD <= 0
```

Therefore:

```text
NEW <= OLD
```

So swapping the wrongly paired values never increases the dot product.

Repeatedly fixing such pairs eventually gives:

```text
A ascending
B descending
```

which is optimal.

## Concrete algebra example

Take:

```text
a[i] = 2
a[k] = 5

b[i] = 3
b[k] = 8
```

Old:

```text
2*3 + 5*8
= 6 + 40
= 46
```

New:

```text
2*8 + 5*3
= 16 + 15
= 31
```

Using formula:

```text
NEW - OLD

= (2 - 5)(8 - 3)

= (-3)(5)

= -15
```

Therefore:

```text
NEW = OLD - 15
```

So:

```text
31 = 46 - 15
```

## C++ code

If `A` is already non-decreasing:

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {1, 2, 3, 4, 5};
    vector<int> b = {4, 5, 1, 3, 1};

    // A is already sorted ascending.
    sort(b.rbegin(), b.rend());

    long long ans = 0;

    for (int i = 0; i < (int)a.size(); i++) {
        ans += 1LL * a[i] * b[i];
    }

    cout << ans << '\n';
}
```

Output:

```text
31
```

Time:

```text
O(N log N)
```

---

# 8. Quick Pattern Recognition Table

| Problem | Greedy Choice |
|---|---|
| Pick 2 numbers with maximum sum | largest + second largest |
| Fixed sum, maximum product | make values as equal as possible |
| Maximum non-overlapping jobs | earliest finish time |
| Complete all jobs before deadlines | earliest deadline first |
| Job sequencing for max profit | highest profit first, place as late as possible |
| Fractional knapsack | highest value/weight first |
| Minimum dot product | one array ascending, the other descending |

---

# 9. Greedy Questions to Ask Before Coding

Whenever you think a problem may be greedy, ask:

```text
1. What am I optimizing?
   max / min

2. What local choice looks best?

3. If an optimal solution makes a different choice,
   can I replace its choice with mine?

4. After replacement:
   - is the solution still valid?
   - is the answer no worse?

5. Can the same logic be repeated?
```

For algebra-based greedy proofs, a useful target is:

For maximization:

```text
Alternative - Greedy <= 0
```

For minimization:

```text
New - Old <= 0
```

or equivalently prove the greedy arrangement cannot be improved by a swap.

---

# 10. Final Mental Summary

```text
Greedy = choose something locally best
         +
         prove that choice is safe
```

Common proof idea:

```text
OPTIMAL SOLUTION
       |
       | replace one choice
       v
GREEDY CHOICE
       |
       | still valid?
       | no worse?
       v
SAFE
```

The most important habit is:

> Do not stop at “this greedy choice looks right.”

Ask:

> Why can no alternative choice make the answer better?

That is the bridge from intuition to proof.
