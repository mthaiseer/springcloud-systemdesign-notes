# Greedy Problem Solving — Level 3
## Detailed Self-Study Notes — Preliminaries + Proofs + Dry Runs + C++17

> **Goal:** understand the observation and proof behind each greedy problem instead of memorizing the final code.
>
> **Problem flow used throughout this note:**
>
> ```text
> prerequisites
> → what the problem asks
> → variables / model
> → tiny example first
> → brute-force thought
> → greedy observation
> → greedy claim
> → proof with actual numbers
> → general mathematical proof
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
> **Proof style:** every important symbolic equation is followed immediately by an actual numerical example.
>
> **Math rendering:** display equations use fenced `math` blocks only.

---

# Clickable Table of Contents

- [0. How to Use This Note](#0-how-to-use-this-note)
- [1. Shared Greedy Preliminaries](#1-shared-greedy-preliminaries)
- [2. Problem 1 — Find Original Array From Doubled Array](#2-problem-1--find-original-array-from-doubled-array)
- [3. Problem 2 — Bracket Coloring](#3-problem-2--bracket-coloring)
- [4. Problem 3 — Arranging The Sheep](#4-problem-3--arranging-the-sheep)
- [5. Pattern Comparison](#5-pattern-comparison)
- [6. Final Recognition Checklist](#6-final-recognition-checklist)
- [7. Compact Revision Card](#7-compact-revision-card)

---

# 0. How to Use This Note

Do not start by memorizing code.

For every problem:

```text
1. Read "What It Asks".
2. Understand the tiny example.
3. Predict the greedy observation.
4. Read the proof.
5. Reproduce the proof with numbers.
6. Read the C++ only after the idea is clear.
7. Re-solve later using only the Recognition Model.
```

The three lecture problems train different ideas:

```text
Problem 1 → smallest-first forced pairing
Problem 2 → prefix-balance invariant
Problem 3 → transform positions + median
```

---

# 1. Shared Greedy Preliminaries

## 1.1 Constraint vs Objective

Every optimization problem has:

```text
CONSTRAINT → what must remain valid
OBJECTIVE  → what we maximize/minimize
```

Example — Arranging Sheep:

```text
Constraint:
all sheep must end up consecutive

Objective:
minimum total movement
```

Example — Bracket Coloring:

```text
Constraint:
every color subsequence must be beautiful

Objective:
minimum number of colors
```

---

## 1.2 Greedy Choice

A greedy algorithm commits to a local choice.

Example:

```text
Doubled Array:
process the smallest remaining value first
```

But the choice must be proved **forced or safe**.

---

## 1.3 Greedy vs OPT

Use:

```text
G = greedy answer / greedy local choice
O = competing / optimal choice
```

For maximization:

```math
G\ge O
```

For minimization:

```math
G\le O
```

Sometimes we prove using `G-O`; sometimes an inequality or invariant is clearer.

---

## 1.4 Exchange Argument

```text
OPT uses O
    |
    v
Greedy wants G
    |
    v
replace O → G
    |
    v
still feasible?
    |
   YES
    |
    v
same/better answer?
    |
   YES
    |
    v
G is safe
```

---

## 1.5 Forced Choice

A choice is forced when:

```text
if we do not make this choice now,
no valid solution can exist
```

Example pattern:

```text
smallest remaining x
must be original
and must pair with 2x
```

This is Problem 1.

---

## 1.6 Prefix Invariant

For bracket problems:

```text
'(' → +1
')' → -1
```

Balance:

```text
#open - #close
```

Example:

```text
s = (()())

char:     (  (  )  (  )  )
balance:  1  2  1  2  1  0
```

RBS condition:

```text
every prefix balance >= 0
final balance = 0
```

---

## 1.7 Median / Absolute Distance

For sorted values:

```text
x1 <= x2 <= ... <= xk
```

the median minimizes:

```math
|x_1-m|+|x_2-m|+\cdots+|x_k-m|
```

Example:

```text
values = [1,3,5]

