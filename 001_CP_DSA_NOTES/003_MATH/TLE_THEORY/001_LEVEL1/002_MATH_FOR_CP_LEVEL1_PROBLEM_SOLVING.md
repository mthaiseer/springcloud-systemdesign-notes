# Math for CP - Level 1 Problem Solving

> **Style:** Don't memorize the final formula.\
> Go through: **Problem -\> Variables -\> Math prerequisite -\> Model
> -\> Derivation -\> Dry run -\> C++ -\> Recognition**.
>
> **Rendering:** Formulas use plain Markdown/text so the file renders
> safely on GitHub.

## Table of Contents

1.  [Math Preliminaries](#1-math-preliminaries)
2.  [Problem 1 - Upload More RAM](#2-problem-1---upload-more-ram)
3.  [Problem 2 - Maximum Multiple
    Sum](#3-problem-2---maximum-multiple-sum)
4.  [Problem 3 - Large Addition](#4-problem-3---large-addition)
5.  [Final Recognition Sheet](#5-final-recognition-sheet)

------------------------------------------------------------------------

# 1. Math Preliminaries

Only the math needed for these problems is included.

## 1.1 Arithmetic Progression (AP)

An AP has a constant difference.

``` text
1, 4, 7, 10, ...
```

Here:

``` text
first term a = 1
common difference d = 3
```

The nth term is:

``` text
Tn = a + (n - 1)d
```

Example:

``` text
a = 1
d = 3
n = 4

T4
= 1 + (4 - 1) × 3
= 1 + 9
= 10
```

### Why?

``` text
T1 = a
T2 = a + d
T3 = a + 2d
T4 = a + 3d
...
Tn = a + (n-1)d
```

------------------------------------------------------------------------

## 1.2 Sum of First k Natural Numbers

``` text
1 + 2 + 3 + ... + k
=
k(k + 1) / 2
```

Example:

``` text
1 + 2 + 3 + 4
=
4 × 5 / 2
=
10
```

------------------------------------------------------------------------

## 1.3 Sum of Multiples of x

The multiples of `x` are:

``` text
x, 2x, 3x, ..., kx
```

Factor out `x`:

``` text
x + 2x + 3x + ... + kx

= x(1 + 2 + 3 + ... + k)

= x × k(k + 1) / 2
```

If only multiples `<= n` are allowed:

``` text
kx <= n
```

Therefore:

``` text
k = floor(n / x)
```

So:

``` text
sumMultiples(x)
=
x × k × (k + 1) / 2

where k = n / x using integer division
```

------------------------------------------------------------------------

## 1.4 Decimal Addition and Carry

When adding two digits:

``` text
digitA + digitB + incomingCarry
```

Example:

``` text
8 + 7 = 15

write 5
carry 1
```

With an incoming carry:

``` text
8 + 7 + 1
= 16

write 6
carry 1
```

For digits from `5` to `9`:

``` text
minimum sum = 5 + 5 = 10
maximum sum = 9 + 9 = 18
```

This range is the key to the Large Addition problem.

------------------------------------------------------------------------

# 2. Problem 1 - Upload More RAM

**Problem Link:** https://codeforces.com/problemset/problem/1606/B

## 2.1 What Is the Problem Asking?

You need to upload `n` GB.

Every second you upload either:

``` text
0 GB
or
1 GB
```

Restriction:

``` text
in ANY k consecutive seconds,
at most one second may upload 1 GB
```

We need the **minimum total seconds** needed to upload all `n` GB.

------------------------------------------------------------------------

## 2.2 Story -\> Variables

``` text
n = number of 1-GB uploads required
k = minimum spacing pattern forced by the window rule
```

Each uploaded GB corresponds to one `1`.

We want to place `n` ones as close together as legally possible.

------------------------------------------------------------------------

## 2.3 Start With a Small Example

Take:

``` text
n = 2
k = 3
```

We need two `1`s.

Can we do:

``` text
second: 1 2 3
upload: 1 0 1
```

No.

The window `[1,2,3]` contains two `1`s.

Valid earliest arrangement:

``` text
second: 1 2 3 4
upload: 1 0 0 1
```

Answer:

``` text
4 seconds
```

------------------------------------------------------------------------

## 2.4 Key Observation

Upload the first GB immediately:

``` text
second 1
```

After an upload, the next upload can happen only after `k` seconds of
position difference.

So upload positions are:

``` text
1
1 + k
1 + 2k
1 + 3k
...
```

This is an **Arithmetic Progression**.

``` text
first term a = 1
difference d = k
```

We need the position of the `n`th upload.

Use:

``` text
Tn = a + (n - 1)d
```

Substitute:

``` text
answer
= 1 + (n - 1)k
```

------------------------------------------------------------------------

## 2.5 Why `(n - 1)` and Not `n`?

The first upload needs no waiting before it.

``` text
upload #1 -> second 1
```

Only the gaps between uploads cost `k`.

For `n` uploads there are:

``` text
n - 1 gaps
```

ASCII:

``` text
1st        2nd        3rd        4th
 |----------|----------|----------|
      k          k          k

For 4 uploads:
number of gaps = 3 = n - 1
```

Therefore:

``` text
total
= first position + all gaps
= 1 + (n - 1)k
```

------------------------------------------------------------------------

## 2.6 Detailed Dry Run

### Example 1

``` text
n = 5
k = 1
```

Formula:

``` text
answer
= 1 + (5 - 1) × 1
= 1 + 4
= 5
```

Timeline:

``` text
second: 1 2 3 4 5
upload: 1 1 1 1 1
```

With `k = 1`, every window has only one second, so uploading every
second is valid.

------------------------------------------------------------------------

### Example 2

``` text
n = 2
k = 3
```

``` text
answer
= 1 + (2 - 1) × 3
= 4
```

Timeline:

``` text
second: 1 2 3 4
upload: 1 0 0 1
```

------------------------------------------------------------------------

### Example 3

``` text
n = 11
k = 5
```

``` text
answer
= 1 + (11 - 1) × 5
= 1 + 50
= 51
```

No simulation is needed.

------------------------------------------------------------------------

## 2.7 C++17

``` cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;

    while (t--) {
        long long n, k;
        cin >> n >> k;

        long long ans = 1 + (n - 1) * k;
        cout << ans << '\n';
    }
}
```

### Complexity

``` text
Time:  O(1) per test case
Space: O(1)
```

------------------------------------------------------------------------

## 2.8 Recognition Model

When you see:

``` text
repeat an event n times
+
fixed minimum gap
+
find earliest finishing position
```

think:

``` text
positions form an AP
```

Then:

``` text
first + (count - 1) × gap
```

------------------------------------------------------------------------

# 3. Problem 2 - Maximum Multiple Sum

**Problem Link:** https://codeforces.com/problemset/problem/1985/B

## 3.1 What Is the Problem Asking?

Choose:

``` text
2 <= x <= n
```

For that `x`, take every multiple of `x` not exceeding `n`:

``` text
x, 2x, 3x, ..., kx
```

Compute their sum.

Return the `x` producing the maximum sum.

------------------------------------------------------------------------

## 3.2 Brute Force Model

Suppose:

``` text
n = 17
x = 4
```

Multiples:

``` text
4, 8, 12, 16
```

Sum:

``` text
4 + 8 + 12 + 16 = 40
```

A direct nested loop could do this for every `x`, but we can calculate
each sum mathematically.

------------------------------------------------------------------------

## 3.3 First Question - How Many Multiples?

We need:

``` text
kx <= n
```

Divide by `x`:

``` text
k <= n / x
```

Largest integer `k`:

``` text
k = floor(n / x)
```

In C++ integer division:

``` cpp
k = n / x;
```

Example:

``` text
n = 17
x = 4

k = floor(17 / 4)
  = 4
```

So the multiples are:

``` text
1×4, 2×4, 3×4, 4×4
```

------------------------------------------------------------------------

## 3.4 Derive the Sum

We need:

``` text
x + 2x + 3x + ... + kx
```

Factor out `x`:

``` text
= x(1 + 2 + 3 + ... + k)
```

Use:

``` text
1 + 2 + ... + k
=
k(k + 1) / 2
```

Therefore:

``` text
sum
=
x × k(k + 1) / 2
```

where:

``` text
k = floor(n / x)
```

Final model:

``` text
k = n / x

sum(x)
=
x × k × (k + 1) / 2
```

------------------------------------------------------------------------

## 3.5 Detailed Dry Run - n = 7

Try every `x`.

### x = 2

``` text
k = floor(7 / 2)
  = 3
```

Multiples:

``` text
2, 4, 6
```

Formula:

``` text
sum
= 2 × 3 × 4 / 2
= 12
```

------------------------------------------------------------------------

### x = 3

``` text
k = floor(7 / 3)
  = 2
```

``` text
sum
= 3 × 2 × 3 / 2
= 9
```

Multiples:

``` text
3 + 6 = 9
```

------------------------------------------------------------------------

### x = 4

``` text
k = 1
```

``` text
sum = 4
```

Similarly:

``` text
x = 5 -> 5
x = 6 -> 6
x = 7 -> 7
```

Comparison:

``` text
x   sum
-------
2   12   <- maximum
3    9
4    4
5    5
6    6
7    7
```

Answer:

``` text
2
```

------------------------------------------------------------------------

## 3.6 Why the Formula Is Better

Without the formula, for each `x` we may loop through all its multiples.

With the formula:

``` text
k = n / x
sum = x × k × (k + 1) / 2
```

Each candidate `x` takes:

``` text
O(1)
```

Then checking all:

``` text
x = 2 ... n
```

takes:

``` text
O(n)
```

------------------------------------------------------------------------

## 3.7 C++17

``` cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;

    while (t--) {
        long long n;
        cin >> n;

        long long bestX = 2;
        long long bestSum = -1;

        for (long long x = 2; x <= n; ++x) {
            long long k = n / x;

            long long sum =
                x * k * (k + 1) / 2;

            if (sum > bestSum) {
                bestSum = sum;
                bestX = x;
            }
        }

        cout << bestX << '\n';
    }
}
```

### Complexity

``` text
Time:  O(n) per test case
Space: O(1)
```

------------------------------------------------------------------------

## 3.8 Recognition Model

When you see:

``` text
x + 2x + 3x + ... + kx
```

do not loop immediately.

Think:

``` text
factor x

x(1 + 2 + ... + k)
```

then:

``` text
k = floor(n / x)

sum = x × k(k+1)/2
```

This is:

``` text
multiples
+
count terms
+
AP / natural-number sum
```

------------------------------------------------------------------------

# 4. Problem 3 - Large Addition

**Problem Link:** https://codeforces.com/problemset/problem/1984/B

## 4.1 What Is the Problem Asking?

A **large digit** is:

``` text
5, 6, 7, 8, 9
```

A **large number** contains only large digits.

Examples:

``` text
555
679
998
```

are large.

But:

``` text
504
123
```

are not.

Given `x`, determine whether:

``` text
x = A + B
```

where:

``` text
A and B are both large
and
A and B have the same number of digits
```

------------------------------------------------------------------------

## 4.2 Math Preliminary - What Can Two Large Digits Sum To?

Each digit is from `5` to `9`.

Minimum:

``` text
5 + 5 = 10
```

Maximum:

``` text
9 + 9 = 18
```

Therefore without incoming carry:

``` text
two large digits sum to [10 ... 18]
```

This means:

``` text
there is ALWAYS a carry 1
```

to the next position.

That single observation controls the entire problem.

------------------------------------------------------------------------

## 4.3 Units Digit

The rightmost digit has no incoming carry.

So:

``` text
digitA + digitB
```

is between:

``` text
10 and 18
```

Possible result digits are:

``` text
0,1,2,3,4,5,6,7,8
```

Therefore the last digit of `x` can **never be 9**.

Condition:

``` text
last digit != 9
```

------------------------------------------------------------------------

## 4.4 Middle Digits

After the units column, there is always carry `1`.

So every middle column is:

``` text
largeDigitA + largeDigitB + 1
```

Minimum:

``` text
5 + 5 + 1 = 11
```

Maximum:

``` text
9 + 9 + 1 = 19
```

So middle-column totals are:

``` text
11 ... 19
```

Their written digits are:

``` text
1 ... 9
```

Therefore:

``` text
a middle digit can NEVER be 0
```

Condition:

``` text
every middle digit must be from 1 to 9
```

------------------------------------------------------------------------

## 4.5 Most Significant Digit

Every column creates a carry `1`.

After the final column, that carry becomes a new leading digit.

Therefore:

``` text
first digit of x must be 1
```

So the three structural conditions are:

``` text
1. first digit = 1
2. every middle digit != 0
3. last digit != 9
```

------------------------------------------------------------------------

## 4.6 Detailed Dry Run - 1337

Can `1337` be written as the sum of two large numbers?

Check structure:

``` text
first digit = 1       -> valid

middle digits = 3, 3
both non-zero         -> valid

last digit = 7
7 != 9                -> valid
```

So:

``` text
YES
```

Actual construction from the problem:

``` text
  658
+ 679
-----
 1337
```

Column dry run:

### Units

``` text
8 + 9 = 17

write 7
carry 1
```

### Tens

``` text
5 + 7 + 1
= 13

write 3
carry 1
```

### Hundreds

``` text
6 + 6 + 1
= 13

write 3
carry 1
```

Final carry:

``` text
1
```

Result:

``` text
1337
```

------------------------------------------------------------------------

## 4.7 Dry Run - 119

Check:

``` text
first digit = 1 -> good
middle digit = 1 -> good
last digit = 9 -> impossible
```

Why is `9` impossible at the end?

Units must be:

``` text
a + b
```

where:

``` text
5 <= a,b <= 9
```

So:

``` text
10 <= a+b <= 18
```

The possible units digits are:

``` text
0 ... 8
```

Never `9`.

Answer:

``` text
NO
```

------------------------------------------------------------------------

## 4.8 Dry Run - 200

``` text
first digit = 2
```

But the final carry must be exactly:

``` text
1
```

Already impossible.

Also the middle digit is `0`, which is impossible for a column with
incoming carry.

Answer:

``` text
NO
```

------------------------------------------------------------------------

## 4.9 ASCII Carry Model

``` text
        large digits only
             |
             v
         each >= 5
             |
             v
units:    5+5 ... 9+9
           10 ... 18
             |
             +--> written digit = 0...8
             |
             +--> carry = 1
                       |
                       v
middle:  5+5+1 ... 9+9+1
           11 ... 19
             |
             +--> written digit = 1...9
             |
             +--> carry = 1
                       |
                       v
              final leading 1
```

Therefore:

``` text
x must look like:

1 [non-zero digits ...] [0..8]
```

------------------------------------------------------------------------

## 4.10 C++17

Using a string makes digit checking simple.

``` cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;

    while (t--) {
        string s;
        cin >> s;

        bool ok = true;

        // Final carry must create leading 1.
        if (s.front() != '1')
            ok = false;

        // Units digit cannot be 9.
        if (s.back() == '9')
            ok = false;

        // Every internal result digit must be 1..9.
        for (int i = 1; i + 1 < (int)s.size(); ++i) {
            if (s[i] == '0')
                ok = false;
        }

        cout << (ok ? "YES" : "NO") << '\n';
    }
}
```

### Complexity

If `d` is the number of digits:

``` text
Time:  O(d)
Space: O(1) extra
```

------------------------------------------------------------------------

## 4.11 Recognition Model

Do not try to construct both numbers first.

When you see:

``` text
digits restricted to [L ... R]
+
addition
+
ask whether result is possible
```

think:

``` text
find min/max column sum
+
study carry
+
derive allowed result digits
```

For this problem:

``` text
5+5 = 10
9+9 = 18
```

immediately tells us:

``` text
carry is always 1
```

Then reason digit-by-digit.

------------------------------------------------------------------------

# 5. Final Recognition Sheet

## Problem 1 - Fixed Gap / Upload More RAM

``` text
n repeated events
fixed legal spacing k
minimum finishing time
        |
        v
Arithmetic Progression
        |
        v
answer = 1 + (n-1)k
```

------------------------------------------------------------------------

## Problem 2 - Maximum Multiple Sum

``` text
x + 2x + ... + kx
        |
        v
factor x
        |
        v
x(1 + 2 + ... + k)
        |
        v
k = floor(n/x)
        |
        v
sum = x × k(k+1)/2
```

------------------------------------------------------------------------

## Problem 3 - Large Addition

``` text
digits are only 5...9
        |
        v
min sum = 10
max sum = 18
        |
        v
carry always = 1
        |
        +--> leading digit must be 1
        |
        +--> middle digits cannot be 0
        |
        +--> last digit cannot be 9
```

------------------------------------------------------------------------

# Contest Thinking Pattern

Before coding, ask:

``` text
1. What exactly is changing?
2. What are the variables?
3. Is there a sequence?
4. Is the sequence an AP?
5. Am I summing x,2x,3x,...?
6. Can I count terms instead of simulating?
7. Are decimal digits restricted?
8. What are the minimum and maximum possible digit sums?
9. What carry is forced?
10. Can the whole simulation collapse into O(1) or O(n)?
```

> **Core lesson:** The lecture problems are not about memorizing three
> tricks. They train you to replace simulation with a small mathematical
> model.
