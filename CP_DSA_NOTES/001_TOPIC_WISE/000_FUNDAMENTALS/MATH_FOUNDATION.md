# 📘 CP + DSA Math Foundation — Detailed Problem Workbook

> **Goal:** Learn CP/DSA math as reusable patterns using detailed problems, input/output, mental maps, C++ code, and index-by-index dry runs.  
> **Style:** clickable index → concept → recognition signal → C++ helper → problem card → input/output → index-by-index dry run.

> ✅ **Clean workbook version:** No flow/diagram sections. Focused on mental map, detailed problem explanation, input/output, C++ code, and index-by-index dry runs.

---

## 🧭 Clickable Master Index

### Core Math Topics

| # | Topic | Main Pattern | Link |
|---:|---|---|---|
| 0 | Master Mental Map | CP math thinking framework | [Open](#0-master-mental-map) |
| 1 | Ceiling Division | minimum groups / batches | [Open](#1-ceiling-division) |
| 2 | Modulo and Cycles | repeated movement / circular position | [Open](#2-modulo-and-cycles) |
| 3 | Binary Exponentiation | huge power in `O(log n)` | [Open](#3-binary-exponentiation) |
| 4 | Modular Arithmetic | modulo add/sub/mul/division | [Open](#4-modular-arithmetic) |
| 5 | GCD and LCM | common divisor / cycle sync | [Open](#5-gcd-and-lcm) |
| 6 | Primes and Divisors | factorization / divisor count | [Open](#6-primes-and-divisors) |
| 7 | Prefix Sum | many range sum queries | [Open](#7-prefix-sum) |
| 8 | Arithmetic and Geometric Sequences | sequence formula | [Open](#8-arithmetic-and-geometric-sequences) |
| 9 | Summation Formulas | replace loop with formula | [Open](#9-summation-formulas) |
| 10 | Counting Permutation Combination | arrangements / selections | [Open](#10-counting-permutation-combination) |
| 11 | Logs Bits and Halving | powers, bits, binary search | [Open](#11-logs-bits-and-halving) |
| 12 | Algebra and Equations | transform equation | [Open](#12-algebra-and-equations) |
| 13 | Quadratic Formula | roots / discriminant | [Open](#13-quadratic-formula) |
| 14 | Geometry Basics | distance / cross product / area | [Open](#14-geometry-basics) |
| 15 | Big O Mathematics | complexity estimation | [Open](#15-big-o-mathematics) |
| 16 | CP Problem Solving Framework | solve under pressure | [Open](#16-cp-problem-solving-framework) |
| 17 | Candidate Master Pattern Library | advanced math patterns | [Open](#17-candidate-master-pattern-library) |
| 18 | Final Formula Sheet | quick revision | [Open](#18-final-formula-sheet) |
| 19 | Practice Roadmap | phase-wise plan | [Open](#19-practice-roadmap) |
| 20 | Compact C++ Template | reusable helpers | [Open](#20-compact-c-template) |
| 21 | Java Helper Pack | Java helpers | [Open](#21-java-helper-pack) |

### Detailed Problem Workbook

| Problem | Pattern | Link |
|---|---|---|
| Codeforces 151A — Soft Drinking | bottleneck + integer groups | [Open](#problem-1-codeforces-151a-soft-drinking) |
| CSES Static Range Sum Queries | prefix sum | [Open](#problem-2-cses-static-range-sum-queries) |
| CSES Exponentiation | binary exponentiation | [Open](#problem-3-cses-exponentiation) |
| CSES Binomial Coefficients | nCr + modular inverse | [Open](#problem-4-cses-binomial-coefficients) |
| CSES Common Divisors | multiples counting | [Open](#problem-5-cses-common-divisors) |
| CSES Counting Divisors | prime factorization + SPF | [Open](#problem-6-cses-counting-divisors) |
| CSES Number Spiral | layer formula | [Open](#problem-7-cses-number-spiral) |
| CSES Missing Number | sum formula | [Open](#problem-8-cses-missing-number) |
| CSES Factory Machines | binary search on answer | [Open](#problem-9-cses-factory-machines) |
| CSES Point Location Test | geometry cross product | [Open](#problem-10-cses-point-location-test) |

---

## 🎯 How to Study This Note Fast

```text
For every topic:
1. Read recognition signal.
2. Write formula from memory.
3. Code helper without looking.
4. Solve one easy problem.
5. Dry run index by index.
6. Solve one mixed problem later to avoid pattern forgetting.
```

> ✅ **Interview/contest habit:** before coding, write the invariant or formula in one line.

---

---

## 🧩 Problem Card Template

Use this same structure in your own notes.

```text
Problem:
Pattern:
Recognition:
Input:
Output:
Brute force:
Optimization idea:
Formula / invariant:
C++ solution:
Index-by-index dry run:
Complexity:
Mistakes to avoid:
```

---

## 0. Master Mental Map


Core idea:

> CP math converts slow simulation into formulas, patterns, and reusable helpers.

General framework:


### Master Pattern Table

| Signal in problem | Mathematical thought | Typical tool |
|---|---|---|
| minimum groups or days | round up division | ceiling division |
| repeated after k steps | cycle position | modulo |
| huge exponent | use binary bits | binary exponentiation |
| divide under modulo | multiply by inverse | Fermat or extended gcd |
| common divisor | reduce using gcd | Euclid |
| cycles meet | common multiple | lcm |
| many range sums | subtract prefix | prefix sum |
| count ways | product sum complement | combinatorics |
| order matters | arrangement | permutation |
| order ignored | selection | combination |
| repeated halving | logarithmic steps | binary search or powers |
| prime factors matter | exponent formula | factorization |
| large n up to 1e9 | no linear loops | formula or log |
| construct answer | preserve truth | invariant |

---

## 1. Ceiling Division

### 🧮 Formula

For positive integers `a` and `b`:

```text
ceil(a / b) = (a + b - 1) / b
```

Equivalent safer formula:

```text
ceil(a / b) = a / b + (a % b != 0)
```

For non negative integers, the second formula avoids overflow from `a + b - 1`.

### When to use

Use when you need minimum groups, days, operations, pages, packets, buses, boxes, rounds, or batches.

### Example

```text
a = 10 items
b = 3 items per group
ceil(10 / 3) = 4 groups
```


### 💻 C++ Helper


#### 💻 C++ Code

```cpp
long long ceilDiv(long long a, long long b) {
    return a / b + (a % b != 0);
}
```


### Signed version


#### 💻 C++ Code

```cpp
long long floorDiv(long long a, long long b) {
    long long q = a / b;
    long long r = a % b;
    if (r != 0 && ((r > 0) != (b > 0))) q--;
    return q;
}

long long ceilDivSigned(long long a, long long b) {
    long long q = a / b;
    long long r = a % b;
    if (r != 0 && ((r > 0) == (b > 0))) q++;
    return q;
}
```


### 🔎 Dry Run

```text
a = 17, b = 5
17 / 5 = 3 remainder 2
Since remainder exists, answer = 3 + 1 = 4
```

### 🧠 Pattern

If the problem says:

```text
minimum number of operations where each operation handles at most k items
```

Think:

```text
ceil(n / k)
```

### 🧩 Practice Problem: [Codeforces 151A Soft Drinking](https://codeforces.com/problemset/problem/151/A)

| Field | Details |
|---|---|
| Topic | Ceiling Division |
| Main concepts | minimum among limiting resources, integer division |
| Goal | Convert the statement into a known math pattern |
| Code hint | Compute each resource capacity and take minimum. |
| Complexity | O(1) |


#### Approach Logic


1. Compute how many toasts can be made from drink.
2. Compute how many toasts can be made from limes.
3. Compute how many toasts can be made from salt.
4. The answer is the minimum of these values divided by number of friends.
5. This is not pure ceiling division, but it trains integer groups and bottleneck thinking.


#### 🔎 Dry Run

```text
n = 3 friends
drink gives 10 total toasts
limes give 6 toasts
salt gives 9 toasts
bottleneck = 6
answer = 6 / 3 = 2
```

### 🧩 Practice Problem: [Codeforces 919A Supermarket](https://codeforces.com/problemset/problem/919/A)

| Field | Details |
|---|---|
| Topic | Ceiling Division |
| Main concepts | ratio comparison, price per unit, minimum value |
| Goal | Convert the statement into a known math pattern |
| Code hint | Track minimum double ratio. |
| Complexity | O(n) |


#### Approach Logic


1. For each shop, compute price per unit as `a / b`.
2. Find the minimum price per unit.
3. Multiply by required amount.
4. Use double because the answer can be fractional.


#### 🔎 Dry Run

```text
Shop 1 price 10 for 2 kg gives 5 per kg
Shop 2 price 15 for 5 kg gives 3 per kg
Need 4 kg
answer = 3 * 4 = 12
```

---

## 2. Modulo and Cycles

### 🧮 Formula

```text
remainder = a % m
```

Modulo keeps a number inside range:

```text
0 to m - 1
```

Cycle movement:

```text
new_position = (start + steps) % cycle_length
```

Negative normalization:

```text
normalized = ((x % m) + m) % m
```

### Meaning

Modulo means position inside a repeated cycle.

### Example

```text
Today = 3
After 100 days in a 7 day cycle:
(3 + 100) % 7 = 103 % 7 = 5
```


### 💻 C++ Helper


#### 💻 C++ Code

```cpp
long long norm(long long x, long long mod) {
    x %= mod;
    if (x < 0) x += mod;
    return x;
}
```


### 🔎 Dry Run

```text
x = -3, mod = 7
-3 % 7 = -3 in C++
Add 7
answer = 4
```

### 🧠 Pattern

If the problem says:

```text
repeats every k
clock
days
circular array
large number of moves
```

Think modulo.

### 🧩 Practice Problem: [Codeforces 913A Modular Exponentiation](https://codeforces.com/problemset/problem/913/A)

| Field | Details |
|---|---|
| Topic | Modulo and Cycles |
| Main concepts | modulo, powers of two, overflow avoidance |
| Goal | Convert the statement into a known math pattern |
| Code hint | If n is large enough, directly print m. |
| Complexity | O(1) |


#### Approach Logic


1. The problem asks for `m mod 2^n`.
2. If `n` is very large, then `2^n` is bigger than `m`, so answer is `m`.
3. Otherwise compute `2^n` safely and output `m % value`.
4. Key trick: avoid computing impossible huge powers.


#### 🔎 Dry Run

```text
n = 3, m = 10
2^3 = 8
10 mod 8 = 2
answer = 2

n = 40, m = 100
2^40 is larger than 100
100 mod huge number = 100
answer = 100
```

### 🧩 Practice Problem: [CSES Increasing Array](https://cses.fi/problemset/task/1094)

| Field | Details |
|---|---|
| Topic | Modulo and Cycles |
| Main concepts | monotonic invariant, operation count |
| Goal | Convert the statement into a known math pattern |
| Code hint | Track previous maximum. |
| Complexity | O(n) |


#### Approach Logic


1. Maintain the invariant that the processed prefix is non decreasing.
2. If current value is smaller than previous value, increase it to previous value.
3. Add the difference to answer.
4. This is a math invariant problem more than a simulation problem.


#### 🔎 Dry Run

```text
array = 3 2 5 1 7
prev = 3
2 is smaller than 3, add 1, make it 3
5 is okay, prev = 5
1 is smaller than 5, add 4, make it 5
7 is okay
answer = 1 + 4 = 5
```

---

## 3. Binary Exponentiation

### Mathematical formula

```text
x^n = x^(n/2) * x^(n/2), if n is even
x^n = x^(n/2) * x^(n/2) * x, if n is odd
```

Binary representation idea:

```text
13 = 8 + 4 + 1
x^13 = x^8 * x^4 * x^1
```

### 💻 C++ Helper


#### 💻 C++ Code

```cpp
long long binPow(long long base, long long exp) {
    long long res = 1;
    while (exp > 0) {
        if (exp & 1) res *= base;
        base *= base;
        exp >>= 1;
    }
    return res;
}
```


### Modular C++ Helper


#### 💻 C++ Code

```cpp
long long modPow(long long base, long long exp, long long mod) {
    long long res = 1 % mod;
    base %= mod;
    while (exp > 0) {
        if (exp & 1) res = (__int128)res * base % mod;
        base = (__int128)base * base % mod;
        exp >>= 1;
    }
    return res;
}
```


### Java Helper


#### ☕ Java Code

```java
static long modPow(long base, long exp, long mod) {
    long res = 1 % mod;
    base %= mod;
    while (exp > 0) {
        if ((exp & 1) == 1) res = (res * base) % mod;
        base = (base * base) % mod;
        exp >>= 1;
    }
    return res;
}
```


### 🔎 Dry Run: compute `3^13`

```text
13 in binary = 1101
Use powers: 3^1, 3^4, 3^8
3^13 = 3^8 * 3^4 * 3^1
```

| exp | base | res | action |
|---:|---:|---:|---|
| 13 | 3 | 1 | odd so res becomes 3 |
| 6 | 9 | 3 | even so skip |
| 3 | 81 | 3 | odd so res becomes 243 |
| 1 | 6561 | 243 | odd so res becomes 1594323 |
| 0 | done | 1594323 | return |

### 🧠 Pattern

Use binary exponentiation when:

```text
exponent is huge
need power modulo M
need repeated squaring
need matrix power later
```

### 🧩 Practice Problem: [CSES Exponentiation](https://cses.fi/problemset/task/1095)

| Field | Details |
|---|---|
| Topic | Binary Exponentiation |
| Main concepts | binary exponentiation, modulo, many queries |
| Goal | Convert the statement into a known math pattern |
| Code hint | Use modPow(a, b, MOD). |
| Complexity | O(log b) per query |


#### Approach Logic


1. Each query gives `a` and `b`.
2. Direct multiplication is impossible when `b` is large.
3. Use modular binary exponentiation.
4. For every odd exponent bit, multiply answer by current base.
5. Square base every step and halve exponent.


#### 🔎 Dry Run

```text
a = 3, b = 13, mod = 1000000007
13 binary is 1101
Use powers 3^1, 3^4, 3^8
answer = 1594323
```

### 🧩 Practice Problem: [CSES Exponentiation II](https://cses.fi/problemset/task/1712)

| Field | Details |
|---|---|
| Topic | Binary Exponentiation |
| Main concepts | Fermat reduction, nested exponent, modular power |
| Goal | Convert the statement into a known math pattern |
| Code hint | Use modPow twice. |
| Complexity | O(log c plus log MOD) |


#### Approach Logic


1. Need compute `a^(b^c) mod MOD`.
2. Since MOD is prime, reduce exponent modulo `MOD minus 1`.
3. Compute `e = b^c mod MOD minus 1`.
4. Answer is `a^e mod MOD`.
5. This combines Fermat theorem and binary exponentiation.


#### 🔎 Dry Run

```text
a = 2, b = 3, c = 2
exponent = 3^2 = 9
answer = 2^9 = 512
For huge values compute exponent modulo MOD minus 1
```

---

## 4. Modular Arithmetic

### 🧮 Formulas

```text
(a + b) mod M = ((a mod M) + (b mod M)) mod M
(a - b) mod M = ((a mod M) - (b mod M) + M) mod M
(a * b) mod M = ((a mod M) * (b mod M)) mod M
```

For prime `M` and `a` not divisible by `M`:

```text
a^(-1) mod M = a^(M - 2) mod M
```

This is based on Fermat's Little Theorem:

```text
a^(M - 1) = 1 mod M
```

### 💻 C++ Helper


#### 💻 C++ Code

```cpp
const long long MOD = 1000000007LL;

long long addMod(long long a, long long b) {
    return (a % MOD + b % MOD) % MOD;
}

long long subMod(long long a, long long b) {
    return (a % MOD - b % MOD + MOD) % MOD;
}

long long mulMod(long long a, long long b) {
    return (__int128)(a % MOD) * (b % MOD) % MOD;
}

long long modInversePrime(long long a) {
    return modPow(a, MOD - 2, MOD);
}
```


### 🔎 Dry Run

Find `3 / 2 mod 7`.

```text
Division means multiply by inverse.
2 inverse mod 7 = 2^(7 - 2) mod 7 = 2^5 mod 7 = 32 mod 7 = 4
3 / 2 mod 7 = 3 * 4 mod 7 = 12 mod 7 = 5
```

### 🧩 Practice Problem: [CSES Binomial Coefficients](https://cses.fi/problemset/task/1079)

| Field | Details |
|---|---|
| Topic | Modular Arithmetic |
| Main concepts | factorials, inverse factorials, nCr modulo prime |
| Goal | Convert the statement into a known math pattern |
| Code hint | Precompute factorial and inverse factorial. |
| Complexity | O(maxN log MOD plus q) |


#### Approach Logic


1. Need answer many `nCr` queries under prime modulo.
2. Precompute factorials up to maximum n.
3. Precompute inverse factorials using Fermat inverse.
4. Answer each query as `fact[n] * invFact[r] * invFact[n-r]`.
5. This changes each query from O(n) to O(1).


#### 🔎 Dry Run

```text
n = 5, r = 2
fact[5] = 120
fact[2] = 2
fact[3] = 6
C(5,2) = 120 / 12 = 10
Under modulo division becomes multiply by inverse
```

### 🧩 Practice Problem: [CSES Distributing Apples](https://cses.fi/problemset/task/1716)

| Field | Details |
|---|---|
| Topic | Modular Arithmetic |
| Main concepts | stars and bars, nCr modulo prime |
| Goal | Convert the statement into a known math pattern |
| Code hint | Answer nCr(n + m - 1, n - 1). |
| Complexity | O(maxN log MOD plus 1) |


#### Approach Logic


1. Distribute `m` identical apples among `n` children.
2. This is stars and bars.
3. Number of ways is `C(n + m - 1, n - 1)`.
4. Use modular nCr precomputation.


#### 🔎 Dry Run

```text
n = 3 children, m = 4 apples
Represent as stars and bars
**** with 2 separators
Total positions = 4 + 3 - 1 = 6
Choose separator positions = C(6,2) = 15
```

---

## 5. GCD and LCM

### 🧮 Formulas

```text
gcd(a, b) = gcd(b, a mod b)
lcm(a, b) = a / gcd(a, b) * b
```

Important property:

```text
gcd(a, b) * lcm(a, b) = a * b
```

### 💻 C++ Helper


#### 💻 C++ Code

```cpp
long long gcdll(long long a, long long b) {
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
```


### 🔎 Dry Run

```text
gcd(48, 18)
48 % 18 = 12
18 % 12 = 6
12 % 6 = 0
answer = 6
```

### 🧠 Pattern

Use GCD when the problem has:

```text
common divisor
reduce fraction
same step size
period alignment
minimum repeating length
```

Use LCM when the problem has:

```text
when two cycles meet again
common multiple
synchronization
```

### 🧩 Practice Problem: [CSES Common Divisors](https://cses.fi/problemset/task/1081)

| Field | Details |
|---|---|
| Topic | GCD and LCM |
| Main concepts | divisors, frequency counting, largest common divisor |
| Goal | Convert the statement into a known math pattern |
| Code hint | Use frequency array and multiples loop. |
| Complexity | O(maxA log maxA) |


#### Approach Logic


1. Count frequency of every value.
2. Iterate possible divisor from maximum value downward.
3. Count how many array values are multiples of this divisor.
4. First divisor that appears in at least two numbers is the answer.
5. This avoids checking every pair.


#### 🔎 Dry Run

```text
array = 6 10 15
Check divisor 15 gives one multiple
Check divisor 10 gives one multiple
Check divisor 5 gives two multiples 10 and 15
answer = 5
```

### 🧩 Practice Problem: [Codeforces 1458A Row GCD](https://codeforces.com/problemset/problem/1458/A)

| Field | Details |
|---|---|
| Topic | GCD and LCM |
| Main concepts | gcd transformation, difference invariant |
| Goal | Convert the statement into a known math pattern |
| Code hint | Compute gcd of differences once. |
| Complexity | O(n log A plus q log A) |


#### Approach Logic


1. For array `a`, compute gcd of all differences `a[i] - a[0]`.
2. For each query `b`, answer is `gcd(a[0] + b, differenceGcd)`.
3. Key identity: gcd of shifted numbers depends on first shifted value and differences.
4. This is a classic CM level gcd transformation pattern.


#### 🔎 Dry Run

```text
a = [6, 10, 14]
differences from first = 4, 8
g = gcd(4,8) = 4
query b = 2
answer = gcd(6 + 2, 4) = gcd(8,4) = 4
```

---

## 6. Primes and Divisors

### Prime definition

A prime number has exactly two positive divisors:

```text
1 and itself
```

### Prime check formula

If `n` has a divisor greater than `sqrt(n)`, it must also have a paired divisor smaller than `sqrt(n)`.

So check only:

```text
2 to sqrt(n)
```

### C++ Prime Check


#### 💻 C++ Code

```cpp
bool isPrime(long long n) {
    if (n < 2) return false;
    for (long long d = 2; d * d <= n; d++) {
        if (n % d == 0) return false;
    }
    return true;
}
```


### Divisor count formula

If:

```text
n = p1^a1 * p2^a2 * ... * pk^ak
```

Then:

```text
number_of_divisors = (a1 + 1)(a2 + 1)...(ak + 1)
```

### Example

```text
18 = 2^1 * 3^2
number of divisors = (1 + 1)(2 + 1) = 6
Divisors: 1, 2, 3, 6, 9, 18
```

### C++ Factorization


#### 💻 C++ Code

```cpp
vector<pair<long long,int>> factorize(long long n) {
    vector<pair<long long,int>> f;
    for (long long d = 2; d * d <= n; d++) {
        if (n % d == 0) {
            int cnt = 0;
            while (n % d == 0) {
                n /= d;
                cnt++;
            }
            f.push_back({d, cnt});
        }
    }
    if (n > 1) f.push_back({n, 1});
    return f;
}
```


### Sieve Helper


#### 💻 C++ Code

```cpp
vector<int> primes;
vector<bool> isComposite;

void sieve(int n) {
    isComposite.assign(n + 1, false);
    for (int i = 2; i <= n; i++) {
        if (!isComposite[i]) {
            primes.push_back(i);
            if ((long long)i * i <= n) {
                for (long long j = 1LL * i * i; j <= n; j += i) {
                    isComposite[(int)j] = true;
                }
            }
        }
    }
}
```


### Smallest Prime Factor Helper


#### 💻 C++ Code

```cpp
vector<int> spf;

void buildSPF(int n) {
    spf.resize(n + 1);
    for (int i = 0; i <= n; i++) spf[i] = i;
    for (int i = 2; i * i <= n; i++) {
        if (spf[i] == i) {
            for (long long j = 1LL * i * i; j <= n; j += i) {
                if (spf[(int)j] == j) spf[(int)j] = i;
            }
        }
    }
}

vector<pair<int,int>> factorizeSPF(int x) {
    vector<pair<int,int>> res;
    while (x > 1) {
        int p = spf[x];
        int cnt = 0;
        while (x % p == 0) {
            x /= p;
            cnt++;
        }
        res.push_back({p, cnt});
    }
    return res;
}
```


### 🧩 Practice Problem: [CSES Counting Divisors](https://cses.fi/problemset/task/1713)

| Field | Details |
|---|---|
| Topic | Primes and Divisors |
| Main concepts | prime factorization, divisor count formula, precomputation |
| Goal | Convert the statement into a known math pattern |
| Code hint | Build SPF then apply divisor formula. |
| Complexity | O(maxA log log maxA plus q log A) |


#### Approach Logic


1. For each query number `x`, factorize it.
2. If `x = p1^a1 * p2^a2`, divisor count is product of `(ai + 1)`.
3. Since many queries exist, use SPF precomputation.
4. Factor each number in logarithmic like time.


#### 🔎 Dry Run

```text
x = 18
18 = 2^1 * 3^2
divisors = (1 + 1) * (2 + 1) = 6
answer = 6
```

### 🧩 Practice Problem: [Codeforces 546D Soldier and Number Game](https://codeforces.com/problemset/problem/546/D)

| Field | Details |
|---|---|
| Topic | Primes and Divisors |
| Main concepts | prime factor count prefix, SPF, range query |
| Goal | Convert the statement into a known math pattern |
| Code hint | Precompute factor counts and prefix. |
| Complexity | O(maxN log log maxN plus q) |


#### Approach Logic


1. Precompute number of prime factors with multiplicity for every number.
2. Build prefix sum over those counts.
3. For query `a b`, answer is prefix[a] minus prefix[b].
4. This combines SPF and prefix sums.


#### 🔎 Dry Run

```text
primeFactorCount[10] = 2 because 10 = 2 * 5
primeFactorCount[12] = 3 because 12 = 2 * 2 * 3
For query a = 5, b = 3
answer = count factors in 4 and 5
```

---

## 7. Prefix Sum

### 🧮 Formula

For 0 indexed array:

```text
pref[0] = 0
pref[i + 1] = pref[i] + a[i]
range_sum(l, r) = pref[r + 1] - pref[l]
```

### Example

```text
a = [1, 2, 3, 4, 5]
pref = [0, 1, 3, 6, 10, 15]
range_sum(1, 3) = pref[4] - pref[1] = 10 - 1 = 9
```

### 💻 C++ Helper


#### 💻 C++ Code

```cpp
vector<long long> buildPrefix(const vector<int>& a) {
    int n = (int)a.size();
    vector<long long> pref(n + 1, 0);
    for (int i = 0; i < n; i++) {
        pref[i + 1] = pref[i] + a[i];
    }
    return pref;
}

long long rangeSum(const vector<long long>& pref, int l, int r) {
    return pref[r + 1] - pref[l];
}
```


### 2D Prefix Formula

```text
pref[i][j] = grid sum from row 1 col 1 to row i col j

sum rectangle = pref[x2][y2] - pref[x1 - 1][y2] - pref[x2][y1 - 1] + pref[x1 - 1][y1 - 1]
```

### 🔎 Dry Run

```text
a = [2, 4, 1, 7]
pref[0] = 0
pref[1] = 2
pref[2] = 6
pref[3] = 7
pref[4] = 14
sum from index 1 to 3 = pref[4] - pref[1] = 14 - 2 = 12
```

### 🧠 Pattern

Use prefix sum when there are many range sum queries and array does not change.

### 🧩 Practice Problem: [CSES Static Range Sum Queries](https://cses.fi/problemset/task/1646)

| Field | Details |
|---|---|
| Topic | Prefix Sum |
| Main concepts | prefix sum, range query, O one answer |
| Goal | Convert the statement into a known math pattern |
| Code hint | Build prefix once. |
| Complexity | O(n plus q) |


#### Approach Logic


1. Build prefix array where `pref[i]` is sum of first `i` elements.
2. For every query `[l, r]`, convert to zero based or one based carefully.
3. Answer using subtraction of two prefix values.
4. Avoid summing inside each query.


#### 🔎 Dry Run

```text
a = [2, 4, 1, 7]
query l = 2, r = 4 one based
zero based range is 1 to 3
answer = pref[4] - pref[1] = 14 - 2 = 12
```

### 🧩 Practice Problem: [CSES Forest Queries](https://cses.fi/problemset/task/1652)

| Field | Details |
|---|---|
| Topic | Prefix Sum |
| Main concepts | 2D prefix sum, rectangle query |
| Goal | Convert the statement into a known math pattern |
| Code hint | Use one based 2D prefix. |
| Complexity | O(n squared plus q) |


#### Approach Logic


1. Convert each tree cell to 1 and empty cell to 0.
2. Build 2D prefix sum.
3. For a rectangle query, use inclusion exclusion on four prefix cells.
4. Be careful with one based indexing to simplify boundaries.


#### 🔎 Dry Run

```text
Grid values:
1 0
0 1
pref rectangle full grid = 2
Query whole grid answer = 2
```

---

## 8. Arithmetic and Geometric Sequences

### Arithmetic sequence formula

```text
a_n = a_1 + (n - 1)d
```

Sum:

```text
S_n = n(a_1 + a_n) / 2
```

### Geometric sequence formula

```text
a_n = a_1 * r^(n - 1)
```

Sum if `r != 1`:

```text
S_n = a_1(r^n - 1) / (r - 1)
```

Special power of two sum:

```text
1 + 2 + 4 + ... + 2^(n - 1) = 2^n - 1
```

### 💻 C++ Helpers


#### 💻 C++ Code

```cpp
long long arithmeticTerm(long long a1, long long d, long long n) {
    return a1 + (n - 1) * d;
}

long long arithmeticSum(long long a1, long long an, long long n) {
    return n * (a1 + an) / 2;
}
```


### 🔎 Dry Run

```text
Arithmetic: 5, 8, 11, 14
a1 = 5, d = 3
4th term = 5 + (4 - 1) * 3 = 14
```

```text
Geometric: 3, 6, 12, 24
a1 = 3, r = 2
4th term = 3 * 2^(4 - 1) = 24
```

### 🧩 Practice Problem: [CSES Number Spiral](https://cses.fi/problemset/task/1071)

| Field | Details |
|---|---|
| Topic | Sequences |
| Main concepts | pattern by layer, parity, formula |
| Goal | Convert the statement into a known math pattern |
| Code hint | Use max coordinate and parity. |
| Complexity | O(1) per query |


#### Approach Logic


1. The value depends on the layer `max(row, col)`.
2. The square at the end of each layer is `layer squared`.
3. Direction changes based on parity of the layer.
4. Derive formula separately for even and odd layers.


#### 🔎 Dry Run

```text
row = 2, col = 3
layer = 3
layer square = 9
For odd layer, values decrease along row direction
answer = 8
```

### 🧩 Practice Problem: [CSES Weird Algorithm](https://cses.fi/problemset/task/1068)

| Field | Details |
|---|---|
| Topic | Sequences |
| Main concepts | sequence simulation, parity transition |
| Goal | Convert the statement into a known math pattern |
| Code hint | Use long long. |
| Complexity | Depends on sequence length |


#### Approach Logic


1. Start from n.
2. If n is even, divide by two.
3. If n is odd, replace by `3n plus 1`.
4. Continue until n becomes one.
5. This is not formula based, but it trains sequence transitions and overflow awareness.


#### 🔎 Dry Run

```text
n = 3
3 is odd so 10
10 is even so 5
5 is odd so 16
16 8 4 2 1
sequence ends
```

---

## 9. Summation Formulas

### Core formulas

```text
1 + 2 + ... + n = n(n + 1) / 2
1^2 + 2^2 + ... + n^2 = n(n + 1)(2n + 1) / 6
1^3 + 2^3 + ... + n^3 = [n(n + 1) / 2]^2
```

### Summation properties

```text
sum(c * a_i) = c * sum(a_i)
sum(a_i + b_i) = sum(a_i) + sum(b_i)
```

### Example

```text
sum from i = 1 to n of (3i + 2)
= 3 * sum(i) + 2n
= 3n(n + 1)/2 + 2n
```

For `n = 5`:

```text
3 * 5 * 6 / 2 + 10 = 55
```

### 💻 C++ Helpers


#### 💻 C++ Code

```cpp
long long sumN(long long n) {
    return n * (n + 1) / 2;
}

long long sumSquares(long long n) {
    return n * (n + 1) * (2 * n + 1) / 6;
}

long long sumCubes(long long n) {
    long long s = n * (n + 1) / 2;
    return s * s;
}
```


### 🧩 Practice Problem: [CSES Missing Number](https://cses.fi/problemset/task/1083)

| Field | Details |
|---|---|
| Topic | Summation Formulas |
| Main concepts | sum formula, missing value |
| Goal | Convert the statement into a known math pattern |
| Code hint | Use sum formula. |
| Complexity | O(n) |


#### Approach Logic


1. Sum of numbers from `1` to `n` is `n(n+1)/2`.
2. Read `n-1` numbers and compute their sum.
3. Missing number is expected sum minus actual sum.
4. Use long long because sum can be large.


#### 🔎 Dry Run

```text
n = 5
numbers = 2 3 1 5
expected sum = 5 * 6 / 2 = 15
actual sum = 11
missing = 4
```

### 🧩 Practice Problem: [CSES Two Sets](https://cses.fi/problemset/task/1092)

| Field | Details |
|---|---|
| Topic | Summation Formulas |
| Main concepts | sum parity, constructive partition |
| Goal | Convert the statement into a known math pattern |
| Code hint | Check parity then construct greedily. |
| Complexity | O(n) |


#### Approach Logic


1. Total sum is `n(n+1)/2`.
2. If total sum is odd, equal partition is impossible.
3. Otherwise target each set sum is total divided by two.
4. Greedily take large numbers while target remains.
5. This uses summation plus constructive thinking.


#### 🔎 Dry Run

```text
n = 7
total = 28
target = 14
take 7 target becomes 7
take 6 target becomes 1
take 1 target becomes 0
one set = 7 6 1
other set = 2 3 4 5
```

---

## 10. Counting Permutation Combination

### Product rule

```text
If task A has x ways and task B has y ways:
total = x * y
```

### Sum rule

```text
If choose A or B:
total = x + y
```

### Complement rule

```text
good = total - bad
```

### Factorial

```text
n! = n * (n - 1) * ... * 1
```

### Permutation

Order matters:

```text
P(n, r) = n! / (n - r)!
```

### Combination

Order does not matter:

```text
C(n, r) = n! / (r!(n - r)!)
```

Symmetry:

```text
C(n, r) = C(n, n - r)
```

### Stars and Bars

Number of non negative integer solutions to:

```text
x1 + x2 + ... + xk = n
```

is:

```text
C(n + k - 1, k - 1)
```

### C++ Small nCr Helper


#### 💻 C++ Code

```cpp
long long nCrSmall(int n, int r) {
    if (r < 0 || r > n) return 0;
    r = min(r, n - r);
    long long ans = 1;
    for (int i = 1; i <= r; i++) {
        ans = ans * (n - r + i) / i;
    }
    return ans;
}
```


### Modular nCr helper for prime MOD


#### 💻 C++ Code

```cpp
const int MAXN = 1000000;
const long long MOD = 1000000007LL;
long long fact[MAXN + 1], invFact[MAXN + 1];

void buildFactorials() {
    fact[0] = 1;
    for (int i = 1; i <= MAXN; i++) fact[i] = fact[i - 1] * i % MOD;
    invFact[MAXN] = modPow(fact[MAXN], MOD - 2, MOD);
    for (int i = MAXN - 1; i >= 0; i--) invFact[i] = invFact[i + 1] * (i + 1) % MOD;
}

long long nCrMod(int n, int r) {
    if (r < 0 || r > n) return 0;
    return fact[n] * invFact[r] % MOD * invFact[n - r] % MOD;
}
```


### 🧩 Practice Problem: [CSES Creating Strings](https://cses.fi/problemset/task/1622)

| Field | Details |
|---|---|
| Topic | Counting |
| Main concepts | permutations with duplicate characters |
| Goal | Convert the statement into a known math pattern |
| Code hint | Sort then use next_permutation. |
| Complexity | O(k times n) |


#### Approach Logic


1. Need print all distinct permutations of a string.
2. Sort the string first.
3. Use `next_permutation` to generate permutations in sorted order.
4. Since duplicates exist, sorting plus next permutation naturally avoids duplicate arrangements.


#### 🔎 Dry Run

```text
s = aab
sorted = aab
permutations:
aab
aba
baa
count = 3
```

### 🧩 Practice Problem: [CSES Distributing Apples](https://cses.fi/problemset/task/1716)

| Field | Details |
|---|---|
| Topic | Counting |
| Main concepts | stars and bars, modular combination |
| Goal | Convert the statement into a known math pattern |
| Code hint | Use modular nCr. |
| Complexity | O(maxN log MOD) |


#### Approach Logic


1. Apples are identical and children are distinct.
2. Need non negative solutions to `x1 + x2 + ... + xn = m`.
3. Apply stars and bars.
4. Answer is `C(n + m - 1, m)` or `C(n + m - 1, n - 1)`.


#### 🔎 Dry Run

```text
n = 3, m = 4
Need x1 + x2 + x3 = 4
Formula C(4 + 3 - 1, 3 - 1)
C(6,2) = 15
```

---

## 11. Logs Bits and Halving

### Log meaning

```text
2^3 = 8
log2(8) = 3
```

Log asks:

```text
What power gives this number?
```

### 🧮 Formulas

```text
log(xy) = log(x) + log(y)
log(x / y) = log(x) - log(y)
log(x^k) = k log(x)
```

### Bits needed

For positive `n`:

```text
bits = floor(log2(n)) + 1
```

### Digits needed

For positive `n`:

```text
digits = floor(log10(n)) + 1
```

### Power of two check

```text
n > 0 and (n & (n - 1)) == 0
```

### 💻 C++ Helpers


#### 💻 C++ Code

```cpp
bool isPowerOfTwo(long long n) {
    return n > 0 && (n & (n - 1)) == 0;
}

int bitCount(unsigned long long n) {
    if (n == 0) return 1;
    return 64 - __builtin_clzll(n);
}
```


### 🔎 Dry Run

```text
8 = 1000
7 = 0111
8 & 7 = 0000
So 8 is power of two.
```

### Where used

- binary search
- heap height
- divide and conquer
- binary exponentiation
- sparse table
- lifting in trees

### 🧩 Practice Problem: [CSES Gray Code](https://cses.fi/problemset/task/2205)

| Field | Details |
|---|---|
| Topic | Logs Bits and Halving |
| Main concepts | bits, xor, binary representation |
| Goal | Convert the statement into a known math pattern |
| Code hint | Use gray = i ^ (i >> 1). |
| Complexity | O(n times 2^n) |


#### Approach Logic


1. Gray code of number `i` is `i xor (i shifted right by one)`.
2. Generate values from `0` to `2^n - 1`.
3. Print each value as binary with exactly `n` bits.
4. Adjacent Gray codes differ by one bit.


#### 🔎 Dry Run

```text
n = 2
i = 0 gives 00
i = 1 gives 01
i = 2 gives 11
i = 3 gives 10
Each adjacent pair differs by one bit
```

### 🧩 Practice Problem: [CSES Subset Sums](https://cses.fi/problemset/task/1655)

| Field | Details |
|---|---|
| Topic | Logs Bits and Halving |
| Main concepts | xor basis, bit linear algebra |
| Goal | Convert the statement into a known math pattern |
| Code hint | Use xor basis. |
| Complexity | O(n log A) |


#### Approach Logic


1. This is an advanced bit math problem.
2. Maintain a xor basis where each basis element has a unique highest set bit.
3. Insert each number by reducing it with existing basis values.
4. Maximum xor is built greedily from high bit to low bit.


#### 🔎 Dry Run

```text
numbers = 3, 5
3 binary 011
5 binary 101
possible xor 3 xor 5 = 6
maximum subset xor = 6
```

---

## 12. Algebra and Equations

### Basic formulas

Distributive property:

```text
a(b + c) = ab + ac
```

Factoring:

```text
ab + ac = a(b + c)
```

Linear equation:

```text
ax + b = c
x = (c - b) / a
```

Inequality warning:

```text
If multiply or divide by negative, flip sign.
```

Example:

```text
-2x > 6
x < -3
```

### System of equations

```text
x + y = 10
x - y = 4
```

Add equations:

```text
2x = 14
x = 7
y = 3
```

### CP Pattern

Many CP problems hide algebra like:

```text
Find minimum x such that ax + b >= n
```

Rearrange:

```text
x >= (n - b) / a
answer = ceil((n - b) / a)
```

### 🧩 Practice Problem: [CSES Apple Division](https://cses.fi/problemset/task/1623)

| Field | Details |
|---|---|
| Topic | Algebra |
| Main concepts | minimize difference, total sum relation, subset search |
| Goal | Convert the statement into a known math pattern |
| Code hint | Use bitmask subset enumeration. |
| Complexity | O(2^n times n) or O(2^n) |


#### Approach Logic


1. Let total sum be `S`.
2. If one group sum is `x`, other group sum is `S - x`.
3. Difference is `abs(S - 2x)`.
4. Try all subsets because n is small.
5. This is algebra plus brute force based on constraints.


#### 🔎 Dry Run

```text
weights = 3 2 7
total = 12
choose subset 3 plus 2 = 5
other = 7
difference = abs(12 - 2*5) = 2
```

### 🧩 Practice Problem: [Codeforces 1360D Buying Shovels](https://codeforces.com/problemset/problem/1360/D)

| Field | Details |
|---|---|
| Topic | Algebra |
| Main concepts | factor choice, minimize packages, divisor constraint |
| Goal | Convert the statement into a known math pattern |
| Code hint | Scan divisors up to sqrt n. |
| Complexity | O(sqrt n) per test |


#### Approach Logic


1. Need split `n` shovels into packages of equal size.
2. Package size must divide `n`.
3. Choose largest divisor not exceeding `k`.
4. Answer is `n / chosenDivisor`.
5. This uses algebraic rearrangement and divisors.


#### 🔎 Dry Run

```text
n = 12, k = 5
divisors not above 5 are 1 2 3 4
largest is 4
answer = 12 / 4 = 3 packages
```

---

## 13. Quadratic Formula

### 🧮 Formula

For:

```text
ax^2 + bx + c = 0
```

Roots:

```text
x = (-b plus or minus sqrt(b^2 - 4ac)) / (2a)
```

Discriminant:

```text
D = b^2 - 4ac
```

### Cases

```text
D > 0: two real roots
D = 0: one real root
D < 0: no real roots
```

### Example

```text
x^2 - 5x + 6 = 0
a = 1, b = -5, c = 6
D = 25 - 24 = 1
x = (5 plus or minus 1) / 2
x = 3 or 2
```

### 💻 C++ Helper


#### 💻 C++ Code

```cpp
vector<double> quadratic(double a, double b, double c) {
    double D = b * b - 4 * a * c;
    vector<double> roots;
    if (D < 0) return roots;
    roots.push_back((-b + sqrt(D)) / (2 * a));
    if (D > 0) roots.push_back((-b - sqrt(D)) / (2 * a));
    return roots;
}
```


### CP Pattern

Use quadratic logic when:

```text
answer depends on triangular number
x(x + 1) / 2 <= n
```

This gives approximately:

```text
x around sqrt(2n)
```

### 🧩 Practice Problem: [CSES Number Spiral](https://cses.fi/problemset/task/1071)

| Field | Details |
|---|---|
| Topic | Quadratic Formula |
| Main concepts | square layers, parity, coordinate formula |
| Goal | Convert the statement into a known math pattern |
| Code hint | Use layer equals max(row, col). |
| Complexity | O(1) |


#### Approach Logic


1. Each layer is based on maximum of row and column.
2. Layer end value is square of layer number.
3. The direction depends on odd or even layer.
4. Though not solving roots, it trains square layer formulas.


#### 🔎 Dry Run

```text
row = 4, col = 2
layer = 4
layer square = 16
even layer direction uses row as increasing side
answer derived from 16 and offset
```

### 🧩 Practice Problem: [Codeforces 1352C K-th Not Divisible by n](https://codeforces.com/problemset/problem/1352/C)

| Field | Details |
|---|---|
| Topic | Quadratic Formula |
| Main concepts | counting formula, inverse reasoning |
| Goal | Convert the statement into a known math pattern |
| Code hint | Use derived formula. |
| Complexity | O(1) |


#### Approach Logic


1. Need kth positive integer not divisible by n.
2. Among numbers up to x, count not divisible by n is `x - floor(x / n)`.
3. Find smallest x such that this count is at least k.
4. Formula solution exists: answer is `k + floor((k - 1)/(n - 1))`.


#### 🔎 Dry Run

```text
n = 3, k = 7
numbers not divisible by 3:
1 2 4 5 7 8 10
answer = 10
formula = 7 + floor(6 / 2) = 10
```

---

## 14. Geometry Basics

### 🧮 Formulas

Rectangle:

```text
area = length * width
perimeter = 2(length + width)
```

Triangle:

```text
area = base * height / 2
```

Circle:

```text
area = pi * r^2
circumference = 2 * pi * r
```

Distance between points:

```text
distance = sqrt((x2 - x1)^2 + (y2 - y1)^2)
```

Slope:

```text
slope = (y2 - y1) / (x2 - x1)
```

Vector cross product:

```text
cross(a, b) = ax * by - ay * bx
```

Triangle doubled area:

```text
abs(cross(b - a, c - a))
```

### 💻 C++ Helpers


#### 💻 C++ Code

```cpp
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

long long dist2(Point a, Point b) {
    long long dx = a.x - b.x;
    long long dy = a.y - b.y;
    return dx * dx + dy * dy;
}
```


### 🧠 Pattern

If only comparison is needed, avoid `sqrt`:

```text
compare squared distances instead
```

### 🧩 Practice Problem: [CSES Point Location Test](https://cses.fi/problemset/task/2189)

| Field | Details |
|---|---|
| Topic | Geometry |
| Main concepts | cross product, orientation |
| Goal | Convert the statement into a known math pattern |
| Code hint | Use integer cross product. |
| Complexity | O(1) per query |


#### Approach Logic


1. Given directed line from point A to point B and query point C.
2. Compute cross product of vectors AB and AC.
3. If cross is positive, C is on left side.
4. If cross is negative, C is on right side.
5. If cross is zero, points are collinear.


#### 🔎 Dry Run

```text
A = (0,0), B = (4,0), C = (2,2)
AB = (4,0), AC = (2,2)
cross = 4*2 - 0*2 = 8
positive so C is left
```

### 🧩 Practice Problem: [CSES Polygon Area](https://cses.fi/problemset/task/2191)

| Field | Details |
|---|---|
| Topic | Geometry |
| Main concepts | shoelace formula, cross product sum |
| Goal | Convert the statement into a known math pattern |
| Code hint | Use long long cross sum. |
| Complexity | O(n) |


#### Approach Logic


1. Traverse polygon vertices in order.
2. Add `xi * y_next - yi * x_next` for every edge.
3. Absolute value of this sum is twice the area.
4. Print doubled area if problem asks doubled area, otherwise divide by two carefully.


#### 🔎 Dry Run

```text
Triangle (0,0), (4,0), (0,3)
sum = 0 + 12 + 0 = 12
area = 12 / 2 = 6
```

---

## 15. Big O Mathematics

### Common complexities

```text
O(1)        constant
O(log n)    halving
O(n)        one loop
O(n log n)  sorting or divide and conquer with linear merge
O(n^2)      nested loops
O(2^n)      subsets
O(n!)       permutations
```

### Loop formulas

Full nested loop:

```text
n * n = n^2
```

Triangular loop:

```text
1 + 2 + ... + n = n(n + 1) / 2 = O(n^2)
```

Halving loop:

```text
n, n/2, n/4, ..., 1 = O(log n)
```

### Mental constraint guide

```text
n <= 10        O(n!) or O(2^n) may pass
n <= 20        O(2^n) may pass
n <= 500       O(n^3) may pass
n <= 5000      O(n^2) may pass
n <= 200000    O(n log n) usually needed
n <= 1000000   O(n) or O(n log n)
n >= 1000000000 O(log n) or O(1)
```

### 🧩 Practice Problem: [CSES Apartments](https://cses.fi/problemset/task/1084)

| Field | Details |
|---|---|
| Topic | Big O |
| Main concepts | sorting, two pointers, complexity reduction |
| Goal | Convert the statement into a known math pattern |
| Code hint | Sort then two pointers. |
| Complexity | O(n log n plus m log m) |


#### Approach Logic


1. Sort applicants and apartments.
2. Use two pointers.
3. If apartment fits applicant, match both.
4. If apartment too small, move apartment pointer.
5. If apartment too large, move applicant pointer.
6. Complexity is sorting plus linear scan.


#### 🔎 Dry Run

```text
applicants = 60 45 80
apartments = 30 60 75
k = 5
45 cannot use 30, move apartment
45 cannot use 60 if range is 40 to 50, move applicant
60 matches 60
answer includes this match
```

### 🧩 Practice Problem: [CSES Ferris Wheel](https://cses.fi/problemset/task/1090)

| Field | Details |
|---|---|
| Topic | Big O |
| Main concepts | greedy, sorting, two pointers |
| Goal | Convert the statement into a known math pattern |
| Code hint | Sort and use two pointers. |
| Complexity | O(n log n) |


#### Approach Logic


1. Sort weights.
2. Pair lightest with heaviest when possible.
3. If they fit, use one gondola for both.
4. Otherwise heaviest goes alone.
5. Move pointers and count gondolas.


#### 🔎 Dry Run

```text
weights = 2 3 7 9
limit = 10
2 plus 9 too big so 9 alone
2 plus 7 fits so pair
3 alone
answer = 3
```

---

## 16. CP Problem Solving Framework

### Universal checklist


### 🧠 Pattern recognition table

| Problem phrase | Think |
|---|---|
| after many steps | modulo |
| minimum groups | ceiling division |
| repeated multiplication | binary exponentiation |
| divide under mod | modular inverse |
| many range sums | prefix sum |
| common divisor | gcd |
| cycles meet | lcm |
| choose objects | combination |
| order arrangements | permutation |
| halves each time | logarithm |
| loop sum | summation formula |
| prime factors | divisor formula |
| maintain truth | invariant |
| answer monotonic | binary search on answer |
| total minus invalid | complement counting |

### Constraints to solution table

| Constraint | Usually possible |
|---|---|
| n <= 10 | permutations or exhaustive search |
| n <= 20 | bitmask subsets |
| n <= 500 | cubic DP or Floyd |
| n <= 5000 | quadratic |
| n <= 200000 | n log n |
| n <= 1000000 | linear or sieve |
| n up to 1e9 | sqrt, log, or formula |
| n up to 1e18 | log, digit DP, math |

### Debugging checklist

1. Are indices zero based or one based?
2. Is overflow possible?
3. Does modulo need normalization?
4. Are edge cases `0` and `1` handled?
5. Is the answer type `long long`?
6. Can multiplication use `__int128`?
7. Did you reset data between test cases?
8. Is the formula valid for all constraints?


---

## 17. Candidate Master Pattern Library

This section adds deeper patterns that often separate beginner math from CM direction.

### 🧠 Pattern 1: Invariant

An invariant is something that remains true after every operation.


| Signal | Example thought |
|---|---|
| can perform operation many times | what remains unchanged |
| transform array | sum parity or gcd may stay fixed |
| game or moves | parity may decide winner |

### 🧠 Pattern 2: Complement Counting

Count everything, subtract bad cases.


### 🧠 Pattern 3: Binary Search on Answer

If answer has monotonic property, binary search it.


| Requirement | Meaning |
|---|---|
| Search space | possible answer values |
| Check function | tells if candidate works |
| Monotonic | once true always true, or once false always false |

### 🧠 Pattern 4: Difference Array

For many range additions, use difference array.

```text
diff[l] += x
diff[r + 1] -= x
final array = prefix of diff
```


### 🧠 Pattern 5: Prefix with Frequency

Sometimes prefix sum is not enough. Use prefix counts.

```text
count of value v in range l r = pref[v][r] - pref[v][l - 1]
```

### 🧠 Pattern 6: GCD of Differences

For shifted arrays:

```text
gcd(a1 + x, a2 + x, ..., an + x)
= gcd(a1 + x, a2 - a1, a3 - a1, ..., an - a1)
```

This is important for Codeforces Row GCD style problems.

### 🧠 Pattern 7: Prime Exponent Thinking

If:

```text
n = p1^a1 * p2^a2 * ... * pk^ak
```

Then many properties become exponent problems:

| Question | Formula |
|---|---|
| divisor count | product of ai plus one |
| perfect square | all ai even |
| perfect cube | all ai divisible by three |
| gcd exponents | min exponents |
| lcm exponents | max exponents |

### 🧠 Pattern 8: Modulo State Compression

If process repeats based on remainder, track remainder state.

Examples:

- prefix sum modulo k
- cycle detection
- DP by remainder
- divisibility tests

### 🧠 Pattern 9: Linear Contribution

Instead of counting each object directly, count how much each object contributes.


This is common in subarray contribution problems.

### 🧠 Pattern 10: Indicator Variables

For expected value:

```text
E[sum Xi] = sum E[Xi]
```

Even if events are dependent, linearity still works.


---

## 18. Advanced Practice Problems With Approach Flow

### 🧩 Practice Problem: [CSES Factory Machines](https://cses.fi/problemset/task/1620)

| Field | Details |
|---|---|
| Topic | Binary Search on Answer |
| Main concepts | binary search, monotonic predicate, production count |
| Goal | Convert the statement into a known math pattern |
| Code hint | Binary search minimum time. |
| Complexity | O(n log answer) |


#### Approach Logic


     1. If time `t` is enough to produce required products, any larger time is also enough.
     2. This monotonic property allows binary search.
     3. Check function sums `t / machineTime[i]`.
     4. Stop early if count reaches target to avoid overflow.


#### 🔎 Dry Run

```text
machines = 2 3 7
target = 5
time = 6
produced = 6/2 + 6/3 + 6/7 = 3 + 2 + 0 = 5
time 6 works
try smaller time
```

### 🧩 Practice Problem: [CSES Increasing Array Queries](https://cses.fi/problemset/task/2416)

| Field | Details |
|---|---|
| Topic | Difference Array |
| Main concepts | prefix style thinking, monotonic stack, contribution |
| Goal | Convert the statement into a known math pattern |
| Code hint | Study after basic prefix sums. |
| Complexity | Advanced |


#### Approach Logic


     1. This is more advanced than basic prefix sum.
     2. It asks how much must be added to make subarray non decreasing.
     3. Use structure over next greater elements and prefix contributions.
     4. Learn it after mastering prefix and monotonic stack.


#### 🔎 Dry Run

```text
array = 3 1 2
to make full range non decreasing:
1 becomes 3 add 2
2 becomes 3 add 1
total = 3
```

### 🧩 Practice Problem: [CSES Counting Coprime Pairs](https://cses.fi/problemset/task/2417)

| Field | Details |
|---|---|
| Topic | Inclusion Exclusion |
| Main concepts | mobius, inclusion exclusion, coprime counting |
| Goal | Convert the statement into a known math pattern |
| Code hint | Use divisor counts and Mobius. |
| Complexity | O(maxA log maxA) |


#### Approach Logic


     1. Count how many numbers are divisible by each d.
     2. Number of pairs with gcd exactly one can be counted using Mobius inversion.
     3. This is advanced number theory.
     4. Start by understanding divisor multiples and inclusion exclusion.


#### 🔎 Dry Run

```text
numbers = 2 3 4
coprime pairs:
2 and 3 yes
2 and 4 no
3 and 4 yes
answer = 2
```

### 🧩 Practice Problem: [CSES Nim Game I](https://cses.fi/problemset/task/1730)

| Field | Details |
|---|---|
| Topic | Game Theory |
| Main concepts | xor invariant, impartial games |
| Goal | Convert the statement into a known math pattern |
| Code hint | Xor all piles. |
| Complexity | O(n) |


#### Approach Logic


     1. Compute xor of all pile sizes.
     2. If xor is zero, current player loses with perfect play.
     3. Otherwise current player wins.
     4. This is a core game theory pattern.


#### 🔎 Dry Run

```text
piles = 3 4 5
xor = 3 xor 4 xor 5 = 2
xor is non zero
first player wins
```

### 🧩 Practice Problem: [Codeforces 1096F Inversion Expectation](https://codeforces.com/problemset/problem/1096/F)

| Field | Details |
|---|---|
| Topic | Expected Value |
| Main concepts | linearity of expectation, inversions, modular probability |
| Goal | Convert the statement into a known math pattern |
| Code hint | Use linearity of expectation. |
| Complexity | Advanced |


#### Approach Logic


     1. Expected inversions can be calculated pair by pair.
     2. For each pair, add probability that pair is inverted.
     3. Known values contribute 0 or 1.
     4. Unknown values contribute probability under remaining permutation.
     5. This is advanced but very important for 1800 plus.


#### 🔎 Dry Run

```text
array = [2, unknown, 1]
pair 2 and 1 is always inversion
pairs with unknown depend on probability
total expectation is sum of pair probabilities
```

---

## 19. Final Formula Sheet

```text
ceil(a / b) = (a + b - 1) / b
ceil(a / b) = a / b + (a % b != 0)

(a + b) mod M = ((a mod M) + (b mod M)) mod M
(a - b) mod M = ((a mod M) - (b mod M) + M) mod M
(a * b) mod M = ((a mod M) * (b mod M)) mod M

x^n by binary exponentiation = O(log n)

a^(-1) mod M = a^(M - 2) mod M, when M is prime

gcd(a, b) = gcd(b, a mod b)
lcm(a, b) = a / gcd(a, b) * b

a_n arithmetic = a_1 + (n - 1)d
S_n arithmetic = n(a_1 + a_n) / 2

a_n geometric = a_1 * r^(n - 1)
S_n geometric = a_1(r^n - 1) / (r - 1)

1 + 2 + ... + n = n(n + 1) / 2
1^2 + 2^2 + ... + n^2 = n(n + 1)(2n + 1) / 6
1^3 + 2^3 + ... + n^3 = [n(n + 1) / 2]^2

P(n, r) = n! / (n - r)!
C(n, r) = n! / (r!(n - r)!)

stars and bars:
x1 + x2 + ... + xk = n
ways = C(n + k - 1, k - 1)

If n = p1^a1 * p2^a2 * ... * pk^ak:
number_of_divisors = (a1 + 1)(a2 + 1)...(ak + 1)

bits = floor(log2(n)) + 1
digits = floor(log10(n)) + 1

Distance = sqrt((x2 - x1)^2 + (y2 - y1)^2)
Squared distance = (x2 - x1)^2 + (y2 - y1)^2
Cross product = ax * by - ay * bx

Expected value:
E[X + Y] = E[X] + E[Y]

Binary search on answer:
find minimum x such that check(x) is true
```


---

## 20. Practice Roadmap

### Level 1: Foundation

| Order | Topic | Problems |
|---:|---|---|
| 1 | Ceiling division and simple math | Codeforces 151A, Codeforces 919A |
| 2 | Modulo and cycles | Codeforces 913A |
| 3 | Prefix sum | CSES Static Range Sum Queries |
| 4 | GCD and LCM | CSES Common Divisors |
| 5 | Basic prime checking | CSES Counting Divisors |

### Level 2: Core CP Math

| Order | Topic | Problems |
|---:|---|---|
| 1 | Binary exponentiation | CSES Exponentiation |
| 2 | Nested exponentiation | CSES Exponentiation II |
| 3 | Modular inverse | CSES Binomial Coefficients |
| 4 | Stars and bars | CSES Distributing Apples |
| 5 | Prime factorization | Codeforces 546D |

### Level 3: Candidate Master Direction

| Order | Topic | Problems |
|---:|---|---|
| 1 | GCD transformation | Codeforces Row GCD |
| 2 | Binary search answer | CSES Factory Machines |
| 3 | Geometry cross product | CSES Point Location Test |
| 4 | Shoelace formula | CSES Polygon Area |
| 5 | Game theory xor | CSES Nim Game I |
| 6 | Expected value | Codeforces 1096F |
| 7 | Inclusion exclusion | CSES Counting Coprime Pairs |

### Recommended Weekly Plan

| Day | Work |
|---|---|
| Day 1 | Read one topic and implement helper from memory |
| Day 2 | Solve two easy problems from that topic |
| Day 3 | Solve one medium problem and write dry run |
| Day 4 | Revisit wrong submissions and update notes |
| Day 5 | Mix topic with previous topic |
| Day 6 | Timed practice |
| Day 7 | Review formulas and rebuild cheat sheet |

### Final Mental Checklist Before Coding

Ask these questions:

1. Can I replace simulation with a formula?
2. Is there a modulo cycle?
3. Is this repeated multiplication?
4. Can I use binary exponentiation?
5. Is division under modulo actually modular inverse?
6. Can a range query be answered by prefix sum?
7. Is there a GCD or LCM hidden in the problem?
8. Can I count total minus bad?
9. Does order matter?
10. Is the loop actually a known summation?
11. Can I avoid overflow using `long long` or `__int128`?
12. Can I divide before multiplying?
13. Are there edge cases: 0, 1, negative, max constraints?
14. Is there a monotonic check for binary search?
15. Is there an invariant that never changes?


---

## 21. Compact C++ Template


#### 💻 C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;
const ll MOD = 1000000007LL;

ll ceilDiv(ll a, ll b) {
    return a / b + (a % b != 0);
}

ll norm(ll x, ll mod) {
    x %= mod;
    if (x < 0) x += mod;
    return x;
}

ll modPow(ll base, ll exp, ll mod) {
    ll res = 1 % mod;
    base %= mod;
    while (exp > 0) {
        if (exp & 1) res = (__int128)res * base % mod;
        base = (__int128)base * base % mod;
        exp >>= 1;
    }
    return res;
}

ll modInversePrime(ll a, ll mod) {
    return modPow(a, mod - 2, mod);
}

ll gcdll(ll a, ll b) {
    while (b) {
        ll r = a % b;
        a = b;
        b = r;
    }
    return a;
}

ll lcmll(ll a, ll b) {
    return a / gcdll(a, b) * b;
}

bool isPrime(ll n) {
    if (n < 2) return false;
    for (ll d = 2; d * d <= n; d++) {
        if (n % d == 0) return false;
    }
    return true;
}

vector<pair<ll,int>> factorize(ll n) {
    vector<pair<ll,int>> f;
    for (ll d = 2; d * d <= n; d++) {
        if (n % d == 0) {
            int cnt = 0;
            while (n % d == 0) {
                n /= d;
                cnt++;
            }
            f.push_back({d, cnt});
        }
    }
    if (n > 1) f.push_back({n, 1});
    return f;
}

vector<ll> buildPrefix(const vector<int>& a) {
    vector<ll> pref(a.size() + 1, 0);
    for (int i = 0; i < (int)a.size(); i++) {
        pref[i + 1] = pref[i] + a[i];
    }
    return pref;
}

ll rangeSum(const vector<ll>& pref, int l, int r) {
    return pref[r + 1] - pref[l];
}

struct Point {
    ll x, y;
};

ll cross(Point a, Point b, Point c) {
    ll x1 = b.x - a.x;
    ll y1 = b.y - a.y;
    ll x2 = c.x - a.x;
    ll y2 = c.y - a.y;
    return x1 * y2 - y1 * x2;
}

ll dist2(Point a, Point b) {
    ll dx = a.x - b.x;
    ll dy = a.y - b.y;
    return dx * dx + dy * dy;
}
```


---

## 22. Java Helper Pack


#### ☕ Java Code

```java
import java.io.*;
import java.util.*;

public class Main {
    static final long MOD = 1000000007L;

    static long modPow(long base, long exp, long mod) {
        long res = 1 % mod;
        base %= mod;
        while (exp > 0) {
            if ((exp & 1L) == 1L) res = (res * base) % mod;
            base = (base * base) % mod;
            exp >>= 1;
        }
        return res;
    }

    static long gcd(long a, long b) {
        while (b != 0) {
            long r = a % b;
            a = b;
            b = r;
        }
        return a;
    }

    static long lcm(long a, long b) {
        return a / gcd(a, b) * b;
    }

    static long norm(long x, long mod) {
        x %= mod;
        if (x < 0) x += mod;
        return x;
    }

    static long ceilDiv(long a, long b) {
        return a / b + (a % b != 0 ? 1 : 0);
    }
}
```


---

END

---

# 🔥 Detailed Problem Workbook with C++ + Index-by-Index Dry Runs

## Problem 1: Codeforces 151A — Soft Drinking

### Pattern

```text
Bottleneck resource + integer grouping
```

### Mental Map

```text
Recognition:
Bottleneck resource + integer grouping

Think:
1. Identify the mathematical pattern.
2. Convert the statement into formula/invariant.
3. Code helper safely.
4. Dry run using exact indices and values.
```

### Problem Explanation

You have `n` friends. To make one toast for one friend, you need:

```text
nl ml drink + 1 lime slice + np grams salt
```

Find the maximum number of complete rounds of toasts every friend can get.

### Input

```text
n k l c d p nl np
```

### Output

```text
Maximum number of toasts per friend
```

### Example

```text
Input:
3 4 5 10 8 100 3 1

Output:
2
```

### Idea

Compute how many total toasts each resource can support:

```text
drink_toasts = (k * l) / nl
lime_toasts  = c * d
salt_toasts  = p / np
```

The limiting resource decides total toasts. Divide by `n` friends.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, k, l, c, d, p, nl, np;
    cin >> n >> k >> l >> c >> d >> p >> nl >> np;

    int drinkToasts = (k * l) / nl;
    int limeToasts = c * d;
    int saltToasts = p / np;

    int totalToasts = min({drinkToasts, limeToasts, saltToasts});
    cout << totalToasts / n << '\n';

    return 0;
}
```

### Index-by-Index Dry Run

```text
n = 3 friends
k = 4 bottles
l = 5 ml each bottle
c = 10 limes
d = 8 slices per lime
p = 100 grams salt
nl = 3 ml per toast
np = 1 gram salt per toast
```

| Step | Calculation | Result |
|---:|---|---:|
| 1 | total drink = `k * l = 4 * 5` | 20 ml |
| 2 | drink toasts = `20 / 3` | 6 |
| 3 | lime toasts = `10 * 8` | 80 |
| 4 | salt toasts = `100 / 1` | 100 |
| 5 | bottleneck = `min(6, 80, 100)` | 6 |
| 6 | per friend = `6 / 3` | 2 |

### Mistakes to Avoid

```text
Do not divide each resource by n first.
First compute total possible toasts, then divide by friends.
```

---

## Problem 2: CSES Static Range Sum Queries

### Pattern

```text
Prefix sum for many range sum queries
```

### Mental Map

```text
Recognition:
Prefix sum for many range sum queries

Think:
1. Identify the mathematical pattern.
2. Convert the statement into formula/invariant.
3. Code helper safely.
4. Dry run using exact indices and values.
```

### Problem Explanation

Given an array and many queries `[l, r]`, find sum from `l` to `r` quickly.

### Input

```text
n q
a1 a2 ... an
l1 r1
l2 r2
...
```

### Output

```text
sum(l, r) for each query
```

### Example

```text
Input:
5 3
2 4 1 7 3
1 3
2 4
3 5

Output:
7
12
11
```

### Formula

For 1-based prefix:

```text
pref[0] = 0
pref[i] = pref[i - 1] + a[i]
sum(l, r) = pref[r] - pref[l - 1]
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, q;
    cin >> n >> q;

    vector<long long> pref(n + 1, 0);

    for (int i = 1; i <= n; i++) {
        long long x;
        cin >> x;
        pref[i] = pref[i - 1] + x;
    }

    while (q--) {
        int l, r;
        cin >> l >> r;
        cout << pref[r] - pref[l - 1] << '\n';
    }

    return 0;
}
```

### Index-by-Index Dry Run

Array:

```text
index:  1  2  3  4  5
value:  2  4  1  7  3
```

Build prefix:

| i | a[i] | pref[i - 1] | pref[i] |
|---:|---:|---:|---:|
| 0 | - | - | 0 |
| 1 | 2 | 0 | 2 |
| 2 | 4 | 2 | 6 |
| 3 | 1 | 6 | 7 |
| 4 | 7 | 7 | 14 |
| 5 | 3 | 14 | 17 |

Query `[2, 4]`:

```text
sum = pref[4] - pref[1]
    = 14 - 2
    = 12
```

### Why This Works

`pref[r]` contains sum from `1..r`.  
`pref[l - 1]` contains unwanted left part `1..l-1`.  
Subtracting leaves only `l..r`.

---

## Problem 3: CSES Exponentiation

### Pattern

```text
Binary exponentiation + modulo
```

### Mental Map

```text
Recognition:
Binary exponentiation + modulo

Think:
1. Identify the mathematical pattern.
2. Convert the statement into formula/invariant.
3. Code helper safely.
4. Dry run using exact indices and values.
```

### Problem Explanation

For each query, compute:

```text
a^b mod 1e9+7
```

`b` can be large, so normal multiplication is too slow.

### Input

```text
t
a b
a b
...
```

### Output

```text
a^b modulo 1e9+7 for each query
```

### Example

```text
Input:
3
3 4
2 8
5 0

Output:
81
256
1
```

### Core Idea

Use binary representation of exponent.

```text
13 = 8 + 4 + 1
3^13 = 3^8 * 3^4 * 3^1
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

const long long MOD = 1000000007LL;

long long modPow(long long base, long long exp) {
    long long ans = 1;
    base %= MOD;

    while (exp > 0) {
        if (exp & 1) {
            ans = ans * base % MOD;
        }
        base = base * base % MOD;
        exp >>= 1;
    }

    return ans;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;

    while (t--) {
        long long a, b;
        cin >> a >> b;
        cout << modPow(a, b) << '\n';
    }

    return 0;
}
```

### Index-by-Index Dry Run: `3^13`

| Step | exp | base | ans before | exp odd? | ans after | next base |
|---:|---:|---:|---:|---|---:|---:|
| 1 | 13 | 3 | 1 | yes | 3 | 9 |
| 2 | 6 | 9 | 3 | no | 3 | 81 |
| 3 | 3 | 81 | 3 | yes | 243 | 6561 |
| 4 | 1 | 6561 | 243 | yes | 1594323 | 43046721 |
| 5 | 0 | - | 1594323 | stop | 1594323 | - |

### Mistakes to Avoid

```text
Always apply modulo after multiplication.
Use long long.
For very large multiplication, use __int128 if MOD can be near 1e18.
```

---

## Problem 4: CSES Binomial Coefficients

### Pattern

```text
nCr under prime modulo
```

### Mental Map

```text
Recognition:
nCr under prime modulo

Think:
1. Identify the mathematical pattern.
2. Convert the statement into formula/invariant.
3. Code helper safely.
4. Dry run using exact indices and values.
```

### Problem Explanation

Answer many queries:

```text
C(n, r) mod 1e9+7
```

### Input

```text
q
n r
n r
...
```

### Output

```text
C(n, r) modulo MOD for each query
```

### Formula

```text
C(n, r) = n! / (r! * (n-r)!)
```

Under modulo division:

```text
x / y mod MOD = x * inverse(y) mod MOD
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

const int MAXN = 1000000;
const long long MOD = 1000000007LL;

long long fact[MAXN + 1], invFact[MAXN + 1];

long long modPow(long long base, long long exp) {
    long long ans = 1;
    while (exp > 0) {
        if (exp & 1) ans = ans * base % MOD;
        base = base * base % MOD;
        exp >>= 1;
    }
    return ans;
}

void build() {
    fact[0] = 1;
    for (int i = 1; i <= MAXN; i++) {
        fact[i] = fact[i - 1] * i % MOD;
    }

    invFact[MAXN] = modPow(fact[MAXN], MOD - 2);
    for (int i = MAXN - 1; i >= 0; i--) {
        invFact[i] = invFact[i + 1] * (i + 1) % MOD;
    }
}

long long nCr(int n, int r) {
    if (r < 0 || r > n) return 0;
    return fact[n] * invFact[r] % MOD * invFact[n - r] % MOD;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    build();

    int q;
    cin >> q;
    while (q--) {
        int n, r;
        cin >> n >> r;
        cout << nCr(n, r) << '\n';
    }

    return 0;
}
```

### Dry Run: `C(5, 2)`

| Part | Value |
|---|---:|
| `fact[5]` | 120 |
| `fact[2]` | 2 |
| `fact[3]` | 6 |
| denominator | 12 |
| actual result | 10 |

Modulo version computes:

```text
fact[5] * inverse(fact[2]) * inverse(fact[3]) mod MOD
```

### Recognition Signal

```text
many nCr queries
large n
modulo prime
```

Think factorial + inverse factorial.

---

## Problem 5: CSES Common Divisors

### Pattern

```text
Largest divisor that appears in at least two numbers
```

### Mental Map

```text
Recognition:
Largest divisor that appears in at least two numbers

Think:
1. Identify the mathematical pattern.
2. Convert the statement into formula/invariant.
3. Code helper safely.
4. Dry run using exact indices and values.
```

### Problem Explanation

Given `n` numbers, find the greatest integer that divides at least two numbers.

### Example

```text
Input:
3
6 10 15

Output:
5
```

### Brute Force

Check every pair and compute gcd.

```text
O(n^2 log A) too slow
```

### Optimized Idea

Count frequency of each number.  
For each possible divisor `d` from max value down to 1, count how many input numbers are multiples of `d`.

First `d` with count at least 2 is answer.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<int> a(n);
    int maxA = 0;

    for (int i = 0; i < n; i++) {
        cin >> a[i];
        maxA = max(maxA, a[i]);
    }

    vector<int> freq(maxA + 1, 0);
    for (int x : a) freq[x]++;

    for (int d = maxA; d >= 1; d--) {
        int countMultiples = 0;

        for (int multiple = d; multiple <= maxA; multiple += d) {
            countMultiples += freq[multiple];
        }

        if (countMultiples >= 2) {
            cout << d << '\n';
            return 0;
        }
    }

    return 0;
}
```

### Index-by-Index Dry Run

Input:

```text
a = [6, 10, 15]
```

Frequency:

| value | freq |
|---:|---:|
| 6 | 1 |
| 10 | 1 |
| 15 | 1 |

Check from large to small:

| d | Multiples present | Count | Valid? |
|---:|---|---:|---|
| 15 | 15 | 1 | no |
| 14 | - | 0 | no |
| 13 | - | 0 | no |
| 12 | - | 0 | no |
| 11 | - | 0 | no |
| 10 | 10 | 1 | no |
| 9 | - | 0 | no |
| 8 | - | 0 | no |
| 7 | - | 0 | no |
| 6 | 6 | 1 | no |
| 5 | 10, 15 | 2 | yes |

Answer = `5`.

---

## Problem 6: CSES Counting Divisors

### Pattern

```text
Prime factorization + divisor count formula
```

### Mental Map

```text
Recognition:
Prime factorization + divisor count formula

Think:
1. Identify the mathematical pattern.
2. Convert the statement into formula/invariant.
3. Code helper safely.
4. Dry run using exact indices and values.
```

### Formula

If:

```text
x = p1^a1 * p2^a2 * ... * pk^ak
```

Then:

```text
divisors = (a1 + 1) * (a2 + 1) * ... * (ak + 1)
```

### Example

```text
18 = 2^1 * 3^2
number of divisors = (1 + 1) * (2 + 1) = 6
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

const int MAXA = 1000000;
int spf[MAXA + 1];

void buildSPF() {
    for (int i = 0; i <= MAXA; i++) spf[i] = i;

    for (int i = 2; i * i <= MAXA; i++) {
        if (spf[i] == i) {
            for (long long j = 1LL * i * i; j <= MAXA; j += i) {
                if (spf[j] == j) spf[j] = i;
            }
        }
    }
}

int countDivisors(int x) {
    int ans = 1;

    while (x > 1) {
        int p = spf[x];
        int exponent = 0;

        while (x % p == 0) {
            x /= p;
            exponent++;
        }

        ans *= (exponent + 1);
    }

    return ans;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    buildSPF();

    int q;
    cin >> q;
    while (q--) {
        int x;
        cin >> x;
        cout << countDivisors(x) << '\n';
    }

    return 0;
}
```

### Index-by-Index Dry Run: `x = 72`

```text
72 = 2^3 * 3^2
```

| Step | x before | spf[x] | exponent count | x after division | ans update |
|---:|---:|---:|---:|---:|---:|
| 1 | 72 | 2 | 3 | 9 | `1 * (3 + 1) = 4` |
| 2 | 9 | 3 | 2 | 1 | `4 * (2 + 1) = 12` |

Answer = `12` divisors.

### Divisors of 72

```text
1, 2, 3, 4, 6, 8, 9, 12, 18, 24, 36, 72
```

---

## Problem 7: CSES Number Spiral

### Pattern

```text
Layer + parity formula
```

### Mental Map

```text
Recognition:
Layer + parity formula

Think:
1. Identify the mathematical pattern.
2. Convert the statement into formula/invariant.
3. Code helper safely.
4. Dry run using exact indices and values.
```

### Problem Explanation

Given row `y` and column `x`, find value at that cell in the number spiral.

### Input

```text
t
y x
...
```

### Output

```text
value at row y column x
```

### Formula Thinking

```text
layer = max(y, x)
layer square = layer * layer
```

Direction depends on whether layer is odd or even.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;

    while (t--) {
        long long y, x;
        cin >> y >> x;

        long long layer = max(y, x);
        long long square = layer * layer;
        long long ans;

        if (layer % 2 == 0) {
            if (y == layer) {
                ans = square - x + 1;
            } else {
            ans = (layer - 1) * (layer - 1) + y;
        }
    } else {
    if (x == layer) {
        ans = square - y + 1;
    } else {
    ans = (layer - 1) * (layer - 1) + x;
}
}

cout << ans << '\n';
}

return 0;
}
```

### Index-by-Index Dry Run: `y = 2, x = 3`

| Step | Value |
|---:|---|
| 1 | `layer = max(2, 3) = 3` |
| 2 | `square = 3 * 3 = 9` |
| 3 | `layer` is odd |
| 4 | `x == layer`, so use `square - y + 1` |
| 5 | `9 - 2 + 1 = 8` |

Answer = `8`.

### Mental Picture

```text
1   2   9
4   3   8
5   6   7
```

Cell `(2, 3)` contains `8`.

---

## Problem 8: CSES Missing Number

### Pattern

```text
Expected sum - actual sum
```

### Mental Map

```text
Recognition:
Expected sum - actual sum

Think:
1. Identify the mathematical pattern.
2. Convert the statement into formula/invariant.
3. Code helper safely.
4. Dry run using exact indices and values.
```

### Problem Explanation

Numbers from `1` to `n` are given, but one number is missing.

### Input

```text
n
n-1 numbers
```

### Output

```text
missing number
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    long long n;
    cin >> n;

    long long expected = n * (n + 1) / 2;
    long long actual = 0;

    for (int i = 0; i < n - 1; i++) {
        long long x;
        cin >> x;
        actual += x;
    }

    cout << expected - actual << '\n';
    return 0;
}
```

### Index-by-Index Dry Run

```text
n = 5
numbers = 2 3 1 5
```

| Step | Read x | actual sum |
|---:|---:|---:|
| 1 | 2 | 2 |
| 2 | 3 | 5 |
| 3 | 1 | 6 |
| 4 | 5 | 11 |

```text
expected = 5 * 6 / 2 = 15
missing = 15 - 11 = 4
```

---

## Problem 9: CSES Factory Machines

### Pattern

```text
Binary search on answer
```

### Mental Map

```text
Recognition:
Binary search on answer

Think:
1. Identify the mathematical pattern.
2. Convert the statement into formula/invariant.
3. Code helper safely.
4. Dry run using exact indices and values.
```

### Problem Explanation

Given machine times. Machine `i` produces one item in `time[i]`. Find minimum time needed to produce at least `t` products.

### Input

```text
n t
time1 time2 ... timen
```

### Output

```text
minimum time
```

### Check Function

For a candidate time `mid`:

```text
products = sum(mid / time[i])
```

If products >= target, `mid` is enough.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool canMake(const vector<long long>& machines, long long target, long long timeLimit) {
    long long products = 0;

    for (long long m : machines) {
        products += timeLimit / m;
        if (products >= target) return true;
    }

    return false;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    long long target;
    cin >> n >> target;

    vector<long long> machines(n);
    for (long long& x : machines) cin >> x;

    long long low = 0;
    long long high = *min_element(machines.begin(), machines.end()) * target;

    while (low < high) {
        long long mid = low + (high - low) / 2;

        if (canMake(machines, target, mid)) {
            high = mid;
        } else {
        low = mid + 1;
    }
}

cout << low << '\n';
return 0;
}
```

### Index-by-Index Dry Run

```text
machines = [2, 3, 7]
target = 10
```

Search range:

```text
low = 0
high = fastest_machine * target = 2 * 10 = 20
```

| Step | low | high | mid | products by machines | total | enough? | move |
|---:|---:|---:|---:|---|---:|---|---|
| 1 | 0 | 20 | 10 | `10/2=5`, `10/3=3`, `10/7=1` | 9 | no | low = 11 |
| 2 | 11 | 20 | 15 | `7 + 5 + 2` | 14 | yes | high = 15 |
| 3 | 11 | 15 | 13 | `6 + 4 + 1` | 11 | yes | high = 13 |
| 4 | 11 | 13 | 12 | `6 + 4 + 1` | 11 | yes | high = 12 |
| 5 | 11 | 12 | 11 | `5 + 3 + 1` | 9 | no | low = 12 |

Answer = `12`.

### Recognition Signal

```text
minimum time / maximum capacity
answer space is monotonic
can check candidate answer
```

Think binary search on answer.

---

## Problem 10: CSES Point Location Test

### Pattern

```text
Cross product orientation
```

### Mental Map

```text
Recognition:
Cross product orientation

Think:
1. Identify the mathematical pattern.
2. Convert the statement into formula/invariant.
3. Code helper safely.
4. Dry run using exact indices and values.
```

### Problem Explanation

Given directed line from point `A` to point `B`, determine whether point `C` is on the left, right, or on the line.

### Input

```text
t
x1 y1 x2 y2 x3 y3
...
```

### Output

```text
LEFT / RIGHT / TOUCH
```

### Formula

```text
cross = (B - A) x (C - A)
      = (x2 - x1) * (y3 - y1) - (y2 - y1) * (x3 - x1)
```

```text
cross > 0  => LEFT
cross < 0  => RIGHT
cross == 0 => TOUCH
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;

    while (t--) {
        long long x1, y1, x2, y2, x3, y3;
        cin >> x1 >> y1 >> x2 >> y2 >> x3 >> y3;

        long long ax = x2 - x1;
        long long ay = y2 - y1;
        long long bx = x3 - x1;
        long long by = y3 - y1;

        long long cross = ax * by - ay * bx;

        if (cross > 0) cout << "LEFT\n";
        else if (cross < 0) cout << "RIGHT\n";
        else cout << "TOUCH\n";
    }

    return 0;
}
```

### Index-by-Index Dry Run

```text
A = (0, 0)
B = (4, 0)
C = (2, 3)
```

| Step | Calculation | Value |
|---:|---|---:|
| 1 | `ax = x2 - x1` | 4 |
| 2 | `ay = y2 - y1` | 0 |
| 3 | `bx = x3 - x1` | 2 |
| 4 | `by = y3 - y1` | 3 |
| 5 | `cross = ax * by - ay * bx` | `4*3 - 0*2 = 12` |
| 6 | cross > 0 | LEFT |

### Mental Picture

```text
C is above the directed line A -> B.
So C is on the LEFT side.
```

---

# ✅ Final Revision Checklist

Before solving any math problem, ask:

```text
1. Is there a direct formula?
2. Is there a repeated cycle?
3. Is there a range query?
4. Is there a gcd/lcm hidden?
5. Is the answer monotonic?
6. Is there counting with order / without order?
7. Is modulo division needed?
8. Can a loop be replaced by summation?
9. Can factorization reveal the answer?
10. Can geometry be solved by cross product?
```

---

# 🧪 Fast Practice Method

```text
Round 1: Read topic + formula.
Round 2: Code helper from memory.
Round 3: Solve easy problem.
Round 4: Dry run by index/table.
Round 5: Solve same topic after 2 days.
Round 6: Mix with previous topics.
```

> Your target is not memorizing code. Your target is recognizing the pattern in under 5 seconds.


---

## 📌 File Update Summary

```text
Updated with:
- Better visual layout
- Clickable master index
- Problem workbook index
- C++ full solutions
- Input/output sections
- More detailed explanations
- Index-by-index dry runs
- Mistakes to avoid
- Final revision checklist
```