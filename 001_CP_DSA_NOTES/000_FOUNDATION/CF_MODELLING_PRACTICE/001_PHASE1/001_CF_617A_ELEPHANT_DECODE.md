# CF 617A --- Elephant

**Problem:** https://codeforces.com/problemset/problem/617/A\
**Pattern:** Minimum Operations → Ceil Division

## 1. Problem Asking

Elephant starts at position `0`. The house is at position `x`.

In one step, it can move `1`, `2`, `3`, `4`, or `5` positions.

**Goal:** Find the minimum number of steps needed to reach exactly `x`.

## 2. Story → Variables

``` text
Elephant starts at 0  → start = 0
House is at x         → target = x
Each step moves 1..5  → maxMove = 5
Minimum steps         → k = number of steps
```

Reduced problem:

``` text
start = 0
target = x
maxMove = 5
find minimum k
```

## 3. Mathematical Model

Maximum distance covered in `k` steps:

``` text
1 step  → at most 5
2 steps → at most 10
k steps → at most 5k
```

To have enough steps to reach `x`:

``` text
5k >= x
```

## 4. Derivation

``` text
5k >= x
k >= x / 5

k must be an integer
→ k = ceil(x / 5)
```

Integer form:

``` text
k = (x + 4) / 5
```

## 5. Dry Run

``` text
x = 12

5k >= 12
k >= 12 / 5
k >= 2.4

minimum integer k = 3

0 → 5 → 10 → 12
```

## 6. Observation / Recognition

``` text
minimum operations
+ maximum K per operation
→ ceil(required / K)
```

**Recognition:** When a problem asks for the minimum number of
operations and each operation can cover **at most `K`**, think **ceil
division**.

## 7. C++ Code

``` cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int x;
    cin >> x;

    cout << (x + 4) / 5 << '\n';

    return 0;
}
```
