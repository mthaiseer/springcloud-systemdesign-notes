# Time Complexity of Recursion — Level 2
## TLE-Style Self-Study Notes with Prerequisites, Recursion Trees, Substitution, Master Theorem, Quizzes, and C++17

> **Lecture focus:** Substitution Method, Master’s Method, and Time Complexity in Recursion.
>
> **Self-study goal:** Given recursive code or a recurrence, learn to answer:
>
> 1. What is the recurrence?
> 2. How many recursive calls are created?
> 3. How much does the input shrink?
> 4. How much non-recursive work happens in one call?
> 5. What is the recursion-tree height?
> 6. How many nodes exist at each level?
> 7. What is the total number of nodes?
> 8. Can the lecture’s Master Theorem form be applied?
> 9. If not, can substitution or a recursion tree solve it?

---

# Table of Contents

1. [Lecture Roadmap](#1-lecture-roadmap)
2. [Prerequisite 1 — What Does Time Complexity Measure?](#2-prerequisite-1--what-does-time-complexity-measure)
3. [Prerequisite 2 — Big-O Refresher](#3-prerequisite-2--big-o-refresher)
4. [Prerequisite 3 — Loop Complexity Refresher](#4-prerequisite-3--loop-complexity-refresher)
5. [Prerequisite 4 — Logarithms You Need for Recursion](#5-prerequisite-4--logarithms-you-need-for-recursion)
6. [Prerequisite 5 — Geometric Series](#6-prerequisite-5--geometric-series)
7. [Prerequisite 6 — From Recursive Code to a Recurrence](#7-prerequisite-6--from-recursive-code-to-a-recurrence)
8. [The Core Formula for Recursive Time](#8-the-core-formula-for-recursive-time)
9. [Method 1 — Substitution Method](#9-method-1--substitution-method)
10. [Substitution Example — `T(n) = 2T(n/2) + n`](#10-substitution-example--tn--2tn2--n)
11. [Testing a Wrong Guess — `O(n)`](#11-testing-a-wrong-guess--on)
12. [Testing a Loose Guess — `O(n^2)`](#12-testing-a-loose-guess--on2)
13. [What Substitution Really Proves](#13-what-substitution-really-proves)
14. [Method 2 — Master Theorem](#14-method-2--master-theorem)
15. [How to Identify `a`, `b`, and `d`](#15-how-to-identify-a-b-and-d)
16. [Master Theorem Case 1](#16-master-theorem-case-1)
17. [Master Theorem Case 2](#17-master-theorem-case-2)
18. [Master Theorem Case 3](#18-master-theorem-case-3)
19. [Lecture Example 1 — `T(n) = T(n/2) + O(n)`](#19-lecture-example-1--tn--tn2--on)
20. [Lecture Example 2 — `T(n) = 7T(n/2) + O(n^2)`](#20-lecture-example-2--tn--7tn2--on2)
21. [Lecture Example 3 — `T(n) = 3T(n/4) + O(n)`](#21-lecture-example-3--tn--3tn4--on)
22. [Classic Example — `T(n) = 2T(n/2) + O(n)`](#22-classic-example--tn--2tn2--on)
23. [Drawbacks / When This Master-Theorem Form Does Not Apply](#23-drawbacks--when-this-master-theorem-form-does-not-apply)
24. [Method 3 — Recursion Tree](#24-method-3--recursion-tree)
25. [One-Child Decrement Recursion](#25-one-child-decrement-recursion)
26. [Binary Branching Recursion](#26-binary-branching-recursion)
27. [Three-Way Branching Recursion](#27-three-way-branching-recursion)
28. [General Tree Formula — Branching Factor × Height](#28-general-tree-formula--branching-factor--height)
29. [Very Important Rule — Nodes × Work Per Node](#29-very-important-rule--nodes--work-per-node)
30. [Quiz 1 — One Call on `n - 1`](#30-quiz-1--one-call-on-n---1)
31. [Quiz 2 — Two Calls on `n - 1`](#31-quiz-2--two-calls-on-n---1)
32. [Quiz 3 — One Call on `n / 2`](#32-quiz-3--one-call-on-n--2)
33. [Quiz 4 — Two Calls on `n / 2`](#33-quiz-4--two-calls-on-n--2)
34. [Quiz 5 — Two Calls on `n / 2` + `O(n)` Work](#34-quiz-5--two-calls-on-n--2--on-work)
35. [Compact Pattern Table](#35-compact-pattern-table)
36. [How to Analyze Any Recursive Function Step by Step](#36-how-to-analyze-any-recursive-function-step-by-step)
37. [Common Mistakes](#37-common-mistakes)
38. [Recognition Checklist](#38-recognition-checklist)
39. [Final Mental Model](#39-final-mental-model)
40. [One-Minute Revision](#40-one-minute-revision)

---

# 1. Lecture Roadmap

The lecture develops recursive time complexity in this order:

```text
recurrence relation
      ↓
substitution method
      ↓
test a guessed bound
      ↓
Master Theorem
      ↓
examples
      ↓
limitations of Master Theorem
      ↓
recursion tree
      ↓
count tree nodes
      ↓
multiply by work per node
      ↓
quiz patterns
```

The most useful practical idea is:

```text
TIME COMPLEXITY
=
how many recursive calls are executed
×
how much non-recursive work each call performs
```

For recursion trees, this becomes:

```text
TOTAL TIME
≈
sum of work over every node in the recursion tree
```

---

# 2. Prerequisite 1 — What Does Time Complexity Measure?

Time complexity describes how the number of operations grows as input size `n` grows.

Example:

```cpp
for (int i = 0; i < n; i++) {
    cout << i << '\n';
}
```

The loop runs:

```text
n times
```

So:

```text
Time = O(n)
```

---

## 2.1 Recursive code is not different in principle

For iterative code we ask:

```text
How many iterations?
How much work per iteration?
```

For recursive code we ask:

```text
How many function calls?
How much work per function call?
```

The difficulty is that recursive calls form a tree.

---

# 3. Prerequisite 2 — Big-O Refresher

Big-O gives an upper bound on growth.

Examples:

```text
O(1)
O(log n)
O(n)
O(n log n)
O(n^2)
O(2^n)
O(3^n)
```

Typical growth order:

```text
O(1)
<
O(log n)
<
O(n)
<
O(n log n)
<
O(n^2)
<
O(2^n)
<
O(3^n)
```

---

## 3.1 Ignore constants

```text
O(2n)
=
O(n)
```

```text
O(100n^2)
=
O(n^2)
```

---

## 3.2 Keep the dominant term

```text
O(n^2 + n + 10)
=
O(n^2)
```

---

# 4. Prerequisite 3 — Loop Complexity Refresher

The lecture later compares loops with recursive shrinking.

You should recognize these immediately.

---

## 4.1 Additive loop

```cpp
for (int i = 0; i < n; i++) {
}
```

Values:

```text
0,1,2,3,...,n-1
```

Number of iterations:

```text
n
```

Therefore:

```text
O(n)
```

---

## 4.2 Add by 2

```cpp
for (int i = 0; i < n; i += 2) {
}
```

Iterations:

```text
n/2
```

But:

```text
O(n/2)
=
O(n)
```

---

## 4.3 Multiply by 2

```cpp
for (int i = 1; i <= n; i *= 2) {
}
```

Values:

```text
1
2
4
8
16
...
```

After `k` iterations:

```text
2^k ≈ n
```

So:

```text
k ≈ log2(n)
```

Therefore:

```text
O(log n)
```

This exact idea appears in:

```text
recurse(n / 2)
```

---

# 5. Prerequisite 4 — Logarithms You Need for Recursion

You do not need advanced logarithms.

You mainly need these rules.

---

## 5.1 Definition

```text
log_b(n) = k
```

means:

```text
b^k = n
```

Example:

```text
log2(8) = 3
```

because:

```text
2^3 = 8
```

---

## 5.2 Division rule

```text
log(a / b)
=
log(a) - log(b)
```

This is used in the lecture’s substitution proof.

Example:

```text
log(n / 2)
=
log(n) - log(2)
```

If log base is 2:

```text
log2(n / 2)
=
log2(n) - 1
```

---

## 5.3 Height of repeated halving

Suppose:

```text
n
→ n/2
→ n/4
→ n/8
→ ...
→ 1
```

After `k` halvings:

```text
n / 2^k = 1
```

Therefore:

```text
n = 2^k
```

Take log base 2:

```text
k = log2(n)
```

So recursion depth is:

```text
O(log n)
```

---

# 6. Prerequisite 5 — Geometric Series

Recursion trees often create:

```text
1 + 2 + 4 + 8 + ... + 2^k
```

This is a geometric progression.

General formula:

```text
1 + r + r^2 + ... + r^k
=
(r^(k+1) - 1) / (r - 1)
```

For `r = 2`:

```text
1 + 2 + 4 + ... + 2^k
=
2^(k+1) - 1
```

Asymptotically:

```text
O(2^k)
```

---

## 6.1 Why this matters

If recursion-tree height is `n` and every node creates 2 children:

```text
level 0 → 1 node
level 1 → 2 nodes
level 2 → 4 nodes
level 3 → 8 nodes
...
level n → 2^n nodes
```

Total nodes:

```text
1 + 2 + 4 + ... + 2^n
=
O(2^n)
```

---

# 7. Prerequisite 6 — From Recursive Code to a Recurrence

The first skill is converting code into a recurrence.

---

## 7.1 One recursive call

```cpp
void recurse(int n) {
    if (n == 0) {
        return;
    }

    recurse(n - 1);
}
```

One recursive child of size:

```text
n - 1
```

Non-recursive work per call:

```text
O(1)
```

Therefore:

```text
T(n)
=
T(n - 1)
+
O(1)
```

---

## 7.2 Two recursive calls

```cpp
void recurse(int n) {
    if (n == 0) {
        return;
    }

    recurse(n - 1);
    recurse(n - 1);
}
```

Two recursive children of size:

```text
n - 1
```

So:

```text
T(n)
=
2T(n - 1)
+
O(1)
```

---

## 7.3 Halving recursion

```cpp
void recurse(int n) {
    if (n == 0) {
        return;
    }

    recurse(n / 2);
}
```

Recurrence:

```text
T(n)
=
T(n / 2)
+
O(1)
```

---

## 7.4 Two half-size recursive calls

```cpp
void recurse(int n) {
    if (n == 0) {
        return;
    }

    recurse(n / 2);
    recurse(n / 2);
}
```

Recurrence:

```text
T(n)
=
2T(n / 2)
+
O(1)
```

---

## 7.5 Recursive calls plus a loop

```cpp
void recurse(int n) {
    if (n == 0) {
        return;
    }

    recurse(n / 2);
    recurse(n / 2);

    for (int i = 0; i < n; i++) {
        cout << "Hello\n";
    }
}
```

Per node:

```text
loop work = O(n)
```

Therefore:

```text
T(n)
=
2T(n / 2)
+
O(n)
```

---

# 8. The Core Formula for Recursive Time

Whenever possible, separate:

```text
recursive work
+
local/non-recursive work
```

Generic recurrence:

```text
T(n)
=
sum of recursive-child costs
+
work done in current call
```

Example:

```text
T(n)
=
2T(n/2)
+
n
```

means:

```text
2 recursive calls
each on n/2

plus

n work outside recursion
```

---

# 9. Method 1 — Substitution Method

The lecture defines substitution as:

```text
1. Guess a complexity bound.
2. Assume the bound is true for smaller inputs.
3. Substitute the assumed bound into the recurrence.
4. Check whether the resulting inequality proves the guess.
```

This is essentially an induction-style proof.

---

## 9.1 General idea

Suppose:

```text
T(n)
=
2T(n/2)
+
n
```

Guess:

```text
T(n)
=
O(n log n)
```

To prove it, we try to show:

```text
T(n)
<=
c n log n
```

for some constant `c`.

Assume the bound is true for smaller values.

Then:

```text
T(n/2)
<=
c (n/2) log(n/2)
```

Substitute.

---

# 10. Substitution Example — `T(n) = 2T(n/2) + n`

We want to test:

```text
T(n)
=
O(n log n)
```

Assume:

```text
T(n)
<=
c n log n
```

for smaller inputs.

In particular:

```text
T(n/2)
<=
c (n/2) log(n/2)
```

Now start from the recurrence:

```text
T(n)
=
2T(n/2) + n
```

Substitute the assumption:

```text
T(n)
<=
2 * [c(n/2)log(n/2)] + n
```

Simplify `2 * n/2`:

```text
T(n)
<=
c n log(n/2) + n
```

Use:

```text
log(n/2)
=
log n - log 2
```

Taking base 2 for simplicity:

```text
log2 2 = 1
```

So:

```text
T(n)
<=
c n (log n - 1) + n
```

Expand:

```text
T(n)
<=
c n log n - c n + n
```

Group the final terms:

```text
T(n)
<=
c n log n - (c - 1)n
```

For:

```text
c >= 1
```

we have:

```text
(c - 1)n >= 0
```

Therefore:

```text
T(n)
<=
c n log n
```

So the guessed upper bound works:

```text
T(n)
=
O(n log n)
```

---

## 10.1 Algebra picture

```text
          T(n)
            |
            v
      2T(n/2) + n
            |
       substitute
            |
            v
 2[c(n/2)log(n/2)] + n
            |
            v
   cn log(n/2) + n
            |
            v
 cn(log n - 1) + n
            |
            v
 cn log n - cn + n
            |
            v
      <= cn log n
```

---

# 11. Testing a Wrong Guess — `O(n)`

Now the lecture tests:

```text
T(n)
=
O(n)
```

We try to prove:

```text
T(n)
<=
cn
```

Assume:

```text
T(n/2)
<=
c(n/2)
```

Recurrence:

```text
T(n)
=
2T(n/2) + n
```

Substitute:

```text
T(n)
<=
2 * c(n/2) + n
```

Simplify:

```text
T(n)
<=
cn + n
```

Factor:

```text
T(n)
<=
(c + 1)n
```

But to prove our guess, we needed:

```text
T(n)
<=
cn
```

Instead we derived:

```text
T(n)
<=
(c + 1)n
```

and:

```text
(c + 1)n > cn
```

So this proof cannot establish the guessed `O(n)` bound.

---

## 11.1 Visual contradiction

```text
wanted:
T(n) <= cn

derived:
T(n) <= (c+1)n

but:

(c+1)n
   >
 cn
```

The extra `+n` does not disappear.

---

# 12. Testing a Loose Guess — `O(n^2)`

The lecture also tests a larger guess.

Assume:

```text
T(n)
<=
cn^2
```

Then:

```text
T(n/2)
<=
c(n/2)^2
```

Start:

```text
T(n)
=
2T(n/2)
+
n
```

Substitute:

```text
T(n)
<=
2 * c(n/2)^2
+
n
```

Square:

```text
(n/2)^2
=
n^2 / 4
```

So:

```text
T(n)
<=
2 * c n^2 / 4
+
n
```

Therefore:

```text
T(n)
<=
(cn^2)/2 + n
```

For sufficiently large `n`, this is at most:

```text
cn^2
```

for a suitable constant `c`.

So:

```text
O(n^2)
```

is also a valid upper bound.

---

## 12.1 Important self-study insight

This does **not** mean the tight complexity is `O(n^2)`.

The same recurrence is already proven to be:

```text
O(n log n)
```

And:

```text
n log n
<
n^2
```

for large `n`.

So:

```text
O(n^2)
```

is a looser upper bound.

Think:

```text
Correct Big-O upper bound
does not automatically mean
tightest useful complexity.
```

---

# 13. What Substitution Really Proves

Substitution is a proof technique.

It answers:

```text
"If I guess this bound,
can I prove the recurrence stays inside it?"
```

Workflow:

```text
guess
  ↓
assume for smaller input
  ↓
substitute
  ↓
simplify
  ↓
does it fit original guessed bound?
```

---

## 13.1 When substitution is useful

Use it when:

```text
- you already suspect the answer
- Master Theorem does not apply
- you want a formal proof
- a recursion-tree pattern suggests a guess
```

---

# 14. Method 2 — Master Theorem

The lecture uses the following form:

```text
T(n)
=
aT(n/b)
+
f(n)
```

with:

```text
a > 0
b > 1
f(n) in O(n^d)
d >= 0
```

Then compare:

```text
d
```

with:

```text
log_b(a)
```

---

## 14.1 Meaning of the symbols

```text
a
=
number of recursive subproblems

b
=
how much the input size is divided by

n/b
=
size of each recursive child

f(n)
=
non-recursive work in one call

d
=
polynomial exponent of f(n)
```

Example:

```text
T(n)
=
2T(n/2)
+
O(n)
```

Then:

```text
a = 2
b = 2
f(n) = O(n^1)
d = 1
```

---

# 15. How to Identify `a`, `b`, and `d`

Consider:

```text
T(n)
=
7T(n/2)
+
O(n^2)
```

Match:

```text
aT(n/b) + O(n^d)
```

So:

```text
a = 7
b = 2
d = 2
```

---

## 15.1 Another example

```text
T(n)
=
3T(n/4)
+
O(n)
```

Therefore:

```text
a = 3
b = 4
d = 1
```

---

## 15.2 A common mistake

In:

```text
T(n)
=
T(n/2)
+
O(n)
```

do not say:

```text
a = 0
```

There is one recursive call.

So:

```text
a = 1
```

---

# 16. Master Theorem Case 1

The lecture’s first case is:

```text
if d > log_b(a)
```

then:

```text
T(n)
=
O(n^d)
```

Interpretation:

```text
work inside each node
dominates the recursion-tree growth
```

---

# 17. Master Theorem Case 2

If:

```text
d = log_b(a)
```

then:

```text
T(n)
=
O(n^d log n)
```

This is the balanced case.

Classic example:

```text
T(n)
=
2T(n/2)
+
O(n)
```

because:

```text
a = 2
b = 2
d = 1

log2(2) = 1
```

So:

```text
d = log_b(a)
```

Therefore:

```text
T(n)
=
O(n log n)
```

---

# 18. Master Theorem Case 3

If:

```text
d < log_b(a)
```

then:

```text
T(n)
=
O(n^(log_b a))
```

Interpretation:

```text
recursive branching dominates
the per-node polynomial work
```

---

# 19. Lecture Example 1 — `T(n) = T(n/2) + O(n)`

Match:

```text
T(n)
=
aT(n/b)
+
O(n^d)
```

We get:

```text
a = 1
b = 2
d = 1
```

Compute:

```text
log_b(a)
=
log2(1)
=
0
```

Compare:

```text
d = 1
>
0
```

So:

```text
d > log_b(a)
```

Lecture case:

```text
T(n)
=
O(n^d)
```

Therefore:

```text
T(n)
=
O(n)
```

---

## 19.1 Tree intuition

```text
level 0: n
level 1: n/2
level 2: n/4
level 3: n/8
...
```

Total work:

```text
n + n/2 + n/4 + ...
```

Geometric series:

```text
< 2n
```

So:

```text
O(n)
```

This confirms the theorem.

---

# 20. Lecture Example 2 — `T(n) = 7T(n/2) + O(n^2)`

Identify:

```text
a = 7
b = 2
d = 2
```

Compute:

```text
log_b(a)
=
log2(7)
```

Since:

```text
2^2 = 4
2^3 = 8
```

we know:

```text
2 < log2(7) < 3
```

Approximately:

```text
log2(7) ≈ 2.807
```

Compare:

```text
d = 2
<
2.807
```

Therefore:

```text
d < log_b(a)
```

Lecture result:

```text
T(n)
=
O(n^(log2 7))
```

---

# 21. Lecture Example 3 — `T(n) = 3T(n/4) + O(n)`

Identify:

```text
a = 3
b = 4
d = 1
```

Compute:

```text
log4(3)
```

Since:

```text
4^0 = 1
4^1 = 4
```

we have:

```text
0 < log4(3) < 1
```

Therefore:

```text
d = 1
>
log4(3)
```

So:

```text
T(n)
=
O(n^d)
=
O(n)
```

---

# 22. Classic Example — `T(n) = 2T(n/2) + O(n)`

Identify:

```text
a = 2
b = 2
d = 1
```

Compute:

```text
log2(2)
=
1
```

Therefore:

```text
d = log_b(a)
```

Use balanced case:

```text
T(n)
=
O(n^d log n)
```

So:

```text
T(n)
=
O(n log n)
```

---

## 22.1 Why each level costs `O(n)`

Recursion tree:

```text
                    n
               /         \
             n/2         n/2
            /  \         /  \
          n/4 n/4      n/4 n/4
```

If local work is proportional to subproblem size:

```text
level 0:
n

level 1:
n/2 + n/2 = n

level 2:
4 * (n/4) = n
```

Every level:

```text
O(n)
```

Number of levels:

```text
O(log n)
```

Therefore:

```text
O(n)
×
O(log n)
=
O(n log n)
```

This is the recursion-tree intuition behind the Master-Theorem result.

---

# 23. Drawbacks / When This Master-Theorem Form Does Not Apply

The lecture explicitly lists cases where its stated Master-Theorem form should not be used.

---

## 23.1 `T(n)` not monotone

Lecture example:

```text
T(n) = cos(n)
```

---

## 23.2 `f(n)` not polynomial in the lecture’s form

Lecture example:

```text
f(n) = 2^n
```

The lecture theorem expects:

```text
f(n) in O(n^d)
```

---

## 23.3 `b` cannot be represented as a constant

Lecture example:

```text
T(n)
=
T(sqrt(n))
```

This cannot be written as:

```text
T(n/b)
```

for one fixed constant `b`.

---

## 23.4 Practical response

If the lecture’s Master-Theorem form does not fit, try:

```text
recursion tree
substitution
direct expansion
change of variable
another recurrence technique
```

For this note, the main alternatives are:

```text
recursion tree
+
substitution
```

---

# 24. Method 3 — Recursion Tree

A recursion tree expands every recursive call.

Each node represents:

```text
one function call
```

The total running time is:

```text
sum of non-recursive work done by all nodes
```

A very useful approximation when each node has similar local cost is:

```text
time
=
number of nodes
×
work per node
```

---

# 25. One-Child Decrement Recursion

Consider:

```text
T(n)
=
T(n - 1)
+
O(1)
```

Tree:

```text
n
|
n-1
|
n-2
|
n-3
|
...
|
1
|
0
```

Number of calls:

```text
n + 1
```

Asymptotically:

```text
O(n)
```

Work per node:

```text
O(1)
```

Therefore:

```text
T(n)
=
O(n)
```

---

# 26. Binary Branching Recursion

Consider:

```text
T(n)
=
2T(n - 1)
+
O(1)
```

Tree:

```text
                          n
                     /         \
                  n-1           n-1
                /    \         /    \
             n-2    n-2     n-2    n-2
             ...     ...     ...     ...
```

At each level:

```text
level 0 → 1
level 1 → 2
level 2 → 4
level 3 → 8
...
```

Depth:

```text
n
```

Total nodes:

```text
1 + 2 + 4 + ... + 2^n
=
O(2^n)
```

Work per node:

```text
O(1)
```

Therefore:

```text
O(2^n)
```

---

# 27. Three-Way Branching Recursion

Lecture-style example:

```cpp
int f(int n) {
    if (n <= 0) {
        return 0;
    }

    return f(n - 1) + f(n - 2) + f(n - 3);
}
```

A rough upper-bound tree can treat every non-base node as having at most:

```text
3 children
```

Depth is at most:

```text
n
```

So:

```text
level 0 → 1
level 1 → 3
level 2 → 3^2
level 3 → 3^3
...
```

Total nodes bounded by:

```text
1 + 3 + 3^2 + ... + 3^n
=
O(3^n)
```

Therefore a simple upper bound is:

```text
O(3^n)
```

---

## 27.1 Why this is an upper bound

The actual children shrink by:

```text
n - 1
n - 2
n - 3
```

so not every path has identical depth.

But:

```text
maximum branching factor = 3
maximum depth = n
```

gives the safe tree bound:

```text
O(3^n)
```

---

# 28. General Tree Formula — Branching Factor × Height

Suppose every node creates at most:

```text
b
```

children.

And recursion-tree height is:

```text
h
```

Then the maximum number of nodes is roughly:

```text
1 + b + b^2 + ... + b^h
```

For `b > 1`:

```text
O(b^h)
```

---

## 28.1 Additive shrink

If state changes:

```text
n → n - 1
```

then:

```text
height = O(n)
```

If branching factor is `b`:

```text
nodes ≈ O(b^n)
```

---

## 28.2 Multiplicative shrink

If state changes:

```text
n → n / 2
```

then:

```text
height = O(log n)
```

If branching factor is `b`:

```text
nodes
≈
b^(log2 n)
```

Using:

```text
a^(log_b n)
=
n^(log_b a)
```

For `b` recursive children and halving:

```text
nodes
≈
n^(log2 b)
```

Example:

```text
2 children
height log2 n
```

gives:

```text
2^(log2 n)
=
n
```

---

# 29. Very Important Rule — Nodes × Work Per Node

The lecture emphasizes:

```text
Time Complexity
=
Total number of nodes
×
Time complexity of a single node
```

This is especially useful when each node has the same asymptotic local work.

---

## 29.1 Example

Suppose recursion tree has:

```text
O(3^n)
```

nodes.

If each call also performs:

```text
O(n)
```

work, a straightforward upper bound is:

```text
O(n * 3^n)
```

---

## 29.2 But be careful

Sometimes local work changes by level.

Example:

```text
T(n)
=
2T(n/2)
+
n
```

At root:

```text
work = n
```

At next level, each node does:

```text
n/2
```

So it is more accurate to sum **level work**:

```text
level 0: n
level 1: 2(n/2) = n
level 2: 4(n/4) = n
...
```

Total:

```text
n * log n
```

---

# 30. Quiz 1 — One Call on `n - 1`

Lecture code pattern:

```cpp
void recurse(int n) {
    if (n == 0) {
        return;
    }

    recurse(n - 1);
}
```

Recurrence:

```text
T(n)
=
T(n - 1)
+
O(1)
```

Tree:

```text
n
|
n-1
|
n-2
|
...
|
1
|
0
```

Number of nodes:

```text
O(n)
```

Work per node:

```text
O(1)
```

Answer:

```text
O(n)
```

---

# 31. Quiz 2 — Two Calls on `n - 1`

Code:

```cpp
void recurse(int n) {
    if (n == 0) {
        return;
    }

    recurse(n - 1);
    recurse(n - 1);
}
```

Recurrence:

```text
T(n)
=
2T(n - 1)
+
O(1)
```

Tree:

```text
                           n
                       /       \
                    n-1         n-1
                  /   \         /   \
               n-2   n-2    n-2   n-2
               ...   ...    ...   ...
```

Level sizes:

```text
1
2
4
8
...
2^n
```

Total nodes:

```text
O(2^n)
```

Therefore:

```text
Time = O(2^n)
```

---

## 31.1 Important contrast with Quiz 1

Only one line changed:

```text
one recursive call
→ O(n)

two recursive calls
→ O(2^n)
```

Branching completely changes the growth.

---

# 32. Quiz 3 — One Call on `n / 2`

Code:

```cpp
void recurse(int n) {
    if (n == 0) {
        return;
    }

    recurse(n / 2);
}
```

State progression:

```text
n
↓
n/2
↓
n/4
↓
n/8
↓
...
↓
1
↓
0
```

We need:

```text
n / 2^k ≈ 1
```

So:

```text
k = log2(n)
```

Number of calls:

```text
O(log n)
```

Work per call:

```text
O(1)
```

Answer:

```text
O(log n)
```

---

# 33. Quiz 4 — Two Calls on `n / 2`

Code:

```cpp
void recurse(int n) {
    if (n == 0) {
        return;
    }

    recurse(n / 2);
    recurse(n / 2);
}
```

Recurrence:

```text
T(n)
=
2T(n/2)
+
O(1)
```

---

## 33.1 Tree height

Each call halves `n`.

So:

```text
height = O(log n)
```

---

## 33.2 Nodes at each level

```text
level 0 → 1
level 1 → 2
level 2 → 4
...
level log2(n) → 2^(log2 n)
```

Now:

```text
2^(log2 n)
=
n
```

Total tree nodes:

```text
1 + 2 + 4 + ... + n
=
O(n)
```

Work per node:

```text
O(1)
```

Therefore:

```text
Time = O(n)
```

---

## 33.3 Master-Theorem confirmation

```text
T(n)
=
2T(n/2)
+
O(1)
```

So:

```text
a = 2
b = 2
d = 0
```

Compute:

```text
log2(2)
=
1
```

Compare:

```text
d = 0
<
1
```

Therefore:

```text
O(n^(log2 2))
=
O(n)
```

Same result.

---

# 34. Quiz 5 — Two Calls on `n / 2` + `O(n)` Work

Lecture code pattern:

```cpp
void recurse(int n) {
    if (n == 0) {
        return;
    }

    recurse(n / 2);
    recurse(n / 2);

    for (int i = 0; i < n; i++) {
        cout << "Hello\n";
    }
}
```

Recurrence:

```text
T(n)
=
2T(n/2)
+
O(n)
```

---

## 34.1 Method A — Master Theorem

```text
a = 2
b = 2
d = 1
```

Compute:

```text
log2(2)
=
1
```

So:

```text
d = log_b(a)
```

Balanced case:

```text
T(n)
=
O(n log n)
```

---

## 34.2 Method B — Recursion tree

At root:

```text
1 node
work per node = n

level work = n
```

Next level:

```text
2 nodes
each problem size = n/2
work per node = n/2

level work:
2 * n/2 = n
```

Next:

```text
4 nodes
each size = n/4

level work:
4 * n/4 = n
```

So:

```text
every level costs O(n)
```

Height:

```text
O(log n)
```

Therefore:

```text
O(n) * O(log n)
=
O(n log n)
```

---

## 34.3 Tree diagram

```text
                     size n
                  local work n
                  /          \
          size n/2          size n/2
          work n/2          work n/2
           /   \              /   \
       n/4    n/4         n/4    n/4
      work    work        work    work
      n/4     n/4         n/4     n/4
```

Level totals:

```text
level 0 → n
level 1 → n
level 2 → n
...
```

Number of levels:

```text
log n
```

Total:

```text
n log n
```

---

# 35. Compact Pattern Table

| Recursive pattern | Recurrence | Height | Approx. nodes | Time if local work `O(1)` |
|---|---|---:|---:|---:|
| One call on `n-1` | `T(n)=T(n-1)+1` | `n` | `n` | `O(n)` |
| Two calls on `n-1` | `T(n)=2T(n-1)+1` | `n` | `2^n` | `O(2^n)` |
| Three calls on smaller additive states | roughly `3`-ary | `n` | up to `3^n` | `O(3^n)` upper bound |
| One call on `n/2` | `T(n)=T(n/2)+1` | `log n` | `log n` | `O(log n)` |
| Two calls on `n/2` | `T(n)=2T(n/2)+1` | `log n` | `n` | `O(n)` |
| Two calls on `n/2` + `n` work | `T(n)=2T(n/2)+n` | `log n` | `n` | `O(n log n)` |

---

# 36. How to Analyze Any Recursive Function Step by Step

Use this exact checklist.

---

## Step 1 — Ignore recursive calls temporarily

Find work done directly in one function call.

Examples:

```text
few arithmetic statements
→ O(1)

loop from 0 to n
→ O(n)

sort n items
→ O(n log n)
```

Call this:

```text
local work
```

---

## Step 2 — Count recursive calls

Example:

```cpp
solve(n - 1);
```

means:

```text
1 child
```

Example:

```cpp
solve(n - 1);
solve(n - 1);
```

means:

```text
2 children
```

---

## Step 3 — Find child input size

Examples:

```text
n - 1
n - 2
n / 2
n / 3
```

---

## Step 4 — Write the recurrence

Example:

```text
2 calls
each size n/2
plus O(n) local work
```

becomes:

```text
T(n)
=
2T(n/2)
+
O(n)
```

---

## Step 5 — Choose a solution technique

Try:

```text
Master Theorem
```

when recurrence matches the lecture form:

```text
aT(n/b) + O(n^d)
```

Otherwise try:

```text
recursion tree
substitution
direct expansion
```

---

## Step 6 — Cross-check with recursion tree

Ask:

```text
What is the branching factor?

What is the height?

How many nodes exist at level k?

What is total work at each level?
```

---

## Step 7 — Separate time from stack space

Time:

```text
all calls in complete execution
```

Stack:

```text
maximum simultaneously active path
```

Example:

```text
two recurse(n-1) calls

Time:
O(2^n)

Stack depth:
O(n)
```

---

# 37. Common Mistakes

## Mistake 1 — Complexity = recursion depth

Wrong.

Example:

```text
two recurse(n-1) calls
```

Depth:

```text
O(n)
```

But nodes:

```text
O(2^n)
```

So time:

```text
O(2^n)
```

---

## Mistake 2 — Counting only recursive calls and ignoring local work

For:

```text
T(n)
=
2T(n/2)
+
O(n)
```

tree has:

```text
O(n)
```

nodes.

But time is not simply:

```text
O(n)
```

because internal nodes perform size-dependent work.

Correct level summation gives:

```text
O(n log n)
```

---

## Mistake 3 — Confusing `n-1` with `n/2`

```text
n → n-1
```

needs:

```text
O(n)
```

levels.

But:

```text
n → n/2
```

needs:

```text
O(log n)
```

levels.

---

## Mistake 4 — Thinking `i += 2` is `O(log n)`

```cpp
i += 2
```

is additive growth.

Still:

```text
O(n)
```

Logarithmic loops use multiplicative growth:

```cpp
i *= 2
```

---

## Mistake 5 — Incorrect Master-Theorem parameters

For:

```text
T(n)
=
7T(n/2)
+
n^2
```

correct:

```text
a=7
b=2
d=2
```

---

## Mistake 6 — Forgetting to compare `d` with `log_b(a)`

Do not compare:

```text
a with d
```

Directly compare:

```text
d
vs
log_b(a)
```

---

## Mistake 7 — Assuming any recurrence can use Master Theorem

The lecture’s theorem form specifically expects:

```text
aT(n/b) + O(n^d)
```

If the recurrence does not fit, use another technique.

---

## Mistake 8 — Treating a valid upper bound as automatically tight

If:

```text
T(n) = O(n log n)
```

then:

```text
T(n) = O(n^2)
```

may also be mathematically true as an upper bound.

But:

```text
O(n log n)
```

is much more informative.

---

# 38. Recognition Checklist

Given recursive code, ask:

```text
[ ] How many recursive calls per state?
[ ] What is each child's input size?
[ ] Is the shrink additive: n-1, n-2?
[ ] Is the shrink multiplicative: n/2, n/3?
[ ] What is local work in one call?
[ ] Can I write T(n)?
[ ] Does it match aT(n/b) + O(n^d)?
[ ] If yes, what are a, b, d?
[ ] Compare d with log_b(a).
[ ] If not, draw a recursion tree.
[ ] What is tree height?
[ ] How many nodes per level?
[ ] What is total work per level?
[ ] What is the sum across all levels?
```

---

# 39. Final Mental Model

Recursive time complexity becomes much easier if you always separate four questions.

---

## 39.1 Branching

```text
How many recursive calls does one node create?
```

Examples:

```text
1
2
3
```

---

## 39.2 Shrinking

```text
How quickly does input size decrease?
```

Examples:

```text
n → n-1
n → n-2
n → n/2
```

---

## 39.3 Local work

```text
What does one call do besides recursion?
```

Examples:

```text
O(1)
O(n)
O(n log n)
```

---

## 39.4 Combine across the tree

```text
Total time
=
sum of local work
over every recursive call
```

Master diagram:

```text
                  RECURSIVE CODE
                        |
        +---------------+---------------+
        |               |               |
   # children       child size      local work
        |               |               |
        +---------------+---------------+
                        |
                        v
                    recurrence
                        |
           +------------+------------+
           |                         |
      fits Master                does not fit
      Theorem form                    |
           |                          |
 compare d vs log_b(a)          recursion tree /
           |                    substitution
           v
      complexity
```

---

# 40. One-Minute Revision

```text
TIME COMPLEXITY OF RECURSION

Step 1:
Count recursive calls.

Step 2:
Find new input size.

Step 3:
Find non-recursive work per call.

Step 4:
Write recurrence.

Example:
2 calls on n/2 + n local work

T(n) = 2T(n/2) + O(n)

--------------------------------

SUBSTITUTION

1. Guess.
2. Assume guess for smaller input.
3. Substitute.
4. Simplify.
5. Prove or reject.

For:
T(n)=2T(n/2)+n

tight useful result:
O(n log n)

--------------------------------

LECTURE MASTER THEOREM FORM

T(n)=aT(n/b)+O(n^d)

Compare:

d
with
log_b(a)

If:
d > log_b(a)
→ O(n^d)

If:
d = log_b(a)
→ O(n^d log n)

If:
d < log_b(a)
→ O(n^(log_b a))

--------------------------------

RECURSION TREE

one recurse(n-1)
→ O(n)

two recurse(n-1)
→ O(2^n)

one recurse(n/2)
→ O(log n)

two recurse(n/2)
→ O(n)

two recurse(n/2) + O(n) local work
→ O(n log n)

--------------------------------

TREE RULE

Total time
=
sum of work over all nodes

For uniform local work:
≈
#nodes × work/node

For changing local work:
sum level-by-level.

--------------------------------

HEIGHT

n → n-1
→ O(n)

n → n/2
→ O(log n)

--------------------------------

DO NOT CONFUSE

tree size = TIME clue

maximum active path = STACK SPACE clue
```
