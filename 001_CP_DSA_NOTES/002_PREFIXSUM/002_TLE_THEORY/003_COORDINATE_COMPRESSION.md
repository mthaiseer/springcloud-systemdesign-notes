# Coordinate Compression
### Step-by-Step Visual Notes with Prerequisites, ASCII Diagrams and C++17

> Based on the uploaded TLE Level 2 lecture:
> **Coordinate Compression**
>
> The lecture focuses on:
>
> 1. Understanding coordinate compression.
> 2. Preserving ordering while replacing very large values with small indices.
> 3. Combining coordinate compression with a difference array.
> 4. Applying the idea to **AtCoder ABC188 D - Snuke Prime**.
>
> The final lecture framework is:
>
> ```text
> 1. Figure out the important points.
> 2. Map the important points.
> 3. Solve using only those important points.
> 4. Recover the contribution of the points/intervals between them.
> ```

---

# Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [What Problem Does Coordinate Compression Solve?](#2-what-problem-does-coordinate-compression-solve)
3. [The Main Requirement - Preserve Ordering](#3-the-main-requirement---preserve-ordering)
4. [Why Simple Arithmetic Transformations Do Not Work](#4-why-simple-arithmetic-transformations-do-not-work)
5. [Core Idea - Replace Values by Their Ranks](#5-core-idea---replace-values-by-their-ranks)
6. [Example 1 - Unique Values](#6-example-1---unique-values)
7. [Example 2 - Duplicate Values](#7-example-2---duplicate-values)
8. [Step-by-Step Compression Algorithm](#8-step-by-step-compression-algorithm)
9. [C++17 - Basic Coordinate Compression](#9-c17---basic-coordinate-compression)
10. [Using `lower_bound`](#10-using-lower_bound)
11. [Important Warning - Compressed Distance Is Not Real Distance](#11-important-warning---compressed-distance-is-not-real-distance)
12. [Coordinate Compression + Difference Array](#12-coordinate-compression--difference-array)
13. [Problem - Snuke Prime](#13-problem---snuke-prime)
14. [Understanding the Snuke Prime Model](#14-understanding-the-snuke-prime-model)
15. [Case 1 - Small Coordinates](#15-case-1---small-coordinates)
16. [Difference Array for Small Coordinates](#16-difference-array-for-small-coordinates)
17. [Why Large Coordinates Break the Direct Approach](#17-why-large-coordinates-break-the-direct-approach)
18. [Finding the Important Points](#18-finding-the-important-points)
19. [Why We Store `b + 1`](#19-why-we-store-b--1)
20. [Compressing the Important Days](#20-compressing-the-important-days)
21. [Building the Difference Array on Compressed Coordinates](#21-building-the-difference-array-on-compressed-coordinates)
22. [Recovering the Active Cost](#22-recovering-the-active-cost)
23. [The Missing Piece - Interval Length](#23-the-missing-piece---interval-length)
24. [Full ASCII Dry Run](#24-full-ascii-dry-run)
25. [Final Formula](#25-final-formula)
26. [C++17 - Snuke Prime](#26-c17---snuke-prime)
27. [Alternative Event-Based View](#27-alternative-event-based-view)
28. [Complexity](#28-complexity)
29. [Common Mistakes](#29-common-mistakes)
30. [Recognition Pattern](#30-recognition-pattern)
31. [Coordinate Compression Checklist](#31-coordinate-compression-checklist)
32. [Final Mental Model](#32-final-mental-model)

---

# 1. Prerequisites

The lecture assumes familiarity with several ideas.

This section is a supporting refresher so the main lecture becomes easier to follow.

---

## 1.1 Arrays and indices

Example:

```text
index:   0   1   2   3
value:   7  20  50  90
```

We often want to use a value as an array index.

This is easy when values are small:

```text
value <= 10^6
```

But impossible when values can be:

```text
10^12
```

because we cannot allocate:

```text
array[10^12]
```

---

## 1.2 Sorting

Example:

```text
original:

40, 2, 1000, 11
```

After sorting:

```text
2, 11, 40, 1000
```

Sorting reveals the relative order.

---

## 1.3 Removing duplicates

Example:

```text
2, 40, 40, 60
```

Unique sorted values:

```text
2, 40, 60
```

We want both occurrences of `40` to receive the same compressed value.

---

## 1.4 `lower_bound`

For a sorted vector:

```text
coords = [2, 40, 60]
```

`lower_bound(coords.begin(), coords.end(), 40)` points to index:

```text
1
```

So we can map:

```text
40 -> 1
```

---

## 1.5 Difference array

For a range update:

```text
add X to every position in [L, R]
```

we can write:

```text
diff[L] += X
diff[R + 1] -= X
```

Then prefix sum of `diff` recovers the actual active value.

ASCII:

```text
L                     R
|---------------------|
+X                    stop

diff[L]     += X
diff[R + 1] -= X
```

This idea is essential for the second half of the lecture.

---

## 1.6 Use `long long`

The lecture uses coordinates that may go up to:

```text
10^9
```

and the first compression example discusses values up to:

```text
10^12
```

Products such as:

```text
cost * numberOfDays
```

can become large.

Use:

```cpp
long long
```

for coordinates, costs and answers.

---

# 2. What Problem Does Coordinate Compression Solve?

The lecture starts with a problem of this form:

```text
We have N numbers.

N is manageable.

But each number may be extremely large.
```

For example:

```text
N <= 2 * 10^5
A[i] <= 10^12
```

We may want to use these values as positions or indices.

But:

```text
10^12 positions
```

cannot be stored directly.

The important observation is:

```text
We do not always care about the exact magnitude.

Sometimes we only care about:

1. which value is smaller,
2. which value is larger,
3. which values are equal.
```

That is where coordinate compression helps.

---

# 3. The Main Requirement - Preserve Ordering

Suppose:

```text
A[i] < A[j]
```

After compression, we want:

```text
compressed[A[i]] < compressed[A[j]]
```

And if:

```text
A[i] == A[j]
```

we want:

```text
compressed[A[i]] == compressed[A[j]]
```

---

## Visual requirement

Original:

```text
2 < 40 < 1000 < 10^11
```

Compressed:

```text
0 <  1 <   2  <   3
```

The actual gaps changed dramatically.

But the **order** is preserved.

---

# 4. Why Simple Arithmetic Transformations Do Not Work

The lecture explicitly considers simple operations such as:

```text
subtraction
division
modulo
log
```

and rejects them as general coordinate-compression methods.

Why?

Because coordinate compression must preserve the ordering and equality relationships reliably.

---

## Example - modulo can destroy order

Suppose:

```text
10 < 101
```

Take modulo 10:

```text
10 % 10  = 0
101 % 10 = 1
```

This happened to preserve order here.

But:

```text
19 < 20
```

becomes:

```text
19 % 10 = 9
20 % 10 = 0
```

Now:

```text
9 > 0
```

The order is destroyed.

---

## What we really need

We need:

```text
ranking
```

not arbitrary arithmetic transformation.

---

# 5. Core Idea - Replace Values by Their Ranks

Suppose the values are:

```text
2, 40, 1000, 10^11, 10^12
```

Sort them:

```text
2 < 40 < 1000 < 10^11 < 10^12
```

Assign ranks:

```text
2       -> 0
40      -> 1
1000    -> 2
10^11   -> 3
10^12   -> 4
```

Now every huge coordinate has become a small integer.

---

## ASCII picture

```text
Original coordinate line

2 -------- 40 ---------------- 1000 -------------------- 10^11 -------- 10^12


Compressed coordinate line

0 -------- 1 ------------------ 2 ----------------------- 3 ------------ 4
```

The distances are no longer preserved.

The ordering is preserved.

That is the key.

---

# 6. Example 1 - Unique Values

Suppose:

```text
A = [2, 10^11, 10^12, 1000, 40]
```

---

## Step 1 - Copy

```text
values = [2, 10^11, 10^12, 1000, 40]
```

---

## Step 2 - Sort

```text
[2, 40, 1000, 10^11, 10^12]
```

---

## Step 3 - Assign ranks

```text
2       -> 0
40      -> 1
1000    -> 2
10^11   -> 3
10^12   -> 4
```

---

## Step 4 - Replace original values

Original:

```text
[2, 10^11, 10^12, 1000, 40]
```

Compressed:

```text
[0, 3, 4, 2, 1]
```

---

# 7. Example 2 - Duplicate Values

Suppose:

```text
A = [2, 40, 40, 60]
```

Sorted unique values:

```text
[2, 40, 60]
```

Mapping:

```text
2  -> 0
40 -> 1
60 -> 2
```

Original:

```text
2   40   40   60
```

Compressed:

```text
0    1    1    2
```

ASCII:

```text
2  ---------> 0

40 ---------> 1
40 ---------> 1

60 ---------> 2
```

Equal values receive equal compressed coordinates.

---

# 8. Step-by-Step Compression Algorithm

The standard workflow is:

```text
Original values
      |
      v
Copy values
      |
      v
Sort
      |
      v
Remove duplicates
      |
      v
For each original value:
    find its index in sorted unique values
      |
      v
Compressed coordinate
```

More concretely:

```text
1. Put all relevant coordinates into a vector.

2. Sort the vector.

3. Remove duplicates.

4. For every original coordinate x:
       compressed(x)
       =
       lower_bound(coords, x)
```

---

# 9. C++17 - Basic Coordinate Compression

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<long long> a(n);
    for (int i = 0; i < n; i++) {
        cin >> a[i];
    }

    vector<long long> coords = a;

    sort(coords.begin(), coords.end());

    coords.erase(
        unique(coords.begin(), coords.end()),
        coords.end()
    );

    vector<int> compressed(n);

    for (int i = 0; i < n; i++) {
        compressed[i] =
            lower_bound(coords.begin(), coords.end(), a[i])
            - coords.begin();
    }

    for (int x : compressed) {
        cout << x << ' ';
    }

    cout << '\n';

    return 0;
}
```

---

# 10. Using `lower_bound`

Suppose:

```text
coords = [2, 40, 1000, 10^11]
```

We want to compress:

```text
1000
```

`lower_bound` finds the first position where:

```text
value >= 1000
```

That is index:

```text
2
```

So:

```text
1000 -> 2
```

ASCII:

```text
coords:

index:    0      1       2        3
value:    2     40     1000     10^11
                         ^
                         |
                  lower_bound(1000)
```

---

# 11. Important Warning - Compressed Distance Is Not Real Distance

This is one of the most important ideas in the whole topic.

Suppose:

```text
real coordinates:

2, 4, 1000000000
```

Compressed:

```text
0, 1, 2
```

Look at the gaps.

Real:

```text
4 - 2 = 2

1000000000 - 4 = 999999996
```

Compressed:

```text
1 - 0 = 1

2 - 1 = 1
```

So:

```text
compressedIndex difference
```

does **not** represent:

```text
real coordinate difference
```

Therefore, when solving interval problems, always keep the original sorted coordinates.

---

## Visual

```text
REAL:

2 ---- 4 ---------------------------------------------------------- 1e9

gap=2                         huge gap


COMPRESSED:

0 ---- 1 ---- 2

gap=1   gap=1
```

Compression preserves:

```text
order
```

not:

```text
distance
```

---

# 12. Coordinate Compression + Difference Array

The lecture's second goal is to combine:

```text
Difference Array
+
Coordinate Compression
```

This becomes useful when:

```text
coordinates are huge
```

but only a small number of coordinates are important.

---

## General pattern

Suppose we have many intervals:

```text
[L1, R1]
[L2, R2]
[L3, R3]
...
```

and coordinates may be as large as:

```text
10^9
```

We cannot build:

```text
diff[10^9]
```

But if only:

```text
2N
```

endpoints matter, we can compress those endpoints.

---

# 13. Problem - Snuke Prime

The lecture uses:

```text
AtCoder ABC188 D
Snuke Prime
```

Problem model:

Each service has:

```text
a[i] = start day
b[i] = end day
c[i] = daily cost
```

The service is active on every day:

```text
a[i] ... b[i]
```

There is also a daily subscription cap:

```text
C
```

So the amount paid on one day is:

```text
min(
    sum of active service costs,
    C
)
```

We want the total cost over all relevant days.

---

# 14. Understanding the Snuke Prime Model

Suppose:

```text
Service 1:
a = 2
b = 4
c = 6

Service 2:
a = 4
b = 6
c = 5

Daily cap:
C = 10
```

Timeline:

```text
day:       1   2   3   4   5   6   7

service1:      6   6   6
service2:              5   5   5
```

Total active cost:

```text
day 2 -> 6
day 3 -> 6
day 4 -> 11
day 5 -> 5
day 6 -> 5
```

But daily payment is capped at `10`.

So:

```text
day 2 -> min(6,10)  = 6
day 3 -> min(6,10)  = 6
day 4 -> min(11,10) = 10
day 5 -> min(5,10)  = 5
day 6 -> min(5,10)  = 5
```

---

# 15. Case 1 - Small Coordinates

The lecture first discusses the easier case:

```text
a[i], b[i] <= 10^6
```

Then every day can directly correspond to an array index.

We can allocate something like:

```text
diff[1 ... 10^6]
```

---

# 16. Difference Array for Small Coordinates

For one service:

```text
[a, b]
cost = c
```

we want to add `c` to every day:

```text
a, a+1, ..., b
```

Difference array:

```text
diff[a]     += c
diff[b + 1] -= c
```

Then take prefix sums.

---

## Example

Service:

```text
a = 2
b = 5
c = 6
```

Store:

```text
diff[2] += 6
diff[6] -= 6
```

ASCII:

```text
day:     1   2   3   4   5   6   7

diff:    0  +6   0   0   0  -6   0

prefix:  0   6   6   6   6   0   0
```

Exactly the required active cost.

---

# 17. Why Large Coordinates Break the Direct Approach

Now suppose:

```text
a[i], b[i] <= 10^9
```

We cannot safely build:

```text
diff[1e9]
```

Most days may not even matter.

Example:

```text
important days:

2
4
6
9
```

Everything between two consecutive important days behaves the same.

That is the opportunity for coordinate compression.

---

# 18. Finding the Important Points

The lecture highlights:

```text
a[i]
```

and:

```text
b[i] + 1
```

as the important points.

Why?

Because active cost changes only when:

```text
a service starts
```

or:

```text
a service stops
```

So for every interval:

```text
[a[i], b[i]]
```

collect:

```text
a[i]
b[i] + 1
```

---

## ASCII

Service:

```text
a = 2
b = 5
c = 6
```

Important points:

```text
2          6
|----------|
start      stop
+6         -6
```

Notice:

```text
stop point = b + 1
           = 6
```

---

# 19. Why We Store `b + 1`

Suppose a service is active:

```text
day 2 through day 5
```

It must still contribute on day `5`.

So we cannot stop it at:

```text
b = 5
```

We stop immediately after:

```text
b + 1 = 6
```

Difference array:

```text
diff[2] += 6
diff[6] -= 6
```

Then:

```text
days 2,3,4,5
```

all receive `+6`.

---

# 20. Compressing the Important Days

Suppose our important real days are:

```text
2, 4, 6, 9
```

Sorted unique:

```text
coords = [2, 4, 6, 9]
```

Compressed mapping:

```text
2 -> 0
4 -> 1
6 -> 2
9 -> 3
```

ASCII:

```text
REAL DAY

2 -------- 4 -------- 6 ---------------- 9


COMPRESSED INDEX

0 -------- 1 -------- 2 ---------------- 3
```

Again:

```text
index gap != real-day gap
```

We still retain:

```text
coords = [2,4,6,9]
```

to recover real interval lengths.

---

# 21. Building the Difference Array on Compressed Coordinates

Consider the lecture-style example represented by these changes:

```text
day 2 -> +6
day 4 -> +9
day 6 -> -6
day 9 -> -9
```

These could come from:

```text
service 1: [2,5], cost 6
service 2: [4,8], cost 9
```

Important days:

```text
2,4,6,9
```

Compressed:

```text
2 -> 0
4 -> 1
6 -> 2
9 -> 3
```

Difference values:

```text
compressed index:   0    1    2    3

real day:           2    4    6    9

diff:              +6   +9   -6   -9
```

---

# 22. Recovering the Active Cost

Take prefix sum of the compressed difference array.

Start:

```text
diff = [6, 9, -6, -9]
```

Prefix:

```text
prefix[0] = 6

prefix[1] = 6 + 9
          = 15

prefix[2] = 15 - 6
          = 9

prefix[3] = 9 - 9
          = 0
```

So:

```text
real day:        2     4     6     9

active cost:     6    15     9     0
```

What does that mean?

```text
from day 2 until before day 4:
    active cost = 6

from day 4 until before day 6:
    active cost = 15

from day 6 until before day 9:
    active cost = 9
```

---

# 23. The Missing Piece - Interval Length

This is where many coordinate-compression mistakes happen.

The compressed indices are:

```text
0,1,2,3
```

But the real intervals are:

```text
2 -> 4
4 -> 6
6 -> 9
```

Lengths:

```text
4 - 2 = 2 days

6 - 4 = 2 days

9 - 6 = 3 days
```

So the contribution of one compressed segment is:

```text
paymentPerDay
*
realSegmentLength
```

not simply:

```text
paymentPerDay * 1
```

---

## ASCII

```text
coords:

2 -------- 4 -------- 6 ------------ 9
|          |          |              |
index 0    index 1    index 2        index 3

segment 0:
[2,4)
days = 2,3
length = 2

segment 1:
[4,6)
days = 4,5
length = 2

segment 2:
[6,9)
days = 6,7,8
length = 3
```

---

# 24. Full ASCII Dry Run

Take:

```text
service 1:
[2,5], cost 6

service 2:
[4,8], cost 9

daily cap:
C = 10
```

---

## Step 1 - Convert intervals to events

Service 1:

```text
start at 2     -> +6
stop at 5 + 1 -> -6 at 6
```

Service 2:

```text
start at 4     -> +9
stop at 8 + 1 -> -9 at 9
```

So:

```text
events:

2 -> +6
4 -> +9
6 -> -6
9 -> -9
```

---

## Step 2 - Collect important points

```text
[2,4,6,9]
```

---

## Step 3 - Compress

```text
2 -> 0
4 -> 1
6 -> 2
9 -> 3
```

---

## Step 4 - Difference array

```text
index:     0    1    2    3
day:       2    4    6    9
diff:     +6   +9   -6   -9
```

---

## Step 5 - Prefix sum

```text
index:        0    1    2    3
day:          2    4    6    9
activeCost:   6   15    9    0
```

---

## Step 6 - Apply daily cap

```text
C = 10
```

So:

```text
at day 2 segment:
min(6,10) = 6

at day 4 segment:
min(15,10) = 10

at day 6 segment:
min(9,10) = 9
```

---

## Step 7 - Multiply by real interval length

Segment `[2,4)`:

```text
length = 4 - 2 = 2

contribution
=
6 * 2
=
12
```

Segment `[4,6)`:

```text
length = 6 - 4 = 2

contribution
=
10 * 2
=
20
```

Segment `[6,9)`:

```text
length = 9 - 6 = 3

contribution
=
9 * 3
=
27
```

Total:

```text
12 + 20 + 27
=
59
```

---

## Same answer day by day

```text
day 2 -> 6
day 3 -> 6

day 4 -> min(15,10) = 10
day 5 -> min(15,10) = 10

day 6 -> 9
day 7 -> 9
day 8 -> 9
```

Total:

```text
6 + 6 + 10 + 10 + 9 + 9 + 9

=
59
```

Compression simply groups equal-behaviour days together.

---

# 25. Final Formula

After compression and prefixing the difference array:

```text
activeCost[i]
```

is constant over:

```text
coords[i] ... coords[i+1]-1
```

Number of real days:

```text
coords[i+1] - coords[i]
```

Payment per day:

```text
min(activeCost[i], C)
```

Therefore:

```text
contribution
=
min(activeCost[i], C)
*
(coords[i+1] - coords[i])
```

Final answer:

```text
answer
=
Σ min(activeCost[i], C)
  * (coords[i+1] - coords[i])
```

for every adjacent pair of important coordinates.

---

# 26. C++17 - Snuke Prime

This implementation follows the lecture method:

```text
important points
-> compression
-> difference array
-> prefix
-> multiply by real interval length
```

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Service {
    long long start;
    long long end;
    long long cost;
};

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    long long cap;

    cin >> n >> cap;

    vector<Service> services(n);
    vector<long long> coords;

    for (int i = 0; i < n; i++) {
        long long a, b, c;
        cin >> a >> b >> c;

        services[i] = {a, b, c};

        coords.push_back(a);
        coords.push_back(b + 1);
    }

    sort(coords.begin(), coords.end());

    coords.erase(
        unique(coords.begin(), coords.end()),
        coords.end()
    );

    vector<long long> diff(coords.size() + 1, 0);

    for (const auto& service : services) {
        int left =
            lower_bound(
                coords.begin(),
                coords.end(),
                service.start
            ) - coords.begin();

        int rightAfter =
            lower_bound(
                coords.begin(),
                coords.end(),
                service.end + 1
            ) - coords.begin();

        diff[left] += service.cost;
        diff[rightAfter] -= service.cost;
    }

    for (int i = 1; i < (int)coords.size(); i++) {
        diff[i] += diff[i - 1];
    }

    long long answer = 0;

    for (int i = 0; i + 1 < (int)coords.size(); i++) {
        long long days = coords[i + 1] - coords[i];
        long long dailyCost = min(diff[i], cap);

        answer += dailyCost * days;
    }

    cout << answer << '\n';

    return 0;
}
```

---

# 27. Alternative Event-Based View

The lecture teaches the idea through:

```text
coordinate compression
+
difference array
```

There is another way to mentally view exactly the same information:

```text
each important coordinate is an event
```

Example:

```text
2 -> +6
4 -> +9
6 -> -6
9 -> -9
```

Sort events:

```text
2,4,6,9
```

Maintain:

```text
current active cost
```

Between two adjacent events:

```text
current cost does not change
```

This is the same observation used by compressed difference arrays.

The lecture's four-step framework is still the same:

```text
find important points
map/sort them
solve on them
account for the gaps
```

---

# 28. Complexity

Let:

```text
N = number of services
K = number of distinct important coordinates
```

Each service contributes at most:

```text
2 important points
```

So:

```text
K <= 2N
```

---

## Sorting coordinates

```text
O(N log N)
```

---

## Finding compressed indices

Using `lower_bound`:

```text
2 lookups per service

O(N log N)
```

---

## Prefix over compressed array

```text
O(K)
```

and:

```text
K <= 2N
```

so:

```text
O(N)
```

---

## Total

```text
O(N log N)
```

Space:

```text
O(N)
```

---

# 29. Common Mistakes

## Mistake 1 - Using the original huge coordinate as an index

Wrong:

```cpp
vector<long long> diff(1000000001);
```

for large sparse coordinates.

Instead:

```text
compress only important coordinates
```

---

## Mistake 2 - Forgetting duplicates

If two original coordinates are equal:

```text
40
40
```

they must map to the same compressed value.

Always:

```cpp
sort(...)
unique(...)
```

---

## Mistake 3 - Compression that does not preserve order

Coordinate compression is not:

```text
x % M
```

or arbitrary division.

It is:

```text
rank among sorted unique values
```

---

## Mistake 4 - Using compressed-index difference as real distance

Wrong:

```cpp
days = (i + 1) - i;
```

That is always:

```text
1
```

Correct:

```cpp
days = coords[i + 1] - coords[i];
```

---

## Mistake 5 - Using `b` instead of `b + 1`

For inclusive service interval:

```text
[a,b]
```

the subtraction event belongs at:

```text
b + 1
```

---

## Mistake 6 - Forgetting the prefix step

After writing:

```text
diff[left] += cost;
diff[rightAfter] -= cost;
```

the `diff` array contains change events.

You still need:

```cpp
diff[i] += diff[i - 1];
```

to obtain the current active cost.

---

## Mistake 7 - Forgetting the cap

For Snuke Prime:

```text
dailyCost
=
min(activeCost, C)
```

not simply:

```text
activeCost
```

---

## Mistake 8 - Using `int`

Values such as:

```text
coordinate up to 10^9 or 10^12
cost * duration
total answer
```

can exceed 32-bit integer range.

Use:

```cpp
long long
```

---

# 30. Recognition Pattern

Coordinate compression should come to mind when you see:

```text
1. Coordinates/values are huge.

2. Number of actual values is relatively small.

3. Relative ordering matters.

4. Only certain positions cause the state to change.

5. Direct array indexing by the original value is impossible.
```

---

## Especially common with

```text
intervals
range updates
sweep-line problems
difference arrays
Fenwick trees
segment trees
frequency queries
large timestamps
large positions
```

The lecture specifically demonstrates:

```text
difference array + coordinate compression
```

for large day coordinates.

---

# 31. Coordinate Compression Checklist

The final lecture slide gives four essential steps.

Use this checklist whenever you suspect compression.

---

## Step 1 - Figure out the important points

Ask:

```text
At which coordinates can something actually change?
```

For Snuke Prime:

```text
a[i]
b[i] + 1
```

---

## Step 2 - Map the important points

```text
collect
sort
remove duplicates
assign indices
```

ASCII:

```text
2   4   6   9
|   |   |   |
0   1   2   3
```

---

## Step 3 - Solve using only the important points

For the lecture problem:

```text
difference updates
+
prefix sums
```

are performed over the compressed indices.

---

## Step 4 - Recover the remaining points

The values between two consecutive important coordinates do not change.

So calculate:

```text
segmentLength
=
coords[i+1] - coords[i]
```

and multiply by the value valid on that whole segment.

---

# 32. Final Mental Model

```text
COORDINATE COMPRESSION
======================

huge sparse values
       |
       v
collect relevant values
       |
       v
sort + unique
       |
       v
replace each value by its rank
       |
       v
small dense index space
```

For interval problems:

```text
REAL COORDINATES

2 -------- 4 -------- 6 ---------------- 9

       compress

0 -------- 1 -------- 2 ---------------- 3

BUT KEEP:

coords = [2,4,6,9]

because:

real distance
=
coords[i+1] - coords[i]
```

For Snuke Prime:

```text
service [a,b], cost c
        |
        v
important points:
a
b+1
        |
        v
compress them
        |
        v
diff[index(a)]     += c
diff[index(b + 1)] -= c
        |
        v
prefix difference array
        |
        v
active cost for each compressed segment
        |
        v
min(activeCost, cap)
        |
        v
multiply by real segment length
        |
        v
answer
```

The most important sentence to remember is:

```text
Coordinate compression preserves ORDER,
not DISTANCE.
```

And the lecture's four-step process is:

```text
1. Find important points.
2. Map important points.
3. Solve only on those points.
4. Account for everything between consecutive points.
```
