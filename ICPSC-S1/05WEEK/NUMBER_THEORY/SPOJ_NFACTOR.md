# SPOJ NFACTOR — Visual Learning Note
## Sieve + Range Loop + Bucketing + Binary Search

**Core pattern:**

```text
Many queries about a STATIC property
            ↓
Precompute property with sieve
            ↓
Option 1: scan [A,B]
            ↓
Option 2: bucket by property
            ↓
Binary search inside sorted bucket
```

---

# 1. Problem in Simple Words

For every query, we are given:

```text
A B N
```

We need to count how many numbers `x` in:

```text
A <= x <= B
```

have exactly `N` **distinct prime factors**.

Example:

```text
6 = 2 × 3
```

Distinct prime factors:

```text
{2,3}
```

So:

```text
factorCount[6] = 2
```

Another example:

```text
12 = 2² × 3
```

The prime `2` appears twice, but we count it only once.

Distinct prime factors:

```text
{2,3}
```

Therefore:

```text
factorCount[12] = 2
```

---

# 2. What Does "Distinct Prime Factors" Mean?

```text
Number     Factorization       Distinct primes       Count
-----------------------------------------------------------
2          2                   {2}                     1
4          2²                  {2}                     1
6          2 × 3               {2,3}                   2
8          2³                  {2}                     1
10         2 × 5               {2,5}                   2
12         2² × 3              {2,3}                   2
30         2 × 3 × 5           {2,3,5}                 3
60         2² × 3 × 5          {2,3,5}                 3
```

Important:

```text
Exponent does NOT matter.

12 = 2² × 3

2 contributes +1
3 contributes +1

answer = 2
```

---

# 3. Brute Force Idea

For every query:

```text
[A,B,N]
```

we could do:

```text
for x = A to B:
    factorize x
    count distinct prime factors
    if count == N:
        answer++
```

ASCII:

```text
A     A+1     A+2     A+3              B
│      │       │       │               │
▼      ▼       ▼       ▼               ▼
factor factor  factor  factor   ...   factor
│
▼
count distinct primes
│
▼
== N ?
```

The problem:

```text
We repeatedly factorize the same numbers
for different queries.
```

That is wasted work.

---

# 4. First Observation — The Property Is Static

For a number `x`:

```text
number of distinct prime factors of x
```

never changes.

For example:

```text
factorCount[12] = 2
```

for every query forever.

So instead of recalculating it:

```text
QUERY
  ↓
factorize x
```

do:

```text
PRECOMPUTE ONCE
      ↓
factorCount[x]
      ↓
reuse for every query
```

This is the first major pattern:

> **Many queries + static property → precompute.**

---

# 5. How Can Sieve Calculate the Property?

Recall the normal sieve idea.

If `p` is prime, its multiples are:

```text
p
2p
3p
4p
5p
...
```

Every one of those numbers contains `p` as a prime factor.

Example for `p = 2`:

```text
2,4,6,8,10,12,...
```

Every number there has prime factor `2`.

So instead of merely marking the multiples, we do:

```text
factorCount[multiple]++
```

Mental model:

```text
             PRIME p
                │
                ▼
      visit every multiple
                │
       ┌────────┼────────┐
       ▼        ▼        ▼
       p        2p       3p ...
       │        │        │
      +1       +1       +1

"p is one distinct prime factor
 of every number I visit."
```

---

# 6. Visual Sieve Dry Run Up to 12

Start:

```text
number:       2  3  4  5  6  7  8  9 10 11 12
count:        0  0  0  0  0  0  0  0  0  0  0
```

## Prime 2

Visit:

```text
2,4,6,8,10,12
```

Add `+1`:

```text
number:       2  3  4  5  6  7  8  9 10 11 12
count:        1  0  1  0  1  0  1  0  1  0  1
```

Visual:

```text
prime 2
  │
  ├──→ 2   +1
  ├──→ 4   +1
  ├──→ 6   +1
  ├──→ 8   +1
  ├──→ 10  +1
  └──→ 12  +1
```

## Prime 3

Visit:

```text
3,6,9,12
```

After incrementing:

```text
number:       2  3  4  5  6  7  8  9 10 11 12
count:        1  1  1  0  2  0  1  1  1  0  2
```

Notice:

```text
6 was visited by 2 and 3
→ count[6] = 2

12 was visited by 2 and 3
→ count[12] = 2
```

## Prime 5

Visit:

```text
5,10
```

Now:

```text
5  → 1
10 → 2
```

## Prime 7

```text
7 → 1
```

## Prime 11

```text
11 → 1
```

Final:

```text
number:       2  3  4  5  6  7  8  9 10 11 12
count:        1  1  1  1  2  1  1  1  2  1  2
```

