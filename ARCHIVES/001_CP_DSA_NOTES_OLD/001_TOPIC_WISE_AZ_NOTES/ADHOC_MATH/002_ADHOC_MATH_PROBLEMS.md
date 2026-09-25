# CP Math Concepts Applied — Phase Wise Problem Ladder

> Goal: understand **how each math concept is applied** in Codeforces A/B/C/D and LeetCode contest problems.
>
> Focus: **Pattern → Trigger → Observation → Code → Dry Run**.

---

## Clickable Index

### Part 0 — How To Use This File
1. [How Concepts Are Applied](#1-how-concepts-are-applied)
2. [5-Second Pattern Recognition Table](#2-5-second-pattern-recognition-table)
3. [Practice Method](#3-practice-method)

### Part 1 — Core Math Concepts Applied
4. [Parity / Odd-Even](#4-parity--odd-even)
5. [Invariants](#5-invariants)
6. [Modulo / Cyclic Math](#6-modulo--cyclic-math)
7. [GCD / LCM](#7-gcd--lcm)
8. [Prime / Sieve / Factorization](#8-prime--sieve--factorization)
9. [Divisors](#9-divisors)
10. [XOR / Bit Math](#10-xor--bit-math)
11. [Counting / Contribution](#11-counting--contribution)
12. [Combinatorics](#12-combinatorics)
13. [Inclusion-Exclusion](#13-inclusion-exclusion)
14. [Binary Exponentiation / Modular Inverse](#14-binary-exponentiation--modular-inverse)
15. [Pigeonhole Principle](#15-pigeonhole-principle)
16. [Game Theory Basics](#16-game-theory-basics)
17. [Coordinate Geometry Basics](#17-coordinate-geometry-basics)

### Part 2 — How Math Connects To CF Thinking
18. [Math in Ad Hoc Problems](#18-math-in-ad-hoc-problems)
19. [Math in Greedy Problems](#19-math-in-greedy-problems)
20. [Math in Constructive Problems](#20-math-in-constructive-problems)
21. [Pattern Recognition Checklist](#21-pattern-recognition-checklist)
22. [A/B/C/D Practice Ladder](#22-abcd-practice-ladder)

---

# 1. How Concepts Are Applied

In contest problems, math is usually not used as a direct formula. It is used as an **observation unlock**.

```text
Problem statement
    ↓
Recognize trigger
    ↓
Find invariant / formula / modulo / parity / count
    ↓
Reduce problem
    ↓
Simple code
```

Example:

```text
Operation: add 2
Trigger: parity
Observation: odd/even never changes
Result: possible/impossible check
```

---

# 2. 5-Second Pattern Recognition Table

| If you see this | Think this concept |
|---|---|
| +2 / -2 / odd-even | Parity |
| repeated operation | Invariant |
| circular / days / rotations | Modulo |
| divisibility / common factor | GCD / LCM |
| prime / divisor / coprime | Prime / Factorization |
| pair cancels / bitwise | XOR |
| count pairs / subarrays | Contribution |
| choose groups / ways | Combinatorics |
| count A or B | Inclusion-Exclusion |
| huge power / modulo answer | Binary exponentiation |
| too many objects into few boxes | Pigeonhole |
| two-player take game | Game theory |
| points / lines / triangle | Geometry |
| build any answer | Constructive math |
| minimize/maximize | Greedy math |
| simple hidden trick | Ad hoc math |

---

# 3. Practice Method

For each problem, write this after solving:

```text
Pattern:
Trigger:
Observation:
Why it works:
Mistake to avoid:
```

Do not only write code. Your goal is to train **recognition**.

---

# 4. Parity / Odd-Even

## How this concept is applied

Parity is used when operations preserve odd/even nature.

Common triggers:

```text
+2, -2
odd/even count
possible/impossible
same parity required
```

---

## Problem 4.1 — Codeforces 1324A: Yet Another Tetris Problem

Link: https://codeforces.com/problemset/problem/1324/A

### Pattern

Parity invariant.

### Statement Summary

You can increase elements by 2. Can all elements become equal?

### Input Example

```text
1
3
1 3 5
```

### Output

```text
YES
```

### Observation

Adding 2 never changes parity.

```text
1 -> 3 -> 5 -> odd forever
2 -> 4 -> 6 -> even forever
```

So all elements must have the same parity.

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
        int n;
        cin >> n;
        vector<int> a(n);
        for (int &x : a) cin >> x;

        int p = a[0] % 2;
        bool ok = true;
        for (int x : a) {
            if (x % 2 != p) ok = false;
        }

        cout << (ok ? "YES" : "NO") << '\n';
    }
}
```

### Index-by-Index Dry Run

```text
a = [1, 3, 5]

index 0: 1 % 2 = 1, base parity = 1
index 1: 3 % 2 = 1, same
index 2: 5 % 2 = 1, same

Answer = YES
```

---

## Problem 4.2 — Codeforces 1542A: Odd Set

Link: https://codeforces.com/problemset/problem/1542/A

### Pattern

Parity count.

### Observation

To split `2n` numbers into `n` pairs where each pair has odd sum, each pair needs:

```text
odd + even = odd
```

So number of odds must equal number of evens.

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
        int n;
        cin >> n;
        int odd = 0, even = 0;
        for (int i = 0; i < 2 * n; i++) {
            int x;
            cin >> x;
            if (x % 2) odd++;
            else even++;
        }
        cout << (odd == even ? "Yes" : "No") << '\n';
    }
}
```

### Dry Run

```text
n = 2
arr = [1, 2, 3, 4]

odd = 2 -> 1,3
even = 2 -> 2,4

Can pair: (1,2), (3,4)
Answer = Yes
```

---

# 5. Invariants

## How this concept is applied

Invariant = something that never changes after operation.

Common triggers:

```text
operation repeated
possible/impossible
transform array
redistribute values
```

---

## Problem 5.1 — Equalize by Moving Units

Practice Type: Classic CP invariant model.

### Pattern

Sum invariant.

### Statement

You can choose `i, j`, subtract 1 from `a[i]`, and add 1 to `a[j]`. Can all elements become equal?

### Input Example

```text
n = 4
a = [1, 2, 3, 4]
```

### Output

```text
NO
```

### Observation

Total sum never changes.

For all elements to become equal:

```text
final value = sum / n
```

So `sum % n` must be `0`.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;
    vector<int> a(n);
    long long sum = 0;
    for (int &x : a) {
        cin >> x;
        sum += x;
    }

    cout << (sum % n == 0 ? "YES" : "NO") << '\n';
}
```

### Dry Run

```text
a = [1,2,3,4]

index 0: sum = 1
index 1: sum = 3
index 2: sum = 6
index 3: sum = 10

sum = 10
n = 4
10 % 4 = 2

Cannot split 10 into 4 equal integers.
Answer = NO
```

---

## Problem 5.2 — Codeforces 1367B: Even Array

Link: https://codeforces.com/problemset/problem/1367/B

### Pattern

Parity position invariant + swap correction.

### Observation

Index parity and value parity should match. Count mismatches.

If odd-position mismatches and even-position mismatches are equal, swaps can fix them.

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
        int n;
        cin >> n;
        int evenBad = 0, oddBad = 0;

        for (int i = 0; i < n; i++) {
            int x;
            cin >> x;
            if (i % 2 != x % 2) {
                if (i % 2 == 0) evenBad++;
                else oddBad++;
            }
        }

        if (evenBad == oddBad) cout << evenBad << '\n';
        else cout << -1 << '\n';
    }
}
```

### Dry Run

```text
a = [3,2,7,6]

index 0 should be even, value 3 odd -> evenBad = 1
index 1 should be odd, value 2 even -> oddBad = 1
index 2 should be even, value 7 odd -> evenBad = 2
index 3 should be odd, value 6 even -> oddBad = 2

Swap mismatched pairs.
Answer = 2
```

---

# 6. Modulo / Cyclic Math

## How this concept is applied

Modulo compresses infinite movement into finite remainder classes.

Common triggers:

```text
cycle
rotation
large operations
add/subtract x
same remainder
```

---

## Problem 6.1 — Codeforces 1714E: Add Modulo 10

Link: https://codeforces.com/problemset/problem/1714/E

### Pattern

Modulo invariant after normalization.

### Observation

Numbers ending in `0` stop.

Numbers ending in `5` become ending in `0` after one operation.

Other numbers eventually enter a cycle where modulo `20` matters.

```text
2 -> 4 -> 8 -> 16 -> 22
Modulo 20: 2,4,8,16,2
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int normalize(int x) {
    while (x % 10 != 2 && x % 10 != 0) {
        x += x % 10;
    }
    return x;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;
    while (t--) {
        int n;
        cin >> n;
        vector<int> a(n);
        bool hasZero = false;

        for (int i = 0; i < n; i++) {
            cin >> a[i];
            if (a[i] % 10 == 5) a[i] += 5;
            if (a[i] % 10 == 0) hasZero = true;
        }

        if (hasZero) {
            bool ok = true;
            for (int i = 1; i < n; i++) {
                if (a[i] != a[0]) ok = false;
            }
            cout << (ok ? "Yes" : "No") << '\n';
        } else {
            for (int &x : a) x = normalize(x);
            int rem = a[0] % 20;
            bool ok = true;
            for (int x : a) {
                if (x % 20 != rem) ok = false;
            }
            cout << (ok ? "Yes" : "No") << '\n';
        }
    }
}
```

### Dry Run

```text
a = [6, 11]

6 -> 6 + 6 = 12
11 -> 11 + 1 = 12

normalize both:
6 becomes 12, 12 % 20 = 12
11 becomes 12, 12 % 20 = 12

Answer = Yes
```

---

## Problem 6.2 — Codeforces 1560A: Dislike of Threes

Link: https://codeforces.com/problemset/problem/1560/A

### Pattern

Modulo filtering.

### Observation

Valid numbers are not divisible by `3` and do not end in `3`.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool good(int x) {
    return x % 3 != 0 && x % 10 != 3;
}

int main() {
    vector<int> vals;
    for (int x = 1; (int)vals.size() < 1000; x++) {
        if (good(x)) vals.push_back(x);
    }

    int t;
    cin >> t;
    while (t--) {
        int k;
        cin >> k;
        cout << vals[k - 1] << '\n';
    }
}
```

### Dry Run

```text
numbers:
1 good
2 good
3 bad, divisible by 3 and ends with 3
4 good
5 good
6 bad
7 good
8 good
9 bad
10 good

Build list and answer kth.
```

---

# 7. GCD / LCM

## How this concept is applied

GCD appears when values are changed by common steps or differences.

Common triggers:

```text
divisibility
common factor
add/subtract same value
array differences
make equal using steps
```

---

## Problem 7.1 — Codeforces 1458A: Row GCD

Link: https://codeforces.com/problemset/problem/1458/A

### Pattern

GCD of differences.

### Observation

For array `a`, after adding query value `b`, we need gcd of:

```text
gcd(a1+b, a2+b, a3+b, ...)
```

GCD property:

```text
gcd(x, y) = gcd(x, y - x)
```

So answer:

```text
gcd(a1 + b, gcd(a2-a1, a3-a1, ...))
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;
#define int long long

signed main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;
    vector<int> a(n), b(m);
    for (int &x : a) cin >> x;
    for (int &x : b) cin >> x;

    int g = 0;
    for (int i = 1; i < n; i++) {
        g = gcd(g, abs(a[i] - a[0]));
    }

    for (int x : b) {
        cout << gcd(a[0] + x, g) << ' ';
    }
    cout << '\n';
}
```

### Dry Run

```text
a = [6, 10, 14]

base = 6
differences:
10 - 6 = 4
14 - 6 = 8

g = gcd(4,8) = 4

query b = 2
answer = gcd(a[0] + b, g)
       = gcd(8, 4)
       = 4
```

---

## Problem 7.2 — Codeforces 1543A: Exciting Bets

Link: https://codeforces.com/problemset/problem/1543/A

### Pattern

Difference invariant.

### Observation

You can change both numbers. Maximum possible gcd becomes `abs(a-b)`.

Minimum moves = distance to nearest multiple of `d`.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;
#define int long long

signed main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;
    while (t--) {
        int a, b;
        cin >> a >> b;
        int d = abs(a - b);

        if (d == 0) {
            cout << "0 0\n";
            continue;
        }

        int r = min(a % d, d - (a % d));
        cout << d << ' ' << r << '\n';
    }
}
```

### Dry Run

```text
a = 10, b = 4

d = |10 - 4| = 6
max gcd = 6

a % d = 10 % 6 = 4
nearest moves = min(4, 6-4) = 2

Answer = 6 2
```

---

# 8. Prime / Sieve / Factorization

## How this concept is applied

Prime/factorization helps when problem asks divisors, coprime, prime checks, or repeated factor grouping.

---

## Problem 8.1 — Count Primes Up To N

Practice Type: Sieve foundation.

### Pattern

Sieve.

### Input

```text
n = 10
```

### Output

```text
4
```

### Observation

Instead of checking every number separately, mark multiples.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;
    vector<bool> prime(n + 1, true);
    if (n >= 0) prime[0] = false;
    if (n >= 1) prime[1] = false;

    for (int i = 2; i * i <= n; i++) {
        if (prime[i]) {
            for (int j = i * i; j <= n; j += i) {
                prime[j] = false;
            }
        }
    }

    int cnt = 0;
    for (int i = 2; i <= n; i++) cnt += prime[i];
    cout << cnt << '\n';
}
```

### Dry Run

```text
n = 10
Start: 2 3 4 5 6 7 8 9 10
2 prime -> mark 4,6,8,10
3 prime -> mark 9
Remaining: 2,3,5,7
Count = 4
```

---

## Problem 8.2 — Codeforces 230B: T-primes

Link: https://codeforces.com/problemset/problem/230/B

### Pattern

Prime square observation.

### Observation

A number has exactly 3 divisors iff it is square of a prime.

```text
x = p^2
Divisors: 1, p, p^2
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;
#define int long long

signed main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    const int N = 1000000;
    vector<bool> prime(N + 1, true);
    prime[0] = prime[1] = false;
    for (int i = 2; i * i <= N; i++) {
        if (prime[i]) {
            for (int j = i * i; j <= N; j += i) prime[j] = false;
        }
    }

    int n;
    cin >> n;
    while (n--) {
        long long x;
        cin >> x;
        long long r = sqrtl(x);
        while (r * r < x) r++;
        while (r * r > x) r--;

        if (r * r == x && prime[r]) cout << "YES\n";
        else cout << "NO\n";
    }
}
```

### Dry Run

```text
x = 49
sqrt(49) = 7
7 is prime
49 = 7^2
Divisors = 1,7,49
Answer = YES
```

---

# 9. Divisors

## How this concept is applied

Divisor problems use pair property:

```text
if d divides n, then n/d also divides n
```

---

## Problem 9.1 — Print Divisors

Practice Type: Basic divisor enumeration.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long n;
    cin >> n;
    vector<long long> divs;

    for (long long d = 1; d * d <= n; d++) {
        if (n % d == 0) {
            divs.push_back(d);
            if (d != n / d) divs.push_back(n / d);
        }
    }

    sort(divs.begin(), divs.end());
    for (long long d : divs) cout << d << ' ';
}
```

### Dry Run

```text
n = 36

d=1 -> 1,36
d=2 -> 2,18
d=3 -> 3,12
d=4 -> 4,9
d=5 -> no
d=6 -> 6,6

Answer: 1 2 3 4 6 9 12 18 36
```

---

# 10. XOR / Bit Math

## How this concept is applied

XOR is used when values cancel.

Common triggers:

```text
pairs
duplicates
unique number
bitwise operation
xor target
```

---

## Problem 10.1 — LeetCode Single Number

Link: https://leetcode.com/problems/single-number/

### Pattern

XOR cancellation.

### Observation

```text
a ^ a = 0
x ^ 0 = x
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int singleNumber(vector<int>& nums) {
    int xr = 0;
    for (int x : nums) xr ^= x;
    return xr;
}
```

### Dry Run

```text
a = [4,1,2,1,2]

xr = 0
index 0: xr = 0 ^ 4 = 4
index 1: xr = 4 ^ 1 = 5
index 2: xr = 5 ^ 2 = 7
index 3: xr = 7 ^ 1 = 6
index 4: xr = 6 ^ 2 = 4

Answer = 4
```

---

## Problem 10.2 — Codeforces 1527A: And Then There Were K

Link: https://codeforces.com/problemset/problem/1527/A

### Pattern

Highest power of 2 / bit observation.

### Observation

Answer is one less than highest power of 2 not exceeding `n`.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int t;
    cin >> t;
    while (t--) {
        int n;
        cin >> n;
        int p = 1;
        while (p * 2 <= n) p *= 2;
        cout << p - 1 << '\n';
    }
}
```

### Dry Run

```text
n = 10
powers: 1,2,4,8
highest <= 10 is 8
answer = 8 - 1 = 7
```

---

# 11. Counting / Contribution

## How this concept is applied

When brute force counts all pairs/subarrays, contribution avoids O(n^2).

---

## Problem 11.1 — Codeforces 1520D: Same Differences

Link: https://codeforces.com/problemset/problem/1520/D

### Pattern

Transform pair equation.

### Observation

Condition:

```text
a[j] - a[i] = j - i
```

Rearrange:

```text
a[j] - j = a[i] - i
```

So count equal values of `a[i] - i`.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;
#define int long long

signed main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;
    while (t--) {
        int n;
        cin >> n;
        map<int,int> freq;
        long long ans = 0;

        for (int i = 1; i <= n; i++) {
            int x;
            cin >> x;
            int key = x - i;
            ans += freq[key];
            freq[key]++;
        }

        cout << ans << '\n';
    }
}
```

### Dry Run

```text
a = [3, 5, 1, 4]
1-based index

i=1: key = 3-1 = 2, previous 0, ans=0, freq[2]=1
i=2: key = 5-2 = 3, previous 0, ans=0, freq[3]=1
i=3: key = 1-3 = -2, previous 0, ans=0, freq[-2]=1
i=4: key = 4-4 = 0, previous 0, ans=0

No pairs.
```

---

## Problem 11.2 — Codeforces 1398C: Good Subarrays

Link: https://codeforces.com/problemset/problem/1398/C

### Pattern

Prefix sum transformation.

### Observation

A subarray is good if:

```text
sum(l..r) = length(l..r)
```

Using prefix:

```text
pref[r] - pref[l-1] = r - l + 1
pref[r] - r = pref[l-1] - (l-1)
```

Count equal `pref[i] - i`.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;
#define int long long

signed main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;
    while (t--) {
        int n;
        string s;
        cin >> n >> s;

        map<int,int> freq;
        freq[0] = 1;
        int pref = 0;
        long long ans = 0;

        for (int i = 1; i <= n; i++) {
            pref += s[i - 1] - '0';
            int key = pref - i;
            ans += freq[key];
            freq[key]++;
        }

        cout << ans << '\n';
    }
}
```

### Dry Run

```text
s = "120"

freq[0] = 1

i=1: pref=1, key=1-1=0, ans += 1 -> ans=1
i=2: pref=3, key=3-2=1, ans += 0
i=3: pref=3, key=3-3=0, ans += 2 -> ans=3

Answer = 3
```

---

# 12. Combinatorics

## How this concept is applied

Combinatorics counts choices without enumerating all pairs/groups.

---

## Problem 12.1 — Count Equal Pairs

Practice Type: nC2 from frequency.

### Observation

If a value appears `cnt` times, number of pairs:

```text
cnt * (cnt - 1) / 2
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;
    map<int,long long> freq;
    for (int i = 0; i < n; i++) {
        int x;
        cin >> x;
        freq[x]++;
    }

    long long ans = 0;
    for (auto [val, cnt] : freq) {
        ans += cnt * (cnt - 1) / 2;
    }
    cout << ans << '\n';
}
```

### Dry Run

```text
a = [1,2,1,1,2]

freq[1] = 3 -> 3*2/2 = 3
freq[2] = 2 -> 2*1/2 = 1

answer = 4
```

---

## Problem 12.2 — Codeforces 1428F? Alternative Practice: Pair Counting Model

Use any pair-counting problem where equal frequency groups appear. The important CP trick is:

```text
Instead of checking all pairs, group by key and apply nC2.
```

---

# 13. Inclusion-Exclusion

## How this concept is applied

Used when counting numbers satisfying A or B and overlap is double-counted.

---

## Problem 13.1 — Count Numbers Divisible by A or B

Practice Type: Inclusion-exclusion.

### Statement

Count numbers from `1..n` divisible by `a` or `b`.

### Input

```text
n = 10, a = 2, b = 3
```

### Output

```text
7
```

### Observation

```text
count(divisible by 2) = 5 -> 2,4,6,8,10
count(divisible by 3) = 3 -> 3,6,9
overlap divisible by lcm(2,3)=6 -> 1 -> 6
answer = 5 + 3 - 1 = 7
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;
#define int long long

signed main() {
    int n, a, b;
    cin >> n >> a >> b;
    int l = lcm(a, b);
    cout << n / a + n / b - n / l << '\n';
}
```

### Dry Run

```text
n=10, a=2, b=3

n/a = 5
n/b = 3
lcm = 6
n/lcm = 1
answer = 5 + 3 - 1 = 7
```

---

# 14. Binary Exponentiation / Modular Inverse

## How this concept is applied

Used when power is huge or answer required modulo prime.

---

## Problem 14.1 — Fast Power Modulo

Practice Type: Binary exponentiation.

### Input

```text
a = 2, b = 10, mod = 1000000007
```

### Output

```text
1024
```

### Observation

Use exponent bits.

```text
2^10 = 2^8 * 2^2
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;
#define int long long

long long modpow(long long a, long long e, long long mod) {
    long long res = 1 % mod;
    a %= mod;
    while (e > 0) {
        if (e & 1) res = res * a % mod;
        a = a * a % mod;
        e >>= 1;
    }
    return res;
}

signed main() {
    long long a, b, mod;
    cin >> a >> b >> mod;
    cout << modpow(a, b, mod) << '\n';
}
```

### Dry Run

```text
a=2, e=10, res=1

e=10 even: a=4, e=5
e=5 odd: res=1*4=4, a=16, e=2
e=2 even: a=256, e=1
e=1 odd: res=4*256=1024, e=0

Answer = 1024
```

---

## Modular Inverse Note

When `mod` is prime:

```text
inverse(a) = a^(mod-2) % mod
```

Used in:

```text
nCr modulo
probability modulo
division under modulo
```

---

# 15. Pigeonhole Principle

## How this concept is applied

If there are more objects than boxes, at least one box has multiple objects.

---

## Problem 15.1 — Duplicate Remainder

Practice Type: Pigeonhole + modulo.

### Statement

Given `n+1` numbers. Show that two numbers have same remainder modulo `n`.

### Observation

There are only `n` possible remainders:

```text
0,1,2,...,n-1
```

But there are `n+1` numbers.

So two must share same remainder.

### Dry Run

```text
n = 3
numbers = [4, 7, 8, 10]

remainders modulo 3:
4 % 3 = 1
7 % 3 = 1
8 % 3 = 2
10 % 3 = 1

Remainder 1 repeats.
```

### Code To Detect Pair

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;
    vector<int> seen(n, -1);

    for (int i = 0; i < n + 1; i++) {
        int x;
        cin >> x;
        int r = ((x % n) + n) % n;
        if (seen[r] != -1) {
            cout << "Pair indices: " << seen[r] << " " << i << '\n';
            return 0;
        }
        seen[r] = i;
    }
}
```

---

# 16. Game Theory Basics

## How this concept is applied

Game theory usually asks who wins with optimal play.

Common triggers:

```text
Alice/Bob
turns
remove stones
optimal play
last move wins
xor game
```

---

## Problem 16.1 — Nim Basic

Practice Type: XOR game.

### Statement

There are piles. Players remove stones from one pile. Player unable to move loses.

### Observation

If xor of all piles is `0`, losing state. Else winning state.

### C++ Code

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
    cout << (xr ? "First" : "Second") << '\n';
}
```

### Dry Run

```text
piles = [1,2,3]

xr = 1 ^ 2 ^ 3
1 ^ 2 = 3
3 ^ 3 = 0

Losing for first.
Answer = Second
```

---

# 17. Coordinate Geometry Basics

## How this concept is applied

Geometry problems usually use distance, slope, area, or orientation.

---

## Problem 17.1 — Check Collinear Points

Practice Type: Cross product.

### Statement

Given three points, check if they lie on one line.

### Observation

Three points are collinear if triangle area is zero.

Cross product:

```text
(x2-x1)*(y3-y1) - (y2-y1)*(x3-x1) == 0
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;
#define int long long

signed main() {
    int x1,y1,x2,y2,x3,y3;
    cin >> x1 >> y1 >> x2 >> y2 >> x3 >> y3;

    long long cross = (x2-x1)*(y3-y1) - (y2-y1)*(x3-x1);
    cout << (cross == 0 ? "YES" : "NO") << '\n';
}
```

### Dry Run

```text
points: (0,0), (1,1), (2,2)

cross = (1-0)*(2-0) - (1-0)*(2-0)
      = 1*2 - 1*2
      = 0

Answer = YES
```

---

# 18. Math in Ad Hoc Problems

## How this is applied

Ad hoc means the solution depends on a hidden observation.

Math usually appears as:

```text
parity
modulo
small formula
invariant
casework
```

## Problem — Codeforces 1766A: Extremely Round

Link: https://codeforces.com/problemset/problem/1766/A

### Observation

Extremely round numbers have only one non-zero digit:

```text
1,2,...,9,10,20,...,90,100,200,...
```

Generate and count.

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> v;
    for (int p = 1; p <= 1000000; p *= 10) {
        for (int d = 1; d <= 9; d++) {
            v.push_back(d * p);
        }
    }
    sort(v.begin(), v.end());

    int t;
    cin >> t;
    while (t--) {
        int n;
        cin >> n;
        cout << upper_bound(v.begin(), v.end(), n) - v.begin() << '\n';
    }
}
```

### Dry Run

```text
n = 25
valid <= 25:
1 2 3 4 5 6 7 8 9 10 20
count = 11
```

---

# 19. Math in Greedy Problems

## How this is applied

Greedy math asks:

```text
What local choice is forced?
What should I sort?
What quantity should I maximize/minimize first?
```

---

## Problem — Codeforces 230A: Dragons

Link: https://codeforces.com/problemset/problem/230/A

### Pattern

Sort + greedy.

### Observation

Fight weakest dragon first. If you cannot beat current weakest, no other harder dragon can help.

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int s, n;
    cin >> s >> n;
    vector<pair<int,int>> dragons(n);
    for (auto &p : dragons) cin >> p.first >> p.second;

    sort(dragons.begin(), dragons.end());

    for (auto [strength, bonus] : dragons) {
        if (s <= strength) {
            cout << "NO\n";
            return 0;
        }
        s += bonus;
    }
    cout << "YES\n";
}
```

### Dry Run

```text
s = 2
Dragons: (1,99), (100,0)

sort -> (1,99), (100,0)

fight 1: 2 > 1, s = 101
fight 2: 101 > 100, s = 101

Answer = YES
```

---

# 20. Math in Constructive Problems

## How this is applied

Constructive asks you to build any valid answer.

Common triggers:

```text
construct array
find permutation
any valid answer
print -1 if impossible
```

---

## Problem — Codeforces 1343B: Balanced Array

Link: https://codeforces.com/problemset/problem/1343/B

### Pattern

Parity + construction.

### Observation

Need `n/2` even numbers and `n/2` odd numbers with equal sum.

Possible only when `n/2` is even.

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int t;
    cin >> t;
    while (t--) {
        int n;
        cin >> n;

        if ((n / 2) % 2 == 1) {
            cout << "NO\n";
            continue;
        }

        cout << "YES\n";
        vector<int> ans;
        int sumEven = 0, sumOdd = 0;

        for (int i = 1; i <= n / 2; i++) {
            ans.push_back(2 * i);
            sumEven += 2 * i;
        }

        for (int i = 1; i < n / 2; i++) {
            ans.push_back(2 * i - 1);
            sumOdd += 2 * i - 1;
        }

        ans.push_back(sumEven - sumOdd);

        for (int x : ans) cout << x << ' ';
        cout << '\n';
    }
}
```

### Dry Run

```text
n = 8
n/2 = 4 even -> possible

Even side:
2 4 6 8, sumEven = 20

Odd side first 3 odds:
1 3 5, sumOdd = 9

Last odd = 20 - 9 = 11

Answer:
2 4 6 8 1 3 5 11
```

---

# 21. Pattern Recognition Checklist

Before coding, ask:

```text
1. Does parity stay same?
2. Does total sum stay same?
3. Does modulo class stay same?
4. Is gcd of differences useful?
5. Can duplicates cancel with xor?
6. Can I transform pair condition?
7. Can I count by frequency instead of pairs?
8. Can I sort and greedily choose?
9. Can I construct using parity or extremes?
10. Is there a cycle?
```

---

# 22. A/B/C/D Practice Ladder

## Phase A — A-Level Foundation

| Problem | Link | Main Concept | Key Observation |
|---|---|---|---|
| Yet Another Tetris Problem | https://codeforces.com/problemset/problem/1324/A | Parity | +2 preserves parity |
| Odd Set | https://codeforces.com/problemset/problem/1542/A | Parity count | odd+even pair needed |
| Dislike of Threes | https://codeforces.com/problemset/problem/1560/A | Modulo filter | skip divisible by 3 / ending 3 |
| Extremely Round | https://codeforces.com/problemset/problem/1766/A | Ad hoc generation | one non-zero digit |
| Linear Keyboard | https://codeforces.com/problemset/problem/1607/A | Index math | convert char to position |

## Phase B — B-Level Observation

| Problem | Link | Main Concept | Key Observation |
|---|---|---|---|
| Even Array | https://codeforces.com/problemset/problem/1367/B | Invariant/parity | mismatch counts must match |
| Balanced Array | https://codeforces.com/problemset/problem/1343/B | Constructive/parity | n/2 must be even |
| Dragons | https://codeforces.com/problemset/problem/230/A | Greedy | fight weakest first |
| Taxi | https://codeforces.com/problemset/problem/158/B | Greedy counting | pair groups optimally |
| T-primes | https://codeforces.com/problemset/problem/230/B | Prime | square of prime has 3 divisors |

## Phase C — C-Level Counting / Math

| Problem | Link | Main Concept | Key Observation |
|---|---|---|---|
| Same Differences | https://codeforces.com/problemset/problem/1520/D | Contribution | group by a[i]-i |
| Good Subarrays | https://codeforces.com/problemset/problem/1398/C | Prefix transform | group by pref[i]-i |
| Number of Pairs | https://codeforces.com/problemset/problem/1538/C | Sorting + counting | count pair sums in range |
| Add Modulo 10 | https://codeforces.com/problemset/problem/1714/E | Modulo invariant | normalize then compare mod 20 |
| Exciting Bets | https://codeforces.com/problemset/problem/1543/A | GCD/difference | max gcd = abs(a-b) |

## Phase D — D-Level Direction

| Problem | Link | Main Concept | Key Observation |
|---|---|---|---|
| Row GCD | https://codeforces.com/problemset/problem/1458/A | GCD of differences | gcd(a1+b, differences) |
| And Then There Were K | https://codeforces.com/problemset/problem/1527/A | Bit math | highest power of 2 minus 1 |
| Good Subarrays | https://codeforces.com/problemset/problem/1398/C | Prefix counting | transform condition |
| Same Differences | https://codeforces.com/problemset/problem/1520/D | Pair transform | rearrange equation |
| Balanced Array | https://codeforces.com/problemset/problem/1343/B | Constructive | build with parity sums |

---

# Final Revision Sheet

```text
Parity:
+2 / -2 / odd-even -> parity invariant

Invariant:
operation repeated -> ask what never changes

Modulo:
+/- x -> remainder modulo x unchanged
cycle -> use modulo cycle length

GCD:
differences and divisibility -> gcd of differences

Prime:
exactly 3 divisors -> square of prime

Divisors:
loop until sqrt(n)

XOR:
duplicates cancel

Contribution:
count pairs by key/frequency

Combinatorics:
frequency cnt -> cnt*(cnt-1)/2

Inclusion-exclusion:
A or B = A + B - both

Binary exponentiation:
huge power -> use bits of exponent

Pigeonhole:
more objects than boxes -> duplicate exists

Game theory:
xor zero -> losing in Nim

Geometry:
cross product zero -> collinear

Ad Hoc:
look for hidden small observation

Greedy:
sort and choose forced local best

Constructive:
build answer while preserving condition
```
