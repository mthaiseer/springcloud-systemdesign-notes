# Merge Sort — Divide & Conquer
## TLE-Style Self-Study Note with Prerequisites, Recursion Tree, Dry Runs, Proof, and LCCM Comparison

> **Core pattern**
>
> Merge Sort is a classic **Divide and Conquer** algorithm:
>
> ```text
> DIVIDE → CONQUER → COMBINE
> ```
>
> It is recursive, but it is **not backtracking**.
>
> The most important recursive contract is:
>
> ```text
> mergeSort(a, temp, l, r)
> =
> sort a[l..r]
> ```
>
> The function does not try choices and undo them.  
> It solves two smaller independent ranges and combines their answers.

---

# Table of Contents

1. [Prerequisite 1 — Recursion Contract](#1-prerequisite-1--recursion-contract)
2. [Prerequisite 2 — Inclusive Array Ranges](#2-prerequisite-2--inclusive-array-ranges)
3. [Prerequisite 3 — What Does Sorted Mean?](#3-prerequisite-3--what-does-sorted-mean)
4. [Prerequisite 4 — Merge Two Sorted Arrays](#4-prerequisite-4--merge-two-sorted-arrays)
5. [Recursion vs Divide and Conquer](#5-recursion-vs-divide-and-conquer)
6. [Problem Statement](#6-problem-statement)
7. [The Three-Step Divide-Conquer-Combine Framework](#7-the-three-step-divide-conquer-combine-framework)
8. [Why Merge Sort Works](#8-why-merge-sort-works)
9. [The Merge Operation](#9-the-merge-operation)
10. [Detailed Merge Dry Run](#10-detailed-merge-dry-run)
11. [Recursive Contract of Merge Sort](#11-recursive-contract-of-merge-sort)
12. [Base Case](#12-base-case)
13. [Divide Step](#13-divide-step)
14. [Conquer Step](#14-conquer-step)
15. [Combine Step](#15-combine-step)
16. [Complete Recursion Tree](#16-complete-recursion-tree)
17. [Full Dry Run — Divide Down, Merge Up](#17-full-dry-run--divide-down-merge-up)
18. [Recursion Tree vs Recursion Stack](#18-recursion-tree-vs-recursion-stack)
19. [LCCM Comparison — Why Merge Sort Is Not Backtracking](#19-lccm-comparison--why-merge-sort-is-not-backtracking)
20. [Clean C++17 Implementation](#20-clean-c17-implementation)
21. [Line-by-Line Algorithm Walkthrough](#21-line-by-line-algorithm-walkthrough)
22. [Correctness Proof](#22-correctness-proof)
23. [Time Complexity](#23-time-complexity)
24. [Recurrence Tree Analysis](#24-recurrence-tree-analysis)
25. [Space Complexity](#25-space-complexity)
26. [Stability](#26-stability)
27. [Common Mistakes](#27-common-mistakes)
28. [Recognition Checklist](#28-recognition-checklist)
29. [Final Mental Model](#29-final-mental-model)

---

# 1. Prerequisite 1 — Recursion Contract

Before writing a recursive function, define exactly what it promises to do.

For Merge Sort:

```text
mergeSort(a, temp, l, r)
```

means:

```text
Sort the elements in a[l..r]
into nondecreasing order.
```

Example:

```text
a = [8, 5, 1, 7, 3]

call:
mergeSort(a, temp, 1, 3)
```

The active range is:

```text
index: 0  1  2  3  4
value: 8  5  1  7  3
          --------
          active
```

After the call finishes:

```text
a = [8, 1, 5, 7, 3]
```

Only:

```text
a[1..3]
```

must become sorted.

The positions outside that range remain unchanged.

---

## Why defining the contract matters

When we write:

```cpp
mergeSort(a, temp, l, mid);
mergeSort(a, temp, mid + 1, r);
```

we do not need to mentally simulate every recursive call.

We trust the contract:

```text
after first call:
a[l..mid] is sorted

after second call:
a[mid+1..r] is sorted
```

Then the parent only needs to solve:

```text
How do I combine two sorted halves?
```

This is the central recursion skill.

---

# 2. Prerequisite 2 — Inclusive Array Ranges

This note uses:

```text
[l..r]
```

meaning both ends are included.

Example:

```text
l = 2
r = 5

range = indices 2,3,4,5
length = r - l + 1
```

---

## Splitting an inclusive range

Use:

```cpp
int mid = l + (r - l) / 2;
```

Then split into:

```text
left  = [l..mid]
right = [mid+1..r]
```

Example:

```text
l = 0
r = 5

mid = 2
```

So:

```text
left  = [0..2]
right = [3..5]
```

Diagram:

```text
0   1   2 | 3   4   5
----------+----------
 left     |   right
```

The two ranges:

- do not overlap,
- do not miss any position,
- are both smaller than the parent when `l < r`.

---

# 3. Prerequisite 3 — What Does Sorted Mean?

Nondecreasing order means:

```text
a[i] <= a[i+1]
```

for every adjacent pair.

Example:

```text
[1, 2, 2, 3, 4, 5]
```

is sorted.

Duplicates are allowed.

Sorting must preserve every occurrence.

Example:

```text
input:
[5, 1, 4, 2, 3, 2]

output:
[1, 2, 2, 3, 4, 5]
```

The two `2`s are both still present.

---

# 4. Prerequisite 4 — Merge Two Sorted Arrays

Suppose we already have:

```text
A = [1, 4, 5]
B = [2, 2, 3]
```

Both are sorted.

We want:

```text
C = [1, 2, 2, 3, 4, 5]
```

The important observation is:

```text
The smallest remaining value
must be at the front of A or B.
```

We never need to search inside the middle of either sorted array.

---

## Three-pointer idea

Use:

```text
i = current unread position in A
j = current unread position in B
k = next output position in C
```

ASCII:

```text
A = [1, 4, 5]
      ^
      i

B = [2, 2, 3]
      ^
      j

C = [_, _, _, _, _, _]
      ^
      k
```

Compare:

```text
A[i] = 1
B[j] = 2
```

Take smaller:

```text
1
```

Now:

```text
A = [1, 4, 5]
         ^
         i

B = [2, 2, 3]
      ^
      j

C = [1, _, _, _, _, _]
         ^
         k
```

Repeat until one side is exhausted.

Then copy the leftovers from the other side.

---

# 5. Recursion vs Divide and Conquer

These ideas are related, but they are not identical.

## Recursion

Recursion means:

```text
a function calls itself
on a smaller or simpler state
```

Example:

```text
fib(n)
→ fib(n-1)
→ fib(n-2)
```

---

## Divide and Conquer

Divide and Conquer is an algorithm-design strategy:

```text
1. Divide
2. Conquer
3. Combine
```

For Merge Sort:

```text
current range
   |
   +-- left half
   |
   +-- right half

sort both
merge both
```

---

## Important difference from naive Fibonacci

Naive Fibonacci:

```text
F(n)
├── F(n-1)
│   ├── F(n-2)
│   └── ...
└── F(n-2)
```

The same state such as:

```text
F(n-2)
```

can be solved multiple times.

This is overlapping recursion.

Merge Sort:

```text
[l..mid]
[mid+1..r]
```

uses disjoint index ranges.

The sibling subproblems do not overlap.

---

## Quick comparison

| Question | General Recursion | Merge Sort Divide & Conquer |
|---|---|---|
| Uses self-calls? | Possibly yes | Yes |
| Splits problem? | Not required | Yes |
| Child states independent? | Not required | Yes, sibling ranges are disjoint |
| Child answers combined? | Depends | Yes |
| Repeated same state? | Possible | Not between sibling ranges |
| Main structure | Recursive relation | Divide → Conquer → Combine |

---

# 6. Problem Statement

Given an array of `N` integers, sort it into nondecreasing order using Merge Sort.

Example:

```text
Input:
6
5 1 4 2 3 2
```

Expected output:

```text
1 2 2 3 4 5
```

---

# 7. The Three-Step Divide-Conquer-Combine Framework

For Merge Sort:

## Step 1 — Divide

Split:

```text
a[l..r]
```

into:

```text
a[l..mid]
a[mid+1..r]
```

---

## Step 2 — Conquer

Sort both recursively:

```cpp
mergeSort(a, temp, l, mid);
mergeSort(a, temp, mid + 1, r);
```

---

## Step 3 — Combine

Merge the two sorted halves:

```cpp
mergeRanges(a, temp, l, mid, r);
```

---

## Diagram

```text
                  a[l..r]
                     |
                  DIVIDE
                     |
          +----------+----------+
          |                     |
     a[l..mid]            a[mid+1..r]
          |                     |
       CONQUER               CONQUER
          |                     |
      sorted left           sorted right
          \                     /
           \                   /
            +------MERGE------+
                    |
               sorted a[l..r]
```

---

# 8. Why Merge Sort Works

Suppose:

```text
left half is sorted
right half is sorted
```

Then the only remaining problem is:

```text
combine two sorted sequences
```

That can be done in linear time.

So the recursive logic is:

```text
To sort a large range:

1. sort the left half
2. sort the right half
3. merge them
```

This is a perfect recursive contract.

---

# 9. The Merge Operation

Suppose:

```text
a[l..mid]     is sorted
a[mid+1..r]   is sorted
```

Example:

```text
[1,4,5 | 2,2,3]
```

We need:

```text
[1,2,2,3,4,5]
```

---

## Pointers

```text
i = l
j = mid + 1
k = l
```

Diagram:

```text
a:
 index   0  1  2 | 3  4  5
 value   1  4  5 | 2  2  3
         ^        ^
         i        j

temp:
         _  _  _  _  _  _
         ^
         k
```

---

## Main comparison

While both halves still have elements:

```text
if a[i] <= a[j]:
    take a[i]
else:
    take a[j]
```

---

## Leftover elements

Eventually one side becomes empty.

Example:

```text
left:
[4,5]

right:
[]
```

We cannot stop.

The remaining values:

```text
4,5
```

must still be copied.

That is why merge has two leftover loops.

---

# 10. Detailed Merge Dry Run

Merge:

```text
A = [1,4,5]
B = [2,2,3]
```

Start:

```text
i=0, j=0
output=[]
```

Compact iteration trace:

```text
Step 1: A[i]=1, B[j]=2 → take 1 from A → output=[1] → i=1
Step 2: A[i]=4, B[j]=2 → take 2 from B → output=[1,2] → j=1
Step 3: A[i]=4, B[j]=2 → take 2 from B → output=[1,2,2] → j=2
Step 4: A[i]=4, B[j]=3 → take 3 from B → output=[1,2,2,3] → j=3
Step 5: B exhausted → copy 4 → output=[1,2,2,3,4]
Step 6: copy 5 → output=[1,2,2,3,4,5]
```

Final:

```text
[1,2,2,3,4,5]
```

---

# 11. Recursive Contract of Merge Sort

Define:

```text
mergeSort(a, temp, l, r)
```

as:

```text
Sort exactly the original elements in a[l..r]
into nondecreasing order.

Do not change positions outside [l..r].
```

The scratch array:

```text
temp
```

is only temporary storage used during merging.

It is not itself a permanently sorted copy of the array.

---

# 12. Base Case

```cpp
if (l >= r) {
    return;
}
```

Why?

## Case 1 — One element

```text
l == r
```

Example:

```text
[5]
```

A one-element range is already sorted.

---

## Case 2 — Empty range

```text
l > r
```

This can happen for:

```text
n = 0
```

with an initial call:

```text
mergeSort(a, temp, 0, -1)
```

The function returns immediately.

---

# 13. Divide Step

Calculate:

```cpp
int mid = l + (r - l) / 2;
```

Example:

```text
l = 0
r = 5

mid = 2
```

Parent:

```text
[0..5]
```

Children:

```text
[0..2]
[3..5]
```

Diagram:

```text
[0 1 2 3 4 5]
    parent

[0 1 2] [3 4 5]
   left     right
```

---

## Why the children are valid

They:

```text
cover every parent position
```

because:

```text
[l..mid] + [mid+1..r]
```

covers:

```text
[l..r]
```

They do not overlap because one ends at `mid` and the other begins at `mid+1`.

They are smaller than the parent when:

```text
l < r
```

so recursion makes progress toward the base case.

---

# 14. Conquer Step

```cpp
mergeSort(a, temp, l, mid);
mergeSort(a, temp, mid + 1, r);
```

After these calls return, trust the recursive contract:

```text
a[l..mid] is sorted
a[mid+1..r] is sorted
```

Example:

Before:

```text
[5,1,4 | 2,3,2]
```

After the two child calls:

```text
[1,4,5 | 2,2,3]
```

Notice:

```text
the entire array is NOT sorted yet
```

because:

```text
5 > 2
```

This is why we still need Combine.

---

# 15. Combine Step

Now merge:

```text
[1,4,5]
```

and:

```text
[2,2,3]
```

to get:

```text
[1,2,2,3,4,5]
```

The combine step is:

```cpp
mergeRanges(a, temp, l, mid, r);
```

---

# 16. Complete Recursion Tree

For:

```text
[5,1,4,2,3,2]
```

the recursive decomposition is:

```text
                         [5,1,4,2,3,2]
                         range [0..5]
                       /              \
                      /                \
              [5,1,4]                [2,3,2]
              [0..2]                  [3..5]
             /      \                /      \
          [5,1]     [4]           [2,3]     [2]
          [0..1]   [2..2]         [3..4]   [5..5]
          /   \                    /   \
        [5]   [1]                [2]   [3]
       [0]    [1]               [3]    [4]
```

At the leaves:

```text
every range contains one element
```

so recursion stops.

---

## The important direction

Going DOWN:

```text
divide
divide
divide
```

Coming UP:

```text
merge
merge
merge
```

So Merge Sort is naturally remembered as:

```text
DIVIDE DOWN
MERGE UP
```

---

# 17. Full Dry Run — Divide Down, Merge Up

Input:

```text
[5,1,4,2,3,2]
```

---

## Stage 1 — Divide

```text
[5,1,4,2,3,2]
        /       \
   [5,1,4]     [2,3,2]
    /   \       /   \
 [5,1] [4]   [2,3] [2]
  / \          / \
[5] [1]      [2] [3]
```

---

## Stage 2 — Merge smallest left range

Merge:

```text
[5]
[1]
```

Result:

```text
[1,5]
```

Now left side becomes:

```text
[1,5] + [4]
```

Merge:

```text
[1,5]
[4]
```

Dry run:

```text
1 < 4 → [1]
4 < 5 → [1,4]
right exhausted → append 5
```

Result:

```text
[1,4,5]
```

---

## Stage 3 — Merge right range

Merge:

```text
[2]
[3]
```

Result:

```text
[2,3]
```

Now merge:

```text
[2,3]
[2]
```

Using left-first on ties:

```text
2 <= 2 → take left 2
2 from right
3
```

Result:

```text
[2,2,3]
```

---

## Stage 4 — Root combine

Now:

```text
left  = [1,4,5]
right = [2,2,3]
```

Merge:

```text
1 → 2 → 2 → 3 → 4 → 5
```

Final:

```text
[1,2,2,3,4,5]
```

---

## Compact complete trace

```text
[5,1,4,2,3,2]
→ split [5,1,4] and [2,3,2]

left:
[5,1] → [5] + [1] → merge → [1,5]
[1,5] + [4] → merge → [1,4,5]

right:
[2,3] → [2] + [3] → merge → [2,3]
[2,3] + [2] → merge → [2,2,3]

root:
[1,4,5] + [2,2,3]
→ [1,2,2,3,4,5]
```

---

# 18. Recursion Tree vs Recursion Stack

These are different views.

## Recursion tree

Shows:

```text
every recursive call
during the complete execution
```

Example:

```text
                 [0..5]
              /          \
          [0..2]        [3..5]
          /   \          /   \
      [0..1] [2]      [3..4] [5]
       /  \             / \
     [0]  [1]         [3] [4]
```

---

## Recursion stack

Shows only:

```text
the calls active at one moment
```

When the algorithm is descending to the leftmost leaf:

```text
mergeSort(0,5)
mergeSort(0,2)
mergeSort(0,1)
mergeSort(0,0)
```

Only this one root-to-leaf path is active.

This explains why recursion-stack space is:

```text
O(log N)
```

rather than:

```text
O(number of recursion-tree nodes)
```

---

# 19. LCCM Comparison — Why Merge Sort Is Not Backtracking

Your backtracking framework is:

```text
L = Level
C = Choice
C = Check
M = Move
```

That framework is excellent when a problem says:

```text
try choices
reject invalid choices
recurse
undo
```

Examples:

```text
N-Queens
permutations
subsets
K knights
Sudoku
```

Merge Sort does NOT have this structure.

---

## LCCM-style comparison table

| LCCM Idea | Backtracking Meaning | Merge Sort Equivalent |
|---|---|---|
| Level | decision depth | current range size / recursion depth |
| Choice | one candidate decision | not really applicable |
| Check | is candidate valid? | not needed |
| Move | choose → recurse → undo | split → recurse left/right → merge |
| Undo | restore shared state | not needed |

So for Merge Sort, do not force:

```text
Level → Choice → Check → Move
```

The correct framework is:

```text
STATE → BASE → DIVIDE → CONQUER → COMBINE
```

---

## Better recursive design checklist for Merge Sort

### State

```text
What part of the array am I responsible for?

[l..r]
```

### Base

```text
When is the range already sorted?

l >= r
```

### Divide

```text
How do I make smaller independent problems?

[l..mid]
[mid+1..r]
```

### Conquer

```text
What recursive calls solve them?

mergeSort(left)
mergeSort(right)
```

### Combine

```text
How do I construct the parent answer?

merge the two sorted halves
```

This is the Divide-and-Conquer equivalent of the disciplined thinking that LCCM gives you for backtracking.

---

# 20. Clean C++17 Implementation

```cpp
#include <bits/stdc++.h>
using namespace std;

void mergeRanges(vector<long long>& a, vector<long long>& temp, int l, int mid, int r) {
    int i = l;
    int j = mid + 1;
    int k = l;

    while (i <= mid && j <= r) {
        if (a[i] <= a[j]) {
            temp[k++] = a[i++];
        } else {
            temp[k++] = a[j++];
        }
    }

    while (i <= mid) {
        temp[k++] = a[i++];
    }

    while (j <= r) {
        temp[k++] = a[j++];
    }

    for (int pos = l; pos <= r; pos++) {
        a[pos] = temp[pos];
    }
}

void mergeSort(vector<long long>& a, vector<long long>& temp, int l, int r) {
    if (l >= r) {
        return;
    }

    int mid = l + (r - l) / 2;

    mergeSort(a, temp, l, mid);
    mergeSort(a, temp, mid + 1, r);

    mergeRanges(a, temp, l, mid, r);
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<long long> a(n);

    for (long long& value : a) {
        cin >> value;
    }

    vector<long long> temp(n);

    mergeSort(a, temp, 0, n - 1);

    for (int i = 0; i < n; i++) {
        if (i > 0) {
            cout << ' ';
        }

        cout << a[i];
    }

    cout << '\n';

    return 0;
}
```

---

# 21. Line-by-Line Algorithm Walkthrough

## Main call

```cpp
mergeSort(a, temp, 0, n - 1);
```

Meaning:

```text
sort the complete array
```

---

## Base case

```cpp
if (l >= r) {
    return;
}
```

A range of size `0` or `1` is already sorted.

---

## Midpoint

```cpp
int mid = l + (r - l) / 2;
```

This splits the current range.

---

## Sort left

```cpp
mergeSort(a, temp, l, mid);
```

After it returns:

```text
a[l..mid] is sorted
```

---

## Sort right

```cpp
mergeSort(a, temp, mid + 1, r);
```

After it returns:

```text
a[mid+1..r] is sorted
```

---

## Merge both

```cpp
mergeRanges(a, temp, l, mid, r);
```

Precondition:

```text
both halves are already sorted
```

Postcondition:

```text
a[l..r] is sorted
```

---

# 22. Correctness Proof

We prove:

```text
mergeSort(a, temp, l, r)
correctly sorts a[l..r].
```

Use induction on the length:

```text
len = r - l + 1
```

---

## Base case

If:

```text
len <= 1
```

the range is already sorted.

So the algorithm is correct.

---

## Inductive hypothesis

Assume Merge Sort correctly sorts every smaller range.

---

## Inductive step

Consider a range:

```text
a[l..r]
```

with at least two elements.

The algorithm splits it into:

```text
a[l..mid]
a[mid+1..r]
```

Both are smaller than the original range.

By the inductive hypothesis:

```text
the left recursive call sorts the left half
the right recursive call sorts the right half
```

Now both halves are sorted.

The merge procedure repeatedly chooses the smallest unread front element.

Therefore it produces:

```text
one sorted sequence
containing exactly all elements from both halves
```

Thus:

```text
a[l..r]
```

is sorted correctly.

Therefore Merge Sort is correct.

---

# 23. Time Complexity

Let:

```text
T(n)
```

be the time to sort `n` elements.

Merge Sort creates:

```text
2 subproblems of size about n/2
```

and performs:

```text
O(n)
```

merge work.

Therefore:

```text
T(n) = 2T(n/2) + O(n)
```

---

## Why merge is O(n)

Suppose:

```text
left size  = p
right size = q
```

Every element is copied exactly once into the output.

So:

```text
merge time = O(p + q)
```

For the parent:

```text
p + q = n
```

Therefore:

```text
merge = O(n)
```

---

# 24. Recurrence Tree Analysis

At recursion depth `0`:

```text
1 problem of size n
merge work = n
```

At depth `1`:

```text
2 problems of size n/2
total work = 2 * n/2 = n
```

At depth `2`:

```text
4 problems of size n/4
total work = 4 * n/4 = n
```

General level:

```text
2^d problems
each size n / 2^d
```

Total:

```text
2^d * (n / 2^d)
= n
```

So every level costs:

```text
O(n)
```

---

## Number of levels

Keep dividing by `2`:

```text
n
n/2
n/4
n/8
...
1
```

We stop when:

```text
n / 2^d = 1
```

So:

```text
d = log2(n)
```

Therefore:

```text
time
=
work per level * number of levels
=
O(n) * O(log n)
=
O(n log n)
```

---

## Recursion tree picture

```text
Level 0:                    n
                           O(n)

                         /     \

Level 1:              n/2       n/2
                      O(n/2)    O(n/2)
                      total O(n)

                     /   \       /   \

Level 2:           n/4 n/4    n/4 n/4
                   total O(n)

                     ...

Level log n:      1 1 1 1 ... 1
```

Every non-leaf level contributes:

```text
O(n)
```

There are:

```text
O(log n)
```

levels.

So:

```text
O(n log n)
```

---

# 25. Space Complexity

The implementation uses:

```text
temp array = O(n)
```

The recursion depth is:

```text
O(log n)
```

because the range is halved each time.

Therefore:

```text
auxiliary space
=
O(n) + O(log n)
=
O(n)
```

---

## Why not O(n log n)?

Because the same:

```text
temp
```

buffer is reused.

We do not allocate a new full-size array at every recursion level.

Also, only one root-to-leaf recursion path is active at one time.

---

# 26. Stability

A sort is stable when equal keys preserve their original relative order.

Suppose:

```text
2a
2b
```

have equal value `2`, but `2a` originally appears first.

During merge:

```text
left  = [2a, 3]
right = [2b]
```

If we use:

```cpp
if (a[i] <= a[j])
```

then on equality we take from the left first.

Output:

```text
[2a, 2b, 3]
```

Original relative order is preserved.

If we used strict `<` and took from the right on ties:

```text
[2b, 2a, 3]
```

The result would still be numerically sorted, but not stable.

---

# 27. Common Mistakes

## Mistake 1 — Mixing interval conventions

If you use inclusive:

```text
[l..r]
```

then use consistently:

```text
left  = [l..mid]
right = [mid+1..r]
```

Do not mix this with half-open ranges such as:

```text
[l..r)
```

---

## Mistake 2 — Forgetting the base case

Without:

```cpp
if (l >= r) {
    return;
}
```

the recursion may never stop.

---

## Mistake 3 — Recursive child does not shrink

Bad recursive ranges can call the same state again.

Correct:

```text
[l..mid]
[mid+1..r]
```

Both are strictly smaller when `l < r`.

---

## Mistake 4 — Thinking recursive calls alone sort the entire range

After:

```cpp
mergeSort(left);
mergeSort(right);
```

we only know:

```text
left sorted
right sorted
```

We still need:

```text
merge
```

---

## Mistake 5 — Concatenating sorted halves

Example:

```text
left  = [1,4,5]
right = [2,2,3]
```

Concatenation gives:

```text
[1,4,5,2,2,3]
```

which is not sorted.

The halves must be merged.

---

## Mistake 6 — Forgetting leftovers

The main comparison loop ends when one side is exhausted.

The other side may still contain values.

Always copy leftovers.

---

## Mistake 7 — Writing into unread input

If you overwrite the original range while still reading from it, you may destroy values that have not been processed.

Using:

```text
temp
```

avoids this.

---

## Mistake 8 — Forgetting copy-back

After building the sorted range in:

```text
temp[l..r]
```

copy it back to:

```text
a[l..r]
```

Otherwise the parent recursive call will not see the sorted child result.

---

## Mistake 9 — Passing the array by value

This would repeatedly copy the vector.

Use references:

```cpp
vector<long long>& a
```

---

## Mistake 10 — Saying "two recursive calls = exponential"

This is incorrect.

The important question is:

```text
How large are the child problems?
```

Merge Sort has:

```text
2 children of size n/2
```

not:

```text
2 children of size n-1
```

Hence:

```text
O(n log n)
```

not exponential.

---

# 28. Recognition Checklist

When you see a Divide-and-Conquer problem, ask:

### 1. State

```text
What exactly does solve(state) promise?
```

For Merge Sort:

```text
sort a[l..r]
```

### 2. Base case

```text
What is already trivially solved?
```

For Merge Sort:

```text
range size <= 1
```

### 3. Divide

```text
How can I create smaller instances?
```

For Merge Sort:

```text
left half
right half
```

### 4. Independence

```text
Can the children be solved separately?
```

For Merge Sort:

```text
yes
```

because their index ranges are disjoint.

### 5. Combine

```text
What information do the solved children give me?
```

They give:

```text
two sorted halves
```

### 6. Efficient combine

```text
Can I combine them faster than solving from scratch?
```

For Merge Sort:

```text
yes — linear two-pointer merge
```

---

# 29. Final Mental Model

Do not remember Merge Sort merely as:

```text
split array recursively
```

Remember the full contract:

```text
                 SORT [l..r]
                     |
                  DIVIDE
                     |
          +----------+----------+
          |                     |
      SORT LEFT              SORT RIGHT
          |                     |
          +----------+----------+
                     |
                   MERGE
                     |
               SORTED [l..r]
```

And remember the execution direction:

```text
DOWNWARD:
    divide into smaller ranges

UPWARD:
    merge solved ranges
```

So the one-line memory rule is:

```text
Merge Sort =
divide the range
+ trust both recursive child contracts
+ merge the two sorted answers
```

---

## 60-second recognition model

If a problem has:

```text
1. a large state
2. two or more smaller independent states
3. an efficient way to combine their answers
```

think:

```text
DIVIDE & CONQUER
```

For Merge Sort specifically:

```text
STATE   = [l..r]
BASE    = l >= r
DIVIDE  = [l..mid] and [mid+1..r]
CONQUER = recursively sort both
COMBINE = merge in O(length)
```

That gives:

```text
T(n) = 2T(n/2) + O(n)
     = O(n log n)
```

---

## Final contrast with LCCM

Backtracking:

```text
LEVEL → CHOICE → CHECK → MOVE → UNDO
```

Merge Sort:

```text
STATE → BASE → DIVIDE → CONQUER → COMBINE
```

Both are recursive frameworks, but they solve different classes of problems.

Use the framework that matches the structure of the problem.
