# Derangement — From Zero to Competitive Programming

> **Assumption:** You know permutations (`n!`) but have never studied derangements.
>
> **Goal:** Understand the idea first. Formula comes later.

---

## Table of Contents

- [1. Start With a Normal Permutation](#1-start-with-a-normal-permutation)
- [2. What Is a Fixed Position?](#2-what-is-a-fixed-position)
- [3. What Is a Derangement?](#3-what-is-a-derangement)
- [4. Example 1 — Two Objects](#4-example-1--two-objects)
- [5. Example 2 — Three Objects](#5-example-2--three-objects)
- [6. Example 3 — Four Objects](#6-example-3--four-objects)
- [7. Story Problems → Same Model](#7-story-problems--same-model)
- [8. Derangement Recurrence](#8-derangement-recurrence)
- [9. Why the Recurrence Works](#9-why-the-recurrence-works)
- [10. Dry Run of the Recurrence](#10-dry-run-of-the-recurrence)
- [11. C++ — Basic Version](#11-c--basic-version)
- [12. C++ — O(1) Memory](#12-c--o1-memory)
- [13. Modulo Version](#13-modulo-version)
- [14. Inclusion-Exclusion Formula](#14-inclusion-exclusion-formula)
- [15. Example — SWORD](#15-example--sword)
- [16. Example — Balls and Boxes](#16-example--balls-and-boxes)
- [17. Example — Forced Placement](#17-example--forced-placement)
- [18. How to Recognize Derangement](#18-how-to-recognize-derangement)
- [19. Common Mistakes](#19-common-mistakes)
- [20. Final Memory Card](#20-final-memory-card)

---

# 1. Start With a Normal Permutation

Suppose we have:

```text
A B C
```

A **permutation** means rearranging these objects.

Examples:

```text
ABC
ACB
BAC
BCA
CAB
CBA
```

Number of permutations:

```text
3! = 3 × 2 × 1 = 6
```

So:

```text
Permutation
=
arrange objects in different orders
```

Derangement is just a **special type of permutation**.

---

# 2. What Is a Fixed Position?

Start with:

```text
Position:  1   2   3
Original:  A   B   C
```

Consider:

```text
New:       A   C   B
```

Compare:

```text
Position:  1   2   3
Original:  A   B   C
New:       A   C   B
           ↑
         same
```

`A` stayed in its original position.

Therefore `A` is a **fixed point**.

Another example:

```text
Original:  A   B   C
New:       C   B   A
               ↑
             same
```

`B` is fixed.

### Mathematical notation

For a permutation `p`:

```text
p[i] = i
```

means position `i` is fixed.

Example:

```text
p = [2,1,3]

p[3] = 3
```

So position `3` is fixed.

---

# 3. What Is a Derangement?

A **derangement** is a permutation with:

```text
NO fixed positions
```

In other words:

```text
p[i] != i
for every i
```

Visual:

```text
Original: A   B   C
New:      B   C   A
          ✓   ✓   ✓
```

Check:

```text
Position 1: A → B   changed ✓
Position 2: B → C   changed ✓
Position 3: C → A   changed ✓
```

Therefore:

```text
BCA
```

is a derangement.

The number of derangements of `n` objects is written:

```text
D(n)
```

or sometimes:

```text
!n
```

---

# 4. Example 1 — Two Objects

Start with:

```text
A B
```

All permutations:

```text
AB
BA
```

Check them.

### AB

```text
Original: A B
New:      A B
          ↑ ↑
```

Both are fixed.

```text
AB ❌
```

### BA

```text
Original: A B
New:      B A
          ✓ ✓
```

Nobody stays in the original position.

```text
BA ✅
```

Therefore:

```text
D(2) = 1
```

---

# 5. Example 2 — Three Objects

Original:

```text
A B C
```

All `3! = 6` permutations:

```text
ABC   ❌
ACB   ❌
BAC   ❌
BCA   ✅
CAB   ✅
CBA   ❌
```

Let's inspect the valid ones.

### BCA

```text
Original: A B C
New:      B C A
          ✓ ✓ ✓
```

### CAB

```text
Original: A B C
New:      C A B
          ✓ ✓ ✓
```

Only two work.

Therefore:

```text
D(3) = 2
```

---

# 6. Example 3 — Four Objects

Now suppose:

```text
1 2 3 4
```

We need:

```text
p[1] != 1
p[2] != 2
p[3] != 3
p[4] != 4
```

Examples of valid derangements:

```text
2 1 4 3
2 3 4 1
2 4 1 3
3 1 4 2
...
```

The total is:

```text
D(4) = 9
```

At this point:

```text
D(1) = 0
D(2) = 1
D(3) = 2
D(4) = 9
```

Instead of generating every permutation, we need a formula.

---

# 7. Story Problems → Same Model

Derangement problems often hide behind stories.

## Story A — Gifts

```text
3 people
3 gifts
each person originally owns one gift

Requirement:
nobody receives their own gift
```

Remove nouns:

```text
person → position
gift   → object
```

Condition:

```text
object cannot return to original position
```

That is:

```text
DERANGEMENT
```

---

## Story B — Balls and Boxes

```text
Ball 1 → Box 1 is forbidden
Ball 2 → Box 2 is forbidden
Ball 3 → Box 3 is forbidden
...
```

Mathematically:

```text
p[i] != i
```

Again:

```text
DERANGEMENT
```

---

## Story C — Letters

Original word:

```text
S W O R D
```

Requirement:

```text
S cannot remain at position 1
W cannot remain at position 2
O cannot remain at position 3
R cannot remain at position 4
D cannot remain at position 5
```

Again:

```text
D(5)
```

### Core translation

```text
"No object stays where it originally belonged"
                    ↓
                p[i] != i
                    ↓
               DERANGEMENT
```

---

# 8. Derangement Recurrence

The most useful formula for programming is:

```text
D(0) = 1
D(1) = 0
```

and:

```text
D(n)
=
(n-1) × [D(n-1) + D(n-2)]
```

Don't memorize it yet.

First understand why.

---

# 9. Why the Recurrence Works

Suppose we have:

```text
1 2 3 ... n
```

Focus only on object `1`.

Object `1` cannot go to position `1`.

So it can choose:

```text
2, 3, 4, ..., n
```

Number of choices:

```text
n - 1
```

Suppose it chooses position `j`.

```text
1 → j
```

Now there are **two cases**.

---

## Case A — j goes to position 1

```text
1 → j
j → 1
```

ASCII:

```text
Position:  1   ...   j
           ↑         ↑
           j         1

           ↖─────────↘
             swap pair
```

These two objects are completely handled.

Remaining objects:

```text
n - 2
```

They must derange among themselves.

Ways:

```text
D(n-2)
```

---

## Case B — j does NOT go to position 1

We already have:

```text
1 → j
```

but:

```text
j → something else
```

The remaining structure behaves like a derangement problem on:

```text
n - 1 objects
```

Ways:

```text
D(n-1)
```

---

## Combine Both Cases

For one chosen destination `j`:

```text
D(n-2) + D(n-1)
```

But object `1` had:

```text
n - 1
```

choices for `j`.

Therefore:

```text
D(n)
=
(n-1) × [D(n-2) + D(n-1)]
```

Usually written:

```text
D(n)
=
(n-1) × [D(n-1) + D(n-2)]
```

### Entire idea in one diagram

```text
                    Object 1
                       │
              choose destination j
                       │
                  n-1 choices
                       │
             ┌─────────┴─────────┐
             │                   │
             ▼                   ▼
           j → 1             j → elsewhere
             │                   │
       pair completed       still connected
             │                   │
             ▼                   ▼
          D(n-2)              D(n-1)
             │                   │
             └─────────┬─────────┘
                       ▼
                D(n-2)+D(n-1)
                       │
                       ▼
        (n-1)[D(n-1)+D(n-2)]
```

---

# 10. Dry Run of the Recurrence

Base cases:

```text
D(0) = 1
D(1) = 0
```

Why `D(0)=1`?

There is exactly one way to arrange nothing:

```text
empty arrangement
```

This base case also makes the recurrence work cleanly.

---

## Calculate D(2)

```text
D(2)
=
(2-1)[D(1)+D(0)]

=
1 × (0+1)

=
1
```

Matches:

```text
AB → BA
```

---

## Calculate D(3)

```text
D(3)
=
(3-1)[D(2)+D(1)]

=
2 × (1+0)

=
2
```

Matches:

```text
BCA
CAB
```

---

## Calculate D(4)

```text
D(4)
=
(4-1)[D(3)+D(2)]

=
3 × (2+1)

=
9
```

---

## Calculate D(5)

```text
D(5)
=
(5-1)[D(4)+D(3)]

=
4 × (9+2)

=
44
```

So:

```text
n       0   1   2   3   4   5   6
        ↓   ↓   ↓   ↓   ↓   ↓   ↓
D(n)    1   0   1   2   9  44 265
```

---

# 11. C++ — Basic Version

The recurrence directly becomes DP.

```cpp
#include <bits/stdc++.h>
using namespace std;

long long derangement(int n) {
    vector<long long> dp(n + 1);

    dp[0] = 1;

    if (n >= 1)
        dp[1] = 0;

    for (int i = 2; i <= n; ++i) {
        dp[i] = (i - 1LL) * (dp[i - 1] + dp[i - 2]);
    }

    return dp[n];
}

int main() {
    cout << derangement(5) << '\n'; // 44
}
```

State meaning:

```text
dp[i]
=
number of derangements of i objects
```

Transition:

```text
dp[i]
=
(i-1) × (dp[i-1] + dp[i-2])
```

Complexity:

```text
Time   : O(n)
Memory : O(n)
```

---

# 12. C++ — O(1) Memory

Notice:

```text
D(n)
```

only needs:

```text
D(n-1)
D(n-2)
```

So we do not need the entire array.

```cpp
long long derangement(int n) {
    if (n == 0) return 1;
    if (n == 1) return 0;

    long long prev2 = 1; // D(0)
    long long prev1 = 0; // D(1)

    for (int i = 2; i <= n; ++i) {
        long long cur =
            (i - 1LL) * (prev1 + prev2);

        prev2 = prev1;
        prev1 = cur;
    }

    return prev1;
}
```

Visual:

```text
D(0) D(1)
  ↑    ↑
prev2 prev1

calculate D(2)
      ↓

D(1) D(2)
  ↑    ↑
prev2 prev1

calculate D(3)
      ↓

...
```

Complexity:

```text
Time   : O(n)
Memory : O(1)
```

---

# 13. Modulo Version

Derangements grow very quickly.

If the problem asks:

```text
answer modulo 1e9+7
```

apply modulo during every transition.

```cpp
const long long MOD = 1'000'000'007LL;

long long derangement(int n) {
    if (n == 0) return 1;
    if (n == 1) return 0;

    long long prev2 = 1;
    long long prev1 = 0;

    for (int i = 2; i <= n; ++i) {
        long long cur =
            (i - 1LL) *
            ((prev1 + prev2) % MOD)
            % MOD;

        prev2 = prev1;
        prev1 = cur;
    }

    return prev1;
}
```

No modular division or modular inverse is required.

---

# 14. Inclusion-Exclusion Formula

You should understand this **after** understanding the recurrence.

We want:

```text
all permutations
-
permutations where someone stays fixed
```

Let:

```text
Ai = object i stays at position i
```

Start with:

```text
n!
```

Subtract permutations where one chosen object is fixed:

```text
C(n,1)(n-1)!
```

But arrangements with two fixed objects were subtracted twice, so add them:

```text
C(n,2)(n-2)!
```

Continue alternating:

```text
D(n)

= n!
- C(n,1)(n-1)!
+ C(n,2)(n-2)!
- C(n,3)(n-3)!
+ ...
```

For `k` fixed objects:

```text
C(n,k)(n-k)!

= n!/[k!(n-k)!] × (n-k)!

= n!/k!
```

Therefore:

```text
D(n)
=
n! ×
(1 - 1/1! + 1/2! - 1/3! + ... + (-1)^n/n!)
```

Compact form:

```text
             n
D(n) = n! × Σ (-1)^k/k!
            k=0
```

Approximation:

```text
D(n) ≈ n!/e
```

For programming, prefer the recurrence unless the problem specifically needs the mathematical formula.

---

# 15. Example — SWORD

Problem:

```text
Rearrange SWORD so that
no letter remains in its original position.
```

### Step 1 — Remove story

```text
S,W,O,R,D → 5 distinct objects
```

### Step 2 — Extract condition

```text
no object remains in original position
```

### Step 3 — Mathematical model

```text
p[i] != i
```

### Step 4 — Recognize

```text
DERANGEMENT
```

### Step 5 — Solve

```text
D(5)
=
4[D(4)+D(3)]

=
4(9+2)

=
44
```

Answer:

```text
44
```

---

# 16. Example — Balls and Boxes

Problem:

```text
5 differently colored balls
5 boxes with matching colors

No ball can enter its same-colored box.
```

### Remove nouns

```text
ball        → object
box         → position
matching    → original position
```

Now the problem becomes:

```text
No object can go to its original position.
```

Therefore:

```text
D(5) = 44
```

The `SWORD` and balls/boxes problems look different, but mathematically they are identical.

```text
SWORD                    BALLS/BOXES
  │                           │
  └────────────┬──────────────┘
               ▼
       no original position
               │
               ▼
           p[i] != i
               │
               ▼
          DERANGEMENT
```

---

# 17. Example — Forced Placement

Now add an extra condition.

```text
6 cards
6 matching envelopes

card i cannot enter envelope i

AND

Card 1 must enter Envelope 2
```

Without the extra condition:

```text
D(6) = 265
```

Card `1` cannot enter Envelope `1`, so its possible destinations are:

```text
2 3 4 5 6
```

There are:

```text
5
```

symmetric choices.

We require exactly:

```text
1 → 2
```

Therefore:

```text
answer
=
D(6)/5

=
265/5

=
53
```

Answer:

```text
53
```

### Important lesson

Standard condition:

```text
p[i] != i
```

→ directly use `D(n)`.

Extra conditions:

```text
1 must go to 2
some positions additionally forbidden
some positions already fixed
```

→ do **not** blindly return `D(n)`.

Model the extra restriction.

---

# 18. How to Recognize Derangement

Look for phrases like:

```text
"no element stays in its original position"

"nobody receives their own gift"

"no ball goes into its matching box"

"no card goes into the same-numbered envelope"

"every person gets somebody else's item"

"p[i] != i for every i"
```

Mental translation:

```text
Story
  │
  ▼
objects + positions
  │
  ▼
original mapping i → i forbidden
  │
  ▼
p[i] != i
  │
  ▼
DERANGEMENT
```

---

# 19. Common Mistakes

## Mistake 1 — Confusing permutation and derangement

Permutation:

```text
any arrangement
```

Derangement:

```text
permutation
+
no fixed positions
```

---

## Mistake 2 — Thinking "at least one moves" is derangement

Wrong.

Derangement requires:

```text
EVERY object moves
```

Example:

```text
Original: ABC
New:      ACB
```

`B` and `C` moved, but `A` stayed.

So this is **not** a derangement.

---

## Mistake 3 — Using D(n) with extra restrictions

If the problem says:

```text
p[i] != i
```

for all `i`, standard `D(n)` works.

But:

```text
p[i] != i
AND
p[1] = 2
```

has an additional constraint.

Handle it separately.

---

## Mistake 4 — Forgetting D(0)

Use:

```text
D(0) = 1
D(1) = 0
```

This makes the recurrence work correctly.

---

# 20. Final Memory Card

```text
DERANGEMENT
===========

Start:
    permutation = arrangement

Fixed point:
    p[i] = i

Derangement:
    p[i] != i
    for EVERY i

Meaning:
    nobody/object stays
    in original position

Base:
    D(0) = 1
    D(1) = 0

Recurrence:
    D(n)
    =
    (n-1)[D(n-1)+D(n-2)]

Small values:
    D(1)=0
    D(2)=1
    D(3)=2
    D(4)=9
    D(5)=44
    D(6)=265

Formula:
             n
    D(n)=n! Σ (-1)^k/k!
            k=0

Approximation:
    D(n) ≈ n!/e

CP:
    recurrence
    O(n) time
    O(1) memory

Recognition:
    "no object gets its own position"
                 ↓
             p[i] != i
                 ↓
            DERANGEMENT
```

## Learning Order

```text
1. Understand permutation
        ↓
2. Understand fixed point
        ↓
3. Understand p[i] != i
        ↓
4. Manually solve n=2 and n=3
        ↓
5. Learn recurrence
        ↓
6. Dry-run D(4), D(5)
        ↓
7. Code recurrence
        ↓
8. Learn Inclusion-Exclusion formula
        ↓
9. Solve story variations
```

> **Do not start by memorizing the formula.**
>
> First make this observation automatic:
>
> ```text
> "Every object must avoid its original position"
>                     ↓
>                 DERANGEMENT
> ```
