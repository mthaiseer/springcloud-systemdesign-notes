# 016_Prefix_XOR.md — MiniPrefixSumDifferenceEngine

# Prefix XOR

> Prefix XOR is the bitwise cousin of Prefix Sum.
>
> Prefix Sum uses:
>
> ```text
> sum(l..r) = pref[r] - pref[l-1]
> ```
>
> Prefix XOR uses:
>
> ```text
> xor(l..r) = prefXor[r] ^ prefXor[l-1]
> ```

---

## Clickable Index

1. [What Is Prefix XOR?](#1-what-is-prefix-xor)
2. [Why Prefix XOR Matters](#2-why-prefix-xor-matters)
3. [XOR Properties You Must Know](#3-xor-properties-you-must-know)
4. [Prefix XOR Formula](#4-prefix-xor-formula)
5. [Range XOR Formula](#5-range-xor-formula)
6. [0-Indexed vs 1-Indexed Prefix XOR](#6-0-indexed-vs-1-indexed-prefix-xor)
7. [Why XOR Cancels](#7-why-xor-cancels)
8. [Step-by-Step Dry Run — Build Prefix XOR](#8-step-by-step-dry-run--build-prefix-xor)
9. [Step-by-Step Dry Run — Range XOR Query](#9-step-by-step-dry-run--range-xor-query)
10. [Prefix XOR vs Prefix Sum](#10-prefix-xor-vs-prefix-sum)
11. [Problem Form 1 — Range XOR Query](#11-problem-form-1--range-xor-query)
12. [Problem Form 2 — XOR From L To R](#12-problem-form-2--xor-from-l-to-r)
13. [Problem Form 3 — Find Missing Number](#13-problem-form-3--find-missing-number)
14. [Problem Form 4 — Single Number / Odd Occurrence](#14-problem-form-4--single-number--odd-occurrence)
15. [Problem Form 5 — Prefix XOR Array Reconstruction](#15-problem-form-5--prefix-xor-array-reconstruction)
16. [Real World Model 1 — Bitmask State Tracking](#16-real-world-model-1--bitmask-state-tracking)
17. [Real World Model 2 — Lightweight Integrity Check](#17-real-world-model-2--lightweight-integrity-check)
18. [Real World Model 3 — Feature Toggle State Delta](#18-real-world-model-3--feature-toggle-state-delta)
19. [Real World Model 4 — Parity-Based Event State](#19-real-world-model-4--parity-based-event-state)
20. [Decision Tree](#20-decision-tree)
21. [Common Mistakes](#21-common-mistakes)
22. [Complexity](#22-complexity)
23. [Reusable C++ Templates](#23-reusable-c-templates)
24. [CP / FAANG Problem Forms](#24-cp--faang-problem-forms)
25. [Practice Checklist](#25-practice-checklist)
26. [Next Step](#26-next-step)

---

## 1. What Is Prefix XOR?

Prefix XOR stores cumulative XOR from start to current index.

For array:

```text
a = [3, 8, 2, 6, 4]
```

Prefix XOR:

```text
pref[0] = 0
pref[1] = 3
pref[2] = 3 ^ 8
pref[3] = 3 ^ 8 ^ 2
...
```

Using 1-indexed prefix:

```text
pref[i] = pref[i-1] ^ a[i-1]
```

---

## 2. Why Prefix XOR Matters

Prefix XOR appears in:

```text
range XOR queries
subarray XOR target
zero XOR subarrays
bitmask parity
single number problems
missing number problems
odd/even occurrence problems
state toggle systems
```

It is important because XOR has cancellation property:

```text
x ^ x = 0
```

That makes prefix XOR very powerful.

---

## 3. XOR Properties You Must Know

```text
x ^ x = 0
x ^ 0 = x
x ^ y = y ^ x
(x ^ y) ^ z = x ^ (y ^ z)
```

Meaning:

```text
same value cancels itself
```

Example:

```text
5 ^ 7 ^ 5 = 7
```

because:

```text
5 ^ 5 = 0
0 ^ 7 = 7
```

---

## 4. Prefix XOR Formula

1-indexed prefix XOR:

```text
pref[0] = 0
pref[i] = pref[i-1] ^ a[i-1]
```

Example:

```text
a = [3, 8, 2]
```

```text
pref[0] = 0
pref[1] = 0 ^ 3 = 3
pref[2] = 3 ^ 8 = 11
pref[3] = 11 ^ 2 = 9
```

---

## 5. Range XOR Formula

For range:

```text
[l, r]
```

Using 1-indexed prefix:

```text
xor(l,r) = pref[r+1] ^ pref[l]
```

Why?

```text
pref[r+1] = a[0] ^ a[1] ^ ... ^ a[r]
pref[l]   = a[0] ^ a[1] ^ ... ^ a[l-1]
```

XOR them:

```text
common prefix cancels
```

Remaining:

```text
a[l] ^ a[l+1] ^ ... ^ a[r]
```

---

## 6. 0-Indexed vs 1-Indexed Prefix XOR

### 0-Indexed Prefix XOR

```cpp
pref[0] = a[0];

for (int i = 1; i < n; i++) {
    pref[i] = pref[i - 1] ^ a[i];
}
```

Query:

```cpp
if (l == 0) ans = pref[r];
else ans = pref[r] ^ pref[l - 1];
```

---

### 1-Indexed Prefix XOR

```cpp
pref[0] = 0;

for (int i = 1; i <= n; i++) {
    pref[i] = pref[i - 1] ^ a[i - 1];
}
```

Query:

```cpp
ans = pref[r + 1] ^ pref[l];
```

Recommended:

```text
Use 1-indexed prefix XOR.
```

---

## 7. Why XOR Cancels

Suppose:

```text
pref[r+1] = a0 ^ a1 ^ a2 ^ a3
pref[l]   = a0 ^ a1
```

Then:

```text
pref[r+1] ^ pref[l]
= a0 ^ a1 ^ a2 ^ a3 ^ a0 ^ a1
```

Rearrange:

```text
(a0 ^ a0) ^ (a1 ^ a1) ^ a2 ^ a3
= 0 ^ 0 ^ a2 ^ a3
= a2 ^ a3
```

So range XOR works.

---

## 8. Step-by-Step Dry Run — Build Prefix XOR

Input:

```text
a = [3, 8, 2, 6]
```

Initial:

```text
pref = [0, 0, 0, 0, 0]
```

### i = 1

```text
pref[1] = pref[0] ^ a[0]
        = 0 ^ 3
        = 3
```

### i = 2

```text
pref[2] = pref[1] ^ a[1]
        = 3 ^ 8
        = 11
```

### i = 3

```text
pref[3] = pref[2] ^ a[2]
        = 11 ^ 2
        = 9
```

### i = 4

```text
pref[4] = pref[3] ^ a[3]
        = 9 ^ 6
        = 15
```

Final:

```text
pref = [0, 3, 11, 9, 15]
```

---

## 9. Step-by-Step Dry Run — Range XOR Query

Array:

```text
a = [3, 8, 2, 6]
```

Prefix:

```text
pref = [0, 3, 11, 9, 15]
```

Query:

```text
xor(1,2)
```

Manual:

```text
a[1] ^ a[2]
= 8 ^ 2
= 10
```

Using prefix:

```text
pref[3] ^ pref[1]
= 9 ^ 3
= 10
```

Correct.

---

## 10. Prefix XOR vs Prefix Sum

| Concept | Prefix Sum | Prefix XOR |
|---|---|---|
| Build | `pref[i]=pref[i-1]+a[i-1]` | `pref[i]=pref[i-1]^a[i-1]` |
| Range query | `pref[r+1]-pref[l]` | `pref[r+1]^pref[l]` |
| Cancellation | subtraction | XOR self-cancel |
| Used for | sums | bitwise parity/state |

---

## 11. Problem Form 1 — Range XOR Query

### Problem

Given static array and many queries:

```text
xor(l,r)
```

Return XOR of subarray.

Input:

```text
a = [3, 8, 2, 6]
queries:
[1,2]
[0,3]
```

Output:

```text
10
15
```

---

### Pattern Recognition

Use when:

```text
static array
many range XOR queries
```

Pattern:

```text
Prefix XOR
```

---

### Step-by-Step Working

Build:

```text
pref = [0, 3, 11, 9, 15]
```

Query `[1,2]`:

```text
pref[3] ^ pref[1]
= 9 ^ 3
= 10
```

Query `[0,3]`:

```text
pref[4] ^ pref[0]
= 15 ^ 0
= 15
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class RangeXorQuery {
private:
    vector<int> pref;

public:
    RangeXorQuery(vector<int>& a) {
        int n = a.size();

        pref.assign(n + 1, 0);

        for (int i = 1; i <= n; i++) {
            // pref[i] stores XOR of first i elements.
            pref[i] = pref[i - 1] ^ a[i - 1];
        }
    }

    int query(int l, int r) {
        // XOR from l to r inclusive.
        return pref[r + 1] ^ pref[l];
    }
};

int main() {
    vector<int> a = {3, 8, 2, 6};

    RangeXorQuery rxq(a);

    cout << rxq.query(1, 2) << "\n";
    cout << rxq.query(0, 3) << "\n";

    return 0;
}
```

---

### Complexity

```text
Build: O(N)
Query: O(1)
Space: O(N)
```

---

## 12. Problem Form 2 — XOR From L To R

### Problem

Find XOR of all integers from `L` to `R`.

Example:

```text
L = 3
R = 6
```

Numbers:

```text
3 ^ 4 ^ 5 ^ 6
```

---

### Pattern Recognition

Use special XOR pattern from `0..n`.

```text
xor(0..n) repeats every 4
```

Then:

```text
xor(L..R) = xor(0..R) ^ xor(0..L-1)
```

---

### Step-by-Step Working

Function `xor0ToN(n)`:

```text
n % 4 == 0 -> n
n % 4 == 1 -> 1
n % 4 == 2 -> n + 1
n % 4 == 3 -> 0
```

For:

```text
L = 3, R = 6
```

```text
xor(0..6) = 7
xor(0..2) = 3
xor(3..6) = 7 ^ 3 = 4
```

Manual:

```text
3 ^ 4 ^ 5 ^ 6 = 4
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int xor0ToN(int n) {
    if (n < 0) return 0;

    int rem = n % 4;

    if (rem == 0) return n;
    if (rem == 1) return 1;
    if (rem == 2) return n + 1;

    return 0;
}

int xorLToR(int l, int r) {
    return xor0ToN(r) ^ xor0ToN(l - 1);
}

int main() {
    cout << xorLToR(3, 6) << "\n";

    return 0;
}
```

---

### Complexity

```text
O(1)
```

---

## 13. Problem Form 3 — Find Missing Number

### Problem

Given numbers from:

```text
0..n
```

with one missing, find missing number.

Input:

```text
nums = [3, 0, 1]
```

Expected range:

```text
0, 1, 2, 3
```

Missing:

```text
2
```

---

### Pattern Recognition

XOR all expected numbers and all actual numbers.

Pairs cancel.

Remaining value is missing.

---

### Step-by-Step Working

Expected XOR:

```text
0 ^ 1 ^ 2 ^ 3
```

Actual XOR:

```text
3 ^ 0 ^ 1
```

Combined:

```text
0 ^ 1 ^ 2 ^ 3 ^ 3 ^ 0 ^ 1
```

Cancel:

```text
0^0 = 0
1^1 = 0
3^3 = 0
```

Left:

```text
2
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int missingNumber(vector<int>& nums) {
    int n = nums.size();

    int xr = 0;

    // XOR all numbers from 0 to n.
    for (int i = 0; i <= n; i++) {
        xr ^= i;
    }

    // XOR all given numbers.
    // Matching numbers cancel out.
    for (int x : nums) {
        xr ^= x;
    }

    return xr;
}

int main() {
    vector<int> nums = {3, 0, 1};

    cout << missingNumber(nums) << "\n";

    return 0;
}
```

---

### Complexity

```text
Time: O(N)
Space: O(1)
```

---

## 14. Problem Form 4 — Single Number / Odd Occurrence

### Problem

Every number appears twice except one number.

Find that single number.

Input:

```text
nums = [4, 1, 2, 1, 2]
```

Output:

```text
4
```

---

### Pattern Recognition

Use XOR cancellation:

```text
x ^ x = 0
```

All duplicate pairs cancel.

Only odd-occurring value remains.

---

### Step-by-Step Working

```text
4 ^ 1 ^ 2 ^ 1 ^ 2
```

Rearrange:

```text
4 ^ (1 ^ 1) ^ (2 ^ 2)
= 4 ^ 0 ^ 0
= 4
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int singleNumber(vector<int>& nums) {
    int xr = 0;

    for (int x : nums) {
        xr ^= x;
    }

    return xr;
}

int main() {
    vector<int> nums = {4, 1, 2, 1, 2};

    cout << singleNumber(nums) << "\n";

    return 0;
}
```

---

## 15. Problem Form 5 — Prefix XOR Array Reconstruction

### Problem

Given prefix XOR array:

```text
pref[i] = a[0] ^ a[1] ^ ... ^ a[i]
```

Reconstruct original array.

Input:

```text
pref = [5, 2, 0, 3, 1]
```

---

### Pattern Recognition

Since:

```text
pref[i] = pref[i-1] ^ a[i]
```

Then:

```text
a[i] = pref[i] ^ pref[i-1]
```

For `i = 0`:

```text
a[0] = pref[0]
```

---

### Step-by-Step Working

```text
a[0] = pref[0] = 5
a[1] = pref[1] ^ pref[0] = 2 ^ 5 = 7
a[2] = pref[2] ^ pref[1] = 0 ^ 2 = 2
a[3] = pref[3] ^ pref[2] = 3 ^ 0 = 3
a[4] = pref[4] ^ pref[3] = 1 ^ 3 = 2
```

Original:

```text
[5, 7, 2, 3, 2]
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> findArray(vector<int>& pref) {
    int n = pref.size();

    vector<int> a(n);

    a[0] = pref[0];

    for (int i = 1; i < n; i++) {
        // pref[i] = pref[i-1] ^ a[i]
        // so a[i] = pref[i] ^ pref[i-1]
        a[i] = pref[i] ^ pref[i - 1];
    }

    return a;
}

int main() {
    vector<int> pref = {5, 2, 0, 3, 1};

    vector<int> a = findArray(pref);

    for (int x : a) {
        cout << x << " ";
    }

    return 0;
}
```

---

## 16. Real World Model 1 — Bitmask State Tracking

### Scenario

A backend system stores feature flags as bits.

Example:

```text
bit 0 = dark mode
bit 1 = beta user
bit 2 = premium
bit 3 = notifications enabled
```

Toggling a feature is XOR:

```text
state ^= (1 << bit)
```

---

### Problem Simulation

Initial state:

```text
0000
```

Toggle premium:

```text
0100
```

Toggle beta:

```text
0110
```

Toggle premium again:

```text
0010
```

Premium toggled twice, so it cancels.

---

### System Mapping

Useful for:

```text
feature toggles
permission masks
state transitions
configuration diffs
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int state = 0;

    int PREMIUM = 2;
    int BETA = 1;

    // Toggle premium.
    state ^= (1 << PREMIUM);

    // Toggle beta.
    state ^= (1 << BETA);

    // Toggle premium again.
    state ^= (1 << PREMIUM);

    cout << state << "\n";

    return 0;
}
```

---

## 17. Real World Model 2 — Lightweight Integrity Check

### Scenario

A system receives IDs.

Every ID should appear exactly twice except one corrupted/missing/extra ID.

XOR can detect the odd-occurring ID.

---

### Problem Simulation

IDs:

```text
101, 202, 303, 202, 101
```

Only odd-occurring:

```text
303
```

XOR all IDs:

```text
101 ^ 202 ^ 303 ^ 202 ^ 101 = 303
```

---

### System Mapping

Useful for:

```text
lightweight consistency checks
duplicate cancellation
missing/extra ID detection
log sanity checks
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

int findOddId(vector<int>& ids) {
    int xr = 0;

    for (int id : ids) {
        xr ^= id;
    }

    return xr;
}

int main() {
    vector<int> ids = {101, 202, 303, 202, 101};

    cout << findOddId(ids) << "\n";

    return 0;
}
```

---

## 18. Real World Model 3 — Feature Toggle State Delta

### Scenario

Events represent feature toggle changes.

```text
toggle A
toggle B
toggle A
```

Final active toggles:

```text
only B
```

Because A toggled twice.

---

### Problem Simulation

Feature bits:

```text
A = bit 0
B = bit 1
C = bit 2
```

Events:

```text
A, B, A, C
```

State:

```text
000
001
011
010
110
```

Final:

```text
B and C enabled
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> toggles = {0, 1, 0, 2};

    int state = 0;

    for (int bit : toggles) {
        state ^= (1 << bit);
    }

    cout << state << "\n";

    return 0;
}
```

---

## 19. Real World Model 4 — Parity-Based Event State

### Scenario

Some systems only care whether event count is odd or even.

Example:

```text
door opened/closed
toggle switch
user online/offline
lock/unlock
```

XOR models parity naturally.

---

### Problem Simulation

Events:

```text
login, logout, login
```

If login/logout are toggles, odd count means active.

State:

```text
0 -> 1 -> 0 -> 1
```

Final:

```text
active
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int active = 0;

    vector<string> events = {"login", "logout", "login"};

    for (string e : events) {
        // Toggle active state.
        active ^= 1;
    }

    cout << (active ? "active" : "inactive") << "\n";

    return 0;
}
```

---

## 20. Decision Tree

```text
Array / bitwise problem?
|
+-- Need range XOR query?
|   |
|   +-- Prefix XOR
|
+-- Need subarray XOR target?
|   |
|   +-- Prefix XOR + HashMap
|
+-- Pairs cancel?
|   |
|   +-- XOR all values
|
+-- Need missing number?
|   |
|   +-- XOR expected and actual
|
+-- Need toggle state?
    |
    +-- XOR bitmask
```

---

## 21. Common Mistakes

### Mistake 1 — Using subtraction for XOR range

Wrong:

```cpp
pref[r+1] - pref[l]
```

Correct:

```cpp
pref[r+1] ^ pref[l]
```

---

### Mistake 2 — Forgetting `pref[0] = 0`

Use 1-indexed prefix:

```cpp
pref[0] = 0
```

---

### Mistake 3 — Thinking XOR is addition

XOR is bitwise.

```text
2 ^ 3 = 1
```

not:

```text
5
```

---

### Mistake 4 — Not Understanding Cancellation

```text
x ^ x = 0
```

This is the foundation.

---

### Mistake 5 — Using XOR when counts are not parity-like

XOR is good when duplicates cancel or state toggles.

It is not a replacement for sum.

---

## 22. Complexity

Prefix XOR build:

```text
O(N)
```

Range query:

```text
O(1)
```

Space:

```text
O(N)
```

For single-number XOR:

```text
Time  : O(N)
Space : O(1)
```

---

## 23. Reusable C++ Templates

### Template 1 — Prefix XOR Build

```cpp
vector<int> buildPrefixXor(vector<int>& a) {
    int n = a.size();

    vector<int> pref(n + 1, 0);

    for (int i = 1; i <= n; i++) {
        pref[i] = pref[i - 1] ^ a[i - 1];
    }

    return pref;
}
```

---

### Template 2 — Range XOR Query

```cpp
int rangeXor(vector<int>& pref, int l, int r) {
    return pref[r + 1] ^ pref[l];
}
```

---

### Template 3 — XOR 0 To N

```cpp
int xor0ToN(int n) {
    if (n < 0) return 0;

    int rem = n % 4;

    if (rem == 0) return n;
    if (rem == 1) return 1;
    if (rem == 2) return n + 1;

    return 0;
}
```

---

### Template 4 — XOR L To R

```cpp
int xorLToR(int l, int r) {
    return xor0ToN(r) ^ xor0ToN(l - 1);
}
```

---

## 24. CP / FAANG Problem Forms

---

### Problem 1 — Range XOR Query

#### Recognition

```text
static array
many XOR queries [l,r]
```

#### Pattern

```text
Prefix XOR
```

#### Step-by-Step Working

```text
1. Build pref[0] = 0
2. pref[i] = pref[i-1] ^ a[i-1]
3. Query answer = pref[r+1] ^ pref[l]
```

#### Commented C++ Code

```cpp
class RangeXorQuery {
private:
    vector<int> pref;

public:
    RangeXorQuery(vector<int>& a) {
        pref.assign(a.size() + 1, 0);

        for (int i = 1; i <= a.size(); i++) {
            pref[i] = pref[i - 1] ^ a[i - 1];
        }
    }

    int query(int l, int r) {
        return pref[r + 1] ^ pref[l];
    }
};
```

---

### Problem 2 — Missing Number

#### Recognition

```text
numbers 0..n
one missing
```

#### Pattern

```text
XOR expected and actual
```

#### Commented C++ Code

```cpp
int missingNumber(vector<int>& nums) {
    int xr = 0;
    int n = nums.size();

    for (int i = 0; i <= n; i++) {
        xr ^= i;
    }

    for (int x : nums) {
        xr ^= x;
    }

    return xr;
}
```

---

### Problem 3 — Single Number

#### Recognition

```text
all numbers twice except one
```

#### Pattern

```text
XOR cancellation
```

#### Commented C++ Code

```cpp
int singleNumber(vector<int>& nums) {
    int xr = 0;

    for (int x : nums) {
        xr ^= x;
    }

    return xr;
}
```

---

### Problem 4 — Prefix XOR Reconstruction

#### Recognition

```text
given prefix XOR array
recover original
```

#### Pattern

```text
a[i] = pref[i] ^ pref[i-1]
```

#### Commented C++ Code

```cpp
vector<int> findArray(vector<int>& pref) {
    vector<int> a(pref.size());

    a[0] = pref[0];

    for (int i = 1; i < pref.size(); i++) {
        a[i] = pref[i] ^ pref[i - 1];
    }

    return a;
}
```

---

### Problem 5 — XOR L To R

#### Recognition

```text
xor of consecutive integer range
```

#### Pattern

```text
xor pattern repeats every 4
```

#### Commented C++ Code

```cpp
int xor0ToN(int n) {
    if (n < 0) return 0;

    if (n % 4 == 0) return n;
    if (n % 4 == 1) return 1;
    if (n % 4 == 2) return n + 1;

    return 0;
}

int xorLToR(int l, int r) {
    return xor0ToN(r) ^ xor0ToN(l - 1);
}
```

---

## 25. Practice Checklist

Before solving:

```text
1. Is this a XOR/range XOR problem?
2. Can duplicate values cancel?
3. Is the problem parity/toggle based?
4. Do I need prefix XOR?
5. Is query [l,r] static?
6. Did I use pref[r+1] ^ pref[l]?
7. Is it missing number / single number?
8. Does XOR 0..n pattern apply?
9. Do I need hashmap for target XOR?
10. Is this actually a sum problem instead?
```

---

## 26. Next Step

```text
017_Subarray_XOR_Problems.md
```

Next file covers:

```text
subarray XOR equals K
count target XOR
zero XOR subarrays
prefix XOR hashmap
XOR trie preview
```
