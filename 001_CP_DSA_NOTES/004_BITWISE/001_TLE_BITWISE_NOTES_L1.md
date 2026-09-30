# TLE Bit Manipulation - Step-by-Step Visual Handbook

> Based primarily on the two uploaded TLE lectures: **Bit Manipulation -
> 1** and **Bit Manipulation - 2** by Harsh Gupta.\
> I have expanded the lecture ideas into a revision + problem-solving
> handbook with ASCII diagrams, real-world analogies, recognition
> signals, C++ templates, and worked examples.
>
> **Indexing convention in this handbook:** unless a problem explicitly
> says otherwise, programming bits are **0-based from the right**.\
> TLE's "Kth bit" slides use a **1-based K**, so `Kth bit` there
> corresponds to programming index `K-1`.

------------------------------------------------------------------------

## Table of Contents

1.  [Mental Model: What Is a Bit?](#1-mental-model-what-is-a-bit)
2.  [Number Systems](#2-number-systems)
3.  [Decimal to Binary / Octal / Hex](#3-decimal-to-binary--octal--hex)
4.  [Any Base to Decimal](#4-any-base-to-decimal)
5.  [OR - Combine Features](#5-or---combine-features)
6.  [AND - Keep Common Features](#6-and---keep-common-features)
7.  [XOR - Difference / Toggle /
    Parity](#7-xor---difference--toggle--parity)
8.  [Left Shift](#8-left-shift)
9.  [Right Shift](#9-right-shift)
10. [Core Bitwise Properties](#10-core-bitwise-properties)
11. [Check Odd / Even](#11-check-odd--even)
12. [Power of Two](#12-power-of-two)
13. [Set the Kth Bit](#13-set-the-kth-bit)
14. [Check the Kth Bit](#14-check-the-kth-bit)
15. [Toggle the Kth Bit](#15-toggle-the-kth-bit)
16. [Unset / Clear the Kth Bit](#16-unset--clear-the-kth-bit)
17. [Bit Masks - The Universal Mental
    Model](#17-bit-masks---the-universal-mental-model)
18. [XOR Cancellation](#18-xor-cancellation)
19. [XOR and Parity](#19-xor-and-parity)
20. [Addition Identities with Bits](#20-addition-identities-with-bits)
21. [Remainder Modulo Powers of Two](#21-remainder-modulo-powers-of-two)
22. [TLE Problem: Make Almost Equal With
    Mod](#22-tle-problem-make-almost-equal-with-mod)
23. [Recognition Signals for CF](#23-recognition-signals-for-cf)
24. [C++ Bit Toolkit](#24-c-bit-toolkit)
25. [Common Bugs and Precedence
    Traps](#25-common-bugs-and-precedence-traps)
26. [One-Page Revision Map](#26-one-page-revision-map)
27. [Practice Ladder](#27-practice-ladder)
28. [References](#28-references)

------------------------------------------------------------------------

# 1. Mental Model: What Is a Bit?

A bit is simply a switch.

``` text
0 = OFF
1 = ON
```

A number is a row of switches. Each switch has a value determined by its
position.

``` text
bit index:    5    4    3    2    1    0
weight:      32   16    8    4    2    1

binary:       1    1    0    1    0    1
              |    |         |         |
             32 + 16       + 4       + 1

decimal = 53
```

The rightmost bit is the **least significant bit (LSB)**. The leftmost
active bit is the **most significant bit (MSB)**.

### Real-world mapping: control panel

Imagine six switches controlling six independent devices:

``` text
bit:      5      4      3      2      1      0
device:  AC    Light   TV    Fan    Pump   Alarm
state:    1      1      0      1      0      1
```

The integer is just a compact way of storing all six ON/OFF states.

This is why bit manipulation appears in: - permissions, - subsets, -
feature flags, - state compression, - masks, - parity, - XOR problems.

------------------------------------------------------------------------

# 2. Number Systems

The first TLE lecture begins with positional number systems.

``` text
Decimal      base 10   digits 0..9
Binary       base  2   digits 0..1
Octal        base  8   digits 0..7
Hexadecimal  base 16   digits 0..9, A..F
```

Examples:

``` text
9 decimal  = 1001 binary
9 decimal  = 11 octal
10 decimal = A hexadecimal
```

For hexadecimal:

``` text
A = 10
B = 11
C = 12
D = 13
E = 14
F = 15
```

The key idea is always:

``` text
digit × base^position
```

For binary:

``` text
101101
||||||
|||||+-- 1 × 2^0
||||+--- 0 × 2^1
|||+---- 1 × 2^2
||+----- 1 × 2^3
|+------ 0 × 2^4
+------- 1 × 2^5
```

------------------------------------------------------------------------

# 3. Decimal to Binary / Octal / Hex

TLE uses `55` as the main conversion example.

## 3.1 Decimal -\> binary

Repeatedly divide by 2 and collect remainders.

``` text
55 / 2 = 27 remainder 1
27 / 2 = 13 remainder 1
13 / 2 =  6 remainder 1
 6 / 2 =  3 remainder 0
 3 / 2 =  1 remainder 1
 1 / 2 =  0 remainder 1
```

Read remainders **bottom -\> top**:

``` text
55 = 110111₂
```

Why?

``` text
110111
= 32 + 16 + 0 + 4 + 2 + 1
= 55
```

## 3.2 Decimal -\> octal

Divide by 8:

``` text
55 / 8 = 6 remainder 7
 6 / 8 = 0 remainder 6

=> 67₈
```

## 3.3 Decimal -\> hexadecimal

Divide by 16:

``` text
55 / 16 = 3 remainder 7
 3 / 16 = 0 remainder 3

=> 37₁₆
```

### General algorithm

``` text
number N, target base B

while N > 0:
    digit = N % B
    save digit
    N = N / B

reverse saved digits
```

------------------------------------------------------------------------

# 4. Any Base to Decimal

Go in the opposite direction: multiply every digit by its positional
weight.

## Binary example

``` text
110111₂

position:  5   4   3   2   1   0
digit:     1   1   0   1   1   1
weight:   32  16   8   4   2   1

= 1×32 + 1×16 + 0×8 + 1×4 + 1×2 + 1×1
= 55
```

## Hex example

``` text
37₁₆

= 3 × 16^1 + 7 × 16^0
= 48 + 7
= 55
```

## General form

For digits:

``` text
d[k] d[k-1] ... d[1] d[0]
```

in base `B`:

``` text
value = d[k]×B^k + ... + d[1]×B + d[0]
```

------------------------------------------------------------------------

# 5. OR - Combine Features

TLE defines OR as: for each bit position, the result is set if **at
least one** input bit is set.

Symbol:

``` cpp
A | B
```

Truth table:

``` text
A B | A|B
----+----
0 0 |  0
0 1 |  1
1 0 |  1
1 1 |  1
```

Example:

``` text
A = 101100
B = 010101
    ------
A|B
  = 111101
```

### Real-world mapping: union of permissions

Suppose:

``` text
READ  = bit 0
WRITE = bit 1
ADMIN = bit 2

Alice = 001  -> READ
Bob   = 110  -> WRITE + ADMIN
```

Combine capabilities:

``` text
001
110
---
111
```

OR says:

> "Keep a feature if either side has it."

### Recognition signal

Think OR when the statement sounds like:

-   enable this flag,
-   combine available features,
-   union of bit properties,
-   force a bit to `1`.

------------------------------------------------------------------------

# 6. AND - Keep Common Features

TLE defines AND as: a bit survives only if **all corresponding bits are
set**.

Symbol:

``` cpp
A & B
```

Truth table:

``` text
A B | A&B
----+----
0 0 |  0
0 1 |  0
1 0 |  0
1 1 |  1
```

Example:

``` text
A = 101101
B = 101011
    ------
A&B
  = 101001
```

### Real-world mapping: common permissions

``` text
User permission:      101101
Resource permission:  101011
                      ------
Allowed on both:      101001
```

AND says:

> "Keep only what both sides contain."

### Important inequality from TLE

``` text
A & B <= min(A, B)
```

Why? AND can only **remove** set bits; it cannot create a new `1`.

For many numbers:

``` text
A & B & C & D <= min(A, B, C, D)
```

### Recognition signal

Think AND when you see:

-   common bits,
-   test whether a flag exists,
-   remove/clear bits,
-   intersection,
-   mask part of a number.

------------------------------------------------------------------------

# 7. XOR - Difference / Toggle / Parity

Symbol:

``` cpp
A ^ B
```

Truth table:

``` text
A B | A^B
----+----
0 0 |  0
0 1 |  1
1 0 |  1
1 1 |  0
```

For two bits, XOR is `1` when the bits are **different**.

``` text
same      -> 0
different -> 1
```

Example:

``` text
A = 10110
B = 10011
    -----
A^B
  = 00101
```

### Real-world mapping: light switch

A switch press toggles a light:

``` text
OFF --press--> ON
 ON --press--> OFF
```

XOR with `1` does exactly that:

``` text
0 ^ 1 = 1
1 ^ 1 = 0
```

XOR with `0` leaves the bit unchanged:

``` text
0 ^ 0 = 0
1 ^ 0 = 1
```

So remember:

``` text
XOR 0 = KEEP
XOR 1 = FLIP
```

For more than two inputs, TLE frames XOR as the **parity of the number
of set bits** at each position:

``` text
odd number of 1s  -> result 1
even number of 1s -> result 0
```

------------------------------------------------------------------------

# 8. Left Shift

TLE:

``` text
A << B
```

moves the binary representation `B` positions to the left and appends
zeros.

For non-overflowing non-negative integers:

``` text
A << B = A × 2^B
```

Example:

``` text
5 = 101₂

5 << 1:

101
  \ shift left
1010

1010₂ = 10

=> 5 × 2 = 10
```

Two shifts:

``` text
5 << 2

101   ->   10100
             ^^
          two zeros

10100₂ = 20
5 × 2² = 20
```

### Most useful CP form

``` cpp
1LL << k
```

creates a number with exactly bit `k` set.

``` text
k = 0: 000001 = 1
k = 1: 000010 = 2
k = 2: 000100 = 4
k = 3: 001000 = 8
k = 4: 010000 = 16
```

This is the foundation of masks.

------------------------------------------------------------------------

# 9. Right Shift

TLE:

``` text
A >> B
```

moves bits right and discards bits from the end.

For non-negative integers:

``` text
A >> B = floor(A / 2^B)
```

Example:

``` text
5 = 101₂

5 >> 1
101 -> 10

10₂ = 2
floor(5 / 2) = 2
```

Again:

``` text
5 >> 2
101 -> 1

floor(5 / 4) = 1
```

### Important interpretation

``` text
A >> k
```

moves original bit `k` into position `0`.

That gives another way to inspect a bit:

``` cpp
((A >> k) & 1)
```

ASCII:

``` text
A             = 10110100
want bit 4          ^
shift right 4:

A >> 4        = 00001011
                           ^
                           bit 0

(A >> 4) & 1  = 1
```

------------------------------------------------------------------------

# 10. Core Bitwise Properties

The first TLE lecture highlights these properties.

## Associative

``` text
(A | B) | C = A | (B | C)
(A & B) & C = A & (B & C)
(A ^ B) ^ C = A ^ (B ^ C)
```

Therefore you can reduce an entire array:

``` cpp
int xr = 0;
for (int x : a) xr ^= x;
```

## Commutative

``` text
A | B = B | A
A & B = B & A
A ^ B = B ^ A
```

Order does not matter.

## XOR identity

``` text
A ^ 0 = A
```

## XOR cancellation

``` text
A ^ A = 0
```

## XOR recovery

If:

``` text
A ^ B = C
```

then:

``` text
A ^ C = B
B ^ C = A
```

because duplicate values cancel.

## AND bound

``` text
A & B <= min(A, B)
```

## OR bound

``` text
A | B >= max(A, B)
```

OR can only preserve or add set bits.

------------------------------------------------------------------------

# 11. Check Odd / Even

TLE property:

``` cpp
A & 1
```

The least significant bit tells parity.

``` text
even binary numbers end in 0
odd  binary numbers end in 1
```

Examples:

``` text
10 = 1010 -> last bit 0 -> even
13 = 1101 -> last bit 1 -> odd
```

Code:

``` cpp
if (A & 1LL) {
    // odd
} else {
    // even
}
```

### Why this works algebraically

Every binary number is:

``` text
A = higher_bits × 2 + last_bit
```

The `higher_bits × 2` part is even. Therefore only the final bit
determines parity.

------------------------------------------------------------------------

# 12. Power of Two

TLE gives:

``` text
A & (A - 1) = 0  if A is a power of two
```

For **positive** `A`, the complete check is:

``` cpp
A > 0 && (A & (A - 1)) == 0
```

Why?

A power of two contains exactly one set bit:

``` text
8     = 1000
8 - 1 = 0111
        ----
&       0000
```

Another:

``` text
16     = 10000
15     = 01111
         -----
&        00000
```

But a non-power-of-two has more than one set bit:

``` text
12     = 1100
11     = 1011
         ----
&        1000 != 0
```

### Deeper pattern

Subtracting 1 does this:

``` text
...10000
      -1
--------
...01111
```

So:

``` cpp
x & (x - 1)
```

removes the **lowest set bit**.

Example:

``` text
x         = 10110000
x - 1     = 10101111
            --------
x&(x-1)   = 10100000
                  ^
           lowest 1 removed
```

This is a very important future pattern for counting set bits.

------------------------------------------------------------------------

# 13. Set the Kth Bit

TLE's slide uses **1-based K**:

``` cpp
A | (1 << (K - 1))
```

In normal 0-based CP indexing:

``` cpp
A | (1LL << k)
```

### Goal

Force bit `k` to `1`, regardless of its old value.

Suppose:

``` text
A = 1001010000
```

We want to set the bit indicated below:

``` text
A       1 0 0 1 0 1 0 0 0 0
                      ^
mask    0 0 0 0 0 0 1 0 0 0
        -------------------
OR      1 0 0 1 0 1 1 0 0 0
```

Why OR?

``` text
old bit | 1

0 | 1 = 1
1 | 1 = 1
```

The selected bit is guaranteed to become `1`.

Every other mask bit is `0`, and:

``` text
x | 0 = x
```

so all other positions stay unchanged.

### Real-world mapping: turn Wi-Fi ON

If each bit is a device setting:

``` text
Bluetooth  GPS  WiFi  NFC
    1       0    0    1
```

To guarantee Wi-Fi is ON, OR with a mask containing only the Wi-Fi bit.

``` text
1001
0010
----
1011
```

### Template

``` cpp
x |= (1LL << k);
```

------------------------------------------------------------------------

# 14. Check the Kth Bit

TLE's 1-based form:

``` cpp
A & (1 << (K - 1))
```

If the result is non-zero, the bit is set.

0-based CP form:

``` cpp
(A & (1LL << k)) != 0
```

Example:

``` text
A       = 10110100
mask    = 00010000
          --------
A&mask  = 00010000   non-zero -> SET
```

If the bit were zero:

``` text
A       = 10100100
mask    = 00010000
          --------
A&mask  = 00000000   -> NOT SET
```

### Why AND?

Every position except `k` gets ANDed with `0`, so it disappears.

``` text
x & 0 = 0
```

At the target:

``` text
0 & 1 = 0
1 & 1 = 1
```

The mask acts like a microscope that hides every bit except one.

### Alternative method

``` cpp
((A >> k) & 1LL)
```

This first moves the desired bit to the end, then isolates it.

------------------------------------------------------------------------

# 15. Toggle the Kth Bit

The handwritten TLE explanation demonstrates XOR for toggling.

0-based form:

``` cpp
A ^ (1LL << k)
```

Why XOR?

``` text
old ^ 1 = flipped

0 ^ 1 = 1
1 ^ 1 = 0
```

Example:

``` text
A       = 1001010000
mask    = 0000010000
          ----------
XOR     = 1001000000
```

If the bit was `1`, it becomes `0`.

Apply the same toggle again:

``` text
1001000000
0000010000
----------
1001010000
```

We returned to the original state.

### Real-world mapping: wall switch

Set is like:

> "Make the light ON."

Toggle is:

> "Press the switch."

You do **not** need to know the current state.

### Template

``` cpp
x ^= (1LL << k);
```

------------------------------------------------------------------------

# 16. Unset / Clear the Kth Bit

Goal:

``` text
target bit -> 0
everything else unchanged
```

The robust standard mask is:

``` cpp
A & ~(1LL << k)
```

Build it visually.

Suppose `k = 3`:

``` text
1 << 3      = 00001000
~(1 << 3)   = 11110111
```

Now:

``` text
A           = 10111101
mask        = 11110111
              --------
A & mask    = 10110101
                  ^
                  cleared
```

Why?

At target:

``` text
x & 0 = 0
```

Everywhere else:

``` text
x & 1 = x
```

### Real-world mapping: revoke exactly one permission

``` text
READ WRITE DELETE ADMIN
 1     1      1     1
```

To revoke DELETE only, AND with a mask containing `0` at DELETE and `1`
everywhere else.

### Template

``` cpp
x &= ~(1LL << k);
```

------------------------------------------------------------------------

# 17. Bit Masks - The Universal Mental Model

Instead of memorizing four unrelated formulas, think:

> **Create a mask that marks the bit(s) you care about, then choose an
> operator whose truth table produces the desired effect.**

``` text
                   MASK
                    |
                    v
number  --------> operator --------> result
```

## Set

Need:

``` text
0 -> 1
1 -> 1
```

That is OR with `1`.

``` cpp
x | mask
```

## Check

Need to expose only one bit.

That is AND with `1` at target and `0` elsewhere.

``` cpp
x & mask
```

## Toggle

Need:

``` text
0 -> 1
1 -> 0
```

That is XOR with `1`.

``` cpp
x ^ mask
```

## Clear

Need target to become `0`, others unchanged.

Use:

``` text
target mask bit = 0
other mask bits = 1
```

then AND.

``` cpp
x & ~mask
```

### Master table

  Operation        Formula             Meaning
  ---------------- ------------------- ------------
  Set bit `k`      `x \| (1LL << k)`   force to 1
  Check bit `k`    `x & (1LL << k)`    inspect
  Toggle bit `k`   `x ^ (1LL << k)`    flip
  Clear bit `k`    `x & ~(1LL << k)`   force to 0

------------------------------------------------------------------------

# 18. XOR Cancellation

One of the strongest CP ideas from the TLE properties is:

``` text
x ^ x = 0
x ^ 0 = x
```

Therefore pairs disappear.

Suppose:

``` text
[4, 7, 2, 7, 2]
```

XOR all values:

``` text
4 ^ 7 ^ 2 ^ 7 ^ 2
```

Because XOR is associative and commutative, reorder conceptually:

``` text
4 ^ (7 ^ 7) ^ (2 ^ 2)
= 4 ^ 0 ^ 0
= 4
```

ASCII:

``` text
4   survives
7 ─┐
7 ─┘ cancel

2 ─┐
2 ─┘ cancel
```

### Real-world mapping: matching socks

Every sock has a matching identical sock except one.

``` text
A A   B B   C   D D
|_|   |_|       |_|
cancel cancel    cancel

C remains
```

### Recognition signals

Think XOR cancellation when:

-   every value occurs twice except one,
-   values are added/removed in pairs,
-   you need a missing element from two nearly equal collections,
-   an operation must be reversible without storing extra state.

------------------------------------------------------------------------

# 19. XOR and Parity

TLE explicitly connects XOR to parity.

At one bit position:

``` text
number of 1s = 0 -> XOR 0
number of 1s = 1 -> XOR 1
number of 1s = 2 -> XOR 0
number of 1s = 3 -> XOR 1
...
```

So:

``` text
XOR = count_of_ones mod 2
```

Example:

``` text
A = 1011
B = 1101
C = 0111
```

Column by column:

``` text
       1 0 1 1
       1 1 0 1
       0 1 1 1
       -------
ones:  2 2 2 3
parity 0 0 0 1

XOR = 0001
```

This explains why duplicate numbers cancel: every duplicated `1`
contributes an even count.

------------------------------------------------------------------------

# 20. Addition Identities with Bits

The second TLE lecture lists two useful identities:

``` text
A + B = (A ^ B) + 2 × (A & B)
```

and:

``` text
A + B = (A | B) + (A & B)
```

## 20.1 Why XOR + AND represents addition

At a single bit:

``` text
0 + 0 -> sum bit 0, carry 0
0 + 1 -> sum bit 1, carry 0
1 + 0 -> sum bit 1, carry 0
1 + 1 -> sum bit 0, carry 1
```

Compare:

``` text
XOR  = sum without carry
AND  = positions producing carry
```

A carry from bit `i` is worth twice the original position, hence:

``` text
A + B = XOR + 2×AND
```

Example:

``` text
A = 10 = 1010
B =  5 = 0101
```

No overlapping set bits:

``` text
A ^ B = 1111 = 15
A & B = 0000 = 0

15 + 2×0 = 15
```

Another:

``` text
A = 10 = 1010
B =  6 = 0110

XOR = 1100 = 12
AND = 0010 = 2

A+B = 12 + 2×2
    = 16
```

## 20.2 Why OR + AND also works

For each bit, OR counts an overlapping `1` once; AND adds the second
copy.

``` text
A + B = (A | B) + (A & B)
```

Example:

``` text
A = 10
B =  6

A|B = 1110 = 14
A&B = 0010 =  2

14 + 2 = 16
```

### Recognition signal

These identities matter when a problem gives: - XOR and AND but asks
about sum, - sum and XOR and asks for AND, - OR/AND constraints, -
bit-by-bit reconstruction.

------------------------------------------------------------------------

# 21. Remainder Modulo Powers of Two

A central idea in the second TLE lecture is:

> When dividing a non-negative integer by `2^k`, the remainder is
> represented by the last `k` binary bits.

Why?

Write:

``` text
N = high_part × 2^k + low_k_bits
```

Therefore:

``` text
N mod 2^k = low_k_bits
```

Example with `N = 22`:

``` text
22 = 10110₂
```

Modulo `2² = 4`:

``` text
101 | 10
      ^^
   last 2 bits

10₂ = 2

22 % 4 = 2
```

Modulo `2³ = 8`:

``` text
10 | 110
     ^^^
 last 3 bits

110₂ = 6

22 % 8 = 6
```

### Mask equivalent

The last `k` bits can also be extracted by:

``` cpp
N & ((1LL << k) - 1)
```

Because:

``` text
(1 << k) - 1
```

is `k` ones.

For `k = 4`:

``` text
1 << 4       = 10000
(1 << 4)-1   = 01111
```

Then:

``` text
N            = abcdefgh
mask         = 00001111
               --------
N & mask     = 0000efgh
```

So for non-negative `N`:

``` text
N % 2^k == N & (2^k - 1)
```

This is a powerful bridge between **modulo** and **bits**.

------------------------------------------------------------------------

# 22. TLE Problem: Make Almost Equal With Mod

The TLE lecture solves **Codeforces 1909B - Make Almost Equal With Mod**
by trying powers of two:

``` cpp
for (int i = 1; i <= 61; ++i) {
    long long num = (1LL << i);

    set<long long> st;

    for (int j = 0; j < n; ++j) {
        st.insert(v[j] % num);
    }

    if (st.size() == 2) {
        cout << num << '
';
        return;
    }
}
```

This is the same structure shown in the lecture code: generate `2^i`,
compute every remainder, put the remainders into a set, and stop when
there are exactly **two distinct remainder values**.

## 22.1 What does `1LL << i` mean here?

``` text
i = 1      1 << 1 = 000010 = 2
i = 2      1 << 2 = 000100 = 4
i = 3      1 << 3 = 001000 = 8
i = 4      1 << 4 = 010000 = 16
```

So the loop checks:

``` text
2 -> 4 -> 8 -> 16 -> 32 -> ...
```

The crucial TLE observation is:

``` text
x % 2^i = value represented by the final i binary bits of x
```

Therefore the code is really doing this:

``` text
try 1-bit suffix
try 2-bit suffix
try 3-bit suffix
...
until exactly two different suffix-values remain
```

------------------------------------------------------------------------

## 22.2 Bit-by-bit dry run: `[8, 14, 22, 30]`

Write every number with the same width:

``` text
decimal       binary

 8            001000
14            001110
22            010110
30            011110
```

### Iteration 1: `i = 1`

``` cpp
num = 1LL << 1;   // 2
```

Binary modulus:

``` text
2 = 000010 = 2^1
```

Modulo `2` keeps only the **last 1 bit**:

``` text
                       last 1 bit
                            |
                            v
 8 = 00100[0]  ----------> 0
14 = 00111[0]  ----------> 0
22 = 01011[0]  ----------> 0
30 = 01111[0]  ----------> 0
```

Equivalent arithmetic:

``` text
8  % 2 = 0
14 % 2 = 0
22 % 2 = 0
30 % 2 = 0
```

Set construction:

``` text
start: st = {}

8  % 2 = 0  -> st = {0}
14 % 2 = 0  -> st = {0}
22 % 2 = 0  -> st = {0}
30 % 2 = 0  -> st = {0}

st.size() = 1
```

Visual grouping:

``` text
suffix

0 :  8, 14, 22, 30

only ONE group
        |
        v
continue
```

------------------------------------------------------------------------

### Iteration 2: `i = 2`

``` cpp
num = 1LL << 2;   // 4
```

Binary:

``` text
4 = 000100 = 2^2
```

Modulo `4` keeps the **last 2 bits**:

``` text
                        last 2 bits
                           |  |
                           v  v

 8 = 0010[00]  ---------> 00₂ = 0
14 = 0011[10]  ---------> 10₂ = 2
22 = 0101[10]  ---------> 10₂ = 2
30 = 0111[10]  ---------> 10₂ = 2
```

Arithmetic check:

``` text
 8 % 4 = 0
14 % 4 = 2
22 % 4 = 2
30 % 4 = 2
```

Set construction step by step:

``` text
start: st = {}

8 % 4 = 0

st = {0}


14 % 4 = 2

st = {0, 2}


22 % 4 = 2

st = {0, 2}


30 % 4 = 2

st = {0, 2}
```

Now:

``` text
st.size() = 2
```

Exactly two groups:

``` text
remainder 0                    remainder 2
    |                              |
    v                              v
   [8]                      [14, 22, 30]

    \______________________________/
                   |
          exactly 2 groups
                   |
                   v
              ANSWER = 4
```

So the loop prints:

``` text
4
```

------------------------------------------------------------------------

## 22.3 Why does modulo `2^k` mean "last k bits"?

Take:

``` text
22 = 010110₂
```

For `k = 2`:

``` text
22 = 0101 | 10
            ^^
         last 2 bits
```

The high part is a multiple of `4`:

``` text
0101₂ × 4 + 10₂

5 × 4 + 2
= 22
```

Therefore:

``` text
22 % 4 = 2
```

ASCII:

``` text
          quotient part        remainder part
               |                    |
               v                    v
22 binary =  0101                   10
             ----                   --
             × 2²                   < 2²

22 = 5 × 4 + 2
             ^
             remainder
```

General form:

``` text
x = [ HIGH BITS ][ LAST k BITS ]
        |
        +---- multiple of 2^k

x % 2^k = [ LAST k BITS ]
```

------------------------------------------------------------------------

## 22.4 Same operation using a bit mask

For:

``` text
num = 4 = 2²
```

build:

``` text
num - 1 = 3 = 000011
```

Then:

``` text
x % 4 == x & 3
```

For each number:

``` text
 8       001000
mask     000011
         ------
         000000 = 0


14       001110
mask     000011
         ------
         000010 = 2


22       010110
mask     000011
         ------
         000010 = 2


30       011110
mask     000011
         ------
         000010 = 2
```

This makes the bit meaning of the modulo operation very visible.

------------------------------------------------------------------------

## 22.5 Read the lecture code line by line

``` cpp
for (int i = 1; i <= 61; i++) {
```

Meaning:

``` text
try suffix lengths:

1 bit
2 bits
3 bits
...
```

Next:

``` cpp
long long num = (1LL << i);
```

Meaning:

``` text
num = 2^i
```

Next:

``` cpp
set<long long> st;
```

Meaning:

``` text
store only DISTINCT remainder values
```

Next:

``` cpp
for (int j = 0; j < n; j++) {
    st.insert(v[j] % num);
}
```

Bit interpretation:

``` text
for every number:

take its final i bits
        |
        v
convert that suffix to remainder
        |
        v
insert into distinct-groups set
```

Finally:

``` cpp
if (st.size() == 2) {
    cout << num << '
';
    return;
}
```

Meaning:

``` text
Did this suffix length divide the array
into exactly two remainder classes?

NO  -> reveal one more bit
YES -> output 2^i
```

Complete visual algorithm:

``` text
                    ARRAY
                      |
                      v
             i = 1, num = 2
                      |
             inspect last 1 bit
                      |
               distinct count?
                /           \
              != 2           2
               |             |
               v             v
             i = 2         OUTPUT
             num = 4
               |
       inspect last 2 bits
               |
        distinct count?
          /         \
        !=2          2
         |           |
         v           v
   reveal next     OUTPUT
      bit
```

------------------------------------------------------------------------

## 22.6 Clean C++ version matching the TLE approach

``` cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

void solve() {
    int n;
    cin >> n;

    vector<ll> v(n);

    for (auto &x : v) {
        cin >> x;
    }

    for (int i = 1; i <= 61; ++i) {

        ll num = (1LL << i);

        set<ll> st;

        for (int j = 0; j < n; ++j) {
            st.insert(v[j] % num);
        }

        if (st.size() == 2) {
            cout << num << '
';
            return;
        }
    }
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int T;
    cin >> T;

    while (T--) {
        solve();
    }
}
```

### Mental translation of the code

``` text
for each power of two:
        |
        v
+-------------------------+
| num = 2^i               |
+-------------------------+
        |
        v
+-------------------------+
| x % num for every x     |
|                         |
| = inspect last i bits   |
+-------------------------+
        |
        v
+-------------------------+
| put distinct values     |
| into set                |
+-------------------------+
        |
        v
    st.size() == 2 ?
       /       \
     no         yes
     |           |
     v           v
 next i      print num
```

### Recognition rule to remember

``` text
Problem gives / suggests:

       modulo by powers of 2
                |
                v
         DO NOT see only %
                |
                v
      draw binary representation
                |
                v
       x % 2^k = last k bits
                |
                v
      compare/group bit suffixes
```

# 23. Recognition Signals for CF

When reading a problem, don't ask only:

> "Which bit operator should I use?"

Ask:

> "What structural clue tells me the number is secretly a collection of
> bits?"

## Signal A - powers of two

Words/numbers like:

``` text
2^k
1, 2, 4, 8, 16, ...
```

Think: - shifts, - one set bit, - masks, - binary suffix/prefix
structure.

## Signal B - modulo by `2^k`

Think:

``` text
last k bits
```

## Signal C - multiply/divide by powers of two

Think:

``` text
<<
>>
```

## Signal D - odd/even

Think:

``` cpp
x & 1
```

## Signal E - pairs cancel / one unique value

Think:

``` text
XOR
```

## Signal F - turn feature ON

Think:

``` text
OR
```

## Signal G - keep common bits / test feature

Think:

``` text
AND
```

## Signal H - flip state

Think:

``` text
XOR with mask
```

## Signal I - set/unset/check Kth position

Immediately draw:

``` text
number:  ????????
mask:    00010000
```

Then choose OR / AND / XOR based on desired truth table.

## Signal J - minimum/maximum after repeated AND/OR

Recall:

``` text
AND can only remove bits -> tends downward
OR  can only add bits    -> tends upward
```

Formally from TLE:

``` text
A&B <= min(A,B)
A|B >= max(A,B)
```

------------------------------------------------------------------------

# 24. C++ Bit Toolkit

``` cpp
using ll = long long;

// Check odd
bool isOdd(ll x) {
    return x & 1LL;
}

// Check bit k (0-based)
bool isSet(ll x, int k) {
    return (x & (1LL << k)) != 0;
}

// Alternative check
bool isSetShift(ll x, int k) {
    return ((x >> k) & 1LL) != 0;
}

// Set bit k
ll setBit(ll x, int k) {
    return x | (1LL << k);
}

// Clear bit k
ll clearBit(ll x, int k) {
    return x & ~(1LL << k);
}

// Toggle bit k
ll toggleBit(ll x, int k) {
    return x ^ (1LL << k);
}

// Positive power of two
bool isPowerOfTwo(ll x) {
    return x > 0 && (x & (x - 1)) == 0;
}

// Remove lowest set bit
ll removeLowestSetBit(ll x) {
    return x & (x - 1);
}

// Extract last k bits, for suitable k
ll lowBits(ll x, int k) {
    return x & ((1LL << k) - 1);
}
```

For large shifts, prefer:

``` cpp
1LL << k
```

instead of:

``` cpp
1 << k
```

because the latter starts as a 32-bit `int`.

------------------------------------------------------------------------

# 25. Common Bugs and Precedence Traps

## Bug 1 - Power-of-two parentheses

Wrong / dangerous:

``` cpp
if (x & (x - 1) == 0)
```

Write:

``` cpp
if ((x & (x - 1)) == 0)
```

Also guard zero:

``` cpp
if (x > 0 && (x & (x - 1)) == 0)
```

## Bug 2 - Confusing 1-based and 0-based bit numbering

If a lecture says "4th bit" using 1-based indexing:

``` text
4th bit -> programming index 3
```

TLE form:

``` cpp
1 << (K - 1)
```

CP 0-based form:

``` cpp
1LL << k
```

Write the convention at the top of your solution notes.

## Bug 3 - Toggle vs set

``` cpp
x | mask
```

does **not** flip. It forces bits ON.

``` cpp
x ^ mask
```

flips.

## Bug 4 - Clearing using XOR

XOR only clears a bit if you already know it is `1`.

Robust clearing is:

``` cpp
x &= ~(1LL << k);
```

## Bug 5 - Signed shift edge cases

For ordinary beginner CF bit work, use non-negative values when
reasoning with:

``` text
A >> B = floor(A / 2^B)
```

Signed negative right-shift behavior is not the simple visual model you
want for beginner notes.

## Bug 6 - Shift width

Avoid shifting by an amount outside the type width. Choose `long long` /
unsigned types appropriately for constraints.

------------------------------------------------------------------------

# 26. One-Page Revision Map

``` text
                         BIT MANIPULATION
                               |
        +----------------------+----------------------+
        |                      |                      |
     POSITION               OPERATOR               PATTERN
        |                      |                      |
   2^k weight          OR   -> combine          odd/even -> &1
   1<<k mask           AND  -> common           power 2  -> x&(x-1)
   MSB / LSB           XOR  -> differ/toggle    pairs    -> XOR
                       <<   -> ×2^k              mod 2^k -> low k bits
                       >>   -> /2^k

KTH BIT TOOLBOX
--------------------------------------------------------------
SET       x |  (1LL<<k)       force 1
CHECK     x &  (1LL<<k)       inspect
TOGGLE    x ^  (1LL<<k)       flip
CLEAR     x & ~(1LL<<k)       force 0

XOR TOOLBOX
--------------------------------------------------------------
x ^ 0 = x
x ^ x = 0
a ^ b = c  =>  a ^ c = b
pairs cancel
odd count of 1s at a bit -> XOR bit 1

ORDER / MAGNITUDE
--------------------------------------------------------------
A & B <= min(A,B)
A | B >= max(A,B)

ADDITION
--------------------------------------------------------------
A+B = (A^B) + 2(A&B)
A+B = (A|B) + (A&B)

POWER-OF-TWO MODULO
--------------------------------------------------------------
x % 2^k = last k binary bits
          = x & (2^k - 1)       [non-negative x]
```

------------------------------------------------------------------------

# 27. Practice Ladder

These are chosen to reinforce the lecture concepts rather than jump
immediately into advanced bitmask DP.

## Level 1 - mechanical bit understanding

1.  Convert `55` to binary, octal and hexadecimal by hand.
2.  Convert `110111₂` to decimal.
3.  For `x = 44`, write its binary representation.
4.  Check whether bits `0..5` are set.
5.  Set, toggle and clear each bit manually.

## Level 2 - properties

1.  Prove `x ^ x = 0` bit by bit.
2.  Prove `x & (x-1)` removes the lowest set bit.
3.  Explain why `x & 1` gives parity.
4.  Verify `A+B = (A^B)+2(A&B)` for five pairs.
5.  Verify `x % 8` equals the value of the last three bits.

## Level 3 - Codeforces-style recognition

-   **CF 1475A - Odd Divisor**\
    Recognition: repeatedly removing factors of two / power-of-two
    structure.

-   **CF 1909B - Make Almost Equal With Mod**\
    Recognition: modulo powers of two = inspect binary suffixes. This is
    the problem explicitly referenced in TLE Bit Manipulation - 2.

-   Problems where every element occurs twice except one\
    Recognition: XOR cancellation.

-   Problems asking whether a Kth flag is enabled\
    Recognition: single-bit AND mask.

## Suggested solving workflow

For every bit problem, write these four lines before coding:

``` text
1. What does one bit mean?
2. Which bits matter?
3. What mask isolates/changes them?
4. Which truth table gives the operation I want?
```

Example:

``` text
Need to set bit 5.

1. target bit must become 1
2. only bit 5 matters
3. mask = 1<<5
4. 0|1=1 and 1|1=1

=> OR
```

This approach is much safer than memorizing formulas without
understanding them.

------------------------------------------------------------------------

# 28. References

## Primary source - uploaded TLE notes

This handbook is based on:

-   **Bit Manipulation - 1, Harsh Gupta (TLE)**\
    Main lecture flow: number systems, base conversion, OR, AND, XOR,
    shifts, and fundamental bitwise properties.

-   **Bit Manipulation - 2, Harsh Gupta (TLE)**\
    Main lecture flow: Kth-bit operations, bitwise tricks, XOR/set-bit
    parity, addition identities, and **Make Almost Equal With Mod**.

## Lecture-to-handbook mapping

``` text
TLE Bit Manipulation - 1
    |
    +-- Number system ----------> Sections 2-4
    +-- OR / AND / XOR ---------> Sections 5-7
    +-- Left / right shift -----> Sections 8-9
    +-- Properties -------------> Sections 10-12, 18-20

TLE Bit Manipulation - 2
    |
    +-- Set/check/toggle/unset -> Sections 13-17
    +-- Bitwise tricks ---------> Sections 19-20
    +-- Modulo powers of 2 -----> Section 21
    +-- CF 1909B ---------------> Section 22
```

## External problem reference

-   Codeforces 1909B - Make Almost Equal With Mod\
    `https://codeforces.com/problemset/problem/1909/B`

------------------------------------------------------------------------

------------------------------------------------------------------------

# Appendix A - Bit-by-Bit Visual Dry Runs + C++ Code

This appendix expands the most important TLE operations **one bit at a
time**.

## A1. OR - bit by bit

Example:

``` text
A = 10 = 1010
B =  6 = 0110

bit index       3   2   1   0
                |   |   |   |
A               1   0   1   0
B               0   1   1   0
                -------------
A | B           1   1   1   0
```

Now evaluate each column independently:

``` text
bit 0: 0 | 0 = 0
bit 1: 1 | 1 = 1
bit 2: 0 | 1 = 1
bit 3: 1 | 0 = 1

result = 1110₂ = 14
```

Visual meaning:

``` text
OR asks at every position:

"Does A OR B have this switch ON?"

A:      OFF ON  ON  OFF
B:      OFF ON  OFF ON
         |   |   |   |
Result: OFF ON  ON  ON
```

C++:

``` cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long a = 10;
    long long b = 6;

    long long result = a | b;

    cout << result << '\n'; // 14
}
```

------------------------------------------------------------------------

## A2. AND - bit by bit

Use the same values:

``` text
A = 10 = 1010
B =  6 = 0110

bit index       3   2   1   0
A               1   0   1   0
B               0   1   1   0
                -------------
A & B           0   0   1   0
```

Column evaluation:

``` text
bit 0: 0 & 0 = 0
bit 1: 1 & 1 = 1
bit 2: 0 & 1 = 0
bit 3: 1 & 0 = 0

result = 0010₂ = 2
```

Think:

``` text
AND = BOTH must have the switch ON.

A:       1 0 1 0
B:       0 1 1 0
             ^
             only common ON bit

Result:  0 0 1 0
```

C++:

``` cpp
long long a = 10;
long long b = 6;

cout << (a & b) << '\n'; // 2
```

------------------------------------------------------------------------

## A3. XOR - bit by bit

``` text
A = 10 = 1010
B =  6 = 0110

bit index       3   2   1   0
A               1   0   1   0
B               0   1   1   0
                -------------
A ^ B           1   1   0   0
```

Column evaluation:

``` text
bit 0: 0 ^ 0 = 0   same      -> 0
bit 1: 1 ^ 1 = 0   same      -> 0
bit 2: 0 ^ 1 = 1   different -> 1
bit 3: 1 ^ 0 = 1   different -> 1

result = 1100₂ = 12
```

Memory rule:

``` text
XOR

same      -> 0
different -> 1

0 ^ 0 = 0
1 ^ 1 = 0

0 ^ 1 = 1
1 ^ 0 = 1
```

C++:

``` cpp
long long a = 10;
long long b = 6;

cout << (a ^ b) << '\n'; // 12
```

------------------------------------------------------------------------

## A4. Left shift - move every bit

Take:

``` text
x = 5 = 101₂
```

### `x << 1`

``` text
before:

bit:       2   1   0
           1   0   1
           |   |   |
           v   v   v

after <<1:

bit:   3   2   1   0
       1   0   1   0
                   ^
              new zero
```

Numerically:

``` text
101₂  = 5
1010₂ = 10

5 << 1 = 10 = 5 × 2¹
```

### `x << 2`

``` text
101
 |||
 vv
10100
   ^^
two zeroes inserted

10100₂ = 20
5 × 2² = 20
```

C++:

``` cpp
long long x = 5;

cout << (x << 1) << '\n'; // 10
cout << (x << 2) << '\n'; // 20
```

------------------------------------------------------------------------

## A5. Right shift - remove low bits

``` text
x = 22 = 10110₂
```

### `x >> 1`

``` text
1 0 1 1 0
 \ \ \ \
  1 0 1 1      last bit disappears

1011₂ = 11
```

### `x >> 2`

``` text
original:  1 0 1 1 0
                     X   remove bit 0
                   X     remove bit 1

remaining: 1 0 1

101₂ = 5
```

Numerically:

``` text
22 >> 1 = floor(22 / 2) = 11
22 >> 2 = floor(22 / 4) = 5
```

C++:

``` cpp
long long x = 22;

cout << (x >> 1) << '\n'; // 11
cout << (x >> 2) << '\n'; // 5
```

------------------------------------------------------------------------

## A6. Build `1 << k` bit by bit

This is the most important mask construction.

Start:

``` text
1 = 00000001
```

Shift:

``` text
1 << 0 = 00000001
1 << 1 = 00000010
1 << 2 = 00000100
1 << 3 = 00001000
1 << 4 = 00010000
1 << 5 = 00100000
```

Notice:

``` text
                    k
                    |
1 << k = 000000001000000
                    ^
              exactly one 1
```

So:

``` cpp
long long mask = 1LL << k;
```

means:

> construct a mask that points at exactly bit `k`.

------------------------------------------------------------------------

## A7. Check a bit - full visual dry run

Suppose:

``` text
x = 44
```

Binary:

``` text
44 = 32 + 8 + 4

bit index:  5   4   3   2   1   0
weight:    32  16   8   4   2   1
x:          1   0   1   1   0   0
```

Check bit `3`.

### Step 1 - construct mask

``` text
1 << 3

000001
   <<3
--------
001000
```

### Step 2 - AND

``` text
x       = 101100
mask    = 001000
          ------
x&mask  = 001000
```

### Step 3 - interpret

``` text
001000 != 0

therefore bit 3 is SET.
```

Bit-by-bit:

``` text
x:       1 0 1 1 0 0
mask:    0 0 1 0 0 0
          | | | | | |
AND:     0 0 1 0 0 0
              ^
          survives
```

C++:

``` cpp
bool isSet(long long x, int k) {
    return (x & (1LL << k)) != 0;
}
```

Alternative:

``` cpp
bool isSet(long long x, int k) {
    return ((x >> k) & 1LL) == 1;
}
```

Visual for the alternative:

``` text
x = 101100
        ^
      bit 3

x >> 3:

101100
   >>3
------
000101
     ^
desired bit moved to LSB

000101
000001
------
000001
```

------------------------------------------------------------------------

## A8. Set a bit - full visual dry run

Use:

``` text
x = 40 = 101000
```

Set bit `2`.

Current:

``` text
bit:     5 4 3 2 1 0
x:       1 0 1 0 0 0
                 ^
              currently 0
```

Construct mask:

``` text
1 << 2 = 000100
```

OR:

``` text
x       101000
mask    000100
        ------
result  101100
```

Target column:

``` text
0 | 1 = 1
```

Other columns:

``` text
x | 0 = x
```

Therefore only the target changes.

``` text
101000 = 40
101100 = 44
```

C++:

``` cpp
long long setBit(long long x, int k) {
    return x | (1LL << k);
}
```

In-place:

``` cpp
x |= (1LL << k);
```

------------------------------------------------------------------------

## A9. Clear a bit - full visual dry run

Start:

``` text
x = 44 = 101100
```

Clear bit `3`.

``` text
bit:      5 4 3 2 1 0
x:        1 0 1 1 0 0
                ^
              bit 3
```

### Step 1

``` text
1 << 3 = 001000
```

### Step 2 - invert the mask

Conceptually using six bits:

``` text
001000
NOT
------
110111
```

### Step 3 - AND

``` text
x       101100
mask    110111
        ------
result  100100
```

Why target disappears:

``` text
1 & 0 = 0
```

Why everything else remains:

``` text
x & 1 = x
```

C++:

``` cpp
long long clearBit(long long x, int k) {
    return x & ~(1LL << k);
}
```

------------------------------------------------------------------------

## A10. Toggle a bit - full visual dry run

Start:

``` text
x = 40 = 101000
```

Toggle bit `2`.

``` text
mask = 1 << 2 = 000100
```

XOR:

``` text
101000
000100
------
101100
```

Bit changed:

``` text
0 ^ 1 = 1
```

Toggle the same bit again:

``` text
101100
000100
------
101000
```

Now:

``` text
1 ^ 1 = 0
```

Therefore:

``` text
toggle once  -> flip
toggle twice -> original
```

C++:

``` cpp
long long toggleBit(long long x, int k) {
    return x ^ (1LL << k);
}
```

------------------------------------------------------------------------

## A11. `x & (x-1)` - bit-by-bit

Example:

``` text
x = 40 = 101000
```

Subtract one:

``` text
x       = 101000
x - 1   = 100111
```

Why does subtraction look like that?

``` text
101000
     ^
lowest 1

subtract 1:

the lowest 1 becomes 0
all zeroes after it become 1

101000
   |
   +----------+
              |
100111 <------+
```

AND:

``` text
x       101000
x-1     100111
        ------
result  100000
```

The lowest set bit disappeared.

### Power-of-two case

``` text
x = 8

x       1000
x-1     0111
        ----
&       0000
```

Exactly one set bit existed, so removing the lowest set bit leaves zero.

C++:

``` cpp
bool isPowerOfTwo(long long x) {
    return x > 0 && (x & (x - 1)) == 0;
}
```

Remove one set bit:

``` cpp
x &= (x - 1);
```

------------------------------------------------------------------------

## A12. Count set bits using `x & (x-1)`

This is a direct extension of the TLE property.

Example:

``` text
x = 44 = 101100
```

There are three set bits.

Iteration 1:

``` text
101100
101011   x-1
------
101000   one 1 removed
count = 1
```

Iteration 2:

``` text
101000
100111
------
100000
count = 2
```

Iteration 3:

``` text
100000
011111
------
000000
count = 3
```

C++:

``` cpp
int countBits(long long x) {
    int cnt = 0;

    while (x != 0) {
        x &= (x - 1);
        ++cnt;
    }

    return cnt;
}
```

Complexity:

``` text
O(number of set bits)
```

rather than scanning every possible bit.

------------------------------------------------------------------------

## A13. XOR cancellation - full array dry run

Array:

``` text
[5, 3, 7, 3, 5]
```

Goal: find the value without a pair.

Start:

``` text
xr = 0
```

Step-by-step:

``` text
xr = 0 ^ 5
   = 5

xr = 5 ^ 3
   = 6

xr = 6 ^ 7
   = 1

xr = 1 ^ 3
   = 2

xr = 2 ^ 5
   = 7
```

But the better mathematical view is:

``` text
5 ^ 3 ^ 7 ^ 3 ^ 5

rearrange because XOR is commutative:

(5 ^ 5) ^ (3 ^ 3) ^ 7
    |         |
    0         0

= 0 ^ 0 ^ 7
= 7
```

C++:

``` cpp
long long xr = 0;

for (long long x : a) {
    xr ^= x;
}

cout << xr << '\n';
```

------------------------------------------------------------------------

## A14. Addition identity - bit-by-bit

TLE gives:

``` text
A + B = (A ^ B) + 2 × (A & B)
```

Example:

``` text
A = 10 = 1010
B =  6 = 0110
```

Normal addition:

``` text
   1010
 + 0110
 ------
  10000 = 16
```

Now split the work.

### XOR = addition without carry

``` text
1010
0110
----
1100 = 12
```

### AND = positions generating carry

``` text
1010
0110
----
0010 = 2
```

But a carry moves one position left:

``` text
0010 << 1 = 0100 = 4
```

Therefore:

``` text
12 + 4 = 16
```

Which is:

``` text
(A ^ B) + 2 × (A & B)
```

C++ verification:

``` cpp
long long a = 10;
long long b = 6;

long long lhs = a + b;
long long rhs = (a ^ b) + 2LL * (a & b);

cout << lhs << ' ' << rhs << '\n'; // 16 16
```

------------------------------------------------------------------------

## A15. `x % 2^k` - bit-by-bit

Take:

``` text
x = 22 = 10110
```

### Modulo 2

``` text
2 = 2¹
keep last 1 bit:

1011 | 0

remainder = 0
```

### Modulo 4

``` text
4 = 2²
keep last 2 bits:

101 | 10

10₂ = 2

22 % 4 = 2
```

### Modulo 8

``` text
8 = 2³
keep last 3 bits:

10 | 110

110₂ = 6

22 % 8 = 6
```

Mask view for modulo 8:

``` text
2³ - 1 = 7 = 00111

x       10110
mask    00111
        -----
result  00110 = 6
```

C++:

``` cpp
long long x = 22;
int k = 3;

long long mod = 1LL << k;

cout << x % mod << '\n';               // 6
cout << (x & (mod - 1)) << '\n';       // 6
```

------------------------------------------------------------------------

## A16. TLE CF 1909B - visual suffix search

The TLE lecture's key connection is:

``` text
mod 2^k  <=>  inspect the final k binary bits
```

Suppose:

``` text
a = [8, 14, 22, 30]
```

Binary:

``` text
 8 = 001000
14 = 001110
22 = 010110
30 = 011110
```

Try `mod 2`:

``` text
look at 1 final bit

 8  ...0 -> 0
14  ...0 -> 0
22  ...0 -> 0
30  ...0 -> 0

distinct remainders = {0}
```

Not two.

Try `mod 4`:

``` text
look at 2 final bits

 8  ...00 -> 0
14  ...10 -> 2
22  ...10 -> 2
30  ...10 -> 2

distinct remainders = {0, 2}
```

Exactly two.

Therefore:

``` text
k = 4
```

is suitable for this example.

Visual:

``` text
numbers
   |
   | mod 2 = reveal 1 bit
   v

0 0 0 0
\_______/
 one group

   |
   | mod 4 = reveal 2 bits
   v

00 10 10 10
|   \_____/
|      |
group1 group2
```

C++ shape:

``` cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int T;
    cin >> T;

    while (T--) {
        int n;
        cin >> n;

        vector<long long> a(n);
        for (auto &x : a) cin >> x;

        for (long long k = 2; ; k <<= 1) {
            set<long long> rem;

            for (long long x : a) {
                rem.insert(x % k);
            }

            if (rem.size() == 2) {
                cout << k << '\n';
                break;
            }
        }
    }
}
```

Recognition:

``` text
modulus candidate is 2,4,8,16...
                |
                v
        think binary suffix
                |
                v
    how many distinct suffixes?
```

------------------------------------------------------------------------

# Appendix B - Operator Decision Diagram

When you know the desired change but forget the operator:

``` text
                    WHAT DO I WANT?
                          |
          +---------------+----------------+
          |               |                |
       FORCE 1          FORCE 0           FLIP
          |               |                |
          v               v                v
         OR              AND              XOR
      with 1          with 0            with 1


Want to inspect one bit?
          |
          v
     AND with mask
```

For all other positions, choose neutral mask bits:

``` text
OR neutral value  = 0     x | 0 = x
AND neutral value = 1     x & 1 = x
XOR neutral value = 0     x ^ 0 = x
```

This gives the formulas naturally:

``` text
SET:
target = 1, others neutral for OR = 0
mask = 00010000
x | mask

CHECK:
target = 1, all others 0
x & mask

TOGGLE:
target = 1, others neutral for XOR = 0
x ^ mask

CLEAR:
target = 0, others neutral for AND = 1
mask = 11101111
x & mask
```

------------------------------------------------------------------------

# Appendix C - Debug Helper for Practice

During practice, print a number in binary to verify your reasoning.

``` cpp
#include <bits/stdc++.h>
using namespace std;

void showBits(unsigned long long x, int width = 8) {
    for (int i = width - 1; i >= 0; --i) {
        cout << ((x >> i) & 1ULL);

        if (i % 4 == 0 && i != 0)
            cout << ' ';
    }
    cout << '\n';
}

int main() {
    unsigned long long x = 44;

    showBits(x);                  // 0010 1100
    showBits(x | (1ULL << 1));    // set bit 1
    showBits(x & ~(1ULL << 3));   // clear bit 3
    showBits(x ^ (1ULL << 2));    // toggle bit 2
}
```

For learning, combine decimal and binary:

``` text
x = 44

decimal: 44
binary : 00101100
```

Then perform the operation on paper before running the program.

------------------------------------------------------------------------

# Appendix D - Recommended Practice Routine

For each new bit problem:

``` text
STEP 1
Write 2-3 sample numbers in binary.

STEP 2
Label bit positions.

        5 4 3 2 1 0
        -----------
x       1 0 1 1 0 0

STEP 3
Mark the bit/suffix that matters.

        1 0 [1 1 0 0]
              ^^^^^

STEP 4
Translate the statement.

"make bit 2 ON"
        |
        v
force 0->1 and 1->1
        |
        v
OR with 1

STEP 5
Construct mask.

1 << 2 = 000100

STEP 6
Dry run bit by bit.

101000
000100
------
101100

STEP 7
Only now write C++.
```

The goal is to train the recognition:

``` text
WORDS -> BIT EFFECT -> MASK -> OPERATOR -> CODE
```

rather than:

``` text
WORDS -> guess formula
```

## Final Mental Model

Do not see an integer only as a value such as `53`.

See both views:

``` text
DECIMAL VIEW                     BIT VIEW

53                               110101
 |                               ||||||
 arithmetic                      six independent switches
 + - * / %                       OR AND XOR SHIFT MASK
```

Competitive programming frequently becomes easier when you switch to the
representation that exposes the invariant.

``` text
power of 2?          -> binary shape
odd/even?            -> last bit
mod 2^k?             -> last k bits
pairs cancel?        -> XOR
feature enabled?     -> AND mask
force feature on?    -> OR mask
flip feature?        -> XOR mask
```

**Core habit:** whenever you see powers of two, parity, repeated pairs,
masks, toggling, or modulo `2^k`, draw the binary representation before
writing code.
