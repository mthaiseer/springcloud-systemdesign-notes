# TLE Eliminators — Advanced Binary Search 3
## Problem Solving — Get Together
### Binary Search on Time + Intersection of Reachable Intervals

> **Source:** TLE Eliminators — **Advanced Binary Search 3 / Problem Solving 2**
>
> **Main problem:** **Get Together**
>
> We are given several people on a number line.  
> Person `i` starts at position `x[i]` and can move with speed `v[i]`.
>
> We need the **minimum time** in which all people can be at the **same coordinate**.
>
> The lecture's key transformation is:
>
> ```text
> minimum time
>      |
>      v
> binary search on time T
>      |
>      v
> for each person:
> reachable interval after T seconds
>      |
>      v
> do all reachable intervals intersect?
>      |
>      v
> monotonic predicate
> F F F F T T T T
> ```
>
> **Teaching flow**
>
> ```text
> What It Asks
> → Expected Result
> → Tiny Dry Run
> → Math Model
> → Reachable Interval
> → Intersection Condition
> → Checker(T)
> → Monotonicity
> → Binary Search
> → Detailed Dry Run
> → ASCII Visualization
> → C++17
> → Complexity
> → Recognition Model
> ```

---

# Clickable Table of Contents

- [0. Class Map](#0-class-map)
- [1. Prerequisites](#1-prerequisites)
  - [1.1 Distance = Speed × Time](#11-distance--speed--time)
  - [1.2 Reachable Interval of One Person](#12-reachable-interval-of-one-person)
  - [1.3 Intersection of Two Intervals](#13-intersection-of-two-intervals)
  - [1.4 Intersection of N Intervals](#14-intersection-of-n-intervals)
  - [1.5 Binary Search on Answer](#15-binary-search-on-answer)
  - [1.6 First True Pattern](#16-first-true-pattern)
  - [1.7 Decimal Binary Search](#17-decimal-binary-search)
  - [1.8 Why 60 Iterations Are Enough](#18-why-60-iterations-are-enough)
  - [1.9 Recognition Checklist](#19-recognition-checklist)
- [2. Problem — Get Together](#2-problem--get-together)
  - [2.1 What It Asks](#21-what-it-asks)
  - [2.2 What Result Is Expected](#22-what-result-is-expected)
  - [2.3 Tiny Dry Run First](#23-tiny-dry-run-first)
  - [2.4 Convert Time T Into Reachable Intervals](#24-convert-time-t-into-reachable-intervals)
  - [2.5 Mathematical Condition for All People to Meet](#25-mathematical-condition-for-all-people-to-meet)
  - [2.6 Checker(T)](#26-checkert)
  - [2.7 Why Checker(T) Is Monotonic](#27-why-checkert-is-monotonic)
  - [2.8 Lecture Dry Run — T = 2](#28-lecture-dry-run--t--2)
  - [2.9 Lecture Dry Run — T = 4](#29-lecture-dry-run--t--4)
  - [2.10 Binary Search Dry Run](#210-binary-search-dry-run)
  - [2.11 Algorithm](#211-algorithm)
  - [2.12 C++17 — Lecture Style](#212-c17--lecture-style)
  - [2.13 Complexity](#213-complexity)
  - [2.14 Recognition Model](#214-recognition-model)
- [3. Two Ways to Maintain Interval Intersection](#3-two-ways-to-maintain-interval-intersection)
  - [3.1 Running Intersection Pair](#31-running-intersection-pair)
  - [3.2 Max Left + Min Right](#32-max-left--min-right)
- [4. Common Mistakes](#4-common-mistakes)
- [5. Final Mental Model](#5-final-mental-model)
- [6. Source Note — Tracking Segments](#6-source-note--tracking-segments)

---

# 0. Class Map

```text
GET TOGETHER
│
├── Person i
│   ├── start position = x[i]
│   └── speed          = v[i]
│
├── Guess time T
│   │
│   └── reachable interval:
│       [x[i] - v[i]T , x[i] + v[i]T]
│
├── Can everybody meet?
│   │
│   └── Do all reachable intervals intersect?
│
├── Predicate:
│
│   checker(T)
│   =
│   True if common intersection exists
│
└── Monotonic:
    F F F F T T T T
            ^
            |
        first True
```

---

# 1. Prerequisites

## 1.1 Distance = Speed × Time

The basic motion formula is:

```text
distance
=
speed × time
```

If a person can move with speed:

```text
v
```

for:

```text
T seconds
```

then maximum distance they can travel is:

```text
vT
```

### Tiny Dry Run

Suppose:

```text
speed = 3
time  = 4
```

Then:

```text
distance
=
3 × 4
=
12
```

So in four seconds the person can move at most:

```text
12 units
```

left or right.

ASCII:

```text
          start
            |
            v
------------●------------
        <- 12  12 ->

speed = 3
time  = 4
```

---

## 1.2 Reachable Interval of One Person

Suppose a person starts at:

```text
x
```

and has speed:

```text
v
```

After time:

```text
T
```

maximum movement distance is:

```text
vT
```

Therefore the person can be anywhere from:

```text
x - vT
```

to:

```text
x + vT
```

So their reachable interval is:

```text
[x - vT , x + vT]
```

### Tiny Dry Run

Suppose:

```text
x = 5
v = 2
T = 3
```

Distance:

```text
vT
=
2 × 3
=
6
```

Leftmost reachable coordinate:

```text
5 - 6
=
-1
```

Rightmost reachable coordinate:

```text
5 + 6
=
11
```

So:

```text
reachable interval
=
[-1, 11]
```

ASCII:

```text
-1-------------------5-------------------11
 |                    ●                    |
 <------ 6 ----------><------- 6 --------->

start = 5
```

This formula is the heart of the problem.

---

## 1.3 Intersection of Two Intervals

Suppose two intervals are:

```text
A = [L1, R1]
B = [L2, R2]
```

They intersect when:

```text
max(L1, L2)
<=
min(R1, R2)
```

### Dry Run — Intersecting

```text
A = [1, 7]
B = [4, 10]
```

Largest left endpoint:

```text
max(1,4)
=
4
```

Smallest right endpoint:

```text
min(7,10)
=
7
```

Check:

```text
4 <= 7
```

True.

Intersection:

```text
[4,7]
```

ASCII:

```text
A: [1----------------7]
B:       [4----------------10]

I:       [4------7]
```

---

### Dry Run — No Intersection

```text
A = [1,3]
B = [5,8]
```

Compute:

```text
max(1,5)
=
5
```

and:

```text
min(3,8)
=
3
```

Check:

```text
5 <= 3
```

False.

So:

```text
intersection = empty
```

ASCII:

```text
A: [1----3]

B:          [5------8]

no common point
```

---

## 1.4 Intersection of N Intervals

For intervals:

```text
[L1,R1]
[L2,R2]
...
[Ln,Rn]
```

all intervals have a common point exactly when:

```text
max(L1,L2,...,Ln)
<=
min(R1,R2,...,Rn)
```

Define:

```text
maximumLeft
=
max(all left endpoints)
```

and:

```text
minimumRight
=
min(all right endpoints)
```

Then:

```text
common intersection exists
iff
maximumLeft <= minimumRight
```

### Dry Run

Intervals:

```text
A = [-2, 8]
B = [ 1, 7]
C = [ 3,10]
```

Maximum left endpoint:

```text
max(-2,1,3)
=
3
```

Minimum right endpoint:

```text
min(8,7,10)
=
7
```

Therefore:

```text
3 <= 7
```

True.

Common intersection:

```text
[3,7]
```

ASCII:

```text
A: [-2--------------------8]
B:       [1------------7]
C:             [3----------------10]

I:             [3------7]
```

---

## 1.5 Binary Search on Answer

The problem asks:

```text
minimum time
```

We do not know that time directly.

So choose a candidate:

```text
T
```

and ask:

```text
Can everybody meet
within T seconds?
```

That gives a predicate:

```text
checker(T)
```

Then binary search searches over:

```text
possible time values
```

not over an array.

### Tiny Dry Run

Suppose:

```text
checker(2) = False
checker(4) = True
```

Then minimum time lies somewhere in:

```text
(2,4]
```

ASCII:

```text
0---------2----------------4---------------->

          F                T
          |----------------|
             answer here
```

---

## 1.6 First True Pattern

For this problem:

```text
small time
→ people cannot meet
→ False
```

After enough time:

```text
people can meet
→ True
```

And once they can meet at time `T`, any larger time also works.

Therefore:

```text
F F F F F T T T T T
          ^
          |
      first True
```

We want:

```text
minimum feasible T
```

so this is:

```text
FIRST TRUE
```

### Tiny Dry Run

Imagine:

```text
T:       0 1 2 3 4 5 6
checker: F F F T T T T
```

Expected result:

```text
3
```

---

## 1.7 Decimal Binary Search

Time can be a real value.

The minimum time may be:

```text
2.25
3.137
4.000001
...
```

So we use binary search over:

```text
double
```

or:

```text
long double
```

The lecture solution uses fixed iterations:

```text
60
```

and updates:

```text
if checker(mid):
    right = mid
else:
    left = mid
```

### Tiny Dry Run

Suppose:

```text
left  = 2
right = 4
```

Mid:

```text
mid = 3
```

If:

```text
checker(3) = True
```

minimum time is no larger than `3`.

So:

```text
right = 3
```

New range:

```text
[2,3]
```

If instead:

```text
checker(3) = False
```

then:

```text
left = 3
```

New range:

```text
[3,4]
```

---

## 1.8 Why 60 Iterations Are Enough

The lecture uses:

```text
right = 1e9
```

and approximately:

```text
60 iterations
```

Each iteration halves the interval.

Initial width:

```text
1e9
```

After 60 iterations:

```text
width
=
1e9 / 2^60
```

Now:

```text
2^60
≈
1.15 × 10^18
```

So:

```text
1e9 / 2^60
≈
8.7 × 10^-10
```

That is extremely small.

ASCII:

```text
1e9
↓ half
5e8
↓
2.5e8
↓
...
↓ 60 times
≈ 8.7e-10
```

So fixed-iteration binary search gives more than enough precision for ordinary contest tolerance such as:

```text
1e-6
```

---

## 1.9 Recognition Checklist

When you read a problem, ask:

```text
1. Is the answer numeric?

2. Are we minimizing / maximizing it?

3. If I guess answer T,
   can I check feasibility?

4. Does more time / capacity / distance
   only make feasibility easier?

5. Does each object produce
   an interval / range?

6. Does feasibility become
   intersection / union / overlap?

7. Is the answer real-valued?

8. Then:
   binary search on decimals?
```

For this problem:

```text
minimum time
+
reachable ranges
+
common meeting point
=
binary search on time
+
intersection of intervals
```

---

# 2. Problem — Get Together

## 2.1 What It Asks

There are:

```text
N people
```

on a number line.

For each person `i`:

```text
x[i]
=
starting coordinate
```

and:

```text
v[i]
=
speed
```

Each person may move left or right.

Find:

```text
the minimum time T
```

such that all people can be at:

```text
one same coordinate
```

at or before time `T`.

### Story Removed

The problem becomes:

```text
Given:
(x1,v1)
(x2,v2)
...
(xn,vn)

Find minimum T such that:

there exists a coordinate P

for every i:

|P - x[i]| / v[i]
<=
T
```

That equation is shown conceptually in the lecture.

---

## 2.2 What Result Is Expected

The output is:

```text
minimum feasible time
```

usually as a decimal approximation.

Not:

```text
meeting coordinate
```

Not:

```text
distance travelled
```

Not:

```text
which person arrives first
```

We only need:

```text
minimum time
```

### Tiny Example

Two people:

```text
Person A:
x = 0
v = 1

Person B:
x = 10
v = 1
```

They can move toward each other.

After:

```text
T = 5
```

A can reach:

```text
[-5,5]
```

B can reach:

```text
[5,15]
```

They meet at:

```text
5
```

Expected answer:

```text
5
```

ASCII:

```text
0--------------------5--------------------10
A -------------------><------------------- B

              meeting point
```

---

## 2.3 Tiny Dry Run First

Use the four-person values illustrated in the lecture:

```text
Person 1:
x = 1
v = 2

Person 2:
x = 6
v = 3

Person 3:
x = -2
v = 1

Person 4:
x = -7
v = 2
```

We will compare:

```text
T = 2
```

and:

```text
T = 4
```

The lecture diagrams show that:

```text
T = 2
→ impossible
```

while:

```text
T = 4
→ possible
```

Therefore the minimum answer lies somewhere between them.

---

## 2.4 Convert Time T Into Reachable Intervals

For person `i`:

```text
start = x[i]
speed = v[i]
```

After time `T`, maximum distance:

```text
v[i] * T
```

So:

```text
left[i]
=
x[i] - v[i]T
```

and:

```text
right[i]
=
x[i] + v[i]T
```

Therefore reachable interval:

```text
[x[i] - v[i]T,
 x[i] + v[i]T]
```

### Inline Example

Person:

```text
x = -2
v = 1
T = 4
```

Distance:

```text
1 × 4
=
4
```

Interval:

```text
[-2 - 4,
 -2 + 4]
```

So:

```text
[-6,2]
```

ASCII:

```text
-6--------------(-2)--------------2
 |                 ●                |
 <------ 4 -------><------ 4 ------->
```

---

## 2.5 Mathematical Condition for All People to Meet

At time `T`, person `i` can occupy any position inside:

```text
Li(T)
=
x[i] - v[i]T
```

to:

```text
Ri(T)
=
x[i] + v[i]T
```

All people can meet if there exists some coordinate:

```text
P
```

belonging to every reachable interval.

So we need:

```text
intersection of all intervals
!=
empty
```

For all intervals:

```text
[L1,R1]
[L2,R2]
...
[Ln,Rn]
```

common intersection exists iff:

```text
max(L1,L2,...,Ln)
<=
min(R1,R2,...,Rn)
```

Therefore:

```text
checker(T)
=
(
    maximum reachable left endpoint
    <=
    minimum reachable right endpoint
)
```

### Compact Formula

```text
max_i(
    x[i] - v[i]T
)
<=
min_i(
    x[i] + v[i]T
)
```

This is the entire mathematical model.

---

## 2.6 Checker(T)

The lecture implements the feasibility check by maintaining the current common intersection.

Start with a very large interval:

```text
current =
[-INF, +INF]
```

For each person:

```text
range =
[x[i] - v[i]T,
 x[i] + v[i]T]
```

Intersect:

```text
current.left
=
max(current.left, range.left)
```

```text
current.right
=
min(current.right, range.right)
```

If at any point:

```text
current.left
>
current.right
```

then:

```text
intersection is empty
```

Return:

```text
False
```

Otherwise after all people:

```text
True
```

### Checker Dry Run

Intervals arrive:

```text
[-15,1]
[-6,2]
[-7,9]
[-6,18]
```

Start:

```text
current =
[-INF,+INF]
```

After first:

```text
[-15,1]
```

After second:

```text
max(-15,-6) = -6
min(1,2)    = 1

current = [-6,1]
```

After third:

```text
max(-6,-7) = -6
min(1,9)   = 1

current = [-6,1]
```

After fourth:

```text
max(-6,-6) = -6
min(1,18)  = 1

current = [-6,1]
```

Non-empty.

So:

```text
checker(T) = True
```

---

## 2.7 Why Checker(T) Is Monotonic

Suppose everybody can meet in:

```text
T seconds
```

That means there exists a coordinate:

```text
P
```

such that each person can reach `P` within `T`.

Now give everybody more time:

```text
T' > T
```

Every person's reachable interval expands.

Why?

Original:

```text
[x - vT,
 x + vT]
```

New:

```text
[x - vT',
 x + vT']
```

Since:

```text
T' > T
```

we have:

```text
vT' > vT
```

So:

```text
left endpoint moves left
right endpoint moves right
```

ASCII:

```text
time T:

        [-------]

time T':

    [---------------]
```

Therefore any point reachable at time `T` is still reachable at time `T'`.

So:

```text
checker(T) = True
and
T' >= T

⇒

checker(T') = True
```

Hence:

```text
F F F F F T T T T
```

This proves binary search is valid.

---

## 2.8 Lecture Dry Run — T = 2

Use:

```text
Person 1:
x = 1
v = 2

Person 2:
x = 6
v = 3

Person 3:
x = -2
v = 1

Person 4:
x = -7
v = 2
```

For:

```text
T = 2
```

---

### Person 1

```text
x = 1
v = 2
```

Distance:

```text
vT
=
2 × 2
=
4
```

Range:

```text
[1-4,
 1+4]
=
[-3,5]
```

---

### Person 2

```text
x = 6
v = 3
```

Distance:

```text
3 × 2
=
6
```

Range:

```text
[6-6,
 6+6]
=
[0,12]
```

---

### Person 3

```text
x = -2
v = 1
```

Distance:

```text
1 × 2
=
2
```

Range:

```text
[-2-2,
 -2+2]
=
[-4,0]
```

---

### Person 4

```text
x = -7
v = 2
```

Distance:

```text
2 × 2
=
4
```

Range:

```text
[-7-4,
 -7+4]
=
[-11,-3]
```

---

### All Ranges

```text
P4: [-11----------- -3]
P3:        [-4----------0]
P1:          [-3----------------5]
P2:                [0---------------------12]
```

Compute:

```text
maximumLeft
=
max(
    -3,
     0,
    -4,
   -11
)
=
0
```

Minimum right:

```text
minimumRight
=
min(
     5,
    12,
     0,
    -3
)
=
-3
```

Check:

```text
0 <= -3
```

False.

Therefore:

```text
checker(2)
=
False
```

### Visual Reason

The far-left person can only reach up to:

```text
-3
```

while the person at `6` can only reach as far left as:

```text
0
```

There is a gap:

```text
-3 ----- 0
```

So no coordinate is reachable by everyone.

---

## 2.9 Lecture Dry Run — T = 4

Same people:

```text
(1,2)
(6,3)
(-2,1)
(-7,2)
```

Now:

```text
T = 4
```

---

### Person 1

```text
x = 1
v = 2

vT = 8
```

Range:

```text
[1-8,
 1+8]
=
[-7,9]
```

---

### Person 2

```text
x = 6
v = 3

vT = 12
```

Range:

```text
[6-12,
 6+12]
=
[-6,18]
```

---

### Person 3

```text
x = -2
v = 1

vT = 4
```

Range:

```text
[-2-4,
 -2+4]
=
[-6,2]
```

---

### Person 4

```text
x = -7
v = 2

vT = 8
```

Range:

```text
[-7-8,
 -7+8]
=
[-15,1]
```

---

### All Ranges

```text
P4: [-15------------------------------1]
P1:         [-7-----------------------------9]
P3:          [-6--------------2]
P2:          [-6----------------------------------------18]
```

Maximum left endpoint:

```text
max(
    -7,
    -6,
    -6,
   -15
)
=
-6
```

Minimum right endpoint:

```text
min(
     9,
    18,
     2,
     1
)
=
1
```

Check:

```text
-6 <= 1
```

True.

Common intersection:

```text
[-6,1]
```

So any coordinate inside:

```text
[-6,1]
```

can be the gathering point.

Therefore:

```text
checker(4)
=
True
```

ASCII:

```text
                    common region
                         ↓

P4: [-15-------------[-6========1]
P1:         [-7------[-6========1]--------9]
P3:          [-6-----[-6========1]-2]
P2:          [-6-----[-6========1]----------------18]

                    intersection
                       [-6,1]
```

---

## 2.10 Binary Search Dry Run

We now know:

```text
checker(2)
=
False
```

and:

```text
checker(4)
=
True
```

So minimum time lies in:

```text
[2,4]
```

For understanding, binary search only this interval.

---

### Iteration 1

```text
left  = 2
right = 4
```

Mid:

```text
mid
=
(2+4)/2
=
3
```

Suppose:

```text
checker(3)
=
True
```

Then minimum time is:

```text
<= 3
```

So:

```text
right = 3
```

New range:

```text
[2,3]
```

---

### Iteration 2

```text
mid
=
(2+3)/2
=
2.5
```

If:

```text
checker(2.5)
=
False
```

then minimum time is:

```text
> 2.5
```

So:

```text
left = 2.5
```

New range:

```text
[2.5,3]
```

---

### Iteration 3

```text
mid
=
(2.5+3)/2
=
2.75
```

Again:

```text
checker(mid)
```

tells us which half to keep.

The interval keeps shrinking:

```text
[2,4]
→ [2,3]
→ [2.5,3]
→ [2.5,2.75]
→ ...
```

Eventually:

```text
left
and
right
```

become almost equal.

That boundary is the minimum meeting time.

---

## 2.11 Algorithm

### Step 1 — Read People

Store:

```text
(x[i], v[i])
```

---

### Step 2 — Write `checker(T)`

For each person:

```text
reachableLeft
=
x[i] - v[i]T

reachableRight
=
x[i] + v[i]T
```

Maintain:

```text
commonLeft
=
max(all reachableLeft)
```

and:

```text
commonRight
=
min(all reachableRight)
```

If:

```text
commonLeft > commonRight
```

return:

```text
False
```

Else:

```text
True
```

---

### Step 3 — Binary Search Time

Lecture bounds:

```text
left  = 0
right = 1e9
```

Repeat:

```text
60 times
```

Mid:

```text
mid
=
(left + right) / 2
```

If:

```text
checker(mid)
=
True
```

we can try a smaller time:

```text
right = mid
```

Else:

```text
left = mid
```

---

### Step 4 — Output

After enough iterations:

```text
right
```

is approximately the minimum feasible time.

---

## 2.12 C++17 — Lecture Style

```cpp
#include <bits/stdc++.h>
using namespace std;

bool checker(
    double time,
    const vector<pair<double, double>>& people
) {
    pair<double, double> current = {
        -1e18,
         1e18
    };

    for (auto person : people) {
        double position = person.first;
        double speed = person.second;

        pair<double, double> reachable = {
            position - speed * time,
            position + speed * time
        };

        current.first =
            max(
                current.first,
                reachable.first
            );

        current.second =
            min(
                current.second,
                reachable.second
            );

        if (current.first > current.second)
            return false;
    }

    return true;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<pair<double, double>> people(n);

    for (int i = 0; i < n; ++i) {
        cin >> people[i].first
            >> people[i].second;
    }

    double left = 0.0;
    double right = 1e9;

    for (int iteration = 0;
         iteration < 60;
         ++iteration) {

        double mid =
            (left + right) / 2.0;

        if (checker(mid, people)) {
            right = mid;
        }
        else {
            left = mid;
        }
    }

    cout << fixed
         << setprecision(7)
         << right
         << '\n';

    return 0;
}
```

---

### Code Flow

```text
checker(mid)
      |
      v

build reachable interval
for each person

      |
      v

intersect all intervals

      |
  +---+---+
  |       |
empty?   non-empty?
  |       |
  v       v
False    True

  |       |
  v       v

left     right
= mid    = mid
```

---

### Code Dry Run

Suppose:

```text
mid = 2
```

Checker computes:

```text
[-3,5]
[0,12]
[-4,0]
[-11,-3]
```

Running intersection:

```text
start:
[-INF,+INF]

after [-3,5]:
[-3,5]

after [0,12]:
[0,5]

after [-4,0]:
[0,0]

after [-11,-3]:

left
=
max(0,-11)
=
0

right
=
min(0,-3)
=
-3
```

Now:

```text
0 > -3
```

So:

```text
False
```

Binary search does:

```text
left = mid
```

---

## 2.13 Complexity

Let:

```text
N = number of people
I = binary-search iterations
```

Each `checker(T)` scans all people:

```text
O(N)
```

Binary search runs:

```text
I = 60
```

iterations.

Total:

```text
O(N * 60)
```

Since `60` is constant:

```text
O(N)
```

for practical asymptotic discussion.

More generally:

```text
O(
    N * log(searchRange / precision)
)
```

Space:

```text
O(N)
```

for storing people.

The checker itself uses:

```text
O(1)
```

extra space.

---

## 2.14 Recognition Model

If the problem says:

```text
N moving objects
+
position
+
speed
+
minimum time
+
all must satisfy a common condition
```

ask:

```text
At time T,
what region can each object reach?
```

If that becomes:

```text
interval i
```

then ask:

```text
Do all intervals intersect?
```

If:

```text
more time only expands intervals
```

then:

```text
feasibility is monotonic
```

Therefore:

```text
Binary Search on Time
+
Interval Intersection
```

---

# 3. Two Ways to Maintain Interval Intersection

## 3.1 Running Intersection Pair

This matches the lecture code.

Start:

```text
current =
[-INF,+INF]
```

For each new interval:

```text
[L,R]
```

update:

```text
current.left
=
max(current.left, L)
```

```text
current.right
=
min(current.right, R)
```

If:

```text
current.left
>
current.right
```

stop:

```text
no intersection
```

### Dry Run

```text
current = [-INF,+INF]

A = [-5,8]
→ [-5,8]

B = [-2,6]
→ [-2,6]

C = [1,10]
→ [1,6]
```

Final:

```text
[1,6]
```

---

## 3.2 Max Left + Min Right

Equivalent simpler mental model:

```text
maximumLeft = -INF
minimumRight = +INF
```

For each interval:

```text
maximumLeft =
max(maximumLeft, L)
```

```text
minimumRight =
min(minimumRight, R)
```

At the end:

```text
maximumLeft <= minimumRight
```

means:

```text
common intersection exists
```

### Dry Run

Intervals:

```text
[-7,9]
[-6,18]
[-6,2]
[-15,1]
```

Maximum left:

```text
max(
    -7,
    -6,
    -6,
   -15
)
=
-6
```

Minimum right:

```text
min(
     9,
    18,
     2,
     1
)
=
1
```

So:

```text
[-6,1]
```

is the common intersection.

Both implementations are mathematically identical.

---

# 4. Common Mistakes

## Mistake 1 — Binary Search on Meeting Coordinate

The problem asks for:

```text
minimum time
```

So the natural answer-space variable is:

```text
T
```

not the meeting coordinate.

Meeting coordinate is handled indirectly through:

```text
intersection of reachable intervals
```

---

## Mistake 2 — Checking Pairwise Intersections Only

It is not enough to say:

```text
A intersects B
B intersects C
```

therefore:

```text
A, B, C all intersect
```

That implication is not generally safe.

Instead compute:

```text
max(all left endpoints)
<=
min(all right endpoints)
```

---

## Mistake 3 — Using Distance to One Fixed Coordinate

The gathering coordinate is not given.

It can be anywhere.

So do not test only:

```text
0
```

or:

```text
average position
```

or:

```text
median position
```

The interval-intersection check automatically considers every possible meeting coordinate.

---

## Mistake 4 — Forgetting Speed × Time

Reachable distance is:

```text
v[i] * T
```

not:

```text
v[i] + T
```

and not:

```text
T / v[i]
```

Reachable interval:

```text
[x[i] - v[i]T,
 x[i] + v[i]T]
```

---

## Mistake 5 — Wrong Binary Search Direction

Predicate shape:

```text
F F F F T T T
```

We need:

```text
FIRST TRUE
```

So:

```text
True
→ answer may be smaller
→ move right boundary left

False
→ need more time
→ move left boundary right
```

Lecture code:

```cpp
if (checker(mid, people))
    right = mid;
else
    left = mid;
```

---

## Mistake 6 — Integer Binary Search Updates

Time is decimal.

Do not use:

```cpp
left = mid + 1;
right = mid - 1;
```

Use real-space narrowing:

```cpp
left = mid;
right = mid;
```

with fixed iterations.

---

## Mistake 7 — Too Few Iterations

A large range such as:

```text
[0,1e9]
```

needs enough halvings.

The lecture uses:

```text
60
```

which shrinks the interval to roughly:

```text
1e-9
```

width.

---

# 5. Final Mental Model

```text
                GET TOGETHER
                     |
                     v
              Guess time T
                     |
                     v
       Person i can reach distance v[i]T
                     |
                     v
         reachable interval of person i
                     |
                     v
       [x[i]-v[i]T , x[i]+v[i]T]
                     |
                     v
          intersect all intervals
                     |
              +------+------+
              |             |
           empty         non-empty
              |             |
              v             v
            False          True
              |             |
              +------+------+
                     |
                     v
           F F F F T T T T
                   ^
                   |
               first True
                     |
                     v
         binary search minimum time
```

## Core Math

```text
Person i:

distance in T seconds
=
v[i]T
```

So:

```text
reachable range
=
[x[i]-v[i]T,
 x[i]+v[i]T]
```

All can meet iff:

```text
max_i(
    x[i]-v[i]T
)
<=
min_i(
    x[i]+v[i]T
)
```

That single inequality is the mathematical heart of the problem.

---

## 60-Second Recognition

```text
MINIMUM TIME?
     |
     v
GUESS T
     |
     v
WHAT CAN EACH OBJECT REACH?
     |
     v
INTERVAL
     |
     v
COMMON POINT NEEDED?
     |
     v
INTERSECTION
     |
     v
MORE TIME ONLY HELPS?
     |
     v
MONOTONIC
     |
     v
BINARY SEARCH
```

---

# 6. Source Note — Tracking Segments

The final page of the supplied lecture introduces:

```text
Problem 2:
Tracking Segments

[CP-31 1600: P5]
```

but the supplied PDF ends at that title slide.

Therefore this note does **not** invent or reconstruct the solution for Tracking Segments from outside the lecture.

It should be covered in a separate note once the corresponding lecture/problem material is supplied.
