# Recursion Problem Solving — Level 2
## TLE-Style Self-Study Notes with Prerequisites, Recursion Trees, Dry Runs, and C++17

> **Lecture problems covered**
>
> 1. Compute `a^b` recursively.
> 2. Generate the binary representation of an integer.
> 3. Generate all binary strings of length `n` with no adjacent zeroes.
>
> The main goal is not to memorize code. The goal is to learn how to define a recursive function, shrink the problem, understand the call stack, and design a recursion tree.

---

# Table of Contents

1. [How to Think About Recursive Problems](#1-how-to-think-about-recursive-problems)
2. [Prerequisite 1 — Recursive Contract](#2-prerequisite-1--recursive-contract)
3. [Prerequisite 2 — Base Case](#3-prerequisite-2--base-case)
4. [Prerequisite 3 — Recursive Case](#4-prerequisite-3--recursive-case)
5. [Prerequisite 4 — Work Before vs After Recursion](#5-prerequisite-4--work-before-vs-after-recursion)
6. [Prerequisite 5 — Recursion Tree vs Call Stack](#6-prerequisite-5--recursion-tree-vs-call-stack)
7. [Prerequisite 6 — Recurrence and Complexity](#7-prerequisite-6--recurrence-and-complexity)
8. [Problem 1 — Compute `a^b`](#8-problem-1--compute-ab)
9. [Power — Mathematical Observation](#9-power--mathematical-observation)
10. [Power — Recursive Contract](#10-power--recursive-contract)
11. [Power — Base Case](#11-power--base-case)
12. [Power — Recursive Case](#12-power--recursive-case)
13. [Power — Recursion Tree / Call Chain](#13-power--recursion-tree--call-chain)
14. [Power — Detailed Dry Run for `5^3`](#14-power--detailed-dry-run-for-53)
15. [Power — C++17](#15-power--c17)
16. [Power — Complexity](#16-power--complexity)
17. [Self-Study Extension — Fast Power](#17-self-study-extension--fast-power)
18. [Fast Power — Recursion Tree](#18-fast-power--recursion-tree)
19. [Problem 2 — Decimal to Binary Recursively](#19-problem-2--decimal-to-binary-recursively)
20. [Binary Representation Prerequisite](#20-binary-representation-prerequisite)
21. [Why Division by 2 Works](#21-why-division-by-2-works)
22. [Binary — Recursive Contract](#22-binary--recursive-contract)
23. [Binary — Base Case](#23-binary--base-case)
24. [Binary — Recursive Case](#24-binary--recursive-case)
25. [Binary — Why the Recursive Answer Comes First](#25-binary--why-the-recursive-answer-comes-first)
26. [Binary — Recursion Tree / Call Chain](#26-binary--recursion-tree--call-chain)
27. [Binary — Detailed Dry Run for `13`](#27-binary--detailed-dry-run-for-13)
28. [Binary — C++17](#28-binary--c17)
29. [Binary — Complexity](#29-binary--complexity)
30. [Problem 3 — Binary Strings Without Adjacent Zeroes](#30-problem-3--binary-strings-without-adjacent-zeroes)
31. [Generation Prerequisite — Decision Tree](#31-generation-prerequisite--decision-tree)
32. [Brute Force First](#32-brute-force-first)
33. [What Makes a String Invalid?](#33-what-makes-a-string-invalid)
34. [Recursive State](#34-recursive-state)
35. [LCCM View](#35-lccm-view)
36. [Choice and Check](#36-choice-and-check)
37. [Recursion Tree for `n = 3`](#37-recursion-tree-for-n--3)
38. [Pruned Recursion Tree](#38-pruned-recursion-tree)
39. [Detailed Dry Run for `n = 3`](#39-detailed-dry-run-for-n--3)
40. [C++17 — Simple String-State Version](#40-c17--simple-string-state-version)
41. [C++17 — Index + Previous Character Version](#41-c17--index--previous-character-version)
42. [Complexity](#42-complexity)
43. [Why the Count Follows Fibonacci](#43-why-the-count-follows-fibonacci)
44. [Common Mistakes](#44-common-mistakes)
45. [Recognition Patterns](#45-recognition-patterns)
46. [Recursive Design Checklist](#46-recursive-design-checklist)
47. [Final Mental Model](#47-final-mental-model)
48. [One-Minute Revision](#48-one-minute-revision)

---

# 1. How to Think About Recursive Problems

Do not begin with:

```text
"How do I write recursion?"
```

Begin with:

```text
"What does my function mean?"
```

Then answer:

```text
1. State:
   What information describes the current smaller problem?

2. Base case:
   When is the answer immediate?

3. Transition:
   How do I move to a smaller problem?

4. Combination:
   How does the smaller answer help build the current answer?
```

For generation problems, also ask:

```text
5. Choices:
   What options can I try here?

6. Check:
   Which options are legal?

7. Undo:
   Do I need to restore shared state after recursion?
```

---

# 2. Prerequisite 1 — Recursive Contract

A recursive contract is one sentence describing the exact responsibility of the function.

Examples:

```text
power(a, b)
returns a^b.
```

```text
toBinary(n)
returns the binary representation of n.
```

```text
generate(index, current)
generates every valid binary string from this partial state.
```

This sentence is the foundation of the solution.

If the function meaning is unclear, the recursion usually becomes confusing.

---

# 3. Prerequisite 2 — Base Case

A base case is a problem small enough to answer directly.

Example:

```text
a^0 = 1
```

Therefore:

```cpp
if (b == 0) {
    return 1;
}
```

Another example:

```text
binary representation of 0
```

can be handled directly.

Generation example:

```text
if current string already has length n
→ one complete answer has been built.
```

---

# 4. Prerequisite 3 — Recursive Case

The recursive case must make the problem smaller.

For power:

```text
a^b
=
a × a^(b-1)
```

So:

```text
b → b - 1
```

For binary representation:

```text
n → n / 2
```

For string generation:

```text
index → index + 1
```

Every recursive call must move toward its base case.

---

# 5. Prerequisite 4 — Work Before vs After Recursion

This idea is extremely important.

Consider:

```cpp
void f(int n) {
    if (n == 0) {
        return;
    }

    cout << n << ' ';
    f(n - 1);
}
```

Output:

```text
n n-1 n-2 ... 1
```

because printing happens **before** recursion.

Now:

```cpp
void f(int n) {
    if (n == 0) {
        return;
    }

    f(n - 1);
    cout << n << ' ';
}
```

Output:

```text
1 2 3 ... n
```

because printing happens while recursion is **returning**.

This same idea is essential in decimal-to-binary conversion.

---

# 6. Prerequisite 5 — Recursion Tree vs Call Stack

## Recursion tree

Shows:

```text
every recursive call created during the whole execution
```

## Recursion stack

Shows:

```text
only currently active calls
```

For one-child recursion:

```text
f(3)
 |
f(2)
 |
f(1)
 |
f(0)
```

the tree and active call path look almost identical.

For branching recursion they are very different.

---

# 7. Prerequisite 6 — Recurrence and Complexity

Translate recursive code into:

```text
T(current input)
=
recursive cost
+
local work
```

Examples:

```text
one call on n-1:

T(n) = T(n-1) + O(1)
```

```text
one call on n/2:

T(n) = T(n/2) + O(1)
```

```text
two calls on n-1:

T(n) = 2T(n-1) + O(1)
```

This lets us connect the code to the recursion tree.

---

# 8. Problem 1 — Compute `a^b`

## Problem

Given:

```text
a
b
```

calculate:

```text
a^b
```

Lecture example:

```text
a = 5
b = 3
```

Expected:

```text
125
```

because:

```text
5^3
=
5 × 5 × 5
=
125
```

---

# 9. Power — Mathematical Observation

The direct multiplication view is:

```text
a^b
=
a × a × a × ... × a
```

with exactly:

```text
b copies of a
```

Now remove one copy:

```text
a^b
=
a × a^(b-1)
```

That is already a recursive definition.

Example:

```text
5^3
=
5 × 5^2
```

and:

```text
5^2
=
5 × 5^1
```

and:

```text
5^1
=
5 × 5^0
```

Base:

```text
5^0 = 1
```

---

# 10. Power — Recursive Contract

Define:

```text
power(a, b)
returns a^b.
```

Now do not worry about how `power(a, b-1)` works internally.

Trust the contract:

```text
power(a, b-1)
returns a^(b-1)
```

Therefore:

```text
a × power(a, b-1)
=
a × a^(b-1)
=
a^b
```

This is the recursion leap of faith.

---

# 11. Power — Base Case

Mathematical fact:

```text
a^0 = 1
```

Therefore:

```cpp
if (b == 0) {
    return 1;
}
```

---

# 12. Power — Recursive Case

From:

```text
a^b
=
a × a^(b-1)
```

we get:

```cpp
return a * power(a, b - 1);
```

Complete structure:

```text
power(a, b)

if b == 0
    return 1

return a × power(a, b-1)
```

---

# 13. Power — Recursion Tree / Call Chain

For:

```text
power(5, 3)
```

there is only one recursive child per call.

So the recursion tree is a chain:

```text
power(5, 3)
    |
    v
power(5, 2)
    |
    v
power(5, 1)
    |
    v
power(5, 0)
```

Downward direction:

```text
3 → 2 → 1 → 0
```

Return direction:

```text
1
→ 5
→ 25
→ 125
```

---

# 14. Power — Detailed Dry Run for `5^3`

Start:

```text
power(5,3)
```

Expansion:

```text
power(5,3)
= 5 × power(5,2)

power(5,2)
= 5 × power(5,1)

power(5,1)
= 5 × power(5,0)

power(5,0)
= 1
```

Now return upward:

```text
power(5,1)
= 5 × 1
= 5

power(5,2)
= 5 × 5
= 25

power(5,3)
= 5 × 25
= 125
```

Compact call/return view:

```text
Call:   power(5,3) → power(5,2) → power(5,1) → power(5,0)

Return:      125   ←     25     ←      5     ←      1
```

---

# 15. Power — C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

long long power(long long a, int b) {
    if (b == 0) {
        return 1;
    }

    return a * power(a, b - 1);
}

int main() {
    long long a;
    int b;

    cin >> a >> b;

    cout << power(a, b) << '\n';

    return 0;
}
```

---

# 16. Power — Complexity

One recursive call:

```text
b
→ b-1
→ b-2
→ ...
→ 0
```

Number of calls:

```text
b + 1
```

So:

```text
Time = O(b)
```

Maximum recursion depth:

```text
O(b)
```

Therefore:

```text
Auxiliary stack space = O(b)
```

---

## 16.1 Recurrence

```text
T(b)
=
T(b-1)
+
O(1)
```

Expanding:

```text
T(b)
=
T(b-2)
+
O(1)
+
O(1)
```

Eventually:

```text
T(b)
=
T(0)
+
b × O(1)
```

Therefore:

```text
O(b)
```

---

# 17. Self-Study Extension — Fast Power

> This section is an additional optimization for mastery. The lecture first builds the linear recursive version.

We can use:

```text
a^b
```

by splitting the exponent in half.

If `b` is even:

```text
a^b
=
a^(b/2) × a^(b/2)
```

If `b` is odd:

```text
a^b
=
a × a^(b/2) × a^(b/2)
```

But do **not** recursively compute the same half twice.

Store it once:

```cpp
long long fastPower(long long a, long long b) {
    if (b == 0) {
        return 1;
    }

    long long half = fastPower(a, b / 2);
    long long answer = half * half;

    if (b % 2 == 1) {
        answer *= a;
    }

    return answer;
}
```

---

# 18. Fast Power — Recursion Tree

For:

```text
fastPower(a, 13)
```

the exponent becomes:

```text
13
↓
6
↓
3
↓
1
↓
0
```

Each step divides by 2.

Number of levels:

```text
O(log b)
```

Therefore:

```text
Time = O(log b)
Stack = O(log b)
```

Important:

```text
linear power:
b → b-1
O(b)

fast power:
b → b/2
O(log b)
```

---

# 19. Problem 2 — Decimal to Binary Recursively

## Problem

Given an integer `n`, generate its binary representation.

Lecture example:

```text
n = 5
```

Expected:

```text
101
```

---

# 20. Binary Representation Prerequisite

Binary uses only:

```text
0
1
```

Every decimal integer can be written as powers of 2.

Example:

```text
13
=
8 + 4 + 1
=
2^3 + 2^2 + 2^0
```

Therefore:

```text
13 = 1101₂
```

because:

```text
1×8 + 1×4 + 0×2 + 1×1
=
13
```

---

# 21. Why Division by 2 Works

When dividing an integer by 2:

```text
quotient
=
n / 2

remainder
=
n % 2
```

The remainder is always:

```text
0 or 1
```

That remainder is the current least-significant binary digit.

Example:

```text
13 / 2 = 6 remainder 1
 6 / 2 = 3 remainder 0
 3 / 2 = 1 remainder 1
 1 / 2 = 0 remainder 1
```

Remainders appear:

```text
1 0 1 1
```

but from least significant to most significant.

Reverse order:

```text
1101
```

---

# 22. Binary — Recursive Contract

Define:

```text
toBinary(n)
returns the binary representation of n as a string.
```

Suppose:

```text
toBinary(n / 2)
```

already correctly returns the higher-order bits.

Then only one bit remains:

```text
n % 2
```

So:

```text
toBinary(n)
=
toBinary(n/2)
+
lastBit
```

---

# 23. Binary — Base Case

A robust base case is:

```text
n < 2
```

because:

```text
0 → "0"
1 → "1"
```

So:

```cpp
if (n < 2) {
    return string(1, char('0' + n));
}
```

For a positive-only version, another common formulation is to stop at `n == 1`.

---

# 24. Binary — Recursive Case

For:

```text
n >= 2
```

first solve:

```text
n / 2
```

Then append:

```text
n % 2
```

Formula:

```text
binary(n)
=
binary(n/2)
+
char('0' + n%2)
```

Example:

```text
binary(13)
=
binary(6) + "1"
```

```text
binary(6)
=
binary(3) + "0"
```

```text
binary(3)
=
binary(1) + "1"
```

```text
binary(1)
=
"1"
```

So:

```text
binary(3)
=
"1" + "1"
=
"11"
```

```text
binary(6)
=
"11" + "0"
=
"110"
```

```text
binary(13)
=
"110" + "1"
=
"1101"
```

---

# 25. Binary — Why the Recursive Answer Comes First

Suppose we append the current remainder before recursion.

For:

```text
13
```

remainders appear as:

```text
1
0
1
1
```

That is reverse order.

Instead, recurse first:

```text
binary(n/2)
```

and append the remainder while returning.

Call direction:

```text
13 → 6 → 3 → 1
```

Return direction:

```text
1
↓
11
↓
110
↓
1101
```

This is a classic **post-recursion work** pattern.

---

# 26. Binary — Recursion Tree / Call Chain

For:

```text
toBinary(13)
```

```text
toBinary(13)
      |
      v
toBinary(6)
      |
      v
toBinary(3)
      |
      v
toBinary(1)
```

Return values:

```text
toBinary(1)
= "1"

toBinary(3)
= "1" + "1"
= "11"

toBinary(6)
= "11" + "0"
= "110"

toBinary(13)
= "110" + "1"
= "1101"
```

---

# 27. Binary — Detailed Dry Run for `13`

```text
Call 1:
n=13
13/2=6
13%2=1
need binary(6) first
```

```text
Call 2:
n=6
6/2=3
6%2=0
need binary(3) first
```

```text
Call 3:
n=3
3/2=1
3%2=1
need binary(1) first
```

```text
Call 4:
n=1
base case
return "1"
```

Now unwind:

```text
binary(3)
= "1" + "1"
= "11"
```

```text
binary(6)
= "11" + "0"
= "110"
```

```text
binary(13)
= "110" + "1"
= "1101"
```

Answer:

```text
1101
```

---

# 28. Binary — C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

string toBinary(int n) {
    if (n < 2) {
        return string(1, char('0' + n));
    }

    string answer = toBinary(n / 2);
    answer += char('0' + (n % 2));

    return answer;
}

int main() {
    int n;
    cin >> n;

    cout << toBinary(n) << '\n';

    return 0;
}
```

---

# 29. Binary — Complexity

Each recursive call halves `n`:

```text
n
→ n/2
→ n/4
→ n/8
→ ...
→ 1
```

Number of calls:

```text
O(log n)
```

Ignoring string-copy cost for the moment:

```text
recursion depth = O(log n)
```

and there are:

```text
O(log n)
```

binary digits.

For interview/CP analysis, if building the answer with efficient append semantics or a buffer:

```text
Time = O(log n)
Space = O(log n)
```

---

## 29.1 Important implementation note

Repeated immutable-style string concatenation can introduce extra copying in some languages/implementations.

In C++, an even cleaner complexity-controlled approach is:

```text
recurse first
push one character into an output string
```

Example:

```cpp
void buildBinary(int n, string& answer) {
    if (n < 2) {
        answer.push_back(char('0' + n));
        return;
    }

    buildBinary(n / 2, answer);
    answer.push_back(char('0' + (n % 2)));
}
```

---

# 30. Problem 3 — Binary Strings Without Adjacent Zeroes

## Problem

Generate all binary strings of length `n` such that:

```text
"00"
```

never appears.

For:

```text
n = 3
```

all 8 binary strings are:

```text
000
001
010
011
100
101
110
111
```

Invalid:

```text
000
001
100
```

because they contain:

```text
00
```

Valid:

```text
010
011
101
110
111
```

Total:

```text
5
```

---

# 31. Generation Prerequisite — Decision Tree

For each position in a binary string we normally have two choices:

```text
0
1
```

For length `n`:

```text
2 choices at each of n levels
```

Brute-force candidate count:

```text
2^n
```

For:

```text
n = 3
```

tree depth:

```text
3
```

and leaves:

```text
2^3 = 8
```

---

# 32. Brute Force First

A natural first solution is:

```text
generate every binary string
then reject strings containing "00"
```

Tree:

```text
                         ""
                    /          \
                  "0"          "1"
                 /   \        /   \
              "00"  "01"   "10"  "11"
              / \    / \    / \    / \
           000 001 010 011 100 101 110 111
```

Then validate leaves.

But we can do better conceptually.

If a partial string already ends in:

```text
0
```

we know appending another:

```text
0
```

is immediately illegal.

So reject that branch early.

That is pruning.

---

# 33. What Makes a String Invalid?

The only forbidden local pattern is:

```text
00
```

Therefore to decide whether we may append `0`, we only need to know:

```text
the previous character
```

We do **not** need to inspect the whole prefix.

This is state compression.

---

# 34. Recursive State

A compact state can be:

```text
index
previousCharacter
currentString
```

Meaning:

```text
index
=
which position are we filling?

previousCharacter
=
what was placed immediately before this position?

currentString
=
partial answer built so far
```

Recursive contract:

```text
generate(index, previous, current)
generates every valid completion of current
for positions index ... n-1.
```

---

# 35. LCCM View

This generation problem fits the LCCM backtracking framework.

| Part | Question | Answer |
|---|---|---|
| Level | What position are we deciding? | `index` |
| Choice | What can we place? | `'0'` or `'1'` |
| Check | Is the choice legal? | Do not create `"00"` |
| Move | What happens next? | Append choice, recurse, undo |

Compact:

```text
LEVEL
current index

CHOICE
0 or 1

CHECK
if previous == 0,
cannot choose 0

MOVE
append → recurse → pop
```

---

# 36. Choice and Check

At every position:

```text
choice 1:
append '1'
```

This is always legal.

For `0`:

```text
append '0'
only if previous character is not '0'
```

Condition:

```cpp
if (previous != '0') {
    // choose 0
}
```

---

## 36.1 Another equivalent condition

If using the current string:

```cpp
if (current.empty() || current.back() != '0') {
    // append 0
}
```

---

# 37. Recursion Tree for `n = 3`

Start:

```text
""
```

Level 0:

```text
                         ""
                    /          \
                  "0"          "1"
```

Level 1:

From `"0"`:

```text
cannot choose 0
can choose 1
```

From `"1"`:

```text
can choose 0
can choose 1
```

So:

```text
                         ""
                    /          \
                  "0"          "1"
                    \          /  \
                   "01"      "10" "11"
```

Level 2:

```text
"01" → "010", "011"

"10" → only "101"

"11" → "110", "111"
```

Leaves:

```text
010
011
101
110
111
```

---

# 38. Pruned Recursion Tree

Full brute-force tree:

```text
                         ""
                  /              \
                0                  1
             /     \            /    \
           00       01        10      11
          / \      /  \      / \      / \
       000 001   010 011   100 101  110 111
```

Prune every branch containing `00`:

```text
                         ""
                  /              \
                0                  1
                 \               / \
                 01             10  11
                /  \             \  / \
              010  011          101 110 111
```

Crossed branches:

```text
0 → 00     X

10 → 100   X
```

Pruning means:

```text
Do not generate an invalid complete candidate
if the partial candidate is already impossible.
```

---

# 39. Detailed Dry Run for `n = 3`

Initial:

```text
index=0
current=""
```

Try `0`:

```text
current="0"
index=1
```

At index 1:

```text
previous='0'

choice '0'
→ invalid
→ skip

choice '1'
→ current="01"
```

At index 2 from `"01"`:

```text
previous='1'

choice '0'
→ "010"
→ complete
→ save

backtrack to "01"

choice '1'
→ "011"
→ complete
→ save
```

Backtrack to root.

Try root choice `1`:

```text
current="1"
```

At next position:

```text
choice '0'
→ "10"
```

From `"10"`:

```text
choice '0'
→ invalid

choice '1'
→ "101"
→ save
```

Back to `"1"`.

Choose `1`:

```text
"11"
```

From `"11"`:

```text
choose 0
→ "110"
→ save

choose 1
→ "111"
→ save
```

Final answers:

```text
010
011
101
110
111
```

---

# 40. C++17 — Simple String-State Version

```cpp
#include <bits/stdc++.h>
using namespace std;

void generate(int n, string& current, vector<string>& answer) {
    if ((int)current.size() == n) {
        answer.push_back(current);
        return;
    }

    if (current.empty() || current.back() != '0') {
        current.push_back('0');
        generate(n, current, answer);
        current.pop_back();
    }

    current.push_back('1');
    generate(n, current, answer);
    current.pop_back();
}

int main() {
    int n;
    cin >> n;

    vector<string> answer;
    string current;

    generate(n, current, answer);

    for (const string& s : answer) {
        cout << s << '\n';
    }

    return 0;
}
```

---

## 40.1 Why `pop_back()` is necessary

Suppose:

```text
current = "01"
```

We try:

```text
current += '0'
→ "010"
```

After returning, the next sibling choice must start from:

```text
"01"
```

not:

```text
"010"
```

Therefore:

```cpp
current.pop_back();
```

restores the state.

This is the backtracking undo step.

---

# 41. C++17 — Index + Previous Character Version

This version makes the recursive state explicit.

```cpp
#include <bits/stdc++.h>
using namespace std;

void generate(int index, int n, char previous, string& current, vector<string>& answer) {
    if (index == n) {
        answer.push_back(current);
        return;
    }

    if (previous != '0') {
        current.push_back('0');
        generate(index + 1, n, '0', current, answer);
        current.pop_back();
    }

    current.push_back('1');
    generate(index + 1, n, '1', current, answer);
    current.pop_back();
}

int main() {
    int n;
    cin >> n;

    vector<string> answer;
    string current;

    generate(0, n, '#', current, answer);

    for (const string& s : answer) {
        cout << s << '\n';
    }

    return 0;
}
```

Here:

```text
'#'
```

means:

```text
there is no previous bit yet
```

so both starting choices are allowed.

---

# 42. Complexity

Without pruning, a binary decision tree of depth `n` has:

```text
O(2^n)
```

nodes/leaves.

With the no-`00` restriction, many branches disappear.

The number of valid strings follows a Fibonacci-like recurrence.

Still, a safe simple upper bound is:

```text
O(2^n)
```

for generation.

---

## 42.1 Output-sensitive view

If there are:

```text
K
```

valid strings and every output has length:

```text
n
```

then simply writing/copying all outputs costs:

```text
O(K × n)
```

So for actual generation:

```text
Time = O(number of generated states + total output size)
```

A common practical bound:

```text
O(n × 2^n)
```

is safe.

---

## 42.2 Stack space

Maximum recursion depth:

```text
n
```

Therefore:

```text
Auxiliary recursion stack = O(n)
```

excluding stored answers.

---

# 43. Why the Count Follows Fibonacci

Let:

```text
F(n)
=
number of valid binary strings of length n
with no adjacent zeroes.
```

Split valid strings by first bit.

---

## Case 1 — First bit is `1`

Remaining:

```text
n-1
```

positions can form any valid string.

Count:

```text
F(n-1)
```

---

## Case 2 — First bit is `0`

The next bit must be:

```text
1
```

So prefix is forced:

```text
01
```

Remaining:

```text
n-2
```

positions can form any valid string.

Count:

```text
F(n-2)
```

Therefore:

```text
F(n)
=
F(n-1)
+
F(n-2)
```

Base:

```text
F(1)=2
```

strings:

```text
0
1
```

and:

```text
F(2)=3
```

strings:

```text
01
10
11
```

Then:

```text
F(3)
=
F(2)+F(1)
=
3+2
=
5
```

matching:

```text
010
011
101
110
111
```

---

# 44. Common Mistakes

## Mistake 1 — No base case

Recursive calls continue forever.

Always identify:

```text
smallest directly solvable state
```

---

## Mistake 2 — Recursive call does not shrink state

Bad:

```cpp
return a * power(a, b);
```

`b` never changes.

Correct:

```cpp
return a * power(a, b - 1);
```

---

## Mistake 3 — Wrong order in binary conversion

If you append:

```text
n % 2
```

before recursively processing `n/2`, you naturally produce bits in reverse order.

Use:

```text
recursive answer first
then current remainder
```

---

## Mistake 4 — Generate invalid complete strings and filter later

This works but misses the central backtracking insight.

Better:

```text
prune as soon as "00" would be created
```

---

## Mistake 5 — Forgetting undo

After:

```cpp
current.push_back(choice);
```

you must restore:

```cpp
current.pop_back();
```

before trying the next sibling branch.

---

## Mistake 6 — Confusing recursion tree with stack

For binary-string generation:

```text
tree contains many states
```

but:

```text
only one root-to-current path
is active at any moment
```

---

## Mistake 7 — Saying every recursive algorithm is exponential

No.

Examples:

```text
power(a,b) with b-1
→ O(b)

toBinary(n) with n/2
→ O(log n)

binary generation
→ branching search
→ exponential-scale
```

Complexity depends on:

```text
branching factor
+
depth
+
work per node
```

---

# 45. Recognition Patterns

## Pattern A — Reduce one numeric parameter

Examples:

```text
power(a,b)
factorial(n)
sum(1...n)
```

Typical shape:

```text
solve(n)
=
local work
+
solve(n-1)
```

---

## Pattern B — Divide the numeric state

Examples:

```text
decimal → binary
fast power
binary search
```

Typical shape:

```text
solve(n)
uses
solve(n/2)
```

Depth:

```text
O(log n)
```

---

## Pattern C — Generate all valid configurations

Examples:

```text
binary strings
subsets
permutations
N-Queens
```

Typical shape:

```text
at each level:
try choices
check validity
recurse
undo
```

This is backtracking.

---

## Pattern D — Local restriction enables pruning

Example:

```text
no adjacent zeroes
```

Only recent state is needed:

```text
previous bit
```

This often lets us avoid generating impossible branches.

---

# 46. Recursive Design Checklist

Before coding, answer:

```text
1. What does my function return/do?

2. What is the recursive state?

3. What is the smallest valid state?

4. What is the base-case answer?

5. How does each recursive call make progress?

6. Is there one recursive child or many?

7. Is work done before recursion or after it?

8. If there are choices, what are they?

9. Which choices are invalid?

10. Can invalid choices be pruned early?

11. Do I modify shared state?

12. If yes, what is the exact undo operation?

13. What is the recursion-tree depth?

14. What is the branching factor?

15. What is the time complexity?

16. What is the stack-space complexity?
```

---

# 47. Final Mental Model

These three lecture problems represent three important recursion forms.

```text
PROBLEM 1 — POWER

a^b
=
a × a^(b-1)

State:
b

Shrink:
b → b-1

Tree:
one chain

Time:
O(b)
```

```text
PROBLEM 2 — DECIMAL TO BINARY

binary(n)
=
binary(n/2)
+
(n%2)

State:
n

Shrink:
n → n/2

Tree:
one chain

Depth:
O(log n)

Key idea:
work after recursion
```

```text
PROBLEM 3 — VALID BINARY STRINGS

State:
current position + previous bit

Choices:
0 / 1

Check:
do not create 00

Move:
append → recurse → undo

Tree:
branching search

Key idea:
pruning + backtracking
```

Master picture:

```text
                         RECURSION
                             |
          +------------------+------------------+
          |                  |                  |
      one-child          divide-state       branching
      recursion          recursion          recursion
          |                  |                  |
       a^b               decimal→binary      generation
          |                  |                  |
      b → b-1            n → n/2           try choices
          |                  |                  |
       O(b)             O(log n)       check / prune / undo
```

---

# 48. One-Minute Revision

```text
RECURSION PROBLEM SOLVING

Always define:

solve(state) means ...

Then:

BASE
TRANSITION
COMBINATION

--------------------------------

POWER

a^b = a × a^(b-1)

base:
a^0 = 1

T(b)=T(b-1)+O(1)

Time:
O(b)

--------------------------------

FAST POWER

b → b/2

Time:
O(log b)

--------------------------------

DECIMAL → BINARY

binary(n)
=
binary(n/2)
+
(n%2)

Example:

13
↓
6
↓
3
↓
1

return:

1
11
110
1101

Time:
O(log n)

--------------------------------

NO ADJACENT ZEROES

At each level:

choice 0
choice 1

but:

if previous == 0
cannot choose 0

LCCM:

Level  = index
Choice = 0/1
Check  = no "00"
Move   = append → recurse → pop

For n=3:

010
011
101
110
111

--------------------------------

TREE THINKING

one child + depth n
→ linear-scale

one child + halve input
→ log depth

multiple choices
→ branching recursion

pruning
→ cut impossible branches early
```