m=1 → 0+2+4 = 6
m=3 → 2+0+2 = 4
m=5 → 4+2+0 = 6
```

Best:

```text
median = 3
```

---

## 1.8 Algebra Rules Used Here

### Remove brackets

```text
a-(b+c)
= a-b-c
```

### Rearrange

```text
p_i-t-i
= (p_i-i)-t
```

### Absolute distance

```text
|a-b|
```

means distance from `a` to `b`.

Example:

```text
|7-3| = 4
```

---

## 1.9 Universal Greedy Proof Checklist

```text
1. What is the objective?
2. What must stay valid?
3. What is the greedy choice?
4. Is it forced / exchangeable / invariant-based?
5. Can I show one tiny example?
6. Can I find a counterexample?
7. What quantity remains invariant?
8. Can I prove it with actual numbers first?
9. Can I generalize the same argument?
```

---

# 2. Problem 1 — Find Original Array From Doubled Array

**Platform:** LeetCode  
**Link:** https://leetcode.com/problems/find-original-array-from-doubled-array/

---

## 2.1 What the Problem Asks

An unknown array:

```text
original = [a1,a2,...,ak]
```

is combined with:

```text
[2a1,2a2,...,2ak]
```

and shuffled.

Given:

```text
changed
```

recover one valid `original`.

If impossible:

```text
return {}
```

---

## 2.2 Tiny Example

Original:

```text
[1,3,4]
```

Doubled:

```text
[2,6,8]
```

Combined:

```text
[1,3,4,2,6,8]
```

Possible shuffled input:

```text
[1,4,3,2,8,6]
```

Need:

```text
[1,3,4]
```

---

## 2.3 Preliminary — Size Must Be Even

If original has `k` elements:

```text
changed has:
k originals + k doubles = 2k
```

So:

```text
changed.size() must be even
```

Odd size:

```text
impossible
```

---

## 2.4 Preliminary — Frequency Map

Example:

```text
changed = [1,2,2,4]
```

Frequency:

```text
1 → 1
2 → 2
4 → 1
```

We need counts because duplicates are allowed.

---

## 2.5 Core Pair Relationship

Every original:

```text
x
```

requires:

```text
2x
```

So:

```text
x ↔ 2x
```

Examples:

```text
1 ↔ 2
3 ↔ 6
4 ↔ 8
```

---

## 2.6 Why Sort?

Example:

```text
changed:
[4,2,8,1,6,3]
```

Sorted:

```text
[1,2,3,4,6,8]
```

Now the smallest remaining value becomes easy to reason about.

---

## 2.7 Greedy Observation

Let:

```text
x = smallest remaining positive value
```

Could `x` be a doubled value?

That would require:

```math
x=2y
```

Therefore:

```math
y=\frac{x}{2}
```

Since `x > 0`:

```math
\frac{x}{2}<x
```

So `y` would be smaller than `x`.

But `x` is the smallest remaining value.

Therefore:

```text
x must be an original value
```

Its partner is forced:

```text
2x
```

---

## 2.8 Actual Example of the Forced Choice

Remaining sorted values:

```text
[3,6,8,16]
```

Smallest:

```text
x = 3
```

So choose:

```text
3 as original
```

Required partner:

```text
2×3 = 6
```

Remove:

```text
3,6
```

Remaining:

```text
[8,16]
```

Then:

```text
8 ↔ 16
```

Recovered:

```text
[3,8]
```

---

## 2.9 Why Large-First Is Ambiguous

Input:

```text
[1,2,2,4]
```

Largest value:

```text
4
```

Could be:

```text
original 4
```

or:

```text
double of 2
```

Ambiguous.

Smallest:

```text
1
```

is forced original.

Then:

```text
1 ↔ 2
```

Remaining:

```text
2 ↔ 4
```

So smallest-first removes ambiguity.

---

## 2.10 Special Case — Zero

For:

```text
x = 0
```

we have:

```text
2x = 0
```

So zeros must be paired with zeros.

Valid:

```text
[0,0]
→ original [0]
```

Invalid:

```text
[0]
```

---

## 2.11 Full Dry Run

Input:

```text
[1,3,4,2,6,8]
```

Sorted:

```text
[1,2,3,4,6,8]
```

Process:

```text
x=1
need 2
exists
answer=[1]

