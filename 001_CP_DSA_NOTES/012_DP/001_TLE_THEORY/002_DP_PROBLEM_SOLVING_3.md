# 004 — Dynamic Programming Problem Solving
## Block Sequence → Color with Occurrences → Flowers

> **Self-study goal:** For every problem, follow the same DP construction process:
>
> **Observation → Story Removed → State → Choice → Transition → Base Case → Final Subproblem → Complexity → Dry Run → C++17**
>
> This note is designed so that you can revise the lecture without replaying the full video.

---

# Problem Links

1. **Codeforces 1881E — Block Sequence**  
   https://codeforces.com/problemset/problem/1881/E

2. **Codeforces 1714D — Color with Occurrences**  
   https://codeforces.com/problemset/problem/1714/D

3. **Codeforces 474D — Flowers**  
   https://codeforces.com/problemset/problem/474/D

---

# Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [The DP Problem-Solving Template](#2-the-dp-problem-solving-template)
3. [Problem 1 — Block Sequence](#3-problem-1--block-sequence)
4. [Problem 2 — Color with Occurrences](#4-problem-2--color-with-occurrences)
5. [Problem 3 — Flowers](#5-problem-3--flowers)
6. [Pattern Comparison](#6-pattern-comparison)
7. [How to Recognize These Patterns in Contests](#7-how-to-recognize-these-patterns-in-contests)
8. [Final Mental Model](#8-final-mental-model)

---

# 1. Prerequisites

Before solving the three problems, make sure these ideas are comfortable.

---

## 1.1 A DP state must have an English meaning

Never start with:

```text
dp[i] = ?
```

Start with:

```text
What exact smaller problem does index i represent?
```

For example:

```text
dp[i]
= best answer using the suffix starting at index i
```

or:

```text
dp[i]
= number of valid constructions of total length i
```

or:

```text
dp[i]
= minimum operations needed after covering the first i characters
```

If the English meaning is clear, the recurrence becomes much easier.

---

## 1.2 Prefix vs suffix state

Two common styles:

### Prefix DP

```text
dp[i]
= answer after processing / covering
  the first i elements
```

Picture:

```text
[ processed / solved ][ not processed ]
0                  i                 n
```

---

### Suffix DP

```text
dp[i]
= answer for the subarray / substring
  starting from i
```

Picture:

```text
[ already ignored ][ solve this suffix ]
0                 i                 n
```

Both are valid.

Choose the one that makes the transition easiest.

---

## 1.3 Optimization DP

Some DP problems ask:

```text
minimum
maximum
```

Typical recurrence:

```text
dp[state]
=
min(choice1, choice2, ...)
```

or:

```text
dp[state]
=
max(choice1, choice2, ...)
```

---

## 1.4 Counting DP

Some problems ask:

```text
How many valid ways?
```

Then transitions usually add:

```text
dp[state]
=
ways from choice 1
+
ways from choice 2
+
...
```

---

## 1.5 Take / Skip pattern

Very common:

```text
At item i:

Choice 1 → skip it
Choice 2 → take it
```

So the recurrence often looks like:

```text
dp[i]
=
max(
    skip,
    take
)
```

or:

```text
dp[i]
=
min(
    skip,
    take
)
```

Problem 1 uses exactly this idea.

---

## 1.6 Interval coverage

Suppose some operation covers:

```text
[start ... end]
```

If we already covered:

```text
[0 ... covered-1]
```

then an interval can extend our coverage only if it begins no later than the first uncovered point.

Visual:

```text
already covered
vvvvvvvvvvvv
[-------------)
0            covered

          [----------------)
          start            end
```

Valid extension condition:

```text
start <= covered
and
end > covered
```

Then the new covered prefix becomes:

```text
[0 ... end-1]
```

Problem 2 uses this model.

---

## 1.7 Prefix sums over DP answers

Sometimes DP gives:

```text
ways[1]
ways[2]
ways[3]
...
```

but each query asks:

```text
ways[a] + ways[a+1] + ... + ways[b]
```

Doing this separately for every query is too slow.

Build:

```text
prefix[i]
=
ways[1] + ways[2] + ... + ways[i]
```

Then:

```text
answer(a,b)
=
prefix[b] - prefix[a-1]
```

Problem 3 uses this second layer.

---

## 1.8 Modulo subtraction

If all values are modulo:

```text
MOD = 1e9 + 7
```

then:

```text
answer
=
(prefix[b] - prefix[a-1] + MOD) % MOD
```

Why add `MOD`?

Because:

```text
prefix[b] - prefix[a-1]
```

may become negative after modular reduction.

---

# 2. The DP Problem-Solving Template

For every problem in this note, use the same sequence.

```text
1. What is the problem asking?
        ↓
2. Remove the story
        ↓
3. Observation
        ↓
4. STATE
        ↓
5. CHOICES
        ↓
6. TRANSITION
        ↓
7. BASE CASE
        ↓
8. FINAL SUBPROBLEM
        ↓
9. COMPLEXITY
        ↓
10. DRY RUN
        ↓
11. CODE
```

The most important four parts remain:

```text
STATE
TRANSITION
BASE CASE
FINAL SUBPROBLEM
```

---

# 3. Problem 1 — Block Sequence

## Codeforces Link

https://codeforces.com/problemset/problem/1881/E

---

## 3.1 What is the problem asking?

You are given:

```text
a[0], a[1], ..., a[n-1]
```

A beautiful sequence consists of blocks.

For every block:

```text
[first value = number of following elements]
```

Example:

```text
[3, 8, 5, 2] [2, 7, 4]
 ^              ^
 |              |
3 values        2 values
after it        after it
```

So:

```text
3 8 5 2
```

is one valid block because `3` says:

```text
take the next 3 values
```

The problem allows deleting arbitrary elements.

Goal:

```text
minimum deletions
```

to make the remaining sequence beautiful.

---

## 3.2 First observation from the lecture

The lecture starts from:

```text
minimum deletions
```

and changes the viewpoint.

Instead of directly minimizing deleted elements:

```text
min deletions
```

maximize how many elements we keep.

Because:

```text
deleted + retained = n
```

Therefore:

```text
minimumDeleted
=
n - maximumRetained
```

This is the first important modelling trick.

---

## 3.3 Why keeping elements is easier

If we decide that:

```text
a[i] = x
```

will be the first element of a block, then the structure of that block becomes fixed.

We must retain:

```text
a[i]
a[i+1]
a[i+2]
...
a[i+x]
```

That is:

```text
1 header + x data elements
```

Total retained:

```text
x + 1
```

And the next block may begin at:

```text
i + x + 1
```

---

## 3.4 Visual example

Suppose:

```text
index:  0  1  2  3  4  5  6
array: [3, 8, 5, 2, 1, 7, 9]
```

If we choose index `0` as a block header:

```text
a[0] = 3
```

then:

```text
header
  |
  v
[ 3 | 8  5  2 | 1  7  9 ]
      <------>

need exactly 3 following elements
```

So this chosen block consumes:

```text
indices 0..3
```

Next state:

```text
index 4
```

---

# 3.5 Story removed

At every index `i`, we have two choices.

### Choice 1 — Delete / skip `a[i]`

Then:

```text
move to i+1
```

and retain nothing from `a[i]`.

---

### Choice 2 — Keep `a[i]` as the beginning of a block

Let:

```text
x = a[i]
```

Then we need:

```text
x more elements
```

after `i`.

So the last required index is:

```text
i + x
```

This is valid only if:

```text
i + x < n
```

If valid, retain:

```text
x + 1
```

elements and continue from:

```text
i + x + 1
```

---

# 3.6 STATE

Following the lecture's formulation:

```text
dp[i]
=
maximum number of elements we can retain
from suffix [i ... n-1]
such that the retained elements form
complete valid blocks
```

This is a suffix DP.

Visual:

```text
0                 i                      n
+-----------------+----------------------+
| already ignored | solve this suffix    |
+-----------------+----------------------+
```

---

# 3.7 CHOICES

At `i`:

```text
          dp[i]
         /     \
        /       \
     SKIP       TAKE
      |           |
    i+1       i+a[i]+1
```

---

# 3.8 TRANSITION

## Choice 1 — Skip

If we delete `a[i]`:

```text
skip = dp[i+1]
```

---

## Choice 2 — Take

Possible only when:

```text
i + a[i] < n
```

Then current block retains:

```text
1 + a[i]
```

elements.

Remaining suffix begins at:

```text
i + a[i] + 1
```

Therefore:

```text
take
=
1 + a[i] + dp[i + a[i] + 1]
```

---

## Final recurrence

```text
dp[i] = dp[i+1]
```

and if:

```text
i + a[i] < n
```

then:

```text
dp[i]
=
max(
    dp[i],
    1 + a[i] + dp[i + a[i] + 1]
)
```

---

# 3.9 Why `i + a[i] < n`?

Suppose:

```text
a[i] = x
```

We need positions:

```text
i+1
i+2
...
i+x
```

The last required position:

```text
i+x
```

must be a valid array index.

Largest valid index:

```text
n-1
```

Therefore:

```text
i+x <= n-1
```

which is equivalent to:

```text
i+x < n
```

---

# 3.10 BASE CASE

If we reach beyond the array:

```text
dp[n] = 0
```

There are no elements left to retain.

---

# 3.11 FINAL SUBPROBLEM

Lecture state gives:

```text
maximumRetained = dp[0]
```

But the original problem wants:

```text
minimum deletions
```

Therefore:

```text
answer = n - dp[0]
```

---

# 3.12 Compact dry run

Take:

```text
a = [2, 7, 1, 8]
n = 4
```

We compute right to left.

### `i = 3`

```text
a[3] = 8
3 + 8 >= 4
```

Cannot start a valid block.

```text
dp[3] = dp[4] = 0
```

---

### `i = 2`

```text
a[2] = 1
2 + 1 < 4
```

Take:

```text
1 + 1 + dp[4]
= 2
```

Skip:

```text
dp[3] = 0
```

So:

```text
dp[2] = 2
```

---

### `i = 1`

```text
a[1] = 7
1 + 7 >= 4
```

Cannot take.

```text
dp[1] = dp[2] = 2
```

---

### `i = 0`

```text
a[0] = 2
0 + 2 < 4
```

Take:

```text
1 + 2 + dp[3]
= 3
```

Skip:

```text
dp[1] = 2
```

So:

```text
dp[0] = 3
```

Final:

```text
minimum deletions
=
4 - 3
=
1
```

A valid retained block is:

```text
[2, 7, 1]
```

---

# 3.13 Bottom-up order

Transition uses:

```text
dp[i+1]
```

and possibly:

```text
dp[i+a[i]+1]
```

Both indices are greater than `i`.

Therefore compute:

```text
right → left
```

Visual:

```text
dp[n] → dp[n-1] → dp[n-2] → ... → dp[0]
```

---

# 3.14 Complexity

States:

```text
n + 1
```

Work per state:

```text
O(1)
```

Therefore:

```text
Time  = O(n)
Space = O(n)
```

Across test cases the total `n` fits the problem constraint, so this is efficient.

---

# 3.15 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

void solve() {
    int n;
    cin >> n;

    vector<int> a(n);

    for (int i = 0; i < n; i++) {
        cin >> a[i];
    }

    vector<int> dp(n + 1, 0);

    for (int i = n - 1; i >= 0; i--) {
        dp[i] = dp[i + 1];

        if (i + a[i] < n) {
            int nextIndex = i + a[i] + 1;
            int take = a[i] + 1 + dp[nextIndex];

            dp[i] = max(dp[i], take);
        }
    }

    cout << n - dp[0] << '\n';
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;

    while (t--) {
        solve();
    }

    return 0;
}
```

---

# 3.16 Recognition model

Think **Block Sequence DP** when:

```text
At position i:

either remove/ignore current item
OR
current value determines exactly how far the chosen block jumps
```

Mental form:

```text
skip → i+1
take → i + jump
```

This is classic:

```text
suffix DP + take/skip
```

---

# 4. Problem 2 — Color with Occurrences

## Codeforces Link

https://codeforces.com/problemset/problem/1714/D

---

## 4.1 What is the problem asking?

You have:

```text
target string t
```

and several strings:

```text
s1, s2, ..., sn
```

Whenever one of the strings occurs inside `t`, you may select that occurrence.

Selecting it colors that interval.

Goal:

```text
color every target character
using the minimum number of selected occurrences
```

Occurrences are allowed to overlap.

We must also output which strings and starting positions were used.

---

# 4.2 First convert strings into intervals

This is the most important modelling step.

Suppose:

```text
t = "bababa"
s = "aba"
```

Occurrences:

```text
position 1..3?   No
position 2..4    Yes
position 4..6    Yes
```

Using 0-based indexing:

```text
"aba" at start 1 covers [1,4)
"aba" at start 3 covers [3,6)
```

So after finding matches, we can forget most of the string story.

Each occurrence becomes:

```text
[start, end)
```

with extra information:

```text
which pattern produced this interval
```

---

# 4.3 Interval picture

Example:

```text
target indices

0   1   2   3   4   5
b   a   b   a   b   a
|---|---|---|---|---|

"ba"  at 0 : [0,2)
"aba" at 1 :   [1,4)
"aba" at 3 :       [3,6)
```

Goal:

```text
cover [0,6)
```

with minimum intervals.

---

# 4.4 What does "covered prefix" mean?

If the first `x` characters are fully red:

```text
[0, x)
```

is covered.

Example:

```text
x = 4

index: 0 1 2 3 4 5
       R R R R _ _
       <----->
       covered
```

The first uncovered character is:

```text
x
```

---

# 4.5 STATE

A clean self-study formulation is:

```text
dp[x]
=
minimum number of chosen occurrences
needed to fully color prefix [0, x)
```

So:

```text
dp[0] = 0
```

means:

```text
coloring zero characters costs zero operations
```

Final goal:

```text
dp[m]
```

where:

```text
m = t.length()
```

---

# 4.6 Which occurrence can extend state `x`?

Suppose current covered prefix is:

```text
[0, x)
```

Take an occurrence:

```text
[start, end)
```

It must overlap or touch the current covered prefix:

```text
start <= x
```

and it must actually extend coverage:

```text
end > x
```

Therefore valid extension condition:

```text
start <= x < end
```

Then new coverage becomes:

```text
[0, end)
```

---

## Why no gap appears

We already covered:

```text
[0, x)
```

and the new interval starts at:

```text
start <= x
```

so the two intervals overlap or touch.

Example:

```text
old: [----------------)
     0                x

new:              [----------------)
                  start            end

start <= x
```

Together:

```text
[-----------------------------------)
0                                  end
```

No uncovered hole exists.

---

# 4.7 TRANSITION

For every state `x` and every occurrence `[start,end)`:

If:

```text
start <= x
and
x < end
```

then:

```text
dp[end]
=
min(
    dp[end],
    dp[x] + 1
)
```

This is a shortest-path style DP over covered-prefix length.

---

# 4.8 BASE CASE

```text
dp[0] = 0
```

All other states initially:

```text
INF
```

meaning:

```text
not reachable yet
```

---

# 4.9 FINAL SUBPROBLEM

Let:

```text
m = t.length()
```

If:

```text
dp[m] == INF
```

answer:

```text
-1
```

Otherwise:

```text
dp[m]
```

is the minimum number of occurrences.

But we also need to reconstruct which intervals were chosen.

---

# 4.10 Reconstruction prerequisite

Whenever we improve:

```text
dp[end]
```

from state `x`, store:

```text
parentState[end] = x
patternUsed[end] = pattern index
startUsed[end] = occurrence start
```

Then start from:

```text
cur = m
```

and repeatedly move:

```text
cur = parentState[cur]
```

until:

```text
cur = 0
```

This gives the chosen operations in reverse order.

---

# 4.11 Build all occurrences

For every pattern `s[id]`:

Try every start position:

```text
start = 0 ... m-|s[id]|
```

Check whether:

```text
t.substr(start, length) == s[id]
```

If yes, create:

```text
Occurrence {
    start,
    end = start + length,
    id
}
```

Constraints are tiny enough for direct matching.

---

# 4.12 Detailed example

Take:

```text
t = "bababa"

patterns:
1 → "ba"
2 → "aba"
```

Occurrences:

```text
pattern 1 "ba":
[0,2)
[2,4)
[4,6)

pattern 2 "aba":
[1,4)
[3,6)
```

---

## Start

```text
dp[0] = 0
```

Coverage:

```text
nothing yet
```

From `x=0`, valid occurrence must satisfy:

```text
start <= 0 < end
```

Only:

```text
[0,2)
```

So:

```text
dp[2] = 1
```

---

## State `x=2`

We have covered:

```text
[0,2)
```

Occurrences that cross position `2`:

```text
[1,4)
[2,4)
```

Both can extend to `4`.

Thus:

```text
dp[4] = 2
```

---

## State `x=4`

We covered:

```text
[0,4)
```

Occurrences crossing `4` include:

```text
[3,6)
[4,6)
```

So:

```text
dp[6] = 3
```

Final:

```text
answer = 3
```

One possible solution:

```text
"ba"  at 1-based position 1
"aba" at 1-based position 2
"aba" at 1-based position 4
```

Different valid minimum covers may also exist.

---

# 4.13 Why overlapping is useful

Suppose current coverage ends at:

```text
x = 5
```

An occurrence:

```text
[3,8)
```

starts before `5`, but extends beyond it.

That is useful:

```text
old: [0----------5)
new:       [3-------------8)

union:
     [0-------------------8)
```

So do **not** require:

```text
start == x
```

The correct condition is:

```text
start <= x < end
```

This is the key interval insight.

---

# 4.14 Complexity

Let:

```text
L = |t| <= 100
N = number of strings <= 10
S = maximum pattern length <= 10
```

Finding occurrences directly costs at most roughly:

```text
O(L × N × S)
```

Number of occurrences is small.

DP checks:

```text
L states × occurrences
```

So this is easily fast enough.

A safe simple bound is around:

```text
O(L²N + LNS)
```

which is tiny for the constraints.

---

# 4.15 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Occurrence {
    int start;
    int end;
    int id;
};

void solve() {
    string target;
    cin >> target;

    int n;
    cin >> n;

    vector<string> patterns(n);

    for (string& s : patterns) {
        cin >> s;
    }

    int m = target.size();

    vector<Occurrence> occurrences;

    for (int id = 0; id < n; id++) {
        int len = patterns[id].size();

        for (int start = 0; start + len <= m; start++) {
            if (target.substr(start, len) == patterns[id]) {
                occurrences.push_back({start, start + len, id});
            }
        }
    }

    const int INF = 1e9;

    vector<int> dp(m + 1, INF);
    vector<int> parentState(m + 1, -1);
    vector<int> usedPattern(m + 1, -1);
    vector<int> usedStart(m + 1, -1);

    dp[0] = 0;

    for (int covered = 0; covered <= m; covered++) {
        if (dp[covered] == INF) {
            continue;
        }

        for (const auto& occ : occurrences) {
            if (occ.start <= covered && covered < occ.end) {
                if (dp[covered] + 1 < dp[occ.end]) {
                    dp[occ.end] = dp[covered] + 1;
                    parentState[occ.end] = covered;
                    usedPattern[occ.end] = occ.id;
                    usedStart[occ.end] = occ.start;
                }
            }
        }
    }

    if (dp[m] == INF) {
        cout << -1 << '\n';
        return;
    }

    vector<pair<int, int>> answer;

    int cur = m;

    while (cur > 0) {
        answer.push_back({
            usedPattern[cur] + 1,
            usedStart[cur] + 1
        });

        cur = parentState[cur];
    }

    reverse(answer.begin(), answer.end());

    cout << answer.size() << '\n';

    for (auto [patternId, start] : answer) {
        cout << patternId << ' ' << start << '\n';
    }
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int q;
    cin >> q;

    while (q--) {
        solve();
    }

    return 0;
}
```

---

# 4.16 Why this problem is still useful for DP thinking

The official problem can also be viewed greedily as interval covering.

But for DP learning, this formulation teaches three valuable skills:

```text
1. Convert string occurrences into intervals.
2. Define state by covered prefix length.
3. Store parent information for reconstruction.
```

These ideas appear again in:

```text
string segmentation
interval DP
shortest path on states
minimum operations with reconstruction
```

---

# 4.17 Recognition model

When you see:

```text
many operations each cover an interval
+
goal is to cover an entire prefix/string
+
operations may overlap
+
minimum operations
```

think:

```text
covered-prefix state
```

and ask:

```text
Which interval can extend my current coverage?
```

---

# 5. Problem 3 — Flowers

## Codeforces Link

https://codeforces.com/problemset/problem/474/D

---

## 5.1 What is the problem asking?

A dinner is a sequence of flowers.

Two kinds:

```text
Red
White
```

A red flower can appear individually.

White flowers must appear only in groups of exactly:

```text
k
```

For many queries `[a,b]`, count how many valid sequences have total length:

```text
a
a+1
...
b
```

Answer modulo:

```text
1e9 + 7
```

---

# 5.2 Story removed

Forget colors for a moment.

We want to build total length `len`.

At the beginning of the construction, the next piece can be:

```text
one red flower
```

which contributes length:

```text
1
```

or:

```text
one white block
```

which contributes length:

```text
k
```

Therefore the problem becomes:

> How many ordered ways can we construct total length `len` using pieces of size `1` and `k`?

This is the core DP model.

---

# 5.3 Why order matters

For:

```text
k = 2
length = 3
```

Possible constructions:

```text
R R R
R W W
W W R
```

The last two are different because the white block appears in a different position.

So this is not a combination problem.

It is an ordered construction DP.

---

# 5.4 STATE

Define:

```text
dp[len]
=
number of valid flower sequences
having exactly len flowers
```

Examples for `k=2`:

```text
dp[1] = 1
dp[2] = 2
dp[3] = 3
```

---

# 5.5 CHOICES

Look at the **last piece** of a valid sequence of length `len`.

There are only two possibilities.

### Choice 1 — Last piece is one red flower

Remove that final red flower.

Remaining length:

```text
len - 1
```

Number of such constructions:

```text
dp[len-1]
```

---

### Choice 2 — Last piece is one white block of size `k`

Possible only when:

```text
len >= k
```

Remove that final white block.

Remaining length:

```text
len - k
```

Number of such constructions:

```text
dp[len-k]
```

---

# 5.6 TRANSITION

Therefore:

```text
dp[len]
=
dp[len-1]
```

and if:

```text
len >= k
```

add:

```text
dp[len-k]
```

Final:

```text
dp[len]
=
dp[len-1]
+
dp[len-k]
```

when `len >= k`.

Otherwise:

```text
dp[len]
=
dp[len-1]
```

---

# 5.7 BASE CASE

The cleanest base case is:

```text
dp[0] = 1
```

Why one way for length zero?

Because there is exactly one empty construction.

This allows:

```text
len = k
```

to correctly count the all-white block using:

```text
dp[k-k]
=
dp[0]
=
1
```

---

## Example `k = 3`

```text
dp[0] = 1
```

For lengths below `3`:

```text
dp[1] = dp[0] = 1
dp[2] = dp[1] = 1
```

For `3`:

```text
dp[3]
=
dp[2] + dp[0]
=
1 + 1
=
2
```

Sequences:

```text
RRR
WWW
```

Correct.

---

# 5.8 Dry run for `k = 2`

Start:

```text
dp[0] = 1
```

### `len = 1`

```text
1 < k

dp[1]
=
dp[0]
=
1
```

Valid:

```text
R
```

---

### `len = 2`

```text
dp[2]
=
dp[1] + dp[0]
=
1 + 1
=
2
```

Valid:

```text
RR
WW
```

---

### `len = 3`

```text
dp[3]
=
dp[2] + dp[1]
=
2 + 1
=
3
```

Valid:

```text
RRR
RWW
WWR
```

---

### `len = 4`

```text
dp[4]
=
dp[3] + dp[2]
=
3 + 2
=
5
```

Possible:

```text
RRRR
RRWW
RWWR
WWRR
WWWW
```

So:

```text
dp = [1, 1, 2, 3, 5, ...]
```

For `k=2`, it resembles Fibonacci because the pieces have sizes `1` and `2`.

---

# 5.9 Why DP alone is not enough

There may be up to:

```text
10^5 queries
```

Each query asks:

```text
sum dp[len]
for len from a to b
```

If we loop through `[a,b]` for every query:

```text
O(range length × number of queries)
```

could become around:

```text
10^10
```

Too slow.

So after DP, add a second preprocessing layer:

```text
prefix sums
```

---

# 5.10 Prefix sum state

Define:

```text
prefix[i]
=
dp[1] + dp[2] + ... + dp[i]
```

Transition:

```text
prefix[i]
=
prefix[i-1] + dp[i]
```

modulo `MOD`.

---

# 5.11 Query formula derivation

Suppose we need:

```text
dp[a] + dp[a+1] + ... + dp[b]
```

But:

```text
prefix[b]
=
dp[1] + ... + dp[a-1]
+ dp[a] + ... + dp[b]
```

and:

```text
prefix[a-1]
=
dp[1] + ... + dp[a-1]
```

Subtract:

```text
prefix[b] - prefix[a-1]
```

Everything before `a` cancels.

Therefore:

```text
answer
=
prefix[b] - prefix[a-1]
```

Modulo-safe:

```text
answer
=
(prefix[b] - prefix[a-1] + MOD) % MOD
```

---

# 5.12 Query dry run

For:

```text
k = 2
```

we had:

```text
len:       1  2  3  4
dp:        1  2  3  5
```

Prefix:

```text
prefix[1] = 1
prefix[2] = 1+2 = 3
prefix[3] = 1+2+3 = 6
prefix[4] = 1+2+3+5 = 11
```

Query:

```text
[a,b] = [2,3]
```

Wanted:

```text
dp[2] + dp[3]
=
2 + 3
=
5
```

Using prefix:

```text
prefix[3] - prefix[1]
=
6 - 1
=
5
```

---

# 5.13 FINAL SUBPROBLEM

There is not one fixed final DP state because there are many queries.

We first precompute:

```text
dp[0 ... MAX]
prefix[0 ... MAX]
```

Then each query:

```text
a b
```

returns:

```text
(prefix[b] - prefix[a-1] + MOD) % MOD
```

---

# 5.14 Complexity

Maximum flower count:

```text
MAX = 100000
```

DP preprocessing:

```text
O(MAX)
```

Prefix preprocessing:

```text
O(MAX)
```

Each query:

```text
O(1)
```

For `t` queries:

```text
Total
=
O(MAX + t)
```

Space:

```text
O(MAX)
```

This is exactly the type of improvement required when many queries ask ranges over the same DP values.

---

# 5.15 C++17

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
        dp[len] = dp[len - 1];

        if (len >= k) {
            dp[len] += dp[len - k];
        }

        dp[len] %= MOD;
    }

    for (int len = 1; len <= MAXN; len++) {
        prefix[len] = (prefix[len - 1] + dp[len]) % MOD;
    }

    while (t--) {
        int a, b;
        cin >> a >> b;

        ll answer = (prefix[b] - prefix[a - 1] + MOD) % MOD;

        cout << answer << '\n';
    }

    return 0;
}
```

---

# 5.16 Recognition model

Think **Flowers-style DP** when:

```text
construct length / sum / score
using several allowed block sizes
```

and each final construction can be classified by its last chosen block.

Mental pattern:

```text
dp[x]
=
Σ dp[x - allowedMove]
```

Then if many range queries ask:

```text
sum of dp[a..b]
```

add:

```text
prefix sum on top of DP
```

---

# 6. Pattern Comparison

| Problem | State type | Main choice | DP type | Extra idea |
|---|---|---|---|---|
| Block Sequence | Suffix index | Skip / start block | Maximum | Convert min deletions → max retained |
| Color with Occurrences | Covered prefix | Choose extending interval | Minimum | Reconstruction |
| Flowers | Constructed length | End with size `1` / `k` | Counting | Prefix sums for queries |

---

## 6.1 Block Sequence mental picture

```text
index i
   |
   +---- skip ----> i+1
   |
   +---- take ----> i+a[i]+1
```

Use:

```text
max()
```

because we maximize retained elements.

---

## 6.2 Color with Occurrences mental picture

```text
covered = x

[===========)
0           x

     [================)
     start            end

start <= x < end

new state = end
```

Use:

```text
min()
```

because we minimize selected occurrences.

---

## 6.3 Flowers mental picture

```text
             dp[len]
             /     \
            /       \
        final R    final WWW...W
          |            |
      dp[len-1]     dp[len-k]
```

Use:

```text
+
```

because both groups of constructions are valid and disjoint by their final piece.

---

# 7. How to Recognize These Patterns in Contests

Do not try to memorize problem names.

Look for structural clues.

---

## Pattern A — Current value determines a jump

Clues:

```text
array
remove some elements
current number determines block length
take or skip current position
```

Think:

```text
suffix DP
```

with:

```text
skip → i+1
take → jump
```

Example:

```text
Block Sequence
```

---

## Pattern B — Cover a line/string using intervals

Clues:

```text
substrings / segments / operations cover ranges
overlap allowed
cover everything
minimum number of operations
```

Convert:

```text
operation → interval
```

then think:

```text
covered prefix
```

Example:

```text
Color with Occurrences
```

---

## Pattern C — Count constructions using pieces

Clues:

```text
number of ways
build total length
allowed unit sizes
order matters
```

Think:

```text
dp[len]
=
sum of dp[len - choice]
```

Example:

```text
Flowers
```

---

## Pattern D — Many queries over same DP table

Clues:

```text
up to 10^5 queries
each asks answer over [a,b]
same global parameter k
```

Think:

```text
precompute DP once
+
prefix sum
+
O(1) query
```

---

# 8. Final Mental Model

These three problems teach three different meanings of a DP transition.

---

## Problem 1

```text
At position i:

DELETE IT
    ↓
dp[i+1]

or

MAKE IT A BLOCK HEADER
    ↓
1 + a[i] + dp[i+a[i]+1]
```

State:

```text
best retained elements from suffix i
```

---

## Problem 2

```text
I have completely covered [0,x).

Which occurrence overlaps x
and reaches farther?

[start,end)

start <= x < end
        ↓
new state = end
```

State:

```text
minimum operations to cover prefix [0,x)
```

---

## Problem 3

```text
How can a valid construction of length len end?

RED:
remove 1
→ dp[len-1]

WHITE BLOCK:
remove k
→ dp[len-k]
```

State:

```text
number of valid constructions of exact length len
```

Then:

```text
many range queries
        ↓
prefix sums
```

---

# The 4 Questions to Ask Before Coding

For every DP problem, write these four answers first:

```text
1. STATE
   What exactly does dp[...] mean?

2. TRANSITION
   What choices create this state?

3. BASE CASE
   Which smallest states are already known?

4. FINAL SUBPROBLEM
   Which dp state / expression is the actual answer?
```

Only after these are clear should you write C++.

That is the habit that turns DP from formula memorization into problem solving.
