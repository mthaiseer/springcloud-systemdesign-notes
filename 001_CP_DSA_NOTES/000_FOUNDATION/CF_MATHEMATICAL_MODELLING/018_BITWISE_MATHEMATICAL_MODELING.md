# Part 17 — Bitwise Mathematical Modeling

> **Goal:** model integers as collections of binary variables, derive conditions one bit at a time, and recognize XOR/AND/OR/counting patterns quickly in Codeforces.
>
> **Core workflow:** `Story → Binary meaning → One-bit condition → Count/derive → Combine with 2^bit`
>
> **Recognition question:** **Can I stop reasoning about the whole integer and solve the condition independently for each binary position?**

## Table of Contents

- [17.0 Bitwise Mental Model](#170-bitwise-mental-model)
- [17.1 Binary Representation](#171-binary-representation)
- [17.2 Test, Set, Clear and Toggle a Bit](#172-test-set-clear-and-toggle-a-bit)
- [17.3 Odd, Even and Bit Parity](#173-odd-even-and-bit-parity)
- [17.4 Power of Two](#174-power-of-two)
- [17.5 Lowest Set Bit](#175-lowest-set-bit)
- [17.6 Popcount](#176-popcount)
- [17.7 XOR — Difference Without Carry](#177-xor--difference-without-carry)
- [17.8 XOR Cancellation](#178-xor-cancellation)
- [17.9 Global XOR — Unique Element](#179-global-xor--unique-element)
- [17.10 XOR Equation Modeling](#1710-xor-equation-modeling)
- [17.11 XOR 1..N and Range XOR](#1711-xor-1n-and-range-xor)
- [17.12 Prefix XOR](#1712-prefix-xor)
- [17.13 AND — Common Required Bits](#1713-and--common-required-bits)
- [17.14 OR — Union of Available Bits](#1714-or--union-of-available-bits)
- [17.15 Submask and Superset Conditions](#1715-submask-and-superset-conditions)
- [17.16 Bitwise Contribution Mental Model](#1716-bitwise-contribution-mental-model)
- [17.17 Pairwise XOR Contribution](#1717-pairwise-xor-contribution)
- [17.18 Pairwise AND Contribution](#1718-pairwise-and-contribution)
- [17.19 Pairwise OR Contribution](#1719-pairwise-or-contribution)
- [17.20 XOR as Parity of Set-Bit Counts](#1720-xor-as-parity-of-set-bit-counts)
- [17.21 Bitmask as a Set](#1721-bitmask-as-a-set)
- [17.22 Enumerating Submasks](#1722-enumerating-submasks)
- [17.23 Greedy High-Bit Construction](#1723-greedy-high-bit-construction)
- [17.24 Addition, XOR and Carry](#1724-addition-xor-and-carry)
- [17.25 When a + b = a XOR b](#1725-when-a--b--a-xor-b)
- [17.26 Important Bitwise Identities](#1726-important-bitwise-identities)
- [17.27 Signed Values, Shifts and Overflow](#1727-signed-values-shifts-and-overflow)
- [17.28 60-Second Discovery Workflow](#1728-60-second-discovery-workflow)
- [17.29 Codeforces Recognition Map](#1729-codeforces-recognition-map)
- [17.30 Common Mistakes](#1730-common-mistakes)
- [17.31 Fast Revision Card](#1731-fast-revision-card)

---

## 17.0 Bitwise Mental Model

An integer is a collection of independent binary positions.

```text
13 = 1101₂

bit index:   3 2 1 0
weight:      8 4 2 1
bit:         1 1 0 1

13 = 8 + 4 + 1
```

### Real-world scenario — apartment features

```text
bit 0 -> parking
bit 1 -> balcony
bit 2 -> gym
bit 3 -> pool

mask = 1101

pool    YES
gym     YES
balcony NO
parking YES
```

### Mathematical model

```text
whole integer
     ↓ binary
independent 0/1 states
     ↓
solve each bit
     ↓
combine with powers of two
```

**Memory hook:** `BITWISE = BREAK ONE INTEGER INTO MANY YES/NO VARIABLES.`

---

## 17.1 Binary Representation

Every non-negative integer can be written as:

```text
x = Σ b[k] * 2^k
where b[k] ∈ {0,1}
```

Example:

```text
22 = 10110₂
   = 1*16 + 0*8 + 1*4 + 1*2 + 0*1
   = 16 + 4 + 2
```

### Real-world scenario — power-of-two tokens

Available tokens:

```text
16  8  4  2  1
```

Pay `22`:

```text
16 + 4 + 2 = 22

16 8 4 2 1
 1 0 1 1 0
```

Binary representation is simply selecting powers of two.

---

## 17.2 Test, Set, Clear and Toggle a Bit

For bit `k`:

```text
mask = 1LL << k
```

Operations:

```text
test:   (x >> k) & 1
set:    x |= mask
clear:  x &= ~mask
toggle: x ^= mask
```

### Step-by-step permission example

Start:

```text
x = 0101
```

Set bit 1:

```text
0101
0010 OR
----
0111
```

Clear bit 2:

```text
0111
1011 AND
----
0011
```

Toggle bit 0:

```text
0011
0001 XOR
----
0010
```

```text
OR          -> force ON
AND with ~  -> force OFF
XOR         -> flip
```

---

## 17.3 Odd, Even and Bit Parity

The least significant bit has weight `2^0 = 1`.

Therefore:

```text
x even <=> (x & 1) == 0
x odd  <=> (x & 1) == 1
```

### Why?

Every integer is:

```text
x = 2q + r
r ∈ {0,1}
```

`r` is exactly bit 0.

### Real-world scenario — forming pairs

```text
17 people

17 = 2*8 + 1
```

Eight complete pairs and one person remains.

Binary:

```text
17 = 10001
           ^
           LSB=1 -> odd
```

---

## 17.4 Power of Two

A positive power of two has exactly one set bit:

```text
1  = 0001
2  = 0010
4  = 0100
8  = 1000
```

Test:

```text
x > 0 && (x & (x-1)) == 0
```

### Derivation

For `x=8`:

```text
x   = 1000
x-1 = 0111
      ----
AND = 0000
```

For `x=10`:

```text
x   = 1010
x-1 = 1001
      ----
AND = 1000
```

So `10` is not a power of two.

**Important:** explicitly require `x > 0`.

---

## 17.5 Lowest Set Bit

Two important forms:

```text
x & -x
```

isolates the lowest set bit.

```text
x & (x-1)
```

removes the lowest set bit.

### Example

```text
x = 12 = 1100

lowest ON bit = 0100 = 4
```

So:

```text
12 & -12 = 4
```

Removing it:

```text
1100
1011
----
1000 = 8
```

### Real-world intuition

If ON bits represent active power-of-two denominations, `x & -x` finds the smallest active denomination.

---

## 17.6 Popcount

`popcount(x)` is the number of set bits.

```text
13 = 1101
       ^^^
3 set bits
```

Mathematically, if:

```text
x = Σ b[k]2^k
```

then:

```text
popcount(x) = Σ b[k]
```

### Real-world scenario — enabled services

```text
mask = 101101

enabled:
1 + 0 + 1 + 1 + 0 + 1
= 4
```

### Kernighan observation

```text
x &= x-1
```

removes exactly one set bit.

Therefore number of repetitions until `x=0` equals popcount.

---

## 17.7 XOR — Difference Without Carry

Per bit:

| a | b | a XOR b |
|---:|---:|---:|
| 0 | 0 | 0 |
| 0 | 1 | 1 |
| 1 | 0 | 1 |
| 1 | 1 | 0 |

So XOR is `1` exactly where bits differ.

Example:

```text
10 = 1010
12 = 1100
     ----
XOR  0110 = 6
```

### Real-world scenario — compare permissions

```text
Alice = 1010
Bob   = 1100

XOR   = 0110
```

The `1` bits identify permissions where they disagree.

### Mathematical model

Per bit:

```text
XOR = addition modulo 2
```

with no carry.

---

## 17.8 XOR Cancellation

Core identities:

```text
x ^ x = 0
x ^ 0 = x
```

XOR is associative and commutative.

Example:

```text
5 ^ 7 ^ 5

= 5 ^ 5 ^ 7
= 0 ^ 7
= 7
```

### Real-world scenario — light switch toggles

```text
OFF
toggle -> ON
toggle -> OFF
```

Two identical toggles cancel.

```text
even number of identical XORs -> disappear
odd number                    -> one remains
```

---

## 17.9 Global XOR — Unique Element

If every value occurs twice except one:

```text
[4,1,2,1,2]
```

XOR all:

```text
4 ^ 1 ^ 2 ^ 1 ^ 2

= 4 ^ (1^1) ^ (2^2)
= 4 ^ 0 ^ 0
= 4
```

### Real-world scenario — luggage tags

Every tag was scanned twice except one:

```text
42,17,9,17,42
```

```text
42 ^ 17 ^ 9 ^ 17 ^ 42
= 9
```

**CF trigger:** `all occur an even number of times except ...`.

---

## 17.10 XOR Equation Modeling

Given:

```text
a ^ x = b
```

XOR both sides by `a`:

```text
a ^ a ^ x = a ^ b
```

Therefore:

```text
x = a ^ b
```

### Example

```text
10 ^ x = 6

x = 10 ^ 6

1010
0110
----
1100 = 12
```

Verify:

```text
10 ^ 12 = 6
```

### Real-world scenario

If `a` is the old configuration and `b` is the new configuration:

```text
toggle mask = a ^ b
```

because XOR marks exactly the changed bits.

---

## 17.11 XOR 1..N and Range XOR

Define:

```text
F(n) = 1 ^ 2 ^ ... ^ n
```

Pattern by `n mod 4`:

```text
n%4=0 -> n
n%4=1 -> 1
n%4=2 -> n+1
n%4=3 -> 0
```

Dry run:

```text
F(0)=0
F(1)=1
F(2)=3
F(3)=0
F(4)=4
F(5)=1
F(6)=7
F(7)=0
```

Then:

```text
XOR(L..R)
=
F(R) ^ F(L-1)
```

This is prefix cancellation in closed form.

---

## 17.12 Prefix XOR

Define:

```text
px[0]=0
px[i+1]=px[i]^a[i]
```

Range:

```text
xor(L,R)=px[R+1]^px[L]
```

### Example

```text
a = [5,2,7,3]
```

Build:

```text
px[0]=0
px[1]=0^5 = 5
px[2]=5^2 = 7
px[3]=7^7 = 0
px[4]=0^3 = 3
```

Query `[1,3]`:

```text
px[4]^px[1]
=3^5
=6
```

Direct:

```text
2^7^3=6
```

The prefix before `L` occurs twice and cancels.

---

## 17.13 AND — Common Required Bits

AND bit is `1` only when both input bits are `1`.

```text
14 = 1110
11 = 1011
     ----
AND  1010 = 10
```

### Real-world scenario — common permissions

```text
Employee A = 1110
Employee B = 1011

common permissions
= A & B
= 1010
```

### Mathematical interpretation

```text
AND = intersection
```

Repeated AND can only remove bits.

For non-negative integers:

```text
(a & b) <= a
(a & b) <= b
```

---

## 17.14 OR — Union of Available Bits

OR bit is `1` if at least one operand has it.

```text
10 = 1010
5  = 0101
     ----
OR   1111 = 15
```

### Real-world scenario — team skills

If bits represent skills:

```text
A | B
```

represents every skill available from at least one team member.

### Mathematical interpretation

```text
OR = union
```

Repeated OR can only add bits.

---

## 17.15 Submask and Superset Conditions

All bits required by `a` exist in `b` iff:

```text
(a & b) == a
```

Equivalent:

```text
(a | b) == b
```

### Example

```text
required  = 0101
available = 1101

0101
1101
----
AND = 0101
```

All requirements are present.

### Missing bits

```text
required & ~available
```

If this is zero, nothing is missing.

### Real-world scenario — permissions

```text
required mask
      ↓
check against user permissions
      ↓
(required & have) == required
```

---

## 17.16 Bitwise Contribution Mental Model

For sums over many pairs, avoid enumerating all pairs.

Analyze one bit at a time:

```text
for each bit b:
    count ones
    count zeros
    determine how many pairs make result bit 1
    multiply by 2^b
```

General shape:

```text
answer
=
Σ pairCount[b] * 2^b
```

### Real-world scenario — compare feature packages

Instead of comparing complete package masks pair by pair:

```text
parking separately
gym separately
pool separately
...
```

Count how many packages have each feature.

### Why it works

`XOR`, `AND`, and `OR` determine bit `b` only from input bit `b`.

There is no carry between positions.

---

## 17.17 Pairwise XOR Contribution

XOR produces `1` when bits differ.

At bit `b`:

```text
ones  = c1
zeros = c0
```

Unordered differing pairs:

```text
c1*c0
```

Contribution:

```text
c1*c0*2^b
```

### Example

```text
a=[1,2,3]

1=01
2=10
3=11
```

Bit 0:

```text
ones=2
zeros=1

pairs=2
value contribution=2*1=2
```

Bit 1:

```text
ones=2
zeros=1

pairs=2
value contribution=2*2=4
```

Total:

```text
2+4=6
```

Direct:

```text
1^2=3
1^3=2
2^3=1
-----
sum=6
```

---

## 17.18 Pairwise AND Contribution

AND bit is `1` only when both values have the bit.

If:

```text
ones=c
```

number of unordered pairs:

```text
C(c,2)=c(c-1)/2
```

Contribution:

```text
C(c,2)*2^b
```

### Example

```text
[1,3,5]

1=001
3=011
5=101
```

Bit 0 is ON in all three:

```text
C(3,2)=3
contribution=3*1=3
```

Other bits appear in only one number.

Total:

```text
3
```

Direct:

```text
1&3=1
1&5=1
3&5=1
sum=3
```

---

## 17.19 Pairwise OR Contribution

OR bit is `0` only if both values have zero there.

Total unordered pairs:

```text
C(n,2)
```

If:

```text
zeros=c0
```

zero-producing pairs:

```text
C(c0,2)
```

Therefore ON pairs:

```text
C(n,2)-C(c0,2)
```

Contribution:

```text
[C(n,2)-C(c0,2)]*2^b
```

### Example

```text
[1,2,3]
01
10
11
```

For each of bits 0 and 1, only one value has zero:

```text
C(1,2)=0
```

So all 3 pairs produce `1`.

```text
bit0: 3*1=3
bit1: 3*2=6

total=9
```

Direct:

```text
1|2=3
1|3=3
2|3=3
sum=9
```

---

## 17.20 XOR as Parity of Set-Bit Counts

For many values, result bit `b` of their XOR is:

```text
1 iff count of set bits at b is odd
```

because ones cancel in pairs.

### Example

```text
5 = 101
7 = 111
3 = 011
```

Bit 0:

```text
1+1+1 = 3 -> odd -> 1
```

Bit 1:

```text
0+1+1 = 2 -> even -> 0
```

Bit 2:

```text
1+1+0 = 2 -> even -> 0
```

Result:

```text
001 = 1
```

### Real-world scenario — lamps

Each operation toggles selected lamps.

```text
even toggles -> lamp returns to original state
odd toggles  -> lamp is flipped
```

---

## 17.21 Bitmask as a Set

For a small universe, bit `i` means whether item `i` is present.

Example:

```text
bit 0 -> apple
bit 1 -> banana
bit 2 -> mango
bit 3 -> orange

mask=1011
```

Set contains:

```text
apple
banana
orange
```

Operations:

```text
union                 A|B
intersection          A&B
difference            A&~B
symmetric difference  A^B
membership            A&(1<<i)
```

### Real-world scenario — pizza toppings

With `n` optional toppings:

```text
each topping -> choose / don't choose
```

Number of possible selections:

```text
2^n
```

---

## 17.22 Enumerating Submasks

For mask `M`:

```cpp
for (int s=M; s; s=(s-1)&M)
```

enumerates all non-zero submasks.

### Example

```text
M=1011
```

It has `3` set bits.

Number of all submasks:

```text
2^3=8
```

Non-zero submasks:

```text
1011
1010
1001
1000
0011
0010
0001
```

### Real-world scenario — recipe ingredients

Available:

```text
cheese
tomato
olive
```

Every recipe may use any subset of those available ingredients.

The loop visits only valid subsets.

### Complexity

For `k=popcount(M)`:

```text
O(2^k)
```

Across all `n`-bit masks:

```text
Σ 2^popcount(mask) = 3^n
```

---

## 17.23 Greedy High-Bit Construction

A higher bit is worth more than all lower bits combined:

```text
2^b
>
2^0+2^1+...+2^(b-1)
```

because:

```text
sum lower = 2^b-1
```

Therefore numerical maximization often tests bits high-to-low.

### Real-world scenario — capacity blocks

```text
16,8,4,2,1
```

Want largest feasible capacity.

```text
try 16
if feasible -> keep

try 16+8
if feasible -> keep

try +4
...
```

Generic model:

```text
ans=0

for bit high -> low:
    candidate=ans|(1<<bit)

    if candidate feasible:
        ans=candidate
```

**Important:** feasibility and correctness are problem-specific and still require proof.

---

## 17.24 Addition, XOR and Carry

Binary addition decomposes into:

```text
sum without carry = a ^ b
carry positions   = a & b
```

Each carry moves one bit left, giving:

```text
a+b
=
(a^b)+2*(a&b)
```

### Example

```text
a=5 = 0101
b=3 = 0011
```

XOR:

```text
0101
0011
----
0110 = 6
```

AND:

```text
0101
0011
----
0001 = 1
```

Therefore:

```text
5+3
=6 + 2*1
=8
```

### Real-world intuition — binary coins

Two `2^k` coins in the same column combine into one `2^(k+1)` coin.

That is a carry.

---

## 17.25 When a + b = a XOR b

From:

```text
a+b=(a^b)+2*(a&b)
```

we obtain:

```text
a+b=a^b
```

iff:

```text
a&b=0
```

Meaning: no binary position contains `1` in both values.

### Example — no carry

```text
4=100
3=011

100
011
---
AND=000
```

Thus:

```text
4+3=7
4^3=7
```

### Counterexample

```text
5=101
3=011

AND=001
```

A carry exists:

```text
5+3=8
5^3=6
```

**CF trigger:** whenever a statement relates `sum`, `XOR`, or `carry`, inspect `AND`.

---

## 17.26 Important Bitwise Identities

High-yield identities:

```text
x^x = 0
x^0 = x

x&x = x
x|x = x

x&0 = 0
x|0 = x
```

Addition identities:

```text
a+b = (a^b)+2*(a&b)
```

and:

```text
a+b = (a|b)+(a&b)
```

### Verify per bit

For `a|b` and `a&b`:

```text
00 -> 0+0
01 -> 1+0
10 -> 1+0
11 -> 1+1
```

This matches the contribution of `a+b`.

### Set interpretation

```text
AND -> intersection
OR  -> union
XOR -> symmetric difference
NOT -> complement
```

This makes many identities easier to remember.

---

## 17.27 Signed Values, Shifts and Overflow

Bitwise contest code needs type discipline.

### Use 64-bit masks when needed

Prefer:

```cpp
1LL << k
```

rather than:

```cpp
1 << k
```

for large bit positions.

### Complement warning

`~x` flips all bits of the machine representation.

Conceptually in 8 bits:

```text
x  = 00000101
~x = 11111010
```

It does not mean merely "flip the displayed low bits."

### Signed values

Reasoning about raw bit patterns is usually clearer with non-negative or unsigned values.

### Parentheses

Prefer:

```cpp
if ((x & (1LL<<k)) != 0)
```

### Contest checklist

```text
maximum value?
highest required bit?
can 1<<k overflow int?
can pair contributions overflow long long?
```

---

## 17.28 60-Second Discovery Workflow

```text
PROBLEM
   |
   v
Does the state contain binary properties
or powers-of-two structure?
   |
 +---+---+
NO      YES
         |
         v
What does ONE bit mean?
  /       |       |        common   union  different   parity
  |       |       |          |
 AND      OR      XOR        XOR
   \      |       |         /
          v
Can bits be handled independently?
          |
       +--+--+
      YES   NO
       |     |
count 0/1   inspect carry,
per bit     dependencies,
       |    DP/state
       v
Σ contribution[b]*2^b
```

### Fast questions

```text
1. What does bit b represent?
2. Is the condition about common bits? -> AND
3. Present anywhere? -> OR
4. Different/toggle/parity? -> XOR
5. Do duplicates cancel?
6. Is one mask contained in another?
7. Can I count ones and zeros per bit?
8. Is this a pairwise sum?
9. Can high bits be decided greedily?
10. Is carry coupling neighboring bits?
```

---

## 17.29 Codeforces Recognition Map

| Statement clue | Mathematical model |
|---|---|
| odd/even | `x&1` |
| power of two | `x>0 && !(x&(x-1))` |
| isolate lowest set bit | `x&-x` |
| remove lowest set bit | `x&(x-1)` |
| number of ON bits | popcount |
| duplicate values cancel | XOR |
| all twice except one | global XOR |
| `a^x=b` | `x=a^b` |
| XOR on `[L,R]` | prefix XOR |
| common bits | AND |
| present in at least one | OR |
| required mask contained | `(req&have)==req` |
| pairwise XOR total | `ones*zeros` per bit |
| pairwise AND total | `C(ones,2)` |
| pairwise OR total | `C(n,2)-C(zeros,2)` |
| small set/subset | bitmask |
| all subsets of mask | submask enumeration |
| maximize a mask | high-bit greedy + feasibility |
| sum equals XOR | `a&b==0` |
| toggled odd times | XOR parity |

---

## 17.30 Common Mistakes

1. **Thinking about the whole integer instead of one bit.**

2. **Confusing XOR and OR.**

```text
1^1=0
1|1=1
```

3. **Forgetting `x>0` in the power-of-two test.**

4. **Using `1<<k` for large `k`.** Prefer `1LL<<k`.

5. **Ordered vs unordered pair confusion.**

For unordered XOR pairs:

```text
ones*zeros
```

For ordered pairs:

```text
2*ones*zeros
```

6. **Using `C(c,2)` when self-pairs are allowed.**

7. **Assuming bits are independent when ordinary addition introduces carry.**

8. **Using `~mask` without controlling the relevant bit width.**

9. **Applying high-bit greedy without proving the feasibility test.**

10. **Overflow in contribution formulas.**

```text
count * count * 2^bit
```

can become very large.

---

## 17.31 Fast Revision Card

```text
========================================================
PART 17 — BITWISE MATHEMATICAL MODELING
========================================================

INTEGER
x = Σ bit[k]*2^k

TEST
(x>>k)&1

SET
x |= 1LL<<k

CLEAR
x &= ~(1LL<<k)

TOGGLE
x ^= 1LL<<k

--------------------------------------------

ODD
x&1

POWER OF TWO
x>0 && (x&(x-1))==0

LOWBIT
x&-x

REMOVE LOWEST SET BIT
x&(x-1)

POPCOUNT
number of ON bits

--------------------------------------------

XOR = DIFFERENCE / TOGGLE / PARITY

x^x=0
x^0=x

a^x=b
x=a^b

RANGE XOR
px[R+1]^px[L]

--------------------------------------------

AND = COMMON / INTERSECTION
OR  = UNION / PRESENT ANYWHERE

SUBMASK
(a&b)==a

--------------------------------------------

PAIR CONTRIBUTION

XOR:
ones*zeros*2^b

AND:
C(ones,2)*2^b

OR:
[C(n,2)-C(zeros,2)]*2^b

--------------------------------------------

BITMASK SETS

union         A|B
intersection  A&B
difference    A&~B
sym difference A^B

--------------------------------------------

ADDITION / CARRY

a+b=(a^b)+2*(a&b)

NO CARRY:
a+b=a^b
iff
a&b=0

--------------------------------------------

HIGH-BIT GREEDY

2^b > 2^b-1
      ^ sum of all lower bits

test high -> low

--------------------------------------------

CORE QUESTION

"Can I rewrite the condition for ONE BIT,
solve that tiny 0/1 problem,
then combine all bit contributions?"
========================================================
```
