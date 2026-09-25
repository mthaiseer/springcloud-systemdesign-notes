# Form 5 — Parity Constraint

> **Goal:** Recognize when a Codeforces statement depends only on
> whether values are **even or odd**, replace full values with `mod 2`,
> derive the parity algebra step by step, and reduce the problem to
> counting, invariants, mismatch balancing, or constructive casework.

<a id="table-of-contents"></a>

## Table of Contents

| Section | Topic                                                  |
|--------:|--------------------------------------------------------|
|       1 | [What is a Parity Constraint?](#parity-01)             |
|       2 | [Simple Example](#parity-02)                           |
|       3 | [Real-World Example — Alternating Seats](#parity-03)   |
|       4 | [Core Algebra — Modulo 2](#parity-04)                  |
|       5 | [Parity of Addition](#parity-05)                       |
|       6 | [Parity of Subtraction](#parity-06)                    |
|       7 | [Parity of Multiplication](#parity-07)                 |
|       8 | [Parity of a Sum](#parity-08)                          |
|       9 | [Parity Under ±2 / Even Changes](#parity-09)           |
|      10 | [Parity Flips Under ±1](#parity-10)                    |
|      11 | [Index Parity vs Value Parity](#parity-11)             |
|      12 | [Odd-Count Model](#parity-12)                          |
|      13 | [XOR View of Parity](#parity-13)                       |
|      14 | [Algorithmic Reduction Matrix](#parity-14)             |
|      15 | [Codeforces Mental Triggers](#parity-15)               |
|      16 | [Standard C++ Snippets — int vs long long](#parity-16) |
|      17 | [Curated Codeforces Benchmarks](#parity-17)            |
|      18 | [CF 1857A — Array Coloring](#parity-cf1857a)           |
|      19 | [CF 1367B — Even Array](#parity-cf1367b)               |
|      20 | [CF 1296A — Array with Odd Sum](#parity-cf1296a)       |
|      21 | [Compare the Three Parity Variants](#parity-21)        |
|      22 | [Parity Constraint — Recognition Map](#parity-22)      |
|      23 | [Instant Recognition Drill](#parity-23)                |
|      24 | [Mathematical Form to Memorize](#parity-24)            |
|      25 | [Common Parity Traps](#parity-25)                      |
|      26 | [Pattern Recognition](#parity-26)                      |
|      27 | [Contest Mental Compression](#parity-27)               |
|      28 | [Final One-Line Takeaway](#parity-28)                  |

------------------------------------------------------------------------

<a id="parity-01"></a>

## 1. What is a Parity Constraint?

Parity means:

``` text
EVEN or ODD
```

Mathematically:

``` text
x is even
⇔
x % 2 = 0

x is odd
⇔
x % 2 = 1
```

Typical Codeforces wording:

``` text
"even"
"odd"
"same parity"
"different parity"
"sum is even/odd"
"number of odd elements"
"index and value have same parity"
"operation changes a value by 1"
"operation changes a value by 2"
```

The main compression is:

``` text
FULL INTEGER
     ↓
ONLY x % 2 MATTERS
     ↓
0 = even
1 = odd
```

Example:

``` text
2, 100, 1000000
```

are identical from a parity perspective:

``` text
0, 0, 0
```

and:

``` text
3, 57, 999
```

all become:

``` text
1, 1, 1
```

------------------------------------------------------------------------

<a id="parity-02"></a>

## 2. Simple Example

Question:

``` text
Is 13 + 27 even or odd?
```

You do not need the complete sum.

Reduce each value modulo `2`:

``` text
13 % 2 = 1
27 % 2 = 1
```

Then:

``` text
(13+27)%2
=
(1+1)%2
=
0
```

Therefore:

``` text
13+27 is EVEN
```

General lesson:

``` text
for parity,
replace every number by number%2
```

------------------------------------------------------------------------

<a id="parity-03"></a>

## 3. Real-World Example — Alternating Seats

Imagine seats numbered:

``` text
0 1 2 3 4 5 6 7
```

Two seat groups:

``` text
even seats: 0 2 4 6
odd seats:  1 3 5 7
```

Suppose a rule says:

``` text
even-numbered students must sit on even seats
odd-numbered students must sit on odd seats
```

Then the only property that matters is:

``` text
studentNumber % 2
seatIndex % 2
```

Required:

``` text
studentNumber % 2
=
seatIndex % 2
```

This is exactly the mathematical structure behind many array
parity-placement problems.

------------------------------------------------------------------------

<a id="parity-04"></a>

## 4. Core Algebra — Modulo 2

Any integer can be written as either:

``` text
EVEN:
x = 2k

ODD:
x = 2k+1
```

for some integer `k`.

Why?

Division by `2` leaves only two possible remainders:

``` text
0 or 1
```

Therefore:

``` text
x = 2q + r

where:
r ∈ {0,1}
```

So:

``` text
r=0 → even
r=1 → odd
```

This is the foundation of parity algebra.

------------------------------------------------------------------------

<a id="parity-05"></a>

## 5. Parity of Addition

### Even + Even

``` text
x=2a
y=2b

x+y
=
2a+2b
=
2(a+b)
```

Therefore:

``` text
EVEN + EVEN = EVEN
```

### Odd + Odd

``` text
x=2a+1
y=2b+1

x+y
=
2a+1+2b+1
=
2(a+b+1)
```

Therefore:

``` text
ODD + ODD = EVEN
```

### Even + Odd

``` text
x=2a
y=2b+1

x+y
=
2a+2b+1
=
2(a+b)+1
```

Therefore:

``` text
EVEN + ODD = ODD
```

Summary:

| `x`  | `y`  | `x+y` |
|------|------|-------|
| even | even | even  |
| even | odd  | odd   |
| odd  | even | odd   |
| odd  | odd  | even  |

Modulo form:

``` text
(x+y)%2
=
((x%2)+(y%2))%2
```

------------------------------------------------------------------------

<a id="parity-06"></a>

## 6. Parity of Subtraction

The same parity table applies to subtraction.

``` text
even-even = even
odd-odd   = even
even-odd  = odd
odd-even  = odd
```

Therefore:

``` text
x-y is even
⇔
x and y have the same parity
```

and:

``` text
x-y is odd
⇔
x and y have different parity
```

This gives an important transformation:

``` text
(x-y)%2=0
        ↓
x%2 = y%2
```

------------------------------------------------------------------------

<a id="parity-07"></a>

## 7. Parity of Multiplication

### If at least one factor is even

``` text
x=2a
```

Then:

``` text
x*y
=
2a*y
=
2(ay)
```

so the product is even.

Therefore:

``` text
EVEN * anything = EVEN
```

### Odd × Odd

``` text
(2a+1)(2b+1)

=
4ab+2a+2b+1

=
2(2ab+a+b)+1
```

so:

``` text
ODD * ODD = ODD
```

Summary:

``` text
product is odd
⇔
EVERY factor is odd

product is even
⇔
AT LEAST ONE factor is even
```

------------------------------------------------------------------------

<a id="parity-08"></a>

## 8. Parity of a Sum

Suppose:

``` text
S = a1+a2+...+an
```

Even elements contribute:

``` text
0 modulo 2
```

Odd elements contribute:

``` text
1 modulo 2
```

Therefore:

``` text
S % 2
=
numberOfOddElements % 2
```

Hence:

``` text
sum is EVEN
⇔
number of odd elements is EVEN

sum is ODD
⇔
number of odd elements is ODD
```

Example:

``` text
A = [8,3,6,5,7]

odd values:
3,5,7

oddCount=3
```

So without computing the complete sum:

``` text
sum is odd
```

Indeed:

``` text
8+3+6+5+7=29
```

------------------------------------------------------------------------

<a id="parity-09"></a>

## 9. Parity Under ±2 / Even Changes

Suppose:

``` text
x → x+2
```

Then:

``` text
(x+2)%2
=
(x%2 + 0)%2
=
x%2
```

Therefore adding or subtracting any even value preserves parity.

``` text
x → x ± 2k
```

means:

``` text
parity unchanged
```

This often creates an invariant:

``` text
operation changes values only by even amounts
                ↓
each value's parity is invariant
```

------------------------------------------------------------------------

<a id="parity-10"></a>

## 10. Parity Flips Under ±1

Suppose:

``` text
x → x+1
```

If `x` is even:

``` text
even+1=odd
```

If `x` is odd:

``` text
odd+1=even
```

Therefore:

``` text
±1 operation
    ↓
FLIPS parity
```

Modulo view:

``` text
newParity
=
(x%2) XOR 1
```

Mental rule:

``` text
EVEN CHANGE → parity preserved
ODD CHANGE  → parity flipped
```

------------------------------------------------------------------------

<a id="parity-11"></a>

## 11. Index Parity vs Value Parity

Some problems require:

``` text
i%2 = a[i]%2
```

Meaning:

``` text
even index → even value
odd index  → odd value
```

A mismatch can be one of only two types:

``` text
Type A:
even index contains odd value

Type B:
odd index contains even value
```

One swap between a Type-A and Type-B position fixes both simultaneously.

Therefore:

``` text
countA must equal countB
```

and if possible:

``` text
minimum swaps = countA = countB
```

This is the core model behind CF 1367B.

------------------------------------------------------------------------

<a id="parity-12"></a>

## 12. Odd-Count Model

Instead of tracking all values, often track only:

``` text
oddCount
evenCount
```

where:

``` text
oddCount + evenCount = n
```

Useful deductions:

``` text
sum parity = oddCount % 2
```

and:

``` text
if oddCount > 0
→ at least one odd value exists

if evenCount > 0
→ at least one even value exists
```

This can collapse an entire array problem into two counters.

------------------------------------------------------------------------

<a id="parity-13"></a>

## 13. XOR View of Parity

Modulo-2 addition behaves like XOR:

``` text
0+0 mod2 = 0
0+1 mod2 = 1
1+0 mod2 = 1
1+1 mod2 = 0
```

Exactly:

``` text
0 XOR 0 = 0
0 XOR 1 = 1
1 XOR 0 = 1
1 XOR 1 = 0
```

Therefore:

``` text
(x+y)%2
=
(x%2) XOR (y%2)
```

For many values:

``` text
sumParity
=
parity(a1) XOR parity(a2) XOR ... XOR parity(an)
```

This is useful conceptually even when you implement with `sum%2`.

------------------------------------------------------------------------

<a id="parity-14"></a>

## 14. Algorithmic Reduction Matrix

| Mathematical Condition       | Meaning                   | Typical Technique    | Complexity |
|------------------------------|---------------------------|----------------------|-----------:|
| `x%2==0`                     | `x` is even               | Modulo check         |     `O(1)` |
| `x%2==1`                     | `x` is odd                | Modulo check         |     `O(1)` |
| `(x+y)%2==0`                 | Same parity               | Compare `x%2`, `y%2` |     `O(1)` |
| `(x+y)%2==1`                 | Different parity          | Compare parity       |     `O(1)` |
| `sum%2`                      | Depends only on odd count | Count odds           |     `O(n)` |
| `i%2==a[i]%2`                | Correct parity placement  | Count mismatch types |     `O(n)` |
| Operation `±2k`              | Parity invariant          | Invariant reasoning  |     varies |
| Operation `±1`               | Parity flips              | Count required flips |     varies |
| Need odd sum                 | Odd number of odd terms   | Count / casework     |     `O(n)` |
| Need equal-parity group sums | Total sum must be even    | Sum parity invariant |     `O(n)` |

------------------------------------------------------------------------

<a id="parity-15"></a>

## 15. Codeforces Mental Triggers

### Trigger 1 — “Sum must be even/odd”

``` text
sum parity
    ↓
count odd elements
    ↓
oddCount%2
```

Ask:

``` text
Do I really need the full sum,
or only the number of odd values?
```

### Trigger 2 — “Index parity must match value parity”

``` text
i%2 == a[i]%2
        ↓
count two mismatch types
        ↓
one opposite mismatch pair fixes both
```

### Trigger 3 — “Operation adds/copies/changes values”

Ask immediately:

``` text
Does this operation preserve parity?
Does it flip parity?
Can it introduce a parity that does not already exist?
```

------------------------------------------------------------------------

<a id="parity-16"></a>

## 16. Standard C++ Snippets — `int` vs `long long`

### Which type should you use?

| Situation                            | Recommended Type         | Reason                        |
|--------------------------------------|--------------------------|-------------------------------|
| `n`, index `i`, odd/even counters    | `int`                    | Usually small counts          |
| `a[i]` when constraints are small    | `int`                    | Enough for parity checks      |
| Sum of many potentially large values | `long long`              | Avoid sum overflow            |
| Only checking each `a[i]%2`          | `int` or `long long`     | Either works if input fits    |
| Product or large derived arithmetic  | `long long` / `__int128` | Avoid multiplication overflow |

For parity itself:

``` cpp
bool odd = (x % 2 != 0);
bool even = (x % 2 == 0);
```

### Safe parity for negative integers

Prefer:

``` cpp
x % 2 != 0
```

for oddness rather than:

``` cpp
x % 2 == 1
```

because in C++ a negative odd number can have remainder `-1`.

Example:

``` cpp
-3 % 2 == -1
```

So:

``` cpp
bool isOdd(long long x) {
    return x % 2 != 0;
}
```

### Count odds

``` cpp
int odd = 0;

for (long long x : a)
    odd += (x % 2 != 0);
```

Then:

``` cpp
bool sumIsOdd = (odd % 2 != 0);
```

### Index/value mismatch counting

``` cpp
int evenIndexOddValue = 0;
int oddIndexEvenValue = 0;

for (int i = 0; i < n; ++i) {
    if ((i & 1) == 0 && (a[i] & 1))
        ++evenIndexOddValue;

    if ((i & 1) == 1 && (a[i] & 1) == 0)
        ++oddIndexEvenValue;
}
```

[↑ Back to Table of Contents](#table-of-contents)

------------------------------------------------------------------------

<a id="parity-17"></a>

## 17. Curated Codeforces Benchmarks

| Problem                                                                           | Rating | What the Problem Is About                                                          | Parity Model            | Key Observation                                                                                                     | Technique                | C++ Type                         |
|-----------------------------------------------------------------------------------|-------:|------------------------------------------------------------------------------------|-------------------------|---------------------------------------------------------------------------------------------------------------------|--------------------------|----------------------------------|
| [CF 1857A — Array Coloring](https://codeforces.com/problemset/problem/1857/A)     |    800 | Split the array into two non-empty color groups whose sums have the same parity.   | `S1%2 = S2%2`           | If the two group sums have equal parity, their total sum must be even; if total is even, a valid split exists.      | Total-sum parity         | `long long` for sum              |
| [CF 1367B — Even Array](https://codeforces.com/problemset/problem/1367/B)         |    800 | Swap elements so every index has an element of the same parity.                    | `i%2 = a[i]%2`          | Opposite mismatch types must occur equally; one swap fixes one of each.                                             | Mismatch counting        | `int` sufficient for constraints |
| [CF 1296A — Array with Odd Sum](https://codeforces.com/problemset/problem/1296/A) |    800 | Using copy/assignment operations, decide whether the final array can have odd sum. | Odd sum ⇔ odd odd-count | Existing odd sum works immediately; otherwise both parities must exist so one assignment can flip odd-count parity. | Parity counts + casework | `int` sufficient for constraints |

These Codeforces statements are all rated 800; their official pages
describe the parity conditions and operations used below.

------------------------------------------------------------------------

<a id="parity-cf1857a"></a>

# 18. Variant 1 — CF 1857A: Array Coloring

Problem: <https://codeforces.com/problemset/problem/1857/A>

## What This Problem Is All About

You are given an array and must color every element using two colors.

Each color must receive at least one element.

Let:

``` text
S1 = sum of first color
S2 = sum of second color
```

You need:

``` text
S1 and S2 to have the same parity
```

At first this sounds like a partition problem:

``` text
Which elements should go into which group?
```

But the important observation is that the exact partition does **not**
matter.

Only the parity of the total sum matters.

Recognition trigger:

``` text
two group sums must have same parity
                ↓
S1%2 = S2%2
                ↓
what does that imply about S1+S2?
```

## A. Remove Story Nouns

``` text
colors          → two groups
colored values  → partition of array
blue sum        → S1
red sum         → S2
```

Abstract problem:

``` text
Partition all elements into two non-empty groups.

Need:
S1%2 = S2%2
```

## B. Define Variables

``` text
T  = total array sum
S1 = first group sum
S2 = second group sum
```

Because all elements belong to exactly one group:

``` text
T = S1+S2
```

## C. Core Mathematical Constraint

Required:

``` text
S1%2 = S2%2
```

There are only two possibilities.

### Both even

``` text
S1=2a
S2=2b
```

Then:

``` text
T
=
S1+S2
=
2a+2b
=
2(a+b)
```

So:

``` text
T is even
```

### Both odd

``` text
S1=2a+1
S2=2b+1
```

Then:

``` text
T
=
2a+1+2b+1
=
2(a+b+1)
```

Again:

``` text
T is even
```

Therefore:

``` text
same parity group sums
        ↓
total sum must be even
```

## D. Why Is Even Total Also Sufficient?

Suppose:

``` text
T is even
```

Because `n>=2`, put one element `a[0]` into the first group.

Then:

``` text
S1 = a[0]
S2 = T-a[0]
```

Modulo `2`:

``` text
S2%2
=
(T-a[0])%2
```

Since:

``` text
T%2=0
```

subtracting `a[0]` leaves the same parity as `a[0]`.

So:

``` text
S2%2 = S1%2
```

Thus:

``` text
T even
⇔
a valid coloring exists
```

## E. Key Observation

Do not construct the coloring.

``` text
partition story
    ↓
S1+S2=T
    ↓
S1,S2 same parity
    ↓
T even
```

## F. Solution

``` text
sum all elements

if total sum is even:
    YES
else:
    NO
```

## G. Horizontal Dry Run

``` text
A:       1   2   4   3   2   3   5   4
sum:                         24
24%2:                         0
answer:                      YES
```

Second example:

``` text
A:       4   7
sum:        11
11%2:        1
answer:     NO
```

## H. Pseudocode

``` text
READ n

sum=0

FOR each x:
    sum += x

IF sum%2==0:
    PRINT YES
ELSE:
    PRINT NO
```

## I. Complexity

``` text
Time:  O(n)
Space: O(1)
```

## J. Variant Lesson

``` text
STORY:
partition into two groups

CONDITION:
group sums have same parity

EQUATION:
T=S1+S2

DERIVATION:
even+even = even
odd+odd   = even

FINAL:
T must be even

ALGORITHM:
check total sum parity
```

[↑ Back to Table of Contents](#table-of-contents)

------------------------------------------------------------------------

<a id="parity-cf1367b"></a>

# 19. Variant 2 — CF 1367B: Even Array

Problem: <https://codeforces.com/problemset/problem/1367/B>

## What This Problem Is All About

An array is called good when:

``` text
index parity = value parity
```

That means:

``` text
index 0,2,4,... must contain even values
index 1,3,5,... must contain odd values
```

You may swap any two elements.

Need:

``` text
minimum swaps
```

or:

``` text
-1 if impossible
```

The problem is not about sorting values.

It is about pairing two opposite kinds of parity mistakes.

Recognition trigger:

``` text
position requires parity
        ↓
count wrong parity placements
        ↓
can opposite mistakes cancel via one swap?
```

## A. Remove Story Nouns

``` text
good array       → parity matches at every position
swap operation   → exchange two parity placements
```

Required:

``` text
i%2 = a[i]%2
```

## B. Define Mismatch Types

### Type A

``` text
i even
a[i] odd
```

Count:

``` text
A
```

### Type B

``` text
i odd
a[i] even
```

Count:

``` text
B
```

## C. Why One Swap Fixes Two Errors

Take:

``` text
even index holding odd value
```

and:

``` text
odd index holding even value
```

Before:

``` text
index:    even       odd
value:     odd       even
status:   WRONG      WRONG
```

Swap:

``` text
index:    even       odd
value:    even       odd
status:   GOOD       GOOD
```

Therefore:

``` text
one A + one B
→ one swap
→ both fixed
```

## D. Feasibility Derivation

Every Type-A mismatch needs a Type-B mismatch as its swap partner.

Therefore:

``` text
A = B
```

is necessary.

If:

``` text
A != B
```

some mismatch cannot be paired.

So:

``` text
impossible
```

If:

``` text
A=B
```

minimum swaps:

``` text
A
```

because every swap can fix at most one mismatch of each type.

## E. Key Observation

``` text
wrong positions
     ↓
classify by direction of parity mismatch
     ↓
evenIndex/oddValue  ↔  oddIndex/evenValue
     ↓
pair them
```

## F. Horizontal Dry Run

``` text
index:     0   1   2   3
required:  E   O   E   O
value:     3   2   7   6
actual:    O   E   O   E
type:      A   B   A   B

A=2
B=2

swap (0,1)
swap (2,3)

answer=2
```

Impossible example:

``` text
index:     0   1   2
required:  E   O   E
value:     3   2   6
actual:    O   E   E
type:      A   B   OK

A=1
B=1

answer=1
```

Single value:

``` text
index:     0
required:  E
value:     7
actual:    O
type:      A

A=1
B=0

answer=-1
```

## G. Pseudocode

``` text
A=0
B=0

FOR i=0 ... n-1:

    IF i%2 != a[i]%2:

        IF i%2==0:
            A++

        ELSE:
            B++

IF A!=B:
    PRINT -1
ELSE:
    PRINT A
```

## H. Complexity

``` text
Time:  O(n)
Space: O(1)
```

## I. Variant Lesson

``` text
CONDITION:
i%2=a[i]%2

MISMATCHS:
even index + odd value
odd index  + even value

ONE SWAP:
fixes one of each

FEASIBILITY:
counts must match

ANSWER:
number of either mismatch type
```

[↑ Back to Table of Contents](#table-of-contents)

------------------------------------------------------------------------

<a id="parity-cf1296a"></a>

# 20. Variant 3 — CF 1296A: Array with Odd Sum

Problem: <https://codeforces.com/problemset/problem/1296/A>

## What This Problem Is All About

You have an array.

An operation lets you choose different indices `i,j` and copy:

``` text
a[i] := a[j]
```

You may perform the operation any number of times.

Need to determine whether it is possible to end with:

``` text
odd total sum
```

The exact values matter much less than whether the array contains:

``` text
odd values
even values
```

Recognition trigger:

``` text
target = odd sum
       ↓
odd number of odd elements
       ↓
how can assignment change oddCount parity?
```

## A. Remove Story Nouns

``` text
array values      → parity values 0/1
assignment        → replace one parity by another existing parity
target odd sum    → odd number of odd elements
```

## B. Define Variables

``` text
odd  = number of odd elements
even = number of even elements
n    = odd+even
```

Target:

``` text
oddCount%2 = 1
```

because:

``` text
sum%2 = oddCount%2
```

## C. Case 1 — Sum Already Odd

If:

``` text
odd%2=1
```

then the current sum is already odd.

No operation is needed.

``` text
YES
```

## D. Case 2 — All Values Even

If:

``` text
odd=0
```

then every source value is even.

Assignment:

``` text
a[i] := a[j]
```

can only copy another even value.

So no odd value can ever appear.

Therefore the sum can never become odd.

``` text
NO
```

## E. Case 3 — All Values Odd

Suppose:

``` text
even=0
```

Every assignment copies an odd value over another odd value.

Therefore every array element remains odd.

The number of odd elements stays:

``` text
n
```

If the current sum is even, then:

``` text
n is even
```

and it remains even.

Therefore:

``` text
NO
```

in this case when the sum is not already odd.

## F. Case 4 — Both Parities Exist

Suppose:

``` text
odd>0
even>0
```

If the current sum is even, `odd` is even.

Copy an odd value into one even position:

``` text
even → odd
```

Then:

``` text
oddCount
→
oddCount+1
```

Even count of odds becomes odd.

Therefore total sum becomes odd.

So:

``` text
YES
```

## G. Final Condition

``` text
if oddCount is odd:
    YES

else if odd>0 AND even>0:
    YES

else:
    NO
```

Equivalent:

``` text
YES if:
current sum is odd
OR
both an odd and an even value exist
```

## H. Horizontal Dry Run

### Example 1

``` text
A:          2   3
parity:     E   O
oddCount:       1
sum parity:   ODD

answer: YES
```

### Example 2

``` text
A:          2   2   8   8
parity:     E   E   E   E
oddCount:           0
sum parity:        EVEN

no odd source exists
→ cannot create odd value
→ NO
```

### Example 3

``` text
A:          5   5   5   5
parity:     O   O   O   O
oddCount:           4
sum parity:        EVEN

no even value exists
→ assignment O→O changes nothing
→ NO
```

## I. Pseudocode

``` text
odd=0
even=0

FOR each x:

    IF x%2!=0:
        odd++
    ELSE:
        even++

IF odd%2==1:
    PRINT YES

ELSE IF odd>0 AND even>0:
    PRINT YES

ELSE:
    PRINT NO
```

## J. Complexity

``` text
Time:  O(n)
Space: O(1)
```

## K. Variant Lesson

``` text
TARGET:
odd sum

PARITY FORM:
oddCount must be odd

OPERATION:
copy existing parity

OBSERVATION:
if both parities exist,
one assignment can change oddCount by 1

ALGORITHM:
count odd/even + casework
```

[↑ Back to Table of Contents](#table-of-contents)

------------------------------------------------------------------------

<a id="parity-21"></a>

# 21. Compare the Three Parity Variants

| Problem  | Base Parity Form | Extra Constraint                         | Main Transformation           | Observation                                                      | Algorithm         |
|----------|------------------|------------------------------------------|-------------------------------|------------------------------------------------------------------|-------------------|
| CF 1857A | `S1%2=S2%2`      | Partition into two non-empty groups      | `T=S1+S2`                     | Equal parity sums imply even total, and even total is sufficient | Sum parity        |
| CF 1367B | `i%2=a[i]%2`     | Arbitrary swaps                          | Count opposite mismatch types | One swap fixes one mismatch of each type                         | Mismatch counting |
| CF 1296A | `sum%2=1`        | Copy one existing value to another index | `sum%2=oddCount%2`            | Need odd odd-count; both parities allow a flip                   | Odd/even counts   |

------------------------------------------------------------------------

<a id="parity-22"></a>

# 22. Parity Constraint — Recognition Map

``` text
                          PARITY CONSTRAINT
                                 |
        +------------------------+-----------------------+
        |                        |                       |
      VALUE                    SUM                    POSITION
     x%2                     sum%2                  i%2 vs a[i]%2
        |                        |                       |
   even / odd              count odds               mismatch types
        |                        |                       |
   local property          oddCount%2             pair opposite errors


OPERATIONS
    |
    +-- add/subtract even → parity preserved
    |
    +-- add/subtract odd  → parity flipped
    |
    +-- copy value        → only existing parity can be copied
    |
    +-- swap              → parity multiset preserved
```

------------------------------------------------------------------------

<a id="parity-23"></a>

# 23. Instant Recognition Drill

| English Phrase                 | Mathematical Translation |
|--------------------------------|--------------------------|
| “x is even”                    | `x%2=0`                  |
| “x is odd”                     | `x%2!=0`                 |
| “same parity”                  | `x%2=y%2`                |
| “different parity”             | `x%2!=y%2`               |
| “sum is odd”                   | `oddCount%2=1`           |
| “sum is even”                  | `oddCount%2=0`           |
| “index and value parity match” | `i%2=a[i]%2`             |
| “add 2 each operation”         | parity invariant         |
| “add 1 each operation”         | parity flips             |

### Mental drills

``` text
1. odd + odd = ?
2. odd + even = ?
3. When is a product odd?
4. Array has 7 odd values. Sum parity?
5. x-y is even. Relation between x and y parity?
6. Add 6 to x. Does parity change?
7. Add 5 to x. Does parity change?
```

### Answers

``` text
1. even
2. odd
3. only when every factor is odd
4. odd
5. same parity
6. no
7. yes
```

------------------------------------------------------------------------

<a id="parity-24"></a>

# 24. Mathematical Form to Memorize

``` text
FORM:
Parity Constraint

EVEN:
x%2=0
x=2k

ODD:
x%2!=0
x=2k+1

SUM:
(x+y)%2
=
((x%2)+(y%2))%2

SAME PARITY:
x%2=y%2
⇔
x-y is even
⇔
x+y is even

DIFFERENT PARITY:
x%2!=y%2
⇔
x-y is odd
⇔
x+y is odd

ARRAY SUM:
sum%2
=
oddCount%2

PRODUCT:
product odd
⇔
all factors odd

EVEN CHANGE:
x → x+2k
→ parity preserved

ODD CHANGE:
x → x+(2k+1)
→ parity flipped
```

Core mental model:

``` text
FULL VALUES
    ↓
MODULO 2
    ↓
ONLY 0/1 STATES
    ↓
COUNT / INVARIANT / CASEWORK
```

------------------------------------------------------------------------

<a id="parity-25"></a>

# 25. Common Parity Traps

### Trap 1 — Computing values when only parity matters

Instead of:

``` text
calculate huge sum
then check sum%2
```

sometimes simply:

``` text
count odd values
```

is enough.

### Trap 2 — Assuming odd + odd is odd

Wrong:

``` text
odd+odd = odd
```

Correct:

``` text
odd+odd = even
```

### Trap 3 — Forgetting operation effect

Always classify the operation:

``` text
changes by even amount?
→ parity invariant

changes by odd amount?
→ parity flips

swaps values?
→ global odd/even counts preserved

copies values?
→ cannot introduce a parity absent from all source values
```

### Trap 4 — Negative odd check in C++

Avoid relying on:

``` cpp
x % 2 == 1
```

for arbitrary signed integers.

Use:

``` cpp
x % 2 != 0
```

### Trap 5 — Confusing parity of value with parity of count

These are different:

``` text
a[i]%2
```

versus:

``` text
oddCount%2
```

Many array problems first reduce values to parity and then reduce the
**count** again modulo `2`.

------------------------------------------------------------------------

<a id="parity-26"></a>

# 26. Pattern Recognition

### SIGNAL

``` text
even
odd
same parity
different parity
odd sum
even sum
alternating parity
index parity
```

### MATH

``` text
x%2
sum%2
oddCount%2
i%2 vs a[i]%2
```

### THINK

``` text
Can I throw away the actual magnitudes?

Does only x%2 matter?

Can I count odd/even values?

Does the operation preserve or flip parity?

Is there a parity invariant?

Are there two opposite mismatch types?

Can I derive the condition using x=2k or x=2k+1?
```

### TYPICAL SOLUTIONS

``` text
counting
casework
greedy mismatch pairing
invariants
constructive parity arrangement
prefix parity
XOR
```

------------------------------------------------------------------------

<a id="parity-27"></a>

# 27. Contest Mental Compression

``` text
READ ENGLISH
     ↓
spot EVEN / ODD / ±1 / ±2 / SAME PARITY
     ↓
replace values by x%2
     ↓
WRITE PARITY EQUATION
     ↓
0 = even
1 = odd
     ↓
analyze operation
preserve? flip? swap? copy?
     ↓
reduce to
oddCount / mismatchCount / invariant
     ↓
casework
     ↓
O(n) or O(1) solution
```

Ultra-compressed:

``` text
"even/odd" → mod 2 → count/invariant → operation effect → casework
```

------------------------------------------------------------------------

<a id="parity-28"></a>

# 28. Final One-Line Takeaway

**When a problem cares about even and odd behavior, discard magnitude
first and model every value as `0/1 mod 2`; the remaining problem is
usually counting, an invariant, or a tiny case split.**
