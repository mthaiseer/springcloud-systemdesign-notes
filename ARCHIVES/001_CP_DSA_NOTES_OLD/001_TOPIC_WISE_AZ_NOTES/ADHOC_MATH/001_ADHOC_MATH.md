# CP Math Master Guide for Codeforces A/B/C/D + LeetCode Contests

> Focus: **Ad Hoc + Greedy + Constructive + Math Observation** for DSA/CP.
>
> Goal: solve CF Div2 **A/B/C/D**, LeetCode Q1/Q2/Q3, and build foundation for Q4.

---

## Clickable Index

### Part 0 — How to Use Math in CP
1. [Math is Observation, not Formula Memorization](#1-math-is-observation-not-formula-memorization)
2. [A/B/C/D Math Decision Tree](#2-abcd-math-decision-tree)
3. [Ad Hoc + Greedy + Constructive Dependency](#3-ad-hoc--greedy--constructive-dependency)
4. [Universal C++ Math Template](#4-universal-c-math-template)

### Part 1 — Core Math Topics
5. [Parity / Odd-Even](#5-parity--odd-even)
6. [Invariants](#6-invariants)
7. [Modulo / Cyclic Math](#7-modulo--cyclic-math)
8. [GCD / LCM](#8-gcd--lcm)
9. [Prime / Sieve / Factorization](#9-prime--sieve--factorization)
10. [Divisors](#10-divisors)
11. [XOR / Bit Math](#11-xor--bit-math)
12. [Counting / Contribution](#12-counting--contribution)
13. [Combinatorics](#13-combinatorics)
14. [Inclusion-Exclusion](#14-inclusion-exclusion)
15. [Binary Exponentiation / Modular Inverse](#15-binary-exponentiation--modular-inverse)
16. [Pigeonhole Principle](#16-pigeonhole-principle)
17. [Game Theory Basics](#17-game-theory-basics)
18. [Coordinate Geometry Basics](#18-coordinate-geometry-basics)

### Part 2 — How Math Connects to CF Thinking
19. [Math in Ad Hoc Problems](#19-math-in-ad-hoc-problems)
20. [Math in Greedy Problems](#20-math-in-greedy-problems)
21. [Math in Constructive Problems](#21-math-in-constructive-problems)
22. [Pattern Recognition Checklist](#22-pattern-recognition-checklist)
23. [A/B/C/D Practice Ladder](#23-abcd-practice-ladder)
24. [Final Revision Sheet](#24-final-revision-sheet)

---

# 1. Math is Observation, not Formula Memorization

In Codeforces A/B/C/D, math usually appears as:

```text
Problem statement
   ↓
Observation
   ↓
Small formula / parity / gcd / modulo / xor
   ↓
Simple implementation
```

Most C/D problems are not heavy theorem problems. They are usually:

```text
Ad Hoc observation + Greedy / Constructive + Light Math
```

## What you should ask first

```text
1. Does parity decide possibility?
2. Is something invariant after operations?
3. Does modulo create cycles?
4. Does gcd/lcm simplify divisibility?
5. Does xor cancel duplicates?
6. Can I count contribution instead of simulating?
7. Can sorting expose a greedy choice?
8. Can I construct using extremes, parity, or blocks?
```

---

# 2. A/B/C/D Math Decision Tree

```text
Start
│
├── Operations repeated?
│   ├── Check invariant: sum, parity, gcd, modulo
│   └── Try reverse simulation
│
├── Need minimum / maximum?
│   ├── Sort?
│   ├── Greedy forced choice?
│   └── Binary search on answer?
│
├── Need count?
│   ├── Prefix / contribution
│   ├── nC2 / pairs
│   ├── Inclusion-exclusion
│   └── Modular arithmetic
│
├── Need build answer?
│   ├── Constructive parity
│   ├── Permutation pattern
│   ├── Alternate / extremes
│   └── Maintain invariant
│
└── Bitwise words? XOR, AND, OR?
    ├── Bit contribution
    ├── Prefix xor
    └── XOR cancellation
```

---

# 3. Ad Hoc + Greedy + Constructive Dependency

Best learning order:

```text
Ad Hoc Observation
      ↓
Greedy Reasoning
      ↓
Constructive Building
```

Math supports all three:

```text
Parity + Invariant + GCD + XOR + Counting
              ↓
       Unlocks hidden observation
```

Why greedy before constructive?

Many constructive problems secretly ask:

```text
Build an answer using greedy choices while preserving an invariant.
```

---

# 4. Universal C++ Math Template

```cpp
#include <bits/stdc++.h>
using namespace std;
#define int long long
```

```cpp
long long gcdll(long long a, long long b) {
    while (b) {
        long long r = a % b;
        a = b;
        b = r;
    }
    return a;
}

long long lcmll(long long a, long long b) {
    return a / gcdll(a, b) * b;
}

long long modpow(long long a, long long e, long long mod) {
    long long res = 1 % mod;
    a %= mod;
    while (e > 0) {
        if (e & 1) res = res * a % mod;
        a = a * a % mod;
        e >>= 1;
    }
    return res;
}

signed main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;
    while (t--) {
        // solve each test case
    }
}
```

---

# 5. Parity / Odd-Even


## Core idea

Parity means whether a number is odd or even.

```text
even + even = even
odd + odd   = even
odd + even  = odd
```

In CF, parity often decides:
- possible / impossible
- number of operations
- who wins a game
- how to construct permutation

## When to suspect parity

```text
Operation changes value by 2
Operation swaps two things
Operation adds/subtracts same amount
Problem asks possible/impossible
Array has odd/even counts
```

## Problem: Can all numbers become even?

### Statement
Given an array. In one operation, you may add 2 to any element. Can all numbers become even?

### Input
```text
n = 5
arr = [1, 3, 4, 6, 8]
```

### Output
```text
NO
```

### Observation
Adding 2 never changes parity.

```text
1 -> 3 -> 5 -> odd forever
3 -> 5 -> 7 -> odd forever
4 -> 6 -> 8 -> even forever
```

So if any number is odd initially, it can never become even.

### C++ Code
```cpp
#include <bits/stdc++.h>
using namespace std;
#define int long long

signed main() {
    int n;
    cin >> n;
    vector<int> a(n);
    for (int &x : a) cin >> x;

    bool ok = true;
    for (int x : a) {
        if (x % 2 != 0) ok = false;
    }

    cout << (ok ? "YES" : "NO") << '\n';
}
```

### Index-by-Index Dry Run
```text
arr = [1, 3, 4, 6, 8]

index 0: 1 is odd  -> impossible flag
index 1: 3 is odd  -> still impossible
index 2: 4 is even
index 3: 6 is even
index 4: 8 is even

Answer = NO
```

## How it applies to Ad Hoc / Greedy / Constructive

| Area | Usage |
|---|---|
| Ad Hoc | spot impossibility quickly |
| Greedy | choose operation that fixes parity first |
| Constructive | build odd/even arrangement |

---

# 6. Invariants


## Core idea

An invariant is something that **never changes** after operations.

Examples:
- parity of sum
- gcd of array
- xor of all elements
- total sum
- value modulo k

## When to suspect invariant

```text
Problem has operations
Need possible/impossible
Many transformations allowed
Need prove answer cannot exist
```

## Problem: Equalize by moving 1 unit

### Statement
You can choose two indices `i, j`, subtract 1 from `a[i]`, and add 1 to `a[j]`. Can all elements become equal?

### Input
```text
n = 4
arr = [1, 2, 3, 4]
```

### Output
```text
NO
```

### Observation
Total sum never changes.

```text
Before operation: sum = S
After operation:  one element -1, another +1
New sum = S
```

For all elements to be equal, sum must be divisible by n.

```text
sum = 1 + 2 + 3 + 4 = 10
n = 4
10 % 4 != 0
```

So impossible.

### C++ Code
```cpp
#include <bits/stdc++.h>
using namespace std;
#define int long long

signed main() {
    int n;
    cin >> n;
    vector<int> a(n);
    int sum = 0;
    for (int &x : a) {
        cin >> x;
        sum += x;
    }

    cout << (sum % n == 0 ? "YES" : "NO") << '\n';
}
```

### Dry Run
```text
index 0: sum = 1
index 1: sum = 3
index 2: sum = 6
index 3: sum = 10

10 % 4 = 2
Answer = NO
```

## Common invariants table

| Operation | Possible invariant |
|---|---|
| add/subtract 2 | parity |
| swap elements | multiset / sum |
| move x from one to another | total sum |
| xor with same number twice | xor state |
| replace a,b with gcd-like operation | gcd |
| rotate array | cyclic order |

---

# 7. Modulo / Cyclic Math


## Core idea

Modulo compresses infinite values into finite states.

```text
x % k gives remainder from 0 to k-1
```

## When to suspect modulo

```text
Problem has cycles
Days / weeks / rotations
Large number of operations
Only remainder matters
```

## Problem: Position after k moves in circular array

### Statement
You start at index 0 in an array of size n. Each move goes one step right cyclically. Where are you after k moves?

### Input
```text
n = 5, k = 12
```

### Output
```text
2
```

### Observation
Every 5 moves returns to same position.

```text
position = k % n = 12 % 5 = 2
```

### C++ Code
```cpp
#include <bits/stdc++.h>
using namespace std;
#define int long long

signed main() {
    int n, k;
    cin >> n >> k;
    cout << k % n << '\n';
}
```

### Dry Run
```text
n = 5
k = 12

moves:
0 -> 1 -> 2 -> 3 -> 4 -> 0 -> 1 -> 2 -> 3 -> 4 -> 0 -> 1 -> 2

After 12 moves = index 2
Using formula = 12 % 5 = 2
```

## CP trick

If operation repeats many times:

```text
Find cycle length
Answer = k % cycle_length
```

---

# 8. GCD / LCM


## Core idea

GCD finds common divisibility structure.

```text
gcd(a, b) = largest number dividing both a and b
```

LCM finds first common multiple.

```text
lcm(a, b) = a / gcd(a, b) * b
```

## When to suspect GCD

```text
Divisibility
Array operations based on factors
Make all numbers equal by dividing
Common step size
```

## Problem: Can all numbers be made multiples of x?

### Statement
Given array and x. Can every number be divisible by x?

### Input
```text
x = 3
arr = [6, 12, 15]
```

### Output
```text
YES
```

### Observation
Each element must satisfy `a[i] % x == 0`.

### C++ Code
```cpp
#include <bits/stdc++.h>
using namespace std;
#define int long long

signed main() {
    int n, x;
    cin >> n >> x;
    vector<int> a(n);
    bool ok = true;
    for (int &v : a) {
        cin >> v;
        if (v % x != 0) ok = false;
    }
    cout << (ok ? "YES" : "NO") << '\n';
}
```

### Dry Run
```text
x = 3
6  % 3 = 0
12 % 3 = 0
15 % 3 = 0
Answer YES
```

## Important GCD observation

If you can add/subtract multiples of x, then value modulo x is invariant.

```text
a -> a + x
remainder does not change
```

This is both **GCD math** and **invariant thinking**.

## Euclidean dry run
```text
gcd(48, 18)
48 % 18 = 12
18 % 12 = 6
12 % 6  = 0
answer = 6
```

### Code
```cpp
long long gcdll(long long a, long long b) {
    while (b) {
        long long r = a % b;
        a = b;
        b = r;
    }
    return a;
}
```

---

# 9. Prime / Sieve / Factorization


## Core idea

Prime numbers are building blocks of integers.

```text
12 = 2^2 * 3
18 = 2 * 3^2
```

## When to suspect primes

```text
Divisors
Coprime
Need factor groups
Large number many queries
```

## Sieve of Eratosthenes

### Code
```cpp
vector<int> sieve(int n) {
    vector<int> isPrime(n + 1, true);
    isPrime[0] = isPrime[1] = false;

    for (int i = 2; i * i <= n; i++) {
        if (isPrime[i]) {
            for (int j = i * i; j <= n; j += i) {
                isPrime[j] = false;
            }
        }
    }

    vector<int> primes;
    for (int i = 2; i <= n; i++) {
        if (isPrime[i]) primes.push_back(i);
    }
    return primes;
}
```

## Dry Run for n = 10
```text
Start: 2 3 4 5 6 7 8 9 10
Prime 2 -> remove 4,6,8,10
Prime 3 -> remove 9
Remaining primes: 2,3,5,7
```

## Problem: Count primes up to n

### Input
```text
n = 10
```

### Output
```text
4
```

### Applied observation
Use sieve instead of testing each number by all divisors.


---

# 10. Divisors


## Core idea

Divisors come in pairs.

```text
n = 36
1 * 36
2 * 18
3 * 12
4 * 9
6 * 6
```

So we only loop until `sqrt(n)`.

## Problem: Print all divisors

### Input
```text
n = 36
```

### Output
```text
1 2 3 4 6 9 12 18 36
```

### C++ Code
```cpp
#include <bits/stdc++.h>
using namespace std;
#define int long long

signed main() {
    int n;
    cin >> n;
    vector<int> divs;

    for (int d = 1; d * d <= n; d++) {
        if (n % d == 0) {
            divs.push_back(d);
            if (d != n / d) divs.push_back(n / d);
        }
    }

    sort(divs.begin(), divs.end());
    for (int d : divs) cout << d << ' ';
}
```

### Dry Run
```text
n = 36

d = 1 -> pair 1,36
d = 2 -> pair 2,18
d = 3 -> pair 3,12
d = 4 -> pair 4,9
d = 5 -> no
d = 6 -> pair 6,6

Stop because next d=7 and 7*7 > 36
```

## Why sqrt appears

If `d` is a divisor, then `n/d` is the paired divisor.

One of the pair is always `<= sqrt(n)`.

---

# 11. XOR / Bit Math


## Core idea

XOR cancels equal values.

```text
a ^ a = 0
x ^ 0 = x
```

## When to suspect XOR

```text
Pairs cancel
Find unique element
Bitwise operation allowed
Game theory
Constructive with xor target
```

## Problem: Single number

### Statement
Every number appears twice except one. Find the unique number.

### Input
```text
arr = [4, 1, 2, 1, 2]
```

### Output
```text
4
```

### Observation
Duplicates cancel.

```text
4 ^ 1 ^ 2 ^ 1 ^ 2
= 4 ^ (1 ^ 1) ^ (2 ^ 2)
= 4 ^ 0 ^ 0
= 4
```

### C++ Code
```cpp
#include <bits/stdc++.h>
using namespace std;
#define int long long

signed main() {
    int n;
    cin >> n;
    int xr = 0;
    for (int i = 0; i < n; i++) {
        int x;
        cin >> x;
        xr ^= x;
    }
    cout << xr << '\n';
}
```

### Index-by-Index Dry Run
```text
xr = 0
index 0: xr = 0 ^ 4 = 4
index 1: xr = 4 ^ 1 = 5
index 2: xr = 5 ^ 2 = 7
index 3: xr = 7 ^ 1 = 6
index 4: xr = 6 ^ 2 = 4
answer = 4
```

## Bit contribution idea

For pair xor sum, count how many numbers have bit `b` set and unset.

```text
contribution of bit b = set_count * unset_count * 2^b
```

This avoids O(n^2).

---

# 12. Counting / Contribution


## Core idea

Instead of simulating every pair/subarray, count how much each element contributes.

## Most common formulas

```text
Number of pairs from n items = n * (n - 1) / 2
Number of subarrays = n * (n + 1) / 2
Element at index i appears in (i + 1) * (n - i) subarrays
```

## Problem: Sum of all subarray sums

### Input
```text
arr = [1, 2, 3]
```

### Output
```text
20
```

### Brute force
```text
[1] = 1
[1,2] = 3
[1,2,3] = 6
[2] = 2
[2,3] = 5
[3] = 3
Total = 20
```

### Observation
Element contribution:

```text
a[i] appears in (i + 1) * (n - i) subarrays
```

### Table

| index | value | left choices | right choices | contribution |
|---|---:|---:|---:|---:|
| 0 | 1 | 1 | 3 | 3 |
| 1 | 2 | 2 | 2 | 8 |
| 2 | 3 | 3 | 1 | 9 |

Total = 3 + 8 + 9 = 20

### C++ Code
```cpp
#include <bits/stdc++.h>
using namespace std;
#define int long long

signed main() {
    int n;
    cin >> n;
    vector<int> a(n);
    for (int &x : a) cin >> x;

    long long ans = 0;
    for (int i = 0; i < n; i++) {
        long long left = i + 1;
        long long right = n - i;
        ans += a[i] * left * right;
    }

    cout << ans << '\n';
}
```

### Dry Run
```text
arr = [1,2,3]

i=0: contribution = 1 * 1 * 3 = 3
i=1: contribution = 2 * 2 * 2 = 8
i=2: contribution = 3 * 3 * 1 = 9
ans = 20
```

## How this appears in CF

```text
Need count pairs/subarrays/subsequences
O(n^2) too slow
Find contribution of each index/value/bit
```

---

# 13. Combinatorics


## Core idea

Combinatorics counts choices.

```text
nCr = choose r items from n items
```

## Most common CP usage

```text
choose 2 equal values -> cnt * (cnt - 1) / 2
choose positions
count arrangements
```

## Problem: Count equal pairs

### Input
```text
arr = [1, 2, 1, 1, 2]
```

### Output
```text
4
```

### Observation
Frequency:

```text
1 appears 3 times -> choose 2 = 3
2 appears 2 times -> choose 2 = 1
Total = 4
```

### C++ Code
```cpp
#include <bits/stdc++.h>
using namespace std;
#define int long long

signed main() {
    int n;
    cin >> n;
    map<int,int> freq;
    for (int i = 0; i < n; i++) {
        int x;
        cin >> x;
        freq[x]++;
    }

    long long ans = 0;
    for (auto [val, cnt] : freq) {
        ans += cnt * (cnt - 1) / 2;
    }

    cout << ans << '\n';
}
```

### Dry Run
```text
arr = [1,2,1,1,2]

freq[1] = 3 -> pairs = 3*2/2 = 3
freq[2] = 2 -> pairs = 2*1/2 = 1
answer = 4
```

## nCr with modulo

```cpp
const long long MOD = 1e9 + 7;
const int N = 200000;
long long fact[N + 1], invfact[N + 1];

long long modpow(long long a, long long e) {
    long long res = 1;
    while (e) {
        if (e & 1) res = res * a % MOD;
        a = a * a % MOD;
        e >>= 1;
    }
    return res;
}

long long C(int n, int r) {
    if (r < 0 || r > n) return 0;
    return fact[n] * invfact[r] % MOD * invfact[n-r] % MOD;
}
```

---

# 14. Inclusion-Exclusion


## Core idea

When counting union, avoid double counting intersection.

![Inclusion Exclusion Visual](data:image/png;base64,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)

```text
|A union B| = |A| + |B| - |A intersection B|
```

## When to suspect inclusion-exclusion

```text
Count numbers divisible by a or b
Count items satisfying condition A or B
Overlap exists
```

## Problem: Count numbers <= n divisible by a or b

### Input
```text
n = 20, a = 2, b = 3
```

### Output
```text
13
```

### Observation
```text
multiples of 2: floor(20/2) = 10
multiples of 3: floor(20/3) = 6
multiples of both 2 and 3 = multiples of lcm(2,3)=6: floor(20/6)=3
answer = 10 + 6 - 3 = 13
```

### C++ Code
```cpp
#include <bits/stdc++.h>
using namespace std;
#define int long long

long long gcdll(long long a, long long b) {
    while (b) {
        long long r = a % b;
        a = b;
        b = r;
    }
    return a;
}

long long lcmll(long long a, long long b) {
    return a / gcdll(a, b) * b;
}

signed main() {
    long long n, a, b;
    cin >> n >> a >> b;

    long long both = lcmll(a, b);
    long long ans = n / a + n / b - n / both;
    cout << ans << '
';
}
```

### Dry Run
```text
n = 20
A = multiples of 2 = 2,4,6,8,10,12,14,16,18,20 -> 10
B = multiples of 3 = 3,6,9,12,15,18 -> 6
Intersection = 6,12,18 -> 3
Union = 10 + 6 - 3 = 13
```

---

# 15. Binary Exponentiation / Modular Inverse


## Core idea

Compute power fast using repeated squaring.

```text
a^13 = a^8 * a^4 * a^1
```

## When to suspect binary exponentiation

```text
Power is huge
Need modulo power
Combinatorics with modulo inverse
```

## Problem: Calculate a^b mod M

### Input
```text
a = 2, b = 10, M = 1000000007
```

### Output
```text
1024
```

### C++ Code
```cpp
#include <bits/stdc++.h>
using namespace std;
#define int long long

long long modpow(long long a, long long b, long long mod) {
    long long ans = 1 % mod;
    a %= mod;

    while (b > 0) {
        if (b & 1) ans = ans * a % mod;
        a = a * a % mod;
        b >>= 1;
    }

    return ans;
}

signed main() {
    long long a, b, mod;
    cin >> a >> b >> mod;
    cout << modpow(a, b, mod) << '\n';
}
```

### Dry Run
```text
a=2, b=10
ans=1

b=10 even -> a=4, b=5
b=5 odd  -> ans=4, a=16, b=2
b=2 even -> a=256, b=1
b=1 odd  -> ans=4*256=1024, b=0
```

## Modular inverse

If MOD is prime:

```text
inverse(a) = a^(MOD-2) mod MOD
```

Used in:
- nCr modulo prime
- probability modulo
- division under modulo

---

# 16. Pigeonhole Principle


## Core idea

If more objects than boxes, some box must contain at least two objects.

## When to suspect pigeonhole

```text
Need guarantee existence
n items, k categories
Remainders modulo k
Duplicate forced
```

## Problem: Two numbers with same remainder

### Statement
Given n+1 numbers, prove two have same remainder modulo n.

### Observation
There are only n possible remainders:

```text
0,1,2,...,n-1
```

But there are n+1 numbers.

So at least two numbers share a remainder.

## CP usage

Pigeonhole often appears in constructive proofs:

```text
If there are too many items, some collision is guaranteed.
```

---

# 17. Game Theory Basics


## Core idea

Many simple games depend on winning/losing state.

## Nim XOR rule

```text
xor of pile sizes = 0  -> losing state
xor of pile sizes != 0 -> winning state
```

## Problem: Nim winner

### Input
```text
piles = [1, 2, 3]
```

### Output
```text
Second
```

### Observation
```text
1 ^ 2 ^ 3 = 0
```

Current player is losing if both play optimally.

### C++ Code
```cpp
#include <bits/stdc++.h>
using namespace std;
#define int long long

signed main() {
    int n;
    cin >> n;
    int xr = 0;
    for (int i = 0; i < n; i++) {
        int x;
        cin >> x;
        xr ^= x;
    }
    cout << (xr ? "First" : "Second") << '\n';
}
```

### Dry Run
```text
xr = 0
xr ^= 1 -> 1
xr ^= 2 -> 3
xr ^= 3 -> 0
answer = Second
```

## CF game checklist

```text
1. Can move count decide winner?
2. Does parity decide winner?
3. Is xor involved?
4. Can current player move to losing state?
```

---

# 18. Coordinate Geometry Basics


## Core idea

Geometry in A/B/C/D is usually basic:
- distance
- slope
- area
- orientation
- Manhattan distance

![Distance Formula Visual](data:image/png;base64,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)

## Distance formula

```text
d = sqrt((x2-x1)^2 + (y2-y1)^2)
```

## Problem: Distance between two points

### Input
```text
x1 = -6, y1 = -6
x2 = 6, y2 = 6
```

### Output
```text
16.9706
```

### Observation
```text
dx = 6 - (-6) = 12
dy = 6 - (-6) = 12

d = sqrt(12^2 + 12^2)
  = sqrt(288)
  = 16.9706
```

### C++ Code
```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    double x1, y1, x2, y2;
    cin >> x1 >> y1 >> x2 >> y2;

    double dx = x2 - x1;
    double dy = y2 - y1;
    double d = sqrt(dx * dx + dy * dy);

    cout << fixed << setprecision(6) << d << '
';
}
```

### Dry Run
```text
x1=-6, y1=-6, x2=6, y2=6

dx = 12
dy = 12
dx*dx = 144
dy*dy = 144
sum = 288
sqrt(288) = 16.9706
```

## Manhattan distance

```text
abs(x1-x2) + abs(y1-y2)
```

Often used in grid problems.

---

# 19. Math in Ad Hoc Problems


## What ad hoc means

Ad hoc means no fixed algorithm. You solve by observation.

Math helps you find the observation.

## Common ad hoc math forms

| Form | Question to ask |
|---|---|
| Parity | Does odd/even decide answer? |
| Modulo | Does remainder decide state? |
| Invariant | What never changes? |
| Counting | Can I count directly? |
| Boundary | Only min/max/endpoints matter? |
| Formula after dry run | Can I test n=1..10 and derive formula? |

## Problem: Bulb Switcher style observation

### Statement
There are n bulbs off. Round i toggles every i-th bulb. How many bulbs remain on?

### Input
```text
n = 10
```

### Output
```text
3
```

### Observation
A bulb toggles once for each divisor.

```text
bulb 6 divisors: 1,2,3,6 -> 4 toggles -> OFF
bulb 9 divisors: 1,3,9   -> 3 toggles -> ON
```

Only perfect squares have odd number of divisors.

Perfect squares <= 10:

```text
1, 4, 9
```

Answer = 3.

### C++ Code
```cpp
#include <bits/stdc++.h>
using namespace std;
#define int long long

signed main() {
    long long n;
    cin >> n;
    cout << (long long)sqrt(n) << '\n';
}
```

### Index-by-Index Dry Run
```text
n = 10

bulb 1: divisors 1       -> odd toggles -> ON
bulb 2: divisors 1,2     -> even -> OFF
bulb 3: divisors 1,3     -> even -> OFF
bulb 4: divisors 1,2,4   -> odd -> ON
bulb 5: divisors 1,5     -> even -> OFF
bulb 6: divisors 1,2,3,6 -> even -> OFF
bulb 7: divisors 1,7     -> even -> OFF
bulb 8: divisors 1,2,4,8 -> even -> OFF
bulb 9: divisors 1,3,9   -> odd -> ON
bulb10: divisors 1,2,5,10-> even -> OFF

ON bulbs = 1,4,9 = 3
```

## Ad hoc habit

For every problem, dry run small values:

```text
n=1
n=2
n=3
n=4
n=5
```

Then look for:
- parity
- square
- gcd
- modulo
- monotonicity
- symmetry

---

# 20. Math in Greedy Problems


## Greedy + math idea

Greedy needs proof. Math gives proof.

Common proof styles:
- exchange argument
- invariant preservation
- monotonicity
- contribution comparison

## Problem: Minimum coins with canonical denominations

### Statement
Given coin values `[100, 20, 10, 5, 1]`, make amount x using minimum coins.

### Input
```text
x = 126
```

### Output
```text
4
```

### Observation
Always take the largest possible coin.

```text
126 -> take 100, remaining 26
26  -> take 20, remaining 6
6   -> take 5, remaining 1
1   -> take 1, remaining 0
```

### C++ Code
```cpp
#include <bits/stdc++.h>
using namespace std;
#define int long long

signed main() {
    int x;
    cin >> x;
    vector<int> coins = {100, 20, 10, 5, 1};

    int ans = 0;
    for (int c : coins) {
        ans += x / c;
        x %= c;
    }

    cout << ans << '\n';
}
```

### Dry Run
```text
x=126
coin=100 -> use 1, rem=26, ans=1
coin=20  -> use 1, rem=6,  ans=2
coin=10  -> use 0, rem=6,  ans=2
coin=5   -> use 1, rem=1,  ans=3
coin=1   -> use 1, rem=0,  ans=4
```

## Greedy proof checklist

```text
1. What local choice do I make?
2. Why can this choice never hurt?
3. Can any optimal solution be transformed to include my choice?
4. Is some quantity monotonic?
5. Does sorting make choices forced?
```

## Exchange argument mini-template

```text
Assume optimal solution does not choose greedy option G.
Show we can replace its choice with G.
Cost does not increase / validity remains.
Therefore an optimal solution exists with G.
So greedy is safe.
```

---

# 21. Math in Constructive Problems


## Constructive + math idea

Constructive asks you to build an answer.

Math tells:
- when answer is impossible
- what invariant must be maintained
- how to arrange parity/counts

## Common constructive forms

| Form | Tool |
|---|---|
| Permutation construction | parity / extremes |
| Matrix construction | pattern / modulo |
| Operation sequence | invariant / reverse thinking |
| Lexicographic construction | greedy |
| Equalization | sum / divisibility |

## Problem: Beautiful permutation idea

### Statement
Construct a permutation of 1..n such that no adjacent numbers differ by 1.

### Input
```text
n = 5
```

### Output
```text
2 4 1 3 5
```

### Observation
Separate evens and odds.

```text
Evens: 2 4
Odds:  1 3 5
Combined: 2 4 1 3 5
```

Adjacent differences:

```text
|2-4| = 2
|4-1| = 3
|1-3| = 2
|3-5| = 2
```

Valid.

### C++ Code
```cpp
#include <bits/stdc++.h>
using namespace std;
#define int long long

signed main() {
    int n;
    cin >> n;

    if (n == 1) {
        cout << 1 << '\n';
        return 0;
    }
    if (n <= 3) {
        cout << "NO SOLUTION\n";
        return 0;
    }

    for (int i = 2; i <= n; i += 2) cout << i << ' ';
    for (int i = 1; i <= n; i += 2) cout << i << ' ';
    cout << '\n';
}
```

### Dry Run
```text
n=5

print evens:
i=2 -> 2
i=4 -> 4

print odds:
i=1 -> 1
i=3 -> 3
i=5 -> 5

answer = 2 4 1 3 5
```

## Constructive thinking checklist

```text
1. What condition makes impossible?
2. What invariant must remain true?
3. Can I build by parity groups?
4. Can I build using smallest/largest alternation?
5. Can I reverse operations?
6. Can I test n=1..10 and see pattern?
```

---

# 22. Pattern Recognition Checklist


## 5-second CF checklist

When you open a problem, ask:

```text
Input small?       -> brute force / DP possible?
Need min/max?      -> greedy / binary search?
Need count?        -> prefix / contribution / combinatorics?
Need possible?     -> invariant / parity / gcd / modulo?
Need build?        -> constructive / greedy / parity pattern?
Operations?        -> invariant / reverse simulation?
Pairs/subarrays?   -> contribution / prefix / two pointers?
Divisibility?      -> gcd / lcm / primes?
Bitwise?           -> xor / bit contribution?
Cycle?             -> modulo / k % cycle
```

## Topic to trigger map

| Keyword in problem | Try first |
|---|---|
| possible / impossible | parity, invariant, gcd |
| minimum operations | greedy, formula, invariant |
| after k operations | modulo, cycle, fast exponentiation |
| pairs | nC2, sorting, two pointers |
| subarrays | prefix, contribution, sliding window |
| divisible | gcd/lcm, primes, modulo |
| xor | prefix xor, xor cancellation |
| construct | parity, greedy, reverse, extremes |
| game | parity, xor, losing/winning state |
| grid distance | Manhattan / Euclidean |

## A/B/C/D reality

```text
A: direct observation / formula / parity
B: sorting + counting / simulation / greedy
C: observation + greedy / constructive / invariant
D: mixed pattern + proof + efficient implementation
```

---

# 23. A/B/C/D Practice Ladder


## Phase 1 — A/B Foundation

Master:
- parity
- modulo
- simple counting
- simulation
- sorting observations

Practice types:
```text
Odd-even answer
Remainder cycles
Frequency count
Min/max formula
Simple constructive
```

## Phase 2 — B/C Transition

Master:
- gcd/lcm
- prefix/contribution
- greedy sorting
- invariant
- xor basics

Practice types:
```text
Array transformation
Pair count
Operation minimization
Sorting + choose
Prefix xor / prefix sum
```

## Phase 3 — C/D Core

Master:
- exchange argument
- constructive parity
- reverse thinking
- divisor/factorization
- combinatorics with modulo

Practice types:
```text
Build permutation
Minimum operations with invariant
Greedy with proof
Counting large answer mod M
Divisibility construction
```

## Phase 4 — D Strength

Master:
- advanced contribution
- bit contribution
- DP + math
- graph + invariant
- binary search + greedy check

Practice types:
```text
O(n^2) to O(n log n)
Offline counting
Bitwise optimization
Math + DP
Constructive with proof
```

---

# 24. Final Revision Sheet


## Must-know formulas

```text
pairs = n * (n - 1) / 2
subarrays = n * (n + 1) / 2
index contribution in subarrays = (i + 1) * (n - i)
lcm(a,b) = a / gcd(a,b) * b
mod inverse under prime MOD = a^(MOD-2) mod MOD
```

## Must-know observations

```text
Adding/subtracting 2 preserves parity
Moving value between elements preserves sum
XOR duplicates cancel
Modulo creates cycles
Perfect squares have odd number of divisors
If all become equal, sum usually divisible by n
If operation uses multiples of x, remainder mod x may be invariant
```

## Must-know C++ snippets

### GCD / LCM
```cpp
long long gcdll(long long a, long long b) {
    while (b) {
        long long r = a % b;
        a = b;
        b = r;
    }
    return a;
}

long long lcmll(long long a, long long b) {
    return a / gcdll(a, b) * b;
}
```

### Fast power
```cpp
long long modpow(long long a, long long e, long long mod) {
    long long res = 1 % mod;
    a %= mod;
    while (e) {
        if (e & 1) res = res * a % mod;
        a = a * a % mod;
        e >>= 1;
    }
    return res;
}
```

### Sieve
```cpp
vector<int> sieve(int n) {
    vector<int> isPrime(n + 1, true);
    isPrime[0] = isPrime[1] = false;
    for (int i = 2; i * i <= n; i++) {
        if (isPrime[i]) {
            for (int j = i * i; j <= n; j += i) {
                isPrime[j] = false;
            }
        }
    }
    vector<int> primes;
    for (int i = 2; i <= n; i++) {
        if (isPrime[i]) primes.push_back(i);
    }
    return primes;
}
```

## Final priority for CF A/B/C/D

```text
1. Parity / invariant
2. GCD / modulo
3. Counting / contribution
4. XOR / bit math
5. Prime / divisors
6. Combinatorics
7. Greedy proof
8. Constructive pattern
```

## One-line mindset

```text
Before coding, reduce the problem using math observation.
```


---

# Extra Practice Bank by Topic

## Parity
- Can operation change odd/even?
- Can all numbers become same parity?
- Does odd count decide winner?

## Invariant
- What value is preserved?
- Sum? XOR? GCD? Remainder?
- If final state requires different invariant, impossible.

## GCD/LCM
- Common divisor problems
- Array gcd after removing one element
- Make all numbers divisible by x

## Counting
- Equal pairs using frequency
- Number of subarrays
- Element contribution
- Bit contribution

## Constructive
- Build permutation using evens then odds
- Alternate min/max
- Reverse operations
- Keep prefix condition valid

## Greedy
- Sort then choose
- Use largest/smallest first
- Heap for dynamic best choice
- Exchange argument proof

---

# Mini Templates for Thinking

## Possible / Impossible Template
```text
1. Check sum divisibility
2. Check parity
3. Check gcd/remainder invariant
4. Check bounds
5. Try small examples
```

## Minimum Operations Template
```text
1. Is there a direct formula?
2. Does each operation fix at most one unit?
3. Can greedy fix largest error first?
4. Is binary search on answer possible?
```

## Construct Answer Template
```text
1. Print small n manually
2. Find impossible cases
3. Find repeating pattern
4. Use parity groups / extremes
5. Verify adjacent/global condition
```

## Count Template
```text
1. What object am I counting?
2. Count directly or via complement?
3. Any overlap? Use inclusion-exclusion.
4. Any repeated values? Use frequency.
5. Any pair/subarray contribution?
```

---

# End

This guide is designed as a revision + problem-solving map. Revisit it before every CF virtual contest until these checks become automatic.
