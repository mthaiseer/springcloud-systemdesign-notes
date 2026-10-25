# Backtracking Problems 4 — Level 2
## Digit Sequence Decoding — Print All + Count All
### TLE-Style Self-Study Notes with Prerequisites, Recursion Trees, Dry Runs, and C++17

> **Lecture scope**
>
> This lecture covers two closely related decoding problems:
>
> 1. **Print all possible decodings of a given digit sequence**
> 2. **Count possible decodings of a given digit sequence**
>
> The lecture also discusses the important edge cases around `0` and the wildcard character `*`.

---

# Table of Contents

1. [Big Picture](#1-big-picture)
2. [Prerequisite 1 — Digit to Character Mapping](#2-prerequisite-1--digit-to-character-mapping)
3. [Prerequisite 2 — One-Digit and Two-Digit Choices](#3-prerequisite-2--one-digit-and-two-digit-choices)
4. [Prerequisite 3 — Why 0 Is Special](#4-prerequisite-3--why-0-is-special)
5. [Prerequisite 4 — Recursive State](#5-prerequisite-4--recursive-state)
6. [Prerequisite 5 — Choice Tree](#6-prerequisite-5--choice-tree)
7. [Problem 1 — Print All Possible Decodings](#7-problem-1--print-all-possible-decodings)
8. [Problem 1 — What the Recursive Function Means](#8-problem-1--what-the-recursive-function-means)
9. [Problem 1 — Choices at Each Index](#9-problem-1--choices-at-each-index)
10. [Problem 1 — Recursion Tree for 123](#10-problem-1--recursion-tree-for-123)
11. [Problem 1 — Step-by-Step Dry Run](#11-problem-1--step-by-step-dry-run)
12. [Problem 1 — C++17](#12-problem-1--c17)
13. [Problem 1 — Correctness](#13-problem-1--correctness)
14. [Problem 1 — Complexity](#14-problem-1--complexity)
15. [Problem 2 — Count All Decodings](#15-problem-2--count-all-decodings)
16. [Problem 2 — Same Tree, Different Output](#16-problem-2--same-tree-different-output)
17. [Problem 2 — Recurrence](#17-problem-2--recurrence)
18. [Problem 2 — Dry Run for 123](#18-problem-2--dry-run-for-123)
19. [Problem 2 — C++17 Recursive Version](#19-problem-2--c17-recursive-version)
20. [Memoization Bridge](#20-memoization-bridge)
21. [Wildcard `*` — Meaning](#21-wildcard--meaning)
22. [Wildcard `*` — Single-Character Contribution](#22-wildcard--single-character-contribution)
23. [Wildcard `*` — Two-Character Contribution](#23-wildcard--two-character-contribution)
24. [Wildcard Cases Table](#24-wildcard-cases-table)
25. [Wildcard Dry Run](#25-wildcard-dry-run)
26. [Wildcard C++17 Counting Template](#26-wildcard-c17-counting-template)
27. [Common Mistakes](#27-common-mistakes)
28. [Recognition Model](#28-recognition-model)
29. [Final Mental Model](#29-final-mental-model)
30. [One-Minute Revision](#30-one-minute-revision)

---

# 1. Big Picture

We are given a string of digits.

Each valid number from `1` to `26` maps to one lowercase letter:

```text
1  -> a
2  -> b
3  -> c
...
26 -> z
```

The main recursive question is:

```text
At the current index,
should I decode:

1 digit

or

2 digits?
```

That gives a binary-style recursion tree.

But unlike ordinary binary recursion, some branches are invalid.

Examples:

```text
0      invalid by itself
27     invalid as one pair
30     invalid as one pair
06     invalid as one pair
```

So the recursion is:

```text
TRY one digit
    if valid

TRY two digits
    if valid
```

---

# 2. Prerequisite 1 — Digit to Character Mapping

The mapping is:

```text
1  -> a
2  -> b
3  -> c
...
9  -> i

10 -> j
11 -> k
12 -> l
...
26 -> z
```

A compact formula:

```text
character = 'a' + value - 1
```

Example:

```text
value = 1
'a' + 0 = 'a'
```

```text
value = 11
'a' + 10 = 'k'
```

```text
value = 26
'a' + 25 = 'z'
```

C++:

```cpp
char decode(int value) {
    return char('a' + value - 1);
}
```

---

# 3. Prerequisite 2 — One-Digit and Two-Digit Choices

Suppose:

```text
digits = "123"
```

At index `0` we may consume:

```text
"1"
```

which becomes:

```text
a
```

or consume:

```text
"12"
```

which becomes:

```text
l
```

So:

```text
"123"

Choice 1:
1 | 23
a | ...

Choice 2:
12 | 3
l  | ...
```

This is the heart of the recursion.

---

# 4. Prerequisite 3 — Why 0 Is Special

`0` does not map to any letter.

There is no:

```text
0 -> ?
```

So:

```text
"0"
```

alone is invalid.

But `0` can appear inside:

```text
10 -> j
20 -> t
```

Examples:

```text
"10"
```

has exactly one decoding:

```text
j
```

Not:

```text
a0
```

Similarly:

```text
"120"
```

can decode as:

```text
1 | 20
a | t
= "at"
```

But:

```text
12 | 0
```

is invalid because `0` cannot stand alone.

---

# 5. Prerequisite 4 — Recursive State

For printing all decodings, use:

```text
solve(index, builtString)
```

Meaning:

```text
Decode the suffix starting at `index`
and append every valid decoding
to `builtString`.
```

Example:

```text
solve(0, "")
```

For:

```text
"123"
```

---

# 6. Prerequisite 5 — Choice Tree

Every recursive node is:

```text
(index, builtString)
```

From a node, we can have up to two children:

```text
take one digit
take two digits
```

ASCII:

```text
                    (idx, current)
                     /           \
              take 1 digit     take 2 digits
                  /                 \
         (idx+1, ...)          (idx+2, ...)
```

Some branches disappear because the chosen number is invalid.

---

# 7. Problem 1 — Print All Possible Decodings

We want to print every valid decoded string.

Example:

```text
digits = "123"
```

Possible partitions:

```text
1 | 2 | 3
1 | 23
12 | 3
```

Decodings:

```text
abc
aw
lc
```

So output:

```text
abc
aw
lc
```

---

# 8. Problem 1 — What the Recursive Function Means

Define:

```text
generate(index, current)
```

Meaning:

```text
Print every valid decoding of digits[index...n-1]
after prefix `current`.
```

Base case:

```text
index == n
```

means:

```text
we successfully consumed the entire digit string
```

Therefore:

```text
current is one complete decoding
```

So print it.

---

# 9. Problem 1 — Choices at Each Index

Suppose:

```text
digits[index] = '1'
```

## Choice 1 — take one digit

Value:

```text
1
```

Letter:

```text
a
```

Next state:

```text
generate(index + 1, current + 'a')
```

---

## Choice 2 — take two digits

Suppose the next digit is:

```text
2
```

Then:

```text
12
```

is valid because:

```text
1 <= 12 <= 26
```

Letter:

```text
l
```

Next state:

```text
generate(index + 2, current + 'l')
```

---

## Validity rules

One digit:

```text
1..9
```

Two digits:

```text
10..26
```

This single rule automatically handles many zero cases.

---

# 10. Problem 1 — Recursion Tree for 123

Input:

```text
"123"
```

Tree:

```text
                              (0, "")
                       /                    \
                    take 1                take 12
                       |                     |
                    (1,"a")               (2,"l")
                  /         \                 |
              take 2      take 23          take 3
                 |           |               |
              (2,"ab")     (3,"aw")        (3,"lc")
                 |             ✓               ✓
              take 3
                 |
              (3,"abc")
                 ✓
```

Leaves:

```text
abc
aw
lc
```

---

# 11. Problem 1 — Step-by-Step Dry Run

Input:

```text
digits = "123"
```

Start:

```text
generate(0, "")
```

---

## Branch 1 — choose one digit `1`

```text
1 -> a
```

State:

```text
generate(1, "a")
```

At index `1`:

```text
digits = "23"
```

### Branch 1.1 — choose `2`

```text
2 -> b
```

State:

```text
generate(2, "ab")
```

At index `2`:

```text
3 -> c
```

State:

```text
generate(3, "abc")
```

Now:

```text
index == n
```

Print:

```text
abc
```

---

### Branch 1.2 — choose `23`

```text
23 -> w
```

State:

```text
generate(3, "aw")
```

Print:

```text
aw
```

---

## Branch 2 — choose `12`

```text
12 -> l
```

State:

```text
generate(2, "l")
```

Take:

```text
3 -> c
```

State:

```text
generate(3, "lc")
```

Print:

```text
lc
```

Final outputs:

```text
abc
aw
lc
```

---

# 12. Problem 1 — C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

char decodeValue(int value) {
    return char('a' + value - 1);
}

void generate(int index, const string& digits, string current) {
    int n = digits.size();

    if (index == n) {
        cout << current << '\n';
        return;
    }

    if (digits[index] == '0') {
        return;
    }

    int oneDigit = digits[index] - '0';

    generate(
        index + 1,
        digits,
        current + decodeValue(oneDigit)
    );

    if (index + 1 < n) {
        int twoDigits = (digits[index] - '0') * 10 + (digits[index + 1] - '0');

        if (twoDigits >= 10 && twoDigits <= 26) {
            generate(
                index + 2,
                digits,
                current + decodeValue(twoDigits)
            );
        }
    }
}

int main() {
    string digits;
    cin >> digits;

    generate(0, digits, "");

    return 0;
}
```

---

## 12.1 Same code in the compact style

```cpp
#include <bits/stdc++.h>
using namespace std;

char decodeValue(int value) {
    return char('a' + value - 1);
}

void generate(int index, const string& digits, string current) {
    int n = digits.size();

    if (index == n) {
        cout << current << '\n';
        return;
    }

    if (digits[index] == '0') {
        return;
    }

    int oneDigit = digits[index] - '0';
    generate(index + 1, digits, current + decodeValue(oneDigit));

    if (index + 1 < n) {
        int twoDigits = (digits[index] - '0') * 10 + (digits[index + 1] - '0');

        if (twoDigits >= 10 && twoDigits <= 26) {
            generate(index + 2, digits, current + decodeValue(twoDigits));
        }
    }
}

int main() {
    string digits;
    cin >> digits;

    generate(0, digits, "");

    return 0;
}
```

Use this second formatting style for revision.

---

# 13. Problem 1 — Correctness

At each index there are only two possible valid token lengths:

```text
1 digit
2 digits
```

The recursion tries both whenever they are valid.

Therefore:

```text
every valid partition of the digit string
corresponds to exactly one root-to-leaf path
```

A branch is rejected only when:

```text
a token is invalid
```

Examples:

```text
0
27
30
```

Therefore every printed string is valid and every valid decoding is printed.

---

# 14. Problem 1 — Complexity

In the worst case, many positions allow both one-digit and two-digit choices.

The recursion behaves similarly to Fibonacci:

```text
T(n) = T(n-1) + T(n-2)
```

So:

```text
Time = O(phi^n)
```

and a loose upper bound is:

```text
O(2^n)
```

Recursion depth:

```text
O(n)
```

If we print all answers, output cost itself may also be exponential.

---

# 15. Problem 2 — Count All Decodings

Now we do not need the actual strings.

We only need:

```text
How many valid decodings exist?
```

The recursion tree stays the same.

Only the return value changes.

Instead of:

```text
print every leaf
```

we do:

```text
each valid leaf contributes 1
```

---

# 16. Problem 2 — Same Tree, Different Output

For:

```text
"123"
```

Tree:

```text
                           "123"
                         /       \
                       1          12
                      /            \
                   "23"            "3"
                  /   \              |
                 2     23            3
                 |      |            |
                 3      ✓            ✓
                 |
                 ✓
```

Valid leaves:

```text
abc
aw
lc
```

Count:

```text
3
```

---

# 17. Problem 2 — Recurrence

Let:

```text
ways(index)
```

mean:

```text
number of valid decodings of suffix digits[index...]
```

Then:

```text
ways(index)
=
ways(index + 1)
+
ways(index + 2)
```

but only include a term if its token is valid.

So:

```text
answer = 0

if one digit is valid:
    answer += ways(index + 1)

if two digits are valid:
    answer += ways(index + 2)
```

Base:

```text
index == n
→ 1
```

Why return `1`?

Because:

```text
one complete valid decoding
has been formed
```

---

# 18. Problem 2 — Dry Run for 123

```text
ways(0)
```

At index `0`:

```text
1 valid
12 valid
```

So:

```text
ways(0)
=
ways(1)
+
ways(2)
```

Now:

```text
ways(1)
```

suffix:

```text
"23"
```

Both:

```text
2
23
```

valid.

Therefore:

```text
ways(1)
=
ways(2)
+
ways(3)
```

At:

```text
ways(2)
```

suffix:

```text
"3"
```

Only one digit.

So:

```text
ways(2)
=
ways(3)
```

And:

```text
ways(3)=1
```

Therefore:

```text
ways(2)=1
```

```text
ways(1)=1+1=2
```

Finally:

```text
ways(0)=2+1=3
```

Answer:

```text
3
```

---

# 19. Problem 2 — C++17 Recursive Version

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countWays(int index, const string& digits) {
    int n = digits.size();

    if (index == n) {
        return 1;
    }

    if (digits[index] == '0') {
        return 0;
    }

    long long answer = 0;

    answer += countWays(index + 1, digits);

    if (index + 1 < n) {
        int value = (digits[index] - '0') * 10 + (digits[index + 1] - '0');

        if (value >= 10 && value <= 26) {
            answer += countWays(index + 2, digits);
        }
    }

    return answer;
}

int main() {
    string digits;
    cin >> digits;

    cout << countWays(0, digits) << '\n';

    return 0;
}
```

---

# 20. Memoization Bridge

The recursive count problem repeats states.

Example:

```text
ways(0)
├── ways(1)
│   ├── ways(2)
│   └── ways(3)
└── ways(2)
```

Notice:

```text
ways(2)
```

appears twice.

So memoize by:

```text
index
```

because the answer depends only on:

```text
the suffix starting at index
```

C++:

```cpp
long long solve(int index, const string& digits, vector<long long>& dp) {
    int n = digits.size();

    if (index == n) {
        return 1;
    }

    if (digits[index] == '0') {
        return 0;
    }

    if (dp[index] != -1) {
        return dp[index];
    }

    long long answer = solve(index + 1, digits, dp);

    if (index + 1 < n) {
        int value = (digits[index] - '0') * 10 + (digits[index + 1] - '0');

        if (value <= 26) {
            answer += solve(index + 2, digits, dp);
        }
    }

    return dp[index] = answer;
}
```

Complexity becomes:

```text
Time  = O(n)
Space = O(n)
```

---

# 21. Wildcard `*` — Meaning

The lecture later introduces:

```text
*
```

as a wildcard digit.

Interpretation:

```text
* can represent any digit from 1 to 9
```

So a single `*` contributes:

```text
9 possibilities
```

Example:

```text
"*"
```

can be:

```text
1,2,3,4,5,6,7,8,9
```

which correspond to:

```text
a,b,c,d,e,f,g,h,i
```

Therefore:

```text
ways("*") = 9
```

---

# 22. Wildcard `*` — Single-Character Contribution

Suppose current character is:

```text
*
```

and the suffix after it has:

```text
nextWays
```

possible decodings.

Since `*` may be any of:

```text
1..9
```

the one-character contribution is:

```text
9 × nextWays
```

So:

```text
answer += 9 * ways(index + 1)
```

---

# 23. Wildcard `*` — Two-Character Contribution

The more interesting part is:

```text
two-character combinations
```

Valid numbers are:

```text
10..26
```

We need to count how many wildcard assignments produce a valid pair.

---

## Case A — `**`

Possible valid pairs:

```text
11..19
21..26
```

Count:

```text
9 + 6 = 15
```

Therefore:

```text
"**"
```

as one two-digit token has:

```text
15 possibilities
```

---

## Case B — `1*`

Can become:

```text
11..19
```

Count:

```text
9
```

---

## Case C — `2*`

Can become:

```text
21..26
```

Count:

```text
6
```

---

## Case D — `*0`

Only:

```text
10
20
```

are valid.

Count:

```text
2
```

---

## Case E — `*7`

Possible tens digits:

```text
1
```

because:

```text
17 valid
27 invalid
```

Count:

```text
1
```

---

## Case F — `*3`

Possible:

```text
13
23
```

Count:

```text
2
```

In general:

```text
*1 .. *6
→ 2 possibilities

*7 .. *9
→ 1 possibility
```

---

# 24. Wildcard Cases Table

| Pair | Number of Valid Two-Digit Interpretations |
|---|---:|
| `**` | 15 |
| `1*` | 9 |
| `2*` | 6 |
| `*0` | 2 |
| `*1` ... `*6` | 2 |
| `*7` ... `*9` | 1 |
| `10` ... `26` | 1 |
| everything else | 0 |

Single-character ways:

| Character | Ways |
|---|---:|
| `1..9` | 1 |
| `0` | 0 |
| `*` | 9 |

---

# 25. Wildcard Dry Run

Consider:

```text
"1*"
```

We count:

```text
single-character path
+
two-character path
```

---

## Single path

First character:

```text
1
```

is one fixed decoding.

Remaining:

```text
*
```

has:

```text
9
```

possibilities.

Contribution:

```text
1 × 9 = 9
```

---

## Pair path

Pair:

```text
1*
```

can be:

```text
11,12,13,14,15,16,17,18,19
```

Count:

```text
9
```

After consuming both characters:

```text
ways(end) = 1
```

Contribution:

```text
9 × 1 = 9
```

Total:

```text
9 + 9 = 18
```

---

## Another useful example — `**`

Single-single contribution:

```text
9 × 9 = 81
```

Two-digit contribution:

```text
15
```

Total:

```text
96
```

---

# 26. Wildcard C++17 Counting Template

The lecture's later wildcard discussion is a counting variant, so we separate:

```text
single-character multiplicity
```

from:

```text
two-character multiplicity
```

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll singleWays(char ch) {
    if (ch == '0') {
        return 0;
    }

    if (ch == '*') {
        return 9;
    }

    return 1;
}

ll pairWays(char first, char second) {
    if (first == '*' && second == '*') {
        return 15;
    }

    if (first == '*') {
        if (second >= '0' && second <= '6') {
            return 2;
        }

        return 1;
    }

    if (second == '*') {
        if (first == '1') {
            return 9;
        }

        if (first == '2') {
            return 6;
        }

        return 0;
    }

    int value = (first - '0') * 10 + (second - '0');

    return value >= 10 && value <= 26 ? 1 : 0;
}

ll countWays(int index, const string& s, vector<ll>& dp) {
    int n = s.size();

    if (index == n) {
        return 1;
    }

    if (dp[index] != -1) {
        return dp[index];
    }

    ll answer = 0;

    answer += singleWays(s[index]) * countWays(index + 1, s, dp);

    if (index + 1 < n) {
        answer += pairWays(s[index], s[index + 1]) * countWays(index + 2, s, dp);
    }

    return dp[index] = answer;
}

int main() {
    string s;
    cin >> s;

    vector<ll> dp(s.size(), -1);

    cout << countWays(0, s, dp) << '\n';

    return 0;
}
```

If the original problem requires a modulo, apply it after every multiplication and addition.

---

# 27. Common Mistakes

## Mistake 1 — allowing `0` alone

Wrong:

```text
0 -> some letter
```

There is no mapping.

Correct:

```text
0 alone contributes 0 ways
```

---

## Mistake 2 — allowing every two-digit number

Wrong:

```text
27
30
99
```

Correct valid range:

```text
10..26
```

---

## Mistake 3 — using `stoi(substr(...))` everywhere

It works for tiny strings, but for recursion it adds unnecessary substring construction.

Prefer:

```cpp
int value = (s[i] - '0') * 10 + (s[i + 1] - '0');
```

---

## Mistake 4 — returning `0` at the end

Wrong:

```cpp
if (index == n) {
    return 0;
}
```

A fully consumed valid string contributes:

```text
1 complete decoding
```

So:

```cpp
if (index == n) {
    return 1;
}
```

---

## Mistake 5 — not memoizing the count version

Printing all answers inherently requires outputting all answers.

But counting does not.

So the count version should usually be optimized with:

```text
memoization / DP
```

---

## Mistake 6 — treating `*` as including zero

In the lecture's wildcard interpretation:

```text
* = 1..9
```

not:

```text
0..9
```

---

## Mistake 7 — assuming `** = 9 × 9` for pair decoding

For a pair:

```text
**
```

we only want values:

```text
10..26
```

The valid pair count is:

```text
15
```

not:

```text
81
```

`81` is the number of independent single-character assignments.

---

# 28. Recognition Model

Recognize this pattern when:

```text
You are consuming a sequence from left to right
and at each step you may take
one item or two items.
```

Typical signs:

```text
partition a string
decode digits
parse tokens
word break style recursion
climbing-style recurrence
```

Mental model:

```text
index
 |
 +-- consume 1 item
 |
 `-- consume 2 items
```

Validity decides which edges actually exist.

---

# 29. Final Mental Model

For ordinary decoding:

```text
solve(index)

if index == n:
    return 1

if s[index] == '0':
    return 0

answer = solve(index + 1)

if two-digit value is in [10,26]:
    answer += solve(index + 2)

return answer
```

For printing:

```text
carry current decoded string
and print at index == n
```

For wildcard counting:

```text
answer
=
singleMultiplicity × solve(index + 1)
+
pairMultiplicity × solve(index + 2)
```

That is the clean generalization.

---

# 30. One-Minute Revision

```text
DIGIT DECODING
==============

Mapping:

1 -> a
2 -> b
...
26 -> z

----------------------------------

STATE

solve(index)

or for printing:

solve(index, currentString)

----------------------------------

CHOICES

1 digit:
1..9

2 digits:
10..26

----------------------------------

ZERO

0 alone = invalid

10 valid
20 valid

----------------------------------

BASE

index == n

Printing:
print current

Counting:
return 1

----------------------------------

COUNT RECURRENCE

ways(i)
=
ways(i+1)
+
ways(i+2)

only when those choices are valid

----------------------------------

RECURSION TREE FOR 123

123
├── 1
│   ├── 2
│   │   └── 3 -> abc
│   └── 23 -> aw
└── 12
    └── 3 -> lc

count = 3

----------------------------------

WILDCARD *

single:
* -> 9 possibilities

pairs:

**  -> 15
1*  -> 9
2*  -> 6
*0  -> 2
*1..*6 -> 2
*7..*9 -> 1

----------------------------------

MASTER PATTERN

answer
=
ways of taking 1 symbol
+
ways of taking 2 symbols

with validity/multiplicity
applied to each branch.
```
