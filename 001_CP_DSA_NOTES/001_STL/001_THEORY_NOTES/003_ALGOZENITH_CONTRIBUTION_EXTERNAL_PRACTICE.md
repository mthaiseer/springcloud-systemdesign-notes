# AlgoZenith — Contribution Technique Practice

## Table of Contents

1. [Count Distinct Char in Substrings](#1-count-distinct-char-in-substrings)
2. [Count Unique Char in Substrings](#2-count-unique-char-in-substrings)
3. [Super Minimum Sum](#3-super-minimum-sum)
4. [Final Formula Summary](#4-final-formula-summary)

---


## Base Formula Used in Every Problem

For a normal subarray containing index `i`:

```text
left choices  = i + 1
right choices = n - i
```

Therefore:

```text
total subarrays containing i
= (i + 1) × (n - i)
```

If every such subarray allows `a[i]` to contribute:

```text
contribution(i)
= a[i] × (i + 1) × (n - i)
```

For the problems below, use this as the **starting formula** and restrict the left/right boundaries when another equal/smaller element blocks the contribution.

```text
NORMAL:
(i+1) × (n-i)

RESTRICTED:
validLeftChoices × validRightChoices
```

---

# 1. Count Distinct Char in Substrings

**Problem:** [Maang — Count Distinct Char in Substrings](https://maang.in/problems/Count-Distinct-Char-in-Substrings-62)

## What is the problem asking?

For every substring:

```text
count how many different characters it contains
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

Brute force generates all substrings.

Contribution technique asks:

```text
For this occurrence s[i],
how many substrings get +1 distinct-character contribution from it?
```

## Start From `(i+1) × (n-i)`

Normally, index `i` belongs to:

```text
(i+1) × (n-i)
```

substrings.

But for **distinct-character contribution**, if the substring also starts before the previous same character, this occurrence must not contribute another `+1`.

So restrict the normal left choices:

```text
normal left choices = i+1
valid left choices  = i-prev
```

The right side is unrestricted:

```text
right choices = n-i
```

Hence:

```text
(i+1)(n-i)
        ↓ restrict left
(i-prev)(n-i)
```

## Contribution Idea

Fix occurrence `s[i]`.

Let:

```text
prev = previous index containing s[i]
```

To make this occurrence responsible for the character's distinct contribution:

```text
L must be after prev
L <= i
```

So:

```text
L ∈ [prev+1 ... i]

leftChoices = i - prev
```

The substring may end anywhere:

```text
R ∈ [i ... n-1]

rightChoices = n - i
```

Therefore:

```text
contribution(i)
= (i-prev)(n-i)
```

## Algebraic Derivation

```text
left choices  = i - prev
right choices = n - i

number of substrings
= leftChoices × rightChoices

contribution(i)
= (i-prev)(n-i)
```

Why no `s[i]` multiplication?

```text
Each occurrence contributes +1
to the DISTINCT COUNT,
not its character value.
```

## Dry Run — `"ABA"`

```text
n = 3
last occurrence initially = -1
```

### `i = 0`, `'A'`

```text
prev = -1

left  = 0-(-1) = 1
right = 3-0    = 3

contribution = 1×3 = 3
```

These are:

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

contribution = 2×2 = 4
```

### `i = 2`, second `'A'`

Previous `A` is at index `0`.

```text
prev = 0

left  = 2-0 = 2
right = 3-2 = 1

contribution = 2×1 = 2
```

Therefore:

```text
answer
= 3 + 4 + 2
= 9
```

## C++

```cpp
#include <bits/stdc++.h>
using namespace std;

long long solve(const string& s) {
    int n = s.size();

    vector<int> last(256, -1);
    long long ans = 0;

    for (int i = 0; i < n; i++) {
        int prev = last[s[i]];

        long long left  = i - prev;
        long long right = n - i;

        ans += left * right;

        last[s[i]] = i;
    }

    return ans;
}
```

**Complexity:** `O(n)` time, `O(1)` alphabet space.

---

# 2. Count Unique Char in Substrings

**Problem:** [Maang — Count Unique Char in Substrings](https://maang.in/problems/Count-Unique-Char-in-Substrings-63)

## What is the problem asking?

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
"ABA" → 1   // only B occurs exactly once

answer = 8
```

## Start From `(i+1) × (n-i)`

Normally:

```text
left  = i+1
right = n-i
```

and:

```text
subarrays containing i
= (i+1)(n-i)
```

For `s[i]` to be **unique**, we cannot include either the previous or the next occurrence of the same character.

Therefore both sides become restricted:

```text
normal:
(i+1)(n-i)

restricted:
(i-prev)(next-i)
```

## Contribution Idea

Fix occurrence:

```text
s[i] = c
```

For this occurrence to be **unique** inside a substring, that substring must contain:

```text
this c
```

but must NOT contain:

```text
previous c
next c
```

Let:

```text
prev = previous occurrence of c
next = next occurrence of c
```

Then the left boundary can be:

```text
L ∈ [prev+1 ... i]
```

so:

```text
leftChoices = i-prev
```

The right boundary can be:

```text
R ∈ [i ... next-1]
```

so:

```text
rightChoices = next-i
```

Therefore:

```text
contribution(i)
= (i-prev)(next-i)
```

## Algebraic Derivation

For occurrence `i`:

```text
prev < L <= i <= R < next
```

Number of `(L,R)` pairs:

```text
(i-prev) × (next-i)
```

Hence:

```text
answer
= Σ (i-prev[i])(next[i]-i)
```

Again, each valid occurrence adds:

```text
+1 unique character
```

so there is no multiplication by the character itself.

## Dry Run — `"ABA"`

Indices:

```text
0 1 2
A B A
```

### First `A`, `i = 0`

```text
prev = -1
next = 2

left  = 0-(-1) = 1
right = 2-0    = 2

contribution = 2
```

Valid substrings:

```text
"A"
"AB"
```

Not `"ABA"` because it contains another `A`.

### `B`, `i = 1`

```text
prev = -1
next = n = 3

left  = 1-(-1) = 2
right = 3-1    = 2

contribution = 4
```

### Second `A`, `i = 2`

```text
prev = 0
next = 3

left  = 2-0 = 2
right = 3-2 = 1

contribution = 2
```

Therefore:

```text
answer
= 2 + 4 + 2
= 8
```

## C++

Store all occurrence positions. Add virtual boundaries:

```text
-1 and n
```

```cpp
#include <bits/stdc++.h>
using namespace std;

long long solve(const string& s) {
    int n = s.size();

    vector<vector<int>> pos(256);

    for (int i = 0; i < n; i++)
        pos[s[i]].push_back(i);

    long long ans = 0;

    for (auto& p : pos) {
        for (int j = 0; j < (int)p.size(); j++) {
            int i = p[j];

            int prev = (j == 0 ? -1 : p[j - 1]);
            int next = (j + 1 == (int)p.size() ? n : p[j + 1]);

            long long left  = i - prev;
            long long right = next - i;

            ans += left * right;
        }
    }

    return ans;
}
```

**Complexity:** `O(n)` time, `O(n)` occurrence storage.

---

# 3. Super Minimum Sum

**Problem:** [Maang — Super Minimum Sum](https://maang.in/problems/Super-Minimum-Sum-78)

## What is the problem asking?

For every contiguous subarray:

```text
find its minimum
```

Then add all those minimums.

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

Instead of processing every subarray, fix `a[i]`.

Ask:

```text
In how many subarrays is a[i] the minimum?
```

## Start From `(i+1) × (n-i)`

If there were no minimum restriction, `a[i]` belongs to:

```text
(i+1)(n-i)
```

subarrays.

For `a[i]` to contribute as the **minimum**, we cannot extend through a blocking smaller element.

So replace the normal boundaries:

```text
normal left  = i+1
normal right = n-i
```

with:

```text
valid left  = i-P
valid right = N-i
```

where:

```text
P = previous strictly smaller index
N = next smaller-or-equal index
```

Thus:

```text
a[i](i+1)(n-i)
        ↓ restrict boundaries
a[i](i-P)(N-i)
```

## Contribution Idea

For `a[i]` to remain the chosen minimum, expand:

```text
left  until a smaller element blocks us
right until a smaller/equal element blocks us
```

Let:

```text
P = index of previous strictly smaller element
N = index of next smaller-or-equal element
```

Then:

```text
leftChoices  = i-P
rightChoices = N-i
```

Therefore:

```text
frequency(i)
= (i-P)(N-i)
```

Since every such subarray contributes minimum `a[i]`:

```text
contribution(i)
= a[i](i-P)(N-i)
```

Final:

```text
answer
= Σ a[i](i-P)(N-i)
```

A monotonic stack finds `P` and `N` in `O(n)`.

## Why One Side Strict and One Side Non-Strict?

With duplicates:

```text
[2,2]
```

both `2`s must not claim the same subarray.

Use:

```text
previous strictly smaller
next smaller-or-equal
```

to give every subarray to exactly one occurrence.

## Dry Run — `[3,1,2]`

### `3` at `i=0`

```text
previous smaller = -1
next <= 3        = index 1

left  = 0-(-1) = 1
right = 1-0    = 1

contribution
= 3×1×1
= 3
```

### `1` at `i=1`

No smaller element exists on either side:

```text
P = -1
N = 3

left  = 1-(-1) = 2
right = 3-1    = 2

contribution
= 1×2×2
= 4
```

It owns:

```text
[1]
[3,1]
[1,2]
[3,1,2]
```

### `2` at `i=2`

```text
P = 1
N = 3

left  = 2-1 = 1
right = 3-2 = 1

contribution
= 2×1×1
= 2
```

Therefore:

```text
answer
= 3 + 4 + 2
= 9
```

## C++

```cpp
#include <bits/stdc++.h>
using namespace std;

long long solve(vector<long long>& a) {
    int n = a.size();

    vector<int> left(n), right(n);
    stack<int> st;

    // Previous strictly smaller
    for (int i = 0; i < n; i++) {
        while (!st.empty() && a[st.top()] >= a[i])
            st.pop();

        left[i] = st.empty() ? -1 : st.top();
        st.push(i);
    }

    while (!st.empty())
        st.pop();

    // Next smaller-or-equal
    for (int i = n - 1; i >= 0; i--) {
        while (!st.empty() && a[st.top()] > a[i])
            st.pop();

        right[i] = st.empty() ? n : st.top();
        st.push(i);
    }

    long long ans = 0;

    for (int i = 0; i < n; i++) {
        long long leftChoices  = i - left[i];
        long long rightChoices = right[i] - i;

        ans += a[i] * leftChoices * rightChoices;
    }

    return ans;
}
```

**Complexity:** `O(n)` time, `O(n)` space.

---

# 4. Final Formula Summary

Start every subarray contribution problem with:

```text
LEFT × RIGHT

= (i+1) × (n-i)
```

Then ask whether the problem imposes a restriction.

| Problem | Base | Restriction | Final Contribution |
|---|---|---|---|
| Normal subarray sum | `(i+1)(n-i)` | none | `a[i](i+1)(n-i)` |
| Distinct chars | `(i+1)(n-i)` | previous same char blocks left | `(i-prev)(n-i)` |
| Unique chars | `(i+1)(n-i)` | previous + next same char | `(i-prev)(next-i)` |
| Subarray minimum | `(i+1)(n-i)` | smaller elements block both sides | `a[i](i-P)(N-i)` |

The common contribution model is:

```text
FIX one occurrence
        ↓
find valid LEFT choices
        ↓
find valid RIGHT choices
        ↓
frequency = LEFT × RIGHT
        ↓
contribution = value × frequency
```

For character-count problems:

```text
value = 1
```

For minimum-sum problems:

```text
value = a[i]
```
