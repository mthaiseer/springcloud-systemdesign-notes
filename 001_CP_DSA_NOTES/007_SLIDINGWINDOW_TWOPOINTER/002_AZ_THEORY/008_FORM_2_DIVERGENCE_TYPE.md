# Two Pointers — Form 2: Divergence Type
## Opposite-End Elimination, Container With Most Water, and 3Sum Counting
### TLE-Style Self-Study Notes

> **Source:** supplied Form 2 lecture screenshots.
>
> Form 0 and Form 1 used a window moving left to right.
>
> Form 2 begins differently:
>
> ```text
> tail starts at the LEFT end
> head starts at the RIGHT end
>
> tail  →                 ←  head
> ```
>
> At every step:
>
> ```text
> evaluate current pair
> +
> prove that one endpoint can never lead to a better answer
> +
> discard that endpoint
> ```
>
> The deepest idea is not "two pointers start at opposite ends".
>
> The deepest idea is:
>
> ```text
> SAFE ELIMINATION
> ```

---

# Table of Contents

- [1. What Form 2 Means](#1-what-form-2-means)
- [2. Prerequisite 1 — Pair Search Space](#2-prerequisite-1--pair-search-space)
- [3. Prerequisite 2 — Safe Elimination](#3-prerequisite-2--safe-elimination)
- [4. Prerequisite 3 — Why Ordered Structure Matters](#4-prerequisite-3--why-ordered-structure-matters)
- [5. Core Form 2 Template](#5-core-form-2-template)
- [6. How to Recognize Form 2](#6-how-to-recognize-form-2)
- [7. Problem 1 — Maximum Value of F(i,j)](#7-problem-1--maximum-value-of-fij)
- [8. Brute Force](#8-brute-force)
- [9. What Controls the Score?](#9-what-controls-the-score)
- [10. Why Move the Smaller Endpoint?](#10-why-move-the-smaller-endpoint)
- [11. Formal Elimination Proof](#11-formal-elimination-proof)
- [12. Detailed Dry Run](#12-detailed-dry-run)
- [13. Proper C++17 — Maximum F(i,j)](#13-proper-c17--maximum-fij)
- [14. Container With Most Water Connection](#14-container-with-most-water-connection)
- [15. Prerequisite 4 — Sorted Two-Sum Movement](#15-prerequisite-4--sorted-two-sum-movement)
- [16. Problem 2 — Two Sum in a Sorted Array](#16-problem-2--two-sum-in-a-sorted-array)
- [17. Prerequisite 5 — From Two Sum to Three Sum](#17-prerequisite-5--from-two-sum-to-three-sum)
- [18. Problem 3 — 3Sum / Target Triple](#18-problem-3--3sum--target-triple)
- [19. 3Sum Existence Template](#19-3sum-existence-template)
- [20. Prerequisite 6 — Duplicate Blocks](#20-prerequisite-6--duplicate-blocks)
- [21. Counting 3Sum Triples With Duplicates](#21-counting-3sum-triples-with-duplicates)
- [22. Detailed Duplicate-Counting Example](#22-detailed-duplicate-counting-example)
- [23. Proper C++17 — Count Target Triples](#23-proper-c17--count-target-triples)
- [24. Complexity Analysis](#24-complexity-analysis)
- [25. Common Mistakes](#25-common-mistakes)
- [26. Recognition Table](#26-recognition-table)
- [27. Final Mental Model](#27-final-mental-model)

---

# 1. What Form 2 Means

In Form 0 and Form 1:

```text
tail and head described a moving window
```

Form 2 is different.

Initial positions:

```text
tail = 0
head = n - 1
```

ASCII:

```text
index:  0   1   2   3   4   5   6   7

        TAIL ---------------------- HEAD
          |                           |
          v                           v
         [a] [b] [c] [d] [e] [f] [g] [h]
```

Pointers move:

```text
toward each other
```

The loop usually looks like:

```cpp
while (tail < head) {
    useCurrentPair();

    if (leftEndpointCanBeDiscarded()) {
        tail++;
    } else {
        head--;
    }
}
```

The important part is the proof behind:

```text
which endpoint is safe to discard?
```

---

# 2. Prerequisite 1 — Pair Search Space

For `N` elements, the number of pairs:

```text
(i,j), i < j
```

is:

```text
N(N-1) / 2
```

Example:

```text
N = 5
```

Pairs:

```text
(0,1) (0,2) (0,3) (0,4)
      (1,2) (1,3) (1,4)
            (2,3) (2,4)
                  (3,4)
```

Count:

```text
10
=
5*4/2
```

A brute-force pair solution is therefore:

```text
O(N²)
```

Form 2 tries to reduce the two-dimensional pair search space into one monotonic path.

Instead of exploring every pair:

```text
(i,j)
```

we evaluate only about:

```text
O(N)
```

pairs by eliminating one endpoint at every step.

---

# 3. Prerequisite 2 — Safe Elimination

Suppose current pair is:

```text
(tail, head)
```

After evaluating it, we want to move one pointer.

But we may move it only if we can prove:

```text
Every future pair using this discarded endpoint
cannot beat what we have already considered.
```

That is the Form 2 invariant.

---

## Wrong Mental Model

```text
"Array has two ends,
so I will use two pointers."
```

Not enough.

---

## Correct Mental Model

```text
"I can compare the endpoints,
and that comparison proves one entire family of pairs is useless."
```

That is when Form 2 is justified.

---

# 4. Prerequisite 3 — Why Ordered Structure Matters

Many Form 2 problems depend on ordering.

Example:

```text
sorted array
+
pair sum target
```

If:

```text
arr[tail] + arr[head] < target
```

then decreasing `head` makes the sum even smaller.

So the only useful move is:

```text
tail++
```

Likewise if:

```text
sum > target
```

the only useful move is:

```text
head--
```

Sorted order gives the elimination proof.

Other Form 2 problems may not require the array to be sorted, but they need some other ordered structure or inequality that proves one endpoint is dominated.

---

# 5. Core Form 2 Template

```cpp
int tail = 0;
int head = n - 1;

while (tail < head) {
    // 1. Use the current pair.

    // 2. Prove which endpoint is useless.

    if (shouldMoveTail()) {
        tail++;
    } else {
        head--;
    }
}
```

Loop condition:

```text
tail < head
```

because the current state represents a pair of two different positions.

---

# 6. How to Recognize Form 2

Consider Form 2 when:

```text
1. The answer depends on a pair of positions.

2. Candidates have an ordered structure.

3. Both endpoints tell us something useful about the current answer.

4. Comparing endpoints proves one endpoint cannot participate
   in a better future answer.
```

Recognition sentence:

```text
PAIR + ORDER + SAFE ELIMINATION
→ Form 2
```

---

# 7. Problem 1 — Maximum Value of F(i,j)

The lecture considers:

```text
F(i,j)
=
|i-j| * min(arr[i], arr[j])
```

for:

```text
0 <= i < j < N
```

Since:

```text
i < j
```

distance is:

```text
|i-j|
=
j-i
```

So:

```text
F(i,j)
=
(j-i) * min(arr[i], arr[j])
```

---

## Example

```text
arr = [1,8,6,2,5,4,8,3,7]
```

Best pair:

```text
i = 1
j = 8
```

Then:

```text
width
=
8-1
=
7
```

Limiting value:

```text
min(8,7)
=
7
```

Score:

```text
7 * 7
=
49
```

Answer:

```text
49
```

---

# 8. Brute Force

Direct solution:

```cpp
long long answer = 0;

for (int i = 0; i < n; i++) {
    for (int j = i + 1; j < n; j++) {
        long long width = j - i;
        long long limitingHeight = min(arr[i], arr[j]);

        answer = max(answer, width * limitingHeight);
    }
}
```

Number of pairs:

```text
O(N²)
```

So:

```text
Time = O(N²)
```

We need an elimination rule.

---

# 9. What Controls the Score?

Score:

```text
(head-tail) * min(arr[tail], arr[head])
```

Two components:

```text
WIDTH
=
head-tail

LIMITING HEIGHT
=
min(arr[tail], arr[head])
```

ASCII:

```text
score
=
(width) * (limiting endpoint)

tail                                   head
 |                                       |
 v                                       v
[height A] ------------------------- [height B]

width = head-tail
usable height = min(A,B)
```

Initially:

```text
width is maximum
```

Every pointer move:

```text
reduces width
```

Therefore, to obtain a better future score after reducing width:

```text
the limiting height must improve
```

Which endpoint limits the height?

```text
the smaller endpoint
```

So the smaller endpoint is the one we must try to replace.

---

# 10. Why Move the Smaller Endpoint?

Suppose:

```text
arr[tail] < arr[head]
```

Current limiting height:

```text
arr[tail]
```

If we move `head` inward while keeping `tail` fixed:

```text
width becomes smaller
```

but limiting height can never exceed:

```text
arr[tail]
```

because:

```text
min(arr[tail], anything)
<=
arr[tail]
```

So keeping the smaller endpoint cannot produce a better score.

Therefore discard it:

```cpp
tail++;
```

Symmetrically:

```text
arr[head] < arr[tail]
```

means:

```cpp
head--;
```

If equal:

```text
either endpoint may be moved
```

---

## Movement Rule

```cpp
if (arr[tail] < arr[head]) {
    tail++;
} else {
    head--;
}
```

The current pair must be evaluated before moving.

---

# 11. Formal Elimination Proof

Suppose:

```text
arr[tail] <= arr[head]
```

Current score:

```text
current
=
(head-tail) * arr[tail]
```

Now keep `tail` fixed and choose any:

```text
k
```

such that:

```text
tail < k < head
```

New width:

```text
k-tail
<
head-tail
```

New limiting height:

```text
min(arr[tail], arr[k])
<=
arr[tail]
```

Because values are non-negative:

```text
(k-tail) * min(arr[tail], arr[k])
<=
(head-tail) * arr[tail]
```

So no closer `head` can produce a better score while keeping this same `tail`.

We have already evaluated the best possible width using this limiting endpoint.

Therefore:

```text
tail is safe to discard
```

Execute:

```cpp
tail++;
```

The proof is symmetric when:

```text
arr[head] < arr[tail]
```

Then:

```cpp
head--;
```

This elimination proof is the central invariant of this Form 2 problem.

---

# 12. Detailed Dry Run

Use:

```text
arr = [1,8,6,2,5,4,8,3,7]
```

Start:

```text
tail = 0
head = 8
answer = 0
```

Compact trace:

```text
tail=0 head=8 → values 1,7 → score=8*1=8  → ans=8
                  smaller=1 → tail++

tail=1 head=8 → values 8,7 → score=7*7=49 → ans=49
                  smaller=7 → head--

tail=1 head=7 → values 8,3 → score=6*3=18 → ans=49
                  smaller=3 → head--

tail=1 head=6 → values 8,8 → score=5*8=40 → ans=49
                  equal → move either, choose head--

tail=1 head=5 → values 8,4 → score=4*4=16 → ans=49
                  smaller=4 → head--

tail=1 head=4 → values 8,5 → score=3*5=15 → ans=49
                  smaller=5 → head--

tail=1 head=3 → values 8,2 → score=2*2=4  → ans=49
                  smaller=2 → head--

tail=1 head=2 → values 8,6 → score=1*6=6  → ans=49
                  smaller=6 → head--
```

Stop when:

```text
tail == head
```

Final answer:

```text
49
```

---

# 13. Proper C++17 — Maximum F(i,j)

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll maximumScore(const vector<ll>& arr) {
    int n = arr.size();

    if (n < 2) {
        return 0;
    }

    int tail = 0;
    int head = n - 1;

    ll answer = 0;

    while (tail < head) {
        ll width = head - tail;
        ll limitingHeight = min(arr[tail], arr[head]);

        answer = max(answer, width * limitingHeight);

        if (arr[tail] < arr[head]) {
            tail++;
        } else {
            head--;
        }
    }

    return answer;
}
```

Complexity:

```text
tail moves right at most N-1 times
head moves left at most N-1 times
```

But exactly one pointer moves per iteration.

So total iterations are:

```text
O(N)
```

Space:

```text
O(1)
```

---

# 14. Container With Most Water Connection

This formula is exactly the classical **Container With Most Water** pattern:

```text
area
=
width * min(leftHeight, rightHeight)
```

Recognition:

```text
two endpoints
+
width decreases when pointers move inward
+
smaller height is the bottleneck
```

Therefore:

```text
move the smaller height
```

Do not memorize only the code.

Memorize the proof:

```text
Keeping the smaller endpoint
cannot improve limiting height,
while width becomes smaller.
```

---

# 15. Prerequisite 4 — Sorted Two-Sum Movement

Before 3Sum, master sorted Two Sum.

Suppose:

```text
arr is sorted
target = T
```

Start:

```text
tail = 0
head = n-1
```

Current:

```text
sum = arr[tail] + arr[head]
```

Three cases:

```text
sum == T
→ found

sum < T
→ need larger sum
→ tail++

sum > T
→ need smaller sum
→ head--
```

Why is this safe?

Because sorting gives:

```text
moving tail right increases or preserves value

moving head left decreases or preserves value
```

That monotonicity is the elimination proof.

---

# 16. Problem 2 — Two Sum in a Sorted Array

Example:

```text
arr = [1,3,4,6,8,10]
target = 14
```

Dry run:

```text
tail=0 head=5 → 1+10=11 <14 → tail++
tail=1 head=5 → 3+10=13 <14 → tail++
tail=2 head=5 → 4+10=14      → found
```

C++:

```cpp
bool hasTwoSum(vector<int> arr, int target) {
    sort(arr.begin(), arr.end());

    int tail = 0;
    int head = arr.size() - 1;

    while (tail < head) {
        int sum = arr[tail] + arr[head];

        if (sum == target) {
            return true;
        }

        if (sum < target) {
            tail++;
        } else {
            head--;
        }
    }

    return false;
}
```

Complexity:

```text
sorting = O(N log N)
scan    = O(N)

total   = O(N log N)
```

If already sorted:

```text
O(N)
```

---

# 17. Prerequisite 5 — From Two Sum to Three Sum

3Sum asks for indices:

```text
i < j < k
```

such that:

```text
arr[i] + arr[j] + arr[k] = target
```

Brute force:

```text
choose i
choose j
choose k
```

Complexity:

```text
O(N³)
```

After sorting, fix one position and solve a Two Sum problem on the remaining positions.

Common pattern:

```text
fix one index
+
two pointers for the other two
```

This reduces:

```text
O(N³)
→
O(N²)
```

---

# 18. Problem 3 — 3Sum / Target Triple

The lecture shows the target-triple form:

```text
find/count triples
(i,j,k)

0 <= i < j < k < N

arr[i] + arr[j] + arr[k] = target
```

The screenshots fix the middle index:

```text
j
```

Then:

```text
i starts at 0
k starts at n-1
```

and require:

```text
i < j < k
```

ASCII:

```text
sorted array:

i →                 j                 ← k
|                   |                   |
v                   v                   v
[ ... left side ... ][j][ ... right ... ]
```

For each fixed `j`, compare:

```text
arr[i] + arr[j] + arr[k]
```

with `target`.

---

# 19. 3Sum Existence Template

```cpp
bool hasTargetTriple(vector<int> arr, long long target) {
    int n = arr.size();

    sort(arr.begin(), arr.end());

    for (int j = 1; j + 1 < n; j++) {
        int i = 0;
        int k = n - 1;

        while (i < j && j < k) {
            long long sum = 1LL * arr[i] + arr[j] + arr[k];

            if (sum == target) {
                return true;
            }

            if (sum < target) {
                i++;
            } else {
                k--;
            }
        }
    }

    return false;
}
```

Why movement works:

```text
sum < target
→ need a larger left value
→ i++

sum > target
→ need a smaller right value
→ k--
```

Again:

```text
sorting creates monotonic movement
```

---

# 20. Prerequisite 6 — Duplicate Blocks

Counting triples is harder than merely finding one.

Suppose sorted values contain repeated blocks:

```text
[3,3,3,5,5,7,7,7]
```

If current left value is:

```text
3
```

and current right value is:

```text
7
```

there may be several index choices with the same values.

Instead of counting one and moving by one, count the entire duplicate blocks.

---

## Block Length

Suppose:

```text
i points at first 3
iNext points just after all 3s
```

Then:

```text
leftCount
=
iNext - i
```

Likewise if:

```text
k points at last 7
kNext points just before all 7s
```

Then:

```text
rightCount
=
k - kNext
```

Number of index combinations using these two value blocks:

```text
leftCount * rightCount
```

---

## Connection to Bounds

In a sorted array:

```text
count(value == x)
=
#(value <= x) - #(value < x)
```

Equivalent C++ iterator formula:

```cpp
upper_bound(begin, end, x)
-
lower_bound(begin, end, x)
```

The lecture's duplicate-block loops are the Two Pointer version of the same idea.

---

# 21. Counting 3Sum Triples With Duplicates

For fixed middle index `j`:

```text
i = 0
k = n-1
```

If:

```text
arr[i] + arr[j] + arr[k] == target
```

find:

```text
iNext
=
first index after the block equal to arr[i]
on the left side of j
```

and:

```text
kNext
=
first index before the block equal to arr[k]
on the right side of j
```

Then:

```text
leftCount
=
iNext-i

rightCount
=
k-kNext
```

Contribution:

```text
leftCount * rightCount
```

Then jump:

```text
i = iNext
k = kNext
```

This avoids repeatedly processing the same value combination one index at a time.

---

# 22. Detailed Duplicate-Counting Example

Teaching example:

```text
arr = [1,1,1,5,7,7,7]
target = 13
```

Fix middle:

```text
j = 3
arr[j] = 5
```

Then:

```text
i = 0 → arr[i] = 1
k = 6 → arr[k] = 7
```

Sum:

```text
1 + 5 + 7
=
13
```

Match.

Left block:

```text
indices 0,1,2
value = 1
```

So:

```text
leftCount = 3
```

Right block:

```text
indices 4,5,6
value = 7
```

So:

```text
rightCount = 3
```

Number of index triples with this fixed `j`:

```text
3 * 3
=
9
```

Why multiplication?

Choose:

```text
one of 3 left indices
and
one of 3 right indices
```

Independent choices:

```text
3 × 3
```

---

# 23. Proper C++17 — Count Target Triples

This follows the duplicate-block idea visible in the lecture screenshots.

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll countTargetTriples(vector<int> arr, ll target) {
    int n = arr.size();

    sort(arr.begin(), arr.end());

    ll answer = 0;

    for (int j = 1; j + 1 < n; j++) {
        int i = 0;
        int k = n - 1;

        while (i < j && j < k) {
            ll sum = 1LL * arr[i] + arr[j] + arr[k];

            if (sum == target) {
                int iNext = i;
                int kNext = k;

                while (iNext < j && arr[iNext] == arr[i]) {
                    iNext++;
                }

                while (kNext > j && arr[kNext] == arr[k]) {
                    kNext--;
                }

                ll leftCount = iNext - i;
                ll rightCount = k - kNext;

                answer += leftCount * rightCount;

                i = iNext;
                k = kNext;
            } else if (sum < target) {
                i++;
            } else {
                k--;
            }
        }
    }

    return answer;
}
```

Important:

```text
This counts index triples i < j < k.
```

The outer loop fixes exactly one middle index `j`, so each index triple has one unique middle position and is not counted under another `j`.

Complexity:

```text
sort
=
O(N log N)

for each j:
two pointers traverse left/right monotonically
=
O(N)

for all j:
O(N²)
```

Total:

```text
O(N²)
```

Extra space apart from sorting:

```text
O(1)
```

depending on sort implementation.

---

# 24. Complexity Analysis

## Maximum F(i,j)

```text
tail moves right
head moves left
```

Each movement discards one endpoint.

Time:

```text
O(N)
```

Space:

```text
O(1)
```

---

## Two Sum

If sorted:

```text
O(N)
```

If sorting required:

```text
O(N log N)
```

---

## 3Sum

Sort:

```text
O(N log N)
```

For each fixed index:

```text
O(N)
```

Across `N` fixed indices:

```text
O(N²)
```

This improves brute force:

```text
O(N³)
→
O(N²)
```

---

# 25. Common Mistakes

## Mistake 1 — Moving an Endpoint Without a Proof

Form 2 is not:

```text
"move something until pointers meet"
```

Every move must eliminate a set of impossible candidates safely.

---

## Mistake 2 — Moving the Larger Height in Container Problem

If:

```text
leftHeight < rightHeight
```

moving the larger right endpoint keeps:

```text
leftHeight
```

as the maximum possible limiting height while width decreases.

It cannot improve the score while that left endpoint remains.

Move the smaller endpoint.

---

## Mistake 3 — Moving Both Pointers

After one comparison, normally only one endpoint has been proven useless.

Moving both can skip the optimal pair.

---

## Mistake 4 — Updating Answer After Moving

Wrong:

```text
move pointer
then evaluate
```

The current pair might be optimal.

Correct:

```text
evaluate current pair
then discard one endpoint
```

---

## Mistake 5 — Use Container Proof with Negative Values

The standard limiting-height proof assumes non-negative values.

With arbitrary negative values, reducing width can make a negative product numerically larger, so the same proof is not valid.

---

## Mistake 6 — Apply 2Sum Pointer Movement Before Sorting

For target sums:

```text
sum < target → move left rightward
```

is justified by sorted order.

Without sorting, this movement has no proof.

---

## Mistake 7 — Forget Duplicate Multiplicity in 3Sum Count

If several equal values exist at both pointer blocks, one equality may represent many index triples.

Count:

```text
leftBlockSize * rightBlockSize
```

---

## Mistake 8 — Overflow

Expressions such as:

```text
width * height
```

or:

```text
arr[i] + arr[j] + arr[k]
```

may exceed 32-bit `int`.

Use:

```cpp
long long
```

for scores and sums.

---

# 26. Recognition Table

| Signal | Form 2 Interpretation |
|---|---|
| Pair of positions, opposite ends useful | Consider divergence |
| Sorted pair sum | Smaller sum → move left; larger sum → move right |
| Width × limiting endpoint | Move limiting/smaller endpoint |
| Need to reduce O(N²) pair search | Look for safe endpoint elimination |
| 3Sum after sorting | Fix one index + Form 2 Two Sum |
| Count duplicate 3Sum triples | Two pointers + duplicate block sizes |
| Cannot prove discarded endpoint is useless | Do **not** use Form 2 yet |

---

# 27. Final Mental Model

```text
                    FORM 2
                 DIVERGENCE
                      |
                      v
          tail at left, head at right
                      |
                      v
            evaluate current pair
                      |
                      v
      which endpoint is provably useless?
                /             \
               /               \
              v                 v
           tail++             head--
               \               /
                \             /
                 +-----------+
                      |
                      v
                   repeat
```

The master question is:

```text
"If I discard this endpoint,
can any future pair using it beat
what I have already considered?"
```

If the answer is:

```text
NO, and I can prove it
```

the pointer movement is safe.

---

## Container / Maximum F(i,j)

```text
score
=
width * min(leftHeight, rightHeight)
```

Width always decreases.

Therefore improve the bottleneck:

```text
move the smaller endpoint
```

---

## Sorted Two Sum

```text
sum < target
→ need larger value
→ tail++

sum > target
→ need smaller value
→ head--
```

---

## 3Sum

```text
sort
+
fix one index
+
run Two Sum with opposite pointers
```

Complexity transformation:

```text
O(N³)
→
O(N²)
```

---

## 60-Second Recognition

```text
PAIR PROBLEM
+
ORDERED STRUCTURE
+
COMPARISON CAN ELIMINATE ONE ENDPOINT
=
FORM 2
```

Do not memorize:

```text
tail++ / head--
```

Memorize:

```text
EVALUATE
→ PROVE
→ ELIMINATE
```
