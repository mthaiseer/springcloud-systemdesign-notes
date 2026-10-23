# Backtracking Application — Permutations I & II with the LCCM Framework

> **Main idea:** A permutation problem becomes very easy to reason about when we define:
>
> **L = Level → C = Choice → C = Check → M = Move**
>
> For swap-based permutation generation:
>
> - **Level** = which index are we fixing now?
> - **Choice** = which remaining value should be placed at this index?
> - **Check** = is this value allowed at this level?
> - **Move** = swap it into position → recurse → swap back.

---

# Table of Contents

1. [Problems](#1-problems)
2. [Prerequisites](#2-prerequisites)
   - [2.1 What is a permutation?](#21-what-is-a-permutation)
   - [2.2 Why there are N! permutations](#22-why-there-are-n-permutations)
   - [2.3 What is the recursive state?](#23-what-is-the-recursive-state)
   - [2.4 Fixed prefix and unfixed suffix](#24-fixed-prefix-and-unfixed-suffix)
   - [2.5 Why swapping is useful](#25-why-swapping-is-useful)
   - [2.6 Backtracking = change → recurse → undo](#26-backtracking--change--recurse--undo)
   - [2.7 Recursion tree vs recursion stack](#27-recursion-tree-vs-recursion-stack)
3. [Permutation I — Distinct Elements](#3-permutation-i--distinct-elements)
4. [LCCM for Permutation I](#4-lccm-for-permutation-i)
5. [Step-by-Step Recursion Tree for `[1,2,3]`](#5-step-by-step-recursion-tree-for-123)
6. [Detailed Dry Run](#6-detailed-dry-run)
7. [Complete C++17 — Permutations I](#7-complete-c17--permutations-i)
8. [Why the Swap Method Generates Every Permutation](#8-why-the-swap-method-generates-every-permutation)
9. [Complexity of Permutations I](#9-complexity-of-permutations-i)
10. [Permutation II — Duplicate Elements](#10-permutation-ii--duplicate-elements)
11. [Why the Basic Algorithm Produces Duplicates](#11-why-the-basic-algorithm-produces-duplicates)
12. [LCCM for Permutation II](#12-lccm-for-permutation-ii)
13. [The Critical Idea — One `tried` Set Per Level](#13-the-critical-idea--one-tried-set-per-level)
14. [Recursion Tree for `[1,1,2]`](#14-recursion-tree-for-112)
15. [Detailed Dry Run for Duplicate Pruning](#15-detailed-dry-run-for-duplicate-pruning)
16. [Complete C++17 — Permutations II](#16-complete-c17--permutations-ii)
17. [Why Duplicate Pruning Is Correct](#17-why-duplicate-pruning-is-correct)
18. [Permutation I vs Permutation II](#18-permutation-i-vs-permutation-ii)
19. [Common Mistakes](#19-common-mistakes)
20. [LCCM Recognition Checklist](#20-lccm-recognition-checklist)
21. [Final Mental Model](#21-final-mental-model)

---

# 1. Problems

## Problem 1 — Permutations

Given an array of **distinct integers**, return all possible permutations.

**Clickable problem link:**  
[LeetCode 46 — Permutations](https://leetcode.com/problems/permutations/)

Example:

```text
nums = [1,2,3]

answer =
[
  [1,2,3],
  [1,3,2],
  [2,1,3],
  [2,3,1],
  [3,2,1],
  [3,1,2]
]
```

---

## Problem 2 — Permutations II

Given an array that **may contain duplicates**, return all **unique permutations**.

**Clickable problem link:**  
[LeetCode 47 — Permutations II](https://leetcode.com/problems/permutations-ii/)

Example:

```text
nums = [1,1,2]

unique permutations =
[
  [1,1,2],
  [1,2,1],
  [2,1,1]
]
```

The difficulty is not generating permutations.

The difficulty is:

```text
do not generate the same permutation more than once
```

---

# 2. Prerequisites

---

## 2.1 What is a permutation?

A permutation is an ordering of all elements.

For:

```text
[1,2,3]
```

some possible orderings are:

```text
[1,2,3]
[1,3,2]
[2,1,3]
[2,3,1]
[3,1,2]
[3,2,1]
```

Every element appears exactly once in each permutation.

---

## 2.2 Why there are N! permutations

Suppose all `N` elements are distinct.

At position `0`:

```text
N choices
```

After fixing one element, at position `1`:

```text
N - 1 choices
```

Then:

```text
N - 2 choices
```

and so on.

Therefore:

```text
N × (N - 1) × (N - 2) × ... × 1
```

which is:

```text
N!
```

Example:

```text
N = 3

3! = 3 × 2 × 1 = 6
```

That is why `[1,2,3]` has exactly six permutations.

---

## 2.3 What is the recursive state?

We use:

```text
index
```

to mean:

> All positions before `index` are already fixed.  
> Now choose which value should be placed at position `index`.

So the recursive function means:

```text
allPermutations(index)

= generate every permutation obtainable
  by fixing positions index ... N - 1,
  while positions 0 ... index - 1
  are already finalized.
```

This function contract is the most important part of the solution.

---

## 2.4 Fixed prefix and unfixed suffix

Suppose:

```text
nums = [2,1,3]
index = 1
```

Then:

```text
[2 | 1,3]
 ↑    ↑
fixed remaining
```

Position `0` is already decided.

We are now deciding position `1`.

Visual:

```text
index:   0   1   2
         |   |   |
nums =  [2 | 1   3]
         ^
      fixed prefix

remaining candidates:
          [1,3]
```

At each recursive level:

```text
prefix grows by exactly one position
```

---

## 2.5 Why swapping is useful

Suppose:

```text
nums = [1,2,3]
index = 0
```

We need to choose which value occupies position `0`.

Instead of creating a new array, choose some position `i` and swap:

```cpp
swap(nums[index], nums[i]);
```

Example:

```text
choose value 3 for index 0

before:
[1,2,3]
 ↑   ↑
idx  i

swap(0,2)

after:
[3,2,1]
 ↑
position 0 is now fixed
```

Now recurse on:

```text
index = 1
```

This automatically means:

```text
only the remaining suffix is still free
```

---

## 2.6 Backtracking = change → recurse → undo

A recursive branch temporarily changes the array.

After that branch finishes, restore the parent state.

Example:

```text
start:
[1,2,3]

choose 2 for position 0:

swap(0,1)
      ↓
[2,1,3]

recurse and explore everything beginning with 2

then swap back:

swap(0,1)
      ↓
[1,2,3]
```

The classic pattern is:

```cpp
swap(nums[index], nums[i]);     // choose
allPermutations(index + 1);     // explore
swap(nums[index], nums[i]);     // undo
```

This is:

```text
CHOOSE → EXPLORE → UNDO
```

or in LCCM terminology:

```text
MOVE
```

---

## 2.7 Recursion tree vs recursion stack

The **recursion tree** shows every branch generated during the whole algorithm.

For `[1,2,3]`:

```text
                     []
             /        |        \
           [1]       [2]       [3]
          /   \      /   \      /   \
       [1,2] [1,3] [2,1] [2,3] [3,1] [3,2]
         |      |      |      |      |      |
     [1,2,3] [1,3,2] ...
```

The **recursion stack** shows only the currently active path.

Example while building `[2,3,1]`:

```text
allPermutations(0)
allPermutations(1)
allPermutations(2)
allPermutations(3)
```

Tree size determines most of the running time.

Stack depth is only:

```text
O(N)
```

---

# 3. Permutation I — Distinct Elements

Input:

```text
nums = [1,2,3]
```

Goal:

```text
generate every ordering
```

The key design decision is:

```text
one recursion level = one array position
```

At:

```text
index = 0
```

choose who occupies position `0`.

At:

```text
index = 1
```

choose who occupies position `1`.

At:

```text
index = 2
```

choose who occupies position `2`.

When:

```text
index == N
```

every position has been fixed.

Therefore we have one complete permutation.

---

# 4. LCCM for Permutation I

| Part | Question | Permutation answer |
|---|---|---|
| **Level** | What part are we fixing now? | Array position `index` |
| **Choice** | What values can occupy it? | Any value in positions `index ... N-1` |
| **Check** | Is this choice legal? | Always yes because values are distinct |
| **Move** | How do we apply it? | Swap into `index` → recurse → swap back |

The structure:

```text
LEVEL
index
  |
  v
CHOICE
i = index ... N-1
  |
  v
CHECK
none needed
  |
  v
MOVE
swap(index, i)
rec(index + 1)
swap(index, i)
```

---

## Recursive invariant

Before:

```text
allPermutations(index)
```

starts:

```text
positions [0 ... index - 1] are fixed
positions [index ... N - 1] are available for rearrangement
```

The function generates:

```text
every possible completion of the suffix
```

without changing the parent's array after it returns.

That final clause is guaranteed by the undo swap.

---

# 5. Step-by-Step Recursion Tree for `[1,2,3]`

Start:

```text
nums = [1,2,3]
index = 0
```

At level 0 we have three choices:

```text
put 1 first
put 2 first
put 3 first
```

Complete recursion tree:

```text
                              []
                     index = 0
                 /       |        \
                /        |         \
               1         2          3
               |         |          |
              [1]       [2]        [3]
           index=1    index=1     index=1
           /   \       /   \       /   \
          2     3     1     3     1     2
         /       \   /       \   /       \
      [1,2]   [1,3] [2,1] [2,3] [3,1] [3,2]
        |        |     |      |      |      |
        3        2     3      1      2      1
        |        |     |      |      |      |
   [1,2,3] [1,3,2] [2,1,3] [2,3,1] [3,1,2] [3,2,1]
```

Number of leaves:

```text
3 × 2 × 1 = 6
```

which equals:

```text
3!
```

---

## Same tree in swap language

Root:

```text
[1,2,3], index=0
```

Branch 1:

```text
swap(0,0)
→ [1,2,3]
```

Branch 2:

```text
swap(0,1)
→ [2,1,3]
```

Branch 3:

```text
swap(0,2)
→ [3,2,1]
```

Each child recursively permutes only the suffix.

---

# 6. Detailed Dry Run

Input:

```text
nums = [1,2,3]
```

Call:

```text
allPermutations(0)
```

---

## Branch 1 — first element = 1

```text
index=0, i=0
swap(0,0)
[1,2,3]
→ recurse index=1
```

At index 1:

```text
i=1
swap(1,1)
[1,2,3]
→ recurse index=2
```

At index 2:

```text
i=2
swap(2,2)
[1,2,3]
→ recurse index=3
```

Base case:

```text
index == 3
```

Store:

```text
[1,2,3]
```

Return.

Undo:

```text
swap(2,2)
```

Back at index 1, try:

```text
i=2
swap(1,2)
[1,3,2]
→ recurse
```

Base eventually stores:

```text
[1,3,2]
```

Undo:

```text
swap(1,2)

[1,2,3]
```

This restoration is essential.

---

## Branch 2 — first element = 2

Back at index 0:

```text
i=1
swap(0,1)

[2,1,3]
```

Recursively generate:

```text
[2,1,3]
[2,3,1]
```

Then undo:

```text
swap(0,1)

[1,2,3]
```

---

## Branch 3 — first element = 3

At index 0:

```text
i=2
swap(0,2)

[3,2,1]
```

Recursively generate:

```text
[3,2,1]
[3,1,2]
```

Then undo:

```text
swap(0,2)

[1,2,3]
```

Final answer contains all six permutations.

---

# 7. Complete C++17 — Permutations I

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    void allPermutations(int index, vector<int>& nums, vector<vector<int>>& ans) {
        if (index == nums.size()) {
            ans.push_back(nums);
            return;
        }

        for (int i = index; i < nums.size(); i++) {
            swap(nums[index], nums[i]);

            allPermutations(index + 1, nums, ans);

            swap(nums[index], nums[i]);
        }
    }

    vector<vector<int>> permute(vector<int>& nums) {
        vector<vector<int>> ans;

        allPermutations(0, nums, ans);

        return ans;
    }
};
```

---

## Read the code as LCCM

```cpp
allPermutations(index, ...)
```

### Level

```text
index
```

### Choice

```cpp
for (int i = index; i < nums.size(); i++)
```

Candidate:

```text
nums[i]
```

for the current position.

### Check

For distinct values:

```text
every candidate is allowed
```

so no explicit check is necessary.

### Move

```cpp
swap(nums[index], nums[i]);
allPermutations(index + 1, nums, ans);
swap(nums[index], nums[i]);
```

Exactly:

```text
PLACE → RECURSE → UNDO
```

---

# 8. Why the Swap Method Generates Every Permutation

We need to prove three ideas.

---

## 8.1 Every generated leaf is a valid permutation

At level 0:

```text
one original element is placed at position 0
```

At level 1:

```text
one of the remaining elements is placed at position 1
```

This continues until every original element occupies exactly one position.

Swapping never creates or deletes elements.

Therefore every leaf contains exactly the original elements once each.

So every leaf is a valid permutation.

---

## 8.2 Every permutation can be generated

Take any target permutation.

Example:

```text
target = [3,1,2]
```

At level 0, the loop eventually chooses the value `3`.

Swap it into position 0.

At level 1, among the remaining values, the loop eventually chooses `1`.

At level 2, only `2` remains.

Therefore there exists a root-to-leaf path for every possible ordering.

---

## 8.3 No duplicate leaves when input values are distinct

Suppose two recursion paths differ for the first time at level `k`.

They choose two different values for position `k`.

Therefore their final permutations differ at position `k`.

So two different paths cannot create the same final array.

This argument depends on:

```text
all values being distinct
```

That condition changes in Permutations II.

---

# 9. Complexity of Permutations I

There are:

```text
N!
```

complete permutations.

Each one is copied into the answer:

```cpp
ans.push_back(nums);
```

Copy cost:

```text
O(N)
```

Therefore output-sensitive time complexity is:

```text
O(N × N!)
```

Recursion depth:

```text
N
```

so auxiliary recursion stack:

```text
O(N)
```

Ignoring output storage.

The answer itself necessarily stores:

```text
N! arrays × N elements
```

so output space is:

```text
O(N × N!)
```

---

# 10. Permutation II — Duplicate Elements

Now input may contain equal values.

Example:

```text
nums = [1,1,2]
```

Mathematically, unique permutations are:

```text
[1,1,2]
[1,2,1]
[2,1,1]
```

Only:

```text
3
```

unique answers.

But the ordinary swap recursion sees the two `1`s as two separate positions.

This can produce duplicate recursion branches.

---

# 11. Why the Basic Algorithm Produces Duplicates

Label the identical values temporarily:

```text
[1a, 1b, 2]
```

At level 0, the ordinary loop can choose:

```text
1a
or
1b
```

But value-wise these are the same decision:

```text
"put 1 at position 0"
```

Tree without pruning:

```text
                         []
                    /     |     \
                  1a      1b      2
                  |       |       |
              subtree  subtree   ...
```

The first two subtrees produce the same value permutations.

So the problem is:

> At the same recursion level, do not start two branches using the same value.

That sentence is the entire key to Permutations II.

---

# 12. LCCM for Permutation II

| Part | Question | Permutations II answer |
|---|---|---|
| **Level** | What position are we fixing? | `index` |
| **Choice** | What remaining value can go here? | `nums[i]`, `i = index ... N-1` |
| **Check** | Have we already tried this value at this level? | Skip if yes |
| **Move** | How do we explore it? | Swap → recurse → swap back |

The only conceptual difference from Permutations I is:

```text
CHECK
```

Permutations I:

```text
no duplicate-value check needed
```

Permutations II:

```text
at each level, use each distinct value at most once
```

---

# 13. The Critical Idea — One `tried` Set Per Level

Inside each recursive call:

```cpp
unordered_set<int> tried;
```

This set records:

```text
which values have already been used
as the choice for the CURRENT index
```

Example:

```text
nums = [1,1,2]
index = 0
```

Initially:

```text
tried = {}
```

First candidate:

```text
nums[0] = 1

tried = {1}
```

Explore the entire branch beginning with `1`.

Later at the SAME level:

```text
nums[1] = 1
```

But:

```text
1 is already in tried
```

So skip it.

Then:

```text
nums[2] = 2
```

`2` has not been tried at this level.

Explore it.

---

## Why must the set be local?

At:

```text
index = 0
```

using value `1` once is enough for that position.

But at:

```text
index = 1
```

value `1` may absolutely be needed again.

Example valid permutation:

```text
[2,1,1]
```

So we must not globally mark value `1` as permanently used.

Correct:

```text
one fresh set for every recursion call / tree node
```

Wrong:

```text
one global set shared across every recursion level
```

Visual:

```text
level 0:
tried = {1,2}

      choose 1
         |
         v

level 1:
new tried = {}
```

The child level starts with a fresh duplicate filter.

---

# 14. Recursion Tree for `[1,1,2]`

We show only unique branches.

Start:

```text
[]
index = 0
tried = {}
```

Tree:

```text
                           []
                    tried = {}
                    /         \
                   1           2
                  /             \
                [1]             [2]
          tried = {}        tried = {}
             /    \              |
            1      2             1
            |      |             |
          [1,1]  [1,2]         [2,1]
            |      |             |
            2      1             1
            |      |             |
        [1,1,2] [1,2,1]      [2,1,1]
```

Why only two root children?

Input:

```text
[1,1,2]
```

At root:

```text
first 1 → explored
second 1 → duplicate at SAME level → skipped
2 → explored
```

---

## More explicit duplicate-pruning tree

```text
index = 0

[]
├── choose 1        ✓ first 1 at this level
│   │
│   └── index = 1
│       ├── choose 1
│       │   └── [1,1,2]
│       │
│       └── choose 2
│           └── [1,2,1]
│
├── choose 1        X already tried value 1 at index 0
│
└── choose 2        ✓
    │
    └── index = 1
        ├── choose 1
        │   └── [2,1,1]
        │
        └── choose 1
            X duplicate value 1 at index 1
```

The pruning rule is:

```text
duplicate VALUE at the SAME LEVEL → skip
```

not:

```text
duplicate value anywhere in the recursion → skip
```

---

# 15. Detailed Dry Run for Duplicate Pruning

Input:

```text
nums = [1,1,2]
```

Call:

```text
allPermutations(0)
```

---

## Level 0

```text
index = 0
tried = {}
nums = [1,1,2]
```

### i = 0

```text
value = 1
1 not in tried

tried = {1}
```

Swap:

```text
swap(0,0)

[1,1,2]
```

Call:

```text
allPermutations(1)
```

---

## Level 1 under prefix `[1]`

Fresh local set:

```text
tried = {}
nums = [1,1,2]
index = 1
```

### i = 1

```text
value = 1
not tried yet
```

Choose it:

```text
[1,1,2]
```

Eventually base case stores:

```text
[1,1,2]
```

Undo.

### i = 2

```text
value = 2
not tried at level 1
```

Swap:

```text
[1,2,1]
```

Base case stores:

```text
[1,2,1]
```

Undo.

Return to level 0.

---

## Back at level 0

Parent state is restored:

```text
[1,1,2]
```

and importantly the root's set is still:

```text
tried = {1}
```

### i = 1

Candidate:

```text
nums[1] = 1
```

But:

```text
tried.count(1) == true
```

So:

```text
SKIP
```

No duplicate subtree is generated.

---

### i = 2

Candidate:

```text
2
```

Not in root `tried`.

Insert:

```text
tried = {1,2}
```

Swap:

```text
swap(0,2)

[2,1,1]
```

Recurse.

At the next level, the two remaining values are both `1`.

A fresh child-level set allows the first `1` and skips the second duplicate `1`.

Final new permutation:

```text
[2,1,1]
```

So answer is exactly:

```text
[1,1,2]
[1,2,1]
[2,1,1]
```

---

# 16. Complete C++17 — Permutations II

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    void allPermutations(int index, vector<int>& nums, vector<vector<int>>& ans) {
        if (index == nums.size()) {
            ans.push_back(nums);
            return;
        }

        unordered_set<int> tried;

        for (int i = index; i < nums.size(); i++) {
            if (tried.count(nums[i])) {
                continue;
            }

            tried.insert(nums[i]);

            swap(nums[index], nums[i]);

            allPermutations(index + 1, nums, ans);

            swap(nums[index], nums[i]);
        }
    }

    vector<vector<int>> permuteUnique(vector<int>& nums) {
        vector<vector<int>> ans;

        allPermutations(0, nums, ans);

        return ans;
    }
};
```

---

# 17. Why Duplicate Pruning Is Correct

We must answer two questions.

---

## 17.1 Why is it safe to skip the second equal value at the same level?

Suppose at one level the unfixed suffix contains:

```text
[1a, 1b, 2]
```

If we place `1a` at the current position, recursion will permute everything remaining.

If instead we place `1b` at the same position, the visible value at that position is still:

```text
1
```

and the remaining multiset of values is still:

```text
{1,2}
```

Therefore both branches represent the same value-state:

```text
prefix + [1] + remaining multiset {1,2}
```

So exploring both is redundant.

One branch is enough.

---

## 17.2 Why may the same value appear again at a deeper level?

Because deeper level means:

```text
a different POSITION
```

For:

```text
[1,1,2]
```

we need permutations containing both copies of `1`.

Example:

```text
position 0 = 1
position 1 = 1
position 2 = 2
```

So duplicate pruning only applies to competing choices for the **same current position**.

Hence:

```text
one local tried-set per recursion level
```

is exactly the correct scope.

---

# 18. Permutation I vs Permutation II

| Idea | Permutations I | Permutations II |
|---|---|---|
| Input | All values distinct | Duplicates possible |
| Level | `index` | `index` |
| Choice | Any suffix element | Any suffix element |
| Check | None | Skip value already tried at this level |
| Move | swap → recurse → swap back | same |
| Base case | `index == N` | same |
| Result | all permutations | all unique permutations |
| Key extra structure | none | local `unordered_set<int>` |

The core recursion is unchanged.

Only the `CHECK` becomes stronger.

This is a major backtracking lesson:

> Often two problems share the same recursion tree structure.  
> The harder version simply adds a stronger feasibility / duplicate-pruning check.

---

# 19. Common Mistakes

## Mistake 1 — Forgetting the swap-back

Wrong:

```cpp
swap(nums[index], nums[i]);
allPermutations(index + 1, nums, ans);
```

The next sibling starts from the modified array.

Correct:

```cpp
swap(nums[index], nums[i]);
allPermutations(index + 1, nums, ans);
swap(nums[index], nums[i]);
```

---

## Mistake 2 — Starting the loop from `0`

Wrong:

```cpp
for (int i = 0; i < nums.size(); i++)
```

This can move already-fixed prefix elements again.

Correct:

```cpp
for (int i = index; i < nums.size(); i++)
```

Only the unfixed suffix contains legal candidates.

---

## Mistake 3 — Wrong base case

Correct:

```cpp
if (index == nums.size()) {
    ans.push_back(nums);
    return;
}
```

At this moment:

```text
every position is fixed
```

---

## Mistake 4 — Using one global `tried` set in Permutations II

Wrong idea:

```text
once value 1 is used anywhere,
never use 1 again
```

That destroys valid permutations containing repeated values.

Correct:

```text
fresh tried set inside every recursive call
```

It means:

```text
do not start two sibling branches
with the same value
```

---

## Mistake 5 — Confusing index with chosen value

`index` means:

```text
which POSITION are we filling?
```

`i` means:

```text
which remaining element are we considering
as the value for that position?
```

Example:

```text
index = 1
i = 2

swap(nums[1], nums[2])
```

means:

> Put the value currently at position 2 into the position currently being fixed, position 1.

---

## Mistake 6 — Adding a `visited[]` array unnecessarily

In the swap-based method, the array itself separates:

```text
fixed prefix
from
remaining suffix
```

Therefore we do not need a normal `visited[]` array for distinct-element permutations.

Swap-based state already encodes what remains.

---

## Mistake 7 — Copying the array at every call

You can write recursion by creating a new vector for every branch, but that introduces additional copying.

The swap-back pattern uses one shared vector:

```text
modify
→ recurse
→ restore
```

This is the standard backtracking style.

---

# 20. LCCM Recognition Checklist

For a permutation problem, ask:

1. **State:** Which prefix has already been fixed?
2. **Level:** Which position am I choosing now?
3. **Choice:** Which remaining element can occupy this position?
4. **Check:** Is this candidate allowed? Have I used this value already at this level?
5. **Move:** How do I put it into the current position?
6. **Explore:** What does the next recursive level represent?
7. **Undo:** How do I restore the parent's state?
8. **Base case:** When are all positions fixed?
9. **Output:** Store one permutation, count permutations, or stop at one?
10. **Duplicate pruning:** Are equal values creating identical sibling branches?

---

# 21. Final Mental Model

## Permutations I

```text
PERMUTATION
│
├── LEVEL
│   └── index being fixed
│
├── CHOICE
│   └── every remaining nums[i]
│
├── CHECK
│   └── none — all values distinct
│
└── MOVE
    ├── swap(index, i)
    ├── recurse(index + 1)
    └── swap(index, i) back
```

Recursion tree:

```text
                    FIX POSITION 0
                  /       |       \
                 1        2        3
                / \      / \      / \
               ...      ...      ...
```

---

## Permutations II

```text
UNIQUE PERMUTATION
│
├── LEVEL
│   └── index being fixed
│
├── CHOICE
│   └── every remaining nums[i]
│
├── CHECK
│   └── has nums[i] already been tried
│       at THIS level?
│
└── MOVE
    ├── mark value tried
    ├── swap
    ├── recurse
    └── swap back
```

The most important duplicate rule:

```text
same VALUE + same LEVEL
        ↓
duplicate subtree
        ↓
SKIP
```

But:

```text
same VALUE + deeper LEVEL
        ↓
different array position
        ↓
may be REQUIRED
```

And the universal backtracking pattern remains:

```text
LEVEL
  ↓
CHOICE
  ↓
CHECK
  ↓
PLACE
  ↓
RECURSE
  ↓
UNDO
```

For permutations specifically:

```text
FIX POSITION
   ↓
TRY REMAINING VALUE
   ↓
SKIP DUPLICATE IF NEEDED
   ↓
SWAP INTO POSITION
   ↓
RECURSE
   ↓
SWAP BACK
```

Once this tree becomes natural, many later problems become variations of the same structure:

```text
subsets
combination sum
palindrome partitioning
N-Queens
Sudoku
word search
k-combinations
string arrangements
constraint satisfaction
```

The surface problem changes.

The backtracking engine remains:

```text
LCCM + recursion tree + exact undo
```
