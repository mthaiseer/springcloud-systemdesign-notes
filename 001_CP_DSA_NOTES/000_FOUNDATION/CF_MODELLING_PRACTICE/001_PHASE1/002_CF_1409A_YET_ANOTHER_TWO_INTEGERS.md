# CF 1409A --- Yet Another Two Integers Problem

**Problem:** https://codeforces.com/problemset/problem/1409/A\
**Pattern:** Absolute Difference → Minimum Operations → Ceil Division

## 1. Problem Asking

Given two integers `a` and `b`.

In one move, choose an integer `k` from `1` to `10` and either:

``` text
a = a + k
```

or:

``` text
a = a - k
```

**Goal:** Find the minimum number of moves needed to make `a = b`.

## 2. Story → Variables

``` text
Initial value       → start = a
Required value      → target = b
Add/subtract 1..10  → maxMove = 10
Number of moves     → moves
```

Since `a` can be smaller or larger than `b`, only the distance matters:

``` text
distance = |a - b|
```

Reduced problem:

``` text
distance = |a-b|
maxMove = 10
find minimum moves
```

## 3. Mathematical Model

One move can reduce the distance by at most `10`.

``` text
1 move     → at most 10
2 moves    → at most 20
moves      → at most 10 * moves
```

To cover the whole difference:

``` text
10 * moves >= |a-b|
```

## 4. Derivation

``` text
10 * moves >= |a-b|

moves >= |a-b| / 10
```

`moves` must be an integer:

``` text
moves = ceil(|a-b| / 10)
```

Integer form:

``` text
moves = (|a-b| + 9) / 10
```

## 5. Dry Run

``` text
a = 13
b = 42

distance = |13-42|
         = 29

10 * moves >= 29

moves >= 2.9

minimum integer moves = 3
```

Possible moves:

``` text
13 → 23 → 33 → 42
     +10   +10   +9

→ 3 moves
```

## 6. Observation / Recognition

``` text
two values
→ take absolute difference

minimum operations
+ maximum K change per operation
→ ceil(difference / K)
```

Recognition:

``` text
a may be above or below b
→ |a-b|

minimum moves with at most 10 per move
→ ceil(|a-b| / 10)
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
        long long a, b;
        cin >> a >> b;

        long long diff = abs(a - b);
        cout << (diff + 9) / 10 << '\n';
    }

    return 0;
}
```
