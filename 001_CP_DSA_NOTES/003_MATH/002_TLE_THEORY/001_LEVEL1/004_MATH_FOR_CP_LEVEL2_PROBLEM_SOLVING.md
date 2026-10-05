# Math for CP — Intermediate Problem Solving — Level 1

> **Study goal:** do not memorize the final condition or code.  
> Build the answer from a small mathematical model, test it with examples, then implement it.

---

## Clickable Table of Contents

- [0. Core Preliminaries](#0-core-preliminaries)
  - [0.1 Absolute Difference](#01-absolute-difference)
  - [0.2 Sorting and Extremes](#02-sorting-and-extremes)
  - [0.3 Divisibility](#03-divisibility)
  - [0.4 Consecutive Integers](#04-consecutive-integers)
  - [0.5 LCM of Many Numbers](#05-lcm-of-many-numbers)
  - [0.6 Local Contribution](#06-local-contribution)
  - [0.7 Independent Choices](#07-independent-choices)
- [1. Vlad and Candies](#1-vlad-and-candies)
  - [1.1 What the Problem Is Asking](#11-what-the-problem-is-asking)
  - [1.2 Build the Model](#12-build-the-model)
  - [1.3 Why Only the Two Largest Matter](#13-why-only-the-two-largest-matter)
  - [1.4 Dry Runs](#14-dry-runs)
  - [1.5 C++](#15-c)
  - [1.6 Don't-Memorize Model](#16-dont-memorize-model)
- [2. Longest Divisors Interval](#2-longest-divisors-interval)
  - [2.1 What the Problem Is Asking](#21-what-the-problem-is-asking)
  - [2.2 Key Transformation](#22-key-transformation)
  - [2.3 Why the Answer Starts from 1](#23-why-the-answer-starts-from-1)
  - [2.4 Dry Runs](#24-dry-runs)
  - [2.5 C++](#25-c)
  - [2.6 Don't-Memorize Model](#26-dont-memorize-model)
- [3. Array Balancing](#3-array-balancing)
  - [3.1 What the Problem Is Asking](#31-what-the-problem-is-asking)
  - [3.2 Local Contribution](#32-local-contribution)
  - [3.3 Swap vs No Swap](#33-swap-vs-no-swap)
  - [3.4 Why Greedy Works](#34-why-greedy-works)
  - [3.5 Dry Run](#35-dry-run)
  - [3.6 C++](#36-c)
  - [3.7 Don't-Memorize Model](#37-dont-memorize-model)
- [4. Final Recognition Sheet](#4-final-recognition-sheet)

---

# 0. Core Preliminaries

These are the small ideas needed for the three problems.

## 0.1 Absolute Difference

Absolute difference measures distance between two numbers:

```text
|a - b|
```

Examples:

```text
|8 - 3| = 5
|3 - 8| = 5
```

Order does not matter:

```text
|a - b| = |b - a|
```

C++:

```cpp
long long d = abs(a - b);
```

### Recognition

When a problem asks about:

```text
distance
difference
closeness
adjacent cost
```

look for:

```text
abs(x - y)
```

---

## 0.2 Sorting and Extremes

After sorting:

```text
a[0] <= a[1] <= ... <= a[n-2] <= a[n-1]
```

So:

```text
largest        = a[n-1]
second largest = a[n-2]
```

This is useful when feasibility is controlled by the biggest values.

But do not automatically sort every problem.

Ask first:

```text
Do I need the full order?
or
Do I only need max and second max?
```

For the first problem, only the largest two values matter.

---

## 0.3 Divisibility

`x` divides `N` when:

```text
N % x == 0
```

Example:

```text
12 % 3 = 0  -> 3 divides 12
12 % 5 = 2  -> 5 does not divide 12
```

Notation:

```text
x | N
```

means:

```text
x divides N
```

---

## 0.4 Consecutive Integers

An interval:

```text
[l, r]
```

contains:

```text
l, l+1, l+2, ..., r
```

Its length is:

```text
r - l + 1
```

Example:

```text
[9, 11]

numbers = 9, 10, 11
length  = 11 - 9 + 1
        = 3
```

---

## 0.5 LCM of Many Numbers

For `N` to be divisible by every number:

```text
1, 2, 3, ..., k
```

`N` must be divisible by their least common multiple:

```text
LCM(1, 2, ..., k)
```

Example:

```text
k = 4

LCM(1,2,3,4) = 12
```

So any number divisible by all of:

```text
1, 2, 3, 4
```

must be a multiple of `12`.

This is a useful mathematical interpretation even when the implementation simply checks:

```cpp
N % i == 0
```

---

## 0.6 Local Contribution

A large objective often looks like:

```text
total = contribution_1
      + contribution_2
      + ...
```

If one operation only changes one small part of the total, compare only that affected part.

This is the central idea in **Array Balancing**.

Instead of recomputing the whole answer:

```text
old total -> perform operation -> recompute everything
```

ask:

```text
Which terms actually changed?
```

---

## 0.7 Independent Choices

Suppose at position `i` there are two choices:

```text
Choice A -> costA
Choice B -> costB
```

If that choice does not affect any future contribution, then we can safely choose:

```text
min(costA, costB)
```

This is a local greedy decision.

The important part is not memorizing `min(...)`.

The important question is:

```text
Does my current choice affect future states?
```

If **no**, local minimization may be globally optimal.

---

# 1. Vlad and Candies

**Problem:** Vlad and Candies  
**Problem Link:** https://codeforces.com/contest/1660/problem/B

---

## 1.1 What the Problem Is Asking

There are several types of candies.

Let:

```text
a[i] = number of candies of type i
```

Vlad repeatedly eats candies, but consecutive candies cannot be taken from the same type.

Question:

```text
Can all candies be eaten while respecting the rule?
```

This initially looks like a simulation problem.

But we do not need to construct the entire eating order.

We only need to determine whether such an order can exist.

---

## 1.2 Build the Model

Imagine the most frequent candy type has:

```text
mx
```

candies.

All other candy types together provide opportunities to separate those candies.

The dangerous situation is:

```text
one type appears too many times
```

For the structure of this problem, after comparing the largest piles, the critical condition becomes:

```text
largest - second_largest <= 1
```

Equivalently:

```text
largest <= second_largest + 1
```

---

## 1.3 Why Only the Two Largest Matter

Sort conceptually:

```text
a1 <= a2 <= ... <= a[n-1] <= a[n]
```

Let:

```text
mx  = largest
mx2 = second largest
```

If:

```text
mx > mx2 + 1
```

then the largest pile stays too far ahead.

Example:

```text
2 4 6
```

Largest two:

```text
6 and 4
```

Difference:

```text
6 - 4 = 2
```

After using one candy from the largest pile:

```text
2 4 5
```

The imbalance still exists.

Eventually the dominant type cannot be separated correctly.

So:

```text
difference > 1 -> NO
difference <= 1 -> YES
```

### Special case — n = 1

If there is only one type:

```text
[a0]
```

then:

```text
a0 = 1 -> YES
a0 > 1 -> NO
```

because with multiple candies of the same type, two consecutive choices would necessarily come from that same type.

---

## 1.4 Dry Runs

### Example A

```text
a = [1, 2, 3, 3, 4]
```

Largest values:

```text
mx  = 4
mx2 = 3
```

Difference:

```text
4 - 3 = 1
```

Therefore:

```text
YES
```

Visual idea:

```text
largest pile       ****
second largest     ***

difference = *
             1
```

The largest pile is only one ahead.

---

### Example B

```text
a = [2, 4, 6]
```

Largest values:

```text
mx  = 6
mx2 = 4
```

Difference:

```text
6 - 4 = 2
```

Therefore:

```text
NO
```

Visual:

```text
largest pile       ******
second largest     ****

extra              **
                   2
```

The largest pile is too far ahead.

---

### Example C — One Type

```text
n = 1
a = [1]
```

Only one candy exists:

```text
YES
```

But:

```text
n = 1
a = [4]
```

would force:

```text
same type -> same type -> ...
```

Therefore:

```text
NO
```

---

## 1.5 C++

We do not actually need to sort.

Track the largest and second-largest values in one pass.

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int T;
    cin >> T;

    while (T--) {
        int n;
        cin >> n;

        long long mx = -1;
        long long secondMx = -1;

        for (int i = 0; i < n; ++i) {
            long long x;
            cin >> x;

            if (x > mx) {
                secondMx = mx;
                mx = x;
            } else if (x > secondMx) {
                secondMx = x;
            }
        }

        if (n == 1) {
            cout << (mx == 1 ? "YES\n" : "NO\n");
            continue;
        }

        cout << (mx - secondMx <= 1 ? "YES\n" : "NO\n");
    }
}
```

Complexity:

```text
Time  : O(n) per test case
Space : O(1)
```

---

## 1.6 Don't-Memorize Model

Do not memorize:

```text
max - secondMax <= 1
```

Start from the obstruction:

```text
Need to alternate types
        |
        v
What can make this impossible?
        |
        v
One pile dominates
        |
        v
Compare largest pile with its closest competitor
        |
        v
largest cannot be more than 1 ahead
```

Recognition:

```text
repeatedly choose different types
+
one category may dominate
        |
        v
look at extreme frequencies
```

---

# 2. Longest Divisors Interval

**Problem:** Longest Divisors Interval  
**Problem Link:** https://codeforces.com/problemset/problem/1855/B

---

## 2.1 What the Problem Is Asking

Given `N`, find the maximum length of an interval:

```text
[l, r]
```

such that every integer in that interval divides `N`.

That means:

```text
N % l     == 0
N % (l+1) == 0
...
N % r     == 0
```

Example from the lecture:

```text
N = 990990

[9, 10, 11]
```

All three divide `N`, so this gives an interval of length:

```text
3
```

The key is to avoid searching every possible `[l, r]`.

---

## 2.2 Key Transformation

Suppose:

```text
[l, l+1, l+2, ..., r]
```

is a valid interval.

Its length is:

```text
k = r - l + 1
```

Now compare it with:

```text
[1, 2, 3, ..., k]
```

For each position:

```text
1 <= l
2 <= l+1
3 <= l+2
...
k <= r
```

The crucial number-theory result used by this problem is:

```text
If N is divisible by k consecutive integers,
then N is divisible by 1, 2, ..., k.
```

Therefore, if an interval of length `k` exists anywhere, then a valid interval of the same length exists starting from `1`.

So we only need to find the largest `k` such that:

```text
1 | N
2 | N
3 | N
...
k | N
```

This turns an interval-search problem into a prefix-divisibility problem.

---

## 2.3 Why the Answer Starts from 1

Instead of trying:

```text
[9,10,11]
[5,6,7]
[20,21,22]
...
```

we can reduce the question to:

```text
[1,2,3,...,k]
```

So the algorithm becomes:

```text
i = 1

while N % i == 0:
    i++

answer = i - 1
```

Why?

Because:

```text
1 divides N
2 divides N
...
answer divides N

but

answer + 1 does NOT divide N
```

So the prefix cannot be extended.

---

## 2.4 Dry Runs

### Example A — N = 12

Check from `1`:

| `i` | `12 % i` | Divides? |
|---:|---:|:---:|
| 1 | 0 | Yes |
| 2 | 0 | Yes |
| 3 | 0 | Yes |
| 4 | 0 | Yes |
| 5 | 2 | No |

Stop at:

```text
i = 5
```

Therefore:

```text
answer = 5 - 1 = 4
```

Valid interval:

```text
[1, 4]
```

Check:

```text
1 | 12
2 | 12
3 | 12
4 | 12
```

---

### Example B — N = 990990

The lecture shows that:

```text
9  | 990990
10 | 990990
11 | 990990
```

so:

```text
[9,10,11]
```

is a valid interval of length `3`.

The transformation tells us:

```text
a valid interval of length 3 exists
        |
        v
we can reason using [1,2,3]
```

Indeed:

```text
1 | 990990
2 | 990990
3 | 990990
```

The important idea is the **length**, not the original starting point.

---

### Example C — Stop at the First Failure

Suppose:

```text
N % 1 == 0
N % 2 == 0
N % 3 == 0
N % 4 != 0
```

Then:

```text
[1,2,3] -> valid
[1,2,3,4] -> invalid
```

So:

```text
answer = 3
```

We stop immediately.

---

## 2.5 C++

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int T;
    cin >> T;

    while (T--) {
        long long n;
        cin >> n;

        long long i = 1;

        while (n % i == 0) {
            ++i;
        }

        cout << i - 1 << '\n';
    }
}
```

The loop is tiny even when `N` is very large.

Why?

If:

```text
1,2,3,...,k
```

all divide `N`, then:

```text
LCM(1,2,...,k) <= N
```

The LCM grows quickly, so `k` cannot become large for normal 64-bit constraints.

---

## 2.6 Don't-Memorize Model

Do not memorize:

```text
start checking divisibility from 1
```

Derive it:

```text
Need longest interval of consecutive divisors
        |
        v
Suppose some interval has length k
        |
        v
only its length matters
        |
        v
valid length k implies 1..k divide N
        |
        v
check prefix:
1, 2, 3, ... until first failure
```

Recognition:

```text
longest consecutive divisors
+
N is fixed
        |
        v
try to transform arbitrary interval
into a prefix starting from 1
```

---

# 3. Array Balancing

**Problem:** Array Balancing  
**Problem Link:** https://codeforces.com/contest/1661/problem/A

---

## 3.1 What the Problem Is Asking

We have two arrays:

```text
a[0], a[1], ..., a[n-1]
b[0], b[1], ..., b[n-1]
```

At each index `i`, we may swap:

```text
a[i] <-> b[i]
```

The objective is to minimize the total adjacent-difference cost:

```text
|a[0]-a[1]| + |a[1]-a[2]| + ...
+
|b[0]-b[1]| + |b[1]-b[2]| + ...
```

A naive idea is:

```text
try every combination of swaps
```

There are:

```text
2^n
```

possible configurations.

That is unnecessary.

---

## 3.2 Local Contribution

Look only at two neighboring columns:

```text
index       i              i+1

array a    a[i] ---------- a[i+1]

array b    b[i] ---------- b[i+1]
```

Only two edges contribute between these columns.

So instead of thinking about the entire arrays, calculate the cost of this boundary.

This is the key modeling step.

---

## 3.3 Swap vs No Swap

For neighboring positions `i` and `i+1`, there are two meaningful pairings.

### Option 1 — Straight / No Cross

```text
a[i] ---- a[i+1]
b[i] ---- b[i+1]
```

Cost:

```text
straight =
|a[i] - a[i+1]|
+
|b[i] - b[i+1]|
```

### Option 2 — Cross

```text
a[i] ---- b[i+1]
b[i] ---- a[i+1]
```

Cost:

```text
cross =
|a[i] - b[i+1]|
+
|b[i] - a[i+1]|
```

For this boundary, choose:

```text
min(straight, cross)
```

So:

```text
answer += min(
    |a[i]-a[i+1]| + |b[i]-b[i+1]|,
    |a[i]-b[i+1]| + |b[i]-a[i+1]|
)
```

---

## 3.4 Why Greedy Works

The important observation is that swapping an entire column:

```text
(a[i], b[i])
```

does not change the unordered pair stored at that column.

For one boundary, we only care about how the two values in the left column are paired with the two values in the right column.

There are only two possibilities:

```text
straight
cross
```

Therefore each adjacent boundary contributes the cheaper of those two pairings.

The total is a sum of these local boundary contributions:

```text
answer =
best boundary 0-1
+
best boundary 1-2
+
...
+
best boundary n-2 to n-1
```

So we do not need to enumerate all `2^n` global swap configurations.

---

## 3.5 Dry Run

Take:

```text
a = [1, 7, 4]
b = [3, 2, 6]
```

We process one boundary at a time.

### Boundary 0 -> 1

Values:

```text
left column       right column

a[0] = 1          a[1] = 7
b[0] = 3          b[1] = 2
```

#### Straight

```text
1 ---- 7
3 ---- 2
```

Cost:

```text
|1 - 7| + |3 - 2|
= 6 + 1
= 7
```

#### Cross

```text
1 ---- 2
3 ---- 7
```

Cost:

```text
|1 - 2| + |3 - 7|
= 1 + 4
= 5
```

Choose:

```text
min(7,5) = 5
```

Current answer:

```text
ans = 5
```

---

### Boundary 1 -> 2

Values:

```text
left column       right column

a[1] = 7          a[2] = 4
b[1] = 2          b[2] = 6
```

#### Straight

```text
7 ---- 4
2 ---- 6
```

Cost:

```text
|7 - 4| + |2 - 6|
= 3 + 4
= 7
```

#### Cross

```text
7 ---- 6
2 ---- 4
```

Cost:

```text
|7 - 6| + |2 - 4|
= 1 + 2
= 3
```

Choose:

```text
min(7,3) = 3
```

Final:

```text
ans = 5 + 3
    = 8
```

### Dry-run table

| Boundary | Straight | Cross | Take |
|---|---:|---:|---:|
| `0 -> 1` | 7 | 5 | 5 |
| `1 -> 2` | 7 | 3 | 3 |

```text
answer = 5 + 3 = 8
```

---

## 3.6 C++

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int T;
    cin >> T;

    while (T--) {
        int n;
        cin >> n;

        vector<long long> a(n), b(n);

        for (auto &x : a) cin >> x;
        for (auto &x : b) cin >> x;

        long long ans = 0;

        for (int i = 0; i + 1 < n; ++i) {
            long long straight =
                llabs(a[i] - a[i + 1]) +
                llabs(b[i] - b[i + 1]);

            long long cross =
                llabs(a[i] - b[i + 1]) +
                llabs(b[i] - a[i + 1]);

            ans += min(straight, cross);
        }

        cout << ans << '\n';
    }
}
```

Complexity:

```text
Time  : O(n)
Space : O(n)
```

---

## 3.7 Don't-Memorize Model

Do not memorize the four absolute-value terms.

Draw two columns:

```text
a[i]        a[i+1]
  o----------o
  o----------o
b[i]        b[i+1]
```

There are only two pairings:

```text
STRAIGHT

a[i] ------ a[i+1]
b[i] ------ b[i+1]
```

or:

```text
CROSS

a[i] ------ b[i+1]
b[i] ------ a[i+1]
```

Then calculate both.

Recognition:

```text
two arrays
+
swap values inside each column
+
objective = sum of adjacent costs
        |
        v
focus on ONE boundary
        |
        v
two possible pairings
        |
        v
straight vs cross
        |
        v
take minimum contribution
```

---

# 4. Final Recognition Sheet

## Problem 1 — Vlad and Candies

```text
Repeatedly choose different types
        |
        v
What can block us?
        |
        v
one type dominates
        |
        v
look at largest frequencies
        |
        v
largest - second largest <= 1
```

Core skill:

```text
feasibility from extreme values
```

---

## Problem 2 — Longest Divisors Interval

```text
Need longest consecutive divisor interval
        |
        v
arbitrary [l,r] looks expensive
        |
        v
focus on interval LENGTH
        |
        v
length k can be represented by [1..k]
        |
        v
check:
N%1, N%2, N%3, ...
until first failure
```

Core skill:

```text
transform arbitrary interval -> canonical prefix
```

---

## Problem 3 — Array Balancing

```text
2^n possible swaps
        |
        v
global brute force looks impossible
        |
        v
objective is a SUM
        |
        v
inspect one local boundary
        |
        v
only two pairings:
straight / cross
        |
        v
take cheaper local contribution
```

Core skill:

```text
global sum -> local contribution
```

---

# Master Don't-Memorize Model

When a new CP problem looks complicated:

```text
1. What exactly is being asked?
            |
            v
2. Remove the story.
   Define variables.
            |
            v
3. What makes the answer impossible
   or expensive?
            |
            v
4. Can the global problem be reduced to:
      - extremes?
      - a canonical form?
      - local contributions?
            |
            v
5. Derive the condition.
            |
            v
6. Dry run normal + edge cases.
            |
            v
7. Only then write C++.
```

For these three problems:

```text
Vlad and Candies
    -> EXTREMES

Longest Divisors Interval
    -> CANONICAL PREFIX

Array Balancing
    -> LOCAL CONTRIBUTION
```

That classification is more reusable than memorizing any individual solution.
