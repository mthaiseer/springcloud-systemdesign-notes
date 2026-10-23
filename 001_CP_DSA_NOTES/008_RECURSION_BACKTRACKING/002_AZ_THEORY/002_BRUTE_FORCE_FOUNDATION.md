# Brute Force Foundation — Generate, Check, Reduce the Search Space

> **Goal:** Build a strong brute-force foundation before recursion and backtracking.
>
> The main lesson is not “try random loops.”  
> It is:
>
> **1. Identify the complete search space.**  
> **2. Estimate its size.**  
> **3. Generate candidates systematically.**  
> **4. Check each candidate quickly.**  
> **5. Use equations/constraints to reduce what must be generated.**
>
> This is the bridge from **Brute Force → Recursion → Backtracking**.

---

## Table of Contents

1. [What Brute Force Really Means](#1-what-brute-force-really-means)
2. [Prerequisite 1 — Search Space and Candidate](#2-prerequisite-1--search-space-and-candidate)
3. [Prerequisite 2 — Constraints Decide Whether Brute Force Is Possible](#3-prerequisite-2--constraints-decide-whether-brute-force-is-possible)
4. [Prerequisite 3 — Counting the Search Space](#4-prerequisite-3--counting-the-search-space)
5. [Prerequisite 4 — Generation vs Checking](#5-prerequisite-4--generation-vs-checking)
6. [Prerequisite 5 — Unique Mapping / Permutations](#6-prerequisite-5--unique-mapping--permutations)
7. [Prerequisite 6 — Leading Zeros and Fixed-Width Numbers](#7-prerequisite-6--leading-zeros-and-fixed-width-numbers)
8. [Prerequisite 7 — Convert a Condition into a Faster Check](#8-prerequisite-7--convert-a-condition-into-a-faster-check)
9. [Main Lecture Problem — `abcde / fghij = N`](#9-main-lecture-problem--abcde--fghij--n)
10. [Approach 1 — Generate All 10! Digit Mappings](#10-approach-1--generate-all-10-digit-mappings)
11. [Approach 2 — Generate One Number and Derive the Other](#11-approach-2--generate-one-number-and-derive-the-other)
12. [Step-by-Step Checker for All Ten Digits](#12-step-by-step-checker-for-all-ten-digits)
13. [Complete C++17 Solution](#13-complete-c17-solution)
14. [Search-Tree View](#14-search-tree-view)
15. [Brute Force vs Backtracking](#15-brute-force-vs-backtracking)
16. [Common Brute-Force Generation Forms](#16-common-brute-force-generation-forms)
17. [Common Mistakes](#17-common-mistakes)
18. [Recognition Checklist](#18-recognition-checklist)
19. [Final Mental Model](#19-final-mental-model)

---

# 1. What Brute Force Really Means

When we first see a problem, the most direct solution is often:

```text
Generate every possible candidate
            ↓
Check whether the candidate is valid
            ↓
Keep / count / maximize / minimize valid candidates
```

That is **brute force**.

For an optimization problem:

```text
answer = best F(x)
         over every possible x
```

For example, if:

```text
x ∈ {0,1,2,...,9}
```

and we want the maximum value of `F(x)`, the most direct method is:

```text
F(0)
F(1)
F(2)
...
F(9)

take the maximum
```

Brute force is therefore primarily a **search idea**.

The implementation may use:

- loops,
- permutations,
- subsets,
- bitmasks,
- recursion,
- DFS,
- or later, **backtracking**.

---

# 2. Prerequisite 1 — Search Space and Candidate

Before coding, define two words.

## Candidate

A **candidate** is one possible answer/configuration that we may test.

Example:

```text
Digits = {0,1,2}

Possible 3-digit arrangements:

012
021
102
120
201
210
```

Each arrangement is one candidate.

---

## Search Space

The **search space** is the set of all candidates.

```text
Search Space
│
├── candidate 1
├── candidate 2
├── candidate 3
├── ...
└── candidate K
```

Brute force means exploring the whole required search space unless we can mathematically reduce it.

---

## Tiny Example

Suppose:

```text
Find the maximum value of x * (10 - x)
for 0 <= x <= 10
```

Brute force:

```text
x=0  → 0
x=1  → 9
x=2  → 16
x=3  → 21
x=4  → 24
x=5  → 25   ← maximum
x=6  → 24
...
```

Search space size:

```text
11 candidates
```

So brute force is obviously acceptable.

---

# 3. Prerequisite 2 — Constraints Decide Whether Brute Force Is Possible

The first important habit is:

> **Estimate the number of generated states before writing the generator.**

Very rough competitive-programming intuition:

| Search Space | Typical Meaning |
|---|---|
| `10` | tiny |
| `10^3` | very small |
| `10^5` | usually easy |
| `10^6` | often fine |
| `10^7` | depends on work per state |
| `2^20 ≈ 10^6` | often manageable |
| `10! ≈ 3.6 × 10^6` | often manageable |
| `15! ≈ 1.3 × 10^12` | impossible to enumerate directly |

The important point is not a universal cutoff.

It is:

```text
Total Work
=
Number of generated candidates
×
Cost of checking one candidate
```

---

## Example

Suppose we generate all permutations of ten digits.

```text
10! = 3,628,800
```

If each permutation needs only about `O(10)` work:

```text
≈ 36 million simple operations
```

This may be completely reasonable.

But if each permutation requires an `O(N^2)` expensive check, it may no longer be practical.

---

# 4. Prerequisite 3 — Counting the Search Space

Understanding factorials and permutations is essential for brute force.

---

## 4.1 Permutation of N distinct objects

Number of ways to arrange all `N` objects:

```text
N!
```

Example:

```text
{A,B,C}

ABC
ACB
BAC
BCA
CAB
CBA
```

Therefore:

```text
3! = 6
```

---

## 4.2 Permutation Tree

For `{0,1,2}`:

```text
                         []
                   /      |      \
                  0       1       2
                /  \     / \     / \
               1    2   0   2   0   1
               |    |   |   |   |   |
               2    1   2   0   1   0
               ↓    ↓   ↓   ↓   ↓   ↓
              012  021 102 120 201 210
```

Level sizes:

```text
Level 0: 1 choice state
Level 1: 3 choices
Level 2: 2 remaining choices
Level 3: 1 remaining choice

Leaves = 3 × 2 × 1 = 3! = 6
```

For ten different digits:

```text
10 × 9 × 8 × ... × 1 = 10!
```

---

## 4.3 Choosing only K ordered elements

If order matters and we choose `K` values from `N` distinct values:

```text
P(N,K) = N! / (N-K)!
```

Example:

```text
Choose an ordered 2-digit number from {1,2,3}

12
13
21
23
31
32

P(3,2) = 3 × 2 = 6
```

---

# 5. Prerequisite 4 — Generation vs Checking

This distinction from the lecture is extremely important.

A brute-force solution usually has two parts:

```text
             BRUTE FORCE
            /           \
      GENERATOR          CHECKER
     (usually larger)   (should be fast)

Generate candidate      Is candidate valid?
Generate candidate      Does it satisfy equation?
Generate candidate      Does it improve answer?
```

---

## Generator

Responsible for visiting every candidate that may contain the answer.

Examples:

```text
for x = 0..N
all subsets
all permutations
all strings
all placements
```

---

## Checker

Given one candidate, answer quickly:

```text
valid or invalid?
```

Example:

```cpp
bool valid(int candidate) {
    return candidate % 7 == 0;
}
```

---

## Key Design Question

Do **not** automatically generate the largest obvious search space.

Ask:

> Can I generate fewer values and derive the rest?

This is one of the most useful brute-force optimizations.

---

# 6. Prerequisite 5 — Unique Mapping / Permutations

The lecture example uses the ten decimal digits:

```text
0 1 2 3 4 5 6 7 8 9
```

Every digit must be assigned to exactly one position among:

```text
a b c d e f g h i j
```

Conceptually:

```text
positions:   a b c d e f g h i j
             ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓
digits:      ? ? ? ? ? ? ? ? ? ?
```

Example assignment:

```text
a b c d e f g h i j
9 4 7 3 6 0 1 5 2 8
```

This produces:

```text
abcde = 94736
fghij = 01528
```

Every digit appears exactly once.

---

## Why 10!?

For position `a`:

```text
10 choices
```

For `b`:

```text
9 remaining choices
```

Then:

```text
8, 7, 6, ...
```

Therefore:

```text
10 × 9 × 8 × ... × 1 = 10!
                          = 3,628,800
```

ASCII tree:

```text
                         a
          /      /      ...       \
         0      1                  9
        /       |                  |
       b        b                  b
   9 choices 9 choices         9 choices
      ...      ...                ...
                 ↓
             10! leaves
```

This is already small enough to be a valid brute-force idea.

But we can do better.

---

# 7. Prerequisite 6 — Leading Zeros and Fixed-Width Numbers

This problem contains an easy-to-miss detail.

A block such as:

```text
01283
```

is conceptually a **five-digit field**, even though as an integer it is:

```text
1283
```

So when checking its digits, we must still include the leading zero.

---

## Wrong digit extraction

```cpp
while (num > 0) {
    int digit = num % 10;
    num /= 10;
}
```

For:

```text
num = 1283
```

this sees:

```text
1,2,8,3
```

but misses the leading `0` from:

```text
01283
```

---

## Correct fixed-width extraction

If the number must represent exactly five positions:

```cpp
for (int i = 0; i < 5; i++) {
    int digit = num % 10;
    num /= 10;
}
```

For `1283`:

```text
Iteration 1 → 3
Iteration 2 → 8
Iteration 3 → 2
Iteration 4 → 1
Iteration 5 → 0   ← leading zero is correctly processed
```

This is extremely important for digit-assignment brute force.

---

# 8. Prerequisite 7 — Convert a Condition into a Faster Check

Suppose the candidate must satisfy:

```text
abcde / fghij = N
```

Let:

```text
A = abcde
B = fghij
```

Then:

```text
A / B = N
```

Instead of repeatedly thinking about division, rewrite it:

```text
A = N × B
```

This changes the design completely.

---

## Naive thought

Generate both `A` and `B`.

```text
A choices × B choices
```

That can be unnecessarily large.

---

## Better thought

Generate only `B`.

Then:

```text
A = N × B
```

So for every chosen `B`, there is at most one possible `A`.

Diagram:

```text
Generate B
    |
    v
A = N * B
    |
    v
Check digit constraints
    |
   / \
valid invalid
```

This is an important general brute-force pattern:

> **Generate one degree of freedom and derive everything else from the equation.**

---

# 9. Main Lecture Problem — `abcde / fghij = N`

We want two fixed-width five-digit values:

```text
abcde
fghij
```

such that:

```text
abcde / fghij = N
```

and the ten positions together use:

```text
0,1,2,3,4,5,6,7,8,9
```

exactly once.

Equivalently:

```text
A = N × B
```

with all ten digits of the two five-position fields distinct.

---

## Lecture Example: N = 62

One valid assignment visible in the lecture is:

```text
79546
-----
01283
```

Check:

```text
1283 × 62
= 1283 × (60 + 2)
= 76980 + 2566
= 79546
```

Therefore:

```text
79546 / 01283 = 62
```

Digits used:

```text
79546 → {7,9,5,4,6}
01283 → {0,1,2,8,3}
```

Together:

```text
{0,1,2,3,4,5,6,7,8,9}
```

exactly once.

Another lecture example:

```text
94736 / 01528 = 62
```

because:

```text
1528 × 62
= 1528 × 60 + 1528 × 2
= 91680 + 3056
= 94736
```

Again all ten digits are used exactly once.

---

# 10. Approach 1 — Generate All 10! Digit Mappings

The lecture's first option is:

```text
generate using permutation
→ check the equation
```

Because every permutation of `0..9` already uses every digit exactly once, the digit-uniqueness condition is automatically satisfied.

---

## Search Space

```text
10! = 3,628,800
```

---

## Example Mapping

```text
Permutation:

9 4 7 3 6 0 1 5 2 8
|-------| |-------|
  abcde     fghij

abcde = 94736
fghij = 01528
```

Then check:

```text
abcde = N × fghij
```

For `N = 62`:

```text
94736 = 62 × 1528
94736 = 94736
```

so the candidate is valid.

---

## Same Lecture Code Structure — `option_one()`

```cpp
void option_one(int n) {
    // Generate using permutation, check equation.
    int arr[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    int cnt = 0;

    do {
        int abcde = 10000 * arr[0] + 1000 * arr[1] + 100 * arr[2] + 10 * arr[3] + arr[4];
        int fghij = 10000 * arr[5] + 1000 * arr[6] + 100 * arr[7] + 10 * arr[8] + arr[9];

        if (abcde == fghij * n) {
            cout << setw(5) << setfill('0') << abcde << " ";
            cout << setw(5) << setfill('0') << fghij << '\n';
            cnt++;
        }
    } while (next_permutation(arr, arr + 10));

    cout << "count = " << cnt << '\n';
}
```

### Flow

```text
arr = [0,1,2,3,4,5,6,7,8,9]
                |
                v
       next_permutation()
                |
                v
      one complete mapping
                |
        +-------+-------+
        |               |
        v               v
 first 5 digits     last 5 digits
     abcde             fghij
        \               /
         \             /
          v           v
        check abcde = n * fghij
```

> The lecture screen shows the quotient-style idea `abcde / fghij == n`.  
> Here we use the exact equivalent `abcde == fghij * n`, which avoids integer-division false positives.

---

# 11. Approach 2 — Generate One Number and Derive the Other

The lecture's second option is:

```text
generate using equation
→ check permutation/distinct digits
```

Start from:

```text
abcde / fghij = N
```

Rearrange:

```text
abcde = fghij × N
```

Now `abcde` does not need to be generated independently.

Generate only:

```text
fghij
```

then derive:

```text
abcde = fghij * N
```

---

## Search-Space Reduction

### Option 1

```text
generate all digit mappings
→ 10!
→ check equation
```

### Option 2

```text
generate fghij
→ abcde = fghij * N
→ check 10 distinct digit positions
```

ASCII:

```text
OPTION 1

all permutations of 0..9
          |
          v
     3,628,800
          |
          v
   check equation


OPTION 2

fghij = 01234
fghij = 01235
fghij = 01236
      ...
          |
          v
 abcde = fghij * N
          |
          v
 check the 10 digits
```

Since:

```text
abcde <= 98765
```

we need:

```text
fghij * N <= 98765
```

so:

```text
fghij <= 98765 / N
```

---

# 12. Step-by-Step Checker for All Ten Digits

The lecture uses:

```cpp
set<int> st;
```

A set stores unique digits only.

If the ten digit positions together contain every decimal digit exactly once:

```text
0,1,2,3,4,5,6,7,8,9
```

then:

```text
st.size() == 10
```

---

## Why We Process Exactly Five Digits

Suppose:

```text
fghij = 01283
```

As an integer:

```text
fghij = 1283
```

But the conceptual field still has five positions:

```text
0 1 2 8 3
```

So the lecture checks exactly five times:

```cpp
for (int i = 0; i < 5; i++) {
    st.insert(temp % 10);
    temp /= 10;
}
```

Dry run:

```text
temp = 1283

i=0 → digit=3 → temp=128
i=1 → digit=8 → temp=12
i=2 → digit=2 → temp=1
i=3 → digit=1 → temp=0
i=4 → digit=0 → temp=0
```

The last iteration correctly recovers the leading zero.

---

## Lecture Checker

```cpp
set<int> st;

int temp = abcde;
for (int i = 0; i < 5; i++) {
    st.insert(temp % 10);
    temp /= 10;
}

temp = fghij;
for (int i = 0; i < 5; i++) {
    st.insert(temp % 10);
    temp /= 10;
}

if (st.size() == 10) {
    // Valid mapping.
}
```

---

## Detailed Example — `79546 / 01283 = 62`

### Step 1 — Generate `fghij`

```text
fghij = 1283
```

Conceptually:

```text
01283
```

### Step 2 — Derive `abcde`

```text
abcde = fghij × N
      = 1283 × 62
      = 79546
```

### Step 3 — Insert five digits of `abcde`

```text
79546

6 → insert
4 → insert
5 → insert
9 → insert
7 → insert
```

Set:

```text
{4,5,6,7,9}
```

### Step 4 — Insert five positions of `fghij`

```text
01283

3 → insert
8 → insert
2 → insert
1 → insert
0 → insert
```

Final set:

```text
{0,1,2,3,4,5,6,7,8,9}
```

Therefore:

```text
st.size() = 10
```

and the candidate is valid.

---

# 13. Complete C++17 Solution — Same Lecture Code

```cpp
#include <bits/stdc++.h>
using namespace std;

void option_one(int n) {
    // Generate using permutation, check equation.
    int arr[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    int cnt = 0;

    do {
        int abcde = 10000 * arr[0] + 1000 * arr[1] + 100 * arr[2] + 10 * arr[3] + arr[4];
        int fghij = 10000 * arr[5] + 1000 * arr[6] + 100 * arr[7] + 10 * arr[8] + arr[9];

        if (abcde == fghij * n) {
            cout << setw(5) << setfill('0') << abcde << " ";
            cout << setw(5) << setfill('0') << fghij << '\n';
            cnt++;
        }
    } while (next_permutation(arr, arr + 10));

    cout << "count = " << cnt << '\n';
}

void option_two(int n) {
    // Generate using equation, check distinct digits.
    int cnt = 0;

    // Conceptually starts from 01234.
    for (int fghij = 1234; fghij <= 98765 / n; fghij++) {
        int abcde = fghij * n;

        set<int> st;

        int temp = abcde;
        for (int i = 0; i < 5; i++) {
            st.insert(temp % 10);
            temp /= 10;
        }

        temp = fghij;
        for (int i = 0; i < 5; i++) {
            st.insert(temp % 10);
            temp /= 10;
        }

        if (st.size() == 10) {
            cnt++;

            cout << setw(5) << setfill('0') << abcde << " ";
            cout << setw(5) << setfill('0') << fghij << '\n';
        }
    }

    cout << "count = " << cnt << '\n';
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    option_two(n);

    return 0;
}
```

---

## Why the Code Uses `1234` Instead of Writing `01234`

The lecture visually represents the lower value as:

```text
01234
```

That is correct as a **five-position decimal representation**.

In C++ source code, write:

```cpp
1234
```

and restore the leading zero only when printing:

```cpp
cout << setw(5) << setfill('0') << fghij;
```

Output:

```text
01234
```

This preserves the lecture logic safely.

---

## Option 1 vs Option 2

| Property | Option 1 | Option 2 |
|---|---|---|
| Generate | permutation of digits | `fghij` |
| Derive | nothing | `abcde = fghij * n` |
| Checker | equation | distinct digits |
| Candidate count | `10!` | at most about `10^5` |
| Main idea | generate mapping | generate from equation |
| Better approach | baseline | **preferred** |

The important brute-force design lesson is:

```text
There are usually two parts:

GENERATOR + CHECKER
```

If one condition lets us **derive** part of the candidate, move that work from the generator into the checker/derivation.

That can dramatically reduce the search space.

---

# 14. Search-Tree View

Even if the current implementation uses loops, visualizing generation as a tree is excellent preparation for recursion/backtracking.

Suppose we generate a digit permutation.

```text
Level 0: choose digit for position a

                            []
              /              |             \
             0               1             ...
            / \             / \
           1   2           0   2
          /     \         /     \
        ...     ...      ...     ...
```

Every level means:

```text
one more decision has been fixed
```

For ten positions:

```text
Level 0  → choose a
Level 1  → choose b
Level 2  → choose c
Level 3  → choose d
Level 4  → choose e
Level 5  → choose f
Level 6  → choose g
Level 7  → choose h
Level 8  → choose i
Level 9  → choose j
Level 10 → complete candidate
```

---

## Number of Children Shrinks

```text
depth 0 → 10 choices
depth 1 →  9 choices
depth 2 →  8 choices
...
depth 9 →  1 choice
```

So:

```text
number of leaves = 10!
```

This is the recursion-tree interpretation of permutation generation.

---

# 15. Brute Force vs Backtracking

This is the most important bridge to the next topic.

## Plain Brute Force

Generate every complete candidate first.

```text
                            root
                    /       |       \
                   A        B        C
                 / | \    / | \    / | \
                ... ...  ... ...  ... ...
                  ↓         ↓         ↓
             complete   complete   complete
              check       check      check
```

Invalidity may be discovered only at the leaf.

---

## Backtracking

Check partial candidates while constructing them.

```text
                            root
                     /       |       \
                    A        B        C
                  / | \      X      / | \
                 /  X  \           X  |  \
                ↓      ↓              ↓   ↓
              valid   valid         valid valid
```

`X` means:

```text
partial candidate already violates a constraint
→ stop exploring that entire branch
```

---

## Core Relationship

```text
BRUTE FORCE
=
generate complete search space
+
check candidates


BACKTRACKING
=
generate search space recursively
+
check while building
+
stop bad branches early
```

Another useful way to remember it:

```text
Brute Force  → WHAT possibilities exist?
Backtracking → HOW can we explore only promising possibilities?
```

---

## LCCM Preview

For upcoming backtracking problems, organize the recursion using:

```text
L — Level
    What decision am I currently making?

C — Choice
    What options can I try here?

C — Check
    Is this choice currently valid?

M — Move
    Apply choice → recurse → undo choice
```

Generic tree:

```text
                         solve(level)
                       /      |      \
                  choice1  choice2  choice3
                     |        X        |
                  recurse            recurse
                     |                  |
                   undo               undo
```

---

# 16. Common Brute-Force Generation Forms

Recognizing the generator is a major competitive-programming skill.

| Search Object | Typical Generator | Search Size |
|---|---|---:|
| Integer in range | `for (x=L; x<=R; x++)` | `R-L+1` |
| Pair `(i,j)` | two loops | `O(N²)` |
| Triple `(i,j,k)` | three loops | `O(N³)` |
| Subset | bitmask / take-skip recursion | `2^N` |
| Permutation | `next_permutation` / recursion | `N!` |
| String of length `N`, alphabet size `K` | recursion / loops | `K^N` |
| Choose `K` items | combinations | `C(N,K)` |
| Unique assignment | permutation / backtracking | up to `N!` |

---

## 16.1 Pair Generation

```cpp
for (int i = 0; i < n; i++) {
    for (int j = i + 1; j < n; j++) {
        // candidate pair (i, j)
    }
}
```

Search size:

```text
C(N,2) = N(N-1)/2
```

---

## 16.2 Subset Generation

For `N` elements:

```text
each element has two choices:

TAKE
SKIP
```

Tree:

```text
                         index 0
                       /         \
                    TAKE         SKIP
                    /              \
               index 1           index 1
               /    \            /    \
            TAKE   SKIP       TAKE   SKIP
              ...                ...
```

Leaves:

```text
2^N
```

This is one of the most important recursion/backtracking forms.

---

## 16.3 Permutation Generation

At every level choose one unused element.

```text
                         []
                /         |         \
               A          B          C
              / \        / \        / \
             B   C      A   C      A   B
             |   |      |   |      |   |
             C   B      C   A      B   A
```

Leaves:

```text
N!
```

---

# 17. Common Mistakes

## Mistake 1 — Coding before estimating search size

Bad:

```text
"I can write 8 nested loops."
```

Better:

```text
How many states does that create?
```

Always estimate first.

---

## Mistake 2 — Generating variables that can be derived

If:

```text
A = N * B
```

do not independently enumerate both `A` and `B` unless necessary.

Generate:

```text
B
```

derive:

```text
A
```

---

## Mistake 3 — Forgetting leading zeros

For a five-position field:

```text
01283
```

must contribute the digit `0`.

Therefore process exactly five positions.

---

## Mistake 4 — Checking uniqueness separately

It is not enough that:

```text
A has unique digits
and
B has unique digits
```

A digit could still appear once in each.

We need uniqueness across:

```text
A + B together
```

---

## Mistake 5 — Confusing tree depth with total complexity

Permutation generation has depth:

```text
N
```

but up to:

```text
N!
```

leaves.

So:

```text
recursion depth != time complexity
```

The complete search tree determines total work.

---

## Mistake 6 — Using an expensive checker unnecessarily

If the generator creates millions of states, the checker should ideally be very small.

For ten digits:

```text
frequency[10]
```

or:

```text
10-bit mask
```

is enough.

---

# 18. Recognition Checklist

When a new problem looks brute-forceable, ask these questions in order.

### Step 1 — What exactly is a candidate?

```text
integer?
pair?
subset?
permutation?
assignment?
string?
path?
```

### Step 2 — How many candidates exist?

Write the search-space formula:

```text
N
N²
2^N
N!
K^N
C(N,K)
```

### Step 3 — What must be true for a candidate to be valid?

Turn the statement into a fast checker.

### Step 4 — Can an equation derive part of the candidate?

Example:

```text
A / B = N
→
A = N * B
```

### Step 5 — Can I reject partial candidates early?

If yes, we are moving naturally toward:

```text
BACKTRACKING
```

### Step 6 — What is the real total work?

```text
generated states × checker cost
```

---

# 19. Final Mental Model

Do not memorize brute force as “nested loops.”

Remember this pipeline:

```text
                 PROBLEM
                    |
                    v
          Define candidate/state
                    |
                    v
          Count the search space
                    |
          Is it small enough?
             /             \
           NO               YES
           |                 |
    find structure      choose generator
    / prune / DP              |
                              v
                        generate state
                              |
                              v
                         fast checker
                              |
                   valid? ----+---- no
                     |
                    yes
                     |
                     v
             count / print / optimize
```

And the most important optimization from this lecture:

```text
Do not generate what you can derive.
```

For the digit-division example:

```text
A / B = N
    ↓
A = N * B
    ↓
generate only B
    ↓
derive A
    ↓
check all 10 digits
```

Finally, the bridge to the next chapter is:

```text
Brute Force
    ↓
Generate every possibility

Recursion
    ↓
A natural way to represent the generation tree

Backtracking
    ↓
Recursion + choices + validity checks + pruning + undo
```

That is the foundation for:

```text
subsets
subsequences
permutations
combination problems
N-Queens
Sudoku
word search
constraint satisfaction
```

---

## 60-Second Revision

```text
BRUTE FORCE = GENERATE + CHECK

1. Define candidate.
2. Count candidates.
3. Estimate total work.
4. Generate systematically.
5. Keep checker fast.
6. Derive variables instead of enumerating them.
7. Handle fixed-width/leading-zero details carefully.
8. If partial states can be rejected early → backtracking.

Common search sizes:
N       → range
N²      → pairs
2^N     → subsets
N!      → permutations
K^N     → length-N strings over K choices

Lecture example:
abcde / fghij = N
→ A = N * B
→ generate B
→ compute A
→ verify digits 0..9 occur exactly once
```
