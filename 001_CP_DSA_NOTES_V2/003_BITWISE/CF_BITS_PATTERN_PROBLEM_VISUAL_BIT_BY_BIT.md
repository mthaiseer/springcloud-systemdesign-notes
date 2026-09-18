# Codeforces Bitwise Forms — Worked Problems

<a id="toc"></a>

> Each form contains worked Codeforces problems. Every problem is kept focused on:
> **how the form applies → bit-by-bit ASCII dry run → formatted C++ solution**.

## Table of Contents

- [Form 1 — Parity / LSB](#form-1)
  - [Problem 1 — 1367B — Even Array](#form-1-problem-1)
  - [Problem 2 — 1475A — Odd Divisor](#form-1-problem-2)
  - [Problem 3 — 1688B — Patchouli’s Magical Talisman](#form-1-problem-3)
  - [Problem 4 — 1805A — We Need the Zero](#form-1-problem-4)

- [Form 2 — Power of Two / Remove Lowest Set Bit](#form-2)
  - [Problem 1 — 1475A — Odd Divisor](#form-2-problem-1)
  - [Problem 2 — 1527A — And Then There Were K](#form-2-problem-2)
  - [Problem 3 — 1567B — MEXor Mixup](#form-2-problem-3)
  - [Problem 4 — 1909B — Make Almost Equal With Mod](#form-2-problem-4)

- [Form 3 — Kth-bit Masking](#form-3)
  - [Problem 1 — 1669H — Maximal AND](#form-3-problem-1)
  - [Problem 2 — 1547D — Co-growing Sequence](#form-3-problem-2)
  - [Problem 3 — 1926D — Vlad and Division](#form-3-problem-3)
  - [Problem 4 — 1042B — Vitamins](#form-3-problem-4)

- [Form 4 — XOR Cancellation](#form-4)
  - [Problem 1 — 1698A — XOR Mixup](#form-4-problem-1)
  - [Problem 2 — 1805A — We Need the Zero](#form-4-problem-2)
  - [Problem 3 — 1516B — AGAGA XOOORRR](#form-4-problem-3)
  - [Problem 4 — 1872E — Data Structures Fan](#form-4-problem-4)

- [Form 5 — Global XOR / Solve for X](#form-5)
  - [Problem 1 — 1805A — We Need the Zero](#form-5-problem-1)
  - [Problem 2 — 1698A — XOR Mixup](#form-5-problem-2)
  - [Problem 3 — 1567B — MEXor Mixup](#form-5-problem-3)
  - [Problem 4 — 1516B — AGAGA XOOORRR](#form-5-problem-4)

- [Form 6 — XOR 1..N Cycle](#form-6)
  - [Problem 1 — 1567B — MEXor Mixup](#form-6-problem-1)
  - [Problem 2 — 1527A — And Then There Were K](#form-6-problem-2)
  - [Problem 3 — 1909B — Make Almost Equal With Mod](#form-6-problem-3)
  - [Problem 4 — 1097B — Petr and a Combination Lock](#form-6-problem-4)

- [Form 7 — Prefix XOR](#form-7)
  - [Problem 1 — 1872E — Data Structures Fan](#form-7-problem-1)
  - [Problem 2 — 1516B — AGAGA XOOORRR](#form-7-problem-2)
  - [Problem 3 — 1698A — XOR Mixup](#form-7-problem-3)
  - [Problem 4 — 1805A — We Need the Zero](#form-7-problem-4)

- [Form 8 — XOR Difference Mask / Hamming Bits](#form-8)
  - [Problem 1 — 1918C — XOR-distance](#form-8-problem-1)
  - [Problem 2 — 1362B — Johnny and His Hobbies](#form-8-problem-2)
  - [Problem 3 — 1421A — XORwice](#form-8-problem-3)
  - [Problem 4 — 1547D — Co-growing Sequence](#form-8-problem-4)

- [Form 9 — Modulo 2^k = Binary Suffix](#form-9)
  - [Problem 1 — 1909B — Make Almost Equal With Mod](#form-9-problem-1)
  - [Problem 2 — 1475A — Odd Divisor](#form-9-problem-2)
  - [Problem 3 — 1527A — And Then There Were K](#form-9-problem-3)
  - [Problem 4 — 1926D — Vlad and Division](#form-9-problem-4)

- [Form 10 — Highest Set Bit / MSB Grouping](#form-10)
  - [Problem 1 — 1420B — Rock and Lever](#form-10-problem-1)
  - [Problem 2 — 1527A — And Then There Were K](#form-10-problem-2)
  - [Problem 3 — 1918C — XOR-distance](#form-10-problem-3)
  - [Problem 4 — 1669H — Maximal AND](#form-10-problem-4)

- [Form 11 — Lowest Set Bit / 2-adic Structure](#form-11)
  - [Problem 1 — 1475A — Odd Divisor](#form-11-problem-1)
  - [Problem 2 — 1909B — Make Almost Equal With Mod](#form-11-problem-2)
  - [Problem 3 — 1527A — And Then There Were K](#form-11-problem-3)
  - [Problem 4 — 1688B — Patchouli’s Magical Talisman](#form-11-problem-4)

- [Form 12 — OR Monotonicity / Required Bits](#form-12)
  - [Problem 1 — 1842B — Tenzing and Books](#form-12-problem-1)
  - [Problem 2 — 1547D — Co-growing Sequence](#form-12-problem-2)
  - [Problem 3 — 1042B — Vitamins](#form-12-problem-3)
  - [Problem 4 — 1829C — Mr. Perfectly Fine](#form-12-problem-4)

- [Form 13 — AND Monotonicity / Maximal AND](#form-13)
  - [Problem 1 — 1669H — Maximal AND](#form-13-problem-1)
  - [Problem 2 — 1514B — AND 0, Sum Big](#form-13-problem-2)
  - [Problem 3 — 1991B — AND Reconstruction](#form-13-problem-3)
  - [Problem 4 — 1903B — StORage room](#form-13-problem-4)

- [Form 14 — Bit Frequency / Majority Per Bit](#form-14)
  - [Problem 1 — 1625A — Ancient Civilization](#form-14-problem-1)
  - [Problem 2 — 1669H — Maximal AND](#form-14-problem-2)
  - [Problem 3 — 1514B — AND 0, Sum Big](#form-14-problem-3)
  - [Problem 4 — 1367B — Even Array](#form-14-problem-4)

- [Form 15 — Bit-by-Bit Constraint Construction](#form-15)
  - [Problem 1 — 1903B — StORage room](#form-15-problem-1)
  - [Problem 2 — 1991B — AND Reconstruction](#form-15-problem-2)
  - [Problem 3 — 1547D — Co-growing Sequence](#form-15-problem-3)
  - [Problem 4 — 1842B — Tenzing and Books](#form-15-problem-4)

- [Form 16 — Pairwise XOR Contribution](#form-16)
  - [Problem 1 — 1421A — XORwice](#form-16-problem-1)
  - [Problem 2 — 1918C — XOR-distance](#form-16-problem-2)
  - [Problem 3 — 1698A — XOR Mixup](#form-16-problem-3)
  - [Problem 4 — 1516B — AGAGA XOOORRR](#form-16-problem-4)

- [Form 17 — Pairwise AND / OR Contribution](#form-17)
  - [Problem 1 — 1514B — AND 0, Sum Big](#form-17-problem-1)
  - [Problem 2 — 1669H — Maximal AND](#form-17-problem-2)
  - [Problem 3 — 1903B — StORage room](#form-17-problem-3)
  - [Problem 4 — 1991B — AND Reconstruction](#form-17-problem-4)

- [Form 18 — Conservation / Operation Decoding](#form-18)
  - [Problem 1 — 1805A — We Need the Zero](#form-18-problem-1)
  - [Problem 2 — 1516B — AGAGA XOOORRR](#form-18-problem-2)
  - [Problem 3 — 1698A — XOR Mixup](#form-18-problem-3)
  - [Problem 4 — 1547D — Co-growing Sequence](#form-18-problem-4)

- [Form 19 — Highest Bit -> Lowest Bit Greedy](#form-19)
  - [Problem 1 — 1669H — Maximal AND](#form-19-problem-1)
  - [Problem 2 — 1918C — XOR-distance](#form-19-problem-2)
  - [Problem 3 — 1527A — And Then There Were K](#form-19-problem-3)
  - [Problem 4 — 1420B — Rock and Lever](#form-19-problem-4)

- [Form 20 — Prefix Counts of Bits](#form-20)
  - [Problem 1 — 1872E — Data Structures Fan](#form-20-problem-1)
  - [Problem 2 — 1625A — Ancient Civilization](#form-20-problem-2)
  - [Problem 3 — 1669H — Maximal AND](#form-20-problem-3)
  - [Problem 4 — 1516B — AGAGA XOOORRR](#form-20-problem-4)

- [Form 21 — Common Binary Prefix / Range AND](#form-21)
  - [Problem 1 — 1527A — And Then There Were K](#form-21-problem-1)
  - [Problem 2 — 1420B — Rock and Lever](#form-21-problem-2)
  - [Problem 3 — 1669H — Maximal AND](#form-21-problem-3)
  - [Problem 4 — 1475A — Odd Divisor](#form-21-problem-4)

- [Form 22 — Complement Within Fixed Width](#form-22)
  - [Problem 1 — 1926D — Vlad and Division](#form-22-problem-1)
  - [Problem 2 — 1421A — XORwice](#form-22-problem-2)
  - [Problem 3 — 1918C — XOR-distance](#form-22-problem-3)
  - [Problem 4 — 1362B — Johnny and His Hobbies](#form-22-problem-4)

- [Form 23 — Subset Enumeration](#form-23)
  - [Problem 1 — 1097B — Petr and a Combination Lock](#form-23-problem-1)
  - [Problem 2 — 550B — Preparing Olympiad](#form-23-problem-2)
  - [Problem 3 — 1042B — Vitamins](#form-23-problem-3)
  - [Problem 4 — 1829C — Mr. Perfectly Fine](#form-23-problem-4)

- [Form 24 — Bitmask as State](#form-24)
  - [Problem 1 — 1042B — Vitamins](#form-24-problem-1)
  - [Problem 2 — 1829C — Mr. Perfectly Fine](#form-24-problem-2)
  - [Problem 3 — 1097B — Petr and a Combination Lock](#form-24-problem-3)
  - [Problem 4 — 550B — Preparing Olympiad](#form-24-problem-4)

- [Form 25 — Submask Enumeration](#form-25)
  - [Problem 1 — 1042B — Vitamins](#form-25-problem-1)
  - [Problem 2 — 1829C — Mr. Perfectly Fine](#form-25-problem-2)
  - [Problem 3 — 550B — Preparing Olympiad](#form-25-problem-3)
  - [Problem 4 — 1097B — Petr and a Combination Lock](#form-25-problem-4)

- [Form 26 — Bitmask DP](#form-26)
  - [Problem 1 — 1042B — Vitamins](#form-26-problem-1)
  - [Problem 2 — 1829C — Mr. Perfectly Fine](#form-26-problem-2)
  - [Problem 3 — 1097B — Petr and a Combination Lock](#form-26-problem-3)
  - [Problem 4 — 550B — Preparing Olympiad](#form-26-problem-4)

---

<a id="form-1"></a>

# Form 1 — Parity / LSB

## Form explained visually

**What this form means:** Only the last binary bit matters. `0` means even, `1` means odd.

**Daily-life mapping — odd/even parking gates**
```text
Car number             Binary tail          Gate
----------------------------------------------------
12 = ...1100               0              EVEN gate
13 = ...1101               1              ODD gate

Number
  │
  ▼
Look only at bit 0
  │
  ├── 0 → EVEN
  └── 1 → ODD
```

**Recognition signal**
```text
odd / even / parity
index parity must match value parity
operation changes parity
divisible by 2
        ↓
Think: LSB
```

**Core tools:** `x & 1`, `(x & 1) == (y & 1)`.

### Bit-by-bit form example

```text
Example: x = 13

x       = 1101
bits      3210
             ↑
           bit 0

x & 1:

x       = 1101
mask    = 0001
          &
          ────
result  = 0001
             ↑
             1 → ODD

Compare x = 12:

x       = 1100
mask    = 0001
          &
          ────
result  = 0000
             ↑
             0 → EVEN
```

**Pattern:** parity → inspect only the last bit.


<a id="form-1-problem-1"></a>

## Problem 1 — [1367B — Even Array](https://codeforces.com/problemset/problem/1367/B)

### What the problem wants — simple words

Make every position valid: an even index must contain an even value and an odd index must contain an odd value, using the minimum swaps.

### Real-world mapping

Think of two parking zones: EVEN cars belong in EVEN slots and ODD cars in ODD slots. One swap fixes one car misplaced in each zone.

### How this form applies

The condition is parity only. Compare the least-significant bit of the index and value. A swap fixes one odd-at-even mismatch together with one even-at-odd mismatch.

### How the solution works — step by step

1. Identify the bit property used by this form: The condition is parity only.
2. Compare the least-significant bit of the index and value.
3. A swap fixes one odd-at-even mismatch together with one even-at-odd mismatch.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ Example: a = [3,2,7,6]
│
▼

STEP 2
│
├─ index      0    1    2    3
├─ index LSB  0    1    0    1
├─ value      3    2    7    6
├─ binary    11   10  111  110
├─ value LSB  1    0    1    0
├─             X    X    X    X
│
▼

STEP 3
│
├─ odd value at even index  = 2
├─ Even value at odd index  = 2
├─ Each swap consumes one of each -> answer = 2.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0001
          &
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        int x,y=0,z=0;
        for (int i=0;i<n;i++){
            cin>>x;
            if ((x&1)!=(i&1)){
                if (x&1)y++;
                else z++;
            }
        }
        cout<<(y==z?y:-1)<<'\n';
    }
}
```


---

<a id="form-1-problem-2"></a>

## Problem 2 — [1475A — Odd Divisor](https://codeforces.com/problemset/problem/1475/A)

### What the problem wants — simple words

Decide whether n has an odd divisor greater than 1.

### Real-world mapping

Imagine repeatedly cutting a number in half. If you eventually reach an odd number greater than 1, that odd number is an odd divisor. Pure powers of two only reach 1.

### How this form applies

A number has no odd divisor greater than 1 exactly when it is a power of two. Use `n & (n-1)` to test that form.

### How the solution works — step by step

1. Identify the bit property used by this form: A number has no odd divisor greater than 1 exactly when it is a power of two.
2. Use `n & (n-1)` to test that form.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ n = 8
├─ 8   = 1000
├─ 7   = 0111
├─ &     0000 -> power of two -> NO
│
▼

STEP 2
│
├─ n = 12
├─ 12  = 1100
├─ 11  = 1011
├─ &     1000 -> more than one set bit -> has odd factor 3 -> YES.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0001
          &
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        long long n;
        cin>>n;
        cout<<((n&(n-1))?"YES":"NO")<<'\n';
    }
}
```


---

<a id="form-1-problem-3"></a>

## Problem 3 — [1688B — Patchouli’s Magical Talisman](https://codeforces.com/problemset/problem/1688/B)

### What the problem wants — simple words

Find the minimum operations for the problem’s transformation; the key distinction is whether an odd element already exists.

### Real-world mapping

Think of an odd element as a lit match. If one already exists, it can help process the even elements. If none exists, first expose an odd core by removing factors of 2.

### How this form applies

Parity is the first split. If at least one number is odd, every even number can be combined/converted with one operation in the optimal process. If all are even, choose the element with the fewest trailing zero bits to become odd first, then handle the rest.

### How the solution works — step by step

1. Identify the bit property used by this form: Parity is the first split.
2. If at least one number is odd, every even number can be combined/converted with one operation in the optimal process.
3. If all are even, choose the element with the fewest trailing zero bits to become odd first, then handle the rest.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[4,8,12]
├─ 4 =0100 -> trailing zeros 2
├─ 8 =1000 -> trailing zeros 3
├─ 12=1100 -> trailing zeros 2
├─ All even.
├─ Pick 4 (or12): need 2 divisions-by-2 style steps to expose odd bit.
├─ Then remaining n-1=2 elements each need one combining step.
├─ answer=2+2=4.
│
▼

STEP 2
│
├─ If a=[3,4,8], odd already exists -> answer = number of evens =2.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0001
          &
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<int>a(n);
        int even=0,mn=31;
        for (int&x:a){
            cin>>x;
            if (x%2==0){
                even++;
                mn=min(mn,__builtin_ctz(x));
            }
        }
        if (even<n)cout<<even<<'\n';
        else cout<<n-1+mn<<'\n';
    }
}
```


---

<a id="form-1-problem-4"></a>

## Problem 4 — [1805A — We Need the Zero](https://codeforces.com/problemset/problem/1805/A)

### What the problem wants — simple words

Choose x so that XORing every array element with x makes the XOR of the whole transformed array equal to 0, or report that it is impossible.

### Real-world mapping

Imagine every number wears the same XOR mask x. If the group size is even, identical masks cancel in pairs; if it is odd, one copy of the mask remains.

### How this form applies

Let S be XOR of the array. After XORing every element with x, total becomes `S ^ x` if n is odd and `S` if n is even.

### How the solution works — step by step

1. Identify the bit property used by this form: Let S be XOR of the array.
2. After XORing every element with x, total becomes `S ^ x` if n is odd and `S` if n is even.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[1,2,5]
├─ 1=001
├─ 2=010
├─ 5=101
├─ S=110=6
├─ n=3 odd -> choose x=S=110
│
▼

STEP 2
│
├─ 001^110=111
├─ 010^110=100
├─ 101^110=011
├─ 111^100^011=000.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0001
          &
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        int xr=0,x;
        for (int i=0;i<n;i++){
            cin>>x;
            xr^=x;
        }
        if (n&1) cout<<xr<<'\n';
        else cout<<(xr==0?0:-1)<<'\n';
    }
}
```

---

<a id="form-2"></a>

# Form 2 — Power of Two / Remove Lowest Set Bit

## Form explained visually

**What this form means:** A power of two has exactly one `1` bit. `x & (x-1)` removes the lowest set bit.

**Daily-life mapping — one light ON**
```text
8  = 1000   → exactly ONE light ON → power of 2
12 = 1100   → TWO lights ON        → not power of 2

12        1100
11        1011
AND       1000   ← lowest ON light removed
```

**Recognition signal**
```text
power of two / exactly one set bit
repeatedly remove a set bit
odd divisor after removing powers of 2
        ↓
Think: x & (x-1)
```

**Core tools:** `x > 0 && (x & (x-1)) == 0`, `x &= x-1`.

### Bit-by-bit form example

```text
Example: x = 12

x       = 1100
x - 1   = 1011
          &
          ────
result  = 1000
             ↑
the lowest 1-bit of x disappeared

Now x = 8:

x       = 1000
x - 1   = 0111
          &
          ────
result  = 0000

Only one 1-bit existed → power of two.
```

**Pattern:** `x & (x-1)` removes one set bit; result `0` means exactly one set bit.


<a id="form-2-problem-1"></a>

## Problem 1 — [1475A — Odd Divisor](https://codeforces.com/problemset/problem/1475/A)

### What the problem wants — simple words

Decide whether n has an odd divisor greater than 1.

### Real-world mapping

Imagine repeatedly cutting a number in half. If you eventually reach an odd number greater than 1, that odd number is an odd divisor. Pure powers of two only reach 1.

### How this form applies

A number has no odd divisor greater than 1 exactly when it is a power of two. Use `n & (n-1)` to test that form.

### How the solution works — step by step

1. Identify the bit property used by this form: A number has no odd divisor greater than 1 exactly when it is a power of two.
2. Use `n & (n-1)` to test that form.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ n = 8
├─ 8   = 1000
├─ 7   = 0111
├─ &     0000 -> power of two -> NO
│
▼

STEP 2
│
├─ n = 12
├─ 12  = 1100
├─ 11  = 1011
├─ &     1000 -> more than one set bit -> has odd factor 3 -> YES.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        long long n;
        cin>>n;
        cout<<((n&(n-1))?"YES":"NO")<<'\n';
    }
}
```


---

<a id="form-2-problem-2"></a>

## Problem 2 — [1527A — And Then There Were K](https://codeforces.com/problemset/problem/1527/A)

### What the problem wants — simple words

Find the largest k<n such that n & (n-1) & ... & k becomes 0.

### Real-world mapping

Think of walking downward from n until every binary switch has been turned OFF at least once. The highest power-of-two boundary tells you where that happens.

### How this form applies

For maximum `k<n` with `n & (n-1) & ... & k = 0`, find the highest power of two not exceeding `n`; answer is that power minus one.

### How the solution works — step by step

1. Identify the bit property used by this form: For maximum `k<n` with `n & (n-1) & ...
2. & k = 0`, find the highest power of two not exceeding `n`; answer is that power minus one.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ n = 10 = 1010
├─ highest power of 2 <= n = 8 = 1000
├─ answer = 8-1 = 7 = 0111
│
▼

STEP 2
│
├─ The interval 10,9,8,7 contains values that clear every bit in the cumulative AND.
├─ The boundary is determined by the MSB of n.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        long long n;
        cin>>n;
        long long p=1;
        while ((p<<1)<=n)p<<=1;
        cout<<p-1<<'\n';
    }
}
```


---

<a id="form-2-problem-3"></a>

## Problem 3 — [1567B — MEXor Mixup](https://codeforces.com/problemset/problem/1567/B)

### What the problem wants — simple words

Find the minimum array length whose MEX is a and whose XOR is b.

### Real-world mapping

To force MEX=a, you must collect every label 0..a-1. Their XOR is your current checksum; add the smallest extra item(s) needed to change that checksum to b without accidentally changing the MEX.

### How this form applies

The XOR of `0..a-1` is obtained from the 4-cycle. If it already equals b, length a works; otherwise append one number, except when that number equals a, which would change MEX and requires two.

### How the solution works — step by step

1. Identify the bit property used by this form: The XOR of `0..a-1` is obtained from the 4-cycle.
2. If it already equals b, length a works; otherwise append one number, except when that number equals a, which would change MEX and requires two.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ Suppose a=4, b=2.
├─ Need array containing 0,1,2,3.
├─ XOR(0..3)=0 (cycle).
├─ Need extra value d = 0^2 = 2.
├─ But 2<a, already allowed; append 2 -> XOR becomes 2 and MEX remains 4.
├─ Answer a+1=5.
│
▼

STEP 2
│
├─ If d==a, appending a would make MEX > a, so use two extra values -> a+2.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

long long px(long long n){
    if (n<0)return 0;
    switch (n&3){
        case 0:return n;
        case 1:return 1;
        case 2:return n+1;
        default:return 0;
    }
}

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        long long a,b;
        cin>>a>>b;
        long long x=px(a-1);
        if (x==b)cout<<a;
        else if ((x^b)==a)cout<<a+2;
        else cout<<a+1;
        cout<<'\n';
    }
}
```


---

<a id="form-2-problem-4"></a>

## Problem 4 — [1909B — Make Almost Equal With Mod](https://codeforces.com/problemset/problem/1909/B)

### What the problem wants — simple words

Find a power-of-two modulus that makes the array produce exactly two distinct remainders.

### Real-world mapping

A modulus 2^k is like looking only through a window at the last k binary digits. Widen the window until the numbers split into exactly two groups.

### How this form applies

Try powers of two. `x mod 2^k` is exactly the last k bits. Increase suffix length until exactly two distinct suffixes occur.

### How the solution works — step by step

1. Identify the bit property used by this form: Try powers of two.
2. `x mod 2^k` is exactly the last k bits.
3. Increase suffix length until exactly two distinct suffixes occur.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[8,14,22,30]
├─ 8 =001000
├─ 14=001110
├─ 22=010110
├─ 30=011110
│
▼

STEP 2
│
├─ mod2 -> last1: 0,0,0,0 -> {0}
├─ mod4 -> last2: 00,10,10,10 -> {0,2}
├─ Exactly two -> answer 4.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<long long>a(n);
        for (auto&x:a)cin>>x;
        for (int b=1;b<=61;b++){
            long long m=1LL<<b;
            set<long long>s;
            for (auto x:a)s.insert(x%m);
            if (s.size()==2){
                cout<<m<<'\n';
                break;
            }
        }
    }
}
```

---

<a id="form-3"></a>

# Form 3 — Kth-bit Masking

## Form explained visually

**What this form means:** Inspect, set, clear, or toggle one chosen bit without disturbing the others.

**Daily-life mapping — apartment switchboard**
```text
bits:     b3 b2 b1 b0
number:    1  0  1  0
                 ↑
              inspect b1

mask = 1 << 1 = 0010

CHECK : x & mask
SET   : x | mask
CLEAR : x & ~mask
TOGGLE: x ^ mask
```

**Recognition signal**
```text
"for every bit"
"does bit k exist?"
"make bit k = 1"
"flip bit k"
        ↓
Build mask 1 << k
```

**Core tools:** `1LL<<k`, `x&(1LL<<k)`, `x|=1LL<<k`, `x^=1LL<<k`.

### Bit-by-bit form example

```text
Example: x = 10, check bit k = 1

x       = 1010
bits      3210

mask = 1 << 1

mask    = 0010
x       = 1010
          &
          ────
result  = 0010
            ↑
          bit 1 is ON

SET bit 2:

x       = 1010
mask    = 0100
          |
          ────
result  = 1110
```

**Pattern:** `1 << k` isolates exactly one bit column.


<a id="form-3-problem-1"></a>

## Problem 1 — [1669H — Maximal AND](https://codeforces.com/problemset/problem/1669/H)

### What the problem wants — simple words

Use at most k allowed bit operations to maximize the AND of all array elements.

### Real-world mapping

A final AND bit is like a team requirement: that badge appears in the team result only if every member has it. Spend your budget giving missing badges, starting with the most valuable high bit.

### How this form applies

To put bit b into the final AND, every element must have bit b=1. Cost is the number of elements missing that bit. Spend budget greedily from bit 30 down to 0.

### How the solution works — step by step

1. Identify the bit property used by this form: To put bit b into the final AND, every element must have bit b=1.
2. Cost is the number of elements missing that bit.
3. Spend budget greedily from bit 30 down to 0.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[2,1,1], k=2
├─ 2=10
├─ 1=01
├─ 1=01
│
▼

STEP 2
│
├─ bit1 column: 1,0,0 -> need 2 operations
├─ k=2 -> buy bit1
├─ array can become 10,11,11
├─ AND =10 =2
├─ No budget remains.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0010
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        long long k;
        cin>>n>>k;
        vector<int>a(n);
        for (int&x:a)cin>>x;
        long long ans=0;
        for (int b=30;b>=0;b--){
            long long need=0;
            for (int x:a)if (!(x&(1<<b)))need++;
            if (need<=k){
                k-=need;
                ans|=1LL<<b;
            }
        }
        cout<<ans<<'\n';
    }
}
```


---

<a id="form-3-problem-2"></a>

## Problem 2 — [1547D — Co-growing Sequence](https://codeforces.com/problemset/problem/1547/D)

### What the problem wants — simple words

Construct the required companion sequence so consecutive transformed values satisfy the problem’s bitwise growing condition.

### Real-world mapping

Treat every 1-bit already required by the previous value as a feature the next value must keep. Add only the missing features.

### How this form applies

Choose y[i] so `(x[i] xor y[i]) & (x[i+1] xor y[i+1]) = x[i] xor y[i]`. This means every 1-bit in previous transformed value must also be 1 in current transformed value. Add exactly the missing bits.

### How the solution works — step by step

1. Identify the bit property used by this form: Choose y[i] so `(x[i] xor y[i]) & (x[i+1] xor y[i+1]) = x[i] xor y[i]`.
2. This means every 1-bit in previous transformed value must also be 1 in current transformed value.
3. Add exactly the missing bits.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ previous transformed p=1011
├─ current x=0010
├─ Need current transformed z to contain all bits of p.
├─ Missing bits = p & ~x
├─ 1011
├─ ~0010 (within width) -> ...1101
├─ AND ->1001
├─ Choose y=1001
├─ x^y=0010^1001=1011
├─ Now p & z =1011 = p.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0010
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<int>x(n),y(n);
        for (int&i:x)cin>>i;
        int prev=x[0];
        for (int i=1;i<n;i++){
            y[i]=prev & (~x[i]);
            prev=x[i]^y[i];
        }
        for (int v:y)cout<<v<<' ';
        cout<<'\n';
    }
}
```


---

<a id="form-3-problem-3"></a>

## Problem 3 — [1926D — Vlad and Division](https://codeforces.com/problemset/problem/1926/D)

### What the problem wants — simple words

Group numbers so paired values have opposite bits in the required fixed bit range, minimizing the number of groups.

### Real-world mapping

Think of two puzzle pieces: every 0 needs a 1 opposite it and every 1 needs a 0. The matching piece is the fixed-width binary complement.

### How this form applies

Two values can pair when their lowest 31 bits are opposite. Partner is `x xor ((1<<31)-1)`. Greedily match complements.

### How the solution works — step by step

1. Identify the bit property used by this form: Two values can pair when their lowest 31 bits are opposite.
2. Partner is `x xor ((1<<31)-1)`.
3. Greedily match complements.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ 5-bit illustration:
├─ x=10110
├─ mask=11111
├─ partner=01001
│
▼

STEP 2
│
├─ 10110
├─ 01001
├─ -----
├─ every column is 1/0 or 0/1.
├─ If partner already waits, pair them; otherwise start a new group with x.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0010
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    const long long M=(1LL<<31)-1;
    while (T--){
        int n;
        cin>>n;
        unordered_map<long long,int>cnt;
        int ans=0;
        while (n--){
            long long x;
            cin>>x,y=x^M;
            if (cnt[y])cnt[y]--;
            else{
                cnt[x]++;
                ans++;
            }
        }
        cout<<ans<<'\n';
    }
}
```


---

<a id="form-3-problem-4"></a>

## Problem 4 — [1042B — Vitamins](https://codeforces.com/problemset/problem/1042/B)

### What the problem wants — simple words

Buy the cheapest set of juices that together provide vitamins A, B, and C.

### Real-world mapping

A, B, C are three checkboxes. Encode the checked boxes as bits; OR combines the vitamins supplied by purchases.

### How this form applies

Encode vitamins A,B,C as bits 0,1,2. Each juice is a mask. OR combines acquired vitamins; DP over 8 masks gives minimum cost.

### How the solution works — step by step

1. Identify the bit property used by this form: Encode vitamins A,B,C as bits 0,1,2.
2. Each juice is a mask.
3. OR combines acquired vitamins; DP over 8 masks gives minimum cost.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ A=001,B=010,C=100
├─ juice AB ->011 cost5
├─ juice C  ->100 cost3
├─ state 000 --buy AB-->011
├─ 011 |100 =111
├─ cost=8, all vitamins covered.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0010
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin>>n;
    const int INF=1e9;
    vector<int>dp(8,INF);
    dp[0]=0;
    while (n--){
        int c;
        string s;
        cin>>c>>s;
        int m=0;
        for (char ch:s)m|=1<<(ch-'A');
        auto ndp=dp;
        for (int st=0;st<8;st++)ndp[st|m]=min(ndp[st|m],dp[st]+c);
        dp=ndp;
    }
    cout<<(dp[7]>=INF?-1:dp[7])<<'\n';
}
```

---

<a id="form-4"></a>

# Form 4 — XOR Cancellation

## Form explained visually

**What this form means:** Equal XOR values cancel: `x ^ x = 0`, and `x ^ 0 = x`.

**Daily-life mapping — matching socks**
```text
A ^ A → pair disappears
B ^ B → pair disappears

A A B B C
↓ ↓ ↓ ↓ ↓
0   0   C
        ↓
only C remains
```

**Recognition signal**
```text
pairs / duplicates
one value appears differently
same value applied twice
undo an XOR
        ↓
Think: cancellation
```

**Core tools:** accumulate with `xr ^= x`.

### Bit-by-bit form example

```text
Example: 13 ^ 6

x       = 1101
y       = 0110
          ^
          ────
result  = 1011

Column rule:

1 ^ 0 = 1
1 ^ 1 = 0   ← equal bits cancel
0 ^ 1 = 1
1 ^ 0 = 1

Cancellation example:

x       = 1101
x       = 1101
          ^
          ────
result  = 0000
```

**Pattern:** equal XOR values cancel: `x ^ x = 0`.


<a id="form-4-problem-1"></a>

## Problem 1 — [1698A — XOR Mixup](https://codeforces.com/problemset/problem/1698/A)

### What the problem wants — simple words

Recover the required value using the XOR relation among all given numbers.

### Real-world mapping

XOR is a cancellation checksum: equal contributions appearing twice disappear, leaving the value that must be recovered.

### How this form applies

The appended value is XOR of all original values. XORing the final array gives zero, so any element can be reconstructed as XOR of all the others; outputting XOR of all final values is also a valid answer under the construction.

### How the solution works — step by step

1. Identify the bit property used by this form: The appended value is XOR of all original values.
2. XORing the final array gives zero, so any element can be reconstructed as XOR of all the others; outputting XOR of all final values is also a valid answer under the construction.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ Example final array: [4,3,2,5]
├─ 4=100
├─ 3=011
├─ 2=010
├─ 5=101
├─ XOR:
├─ 100 ^ 011 = 111
├─ 111 ^ 010 = 101
├─ 101 ^ 101 = 000
│
▼

STEP 2
│
├─ The full XOR is 0 because original-XOR x appears once in addition to the originals whose XOR is x. A valid x can be obtained using the problem's guaranteed construction; the standard implementation XORs the required subset relation.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          ^
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<int>a(n);
        for (int&x:a)cin>>x;
        // Any a[i] can serve as x when XOR of all final elements is 0.
        cout<<a[0]<<'\n';
    }
}
```


---

<a id="form-4-problem-2"></a>

## Problem 2 — [1805A — We Need the Zero](https://codeforces.com/problemset/problem/1805/A)

### What the problem wants — simple words

Choose x so that XORing every array element with x makes the XOR of the whole transformed array equal to 0, or report that it is impossible.

### Real-world mapping

Imagine every number wears the same XOR mask x. If the group size is even, identical masks cancel in pairs; if it is odd, one copy of the mask remains.

### How this form applies

Let S be XOR of the array. After XORing every element with x, total becomes `S ^ x` if n is odd and `S` if n is even.

### How the solution works — step by step

1. Identify the bit property used by this form: Let S be XOR of the array.
2. After XORing every element with x, total becomes `S ^ x` if n is odd and `S` if n is even.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[1,2,5]
├─ 1=001
├─ 2=010
├─ 5=101
├─ S=110=6
├─ n=3 odd -> choose x=S=110
│
▼

STEP 2
│
├─ 001^110=111
├─ 010^110=100
├─ 101^110=011
├─ 111^100^011=000.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          ^
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        int xr=0,x;
        for (int i=0;i<n;i++){
            cin>>x;
            xr^=x;
        }
        if (n&1) cout<<xr<<'\n';
        else cout<<(xr==0?0:-1)<<'\n';
    }
}
```


---

<a id="form-4-problem-3"></a>

## Problem 3 — [1516B — AGAGA XOOORRR](https://codeforces.com/problemset/problem/1516/B)

### What the problem wants — simple words

Decide whether the array can be split according to the problem so the required segment XOR values match.

### Real-world mapping

Think of prefix XOR as a running checksum. Equal segment checksums mean the boundaries can be placed where the running XOR reaches the needed states.

### How this form applies

Partition into at least two segments with equal XOR. If total XOR is 0, two parts can work. Otherwise, scan for two prefixes/segments whose XOR equals total XOR, giving three equal-XOR parts.

### How the solution works — step by step

1. Identify the bit property used by this form: Partition into at least two segments with equal XOR.
2. If total XOR is 0, two parts can work.
3. Otherwise, scan for two prefixes/segments whose XOR equals total XOR, giving three equal-XOR parts.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[1,2,3]
├─ 1=01,2=10,3=11
├─ total=01^10^11=00 -> YES immediately.
│
▼

STEP 2
│
├─ If total S!=0, look for:
├─ segment1 XOR=S
├─ segment2 XOR=S
├─ then remaining XOR = S^S^S = S,
├─ so three segments have equal XOR.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          ^
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<int>a(n);
        int S=0;
        for (int&x:a){
            cin>>x;
            S^=x;
        }
        if (S==0){
            cout<<"YES\n";
            continue;
        }
        int cur=0,cnt=0;
        for (int i=0;i<n-1;i++){
            cur^=a[i];
            if (cur==S){
                cnt++;
                cur=0;
            }
        }
        cout<<(cnt>=2?"YES":"NO")<<'\n';
    }
}
```


---

<a id="form-4-problem-4"></a>

## Problem 4 — [1872E — Data Structures Fan](https://codeforces.com/problemset/problem/1872/E)

### What the problem wants — simple words

Maintain XOR information for two groups while range queries flip which group selected elements belong to.

### Real-world mapping

Imagine two XOR buckets labelled 0 and 1. Flipping a range moves its combined XOR effect between the buckets; prefix XOR gives that range effect instantly.

### How this form applies

Maintain XOR of the two groups defined by a binary string. Flipping a whole substring swaps membership there; prefix XOR gives XOR of the flipped values in O(1), and that value toggles both group XORs.

### How the solution works — step by step

1. Identify the bit property used by this form: Maintain XOR of the two groups defined by a binary string.
2. Flipping a whole substring swaps membership there; prefix XOR gives XOR of the flipped values in O(1), and that value toggles both group XORs.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[1,2,3], s=010
├─ binary: 1=01,2=10,3=11
├─ Group0 XOR = 1^3 = 01^11 = 10 =2
├─ Group1 XOR = 2 =10
│
▼

STEP 2
│
├─ Flip [1,2] (0-based values 2,3): segment XOR=2^3=01
├─ Both group XORs ^=01:
├─ g0:10^01=11=3
├─ g1:10^01=11=3
├─ Membership swapped only inside range.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          ^
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<long long>a(n),p(n+1);
        for (int i=0;i<n;i++){
            cin>>a[i];
            p[i+1]=p[i]^a[i];
        }
        string s;
        cin>>s;
        long long g[2]={
            0,0
        }
        ;
        for (int i=0;i<n;i++)g[s[i]-'0']^=a[i];
        int q;
        cin>>q;
        while (q--){
            int tp;
            cin>>tp;
            if (tp==1){
                int l,r;
                cin>>l>>r;
                long long x=p[r]^p[l-1];
                g[0]^=x;
                g[1]^=x;
            }
            else{
                int b;
                cin>>b;
                cout<<g[b]<<' ';
            }
        }
        cout<<'\n';
    }
}
```

---

<a id="form-5"></a>

# Form 5 — Global XOR / Solve for X

## Form explained visually

**What this form means:** Collapse the whole array into one XOR equation, then isolate the unknown using XOR again.

**Daily-life mapping — checksum equation**
```text
known checksum S
unknown mask   X

S ^ X = target
    │
XOR S on both sides
    ▼
X = S ^ target
```

**Recognition signal**
```text
same x XORed with many elements
find x
whole-array XOR condition
target XOR must become 0/value
        ↓
Collapse globally first
```

**Core tools:** XOR all elements; use associativity and self-inverse property.

### Bit-by-bit form example

```text
Equation:

S ^ X = T

S = 6 = 110
T = 3 = 011

X = S ^ T

S       = 110
T       = 011
          ^
          ───
X       = 101 = 5

Verify:

S       = 110
X       = 101
          ^
          ───
T       = 011
```

**Pattern:** XOR is its own inverse.


<a id="form-5-problem-1"></a>

## Problem 1 — [1805A — We Need the Zero](https://codeforces.com/problemset/problem/1805/A)

### What the problem wants — simple words

Choose x so that XORing every array element with x makes the XOR of the whole transformed array equal to 0, or report that it is impossible.

### Real-world mapping

Imagine every number wears the same XOR mask x. If the group size is even, identical masks cancel in pairs; if it is odd, one copy of the mask remains.

### How this form applies

Let S be XOR of the array. After XORing every element with x, total becomes `S ^ x` if n is odd and `S` if n is even.

### How the solution works — step by step

1. Identify the bit property used by this form: Let S be XOR of the array.
2. After XORing every element with x, total becomes `S ^ x` if n is odd and `S` if n is even.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[1,2,5]
├─ 1=001
├─ 2=010
├─ 5=101
├─ S=110=6
├─ n=3 odd -> choose x=S=110
│
▼

STEP 2
│
├─ 001^110=111
├─ 010^110=100
├─ 101^110=011
├─ 111^100^011=000.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          ^
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        int xr=0,x;
        for (int i=0;i<n;i++){
            cin>>x;
            xr^=x;
        }
        if (n&1) cout<<xr<<'\n';
        else cout<<(xr==0?0:-1)<<'\n';
    }
}
```


---

<a id="form-5-problem-2"></a>

## Problem 2 — [1698A — XOR Mixup](https://codeforces.com/problemset/problem/1698/A)

### What the problem wants — simple words

Recover the required value using the XOR relation among all given numbers.

### Real-world mapping

XOR is a cancellation checksum: equal contributions appearing twice disappear, leaving the value that must be recovered.

### How this form applies

The appended value is XOR of all original values. XORing the final array gives zero, so any element can be reconstructed as XOR of all the others; outputting XOR of all final values is also a valid answer under the construction.

### How the solution works — step by step

1. Identify the bit property used by this form: The appended value is XOR of all original values.
2. XORing the final array gives zero, so any element can be reconstructed as XOR of all the others; outputting XOR of all final values is also a valid answer under the construction.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ Example final array: [4,3,2,5]
├─ 4=100
├─ 3=011
├─ 2=010
├─ 5=101
├─ XOR:
├─ 100 ^ 011 = 111
├─ 111 ^ 010 = 101
├─ 101 ^ 101 = 000
│
▼

STEP 2
│
├─ The full XOR is 0 because original-XOR x appears once in addition to the originals whose XOR is x. A valid x can be obtained using the problem's guaranteed construction; the standard implementation XORs the required subset relation.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          ^
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<int>a(n);
        for (int&x:a)cin>>x;
        // Any a[i] can serve as x when XOR of all final elements is 0.
        cout<<a[0]<<'\n';
    }
}
```


---

<a id="form-5-problem-3"></a>

## Problem 3 — [1567B — MEXor Mixup](https://codeforces.com/problemset/problem/1567/B)

### What the problem wants — simple words

Find the minimum array length whose MEX is a and whose XOR is b.

### Real-world mapping

To force MEX=a, you must collect every label 0..a-1. Their XOR is your current checksum; add the smallest extra item(s) needed to change that checksum to b without accidentally changing the MEX.

### How this form applies

The XOR of `0..a-1` is obtained from the 4-cycle. If it already equals b, length a works; otherwise append one number, except when that number equals a, which would change MEX and requires two.

### How the solution works — step by step

1. Identify the bit property used by this form: The XOR of `0..a-1` is obtained from the 4-cycle.
2. If it already equals b, length a works; otherwise append one number, except when that number equals a, which would change MEX and requires two.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ Suppose a=4, b=2.
├─ Need array containing 0,1,2,3.
├─ XOR(0..3)=0 (cycle).
├─ Need extra value d = 0^2 = 2.
├─ But 2<a, already allowed; append 2 -> XOR becomes 2 and MEX remains 4.
├─ Answer a+1=5.
│
▼

STEP 2
│
├─ If d==a, appending a would make MEX > a, so use two extra values -> a+2.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          ^
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

long long px(long long n){
    if (n<0)return 0;
    switch (n&3){
        case 0:return n;
        case 1:return 1;
        case 2:return n+1;
        default:return 0;
    }
}

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        long long a,b;
        cin>>a>>b;
        long long x=px(a-1);
        if (x==b)cout<<a;
        else if ((x^b)==a)cout<<a+2;
        else cout<<a+1;
        cout<<'\n';
    }
}
```


---

<a id="form-5-problem-4"></a>

## Problem 4 — [1516B — AGAGA XOOORRR](https://codeforces.com/problemset/problem/1516/B)

### What the problem wants — simple words

Decide whether the array can be split according to the problem so the required segment XOR values match.

### Real-world mapping

Think of prefix XOR as a running checksum. Equal segment checksums mean the boundaries can be placed where the running XOR reaches the needed states.

### How this form applies

Partition into at least two segments with equal XOR. If total XOR is 0, two parts can work. Otherwise, scan for two prefixes/segments whose XOR equals total XOR, giving three equal-XOR parts.

### How the solution works — step by step

1. Identify the bit property used by this form: Partition into at least two segments with equal XOR.
2. If total XOR is 0, two parts can work.
3. Otherwise, scan for two prefixes/segments whose XOR equals total XOR, giving three equal-XOR parts.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[1,2,3]
├─ 1=01,2=10,3=11
├─ total=01^10^11=00 -> YES immediately.
│
▼

STEP 2
│
├─ If total S!=0, look for:
├─ segment1 XOR=S
├─ segment2 XOR=S
├─ then remaining XOR = S^S^S = S,
├─ so three segments have equal XOR.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          ^
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<int>a(n);
        int S=0;
        for (int&x:a){
            cin>>x;
            S^=x;
        }
        if (S==0){
            cout<<"YES\n";
            continue;
        }
        int cur=0,cnt=0;
        for (int i=0;i<n-1;i++){
            cur^=a[i];
            if (cur==S){
                cnt++;
                cur=0;
            }
        }
        cout<<(cnt>=2?"YES":"NO")<<'\n';
    }
}
```

---

<a id="form-6"></a>

# Form 6 — XOR 1..N Cycle

## Form explained visually

**What this form means:** `0 ^ 1 ^ ... ^ n` repeats a four-case pattern based on `n % 4`.

**Daily-life mapping — four-stop circular bus**
```text
n % 4:   0      1      2      3
answer:  n      1     n+1     0
          └──────── repeats ────────┘
```

**Recognition signal**
```text
XOR of 1..n
XOR of 0..n
consecutive XOR from 1
huge n, cannot loop
        ↓
Think: 4-cycle
```

**Core tool:** prefix-XOR formula by `n & 3`.

### Bit-by-bit form example

```text
XOR from 0..n repeats every 4 values.

n = 0: 0             = 0
n = 1: 0 ^ 1         = 1
n = 2: 0 ^ 1 ^ 2     = 3
n = 3: 0 ^ 1 ^ 2 ^ 3 = 0

Then the pattern restarts:

n % 4 = 0 → n
n % 4 = 1 → 1
n % 4 = 2 → n + 1
n % 4 = 3 → 0

Example n = 6:

6 % 4 = 2
XOR(0..6) = 6 + 1 = 7

6       = 110
answer  = 111
```

**Pattern:** recognize the 4-cycle instead of looping.


<a id="form-6-problem-1"></a>

## Problem 1 — [1567B — MEXor Mixup](https://codeforces.com/problemset/problem/1567/B)

### What the problem wants — simple words

Find the minimum array length whose MEX is a and whose XOR is b.

### Real-world mapping

To force MEX=a, you must collect every label 0..a-1. Their XOR is your current checksum; add the smallest extra item(s) needed to change that checksum to b without accidentally changing the MEX.

### How this form applies

The XOR of `0..a-1` is obtained from the 4-cycle. If it already equals b, length a works; otherwise append one number, except when that number equals a, which would change MEX and requires two.

### How the solution works — step by step

1. Identify the bit property used by this form: The XOR of `0..a-1` is obtained from the 4-cycle.
2. If it already equals b, length a works; otherwise append one number, except when that number equals a, which would change MEX and requires two.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ Suppose a=4, b=2.
├─ Need array containing 0,1,2,3.
├─ XOR(0..3)=0 (cycle).
├─ Need extra value d = 0^2 = 2.
├─ But 2<a, already allowed; append 2 -> XOR becomes 2 and MEX remains 4.
├─ Answer a+1=5.
│
▼

STEP 2
│
├─ If d==a, appending a would make MEX > a, so use two extra values -> a+2.

RESULT
```
#### Visual bit operation

```text
x       = 0110
y       = 0001
          ^
          ────
result  = 0111
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

long long px(long long n){
    if (n<0)return 0;
    switch (n&3){
        case 0:return n;
        case 1:return 1;
        case 2:return n+1;
        default:return 0;
    }
}

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        long long a,b;
        cin>>a>>b;
        long long x=px(a-1);
        if (x==b)cout<<a;
        else if ((x^b)==a)cout<<a+2;
        else cout<<a+1;
        cout<<'\n';
    }
}
```


---

<a id="form-6-problem-2"></a>

## Problem 2 — [1527A — And Then There Were K](https://codeforces.com/problemset/problem/1527/A)

### What the problem wants — simple words

Find the largest k<n such that n & (n-1) & ... & k becomes 0.

### Real-world mapping

Think of walking downward from n until every binary switch has been turned OFF at least once. The highest power-of-two boundary tells you where that happens.

### How this form applies

For maximum `k<n` with `n & (n-1) & ... & k = 0`, find the highest power of two not exceeding `n`; answer is that power minus one.

### How the solution works — step by step

1. Identify the bit property used by this form: For maximum `k<n` with `n & (n-1) & ...
2. & k = 0`, find the highest power of two not exceeding `n`; answer is that power minus one.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ n = 10 = 1010
├─ highest power of 2 <= n = 8 = 1000
├─ answer = 8-1 = 7 = 0111
│
▼

STEP 2
│
├─ The interval 10,9,8,7 contains values that clear every bit in the cumulative AND.
├─ The boundary is determined by the MSB of n.

RESULT
```
#### Visual bit operation

```text
x       = 0110
y       = 0001
          ^
          ────
result  = 0111
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        long long n;
        cin>>n;
        long long p=1;
        while ((p<<1)<=n)p<<=1;
        cout<<p-1<<'\n';
    }
}
```


---

<a id="form-6-problem-3"></a>

## Problem 3 — [1909B — Make Almost Equal With Mod](https://codeforces.com/problemset/problem/1909/B)

### What the problem wants — simple words

Find a power-of-two modulus that makes the array produce exactly two distinct remainders.

### Real-world mapping

A modulus 2^k is like looking only through a window at the last k binary digits. Widen the window until the numbers split into exactly two groups.

### How this form applies

Try powers of two. `x mod 2^k` is exactly the last k bits. Increase suffix length until exactly two distinct suffixes occur.

### How the solution works — step by step

1. Identify the bit property used by this form: Try powers of two.
2. `x mod 2^k` is exactly the last k bits.
3. Increase suffix length until exactly two distinct suffixes occur.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[8,14,22,30]
├─ 8 =001000
├─ 14=001110
├─ 22=010110
├─ 30=011110
│
▼

STEP 2
│
├─ mod2 -> last1: 0,0,0,0 -> {0}
├─ mod4 -> last2: 00,10,10,10 -> {0,2}
├─ Exactly two -> answer 4.

RESULT
```
#### Visual bit operation

```text
x       = 0110
y       = 0001
          ^
          ────
result  = 0111
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<long long>a(n);
        for (auto&x:a)cin>>x;
        for (int b=1;b<=61;b++){
            long long m=1LL<<b;
            set<long long>s;
            for (auto x:a)s.insert(x%m);
            if (s.size()==2){
                cout<<m<<'\n';
                break;
            }
        }
    }
}
```


---

<a id="form-6-problem-4"></a>

## Problem 4 — [1097B — Petr and a Combination Lock](https://codeforces.com/problemset/problem/1097/B)

### What the problem wants — simple words

Choose clockwise or counter-clockwise for every angle so the final rotation is divisible by 360.

### Real-world mapping

Each bit of a mask is a direction switch: 1 means +angle and 0 means -angle. Try every switch configuration.

### How this form applies

Each angle has two choices: + or -. Encode the choice for angle i in bit i of a mask and enumerate all 2^n assignments.

### How the solution works — step by step

1. Identify the bit property used by this form: Each angle has two choices: + or -.
2. Encode the choice for angle i in bit i of a mask and enumerate all 2^n assignments.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ n=3, angles=[10,20,30]
├─ mask=101
├─ bit0=1 -> +10
├─ bit1=0 -> -20
├─ bit2=1 -> +30
├─ sum=20, not divisible by360.
├─ Try every mask 000..111 until one gives sum%360=0.

RESULT
```
#### Visual bit operation

```text
x       = 0110
y       = 0001
          ^
          ────
result  = 0111
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin>>n;
    vector<int>a(n);
    for (int&x:a)cin>>x;
    for (int m=0;m<(1<<n);m++){
        int s=0;
        for (int i=0;i<n;i++)s+=(m>>i&1)?a[i]:-a[i];
        if ((s%360+360)%360==0){
            cout<<"YES\n";
            return 0;
        }
    }
    cout<<"NO\n";
}
```

---

<a id="form-7"></a>

# Form 7 — Prefix XOR

## Form explained visually

**What this form means:** Precompute XOR from the start so any range XOR is answered in O(1).

**Daily-life mapping — cumulative security checksum**
```text
a:      [a0] [a1] [a2] [a3] [a4]
prefix:  0 → p1 → p2 → p3 → p4 → p5

Want XOR [L..R]

prefix before L  ^  prefix through R
        │                    │
        └──── common part cancels ────┘

rangeXor = px[R+1] ^ px[L]
```

**Recognition signal**
```text
many [L,R] XOR queries
subarray XOR repeatedly
Q up to 1e5
        ↓
Think: Prefix XOR
```

**Core tool:** `px[i+1] = px[i] ^ a[i]`.

### Bit-by-bit form example

```text
a = [5, 2, 7, 3]

5 = 101
2 = 010
7 = 111
3 = 011

Prefix XOR:

px[0] = 000
px[1] = 000 ^ 101 = 101
px[2] = 101 ^ 010 = 111
px[3] = 111 ^ 111 = 000
px[4] = 000 ^ 011 = 011

Range [1..2]:

px[3]  = 000
px[1]  = 101
          ^
          ───
answer = 101

Why?
(a0 ^ a1 ^ a2) ^ a0
       ↓ a0 cancels
      a1 ^ a2
```

**Pattern:** range XOR = `px[R+1] ^ px[L]`.


<a id="form-7-problem-1"></a>

## Problem 1 — [1872E — Data Structures Fan](https://codeforces.com/problemset/problem/1872/E)

### What the problem wants — simple words

Maintain XOR information for two groups while range queries flip which group selected elements belong to.

### Real-world mapping

Imagine two XOR buckets labelled 0 and 1. Flipping a range moves its combined XOR effect between the buckets; prefix XOR gives that range effect instantly.

### How this form applies

Maintain XOR of the two groups defined by a binary string. Flipping a whole substring swaps membership there; prefix XOR gives XOR of the flipped values in O(1), and that value toggles both group XORs.

### How the solution works — step by step

1. Identify the bit property used by this form: Maintain XOR of the two groups defined by a binary string.
2. Flipping a whole substring swaps membership there; prefix XOR gives XOR of the flipped values in O(1), and that value toggles both group XORs.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[1,2,3], s=010
├─ binary: 1=01,2=10,3=11
├─ Group0 XOR = 1^3 = 01^11 = 10 =2
├─ Group1 XOR = 2 =10
│
▼

STEP 2
│
├─ Flip [1,2] (0-based values 2,3): segment XOR=2^3=01
├─ Both group XORs ^=01:
├─ g0:10^01=11=3
├─ g1:10^01=11=3
├─ Membership swapped only inside range.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          ^
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<long long>a(n),p(n+1);
        for (int i=0;i<n;i++){
            cin>>a[i];
            p[i+1]=p[i]^a[i];
        }
        string s;
        cin>>s;
        long long g[2]={
            0,0
        }
        ;
        for (int i=0;i<n;i++)g[s[i]-'0']^=a[i];
        int q;
        cin>>q;
        while (q--){
            int tp;
            cin>>tp;
            if (tp==1){
                int l,r;
                cin>>l>>r;
                long long x=p[r]^p[l-1];
                g[0]^=x;
                g[1]^=x;
            }
            else{
                int b;
                cin>>b;
                cout<<g[b]<<' ';
            }
        }
        cout<<'\n';
    }
}
```


---

<a id="form-7-problem-2"></a>

## Problem 2 — [1516B — AGAGA XOOORRR](https://codeforces.com/problemset/problem/1516/B)

### What the problem wants — simple words

Decide whether the array can be split according to the problem so the required segment XOR values match.

### Real-world mapping

Think of prefix XOR as a running checksum. Equal segment checksums mean the boundaries can be placed where the running XOR reaches the needed states.

### How this form applies

Partition into at least two segments with equal XOR. If total XOR is 0, two parts can work. Otherwise, scan for two prefixes/segments whose XOR equals total XOR, giving three equal-XOR parts.

### How the solution works — step by step

1. Identify the bit property used by this form: Partition into at least two segments with equal XOR.
2. If total XOR is 0, two parts can work.
3. Otherwise, scan for two prefixes/segments whose XOR equals total XOR, giving three equal-XOR parts.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[1,2,3]
├─ 1=01,2=10,3=11
├─ total=01^10^11=00 -> YES immediately.
│
▼

STEP 2
│
├─ If total S!=0, look for:
├─ segment1 XOR=S
├─ segment2 XOR=S
├─ then remaining XOR = S^S^S = S,
├─ so three segments have equal XOR.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          ^
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<int>a(n);
        int S=0;
        for (int&x:a){
            cin>>x;
            S^=x;
        }
        if (S==0){
            cout<<"YES\n";
            continue;
        }
        int cur=0,cnt=0;
        for (int i=0;i<n-1;i++){
            cur^=a[i];
            if (cur==S){
                cnt++;
                cur=0;
            }
        }
        cout<<(cnt>=2?"YES":"NO")<<'\n';
    }
}
```


---

<a id="form-7-problem-3"></a>

## Problem 3 — [1698A — XOR Mixup](https://codeforces.com/problemset/problem/1698/A)

### What the problem wants — simple words

Recover the required value using the XOR relation among all given numbers.

### Real-world mapping

XOR is a cancellation checksum: equal contributions appearing twice disappear, leaving the value that must be recovered.

### How this form applies

The appended value is XOR of all original values. XORing the final array gives zero, so any element can be reconstructed as XOR of all the others; outputting XOR of all final values is also a valid answer under the construction.

### How the solution works — step by step

1. Identify the bit property used by this form: The appended value is XOR of all original values.
2. XORing the final array gives zero, so any element can be reconstructed as XOR of all the others; outputting XOR of all final values is also a valid answer under the construction.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ Example final array: [4,3,2,5]
├─ 4=100
├─ 3=011
├─ 2=010
├─ 5=101
├─ XOR:
├─ 100 ^ 011 = 111
├─ 111 ^ 010 = 101
├─ 101 ^ 101 = 000
│
▼

STEP 2
│
├─ The full XOR is 0 because original-XOR x appears once in addition to the originals whose XOR is x. A valid x can be obtained using the problem's guaranteed construction; the standard implementation XORs the required subset relation.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          ^
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<int>a(n);
        for (int&x:a)cin>>x;
        // Any a[i] can serve as x when XOR of all final elements is 0.
        cout<<a[0]<<'\n';
    }
}
```


---

<a id="form-7-problem-4"></a>

## Problem 4 — [1805A — We Need the Zero](https://codeforces.com/problemset/problem/1805/A)

### What the problem wants — simple words

Choose x so that XORing every array element with x makes the XOR of the whole transformed array equal to 0, or report that it is impossible.

### Real-world mapping

Imagine every number wears the same XOR mask x. If the group size is even, identical masks cancel in pairs; if it is odd, one copy of the mask remains.

### How this form applies

Let S be XOR of the array. After XORing every element with x, total becomes `S ^ x` if n is odd and `S` if n is even.

### How the solution works — step by step

1. Identify the bit property used by this form: Let S be XOR of the array.
2. After XORing every element with x, total becomes `S ^ x` if n is odd and `S` if n is even.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[1,2,5]
├─ 1=001
├─ 2=010
├─ 5=101
├─ S=110=6
├─ n=3 odd -> choose x=S=110
│
▼

STEP 2
│
├─ 001^110=111
├─ 010^110=100
├─ 101^110=011
├─ 111^100^011=000.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          ^
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        int xr=0,x;
        for (int i=0;i<n;i++){
            cin>>x;
            xr^=x;
        }
        if (n&1) cout<<xr<<'\n';
        else cout<<(xr==0?0:-1)<<'\n';
    }
}
```

---

<a id="form-8"></a>

# Form 8 — XOR Difference Mask / Hamming Bits

## Form explained visually

**What this form means:** `a ^ b` marks exactly the bit positions where `a` and `b` differ.

**Daily-life mapping — compare two switchboards**
```text
A       101101
B       100011
XOR     001110
          ↑↑↑
       different switches

0 in XOR → same
1 in XOR → different
```

**Recognition signal**
```text
which bits differ?
minimum flips
binary distance
transform A into B
        ↓
Think: A ^ B
```

**Core tools:** `diff=a^b`, `__builtin_popcount(diff)`.

### Bit-by-bit form example

```text
A = 13, B = 10

A       = 1101
B       = 1010
          ^
          ────
diff    = 0111
           ↑↑↑
           these bit positions differ

bit      3 2 1 0
A        1 1 0 1
B        1 0 1 0
XOR      0 1 1 1

popcount(A ^ B) = 3
```

**Pattern:** a `1` in `A ^ B` marks one disagreement.


<a id="form-8-problem-1"></a>

## Problem 1 — [1918C — XOR-distance](https://codeforces.com/problemset/problem/1918/C)

### What the problem wants — simple words

Choose x with 0<=x<=r to minimize |(a XOR x) - (b XOR x)|.

### Real-world mapping

You can flip selected switches in both numbers with the same mask. The highest differing switch dominates the numerical gap, so decide important bits before small ones.

### How this form applies

Only differing bits of a and b matter. Keep the highest differing bit unchanged (it determines which value is larger), then greedily flip lower differing bits when the corresponding x-bit fits within r and reduces the gap.

### How the solution works — step by step

1. Identify the bit property used by this form: Only differing bits of a and b matter.
2. Keep the highest differing bit unchanged (it determines which value is larger), then greedily flip lower differing bits when the corresponding x-bit fits within r and reduces the gap.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=10=1010, b=3=0011. a>b.
├─ Highest differing bit is bit3:
├─ a:1, b:0. Do NOT flip it, otherwise ordering reverses with a huge change.
├─ Lower differing bits:
├─ bit0: a=0,b=1. Setting x bit0 swaps them and can reduce gap.
│
▼

STEP 2
│
├─ x starts 0000. If 0001<=r, choose it:
├─ a^x=1011=11
├─ b^x=0010=2
├─ Here it increased gap, so direction of lower bits matters: flip only bits where the larger number has 1 and smaller has 0, subject to r. Process from high to low.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          ^
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        long long a,b,r;
        cin>>a>>b>>r;
        if (a<b)swap(a,b);
        long long x=0;
        bool first=true;
        for (int i=62;i>=0;i--){
            long long bit=1LL<<i;
            bool A=a&bit,B=b&bit;
            if (A!=B){
                if (first){
                    first=false;
                    continue;
                }
                if (A&&!B && x+bit<=r)x+=bit;
            }
        }
        cout<<llabs((a^x)-(b^x))<<'\n';
    }
}
```


---

<a id="form-8-problem-2"></a>

## Problem 2 — [1362B — Johnny and His Hobbies](https://codeforces.com/problemset/problem/1362/B)

### What the problem wants — simple words

Find a positive x such that XORing every array value with x produces exactly the same set of values.

### Real-world mapping

Think of x as a relabelling mask. After relabelling every ID with XOR, the collection of IDs must look unchanged.

### How this form applies

Find positive x such that XORing every set element by x produces the same set. Try x and compare transformed multiset/set.

### How the solution works — step by step

1. Identify the bit property used by this form: Find positive x such that XORing every set element by x produces the same set.
2. Try x and compare transformed multiset/set.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ S={1,2,3,4}
├─ Try x=5 (101):
├─ 1=001 ^101=100=4
├─ 2=010 ^101=111=7 -> 7 not in S -> fail
│
▼

STEP 2
│
├─ For a candidate x, every bit where x has 1 toggles that column for all numbers. The transformed collection must exactly match the original collection.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          ^
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<int>a(n);
        set<int>s;
        for (int&v:a){
            cin>>v;
            s.insert(v);
        }
        int ans=-1;
        for (int x=1;x<1024;x++){
            set<int>t;
            for (int v:a)t.insert(v^x);
            if (t==s){
                ans=x;
                break;
            }
        }
        cout<<ans<<'\n';
    }
}
```


---

<a id="form-8-problem-3"></a>

## Problem 3 — [1421A — XORwice](https://codeforces.com/problemset/problem/1421/A)

### What the problem wants — simple words

Compute the minimum value of (a XOR x) + (b XOR x) over x.

### Real-world mapping

At each bit, you choose whether the common mask flips both switches. Bits where a and b disagree always contribute one 1; equal 1-bits can be switched off.

### How this form applies

Minimize `(a xor x) + (b xor x)`. At every bit where both `a` and `b` are 1, both terms cannot simultaneously lose that contribution; where they differ, choose x so one side has the bit. The minimum equals `a xor b`.

### How the solution works — step by step

1. Identify the bit property used by this form: Minimize `(a xor x) + (b xor x)`.
2. At every bit where both `a` and `b` are 1, both terms cannot simultaneously lose that contribution; where they differ, choose x so one side has the bit.
3. The minimum equals `a xor b`.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=5=101, b=3=011
│
▼

STEP 2
│
├─ a^b = 110 = 6
│
▼

STEP 3
│
├─ Try x = a&b = 001:
├─ a^x = 100 = 4
├─ b^x = 010 = 2
├─ sum = 6
│
▼

STEP 4
│
├─ Bit columns:
├─ bit2: 1/0 -> contribution 4
├─ bit1: 0/1 -> contribution 2
├─ bit0: 1/1 -> choose x=1 -> both become 0
├─ Total = 6.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          ^
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        long long a,b;
        cin>>a>>b;
        cout<<(a^b)<<'\n';
    }
}
```


---

<a id="form-8-problem-4"></a>

## Problem 4 — [1547D — Co-growing Sequence](https://codeforces.com/problemset/problem/1547/D)

### What the problem wants — simple words

Construct the required companion sequence so consecutive transformed values satisfy the problem’s bitwise growing condition.

### Real-world mapping

Treat every 1-bit already required by the previous value as a feature the next value must keep. Add only the missing features.

### How this form applies

Choose y[i] so `(x[i] xor y[i]) & (x[i+1] xor y[i+1]) = x[i] xor y[i]`. This means every 1-bit in previous transformed value must also be 1 in current transformed value. Add exactly the missing bits.

### How the solution works — step by step

1. Identify the bit property used by this form: Choose y[i] so `(x[i] xor y[i]) & (x[i+1] xor y[i+1]) = x[i] xor y[i]`.
2. This means every 1-bit in previous transformed value must also be 1 in current transformed value.
3. Add exactly the missing bits.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ previous transformed p=1011
├─ current x=0010
├─ Need current transformed z to contain all bits of p.
├─ Missing bits = p & ~x
├─ 1011
├─ ~0010 (within width) -> ...1101
├─ AND ->1001
├─ Choose y=1001
├─ x^y=0010^1001=1011
├─ Now p & z =1011 = p.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          ^
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<int>x(n),y(n);
        for (int&i:x)cin>>i;
        int prev=x[0];
        for (int i=1;i<n;i++){
            y[i]=prev & (~x[i]);
            prev=x[i]^y[i];
        }
        for (int v:y)cout<<v<<' ';
        cout<<'\n';
    }
}
```

---

<a id="form-9"></a>

# Form 9 — Modulo 2^k = Binary Suffix

## Form explained visually

**What this form means:** Modulo `2^k` keeps exactly the last `k` binary bits.

**Daily-life mapping — window showing only last k digits**
```text
x = 11010110

mod 2   →       0
mod 4   →      10
mod 8   →     110
mod 16  →    0110
              ↑
       suffix window grows
```

**Recognition signal**
```text
modulus must be power of two
remainders under 2^k
group by binary suffix
        ↓
Look at last k bits
```

**Core identity:** `x % (1<<k) == x & ((1<<k)-1)` for nonnegative `x`.

### Bit-by-bit form example

```text
x = 22 = 10110

22 % 8:
8 = 2^3 → keep the last 3 bits

x       = 10110
mask    = 00111
          &
          ─────
result  = 00110 = 6

So:

10110
  └──┘
 last 3 bits = 110 = 6
```

**Pattern:** `% 2^k` means keep the last `k` bits.


<a id="form-9-problem-1"></a>

## Problem 1 — [1909B — Make Almost Equal With Mod](https://codeforces.com/problemset/problem/1909/B)

### What the problem wants — simple words

Find a power-of-two modulus that makes the array produce exactly two distinct remainders.

### Real-world mapping

A modulus 2^k is like looking only through a window at the last k binary digits. Widen the window until the numbers split into exactly two groups.

### How this form applies

Try powers of two. `x mod 2^k` is exactly the last k bits. Increase suffix length until exactly two distinct suffixes occur.

### How the solution works — step by step

1. Identify the bit property used by this form: Try powers of two.
2. `x mod 2^k` is exactly the last k bits.
3. Increase suffix length until exactly two distinct suffixes occur.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[8,14,22,30]
├─ 8 =001000
├─ 14=001110
├─ 22=010110
├─ 30=011110
│
▼

STEP 2
│
├─ mod2 -> last1: 0,0,0,0 -> {0}
├─ mod4 -> last2: 00,10,10,10 -> {0,2}
├─ Exactly two -> answer 4.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0111
          &
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<long long>a(n);
        for (auto&x:a)cin>>x;
        for (int b=1;b<=61;b++){
            long long m=1LL<<b;
            set<long long>s;
            for (auto x:a)s.insert(x%m);
            if (s.size()==2){
                cout<<m<<'\n';
                break;
            }
        }
    }
}
```


---

<a id="form-9-problem-2"></a>

## Problem 2 — [1475A — Odd Divisor](https://codeforces.com/problemset/problem/1475/A)

### What the problem wants — simple words

Decide whether n has an odd divisor greater than 1.

### Real-world mapping

Imagine repeatedly cutting a number in half. If you eventually reach an odd number greater than 1, that odd number is an odd divisor. Pure powers of two only reach 1.

### How this form applies

A number has no odd divisor greater than 1 exactly when it is a power of two. Use `n & (n-1)` to test that form.

### How the solution works — step by step

1. Identify the bit property used by this form: A number has no odd divisor greater than 1 exactly when it is a power of two.
2. Use `n & (n-1)` to test that form.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ n = 8
├─ 8   = 1000
├─ 7   = 0111
├─ &     0000 -> power of two -> NO
│
▼

STEP 2
│
├─ n = 12
├─ 12  = 1100
├─ 11  = 1011
├─ &     1000 -> more than one set bit -> has odd factor 3 -> YES.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0111
          &
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        long long n;
        cin>>n;
        cout<<((n&(n-1))?"YES":"NO")<<'\n';
    }
}
```


---

<a id="form-9-problem-3"></a>

## Problem 3 — [1527A — And Then There Were K](https://codeforces.com/problemset/problem/1527/A)

### What the problem wants — simple words

Find the largest k<n such that n & (n-1) & ... & k becomes 0.

### Real-world mapping

Think of walking downward from n until every binary switch has been turned OFF at least once. The highest power-of-two boundary tells you where that happens.

### How this form applies

For maximum `k<n` with `n & (n-1) & ... & k = 0`, find the highest power of two not exceeding `n`; answer is that power minus one.

### How the solution works — step by step

1. Identify the bit property used by this form: For maximum `k<n` with `n & (n-1) & ...
2. & k = 0`, find the highest power of two not exceeding `n`; answer is that power minus one.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ n = 10 = 1010
├─ highest power of 2 <= n = 8 = 1000
├─ answer = 8-1 = 7 = 0111
│
▼

STEP 2
│
├─ The interval 10,9,8,7 contains values that clear every bit in the cumulative AND.
├─ The boundary is determined by the MSB of n.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0111
          &
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        long long n;
        cin>>n;
        long long p=1;
        while ((p<<1)<=n)p<<=1;
        cout<<p-1<<'\n';
    }
}
```


---

<a id="form-9-problem-4"></a>

## Problem 4 — [1926D — Vlad and Division](https://codeforces.com/problemset/problem/1926/D)

### What the problem wants — simple words

Group numbers so paired values have opposite bits in the required fixed bit range, minimizing the number of groups.

### Real-world mapping

Think of two puzzle pieces: every 0 needs a 1 opposite it and every 1 needs a 0. The matching piece is the fixed-width binary complement.

### How this form applies

Two values can pair when their lowest 31 bits are opposite. Partner is `x xor ((1<<31)-1)`. Greedily match complements.

### How the solution works — step by step

1. Identify the bit property used by this form: Two values can pair when their lowest 31 bits are opposite.
2. Partner is `x xor ((1<<31)-1)`.
3. Greedily match complements.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ 5-bit illustration:
├─ x=10110
├─ mask=11111
├─ partner=01001
│
▼

STEP 2
│
├─ 10110
├─ 01001
├─ -----
├─ every column is 1/0 or 0/1.
├─ If partner already waits, pair them; otherwise start a new group with x.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0111
          &
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    const long long M=(1LL<<31)-1;
    while (T--){
        int n;
        cin>>n;
        unordered_map<long long,int>cnt;
        int ans=0;
        while (n--){
            long long x;
            cin>>x,y=x^M;
            if (cnt[y])cnt[y]--;
            else{
                cnt[x]++;
                ans++;
            }
        }
        cout<<ans<<'\n';
    }
}
```

---

<a id="form-10"></a>

# Form 10 — Highest Set Bit / MSB Grouping

## Form explained visually

**What this form means:** The highest `1` bit tells the magnitude class of a positive number.

**Daily-life mapping — floors in a building**
```text
8..15   → MSB bit3 → floor 3
4..7    → MSB bit2 → floor 2
2..3    → MSB bit1 → floor 1
1       → MSB bit0 → floor 0

10 = 1010
     ↑
   highest 1 = bit3
```

**Recognition signal**
```text
group numbers by magnitude
highest differing bit dominates
same leading bit
largest power of two <= x
        ↓
Think: MSB
```

**Core tools:** `63-__builtin_clzll(x)`, powers-of-two boundaries.

### Bit-by-bit form example

```text
x = 13

x       = 1101
          ↑
          highest set bit = bit 3

Numbers with the same MSB:

8   = 1000
9   = 1001
10  = 1010
...
15  = 1111
      ↑
      same leading 1

MSB value = 2^3 = 8
```

**Pattern:** the highest differing bit often decides which whole number is larger.


<a id="form-10-problem-1"></a>

## Problem 1 — [1420B — Rock and Lever](https://codeforces.com/problemset/problem/1420/B)

### What the problem wants — simple words

Count pairs satisfying the problem’s bitwise inequality; valid pairs are characterized by having the same highest set bit.

### Real-world mapping

Put numbers on shelves by their highest 1-bit. Only two numbers from the same shelf can form a valid pair.

### How this form applies

A valid pair is characterized by the same highest set bit. Group numbers into MSB buckets and count pairs inside each bucket.

### How the solution works — step by step

1. Identify the bit property used by this form: A valid pair is characterized by the same highest set bit.
2. Group numbers into MSB buckets and count pairs inside each bucket.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[4,5,6,9]
├─ 4=0100 -> MSB2
├─ 5=0101 -> MSB2
├─ 6=0110 -> MSB2
├─ 9=1001 -> MSB3
│
▼

STEP 2
│
├─ MSB2 bucket size=3 -> C(3,2)=3
├─ MSB3 bucket size=1 -> 0
├─ answer=3.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        long long c[32]={
        }
        ,ans=0;
        while (n--){
            int x;
            cin>>x;
            int b=31-__builtin_clz(x);
            c[b]++;
        }
        for (long long v:c)ans+=v*(v-1)/2;
        cout<<ans<<'\n';
    }
}
```


---

<a id="form-10-problem-2"></a>

## Problem 2 — [1527A — And Then There Were K](https://codeforces.com/problemset/problem/1527/A)

### What the problem wants — simple words

Find the largest k<n such that n & (n-1) & ... & k becomes 0.

### Real-world mapping

Think of walking downward from n until every binary switch has been turned OFF at least once. The highest power-of-two boundary tells you where that happens.

### How this form applies

For maximum `k<n` with `n & (n-1) & ... & k = 0`, find the highest power of two not exceeding `n`; answer is that power minus one.

### How the solution works — step by step

1. Identify the bit property used by this form: For maximum `k<n` with `n & (n-1) & ...
2. & k = 0`, find the highest power of two not exceeding `n`; answer is that power minus one.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ n = 10 = 1010
├─ highest power of 2 <= n = 8 = 1000
├─ answer = 8-1 = 7 = 0111
│
▼

STEP 2
│
├─ The interval 10,9,8,7 contains values that clear every bit in the cumulative AND.
├─ The boundary is determined by the MSB of n.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        long long n;
        cin>>n;
        long long p=1;
        while ((p<<1)<=n)p<<=1;
        cout<<p-1<<'\n';
    }
}
```


---

<a id="form-10-problem-3"></a>

## Problem 3 — [1918C — XOR-distance](https://codeforces.com/problemset/problem/1918/C)

### What the problem wants — simple words

Choose x with 0<=x<=r to minimize |(a XOR x) - (b XOR x)|.

### Real-world mapping

You can flip selected switches in both numbers with the same mask. The highest differing switch dominates the numerical gap, so decide important bits before small ones.

### How this form applies

Only differing bits of a and b matter. Keep the highest differing bit unchanged (it determines which value is larger), then greedily flip lower differing bits when the corresponding x-bit fits within r and reduces the gap.

### How the solution works — step by step

1. Identify the bit property used by this form: Only differing bits of a and b matter.
2. Keep the highest differing bit unchanged (it determines which value is larger), then greedily flip lower differing bits when the corresponding x-bit fits within r and reduces the gap.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=10=1010, b=3=0011. a>b.
├─ Highest differing bit is bit3:
├─ a:1, b:0. Do NOT flip it, otherwise ordering reverses with a huge change.
├─ Lower differing bits:
├─ bit0: a=0,b=1. Setting x bit0 swaps them and can reduce gap.
│
▼

STEP 2
│
├─ x starts 0000. If 0001<=r, choose it:
├─ a^x=1011=11
├─ b^x=0010=2
├─ Here it increased gap, so direction of lower bits matters: flip only bits where the larger number has 1 and smaller has 0, subject to r. Process from high to low.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        long long a,b,r;
        cin>>a>>b>>r;
        if (a<b)swap(a,b);
        long long x=0;
        bool first=true;
        for (int i=62;i>=0;i--){
            long long bit=1LL<<i;
            bool A=a&bit,B=b&bit;
            if (A!=B){
                if (first){
                    first=false;
                    continue;
                }
                if (A&&!B && x+bit<=r)x+=bit;
            }
        }
        cout<<llabs((a^x)-(b^x))<<'\n';
    }
}
```


---

<a id="form-10-problem-4"></a>

## Problem 4 — [1669H — Maximal AND](https://codeforces.com/problemset/problem/1669/H)

### What the problem wants — simple words

Use at most k allowed bit operations to maximize the AND of all array elements.

### Real-world mapping

A final AND bit is like a team requirement: that badge appears in the team result only if every member has it. Spend your budget giving missing badges, starting with the most valuable high bit.

### How this form applies

To put bit b into the final AND, every element must have bit b=1. Cost is the number of elements missing that bit. Spend budget greedily from bit 30 down to 0.

### How the solution works — step by step

1. Identify the bit property used by this form: To put bit b into the final AND, every element must have bit b=1.
2. Cost is the number of elements missing that bit.
3. Spend budget greedily from bit 30 down to 0.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[2,1,1], k=2
├─ 2=10
├─ 1=01
├─ 1=01
│
▼

STEP 2
│
├─ bit1 column: 1,0,0 -> need 2 operations
├─ k=2 -> buy bit1
├─ array can become 10,11,11
├─ AND =10 =2
├─ No budget remains.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        long long k;
        cin>>n>>k;
        vector<int>a(n);
        for (int&x:a)cin>>x;
        long long ans=0;
        for (int b=30;b>=0;b--){
            long long need=0;
            for (int x:a)if (!(x&(1<<b)))need++;
            if (need<=k){
                k-=need;
                ans|=1LL<<b;
            }
        }
        cout<<ans<<'\n';
    }
}
```

---

<a id="form-11"></a>

# Form 11 — Lowest Set Bit / 2-adic Structure

## Form explained visually

**What this form means:** The lowest `1` bit tells how many times a number is divisible by 2.

**Daily-life mapping — trailing-zero staircase**
```text
40 = 101000
          ↑
      3 zeros

40 / 2 = 20
20 / 2 = 10
10 / 2 = 5  ← odd

lowest set bit = 8
```

**Recognition signal**
```text
trailing zeros
divide by 2 repeatedly
largest power of 2 dividing x
first differing low bit
        ↓
Think: x & -x / ctz
```

**Core tools:** `x & -x`, `__builtin_ctzll(x)`.

### Bit-by-bit form example

```text
x = 40

x       = 00101000
-x      = 11011000
          &
          ────────
lowbit  = 00001000 = 8

40 = 5 × 8 = 5 × 2^3

Trailing-zero view:

101000
   ↑↑↑
3 zeros → divisible by 2 three times

40 → 20 → 10 → 5
```

**Pattern:** `x & -x` isolates the lowest set bit.


<a id="form-11-problem-1"></a>

## Problem 1 — [1475A — Odd Divisor](https://codeforces.com/problemset/problem/1475/A)

### What the problem wants — simple words

Decide whether n has an odd divisor greater than 1.

### Real-world mapping

Imagine repeatedly cutting a number in half. If you eventually reach an odd number greater than 1, that odd number is an odd divisor. Pure powers of two only reach 1.

### How this form applies

A number has no odd divisor greater than 1 exactly when it is a power of two. Use `n & (n-1)` to test that form.

### How the solution works — step by step

1. Identify the bit property used by this form: A number has no odd divisor greater than 1 exactly when it is a power of two.
2. Use `n & (n-1)` to test that form.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ n = 8
├─ 8   = 1000
├─ 7   = 0111
├─ &     0000 -> power of two -> NO
│
▼

STEP 2
│
├─ n = 12
├─ 12  = 1100
├─ 11  = 1011
├─ &     1000 -> more than one set bit -> has odd factor 3 -> YES.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 1111
          &
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        long long n;
        cin>>n;
        cout<<((n&(n-1))?"YES":"NO")<<'\n';
    }
}
```


---

<a id="form-11-problem-2"></a>

## Problem 2 — [1909B — Make Almost Equal With Mod](https://codeforces.com/problemset/problem/1909/B)

### What the problem wants — simple words

Find a power-of-two modulus that makes the array produce exactly two distinct remainders.

### Real-world mapping

A modulus 2^k is like looking only through a window at the last k binary digits. Widen the window until the numbers split into exactly two groups.

### How this form applies

Try powers of two. `x mod 2^k` is exactly the last k bits. Increase suffix length until exactly two distinct suffixes occur.

### How the solution works — step by step

1. Identify the bit property used by this form: Try powers of two.
2. `x mod 2^k` is exactly the last k bits.
3. Increase suffix length until exactly two distinct suffixes occur.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[8,14,22,30]
├─ 8 =001000
├─ 14=001110
├─ 22=010110
├─ 30=011110
│
▼

STEP 2
│
├─ mod2 -> last1: 0,0,0,0 -> {0}
├─ mod4 -> last2: 00,10,10,10 -> {0,2}
├─ Exactly two -> answer 4.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 1111
          &
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<long long>a(n);
        for (auto&x:a)cin>>x;
        for (int b=1;b<=61;b++){
            long long m=1LL<<b;
            set<long long>s;
            for (auto x:a)s.insert(x%m);
            if (s.size()==2){
                cout<<m<<'\n';
                break;
            }
        }
    }
}
```


---

<a id="form-11-problem-3"></a>

## Problem 3 — [1527A — And Then There Were K](https://codeforces.com/problemset/problem/1527/A)

### What the problem wants — simple words

Find the largest k<n such that n & (n-1) & ... & k becomes 0.

### Real-world mapping

Think of walking downward from n until every binary switch has been turned OFF at least once. The highest power-of-two boundary tells you where that happens.

### How this form applies

For maximum `k<n` with `n & (n-1) & ... & k = 0`, find the highest power of two not exceeding `n`; answer is that power minus one.

### How the solution works — step by step

1. Identify the bit property used by this form: For maximum `k<n` with `n & (n-1) & ...
2. & k = 0`, find the highest power of two not exceeding `n`; answer is that power minus one.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ n = 10 = 1010
├─ highest power of 2 <= n = 8 = 1000
├─ answer = 8-1 = 7 = 0111
│
▼

STEP 2
│
├─ The interval 10,9,8,7 contains values that clear every bit in the cumulative AND.
├─ The boundary is determined by the MSB of n.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 1111
          &
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        long long n;
        cin>>n;
        long long p=1;
        while ((p<<1)<=n)p<<=1;
        cout<<p-1<<'\n';
    }
}
```


---

<a id="form-11-problem-4"></a>

## Problem 4 — [1688B — Patchouli’s Magical Talisman](https://codeforces.com/problemset/problem/1688/B)

### What the problem wants — simple words

Find the minimum operations for the problem’s transformation; the key distinction is whether an odd element already exists.

### Real-world mapping

Think of an odd element as a lit match. If one already exists, it can help process the even elements. If none exists, first expose an odd core by removing factors of 2.

### How this form applies

Parity is the first split. If at least one number is odd, every even number can be combined/converted with one operation in the optimal process. If all are even, choose the element with the fewest trailing zero bits to become odd first, then handle the rest.

### How the solution works — step by step

1. Identify the bit property used by this form: Parity is the first split.
2. If at least one number is odd, every even number can be combined/converted with one operation in the optimal process.
3. If all are even, choose the element with the fewest trailing zero bits to become odd first, then handle the rest.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[4,8,12]
├─ 4 =0100 -> trailing zeros 2
├─ 8 =1000 -> trailing zeros 3
├─ 12=1100 -> trailing zeros 2
├─ All even.
├─ Pick 4 (or12): need 2 divisions-by-2 style steps to expose odd bit.
├─ Then remaining n-1=2 elements each need one combining step.
├─ answer=2+2=4.
│
▼

STEP 2
│
├─ If a=[3,4,8], odd already exists -> answer = number of evens =2.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 1111
          &
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<int>a(n);
        int even=0,mn=31;
        for (int&x:a){
            cin>>x;
            if (x%2==0){
                even++;
                mn=min(mn,__builtin_ctz(x));
            }
        }
        if (even<n)cout<<even<<'\n';
        else cout<<n-1+mn<<'\n';
    }
}
```

---

<a id="form-12"></a>

# Form 12 — OR Monotonicity / Required Bits

## Form explained visually

**What this form means:** OR can turn `0→1`, but once a bit is `1`, OR cannot turn it back to `0`.

**Daily-life mapping — permanent checklist**
```text
current features  00101
new item          01010
OR                01111

Once checked ✓, OR never unchecks it.
```

**Recognition signal**
```text
build exact target using OR
forbidden extra bits
accumulate features
once bad bit appears cannot remove
        ↓
Think: OR is monotonic
```

**Core condition:** an item is safe for target `x` when `(item | x) == x`.

### Bit-by-bit form example

```text
Target = 1011

current = 0001
item    = 0010
          |
          ────
result  = 0011   ✓ still inside target

Forbidden item:

current = 0011
item    = 0100
          |
          ────
result  = 0111
           ↑
           forbidden bit became 1

OR cannot turn it back to 0.
```

**Pattern:** with OR, bits move only `0 → 1`.


<a id="form-12-problem-1"></a>

## Problem 1 — [1842B — Tenzing and Books](https://codeforces.com/problemset/problem/1842/B)

### What the problem wants — simple words

Take allowed prefixes from the three stacks so their OR becomes exactly x.

### Real-world mapping

You are collecting features to match a target checklist. Once a book introduces a feature not present in x, OR can never remove it, so that stack must stop there.

### How this form applies

OR can only add 1-bits. A book is usable only if all its 1-bits are already allowed by target x: `(v|x)==x`. Once a forbidden book appears in a stack, deeper books are inaccessible.

### How the solution works — step by step

1. Identify the bit property used by this form: OR can only add 1-bits.
2. A book is usable only if all its 1-bits are already allowed by target x: `(v|x)==x`.
3. Once a forbidden book appears in a stack, deeper books are inaccessible.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ target x=101
├─ book v=001: 001|101=101 -> safe
├─ cur=000|001=001
├─ book v=100: 100|101=101 -> safe
├─ cur=001|100=101 -> reached target
│
▼

STEP 2
│
├─ book 010 would be forbidden:
├─ 010|101=111 !=101, and OR could never remove that bit1.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          |
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n,x;
        cin>>n>>x;
        vector<vector<int>>a(3,vector<int>(n));
        for (auto&v:a)for (int&z:v)cin>>z;
        int cur=0;
        for (auto&v:a)for (int z:v){
            if ((z|x)!=x)break;
            cur|=z;
        }
        cout<<(cur==x?"Yes":"No")<<'\n';
    }
}
```


---

<a id="form-12-problem-2"></a>

## Problem 2 — [1547D — Co-growing Sequence](https://codeforces.com/problemset/problem/1547/D)

### What the problem wants — simple words

Construct the required companion sequence so consecutive transformed values satisfy the problem’s bitwise growing condition.

### Real-world mapping

Treat every 1-bit already required by the previous value as a feature the next value must keep. Add only the missing features.

### How this form applies

Choose y[i] so `(x[i] xor y[i]) & (x[i+1] xor y[i+1]) = x[i] xor y[i]`. This means every 1-bit in previous transformed value must also be 1 in current transformed value. Add exactly the missing bits.

### How the solution works — step by step

1. Identify the bit property used by this form: Choose y[i] so `(x[i] xor y[i]) & (x[i+1] xor y[i+1]) = x[i] xor y[i]`.
2. This means every 1-bit in previous transformed value must also be 1 in current transformed value.
3. Add exactly the missing bits.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ previous transformed p=1011
├─ current x=0010
├─ Need current transformed z to contain all bits of p.
├─ Missing bits = p & ~x
├─ 1011
├─ ~0010 (within width) -> ...1101
├─ AND ->1001
├─ Choose y=1001
├─ x^y=0010^1001=1011
├─ Now p & z =1011 = p.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          |
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<int>x(n),y(n);
        for (int&i:x)cin>>i;
        int prev=x[0];
        for (int i=1;i<n;i++){
            y[i]=prev & (~x[i]);
            prev=x[i]^y[i];
        }
        for (int v:y)cout<<v<<' ';
        cout<<'\n';
    }
}
```


---

<a id="form-12-problem-3"></a>

## Problem 3 — [1042B — Vitamins](https://codeforces.com/problemset/problem/1042/B)

### What the problem wants — simple words

Buy the cheapest set of juices that together provide vitamins A, B, and C.

### Real-world mapping

A, B, C are three checkboxes. Encode the checked boxes as bits; OR combines the vitamins supplied by purchases.

### How this form applies

Encode vitamins A,B,C as bits 0,1,2. Each juice is a mask. OR combines acquired vitamins; DP over 8 masks gives minimum cost.

### How the solution works — step by step

1. Identify the bit property used by this form: Encode vitamins A,B,C as bits 0,1,2.
2. Each juice is a mask.
3. OR combines acquired vitamins; DP over 8 masks gives minimum cost.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ A=001,B=010,C=100
├─ juice AB ->011 cost5
├─ juice C  ->100 cost3
├─ state 000 --buy AB-->011
├─ 011 |100 =111
├─ cost=8, all vitamins covered.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          |
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin>>n;
    const int INF=1e9;
    vector<int>dp(8,INF);
    dp[0]=0;
    while (n--){
        int c;
        string s;
        cin>>c>>s;
        int m=0;
        for (char ch:s)m|=1<<(ch-'A');
        auto ndp=dp;
        for (int st=0;st<8;st++)ndp[st|m]=min(ndp[st|m],dp[st]+c);
        dp=ndp;
    }
    cout<<(dp[7]>=INF?-1:dp[7])<<'\n';
}
```


---

<a id="form-12-problem-4"></a>

## Problem 4 — [1829C — Mr. Perfectly Fine](https://codeforces.com/problemset/problem/1829/C)

### What the problem wants — simple words

Find the minimum time/cost to obtain both required skills.

### Real-world mapping

There are only two skill switches. An item can give 01, 10, or 11; combine masks with OR and keep the cheapest way to reach 11.

### How this form applies

Two skills are a 2-bit mask: 01,10,11. Keep cheapest cost for each mask. Answer is min(cost11, cost01+cost10).

### How the solution works — step by step

1. Identify the bit property used by this form: Two skills are a 2-bit mask: 01,10,11.
2. Keep cheapest cost for each mask.
3. Answer is min(cost11, cost01+cost10).
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ book1 skill 10 cost4 -> mask2
├─ book2 skill 01 cost3 -> mask1
├─ book3 skill 11 cost10 -> mask3
├─ Combine mask2|mask1=11 cost7
├─ min(7,10)=7.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          |
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        const int INF=1e9;
        int best[4]={
            0,INF,INF,INF
        }
        ;
        while (n--){
            int c;
            string s;
            cin>>c>>s;
            int m=(s[0]-'0')*2+(s[1]-'0');
            best[m]=min(best[m],c);
        }
        int ans=min(best[3],best[1]+best[2]);
        cout<<(ans>=INF?-1:ans)<<'\n';
    }
}
```

---

<a id="form-13"></a>

# Form 13 — AND Monotonicity / Maximal AND

## Form explained visually

**What this form means:** A bit survives AND only if every participating number has that bit set.

**Daily-life mapping — unanimous team vote**
```text
Member A   1110
Member B   1011
Member C   1010
AND        1010

bit survives ⇔ EVERY member voted 1
```

**Recognition signal**
```text
maximize AND
common bits
all elements must contain bit
adding more numbers can only remove 1s
        ↓
Think: unanimous bits
```

**Core idea:** evaluate candidate bits from high to low and ensure all required elements support them.

### Bit-by-bit form example

```text
a = 14 = 1110
b = 11 = 1011
c = 10 = 1010

a       = 1110
b       = 1011
          &
          ────
a&b     = 1010

a&b     = 1010
c       = 1010
          &
          ────
result  = 1010

Column view:

bit3: 1 & 1 & 1 = 1 ✓
bit2: 1 & 0 & 0 = 0
bit1: 1 & 1 & 1 = 1 ✓
bit0: 0 & 1 & 0 = 0
```

**Pattern:** an AND bit survives only if everybody has `1` there.


<a id="form-13-problem-1"></a>

## Problem 1 — [1669H — Maximal AND](https://codeforces.com/problemset/problem/1669/H)

### What the problem wants — simple words

Use at most k allowed bit operations to maximize the AND of all array elements.

### Real-world mapping

A final AND bit is like a team requirement: that badge appears in the team result only if every member has it. Spend your budget giving missing badges, starting with the most valuable high bit.

### How this form applies

To put bit b into the final AND, every element must have bit b=1. Cost is the number of elements missing that bit. Spend budget greedily from bit 30 down to 0.

### How the solution works — step by step

1. Identify the bit property used by this form: To put bit b into the final AND, every element must have bit b=1.
2. Cost is the number of elements missing that bit.
3. Spend budget greedily from bit 30 down to 0.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[2,1,1], k=2
├─ 2=10
├─ 1=01
├─ 1=01
│
▼

STEP 2
│
├─ bit1 column: 1,0,0 -> need 2 operations
├─ k=2 -> buy bit1
├─ array can become 10,11,11
├─ AND =10 =2
├─ No budget remains.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        long long k;
        cin>>n>>k;
        vector<int>a(n);
        for (int&x:a)cin>>x;
        long long ans=0;
        for (int b=30;b>=0;b--){
            long long need=0;
            for (int x:a)if (!(x&(1<<b)))need++;
            if (need<=k){
                k-=need;
                ans|=1LL<<b;
            }
        }
        cout<<ans<<'\n';
    }
}
```


---

<a id="form-13-problem-2"></a>

## Problem 2 — [1514B — AND 0, Sum Big](https://codeforces.com/problemset/problem/1514/B)

### What the problem wants — simple words

Count arrays satisfying the problem’s AND/sum conditions.

### Real-world mapping

Handle one bit-column at a time. For the total AND to be 0, each column must contain at least one 0; independent column choices multiply.

### How this form applies

For every bit position, not all n numbers may have that bit=1, otherwise total AND is nonzero. There are `2^n-1` valid column assignments independently for each of k bits.

### How the solution works — step by step

1. Identify the bit property used by this form: For every bit position, not all n numbers may have that bit=1, otherwise total AND is nonzero.
2. There are `2^n-1` valid column assignments independently for each of k bits.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ n=3, one bit column has 2^3=8 assignments:
├─ 000 001 010 011 100 101 110 111
├─ Only 111 makes AND bit=1.
├─ So 7 valid assignments per bit.
├─ For k independent bits -> 7^k.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

const long long MOD=1e9+7;

long long pw(long long a,long long e){
    long long r=1;
    while (e){
        if (e&1)r=r*a%MOD;
        a=a*a%MOD;
        e>>=1;
    }
    return r;
}

int main(){
    int T;
    cin>>T;
    while (T--){
        long long n,k;
        cin>>n>>k;
        cout<<pw((pw(2,n)-1+MOD)%MOD,k)<<'\n';
    }
}
```


---

<a id="form-13-problem-3"></a>

## Problem 3 — [1991B — AND Reconstruction](https://codeforces.com/problemset/problem/1991/B)

### What the problem wants — simple words

Construct an array whose adjacent AND values equal the given array b, or report that no such array exists.

### Real-world mapping

Each b[i] is a contract between neighboring positions: every 1-bit in the contract must exist on both sides. Merge neighboring contracts with OR, then verify them with AND.

### How this form applies

Given b[i]=a[i]&a[i+1], construct a. A standard candidate uses OR of adjacent constraints: internal `a[i]=b[i-1]|b[i]`; endpoints use their only constraint. Then verify.

### How the solution works — step by step

1. Identify the bit property used by this form: Given b[i]=a[i]&a[i+1], construct a.
2. A standard candidate uses OR of adjacent constraints: internal `a[i]=b[i-1]|b[i]`; endpoints use their only constraint.
3. Then verify.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ b=[2,0]
├─ 2=10, 0=00
├─ Construct:
├─ a0=b0=10
├─ a1=b0|b1=10|00=10
├─ a2=b1=00
├─ Check:
├─ a0&a1=10&10=10 = b0
├─ a1&a2=10&00=00 = b1
├─ works.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<int>b(n-1);
        for (int&x:b)cin>>x;
        vector<int>a(n);
        if (n==1){
            cout<<0<<'\n';
            continue;
        }
        a[0]=b[0];
        a[n-1]=b[n-2];
        for (int i=1;i<n-1;i++)a[i]=b[i-1]|b[i];
        bool ok=true;
        for (int i=0;i<n-1;i++)if ((a[i]&a[i+1])!=b[i])ok=false;
        if (!ok)cout<<-1<<'\n';
        else{
            for (int x:a)cout<<x<<' ';
            cout<<'\n';
        }
    }
}
```


---

<a id="form-13-problem-4"></a>

## Problem 4 — [1903B — StORage room](https://codeforces.com/problemset/problem/1903/B)

### What the problem wants — simple words

Reconstruct numbers whose pairwise OR values equal the given matrix, or report that the matrix is impossible.

### Real-world mapping

Each matrix entry says which switches must be ON when two devices are combined. Infer safe switches for each device, then replay every pair to verify the specification.

### How this form applies

Need `a[i] | a[j] = M[i][j]`. For each i, AND all M[i][j] over j!=i to build the bits that can safely remain in a[i], then verify every pair.

### How the solution works — step by step

1. Identify the bit property used by this form: Need `a[i] | a[j] = M[i][j]`.
2. For each i, AND all M[i][j] over j!=i to build the bits that can safely remain in a[i], then verify every pair.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ One bit only, three nodes:
├─ M01=1, M02=0, M12=1
├─ M02=0 forces a0=0 and a2=0 at this bit.
├─ Then M01=1 forces a1=1.
├─ M12=1 is satisfied by a1=1,a2=0.
├─ Candidate bit column: [0,1,0].
│
▼

STEP 2
│
├─ Construction followed by full verification catches inconsistent matrices.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<vector<int>>m(n,vector<int>(n));
        for (auto&r:m)for (int&x:r)cin>>x;
        vector<int>a(n,(1<<30)-1);
        for (int i=0;i<n;i++)for (int j=0;j<n;j++)if (i!=j)a[i]&=m[i][j];
        bool ok=true;
        for (int i=0;i<n;i++)for (int j=0;j<n;j++)if (i!=j && (a[i]|a[j])!=m[i][j])ok=false;
        if (!ok)cout<<"NO\n";
        else{
            cout<<"YES\n";
            for (int x:a)cout<<x<<' ';
            cout<<'\n';
        }
    }
}
```

---

<a id="form-14"></a>

# Form 14 — Bit Frequency / Majority Per Bit

## Form explained visually

**What this form means:** Count zeros/ones independently in each bit column.

**Daily-life mapping — election per switch**
```text
numbers:
101
111
001
100
---
bit2 votes: 1 1 0 1 → majority 1
bit1 votes: 0 1 0 0 → majority 0
bit0 votes: 1 1 1 0 → majority 1

result = 101
```

**Recognition signal**
```text
majority bit
minimum changes per bit
count how many numbers contain bit b
construct answer column by column
        ↓
Think: frequency[bit]
```

**Core pattern:** loop bits, then loop elements and count `(x>>b)&1`.

### Bit-by-bit form example

```text
Numbers:

5 = 101
7 = 111
1 = 001
4 = 100

          b2 b1 b0
5          1  0  1
7          1  1  1
1          0  0  1
4          1  0  0
           --------
ones       3  1  3
zeros      1  3  1

Majority:
b2 → 1
b1 → 0
b0 → 1

answer = 101 = 5
```

**Pattern:** solve each bit column independently by counting `0`s and `1`s.


<a id="form-14-problem-1"></a>

## Problem 1 — [1625A — Ancient Civilization](https://codeforces.com/problemset/problem/1625/A)

### What the problem wants — simple words

Construct the number required by choosing each bit from the majority of the input numbers.

### Real-world mapping

Treat every bit position as an election: count 0-votes and 1-votes independently, then choose the majority for that column.

### How this form applies

For each bit independently, choose the majority bit to minimize total differing positions.

### How the solution works — step by step

1. Identify the bit property used by this form: For each bit independently, choose the majority bit to minimize total differing positions.
2. Write the relevant values in binary and inspect only the bit positions that affect the condition.
3. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ numbers:
├─ 1011
├─ 1100
├─ 0111
│
▼

STEP 2
│
├─ bit3:1,1,0 -> ones2 -> answer bit=1
├─ bit2:0,1,1 -> ones2 -> 1
├─ bit1:1,0,1 -> ones2 -> 1
├─ bit0:1,0,1 -> ones2 -> 1
├─ answer=1111.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n,l;
        cin>>n>>l;
        vector<int>a(n);
        for (int&x:a)cin>>x;
        int ans=0;
        for (int b=0;b<l;b++){
            int one=0;
            for (int x:a)one+=(x>>b)&1;
            if (one>n-one)ans|=1<<b;
        }
        cout<<ans<<'\n';
    }
}
```


---

<a id="form-14-problem-2"></a>

## Problem 2 — [1669H — Maximal AND](https://codeforces.com/problemset/problem/1669/H)

### What the problem wants — simple words

Use at most k allowed bit operations to maximize the AND of all array elements.

### Real-world mapping

A final AND bit is like a team requirement: that badge appears in the team result only if every member has it. Spend your budget giving missing badges, starting with the most valuable high bit.

### How this form applies

To put bit b into the final AND, every element must have bit b=1. Cost is the number of elements missing that bit. Spend budget greedily from bit 30 down to 0.

### How the solution works — step by step

1. Identify the bit property used by this form: To put bit b into the final AND, every element must have bit b=1.
2. Cost is the number of elements missing that bit.
3. Spend budget greedily from bit 30 down to 0.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[2,1,1], k=2
├─ 2=10
├─ 1=01
├─ 1=01
│
▼

STEP 2
│
├─ bit1 column: 1,0,0 -> need 2 operations
├─ k=2 -> buy bit1
├─ array can become 10,11,11
├─ AND =10 =2
├─ No budget remains.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        long long k;
        cin>>n>>k;
        vector<int>a(n);
        for (int&x:a)cin>>x;
        long long ans=0;
        for (int b=30;b>=0;b--){
            long long need=0;
            for (int x:a)if (!(x&(1<<b)))need++;
            if (need<=k){
                k-=need;
                ans|=1LL<<b;
            }
        }
        cout<<ans<<'\n';
    }
}
```


---

<a id="form-14-problem-3"></a>

## Problem 3 — [1514B — AND 0, Sum Big](https://codeforces.com/problemset/problem/1514/B)

### What the problem wants — simple words

Count arrays satisfying the problem’s AND/sum conditions.

### Real-world mapping

Handle one bit-column at a time. For the total AND to be 0, each column must contain at least one 0; independent column choices multiply.

### How this form applies

For every bit position, not all n numbers may have that bit=1, otherwise total AND is nonzero. There are `2^n-1` valid column assignments independently for each of k bits.

### How the solution works — step by step

1. Identify the bit property used by this form: For every bit position, not all n numbers may have that bit=1, otherwise total AND is nonzero.
2. There are `2^n-1` valid column assignments independently for each of k bits.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ n=3, one bit column has 2^3=8 assignments:
├─ 000 001 010 011 100 101 110 111
├─ Only 111 makes AND bit=1.
├─ So 7 valid assignments per bit.
├─ For k independent bits -> 7^k.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

const long long MOD=1e9+7;

long long pw(long long a,long long e){
    long long r=1;
    while (e){
        if (e&1)r=r*a%MOD;
        a=a*a%MOD;
        e>>=1;
    }
    return r;
}

int main(){
    int T;
    cin>>T;
    while (T--){
        long long n,k;
        cin>>n>>k;
        cout<<pw((pw(2,n)-1+MOD)%MOD,k)<<'\n';
    }
}
```


---

<a id="form-14-problem-4"></a>

## Problem 4 — [1367B — Even Array](https://codeforces.com/problemset/problem/1367/B)

### What the problem wants — simple words

Make every position valid: an even index must contain an even value and an odd index must contain an odd value, using the minimum swaps.

### Real-world mapping

Think of two parking zones: EVEN cars belong in EVEN slots and ODD cars in ODD slots. One swap fixes one car misplaced in each zone.

### How this form applies

The condition is parity only. Compare the least-significant bit of the index and value. A swap fixes one odd-at-even mismatch together with one even-at-odd mismatch.

### How the solution works — step by step

1. Identify the bit property used by this form: The condition is parity only.
2. Compare the least-significant bit of the index and value.
3. A swap fixes one odd-at-even mismatch together with one even-at-odd mismatch.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ Example: a = [3,2,7,6]
│
▼

STEP 2
│
├─ index      0    1    2    3
├─ index LSB  0    1    0    1
├─ value      3    2    7    6
├─ binary    11   10  111  110
├─ value LSB  1    0    1    0
├─             X    X    X    X
│
▼

STEP 3
│
├─ odd value at even index  = 2
├─ Even value at odd index  = 2
├─ Each swap consumes one of each -> answer = 2.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        int x,y=0,z=0;
        for (int i=0;i<n;i++){
            cin>>x;
            if ((x&1)!=(i&1)){
                if (x&1)y++;
                else z++;
            }
        }
        cout<<(y==z?y:-1)<<'\n';
    }
}
```

---

<a id="form-15"></a>

# Form 15 — Bit-by-Bit Constraint Construction

## Form explained visually

**What this form means:** Construct an answer by deciding each bit independently from the constraints.

**Daily-life mapping — building from a specification sheet**
```text
Specification:
bit3 must be 1
bit2 may be 0/1
bit1 must be 0
bit0 must be 1

        ↓ decide column by column

answer: 1 ? 0 1
```

**Recognition signal**
```text
construct x
AND/OR/XOR equations
requirements on individual bits
need any valid answer
        ↓
Split equation into independent bit columns
```

**Core method:** for each bit, write the 0/1 truth table, derive allowed states, set answer bit, then verify.

### Bit-by-bit form example

```text
Construct minimum x such that:

x | A = B

A = 0101
B = 1101

bit      3 2 1 0
A        0 1 0 1
B        1 1 0 1
x        ? ? ? ?

bit3: 0 | ? must become 1 → x3 = 1
bit2: 1 already gives 1      → x2 = 0
bit1: target is 0            → x1 = 0
bit0: 1 already gives 1      → x0 = 0

x       = 1000
A       = 0101
          |
          ────
B       = 1101
```

**Pattern:** convert the equation into one tiny constraint per bit.


<a id="form-15-problem-1"></a>

## Problem 1 — [1903B — StORage room](https://codeforces.com/problemset/problem/1903/B)

### What the problem wants — simple words

Reconstruct numbers whose pairwise OR values equal the given matrix, or report that the matrix is impossible.

### Real-world mapping

Each matrix entry says which switches must be ON when two devices are combined. Infer safe switches for each device, then replay every pair to verify the specification.

### How this form applies

Need `a[i] | a[j] = M[i][j]`. For each i, AND all M[i][j] over j!=i to build the bits that can safely remain in a[i], then verify every pair.

### How the solution works — step by step

1. Identify the bit property used by this form: Need `a[i] | a[j] = M[i][j]`.
2. For each i, AND all M[i][j] over j!=i to build the bits that can safely remain in a[i], then verify every pair.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ One bit only, three nodes:
├─ M01=1, M02=0, M12=1
├─ M02=0 forces a0=0 and a2=0 at this bit.
├─ Then M01=1 forces a1=1.
├─ M12=1 is satisfied by a1=1,a2=0.
├─ Candidate bit column: [0,1,0].
│
▼

STEP 2
│
├─ Construction followed by full verification catches inconsistent matrices.

RESULT
```
#### Visual bit operation

```text
x       = 0101
y       = 1000
          |
          ────
result  = 1101
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<vector<int>>m(n,vector<int>(n));
        for (auto&r:m)for (int&x:r)cin>>x;
        vector<int>a(n,(1<<30)-1);
        for (int i=0;i<n;i++)for (int j=0;j<n;j++)if (i!=j)a[i]&=m[i][j];
        bool ok=true;
        for (int i=0;i<n;i++)for (int j=0;j<n;j++)if (i!=j && (a[i]|a[j])!=m[i][j])ok=false;
        if (!ok)cout<<"NO\n";
        else{
            cout<<"YES\n";
            for (int x:a)cout<<x<<' ';
            cout<<'\n';
        }
    }
}
```


---

<a id="form-15-problem-2"></a>

## Problem 2 — [1991B — AND Reconstruction](https://codeforces.com/problemset/problem/1991/B)

### What the problem wants — simple words

Construct an array whose adjacent AND values equal the given array b, or report that no such array exists.

### Real-world mapping

Each b[i] is a contract between neighboring positions: every 1-bit in the contract must exist on both sides. Merge neighboring contracts with OR, then verify them with AND.

### How this form applies

Given b[i]=a[i]&a[i+1], construct a. A standard candidate uses OR of adjacent constraints: internal `a[i]=b[i-1]|b[i]`; endpoints use their only constraint. Then verify.

### How the solution works — step by step

1. Identify the bit property used by this form: Given b[i]=a[i]&a[i+1], construct a.
2. A standard candidate uses OR of adjacent constraints: internal `a[i]=b[i-1]|b[i]`; endpoints use their only constraint.
3. Then verify.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ b=[2,0]
├─ 2=10, 0=00
├─ Construct:
├─ a0=b0=10
├─ a1=b0|b1=10|00=10
├─ a2=b1=00
├─ Check:
├─ a0&a1=10&10=10 = b0
├─ a1&a2=10&00=00 = b1
├─ works.

RESULT
```
#### Visual bit operation

```text
x       = 0101
y       = 1000
          |
          ────
result  = 1101
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<int>b(n-1);
        for (int&x:b)cin>>x;
        vector<int>a(n);
        if (n==1){
            cout<<0<<'\n';
            continue;
        }
        a[0]=b[0];
        a[n-1]=b[n-2];
        for (int i=1;i<n-1;i++)a[i]=b[i-1]|b[i];
        bool ok=true;
        for (int i=0;i<n-1;i++)if ((a[i]&a[i+1])!=b[i])ok=false;
        if (!ok)cout<<-1<<'\n';
        else{
            for (int x:a)cout<<x<<' ';
            cout<<'\n';
        }
    }
}
```


---

<a id="form-15-problem-3"></a>

## Problem 3 — [1547D — Co-growing Sequence](https://codeforces.com/problemset/problem/1547/D)

### What the problem wants — simple words

Construct the required companion sequence so consecutive transformed values satisfy the problem’s bitwise growing condition.

### Real-world mapping

Treat every 1-bit already required by the previous value as a feature the next value must keep. Add only the missing features.

### How this form applies

Choose y[i] so `(x[i] xor y[i]) & (x[i+1] xor y[i+1]) = x[i] xor y[i]`. This means every 1-bit in previous transformed value must also be 1 in current transformed value. Add exactly the missing bits.

### How the solution works — step by step

1. Identify the bit property used by this form: Choose y[i] so `(x[i] xor y[i]) & (x[i+1] xor y[i+1]) = x[i] xor y[i]`.
2. This means every 1-bit in previous transformed value must also be 1 in current transformed value.
3. Add exactly the missing bits.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ previous transformed p=1011
├─ current x=0010
├─ Need current transformed z to contain all bits of p.
├─ Missing bits = p & ~x
├─ 1011
├─ ~0010 (within width) -> ...1101
├─ AND ->1001
├─ Choose y=1001
├─ x^y=0010^1001=1011
├─ Now p & z =1011 = p.

RESULT
```
#### Visual bit operation

```text
x       = 0101
y       = 1000
          |
          ────
result  = 1101
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<int>x(n),y(n);
        for (int&i:x)cin>>i;
        int prev=x[0];
        for (int i=1;i<n;i++){
            y[i]=prev & (~x[i]);
            prev=x[i]^y[i];
        }
        for (int v:y)cout<<v<<' ';
        cout<<'\n';
    }
}
```


---

<a id="form-15-problem-4"></a>

## Problem 4 — [1842B — Tenzing and Books](https://codeforces.com/problemset/problem/1842/B)

### What the problem wants — simple words

Take allowed prefixes from the three stacks so their OR becomes exactly x.

### Real-world mapping

You are collecting features to match a target checklist. Once a book introduces a feature not present in x, OR can never remove it, so that stack must stop there.

### How this form applies

OR can only add 1-bits. A book is usable only if all its 1-bits are already allowed by target x: `(v|x)==x`. Once a forbidden book appears in a stack, deeper books are inaccessible.

### How the solution works — step by step

1. Identify the bit property used by this form: OR can only add 1-bits.
2. A book is usable only if all its 1-bits are already allowed by target x: `(v|x)==x`.
3. Once a forbidden book appears in a stack, deeper books are inaccessible.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ target x=101
├─ book v=001: 001|101=101 -> safe
├─ cur=000|001=001
├─ book v=100: 100|101=101 -> safe
├─ cur=001|100=101 -> reached target
│
▼

STEP 2
│
├─ book 010 would be forbidden:
├─ 010|101=111 !=101, and OR could never remove that bit1.

RESULT
```
#### Visual bit operation

```text
x       = 0101
y       = 1000
          |
          ────
result  = 1101
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n,x;
        cin>>n>>x;
        vector<vector<int>>a(3,vector<int>(n));
        for (auto&v:a)for (int&z:v)cin>>z;
        int cur=0;
        for (auto&v:a)for (int z:v){
            if ((z|x)!=x)break;
            cur|=z;
        }
        cout<<(cur==x?"Yes":"No")<<'\n';
    }
}
```

---

<a id="form-16"></a>

# Form 16 — Pairwise XOR Contribution

## Form explained visually

**What this form means:** For a fixed bit, pair XOR is 1 only when one number has 0 and the other has 1.

**Daily-life mapping — pair people wearing opposite badges**
```text
At bit b:
zeros = Z
ones  = O

valid opposite pairs = Z * O

Each such pair contributes 2^b
        ↓
contribution = Z * O * 2^b
```

**Recognition signal**
```text
sum XOR over all pairs
count pair contribution
n too large for O(n²)
        ↓
Count zeros/ones per bit
```

**Core idea:** independent contribution of every bit.

### Bit-by-bit form example

```text
At bit b = 3, four numbers have:

values   A B C D
bit3     0 1 0 1

zeros = 2
ones  = 2

XOR is 1 only for opposite bits:

0 ^ 1 = 1
1 ^ 0 = 1

opposite pairs = zeros × ones
               = 2 × 2
               = 4

Each contributes 2^3 = 8.

bit3 contribution = 4 × 8 = 32
```

**Pattern:** pairwise XOR contribution per bit = `zeros × ones × 2^b`.


<a id="form-16-problem-1"></a>

## Problem 1 — [1421A — XORwice](https://codeforces.com/problemset/problem/1421/A)

### What the problem wants — simple words

Compute the minimum value of (a XOR x) + (b XOR x) over x.

### Real-world mapping

At each bit, you choose whether the common mask flips both switches. Bits where a and b disagree always contribute one 1; equal 1-bits can be switched off.

### How this form applies

Minimize `(a xor x) + (b xor x)`. At every bit where both `a` and `b` are 1, both terms cannot simultaneously lose that contribution; where they differ, choose x so one side has the bit. The minimum equals `a xor b`.

### How the solution works — step by step

1. Identify the bit property used by this form: Minimize `(a xor x) + (b xor x)`.
2. At every bit where both `a` and `b` are 1, both terms cannot simultaneously lose that contribution; where they differ, choose x so one side has the bit.
3. The minimum equals `a xor b`.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=5=101, b=3=011
│
▼

STEP 2
│
├─ a^b = 110 = 6
│
▼

STEP 3
│
├─ Try x = a&b = 001:
├─ a^x = 100 = 4
├─ b^x = 010 = 2
├─ sum = 6
│
▼

STEP 4
│
├─ Bit columns:
├─ bit2: 1/0 -> contribution 4
├─ bit1: 0/1 -> contribution 2
├─ bit0: 1/1 -> choose x=1 -> both become 0
├─ Total = 6.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          ^
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        long long a,b;
        cin>>a>>b;
        cout<<(a^b)<<'\n';
    }
}
```


---

<a id="form-16-problem-2"></a>

## Problem 2 — [1918C — XOR-distance](https://codeforces.com/problemset/problem/1918/C)

### What the problem wants — simple words

Choose x with 0<=x<=r to minimize |(a XOR x) - (b XOR x)|.

### Real-world mapping

You can flip selected switches in both numbers with the same mask. The highest differing switch dominates the numerical gap, so decide important bits before small ones.

### How this form applies

Only differing bits of a and b matter. Keep the highest differing bit unchanged (it determines which value is larger), then greedily flip lower differing bits when the corresponding x-bit fits within r and reduces the gap.

### How the solution works — step by step

1. Identify the bit property used by this form: Only differing bits of a and b matter.
2. Keep the highest differing bit unchanged (it determines which value is larger), then greedily flip lower differing bits when the corresponding x-bit fits within r and reduces the gap.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=10=1010, b=3=0011. a>b.
├─ Highest differing bit is bit3:
├─ a:1, b:0. Do NOT flip it, otherwise ordering reverses with a huge change.
├─ Lower differing bits:
├─ bit0: a=0,b=1. Setting x bit0 swaps them and can reduce gap.
│
▼

STEP 2
│
├─ x starts 0000. If 0001<=r, choose it:
├─ a^x=1011=11
├─ b^x=0010=2
├─ Here it increased gap, so direction of lower bits matters: flip only bits where the larger number has 1 and smaller has 0, subject to r. Process from high to low.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          ^
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        long long a,b,r;
        cin>>a>>b>>r;
        if (a<b)swap(a,b);
        long long x=0;
        bool first=true;
        for (int i=62;i>=0;i--){
            long long bit=1LL<<i;
            bool A=a&bit,B=b&bit;
            if (A!=B){
                if (first){
                    first=false;
                    continue;
                }
                if (A&&!B && x+bit<=r)x+=bit;
            }
        }
        cout<<llabs((a^x)-(b^x))<<'\n';
    }
}
```


---

<a id="form-16-problem-3"></a>

## Problem 3 — [1698A — XOR Mixup](https://codeforces.com/problemset/problem/1698/A)

### What the problem wants — simple words

Recover the required value using the XOR relation among all given numbers.

### Real-world mapping

XOR is a cancellation checksum: equal contributions appearing twice disappear, leaving the value that must be recovered.

### How this form applies

The appended value is XOR of all original values. XORing the final array gives zero, so any element can be reconstructed as XOR of all the others; outputting XOR of all final values is also a valid answer under the construction.

### How the solution works — step by step

1. Identify the bit property used by this form: The appended value is XOR of all original values.
2. XORing the final array gives zero, so any element can be reconstructed as XOR of all the others; outputting XOR of all final values is also a valid answer under the construction.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ Example final array: [4,3,2,5]
├─ 4=100
├─ 3=011
├─ 2=010
├─ 5=101
├─ XOR:
├─ 100 ^ 011 = 111
├─ 111 ^ 010 = 101
├─ 101 ^ 101 = 000
│
▼

STEP 2
│
├─ The full XOR is 0 because original-XOR x appears once in addition to the originals whose XOR is x. A valid x can be obtained using the problem's guaranteed construction; the standard implementation XORs the required subset relation.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          ^
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<int>a(n);
        for (int&x:a)cin>>x;
        // Any a[i] can serve as x when XOR of all final elements is 0.
        cout<<a[0]<<'\n';
    }
}
```


---

<a id="form-16-problem-4"></a>

## Problem 4 — [1516B — AGAGA XOOORRR](https://codeforces.com/problemset/problem/1516/B)

### What the problem wants — simple words

Decide whether the array can be split according to the problem so the required segment XOR values match.

### Real-world mapping

Think of prefix XOR as a running checksum. Equal segment checksums mean the boundaries can be placed where the running XOR reaches the needed states.

### How this form applies

Partition into at least two segments with equal XOR. If total XOR is 0, two parts can work. Otherwise, scan for two prefixes/segments whose XOR equals total XOR, giving three equal-XOR parts.

### How the solution works — step by step

1. Identify the bit property used by this form: Partition into at least two segments with equal XOR.
2. If total XOR is 0, two parts can work.
3. Otherwise, scan for two prefixes/segments whose XOR equals total XOR, giving three equal-XOR parts.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[1,2,3]
├─ 1=01,2=10,3=11
├─ total=01^10^11=00 -> YES immediately.
│
▼

STEP 2
│
├─ If total S!=0, look for:
├─ segment1 XOR=S
├─ segment2 XOR=S
├─ then remaining XOR = S^S^S = S,
├─ so three segments have equal XOR.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          ^
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<int>a(n);
        int S=0;
        for (int&x:a){
            cin>>x;
            S^=x;
        }
        if (S==0){
            cout<<"YES\n";
            continue;
        }
        int cur=0,cnt=0;
        for (int i=0;i<n-1;i++){
            cur^=a[i];
            if (cur==S){
                cnt++;
                cur=0;
            }
        }
        cout<<(cnt>=2?"YES":"NO")<<'\n';
    }
}
```

---

<a id="form-17"></a>

# Form 17 — Pairwise AND / OR Contribution

## Form explained visually

**What this form means:** Count how many pairs activate each bit under AND or OR instead of checking every pair.

**Daily-life mapping — two-person access rules**
```text
AND badge:
both must have badge
pairs = C(ones,2)

OR badge:
at least one has badge
pairs = totalPairs - C(zeros,2)
```

**Recognition signal**
```text
sum AND/OR over all pairs
O(n²) impossible
pair contribution per bit
        ↓
Count ones and zeros
```

**Core idea:** multiply number of qualifying pairs by `2^b`.

### Bit-by-bit form example

```text
One bit column:

bits = [1, 1, 1, 0]

ones  = 3
zeros = 1
total pairs = C(4,2) = 6

AND:
only (1,1) produces 1
active pairs = C(3,2) = 3

OR:
only (0,0) produces 0
active pairs = 6 - C(1,2)
             = 6

If b = 2, value = 2^2 = 4

AND contribution = 3 × 4 = 12
OR  contribution = 6 × 4 = 24
```

**Pattern:** count qualifying pairs in each bit column.


<a id="form-17-problem-1"></a>

## Problem 1 — [1514B — AND 0, Sum Big](https://codeforces.com/problemset/problem/1514/B)

### What the problem wants — simple words

Count arrays satisfying the problem’s AND/sum conditions.

### Real-world mapping

Handle one bit-column at a time. For the total AND to be 0, each column must contain at least one 0; independent column choices multiply.

### How this form applies

For every bit position, not all n numbers may have that bit=1, otherwise total AND is nonzero. There are `2^n-1` valid column assignments independently for each of k bits.

### How the solution works — step by step

1. Identify the bit property used by this form: For every bit position, not all n numbers may have that bit=1, otherwise total AND is nonzero.
2. There are `2^n-1` valid column assignments independently for each of k bits.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ n=3, one bit column has 2^3=8 assignments:
├─ 000 001 010 011 100 101 110 111
├─ Only 111 makes AND bit=1.
├─ So 7 valid assignments per bit.
├─ For k independent bits -> 7^k.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

const long long MOD=1e9+7;

long long pw(long long a,long long e){
    long long r=1;
    while (e){
        if (e&1)r=r*a%MOD;
        a=a*a%MOD;
        e>>=1;
    }
    return r;
}

int main(){
    int T;
    cin>>T;
    while (T--){
        long long n,k;
        cin>>n>>k;
        cout<<pw((pw(2,n)-1+MOD)%MOD,k)<<'\n';
    }
}
```


---

<a id="form-17-problem-2"></a>

## Problem 2 — [1669H — Maximal AND](https://codeforces.com/problemset/problem/1669/H)

### What the problem wants — simple words

Use at most k allowed bit operations to maximize the AND of all array elements.

### Real-world mapping

A final AND bit is like a team requirement: that badge appears in the team result only if every member has it. Spend your budget giving missing badges, starting with the most valuable high bit.

### How this form applies

To put bit b into the final AND, every element must have bit b=1. Cost is the number of elements missing that bit. Spend budget greedily from bit 30 down to 0.

### How the solution works — step by step

1. Identify the bit property used by this form: To put bit b into the final AND, every element must have bit b=1.
2. Cost is the number of elements missing that bit.
3. Spend budget greedily from bit 30 down to 0.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[2,1,1], k=2
├─ 2=10
├─ 1=01
├─ 1=01
│
▼

STEP 2
│
├─ bit1 column: 1,0,0 -> need 2 operations
├─ k=2 -> buy bit1
├─ array can become 10,11,11
├─ AND =10 =2
├─ No budget remains.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        long long k;
        cin>>n>>k;
        vector<int>a(n);
        for (int&x:a)cin>>x;
        long long ans=0;
        for (int b=30;b>=0;b--){
            long long need=0;
            for (int x:a)if (!(x&(1<<b)))need++;
            if (need<=k){
                k-=need;
                ans|=1LL<<b;
            }
        }
        cout<<ans<<'\n';
    }
}
```


---

<a id="form-17-problem-3"></a>

## Problem 3 — [1903B — StORage room](https://codeforces.com/problemset/problem/1903/B)

### What the problem wants — simple words

Reconstruct numbers whose pairwise OR values equal the given matrix, or report that the matrix is impossible.

### Real-world mapping

Each matrix entry says which switches must be ON when two devices are combined. Infer safe switches for each device, then replay every pair to verify the specification.

### How this form applies

Need `a[i] | a[j] = M[i][j]`. For each i, AND all M[i][j] over j!=i to build the bits that can safely remain in a[i], then verify every pair.

### How the solution works — step by step

1. Identify the bit property used by this form: Need `a[i] | a[j] = M[i][j]`.
2. For each i, AND all M[i][j] over j!=i to build the bits that can safely remain in a[i], then verify every pair.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ One bit only, three nodes:
├─ M01=1, M02=0, M12=1
├─ M02=0 forces a0=0 and a2=0 at this bit.
├─ Then M01=1 forces a1=1.
├─ M12=1 is satisfied by a1=1,a2=0.
├─ Candidate bit column: [0,1,0].
│
▼

STEP 2
│
├─ Construction followed by full verification catches inconsistent matrices.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<vector<int>>m(n,vector<int>(n));
        for (auto&r:m)for (int&x:r)cin>>x;
        vector<int>a(n,(1<<30)-1);
        for (int i=0;i<n;i++)for (int j=0;j<n;j++)if (i!=j)a[i]&=m[i][j];
        bool ok=true;
        for (int i=0;i<n;i++)for (int j=0;j<n;j++)if (i!=j && (a[i]|a[j])!=m[i][j])ok=false;
        if (!ok)cout<<"NO\n";
        else{
            cout<<"YES\n";
            for (int x:a)cout<<x<<' ';
            cout<<'\n';
        }
    }
}
```


---

<a id="form-17-problem-4"></a>

## Problem 4 — [1991B — AND Reconstruction](https://codeforces.com/problemset/problem/1991/B)

### What the problem wants — simple words

Construct an array whose adjacent AND values equal the given array b, or report that no such array exists.

### Real-world mapping

Each b[i] is a contract between neighboring positions: every 1-bit in the contract must exist on both sides. Merge neighboring contracts with OR, then verify them with AND.

### How this form applies

Given b[i]=a[i]&a[i+1], construct a. A standard candidate uses OR of adjacent constraints: internal `a[i]=b[i-1]|b[i]`; endpoints use their only constraint. Then verify.

### How the solution works — step by step

1. Identify the bit property used by this form: Given b[i]=a[i]&a[i+1], construct a.
2. A standard candidate uses OR of adjacent constraints: internal `a[i]=b[i-1]|b[i]`; endpoints use their only constraint.
3. Then verify.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ b=[2,0]
├─ 2=10, 0=00
├─ Construct:
├─ a0=b0=10
├─ a1=b0|b1=10|00=10
├─ a2=b1=00
├─ Check:
├─ a0&a1=10&10=10 = b0
├─ a1&a2=10&00=00 = b1
├─ works.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<int>b(n-1);
        for (int&x:b)cin>>x;
        vector<int>a(n);
        if (n==1){
            cout<<0<<'\n';
            continue;
        }
        a[0]=b[0];
        a[n-1]=b[n-2];
        for (int i=1;i<n-1;i++)a[i]=b[i-1]|b[i];
        bool ok=true;
        for (int i=0;i<n-1;i++)if ((a[i]&a[i+1])!=b[i])ok=false;
        if (!ok)cout<<-1<<'\n';
        else{
            for (int x:a)cout<<x<<' ';
            cout<<'\n';
        }
    }
}
```

---

<a id="form-18"></a>

# Form 18 — Conservation / Operation Decoding

## Form explained visually

**What this form means:** When many operation choices look exponential, search for a property that all operations preserve or change identically.

**Daily-life mapping — different roads, same destination color**
```text
Operation A ─┐
             ├──► invariant stays SAME
Operation B ─┘

Example parity:
(d + a) bit0 = d_bit0 XOR a_bit0
(d ^ a) bit0 = d_bit0 XOR a_bit0

Different operations
        ↓
same parity behavior
        ↓
track parity only
```

**Recognition signal**
```text
choose operation A or B repeatedly
2^n possibilities
only YES/NO or small property asked
        ↓
Ask: what cannot distinguish the operations?
```

**Core habit:** test parity, XOR-total, sum parity, bit counts, gcd, or another invariant before simulating states.

### Bit-by-bit form example

```text
Allowed operation:

d = d + a
or
d = d ^ a

Example:
d = 5 = 101
a = 3 = 011

Addition:

d       = 0101
a       = 0011
          +
          ────
result  = 1000
LSB       0

XOR:

d       = 0101
a       = 0011
          ^
          ────
result  = 0110
LSB       0

Different full values,
same parity.
```

**Pattern:** when operations branch, search for a property conserved by every branch.


<a id="form-18-problem-1"></a>

## Problem 1 — [1805A — We Need the Zero](https://codeforces.com/problemset/problem/1805/A)

### What the problem wants — simple words

Choose x so that XORing every array element with x makes the XOR of the whole transformed array equal to 0, or report that it is impossible.

### Real-world mapping

Imagine every number wears the same XOR mask x. If the group size is even, identical masks cancel in pairs; if it is odd, one copy of the mask remains.

### How this form applies

Let S be XOR of the array. After XORing every element with x, total becomes `S ^ x` if n is odd and `S` if n is even.

### How the solution works — step by step

1. Identify the bit property used by this form: Let S be XOR of the array.
2. After XORing every element with x, total becomes `S ^ x` if n is odd and `S` if n is even.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[1,2,5]
├─ 1=001
├─ 2=010
├─ 5=101
├─ S=110=6
├─ n=3 odd -> choose x=S=110
│
▼

STEP 2
│
├─ 001^110=111
├─ 010^110=100
├─ 101^110=011
├─ 111^100^011=000.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          ^
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        int xr=0,x;
        for (int i=0;i<n;i++){
            cin>>x;
            xr^=x;
        }
        if (n&1) cout<<xr<<'\n';
        else cout<<(xr==0?0:-1)<<'\n';
    }
}
```


---

<a id="form-18-problem-2"></a>

## Problem 2 — [1516B — AGAGA XOOORRR](https://codeforces.com/problemset/problem/1516/B)

### What the problem wants — simple words

Decide whether the array can be split according to the problem so the required segment XOR values match.

### Real-world mapping

Think of prefix XOR as a running checksum. Equal segment checksums mean the boundaries can be placed where the running XOR reaches the needed states.

### How this form applies

Partition into at least two segments with equal XOR. If total XOR is 0, two parts can work. Otherwise, scan for two prefixes/segments whose XOR equals total XOR, giving three equal-XOR parts.

### How the solution works — step by step

1. Identify the bit property used by this form: Partition into at least two segments with equal XOR.
2. If total XOR is 0, two parts can work.
3. Otherwise, scan for two prefixes/segments whose XOR equals total XOR, giving three equal-XOR parts.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[1,2,3]
├─ 1=01,2=10,3=11
├─ total=01^10^11=00 -> YES immediately.
│
▼

STEP 2
│
├─ If total S!=0, look for:
├─ segment1 XOR=S
├─ segment2 XOR=S
├─ then remaining XOR = S^S^S = S,
├─ so three segments have equal XOR.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          ^
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<int>a(n);
        int S=0;
        for (int&x:a){
            cin>>x;
            S^=x;
        }
        if (S==0){
            cout<<"YES\n";
            continue;
        }
        int cur=0,cnt=0;
        for (int i=0;i<n-1;i++){
            cur^=a[i];
            if (cur==S){
                cnt++;
                cur=0;
            }
        }
        cout<<(cnt>=2?"YES":"NO")<<'\n';
    }
}
```


---

<a id="form-18-problem-3"></a>

## Problem 3 — [1698A — XOR Mixup](https://codeforces.com/problemset/problem/1698/A)

### What the problem wants — simple words

Recover the required value using the XOR relation among all given numbers.

### Real-world mapping

XOR is a cancellation checksum: equal contributions appearing twice disappear, leaving the value that must be recovered.

### How this form applies

The appended value is XOR of all original values. XORing the final array gives zero, so any element can be reconstructed as XOR of all the others; outputting XOR of all final values is also a valid answer under the construction.

### How the solution works — step by step

1. Identify the bit property used by this form: The appended value is XOR of all original values.
2. XORing the final array gives zero, so any element can be reconstructed as XOR of all the others; outputting XOR of all final values is also a valid answer under the construction.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ Example final array: [4,3,2,5]
├─ 4=100
├─ 3=011
├─ 2=010
├─ 5=101
├─ XOR:
├─ 100 ^ 011 = 111
├─ 111 ^ 010 = 101
├─ 101 ^ 101 = 000
│
▼

STEP 2
│
├─ The full XOR is 0 because original-XOR x appears once in addition to the originals whose XOR is x. A valid x can be obtained using the problem's guaranteed construction; the standard implementation XORs the required subset relation.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          ^
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<int>a(n);
        for (int&x:a)cin>>x;
        // Any a[i] can serve as x when XOR of all final elements is 0.
        cout<<a[0]<<'\n';
    }
}
```


---

<a id="form-18-problem-4"></a>

## Problem 4 — [1547D — Co-growing Sequence](https://codeforces.com/problemset/problem/1547/D)

### What the problem wants — simple words

Construct the required companion sequence so consecutive transformed values satisfy the problem’s bitwise growing condition.

### Real-world mapping

Treat every 1-bit already required by the previous value as a feature the next value must keep. Add only the missing features.

### How this form applies

Choose y[i] so `(x[i] xor y[i]) & (x[i+1] xor y[i+1]) = x[i] xor y[i]`. This means every 1-bit in previous transformed value must also be 1 in current transformed value. Add exactly the missing bits.

### How the solution works — step by step

1. Identify the bit property used by this form: Choose y[i] so `(x[i] xor y[i]) & (x[i+1] xor y[i+1]) = x[i] xor y[i]`.
2. This means every 1-bit in previous transformed value must also be 1 in current transformed value.
3. Add exactly the missing bits.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ previous transformed p=1011
├─ current x=0010
├─ Need current transformed z to contain all bits of p.
├─ Missing bits = p & ~x
├─ 1011
├─ ~0010 (within width) -> ...1101
├─ AND ->1001
├─ Choose y=1001
├─ x^y=0010^1001=1011
├─ Now p & z =1011 = p.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          ^
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<int>x(n),y(n);
        for (int&i:x)cin>>i;
        int prev=x[0];
        for (int i=1;i<n;i++){
            y[i]=prev & (~x[i]);
            prev=x[i]^y[i];
        }
        for (int v:y)cout<<v<<' ';
        cout<<'\n';
    }
}
```

---

<a id="form-19"></a>

# Form 19 — Highest Bit -> Lowest Bit Greedy

## Form explained visually

**What this form means:** When higher bits are worth more than all lower bits combined, make decisions from MSB to LSB.

**Daily-life mapping — choose banknotes first**
```text
bit value:
2^5 = 32   ← decide first
2^4 = 16
2^3 =  8
2^2 =  4
2^1 =  2
2^0 =  1

A high-bit decision dominates lower-bit gains.
```

**Recognition signal**
```text
maximize/minimize integer
budget to set bits
lexicographically maximize binary answer
        ↓
Try bits high → low
```

**Core method:** tentatively take a high bit if constraints/budget still permit a valid solution.

### Bit-by-bit form example

```text
Suppose only one bit can be chosen.

High bit:
1000 = 8

ALL lower bits together:
0111 = 7

Therefore:

1000
>
0111

Greedy decision:

bit3 (8) → try first
bit2 (4)
bit1 (2)
bit0 (1)

A successful higher bit dominates every possible lower-bit combination.
```

**Pattern:** maximizing a binary number usually means deciding from MSB to LSB.


<a id="form-19-problem-1"></a>

## Problem 1 — [1669H — Maximal AND](https://codeforces.com/problemset/problem/1669/H)

### What the problem wants — simple words

Use at most k allowed bit operations to maximize the AND of all array elements.

### Real-world mapping

A final AND bit is like a team requirement: that badge appears in the team result only if every member has it. Spend your budget giving missing badges, starting with the most valuable high bit.

### How this form applies

To put bit b into the final AND, every element must have bit b=1. Cost is the number of elements missing that bit. Spend budget greedily from bit 30 down to 0.

### How the solution works — step by step

1. Identify the bit property used by this form: To put bit b into the final AND, every element must have bit b=1.
2. Cost is the number of elements missing that bit.
3. Spend budget greedily from bit 30 down to 0.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[2,1,1], k=2
├─ 2=10
├─ 1=01
├─ 1=01
│
▼

STEP 2
│
├─ bit1 column: 1,0,0 -> need 2 operations
├─ k=2 -> buy bit1
├─ array can become 10,11,11
├─ AND =10 =2
├─ No budget remains.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        long long k;
        cin>>n>>k;
        vector<int>a(n);
        for (int&x:a)cin>>x;
        long long ans=0;
        for (int b=30;b>=0;b--){
            long long need=0;
            for (int x:a)if (!(x&(1<<b)))need++;
            if (need<=k){
                k-=need;
                ans|=1LL<<b;
            }
        }
        cout<<ans<<'\n';
    }
}
```


---

<a id="form-19-problem-2"></a>

## Problem 2 — [1918C — XOR-distance](https://codeforces.com/problemset/problem/1918/C)

### What the problem wants — simple words

Choose x with 0<=x<=r to minimize |(a XOR x) - (b XOR x)|.

### Real-world mapping

You can flip selected switches in both numbers with the same mask. The highest differing switch dominates the numerical gap, so decide important bits before small ones.

### How this form applies

Only differing bits of a and b matter. Keep the highest differing bit unchanged (it determines which value is larger), then greedily flip lower differing bits when the corresponding x-bit fits within r and reduces the gap.

### How the solution works — step by step

1. Identify the bit property used by this form: Only differing bits of a and b matter.
2. Keep the highest differing bit unchanged (it determines which value is larger), then greedily flip lower differing bits when the corresponding x-bit fits within r and reduces the gap.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=10=1010, b=3=0011. a>b.
├─ Highest differing bit is bit3:
├─ a:1, b:0. Do NOT flip it, otherwise ordering reverses with a huge change.
├─ Lower differing bits:
├─ bit0: a=0,b=1. Setting x bit0 swaps them and can reduce gap.
│
▼

STEP 2
│
├─ x starts 0000. If 0001<=r, choose it:
├─ a^x=1011=11
├─ b^x=0010=2
├─ Here it increased gap, so direction of lower bits matters: flip only bits where the larger number has 1 and smaller has 0, subject to r. Process from high to low.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        long long a,b,r;
        cin>>a>>b>>r;
        if (a<b)swap(a,b);
        long long x=0;
        bool first=true;
        for (int i=62;i>=0;i--){
            long long bit=1LL<<i;
            bool A=a&bit,B=b&bit;
            if (A!=B){
                if (first){
                    first=false;
                    continue;
                }
                if (A&&!B && x+bit<=r)x+=bit;
            }
        }
        cout<<llabs((a^x)-(b^x))<<'\n';
    }
}
```


---

<a id="form-19-problem-3"></a>

## Problem 3 — [1527A — And Then There Were K](https://codeforces.com/problemset/problem/1527/A)

### What the problem wants — simple words

Find the largest k<n such that n & (n-1) & ... & k becomes 0.

### Real-world mapping

Think of walking downward from n until every binary switch has been turned OFF at least once. The highest power-of-two boundary tells you where that happens.

### How this form applies

For maximum `k<n` with `n & (n-1) & ... & k = 0`, find the highest power of two not exceeding `n`; answer is that power minus one.

### How the solution works — step by step

1. Identify the bit property used by this form: For maximum `k<n` with `n & (n-1) & ...
2. & k = 0`, find the highest power of two not exceeding `n`; answer is that power minus one.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ n = 10 = 1010
├─ highest power of 2 <= n = 8 = 1000
├─ answer = 8-1 = 7 = 0111
│
▼

STEP 2
│
├─ The interval 10,9,8,7 contains values that clear every bit in the cumulative AND.
├─ The boundary is determined by the MSB of n.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        long long n;
        cin>>n;
        long long p=1;
        while ((p<<1)<=n)p<<=1;
        cout<<p-1<<'\n';
    }
}
```


---

<a id="form-19-problem-4"></a>

## Problem 4 — [1420B — Rock and Lever](https://codeforces.com/problemset/problem/1420/B)

### What the problem wants — simple words

Count pairs satisfying the problem’s bitwise inequality; valid pairs are characterized by having the same highest set bit.

### Real-world mapping

Put numbers on shelves by their highest 1-bit. Only two numbers from the same shelf can form a valid pair.

### How this form applies

A valid pair is characterized by the same highest set bit. Group numbers into MSB buckets and count pairs inside each bucket.

### How the solution works — step by step

1. Identify the bit property used by this form: A valid pair is characterized by the same highest set bit.
2. Group numbers into MSB buckets and count pairs inside each bucket.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[4,5,6,9]
├─ 4=0100 -> MSB2
├─ 5=0101 -> MSB2
├─ 6=0110 -> MSB2
├─ 9=1001 -> MSB3
│
▼

STEP 2
│
├─ MSB2 bucket size=3 -> C(3,2)=3
├─ MSB3 bucket size=1 -> 0
├─ answer=3.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        long long c[32]={
        }
        ,ans=0;
        while (n--){
            int x;
            cin>>x;
            int b=31-__builtin_clz(x);
            c[b]++;
        }
        for (long long v:c)ans+=v*(v-1)/2;
        cout<<ans<<'\n';
    }
}
```

---

<a id="form-20"></a>

# Form 20 — Prefix Counts of Bits

## Form explained visually

**What this form means:** Build a prefix count for every bit so a range can instantly tell how many values have that bit set.

**Daily-life mapping — attendance register per skill**
```text
          bit2 bit1 bit0
prefix 0    0    0    0
prefix 1    1    0    1
prefix 2    1    1    2
prefix 3    2    2    2

count bit b in [L,R]
= pref[R+1][b] - pref[L][b]
```

**Recognition signal**
```text
many range queries
need count of set bits in [L,R]
range AND/OR/XOR-related statistics
        ↓
Prefix count each bit
```

**Core complexity:** preprocessing `O(N·B)`, each query `O(B)`.

### Bit-by-bit form example

```text
a = [5,2,7]

5 = 101
2 = 010
7 = 111

Prefix count for bit0:

index        0  1  2
value        5  2  7
bit0         1  0  1
prefix    0  1  1  2

How many bit0=1 in range [1..2]?

pref[3] - pref[1]
= 2 - 1
= 1
```

**Pattern:** per-bit prefix counts answer range bit-frequency queries by subtraction.


<a id="form-20-problem-1"></a>

## Problem 1 — [1872E — Data Structures Fan](https://codeforces.com/problemset/problem/1872/E)

### What the problem wants — simple words

Maintain XOR information for two groups while range queries flip which group selected elements belong to.

### Real-world mapping

Imagine two XOR buckets labelled 0 and 1. Flipping a range moves its combined XOR effect between the buckets; prefix XOR gives that range effect instantly.

### How this form applies

Maintain XOR of the two groups defined by a binary string. Flipping a whole substring swaps membership there; prefix XOR gives XOR of the flipped values in O(1), and that value toggles both group XORs.

### How the solution works — step by step

1. Identify the bit property used by this form: Maintain XOR of the two groups defined by a binary string.
2. Flipping a whole substring swaps membership there; prefix XOR gives XOR of the flipped values in O(1), and that value toggles both group XORs.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[1,2,3], s=010
├─ binary: 1=01,2=10,3=11
├─ Group0 XOR = 1^3 = 01^11 = 10 =2
├─ Group1 XOR = 2 =10
│
▼

STEP 2
│
├─ Flip [1,2] (0-based values 2,3): segment XOR=2^3=01
├─ Both group XORs ^=01:
├─ g0:10^01=11=3
├─ g1:10^01=11=3
├─ Membership swapped only inside range.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<long long>a(n),p(n+1);
        for (int i=0;i<n;i++){
            cin>>a[i];
            p[i+1]=p[i]^a[i];
        }
        string s;
        cin>>s;
        long long g[2]={
            0,0
        }
        ;
        for (int i=0;i<n;i++)g[s[i]-'0']^=a[i];
        int q;
        cin>>q;
        while (q--){
            int tp;
            cin>>tp;
            if (tp==1){
                int l,r;
                cin>>l>>r;
                long long x=p[r]^p[l-1];
                g[0]^=x;
                g[1]^=x;
            }
            else{
                int b;
                cin>>b;
                cout<<g[b]<<' ';
            }
        }
        cout<<'\n';
    }
}
```


---

<a id="form-20-problem-2"></a>

## Problem 2 — [1625A — Ancient Civilization](https://codeforces.com/problemset/problem/1625/A)

### What the problem wants — simple words

Construct the number required by choosing each bit from the majority of the input numbers.

### Real-world mapping

Treat every bit position as an election: count 0-votes and 1-votes independently, then choose the majority for that column.

### How this form applies

For each bit independently, choose the majority bit to minimize total differing positions.

### How the solution works — step by step

1. Identify the bit property used by this form: For each bit independently, choose the majority bit to minimize total differing positions.
2. Write the relevant values in binary and inspect only the bit positions that affect the condition.
3. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ numbers:
├─ 1011
├─ 1100
├─ 0111
│
▼

STEP 2
│
├─ bit3:1,1,0 -> ones2 -> answer bit=1
├─ bit2:0,1,1 -> ones2 -> 1
├─ bit1:1,0,1 -> ones2 -> 1
├─ bit0:1,0,1 -> ones2 -> 1
├─ answer=1111.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n,l;
        cin>>n>>l;
        vector<int>a(n);
        for (int&x:a)cin>>x;
        int ans=0;
        for (int b=0;b<l;b++){
            int one=0;
            for (int x:a)one+=(x>>b)&1;
            if (one>n-one)ans|=1<<b;
        }
        cout<<ans<<'\n';
    }
}
```


---

<a id="form-20-problem-3"></a>

## Problem 3 — [1669H — Maximal AND](https://codeforces.com/problemset/problem/1669/H)

### What the problem wants — simple words

Use at most k allowed bit operations to maximize the AND of all array elements.

### Real-world mapping

A final AND bit is like a team requirement: that badge appears in the team result only if every member has it. Spend your budget giving missing badges, starting with the most valuable high bit.

### How this form applies

To put bit b into the final AND, every element must have bit b=1. Cost is the number of elements missing that bit. Spend budget greedily from bit 30 down to 0.

### How the solution works — step by step

1. Identify the bit property used by this form: To put bit b into the final AND, every element must have bit b=1.
2. Cost is the number of elements missing that bit.
3. Spend budget greedily from bit 30 down to 0.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[2,1,1], k=2
├─ 2=10
├─ 1=01
├─ 1=01
│
▼

STEP 2
│
├─ bit1 column: 1,0,0 -> need 2 operations
├─ k=2 -> buy bit1
├─ array can become 10,11,11
├─ AND =10 =2
├─ No budget remains.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        long long k;
        cin>>n>>k;
        vector<int>a(n);
        for (int&x:a)cin>>x;
        long long ans=0;
        for (int b=30;b>=0;b--){
            long long need=0;
            for (int x:a)if (!(x&(1<<b)))need++;
            if (need<=k){
                k-=need;
                ans|=1LL<<b;
            }
        }
        cout<<ans<<'\n';
    }
}
```


---

<a id="form-20-problem-4"></a>

## Problem 4 — [1516B — AGAGA XOOORRR](https://codeforces.com/problemset/problem/1516/B)

### What the problem wants — simple words

Decide whether the array can be split according to the problem so the required segment XOR values match.

### Real-world mapping

Think of prefix XOR as a running checksum. Equal segment checksums mean the boundaries can be placed where the running XOR reaches the needed states.

### How this form applies

Partition into at least two segments with equal XOR. If total XOR is 0, two parts can work. Otherwise, scan for two prefixes/segments whose XOR equals total XOR, giving three equal-XOR parts.

### How the solution works — step by step

1. Identify the bit property used by this form: Partition into at least two segments with equal XOR.
2. If total XOR is 0, two parts can work.
3. Otherwise, scan for two prefixes/segments whose XOR equals total XOR, giving three equal-XOR parts.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[1,2,3]
├─ 1=01,2=10,3=11
├─ total=01^10^11=00 -> YES immediately.
│
▼

STEP 2
│
├─ If total S!=0, look for:
├─ segment1 XOR=S
├─ segment2 XOR=S
├─ then remaining XOR = S^S^S = S,
├─ so three segments have equal XOR.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<int>a(n);
        int S=0;
        for (int&x:a){
            cin>>x;
            S^=x;
        }
        if (S==0){
            cout<<"YES\n";
            continue;
        }
        int cur=0,cnt=0;
        for (int i=0;i<n-1;i++){
            cur^=a[i];
            if (cur==S){
                cnt++;
                cur=0;
            }
        }
        cout<<(cnt>=2?"YES":"NO")<<'\n';
    }
}
```

---

<a id="form-21"></a>

# Form 21 — Common Binary Prefix / Range AND

## Form explained visually

**What this form means:** AND of all integers in a range keeps only the leading bits that never change across the range.

**Daily-life mapping — common address prefix**
```text
12 = 1100
13 = 1101
14 = 1110
15 = 1111
     ^^
common stable prefix = 11
changing suffix bits eventually AND to 0
```

**Recognition signal**
```text
AND of every number L..R
common leading bits
range crosses power-of-two boundary
        ↓
Find common binary prefix of L and R
```

**Core method:** right-shift `L` and `R` until equal; shift the common prefix back.

### Bit-by-bit form example

```text
Range AND of [12..15]

12 = 1100
13 = 1101
14 = 1110
15 = 1111
     ^^
     common prefix

AND:

1100
1101
1110
1111
----
1100

Endpoint shifting:

12 = 1100    15 = 1111
     >>1          >>1
 6 = 110      7 = 111
     >>1          >>1
 3 = 11       3 = 11  ← same

11 << 2 = 1100
```

**Pattern:** range AND preserves only the common binary prefix.


<a id="form-21-problem-1"></a>

## Problem 1 — [1527A — And Then There Were K](https://codeforces.com/problemset/problem/1527/A)

### What the problem wants — simple words

Find the largest k<n such that n & (n-1) & ... & k becomes 0.

### Real-world mapping

Think of walking downward from n until every binary switch has been turned OFF at least once. The highest power-of-two boundary tells you where that happens.

### How this form applies

For maximum `k<n` with `n & (n-1) & ... & k = 0`, find the highest power of two not exceeding `n`; answer is that power minus one.

### How the solution works — step by step

1. Identify the bit property used by this form: For maximum `k<n` with `n & (n-1) & ...
2. & k = 0`, find the highest power of two not exceeding `n`; answer is that power minus one.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ n = 10 = 1010
├─ highest power of 2 <= n = 8 = 1000
├─ answer = 8-1 = 7 = 0111
│
▼

STEP 2
│
├─ The interval 10,9,8,7 contains values that clear every bit in the cumulative AND.
├─ The boundary is determined by the MSB of n.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        long long n;
        cin>>n;
        long long p=1;
        while ((p<<1)<=n)p<<=1;
        cout<<p-1<<'\n';
    }
}
```


---

<a id="form-21-problem-2"></a>

## Problem 2 — [1420B — Rock and Lever](https://codeforces.com/problemset/problem/1420/B)

### What the problem wants — simple words

Count pairs satisfying the problem’s bitwise inequality; valid pairs are characterized by having the same highest set bit.

### Real-world mapping

Put numbers on shelves by their highest 1-bit. Only two numbers from the same shelf can form a valid pair.

### How this form applies

A valid pair is characterized by the same highest set bit. Group numbers into MSB buckets and count pairs inside each bucket.

### How the solution works — step by step

1. Identify the bit property used by this form: A valid pair is characterized by the same highest set bit.
2. Group numbers into MSB buckets and count pairs inside each bucket.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[4,5,6,9]
├─ 4=0100 -> MSB2
├─ 5=0101 -> MSB2
├─ 6=0110 -> MSB2
├─ 9=1001 -> MSB3
│
▼

STEP 2
│
├─ MSB2 bucket size=3 -> C(3,2)=3
├─ MSB3 bucket size=1 -> 0
├─ answer=3.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        long long c[32]={
        }
        ,ans=0;
        while (n--){
            int x;
            cin>>x;
            int b=31-__builtin_clz(x);
            c[b]++;
        }
        for (long long v:c)ans+=v*(v-1)/2;
        cout<<ans<<'\n';
    }
}
```


---

<a id="form-21-problem-3"></a>

## Problem 3 — [1669H — Maximal AND](https://codeforces.com/problemset/problem/1669/H)

### What the problem wants — simple words

Use at most k allowed bit operations to maximize the AND of all array elements.

### Real-world mapping

A final AND bit is like a team requirement: that badge appears in the team result only if every member has it. Spend your budget giving missing badges, starting with the most valuable high bit.

### How this form applies

To put bit b into the final AND, every element must have bit b=1. Cost is the number of elements missing that bit. Spend budget greedily from bit 30 down to 0.

### How the solution works — step by step

1. Identify the bit property used by this form: To put bit b into the final AND, every element must have bit b=1.
2. Cost is the number of elements missing that bit.
3. Spend budget greedily from bit 30 down to 0.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=[2,1,1], k=2
├─ 2=10
├─ 1=01
├─ 1=01
│
▼

STEP 2
│
├─ bit1 column: 1,0,0 -> need 2 operations
├─ k=2 -> buy bit1
├─ array can become 10,11,11
├─ AND =10 =2
├─ No budget remains.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        long long k;
        cin>>n>>k;
        vector<int>a(n);
        for (int&x:a)cin>>x;
        long long ans=0;
        for (int b=30;b>=0;b--){
            long long need=0;
            for (int x:a)if (!(x&(1<<b)))need++;
            if (need<=k){
                k-=need;
                ans|=1LL<<b;
            }
        }
        cout<<ans<<'\n';
    }
}
```


---

<a id="form-21-problem-4"></a>

## Problem 4 — [1475A — Odd Divisor](https://codeforces.com/problemset/problem/1475/A)

### What the problem wants — simple words

Decide whether n has an odd divisor greater than 1.

### Real-world mapping

Imagine repeatedly cutting a number in half. If you eventually reach an odd number greater than 1, that odd number is an odd divisor. Pure powers of two only reach 1.

### How this form applies

A number has no odd divisor greater than 1 exactly when it is a power of two. Use `n & (n-1)` to test that form.

### How the solution works — step by step

1. Identify the bit property used by this form: A number has no odd divisor greater than 1 exactly when it is a power of two.
2. Use `n & (n-1)` to test that form.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ n = 8
├─ 8   = 1000
├─ 7   = 0111
├─ &     0000 -> power of two -> NO
│
▼

STEP 2
│
├─ n = 12
├─ 12  = 1100
├─ 11  = 1011
├─ &     1000 -> more than one set bit -> has odd factor 3 -> YES.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        long long n;
        cin>>n;
        cout<<((n&(n-1))?"YES":"NO")<<'\n';
    }
}
```

---

<a id="form-22"></a>

# Form 22 — Complement Within Fixed Width

## Form explained visually

**What this form means:** Flip every bit only inside a chosen width; do not accidentally flip infinitely many leading zeros.

**Daily-life mapping — invert switches inside one control panel**
```text
width = 5
x     = 10110
mask  = 11111
x^mask= 01001

Only these 5 switches are inverted.
```

**Recognition signal**
```text
opposite bits
binary complement partner
flip lowest k/31 bits
        ↓
Build all-ones width mask
```

**Core tools:** `mask=(1LL<<k)-1`, `partner=x^mask`.

### Bit-by-bit form example

```text
Use exactly 5 bits.

x       = 10110
mask    = 11111
          ^
          ─────
comp    = 01001

Every bit flips:

1 → 0
0 → 1
1 → 0
1 → 0
0 → 1

Fixed-width mask:
(1 << 5) - 1 = 11111
```

**Pattern:** XOR with an all-ones mask complements only the chosen width.


<a id="form-22-problem-1"></a>

## Problem 1 — [1926D — Vlad and Division](https://codeforces.com/problemset/problem/1926/D)

### What the problem wants — simple words

Group numbers so paired values have opposite bits in the required fixed bit range, minimizing the number of groups.

### Real-world mapping

Think of two puzzle pieces: every 0 needs a 1 opposite it and every 1 needs a 0. The matching piece is the fixed-width binary complement.

### How this form applies

Two values can pair when their lowest 31 bits are opposite. Partner is `x xor ((1<<31)-1)`. Greedily match complements.

### How the solution works — step by step

1. Identify the bit property used by this form: Two values can pair when their lowest 31 bits are opposite.
2. Partner is `x xor ((1<<31)-1)`.
3. Greedily match complements.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ 5-bit illustration:
├─ x=10110
├─ mask=11111
├─ partner=01001
│
▼

STEP 2
│
├─ 10110
├─ 01001
├─ -----
├─ every column is 1/0 or 0/1.
├─ If partner already waits, pair them; otherwise start a new group with x.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          ^
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    const long long M=(1LL<<31)-1;
    while (T--){
        int n;
        cin>>n;
        unordered_map<long long,int>cnt;
        int ans=0;
        while (n--){
            long long x;
            cin>>x,y=x^M;
            if (cnt[y])cnt[y]--;
            else{
                cnt[x]++;
                ans++;
            }
        }
        cout<<ans<<'\n';
    }
}
```


---

<a id="form-22-problem-2"></a>

## Problem 2 — [1421A — XORwice](https://codeforces.com/problemset/problem/1421/A)

### What the problem wants — simple words

Compute the minimum value of (a XOR x) + (b XOR x) over x.

### Real-world mapping

At each bit, you choose whether the common mask flips both switches. Bits where a and b disagree always contribute one 1; equal 1-bits can be switched off.

### How this form applies

Minimize `(a xor x) + (b xor x)`. At every bit where both `a` and `b` are 1, both terms cannot simultaneously lose that contribution; where they differ, choose x so one side has the bit. The minimum equals `a xor b`.

### How the solution works — step by step

1. Identify the bit property used by this form: Minimize `(a xor x) + (b xor x)`.
2. At every bit where both `a` and `b` are 1, both terms cannot simultaneously lose that contribution; where they differ, choose x so one side has the bit.
3. The minimum equals `a xor b`.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=5=101, b=3=011
│
▼

STEP 2
│
├─ a^b = 110 = 6
│
▼

STEP 3
│
├─ Try x = a&b = 001:
├─ a^x = 100 = 4
├─ b^x = 010 = 2
├─ sum = 6
│
▼

STEP 4
│
├─ Bit columns:
├─ bit2: 1/0 -> contribution 4
├─ bit1: 0/1 -> contribution 2
├─ bit0: 1/1 -> choose x=1 -> both become 0
├─ Total = 6.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          ^
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        long long a,b;
        cin>>a>>b;
        cout<<(a^b)<<'\n';
    }
}
```


---

<a id="form-22-problem-3"></a>

## Problem 3 — [1918C — XOR-distance](https://codeforces.com/problemset/problem/1918/C)

### What the problem wants — simple words

Choose x with 0<=x<=r to minimize |(a XOR x) - (b XOR x)|.

### Real-world mapping

You can flip selected switches in both numbers with the same mask. The highest differing switch dominates the numerical gap, so decide important bits before small ones.

### How this form applies

Only differing bits of a and b matter. Keep the highest differing bit unchanged (it determines which value is larger), then greedily flip lower differing bits when the corresponding x-bit fits within r and reduces the gap.

### How the solution works — step by step

1. Identify the bit property used by this form: Only differing bits of a and b matter.
2. Keep the highest differing bit unchanged (it determines which value is larger), then greedily flip lower differing bits when the corresponding x-bit fits within r and reduces the gap.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ a=10=1010, b=3=0011. a>b.
├─ Highest differing bit is bit3:
├─ a:1, b:0. Do NOT flip it, otherwise ordering reverses with a huge change.
├─ Lower differing bits:
├─ bit0: a=0,b=1. Setting x bit0 swaps them and can reduce gap.
│
▼

STEP 2
│
├─ x starts 0000. If 0001<=r, choose it:
├─ a^x=1011=11
├─ b^x=0010=2
├─ Here it increased gap, so direction of lower bits matters: flip only bits where the larger number has 1 and smaller has 0, subject to r. Process from high to low.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          ^
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        long long a,b,r;
        cin>>a>>b>>r;
        if (a<b)swap(a,b);
        long long x=0;
        bool first=true;
        for (int i=62;i>=0;i--){
            long long bit=1LL<<i;
            bool A=a&bit,B=b&bit;
            if (A!=B){
                if (first){
                    first=false;
                    continue;
                }
                if (A&&!B && x+bit<=r)x+=bit;
            }
        }
        cout<<llabs((a^x)-(b^x))<<'\n';
    }
}
```


---

<a id="form-22-problem-4"></a>

## Problem 4 — [1362B — Johnny and His Hobbies](https://codeforces.com/problemset/problem/1362/B)

### What the problem wants — simple words

Find a positive x such that XORing every array value with x produces exactly the same set of values.

### Real-world mapping

Think of x as a relabelling mask. After relabelling every ID with XOR, the collection of IDs must look unchanged.

### How this form applies

Find positive x such that XORing every set element by x produces the same set. Try x and compare transformed multiset/set.

### How the solution works — step by step

1. Identify the bit property used by this form: Find positive x such that XORing every set element by x produces the same set.
2. Try x and compare transformed multiset/set.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ S={1,2,3,4}
├─ Try x=5 (101):
├─ 1=001 ^101=100=4
├─ 2=010 ^101=111=7 -> 7 not in S -> fail
│
▼

STEP 2
│
├─ For a candidate x, every bit where x has 1 toggles that column for all numbers. The transformed collection must exactly match the original collection.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          ^
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        vector<int>a(n);
        set<int>s;
        for (int&v:a){
            cin>>v;
            s.insert(v);
        }
        int ans=-1;
        for (int x=1;x<1024;x++){
            set<int>t;
            for (int v:a)t.insert(v^x);
            if (t==s){
                ans=x;
                break;
            }
        }
        cout<<ans<<'\n';
    }
}
```

---

<a id="form-23"></a>

# Form 23 — Subset Enumeration

## Form explained visually

**What this form means:** A bitmask represents which items are selected; enumerate every subset when `n` is small.

**Daily-life mapping — packing a travel bag**
```text
items:    A B C D
mask:     1 0 1 1

1 = pack item
0 = leave item

1011 → choose A, C, D
```

**Recognition signal**
```text
n around 20
choose any subset
try all combinations
include/exclude each item
        ↓
for mask = 0 .. (1<<n)-1
```

**Core complexity:** `O(2^n · n)`.

### Bit-by-bit form example

```text
Three items: A, B, C

mask bits:
bit2 bit1 bit0
 C    B    A

000 → {}
001 → {A}
010 → {B}
011 → {A,B}
100 → {C}
101 → {A,C}
110 → {B,C}
111 → {A,B,C}

Example mask = 101:

101
↑ ↑
C A selected
```

**Pattern:** one `n`-bit number represents one subset of `n` items.


<a id="form-23-problem-1"></a>

## Problem 1 — [1097B — Petr and a Combination Lock](https://codeforces.com/problemset/problem/1097/B)

### What the problem wants — simple words

Choose clockwise or counter-clockwise for every angle so the final rotation is divisible by 360.

### Real-world mapping

Each bit of a mask is a direction switch: 1 means +angle and 0 means -angle. Try every switch configuration.

### How this form applies

Each angle has two choices: + or -. Encode the choice for angle i in bit i of a mask and enumerate all 2^n assignments.

### How the solution works — step by step

1. Identify the bit property used by this form: Each angle has two choices: + or -.
2. Encode the choice for angle i in bit i of a mask and enumerate all 2^n assignments.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ n=3, angles=[10,20,30]
├─ mask=101
├─ bit0=1 -> +10
├─ bit1=0 -> -20
├─ bit2=1 -> +30
├─ sum=20, not divisible by360.
├─ Try every mask 000..111 until one gives sum%360=0.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin>>n;
    vector<int>a(n);
    for (int&x:a)cin>>x;
    for (int m=0;m<(1<<n);m++){
        int s=0;
        for (int i=0;i<n;i++)s+=(m>>i&1)?a[i]:-a[i];
        if ((s%360+360)%360==0){
            cout<<"YES\n";
            return 0;
        }
    }
    cout<<"NO\n";
}
```


---

<a id="form-23-problem-2"></a>

## Problem 2 — [550B — Preparing Olympiad](https://codeforces.com/problemset/problem/550/B)

### What the problem wants — simple words

Count subsets of problems satisfying the required size/sum/difficulty constraints.

### Real-world mapping

Each problem is an ON/OFF switch in a subset mask. Turn on a set of problems, then test whether that selected package satisfies all rules.

### How this form applies

Enumerate every subset. A mask chooses problems; test count>=2, total difficulty in [l,r], and max-min>=x.

### How the solution works — step by step

1. Identify the bit property used by this form: Enumerate every subset.
2. A mask chooses problems; test count>=2, total difficulty in [l,r], and max-min>=x.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ d=[800,1000,1300], mask=101
├─ chosen bits: problem0 and problem2
├─ count=2
├─ sum=2100
├─ max-min=1300-800=500
├─ Check all three conditions. Each mask is one candidate team.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    int n,l,r,x;
    cin>>n>>l>>r>>x;
    vector<int>a(n);
    for (int&v:a)cin>>v;
    int ans=0;
    for (int m=0;m<(1<<n);m++){
        int c=0,s=0,mn=1e9,mx=-1;
        for (int i=0;i<n;i++)if (m>>i&1){
            c++;
            s+=a[i];
            mn=min(mn,a[i]);
            mx=max(mx,a[i]);
        }
        if (c>=2&&s>=l&&s<=r&&mx-mn>=x)ans++;
    }
    cout<<ans<<'\n';
}
```


---

<a id="form-23-problem-3"></a>

## Problem 3 — [1042B — Vitamins](https://codeforces.com/problemset/problem/1042/B)

### What the problem wants — simple words

Buy the cheapest set of juices that together provide vitamins A, B, and C.

### Real-world mapping

A, B, C are three checkboxes. Encode the checked boxes as bits; OR combines the vitamins supplied by purchases.

### How this form applies

Encode vitamins A,B,C as bits 0,1,2. Each juice is a mask. OR combines acquired vitamins; DP over 8 masks gives minimum cost.

### How the solution works — step by step

1. Identify the bit property used by this form: Encode vitamins A,B,C as bits 0,1,2.
2. Each juice is a mask.
3. OR combines acquired vitamins; DP over 8 masks gives minimum cost.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ A=001,B=010,C=100
├─ juice AB ->011 cost5
├─ juice C  ->100 cost3
├─ state 000 --buy AB-->011
├─ 011 |100 =111
├─ cost=8, all vitamins covered.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin>>n;
    const int INF=1e9;
    vector<int>dp(8,INF);
    dp[0]=0;
    while (n--){
        int c;
        string s;
        cin>>c>>s;
        int m=0;
        for (char ch:s)m|=1<<(ch-'A');
        auto ndp=dp;
        for (int st=0;st<8;st++)ndp[st|m]=min(ndp[st|m],dp[st]+c);
        dp=ndp;
    }
    cout<<(dp[7]>=INF?-1:dp[7])<<'\n';
}
```


---

<a id="form-23-problem-4"></a>

## Problem 4 — [1829C — Mr. Perfectly Fine](https://codeforces.com/problemset/problem/1829/C)

### What the problem wants — simple words

Find the minimum time/cost to obtain both required skills.

### Real-world mapping

There are only two skill switches. An item can give 01, 10, or 11; combine masks with OR and keep the cheapest way to reach 11.

### How this form applies

Two skills are a 2-bit mask: 01,10,11. Keep cheapest cost for each mask. Answer is min(cost11, cost01+cost10).

### How the solution works — step by step

1. Identify the bit property used by this form: Two skills are a 2-bit mask: 01,10,11.
2. Keep cheapest cost for each mask.
3. Answer is min(cost11, cost01+cost10).
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ book1 skill 10 cost4 -> mask2
├─ book2 skill 01 cost3 -> mask1
├─ book3 skill 11 cost10 -> mask3
├─ Combine mask2|mask1=11 cost7
├─ min(7,10)=7.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        const int INF=1e9;
        int best[4]={
            0,INF,INF,INF
        }
        ;
        while (n--){
            int c;
            string s;
            cin>>c>>s;
            int m=(s[0]-'0')*2+(s[1]-'0');
            best[m]=min(best[m],c);
        }
        int ans=min(best[3],best[1]+best[2]);
        cout<<(ans>=INF?-1:ans)<<'\n';
    }
}
```

---

<a id="form-24"></a>

# Form 24 — Bitmask as State

## Form explained visually

**What this form means:** Several boolean properties are compressed into one integer state.

**Daily-life mapping — three-feature membership card**
```text
A = 001
B = 010
C = 100

Have A+C:
001 | 100 = 101

state 101 means:
A ✓
B ✗
C ✓
```

**Recognition signal**
```text
small number of yes/no features
skills / keys / vitamins / visited categories
combine feature sets
        ↓
Encode state as bits
```

**Core operation:** combine states using OR.

### Bit-by-bit form example

```text
Skills:
A = 001
B = 010
C = 100

Current state has A:

state   = 001

Acquire C:

state   = 001
C       = 100
          |
          ───
new     = 101

Decode:
bit2 = 1 → C ✓
bit1 = 0 → B ✗
bit0 = 1 → A ✓

Acquire B:

101 | 010 = 111
```

**Pattern:** a mask stores many yes/no states in one integer.


<a id="form-24-problem-1"></a>

## Problem 1 — [1042B — Vitamins](https://codeforces.com/problemset/problem/1042/B)

### What the problem wants — simple words

Buy the cheapest set of juices that together provide vitamins A, B, and C.

### Real-world mapping

A, B, C are three checkboxes. Encode the checked boxes as bits; OR combines the vitamins supplied by purchases.

### How this form applies

Encode vitamins A,B,C as bits 0,1,2. Each juice is a mask. OR combines acquired vitamins; DP over 8 masks gives minimum cost.

### How the solution works — step by step

1. Identify the bit property used by this form: Encode vitamins A,B,C as bits 0,1,2.
2. Each juice is a mask.
3. OR combines acquired vitamins; DP over 8 masks gives minimum cost.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ A=001,B=010,C=100
├─ juice AB ->011 cost5
├─ juice C  ->100 cost3
├─ state 000 --buy AB-->011
├─ 011 |100 =111
├─ cost=8, all vitamins covered.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          |
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin>>n;
    const int INF=1e9;
    vector<int>dp(8,INF);
    dp[0]=0;
    while (n--){
        int c;
        string s;
        cin>>c>>s;
        int m=0;
        for (char ch:s)m|=1<<(ch-'A');
        auto ndp=dp;
        for (int st=0;st<8;st++)ndp[st|m]=min(ndp[st|m],dp[st]+c);
        dp=ndp;
    }
    cout<<(dp[7]>=INF?-1:dp[7])<<'\n';
}
```


---

<a id="form-24-problem-2"></a>

## Problem 2 — [1829C — Mr. Perfectly Fine](https://codeforces.com/problemset/problem/1829/C)

### What the problem wants — simple words

Find the minimum time/cost to obtain both required skills.

### Real-world mapping

There are only two skill switches. An item can give 01, 10, or 11; combine masks with OR and keep the cheapest way to reach 11.

### How this form applies

Two skills are a 2-bit mask: 01,10,11. Keep cheapest cost for each mask. Answer is min(cost11, cost01+cost10).

### How the solution works — step by step

1. Identify the bit property used by this form: Two skills are a 2-bit mask: 01,10,11.
2. Keep cheapest cost for each mask.
3. Answer is min(cost11, cost01+cost10).
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ book1 skill 10 cost4 -> mask2
├─ book2 skill 01 cost3 -> mask1
├─ book3 skill 11 cost10 -> mask3
├─ Combine mask2|mask1=11 cost7
├─ min(7,10)=7.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          |
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        const int INF=1e9;
        int best[4]={
            0,INF,INF,INF
        }
        ;
        while (n--){
            int c;
            string s;
            cin>>c>>s;
            int m=(s[0]-'0')*2+(s[1]-'0');
            best[m]=min(best[m],c);
        }
        int ans=min(best[3],best[1]+best[2]);
        cout<<(ans>=INF?-1:ans)<<'\n';
    }
}
```


---

<a id="form-24-problem-3"></a>

## Problem 3 — [1097B — Petr and a Combination Lock](https://codeforces.com/problemset/problem/1097/B)

### What the problem wants — simple words

Choose clockwise or counter-clockwise for every angle so the final rotation is divisible by 360.

### Real-world mapping

Each bit of a mask is a direction switch: 1 means +angle and 0 means -angle. Try every switch configuration.

### How this form applies

Each angle has two choices: + or -. Encode the choice for angle i in bit i of a mask and enumerate all 2^n assignments.

### How the solution works — step by step

1. Identify the bit property used by this form: Each angle has two choices: + or -.
2. Encode the choice for angle i in bit i of a mask and enumerate all 2^n assignments.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ n=3, angles=[10,20,30]
├─ mask=101
├─ bit0=1 -> +10
├─ bit1=0 -> -20
├─ bit2=1 -> +30
├─ sum=20, not divisible by360.
├─ Try every mask 000..111 until one gives sum%360=0.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          |
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin>>n;
    vector<int>a(n);
    for (int&x:a)cin>>x;
    for (int m=0;m<(1<<n);m++){
        int s=0;
        for (int i=0;i<n;i++)s+=(m>>i&1)?a[i]:-a[i];
        if ((s%360+360)%360==0){
            cout<<"YES\n";
            return 0;
        }
    }
    cout<<"NO\n";
}
```


---

<a id="form-24-problem-4"></a>

## Problem 4 — [550B — Preparing Olympiad](https://codeforces.com/problemset/problem/550/B)

### What the problem wants — simple words

Count subsets of problems satisfying the required size/sum/difficulty constraints.

### Real-world mapping

Each problem is an ON/OFF switch in a subset mask. Turn on a set of problems, then test whether that selected package satisfies all rules.

### How this form applies

Enumerate every subset. A mask chooses problems; test count>=2, total difficulty in [l,r], and max-min>=x.

### How the solution works — step by step

1. Identify the bit property used by this form: Enumerate every subset.
2. A mask chooses problems; test count>=2, total difficulty in [l,r], and max-min>=x.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ d=[800,1000,1300], mask=101
├─ chosen bits: problem0 and problem2
├─ count=2
├─ sum=2100
├─ max-min=1300-800=500
├─ Check all three conditions. Each mask is one candidate team.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          |
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    int n,l,r,x;
    cin>>n>>l>>r>>x;
    vector<int>a(n);
    for (int&v:a)cin>>v;
    int ans=0;
    for (int m=0;m<(1<<n);m++){
        int c=0,s=0,mn=1e9,mx=-1;
        for (int i=0;i<n;i++)if (m>>i&1){
            c++;
            s+=a[i];
            mn=min(mn,a[i]);
            mx=max(mx,a[i]);
        }
        if (c>=2&&s>=l&&s<=r&&mx-mn>=x)ans++;
    }
    cout<<ans<<'\n';
}
```

---

<a id="form-25"></a>

# Form 25 — Submask Enumeration

## Form explained visually

**What this form means:** Enumerate only subsets that are contained inside an existing mask.

**Daily-life mapping — choose a team only from available employees**
```text
available mask = 10110

Allowed submasks may use ONLY those 1 positions.

s = mask
s = (s-1) & mask
s = (s-1) & mask
...
0
```

**Recognition signal**
```text
iterate all subsets of a given set/mask
partition mask
DP transition over contained subsets
        ↓
Use (s-1) & mask
```

**Core complexity:** all submasks of one mask = `O(2^k)` where `k=popcount(mask)`; over all masks often `O(3^n)`.

### Bit-by-bit form example

```text
mask = 1011

Enumerate only its submasks:

s = 1011
    ↓ (s-1)&mask
    1010
    ↓
    1001
    ↓
    1000
    ↓
    0011
    ↓
    0010
    ↓
    0001
    ↓
    0000

Notice bit2 of mask is 0,
so bit2 is 0 in every submask.
```

**Pattern:** `s = (s-1) & mask` visits every submask and nothing outside the mask.


<a id="form-25-problem-1"></a>

## Problem 1 — [1042B — Vitamins](https://codeforces.com/problemset/problem/1042/B)

### What the problem wants — simple words

Buy the cheapest set of juices that together provide vitamins A, B, and C.

### Real-world mapping

A, B, C are three checkboxes. Encode the checked boxes as bits; OR combines the vitamins supplied by purchases.

### How this form applies

Encode vitamins A,B,C as bits 0,1,2. Each juice is a mask. OR combines acquired vitamins; DP over 8 masks gives minimum cost.

### How the solution works — step by step

1. Identify the bit property used by this form: Encode vitamins A,B,C as bits 0,1,2.
2. Each juice is a mask.
3. OR combines acquired vitamins; DP over 8 masks gives minimum cost.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ A=001,B=010,C=100
├─ juice AB ->011 cost5
├─ juice C  ->100 cost3
├─ state 000 --buy AB-->011
├─ 011 |100 =111
├─ cost=8, all vitamins covered.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin>>n;
    const int INF=1e9;
    vector<int>dp(8,INF);
    dp[0]=0;
    while (n--){
        int c;
        string s;
        cin>>c>>s;
        int m=0;
        for (char ch:s)m|=1<<(ch-'A');
        auto ndp=dp;
        for (int st=0;st<8;st++)ndp[st|m]=min(ndp[st|m],dp[st]+c);
        dp=ndp;
    }
    cout<<(dp[7]>=INF?-1:dp[7])<<'\n';
}
```


---

<a id="form-25-problem-2"></a>

## Problem 2 — [1829C — Mr. Perfectly Fine](https://codeforces.com/problemset/problem/1829/C)

### What the problem wants — simple words

Find the minimum time/cost to obtain both required skills.

### Real-world mapping

There are only two skill switches. An item can give 01, 10, or 11; combine masks with OR and keep the cheapest way to reach 11.

### How this form applies

Two skills are a 2-bit mask: 01,10,11. Keep cheapest cost for each mask. Answer is min(cost11, cost01+cost10).

### How the solution works — step by step

1. Identify the bit property used by this form: Two skills are a 2-bit mask: 01,10,11.
2. Keep cheapest cost for each mask.
3. Answer is min(cost11, cost01+cost10).
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ book1 skill 10 cost4 -> mask2
├─ book2 skill 01 cost3 -> mask1
├─ book3 skill 11 cost10 -> mask3
├─ Combine mask2|mask1=11 cost7
├─ min(7,10)=7.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        const int INF=1e9;
        int best[4]={
            0,INF,INF,INF
        }
        ;
        while (n--){
            int c;
            string s;
            cin>>c>>s;
            int m=(s[0]-'0')*2+(s[1]-'0');
            best[m]=min(best[m],c);
        }
        int ans=min(best[3],best[1]+best[2]);
        cout<<(ans>=INF?-1:ans)<<'\n';
    }
}
```


---

<a id="form-25-problem-3"></a>

## Problem 3 — [550B — Preparing Olympiad](https://codeforces.com/problemset/problem/550/B)

### What the problem wants — simple words

Count subsets of problems satisfying the required size/sum/difficulty constraints.

### Real-world mapping

Each problem is an ON/OFF switch in a subset mask. Turn on a set of problems, then test whether that selected package satisfies all rules.

### How this form applies

Enumerate every subset. A mask chooses problems; test count>=2, total difficulty in [l,r], and max-min>=x.

### How the solution works — step by step

1. Identify the bit property used by this form: Enumerate every subset.
2. A mask chooses problems; test count>=2, total difficulty in [l,r], and max-min>=x.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ d=[800,1000,1300], mask=101
├─ chosen bits: problem0 and problem2
├─ count=2
├─ sum=2100
├─ max-min=1300-800=500
├─ Check all three conditions. Each mask is one candidate team.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    int n,l,r,x;
    cin>>n>>l>>r>>x;
    vector<int>a(n);
    for (int&v:a)cin>>v;
    int ans=0;
    for (int m=0;m<(1<<n);m++){
        int c=0,s=0,mn=1e9,mx=-1;
        for (int i=0;i<n;i++)if (m>>i&1){
            c++;
            s+=a[i];
            mn=min(mn,a[i]);
            mx=max(mx,a[i]);
        }
        if (c>=2&&s>=l&&s<=r&&mx-mn>=x)ans++;
    }
    cout<<ans<<'\n';
}
```


---

<a id="form-25-problem-4"></a>

## Problem 4 — [1097B — Petr and a Combination Lock](https://codeforces.com/problemset/problem/1097/B)

### What the problem wants — simple words

Choose clockwise or counter-clockwise for every angle so the final rotation is divisible by 360.

### Real-world mapping

Each bit of a mask is a direction switch: 1 means +angle and 0 means -angle. Try every switch configuration.

### How this form applies

Each angle has two choices: + or -. Encode the choice for angle i in bit i of a mask and enumerate all 2^n assignments.

### How the solution works — step by step

1. Identify the bit property used by this form: Each angle has two choices: + or -.
2. Encode the choice for angle i in bit i of a mask and enumerate all 2^n assignments.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ n=3, angles=[10,20,30]
├─ mask=101
├─ bit0=1 -> +10
├─ bit1=0 -> -20
├─ bit2=1 -> +30
├─ sum=20, not divisible by360.
├─ Try every mask 000..111 until one gives sum%360=0.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          &
          ────
result  = 0000
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin>>n;
    vector<int>a(n);
    for (int&x:a)cin>>x;
    for (int m=0;m<(1<<n);m++){
        int s=0;
        for (int i=0;i<n;i++)s+=(m>>i&1)?a[i]:-a[i];
        if ((s%360+360)%360==0){
            cout<<"YES\n";
            return 0;
        }
    }
    cout<<"NO\n";
}
```

---

<a id="form-26"></a>

# Form 26 — Bitmask DP

## Form explained visually

**What this form means:** DP state is a bitmask describing what has already been collected/assigned/visited.

**Daily-life mapping — delivery checklist**
```text
4 deliveries:
state = 0101

delivery 0 ✓
delivery 1 ✗
delivery 2 ✓
delivery 3 ✗

DP[state] = best cost to reach this checklist
        │
        ├─ add delivery 1 → 0111
        └─ add delivery 3 → 1101
```

**Recognition signal**
```text
n/features small
order/assignment matters
need min/max/count over subsets
same subset reached in many ways
        ↓
DP[mask]
```

**Core pattern:** transition from `mask` to `mask | (1<<i)` and keep the best value.

### Bit-by-bit form example

```text
Goal: collect A,B,C with minimum cost.

A=001, B=010, C=100

dp[mask] = cheapest cost to reach mask

Start:
dp[000] = 0

Buy AB=011 for 5:

000 | 011 = 011
dp[011] = 5

Then buy C=100 for 3:

011 | 100 = 111
dp[111] = 8

State path:

000
 │ +011 (5)
 ▼
011
 │ +100 (3)
 ▼
111  ← goal
```

**Pattern:** bitmask DP = compact state + transitions + best value per state.


<a id="form-26-problem-1"></a>

## Problem 1 — [1042B — Vitamins](https://codeforces.com/problemset/problem/1042/B)

### What the problem wants — simple words

Buy the cheapest set of juices that together provide vitamins A, B, and C.

### Real-world mapping

A, B, C are three checkboxes. Encode the checked boxes as bits; OR combines the vitamins supplied by purchases.

### How this form applies

Encode vitamins A,B,C as bits 0,1,2. Each juice is a mask. OR combines acquired vitamins; DP over 8 masks gives minimum cost.

### How the solution works — step by step

1. Identify the bit property used by this form: Encode vitamins A,B,C as bits 0,1,2.
2. Each juice is a mask.
3. OR combines acquired vitamins; DP over 8 masks gives minimum cost.
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ A=001,B=010,C=100
├─ juice AB ->011 cost5
├─ juice C  ->100 cost3
├─ state 000 --buy AB-->011
├─ 011 |100 =111
├─ cost=8, all vitamins covered.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          |
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin>>n;
    const int INF=1e9;
    vector<int>dp(8,INF);
    dp[0]=0;
    while (n--){
        int c;
        string s;
        cin>>c>>s;
        int m=0;
        for (char ch:s)m|=1<<(ch-'A');
        auto ndp=dp;
        for (int st=0;st<8;st++)ndp[st|m]=min(ndp[st|m],dp[st]+c);
        dp=ndp;
    }
    cout<<(dp[7]>=INF?-1:dp[7])<<'\n';
}
```


---

<a id="form-26-problem-2"></a>

## Problem 2 — [1829C — Mr. Perfectly Fine](https://codeforces.com/problemset/problem/1829/C)

### What the problem wants — simple words

Find the minimum time/cost to obtain both required skills.

### Real-world mapping

There are only two skill switches. An item can give 01, 10, or 11; combine masks with OR and keep the cheapest way to reach 11.

### How this form applies

Two skills are a 2-bit mask: 01,10,11. Keep cheapest cost for each mask. Answer is min(cost11, cost01+cost10).

### How the solution works — step by step

1. Identify the bit property used by this form: Two skills are a 2-bit mask: 01,10,11.
2. Keep cheapest cost for each mask.
3. Answer is min(cost11, cost01+cost10).
4. Write the relevant values in binary and inspect only the bit positions that affect the condition.
5. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ book1 skill 10 cost4 -> mask2
├─ book2 skill 01 cost3 -> mask1
├─ book3 skill 11 cost10 -> mask3
├─ Combine mask2|mask1=11 cost7
├─ min(7,10)=7.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          |
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while (T--){
        int n;
        cin>>n;
        const int INF=1e9;
        int best[4]={
            0,INF,INF,INF
        }
        ;
        while (n--){
            int c;
            string s;
            cin>>c>>s;
            int m=(s[0]-'0')*2+(s[1]-'0');
            best[m]=min(best[m],c);
        }
        int ans=min(best[3],best[1]+best[2]);
        cout<<(ans>=INF?-1:ans)<<'\n';
    }
}
```


---

<a id="form-26-problem-3"></a>

## Problem 3 — [1097B — Petr and a Combination Lock](https://codeforces.com/problemset/problem/1097/B)

### What the problem wants — simple words

Choose clockwise or counter-clockwise for every angle so the final rotation is divisible by 360.

### Real-world mapping

Each bit of a mask is a direction switch: 1 means +angle and 0 means -angle. Try every switch configuration.

### How this form applies

Each angle has two choices: + or -. Encode the choice for angle i in bit i of a mask and enumerate all 2^n assignments.

### How the solution works — step by step

1. Identify the bit property used by this form: Each angle has two choices: + or -.
2. Encode the choice for angle i in bit i of a mask and enumerate all 2^n assignments.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ n=3, angles=[10,20,30]
├─ mask=101
├─ bit0=1 -> +10
├─ bit1=0 -> -20
├─ bit2=1 -> +30
├─ sum=20, not divisible by360.
├─ Try every mask 000..111 until one gives sum%360=0.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          |
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    int n;
    cin>>n;
    vector<int>a(n);
    for (int&x:a)cin>>x;
    for (int m=0;m<(1<<n);m++){
        int s=0;
        for (int i=0;i<n;i++)s+=(m>>i&1)?a[i]:-a[i];
        if ((s%360+360)%360==0){
            cout<<"YES\n";
            return 0;
        }
    }
    cout<<"NO\n";
}
```


---

<a id="form-26-problem-4"></a>

## Problem 4 — [550B — Preparing Olympiad](https://codeforces.com/problemset/problem/550/B)

### What the problem wants — simple words

Count subsets of problems satisfying the required size/sum/difficulty constraints.

### Real-world mapping

Each problem is an ON/OFF switch in a subset mask. Turn on a set of problems, then test whether that selected package satisfies all rules.

### How this form applies

Enumerate every subset. A mask chooses problems; test count>=2, total difficulty in [l,r], and max-min>=x.

### How the solution works — step by step

1. Identify the bit property used by this form: Enumerate every subset.
2. A mask chooses problems; test count>=2, total difficulty in [l,r], and max-min>=x.
3. Write the relevant values in binary and inspect only the bit positions that affect the condition.
4. Apply the rule shown in the dry run below, then implement the same decision with bit operations.

### Bit-by-bit ASCII dry run

```text
READ THE EXAMPLE AS BIT COLUMNS
-------------------------------
decimal value  -> binary switches
bit = 1        -> switch ON
bit = 0        -> switch OFF

STEP 1
│
├─ d=[800,1000,1300], mask=101
├─ chosen bits: problem0 and problem2
├─ count=2
├─ sum=2100
├─ max-min=1300-800=500
├─ Check all three conditions. Each mask is one candidate team.

RESULT
```
#### Visual bit operation

```text
x       = 0001
y       = 0000
          |
          ────
result  = 0001
```



### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){
    int n,l,r,x;
    cin>>n>>l>>r>>x;
    vector<int>a(n);
    for (int&v:a)cin>>v;
    int ans=0;
    for (int m=0;m<(1<<n);m++){
        int c=0,s=0,mn=1e9,mx=-1;
        for (int i=0;i<n;i++)if (m>>i&1){
            c++;
            s+=a[i];
            mn=min(mn,a[i]);
            mx=max(mx,a[i]);
        }
        if (c>=2&&s>=l&&s<=r&&mx-mn>=x)ans++;
    }
    cout<<ans<<'\n';
}
```
