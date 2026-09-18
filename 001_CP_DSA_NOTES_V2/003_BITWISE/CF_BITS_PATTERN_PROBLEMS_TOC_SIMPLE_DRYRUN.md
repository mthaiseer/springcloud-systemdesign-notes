# Codeforces Bitwise Forms — 4 Worked Problems Under Every Form

> Each form contains 4 worked Codeforces problems. Every problem is organized as: **how the form applies → simple bit-by-bit dry run → C++ solution**.

## Table of Contents

- [Form 1 — Parity / LSB](#form-1-parity-lsb)
  - [Problem 1 — 1367B — Even Array](#problem-1-1367b-even-array)
  - [Problem 2 — 1475A — Odd Divisor](#problem-2-1475a-odd-divisor)
  - [Problem 3 — 1688B — Patchouli’s Magical Talisman](#problem-3-1688b-patchoulis-magical-talisman)
  - [Problem 4 — 1805A — We Need the Zero](#problem-4-1805a-we-need-the-zero)
- [Form 2 — Power of Two / Remove Lowest Set Bit](#form-2-power-of-two-remove-lowest-set-bit)
  - [Problem 1 — 1475A — Odd Divisor](#problem-1-1475a-odd-divisor)
  - [Problem 2 — 1527A — And Then There Were K](#problem-2-1527a-and-then-there-were-k)
  - [Problem 3 — 1567B — MEXor Mixup](#problem-3-1567b-mexor-mixup)
  - [Problem 4 — 1909B — Make Almost Equal With Mod](#problem-4-1909b-make-almost-equal-with-mod)
- [Form 3 — Kth-bit Masking](#form-3-kth-bit-masking)
  - [Problem 1 — 1669H — Maximal AND](#problem-1-1669h-maximal-and)
  - [Problem 2 — 1547D — Co-growing Sequence](#problem-2-1547d-co-growing-sequence)
  - [Problem 3 — 1926D — Vlad and Division](#problem-3-1926d-vlad-and-division)
  - [Problem 4 — 1042B — Vitamins](#problem-4-1042b-vitamins)
- [Form 4 — XOR Cancellation](#form-4-xor-cancellation)
  - [Problem 1 — 1698A — XOR Mixup](#problem-1-1698a-xor-mixup)
  - [Problem 2 — 1805A — We Need the Zero](#problem-2-1805a-we-need-the-zero)
  - [Problem 3 — 1516B — AGAGA XOOORRR](#problem-3-1516b-agaga-xooorrr)
  - [Problem 4 — 1872E — Data Structures Fan](#problem-4-1872e-data-structures-fan)
- [Form 5 — Global XOR / Solve for X](#form-5-global-xor-solve-for-x)
  - [Problem 1 — 1805A — We Need the Zero](#problem-1-1805a-we-need-the-zero)
  - [Problem 2 — 1698A — XOR Mixup](#problem-2-1698a-xor-mixup)
  - [Problem 3 — 1567B — MEXor Mixup](#problem-3-1567b-mexor-mixup)
  - [Problem 4 — 1516B — AGAGA XOOORRR](#problem-4-1516b-agaga-xooorrr)
- [Form 6 — XOR 1..N Cycle](#form-6-xor-1n-cycle)
  - [Problem 1 — 1567B — MEXor Mixup](#problem-1-1567b-mexor-mixup)
  - [Problem 2 — 1527A — And Then There Were K](#problem-2-1527a-and-then-there-were-k)
  - [Problem 3 — 1909B — Make Almost Equal With Mod](#problem-3-1909b-make-almost-equal-with-mod)
  - [Problem 4 — 1097B — Petr and a Combination Lock](#problem-4-1097b-petr-and-a-combination-lock)
- [Form 7 — Prefix XOR](#form-7-prefix-xor)
  - [Problem 1 — 1872E — Data Structures Fan](#problem-1-1872e-data-structures-fan)
  - [Problem 2 — 1516B — AGAGA XOOORRR](#problem-2-1516b-agaga-xooorrr)
  - [Problem 3 — 1698A — XOR Mixup](#problem-3-1698a-xor-mixup)
  - [Problem 4 — 1805A — We Need the Zero](#problem-4-1805a-we-need-the-zero)
- [Form 8 — XOR Difference Mask / Hamming Bits](#form-8-xor-difference-mask-hamming-bits)
  - [Problem 1 — 1918C — XOR-distance](#problem-1-1918c-xor-distance)
  - [Problem 2 — 1362B — Johnny and His Hobbies](#problem-2-1362b-johnny-and-his-hobbies)
  - [Problem 3 — 1421A — XORwice](#problem-3-1421a-xorwice)
  - [Problem 4 — 1547D — Co-growing Sequence](#problem-4-1547d-co-growing-sequence)
- [Form 9 — Modulo 2^k = Binary Suffix](#form-9-modulo-2k-binary-suffix)
  - [Problem 1 — 1909B — Make Almost Equal With Mod](#problem-1-1909b-make-almost-equal-with-mod)
  - [Problem 2 — 1475A — Odd Divisor](#problem-2-1475a-odd-divisor)
  - [Problem 3 — 1527A — And Then There Were K](#problem-3-1527a-and-then-there-were-k)
  - [Problem 4 — 1926D — Vlad and Division](#problem-4-1926d-vlad-and-division)
- [Form 10 — Highest Set Bit / MSB Grouping](#form-10-highest-set-bit-msb-grouping)
  - [Problem 1 — 1420B — Rock and Lever](#problem-1-1420b-rock-and-lever)
  - [Problem 2 — 1527A — And Then There Were K](#problem-2-1527a-and-then-there-were-k)
  - [Problem 3 — 1918C — XOR-distance](#problem-3-1918c-xor-distance)
  - [Problem 4 — 1669H — Maximal AND](#problem-4-1669h-maximal-and)
- [Form 11 — Lowest Set Bit / 2-adic Structure](#form-11-lowest-set-bit-2-adic-structure)
  - [Problem 1 — 1475A — Odd Divisor](#problem-1-1475a-odd-divisor)
  - [Problem 2 — 1909B — Make Almost Equal With Mod](#problem-2-1909b-make-almost-equal-with-mod)
  - [Problem 3 — 1527A — And Then There Were K](#problem-3-1527a-and-then-there-were-k)
  - [Problem 4 — 1688B — Patchouli’s Magical Talisman](#problem-4-1688b-patchoulis-magical-talisman)
- [Form 12 — OR Monotonicity / Required Bits](#form-12-or-monotonicity-required-bits)
  - [Problem 1 — 1842B — Tenzing and Books](#problem-1-1842b-tenzing-and-books)
  - [Problem 2 — 1547D — Co-growing Sequence](#problem-2-1547d-co-growing-sequence)
  - [Problem 3 — 1042B — Vitamins](#problem-3-1042b-vitamins)
  - [Problem 4 — 1829C — Mr. Perfectly Fine](#problem-4-1829c-mr-perfectly-fine)
- [Form 13 — AND Monotonicity / Maximal AND](#form-13-and-monotonicity-maximal-and)
  - [Problem 1 — 1669H — Maximal AND](#problem-1-1669h-maximal-and)
  - [Problem 2 — 1514B — AND 0, Sum Big](#problem-2-1514b-and-0-sum-big)
  - [Problem 3 — 1991B — AND Reconstruction](#problem-3-1991b-and-reconstruction)
  - [Problem 4 — 1903B — StORage room](#problem-4-1903b-storage-room)
- [Form 14 — Bit Frequency / Majority Per Bit](#form-14-bit-frequency-majority-per-bit)
  - [Problem 1 — 1625A — Ancient Civilization](#problem-1-1625a-ancient-civilization)
  - [Problem 2 — 1669H — Maximal AND](#problem-2-1669h-maximal-and)
  - [Problem 3 — 1514B — AND 0, Sum Big](#problem-3-1514b-and-0-sum-big)
  - [Problem 4 — 1367B — Even Array](#problem-4-1367b-even-array)
- [Form 15 — Bit-by-Bit Constraint Construction](#form-15-bit-by-bit-constraint-construction)
  - [Problem 1 — 1903B — StORage room](#problem-1-1903b-storage-room)
  - [Problem 2 — 1991B — AND Reconstruction](#problem-2-1991b-and-reconstruction)
  - [Problem 3 — 1547D — Co-growing Sequence](#problem-3-1547d-co-growing-sequence)
  - [Problem 4 — 1842B — Tenzing and Books](#problem-4-1842b-tenzing-and-books)
- [Form 16 — Pairwise XOR Contribution](#form-16-pairwise-xor-contribution)
  - [Problem 1 — 1421A — XORwice](#problem-1-1421a-xorwice)
  - [Problem 2 — 1918C — XOR-distance](#problem-2-1918c-xor-distance)
  - [Problem 3 — 1698A — XOR Mixup](#problem-3-1698a-xor-mixup)
  - [Problem 4 — 1516B — AGAGA XOOORRR](#problem-4-1516b-agaga-xooorrr)
- [Form 17 — Pairwise AND / OR Contribution](#form-17-pairwise-and-or-contribution)
  - [Problem 1 — 1514B — AND 0, Sum Big](#problem-1-1514b-and-0-sum-big)
  - [Problem 2 — 1669H — Maximal AND](#problem-2-1669h-maximal-and)
  - [Problem 3 — 1903B — StORage room](#problem-3-1903b-storage-room)
  - [Problem 4 — 1991B — AND Reconstruction](#problem-4-1991b-and-reconstruction)
- [Form 18 — Conservation / Operation Decoding](#form-18-conservation-operation-decoding)
  - [Problem 1 — 1805A — We Need the Zero](#problem-1-1805a-we-need-the-zero)
  - [Problem 2 — 1516B — AGAGA XOOORRR](#problem-2-1516b-agaga-xooorrr)
  - [Problem 3 — 1698A — XOR Mixup](#problem-3-1698a-xor-mixup)
  - [Problem 4 — 1547D — Co-growing Sequence](#problem-4-1547d-co-growing-sequence)
- [Form 19 — Highest Bit -> Lowest Bit Greedy](#form-19-highest-bit-lowest-bit-greedy)
  - [Problem 1 — 1669H — Maximal AND](#problem-1-1669h-maximal-and)
  - [Problem 2 — 1918C — XOR-distance](#problem-2-1918c-xor-distance)
  - [Problem 3 — 1527A — And Then There Were K](#problem-3-1527a-and-then-there-were-k)
  - [Problem 4 — 1420B — Rock and Lever](#problem-4-1420b-rock-and-lever)
- [Form 20 — Prefix Counts of Bits](#form-20-prefix-counts-of-bits)
  - [Problem 1 — 1872E — Data Structures Fan](#problem-1-1872e-data-structures-fan)
  - [Problem 2 — 1625A — Ancient Civilization](#problem-2-1625a-ancient-civilization)
  - [Problem 3 — 1669H — Maximal AND](#problem-3-1669h-maximal-and)
  - [Problem 4 — 1516B — AGAGA XOOORRR](#problem-4-1516b-agaga-xooorrr)
- [Form 21 — Common Binary Prefix / Range AND](#form-21-common-binary-prefix-range-and)
  - [Problem 1 — 1527A — And Then There Were K](#problem-1-1527a-and-then-there-were-k)
  - [Problem 2 — 1420B — Rock and Lever](#problem-2-1420b-rock-and-lever)
  - [Problem 3 — 1669H — Maximal AND](#problem-3-1669h-maximal-and)
  - [Problem 4 — 1475A — Odd Divisor](#problem-4-1475a-odd-divisor)
- [Form 22 — Complement Within Fixed Width](#form-22-complement-within-fixed-width)
  - [Problem 1 — 1926D — Vlad and Division](#problem-1-1926d-vlad-and-division)
  - [Problem 2 — 1421A — XORwice](#problem-2-1421a-xorwice)
  - [Problem 3 — 1918C — XOR-distance](#problem-3-1918c-xor-distance)
  - [Problem 4 — 1362B — Johnny and His Hobbies](#problem-4-1362b-johnny-and-his-hobbies)
- [Form 23 — Subset Enumeration](#form-23-subset-enumeration)
  - [Problem 1 — 1097B — Petr and a Combination Lock](#problem-1-1097b-petr-and-a-combination-lock)
  - [Problem 2 — 550B — Preparing Olympiad](#problem-2-550b-preparing-olympiad)
  - [Problem 3 — 1042B — Vitamins](#problem-3-1042b-vitamins)
  - [Problem 4 — 1829C — Mr. Perfectly Fine](#problem-4-1829c-mr-perfectly-fine)
- [Form 24 — Bitmask as State](#form-24-bitmask-as-state)
  - [Problem 1 — 1042B — Vitamins](#problem-1-1042b-vitamins)
  - [Problem 2 — 1829C — Mr. Perfectly Fine](#problem-2-1829c-mr-perfectly-fine)
  - [Problem 3 — 1097B — Petr and a Combination Lock](#problem-3-1097b-petr-and-a-combination-lock)
  - [Problem 4 — 550B — Preparing Olympiad](#problem-4-550b-preparing-olympiad)
- [Form 25 — Submask Enumeration](#form-25-submask-enumeration)
  - [Problem 1 — 1042B — Vitamins](#problem-1-1042b-vitamins)
  - [Problem 2 — 1829C — Mr. Perfectly Fine](#problem-2-1829c-mr-perfectly-fine)
  - [Problem 3 — 550B — Preparing Olympiad](#problem-3-550b-preparing-olympiad)
  - [Problem 4 — 1097B — Petr and a Combination Lock](#problem-4-1097b-petr-and-a-combination-lock)
- [Form 26 — Bitmask DP](#form-26-bitmask-dp)
  - [Problem 1 — 1042B — Vitamins](#problem-1-1042b-vitamins)
  - [Problem 2 — 1829C — Mr. Perfectly Fine](#problem-2-1829c-mr-perfectly-fine)
  - [Problem 3 — 1097B — Petr and a Combination Lock](#problem-3-1097b-petr-and-a-combination-lock)
  - [Problem 4 — 550B — Preparing Olympiad](#problem-4-550b-preparing-olympiad)

# Form 1 — Parity / LSB

## Problem 1 — [1367B — Even Array](https://codeforces.com/problemset/problem/1367/B)

### How this form applies

The condition is parity only. Compare the least-significant bit of the index and value. A swap fixes one odd-at-even mismatch together with one even-at-odd mismatch.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
The condition is parity only. Compare the least-significant bit of the index and value. A swap fixes one odd-at-even mismatch together with one even-at-odd mismatch.

NOW FOLLOW THE BITS
-------------------
Example: a = [3,2,7,6]

index      0    1    2    3
index LSB  0    1    0    1
value      3    2    7    6
binary    11   10  111  110
value LSB  1    0    1    0
            X    X    X    X

odd value at even index  = 2
Even value at odd index  = 2
Each swap consumes one of each -> answer = 2.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);
 int T; cin>>T; while(T--){int n;cin>>n;int x,y=0,z=0;
  for(int i=0;i<n;i++){cin>>x;if((x&1)!=(i&1)){if(x&1)y++;else z++;}}
  cout<<(y==z?y:-1)<<'\n';
 }}
```


---

## Problem 2 — [1475A — Odd Divisor](https://codeforces.com/problemset/problem/1475/A)

### How this form applies

A number has no odd divisor greater than 1 exactly when it is a power of two. Use `n & (n-1)` to test that form.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
A number has no odd divisor greater than 1 exactly when it is a power of two. Use n & (n-1) to test that form.

NOW FOLLOW THE BITS
-------------------
n = 8
8   = 1000
7   = 0111
&     0000 -> power of two -> NO

n = 12
12  = 1100
11  = 1011
&     1000 -> more than one set bit -> has odd factor 3 -> YES.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);
 int T;cin>>T;while(T--){long long n;cin>>n;
  cout<<((n&(n-1))?"YES":"NO")<<'\n';
 }}
```


---

## Problem 3 — [1688B — Patchouli’s Magical Talisman](https://codeforces.com/problemset/problem/1688/B)

### How this form applies

Parity is the first split. If at least one number is odd, every even number can be combined/converted with one operation in the optimal process. If all are even, choose the element with the fewest trailing zero bits to become odd first, then handle the rest.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Parity is the first split. If at least one number is odd, every even number can be combined/converted with one operation in the optimal process. If all are even, choose the element with the fewest trailing zero bits to become odd first, then handle the rest.

NOW FOLLOW THE BITS
-------------------
a=[4,8,12]
4 =0100 -> trailing zeros 2
8 =1000 -> trailing zeros 3
12=1100 -> trailing zeros 2
All even.
Pick 4 (or12): need 2 divisions-by-2 style steps to expose odd bit.
Then remaining n-1=2 elements each need one combining step.
answer=2+2=4.

If a=[3,4,8], odd already exists -> answer = number of evens =2.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;vector<int>a(n);int even=0,mn=31;for(int&x:a){cin>>x;if(x%2==0){even++;mn=min(mn,__builtin_ctz(x));}}if(even<n)cout<<even<<'\n';else cout<<n-1+mn<<'\n';}}
```


---

## Problem 4 — [1805A — We Need the Zero](https://codeforces.com/problemset/problem/1805/A)

### How this form applies

Let S be XOR of the array. After XORing every element with x, total becomes `S ^ x` if n is odd and `S` if n is even.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Let S be XOR of the array. After XORing every element with x, total becomes S ^ x if n is odd and S if n is even.

NOW FOLLOW THE BITS
-------------------
a=[1,2,5]
1=001
2=010
5=101
S=110=6
n=3 odd -> choose x=S=110

001^110=111
010^110=100
101^110=011
111^100^011=000.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);
 int T;cin>>T;while(T--){int n;cin>>n;int xr=0,x;for(int i=0;i<n;i++){cin>>x;xr^=x;}
  if(n&1) cout<<xr<<'\n'; else cout<<(xr==0?0:-1)<<'\n';
 }}
```


# Form 2 — Power of Two / Remove Lowest Set Bit

## Problem 1 — [1475A — Odd Divisor](https://codeforces.com/problemset/problem/1475/A)

### How this form applies

A number has no odd divisor greater than 1 exactly when it is a power of two. Use `n & (n-1)` to test that form.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
A number has no odd divisor greater than 1 exactly when it is a power of two. Use n & (n-1) to test that form.

NOW FOLLOW THE BITS
-------------------
n = 8
8   = 1000
7   = 0111
&     0000 -> power of two -> NO

n = 12
12  = 1100
11  = 1011
&     1000 -> more than one set bit -> has odd factor 3 -> YES.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);
 int T;cin>>T;while(T--){long long n;cin>>n;
  cout<<((n&(n-1))?"YES":"NO")<<'\n';
 }}
```


---

## Problem 2 — [1527A — And Then There Were K](https://codeforces.com/problemset/problem/1527/A)

### How this form applies

For maximum `k<n` with `n & (n-1) & ... & k = 0`, find the highest power of two not exceeding `n`; answer is that power minus one.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
For maximum k<n with n & (n-1) & ... & k = 0, find the highest power of two not exceeding n; answer is that power minus one.

NOW FOLLOW THE BITS
-------------------
n = 10 = 1010
highest power of 2 <= n = 8 = 1000
answer = 8-1 = 7 = 0111

The interval 10,9,8,7 contains values that clear every bit in the cumulative AND.
The boundary is determined by the MSB of n.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);
 int T;cin>>T;while(T--){long long n;cin>>n;long long p=1;
  while((p<<1)<=n)p<<=1;
  cout<<p-1<<'\n';
 }}
```


---

## Problem 3 — [1567B — MEXor Mixup](https://codeforces.com/problemset/problem/1567/B)

### How this form applies

The XOR of `0..a-1` is obtained from the 4-cycle. If it already equals b, length a works; otherwise append one number, except when that number equals a, which would change MEX and requires two.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
The XOR of 0..a-1 is obtained from the 4-cycle. If it already equals b, length a works; otherwise append one number, except when that number equals a, which would change MEX and requires two.

NOW FOLLOW THE BITS
-------------------
Suppose a=4, b=2.
Need array containing 0,1,2,3.
XOR(0..3)=0 (cycle).
Need extra value d = 0^2 = 2.
But 2<a, already allowed; append 2 -> XOR becomes 2 and MEX remains 4.
Answer a+1=5.

If d==a, appending a would make MEX > a, so use two extra values -> a+2.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
long long px(long long n){if(n<0)return 0;switch(n&3){case 0:return n;case 1:return 1;case 2:return n+1;default:return 0;}}
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){long long a,b;cin>>a>>b;long long x=px(a-1);if(x==b)cout<<a;else if((x^b)==a)cout<<a+2;else cout<<a+1;cout<<'\n';}}
```


---

## Problem 4 — [1909B — Make Almost Equal With Mod](https://codeforces.com/problemset/problem/1909/B)

### How this form applies

Try powers of two. `x mod 2^k` is exactly the last k bits. Increase suffix length until exactly two distinct suffixes occur.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Try powers of two. x mod 2^k is exactly the last k bits. Increase suffix length until exactly two distinct suffixes occur.

NOW FOLLOW THE BITS
-------------------
a=[8,14,22,30]
8 =001000
14=001110
22=010110
30=011110

mod2 -> last1: 0,0,0,0 -> {0}
mod4 -> last2: 00,10,10,10 -> {0,2}
Exactly two -> answer 4.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;vector<long long>a(n);for(auto&x:a)cin>>x;for(int b=1;b<=61;b++){long long m=1LL<<b;set<long long>s;for(auto x:a)s.insert(x%m);if(s.size()==2){cout<<m<<'\n';break;}}}}
```


# Form 3 — Kth-bit Masking

## Problem 1 — [1669H — Maximal AND](https://codeforces.com/problemset/problem/1669/H)

### How this form applies

To put bit b into the final AND, every element must have bit b=1. Cost is the number of elements missing that bit. Spend budget greedily from bit 30 down to 0.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
To put bit b into the final AND, every element must have bit b=1. Cost is the number of elements missing that bit. Spend budget greedily from bit 30 down to 0.

NOW FOLLOW THE BITS
-------------------
a=[2,1,1], k=2
2=10
1=01
1=01

bit1 column: 1,0,0 -> need 2 operations
k=2 -> buy bit1
array can become 10,11,11
AND =10 =2
No budget remains.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;long long k;cin>>n>>k;vector<int>a(n);for(int&x:a)cin>>x;long long ans=0;for(int b=30;b>=0;b--){long long need=0;for(int x:a)if(!(x&(1<<b)))need++;if(need<=k){k-=need;ans|=1LL<<b;}}cout<<ans<<'\n';}}
```


---

## Problem 2 — [1547D — Co-growing Sequence](https://codeforces.com/problemset/problem/1547/D)

### How this form applies

Choose y[i] so `(x[i] xor y[i]) & (x[i+1] xor y[i+1]) = x[i] xor y[i]`. This means every 1-bit in previous transformed value must also be 1 in current transformed value. Add exactly the missing bits.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Choose y[i] so (x[i] xor y[i]) & (x[i+1] xor y[i+1]) = x[i] xor y[i]. This means every 1-bit in previous transformed value must also be 1 in current transformed value. Add exactly the missing bits.

NOW FOLLOW THE BITS
-------------------
previous transformed p=1011
current x=0010
Need current transformed z to contain all bits of p.
Missing bits = p & ~x
1011
~0010 (within width) -> ...1101
AND ->1001
Choose y=1001
x^y=0010^1001=1011
Now p & z =1011 = p.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;vector<int>x(n),y(n);for(int&i:x)cin>>i;int prev=x[0];for(int i=1;i<n;i++){y[i]=prev & (~x[i]);prev=x[i]^y[i];}for(int v:y)cout<<v<<' ';cout<<'\n';}}
```


---

## Problem 3 — [1926D — Vlad and Division](https://codeforces.com/problemset/problem/1926/D)

### How this form applies

Two values can pair when their lowest 31 bits are opposite. Partner is `x xor ((1<<31)-1)`. Greedily match complements.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Two values can pair when their lowest 31 bits are opposite. Partner is x xor ((1<<31)-1). Greedily match complements.

NOW FOLLOW THE BITS
-------------------
5-bit illustration:
x=10110
mask=11111
partner=01001

10110
01001
-----
every column is 1/0 or 0/1.
If partner already waits, pair them; otherwise start a new group with x.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;const long long M=(1LL<<31)-1;while(T--){int n;cin>>n;unordered_map<long long,int>cnt;int ans=0;while(n--){long long x;cin>>x,y=x^M;if(cnt[y])cnt[y]--;else{cnt[x]++;ans++;}}cout<<ans<<'\n';}}
```


---

## Problem 4 — [1042B — Vitamins](https://codeforces.com/problemset/problem/1042/B)

### How this form applies

Encode vitamins A,B,C as bits 0,1,2. Each juice is a mask. OR combines acquired vitamins; DP over 8 masks gives minimum cost.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Encode vitamins A,B,C as bits 0,1,2. Each juice is a mask. OR combines acquired vitamins; DP over 8 masks gives minimum cost.

NOW FOLLOW THE BITS
-------------------
A=001,B=010,C=100
juice AB ->011 cost5
juice C  ->100 cost3
state 000 --buy AB-->011
011 |100 =111
cost=8, all vitamins covered.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){int n;cin>>n;const int INF=1e9;vector<int>dp(8,INF);dp[0]=0;while(n--){int c;string s;cin>>c>>s;int m=0;for(char ch:s)m|=1<<(ch-'A');auto ndp=dp;for(int st=0;st<8;st++)ndp[st|m]=min(ndp[st|m],dp[st]+c);dp=ndp;}cout<<(dp[7]>=INF?-1:dp[7])<<'\n';}
```


# Form 4 — XOR Cancellation

## Problem 1 — [1698A — XOR Mixup](https://codeforces.com/problemset/problem/1698/A)

### How this form applies

The appended value is XOR of all original values. XORing the final array gives zero, so any element can be reconstructed as XOR of all the others; outputting XOR of all final values is also a valid answer under the construction.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
The appended value is XOR of all original values. XORing the final array gives zero, so any element can be reconstructed as XOR of all the others; outputting XOR of all final values is also a valid answer under the construction.

NOW FOLLOW THE BITS
-------------------
Example final array: [4,3,2,5]
4=100
3=011
2=010
5=101
XOR:
100 ^ 011 = 111
111 ^ 010 = 101
101 ^ 101 = 000

The full XOR is 0 because original-XOR x appears once in addition to the originals whose XOR is x. A valid x can be obtained using the problem's guaranteed construction; the standard implementation XORs the required subset relation.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);
 int T;cin>>T;while(T--){int n;cin>>n;vector<int>a(n);for(int&x:a)cin>>x;
  // Any a[i] can serve as x when XOR of all final elements is 0.
  cout<<a[0]<<'\n';
 }}
```


---

## Problem 2 — [1805A — We Need the Zero](https://codeforces.com/problemset/problem/1805/A)

### How this form applies

Let S be XOR of the array. After XORing every element with x, total becomes `S ^ x` if n is odd and `S` if n is even.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Let S be XOR of the array. After XORing every element with x, total becomes S ^ x if n is odd and S if n is even.

NOW FOLLOW THE BITS
-------------------
a=[1,2,5]
1=001
2=010
5=101
S=110=6
n=3 odd -> choose x=S=110

001^110=111
010^110=100
101^110=011
111^100^011=000.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);
 int T;cin>>T;while(T--){int n;cin>>n;int xr=0,x;for(int i=0;i<n;i++){cin>>x;xr^=x;}
  if(n&1) cout<<xr<<'\n'; else cout<<(xr==0?0:-1)<<'\n';
 }}
```


---

## Problem 3 — [1516B — AGAGA XOOORRR](https://codeforces.com/problemset/problem/1516/B)

### How this form applies

Partition into at least two segments with equal XOR. If total XOR is 0, two parts can work. Otherwise, scan for two prefixes/segments whose XOR equals total XOR, giving three equal-XOR parts.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Partition into at least two segments with equal XOR. If total XOR is 0, two parts can work. Otherwise, scan for two prefixes/segments whose XOR equals total XOR, giving three equal-XOR parts.

NOW FOLLOW THE BITS
-------------------
a=[1,2,3]
1=01,2=10,3=11
total=01^10^11=00 -> YES immediately.

If total S!=0, look for:
segment1 XOR=S
segment2 XOR=S
then remaining XOR = S^S^S = S,
so three segments have equal XOR.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;vector<int>a(n);int S=0;for(int&x:a){cin>>x;S^=x;}if(S==0){cout<<"YES\n";continue;}int cur=0,cnt=0;for(int i=0;i<n-1;i++){cur^=a[i];if(cur==S){cnt++;cur=0;}}cout<<(cnt>=2?"YES":"NO")<<'\n';}}
```


---

## Problem 4 — [1872E — Data Structures Fan](https://codeforces.com/problemset/problem/1872/E)

### How this form applies

Maintain XOR of the two groups defined by a binary string. Flipping a whole substring swaps membership there; prefix XOR gives XOR of the flipped values in O(1), and that value toggles both group XORs.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Maintain XOR of the two groups defined by a binary string. Flipping a whole substring swaps membership there; prefix XOR gives XOR of the flipped values in O(1), and that value toggles both group XORs.

NOW FOLLOW THE BITS
-------------------
a=[1,2,3], s=010
binary: 1=01,2=10,3=11
Group0 XOR = 1^3 = 01^11 = 10 =2
Group1 XOR = 2 =10

Flip [1,2] (0-based values 2,3): segment XOR=2^3=01
Both group XORs ^=01:
g0:10^01=11=3
g1:10^01=11=3
Membership swapped only inside range.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;vector<long long>a(n),p(n+1);for(int i=0;i<n;i++){cin>>a[i];p[i+1]=p[i]^a[i];}string s;cin>>s;long long g[2]={0,0};for(int i=0;i<n;i++)g[s[i]-'0']^=a[i];int q;cin>>q;while(q--){int tp;cin>>tp;if(tp==1){int l,r;cin>>l>>r;long long x=p[r]^p[l-1];g[0]^=x;g[1]^=x;}else{int b;cin>>b;cout<<g[b]<<' ';}}cout<<'\n';}}
```


# Form 5 — Global XOR / Solve for X

## Problem 1 — [1805A — We Need the Zero](https://codeforces.com/problemset/problem/1805/A)

### How this form applies

Let S be XOR of the array. After XORing every element with x, total becomes `S ^ x` if n is odd and `S` if n is even.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Let S be XOR of the array. After XORing every element with x, total becomes S ^ x if n is odd and S if n is even.

NOW FOLLOW THE BITS
-------------------
a=[1,2,5]
1=001
2=010
5=101
S=110=6
n=3 odd -> choose x=S=110

001^110=111
010^110=100
101^110=011
111^100^011=000.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);
 int T;cin>>T;while(T--){int n;cin>>n;int xr=0,x;for(int i=0;i<n;i++){cin>>x;xr^=x;}
  if(n&1) cout<<xr<<'\n'; else cout<<(xr==0?0:-1)<<'\n';
 }}
```


---

## Problem 2 — [1698A — XOR Mixup](https://codeforces.com/problemset/problem/1698/A)

### How this form applies

The appended value is XOR of all original values. XORing the final array gives zero, so any element can be reconstructed as XOR of all the others; outputting XOR of all final values is also a valid answer under the construction.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
The appended value is XOR of all original values. XORing the final array gives zero, so any element can be reconstructed as XOR of all the others; outputting XOR of all final values is also a valid answer under the construction.

NOW FOLLOW THE BITS
-------------------
Example final array: [4,3,2,5]
4=100
3=011
2=010
5=101
XOR:
100 ^ 011 = 111
111 ^ 010 = 101
101 ^ 101 = 000

The full XOR is 0 because original-XOR x appears once in addition to the originals whose XOR is x. A valid x can be obtained using the problem's guaranteed construction; the standard implementation XORs the required subset relation.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);
 int T;cin>>T;while(T--){int n;cin>>n;vector<int>a(n);for(int&x:a)cin>>x;
  // Any a[i] can serve as x when XOR of all final elements is 0.
  cout<<a[0]<<'\n';
 }}
```


---

## Problem 3 — [1567B — MEXor Mixup](https://codeforces.com/problemset/problem/1567/B)

### How this form applies

The XOR of `0..a-1` is obtained from the 4-cycle. If it already equals b, length a works; otherwise append one number, except when that number equals a, which would change MEX and requires two.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
The XOR of 0..a-1 is obtained from the 4-cycle. If it already equals b, length a works; otherwise append one number, except when that number equals a, which would change MEX and requires two.

NOW FOLLOW THE BITS
-------------------
Suppose a=4, b=2.
Need array containing 0,1,2,3.
XOR(0..3)=0 (cycle).
Need extra value d = 0^2 = 2.
But 2<a, already allowed; append 2 -> XOR becomes 2 and MEX remains 4.
Answer a+1=5.

If d==a, appending a would make MEX > a, so use two extra values -> a+2.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
long long px(long long n){if(n<0)return 0;switch(n&3){case 0:return n;case 1:return 1;case 2:return n+1;default:return 0;}}
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){long long a,b;cin>>a>>b;long long x=px(a-1);if(x==b)cout<<a;else if((x^b)==a)cout<<a+2;else cout<<a+1;cout<<'\n';}}
```


---

## Problem 4 — [1516B — AGAGA XOOORRR](https://codeforces.com/problemset/problem/1516/B)

### How this form applies

Partition into at least two segments with equal XOR. If total XOR is 0, two parts can work. Otherwise, scan for two prefixes/segments whose XOR equals total XOR, giving three equal-XOR parts.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Partition into at least two segments with equal XOR. If total XOR is 0, two parts can work. Otherwise, scan for two prefixes/segments whose XOR equals total XOR, giving three equal-XOR parts.

NOW FOLLOW THE BITS
-------------------
a=[1,2,3]
1=01,2=10,3=11
total=01^10^11=00 -> YES immediately.

If total S!=0, look for:
segment1 XOR=S
segment2 XOR=S
then remaining XOR = S^S^S = S,
so three segments have equal XOR.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;vector<int>a(n);int S=0;for(int&x:a){cin>>x;S^=x;}if(S==0){cout<<"YES\n";continue;}int cur=0,cnt=0;for(int i=0;i<n-1;i++){cur^=a[i];if(cur==S){cnt++;cur=0;}}cout<<(cnt>=2?"YES":"NO")<<'\n';}}
```


# Form 6 — XOR 1..N Cycle

## Problem 1 — [1567B — MEXor Mixup](https://codeforces.com/problemset/problem/1567/B)

### How this form applies

The XOR of `0..a-1` is obtained from the 4-cycle. If it already equals b, length a works; otherwise append one number, except when that number equals a, which would change MEX and requires two.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
The XOR of 0..a-1 is obtained from the 4-cycle. If it already equals b, length a works; otherwise append one number, except when that number equals a, which would change MEX and requires two.

NOW FOLLOW THE BITS
-------------------
Suppose a=4, b=2.
Need array containing 0,1,2,3.
XOR(0..3)=0 (cycle).
Need extra value d = 0^2 = 2.
But 2<a, already allowed; append 2 -> XOR becomes 2 and MEX remains 4.
Answer a+1=5.

If d==a, appending a would make MEX > a, so use two extra values -> a+2.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
long long px(long long n){if(n<0)return 0;switch(n&3){case 0:return n;case 1:return 1;case 2:return n+1;default:return 0;}}
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){long long a,b;cin>>a>>b;long long x=px(a-1);if(x==b)cout<<a;else if((x^b)==a)cout<<a+2;else cout<<a+1;cout<<'\n';}}
```


---

## Problem 2 — [1527A — And Then There Were K](https://codeforces.com/problemset/problem/1527/A)

### How this form applies

For maximum `k<n` with `n & (n-1) & ... & k = 0`, find the highest power of two not exceeding `n`; answer is that power minus one.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
For maximum k<n with n & (n-1) & ... & k = 0, find the highest power of two not exceeding n; answer is that power minus one.

NOW FOLLOW THE BITS
-------------------
n = 10 = 1010
highest power of 2 <= n = 8 = 1000
answer = 8-1 = 7 = 0111

The interval 10,9,8,7 contains values that clear every bit in the cumulative AND.
The boundary is determined by the MSB of n.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);
 int T;cin>>T;while(T--){long long n;cin>>n;long long p=1;
  while((p<<1)<=n)p<<=1;
  cout<<p-1<<'\n';
 }}
```


---

## Problem 3 — [1909B — Make Almost Equal With Mod](https://codeforces.com/problemset/problem/1909/B)

### How this form applies

Try powers of two. `x mod 2^k` is exactly the last k bits. Increase suffix length until exactly two distinct suffixes occur.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Try powers of two. x mod 2^k is exactly the last k bits. Increase suffix length until exactly two distinct suffixes occur.

NOW FOLLOW THE BITS
-------------------
a=[8,14,22,30]
8 =001000
14=001110
22=010110
30=011110

mod2 -> last1: 0,0,0,0 -> {0}
mod4 -> last2: 00,10,10,10 -> {0,2}
Exactly two -> answer 4.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;vector<long long>a(n);for(auto&x:a)cin>>x;for(int b=1;b<=61;b++){long long m=1LL<<b;set<long long>s;for(auto x:a)s.insert(x%m);if(s.size()==2){cout<<m<<'\n';break;}}}}
```


---

## Problem 4 — [1097B — Petr and a Combination Lock](https://codeforces.com/problemset/problem/1097/B)

### How this form applies

Each angle has two choices: + or -. Encode the choice for angle i in bit i of a mask and enumerate all 2^n assignments.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Each angle has two choices: + or -. Encode the choice for angle i in bit i of a mask and enumerate all 2^n assignments.

NOW FOLLOW THE BITS
-------------------
n=3, angles=[10,20,30]
mask=101
bit0=1 -> +10
bit1=0 -> -20
bit2=1 -> +30
sum=20, not divisible by360.
Try every mask 000..111 until one gives sum%360=0.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){int n;cin>>n;vector<int>a(n);for(int&x:a)cin>>x;for(int m=0;m<(1<<n);m++){int s=0;for(int i=0;i<n;i++)s+=(m>>i&1)?a[i]:-a[i];if((s%360+360)%360==0){cout<<"YES\n";return 0;}}cout<<"NO\n";}
```


# Form 7 — Prefix XOR

## Problem 1 — [1872E — Data Structures Fan](https://codeforces.com/problemset/problem/1872/E)

### How this form applies

Maintain XOR of the two groups defined by a binary string. Flipping a whole substring swaps membership there; prefix XOR gives XOR of the flipped values in O(1), and that value toggles both group XORs.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Maintain XOR of the two groups defined by a binary string. Flipping a whole substring swaps membership there; prefix XOR gives XOR of the flipped values in O(1), and that value toggles both group XORs.

NOW FOLLOW THE BITS
-------------------
a=[1,2,3], s=010
binary: 1=01,2=10,3=11
Group0 XOR = 1^3 = 01^11 = 10 =2
Group1 XOR = 2 =10

Flip [1,2] (0-based values 2,3): segment XOR=2^3=01
Both group XORs ^=01:
g0:10^01=11=3
g1:10^01=11=3
Membership swapped only inside range.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;vector<long long>a(n),p(n+1);for(int i=0;i<n;i++){cin>>a[i];p[i+1]=p[i]^a[i];}string s;cin>>s;long long g[2]={0,0};for(int i=0;i<n;i++)g[s[i]-'0']^=a[i];int q;cin>>q;while(q--){int tp;cin>>tp;if(tp==1){int l,r;cin>>l>>r;long long x=p[r]^p[l-1];g[0]^=x;g[1]^=x;}else{int b;cin>>b;cout<<g[b]<<' ';}}cout<<'\n';}}
```


---

## Problem 2 — [1516B — AGAGA XOOORRR](https://codeforces.com/problemset/problem/1516/B)

### How this form applies

Partition into at least two segments with equal XOR. If total XOR is 0, two parts can work. Otherwise, scan for two prefixes/segments whose XOR equals total XOR, giving three equal-XOR parts.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Partition into at least two segments with equal XOR. If total XOR is 0, two parts can work. Otherwise, scan for two prefixes/segments whose XOR equals total XOR, giving three equal-XOR parts.

NOW FOLLOW THE BITS
-------------------
a=[1,2,3]
1=01,2=10,3=11
total=01^10^11=00 -> YES immediately.

If total S!=0, look for:
segment1 XOR=S
segment2 XOR=S
then remaining XOR = S^S^S = S,
so three segments have equal XOR.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;vector<int>a(n);int S=0;for(int&x:a){cin>>x;S^=x;}if(S==0){cout<<"YES\n";continue;}int cur=0,cnt=0;for(int i=0;i<n-1;i++){cur^=a[i];if(cur==S){cnt++;cur=0;}}cout<<(cnt>=2?"YES":"NO")<<'\n';}}
```


---

## Problem 3 — [1698A — XOR Mixup](https://codeforces.com/problemset/problem/1698/A)

### How this form applies

The appended value is XOR of all original values. XORing the final array gives zero, so any element can be reconstructed as XOR of all the others; outputting XOR of all final values is also a valid answer under the construction.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
The appended value is XOR of all original values. XORing the final array gives zero, so any element can be reconstructed as XOR of all the others; outputting XOR of all final values is also a valid answer under the construction.

NOW FOLLOW THE BITS
-------------------
Example final array: [4,3,2,5]
4=100
3=011
2=010
5=101
XOR:
100 ^ 011 = 111
111 ^ 010 = 101
101 ^ 101 = 000

The full XOR is 0 because original-XOR x appears once in addition to the originals whose XOR is x. A valid x can be obtained using the problem's guaranteed construction; the standard implementation XORs the required subset relation.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);
 int T;cin>>T;while(T--){int n;cin>>n;vector<int>a(n);for(int&x:a)cin>>x;
  // Any a[i] can serve as x when XOR of all final elements is 0.
  cout<<a[0]<<'\n';
 }}
```


---

## Problem 4 — [1805A — We Need the Zero](https://codeforces.com/problemset/problem/1805/A)

### How this form applies

Let S be XOR of the array. After XORing every element with x, total becomes `S ^ x` if n is odd and `S` if n is even.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Let S be XOR of the array. After XORing every element with x, total becomes S ^ x if n is odd and S if n is even.

NOW FOLLOW THE BITS
-------------------
a=[1,2,5]
1=001
2=010
5=101
S=110=6
n=3 odd -> choose x=S=110

001^110=111
010^110=100
101^110=011
111^100^011=000.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);
 int T;cin>>T;while(T--){int n;cin>>n;int xr=0,x;for(int i=0;i<n;i++){cin>>x;xr^=x;}
  if(n&1) cout<<xr<<'\n'; else cout<<(xr==0?0:-1)<<'\n';
 }}
```


# Form 8 — XOR Difference Mask / Hamming Bits

## Problem 1 — [1918C — XOR-distance](https://codeforces.com/problemset/problem/1918/C)

### How this form applies

Only differing bits of a and b matter. Keep the highest differing bit unchanged (it determines which value is larger), then greedily flip lower differing bits when the corresponding x-bit fits within r and reduces the gap.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Only differing bits of a and b matter. Keep the highest differing bit unchanged (it determines which value is larger), then greedily flip lower differing bits when the corresponding x-bit fits within r and reduces the gap.

NOW FOLLOW THE BITS
-------------------
a=10=1010, b=3=0011. a>b.
Highest differing bit is bit3:
a:1, b:0. Do NOT flip it, otherwise ordering reverses with a huge change.
Lower differing bits:
bit0: a=0,b=1. Setting x bit0 swaps them and can reduce gap.

x starts 0000. If 0001<=r, choose it:
a^x=1011=11
b^x=0010=2
Here it increased gap, so direction of lower bits matters: flip only bits where the larger number has 1 and smaller has 0, subject to r. Process from high to low.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){long long a,b,r;cin>>a>>b>>r;if(a<b)swap(a,b);long long x=0;bool first=true;for(int i=62;i>=0;i--){long long bit=1LL<<i;bool A=a&bit,B=b&bit;if(A!=B){if(first){first=false;continue;}if(A&&!B && x+bit<=r)x+=bit;}}cout<<llabs((a^x)-(b^x))<<'\n';}}
```


---

## Problem 2 — [1362B — Johnny and His Hobbies](https://codeforces.com/problemset/problem/1362/B)

### How this form applies

Find positive x such that XORing every set element by x produces the same set. Try x and compare transformed multiset/set.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Find positive x such that XORing every set element by x produces the same set. Try x and compare transformed multiset/set.

NOW FOLLOW THE BITS
-------------------
S={1,2,3,4}
Try x=5 (101):
1=001 ^101=100=4
2=010 ^101=111=7 -> 7 not in S -> fail

For a candidate x, every bit where x has 1 toggles that column for all numbers. The transformed collection must exactly match the original collection.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;vector<int>a(n);set<int>s;for(int&v:a){cin>>v;s.insert(v);}int ans=-1;for(int x=1;x<1024;x++){set<int>t;for(int v:a)t.insert(v^x);if(t==s){ans=x;break;}}cout<<ans<<'\n';}}
```


---

## Problem 3 — [1421A — XORwice](https://codeforces.com/problemset/problem/1421/A)

### How this form applies

Minimize `(a xor x) + (b xor x)`. At every bit where both `a` and `b` are 1, both terms cannot simultaneously lose that contribution; where they differ, choose x so one side has the bit. The minimum equals `a xor b`.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Minimize (a xor x) + (b xor x). At every bit where both a and b are 1, both terms cannot simultaneously lose that contribution; where they differ, choose x so one side has the bit. The minimum equals a xor b.

NOW FOLLOW THE BITS
-------------------
a=5=101, b=3=011

a^b = 110 = 6

Try x = a&b = 001:
a^x = 100 = 4
b^x = 010 = 2
sum = 6

Bit columns:
bit2: 1/0 -> contribution 4
bit1: 0/1 -> contribution 2
bit0: 1/1 -> choose x=1 -> both become 0
Total = 6.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);
 int T;cin>>T;while(T--){long long a,b;cin>>a>>b;cout<<(a^b)<<'\n';}
}
```


---

## Problem 4 — [1547D — Co-growing Sequence](https://codeforces.com/problemset/problem/1547/D)

### How this form applies

Choose y[i] so `(x[i] xor y[i]) & (x[i+1] xor y[i+1]) = x[i] xor y[i]`. This means every 1-bit in previous transformed value must also be 1 in current transformed value. Add exactly the missing bits.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Choose y[i] so (x[i] xor y[i]) & (x[i+1] xor y[i+1]) = x[i] xor y[i]. This means every 1-bit in previous transformed value must also be 1 in current transformed value. Add exactly the missing bits.

NOW FOLLOW THE BITS
-------------------
previous transformed p=1011
current x=0010
Need current transformed z to contain all bits of p.
Missing bits = p & ~x
1011
~0010 (within width) -> ...1101
AND ->1001
Choose y=1001
x^y=0010^1001=1011
Now p & z =1011 = p.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;vector<int>x(n),y(n);for(int&i:x)cin>>i;int prev=x[0];for(int i=1;i<n;i++){y[i]=prev & (~x[i]);prev=x[i]^y[i];}for(int v:y)cout<<v<<' ';cout<<'\n';}}
```


# Form 9 — Modulo 2^k = Binary Suffix

## Problem 1 — [1909B — Make Almost Equal With Mod](https://codeforces.com/problemset/problem/1909/B)

### How this form applies

Try powers of two. `x mod 2^k` is exactly the last k bits. Increase suffix length until exactly two distinct suffixes occur.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Try powers of two. x mod 2^k is exactly the last k bits. Increase suffix length until exactly two distinct suffixes occur.

NOW FOLLOW THE BITS
-------------------
a=[8,14,22,30]
8 =001000
14=001110
22=010110
30=011110

mod2 -> last1: 0,0,0,0 -> {0}
mod4 -> last2: 00,10,10,10 -> {0,2}
Exactly two -> answer 4.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;vector<long long>a(n);for(auto&x:a)cin>>x;for(int b=1;b<=61;b++){long long m=1LL<<b;set<long long>s;for(auto x:a)s.insert(x%m);if(s.size()==2){cout<<m<<'\n';break;}}}}
```


---

## Problem 2 — [1475A — Odd Divisor](https://codeforces.com/problemset/problem/1475/A)

### How this form applies

A number has no odd divisor greater than 1 exactly when it is a power of two. Use `n & (n-1)` to test that form.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
A number has no odd divisor greater than 1 exactly when it is a power of two. Use n & (n-1) to test that form.

NOW FOLLOW THE BITS
-------------------
n = 8
8   = 1000
7   = 0111
&     0000 -> power of two -> NO

n = 12
12  = 1100
11  = 1011
&     1000 -> more than one set bit -> has odd factor 3 -> YES.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);
 int T;cin>>T;while(T--){long long n;cin>>n;
  cout<<((n&(n-1))?"YES":"NO")<<'\n';
 }}
```


---

## Problem 3 — [1527A — And Then There Were K](https://codeforces.com/problemset/problem/1527/A)

### How this form applies

For maximum `k<n` with `n & (n-1) & ... & k = 0`, find the highest power of two not exceeding `n`; answer is that power minus one.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
For maximum k<n with n & (n-1) & ... & k = 0, find the highest power of two not exceeding n; answer is that power minus one.

NOW FOLLOW THE BITS
-------------------
n = 10 = 1010
highest power of 2 <= n = 8 = 1000
answer = 8-1 = 7 = 0111

The interval 10,9,8,7 contains values that clear every bit in the cumulative AND.
The boundary is determined by the MSB of n.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);
 int T;cin>>T;while(T--){long long n;cin>>n;long long p=1;
  while((p<<1)<=n)p<<=1;
  cout<<p-1<<'\n';
 }}
```


---

## Problem 4 — [1926D — Vlad and Division](https://codeforces.com/problemset/problem/1926/D)

### How this form applies

Two values can pair when their lowest 31 bits are opposite. Partner is `x xor ((1<<31)-1)`. Greedily match complements.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Two values can pair when their lowest 31 bits are opposite. Partner is x xor ((1<<31)-1). Greedily match complements.

NOW FOLLOW THE BITS
-------------------
5-bit illustration:
x=10110
mask=11111
partner=01001

10110
01001
-----
every column is 1/0 or 0/1.
If partner already waits, pair them; otherwise start a new group with x.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;const long long M=(1LL<<31)-1;while(T--){int n;cin>>n;unordered_map<long long,int>cnt;int ans=0;while(n--){long long x;cin>>x,y=x^M;if(cnt[y])cnt[y]--;else{cnt[x]++;ans++;}}cout<<ans<<'\n';}}
```


# Form 10 — Highest Set Bit / MSB Grouping

## Problem 1 — [1420B — Rock and Lever](https://codeforces.com/problemset/problem/1420/B)

### How this form applies

A valid pair is characterized by the same highest set bit. Group numbers into MSB buckets and count pairs inside each bucket.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
A valid pair is characterized by the same highest set bit. Group numbers into MSB buckets and count pairs inside each bucket.

NOW FOLLOW THE BITS
-------------------
a=[4,5,6,9]
4=0100 -> MSB2
5=0101 -> MSB2
6=0110 -> MSB2
9=1001 -> MSB3

MSB2 bucket size=3 -> C(3,2)=3
MSB3 bucket size=1 -> 0
answer=3.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;long long c[32]={},ans=0;while(n--){int x;cin>>x;int b=31-__builtin_clz(x);c[b]++;}for(long long v:c)ans+=v*(v-1)/2;cout<<ans<<'\n';}}
```


---

## Problem 2 — [1527A — And Then There Were K](https://codeforces.com/problemset/problem/1527/A)

### How this form applies

For maximum `k<n` with `n & (n-1) & ... & k = 0`, find the highest power of two not exceeding `n`; answer is that power minus one.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
For maximum k<n with n & (n-1) & ... & k = 0, find the highest power of two not exceeding n; answer is that power minus one.

NOW FOLLOW THE BITS
-------------------
n = 10 = 1010
highest power of 2 <= n = 8 = 1000
answer = 8-1 = 7 = 0111

The interval 10,9,8,7 contains values that clear every bit in the cumulative AND.
The boundary is determined by the MSB of n.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);
 int T;cin>>T;while(T--){long long n;cin>>n;long long p=1;
  while((p<<1)<=n)p<<=1;
  cout<<p-1<<'\n';
 }}
```


---

## Problem 3 — [1918C — XOR-distance](https://codeforces.com/problemset/problem/1918/C)

### How this form applies

Only differing bits of a and b matter. Keep the highest differing bit unchanged (it determines which value is larger), then greedily flip lower differing bits when the corresponding x-bit fits within r and reduces the gap.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Only differing bits of a and b matter. Keep the highest differing bit unchanged (it determines which value is larger), then greedily flip lower differing bits when the corresponding x-bit fits within r and reduces the gap.

NOW FOLLOW THE BITS
-------------------
a=10=1010, b=3=0011. a>b.
Highest differing bit is bit3:
a:1, b:0. Do NOT flip it, otherwise ordering reverses with a huge change.
Lower differing bits:
bit0: a=0,b=1. Setting x bit0 swaps them and can reduce gap.

x starts 0000. If 0001<=r, choose it:
a^x=1011=11
b^x=0010=2
Here it increased gap, so direction of lower bits matters: flip only bits where the larger number has 1 and smaller has 0, subject to r. Process from high to low.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){long long a,b,r;cin>>a>>b>>r;if(a<b)swap(a,b);long long x=0;bool first=true;for(int i=62;i>=0;i--){long long bit=1LL<<i;bool A=a&bit,B=b&bit;if(A!=B){if(first){first=false;continue;}if(A&&!B && x+bit<=r)x+=bit;}}cout<<llabs((a^x)-(b^x))<<'\n';}}
```


---

## Problem 4 — [1669H — Maximal AND](https://codeforces.com/problemset/problem/1669/H)

### How this form applies

To put bit b into the final AND, every element must have bit b=1. Cost is the number of elements missing that bit. Spend budget greedily from bit 30 down to 0.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
To put bit b into the final AND, every element must have bit b=1. Cost is the number of elements missing that bit. Spend budget greedily from bit 30 down to 0.

NOW FOLLOW THE BITS
-------------------
a=[2,1,1], k=2
2=10
1=01
1=01

bit1 column: 1,0,0 -> need 2 operations
k=2 -> buy bit1
array can become 10,11,11
AND =10 =2
No budget remains.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;long long k;cin>>n>>k;vector<int>a(n);for(int&x:a)cin>>x;long long ans=0;for(int b=30;b>=0;b--){long long need=0;for(int x:a)if(!(x&(1<<b)))need++;if(need<=k){k-=need;ans|=1LL<<b;}}cout<<ans<<'\n';}}
```


# Form 11 — Lowest Set Bit / 2-adic Structure

## Problem 1 — [1475A — Odd Divisor](https://codeforces.com/problemset/problem/1475/A)

### How this form applies

A number has no odd divisor greater than 1 exactly when it is a power of two. Use `n & (n-1)` to test that form.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
A number has no odd divisor greater than 1 exactly when it is a power of two. Use n & (n-1) to test that form.

NOW FOLLOW THE BITS
-------------------
n = 8
8   = 1000
7   = 0111
&     0000 -> power of two -> NO

n = 12
12  = 1100
11  = 1011
&     1000 -> more than one set bit -> has odd factor 3 -> YES.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);
 int T;cin>>T;while(T--){long long n;cin>>n;
  cout<<((n&(n-1))?"YES":"NO")<<'\n';
 }}
```


---

## Problem 2 — [1909B — Make Almost Equal With Mod](https://codeforces.com/problemset/problem/1909/B)

### How this form applies

Try powers of two. `x mod 2^k` is exactly the last k bits. Increase suffix length until exactly two distinct suffixes occur.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Try powers of two. x mod 2^k is exactly the last k bits. Increase suffix length until exactly two distinct suffixes occur.

NOW FOLLOW THE BITS
-------------------
a=[8,14,22,30]
8 =001000
14=001110
22=010110
30=011110

mod2 -> last1: 0,0,0,0 -> {0}
mod4 -> last2: 00,10,10,10 -> {0,2}
Exactly two -> answer 4.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;vector<long long>a(n);for(auto&x:a)cin>>x;for(int b=1;b<=61;b++){long long m=1LL<<b;set<long long>s;for(auto x:a)s.insert(x%m);if(s.size()==2){cout<<m<<'\n';break;}}}}
```


---

## Problem 3 — [1527A — And Then There Were K](https://codeforces.com/problemset/problem/1527/A)

### How this form applies

For maximum `k<n` with `n & (n-1) & ... & k = 0`, find the highest power of two not exceeding `n`; answer is that power minus one.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
For maximum k<n with n & (n-1) & ... & k = 0, find the highest power of two not exceeding n; answer is that power minus one.

NOW FOLLOW THE BITS
-------------------
n = 10 = 1010
highest power of 2 <= n = 8 = 1000
answer = 8-1 = 7 = 0111

The interval 10,9,8,7 contains values that clear every bit in the cumulative AND.
The boundary is determined by the MSB of n.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);
 int T;cin>>T;while(T--){long long n;cin>>n;long long p=1;
  while((p<<1)<=n)p<<=1;
  cout<<p-1<<'\n';
 }}
```


---

## Problem 4 — [1688B — Patchouli’s Magical Talisman](https://codeforces.com/problemset/problem/1688/B)

### How this form applies

Parity is the first split. If at least one number is odd, every even number can be combined/converted with one operation in the optimal process. If all are even, choose the element with the fewest trailing zero bits to become odd first, then handle the rest.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Parity is the first split. If at least one number is odd, every even number can be combined/converted with one operation in the optimal process. If all are even, choose the element with the fewest trailing zero bits to become odd first, then handle the rest.

NOW FOLLOW THE BITS
-------------------
a=[4,8,12]
4 =0100 -> trailing zeros 2
8 =1000 -> trailing zeros 3
12=1100 -> trailing zeros 2
All even.
Pick 4 (or12): need 2 divisions-by-2 style steps to expose odd bit.
Then remaining n-1=2 elements each need one combining step.
answer=2+2=4.

If a=[3,4,8], odd already exists -> answer = number of evens =2.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;vector<int>a(n);int even=0,mn=31;for(int&x:a){cin>>x;if(x%2==0){even++;mn=min(mn,__builtin_ctz(x));}}if(even<n)cout<<even<<'\n';else cout<<n-1+mn<<'\n';}}
```


# Form 12 — OR Monotonicity / Required Bits

## Problem 1 — [1842B — Tenzing and Books](https://codeforces.com/problemset/problem/1842/B)

### How this form applies

OR can only add 1-bits. A book is usable only if all its 1-bits are already allowed by target x: `(v|x)==x`. Once a forbidden book appears in a stack, deeper books are inaccessible.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
OR can only add 1-bits. A book is usable only if all its 1-bits are already allowed by target x: (v|x)==x. Once a forbidden book appears in a stack, deeper books are inaccessible.

NOW FOLLOW THE BITS
-------------------
target x=101
book v=001: 001|101=101 -> safe
cur=000|001=001
book v=100: 100|101=101 -> safe
cur=001|100=101 -> reached target

book 010 would be forbidden:
010|101=111 !=101, and OR could never remove that bit1.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n,x;cin>>n>>x;vector<vector<int>>a(3,vector<int>(n));for(auto&v:a)for(int&z:v)cin>>z;int cur=0;for(auto&v:a)for(int z:v){if((z|x)!=x)break;cur|=z;}cout<<(cur==x?"Yes":"No")<<'\n';}}
```


---

## Problem 2 — [1547D — Co-growing Sequence](https://codeforces.com/problemset/problem/1547/D)

### How this form applies

Choose y[i] so `(x[i] xor y[i]) & (x[i+1] xor y[i+1]) = x[i] xor y[i]`. This means every 1-bit in previous transformed value must also be 1 in current transformed value. Add exactly the missing bits.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Choose y[i] so (x[i] xor y[i]) & (x[i+1] xor y[i+1]) = x[i] xor y[i]. This means every 1-bit in previous transformed value must also be 1 in current transformed value. Add exactly the missing bits.

NOW FOLLOW THE BITS
-------------------
previous transformed p=1011
current x=0010
Need current transformed z to contain all bits of p.
Missing bits = p & ~x
1011
~0010 (within width) -> ...1101
AND ->1001
Choose y=1001
x^y=0010^1001=1011
Now p & z =1011 = p.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;vector<int>x(n),y(n);for(int&i:x)cin>>i;int prev=x[0];for(int i=1;i<n;i++){y[i]=prev & (~x[i]);prev=x[i]^y[i];}for(int v:y)cout<<v<<' ';cout<<'\n';}}
```


---

## Problem 3 — [1042B — Vitamins](https://codeforces.com/problemset/problem/1042/B)

### How this form applies

Encode vitamins A,B,C as bits 0,1,2. Each juice is a mask. OR combines acquired vitamins; DP over 8 masks gives minimum cost.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Encode vitamins A,B,C as bits 0,1,2. Each juice is a mask. OR combines acquired vitamins; DP over 8 masks gives minimum cost.

NOW FOLLOW THE BITS
-------------------
A=001,B=010,C=100
juice AB ->011 cost5
juice C  ->100 cost3
state 000 --buy AB-->011
011 |100 =111
cost=8, all vitamins covered.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){int n;cin>>n;const int INF=1e9;vector<int>dp(8,INF);dp[0]=0;while(n--){int c;string s;cin>>c>>s;int m=0;for(char ch:s)m|=1<<(ch-'A');auto ndp=dp;for(int st=0;st<8;st++)ndp[st|m]=min(ndp[st|m],dp[st]+c);dp=ndp;}cout<<(dp[7]>=INF?-1:dp[7])<<'\n';}
```


---

## Problem 4 — [1829C — Mr. Perfectly Fine](https://codeforces.com/problemset/problem/1829/C)

### How this form applies

Two skills are a 2-bit mask: 01,10,11. Keep cheapest cost for each mask. Answer is min(cost11, cost01+cost10).

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Two skills are a 2-bit mask: 01,10,11. Keep cheapest cost for each mask. Answer is min(cost11, cost01+cost10).

NOW FOLLOW THE BITS
-------------------
book1 skill 10 cost4 -> mask2
book2 skill 01 cost3 -> mask1
book3 skill 11 cost10 -> mask3
Combine mask2|mask1=11 cost7
min(7,10)=7.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;const int INF=1e9;int best[4]={0,INF,INF,INF};while(n--){int c;string s;cin>>c>>s;int m=(s[0]-'0')*2+(s[1]-'0');best[m]=min(best[m],c);}int ans=min(best[3],best[1]+best[2]);cout<<(ans>=INF?-1:ans)<<'\n';}}
```


# Form 13 — AND Monotonicity / Maximal AND

## Problem 1 — [1669H — Maximal AND](https://codeforces.com/problemset/problem/1669/H)

### How this form applies

To put bit b into the final AND, every element must have bit b=1. Cost is the number of elements missing that bit. Spend budget greedily from bit 30 down to 0.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
To put bit b into the final AND, every element must have bit b=1. Cost is the number of elements missing that bit. Spend budget greedily from bit 30 down to 0.

NOW FOLLOW THE BITS
-------------------
a=[2,1,1], k=2
2=10
1=01
1=01

bit1 column: 1,0,0 -> need 2 operations
k=2 -> buy bit1
array can become 10,11,11
AND =10 =2
No budget remains.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;long long k;cin>>n>>k;vector<int>a(n);for(int&x:a)cin>>x;long long ans=0;for(int b=30;b>=0;b--){long long need=0;for(int x:a)if(!(x&(1<<b)))need++;if(need<=k){k-=need;ans|=1LL<<b;}}cout<<ans<<'\n';}}
```


---

## Problem 2 — [1514B — AND 0, Sum Big](https://codeforces.com/problemset/problem/1514/B)

### How this form applies

For every bit position, not all n numbers may have that bit=1, otherwise total AND is nonzero. There are `2^n-1` valid column assignments independently for each of k bits.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
For every bit position, not all n numbers may have that bit=1, otherwise total AND is nonzero. There are 2^n-1 valid column assignments independently for each of k bits.

NOW FOLLOW THE BITS
-------------------
n=3, one bit column has 2^3=8 assignments:
000 001 010 011 100 101 110 111
Only 111 makes AND bit=1.
So 7 valid assignments per bit.
For k independent bits -> 7^k.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;const long long MOD=1e9+7;long long pw(long long a,long long e){long long r=1;while(e){if(e&1)r=r*a%MOD;a=a*a%MOD;e>>=1;}return r;}int main(){int T;cin>>T;while(T--){long long n,k;cin>>n>>k;cout<<pw((pw(2,n)-1+MOD)%MOD,k)<<'\n';}}
```


---

## Problem 3 — [1991B — AND Reconstruction](https://codeforces.com/problemset/problem/1991/B)

### How this form applies

Given b[i]=a[i]&a[i+1], construct a. A standard candidate uses OR of adjacent constraints: internal `a[i]=b[i-1]|b[i]`; endpoints use their only constraint. Then verify.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Given b[i]=a[i]&a[i+1], construct a. A standard candidate uses OR of adjacent constraints: internal a[i]=b[i-1]|b[i]; endpoints use their only constraint. Then verify.

NOW FOLLOW THE BITS
-------------------
b=[2,0]
2=10, 0=00
Construct:
a0=b0=10
a1=b0|b1=10|00=10
a2=b1=00
Check:
a0&a1=10&10=10 = b0
a1&a2=10&00=00 = b1
works.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;vector<int>b(n-1);for(int&x:b)cin>>x;vector<int>a(n);if(n==1){cout<<0<<'\n';continue;}a[0]=b[0];a[n-1]=b[n-2];for(int i=1;i<n-1;i++)a[i]=b[i-1]|b[i];bool ok=true;for(int i=0;i<n-1;i++)if((a[i]&a[i+1])!=b[i])ok=false;if(!ok)cout<<-1<<'\n';else{for(int x:a)cout<<x<<' ';cout<<'\n';}}}
```


---

## Problem 4 — [1903B — StORage room](https://codeforces.com/problemset/problem/1903/B)

### How this form applies

Need `a[i] | a[j] = M[i][j]`. For each i, AND all M[i][j] over j!=i to build the bits that can safely remain in a[i], then verify every pair.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Need a[i] | a[j] = M[i][j]. For each i, AND all M[i][j] over j!=i to build the bits that can safely remain in a[i], then verify every pair.

NOW FOLLOW THE BITS
-------------------
One bit only, three nodes:
M01=1, M02=0, M12=1
M02=0 forces a0=0 and a2=0 at this bit.
Then M01=1 forces a1=1.
M12=1 is satisfied by a1=1,a2=0.
Candidate bit column: [0,1,0].

Construction followed by full verification catches inconsistent matrices.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;vector<vector<int>>m(n,vector<int>(n));for(auto&r:m)for(int&x:r)cin>>x;vector<int>a(n,(1<<30)-1);for(int i=0;i<n;i++)for(int j=0;j<n;j++)if(i!=j)a[i]&=m[i][j];bool ok=true;for(int i=0;i<n;i++)for(int j=0;j<n;j++)if(i!=j && (a[i]|a[j])!=m[i][j])ok=false;if(!ok)cout<<"NO\n";else{cout<<"YES\n";for(int x:a)cout<<x<<' ';cout<<'\n';}}}
```


# Form 14 — Bit Frequency / Majority Per Bit

## Problem 1 — [1625A — Ancient Civilization](https://codeforces.com/problemset/problem/1625/A)

### How this form applies

For each bit independently, choose the majority bit to minimize total differing positions.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
For each bit independently, choose the majority bit to minimize total differing positions.

NOW FOLLOW THE BITS
-------------------
numbers:
1011
1100
0111

bit3:1,1,0 -> ones2 -> answer bit=1
bit2:0,1,1 -> ones2 -> 1
bit1:1,0,1 -> ones2 -> 1
bit0:1,0,1 -> ones2 -> 1
answer=1111.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n,l;cin>>n>>l;vector<int>a(n);for(int&x:a)cin>>x;int ans=0;for(int b=0;b<l;b++){int one=0;for(int x:a)one+=(x>>b)&1;if(one>n-one)ans|=1<<b;}cout<<ans<<'\n';}}
```


---

## Problem 2 — [1669H — Maximal AND](https://codeforces.com/problemset/problem/1669/H)

### How this form applies

To put bit b into the final AND, every element must have bit b=1. Cost is the number of elements missing that bit. Spend budget greedily from bit 30 down to 0.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
To put bit b into the final AND, every element must have bit b=1. Cost is the number of elements missing that bit. Spend budget greedily from bit 30 down to 0.

NOW FOLLOW THE BITS
-------------------
a=[2,1,1], k=2
2=10
1=01
1=01

bit1 column: 1,0,0 -> need 2 operations
k=2 -> buy bit1
array can become 10,11,11
AND =10 =2
No budget remains.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;long long k;cin>>n>>k;vector<int>a(n);for(int&x:a)cin>>x;long long ans=0;for(int b=30;b>=0;b--){long long need=0;for(int x:a)if(!(x&(1<<b)))need++;if(need<=k){k-=need;ans|=1LL<<b;}}cout<<ans<<'\n';}}
```


---

## Problem 3 — [1514B — AND 0, Sum Big](https://codeforces.com/problemset/problem/1514/B)

### How this form applies

For every bit position, not all n numbers may have that bit=1, otherwise total AND is nonzero. There are `2^n-1` valid column assignments independently for each of k bits.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
For every bit position, not all n numbers may have that bit=1, otherwise total AND is nonzero. There are 2^n-1 valid column assignments independently for each of k bits.

NOW FOLLOW THE BITS
-------------------
n=3, one bit column has 2^3=8 assignments:
000 001 010 011 100 101 110 111
Only 111 makes AND bit=1.
So 7 valid assignments per bit.
For k independent bits -> 7^k.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;const long long MOD=1e9+7;long long pw(long long a,long long e){long long r=1;while(e){if(e&1)r=r*a%MOD;a=a*a%MOD;e>>=1;}return r;}int main(){int T;cin>>T;while(T--){long long n,k;cin>>n>>k;cout<<pw((pw(2,n)-1+MOD)%MOD,k)<<'\n';}}
```


---

## Problem 4 — [1367B — Even Array](https://codeforces.com/problemset/problem/1367/B)

### How this form applies

The condition is parity only. Compare the least-significant bit of the index and value. A swap fixes one odd-at-even mismatch together with one even-at-odd mismatch.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
The condition is parity only. Compare the least-significant bit of the index and value. A swap fixes one odd-at-even mismatch together with one even-at-odd mismatch.

NOW FOLLOW THE BITS
-------------------
Example: a = [3,2,7,6]

index      0    1    2    3
index LSB  0    1    0    1
value      3    2    7    6
binary    11   10  111  110
value LSB  1    0    1    0
            X    X    X    X

odd value at even index  = 2
Even value at odd index  = 2
Each swap consumes one of each -> answer = 2.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);
 int T; cin>>T; while(T--){int n;cin>>n;int x,y=0,z=0;
  for(int i=0;i<n;i++){cin>>x;if((x&1)!=(i&1)){if(x&1)y++;else z++;}}
  cout<<(y==z?y:-1)<<'\n';
 }}
```


# Form 15 — Bit-by-Bit Constraint Construction

## Problem 1 — [1903B — StORage room](https://codeforces.com/problemset/problem/1903/B)

### How this form applies

Need `a[i] | a[j] = M[i][j]`. For each i, AND all M[i][j] over j!=i to build the bits that can safely remain in a[i], then verify every pair.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Need a[i] | a[j] = M[i][j]. For each i, AND all M[i][j] over j!=i to build the bits that can safely remain in a[i], then verify every pair.

NOW FOLLOW THE BITS
-------------------
One bit only, three nodes:
M01=1, M02=0, M12=1
M02=0 forces a0=0 and a2=0 at this bit.
Then M01=1 forces a1=1.
M12=1 is satisfied by a1=1,a2=0.
Candidate bit column: [0,1,0].

Construction followed by full verification catches inconsistent matrices.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;vector<vector<int>>m(n,vector<int>(n));for(auto&r:m)for(int&x:r)cin>>x;vector<int>a(n,(1<<30)-1);for(int i=0;i<n;i++)for(int j=0;j<n;j++)if(i!=j)a[i]&=m[i][j];bool ok=true;for(int i=0;i<n;i++)for(int j=0;j<n;j++)if(i!=j && (a[i]|a[j])!=m[i][j])ok=false;if(!ok)cout<<"NO\n";else{cout<<"YES\n";for(int x:a)cout<<x<<' ';cout<<'\n';}}}
```


---

## Problem 2 — [1991B — AND Reconstruction](https://codeforces.com/problemset/problem/1991/B)

### How this form applies

Given b[i]=a[i]&a[i+1], construct a. A standard candidate uses OR of adjacent constraints: internal `a[i]=b[i-1]|b[i]`; endpoints use their only constraint. Then verify.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Given b[i]=a[i]&a[i+1], construct a. A standard candidate uses OR of adjacent constraints: internal a[i]=b[i-1]|b[i]; endpoints use their only constraint. Then verify.

NOW FOLLOW THE BITS
-------------------
b=[2,0]
2=10, 0=00
Construct:
a0=b0=10
a1=b0|b1=10|00=10
a2=b1=00
Check:
a0&a1=10&10=10 = b0
a1&a2=10&00=00 = b1
works.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;vector<int>b(n-1);for(int&x:b)cin>>x;vector<int>a(n);if(n==1){cout<<0<<'\n';continue;}a[0]=b[0];a[n-1]=b[n-2];for(int i=1;i<n-1;i++)a[i]=b[i-1]|b[i];bool ok=true;for(int i=0;i<n-1;i++)if((a[i]&a[i+1])!=b[i])ok=false;if(!ok)cout<<-1<<'\n';else{for(int x:a)cout<<x<<' ';cout<<'\n';}}}
```


---

## Problem 3 — [1547D — Co-growing Sequence](https://codeforces.com/problemset/problem/1547/D)

### How this form applies

Choose y[i] so `(x[i] xor y[i]) & (x[i+1] xor y[i+1]) = x[i] xor y[i]`. This means every 1-bit in previous transformed value must also be 1 in current transformed value. Add exactly the missing bits.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Choose y[i] so (x[i] xor y[i]) & (x[i+1] xor y[i+1]) = x[i] xor y[i]. This means every 1-bit in previous transformed value must also be 1 in current transformed value. Add exactly the missing bits.

NOW FOLLOW THE BITS
-------------------
previous transformed p=1011
current x=0010
Need current transformed z to contain all bits of p.
Missing bits = p & ~x
1011
~0010 (within width) -> ...1101
AND ->1001
Choose y=1001
x^y=0010^1001=1011
Now p & z =1011 = p.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;vector<int>x(n),y(n);for(int&i:x)cin>>i;int prev=x[0];for(int i=1;i<n;i++){y[i]=prev & (~x[i]);prev=x[i]^y[i];}for(int v:y)cout<<v<<' ';cout<<'\n';}}
```


---

## Problem 4 — [1842B — Tenzing and Books](https://codeforces.com/problemset/problem/1842/B)

### How this form applies

OR can only add 1-bits. A book is usable only if all its 1-bits are already allowed by target x: `(v|x)==x`. Once a forbidden book appears in a stack, deeper books are inaccessible.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
OR can only add 1-bits. A book is usable only if all its 1-bits are already allowed by target x: (v|x)==x. Once a forbidden book appears in a stack, deeper books are inaccessible.

NOW FOLLOW THE BITS
-------------------
target x=101
book v=001: 001|101=101 -> safe
cur=000|001=001
book v=100: 100|101=101 -> safe
cur=001|100=101 -> reached target

book 010 would be forbidden:
010|101=111 !=101, and OR could never remove that bit1.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n,x;cin>>n>>x;vector<vector<int>>a(3,vector<int>(n));for(auto&v:a)for(int&z:v)cin>>z;int cur=0;for(auto&v:a)for(int z:v){if((z|x)!=x)break;cur|=z;}cout<<(cur==x?"Yes":"No")<<'\n';}}
```


# Form 16 — Pairwise XOR Contribution

## Problem 1 — [1421A — XORwice](https://codeforces.com/problemset/problem/1421/A)

### How this form applies

Minimize `(a xor x) + (b xor x)`. At every bit where both `a` and `b` are 1, both terms cannot simultaneously lose that contribution; where they differ, choose x so one side has the bit. The minimum equals `a xor b`.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Minimize (a xor x) + (b xor x). At every bit where both a and b are 1, both terms cannot simultaneously lose that contribution; where they differ, choose x so one side has the bit. The minimum equals a xor b.

NOW FOLLOW THE BITS
-------------------
a=5=101, b=3=011

a^b = 110 = 6

Try x = a&b = 001:
a^x = 100 = 4
b^x = 010 = 2
sum = 6

Bit columns:
bit2: 1/0 -> contribution 4
bit1: 0/1 -> contribution 2
bit0: 1/1 -> choose x=1 -> both become 0
Total = 6.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);
 int T;cin>>T;while(T--){long long a,b;cin>>a>>b;cout<<(a^b)<<'\n';}
}
```


---

## Problem 2 — [1918C — XOR-distance](https://codeforces.com/problemset/problem/1918/C)

### How this form applies

Only differing bits of a and b matter. Keep the highest differing bit unchanged (it determines which value is larger), then greedily flip lower differing bits when the corresponding x-bit fits within r and reduces the gap.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Only differing bits of a and b matter. Keep the highest differing bit unchanged (it determines which value is larger), then greedily flip lower differing bits when the corresponding x-bit fits within r and reduces the gap.

NOW FOLLOW THE BITS
-------------------
a=10=1010, b=3=0011. a>b.
Highest differing bit is bit3:
a:1, b:0. Do NOT flip it, otherwise ordering reverses with a huge change.
Lower differing bits:
bit0: a=0,b=1. Setting x bit0 swaps them and can reduce gap.

x starts 0000. If 0001<=r, choose it:
a^x=1011=11
b^x=0010=2
Here it increased gap, so direction of lower bits matters: flip only bits where the larger number has 1 and smaller has 0, subject to r. Process from high to low.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){long long a,b,r;cin>>a>>b>>r;if(a<b)swap(a,b);long long x=0;bool first=true;for(int i=62;i>=0;i--){long long bit=1LL<<i;bool A=a&bit,B=b&bit;if(A!=B){if(first){first=false;continue;}if(A&&!B && x+bit<=r)x+=bit;}}cout<<llabs((a^x)-(b^x))<<'\n';}}
```


---

## Problem 3 — [1698A — XOR Mixup](https://codeforces.com/problemset/problem/1698/A)

### How this form applies

The appended value is XOR of all original values. XORing the final array gives zero, so any element can be reconstructed as XOR of all the others; outputting XOR of all final values is also a valid answer under the construction.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
The appended value is XOR of all original values. XORing the final array gives zero, so any element can be reconstructed as XOR of all the others; outputting XOR of all final values is also a valid answer under the construction.

NOW FOLLOW THE BITS
-------------------
Example final array: [4,3,2,5]
4=100
3=011
2=010
5=101
XOR:
100 ^ 011 = 111
111 ^ 010 = 101
101 ^ 101 = 000

The full XOR is 0 because original-XOR x appears once in addition to the originals whose XOR is x. A valid x can be obtained using the problem's guaranteed construction; the standard implementation XORs the required subset relation.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);
 int T;cin>>T;while(T--){int n;cin>>n;vector<int>a(n);for(int&x:a)cin>>x;
  // Any a[i] can serve as x when XOR of all final elements is 0.
  cout<<a[0]<<'\n';
 }}
```


---

## Problem 4 — [1516B — AGAGA XOOORRR](https://codeforces.com/problemset/problem/1516/B)

### How this form applies

Partition into at least two segments with equal XOR. If total XOR is 0, two parts can work. Otherwise, scan for two prefixes/segments whose XOR equals total XOR, giving three equal-XOR parts.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Partition into at least two segments with equal XOR. If total XOR is 0, two parts can work. Otherwise, scan for two prefixes/segments whose XOR equals total XOR, giving three equal-XOR parts.

NOW FOLLOW THE BITS
-------------------
a=[1,2,3]
1=01,2=10,3=11
total=01^10^11=00 -> YES immediately.

If total S!=0, look for:
segment1 XOR=S
segment2 XOR=S
then remaining XOR = S^S^S = S,
so three segments have equal XOR.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;vector<int>a(n);int S=0;for(int&x:a){cin>>x;S^=x;}if(S==0){cout<<"YES\n";continue;}int cur=0,cnt=0;for(int i=0;i<n-1;i++){cur^=a[i];if(cur==S){cnt++;cur=0;}}cout<<(cnt>=2?"YES":"NO")<<'\n';}}
```


# Form 17 — Pairwise AND / OR Contribution

## Problem 1 — [1514B — AND 0, Sum Big](https://codeforces.com/problemset/problem/1514/B)

### How this form applies

For every bit position, not all n numbers may have that bit=1, otherwise total AND is nonzero. There are `2^n-1` valid column assignments independently for each of k bits.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
For every bit position, not all n numbers may have that bit=1, otherwise total AND is nonzero. There are 2^n-1 valid column assignments independently for each of k bits.

NOW FOLLOW THE BITS
-------------------
n=3, one bit column has 2^3=8 assignments:
000 001 010 011 100 101 110 111
Only 111 makes AND bit=1.
So 7 valid assignments per bit.
For k independent bits -> 7^k.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;const long long MOD=1e9+7;long long pw(long long a,long long e){long long r=1;while(e){if(e&1)r=r*a%MOD;a=a*a%MOD;e>>=1;}return r;}int main(){int T;cin>>T;while(T--){long long n,k;cin>>n>>k;cout<<pw((pw(2,n)-1+MOD)%MOD,k)<<'\n';}}
```


---

## Problem 2 — [1669H — Maximal AND](https://codeforces.com/problemset/problem/1669/H)

### How this form applies

To put bit b into the final AND, every element must have bit b=1. Cost is the number of elements missing that bit. Spend budget greedily from bit 30 down to 0.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
To put bit b into the final AND, every element must have bit b=1. Cost is the number of elements missing that bit. Spend budget greedily from bit 30 down to 0.

NOW FOLLOW THE BITS
-------------------
a=[2,1,1], k=2
2=10
1=01
1=01

bit1 column: 1,0,0 -> need 2 operations
k=2 -> buy bit1
array can become 10,11,11
AND =10 =2
No budget remains.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;long long k;cin>>n>>k;vector<int>a(n);for(int&x:a)cin>>x;long long ans=0;for(int b=30;b>=0;b--){long long need=0;for(int x:a)if(!(x&(1<<b)))need++;if(need<=k){k-=need;ans|=1LL<<b;}}cout<<ans<<'\n';}}
```


---

## Problem 3 — [1903B — StORage room](https://codeforces.com/problemset/problem/1903/B)

### How this form applies

Need `a[i] | a[j] = M[i][j]`. For each i, AND all M[i][j] over j!=i to build the bits that can safely remain in a[i], then verify every pair.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Need a[i] | a[j] = M[i][j]. For each i, AND all M[i][j] over j!=i to build the bits that can safely remain in a[i], then verify every pair.

NOW FOLLOW THE BITS
-------------------
One bit only, three nodes:
M01=1, M02=0, M12=1
M02=0 forces a0=0 and a2=0 at this bit.
Then M01=1 forces a1=1.
M12=1 is satisfied by a1=1,a2=0.
Candidate bit column: [0,1,0].

Construction followed by full verification catches inconsistent matrices.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;vector<vector<int>>m(n,vector<int>(n));for(auto&r:m)for(int&x:r)cin>>x;vector<int>a(n,(1<<30)-1);for(int i=0;i<n;i++)for(int j=0;j<n;j++)if(i!=j)a[i]&=m[i][j];bool ok=true;for(int i=0;i<n;i++)for(int j=0;j<n;j++)if(i!=j && (a[i]|a[j])!=m[i][j])ok=false;if(!ok)cout<<"NO\n";else{cout<<"YES\n";for(int x:a)cout<<x<<' ';cout<<'\n';}}}
```


---

## Problem 4 — [1991B — AND Reconstruction](https://codeforces.com/problemset/problem/1991/B)

### How this form applies

Given b[i]=a[i]&a[i+1], construct a. A standard candidate uses OR of adjacent constraints: internal `a[i]=b[i-1]|b[i]`; endpoints use their only constraint. Then verify.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Given b[i]=a[i]&a[i+1], construct a. A standard candidate uses OR of adjacent constraints: internal a[i]=b[i-1]|b[i]; endpoints use their only constraint. Then verify.

NOW FOLLOW THE BITS
-------------------
b=[2,0]
2=10, 0=00
Construct:
a0=b0=10
a1=b0|b1=10|00=10
a2=b1=00
Check:
a0&a1=10&10=10 = b0
a1&a2=10&00=00 = b1
works.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;vector<int>b(n-1);for(int&x:b)cin>>x;vector<int>a(n);if(n==1){cout<<0<<'\n';continue;}a[0]=b[0];a[n-1]=b[n-2];for(int i=1;i<n-1;i++)a[i]=b[i-1]|b[i];bool ok=true;for(int i=0;i<n-1;i++)if((a[i]&a[i+1])!=b[i])ok=false;if(!ok)cout<<-1<<'\n';else{for(int x:a)cout<<x<<' ';cout<<'\n';}}}
```


# Form 18 — Conservation / Operation Decoding

## Problem 1 — [1805A — We Need the Zero](https://codeforces.com/problemset/problem/1805/A)

### How this form applies

Let S be XOR of the array. After XORing every element with x, total becomes `S ^ x` if n is odd and `S` if n is even.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Let S be XOR of the array. After XORing every element with x, total becomes S ^ x if n is odd and S if n is even.

NOW FOLLOW THE BITS
-------------------
a=[1,2,5]
1=001
2=010
5=101
S=110=6
n=3 odd -> choose x=S=110

001^110=111
010^110=100
101^110=011
111^100^011=000.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);
 int T;cin>>T;while(T--){int n;cin>>n;int xr=0,x;for(int i=0;i<n;i++){cin>>x;xr^=x;}
  if(n&1) cout<<xr<<'\n'; else cout<<(xr==0?0:-1)<<'\n';
 }}
```


---

## Problem 2 — [1516B — AGAGA XOOORRR](https://codeforces.com/problemset/problem/1516/B)

### How this form applies

Partition into at least two segments with equal XOR. If total XOR is 0, two parts can work. Otherwise, scan for two prefixes/segments whose XOR equals total XOR, giving three equal-XOR parts.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Partition into at least two segments with equal XOR. If total XOR is 0, two parts can work. Otherwise, scan for two prefixes/segments whose XOR equals total XOR, giving three equal-XOR parts.

NOW FOLLOW THE BITS
-------------------
a=[1,2,3]
1=01,2=10,3=11
total=01^10^11=00 -> YES immediately.

If total S!=0, look for:
segment1 XOR=S
segment2 XOR=S
then remaining XOR = S^S^S = S,
so three segments have equal XOR.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;vector<int>a(n);int S=0;for(int&x:a){cin>>x;S^=x;}if(S==0){cout<<"YES\n";continue;}int cur=0,cnt=0;for(int i=0;i<n-1;i++){cur^=a[i];if(cur==S){cnt++;cur=0;}}cout<<(cnt>=2?"YES":"NO")<<'\n';}}
```


---

## Problem 3 — [1698A — XOR Mixup](https://codeforces.com/problemset/problem/1698/A)

### How this form applies

The appended value is XOR of all original values. XORing the final array gives zero, so any element can be reconstructed as XOR of all the others; outputting XOR of all final values is also a valid answer under the construction.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
The appended value is XOR of all original values. XORing the final array gives zero, so any element can be reconstructed as XOR of all the others; outputting XOR of all final values is also a valid answer under the construction.

NOW FOLLOW THE BITS
-------------------
Example final array: [4,3,2,5]
4=100
3=011
2=010
5=101
XOR:
100 ^ 011 = 111
111 ^ 010 = 101
101 ^ 101 = 000

The full XOR is 0 because original-XOR x appears once in addition to the originals whose XOR is x. A valid x can be obtained using the problem's guaranteed construction; the standard implementation XORs the required subset relation.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);
 int T;cin>>T;while(T--){int n;cin>>n;vector<int>a(n);for(int&x:a)cin>>x;
  // Any a[i] can serve as x when XOR of all final elements is 0.
  cout<<a[0]<<'\n';
 }}
```


---

## Problem 4 — [1547D — Co-growing Sequence](https://codeforces.com/problemset/problem/1547/D)

### How this form applies

Choose y[i] so `(x[i] xor y[i]) & (x[i+1] xor y[i+1]) = x[i] xor y[i]`. This means every 1-bit in previous transformed value must also be 1 in current transformed value. Add exactly the missing bits.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Choose y[i] so (x[i] xor y[i]) & (x[i+1] xor y[i+1]) = x[i] xor y[i]. This means every 1-bit in previous transformed value must also be 1 in current transformed value. Add exactly the missing bits.

NOW FOLLOW THE BITS
-------------------
previous transformed p=1011
current x=0010
Need current transformed z to contain all bits of p.
Missing bits = p & ~x
1011
~0010 (within width) -> ...1101
AND ->1001
Choose y=1001
x^y=0010^1001=1011
Now p & z =1011 = p.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;vector<int>x(n),y(n);for(int&i:x)cin>>i;int prev=x[0];for(int i=1;i<n;i++){y[i]=prev & (~x[i]);prev=x[i]^y[i];}for(int v:y)cout<<v<<' ';cout<<'\n';}}
```


# Form 19 — Highest Bit -> Lowest Bit Greedy

## Problem 1 — [1669H — Maximal AND](https://codeforces.com/problemset/problem/1669/H)

### How this form applies

To put bit b into the final AND, every element must have bit b=1. Cost is the number of elements missing that bit. Spend budget greedily from bit 30 down to 0.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
To put bit b into the final AND, every element must have bit b=1. Cost is the number of elements missing that bit. Spend budget greedily from bit 30 down to 0.

NOW FOLLOW THE BITS
-------------------
a=[2,1,1], k=2
2=10
1=01
1=01

bit1 column: 1,0,0 -> need 2 operations
k=2 -> buy bit1
array can become 10,11,11
AND =10 =2
No budget remains.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;long long k;cin>>n>>k;vector<int>a(n);for(int&x:a)cin>>x;long long ans=0;for(int b=30;b>=0;b--){long long need=0;for(int x:a)if(!(x&(1<<b)))need++;if(need<=k){k-=need;ans|=1LL<<b;}}cout<<ans<<'\n';}}
```


---

## Problem 2 — [1918C — XOR-distance](https://codeforces.com/problemset/problem/1918/C)

### How this form applies

Only differing bits of a and b matter. Keep the highest differing bit unchanged (it determines which value is larger), then greedily flip lower differing bits when the corresponding x-bit fits within r and reduces the gap.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Only differing bits of a and b matter. Keep the highest differing bit unchanged (it determines which value is larger), then greedily flip lower differing bits when the corresponding x-bit fits within r and reduces the gap.

NOW FOLLOW THE BITS
-------------------
a=10=1010, b=3=0011. a>b.
Highest differing bit is bit3:
a:1, b:0. Do NOT flip it, otherwise ordering reverses with a huge change.
Lower differing bits:
bit0: a=0,b=1. Setting x bit0 swaps them and can reduce gap.

x starts 0000. If 0001<=r, choose it:
a^x=1011=11
b^x=0010=2
Here it increased gap, so direction of lower bits matters: flip only bits where the larger number has 1 and smaller has 0, subject to r. Process from high to low.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){long long a,b,r;cin>>a>>b>>r;if(a<b)swap(a,b);long long x=0;bool first=true;for(int i=62;i>=0;i--){long long bit=1LL<<i;bool A=a&bit,B=b&bit;if(A!=B){if(first){first=false;continue;}if(A&&!B && x+bit<=r)x+=bit;}}cout<<llabs((a^x)-(b^x))<<'\n';}}
```


---

## Problem 3 — [1527A — And Then There Were K](https://codeforces.com/problemset/problem/1527/A)

### How this form applies

For maximum `k<n` with `n & (n-1) & ... & k = 0`, find the highest power of two not exceeding `n`; answer is that power minus one.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
For maximum k<n with n & (n-1) & ... & k = 0, find the highest power of two not exceeding n; answer is that power minus one.

NOW FOLLOW THE BITS
-------------------
n = 10 = 1010
highest power of 2 <= n = 8 = 1000
answer = 8-1 = 7 = 0111

The interval 10,9,8,7 contains values that clear every bit in the cumulative AND.
The boundary is determined by the MSB of n.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);
 int T;cin>>T;while(T--){long long n;cin>>n;long long p=1;
  while((p<<1)<=n)p<<=1;
  cout<<p-1<<'\n';
 }}
```


---

## Problem 4 — [1420B — Rock and Lever](https://codeforces.com/problemset/problem/1420/B)

### How this form applies

A valid pair is characterized by the same highest set bit. Group numbers into MSB buckets and count pairs inside each bucket.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
A valid pair is characterized by the same highest set bit. Group numbers into MSB buckets and count pairs inside each bucket.

NOW FOLLOW THE BITS
-------------------
a=[4,5,6,9]
4=0100 -> MSB2
5=0101 -> MSB2
6=0110 -> MSB2
9=1001 -> MSB3

MSB2 bucket size=3 -> C(3,2)=3
MSB3 bucket size=1 -> 0
answer=3.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;long long c[32]={},ans=0;while(n--){int x;cin>>x;int b=31-__builtin_clz(x);c[b]++;}for(long long v:c)ans+=v*(v-1)/2;cout<<ans<<'\n';}}
```


# Form 20 — Prefix Counts of Bits

## Problem 1 — [1872E — Data Structures Fan](https://codeforces.com/problemset/problem/1872/E)

### How this form applies

Maintain XOR of the two groups defined by a binary string. Flipping a whole substring swaps membership there; prefix XOR gives XOR of the flipped values in O(1), and that value toggles both group XORs.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Maintain XOR of the two groups defined by a binary string. Flipping a whole substring swaps membership there; prefix XOR gives XOR of the flipped values in O(1), and that value toggles both group XORs.

NOW FOLLOW THE BITS
-------------------
a=[1,2,3], s=010
binary: 1=01,2=10,3=11
Group0 XOR = 1^3 = 01^11 = 10 =2
Group1 XOR = 2 =10

Flip [1,2] (0-based values 2,3): segment XOR=2^3=01
Both group XORs ^=01:
g0:10^01=11=3
g1:10^01=11=3
Membership swapped only inside range.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;vector<long long>a(n),p(n+1);for(int i=0;i<n;i++){cin>>a[i];p[i+1]=p[i]^a[i];}string s;cin>>s;long long g[2]={0,0};for(int i=0;i<n;i++)g[s[i]-'0']^=a[i];int q;cin>>q;while(q--){int tp;cin>>tp;if(tp==1){int l,r;cin>>l>>r;long long x=p[r]^p[l-1];g[0]^=x;g[1]^=x;}else{int b;cin>>b;cout<<g[b]<<' ';}}cout<<'\n';}}
```


---

## Problem 2 — [1625A — Ancient Civilization](https://codeforces.com/problemset/problem/1625/A)

### How this form applies

For each bit independently, choose the majority bit to minimize total differing positions.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
For each bit independently, choose the majority bit to minimize total differing positions.

NOW FOLLOW THE BITS
-------------------
numbers:
1011
1100
0111

bit3:1,1,0 -> ones2 -> answer bit=1
bit2:0,1,1 -> ones2 -> 1
bit1:1,0,1 -> ones2 -> 1
bit0:1,0,1 -> ones2 -> 1
answer=1111.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n,l;cin>>n>>l;vector<int>a(n);for(int&x:a)cin>>x;int ans=0;for(int b=0;b<l;b++){int one=0;for(int x:a)one+=(x>>b)&1;if(one>n-one)ans|=1<<b;}cout<<ans<<'\n';}}
```


---

## Problem 3 — [1669H — Maximal AND](https://codeforces.com/problemset/problem/1669/H)

### How this form applies

To put bit b into the final AND, every element must have bit b=1. Cost is the number of elements missing that bit. Spend budget greedily from bit 30 down to 0.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
To put bit b into the final AND, every element must have bit b=1. Cost is the number of elements missing that bit. Spend budget greedily from bit 30 down to 0.

NOW FOLLOW THE BITS
-------------------
a=[2,1,1], k=2
2=10
1=01
1=01

bit1 column: 1,0,0 -> need 2 operations
k=2 -> buy bit1
array can become 10,11,11
AND =10 =2
No budget remains.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;long long k;cin>>n>>k;vector<int>a(n);for(int&x:a)cin>>x;long long ans=0;for(int b=30;b>=0;b--){long long need=0;for(int x:a)if(!(x&(1<<b)))need++;if(need<=k){k-=need;ans|=1LL<<b;}}cout<<ans<<'\n';}}
```


---

## Problem 4 — [1516B — AGAGA XOOORRR](https://codeforces.com/problemset/problem/1516/B)

### How this form applies

Partition into at least two segments with equal XOR. If total XOR is 0, two parts can work. Otherwise, scan for two prefixes/segments whose XOR equals total XOR, giving three equal-XOR parts.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Partition into at least two segments with equal XOR. If total XOR is 0, two parts can work. Otherwise, scan for two prefixes/segments whose XOR equals total XOR, giving three equal-XOR parts.

NOW FOLLOW THE BITS
-------------------
a=[1,2,3]
1=01,2=10,3=11
total=01^10^11=00 -> YES immediately.

If total S!=0, look for:
segment1 XOR=S
segment2 XOR=S
then remaining XOR = S^S^S = S,
so three segments have equal XOR.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;vector<int>a(n);int S=0;for(int&x:a){cin>>x;S^=x;}if(S==0){cout<<"YES\n";continue;}int cur=0,cnt=0;for(int i=0;i<n-1;i++){cur^=a[i];if(cur==S){cnt++;cur=0;}}cout<<(cnt>=2?"YES":"NO")<<'\n';}}
```


# Form 21 — Common Binary Prefix / Range AND

## Problem 1 — [1527A — And Then There Were K](https://codeforces.com/problemset/problem/1527/A)

### How this form applies

For maximum `k<n` with `n & (n-1) & ... & k = 0`, find the highest power of two not exceeding `n`; answer is that power minus one.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
For maximum k<n with n & (n-1) & ... & k = 0, find the highest power of two not exceeding n; answer is that power minus one.

NOW FOLLOW THE BITS
-------------------
n = 10 = 1010
highest power of 2 <= n = 8 = 1000
answer = 8-1 = 7 = 0111

The interval 10,9,8,7 contains values that clear every bit in the cumulative AND.
The boundary is determined by the MSB of n.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);
 int T;cin>>T;while(T--){long long n;cin>>n;long long p=1;
  while((p<<1)<=n)p<<=1;
  cout<<p-1<<'\n';
 }}
```


---

## Problem 2 — [1420B — Rock and Lever](https://codeforces.com/problemset/problem/1420/B)

### How this form applies

A valid pair is characterized by the same highest set bit. Group numbers into MSB buckets and count pairs inside each bucket.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
A valid pair is characterized by the same highest set bit. Group numbers into MSB buckets and count pairs inside each bucket.

NOW FOLLOW THE BITS
-------------------
a=[4,5,6,9]
4=0100 -> MSB2
5=0101 -> MSB2
6=0110 -> MSB2
9=1001 -> MSB3

MSB2 bucket size=3 -> C(3,2)=3
MSB3 bucket size=1 -> 0
answer=3.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;long long c[32]={},ans=0;while(n--){int x;cin>>x;int b=31-__builtin_clz(x);c[b]++;}for(long long v:c)ans+=v*(v-1)/2;cout<<ans<<'\n';}}
```


---

## Problem 3 — [1669H — Maximal AND](https://codeforces.com/problemset/problem/1669/H)

### How this form applies

To put bit b into the final AND, every element must have bit b=1. Cost is the number of elements missing that bit. Spend budget greedily from bit 30 down to 0.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
To put bit b into the final AND, every element must have bit b=1. Cost is the number of elements missing that bit. Spend budget greedily from bit 30 down to 0.

NOW FOLLOW THE BITS
-------------------
a=[2,1,1], k=2
2=10
1=01
1=01

bit1 column: 1,0,0 -> need 2 operations
k=2 -> buy bit1
array can become 10,11,11
AND =10 =2
No budget remains.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;long long k;cin>>n>>k;vector<int>a(n);for(int&x:a)cin>>x;long long ans=0;for(int b=30;b>=0;b--){long long need=0;for(int x:a)if(!(x&(1<<b)))need++;if(need<=k){k-=need;ans|=1LL<<b;}}cout<<ans<<'\n';}}
```


---

## Problem 4 — [1475A — Odd Divisor](https://codeforces.com/problemset/problem/1475/A)

### How this form applies

A number has no odd divisor greater than 1 exactly when it is a power of two. Use `n & (n-1)` to test that form.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
A number has no odd divisor greater than 1 exactly when it is a power of two. Use n & (n-1) to test that form.

NOW FOLLOW THE BITS
-------------------
n = 8
8   = 1000
7   = 0111
&     0000 -> power of two -> NO

n = 12
12  = 1100
11  = 1011
&     1000 -> more than one set bit -> has odd factor 3 -> YES.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);
 int T;cin>>T;while(T--){long long n;cin>>n;
  cout<<((n&(n-1))?"YES":"NO")<<'\n';
 }}
```


# Form 22 — Complement Within Fixed Width

## Problem 1 — [1926D — Vlad and Division](https://codeforces.com/problemset/problem/1926/D)

### How this form applies

Two values can pair when their lowest 31 bits are opposite. Partner is `x xor ((1<<31)-1)`. Greedily match complements.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Two values can pair when their lowest 31 bits are opposite. Partner is x xor ((1<<31)-1). Greedily match complements.

NOW FOLLOW THE BITS
-------------------
5-bit illustration:
x=10110
mask=11111
partner=01001

10110
01001
-----
every column is 1/0 or 0/1.
If partner already waits, pair them; otherwise start a new group with x.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;const long long M=(1LL<<31)-1;while(T--){int n;cin>>n;unordered_map<long long,int>cnt;int ans=0;while(n--){long long x;cin>>x,y=x^M;if(cnt[y])cnt[y]--;else{cnt[x]++;ans++;}}cout<<ans<<'\n';}}
```


---

## Problem 2 — [1421A — XORwice](https://codeforces.com/problemset/problem/1421/A)

### How this form applies

Minimize `(a xor x) + (b xor x)`. At every bit where both `a` and `b` are 1, both terms cannot simultaneously lose that contribution; where they differ, choose x so one side has the bit. The minimum equals `a xor b`.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Minimize (a xor x) + (b xor x). At every bit where both a and b are 1, both terms cannot simultaneously lose that contribution; where they differ, choose x so one side has the bit. The minimum equals a xor b.

NOW FOLLOW THE BITS
-------------------
a=5=101, b=3=011

a^b = 110 = 6

Try x = a&b = 001:
a^x = 100 = 4
b^x = 010 = 2
sum = 6

Bit columns:
bit2: 1/0 -> contribution 4
bit1: 0/1 -> contribution 2
bit0: 1/1 -> choose x=1 -> both become 0
Total = 6.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);
 int T;cin>>T;while(T--){long long a,b;cin>>a>>b;cout<<(a^b)<<'\n';}
}
```


---

## Problem 3 — [1918C — XOR-distance](https://codeforces.com/problemset/problem/1918/C)

### How this form applies

Only differing bits of a and b matter. Keep the highest differing bit unchanged (it determines which value is larger), then greedily flip lower differing bits when the corresponding x-bit fits within r and reduces the gap.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Only differing bits of a and b matter. Keep the highest differing bit unchanged (it determines which value is larger), then greedily flip lower differing bits when the corresponding x-bit fits within r and reduces the gap.

NOW FOLLOW THE BITS
-------------------
a=10=1010, b=3=0011. a>b.
Highest differing bit is bit3:
a:1, b:0. Do NOT flip it, otherwise ordering reverses with a huge change.
Lower differing bits:
bit0: a=0,b=1. Setting x bit0 swaps them and can reduce gap.

x starts 0000. If 0001<=r, choose it:
a^x=1011=11
b^x=0010=2
Here it increased gap, so direction of lower bits matters: flip only bits where the larger number has 1 and smaller has 0, subject to r. Process from high to low.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){long long a,b,r;cin>>a>>b>>r;if(a<b)swap(a,b);long long x=0;bool first=true;for(int i=62;i>=0;i--){long long bit=1LL<<i;bool A=a&bit,B=b&bit;if(A!=B){if(first){first=false;continue;}if(A&&!B && x+bit<=r)x+=bit;}}cout<<llabs((a^x)-(b^x))<<'\n';}}
```


---

## Problem 4 — [1362B — Johnny and His Hobbies](https://codeforces.com/problemset/problem/1362/B)

### How this form applies

Find positive x such that XORing every set element by x produces the same set. Try x and compare transformed multiset/set.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Find positive x such that XORing every set element by x produces the same set. Try x and compare transformed multiset/set.

NOW FOLLOW THE BITS
-------------------
S={1,2,3,4}
Try x=5 (101):
1=001 ^101=100=4
2=010 ^101=111=7 -> 7 not in S -> fail

For a candidate x, every bit where x has 1 toggles that column for all numbers. The transformed collection must exactly match the original collection.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;vector<int>a(n);set<int>s;for(int&v:a){cin>>v;s.insert(v);}int ans=-1;for(int x=1;x<1024;x++){set<int>t;for(int v:a)t.insert(v^x);if(t==s){ans=x;break;}}cout<<ans<<'\n';}}
```


# Form 23 — Subset Enumeration

## Problem 1 — [1097B — Petr and a Combination Lock](https://codeforces.com/problemset/problem/1097/B)

### How this form applies

Each angle has two choices: + or -. Encode the choice for angle i in bit i of a mask and enumerate all 2^n assignments.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Each angle has two choices: + or -. Encode the choice for angle i in bit i of a mask and enumerate all 2^n assignments.

NOW FOLLOW THE BITS
-------------------
n=3, angles=[10,20,30]
mask=101
bit0=1 -> +10
bit1=0 -> -20
bit2=1 -> +30
sum=20, not divisible by360.
Try every mask 000..111 until one gives sum%360=0.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){int n;cin>>n;vector<int>a(n);for(int&x:a)cin>>x;for(int m=0;m<(1<<n);m++){int s=0;for(int i=0;i<n;i++)s+=(m>>i&1)?a[i]:-a[i];if((s%360+360)%360==0){cout<<"YES\n";return 0;}}cout<<"NO\n";}
```


---

## Problem 2 — [550B — Preparing Olympiad](https://codeforces.com/problemset/problem/550/B)

### How this form applies

Enumerate every subset. A mask chooses problems; test count>=2, total difficulty in [l,r], and max-min>=x.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Enumerate every subset. A mask chooses problems; test count>=2, total difficulty in [l,r], and max-min>=x.

NOW FOLLOW THE BITS
-------------------
d=[800,1000,1300], mask=101
chosen bits: problem0 and problem2
count=2
sum=2100
max-min=1300-800=500
Check all three conditions. Each mask is one candidate team.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){int n,l,r,x;cin>>n>>l>>r>>x;vector<int>a(n);for(int&v:a)cin>>v;int ans=0;for(int m=0;m<(1<<n);m++){int c=0,s=0,mn=1e9,mx=-1;for(int i=0;i<n;i++)if(m>>i&1){c++;s+=a[i];mn=min(mn,a[i]);mx=max(mx,a[i]);}if(c>=2&&s>=l&&s<=r&&mx-mn>=x)ans++;}cout<<ans<<'\n';}
```


---

## Problem 3 — [1042B — Vitamins](https://codeforces.com/problemset/problem/1042/B)

### How this form applies

Encode vitamins A,B,C as bits 0,1,2. Each juice is a mask. OR combines acquired vitamins; DP over 8 masks gives minimum cost.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Encode vitamins A,B,C as bits 0,1,2. Each juice is a mask. OR combines acquired vitamins; DP over 8 masks gives minimum cost.

NOW FOLLOW THE BITS
-------------------
A=001,B=010,C=100
juice AB ->011 cost5
juice C  ->100 cost3
state 000 --buy AB-->011
011 |100 =111
cost=8, all vitamins covered.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){int n;cin>>n;const int INF=1e9;vector<int>dp(8,INF);dp[0]=0;while(n--){int c;string s;cin>>c>>s;int m=0;for(char ch:s)m|=1<<(ch-'A');auto ndp=dp;for(int st=0;st<8;st++)ndp[st|m]=min(ndp[st|m],dp[st]+c);dp=ndp;}cout<<(dp[7]>=INF?-1:dp[7])<<'\n';}
```


---

## Problem 4 — [1829C — Mr. Perfectly Fine](https://codeforces.com/problemset/problem/1829/C)

### How this form applies

Two skills are a 2-bit mask: 01,10,11. Keep cheapest cost for each mask. Answer is min(cost11, cost01+cost10).

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Two skills are a 2-bit mask: 01,10,11. Keep cheapest cost for each mask. Answer is min(cost11, cost01+cost10).

NOW FOLLOW THE BITS
-------------------
book1 skill 10 cost4 -> mask2
book2 skill 01 cost3 -> mask1
book3 skill 11 cost10 -> mask3
Combine mask2|mask1=11 cost7
min(7,10)=7.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;const int INF=1e9;int best[4]={0,INF,INF,INF};while(n--){int c;string s;cin>>c>>s;int m=(s[0]-'0')*2+(s[1]-'0');best[m]=min(best[m],c);}int ans=min(best[3],best[1]+best[2]);cout<<(ans>=INF?-1:ans)<<'\n';}}
```


# Form 24 — Bitmask as State

## Problem 1 — [1042B — Vitamins](https://codeforces.com/problemset/problem/1042/B)

### How this form applies

Encode vitamins A,B,C as bits 0,1,2. Each juice is a mask. OR combines acquired vitamins; DP over 8 masks gives minimum cost.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Encode vitamins A,B,C as bits 0,1,2. Each juice is a mask. OR combines acquired vitamins; DP over 8 masks gives minimum cost.

NOW FOLLOW THE BITS
-------------------
A=001,B=010,C=100
juice AB ->011 cost5
juice C  ->100 cost3
state 000 --buy AB-->011
011 |100 =111
cost=8, all vitamins covered.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){int n;cin>>n;const int INF=1e9;vector<int>dp(8,INF);dp[0]=0;while(n--){int c;string s;cin>>c>>s;int m=0;for(char ch:s)m|=1<<(ch-'A');auto ndp=dp;for(int st=0;st<8;st++)ndp[st|m]=min(ndp[st|m],dp[st]+c);dp=ndp;}cout<<(dp[7]>=INF?-1:dp[7])<<'\n';}
```


---

## Problem 2 — [1829C — Mr. Perfectly Fine](https://codeforces.com/problemset/problem/1829/C)

### How this form applies

Two skills are a 2-bit mask: 01,10,11. Keep cheapest cost for each mask. Answer is min(cost11, cost01+cost10).

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Two skills are a 2-bit mask: 01,10,11. Keep cheapest cost for each mask. Answer is min(cost11, cost01+cost10).

NOW FOLLOW THE BITS
-------------------
book1 skill 10 cost4 -> mask2
book2 skill 01 cost3 -> mask1
book3 skill 11 cost10 -> mask3
Combine mask2|mask1=11 cost7
min(7,10)=7.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;const int INF=1e9;int best[4]={0,INF,INF,INF};while(n--){int c;string s;cin>>c>>s;int m=(s[0]-'0')*2+(s[1]-'0');best[m]=min(best[m],c);}int ans=min(best[3],best[1]+best[2]);cout<<(ans>=INF?-1:ans)<<'\n';}}
```


---

## Problem 3 — [1097B — Petr and a Combination Lock](https://codeforces.com/problemset/problem/1097/B)

### How this form applies

Each angle has two choices: + or -. Encode the choice for angle i in bit i of a mask and enumerate all 2^n assignments.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Each angle has two choices: + or -. Encode the choice for angle i in bit i of a mask and enumerate all 2^n assignments.

NOW FOLLOW THE BITS
-------------------
n=3, angles=[10,20,30]
mask=101
bit0=1 -> +10
bit1=0 -> -20
bit2=1 -> +30
sum=20, not divisible by360.
Try every mask 000..111 until one gives sum%360=0.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){int n;cin>>n;vector<int>a(n);for(int&x:a)cin>>x;for(int m=0;m<(1<<n);m++){int s=0;for(int i=0;i<n;i++)s+=(m>>i&1)?a[i]:-a[i];if((s%360+360)%360==0){cout<<"YES\n";return 0;}}cout<<"NO\n";}
```


---

## Problem 4 — [550B — Preparing Olympiad](https://codeforces.com/problemset/problem/550/B)

### How this form applies

Enumerate every subset. A mask chooses problems; test count>=2, total difficulty in [l,r], and max-min>=x.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Enumerate every subset. A mask chooses problems; test count>=2, total difficulty in [l,r], and max-min>=x.

NOW FOLLOW THE BITS
-------------------
d=[800,1000,1300], mask=101
chosen bits: problem0 and problem2
count=2
sum=2100
max-min=1300-800=500
Check all three conditions. Each mask is one candidate team.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){int n,l,r,x;cin>>n>>l>>r>>x;vector<int>a(n);for(int&v:a)cin>>v;int ans=0;for(int m=0;m<(1<<n);m++){int c=0,s=0,mn=1e9,mx=-1;for(int i=0;i<n;i++)if(m>>i&1){c++;s+=a[i];mn=min(mn,a[i]);mx=max(mx,a[i]);}if(c>=2&&s>=l&&s<=r&&mx-mn>=x)ans++;}cout<<ans<<'\n';}
```


# Form 25 — Submask Enumeration

## Problem 1 — [1042B — Vitamins](https://codeforces.com/problemset/problem/1042/B)

### How this form applies

Encode vitamins A,B,C as bits 0,1,2. Each juice is a mask. OR combines acquired vitamins; DP over 8 masks gives minimum cost.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Encode vitamins A,B,C as bits 0,1,2. Each juice is a mask. OR combines acquired vitamins; DP over 8 masks gives minimum cost.

NOW FOLLOW THE BITS
-------------------
A=001,B=010,C=100
juice AB ->011 cost5
juice C  ->100 cost3
state 000 --buy AB-->011
011 |100 =111
cost=8, all vitamins covered.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){int n;cin>>n;const int INF=1e9;vector<int>dp(8,INF);dp[0]=0;while(n--){int c;string s;cin>>c>>s;int m=0;for(char ch:s)m|=1<<(ch-'A');auto ndp=dp;for(int st=0;st<8;st++)ndp[st|m]=min(ndp[st|m],dp[st]+c);dp=ndp;}cout<<(dp[7]>=INF?-1:dp[7])<<'\n';}
```


---

## Problem 2 — [1829C — Mr. Perfectly Fine](https://codeforces.com/problemset/problem/1829/C)

### How this form applies

Two skills are a 2-bit mask: 01,10,11. Keep cheapest cost for each mask. Answer is min(cost11, cost01+cost10).

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Two skills are a 2-bit mask: 01,10,11. Keep cheapest cost for each mask. Answer is min(cost11, cost01+cost10).

NOW FOLLOW THE BITS
-------------------
book1 skill 10 cost4 -> mask2
book2 skill 01 cost3 -> mask1
book3 skill 11 cost10 -> mask3
Combine mask2|mask1=11 cost7
min(7,10)=7.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;const int INF=1e9;int best[4]={0,INF,INF,INF};while(n--){int c;string s;cin>>c>>s;int m=(s[0]-'0')*2+(s[1]-'0');best[m]=min(best[m],c);}int ans=min(best[3],best[1]+best[2]);cout<<(ans>=INF?-1:ans)<<'\n';}}
```


---

## Problem 3 — [550B — Preparing Olympiad](https://codeforces.com/problemset/problem/550/B)

### How this form applies

Enumerate every subset. A mask chooses problems; test count>=2, total difficulty in [l,r], and max-min>=x.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Enumerate every subset. A mask chooses problems; test count>=2, total difficulty in [l,r], and max-min>=x.

NOW FOLLOW THE BITS
-------------------
d=[800,1000,1300], mask=101
chosen bits: problem0 and problem2
count=2
sum=2100
max-min=1300-800=500
Check all three conditions. Each mask is one candidate team.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){int n,l,r,x;cin>>n>>l>>r>>x;vector<int>a(n);for(int&v:a)cin>>v;int ans=0;for(int m=0;m<(1<<n);m++){int c=0,s=0,mn=1e9,mx=-1;for(int i=0;i<n;i++)if(m>>i&1){c++;s+=a[i];mn=min(mn,a[i]);mx=max(mx,a[i]);}if(c>=2&&s>=l&&s<=r&&mx-mn>=x)ans++;}cout<<ans<<'\n';}
```


---

## Problem 4 — [1097B — Petr and a Combination Lock](https://codeforces.com/problemset/problem/1097/B)

### How this form applies

Each angle has two choices: + or -. Encode the choice for angle i in bit i of a mask and enumerate all 2^n assignments.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Each angle has two choices: + or -. Encode the choice for angle i in bit i of a mask and enumerate all 2^n assignments.

NOW FOLLOW THE BITS
-------------------
n=3, angles=[10,20,30]
mask=101
bit0=1 -> +10
bit1=0 -> -20
bit2=1 -> +30
sum=20, not divisible by360.
Try every mask 000..111 until one gives sum%360=0.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){int n;cin>>n;vector<int>a(n);for(int&x:a)cin>>x;for(int m=0;m<(1<<n);m++){int s=0;for(int i=0;i<n;i++)s+=(m>>i&1)?a[i]:-a[i];if((s%360+360)%360==0){cout<<"YES\n";return 0;}}cout<<"NO\n";}
```


# Form 26 — Bitmask DP

## Problem 1 — [1042B — Vitamins](https://codeforces.com/problemset/problem/1042/B)

### How this form applies

Encode vitamins A,B,C as bits 0,1,2. Each juice is a mask. OR combines acquired vitamins; DP over 8 masks gives minimum cost.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Encode vitamins A,B,C as bits 0,1,2. Each juice is a mask. OR combines acquired vitamins; DP over 8 masks gives minimum cost.

NOW FOLLOW THE BITS
-------------------
A=001,B=010,C=100
juice AB ->011 cost5
juice C  ->100 cost3
state 000 --buy AB-->011
011 |100 =111
cost=8, all vitamins covered.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){int n;cin>>n;const int INF=1e9;vector<int>dp(8,INF);dp[0]=0;while(n--){int c;string s;cin>>c>>s;int m=0;for(char ch:s)m|=1<<(ch-'A');auto ndp=dp;for(int st=0;st<8;st++)ndp[st|m]=min(ndp[st|m],dp[st]+c);dp=ndp;}cout<<(dp[7]>=INF?-1:dp[7])<<'\n';}
```


---

## Problem 2 — [1829C — Mr. Perfectly Fine](https://codeforces.com/problemset/problem/1829/C)

### How this form applies

Two skills are a 2-bit mask: 01,10,11. Keep cheapest cost for each mask. Answer is min(cost11, cost01+cost10).

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Two skills are a 2-bit mask: 01,10,11. Keep cheapest cost for each mask. Answer is min(cost11, cost01+cost10).

NOW FOLLOW THE BITS
-------------------
book1 skill 10 cost4 -> mask2
book2 skill 01 cost3 -> mask1
book3 skill 11 cost10 -> mask3
Combine mask2|mask1=11 cost7
min(7,10)=7.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ios::sync_with_stdio(false);cin.tie(nullptr);int T;cin>>T;while(T--){int n;cin>>n;const int INF=1e9;int best[4]={0,INF,INF,INF};while(n--){int c;string s;cin>>c>>s;int m=(s[0]-'0')*2+(s[1]-'0');best[m]=min(best[m],c);}int ans=min(best[3],best[1]+best[2]);cout<<(ans>=INF?-1:ans)<<'\n';}}
```


---

## Problem 3 — [1097B — Petr and a Combination Lock](https://codeforces.com/problemset/problem/1097/B)

### How this form applies

Each angle has two choices: + or -. Encode the choice for angle i in bit i of a mask and enumerate all 2^n assignments.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Each angle has two choices: + or -. Encode the choice for angle i in bit i of a mask and enumerate all 2^n assignments.

NOW FOLLOW THE BITS
-------------------
n=3, angles=[10,20,30]
mask=101
bit0=1 -> +10
bit1=0 -> -20
bit2=1 -> +30
sum=20, not divisible by360.
Try every mask 000..111 until one gives sum%360=0.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){int n;cin>>n;vector<int>a(n);for(int&x:a)cin>>x;for(int m=0;m<(1<<n);m++){int s=0;for(int i=0;i<n;i++)s+=(m>>i&1)?a[i]:-a[i];if((s%360+360)%360==0){cout<<"YES\n";return 0;}}cout<<"NO\n";}
```


---

## Problem 4 — [550B — Preparing Olympiad](https://codeforces.com/problemset/problem/550/B)

### How this form applies

Enumerate every subset. A mask chooses problems; test count>=2, total difficulty in [l,r], and max-min>=x.

### Simple self-explanatory bit-by-bit dry run

```text
WHAT TO NOTICE
--------------
Enumerate every subset. A mask chooses problems; test count>=2, total difficulty in [l,r], and max-min>=x.

NOW FOLLOW THE BITS
-------------------
d=[800,1000,1300], mask=101
chosen bits: problem0 and problem2
count=2
sum=2100
max-min=1300-800=500
Check all three conditions. Each mask is one candidate team.

FINAL IDEA
----------
Do not memorize the decimal example. Identify the same bit condition in the new input, apply the form shown above, and then implement exactly that condition.
```

### C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){int n,l,r,x;cin>>n>>l>>r>>x;vector<int>a(n);for(int&v:a)cin>>v;int ans=0;for(int m=0;m<(1<<n);m++){int c=0,s=0,mn=1e9,mx=-1;for(int i=0;i<n;i++)if(m>>i&1){c++;s+=a[i];mn=min(mn,a[i]);mx=max(mx,a[i]);}if(c>=2&&s>=l&&s<=r&&mx-mn>=x)ans++;}cout<<ans<<'\n';}
```

