# Bit Manipulation Mastery

> A step-by-step Competitive Programming revision guide built from the
> provided Bit Manipulation / Bitmasking study materials.
>
> **Format used throughout:**\
> **Concept & Core Intuition -\> Dry Run & Step-by-Step Diagram -\>
> Pseudocode -\> Complete C++ Code**
>
> **Important convention:** Unless a problem explicitly involves
> negative values, examples below use non-negative integers. For
> shifts/masks, prefer `1LL << bit` when the bit can approach or exceed
> 30.

------------------------------------------------------------------------

# Table of Contents

-   [Section 1 - Core Bitwise Foundations &
    Operators](#section-1---core-bitwise-foundations--operators)
    -   [1.1 Binary Representation and Base
        Conversion](#11-binary-representation-and-base-conversion)
    -   [1.2 AND](#12-and)
    -   [1.3 OR](#13-or)
    -   [1.4 XOR](#14-xor)
    -   [1.5 NOT](#15-not)
    -   [1.6 Left and Right Shift](#16-left-and-right-shift)
    -   [1.7 Data Types, Bit Capacity, and
        Overflow](#17-data-types-bit-capacity-and-overflow)
    -   [1.8 Operator Precedence
        Pitfalls](#18-operator-precedence-pitfalls)
    -   [1.9 Powers of Two Reference](#19-powers-of-two-reference)
-   [Section 2 - Bitmasking & Set
    Representation](#section-2---bitmasking--set-representation)
    -   [2.1 Representing a Set with an
        Integer](#21-representing-a-set-with-an-integer)
    -   [2.2 Check / Set / Clear / Toggle a
        Bit](#22-check--set--clear--toggle-a-bit)
    -   [2.3 Union and Intersection](#23-union-and-intersection)
    -   [2.4 Generate All Subsets](#24-generate-all-subsets)
    -   [2.5 Included vs Excluded
        Elements](#25-included-vs-excluded-elements)
-   [Section 3 - C++ STL std::bitset](#section-3---c-stl-stdbitset)
    -   [3.1 Declaration and
        Construction](#31-declaration-and-construction)
    -   [3.2 Indexing and Manipulation](#32-indexing-and-manipulation)
    -   [3.3 Large Fixed-Size Bitsets](#33-large-fixed-size-bitsets)
-   [Section 4 - Deep-Dive Applications & Master
    Patterns](#section-4---deep-dive-applications--master-patterns)
    -   [Application 1 - Cyclic Property of
        Bits](#application-1---cyclic-property-of-bits)
    -   [Application 1A - Small K
        Precomputation](#application-1a---small-k-precomputation)
    -   [Application 1B - Large K with Binary
        Search](#application-1b---large-k-with-binary-search)
    -   [Application 2 - All-Pair XOR
        Sum](#application-2---all-pair-xor-sum)
    -   [Application 3 - Operation Decoding / Conservation of
        Bits](#application-3---operation-decoding--conservation-of-bits)
    -   [Application 4 - Highest-to-Lowest Bit
        Greedy](#application-4---highest-to-lowest-bit-greedy)
    -   [Application 5 - Shifted XOR
        Reconstruction](#application-5---shifted-xor-reconstruction)
-   [Section 5 - Pattern & Problem Selection Decision
    Matrix](#section-5---pattern--problem-selection-decision-matrix)
-   [Section 6 - Final Contest Cheat
    Sheet](#section-6---final-contest-cheat-sheet)

------------------------------------------------------------------------

# Section 1 - Core Bitwise Foundations & Operators

## 1.1 Binary Representation and Base Conversion

### 1. Concept & Core Intuition

A binary number uses only `0` and `1`.

Each position represents a power of two:

``` text
bit index:    4    3    2    1    0
weight:      16    8    4    2    1
             2^4  2^3  2^2  2^1  2^0
```

For example:

``` text
13 = 8 + 4 + 1
   = 2^3 + 2^2 + 2^0
   = 1101₂
```

To convert decimal to binary, repeatedly divide by `2`, record each
remainder, then reverse the remainders.

### 2. Dry Run & Step-by-Step Diagram

Convert `13` to binary:

``` text
number   quotient   remainder
--------------------------------
13 / 2      6          1
 6 / 2      3          0
 3 / 2      1          1
 1 / 2      0          1
```

Read the remainders **bottom -\> top**:

``` text
1 1 0 1

13₁₀ = 1101₂
```

Verification:

``` text
1101₂
│││└── 1 * 2^0 = 1
││└─── 0 * 2^1 = 0
│└──── 1 * 2^2 = 4
└───── 1 * 2^3 = 8

8 + 4 + 0 + 1 = 13
```

### 3. Pseudocode

``` text
FUNCTION decimalToBinary(x):
    IF x == 0:
        RETURN "0"

    result = empty string

    WHILE x > 0:
        bit = x MOD 2
        append bit to result
        x = x / 2

    reverse result
    RETURN result
```

### 4. Complete C++ Code

``` cpp
#include <algorithm>
#include <iostream>
#include <string>

using namespace std;

string decimalToBinary(unsigned long long x) {
    if (x == 0) return "0";

    string bits;

    while (x > 0) {
        bits.push_back(char('0' + (x % 2)));
        x /= 2;
    }

    reverse(bits.begin(), bits.end());
    return bits;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    unsigned long long x;
    cin >> x;

    cout << decimalToBinary(x) << '\n';
    return 0;
}
```

------------------------------------------------------------------------

## 1.2 AND

### 1. Concept & Core Intuition

AND is `1` only when **both input bits are 1**.

``` text
a b | a & b
----+------
0 0 |   0
0 1 |   0
1 0 |   0
1 1 |   1
```

Think:

> AND keeps only bits common to both numbers.

### 2. Dry Run & Step-by-Step Diagram

``` text
12 = 1100
10 = 1010

     1100
   & 1010
   ------
     1000 = 8
```

Column-by-column:

``` text
1 & 1 = 1
1 & 0 = 0
0 & 1 = 0
0 & 0 = 0
```

### 3. Pseudocode

``` text
READ a, b
answer = a AND b
PRINT answer
```

### 4. Complete C++ Code

``` cpp
#include <iostream>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    long long a, b;
    cin >> a >> b;

    cout << (a & b) << '\n';
    return 0;
}
```

------------------------------------------------------------------------

## 1.3 OR

### 1. Concept & Core Intuition

OR is `1` if **at least one** input bit is `1`.

``` text
a b | a | b
----+------
0 0 |   0
0 1 |   1
1 0 |   1
1 1 |   1
```

Think:

> OR combines the set bits of both numbers.

### 2. Dry Run & Step-by-Step Diagram

``` text
12 = 1100
10 = 1010

     1100
   | 1010
   ------
     1110 = 14
```

### 3. Pseudocode

``` text
READ a, b
answer = a OR b
PRINT answer
```

### 4. Complete C++ Code

``` cpp
#include <iostream>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    long long a, b;
    cin >> a >> b;

    cout << (a | b) << '\n';
    return 0;
}
```

------------------------------------------------------------------------

## 1.4 XOR

### 1. Concept & Core Intuition

XOR is `1` when the two bits are **different**.

``` text
a b | a ^ b
----+------
0 0 |   0
0 1 |   1
1 0 |   1
1 1 |   0
```

Important properties:

``` text
x ^ 0 = x              identity
x ^ x = 0              self-cancellation
a ^ b = b ^ a          commutative
(a ^ b) ^ c = a ^ (b ^ c)   associative
```

Therefore:

``` text
a ^ b = c

XOR both sides by a:

a ^ a ^ b = a ^ c
0 ^ b     = a ^ c

b = a ^ c
```

### 2. Dry Run & Step-by-Step Diagram

``` text
12 = 1100
10 = 1010

     1100
   ^ 1010
   ------
     0110 = 6
```

Recover an unknown:

``` text
a = 5
c = 6
a ^ b = c

b = a ^ c
  = 5 ^ 6

5 = 101
6 = 110
    ---
    011 = 3
```

Check:

``` text
5 ^ 3 = 6
101
011
---
110
```

**Important:** XOR is not ordinary arithmetic and does not generally
distribute over addition:

``` text
a ^ (b + c) != (a ^ b) + (a ^ c)
```

### 3. Pseudocode

``` text
FUNCTION recoverB(a, c):
    RETURN a XOR c
```

### 4. Complete C++ Code

``` cpp
#include <iostream>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    long long a, c;
    cin >> a >> c;

    const long long b = a ^ c;
    cout << b << '\n';

    return 0;
}
```

------------------------------------------------------------------------

## 1.5 NOT

### 1. Concept & Core Intuition

NOT `~` flips every stored bit:

``` text
0 -> 1
1 -> 0
```

For a fixed-width unsigned integer:

``` text
x = 00001101
~x  = 11110010
```

### Edge Case / Warning

C++ does **not** flip only the visible binary digits.

`~x` flips all bits of the promoted integer type. For signed integers
this often produces a negative value because signed integers normally
use two's-complement representation.

If you only want to flip the lowest `k` bits:

``` cpp
x ^= ((1ULL << k) - 1);
```

provided `k < 64`.

### 2. Dry Run & Step-by-Step Diagram

Flip only the lowest four bits of `13`:

``` text
x       = 1101
mask    = 1111
x^mask  = 0010 = 2
```

### 3. Pseudocode

``` text
mask = 2^k - 1
answer = x XOR mask
```

### 4. Complete C++ Code

``` cpp
#include <iostream>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    unsigned long long x;
    int k;
    cin >> x >> k;

    if (k < 0 || k > 64) return 0;

    unsigned long long mask =
        (k == 64 ? ~0ULL : ((1ULL << k) - 1ULL));

    cout << (x ^ mask) << '\n';
    return 0;
}
```

------------------------------------------------------------------------

## 1.6 Left and Right Shift

### 1. Concept & Core Intuition

For a non-negative integer `x`:

``` text
x << y  ~= x * 2^y
x >> y  = floor(x / 2^y)
```

The multiplication interpretation is valid only when the shifted value
is representable.

### 2. Dry Run & Step-by-Step Diagram

For `x = 11`:

``` text
11 = 1011

1011 << 1 = 10110  = 22
1011 << 2 = 101100 = 44
```

Right shift:

``` text
1011 >> 1 = 0101 = 5
1011 >> 2 = 0010 = 2
1011 >> 3 = 0001 = 1
```

### 3. Pseudocode

``` text
left  = x shifted left by y
right = x shifted right by y
```

### 4. Complete C++ Code

``` cpp
#include <iostream>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    unsigned long long x;
    int y;
    cin >> x >> y;

    if (y < 0 || y >= 64) return 0;

    cout << "left  = " << (x << y) << '\n';
    cout << "right = " << (x >> y) << '\n';

    return 0;
}
```

------------------------------------------------------------------------

## 1.7 Data Types, Bit Capacity, and Overflow

### 1. Concept & Core Intuition

Typical competitive-programming implementations provide:

``` text
int                 commonly 32 bits
long long           at least 64 bits
unsigned long long  at least 64 bits
```

Use:

``` cpp
1LL << bit
```

instead of:

``` cpp
1 << bit
```

when the result may not fit a signed 32-bit `int`.

Example:

``` text
1 << 30     usually okay for signed 32-bit int
1 << 31     problematic / not representable as positive signed int
1LL << 31   safely represented in long long
```

### Shift Warnings

Never blindly do:

``` cpp
1LL << 63
```

on a signed `long long`.

Prefer unsigned masks when using the top bit:

``` cpp
1ULL << 63
```

Also avoid shift counts `>=` the width of the type.

### 2. Dry Run & Step-by-Step Diagram

``` text
1LL << 31

1 * 2^31
= 2147483648
```

But typical signed 32-bit maximum is:

``` text
2^31 - 1
= 2147483647
```

So the value needs a wider type.

### 3. Pseudocode

``` text
IF bit can be >= 31:
    use 64-bit literal 1LL or 1ULL
```

### 4. Complete C++ Code

``` cpp
#include <cstdint>
#include <iostream>
#include <limits>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cout << "int bits: "
         << numeric_limits<unsigned int>::digits << '\n';

    cout << "unsigned long long bits: "
         << numeric_limits<unsigned long long>::digits << '\n';

    const long long x = (1LL << 31);
    cout << x << '\n';

    return 0;
}
```

------------------------------------------------------------------------

## 1.8 Operator Precedence Pitfalls

### 1. Concept & Core Intuition

When mixing shifts, bitwise operations, and comparisons, write
parentheses explicitly.

Prefer:

``` cpp
if (((mask >> pos) & 1LL) == 0) {
    ...
}
```

instead of relying on memorized precedence.

Another safe style:

``` cpp
bool isSet = ((mask >> pos) & 1LL) != 0;
```

### 2. Dry Run & Step-by-Step Diagram

``` text
mask = 13 = 1101
pos  = 2

mask >> 2
= 0011

0011 & 0001
= 1

bit is set
```

The intended grouping is visibly:

``` text
((mask >> pos) & 1LL)
```

### 3. Pseudocode

``` text
bit = (mask shifted right by pos) AND 1

IF bit == 0:
    unset
ELSE:
    set
```

### 4. Complete C++ Code

``` cpp
#include <iostream>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    long long mask;
    int pos;
    cin >> mask >> pos;

    const bool isSet = (((mask >> pos) & 1LL) != 0);

    cout << (isSet ? "SET" : "UNSET") << '\n';
    return 0;
}
```

------------------------------------------------------------------------

## 1.9 Powers of Two Reference

### 1. Concept & Core Intuition

``` text
2^0  = 1
2^1  = 2
2^2  = 4
2^3  = 8
2^4  = 16
2^5  = 32
2^6  = 64
2^7  = 128
2^8  = 256
2^9  = 512
2^10 = 1024
2^11 = 2048
2^12 = 4096
2^13 = 8192
2^14 = 16384
2^15 = 32768
2^16 = 65536

2^30 = 1,073,741,824
2^31 = 2,147,483,648
```

Useful memory anchors:

``` text
2^10 ~= 10^3
2^20 ~= 10^6
2^30 ~= 10^9
```

### 2. Dry Run & Step-by-Step Diagram

``` text
1 << 5

000001
   ↓ shift 5 places
100000

= 32
= 2^5
```

### 3. Pseudocode

``` text
FOR i from 0 to 16:
    PRINT i, 2^i
```

### 4. Complete C++ Code

``` cpp
#include <iostream>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    for (int i = 0; i <= 16; ++i) {
        cout << "2^" << i << " = " << (1LL << i) << '\n';
    }

    cout << "2^30 = " << (1LL << 30) << '\n';
    cout << "2^31 = " << (1LL << 31) << '\n';

    return 0;
}
```

------------------------------------------------------------------------

# Section 2 - Bitmasking & Set Representation

## 2.1 Representing a Set with an Integer

### 1. Concept & Core Intuition

Suppose:

``` text
index:  0  1  2  3  4
value:  1  3  7  5 10
```

Use one bit per index:

``` text
bit = 1 -> element included
bit = 0 -> element excluded
```

Subset `{1,5,10}` means indices `{0,3,4}` are selected:

``` text
index:  4 3 2 1 0
mask :  1 1 0 0 1
        -----------
        11001₂ = 25
```

Subset `{3,7,5}` uses indices `{1,2,3}`:

``` text
01110₂ = 14
```

The integer is not storing the element values themselves. It stores
**which positions are selected**.

### 2. Dry Run & Step-by-Step Diagram

``` text
values = [1, 3, 7, 5, 10]
mask   = 25 = 11001₂

i=0 -> bit 0 = 1 -> include 1
i=1 -> bit 1 = 0 -> exclude 3
i=2 -> bit 2 = 0 -> exclude 7
i=3 -> bit 3 = 1 -> include 5
i=4 -> bit 4 = 1 -> include 10

subset = {1,5,10}
```

### 3. Pseudocode

``` text
FOR i = 0 TO n-1:
    IF bit i of mask is 1:
        include a[i]
```

### 4. Complete C++ Code

``` cpp
#include <iostream>
#include <vector>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    vector<int> a{1, 3, 7, 5, 10};
    long long mask = 25;

    cout << "{ ";

    for (int i = 0; i < static_cast<int>(a.size()); ++i) {
        if (((mask >> i) & 1LL) != 0) {
            cout << a[i] << ' ';
        }
    }

    cout << "}\n";
    return 0;
}
```

------------------------------------------------------------------------

## 2.2 Check / Set / Clear / Toggle a Bit

### 1. Concept & Core Intuition

``` text
Check bit i:   (mask >> i) & 1
Set bit i:     mask |  (1 << i)
Clear bit i:   mask & ~(1 << i)
Toggle bit i:  mask ^  (1 << i)
```

### 2. Dry Run & Step-by-Step Diagram

Take:

``` text
mask = 8 = 1000
```

Set bit `1`:

``` text
1 << 1 = 0010

1000
0010
----
1010 = 10
```

Clear bit `3`:

``` text
mask       = 1010
1 << 3     = 1000
~(1 << 3)  = ...0111

1010
0111
----
0010
```

Toggle bit `0`:

``` text
0010
0001
----
0011
```

### 3. Pseudocode

``` text
FUNCTION isSet(mask, i):
    RETURN ((mask >> i) AND 1) == 1

FUNCTION setBit(mask, i):
    RETURN mask OR (1 << i)

FUNCTION clearBit(mask, i):
    RETURN mask AND NOT(1 << i)

FUNCTION toggleBit(mask, i):
    RETURN mask XOR (1 << i)
```

### 4. Complete C++ Code

``` cpp
#include <cstdint>
#include <iostream>
using namespace std;

bool isSet(uint64_t mask, int i) {
    return ((mask >> i) & 1ULL) != 0;
}

uint64_t setBit(uint64_t mask, int i) {
    return mask | (1ULL << i);
}

uint64_t clearBit(uint64_t mask, int i) {
    return mask & ~(1ULL << i);
}

uint64_t toggleBit(uint64_t mask, int i) {
    return mask ^ (1ULL << i);
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    uint64_t mask;
    int i;
    cin >> mask >> i;

    cout << "check  : " << isSet(mask, i) << '\n';
    cout << "set    : " << setBit(mask, i) << '\n';
    cout << "clear  : " << clearBit(mask, i) << '\n';
    cout << "toggle : " << toggleBit(mask, i) << '\n';

    return 0;
}
```

------------------------------------------------------------------------

## 2.3 Union and Intersection

### 1. Concept & Core Intuition

For masks representing subsets of the same universe:

``` text
A union B        <=> A | B
A intersection B <=> A & B
```

Why?

At each bit:

``` text
Union:
present in A OR present in B

Intersection:
present in A AND present in B
```

### 2. Dry Run & Step-by-Step Diagram

From the notes:

``` text
A = 25 = 11001
B = 14 = 01110
```

Union:

``` text
  11001
| 01110
-------
  11111 = 31
```

Intersection:

``` text
  11001
& 01110
-------
  01000 = 8
```

### 3. Pseudocode

``` text
unionMask = A OR B
intersectionMask = A AND B
```

### 4. Complete C++ Code

``` cpp
#include <iostream>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    long long a, b;
    cin >> a >> b;

    cout << "union mask        = " << (a | b) << '\n';
    cout << "intersection mask = " << (a & b) << '\n';

    return 0;
}
```

------------------------------------------------------------------------

## 2.4 Generate All Subsets

### 1. Concept & Core Intuition

Every element has two choices:

``` text
take     -> 1
not take -> 0
```

For `N` elements:

``` text
2 * 2 * ... * 2 = 2^N
```

Therefore every integer:

``` text
0 ... (2^N - 1)
```

represents exactly one subset.

### 2. Dry Run & Step-by-Step Diagram

For:

``` text
a = [2,3,5]
N = 3
```

``` text
mask   decision     subset
--------------------------------
000    NT NT NT     {}
001    T  NT NT     {2}
010    NT T  NT     {3}
011    T  T  NT     {2,3}
100    NT NT T      {5}
101    T  NT T      {2,5}
110    NT T  T      {3,5}
111    T  T  T      {2,3,5}
```

Complexity:

``` text
2^N masks
N bit checks per mask

O(N * 2^N)
```

### 3. Pseudocode

``` text
FOR mask = 0 TO (2^N)-1:

    currentSubset = empty

    FOR i = 0 TO N-1:
        IF bit i of mask is set:
            add a[i] to currentSubset

    output currentSubset
```

### 4. Complete C++ Code

``` cpp
#include <iostream>
#include <vector>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<long long> a(n);
    for (auto& x : a) cin >> x;

    // This enumeration is practical only for small n.
    const unsigned long long totalMasks = (1ULL << n);

    for (unsigned long long mask = 0; mask < totalMasks; ++mask) {
        cout << "{ ";

        for (int i = 0; i < n; ++i) {
            if (((mask >> i) & 1ULL) != 0) {
                cout << a[i] << ' ';
            }
        }

        cout << "}\n";
    }

    return 0;
}
```

------------------------------------------------------------------------

## 2.5 Included vs Excluded Elements

### 1. Concept & Core Intuition

For each position:

``` text
bit = 1 -> included
bit = 0 -> excluded
```

This lets one mask describe both groups.

### 2. Dry Run & Step-by-Step Diagram

``` text
a    = [10,20,30,40]
mask = 0101₂
```

``` text
i=0: bit=1 -> included -> 10
i=1: bit=0 -> excluded -> 20
i=2: bit=1 -> included -> 30
i=3: bit=0 -> excluded -> 40

included = [10,30]
excluded = [20,40]
```

### 3. Pseudocode

``` text
FOR every index i:
    IF bit i is set:
        add a[i] to included
    ELSE:
        add a[i] to excluded
```

### 4. Complete C++ Code

``` cpp
#include <iostream>
#include <vector>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    vector<int> a{10, 20, 30, 40};
    unsigned mask = 0b0101;

    vector<int> included;
    vector<int> excluded;

    for (int i = 0; i < static_cast<int>(a.size()); ++i) {
        if (((mask >> i) & 1U) != 0) {
            included.push_back(a[i]);
        } else {
            excluded.push_back(a[i]);
        }
    }

    cout << "Included: ";
    for (int x : included) cout << x << ' ';

    cout << "\nExcluded: ";
    for (int x : excluded) cout << x << ' ';

    cout << '\n';
    return 0;
}
```

------------------------------------------------------------------------

# Section 3 - C++ STL `std::bitset`

## 3.1 Declaration and Construction

### 1. Concept & Core Intuition

`std::bitset<N>` stores exactly `N` bits.

Examples:

``` cpp
bitset<4> x;
bitset<4> a(7);
bitset<4> b("1010");
bitset<4> c(0b1010);
```

`to_string()` converts it to a printable binary string.

### 2. Dry Run & Step-by-Step Diagram

``` cpp
bitset<4> x(7);
```

``` text
7 = 111₂

stored in 4 bits:

0111
```

String construction:

``` cpp
bitset<4> y("1010");
```

``` text
y = 1010
```

Binary literal:

``` cpp
bitset<4> z(0b1010);
```

``` text
z = 1010
```

### 3. Pseudocode

``` text
create bitset of length 4 from integer 7
print bitset
convert bitset to string
```

### 4. Complete C++ Code

``` cpp
#include <bitset>
#include <iostream>
#include <string>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    bitset<4> a(7);
    bitset<4> b(string("1010"));
    bitset<4> c(0b1010);

    cout << a << '\n';
    cout << b.to_string() << '\n';
    cout << c << '\n';

    return 0;
}
```

------------------------------------------------------------------------

## 3.2 Indexing and Manipulation

### 1. Concept & Core Intuition

`bitset` can be accessed like an array.

Important:

``` text
x[0] = least-significant/rightmost bit
```

Useful methods:

``` cpp
x.set(i);       // set bit i
x.reset(i);     // clear bit i
x.flip(i);      // toggle bit i
x.test(i);      // read bit i with bounds checking
x.count();      // number of set bits
x.any();        // any bit set?
x.none();       // no bits set?
x.all();        // all bits set?
```

### 2. Dry Run & Step-by-Step Diagram

``` text
x = 1010

index: 3 2 1 0
bit:   1 0 1 0
```

Set `x[2] = 1`:

``` text
before: 1010
             ^
index 2 currently 0

after : 1110
```

### 3. Pseudocode

``` text
create x = 1010
set bit 2
print x
print number of set bits
```

### 4. Complete C++ Code

``` cpp
#include <bitset>
#include <iostream>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    bitset<4> x("1010");

    x[2] = 1;

    cout << "x       = " << x << '\n';
    cout << "count   = " << x.count() << '\n';
    cout << "bit[2]  = " << x.test(2) << '\n';

    return 0;
}
```

------------------------------------------------------------------------

## 3.3 Large Fixed-Size Bitsets

### 1. Concept & Core Intuition

A normal integer gives only a fixed machine-sized set of bits.

But:

``` cpp
bitset<100> x;
```

stores 100 bits.

This does **not** make it an arbitrary-precision integer. It gives a
fixed compile-time-size bit container supporting bit operations.

### 2. Dry Run & Step-by-Step Diagram

``` text
bitset<100>

index:
99 ........................................ 2 1 0

Each position independently stores:
0 or 1
```

### 3. Pseudocode

``` text
create 100-bit container
set bit 99
set bit 0
print count
```

### 4. Complete C++ Code

``` cpp
#include <bitset>
#include <iostream>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    bitset<100> bits;

    bits.set(99);
    bits.set(0);

    cout << bits << '\n';
    cout << "set bits = " << bits.count() << '\n';

    return 0;
}
```

------------------------------------------------------------------------

# Section 4 - Deep-Dive Applications & Master Patterns

# Application 1 - Cyclic Property of Bits

## 1. Concept & Core Intuition

Write consecutive integers vertically:

``` text
number   b3 b2 b1 b0
--------------------
0         0  0  0  0
1         0  0  0  1
2         0  0  1  0
3         0  0  1  1
4         0  1  0  0
5         0  1  0  1
6         0  1  1  0
7         0  1  1  1
8         1  0  0  0
```

Each bit column is periodic.

For bit `i`:

``` text
0 repeats 2^i times
1 repeats 2^i times

period = 2^(i+1)
```

Examples:

``` text
bit 0:
0 1 | 0 1 | 0 1 ...
period = 2

bit 1:
0 0 1 1 | 0 0 1 1 ...
period = 4

bit 2:
0 0 0 0 1 1 1 1 | ...
period = 8
```

This converts a huge row-by-row simulation into **column-wise
counting**.

------------------------------------------------------------------------

## 2. Dry Run & Step-by-Step Diagram

Count the number of `1`s at bit `1` among `0..7`.

``` text
number:  0 1 2 3 4 5 6 7
bit 1 :  0 0 1 1 0 0 1 1
```

For bit `1`:

``` text
half   = 2^1 = 2
period = 2^2 = 4

total numbers = 8
full periods  = 8 / 4 = 2

ones per full period = 2

ones = 2 periods * 2
     = 4
```

General range is `0..x`, so:

``` text
total = x + 1
```

For bit `i`:

``` text
half   = 2^i
period = 2^(i+1)

fullPeriods = total / period
remainder   = total % period

ones from full periods:
fullPeriods * half

extra ones:
max(0, remainder - half)
```

Therefore:

``` text
onesAtBit(x,i)
=
((x+1) / 2^(i+1)) * 2^i
+
max(0, ((x+1) % 2^(i+1)) - 2^i)
```

------------------------------------------------------------------------

## 3. Pseudocode

``` text
FUNCTION onesAtBit(x, bit):
    total = x + 1

    half = 2^bit
    period = 2^(bit+1)

    fullPeriods = total / period
    remainder = total MOD period

    answer = fullPeriods * half

    IF remainder > half:
        answer += remainder - half

    RETURN answer


FUNCTION sumOfBits(x):
    answer = 0

    FOR every relevant bit:
        answer += onesAtBit(x, bit)

    RETURN answer
```

------------------------------------------------------------------------

## 4. Complete C++ Code

``` cpp
#include <algorithm>
#include <cstdint>
#include <iostream>
using namespace std;

using int64 = long long;
using i128 = __int128_t;

// Number of 1s at one bit among integers [0, x].
i128 onesAtBit(int64 x, int bit) {
    if (x < 0) return 0;

    const i128 total = static_cast<i128>(x) + 1;
    const i128 half = static_cast<i128>(1) << bit;
    const i128 period = half << 1;

    const i128 fullPeriods = total / period;
    const i128 remainder = total % period;

    i128 result = fullPeriods * half;

    if (remainder > half) {
        result += remainder - half;
    }

    return result;
}

// Total number of set bits in binary representations of 0..x.
i128 sumOfBits(int64 x) {
    if (x < 0) return 0;

    i128 answer = 0;

    for (int bit = 0; bit <= 62; ++bit) {
        answer += onesAtBit(x, bit);
    }

    return answer;
}

void printInt128(i128 x) {
    if (x == 0) {
        cout << 0;
        return;
    }

    if (x < 0) {
        cout << '-';
        x = -x;
    }

    string s;

    while (x > 0) {
        s.push_back(char('0' + x % 10));
        x /= 10;
    }

    reverse(s.begin(), s.end());
    cout << s;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    long long x;
    cin >> x;

    printInt128(sumOfBits(x));
    cout << '\n';

    return 0;
}
```

------------------------------------------------------------------------

# Application 1A - Small K Precomputation

## Problem

Consider the concatenation of ordinary binary representations:

``` text
0, 1, 10, 11, 100, 101, 110, 111, ...
```

The study notes first attack small `K` by explicitly generating enough
binary digits, caching positions of every `1`, then answering the `K`-th
`1`.

## 1. Concept & Core Intuition

For small constraints such as approximately `K <= 10^5`, direct
generation is practical.

Example concatenation:

``` text
number:       0 | 1 | 10 | 11 | 100 | 101 | ...
concatenated: 0   1   10   11   100   101 ...
```

Store:

``` text
positions = all indices where concatenatedString[i] == '1'
```

Then:

``` text
K-th 1 position = positions[K-1]
```

### 2. Dry Run & Step-by-Step Diagram

Using numbers `0..5`:

``` text
0 -> 0
1 -> 1
2 -> 10
3 -> 11
4 -> 100
5 -> 101
```

Concatenate:

``` text
0|1|10|11|100|101
```

Scan:

``` text
character: 0 1 1 0 1 1 1 0 0 1 0 1
           ^ ^ ^   ...
```

Every time the character is `1`, save its position.

### 3. Pseudocode

``` text
FUNCTION getbitstr(x):
    convert x to binary string
    return it

FUNCTION precompute(limit):
    s = empty string

    FOR x from 0 upward:
        s += getbitstr(x)

        IF enough ones have been generated:
            BREAK

    positions = empty

    FOR i in [0 .. length(s)-1]:
        IF s[i] == '1':
            append i to positions

    RETURN positions
```

### 4. Complete C++ Code

``` cpp
#include <algorithm>
#include <iostream>
#include <string>
#include <vector>
using namespace std;

string getbitstr(unsigned long long x) {
    if (x == 0) return "0";

    string s;

    while (x > 0) {
        s.push_back(char('0' + (x & 1ULL)));
        x >>= 1ULL;
    }

    reverse(s.begin(), s.end());
    return s;
}

// Returns 0-based positions of the first needOnes set bits
// in the concatenated binary stream.
vector<unsigned long long> buildOnePositions(size_t needOnes) {
    vector<unsigned long long> positions;
    positions.reserve(needOnes);

    unsigned long long streamPos = 0;

    for (unsigned long long x = 0; positions.size() < needOnes; ++x) {
        const string bits = getbitstr(x);

        for (char c : bits) {
            if (c == '1') {
                positions.push_back(streamPos);

                if (positions.size() == needOnes) {
                    break;
                }
            }

            ++streamPos;
        }

        // If the inner loop stopped immediately after pushing
        // the final required 1, streamPos still points to that
        // character. No later position is needed.
    }

    return positions;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    size_t k;
    cin >> k;

    if (k == 0) return 0;

    const auto positions = buildOnePositions(k);

    cout << positions[k - 1] << '\n';
    return 0;
}
```

------------------------------------------------------------------------

# Application 1B - Large K with Binary Search

## Problem

When `K` becomes very large (the notes escalate to about `10^9`),
constructing the entire binary stream is too expensive.

Instead:

``` text
1. Find which integer contains the K-th set bit.
2. Find which set bit inside that integer it is.
3. Convert that local position into the global concatenated-string position.
```

## 1. Concept & Core Intuition

Define:

``` text
f(x) = total number of 1-bits in numbers [0..x]
```

`f(x)` is monotonic:

``` text
x increases
=> we append another non-negative number of 1s
=> f(x) never decreases
```

Therefore find:

``` text
smallest x such that f(x) >= K
```

with binary search.

Then:

``` text
ones before x = f(x-1)

local rank inside x
= K - f(x-1)
```

Finally find the local rank-th `1` in `binary(x)`.

To compute the global character position, count the total binary digits
before `x`.

For positive integers:

``` text
numbers with bit-length len:
[2^(len-1), 2^len - 1]

count = 2^(len-1)
contribution to character count
= len * 2^(len-1)
```

This yields `totalBitsTill(x)`.

### 2. Dry Run & Step-by-Step Diagram

Suppose:

``` text
K = 7
```

Count total ones:

``` text
0 -> 0     cumulative 0
1 -> 1     cumulative 1
2 -> 10    cumulative 2
3 -> 11    cumulative 4
4 -> 100   cumulative 5
5 -> 101   cumulative 7
```

So the 7-th `1` lies in:

``` text
x = 5
binary(5) = 101
```

Ones before `5`:

``` text
f(4) = 5
```

Local rank:

``` text
7 - 5 = 2
```

The second `1` in `"101"` is at local index `2`.

Digits before number `5`:

``` text
"0"  -> 1
"1"  -> 1
"10" -> 2
"11" -> 2
"100"-> 3

total = 9
```

Therefore global 0-based position:

``` text
9 + 2 = 11
```

### 3. Pseudocode

``` text
FUNCTION find_kth_one(K):
    lo = 0
    hi = 1

    WHILE sumOfBits(hi) < K:
        hi *= 2

    WHILE lo < hi:
        mid = lo + (hi-lo)/2

        IF sumOfBits(mid) >= K:
            hi = mid
        ELSE:
            lo = mid + 1

    RETURN lo


FUNCTION getKthOnePosInNum(x, localK):
    bits = binary representation of x

    FOR i from 0 to length(bits)-1:
        IF bits[i] == '1':
            localK--

            IF localK == 0:
                RETURN i


FUNCTION totalBitsTill(x):
    count total binary digits in representations 0..x
    RETURN count
```

### 4. Complete Modular C++ Implementation

``` cpp
#include <algorithm>
#include <cstdint>
#include <iostream>
#include <string>
using namespace std;

using int64 = long long;
using i128 = __int128_t;

string getbitstr(unsigned long long x) {
    if (x == 0) return "0";

    string s;

    while (x > 0) {
        s.push_back(char('0' + (x & 1ULL)));
        x >>= 1ULL;
    }

    reverse(s.begin(), s.end());
    return s;
}

i128 onesAtBit(int64 x, int bit) {
    if (x < 0) return 0;

    const i128 total = static_cast<i128>(x) + 1;
    const i128 half = static_cast<i128>(1) << bit;
    const i128 period = half << 1;

    i128 answer = (total / period) * half;
    const i128 remainder = total % period;

    if (remainder > half) {
        answer += remainder - half;
    }

    return answer;
}

i128 sumOfBits(int64 x) {
    if (x < 0) return 0;

    i128 answer = 0;

    for (int bit = 0; bit <= 62; ++bit) {
        answer += onesAtBit(x, bit);
    }

    return answer;
}

// Smallest x such that total number of 1s in [0..x] >= k.
int64 find_kth_one(i128 k) {
    int64 lo = 0;
    int64 hi = 1;

    while (sumOfBits(hi) < k) {
        if (hi > (1LL << 61)) {
            hi = (1LL << 62);
            break;
        }

        hi *= 2;
    }

    while (lo < hi) {
        const int64 mid = lo + (hi - lo) / 2;

        if (sumOfBits(mid) >= k) {
            hi = mid;
        } else {
            lo = mid + 1;
        }
    }

    return lo;
}

// localK is 1-based among the set bits of x.
// Returns the 0-based character index inside binary(x).
int getKthOnePosInNum(unsigned long long x, long long localK) {
    const string bits = getbitstr(x);

    for (int i = 0; i < static_cast<int>(bits.size()); ++i) {
        if (bits[i] == '1') {
            --localK;

            if (localK == 0) {
                return i;
            }
        }
    }

    return -1;
}

// Number of characters in:
// binary(0) + binary(1) + ... + binary(x)
//
// This convention represents 0 as "0".
i128 totalBitsTill(unsigned long long x) {
    if (x == 0) return 1;

    i128 total = 1; // binary(0) = "0"

    int len = 1;

    while (len < 64) {
        const unsigned long long start = (1ULL << (len - 1));

        if (start > x) break;

        unsigned long long end;

        if (len == 64) {
            end = ~0ULL;
        } else {
            end = (1ULL << len) - 1ULL;
        }

        end = min(end, x);

        const i128 count =
            static_cast<i128>(end) - start + 1;

        total += count * len;

        if (end == x || len == 63) break;

        ++len;
    }

    return total;
}

void printInt128(i128 x) {
    if (x == 0) {
        cout << 0;
        return;
    }

    string s;

    while (x > 0) {
        s.push_back(char('0' + x % 10));
        x /= 10;
    }

    reverse(s.begin(), s.end());
    cout << s;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    long long kInput;
    cin >> kInput;

    if (kInput <= 0) return 0;

    const i128 k = kInput;

    const int64 number = find_kth_one(k);
    const i128 onesBefore = sumOfBits(number - 1);
    const long long localRank =
        static_cast<long long>(k - onesBefore);

    const int localIndex =
        getKthOnePosInNum(number, localRank);

    // Number of characters before binary(number).
    const i128 charsBefore =
        (number == 0 ? 0 : totalBitsTill(number - 1));

    const i128 globalZeroBasedPosition =
        charsBefore + localIndex;

    cout << "number = " << number << '\n';
    cout << "binary = " << getbitstr(number) << '\n';
    cout << "local index = " << localIndex << '\n';
    cout << "global 0-based position = ";
    printInt128(globalZeroBasedPosition);
    cout << '\n';

    return 0;
}
```

Complexity:

``` text
sumOfBits(x) = O(log x)

binary search:
O(log answer) checks

total:
O(log² answer)
```

------------------------------------------------------------------------

# Application 2 - All-Pair XOR Sum

## Problem

Compute:

``` text
sum over all i < j of:

A[i] ^ A[j]
```

Example:

``` text
A = [1,3,5]
```

## 1. Concept & Core Intuition

Brute force:

``` text
for every pair
    calculate XOR

O(N²)
```

The key observation from the notes:

> Bit expressions are independent at each bit.

At bit `j`, XOR contributes `1` exactly when one number has `0` and the
other has `1`.

Let:

``` text
cnt1 = number of array elements with bit j = 1
cnt0 = N - cnt1
```

Every `0` can pair with every `1`:

``` text
different-bit unordered pairs
= cnt0 * cnt1
```

Each such pair contributes:

``` text
2^j
```

Therefore:

``` text
contribution(j)
=
cnt0 * cnt1 * 2^j
```

Final answer:

``` text
sum contribution(j) over all bits
```

## 2. Dry Run & Step-by-Step Diagram

``` text
A = [1,3,5]

1 = 001
3 = 011
5 = 101
```

Bit table:

``` text
       bit2 bit1 bit0
1        0    0    1
3        0    1    1
5        1    0    1
```

### bit 0

``` text
bits = [1,1,1]

cnt1 = 3
cnt0 = 0

pairs = 3 * 0 = 0
value = 2^0 = 1

contribution = 0
```

### bit 1

``` text
bits = [0,1,0]

cnt1 = 1
cnt0 = 2

pairs = 1 * 2 = 2
value = 2

contribution = 2 * 2 = 4
```

### bit 2

``` text
bits = [0,0,1]

cnt1 = 1
cnt0 = 2

pairs = 2
value = 4

contribution = 2 * 4 = 8
```

Final:

``` text
0 + 4 + 8 = 12
```

Manual verification:

``` text
1 ^ 3 = 2
1 ^ 5 = 4
3 ^ 5 = 6

2 + 4 + 6 = 12
```

## 3. Pseudocode

``` text
answer = 0

FOR bit from 0 to MAX_BIT:
    cnt1 = 0

    FOR x in array:
        IF bit-th bit of x is set:
            cnt1++

    cnt0 = N - cnt1

    pairCount = cnt0 * cnt1

    answer += pairCount * 2^bit

RETURN answer
```

## 4. Complete C++ Code

``` cpp
#include <iostream>
#include <vector>
using namespace std;

using int64 = long long;
using i128 = __int128_t;

i128 allPairXorSum(const vector<unsigned long long>& a) {
    const long long n = static_cast<long long>(a.size());
    i128 answer = 0;

    for (int bit = 0; bit < 64; ++bit) {
        long long cnt1 = 0;

        for (const auto x : a) {
            if (((x >> bit) & 1ULL) != 0) {
                ++cnt1;
            }
        }

        const long long cnt0 = n - cnt1;
        const i128 pairCount =
            static_cast<i128>(cnt0) * cnt1;

        answer += pairCount *
                  (static_cast<i128>(1) << bit);
    }

    return answer;
}

void printInt128(i128 x) {
    if (x == 0) {
        cout << 0;
        return;
    }

    string s;

    while (x > 0) {
        s.push_back(char('0' + x % 10));
        x /= 10;
    }

    reverse(s.begin(), s.end());
    cout << s;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<unsigned long long> a(n);
    for (auto& x : a) cin >> x;

    printInt128(allPairXorSum(a));
    cout << '\n';

    return 0;
}
```

Complexity:

``` text
O(N * B)

B = number of relevant bits
```

For ordinary values up to about `1e9`:

``` text
B ~= 31
```

------------------------------------------------------------------------

## Extension - AND / OR Contribution

### AND

At one bit:

``` text
AND bit = 1
only when both bits are 1
```

If `cnt1` numbers have that bit:

``` text
unordered contributing pairs
= cnt1 * (cnt1 - 1) / 2
```

### OR

OR bit is `0` only for a `0-0` pair.

``` text
totalPairs = N(N-1)/2
zeroZeroPairs = cnt0(cnt0-1)/2

OR-one pairs
= totalPairs - zeroZeroPairs
```

### Bit-Level Decision Table

``` text
operation    output bit 1 when...
---------------------------------------------
XOR          bits are different
AND          both are 1
OR           at least one is 1
```

------------------------------------------------------------------------

# Application 3 - Operation Decoding / Conservation of Bits

## Problem

The source notes study a repeated pair operation based on:

``` text
newA = A | B
newB = A & B
```

and use it to maximize:

``` text
sum(A[i]^2)
```

> **Important source clarification:** the prompt text says\
> `A[j] <- A[j] & A[j]`, which would simply leave `A[j]` unchanged.\
> The conservation identity and the supplied study material use the pair
> transformation `(A|B, A&B)`, i.e. the second value depends on both
> original values. The derivation below follows that source operation.

## 1. Concept & Core Intuition

The fundamental identity is:

``` text
A + B = (A | B) + (A & B)
```

### Why?

Look at one bit of weight `2^k`.

``` text
A B | OR AND | number of 1-copies before/after
----+--------+---------------------------------
0 0 |  0   0 | 0 -> 0
0 1 |  1   0 | 1 -> 1
1 0 |  1   0 | 1 -> 1
1 1 |  1   1 | 2 -> 2
```

Therefore each bit position preserves its total number of set-bit
copies.

Consequences:

``` text
1. Sum of the array is conserved.
2. Count of set bits at every bit position is conserved.
3. Repeated operations can redistribute those bit copies.
```

Now maximize:

``` text
sum x_i^2
```

For fixed total, making values more unequal increases the sum of
squares.

For two non-negative values:

``` text
x + y = S

x² + y²
= x² + (S-x)²
```

The maximum occurs toward an extreme, not near equality.

Therefore:

> Pack as many available set bits as possible into the same constructed
> number, then repeat.

This is the notes' "free-fall" / bit-packing intuition.

## 2. Dry Run & Step-by-Step Diagram

Identity example:

``` text
A = 12 = 1100
B = 10 = 1010
```

OR:

``` text
1100
1010
----
1110 = 14
```

AND:

``` text
1100
1010
----
1000 = 8
```

Check sum:

``` text
before = 12 + 10 = 22
after  = 14 + 8  = 22
```

Bit-copy conservation:

``` text
bit3:
12 -> 1
10 -> 1
copies before = 2

14 -> 1
 8 -> 1
copies after = 2
```

### Why extremes help squares

``` text
x + y = 5

(0,5): 0² + 5² = 25
(1,4): 1² + 4² = 17
(2,3): 2² + 3² = 13
```

### Constructing values from conserved bit counts

Suppose the array has bit counts:

``` text
bit2 count = 2
bit1 count = 1
bit0 count = 3
```

First constructed number:

``` text
take one available copy of every bit

bit2 -> take
bit1 -> take
bit0 -> take

x1 = 111₂ = 7

remaining:
bit2 = 1
bit1 = 0
bit0 = 2
```

Second:

``` text
x2 = 101₂ = 5

remaining:
bit2 = 0
bit1 = 0
bit0 = 1
```

Third:

``` text
x3 = 001₂ = 1
```

This deliberately stacks high-value bit copies together.

## 3. Pseudocode

``` text
count[bit] = 0

FOR each value x:
    FOR each bit:
        IF bit is set in x:
            count[bit]++

answer = 0

REPEAT N times:
    x = 0

    FOR each bit:
        IF count[bit] > 0:
            set this bit in x
            count[bit]--

    answer += x * x

RETURN answer
```

## 4. Complete C++ Code

``` cpp
#include <array>
#include <cstdint>
#include <iostream>
#include <vector>
using namespace std;

using u64 = unsigned long long;
using u128 = __uint128_t;

u128 maximizeSquareSum(const vector<u64>& a) {
    array<long long, 64> count{};

    for (const u64 x : a) {
        for (int bit = 0; bit < 64; ++bit) {
            if (((x >> bit) & 1ULL) != 0) {
                ++count[bit];
            }
        }
    }

    u128 answer = 0;

    for (size_t iteration = 0; iteration < a.size(); ++iteration) {
        u64 x = 0;

        for (int bit = 0; bit < 64; ++bit) {
            if (count[bit] > 0) {
                x |= (1ULL << bit);
                --count[bit];
            }
        }

        answer += static_cast<u128>(x) * x;
    }

    return answer;
}

void printU128(u128 x) {
    if (x == 0) {
        cout << 0;
        return;
    }

    string s;

    while (x > 0) {
        s.push_back(char('0' + x % 10));
        x /= 10;
    }

    reverse(s.begin(), s.end());
    cout << s;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<u64> a(n);
    for (auto& x : a) cin >> x;

    printU128(maximizeSquareSum(a));
    cout << '\n';

    return 0;
}
```

### Recognition Signal

Whenever a statement says:

``` text
"apply this operation any number of times"
```

do not immediately simulate.

Ask:

``` text
What does ONE operation preserve?
```

Try:

``` text
sum
XOR
AND/OR relationship
count of set bits at each position
parity
multiset property
```

------------------------------------------------------------------------

# Application 4 - Highest-to-Lowest Bit Greedy

## Problem

Choose `x` elements from an array so that their bitwise AND is
maximized.

## 1. Concept & Core Intuition

For the AND of selected numbers to contain bit `k`:

``` text
EVERY selected number must contain bit k.
```

So if we want to set bit `k` in the answer, we must still have at least
`x` candidate elements with that bit.

Why process high -\> low?

``` text
2^k > 2^0 + 2^1 + ... + 2^(k-1)
```

because:

``` text
2^0 + ... + 2^(k-1)
= 2^k - 1
```

Thus obtaining bit `k` is more valuable than all lower bits combined.

So greedily decide the most significant possible bit first.

## 2. Dry Run & Step-by-Step Diagram

Example:

``` text
A = [1,3,5,6]
x = 2
```

Binary:

``` text
      bit2 bit1 bit0
1       0    0    1
3       0    1    1
5       1    0    1
6       1    1    0
```

Start:

``` text
candidates = [1,3,5,6]
answer = 000
```

### bit 2

Candidates having bit 2:

``` text
5 = 101
6 = 110

count = 2 >= x
```

Accept:

``` text
answer = 100
candidates = [5,6]
```

### bit 1

Among `[5,6]`:

``` text
5 -> bit1 = 0
6 -> bit1 = 1

count = 1 < 2
```

Reject bit 1.

``` text
answer remains 100
candidates remain [5,6]
```

### bit 0

``` text
5 -> 1
6 -> 0

count = 1 < 2
```

Reject.

Final:

``` text
answer = 100₂ = 4
```

Indeed:

``` text
5 & 6

101
110
---
100 = 4
```

## 3. Pseudocode

``` text
candidates = all elements
answer = 0

FOR bit from HIGH down to 0:

    nextCandidates = all candidate values
                     whose current bit is 1

    IF size(nextCandidates) >= x:
        set current bit in answer
        candidates = nextCandidates

RETURN answer
```

## 4. Complete C++ Code

``` cpp
#include <iostream>
#include <vector>
using namespace std;

unsigned long long maximizeAndOfX(
    const vector<unsigned long long>& a,
    int x
) {
    vector<unsigned long long> candidates = a;
    unsigned long long answer = 0;

    for (int bit = 63; bit >= 0; --bit) {
        vector<unsigned long long> next;
        next.reserve(candidates.size());

        for (const auto value : candidates) {
            if (((value >> bit) & 1ULL) != 0) {
                next.push_back(value);
            }
        }

        if (static_cast<int>(next.size()) >= x) {
            answer |= (1ULL << bit);
            candidates = move(next);
        }
    }

    return answer;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, x;
    cin >> n >> x;

    vector<unsigned long long> a(n);
    for (auto& value : a) cin >> value;

    if (x < 1 || x > n) return 0;

    cout << maximizeAndOfX(a, x) << '\n';
    return 0;
}
```

Complexity:

``` text
O(B * N)
```

with a small constant number of bit levels.

------------------------------------------------------------------------

# Application 5 - Shifted XOR Reconstruction

## Problem Pattern

The second Bit Manipulation note includes expressions of the form:

``` text
x ^ (x << 1) ^ (x << 2) ^ ... ^ (x << k)
```

The key is to align shifted copies and reason **column by column**.

## 1. Concept & Core Intuition

Suppose:

``` text
x = abcd
```

Then:

``` text
x       =   a b c d
x << 1  = a b c d 0
x << 2  = a b c d 0 0
```

XOR each vertical column independently.

This can turn an apparently complicated integer equation into small XOR
equations between individual bits.

Use:

``` text
z ^ z = 0
z ^ 0 = z
```

to recover unknown bits.

## 2. Dry Run & Step-by-Step Diagram

Suppose aligned columns imply:

``` text
c ^ d = 0
```

and we know:

``` text
d = 1
```

Then:

``` text
c ^ 1 = 0
=> c = 1
```

Next column:

``` text
b ^ c ^ d = 0

b ^ 1 ^ 1 = 0
b ^ 0 = 0

b = 0
```

Next:

``` text
a ^ b ^ c = 0

a ^ 0 ^ 1 = 0

a ^ 1 = 0

a = 1
```

So we reconstruct:

``` text
a=1, b=0, c=1, d=1
```

The important contest habit is:

``` text
DO NOT expand the whole expression numerically first.

ALIGN
↓
ONE COLUMN
↓
XOR EQUATION
↓
RECOVER BIT
```

## 3. Pseudocode

``` text
write x as unknown bits

align:
x
x shifted by 1
x shifted by 2
...
x shifted by k

FOR each output column:
    XOR all participating input bits

    compare with known output bit

    solve the resulting XOR equation
```

## 4. Complete C++ Code - Evaluate the Shifted XOR

``` cpp
#include <iostream>
using namespace std;

unsigned long long shiftedXor(
    unsigned long long x,
    int k
) {
    unsigned long long answer = 0;

    for (int shift = 0; shift <= k; ++shift) {
        if (shift >= 64) break;
        answer ^= (x << shift);
    }

    return answer;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    unsigned long long x;
    int k;
    cin >> x >> k;

    cout << shiftedXor(x, k) << '\n';
    return 0;
}
```

> Exact reconstruction code depends on the constraints and which side of
> the equation is known. The reusable technique from the notes is the
> **column-wise XOR equation**, not one universal inverse routine.

------------------------------------------------------------------------

# Section 5 - Pattern & Problem Selection Decision Matrix

## 1. Concept & Core Intuition

The final notes group bit problems by what the query is asking for:
pairs, subarrays, an optimal `x`, repeated operations, or per-bit range
information.

Use the query shape to choose the technique.

## 2. Decision Matrix

  ---------------------------------------------------------------------------------------------
  Problem signal      First technique to  Core observation                   Typical complexity
                      consider                                            
  ------------------- ------------------- ------------------------------- ---------------------
  Represent a small   Bitmask             One bit per element                      `O(1)` state
  subset                                                                             operations

  Generate all        Mask enumeration    `2^N` take/not-take states                 `O(N*2^N)`
  subsets                                                                 

  Union /             OR / AND            Membership is per bit                          `O(1)`
  intersection of                                                         
  encoded sets                                                            

  Find best pair for  Binary Trie         Prefer opposite bit high-to-low              `O(N*B)`
  XOR                                                                     

  Maximize AND of     High-to-low         AND bit requires bit in every                `O(N*B)`
  chosen values       greedy/filter       selected value                  

  Maximize OR under   High-to-low /       High bits dominate lower bits          often `O(N*B)`
  bit decisions       feasibility greedy,                                 
                      problem-dependent                                   

  Sum XOR of all      Bit contribution    Different bits create XOR `1`                `O(N*B)`
  pairs                                                                   

  Sum AND of all      Bit contribution    Both bits must be `1`                        `O(N*B)`
  pairs                                                                   

  Sum OR of all pairs Bit contribution    Exclude only `0-0` pairs                     `O(N*B)`

  Count set bits in   Cyclic bit counting bit `i` has period `2^(i+1)`                   `O(B)`
  `0..X`                                                                  

  K-th event in       Counting + binary   Prefix count is monotonic           `O(B log answer)`
  monotonic bit       search                                              
  stream                                                                  

  Repeated OR/AND     Conservation /      Per-bit copies may be conserved      usually `O(N*B)`
  transformation      invariant                                                    construction

  XOR of subarray     Prefix XOR          `xor(L..R)=pref[R]^pref[L-1]`            `O(1)` query

  Sum/count XOR       Prefix XOR +        Transform subarray to               problem-dependent
  subarrays           frequency map/trie  prefix-pair relation            

  Static range        Sparse Table        AND/OR are associative +          build `O(N log N)`,
  AND/OR/idempotent                       idempotent                               query `O(1)`
  query                                                                   

  Dynamic range bit   Segment Tree        Store per-bit/range aggregate          `O(log N)` per
  queries                                                                          update/query

  Count bits in       Prefix count per    `pref[bit][R]-pref[bit][L-1]`            `O(B)` query
  static ranges       bit                                                 

  Subset-mask         SOS DP              Aggregate over                             `O(N*2^N)`
  transitions                             submasks/supermasks             

  Distinct OR values  Compress current OR OR only gains bits, limiting    often `O(N*B)` states
  of subarrays        states              distinct transitions            

  Shifted XOR         Align columns       Each output bit is XOR of          `O(B*k)` or better
  equation                                participating input bits        
  ---------------------------------------------------------------------------------------------

------------------------------------------------------------------------

## 3. Visual Decision Tree

``` text
                         BIT PROBLEM
                              |
        +---------------------+----------------------+
        |                                            |
   SMALL N / SUBSET?                           ARRAY / HUGE N?
        |                                            |
     BITMASK                                Can each bit be
  enumerate 2^N                              treated independently?
                                                     |
                                      +--------------+-------------+
                                      |                            |
                                     YES                          NO
                                      |
              +-----------------------+-----------------------+
              |                       |                       |
          ALL PAIRS?                0..X?              MAXIMIZE BINARY
              |                       |                    RESULT?
              |                       |                       |
       CONTRIBUTION             CYCLIC BITS            HIGH -> LOW
                                      |                   GREEDY
                               need K-th/boundary?
                                      |
                                BINARY SEARCH


Repeated operation any number of times?
              |
              v
      ANALYZE ONE OPERATION
              |
              v
       FIND INVARIANT
              |
              v
   CONSERVATION / DIRECT BUILD


Subarray XOR?
      |
      v
 PREFIX XOR
      |
      +--> pair/count relation?
             |
             v
       MAP / TRIE / CONTRIBUTION


Range query per bit?
      |
      +--> static counts -> PREFIX PER BIT
      |
      +--> static idempotent AND/OR -> SPARSE TABLE
      |
      +--> updates -> SEGMENT TREE


Subset/supermask aggregation?
      |
      v
    SOS DP
```

------------------------------------------------------------------------

## 4. Problem-Solving Checklist

Before coding a bit problem, ask:

``` text
[ ] Can I inspect one bit independently?

[ ] Is this actually a subset-selection problem?
    -> mask

[ ] Are values from 0..N involved?
    -> cyclic property

[ ] Is N huge but only ~30/60 bits exist?
    -> count columns, not numbers

[ ] Does the problem ask about every pair?
    -> contribution

[ ] Does it ask for maximum AND/OR/binary value?
    -> try high bit to low bit

[ ] Does an operation repeat arbitrarily?
    -> find what one operation conserves

[ ] Is there a K-th / minimum X / maximum X?
    -> construct a monotonic prefix/check and binary search

[ ] Is XOR over a subarray involved?
    -> prefix XOR

[ ] Are range queries asking about individual bits?
    -> prefix counts / segment tree per bit

[ ] Are shifted copies XORed?
    -> align them vertically and solve column-by-column
```

------------------------------------------------------------------------

# Section 6 - Final Contest Cheat Sheet

## Basic Operations

``` cpp
bool bit = ((x >> i) & 1LL) != 0;  // check
x |=  (1LL << i);                  // set
x &= ~(1LL << i);                  // clear
x ^=  (1LL << i);                  // toggle
```

## XOR

``` text
x ^ 0 = x
x ^ x = 0

a ^ b = b ^ a
(a ^ b) ^ c = a ^ (b ^ c)

a ^ b = c
=> b = a ^ c
```

## Shift

``` text
x << k  ~= x * 2^k
x >> k   = floor(x / 2^k) for non-negative x
```

Use a sufficiently wide unsigned/signed type and avoid invalid shift
counts.

## Bitmask

``` text
N elements
=> 2^N subsets

mask:
0 ... (1<<N)-1
```

## Sets

``` text
union        -> A | B
intersection -> A & B
```

## Cyclic Bits

At bit `i`:

``` text
0 repeated 2^i times
1 repeated 2^i times

period = 2^(i+1)
```

Count in `0..x`:

``` text
total = x+1
half = 2^i
period = 2^(i+1)

ones =
(total/period)*half
+
max(0, total%period-half)
```

## Pair Contribution

``` text
XOR:
cnt0 * cnt1 * 2^bit

AND:
C(cnt1,2) * 2^bit

OR:
(totalPairs - C(cnt0,2)) * 2^bit
```

## Conservation Identity

``` text
A + B = (A | B) + (A & B)
```

Per bit:

``` text
number of copies of that set bit is conserved
```

## High-to-Low Greedy

``` text
2^k > 2^k - 1
    = sum of all lower powers

therefore:
decide important high bits first
```

For maximizing AND of `x` selected elements:

``` text
keep only candidates with current bit
if at least x survive
```

## Prefix XOR

``` text
pref[i] = a[0] ^ ... ^ a[i]

xor(L..R)
=
pref[R] ^ pref[L-1]
```

## Recognition Summary

``` text
SUBSET                -> BITMASK
0..N BIT COUNTS       -> CYCLIC PROPERTY
ALL PAIRS             -> CONTRIBUTION
MAX BINARY VALUE      -> HIGH -> LOW
REPEATED OPERATION    -> INVARIANT / CONSERVATION
K-TH / BOUNDARY       -> COUNT + BINARY SEARCH
SUBARRAY XOR          -> PREFIX XOR
BEST XOR PAIR         -> BINARY TRIE
RANGE BIT COUNTS      -> PREFIX PER BIT
DYNAMIC RANGE         -> SEGMENT TREE
SUBMASK AGGREGATION   -> SOS DP
SHIFTED XOR           -> COLUMN-BY-COLUMN
```

------------------------------------------------------------------------

# Recommended Mastery Order

``` text
1. Binary representation
        ↓
2. &, |, ^, ~, <<, >>
        ↓
3. check / set / clear / toggle
        ↓
4. bitmask = subset
        ↓
5. enumerate all masks
        ↓
6. std::bitset
        ↓
7. cyclic property
        ↓
8. count set bits in 0..X
        ↓
9. binary search on bit-count function
        ↓
10. pair contribution
        ↓
11. XOR algebra
        ↓
12. conservation / operation decoding
        ↓
13. highest-to-lowest greedy
        ↓
14. prefix XOR / tries / per-bit DS
        ↓
15. mixed problems
```

The contest goal is to move from:

``` text
"I know bit manipulation."
```

to:

``` text
"I see all-pair XOR.
Pair brute force is O(N²).
XOR is independent per bit.
At each bit, only 0-1 pairs contribute.
Therefore count zeros and ones."
```

That recognition is **Bit Manipulation Mastery**.
