# 024_Binary_Number_System.md

# Binary Number System For Competitive Programming

---

# 1. Introduction

Binary Number System is one of the MOST important foundations in CP.

It appears everywhere:
- bitwise operations
- bitmasking
- binary search
- low-level optimization
- subsets
- DP states
- hardware thinking

Strong contestants think binary as:

```text
Representation using powers of 2
```

This intuition is critical.

---

# 2. What Is Binary?

Binary means:

```text
Base 2 number system
```

Only digits allowed:

```text
0 and 1
```

Unlike decimal:
```text
Base 10
```

---

# 3. Decimal Vs Binary

Decimal:

```text
572
=
5×10²
+
7×10¹
+
2×10⁰
```

Binary:

```text
1011
=
1×2³
+
0×2²
+
1×2¹
+
1×2⁰
```

---

# 4. Binary To Decimal

Example:

```text
1011
```

Step-by-step:

```text
1×8 = 8
0×4 = 0
1×2 = 2
1×1 = 1
```

Total:

```text
11
```

---

# 5. Decimal To Binary

Example:

Convert:
```text
13
```

Repeated division by 2:

```text
13 / 2 = 6 remainder 1
6 / 2 = 3 remainder 0
3 / 2 = 1 remainder 1
1 / 2 = 0 remainder 1
```

Read bottom-up:

```text
1101
```

---

# 6. Why Binary Matters In CP

Binary gives:
- compact representation
- fast operations
- subset encoding
- bit-level optimization

Very important in:
- bitmask DP
- graphs
- greedy
- state compression

---

# 7. Powers Of Two

Binary positions represent:

```text
2⁰ = 1
2¹ = 2
2² = 4
2³ = 8
2⁴ = 16
```

This is the MOST important binary intuition.

---

# 8. Bit Positions

Example:

```text
13
=
1101
```

Positions:

```text
8 4 2 1
1 1 0 1
```

Meaning:

```text
8 + 4 + 1
=
13
```

---

# 9. Least Significant Bit (LSB)

Rightmost bit.

Determines:
```text
odd/even
```

Rules:

```text
LSB = 1 → odd
LSB = 0 → even
```

---

# Example

```text
13 = 1101
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

# 10. Most Significant Bit (MSB)

Leftmost set bit.

Represents:
```text
largest power of 2
```

Very important in:
- bit manipulation
- greedy
- tries

---

# 11. Binary Length

Number of bits needed.

Formula:

```text
floor(log2(n)) + 1
```

Example:

```text
13
=
1101
```

Needs:
```text
4 bits
```

---

# 12. Binary And Subsets

Extremely important CP observation.

Binary naturally represents subsets.

Example:

```text
3 bits
```

Possible masks:

```text
000
001
010
011
100
101
110
111
```

Total:
```text
2³ = 8 subsets
```

Foundation of:
- bitmask DP
- subset enumeration

---

# 13. Binary And Computers

Computers internally use:
```text
binary states
```

because hardware naturally supports:
- ON/OFF
- TRUE/FALSE
- 1/0

Very important real-world intuition.

---

# 14. Binary Compression

Binary compresses:
- multiple booleans
- states
- flags

into single integer.

Very important optimization technique.

---

# 15. CP / FAANG Problem Forms

---

# Form 1 — Binary To Decimal

## Problem

Convert binary to decimal.

---

## Observation

Use powers of 2.

---

## Step-by-Step Working

```text
1011

=
1×8 + 0×4 + 1×2 + 1×1
=
11
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int binaryToDecimal(
    string s
) {

    int ans = 0;

    for (char c : s) {

        ans =
            ans * 2
          + (c - '0');
    }

    return ans;
}
```

---

# Form 2 — Decimal To Binary

## Problem

Convert decimal to binary.

---

## Observation

Repeated division by 2.

---

## Step-by-Step Working

```text
13

13/2 = 6 rem 1
6/2 = 3 rem 0
3/2 = 1 rem 1
1/2 = 0 rem 1

Binary:
1101
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

string decimalToBinary(
    int n
) {

    string ans;

    while (n > 0) {

        ans +=
            char('0' + (n % 2));

        n /= 2;
    }

    reverse(ans.begin(),
            ans.end());

    return ans;
}
```

---

# Form 3 — Odd Even Using Binary

## Problem

Determine odd/even.

---

## Observation

LSB determines parity.

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

# Form 4 — Count Bits Needed

## Problem

Find binary length.

---

## Observation

Highest power of 2.

---

## Step-by-Step Working

```text
13
=
1101
```

Length:
```text
4
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int bitLength(int n) {

    int len = 0;

    while (n > 0) {

        len++;

        n >>= 1;
    }

    return len;
}
```

---

# Form 5 — Subset Enumeration

## Problem

Enumerate subsets using binary masks.

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

# Form 6 — Largest Power Of 2

## Problem

Find MSB power.

---

## Observation

Leftmost set bit.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int highestPower2(int n) {

    int ans = 1;

    while (ans <= n) {

        ans <<= 1;
    }

    return ans >> 1;
}
```

---

# Form 7 — Binary State Compression

## Problem

Store boolean states efficiently.

---

## Observation

Bits compress states.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int setBit(
    int mask,
    int pos
) {

    return mask | (1 << pos);
}
```

---

# 16. Real World Applications

| Real System | Usage |
|---|---|
| Operating systems | binary memory |
| Databases | bitmap indexes |
| Networking | packet flags |
| Compression | bit encoding |
| CPUs | machine instructions |
| Distributed systems | state flags |
| Graphics | pixel representation |

---

# 17. Real Engineering Insight

Binary usually means:

```text
Compact Power-Of-Two Representation
```

This mindset is extremely important.

---

# 18. Observation Recognition Signals

Look for:

```text
1. powers of 2
2. subsets
3. bit operations
4. flags
5. compression
6. parity
7. masks
8. state representation
```

---

# 19. Decision Tree

```text
Need compact states?
→ binary mask

Need odd/even?
→ LSB

Need subsets?
→ binary enumeration

Need largest power of 2?
→ MSB

Need state compression?
→ bits
```

---

# 20. Common Traps

```text
1. Wrong bit position
2. MSB/LSB confusion
3. Off-by-one shifts
4. Binary reading direction mistakes
5. Forgetting powers of 2
6. Overflow during shifts
7. Wrong subset masks
8. Leading zero confusion
```

---

# 21. Final Checklist

Before solving:

```text
1. Can binary compress states?
2. Are powers of 2 involved?
3. Can masks represent subsets?
4. Is parity useful?
5. Is bit representation helpful?
6. Is state compression possible?
7. Is LSB/MSB important?
8. Can bit operations optimize?
```

---

# 22. Final Mental Shortcut

```text
Binary
=
Powers Of Two
+
Compact States
+
Bit Representation
```
