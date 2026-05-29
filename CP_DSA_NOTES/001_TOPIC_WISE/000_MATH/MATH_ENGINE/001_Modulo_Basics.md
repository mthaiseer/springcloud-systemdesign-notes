# 001_Modulo_Basics.md

# MiniMathEngine — Modulo Basics

> **Goal:** Build bottom-up intuition for modular arithmetic so later topics like fast power, modular inverse, nCr under MOD, CRT, hashing, and number theory feel natural instead of formula-based.

---

## Clickable Index

- [1. Why Modulo Matters](#1-why-modulo-matters)
- [2. Core Mental Model](#2-core-mental-model)
- [3. Clock Arithmetic Intuition](#3-clock-arithmetic-intuition)
- [4. Problem Pattern Detector](#4-problem-pattern-detector)
- [5. Core Rules](#5-core-rules)
- [6. Step-by-Step Working](#6-step-by-step-working)
- [7. C++ Code With Comments](#7-c-code-with-comments)
- [8. Java Logic Comments](#8-java-logic-comments)
- [9. Common Mistakes](#9-common-mistakes)
- [10. CP / FAANG Practice Problems](#10-cp--faang-practice-problems)
- [11. Real-World Mapping](#11-real-world-mapping)
- [12. What Changes In Next Step](#12-what-changes-in-next-step)
- [13. Next File](#13-next-file)

---

# 1. Why Modulo Matters

Modulo appears whenever numbers become too large or we only care about the remainder.

In CP / DSA, modulo is used because answers can be huge:

```text
Number of ways = 123456789123456789123456789
Return answer % 1_000_000_007
```

In real systems, modulo appears in:

```text
hash(key) % bucket_count
user_id % shard_count
request_id % partition_count
clock/timer cycles
consistent hashing rings
cryptography
```

So modulo is not just a CP trick. It is a **cyclic arithmetic engine**.

---

# 2. Core Mental Model

```text
Modulo = keep number inside a fixed circular range.
```

For MOD = 7:

```text
0, 1, 2, 3, 4, 5, 6, then back to 0
```

So:

```text
7  % 7 = 0
8  % 7 = 1
9  % 7 = 2
10 % 7 = 3
```

Think of numbers walking around a circle.

```text
Number line:
0 1 2 3 4 5 6 7 8 9 10 11 12 13 14

Modulo 7:
0 1 2 3 4 5 6 0 1 2  3  4  5  6  0
```

---

# 3. Clock Arithmetic Intuition

Clock is the best real-life example.

For a 12-hour clock:

```text
13 o'clock = 1 o'clock
25 o'clock = 1 o'clock
37 o'clock = 1 o'clock
```

Because:

```text
13 % 12 = 1
25 % 12 = 1
37 % 12 = 1
```

Modulo means:

```text
After crossing boundary, wrap around.
```

---

# 4. Problem Pattern Detector

Use modulo when you see:

```text
1. Answer can be very large
2. Return answer modulo 1e9+7 / 998244353
3. Count number of ways
4. Product of many numbers
5. Repeated multiplication
6. Hashing / bucket / shard / partition
7. Cyclic behavior
8. Circular array / clock / rotation
```

Decision tree:

```text
Problem has huge answer?
        |
        v
Need answer % MOD?
        |
        v
Use modular arithmetic from the beginning.
```

Important:

```text
Do not compute huge value first and then take MOD.
Apply MOD at every step.
```

Bad:

```cpp
ans = a * b * c * d;
ans %= MOD;
```

Good:

```cpp
ans = 1;
ans = (ans * a) % MOD;
ans = (ans * b) % MOD;
ans = (ans * c) % MOD;
ans = (ans * d) % MOD;
```

---

# 5. Core Rules

Let:

```text
MOD = m
```

## 5.1 Addition

```text
(a + b) % m = ((a % m) + (b % m)) % m
```

## 5.2 Subtraction

```text
(a - b) % m = ((a % m) - (b % m) + m) % m
```

Why add `m`?

Because C++ can produce negative remainder.

Example:

```text
(3 - 5) % 7 = -2 in C++ style
Correct positive answer = 5
```

So:

```text
(3 - 5 + 7) % 7 = 5
```

## 5.3 Multiplication

```text
(a * b) % m = ((a % m) * (b % m)) % m
```

But beware overflow if `a` and `b` are extremely large.

For normal CP with `MOD = 1e9+7`, `long long` is usually enough for:

```text
(1e9 * 1e9) = 1e18
```

which fits inside signed 64-bit approximately up to `9e18`.

## 5.4 Division Is Special

This is wrong in modular arithmetic:

```text
(a / b) % MOD
```

You cannot directly divide under modulo.

Instead later we use:

```text
a / b mod MOD = a * inverse(b) mod MOD
```

That comes in:

```text
003_Modular_Inverse.md
```

---

# 6. Step-by-Step Working

## Example 1 — Addition

```text
MOD = 7
A = 10
B = 12
Find (A + B) % MOD
```

Step-by-step:

```text
A % 7 = 10 % 7 = 3
B % 7 = 12 % 7 = 5

(A + B) % 7
= (3 + 5) % 7
= 8 % 7
= 1
```

Answer:

```text
1
```

---

## Example 2 — Subtraction

```text
MOD = 7
A = 3
B = 5
Find (A - B) % MOD
```

Naive:

```text
3 - 5 = -2
-2 % 7 = -2 in C++
```

But we want positive modulo range:

```text
0 to 6
```

Correct:

```text
(3 - 5 + 7) % 7
= 5 % 7
= 5
```

Answer:

```text
5
```

---

## Example 3 — Multiplication

```text
MOD = 7
A = 10
B = 12
Find (A * B) % MOD
```

Step-by-step:

```text
A % 7 = 3
B % 7 = 5

(A * B) % 7
= (3 * 5) % 7
= 15 % 7
= 1
```

Answer:

```text
1
```

---

## Example 4 — Running Product

```text
Find product of [10, 12, 15, 20] modulo 7
```

Step-by-step:

```text
ans = 1

x = 10
ans = (1 * 10) % 7 = 3

x = 12
ans = (3 * 12) % 7 = 36 % 7 = 1

x = 15
ans = (1 * 15) % 7 = 1

x = 20
ans = (1 * 20) % 7 = 6
```

Answer:

```text
6
```

---

# 7. C++ Code With Comments

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

// Most common CP modulo.
// It is prime, which becomes useful later for modular inverse.
const ll MOD = 1'000'000'007;

// ------------------------------------------------------------
// Step 1: Normalize number into range [0, MOD - 1]
// ------------------------------------------------------------
ll normalize(ll x) {
    // C++ can give negative remainder for negative x.
    // Example: -2 % 7 = -2
    x %= MOD;

    // If negative, push it back into positive modulo range.
    if (x < 0) x += MOD;

    return x;
}

// ------------------------------------------------------------
// Step 2: Modular addition
// Formula:
// (a + b) % MOD = ((a % MOD) + (b % MOD)) % MOD
// ------------------------------------------------------------
ll modAdd(ll a, ll b) {
    a = normalize(a);
    b = normalize(b);

    return (a + b) % MOD;
}

// ------------------------------------------------------------
// Step 3: Modular subtraction
// Formula:
// (a - b) % MOD = ((a % MOD) - (b % MOD) + MOD) % MOD
// ------------------------------------------------------------
ll modSub(ll a, ll b) {
    a = normalize(a);
    b = normalize(b);

    // Add MOD to avoid negative result.
    return (a - b + MOD) % MOD;
}

// ------------------------------------------------------------
// Step 4: Modular multiplication
// Formula:
// (a * b) % MOD = ((a % MOD) * (b % MOD)) % MOD
// ------------------------------------------------------------
ll modMul(ll a, ll b) {
    a = normalize(a);
    b = normalize(b);

    return (a * b) % MOD;
}

// ------------------------------------------------------------
// Step 5: Running product under modulo
// Useful for factorials, product arrays, combinatorics, DP counts.
// ------------------------------------------------------------
ll productMod(const vector<ll>& nums) {
    ll ans = 1;

    for (ll x : nums) {
        // Always take MOD at every multiplication step.
        ans = modMul(ans, x);
    }

    return ans;
}

// ------------------------------------------------------------
// Driver code
// ------------------------------------------------------------
int main() {
    cout << "Modulo Basics Demo\n";

    ll a = 10;
    ll b = 20;

    cout << "modAdd(10, 20) = " << modAdd(a, b) << "\n";
    cout << "modSub(10, 20) = " << modSub(a, b) << "\n";
    cout << "modMul(10, 20) = " << modMul(a, b) << "\n";

    vector<ll> nums = {10, 12, 15, 20};
    cout << "productMod({10,12,15,20}) = " << productMod(nums) << "\n";

    cout << "normalize(-2) = " << normalize(-2) << "\n";

    return 0;
}
```

---

# 8. Java Logic Comments

Same logic in Java mindset:

```java
/*
Step 1:
Use long instead of int for multiplication.

Step 2:
Normalize every value:
x = x % MOD
if x < 0, x += MOD

Step 3:
Addition:
(a + b) % MOD

Step 4:
Subtraction:
(a - b + MOD) % MOD

Step 5:
Multiplication:
(a * b) % MOD

Step 6:
For factorial / counting / DP:
Always take MOD during each update.
Never wait until the final answer.
*/
```

Java skeleton:

```java
class ModuloBasics {
    static final long MOD = 1_000_000_007L;

    static long normalize(long x) {
        x %= MOD;
        if (x < 0) x += MOD;
        return x;
    }

    static long modAdd(long a, long b) {
        a = normalize(a);
        b = normalize(b);
        return (a + b) % MOD;
    }

    static long modSub(long a, long b) {
        a = normalize(a);
        b = normalize(b);
        return (a - b + MOD) % MOD;
    }

    static long modMul(long a, long b) {
        a = normalize(a);
        b = normalize(b);
        return (a * b) % MOD;
    }
}
```

---

# 9. Common Mistakes

## Mistake 1 — Taking MOD only at the end

Bad:

```cpp
long long ans = 1;
for (long long x : nums) {
    ans *= x;
}
ans %= MOD;
```

Problem:

```text
ans may overflow before modulo.
```

Good:

```cpp
long long ans = 1;
for (long long x : nums) {
    ans = (ans * x) % MOD;
}
```

---

## Mistake 2 — Negative modulo

Bad:

```cpp
return (a - b) % MOD;
```

Good:

```cpp
return (a - b + MOD) % MOD;
```

Safer if values can be very negative:

```cpp
return ((a - b) % MOD + MOD) % MOD;
```

---

## Mistake 3 — Direct division under MOD

Bad:

```cpp
ans = (a / b) % MOD;
```

Correct later:

```cpp
ans = a * modInverse(b) % MOD;
```

---

## Mistake 4 — Using int for multiplication

Bad:

```cpp
int ans = (a * b) % MOD;
```

Good:

```cpp
long long ans = (1LL * a * b) % MOD;
```

---

# 10. CP / FAANG Practice Problems

Start with easy intuition problems:

```text
1. Compute sum of array modulo MOD
2. Compute product of array modulo MOD
3. Count number of paths in grid modulo MOD
4. Fibonacci modulo MOD
5. Factorial modulo MOD
6. Number of ways DP problems with MOD
```

LeetCode-style examples:

```text
- Unique Paths variants
- Count Good Numbers
- Number of Ways to Stay in Same Place
- Decode Ways variants with modulo
- Count Vowels Permutation
```

CP-style examples:

```text
- Compute n! % MOD
- Compute sum of first n numbers % MOD
- Count binary strings of length n % MOD
- Count subsequences % MOD
```

---

# 11. Real-World Mapping

## 11.1 Hash Buckets

```text
bucket = hash(key) % bucket_count
```

Used in:

```text
HashMap
Redis-style key distribution
cache buckets
load balancer hashing
```

---

## 11.2 Kafka Partitioning

```text
partition = hash(message_key) % number_of_partitions
```

Modulo decides where message goes.

---

## 11.3 Database Sharding

```text
shard = user_id % shard_count
```

Simple modulo sharding.

---

## 11.4 Time Cycles

```text
current_slot = current_time % wheel_size
```

Used in:

```text
timer wheels
schedulers
rate limiters
round-robin systems
```

---

## 11.5 Cryptography Foundation

Modulo arithmetic is the base of:

```text
RSA
Diffie-Hellman
Elliptic Curve Cryptography
JWT signing internals
TLS/HTTPS
```

Later fast power + modular inverse will connect strongly here.

---

# 12. What Changes In Next Step

In this file, we learned:

```text
safe add/sub/mul under MOD
```

But what if we need:

```text
a^b % MOD
```

Naive method:

```text
multiply a, b times
```

Too slow when:

```text
b = 10^18
```

Next file introduces:

```text
Binary Exponentiation
```

Core idea:

```text
Use bits of exponent to reduce O(b) to O(log b)
```

---

# 13. Next File

```text
002_Fast_Power_Binary_Exponentiation.md
```

You will learn:

```text
1. Why normal power is too slow
2. How exponent bits work
3. Recursive binary exponentiation
4. Iterative binary exponentiation
5. Modular power
6. CP/FAANG patterns
7. Real-world mapping to cryptography and simulations
```