x=2
already consumed
skip

x=3
need 6
exists
answer=[1,3]

x=4
need 8
exists
answer=[1,3,4]
```

---

## 2.12 Failure Example

```text
[1,2,3,4]
```

Pair:

```text
1 ↔ 2
```

Remaining:

```text
3,4
```

For:

```text
x=3
```

need:

```text
6
```

Missing.

Therefore impossible.

---

## 2.13 Visual Model

```text
sorted:

1   2   3   4   6   8
|   |   |       |   |
+---+   +-------+   |
1↔2       3↔6    4↔8
```

Algorithm idea:

```text
smallest remaining x
       |
       v
need one 2x
   /       \
found      missing
 |           |
pair       impossible
```

---

## 2.14 Algorithm

```text
1. If n is odd → return {}.
2. Sort changed.
3. Build frequencies.
4. Scan x from small to large.
5. If freq[x]==0 → skip.
6. Consume one x.
7. Require one 2x.
8. If missing → return {}.
9. Consume 2x.
10. Push x into original.
```

---

## 2.15 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> findOriginalArray(vector<int>& changed) {
    int n = (int)changed.size();

    if (n % 2 != 0)
        return {};

    sort(changed.begin(), changed.end());

    unordered_map<int,int> freq;

    for (int x : changed)
        ++freq[x];

    vector<int> original;
    original.reserve(n / 2);

    for (int x : changed) {
        if (freq[x] == 0)
            continue;

        --freq[x];

        int doubled = 2 * x;

        if (freq[doubled] == 0)
            return {};

        --freq[doubled];
        original.push_back(x);
    }

    return original;
}
```

---

## 2.16 Complexity

```text
sorting: O(N log N)
scan:    O(N)

total:
O(N log N)

space:
O(N)
```

---

## 2.17 Edge Cases

```text
odd size → impossible
missing 2x → impossible
duplicates → frequency map
zeros → need zero pairs
```

---

## 2.18 Recognition Model

```text
each original creates transformed partner
+
shuffled multiset
+
transform preserves order for non-negative values
        |
        v
sort
→ process smallest
→ force partner
→ decrement frequencies
```

---

## 2.19 Don't-Memorize Model

Do not memorize:

```text
sort + hashmap
```

Remember:

```text
smallest remaining positive x
cannot be an unexplained doubled value

therefore:
x is forced original

then:
consume 2x
```

---

# 3. Problem 2 — Bracket Coloring

**Platform:** Codeforces  
**Link:** https://codeforces.com/problemset/problem/1837/D

---

## 3.1 What the Problem Asks

Color every bracket with the minimum number of colors so that every color's subsequence is **beautiful**.

The lecture uses two structures:

```text
RBS  → Regular Bracket Sequence
RRBS → reverse-direction regular bracket sequence
```

---

## 3.2 Preliminary — Balance

Map:

```text
'(' → +1
')' → -1
```

Example:

```text
s = (())

char:     (  (  )  )
balance:  1  2  1  0
```

---

## 3.3 RBS

RBS requires:

```text
final balance = 0
every prefix balance >= 0
```

Example:

```text
(())

balance:
1,2,1,0
```

Never negative.

---

## 3.4 RRBS

RRBS requires:

```text
final balance = 0
every prefix balance <= 0
```

Example:

```text
)(

balance:
-1,0
```

Never positive.

---

## 3.5 Necessary Condition

Overall:

```text
# '(' must equal # ')'
```

Equivalent:

```text
final balance = 0
```

If not:

```text
answer = -1
```

---

