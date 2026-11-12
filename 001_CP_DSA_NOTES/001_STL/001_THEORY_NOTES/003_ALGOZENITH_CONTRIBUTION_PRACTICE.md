# AlgoZenith — Contribution Technique Practice
## Visual Self-Learning Version

# Table of Contents

- [0. Prerequisites](#0-prerequisites)
- [1. Common Contribution Framework](#1-common-contribution-framework)
- [2. Base Formula — LEFT × RIGHT](#2-base-formula--left--right)
- [3. Count Distinct Char in Substrings](#3-count-distinct-char-in-substrings)
- [4. Count Unique Char in Substrings](#4-count-unique-char-in-substrings)
- [5. Super Minimum Sum](#5-super-minimum-sum)
- [6. Pattern Comparison](#6-pattern-comparison)
- [7. Contest Recognition Checklist](#7-contest-recognition-checklist)
- [8. Final Formula Summary](#8-final-formula-summary)

---

# 0. Prerequisites

Before solving these contribution problems, be comfortable with the following ideas.

## 0.1 Substring / Subarray

A substring or subarray is contiguous.

```text
s = A B A C

valid substrings:

A
AB
ABA
BAC
ABAC

not a substring:

AC
```

The same idea applies to arrays.

---

## 0.2 Contribution Technique

Instead of enumerating every substring:

```text
all substrings
    ↓
compute answer for each
    ↓
sum everything
```

reverse the thinking:

```text
fix one occurrence / index
        ↓
count how many substrings
let this occurrence contribute
        ↓
contribution
=
value × frequency
```

For character-count problems:

```text
value = 1
```

For minimum-sum problems:

```text
value = a[i]
```

---

## 0.3 Multiplication Principle

If:

```text
left boundary has A valid choices

and

right boundary has B valid choices
```

then:

```text
number of valid substrings
=
A × B
```

Why?

Every valid left endpoint can pair with every valid right endpoint.

---

## 0.4 Previous / Next Occurrence

For a character occurrence at index `i`:

```text
prev = previous index containing same character
next = next index containing same character
```

If none exists:

```text
prev = -1
next = n
```

These virtual boundaries make formulas clean.

---

## 0.5 Monotonic Stack

For minimum-contribution problems, we need:

```text
previous smaller
next smaller or equal
```

A monotonic stack finds these boundaries in:

```text
O(n)
```

instead of scanning left/right for every index.

---

## 0.6 Strict vs Non-Strict Comparison

With duplicate values, two equal elements must not claim the same subarray.

Typical ownership rule:

```text
left:
previous strictly smaller

right:
next smaller-or-equal
```

This assigns every subarray to exactly one equal occurrence.

---

# 1. Common Contribution Framework

Use this same thinking for all three problems.

```text
STEP 1
Fix one occurrence/index i.

STEP 2
Start from the normal number of substrings containing i.

STEP 3
Ask:
"What blocks this occurrence from contributing?"

STEP 4
Restrict LEFT and/or RIGHT choices.

STEP 5
frequency
=
validLeftChoices × validRightChoices

STEP 6
contribution
=
value × frequency

STEP 7
Sum all contributions.
```

ASCII memory map:

```text
                 FIX i
                   |
        +----------+----------+
        |                     |
   valid LEFT             valid RIGHT
    choices                 choices
        |                     |
        +----------+----------+
                   |
                   v
             frequency
          = LEFT × RIGHT
                   |
                   v
            contribution
        = value × frequency
```

---

# 2. Base Formula — LEFT × RIGHT

For a normal subarray containing index `i`:

```text
0 ... i ... n-1
```

How many left endpoints?

```text
L can be:

0
1
2
...
i

leftChoices = i + 1
```

How many right endpoints?

```text
R can be:

i
i+1
...
n-1

rightChoices = n - i
```

Therefore:

```text
total subarrays containing i
=
(i + 1) × (n - i)
```

ASCII:

```text
index:

0   1   2   3   4   5
            ^
            i

LEFT choices:
0 -> i
1 -> i
2 -> i

RIGHT choices:
i -> 3
i -> 4
i -> 5

Every LEFT can pair with every RIGHT.
```

This is the starting point for every problem below.

Normal:

```text
(i+1)(n-i)
```

Restricted:

```text
validLeftChoices × validRightChoices
```

---

# 3. Count Distinct Char in Substrings

**Problem:** [Maang — Count Distinct Char in Substrings](https://maang.in/problems/Count-Distinct-Char-in-Substrings-62)

## 3.1 What is the problem asking?

For every substring:

```text
count how many DIFFERENT characters it contains
```

Then add those counts.

Example:

```text
s = "ABA"

"A"   → 1
"B"   → 1
"A"   → 1
"AB"  → 2
"BA"  → 2
"ABA" → 2

answer = 9
```

---

## 3.2 Brute Force

Generate every substring.

For each substring:

```text
use a set / frequency array
count distinct characters
```

This is far too slow for large `n`.

Contribution question:

```text
For occurrence s[i],
how many substrings should receive
+1 distinct-character contribution
from THIS occurrence?
```

---

## 3.3 Start from the Base Formula

Normally index `i` belongs to:

```text
(i+1)(n-i)
```

substrings.

But for **distinct** contribution, if the substring already contains the previous same character, this occurrence should not add another `+1`.

So only the LEFT side gets restricted.

```text
normal:

left  = i + 1
right = n - i

distinct:

left  = i - prev
right = n - i
```

---

## 3.4 Visual Example

Take:

```text
s = A B A C
        ^
        i = 2
        current = A
```

Previous `A`:

```text
prev = 0
```

For this second `A` to be the occurrence responsible for distinct `A`:

```text
L must start AFTER index 0
```

Valid left endpoints:

```text
L = 1
L = 2
```

ASCII:

```text
index:   0   1   2   3
char:    A   B   A   C
         ^       ^
        prev     i

INVALID starts:

L = 0
[A B A ...]
contains previous A already

VALID starts:

L = 1
[B A ...]

L = 2
[A ...]
```

Right endpoint is unrestricted:

```text
R = 2
R = 3
```

So:

```text
leftChoices  = 2 - 0 = 2
rightChoices = 4 - 2 = 2

contribution = 2 × 2 = 4
```

Substrings where second `A` contributes distinct `A`:

```text
"A"
"AC"
"BA"
"BAC"
```

---

## 3.5 Step-by-Step Derivation

Fix:

```text
s[i]
```

Let:

```text
prev = previous occurrence of s[i]
```

To make current occurrence responsible:

```text
prev < L <= i
```

Therefore:

```text
L ∈ [prev+1 ... i]
```

Number of choices:

```text
leftChoices = i - prev
```

Right endpoint may be anywhere:

```text
R ∈ [i ... n-1]
```

Number of choices:

```text
rightChoices = n - i
```

So:

```text
frequency(i)
=
(i-prev)(n-i)
```

Each valid substring gets:

```text
+1 distinct character
```

Therefore:

```text
contribution(i)
=
(i-prev)(n-i)
```

No character value multiplication is needed.

---

## 3.6 Dry Run — `"ABA"`

```text
n = 3
last occurrence initially = -1
```

### `i = 0`, `'A'`

```text
prev = -1

left  = 0-(-1) = 1
right = 3-0    = 3

contribution = 3
```

Substrings:

```text
"A"
"AB"
"ABA"
```

### `i = 1`, `'B'`

```text
prev = -1

left  = 1-(-1) = 2
right = 3-1    = 2

contribution = 4
```

### `i = 2`, second `'A'`

```text
prev = 0

left  = 2-0 = 2
right = 3-2 = 1

contribution = 2
```

Final:

```text
answer
=
3 + 4 + 2
=
9
```

---

## 3.7 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

long long solve(const string& s) {
    int n = s.size();

    vector<int> last(256, -1);
    long long ans = 0;

    for (int i = 0; i < n; i++) {
        int prev = last[(unsigned char)s[i]];

        long long leftChoices = i - prev;
        long long rightChoices = n - i;

        ans += leftChoices * rightChoices;

        last[(unsigned char)s[i]] = i;
    }

    return ans;
}
```

---

## 3.8 Complexity

```text
Time  = O(n)
Space = O(1) for fixed alphabet
```

---

## 3.9 Recognition Signal

Think of this pattern when:

```text
for every substring,
count DISTINCT categories/characters
```

Mental question:

```text
Which occurrence should be responsible
for this character's +1 contribution?
```

Answer:

```text
current occurrence owns substrings
whose L starts after previous same occurrence
```

---

## 3.10 Common Mistake

Wrong:

```text
use next same occurrence too
```

For **distinct count**, only previous occurrence restricts ownership.

The right boundary is unrestricted.

---

# 4. Count Unique Char in Substrings

**Problem:** [Maang — Count Unique Char in Substrings](https://maang.in/problems/Count-Unique-Char-in-Substrings-63)

## 4.1 What is the problem asking?

For every substring:

```text
count characters appearing EXACTLY ONCE
```

Then add those counts.

Example:

```text
s = "ABA"

"A"   → 1
"B"   → 1
"A"   → 1
"AB"  → 2
"BA"  → 2
"ABA" → 1

answer = 8
```

---

## 4.2 Difference from DISTINCT

For DISTINCT:

```text
at least one occurrence is enough
```

For UNIQUE:

```text
exactly one occurrence must be present
```

So current occurrence `s[i]` must avoid both:

```text
previous same character
next same character
```

Therefore both LEFT and RIGHT are restricted.

---

## 4.3 Visual Example

Take:

```text
s = A B A C A
        ^
        i = 2
        current = A
```

Previous `A`:

```text
prev = 0
```

Next `A`:

```text
next = 4
```

For index `2` to be the **only** `A` inside substring:

```text
L must be after prev

and

R must be before next
```

ASCII:

```text
index:   0   1   2   3   4
char:    A   B   A   C   A
         ^       ^       ^
        prev     i      next

valid region:

            +-------+
            | A  C  |
        B   | A  C  |
            +-------+

L ∈ {1,2}
R ∈ {2,3}
```

So:

```text
leftChoices  = 2 - 0 = 2
rightChoices = 4 - 2 = 2

frequency = 2 × 2 = 4
```

---

## 4.4 Step-by-Step Derivation

Fix:

```text
s[i] = c
```

Let:

```text
prev = previous occurrence of c
next = next occurrence of c
```

For current `c` to appear exactly once:

```text
prev < L <= i <= R < next
```

Left choices:

```text
L ∈ [prev+1 ... i]

leftChoices = i - prev
```

Right choices:

```text
R ∈ [i ... next-1]

rightChoices = next - i
```

Therefore:

```text
frequency(i)
=
(i-prev)(next-i)
```

Each valid substring gets:

```text
+1 unique character
```

So:

```text
contribution(i)
=
(i-prev)(next-i)
```

Final:

```text
answer
=
Σ (i-prev[i])(next[i]-i)
```

---

## 4.5 ASCII Ownership Picture

```text
previous c            current c            next c
    |                     |                   |
    v                     v                   v

----P---------------------i-------------------N----

Valid L:
P+1 ... i

Valid R:
i ... N-1

Every pair (L,R) creates
one substring where current c is UNIQUE.
```

---

## 4.6 Dry Run — `"ABA"`

Indices:

```text
0 1 2
A B A
```

### First `A`, `i = 0`

```text
prev = -1
next = 2

left  = 1
right = 2

contribution = 2
```

Valid:

```text
"A"
"AB"
```

Not:

```text
"ABA"
```

because `A` appears twice.

---

### `B`, `i = 1`

```text
prev = -1
next = 3

left  = 2
right = 2

contribution = 4
```

---

### Second `A`, `i = 2`

```text
prev = 0
next = 3

left  = 2
right = 1

contribution = 2
```

Final:

```text
answer
=
2 + 4 + 2
=
8
```

---

## 4.7 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

long long solve(const string& s) {
    int n = s.size();

    vector<vector<int>> pos(256);

    for (int i = 0; i < n; i++) {
        pos[(unsigned char)s[i]].push_back(i);
    }

    long long ans = 0;

    for (auto& p : pos) {
        for (int j = 0; j < (int)p.size(); j++) {
            int i = p[j];

            int prev = (j == 0 ? -1 : p[j - 1]);
            int next = (j + 1 == (int)p.size() ? n : p[j + 1]);

            long long leftChoices = i - prev;
            long long rightChoices = next - i;

            ans += leftChoices * rightChoices;
        }
    }

    return ans;
}
```

---

## 4.8 Complexity

```text
Time  = O(n)
Space = O(n)
```

---

## 4.9 Recognition Signal

Think of this pattern when:

```text
for every substring,
count characters/categories appearing EXACTLY ONCE
```

Mental picture:

```text
previous same
      |
      v
------P------i------N------
             ^
          current
                    ^
                 next same

current owns only substrings
fully inside (P,N)
and containing i
```

---

## 4.10 Common Mistake

Distinct and unique are different.

```text
DISTINCT:
previous same blocks LEFT only

UNIQUE:
previous same blocks LEFT
next same blocks RIGHT
```

---

# 5. Super Minimum Sum

**Problem:** [Maang — Super Minimum Sum](https://maang.in/problems/Super-Minimum-Sum-78)

## 5.1 What is the problem asking?

For every contiguous subarray:

```text
find its minimum
```

Then add all minimum values.

Example:

```text
a = [3,1,2]

[3]     → 3
[1]     → 1
[2]     → 2
[3,1]   → 1
[1,2]   → 1
[3,1,2] → 1

answer = 9
```

---

## 5.2 Brute Force

Enumerate all subarrays.

For each:

```text
scan to find minimum
```

Too slow.

Contribution question:

```text
For a[i],
how many subarrays choose THIS occurrence
as their minimum?
```

---

## 5.3 Start from the Base Formula

Without restrictions:

```text
left  = i+1
right = n-i
```

But a smaller value blocks the expansion.

Let:

```text
P = previous strictly smaller index
N = next smaller-or-equal index
```

Then:

```text
valid left  = i - P
valid right = N - i
```

So:

```text
frequency(i)
=
(i-P)(N-i)
```

Contribution:

```text
a[i](i-P)(N-i)
```

---

## 5.4 ASCII Boundary Example

Take:

```text
a = [4, 2, 3, 1, 5]
         ^
         i = 1
         value = 2
```

Previous smaller than `2`:

```text
none
P = -1
```

Next smaller-or-equal:

```text
1 at index 3
N = 3
```

ASCII:

```text
index:   -1   0   1   2   3   4
               4   2   3   1   5
                   ^
                   i

P = -1 -------------------|
                          |
                 valid zone
                 [4,2,3]
                          |
                          N = 3 blocks
```

Left choices:

```text
L = 0 or 1

leftChoices = 2
```

Right choices:

```text
R = 1 or 2

rightChoices = 2
```

So value `2` is minimum in:

```text
[2]
[2,3]
[4,2]
[4,2,3]
```

Frequency:

```text
2 × 2 = 4
```

Contribution:

```text
2 × 4 = 8
```

---

## 5.5 Step-by-Step Derivation

Fix index `i`.

To keep `a[i]` as minimum:

```text
we may expand left
until a strictly smaller value appears

we may expand right
until a smaller-or-equal value appears
```

Let:

```text
P = previous strictly smaller
N = next smaller-or-equal
```

Valid starts:

```text
L ∈ [P+1 ... i]
```

Count:

```text
leftChoices = i - P
```

Valid ends:

```text
R ∈ [i ... N-1]
```

Count:

```text
rightChoices = N - i
```

Therefore:

```text
frequency(i)
=
(i-P)(N-i)
```

Each such subarray contributes:

```text
a[i]
```

So:

```text
contribution(i)
=
a[i](i-P)(N-i)
```

Final:

```text
answer
=
Σ a[i](i-P)(N-i)
```

---

## 5.6 Why One Side Strict and One Side Non-Strict?

Consider:

```text
[2,2]
```

Subarrays:

```text
[first 2]
[second 2]
[2,2]
```

For `[2,2]`, both elements are minimum.

But contribution counting must assign the subarray to only one occurrence.

So choose one ownership convention.

Example used here:

```text
LEFT:
previous strictly smaller

RIGHT:
next smaller-or-equal
```

ASCII:

```text
index:  0   1
value:  2   2
        A   B

With asymmetric tie rule:

A owns:
[A]

B owns:
[B]
[A,B]

No double count.
```

---

## 5.7 Dry Run — `[3,1,2]`

### `3` at `i = 0`

```text
P = -1
N = 1

left  = 1
right = 1

contribution
=
3×1×1
=
3
```

---

### `1` at `i = 1`

```text
P = -1
N = 3

left  = 2
right = 2

contribution
=
1×2×2
=
4
```

Owned subarrays:

```text
[1]
[3,1]
[1,2]
[3,1,2]
```

---

### `2` at `i = 2`

```text
P = 1
N = 3

left  = 1
right = 1

contribution
=
2×1×1
=
2
```

Final:

```text
answer
=
3 + 4 + 2
=
9
```

---

## 5.8 Why Monotonic Stack?

Naively, for every `i`:

```text
scan left until smaller
scan right until smaller/equal
```

Worst case:

```text
O(n^2)
```

Monotonic stack finds all boundaries in:

```text
O(n)
```

because every index is:

```text
pushed once
popped at most once
```

---

## 5.9 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

long long solve(vector<long long>& a) {
    int n = a.size();

    vector<int> left(n), right(n);
    stack<int> st;

    // Previous strictly smaller
    for (int i = 0; i < n; i++) {
        while (!st.empty() && a[st.top()] >= a[i]) {
            st.pop();
        }

        left[i] = st.empty() ? -1 : st.top();
        st.push(i);
    }

    while (!st.empty()) {
        st.pop();
    }

    // Next smaller-or-equal
    for (int i = n - 1; i >= 0; i--) {
        while (!st.empty() && a[st.top()] > a[i]) {
            st.pop();
        }

        right[i] = st.empty() ? n : st.top();
        st.push(i);
    }

    long long ans = 0;

    for (int i = 0; i < n; i++) {
        long long leftChoices = i - left[i];
        long long rightChoices = right[i] - i;

        ans += a[i] * leftChoices * rightChoices;
    }

    return ans;
}
```

---

## 5.10 Complexity

```text
Time  = O(n)
Space = O(n)
```

---

## 5.11 Recognition Signal

Think:

```text
fix each element as MINIMUM/MAXIMUM
```

Then ask:

```text
How far left can it own?
How far right can it own?
```

This very often means:

```text
monotonic stack
+
LEFT × RIGHT
```

---

## 5.12 Common Mistakes

```text
1. Using smaller on both sides with duplicates.
2. Using smaller-or-equal on both sides.
3. Multiplying wrong boundary distances.
4. Forgetting virtual boundaries:
   -1 and n.
```

---

# 6. Pattern Comparison

All three problems start from the same base:

```text
normal containing-i count
=
(i+1)(n-i)
```

Then the problem tells us what blocks contribution.

---

## 6.1 Distinct Character

Restriction:

```text
previous same character blocks LEFT
```

Formula:

```text
(i-prev)(n-i)
```

ASCII:

```text
prev same      current
    |             |
----P-------------i-------------------->

L must be after P
R unrestricted
```

---

## 6.2 Unique Character

Restriction:

```text
previous same blocks LEFT
next same blocks RIGHT
```

Formula:

```text
(i-prev)(next-i)
```

ASCII:

```text
previous        current         next
   |               |              |
---P---------------i--------------N---

L inside (P,i]
R inside [i,N)
```

---

## 6.3 Subarray Minimum

Restriction:

```text
smaller value blocks LEFT
smaller/equal value blocks RIGHT
```

Formula:

```text
a[i](i-P)(N-i)
```

ASCII:

```text
smaller          current        smaller/equal
   |                |                |
---P----------------i----------------N---

all subarrays inside this ownership zone
that contain i choose a[i] as minimum
```

---

# 7. Contest Recognition Checklist

When you see a contribution problem, ask these in order.

```text
1. Can I FIX one occurrence/index?

2. What is the normal LEFT × RIGHT count?

3. What makes this occurrence invalid?

4. Does something block the LEFT side?

5. Does something block the RIGHT side?

6. Are the blockers:
   same value?
   same character?
   smaller value?
   greater value?

7. Can I find those blockers in O(n)?
   last occurrence?
   next occurrence?
   monotonic stack?

8. What does each valid substring contribute?
   1?
   a[i]?
   frequency?
   another weight?
```

---

# 8. Final Formula Summary

| Problem | Left Choices | Right Choices | Per-Occurrence Contribution |
|---|---:|---:|---:|
| Normal subarray sum | `i+1` | `n-i` | `a[i](i+1)(n-i)` |
| Distinct chars | `i-prev` | `n-i` | `(i-prev)(n-i)` |
| Unique chars | `i-prev` | `next-i` | `(i-prev)(next-i)` |
| Subarray minimum | `i-P` | `N-i` | `a[i](i-P)(N-i)` |

---

## Final Memory Diagram

```text
NORMAL
======

             i
             |
0 -----------|------------- n-1

LEFT  = i+1
RIGHT = n-i


DISTINCT
========

prev same    i
    |        |
----P--------|-------------------->

LEFT  = i-P
RIGHT = n-i


UNIQUE
======

prev same    i       next same
    |        |           |
----P--------|-----------N--------

LEFT  = i-P
RIGHT = N-i


MINIMUM
=======

prev smaller i    next smaller/equal
      |       |            |
------P-------|------------N--------

LEFT  = i-P
RIGHT = N-i

contribution
=
a[i] × LEFT × RIGHT
```

---

# Final Takeaway

Do not memorize three unrelated formulas.

Remember one framework:

```text
START:
(i+1)(n-i)

THEN ASK:
what blocks LEFT?
what blocks RIGHT?

REPLACE:
normal boundaries
with restricted boundaries

THEN:
frequency = LEFT × RIGHT

FINALLY:
contribution = value × frequency
```

That is the reusable contribution pattern behind all three problems.
