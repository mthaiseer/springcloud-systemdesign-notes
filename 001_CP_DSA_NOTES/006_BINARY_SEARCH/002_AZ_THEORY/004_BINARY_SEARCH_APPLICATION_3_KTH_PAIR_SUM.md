# AlgoZenith — Binary Search Application 4
## K-th Smallest Pair Sum from Two Arrays
### TLE-Style Self-Study Notes

> **Source:** supplied AlgoZenith lecture PDF `004_Binary Search Application idea 3.pdf`.
>
> The lecture page itself is titled **Binary Search — Application 4**.
>
> Main form:
>
> ```text
> Array A of size N
> Array B of size M
>
> Build conceptual array C:
> C contains every A[i] + B[j]
>
> Size of C = N * M
>
> Find the K-th smallest value in C.
> ```
>
> We do **not** explicitly build all `N*M` sums.
>
> Core idea:
>
> ```text
> Binary Search on the answer X
> +
> count how many pair sums are <= X
> ```

---

# Table of Contents

- [1. What the Problem Is Asking](#1-what-the-problem-is-asking)
- [2. Small Example First](#2-small-example-first)
- [3. Story Removed](#3-story-removed)
- [4. Why Building All Pair Sums Is Expensive](#4-why-building-all-pair-sums-is-expensive)
- [5. Binary Search Formulation](#5-binary-search-formulation)
- [6. `check(x)` — What Does It Mean?](#6-checkx--what-does-it-mean)
- [7. Math Derivation of the Count](#7-math-derivation-of-the-count)
- [8. Why `upper_bound` Appears](#8-why-upper_bound-appears)
- [9. Monotonicity](#9-monotonicity)
- [10. Search Space](#10-search-space)
- [11. Compact Checker Dry Run](#11-compact-checker-dry-run)
- [12. Compact Binary Search Dry Run](#12-compact-binary-search-dry-run)
- [13. Why Iterate the Smaller Array](#13-why-iterate-the-smaller-array)
- [14. Properly Formatted C++17](#14-properly-formatted-c17)
- [15. Complexity](#15-complexity)
- [16. Common Mistakes](#16-common-mistakes)
- [17. Similar Product Form](#17-similar-product-form)
- [18. Recognition Pattern](#18-recognition-pattern)
- [19. Final Mental Model](#19-final-mental-model)

---

# 1. What the Problem Is Asking

We are given two arrays:

```text
A → N elements
B → M elements
```

We conceptually create all possible pairs:

```text
(A[i], B[j])
```

and from every pair create:

```text
A[i] + B[j]
```

So conceptual array `C` contains:

```text
N * M
```

values.

The task is:

```text
Find the K-th smallest value in C.
```

---

# 2. Small Example First

Lecture example:

```text
A = [1,2,3]
B = [4,5,6]
```

All pair sums:

```text
1+4 = 5
1+5 = 6
1+6 = 7

2+4 = 6
2+5 = 7
2+6 = 8

3+4 = 7
3+5 = 8
3+6 = 9
```

Conceptual matrix:

```text
          B
          4   5   6
        +---+---+---+
A = 1   | 5 | 6 | 7 |
        +---+---+---+
A = 2   | 6 | 7 | 8 |
        +---+---+---+
A = 3   | 7 | 8 | 9 |
        +---+---+---+
```

So:

```text
C = [5,6,7,6,7,8,7,8,9]
```

After sorting:

```text
sorted C =
[5,6,6,7,7,7,8,8,9]
```

For:

```text
K = 6
```

the answer is:

```text
7
```

because the 6th smallest value is `7`.

---

# 3. Story Removed

The problem becomes:

```text
Given:
sorted A[0...N-1]
sorted B[0...M-1]
K

Consider all values:

A[i] + B[j]

Find the K-th smallest generated value.
```

Instead of generating all values, convert the K-th problem into a counting problem:

```text
For candidate X:

How many pair sums are <= X?
```

Then:

```text
if count >= K
    K-th smallest <= X

else
    K-th smallest > X
```

That gives Binary Search.

---

# 4. Why Building All Pair Sums Is Expensive

The conceptual array has:

```text
N * M
```

values.

Direct method:

```text
generate all pair sums
sort them
take K-th
```

requires roughly:

```text
O(N*M)
```

memory just to store them.

The lecture instead avoids constructing `C`.

We only need to answer:

```text
count(pair sums <= X)
```

for a guessed value `X`.

---

# 5. Binary Search Formulation

We do not Binary Search an index.

We Binary Search the possible value of the answer.

Guess:

```text
X = mid
```

Question:

```text
Is the K-th smallest pair sum <= X?
```

Equivalent counting question:

```text
Are there at least K pair sums <= X?
```

So:

```text
check(X)
=
count(
    A[i] + B[j] <= X
) >= K
```

---

# 6. `check(x)` — What Does It Mean?

The lecture writes the checker idea as:

```text
# of elements in C <= X
is >= K
```

So:

```text
check(X) = true
```

means:

```text
there are already at least K generated values
not larger than X
```

Therefore the K-th smallest value is:

```text
<= X
```

and we should search left for a smaller valid value.

If:

```text
check(X) = false
```

then fewer than `K` values are `<= X`, so:

```text
answer > X
```

and we search right.

---

# 7. Math Derivation of the Count

For a fixed element:

```text
A[i]
```

we need all `B[j]` satisfying:

```text
A[i] + B[j] <= X
```

Move `A[i]` to the other side:

```text
B[j] <= X - A[i]
```

This is the key algebra step.

So for each `A[i]`:

```text
count how many values in B
are <= X - A[i]
```

Then add those counts.

Formula:

```text
count(X)
=
Σ count(
    B[j] <= X - A[i]
)
```

for all `i`.

---

## Tiny Example

Take:

```text
A = [1,2,3]
B = [4,5,6]
X = 7
```

For:

```text
A[0] = 1
```

need:

```text
B[j] <= 7 - 1
B[j] <= 6
```

All:

```text
4,5,6
```

work.

Contribution:

```text
3
```

For:

```text
A[1] = 2
```

need:

```text
B[j] <= 5
```

Values:

```text
4,5
```

Contribution:

```text
2
```

For:

```text
A[2] = 3
```

need:

```text
B[j] <= 4
```

Value:

```text
4
```

Contribution:

```text
1
```

Total:

```text
3 + 2 + 1
=
6
```

Therefore:

```text
count(7) = 6
```

If:

```text
K = 6
```

then:

```text
check(7) = true
```

---

# 8. Why `upper_bound` Appears

After sorting `B`, we need:

```text
number of elements <= target
```

where:

```text
target = X - A[i]
```

C++:

```cpp
upper_bound(b.begin(), b.end(), target)
```

returns an iterator to:

```text
first element > target
```

Therefore:

```cpp
upper_bound(b.begin(), b.end(), target) - b.begin()
```

equals:

```text
number of elements <= target
```

---

## Dry Run

```text
B = [4,5,6]
target = 5
```

`upper_bound(B,5)` points after `5`:

```text
B = [4,5,6]
         ^
         first > 5 is 6
```

Number of elements before that position:

```text
2
```

Exactly the number of values:

```text
<= 5
```

---

# 9. Monotonicity

Suppose:

```text
X = 7
```

already has:

```text
count(X) >= K
```

Then for any larger value:

```text
X+1, X+2, ...
```

the condition cannot lose valid pair sums.

So:

```text
count(X)
```

is non-decreasing as `X` increases.

For the lecture example with `K = 6`:

```text
X:        4 5 6 7 8 9 ...
count>=K: 0 0 0 1 1 1 ...
                    ^
                    |
                first True
```

We need:

```text
minimum X such that count(X) >= K
```

That minimum is exactly the K-th smallest pair sum.

---

# 10. Search Space

After sorting both arrays:

Smallest possible pair sum:

```text
A[0] + B[0]
```

Largest possible pair sum:

```text
A[N-1] + B[M-1]
```

So:

```text
low  = A[0] + B[0]
high = A[N-1] + B[M-1]
```

For:

```text
A = [1,2,3]
B = [4,5,6]
```

we get:

```text
low  = 1+4 = 5
high = 3+6 = 9
```

Search only:

```text
[5,9]
```

---

# 11. Compact Checker Dry Run

Use:

```text
A = [1,2,3]
B = [4,5,6]
K = 6
```

---

## Check `X = 6`

```text
A=1 → B <= 5 → {4,5} → 2
A=2 → B <= 4 → {4}   → 1
A=3 → B <= 3 → {}    → 0

count = 2+1+0 = 3
```

Since:

```text
3 < 6
```

therefore:

```text
check(6) = false
```

---

## Check `X = 7`

```text
A=1 → B <= 6 → {4,5,6} → 3
A=2 → B <= 5 → {4,5}   → 2
A=3 → B <= 4 → {4}     → 1

count = 3+2+1 = 6
```

Since:

```text
6 >= K
```

therefore:

```text
check(7) = true
```

So:

```text
6 → false
7 → true
```

Answer:

```text
7
```

---

# 12. Compact Binary Search Dry Run

Search range:

```text
low = 5
high = 9
```

Dry run:

```text
Iter 1: low=5, high=9 → mid=7 → count=6 >=6 → YES → ans=7 → high=6
Iter 2: low=5, high=6 → mid=5 → count=1 <6  → NO  → low=6
Iter 3: low=6, high=6 → mid=6 → count=3 <6  → NO  → low=7

answer = 7
```

---

# 13. Why Iterate the Smaller Array

The lecture notes:

```text
iterate on smaller array
and binary search on larger array
```

Suppose:

```text
N <= M
```

Then for each element of `A`:

```text
upper_bound in B
```

costs:

```text
O(log M)
```

Repeating for `N` elements:

```text
O(N log M)
```

If instead `N > M`, swap arrays.

So the code makes:

```text
N <= M
```

before counting.

This keeps each checker cheaper.

---

# 14. Properly Formatted C++17

The lecture code below is cleaned into the same compact C++17 format used in the earlier notes, while keeping the same algorithm.

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

bool check(ll x, const vector<ll>& a, const vector<ll>& b, ll k) {
    ll count = 0;

    for (ll valueA : a) {
        ll maxB = x - valueA;

        count += upper_bound(b.begin(), b.end(), maxB) - b.begin();

        if (count >= k) {
            return true;
        }
    }

    return false;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;

    while (t--) {
        int n, m;
        ll k;
        cin >> n >> m >> k;

        vector<ll> a(n);
        vector<ll> b(m);

        for (ll& x : a) {
            cin >> x;
        }

        for (ll& x : b) {
            cin >> x;
        }

        if (n > m) {
            swap(n, m);
            swap(a, b);
        }

        sort(a.begin(), a.end());
        sort(b.begin(), b.end());

        ll low = a.front() + b.front();
        ll high = a.back() + b.back();
        ll answer = high;

        while (low <= high) {
            ll mid = low + (high - low) / 2;

            if (check(mid, a, b, k)) {
                answer = mid;
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        cout << answer << '\n';
    }

    return 0;
}
```

---

## Core Counting Line

The most important line is:

```cpp
count += upper_bound(b.begin(), b.end(), x - valueA) - b.begin();
```

Read it as:

```text
For this A[i],

count how many B[j] satisfy:

B[j] <= X - A[i]

which is equivalent to:

A[i] + B[j] <= X
```

---

# 15. Complexity

Assume after possible swapping:

```text
N <= M
```

Sorting:

```text
O(N log N + M log M)
```

One checker:

```text
N iterations
*
upper_bound on M elements
```

So:

```text
O(N log M)
```

Binary Search over answer values:

```text
O(log(valueRange))
```

Therefore:

```text
O(
    N log N
    +
    M log M
    +
    N log M * log(valueRange)
)
```

Memory:

```text
O(N + M)
```

for the two input arrays.

Most importantly, we avoid:

```text
O(N*M)
```

memory for the conceptual pair-sum array.

---

# 16. Common Mistakes

## Mistake 1 — Building `C`

Wrong:

```text
for every i
    for every j
        C.push_back(A[i]+B[j])

sort(C)
```

This can create:

```text
N*M
```

values.

The lecture's intended idea is:

```text
count values <= X
without building C
```

---

## Mistake 2 — Counting `< X` Instead of `<= X`

We need:

```text
number of pair sums <= X
```

That is why:

```cpp
upper_bound(...)
```

fits naturally.

---

## Mistake 3 — Forgetting the Algebra Step

Start:

```text
A[i] + B[j] <= X
```

Rearrange:

```text
B[j] <= X - A[i]
```

That transformation is what turns the 2D pair problem into a 1D binary-search query on `B`.

---

## Mistake 4 — Searching Right After `check(mid)=true`

We want:

```text
minimum X
such that count(X) >= K
```

Therefore when `mid` works:

```cpp
answer = mid;
high = mid - 1;
```

Search left.

---

## Mistake 5 — Wrong Search Bounds

Use actual possible pair-sum values:

```text
low  = minA + minB
high = maxA + maxB
```

after sorting.

---

## Mistake 6 — Iterating the Larger Array

The lecture explicitly suggests:

```text
iterate smaller array
binary-search larger array
```

This gives:

```text
min(N,M) * log(max(N,M))
```

work per checker.

---

## Mistake 7 — Using Only `int`

Pair sums and counts can become large.

Using:

```cpp
long long
```

for:

```text
K
pair sums
mid
count
```

is safer.

---

# 17. Similar Product Form

The lecture notes that a similar problem can be created using:

```text
C[i][j] = A[i] * B[j]
```

instead of:

```text
A[i] + B[j]
```

The same high-level idea remains:

```text
Binary Search candidate X
+
count generated values <= X
```

The exact counting derivation depends on the product constraints/signs.

So the important transferable pattern is:

```text
huge implicit pair space
+
K-th smallest
→ count <= X
→ Binary Search X
```

---

# 18. Recognition Pattern

When a problem says:

```text
two arrays
+
consider every pair
+
generated value = f(A[i], B[j])
+
find K-th smallest
```

ask:

```text
Can I avoid generating all N*M values?
```

Then try:

```text
candidate X
→ count how many generated values <= X
```

For pair sums:

```text
A[i] + B[j] <= X
```

becomes:

```text
B[j] <= X - A[i]
```

which is directly countable with:

```text
upper_bound
```

So recognition:

```text
K-th smallest pair sum
→ Binary Search on answer
→ count <= X
→ upper_bound
```

---

# 19. Final Mental Model

```text
A size N
B size M
        |
        v
conceptual N*M pair sums
        |
        v
need K-th smallest
        |
        v
DO NOT build all sums
        |
        v
guess answer X
        |
        v
count pairs:
A[i] + B[j] <= X
        |
        v
rearrange:
B[j] <= X - A[i]
        |
        v
upper_bound in B
for every A[i]
        |
        v
count >= K ?
    /        \
  NO          YES
  |            |
  v            v
search       save X
right        search left
        |
        v
first X with count >= K
        |
        v
K-th smallest pair sum
```

The one derivation to remember is:

```text
A[i] + B[j] <= X

⇔

B[j] <= X - A[i]
```

The one checker to remember is:

```text
count(pair sums <= X) >= K
```

And the one recognition sentence is:

```text
K-th smallest in a huge implicit pair space
→ Binary Search the value
→ count how many generated values are <= it.
```