---

# 7. Why Powers Are Counted Correctly

Consider:

```text
12 = 2² × 3
```

When processing prime `2`, we visit `12` exactly once:

```text
2 → 4 → 6 → 8 → 10 → 12
```

So `2` contributes:

```text
+1
```

not:

```text
+2
```

Then prime `3` visits `12` once:

```text
+1
```

Therefore:

```text
factorCount[12] = 2
```

which represents:

```text
{2,3}
```

Exactly what we need.

---

# 8. How Do We Detect a Prime?

Initially:

```text
factorCount[x] = 0
```

Suppose we reach `p` and:

```text
factorCount[p] == 0
```

No smaller prime has divided `p`.

Therefore `p` itself must be prime.

So:

```cpp
for (int p = 2; p <= MAXN; p++) {

    if (factorCount[p] == 0) {

        // p is prime

        for (int multiple = p;
             multiple <= MAXN;
             multiple += p) {

            factorCount[multiple]++;
        }
    }
}
```

Mental model:

```text
factorCount[p] == 0
          ↓
no smaller prime divided p
          ↓
p is prime
          ↓
visit all multiples of p
          ↓
increment their distinct-factor count
```

---

# 9. Solution Level 1 — Sieve + Loop From A to B

After the sieve, we know:

```text
factorCount[x]
```

for every possible `x`.

For a query:

```text
A = 2
B = 12
N = 2
```

scan:

```text
x     factorCount[x]      Match N=2?
-------------------------------------
2          1                 ✗
3          1                 ✗
4          1                 ✗
5          1                 ✗
6          2                 ✓
7          1                 ✗
8          1                 ✗
9          1                 ✗
10         2                 ✓
11         1                 ✗
12         2                 ✓
```

Answer:

```text
3
```

The matching numbers are:

```text
6,10,12
```

---

# 10. C++ — Sieve + Range Loop

```cpp
#include <bits/stdc++.h>
using namespace std;

const int MAXN = 1000000;

int factorCount[MAXN + 1];

int main() {

    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    // Precompute distinct prime-factor counts.
    for (int p = 2; p <= MAXN; p++) {

        if (factorCount[p] == 0) {

            // p is prime.
            for (int multiple = p;
                 multiple <= MAXN;
                 multiple += p) {

                factorCount[multiple]++;
            }
        }
    }

    int T;
    cin >> T;

    while (T--) {

        int A, B, N;
        cin >> A >> B >> N;

        int answer = 0;

        for (int x = A; x <= B; x++) {

            if (factorCount[x] == N) {
                answer++;
            }
        }

        cout << answer << '\n';
    }
}
```

---

# 11. What Is Still Slow?

We removed repeated factorization.

But for every query we still do:

```text
A → A+1 → A+2 → ... → B
```

Example:

```text
Query 1: scan 1 ........ 1,000,000
Query 2: scan 1 ........ 1,000,000
Query 3: scan 1 ........ 1,000,000
...
```

ASCII:

```text
SIEVE solved:
"How many factors does x have?"

But query still asks:

"Check every x in [A,B]."
```

Can we remove this scan too?

Yes.

---

# 12. Second Observation — Bucket Numbers by Their Property

After the sieve we have mappings like:

```text
2  → 1
3  → 1
4  → 1
5  → 1
6  → 2
7  → 1
8  → 1
9  → 1
10 → 2
11 → 1
12 → 2
```

Instead of asking:

```text
"What is factorCount[x]?"
```

reverse the relationship:

```text
"Which x values have factor count k?"
```

Build:

```text
numbersWith[k]
```

where:

```text
numbersWith[k]
=
all numbers having exactly k distinct prime factors
```

Example:

```text
numbersWith[1]
=
[2,3,4,5,7,8,9,11,...]

numbersWith[2]
=
[6,10,12,...]

numbersWith[3]
=
[30,42,60,...]
```

ASCII:

```text
                 ALL NUMBERS
                      │
              factorCount[x]
                      │
       ┌──────────────┼───────────────┐
       ▼              ▼               ▼
   1 factor        2 factors       3 factors

[2,3,4,...]      [6,10,12,...]   [30,42,60,...]
```

---

# 13. Why Are the Buckets Sorted?

Build them like this:

```cpp
for (int x = 2; x <= MAXN; x++) {
    numbersWith[factorCount[x]].push_back(x);
}
```

We process:

```text
x = 2,3,4,5,6,7,8,...
```

in increasing order.

Therefore every vector automatically receives its elements in increasing order.

Example:

```text
numbersWith[2]

6 → 10 → 12 → 14 → 15 → 18 → ...
```

Sorted data means:

