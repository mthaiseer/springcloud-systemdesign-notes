# MiniMathEngine_Master.md

## Complete CP/DSA Math Engine for Codeforces CM + FAANG Interviews

> **Scope:** All topics from the combined math engine, upgraded into a single high-signal Markdown guide.  
> **Style:** C++17 only, CP/DSA only, beginner-friendly but deep enough for Codeforces Candidate Master preparation.  
> **Core mental model:** Math in CP is not formula memorization. It is **structure recognition**.

---

# Clickable Index

- [000. How To Use This File](#000-how-to-use-this-file)
- [001. The Single Core Mental Model](#001-the-single-core-mental-model)
- [002. Constraint → Math Pattern Router](#002-constraint--math-pattern-router)
- [003. Number Line Thinking](#003-number-line-thinking)
- [004. Algebraic Rearrangement](#004-algebraic-rearrangement)
- [005. Min Max Bounds](#005-min-max-bounds)
- [006. Absolute Value](#006-absolute-value)
- [007. Powers and Logarithms](#007-powers-and-logarithms)
- [008. Floor, Ceil, and Integer Division](#008-floor-ceil-and-integer-division)
- [009. Parity](#009-parity)
- [010. Invariants](#010-invariants)
- [011. Divisibility, GCD, and LCM](#011-divisibility-gcd-and-lcm)
- [012. Modulo Arithmetic](#012-modulo-arithmetic)
- [013. Modulo Cycles](#013-modulo-cycles)
- [014. Binary Exponentiation](#014-binary-exponentiation)
- [015. Modular Inverse](#015-modular-inverse)
- [016. Prime Checking](#016-prime-checking)
- [017. Sieve and SPF](#017-sieve-and-spf)
- [018. Prime Factorization](#018-prime-factorization)
- [019. Divisor Count and Divisor Sum](#019-divisor-count-and-divisor-sum)
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
- [031. Binary, Bitwise Math, and XOR](#031-binary-bitwise-math-and-xor)
- [032. Geometry: Distance, Area, Orientation](#032-geometry-distance-area-orientation)
- [033. Extended Euclid, Diophantine, CRT](#033-extended-euclid-diophantine-crt)
- [034. Floor Sum Thinking](#034-floor-sum-thinking)
- [035. Codeforces Math Forms](#035-codeforces-math-forms)
- [036. FAANG Math Forms](#036-faang-math-forms)
- [037. Math Bug Encyclopedia](#037-math-bug-encyclopedia)
- [099. Final Cheat Sheet](#099-final-cheat-sheet)

---

# 000. How To Use This File

This is a **pattern engine**, not a school textbook.

Use it in 4 passes:

```text
Pass 1:
    Read mental models and ASCII diagrams.

Pass 2:
    Type the C++17 templates.

Pass 3:
    For each solved problem, tag the hidden math structure:
        parity / gcd / modulo / nCr / prime / invariant / geometry

Pass 4:
    During contests, start from constraints and pattern recognition.
```

## The Training Rule

```text
Do not ask:
    "Which formula do I remember?"

Ask:
    "What structure is hidden in the problem?"
```

---

# 001. The Single Core Mental Model

Every math problem in CP reduces to one question:

> **What structure stays true when input becomes too large to brute force?**

```text
Huge input
    ↓
Brute force impossible
    ↓
Find mathematical structure
    ↓
Compress work into formula / log / precompute / invariant
```

## The Math Structure Map

```text
                    CP MATH ENGINE
                          |
    ----------------------------------------------------
    |          |          |          |        |         |
 Remainder   Factor     Choice    Position  State    Shape
    |          |          |          |        |         |
 modulo      gcd        nCr       number    invariant geometry
 cycles      lcm        perm      line      parity    cross
 inverse     primes     stars     abs       xor       area
 CRT         phi        catalan   distance  cycle     slope
```

## Contest Translation

```text
N <= 1e18        -> formula / log / number theory
Answer huge      -> modulo
Operations repeat-> invariant / cycle
Common divisor   -> gcd/lcm
Choose objects   -> combinatorics
Coordinates      -> geometry
Random process   -> probability / expectation
```

---

# 002. Constraint → Math Pattern Router

Before coding, route the problem.

```text
Read constraints
      |
      v
Is N huge? 1e9 / 1e18?
      | yes
      v
Need O(logN), O(sqrtN), or O(1)
      |
      +--> binary exponentiation
      +--> gcd / factorization
      +--> formula
      +--> modulo cycle
```

## Router Diagram

```text
Problem Statement
      |
      v
What is repeated / counted / preserved?
      |
      +-- repeated multiplication?  -> binary exponentiation
      +-- remainder/circle?         -> modulo
      +-- common divisor?           -> gcd/lcm
      +-- prime/divisor?            -> sieve/factorization
      +-- choose ways?              -> nCr/combinatorics
      +-- impossible transform?     -> invariant/parity
      +-- coordinate?               -> geometry
      +-- random average?           -> expected value
```

## Recognition Checklist

```text
[ ] Is answer too large? Use modulo.
[ ] Does operation repeat many times? Look for cycle/invariant.
[ ] Are there divisibility words? Use gcd/lcm/factors.
[ ] Are there choices/ways? Use combinatorics.
[ ] Are there positions/distances? Use number line/geometry.
[ ] Are there constraints up to 1e18? Avoid O(N).
```


# 003. Number Line Thinking

## Why This Exists

Number line thinking turns abstract integers into positions. Many CP problems are really about distance, order, interval overlap, and closest/farthest points.

## Core Mental Model

```text
<---- negative ---- 0 ---- positive ---->
-5 -4 -3 -2 -1 0 1 2 3 4 5
```

Distance:

```text
distance(a,b) = |a-b|
```

## Pattern Recognition

Use this when you see:

```text
minimum distance
closest value
range overlap
median
binary search on answer
absolute difference
```

## Sub-Patterns

```text
closest point          -> sort + scan / binary search
minimize absolute sum  -> median
minimize maximum       -> binary search on answer
range intersection     -> max(left), min(right)
```

## ASCII Diagram

```text
target = 10

0----3------7---10----13--------20
     |      |    |     |         |
   dist7  dist3 0   dist3      dist10
```

## C++17: Minimum Distance To Target

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

## Dry Run

```text
a = [3, 7, 13, 20], target = 10

x=3  -> |3-10|=7  best=7
x=7  -> |7-10|=3  best=3
x=13 -> |13-10|=3 best=3
x=20 -> |20-10|=10 best=3
```

## Complexity

```text
Time  O(N)
Space O(1) extra
```

## Bugs

```text
abs(int_min) overflow risk
use long long for coordinates up to 1e9/1e18
```

---

# 004. Algebraic Rearrangement

## Why This Exists

Algebra converts a slow search into a direct lookup.

## Core Mental Model

```text
Original condition:
a + b = X

Rearrange:
b = X - a
```

Now you can store seen values and check complements.

## Pattern Recognition

Use when you see:

```text
two values satisfy equation
prefix[j] - prefix[i] = k
a + b = x
a - b = x
a * b = x
```

## Brute Force

```text
Try all pairs -> O(N²)
```

## Optimal Idea

```text
Store what you have seen.
Ask: do I already have the missing value?
```

## C++17: Pair Sum

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

        int need = X - x;

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

## Dry Run

```text
X = 10
array = [3, 8, 4, 7]

x=3 need=7 seen={}      no, insert 3
x=8 need=2 seen={3}     no, insert 8
x=4 need=6 seen={3,8}   no, insert 4
x=7 need=3 seen={3,8,4} yes
```

## Complexity

```text
Average Time O(N)
Space O(N)
```

## Common Mistakes

```text
checking after insert can match same element when X=2*x
using vector find gives O(N²)
overflow in X-x if values huge
```

---

# 005. Min Max Bounds

## Why This Exists

Many answers are unknown but bounded. Bounds create binary search on answer.

## Core Mental Model

```text
Answer is hidden inside [LOW, HIGH]
```

Example:

```text
Minimum ship capacity:
LOW  = max single package
HIGH = sum of all packages
```

## Pattern Recognition

Use bounds when problem asks:

```text
minimize maximum
maximize minimum
smallest possible X
largest possible X
can we do within K?
```

## ASCII Diagram

```text
Capacity:
[ impossible impossible ][ possible possible possible ]
          low     mid                  high

We binary search first possible.
```

## C++17: Compute Bounds

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

    cout << mx << " " << sum << "\n";
    return 0;
}
```

## Dry Run

```text
weights = [3,2,7,4]

LOW=max=7
HIGH=sum=16
answer must be between 7 and 16
```

## Related Patterns

```text
binary search on answer
greedy feasibility
load balancing
partition array
aggressive cows
```

---

# 006. Absolute Value

## Why This Exists

Absolute value measures distance without direction.

## Core Mental Model

```text
|a-b| = distance between positions a and b
```

## Pattern Recognition

Use when:

```text
minimize distance
closest point
median
pair difference
Manhattan distance
```

## Key Insight: Median

To minimize sum of absolute distances on a line, choose the median.

```text
points: 1 2 10 12 13
median = 10
```

ASCII:

```text
1---2--------10--12--13
             ^
          best meeting point
```

## C++17: Minimum Total Absolute Distance

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

## Dry Run

```text
a = [1,2,10,12,13]
median = 10

|1-10|=9
|2-10|=8
|10-10|=0
|12-10|=2
|13-10|=3
total=22
```

## Bugs

```text
use llabs for long long
sort needed before median
for even n, either middle median works for minimum sum
```

---

# 007. Powers and Logarithms

## Why This Exists

Powers grow fast; logarithms count how many times you can divide.

## Core Mental Model

```text
2^10 ≈ 1e3
2^20 ≈ 1e6
2^30 ≈ 1e9
2^60 ≈ 1e18
```

So `O(log N)` for `N=1e18` is only about 60 steps.

## ASCII Diagram

```text
N = 64

64 -> 32 -> 16 -> 8 -> 4 -> 2 -> 1
          6 halving steps
```

## Pattern Recognition

Use when:

```text
huge exponent
divide by two
powers of two
binary representation
repeated squaring
```

## C++17: Power Of Two Check

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long x;
    cin >> x;

    bool ok = (x > 0 && (x & (x - 1)) == 0);

    cout << (ok ? "YES\n" : "NO\n");
    return 0;
}
```

## Dry Run

```text
x = 8  = 1000
x-1=7  = 0111
x&(x-1)=0000 -> power of two

x = 10 = 1010
x-1=9  = 1001
x&(x-1)=1000 -> not zero
```

---

# 008. Floor, Ceil, and Integer Division

## Why This Exists

Many CP formulas depend on grouping numbers into blocks.

## Core Mental Model

```text
floor(a/b) = complete groups of size b inside a
ceil(a/b)  = groups needed to cover a
```

## ASCII

```text
a = 10 items, b = 3 per box

[xxx][xxx][xxx][x]
floor = 3 full boxes
ceil  = 4 boxes needed
```

## Formulas For Positive Integers

```text
floor(a/b) = a / b
ceil(a/b)  = (a + b - 1) / b
```

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

long long ceilDiv(long long a, long long b) {
    return (a + b - 1) / b;
}

int main() {
    long long a, b;
    cin >> a >> b;

    cout << a / b << "\n";
    cout << ceilDiv(a, b) << "\n";
    return 0;
}
```

## Count Multiples In [L, R]

```text
multiples up to R = floor(R/k)
multiples before L = floor((L-1)/k)
answer = R/k - (L-1)/k
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

## Bugs

```text
ceil(a / b) is wrong because a/b already truncates
negative division in C++ truncates toward zero
a+b-1 may overflow if a,b near 1e18
```

---

# 009. Parity

## Why This Exists

Parity reduces many problems to two states: odd or even.

## Core Mental Model

```text
even = 0
odd  = 1
```

## Rules

```text
even + even = even
even + odd  = odd
odd + odd   = even

even * anything = even
odd * odd       = odd
```

## ASCII Checkerboard

```text
(row+col)%2

E O E O
O E O E
E O E O
O E O E
```

## Pattern Recognition

Use when:

```text
operation changes by 2
checkerboard
alternating turns
odd/even count
impossible transformation
sum parity
```

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long x;
    cin >> x;

    cout << (x % 2 == 0 ? "EVEN\n" : "ODD\n");
    return 0;
}
```

## Dry Run

```text
Operation: add 2
5 -> 7 -> 9 -> 11
odd remains odd

So cannot reach even number from odd using +2.
```

---

# 010. Invariants

## Why This Exists

Some problems are impossible not because you cannot find the sequence, but because a hidden property never changes.

## Core Mental Model

```text
Operation happens
    ↓
Something stays unchanged
```

## Common Invariants

```text
parity
sum
gcd
xor
modulo class
color of checkerboard cell
relative order
```

## ASCII

```text
State A --operation--> State B --operation--> State C

Invariant value:
   1                  1                  1
```

## Pattern Recognition

Use when:

```text
can we transform?
is it possible?
repeated operation
game state
grid coloring
```

## C++17: Parity Invariant

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long a, b;
    cin >> a >> b;

    if (((a - b) % 2 + 2) % 2 == 0) {
        cout << "POSSIBLE\n";
    } else {
        cout << "IMPOSSIBLE\n";
    }

    return 0;
}
```

## Dry Run

```text
Allowed operation: add/subtract 2

a=5, b=11
difference=6 even -> possible

a=5, b=10
difference=5 odd -> impossible
```

---

# 011. Divisibility, GCD, and LCM

## Why This Exists

GCD/LCM solve problems about common structure: shared divisors and aligned cycles.

## Core Mental Model

```text
GCD = biggest common block size
LCM = first common meeting time
```

## ASCII: GCD As Blocks

```text
24 items: [******][******][******][******]
36 items: [******][******][******][******][******][******]

largest equal block = 6
gcd(24,36)=12? Wait blocks of 12 also:
24: [************][************]
36: [************][************][************]

largest = 12
```

## Euclid Algorithm

```text
gcd(a,b) = gcd(b, a%b)
```

Dry run:

```text
gcd(48,18)
48 % 18 = 12 -> gcd(18,12)
18 % 12 = 6  -> gcd(12,6)
12 % 6 = 0   -> answer 6
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

## Pattern Recognition

```text
common divisor
make all numbers equal by division
period alignment
two blinking lights meeting
array gcd
minimum common multiple
```

## Bugs

```text
a*b/gcd can overflow; use a/gcd*b
gcd(0,x)=x
lcm with zero needs care
```

---

# 012. Modulo Arithmetic

## Why This Exists

Modulo keeps numbers small and reveals cycles.

## Core Mental Model

Modulo is clock arithmetic.

```text
MOD = 7

0 -> 1 -> 2 -> 3 -> 4 -> 5 -> 6
^                              |
|______________________________|
```

## Rules

```text
(a+b)%m = ((a%m)+(b%m))%m
(a-b)%m = ((a%m)-(b%m)+m)%m
(a*b)%m = ((a%m)*(b%m))%m
```

Division is not direct.

## Pattern Recognition

Use modulo when:

```text
answer huge
output mod 1e9+7
cyclic behavior
remainder classes
clock/circular array
prefix sum divisible by k
```

## C++17 Toolkit

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
    return norm(a) * norm(b) % MOD;
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

## Dry Run

```text
MOD=7
a=5, b=6

a+b=11 -> 11%7=4
a-b=-1 -> normalized to 6
a*b=30 -> 30%7=2
```

## Bugs

```text
negative modulo in C++ stays negative
overflow before modulo
modular division needs inverse
```

---

# 013. Modulo Cycles

## Why This Exists

Repeated modulo states eventually repeat.

## Core Mental Model

Finite states + repeated transition = cycle.

```text
state -> state -> state -> repeated state
```

## ASCII: Last Digit of Powers of 2

```text
2 -> 4 -> 8 -> 6
^              |
|______________|
```

## Pattern Recognition

Use when:

```text
huge exponent
repeated operation
small state space
clock movement
circular simulation
```

## C++17 Cycle Detection

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long base, mod;
    cin >> base >> mod;

    vector<int> seen(mod, -1);
    vector<long long> values;

    long long cur = 1;
    int step = 0;

    while (seen[cur] == -1) {
        seen[cur] = step++;
        values.push_back(cur);
        cur = (cur * base) % mod;
    }

    cout << "cycle starts at " << seen[cur] << "\n";
    for (long long x : values) cout << x << " ";
    cout << "\n";

    return 0;
}
```

## Dry Run

```text
base=2, mod=10

cur=1
2
4
8
6
2 repeated -> cycle [2,4,8,6]
```

---

# 014. Binary Exponentiation

## Why This Exists

Computing `a^b` by multiplying `b` times is too slow for `b=1e18`.

## Core Mental Model

Use binary representation.

```text
13 = 8 + 4 + 1
a^13 = a^8 * a^4 * a
```

## ASCII

```text
b = 13 = 1101₂

bit 0 -> a^1  used
bit 1 -> a^2  not used
bit 2 -> a^4  used
bit 3 -> a^8  used
```

## C++17

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
            res = res * a % MOD;
        }

        a = a * a % MOD;
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

## Variable Table

For `2^13 mod 100`:

```text
+---+----+-----+-----+--------+
| b |bit | res | a   | action |
+---+----+-----+-----+--------+
|13 | 1  | 2   | 4   | take   |
|6  | 0  | 2   | 16  | skip   |
|3  | 1  | 32  | 56  | take   |
|1  | 1  | 92  | 36  | take   |
+---+----+-----+-----+--------+
```

## Complexity

```text
O(log b)
```

---

# 015. Modular Inverse

## Why This Exists

Division under modulo is not normal division.

## Core Mental Model

```text
a / b mod M = a * inverse(b) mod M
```

When `M` is prime:

```text
inverse(b) = b^(M-2) mod M
```

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;
const ll MOD = 1'000'000'007;

ll binpow(ll a, ll b) {
    ll res = 1;
    a %= MOD;

    while (b) {
        if (b & 1) res = res * a % MOD;
        a = a * a % MOD;
        b >>= 1;
    }

    return res;
}

ll inv(ll x) {
    return binpow(x, MOD - 2);
}

int main() {
    ll a, b;
    cin >> a >> b;

    cout << a % MOD * inv(b) % MOD << "\n";
    return 0;
}
```

## Recognition

```text
nCr modulo prime
probability modulo
division with MOD
fraction under modulo
```

## Bugs

```text
inverse exists only when gcd(b,MOD)=1
for non-prime MOD, Fermat inverse may fail
b cannot be 0 mod MOD
```

---

# 016. Prime Checking

## Why This Exists

Prime numbers are building blocks of divisibility.

## Core Mental Model

If `n = a*b`, one factor is at most `sqrt(n)`.

```text
If a > sqrt(n) and b > sqrt(n),
then a*b > n, impossible.
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

## Dry Run

```text
n=29
try d=2,3,4,5
none divides
5*5<=29, next 6*6>29
prime
```

## Complexity

```text
O(sqrtN)
```

---

# 017. Sieve and SPF

## Why This Exists

If many prime queries exist, checking each number separately is too slow.

## Core Mental Model

A composite number has a prime that marks it.

```text
2 marks 4,6,8,10...
3 marks 9,12,15...
5 marks 25,30...
```

## Sieve C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<bool> prime(n + 1, true);
    prime[0] = prime[1] = false;

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

## SPF C++17

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

## Complexity

```text
sieve O(N log log N)
SPF preprocessing O(N log log N)
factorization using SPF O(logN-ish)
```

---

# 018. Prime Factorization

## Why This Exists

Many divisor/counting problems become easy after factorization.

## Core Mental Model

```text
n = p1^a1 * p2^a2 * ... * pk^ak
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

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<pair<long long,int>> factorize(long long n) {
    vector<pair<long long,int>> f;

    for (long long p = 2; p * p <= n; p++) {
        if (n % p == 0) {
            int cnt = 0;

            while (n % p == 0) {
                n /= p;
                cnt++;
            }

            f.push_back({p, cnt});
        }
    }

    if (n > 1) f.push_back({n, 1});
    return f;
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

## Bugs

```text
use p*p<=n with long long carefully
if remaining n>1, it is prime
trial division too slow for many large queries
```

---

# 019. Divisor Count and Divisor Sum

## Why This Exists

Once factorized, divisors are combinations of prime powers.

## Core Mental Model

If:

```text
n = 2^2 * 3^1 * 5^1
```

A divisor chooses exponent:

```text
2 exponent: 0,1,2 -> 3 choices
3 exponent: 0,1   -> 2 choices
5 exponent: 0,1   -> 2 choices
```

Total:

```text
3*2*2 = 12 divisors
```

## Formula

```text
count divisors = Π(ai + 1)
sum divisors = Π(1 + p + p² + ... + p^a)
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
            f.push_back({p,e});
        }
    }

    if (n > 1) f.push_back({n,1});
    return f;
}

int main() {
    ll n;
    cin >> n;

    ll cnt = 1;
    ll sum = 1;

    for (auto [p,e] : factorize(n)) {
        cnt *= (e + 1);

        ll term = 1;
        ll power = 1;
        for (int i = 1; i <= e; i++) {
            power *= p;
            term += power;
        }

        sum *= term;
    }

    cout << cnt << "\n" << sum << "\n";
    return 0;
}
```

---

# 020. Euler Phi

## Why This Exists

Phi counts coprime numbers and appears in modular number theory.

## Core Mental Model

```text
phi(n) = count of x in [1,n] such that gcd(x,n)=1
```

Formula:

```text
phi(n) = n * Π(1 - 1/p)
```

for distinct prime factors `p`.

## C++17

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

## Dry Run

```text
n=10
prime factors: 2,5

ans=10
remove multiples of 2: ans=10-5=5
remove multiples of 5: ans=5-1=4

coprime: 1,3,7,9
```

---

# 021. Counting Principles

## Why This Exists

Counting is the base of combinatorics.

## Core Mental Model

```text
OR  -> add
AND -> multiply
```

## ASCII

```text
Choose shirt:  A B C    3 choices
Choose pants:  X Y      2 choices

AX AY
BX BY
CX CY

total = 3*2 = 6
```

## C++17

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

## Pattern Recognition

```text
independent stages -> multiply
alternative cases  -> add
avoid overcount     -> subtract overlap
```

---

# 022. nCr Combinations

## Why This Exists

`nCr` counts selections where order does not matter.

## Core Mental Model

```text
Choose r positions/items from n.
```

## ASCII

```text
A B C D

choose 2:
AB AC AD BC BD CD

4C2 = 6
```

## C++17 Without Mod

```cpp
#include <bits/stdc++.h>
using namespace std;

long long C(int n, int r) {
    if (r < 0 || r > n) return 0;
    r = min(r, n-r);

    long long ans = 1;

    for (int i = 1; i <= r; i++) {
        ans = ans * (n-r+i) / i;
    }

    return ans;
}

int main() {
    int n, r;
    cin >> n >> r;

    cout << C(n,r) << "\n";
    return 0;
}
```

## Recognition

```text
choose positions
unordered selection
grid path with R/D moves
pick k elements
```

---

# 023. nCr Modulo Prime

## Why This Exists

Combinatorics answers are huge and commonly required modulo `1e9+7`.

## Core Mental Model

```text
C(n,r) = fact[n] * invFact[r] * invFact[n-r]
```

## C++17 Template

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;
const ll MOD = 1'000'000'007;
const int MAXN = 1'000'000;

vector<ll> fact(MAXN+1), invFact(MAXN+1);

ll binpow(ll a, ll b) {
    ll res = 1;
    a %= MOD;

    while (b) {
        if (b & 1) res = res * a % MOD;
        a = a * a % MOD;
        b >>= 1;
    }

    return res;
}

void build() {
    fact[0] = 1;

    for (int i = 1; i <= MAXN; i++) {
        fact[i] = fact[i-1] * i % MOD;
    }

    invFact[MAXN] = binpow(fact[MAXN], MOD-2);

    for (int i = MAXN-1; i >= 0; i--) {
        invFact[i] = invFact[i+1] * (i+1) % MOD;
    }
}

ll C(int n, int r) {
    if (r < 0 || r > n) return 0;
    return fact[n] * invFact[r] % MOD * invFact[n-r] % MOD;
}

int main() {
    build();

    int n, r;
    cin >> n >> r;

    cout << C(n,r) << "\n";
    return 0;
}
```

## Complexity

```text
Precompute O(MAXN + logMOD)
Query O(1)
```

## Bugs

```text
MAXN must cover max n
MOD must be prime for Fermat inverse
negative r or r>n returns 0
```

---

# 024. Permutations

## Why This Exists

Permutations count ordered arrangements.

## Core Mental Model

```text
order matters
```

Without repetition:

```text
nPk = n * (n-1) * ... * (n-k+1)
```

With repetition:

```text
n^k
```

## ASCII

```text
Digits 0..9, length 3, repetition allowed:
10 * 10 * 10 = 1000
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

    cout << permNoRepeat(n,k) << "\n";
    return 0;
}
```

## Recognition

```text
order matters
arrange people
assign distinct objects
passwords/codes
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

## ASCII

```text
n = 5 stars, k = 3 boxes

** | * | **
x1=2, x2=1, x3=2

5 stars + 2 bars = 7 positions
choose bar positions = C(7,2)
```

## Pattern Recognition

```text
non-negative integer solutions
identical balls into boxes
distribute candies
sum fixed
```

## Common Variant

For positive integers:

```text
x1 + ... + xk = n, xi >= 1
```

Set:

```text
yi = xi - 1
```

Then:

```text
y1+...+yk = n-k
answer = C(n-1, k-1)
```

---

# 026. Inclusion Exclusion

## Why This Exists

When counting items satisfying at least one condition, overlaps get counted multiple times.

## Core Mental Model

```text
A or B = A + B - both
```

## ASCII

```text
      AAAAA
   AAAAA BBBBB
      BBBBB

middle overlap counted twice
subtract once
```

## C++17: Multiples of A or B

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countMultiples(long long n, long long a, long long b) {
    long long g = gcd(a,b);
    long long l = a / g * b;
    return n/a + n/b - n/l;
}

int main() {
    long long n, a, b;
    cin >> n >> a >> b;

    cout << countMultiples(n,a,b) << "\n";
    return 0;
}
```

## Dry Run

```text
n=10, a=2, b=3
multiples of 2: 2,4,6,8,10 -> 5
multiples of 3: 3,6,9 -> 3
overlap multiples of 6: 6 -> 1
answer=5+3-1=7
```

---

# 027. Pigeonhole Principle

## Why This Exists

Pigeonhole proves existence without constructing.

## Core Mental Model

```text
More objects than boxes => at least one box has 2+
```

## ASCII

```text
Pigeons:  P P P P
Holes:    [ ] [ ] [ ]

4 pigeons, 3 holes
some hole must contain at least 2
```

## CP Pattern

Prefix sums modulo `n`.

```text
If two prefix sums have same remainder,
their difference is divisible by n.
```

## Recognition

```text
guaranteed duplicate
modulo remainders
large set into small states
collision argument
```

---

# 028. Catalan Numbers

## Why This Exists

Catalan counts recursively balanced structures.

## Core Mental Model

```text
valid nested structure
```

Examples:

```text
valid parentheses
BST shapes
non-crossing chords
Dyck paths
```

## Formula

```text
Catalan(n) = C(2n,n)/(n+1)
```

## ASCII Parentheses

```text
n=3

((()))
(()())
(())()
()(())
()()()
```

## Recognition

```text
balanced parentheses
number of BSTs
non-crossing pairings
never go below zero path
```

---

# 029. Derangements

## Why This Exists

Derangements count permutations with no fixed point.

## Core Mental Model

```text
Nobody gets own item.
```

## Recurrence

```text
D[0]=1
D[1]=0
D[n]=(n-1)(D[n-1]+D[n-2])
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

    vector<ll> D(n+2);
    D[0] = 1;
    D[1] = 0;

    for (int i = 2; i <= n; i++) {
        D[i] = (i-1) * (D[i-1] + D[i-2]) % MOD;
    }

    cout << D[n] << "\n";
    return 0;
}
```

## Recognition

```text
secret santa
no fixed position
no one receives own object
permutation with forbidden self-match
```

---

# 030. Probability and Expected Value

## Why This Exists

Probability solves random process problems; expected value computes average outcome.

## Core Mental Model

```text
Probability = favorable / total
Expectation = weighted average
```

Linearity:

```text
E[X+Y] = E[X] + E[Y]
```

## ASCII Dice

```text
1 2 3 4 5 6
average = (1+2+3+4+5+6)/6 = 3.5
```

## C++17

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
expected number of steps
average score
probability DP
linearity of expectation
```

---

# 031. Binary, Bitwise Math, and XOR

## Why This Exists

Bitwise math compresses sets/states and detects parity/cancellation.

## Core Mental Model

```text
binary = powers of two
xor = pair cancellation
```

## XOR Rules

```text
x ^ x = 0
x ^ 0 = x
```

## ASCII

```text
5 ^ 3 ^ 5 ^ 7 ^ 3
(5^5) ^ (3^3) ^ 7
0 ^ 0 ^ 7 = 7
```

## C++17: Single Number

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

## Recognition

```text
pairs cancel
odd occurrence
toggle state
subset mask
parity of bits
```

---

# 032. Geometry: Distance, Area, Orientation

## Why This Exists

Geometry problems often fail because of floating-point errors. Integer geometry avoids that.

## Core Mental Model

```text
Use squared distance and cross product.
Avoid division when possible.
```

## Distance

```cpp
long long dist2(long long x1, long long y1, long long x2, long long y2) {
    long long dx = x1 - x2;
    long long dy = y1 - y2;
    return dx * dx + dy * dy;
}
```

## Orientation

```text
cross(B-A, C-A)
>0 counter-clockwise
<0 clockwise
=0 collinear
```

## ASCII

```text
A ---- B
 \
  \
   C

cross tells which side C lies on.
```

## C++17

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
    return x1*y2 - y1*x2;
}

int main() {
    Point a, b, c;
    cin >> a.x >> a.y >> b.x >> b.y >> c.x >> c.y;

    long long v = cross(a,b,c);

    if (v > 0) cout << "CCW\n";
    else if (v < 0) cout << "CW\n";
    else cout << "COLLINEAR\n";

    return 0;
}
```

## Bugs

```text
avoid double equality
use long long for coordinates
cross product can overflow if coordinates huge; use __int128 if needed
```

---

# 033. Extended Euclid, Diophantine, CRT

## Why This Exists

Some equations need integer solutions and modular merging.

## Core Mental Model

```text
ax + by = gcd(a,b)
```

Extended Euclid finds `x,y`.

## C++17 Extended GCD

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
    long long g = extgcd(b, a%b, x1, y1);

    x = y1;
    y = x1 - (a/b) * y1;

    return g;
}

int main() {
    long long a, b;
    cin >> a >> b;

    long long x, y;
    long long g = extgcd(a,b,x,y);

    cout << g << "\n";
    cout << x << " " << y << "\n";
    return 0;
}
```

## Diophantine Recognition

```text
ax + by = c
solution exists if gcd(a,b) divides c
```

## CRT Recognition

```text
x ≡ a1 mod m1
x ≡ a2 mod m2
```

Use when merging remainder constraints or aligning cycles.

---

# 034. Floor Sum Thinking

## Why This Exists

Floor expressions appear in lattice counting and advanced number theory.

## Core Mental Model

`floor(x/k)` stays constant over blocks.

```text
x:          0 1 2 3 4 5 6 7 8
floor(x/3):0 0 0 1 1 1 2 2 2
```

## Basic C++17 O(N)

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long n, a, b, m;
    cin >> n >> a >> b >> m;

    long long ans = 0;

    for (long long i = 0; i < n; i++) {
        ans += (a*i + b) / m;
    }

    cout << ans << "\n";
    return 0;
}
```

## Recognition

```text
sum of floor terms
count lattice points under line
grouping by quotient
advanced divisor summatory patterns
```

---

# 035. Codeforces Math Forms

## Div2 A

```text
parity
simple formula
min/max
basic modulo
floor/ceil
```

## Div2 B

```text
gcd/lcm
sorting + math
counting
simple invariant
prefix modulo
```

## Div2 C

```text
binary exponentiation
combinatorics
sieve/factorization
pigeonhole
inclusion-exclusion basics
```

## Div2 D

```text
advanced number theory
DP + combinatorics
prime factorization with constraints
CRT basics
geometry
```

## CM Zone

```text
phi
CRT
floor sum
advanced combinatorics
geometry + data structure
number theory + DP
```

## Contest Recognition Table

```text
"divisible"          -> gcd/mod/factors
"ways"               -> combinatorics/DP
"huge exponent"      -> binpow/cycle
"same remainder"     -> pigeonhole/prefix modulo
"possible operation" -> invariant
"coordinates"        -> geometry
```

---

# 036. FAANG Math Forms

## Common Interview Forms

```text
Pow(x,n)                  -> binary exponentiation
Product Except Self       -> prefix/suffix multiplication
Happy Number              -> cycle detection
Excel Column Number       -> base conversion
Random Pick With Weight   -> prefix sum + probability
Rectangle Overlap         -> coordinate geometry
Robot Bounded Circle      -> direction cycle
Single Number             -> XOR
```

## Explanation Template

```text
Brute force:
    Describe direct simulation.

Bottleneck:
    Explain why it is too slow.

Math structure:
    Identify formula/cycle/invariant.

Optimal:
    Give time/space and edge cases.
```

---

# 037. Math Bug Encyclopedia

## Negative Mod

```cpp
(-2) % 7 == -2
```

Fix:

```cpp
((x % MOD) + MOD) % MOD
```

## Overflow Before Mod

Bad:

```cpp
long long x = a * b % MOD;
```

Safer:

```cpp
long long x = (__int128)a * b % MOD;
```

## Wrong Ceil

Bad:

```cpp
ceil(a / b)
```

Good:

```cpp
(a + b - 1) / b
```

## Modular Division Bug

Bad:

```cpp
a / b % MOD
```

Good when inverse exists:

```cpp
a * inv(b) % MOD
```

## Geometry Precision Bug

Bad:

```text
compare double slopes
```

Good:

```text
cross multiplication / cross product
```

---

# 099. Final Cheat Sheet

## Formula Sheet

```text
ceil(a/b) positive       = (a+b-1)/b
multiples in [L,R]       = R/k - (L-1)/k
gcd(a,b)                 = gcd(b,a%b)
lcm(a,b)                 = a/gcd(a,b)*b
C(n,r)                   = n!/(r!(n-r)!)
stars and bars           = C(n+k-1,k-1)
Catalan                  = C(2n,n)/(n+1)
Derangement              = D[n]=(n-1)(D[n-1]+D[n-2])
phi(n)                   = n * Π(1 - 1/p)
orientation              = cross(B-A,C-A)
```

## Pattern Router

```text
Huge answer         -> modulo
Repeated power      -> binary exponentiation
Common divisor      -> gcd/lcm
Prime structure     -> sieve/factorization
Choose ways         -> nCr/permutation
Distribute items    -> stars and bars
At least one        -> inclusion-exclusion
Guaranteed collision-> pigeonhole
No fixed point      -> derangement
Balanced structure  -> Catalan
Random average      -> expected value
Pairs cancel        -> XOR
Coordinates         -> geometry
Remainder systems   -> CRT
```

## Master C++17 Math Template

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

ll binpow(ll a, ll b) {
    a = norm(a);
    ll res = 1;

    while (b) {
        if (b & 1) res = res * a % MOD;
        a = a * a % MOD;
        b >>= 1;
    }

    return res;
}

ll inv(ll x) {
    return binpow(x, MOD - 2);
}

ll gcdll(ll a, ll b) {
    a = llabs(a);
    b = llabs(b);

    while (b) {
        ll r = a % b;
        a = b;
        b = r;
    }

    return a;
}

ll lcmll(ll a, ll b) {
    return a / gcdll(a,b) * b;
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
Find hidden structure
    |
    +-- remainder  -> modulo/cycle/inverse/CRT
    +-- factor     -> gcd/lcm/prime/divisor
    +-- choice     -> nCr/permutation/stars-bars
    +-- preserved  -> parity/invariant/xor
    +-- position   -> number line/abs/geometry
    +-- random     -> probability/expectation
    |
    v
Apply safe C++17 template
    |
    v
Check overflow, mod, edge cases
```
