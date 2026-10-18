# Bit Manipulation - Problem Solving 1
## Detailed bit-by-bit dry runs + C++17

Source: **TLE - Bit Manipulation Beginner, Level 2 - Problem Solving 1 (Harsh Gupta)**

This note follows the three problems shown in the lecture PDF. The purpose of every section is:

```text
UNDERSTAND THE PROBLEM
        ↓
FIND THE BIT OBSERVATION
        ↓
TRACE A SMALL EXAMPLE BIT BY BIT
        ↓
CONVERT THE SAME STEPS INTO CODE
```

---

## Table of Contents

1. [Problem 1 - Bitwise Equation (CodeChef BITEQU)](#problem-1---bitwise-equation-codechef-bitequ)
2. [Problem 2 - Range XOR Queries](#problem-2---range-xor-queries)
3. [Problem 3 - Fortune Telling (Codeforces 1634B)](#problem-3---fortune-telling-codeforces-1634b)

---

# Problem 1 - Bitwise Equation (CodeChef BITEQU)

**Problem link:** https://www.codechef.com/problems/BITEQU

## What the problem wants

For a given `N`, construct **four distinct positive integers**

```text
a, b, c, d
```

such that

```text
((a & b) | c) ^ d = N
```

The important word is **construct**.

We are not asked to search for the smallest values.  
We are free to choose convenient values that make the equation easy.

---

## Step 1 - Look at the equation from inside to outside

```text
((a & b) | c) ^ d = N
  └───┘
    ↓
 first

(a & b)
    ↓
OR with c
    ↓
XOR with d
    ↓
N
```

A good constructive strategy is:

```text
make (a & b) easy
        ↓
make ((a & b) | c) a known constant
        ↓
solve known_constant ^ d = N
```

---

## Step 2 - Force `a & b = 0`

Choose `a` and `b` with different high bits.

For example:

```text
a = 1LL << 37
b = 1LL << 36
```

Zoom in on those high bits:

```text
bit position       37 36 35 ... 2 1 0

a                   1  0  0 ... 0 0 0
b                   0  1  0 ... 0 0 0
                    &
                    ------------------
a & b               0  0  0 ... 0 0 0
```

Therefore:

```text
a & b = 0
```

The original equation becomes

```text
(0 | c) ^ d = N

c ^ d = N
```

Now the problem is much simpler.

---

## Step 3 - Choose a high bit for `c`

The lecture uses the idea of putting a bit above all bits of `N`.

Since

```text
N < 2^32
```

bits `32` and above are not present in `N`.

Choose:

```text
c = 1LL << 35
```

So `c` looks like:

```text
bit:   35 34 33 32 31 ........ 1 0
c       1  0  0  0  0 ........ 0 0
N       0  0  0  0  ? ........ ? ?
```

There is no overlap between the high bit of `c` and the bits of `N`.

For `N > 0`, choose:

```text
d = c | N
```

Because their set bits do not overlap:

```text
d = c + N
```

and also

```text
c ^ d = N
```

---

## Detailed bit-by-bit dry run

Take:

```text
N = 10
```

Binary:

```text
N = 1010
```

For readability, show only the relevant high marker `H = 2^35` and the low four bits:

```text
             H | low bits
             --+---------
c             1 | 0000
N             0 | 1010
```

Construct `d`:

```text
c             1 | 0000
N             0 | 1010
              |
              ----------
d             1 | 1010
```

Now evaluate the full expression.

### STEP 1 - `a & b`

```text
a has only bit 37 set
b has only bit 36 set

bit            37 36 35
a               1  0  0
b               0  1  0
                &
                --------
a & b           0  0  0

a & b = 0
```

### STEP 2 - `(a & b) | c`

```text
a & b           0 | 0000
c               1 | 0000
                |
                --------
result          1 | 0000
```

So:

```text
(a & b) | c = c
```

### STEP 3 - `c ^ d`

```text
c               1 | 0000
d               1 | 1010
                ^
                --------
result          0 | 1010
```

The high bit cancels:

```text
1 ^ 1 = 0
```

The low bits remain:

```text
0000 ^ 1010 = 1010
```

Therefore:

```text
((a & b) | c) ^ d
= 1010
= 10
= N
```

### Visual flow

```text
a = bit37 only ─┐
                ├─ AND ─→ 0 ─┐
b = bit36 only ─┘             │
                              ├─ OR ─→ c ─┐
c = bit35 only ───────────────┘           │
                                          ├─ XOR ─→ N
d = c | N ────────────────────────────────┘
```

---

## What about `N = 0`?

If we used

```text
d = c | N
```

then for `N = 0`:

```text
d = c
```

but the problem requires all four numbers to be distinct.

Use a small special construction:

```text
a = 17
b = 1
c = 2
d = 3
```

Check:

```text
17 = 10001
 1 = 00001
     &
     -----
     00001 = 1

1 | 2:

1 = 001
2 = 010
    |
    ---
    011 = 3

3 ^ 3:

3 = 011
3 = 011
    ^
    ---
    000 = 0
```

So the answer is valid for `N = 0`.

---

## From dry run to code

The dry run gave us the construction directly:

```text
if N == 0
    use a known special answer
else
    a = 2^37
    b = 2^36
    c = 2^35
    d = c | N
```

There is no search.

### Complexity

```text
Time:  O(1) per test case
Space: O(1)
```

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using int64 = long long;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int T;
    cin >> T;

    while (T--) {
        int64 n;
        cin >> n;

        if (n == 0) {
            cout << 17 << ' ' << 1 << ' ' << 2 << ' ' << 3 << '\n';
            continue;
        }

        int64 a = (1LL << 37);
        int64 b = (1LL << 36);
        int64 c = (1LL << 35);
        int64 d = c | n;

        cout << a << ' ' << b << ' ' << c << ' ' << d << '\n';
    }

    return 0;
}
```

---

# Problem 2 - Range XOR Queries

**Problem link:** The lecture PDF gives the exercise directly but does **not provide an external problem link**.

## What the problem wants

We have an array:

```text
A[0], A[1], ..., A[N-1]
```

For many queries `[L, R]`, calculate:

```text
A[L] ^ A[L+1] ^ ... ^ A[R]
```

If both `N` and `Q` are around `10^5`, recomputing the XOR for every query can be too slow.

---

## Brute force first

Suppose:

```text
A = [5, 2, 7, 3, 6]
```

Query:

```text
L = 1
R = 3
```

Brute force:

```text
A[1] ^ A[2] ^ A[3]

= 2 ^ 7 ^ 3
```

Binary:

```text
2 = 010
7 = 111
3 = 011
```

First:

```text
  010
^ 111
-----
  101
```

Then:

```text
  101
^ 011
-----
  110
```

Answer:

```text
110₂ = 6
```

This is fine for one query, but doing up to `O(N)` work for each of `Q` queries gives:

```text
O(N * Q)
```

---

## Key XOR property

The important identity is:

```text
x ^ x = 0
```

and

```text
x ^ 0 = x
```

So if the same prefix appears twice, it disappears.

This suggests **prefix XOR**.

---

## Build prefix XOR

Define:

```text
pref[0] = 0

pref[i + 1] = pref[i] ^ A[i]
```

For:

```text
A = [5, 2, 7, 3, 6]
```

Binary values:

```text
A[0] = 5 = 101
A[1] = 2 = 010
A[2] = 7 = 111
A[3] = 3 = 011
A[4] = 6 = 110
```

Now build it one element at a time.

### STEP 1

```text
pref[0] = 000
```

### STEP 2 - include `A[0]`

```text
pref[0] = 000
A[0]    = 101
           ^
           ---
pref[1] = 101
```

### STEP 3 - include `A[1]`

```text
pref[1] = 101
A[1]    = 010
           ^
           ---
pref[2] = 111
```

### STEP 4 - include `A[2]`

```text
pref[2] = 111
A[2]    = 111
           ^
           ---
pref[3] = 000
```

The equal values cancel:

```text
111 ^ 111 = 000
```

### STEP 5 - include `A[3]`

```text
pref[3] = 000
A[3]    = 011
           ^
           ---
pref[4] = 011
```

### STEP 6 - include `A[4]`

```text
pref[4] = 011
A[4]    = 110
           ^
           ---
pref[5] = 101
```

Final prefix array:

```text
index       0    1    2    3    4    5
            --------------------------------
pref        000  101  111  000  011  101
```

---

## Detailed query dry run

Again ask:

```text
L = 1
R = 3
```

We want:

```text
A[1] ^ A[2] ^ A[3]
```

Using prefix XOR:

```text
answer = pref[R + 1] ^ pref[L]

       = pref[4] ^ pref[1]
```

From above:

```text
pref[4] = 011
pref[1] = 101
```

Bit by bit:

```text
pref[4] = 011
pref[1] = 101
           ^
           ---
answer  = 110 = 6
```

Exactly the same answer as brute force.

---

## Why does the formula work?

Expand both prefixes:

```text
pref[R+1]
= A[0] ^ A[1] ^ ... ^ A[L-1] ^ A[L] ^ ... ^ A[R]

pref[L]
= A[0] ^ A[1] ^ ... ^ A[L-1]
```

XOR them:

```text
pref[R+1] ^ pref[L]
```

Visual cancellation:

```text
(A0 ^ A1 ^ ... ^ A[L-1] ^ A[L] ^ ... ^ A[R])
 ^
(A0 ^ A1 ^ ... ^ A[L-1])

 A0 cancels
 A1 cancels
 ...
 A[L-1] cancels
          ↓
A[L] ^ A[L+1] ^ ... ^ A[R]
```

That is exactly the query.

---

## Real-world mental model

Think of `pref[i]` as a **cumulative XOR checksum**.

```text
start
  |
  v
[ A0 ][ A1 ][ A2 ][ A3 ][ A4 ]
  |     |     |     |     |
  v     v     v     v     v
 p1    p2    p3    p4    p5
```

To isolate a middle interval, XOR away the checksum before the interval.

```text
whole prefix through R
        XOR
prefix before L
        ↓
only [L..R] survives
```

---

## From dry run to code

We need only two stages:

```text
PREPROCESS

pref[0] = 0
for each i:
    pref[i+1] = pref[i] ^ A[i]


QUERY [L,R]

answer = pref[R+1] ^ pref[L]
```

### Complexity

```text
Build prefix: O(N)
Each query:   O(1)
Total:        O(N + Q)
Space:        O(N)
```

## C++17

> The PDF states the query operation but does not specify a complete input format.  
> This implementation assumes: `N Q`, then `N` array elements, followed by `Q` zero-based `L R` queries.

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, q;
    cin >> n >> q;

    vector<long long> a(n);
    vector<long long> pref(n + 1, 0);

    for (int i = 0; i < n; ++i) {
        cin >> a[i];
        pref[i + 1] = pref[i] ^ a[i];
    }

    while (q--) {
        int l, r;
        cin >> l >> r;

        long long answer = pref[r + 1] ^ pref[l];
        cout << answer << '\n';
    }

    return 0;
}
```

---

# Problem 3 - Fortune Telling (Codeforces 1634B)

**Problem link:** https://codeforces.com/problemset/problem/1634/B

## What the problem wants

Alice starts with:

```text
d = x
```

Bob starts with:

```text
d = x + 3
```

For every `a[i]`, each person independently chooses one operation:

```text
d = d + a[i]
```

or

```text
d = d ^ a[i]
```

At the end, one of them reaches `y`.

We must determine whether it was:

```text
Alice
```

or

```text
Bob
```

---

## First instinct - simulate all possibilities?

For every array element there are two choices:

```text
        current d
        /       \
      +a         ^a
     /             \
 next              next
```

After `n` elements:

```text
2^n possible operation sequences
```

That is impossible for large `n`.

So we should ask:

> Do `+ a[i]` and `^ a[i]` share some property?

Yes: **they have exactly the same effect on parity**.

---

## Focus only on bit 0

Parity is determined only by the least significant bit:

```text
even → bit0 = 0
odd  → bit0 = 1
```

For the lowest bit, addition behaves like XOR because there is **no carry entering bit 0**.

Truth table:

```text
d0   a0   (d+a) bit0   d0^a0
--------------------------------
0    0         0          0
0    1         1          1
1    0         1          1
1    1         0          0
```

Therefore:

```text
parity(d + a) = parity(d ^ a)
```

The choice between `+` and `^` does not matter for parity.

---

## Bit-by-bit example: odd `a`

Take:

```text
d = 3
a = 5
```

Binary:

```text
d = 011
a = 101
```

### Addition

```text
d       = 011
a       = 101
          +
          ---
result  = 1000
             ↑
           bit0 = 0 → EVEN
```

### XOR

```text
d       = 011
a       = 101
          ^
          ---
result  = 110
            ↑
          bit0 = 0 → EVEN
```

The full numbers differ:

```text
3 + 5 = 8
3 ^ 5 = 6
```

but both are even.

Why?

```text
odd operation value
        ↓
toggles parity
```

---

## Bit-by-bit example: even `a`

Take:

```text
d = 3
a = 4
```

### Addition

```text
d       = 011
a       = 100
          +
          ---
result  = 111
            ↑
          bit0 = 1 → ODD
```

### XOR

```text
d       = 011
a       = 100
          ^
          ---
result  = 111
            ↑
          bit0 = 1 → ODD
```

An even `a` does not change parity.

So:

```text
a[i] odd  → parity toggles
a[i] even → parity stays
```

regardless of whether we use `+` or `^`.

---

## What happens after the entire array?

Let:

```text
S = a[0] + a[1] + ... + a[n-1]
```

We do **not** claim that the final value is `x + S`.

We only claim that its **parity** is the same as `x + S`.

Why?

Every odd `a[i]` toggles parity once.  
Every even `a[i]` leaves parity unchanged.

That is exactly what ordinary addition of the whole sum does to parity.

Therefore Alice's final parity is:

```text
(x + S) & 1
```

---

## Why Alice and Bob can never have the same final parity

Alice starts at:

```text
x
```

Bob starts at:

```text
x + 3
```

Since `3` is odd:

```text
x and x+3 have opposite parity
```

Example:

```text
x = 6      → even
x + 3 = 9  → odd
```

Both then process the same array.

Each odd `a[i]` toggles both parities.  
Each even `a[i]` keeps both parities.

So they remain opposite forever.

```text
START

Alice parity = P
Bob parity   = opposite(P)

       │ process a1
       ▼

still opposite

       │ process a2
       ▼

still opposite

       │ ...
       ▼

FINAL

still opposite
```

Thus the parity of `y` uniquely tells us which person could have reached it.

---

## Detailed official-sample dry run

Take the first Codeforces sample:

```text
n = 1
x = 7
y = 9

a = [2]
```

### STEP 1 - Starting parity

Alice:

```text
x = 7

7 = 111
        ↑
      bit0 = 1 → ODD
```

Bob:

```text
x + 3 = 10

10 = 1010
          ↑
        bit0 = 0 → EVEN
```

They start with opposite parity.

### STEP 2 - Process `a[0] = 2`

```text
2 = 10
       ↑
     bit0 = 0 → EVEN
```

An even number does not change parity.

Alice remains odd.

Check both possible operations:

```text
Alice d = 7

Addition:
7 + 2 = 9

  0111
+ 0010
------
  1001
     ↑
     1 → odd


XOR:
7 ^ 2 = 5

  0111
^ 0010
------
  0101
     ↑
     1 → odd
```

Different values:

```text
9 != 5
```

Same parity:

```text
odd = odd
```

### STEP 3 - Inspect target `y`

```text
y = 9 = 1001
             ↑
           bit0 = 1 → ODD
```

Alice's possible final values must be odd.  
Bob's possible final values must be even.

Therefore:

```text
ANSWER = Alice
```

---

## Even more compact parity trace

For the same example:

```text
Alice start:

x = 7
parity = 1

a[0] = 2
parity(a[0]) = 0

new parity:

1 XOR 0 = 1

target y = 9
parity(y) = 1

1 == 1
   ↓
Alice
```

---

## Algebra behind the observation

For any two integers:

```text
a + b = (a ^ b) + 2 * (a & b)
```

The second term:

```text
2 * (a & b)
```

is always even.

Therefore modulo 2:

```text
(a + b) % 2 = (a ^ b) % 2
```

That is the whole trick.

---

## From dry run to code

We only need the parity of:

```text
x + sum(a)
```

and compare it with the parity of `y`.

```text
sum = 0

for every a[i]:
    sum += a[i]

aliceParity = (x + sum) & 1
targetParity = y & 1

if aliceParity == targetParity
    Alice
else
    Bob
```

Notice what we **do not** need:

```text
✗ simulate + and XOR choices
✗ generate 2^n possibilities
✗ store the array
✗ inspect all 32/64 bits
```

We need only:

```text
bit 0
```

### Complexity

```text
Time:  O(N)
Space: O(1)
```

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using int64 = long long;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int T;
    cin >> T;

    while (T--) {
        int n;
        int64 x, y;

        cin >> n >> x >> y;

        int64 sum = 0;

        for (int i = 0; i < n; ++i) {
            int64 value;
            cin >> value;
            sum += value;
        }

        int aliceParity = (x + sum) & 1LL;
        int targetParity = y & 1LL;

        if (aliceParity == targetParity) {
            cout << "Alice\n";
        } else {
            cout << "Bob\n";
        }
    }

    return 0;
}
```

---

# Final Recognition Map

```text
Problem 1 - BITEQU
"Construct a,b,c,d satisfying bit equation"
             ↓
CONTROL THE BITS
             ↓
force a&b = 0
             ↓
reduce to c^d = N
             ↓
construct d


Problem 2 - Range XOR
"Many XOR queries [L,R]"
             ↓
REPEATED PREFIX
             ↓
x^x = 0
             ↓
prefix XOR
             ↓
pref[R+1] ^ pref[L]


Problem 3 - Fortune Telling
"Every step chooses + or XOR"
             ↓
too many possibilities
             ↓
LOOK FOR INVARIANT
             ↓
+ and XOR have same bit0 behavior
             ↓
track only parity
```

## The main learning point

```text
BIT PROBLEM
    ↓
Do NOT immediately manipulate the whole integer.
    ↓
Ask:
"What happens to ONE bit?"
    ↓
especially:
bit 0?
highest bit?
same bits?
different bits?
bits that can never be removed?
    ↓
Find the invariant / construction / cancellation.
    ↓
Then code exactly that observation.
```
