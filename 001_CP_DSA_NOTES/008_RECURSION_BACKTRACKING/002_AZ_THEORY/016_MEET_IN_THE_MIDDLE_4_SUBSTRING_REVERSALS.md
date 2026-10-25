# Meet-in-the-Middle — Transform String `S` into `T` Using 4 Substring Reversals
## TLE-Style Self-Study Note with Prerequisites, Recursion Trees, Dry Runs, Proof, and C++17
### Pattern: Reversible Operations + Fixed Number of Moves + Meet in the Middle

> **Core problem**
>
> Given two strings `S` and `T` of equal length `N`, determine whether `S` can be transformed into `T` using **4 substring reversals**.
>
> In one operation, choose a substring `[l, r]` and reverse it.

---

# Table of Contents

1. [Problem Statement](#1-problem-statement)
2. [Prerequisite 1 — What Is a Substring Reversal?](#2-prerequisite-1--what-is-a-substring-reversal)
3. [Prerequisite 2 — How Many Reversals Are Possible?](#3-prerequisite-2--how-many-reversals-are-possible)
4. [Prerequisite 3 — Reversal Is Self-Inverse](#4-prerequisite-3--reversal-is-self-inverse)
5. [Brute Force Idea](#5-brute-force-idea)
6. [Brute Force Recursion Tree](#6-brute-force-recursion-tree)
7. [Why Brute Force Is Too Slow](#7-why-brute-force-is-too-slow)
8. [The Meet-in-the-Middle Observation](#8-the-meet-in-the-middle-observation)
9. [Why We Can Search from Both `S` and `T`](#9-why-we-can-search-from-both-s-and-t)
10. [The Middle-State Equation](#10-the-middle-state-equation)
11. [Generate All Strings Reachable in 2 Reversals](#11-generate-all-strings-reachable-in-2-reversals)
12. [Recursion Tree for `generate(S, 2)`](#12-recursion-tree-for-generates-2)
13. [MITM Algorithm](#13-mitm-algorithm)
14. [Detailed Dry Run](#14-detailed-dry-run)
15. [C++17 Implementation](#15-c17-implementation)
16. [Code Walkthrough](#16-code-walkthrough)
17. [Correctness Proof](#17-correctness-proof)
18. [Complexity Analysis](#18-complexity-analysis)
19. [Exactly 4 vs At Most 4 Reversals](#19-exactly-4-vs-at-most-4-reversals)
20. [Important Optimizations](#20-important-optimizations)
21. [Common Mistakes](#21-common-mistakes)
22. [Recognition Pattern](#22-recognition-pattern)
23. [Generalization to `K` Reversible Moves](#23-generalization-to-k-reversible-moves)
24. [Final Mental Model](#24-final-mental-model)
25. [One-Minute Revision](#25-one-minute-revision)

---

# 1. Problem Statement

We have:

```text
S = starting string
T = target string
```

One move is:

```text
choose l and r
reverse S[l ... r]
```

We want to know whether there exists a sequence of four reversals:

```text
S
 --reverse 1-->
S1
 --reverse 2-->
S2
 --reverse 3-->
S3
 --reverse 4-->
T
```

---

## Lecture-style example

The lecture shows a transformation chain conceptually like:

```text
S
↓ reverse one substring
S'
↓ reverse another substring
S''
↓ reverse another substring
S'''
↓ reverse another substring
T
```

The actual important idea is not the exact characters.

The important structure is:

```text
4 moves
+
each move has O(N^2) possible substring choices
```

That immediately suggests a huge brute-force search tree.

---

# 2. Prerequisite 1 — What Is a Substring Reversal?

Given:

```text
S = "abcdef"
```

choose:

```text
l = 2
r = 4
```

using 0-based indexing.

The substring is:

```text
S[2 ... 4] = "cde"
```

Reverse it:

```text
"cde" → "edc"
```

So:

```text
abcdef
  |||
  cde

becomes

abedcf
```

More explicitly:

```text
prefix      reversed part      suffix

"ab"   +      "edc"       +     "f"

= "abedcf"
```

---

## C++ operation

```cpp
string next = current;
reverse(next.begin() + left, next.begin() + right + 1);
```

---

# 3. Prerequisite 2 — How Many Reversals Are Possible?

For one reversal, we choose:

```text
left endpoint  l
right endpoint r
```

where:

```text
0 <= l <= r < N
```

The number of substrings is:

```text
N(N + 1) / 2
```

which is:

```text
O(N^2)
```

So every state has about:

```text
O(N^2)
```

possible next states.

---

## Tiny example

For:

```text
N = 3
```

string positions are:

```text
0 1 2
```

Possible intervals include:

```text
[0,0]
[0,1]
[0,2]
[1,1]
[1,2]
[2,2]
```

Total:

```text
3 * 4 / 2 = 6
```

---

## Visual branching factor

```text
                current string
                     |
       --------------------------------
       |        |        |        ...
      [0,0]    [0,1]    [0,2]     ...
       |        |        |
      S1       S2       S3
```

There are:

```text
Theta(N^2)
```

children from one node.

---

# 4. Prerequisite 3 — Reversal Is Self-Inverse

This is the most important prerequisite for the MITM solution.

Suppose:

```text
S = "abcdef"
```

Reverse:

```text
[1,4]
```

We get:

```text
aedcbf
```

Now reverse the **same interval** again:

```text
aedcbf
  ↓ reverse [1,4]

abcdef
```

Therefore:

```text
reverse(reverse(S, l, r), l, r) = S
```

A substring reversal is its own inverse.

---

## Why this matters

Suppose we have:

```text
M --reverse A--> X --reverse B--> T
```

Then starting from `T`, we can undo those operations:

```text
T --reverse B--> X --reverse A--> M
```

So:

```text
M can reach T in 2 reversals
```

is equivalent to:

```text
T can reach M in 2 reversals
```

This is exactly why we are allowed to search outward from both `S` and `T`.

---

# 5. Brute Force Idea

The direct method is:

```text
try every first reversal
    try every second reversal
        try every third reversal
            try every fourth reversal
                check whether result == T
```

Each level has:

```text
O(N^2)
```

choices.

Four levels give:

```text
O((N^2)^4)
=
O(N^8)
```

candidate reversal sequences.

---

## Recursive form

Define:

```text
generate(currentString, movesLeft)
```

At each call:

```text
for every (l,r):
    next = reverse(currentString,l,r)
    generate(next,movesLeft-1)
```

Base case:

```text
movesLeft == 0
```

Then compare against `T`.

---

# 6. Brute Force Recursion Tree

Let:

```text
B = number of possible intervals ≈ N^2
```

Then:

```text
                         S
                 / / / / | \ \ \ \
                B possible reversals
              /           |           \
            S1            S2           ...
          / | \         / | \
         B children    B children
          ...
```

Depth:

```text
4
```

Leaves:

```text
B^4
≈
(N^2)^4
=
N^8
```

---

## Level-by-level

```text
level 0: 1

level 1: N^2

level 2: N^4

level 3: N^6

level 4: N^8
```

So the search blows up rapidly.

---

# 7. Why Brute Force Is Too Slow

Ignoring string manipulation cost:

```text
O(N^8)
```

candidate states are generated at depth 4.

But constructing/reversing a string costs up to:

```text
O(N)
```

with the straightforward implementation.

Therefore the lecture-style upper-bound becomes:

```text
O(N^8 * N)
=
O(N^9)
```

This matches the important lecture observation:

```text
brute force ≈ O(N^9)
```

when full string-copy/reversal work is included.

---

## Example intuition

If:

```text
N = 7
```

the exponent is already painful:

```text
N^8
```

candidate move sequences, plus string work.

We need to split the 4 moves.

---

# 8. The Meet-in-the-Middle Observation

Instead of searching:

```text
4 moves from S
```

split the move sequence into:

```text
2 moves from S
+
2 moves from T
```

We search for a string `M` such that:

```text
S --2 reversals--> M
```

and:

```text
T --2 reversals--> M
```

If such an `M` exists, then:

```text
S --2 reversals--> M --2 reversals--> T
```

because the reversals from `T` to `M` can be reversed in order to go from `M` to `T`.

---

## MITM picture

```text
S
 \
  \ 2 reversals
   \
    M
   /
  / 2 reversals, searched backward from T
 /
T
```

Or as a path:

```text
S
 --1-->
S1
 --2-->
M
 --3-->
T1
 --4-->
T
```

We do not need to explicitly know reversals 3 and 4 while generating from `S`.

We discover the same middle state from `T`.

---

# 9. Why We Can Search from Both `S` and `T`

Suppose a valid four-move path exists:

```text
S
 --R1-->
A
 --R2-->
M
 --R3-->
B
 --R4-->
T
```

Because reversal is self-inverse:

```text
T
 --R4-->
B
 --R3-->
M
```

So `M` is reachable:

```text
from S in 2 reversals
```

and:

```text
from T in 2 reversals
```

Therefore every valid four-move path creates a common middle string.

---

# 10. The Middle-State Equation

Define:

```text
Gen(S,2)
=
set of all strings reachable from S
after exactly 2 reversals
```

Similarly:

```text
Gen(T,2)
=
set of all strings reachable from T
after exactly 2 reversals
```

Then:

```text
S can reach T in 4 reversals
```

iff:

```text
Gen(S,2) ∩ Gen(T,2) != empty
```

That is the entire MITM solution.

---

## Visual form

```text
Generate from S:

S
├── ...
│   └── M
├── ...
└── ...

Generate from T:

T
├── ...
│   └── M
├── ...
└── ...

Common state:
M
```

---

# 11. Generate All Strings Reachable in 2 Reversals

Define:

```text
generate(current, k)
```

Meaning:

```text
Generate every string reachable from current
using exactly k more reversals.
```

---

## Base case

If:

```text
k == 0
```

store:

```text
current
```

in the generated set.

---

## Recursive case

For every interval:

```text
[l,r]
```

create:

```text
next = current
reverse(next[l ... r])
```

then recurse:

```text
generate(next, k - 1)
```

---

## Pseudocode

```text
generate(s, k):

    if k == 0:
        insert s into generated set
        return

    for every l:
        for every r >= l:
            next = s
            reverse next[l ... r]
            generate(next, k - 1)
```

---

# 12. Recursion Tree for `generate(S, 2)`

For simplicity, let:

```text
B ≈ N^2
```

possible reversals.

Then:

```text
                         S
                 / / / / | \ \ \ \
              B possible first moves
                /          |        \
              S1           S2       ...
            / | \        / | \
          B moves      B moves
            |             |
         middle         middle
         strings        strings
```

Number of depth-2 paths:

```text
B^2
=
(N^2)^2
=
N^4
```

So each side generates about:

```text
O(N^4)
```

candidate middle strings.

---

# 13. MITM Algorithm

## Step 1

Generate all strings reachable from `S` in exactly 2 reversals:

```text
leftSet = generate(S,2)
```

---

## Step 2

Generate all strings reachable from `T` in exactly 2 reversals:

```text
rightSet = generate(T,2)
```

---

## Step 3

Check whether they share any common string.

```text
leftSet ∩ rightSet != empty
```

If yes:

```text
YES
```

Otherwise:

```text
NO
```

---

## Better implementation pattern

We do not necessarily need to store both sets.

We can:

```text
1. store all states from S after 2 moves
2. generate states from T after 2 moves
3. at each T-side leaf, check whether it exists in S-set
```

This saves one large set.

---

# 14. Detailed Dry Run

Use a small teaching example:

```text
S = "abcd"
T = "badc"
```

We ask whether some common middle string can be reached in two reversals from both sides.

Suppose from `S`:

```text
S = "abcd"

Reverse [0,1]:
"bacd"

Reverse [2,3]:
"badc"
```

So:

```text
"badc" ∈ Gen(S,2)
```

Now from:

```text
T = "badc"
```

if length-1 reversals are allowed, we can perform two no-op reversals and remain at:

```text
"badc"
```

Thus:

```text
"badc" ∈ Gen(T,2)
```

There is a common middle string.

---

## More important structural dry run

Assume a true path:

```text
S --R1--> A --R2--> M --R3--> B --R4--> T
```

Forward half:

```text
generate(S,2)
```

eventually explores:

```text
R1
then R2
```

and stores:

```text
M
```

Backward half:

```text
generate(T,2)
```

eventually explores:

```text
R4
then R3
```

because each reversal undoes itself.

So it also reaches:

```text
M
```

Intersection succeeds.

---

## Path visualization

```text
                   M
                /     \
              R2       R3
              /         \
             A           B
             |           |
            R1          R4
             |           |
             S           T
```

Search only two levels from each endpoint.

---

# 15. C++17 Implementation

```cpp
#include <bits/stdc++.h>
using namespace std;

unordered_set<string> middleStates;

void generateFromStart(const string& current, int movesLeft) {
    if (movesLeft == 0) {
        middleStates.insert(current);
        return;
    }

    int n = current.size();

    for (int left = 0; left < n; left++) {
        for (int right = left; right < n; right++) {
            string next = current;
            reverse(next.begin() + left, next.begin() + right + 1);
            generateFromStart(next, movesLeft - 1);
        }
    }
}

bool searchFromTarget(const string& current, int movesLeft) {
    if (movesLeft == 0) {
        return middleStates.count(current);
    }

    int n = current.size();

    for (int left = 0; left < n; left++) {
        for (int right = left; right < n; right++) {
            string next = current;
            reverse(next.begin() + left, next.begin() + right + 1);

            if (searchFromTarget(next, movesLeft - 1)) {
                return true;
            }
        }
    }

    return false;
}

bool canTransformInFourReversals(const string& s, const string& t) {
    if (s.size() != t.size()) {
        return false;
    }

    string sortedS = s;
    string sortedT = t;

    sort(sortedS.begin(), sortedS.end());
    sort(sortedT.begin(), sortedT.end());

    if (sortedS != sortedT) {
        return false;
    }

    middleStates.clear();

    generateFromStart(s, 2);
    return searchFromTarget(t, 2);
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    string s, t;
    cin >> s >> t;

    cout << (canTransformInFourReversals(s, t) ? "YES" : "NO") << '\n';

    return 0;
}
```

---

# 16. Code Walkthrough

## Character multiset check

A substring reversal only changes order.

It never changes which characters exist.

Therefore if:

```text
sorted(S) != sorted(T)
```

transformation is impossible.

This is a cheap necessary condition.

---

## Generate from `S`

```cpp
generateFromStart(s, 2);
```

This stores all middle strings reachable after two reversals.

---

## Search from `T`

```cpp
searchFromTarget(t, 2);
```

At depth 2:

```cpp
return middleStates.count(current);
```

If the same middle state exists in the set, the two halves connect.

---

## Why `unordered_set<string>`?

We need fast membership:

```text
Does this middle string exist?
```

Average hash-table lookup is:

```text
O(1)
```

ignoring the cost of hashing the string itself.

Since hashing a length-`N` string costs:

```text
O(N)
```

the realistic lookup cost includes the string length.

---

# 17. Correctness Proof

We prove:

```text
algorithm returns true
iff
there exists a four-reversal transformation from S to T
```

---

## 17.1 If the algorithm returns true

Then there exists some string:

```text
M
```

such that:

```text
M ∈ Gen(S,2)
```

and:

```text
M ∈ Gen(T,2)
```

Therefore:

```text
S --2 reversals--> M
```

and:

```text
T --2 reversals--> M
```

Because reversals are self-inverse, reverse the second two-reversal sequence:

```text
M --2 reversals--> T
```

Hence:

```text
S --2--> M --2--> T
```

So `S` reaches `T` in four reversals.

---

## 17.2 If a four-reversal transformation exists

Suppose:

```text
S --R1--> A --R2--> M --R3--> B --R4--> T
```

The first generator enumerates every pair of reversals from `S`, so it generates `M`.

From `T`, applying:

```text
R4
then R3
```

returns to `M`.

The target-side search enumerates every pair of reversals from `T`, so it also reaches `M`.

Therefore the intersection is non-empty and the algorithm returns true.

---

# 18. Complexity Analysis

This section is important because the lecture distinguishes the **number of generated states** from the **cost of producing each string**.

---

## 18.1 One reversal

Number of intervals:

```text
O(N^2)
```

Copy/reverse one string:

```text
O(N)
```

---

## 18.2 Two reversals

Number of reversal sequences:

```text
O((N^2)^2)
=
O(N^4)
```

Each leaf string has length `N`.

With straightforward string copies/reversals, lecture-style complexity is approximately:

```text
O(N^4 * N)
=
O(N^5)
```

for generating one side.

---

## 18.3 Two MITM sides

Generate from:

```text
S with 2 moves
T with 2 moves
```

So:

```text
O(N^5) + O(N^5)
=
O(N^5)
```

asymptotically.

The set operations also hash/compare strings of length `N`, so they remain compatible with this `O(N^5)` style upper bound.

---

## 18.4 Comparison

Direct brute force:

```text
4 reversal levels

number of move sequences:
(N^2)^4 = N^8

including O(N) string work:
O(N^9)
```

MITM:

```text
2 levels from S
2 levels from T

each side:
(N^2)^2 = N^4 states

including string work:
O(N^5)
```

Therefore the important reduction is:

```text
O(N^9)
↓
O(N^5)
```

in the lecture's cost model.

---

## Search-tree comparison

```text
BRUTE FORCE

S
|
N^2
|
N^2
|
N^2
|
N^2
|
T?

≈ N^8 paths
```

versus:

```text
MITM

S                T
|                |
N^2              N^2
|                |
N^2              N^2
|                |
Gen1 ----∩---- Gen2

≈ N^4 states per side
```

---

# 19. Exactly 4 vs At Most 4 Reversals

This detail matters.

The lecture formulation generates:

```text
generate(S,2)
generate(T,2)
```

and checks for a common state.

That directly models a split of:

```text
2 + 2 = 4
```

moves.

---

## If length-1 intervals are allowed

Reversing:

```text
[l,l]
```

changes nothing.

So any solution using fewer moves can often be padded with no-op reversals.

For example:

```text
2 real reversals
+
2 length-1 reversals
=
4 operations
```

Hence "exactly 4" and "at most 4" can coincide under this operation definition.

---

## If the problem requires `l < r`

Then there are no guaranteed no-op moves.

In that case:

```text
exactly 4
```

and:

```text
at most 4
```

are genuinely different problems.

The implementation must follow the actual problem statement.

---

# 20. Important Optimizations

## 20.1 Deduplicate middle states

Many different reversal sequences may produce the same string.

Example:

```text
sequence A → "abcde"
sequence B → "abcde"
```

We only need one copy.

So:

```cpp
unordered_set<string>
```

automatically deduplicates.

---

## 20.2 Generate one side, search the other immediately

Instead of storing:

```text
Gen(S,2)
and
Gen(T,2)
```

store only:

```text
Gen(S,2)
```

Then DFS from `T`.

At each leaf:

```text
if current in Gen(S,2)
    return true
```

This can terminate early.

---

## 20.3 Check character frequencies first

Reversal preserves character counts.

So before exponential generation:

```text
frequency(S) must equal frequency(T)
```

For lowercase English letters, an array of 26 counts works.

Sorting is simpler:

```cpp
sort(sortedS.begin(), sortedS.end());
sort(sortedT.begin(), sortedT.end());
```

---

## 20.4 Skip duplicate generated children locally

Different intervals can sometimes produce the same next string, especially with repeated characters.

A local set can avoid recursing into duplicate children from the same node.

This is optional and problem-size dependent.

---

# 21. Common Mistakes

## Mistake 1 — Splitting the string instead of splitting the moves

This MITM does **not** split:

```text
first N/2 characters
second N/2 characters
```

It splits the **4 operations**:

```text
2 operations from S
2 operations from T
```

This is a different MITM form from subset-sum MITM.

---

## Mistake 2 — Searching backward without checking reversibility

MITM from the target only works cleanly because reversal is self-inverse.

For a non-reversible operation, you cannot automatically run the same operation from `T`.

Always ask:

```text
Can I reverse the transition?
```

---

## Mistake 3 — Multiplying the two generated sets

Do not do:

```text
for each state in Gen(S,2):
    for each state in Gen(T,2):
        compare
```

That gives:

```text
N^4 * N^4 = N^8
```

and destroys the improvement.

Use hashing/set intersection.

---

## Mistake 4 — Forgetting string-operation cost

Counting only branches gives:

```text
N^4
```

for each half.

But a straightforward implementation copies/reverses strings of length `N`.

That is why the lecture reaches roughly:

```text
N^5
```

rather than just `N^4`.

---

## Mistake 5 — Storing every recursion level

For the intersection, only depth-2 states matter.

You do not need to permanently store all intermediate strings.

---

## Mistake 6 — Forgetting character-count impossibility

If:

```text
S = "abc"
T = "abd"
```

no sequence of reversals can change:

```text
c → d
```

Return false immediately.

---

# 22. Recognition Pattern

This is an important MITM pattern that is different from subset-sum.

Recognize it when you have:

```text
start state S
target state T
exactly K moves
large branching factor
reversible transitions
small K
```

Then think:

```text
split K into K/2 + K/2
```

Generate states from both endpoints and intersect.

---

## Recognition checklist

```text
1. Is there a fixed small number of operations?
2. Does each operation have many choices?
3. Does direct depth-K search explode?
4. Is the operation reversible?
5. Can I generate all states at depth K/2 from S?
6. Can I generate all states at depth K/2 from T?
7. Can I hash the states and look for intersection?
```

If yes:

```text
Meet-in-the-Middle on move depth
```

is a strong candidate.

---

# 23. Generalization to `K` Reversible Moves

Suppose each state has branching factor:

```text
B
```

and we want exactly:

```text
K
```

moves.

Naive search:

```text
B^K
```

If transitions are reversible, split:

```text
K1 = floor(K/2)
K2 = K - K1
```

Generate:

```text
B^K1
```

states from the start and:

```text
B^K2
```

states from the target.

Then intersect.

Balanced case:

```text
roughly B^(K/2)
```

instead of:

```text
B^K
```

---

## In this problem

One reversal has:

```text
B = O(N^2)
```

and:

```text
K = 4
```

So:

```text
naive:
B^4 = N^8

MITM:
B^2 = N^4
```

before state-copy cost.

---

# 24. Final Mental Model

Do not memorize:

```text
"4 reversals → generate twice"
```

Instead reason:

```text
One move:
choose substring [l,r]
→ O(N^2) choices

Four moves:
(N^2)^4
→ O(N^8) paths
→ O(N^9) with string work
```

Then notice:

```text
reversal is self-inverse
```

So place an imaginary middle state:

```text
S --2 moves--> M <--2 moves-- T
```

Generate:

```text
Gen(S,2)
```

and:

```text
Gen(T,2)
```

If they share a state:

```text
M
```

then:

```text
S → M → T
```

uses four reversals.

That is the whole pattern.

---

## ASCII master diagram

```text
                        FOUR-MOVE SEARCH

S
|
| R1
v
A
|
| R2
v
M
|
| R3
v
B
|
| R4
v
T


                 CUT AT THE MIDDLE


S                                       T
|                                       |
| all 1st reversals                     | undo all 4th reversals
v                                       v
many states                         many states
|                                       |
| all 2nd reversals                     | undo all 3rd reversals
v                                       v

        Gen(S,2)  ∩  Gen(T,2)
                  |
                  v
             common M ?
```

---

# 25. One-Minute Revision

```text
Problem:
Can S become T using 4 substring reversals?

One reversal:
choose [l,r]

Number of choices:
O(N^2)

Brute force:
4 levels

(N^2)^4
=
N^8 paths

With O(N) string work:
≈ O(N^9)

Key property:
substring reversal is self-inverse

Therefore split 4 moves:

2 from S
+
2 backward from T

Generate:

Gen(S,2)
Gen(T,2)

If intersection is non-empty:
YES

Why?
If M is reachable from T in 2 reversals,
the same reversals in reverse order
take M to T.

Each half:
(N^2)^2 = N^4 paths

With string work:
≈ O(N^5)

Core recognition:
fixed-depth search
+
reversible transitions
+
large branching factor
→ MITM on the number of moves
```
