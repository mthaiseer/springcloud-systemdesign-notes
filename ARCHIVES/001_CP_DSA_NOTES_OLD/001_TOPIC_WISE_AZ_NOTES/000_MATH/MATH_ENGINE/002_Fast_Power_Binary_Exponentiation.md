# 002_Fast_Power_Binary_Exponentiation.md

# MiniMathEngine — Fast Power / Binary Exponentiation

> Goal: learn how to compute huge powers like `a^b` or `a^b % MOD` in `O(log b)` instead of `O(b)`.

---

# Clickable Index

- [1. Why This Topic Matters](#1-why-this-topic-matters)
- [2. Bottom-Up Intuition](#2-bottom-up-intuition)
- [3. Naive Power vs Fast Power](#3-naive-power-vs-fast-power)
- [4. Core Pattern](#4-core-pattern)
- [5. Bit Representation Intuition](#5-bit-representation-intuition)
- [6. Step-by-Step Dry Run](#6-step-by-step-dry-run)
- [7. Recursive C++ Code](#7-recursive-c-code)
- [8. Iterative C++ Code](#8-iterative-c-code)
- [9. Modular Fast Power C++ Template](#9-modular-fast-power-c-template)
- [10. Java Logic Comments](#10-java-logic-comments)
- [11. Common Mistakes](#11-common-mistakes)
- [12. Pattern Detector](#12-pattern-detector)
- [13. CP / FAANG Practice Problems](#13-cp--faang-practice-problems)
- [14. Real-World Mapping](#14-real-world-mapping)
- [15. Next Step](#15-next-step)

---

# 1. Why This Topic Matters

Many CP and interview problems ask for something like:

```text
2^1000000000 % MOD
3^n % MOD
number_of_ways = base^positions % MOD
```

Naive multiplication is too slow.

```cpp
for (int i = 1; i <= b; i++) {
    ans *= a;
}
```

This takes:

```text
O(b)
```

If `b = 10^9`, this is impossible.

Fast power reduces it to:

```text
O(log b)
```

because every step divides the exponent by `2`.

---

# 2. Bottom-Up Intuition

Power means repeated multiplication.

```text
2^8 = 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2
```

But instead of multiplying 8 times, we can square:

```text
2^1 = 2
2^2 = 4
2^4 = 16
2^8 = 256
```

So:

```text
2^8
= ((2^2)^2)^2
```

This is the key idea:

```text
Reuse previous square instead of multiplying one by one.
```

---

# 3. Naive Power vs Fast Power

## Naive

```text
a^b = multiply a exactly b times
```

Time:

```text
O(b)
```

## Fast Power

```text
If b is even:
a^b = (a^(b/2)) * (a^(b/2))

If b is odd:
a^b = a * a^(b-1)
```

Better form:

```text
If b is odd, take current base into answer.
Then square base and halve b.
```

Time:

```text
O(log b)
```

---

# 4. Core Pattern

```text
answer = 1
base = a
exponent = b

while exponent > 0:
    if exponent is odd:
        answer = answer * base

    base = base * base
    exponent = exponent / 2
```

Modulo version:

```text
answer = 1
base = a % MOD

while b > 0:
    if b is odd:
        answer = (answer * base) % MOD

    base = (base * base) % MOD
    b = b / 2
```

---

# 5. Bit Representation Intuition

Every number can be represented using powers of 2.

Example:

```text
13 = 8 + 4 + 1
13 = 2^3 + 2^2 + 2^0
```

So:

```text
a^13 = a^(8 + 4 + 1)
     = a^8 * a^4 * a^1
```

Binary of `13`:

```text
13 = 1101₂
```

Each `1` bit means:

```text
Use this current power of base.
```

Plain diagram:

```text
b = 13
binary = 1101

power positions:
2^0 -> 1  -> use a^1
2^1 -> 2  -> skip a^2
2^2 -> 4  -> use a^4
2^3 -> 8  -> use a^8

Final:
a^13 = a^1 * a^4 * a^8
```

---

# 6. Step-by-Step Dry Run

Compute:

```text
3^13
```

Initial:

```text
answer = 1
base = 3
b = 13
```

| Step | b | b odd? | answer update | base update | new b |
|---|---:|---|---|---|---:|
| 1 | 13 | yes | ans = 1 * 3 = 3 | base = 3 * 3 = 9 | 6 |
| 2 | 6 | no | ans = 3 | base = 9 * 9 = 81 | 3 |
| 3 | 3 | yes | ans = 3 * 81 = 243 | base = 81 * 81 = 6561 | 1 |
| 4 | 1 | yes | ans = 243 * 6561 = 1594323 | base = 6561 * 6561 | 0 |

Final:

```text
3^13 = 1594323
```

Decision flow:

```text
Start
  |
  v
Is b > 0?
  |
  +-- no --> return answer
  |
 yes
  |
  v
Is b odd?
  |
  +-- yes --> answer = answer * base
  |
  +-- no  --> answer unchanged
  |
  v
base = base * base
b = b / 2
  |
  v
Repeat
```

---

# 7. Recursive C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

// Function: computes a^b without modulo
// Time Complexity: O(log b)
// Space Complexity: O(log b) due to recursion stack
ll powerRecursive(ll a, ll b) {
    // Step 1: Base case
    // Any number power 0 is 1
    if (b == 0) {
        return 1;
    }

    // Step 2: Solve smaller problem
    // Compute a^(b/2)
    ll half = powerRecursive(a, b / 2);

    // Step 3: Square the half result
    ll result = half * half;

    // Step 4: If exponent is odd, multiply one extra a
    if (b % 2 == 1) {
        result = result * a;
    }

    // Step 5: Return final result
    return result;
}

int main() {
    cout << powerRecursive(3, 13) << "\n"; // 1594323
    return 0;
}
```

## Why recursive works

```text
3^13
= 3 * 3^12
= 3 * (3^6)^2
= 3 * ((3^3)^2)^2
```

The recursion keeps reducing:

```text
13 -> 6 -> 3 -> 1 -> 0
```

---

# 8. Iterative C++ Code

This is the most common CP template.

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

// Function: computes a^b without modulo
// Time Complexity: O(log b)
// Space Complexity: O(1)
ll powerIterative(ll a, ll b) {
    // Step 1: answer starts as multiplicative identity
    ll ans = 1;

    // Step 2: base starts as a
    ll base = a;

    // Step 3: process exponent until it becomes 0
    while (b > 0) {

        // Step 4: If current bit of exponent is 1,
        // multiply current base into answer
        if (b % 2 == 1) {
            ans = ans * base;
        }

        // Step 5: Square the base for next bit position
        base = base * base;

        // Step 6: Move to next bit by dividing exponent by 2
        b = b / 2;
    }

    // Step 7: Final answer
    return ans;
}

int main() {
    cout << powerIterative(3, 13) << "\n"; // 1594323
    return 0;
}
```

---

# 9. Modular Fast Power C++ Template

Use this in most CP problems.

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll MOD = 1e9 + 7;

// Function: computes (a^b) % MOD safely
// Time Complexity: O(log b)
// Space Complexity: O(1)
ll modPower(ll a, ll b) {
    // Step 1: Normalize base under modulo
    a %= MOD;

    // Step 2: answer starts as 1
    ll ans = 1;

    // Step 3: Process exponent bits
    while (b > 0) {

        // Step 4: If current exponent bit is 1,
        // include current base in answer
        if (b & 1) {
            ans = (ans * a) % MOD;
        }

        // Step 5: Square base under modulo
        a = (a * a) % MOD;

        // Step 6: Shift exponent right by 1 bit
        b >>= 1;
    }

    // Step 7: Return final modular power
    return ans;
}

int main() {
    cout << modPower(3, 13) << "\n"; // 1594323
    cout << modPower(2, 1000000000LL) << "\n";
    return 0;
}
```

## Why `% MOD` after each multiplication?

Because numbers grow extremely fast.

```text
2^1000000000
```

cannot fit in normal integer types.

So we keep every intermediate value small:

```cpp
ans = (ans * a) % MOD;
a = (a * a) % MOD;
```

---

# 10. Java Logic Comments

Use this same logic in Java:

```java
static final long MOD = 1_000_000_007L;

static long modPower(long a, long b) {
    // Step 1: normalize base
    a %= MOD;

    // Step 2: answer starts from 1
    long ans = 1;

    // Step 3: process exponent bits
    while (b > 0) {

        // Step 4: if current bit is 1, use current base
        if ((b & 1) == 1) {
            ans = (ans * a) % MOD;
        }

        // Step 5: square base for next power of two
        a = (a * a) % MOD;

        // Step 6: move to next bit
        b >>= 1;
    }

    // Step 7: return final answer
    return ans;
}
```

---

# 11. Common Mistakes

## Mistake 1: Forgetting modulo after multiplication

Wrong:

```cpp
ans = ans * a;
```

Correct:

```cpp
ans = (ans * a) % MOD;
```

---

## Mistake 2: Using int when multiplication may overflow

Wrong:

```cpp
int ans = 1;
int a = 1e9;
```

Correct:

```cpp
long long ans = 1;
long long a = 1e9;
```

---

## Mistake 3: Thinking fast power only works for modulo

It works for normal power too.

But modulo version is more common in CP.

---

## Mistake 4: Not handling exponent 0

```text
a^0 = 1
```

Always remember this base case.

---

## Mistake 5: Negative base issue

If `a` may be negative:

```cpp
a = ((a % MOD) + MOD) % MOD;
```

This normalizes it into positive modulo range.

---

# 12. Pattern Detector

Use fast power when you see:

```text
1. Huge exponent
2. a^b % MOD
3. number of ways involving repeated choices
4. repeated doubling/squaring
5. modular inverse using Fermat
6. matrix exponentiation later
7. state transition repeated many times
```

Examples:

```text
2^n % MOD
3^(n-1) % MOD
10^k % MOD
base^positions % MOD
```

Mental trigger:

```text
Exponent is huge?
=> Think binary exponentiation.
```

---

# 13. CP / FAANG Practice Problems

## Basic

```text
1. Compute a^b
2. Compute a^b % MOD
3. Last digit of a^b
4. Count binary strings of length n = 2^n
```

## Medium

```text
1. Count good numbers
2. Super Pow
3. Pow(x, n)
4. Modular inverse using fast power
5. Number of ways with repeated independent choices
```

## Advanced Later

```text
1. Matrix exponentiation Fibonacci
2. Linear recurrence using matrix power
3. Fast exponentiation of permutations
4. RSA-style modular power
```

---

# 14. Real-World Mapping

## Cryptography

RSA and many cryptographic algorithms need:

```text
a^b mod m
```

with extremely large numbers.

Fast power is the core optimization.

---

## Distributed Systems

Repeated exponential growth or decay modeling:

```text
replicas doubling
retry backoff
capacity growth
```

Example:

```text
retry delay = base * 2^attempt
```

Fast power helps compute large retry levels efficiently.

---

## Graphics / Simulation

Repeated transformations can be optimized using power-like methods.

Example:

```text
Apply same transformation N times
=> use exponentiation idea
```

---

## CP / Interview Intuition

Fast power teaches a very important general idea:

```text
Use binary representation to reduce repeated work.
```

This idea appears again in:

```text
binary lifting
segment trees
sparse table
matrix exponentiation
bitmask DP
```

---

# 15. Next Step

Next file:

```text
003_Modular_Inverse.md
```

Because modular inverse uses fast power:

```text
a^(MOD-2) % MOD
```

when `MOD` is prime.

Learning flow:

```text
Modulo Basics
→ Fast Power
→ Modular Inverse
→ nCr % MOD
```

This is one of the most important CP math chains.

