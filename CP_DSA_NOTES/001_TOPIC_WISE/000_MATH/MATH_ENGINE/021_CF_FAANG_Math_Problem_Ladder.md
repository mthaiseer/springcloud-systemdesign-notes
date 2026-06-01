# 021_CF_FAANG_Math_Problem_Ladder

> Goal: After finishing `MiniMathEngine`, use this ladder to learn Codeforces A/B/C/D math forms and FAANG-style math interview forms.
>
> Style: **problem link → observation → form → commented C++ code**.

---

## Clickable Index

1. [How To Use This Ladder](#1-how-to-use-this-ladder)
2. [MiniMathEngine Topic Map](#2-minimathengine-topic-map)
3. [Level A — CF A / FAANG Easy Math Forms](#3-level-a--cf-a--faang-easy-math-forms)
4. [Level B — CF B / FAANG Easy-Medium Forms](#4-level-b--cf-b--faang-easy-medium-forms)
5. [Level C — CF C / FAANG Medium Math Forms](#5-level-c--cf-c--faang-medium-math-forms)
6. [Level D — CF D / FAANG Medium-Hard Math Forms](#6-level-d--cf-d--faang-medium-hard-math-forms)
7. [FAANG Math Interview Forms](#7-faang-math-interview-forms)
8. [Final Revision Cheatsheet](#8-final-revision-cheatsheet)

---

## 1. How To Use This Ladder

For every problem:

1. Read the statement.
2. Write only the **observation** first.
3. Identify the **math form**.
4. Code from scratch.
5. After AC, write one-line mental trigger.

Recommended pace:

| Stage | Problems | Goal |
|---|---:|---|
| CF A | 25–40 | Fast observation, formulas, parity, divisibility |
| CF B | 30–50 | Number theory + constructive math |
| CF C | 30–50 | Prefix modulo, combinatorics, greedy math |
| CF D | 20–35 | Advanced counting, modular arithmetic, proof-style math |
| FAANG | 30–50 | Interview clean implementation + edge cases |

---

## 2. MiniMathEngine Topic Map

| MiniMathEngine File | Contest Form |
|---|---|
| `001_Modulo_Basics.md` | Remainder cycles, divisibility, prefix modulo |
| `002_Fast_Power_Binary_Exponentiation.md` | Large powers, modular exponentiation |
| `003_Modular_Inverse.md` | Division under modulo, nCr mod prime |
| `004_GCD_LCM_Euclid.md` | Shared structure, synchronization, coprime logic |
| `005_Prime_Checking.md` | Single number prime logic |
| `006_Sieve_And_SPF.md` | Many prime queries, smallest prime factor |
| `007_Prime_Factorization.md` | Exponent counting, perfect square/cube forms |
| `008_Divisors_Count_Sum.md` | Number of divisors, divisor contribution |
| `009_Euler_Phi_Coprime.md` | Count coprimes, reduced fractions |
| `010_nCr_Basic.md` | Basic combinations |
| `011_nCr_Modulo_Prime.md` | Large nCr modulo prime |
| `012_Permutations_Repetition.md` | Arrangement counting |
| `013_Stars_And_Bars.md` | Distribute identical objects |
| `014_Inclusion_Exclusion.md` | Count union / avoid overcounting |
| `015_Catalan_Numbers.md` | Valid bracket / tree / non-crossing forms |
| `016_Derangements.md` | No fixed position counting |
| `017_CRT.md` | Combine remainders |
| `018_Diophantine_Equation.md` | Linear equation integer solutions |
| `019_Floor_Sum.md` | Sum of floors, multiples counting |
| `020_Math_Pattern_Decision_Engine.md` | Decide correct pattern fast |

---

## 3. Level A — CF A / FAANG Easy Math Forms

### A1. Parity Decision Form

**Problem:** [Codeforces 4A - Watermelon](https://codeforces.com/problemset/problem/4/A)

**Observation:** Split `w` into two positive even numbers. This is possible only when `w` is even and greater than `2`.

**Form:** Parity + lower bound.

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int w;
    cin >> w;

    // Need two positive even parts.
    // Smallest even positive split is 2 + 2 = 4.
    if (w % 2 == 0 && w > 2) {
        cout << "YES\n";
    } else {
        cout << "NO\n";
    }
    return 0;
}
```

**Mental trigger:** Even is not enough. Check minimum valid size.

---

### A2. Formula From Pattern Form

**Problem:** [Codeforces 486A - Calculating Function](https://codeforces.com/problemset/problem/486/A)

**Observation:** The sequence is `-1 + 2 - 3 + 4 ...`. Pair terms: `(-1 + 2) = 1`, `(-3 + 4) = 1`. If `n` is even answer is `n/2`; if odd answer is `-(n+1)/2`.

**Form:** Alternating sum formula.

```cpp
#include <bits/stdc++.h>
using namespace std;
using ll = long long;

int main() {
    ll n;
    cin >> n;

    if (n % 2 == 0) {
        cout << n / 2 << '\n';
    } else {
        cout << -(n + 1) / 2 << '\n';
    }
    return 0;
}
```

**Mental trigger:** Never loop when a direct formula exists.

---

### A3. Divisibility / Modulo Form

**Problem:** [Codeforces 136A - Presents](https://codeforces.com/problemset/problem/136/A)

**Observation:** If person `i` gives gift to `p[i]`, then inverse mapping answer is `ans[p[i]] = i`.

**Form:** Inverse mapping. Not pure number theory, but common CF A math-index form.

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<int> ans(n + 1);

    for (int giver = 1; giver <= n; giver++) {
        int receiver;
        cin >> receiver;
        ans[receiver] = giver;
    }

    for (int person = 1; person <= n; person++) {
        cout << ans[person] << ' ';
    }
    return 0;
}
```

**Mental trigger:** Given `i -> p[i]`, sometimes answer needs reverse `p[i] -> i`.

---

### A4. GCD Common Structure Form

**Problem:** [Codeforces 122A - Lucky Division](https://codeforces.com/problemset/problem/122/A)

**Observation:** Only need to check divisibility by lucky numbers made of digits `4` and `7` up to `n`.

**Form:** Divisibility by generated candidates.

```cpp
#include <bits/stdc++.h>
using namespace std;

bool isLucky(int x) {
    while (x > 0) {
        int d = x % 10;
        if (d != 4 && d != 7) return false;
        x /= 10;
    }
    return true;
}

int main() {
    int n;
    cin >> n;

    for (int x = 1; x <= n; x++) {
        // If x itself is lucky and divides n,
        // then n is almost lucky.
        if (isLucky(x) && n % x == 0) {
            cout << "YES\n";
            return 0;
        }
    }

    cout << "NO\n";
    return 0;
}
```

**Mental trigger:** For small constraints, generate all special numbers and test divisibility.

---

### A5. Prime Split / Constructive Math Form

**Problem:** [Codeforces 749A - Bachgold Problem](https://codeforces.com/problemset/problem/749/A)

**Observation:** To maximize number of prime summands, use as many `2`s as possible. If `n` is odd, use one `3` and remaining `2`s.

**Form:** Greedy prime construction.

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    if (n % 2 == 0) {
        cout << n / 2 << '\n';
        for (int i = 0; i < n / 2; i++) cout << 2 << ' ';
    } else {
        // One 3 makes remaining n - 3 even.
        cout << 1 + (n - 3) / 2 << '\n';
        cout << 3 << ' ';
        for (int i = 0; i < (n - 3) / 2; i++) cout << 2 << ' ';
    }
    return 0;
}
```

**Mental trigger:** For maximum count of primes in a sum, prefer smallest prime `2`.

---

## 4. Level B — CF B / FAANG Easy-Medium Forms

### B1. GCD / LCM Synchronization Form

**Problem:** [Codeforces 510A style practice + use on LCM cycles](https://codeforces.com/problemset/problem/235/A)

**Observation:** To maximize LCM of three numbers up to `n`, answer is usually product of three large numbers, but parity/gcd conflicts matter.

**Form:** LCM maximization using coprime candidates.

```cpp
#include <bits/stdc++.h>
using namespace std;
using ll = long long;

ll lcm2(ll a, ll b) {
    return a / gcd(a, b) * b;
}

int main() {
    ll n;
    cin >> n;

    ll best = 1;

    // For this problem, checking last few numbers is enough
    // because maximum LCM must come from large values near n.
    for (ll a = max(1LL, n - 10); a <= n; a++) {
        for (ll b = max(1LL, n - 10); b <= n; b++) {
            for (ll c = max(1LL, n - 10); c <= n; c++) {
                best = max(best, lcm2(lcm2(a, b), c));
            }
        }
    }

    cout << best << '\n';
    return 0;
}
```

**Mental trigger:** Maximum LCM usually comes from large near-coprime numbers.

---

### B2. Modular Cycle Form

**Problem:** [Codeforces 742A - Arpa’s hard exam and Mehrdad’s naive cheat](https://codeforces.com/problemset/problem/742/A)

**Observation:** Last digit of `1378^n` depends only on last digit `8^n`. Cycle is `8, 4, 2, 6`.

**Form:** Power last digit cycle.

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long n;
    cin >> n;

    int cycle[4] = {8, 4, 2, 6};

    // n is 1-indexed in the cycle.
    cout << cycle[(n - 1) % 4] << '\n';
    return 0;
}
```

**Mental trigger:** Last digit powers repeat in cycles of length 1, 2, or 4.

---

### B3. Factorization Exponent Form

**Problem:** [Codeforces 230B - T-primes](https://codeforces.com/problemset/problem/230/B)

**Observation:** A number has exactly 3 divisors iff it is square of a prime: `x = p^2`.

**Form:** Divisor count via prime exponent.

```cpp
#include <bits/stdc++.h>
using namespace std;
using ll = long long;

const int MAXN = 1000000;

int main() {
    vector<bool> prime(MAXN + 1, true);
    prime[0] = prime[1] = false;

    for (int i = 2; i * i <= MAXN; i++) {
        if (prime[i]) {
            for (int j = i * i; j <= MAXN; j += i) {
                prime[j] = false;
            }
        }
    }

    int q;
    cin >> q;

    while (q--) {
        ll x;
        cin >> x;

        ll r = sqrtl(x);

        // x must be a perfect square and sqrt(x) must be prime.
        if (r * r == x && prime[r]) cout << "YES\n";
        else cout << "NO\n";
    }

    return 0;
}
```

**Mental trigger:** Exactly 3 divisors means `1, p, p^2`.

---

### B4. Prefix Modulo Pigeonhole Form

**Problem:** [LeetCode 974 - Subarray Sums Divisible by K](https://leetcode.com/problems/subarray-sums-divisible-by-k/)

**Observation:** If two prefix sums have same remainder modulo `k`, their difference is divisible by `k`.

**Form:** Prefix modulo frequency counting.

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    int subarraysDivByK(vector<int>& nums, int k) {
        vector<int> freq(k, 0);
        freq[0] = 1;

        int pref = 0;
        int ans = 0;

        for (int x : nums) {
            pref = ((pref + x) % k + k) % k;

            // Previous prefixes with same remainder form valid subarrays.
            ans += freq[pref];
            freq[pref]++;
        }

        return ans;
    }
};
```

**Mental trigger:** Same remainder means divisible difference.

---

### B5. Binary Exponentiation Form

**Problem:** [CSES - Exponentiation](https://cses.fi/problemset/task/1095)

**Observation:** Compute `a^b mod M` by repeated squaring in `O(log b)`.

**Form:** Fast power.

```cpp
#include <bits/stdc++.h>
using namespace std;
using ll = long long;

const ll MOD = 1000000007;

ll binpow(ll a, ll b) {
    ll ans = 1;
    a %= MOD;

    while (b > 0) {
        if (b & 1) ans = ans * a % MOD;
        a = a * a % MOD;
        b >>= 1;
    }

    return ans;
}

int main() {
    int t;
    cin >> t;

    while (t--) {
        ll a, b;
        cin >> a >> b;
        cout << binpow(a, b) << '\n';
    }

    return 0;
}
```

**Mental trigger:** Large power means binary exponentiation.

---

## 5. Level C — CF C / FAANG Medium Math Forms

### C1. Combinatorics nCr Form

**Problem:** [LeetCode 62 - Unique Paths](https://leetcode.com/problems/unique-paths/)

**Observation:** To go from top-left to bottom-right, you need `(m-1)` down moves and `(n-1)` right moves. Total moves = `m+n-2`. Choose positions for down moves.

**Form:** Combination: `C(m+n-2, m-1)`.

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    int uniquePaths(int m, int n) {
        long long ans = 1;
        int total = m + n - 2;
        int choose = min(m - 1, n - 1);

        // Compute C(total, choose) without overflow for LeetCode constraints.
        for (int i = 1; i <= choose; i++) {
            ans = ans * (total - choose + i) / i;
        }

        return (int)ans;
    }
};
```

**Mental trigger:** Path with fixed counts of moves = choose positions.

---

### C2. Stars and Bars Form

**Problem:** [CSES - Distributing Apples](https://cses.fi/problemset/task/1716)

**Observation:** Number of ways to distribute `m` identical apples among `n` children is `C(n + m - 1, n - 1)`.

**Form:** Stars and bars + modulo nCr.

```cpp
#include <bits/stdc++.h>
using namespace std;
using ll = long long;

const ll MOD = 1000000007;

ll modpow(ll a, ll b) {
    ll ans = 1;
    while (b) {
        if (b & 1) ans = ans * a % MOD;
        a = a * a % MOD;
        b >>= 1;
    }
    return ans;
}

int main() {
    int n, m;
    cin >> n >> m;

    int N = n + m;
    vector<ll> fact(N + 1), invFact(N + 1);
    fact[0] = 1;

    for (int i = 1; i <= N; i++) fact[i] = fact[i - 1] * i % MOD;

    invFact[N] = modpow(fact[N], MOD - 2);
    for (int i = N - 1; i >= 0; i--) invFact[i] = invFact[i + 1] * (i + 1) % MOD;

    auto nCr = [&](int a, int b) -> ll {
        if (b < 0 || b > a) return 0;
        return fact[a] * invFact[b] % MOD * invFact[a - b] % MOD;
    };

    cout << nCr(n + m - 1, n - 1) << '\n';
    return 0;
}
```

**Mental trigger:** Identical items into boxes = stars and bars.

---

### C3. Inclusion-Exclusion Form

**Problem:** [CSES - Prime Multiples](https://cses.fi/problemset/task/2185)

**Observation:** Count numbers `<= n` divisible by at least one prime using subset inclusion-exclusion.

**Form:** Inclusion-exclusion over subsets.

```cpp
#include <bits/stdc++.h>
using namespace std;
using ll = long long;

int main() {
    ll n;
    int k;
    cin >> n >> k;

    vector<ll> p(k);
    for (ll &x : p) cin >> x;

    ll ans = 0;

    for (int mask = 1; mask < (1 << k); mask++) {
        ll product = 1;
        int bits = 0;
        bool overflow = false;

        for (int i = 0; i < k; i++) {
            if (mask & (1 << i)) {
                bits++;
                if (product > n / p[i]) {
                    overflow = true;
                    break;
                }
                product *= p[i];
            }
        }

        if (overflow) continue;

        ll count = n / product;

        if (bits % 2 == 1) ans += count;
        else ans -= count;
    }

    cout << ans << '\n';
    return 0;
}
```

**Mental trigger:** OR condition in counting often means inclusion-exclusion.

---

### C4. Divisor Contribution Form

**Problem:** [CSES - Sum of Divisors](https://cses.fi/problemset/task/1082)

**Observation:** Instead of summing divisors for every number, count how many numbers have divisor `d`. Contribution of `d` is `d * floor(n/d)`.

**Form:** Contribution + floor grouping.

```cpp
#include <bits/stdc++.h>
using namespace std;
using ll = long long;

const ll MOD = 1000000007;

ll sumRange(ll l, ll r) {
    // Sum l + (l+1) + ... + r modulo MOD.
    ll cnt = (r - l + 1) % MOD;
    ll s = ((l + r) % MOD) * cnt % MOD;
    return s * 500000004 % MOD; // inverse of 2 mod 1e9+7
}

int main() {
    ll n;
    cin >> n;

    ll ans = 0;

    for (ll l = 1; l <= n; ) {
        ll q = n / l;
        ll r = n / q;

        // For all d in [l, r], floor(n / d) is q.
        ans = (ans + sumRange(l, r) * (q % MOD)) % MOD;

        l = r + 1;
    }

    cout << ans << '\n';
    return 0;
}
```

**Mental trigger:** If `floor(n / i)` appears, group equal quotient ranges.

---

### C5. Diophantine Equation Form

**Problem:** [Codeforces 7C - Line](https://codeforces.com/problemset/problem/7/C)

**Observation:** Equation `ax + by + c = 0` becomes `ax + by = -c`. Integer solution exists iff `gcd(a,b)` divides `-c`.

**Form:** Extended Euclid.

```cpp
#include <bits/stdc++.h>
using namespace std;
using ll = long long;

ll extgcd(ll a, ll b, ll &x, ll &y) {
    if (b == 0) {
        x = 1;
        y = 0;
        return a;
    }

    ll x1, y1;
    ll g = extgcd(b, a % b, x1, y1);

    x = y1;
    y = x1 - (a / b) * y1;
    return g;
}

int main() {
    ll a, b, c;
    cin >> a >> b >> c;

    ll x, y;
    ll g = extgcd(abs(a), abs(b), x, y);

    ll target = -c;

    if (target % g != 0) {
        cout << -1 << '\n';
        return 0;
    }

    x *= target / g;
    y *= target / g;

    if (a < 0) x = -x;
    if (b < 0) y = -y;

    cout << x << ' ' << y << '\n';
    return 0;
}
```

**Mental trigger:** `ax + by = c` integer solution iff `gcd(a,b) | c`.

---

## 6. Level D — CF D / FAANG Medium-Hard Math Forms

### D1. Catalan Number Form

**Problem:** [CSES - Bracket Sequences I](https://cses.fi/problemset/task/2064)

**Observation:** Number of valid bracket sequences of length `n` is Catalan number `C(n, n/2) / (n/2 + 1)` when `n` is even.

**Form:** Catalan + modular inverse.

```cpp
#include <bits/stdc++.h>
using namespace std;
using ll = long long;

const ll MOD = 1000000007;

ll modpow(ll a, ll b) {
    ll ans = 1;
    while (b) {
        if (b & 1) ans = ans * a % MOD;
        a = a * a % MOD;
        b >>= 1;
    }
    return ans;
}

int main() {
    int n;
    cin >> n;

    if (n % 2 == 1) {
        cout << 0 << '\n';
        return 0;
    }

    int m = n / 2;

    vector<ll> fact(n + 1), invFact(n + 1);
    fact[0] = 1;
    for (int i = 1; i <= n; i++) fact[i] = fact[i - 1] * i % MOD;

    invFact[n] = modpow(fact[n], MOD - 2);
    for (int i = n - 1; i >= 0; i--) invFact[i] = invFact[i + 1] * (i + 1) % MOD;

    auto nCr = [&](int a, int b) -> ll {
        if (b < 0 || b > a) return 0;
        return fact[a] * invFact[b] % MOD * invFact[a - b] % MOD;
    };

    ll catalan = nCr(n, m) * modpow(m + 1, MOD - 2) % MOD;
    cout << catalan << '\n';

    return 0;
}
```

**Mental trigger:** Valid brackets / non-crossing / full binary tree count often means Catalan.

---

### D2. Derangement Form

**Problem:** [CSES - Christmas Party](https://cses.fi/problemset/task/1717)

**Observation:** Derangement recurrence: `dp[n] = (n - 1) * (dp[n - 1] + dp[n - 2])`.

**Form:** Count permutations with no fixed point.

```cpp
#include <bits/stdc++.h>
using namespace std;
using ll = long long;

const ll MOD = 1000000007;

int main() {
    int n;
    cin >> n;

    vector<ll> dp(n + 2);
    dp[0] = 1;
    dp[1] = 0;

    for (int i = 2; i <= n; i++) {
        dp[i] = (i - 1) * (dp[i - 1] + dp[i - 2]) % MOD;
    }

    cout << dp[n] << '\n';
    return 0;
}
```

**Mental trigger:** No one gets own item = derangement.

---

### D3. Chinese Remainder Form

**Problem:** [CRT Practice - Kattis Chinese Remainder](https://open.kattis.com/problems/chineseremainder)

**Observation:** Combine two congruences into one using modular inverse when moduli are coprime.

**Form:** CRT.

```cpp
#include <bits/stdc++.h>
using namespace std;
using ll = long long;

ll extgcd(ll a, ll b, ll &x, ll &y) {
    if (b == 0) {
        x = 1;
        y = 0;
        return a;
    }
    ll x1, y1;
    ll g = extgcd(b, a % b, x1, y1);
    x = y1;
    y = x1 - (a / b) * y1;
    return g;
}

ll modInverse(ll a, ll mod) {
    ll x, y;
    extgcd(a, mod, x, y);
    x %= mod;
    if (x < 0) x += mod;
    return x;
}

int main() {
    int t;
    cin >> t;

    while (t--) {
        ll a, n, b, m;
        cin >> a >> n >> b >> m;

        // x = a mod n
        // x = b mod m
        ll invN = modInverse(n, m);
        ll k = ((b - a) % m + m) % m;
        k = k * invN % m;

        ll lcm = n * m;
        ll x = (a + n * k) % lcm;

        cout << x << ' ' << lcm << '\n';
    }

    return 0;
}
```

**Mental trigger:** Multiple remainder conditions = CRT.

---

### D4. Floor Sum / Multiples Form

**Problem:** [AtCoder Library Practice - Floor Sum](https://atcoder.jp/contests/practice2/tasks/practice2_c)

**Observation:** Need efficient sum of floor values. This is classic recursive Euclidean reduction.

**Form:** Floor sum.

```cpp
#include <bits/stdc++.h>
using namespace std;
using ll = long long;

// Returns sum_{i=0}^{n-1} floor((a*i + b) / m)
ll floor_sum(ll n, ll m, ll a, ll b) {
    ll ans = 0;

    while (true) {
        if (a >= m) {
            ans += (n - 1) * n * (a / m) / 2;
            a %= m;
        }

        if (b >= m) {
            ans += n * (b / m);
            b %= m;
        }

        ll yMax = a * n + b;
        if (yMax < m) break;

        n = yMax / m;
        b = yMax % m;
        swap(m, a);
    }

    return ans;
}

int main() {
    int T;
    cin >> T;

    while (T--) {
        ll n, m, a, b;
        cin >> n >> m >> a >> b;
        cout << floor_sum(n, m, a, b) << '\n';
    }

    return 0;
}
```

**Mental trigger:** Sum of many floor expressions = floor_sum / quotient grouping.

---

## 7. FAANG Math Interview Forms

### F1. Sqrt Without Library

**Problem:** [LeetCode 69 - Sqrt(x)](https://leetcode.com/problems/sqrtx/)

**Observation:** Need largest `mid` such that `mid * mid <= x`. Avoid overflow using `mid <= x / mid`.

**Form:** Binary search on answer.

```cpp
class Solution {
public:
    int mySqrt(int x) {
        long long lo = 0, hi = x, ans = 0;

        while (lo <= hi) {
            long long mid = lo + (hi - lo) / 2;

            if (mid <= x / max(1LL, mid)) {
                ans = mid;
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }

        return (int)ans;
    }
};
```

---

### F2. Pow(x, n)

**Problem:** [LeetCode 50 - Pow(x, n)](https://leetcode.com/problems/powx-n/)

**Observation:** Use binary exponentiation. Convert exponent to `long long` because `INT_MIN` cannot be safely negated as int.

**Form:** Fast power with negative exponent.

```cpp
class Solution {
public:
    double myPow(double x, int n) {
        long long power = n;

        if (power < 0) {
            x = 1.0 / x;
            power = -power;
        }

        double ans = 1.0;

        while (power > 0) {
            if (power & 1LL) ans *= x;
            x *= x;
            power >>= 1LL;
        }

        return ans;
    }
};
```

---

### F3. Count Primes

**Problem:** [LeetCode 204 - Count Primes](https://leetcode.com/problems/count-primes/)

**Observation:** Use sieve. Mark multiples starting from `i*i`.

**Form:** Sieve of Eratosthenes.

```cpp
class Solution {
public:
    int countPrimes(int n) {
        if (n <= 2) return 0;

        vector<bool> prime(n, true);
        prime[0] = prime[1] = false;

        for (long long i = 2; i * i < n; i++) {
            if (prime[i]) {
                for (long long j = i * i; j < n; j += i) {
                    prime[j] = false;
                }
            }
        }

        int count = 0;
        for (bool x : prime) count += x;
        return count;
    }
};
```

---

### F4. Happy Number

**Problem:** [LeetCode 202 - Happy Number](https://leetcode.com/problems/happy-number/)

**Observation:** Repeated digit-square sum either reaches `1` or enters a cycle.

**Form:** Cycle detection using set / Floyd.

```cpp
class Solution {
public:
    int nextNumber(int n) {
        int sum = 0;
        while (n > 0) {
            int d = n % 10;
            sum += d * d;
            n /= 10;
        }
        return sum;
    }

    bool isHappy(int n) {
        unordered_set<int> seen;

        while (n != 1 && !seen.count(n)) {
            seen.insert(n);
            n = nextNumber(n);
        }

        return n == 1;
    }
};
```

---

### F5. Excel Column Number

**Problem:** [LeetCode 171 - Excel Sheet Column Number](https://leetcode.com/problems/excel-sheet-column-number/)

**Observation:** This is base-26, but digits are `A=1` to `Z=26`, not `0` to `25`.

**Form:** Custom base conversion.

```cpp
class Solution {
public:
    int titleToNumber(string columnTitle) {
        long long ans = 0;

        for (char c : columnTitle) {
            int value = c - 'A' + 1;
            ans = ans * 26 + value;
        }

        return (int)ans;
    }
};
```

---

## 8. Final Revision Cheatsheet

| Signal in Problem | Pattern |
|---|---|
| Even/odd split | Parity + lower bound |
| Huge power | Binary exponentiation |
| Last digit / repeated remainder | Modulo cycle |
| Common divisor / synchronization | GCD / LCM |
| Exactly 3 divisors | Square of prime |
| Number of divisors | Prime exponent formula |
| Count paths with fixed moves | nCr |
| Identical items distributed | Stars and bars |
| Count numbers divisible by any of many values | Inclusion-exclusion |
| Valid brackets | Catalan |
| No fixed point permutation | Derangement |
| `ax + by = c` | Diophantine / Extended Euclid |
| Multiple remainder constraints | CRT |
| `floor(n / i)` repeated | Quotient grouping / floor sum |
| Prefix sum divisible by k | Prefix modulo frequency |
| OR in counting | Inclusion-exclusion |
| Need divide under modulo | Modular inverse |
| Large many prime queries | Sieve / SPF |

---

## Suggested Practice Order

### First 10 Problems

1. Watermelon
2. Calculating Function
3. Bachgold Problem
4. Arpa Hard Exam
5. T-primes
6. Count Primes
7. Pow(x,n)
8. Subarray Sums Divisible by K
9. Unique Paths
10. Distributing Apples

### Next 20 Problems

Add:

- CSES Exponentiation
- CSES Counting Divisors
- CSES Common Divisors
- CSES Sum of Divisors
- CSES Binomial Coefficients
- CSES Creating Strings II
- CSES Christmas Party
- CSES Bracket Sequences I
- CSES Prime Multiples
- Codeforces 7C Line

---

## Final Rule

Do not memorize problems. Memorize **forms**.

A math problem becomes easy when you can say:

```text
This is not random.
This is parity / gcd / modulo / nCr / factorization / inclusion-exclusion / floor-sum form.
```
