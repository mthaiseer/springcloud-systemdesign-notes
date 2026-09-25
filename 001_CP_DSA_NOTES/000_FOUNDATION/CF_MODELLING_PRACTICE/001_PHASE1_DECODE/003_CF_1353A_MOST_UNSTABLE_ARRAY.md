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

## 2. Story → Variables + Small Test Cases

``` text
Array length        → n
Total sum           → m
Array values        → a[i]
Score               → Σ|a[i]-a[i+1]|
Goal                → maximize score
```

Fix `m = 10`, then increase `n` from small to large.

### n = 1

``` text
[10]

score = 0

n=1 → 0
```

### n = 2

Try several valid arrays whose sum is `10`:

``` text
[5,5]  → |5-5|  = 0
[4,6]  → |4-6|  = 2
[2,8]  → |2-8|  = 6
[1,9]  → |1-9|  = 8
[0,10] → |0-10| = 10  ← best
```

Observation:

``` text
maximize |x-y|
→ make x and y far apart
→ try extremes: 0 and m
```

Therefore:

``` text
n=2 → m
```

### n = 3

Use the observation from `n=2`:

``` text
[0,10,0]

|0-10| + |10-0|
= 10 + 10
= 20
= 2m
```

Therefore:

``` text
n=3 → 2m
```

### n = 4

Can another position make it `3m`?

``` text
[0,10,0,0]

= 10 + 10 + 0
= 20
= 2m
```

Try splitting `m`:

``` text
[0,5,0,5]

= 5 + 5 + 5
= 15
```

So:

``` text
n=4 → 2m
```

### n = 5

``` text
[0,10,0,0,0]

= 10 + 10 + 0 + 0
= 20
= 2m
```

Or:

``` text
[0,4,0,6,0]

= 4 + 4 + 6 + 6
= 20
= 2(4+6)
= 2m
```

So:

``` text
n=5 → 2m
```

Small-case pattern:

``` text
n=1 → 0
n=2 → m
n=3 → 2m
n=4 → 2m
n=5 → 2m
...
```

## 3. Mathematical Model

Factor out `m`:

``` text
n      answer      multiplier
1        0             0
2        m             1
3       2m             2
4       2m             2
5       2m             2
```

Multiplier pattern:

``` text
0, 1, 2, 2, 2, ...
```

An array of length `n` has:

``` text
n - 1
```

adjacent gaps.

But the total amount `m` can contribute at most twice:

``` text
0 → m → 0

|0-m| + |m-0|
= m + m
= 2m
```

Therefore:

``` text
multiplier = min(2,n-1)
```

## 4. Derivation

From small cases:

``` text
n=1 → 0m
n=2 → 1m
n=3 → 2m
n=4 → 2m
n=5 → 2m
```

Compare:

``` text
n     n-1     min(2,n-1)
1      0           0
2      1           1
3      2           2
4      3           2
5      4           2
```

Hence:

``` text
answer = min(2,n-1) * m
```

Equivalent:

``` text
n=1  → 0
n=2  → m
n>=3 → 2m
```

## 5. Dry Run

``` text
n = 4
m = 10

small-case pattern:
n=1 → 0
n=2 → 10
n=3 → 20
n=4 → 20
```

Construction:

``` text
[0,10,0,0]

score
= |0-10| + |10-0| + |0-0|
= 10 + 10 + 0
= 20
```

Formula:

``` text
min(2,n-1) * m
= min(2,3) * 10
= 2 * 10
= 20
```

## 6. Observation / Recognition

``` text
formula not obvious
        ↓
fix small m
        ↓
start n=1
        ↓
increase n: 2,3,4,5
        ↓
try extreme values
        ↓
record best result
        ↓
0,m,2m,2m,2m...
        ↓
factor out m
        ↓
0,1,2,2,2...
        ↓
min(2,n-1)
```

Recognition:

``` text
maximize |difference|
→ try LOW ↔ HIGH extremes

hard formula
→ small n → large n
→ detect sequence
→ derive formula
→ prove
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
