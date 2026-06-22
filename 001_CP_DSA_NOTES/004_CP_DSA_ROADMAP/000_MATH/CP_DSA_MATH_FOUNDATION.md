# MiniMathEngine_Master_10_10_Polished.md

## Complete CP/DSA Math Foundation Engine — From Child-Level Basics to Codeforces CM + FAANG

> **Goal:** Build math intuition, not formula memory.  
> **Language:** C++17 only.  
> **Audience:** Beginner → strong interview/contest solver.  
> **Teaching style:** first-principles, ASCII diagrams, dry runs, pattern recognition, and safe templates.

---

# Clickable Index

- [000. How To Use This File](#000-how-to-use-this-file)
- [001. The One Mental Model](#001-the-one-mental-model)
- [002. Constraint Router](#002-constraint-router)
- [003. Number Line Thinking](#003-number-line-thinking)
- [004. Algebraic Rearrangement](#004-algebraic-rearrangement)
- [005. Min/Max Bounds](#005-minmax-bounds)
- [006. Absolute Value](#006-absolute-value)
- [007. Powers and Logarithms](#007-powers-and-logarithms)
- [008. Floor, Ceil, Integer Division](#008-floor-ceil-integer-division)
- [009. Parity](#009-parity)
- [010. Invariants](#010-invariants)
- [011. Divisibility, GCD, LCM](#011-divisibility-gcd-lcm)
- [012. Modulo Arithmetic](#012-modulo-arithmetic)
- [013. Modulo Cycles](#013-modulo-cycles)
- [014. Binary Exponentiation](#014-binary-exponentiation)
- [015. Modular Inverse](#015-modular-inverse)
- [016. Prime Checking](#016-prime-checking)
- [017. Sieve and SPF](#017-sieve-and-spf)
- [018. Prime Factorization](#018-prime-factorization)
- [019. Divisor Count and Sum](#019-divisor-count-and-sum)
- [020. Euler Phi](#020-euler-phi)
- [021. Counting Principles](#021-counting-principles)
- [022. nCr Combinations](#022-ncr-combinations)
- [023. nCr Modulo Prime](#023-ncr-modulo-prime)
- [024. Permutations](#024-permutations)
- [025. Stars and Bars](#025-stars-and-bars)
- [026. Inclusion Exclusion](#026-inclusion-exclusion)
- [027. Pigeonhole Principle](#027-pigeonhole-principle)
- [028. Catalan Numbers](#028-catalan-numbers)
- [029. Derangements](#029-derangements)
- [030. Probability and Expected Value](#030-probability-and-expected-value)
- [031. Binary, Bitwise Math, XOR](#031-binary-bitwise-math-xor)
- [032. Geometry Basics](#032-geometry-basics)
- [033. Extended Euclid, Diophantine, CRT](#033-extended-euclid-diophantine-crt)
- [034. Floor Sum Thinking](#034-floor-sum-thinking)
- [035. Codeforces Math Forms](#035-codeforces-math-forms)
- [036. FAANG Math Forms](#036-faang-math-forms)
- [037. Math Bug Encyclopedia](#037-math-bug-encyclopedia)
- [099. Final Cheat Sheet](#099-final-cheat-sheet)

---

# 000. How To Use This File

This file is a **Math Pattern Engine**.

A school textbook often says:

```text
Remember this formula.
Use it in the exam.
```

Competitive Programming asks something different:

```text
Can you see the hidden structure quickly?
Can you convert a huge brute force into a small formula or template?
Can you avoid overflow/modulo/edge-case bugs?
```

## Four-Pass Study Method

```text
Pass 1: Understand pictures.
        Do not code yet.

Pass 2: Type each template by hand.
        Do not copy-paste.

Pass 3: Solve 3 small examples manually.
        Write variable tables.

Pass 4: Solve contest problems.
        Tag each problem:
        modulo / gcd / parity / nCr / prime / invariant / geometry
```

## The Child-Level Rule

For every math idea, ask:

```text
Can I explain this using:
1. boxes,
2. clocks,
3. candies,
4. people,
5. a number line?
```

If yes, you understand it.

---

# 001. The One Mental Model

## Core Idea

Every CP math problem asks:

> **What hidden structure stays true when brute force becomes too slow?**

Example:

```text
Problem:
    Compute 2^1,000,000,000 mod M.

Child brute force:
    multiply 2 one billion times.

CP solution:
    use binary exponentiation.
```

The problem is not about multiplication.
The problem is about **repeated doubling structure**.

## Big Picture

```text
Huge problem
    |
    v
Brute force impossible
    |
    v
Find hidden structure
    |
    +-- same remainder?          -> modulo
    +-- repeated power?          -> binary exponentiation
    +-- common divisor?          -> gcd
    +-- choose objects?          -> nCr
    +-- impossible operation?    -> invariant
    +-- coordinates?             -> geometry
    +-- random average?          -> expectation
    |
    v
Use safe C++17 template
```

## Example: Child-Level Transformation

Suppose there are 1,000,000 children in a circle and you pass a ball 1,000,000,000 times.

Bad idea:

```text
pass ball one by one
```

Math idea:

```text
final position = passes % children
```

This is why modulo exists.

---

# 002. Constraint Router

Before coding, look at constraints.

## The Router

```text
N <= 100             -> O(N^3) may work
N <= 2,000           -> O(N^2) may work
N <= 2e5             -> O(N log N) / O(N)
N <= 1e9             -> O(sqrtN), O(logN), formula
N <= 1e18            -> O(logN), formula, gcd, binpow
Answer very large    -> modulo
Many prime queries   -> sieve
Many nCr queries     -> factorial + inverse factorial
```

## Pattern Router Diagram

```text
Problem statement
      |
      v
Which word appears?
      |
      +-- "divisible", "multiple", "common" -> gcd/lcm/mod
      +-- "ways", "choose", "arrange"       -> combinatorics
      +-- "huge power", "a^b"               -> binpow/cycle
      +-- "possible after operations"       -> invariant/parity
      +-- "prime", "factor", "divisor"      -> sieve/factorization
      +-- "coordinate", "area", "turn"      -> geometry
      +-- "expected", "random"              -> probability
```

## Mini Dry Run: Routing

```text
Problem:
    Count ways to choose 3 players from N.

Word:
    choose

Pattern:
    nCr

Formula:
    C(N,3)
```

```text
Problem:
    Are two machines blinking together again?

Words:
    cycles, together

Pattern:
    LCM
```

```text
Problem:
    Find last digit of 7^10^9.

Words:
    huge power, last digit

Pattern:
    modulo cycle / binary exponentiation
```

---

# 003. Number Line Thinking

## Why This Exists

Numbers are positions.

When you see `a`, `b`, `x`, `L`, `R`, think of points on a road.

```text
<------ negative ------ 0 ------ positive ------>
-5 -4 -3 -2 -1  0  1  2  3  4  5
```

## Core Mental Model

Distance between two points:

```text
distance(a,b) = |a-b|
```

## Child Example

You are at house 3.
Your friend is at house 10.

```text
0---1---2---3---4---5---6---7---8---9---10
            A                           B
```

Distance:

```text
10 - 3 = 7
```

If reversed:

```text
3 - 10 = -7
```

Distance cannot be negative, so:

```text
|3-10| = 7
```

## Pattern Recognition

Use number line when problem says:

```text
closest
minimum distance
maximum distance
range
interval
between L and R
median
absolute difference
```

## ASCII Diagram: Closest To Target

```text
target = 10

0----3------7---10----13--------20
     |      |    |     |         |
     7      3    0     3         10  distance from target
```

## Step-by-Step Dry Run

```text
array  = [3, 7, 13, 20]
target = 10
best   = infinity

x = 3:
    distance = |3 - 10| = 7
    best = 7

x = 7:
    distance = |7 - 10| = 3
    best = 3

x = 13:
    distance = |13 - 10| = 3
    best = 3

x = 20:
    distance = |20 - 10| = 10
    best = 3

answer = 3
```

## Variable Table

```text
+----+----------+------+
| x  | distance | best |
+----+----------+------+
| 3  | 7        | 7    |
| 7  | 3        | 3    |
| 13 | 3        | 3    |
| 20 | 10       | 3    |
+----+----------+------+
```

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    long long target;
    cin >> n >> target;

    vector<long long> a(n);
    for (long long &x : a) cin >> x;

    long long best = LLONG_MAX;

    for (long long x : a) {
        long long distance = llabs(x - target);
        best = min(best, distance);
    }

    cout << best << "\n";
    return 0;
}
```

## Common Bugs

```text
Using int when coordinates are 1e18.
Forgetting absolute value.
Assuming sorted order when array is not sorted.
```

## CP Forms

```text
A/B: closest number, simple distance.
B/C: interval overlap, sort + scan.
C/D: minimize maximum distance, binary search on answer.
FAANG: meeting rooms, closest points, median minimization.
```

---

# 004. Algebraic Rearrangement

## Why This Exists

Algebra changes a hard question into an easy lookup.

## Core Mental Model

Instead of searching both values:

```text
a + b = X
```

rearrange:

```text
b = X - a
```

Now for each `a`, ask:

```text
Have I already seen X-a?
```

## Child Example: Toy Pair

You need total 10 candies.
You already have 3 candies.

```text
need = 10 - 3 = 7
```

So if someone has 7, you are done.

## Brute Force

```text
array = [3, 8, 4, 7]
X = 10

Try all pairs:
3+8 = 11
3+4 = 7
3+7 = 10 yes
```

Time:

```text
O(N^2)
```

## Better Idea

Keep a bag of numbers already seen.

```text
seen = {}
for x:
    need = X - x
    if need in seen -> YES
    put x into seen
```

## ASCII

```text
target X = 10

current x = 7

[ seen numbers ]
  3  8  4

need = 10 - 7 = 3
3 is in seen -> pair found
```

## Step-by-Step Dry Run

```text
X = 10
array = [3, 8, 4, 7]
seen = {}

x = 3:
    need = 7
    seen has 7? no
    insert 3

x = 8:
    need = 2
    seen has 2? no
    insert 8

x = 4:
    need = 6
    seen has 6? no
    insert 4

x = 7:
    need = 3
    seen has 3? yes
    answer = YES
```

## Variable Table

```text
+---+------+-------------+--------+
| x | need | seen before | result |
+---+------+-------------+--------+
| 3 | 7    | {}          | insert |
| 8 | 2    | {3}         | insert |
| 4 | 6    | {3,8}       | insert |
| 7 | 3    | {3,8,4}     | YES    |
+---+------+-------------+--------+
```

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    long long X;
    cin >> n >> X;

    unordered_set<long long> seen;

    for (int i = 0; i < n; i++) {
        long long x;
        cin >> x;

        long long need = X - x;

        if (seen.count(need)) {
            cout << "YES\n";
            return 0;
        }

        seen.insert(x);
    }

    cout << "NO\n";
    return 0;
}
```

## Common Bugs

```text
Insert before checking:
    If X=10 and x=5, you may incorrectly match x with itself.

Overflow:
    X-x can overflow int.

Using vector find:
    Makes solution O(N^2).
```

## CP Forms

```text
A/B: pair sum.
B/C: prefix[j] - prefix[i] = K.
C: count pairs with transformed equation.
D: rearrange equation into hashable state.
FAANG: Two Sum, Subarray Sum Equals K.
```

---

# 005. Min/Max Bounds

## Why This Exists

Sometimes the answer is unknown, but it lives inside a range.

## Core Mental Model

```text
Answer is hidden inside [LOW, HIGH]
```

Example: minimum ship capacity.

```text
weights = [3,2,7,4]

LOW  = max single item = 7
HIGH = sum all items   = 16
```

Why?

```text
Capacity cannot be less than the heaviest package.
Capacity never needs to be more than carrying everything at once.
```

## ASCII

```text
capacity line:

1 2 3 4 5 6 [7 8 9 10 11 12 13 14 15 16]
            ^                            ^
           LOW                          HIGH
```

## Binary Search Shape

```text
Impossible Impossible Impossible Possible Possible Possible
           left side              first possible      right side
```

```text
[ F F F F F T T T T T ]
            ^
        answer
```

## Child Example

You need a lunch box that can hold the biggest sandwich.

```text
sandwich sizes = [3, 2, 7, 4]

box size 6 -> cannot hold 7
box size 7 -> can hold every single sandwich
```

So minimum possible box size starts from 7.

## C++17 Bound Computation

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    long long sum = 0;
    long long mx = 0;

    for (int i = 0; i < n; i++) {
        long long x;
        cin >> x;
        sum += x;
        mx = max(mx, x);
    }

    cout << "LOW = " << mx << "\n";
    cout << "HIGH = " << sum << "\n";
    return 0;
}
```

## Dry Run

```text
weights = [3,2,7,4]

start:
    sum = 0, mx = 0

x = 3:
    sum = 3
    mx = 3

x = 2:
    sum = 5
    mx = 3

x = 7:
    sum = 12
    mx = 7

x = 4:
    sum = 16
    mx = 7

LOW = 7
HIGH = 16
```

## CP Forms

```text
A/B: find min/max possible.
C: binary search on answer.
D: binary search + greedy feasibility.
FAANG: Split Array Largest Sum, Ship Packages Within D Days.
```

---

# 006. Absolute Value

## Why This Exists

Absolute value removes direction and keeps only distance.

## Core Mental Model

```text
|a-b| = how far apart a and b are
```

## ASCII

```text
2 -------- 8

distance = 6

8 - 2 = 6
2 - 8 = -6
|2 - 8| = 6
```

## Important Contest Insight: Median

To minimize total walking distance on a line, meet at the median.

```text
points: 1 2 10 12 13

1---2--------10--12--13
             ^
          median
```

Why median?

If meeting point moves left or right from the middle, more people are hurt than helped.

## Dry Run

```text
a = [1,2,10,12,13]
median = 10

cost:
|1-10|  = 9
|2-10|  = 8
|10-10| = 0
|12-10| = 2
|13-10| = 3

total = 22
```

## Variable Table

```text
+----+-----------+
| x  | |x-10|    |
+----+-----------+
| 1  | 9         |
| 2  | 8         |
| 10 | 0         |
| 12 | 2         |
| 13 | 3         |
+----+-----------+
```

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<long long> a(n);
    for (long long &x : a) cin >> x;

    sort(a.begin(), a.end());

    long long median = a[n / 2];
    long long cost = 0;

    for (long long x : a) {
        cost += llabs(x - median);
    }

    cout << cost << "\n";
    return 0;
}
```

## Common Bugs

```text
Forgetting to sort before taking median.
Using int for cost.
Using mean instead of median for absolute distance.
```

## CP Forms

```text
A/B: absolute difference.
B/C: minimize total distance -> median.
C/D: transform |a_i - x| with sorting/prefix.
FAANG: minimum moves to equal array elements.
```

---

# 007. Powers and Logarithms

## Why This Exists

Powers grow very fast.
Logs count how many times we can divide.

## Core Facts

```text
2^10 ≈ 1,000
2^20 ≈ 1,000,000
2^30 ≈ 1,000,000,000
2^60 ≈ 1,000,000,000,000,000,000
```

So for `N = 1e18`, halving takes about 60 steps.

## ASCII: Halving

```text
64 -> 32 -> 16 -> 8 -> 4 -> 2 -> 1
 ^      ^     ^    ^    ^    ^    ^
step0 step1 step2 step3 step4 step5 step6
```

## Child Example

You have 64 chocolates.
Every round you give away half.

```text
64
32
16
8
4
2
1
```

Only 6 divisions.

This is why `O(logN)` is very powerful.

## Power Of Two Trick

A power of two has only one `1` bit.

```text
8  = 1000
7  = 0111
8 & 7 = 0000
```

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long x;
    cin >> x;

    bool isPowerOfTwo = (x > 0 && (x & (x - 1)) == 0);

    cout << (isPowerOfTwo ? "YES\n" : "NO\n");
    return 0;
}
```

## Dry Run

```text
x = 10

10 in binary = 1010
9  in binary = 1001

1010 & 1001 = 1000

not zero -> not power of two
```

## CP Forms

```text
A/B: power of two check.
B/C: repeated divide by two.
C: binary representation.
C/D: powers inside combinatorics/DP.
```

---

# 008. Floor, Ceil, Integer Division

## Why This Exists

Integer division appears everywhere in CP.

It answers:

```text
How many full groups?
How many groups are needed?
How many multiples exist?
```

## Core Mental Model

```text
floor(a/b) = full boxes
ceil(a/b)  = boxes needed
```

## ASCII

```text
10 candies, 3 candies per box

[***] [***] [***] [*]
  1     2     3    partial

floor(10/3) = 3 full boxes
ceil(10/3)  = 4 boxes needed
```

## Positive Integer Formulas

```text
floor(a/b) = a / b
ceil(a/b)  = (a + b - 1) / b
```

## Dry Run

```text
a = 10, b = 3

a + b - 1 = 10 + 3 - 1 = 12
12 / 3 = 4

ceil = 4
```

## Count Multiples In [L, R]

How many numbers divisible by `k` from `L` to `R`?

```text
multiples up to R      = R/k
multiples before L     = (L-1)/k
answer                 = R/k - (L-1)/k
```

## ASCII

```text
L=5, R=20, k=4

numbers:
1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20
      *       *          *           *              *
multiples in [5,20] = 8,12,16,20 = 4
```

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

long long ceilDivPositive(long long a, long long b) {
    return (a + b - 1) / b;
}

long long countMultiples(long long L, long long R, long long k) {
    return R / k - (L - 1) / k;
}

int main() {
    long long L, R, k;
    cin >> L >> R >> k;

    cout << countMultiples(L, R, k) << "\n";
    return 0;
}
```

## Common Bugs

```text
ceil(a / b) is wrong because a/b already lost decimal.
Negative division in C++ truncates toward zero.
a+b-1 can overflow near 1e18.
```

## CP Forms

```text
A/B: ceil groups.
B/C: count multiples in range.
C/D: group by quotient, floor blocks.
FAANG: minimum pages/batches/capacity calculations.
```

---

# 009. Parity

## Why This Exists

Parity reduces infinite numbers into only two buckets:

```text
even
odd
```

## Core Mental Model

```text
even = divisible by 2
odd  = not divisible by 2
```

## ASCII Buckets

```text
0 2 4 6 8 10  -> EVEN bucket
1 3 5 7 9 11  -> ODD bucket
```

## Rules

```text
even + even = even
even + odd  = odd
odd  + odd  = even

even * anything = even
odd  * odd      = odd
```

## Checkerboard

```text
(row + col) % 2

E O E O
O E O E
E O E O
O E O E
```

## Child Example

Operation: add 2.

```text
5 -> 7 -> 9 -> 11
```

It stays odd forever.

So:

```text
Can 5 become 10 by adding 2?
No.
```

## Dry Run

```text
a = 5, target = 11

difference = 11 - 5 = 6
6 is even
possible by +2 three times
```

```text
a = 5, target = 10

difference = 5
5 is odd
impossible using only +2
```

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long a, b;
    cin >> a >> b;

    if (llabs(a - b) % 2 == 0) {
        cout << "POSSIBLE\n";
    } else {
        cout << "IMPOSSIBLE\n";
    }

    return 0;
}
```

## CP Forms

```text
A/B: odd/even condition.
B/C: operation changes by 2.
C: checkerboard coloring invariant.
D: game state parity.
```

---

# 010. Invariants

## Why This Exists

An invariant is something that never changes.

Many "is it possible?" problems are solved by finding what cannot change.

## Core Mental Model

```text
Start state --operation--> next state --operation--> final state
     |                         |                         |
 invariant                  invariant                  invariant
 same value                 same value                 same value
```

## Common Invariants

```text
parity
sum
gcd
xor
remainder modulo k
checkerboard color
relative order
```

## Child Example

You have a white square.
Every move jumps diagonally on a chessboard.

```text
W B W B
B W B W
W B W B
B W B W
```

A diagonal move keeps the same color.

So if target is black, impossible.

## Step-by-Step

```text
Operation:
    x -> x + 4 or x - 4

Invariant:
    x % 4

Start:
    x = 6
    6 % 4 = 2

Reach:
    y = 14
    14 % 4 = 2

possible maybe

Reach:
    y = 15
    15 % 4 = 3

impossible
```

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long start, target, step;
    cin >> start >> target >> step;

    long long diff = llabs(target - start);

    if (diff % step == 0) {
        cout << "POSSIBLE\n";
    } else {
        cout << "IMPOSSIBLE\n";
    }

    return 0;
}
```

## CP Forms

```text
A/B: possible by fixed step.
B/C: sum/parity invariant.
C/D: grid coloring invariant.
D/E: gcd invariant after operations.
```

---

# 011. Divisibility, GCD, LCM

## Why This Exists

GCD and LCM are about common rhythm.

## Core Mental Model

```text
GCD = largest common block size
LCM = first common meeting time
```

## Child Example: GCD

You have 24 candies and 36 candies.
You want to pack both into equal-size bags without leftovers.

```text
24 candies: [............][............]
36 candies: [............][............][............]

bag size 12 works
```

GCD is the largest bag size that works.

## ASCII: GCD Blocks

```text
24:
[************][************]

36:
[************][************][************]

largest common block = 12
gcd(24,36)=12
```

## Euclid Algorithm

```text
gcd(a,b) = gcd(b, a % b)
```

Why?

If a block size divides both `a` and `b`, it also divides the remainder after removing groups of `b` from `a`.

## Dry Run

```text
gcd(48,18)

48 % 18 = 12
gcd(48,18) = gcd(18,12)

18 % 12 = 6
gcd(18,12) = gcd(12,6)

12 % 6 = 0
answer = 6
```

## Variable Table

```text
+----+----+-------+
| a  | b  | a % b |
+----+----+-------+
| 48 | 18 | 12    |
| 18 | 12 | 6     |
| 12 | 6  | 0     |
+----+----+-------+
```

## LCM Child Example

Light A blinks every 4 seconds.
Light B blinks every 6 seconds.

```text
A: 4, 8, 12, 16, 20, 24
B: 6, 12, 18, 24

first together = 12
lcm(4,6)=12
```

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

long long gcdll(long long a, long long b) {
    a = llabs(a);
    b = llabs(b);

    while (b != 0) {
        long long r = a % b;
        a = b;
        b = r;
    }

    return a;
}

long long lcmll(long long a, long long b) {
    if (a == 0 || b == 0) return 0;
    return a / gcdll(a, b) * b;
}

int main() {
    long long a, b;
    cin >> a >> b;

    cout << gcdll(a, b) << "\n";
    cout << lcmll(a, b) << "\n";
    return 0;
}
```

## Common Bugs

```text
a*b/gcd may overflow.
Use a/gcd*b.

gcd(0,x)=x.
lcm(0,x)=0.

Use long long.
```

## CP Forms

```text
A/B: compute gcd/lcm.
B/C: array gcd after operations.
C: gcd invariant.
D: lcm with overflow/constraints.
FAANG: greatest common divisor of strings.
```

---

# 012. Modulo Arithmetic

## Why This Exists

Modulo means remainder after division.

It is also clock arithmetic.

## Core Mental Model

```text
MOD = 7

0 -> 1 -> 2 -> 3 -> 4 -> 5 -> 6
^                              |
|______________________________|
```

After 6 comes 0 again.

## Child Example: Clock

Clock has 12 hours.
If it is 10 o'clock and 5 hours pass:

```text
10 -> 11 -> 12 -> 1 -> 2 -> 3
```

Math:

```text
(10 + 5) % 12 = 15 % 12 = 3
```

## Rules

```text
(a+b)%m = ((a%m)+(b%m))%m
(a-b)%m = ((a%m)-(b%m)+m)%m
(a*b)%m = ((a%m)*(b%m))%m
```

Division is special.
You cannot directly do normal division under modulo.

## ASCII: Remainder Buckets

```text
MOD = 5

remainder 0: 0, 5, 10, 15, ...
remainder 1: 1, 6, 11, 16, ...
remainder 2: 2, 7, 12, 17, ...
remainder 3: 3, 8, 13, 18, ...
remainder 4: 4, 9, 14, 19, ...
```

## Dry Run

```text
MOD = 7
a = 5
b = 6

add:
    5 + 6 = 11
    11 % 7 = 4

subtract:
    5 - 6 = -1
    in C++ -1 % 7 = -1
    normalize: (-1 + 7) % 7 = 6

multiply:
    5 * 6 = 30
    30 % 7 = 2
```

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;
const ll MOD = 1'000'000'007;

ll norm(ll x) {
    x %= MOD;
    if (x < 0) x += MOD;
    return x;
}

ll addMod(ll a, ll b) {
    return (norm(a) + norm(b)) % MOD;
}

ll subMod(ll a, ll b) {
    return (norm(a) - norm(b) + MOD) % MOD;
}

ll mulMod(ll a, ll b) {
    return (__int128)norm(a) * norm(b) % MOD;
}

int main() {
    ll a, b;
    cin >> a >> b;

    cout << addMod(a,b) << "\n";
    cout << subMod(a,b) << "\n";
    cout << mulMod(a,b) << "\n";
    return 0;
}
```

## Common Bugs

```text
Negative modulo in C++ remains negative.
Overflow before modulo.
Doing division without modular inverse.
Forgetting to apply MOD after every multiplication.
```

## CP Forms

```text
A/B: answer modulo M.
B/C: prefix sum modulo K.
C: count same remainder pairs.
D: modular inverse/combinatorics.
FAANG: circular array, subarray sum divisible by K.
```

---

# 013. Modulo Cycles

## Why This Exists

When states are finite and repeated, eventually some state repeats.

## Core Mental Model

```text
finite states + repeated operation = cycle
```

## Child Example: Last Digit of Powers of 2

```text
2^1 = 2
2^2 = 4
2^3 = 8
2^4 = 16 -> last digit 6
2^5 = 32 -> last digit 2
```

Cycle:

```text
2 -> 4 -> 8 -> 6
^              |
|______________|
```

So last digit repeats every 4 powers.

## Dry Run

Find last digit of `2^13`.

```text
cycle = [2,4,8,6]
cycle length = 4

13 % 4 = 1

answer = cycle[1st position] = 2
```

Careful with 0-index:

```text
index = (13 - 1) % 4 = 0
cycle[0] = 2
```

## C++17 Generic Cycle Storage

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long base, mod;
    cin >> base >> mod;

    vector<int> seen(mod, -1);
    vector<long long> values;

    long long cur = 1 % mod;
    int step = 0;

    while (seen[cur] == -1) {
        seen[cur] = step;
        values.push_back(cur);

        cur = (cur * base) % mod;
        step++;
    }

    int cycleStart = seen[cur];
    int cycleLength = step - cycleStart;

    cout << "cycleStart = " << cycleStart << "\n";
    cout << "cycleLength = " << cycleLength << "\n";

    return 0;
}
```

## CP Forms

```text
A/B: last digit.
B/C: repeated operation with small states.
C/D: functional graph cycle.
D: matrix/DP cycle optimization.
```

---

# 014. Binary Exponentiation

## Why This Exists

Computing `a^b` by multiplying `b` times is impossible when `b = 1e18`.

## Core Mental Model

Break exponent into powers of two.

```text
13 = 8 + 4 + 1

a^13 = a^8 * a^4 * a^1
```

## ASCII

```text
b = 13 = 1101₂

bit 0 = 1 -> take a^1
bit 1 = 0 -> skip a^2
bit 2 = 1 -> take a^4
bit 3 = 1 -> take a^8
```

## Child Example

You want 2^13.

Instead of 13 multiplications:

```text
2^1  = 2
2^2  = 4
2^4  = 16
2^8  = 256

2^13 = 2^8 * 2^4 * 2^1
```

## Step-by-Step Dry Run: 2^13 mod 100

Start:

```text
res = 1
a   = 2
b   = 13
```

Table:

```text
+------+-----+------------+------------+-------------+
| b    | bit | res before | a before   | action      |
+------+-----+------------+------------+-------------+
| 13   | 1   | 1          | 2          | res=1*2=2  |
| 6    | 0   | 2          | 4          | skip        |
| 3    | 1   | 2          | 16         | res=32     |
| 1    | 1   | 32         | 56         | res=92     |
+------+-----+------------+------------+-------------+
```

Why `a` becomes 56?

```text
16 * 16 = 256
256 % 100 = 56
```

Answer:

```text
2^13 = 8192
8192 % 100 = 92
```

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll binpow(ll a, long long b, ll mod) {
    a %= mod;
    ll res = 1 % mod;

    while (b > 0) {
        if (b & 1) {
            res = (__int128)res * a % mod;
        }

        a = (__int128)a * a % mod;
        b >>= 1;
    }

    return res;
}

int main() {
    long long a, b, mod;
    cin >> a >> b >> mod;

    cout << binpow(a, b, mod) << "\n";
    return 0;
}
```

## Common Bugs

```text
Forgetting b >>= 1.
Overflow in a*a.
Not handling mod=1.
Using int for exponent.
```

## CP Forms

```text
A/B: fast power.
B/C: power modulo.
C: modular inverse.
D: matrix exponentiation foundation.
FAANG: Pow(x,n).
```

---

# 015. Modular Inverse

## Why This Exists

Modulo division is not normal division.

In normal math:

```text
10 / 2 = 5
```

Under modulo, division means:

```text
a / b mod M = a * inverse(b) mod M
```

## Core Mental Model

Inverse means:

```text
b * inv(b) ≡ 1 mod M
```

So inverse is the number that makes `b` behave like `1`.

## Child Example: MOD 7

Find inverse of 3.

Try:

```text
3*1 = 3 mod 7
3*2 = 6 mod 7
3*3 = 9 mod 7 = 2
3*4 = 12 mod 7 = 5
3*5 = 15 mod 7 = 1
```

So:

```text
inv(3) = 5 mod 7
```

Because:

```text
3 * 5 % 7 = 1
```

## Fermat Rule

If `MOD` is prime and `gcd(b,MOD)=1`:

```text
inv(b) = b^(MOD-2) mod MOD
```

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll binpow(ll a, ll b, ll mod) {
    a %= mod;
    ll res = 1 % mod;

    while (b > 0) {
        if (b & 1) res = (__int128)res * a % mod;
        a = (__int128)a * a % mod;
        b >>= 1;
    }

    return res;
}

ll modInversePrime(ll x, ll mod) {
    return binpow(x, mod - 2, mod);
}

int main() {
    ll a, b, mod;
    cin >> a >> b >> mod;

    ll invB = modInversePrime(b, mod);
    cout << (__int128)(a % mod) * invB % mod << "\n";

    return 0;
}
```

## Common Bugs

```text
Fermat inverse needs prime MOD.
b must not be divisible by MOD.
For non-prime MOD, use extended Euclid and gcd(b,MOD)=1.
```

## CP Forms

```text
C: nCr modulo prime.
C/D: probability under modulo.
D: rational formula modulo.
FAANG: less common, but useful for modular arithmetic questions.
```

---

# 016. Prime Checking

## Why This Exists

A prime has exactly two divisors:

```text
1 and itself
```

## Core Mental Model

If `n = a*b`, then at least one factor is `<= sqrt(n)`.

Why?

```text
If both a and b were bigger than sqrt(n),
then a*b would be bigger than n.
```

## ASCII

```text
n = 36

factor pairs:
1 * 36
2 * 18
3 * 12
4 * 9
6 * 6
9 * 4
12 * 3
18 * 2
36 * 1

middle is sqrt(36)=6
```

So checking after sqrt is repeated information.

## Dry Run

```text
n = 29

try d = 2:
    29 % 2 != 0

try d = 3:
    29 % 3 != 0

try d = 4:
    29 % 4 != 0

try d = 5:
    29 % 5 != 0

next d = 6:
    6*6 = 36 > 29
stop

prime
```

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

bool isPrime(long long n) {
    if (n < 2) return false;

    for (long long d = 2; d * d <= n; d++) {
        if (n % d == 0) return false;
    }

    return true;
}

int main() {
    long long n;
    cin >> n;

    cout << (isPrime(n) ? "YES\n" : "NO\n");
    return 0;
}
```

## Common Bugs

```text
1 is not prime.
0 is not prime.
Negative numbers are not prime in CP.
d*d can overflow for very large n.
```

## CP Forms

```text
A/B: check prime.
B/C: count prime-like numbers.
C: factorization by sqrt.
D: need sieve/MillerRabin for many/large queries.
```

---

# 017. Sieve and SPF

## Why This Exists

If you need prime answers for many numbers, checking each one by sqrt is slow.

## Core Mental Model

A composite number gets crossed out by one of its prime factors.

## Child Example

Numbers up to 20:

```text
2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20
```

2 marks:

```text
4 6 8 10 12 14 16 18 20
```

3 marks:

```text
6 9 12 15 18
```

5 marks:

```text
10 15 20
```

Unmarked numbers are prime.

## ASCII

```text
prime p marks multiples:

p
|
v
p*p, p*(p+1), p*(p+2), ...
```

Why start at `p*p`?

Because smaller multiples were already marked by smaller primes.

## C++17 Sieve

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<bool> prime(n + 1, true);

    if (n >= 0) prime[0] = false;
    if (n >= 1) prime[1] = false;

    for (long long p = 2; p * p <= n; p++) {
        if (prime[p]) {
            for (long long x = p * p; x <= n; x += p) {
                prime[x] = false;
            }
        }
    }

    for (int i = 2; i <= n; i++) {
        if (prime[i]) cout << i << " ";
    }

    return 0;
}
```

## SPF Mental Model

SPF = smallest prime factor.

```text
spf[12] = 2
spf[15] = 3
spf[49] = 7
```

Useful for fast factorization.

## C++17 SPF

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<int> spf(n + 1);

    for (int i = 0; i <= n; i++) spf[i] = i;

    for (long long i = 2; i * i <= n; i++) {
        if (spf[i] == i) {
            for (long long j = i * i; j <= n; j += i) {
                if (spf[j] == j) spf[j] = i;
            }
        }
    }

    int x;
    cin >> x;

    while (x > 1) {
        cout << spf[x] << " ";
        x /= spf[x];
    }

    return 0;
}
```

## Dry Run SPF: x=60

```text
60 -> spf[60]=2, print 2, x=30
30 -> spf[30]=2, print 2, x=15
15 -> spf[15]=3, print 3, x=5
5  -> spf[5]=5, print 5, x=1

factorization: 2 2 3 5
```

## Common Bugs

```text
Forgetting prime[0], prime[1] false.
Starting marking from 2*p instead of p*p is correct but slower.
SPF array only works up to precomputed n.
```

---

# 018. Prime Factorization

## Why This Exists

Factorization breaks a number into prime building blocks.

## Core Mental Model

```text
n = p1^e1 * p2^e2 * ... * pk^ek
```

Example:

```text
60 = 2^2 * 3^1 * 5^1
```

## ASCII Tree

```text
        60
       /  \
      2    30
          /  \
         2    15
             /  \
            3    5
```

## Step-by-Step Dry Run

Factorize 84.

```text
n = 84

p = 2:
    84 % 2 == 0 -> n=42, count=1
    42 % 2 == 0 -> n=21, count=2
    21 % 2 != 0
    record 2^2

p = 3:
    21 % 3 == 0 -> n=7, count=1
    7 % 3 != 0
    record 3^1

loop stops when p*p > n
remaining n = 7 > 1
record 7^1

answer = 2^2 * 3^1 * 7^1
```

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<pair<long long,int>> factorize(long long n) {
    vector<pair<long long,int>> factors;

    for (long long p = 2; p * p <= n; p++) {
        if (n % p == 0) {
            int exponent = 0;

            while (n % p == 0) {
                n /= p;
                exponent++;
            }

            factors.push_back({p, exponent});
        }
    }

    if (n > 1) {
        factors.push_back({n, 1});
    }

    return factors;
}

int main() {
    long long n;
    cin >> n;

    for (auto [p, e] : factorize(n)) {
        cout << p << "^" << e << "\n";
    }

    return 0;
}
```

## Common Bugs

```text
Forgetting remaining n > 1.
Trial division too slow for many 1e12 queries.
p*p overflow for very large n.
```

## CP Forms

```text
B/C: factor one number.
C/D: factor many numbers with SPF.
D: divisor count/sum from exponents.
```

---

# 019. Divisor Count and Sum

## Why This Exists

Once you know prime powers, divisors become choices.

## Core Mental Model

If:

```text
n = 2^2 * 3^1 * 5^1
```

A divisor chooses exponent:

```text
2 exponent: 0,1,2  -> 3 choices
3 exponent: 0,1    -> 2 choices
5 exponent: 0,1    -> 2 choices
```

Total divisors:

```text
3 * 2 * 2 = 12
```

## ASCII Choice Tree

```text
choose power of 2: 2^0, 2^1, 2^2
        |
choose power of 3: 3^0, 3^1
        |
choose power of 5: 5^0, 5^1
```

Every path creates one divisor.

## Dry Run: 60

```text
60 = 2^2 * 3^1 * 5^1

count = (2+1)(1+1)(1+1)
      = 3*2*2
      = 12
```

Divisors:

```text
1,2,3,4,5,6,10,12,15,20,30,60
```

## Sum Formula

```text
sum divisors = Π(1 + p + p^2 + ... + p^e)
```

For 12:

```text
12 = 2^2 * 3^1

sum = (1+2+4) * (1+3)
    = 7 * 4
    = 28
```

Divisors:

```text
1,2,3,4,6,12 -> sum 28
```

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;
using ll = long long;

vector<pair<ll,int>> factorize(ll n) {
    vector<pair<ll,int>> f;

    for (ll p = 2; p * p <= n; p++) {
        if (n % p == 0) {
            int e = 0;
            while (n % p == 0) {
                n /= p;
                e++;
            }
            f.push_back({p, e});
        }
    }

    if (n > 1) f.push_back({n, 1});
    return f;
}

int main() {
    ll n;
    cin >> n;

    ll divisorCount = 1;
    ll divisorSum = 1;

    for (auto [p, e] : factorize(n)) {
        divisorCount *= (e + 1);

        ll term = 1;
        ll power = 1;

        for (int i = 1; i <= e; i++) {
            power *= p;
            term += power;
        }

        divisorSum *= term;
    }

    cout << divisorCount << "\n";
    cout << divisorSum << "\n";
    return 0;
}
```

## CP Forms

```text
B/C: number of divisors.
C/D: product/sum of divisors modulo.
D: divisor-based DP or counting.
```

---

# 020. Euler Phi

## Why This Exists

Phi counts numbers coprime to `n`.

## Core Mental Model

```text
phi(n) = count of x in [1,n] where gcd(x,n)=1
```

## Child Example

For `n=10`:

```text
1 2 3 4 5 6 7 8 9 10
```

Coprime with 10:

```text
1, 3, 7, 9
```

So:

```text
phi(10)=4
```

## Formula

If prime factors of n are distinct primes `p`:

```text
phi(n) = n * Π(1 - 1/p)
```

Implementation form:

```text
ans = n
for each distinct prime p:
    ans -= ans / p
```

## Dry Run

```text
n = 12
prime factors = 2,3

ans = 12

remove multiples of 2:
    ans = 12 - 6 = 6

remove multiples of 3:
    ans = 6 - 2 = 4

phi(12) = 4

coprime numbers: 1,5,7,11
```

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

long long phi(long long n) {
    long long ans = n;

    for (long long p = 2; p * p <= n; p++) {
        if (n % p == 0) {
            while (n % p == 0) {
                n /= p;
            }

            ans -= ans / p;
        }
    }

    if (n > 1) {
        ans -= ans / n;
    }

    return ans;
}

int main() {
    long long n;
    cin >> n;

    cout << phi(n) << "\n";
    return 0;
}
```

## CP Forms

```text
C/D: count coprime numbers.
D: modular exponent with non-prime modulus.
CM: number theory with phi, divisors, gcd.
```

---

# 021. Counting Principles

## Why This Exists

Counting is the foundation of combinatorics.

## Core Mental Model

```text
OR  -> add
AND -> multiply
```

## Child Example

You can choose:

```text
3 shirts: A B C
2 pants:  X Y
```

Outfits:

```text
AX AY
BX BY
CX CY
```

Total:

```text
3 * 2 = 6
```

## ASCII

```text
        Pants
        X   Y
Shirt A AX  AY
Shirt B BX  BY
Shirt C CX  CY
```

## OR Example

You can eat:

```text
3 fruits OR 2 snacks
```

Total choices:

```text
3 + 2 = 5
```

## Dry Run

```text
Password:
    first char: 26 lowercase letters
    second char: 10 digits
    third char: 10 digits

total = 26 * 10 * 10 = 2600
```

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long choices1, choices2, choices3;
    cin >> choices1 >> choices2 >> choices3;

    cout << choices1 * choices2 * choices3 << "\n";
    return 0;
}
```

## CP Forms

```text
A/B: multiply independent choices.
B/C: split into cases and add.
C/D: avoid overcount using inclusion-exclusion.
```

---

# 022. nCr Combinations

## Why This Exists

`nCr` counts ways to choose items when order does not matter.

## Core Mental Model

```text
choose r from n
```

## Child Example

You have 4 friends:

```text
A B C D
```

Choose 2 friends:

```text
AB AC AD BC BD CD
```

So:

```text
4C2 = 6
```

## Order Does Not Matter

```text
AB and BA are the same team.
```

That is why combinations are not permutations.

## Formula

```text
C(n,r) = n! / (r! * (n-r)!)
```

## Safe Multiplicative Version

Avoid full factorial when `n` is small enough for long long answer:

```text
C(n,r) = product of r terms / product 1..r
```

## Dry Run: C(5,2)

```text
C(5,2)

ans = 1

i=1:
    ans = ans * 4 / 1 = 4

i=2:
    ans = ans * 5 / 2 = 10

answer = 10
```

## C++17 Without Mod

```cpp
#include <bits/stdc++.h>
using namespace std;

long long C(int n, int r) {
    if (r < 0 || r > n) return 0;

    r = min(r, n - r);

    long long ans = 1;

    for (int i = 1; i <= r; i++) {
        ans = ans * (n - r + i) / i;
    }

    return ans;
}

int main() {
    int n, r;
    cin >> n >> r;

    cout << C(n, r) << "\n";
    return 0;
}
```

## Recognition Checklist

```text
[ ] choose positions?
[ ] select team?
[ ] order does not matter?
[ ] grid path with R and D moves?
[ ] choose indices?
```

## CP Forms

```text
A/B: small nCr.
B/C: grid path count.
C/D: nCr modulo prime.
D/E: combinatorics + DP.
FAANG: combinations, subsets, paths.
```

---

# 023. nCr Modulo Prime

## Why This Exists

Combinatorics answers become huge.
CP usually asks:

```text
answer modulo 1e9+7
```

## Core Mental Model

Precompute factorials.

```text
C(n,r) = fact[n] / (fact[r] * fact[n-r])
```

Modulo division becomes inverse:

```text
C(n,r) = fact[n] * invFact[r] * invFact[n-r] mod MOD
```

## ASCII

```text
Build once:

fact[0] fact[1] fact[2] ... fact[MAXN]
   |
   v
answer each query in O(1)
```

## Dry Run: C(5,2)

```text
fact[5] = 120
fact[2] = 2
fact[3] = 6

C(5,2) = 120 / (2*6)
       = 120 / 12
       = 10
```

Modulo version:

```text
120 * inv(2) * inv(6) mod MOD
```

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll MOD = 1'000'000'007;
const int MAXN = 1'000'000;

vector<ll> fact(MAXN + 1);
vector<ll> invFact(MAXN + 1);

ll binpow(ll a, ll b) {
    a %= MOD;
    ll res = 1;

    while (b > 0) {
        if (b & 1) res = res * a % MOD;
        a = a * a % MOD;
        b >>= 1;
    }

    return res;
}

void build() {
    fact[0] = 1;

    for (int i = 1; i <= MAXN; i++) {
        fact[i] = fact[i - 1] * i % MOD;
    }

    invFact[MAXN] = binpow(fact[MAXN], MOD - 2);

    for (int i = MAXN - 1; i >= 0; i--) {
        invFact[i] = invFact[i + 1] * (i + 1) % MOD;
    }
}

ll C(int n, int r) {
    if (r < 0 || r > n) return 0;

    return fact[n] * invFact[r] % MOD * invFact[n - r] % MOD;
}

int main() {
    build();

    int n, r;
    cin >> n >> r;

    cout << C(n, r) << "\n";
    return 0;
}
```

## Common Bugs

```text
MAXN too small.
MOD must be prime for Fermat inverse.
Forgetting r<0 or r>n case.
Building factorial for every query instead of once.
```

## CP Forms

```text
C: choose positions modulo.
C/D: paths/counting modulo.
D/E: combinatorics + DP.
CM: Lucas theorem if n is huge and MOD small.
```

---

# 024. Permutations

## Why This Exists

Permutations count arrangements where order matters.

## Core Mental Model

```text
order matters
```

## Child Example

Letters:

```text
A B C
```

Arrange all:

```text
ABC
ACB
BAC
BCA
CAB
CBA
```

There are:

```text
3! = 6
```

## Permutation Without Repetition

Choose and arrange `k` items from `n`:

```text
nPk = n * (n-1) * ... * (n-k+1)
```

## ASCII Slots

```text
3 slots from 5 people:

slot1: 5 choices
slot2: 4 choices
slot3: 3 choices

total = 5*4*3
```

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

long long permNoRepeat(int n, int k) {
    long long ans = 1;

    for (int i = 0; i < k; i++) {
        ans *= (n - i);
    }

    return ans;
}

int main() {
    int n, k;
    cin >> n >> k;

    cout << permNoRepeat(n, k) << "\n";
    return 0;
}
```

## Combination vs Permutation

```text
Team of 2:
    AB == BA
    use combination

Podium 1st/2nd:
    AB != BA
    use permutation
```

## CP Forms

```text
A/B: arrange objects.
B/C: count passwords/codes.
C/D: permutation with constraints.
FAANG: permutations/backtracking.
```

---

# 025. Stars and Bars

## Why This Exists

Stars and bars counts ways to distribute identical objects into boxes.

## Core Mental Model

```text
x1 + x2 + ... + xk = n
xi >= 0
answer = C(n+k-1, k-1)
```

## Child Example

Distribute 5 identical candies to 3 children.

One distribution:

```text
Child1 gets 2
Child2 gets 1
Child3 gets 2
```

Represent:

```text
** | * | **
```

Stars = candies.
Bars = dividers between children.

## ASCII

```text
5 candies, 3 children

positions:
_ _ _ _ _ _ _

place 5 stars and 2 bars:
* * | * | * *

total positions = 5 + 2 = 7
choose 2 bar positions = C(7,2)
```

## Positive Version

If each child must get at least 1 candy:

```text
x1 + x2 + ... + xk = n
xi >= 1
```

Give 1 candy to everyone first.

```text
remaining = n - k
answer = C((n-k)+k-1, k-1)
       = C(n-1, k-1)
```

## Dry Run

```text
n = 5 candies
k = 3 children
xi >= 0

answer = C(5+3-1, 3-1)
       = C(7,2)
       = 21
```

## Recognition

```text
identical items
boxes/people/groups
non-negative integer solutions
sum fixed
```

## CP Forms

```text
B/C: distribute candies.
C/D: stars and bars with lower bounds.
D: combine with inclusion-exclusion upper bounds.
```

---

# 026. Inclusion Exclusion

## Why This Exists

When counting "A or B", overlap gets counted twice.

## Core Mental Model

```text
count(A or B) = count(A) + count(B) - count(A and B)
```

## Child Example

Students who like cricket or football.

```text
cricket fans = 10
football fans = 8
both = 3
```

If we add:

```text
10 + 8 = 18
```

But the 3 students who like both were counted twice.

So:

```text
answer = 10 + 8 - 3 = 15
```

## ASCII Venn

```text
      Cricket
   ___________
  /           \
 /   A     BOTH\
 \             / Football
  \___________/
```

## Multiples of A or B

Count numbers <= n divisible by `a` or `b`.

```text
n/a       -> multiples of a
n/b       -> multiples of b
n/lcm(a,b)-> multiples of both
```

Formula:

```text
n/a + n/b - n/lcm(a,b)
```

## Dry Run

```text
n = 10
a = 2
b = 3

multiples of 2:
2,4,6,8,10 -> 5

multiples of 3:
3,6,9 -> 3

overlap:
6 -> 1

answer = 5 + 3 - 1 = 7
```

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countMultiples(long long n, long long a, long long b) {
    long long g = gcd(a, b);
    long long l = a / g * b;

    return n / a + n / b - n / l;
}

int main() {
    long long n, a, b;
    cin >> n >> a >> b;

    cout << countMultiples(n, a, b) << "\n";
    return 0;
}
```

## Common Bugs

```text
Overflow in lcm.
For 3+ sets, signs alternate:
+ singles
- pairs
+ triples
- quadruples
```

## CP Forms

```text
B/C: multiples of A or B.
C/D: count with forbidden properties.
D/E: bitmask inclusion-exclusion.
FAANG: overlapping intervals/sets.
```

---

# 027. Pigeonhole Principle

## Why This Exists

Pigeonhole proves that something must exist.

## Core Mental Model

```text
More pigeons than holes -> at least one hole has 2 pigeons
```

## Child Example

4 children, 3 chairs.

```text
children: C C C C
chairs:   [ ] [ ] [ ]
```

At least one chair must have two children or someone cannot sit.

## CP Version: Remainders

There are only `n` possible remainders modulo `n`:

```text
0,1,2,...,n-1
```

If you have `n+1` prefix sums, two must have same remainder.

If:

```text
prefix[j] % n == prefix[i] % n
```

then:

```text
(prefix[j] - prefix[i]) % n == 0
```

So subarray sum divisible by n exists.

## ASCII

```text
prefix sums -> boxes by remainder

rem 0: [prefix2]
rem 1: [prefix0, prefix5]  <- collision
rem 2: []
rem 3: [prefix1]
```

Collision gives divisible difference.

## Dry Run

```text
array = [2, 1, 4]
n = 3

prefix sums:
0, 2, 3, 7

remainders mod 3:
0, 2, 0, 1

prefix0 and prefix2 both remainder 0

subarray sum from index1 to index2:
2 + 1 = 3
divisible by 3
```

## Recognition

```text
guaranteed duplicate
same remainder
large objects into small states
collision
at least one pair
```

## CP Forms

```text
B/C: prefix remainder collision.
C/D: prove existence.
D: advanced modulo/state compression.
```

---

# 028. Catalan Numbers

## Why This Exists

Catalan counts balanced/nested structures.

## Core Mental Model

```text
open choices must never be fewer than close choices
```

## Child Example: Valid Parentheses

For n=3 pairs:

```text
((()))
(()())
(())()
()(())
()()()
```

There are 5 valid strings.

## Formula

```text
Catalan(n) = C(2n,n) / (n+1)
```

## ASCII Path View

`(` means up.
`)` means down.

Valid path never goes below ground.

```text
height
3        /\
2      /   \
1    /      \  /\
0 __/        \/  \__
```

Invalid:

```text
0 __
    \       goes below zero
     \
```

## Recognition

```text
balanced parentheses
number of BST shapes
non-crossing chords
valid mountain paths
never negative prefix
```

## CP Forms

```text
C/D: Catalan formula modulo.
D/E: DP for balanced structures.
FAANG: unique BSTs, generate parentheses.
```

---

# 029. Derangements

## Why This Exists

Derangements count arrangements where nobody gets their own item.

## Core Mental Model

```text
permutation with no fixed point
```

## Child Example

3 children bring hats:

```text
A owns hat A
B owns hat B
C owns hat C
```

Valid derangement:

```text
A gets B
B gets C
C gets A
```

Nobody gets own hat.

## Recurrence

```text
D[0] = 1
D[1] = 0
D[n] = (n-1) * (D[n-1] + D[n-2])
```

## Small Values

```text
D[1] = 0
D[2] = 1
D[3] = 2
D[4] = 9
```

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;
const ll MOD = 1'000'000'007;

int main() {
    int n;
    cin >> n;

    vector<ll> D(n + 2);

    D[0] = 1;
    D[1] = 0;

    for (int i = 2; i <= n; i++) {
        D[i] = (i - 1) * (D[i - 1] + D[i - 2]) % MOD;
    }

    cout << D[n] << "\n";
    return 0;
}
```

## Recognition

```text
secret santa
no fixed point
no one gets own object
permutation with forbidden self-position
```

---

# 030. Probability and Expected Value

## Why This Exists

Probability handles random outcomes.
Expectation gives average outcome.

## Core Mental Model

```text
Probability = favorable outcomes / total outcomes
Expectation = weighted average
```

## Child Example: Dice

A dice has:

```text
1 2 3 4 5 6
```

Probability of getting even:

```text
even outcomes = 2,4,6 -> 3
total outcomes = 6

probability = 3/6 = 1/2
```

Expected dice value:

```text
(1+2+3+4+5+6)/6 = 21/6 = 3.5
```

## Linearity of Expectation

Even if events are dependent:

```text
E[X + Y] = E[X] + E[Y]
```

This is powerful in CP.

## ASCII

```text
Outcome:      1   2   3   4   5   6
Probability: 1/6 1/6 1/6 1/6 1/6 1/6

average = weighted sum
```

## C++17 Basic Probability

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    double favorable, total;
    cin >> favorable >> total;

    cout << fixed << setprecision(10) << favorable / total << "\n";
    return 0;
}
```

## Recognition

```text
random choice
expected steps
average score
probability DP
linearity of expectation
```

## CP Forms

```text
B/C: simple probability.
C/D: expected value using linearity.
D/E: probability DP.
FAANG: random pick with weight.
```

---

# 031. Binary, Bitwise Math, XOR

## Why This Exists

Bits represent numbers as powers of two.
XOR cancels pairs.

## Core Mental Model

```text
binary = powers of two
xor    = pair cancellation
```

## Child Example

```text
x ^ x = 0
x ^ 0 = x
```

So:

```text
5 ^ 3 ^ 5 ^ 7 ^ 3
= (5^5) ^ (3^3) ^ 7
= 0 ^ 0 ^ 7
= 7
```

## ASCII

```text
5 = 101
3 = 011

5 ^ 3:
101
011
---
110 = 6
```

## Single Number Dry Run

```text
array = [4,1,2,1,2]

xr = 0

xr ^= 4 -> 4
xr ^= 1 -> 5
xr ^= 2 -> 7
xr ^= 1 -> 6
xr ^= 2 -> 4

answer = 4
```

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    int xr = 0;

    for (int i = 0; i < n; i++) {
        int x;
        cin >> x;
        xr ^= x;
    }

    cout << xr << "\n";
    return 0;
}
```

## Common Bit Tricks

```text
x & 1              -> odd/even
x & (x-1)          -> remove lowest set bit
x & -x             -> lowest set bit
__builtin_popcount -> count set bits int
```

## CP Forms

```text
A/B: odd/even bit.
B/C: single number/cancellation.
C/D: subset mask.
D/E: xor basis/trie.
FAANG: Single Number, subsets, bitmask DP.
```

---

# 032. Geometry Basics

## Why This Exists

Geometry problems often fail due to floating-point precision.

## Core Mental Model

```text
Prefer integer math:
squared distance
cross product
orientation
```

## Distance

Instead of actual distance:

```text
sqrt(dx^2 + dy^2)
```

use squared distance:

```text
dx^2 + dy^2
```

because sqrt creates decimals.

## ASCII

```text
A(x1,y1) -------- dx -------- B(x2,y2)
       |
       |
       dy
```

## Orientation

Cross product tells turn direction.

```text
cross(B-A, C-A)
```

```text
> 0 -> counter-clockwise
< 0 -> clockwise
= 0 -> collinear
```

## ASCII

```text
Counter-clockwise:

C
 \
  \
   A ---- B

Clockwise:

A ---- B
 \
  \
   C
```

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Point {
    long long x, y;
};

long long dist2(Point a, Point b) {
    long long dx = a.x - b.x;
    long long dy = a.y - b.y;

    return dx * dx + dy * dy;
}

long long cross(Point a, Point b, Point c) {
    long long x1 = b.x - a.x;
    long long y1 = b.y - a.y;

    long long x2 = c.x - a.x;
    long long y2 = c.y - a.y;

    return x1 * y2 - y1 * x2;
}

int main() {
    Point a, b, c;
    cin >> a.x >> a.y >> b.x >> b.y >> c.x >> c.y;

    long long v = cross(a, b, c);

    if (v > 0) cout << "CCW\n";
    else if (v < 0) cout << "CW\n";
    else cout << "COLLINEAR\n";

    return 0;
}
```

## Dry Run

```text
A = (0,0)
B = (4,0)
C = (2,3)

x1 = 4, y1 = 0
x2 = 2, y2 = 3

cross = 4*3 - 0*2 = 12

positive -> CCW
```

## Common Bugs

```text
Comparing double slopes.
Using sqrt when squared distance is enough.
Overflow in cross product for large coordinates.
Use __int128 if coordinates are huge.
```

## CP Forms

```text
B/C: distance/area.
C/D: orientation, segment intersection.
D/E: convex hull.
FAANG: rectangle overlap, points on line.
```

---

# 033. Extended Euclid, Diophantine, CRT

## Why This Exists

Some problems ask for integer solutions to equations.

## Core Mental Model

Extended Euclid finds:

```text
ax + by = gcd(a,b)
```

## Child Example

Can we make 1 using 3 and 7?

```text
3*5 + 7*(-2) = 15 - 14 = 1
```

So 3 and 7 can combine to make 1.

## Extended GCD C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

long long extgcd(long long a, long long b, long long &x, long long &y) {
    if (b == 0) {
        x = 1;
        y = 0;
        return a;
    }

    long long x1, y1;
    long long g = extgcd(b, a % b, x1, y1);

    x = y1;
    y = x1 - (a / b) * y1;

    return g;
}

int main() {
    long long a, b;
    cin >> a >> b;

    long long x, y;
    long long g = extgcd(a, b, x, y);

    cout << "gcd = " << g << "\n";
    cout << "x = " << x << ", y = " << y << "\n";
    cout << a << "*" << x << " + " << b << "*" << y << " = " << g << "\n";

    return 0;
}
```

## Diophantine Equation

```text
ax + by = c
```

Solution exists only if:

```text
gcd(a,b) divides c
```

## Dry Run

```text
6x + 9y = 30

gcd(6,9) = 3
30 % 3 = 0

solution exists
```

```text
6x + 9y = 31

31 % 3 != 0

no integer solution
```

## CRT Mental Model

CRT merges remainder constraints.

```text
x ≡ 2 mod 3
x ≡ 3 mod 5
```

Try numbers that are 2 mod 3:

```text
2, 5, 8, 11, 14, 17, ...
```

Which is 3 mod 5?

```text
8 % 5 = 3
```

So:

```text
x = 8 mod 15
```

## CP Forms

```text
D: linear Diophantine.
D/E: CRT merge cycles.
CM: modular systems and non-coprime moduli.
```

---

# 034. Floor Sum Thinking

## Why This Exists

Floor expressions appear in advanced counting.

## Core Mental Model

`floor(x/k)` stays constant for blocks.

## ASCII

```text
x:          0 1 2 3 4 5 6 7 8
floor(x/3):0 0 0 1 1 1 2 2 2
```

So values come in blocks of size 3.

## Child Example

You have candies and boxes of size 3.

```text
0 candies -> 0 full boxes
1 candies -> 0 full boxes
2 candies -> 0 full boxes
3 candies -> 1 full box
4 candies -> 1 full box
5 candies -> 1 full box
6 candies -> 2 full boxes
```

## Basic Sum

```text
sum floor(i/3), i=0..8

0+0+0+1+1+1+2+2+2 = 9
```

## C++17 Basic O(N)

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long n, k;
    cin >> n >> k;

    long long ans = 0;

    for (long long i = 0; i <= n; i++) {
        ans += i / k;
    }

    cout << ans << "\n";
    return 0;
}
```

## Recognition

```text
sum of floor
count lattice points under line
grouping by quotient
divisor summatory
```

## CP Forms

```text
C/D: group same quotient.
D/E: AtCoder floor_sum.
CM: number theory counting with quotient compression.
```

---

# 035. Codeforces Math Forms

## Div2 A

```text
parity
simple formula
min/max
floor/ceil
basic modulo
```

Example recognition:

```text
Can transform by adding 2?
-> parity
```

## Div2 B

```text
gcd/lcm
sorting + math
counting
simple invariant
prefix modulo
```

Example recognition:

```text
Need count numbers divisible by k in [L,R]
-> floor division
```

## Div2 C

```text
binary exponentiation
combinatorics
sieve/factorization
pigeonhole
inclusion-exclusion basics
```

Example recognition:

```text
Huge a^b modulo M
-> binary exponentiation
```

## Div2 D

```text
advanced number theory
DP + combinatorics
prime factorization under constraints
CRT basics
geometry
```

## CM Zone

```text
phi
CRT
floor sum
advanced combinatorics
geometry + data structures
number theory + DP
```

## Contest Checklist

```text
[ ] Is N huge?
[ ] Is answer huge?
[ ] Is operation repeated?
[ ] Is there divisibility?
[ ] Is there choose/ways?
[ ] Is there impossible/possible?
[ ] Is there coordinate geometry?
```

---

# 036. FAANG Math Forms

## Common Forms

```text
Pow(x,n)                  -> binary exponentiation
Product Except Self       -> prefix/suffix multiplication
Happy Number              -> cycle detection
Excel Column Number       -> base conversion
Random Pick With Weight   -> prefix sum + probability
Rectangle Overlap         -> coordinate geometry
Robot Bounded Circle      -> direction cycle
Single Number             -> XOR
Subarray Sum Divisible K  -> prefix modulo
```

## Interview Explanation Template

```text
1. Brute force:
   Describe direct simulation.

2. Bottleneck:
   Explain why too slow.

3. Hidden structure:
   formula / modulo / gcd / invariant / cycle.

4. Optimal:
   Give code and complexity.

5. Edge cases:
   zero, negative, overflow, modulo.
```

---

# 037. Math Bug Encyclopedia

## 1. Negative Modulo

Bad:

```cpp
cout << (-2 % 7); // -2 in C++
```

Good:

```cpp
long long norm(long long x, long long mod) {
    x %= mod;
    if (x < 0) x += mod;
    return x;
}
```

## 2. Overflow Before Mod

Bad:

```cpp
long long x = a * b % MOD;
```

If `a` and `b` are around `1e18`, multiplication overflows.

Good:

```cpp
long long x = (__int128)a * b % MOD;
```

## 3. Wrong Ceil

Bad:

```cpp
ceil(a / b)
```

Because `a/b` already truncated.

Good for positive integers:

```cpp
(a + b - 1) / b
```

## 4. Modular Division Bug

Bad:

```cpp
a / b % MOD
```

Good if inverse exists:

```cpp
a * inv(b) % MOD
```

## 5. Geometry Precision Bug

Bad:

```text
compare slopes using double
```

Good:

```text
use cross product
```

## 6. GCD/LCM Overflow

Bad:

```cpp
a * b / gcd(a,b)
```

Good:

```cpp
a / gcd(a,b) * b
```

## 7. Factorization Missing Remaining Prime

Bad:

```text
loop finishes and ignores n > 1
```

Good:

```cpp
if (n > 1) factors.push_back({n,1});
```

---

# 099. Final Cheat Sheet

## Core Formula Sheet

```text
ceil(a/b) positive       = (a+b-1)/b
multiples in [L,R]       = R/k - (L-1)/k
gcd(a,b)                 = gcd(b,a%b)
lcm(a,b)                 = a/gcd(a,b)*b
C(n,r)                   = n!/(r!(n-r)!)
stars and bars           = C(n+k-1,k-1)
positive stars-bars      = C(n-1,k-1)
Catalan                  = C(2n,n)/(n+1)
Derangement              = D[n]=(n-1)(D[n-1]+D[n-2])
phi(n)                   = n * Π(1 - 1/p)
orientation              = cross(B-A,C-A)
```

## Pattern Router

```text
Huge answer          -> modulo
Repeated power       -> binary exponentiation
Common divisor       -> gcd/lcm
Prime structure      -> sieve/factorization
Choose ways          -> nCr/permutation
Distribute items     -> stars and bars
At least one         -> inclusion-exclusion
Guaranteed collision -> pigeonhole
No fixed point       -> derangement
Balanced structure   -> Catalan
Random average       -> expected value
Pairs cancel         -> XOR
Coordinates          -> geometry
Remainder systems    -> CRT
```

## Master C++17 Math Template

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;
const ll MOD = 1'000'000'007;

ll norm(ll x, ll mod = MOD) {
    x %= mod;
    if (x < 0) x += mod;
    return x;
}

ll mulMod(ll a, ll b, ll mod = MOD) {
    return (__int128)norm(a, mod) * norm(b, mod) % mod;
}

ll binpow(ll a, ll b, ll mod = MOD) {
    a = norm(a, mod);
    ll res = 1 % mod;

    while (b > 0) {
        if (b & 1) res = mulMod(res, a, mod);
        a = mulMod(a, a, mod);
        b >>= 1;
    }

    return res;
}

ll invPrime(ll x, ll mod = MOD) {
    return binpow(x, mod - 2, mod);
}

ll gcdll(ll a, ll b) {
    a = llabs(a);
    b = llabs(b);

    while (b != 0) {
        ll r = a % b;
        a = b;
        b = r;
    }

    return a;
}

ll lcmll(ll a, ll b) {
    if (a == 0 || b == 0) return 0;
    return a / gcdll(a, b) * b;
}

ll ceilDivPositive(ll a, ll b) {
    return (a + b - 1) / b;
}

ll countMultiples(ll L, ll R, ll k) {
    return R / k - (L - 1) / k;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    return 0;
}
```

## One Picture To Remember

```text
MATH PROBLEM
    |
    v
Do not memorize formula first.
Find hidden structure first.
    |
    +-- remainder?       -> modulo / cycle / inverse / CRT
    +-- factor?          -> gcd / lcm / prime / divisor
    +-- choice?          -> nCr / permutation / stars-bars
    +-- preserved?       -> parity / invariant / xor
    +-- position?        -> number line / abs / geometry
    +-- random?          -> probability / expectation
    |
    v
Pick template
    |
    v
Dry run with small example
    |
    v
Check bugs:
    overflow / negative mod / edge case / complexity
```

---

# Final Learning Rule

A 10/10 math note is not the biggest note.

A 10/10 math note makes you think:

```text
I see the pattern.
I know why it works.
I can dry run it.
I can code it safely.
I know where it fails.
```
