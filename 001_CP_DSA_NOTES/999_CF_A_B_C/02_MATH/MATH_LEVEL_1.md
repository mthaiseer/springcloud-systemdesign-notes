# Competitive Programming Math Pattern Master Guide
## Codeforces / CodeChef A-B-C Pattern Recognition Handbook

**Primary goal:** train the gap between reading a contest statement and recognizing the mathematical fact that collapses it.

This is not meant to make every A/B/C problem automatic. Contest setters mix patterns and invent new observations. The goal is to make the **standard mathematical building blocks automatic**, so your attention is free for the new observation.

---

# How to Use This Guide

For every pattern, train this chain:

```text
LONG STATEMENT
      |
      v
REMOVE STORY
      |
      v
VARIABLES + OPERATIONS + GOAL
      |
      v
WHAT CHANGES?
WHAT DOES NOT CHANGE?
      |
      v
PARITY / MOD / GCD / FACTORS / FORMULA / INVARIANT / COUNTING?
      |
      v
BRUTE FORCE ON TINY CASES
      |
      v
OBSERVATION
      |
      v
FORMULA / O(n) / O(log n)
```

During practice, do **not** immediately look at the pattern name. Give yourself 10–15 minutes to derive it.

## The 60-second contest scan

```text
0–15 sec   Remove story; identify input/output.
15–30 sec  Write the operation mathematically.
30–45 sec  Ask what changes / stays invariant.
45–60 sec  test n=1,2,3; odd/even; multiples; prime; power of two.
```

Questions to run mentally:

```text
Does only odd/even matter?
Does only x % k matter?
Is behavior periodic?
Can repeated operations become division/floor/ceil?
Is there a gcd of values or differences?
Can I factor n?
Is n = 2^k * odd_part useful?
Can I solve an equation instead of simulate?
Does an operation preserve sum/parity/gcd/xor?
Is this "print any answer" constructive?
Am I counting pairs/complements?
Can I binary-search an answer with a counting formula?
```

---



# Table of Contents

