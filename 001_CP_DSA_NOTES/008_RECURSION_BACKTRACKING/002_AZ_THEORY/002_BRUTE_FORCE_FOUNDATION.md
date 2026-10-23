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

A direct brute-force method is:

```text
permute digits 0..9

first five digits  → A
last five digits   → B

check:
A == N * B
```

---

## Search Space

```text
10! = 3,628,800
```

Each candidate needs only constant/small work.

So this is a perfectly meaningful brute-force baseline.

---

## Example Mapping

```text
Permutation:

9 4 7 3 6 0 1 5 2 8
|-------| |-------|
    A         B

A = 94736
B = 01528
```

Checker:

```text
94736 == 62 × 1528
94736 == 94736
TRUE
```

---

## C++17 Skeleton Using `next_permutation`

```cpp
#include <bits/stdc++.h>
using namespace std;

void solve(int n) {
    vector<int> digits = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

    do {
        long long a = 0;
        long long b = 0;

        for (int i = 0; i < 5; i++) {
            a = a * 10 + digits[i];
        }

        for (int i = 5; i < 10; i++) {
            b = b * 10 + digits[i];
        }

        if (b != 0 && a == 1LL * n * b) {
            cout << setw(5) << setfill('0') << a;
            cout << " / ";
            cout << setw(5) << setfill('0') << b;
            cout << " = " << n << '\n';
        }
    } while (next_permutation(digits.begin(), digits.end()));
}
```

This method is easy to reason about because uniqueness is guaranteed by the permutation itself.

But it generates more candidates than necessary.

---

# 11. Approach 2 — Generate One Number and Derive the Other

From:

```text
A / B = N
```

we derive:

```text
A = N × B
```

So only enumerate `B`.

For each `B`:

```text
B
↓
A = N * B
↓
A must fit in five decimal positions
↓
A and B together must use all ten digits once
```

---

## Search-Space Diagram

```text
Brute permutation approach
──────────────────────────
all assignments of 10 digits
        |
        v
      10!
        |
        v
check equation


Derived approach
────────────────
B = 00000
B = 00001
B = 00002
...
B = 99999
        |
        v
A = N * B
        |
        v
check 10 digits
```

The second generator has at most about:

```text
100,000
```

values instead of:

```text
3,628,800
```

and usually fewer because:

```text
N × B <= 99999
```

Therefore:

```text
B <= 99999 / N
```

---

## Why This Is Better

We removed an entire independent search dimension.

Bad model:

```text
choose A
choose B
check A = N * B
```

Better model:

```text
choose B
compute A = N * B
```

Whenever an equation uniquely determines one variable from another, try to **derive instead of enumerate**.

---

# 12. Step-by-Step Checker for All Ten Digits

We need to verify:

```text
A has 5 digit positions
B has 5 digit positions

and across all 10 positions:
every digit 0..9 appears exactly once
```

There are two clean methods.

---

## Method A — Frequency Array

```cpp
bool validDigits(int a, int b) {
    int frequency[10] = {};

    for (int i = 0; i < 5; i++) {
        frequency[a % 10]++;
        a /= 10;
    }

    for (int i = 0; i < 5; i++) {
        frequency[b % 10]++;
        b /= 10;
    }

    for (int digit = 0; digit <= 9; digit++) {
        if (frequency[digit] != 1) {
            return false;
        }
    }

    return true;
}
```

This is very easy to understand.

---

## Method B — 10-bit Mask

Since there are exactly ten possible digits, use one bit per digit.

```text
digit 0 → bit 0
digit 1 → bit 1
...
digit 9 → bit 9
```

Initially:

```text
mask = 0000000000
```

When digit `d` appears:

```text
bit = 1 << d
```

If:

```text
mask & bit
```

is already non-zero, `d` appeared before.

Otherwise:

```text
mask |= bit
```

---

## Dry Run — `79546` and `01283`

Read exactly five positions from each number.

```text
A = 79546
digits from right: 6,4,5,9,7

B = 01283
integer value 1283
five extracted positions: 3,8,2,1,0
```

Combined order:

```text
6,4,5,9,7,3,8,2,1,0
```

All are different.

Final mask:

```text
1111111111₂
```

which is:

```text
(1 << 10) - 1
```

---

## Mask Checker

```cpp
bool validDigits(int a, int b) {
    int mask = 0;

    for (int i = 0; i < 5; i++) {
        int digit = a % 10;
        int bit = 1 << digit;

        if (mask & bit) {
            return false;
        }

        mask |= bit;
        a /= 10;
    }

    for (int i = 0; i < 5; i++) {
        int digit = b % 10;
        int bit = 1 << digit;

        if (mask & bit) {
            return false;
        }

        mask |= bit;
        b /= 10;
    }

    return mask == (1 << 10) - 1;
}
```

Notice the fixed five iterations.

That is what preserves leading zeros.

---

# 13. Complete C++17 Solution

This implementation follows the reduced search:

```text
generate denominator
→ derive numerator
→ check digits
```

```cpp
#include <bits/stdc++.h>
using namespace std;

bool validDigits(int numerator, int denominator) {
    int mask = 0;

    for (int i = 0; i < 5; i++) {
        int digit = numerator % 10;
        int bit = 1 << digit;

        if (mask & bit) {
            return false;
        }

        mask |= bit;
        numerator /= 10;
    }

    for (int i = 0; i < 5; i++) {
        int digit = denominator % 10;
        int bit = 1 << digit;

        if (mask & bit) {
            return false;
        }

        mask |= bit;
        denominator /= 10;
    }

    return mask == (1 << 10) - 1;
}

void solve(int n) {
    bool found = false;

    for (int denominator = 0; 1LL * denominator * n <= 99999; denominator++) {
        int numerator = denominator * n;

        if (!validDigits(numerator, denominator)) {
            continue;
        }

        found = true;

        cout << setw(5) << setfill('0') << numerator;
        cout << " / ";
        cout << setw(5) << setfill('0') << denominator;
        cout << " = " << n << '\n';
    }

    if (!found) {
        cout << "No valid solution\n";
    }
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    solve(n);

    return 0;
}
```

---

## Compact Dry Run for `N = 62`

We do **not** need to manually trace all denominators.

Focus on one successful candidate.

```text
denominator = 1283

numerator = 1283 * 62
          = 79546

Check fixed-width fields:

numerator   = 79546
denominator = 01283

digits:
7,9,5,4,6,0,1,2,8,3

all 10 digits unique → VALID

79546 / 01283 = 62
```

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
