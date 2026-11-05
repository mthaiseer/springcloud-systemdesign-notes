# 008 — DP Advanced Problem Solving 1
## Flowers → Removal Game → Knapsack 2

> **Self-study goal:** Learn how to identify the *right DP viewpoint* from the problem structure and constraints.
>
> This lecture contains three important DP patterns:
>
> 1. **Counting DP + prefix preprocessing** — Codeforces 474D, Flowers
> 2. **Two-player interval DP** — CSES Removal Game
> 3. **Constraint-driven state inversion** — AtCoder DP E, Knapsack 2
>
> Follow the same construction process for every problem:
>
> **Problem Asking → Story Removed → Observation → State → Choices → Transition → Base Case → Final Subproblem → Complexity → Dry Run → C++17 → Recognition**

---

# Problem Links

1. **Codeforces 474D — Flowers**  
   https://codeforces.com/problemset/problem/474/D

2. **CSES 1097 — Removal Game**  
   https://cses.fi/problemset/task/1097

3. **AtCoder DP Contest E — Knapsack 2**  
   https://atcoder.jp/contests/dp/tasks/dp_e

---

# Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [DP Problem-Solving Checklist](#2-dp-problem-solving-checklist)
3. [Problem 1 — Flowers](#3-problem-1--flowers)
4. [Flowers — State and Transition](#4-flowers--state-and-transition)
5. [Flowers — Why `dp[0] = 1`](#5-flowers--why-dp0--1)
6. [Flowers — Dry Run](#6-flowers--dry-run)
7. [Flowers — Range Query Preprocessing](#7-flowers--range-query-preprocessing)
8. [Flowers — C++17](#8-flowers--c17)
9. [Problem 2 — Removal Game](#9-problem-2--removal-game)
10. [Removal Game — Why Normal One-Player DP Is Not Enough](#10-removal-game--why-normal-one-player-dp-is-not-enough)
11. [Removal Game — Interval State](#11-removal-game--interval-state)
12. [Removal Game — Deriving the Transition](#12-removal-game--deriving-the-transition)
13. [Removal Game — Base Cases and Order](#13-removal-game--base-cases-and-order)
14. [Removal Game — Dry Run](#14-removal-game--dry-run)
15. [Removal Game — C++17](#15-removal-game--c17)
16. [Problem 3 — Knapsack 2](#16-problem-3--knapsack-2)
17. [Why Standard Knapsack State Is Wrong Here](#17-why-standard-knapsack-state-is-wrong-here)
18. [Knapsack 2 — Flip the DP State](#18-knapsack-2--flip-the-dp-state)
19. [Knapsack 2 — Transition Derivation](#19-knapsack-2--transition-derivation)
20. [Knapsack 2 — Dry Run](#20-knapsack-2--dry-run)
21. [Knapsack 2 — C++17](#21-knapsack-2--c17)
22. [Pattern Comparison](#22-pattern-comparison)
23. [Contest Recognition Checklist](#23-contest-recognition-checklist)
24. [Final Mental Model](#24-final-mental-model)

---

# 1. Prerequisites

Before solving these three problems, make sure the following ideas are comfortable.

---

## 1.1 DP state must have an English meaning

Do not begin with:

```text
dp[i] = ?
```

Begin with:

```text
What smaller problem does this state represent?
```

Examples from this lecture:

```text
Flowers:
dp[i]
=
number of valid ways to make exactly i flowers
```

```text
Removal Game:
dp[i][j]
=
maximum score the current first player can guarantee
from subarray [i ... j]
```

```text
Knapsack 2:
dp[i][value]
=
minimum weight required to obtain exactly `value`
using the first i items
```

If the meaning is clear, the recurrence becomes much easier to derive.

---

## 1.2 Counting DP

When the problem asks:

```text
How many valid ways?
```

transitions usually **add** possibilities.

Example:

```text
dp[i]
=
ways ending with choice A
+
ways ending with choice B
```

Flowers is a counting DP.

---

## 1.3 Optimization DP

When the problem asks:

```text
maximum
minimum
best
```

transitions commonly use:

```text
max(...)
```

or:

```text
min(...)
```

Removal Game uses both:

```text
max → our optimal move
min → opponent hurts our future result
```

Knapsack 2 uses:

```text
min weight
```

for each obtainable value.

---

## 1.4 Prefix sums

Suppose many queries ask:

```text
dp[a] + dp[a+1] + ... + dp[b]
```

Doing the sum every time is slow.

Build:

```text
prefix[i]
=
dp[1] + dp[2] + ... + dp[i]
```

Then:

```text
sum(a,b)
=
prefix[b] - prefix[a-1]
```

This is used after computing Flowers DP.

---

## 1.5 Interval DP

An interval state uses two boundaries:

```text
dp[left][right]
```

Meaning:

```text
answer for subarray
[left ... right]
```

Typical interval transitions shrink the interval:

```text
[left+1 ... right]

[left ... right-1]

[left+2 ... right]

[left+1 ... right-1]
```

Removal Game is an interval DP.

---

## 1.6 Two-player optimality

In a game:

```text
you want your score as large as possible
```

but the opponent also plays optimally.

Therefore:

```text
our choice
→ MAX
```

and if we are measuring **our future score**:

```text
opponent's choice
→ MIN
```

because the opponent chooses the branch worst for us.

This max/min interaction is the key to Removal Game.

---

## 1.7 0/1 Knapsack basics

Normal 0/1 Knapsack often uses:

```text
dp[i][weight]
=
maximum value obtainable
using first i items
with weight limit `weight`
```

Choices:

```text
skip item
take item
```

But this state is useful only when the weight dimension is manageable.

Knapsack 2 teaches:

```text
constraints decide the state
```

---

## 1.8 Infinity for impossible states

For a minimum DP, impossible states should not look attractive.

Use:

```text
INF
```

Example:

```text
dp[i][value] = INF
```

means:

```text
it is currently impossible
to obtain this value
```

Then `min(...)` works naturally.

---

## 1.9 Long long

Scores, weights and sums may exceed `int`.

Use:

```cpp
long long
```

for:

```text
array sums
DP scores
weights
INF
```

No `__int128` is needed for the patterns in this note.

---

# 2. DP Problem-Solving Checklist

For every problem:

```text
1. What is the problem asking?
        ↓
2. Remove the story
        ↓
3. What are my choices?
        ↓
4. What information determines the future?
        ↓
5. Define STATE in English
        ↓
6. Derive TRANSITION
        ↓
7. Find BASE CASE
        ↓
8. Identify FINAL SUBPROBLEM
        ↓
9. Calculate complexity
        ↓
10. Check constraints
        ↓
11. Optimize / redesign state if needed
```

A critical lesson from this lecture is:

```text
Do not force a familiar DP state
when the constraints make that state impossible.
```

---

# 3. Problem 1 — Flowers

## Codeforces 474D

https://codeforces.com/problemset/problem/474/D

The lecture starts with a counting DP.

There are two ways to extend a valid construction:

```text
1 red flower
```

or:

```text
a block of k white flowers
```

The important restriction is:

```text
white flowers come in groups of exactly k
```

---

## 3.1 Story removed

Forget colors temporarily.

We want to construct a total length:

```text
i
```

using pieces of size:

```text
1
```

and:

```text
k
```

So the problem becomes:

> How many ordered ways can we create total length `i` using a piece of size `1` and a piece of size `k`?

---

## 3.2 Why order matters

For:

```text
k = 2
i = 3
```

possible constructions are:

```text
R R R

R WW

WW R
```

The last two are different.

Therefore this is not a combination problem.

It is an ordered construction DP.

---

# 4. Flowers — State and Transition

## 4.1 STATE

The lecture defines:

```text
dp[i]
=
number of ways in which
Marmot can eat exactly i flowers
```

Equivalent construction meaning:

```text
dp[i]
=
number of valid sequences
having total length i
```

---

## 4.2 Look at the last piece

Every valid sequence of length `i` ends in one of two ways.

### Case 1 — Last piece is one red flower

Remove the last red flower.

Remaining length:

```text
i - 1
```

Number of possibilities:

```text
dp[i-1]
```

---

### Case 2 — Last piece is a white block

A white block contributes:

```text
k
```

flowers.

Remove that final white block.

Remaining length:

```text
i - k
```

Number of possibilities:

```text
dp[i-k]
```

This case is possible only when:

```text
i >= k
```

---

## 4.3 TRANSITION

Therefore:

```text
dp[i]
=
dp[i-1]
+
dp[i-k]
```

when:

```text
i >= k
```

If:

```text
i < k
```

a white block cannot fit.

So:

```text
dp[i]
=
dp[i-1]
```

which eventually means only the all-red construction exists.

---

## 4.4 Lecture visual model

The handwritten notes show the same state in forward form:

```text
from dp[i]

add 1 red
→ reach i+1

add k whites
→ reach i+k
```

Backward recurrence:

```text
dp[i]
← dp[i-1]
← dp[i-k]
```

Both views describe exactly the same DP.

---

# 5. Flowers — Why `dp[0] = 1`

The lecture sets:

```text
dp[0] = 1
```

This is very important.

Why is there one way to make zero flowers?

Because there is one:

```text
empty construction
```

This allows the transition to correctly create the first all-white block.

Suppose:

```text
i = k
```

Then:

```text
dp[k]
=
dp[k-1]
+
dp[0]
```

The `dp[0] = 1` term represents:

```text
one white block of size k
```

Without this base case, that construction would be lost.

---

## 5.1 For `i < k`

Only red flowers can be used.

Therefore:

```text
dp[1] = 1
dp[2] = 1
...
dp[k-1] = 1
```

These represent:

```text
R
RR
RRR
...
```

---

# 6. Flowers — Dry Run

## Example: `k = 2`

Start:

```text
dp[0] = 1
```

---

### `i = 1`

Since:

```text
1 < 2
```

white block is impossible.

```text
dp[1]
=
dp[0]
=
1
```

Construction:

```text
R
```

---

### `i = 2`

Now both choices are possible:

```text
dp[2]
=
dp[1] + dp[0]
=
1 + 1
=
2
```

Constructions:

```text
RR
WW
```

---

### `i = 3`

```text
dp[3]
=
dp[2] + dp[1]
=
2 + 1
=
3
```

Constructions:

```text
RRR
RWW
WWR
```

---

### `i = 4`

```text
dp[4]
=
dp[3] + dp[2]
=
3 + 2
=
5
```

Constructions:

```text
RRRR
RRWW
RWWR
WWRR
WWWW
```

So:

```text
dp:
1, 1, 2, 3, 5, ...
```

For `k = 2`, the recurrence looks Fibonacci-like.

---

## Example: `k = 3`

```text
dp[0] = 1

dp[1] = 1
dp[2] = 1
```

At:

```text
i = 3
```

we can have:

```text
RRR
WWW
```

Therefore:

```text
dp[3]
=
dp[2] + dp[0]
=
1 + 1
=
2
```

---

# 7. Flowers — Range Query Preprocessing

After `dp[i]` is known for every relevant length, queries ask for the total number of valid sequences over a range of lengths.

Conceptually:

```text
answer(a,b)
=
dp[a]
+
dp[a+1]
+
...
+
dp[b]
```

Doing this loop for every query would repeat work.

Build prefix sums.

---

## 7.1 Prefix state

```text
prefix[i]
=
dp[1]
+
dp[2]
+
...
+
dp[i]
```

Transition:

```text
prefix[i]
=
prefix[i-1]
+
dp[i]
```

---

## 7.2 Query formula

```text
answer(a,b)
=
prefix[b]
-
prefix[a-1]
```

Under modulo:

```text
answer
=
(
    prefix[b]
    -
    prefix[a-1]
    +
    MOD
)
% MOD
```

---

## 7.3 Complexity

If:

```text
MAX = maximum queried length
```

then:

```text
DP preprocessing      = O(MAX)

prefix preprocessing  = O(MAX)

each query            = O(1)
```

Total:

```text
O(MAX + numberOfQueries)
```

Space:

```text
O(MAX)
```

---

# 8. Flowers — C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const int MAXN = 100000;
const ll MOD = 1000000007LL;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t, k;
    cin >> t >> k;

    vector<ll> dp(MAXN + 1, 0);
    vector<ll> prefix(MAXN + 1, 0);

    dp[0] = 1;

    for (int len = 1; len <= MAXN; len++) {

        // Last piece is one red flower.
        dp[len] = dp[len - 1];

        // Last piece is one block of k white flowers.
        if (len >= k) {
            dp[len] += dp[len - k];
        }

        dp[len] %= MOD;
    }

    for (int len = 1; len <= MAXN; len++) {
        prefix[len]
            =
            (prefix[len - 1] + dp[len])
            % MOD;
    }

    while (t--) {
        int a, b;
        cin >> a >> b;

        ll answer
            =
            (
                prefix[b]
                -
                prefix[a - 1]
                +
                MOD
            )
            % MOD;

        cout << answer << '\n';
    }

    return 0;
}
```

---

## 8.1 Recognition model

When you see:

```text
construct total size i

and the last piece can have
one of a few fixed sizes
```

think:

```text
dp[i]
=
sum of dp[i - pieceSize]
```

For Flowers:

```text
piece sizes = 1 and k
```

Therefore:

```text
dp[i]
=
dp[i-1]
+
dp[i-k]
```

---

# 9. Problem 2 — Removal Game

## CSES 1097

https://cses.fi/problemset/task/1097

We have an array:

```text
a[0], a[1], ..., a[n-1]
```

Two players play alternately.

On every turn, a player can remove:

```text
leftmost element
```

or:

```text
rightmost element
```

The removed value is added to that player's score.

Both players play optimally.

Goal:

```text
maximum score the first player can guarantee
```

---

## 9.1 Why this is an interval problem

After removing from one end, the remaining elements are always a continuous interval.

If current interval is:

```text
[i ... j]
```

then after one move it becomes:

```text
[i+1 ... j]
```

or:

```text
[i ... j-1]
```

Therefore:

```text
left and right boundaries
```

fully describe the remaining game.

This strongly suggests:

```text
dp[i][j]
```

---

# 10. Removal Game — Why Normal One-Player DP Is Not Enough

The lecture highlights an important difficulty:

```text
the DP function calculates
the optimal score of one player
```

but after our move:

```text
the opponent gets a turn
```

The opponent also plays optimally.

So we cannot pretend the second player makes a random or convenient move.

We must simulate:

```text
their best response
```

from our point of view.

---

## 10.1 Max for us, min for opponent

We want:

```text
our final score as large as possible
```

Therefore our move uses:

```text
MAX
```

But after our move, the opponent chooses an end.

The opponent wants to make our future obtainable score as small as possible.

Therefore the opponent's response becomes:

```text
MIN
```

This produces the pattern:

```text
MAX(
    our left choice + MIN(opponent replies),
    our right choice + MIN(opponent replies)
)
```

---

# 11. Removal Game — Interval State

## STATE

Define:

```text
dp[i][j]
=
maximum score the player
whose turn it is now
can guarantee for themselves
from subarray a[i ... j]
```

The lecture derives the recurrence by moving **two turns** at once:

```text
our move
+
opponent move
→ DP is called for our turn again
```

That is why the recurrence jumps two removals forward.

---

# 12. Removal Game — Deriving the Transition

Current interval:

```text
[i ... j]
```

We have two choices.

---

## 12.1 Choice 1 — We take `a[i]`

Immediately gain:

```text
a[i]
```

Remaining interval:

```text
[i+1 ... j]
```

Now it is the opponent's turn.

The opponent has two choices.

### Opponent takes left

Opponent removes:

```text
a[i+1]
```

Remaining interval for our next turn:

```text
[i+2 ... j]
```

Our future score:

```text
dp[i+2][j]
```

---

### Opponent takes right

Opponent removes:

```text
a[j]
```

Remaining interval for our next turn:

```text
[i+1 ... j-1]
```

Our future score:

```text
dp[i+1][j-1]
```

---

### Opponent is optimal

The opponent chooses the branch that is worse for us.

Therefore after we choose left:

```text
future
=
min(
    dp[i+2][j],
    dp[i+1][j-1]
)
```

So:

```text
takeLeft
=
a[i]
+
min(
    dp[i+2][j],
    dp[i+1][j-1]
)
```

---

## 12.2 Choice 2 — We take `a[j]`

Immediately gain:

```text
a[j]
```

Remaining:

```text
[i ... j-1]
```

The opponent can now take:

```text
a[i]
```

or:

```text
a[j-1]
```

---

### Opponent takes left

Remaining for us:

```text
[i+1 ... j-1]
```

Future:

```text
dp[i+1][j-1]
```

---

### Opponent takes right

Remaining for us:

```text
[i ... j-2]
```

Future:

```text
dp[i][j-2]
```

---

### Opponent chooses worst for us

```text
future
=
min(
    dp[i+1][j-1],
    dp[i][j-2]
)
```

Therefore:

```text
takeRight
=
a[j]
+
min(
    dp[i+1][j-1],
    dp[i][j-2]
)
```

---

## 12.3 Final recurrence

We choose the better of our two moves:

```text
dp[i][j]
=
max(
    a[i]
    +
    min(
        dp[i+2][j],
        dp[i+1][j-1]
    ),

    a[j]
    +
    min(
        dp[i+1][j-1],
        dp[i][j-2]
    )
)
```

This is the central recurrence from the handwritten lecture derivation.

---

# 13. Removal Game — Base Cases and Order

## 13.1 Empty interval

For recurrence convenience:

```text
dp[i][j] = 0
```

when:

```text
i > j
```

because there are no numbers left to collect.

---

## 13.2 One element

If:

```text
i == j
```

only one choice exists.

Therefore:

```text
dp[i][i]
=
a[i]
```

---

## 13.3 Two elements

For:

```text
[i, i+1]
```

the current player simply chooses the larger endpoint:

```text
dp[i][i+1]
=
max(
    a[i],
    a[i+1]
)
```

The general recurrence also agrees with this if invalid/empty ranges contribute zero.

---

## 13.4 Bottom-up order

Large intervals depend on smaller intervals.

Specifically:

```text
length L
```

depends on:

```text
length L-2
```

states.

Therefore fill by increasing interval length:

```text
length = 1
length = 2
length = 3
...
length = n
```

---

## 13.5 Final subproblem

Initially the whole array remains:

```text
[0 ... n-1]
```

Therefore:

```text
answer
=
dp[0][n-1]
```

---

## 13.6 Constant-sum observation from lecture

The handwritten notes also point out:

```text
player1Score + player2Score
=
sum of the whole array
```

The total is fixed.

So maximizing one player's score automatically minimizes what remains for the other.

This is another way to understand why the game has a strong optimal-substructure relationship.

---

# 14. Removal Game — Dry Run

Take:

```text
a = [4, 5, 1]
```

---

## Length 1

```text
dp[0][0] = 4
dp[1][1] = 5
dp[2][2] = 1
```

---

## Length 2

### Interval `[0,1]`

```text
[4,5]
```

Current player chooses:

```text
max(4,5)
=
5
```

So:

```text
dp[0][1] = 5
```

### Interval `[1,2]`

```text
[5,1]
```

So:

```text
dp[1][2] = 5
```

---

## Length 3 — `[0,2]`

Array:

```text
[4,5,1]
```

### Take left `4`

Opponent can leave us either:

```text
[1]
```

or:

```text
[5]
```

Our future score is:

```text
min(1,5)
=
1
```

Therefore:

```text
takeLeft
=
4 + 1
=
5
```

---

### Take right `1`

Opponent can leave us either:

```text
[5]
```

or:

```text
[4]
```

Our future score:

```text
min(5,4)
=
4
```

Therefore:

```text
takeRight
=
1 + 4
=
5
```

---

### Our optimal choice

```text
dp[0][2]
=
max(5,5)
=
5
```

So the first player can guarantee:

```text
5
```

---

# 15. Removal Game — C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<ll> a(n);

    for (ll& value : a) {
        cin >> value;
    }

    vector<vector<ll>> dp(
        n,
        vector<ll>(n, 0)
    );

    // Length 1.
    for (int i = 0; i < n; i++) {
        dp[i][i] = a[i];
    }

    auto get = [&](int left, int right) -> ll {
        if (left > right) {
            return 0;
        }

        return dp[left][right];
    };

    for (int len = 2; len <= n; len++) {

        for (int i = 0; i + len - 1 < n; i++) {

            int j = i + len - 1;

            ll takeLeft
                =
                a[i]
                +
                min(
                    get(i + 2, j),
                    get(i + 1, j - 1)
                );

            ll takeRight
                =
                a[j]
                +
                min(
                    get(i + 1, j - 1),
                    get(i, j - 2)
                );

            dp[i][j]
                =
                max(
                    takeLeft,
                    takeRight
                );
        }
    }

    cout << dp[0][n - 1] << '\n';

    return 0;
}
```

---

## 15.1 Complexity

Possible intervals:

```text
O(n²)
```

Work per interval:

```text
O(1)
```

Therefore:

```text
Time  = O(n²)
Space = O(n²)
```

---

## 15.2 Recognition model

When you see:

```text
two players
+
both play optimally
+
take from array ends
```

think:

```text
interval DP
```

Then ask:

```text
Can I write the state only for
the player whose turn it is?
```

If yes, simulate:

```text
our move      → max

opponent move → min
```

and jump back to our turn.

---

# 16. Problem 3 — Knapsack 2

## AtCoder DP Contest E

https://atcoder.jp/contests/dp/tasks/dp_e

We have `n` items.

Each item has:

```text
weight[i]
value[i]
```

Maximum allowed total weight:

```text
W
```

Each item can be selected at most once.

Goal:

```text
maximize total value
while total weight <= W
```

This sounds like ordinary 0/1 Knapsack.

But the lecture's main lesson is:

```text
the usual state is not always the correct state
```

---

# 17. Why Standard Knapsack State Is Wrong Here

The normal state is:

```text
dp[i][weight]
=
maximum value obtainable
using first i items
with weight limit `weight`
```

This requires a DP dimension up to:

```text
W
```

If `W` is very large, this state space is too large.

So instead of asking:

```text
For every weight,
what maximum value can I get?
```

the lecture flips the question.

Ask:

```text
For every value,
what minimum weight is required?
```

This is **state inversion**.

---

# 18. Knapsack 2 — Flip the DP State

## Old state

```text
dp[i][weight]
=
maximum value
```

---

## New state

Define:

```text
dp[i][value]
=
minimum total weight required
to obtain exactly `value`
using only the first i items
```

This is the main idea shown in the lecture.

---

## 18.1 Why this helps

The state dimension should be built around the quantity whose total range is manageable.

The lecture's lesson:

```text
If weight space is too large
but total value space is manageable,

DP on value instead of weight.
```

This is a general competitive-programming technique.

---

# 19. Knapsack 2 — Transition Derivation

Consider item:

```text
i-1
```

with:

```text
weight = weight[i-1]

value  = value[i-1]
```

For target value:

```text
v
```

there are two choices.

---

## 19.1 Choice 1 — Skip the item

If we do not take item `i-1`:

```text
dp[i][v]
=
dp[i-1][v]
```

Candidate:

```text
skip
=
dp[i-1][v]
```

---

## 19.2 Choice 2 — Take the item

If we take this item, it contributes:

```text
value[i-1]
```

So before taking it, we must already have created:

```text
v - value[i-1]
```

using the first:

```text
i-1
```

items.

Required previous weight:

```text
dp[i-1][v - value[i-1]]
```

After adding the new item:

```text
take
=
dp[i-1][v - value[i-1]]
+
weight[i-1]
```

This is valid only when:

```text
v >= value[i-1]
```

and the previous state is reachable.

---

## 19.3 TRANSITION

```text
dp[i][v]
=
min(
    dp[i-1][v],

    dp[i-1][v - value[i-1]]
    +
    weight[i-1]
)
```

when the take transition is valid.

Why `min`?

Because for the same total value:

```text
smaller required weight
```

is always better.

---

# 19.4 BASE CASE

Using zero items:

```text
value 0
```

requires:

```text
weight 0
```

Therefore:

```text
dp[0][0] = 0
```

Every positive value is impossible:

```text
dp[0][v] = INF
```

for:

```text
v > 0
```

---

# 19.5 FINAL SUBPROBLEM

After processing all items, inspect every total value:

```text
v
```

If:

```text
dp[n][v] <= W
```

then that value can be obtained within the weight limit.

Therefore:

```text
answer
=
maximum v
such that
dp[n][v] <= W
```

---

# 20. Knapsack 2 — Dry Run

Suppose:

```text
capacity W = 8
```

Items:

```text
item 1:
weight = 3
value  = 4

item 2:
weight = 4
value  = 5

item 3:
weight = 5
value  = 7
```

Total possible value:

```text
4 + 5 + 7
=
16
```

We ask:

```text
minimum weight required
for every value 0...16
```

---

## Start

```text
dp[0][0] = 0
```

Everything else:

```text
INF
```

---

## Process item 1 `(weight=3, value=4)`

Skip:

```text
value 0 → weight 0
```

Take:

```text
value 4 → weight 3
```

So reachable values:

```text
0 → 0
4 → 3
```

---

## Process item 2 `(weight=4, value=5)`

Existing possibilities remain.

New take transitions:

From:

```text
value 0
```

take item 2:

```text
value 5
weight 4
```

From:

```text
value 4
```

take item 2:

```text
value 9
weight 3 + 4
       = 7
```

So:

```text
value 9
requires weight 7
```

This is within:

```text
W = 8
```

---

## Process item 3 `(weight=5, value=7)`

Some possibilities:

```text
value 7
weight 5
```

```text
value 11
weight 8
```

from:

```text
item 1 + item 3
```

because:

```text
3 + 5 = 8
```

and:

```text
4 + 7 = 11
```

But:

```text
value 12
```

from items 2 and 3 requires:

```text
4 + 5 = 9
```

which exceeds capacity.

Final answer is the largest value whose minimum weight is at most `8`.

---

# 21. Knapsack 2 — C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    ll W;

    cin >> n >> W;

    vector<ll> weight(n);
    vector<int> value(n);

    int totalValue = 0;

    for (int i = 0; i < n; i++) {
        cin >> weight[i] >> value[i];

        totalValue += value[i];
    }

    const ll INF = numeric_limits<ll>::max() / 4;

    vector<vector<ll>> dp(
        n + 1,
        vector<ll>(totalValue + 1, INF)
    );

    dp[0][0] = 0;

    for (int i = 1; i <= n; i++) {

        for (int v = 0; v <= totalValue; v++) {

            // Choice 1: skip item i-1.
            dp[i][v] = dp[i - 1][v];

            // Choice 2: take item i-1.
            if (v >= value[i - 1]) {

                int previousValue
                    =
                    v - value[i - 1];

                if (dp[i - 1][previousValue] != INF) {

                    ll takeWeight
                        =
                        dp[i - 1][previousValue]
                        +
                        weight[i - 1];

                    dp[i][v]
                        =
                        min(
                            dp[i][v],
                            takeWeight
                        );
                }
            }
        }
    }

    int answer = 0;

    for (int v = 0; v <= totalValue; v++) {

        if (dp[n][v] <= W) {
            answer = v;
        }
    }

    cout << answer << '\n';

    return 0;
}
```

---

## 21.1 Complexity

Let:

```text
SUM_VALUE
=
sum of all item values
```

States:

```text
n × SUM_VALUE
```

Each transition:

```text
O(1)
```

Therefore:

```text
Time
=
O(n × SUM_VALUE)
```

Space:

```text
O(n × SUM_VALUE)
```

The important lesson is not the exact formula.

It is:

```text
Choose the DP dimension
that fits the constraints.
```

---

## 21.2 Recognition model

When standard Knapsack gives:

```text
O(n × W)
```

and `W` is too large, do not immediately give up.

Ask:

```text
Is the total value range much smaller?
```

If yes:

```text
invert the DP
```

from:

```text
weight → maximum value
```

to:

```text
value → minimum weight
```

---

# 22. Pattern Comparison

| Problem | Core state | Transition idea | Main lesson |
|---|---|---|---|
| Flowers | `dp[i] = ways for length i` | `dp[i-1] + dp[i-k]` | Build by last piece |
| Removal Game | `dp[i][j] = best score on interval` | `max(our move + min(opponent replies))` | Simulate optimal opponent |
| Knapsack 2 | `dp[i][v] = min weight for value v` | take / skip with `min` weight | Flip state using constraints |

---

# 23. Contest Recognition Checklist

## 23.1 Flowers pattern

When you see:

```text
count ordered constructions
of total size N
```

and each move contributes fixed sizes:

```text
s1, s2, ...
```

think:

```text
dp[i]
=
sum(dp[i - sj])
```

---

## 23.2 Many queries over DP values

If queries repeatedly ask:

```text
sum over [a,b]
```

think:

```text
precompute DP once
+
prefix sums
```

---

## 23.3 Removal Game pattern

When you see:

```text
take left or right
+
two optimal players
```

think:

```text
interval DP
```

Then decide what the state measures.

If it measures only our guaranteed score:

```text
our choice      → max

opponent choice → min
```

---

## 23.4 Why DP jumps two moves in Removal Game

Lecture mental model:

```text
our turn
    ↓
opponent turn
    ↓
our turn again
```

So the recursive DP can jump directly:

```text
2 removals forward
```

and remain a state for the same player perspective.

---

## 23.5 Knapsack 2 pattern

Whenever a common DP would be:

```text
O(n × X)
```

but `X` is too large, ask:

```text
Can I exchange what is
STATE
and what is
ANSWER?
```

For Knapsack 2:

```text
standard:
weight → max value

flipped:
value → min weight
```

---

## 23.6 Constraint-first questions

Before coding:

```text
1. What is the range of every possible DP dimension?

2. Which dimension is too large?

3. Is there another quantity with a smaller total range?

4. Can I store minimum cost for reward
   instead of maximum reward for cost?

5. Can I precompute once for many queries?
```

---

# 24. Final Mental Model

## Flowers

```text
TOTAL LENGTH i
│
├── ends with RED
│      ↓
│   dp[i-1]
│
└── ends with k WHITES
       ↓
    dp[i-k]

dp[i]
=
dp[i-1] + dp[i-k]
```

---

## Removal Game

```text
CURRENT INTERVAL [i ... j]
          |
     our choice
      /       \
    LEFT      RIGHT
     |          |
 opponent     opponent
 chooses      chooses
 worst        worst
 for us       for us
     \          /
       MAX for us
```

Formula:

```text
dp[i][j]
=
max(
    a[i] + min(...),
    a[j] + min(...)
)
```

---

## Knapsack 2

```text
STANDARD STATE TOO LARGE

dp[item][weight]
=
max value
        |
        v
weight dimension too large

FLIP THE QUESTION

dp[item][value]
=
minimum weight
        |
        v
manageable value dimension
```

---

# Final Revision Box

```text
FLOWERS
────────────────────────────────

State:
dp[i] = number of ways for length i

Transition:
dp[i] = dp[i-1] + dp[i-k]

Base:
dp[0] = 1

Many range queries:
use prefix sums


REMOVAL GAME
────────────────────────────────

State:
dp[i][j] = maximum score current player
           can guarantee from [i..j]

Our move:
MAX

Opponent response:
MIN

Transition:

max(
 a[i] + min(dp[i+2][j], dp[i+1][j-1]),
 a[j] + min(dp[i+1][j-1], dp[i][j-2])
)


KNAPSACK 2
────────────────────────────────

Standard:
dp[item][weight] = max value

Problem:
weight dimension too large

Flip:

dp[item][value]
=
minimum weight needed for that value

Transition:

min(
 skip,
 previousWeight + currentWeight
)

Answer:
largest value whose minimum weight <= W


CORE ADVANCED-DP QUESTION
────────────────────────────────

Do not ask only:

"What recurrence do I know?"

Ask:

"What state fits THESE constraints?"
```
