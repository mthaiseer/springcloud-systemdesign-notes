# Form 1 — Sum Constraint

> **Goal:** Learn to recognize, derive, visualize, and apply the **Sum Constraint** mathematical form in competitive programming.

---

## 1. What is a Sum Constraint?

A **sum constraint** appears when two or more quantities must add up to a fixed value or satisfy a bound.

```text
x + y = S
x + y <= S
x + y >= S
```

Typical Codeforces wording:

```text
"total is S"
"together they have S"
"remaining amount"
"choose two values whose sum is..."
"sum must not exceed..."
```

---

## 2. Simple Example

Suppose:

```text
x + y = 10
```

If we know `x = 4`, what is `y`?

Start:

```text
x + y = 10
```

Substitute `x = 4`:

```text
4 + y = 10
```

Subtract `4` from both sides:

```text
y = 10 - 4
y = 6
```

Therefore:

```text
KNOWN TOTAL - KNOWN PART = MISSING PART
```

General derivation:

```text
x + y = S

subtract x from both sides

x + y - x = S - x

y = S - x
```

---

## 3. Real-World Example — Shopping Budget

You have **100 lei**.

You spend `x = 35` lei on groceries.

How much remains?

```text
spent + remaining = total

x + y = 100
```

Substitute:

```text
35 + y = 100
```

Therefore:

```text
y = 100 - 35
  = 65
```

Visual:

```text
Total = 100

|------ spent ------|---------- remaining ----------|
0                   35                              100
                    <----------- 65 ---------------->

35 + 65 = 100
```

So whenever you see:

```text
total
one known part
one missing part
```

think:

```text
missing = total - known
```

---

## 4. Inequality Form — Upper Bound

Suppose the statement says:

> The total of `x` and `y` cannot exceed `K`.

Translate the English:

```text
cannot exceed K
      ↓
at most K
      ↓
<= K
```

Therefore:

```text
x + y <= K
```

Now isolate `y`.

Start:

```text
x + y <= K
```

Subtract `x` from both sides:

```text
x + y - x <= K - x
```

Therefore:

```text
y <= K - x
```

Meaning:

```text
after choosing x,
the maximum amount available for y
is K - x
```

### Numerical Example

```text
x + y <= 10

x = 6

6 + y <= 10

y <= 10 - 6

y <= 4
```

Visual:

```text
capacity = 10

|------ x=6 ------|---- space for y ----|
0                 6                     10

                  y <= 4
```

---

## 5. Inequality Form — Lower Bound

Suppose:

```text
x + y >= K
```

Solve for `y`:

```text
x + y >= K

subtract x from both sides

y >= K - x
```

Example:

```text
x + y >= 10
x = 6

6 + y >= 10

y >= 4
```

So the three core forms are:

```text
x + y = K   →   y = K - x

x + y <= K  →   y <= K - x

x + y >= K  →   y >= K - x
```

---

## 6. Competitive Programming Mathematical Model

A common transformation is:

```text
Given:

a + b = S
```

Instead of searching independently for both `a` and `b`:

```text
choose a
   ↓
b is forced

b = S - a
```

This reduces:

```text
two unknown values
       ↓
one free variable
```

That is often the important observation.

---

## 7. Pair-Sum Application

Suppose an array contains values and we need two elements whose sum is `S`.

Mathematically:

```text
a + b = S
```

If we currently know `a`, isolate `b`:

```text
a + b = S

subtract a from both sides

b = S - a
```

Therefore, instead of asking:

```text
Which two numbers make S?
```

ask:

```text
For this a,
does S - a exist?
```

This can lead to:

```text
Hash Set
Frequency Map
Two Pointers after sorting
```

depending on the problem.

---

## 8. Horizontal Dry Run

Suppose:

```text
A = [2, 7, 4, 1]
S = 9
```

Need:

```text
a + b = 9
```

Derive the required partner:

```text
a + b = 9

subtract a

b = 9 - a
```

Horizontal dry run:

```text
a:       2      7      4      1
need:    7      2      5      8
         ↑      ↑
       exists exists
```

Therefore:

```text
2 + 7 = 9
```

The formula:

```text
need = S - a
```

is not a memorized trick. It comes directly from isolating `b`.

---

## 9. Pseudocode

```text
seen = empty set

FOR each x:

    need = S - x

    IF need exists in seen:
        pair found

    insert x into seen
```