## 3.6 One Color Case

If all prefix balances are:

```text
>= 0
```

whole string is RBS.

Answer:

```text
1
```

If all prefix balances are:

```text
<= 0
```

whole string is RRBS.

Answer:

```text
1
```

---

## 3.7 Why Two Colors Can Be Needed

Example:

```text
)(()
```

Balance:

```text
char:      )   (   (   )
balance:  -1   0   1   0
```

Path visits:

```text
negative side
and
positive side
```

So whole string is neither RBS nor RRBS.

One color fails.

---

## 3.8 Tiny Coloring Example

String:

```text
)(()
```

Use colors:

```text
2 2 1 1
```

Color 1 subsequence:

```text
()
```

RBS.

Color 2 subsequence:

```text
)(
```

RRBS.

So:

```text
2 colors work
```

---

## 3.9 Balance-Path Idea

Each character is one balance step.

```text
')' : balance decreases by 1
'(' : balance increases by 1
```

For `)(()`:

```text
0 → -1 → 0 → 1 → 0
```

So:

```text
lower-side excursion → one color
upper-side excursion → another color
```

---

## 3.10 Greedy Coloring Rule

Use:

```text
upper-side steps → color 1
lower-side steps → color 2
```

Implementation:

```text
if '(':
    increase balance
    if new balance > 0:
        color 1
    else:
        color 2

if ')':
    if current balance > 0:
        color 1
    else:
        color 2
    decrease balance
```

---

## 3.11 Why It Works — Actual Example

String:

```text
)(()
```

### First `)`

```text
balance:
0 → -1

lower side
→ color 2
```

### First `(`

```text
balance:
-1 → 0

lower-side excursion returning to zero
→ color 2
```

### Second `(`

```text
balance:
0 → 1

upper side
→ color 1
```

### Final `)`

```text
balance:
1 → 0

upper-side excursion returning to zero
→ color 1
```

Colors:

```text
2 2 1 1
```

---

## 3.12 General Proof Idea

Color 1 receives steps on the non-negative side.

Therefore its local prefix balance never goes below zero.

Its excursions return to zero.

So:

```text
color 1 subsequence is RBS
```

Color 2 receives steps on the non-positive side.

Therefore its local prefix balance never goes above zero.

Its excursions return to zero.

So:

```text
color 2 subsequence is RRBS
```

Thus:

```text
2 colors are sufficient whenever final balance = 0
```

---

## 3.13 Why 1 Color Is Minimal When Possible

If whole sequence is already RBS or RRBS:

```text
1 color works
```

Cannot use fewer than 1.

So minimum:

```text
1
```

If balance path visits both positive and negative sides:

```text
whole sequence is neither RBS nor RRBS
```

So:

```text
1 color is impossible
```

but two-color construction works.

Therefore minimum:

```text
2
```

---

## 3.14 Full Dry Run

```text
s = )(()
```

| i | char | before | after | side | color |
|---:|:---:|---:|---:|---|---:|
| 0 | `)` | 0 | -1 | lower | 2 |
| 1 | `(` | -1 | 0 | lower | 2 |
| 2 | `(` | 0 | 1 | upper | 1 |
| 3 | `)` | 1 | 0 | upper | 1 |

---

## 3.15 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

