# Codeforces Bit Manipulation --- Pattern & Form Recognition Handbook

> **Goal:** recognize the likely bit pattern/form within \~60 seconds,
> then derive and implement it.
>
> This handbook expands the TLE bit-manipulation foundations into
> recurring competitive-programming forms. It cannot guarantee that
> every new bit problem will match one template---CF often combines
> patterns---but these forms cover the main recognition vocabulary you
> should test first.

## 60-Second Contest Protocol

``` text
0–15 sec  → What is changing: individual bits, whole XOR, AND/OR, subset?
15–30 sec → Signal: pairs? power of two? range? maximize? construct? n<=20?
30–45 sec → Translate decimal statement into binary columns/suffix/mask.
45–60 sec → Name the form + write the invariant/formula before coding.
```

``` text
pairs/duplicates       -> XOR cancellation
% 2^k                  -> binary suffix
sum over pairs         -> per-bit contribution
maximize AND/number    -> MSB -> LSB greedy
many range XOR queries -> prefix XOR
many range bit queries -> prefix counts per bit
same highest bit       -> MSB grouping
n <= 20 choose subset  -> bitmask enumeration
repeated operation     -> conservation/invariant
construct AND/OR       -> solve each bit independently
```

## Table of Contents

1.  [Parity / LSB](#pattern-1-parity--lsb)
2.  [Power of Two / Remove Lowest Set
    Bit](#pattern-2-power-of-two--remove-lowest-set-bit)
3.  [Kth-bit Masking](#pattern-3-kth-bit-masking)
4.  [XOR Cancellation](#pattern-4-xor-cancellation)
5.  [Global XOR / Solve for X](#pattern-5-global-xor--solve-for-x)
6.  [XOR 1..N Cycle](#pattern-6-xor-1..n-cycle)
7.  [Prefix XOR](#pattern-7-prefix-xor)
8.  [XOR Difference Mask / Hamming
    Bits](#pattern-8-xor-difference-mask--hamming-bits)
9.  [Modulo 2\^k = Binary Suffix](#pattern-9-modulo-2k-=-binary-suffix)
10. [Highest Set Bit / MSB
    Grouping](#pattern-10-highest-set-bit--msb-grouping)
11. [Lowest Set Bit / 2-adic
    Structure](#pattern-11-lowest-set-bit--2-adic-structure)
12. [OR Monotonicity / Required
    Bits](#pattern-12-or-monotonicity--required-bits)
13. [AND Monotonicity / Maximal
    AND](#pattern-13-and-monotonicity--maximal-and)
14. [Bit Frequency / Majority Per
    Bit](#pattern-14-bit-frequency--majority-per-bit)
15. [Bit-by-Bit Constraint
    Construction](#pattern-15-bit-by-bit-constraint-construction)
16. [Pairwise XOR Contribution](#pattern-16-pairwise-xor-contribution)
17. [Pairwise AND / OR
    Contribution](#pattern-17-pairwise-and--or-contribution)
18. [Conservation / Operation
    Decoding](#pattern-18-conservation--operation-decoding)
19. [Highest Bit -\> Lowest Bit
    Greedy](#pattern-19-highest-bit--%3E-lowest-bit-greedy)
20. [Prefix Counts of Bits](#pattern-20-prefix-counts-of-bits)
21. [Common Binary Prefix / Range
    AND](#pattern-21-common-binary-prefix--range-and)
22. [Complement Within Fixed
    Width](#pattern-22-complement-within-fixed-width)
23. [Subset Enumeration](#pattern-23-subset-enumeration)
24. [Bitmask as State](#pattern-24-bitmask-as-state)
25. [Submask Enumeration](#pattern-25-submask-enumeration)
26. [Bitmask DP](#pattern-26-bitmask-dp)

------------------------------------------------------------------------

## Master Recognition Table

  ---------------------------------------------------------------------
  Form                               First signal to test
  ---------------------------------- ----------------------------------
  Parity / LSB                       odd/even, parity, alternating
                                     parity, operations preserving
                                     parity

  Power of Two / Remove Lowest Set   power of two, odd divisor,
  Bit                                repeatedly divide by 2, exactly
                                     one set bit

  Kth-bit Masking                    bit k, flag,
                                     enable/disable/toggle/check
                                     feature

  XOR Cancellation                   pairs, duplicates, one unpaired
                                     value, same value twice

  Global XOR / Solve for X           choose x, XOR all after
                                     transformation, make total XOR
                                     zero

  XOR 1..N Cycle                     XOR consecutive integers, XOR
                                     \[L,R\], huge N but consecutive

  Prefix XOR                         many XOR range queries, subarray
                                     XOR, remove common prefix

  XOR Difference Mask / Hamming Bits make A equal B, differing
                                     positions, flips, XOR distance

  Modulo 2\^k = Binary Suffix        mod 2/4/8/16, powers-of-two
                                     modulus, distinct remainders

  Highest Set Bit / MSB Grouping     same highest bit, pairs whose XOR
                                     is smaller, power-of-two buckets

  Lowest Set Bit / 2-adic Structure  rightmost 1, largest power of two
                                     dividing x, isolate/remove low bit

  OR Monotonicity / Required Bits    accumulate OR, target X, forbidden
                                     bits, prefixes from stacks

  AND Monotonicity / Maximal AND     maximize AND, operations can set
                                     bits, choose highest valuable bits

  Bit Frequency / Majority Per Bit   construct number minimizing total
                                     XOR, majority bit, count ones
                                     column-wise

  Bit-by-Bit Constraint Construction construct array/number satisfying
                                     AND/OR equations

  Pairwise XOR Contribution          sum XOR over all pairs, O(n\^2)
                                     too large, contribution technique

  Pairwise AND / OR Contribution     sum AND/OR over pairs, count per
                                     bit

  Conservation / Operation Decoding  repeated operations, replace two
                                     values, possible/impossible,
                                     invariant

  Highest Bit -\> Lowest Bit Greedy  maximize/minimize integer, budget,
                                     can set chosen bits, lexicographic
                                     binary value

  Prefix Counts of Bits              many range queries asking
                                     count/set bits/AND/OR feasibility

  Common Binary Prefix / Range AND   AND of every integer from L to R,
                                     common prefix

  Complement Within Fixed Width      opposite bits, pair numbers with
                                     complementary low k bits

  Subset Enumeration                 n \<= 20, choose any subset, all
                                     combinations

  Bitmask as State                   few skills/features/categories,
                                     need cover all, each item provides
                                     subset

  Submask Enumeration                iterate every subset of an
                                     existing mask, partition mask

  Bitmask DP                         n around 15-22, state is
                                     chosen/used subset,
                                     assignment/TSP-like
  ---------------------------------------------------------------------

------------------------------------------------------------------------

# Pattern 1 --- Parity / LSB

## 60-Second Recognition Card

**Signals:** odd/even, parity, alternating parity, operations preserving
parity

**Trigger thought:** `x & 1` reads the least-significant bit.

``` text
STATEMENT
   |
   v
ODD/EVEN
   |
   v
PARITY / LSB
```

## Representative Problem

**1367B Even Array** ---
https://codeforces.com/problemset/problem/1367/B

## Bit-by-Bit Dry Run

``` text
a = [3,2,7,6]
bits:  11 10 111 110
LSB :   1  0   1   0
index parity and value parity can be compared with `(a[i]&1) != (i&1)`.
```

## Why the Form Works

The key is to stop treating the values as opaque decimal integers. Draw
the relevant binary positions, determine which columns can be handled
independently, and then use the bit identity/invariant above. Before
implementation, write the one-line proof for why the transformation
preserves or optimizes the required quantity.

## C++ Form / Solution Core

``` cpp
int badEven=0,badOdd=0;
for(int i=0;i<n;i++){
    if((a[i]&1)!=(i&1)){
        if(a[i]&1) badOdd++;
        else badEven++;
    }
}
cout << (badEven==badOdd ? badEven : -1);
```

## Contest Checklist

``` text
[ ] What exact bit property is the statement exposing?
[ ] Can I solve each bit independently?
[ ] Is there cancellation/invariance?
[ ] Is a power of two hiding a suffix/prefix boundary?
[ ] Does MSB -> LSB greediness apply?
[ ] Can I prove the chosen bit never needs to be undone?
```

### More Problems

-   1475A Odd Divisor

------------------------------------------------------------------------

# Pattern 2 --- Power of Two / Remove Lowest Set Bit

## 60-Second Recognition Card

**Signals:** power of two, odd divisor, repeatedly divide by 2, exactly
one set bit

**Trigger thought:** `x & (x-1)` removes the lowest set bit; a positive
power of two becomes zero.

``` text
STATEMENT
   |
   v
POWER OF TWO
   |
   v
POWER OF TWO / REMOVE LOWEST SET BIT
```

## Representative Problem

**1475A Odd Divisor** ---
https://codeforces.com/problemset/problem/1475/A

## Bit-by-Bit Dry Run

``` text
x=8:   1000
x-1=7: 0111
        ----
&       0000

x=12:  1100
x-1:   1011
        ----
&       1000 != 0
```

## Why the Form Works

The key is to stop treating the values as opaque decimal integers. Draw
the relevant binary positions, determine which columns can be handled
independently, and then use the bit identity/invariant above. Before
implementation, write the one-line proof for why the transformation
preserves or optimizes the required quantity.

## C++ Form / Solution Core

``` cpp
bool power2(long long x){ return x>0 && (x&(x-1))==0; }
// Odd Divisor: NO only when n is a power of two.
cout << (power2(n) ? "NO" : "YES");
```

## Contest Checklist

``` text
[ ] What exact bit property is the statement exposing?
[ ] Can I solve each bit independently?
[ ] Is there cancellation/invariance?
[ ] Is a power of two hiding a suffix/prefix boundary?
[ ] Does MSB -> LSB greediness apply?
[ ] Can I prove the chosen bit never needs to be undone?
```

### More Problems

-   1527A And Then There Were K

------------------------------------------------------------------------

# Pattern 3 --- Kth-bit Masking

## 60-Second Recognition Card

**Signals:** bit k, flag, enable/disable/toggle/check feature

**Trigger thought:** Build a one-hot mask `1LL<<k`; combine with
AND/OR/XOR.

``` text
STATEMENT
   |
   v
BIT K
   |
   v
KTH-BIT MASKING
```

## Representative Problem

**Bit operation form**

## Bit-by-Bit Dry Run

``` text
x      = 101000
mask k=2= 000100

CHECK:  x & mask
SET:    x | mask = 101100
TOGGLE: x ^ mask = 101100
CLEAR:  x & ~mask
```

## Why the Form Works

The key is to stop treating the values as opaque decimal integers. Draw
the relevant binary positions, determine which columns can be handled
independently, and then use the bit identity/invariant above. Before
implementation, write the one-line proof for why the transformation
preserves or optimizes the required quantity.

## C++ Form / Solution Core

``` cpp
bool check(long long x,int k){return x&(1LL<<k);}
void setBit(long long&x,int k){x|=1LL<<k;}
void toggle(long long&x,int k){x^=1LL<<k;}
void clearBit(long long&x,int k){x&=~(1LL<<k);}
```

## Contest Checklist

``` text
[ ] What exact bit property is the statement exposing?
[ ] Can I solve each bit independently?
[ ] Is there cancellation/invariance?
[ ] Is a power of two hiding a suffix/prefix boundary?
[ ] Does MSB -> LSB greediness apply?
[ ] Can I prove the chosen bit never needs to be undone?
```

------------------------------------------------------------------------

# Pattern 4 --- XOR Cancellation

## 60-Second Recognition Card

**Signals:** pairs, duplicates, one unpaired value, same value twice

**Trigger thought:** `x^x=0`; XOR is associative/commutative, so equal
pairs disappear.

``` text
STATEMENT
   |
   v
PAIRS
   |
   v
XOR CANCELLATION
```

## Representative Problem

**1698A XOR Mixup** --- https://codeforces.com/problemset/problem/1698/A

## Bit-by-Bit Dry Run

``` text
5 ^ 3 ^ 7 ^ 3 ^ 5
= (5^5) ^ (3^3) ^ 7
= 0 ^ 0 ^ 7
= 7
```

## Why the Form Works

The key is to stop treating the values as opaque decimal integers. Draw
the relevant binary positions, determine which columns can be handled
independently, and then use the bit identity/invariant above. Before
implementation, write the one-line proof for why the transformation
preserves or optimizes the required quantity.

## C++ Form / Solution Core

``` cpp
long long xr=0;
for(long long x:a) xr^=x;
cout<<xr;
```

## Contest Checklist

``` text
[ ] What exact bit property is the statement exposing?
[ ] Can I solve each bit independently?
[ ] Is there cancellation/invariance?
[ ] Is a power of two hiding a suffix/prefix boundary?
[ ] Does MSB -> LSB greediness apply?
[ ] Can I prove the chosen bit never needs to be undone?
```

### More Problems

-   1805A We Need the Zero

------------------------------------------------------------------------

# Pattern 5 --- Global XOR / Solve for X

## 60-Second Recognition Card

**Signals:** choose x, XOR all after transformation, make total XOR zero

**Trigger thought:** Write the total XOR algebraically. If the same `x`
is applied to every element, its effect depends on `n` parity.

``` text
STATEMENT
   |
   v
CHOOSE X
   |
   v
GLOBAL XOR / SOLVE FOR X
```

## Representative Problem

**1805A We Need the Zero** ---
https://codeforces.com/problemset/problem/1805/A

## Bit-by-Bit Dry Run

``` text
Let S = a1^a2^...^an.
After XORing every element with x:
S' = S ^ (x repeated n times)

n even -> x cancels -> S'=S
n odd  -> S'=S^x

Need S'=0:
n odd -> x=S
n even -> possible only if S=0.
```

## Why the Form Works

The key is to stop treating the values as opaque decimal integers. Draw
the relevant binary positions, determine which columns can be handled
independently, and then use the bit identity/invariant above. Before
implementation, write the one-line proof for why the transformation
preserves or optimizes the required quantity.

## C++ Form / Solution Core

``` cpp
long long xr=0;
for(auto x:a) xr^=x;
if(xr==0) cout<<0;
else if(n%2==0) cout<<-1;
else cout<<xr;
```

## Contest Checklist

``` text
[ ] What exact bit property is the statement exposing?
[ ] Can I solve each bit independently?
[ ] Is there cancellation/invariance?
[ ] Is a power of two hiding a suffix/prefix boundary?
[ ] Does MSB -> LSB greediness apply?
[ ] Can I prove the chosen bit never needs to be undone?
```

------------------------------------------------------------------------

# Pattern 6 --- XOR 1..N Cycle

## 60-Second Recognition Card

**Signals:** XOR consecutive integers, XOR \[L,R\], huge N but
consecutive

**Trigger thought:** `xor(1..n)` repeats by `n mod 4`.

``` text
STATEMENT
   |
   v
XOR CONSECUTIVE INTEGERS
   |
   v
XOR 1..N CYCLE
```

## Representative Problem

**Range XOR form**

## Bit-by-Bit Dry Run

``` text
n%4: 0 -> n
     1 -> 1
     2 -> n+1
     3 -> 0

XOR(L..R)=F(R)^F(L-1)
```

## Why the Form Works

The key is to stop treating the values as opaque decimal integers. Draw
the relevant binary positions, determine which columns can be handled
independently, and then use the bit identity/invariant above. Before
implementation, write the one-line proof for why the transformation
preserves or optimizes the required quantity.

## C++ Form / Solution Core

``` cpp
long long F(long long n){
    if(n%4==0) return n;
    if(n%4==1) return 1;
    if(n%4==2) return n+1;
    return 0;
}
```

## Contest Checklist

``` text
[ ] What exact bit property is the statement exposing?
[ ] Can I solve each bit independently?
[ ] Is there cancellation/invariance?
[ ] Is a power of two hiding a suffix/prefix boundary?
[ ] Does MSB -> LSB greediness apply?
[ ] Can I prove the chosen bit never needs to be undone?
```

------------------------------------------------------------------------

# Pattern 7 --- Prefix XOR

## 60-Second Recognition Card

**Signals:** many XOR range queries, subarray XOR, remove common prefix

**Trigger thought:** Exactly like prefix sum, except cancellation uses
`x^x=0`.

``` text
STATEMENT
   |
   v
MANY XOR RANGE QUERIES
   |
   v
PREFIX XOR
```

## Representative Problem

**Range XOR form**

## Bit-by-Bit Dry Run

``` text
a:   a0   a1   a2   a3
px: 0 ->P1 ->P2 ->P3 ->P4

XOR(L..R)
= px[R+1] ^ px[L]

everything before L appears twice and cancels.
```

## Why the Form Works

The key is to stop treating the values as opaque decimal integers. Draw
the relevant binary positions, determine which columns can be handled
independently, and then use the bit identity/invariant above. Before
implementation, write the one-line proof for why the transformation
preserves or optimizes the required quantity.

## C++ Form / Solution Core

``` cpp
vector<long long> px(n+1);
for(int i=0;i<n;i++) px[i+1]=px[i]^a[i];
auto rangeXor=[&](int l,int r){return px[r+1]^px[l];};
```

## Contest Checklist

``` text
[ ] What exact bit property is the statement exposing?
[ ] Can I solve each bit independently?
[ ] Is there cancellation/invariance?
[ ] Is a power of two hiding a suffix/prefix boundary?
[ ] Does MSB -> LSB greediness apply?
[ ] Can I prove the chosen bit never needs to be undone?
```

------------------------------------------------------------------------

# Pattern 8 --- XOR Difference Mask / Hamming Bits

## 60-Second Recognition Card

**Signals:** make A equal B, differing positions, flips, XOR distance

**Trigger thought:** `A^B` has 1 exactly where A and B differ.

``` text
STATEMENT
   |
   v
MAKE A EQUAL B
   |
   v
XOR DIFFERENCE MASK / HAMMING BITS
```

## Representative Problem

**1918C XOR-distance** ---
https://codeforces.com/problemset/problem/1918/C

## Bit-by-Bit Dry Run

``` text
A   = 101101
B   = 100011
      ------
A^B = 001110
        ^^^
Only these positions differ.
```

## Why the Form Works

The key is to stop treating the values as opaque decimal integers. Draw
the relevant binary positions, determine which columns can be handled
independently, and then use the bit identity/invariant above. Before
implementation, write the one-line proof for why the transformation
preserves or optimizes the required quantity.

## C++ Form / Solution Core

``` cpp
long long diff=a^b;
int differing=__builtin_popcountll(diff);
```

## Contest Checklist

``` text
[ ] What exact bit property is the statement exposing?
[ ] Can I solve each bit independently?
[ ] Is there cancellation/invariance?
[ ] Is a power of two hiding a suffix/prefix boundary?
[ ] Does MSB -> LSB greediness apply?
[ ] Can I prove the chosen bit never needs to be undone?
```

------------------------------------------------------------------------

# Pattern 9 --- Modulo 2\^k = Binary Suffix

## 60-Second Recognition Card

**Signals:** mod 2/4/8/16, powers-of-two modulus, distinct remainders

**Trigger thought:** `x % 2^k` is exactly the value of the last `k`
bits.

``` text
STATEMENT
   |
   v
MOD 2/4/8/16
   |
   v
MODULO 2^K = BINARY SUFFIX
```

## Representative Problem

**1909B Make Almost Equal With Mod** ---
https://codeforces.com/problemset/problem/1909/B

## Bit-by-Bit Dry Run

``` text
a=[8,14,22,30]

mod 2, last 1 bit:
8  00100[0] ->0
14 00111[0] ->0
22 01011[0] ->0
30 01111[0] ->0
set={0}

mod 4, last 2 bits:
8  0010[00] ->0
14 0011[10] ->2
22 0101[10] ->2
30 0111[10] ->2
set={0,2} -> exactly two.
```

## Why the Form Works

The key is to stop treating the values as opaque decimal integers. Draw
the relevant binary positions, determine which columns can be handled
independently, and then use the bit identity/invariant above. Before
implementation, write the one-line proof for why the transformation
preserves or optimizes the required quantity.

## C++ Form / Solution Core

``` cpp
for(int i=1;i<=61;i++){
    long long mod=1LL<<i;
    set<long long> st;
    for(auto x:a) st.insert(x%mod);
    if(st.size()==2){ cout<<mod; break; }
}
```

## Contest Checklist

``` text
[ ] What exact bit property is the statement exposing?
[ ] Can I solve each bit independently?
[ ] Is there cancellation/invariance?
[ ] Is a power of two hiding a suffix/prefix boundary?
[ ] Does MSB -> LSB greediness apply?
[ ] Can I prove the chosen bit never needs to be undone?
```

------------------------------------------------------------------------

# Pattern 10 --- Highest Set Bit / MSB Grouping

## 60-Second Recognition Card

**Signals:** same highest bit, pairs whose XOR is smaller, power-of-two
buckets

**Trigger thought:** Numbers in `[2^k,2^(k+1)-1]` share the same MSB.

``` text
STATEMENT
   |
   v
SAME HIGHEST BIT
   |
   v
HIGHEST SET BIT / MSB GROUPING
```

## Representative Problem

**1420B Rock and Lever** ---
https://codeforces.com/problemset/problem/1420/B

## Bit-by-Bit Dry Run

``` text
2,3       -> 10,11      MSB=1
4,5,6,7   -> 100..111   MSB=2
8..15     -> 1000..1111 MSB=3

Group by MSB, then count pairs C(cnt,2).
```

## Why the Form Works

The key is to stop treating the values as opaque decimal integers. Draw
the relevant binary positions, determine which columns can be handled
independently, and then use the bit identity/invariant above. Before
implementation, write the one-line proof for why the transformation
preserves or optimizes the required quantity.

## C++ Form / Solution Core

``` cpp
map<int,long long> cnt;
for(long long x:a){
    int b=63-__builtin_clzll(x);
    cnt[b]++;
}
long long ans=0;
for(auto [b,c]:cnt) ans+=c*(c-1)/2;
```

## Contest Checklist

``` text
[ ] What exact bit property is the statement exposing?
[ ] Can I solve each bit independently?
[ ] Is there cancellation/invariance?
[ ] Is a power of two hiding a suffix/prefix boundary?
[ ] Does MSB -> LSB greediness apply?
[ ] Can I prove the chosen bit never needs to be undone?
```

------------------------------------------------------------------------

# Pattern 11 --- Lowest Set Bit / 2-adic Structure

## 60-Second Recognition Card

**Signals:** rightmost 1, largest power of two dividing x,
isolate/remove low bit

**Trigger thought:** `x&-x` isolates the lowest set bit; `x&(x-1)`
removes it.

``` text
STATEMENT
   |
   v
RIGHTMOST 1
   |
   v
LOWEST SET BIT / 2-ADIC STRUCTURE
```

## Representative Problem

**Bit structure form**

## Bit-by-Bit Dry Run

``` text
x      = 1011000
-x     -> two's complement
x&-x   = 0001000  (lowest set bit)

x-1    = 1010111
x&(x-1)=1010000  (lowest set bit removed)
```

## Why the Form Works

The key is to stop treating the values as opaque decimal integers. Draw
the relevant binary positions, determine which columns can be handled
independently, and then use the bit identity/invariant above. Before
implementation, write the one-line proof for why the transformation
preserves or optimizes the required quantity.

## C++ Form / Solution Core

``` cpp
long long lowbit(long long x){return x&-x;}
while(x){ /* use current set bit */ x&=x-1; }
```

## Contest Checklist

``` text
[ ] What exact bit property is the statement exposing?
[ ] Can I solve each bit independently?
[ ] Is there cancellation/invariance?
[ ] Is a power of two hiding a suffix/prefix boundary?
[ ] Does MSB -> LSB greediness apply?
[ ] Can I prove the chosen bit never needs to be undone?
```

------------------------------------------------------------------------

# Pattern 12 --- OR Monotonicity / Required Bits

## 60-Second Recognition Card

**Signals:** accumulate OR, target X, forbidden bits, prefixes from
stacks

**Trigger thought:** OR only turns bits ON. A bit absent from target
must never be introduced.

``` text
STATEMENT
   |
   v
ACCUMULATE OR
   |
   v
OR MONOTONICITY / REQUIRED BITS
```

## Representative Problem

**1842B Tenzing and Books** ---
https://codeforces.com/problemset/problem/1842/B

## Bit-by-Bit Dry Run

``` text
target x = 10110
candidate = 00110  OK: candidate has no forbidden bit
candidate = 01001  BAD if it contains a 1 where x has 0

test:
(candidate | x) == x
```

## Why the Form Works

The key is to stop treating the values as opaque decimal integers. Draw
the relevant binary positions, determine which columns can be handled
independently, and then use the bit identity/invariant above. Before
implementation, write the one-line proof for why the transformation
preserves or optimizes the required quantity.

## C++ Form / Solution Core

``` cpp
long long cur=0;
for(auto val:sequence){
    if((val|x)!=x) break;
    cur|=val;
}
cout<<(cur==x?"Yes":"No");
```

## Contest Checklist

``` text
[ ] What exact bit property is the statement exposing?
[ ] Can I solve each bit independently?
[ ] Is there cancellation/invariance?
[ ] Is a power of two hiding a suffix/prefix boundary?
[ ] Does MSB -> LSB greediness apply?
[ ] Can I prove the chosen bit never needs to be undone?
```

------------------------------------------------------------------------

# Pattern 13 --- AND Monotonicity / Maximal AND

## 60-Second Recognition Card

**Signals:** maximize AND, operations can set bits, choose highest
valuable bits

**Trigger thought:** AND only loses bits; to keep bit `b` in final AND,
every element must have bit `b` after allowed operations.

``` text
STATEMENT
   |
   v
MAXIMIZE AND
   |
   v
AND MONOTONICITY / MAXIMAL AND
```

## Representative Problem

**1669H Maximal AND** ---
https://codeforces.com/problemset/problem/1669/H

## Bit-by-Bit Dry Run

``` text
For bit b:
count elements with bit b = 0.
cost = zeros.

If cost <= k:
  spend cost
  final AND gets bit b
  k -= cost

Process high -> low.
```

## Why the Form Works

The key is to stop treating the values as opaque decimal integers. Draw
the relevant binary positions, determine which columns can be handled
independently, and then use the bit identity/invariant above. Before
implementation, write the one-line proof for why the transformation
preserves or optimizes the required quantity.

## C++ Form / Solution Core

``` cpp
long long ans=0;
for(int b=30;b>=0;b--){
    int need=0;
    for(int x:a) if(!(x&(1<<b))) need++;
    if(need<=k){k-=need; ans|=1LL<<b;}
}
cout<<ans;
```

## Contest Checklist

``` text
[ ] What exact bit property is the statement exposing?
[ ] Can I solve each bit independently?
[ ] Is there cancellation/invariance?
[ ] Is a power of two hiding a suffix/prefix boundary?
[ ] Does MSB -> LSB greediness apply?
[ ] Can I prove the chosen bit never needs to be undone?
```

------------------------------------------------------------------------

# Pattern 14 --- Bit Frequency / Majority Per Bit

## 60-Second Recognition Card

**Signals:** construct number minimizing total XOR, majority bit, count
ones column-wise

**Trigger thought:** Each bit can often be optimized independently by
counting zeros and ones.

``` text
STATEMENT
   |
   v
CONSTRUCT NUMBER MINIMIZING TOTAL XOR
   |
   v
BIT FREQUENCY / MAJORITY PER BIT
```

## Representative Problem

**1625A Ancient Civilization** ---
https://codeforces.com/problemset/problem/1625/A

## Bit-by-Bit Dry Run

``` text
numbers:
1011
1100
0111
----
bit2 column: 0,1,1 -> ones=2, zeros=1
majority=1 -> set answer bit2.
```

## Why the Form Works

The key is to stop treating the values as opaque decimal integers. Draw
the relevant binary positions, determine which columns can be handled
independently, and then use the bit identity/invariant above. Before
implementation, write the one-line proof for why the transformation
preserves or optimizes the required quantity.

## C++ Form / Solution Core

``` cpp
long long ans=0;
for(int b=0;b<l;b++){
    int ones=0;
    for(int x:a) ones+=(x>>b)&1;
    if(ones>n-ones) ans|=1LL<<b;
}
cout<<ans;
```

## Contest Checklist

``` text
[ ] What exact bit property is the statement exposing?
[ ] Can I solve each bit independently?
[ ] Is there cancellation/invariance?
[ ] Is a power of two hiding a suffix/prefix boundary?
[ ] Does MSB -> LSB greediness apply?
[ ] Can I prove the chosen bit never needs to be undone?
```

------------------------------------------------------------------------

# Pattern 15 --- Bit-by-Bit Constraint Construction

## 60-Second Recognition Card

**Signals:** construct array/number satisfying AND/OR equations

**Trigger thought:** Bitwise constraints separate into independent
Boolean constraints per bit.

``` text
STATEMENT
   |
   v
CONSTRUCT ARRAY/NUMBER SATISFYING AND/OR EQUATIONS
   |
   v
BIT-BY-BIT CONSTRAINT CONSTRUCTION
```

## Representative Problem

**1903B StORage room** ---
https://codeforces.com/problemset/problem/1903/B

## Bit-by-Bit Dry Run

``` text
For each bit:
AND must be 1 -> both participating values need 1.
AND must be 0 -> at least one endpoint needs 0.

Solve/verify the constraints column by column rather than as decimals.
```

## Why the Form Works

The key is to stop treating the values as opaque decimal integers. Draw
the relevant binary positions, determine which columns can be handled
independently, and then use the bit identity/invariant above. Before
implementation, write the one-line proof for why the transformation
preserves or optimizes the required quantity.

## C++ Form / Solution Core

``` cpp
// General form:
for(int b=0;b<B;b++){
    // derive required state of bit b
    // construct candidate
}
// Always verify candidate against every original constraint.
```

## Contest Checklist

``` text
[ ] What exact bit property is the statement exposing?
[ ] Can I solve each bit independently?
[ ] Is there cancellation/invariance?
[ ] Is a power of two hiding a suffix/prefix boundary?
[ ] Does MSB -> LSB greediness apply?
[ ] Can I prove the chosen bit never needs to be undone?
```

------------------------------------------------------------------------

# Pattern 16 --- Pairwise XOR Contribution

## 60-Second Recognition Card

**Signals:** sum XOR over all pairs, O(n\^2) too large, contribution
technique

**Trigger thought:** At bit `b`, XOR is 1 only for a 0/1 pair:
`ones * zeros` pairs.

``` text
STATEMENT
   |
   v
SUM XOR OVER ALL PAIRS
   |
   v
PAIRWISE XOR CONTRIBUTION
```

## Representative Problem

**Contribution form**

## Bit-by-Bit Dry Run

``` text
bit b column:
1
0
1
0
1

ones=3, zeros=2
XOR-contributing unordered pairs = 3*2=6
value per pair = 2^b
contribution = 6*2^b
```

## Why the Form Works

The key is to stop treating the values as opaque decimal integers. Draw
the relevant binary positions, determine which columns can be handled
independently, and then use the bit identity/invariant above. Before
implementation, write the one-line proof for why the transformation
preserves or optimizes the required quantity.

## C++ Form / Solution Core

``` cpp
long long ans=0;
for(int b=0;b<31;b++){
    long long ones=0;
    for(int x:a) ones+=(x>>b)&1;
    long long zeros=n-ones;
    ans += ones*zeros*(1LL<<b);
}
```

## Contest Checklist

``` text
[ ] What exact bit property is the statement exposing?
[ ] Can I solve each bit independently?
[ ] Is there cancellation/invariance?
[ ] Is a power of two hiding a suffix/prefix boundary?
[ ] Does MSB -> LSB greediness apply?
[ ] Can I prove the chosen bit never needs to be undone?
```

### More Problems

-   1879D Sum of XOR Functions (advanced subarray version)

------------------------------------------------------------------------

# Pattern 17 --- Pairwise AND / OR Contribution

## 60-Second Recognition Card

**Signals:** sum AND/OR over pairs, count per bit

**Trigger thought:** AND needs 1/1; OR needs anything except 0/0.

``` text
STATEMENT
   |
   v
SUM AND/OR OVER PAIRS
   |
   v
PAIRWISE AND / OR CONTRIBUTION
```

## Representative Problem

**Contribution form**

## Bit-by-Bit Dry Run

``` text
At bit b with ones=3, zeros=2:

AND pairs = C(3,2)
OR pairs  = totalPairs - C(2,2)

Multiply pair count by 2^b.
```

## Why the Form Works

The key is to stop treating the values as opaque decimal integers. Draw
the relevant binary positions, determine which columns can be handled
independently, and then use the bit identity/invariant above. Before
implementation, write the one-line proof for why the transformation
preserves or optimizes the required quantity.

## C++ Form / Solution Core

``` cpp
long long pairs=1LL*n*(n-1)/2;
for(int b=0;b<31;b++){
    long long one=0;
    for(int x:a) one+=(x>>b)&1;
    long long zero=n-one;
    long long andPairs=one*(one-1)/2;
    long long orPairs=pairs-zero*(zero-1)/2;
}
```

## Contest Checklist

``` text
[ ] What exact bit property is the statement exposing?
[ ] Can I solve each bit independently?
[ ] Is there cancellation/invariance?
[ ] Is a power of two hiding a suffix/prefix boundary?
[ ] Does MSB -> LSB greediness apply?
[ ] Can I prove the chosen bit never needs to be undone?
```

------------------------------------------------------------------------

# Pattern 18 --- Conservation / Operation Decoding

## 60-Second Recognition Card

**Signals:** repeated operations, replace two values,
possible/impossible, invariant

**Trigger thought:** Before simulating, compute what one operation does
to global XOR/AND/OR/parity or per-bit counts.

``` text
STATEMENT
   |
   v
REPEATED OPERATIONS
   |
   v
CONSERVATION / OPERATION DECODING
```

## Representative Problem

**Operation-decoding form**

## Bit-by-Bit Dry Run

``` text
Before:
GLOBAL = a1 ^ a2 ^ ... ^ an

Operation changes p,q.
Cancel unchanged terms:

old contribution = p ^ q
new contribution = p' ^ q'

Compare only these.
If equal, global XOR is conserved.
```

## Why the Form Works

The key is to stop treating the values as opaque decimal integers. Draw
the relevant binary positions, determine which columns can be handled
independently, and then use the bit identity/invariant above. Before
implementation, write the one-line proof for why the transformation
preserves or optimizes the required quantity.

## C++ Form / Solution Core

``` cpp
long long before = p ^ q;
long long after  = np ^ nq;
if(before==after){
    // this operation preserves total XOR
}
```

## Contest Checklist

``` text
[ ] What exact bit property is the statement exposing?
[ ] Can I solve each bit independently?
[ ] Is there cancellation/invariance?
[ ] Is a power of two hiding a suffix/prefix boundary?
[ ] Does MSB -> LSB greediness apply?
[ ] Can I prove the chosen bit never needs to be undone?
```

------------------------------------------------------------------------

# Pattern 19 --- Highest Bit -\> Lowest Bit Greedy

## 60-Second Recognition Card

**Signals:** maximize/minimize integer, budget, can set chosen bits,
lexicographic binary value

**Trigger thought:** A higher bit outweighs all lower bits, so test
feasibility from MSB to LSB.

``` text
STATEMENT
   |
   v
MAXIMIZE/MINIMIZE INTEGER
   |
   v
HIGHEST BIT -> LOWEST BIT GREEDY
```

## Representative Problem

**1669H Maximal AND** ---
https://codeforces.com/problemset/problem/1669/H

## Bit-by-Bit Dry Run

``` text
answer: ????????
try bit 30 -> feasible? set it
try bit 29 -> feasible with previous choices?
...
Never sacrifice an achievable higher bit for lower bits when objective is numeric maximum.
```

## Why the Form Works

The key is to stop treating the values as opaque decimal integers. Draw
the relevant binary positions, determine which columns can be handled
independently, and then use the bit identity/invariant above. Before
implementation, write the one-line proof for why the transformation
preserves or optimizes the required quantity.

## C++ Form / Solution Core

``` cpp
long long ans=0;
for(int b=MAXB;b>=0;b--){
    if(feasible(ans | (1LL<<b)))
        ans |= 1LL<<b;
}
```

## Contest Checklist

``` text
[ ] What exact bit property is the statement exposing?
[ ] Can I solve each bit independently?
[ ] Is there cancellation/invariance?
[ ] Is a power of two hiding a suffix/prefix boundary?
[ ] Does MSB -> LSB greediness apply?
[ ] Can I prove the chosen bit never needs to be undone?
```

### More Problems

-   1918C XOR-distance

------------------------------------------------------------------------

# Pattern 20 --- Prefix Counts of Bits

## 60-Second Recognition Card

**Signals:** many range queries asking count/set bits/AND/OR feasibility

**Trigger thought:** Build one prefix array per bit:
`pref[b][i] = #ones of bit b in [0,i)`.

``` text
STATEMENT
   |
   v
MANY RANGE QUERIES ASKING COUNT/SET BITS/AND/OR FEASIBILITY
   |
   v
PREFIX COUNTS OF BITS
```

## Representative Problem

**Range bit-query form**

## Bit-by-Bit Dry Run

``` text
array: 5  6  3
       101 110 011

bit1:   0   1   1
prefix: 0   0   1   2

ones(bit1,L..R)=pref[1][R+1]-pref[1][L]
```

## Why the Form Works

The key is to stop treating the values as opaque decimal integers. Draw
the relevant binary positions, determine which columns can be handled
independently, and then use the bit identity/invariant above. Before
implementation, write the one-line proof for why the transformation
preserves or optimizes the required quantity.

## C++ Form / Solution Core

``` cpp
const int B=31;
vector<array<int,B>> pref(n+1);
for(int i=0;i<n;i++){
    pref[i+1]=pref[i];
    for(int b=0;b<B;b++) pref[i+1][b]+=(a[i]>>b)&1;
}
```

## Contest Checklist

``` text
[ ] What exact bit property is the statement exposing?
[ ] Can I solve each bit independently?
[ ] Is there cancellation/invariance?
[ ] Is a power of two hiding a suffix/prefix boundary?
[ ] Does MSB -> LSB greediness apply?
[ ] Can I prove the chosen bit never needs to be undone?
```

------------------------------------------------------------------------

# Pattern 21 --- Common Binary Prefix / Range AND

## 60-Second Recognition Card

**Signals:** AND of every integer from L to R, common prefix

**Trigger thought:** Once L and R differ at a bit, lower positions vary
inside the interval and disappear under AND.

``` text
STATEMENT
   |
   v
AND OF EVERY INTEGER FROM L TO R
   |
   v
COMMON BINARY PREFIX / RANGE AND
```

## Representative Problem

**Range AND form**

## Bit-by-Bit Dry Run

``` text
L=26 = 11010
R=29 = 11101
          ^
first difference

common prefix = 11
lower bits -> 0
result = 11000 = 24
```

## Why the Form Works

The key is to stop treating the values as opaque decimal integers. Draw
the relevant binary positions, determine which columns can be handled
independently, and then use the bit identity/invariant above. Before
implementation, write the one-line proof for why the transformation
preserves or optimizes the required quantity.

## C++ Form / Solution Core

``` cpp
int shifts=0;
while(l<r){l>>=1; r>>=1; shifts++;}
cout<<(l<<shifts);
```

## Contest Checklist

``` text
[ ] What exact bit property is the statement exposing?
[ ] Can I solve each bit independently?
[ ] Is there cancellation/invariance?
[ ] Is a power of two hiding a suffix/prefix boundary?
[ ] Does MSB -> LSB greediness apply?
[ ] Can I prove the chosen bit never needs to be undone?
```

------------------------------------------------------------------------

# Pattern 22 --- Complement Within Fixed Width

## 60-Second Recognition Card

**Signals:** opposite bits, pair numbers with complementary low k bits

**Trigger thought:** `~x` flips infinitely/machine-width bits; use a
finite mask `(1<<k)-1`.

``` text
STATEMENT
   |
   v
OPPOSITE BITS
   |
   v
COMPLEMENT WITHIN FIXED WIDTH
```

## Representative Problem

**1926D Vlad and Division** ---
https://codeforces.com/problemset/problem/1926/D

## Bit-by-Bit Dry Run

``` text
k=5
x       10110
mask    11111
        -----
x^mask  01001

Every one of the 5 bits is opposite.
```

## Why the Form Works

The key is to stop treating the values as opaque decimal integers. Draw
the relevant binary positions, determine which columns can be handled
independently, and then use the bit identity/invariant above. Before
implementation, write the one-line proof for why the transformation
preserves or optimizes the required quantity.

## C++ Form / Solution Core

``` cpp
long long mask=(1LL<<31)-1; // choose width required by constraints
long long y=x^mask;
```

## Contest Checklist

``` text
[ ] What exact bit property is the statement exposing?
[ ] Can I solve each bit independently?
[ ] Is there cancellation/invariance?
[ ] Is a power of two hiding a suffix/prefix boundary?
[ ] Does MSB -> LSB greediness apply?
[ ] Can I prove the chosen bit never needs to be undone?
```

------------------------------------------------------------------------

# Pattern 23 --- Subset Enumeration

## 60-Second Recognition Card

**Signals:** n \<= 20, choose any subset, all combinations

**Trigger thought:** Bit i of `mask` means whether item i is chosen.

``` text
STATEMENT
   |
   v
N <= 20
   |
   v
SUBSET ENUMERATION
```

## Representative Problem

**1097B Petr and a Combination Lock** ---
https://codeforces.com/problemset/problem/1097/B

## Bit-by-Bit Dry Run

``` text
n=4
mask=0101
item 3 2 1 0
     0 1 0 1
       ^   ^
      take take
```

## Why the Form Works

The key is to stop treating the values as opaque decimal integers. Draw
the relevant binary positions, determine which columns can be handled
independently, and then use the bit identity/invariant above. Before
implementation, write the one-line proof for why the transformation
preserves or optimizes the required quantity.

## C++ Form / Solution Core

``` cpp
for(int mask=0;mask<(1<<n);mask++){
    int sum=0;
    for(int i=0;i<n;i++){
        if(mask&(1<<i)) sum+=a[i];
        else sum-=a[i];
    }
    if((sum%360+360)%360==0){cout<<"YES"; return;}
}
cout<<"NO";
```

## Contest Checklist

``` text
[ ] What exact bit property is the statement exposing?
[ ] Can I solve each bit independently?
[ ] Is there cancellation/invariance?
[ ] Is a power of two hiding a suffix/prefix boundary?
[ ] Does MSB -> LSB greediness apply?
[ ] Can I prove the chosen bit never needs to be undone?
```

### More Problems

-   550B Preparing Olympiad

------------------------------------------------------------------------

# Pattern 24 --- Bitmask as State

## 60-Second Recognition Card

**Signals:** few skills/features/categories, need cover all, each item
provides subset

**Trigger thought:** Encode a set of features in one integer and combine
with OR.

``` text
STATEMENT
   |
   v
FEW SKILLS/FEATURES/CATEGORIES
   |
   v
BITMASK AS STATE
```

## Representative Problem

**1042B Vitamins** --- https://codeforces.com/problemset/problem/1042/B

## Bit-by-Bit Dry Run

``` text
A=001, B=010, C=100
AB=011
BC=110
ABC=111

buy item AB + item C:
011 | 100 = 111 -> all covered
```

## Why the Form Works

The key is to stop treating the values as opaque decimal integers. Draw
the relevant binary positions, determine which columns can be handled
independently, and then use the bit identity/invariant above. Before
implementation, write the one-line proof for why the transformation
preserves or optimizes the required quantity.

## C++ Form / Solution Core

``` cpp
const int INF=1e9;
vector<int> best(8,INF);
for(each item){
    int m=0;
    for(char c:s) m|=1<<(c-'A');
    best[m]=min(best[m],cost);
}
// combine masks / tiny DP
```

## Contest Checklist

``` text
[ ] What exact bit property is the statement exposing?
[ ] Can I solve each bit independently?
[ ] Is there cancellation/invariance?
[ ] Is a power of two hiding a suffix/prefix boundary?
[ ] Does MSB -> LSB greediness apply?
[ ] Can I prove the chosen bit never needs to be undone?
```

------------------------------------------------------------------------

# Pattern 25 --- Submask Enumeration

## 60-Second Recognition Card

**Signals:** iterate every subset of an existing mask, partition mask

**Trigger thought:** `s=(s-1)&mask` jumps directly to the next submask.

``` text
STATEMENT
   |
   v
ITERATE EVERY SUBSET OF AN EXISTING MASK
   |
   v
SUBMASK ENUMERATION
```

## Representative Problem

**Submask form**

## Bit-by-Bit Dry Run

``` text
mask = 10110
submasks are made only from its 1 positions.

s = mask
s = (s-1)&mask
...
until 0
```

## Why the Form Works

The key is to stop treating the values as opaque decimal integers. Draw
the relevant binary positions, determine which columns can be handled
independently, and then use the bit identity/invariant above. Before
implementation, write the one-line proof for why the transformation
preserves or optimizes the required quantity.

## C++ Form / Solution Core

``` cpp
for(int s=mask;s;s=(s-1)&mask){
    // process s
}
// process 0 separately if needed
```

## Contest Checklist

``` text
[ ] What exact bit property is the statement exposing?
[ ] Can I solve each bit independently?
[ ] Is there cancellation/invariance?
[ ] Is a power of two hiding a suffix/prefix boundary?
[ ] Does MSB -> LSB greediness apply?
[ ] Can I prove the chosen bit never needs to be undone?
```

------------------------------------------------------------------------

# Pattern 26 --- Bitmask DP

## 60-Second Recognition Card

**Signals:** n around 15-22, state is chosen/used subset,
assignment/TSP-like

**Trigger thought:** `dp[mask]` stores the best answer after choosing
exactly the items represented by mask.

``` text
STATEMENT
   |
   v
N AROUND 15-22
   |
   v
BITMASK DP
```

## Representative Problem

**Bitmask-DP form**

## Bit-by-Bit Dry Run

``` text
mask = 01011
        || ||
used:   0 1 0 1 1

transition:
choose an unused bit i
next = mask | (1<<i)
```

## Why the Form Works

The key is to stop treating the values as opaque decimal integers. Draw
the relevant binary positions, determine which columns can be handled
independently, and then use the bit identity/invariant above. Before
implementation, write the one-line proof for why the transformation
preserves or optimizes the required quantity.

## C++ Form / Solution Core

``` cpp
vector<long long> dp(1<<n, INF);
dp[0]=0;
for(int mask=0;mask<(1<<n);mask++){
    for(int i=0;i<n;i++) if(!(mask&(1<<i))){
        int nxt=mask|(1<<i);
        dp[nxt]=min(dp[nxt], dp[mask]+cost(mask,i));
    }
}
```

## Contest Checklist

``` text
[ ] What exact bit property is the statement exposing?
[ ] Can I solve each bit independently?
[ ] Is there cancellation/invariance?
[ ] Is a power of two hiding a suffix/prefix boundary?
[ ] Does MSB -> LSB greediness apply?
[ ] Can I prove the chosen bit never needs to be undone?
```

------------------------------------------------------------------------

# Final 60-Second Decision Tree

``` text
                         BIT-LIKE PROBLEM
                               |
          +--------------------+--------------------+
          |                    |                    |
         XOR                 AND / OR           POWER OF TWO
          |                    |                    |
   +------+-----+        +-----+------+        +----+------+
   |            |        |            |        |           |
pairs?       ranges?   maximize?    target?   %2^k?    one set bit?
   |            |        |            |        |           |
XOR cancel   prefix XOR  MSB greedy  constraints suffix   power-of-2

sum over all pairs?
        |
        v
count 0/1 independently at every bit
        |
        v
contribution technique

many [L,R] questions about bits?
        |
        v
prefix count for every bit

construct answer?
        |
        v
can bits be decided independently?
        |
       yes
        |
        v
bit-by-bit construction / high -> low

n <= ~20 and choose arbitrary items?
        |
        v
subset bitmask / bitmask state / DP

operation repeated many times?
        |
        v
compare BEFORE vs AFTER:
XOR / AND / OR / parity / per-bit count
        |
        v
conservation / invariant
```

# How to Train With This Handbook

Do **not** open the solution immediately. For each random CF problem,
spend the first 60 seconds writing only:

``` text
Signals:
Candidate pattern:
Binary picture:
Invariant/formula:
```

If the pattern is wrong, add the missed signal to that pattern's
recognition card. This turns the handbook into a personal contest
decoder rather than a static theory note.

# Important Combination Forms

Random CF problems often combine two ideas:

``` text
Bits + Prefix Sum       -> prefix count per bit
Bits + Greedy           -> MSB -> LSB construction
Bits + Contribution     -> count 0/1 at every bit
Bits + Math             -> powers of two / modulo suffix
Bits + Constructive     -> satisfy each bit independently
Bits + Invariant        -> decode repeated operations
Bits + DP               -> dp[mask]
Bits + Two Pointers     -> maintain OR/AND/XOR state over a window
```

When no single pattern matches in 60 seconds, test whether the problem
is a **combination of two forms**.

# Reference Formula Sheet

``` cpp
// odd?
x & 1LL

// check/set/clear/toggle k
x & (1LL<<k)
x | (1LL<<k)
x & ~(1LL<<k)
x ^ (1LL<<k)

// power of two
x>0 && (x&(x-1))==0

// remove/isolate lowest set bit
x & (x-1)
x & -x

// number of set bits
__builtin_popcountll(x)

// highest set bit index, x>0
63 - __builtin_clzll(x)

// low k bits / mod 2^k for non-negative x
x & ((1LL<<k)-1)

// submask enumeration
for(int s=mask;s;s=(s-1)&mask) {}

// subset enumeration
for(int mask=0;mask<(1<<n);mask++) {}
```

------------------------------------------------------------------------

# Part II --- Representative Problems Explained at Bit Level

> This section explains the **actual recurring problem forms** rather
> than only giving templates.\
> For every problem, train this sequence:
>
> `statement → signal → binary picture → observation → algorithm → code`

## P1 --- CF 1367B: Even Array

### What is the problem?

Index `i` must have the same parity as `a[i]`. You may swap two
elements. Find the minimum swaps, or `-1` if impossible.

### 60-second recognition

``` text
"index parity must equal value parity"
                 ↓
              x & 1
                 ↓
count the TWO kinds of mismatch
```

### Bit-level example

``` text
index:       0    1    2    3
index LSB:   0    1    0    1

a[i]:        3    2    7    6
binary:     11   10  111  110
value LSB:   1    0    1    0

mismatch:    X    X    X    X
type:       odd  even odd  even
            at   at   at   at
           even odd  even odd
           idx  idx  idx  idx
```

One swap fixes one mismatch of each type:

``` text
odd-at-even-index  <----swap----> even-at-odd-index
```

So the two counts must be equal.

``` cpp
int evenWrong = 0, oddWrong = 0;

for (int i = 0; i < n; ++i) {
    if ((a[i] & 1) != (i & 1)) {
        if (a[i] & 1) ++oddWrong;
        else ++evenWrong;
    }
}

cout << (evenWrong == oddWrong ? evenWrong : -1) << '\n';
```

------------------------------------------------------------------------

## P2 --- CF 1475A: Odd Divisor

### What is the problem?

Determine whether `n` has an odd divisor greater than `1`.

### Recognition

``` text
odd divisor?
    ↓
remove every factor 2
    ↓
anything > 1 remains?
```

The only numbers with **no** odd divisor greater than one are pure
powers of two.

### Bit-level

``` text
8  = 1000   pure power of 2 → NO
16 = 10000  pure power of 2 → NO

12 = 1100
   = 4 × 3
         ^
      odd divisor → YES
```

Power-of-two test:

``` text
x       = 1000
x - 1   = 0111
          ----
x&(x-1) = 0000
```

``` cpp
bool powerOfTwo(long long x) {
    return x > 0 && (x & (x - 1)) == 0;
}

cout << (powerOfTwo(n) ? "NO" : "YES") << '\n';
```

------------------------------------------------------------------------

## P3 --- CF 1698A: XOR Mixup

### What is the problem?

Originally there were `n-1` values. Their XOR `x` was appended to the
array and the array was shuffled. Recover a valid `x`.

### Recognition

``` text
XOR of some values is appended
          ↓
XOR everything
          ↓
cancellation
```

Suppose original values are:

``` text
2 = 010
5 = 101
7 = 111

x = 2 ^ 5 ^ 7
  = 010
    101
    ---
    111
    111
    ---
    000

x = 0
```

In general, final array XOR is:

``` text
a1 ^ a2 ^ ... ^ a(n-1) ^ x

but

x = a1 ^ a2 ^ ... ^ a(n-1)

therefore

x ^ x = 0
```

A valid answer can be obtained from the structure described in the
problem; the standard short solution uses XOR relationships.

``` cpp
long long xr = 0;
for (long long v : a) xr ^= v;

// Depending on the constructive interpretation,
// use the XOR relation required by the statement.
```

The key pattern to learn here is **XOR algebra/cancellation**, not
memorizing one decimal example.

------------------------------------------------------------------------

## P4 --- CF 1805A: We Need the Zero

### What is the problem?

Choose `x`. Replace every `a[i]` with:

``` text
a[i] ^ x
```

The XOR of the resulting array must be zero.

### Algebra first

Let:

``` text
S = a1 ^ a2 ^ ... ^ an
```

After transformation:

``` text
(a1^x) ^ (a2^x) ^ ... ^ (an^x)

= S ^ x ^ x ^ ... ^ x
```

Now parity of `n` matters.

### `n` even

``` text
x ^ x ^ x ^ x ...
\___/   \___/
  0       0

result = S
```

Therefore:

``` text
S = 0     → any x works; choose 0
S != 0    → impossible
```

### `n` odd

``` text
x ^ x ^ x
\___/
  0  ^ x

result = S ^ x
```

Need:

``` text
S ^ x = 0
x = S
```

### Example

``` text
a = [1,2,5]

1 = 001
2 = 010
5 = 101
---------
S = 110 = 6

n=3 odd
x=S=6
```

Transform:

``` text
1^6 = 001 ^ 110 = 111 = 7
2^6 = 010 ^ 110 = 100 = 4
5^6 = 101 ^ 110 = 011 = 3

111
100
011
---
000
```

``` cpp
long long xr = 0;
for (auto v : a) xr ^= v;

if (xr == 0) cout << 0;
else if (n % 2 == 0) cout << -1;
else cout << xr;
```

------------------------------------------------------------------------

## P5 --- CF 1909B: Make Almost Equal With Mod

### What is the problem?

Choose `k`, replace every value by `a[i] % k`, and make the final array
contain exactly two distinct values.

### Recognition

The useful candidates are powers of two:

``` text
2,4,8,16,...
```

Then:

``` text
x % 2^b = last b bits
```

### Example

``` text
a = [8,14,22,30]

 8 = 001000
14 = 001110
22 = 010110
30 = 011110
```

Try `k=2`:

``` text
last 1 bit:

00100[0] → 0
00111[0] → 0
01011[0] → 0
01111[0] → 0

distinct = {0}
```

Try `k=4`:

``` text
last 2 bits:

0010[00] → 0
0011[10] → 2
0101[10] → 2
0111[10] → 2

distinct = {0,2}
```

Exactly two → answer `4`.

``` cpp
for (int b = 1; b <= 61; ++b) {
    long long mod = 1LL << b;
    set<long long> s;

    for (long long x : a)
        s.insert(x % mod);

    if (s.size() == 2) {
        cout << mod << '\n';
        break;
    }
}
```

------------------------------------------------------------------------

## P6 --- CF 1420B: Rock and Lever

### Problem form

The important recognition is that pairs are grouped by their **highest
set bit**.

### Binary buckets

``` text
MSB=0:
1       = 1

MSB=1:
2       = 10
3       = 11

MSB=2:
4       = 100
5       = 101
6       = 110
7       = 111

MSB=3:
8..15   = 1xxx
```

So instead of checking all pairs:

``` text
number → highest set bit → bucket
```

If a bucket contains `c` numbers:

``` text
pairs = c * (c-1) / 2
```

Example:

``` text
a = [4,5,6,9]

4 = 0100 ┐
5 = 0101 ├─ MSB 2 → count=3 → 3 pairs
6 = 0110 ┘

9 = 1001 ─ MSB 3 → count=1 → 0 pairs
```

``` cpp
map<int,long long> cnt;

for (long long x : a) {
    int msb = 63 - __builtin_clzll(x);
    cnt[msb]++;
}

long long ans = 0;
for (auto [b,c] : cnt)
    ans += c * (c - 1) / 2;
```

------------------------------------------------------------------------

## P7 --- CF 1842B: Tenzing and Books

### What is the problem?

There are three stacks. You can only read from each stack top-to-bottom.
Knowledge starts at `0` and becomes:

``` text
knowledge |= book
```

Can you reach exactly `x`?

### Critical OR observation

OR can turn:

``` text
0 → 1
```

but never:

``` text
1 → 0
```

Therefore **never introduce a bit that target `x` does not contain**.

A book `v` is safe iff:

``` cpp
(v | x) == x
```

Equivalent:

``` text
all 1-bits of v are contained inside x
```

### Example

Target:

``` text
x = 7 = 111
```

Books:

``` text
1 = 001  safe
2 = 010  safe
5 = 101  safe

OR:
000
|001 = 001
|010 = 011
|101 = 111
```

But target:

``` text
x = 5 = 101
```

book:

``` text
v = 2 = 010

x | v

101
010
---
111 ≠ 101
```

That forbidden bit can never be removed, so stop that stack when such a
book blocks access to deeper books.

``` cpp
long long cur = 0;

for (auto &stack : stacks) {
    for (long long v : stack) {
        if ((v | x) != x) break;
        cur |= v;
    }
}

cout << (cur == x ? "Yes" : "No");
```

------------------------------------------------------------------------

## P8 --- CF 1669H: Maximal AND

### What is the problem?

You may perform at most `k` operations. One operation sets one chosen
bit of one array element to `1`. Maximize AND of the whole array.

### Recognition

For final AND bit `b` to equal `1`:

``` text
EVERY array element must have bit b = 1.
```

Example:

``` text
a = [2,1,1]
k = 2

2 = 10
1 = 01
1 = 01
```

Consider bit `1` (`2^1=2`):

``` text
       bit1
2=10     1
1=01     0  <- needs operation
1=01     0  <- needs operation

cost = 2
```

We have `k=2`, so set both missing bits:

``` text
10 → 10
01 → 11
01 → 11
```

Now:

``` text
10
11
11
--
10 = 2
```

### Why high-to-low?

``` text
2^30 > sum of all lower bits
```

So for numeric maximization, secure the highest affordable answer bit
first.

``` cpp
long long ans = 0;

for (int b = 30; b >= 0; --b) {
    int need = 0;

    for (long long x : a)
        if (((x >> b) & 1) == 0)
            ++need;

    if (need <= k) {
        k -= need;
        ans |= 1LL << b;
    }
}

cout << ans;
```

------------------------------------------------------------------------

## P9 --- CF 1625A: Ancient Civilization

### Problem form

Construct a binary word/number that minimizes total Hamming distance to
the given words.

### Key idea

Each bit position is independent.

Example:

``` text
a1 = 1011
a2 = 1100
a3 = 1111
a4 = 0010

      b3 b2 b1 b0
a1     1  0  1  1
a2     1  1  0  0
a3     1  1  1  1
a4     0  0  1  0
       ----------
ones   3  2  3  2
zeros  1  2  1  2
```

For each column choose the majority bit; in a tie, follow the problem's
required tie behavior.

Why?

At one bit:

``` text
choose answer bit 0 → cost = number of input 1s
choose answer bit 1 → cost = number of input 0s
```

So choose the cheaper side.

``` cpp
long long ans = 0;

for (int b = 0; b < l; ++b) {
    int ones = 0;

    for (int x : a)
        ones += (x >> b) & 1;

    int zeros = n - ones;

    if (ones > zeros)
        ans |= 1LL << b;
}

cout << ans;
```

------------------------------------------------------------------------

## P10 --- CF 1903B: StORage room

### What is the problem?

Given matrix `M`, find `a` such that for every `i != j`:

``` text
M[i][j] = a[i] | a[j]
```

or report impossible.

### Think one bit at a time

For one particular bit:

``` text
a[i] | a[j] = 0
```

forces:

``` text
a[i] bit = 0
a[j] bit = 0
```

because:

``` text
0|0 = 0
```

While:

``` text
a[i] | a[j] = 1
```

means at least one endpoint has `1`.

So matrix constraints can be decoded **column-by-column in binary**.

Example bit:

``` text
M01 bit = 0
       ↓
a0=0 and a1=0 at this bit

M02 bit = 1
a0 is already 0
       ↓
a2 must be 1
```

General contest strategy:

``` text
construct candidate a[]
       ↓
verify EVERY i,j
       ↓
if any (a[i]|a[j]) != M[i][j]
       ↓
NO
```

Verification is essential in constructive bit problems.

------------------------------------------------------------------------

## P11 --- CF 1918C: XOR-distance

### What is the problem?

Choose `0 <= x <= r` minimizing:

``` text
|(a ^ x) - (b ^ x)|
```

### Recognition

Only positions where `a` and `b` differ matter:

``` text
same bit:
0^x vs 0^x → still same
1^x vs 1^x → still same

different bit:
0/1 can swap their contribution after XORing with 1
```

Example:

``` text
a = 9 = 1001
b = 6 = 0110

diff:
1001
0110
----
1111
```

The highest differing bit determines which side is currently larger.

Process high → low and use `x` bits when affordable under `x <= r` to
reduce the lower-part difference without destroying the dominant
decision.

Mental model:

``` text
highest differing bit
        ↓
determines sign / dominant gap
        ↓
lower differing bits can be flipped
        ↓
try to pull the two values closer
```

This is a **MSB greedy + XOR-difference** combination, not merely an XOR
formula.

------------------------------------------------------------------------

## P12 --- CF 1926D: Vlad and Division

### What is the problem?

Two numbers can share a group only if all of their lowest 31 bits are
opposite.

Therefore the partner of `x` is its 31-bit complement.

### Fixed-width complement

``` text
mask = 111...111   (31 ones)
partner = x ^ mask
```

Small 5-bit illustration:

``` text
x       = 10110
mask    = 11111
          -----
partner = 01001

positions:
1 ↔ 0
0 ↔ 1
1 ↔ 0
1 ↔ 0
0 ↔ 1
```

Pair as many complements as possible. Unpaired values require their own
groups.

``` cpp
const int MASK = (1LL << 31) - 1;
map<long long,int> cnt;
int groups = 0;

for (long long x : a) {
    long long y = x ^ MASK;

    if (cnt[y] > 0) {
        --cnt[y];
    } else {
        ++cnt[x];
        ++groups;
    }
}

cout << groups;
```

------------------------------------------------------------------------

## P13 --- CF 1097B: Petr and a Combination Lock

### What is the problem?

For every angle choose either clockwise `+a[i]` or counter-clockwise
`-a[i]`. Can final rotation be divisible by 360?

### Why bitmask?

Each item has exactly two choices:

``` text
bit 0 → -
bit 1 → +
```

For `n=4`:

``` text
mask = 0101

item:  3 2 1 0
bit :  0 1 0 1

choice:
       - + - +
```

Enumerate all:

``` text
0000
0001
0010
...
1111
```

There are only `2^n` assignments.

``` cpp
for (int mask = 0; mask < (1 << n); ++mask) {
    int sum = 0;

    for (int i = 0; i < n; ++i) {
        if (mask & (1 << i))
            sum += a[i];
        else
            sum -= a[i];
    }

    if ((sum % 360 + 360) % 360 == 0) {
        cout << "YES";
        return;
    }
}

cout << "NO";
```

------------------------------------------------------------------------

## P14 --- CF 1042B: Vitamins

### What is the problem?

Each juice provides some subset of vitamins `A,B,C`. Buy juices with
minimum total cost so that all three are obtained.

### Encode features as bits

``` text
A = 001
B = 010
C = 100

AB  = 011
AC  = 101
BC  = 110
ABC = 111
```

Example:

``` text
juice1: AB = 011, cost 5
juice2: C  = 100, cost 3

011
100
---
111
```

All vitamins obtained for cost `8`.

### State transition

``` text
oldMask | juiceMask = newMask
```

Because OR means "union of features".

``` cpp
const int INF = 1e9;
vector<int> dp(8, INF);
dp[0] = 0;

for (auto [cost, mask] : items) {
    auto ndp = dp;

    for (int s = 0; s < 8; ++s)
        ndp[s | mask] = min(ndp[s | mask], dp[s] + cost);

    dp = ndp;
}

cout << (dp[7] == INF ? -1 : dp[7]);
```

------------------------------------------------------------------------

# Problem-to-Pattern Memory Map

``` text
1367B Even Array
    → LSB / parity

1475A Odd Divisor
    → power of two / x&(x-1)

1698A XOR Mixup
    → XOR algebra / cancellation

1805A We Need the Zero
    → global XOR + n parity

1909B Make Almost Equal With Mod
    → mod 2^k = binary suffix

1420B Rock and Lever
    → group by highest set bit

1842B Tenzing and Books
    → OR only adds bits / forbidden bits

1669H Maximal AND
    → count missing bits + MSB greedy

1625A Ancient Civilization
    → count 0/1 independently per bit

1903B StORage room
    → OR constraints solved per bit + verify

1918C XOR-distance
    → highest differing bit + greedy

1926D Vlad and Division
    → fixed-width complement

1097B Combination Lock
    → bitmask = binary choices

1042B Vitamins
    → bitmask = feature set + OR union
```

# The Four Questions to Ask on Every New Bit Problem

``` text
1. CAN I DRAW EACH NUMBER IN BINARY?
                   |
                   v
2. DOES EACH BIT POSITION ACT INDEPENDENTLY?
          /                      \
        YES                       NO
         |                         |
count/contribution/construct    look for XOR
                               invariant/MSB
                                   |
                                   v
3. IS THERE A POWER OF TWO?
   -> suffix / shift / boundary

4. IS THE OBJECTIVE MAX/MIN?
   -> test MSB -> LSB greediness
```
