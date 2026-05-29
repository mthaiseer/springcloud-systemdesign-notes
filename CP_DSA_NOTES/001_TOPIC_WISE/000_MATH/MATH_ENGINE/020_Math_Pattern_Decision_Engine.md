# 020_Math_Pattern_Decision_Engine.md — MiniMathEngine

# Math Pattern Decision Engine

> This is the final MiniMathEngine file.  
> Goal: recognize math patterns fast in CP / FAANG / Codeforces / AtCoder problems.

---

## Clickable Index

1. [What Is The Math Pattern Decision Engine?](#1-what-is-the-math-pattern-decision-engine)
2. [Why This File Matters](#2-why-this-file-matters)
3. [How To Read A Math Problem](#3-how-to-read-a-math-problem)
4. [Master Decision Tree](#4-master-decision-tree)
5. [Modulo Pattern](#5-modulo-pattern)
6. [Fast Power Pattern](#6-fast-power-pattern)
7. [Modular Inverse Pattern](#7-modular-inverse-pattern)
8. [GCD / LCM Pattern](#8-gcd--lcm-pattern)
9. [Prime Pattern](#9-prime-pattern)
10. [Sieve / SPF Pattern](#10-sieve--spf-pattern)
11. [Prime Factorization Pattern](#11-prime-factorization-pattern)
12. [Divisor Count / Sum Pattern](#12-divisor-count--sum-pattern)
13. [Euler Phi / Coprime Pattern](#13-euler-phi--coprime-pattern)
14. [nCr / Combination Pattern](#14-ncr--combination-pattern)
15. [Permutation With Repetition Pattern](#15-permutation-with-repetition-pattern)
16. [Stars And Bars Pattern](#16-stars-and-bars-pattern)
17. [Inclusion Exclusion Pattern](#17-inclusion-exclusion-pattern)
18. [Catalan Pattern](#18-catalan-pattern)
19. [Derangement Pattern](#19-derangement-pattern)
20. [CRT Pattern](#20-crt-pattern)
21. [Diophantine Pattern](#21-diophantine-pattern)
22. [Floor Sum Pattern](#22-floor-sum-pattern)
23. [CP / FAANG Problem Forms](#23-cp--faang-problem-forms)
24. [One-Minute Pattern Recognition Table](#24-one-minute-pattern-recognition-table)
25. [Common Mistake Map](#25-common-mistake-map)
26. [Reusable C++ Snippet Index](#26-reusable-c-snippet-index)
27. [Practice Strategy](#27-practice-strategy)
28. [Final MiniMathEngine Checklist](#28-final-minimathengine-checklist)
29. [Next Step](#29-next-step)

---

## 1. What Is The Math Pattern Decision Engine?

This file is a **pattern recognition engine**.

When you see a math problem, your goal is not to memorize formulas randomly.

Your goal is to ask:

```text
What form is this problem?
```

Then map it to:

```text
Modulo?
GCD?
Prime?
Combination?
Distribution?
Inclusion-exclusion?
CRT?
Diophantine?
Floor sum?
```

This file helps you identify the correct technique quickly.

---

## 2. Why This File Matters

In CP / FAANG OAs, math problems often look scary because they are wrapped in stories.

Example:

```text
There are n employees and nobody should receive their own gift.
```

This is not really an employee problem.

It is:

```text
Derangement
```

Example:

```text
Find x such that x leaves remainder 2 by 3 and remainder 3 by 5.
```

This is:

```text
CRT
```

Example:

```text
Distribute n identical candies among k children.
```

This is:

```text
Stars and Bars
```

The skill is:

```text
story -> math form -> template
```

---

## 3. How To Read A Math Problem

Use this 5-step method:

```text
1. Identify what is being counted / computed.
2. Identify constraints.
3. Convert story to equation or formula.
4. Match equation/form to known pattern.
5. Apply template carefully.
```

---

## 4. Master Decision Tree

```text
Math problem?
|
+-- Contains modulo?
|   |
|   +-- Need power? -> Fast Power
|   |
|   +-- Need division under mod? -> Modular Inverse
|   |
|   +-- Multiple remainder equations? -> CRT
|
+-- Contains gcd/lcm/divisibility?
|   |
|   +-- ax + by = c? -> Diophantine Equation
|   |
|   +-- coprime count? -> Euler Phi
|   |
|   +-- lcm/gcd relation? -> GCD / LCM
|
+-- Contains primes?
|   |
|   +-- One number? -> Prime Checking / Factorization
|   |
|   +-- Many queries? -> Sieve / SPF
|   |
|   +-- divisor count/sum? -> Prime Factorization + Divisor Formula
|
+-- Counting arrangements?
|   |
|   +-- Order matters? -> Permutation
|   |
|   +-- Order does not matter? -> Combination
|   |
|   +-- Repetition allowed? -> Permutation/Combination with repetition
|
+-- Counting distributions?
|   |
|   +-- Identical objects into boxes? -> Stars and Bars
|   |
|   +-- Upper bounds? -> Inclusion Exclusion
|
+-- Overlapping constraints?
|   |
|   +-- Count union / forbidden cases? -> Inclusion Exclusion
|
+-- Recursive balanced structures?
|   |
|   +-- Parentheses / BST / stack-valid / non-crossing? -> Catalan
|
+-- No fixed point?
|   |
|   +-- Nobody gets own item? -> Derangement
|
+-- Contains floor?
    |
    +-- floor(n/i)? -> Harmonic grouping
    |
    +-- floor((a*i+b)/m)? -> Floor Sum
```

---

## 5. Modulo Pattern

### Signal Words

```text
answer modulo 1e9+7
large answer
remainder
wrap around
cyclic
```

### Use When

You need:

```text
(a + b) % MOD
(a * b) % MOD
(a - b + MOD) % MOD
```

### MiniMath File

```text
001_Modulo_Basics.md
```

### Commented C++ Code

```cpp
const long long MOD = 1'000'000'007;

long long addMod(long long a, long long b) {
    return (a + b) % MOD;
}

long long subMod(long long a, long long b) {
    return (a - b + MOD) % MOD;
}

long long mulMod(long long a, long long b) {
    return (a * b) % MOD;
}
```

---

## 6. Fast Power Pattern

### Signal Words

```text
a^b
large exponent
power modulo
binary exponentiation
```

### Pattern

```text
a^b mod m
```

### MiniMath File

```text
002_Fast_Power_Binary_Exponentiation.md
```

### Commented C++ Code

```cpp
long long modPow(long long a, long long e, long long mod) {
    long long res = 1;

    while (e > 0) {
        if (e & 1) res = (res * a) % mod;
        a = (a * a) % mod;
        e >>= 1;
    }

    return res;
}
```

---

## 7. Modular Inverse Pattern

### Signal Words

```text
division under modulo
nCr modulo
a / b mod MOD
inverse
```

### Pattern

```text
a / b mod MOD = a * inverse(b) mod MOD
```

When MOD is prime:

```text
inverse(b) = b^(MOD-2) mod MOD
```

### MiniMath File

```text
003_Modular_Inverse.md
```

---

## 8. GCD / LCM Pattern

### Signal Words

```text
divides
common divisor
least common multiple
gcd
lcm
```

### Core Rules

```text
gcd(a,b)
lcm(a,b) = a / gcd(a,b) * b
```

### MiniMath File

```text
004_GCD_LCM_Euclid.md
```

### Commented C++ Code

```cpp
long long safeLcm(long long a, long long b) {
    return a / gcd(a, b) * b;
}
```

---

## 9. Prime Pattern

### Signal Words

```text
prime
composite
divisors exactly 2
factor
```

### Use

For one number:

```text
check divisibility up to sqrt(n)
```

### MiniMath File

```text
005_Prime_Checking.md
```

---

## 10. Sieve / SPF Pattern

### Signal Words

```text
many prime queries
factorize many numbers
precompute primes
smallest prime factor
```

### Use

For many queries:

```text
Sieve
SPF
```

### MiniMath File

```text
006_Sieve_And_SPF.md
```

---

## 11. Prime Factorization Pattern

### Signal Words

```text
prime powers
factor decomposition
divisor count
divisor sum
```

### Example

```text
n = p1^a1 * p2^a2 * ...
```

### MiniMath File

```text
007_Prime_Factorization.md
```

---

## 12. Divisor Count / Sum Pattern

### Signal Words

```text
number of divisors
sum of divisors
factor powers
```

### Formula

If:

```text
n = p1^a1 * p2^a2 * ...
```

Then divisor count:

```text
(a1+1)(a2+1)...
```

### MiniMath File

```text
008_Divisors_Count_Sum.md
```

---

## 13. Euler Phi / Coprime Pattern

### Signal Words

```text
coprime
relatively prime
count numbers <= n with gcd(i,n)=1
```

### Formula

```text
phi(n) = n * Π(1 - 1/p)
```

for unique prime factors p of n.

### MiniMath File

```text
009_Euler_Phi_Coprime.md
```

---

## 14. nCr / Combination Pattern

### Signal Words

```text
choose
select
order does not matter
combination
```

### Formula

```text
C(n,r) = n! / (r!(n-r)!)
```

### MiniMath Files

```text
010_nCr_Basic.md
011_nCr_Mod_Prime_Precompute.md
```

---

## 15. Permutation With Repetition Pattern

### Signal Words

```text
arrange
order matters
repetition allowed
password
strings of length r
```

### Formula

```text
n^r
```

### MiniMath File

```text
012_Permutations_Repetition.md
```

---

## 16. Stars And Bars Pattern

### Signal Words

```text
distribute identical items
non-negative integer solutions
x1 + x2 + ... + xk = n
at least one
repetition allowed choose
```

### Formula

For:

```text
x1 + x2 + ... + xk = n
xi >= 0
```

Answer:

```text
C(n+k-1,k-1)
```

### MiniMath File

```text
013_Stars_And_Bars.md
```

---

## 17. Inclusion Exclusion Pattern

### Signal Words

```text
overlap
at least one
none of these
forbidden
divisible by any
upper bound
```

### Pattern

```text
add singles
subtract pairs
add triples
subtract quadruples
```

### MiniMath File

```text
014_Inclusion_Exclusion.md
```

---

## 18. Catalan Pattern

### Signal Words

```text
balanced parentheses
BST count
valid stack sequence
non-crossing chords
mountain ranges
recursive split
```

### Formula

```text
Cn = C(2n,n)/(n+1)
```

### MiniMath File

```text
015_Catalan_Numbers.md
```

---

## 19. Derangement Pattern

### Signal Words

```text
no fixed point
nobody gets own item
Secret Santa
hat check
perm[i] != i
```

### Recurrence

```text
D(n) = (n-1)(D(n-1)+D(n-2))
```

### MiniMath File

```text
016_Derangements.md
```

---

## 20. CRT Pattern

### Signal Words

```text
x leaves remainder
multiple modulo equations
cycle alignment
same day across periods
```

### Form

```text
x ≡ r1 mod m1
x ≡ r2 mod m2
```

### MiniMath File

```text
017_CRT.md
```

---

## 21. Diophantine Pattern

### Signal Words

```text
ax + by = c
integer solution
coin sizes
linear combination
can form amount
```

### Existence

```text
gcd(a,b) divides c
```

### MiniMath File

```text
018_Diophantine_Equation.md
```

---

## 22. Floor Sum Pattern

### Signal Words

```text
floor
sum floor(n/i)
sum floor((a*i+b)/m)
lattice points
quotient grouping
```

### MiniMath File

```text
019_Floor_Sum.md
```

---

## 23. CP / FAANG Problem Forms

---

### Problem 1 — Large Power Modulo

#### Pattern

```text
Fast Power
```

#### Step-by-Step Working

```text
Need a^b mod MOD
b is huge
Use binary exponentiation
```

#### Commented C++ Code

```cpp
long long modPow(long long a, long long e, long long mod) {
    long long res = 1;

    while (e) {
        if (e & 1) res = res * a % mod;
        a = a * a % mod;
        e >>= 1;
    }

    return res;
}
```

---

### Problem 2 — Count Ways To Choose

#### Pattern

```text
nCr
```

#### Step-by-Step Working

```text
Order does not matter
Need choose r from n
Use C(n,r)
```

#### Commented C++ Code

```cpp
long long nCr(int n, int r) {
    if (r < 0 || r > n) return 0;

    r = min(r, n - r);

    long long ans = 1;

    for (int i = 1; i <= r; i++) {
        ans = ans * (n - r + i) / i;
    }

    return ans;
}
```

---

### Problem 3 — Distribute Candies

#### Pattern

```text
Stars and Bars
```

#### Step-by-Step Working

```text
n identical candies
k children
xi >= 0
x1 + x2 + ... + xk = n
Answer = C(n+k-1,k-1)
```

#### Commented C++ Code

```cpp
long long distributeNonNegative(int n, int k) {
    return nCr(n + k - 1, k - 1);
}
```

---

### Problem 4 — Count Numbers Divisible By Any Prime

#### Pattern

```text
Inclusion Exclusion
```

#### Step-by-Step Working

```text
For every non-empty subset
compute LCM
odd subset size -> add
even subset size -> subtract
```

#### Commented C++ Code

```cpp
long long countDivisible(long long N, vector<int> nums) {
    int k = nums.size();

    long long ans = 0;

    for (int mask = 1; mask < (1 << k); mask++) {
        long long lcm = 1;
        int bits = 0;

        for (int i = 0; i < k; i++) {
            if (mask & (1 << i)) {
                bits++;
                lcm = lcm / gcd(lcm, (long long)nums[i]) * nums[i];
            }
        }

        long long contribution = N / lcm;

        if (bits % 2) ans += contribution;
        else ans -= contribution;
    }

    return ans;
}
```

---

### Problem 5 — Unique BST Count

#### Pattern

```text
Catalan
```

#### Step-by-Step Working

```text
Choose root
left subtree
right subtree
sum over all roots
```

#### Commented C++ Code

```cpp
long long countBST(int n) {
    vector<long long> dp(n + 1);

    dp[0] = 1;

    for (int nodes = 1; nodes <= n; nodes++) {
        for (int left = 0; left < nodes; left++) {
            int right = nodes - left - 1;
            dp[nodes] += dp[left] * dp[right];
        }
    }

    return dp[n];
}
```

---

### Problem 6 — Secret Santa

#### Pattern

```text
Derangement
```

#### Step-by-Step Working

```text
Nobody gets own gift
No fixed point permutation
Use derangement recurrence
```

#### Commented C++ Code

```cpp
long long derangement(int n) {
    vector<long long> dp(n + 1);

    dp[0] = 1;

    if (n >= 1) dp[1] = 0;

    for (int i = 2; i <= n; i++) {
        dp[i] = (i - 1) * (dp[i - 1] + dp[i - 2]);
    }

    return dp[n];
}
```

---

### Problem 7 — Multiple Remainders

#### Pattern

```text
CRT
```

#### Step-by-Step Working

```text
x ≡ r1 mod m1
x ≡ r2 mod m2
Use CRT if moduli are coprime
```

#### Commented C++ Code

```cpp
long long egcd(long long a, long long b, long long &x, long long &y) {
    if (b == 0) {
        x = 1;
        y = 0;
        return a;
    }

    long long x1, y1;
    long long g = egcd(b, a % b, x1, y1);

    x = y1;
    y = x1 - (a / b) * y1;

    return g;
}
```

---

### Problem 8 — Coin Equation

#### Pattern

```text
Diophantine Equation
```

#### Step-by-Step Working

```text
ax + by = c
Solution exists iff gcd(a,b) divides c
```

#### Commented C++ Code

```cpp
bool canForm(long long a, long long b, long long c) {
    return c % gcd(a, b) == 0;
}
```

---

### Problem 9 — Sum floor(n/i)

#### Pattern

```text
Floor Sum / Harmonic Grouping
```

#### Step-by-Step Working

```text
floor(n/i) has repeated quotient blocks
Process ranges together
```

#### Commented C++ Code

```cpp
long long sumFloor(long long n) {
    long long ans = 0;

    for (long long l = 1; l <= n; ) {
        long long q = n / l;
        long long r = n / q;

        ans += q * (r - l + 1);

        l = r + 1;
    }

    return ans;
}
```

---

## 24. One-Minute Pattern Recognition Table

| Problem Signal | Pattern |
|---|---|
| `a^b mod m` | Fast Power |
| `a / b mod m` | Modular Inverse |
| many prime queries | Sieve |
| factor many numbers | SPF |
| count divisors | Prime factorization |
| count coprime | Euler Phi |
| choose r from n | nCr |
| order matters | Permutation |
| repetition allowed | Power / repetition |
| distribute identical objects | Stars and Bars |
| upper bound in distribution | Inclusion Exclusion |
| overlapping sets | Inclusion Exclusion |
| valid parentheses | Catalan |
| unique BST | Catalan |
| nobody gets own item | Derangement |
| multiple remainders | CRT |
| `ax + by = c` | Diophantine |
| floor expression | Floor Sum |
| sum `floor(n/i)` | Harmonic grouping |

---

## 25. Common Mistake Map

| Mistake | Fix |
|---|---|
| using permutation when order does not matter | use nCr |
| using nCr when repetition allowed | use stars and bars |
| ignoring overlap | use inclusion-exclusion |
| dividing directly under modulo | use modular inverse |
| using CRT without coprime check | verify gcd compatibility |
| forgetting gcd condition in ax+by=c | check `c % gcd == 0` |
| using floating point for floor | use integer division |
| forgetting modulo normalization | `(x % MOD + MOD) % MOD` |
| using naive floor loop for huge n | use grouping |
| confusing derangement with factorial | use no-fixed-point recurrence |

---

## 26. Reusable C++ Snippet Index

```text
modPow                  -> Fast exponentiation
modInverse              -> Division under prime modulo
nCr                     -> Basic combination
nCrMod                  -> Combination under modulo
sieve                   -> Prime preprocessing
spfFactorize            -> Fast factorization
phi                     -> Euler totient
countDivisors           -> Divisor count
starsBars               -> Integer distributions
inclusionExclusion      -> Overlap counting
catalan                 -> Balanced structures
derangement             -> No fixed point permutations
crt                     -> Multiple remainders
extendedGcd             -> Diophantine / inverse
floor_sum               -> AtCoder floor sum
sumFloor                -> Harmonic grouping
```

---

## 27. Practice Strategy

Use this method for math practice:

```text
1. Read problem statement.
2. Write the mathematical form.
3. Match with one MiniMathEngine topic.
4. Write formula before code.
5. Dry run with small input.
6. Code from template.
7. Test edge cases.
```

---

## 28. Final MiniMathEngine Checklist

You are ready for most CP/FAANG math problems if you can identify:

```text
✓ modulo arithmetic
✓ binary exponentiation
✓ modular inverse
✓ gcd/lcm
✓ prime checking
✓ sieve/SPF
✓ factorization
✓ divisor count/sum
✓ Euler phi
✓ nCr
✓ nCr modulo prime
✓ permutations with repetition
✓ stars and bars
✓ inclusion-exclusion
✓ Catalan
✓ derangement
✓ CRT
✓ Diophantine equation
✓ floor sum
```

---

## 29. Next Step

After MiniMathEngine, the next logical engine is:

```text
MiniBitwiseEngine
```

or:

```text
MiniDPPatternEngine
```

Recommended order:

```text
1. MiniBitwiseEngine
2. MiniRecursionBacktrackingEngine
3. MiniDPPatternEngine
4. MiniGraphMathEngine
```

This completes the math base and prepares you for higher-level CP pattern recognition.
