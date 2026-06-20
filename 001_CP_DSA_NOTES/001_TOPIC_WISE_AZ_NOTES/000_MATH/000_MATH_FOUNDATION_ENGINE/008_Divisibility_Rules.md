# 008_Divisibility_Rules.md

# Divisibility Rules For Competitive Programming

---

# 1. Introduction

Divisibility is one of the most common hidden observations in CP.

Many problems become simple once you recognize:

```text
Which numbers divide cleanly?
```

Divisibility appears in:
- number theory
- constructive problems
- modulo problems
- gcd/lcm
- parity
- cyclic behavior
- digit problems

---

# 2. What Does Divisible Mean?

A number `a` is divisible by `b` if:

```text
a % b == 0
```

Meaning:

```text
No remainder after division.
```

Example:

```text
12 % 3 = 0
```

So:

```text
12 divisible by 3
```

---

# 3. Why Divisibility Matters In CP

Recognition signals:
- factors
- multiples
- modulo
- equal grouping
- cyclic patterns
- divisibility constraints
- repeated subtraction

Very common in:
- adhoc
- constructive
- math
- greedy

---

# 4. Divisibility Rule For 2

A number is divisible by 2 if:

```text
last digit even
```

Examples:

```text
14 → divisible
28 → divisible
31 → not divisible
```

Used heavily in:
- parity
- bitwise
- optimizations

---

# 5. Divisibility Rule For 3

A number is divisible by 3 if:

```text
sum of digits divisible by 3
```

Example:

```text
123

1 + 2 + 3 = 6
```

Since 6 divisible by 3:

```text
123 divisible by 3
```

---

# 6. Divisibility Rule For 4

A number is divisible by 4 if:

```text
last two digits divisible by 4
```

Example:

```text
312

last two digits = 12
12 divisible by 4
```

Therefore:

```text
312 divisible by 4
```

---

# 7. Divisibility Rule For 5

Divisible by 5 if last digit:

```text
0 or 5
```

Examples:

```text
25
70
105
```

---

# 8. Divisibility Rule For 6

Divisible by 6 if:

```text
divisible by 2
AND
divisible by 3
```

Example:

```text
24

even
digit sum = 6
```

Therefore:

```text
24 divisible by 6
```

---

# 9. Divisibility Rule For 8

A number divisible by 8 if:

```text
last three digits divisible by 8
```

Example:

```text
1016

last three digits = 016 = 16
16 divisible by 8
```

Thus:

```text
1016 divisible by 8
```

---

# 10. Divisibility Rule For 9

Divisible by 9 if:

```text
digit sum divisible by 9
```

Example:

```text
729

7 + 2 + 9 = 18
```

18 divisible by 9.

Thus:

```text
729 divisible by 9
```

---

# 11. Divisibility Rule For 10

Divisible by 10 if last digit:

```text
0
```

Examples:

```text
10
50
120
```

---

# 12. Divisibility Rule For 11

Difference between:
- sum of odd position digits
- sum of even position digits

must be divisible by 11.

---

# Example

```text
121

(1 + 1) - 2 = 0
```

0 divisible by 11.

Thus:

```text
121 divisible by 11
```

---

# 13. Why Digit Rules Work

Because decimal representation:

```text
abcd
=
a*1000 + b*100 + c*10 + d
```

Modulo properties create patterns.

---

# 14. Modulo And Divisibility

Important relation:

```text
a divisible by b
⇔
a % b == 0
```

This is the foundation of:
- gcd
- modular arithmetic
- cyclic patterns

---

# 15. Divisibility In Loops

Common optimization:

Instead of checking all numbers:

```text
1 to n
```

iterate only:
```text
multiples of k
```

Example:

```cpp
for (int x = k; x <= n; x += k)
```

Huge optimization.

---

# 16. Divisibility And Prime Numbers

Prime number:

```text
exactly 2 divisors
```

Recognition:
- divisibility testing
- factorization
- sieve

---

# 17. Divisibility And GCD

If:

```text
gcd(a,b) = d
```

then:

```text
d divides both a and b
```

