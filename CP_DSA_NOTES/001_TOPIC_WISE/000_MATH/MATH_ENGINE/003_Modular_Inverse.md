# 003_Modular_Inverse.md

# MiniMathEngine — Modular Inverse

> Goal: build bottom-up intuition for **division under modulo**.
>
> This file continues from:
>
> ```text
> 001_Modulo_Basics.md
> 002_Fast_Power_Binary_Exponentiation.md
> ```
>
> You already learned:
>
> ```text
> modulo = cyclic arithmetic
> fast power = compute huge powers using bits
> ```
>
> Now we use both ideas to understand:
>
> ```text
> modular inverse = division inside modulo world
> ```

---

# Clickable Index

- [1. Why This Topic Matters](#1-why-this-topic-matters)
- [2. Problem With Division Under MOD](#2-problem-with-division-under-mod)
- [3. Core Mental Model](#3-core-mental-model)
- [4. Modular Inverse Definition](#4-modular-inverse-definition)
- [5. Small Example By Brute Force](#5-small-example-by-brute-force)
- [6. When Does Modular Inverse Exist?](#6-when-does-modular-inverse-exist)
- [7. Fermat's Little Theorem Method](#7-fermats-little-theorem-method)
- [8. Why Fermat Gives The Inverse](#8-why-fermat-gives-the-inverse)
- [9. Extended Euclid Method](#9-extended-euclid-method)
- [10. Fermat vs Extended Euclid](#10-fermat-vs-extended-euclid)
- [11. Step-by-Step Working — inverse(3) mod 7](#11-step-by-step-working--inverse3-mod-7)
- [12. Step-by-Step Working — Division Example](#12-step-by-step-working--division-example)
- [13. Pattern Detector](#13-pattern-detector)
- [14. C++ Code 1 — Fermat Method](#14-c-code-1--fermat-method)
- [15. C++ Code 2 — Extended Euclid Method](#15-c-code-2--extended-euclid-method)
- [16. C++ Code 3 — Modular Division Helper](#16-c-code-3--modular-division-helper)
- [17. C++ Code 4 — nCr Preview Using Inverse](#17-c-code-4--ncr-preview-using-inverse)
- [18. Java Logic Comments](#18-java-logic-comments)
- [19. Dry Run Table](#19-dry-run-table)
- [20. Common Mistakes](#20-common-mistakes)
- [21. CP / FAANG Problem Forms](#21-cp--faang-problem-forms)
- [22. Real World Mapping](#22-real-world-mapping)
- [23. Practice Problems](#23-practice-problems)
- [24. Final Mental Summary](#24-final-mental-summary)
- [25. Next Step](#25-next-step)

---

# 1. Why This Topic Matters

In CP and FAANG-style math problems, you often see:

```cpp
answer = numerator / denominator
```

But the problem says:

```text
Print answer modulo 1e9+7
```

You cannot write:

```cpp
answer = numerator / denominator % MOD;
```

That is wrong.

Instead you must write:

```cpp
answer = numerator * inverse(denominator) % MOD;
```

So modular inverse is required for:

```text
division under modulo
```

Very common in:

- combinatorics
- nCr
- probability
- expected value
- modular DP
- factorial division
- prefix product division
- number theory

---

# 2. Problem With Division Under MOD

Normal arithmetic:

```cpp
10 / 2 = 5
```

Modulo arithmetic:

```cpp
10 % 7 = 3
```

Now if we say:

```cpp
3 / 2 mod 7
```

What does that mean?

Direct division does not exist naturally in modular cyclic space.

Modulo has safe operations:

```cpp
addition
subtraction
multiplication
```

But division must be converted into multiplication by inverse.

---

# 3. Core Mental Model

Think like this:

```text
Division means undoing multiplication.
```

In normal arithmetic:

```cpp
10 / 2
```

means:

```cpp
10 * (1/2)
```

In modular arithmetic:

```cpp
a / b mod MOD
```

means:

```cpp
a * inverse(b) mod MOD
```

So inverse is the number that undoes multiplication by `b`.

---

# 4. Modular Inverse Definition

The modular inverse of `a` under `MOD` is a number `x` such that:

```cpp
(a * x) % MOD = 1
```

So:

```cpp
x = inverse(a)
```

Example:

```cpp
inverse(3) mod 7 = 5
```

Because:

```cpp
3 * 5 = 15
15 % 7 = 1
```

Therefore:

```cpp
3^-1 mod 7 = 5
```

---

# 5. Small Example By Brute Force

Find:

```cpp
inverse(2) mod 7
```

We need:

```cpp
2 * x % 7 = 1
```

Try all x:

```text
x = 1 => 2*1 = 2 % 7 = 2
x = 2 => 2*2 = 4 % 7 = 4
x = 3 => 2*3 = 6 % 7 = 6
x = 4 => 2*4 = 8 % 7 = 1
```

So:

```cpp
inverse(2) mod 7 = 4
```

Check:

```cpp
2 * 4 % 7 = 1
```

---

# 6. When Does Modular Inverse Exist?

Inverse exists only when:

```cpp
gcd(a, MOD) = 1
```

Meaning:

```text
a and MOD are coprime
```

Example where inverse exists:

```cpp
inverse(3) mod 7
gcd(3,7)=1
```

Example where inverse does NOT exist:

```cpp
inverse(2) mod 4
gcd(2,4)=2
```

Try:

```text
2*1 % 4 = 2
2*2 % 4 = 0
2*3 % 4 = 2
2*4 % 4 = 0
```

Never becomes `1`.

So inverse does not exist.

---

# 7. Fermat's Little Theorem Method

If `MOD` is prime and `a` is not divisible by `MOD`, then:

```cpp
a^(MOD-1) % MOD = 1
```

This is Fermat's Little Theorem.

From this:

```cpp
a^(MOD-1) = 1
```

Divide both sides by `a` conceptually:

```cpp
a^(MOD-2) = a^-1
```

So:

```cpp
inverse(a) = a^(MOD-2) % MOD
```

This is the most common CP method because many problems use:

```cpp
MOD = 1e9 + 7
```

which is prime.

---

# 8. Why Fermat Gives The Inverse

We want:

```cpp
a * x % MOD = 1
```

Fermat says:

```cpp
a^(MOD-1) % MOD = 1
```

Rewrite:

```cpp
a * a^(MOD-2) % MOD = 1
```

Compare with:

```cpp
a * x % MOD = 1
```

Therefore:

```cpp
x = a^(MOD-2)
```

So:

```cpp
inverse(a) = power(a, MOD-2)
```

This connects directly to previous file:

```text
002_Fast_Power_Binary_Exponentiation.md
```

---

# 9. Extended Euclid Method

Euclid gives:

```cpp
gcd(a, b)
```

Extended Euclid gives coefficients:

```cpp
a*x + b*y = gcd(a,b)
```

For modular inverse:

```cpp
a*x + MOD*y = 1
```

because:

```cpp
gcd(a, MOD)=1
```

Take modulo `MOD`:

```cpp
a*x % MOD = 1
```

So:

```cpp
x = inverse(a)
```

Important:

```text
Extended Euclid works even when MOD is not prime,
as long as gcd(a, MOD)=1.
```

---

# 10. Fermat vs Extended Euclid

| Method | Works When | Time |
|---|---|---|
| Fermat | MOD is prime | O(log MOD) |
| Extended Euclid | gcd(a, MOD)=1 | O(log MOD) |

Use Fermat when:

```cpp
MOD = 1e9+7
MOD = 998244353
```

Use Extended Euclid when:

```text
MOD is not prime
but inverse may still exist
```

---

# 11. Step-by-Step Working — inverse(3) mod 7

Need:

```cpp
3 * x % 7 = 1
```

Using Fermat:

```cpp
inverse(3) = 3^(7-2) % 7
inverse(3) = 3^5 % 7
```

Now compute using fast power:

```text
result = 1
base = 3
exp = 5
```

Binary of 5:

```text
5 = 101
```

Trace:

```text
exp=5 odd:
result = 1*3 % 7 = 3
base = 3*3 % 7 = 2
exp = 2

exp=2 even:
result = 3
base = 2*2 % 7 = 4
exp = 1

exp=1 odd:
result = 3*4 % 7 = 12%7 = 5
base = 4*4 % 7 = 2
exp = 0
```

Answer:

```cpp
5
```

Verify:

```cpp
3 * 5 % 7 = 1
```

---

# 12. Step-by-Step Working — Division Example

Compute:

```cpp
10 / 2 mod 7
```

Wrong way:

```cpp
(10 / 2) % 7 = 5
```

This works accidentally here, but not generally.

Correct modular way:

```cpp
10 * inverse(2) % 7
```

Find inverse:

```cpp
inverse(2) mod 7 = 4
```

Then:

```cpp
10 * 4 % 7
= 40 % 7
= 5
```

Same answer here.

But now try:

```cpp
3 / 2 mod 7
```

Correct way:

```cpp
3 * inverse(2) % 7
= 3 * 4 % 7
= 12 % 7
= 5
```

So:

```cpp
3 / 2 mod 7 = 5
```

Because:

```cpp
5 * 2 % 7 = 3
```

---

# 13. Pattern Detector

When you see this:

```text
division under modulo
```

Think:

```text
modular inverse
```

When you see:

```text
nCr % MOD
```

Think:

```text
factorial + inverse factorial
```

When you see:

```text
a / b % MOD
```

Think:

```text
a * modInverse(b) % MOD
```

When you see:

```text
MOD is prime
```

Think:

```text
Fermat inverse
```

When you see:

```text
MOD is not prime
```

Think:

```text
Extended Euclid inverse
```

---

# 14. C++ Code 1 — Fermat Method

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll MOD = 1e9 + 7;

// ---------------------------------------------------
// Step 1:
// Fast modular exponentiation
//
// Computes:
// base^exp % MOD
//
// Why needed?
// Modular inverse using Fermat requires:
// a^(MOD-2)
// ---------------------------------------------------
ll modPower(ll base, ll exp) {

    // answer starts as multiplicative identity
    ll result = 1;

    // keep base inside modulo range
    base %= MOD;

    while (exp > 0) {

        // If current binary bit is 1,
        // include current base in answer.
        if (exp & 1) {
            result = (result * base) % MOD;
        }

        // Move base to next power:
        // a, a^2, a^4, a^8...
        base = (base * base) % MOD;

        // Shift exponent right by 1 bit
        exp >>= 1;
    }

    return result;
}

// ---------------------------------------------------
// Step 2:
// Modular inverse using Fermat's Little Theorem
//
// inverse(a) = a^(MOD-2) % MOD
//
// Condition:
// MOD must be prime.
// a must not be divisible by MOD.
// ---------------------------------------------------
ll modInverseFermat(ll a) {

    return modPower(a, MOD - 2);
}

int main() {

    ll a = 3;

    ll inverse = modInverseFermat(a);

    cout << "inverse(" << a << ") = "
         << inverse
         << "\n";

    cout << "verification = "
         << (a * inverse) % MOD
         << "\n";

    return 0;
}
```

---

# 15. C++ Code 2 — Extended Euclid Method

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

// ---------------------------------------------------
// Extended Euclid returns gcd(a,b)
// and also finds x,y such that:
//
// a*x + b*y = gcd(a,b)
// ---------------------------------------------------
ll extendedGCD(ll a, ll b, ll &x, ll &y) {

    // Base case:
    // gcd(a,0)=a
    if (b == 0) {
        x = 1;
        y = 0;
        return a;
    }

    ll x1, y1;

    // Recursive call
    ll gcdValue = extendedGCD(b, a % b, x1, y1);

    // Back-substitution
    x = y1;
    y = x1 - (a / b) * y1;

    return gcdValue;
}

// ---------------------------------------------------
// Modular inverse using Extended Euclid
//
// Works if:
// gcd(a, MOD)=1
//
// Works even when MOD is not prime.
// ---------------------------------------------------
ll modInverseExtendedEuclid(ll a, ll MOD) {

    ll x, y;

    ll g = extendedGCD(a, MOD, x, y);

    // Inverse does not exist
    if (g != 1) {
        return -1;
    }

    // x may be negative,
    // normalize it to [0, MOD-1]
    x = (x % MOD + MOD) % MOD;

    return x;
}

int main() {

    ll a = 3;
    ll MOD = 7;

    ll inverse = modInverseExtendedEuclid(a, MOD);

    cout << "inverse = "
         << inverse
         << "\n";

    cout << "verification = "
         << (a * inverse) % MOD
         << "\n";

    return 0;
}
```

---

# 16. C++ Code 3 — Modular Division Helper

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll MOD = 1e9 + 7;

ll modPower(ll base, ll exp) {

    ll result = 1;
    base %= MOD;

    while (exp > 0) {

        if (exp & 1) {
            result = (result * base) % MOD;
        }

        base = (base * base) % MOD;
        exp >>= 1;
    }

    return result;
}

ll modInverse(ll a) {

    return modPower(a, MOD - 2);
}

// ---------------------------------------------------
// Modular division:
// a / b under MOD
//
// Instead of:
// a / b % MOD
//
// Use:
// a * inverse(b) % MOD
// ---------------------------------------------------
ll modDivide(ll a, ll b) {

    a %= MOD;
    b %= MOD;

    return (a * modInverse(b)) % MOD;
}

int main() {

    cout << modDivide(10, 2) << "\n";

    return 0;
}
```

---

# 17. C++ Code 4 — nCr Preview Using Inverse

This is only a preview. Full `nCr` comes later.

Formula:

```cpp
nCr = n! / (r! * (n-r)!)
```

Under modulo:

```cpp
nCr = fact[n] * inverse(fact[r]) * inverse(fact[n-r]) % MOD
```

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll MOD = 1e9 + 7;

ll modPower(ll base, ll exp) {

    ll result = 1;
    base %= MOD;

    while (exp > 0) {

        if (exp & 1) {
            result = (result * base) % MOD;
        }

        base = (base * base) % MOD;
        exp >>= 1;
    }

    return result;
}

ll modInverse(ll a) {
    return modPower(a, MOD - 2);
}

ll nCrSmall(ll n, ll r) {

    if (r < 0 || r > n)
        return 0;

    ll numerator = 1;
    ll denominator = 1;

    // Compute n*(n-1)*...*(n-r+1)
    for (ll i = 0; i < r; i++) {
        numerator = (numerator * (n - i)) % MOD;
    }

    // Compute r!
    for (ll i = 1; i <= r; i++) {
        denominator = (denominator * i) % MOD;
    }

    // Divide numerator by denominator under MOD
    return (numerator * modInverse(denominator)) % MOD;
}

int main() {

    cout << nCrSmall(5, 2) << "\n"; // 10

    return 0;
}
```

---

# 18. Java Logic Comments

```text
function modPower(base, exp):
    result = 1
    base = base % MOD

    while exp > 0:
        if exp is odd:
            result = result * base % MOD

        base = base * base % MOD
        exp = exp / 2

    return result


function modInverse(a):
    return modPower(a, MOD - 2)


function modDivide(a, b):
    return a * modInverse(b) % MOD
```

---

# 19. Dry Run Table

Find:

```cpp
inverse(3) mod 7
```

So compute:

```cpp
3^5 % 7
```

| exp | bit odd? | result | base | action |
|---:|---|---:|---:|---|
| 5 | yes | 3 | 3 | result *= base |
| 2 | no | 3 | 2 | square base |
| 1 | yes | 5 | 4 | result *= base |
| 0 | stop | 5 | 2 | answer |

Answer:

```cpp
5
```

Check:

```cpp
3 * 5 % 7 = 1
```

---

# 20. Common Mistakes

## Mistake 1 — Direct Division

Wrong:

```cpp
ans = a / b % MOD;
```

Correct:

```cpp
ans = a * modInverse(b) % MOD;
```

---

## Mistake 2 — Using Fermat With Non-Prime MOD

Fermat requires:

```cpp
MOD is prime
```

If MOD is not prime, use Extended Euclid.

---

## Mistake 3 — Inverse Does Not Always Exist

Inverse exists only when:

```cpp
gcd(a, MOD) = 1
```

---

## Mistake 4 — Negative Extended Euclid Result

Extended Euclid may return negative x.

Normalize:

```cpp
x = (x % MOD + MOD) % MOD;
```

---

## Mistake 5 — Forgetting To Mod After Multiplication

Wrong:

```cpp
result = result * base;
```

Correct:

```cpp
result = result * base % MOD;
```

---

# 21. CP / FAANG Problem Forms

## Form 1 — nCr Under MOD

```text
Count ways to choose r items from n
answer modulo 1e9+7
```

Need:

```text
factorials + inverse factorials
```

---

## Form 2 — Probability Under MOD

Example:

```text
probability = favorable / total
```

Under MOD:

```cpp
favorable * inverse(total) % MOD
```

---

## Form 3 — Expected Value

Expected value often contains division.

Need modular inverse.

---

## Form 4 — Modular DP

Some transitions divide by count.

Need inverse.

---

## Form 5 — Prefix Product Division

Example:

```cpp
product(l,r) = prefixProduct[r] / prefixProduct[l-1]
```

Under MOD:

```cpp
prefixProduct[r] * inverse(prefixProduct[l-1]) % MOD
```

---

# 22. Real World Mapping

## Cryptography

RSA uses modular inverse to compute private key values.

---

## Blockchain / Finite Fields

Many blockchain systems use arithmetic over finite fields.

Division in finite fields is modular inverse.

---

## Secure Authentication

Public/private key math uses modular number theory.

---

## Error Correcting Codes

Finite field division uses modular inverse.

---

## Hashing / Distributed Systems

Modulo itself is common in partitioning, but inverse appears more in cryptography and mathematical systems.

---

# 23. Practice Problems

Start easy:

```text
1. Find inverse of a mod p
2. Compute a / b mod p
3. Compute nCr for small n
4. Compute probability under MOD
```

Then medium:

```text
5. nCr with precomputed factorials
6. Count paths in grid modulo MOD
7. Expected value with modular inverse
8. Prefix product range query
```

Then hard:

```text
9. nCr for large constraints
10. Lucas theorem
11. Modular Gaussian elimination
12. CRT + inverse
```

---

# 24. Final Mental Summary

Remember this:

```text
Division does not exist directly in modulo.
```

So:

```cpp
a / b mod MOD
```

becomes:

```cpp
a * inverse(b) mod MOD
```

If MOD is prime:

```cpp
inverse(b)=b^(MOD-2)
```

If MOD is not prime:

```text
use Extended Euclid if gcd(b, MOD)=1
```

---

# 25. Next Step

```text
004_GCD_LCM_Euclid.md
```

Next intuition:

```text
GCD teaches divisibility structure.
LCM teaches repeating cycle synchronization.
```
