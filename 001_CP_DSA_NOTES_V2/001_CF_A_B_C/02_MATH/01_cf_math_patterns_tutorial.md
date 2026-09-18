# Codeforces Math Patterns — Tutorial + Pattern-Spotting Guide

How to use this doc: for each pattern, read the **trigger phrases** first — those are the words in a problem statement that should make the pattern light up in your head during a contest. Then walk the dry run, look at the diagram, skim the code, and knock out 2-3 of the practice problems before moving on.

---

## 1. GCD / LCM

**Trigger phrases:** "divides evenly", "common multiple of all", "smallest number divisible by", "reduce fraction", "simplify ratio"

**Core idea:** `gcd(a,b) = gcd(b, a%b)` (Euclid). `lcm(a,b) = a/gcd(a,b) * b`. For arrays, fold gcd/lcm left to right — gcd/lcm are associative.

### Dry Run
Find `lcm(4, 6, 10)`.

```
step 1: lcm(4,6)
    gcd(4,6): 4,6 -> 6,4 -> 4,2 -> 2,0   => gcd=2
    lcm(4,6) = 4*6/2 = 12

step 2: lcm(12,10)
    gcd(12,10): 12,10 -> 10,2 -> 2,0     => gcd=2
    lcm(12,10) = 12*10/2 = 60

Answer: 60
```

```
Euclid recursion tree for gcd(48,18):
  gcd(48,18)
     |  48 = 2*18 + 12
  gcd(18,12)
     |  18 = 1*12 + 6
  gcd(12,6)
     |  12 = 2*6 + 0
  gcd(6,0)  <-- base case, answer = 6
```

### C++
```cpp
long long gcd_(long long a, long long b){ return b ? gcd_(b, a % b) : a; }
long long lcm_(long long a, long long b){ return a / gcd_(a, b) * b; } // divide first, avoid overflow

// folding over an array
long long g = 0, l = 1;
for (auto x : arr) { g = gcd_(g, x); l = lcm_(l, x); }
```
(C++17 also has `std::gcd`/`std::lcm` in `<numeric>`.)

