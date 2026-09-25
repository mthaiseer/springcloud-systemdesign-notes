# CF 1353A --- Most Unstable Array

**Problem:** https://codeforces.com/problemset/problem/1353/A\
**Pattern:** Constructive → Maximize Adjacent Absolute Differences →
Casework

## 1. Problem Asking

Construct an array of length `n`:

``` text
a1 + a2 + ... + an = m
a[i] >= 0
```

Maximize:

``` text
|a1-a2| + |a2-a3| + ... + |a[n-1]-a[n]|
```

## 2. Story → Variables

``` text
Array length          → n
Total available sum   → m
Array values          → a[i]
Required value        → sum of adjacent absolute differences
Goal                  → maximize it
```

Reduced problem:

``` text
sum(a) = m
a[i] >= 0

maximize Σ|a[i]-a[i+1]|
```

## 3. Mathematical Model

To maximize an absolute difference, put large value next to `0`.

``` text
|0-m| = m
```

A value placed between zeros can contribute twice:

``` text
0 → m → 0

|0-m| + |m-0|
= m + m
= 2m
```

So the answer depends on how many adjacent gaps exist.

## 4. Derivation

### n = 1

No adjacent pair:

``` text
answer = 0
```

### n = 2

Only one gap:

``` text
[0, m]

|0-m| = m

answer = m
```

### n \>= 3

We can create two maximum transitions:

``` text
[0, m, 0, ...]

|0-m| + |m-0|
= 2m
```

Therefore:

``` text
n = 1  → 0
n = 2  → m
n >= 3 → 2m
```

Compact:

``` text
answer = min(2, n-1) * m
```

## 5. Dry Run

``` text
n = 5, m = 5

choose:
[0, 2, 0, 3, 0]

value:
|0-2| + |2-0| + |0-3| + |3-0|
= 2 + 2 + 3 + 3
= 10
= 2m
```

## 6. Observation / Recognition

``` text
maximize adjacent |difference|
→ create low ↔ high transitions

fixed total sum = m
→ each amount can contribute at most twice

number of usable transitions:
n=1 → 0
n=2 → 1
n>=3 → 2

answer = min(2,n-1) * m
```

## 7. C++ Code

``` cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;

    while (t--) {
        long long n, m;
        cin >> n >> m;

        cout << min(2LL, n - 1) * m << '\n';
    }

    return 0;
}
```