```text
BINARY SEARCH
```

becomes available.

---

# 14. Transform the Query

Original query:

```text
How many x in [A,B]
have factorCount[x] == N?
```

Instead of searching every number in `[A,B]`:

```text
A A+1 A+2 ... B
```

go directly to:

```text
numbersWith[N]
```

because every value in this vector already satisfies:

```text
factorCount[x] == N
```

Now the only question is:

> How many elements of this sorted vector lie in `[A,B]`?

That is a binary-search range-count problem.

---

# 15. lower_bound and upper_bound

Suppose:

```text
numbersWith[2] =
[6,10,12,14,15,18,20]
```

Query:

```text
A = 10
B = 18
N = 2
```

We need:

```text
[10,12,14,15,18]
```

ASCII:

```text
6   10   12   14   15   18   20
    ↑                        ↑
    A                        B

    |------ valid -------|
```

Use:

```cpp
lower_bound(v.begin(), v.end(), A)
```

Meaning:

```text
first value >= A
```

Here:

```text
lower_bound(10)
      ↓
     10
```

Use:

```cpp
upper_bound(v.begin(), v.end(), B)
```

Meaning:

```text
first value > B
```

Here:

```text
upper_bound(18)
                              ↓
                             20
```

Therefore:

```text
6  [10  12  14  15  18]  20
    ↑                   ↑
   left                right

answer = right - left
       = 5
```

Formula:

```cpp
answer =
    upper_bound(v.begin(), v.end(), B)
  - lower_bound(v.begin(), v.end(), A);
```

---

# 16. Why `upper_bound(B) - lower_bound(A)` Works

Think in terms of iterator positions.

```text
index:   0   1   2   3   4   5   6
value:   6  10  12  14  15  18  20
             ↑                   ↑
          lower(A)            upper(B)

position =   1                   6
```

Then:

```text
6 - 1 = 5
```

There are five values:

```text
10,12,14,15,18
```

So remember:

```text
Inclusive range [A,B]:

LEFT  = first >= A
RIGHT = first > B

COUNT = RIGHT - LEFT
```

---

# 17. Binary Search Query Dry Run

Suppose:

```text
v = numbersWith[2]

v = [6,10,12,14,15,18,20,21,22,26]
```

Query:

```text
A = 11
B = 20
N = 2
```

Find:

```text
lower_bound(11)
```

First value `>= 11`:

```text
12
```

Find:

```text
upper_bound(20)
```

First value `> 20`:

```text
21
```

Visual:

```text
6  10  [12  14  15  18  20]  21  22  26
        ↑                   ↑
     lower(11)           upper(20)
```

Answer:

```text
5
```

---

# 18. Full Optimized Mental Model

```text
QUERY:
count x in [A,B]
with exactly N distinct prime factors

                 │
                 ▼

       PROPERTY IS STATIC

                 │
                 ▼

              SIEVE

for each prime p:
    visit p,2p,3p,...
    factorCount[multiple]++

                 │
                 ▼

       factorCount[x] known

                 │
                 ▼

             BUCKET

numbersWith[k]
=
all x with factorCount[x] == k

                 │
                 ▼

      buckets already sorted

                 │
                 ▼

QUERY (A,B,N)

                 │
                 ▼

v = numbersWith[N]

                 │
          ┌──────┴──────┐
          ▼             ▼
 first value >= A   first value > B
          │             │
    lower_bound(A) upper_bound(B)
          │             │
          └──────┬──────┘
                 ▼

answer =
upper_bound(B) - lower_bound(A)
```

---

# 19. C++ — Sieve + Buckets + Binary Search

```cpp
#include <bits/stdc++.h>
using namespace std;

const int MAXN = 1000000;

int factorCount[MAXN + 1];

// More than enough buckets for the constraints.
vector<int> numbersWith[10];

int main() {

    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    // STEP 1:
    // Sieve to count distinct prime factors.
    for (int p = 2; p <= MAXN; p++) {

        if (factorCount[p] == 0) {

            // p is prime.
            for (int multiple = p;
                 multiple <= MAXN;
                 multiple += p) {

                factorCount[multiple]++;
            }
        }
    }

    // STEP 2:
    // Group numbers by their number of distinct prime factors.
    for (int x = 2; x <= MAXN; x++) {

        int cnt = factorCount[x];

        numbersWith[cnt].push_back(x);
    }

    // STEP 3:
    // Answer queries with binary search.
    int T;
    cin >> T;

    while (T--) {

        int A, B, N;
        cin >> A >> B >> N;

        const vector<int>& v = numbersWith[N];

        auto left =
            lower_bound(v.begin(), v.end(), A);

        auto right =
            upper_bound(v.begin(), v.end(), B);

        cout << right - left << '\n';
    }

    return 0;
}
```