Very important in:
- constructive
- fractions
- number theory

---

# 18. Divisibility In Arrays

Common patterns:
- all divisible?
- pair divisible?
- prefix divisibility?
- modulo grouping?

---

# 19. CP / FAANG Problem Forms

---

# Form 1 — Divisible By 3

## Problem

Check if number divisible by 3.

---

## Observation

Digit sum divisible by 3.

---

## Step-by-Step Working

Example:

```text
123

1 + 2 + 3 = 6
```

Since 6 divisible by 3:

```text
123 divisible by 3
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool divisibleBy3(int n) {

    int sum = 0;

    while (n > 0) {

        sum += n % 10;

        n /= 10;
    }

    return sum % 3 == 0;
}
```

---

# Form 2 — Divisible By 2

## Problem

Check if number divisible by 2.

---

## Observation

Last digit even.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool divisibleBy2(int n) {

    return n % 2 == 0;
}
```

---

# Form 3 — Divisible By 5

## Problem

Check if divisible by 5.

---

## Observation

Last digit:
```text
0 or 5
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool divisibleBy5(int n) {

    int d = n % 10;

    return d == 0 || d == 5;
}
```

---

# Form 4 — Divisible By 6

## Problem

Check if divisible by 6.

---

## Observation

Must satisfy:
- divisible by 2
- divisible by 3

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool divisibleBy6(int n) {

    bool even = (n % 2 == 0);

    int sum = 0;

    int x = n;

    while (x > 0) {

        sum += x % 10;

        x /= 10;
    }

    return even && (sum % 3 == 0);
}
```

---

# Form 5 — Count Multiples

## Problem

Count numbers divisible by k from 1 to n.

---

## Observation

Multiples:

```text
k, 2k, 3k ...
```

Count:

```text
floor(n / k)
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countMultiples(long long n, long long k) {

    return n / k;
}
```

---

# Form 6 — Iterate Multiples Efficiently

## Problem

Process only numbers divisible by k.

---

## Observation

Jump by k.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

void processMultiples(int n, int k) {

    for (int x = k; x <= n; x += k) {

        cout << x << " ";
    }
}
```

---

# Form 7 — Prime Divisibility

## Problem

Check if number prime.

---

## Observation

Need test divisors only up to:

```text
sqrt(n)
```

because divisors come in pairs.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool isPrime(int n) {

    if (n < 2) return false;

    for (int d = 2;
         d * d <= n;
         d++) {

        if (n % d == 0) {
            return false;
        }
    }

    return true;
}
```

---

# 20. Real World Applications

| Real System | Usage |
|---|---|
| Cryptography | modular arithmetic |
| Databases | hash partitioning |
| Distributed systems | sharding |
| Scheduling | periodic events |
| Networking | packet grouping |
| Compression | factorization |
| Graphics | grid alignment |

---

# 21. Real Engineering Insight

Divisibility usually means:

```text
Grouping
Partitioning
Cyclic behavior
Alignment
```

This mental mapping is important.

---

# 22. Decision Tree

```text
Factors involved?
→ divisibility

Repeated cycles?
→ modulo/divisibility

Digit patterns?
→ divisibility rules

Grouping equally?
→ divisibility

Prime/factors?
→ divisibility testing

Need optimization?
→ iterate multiples only
```

---

# 23. Common Traps

```text
1. Overflow in multiplication
2. Negative modulo confusion
3. Forgetting divisibility conditions
4. Brute force divisor iteration
5. Missing sqrt optimization
6. Wrong digit extraction
7. Leading zero confusion
8. Prime edge cases (0,1)
```

---

# 24. Final Checklist

Before solving:

```text
1. Is divisibility involved?
2. Can modulo simplify?
3. Are digit rules useful?
4. Can multiples iteration optimize?
5. Is gcd/lcm connected?
6. Is prime testing needed?
7. Is factorization useful?
8. Is cyclic behavior present?
```

---

# 25. Final Mental Shortcut

```text
Divisibility
=
Factors
+
Modulo
+
Cycles
+
Grouping
```
