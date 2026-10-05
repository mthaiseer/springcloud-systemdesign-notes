# Number Theory — Intermediate Level 2

> **Goal:** understand the prerequisites and derive the ideas instead of memorizing formulas.

## TOC
- [1. Prerequisites](#1-prerequisites)
- [2. Modular Operations](#2-modular-operations)
- [3. Modular Division](#3-modular-division)
- [4. Binary Exponentiation](#4-binary-exponentiation)
- [5. GCD](#5-gcd)
- [6. LCM](#6-lcm)
- [7. Pigeonhole Principle](#7-pigeonhole-principle)
- [8. Application: Subarray Sum Divisible by N](#8-application-subarray-sum-divisible-by-n)
- [9. Recognition Model](#9-recognition-model)

# 1. Prerequisites

### Modulo
`A mod M` is the remainder after dividing `A` by `M`.

$$17=3\cdot5+2 \quad\Rightarrow\quad 17\bmod5=2$$

Congruence:

$$A\equiv B\pmod M$$

means `A` and `B` have the same remainder modulo `M`.

### Divisibility
$$a\mid b \iff b\bmod a=0$$

### Exponents
$$a^{x+y}=a^xa^y$$

$$a^{2k}=(a^k)^2$$

$$a^{2k+1}=(a^k)^2a$$

The last two identities lead directly to binary exponentiation.

### Prime and Coprime
A prime has exactly two positive divisors. Two integers are coprime when:

$$\gcd(a,b)=1$$

This condition matters when working with modular inverses.

---

# 2. Modular Operations

### Addition
$$
(A+B)\bmod M
=
\big((A\bmod M)+(B\bmod M)\big)\bmod M
$$

Example:

$$
(7+6)\bmod5=(2+1)\bmod5=3
$$

### Subtraction
$$
(A-B)\bmod M
=
\big((A\bmod M)-(B\bmod M)+M\big)\bmod M
$$

Example:

$$
(11-8)\bmod5=(1-3+5)\bmod5=3
$$

`+M` prevents a negative remainder.

### Multiplication
$$
(AB)\bmod M
=
\big((A\bmod M)(B\bmod M)\big)\bmod M
$$

Example:

$$
(7\cdot8)\bmod5=(2\cdot3)\bmod5=1
$$

```cpp
long long ans = ((a % MOD) * (b % MOD)) % MOD;
```

---

# 3. Modular Division

Ordinary division cannot simply be performed on remainders.

Instead:

$$
\frac AB \equiv A\cdot B^{-1}\pmod M
$$

The modular inverse satisfies:

$$
BB^{-1}\equiv1\pmod M
$$

For prime `M`, when `B` is not divisible by `M`, Fermat gives:

$$
B^{M-1}\equiv1\pmod M
$$

Hence:

$$
\boxed{B^{-1}\equiv B^{M-2}\pmod M}
$$

So:

```text
A / B mod M
      ↓
find B^(M-2) mod M
      ↓
multiply by A
```

Binary exponentiation computes the inverse efficiently.

---

# 4. Binary Exponentiation

Brute force for `a^b` takes:

$$O(b)$$

But:

$$
a^b=
\begin{cases}
1,&b=0\\
(a^{b/2})^2,&b\text{ even}\\
(a^{\lfloor b/2\rfloor})^2a,&b\text{ odd}
\end{cases}
$$

Each step halves `b`, giving:

$$O(\log b)$$

### Dry Run — `3^6`

```text
result = 1, base = 3, b = 6

b=6 even → base=9,  b=3
b=3 odd  → result=9, base=81, b=1
b=1 odd  → result=729

answer = 729
```

### C++

```cpp
long long binpow(long long a, long long b, long long mod) {
    long long res = 1;
    a %= mod;

    while (b > 0) {
        if (b & 1)
            res = (res * a) % mod;

        a = (a * a) % mod;
        b >>= 1;
    }

    return res;
}
```

### Don't memorize
```text
huge exponent
     ↓
halve exponent
     ↓
even → square
odd  → square × base
     ↓
O(log b)
```

---

# 5. GCD

`gcd(a,b)` is the greatest number dividing both.

Example:

$$
\gcd(8,12)=4
$$

Key property:

$$
\gcd(a,b)=\gcd(a+kb,b)
$$

Choosing the appropriate multiple gives Euclid's algorithm:

$$
\boxed{\gcd(a,b)=\gcd(b,a\bmod b)}
$$

### Dry Run

```text
gcd(48,18)

48 % 18 = 12
18 % 12 = 6
12 % 6  = 0

gcd = 6
```

Also:

$$
\gcd(a,0)=a
$$

$$
\gcd(a,b,c)=\gcd(a,\gcd(b,c))
$$

```cpp
long long g = std::gcd(a, b);
```

Complexity:

$$O(\log(\max(a,b)))$$

### Why Euclid works
If `d` divides both `a` and `b`, it also divides `a-kb`. Therefore subtracting multiples does not change the set of common divisors.

---

# 6. LCM

`lcm(a,b)` is the smallest positive integer divisible by both.

For two positive integers:

$$
\operatorname{lcm}(a,b)\cdot\gcd(a,b)=ab
$$

Therefore:

$$
\boxed{\operatorname{lcm}(a,b)=\frac{ab}{\gcd(a,b)}}
$$

Safer C++:

```cpp
long long lcm(long long a, long long b) {
    return (a / std::gcd(a, b)) * b;
}
```

For several numbers, combine pairwise:

$$
\operatorname{lcm}(a,b,c)
=
\operatorname{lcm}(a,\operatorname{lcm}(b,c))
$$

Do **not** assume:

$$
\operatorname{lcm}(a,b,c)=\frac{abc}{\gcd(a,b,c)}
$$

---

# 7. Pigeonhole Principle

If more than `N` objects are placed into `N` boxes, some box contains at least two objects.

```text
objects > buckets
       ↓
some bucket repeats
```

In CP, the important question is:

```text
What are the objects?
What are the buckets?
```

A common bucket choice is **remainder modulo N**.

---

# 8. Application: Subarray Sum Divisible by N

Given `N` integers, show that there is a non-empty subarray whose sum is divisible by `N`.

Define prefix sums:

$$
P_i=a_1+a_2+\cdots+a_i
$$

A subarray sum is:

$$
P_j-P_i
$$

We want:

$$
(P_j-P_i)\bmod N=0
$$

which happens when:

$$
P_j\bmod N=P_i\bmod N
$$

Now examine the `N` prefix remainders.

Possible remainders:

```text
0, 1, 2, ..., N-1
```

Two cases:

```text
1. Some prefix remainder = 0
   → that prefix itself is divisible by N.

2. No remainder = 0
   → N prefix sums occupy only N-1 possible
     remainders {1,...,N-1}
   → by Pigeonhole, some remainder repeats.
```

If:

$$
P_i\bmod N=P_j\bmod N
$$

then:

$$
(P_j-P_i)\bmod N=0
$$

So the subarray between those prefixes has sum divisible by `N`.

### Dry Run

```text
N = 3
a = [5, 5, 1]

prefix sums     = [5, 10, 11]
prefix mod 3    = [2,  1,  2]
```

Remainder `2` repeats:

$$
P_3-P_1=11-5=6
$$

Thus:

```text
subarray [5,1]
sum = 6
6 % 3 = 0
```

### C++

```cpp
bool existsDivisibleSubarray(const vector<long long>& a) {
    int n = a.size();
    vector<bool> seen(n, false);

    long long prefix = 0;

    for (long long x : a) {
        prefix = (prefix + x) % n;
        if (prefix < 0) prefix += n;

        if (prefix == 0)
            return true;

        if (seen[prefix])
            return true;

        seen[prefix] = true;
    }

    return false;
}
```

Complexity:

```text
Time  O(N)
Space O(N)
```

### Don't memorize
```text
want subarray sum divisible by N
              ↓
subarray = difference of prefixes
              ↓
difference % N = 0
              ↓
equal prefix remainders
              ↓
limited remainder buckets
              ↓
Pigeonhole Principle
```

---

# 9. Recognition Model

| Signal | Think |
|---|---|
| huge arithmetic under `% M` | reduce operands |
| modular subtraction | `+M`, then `%M` |
| modular division | modular inverse |
| prime modulus inverse | Fermat |
| huge exponent | binary exponentiation |
| greatest common divisor | GCD / Euclid |
| least common multiple | LCM |
| more objects than states | Pigeonhole |
| subarray divisible by `N` | prefix modulo |
| equal prefix remainders | difference divisible by `N` |

```text
Number Theory
├── Modulo
│   ├── +, -, ×
│   └── division → inverse
├── Exponentiation → halve exponent
├── Divisibility
│   ├── GCD
│   └── LCM
└── Pigeonhole
    └── finite remainder states → repetition
```

> **Core habit:** first derive the mathematical relation; only then choose the algorithm.
