# Number Theory — Beginner Level 2
## Problem Solving: Almost Prime, Square Difference & Digit Space

> **Goal:** learn the observation behind each problem, not memorize the final code.
>
> **Flow:** prerequisites → what the problem asks → mathematical model → derivation → dry run → C++ → recognition.

---

# Table of Contents

- [0. Preliminary Toolkit](#0-preliminary-toolkit)
  - [0.1 Prime Numbers](#01-prime-numbers)
  - [0.2 Prime Factorisation](#02-prime-factorisation)
  - [0.3 Distinct Prime Factors](#03-distinct-prime-factors)
  - [0.4 Trial Division and √N](#04-trial-division-and-n)
  - [0.5 SPF — Smallest Prime Factor](#05-spf--smallest-prime-factor)
  - [0.6 Difference of Squares](#06-difference-of-squares)
  - [0.7 When Can a Product Be Prime?](#07-when-can-a-product-be-prime)
  - [0.8 GCD and Common Prime Factors](#08-gcd-and-common-prime-factors)
  - [0.9 Permutations and `next_permutation`](#09-permutations-and-next_permutation)
- [1. Almost Prime — CF 26A](#1-almost-prime--cf-26a)
- [2. Square Difference — CF 1033B](#2-square-difference--cf-1033b)
- [3. Digit Space — CodeChef DSP](#3-digit-space--codechef-dsp)
- [4. Final Recognition Table](#4-final-recognition-table)
- [5. Don't-Memorize Model](#5-dont-memorize-model)

---

# 0. Preliminary Toolkit

These are the only ideas needed for the three problems.

---

## 0.1 Prime Numbers

A prime number has exactly two positive divisors:

```text
1 and itself
```

Examples:

```text
2, 3, 5, 7, 11, 13, 17, ...
```

Composite examples:

```text
6  = 2 × 3
10 = 2 × 5
12 = 2² × 3
```

Important:

```text
1 is NOT prime.
```

---

## 0.2 Prime Factorisation

Every integer greater than `1` can be written as a product of primes.

Example:

```text
24
= 2 × 12
= 2 × 2 × 6
= 2 × 2 × 2 × 3
= 2³ × 3
```

Another example:

```text
60 = 2² × 3 × 5
```

Prime factorisation tells us **which primes build the number**.

---

## 0.3 Distinct Prime Factors

This is different from counting prime factors with multiplicity.

Example:

```text
24 = 2³ × 3
```

Prime factors with multiplicity:

```text
2, 2, 2, 3
count = 4
```

Distinct prime factors:

```text
2, 3
count = 2
```

For **Almost Prime**, we need:

```text
exactly 2 DISTINCT prime factors
```

Examples:

```text
6  = 2 × 3      → distinct primes {2,3}   → YES
10 = 2 × 5      → distinct primes {2,5}   → YES
24 = 2³ × 3     → distinct primes {2,3}   → YES
4  = 2²         → distinct primes {2}     → NO
30 = 2 × 3 × 5  → 3 distinct primes       → NO
```

---

## 0.4 Trial Division and √N

Suppose:

```text
a × b = N
```

If both `a` and `b` were greater than `√N`, then:

```text
a × b > √N × √N
      > N
```

which is impossible.

Therefore:

```text
Every composite N has at least one factor <= √N.
```

So primality testing can stop at `√N`.

### C++

```cpp
bool isPrime(long long n) {
    if (n < 2)
        return false;

    for (long long d = 2; d <= n / d; ++d) {
        if (n % d == 0)
            return false;
    }

    return true;
}
```

Why use:

```cpp
d <= n / d
```

instead of:

```cpp
d * d <= n
```

?

Because multiplication can overflow for very large integers.

Complexity:

```text
O(√N)
```

---

## 0.5 SPF — Smallest Prime Factor

`spf[x]` means:

```text
smallest prime that divides x
```

Examples:

```text
spf[12] = 2
spf[15] = 3
spf[25] = 5
spf[17] = 17
```

For a prime:

```text
spf[p] = p
```

SPF is useful when we need to factorise **many bounded values**.

### Build SPF

```cpp
vector<int> buildSPF(int n) {
    vector<int> spf(n + 1);

    for (int i = 0; i <= n; ++i)
        spf[i] = i;

    for (long long p = 2; p * p <= n; ++p) {
        if (spf[p] != p)
            continue;

        for (long long j = p * p; j <= n; j += p) {
            if (spf[j] == j)
                spf[j] = (int)p;
        }
    }

    return spf;
}
```

### Extract distinct prime factors

Example:

```text
x = 24
spf[24] = 2 → remove all 2s
24 → 12 → 6 → 3

spf[3] = 3 → remove 3
3 → 1

distinct primes = {2,3}
```

C++:

```cpp
vector<int> distinctPrimeFactors(
    int x,
    const vector<int>& spf
) {
    vector<int> primes;

    while (x > 1) {
        int p = spf[x];
        primes.push_back(p);

        while (x % p == 0)
            x /= p;
    }

    return primes;
}
```

---

## 0.6 Difference of Squares

One identity is central to the second problem:

```text
a² - b² = (a-b)(a+b)
```

### Derivation

Expand the right side:

```text
(a-b)(a+b)
```

Multiply:

```text
= a(a+b) - b(a+b)

= a² + ab - ab - b²

= a² - b²
```

Therefore:

```text
a² - b² = (a-b)(a+b)
```

Example:

```text
a = 5
b = 4

a² - b²
= 25 - 16
= 9
```

Using factorisation:

```text
(a-b)(a+b)
= (5-4)(5+4)
= 1 × 9
= 9
```

This factorisation exposes structure that is hidden in `a²-b²`.

---

## 0.7 When Can a Product Be Prime?

Suppose:

```text
X × Y = P
```

and `P` is prime.

A prime has only:

```text
1 and P
```

as positive divisors.

Therefore the only positive factorisation is:

```text
P = 1 × P
```

So if:

```text
X > 0
Y > 0
X × Y is prime
```

then one factor **must be `1`** and the other must be prime.

Example:

```text
3 × 5 = 15
```

Both factors are greater than `1`, so the product cannot be prime.

But:

```text
1 × 13 = 13
```

can be prime.

This observation is the key to **Square Difference**.

---

## 0.8 GCD and Common Prime Factors

`gcd(a,b)` is the largest positive integer dividing both `a` and `b`.

Example:

```text
a = 30 = 2 × 3 × 5
b = 45 = 3² × 5
```

Common prime factors:

```text
3, 5
```

Therefore:

```text
gcd(30,45)
= 3 × 5
= 15
```

Prime factors of the GCD are exactly the prime factors common to both numbers.

So:

```text
p divides a AND p divides b
```

is equivalent to:

```text
p divides gcd(a,b)
```

This is useful when a problem asks for a prime dividing numbers from two sets.

---

## 0.9 Permutations and `next_permutation`

A permutation is an ordering of the same elements.

For:

```text
123
```

the permutations are:

```text
123
132
213
231
312
321
```

C++ provides:

```cpp
next_permutation(s.begin(), s.end())
```

It transforms the current arrangement into the next lexicographically larger permutation.

To enumerate all permutations:

```cpp
sort(s.begin(), s.end());

do {
    // use s
} while (next_permutation(s.begin(), s.end()));
```

Why sort first?

Because `next_permutation` moves forward from the current ordering. Starting from the smallest ordering guarantees that all permutations are visited.

### Leading zero

For digits such as:

```text
012
```

a permutation:

```text
012
```

should not be treated as a 3-digit number if the problem forbids leading zeroes.

So commonly:

```cpp
if (s[0] == '0')
    continue;
```

---

# 1. Almost Prime — CF 26A

**Problem:** [Codeforces 26A — Almost Prime](https://codeforces.com/contest/26/problem/A)

---

## 1.1 What Does the Problem Ask?

A number is called **almost prime** when it has exactly two distinct prime divisors.

Given `n`, count how many numbers:

```text
2, 3, ..., n
```

are almost prime.

Example classification:

```text
6  = 2 × 3       → {2,3}   → almost prime
10 = 2 × 5       → {2,5}   → almost prime
12 = 2² × 3      → {2,3}   → almost prime
18 = 2 × 3²      → {2,3}   → almost prime

4  = 2²          → {2}     → not
30 = 2 × 3 × 5   → {2,3,5} → not
```

The exponent does not matter.

We only ask:

```text
How many DIFFERENT primes divide x?
```

---

## 1.2 Direct Model

For every:

```text
x = 2 ... n
```

compute:

```text
countDistinctPrimeFactors(x)
```

If:

```text
count == 2
```

increment the answer.

The lecture notes point to this exact modelling step: iterate through `1..n` and check how many prime divisors each number has. fileciteturn27file0L6-L10

---

## 1.3 Approach — SPF

Since all numbers are in a bounded range, SPF gives an easy reusable factorisation method.

Example:

```text
x = 24
24 = 2³ × 3

distinct factors = {2,3}

count = 2
```

So `24` qualifies.

---

## 1.4 Dry Run — n = 10

Check:

```text
2  = 2       → {2}     → NO
3  = 3       → {3}     → NO
4  = 2²      → {2}     → NO
5  = 5       → {5}     → NO
6  = 2×3     → {2,3}   → YES
7  = 7       → {7}     → NO
8  = 2³      → {2}     → NO
9  = 3²      → {3}     → NO
10 = 2×5     → {2,5}   → YES
```

Answer:

```text
2
```

because:

```text
6, 10
```

have exactly two distinct prime factors.

---

## 1.5 C++

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<int> spf(n + 1);

    for (int i = 0; i <= n; ++i)
        spf[i] = i;

    for (long long p = 2; p * p <= n; ++p) {
        if (spf[p] != p)
            continue;

        for (long long j = p * p; j <= n; j += p) {
            if (spf[j] == j)
                spf[j] = (int)p;
        }
    }

    int answer = 0;

    for (int x = 2; x <= n; ++x) {
        int cur = x;
        int distinct = 0;

        while (cur > 1) {
            int p = spf[cur];
            ++distinct;

            while (cur % p == 0)
                cur /= p;
        }

        if (distinct == 2)
            ++answer;
    }

    cout << answer << '\n';
}
```

---

## 1.6 Complexity

SPF preprocessing:

```text
O(n log log n)
```

Factorising all bounded values is fast enough for this problem.

---

## 1.7 Don't-Memorize Model

```text
"exactly two prime divisors"
          |
          v
Does multiplicity matter?
          |
          v
NO → DISTINCT primes
          |
          v
factorise x
          |
          v
count different primes
          |
          v
count == 2
```

Recognition:

```text
exactly k distinct prime divisors
→ prime factorisation / SPF
→ count unique primes
```

---

# 2. Square Difference — CF 1033B

**Problem:** [Codeforces 1033B — Square Difference](https://codeforces.com/problemset/problem/1033/B)

The lecture frames the target as checking whether the shaded difference of two square areas, `a²-b²`, is prime. fileciteturn27file0L13-L20

---

## 2.1 What Does the Problem Ask?

We need to determine whether:

```text
a² - b²
```

is prime.

A direct idea would be:

```text
N = a² - b²
isPrime(N)
```

But `a` and `b` can be very large, so first look for algebraic structure.

---

## 2.2 Remove the Story → Variables

Target:

```text
a² - b²
```

Question:

```text
When can this value be prime?
```

Immediately recognize:

```text
difference of squares
```

---

## 2.3 Algebraic Transformation

Use:

```text
a² - b² = (a-b)(a+b)
```

Now the target is expressed as a product.

Suppose:

```text
(a-b)(a+b)
```

is prime.

From the preliminary:

```text
A positive product is prime only when one factor is 1.
```

Since:

```text
a > b > 0
```

we have:

```text
a+b > 1
```

Therefore the only factor that can equal `1` is:

```text
a-b
```

So a necessary condition is:

```text
a-b = 1
```

Then:

```text
a²-b²
= (a-b)(a+b)
= 1 × (a+b)
= a+b
```

Therefore the entire problem becomes:

```text
a-b == 1
AND
a+b is prime
```

This is the key observation shown in the lecture's factorisation pages. fileciteturn27file0L17-L20

---

## 2.4 Dry Run — YES Case

Take:

```text
a = 4
b = 3
```

Directly:

```text
a²-b²
= 4²-3²
= 16-9
= 7
```

`7` is prime.

Now derive it:

```text
a-b = 4-3 = 1
a+b = 4+3 = 7
```

Therefore:

```text
(a-b)(a+b)
= 1 × 7
= 7
```

Conditions:

```text
a-b == 1      → YES
a+b is prime  → YES
```

Answer:

```text
YES
```

---

## 2.5 Dry Run — NO Because Difference > 1

Take:

```text
a = 5
b = 3
```

Then:

```text
a-b = 2
a+b = 8
```

So:

```text
a²-b²
= (a-b)(a+b)
= 2 × 8
= 16
```

Both factors are greater than `1`.

Therefore the product is composite.

Answer:

```text
NO
```

No primality test is even needed.

---

## 2.6 Dry Run — Difference Is 1 but Sum Is Composite

Take:

```text
a = 5
b = 4
```

Then:

```text
a-b = 1
```

Good.

But:

```text
a+b = 9
```

and:

```text
9 = 3 × 3
```

is composite.

Therefore:

```text
a²-b²
= 1 × 9
= 9
```

Answer:

```text
NO
```

---

## 2.7 C++

```cpp
#include <bits/stdc++.h>
using namespace std;

bool isPrime(long long n) {
    if (n < 2)
        return false;

    for (long long d = 2; d <= n / d; ++d) {
        if (n % d == 0)
            return false;
    }

    return true;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int T;
    cin >> T;

    while (T--) {
        long long a, b;
        cin >> a >> b;

        if (a - b == 1 && isPrime(a + b))
            cout << "YES\n";
        else
            cout << "NO\n";
    }
}
```

---

## 2.8 Complexity

We test primality only for:

```text
a+b
```

and only when:

```text
a-b == 1
```

Trial division costs:

```text
O(√(a+b))
```

---

## 2.9 Don't-Memorize Model

Do **not** memorize:

```text
if (a-b == 1 && prime(a+b))
```

Derive it:

```text
a²-b²
   |
   v
(a-b)(a+b)
   |
   v
product must be PRIME
   |
   v
one positive factor must be 1
   |
   v
a+b > 1
   |
   v
a-b must be 1
   |
   v
remaining factor a+b must be prime
```

Recognition:

```text
expression contains a²-b²
→ factor first
→ reason about the factors
```

---

# 3. Digit Space — CodeChef DSP

**Problem:** [CodeChef — Digit Space](https://www.codechef.com/problems/DSP)

The lecture models a number `X` through the set of values obtainable from its digit permutations, then looks for prime-factor overlap between the spaces of two numbers. fileciteturn27file0L23-L31

---

## 3.1 Preliminary Model From the Lecture

For a number `X`, think of its **digit space** as numbers obtained by permuting its digits.

Example idea:

```text
X = 2024
```

Possible digit arrangements include values formed from permutations of:

```text
2, 0, 2, 4
```

The lecture then models the target function as the **largest prime that divides some number from one digit space and some number from the other digit space**. fileciteturn27file0L27-L31

So conceptually:

```text
X
 |
 v
all valid digit permutations
 |
 v
prime factors appearing anywhere
 |
 v
set PX
```

Similarly:

```text
Y
 |
 v
all valid digit permutations
 |
 v
prime factors appearing anywhere
 |
 v
set PY
```

Then:

```text
answer = largest prime in PX ∩ PY
```

---

## 3.2 Why Permutations?

The digits stay the same.

Only their order changes.

Example:

```text
digits = 1, 2, 3
```

Permutations:

```text
123
132
213
231
312
321
```

C++:

```cpp
sort(s.begin(), s.end());

do {
    ...
} while (next_permutation(s.begin(), s.end()));
```

The lecture explicitly uses `next_permutation` as the enumeration mechanism. fileciteturn27file0L29-L34

---

## 3.3 Leading Zero Rule

Suppose digits are:

```text
0, 1, 2
```

Permutation:

```text
012
```

starts with zero.

The lecture notes indicate ignoring permutations with leading zeroes. fileciteturn27file0L33-L34

So:

```cpp
if (s[0] == '0')
    continue;
```

---

## 3.4 Prime-Factor Set

Suppose valid permutations of some small example produce:

```text
15
51
```

Factorise:

```text
15 = 3 × 5
51 = 3 × 17
```

Prime factors appearing anywhere:

```text
{3, 5, 17}
```

Notice:

```text
we care whether a prime appears,
not how many times it appears
```

Therefore a set / boolean presence array is natural.

---

## 3.5 Why SPF Helps

We may generate many permutation values.

Factorising every generated value using trial division repeats work.

If every generated number is bounded by some manageable `MAXV`, precompute:

```text
SPF[1 ... MAXV]
```

Then each permutation can be factorised quickly.

The lecture's later pages specifically move from permutation generation to prime factorisation using SPF. fileciteturn27file0L32-L36

---

## 3.6 Step-by-Step Algorithm

For `X`:

```text
1. Convert X to string.
2. Sort digits.
3. Generate every unique permutation.
4. Ignore leading-zero arrangements.
5. Convert permutation to integer.
6. Factorise using SPF.
7. Add every distinct prime factor to PX.
```

Do the same for `Y`:

```text
build PY
```

Finally:

```text
find common primes
take the largest one
```

---

## 3.7 Small Conceptual Dry Run

Use a small illustrative example:

```text
X = 15
Y = 35
```

### Space of X

Digits:

```text
1, 5
```

Permutations:

```text
15
51
```

Factorisations:

```text
15 = 3 × 5
51 = 3 × 17
```

Therefore:

```text
PX = {3, 5, 17}
```

### Space of Y

Digits:

```text
3, 5
```

Permutations:

```text
35
53
```

Factorisations:

```text
35 = 5 × 7
53 = 53
```

Therefore:

```text
PY = {5, 7, 53}
```

Intersection:

```text
PX ∩ PY = {5}
```

Therefore:

```text
answer = 5
```

The important model is:

```text
permutations
    ↓
factor each
    ↓
union prime factors for X
    ↓
union prime factors for Y
    ↓
intersection
    ↓
largest common prime
```

---

## 3.8 Helper — Add Prime Factors Using SPF

```cpp
void addPrimeFactors(
    int x,
    const vector<int>& spf,
    unordered_set<int>& primes
) {
    while (x > 1) {
        int p = spf[x];
        primes.insert(p);

        while (x % p == 0)
            x /= p;
    }
}
```

---

## 3.9 Helper — Process All Digit Permutations

```cpp
unordered_set<int> collectPrimeFactors(
    string s,
    const vector<int>& spf
) {
    unordered_set<int> primes;

    sort(s.begin(), s.end());

    do {
        if (s[0] == '0')
            continue;

        int value = stoi(s);
        addPrimeFactors(value, spf, primes);

    } while (next_permutation(s.begin(), s.end()));

    return primes;
}
```

---

## 3.10 C++ Model

> This implementation follows the lecture's permutation → SPF → prime-set model. The exact numeric bound for the SPF array must match the official problem constraints.

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> buildSPF(int n) {
    vector<int> spf(n + 1);

    for (int i = 0; i <= n; ++i)
        spf[i] = i;

    for (long long p = 2; p * p <= n; ++p) {
        if (spf[p] != p)
            continue;

        for (long long j = p * p; j <= n; j += p) {
            if (spf[j] == j)
                spf[j] = (int)p;
        }
    }

    return spf;
}

void addPrimeFactors(
    int x,
    const vector<int>& spf,
    unordered_set<int>& primes
) {
    while (x > 1) {
        int p = spf[x];
        primes.insert(p);

        while (x % p == 0)
            x /= p;
    }
}

unordered_set<int> collectPrimeFactors(
    string s,
    const vector<int>& spf
) {
    unordered_set<int> primes;

    sort(s.begin(), s.end());

    do {
        if (s[0] == '0')
            continue;

        int value = stoi(s);
        addPrimeFactors(value, spf, primes);

    } while (next_permutation(s.begin(), s.end()));

    return primes;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    string x, y;
    cin >> x >> y;

    // Set this from the official problem's maximum possible
    // permutation value.
    const int MAXV = 10000000;

    vector<int> spf = buildSPF(MAXV);

    auto px = collectPrimeFactors(x, spf);
    auto py = collectPrimeFactors(y, spf);

    int answer = -1;

    for (int p : px) {
        if (py.count(p))
            answer = max(answer, p);
    }

    cout << answer << '\n';
}
```

---

## 3.11 Complexity Model

If the number has `d` digits, there are at most:

```text
d!
```

permutations.

For each generated value, SPF factorisation takes roughly:

```text
O(log V)
```

So conceptually:

```text
O(d! × log V)
```

per number, after SPF preprocessing.

Repeated digits reduce the number of unique permutations visited by `next_permutation`.

---

## 3.12 Don't-Memorize Model

```text
Problem allows rearranging digits
            |
            v
number of digits is small enough
            |
            v
enumerate permutations
            |
            v
problem asks about divisibility / primes
            |
            v
factor each generated number
            |
            v
store only prime presence
            |
            v
compare the two prime sets
            |
            v
largest common prime
```

Recognition:

```text
small digit count
+ arbitrary reorderings
→ permutations

many bounded values need factorisation
→ SPF

"exists in both groups"
→ sets / intersection
```

---

# 4. Final Recognition Table

| Signal in problem | Think |
|---|---|
| exactly `k` distinct prime divisors | factorisation + count unique primes |
| many bounded factorisation queries | SPF |
| `a²-b²` | `(a-b)(a+b)` |
| product must be prime | one positive factor must be `1` |
| primality of one large-ish number | trial division to `√N` |
| rearrange all digits | sort + `next_permutation` |
| leading zero forbidden | skip when first digit is `0` |
| prime divides numbers from both groups | common prime-factor sets |
| largest common prime | set intersection + maximum |

---

# 5. Don't-Memorize Model

The three problems are useful because they demonstrate three different ways number theory appears in contests.

```text
PROBLEM 1 — ALMOST PRIME
------------------------
"how many distinct prime divisors?"
              |
              v
       factorisation / SPF
              |
              v
       count unique primes


PROBLEM 2 — SQUARE DIFFERENCE
-----------------------------
          a² - b²
              |
              v
        algebra first
              |
              v
       (a-b)(a+b)
              |
              v
     product must be prime
              |
              v
       one factor = 1


PROBLEM 3 — DIGIT SPACE
-----------------------
       rearrange digits
              |
              v
         permutations
              |
              v
      factor generated values
              |
              v
             SPF
              |
              v
        prime-factor sets
              |
              v
          intersection
```

## Final mental checklist

When a number-theory problem appears, ask:

```text
1. What exactly is being counted?
   divisors?
   distinct prime factors?
   multiplicity?

2. Can the expression be algebraically factorised?

3. Does "prime product" force one factor to become 1?

4. Is this one factorisation query or many?

5. Are values bounded enough for sieve/SPF?

6. Is the problem generating many arrangements of a small object?
   → permutation / enumeration

7. Do I need exact counts, or only whether a prime appears?
   → vector/count vs set/presence
```

> **Final memory anchor**
>
> **Factorisation reveals structure. Algebra reduces the search. SPF removes repeated work.**
