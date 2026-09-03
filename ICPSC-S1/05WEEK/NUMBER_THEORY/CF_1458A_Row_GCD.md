# Codeforces 1458A --- Row GCD

## Core Pattern

> **Same shift + GCD → take differences → the common shift disappears.**

## Problem

For every query `b`, calculate:

``` text
gcd(a1+b, a2+b, a3+b, ..., an+b)
```

The key detail: the **same `b` is added to every element**.

## Test Case

``` text
a = [1, 25, 121, 169]
b = [1, 2, 7, 23]

Output:
2 3 8 24
```

## Why Brute Force Is Bad

``` text
for every b:
    calculate gcd(a1+b, a2+b, ..., an+b)
```

This repeats work for every query. We want to separate the
query-dependent part from the fixed part.

## GCD Property

The property that unlocks the problem:

``` text
gcd(x,y) = gcd(x,y-x)
```

Example:

``` text
gcd(12,18)
= gcd(12,18-12)
= gcd(12,6)
= 6
```

Why?

``` text
d divides x
d divides y
     ↓
d also divides y-x
```

For many numbers:

``` text
gcd(x1,x2,x3,...,xn)
=
gcd(x1, x2-x1, x3-x1, ..., xn-x1)
```

## Apply It

Start:

``` text
gcd(
    a1+b,
    a2+b,
    a3+b,
    ...,
    an+b
)
```

Use `a1+b` as the anchor:

``` text
(a2+b) - (a1+b) = a2-a1
(a3+b) - (a1+b) = a3-a1
...
(an+b) - (a1+b) = an-a1
```

The `b` disappears.

## Main ASCII Idea

``` text
BEFORE

a1+b ───┐
a2+b ───┤
a3+b ───┼── all depend on b
...     │
an+b ───┘


       subtract (a1+b)
              ↓


AFTER

a1+b ─────────── changes with query

a2-a1 ──┐
a3-a1 ──┤
...     ├──────── fixed forever
an-a1 ──┘
```

Therefore:

``` text
gcd(a1+b, a2+b, ..., an+b)
=
gcd(a1+b, a2-a1, a3-a1, ..., an-a1)
```

## Precompute the Fixed Part

``` text
G = gcd(
        |a2-a1|,
        |a3-a1|,
        ...,
        |an-a1|
    )
```

Then every query becomes:

``` text
answer = gcd(a1+b, G)
```

## Full Mental Model

``` text
gcd(a1+b, a2+b, a3+b, ..., an+b)
                    │
             SAME +b everywhere
                    │
                    ▼
             TAKE DIFFERENCES
                    │
                    ▼
          use (a1+b) as anchor
                    │
                    ▼

(a2+b)-(a1+b) = a2-a1
(a3+b)-(a1+b) = a3-a1
             ...
(an+b)-(a1+b) = an-a1

                    │
                b CANCELS
                    │
                    ▼

gcd(a1+b, a2-a1, a3-a1, ..., an-a1)

                    │
                    ▼
       PRECOMPUTE FIXED PART

G = gcd(|a2-a1|, |a3-a1|, ..., |an-a1|)

                    │
                    ▼
            FOR EACH QUERY

        answer = gcd(a1+b, G)
```

## Detailed Dry Run

``` text
a = [1, 25, 121, 169]
b = [1, 2, 7, 23]
```

Anchor:

``` text
a1 = 1
```

Differences:

``` text
25  - 1 = 24
121 - 1 = 120
169 - 1 = 168
```

ASCII:

``` text
1 --------24--------> 25
1 ----------------120----------------> 121
1 -----------------------168-----------------------> 169
```

Calculate:

``` text
G = gcd(24,120,168)

gcd(24,120) = 24
gcd(24,168) = 24

G = 24
```

Now the fixed information is compressed into:

``` text
a1 = 1
G  = 24
```

### Query b = 1

``` text
a1+b = 1+1 = 2
answer = gcd(2,24) = 2
```

### Query b = 2

``` text
a1+b = 1+2 = 3
answer = gcd(3,24) = 3
```

### Query b = 7

``` text
a1+b = 1+7 = 8
answer = gcd(8,24) = 8
```

### Query b = 23

``` text
a1+b = 1+23 = 24
answer = gcd(24,24) = 24
```

Summary:

``` text
b       a1+b       gcd(a1+b,G)       answer
------------------------------------------------
1         2         gcd(2,24)           2
2         3         gcd(3,24)           3
7         8         gcd(8,24)           8
23       24         gcd(24,24)         24
```

Output:

``` text
2 3 8 24
```

## Second Test Case

``` text
a = [6, 10, 14]
b = [2, 5]
```

Differences:

``` text
10-6 = 4
14-6 = 8

G = gcd(4,8) = 4
```

Query `b=2`:

``` text
answer = gcd(6+2,4)
       = gcd(8,4)
       = 4
```

Direct check:

``` text
gcd(8,12,16) = 4
```

Query `b=5`:

``` text
answer = gcd(6+5,4)
       = gcd(11,4)
       = 1
```

Direct check:

``` text
gcd(11,15,19) = 1
```

Output:

``` text
4 1
```

## Edge Case --- n = 1

If:

``` text
a = [10]
b = [1,5]
```

There are no differences, so:

``` text
G = 0
```

Using:

``` text
gcd(x,0) = |x|
```

we get:

``` text
b=1 → gcd(11,0) = 11
b=5 → gcd(15,0) = 15
```

The same algorithm works.

## C++ Code

``` cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<ll> a(n);

    for (ll &x : a) {
        cin >> x;
    }

    ll G = 0;

    for (int i = 1; i < n; i++) {
        G = gcd(G, abs(a[i] - a[0]));
    }

    for (int i = 0; i < m; i++) {
        ll b;
        cin >> b;

        cout << gcd(a[0] + b, G) << " ";
    }

    cout << '\n';
}
```

## Complexity

``` text
Precompute G: O(n log MAX)
Queries:      O(m log MAX)

Total: O((n+m) log MAX)
```

## GCD Properties to Remember

``` text
gcd(a,b) = gcd(b,a)
gcd(a,0) = |a|
gcd(a,b) = gcd(a,b-a)
gcd(a,b) = gcd(a,b-ka)
gcd(a,a+x) = gcd(a,x)
```

Most important here:

``` text
gcd(x1,x2,...,xn)
=
gcd(x1, x2-x1, x3-x1, ..., xn-x1)
```

## Pattern Recognition Trigger

Whenever you see:

``` text
gcd(
    a1 + SAME_VALUE,
    a2 + SAME_VALUE,
    a3 + SAME_VALUE,
    ...
)
```

ask:

``` text
What happens if I subtract one expression from another?
```

Because:

``` text
(a2+b) - (a1+b)
        ↓
a2+b-a1-b
        ↓
a2-a1
        ↓
b disappeared
```

Lock this:

``` text
 SAME SHIFT
     +
    GCD
     ↓
DIFFERENCES
     ↓
SHIFT CANCELS
```

## One-Line Revision

> **Same shift + GCD → take differences → common shift disappears →
> precompute GCD of differences.**

## 10-Second Recall Diagram

``` text
gcd(a1+b, a2+b, a3+b, ...)
              ↓
       subtract (a1+b)
              ↓
gcd(a1+b, a2-a1, a3-a1, ...)
              ↓
 G = gcd(all fixed differences)
              ↓
 answer = gcd(a1+b, G)
```

If you can reconstruct this diagram, you remember the solution.