---

# 20. Code ↔ Idea Mapping

## Sieve

Idea:

```text
prime p divides every multiple of p
```

Code:

```cpp
for (int multiple = p;
     multiple <= MAXN;
     multiple += p) {

    factorCount[multiple]++;
}
```

---

## Bucket

Idea:

```text
Put x into the group representing
its number of distinct prime factors.
```

Code:

```cpp
numbersWith[factorCount[x]].push_back(x);
```

---

## Query

Idea:

```text
Go directly to numbers having N factors.
```

Code:

```cpp
const vector<int>& v = numbersWith[N];
```

---

## Range Count

Idea:

```text
first >= A
first > B
difference = number inside [A,B]
```

Code:

```cpp
auto left = lower_bound(v.begin(), v.end(), A);
auto right = upper_bound(v.begin(), v.end(), B);

cout << right - left;
```

---

# 21. Complexity

## Sieve preprocessing

For primes, we visit their multiples:

```text
MAX/2 + MAX/3 + MAX/5 + MAX/7 + ...
```

This is efficient sieve-style preprocessing.

A useful high-level bound is around:

```text
O(MAX log log MAX)
```

for this prime-multiple style preprocessing.

## Building buckets

```text
O(MAX)
```

## Solution 1 — Range Scan

Each query:

```text
O(B-A+1)
```

Worst case can be large.

## Solution 2 — Binary Search

Each query performs two binary searches:

```text
O(log MAX)
```

So the optimized approach is approximately:

```text
Preprocessing:
O(MAX log log MAX)

Queries:
O(T log MAX)
```

---

# 22. Why the Optimized Version Is Better

Level 1:

```text
PRECOMPUTE factorCount
          ↓
query
          ↓
scan every x in [A,B]
```

Level 2:

```text
PRECOMPUTE factorCount
          ↓
BUCKET by factor count
          ↓
query
          ↓
jump directly to correct bucket
          ↓
binary search [A,B]
```

The second optimization removes another repeated operation:

```text
Don't repeatedly inspect numbers
that cannot possibly satisfy N.
```

---

# 23. Underlying Reusable Pattern

This problem teaches more than a sieve.

The full pattern is:

```text
Many queries
     +
Static property of numbers
          │
          ▼
     PRECOMPUTE
          │
          ▼
Can values be grouped by property?
          │
         YES
          ▼
       BUCKET
          │
          ▼
Are bucket values sorted?
          │
         YES
          ▼
    BINARY SEARCH
          │
          ▼
Fast range queries
```

This pattern appears in many CP problems.

---

# 24. Recognition Trigger

When you see:

```text
Many queries:
"How many x in [A,B] satisfy property P?"
```

ask:

```text
1. Is P static?

2. Can I precompute P(x)?

3. Can I group x values by P(x)?

4. Are those groups sorted?

5. Can lower_bound / upper_bound
   answer [A,B]?
```

For NFACTOR:

```text
P(x) = number of distinct prime factors of x
```

So:

```text
Static property
      ↓
Sieve
      ↓
factorCount[x]
      ↓
bucket by count
      ↓
binary search range
```

---

# 25. 10-Second Revision Diagram

```text
Need:
count numbers in [A,B]
with N distinct prime factors

            ↓

SIEVE

prime p
  ↓
multiples of p
  ↓
factorCount[multiple]++

            ↓

BUCKET

numbersWith[k]
=
{x | factorCount[x] = k}

            ↓

QUERY

v = numbersWith[N]

            ↓

lower_bound(A)
upper_bound(B)

            ↓

answer = upper - lower
```

---

# 26. One-Line Revision

> **Many static range queries → sieve the property → bucket numbers by property → binary-search the requested range.**

---

# 27. Active Recall Questions

Before reopening the solution, answer:

```text
1. Why do we need distinct prime factors rather than total factors?

2. Why does visiting multiples of prime p add exactly one
   distinct factor?

3. Why does factorCount[p] == 0 tell us p is prime
   during this sieve?

4. After the sieve, how would I solve a query
   by looping A to B?

5. Why can that still be slow?

6. What does numbersWith[k] store?

7. Why is every bucket automatically sorted?

8. What does lower_bound(A) return?

9. What does upper_bound(B) return?

10. Why does upper_bound(B) - lower_bound(A)
    count the inclusive range [A,B]?

11. Can I reconstruct the full chain?

    sieve
      ↓
    factorCount
      ↓
    bucket
      ↓
    binary search
```

If you can derive both the simple range-loop solution and the binary-search optimization without looking at the code, you understand the pattern.