The important line:

```text
need = S - x
```

comes from:

```text
x + need = S

subtract x from both sides

need = S - x
```

---

## 10. Complexity of the Hash-Set Application

For `n` array elements:

```text
Each element processed once       → O(n)
Hash lookup average               → O(1)

Total time                        → O(n)
Extra space                       → O(n)
```

If sorting + two pointers is used:

```text
Sorting                           → O(n log n)
Two-pointer scan                  → O(n)

Total time                        → O(n log n)
```

---

## 11. General Mathematical Rule to Remember

```text
FORM 1 — SUM CONSTRAINT
```

### Exact Sum

```text
x + y = S
    ↓
y = S - x
```

### Upper-Bounded Sum

```text
x + y <= S
      ↓
y <= S - x
```

### Lower-Bounded Sum

```text
x + y >= S
      ↓
y >= S - x
```

---

## 12. Mental Meaning

```text
TOTAL = PART + PART

             ↓

MISSING PART = TOTAL - KNOWN PART
```

Another useful interpretation:

```text
fixed total
    ↓
choose one quantity
    ↓
remaining quantity becomes determined
```

---

## 13. Contest Signals

Look for phrases such as:

```text
"sum equals"
"total is"
"together equal"
"remaining"
"at most total"
"at least total"
"find another value so their sum..."
"two values add up to..."
```

Translate them immediately into:

```text
x + y = S

or

x + y <= S

or

x + y >= S
```

---

## 14. Pattern Recognition

### SIGNAL

```text
Two quantities contribute to one total.
One quantity is known/chosen.
Need to determine or constrain the other.
```

### MATH

```text
x + y = S
```

### THINK

```text
Can I isolate one variable?

If x is known, what must y be?
```

### TRANSFORMATION

```text
x + y = S
    ↓
y = S - x
```

### PATTERN

```text
Complement / Missing Part
```

### TYPICAL SOLUTIONS

```text
Hash lookup
Frequency map
Two pointers
Counting
Greedy capacity reasoning
```

---

## 15. Contest Mental Compression

```text
English:

"Two values must total S"

        ↓

Mathematics:

x + y = S

        ↓

Isolate unknown:

y = S - x

        ↓

Observation:

choosing x determines y

        ↓

Algorithmic question:

does S - x exist?
```

Compressed:

```text
SUM CONSTRAINT
      ↓
x + y = S
      ↓
isolate unknown
      ↓
y = S - x
      ↓
known x determines required y
      ↓
search / lookup / two pointers
```

---

## 16. Mathematical Form to Memorize

```text
FORM: Sum Constraint

EXACT:
x + y = S
→ y = S - x

UPPER BOUND:
x + y <= S
→ y <= S - x

LOWER BOUND:
x + y >= S
→ y >= S - x
```

### Core Mental Model

```text
TOTAL = KNOWN + UNKNOWN

UNKNOWN = TOTAL - KNOWN
```

---

## 17. Final One-Line Takeaway

**When a sum is fixed, choosing one part immediately determines the other as `total - chosen`.**


---

# 18. Codeforces Variants — From Story to Mathematical Model

The goal of these examples is not to memorize the solutions. The goal is to see how the same **sum constraint** appears in different-looking statements.

---

## Variant 1 — Direct Sum Equality

### Codeforces 1742A — Sum

Problem: https://codeforces.com/problemset/problem/1742/A

### A. Remove the Story / Nouns

Original idea:

```text
Given three integers.
Determine whether one of them equals the sum of the other two.
```

There is almost no story, so reduce it immediately to:

```text
Given three values x, y, z.

Need to know whether:

one value = other value + other value
```

### B. Define Variables

```text
a = first number
b = second number
c = third number
```

Target:

```text
Does any one of these hold?

a + b = c
a + c = b
b + c = a
```

### C. Algebraic Formulation

The core form is:

```text
x + y = S
```

But we do not know which of `a, b, c` is the total.

Therefore there are three possibilities:

```text
a is total:
b + c = a

b is total:
a + c = b

c is total:
a + b = c
```

Equivalent complement view:

```text
a + b = c
→ b = c - a

a + c = b
→ c = b - a

b + c = a
→ c = a - b
```

### D. Observation

There are only **three numbers**, so there is no need for sorting, hashing, or searching.

Just test all three possible choices for which number is the total.

