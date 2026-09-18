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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        int x,y=0,z=0;
        for(int i=0;i<n;i++){
            cin>>x;
            if((x&1)!=(i&1)){
                if(x&1)y++;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<int>a(n);
        int even=0,mn=31;
        for(int&x:a){
            cin>>x;
            if(x%2==0){
                even++;
                mn=min(mn,__builtin_ctz(x));
            }
    }
if(even<n)cout<<even<<'\n';
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        int xr=0,x;
        for(int i=0;i<n;i++){
            cin>>x;xr^=x;
        }
    if(n&1) cout<<xr<<'\n';
    else cout<<(xr==0?0:-1)<<'\n';
}
}
```

---

<a id="form-2"></a>

# Form 2 — Power of Two / Remove Lowest Set Bit

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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        long long n;
        cin>>n;
        long long p=1;
        while((p<<1)<=n)p<<=1;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
long long px(long long n){
    if(n<0)return 0;switch(n&3){
        case 0:return n;case 1:return 1;case 2:return n+1;default:return 0;
    }
}
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        long long a,b;
        cin>>a>>b;
        long long x=px(a-1);
        if(x==b)cout<<a;
        else if((x^b)==a)cout<<a+2;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<long long>a(n);
        for(auto&x:a)cin>>x;
        for(int b=1;b<=61;b++){
            long long m=1LL<<b;set<long long>s;
            for(auto x:a)s.insert(x%m);
            if(s.size()==2){
                cout<<m<<'\n';break;
            }
    }
}
}
```

---

<a id="form-3"></a>

