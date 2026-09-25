# Codeforces 1458A — Row GCD
## Visual Learning Note

**Core pattern:** `GCD + same shift → take differences → common shift disappears`

---

# 1. Problem

Given:

```text
a1, a2, ... , an
```

and queries:

```text
b1, b2, ... , bm
```

for every query `b`, calculate:

```text
gcd(a1+b, a2+b, a3+b, ... , an+b)
```

Example:

```text
a = [1, 25, 121, 169]
b = [1, 2, 7, 23]
```

For `b = 1`:

```text
gcd(2, 26, 122, 170)
```

Doing this across all `n` elements for every query is too much repeated work.

---

# 2. GCD Property That Unlocks the Problem

The key property is:

```text
gcd(x,y) = gcd(x,y-x)
```

More generally:

```text
gcd(x,y) = gcd(x,y-k*x)
```

Example:

```text
gcd(12,18)
   =
gcd(12,18-12)
   =
gcd(12,6)
   =
6
```

## Why?

If `d` divides both `x` and `y`:

```text
x = d*p
y = d*q
```

then:

```text
y-x
= d*q - d*p
= d*(q-p)
```

so `d` also divides `y-x`.

ASCII:

```text
d divides x ──────┐
                  ├──→ d divides (y-x)
d divides y ──────┘
```

Therefore subtraction preserves the common divisors and hence the GCD.

---

# 3. Extend the Property to Many Numbers

```text
gcd(x1,x2,x3,...,xn)
```

can be transformed into:

```text
gcd(
    x1,
    x2-x1,
    x3-x1,
    ...,
    xn-x1
)
```

Think of `x1` as an anchor:

```text
x1 ← ANCHOR
│
├── x2-x1
├── x3-x1
├── x4-x1
└── xn-x1
```

---

# 4. Spot the Pattern in Row GCD

For one query:

```text
a1 + b
a2 + b
a3 + b
...
an + b
```

The important visual clue is:

```text
a1 + b
     ↑
a2 + b
     ↑
a3 + b
     ↑
an + b
     ↑
 SAME b everywhere
```

Ask:

> What happens if I subtract the first expression from the others?

Use `a1+b` as the anchor.

---

# 5. Main Transformation

Start:

```text
gcd(
    a1+b,
    a2+b,
    a3+b,
    ...,
    an+b
)
```

Apply the subtraction property:

```text
gcd(
    a1+b,
    (a2+b)-(a1+b),
    (a3+b)-(a1+b),
    ...,
    (an+b)-(a1+b)
)
```

Now:

```text
(a2+b)-(a1+b)

= a2 + b - a1 - b
          ↑        ↑
          └ cancel ┘

= a2-a1
```

Likewise:

```text
(a3+b)-(a1+b) = a3-a1
...
(an+b)-(a1+b) = an-a1
```

Therefore:

```text
gcd(
    a1+b,
    a2-a1,
    a3-a1,
    ...,
    an-a1
)
```

---

# 6. Before vs After

Before:

```text
a1+b ─── changes
a2+b ─── changes
a3+b ─── changes
...      changes
an+b ─── changes
```

After:

```text
a1+b    ───── changes with query

a2-a1 ──┐
a3-a1 ──┤
...     ─┼──── FIXED
an-a1 ──┘
```

Only one term still depends on `b`.

---

# 7. Precompute the Fixed Part

Define:

```text
G = gcd(
      |a2-a1|,
      |a3-a1|,
      ...,
      |an-a1|
    )
```

Then every query becomes:

```text
answer = gcd(a1+b, G)
```

Final formula:

```text
G = gcd of all differences from a1

For each b:
    answer = gcd(a1+b, G)
```

---

# 8. Full ASCII Mental Model

```text
gcd(a1+b, a2+b, a3+b, ..., an+b)
                    │
                    │ SAME +b
                    ▼
             TAKE DIFFERENCES
                    │
                    ▼
          use a1+b as anchor
                    │
                    ▼

(a2+b)-(a1+b) = a2-a1
(a3+b)-(a1+b) = a3-a1
...
(an+b)-(a1+b) = an-a1

                    │
                    │ b disappears
                    ▼

G = gcd(a2-a1, a3-a1, ..., an-a1)

                    │
                    │ precompute once
                    ▼

              each query b

                    │
                    ▼

          answer = gcd(a1+b, G)
```

---

# 9. Main Test Case — Dry Run

```text
a = [1, 25, 121, 169]
queries = [1, 2, 7, 23]
```

Anchor:

```text
a1 = 1
```

Differences:

```text
25  - 1 = 24
121 - 1 = 120
169 - 1 = 168
```

ASCII:

```text
1 --------24--------> 25

1 ----------------120----------------> 121

1 -----------------------168-----------------------> 169
↑
anchor
```

Now:

```text
G = gcd(24,120,168)
```

Step by step:

```text
gcd(24,120) = 24
gcd(24,168) = 24

G = 24
```

The original array has now been compressed, for the purpose of the queries, into:

```text
anchor = 1
G      = 24
```

## Query b = 1

```text
a1+b = 2

gcd(2,24) = 2
```

## Query b = 2

```text
a1+b = 3

gcd(3,24) = 3
```

## Query b = 7

```text
a1+b = 8

gcd(8,24) = 8
```

## Query b = 23

```text
a1+b = 24

gcd(24,24) = 24
```

Result:

```text
2 3 8 24
```

Dry-run table:

```text
b      a1+b      G      answer
--------------------------------
1        2       24        2
2        3       24        3
7        8       24        8
23      24       24       24
```

---

# 10. Verify With the Original Formula

For `b = 7`:

```text
a = [1,25,121,169]
```

Add 7:

```text
[8,32,128,176]
```

Original calculation:

```text
gcd(8,32,128,176) = 8
```

Optimized:

```text
G = 24

gcd(a1+b,G)
=
gcd(1+7,24)
=
gcd(8,24)
=
8
```

Same answer.

---

# 11. Additional Test Case

```text
a = [10,20,30]
queries = [0,5,10]
```

Differences:

```text
20-10 = 10
30-10 = 20

G = gcd(10,20) = 10
```

Queries:

```text
b=0:
gcd(10,10) = 10

b=5:
gcd(15,10) = 5

b=10:
gcd(20,10) = 10
```

Output:

```text
10 5 10
```

---

# 12. Edge Case — n = 1

```text
a = [15]
queries = [1,5,10]
```

There are no differences, so initialize:

```text
G = 0
```

Use:

```text
gcd(x,0) = |x|
```

Therefore:

```text
b=1  → gcd(16,0) = 16
b=5  → gcd(20,0) = 20
b=10 → gcd(25,0) = 25
```

The same implementation works.

---

# 13. GCD Properties to Remember

### Commutative

```text
gcd(a,b) = gcd(b,a)
```

### GCD with zero

```text
gcd(a,0) = |a|
```

### Subtraction

```text
gcd(a,b) = gcd(a,b-a)
```

### Subtract a multiple

```text
gcd(a,b) = gcd(a,b-k*a)
```

### Useful CP form

```text
gcd(a,a+x) = gcd(a,x)
```

because:

```text
gcd(a,a+x)
=
gcd(a,(a+x)-a)
=
gcd(a,x)
```

### Many-number difference form

```text
gcd(x1,x2,...,xn)

=

gcd(
    x1,
    x2-x1,
    x3-x1,
    ...,
    xn-x1
)
```

This last property is the exact form used in this problem.

---

# 14. Underlying Geometric Idea

Imagine the `a` values as points:

```text
a1              a2                       a3
●---------------●------------------------●
       gap                 gap
```

Now add the same `b` to every point:

```text
                       shift +b →

        a1+b              a2+b                       a3+b
          ●---------------●--------------------------●
                 same gaps
```

All points move together.

But their differences do not change:

```text
(a2+b)-(a1+b) = a2-a1
```

So:

```text
COMMON SHIFT changes absolute values
but
COMMON SHIFT does not change differences
```

That is the deeper idea behind the solution.

---

# 15. Brute Force vs Optimized

Brute force:

```text
b1 → modify all n numbers → GCD all n
b2 → modify all n numbers → GCD all n
b3 → modify all n numbers → GCD all n
...
```

Optimized:

```text
                 a[]
                  │
                  ▼
         differences from a1
                  │
                  ▼
          GCD differences
                  │
                  ▼
                  G
             PRECOMPUTE
                  │
       ┌──────────┼──────────┐
       ▼          ▼          ▼
      b1         b2         b3
       │          │          │
       ▼          ▼          ▼
gcd(a1+b1,G) gcd(a1+b2,G) gcd(a1+b3,G)
```

---

# 16. C++ Code

```cpp
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

    // GCD of all differences from a[0]
    ll G = 0;

    for (int i = 1; i < n; i++) {
        G = gcd(G, abs(a[i] - a[0]));
    }

    // Answer each query
    for (int i = 0; i < m; i++) {

        ll b;
        cin >> b;

        cout << gcd(a[0] + b, G) << " ";
    }

    cout << '\n';

    return 0;
}
```

---

# 17. Code ↔ Mathematics

Math:

```text
G = gcd(
      |a2-a1|,
      |a3-a1|,
      ...,
      |an-a1|
    )
```

Code:

```cpp
ll G = 0;

for (int i = 1; i < n; i++) {
    G = gcd(G, abs(a[i] - a[0]));
}
```

Math:

```text
answer = gcd(a1+b, G)
```

Code:

```cpp
cout << gcd(a[0] + b, G);
```

---

# 18. Complexity

Precomputation:

```text
O(n log MAX)
```

Queries:

```text
O(m log MAX)
```

Total:

```text
O((n+m) log MAX)
```

---

# 19. Recognition Trigger

When you see:

```text
gcd(
    a1 + x,
    a2 + x,
    a3 + x,
    ...
)
```

think:

```text
       GCD
        +
 SAME SHIFT x
        │
        ▼
 TAKE DIFFERENCES
        │
        ▼
Does x cancel?
        │
       YES
        ▼
Precompute fixed differences
```

Ask yourself:

> What happens if I subtract the first expression from all the others?

---

# 20. 10-Second Revision Diagram

```text
gcd(a1+b, a2+b, a3+b, ...)
              ↓
          SAME +b
              ↓
      subtract first term
              ↓
gcd(a1+b, a2-a1, a3-a1, ...)
              ↓
G = gcd(a2-a1, a3-a1, ...)
              ↓
answer = gcd(a1+b, G)
```

---

# 21. One-Line Pattern

> **Same shift + GCD → take differences → common shift disappears → precompute GCD of differences.**

---

# 22. One-Line Proof

> Any divisor that divides both `x` and `y` also divides `y-x`, so replacing `y` by `y-x` does not change the GCD.

---

# 23. Active Recall Questions

Before reading the solution again, answer these:

```text
1. What is the brute-force approach and why is it slow?

2. Which GCD property allows subtraction?

3. Why does:
      (ai+b)-(a1+b)
   become:
      ai-a1
   ?

4. Which part is independent of b?

5. What exactly do we precompute?

6. Why does G start from 0?

7. What happens for n=1?

8. What pattern should make me think:
      "take differences"?

9. Can I derive:
      answer = gcd(a1+b,G)
   without looking at the code?
```

If you can reconstruct the transformation and code from these questions, the pattern is learned.
