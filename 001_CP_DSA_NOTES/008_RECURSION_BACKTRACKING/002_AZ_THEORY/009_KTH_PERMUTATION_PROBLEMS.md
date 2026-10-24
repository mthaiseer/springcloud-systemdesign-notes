# Kth Permutation — Easy + Hard Version
## Kth Form / Count-and-Skip Recursion with LCCM Thinking

> **Main pattern**
>
> A normal permutation recursion generates every branch.
>
> For the **Kth permutation**, every branch has a known size.
>
> Therefore we can:
>
> **COUNT → LOCATE → SKIP → DESCEND**
>
> We never need to generate the permutations before the Kth one.

---

# Table of Contents

1. [Problem Statement](#1-problem-statement)
2. [Prerequisite 1 — What Is a Permutation?](#2-prerequisite-1--what-is-a-permutation)
3. [Prerequisite 2 — Lexicographic Order](#3-prerequisite-2--lexicographic-order)
4. [Prerequisite 3 — Why There Are N! Permutations](#4-prerequisite-3--why-there-are-n-permutations)
5. [Prerequisite 4 — Ordinary Permutation Recursion](#5-prerequisite-4--ordinary-permutation-recursion)
6. [The Key Observation — Permutations Form Equal-Sized Blocks](#6-the-key-observation--permutations-form-equal-sized-blocks)
7. [Why Kth Form Is Different from Backtracking](#7-why-kth-form-is-different-from-backtracking)
8. [LCCM Framework for Kth Permutation](#8-lccm-framework-for-kth-permutation)
9. [The Most Important Formula](#9-the-most-important-formula)
10. [Why Convert K from 1-Based to 0-Based?](#10-why-convert-k-from-1-based-to-0-based)
11. [Detailed Dry Run — N = 3, K = 4](#11-detailed-dry-run--n--3-k--4)
12. [Recursion Tree for N = 3](#12-recursion-tree-for-n--3)
13. [Easy Version Algorithm](#13-easy-version-algorithm)
14. [Easy Version C++17](#14-easy-version-c17)
15. [Easy Version Correctness](#15-easy-version-correctness)
16. [Easy Version Complexity](#16-easy-version-complexity)
17. [Hard Version — Why N = 1e5 Changes the Implementation](#17-hard-version--why-n--1e5-changes-the-implementation)
18. [Hard Version Key Observation — Only a Small Suffix Can Change](#18-hard-version-key-observation--only-a-small-suffix-can-change)
19. [Detailed Hard-Version Dry Run — N = 10, K = 3](#19-detailed-hard-version-dry-run--n--10-k--3)
20. [Hard Version Algorithm](#20-hard-version-algorithm)
21. [Hard Version C++17](#21-hard-version-c17)
22. [Hard Version Correctness](#22-hard-version-correctness)
23. [Hard Version Complexity](#23-hard-version-complexity)
24. [Alternative Recursive View](#24-alternative-recursive-view)
25. [Common Mistakes](#25-common-mistakes)
26. [Kth Form Recognition Checklist](#26-kth-form-recognition-checklist)
27. [Reusable Kth-Generation Template](#27-reusable-kth-generation-template)
28. [Connection to Kth Tower of Hanoi](#28-connection-to-kth-tower-of-hanoi)
29. [Final Mental Model](#29-final-mental-model)

---

# 1. Problem Statement

For a given `n`, consider:

```text
A = {1, 2, 3, ..., n}
```

There are:

```text
n!
```

unique permutations of this array.

List all permutations in **lexicographic order**.

Given `n` and `k`, find the **Kth permutation**.

`k` is 1-based.

---

## Easy version constraints

```text
1 <= n <= 9
1 <= k <= n!
```

Example:

```text
Input:
3 4

Output:
2 3 1
```

For `n = 3`, the lexicographic permutations are:

```text
1: 1 2 3
2: 1 3 2
3: 2 1 3
4: 2 3 1
5: 3 1 2
6: 3 2 1
```

Therefore:

```text
K = 4
answer = 2 3 1
```

---

## Hard version constraints

```text
1 <= n <= 100000
1 <= k <= min(n!, 10^9)
```

Example:

```text
Input:
10 3

Output:
1 2 3 4 5 6 7 9 8 10
```

The hard version cannot perform an expensive operation over all `n` positions.

We need to use the bound on `k`.

---

# 2. Prerequisite 1 — What Is a Permutation?

A permutation is an ordering of all elements.

For:

```text
[1, 2, 3]
```

the permutations are:

```text
[1, 2, 3]
[1, 3, 2]
[2, 1, 3]
[2, 3, 1]
[3, 1, 2]
[3, 2, 1]
```

Every element appears:

```text
exactly once
```

in every permutation.

---

# 3. Prerequisite 2 — Lexicographic Order

Lexicographic order is the same idea as dictionary order.

Compare two sequences from left to right.

At the **first position where they differ**, the sequence with the smaller value comes first.

Example:

```text
[1, 3, 2]
[2, 1, 3]
```

Compare the first element:

```text
1 < 2
```

Therefore:

```text
[1, 3, 2] comes first
```

Another example:

```text
[2, 1, 3]
[2, 3, 1]
```

First elements are equal:

```text
2 == 2
```

Compare the second element:

```text
1 < 3
```

Therefore:

```text
[2, 1, 3] comes first
```

---

## Why lexicographic order is useful

All permutations beginning with the same first element appear together.

For `n = 3`:

```text
starts with 1:
    1 2 3
    1 3 2

starts with 2:
    2 1 3
    2 3 1

starts with 3:
    3 1 2
    3 2 1
```

This creates **blocks**.

Those blocks are the foundation of the Kth Form solution.

---

# 4. Prerequisite 3 — Why There Are N! Permutations

Suppose we have `n` distinct elements.

For the first position:

```text
n choices
```

After choosing one element, for the second position:

```text
n - 1 choices
```

Then:

```text
n - 2 choices
```

and so on.

Therefore:

```text
number of permutations
=
n * (n - 1) * (n - 2) * ... * 2 * 1
=
n!
```

Example:

```text
n = 4

4! = 4 * 3 * 2 * 1
   = 24
```

---

# 5. Prerequisite 4 — Ordinary Permutation Recursion

A normal backtracking solution might build the permutation position by position.

State:

```text
current permutation
unused elements
```

At each level:

```text
try every unused element
```

For `[1,2,3]`:

```text
                         []
              /           |           \
             1            2            3
             |            |            |
           [1]          [2]          [3]
          /   \        /   \        /   \
         2     3      1     3      1     2
         |     |      |     |      |     |
      [1,2] [1,3]  [2,1] [2,3]  [3,1] [3,2]
         |     |      |     |      |     |
         3     2      3     1      2     1
         |     |      |     |      |     |
       123   132    213   231    312   321
```

Normal backtracking visits:

```text
all n! leaves
```

If the question asks:

```text
"Generate all permutations"
```

that is exactly what we want.

But here the question asks only:

```text
"Give me permutation number K"
```

Generating all earlier permutations is wasted work.

---

# 6. The Key Observation — Permutations Form Equal-Sized Blocks

Suppose there are:

```text
m
```

unused values.

If we choose one value for the current position, then:

```text
m - 1
```

values remain.

Those remaining values can be arranged in:

```text
(m - 1)!
```

ways.

Therefore **every choice at this level owns exactly `(m - 1)!` permutations**.

---

## Example: n = 4

Available:

```text
[1, 2, 3, 4]
```

If first number is `1`:

```text
remaining = [2,3,4]

number of completions = 3! = 6
```

So the first six permutations begin with `1`.

Likewise:

```text
first = 1 → permutations  1..6
first = 2 → permutations  7..12
first = 3 → permutations 13..18
first = 4 → permutations 19..24
```

ASCII block view:

```text
K:
1            6 7           12 13          18 19          24
|------------| |------------| |------------| |------------|
 first = 1      first = 2      first = 3      first = 4

 each block size = 3! = 6
```

Therefore, if we know `K`, we can immediately determine the first element.

We do not need to generate any permutation.

---

# 7. Why Kth Form Is Different from Backtracking

## Backtracking

At one position:

```text
try choice 1
recurse
undo

try choice 2
recurse
undo

try choice 3
recurse
undo
...
```

We explore **many branches**.

---

## Kth Form

At one position:

```text
count how many answers each choice generates
```

Then:

```text
find which block contains K
```

and enter only that block.

So:

```text
                  all choices
                      |
       +--------------+--------------+
       |              |              |
     block 0        block 1        block 2
       X              ↓              X
                   contains K
```

Branches marked `X` are skipped completely.

---

## Main transformation

```text
GENERATION:
    explore every branch

KTH FORM:
    count every branch
    select one branch
```

This is the same **count-and-skip** idea used in the Kth Tower of Hanoi problem.

---

# 8. LCCM Framework for Kth Permutation

The usual LCCM framework is:

```text
L = Level
C = Choice
C = Check
M = Move
```

For Kth Form, the interpretation changes slightly.

| LCCM Part | Kth Permutation Meaning |
|---|---|
| **Level** | Which permutation position are we filling? |
| **Choice** | Which unused value could go here? |
| **Check** | Which choice's factorial block contains `k`? |
| **Move** | Select that value, remove it, convert `k` to its local rank, continue |

There is an important difference from normal backtracking:

```text
We do not recurse into every valid choice.
```

We recurse only into:

```text
the unique choice whose block contains K.
```

Therefore:

```text
no undo is necessary
```

for the count-and-skip implementation.

---

## LCCM diagram

```text
                    CURRENT STATE
              available values + K
                       |
                       v
              LEVEL = current index
                       |
                       v
          CHOICES = all remaining values
                       |
                       v
       blockSize = (remainingCount - 1)!
                       |
                       v
            CHECK: which block has K?
                       |
                       v
              choose ONE value
                       |
                       v
          MOVE:
          - append chosen value
          - remove chosen value
          - convert K to local K
                       |
                       v
                 NEXT LEVEL
```

---

# 9. The Most Important Formula

Suppose:

```text
m = number of unused elements
```

At this level:

```text
blockSize = (m - 1)!
```

Now use **zero-based `k`**.

Then:

```text
choiceIndex = k / blockSize
```

This tells us which unused value to choose.

Then:

```text
k = k % blockSize
```

This converts `k` into the rank **inside the selected block**.

So the entire algorithm is:

```text
blockSize  = (m - 1)!
choiceIndex = k / blockSize
k           = k % blockSize
```

Then remove:

```text
available[choiceIndex]
```

and repeat.

---

# 10. Why Convert K from 1-Based to 0-Based?

The problem gives:

```text
K = 1, 2, 3, ...
```

But integer division naturally works with zero-based block offsets.

Therefore immediately do:

```cpp
k--;
```

---

## Example

Suppose:

```text
blockSize = 2
```

The blocks in 1-based indexing are:

```text
block 0: K = 1,2
block 1: K = 3,4
block 2: K = 5,6
```

If:

```text
K = 4
```

using 1-based directly:

```text
4 / 2 = 2
```

That incorrectly suggests block `2`.

But convert first:

```text
k = K - 1
  = 3
```

Then:

```text
choiceIndex
= 3 / 2
= 1
```

Correct.

---

## Visual

```text
1-based K:     1  2 | 3  4 | 5  6
0-based k:     0  1 | 2  3 | 4  5
block index:     0  |   1  |   2
```

With zero-based indexing:

```text
blockIndex = k / blockSize
```

becomes exact.

---

# 11. Detailed Dry Run — N = 3, K = 4

We need:

```text
4th permutation of [1,2,3]
```

---

## Step 0 — Convert K

Problem gives:

```text
K = 4
```

Convert:

```text
k = K - 1
  = 3
```

Available:

```text
[1,2,3]
```

---

## Level 0 — Decide the first value

Remaining count:

```text
m = 3
```

Each first-value block contains:

```text
(m - 1)!
= 2!
= 2
```

permutations.

Blocks:

```text
choice 1 → 123,132
choice 2 → 213,231
choice 3 → 312,321
```

ASCII:

```text
zero-based ranks:

0     1 | 2     3 | 4     5
123  132|213   231|312   321
--------+---------+---------
   1        2         3
```

Calculate:

```text
choiceIndex = k / blockSize
            = 3 / 2
            = 1
```

Available:

```text
[1,2,3]
 0 1 2
```

Index `1` gives:

```text
2
```

So first answer value:

```text
2
```

Update local `k`:

```text
k = 3 % 2
  = 1
```

Remove `2`.

Available becomes:

```text
[1,3]
```

Current answer:

```text
[2]
```

---

## Level 1 — Decide the second value

Remaining count:

```text
m = 2
```

Block size:

```text
(m - 1)!
= 1!
= 1
```

Available:

```text
[1,3]
```

Each choice contributes one completion:

```text
choose 1 → 213
choose 3 → 231
```

Current local:

```text
k = 1
```

Calculate:

```text
choiceIndex
= 1 / 1
= 1
```

Choose:

```text
available[1] = 3
```

Update:

```text
k = 1 % 1
  = 0
```

Available:

```text
[1]
```

Answer:

```text
[2,3]
```

---

## Level 2 — Last value

Only:

```text
1
```

remains.

Answer:

```text
[2,3,1]
```

Final:

```text
2 3 1
```

---

## Compact dry run

```text
N=3, K=4 → zero-based k=3
available=[1,2,3]

Level 0: block=2! =2 → idx=3/2=1 → choose 2 → k=3%2=1 → available=[1,3]
Level 1: block=1! =1 → idx=1/1=1 → choose 3 → k=1%1=0 → available=[1]
Level 2: choose 1

answer = [2,3,1]
```

---

# 12. Recursion Tree for N = 3

Complete permutation tree:

```text
                               []
                 /              |              \
                /               |               \
              [1]             [2]              [3]
          ranks 1..2       ranks 3..4       ranks 5..6
            /   \            /   \            /   \
         [1,2] [1,3]      [2,1] [2,3]      [3,1] [3,2]
           |      |          |      |          |      |
          123    132        213    231        312    321
```

For:

```text
K = 4
```

we do **not** explore this whole tree.

We know:

```text
each root branch = 2! = 2 leaves
```

So:

```text
K=4 lies in [2] branch
```

After skipping the first two permutations:

```text
local K = 4 - 2 = 2
```

Inside `[2]`:

```text
[2,1] owns one leaf
[2,3] owns one leaf
```

So local `K=2` chooses:

```text
[2,3]
```

Then:

```text
1
```

is forced.

Path actually followed:

```text
[]
 |
 +-- choose 2
       |
       +-- choose 3
             |
             +-- choose 1
                   |
                  231
```

Everything else is skipped using counts.

---

# 13. Easy Version Algorithm

Easy constraints:

```text
n <= 9
```

So we can store all remaining numbers in a vector and erase the chosen element.

---

## Step 1 — Precompute factorials

```text
fact[0] = 1
fact[i] = i * fact[i-1]
```

For `n <= 9`:

```text
9! = 362880
```

which is small.

---

## Step 2 — Create available values

```text
available = [1,2,3,...,n]
```

---

## Step 3 — Convert K

```text
k--
```

Now `k` is zero-based.

---

## Step 4 — Build the permutation

While values remain:

```text
m = available.size()

blockSize = (m - 1)!

choiceIndex = k / blockSize

take available[choiceIndex]

k %= blockSize

erase chosen value
```

---

## Pseudocode

```text
fact[0] = 1
for i = 1..n:
    fact[i] = fact[i-1] * i

available = [1,2,...,n]

k = k - 1

answer = []

while available is not empty:

    m = available.size()

    blockSize = fact[m-1]

    choiceIndex = k / blockSize

    answer.push(available[choiceIndex])

    k = k % blockSize

    erase available[choiceIndex]
```

---

# 14. Easy Version C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    long long k;
    cin >> n >> k;

    vector<long long> factorial(n + 1, 1);

    for (int i = 1; i <= n; i++) {
        factorial[i] = factorial[i - 1] * i;
    }

    vector<int> available;

    for (int value = 1; value <= n; value++) {
        available.push_back(value);
    }

    k--;

    vector<int> answer;

    while (!available.empty()) {
        int remaining = available.size();
        long long blockSize = factorial[remaining - 1];

        int choiceIndex = k / blockSize;
        k %= blockSize;

        answer.push_back(available[choiceIndex]);
        available.erase(available.begin() + choiceIndex);
    }

    for (int value : answer) {
        cout << value << ' ';
    }

    cout << '\n';

    return 0;
}
```

---

# 15. Easy Version Correctness

At a state with:

```text
m remaining values
```

each possible first choice generates:

```text
(m - 1)!
```

permutations.

Because available values are stored in increasing order, these blocks also appear in increasing lexicographic order.

Let zero-based `k` be the desired rank inside the current state.

Then:

```text
choiceIndex = k / (m - 1)!
```

is exactly the block containing the desired permutation.

After skipping all preceding blocks, the position inside that chosen block is:

```text
k % (m - 1)!
```

The algorithm therefore chooses the correct value at the current position and reduces the problem to the same problem on one fewer value.

Repeating this until no values remain constructs exactly the requested permutation.

---

# 16. Easy Version Complexity

There are:

```text
n
```

positions.

Finding the choice index is O(1).

But:

```cpp
available.erase(...)
```

may shift:

```text
O(n)
```

elements.

Therefore:

```text
Time = O(n^2)
Space = O(n)
```

For:

```text
n <= 9
```

this is easily fast enough.

---

# 17. Hard Version — Why N = 1e5 Changes the Implementation

Hard version:

```text
n <= 100000
k <= 10^9
```

The simple vector erase approach over all `n` positions could cost:

```text
O(n^2)
```

which is too slow.

But the hard version gives us a very important extra fact:

```text
k <= 10^9
```

This drastically limits how far from the sorted permutation the answer can be.

---

# 18. Hard Version Key Observation — Only a Small Suffix Can Change

Factorials grow extremely quickly:

```text
1!  = 1
2!  = 2
3!  = 6
4!  = 24
5!  = 120
6!  = 720
7!  = 5040
8!  = 40320
9!  = 362880
10! = 3628800
11! = 39916800
12! = 479001600
13! = 6227020800
```

But:

```text
k <= 10^9
```

and:

```text
13! > 10^9
```

This tells us something powerful.

Suppose more than `13` values remain.

Then the block for fixing the smallest available value is at least:

```text
13!
```

permutations.

That is already larger than every possible `k`.

Therefore the desired permutation must still be inside the **first block**.

So the smallest remaining number is forced.

---

## Consequence

For large `n`, the beginning of the answer must be:

```text
1 2 3 4 5 ...
```

Only the final at-most-13 elements can differ.

Therefore:

```text
prefix length = n - min(n, 13)
```

The prefix is fixed.

Only the suffix needs Kth-permutation logic.

---

## Example: n = 100000

We do NOT need to permute all 100000 numbers.

We can directly output:

```text
1, 2, 3, ..., 99987
```

Then apply the factorial-block method only to:

```text
99988 ... 100000
```

which contains only:

```text
13 elements
```

So even a simple vector erase on that suffix is tiny.

---

## Why 13?

Because:

```text
12! = 479001600 <= 10^9
13! = 6227020800 > 10^9
```

When there are more than 13 values left, the first lexicographic block is too large for `k` to escape it.

Thus earlier values are forced.

---

# 19. Detailed Hard-Version Dry Run — N = 10, K = 3

Sample:

```text
N = 10
K = 3
```

Since:

```text
n < 13
```

we can reason on all ten elements.

Convert:

```text
k = K - 1
  = 2
```

Initially:

```text
available = [1,2,3,4,5,6,7,8,9,10]
```

---

## First position

Block size:

```text
9! = 362880
```

Since:

```text
k = 2
```

we get:

```text
choiceIndex = 2 / 362880 = 0
```

Choose:

```text
1
```

`k` remains:

```text
2
```

---

## Second position

Block:

```text
8! = 40320
```

Again:

```text
2 / 40320 = 0
```

Choose:

```text
2
```

---

The same happens while the factorial block remains larger than `2`.

So the prefix becomes:

```text
1 2 3 4 5 6 7
```

Now remaining:

```text
[8,9,10]
```

---

## Decide among the final three values

Remaining:

```text
m = 3
```

Block:

```text
2! = 2
```

Current:

```text
k = 2
```

Calculate:

```text
choiceIndex = 2 / 2
            = 1
```

Available:

```text
[8,9,10]
 0 1  2
```

Choose:

```text
9
```

Update:

```text
k = 2 % 2
  = 0
```

Remaining:

```text
[8,10]
```

---

## Next position

Block:

```text
1! = 1
```

```text
choiceIndex = 0 / 1 = 0
```

Choose:

```text
8
```

Remaining:

```text
[10]
```

Choose last:

```text
10
```

Answer:

```text
1 2 3 4 5 6 7 9 8 10
```

which matches the sample.

---

## Compact dry run

```text
N=10, K=3 → k=2

9! > 2 → choose 1
8! > 2 → choose 2
7! > 2 → choose 3
6! > 2 → choose 4
5! > 2 → choose 5
4! > 2 → choose 6
3! > 2 → choose 7

remaining=[8,9,10]
block=2! =2 → idx=2/2=1 → choose 9 → k=0
remaining=[8,10]
block=1! =1 → idx=0 → choose 8
last=10

answer=[1,2,3,4,5,6,7,9,8,10]
```

---

# 20. Hard Version Algorithm

We only need the final:

```text
suffixSize = min(n, 13)
```

values.

---

## Step 1 — Print/store the forced prefix

If:

```text
n > 13
```

then:

```text
1 ... n-13
```

cannot change.

---

## Step 2 — Build suffix

```text
[n-suffixSize+1, ..., n]
```

At most:

```text
13 values
```

---

## Step 3 — Precompute factorials only up to 13

```text
factorial[0..13]
```

using `long long`.

---

## Step 4 — Convert K

```text
k--
```

---

## Step 5 — Apply ordinary factorial-block selection to the suffix

At each suffix position:

```text
blockSize = factorial[remaining - 1]

choiceIndex = k / blockSize

choose available[choiceIndex]

k %= blockSize

erase chosen value
```

Because the vector has at most 13 values:

```text
erase cost is negligible
```

---

# 21. Hard Version C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    long long k;
    cin >> n >> k;

    const int MAX_SUFFIX = 13;

    vector<long long> factorial(MAX_SUFFIX + 1, 1);

    for (int i = 1; i <= MAX_SUFFIX; i++) {
        factorial[i] = factorial[i - 1] * i;
    }

    k--;

    int suffixSize = min(n, MAX_SUFFIX);
    int prefixEnd = n - suffixSize;

    vector<int> answer;

    for (int value = 1; value <= prefixEnd; value++) {
        answer.push_back(value);
    }

    vector<int> available;

    for (int value = prefixEnd + 1; value <= n; value++) {
        available.push_back(value);
    }

    while (!available.empty()) {
        int remaining = available.size();
        long long blockSize = factorial[remaining - 1];

        int choiceIndex = k / blockSize;
        k %= blockSize;

        answer.push_back(available[choiceIndex]);
        available.erase(available.begin() + choiceIndex);
    }

    for (int value : answer) {
        cout << value << ' ';
    }

    cout << '\n';

    return 0;
}
```

---

# 22. Hard Version Correctness

We prove two things.

---

## Part 1 — The prefix we skip is forced

For any state containing more than `13` unused values:

```text
m >= 14
```

the size of the first-choice block is:

```text
(m - 1)! >= 13!
```

And:

```text
13! = 6227020800 > 10^9
```

Since the problem guarantees:

```text
K <= 10^9
```

the desired permutation cannot leave the first block.

Therefore the smallest available value must be chosen.

So every position before the final at-most-13 values is forced to remain sorted.

---

## Part 2 — The suffix algorithm selects the correct permutation

Inside the small suffix, the same factorial-block argument as the easy version applies.

For `m` remaining values:

```text
each choice owns (m-1)! completions
```

Using zero-based `k`:

```text
choiceIndex = k / (m-1)!
```

selects exactly the block containing the desired permutation.

Then:

```text
k %= (m-1)!
```

gives its local rank inside that block.

Therefore the suffix is constructed correctly.

Combining the forced prefix and correctly selected suffix yields the Kth lexicographic permutation of `1..n`.

---

# 23. Hard Version Complexity

Let:

```text
S = min(n, 13)
```

We output `n` values, so:

```text
Ω(n)
```

time is unavoidable just to print the answer.

The suffix manipulation costs:

```text
O(S^2)
```

because of vector erase.

But:

```text
S <= 13
```

so this is effectively constant.

Therefore:

```text
Time = O(n + 13^2)
     = O(n)
```

Space:

```text
answer vector = O(n)
suffix vector = O(13)
```

If printed directly, the extra working space can be reduced.

---

# 24. Alternative Recursive View

Although the iterative factorial method is simpler, the problem is naturally recursive.

Define:

```text
solve(available, k)
```

Meaning:

```text
return the zero-based kth lexicographic permutation
of the currently available sorted values
```

At a state with:

```text
m values
```

each child has:

```text
(m - 1)!
```

leaves.

So:

```text
childIndex = k / (m - 1)!
localK     = k % (m - 1)!
```

Then:

```text
choose childIndex
remove chosen value
recurse on localK
```

---

## Recursion tree with block counts

For:

```text
[1,2,3,4]
```

root:

```text
                         []
        /                 |                 |                 \
      [1]               [2]               [3]               [4]
      3!                3!                3!                3!
   6 leaves          6 leaves          6 leaves          6 leaves
```

Inside `[2]`:

```text
                          [2]
                 /         |         \
              [2,1]     [2,3]      [2,4]
                2!         2!          2!
              2 leaves   2 leaves    2 leaves
```

Inside `[2,3]`:

```text
                    [2,3]
                    /   \
               [2,3,1] [2,3,4]
                  1!       1!
```

Kth Form follows only one path.

---

## Recursive pseudocode

```text
kthPermutation(available, k):

    if available is empty:
        return

    m = available.size()

    blockSize = (m - 1)!

    choiceIndex = k / blockSize

    chosen = available[choiceIndex]

    output chosen

    remove chosen

    k = k % blockSize

    kthPermutation(available, k)
```

This recursion has one child per level.

---

# 25. Common Mistakes

## Mistake 1 — Forgetting `k--`

Problem:

```text
K is 1-based
```

Formula:

```text
choiceIndex = k / blockSize
```

is easiest with zero-based `k`.

Always:

```cpp
k--;
```

before the selection loop.

---

## Mistake 2 — Using N! as the first block size

At a state with `m` values:

```text
total permutations = m!
```

but each first-choice block has:

```text
(m - 1)!
```

Correct:

```text
blockSize = factorial[m - 1]
```

---

## Mistake 3 — Not updating K locally

After choosing a block:

```text
k must become its rank inside that block
```

Correct:

```cpp
k %= blockSize;
```

Without this, later positions are wrong.

---

## Mistake 4 — Generating permutations until reaching K

This may work for tiny `K`, but defeats the whole Kth Form idea.

Do not:

```text
generate 1st
generate 2nd
...
generate Kth
```

Instead:

```text
count whole blocks
skip them
```

---

## Mistake 5 — Using the easy O(N²) vector erase for N = 1e5

For the hard version, do not manipulate a 100000-element vector at every position.

Use:

```text
k <= 1e9
```

to prove the prefix is fixed.

Then manipulate only the last at-most-13 values.

---

## Mistake 6 — Computing huge factorials blindly

`100000!` is astronomically large and unnecessary.

We only need factorials until they exceed the maximum possible `k`.

Here:

```text
13! > 10^9
```

so factorials beyond 13 provide no additional information for block selection.

---

## Mistake 7 — Confusing value with choice index

Suppose:

```text
available = [4,7,9]
choiceIndex = 1
```

The chosen value is:

```text
7
```

not:

```text
1
```

`choiceIndex` indexes the remaining sorted values.

---

## Mistake 8 — Losing sorted order of available values

Lexicographic blocks assume choices are considered in increasing order.

Therefore the remaining candidates must stay ordered.

The vector erase method naturally preserves order.

---

## Mistake 9 — Thinking this is ordinary backtracking

There is a permutation tree, but we are not exploring the tree.

We are navigating directly to one leaf using subtree sizes.

So the better mental model is:

```text
indexed recursion
```

or:

```text
count-and-skip recursion
```

---

# 26. Kth Form Recognition Checklist

When a problem asks:

```text
Kth permutation
Kth string
Kth sequence
Kth generated object
Kth recursive action
```

ask these questions.

---

## 1. What is the generation order?

Example:

```text
lexicographic order
```

Without a fixed order, "Kth" is meaningless.

---

## 2. What are the choices at one level?

For permutations:

```text
any unused value
```

---

## 3. How many valid completions does each choice generate?

For `m` remaining distinct values:

```text
(m - 1)!
```

---

## 4. Are those outputs consecutive in the required order?

For lexicographic permutations:

```text
yes
```

All permutations beginning with a smaller next value appear before those beginning with a larger next value.

---

## 5. Which block contains K?

With zero-based `k`:

```text
choiceIndex = k / blockSize
```

---

## 6. What do I subtract when skipping earlier blocks?

Equivalent formulations:

```text
k -= choiceIndex * blockSize
```

or simply:

```text
k %= blockSize
```

---

## 7. Can I recurse into only one branch?

If yes, we have the Kth Form optimization.

---

# 27. Reusable Kth-Generation Template

Generic form:

```text
findKth(state, k):

    choices = ordered choices from state

    for each choice:

        count = number of outputs under this choice

        if k >= count:
            k -= count
            continue

        apply choice

        return findKth(nextState, k)
```

If every block has equal size:

```text
blockSize = count of one branch

choiceIndex = k / blockSize
k %= blockSize
```

Then directly choose that branch.

---

## Permutation specialization

```text
STATE:
    ordered remaining values + local k

LEVEL:
    current output position

CHOICE:
    one unused value

BLOCK SIZE:
    (remaining - 1)!

CHECK:
    which block contains k?

MOVE:
    append chosen value
    remove it
    k %= blockSize

PROGRESS:
    remaining decreases by one

BASE:
    no values remain
```

---

# 28. Connection to Kth Tower of Hanoi

The Kth permutation and Kth Tower of Hanoi look different, but the deeper pattern is the same.

---

## Kth Tower of Hanoi

Sequence:

```text
LEFT BLOCK
MIDDLE MOVE
RIGHT BLOCK
```

Known sizes:

```text
2^(n-1)-1
1
2^(n-1)-1
```

Use `K` to select one block.

---

## Kth permutation

Sequence:

```text
block for first choice 1
block for first choice 2
block for first choice 3
...
```

Known size of every block:

```text
(n-1)!
```

Use `K` to select one block.

---

## Shared pattern

```text
                 RECURSIVE OUTPUT ORDER
                           |
                           v
                  divide into blocks
                           |
                           v
                 know each block size
                           |
                           v
                     locate K
                           |
                           v
                  skip earlier blocks
                           |
                           v
                convert to local K
                           |
                           v
                  recurse into ONE block
```

This is the essence of **Kth Form**.

---

# 29. Final Mental Model

For normal permutation backtracking:

```text
                         []
              /           |           \
             1            2            3
            / \          / \          / \
           ...          ...          ...

Explore every branch.
```

For Kth permutation:

```text
                         []
              /           |           \
          block 0      block 1      block 2
             X            ↓            X
                       contains K

Follow only this branch.
```

At each level:

```text
remaining = m

blockSize = (m - 1)!

choiceIndex = k / blockSize

choose available[choiceIndex]

k %= blockSize
```

with:

```text
k = originalK - 1
```

---

## 60-second recognition rule

When you see:

```text
"Find the Kth object in a recursively generated ordered set"
```

ask immediately:

```text
How many answers are under each next choice?
```

If branch sizes can be counted:

```text
DO NOT GENERATE THEM.
```

Use:

```text
COUNT → LOCATE → SKIP → DESCEND
```

---

## Easy vs Hard Version

```text
EASY
n <= 9

Use all values.
Factorial blocks.
Vector erase is fine.

Time: O(n^2)
```

```text
HARD
n <= 100000
k <= 10^9

13! > 10^9
→ all but last at-most-13 values are forced
→ run factorial-block logic only on that suffix

Time: O(n)
```

---

## One-line memory rule

```text
Kth permutation =
factorial-sized lexicographic blocks
+ zero-based K
+ choose block
+ K %= blockSize
+ recurse only there
```