void solveBracketColoring() {
    int n;
    string s;
    cin >> n >> s;

    int total = 0;

    for (char c : s) {
        total += (c == '(' ? 1 : -1);
    }

    if (total != 0) {
        cout << -1 << '\n';
        return;
    }

    int bal = 0;
    bool nonNegative = true;
    bool nonPositive = true;

    for (char c : s) {
        bal += (c == '(' ? 1 : -1);

        if (bal < 0)
            nonNegative = false;

        if (bal > 0)
            nonPositive = false;
    }

    if (nonNegative || nonPositive) {
        cout << 1 << '\n';

        for (int i = 0; i < n; ++i) {
            cout << 1 << (i + 1 == n ? '\n' : ' ');
        }
        return;
    }

    cout << 2 << '\n';

    vector<int> color(n);
    bal = 0;

    for (int i = 0; i < n; ++i) {
        if (s[i] == '(') {
            ++bal;
            color[i] = (bal > 0 ? 1 : 2);
        } else {
            color[i] = (bal > 0 ? 1 : 2);
            --bal;
        }
    }

    for (int i = 0; i < n; ++i) {
        cout << color[i]
             << (i + 1 == n ? '\n' : ' ');
    }
}
```

---

## 3.16 Complexity

```text
time:  O(N)
space: O(N)
```

---

## 3.17 Recognition Model

```text
brackets
+
prefix validity
+
minimum groups
        |
        v
convert to +1/-1
        |
        v
study balance path
        |
        v
separate positive/negative excursions
```

---

## 3.18 Don't-Memorize Model

Do not memorize the `if` statements.

Remember:

```text
every bracket is a balance step

upper-side excursions form RBS
lower-side excursions form RRBS
```

---

# 4. Problem 3 — Arranging The Sheep

**Platform:** Codeforces  
**Link:** https://codeforces.com/problemset/problem/1520/E

---

## 4.1 What the Problem Asks

String:

```text
'*' → sheep
'.' → empty cell
```

Move sheep left/right so that all sheep occupy consecutive cells.

Minimize:

```text
total movement
```

---

## 4.2 Tiny Example

Sheep positions:

```text
1,4,7
```

Visual:

```text
0 1 2 3 4 5 6 7
  *     *     *
```

Target:

```text
3,4,5
```

Moves:

```text
1 → 3 = 2
4 → 4 = 0
7 → 5 = 2

total = 4
```

---

## 4.3 Preliminary — Absolute Distance

Movement from `a` to `b`:

```math
|a-b|
```

Example:

```text
|1-3|
= 2
```

---

## 4.4 Preliminary — Consecutive Targets

If first final sheep is at:

```text
t
```

then targets are:

```text
t
t+1
t+2
...
```

For sheep `i`:

```text
target = t+i
```

---

## 4.5 Build the Movement Formula

Let original sheep positions be:

```math
p_0,p_1,p_2,\ldots,p_{k-1}
```

Targets:

```text
sheep 0 → t
sheep 1 → t+1
sheep 2 → t+2
...
```

Therefore total cost:

```math
|p_0-t|
+
|p_1-(t+1)|
+
|p_2-(t+2)|
+\cdots
```

Now derive each term with actual numbers.

---

# 4.6 Equation-by-Equation Example

Use:

```text
p0 = 1
p1 = 4
p2 = 7

t = 3
```

Targets:

```text
t   = 3
t+1 = 4
t+2 = 5
```

---

## 4.6.1 First Sheep

Formula:

```math
|p_0-t|
```

Actual:

```text
|1-3|
= 2
```

---

## 4.6.2 Second Sheep

Formula:

```math
|p_1-(t+1)|
```

Actual:

```text
|4-(3+1)|

= |4-4|

= 0
```

Now transform it.

Start:

```math
p_1-(t+1)
```

Actual:

```text
4-(3+1)
```

Remove bracket:

```math
p_1-t-1
```

Actual:

```text
4-3-1
= 0
```

Rearrange:

```math
(p_1-1)-t
```

Actual:

```text
(4-1)-3

= 3-3

= 0
```

Therefore:

```math
|p_1-(t+1)|
=
|(p_1-1)-t|
```

Actual:

```text
|4-(3+1)|

=
|(4-1)-3|

=
|3-3|

=
0
```

---

## 4.6.3 Third Sheep

Formula:

```math
|p_2-(t+2)|
```

Actual:

```text
|7-(3+2)|

= |7-5|

= 2
```

Transform:

```math
p_2-(t+2)
=
p_2-t-2
```

Actual:

```text
7-(3+2)

=
7-3-2

