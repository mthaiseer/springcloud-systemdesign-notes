# MiniMathEngine_Combined_CM_FAANG.md

## From Math Foundations → Number Theory → Combinatorics → Geometry → CP/FAANG Pattern Engine

> Built as one consolidated master guide from the two requested GitHub math tracks:
>
> - `000_MATH_FOUNDATION_ENGINE`
> - `MATH_ENGINE`
>
> Goal: beginner-friendly but deep enough for Codeforces Candidate Master, FAANG interviews, and CP/DSA pattern recognition.

---

# Clickable Index

- [000. How To Use This File](#000-how-to-use-this-file)
- [001. Why Math Matters In CP and FAANG](#001-why-math-matters-in-cp-and-faang)
- [002. Math Mental Model](#002-math-mental-model)
- [003. Number Line Thinking](#003-number-line-thinking)
- [004. Algebra Basics](#004-algebra-basics)
- [005. Min Max Bounds](#005-min-max-bounds)
- [006. Absolute Value Observations](#006-absolute-value-observations)
- [007. Powers and Logarithms](#007-powers-and-logarithms)
- [008. Divisibility, GCD, and LCM](#008-divisibility-gcd-and-lcm)
- [009. Floor, Ceil, and Integer Division](#009-floor-ceil-and-integer-division)
- [010. Odd Even Parity](#010-odd-even-parity)
- [011. Invariant Thinking](#011-invariant-thinking)
- [012. Modulo Intuition and Properties](#012-modulo-intuition-and-properties)
- [013. Modulo Cycle Patterns](#013-modulo-cycle-patterns)
- [014. Binary Exponentiation](#014-binary-exponentiation)
- [015. Modular Inverse](#015-modular-inverse)
- [016. Prime Checking](#016-prime-checking)
- [017. Sieve and SPF](#017-sieve-and-spf)
- [018. Prime Factorization](#018-prime-factorization)
- [019. Divisor Count and Divisor Sum](#019-divisor-count-and-divisor-sum)
- [020. Euler Phi](#020-euler-phi)
- [021. Counting Basics](#021-counting-basics)
- [022. nCr Basics](#022-ncr-basics)
- [023. nCr Modulo Prime](#023-ncr-modulo-prime)
- [024. Permutations With Repetition](#024-permutations-with-repetition)
- [025. Stars and Bars](#025-stars-and-bars)
- [026. Inclusion Exclusion](#026-inclusion-exclusion)
- [027. Pigeonhole Principle](#027-pigeonhole-principle)
- [028. Catalan Numbers](#028-catalan-numbers)
- [029. Derangements](#029-derangements)
- [030. Probability and Expected Value](#030-probability-and-expected-value)
- [031. Binary, Bitwise Math, and XOR](#031-binary-bitwise-math-and-xor)
- [032. Distance, Slope, Line, Area, Orientation](#032-distance-slope-line-area-orientation)
- [033. CRT and Diophantine Equations](#033-crt-and-diophantine-equations)
- [034. Floor Sum](#034-floor-sum)
- [035. CP Math Pattern Decision Engine](#035-cp-math-pattern-decision-engine)
- [036. CF and FAANG Math Problem Ladder](#036-cf-and-faang-math-problem-ladder)
- [037. Common Math Traps](#037-common-math-traps)
- [099. Final Cheat Sheet](#099-final-cheat-sheet)

---

# 000. How To Use This File

This is not a school-math file.

This is a **competitive programming and FAANG math engine**.

Use it like this:

```text
First pass:
    Read mental models + diagrams.

Second pass:
    Type all C++ templates.

Third pass:
    Solve problems by pattern:
        modulo
        gcd/lcm
        primes
        nCr
        parity
        floor/ceil
        geometry
        combinatorics

Fourth pass:
    During contest, use the decision engine.
```

## One Core Rule

```text
Math in CP is not about formulas.
Math in CP is about discovering structure.
```

---

# 001. Why Math Matters In CP and FAANG

Math appears when brute force is too slow.

Example:

```text
Count multiples of k in [L, R]
```

Bad:

```text
Loop through every number.
```

Good:

```text
floor(R/k) - floor((L-1)/k)
```

Math converts:

```text
simulation → formula
large search → logarithmic
counting paths → combinatorics
repeated multiplication → binary exponentiation
pair/triple logic → parity/invariant
```

## Real CP Examples

```text
N up to 1e18       -> need math/log
Answer huge        -> modulo
Count arrangements -> nCr/permutation
Cyclic behavior    -> modulo cycle
Repeated operation -> invariant
Divisibility       -> gcd/lcm
Grid path count    -> combinatorics
```

## FAANG Examples

```text
Two Sum variants         -> modular/hash thinking
Robot bounded path       -> rotation/cycle
Happy number             -> cycle detection
Excel column number      -> base conversion
Rectangle overlap        -> coordinate geometry
Random pick by weight    -> prefix + probability
```

---

# 002. Math Mental Model

The full math engine can be remembered as:

```text
NUMBER
  |
  +-- size       -> bounds, min/max, overflow
  +-- position   -> number line, abs, distance
  +-- division   -> floor, ceil, divisibility
  +-- remainder  -> modulo, cycles, hashing
  +-- factors    -> primes, gcd, lcm, phi
  +-- choices    -> permutation, combination
  +-- repetition -> stars and bars, DP counting
  +-- geometry   -> points, lines, area, orientation
```

## ASCII Map

```text
                CP MATH ENGINE
                      |
   ------------------------------------------------
   |             |              |                 |
Number Line   Modulo       Number Theory     Counting
   |             |              |                 |
abs/floor    cycles       gcd/prime/phi       nCr/stars
bounds       inverse      divisors/SPF        catalan
parity       CRT          factorization       derangement
```

---

# 003. Number Line Thinking

## Mental Model

Numbers are positions on a line.

```text
<---- negative ---- 0 ---- positive ---->
-5 -4 -3 -2 -1 0 1 2 3 4 5
```

Distance between two numbers:

```text
|a - b|
```

## Why It Matters

Number line thinking helps with:

```text
absolute difference
closest pair
range overlap
minimize max distance
median
binary search on answer
```

## Example: Minimum Distance To Target

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n, target;
    cin >> n >> target;

    vector<int> a(n);
    for (int &x : a) cin >> x;

    int best = INT_MAX;

    for (int x : a) {
        best = min(best, abs(x - target));
    }

    cout << best << "\n";
    return 0;
}
```

Complexity:

```text
O(N)
```

## Diagram

```text
target = 10

number line:
0----3------7---10----13--------20
     |      |    |     |         |
   dist7  dist3 0   dist3      dist10
```

---

# 004. Algebra Basics

## Core Mental Model

Algebra is moving symbols without changing truth.

```text
a + b = c
a = c - b
```

You use algebra in CP to transform a slow condition into a fast lookup.

## Classic Transformation

Pair sum:

```text
a[i] + a[j] = X
```

Rearrange:

```text
a[j] = X - a[i]
```

Now use hash set.

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n, X;
    cin >> n >> X;

    unordered_set<int> seen;

    for (int i = 0; i < n; i++) {
        int x;
        cin >> x;

        if (seen.count(X - x)) {
            cout << "YES\n";
            return 0;
        }

        seen.insert(x);
    }

    cout << "NO\n";
    return 0;
}
```

## CM Pattern

Whenever you see:

```text
a + b = target
a - b = target
a * b = target
prefix[j] - prefix[i] = k
```

Try algebraic rearrangement.

---

# 005. Min Max Bounds

## Mental Model

Many problems are solved by finding the smallest possible and largest possible answer.

```text
answer lies inside [low, high]
```

This is the base of binary search on answer.

## Examples

```text
Minimum ship capacity:
low  = max weight
high = sum of weights

Minimum largest subarray sum:
low  = max element
high = sum of all elements

Aggressive cows:
low  = 1
high = max_position - min_position
```

## ASCII

```text
Possible answers:
[ impossible ][ maybe ][ possible ]
 low                         high

Binary search tries middle.
```

## C++ Bound Example

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    long long sum = 0;
    int mx = 0;

    for (int i = 0; i < n; i++) {
        int x;
        cin >> x;
        sum += x;
        mx = max(mx, x);
    }

    cout << "minimum possible answer = " << mx << "\n";
    cout << "maximum possible answer = " << sum << "\n";

    return 0;
}
```

---

# 006. Absolute Value Observations

## Mental Model

`abs(a - b)` is distance.

```text
distance between a and b = |a-b|
```

## Important Properties

```text
abs(x) >= 0
abs(a-b) = abs(b-a)
minimize sum of absolute distances -> median
minimize max distance -> binary search possible
```

## Median Insight

For points on a line, the best meeting point minimizing total absolute distance is the median.

```text
points: 1 2 10 12 13
median: 10
```

## C++ Example

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<long long> a(n);
    for (auto &x : a) cin >> x;

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

Complexity:

```text
O(N log N)
```

---

# 007. Powers and Logarithms

## Mental Model

Powers grow fast.

```text
2^1 = 2
2^10 = 1024
2^20 ≈ 1,000,000
2^30 ≈ 1,000,000,000
2^60 ≈ 1e18
```

So when `N <= 1e18`, repeated halving takes around 60 steps.

## ASCII

```text
2^0  1
2^1  2
2^2  4
2^3  8
2^4  16
...
2^60 huge
```

## CP Uses

```text
binary exponentiation
bitmask
logarithmic loops
binary search depth
powers of two
```

## Power Check C++

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

---

# 008. Divisibility, GCD, and LCM

## Divisibility

`a` is divisible by `b` if:

```text
a % b == 0
```

## GCD Mental Model

GCD is the largest number that divides both.

```text
gcd(12, 18) = 6
```

ASCII factors:

```text
12: 1 2 3 4 6 12
18: 1 2 3 6 9 18
common: 1 2 3 6
largest = 6
```

## Euclid Algorithm

```text
gcd(a, b) = gcd(b, a % b)
```

```cpp
#include <bits/stdc++.h>
using namespace std;

long long gcdll(long long a, long long b) {
    while (b != 0) {
        long long r = a % b;
        a = b;
        b = r;
    }
    return a;
}

int main() {
    long long a, b;
    cin >> a >> b;

    cout << gcdll(a, b) << "\n";
    return 0;
}
```

## LCM

```text
lcm(a, b) = a / gcd(a, b) * b
```

Use division first to reduce overflow.

```cpp
long long lcmll(long long a, long long b) {
    return a / std::gcd(a, b) * b;
}
```

## CP Pattern Detector

Use GCD/LCM when you see:

```text
common divisor
make numbers equal by operations
periods aligning
cycles meeting
divisibility constraints
array gcd
```

---

# 009. Floor, Ceil, and Integer Division

## Mental Model

Integer division cuts off fractional part.

```text
7 / 3 = 2
```

## Floor

```text
floor(a / b)
```

In C++ for positive integers:

```cpp
a / b
```

## Ceil

For positive integers:

```text
ceil(a / b) = (a + b - 1) / b
```

## C++ Example

```cpp
#include <bits/stdc++.h>
using namespace std;

long long ceilDiv(long long a, long long b) {
    return (a + b - 1) / b;
}

int main() {
    long long a, b;
    cin >> a >> b;

    cout << "floor = " << a / b << "\n";
    cout << "ceil  = " << ceilDiv(a, b) << "\n";

    return 0;
}
```

## Count Multiples In Range

```text
count multiples of k in [L, R]
= floor(R/k) - floor((L-1)/k)
```

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long L, R, k;
    cin >> L >> R >> k;

    cout << R / k - (L - 1) / k << "\n";
    return 0;
}
```

## Common Trap

For negative numbers, C++ division truncates toward zero, not mathematical floor. Handle carefully if negatives are involved.

---

# 010. Odd Even Parity

## Mental Model

Parity means whether a number is odd or even.

```text
even % 2 = 0
odd  % 2 = 1
```

## Parity Rules

```text
even + even = even
even + odd  = odd
odd + odd   = even

even * anything = even
odd * odd       = odd
```

## XOR View

Parity is often a 2-state invariant.

```text
0 = even
1 = odd
```

## C++ Example

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long x;
    cin >> x;

    if (x % 2 == 0) cout << "EVEN\n";
    else cout << "ODD\n";

    return 0;
}
```

## CP Pattern Detector

Use parity when you see:

```text
odd/even moves
alternating colors
checkerboard
operation changes value by 2
sum parity
impossible states
```

## ASCII Checkerboard

```text
(0,0) E   (0,1) O   (0,2) E
(1,0) O   (1,1) E   (1,2) O
(2,0) E   (2,1) O   (2,2) E
```

Cell color can be represented by:

```text
(row + col) % 2
```

---

# 011. Invariant Thinking

## Mental Model

An invariant is something that never changes after operations.

```text
Operation happens
    ↓
Some quantity remains same
```

## Example

Operation:

```text
Add 2 to any number.
```

Invariant:

```text
parity never changes.
```

If a number starts odd, it remains odd.

## CP Use

Invariant proves impossible cases.

```text
Can we transform A into B?
```

Check what cannot change.

## C++ Example: Same Parity Transform

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long a, b;
    cin >> a >> b;

    if ((a % 2 + 2) % 2 == (b % 2 + 2) % 2) {
        cout << "POSSIBLE\n";
    } else {
        cout << "IMPOSSIBLE\n";
    }

    return 0;
}
```

## Invariant Checklist

```text
Does operation preserve:
- parity?
- sum?
- gcd?
- modulo class?
- color?
- order?
- xor?
```

---

# 012. Modulo Intuition and Properties

## Mental Model

Modulo keeps numbers inside a circular range.

For `MOD = 7`:

```text
0 1 2 3 4 5 6 0 1 2 3 4 5 6 0 ...
```

ASCII clock:

```text
        0
    6       1
  5           2
    4       3
```

## Why Modulo Appears

```text
large answers
cyclic behavior
hash buckets
sharding
clock arithmetic
circular arrays
```

## Core Rules

```text
(a + b) % m = ((a % m) + (b % m)) % m
(a - b) % m = ((a % m) - (b % m) + m) % m
(a * b) % m = ((a % m) * (b % m)) % m
```

Division is special:

```text
a / b mod m != direct division
```

Use modular inverse when allowed.

## C++ Mod Toolkit

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

ll modAdd(ll a, ll b) {
    return (norm(a) + norm(b)) % MOD;
}

ll modSub(ll a, ll b) {
    return (norm(a) - norm(b) + MOD) % MOD;
}

ll modMul(ll a, ll b) {
    return (norm(a) * norm(b)) % MOD;
}

int main() {
    ll a, b;
    cin >> a >> b;

    cout << modAdd(a, b) << "\n";
    cout << modSub(a, b) << "\n";
    cout << modMul(a, b) << "\n";

    return 0;
}
```

## Common Mistake

Bad:

```cpp
ans = a * b * c;
ans %= MOD;
```

Good:

```cpp
ans = 1;
ans = ans * a % MOD;
ans = ans * b % MOD;
ans = ans * c % MOD;
```

---

# 013. Modulo Cycle Patterns

## Mental Model

Remainders repeat.

Example powers of 2 last digit:

```text
2^1 = 2
2^2 = 4
2^3 = 8
2^4 = 16 -> 6
2^5 = 32 -> 2
```

Cycle:

```text
2, 4, 8, 6
```

## ASCII

```text
2 → 4 → 8 → 6
↑           |
└───────────┘
```

## C++ Cycle Detection For Remainders

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long base, mod;
    cin >> base >> mod;

    vector<int> seen(mod, -1);
    vector<long long> cycle;

    long long cur = 1;
    int step = 0;

    while (seen[cur] == -1) {
        seen[cur] = step++;
        cycle.push_back(cur);
        cur = (cur * base) % mod;
    }

    cout << "cycle starts at index " << seen[cur] << "\n";
    cout << "cycle values: ";
    for (long long x : cycle) cout << x << " ";
    cout << "\n";

    return 0;
}
```

## Pattern Detector

Use cycle when:

```text
repeated operations
huge exponent
state space small
modulo involved
clock/circular behavior
```

---

# 014. Binary Exponentiation

## Problem

Compute:

```text
a^b mod MOD
```

If `b` is up to `1e18`, normal multiplication `b` times is too slow.

## Mental Model

Use binary representation of exponent.

```text
13 = 8 + 4 + 1
a^13 = a^8 * a^4 * a
```

## ASCII

```text
b = 13 = 1101₂

powers:
a^1
a^2
a^4
a^8

use bits that are 1:
a^1 * a^4 * a^8
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;
const ll MOD = 1'000'000'007;

ll binpow(ll a, long long b) {
    a %= MOD;
    ll res = 1;

    while (b > 0) {
        if (b & 1) {
            res = (res * a) % MOD;
        }

        a = (a * a) % MOD;
        b >>= 1;
    }

    return res;
}

int main() {
    long long a, b;
    cin >> a >> b;

    cout << binpow(a, b) << "\n";
    return 0;
}
```

Complexity:

```text
O(log b)
```

---

# 015. Modular Inverse

## Problem

How to divide under modulo?

```text
a / b mod MOD
```

You cannot do direct division.

Instead:

```text
a / b ≡ a * inverse(b) mod MOD
```

## When MOD Is Prime

Using Fermat:

```text
b^(MOD-1) ≡ 1 mod MOD
b^(MOD-2) ≡ inverse(b) mod MOD
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;
const ll MOD = 1'000'000'007;

ll binpow(ll a, long long b) {
    ll res = 1;
    a %= MOD;

    while (b > 0) {
        if (b & 1) res = res * a % MOD;
        a = a * a % MOD;
        b >>= 1;
    }

    return res;
}

ll modInv(ll x) {
    return binpow(x, MOD - 2);
}

int main() {
    ll a, b;
    cin >> a >> b;

    cout << a % MOD * modInv(b) % MOD << "\n";
    return 0;
}
```

## Trap

Inverse exists only if:

```text
gcd(b, MOD) = 1
```

For prime MOD, all non-zero `b` have inverse.

---

# 016. Prime Checking

## Mental Model

If `n` has a divisor larger than `sqrt(n)`, it must also have a paired divisor smaller than `sqrt(n)`.

So test only up to `sqrt(n)`.

## ASCII

```text
n = a * b

If both a and b > sqrt(n),
then a*b > n, impossible.

So one factor must be <= sqrt(n).
```

## C++ Prime Check

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

Complexity:

```text
O(sqrtN)
```

---

# 017. Sieve and SPF

## Sieve Mental Model

Mark multiples of primes.

```text
2 marks 4,6,8,10...
3 marks 6,9,12...
5 marks 10,15,20...
```

## ASCII

```text
Numbers: 2 3 4 5 6 7 8 9 10
Prime 2: P . X . X . X . X
Prime 3: P P X P X P X X X
```

## Sieve Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<bool> isPrime(n + 1, true);
    isPrime[0] = isPrime[1] = false;

    for (long long p = 2; p * p <= n; p++) {
        if (isPrime[p]) {
            for (long long x = p * p; x <= n; x += p) {
                isPrime[x] = false;
            }
        }
    }

    for (int i = 2; i <= n; i++) {
        if (isPrime[i]) cout << i << " ";
    }

    return 0;
}
```

Complexity:

```text
O(N log log N)
```

## SPF: Smallest Prime Factor

Used for fast factorization of many numbers.

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

---

# 018. Prime Factorization

## Mental Model

Break number into prime powers.

```text
60 = 2^2 * 3^1 * 5^1
```

ASCII factor tree:

```text
        60
       /  \
      2    30
          /  \
         2    15
             /  \
            3    5
```

## Trial Division Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<pair<long long,int>> factorize(long long n) {
    vector<pair<long long,int>> res;

    for (long long p = 2; p * p <= n; p++) {
        if (n % p == 0) {
            int cnt = 0;

            while (n % p == 0) {
                n /= p;
                cnt++;
            }

            res.push_back({p, cnt});
        }
    }

    if (n > 1) res.push_back({n, 1});

    return res;
}

int main() {
    long long n;
    cin >> n;

    auto f = factorize(n);

    for (auto [p, e] : f) {
        cout << p << "^" << e << "\n";
    }

    return 0;
}
```

Complexity:

```text
O(sqrtN)
```

---

# 019. Divisor Count and Divisor Sum

If:

```text
n = p1^a1 * p2^a2 * ... * pk^ak
```

Then number of divisors:

```text
(a1 + 1)(a2 + 1)...(ak + 1)
```

Why?

Each prime exponent can be:

```text
0 to ai
```

## Example

```text
60 = 2^2 * 3^1 * 5^1

divisor count = (2+1)(1+1)(1+1) = 12
```

## Divisor Sum

```text
sum divisors =
(1 + p + p^2 + ... + p^a) multiplied for each prime
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

vector<pair<ll,int>> factorize(ll n) {
    vector<pair<ll,int>> res;

    for (ll p = 2; p * p <= n; p++) {
        if (n % p == 0) {
            int e = 0;
            while (n % p == 0) {
                n /= p;
                e++;
            }
            res.push_back({p, e});
        }
    }

    if (n > 1) res.push_back({n, 1});
    return res;
}

int main() {
    ll n;
    cin >> n;

    auto f = factorize(n);

    ll countDiv = 1;
    ll sumDiv = 1;

    for (auto [p, e] : f) {
        countDiv *= (e + 1);

        ll term = 1;
        ll power = 1;

        for (int i = 1; i <= e; i++) {
            power *= p;
            term += power;
        }

        sumDiv *= term;
    }

    cout << countDiv << "\n";
    cout << sumDiv << "\n";

    return 0;
}
```

---

# 020. Euler Phi

## Mental Model

`phi(n)` counts numbers from `1` to `n` that are coprime with `n`.

```text
phi(10) = 4
numbers: 1,3,7,9
```

Formula:

```text
phi(n) = n * Π(1 - 1/p)
```

for each distinct prime `p` dividing `n`.

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long phi(long long n) {
    long long ans = n;

    for (long long p = 2; p * p <= n; p++) {
        if (n % p == 0) {
            while (n % p == 0) n /= p;
            ans -= ans / p;
        }
    }

    if (n > 1) ans -= ans / n;

    return ans;
}

int main() {
    long long n;
    cin >> n;

    cout << phi(n) << "\n";
    return 0;
}
```

## CP Uses

```text
coprime counting
modular exponent reduction
number theory
primitive roots advanced
```

---

# 021. Counting Basics

## Addition Rule

If choices are independent alternatives:

```text
A or B
total = count(A) + count(B)
```

## Multiplication Rule

If choices happen in sequence:

```text
choose shirt then pants
total = shirts * pants
```

## ASCII

```text
Choice 1:
  A B C       3 options

Choice 2:
  X Y         2 options

Total pairs:
AX AY BX BY CX CY = 6
```

## C++ Example

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long shirts, pants;
    cin >> shirts >> pants;

    cout << shirts * pants << "\n";
    return 0;
}
```

---

# 022. nCr Basics

## Mental Model

`nCr` means choose `r` items from `n`, order does not matter.

```text
nCr = n! / (r! * (n-r)!)
```

## ASCII

```text
People: A B C D
Choose 2:

AB AC AD BC BD CD

4C2 = 6
```

## Simple C++ Without Mod

```cpp
#include <bits/stdc++.h>
using namespace std;

long long nCr(int n, int r) {
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

    cout << nCr(n, r) << "\n";
    return 0;
}
```

## Pattern Detector

Use nCr when:

```text
choose positions
select subset of fixed size
grid paths with right/down moves
count ways without order
```

---

# 023. nCr Modulo Prime

## Problem

Compute many `nCr % MOD`.

Use:

```text
fact[n] * invFact[r] * invFact[n-r] % MOD
```

## C++ Template

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;
const ll MOD = 1'000'000'007;
const int MAXN = 1'000'000;

vector<ll> fact(MAXN + 1), invFact(MAXN + 1);

ll binpow(ll a, ll b) {
    ll res = 1;
    a %= MOD;

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

Complexity:

```text
precompute O(MAXN logMOD) if inverse separately
optimized above O(MAXN + logMOD)
query O(1)
```

---

# 024. Permutations With Repetition

## Mental Model

If order matters and repetition is allowed:

```text
n choices each position
length k
total = n^k
```

Example:

```text
digits 0-9, length 4
10^4 possibilities
```

## If Repetition Not Allowed

```text
nPk = n * (n-1) * ... * (n-k+1)
```

## C++ Code

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

---

# 025. Stars and Bars

## Problem Form

Number of non-negative integer solutions:

```text
x1 + x2 + ... + xk = n
```

Answer:

```text
C(n + k - 1, k - 1)
```

## ASCII

Example:

```text
n = 5 stars, k = 3 boxes

** | * | **
x1=2, x2=1, x3=2
```

There are:

```text
5 stars + 2 bars = 7 positions
choose 2 bar positions
C(7,2)
```

## Pattern Detector

Use stars and bars when:

```text
distribute identical items
non-negative integer solutions
balls into boxes
sum of variables fixed
```

---

# 026. Inclusion Exclusion

## Mental Model

When counting union, avoid double-counting overlap.

```text
|A ∪ B| = |A| + |B| - |A ∩ B|
```

ASCII:

```text
   AAAAA
 AAAAA BBBBB
   BBBBB

middle counted twice
subtract once
```

## Count Multiples of a or b

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countMultiples(long long n, long long a, long long b) {
    long long l = lcm(a, b);
    return n / a + n / b - n / l;
}

int main() {
    long long n, a, b;
    cin >> n >> a >> b;

    cout << countMultiples(n, a, b) << "\n";
    return 0;
}
```

## Pattern Detector

Use inclusion-exclusion when:

```text
count items satisfying at least one condition
overlaps exist
multiples of several numbers
not divisible by any
```

---

# 027. Pigeonhole Principle

## Mental Model

If more pigeons than holes, some hole gets at least two pigeons.

```text
n+1 items into n boxes => collision guaranteed
```

## CP Uses

```text
duplicate modulo remainder
prefix sum modulo
guaranteed equal parity
collision arguments
```

## Classic Prefix Modulo

If there are `n` prefix sums modulo `n`, either:

```text
some prefix sum % n = 0
```

or two prefix sums have same remainder, giving subarray sum divisible by `n`.

## ASCII

```text
prefix remainders are pigeons
0..n-1 are holes
collision => subarray divisible by n
```

---

# 028. Catalan Numbers

## Pattern

Catalan numbers count:

```text
valid parentheses
BST shapes
non-crossing chords
Dyck paths
ways to triangulate polygon
```

Formula:

```text
Cat(n) = C(2n, n) / (n + 1)
```

## Parentheses Example

For `n = 3`:

```text
((()))
(()())
(())()
()(())
()()()
```

## C++ With Mod

```cpp
// Catalan(n) = C(2n,n) * inv(n+1) % MOD
```

Use nCr modulo prime template from section 023.

---

# 029. Derangements

## Problem

Count permutations where no element stays in original position.

Known as:

```text
!n
```

Recurrence:

```text
D[0] = 1
D[1] = 0
D[n] = (n-1) * (D[n-1] + D[n-2])
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;
const ll MOD = 1'000'000'007;

int main() {
    int n;
    cin >> n;

    vector<ll> D(n + 2, 0);
    D[0] = 1;
    D[1] = 0;

    for (int i = 2; i <= n; i++) {
        D[i] = (i - 1) * (D[i - 1] + D[i - 2]) % MOD;
    }

    cout << D[n] << "\n";
    return 0;
}
```

## Pattern Detector

Use derangements when:

```text
no one gets own item
secret santa
permutation with forbidden fixed points
```

---

# 030. Probability and Expected Value

## Probability Basics

```text
probability = favorable outcomes / total outcomes
```

## Expected Value

Expected value is average outcome over long run.

Linearity:

```text
E[X + Y] = E[X] + E[Y]
```

Even when events are dependent.

## Dice Example

Expected dice roll:

```text
(1+2+3+4+5+6)/6 = 3.5
```

## C++ Simple Probability

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

## CP Detector

Use expected value when:

```text
average number of steps
random process
expected score
probability DP
linearity of expectation
```

---

# 031. Binary, Bitwise Math, and XOR

## Binary System

```text
13 decimal = 1101 binary
= 8 + 4 + 1
```

## Bit Operations

```text
x & 1       checks odd/even
x >> 1      divide by 2
x << 1      multiply by 2
x & (x-1)   removes lowest set bit
```

## XOR Mental Model

```text
x ^ x = 0
x ^ 0 = x
```

So duplicates cancel.

## Single Number Code

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

## ASCII

```text
a ^ a = 0

pairs vanish:
5 ^ 3 ^ 5 ^ 7 ^ 3 = 7
```

## Pattern Detector

Use XOR when:

```text
pairs cancel
odd occurrence
toggle state
bitmask DP
subset parity
```

---

# 032. Distance, Slope, Line, Area, Orientation

## Distance

Squared distance avoids floating point:

```text
dx² + dy²
```

```cpp
long long dist2(long long x1, long long y1, long long x2, long long y2) {
    long long dx = x1 - x2;
    long long dy = y1 - y2;
    return dx * dx + dy * dy;
}
```

## Slope

Avoid division:

```text
(y2-y1)/(x2-x1)
```

Use cross multiplication to compare slopes.

## Orientation

For points A, B, C:

```text
cross = (B-A) x (C-A)
```

If:

```text
cross > 0 -> counter-clockwise
cross < 0 -> clockwise
cross = 0 -> collinear
```

## C++ Orientation

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Point {
    long long x, y;
};

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

## ASCII

```text
A ---- B
 \ 
  \
   C

cross tells which side C lies on.
```

---

# 033. CRT and Diophantine Equations

## Diophantine Equation

Form:

```text
ax + by = c
```

Solution exists if:

```text
gcd(a,b) divides c
```

## Extended GCD

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

    return 0;
}
```

## CRT Mental Model

Find number satisfying multiple remainders:

```text
x ≡ a1 mod m1
x ≡ a2 mod m2
```

Useful when combining cycles.

## Pattern Detector

Use CRT when:

```text
multiple modulo constraints
same unknown x
cycle alignment
remainders under different moduli
```

---

# 034. Floor Sum

## Problem Pattern

Expressions like:

```text
sum floor((a*i + b) / m) for i = 0..n-1
```

Appear in advanced number theory and counting lattice points.

## Beginner Mental Model

Floor groups ranges.

```text
floor(x / k)
```

stays constant for intervals of `x`.

ASCII:

```text
x:        0 1 2 3 4 5 6 7 8
floor(x/3):0 0 0 1 1 1 2 2 2
```

## Simpler O(N) Version

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long n, a, b, m;
    cin >> n >> a >> b >> m;

    long long ans = 0;

    for (long long i = 0; i < n; i++) {
        ans += (a * i + b) / m;
    }

    cout << ans << "\n";
    return 0;
}
```

For huge values, use optimized floor-sum algorithms, but first master the grouping idea.

---

# 035. CP Math Pattern Decision Engine

## Master Decision Tree

```text
Problem says "huge answer modulo MOD"?
    -> modulo arithmetic, nCr mod, DP mod

Problem has repeated multiplication or huge exponent?
    -> binary exponentiation

Problem has divisibility/common factor?
    -> gcd/lcm/prime factorization

Problem has count arrangements?
    -> permutation/combination/stars and bars

Problem has "at least one" / "not divisible by"?
    -> inclusion-exclusion

Problem has cycles/remainders?
    -> modulo cycle / CRT

Problem has parity/impossible?
    -> parity/invariant

Problem has coordinates?
    -> geometry: distance/cross/orientation

Problem has random/average?
    -> probability/expected value
```

## ASCII Pattern Router

```text
                 MATH PROBLEM
                      |
     -------------------------------------
     |          |          |             |
  modulo     divisors    counting     geometry
     |          |          |             |
 cycles      gcd/lcm     nCr/stars    cross/distance
 inverse     primes      catalan      orientation
```

## CM Contest Habit

Before coding, say:

```text
What is the mathematical object here?

remainder?
factor?
choice?
cycle?
invariant?
coordinate?
```

---

# 036. CF and FAANG Math Problem Ladder

## Codeforces Ladder

```text
Div2 A:
    parity, formula, min/max, simple modulo

Div2 B:
    gcd/lcm, sorting + math, floor/ceil, counting

Div2 C:
    prefix modulo, binary exponentiation, combinatorics, invariant

Div2 D:
    sieve, prime factorization, DP + math, inclusion-exclusion

Div2 E / CM:
    CRT, phi, floor sum, advanced combinatorics, geometry
```

## FAANG Ladder

```text
Easy:
    parity, digit math, reverse integer, palindrome number

Medium:
    pow(x,n), divide integers, product except self, random pick by weight

Hard:
    number of ways, combinatorics with mod, geometry, probability DP
```

## Training Order

```text
1. parity / floor / ceil
2. gcd / lcm
3. modulo basics
4. binary exponentiation
5. primes / sieve
6. factorization / divisors
7. nCr / combinatorics
8. inclusion-exclusion
9. geometry basics
10. CRT / phi / floor sum
```

---

# 037. Common Math Traps

## Trap 1: Negative Modulo

```cpp
(-2) % 7 == -2 in C++
```

Fix:

```cpp
((x % MOD) + MOD) % MOD
```

## Trap 2: Overflow Before Mod

Bad:

```cpp
long long x = a * b % MOD;
```

If `a` and `b` are huge, multiplication can overflow.

Use `__int128` if needed.

```cpp
long long mulMod(long long a, long long b, long long mod) {
    return (__int128)a * b % mod;
}
```

## Trap 3: Wrong Ceil

Bad:

```cpp
ceil(a / b)
```

because `a / b` already integer-divided.

Good for positive:

```cpp
(a + b - 1) / b
```

## Trap 4: Direct Modular Division

Bad:

```cpp
a / b % MOD
```

Good:

```cpp
a * inverse(b) % MOD
```

## Trap 5: Floating Point Geometry

Avoid comparing doubles when integer cross product works.

## Trap 6: Forgetting `long long`

Use `long long` for:

```text
products
sums
coordinates
n up to 1e9 or 1e18
```

---

# 099. Final Cheat Sheet

## Core Formulas

```text
ceil(a/b) for positive       = (a+b-1)/b
count multiples in [L,R]     = R/k - (L-1)/k
gcd(a,b)                     = gcd(b, a%b)
lcm(a,b)                     = a/gcd(a,b)*b
nCr                          = n!/(r!(n-r)!)
stars and bars               = C(n+k-1, k-1)
Catalan                      = C(2n,n)/(n+1)
Derangement                  = D[n]=(n-1)(D[n-1]+D[n-2])
Euler phi                    = n * Π(1 - 1/p)
orientation                  = cross(B-A, C-A)
```

## Pattern Recognition

```text
Huge answer                  -> modulo
Repeated multiplication       -> binary exponentiation
Need division under mod        -> modular inverse
Common divisor                 -> gcd
Align cycles                   -> lcm / CRT
Prime-related                  -> sieve / SPF / factorization
Count choices                  -> nCr / permutation
Distribute identical objects   -> stars and bars
At least one condition         -> inclusion-exclusion
Guaranteed collision           -> pigeonhole
No fixed point                 -> derangement
Valid parentheses              -> Catalan
Random average                 -> expected value
Pairs cancel                   -> XOR
Coordinates                   -> distance/cross/orientation
```

## C++ Master Math Template

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

ll modAdd(ll a, ll b) {
    return (norm(a) + norm(b)) % MOD;
}

ll modSub(ll a, ll b) {
    return (norm(a) - norm(b) + MOD) % MOD;
}

ll modMul(ll a, ll b) {
    return norm(a) * norm(b) % MOD;
}

ll binpow(ll a, ll b) {
    a = norm(a);
    ll res = 1;

    while (b > 0) {
        if (b & 1) res = res * a % MOD;
        a = a * a % MOD;
        b >>= 1;
    }

    return res;
}

ll modInv(ll x) {
    return binpow(x, MOD - 2);
}

ll gcdll(ll a, ll b) {
    while (b) {
        ll r = a % b;
        a = b;
        b = r;
    }
    return abs(a);
}

ll lcmll(ll a, ll b) {
    return a / gcdll(a, b) * b;
}

ll ceilDiv(ll a, ll b) {
    return (a + b - 1) / b;
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
What structure?
    |
    +-- remainder?     -> modulo / cycles / inverse
    +-- factor?        -> gcd / lcm / primes
    +-- choice?        -> nCr / permutation / stars-bars
    +-- impossible?    -> parity / invariant
    +-- coordinate?    -> geometry
    +-- average?       -> probability / expected value
    +-- repeated huge? -> binary exponentiation
```

Start with the structure.

Then choose the formula.

Then code safely.
