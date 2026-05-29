# 025_Bitwise_Math_Observations.md

# Bitwise Math Observations For Competitive Programming

---

# 1. Introduction

Bitwise problems are NOT just syntax problems.

Strong contestants solve bitwise questions using:

```text
mathematical observations
```

This chapter connects:
- binary
- powers of 2
- parity
- XOR
- subsets
- modulo intuition

with:
```text
bitwise reasoning
```

This is one of the MOST important CP foundations.

---

# 2. Core Bitwise Intuition

Bitwise operations work on:

```text
binary representation
```

Thus every bitwise problem is secretly:

```text
binary mathematics
```

---

# 3. Powers Of Two Observation

Most important binary observation:

```text
Every bit position
represents power of 2
```

Example:

```text
13
=
1101

=
8 + 4 + 1
```

---

# 4. Odd Even Observation

Least significant bit determines parity.

Rules:

```text
LSB = 1 → odd
LSB = 0 → even
```

Thus:

```cpp
n & 1
```

checks odd/even instantly.

---

# Example

```text
13
=
1101
```

LSB:
```text
1
```

Thus:
```text
odd
```

---

# 5. XOR Cancellation Observation

MOST IMPORTANT XOR PROPERTY.

```text
x ^ x = 0
x ^ 0 = x
```

Meaning:
- equal pairs cancel

Foundation of:
- single number problems
- parity tricks
- subset XOR

---

# Example

```text
2 ^ 3 ^ 2 ^ 4 ^ 4
=
3
```

Pairs vanish.

---

# 6. XOR As Difference Detector

XOR detects:
```text
different bits
```

Rules:

```text
same bits → 0
different bits → 1
```

Very important intuition.

---

# 7. Power Of Two Observation

A power of two has:

```text
exactly one set bit
```

Examples:

```text
1  = 0001
2  = 0010
4  = 0100
8  = 1000
```

Very important bitwise pattern.

---

# 8. Power Of Two Trick

Observation:

```text
n & (n-1)
```

removes:
```text
lowest set bit
```

Thus for power of two:

```text
n & (n-1) = 0
```

because only one set bit exists.

---

# Example

```text
8  = 1000
7  = 0111

1000 & 0111
=
0000
```

---

# 9. Counting Set Bits

Set bits:
```text
number of 1s
```

Very important in:
- subsets
- DP
- masks
- combinatorics

---

# Observation

Brian Kernighan trick:

```text
n & (n-1)
```

removes one set bit.

Thus:
- iterations = set bits count

Very elegant observation.

---

# 10. Subset Observation

Each bit:
- selected
- not selected

Thus:
```text
n bits
→
2^n subsets
```

Foundation of:
- subset enumeration
- bitmask DP

---

# 11. Left Shift Observation

```text
n << 1
```

means:

```text
multiply by 2
```

because bits shift left.

Example:

```text
5 = 0101

<<1

1010 = 10
```

---

# 12. Right Shift Observation

```text
n >> 1
```

means:

```text
divide by 2
```

integer division.

Very important optimization.

---

# 13. Binary Mask Observation

Bits represent:
- states
- choices
- flags

Example:

```text
10101
```

can mean:
- selected items
- enabled features
- graph states

Very powerful abstraction.

---

# 14. XOR And Parity

Observation:

XOR behaves like:
```text
parity of bits
```

Odd occurrences:
```text
remain
```

Even occurrences:
```text
cancel
```

Very important insight.

---

# 15. CP / FAANG Problem Forms

---

# Form 1 — Odd Even Using Bit

## Problem

Check odd/even.

---

## Observation

LSB determines parity.

---

## Step-by-Step Working

```text
13
=
1101
```

Last bit:
```text
1
```

Odd.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool isOdd(int n) {

    return n & 1;
}
```

---

# Form 2 — Single Number XOR

## Problem

Every number appears twice except one.

---

## Observation

Pairs cancel.

---

## Step-by-Step Working

```text
2 ^ 3 ^ 2 ^ 4 ^ 4
=
3
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int singleNumber(
    vector<int>& a
) {

    int x = 0;

    for (int v : a) {

        x ^= v;
    }

    return x;
}
```

---

# Form 3 — Power Of Two

## Problem

Check if number power of two.

---

## Observation

Only one set bit.

Formula:

```text
n & (n-1) = 0
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool powerOfTwo(int n) {

    return n > 0
        && (n & (n - 1)) == 0;
}
```

---

# Form 4 — Count Set Bits

## Problem

Count number of set bits.

---

## Observation

Each:
```text
n&(n-1)
```

removes one set bit.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int countBits(int n) {

    int cnt = 0;

    while (n) {

        n &= (n - 1);

        cnt++;
    }

    return cnt;
}
```

---

# Form 5 — Multiply By 2

## Problem

Multiply efficiently.

---

## Observation

Left shift.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int multiply2(int n) {

    return n << 1;
}
```

---

# Form 6 — Divide By 2

## Problem

Divide efficiently.

---

## Observation

Right shift.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int divide2(int n) {

    return n >> 1;
}
```

---

# Form 7 — Subset Enumeration

## Problem

Enumerate subsets.

---

## Observation

Bits represent selections.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

void subsets(
    vector<int>& a
) {

    int n = a.size();

    for (int mask = 0;
         mask < (1 << n);
         mask++) {

        for (int i = 0;
             i < n;
             i++) {

            if (mask & (1 << i)) {

                cout << a[i] << " ";
            }
        }

        cout << "\n";
    }
}
```

---

# 16. Real World Applications

| Real System | Usage |
|---|---|
| CPUs | binary instructions |
| Operating systems | permission bits |
| Databases | bitmap indexes |
| Networking | packet flags |
| Compression | bit encoding |
| Cryptography | XOR encryption |
| Distributed systems | compact states |

---

# 17. Real Engineering Insight

Bitwise usually means:

```text
Binary Mathematics
+
Compact State Representation
```

This mindset is extremely important.

---

# 18. Observation Recognition Signals

Look for:

```text
1. powers of 2
2. parity
3. subsets
4. masks
5. XOR cancellation
6. binary representation
7. compact states
8. bit toggling
```

---

# 19. Decision Tree

```text
Need parity?
→ LSB

Need pair cancellation?
→ XOR

Need subset representation?
→ masks

Need power of 2 check?
→ n&(n-1)

Need count of 1s?
→ bit counting

Need compact states?
→ binary masks
```

---

# 20. Common Traps

```text
1. Wrong bit position
2. Confusing XOR with OR
3. Forgetting precedence
4. Negative shift confusion
5. Wrong mask operations
6. Overflow during shifts
7. Missing XOR cancellation
8. Off-by-one bit indexing
```

---

# 21. Final Checklist

Before solving:

```text
1. Can binary simplify?
2. Is parity useful?
3. Is XOR cancellation involved?
4. Are powers of 2 involved?
5. Can masks represent states?
6. Is subset enumeration useful?
7. Can shifting optimize?
8. Can bits compress information?
```

---

# 22. Final Mental Shortcut

```text
Bitwise
=
Binary Mathematics
+
Bit Observations
+
Compact State Thinking
```