=
2
```

Rearrange:

```math
p_2-t-2
=
(p_2-2)-t
```

Actual:

```text
7-3-2

=
(7-2)-3

=
5-3

=
2
```

Therefore:

```math
|p_2-(t+2)|
=
|(p_2-2)-t|
```

Actual:

```text
|7-(3+2)|

=
|(7-2)-3|

=
|5-3|

=
2
```

---

## 4.7 General i-th Sheep

Target:

```text
t+i
```

Movement:

```math
|p_i-(t+i)|
```

Remove bracket:

```math
p_i-(t+i)
=
p_i-t-i
```

Rearrange:

```math
p_i-t-i
=
(p_i-i)-t
```

Therefore:

```math
|p_i-(t+i)|
=
|(p_i-i)-t|
```

This is the key derivation.

---

## 4.8 Define Transformed Positions

Define:

```math
q_i=p_i-i
```

Then:

```math
|p_i-(t+i)|
=
|q_i-t|
```

So total cost becomes:

```math
\sum_i |q_i-t|
```

This is now a standard:

```text
minimum sum of absolute distances
```

problem.

---

## 4.9 Transform the Example

Original:

```text
p = [1,4,7]
```

Transform:

```text
q0 = 1-0 = 1
q1 = 4-1 = 3
q2 = 7-2 = 5
```

So:

```text
q = [1,3,5]
```

Original cost:

```text
|1-t|
+
|4-(t+1)|
+
|7-(t+2)|
```

becomes:

```text
|1-t|
+
|3-t|
+
|5-t|
```

---

## 4.10 Why Median?

Try:

```text
q = [1,3,5]
```

### t=1

```text
|1-1| + |3-1| + |5-1|

= 0+2+4

= 6
```

### t=2

```text
1+1+3
= 5
```

### t=3

```text
2+0+2
= 4
```

### t=4

```text
3+1+1
= 5
```

### t=5

```text
4+2+0
= 6
```

Minimum:

```text
4
```

at:

```text
t = 3
```

which is the median.

---

## 4.11 Median Intuition

Move candidate `t` one step right.

For every point left of `t`:

```text
distance increases by 1
```

For every point right of `t`:

```text
distance decreases by 1
```

Before the median:

```text
more points are on the right
→ moving right helps
```

After the median:

```text
more points are on the left
→ moving right hurts
```

So the turning point is the median.

---

## 4.12 Visual Derivation

Original:

```text
positions:

0 1 2 3 4 5 6 7
  *     *     *
```

Targets:

```text
3 4 5
* * *
```

Mandatory target spacing:

```text
0,1,2
```

Subtract it:

```text
p0-0
p1-1
p2-2

↓
```

```text
1,3,5
```

Now choose one common `t` minimizing:

```text
|1-t| + |3-t| + |5-t|
```

Median:

```text
3
```

---

## 4.13 Full String Dry Run

```text
s = .*..*..*
```

Positions:

```text
0 1 2 3 4 5 6 7
. * . . * . . *
```

Sheep:

```text
p = [1,4,7]
```

Transform:

```text
q = [1,3,5]
```

Median:

```text
3
```

Cost:

```text
|1-3|
+
|3-3|
+
|5-3|

= 2+0+2

= 4
```

---

## 4.14 Another Example

Sheep positions:

```text
[0,3,4,8]
```

Transform:

```text
q0 = 0-0 = 0
q1 = 3-1 = 2
q2 = 4-2 = 2
q3 = 8-3 = 5
```

So:

```text
q = [0,2,2,5]
```

Median:

```text
2
```

Cost:

```text
|0-2|
+
|2-2|
+
|2-2|
+
|5-2|

= 2+0+0+3

= 5
```

---

## 4.15 Algorithm

```text
1. Collect all sheep positions p[i].
2. If <=1 sheep → answer 0.
3. Transform:
      q[i] = p[i]-i
4. Pick:
      median = q[k/2]
5. Answer:
      sum |q[i]-median|