1. [Parity and Odd/Even State Compression](#1-parity-and-oddeven-state-compression)
2. [Divisibility, Remainders and Residue Classes](#2-divisibility-remainders-and-residue-classes)
3. [Powers of Two and Repeated Halving](#3-powers-of-two-and-repeated-halving)
4. [GCD, LCM and GCD of Differences](#4-gcd-lcm-and-gcd-of-differences)
5. [Prime Factorization and Divisor Structure](#5-prime-factorization-and-divisor-structure)
6. [Floor, Ceiling and Grouping](#6-floor-ceiling-and-grouping)
7. [Periodicity and Modular Cycles](#7-periodicity-and-modular-cycles)
8. [Arithmetic Progressions, Series and Formula Compression](#8-arithmetic-progressions-series-and-formula-compression)
9. [Algebraic Modeling](#9-algebraic-modeling-turn-words-into-equations)
10. [Invariants and Conservation Laws](#10-invariants-and-conservation-laws)
11. [Constructive Mathematics](#11-constructive-mathematics)
12. [Counting Pairs, Complement Counting and Frequencies](#12-counting-pairs-complement-counting-and-frequencies)
13. [Inclusion-Exclusion](#13-inclusionexclusion)
14. [Modular Exponentiation and Safe Modular Arithmetic](#14-modular-exponentiation-and-safe-modular-arithmetic)
15. [Digit Mathematics and Base Representation](#15-digit-mathematics-and-base-representation)
16. [Square Roots, Perfect Squares and Geometric Number Patterns](#16-square-roots-perfect-squares-and-geometric-number-patterns)
17. [Min/Max Bounds and Extremal Math](#17-minmax-bounds-and-extremal-math)
18. [Pigeonhole Principle and Forced Collisions](#18-pigeonhole-principle-and-forced-collisions)
19. [XOR and Bit-Parity Mathematics](#19-xor-and-bitparity-mathematics)
20. [Monotonic Math and Binary Search on Answer](#20-monotonic-math-and-binary-search-on-answer)
21. [Pattern Mixing: Why B/C Feels Harder](#pattern-mixing-why-bc-feels-harder)
22. [Math Gap Diagnostic](#math-gap-diagnostic)
23. [A/B/C Pattern Recognition Table](#abc-pattern-recognition-table)
24. [Contest Scratch-Paper Template](#contest-scratchpaper-template)
25. [Practice Method That Builds Recognition](#practice-method-that-builds-recognition)
26. [Suggested Mastery Order](#suggested-mastery-order)

> **Practice rule:** Problems are intentionally mixed from easier recognition drills to harder combinations. Solve without opening tags. For each miss, record the clue you failed to notice.

---

# 1. Parity and Odd/Even State Compression

## Core idea

Parity compresses an arbitrarily large integer into only two states:

```text
x -> x mod 2

even -> 0
odd  -> 1
```

If every condition and operation cares only about odd/even, the actual magnitude is noise.

### Algebra

```text
(E + E) = E
(O + O) = E
(E + O) = O

E * anything = E
O * O        = O
```

For a sum:

```text
sum is odd <=> number of odd terms is odd
```

### Recognition language

```text
odd / even
sum must be odd
product must be even
add/subtract 1
add/subtract 2
alternate
same parity
different parity
```

## From brute force to pattern

Suppose a problem asks whether some subset can have an odd sum.

A beginner may enumerate subsets. Instead:

```text
odd sum requires odd count of odd numbers.
```

If you are free to select elements, often the entire problem reduces to whether an odd element exists plus whatever size constraints exist.

### ASCII state machine

```text
          + odd
EVEN --------------> ODD
 ^                    |
 |                    |
 +------- odd --------+

+ even keeps the state unchanged.
```

## Worked example: array sum parity

```text
A = [8, 6, 3, 10, 7]

odd elements = {3,7}
count = 2

total parity = even
```

We never needed:

```text
8+6+3+10+7 = 34
```

because:

```text
2 odd terms -> even sum
```

## A-level form

Usually direct classification:

```cpp
int odd = 0;
for (long long x : a)
    odd += (x & 1);

if (odd & 1) ...
```

## B-level form

Parity interacts with an operation.

Example operation:

```text
x -> x + 2
```

Parity is invariant:

```text
odd -> odd
even -> even
```

Therefore any target requiring a parity change is impossible.

## C-level form

Parity may combine with:

- prefix sums,
- graph/bipartite reasoning,
- number of operations,
- invariants,
- constructive choices.

The clue is still the same: reduce each relevant quantity modulo 2.

## Common traps

**Trap 1:** checking actual values when only parity matters.

**Trap 2:** assuming an even sum means every number is even.

Counterexample:

```text
3 + 5 = 8
```

**Trap 3:** forgetting negative odd numbers are still odd. In C++, prefer:

```cpp
(x & 1)
```

for ordinary integer parity, or use `abs(x)%2`.

## 60-second rule

If changing a value by `2k` never affects the answer, immediately try reducing all values modulo 2.

## Practice

1. CF 1296A — Array with Odd Sum  
   https://codeforces.com/problemset/problem/1296/A  
   Hint: count odd and even values.

2. CF 1367B — Even Array  
   https://codeforces.com/problemset/problem/1367/B  
   Hint: compare index parity with value parity.

3. CF 1110A — Parity  
   https://codeforces.com/problemset/problem/1110/A  
   Hint: reason about powers of the base modulo 2.

4. CF 1669C — Odd/Even Increments  
   https://codeforces.com/problemset/problem/1669/C  
   Hint: inspect parity inside each index class.

5. CSES — Coin Piles  
   https://cses.fi/problemset/task/1754  
   Hint: derive necessary equations before simulating.



## Additional Practice — 10 Problems

| # | Problem | Source | Main clue / hint |
|---|---|---|---|
| 1 | [Array with Odd Sum](https://codeforces.com/problemset/problem/1296/A) | CF | An odd sum depends on how many odd terms you choose. |
| 2 | [Even Array](https://codeforces.com/problemset/problem/1367/B) | CF | Compare parity of index and value. |
| 3 | [Parity](https://codeforces.com/problemset/problem/1110/A) | CF | Reduce powers of the base modulo 2. |
| 4 | [Odd/Even Increments](https://codeforces.com/problemset/problem/1669/C) | CF | Same index class must have compatible parity. |
| 5 | [Unit Array](https://codeforces.com/problemset/problem/1834/A) | CF | Sum condition + parity of number of `-1`s. |
| 6 | [Grasshopper on a Line](https://codeforces.com/problemset/problem/1837/A) | CF | Divisibility plus a tiny constructive parity-like split. |
| 7 | [Odd Divisor](https://codeforces.com/problemset/problem/1475/A) | CF | Separate the power-of-two part from the odd part. |
| 8 | [Array Coloring](https://codeforces.com/problemset/problem/1857/A) | CF | Compare parity of total sum with possible partition behavior. |
| 9 | [Game with Integers](https://codeforces.com/problemset/problem/1899/A) | CF | Small-state modulo/parity-style classification. |
| 10 | [Coin Piles](https://cses.fi/problemset/task/1754) | CSES | Derive equations and parity/divisibility conditions. |

---


# 2. Divisibility, Remainders and Residue Classes

## Core idea

Division gives:

```text
x = q*k + r
0 <= r < k
```

For many problems, `q` is irrelevant and the entire state is `r = x % k`.

Numbers fall into residue classes:

```text
mod 5:

0: 0,5,10,15,...
1: 1,6,11,16,...
2: 2,7,12,17,...
3: 3,8,13,18,...
4: 4,9,14,19,...
```

## Recognition language

```text
divisible by
multiple of
remainder
every k
make divisible
minimum additions
same remainder
```

## Worked derivation: distance to next multiple

Want minimum `d >= 0` such that:

```text
x + d ≡ 0 (mod k)
```

Let:

```text
r = x % k
```

Then:

```text
r + d ≡ 0 (mod k)
```

Smallest answer:

```text
d = (k-r) % k
```

### Dry run

```text
x=17, k=5
17 % 5 = 2

17 ---- +3 ----> 20
                   |
                multiple 5
```

## Residue counting pattern

Suppose pairs must satisfy:

```text
(a+b) % k == 0
```

If:

```text
a % k = r
```

then required partner remainder is:

```text
(k-r) % k
```

This turns a pair problem into frequency counting.

```cpp
vector<long long> cnt(k);
for (long long x : a)
    cnt[x % k]++;
```

## A-level

Direct `% k` classification.

## B-level

Count/combine complementary residue classes.

## C-level

Modulo combines with:

- prefix sums,
- cycle detection,
- combinatorics,
- modular arithmetic,
- DP state compression.

## Common traps

Already divisible:

```text
x % k = 0
```

Answer to “add until divisible” is `0`, not `k`.

Hence:

```cpp
(k - x % k) % k
```

## Practice

1. CF 1374A — Required Remainder  
   https://codeforces.com/problemset/problem/1374/A

2. CF 1899A — Game with Integers  
   https://codeforces.com/problemset/problem/1899/A

3. CF 1551A — Polycarp and Coins  
   https://codeforces.com/problemset/problem/1551/A

4. CF 1343A — Candies  
   https://codeforces.com/problemset/problem/1343/A

5. CF 1560A — Dislike of Threes  
   https://codeforces.com/problemset/problem/1560/A



## Additional Practice — 10 Problems

| # | Problem | Source | Main clue / hint |
|---|---|---|---|
| 1 | [Required Remainder](https://codeforces.com/problemset/problem/1374/A) | CF | Write `x = qk+r`. |
| 2 | [Game with Integers](https://codeforces.com/problemset/problem/1899/A) | CF | Enumerate tiny values and look for period 3. |
| 3 | [Polycarp and Coins](https://codeforces.com/problemset/problem/1551/A) | CF | Quotient and remainder modulo 3 determine the split. |
| 4 | [Candies](https://codeforces.com/problemset/problem/1343/A) | CF | Divisibility by a geometric-series denominator. |
| 5 | [Dislike of Threes](https://codeforces.com/problemset/problem/1560/A) | CF | Filter by divisibility/last digit; notice periodic density. |
| 6 | [Division?](https://codeforces.com/problemset/problem/1669/A) | CF | Boundary classification practice. |
| 7 | [Grasshopper on a Line](https://codeforces.com/problemset/problem/1837/A) | CF | If `x%k==0`, construct two non-divisible pieces. |
| 8 | [Buy a Shovel](https://codeforces.com/problemset/problem/732/A) | CF | Last digit / modulo 10 cycle. |
| 9 | [Calculating Function](https://codeforces.com/problemset/problem/486/A) | CF | Odd/even residue determines closed form. |
| 10 | [Josephus Queries](https://cses.fi/problemset/task/2164) | CSES | Repeated positions are controlled by parity and recursion. |

---


# 3. Powers of Two and Repeated Halving

## Core idea

Whenever a process repeatedly divides by 2, separate the power-of-two part:

```text
n = 2^k * m
```

where `m` is odd.

`k` tells how many factors of two exist; `m` is the odd part.

## Worked example: odd divisor

Does `n` contain an odd divisor greater than 1?

```text
40 = 2^3 * 5
             ^
          odd part > 1
=> YES
```

```text
32 = 2^5 * 1
             ^
          no odd part
=> NO
```

ASCII:

```text
40 -> 20 -> 10 -> 5
                   ^
                   odd > 1

32 -> 16 -> 8 -> 4 -> 2 -> 1
                              ^
                              no odd divisor > 1
```

Thus impossible values are exactly:

```text
1,2,4,8,16,32,...
```

i.e. powers of two.

## Bit derivation

A positive power of two has one set bit:

```text
8     = 1000
8 - 1 = 0111
AND     0000
```

So:

```cpp
bool power2(long long n) {
    return n > 0 && (n & (n - 1)) == 0;
}
```

## Why checking 3,5,7 is wrong

```text
22 = 2 * 11
```

11 is an odd divisor, but:

```text
22%3 != 0
22%5 != 0
22%7 != 0
```

The mathematical property is not “divisible by a few small odd primes”; it is “has any odd prime factor.”

## A/B/C progression

A: identify a power of two.

B: count/remove powers of two across an array.

C: redistribute exponents, combine with greedy or number theory.

## Practice

1. CF 1475A — Odd Divisor  
   https://codeforces.com/problemset/problem/1475/A

2. CF 1362A — Johnny and Ancient Computer  
   https://codeforces.com/problemset/problem/1362/A

3. CF 1609A — Divide and Multiply  
   https://codeforces.com/problemset/problem/1609/A

4. CF 1095C — Powers Of Two  
   https://codeforces.com/problemset/problem/1095/C

5. CSES — Bit Strings  
   https://cses.fi/problemset/task/1617



## Additional Practice — 8 Problems

| # | Problem | Source | Main clue / hint |
|---|---|---|---|
| 1 | [Odd Divisor](https://codeforces.com/problemset/problem/1475/A) | CF | `n = 2^k * odd_part`. |
| 2 | [Johnny and Ancient Computer](https://codeforces.com/problemset/problem/1362/A) | CF | Ratio must be a power of two; group factors as 8/4/2. |
| 3 | [Divide and Multiply](https://codeforces.com/problemset/problem/1609/A) | CF | Collect all factors of 2 and place them optimally. |
| 4 | [Powers Of Two](https://codeforces.com/problemset/problem/1095/C) | CF | Split powers of two while preserving total. |
| 5 | [Candies](https://codeforces.com/problemset/problem/1343/A) | CF | Geometric sum `2^k-1`. |
| 6 | [Phoenix and Gold](https://codeforces.com/problemset/problem/1515/A) | CF | Construct while avoiding a forbidden prefix sum. |
| 7 | [Bit Strings](https://cses.fi/problemset/task/1617) | CSES | Number of binary strings is `2^n`. |
| 8 | [Exponentiation](https://cses.fi/problemset/task/1095) | CSES | Repeated squaring is exponent decomposition in binary. |

---


# 4. GCD, LCM and GCD of Differences

## Mental model

`gcd(a,b)` is the largest step size that fits both exactly.

```text
a = g*x
b = g*y
```

This makes GCD natural whenever several quantities must move on the same grid.

Euclid:

```text
gcd(a,b) = gcd(b, a%b)
```

LCM is the first common meeting point of repeating multiples.

```text
lcm(a,b) = a/gcd(a,b)*b
```

## Critical transformation: GCD of differences

If all values should have the same remainder modulo `d`:

```text
a[i] ≡ a[0] (mod d)
```

subtract:

```text
a[i] - a[0] ≡ 0 (mod d)
```

therefore:

```text
d | |a[i]-a[0]|
```

So maximum valid `d` is:

```text
gcd(|a1-a0|, |a2-a0|, ...)
```

### Dry run

```text
14, 26, 38, 50

relative to 14:
0, 12, 24, 36

gcd = 12
```

ASCII:

```text
14 ----12---- 26 ----12---- 38 ----12---- 50
```

## Recognition clues

```text
same remainder
same step
largest common operation size
make all values equal by multiples of x
periods meet
```

## C++ template

```cpp
long long g = 0;
for (int i = 1; i < n; ++i)
    g = gcd(g, llabs(a[i] - a[0]));
```

## Trap

Do not automatically take `gcd(all a[i])`. If the problem concerns making values equal by adding/subtracting the same step, **differences** often matter.

## Practice

1. CF 1325A — EhAb AnD gCd  
   https://codeforces.com/problemset/problem/1325/A

2. CF 1370A — Maximum GCD  
   https://codeforces.com/problemset/problem/1370/A

3. CF 1593D1 — All are Same  
   https://codeforces.com/problemset/problem/1593/D1

4. CF 1618C — Paint the Array  
   https://codeforces.com/problemset/problem/1618/C

5. CF 1665A — GCD vs LCM  
   https://codeforces.com/problemset/problem/1665/A

6. CSES — Common Divisors  
   https://cses.fi/problemset/task/1081



## Additional Practice — 10 Problems

| # | Problem | Source | Main clue / hint |
|---|---|---|---|
| 1 | [EhAb AnD gCd](https://codeforces.com/problemset/problem/1325/A) | CF | Fix one number to make gcd/lcm trivial. |
| 2 | [Maximum GCD](https://codeforces.com/problemset/problem/1370/A) | CF | Search for the largest unavoidable common divisor. |
| 3 | [All are Same](https://codeforces.com/problemset/problem/1593/D1) | CF | GCD of differences. |
| 4 | [Paint the Array](https://codeforces.com/problemset/problem/1618/C) | CF | GCD separately over parity-index groups. |
| 5 | [GCD vs LCM](https://codeforces.com/problemset/problem/1665/A) | CF | Construct easy values satisfying the equation. |
| 6 | [Same Differences](https://codeforces.com/problemset/problem/1520/D) | CF | Algebra converts pair relation to equal transformed values. |
| 7 | [LCM Problem](https://codeforces.com/problemset/problem/1389/A) | CF | Construct `a,b` where one divides the other. |
| 8 | [Common Divisors](https://cses.fi/problemset/task/1081) | CSES | Count multiples of each candidate divisor. |
| 9 | [Counting Divisors](https://cses.fi/problemset/task/1713) | CSES | Factorization gives divisor count. |
| 10 | [Prime Multiples](https://cses.fi/problemset/task/2185) | CSES | LCM controls intersections in inclusion-exclusion. |

---


# 5. Prime Factorization and Divisor Structure

## Core representation

Every `n > 1`:

```text
n = p1^e1 * p2^e2 * ... * pk^ek
```

Many apparently unrelated problems become easy once represented by prime exponents.

## Number of divisors

A divisor chooses exponent:

```text
0..e1 for p1
0..e2 for p2
...
```

Hence:

```text
tau(n) = (e1+1)(e2+1)...(ek+1)
```

### Dry run

```text
72 = 2^3 * 3^2
```

Choices:

```text
2 exponent: 0,1,2,3 -> 4
3 exponent: 0,1,2   -> 3

4*3 = 12 divisors
```

## Exactly three divisors

Need:

```text
(e+1)=3
=> e=2
```

and only one prime.

Therefore:

```text
n = p^2, p prime
```

## Factor movement pattern

When an operation says:

```text
divide one number by p
multiply another by p
```

the **total exponent of p** is invariant.

That observation can turn an array operation into exponent bookkeeping.

## Trial division

```cpp
vector<pair<long long,int>> factorize(long long n) {
    vector<pair<long long,int>> f;
    for (long long p=2; p*p<=n; ++p) {
        if (n%p==0) {
            int e=0;
            while (n%p==0) {
                n/=p;
                ++e;
            }
            f.push_back({p,e});
        }
    }
    if (n>1) f.push_back({n,1});
    return f;
}
```

## Practice

1. CF 230B — T-primes  
   https://codeforces.com/problemset/problem/230/B

2. CF 26A — Almost Prime  
   https://codeforces.com/problemset/problem/26/A

3. CF 1294C — Product of Three Numbers  
   https://codeforces.com/problemset/problem/1294/C

4. CF 1609A — Divide and Multiply  
   https://codeforces.com/problemset/problem/1609/A

5. CSES — Counting Divisors  
   https://cses.fi/problemset/task/1713

6. CSES — Divisor Analysis  
   https://cses.fi/problemset/task/2182



## Additional Practice — 9 Problems

| # | Problem | Source | Main clue / hint |
|---|---|---|---|
| 1 | [T-primes](https://codeforces.com/problemset/problem/230/B) | CF | Exactly three divisors iff square of a prime. |
| 2 | [Almost Prime](https://codeforces.com/problemset/problem/26/A) | CF | Count distinct prime factors. |
| 3 | [Product of Three Numbers](https://codeforces.com/problemset/problem/1294/C) | CF | Extract distinct factors carefully. |
| 4 | [Odd Divisor](https://codeforces.com/problemset/problem/1475/A) | CF | Factorization specialized to prime 2. |
| 5 | [Divide and Multiply](https://codeforces.com/problemset/problem/1609/A) | CF | Move prime exponent 2 between values. |
| 6 | [Counting Divisors](https://cses.fi/problemset/task/1713) | CSES | Use `(e1+1)(e2+1)...`. |
| 7 | [Divisor Analysis](https://cses.fi/problemset/task/2182) | CSES | Work directly with prime exponents. |
| 8 | [Prime Multiples](https://cses.fi/problemset/task/2185) | CSES | Products of selected primes describe intersections. |
| 9 | [Common Divisors](https://cses.fi/problemset/task/1081) | CSES | Reverse viewpoint: candidate divisor -> multiples. |

---


# 6. Floor, Ceiling and Grouping

## Core idea

If each group holds at most `k` objects:

```text
groups = ceil(n/k)
```

For positive integers:

```text
ceil(n/k) = n/k + (n%k != 0)
```

or:

```text
(n+k-1)/k
```

## Dry run

```text
13 items, capacity 4

[4][4][4][1]
             ^
             partial group still needs a container

answer = 4
```

## Recognition clues

```text
minimum boxes
minimum days
at most k per operation
batches
complete groups
how many machines/trips
```

## Deeper B/C pattern: floor counting

If one machine makes an item every `t` seconds, by time `T` it makes:

```text
floor(T/t)
```

With machines:

```text
produced(T) = Σ floor(T / t[i])
```

Now the original scheduling problem becomes:

```text
find minimum T such that produced(T) >= target
```

That predicate is monotonic, leading to binary search.

This is an important bridge:

```text
floor counting -> monotonic predicate -> binary search on answer
```

## Practice

1. CF 1359A — Berland Poker  
   https://codeforces.com/problemset/problem/1359/A

2. CF 1360A — Minimal Square  
   https://codeforces.com/problemset/problem/1360/A

3. CSES — Factory Machines  
   https://cses.fi/problemset/task/1620

4. CSES — Trailing Zeros  
   https://cses.fi/problemset/task/1618



## Additional Practice — 8 Problems

| # | Problem | Source | Main clue / hint |
|---|---|---|---|
| 1 | [Berland Poker](https://codeforces.com/problemset/problem/1359/A) | CF | Capacity per player is a ceiling/floor bound. |
| 2 | [Minimal Square](https://codeforces.com/problemset/problem/1360/A) | CF | Convert placement constraints into minimum side length. |
| 3 | [Required Remainder](https://codeforces.com/problemset/problem/1374/A) | CF | Quotient determines the largest valid candidate. |
| 4 | [Park Lighting](https://codeforces.com/problemset/problem/1358/A) | CF | Pair cells; answer is essentially a ceiling. |
| 5 | [Factory Machines](https://cses.fi/problemset/task/1620) | CSES | `floor(T/t[i])` + binary search. |
| 6 | [Trailing Zeros](https://cses.fi/problemset/task/1618) | CSES | Count `floor(n/5)+floor(n/25)+...`. |
| 7 | [Array Division](https://cses.fi/problemset/task/1085) | CSES | Feasibility for a maximum segment sum is monotonic. |
| 8 | [Number Spiral](https://cses.fi/problemset/task/1071) | CSES | Square-layer boundaries use floor/ceil-like reasoning. |

---


# 7. Periodicity and Modular Cycles

## Core idea

If after `k` operations the state repeats:

```text
state(i+k) = state(i)
```

then:

```text
state(i) depends only on i%k
```

Huge `n` collapses to a tiny remainder.

## Example: last digit of powers of 2

```text
2^1 -> 2
2^2 -> 4
2^3 -> 8
2^4 -> 6
2^5 -> 2
...
```

Cycle:

```text
2,4,8,6
length 4
```

For exponent `n`, inspect `n % 4`.

ASCII:

```text
      +---------------+
      |               |
      v               |
2 -> 4 -> 8 -> 6 -----+
```

## How to discover a cycle

Generate tiny states:

```text
0,1,2,3,4,5,...
```

Write the output underneath. If it repeats, identify:

```text
period length
starting offset
```

Be careful: some sequences have a non-repeating prefix before entering a cycle.

## Practice

1. CF 742A — Arpa’s hard exam and Mehrdad’s terrible code  
   https://codeforces.com/problemset/problem/742/A

2. CF 1899A — Game with Integers  
   https://codeforces.com/problemset/problem/1899/A

3. CSES — Exponentiation  
   https://cses.fi/problemset/task/1095

4. CSES — Josephus Queries  
   https://cses.fi/problemset/task/2164



## Additional Practice — 7 Problems

| # | Problem | Source | Main clue / hint |
|---|---|---|---|
| 1 | [Arpa’s hard exam](https://codeforces.com/problemset/problem/742/A) | CF | Last digits of powers repeat. |
| 2 | [Game with Integers](https://codeforces.com/problemset/problem/1899/A) | CF | Tiny states reveal a period. |
| 3 | [Buy a Shovel](https://codeforces.com/problemset/problem/732/A) | CF | Multiples modulo 10 cycle. |
| 4 | [Calculating Function](https://codeforces.com/problemset/problem/486/A) | CF | Alternation collapses by parity. |
| 5 | [Exponentiation](https://cses.fi/problemset/task/1095) | CSES | Powers modulo `m` + binary exponentiation. |
| 6 | [Exponentiation II](https://cses.fi/problemset/task/1712) | CSES | Nested exponent and modular cycles. |
| 7 | [Josephus Queries](https://cses.fi/problemset/task/2164) | CSES | Repeated elimination has recursive periodic structure. |

---


# 8. Arithmetic Progressions, Series and Formula Compression

## Why this pattern matters

Contest statements often describe a loop in words:

```text
day 1: +1
day 2: +2
...
day n: +n
```

Do not simulate if the contribution has a known formula.

## Core formulas

```text
1+2+...+n = n(n+1)/2

1+3+5+...+(2n-1) = n^2

2+4+...+2n = n(n+1)

AP:
a, a+d, ..., a+(n-1)d

S = n(2a+(n-1)d)/2
```

## Deriving 1..n instead of memorizing

```text
S = 1 + 2 + ... + (n-1) + n
S = n + (n-1) + ... + 2 + 1
--------------------------------
2S=(n+1)+(n+1)+...+(n+1)
```

There are `n` copies:

```text
2S=n(n+1)
S=n(n+1)/2
```

## Pair-count connection

Number of unordered pairs:

```text
(n-1)+(n-2)+...+1
= n(n-1)/2
```

Thus combinatorics and arithmetic series are often the same observation.

## Practice

1. CSES — Two Knights  
   https://cses.fi/problemset/task/1072

2. CSES — Number Spiral  
   https://cses.fi/problemset/task/1071

3. CF 1560C — Infinity Table  
   https://codeforces.com/problemset/problem/1560/C

4. CF 1099B — Squares and Segments  
   https://codeforces.com/problemset/problem/1099/B



## Additional Practice — 8 Problems

| # | Problem | Source | Main clue / hint |
|---|---|---|---|
| 1 | [Calculating Function](https://codeforces.com/problemset/problem/486/A) | CF | Pair consecutive signed terms. |
| 2 | [Infinity Table](https://codeforces.com/problemset/problem/1560/C) | CF | Square layers and offsets. |
| 3 | [Squares and Segments](https://codeforces.com/problemset/problem/1099/B) | CF | Relate `n` to nearby square numbers. |
| 4 | [Park Lighting](https://codeforces.com/problemset/problem/1358/A) | CF | Count cells and pair them. |
| 5 | [Two Knights](https://cses.fi/problemset/task/1072) | CSES | Total pairs minus attacking configurations. |
| 6 | [Number Spiral](https://cses.fi/problemset/task/1071) | CSES | Square sequence gives each layer endpoint. |
| 7 | [Missing Number](https://cses.fi/problemset/task/1083) | CSES | `n(n+1)/2 - observed sum`. |
| 8 | [Coin Piles](https://cses.fi/problemset/task/1754) | CSES | Linear equations and total-operation count. |

---


# 9. Algebraic Modeling: Turn Words into Equations

## The skill

Many A/B problems become trivial only after naming unknowns.

Statement:

```text
Alice has x more than Bob.
Together they have S.
```

Write:

```text
A = B+x
A+B = S
```

Substitute:

```text
2B+x=S
B=(S-x)/2
```

The coding comes last.

## General decoding template

```text
What are the unknowns?
What equations does each sentence imply?
What inequalities come from constraints?
Do integrality/parity conditions appear?
```

## Worked example: sum and difference

```text
x+y=S
x-y=D
```

Add:

```text
2x=S+D
x=(S+D)/2
```

Subtract:

```text
2y=S-D
y=(S-D)/2
```

Feasibility requires appropriate parity and non-negativity.

## Pairwise-sum recovery

Given:

```text
a+b
a+c
b+c
S=a+b+c
```

then:

```text
a=S-(b+c)
b=S-(a+c)
c=S-(a+b)
```

## Practice

1. CF 1618A — Polycarp and Sums  
   https://codeforces.com/problemset/problem/1618/A

2. CF 1294A — Collecting Coins  
   https://codeforces.com/problemset/problem/1294/A

3. CSES — Coin Piles  
   https://cses.fi/problemset/task/1754

4. CF 1551A — Polycarp and Coins  
   https://codeforces.com/problemset/problem/1551/A



## Additional Practice — 8 Problems

| # | Problem | Source | Main clue / hint |
|---|---|---|---|
| 1 | [Polycarp and Sums](https://codeforces.com/problemset/problem/1618/A) | CF | Interpret sorted values as pair sums/total. |
| 2 | [Collecting Coins](https://codeforces.com/problemset/problem/1294/A) | CF | Equal final values give a simple equation. |
| 3 | [Polycarp and Coins](https://codeforces.com/problemset/problem/1551/A) | CF | Solve `a+2b=n` while balancing counts. |
| 4 | [Same Differences](https://codeforces.com/problemset/problem/1520/D) | CF | Rearrange `a[j]-a[i]=j-i`. |
| 5 | [Maximum Increase](https://codeforces.com/problemset/problem/702/A) | CF | Translate wording into a local inequality. |
| 6 | [Coin Piles](https://cses.fi/problemset/task/1754) | CSES | Let operation counts be unknowns and solve. |
| 7 | [Apple Division](https://cses.fi/problemset/task/1623) | CSES | Objective is `|total-2*chosen|`. |
| 8 | [Two Sets](https://cses.fi/problemset/task/1092) | CSES | First derive when total sum can split equally. |

---


# 10. Invariants and Conservation Laws

## Core idea

An invariant is a property unchanged by every legal operation.

If the target has a different invariant value, it is unreachable.

## How to discover one

For one operation, calculate before/after for:

```text
sum
parity
sum mod k
difference
gcd
xor
number of odd values
product sign
```

## Example: transfer

Operation:

```text
a[i] -= 1
a[j] += 1
```

Total:

```text
new sum = old sum -1 +1 = old sum
```

ASCII:

```text
[5,1,3] -> [4,2,3]
   9           9
```

Sum is invariant.

## Example: ±2

```text
x -> x+2
```

Then:

```text
x mod 2
```

is invariant.

## Important distinction

A **monovariant** always moves in one direction rather than staying fixed.

Examples:

```text
number of inversions decreases
maximum decreases
distance to target decreases
```

These help prove termination and operation bounds.

## A/B/C progression

A: obvious parity/sum invariant.

B: infer invariant after experimenting with operations.

C: combine invariant with greedy, constructive, graph, or number theory.

## Practice

1. CF 1538B — Friends and Candies  
   https://codeforces.com/problemset/problem/1538/B

2. CF 1367B — Even Array  
   https://codeforces.com/problemset/problem/1367/B

3. CF 1593D1 — All are Same  
   https://codeforces.com/problemset/problem/1593/D1

4. CSES — Coin Piles  
   https://cses.fi/problemset/task/1754



## Additional Practice — 8 Problems

| # | Problem | Source | Main clue / hint |
|---|---|---|---|
| 1 | [Friends and Candies](https://codeforces.com/problemset/problem/1538/B) | CF | Total sum is preserved; target average must be integral. |
| 2 | [Even Array](https://codeforces.com/problemset/problem/1367/B) | CF | Swapping preserves counts of parity mismatches. |
| 3 | [All are Same](https://codeforces.com/problemset/problem/1593/D1) | CF | Differences reveal the invariant step. |
| 4 | [Unit Array](https://codeforces.com/problemset/problem/1834/A) | CF | Product sign and sum constraints interact. |
| 5 | [Array Coloring](https://codeforces.com/problemset/problem/1857/A) | CF | Total parity is the key feasibility invariant. |
| 6 | [Coin Piles](https://cses.fi/problemset/task/1754) | CSES | Total removed per move and relative pile sizes constrain reachability. |
| 7 | [Two Sets](https://cses.fi/problemset/task/1092) | CSES | Total sum parity is a necessary invariant. |
| 8 | [Missing Number](https://cses.fi/problemset/task/1083) | CSES | Sum or XOR conservation both solve it. |

---


# 11. Constructive Mathematics

## Core idea

If the statement says:

```text
print any valid answer
construct any array
find any pair
```

you do not need all solutions. Find an easy family.

## Construction workflow

```text
conditions
   |
   v
fix easy variables: 0 / 1 / equal / powers of 2
   |
   v
simplify equations
   |
   v
solve remaining variables
   |
   v
verify bounds/distinctness/parity
```

## Worked example

Need:

```text
gcd(a,b)+lcm(a,b)=x
```

Try:

```text
a=1
```

Then:

```text
gcd(1,b)=1
lcm(1,b)=b
```

so:

```text
1+b=x
b=x-1
```

One hard-looking equation becomes trivial.

## Recognition habit

Before searching complicated cases, ask:

```text
Can I set one variable to 1?
Can I make two quantities equal?
Can I use consecutive numbers?
Can I use powers of two to control bits/divisibility?
```

## Practice

1. CF 1325A — EhAb AnD gCd  
   https://codeforces.com/problemset/problem/1325/A

2. CF 1665A — GCD vs LCM  
   https://codeforces.com/problemset/problem/1665/A

3. CF 1294C — Product of Three Numbers  
   https://codeforces.com/problemset/problem/1294/C

4. CF 1095C — Powers Of Two  
   https://codeforces.com/problemset/problem/1095/C



## Additional Practice — 9 Problems

| # | Problem | Source | Main clue / hint |
|---|---|---|---|
| 1 | [EhAb AnD gCd](https://codeforces.com/problemset/problem/1325/A) | CF | Fix one variable to 1. |
| 2 | [GCD vs LCM](https://codeforces.com/problemset/problem/1665/A) | CF | Use repeated simple values. |
| 3 | [Product of Three Numbers](https://codeforces.com/problemset/problem/1294/C) | CF | Construct from factors. |
| 4 | [Powers Of Two](https://codeforces.com/problemset/problem/1095/C) | CF | Repeatedly split a power into two halves. |
| 5 | [Phoenix and Gold](https://codeforces.com/problemset/problem/1515/A) | CF | Reorder to avoid one forbidden prefix. |
| 6 | [Grasshopper on a Line](https://codeforces.com/problemset/problem/1837/A) | CF | One or two summands are enough. |
| 7 | [Twin Permutations](https://codeforces.com/problemset/problem/1831/A) | CF | Complement every value with `n+1-a[i]`. |
| 8 | [Two Sets](https://cses.fi/problemset/task/1092) | CSES | Once feasible, explicitly construct equal sums. |
| 9 | [Gray Code](https://cses.fi/problemset/task/2205) | CSES | Construct sequence with one-bit transitions. |

---


# 12. Counting Pairs, Complement Counting and Frequencies

## Core formulas

Unordered pairs:

```text
C(n,2)=n(n-1)/2
```

One from A and one from B:

```text
|A|*|B|
```

Often:

```text
wanted = total - bad
```

is much easier than direct counting.

## Frequency transformation

If pair validity depends only on category/remainder/value:

```text
array -> frequency table
```

Then count relationships between buckets instead of individual elements.

### Example

Remainders modulo 5:

```text
cnt[0],cnt[1],cnt[2],cnt[3],cnt[4]
```

Pairs whose sum is divisible by 5 come from:

```text
0 + 0
1 + 4
2 + 3
```

This replaces `O(n^2)` pair enumeration with `O(n+k)`.

## Practice

1. CSES — Two Knights  
   https://cses.fi/problemset/task/1072

2. CF 1538C — Challenging Cliffs / pair counting practice  
   https://codeforces.com/problemset/problem/1538/C

3. CSES — Creating Strings  
   https://cses.fi/problemset/task/1622



## Additional Practice — 8 Problems

| # | Problem | Source | Main clue / hint |
|---|---|---|---|
| 1 | [Same Differences](https://codeforces.com/problemset/problem/1520/D) | CF | Transform each index/value to a frequency key. |
| 2 | [Challenging Cliffs](https://codeforces.com/problemset/problem/1538/C) | CF | Count pairs in a sum interval efficiently. |
| 3 | [Two Knights](https://cses.fi/problemset/task/1072) | CSES | Total pairs minus bad pairs. |
| 4 | [Sum of Two Values](https://cses.fi/problemset/task/1640) | CSES | Complement values. |
| 5 | [Distinct Numbers](https://cses.fi/problemset/task/1621) | CSES | Frequency/set compression. |
| 6 | [Creating Strings](https://cses.fi/problemset/task/1622) | CSES | Duplicate frequencies affect number of unique permutations. |
| 7 | [Apartments](https://cses.fi/problemset/task/1084) | CSES | Sorted matching avoids quadratic pair search. |
| 8 | [Ferris Wheel](https://cses.fi/problemset/task/1090) | CSES | Pair extremes greedily under a sum bound. |

---


# 13. Inclusion–Exclusion

## Core idea

When sets overlap, adding their sizes double-counts intersections.

```text
|A union B| = |A|+|B|-|A intersection B|
```

## Divisibility example

Count numbers `<=20` divisible by 3 or 5.

```text
multiples of 3 = floor(20/3)=6
multiples of 5 = floor(20/5)=4
multiples of both = multiples of lcm(3,5)=15
                 = floor(20/15)=1

answer=6+4-1=9
```

ASCII:

```text
A: multiples of 3
B: multiples of 5

        A       B
      (   (15)   )
           ^
       counted twice
```

For several small sets, iterate subsets and alternate signs.

## Recognition clues

```text
at least one
A or B or C
divisible by any
avoid double counting
```

## Practice

1. CSES — Prime Multiples  
   https://cses.fi/problemset/task/2185

2. CSES — Counting Coprime Pairs  
   https://cses.fi/problemset/task/2417



## Additional Practice — 6 Problems

| # | Problem | Source | Main clue / hint |
|---|---|---|---|
| 1 | [Prime Multiples](https://cses.fi/problemset/task/2185) | CSES | Direct subset inclusion-exclusion. |
| 2 | [Counting Coprime Pairs](https://cses.fi/problemset/task/2417) | CSES | Count by common prime factors; Möbius-style extension. |
| 3 | [Common Divisors](https://cses.fi/problemset/task/1081) | CSES | Counting multiples prepares the same divisor-set viewpoint. |
| 4 | [Divisor Analysis](https://cses.fi/problemset/task/2182) | CSES | Prime-exponent structure supports advanced counting. |
| 5 | [Almost Prime](https://codeforces.com/problemset/problem/26/A) | CF | Train classification by prime-factor sets. |
| 6 | [T-primes](https://codeforces.com/problemset/problem/230/B) | CF | Set membership through prime structure. |

---


# 14. Modular Exponentiation and Safe Modular Arithmetic

## Problem

`a^b` is impossible to construct when `b` is huge.

Binary expansion gives:

```text
13 = 8+4+1
a^13 = a^8*a^4*a
```

Repeated squaring computes the result in `O(log b)`.

## Template

```cpp
long long modPow(long long a, long long e, long long mod) {
    long long r = 1 % mod;
    a %= mod;
    while (e) {
        if (e & 1)
            r = (__int128)r * a % mod;
        a = (__int128)a * a % mod;
        e >>= 1;
    }
    return r;
}
```

## Modular identities

```text
(a+b)%m = ((a%m)+(b%m))%m
(a*b)%m = ((a%m)*(b%m))%m
```

But ordinary division is **not**:

```text
(a/b)%m != (a%m)/(b%m)
```

Division modulo a prime usually requires a modular inverse, a later topic.

## Practice

1. CSES — Exponentiation  
   https://cses.fi/problemset/task/1095

2. CSES — Exponentiation II  
   https://cses.fi/problemset/task/1712

3. CSES — Divisor Analysis  
   https://cses.fi/problemset/task/2182

4. CF 742A  
   https://codeforces.com/problemset/problem/742/A



## Additional Practice — 6 Problems

| # | Problem | Source | Main clue / hint |
|---|---|---|---|
| 1 | [Arpa’s hard exam](https://codeforces.com/problemset/problem/742/A) | CF | Small cycle is enough, but compare with binpow. |
| 2 | [Exponentiation](https://cses.fi/problemset/task/1095) | CSES | Standard binary exponentiation. |
| 3 | [Exponentiation II](https://cses.fi/problemset/task/1712) | CSES | Reduce a huge exponent correctly. |
| 4 | [Divisor Analysis](https://cses.fi/problemset/task/2182) | CSES | Modular powers appear inside divisor formulas. |
| 5 | [Bit Strings](https://cses.fi/problemset/task/1617) | CSES | Compute `2^n mod 1e9+7`. |
| 6 | [Creating Strings II](https://cses.fi/problemset/task/1715) | CSES | Factorials and modular inverses extend modular arithmetic. |

---


# 15. Digit Mathematics and Base Representation

## Representation

Digits `d0...dk` in base `b` represent:

```text
N = d0*b^k + d1*b^(k-1) + ... + dk
```

If only `N mod m` is needed, never build huge `N`.

Use Horner:

```cpp
long long rem=0;
for (int d : digits)
    rem=(rem*base+d)%m;
```

## Parity in a base

If base `b` is even:

```text
b^1,b^2,... are even
```

so only the last digit determines parity.

If `b` is odd:

```text
b^k is odd
```

so parity equals parity of the sum of digits.

That is a good example of deriving a contest trick from algebra instead of memorizing it.

## Practice

1. CF 1110A — Parity  
   https://codeforces.com/problemset/problem/1110/A

2. CF 742A  
   https://codeforces.com/problemset/problem/742/A

3. CSES — Number Spiral  
   https://cses.fi/problemset/task/1071



## Additional Practice — 6 Problems

| # | Problem | Source | Main clue / hint |
|---|---|---|---|
| 1 | [Parity](https://codeforces.com/problemset/problem/1110/A) | CF | Analyze base powers modulo 2. |
| 2 | [Dislike of Threes](https://codeforces.com/problemset/problem/1560/A) | CF | Decimal last digit + divisibility. |
| 3 | [Arpa’s hard exam](https://codeforces.com/problemset/problem/742/A) | CF | Last digit cycle. |
| 4 | [Buy a Shovel](https://codeforces.com/problemset/problem/732/A) | CF | Last decimal digit determines success. |
| 5 | [Number Spiral](https://cses.fi/problemset/task/1071) | CSES | Coordinate-number relationship practice. |
| 6 | [Digit Queries](https://cses.fi/problemset/task/2431) | CSES | Group integers by number of decimal digits. |

---


# 16. Square Roots, Perfect Squares and Geometric Number Patterns

## Recognition clues

```text
perfect square
grid
square layers
minimum side
coordinates arranged in expanding squares
```

## Perfect-square test

For integer `n`:

```cpp
long long r = sqrtl((long double)n);
while ((r+1)*(r+1) <= n) ++r;
while (r*r > n) --r;

bool square = (r*r == n);
```

For very large values, guard multiplication with `__int128`.

## Layer pattern

Numbers arranged in square layers often have boundaries:

```text
1^2, 2^2, 3^2, 4^2, ...
```

Given `n`, locate:

```text
k = ceil(sqrt(n))
```

Then reason relative to `k^2`.

This is the heart of several spiral/table problems.

## Dry run

```text
n=20
sqrt(20) ~ 4.47
k=5
k^2=25
```

So 20 belongs to the layer ending at 25. Analyze offset:

```text
25-20=5
```

instead of constructing all previous cells.

## Practice

1. CF 1560C — Infinity Table  
   https://codeforces.com/problemset/problem/1560/C

2. CF 1099B — Squares and Segments  
   https://codeforces.com/problemset/problem/1099/B

3. CF 230B — T-primes  
   https://codeforces.com/problemset/problem/230/B

4. CSES — Number Spiral  
   https://cses.fi/problemset/task/1071



## Additional Practice — 7 Problems

| # | Problem | Source | Main clue / hint |
|---|---|---|---|
| 1 | [Infinity Table](https://codeforces.com/problemset/problem/1560/C) | CF | Locate `n` between consecutive squares. |
| 2 | [Squares and Segments](https://codeforces.com/problemset/problem/1099/B) | CF | Nearest square controls the construction. |
| 3 | [T-primes](https://codeforces.com/problemset/problem/230/B) | CF | Integer square root + primality. |
| 4 | [Minimal Square](https://codeforces.com/problemset/problem/1360/A) | CF | Geometric constraints collapse to side-length math. |
| 5 | [Number Spiral](https://cses.fi/problemset/task/1071) | CSES | Square endpoints define layers. |
| 6 | [Two Knights](https://cses.fi/problemset/task/1072) | CSES | Board size gives polynomial counting formula. |
| 7 | [Chessboard and Queens](https://cses.fi/problemset/task/1624) | CSES | Geometry/diagonal constraints; harder extension. |

---


# 17. Min/Max Bounds and Extremal Math

## Core idea

Some problems look like search, but constraints give a hard lower/upper bound and a construction reaches it.

Proof pattern:

```text
1. prove answer >= X
2. construct solution with answer = X
3. therefore optimum = X
```

## Example concept

If one operation fixes at most `k` bad items and there are `b` bad items:

```text
operations >= ceil(b/k)
```

If you can show a strategy achieving exactly that, the answer is proven.

## Recognition clues

```text
minimum operations
maximum possible gcd
smallest possible maximum
largest possible minimum
```

Ask:

```text
What unavoidable bound exists?
Can I construct a case achieving the bound?
```

## Practice

1. CF 1370A — Maximum GCD  
   https://codeforces.com/problemset/problem/1370/A

2. CF 1359A — Berland Poker  
   https://codeforces.com/problemset/problem/1359/A

3. CSES — Factory Machines  
   https://cses.fi/problemset/task/1620



## Additional Practice — 7 Problems

| # | Problem | Source | Main clue / hint |
|---|---|---|---|
| 1 | [Maximum GCD](https://codeforces.com/problemset/problem/1370/A) | CF | Prove an upper bound, then attain it. |
| 2 | [Berland Poker](https://codeforces.com/problemset/problem/1359/A) | CF | Maximize your share, minimize unavoidable opponent share. |
| 3 | [Minimal Square](https://codeforces.com/problemset/problem/1360/A) | CF | Derive lower bounds on both dimensions. |
| 4 | [Park Lighting](https://codeforces.com/problemset/problem/1358/A) | CF | Each lamp covers at most two cells. |
| 5 | [Factory Machines](https://cses.fi/problemset/task/1620) | CSES | Find minimum feasible time. |
| 6 | [Array Division](https://cses.fi/problemset/task/1085) | CSES | Minimize maximum segment sum. |
| 7 | [Ferris Wheel](https://cses.fi/problemset/task/1090) | CSES | Lower bound from number of people; pair whenever possible. |

---


# 18. Pigeonhole Principle and Forced Collisions

## Core idea

If more objects are placed into fewer categories, some category must receive at least two.

```text
n objects
k boxes
n > k
=> collision guaranteed
```

More generally some box has at least:

```text
ceil(n/k)
```

objects.

## Contest interpretation

Categories are often hidden:

```text
remainders modulo k
parity classes
values in a bounded range
prefix-sum remainders
```

If you have `k+1` prefix sums but only `k` possible remainders, two share a remainder. Their difference is divisible by `k`.

That observation is foundational for later prefix/modulo problems.

ASCII:

```text
prefix remainders:
P0 P1 P2 ... Pk

only boxes:
0 1 2 ... k-1

k+1 objects into k boxes
=> two equal remainders
```

## Recognition clue

Whenever constraints say “many objects but few possible states,” consider pigeonhole.



## Additional Practice — 5 Problems

| # | Problem | Source | Main clue / hint |
|---|---|---|---|
| 1 | [Distinct Numbers](https://cses.fi/problemset/task/1621) | CSES | Basic category/counting viewpoint. |
| 2 | [Missing Number](https://cses.fi/problemset/task/1083) | CSES | Finite-state/value-range reasoning warm-up. |
| 3 | [Common Divisors](https://cses.fi/problemset/task/1081) | CSES | Many numbers sharing divisor buckets force collisions. |
| 4 | [Same Differences](https://codeforces.com/problemset/problem/1520/D) | CF | Equal transformed keys create pairs. |
| 5 | [Parity](https://codeforces.com/problemset/problem/1110/A) | CF | Only two parity boxes exist; use this as the simplest pigeonhole mental model. |

---


# 19. XOR and Bit-Parity Mathematics

## Why it belongs in the math toolkit

XOR is addition without carry in binary. Each bit behaves independently modulo 2.

Core identities:

```text
x ^ x = 0
x ^ 0 = x
x ^ y ^ x = y
```

Therefore duplicate pairs cancel.

## Dry run

```text
5 ^ 7 ^ 5

= (5 ^ 5) ^ 7
= 0 ^ 7
= 7
```

## Recognition clues

```text
all values occur twice except one
toggle
bits independently
operation uses XOR
```

## Bit-by-bit mindset

For AND/OR/XOR objectives, often analyze each bit separately:

```text
bit 0
bit 1
bit 2
...
```

This converts a huge integer problem into many binary problems.

## Practice

1. CSES — Missing Number is arithmetic rather than XOR, but solve it both ways for training.  
   https://cses.fi/problemset/task/1083

2. Continue with Codeforces problems tagged `bitmasks`, `bitwise`, and `constructive algorithms` once parity/modulo are comfortable.



## Additional Practice — 6 Problems

| # | Problem | Source | Main clue / hint |
|---|---|---|---|
| 1 | [Missing Number](https://cses.fi/problemset/task/1083) | CSES | Solve once by sum, once by XOR. |
| 2 | [Gray Code](https://cses.fi/problemset/task/2205) | CSES | Consecutive values differ in one bit. |
| 3 | [Bit Strings](https://cses.fi/problemset/task/1617) | CSES | Binary choices produce `2^n`. |
| 4 | [Odd Divisor](https://codeforces.com/problemset/problem/1475/A) | CF | Bit trick detects powers of two. |
| 5 | [Powers Of Two](https://codeforces.com/problemset/problem/1095/C) | CF | Think in binary decomposition. |
| 6 | [Divide and Multiply](https://codeforces.com/problemset/problem/1609/A) | CF | Bit/exponent-of-two interpretation. |

---


# 20. Monotonic Math and Binary Search on Answer

## Core idea

Sometimes the answer is a number `X`, and you can easily test:

```text
"Is X enough?"
```

If:

```text
X works => every larger X works
```

you have a monotonic predicate.

```text
false false false true true true
                  ^
             first answer
```

Binary search finds the boundary.

## Worked model: machines

Machine `i` produces:

```text
floor(T/t[i])
```

items by time `T`.

Total:

```text
f(T)=Σ floor(T/t[i])
```

As `T` increases, `f(T)` never decreases.

Predicate:

```text
f(T) >= target
```

is monotonic.

## Recognition language

```text
minimum time
minimum capacity
maximum feasible value
can we do it within X?
```

## C++ skeleton

```cpp
long long lo = 0, hi = SOME_SAFE_BOUND;

while (lo < hi) {
    long long mid = lo + (hi-lo)/2;

    if (ok(mid))
        hi = mid;
    else
        lo = mid + 1;
}

cout << lo << '\n';
```

## Practice

1. CSES — Factory Machines  
   https://cses.fi/problemset/task/1620

2. CSES — Array Division  
   https://cses.fi/problemset/task/1085



## Additional Practice — 7 Problems

| # | Problem | Source | Main clue / hint |
|---|---|---|---|
| 1 | [Factory Machines](https://cses.fi/problemset/task/1620) | CSES | `can(T)` is monotonic. |
| 2 | [Array Division](https://cses.fi/problemset/task/1085) | CSES | Can the array be split with maximum sum `X`? |
| 3 | [Apartments](https://cses.fi/problemset/task/1084) | CSES | Solve greedily first; compare with monotonic-search thinking. |
| 4 | [Ferris Wheel](https://cses.fi/problemset/task/1090) | CSES | Bound/feasibility reasoning warm-up. |
| 5 | [Maximum GCD](https://codeforces.com/problemset/problem/1370/A) | CF | Extremal boundary recognition before harder binary-search problems. |
| 6 | [Squares and Segments](https://codeforces.com/problemset/problem/1099/B) | CF | Search for the smallest structural bound satisfying `n`. |
| 7 | [T-primes](https://codeforces.com/problemset/problem/230/B) | CF | Integer-root boundary checks train monotonic numerical reasoning. |

---


# Pattern Mixing: Why B/C Feels Harder

A-level problems often expose the pattern:

```text
odd/even -> parity
divisible -> modulo
```

B-level problems often hide one extra transformation:

```text
operation
   ↓
discover invariant
   ↓
parity/modulo/gcd
```

C-level problems frequently combine two ideas:

```text
prefix sums + modulo
gcd + differences
factorization + greedy
counting + residues
floor counting + binary search
constructive + bit properties
```

So when you cannot classify a B/C immediately, ask:

```text
What is the FIRST transformation?
```

The visible data structure may not be the final pattern.

---

# Math Gap Diagnostic

When you fail a problem, classify the failure.

## Gap A — I could not translate the statement

Train:

```text
variables
equations
operations
constraints
```

## Gap B — I translated it but did not see the property

Train pattern problems from this guide.

## Gap C — I saw the pattern but could not prove it

Write:

```text
necessity: why must this be true?
sufficiency: if true, can I always construct/achieve it?
```

## Gap D — I had the formula but implementation failed

Train:

```text
overflow
integer division
negative modulo
off-by-one
ceil/floor
sqrt precision
```

## Gap E — I chose the wrong pattern

After editorial, record:

```text
What clue did I miss?
What false clue distracted me?
What smallest example reveals the real pattern?
```

This is the most important post-contest note.

---

# A/B/C Pattern Recognition Table

| Visible clue | First thought | Possible deeper version |
|---|---|---|
| odd/even | parity | invariant / prefix parity |
| divisible by k | modulo | residue frequency / prefix mod |
| repeated /2 | powers of 2 | prime exponents |
| same step | gcd differences | number theory construction |
| groups of k | ceil/floor | binary search counting |
| repeating state | modulo cycle | cycle detection |
| sum 1..n | formula | combinatorial counting |
| any valid output | constructive | invariant + construction |
| pairs | nC2/frequencies | two pointers / residue classes |
| several divisibility sets | inclusion-exclusion | subset masks |
| huge exponent | binpow | modular inverse / Euler/Fermat |
| square layers | sqrt | coordinate formula |
| operation preserves property | invariant | monovariant |
| minimum X that works | bound | binary search on answer |

---

# Contest Scratch-Paper Template

Copy mentally or literally:

```text
KNOWN:
n =
operation =
goal =

REMOVE STORY:
________________________________

SMALL CASES:
1:
2:
3:
4:

CHECK:
[ ] parity
[ ] mod
[ ] gcd/lcm
[ ] factors
[ ] power of 2
[ ] floor/ceil
[ ] algebra
[ ] series
[ ] invariant
[ ] counting
[ ] constructive
[ ] bits/xor
[ ] monotonic

WHAT CHANGES?
________________________________

WHAT STAYS SAME?
________________________________

FORMULA / CLAIM:
________________________________

WHY NECESSARY?
________________________________

WHY SUFFICIENT?
________________________________

COUNTEREXAMPLES:
min / max / odd / even / prime / 2^k / k-1,k,k+1
```

---

# Practice Method That Builds Recognition

For each problem:

```text
1. Read without tags.
2. Spend 60 sec classifying possible patterns.
3. Derive brute force for tiny constraints.
4. Generate examples manually.
5. Search for what the brute force results depend on.
6. State the observation in one sentence.
7. Prove it.
8. Code optimized solution.
9. Stress-test brute vs optimized when feasible.
10. Record the recognition clue.
```

Your note should be tiny:

```text
Problem: CF 1475A
Clue: odd divisor
Model: n = 2^k * odd_part
Observation: NO iff odd_part=1
Pattern: power of two / factorization
Missed clue: checking individual small primes is insufficient
```

That is much more useful than copying the full solution.

---

# Suggested Mastery Order

```text
LEVEL 1 — make Div2 A math automatic
Parity
Divisibility
Modulo
Floor/Ceil
Basic algebra
AP / sums
Powers of two

LEVEL 2 — strengthen A/B
GCD/LCM
Prime factors
Perfect squares
Cycles
Constructive math
Counting pairs

LEVEL 3 — strengthen B/C
Invariants
GCD of differences
Residue frequencies
Inclusion-exclusion
Binary exponentiation
Extremal bounds
Pigeonhole
XOR / bit-by-bit
Binary search on answer

LEVEL 4 — next expansion after this handbook
Modular inverse
Fermat/Euler
Sieve/SPF
Combinations nCr
Prefix modulo
Diophantine equations
Chinese remainder theorem basics
Mobius / advanced divisor counting
Expected value / probability basics
```

---


# 100+ Problem Recognition Tracker

Use this table after solving. Do **not** write the full solution.

```text
Problem:
Contest/Rating:
Pattern I first suspected:
Actual pattern:
Trigger clue:
Math equation/invariant:
Why my first idea worked/failed:
Smallest counterexample:
Recognition sentence for next time:
```

Recommended target:

```text
First pass:
5–10 problems per pattern with notes.

Second pass:
mixed A/B/C without tags.

Third pass:
virtual contests; identify candidate pattern in <=60 sec.

Success metric:
not "I solved every problem instantly"
but
"I rarely miss a standard mathematical transformation."
```

---

# Final Goal

Do not aim to memorize:

```text
problem X -> code Y
```

Aim to recognize:

```text
statement
   ↓
mathematical model
   ↓
known structure
   ↓
proof
   ↓
implementation
```

The practical contest target is:

```text
A: recognize standard math almost immediately.
B: identify the hidden transformation quickly.
C: recognize which 2–3 familiar ideas are being combined.
```

A strong pattern library reduces the **math gap**, but novel observation and proof still require practice. The fastest improvement comes from solving mixed problems without tags and recording exactly which clue you missed.
