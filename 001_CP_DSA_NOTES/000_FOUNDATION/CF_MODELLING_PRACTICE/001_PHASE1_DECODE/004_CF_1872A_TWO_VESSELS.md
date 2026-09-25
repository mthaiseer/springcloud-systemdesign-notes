# CF 1872A — Two Vessels

**Problem:** https://codeforces.com/problemset/problem/1872/A  
**Pattern:** Absolute Difference → Operation Delta → Minimum Operations → Ceil Division

## 1. Problem Asking

Two vessels contain `a` and `b` grams of water. In one move, transfer **up to `c` grams** from either vessel to the other. The transferred amount may be fractional.

Find the **minimum moves** needed to make both amounts equal.

```text
initial: a, b
operation: move x grams, where 0 < x <= c
final:   a' = b'
goal:    minimum number of moves
```

## 2. Story → Variables + Small Test Cases

```text
First vessel          → a
Second vessel         → b
Cup capacity          → c
Transferred in a move → x, 0 < x <= c
Initial difference    → d = |a-b|
Moves                 → k
Goal                  → minimize k so difference becomes 0
```

Fix `c = 2` and let the initial difference grow. Assume the first vessel has more water.

### d = 0

```text
a=5, b=5
already equal → 0 moves
```

### d = 1

```text
a=6, b=5
move 0.5 from first to second:
(6,5) → (5.5,5.5)
1 move
```

### d = 2

```text
a=7, b=5
move 1:
(7,5) → (6,6)
1 move
```

### d = 3

```text
a=8, b=5
move 1.5:
(8,5) → (6.5,6.5)
1 move
```

### d = 4

```text
a=9, b=5
move 2:
(9,5) → (7,7)
1 move
```

### d = 5

```text
a=10, b=5
move 2:   (10,5) → (8,7)    difference 1
move 0.5: (8,7)  → (7.5,7.5)
2 moves
```

### d = 6, 7, 8

```text
d=6: transfer 2, then 1       → 2 moves
d=7: transfer 2, then 1.5     → 2 moves
d=8: transfer 2, then 2       → 2 moves
```

Small-case pattern:

| Difference `d` | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 |
|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| Moves (`c=2`) | 0 | 1 | 1 | 1 | 1 | 2 | 2 | 2 | 2 | 3 |

**Observation:** With `c=2`, one move can eliminate **up to 4** units of difference—not just 2.

## 3. Mathematical Model

Suppose `a > b`. Transfer `x` from the first vessel to the second:

```text
before: (a, b)
after:  (a-x, b+x)
```

New signed difference:

```text
(a-x) - (b+x)
= a - b - 2x
```

**One gram transferred reduces the difference by two grams:** one vessel loses 1 and the other gains 1.

Since `x <= c`, a move can reduce the difference by **at most `2c`** without overshooting.

```text
initial difference = d = |a-b|
maximum reduction per move = 2c
number of moves = minimum k with 2ck >= d
```

Equivalently, the total water that must move from the fuller vessel to the emptier one is:

```text
required transfer = d/2
cup capacity      = c
```

## 4. Derivation

From the target of equal amounts, for `a >= b`:

```text
a - x = b + x
```

Move terms step by step:

```text
a - b = x + x

a - b = 2x

x = (a-b)/2
```

For either vessel being fuller:

```text
required total transfer = |a-b|/2
```

Each move transfers at most `c`:

```text
k * c >= |a-b|/2
```

Multiply both sides by 2:

```text
2kc >= |a-b|
```

Divide by `2c` (positive):

```text
k >= |a-b| / (2c)
```

Since `k` must be a nonnegative integer:

```text
answer = ceil(|a-b| / (2c))
```

For integer inputs, avoid floating point:

```text
d = abs(a-b)
answer = (d + 2*c - 1) / (2*c)
```

**Why the bound is achievable:** Always transfer `min(c, remaining_difference/2)` from the fuller vessel. Every full move transfers `c`; the last move transfers the exact remainder, which may be fractional. Thus the lower bound is attained.

## 5. Dry Run

Official example: `a=17, b=4, c=3`.

```text
initial difference = |17-4| = 13
maximum reduction/move = 2*3 = 6

move 1: transfer 3
(17,4) → (14,7)       difference 7

move 2: transfer 3
(14,7) → (11,10)      difference 1

move 3: transfer 0.5
(11,10) → (10.5,10.5) difference 0

answer = ceil(13/6) = 3
```

Horizontal check:

```text
(17,4) → d=13 → 2c=6 → ceil(13/6)=3 moves
```

Edge cases:

| `a` | `b` | `c` | `d` | Answer | Why |
|---:|---:|---:|---:|---:|---|
| 17 | 17 | 1 | 0 | 0 | Already equal |
| 3 | 7 | 2 | 4 | 1 | Transfer 2 |
| 17 | 21 | 100 | 4 | 1 | Transfer 2, not 100 |
| 1 | 100 | 1 | 99 | 50 | Last transfer 0.5 |

## 6. Observation / Recognition

```text
minimum transfers to equalize
             ↓
      difference |a-b|
             ↓
   test what ONE move changes
             ↓
(a-x)-(b+x) = (a-b)-2x
             ↓
 maximum difference reduction = 2c
             ↓
        2ck >= |a-b|
             ↓
      ceil(|a-b| / (2c))
```

**Recognition triggers**

- Transfer from one quantity to another → **both sides change**; difference changes by `2x`.
- Minimum moves with bounded change → derive `k * max_change >= distance`.
- Fractional final move allowed → use **ceil division**, not divisibility by `c`.
- Already equal → zero moves.

**What I should notice in under 30 seconds:** The total water is invariant. Equalization requires moving half the initial difference; the cup limits each transfer to `c`.

**What NOT to do:** Divide `|a-b|` by `c`. That ignores the simultaneous decrease of one vessel and increase of the other.

## 7. C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;

    while (t--) {
        long long a, b, c;
        cin >> a >> b >> c;

        long long d = abs(a - b);
        long long maxReduction = 2 * c;
        cout << (d + maxReduction - 1) / maxReduction << '\n';
    }

    return 0;
}
```

**Time:** `O(1)` per test case.  
**Space:** `O(1)`.
