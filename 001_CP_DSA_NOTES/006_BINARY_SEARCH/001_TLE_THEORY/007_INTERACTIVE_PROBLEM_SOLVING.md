# TLE Eliminators — Interactive Problems: Problem Solving
## Binary Search & Interactive Problems — Level 3
### Searching Local Minimum + Find a Mine + Guessing the Greatest
### TLE-Style Self-Study Notes

> **Source:** supplied TLE Eliminators **Interactive Problems — Problem Solving Session** PDF plus the supplied C++ solutions.
>
> **Problems covered**
>
> ```text
> 1. Searching Local Minimum
>    → binary search on local slope
>
> 2. Find a Mine
>    → Manhattan distance + diagonal geometry
>
> 3. Guessing the Greatest (Hard Version)
>    → second-maximum query + binary search
> ```
>
> **Core lesson**
>
> ```text
> interactive problem
>      |
>      v
> understand exactly what one query reveals
>      |
>      v
> convert reply into structural information
>      |
>      v
> discard possibilities aggressively
>      |
>      v
> stay inside query limit
> ```
>
> The code blocks use the same compact formatting style as the previous Binary Search notes.

---

# Clickable Table of Contents

- [0. Class Map](#0-class-map)
- [1. Prerequisites](#1-prerequisites)
  - [1.1 Interactive Query Model](#11-interactive-query-model)
  - [1.2 Flush After Every Query](#12-flush-after-every-query)
  - [1.3 Query Budget](#13-query-budget)
  - [1.4 Binary Search Without a Sorted Array](#14-binary-search-without-a-sorted-array)
  - [1.5 Local Minimum](#15-local-minimum)
  - [1.6 Sentinel Infinity at Boundaries](#16-sentinel-infinity-at-boundaries)
  - [1.7 Why a Downhill Direction Contains a Local Minimum](#17-why-a-downhill-direction-contains-a-local-minimum)
  - [1.8 Manhattan Distance](#18-manhattan-distance)
  - [1.9 Manhattan-Distance Diagonal](#19-manhattan-distance-diagonal)
  - [1.10 Moving Along `x + y = constant`](#110-moving-along-x--y--constant)
  - [1.11 Second Maximum Query](#111-second-maximum-query)
  - [1.12 Turning Second-Maximum Information Into a Monotonic Predicate](#112-turning-second-maximum-information-into-a-monotonic-predicate)
  - [1.13 Interactive Recognition Checklist](#113-interactive-recognition-checklist)
- [2. Problem 1 — Searching Local Minimum](#2-problem-1--searching-local-minimum)
  - [2.1 Problem Link](#21-problem-link)
  - [2.2 What It Asks](#22-what-it-asks)
  - [2.3 What Result Is Expected](#23-what-result-is-expected)
  - [2.4 First Observation — Permutation Means No Equal Neighbors](#24-first-observation--permutation-means-no-equal-neighbors)
  - [2.5 What to Query at `mid`](#25-what-to-query-at-mid)
  - [2.6 Three Cases at `mid`](#26-three-cases-at-mid)
  - [2.7 Why We Can Discard Half](#27-why-we-can-discard-half)
  - [2.8 Detailed Dry Run](#28-detailed-dry-run)
  - [2.9 Query Count](#29-query-count)
  - [2.10 Algorithm](#210-algorithm)
  - [2.11 C++17](#211-c17)
  - [2.12 Recognition Model](#212-recognition-model)
- [3. Problem 2 — Find a Mine](#3-problem-2--find-a-mine)
  - [3.1 Problem Link](#31-problem-link)
  - [3.2 What It Asks](#32-what-it-asks)
  - [3.3 What One Query Returns](#33-what-one-query-returns)
  - [3.4 First Query From `(1,1)`](#34-first-query-from-11)
  - [3.5 Why a Mine Lies on `x + y = d + 2`](#35-why-a-mine-lies-on-x--y--d--2)
  - [3.6 Find the Two Grid Endpoints of That Diagonal](#36-find-the-two-grid-endpoints-of-that-diagonal)
  - [3.7 Why Distance Along the Diagonal Changes by 2](#37-why-distance-along-the-diagonal-changes-by-2)
  - [3.8 Build Two Candidate Mine Positions](#38-build-two-candidate-mine-positions)
  - [3.9 Why One Verification Query Is Enough](#39-why-one-verification-query-is-enough)
  - [3.10 Detailed Dry Run](#310-detailed-dry-run)
  - [3.11 Query Count](#311-query-count)
  - [3.12 Algorithm](#312-algorithm)
  - [3.13 C++17](#313-c17)
  - [3.14 Recognition Model](#314-recognition-model)
- [4. Problem 3 — Guessing the Greatest (Hard Version)](#4-problem-3--guessing-the-greatest-hard-version)
  - [4.1 Problem Link](#41-problem-link)
  - [4.2 What It Asks](#42-what-it-asks)
  - [4.3 What the Query Returns](#43-what-the-query-returns)
  - [4.4 First Query — Global Second Maximum](#44-first-query--global-second-maximum)
  - [4.5 Determine Whether the Maximum Is Left or Right](#45-determine-whether-the-maximum-is-left-or-right)
  - [4.6 Binary Search When Maximum Is Left](#46-binary-search-when-maximum-is-left)
  - [4.7 Binary Search When Maximum Is Right](#47-binary-search-when-maximum-is-right)
  - [4.8 Detailed Dry Run — Maximum on the Left](#48-detailed-dry-run--maximum-on-the-left)
  - [4.9 Detailed Dry Run — Maximum on the Right](#49-detailed-dry-run--maximum-on-the-right)
  - [4.10 Query Count](#410-query-count)
  - [4.11 Algorithm](#411-algorithm)
  - [4.12 C++17](#412-c17)
  - [4.13 Recognition Model](#413-recognition-model)
- [5. Compare the Three Problems](#5-compare-the-three-problems)
- [6. Common Mistakes](#6-common-mistakes)
- [7. Final Mental Model](#7-final-mental-model)

---

# 0. Class Map

```text
INTERACTIVE PROBLEM SOLVING
│
├── Searching Local Minimum
│   ├── query mid-1, mid, mid+1
│   ├── inspect local slope
│   ├── one half must contain a local minimum
│   └── Binary Search
│
├── Find a Mine
│   ├── query Manhattan distance
│   ├── first query creates diagonal x+y=constant
│   ├── query diagonal endpoints
│   ├── convert distance to coordinate shift
│   └── Geometry / Casework
│
└── Guessing the Greatest
    ├── query gives second-maximum index
    ├── full query reveals global second maximum
    ├── determine side containing maximum
    ├── build monotonic predicate
    └── Binary Search
```

---

# 1. Prerequisites

## 1.1 Interactive Query Model

In an interactive problem, your program repeatedly communicates with the judge.

```text
YOUR PROGRAM
    |
    | query
    v
INTERACTOR
    |
    | reply
    v
YOUR PROGRAM
```

The reply becomes information for the next decision.

### Tiny Dry Run

Suppose a hidden value is `73`.

```text
? 50
<
```

If `<` means:

```text
50 < hidden
```

then your program has learned:

```text
hidden > 50
```

The important question is always:

```text
What uncertainty did this query remove?
```

---

## 1.2 Flush After Every Query

The judge cannot reply until it receives your query.

So after printing a query, flush the output.

Typical lecture-style code:

```cpp
cout << "? " << x << endl;
```

`endl` does:

```text
newline + flush
```

Equivalent:

```cpp
cout << "? " << x << '\n' << flush;
```

Without flushing:

```text
your program waits for reply
judge waits for query
```

---

## 1.3 Query Budget

Interactive problems impose a maximum number of questions.

So before coding, estimate:

```text
queries used by algorithm
<=
queries allowed
```

Example:

```text
N <= 100000
```

Binary-search depth:

```text
ceil(log2(100000)) ≈ 17
```

If one binary-search iteration asks at most three values:

```text
3 × 17 ≈ 51
```

which is below a `100`-query limit.

---

## 1.4 Binary Search Without a Sorted Array

Binary search does **not** require the input array itself to be sorted.

It requires:

```text
from information at mid,
can we prove one whole side
can be discarded?
```

That happens in:

```text
Searching Local Minimum
Guessing the Greatest
```

The values are not sorted, but the query creates a directional decision.

---

## 1.5 Local Minimum

Index `i` is a local minimum when:

```text
a[i] < a[i-1]
and
a[i] < a[i+1]
```

The problem uses conceptual boundaries:

```text
a[0] = +INF
a[n+1] = +INF
```

### Tiny Dry Run

```text
A = [7,3,5]
```

At index `2`:

```text
3 < 7
3 < 5
```

Therefore index `2` is a local minimum.

ASCII:

```text
7       5
 \     /
  \   /
    3
    ^
 local min
```

---

## 1.6 Sentinel Infinity at Boundaries

The supplied code avoids querying outside the array by using:

```text
INT_MAX
```

For `mid = 1`:

```text
left neighbor = +INF
```

For `mid = n`:

```text
right neighbor = +INF
```

### Tiny Dry Run

```text
A = [2,5,8]
```

At index `1`:

```text
left  = +INF
mid   = 2
right = 5
```

So:

```text
2 < INF
2 < 5
```

Index `1` is a valid local minimum.

---

## 1.7 Why a Downhill Direction Contains a Local Minimum

Suppose:

```text
a[mid-1] < a[mid]
```

Moving one step left goes downhill.

Continue conceptually following smaller values.

Eventually:

```text
1. descent stops
   → position is smaller than both neighbors
   → local minimum

or

2. descent reaches boundary
   → outside value is +INF
   → boundary position is local minimum
```

Therefore:

```text
left neighbor smaller
→ some local minimum exists on left
```

Symmetrically:

```text
right neighbor smaller
→ some local minimum exists on right
```

---

## 1.8 Manhattan Distance

For cells:

```text
(x1,y1)
(x2,y2)
```

Manhattan distance:

```text
|x1-x2| + |y1-y2|
```

### Tiny Dry Run

```text
A = (1,1)
B = (4,3)
```

```text
|4-1| + |3-1| = 3 + 2 = 5
```

---

## 1.9 Manhattan-Distance Diagonal

From `(1,1)` to `(x,y)`:

```text
d = |x-1| + |y-1|
```

Since:

```text
x >= 1
y >= 1
```

```text
d = (x-1) + (y-1)
```

So:

```text
d = x + y - 2
```

Hence:

```text
x + y = d + 2
```

A query at `(1,1)` therefore identifies an anti-diagonal containing at least one nearest mine.

---

## 1.10 Moving Along `x + y = constant`

On:

```text
x + y = S
```

one diagonal step is:

```text
(x,y) → (x+1,y-1)
```

Its Manhattan length is:

```text
|1| + |-1| = 2
```

Therefore:

```text
diagonal steps = Manhattan distance / 2
```

This explains:

```text
x += d/2
y -= d/2
```

and the symmetric movement from the other endpoint.

---

## 1.11 Second Maximum Query

In Problem 3:

```text
ask(l,r)
```

returns:

```text
index of the second largest value in a[l...r]
```

### Tiny Dry Run

```text
A = [10,1,2,3,5,8,7,6]
```

Full range:

```text
maximum = 10 at index 1
second maximum = 8 at index 6
```

So:

```text
ask(1,8) = 6
```

---

## 1.12 Turning Second-Maximum Information Into a Monotonic Predicate

Let:

```text
S = global second maximum index
P = global maximum index
```

Suppose:

```text
P < S
```

For:

```text
ask(mid,S)
```

if:

```text
mid <= P
```

the range contains both global maximum and global second maximum, so:

```text
ask(mid,S) = S
```

If:

```text
mid > P
```

the maximum is excluded, so:

```text
ask(mid,S) != S
```

Predicate:

```text
T T T T F F F
      ^
      |
      P
```

So `P` is the last True.

The right-side case is the symmetric first-True pattern.

---

## 1.13 Interactive Recognition Checklist

```text
1. What exactly does one query return?

2. Can that reply eliminate a whole region?

3. Is there a monotonic predicate hidden in the reply?

4. Is there geometry in the returned distance?

5. Is there a global anchor:
   max / second max / nearest point?

6. What boundary convention is needed?

7. How many queries does one iteration use?

8. Is the total below the limit?

9. Did I flush every query?
```

---

# 2. Problem 1 — Searching Local Minimum

## 2.1 Problem Link

[Codeforces 1479A — Searching Local Minimum](https://codeforces.com/contest/1479/problem/A)

---

## 2.2 What It Asks

A hidden permutation contains distinct values.

You may query an index and receive its value.

Find `i` such that:

```text
a[i] < a[i-1]
and
a[i] < a[i+1]
```

with conceptual boundaries:

```text
a[0] = +INF
a[n+1] = +INF
```

The lecture highlights the `100`-query limit.

---

## 2.3 What Result Is Expected

Example hidden array:

```text
[3,2,6,4,5]
```

Index `2`:

```text
2 < 3
2 < 6
```

So a valid answer is:

```text
! 2
```

We do not need the global minimum.

Any local minimum is enough.

---

## 2.4 First Observation — Permutation Means No Equal Neighbors

Because the array is a permutation:

```text
all values are distinct
```

So:

```text
a[i] < a[i+1]
```

or:

```text
a[i] > a[i+1]
```

There is no equality case.

---

## 2.5 What to Query at `mid`

The supplied solution queries:

```text
mid-1
mid
mid+1
```

and stores:

```text
val[0] = a[mid-1]
val[1] = a[mid]
val[2] = a[mid+1]
```

Boundary handling:

```text
outside array → INF
```

ASCII:

```text
         mid
          |
          v
... [mid-1] [mid] [mid+1] ...
      val0    val1   val2
```

---

## 2.6 Three Cases at `mid`

### Case 1 — `mid` Is Local Minimum

```text
val[1] < val[0]
and
val[1] < val[2]
```

Then:

```text
answer = mid
```

### Case 2 — Left Neighbor Is Smaller

Supplied code checks:

```text
val[1] < val[2]
and
val[1] > val[0]
```

So:

```text
val[0] < val[1] < val[2]
```

A local minimum exists on the left.

Move:

```text
high = mid - 1
```

### Case 3 — Otherwise Search Right

The remaining non-local-minimum configurations imply a smaller neighbor on the right.

Move:

```text
low = mid + 1
```

---

## 2.7 Why We Can Discard Half

If:

```text
a[mid-1] < a[mid]
```

the left side has a downhill direction.

Following decreasing values must eventually bottom out at a local minimum, or reach index `1` where the outside neighbor is `+INF`.

Therefore:

```text
left neighbor smaller
→ local minimum exists on left
```

Symmetrically for the right.

That makes binary-search elimination valid even though the array is not sorted.

---

## 2.8 Detailed Dry Run

Use:

```text
A = [9,7,3,5,8,6,10]
```

Start:

```text
low = 1
high = 7
```

### Iteration 1

```text
mid = 4
```

Values:

```text
a[3] = 3
a[4] = 5
a[5] = 8
```

So:

```text
3 < 5 < 8
```

Left neighbor is smaller.

```text
high = 3
```

### Iteration 2

```text
mid = 2
```

Values:

```text
9,7,3
```

Right neighbor is smaller.

```text
low = 3
```

### Iteration 3

```text
mid = 3
```

Values:

```text
7,3,5
```

```text
3 < 7
3 < 5
```

Found:

```text
! 3
```

ASCII:

```text
9   7   3   5   8   6   10
        ^
        |
      local min
```

---

## 2.9 Query Count

For:

```text
n <= 100000
```

binary-search depth is about:

```text
17
```

The supplied `ask(mid,n)` may issue up to three queries per iteration:

```text
3 × 17 ≈ 51
```

which fits below the `100`-query limit shown in the lecture.

---

## 2.10 Algorithm

```text
low = 1
high = n

while low <= high:

    mid = (low + high) / 2

    query mid-1, mid, mid+1
    use INF outside array

    if mid smaller than both:
        answer = mid
        break

    else if left neighbor smaller:
        high = mid - 1

    else:
        low = mid + 1

print answer
```

---

## 2.11 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

array<int, 3> ask(int ind, int n) {
    array<int, 3> ans;

    if (ind - 1 > 0) {
        cout << "? " << ind - 1 << endl;
        cin >> ans[0];
    } else {
        ans[0] = INT_MAX;
    }

    cout << "? " << ind << endl;
    cin >> ans[1];

    if (ind + 1 <= n) {
        cout << "? " << ind + 1 << endl;
        cin >> ans[2];
    } else {
        ans[2] = INT_MAX;
    }

    return ans;
}

void tell(int ind) {
    cout << "! " << ind << endl;
}

int main() {
    int n;
    cin >> n;

    int low = 1, high = n;
    int ans = -1;

    while (low <= high) {
        int mid = (low + high) / 2;
        array<int, 3> val = ask(mid, n);

        if (val[1] < val[2] && val[1] < val[0]) {
            ans = mid;
            break;
        } else if (val[1] < val[2] && val[1] > val[0]) {
            high = mid - 1;
        } else {
            low = mid + 1;
        }
    }

    tell(ans);
}
```

---

## 2.12 Recognition Model

```text
Need local minimum
+
mid compared with neighbors
+
smaller neighbor tells a side
+
that side must contain a local minimum
=
Binary Search on Local Slope
```

---

# 3. Problem 2 — Find a Mine

## 3.1 Problem Link

[Codeforces 1934C — Find a Mine](https://codeforces.com/contest/1934/problem/C)

---

## 3.2 What It Asks

An `n × m` grid contains exactly two mines.

A query at:

```text
(x,y)
```

returns the minimum Manhattan distance from that cell to either mine.

Use at most four queries to find one mine.

---

## 3.3 What One Query Returns

If mines are:

```text
M1 = (x1,y1)
M2 = (x2,y2)
```

then:

```text
ask(x,y)
=
min(
    |x-x1| + |y-y1|,
    |x-x2| + |y-y2|
)
```

The reply gives distance to the nearer mine, not its identity.

---

## 3.4 First Query From `(1,1)`

The supplied solution begins:

```cpp
int a = ask(1, 1);
```

Let reply be:

```text
a
```

At least one nearest mine satisfies:

```text
(x-1) + (y-1) = a
```

Therefore:

```text
x + y = a + 2
```

So one query converts the 2D search into one anti-diagonal.

---

## 3.5 Why a Mine Lies on `x + y = d + 2`

From `(1,1)`:

```text
d = |x-1| + |y-1|
```

Valid grid coordinates satisfy:

```text
x >= 1
y >= 1
```

So:

```text
d = x-1 + y-1
```

Thus:

```text
d = x+y-2
```

Rearrange:

```text
x+y = d+2
```

---

## 3.6 Find the Two Grid Endpoints of That Diagonal

Let:

```text
S = a + 2
```

Need:

```text
x+y=S
1<=x<=n
1<=y<=m
```

The code builds two extreme valid points.

Endpoint 1:

```text
xd1 = max(1, S-m)
yd1 = S-xd1
```

Endpoint 2:

```text
yd2 = max(1, S-n)
xd2 = S-yd2
```

ASCII:

```text
E1 *
    \
     \
      \  x+y=S
       \
        * E2
```

---

## 3.7 Why Distance Along the Diagonal Changes by 2

One step along the same diagonal:

```text
(x,y) → (x+1,y-1)
```

has Manhattan length:

```text
|1| + |-1| = 2
```

Therefore if endpoint-to-mine distance is:

```text
d1
```

number of diagonal steps is:

```text
d1/2
```

This explains:

```text
x += d/2
y -= d/2
```

and the symmetric formula from the other endpoint.

---

## 3.8 Build Two Candidate Mine Positions

Query both endpoints:

```text
ad1 = ask(xd1,yd1)
ad2 = ask(xd2,yd2)
```

If:

```text
ad1 == 0
```

then endpoint 1 is a mine.

If:

```text
ad2 == 0
```

then endpoint 2 is a mine.

Otherwise:

```text
xp1 = xd1 + ad1/2
yp1 = yd1 - ad1/2
```

and:

```text
xp2 = xd2 - ad2/2
yp2 = yd2 + ad2/2
```

These are the two candidates from the supplied solution.

---

## 3.9 Why One Verification Query Is Enough

Query candidate 1:

```text
ask(xp1,yp1)
```

If reply is:

```text
0
```

candidate 1 is a mine.

Otherwise output candidate 2.

The lecture solution is constructed so at least one of these two candidates is a mine.

The extra verification handles ambiguity caused by the fact that each endpoint query returns distance to the nearer of two mines.

---

## 3.10 Detailed Dry Run

Teaching grid:

```text
n = 6
m = 7
```

Assume a nearest mine from `(1,1)` is:

```text
M = (4,3)
```

First query:

```text
ask(1,1)
```

Distance:

```text
|4-1| + |3-1| = 3 + 2 = 5
```

So:

```text
a = 5
S = 7
```

Diagonal:

```text
x+y=7
```

Endpoint 1:

```text
xd1 = max(1, 7-7) = 1
yd1 = 7-1 = 6
```

So:

```text
E1 = (1,6)
```

Distance to mine:

```text
|4-1| + |3-6| = 3 + 3 = 6
```

Diagonal steps:

```text
6/2 = 3
```

Candidate:

```text
xp1 = 1+3 = 4
yp1 = 6-3 = 3
```

So:

```text
P1 = (4,3)
```

Verification:

```text
ask(4,3) = 0
```

Therefore:

```text
! 4 3
```

---

## 3.11 Query Count

Maximum:

```text
1. ask(1,1)
2. ask(endpoint1)
3. ask(endpoint2)
4. ask(candidate1)
```

So:

```text
at most 4 queries
```

---

## 3.12 Algorithm

```text
d = ask(1,1)

diagonal:
x+y=d+2

compute two valid endpoints E1,E2

d1 = ask(E1)
d2 = ask(E2)

if d1==0:
    output E1

if d2==0:
    output E2

derive P1 using d1/2
derive P2 using d2/2

if ask(P1)==0:
    output P1
else:
    output P2
```

---

## 3.13 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int ask(int x, int y) {
    cout << "? " << x << " " << y << endl;

    int ans;
    cin >> ans;

    return ans;
}

void tell(int x, int y) {
    cout << "! " << x << " " << y << endl;
}

int main() {
    int t;
    cin >> t;

    while (t--) {
        int n, m;
        cin >> n >> m;

        int a = ask(1, 1);
        int xd1 = max(1, a + 2 - m), yd1 = a + 2 - xd1;
        int yd2 = max(1, a + 2 - n), xd2 = a + 2 - yd2;

        int ad1 = ask(xd1, yd1);
        int ad2 = ask(xd2, yd2);

        if (ad1 == 0) {
            tell(xd1, yd1);
            continue;
        }

        if (ad2 == 0) {
            tell(xd2, yd2);
            continue;
        }

        int xp1 = xd1 + ad1 / 2, yp1 = yd1 - ad1 / 2;
        int xp2 = xd2 - ad2 / 2, yp2 = yd2 + ad2 / 2;

        if (ask(xp1, yp1) == 0) {
            tell(xp1, yp1);
        } else {
            tell(xp2, yp2);
        }
    }

    return 0;
}
```

---

## 3.14 Recognition Model

```text
small fixed query budget
+
Manhattan distance reply
+
need hidden coordinate
```

Think:

```text
Can one query define an equation / geometric locus?
```

Here:

```text
ask(1,1)=d
→ x+y=d+2
→ one anti-diagonal
```

Then:

```text
query extreme points
→ convert distance into diagonal steps
→ derive candidate coordinates
```

Pattern:

```text
Distance Query
→ Equation
→ Geometric Locus
→ Strategic Endpoints
→ Candidate
```

---

# 4. Problem 3 — Guessing the Greatest (Hard Version)

## 4.1 Problem Link

[Codeforces 1486C2 — Guessing the Greatest (Hard Version)](https://codeforces.com/contest/1486/problem/C2)

---

## 4.2 What It Asks

There is a hidden array of distinct numbers.

For any subarray:

```text
[l,r]
```

you may query:

```text
the index of the second maximum
```

Find the index of the global maximum within the hard-version query limit.

---

## 4.3 What the Query Returns

```text
ask(l,r)
```

returns:

```text
index of second largest value in a[l...r]
```

Example:

```text
A = [10,1,2,3,5,8,7,6]
```

Full range:

```text
max = 10 at 1
second max = 8 at 6
```

Therefore:

```text
ask(1,8) = 6
```

---

## 4.4 First Query — Global Second Maximum

The code starts:

```cpp
int secondMax = ask(1, n);
```

Call it:

```text
S
```

Since the full array is queried:

```text
S = global second maximum index
```

The global maximum lies either:

```text
left of S
```

or:

```text
right of S
```

---

## 4.5 Determine Whether the Maximum Is Left or Right

If:

```text
S != 1
```

query:

```text
ask(1,S)
```

If reply:

```text
S
```

then range `[1,S]` contains both global maximum and global second maximum.

Therefore:

```text
maximum lies left of S
```

Otherwise:

```text
maximum lies right of S
```

If:

```text
S = 1
```

maximum must be on the right.

---

## 4.6 Binary Search When Maximum Is Left

Suppose:

```text
P < S
```

where `P` is the maximum index.

Query:

```text
ask(mid,S)
```

If:

```text
mid <= P
```

range contains both global max `P` and second max `S`.

So:

```text
ask(mid,S) = S
```

If:

```text
mid > P
```

global max is excluded.

So:

```text
ask(mid,S) != S
```

Predicate:

```text
T T T T F F F
      ^
      |
      P
```

Find:

```text
last True
```

---

## 4.7 Binary Search When Maximum Is Right

Suppose:

```text
P > S
```

Query:

```text
ask(S,mid)
```

If:

```text
mid < P
```

maximum excluded:

```text
ask(S,mid) != S
```

If:

```text
mid >= P
```

range contains both max and second max:

```text
ask(S,mid) = S
```

Predicate:

```text
F F F T T T
      ^
      |
      P
```

Find:

```text
first True
```

---

## 4.8 Detailed Dry Run — Maximum on the Left

Use:

```text
A = [10,1,2,3,5,8,7,6]
```

Global max:

```text
P = 1
```

Global second max:

```text
S = 6
```

First:

```text
ask(1,8) = 6
```

Direction query:

```text
ask(1,6) = 6
```

So max lies left of `S`.

Search:

```text
low = 1
high = 5
```

Try:

```text
mid = 3
```

`[3,6]` excludes global max at `1`.

So:

```text
ask(3,6) != 6
```

Move:

```text
high = 2
```

Try:

```text
mid = 1
```

```text
ask(1,6) = 6
```

True.

Try larger:

```text
low = 2
```

At `mid=2`, global max excluded, false.

Last True is:

```text
1
```

Answer:

```text
! 1
```

---

## 4.9 Detailed Dry Run — Maximum on the Right

Use:

```text
A = [1,8,3,5,10,7,6]
```

Global second max:

```text
S = 2
```

Global max:

```text
P = 5
```

First:

```text
ask(1,7) = 2
```

Direction query:

```text
ask(1,2) != 2
```

So max lies right.

Search:

```text
low = 3
high = 7
```

Try:

```text
mid = 5
```

Range `[2,5]` contains:

```text
8 at 2
10 at 5
```

So:

```text
ask(2,5) = 2
```

True.

Try earlier:

```text
high = 4
```

For `mid=3` or `4`, global max at `5` is excluded, so false.

First True is:

```text
5
```

Answer:

```text
! 5
```

---

## 4.10 Query Count

For:

```text
n <= 100000
```

binary-search depth:

```text
≈ 17
```

Total:

```text
1 full-range query
+
0 or 1 direction query
+
about log2(N) search queries
```

So roughly:

```text
<= 19 queries
```

which fits the hard-version limit shown in the lecture.

---

## 4.11 Algorithm

```text
S = ask(1,n)

if n == 2:
    answer = other index

else:

    if S != 1 and ask(1,S) == S:
        maximum lies left
    else:
        maximum lies right

LEFT:

    binary search mid in [1,S-1]

    predicate:
    ask(mid,S) == S

    find last True

RIGHT:

    binary search mid in [S+1,n]

    predicate:
    ask(S,mid) == S

    find first True
```

---

## 4.12 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int ask(int l, int r) {
    cout << "? " << l << " " << r << endl;

    int ans;
    cin >> ans;

    return ans;
}

void tell(int ind) {
    cout << "! " << ind << endl;
}

int main() {
    int n;
    cin >> n;

    int secondMax = ask(1, n);

    if (n == 2) {
        if (secondMax == 1) {
            tell(2);
        } else {
            tell(1);
        }

        return 0;
    }

    int tmp = -1;

    if (secondMax != 1) {
        tmp = ask(1, secondMax);
    }

    if (tmp == secondMax) {
        int low = 1, high = secondMax - 1;
        int ans = -1;

        while (low <= high) {
            int mid = (low + high) / 2;

            if (ask(mid, secondMax) == secondMax) {
                ans = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        tell(ans);
    } else {
        int low = secondMax + 1, high = n;
        int ans = -1;

        while (low <= high) {
            int mid = (low + high) / 2;

            if (ask(secondMax, mid) == secondMax) {
                ans = mid;
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        tell(ans);
    }

    return 0;
}
```

---

## 4.13 Recognition Model

```text
query gives second-maximum index
```

First:

```text
ask full range
→ get global second maximum position S
```

Use `S` as an anchor.

Then ask:

```text
Does this range containing S
also contain the global maximum?
```

If yes:

```text
query returns S
```

That creates a monotonic predicate.

Pattern:

```text
Global Anchor
→ Determine Side
→ Build Monotonic Predicate
→ Binary Search Boundary
```

---

# 5. Compare the Three Problems

| Problem | Query Gives | Key Observation | Main Technique | Query Limit |
|---|---|---|---|---:|
| Searching Local Minimum | value at index | smaller neighbor points toward a side containing a local minimum | Binary Search | 100 |
| Find a Mine | nearest Manhattan distance | corner query creates `x+y=constant` | Geometry + Casework | 4 |
| Guessing the Greatest | second-max index in range | global second max anchors a monotonic range predicate | Binary Search | hard-version limit |

Main difference:

```text
Problem 1:
local slope tells direction

Problem 2:
distance becomes geometry

Problem 3:
second maximum creates a monotonic predicate
```

---

# 6. Common Mistakes

## Mistake 1 — Forgetting to Flush

Use:

```cpp
endl
```

or explicit:

```cpp
flush
```

after every query.

---

## Mistake 2 — Thinking Binary Search Requires Sorted Values

Problem 1 and Problem 3 binary-search information structure, not sorted values.

Ask:

```text
Can this reply prove one side unnecessary?
```

---

## Mistake 3 — Local Minimum: Ignoring Boundaries

Use conceptual:

```text
+INF
```

outside the array.

The supplied code uses:

```cpp
INT_MAX
```

---

## Mistake 4 — Local Minimum: Going Toward the Larger Neighbor

The smaller neighbor gives the downhill direction.

That side is guaranteed to contain a local minimum.

---

## Mistake 5 — Find a Mine: Forgetting Why `+2` Appears

From `(1,1)`:

```text
d = (x-1) + (y-1)
```

So:

```text
x+y = d+2
```

---

## Mistake 6 — Find a Mine: Using `d` Instead of `d/2`

One step along the anti-diagonal has Manhattan length `2`.

Therefore:

```text
steps = d/2
```

---

## Mistake 7 — Find a Mine: Not Verifying a Candidate

Endpoint queries return distance to the nearer of two mines.

The other mine can influence one query.

That is why the supplied solution derives two candidates and verifies one.

---

## Mistake 8 — Guessing the Greatest: Confusing Max With Second Max

```text
ask(l,r)
```

returns the **second maximum index**.

The trick is to use that index as an anchor to locate the maximum.

---

## Mistake 9 — Wrong Binary-Search Boundary in Problem 3

Left case:

```text
ask(mid,S) == S
```

is:

```text
True ... True False ... False
```

Find:

```text
last True
```

Right case:

```text
ask(S,mid) == S
```

is:

```text
False ... False True ... True
```

Find:

```text
first True
```

---

## Mistake 10 — Not Counting Queries

Always estimate:

```text
setup queries
+
queries per iteration × iterations
```

before submission.

---

# 7. Final Mental Model

## Pattern A — Search by Local Slope

```text
query neighborhood around mid
        |
        v
is mid local minimum?
   /             \
 yes             no
 |                |
answer      which neighbor is smaller?
                 |
          +------+------+
          |             |
         left          right
          |             |
          v             v
     discard right  discard left
```

Use for:

```text
Searching Local Minimum
```

---

## Pattern B — Convert Distance Into Geometry

```text
query corner
    |
    v
get Manhattan distance d
    |
    v
x+y=d+2
    |
    v
one candidate diagonal
    |
    v
query its endpoints
    |
    v
distance / 2
    |
    v
candidate coordinates
    |
    v
verify one candidate
```

Use for:

```text
Find a Mine
```

---

## Pattern C — Build a Monotonic Predicate From a Global Anchor

```text
ask full range
    |
    v
S = global second maximum index
    |
    v
determine whether max is left/right of S
    |
    v
query ranges containing S
    |
    v
"does query still return S?"
    |
    v
monotonic True/False pattern
    |
    v
binary search maximum index
```

Use for:

```text
Guessing the Greatest
```

---

## 60-Second Recognition

```text
WHAT DOES THE QUERY RETURN?
        |
        +-------------------------------+
        |               |               |
        v               v               v
neighbor values     distance        second-max index
        |               |               |
        v               v               v
local slope        geometry          global anchor
        |               |               |
        v               v               v
binary search      casework      binary search
```

---

## One Sentence to Remember

```text
Interactive problems are about converting each judge reply into structure:
a direction, an equation, or a monotonic predicate that removes possibilities quickly.
```