```text
(a+b==c)
OR
(a+c==b)
OR
(b+c==a)
```

### E. Solution

```text
If any pair sums to the remaining value:
    YES
otherwise:
    NO
```

### F. Horizontal Dry Run

Example:

```text
a = 9   b = 11   c = 20

check:

a+b=c   → 9+11=20 → TRUE
a+c=b   → not needed
b+c=a   → not needed

answer = YES
```

Another example:

```text
a = 2   b = 5   c = 8

2+5 = 7 ≠ 8
2+8 = 10 ≠ 5
5+8 = 13 ≠ 2

answer = NO
```

### G. Pseudocode

```text
READ a, b, c

IF a + b == c
   OR a + c == b
   OR b + c == a:

    PRINT YES
ELSE:
    PRINT NO
```

### H. Variant Lesson

```text
SUM FORM:
x + y = S

VARIANT:
The total S is not identified.

ACTION:
Try each candidate as S.
```

---

## Variant 2 — Sum Constraint + Parity Constraint

### Codeforces 4A — Watermelon

Problem: https://codeforces.com/problemset/problem/4/A

### A. Remove Story Nouns

Story objects:

```text
watermelon        → total value
two pieces        → x and y
weight            → integer value
both pieces even  → parity constraints
positive pieces   → x > 0 and y > 0
```

Abstract problem:

```text
Given integer w.

Can we find positive integers x and y such that:

x + y = w

x is even
y is even
```

### B. Define Variables

```text
w = total weight
x = first part
y = second part
```

Constraints:

```text
x + y = w

x > 0
y > 0

x % 2 = 0
y % 2 = 0
```

### C. Algebraic Derivation

From the sum constraint:

```text
x + y = w
```

isolate `y`:

```text
y = w - x
```

Now use the parity requirements.

Because both values must be even, write:

```text
x = 2p
y = 2q
```

for positive integers `p` and `q`.

Substitute into:

```text
x + y = w
```

giving:

```text
2p + 2q = w
```

factor out `2`:

```text
2(p + q) = w
```

Therefore:

```text
w must be even
```

But that alone is not sufficient.

Both pieces must be **positive even integers**.

The smallest positive even value is:

```text
2
```

Therefore the smallest possible total is:

```text
2 + 2 = 4
```

So:

```text
w must be even
AND
w >= 4
```

Equivalent Codeforces condition:

```text
w % 2 == 0
AND
w > 2
```

### D. Observation

The story sounds like a splitting problem, but we do **not** need to search over all splits.

The mathematical constraints tell us exactly when a split exists:

```text
even total + enough room for two positive even parts
```

Hence:

```text
w is even and w > 2
```

### E. Solution

```text
IF w is even AND w > 2:
    YES
ELSE:
    NO
```

### F. Horizontal Dry Run

```text
w:          2       4       6       8       9
even?:      yes     yes     yes     yes     no
w > 2?:     no      yes     yes     yes     yes
possible?:  NO      YES     YES     YES     NO
split:      --      2+2     2+4     2+6     --
```

For `w = 8`:

```text
x + y = 8

choose smallest valid even x:

x = 2

y = 8 - 2
  = 6

2 and 6 are both positive even numbers.

YES
```

### G. Pseudocode

```text
READ w

IF w % 2 == 0 AND w > 2:
    PRINT YES
ELSE:
    PRINT NO
```

### H. Variant Lesson

```text
SUM FORM:
x + y = w

EXTRA CONSTRAINT:
x and y must both be positive even numbers

DERIVATION:
x=2p, y=2q
→ w=2(p+q)
→ w must be even

BOUND:
minimum = 2+2 = 4

FINAL:
w even AND w >= 4
```

---

## Variant 3 — Bounded Sum / Pair Enumeration

### Codeforces 1941A — Rudolf and the Ticket

Problem: https://codeforces.com/problemset/problem/1941/A

### A. Remove Story Nouns

Story objects:

```text
left-pocket coins   → array A
right-pocket coins  → array B
ticket cost limit   → k
choose one from each pocket → choose a ∈ A and b ∈ B
```

Abstract problem:

```text
Given arrays A and B and limit k.

Count pairs (a,b) such that:

a ∈ A
b ∈ B

and

a + b <= k
```

### B. Define Variables

```text
n = size of A
m = size of B
k = maximum allowed sum

A[i] = candidate from first array
B[j] = candidate from second array

ans = number of valid pairs
```