```

---

## 4.16 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

long long arrangeSheep(const string& s) {
    vector<long long> pos;

    for (int i = 0; i < (int)s.size(); ++i) {
        if (s[i] == '*')
            pos.push_back(i);
    }

    int k = (int)pos.size();

    if (k <= 1)
        return 0;

    vector<long long> q(k);

    for (int i = 0; i < k; ++i) {
        q[i] = pos[i] - i;
    }

    long long median = q[k / 2];

    long long ans = 0;

    for (long long x : q) {
        ans += llabs(x - median);
    }

    return ans;
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
        int n;
        string s;

        cin >> n >> s;

        cout << arrangeSheep(s) << '\n';
    }
}
```

---

## 4.17 Complexity

```text
time:
O(N)

space:
O(K)

K = number of sheep
```

---

## 4.18 Edge Cases

No sheep:

```text
....
→ 0
```

One sheep:

```text
..*..
→ 0
```

Already consecutive:

```text
.***.
```

Positions:

```text
1,2,3
```

Transform:

```text
1,1,1
```

Cost:

```text
0
```

---

## 4.19 Recognition Model

```text
points must become consecutive
+
movement = absolute distance
        |
        v
targets are t+i
        |
        v
subtract mandatory i
        |
        v
q[i] = p[i]-i
        |
        v
minimize Σ|q[i]-t|
        |
        v
median
```

---

## 4.20 Don't-Memorize Model

Do not memorize:

```text
pos[i]-i
then median
```

Understand:

```text
target sheep i
= t+i
```

So:

```text
movement
= |p_i-(t+i)|
```

Rearrange:

```text
= |(p_i-i)-t|
```

Therefore:

```text
q_i = p_i-i
```

appears naturally.

Then:

```text
minimum absolute-distance sum
→ median
```

---

# 5. Pattern Comparison

| Problem | Signal | Pattern | Proof Style |
|---|---|---|---|
| Doubled Array | `x ↔ 2x` | smallest-first forced pairing | forced choice |
| Bracket Coloring | bracket prefixes | balance-path partition | invariant |
| Arranging Sheep | consecutive targets + movement | offset removal + median | transformation + median |

---

# 6. Final Recognition Checklist

```text
1. What is being optimized?
2. What are the validity constraints?
3. Is there a smallest/largest forced choice?
4. Does each item require a transformed partner?
5. Does sorting make the choice forced?
6. Is there a prefix quantity like balance?
7. Can I visualize a path?
8. Is cost based on absolute distance?
9. Are targets consecutive: t+i?
10. Can I remove a fixed offset?
11. Does the problem reduce to median?
12. Can I explain the proof with numbers first?
```

---

# 7. Compact Revision Card

```text
1. DOUBLED ARRAY
================
x ↔ 2x

sort ascending

smallest remaining positive x
must be original

pair with 2x

missing 2x → impossible


2. BRACKET COLORING
===================
'(' = +1
')' = -1

RBS:
prefix balance >= 0
final balance = 0

RRBS:
prefix balance <= 0
final balance = 0

whole path one side:
1 color

path both sides:
2 colors

upper excursions → RBS
lower excursions → RRBS


3. ARRANGING SHEEP
==================
positions:
p[i]

targets:
t+i

movement:
|p_i-(t+i)|

rearrange:
|(p_i-i)-t|

define:
q[i] = p[i]-i

minimize:
Σ|q[i]-t|

best t:
median(q)
```

---

# Final Mental Model

```text
                 GREEDY / OPTIMIZATION
                         |
       +-----------------+------------------+
       |                 |                  |
   forced pair       prefix path      absolute distance
       |                 |                  |
       v                 v                  v
smallest-first      bracket balance    remove offsets
       |                 |                  |
       v                 v                  v
 x ↔ 2x           RBS / RRBS          median
```

> **Core lesson:** transform the problem until the greedy choice becomes **forced, invariant-driven, or mathematically optimal**.