# Form 3 — Kth-bit Masking

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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        long long k;
        cin>>n>>k;
        vector<int>a(n);
        for(int&x:a)cin>>x;
        long long ans=0;
        for(int b=30;b>=0;b--){
            long long need=0;
            for(int x:a)if(!(x&(1<<b)))need++;
            if(need<=k){
                k-=need;ans|=1LL<<b;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<int>x(n),y(n);
        for(int&i:x)cin>>i;
        int prev=x[0];
        for(int i=1;i<n;i++){
            y[i]=prev & (~x[i]);
            prev=x[i]^y[i];
        }
    for(int v:y)cout<<v<<' ';
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
    while(T--){
        int n;
        cin>>n;unordered_map<long long,int>cnt;
        int ans=0;
        while(n--){
            long long x;
            cin>>x,y=x^M;
            if(cnt[y])cnt[y]--;
            else{
                cnt[x]++;ans++;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    int n;
    cin>>n;
    const int INF=1e9;
    vector<int>dp(8,INF);dp[0]=0;
    while(n--){
        int c;string s;
        cin>>c>>s;
        int m=0;
        for(char ch:s)m|=1<<(ch-'A');
        auto ndp=dp;
        for(int st=0;st<8;st++)ndp[st|m]=min(ndp[st|m],dp[st]+c);
        dp=ndp;
    }
cout<<(dp[7]>=INF?-1:dp[7])<<'\n';
}
```

---

<a id="form-4"></a>

# Form 4 — XOR Cancellation

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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<int>a(n);
        for(int&x:a)cin>>x;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        int xr=0,x;
        for(int i=0;i<n;i++){
            cin>>x;xr^=x;
        }
    if(n&1) cout<<xr<<'\n';
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<int>a(n);
        int S=0;
        for(int&x:a){
            cin>>x;S^=x;
        }
    if(S==0){
        cout<<"YES\n";continue;
    }
int cur=0,cnt=0;
for(int i=0;i<n-1;i++){
    cur^=a[i];
    if(cur==S){
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<long long>a(n),p(n+1);
        for(int i=0;i<n;i++){
            cin>>a[i];p[i+1]=p[i]^a[i];
        }
    string s;
    cin>>s;
    long long g[2]={
        0,0
    }
;
for(int i=0;i<n;i++)g[s[i]-'0']^=a[i];
int q;
cin>>q;
while(q--){
    int tp;
    cin>>tp;
    if(tp==1){
        int l,r;
        cin>>l>>r;
        long long x=p[r]^p[l-1];g[0]^=x;g[1]^=x;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        int xr=0,x;
        for(int i=0;i<n;i++){
            cin>>x;xr^=x;
        }
    if(n&1) cout<<xr<<'\n';
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<int>a(n);
        for(int&x:a)cin>>x;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
long long px(long long n){
    if(n<0)return 0;switch(n&3){
        case 0:return n;case 1:return 1;case 2:return n+1;default:return 0;
    }
}
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        long long a,b;
        cin>>a>>b;
        long long x=px(a-1);
        if(x==b)cout<<a;
        else if((x^b)==a)cout<<a+2;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<int>a(n);
        int S=0;
        for(int&x:a){
            cin>>x;S^=x;
        }
    if(S==0){
        cout<<"YES\n";continue;
    }
int cur=0,cnt=0;
for(int i=0;i<n-1;i++){
    cur^=a[i];
    if(cur==S){
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
long long px(long long n){
    if(n<0)return 0;switch(n&3){
        case 0:return n;case 1:return 1;case 2:return n+1;default:return 0;
    }
}
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        long long a,b;
        cin>>a>>b;
        long long x=px(a-1);
        if(x==b)cout<<a;
        else if((x^b)==a)cout<<a+2;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        long long n;
        cin>>n;
        long long p=1;
        while((p<<1)<=n)p<<=1;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<long long>a(n);
        for(auto&x:a)cin>>x;
        for(int b=1;b<=61;b++){
            long long m=1LL<<b;set<long long>s;
            for(auto x:a)s.insert(x%m);
            if(s.size()==2){
                cout<<m<<'\n';break;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    int n;
    cin>>n;
    vector<int>a(n);
    for(int&x:a)cin>>x;
    for(int m=0;m<(1<<n);m++){
        int s=0;
        for(int i=0;i<n;i++)s+=(m>>i&1)?a[i]:-a[i];
        if((s%360+360)%360==0){
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<long long>a(n),p(n+1);
        for(int i=0;i<n;i++){
            cin>>a[i];p[i+1]=p[i]^a[i];
        }
    string s;
    cin>>s;
    long long g[2]={
        0,0
    }
;
for(int i=0;i<n;i++)g[s[i]-'0']^=a[i];
int q;
cin>>q;
while(q--){
    int tp;
    cin>>tp;
    if(tp==1){
        int l,r;
        cin>>l>>r;
        long long x=p[r]^p[l-1];g[0]^=x;g[1]^=x;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<int>a(n);
        int S=0;
        for(int&x:a){
            cin>>x;S^=x;
        }
    if(S==0){
        cout<<"YES\n";continue;
    }
int cur=0,cnt=0;
for(int i=0;i<n-1;i++){
    cur^=a[i];
    if(cur==S){
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<int>a(n);
        for(int&x:a)cin>>x;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        int xr=0,x;
        for(int i=0;i<n;i++){
            cin>>x;xr^=x;
        }
    if(n&1) cout<<xr<<'\n';
    else cout<<(xr==0?0:-1)<<'\n';
}
}
```

---

<a id="form-8"></a>

# Form 8 — XOR Difference Mask / Hamming Bits

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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        long long a,b,r;
        cin>>a>>b>>r;
        if(a<b)swap(a,b);
        long long x=0;
        bool first=true;
        for(int i=62;i>=0;i--){
            long long bit=1LL<<i;
            bool A=a&bit,B=b&bit;
            if(A!=B){
                if(first){
                    first=false;continue;
                }
            if(A&&!B && x+bit<=r)x+=bit;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<int>a(n);set<int>s;
        for(int&v:a){
            cin>>v;s.insert(v);
        }
    int ans=-1;
    for(int x=1;x<1024;x++){
        set<int>t;
        for(int v:a)t.insert(v^x);
        if(t==s){
            ans=x;break;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<int>x(n),y(n);
        for(int&i:x)cin>>i;
        int prev=x[0];
        for(int i=1;i<n;i++){
            y[i]=prev & (~x[i]);
            prev=x[i]^y[i];
        }
    for(int v:y)cout<<v<<' ';
    cout<<'\n';
}
}
```

---

<a id="form-9"></a>

# Form 9 — Modulo 2^k = Binary Suffix

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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<long long>a(n);
        for(auto&x:a)cin>>x;
        for(int b=1;b<=61;b++){
            long long m=1LL<<b;set<long long>s;
            for(auto x:a)s.insert(x%m);
            if(s.size()==2){
                cout<<m<<'\n';break;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        long long n;
        cin>>n;
        long long p=1;
        while((p<<1)<=n)p<<=1;
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
    while(T--){
        int n;
        cin>>n;unordered_map<long long,int>cnt;
        int ans=0;
        while(n--){
            long long x;
            cin>>x,y=x^M;
            if(cnt[y])cnt[y]--;
            else{
                cnt[x]++;ans++;
            }
    }
cout<<ans<<'\n';
}
}
```

---

<a id="form-10"></a>

# Form 10 — Highest Set Bit / MSB Grouping

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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        long long c[32]={
        }
    ,ans=0;
    while(n--){
        int x;
        cin>>x;
        int b=31-__builtin_clz(x);c[b]++;
    }
for(long long v:c)ans+=v*(v-1)/2;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        long long n;
        cin>>n;
        long long p=1;
        while((p<<1)<=n)p<<=1;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        long long a,b,r;
        cin>>a>>b>>r;
        if(a<b)swap(a,b);
        long long x=0;
        bool first=true;
        for(int i=62;i>=0;i--){
            long long bit=1LL<<i;
            bool A=a&bit,B=b&bit;
            if(A!=B){
                if(first){
                    first=false;continue;
                }
            if(A&&!B && x+bit<=r)x+=bit;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        long long k;
        cin>>n>>k;
        vector<int>a(n);
        for(int&x:a)cin>>x;
        long long ans=0;
        for(int b=30;b>=0;b--){
            long long need=0;
            for(int x:a)if(!(x&(1<<b)))need++;
            if(need<=k){
                k-=need;ans|=1LL<<b;
            }
    }
cout<<ans<<'\n';
}
}
```

---

<a id="form-11"></a>

# Form 11 — Lowest Set Bit / 2-adic Structure

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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<long long>a(n);
        for(auto&x:a)cin>>x;
        for(int b=1;b<=61;b++){
            long long m=1LL<<b;set<long long>s;
            for(auto x:a)s.insert(x%m);
            if(s.size()==2){
                cout<<m<<'\n';break;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        long long n;
        cin>>n;
        long long p=1;
        while((p<<1)<=n)p<<=1;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<int>a(n);
        int even=0,mn=31;
        for(int&x:a){
            cin>>x;
            if(x%2==0){
                even++;
                mn=min(mn,__builtin_ctz(x));
            }
    }
if(even<n)cout<<even<<'\n';
else cout<<n-1+mn<<'\n';
}
}
```

---

<a id="form-12"></a>

# Form 12 — OR Monotonicity / Required Bits

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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n,x;
        cin>>n>>x;
        vector<vector<int>>a(3,vector<int>(n));
        for(auto&v:a)for(int&z:v)cin>>z;
        int cur=0;
        for(auto&v:a)for(int z:v){
            if((z|x)!=x)break;cur|=z;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<int>x(n),y(n);
        for(int&i:x)cin>>i;
        int prev=x[0];
        for(int i=1;i<n;i++){
            y[i]=prev & (~x[i]);
            prev=x[i]^y[i];
        }
    for(int v:y)cout<<v<<' ';
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    int n;
    cin>>n;
    const int INF=1e9;
    vector<int>dp(8,INF);dp[0]=0;
    while(n--){
        int c;string s;
        cin>>c>>s;
        int m=0;
        for(char ch:s)m|=1<<(ch-'A');
        auto ndp=dp;
        for(int st=0;st<8;st++)ndp[st|m]=min(ndp[st|m],dp[st]+c);
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        const int INF=1e9;
        int best[4]={
            0,INF,INF,INF
        }
    ;
    while(n--){
        int c;string s;
        cin>>c>>s;
        int m=(s[0]-'0')*2+(s[1]-'0');best[m]=min(best[m],c);
    }
int ans=min(best[3],best[1]+best[2]);
cout<<(ans>=INF?-1:ans)<<'\n';
}
}
```

---

<a id="form-13"></a>

# Form 13 — AND Monotonicity / Maximal AND

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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        long long k;
        cin>>n>>k;
        vector<int>a(n);
        for(int&x:a)cin>>x;
        long long ans=0;
        for(int b=30;b>=0;b--){
            long long need=0;
            for(int x:a)if(!(x&(1<<b)))need++;
            if(need<=k){
                k-=need;ans|=1LL<<b;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
const long long MOD=1e9+7;
long long pw(long long a,long long e){
    long long r=1;
    while(e){
        if(e&1)r=r*a%MOD;
        a=a*a%MOD;e>>=1;
    }
return r;
}
int main(){
    int T;
    cin>>T;
    while(T--){
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<int>b(n-1);
        for(int&x:b)cin>>x;
        vector<int>a(n);
        if(n==1){
            cout<<0<<'\n';continue;
        }
    a[0]=b[0];a[n-1]=b[n-2];
    for(int i=1;i<n-1;i++)a[i]=b[i-1]|b[i];
    bool ok=true;
    for(int i=0;i<n-1;i++)if((a[i]&a[i+1])!=b[i])ok=false;
    if(!ok)cout<<-1<<'\n';
    else{
        for(int x:a)cout<<x<<' ';
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<vector<int>>m(n,vector<int>(n));
        for(auto&r:m)for(int&x:r)cin>>x;
        vector<int>a(n,(1<<30)-1);
        for(int i=0;i<n;i++)for(int j=0;j<n;j++)if(i!=j)a[i]&=m[i][j];
        bool ok=true;
        for(int i=0;i<n;i++)for(int j=0;j<n;j++)if(i!=j && (a[i]|a[j])!=m[i][j])ok=false;
        if(!ok)cout<<"NO\n";
        else{
            cout<<"YES\n";
            for(int x:a)cout<<x<<' ';
            cout<<'\n';
        }
}
}
```

---

<a id="form-14"></a>

# Form 14 — Bit Frequency / Majority Per Bit

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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n,l;
        cin>>n>>l;
        vector<int>a(n);
        for(int&x:a)cin>>x;
        int ans=0;
        for(int b=0;b<l;b++){
            int one=0;
            for(int x:a)one+=(x>>b)&1;
            if(one>n-one)ans|=1<<b;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        long long k;
        cin>>n>>k;
        vector<int>a(n);
        for(int&x:a)cin>>x;
        long long ans=0;
        for(int b=30;b>=0;b--){
            long long need=0;
            for(int x:a)if(!(x&(1<<b)))need++;
            if(need<=k){
                k-=need;ans|=1LL<<b;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
const long long MOD=1e9+7;
long long pw(long long a,long long e){
    long long r=1;
    while(e){
        if(e&1)r=r*a%MOD;
        a=a*a%MOD;e>>=1;
    }
return r;
}
int main(){
    int T;
    cin>>T;
    while(T--){
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        int x,y=0,z=0;
        for(int i=0;i<n;i++){
            cin>>x;
            if((x&1)!=(i&1)){
                if(x&1)y++;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<vector<int>>m(n,vector<int>(n));
        for(auto&r:m)for(int&x:r)cin>>x;
        vector<int>a(n,(1<<30)-1);
        for(int i=0;i<n;i++)for(int j=0;j<n;j++)if(i!=j)a[i]&=m[i][j];
        bool ok=true;
        for(int i=0;i<n;i++)for(int j=0;j<n;j++)if(i!=j && (a[i]|a[j])!=m[i][j])ok=false;
        if(!ok)cout<<"NO\n";
        else{
            cout<<"YES\n";
            for(int x:a)cout<<x<<' ';
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<int>b(n-1);
        for(int&x:b)cin>>x;
        vector<int>a(n);
        if(n==1){
            cout<<0<<'\n';continue;
        }
    a[0]=b[0];a[n-1]=b[n-2];
    for(int i=1;i<n-1;i++)a[i]=b[i-1]|b[i];
    bool ok=true;
    for(int i=0;i<n-1;i++)if((a[i]&a[i+1])!=b[i])ok=false;
    if(!ok)cout<<-1<<'\n';
    else{
        for(int x:a)cout<<x<<' ';
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<int>x(n),y(n);
        for(int&i:x)cin>>i;
        int prev=x[0];
        for(int i=1;i<n;i++){
            y[i]=prev & (~x[i]);
            prev=x[i]^y[i];
        }
    for(int v:y)cout<<v<<' ';
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n,x;
        cin>>n>>x;
        vector<vector<int>>a(3,vector<int>(n));
        for(auto&v:a)for(int&z:v)cin>>z;
        int cur=0;
        for(auto&v:a)for(int z:v){
            if((z|x)!=x)break;cur|=z;
        }
    cout<<(cur==x?"Yes":"No")<<'\n';
}
}
```

---

<a id="form-16"></a>

# Form 16 — Pairwise XOR Contribution

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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        long long a,b,r;
        cin>>a>>b>>r;
        if(a<b)swap(a,b);
        long long x=0;
        bool first=true;
        for(int i=62;i>=0;i--){
            long long bit=1LL<<i;
            bool A=a&bit,B=b&bit;
            if(A!=B){
                if(first){
                    first=false;continue;
                }
            if(A&&!B && x+bit<=r)x+=bit;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<int>a(n);
        for(int&x:a)cin>>x;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<int>a(n);
        int S=0;
        for(int&x:a){
            cin>>x;S^=x;
        }
    if(S==0){
        cout<<"YES\n";continue;
    }
int cur=0,cnt=0;
for(int i=0;i<n-1;i++){
    cur^=a[i];
    if(cur==S){
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
const long long MOD=1e9+7;
long long pw(long long a,long long e){
    long long r=1;
    while(e){
        if(e&1)r=r*a%MOD;
        a=a*a%MOD;e>>=1;
    }
return r;
}
int main(){
    int T;
    cin>>T;
    while(T--){
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        long long k;
        cin>>n>>k;
        vector<int>a(n);
        for(int&x:a)cin>>x;
        long long ans=0;
        for(int b=30;b>=0;b--){
            long long need=0;
            for(int x:a)if(!(x&(1<<b)))need++;
            if(need<=k){
                k-=need;ans|=1LL<<b;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<vector<int>>m(n,vector<int>(n));
        for(auto&r:m)for(int&x:r)cin>>x;
        vector<int>a(n,(1<<30)-1);
        for(int i=0;i<n;i++)for(int j=0;j<n;j++)if(i!=j)a[i]&=m[i][j];
        bool ok=true;
        for(int i=0;i<n;i++)for(int j=0;j<n;j++)if(i!=j && (a[i]|a[j])!=m[i][j])ok=false;
        if(!ok)cout<<"NO\n";
        else{
            cout<<"YES\n";
            for(int x:a)cout<<x<<' ';
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<int>b(n-1);
        for(int&x:b)cin>>x;
        vector<int>a(n);
        if(n==1){
            cout<<0<<'\n';continue;
        }
    a[0]=b[0];a[n-1]=b[n-2];
    for(int i=1;i<n-1;i++)a[i]=b[i-1]|b[i];
    bool ok=true;
    for(int i=0;i<n-1;i++)if((a[i]&a[i+1])!=b[i])ok=false;
    if(!ok)cout<<-1<<'\n';
    else{
        for(int x:a)cout<<x<<' ';
        cout<<'\n';
    }
}
}
```

---

<a id="form-18"></a>

# Form 18 — Conservation / Operation Decoding

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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        int xr=0,x;
        for(int i=0;i<n;i++){
            cin>>x;xr^=x;
        }
    if(n&1) cout<<xr<<'\n';
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<int>a(n);
        int S=0;
        for(int&x:a){
            cin>>x;S^=x;
        }
    if(S==0){
        cout<<"YES\n";continue;
    }
int cur=0,cnt=0;
for(int i=0;i<n-1;i++){
    cur^=a[i];
    if(cur==S){
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<int>a(n);
        for(int&x:a)cin>>x;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<int>x(n),y(n);
        for(int&i:x)cin>>i;
        int prev=x[0];
        for(int i=1;i<n;i++){
            y[i]=prev & (~x[i]);
            prev=x[i]^y[i];
        }
    for(int v:y)cout<<v<<' ';
    cout<<'\n';
}
}
```

---

<a id="form-19"></a>

# Form 19 — Highest Bit -> Lowest Bit Greedy

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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        long long k;
        cin>>n>>k;
        vector<int>a(n);
        for(int&x:a)cin>>x;
        long long ans=0;
        for(int b=30;b>=0;b--){
            long long need=0;
            for(int x:a)if(!(x&(1<<b)))need++;
            if(need<=k){
                k-=need;ans|=1LL<<b;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        long long a,b,r;
        cin>>a>>b>>r;
        if(a<b)swap(a,b);
        long long x=0;
        bool first=true;
        for(int i=62;i>=0;i--){
            long long bit=1LL<<i;
            bool A=a&bit,B=b&bit;
            if(A!=B){
                if(first){
                    first=false;continue;
                }
            if(A&&!B && x+bit<=r)x+=bit;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        long long n;
        cin>>n;
        long long p=1;
        while((p<<1)<=n)p<<=1;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        long long c[32]={
        }
    ,ans=0;
    while(n--){
        int x;
        cin>>x;
        int b=31-__builtin_clz(x);c[b]++;
    }
for(long long v:c)ans+=v*(v-1)/2;
cout<<ans<<'\n';
}
}
```

---

<a id="form-20"></a>

# Form 20 — Prefix Counts of Bits

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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<long long>a(n),p(n+1);
        for(int i=0;i<n;i++){
            cin>>a[i];p[i+1]=p[i]^a[i];
        }
    string s;
    cin>>s;
    long long g[2]={
        0,0
    }
;
for(int i=0;i<n;i++)g[s[i]-'0']^=a[i];
int q;
cin>>q;
while(q--){
    int tp;
    cin>>tp;
    if(tp==1){
        int l,r;
        cin>>l>>r;
        long long x=p[r]^p[l-1];g[0]^=x;g[1]^=x;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n,l;
        cin>>n>>l;
        vector<int>a(n);
        for(int&x:a)cin>>x;
        int ans=0;
        for(int b=0;b<l;b++){
            int one=0;
            for(int x:a)one+=(x>>b)&1;
            if(one>n-one)ans|=1<<b;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        long long k;
        cin>>n>>k;
        vector<int>a(n);
        for(int&x:a)cin>>x;
        long long ans=0;
        for(int b=30;b>=0;b--){
            long long need=0;
            for(int x:a)if(!(x&(1<<b)))need++;
            if(need<=k){
                k-=need;ans|=1LL<<b;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<int>a(n);
        int S=0;
        for(int&x:a){
            cin>>x;S^=x;
        }
    if(S==0){
        cout<<"YES\n";continue;
    }
int cur=0,cnt=0;
for(int i=0;i<n-1;i++){
    cur^=a[i];
    if(cur==S){
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        long long n;
        cin>>n;
        long long p=1;
        while((p<<1)<=n)p<<=1;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        long long c[32]={
        }
    ,ans=0;
    while(n--){
        int x;
        cin>>x;
        int b=31-__builtin_clz(x);c[b]++;
    }
for(long long v:c)ans+=v*(v-1)/2;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        long long k;
        cin>>n>>k;
        vector<int>a(n);
        for(int&x:a)cin>>x;
        long long ans=0;
        for(int b=30;b>=0;b--){
            long long need=0;
            for(int x:a)if(!(x&(1<<b)))need++;
            if(need<=k){
                k-=need;ans|=1LL<<b;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        long long n;
        cin>>n;
        cout<<((n&(n-1))?"YES":"NO")<<'\n';
    }
}
```

---

<a id="form-22"></a>

# Form 22 — Complement Within Fixed Width

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
    while(T--){
        int n;
        cin>>n;unordered_map<long long,int>cnt;
        int ans=0;
        while(n--){
            long long x;
            cin>>x,y=x^M;
            if(cnt[y])cnt[y]--;
            else{
                cnt[x]++;ans++;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        long long a,b,r;
        cin>>a>>b>>r;
        if(a<b)swap(a,b);
        long long x=0;
        bool first=true;
        for(int i=62;i>=0;i--){
            long long bit=1LL<<i;
            bool A=a&bit,B=b&bit;
            if(A!=B){
                if(first){
                    first=false;continue;
                }
            if(A&&!B && x+bit<=r)x+=bit;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        vector<int>a(n);set<int>s;
        for(int&v:a){
            cin>>v;s.insert(v);
        }
    int ans=-1;
    for(int x=1;x<1024;x++){
        set<int>t;
        for(int v:a)t.insert(v^x);
        if(t==s){
            ans=x;break;
        }
}
cout<<ans<<'\n';
}
}
```

---

<a id="form-23"></a>

# Form 23 — Subset Enumeration

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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    int n;
    cin>>n;
    vector<int>a(n);
    for(int&x:a)cin>>x;
    for(int m=0;m<(1<<n);m++){
        int s=0;
        for(int i=0;i<n;i++)s+=(m>>i&1)?a[i]:-a[i];
        if((s%360+360)%360==0){
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    int n,l,r,x;
    cin>>n>>l>>r>>x;
    vector<int>a(n);
    for(int&v:a)cin>>v;
    int ans=0;
    for(int m=0;m<(1<<n);m++){
        int c=0,s=0,mn=1e9,mx=-1;
        for(int i=0;i<n;i++)if(m>>i&1){
            c++;s+=a[i];
            mn=min(mn,a[i]);
            mx=max(mx,a[i]);
        }
    if(c>=2&&s>=l&&s<=r&&mx-mn>=x)ans++;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    int n;
    cin>>n;
    const int INF=1e9;
    vector<int>dp(8,INF);dp[0]=0;
    while(n--){
        int c;string s;
        cin>>c>>s;
        int m=0;
        for(char ch:s)m|=1<<(ch-'A');
        auto ndp=dp;
        for(int st=0;st<8;st++)ndp[st|m]=min(ndp[st|m],dp[st]+c);
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        const int INF=1e9;
        int best[4]={
            0,INF,INF,INF
        }
    ;
    while(n--){
        int c;string s;
        cin>>c>>s;
        int m=(s[0]-'0')*2+(s[1]-'0');best[m]=min(best[m],c);
    }
int ans=min(best[3],best[1]+best[2]);
cout<<(ans>=INF?-1:ans)<<'\n';
}
}
```

---

<a id="form-24"></a>

# Form 24 — Bitmask as State

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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    int n;
    cin>>n;
    const int INF=1e9;
    vector<int>dp(8,INF);dp[0]=0;
    while(n--){
        int c;string s;
        cin>>c>>s;
        int m=0;
        for(char ch:s)m|=1<<(ch-'A');
        auto ndp=dp;
        for(int st=0;st<8;st++)ndp[st|m]=min(ndp[st|m],dp[st]+c);
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        const int INF=1e9;
        int best[4]={
            0,INF,INF,INF
        }
    ;
    while(n--){
        int c;string s;
        cin>>c>>s;
        int m=(s[0]-'0')*2+(s[1]-'0');best[m]=min(best[m],c);
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    int n;
    cin>>n;
    vector<int>a(n);
    for(int&x:a)cin>>x;
    for(int m=0;m<(1<<n);m++){
        int s=0;
        for(int i=0;i<n;i++)s+=(m>>i&1)?a[i]:-a[i];
        if((s%360+360)%360==0){
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    int n,l,r,x;
    cin>>n>>l>>r>>x;
    vector<int>a(n);
    for(int&v:a)cin>>v;
    int ans=0;
    for(int m=0;m<(1<<n);m++){
        int c=0,s=0,mn=1e9,mx=-1;
        for(int i=0;i<n;i++)if(m>>i&1){
            c++;s+=a[i];
            mn=min(mn,a[i]);
            mx=max(mx,a[i]);
        }
    if(c>=2&&s>=l&&s<=r&&mx-mn>=x)ans++;
}
cout<<ans<<'\n';
}
```

---

<a id="form-25"></a>

# Form 25 — Submask Enumeration

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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    int n;
    cin>>n;
    const int INF=1e9;
    vector<int>dp(8,INF);dp[0]=0;
    while(n--){
        int c;string s;
        cin>>c>>s;
        int m=0;
        for(char ch:s)m|=1<<(ch-'A');
        auto ndp=dp;
        for(int st=0;st<8;st++)ndp[st|m]=min(ndp[st|m],dp[st]+c);
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        const int INF=1e9;
        int best[4]={
            0,INF,INF,INF
        }
    ;
    while(n--){
        int c;string s;
        cin>>c>>s;
        int m=(s[0]-'0')*2+(s[1]-'0');best[m]=min(best[m],c);
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    int n,l,r,x;
    cin>>n>>l>>r>>x;
    vector<int>a(n);
    for(int&v:a)cin>>v;
    int ans=0;
    for(int m=0;m<(1<<n);m++){
        int c=0,s=0,mn=1e9,mx=-1;
        for(int i=0;i<n;i++)if(m>>i&1){
            c++;s+=a[i];
            mn=min(mn,a[i]);
            mx=max(mx,a[i]);
        }
    if(c>=2&&s>=l&&s<=r&&mx-mn>=x)ans++;
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    int n;
    cin>>n;
    vector<int>a(n);
    for(int&x:a)cin>>x;
    for(int m=0;m<(1<<n);m++){
        int s=0;
        for(int i=0;i<n;i++)s+=(m>>i&1)?a[i]:-a[i];
        if((s%360+360)%360==0){
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    int n;
    cin>>n;
    const int INF=1e9;
    vector<int>dp(8,INF);dp[0]=0;
    while(n--){
        int c;string s;
        cin>>c>>s;
        int m=0;
        for(char ch:s)m|=1<<(ch-'A');
        auto ndp=dp;
        for(int st=0;st<8;st++)ndp[st|m]=min(ndp[st|m],dp[st]+c);
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    int T;
    cin>>T;
    while(T--){
        int n;
        cin>>n;
        const int INF=1e9;
        int best[4]={
            0,INF,INF,INF
        }
    ;
    while(n--){
        int c;string s;
        cin>>c>>s;
        int m=(s[0]-'0')*2+(s[1]-'0');best[m]=min(best[m],c);
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    int n;
    cin>>n;
    vector<int>a(n);
    for(int&x:a)cin>>x;
    for(int m=0;m<(1<<n);m++){
        int s=0;
        for(int i=0;i<n;i++)s+=(m>>i&1)?a[i]:-a[i];
        if((s%360+360)%360==0){
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

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){
    int n,l,r,x;
    cin>>n>>l>>r>>x;
    vector<int>a(n);
    for(int&v:a)cin>>v;
    int ans=0;
    for(int m=0;m<(1<<n);m++){
        int c=0,s=0,mn=1e9,mx=-1;
        for(int i=0;i<n;i++)if(m>>i&1){
            c++;s+=a[i];
            mn=min(mn,a[i]);
            mx=max(mx,a[i]);
        }
    if(c>=2&&s>=l&&s<=r&&mx-mn>=x)ans++;
}
cout<<ans<<'\n';
}
```