Core constraint:

```text
A[i] + B[j] <= k
```

### C. Algebraic Derivation

Start:

```text
A[i] + B[j] <= k
```

Suppose `A[i]` is fixed.

Subtract `A[i]` from both sides:

```text
A[i] + B[j] - A[i] <= k - A[i]
```

Therefore:

```text
B[j] <= k - A[i]
```

This means:

```text
after choosing A[i],
the largest allowed B[j] is k - A[i]
```

So the pair condition can be viewed in two equivalent ways:

```text
A[i] + B[j] <= k
```

or:

```text
B[j] <= k - A[i]
```

### D. Observation

The constraints of this problem are small enough that every pair can simply be checked.

The mathematical model tells us exactly what to test:

```text
for each a
    for each b
        check a+b <= k
```

For larger constraints, the transformed form:

```text
b <= k-a
```

could suggest sorting + binary search or two pointers.

But here brute-force pair enumeration is sufficient.

### E. Solution

```text
ans = 0

For every A[i]:
    For every B[j]:

        if A[i] + B[j] <= k:
            ans++
```

### F. Horizontal Dry Run

Example:

```text
A = [1, 2]
B = [2, 3]
k = 4
```

Check all pairs:

```text
pair:    (1,2)   (1,3)   (2,2)   (2,3)
sum:        3       4       4       5
<= 4?:     YES     YES     YES      NO

ans = 3
```

Now view the same example using the complement form:

```text
For a = 1:

b <= 4-1
b <= 3

valid B: 2,3


For a = 2:

b <= 4-2
b <= 2

valid B: 2
```

Therefore:

```text
2 valid pairs + 1 valid pair = 3
```

### G. Pseudocode

```text
READ n, m, k
READ A
READ B

ans = 0

FOR each a in A:

    FOR each b in B:

        IF a + b <= k:
            ans = ans + 1

PRINT ans
```

### H. Variant Lesson

```text
SUM FORM:
a + b <= k

ISOLATE b:
b <= k - a

MEANING:
choosing a creates an upper bound for b

SMALL INPUT:
enumerate every pair

LARGE INPUT:
consider sorting + binary search / two pointers
```

---

# 19. Compare the Three Sum-Constraint Variants

| Variant | Mathematical Form | Extra Condition | Main Observation | Solution Shape |
|---|---|---|---|---|
| CF 1742A — Sum | `x+y=S` | Unknown which value is `S` | Only 3 candidates for the total | Test 3 equations |
| CF 4A — Watermelon | `x+y=w` | `x,y` positive and even | Even + even is even; minimum total is 4 | Parity + lower bound |
| CF 1941A — Rudolf and the Ticket | `a+b<=k` | Count all valid cross-array pairs | Fix `a` → `b<=k-a` | Pair enumeration |

---

# 20. Sum Constraint — Variant Recognition Map

```text
                        SUM CONSTRAINT
                              |
          +-------------------+-------------------+
          |                   |                   |
       EXACT SUM          UPPER BOUND         LOWER BOUND
       x + y = S          x + y <= S          x + y >= S
          |                   |                   |
       y = S-x            y <= S-x            y >= S-x
          |                   |                   |
   required partner      maximum allowed      minimum required
```

Then look for **extra constraints**:

```text
x+y=S
  |
  +-- only a few values?       → test possibilities
  |
  +-- parity restriction?      → substitute x=2p / x=2p+1
  |
  +-- positive values?         → derive minimum possible total
  |
  +-- need pair existence?     → complement lookup S-x
  |
  +-- count pairs?             → enumerate / sort / binary search / two pointers
  |
  +-- x+y<=S?                  → fixing x gives y<=S-x
```

---

# 21. Final Contest Compression

```text
READ ENGLISH
     ↓
REMOVE STORY NOUNS
     ↓
identify quantities x, y, total S
     ↓
WRITE SUM CONSTRAINT
     ↓
x+y=S      x+y<=S      x+y>=S
     ↓
ISOLATE ONE VARIABLE
     ↓
y=S-x      y<=S-x      y>=S-x
     ↓
ADD EXTRA CONDITIONS
parity / positivity / membership / counting
     ↓
OBSERVATION
     ↓
choose the simplest algorithm allowed by constraints
```

**Core lesson:** The same sum equation can produce very different algorithms once parity, bounds, counting, or candidate-selection constraints are added.