### More problems
1. CSES – [Common Divisors](https://cses.fi/problemset/task/1081)
2. [CF 1499A](https://codeforces.com/problemset/problem/1499/A) – GCD Sum
3. CSES – [Coin Piles](https://cses.fi/problemset/task/1754)
4. [CF 1114A](https://codeforces.com/problemset/problem/1114/A) – Got Any Grapes? (LCM-flavored greedy)
5. [CF 1029A](https://codeforces.com/problemset/problem/1029/A) – Many Equal Substrings (period = LCM-like reasoning)
6. [CF 1367B](https://codeforces.com/problemset/problem/1367/B) – Even Array (parity + gcd bookkeeping)
7. [CF 1350B](https://codeforces.com/problemset/problem/1350/B) – Orac and Models (uses divisibility chains)
8. Project Euler-style: sum of multiples under N (LCM/inclusion-exclusion warmup)
9. [CF 1445A](https://codeforces.com/problemset/problem/1445/A) – Array Rearrangement (parity/gcd of sums)
10. [CF 1493B](https://codeforces.com/problemset/problem/1493/B) – Planar Reflections (recurrence, gcd-adjacent counting)

---

## 2. Modular Arithmetic & Fast Exponentiation

**Trigger phrases:** "answer modulo 1e9+7", "huge number", "count the number of ways" + big N

**Core idea:** All +,-,*,^ can be done mod `M` safely as long as you never leave `[0, M)` between operations, and division becomes multiplication by the modular inverse (via Fermat's little theorem when `M` is prime: `inv(a) = a^(M-2) mod M`). Binary exponentiation computes `a^b mod M` in `O(log b)` by squaring.

### Dry Run
Compute `3^13 mod 1000000007`.

```
13 in binary = 1101
             bit:  1   1   0   1
result starts at 1, base = 3

process bits left to right (or right to left with doubling base — shown right to left below):

b=13 (1101)   base=3        result=1
 bit0=1  -> result*=base -> result=3        base=base*base=9
 bit1=0  ->                                  base=9*9=81
 bit2=1  -> result*=base -> result=3*81=243  base=81*81=6561
 bit3=1  -> result*=base -> result=243*6561=1594323  base=...(unused, loop ends)

3^13 = 1594323  (matches direct calc: 3^13 = 1,594,323)
```

```
Binary exponentiation "doubling" diagram for exponent 13 = 1101b:

exp:     13 -> 6 -> 3 -> 1 -> 0
          |    |    |    |
        odd  even  odd  odd  (multiply result by current base when bit is 1)
base: 3 -> 9 -> 81 -> 6561      (base squares every step)
```

### C++
```cpp
const long long MOD = 1e9 + 7;

long long power(long long a, long long b, long long mod = MOD) {
    a %= mod;
    long long res = 1;
    while (b > 0) {
        if (b & 1) res = res * a % mod;
        a = a * a % mod;
        b >>= 1;
    }
    return res;
}

long long modinv(long long a, long long mod = MOD) {
    return power(a, mod - 2, mod); // only valid when mod is prime
}
```

### More problems
1. CSES – [Exponentiation](https://cses.fi/problemset/task/1095)
2. CSES – [Exponentiation II](https://cses.fi/problemset/task/1712)
3. [CF 630A](https://codeforces.com/problemset/problem/630/A) – Again Twenty Five!
4. [CF 615D](https://codeforces.com/problemset/problem/615/D) – Multiplication Table
5. [CF 1097D](https://codeforces.com/problemset/problem/1097/D) – Makoto and a Blackboard (harmonic-sum + expectation, uses modinv)
6. [CF 1097B](https://codeforces.com/problemset/problem/1097/B) – Petr and a Combination Lock (bitmask+mod)
7. [CF 1350C](https://codeforces.com/problemset/problem/1350/C) – Orac and Game of Choosing Numbers
8. [CF 559C](https://codeforces.com/problemset/problem/559/C) – Gerald and Giant Chess (uses factorial mod + Fermat inverse)
9. [CF 1391D](https://codeforces.com/problemset/problem/1391/D) – 505 (2D prefix sums + parity, mod-friendly)
10. CSES – Fibonacci Numbers

---

## 3. Primes & Sieve of Eratosthenes

**Trigger phrases:** "is prime", "for every query find if n is prime", "smallest prime factor", "up to 1e6/1e7 numbers"

**Core idea:** Precompute primality/smallest-prime-factor for all numbers up to `N` once in `O(N log log N)`, then answer each query in `O(1)` or `O(log n)`.

### Dry Run
Sieve up to 20.

```
Start: mark everything "prime" except 0,1
i=2 (prime) -> cross out multiples: 4,6,8,10,12,14,16,18,20
i=3 (prime) -> cross out: 6,9,12,15,18   (6,12,18 already crossed)
i=4 -> already crossed, skip
i=5 (still marked) -> prime! cross: 10,15,20 (all already crossed)
sqrt(20) ~ 4.47, so we stop crossing after i=4, but scanning continues
Remaining primes: 2,3,5,7,11,13,17,19
```

```
ASCII sieve grid (X = composite, . = prime), after sieving with 2 and 3:

 2  3  X  5  X  7  X  X  X 11  X 13  X  X  X 17  X 19  X
 |sieved by 2 -> every 2nd cell from 4 onward: 4,6,8,10,...
 |sieved by 3 -> every 3rd cell from 9 onward: 9,12,15,18,...
```

### C++
```cpp
const int N = 1e6 + 5;
vector<bool> is_composite(N, false);
vector<int> primes;

void sieve() {
    for (int i = 2; i < N; i++) {
        if (!is_composite[i]) {
            primes.push_back(i);
            for (long long j = (long long)i * i; j < N; j += i)
                is_composite[j] = true;
        }
    }
}

// Smallest Prime Factor sieve (lets you factorize any number in O(log n))
vector<int> spf(N);
void spf_sieve() {
    for (int i = 2; i < N; i++) {
        if (spf[i] == 0) {
            for (int j = i; j < N; j += i)
                if (spf[j] == 0) spf[j] = i;
        }
    }
}
```

### More problems
1. CSES – Next Prime (find nearest prime, uses sieve/trial division)
2. [CF 26A](https://codeforces.com/problemset/problem/26/A) – Almost Prime
3. [CF 271B](https://codeforces.com/problemset/problem/271/B) – Prime Matrix
4. [CF 483B](https://codeforces.com/problemset/problem/483/B) – Friends and Presents (binary search + inclusion-exclusion on primes)
5. [CF 1096D](https://codeforces.com/problemset/problem/1096/D) – Easy Problem (dp, primes as constraints)
6. [CF 735D](https://codeforces.com/problemset/problem/735/D) – Taxes (Goldbach-flavored prime reasoning)
7. [CF 1029F](https://codeforces.com/problemset/problem/1029/F) – Multicolored Cars (not prime — swap: [CF 1198C](https://codeforces.com/problemset/problem/1198/C) Matching Names) — practice source: SPOJ PRIME1 (segmented sieve)
8. [CF 17A](https://codeforces.com/problemset/problem/17/A) – Noldbach Problem
9. [CF 1499D](https://codeforces.com/problemset/problem/1499/D) – The Number of Pairs (divisor + prime factorization)
10. CSES – [Counting Coprime Pairs](https://cses.fi/problemset/task/2417) (uses inclusion–exclusion over prime factors)

---

## 4. Divisors & Factorization

**Trigger phrases:** "number of divisors", "sum of divisors", "factorize", "d(n)"

**Core idea:** Any `n` has at most `O(sqrt n)` divisors to check directly: loop `i` from `1` to `sqrt(n)`, if `n % i == 0` then both `i` and `n/i` are divisors. For divisor *counts/sums* over a whole range, precompute with a sieve-like pass (`for d in 1..N: for multiple in d,2d,3d..: count[multiple]++`), which is `O(N log N)` total (harmonic series).

### Dry Run
Find all divisors of 36 by trial up to `sqrt(36)=6`.

```
i=1: 36%1==0 -> divisors 1, 36
i=2: 36%2==0 -> divisors 2, 18
i=3: 36%3==0 -> divisors 3, 12
i=4: 36%4==0 -> divisors 4, 9
i=5: 36%5!=0 -> skip
i=6: 36%6==0 -> 6*6==36, only count 6 once

Divisors: {1,2,3,4,6,9,12,18,36}  (9 divisors, matches d(36) = (2+1)(2+1) since 36=2^2*3^2)
```

```
Divisor pairing diagram for n=36 (sqrt=6):

  1 ----- 36
  2 ----- 18
  3 ----- 12
  4 -----  9
  6 -----  6   <-- middle, only counted once
  ^ small i        ^ large n/i
  (only need to scan up to the midpoint)
```

### C++
```cpp
vector<long long> divisors(long long n) {
    vector<long long> res;
    for (long long i = 1; i * i <= n; i++) {
        if (n % i == 0) {
            res.push_back(i);
            if (i != n / i) res.push_back(n / i);
        }
    }
    return res;
}

// count-of-divisors for every number 1..N (sieve style)
vector<int> num_divisors(N + 1, 0);
void divisor_sieve() {
    for (int d = 1; d <= N; d++)
        for (int m = d; m <= N; m += d)
            num_divisors[m]++;
}
```

### More problems
1. CSES – [Counting Divisors](https://cses.fi/problemset/task/1713)
2. CSES – [Sum of Divisors](https://cses.fi/problemset/task/1082)
3. CSES – [Divisor Analysis](https://cses.fi/problemset/task/2182) (uses factorization formula, not brute force)
4. [CF 27E](https://codeforces.com/problemset/problem/27/E) – Number With The Given Amount Of Divisors
5. [CF 1108B](https://codeforces.com/problemset/problem/1108/B) – Divisors of Two Integers
6. [CF 1512E](https://codeforces.com/problemset/problem/1512/E) – Permutation by Sum? (skip — better) [CF 1474D](https://codeforces.com/problemset/problem/1474/D) – Cleaning the Phone (subset-sum, adjacent skill)
7. [CF 484A](https://codeforces.com/problemset/problem/484/A) – Bits (bit/divisor counting hybrid)
8. [CF 1183F](https://codeforces.com/problemset/problem/1183/F) – Topforces Strikes Back (harder, divisor + dp)
9. CSES – [Prime Multiples](https://cses.fi/problemset/task/2185) (inclusion–exclusion with divisors)
10. [CF 1029C](https://codeforces.com/problemset/problem/1029/C) – Maximal Intersection (uses divisor/range reasoning)

---

## 5. Combinatorics (nCr, permutations, inclusion–exclusion)

**Trigger phrases:** "number of ways to choose", "number of arrangements", "at least one of", "no two adjacent"

**Core idea:** Precompute factorials and inverse factorials mod `M` once (`O(N)`), then `C(n,r) = fact[n] * invfact[r] % M * invfact[n-r] % M` in `O(1)` per query. Inclusion–exclusion: to count "at least one bad condition" subtract overlaps: `|A∪B| = |A|+|B|-|A∩B|`.

### Dry Run
Compute `C(5,2)` using factorials.

```
fact[0..5] = 1,1,2,6,24,120

C(5,2) = fact[5] / (fact[2]*fact[3])
       = 120 / (2*6)
       = 120/12 = 10

Manual check via Pascal's triangle:
        1
       1 1
      1 2 1
     1 3 3 1
    1 4 6 4 1
   1 5 10 10 5 1     <-- row n=5, C(5,2)=10 ✓
```

```
Pascal triangle build diagram (each entry = sum of two above it):

C(0,0)
C(1,0) C(1,1)
C(2,0) C(2,1) C(2,2)
   \   /  \   /
  each cell = left-parent + right-parent
```

### C++
```cpp
const int N = 2e5 + 5;
long long fact[N], invfact[N];

void precompute_factorials() {
    fact[0] = 1;
    for (int i = 1; i < N; i++) fact[i] = fact[i-1] * i % MOD;
    invfact[N-1] = power(fact[N-1], MOD - 2);
    for (int i = N - 2; i >= 0; i--) invfact[i] = invfact[i+1] * (i+1) % MOD;
}

long long C(int n, int r) {
    if (r < 0 || r > n) return 0;
    return fact[n] * invfact[r] % MOD * invfact[n-r] % MOD;
}
```

### More problems
1. CSES – [Binomial Coefficients](https://cses.fi/problemset/task/1079)
2. CSES – [Creating Strings II](https://cses.fi/problemset/task/1715)
3. CSES – [Distributing Apples](https://cses.fi/problemset/task/1716) (stars and bars)
4. CSES – Bracket Sequences I
5. CSES – Bracket Sequences II (Catalan numbers)
6. [CF 559C](https://codeforces.com/problemset/problem/559/C) – Gerald and Giant Chess (combinatorial paths avoiding blocked cells)
7. [CF 1096D](https://codeforces.com/problemset/problem/1096/D) – Easy Problem (dp + combinatorics)
8. [CF 1178F1](https://codeforces.com/problemset/problem/1178/F1) – Short Colorful Strip (harder combinatorics)
9. [CF 630K](https://codeforces.com/problemset/problem/630/K) – Indivisibility (inclusion–exclusion)
10. [CF 1214D](https://codeforces.com/problemset/problem/1214/D) – Bouncing Ball (skip if unfamiliar — swap for) [CF 300C](https://codeforces.com/problemset/problem/300/C) – Beautiful Numbers

---

## 6. Arithmetic/Geometric Series & Ceiling-Floor Tricks

**Trigger phrases:** "minimum number of X to cover Y", "sum of first n terms", "divide into groups of size k"

**Core idea:** `ceil(a/b) = (a + b - 1) / b` in integer division (avoids floats). Arithmetic series sum: `n*(first+last)/2`. Watch for the classic "off by one" trap at boundaries — always dry-run with a small edge case (n=1, n=0).

### Dry Run
Theatre Square style: pave a `7 x 5` square with `3x3` flagstones — how many flagstones needed per dimension?

```
along length 7: ceil(7/3) = (7+3-1)/3 = 9/3 = 3   -> 3 stones cover 9 units (overlap of 2)
along width  5: ceil(5/3) = (5+3-1)/3 = 7/3 = 2   -> 2 stones cover 6 units (overlap of 1)

total flagstones = 3 * 2 = 6
```

```
Grid diagram (S = stone edge), showing overlap/waste:

 length=7 covered by 3 stones of width 3:
 [===][===][===]
  1-3  4-6  7-9     <- stone 3 covers cells 7,8,9 but we only needed up to 7
                        (2 units of "waste" — this is exactly what ceiling captures)
```

### C++
```cpp
long long ceil_div(long long a, long long b) { return (a + b - 1) / b; } // a,b > 0

long long arithmetic_sum(long long first, long long last, long long n) {
    return n * (first + last) / 2;
}
```

### More problems
1. [CF 1A](https://codeforces.com/problemset/problem/1/A) – Theatre Square
2. [CF 546A](https://codeforces.com/problemset/problem/546/A) – Soldier and Bananas
3. [CF 1360A](https://codeforces.com/problemset/problem/1360/A) – Minimal Square
4. [CF 466A](https://codeforces.com/problemset/problem/466/A) – Cheap Travel
5. CSES – [Number Spiral](https://cses.fi/problemset/task/1071)
6. [CF 1141A](https://codeforces.com/problemset/problem/1141/A) – Game With Sticks (parity/turns arithmetic)
7. [CF 1183A](https://codeforces.com/problemset/problem/1183/A) – Nearest Interesting Number (digit-sum + ceiling)
8. [CF 1265A](https://codeforces.com/problemset/problem/1265/A) – Beautiful Regional Contest (sorting + arithmetic grouping)
9. [CF 1512A](https://codeforces.com/problemset/problem/1512/A) – Spy Detected! (edge-case counting)
10. [CF 1350A](https://codeforces.com/problemset/problem/1350/A) – Orac and Factorial (growth-rate/series reasoning)

---

## 7. Parity / Bitwise Math

**Trigger phrases:** "even/odd", "XOR", "flip a bit", "can you make them equal"

**Core idea:** Track parity (mod 2) invariants instead of exact values — many "can we reach state X" problems reduce to a parity check. For XOR problems: XOR is its own inverse (`a^a=0`), associative, and commutative — build prefix-XOR arrays the same way you'd build prefix sums.

### Dry Run
"Watermelon" ([CF 4A](https://codeforces.com/problemset/problem/4/A)): can weight `w` be split into two even positive parts?

```
w = 8
try: even + even = even, and both must be >=2
8 = 2 + 6  ✓  (works!)

w = 3 (odd)
odd can never be written as even+even -> impossible

Rule discovered: answer is YES iff w is even AND w > 2
w=2 -> would need 2 = even+even with both >=2 -> impossible (only 0+2 or 2+0, but need positive even, min is 2+2=4)
```

```
Parity flow diagram:

   w even? ---- no ----> "NO"
      |
     yes
      |
   w > 2? ---- no (w==2) ----> "NO"
      |
     yes
      |
    "YES"
```

### C++
```cpp
// classic parity check example
bool canSplitEven(long long w) {
    return (w % 2 == 0) && (w > 2);
}

// prefix XOR
vector<int> pre_xor(n + 1, 0);
for (int i = 0; i < n; i++) pre_xor[i+1] = pre_xor[i] ^ a[i];
// XOR of range [l, r] (0-indexed inclusive) = pre_xor[r+1] ^ pre_xor[l]
```

### More problems
1. [CF 4A](https://codeforces.com/problemset/problem/4/A) – Watermelon
2. [CF 1352B](https://codeforces.com/problemset/problem/1352/B) – Same Parity Summation
3. [CF 1327A](https://codeforces.com/problemset/problem/1327/A) – Sum of Odd Integers
4. [CF 1327B](https://codeforces.com/problemset/problem/1327/B) – Sum of Bad Numbers
5. CSES – [Bit Strings](https://cses.fi/problemset/task/1617)
6. CSES – [Two Sets](https://cses.fi/problemset/task/1092)
7. [CF 1375C](https://codeforces.com/problemset/problem/1375/C) – Element Extermination (parity of positions)
8. [CF 1466C](https://codeforces.com/problemset/problem/1466/C) – Canine poetry (parity trick on removals)
9. [CF 1385D](https://codeforces.com/problemset/problem/1385/D) – a-Good String (XOR/parity divide & conquer)
10. [CF 1215B](https://codeforces.com/problemset/problem/1215/B) – The Number of Products (sign/parity of prefix products)

---

## 8. Digit Manipulation (digit sums / digit-based construction)

**Trigger phrases:** "digit sum", "smallest number with digit sum", "no leading zero", "decompose into powers of 10"

**Core idea:** Peel digits with `n % 10` then `n /= 10`. For "sum of round numbers" style problems, decompose greedily digit by digit from the least significant, emitting `d * 10^position` as a separate term whenever the digit is nonzero.

### Dry Run
Decompose 1230 into round-number terms ([CF 1352A](https://codeforces.com/problemset/problem/1352/A) style).

```
n = 1230
digit at position 0 (units): 0 -> skip
digit at position 1 (tens):  3 -> emit 30
digit at position 2 (hund.): 2 -> emit 200
digit at position 3 (thou.): 1 -> emit 1000

Terms: 1000 + 200 + 30 = 1230 ✓ (3 terms, matches count of nonzero digits)
```

```
Peeling diagram for n=1230:

 1230 --%10--> 0   (n becomes 123)
  123 --%10--> 3   (n becomes 12)     -> emit 3 * 10^1 = 30
   12 --%10--> 2   (n becomes 1)      -> emit 2 * 10^2 = 200
    1 --%10--> 1   (n becomes 0)      -> emit 1 * 10^3 = 1000
    0 --> stop
```

### C++
```cpp
vector<long long> decompose_round(long long n) {
    vector<long long> terms;
    long long place = 1;
    while (n > 0) {
        int d = n % 10;
        if (d != 0) terms.push_back((long long)d * place);
        n /= 10;
        place *= 10;
    }
    return terms;
}

int digit_sum(long long n) {
    int s = 0;
    while (n > 0) { s += n % 10; n /= 10; }
    return s;
}
```

### More problems
1. [CF 1352A](https://codeforces.com/problemset/problem/1352/A) – Sum of Round Numbers
2. [CF 1183A](https://codeforces.com/problemset/problem/1183/A) – Nearest Interesting Number
3. CSES – [Digit Queries](https://cses.fi/problemset/task/2431)
4. [CF 1244A](https://codeforces.com/problemset/problem/1244/A) – Pens and Pencils (not digit—swap) [CF 1354A](https://codeforces.com/problemset/problem/1354/A) – Alarm Clock
5. [CF 617A](https://codeforces.com/problemset/problem/617/A) – Elephant (greedy step-counting, digit-like decomposition)
6. [CF 1352C](https://codeforces.com/problemset/problem/1352/C) – K-th Not Divisible by n
7. [CF 1029B](https://codeforces.com/problemset/problem/1029/B) – Creating the Contest (not digit—swap) [CF 1352D](https://codeforces.com/problemset/problem/1352/D) – Alice, Bob and Candies
8. [CF 1352F1](https://codeforces.com/problemset/problem/1352/F1) – Nastia and a Hidden Permutation (harder)
9. [CF 118A](https://codeforces.com/problemset/problem/118/A) – String Task (not digit—optional stretch)
10. CSES – [Counting Numbers](https://cses.fi/problemset/task/2220) (digit DP proper — good bridge to harder digit DP)

---

## 9. Probability & Expected Value

**Note up front:** at Div2 A/B/C difficulty, pure probability problems are rare — most "probability" problems only show up around 1500+ rating because expectation problems usually need linearity-of-expectation reasoning layered on DP. What *does* show up at A–C level is basic counting-as-probability ("in how many of the total outcomes does X happen"), so that's what the dry run below covers. Treat this pattern as a bridge toward Div1/harder Div2 material rather than a core A/B/C pattern.

**Trigger phrases:** "expected value", "probability that", "on average"

**Core idea (linearity of expectation):** `E[X+Y] = E[X]+E[Y]` even if X and Y are dependent. This lets you compute expectation of a complex random variable by summing expectations of simple indicator variables, one position/event at a time.

### Dry Run
Roll two fair 6-sided dice — expected value of the sum.

```
E[die] = (1+2+3+4+5+6)/6 = 21/6 = 3.5

By linearity: E[die1 + die2] = E[die1] + E[die2] = 3.5 + 3.5 = 7.0

(No need to enumerate all 36 outcomes and weight each sum — linearity shortcuts it.)
```

```
Indicator-variable diagram for "expected number of fixed points in a random permutation of n items":

 position 1: indicator X1 = 1 if item stays in place, P(X1=1) = 1/n
 position 2: indicator X2 = 1 if item stays in place, P(X2=1) = 1/n
   ...
 position n: indicator Xn = 1 if item stays in place, P(Xn=1) = 1/n

 E[total fixed points] = E[X1]+E[X2]+...+E[Xn] = n * (1/n) = 1
 (true regardless of dependence between the Xi's — that's the power of linearity)
```

### C++
```cpp
double expected_dice_sum(int sides, int numDice) {
    double per_die = (1 + sides) / 2.0;
    return per_die * numDice; // linearity of expectation
}

// modular expectation (when the answer must be given mod p, use modinv instead of division)
long long expected_mod(long long sumOfValues, long long count) {
    return sumOfValues % MOD * modinv(count) % MOD;
}
```

### More problems
1. [CF 1266A](https://codeforces.com/problemset/problem/1266/A) – Radio Station (counting-as-probability warmup)
2. [CF 1097D](https://codeforces.com/problemset/problem/1097/D) – Makoto and a Blackboard (expectation with modinv)
3. [CF 1265E](https://codeforces.com/problemset/problem/1265/E) – Beautiful Mirrors (harder — expectation + DP)
4. [CF 908D](https://codeforces.com/problemset/problem/908/D) – New Year and Arbitrary Arrangement (expectation, harder)
5. [CF 1540B](https://codeforces.com/problemset/problem/1540/B) – Tree Array (expectation over paths, harder)
6. [CF 678E](https://codeforces.com/problemset/problem/678/E) – Another Sith Tournament (expectation, advanced)
7. [CF 626D](https://codeforces.com/problemset/problem/626/D) – Jerry's Protest (basic probability comparison)
8. [CF 1096G](https://codeforces.com/problemset/problem/1096/G) – Lucky Tickets (counting; combinatorics-probability hybrid)
9. [CF 235B](https://codeforces.com/problemset/problem/235/B) – Let's Play Osu! (expectation with DP)
10. Codeforces EDU "Expected Value" section (search "Codeforces EDU probabilities") for a guided problem ladder

---

## 10. Game Theory Basics (Nim / parity-of-moves)

**Trigger phrases:** "two players take turns", "last one to move wins/loses", "optimal play"

**Core idea:** In a single pile of Nim, whoever moves when the pile is at a losing position (P-position) loses with optimal play. For classic Nim with multiple piles, XOR all pile sizes: if the XOR is 0, the position is losing for the player about to move; otherwise it's winning. Many "game" problems reduce to a parity check on total moves rather than full Nim theory.

### Dry Run
Nim with piles `[3, 4, 5]` — who wins if both play optimally?

```
XOR piles: 3 ^ 4 ^ 5
  3 = 011
  4 = 100
  5 = 101
  ------
XOR= 010  = 2  (nonzero)

Nonzero XOR -> the player to move WINS (there exists a move to make XOR=0)

Finding the winning move: we need to reduce one pile so total XOR becomes 0.
Try pile=5 (101): target = 5 ^ 2 = 111 = 7... too big, invalid (must shrink pile)
Try pile=4 (100): target = 4 ^ 2 = 110 = 6... too big, invalid
Try pile=3 (011): target = 3 ^ 2 = 001 = 1... valid! shrink pile 3 -> 1
New piles: [1,4,5] -> XOR = 001^100^101 = 000 ✓ (opponent now faces a losing position)
```

```
Bit-column XOR diagram for piles 3,4,5:

        bit2 bit1 bit0
pile 3:   0    1    1
pile 4:   1    0    0
pile 5:   1    0    1
       ----------------
XOR:      0    1    0   = 2 (decimal)   <- nonzero means current player wins
```

### C++
```cpp
bool nimFirstPlayerWins(vector<long long>& piles) {
    long long x = 0;
    for (auto p : piles) x ^= p;
    return x != 0; // true = player to move wins
}

// generic parity-of-moves game (when total moves is fixed and doesn't depend on choices)
bool winsIfMovesOdd(int totalMoves) {
    return totalMoves % 2 == 1; // first player wins iff total forced moves is odd
}
```

### More problems
1. CSES – Nim Game I
2. CSES – Nim Game II (Sprague-Grundy)
3. CSES – Stick Game
4. [CF 1450A](https://codeforces.com/problemset/problem/1450/A) – Avoid Trygub? (verify — swap if mismatched to) [CF 1194A](https://codeforces.com/problemset/problem/1194/A) – Rewriting
5. [CF 1451B](https://codeforces.com/problemset/problem/1451/B) – Non-antichain Subsets? (swap for game-theory proper:) [CF 1527B1](https://codeforces.com/problemset/problem/1527/B1) – Palindrome Game (easy version)
6. [CF 1194D](https://codeforces.com/problemset/problem/1194/D) – 1-2-K Game
7. [CF 1537D](https://codeforces.com/problemset/problem/1537/D) – Deleting Divisors (Grundy-flavored)
8. [CF 1400D1](https://codeforces.com/problemset/problem/1400/D1) – Zigzags (not game — optional stretch, skip if off-topic)
9. [CF 1073C](https://codeforces.com/problemset/problem/1073/C) – Vasya and Robot (not game — remove if you want strictly game theory only)
10. [CF 455B](https://codeforces.com/problemset/problem/455/B) – A Lot of Games (multi-round game theory)

---

## Quick pattern-spotting cheat sheet (read this before a contest)

```
Statement mentions...                  -> Suspect pattern
------------------------------------------------------------
"modulo 1e9+7"                         -> #2 Modular arithmetic / fast pow
"prime", "divisors", "factorize"       -> #3 / #4 Primes & Divisors
"number of ways", "choose", "arrange"  -> #5 Combinatorics
"ceil", "minimum containers/groups"    -> #6 Series & ceiling tricks
"even/odd", "XOR", "flip"              -> #7 Parity / bitwise
"digit", "leading zero"                -> #8 Digit manipulation
"expected value", "probability"        -> #9 Expectation (rare below 1500)
"two players", "optimal play", "turns" -> #10 Game theory
"gcd", "lcm", "reduce fraction"        -> #1 GCD/LCM
```

Some named problems above are close-fit approximations from memory rather than 100%-verified indices — before drilling a set, quickly confirm the tag/rating on Codeforces' problemset filter (`tags: math, number theory, combinatorics, dp, bitmasks, games` + rating range) so you're not surprised by a mismatch mid-practice.

---

## 11. Binary Search on the Answer

**Trigger phrases:** "find the maximum/minimum value such that...", "maximize the smallest", "what's the largest k for which you can..."

**Core idea:** If a yes/no condition `check(x)` flips exactly once as `x` increases (monotonic), you can binary search on `x` itself instead of the original variable, turning an `O(n)` or worse search into `O(log(range) * cost_of_check)`.

### Dry Run
"What's the max number of chocolates `k` you can buy if each costs `p` and you have `budget`, but price rises by 1 after every purchase?" Suppose `p=3`, `budget=20`.

```
check(k) = "can I buy k chocolates within budget?"
cost(k) = 3 + 4 + 5 + ... (k terms) = k*3 + (0+1+...+(k-1)) = 3k + k(k-1)/2

lo=0, hi=20
mid=10 -> cost = 30 + 45 = 75  > 20 -> too expensive, hi=9
mid=4  -> cost = 12 + 6  = 18  <= 20 -> feasible, ans=4, lo=5
mid=7  -> cost = 21 + 21 = 42  > 20 -> hi=6
mid=5  -> cost = 15 + 10 = 25  > 20 -> hi=4
lo>hi, stop -> answer = 4
```

```
Monotonic condition diagram (F=infeasible, T=feasible), searching for the boundary:

k:      0  1  2  3  4  5  6  7  8 ...
check:  T  T  T  T  T  F  F  F  F ...
                       ^
                 answer = last T = 4
binary search zooms into this boundary in O(log k) checks instead of scanning all k
```

### C++
```cpp
bool check(long long k, long long budget, long long p) {
    long long cost = k * p + k * (k - 1) / 2;
    return cost <= budget;
}

long long binarySearchAnswer(long long lo, long long hi, long long budget, long long p) {
    long long ans = lo - 1;
    while (lo <= hi) {
        long long mid = lo + (hi - lo) / 2;
        if (check(mid, budget, p)) { ans = mid; lo = mid + 1; }
        else hi = mid - 1;
    }
    return ans;
}
```

### More problems
1. CSES – [Factory Machines](https://cses.fi/problemset/task/1620)
2. CSES – [Array Division](https://cses.fi/problemset/task/1085)
3. [CF 1791D](https://codeforces.com/problemset/problem/1791/D) – Distinct Balls (binary search + greedy check)
4. [CF 1547B](https://codeforces.com/problemset/problem/1547/B) – Alphabetical Strings (constructive, not BS — swap: [CF 1520D](https://codeforces.com/problemset/problem/1520/D) – Same Differences? — better) [CF 1616D](https://codeforces.com/problemset/problem/1616/D) – Keep the Average High
5. [CF 1462E2](https://codeforces.com/problemset/problem/1462/E2) – Close Tuples (harder version)
6. [CF 1611E](https://codeforces.com/problemset/problem/1611/E) – Escape The Maze (skip if unfamiliar) — solid alt: [CF 4C](https://codeforces.com/problemset/problem/4/C)-adjacent, use [CF 1195C](https://codeforces.com/problemset/problem/1195/C) – Basketball Exercise
7. [CF 1611D](https://codeforces.com/problemset/problem/1611/D) – Balanced Subsequences (skip) — reliable alt: [CF 940B](https://codeforces.com/problemset/problem/940/B) – Our Tanya is Crying Out Loud (binary-search-like greedy)
8. [CF 1373D](https://codeforces.com/problemset/problem/1373/D) – Maximum Sum on Even Positions (not BS, optional stretch)
9. [CF 1512D](https://codeforces.com/problemset/problem/1512/D) – Corrupted Array (search + verify structure)
10. [CF 1141B](https://codeforces.com/problemset/problem/1141/B) – Maximal Continuous Rest (sliding window, pairs well right after this pattern)

---

## 12. Two Pointers with a Math Invariant

**Trigger phrases:** "contiguous subarray", "sum at most/at least X", "sorted array, find pairs with sum..."

**Core idea:** When moving the right pointer only ever increases some monotonic quantity (like a running sum) and moving the left pointer only ever decreases it, you can slide both pointers forward across the array in a single `O(n)` pass instead of checking all `O(n^2)` windows.

### Dry Run
Array `[2, 1, 3, 4, 1]`, find the longest subarray with sum `<= 6`.

```
l=0, r=0, sum=0, best=0

r=0: sum=0+2=2   <=6, window=[0,0], len=1, best=1
r=1: sum=2+1=3   <=6, window=[0,1], len=2, best=2
r=2: sum=3+3=6   <=6, window=[0,2], len=3, best=3
r=3: sum=6+4=10  >6  -> shrink: sum-=a[l]=2 -> sum=8, l=1; still >6 -> sum-=a[l]=1 -> sum=7, l=2; still>6 -> sum-=3=4, l=3
      now sum=4 <=6, window=[3,3], len=1, best stays 3
r=4: sum=4+1=5   <=6, window=[3,4], len=2, best stays 3

Answer: 3 (subarray [2,1,3])
```

```
Sliding window diagram:

index:   0   1   2   3   4
value:   2   1   3   4   1
         L-------R              sum=6 (window grows while sum<=6)
             L---R  (shrunk from left when sum exceeded 6, sum recalculated)
window only ever moves rightward -> total pointer movement across whole run = O(n)
```

### C++
```cpp
int longestSubarrayAtMostK(vector<int>& a, long long K) {
    int n = a.size(), best = 0;
    long long sum = 0;
    int l = 0;
    for (int r = 0; r < n; r++) {
        sum += a[r];
        while (sum > K) { sum -= a[l]; l++; }
        best = max(best, r - l + 1);
    }
    return best;
}
```

### More problems
1. CSES – [Subarray Sums II](https://cses.fi/problemset/task/1661) (with negatives — uses prefix+hashmap, good contrast case)
2. CSES – [Sum of Two Values](https://cses.fi/problemset/task/1640) (sorted two-pointer)
3. [CF 1354C](https://codeforces.com/problemset/problem/1354/C) – Not Adjacent Matrix (not two pointer, optional skip) — better: [CF 1195C](https://codeforces.com/problemset/problem/1195/C) is BS not TP, use [CF 1512C](https://codeforces.com/problemset/problem/1512/C) — swap: [CF 279B](https://codeforces.com/problemset/problem/279/B) – Books
4. [CF 1265B](https://codeforces.com/problemset/problem/1265/B) – Beautiful Numbers (two pointer on digit blocks)
5. [CF 1462D](https://codeforces.com/problemset/problem/1462/D) – Add to Neighbour and Remove (prefix sum + greedy grouping)
6. [CF 1526C1](https://codeforces.com/problemset/problem/1526/C1) – Potions (Easy Version) (greedy + running sum invariant)
7. [CF 1330B](https://codeforces.com/problemset/problem/1330/B) – Numbers Box (not TP, optional) — reliable: [CF 1200A](https://codeforces.com/problemset/problem/1200/A) – Hotelier? skip if mismatched, use [CF 1223C](https://codeforces.com/problemset/problem/1223/C) – Save the Nature
8. CSES – [Maximum Subarray Sum](https://cses.fi/problemset/task/1643) (Kadane, a close cousin of TP)
9. [CF 1256E](https://codeforces.com/problemset/problem/1256/E) – Yet Another Division Into Teams (sorted + windowed grouping)
10. [CF 1157C1](https://codeforces.com/problemset/problem/1157/C1) – Increasing Subsequence (Easy Version) (greedy two-ended)

---

## 13. Prefix Sums / Difference Arrays

**Trigger phrases:** "sum of range [l,r]", "add v to all elements from l to r", "many queries about subarrays"

**Core idea:** `prefix[i] = a[0]+...+a[i-1]`, so `sum(l,r) = prefix[r+1] - prefix[l]` in O(1) per query after an O(n) build. For range *updates* done offline (all updates known before you need any answer), use a difference array: `diff[l]+=v; diff[r+1]-=v;` then take one prefix sum pass at the end to recover final values.

### Dry Run
Array `[3, 1, 4, 1, 5]`, answer sum(1,3) (0-indexed inclusive, i.e. elements at index 1,2,3).

```
a       =  3   1   4   1   5
prefix  =  0   3   4   8   9  14     (prefix[i] = sum of first i elements)
index:     0   1   2   3   4   5

sum(1,3) = prefix[4] - prefix[1] = 9 - 3 = 6
check by hand: a[1]+a[2]+a[3] = 1+4+1 = 6 ✓
```

```
Prefix array diagram — each cell accumulates everything to its left:

a:       [ 3 ][ 1 ][ 4 ][ 1 ][ 5 ]
prefix: 0 -> 3 -> 4 -> 8 -> 9 -> 14
             ^sum(1,3) = prefix[4]-prefix[1] = 9-3 = 6
                  (subtracts out everything before index 1)

Difference array for range-update [1,3] += 5:
diff:    [0][+5][0][0][-5][0]
              ^l=1        ^r+1=4  (cancels the +5 after position 3)
prefix-of-diff reconstructs: 0,5,5,5,0  -> add to original array
```

### C++
```cpp
// Prefix sum for range-sum queries
vector<long long> prefix(n + 1, 0);
for (int i = 0; i < n; i++) prefix[i+1] = prefix[i] + a[i];
long long rangeSum(int l, int r) { return prefix[r+1] - prefix[l]; } // inclusive [l,r]

// Difference array for offline range-add updates
vector<long long> diff(n + 1, 0);
void rangeAdd(int l, int r, long long v) { diff[l] += v; diff[r+1] -= v; }
void applyUpdates(vector<long long>& a) {
    long long running = 0;
    for (int i = 0; i < n; i++) { running += diff[i]; a[i] += running; }
}
```

### More problems
1. CSES – Static Range Sum Queries
2. CSES – Range Update Queries
3. CSES – Forest Queries (2D prefix sums)
4. [CF 1512D](https://codeforces.com/problemset/problem/1512/D) – Corrupted Array
5. [CF 1391D](https://codeforces.com/problemset/problem/1391/D) – 505 (2D prefix sum + parity trick)
6. [CF 1461B](https://codeforces.com/problemset/problem/1461/B) – Levels (prefix-sum based greedy)
7. [CF 1512F](https://codeforces.com/problemset/problem/1512/F) – Education (unrelated — swap) [CF 1195C](https://codeforces.com/problemset/problem/1195/C) – Basketball Exercise (prefix on two arrays)
8. [CF 1466D](https://codeforces.com/problemset/problem/1466/D) – Employment (prefix parity)
9. [CF 1478A](https://codeforces.com/problemset/problem/1478/A) – Nezzar and Colorful Balls (LIS-flavored, prefix-adjacent)
10. [CF 1553D](https://codeforces.com/problemset/problem/1553/D) – Backspace (stack + counting, prefix-like reasoning)

---

## 14. Constructive / Greedy-with-Proof

**Trigger phrases:** "construct any valid...", "output any array that satisfies", "it can be shown a solution always exists"

**Core idea:** There's no closed-form formula — you build the answer directly, usually via a clean rule (e.g., "put all odds first, then evens", "always pick the largest remaining"). The hard part isn't coding it, it's proving your rule can't fail; on a fresh problem, test your rule against 2-3 hand-picked edge cases before coding.

### Dry Run
"Construct a permutation of `1..n` where no element equals its 1-indexed position." (a derangement-style ask; simplest constructive fix: cyclic shift by 1)

```
n = 5
naive attempt (identity): 1 2 3 4 5  -> every element equals its position -> invalid

constructive rule: shift everything left by one, wrap around
position: 1 2 3 4 5
value:    2 3 4 5 1

check: pos1->2 (ok,!=1), pos2->3(ok), pos3->4(ok), pos4->5(ok), pos5->1(ok)
All positions differ from their value -> valid derangement, built in O(n), no search needed
```

```
Cyclic shift diagram for n=5:

 position:  1   2   3   4   5
              \   \   \   \   \
 value:        2   3   4   5   1     (each value = position+1, last wraps to 1)

 Edge case to always test separately: n=1 -> shifting gives value=1 at position=1 -> FAILS
 (this is why you dry-run n=1 before trusting any constructive rule)
```

### C++
```cpp
vector<int> shiftedDerangement(int n) {
    // WARNING: fails for n==1 -- always special-case n==1 as "impossible"
    vector<int> res(n);
    for (int i = 0; i < n; i++) res[i] = (i + 1) % n + 1; // 1-indexed values, shifted
    return res;
}
```

### More problems
1. [CF 1360C](https://codeforces.com/problemset/problem/1360/C) – Similar Pairs
2. [CF 1471B](https://codeforces.com/problemset/problem/1471/B) – Strange Definition (not constructive — swap) [CF 1512B](https://codeforces.com/problemset/problem/1512/B) – Almost Rectangle
3. [CF 1359B](https://codeforces.com/problemset/problem/1359/B) – New Theatre Square (construction on a grid)
4. [CF 1466A](https://codeforces.com/problemset/problem/1466/A) – Bovine Dilemma (not constructive) — swap: [CF 1520C](https://codeforces.com/problemset/problem/1520/C) – Not Adjacent Matrix
5. [CF 1547C](https://codeforces.com/problemset/problem/1547/C) – Pair Programming (simulate + constructive merge rule)
6. [CF 1345C](https://codeforces.com/problemset/problem/1345/C) – Prefixes of LCS (construction with proof by cases)
7. [CF 1607B](https://codeforces.com/problemset/problem/1607/B) – Poisoned Dagger (greedy construction of hit timing)
8. [CF 1360D](https://codeforces.com/problemset/problem/1360/D) – Buying Shovels (constructive divisor search, bridges pattern #4)
9. [CF 1618D](https://codeforces.com/problemset/problem/1618/D) – Array and Operations (greedy pairing construction)
10. [CF 1512C](https://codeforces.com/problemset/problem/1512/C) – A. Boring Segments (harder, optional stretch)

---

## 15. Basic Geometry Math

**Trigger phrases:** "points on a plane", "area of the triangle/polygon", "are these three points collinear", "distance between"

**Core idea:** Distance: `sqrt((x2-x1)^2+(y2-y1)^2)`. Cross product `(x2-x1)(y3-y1) - (y2-y1)(x3-x1)` tells you turn direction (>0 left turn, <0 right turn, ==0 collinear) without needing sqrt or floats at all — prefer cross products over angles/distances whenever you only need orientation, since it keeps everything in exact integer arithmetic.

### Dry Run
Are points `A(0,0)`, `B(2,2)`, `C(4,4)` collinear?

```
cross = (Bx-Ax)*(Cy-Ay) - (By-Ay)*(Cx-Ax)
      = (2-0)*(4-0) - (2-0)*(4-0)
      = 2*4 - 2*4
      = 8 - 8 = 0   -> collinear!
```

```
Cross product sign diagram:

        C
       /
      B          cross(A,B,C) > 0 -> C is to the LEFT of line A->B
     /
    A

        B---C    cross(A,B,C) == 0 -> A,B,C are collinear (straight line)
       /
      A
```

### C++
```cpp
struct Point { long long x, y; };

long long cross(Point A, Point B, Point C) {
    return (B.x - A.x) * (C.y - A.y) - (B.y - A.y) * (C.x - A.x);
}
bool collinear(Point A, Point B, Point C) { return cross(A, B, C) == 0; }

double dist(Point A, Point B) {
    return sqrt((double)(B.x-A.x)*(B.x-A.x) + (double)(B.y-A.y)*(B.y-A.y));
}
```

### More problems
1. CSES – Point Location Test
2. CSES – Line Segment Intersection
3. CSES – Polygon Area
4. [CF 1195D](https://codeforces.com/problemset/problem/1195/D) – Submarine in the Rybinsk Sea (not geometry) — swap: [CF 1025B](https://codeforces.com/problemset/problem/1025/B) – Weakened Common Divisor? — better: [CF 1266B](https://codeforces.com/problemset/problem/1266/B) – Han Solo and Lazer Gun
5. [CF 1354C2](https://codeforces.com/problemset/problem/1354/C2) – Not So Simple Polygon Embedding (harder, optional)
6. [CF 1523B](https://codeforces.com/problemset/problem/1523/B) – Lord of the Values (not geometry, skip) — reliable: [CF 1027B](https://codeforces.com/problemset/problem/1027/B) – Numbers on the Chessboard (grid geometry)
7. [CF 598C](https://codeforces.com/problemset/problem/598/C) – Nearest vectors (angle sorting)
8. [CF 1548B](https://codeforces.com/problemset/problem/1548/B) – Integers Have Friends (not geometry, optional)
9. [CF 1017D](https://codeforces.com/problemset/problem/1017/D) – The Wu (not geometry, skip) — swap: [CF 1C](https://codeforces.com/problemset/problem/1/C) – Ancient Berland Circus (geometry + combinatorics)
10. [CF 1091D](https://codeforces.com/problemset/problem/1091/D) – New Year and the Permutation Concatenation (skip, unrelated) — swap: [CF 1C](https://codeforces.com/problemset/problem/1/C) is enough as capstone

---

## 16. Diophantine Equations / Extended Euclid

**Trigger phrases:** "find integers x, y such that ax+by=c", "smallest positive combination"

**Core idea:** A solution to `ax+by=gcd(a,b)` always exists and extended Euclid finds it recursively while computing the gcd. `ax+by=c` has an integer solution iff `gcd(a,b) | c`; once you have one solution, all others are `x + k*(b/g)`, `y - k*(a/g)`.

### Dry Run
Solve `3x + 5y = 1` using extended Euclid.

```
gcd(5,3): 5 = 1*3+2 -> gcd(3,2): 3=1*2+1 -> gcd(2,1): 2=2*1+0 -> gcd=1

back-substitute:
1 = 3 - 1*2
2 = 5 - 1*3   -> substitute: 1 = 3 - 1*(5 - 1*3) = 2*3 - 1*5

So: 3*(2) + 5*(-1) = 1  -> x=2, y=-1 is one valid solution
check: 3*2 + 5*(-1) = 6-5 = 1 ✓
```

```
Back-substitution chain diagram:

 5 = 1*3 + 2   -->  2 = 5 - 1*3
 3 = 1*2 + 1   -->  1 = 3 - 1*2
                     substitute 2's expression in:
                1 = 3 - 1*(5 - 1*3) = 2*3 - 1*5
                    (walk back up the Euclid chain, substituting each remainder)
```

### C++
```cpp
long long extgcd(long long a, long long b, long long &x, long long &y) {
    if (b == 0) { x = 1; y = 0; return a; }
    long long x1, y1;
    long long g = extgcd(b, a % b, x1, y1);
    x = y1;
    y = x1 - (a / b) * y1;
    return g;
}
// solves ax+by=g, returns g=gcd(a,b), fills x,y
```

### More problems
1. CSES – [Exponentiation](https://cses.fi/problemset/task/1095) (modinv is a special case of this idea)
2. [CF 1244D](https://codeforces.com/problemset/problem/1244/D) – Paint the Tree (unrelated, skip) — reliable set instead:
3. [CF 1225D](https://codeforces.com/problemset/problem/1225/D) – Power Products (uses factorization + gcd)
4. [CF 1264B](https://codeforces.com/problemset/problem/1264/B) – Beautiful Numbers (not diophantine, optional)
5. [CF 1349A](https://codeforces.com/problemset/problem/1349/A) – Orac and Factors (gcd/lcm adjacent)
6. SPOJ – CEQU (Ax+By=C, classic extended Euclid problem)
7. [CF 1478D](https://codeforces.com/problemset/problem/1478/D) – Nezzar and Nice Beatmap (geometry+number theory hybrid)
8. UVa 10090 – Marbles (classic Diophantine word problem)
9. [CF 1091A](https://codeforces.com/problemset/problem/1091/A) – New Year and the Christmas Ornament (not diophantine, optional)
10. Codeforces EDU: search "extended euclidean algorithm codeforces edu" for a guided problem set

---

## 17. Ternary Search

**Trigger phrases:** "minimize/maximize this function", function is "unimodal" (one clear valley or peak), often paired with real-valued or convex cost functions

**Core idea:** If `f(x)` strictly decreases then strictly increases (a valley) — or the reverse for a peak — you can discard one third of the search range each iteration by comparing `f(m1)` and `f(m2)` at two interior points, converging in `O(log range)`.

### Dry Run
Minimize `f(x) = (x-4)^2` over integers `0..10`.

```
lo=0, hi=10
m1 = lo + (hi-lo)/3 = 3,  m2 = hi - (hi-lo)/3 = 7
f(3)=1, f(7)=9  -> f(m1) < f(m2), minimum is NOT to the right of m2 -> hi=m2-1=6

lo=0, hi=6
m1=2, m2=4
f(2)=4, f(4)=0 -> f(m1) > f(m2) -> minimum is not to the left of m1 -> lo=m1+1=3

lo=3, hi=6
m1=4, m2=5
f(4)=0, f(5)=1 -> f(m1)<f(m2) -> hi=m2-1=4

lo=3,hi=4 -> narrow down further -> converges to x=4, f(4)=0 ✓ (true minimum)
```

```
Valley-shape diagram for f(x)=(x-4)^2:

f(x) |          *                       *
     |      *         *             *
     |  *                 *     *
     |____________*___________________ x
     0  1  2  3  [4]  5  6  7  8  9 10
                  ^ true minimum

ternary search discards a third of the range each step, converging onto the valley bottom
```

### C++
```cpp
double f(double x) { return (x - 4) * (x - 4); }

double ternarySearchMin(double lo, double hi) {
    for (int iter = 0; iter < 200; iter++) { // ~200 iters gives high precision on doubles
        double m1 = lo + (hi - lo) / 3;
        double m2 = hi - (hi - lo) / 3;
        if (f(m1) < f(m2)) hi = m2; else lo = m1;
    }
    return (lo + hi) / 2;
}
```

### More problems
1. [CF 1420D2](https://codeforces.com/problemset/problem/1420/D2) – Rescue Nibel (Hard Version) (uses ternary-search-like monotonic structure)
2. [CF 1355E](https://codeforces.com/problemset/problem/1355/E) – Restorer Distance (classic ternary search on a convex cost function)
3. [CF 936B](https://codeforces.com/problemset/problem/936/B) – Sleepy Game (skip if unrelated) — swap: [CF 1189A](https://codeforces.com/problemset/problem/1189/A) – Keanu's Basement (not TS, optional)
4. [CF 1099F](https://codeforces.com/problemset/problem/1099/F) – Cookies (harder — TS + DP)
5. [CF 1237C](https://codeforces.com/problemset/problem/1237/C) – Balanced Removals (Easier) (not TS, optional skip)
6. Codeforces EDU: search "ternary search codeforces edu" for a guided ladder
7. [CF 1601B](https://codeforces.com/problemset/problem/1601/B) – Frog Jumps (not TS, optional — good BS pairing instead)
8. SPOJ – TRICOIN (classic unimodal optimization, ternary search practice)
9. CF 1425 series (search "ternary search" tag on Codeforces problemset for current matches)
10. CF 1355 series (Div2 rated ~1400-1700 often features convex-function TS problems)

---

## 18. Number Base Conversion

**Trigger phrases:** "represent n in base b", "binary/ternary representation", "convert between number systems"

**Core idea:** To convert decimal to base `b`: repeatedly take `n % b` (that's the next digit, least significant first) and `n /= b` until `n` becomes 0, then reverse the collected digits. To convert back: `sum(digit[i] * b^i)`.

### Dry Run
Convert decimal `29` to base `3`.

```
n=29:  29 % 3 = 2   remainder digit -> [2]     n = 29/3 = 9
n=9:    9 % 3 = 0   remainder digit -> [2,0]   n = 9/3  = 3
n=3:    3 % 3 = 0   remainder digit -> [2,0,0] n = 3/3  = 1
n=1:    1 % 3 = 1   remainder digit -> [2,0,0,1] n = 1/3 = 0
stop (n==0)

reverse collected digits: 1,0,0,2  -> 29 in base 3 is "1002"
check: 1*27 + 0*9 + 0*3 + 2*1 = 27+2 = 29 ✓
```

```
Repeated division diagram:

 29 -> /3 -> 9  (remainder 2)
  9 -> /3 -> 3  (remainder 0)
  3 -> /3 -> 1  (remainder 0)
  1 -> /3 -> 0  (remainder 1)   <- stop, n==0

collect remainders bottom-up: 1 0 0 2  = "1002" base 3
```

### C++
```cpp
vector<int> toBase(long long n, int b) {
    vector<int> digits;
    if (n == 0) return {0};
    while (n > 0) { digits.push_back(n % b); n /= b; }
    reverse(digits.begin(), digits.end());
    return digits;
}

long long fromBase(vector<int>& digits, int b) {
    long long n = 0;
    for (int d : digits) n = n * b + d;
    return n;
}
```

### More problems
1. CSES – [Bit Strings](https://cses.fi/problemset/task/1617) (binary reasoning)
2. [CF 1178A](https://codeforces.com/problemset/problem/1178/A) – Handshakes? (skip, unrelated) — swap: [CF 1195C](https://codeforces.com/problemset/problem/1195/C) not base — reliable: [CF 900A](https://codeforces.com/problemset/problem/900/A) – Find Extra One (skip, geometry) — use: [CF 1114C](https://codeforces.com/problemset/problem/1114/C) – Trailing Loves (or L'oeufs?) (base-p trailing zero counting)
3. [CF 1163A](https://codeforces.com/problemset/problem/1163/A) – Eating Soup (not base, skip) — swap: [CF 1183A](https://codeforces.com/problemset/problem/1183/A) – Nearest Interesting Number (digit/base adjacent)
4. [CF 27B](https://codeforces.com/problemset/problem/27/B) – Tournament (not base) — reliable: [CF 149B](https://codeforces.com/problemset/problem/149/B) – Martian Clock (custom base conversion, classic)
5. [CF 1359A](https://codeforces.com/problemset/problem/1359/A) – Berland Poker (not base, optional skip)
6. UVa 343 – What Base Is This? (classic base-detection problem)
7. [CF 1215A](https://codeforces.com/problemset/problem/1215/A) – Yellow Cards (not base, optional)
8. [CF 1029A](https://codeforces.com/problemset/problem/1029/A) is not base — for a genuine base ladder search Codeforces tag "number theory" + keyword "base" in problemset filter
9. Project Euler 205-style base/digit problems (good supplementary practice outside CF)
10. [CF 1548A](https://codeforces.com/problemset/problem/1548/A) – Digits Sequence (Easy Edition) (digit-position + base-adjacent indexing)

---

## 19. Matrix Exponentiation for Recurrences

**Trigger phrases:** "n up to 10^18", linear recurrence like Fibonacci but huge n, "f(n) = a*f(n-1) + b*f(n-2) + ..."

**Core idea:** Any fixed-order linear recurrence can be written as `state_n = M * state_(n-1)` for some constant matrix `M`. Then `state_n = M^n * state_0`, and `M^n` is computed with the same binary-exponentiation trick from pattern #2, just with matrix multiplication instead of scalar multiplication — turning an `O(n)` recurrence into `O(k^3 log n)` for a `k x k` matrix.

### Dry Run
Fibonacci via matrix power: `[[1,1],[1,0]]^n` gives `F(n+1), F(n)` in its first row.

```
M = [1 1]
    [1 0]

M^1 = [1 1]      -> encodes F(2)=1, F(1)=1
      [1 0]

M^2 = M*M = [1*1+1*1  1*1+1*0]  = [2 1]   -> top-left = F(3)=2
            [1*1+0*1  1*1+0*0]    [1 1]

M^3 = M^2 * M = [2 1] * [1 1] = [2*1+1*1  2*1+1*0] = [3 2]
                [1 1]   [1 0]   [1*1+1*1  1*1+1*0]   [2 1]

top-left of M^3 = 3 = F(4) ✓ (Fibonacci: 1,1,2,3,5,8,...)
```

```
State propagation diagram:

 [F(n+1)]       [F(n)]
 [F(n)  ]  = M * [F(n-1)]

 repeatedly applying M is the same as repeated squaring:
 M^1 -> M^2 -> M^4 -> M^8 -> ...  (binary exponentiation on matrices)
 combine squared powers according to bits of n, exactly like scalar fast-pow
```

### C++
```cpp
typedef vector<vector<long long>> Matrix;

Matrix multiply(const Matrix& A, const Matrix& B, long long mod) {
    int n = A.size(), m = B[0].size(), k = B.size();
    Matrix C(n, vector<long long>(m, 0));
    for (int i = 0; i < n; i++)
        for (int j = 0; j < m; j++)
            for (int l = 0; l < k; l++)
                C[i][j] = (C[i][j] + A[i][l] * B[l][j]) % mod;
    return C;
}

Matrix matpow(Matrix M, long long p, long long mod) {
    int n = M.size();
    Matrix result(n, vector<long long>(n, 0));
    for (int i = 0; i < n; i++) result[i][i] = 1; // identity
    while (p > 0) {
        if (p & 1) result = multiply(result, M, mod);
        M = multiply(M, M, mod);
        p >>= 1;
    }
    return result;
}
```

### More problems
1. CSES – Fibonacci Numbers
2. [CF 185A](https://codeforces.com/problemset/problem/185/A) – Plant (matrix recurrence)
3. [CF 450B](https://codeforces.com/problemset/problem/450/B) – Jzzhu and Sequences (linear recurrence, matrix exponentiation)
4. [CF 1182E](https://codeforces.com/problemset/problem/1182/E) – Product Oriented Recurrence (recurrence -> transform -> matrix power)
5. [CF 947G](https://codeforces.com/problemset/problem/947/G) – Amount of Degrees (not matrix, skip) — swap: [CF 954E](https://codeforces.com/problemset/problem/954/E) – Water Taps (not matrix) — reliable: [CF 1370C](https://codeforces.com/problemset/problem/1370/C) is DP not matrix — use: [CF 793E](https://codeforces.com/problemset/problem/793/E) – Problem of offices (advanced, optional)
6. [CF 678E](https://codeforces.com/problemset/problem/678/E) – Another Sith Tournament (harder, expectation+recurrence)
7. [CF 1225E](https://codeforces.com/problemset/problem/1225/E) – Rock Is Push (DP not matrix, optional skip)
8. SPOJ – FIBOSUM (matrix exponentiation practice)
9. [CF 1097D](https://codeforces.com/problemset/problem/1097/D) bridges here if you extend expectation into recurrence form (optional cross-reference)
10. Codeforces EDU: search "matrix exponentiation codeforces edu" for a guided problem ladder

---

## 20. Pigeonhole / Counting Argument

**Trigger phrases:** "prove that at least two...", "show a solution always exists", "among n items"

**Core idea:** If you're placing `n` items into `k < n` boxes, at least one box gets 2+ items. This isn't something you "compute" — it's a reasoning tool for bounding search space or proving existence, and it often turns an apparently huge search into a tiny one (e.g., "check only the first k+1 candidates, one must repeat/collide").

### Dry Run
"Given any 13 people, show at least 2 share a birth month."

```
Boxes = 12 months
Items = 13 people

If each month had at most 1 person, total people <= 12*1 = 12
But we have 13 people > 12 -> contradiction
=> at least one month must contain >= 2 people (pigeonhole)
```

```
Box-filling diagram (12 boxes = months, 13 items = people):

Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec
 P   P   P   P   P   P   P   P   P   P   P   P    <- 12 people, one per box, all boxes full
 P                                                  <- 13th person MUST double up somewhere
                                                        (no empty box left to place them alone)
```

### C++
```cpp
// Typical pigeonhole application: bound the search using modular residues.
// Example: among any (m+1) integers, two must share the same remainder mod m.
int findDuplicateRemainder(vector<long long>& nums, int m) {
    vector<int> seen(m, -1);
    for (int i = 0; i < (int)nums.size(); i++) {
        int r = nums[i] % m;
        if (seen[r] != -1) return seen[r]; // pigeonhole guarantees this triggers within m+1 steps
        seen[r] = i;
    }
    return -1; // won't happen if nums.size() > m
}
```

### More problems
1. [CF 1220E](https://codeforces.com/problemset/problem/1220/E) – Tourism (not pigeonhole, optional skip)
2. [CF 1462F2](https://codeforces.com/problemset/problem/1462/F2) – The Treasure of The Segments (Hard Version) (uses bounding argument)
3. [CF 1408D](https://codeforces.com/problemset/problem/1408/D) – Searchlights (not pigeonhole, optional)
4. [CF 1359E](https://codeforces.com/problemset/problem/1359/E) – Modular Stability (uses counting/pigeonhole-style structure)
5. [CF 1091D](https://codeforces.com/problemset/problem/1091/D) – New Year and the Permutation Concatenation (existence argument)
6. [CF 1225C](https://codeforces.com/problemset/problem/1225/C) – p-binary (uses bounded search justified by counting)
7. [CF 1288B](https://codeforces.com/problemset/problem/1288/B) – Yet Another Meme Problem (digit + pigeonhole-flavored bound)
8. [CF 1354B2](https://codeforces.com/problemset/problem/1354/B2) – Ternary String (Hard Version) (bounded window via counting argument)
9. [CF 300C](https://codeforces.com/problemset/problem/300/C) – Beautiful Numbers (combinatorial bound)
10. Classic textbook set: search "codeforces pigeonhole principle problems" for a curated list, since this pattern is more often embedded inside a harder problem than tagged standalone

