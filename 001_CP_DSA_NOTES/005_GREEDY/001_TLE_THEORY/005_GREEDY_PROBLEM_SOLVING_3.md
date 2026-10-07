# Greedy Problem Solving 3 — Level 3
## Detailed Self-Study Notes — Prerequisites + Derivations + Inline Examples + C++17

> **Goal:** understand *why* each greedy choice works instead of memorizing the final code.
>
> **Problem flow:**
>
> ```text
> prerequisites
> → what the problem asks
> → story → variables
> → tiny example first
> → key observation
> → greedy claim
> → proof with inline numerical example
> → general derivation
> → full dry run
> → visual model
> → algorithm
> → C++17
> → complexity
> → edge cases
> → recognition model
> → don't-memorize model
> ```
>
> **Proof style:** every important symbolic statement is immediately mapped to real numbers.
>
> **Math rendering:** display equations use fenced `math` blocks only.

---

# Clickable Table of Contents

- [0. How to Use This Note](#0-how-to-use-this-note)
- [1. Shared Prerequisites](#1-shared-prerequisites)
- [2. Problem 1 — Candy Box](#2-problem-1--candy-box)
- [3. Problem 2 — Increasing Subsequence](#3-problem-2--increasing-subsequence)
- [4. Problem 3 — Triangle Coloring](#4-problem-3--triangle-coloring)
- [5. Pattern Comparison](#5-pattern-comparison)
- [6. Final Recognition Checklist](#6-final-recognition-checklist)
- [7. Compact Revision Card](#7-compact-revision-card)

---

# 0. How to Use This Note

For every problem:

```text
1. Read "What It Asks".
2. Understand the tiny example.
3. Predict the greedy choice yourself.
4. Read the proof.
5. Reproduce the proof using numbers.
6. Only then read the implementation.
7. Re-solve later from the Recognition Model.
```

The three lecture problems train three different patterns:

```text
Candy Box
→ frequency compression
→ sort frequencies
→ decreasing distinct capacities

Increasing Subsequence
→ two-ended greedy
→ choose the smaller valid endpoint
→ special tie lookahead

Triangle Coloring
→ local greedy contribution
→ local counting
→ global combinatorics
```

---

# 1. Shared Prerequisites

## 1.1 Constraint vs Objective

Always separate these two.

```text
CONSTRAINT
→ what must remain legal

OBJECTIVE
→ what we maximize/minimize
```

Example — Candy Box:

```text
Constraint:
chosen candy counts for different types
must be distinct

chosen count for a type
cannot exceed its available frequency

Objective:
maximize total candies selected
```

Example — Increasing Subsequence:

```text
Constraint:
every move removes only leftmost or rightmost

written values must be strictly increasing

Objective:
maximize number of moves
```

Example — Triangle Coloring:

```text
Constraint:
exactly n/2 red vertices
and n/2 blue vertices

Objective:
maximize coloring weight

Then:
count maximum-weight colorings
```

---

## 1.2 Greedy Choice and Dominance

A state dominates another when it is:

```text
at least as good now
and
at least as flexible later
```

Example:

```text
last = 3
```

is more flexible than:

```text
last = 7
```

if every future choice must satisfy:

```text
next > last
```

because:

```text
after 3:
4,5,6,7,8,...

after 7:
8,9,10,...
```

So a smaller valid `last` preserves a larger future option set.

---

## 1.3 Frequency Compression

Sometimes labels no longer matter after counting.

Example:

```text
1 1 1 1
2 2 2
3 3
4
```

becomes:

```text
type 1 → 4
type 2 → 3
type 3 → 2
type 4 → 1
```

Then the problem can often be solved only from:

```text
[4,3,2,1]
```

This is exactly the useful compression for Candy Box.

---

## 1.4 Strictly Increasing Sequence

Strictly increasing:

```math
x_1<x_2<x_3<\cdots
```

Example:

```text
1,3,5,8
```

Valid.

But:

```text
1,3,3,8
```

invalid because:

```text
3 < 3
```

is false.

If current last value is:

```text
last
```

the next chosen value must satisfy:

```math
next>last
```

---

## 1.5 Two Pointers / Deque Ends

If only leftmost or rightmost may be removed:

```text
L = left pointer
R = right pointer
```

Current choices:

```text
a[L]
a[R]
```

Take left:

```text
L++
```

Take right:

```text
R--
```

Current state:

```text
[L ............ R]
```

---

## 1.6 Why a Smaller Valid Next Value Is More Flexible

Suppose:

```text
last = 2
left = 4
right = 7
```

Both are legal.

Choose `4`:

```text
new last = 4
future must be > 4
```

Choose `7`:

```text
new last = 7
future must be > 7
```

Every value legal after `7` is also legal after `4`.

So when both are valid and unequal:

```text
choose the smaller endpoint
```

---

## 1.7 Tie Cases Need Extra Reasoning

Suppose:

```text
left = right = 3
```

and:

```text
3 > last
```

If we choose one `3`, then:

```text
last = 3
```

The other endpoint is still:

```text
3
```

but:

```text
3 > 3
```

is false.

So the opposite side becomes blocked.

Therefore we need one-time lookahead:

```text
How long can I continue increasing from left?

How long can I continue increasing from right?
```

Choose the longer direction.

---

## 1.8 Local Contribution in a Triangle

For one triangle:

```text
3 vertices
3 weighted edges
```

If all vertices same color:

```text
crossing edges = 0
contribution = 0
```

If coloring is:

```text
2 of one color
1 of the other
```

then exactly:

```text
2 edges
```

cross colors.

So local contribution is:

```text
sum of 2 edge weights
```

To maximize:

```text
choose the 2 largest edges
```

Equivalent:

```text
total edge sum - smallest edge
```

---

## 1.9 Multiplication Rule

If independent component 1 has:

```text
C1 ways
```

and component 2 has:

```text
C2 ways
```

together:

```math
C_1\cdot C_2
```

ways.

Example:

```text
2 ways × 3 ways
= 6 ways
```

For many independent triangles:

```math
\prod_i C_i
```

---

## 1.10 Combinations

Number of ways to choose `r` objects from `n`:

```math
C(n,r)
=
\frac{n!}{r!(n-r)!}
```

Example:

```text
C(4,2)
= 6
```

This is used in Triangle Coloring for choosing which triangles have:

```text
2 red + 1 blue
```

---

## 1.11 nCr Modulo a Prime

The Triangle Coloring modulus is:

```text
998244353
```

which is prime.

Precompute:

```text
fact[i] = i!
invFact[i] = modular inverse of i!
```

Then:

```math
C(n,r)
=
fact[n]\cdot invFact[r]\cdot invFact[n-r]
```

all modulo `998244353`.

Fermat gives:

```math
a^{-1}
=
a^{MOD-2}\pmod{MOD}
```

for nonzero `a` modulo the prime.

---

## 1.12 Universal Greedy Proof Checklist

```text
1. What is the exact objective?

2. What information can I compress?
   frequencies?
   endpoints?
   local components?

3. What is my greedy choice?

4. Does one choice preserve more future flexibility?

5. Can I prove one state dominates another?

6. Is there a tie where the normal rule loses information?

7. Can the problem be split into independent components?

8. Can each component be optimized locally?

9. Is there a remaining global counting constraint?

10. Does the final answer need combinations?
```

---

# 2. Problem 1 — Candy Box

**Platform:** Codeforces  
**Problem:** 1183D — Candy Box  
**Link:** https://codeforces.com/problemset/problem/1183/D

---

## 2.1 What the Problem Asks

For each candy type, let available frequency be:

```text
f_i
```

We may choose:

```text
0 <= g_i <= f_i
```

candies of that type.

All **positive** chosen counts must be distinct.

Example valid:

```text
5,4,2,1
```

Example invalid:

```text
5,4,4,1
```

Goal:

```math
\max\sum g_i
```

---

## 2.2 Story → Frequency Problem

Example types:

```text
1 1 1 1 1
2 2 2 2
3 3 3
4
```

Frequencies:

```text
5,4,3,1
```

The type labels no longer matter.

Reduced problem:

```text
capacities = [5,4,3,1]

choose distinct positive amounts
not exceeding those capacities

maximize sum
```

---

## 2.3 Tiny Example

Frequencies:

```text
[5,5,4]
```

Cannot choose:

```text
5,5,4
```

because `5` repeats.

Try:

```text
5,4,3
```

Total:

```text
12
```

Why is this optimal?

First maximum:

```text
5
```

Second must be distinct and at most `5`:

```text
at most 4
```

Third must be below `4`:

```text
at most 3
```

So:

```text
5,4,3
```

is the maximum possible structure.

---

## 2.4 Sort Frequencies Descending

Let:

```text
f1 >= f2 >= f3 >= ...
```

After sorting, we can enforce:

```text
g1 > g2 > g3 > ...
```

Then every current choice only needs to respect:

```text
1. own capacity
2. previous chosen count - 1
```

So:

```math
g_1=f_1
```

and:

```math
g_i
=
\min(f_i,g_{i-1}-1)
```

with values clamped at zero.

---

## 2.5 Formula With Inline Example

Sorted:

```text
f = [5,5,4,4]
```

First:

```math
g_1=f_1
```

Actual:

```text
g1 = 5
```

Second:

```math
g_2=\min(f_2,g_1-1)
```

Actual:

```text
g2
= min(5,5-1)
= min(5,4)
= 4
```

Third:

```math
g_3=\min(f_3,g_2-1)
```

Actual:

```text
g3
= min(4,4-1)
= 3
```

Fourth:

```math
g_4=\min(f_4,g_3-1)
```

Actual:

```text
g4
= min(4,3-1)
= 2
```

Total:

```text
5+4+3+2
= 14
```

---

## 2.6 Why `min(f_i, previous-1)`?

Current chosen count must satisfy availability:

```math
g_i\le f_i
```

and distinctness:

```math
g_i<g_{i-1}
```

Since integer:

```math
g_i\le g_{i-1}-1
```

Therefore:

```text
g_i has two upper bounds
```

So largest legal value is:

```math
g_i
=
\min(f_i,g_{i-1}-1)
```

This is simply the maximum integer satisfying both constraints.

---

## 2.7 Proof With Numbers First

Suppose:

```text
current capacity = 7
previous chosen = 5
```

Current must satisfy:

```text
g <= 7
```

and:

```text
g < 5
```

So:

```text
g <= 4
```

Largest legal:

```text
4
```

Could we choose `3`?

Yes, but it is worse.

Current contribution:

```text
4 > 3
```

Future ceiling after `4`:

```text
<= 3
```

Future ceiling after `3`:

```text
<= 2
```

So choosing `3` gives:

```text
less now
AND
less room later
```

Thus it is dominated.

---

## 2.8 General Dominance Proof

Suppose maximum legal current choice is:

```text
M
```

Another solution chooses:

```text
x < M
```

Immediate difference:

```math
M-x>0
```

Actual:

```text
M=4
x=3

4-3
=1
>0
```

Future ceiling after `M`:

```text
M-1
```

Future ceiling after `x`:

```text
x-1
```

Because:

```math
x<M
```

we get:

```math
x-1<M-1
```

Actual:

```text
3-1 < 4-1

2 < 3
```

So `x` is worse both now and for future capacity.

Therefore:

```text
always take the largest legal current count
```

---

## 2.9 Full Dry Run

Frequencies:

```text
[5,5,4,3,3,1]
```

### First

```text
take = 5
answer = 5
prev = 5
```

### Second

```text
take
= min(5,4)
= 4

answer = 9
prev = 4
```

### Third

```text
take
= min(4,3)
= 3

answer = 12
prev = 3
```

### Fourth

```text
take
= min(3,2)
= 2

answer = 14
prev = 2
```

### Fifth

```text
take
= min(3,1)
= 1

answer = 15
prev = 1
```

### Sixth

```text
previous-1
= 0
```

No positive distinct count remains.

Stop.

Final:

```text
5,4,3,2,1
```

Answer:

```text
15
```

---

## 2.10 Visual Model

```text
available:
5   5   4   3   3   1

choose:
5   4   3   2   1   0
    ^   ^   ^   ^
 next must be smaller
```

---

## 2.11 Algorithm

```text
1. Count frequency of each candy type.
2. Store positive frequencies.
3. Sort descending.
4. prev = very large.
5. For each f:
      take = min(f, prev-1)
      if take <= 0:
          stop
      answer += take
      prev = take
```

---

## 2.12 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

void solveCandyBox() {
    int n;
    cin >> n;

    vector<int> count(n + 1, 0);

    for (int i = 0; i < n; ++i) {
        int x;
        cin >> x;
        ++count[x];
    }

    vector<int> freq;

    for (int x : count) {
        if (x > 0)
            freq.push_back(x);
    }

    sort(freq.rbegin(), freq.rend());

    long long answer = 0;
    int prev = INT_MAX;

    for (int f : freq) {
        int take = min(f, prev - 1);

        if (take <= 0)
            break;

        answer += take;
        prev = take;
    }

    cout << answer << '\n';
}
```

Typical wrapper:

```cpp
int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int T;
    cin >> T;

    while (T--) {
        solveCandyBox();
    }
}
```

---

## 2.13 Complexity

```text
counting:
O(N)

sorting:
O(K log K)

K = number of distinct candy types
K <= N

total:
O(N log N)
```

---

## 2.14 Edge Cases

```text
one type
→ take all
```

```text
equal frequencies:
5,5,5
→ 5,4,3
```

```text
all frequencies = 1
→ only one positive count 1 can be used
```

Once:

```text
take <= 0
```

all later types also contribute zero.

---

## 2.15 Recognition Model

```text
category capacities
+
positive chosen amounts must be distinct
+
maximize total
        |
        v
sort capacities descending
        |
        v
current =
min(
    own capacity,
    previous - 1
)
```

---

## 2.16 Don't-Memorize Model

Do not memorize:

```text
sort + min(freq, prev-1)
```

Remember:

```text
current has exactly two ceilings:

1. how many candies exist
2. previous chosen amount - 1

take the largest integer
satisfying both
```

---


# 3. Problem 2 — Increasing Subsequence

**Platform:** Codeforces  
**Problem:** 1157C2 — Increasing Subsequence  
**Link:** https://codeforces.com/problemset/problem/1157/C2

---

## 3.1 What the Problem Asks

Given:

```text
a[0 ... n-1]
```

At each move, take either:

```text
leftmost element
or
rightmost element
```

Write that value into a new sequence.

The written sequence must be:

```text
strictly increasing
```

Goal:

```text
maximize number of moves
```

Also output the move string:

```text
L or R
```

---

## 3.2 State Variables

Maintain:

```text
L
R
last
answer
```

where:

```text
L    = current left endpoint
R    = current right endpoint
last = previously chosen value
```

A current endpoint is legal only if:

```math
candidate>last
```

---

## 3.3 Tiny Example

Array:

```text
[1,2,4,3,2]
```

Start:

```text
left  = 1
right = 2
last  = 0
```

Both valid.

Choose smaller:

```text
1 from left
```

Now:

```text
last = 1
```

Remaining:

```text
[2,4,3,2]
```

Ends:

```text
2 and 2
```

Tie.

Look from left:

```text
2 < 4
```

then:

```text
4 < 3
```

fails.

Left run:

```text
2,4
length = 2
```

Look from right:

```text
2 < 3 < 4
```

Right run:

```text
2,3,4
length = 3
```

So take:

```text
R,R,R
```

Final moves:

```text
L R R R
```

Written values:

```text
1,2,3,4
```

Length:

```text
4
```

---

## 3.4 Forced Case — Only One End Is Valid

Suppose:

```text
last = 5
left = 3
right = 8
```

Check:

```text
3 > 5 ? NO
8 > 5 ? YES
```

Only right is legal.

So:

```text
must take R
```

No greedy proof is needed beyond feasibility.

---

## 3.5 Both Ends Valid and Unequal

Suppose:

```text
last = 2
left = 3
right = 7
```

Both are valid.

Greedy:

```text
choose 3
```

Why?

Choosing `3` gives:

```text
new last = 3
```

Choosing `7` gives:

```text
new last = 7
```

Future values must satisfy:

```text
next > last
```

So after `3`:

```text
4,5,6,7,8,...
```

may work.

After `7`:

```text
8,9,10,...
```

may work.

The smaller chosen value keeps more possibilities alive.

---

## 3.6 Proof With Numbers First

Let:

```text
last = 2
x = 3
y = 7
```

with:

```text
x < y
```

Suppose some future value `z` is legal after choosing `y`.

Then:

```text
z > 7
```

Take:

```text
z = 10
```

So:

```text
10 > 7
```

Since:

```text
7 > 3
```

we also have:

```text
10 > 3
```

So any future value that works after choosing `7` also works after choosing `3`.

Therefore choosing `3` loses nothing that choosing `7` would preserve.

It may preserve extra options such as:

```text
4,5,6,7
```

---

## 3.7 General Dominance Proof

Assume:

```math
last<x<y
```

Greedy chooses:

```text
x
```

Alternative chooses:

```text
y
```

Take any future value `z` that is legal after `y`.

Then:

```math
z>y
```

Because:

```math
y>x
```

transitivity gives:

```math
z>x
```

Therefore:

```text
every future value legal after y
is also legal after x
```

So future feasible values after `x` form a superset of those after `y`.

Thus:

```text
choose the smaller valid unequal endpoint
```

---

## 3.8 Why the Tie Case Breaks the Normal Rule

Suppose:

```text
left = right = 3
```

and:

```text
3 > last
```

If choose left `3`:

```text
last = 3
```

The other endpoint remains:

```text
3
```

but it is now invalid:

```text
3 > 3 ? NO
```

The same happens if we choose right first.

So:

```text
the choice of SIDE matters
even though the immediate VALUE is identical
```

This is why C2 needs extra logic.

---

## 3.9 Key Tie Observation

Once equal valid endpoints occur:

```text
a[L] = a[R] = x
```

after choosing one `x`, the other `x` can never be chosen.

Since that other equal value stays at its side, you can no longer pass through it.

Therefore the remaining valid sequence must continue only from the side you chose.

So compare:

```text
strictly increasing run from left
vs
strictly increasing run from right
```

Choose the longer one.

Then stop.

---

## 3.10 Left Lookahead — Inline Example

Current segment:

```text
[3,4,8,7,6,5,3]
```

Ends:

```text
3 and 3
```

Scan from left:

```text
3 < 4
```

continue.

```text
4 < 8
```

continue.

```text
8 < 7
```

false.

So left run:

```text
3,4,8
```

Length:

```text
3
```

Thus if choosing left, we can append:

```text
LLL
```

---

## 3.11 Right Lookahead — Inline Example

Same segment:

```text
[3,4,8,7,6,5,3]
```

Scan from right inward.

First:

```text
3
```

Next inward:

```text
5
```

Check:

```text
5 > 3
```

yes.

Next:

```text
6 > 5
```

yes.

Next:

```text
7 > 6
```

yes.

Next:

```text
8 > 7
```

yes.

Next:

```text
4 > 8
```

false.

So right run:

```text
3,5,6,7,8
```

Length:

```text
5
```

Choose:

```text
RRRRR
```

---

## 3.12 Why Lookahead Is Correct

At tie:

```text
a[L] = a[R] = x
```

Suppose we choose left.

Then:

```text
last = x
```

Right endpoint is still `x`, so right side can never be chosen again.

Therefore the only possible continuation is:

```text
keep taking from left
while values strictly increase
```

So the maximum remaining number of moves after choosing left is exactly:

```text
leftRun
```

Similarly, after choosing right:

```text
maximum remaining moves = rightRun
```

Thus the optimal tie decision is:

```text
max(leftRun, rightRun)
```

---

## 3.13 Full Dry Run

Array:

```text
[1,3,5,6,5,4,2]
```

Start:

```text
L=0
R=6
last=0
```

### Step 1

Ends:

```text
1,2
```

Both valid.

Smaller:

```text
1
```

Take:

```text
L
```

Now:

```text
last=1
```

---

### Step 2

Ends:

```text
3,2
```

Both valid.

Smaller:

```text
2
```

Take:

```text
R
```

Now:

```text
last=2
```

---

### Step 3

Ends:

```text
3,4
```

Take smaller:

```text
3 → L
```

Now:

```text
last=3
```

---

### Step 4

Ends:

```text
5,4
```

Take smaller:

```text
4 → R
```

Now:

```text
last=4
```

---

### Step 5

Ends:

```text
5,5
```

Equal and valid.

Left run:

```text
5 < 6
```

Length:

```text
2
```

Right run:

```text
5 < 6
```

Length:

```text
2
```

Either side.

Choose right:

```text
RR
```

Final moves:

```text
L R L R R R
```

Values:

```text
1,2,3,4,5,6
```

Length:

```text
6
```

---

## 3.14 Decision Table

| Left valid? | Right valid? | Decision |
|---|---|---|
| No | No | Stop |
| Yes | No | `L` |
| No | Yes | `R` |
| Yes | Yes, unequal | Take smaller value |
| Yes | Yes, equal | Compare left/right increasing runs; take longer; stop |

---

## 3.15 Visual Model

```text
              [ L .............. R ]
                 \              /
                  current choices

which endpoints are > last?
          |
      +---+---+
      |       |
    one      both
      |       |
   forced   unequal?
              |
          +---+---+
          |       |
         yes     no
          |       |
      smaller    equal
        end       |
                  v
             look ahead
             both sides
```

---

## 3.16 Algorithm

```text
L = 0
R = n-1
last = 0

while L <= R:

    leftOk  = a[L] > last
    rightOk = a[R] > last

    if neither:
        stop

    if only left:
        take L

    else if only right:
        take R

    else:
        if a[L] < a[R]:
            take L

        else if a[R] < a[L]:
            take R

        else:
            count increasing run from left
            count increasing run from right

            append moves from longer side

            stop
```

---

## 3.17 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<int> a(n);

    for (int& x : a)
        cin >> x;

    int L = 0;
    int R = n - 1;
    int last = 0;

    string ans;

    while (L <= R) {
        bool leftOk = a[L] > last;
        bool rightOk = a[R] > last;

        if (!leftOk && !rightOk)
            break;

        if (leftOk && !rightOk) {
            last = a[L];
            ans.push_back('L');
            ++L;
        }
        else if (!leftOk && rightOk) {
            last = a[R];
            ans.push_back('R');
            --R;
        }
        else {
            if (a[L] < a[R]) {
                last = a[L];
                ans.push_back('L');
                ++L;
            }
            else if (a[R] < a[L]) {
                last = a[R];
                ans.push_back('R');
                --R;
            }
            else {
                int leftRun = 1;

                for (int i = L + 1;
                     i <= R && a[i] > a[i - 1];
                     ++i) {
                    ++leftRun;
                }

                int rightRun = 1;

                for (int i = R - 1;
                     i >= L && a[i] > a[i + 1];
                     --i) {
                    ++rightRun;
                }

                if (leftRun >= rightRun)
                    ans.append(leftRun, 'L');
                else
                    ans.append(rightRun, 'R');

                break;
            }
        }
    }

    cout << ans.size() << '\n';
    cout << ans << '\n';
}
```

---

## 3.18 Complexity

Normal pointer movement:

```text
O(N)
```

Tie lookahead happens once.

It scans at most the remaining segment once from each direction.

Total remains:

```text
O(N)
```

Space excluding output:

```text
O(1)
```

---

## 3.19 Edge Cases

Neither endpoint valid:

```text
stop
```

Only one endpoint valid:

```text
forced choice
```

Equal endpoints but:

```text
value <= last
```

then both are invalid.

Single remaining element:

```text
take only if > last
```

Equal valid endpoints:

```text
lookahead
then finish
```

---

## 3.20 Recognition Model

```text
choose from two ends
+
build strictly increasing sequence
+
maximize length
        |
        v
two pointers
        |
        v
smaller valid endpoint preserves flexibility
        |
        v
equal endpoints?
        |
        v
compare one-sided increasing runs
```

---

## 3.21 Don't-Memorize Model

Do not memorize the nested `if`s.

Remember:

```text
Future condition:
next > last

So:
smaller valid last
is more flexible

But if endpoints are equal:
choosing one makes the other impossible

Then:
future is trapped on one side

So compare:
left run vs right run
```

---


# 4. Problem 3 — Triangle Coloring

**Platform:** Codeforces  
**Problem:** 1795D — Triangle Coloring  
**Link:** https://codeforces.com/problemset/problem/1795/D

**Modulus:**

```text
998244353
```

---

## 4.1 What the Problem Asks

There are:

```text
n vertices
n edges
```

with:

```text
n divisible by 6
```

The graph consists of:

```text
n/3 independent triangles
```

Each triangle has:

```text
3 vertices
3 weighted edges
```

Color every vertex:

```text
red
or
blue
```

such that exactly:

```text
n/2 vertices are red
n/2 vertices are blue
```

The weight of a coloring is:

```text
sum of edge weights
whose endpoints have different colors
```

Let:

```text
W = maximum possible coloring weight
```

We need:

```text
number of valid colorings
with weight exactly W
```

modulo:

```text
998244353
```

---

## 4.2 First Simplification — Independent Triangles

There are no edges between different triangles.

So total coloring weight is:

```math
total
=
contribution_1
+
contribution_2
+\cdots
+
contribution_T
```

where:

```math
T=\frac{n}{3}
```

Therefore:

```text
maximize each triangle locally
```

as long as we can still satisfy the global red/blue count.

We will prove that we can.

---

## 4.3 Possible Color Counts Inside One Triangle

A triangle has 3 vertices.

Possible red counts:

```text
0 red + 3 blue
1 red + 2 blue
2 red + 1 blue
3 red + 0 blue
```

If all 3 vertices same color:

```text
0 crossing edges
```

Contribution:

```text
0
```

If colors are split `2–1`:

```text
exactly 2 edges cross colors
```

So local contribution is:

```text
sum of exactly 2 edge weights
```

Because all edge weights are positive:

```text
2–1 split is always better than monochromatic
```

---

## 4.4 Inline Example — Why Exactly Two Edges Count?

Triangle vertices:

```text
A, B, C
```

Suppose:

```text
A = red
B = red
C = blue
```

Edges:

```text
A-B → same color → not counted
A-C → different   → counted
B-C → different   → counted
```

So exactly:

```text
2 crossing edges
```

If colors are:

```text
A = red
B = blue
C = blue
```

again exactly two crossing edges:

```text
A-B
A-C
```

---

## 4.5 Local Greedy Choice

Triangle edge weights:

```text
w1, w2, w3
```

Since exactly two edges will be counted, possible contributions are:

```text
w1+w2
w1+w3
w2+w3
```

Therefore local maximum:

```math
M
=
\max(
w_1+w_2,
w_1+w_3,
w_2+w_3
)
```

So:

```text
choose the two largest edge weights
```

---

## 4.6 Inline Example — Weights 1, 3, 5

Possible sums:

```text
1+3 = 4
1+5 = 6
3+5 = 8
```

Maximum:

```text
8
```

So optimal coloring must make edges:

```text
3 and 5
```

cross colors.

The edge:

```text
1
```

must connect same-colored vertices.

---

## 4.7 Equivalent View — Omit the Minimum Edge

Total triangle edge sum:

```math
S=w_1+w_2+w_3
```

In a 2–1 coloring, exactly one edge joins same-colored vertices.

That edge is omitted.

So contribution:

```math
S-\text{omittedEdge}
```

To maximize contribution:

```text
minimize omittedEdge
```

Therefore:

```text
omit one minimum-weight edge
```

So the number of optimal local structures equals:

```text
number of minimum-weight edges
```

---

## 4.8 Local Ways — Case 1: Unique Minimum

Weights:

```text
1,3,5
```

Minimum:

```text
1
```

Only one optimal omitted edge.

Therefore:

```text
localWays = 1
```

Pair sums confirm:

```text
1+3 = 4
1+5 = 6
3+5 = 8 ← unique maximum
```

---

## 4.9 Local Ways — Case 2: Two Minimum Edges Equal

Weights:

```text
1,1,5
```

Total:

```text
7
```

Omit first `1`:

```text
7-1 = 6
```

Omit second `1`:

```text
7-1 = 6
```

Omit `5`:

```text
7-5 = 2
```

So:

```text
localWays = 2
```

Pair sums:

```text
1+1 = 2
1+5 = 6
1+5 = 6
```

Maximum pair sum appears:

```text
2 times
```

---

## 4.10 Local Ways — Case 3: All Equal

Weights:

```text
4,4,4
```

Every pair:

```text
4+4 = 8
```

All 3 choices are optimal.

Therefore:

```text
localWays = 3
```

---

## 4.11 Why `localWays` Is the Number of Singleton Choices

In a `2–1` coloring:

```text
two same-colored vertices
one singleton-color vertex
```

The edge between the two same-colored vertices is the omitted edge.

So once we choose which edge is omitted, the singleton vertex is determined.

For a fixed orientation such as:

```text
2 red + 1 blue
```

choosing an optimal omitted edge uniquely determines:

```text
which vertex is blue
```

Therefore:

```text
localWays
=
number of optimal omitted edges
=
number of maximum pair sums
```

---

## 4.12 Multiply Independent Local Choices

Suppose:

```text
triangle 1 → 2 optimal choices
triangle 2 → 3 optimal choices
triangle 3 → 1 optimal choice
```

Independent choices:

```text
2 × 3 × 1
= 6
```

Therefore:

```math
localProduct
=
\prod_{i=1}^{T}localWays_i
```

---

## 4.13 Global Color Balance

Now we must enforce:

```text
exactly n/2 red
exactly n/2 blue
```

Every locally optimal triangle is either:

```text
2 red + 1 blue
```

or:

```text
1 red + 2 blue
```

Let:

```math
T=\frac{n}{3}
```

be number of triangles.

Let:

```text
x triangles
```

use:

```text
2 red + 1 blue
```

Then:

```text
T-x triangles
```

use:

```text
1 red + 2 blue
```

---

## 4.14 Derive the Number of 2R1B Triangles

Red count:

```math
red
=
2x+1(T-x)
```

Expand:

```math
red
=
2x+T-x
```

Combine:

```math
red
=
T+x
```

We need:

```math
red=\frac{n}{2}
```

and:

```math
T=\frac{n}{3}
```

So:

```math
\frac{n}{3}+x
=
\frac{n}{2}
```

Move `n/3`:

```math
x
=
\frac{n}{2}-\frac{n}{3}
```

Common denominator `6`:

```math
x
=
\frac{3n-2n}{6}
```

Therefore:

```math
x=\frac{n}{6}
```

So exactly:

```text
n/6 triangles
```

must be:

```text
2 red + 1 blue
```

and exactly:

```text
n/6 triangles
```

must be:

```text
1 red + 2 blue
```

---

## 4.15 Same Derivation With Numbers

Let:

```text
n = 12
```

Then:

```text
T = n/3
  = 12/3
  = 4 triangles
```

Suppose:

```text
x triangles are 2R1B
```

Red vertices:

```text
2x + (4-x)
```

Need:

```text
n/2
= 12/2
= 6
```

So:

```text
2x + 4 - x
= 6
```

Therefore:

```text
x + 4
= 6
```

So:

```text
x = 2
```

Also:

```text
n/6
= 12/6
= 2
```

Matches.

---

## 4.16 Global Number of Orientation Choices

There are:

```text
n/3 triangles
```

Choose exactly:

```text
n/6
```

to be:

```text
2 red + 1 blue
```

Therefore:

```math
globalWays
=
C\left(\frac{n}{3},\frac{n}{6}\right)
```

Actual example for:

```text
n = 12
```

```text
choose 2 of 4 triangles

C(4,2)
= 6
```

---

## 4.17 Final Formula

Local choices:

```math
localProduct
=
\prod_i localWays_i
```

Global orientation choices:

```math
globalWays
=
C\left(\frac{n}{3},\frac{n}{6}\right)
```

Final answer:

```math
answer
=
localProduct
\cdot
globalWays
```

taken modulo:

```text
998244353
```

---

## 4.18 Full Example

Let:

```text
n = 6
```

So:

```text
T = 2 triangles
```

Triangle 1:

```text
weights = [1,1,5]
```

Pair sums:

```text
1+1 = 2
1+5 = 6
1+5 = 6
```

So:

```text
localWays1 = 2
```

Triangle 2:

```text
weights = [2,3,4]
```

Pair sums:

```text
2+3 = 5
2+4 = 6
3+4 = 7
```

So:

```text
localWays2 = 1
```

Local product:

```text
2×1
= 2
```

Global balance:

```text
n/3 = 2 triangles
n/6 = 1
```

Choose one triangle as:

```text
2R1B
```

Ways:

```text
C(2,1)
= 2
```

Final:

```text
answer
= 2 × 2
= 4
```

---

## 4.19 Visual Model

One triangle:

```text
          A
         / \
      w3/   \w1
       /     \
      B---w2--C
```

A `2–1` coloring:

```text
counts exactly 2 edges
```

So:

```text
maximize counted sum
=
choose 2 largest edges
```

Equivalent:

```text
omit one minimum edge
```

---

## 4.20 Global Visual Model

```text
T = n/3 triangles

each optimal triangle is:

2R1B
or
1R2B
```

Need:

```text
equal red and blue totals
```

Therefore:

```text
half the triangles use each orientation
```

Since:

```text
T/2
=
(n/3)/2
=
n/6
```

number of orientation choices:

```text
C(n/3, n/6)
```

---

## 4.21 Modular Combination Helper

```cpp
const long long MOD = 998244353;

long long modPow(
    long long a,
    long long e
) {
    long long result = 1;

    while (e > 0) {
        if (e & 1)
            result = result * a % MOD;

        a = a * a % MOD;
        e >>= 1;
    }

    return result;
}
```

Factorials:

```cpp
vector<long long> fact, invFact;

void buildFactorials(int n) {
    fact.assign(n + 1, 1);
    invFact.assign(n + 1, 1);

    for (int i = 1; i <= n; ++i) {
        fact[i] = fact[i - 1] * i % MOD;
    }

    invFact[n] =
        modPow(fact[n], MOD - 2);

    for (int i = n; i >= 1; --i) {
        invFact[i - 1] =
            invFact[i] * i % MOD;
    }
}

long long nCr(int n, int r) {
    if (r < 0 || r > n)
        return 0;

    return fact[n]
         * invFact[r] % MOD
         * invFact[n - r] % MOD;
}
```

---

## 4.22 Complete C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

const long long MOD = 998244353;

long long modPow(
    long long a,
    long long e
) {
    long long result = 1;

    while (e > 0) {
        if (e & 1)
            result = result * a % MOD;

        a = a * a % MOD;
        e >>= 1;
    }

    return result;
}

vector<long long> fact, invFact;

void buildFactorials(int n) {
    fact.assign(n + 1, 1);
    invFact.assign(n + 1, 1);

    for (int i = 1; i <= n; ++i) {
        fact[i] = fact[i - 1] * i % MOD;
    }

    invFact[n] = modPow(fact[n], MOD - 2);

    for (int i = n; i >= 1; --i) {
        invFact[i - 1] =
            invFact[i] * i % MOD;
    }
}

long long nCr(int n, int r) {
    if (r < 0 || r > n)
        return 0;

    return fact[n]
         * invFact[r] % MOD
         * invFact[n - r] % MOD;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<long long> w(n);

    for (long long& x : w)
        cin >> x;

    buildFactorials(n);

    long long localProduct = 1;

    for (int i = 0; i < n; i += 3) {
        long long a = w[i];
        long long b = w[i + 1];
        long long c = w[i + 2];

        long long s1 = a + b;
        long long s2 = a + c;
        long long s3 = b + c;

        long long best = max({s1, s2, s3});

        int localWays = 0;

        if (s1 == best)
            ++localWays;

        if (s2 == best)
            ++localWays;

        if (s3 == best)
            ++localWays;

        localProduct =
            localProduct * localWays % MOD;
    }

    long long globalWays =
        nCr(n / 3, n / 6);

    long long answer =
        localProduct * globalWays % MOD;

    cout << answer << '\n';
}
```

---

## 4.23 Complexity

Triangle processing:

```text
O(N)
```

Factorial preprocessing:

```text
O(N)
```

Combination after preprocessing:

```text
O(1)
```

Total:

```text
O(N)
```

Space:

```text
O(N)
```

---

## 4.24 Edge Cases

Unique minimum edge:

```text
localWays = 1
```

Two equal minimum edges:

```text
localWays = 2
```

All three equal:

```text
localWays = 3
```

Example:

```text
1,5,5
```

Minimum is uniquely `1`.

Pair sums:

```text
1+5 = 6
1+5 = 6
5+5 = 10
```

Only one maximum pair.

So:

```text
localWays = 1
```

---

## 4.25 Recognition Model

```text
independent small graph components
+
maximize sum of local contributions
+
count maximum configurations
+
global red/blue balance
        |
        v
maximize each triangle locally
        |
        v
count local optimal structures
        |
        v
multiply
        |
        v
apply one global combination
```

---

## 4.26 Don't-Memorize Model

Do not memorize:

```text
count max pair sums
multiply
then C(n/3,n/6)
```

Remember:

```text
one triangle:

2–1 coloring
→ exactly 2 crossing edges

maximize:
choose 2 largest edge weights

equivalent:
omit a minimum edge

localWays:
number of minimum edges

global:
every triangle is either
2R1B or 1R2B

equal red/blue totals force:
n/6 triangles of each orientation

choose which ones:
C(n/3,n/6)
```

---

# 5. Pattern Comparison

| Problem | Core Signal | Pattern | Proof Style |
|---|---|---|---|
| Candy Box | category frequencies + distinct selected counts | largest legal decreasing count | dominance / upper-bound |
| Increasing Subsequence | choose from two ends + increasing sequence | smaller valid endpoint | future-option dominance + tie lookahead |
| Triangle Coloring | independent triangles + global balance | local optimum + counting | local contribution + multiplication + combinations |

---

## 5.1 Candy Box Pattern

```text
frequency capacities
       |
       v
sort descending
       |
       v
current has 2 ceilings:
own capacity
previous - 1
       |
       v
take largest legal
```

---

## 5.2 Increasing Subsequence Pattern

```text
left / right endpoints
        |
        v
which are > last?
        |
    +---+---+
    |       |
   one     both
    |       |
 forced   unequal?
            |
        +---+---+
        |       |
       yes     equal
        |       |
 smaller     compare
 endpoint    directional runs
```

---

## 5.3 Triangle Coloring Pattern

```text
triangle
   |
   v
2–1 coloring
   |
   v
2 crossing edges
   |
   v
choose 2 largest
   |
   v
count local optimal choices
   |
   v
multiply across triangles
   |
   v
choose n/6 triangle orientations
```

---

# 6. Final Recognition Checklist

```text
1. Can I compress values into frequencies?

2. Do selected category counts need to be distinct?

3. After sorting, is "largest legal now"
   also best for the future?

4. Am I selecting only from two ends?

5. Does future feasibility depend monotonically
   on the last chosen value?

6. If two current choices are equal,
   does my normal comparison lose information?

7. Can I resolve the tie by one-time lookahead?

8. Is the graph split into independent components?

9. Can I maximize every component locally?

10. How many local optimal configurations exist?

11. Is there a global balance constraint
    after local optimization?

12. Does final counting become:
    product of local ways
    ×
    combination?
```

---

# 7. Compact Revision Card

```text
GREEDY PROBLEM SOLVING 3
========================


1. CANDY BOX
============
count type frequencies

sort descending

g1 = f1

g[i]
=
min(
    f[i],
    g[i-1]-1
)

stop when <= 0

why?
two upper bounds:
availability
+
distinctness


2. INCREASING SUBSEQUENCE
=========================
state:
L, R, last

legal:
a[L] > last
a[R] > last

one legal:
forced

both valid, unequal:
take smaller endpoint

why?
smaller last
preserves more future values

equal endpoints:
normal greedy cannot decide

count:
left increasing run
right increasing run

take longer direction
and stop


3. TRIANGLE COLORING
====================
one triangle:

monochromatic
→ contribution 0

2–1 coloring
→ exactly 2 crossing edges

maximize:
choose 2 largest edge weights
=
omit minimum edge

localWays:
number of optimal pair sums
=
number of minimum edges

multiply localWays

T = n/3 triangles

need:
n/6 triangles as 2R1B

globalWays:
C(n/3,n/6)

answer:
localProduct
×
globalWays
mod 998244353
```

---

# Final Mental Model

```text
              GREEDY PROBLEM SOLVING 3
                         |
          +--------------+--------------+
          |              |              |
      frequencies     two ends      components
          |              |              |
          v              v              v
  decreasing distinct  smaller       local max
       capacities      valid end      + count
          |              |              |
          v              v              v
    largest legal     tie lookahead   global nCr
```

> **Core lesson:** first identify what controls future feasibility — a decreasing capacity, the current `last` value, or a component's local contribution. Once that state is clear, the greedy rule and proof become much easier to derive.
