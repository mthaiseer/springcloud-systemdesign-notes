# Ad-hoc & Patterns (Level 2) — Comprehensive Competitive Programming Notes

> Lecture-derived pattern notes for fast revision and contest recognition.  
> The six problems below combine the two supplied TLE Ad-hoc & Patterns lecture PDFs.

## Table of Contents

- [Problem 1: Modulo Summation](#problem-1-modulo-summation)
- [Problem 2: Rectangle Filling](#problem-2-rectangle-filling)
- [Problem 3: Missing Coin Sum](#problem-3-missing-coin-sum)
- [Problem 4: Multiple Powers of Two](#problem-4-multiple-powers-of-two)
- [Problem 5: Stick Lengths](#problem-5-stick-lengths)
- [Problem 6: Odd Grasshopper](#problem-6-odd-grasshopper)
- [Final Pattern Recognition Sheet](#final-pattern-recognition-sheet)

---

### Problem 1: Modulo Summation

**Problem Title:** Modulo Summation  
**Source Link:** https://atcoder.jp/contests/abc103/tasks/abc103_c

#### Problem Overview

Given `N` positive integers `a_1,a_2,\ldots,a_N`, consider


```text
f(m)=sum_{i=1}^{N}(mmod a_i).
```


Find the maximum possible value of `f(m)` over positive integer choices of `m`.

#### Core Observation / Pattern

For every positive integer `a_i`,


```text
0<= mmod a_i<= a_i-1.
```


Therefore the theoretical maximum contribution of the `i`-th term is


```text
a_i-1.
```


The key ad-hoc observation is that **all terms can reach their individual maximum simultaneously**.

Choose


```text
m=<=ft(product_{i=1}^{N}a_i)-1.
```


Since `product a_i` is divisible by every `a_i`,


```text
m\equiv -1\pmod{a_i}.
```


Hence


```text
mmod a_i=a_i-1.
```


Therefore


```text
max f(m)
=sum_{i=1}^{N}(a_i-1)
=sum_{i=1}^{N}a_i-N.
```


**Important:** We never actually need to calculate `product a_i`. It is only a proof that such an `m` exists.

#### Step-by-Step Dry Run

Take:

```text
N = 3
a = [3, 4, 6]
```

Individual maximum remainders:

| `a_i` | Maximum possible `mmod a_i` |
|---:|---:|
| `3` | `2` |
| `4` | `3` |
| `6` | `5` |

Choose conceptually:


```text
m=(3*4*6)-1=71.
```


Then:

| Expression | Result |
|---|---:|
| `71mod3` | `2` |
| `71mod4` | `3` |
| `71mod6` | `5` |

Thus:


```text
f(71)=2+3+5=10.
```


Direct formula:


```text
(3+4+6)-3=13-3=10.
```


**Edge case:** If `a=[1]`, then


```text
1-1=0,
```


which is correct because every integer modulo `1` is `0`.

#### Algorithm

1. Read `N`.
2. Read every `a_i`.
3. Accumulate `sum a_i`.
4. Output `sum a_i-N`.

#### Pseudocode

```text
read N
sum = 0

repeat N times:
    read x
    sum = sum + x

answer = sum - N
print answer
```

#### Complexity

Sorting or searching is unnecessary.


```text
T(N)=O(N)
```


because every value is read once.


```text
S(N)=O(1)
```


extra space if values are accumulated directly.

#### Complete C++ Implementation

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    long long sum = 0;

    for (int i = 0; i < n; ++i) {
        long long x;
        cin >> x;
        sum += x;
    }

    // Maximum = Σ(ai - 1) = Σai - n.
    cout << sum - n << '\n';

    return 0;
}
```

#### Recognition Trigger

```text
maximize Σ(m mod ai)
        ↓
each remainder ≤ ai - 1
        ↓
can all maxima happen together?
        ↓
choose a common multiple - 1
        ↓
answer = Σ(ai - 1)
```

---

### Problem 2: Rectangle Filling

**Problem Title:** Rectangle Filling  
**Source Link:** https://codeforces.com/contest/1966/problem/B

#### Problem Overview

A grid contains only two colors, `W` and `B`. An operation chooses two cells of the **same color** and recolors every cell in the axis-aligned rectangle determined by those cells with that color.

Determine whether it is possible, after any number of operations, to make the whole grid one color.

#### Core Observation / Pattern

The decisive information lies on **opposite borders**, not in the interior.

An impossible configuration exists if either:

1. the entire top row is one color and the entire bottom row is the opposite color, or
2. the entire left column is one color and the entire right column is the opposite color.

Equivalent test:

```text
If top-left and bottom-right have the same color -> YES.
If top-right and bottom-left have the same color -> YES.

Otherwise inspect whether opposite borders can provide
matching-color endpoints.
```

A compact standard implementation checks whether each color occurring on one opposite border can also occur on the other.

For the row condition, there must be some color that appears in both the first and last rows.  
For the column condition, there must be some color that appears in both the first and last columns.

If either orientation supplies such matching endpoints, the grid can be filled.

#### Border View

```text
      FIRST ROW
  +---------------+
  |               |
L |               | R
E |               | I
F |               | G
T |               | H
  |               | T
  +---------------+
       LAST ROW
```

The interior does not need exhaustive simulation.

#### Dry Run 1 — Impossible Opposite Rows

```text
WWW
BWB
BBB
```

First row:

```text
WWW
```

Last row:

```text
BBB
```

They have no common color.

Now inspect columns:

```text
left  = W B B
right = W B B
```

Both columns contain common colors, so a valid border pairing exists through columns; this case is therefore not rejected solely by the row mismatch.

The lesson is important:

> A bad pair of rows alone is not enough. We need the border conditions that prevent both orientations.

#### Dry Run 2 — Corner Shortcut

```text
BWW
WBW
WWB
```

Corners:

```text
top-left     = B
bottom-right = B
```

They already form same-colored opposite rectangle corners.

```text
answer = YES
```

#### Robust Algorithm

Track which colors occur on each boundary.

```text
topW, topB
bottomW, bottomB
leftW, leftB
rightW, rightB
```

There is a usable horizontal pair if:


```text
(top has W}ANDbottom has W})
OR
(top has B}ANDbottom has B}).
```


There is a usable vertical pair if:


```text
(left has W}ANDright has W})
OR
(left has B}ANDright has B}).
```


The answer is `YES` when both necessary opposite-side color compatibility conditions are not blocked; equivalently, reject the known impossible situation where one pair of opposite borders is forced to opposite monochromatic colors.

A particularly concise implementation uses the four corners plus monochromatic-border tests.

#### Pseudocode

```text
for each test case:
    read n, m
    read grid

    badRows =
        first row is monochromatic
        AND last row is monochromatic
        AND their colors differ

    badCols =
        first column is monochromatic
        AND last column is monochromatic
        AND their colors differ

    if badRows OR badCols:
        print "NO"
    else:
        print "YES"
```

#### Why This Works

If an entire extreme border is color `X` and the opposite extreme border is entirely color `Y\ne X`, there is no pair of same-colored cells spanning those two extremes. Consequently no rectangle operation can bridge that full dimension in the required color.

If this obstruction does not exist for either dimension, the boundary supplies the matching-color structure required by the operation.

#### Complexity

Every cell is read once and the four borders are inspected.


```text
T=O(NM)
```



```text
S=O(NM)
```


for storing the grid. It can also be implemented with reduced auxiliary storage.

#### Complete C++ Implementation

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int T;
    cin >> T;

    while (T--) {
        int n, m;
        cin >> n >> m;

        vector<string> g(n);

        for (string &row : g) {
            cin >> row;
        }

        bool topSame = true;
        bool bottomSame = true;

        for (int j = 1; j < m; ++j) {
            if (g[0][j] != g[0][0]) {
                topSame = false;
            }

            if (g[n - 1][j] != g[n - 1][0]) {
                bottomSame = false;
            }
        }

        bool leftSame = true;
        bool rightSame = true;

        for (int i = 1; i < n; ++i) {
            if (g[i][0] != g[0][0]) {
                leftSame = false;
            }

            if (g[i][m - 1] != g[0][m - 1]) {
                rightSame = false;
            }
        }

        bool badRows =
            topSame &&
            bottomSame &&
            g[0][0] != g[n - 1][0];

        bool badCols =
            leftSame &&
            rightSame &&
            g[0][0] != g[0][m - 1];

        cout << ((badRows || badCols) ? "NO" : "YES") << '\n';
    }

    return 0;
}
```

#### Recognition Trigger

```text
grid operation paints rectangles
        ↓
do not simulate operations
        ↓
ask what cannot be changed / crossed
        ↓
inspect extreme rows and columns
        ↓
opposite monochromatic borders of different colors -> NO
```

---

### Problem 3: Missing Coin Sum

**Problem Title:** Missing Coin Sum  
**Source Link:** https://cses.fi/problemset/task/2183

#### Problem Overview

Given `N` positive coin values, each coin may be used at most once. Find the smallest positive integer that cannot be represented as a subset sum.

#### Core Observation / Greedy Invariant

Maintain:


```text
X=smallest positive sum that cannot currently be formed}.
```


Equivalently, before processing the next coin, we can construct every sum in:


```text
[1,X-1].
```


Initially:


```text
X=1.
```


Sort the coins.

For next coin `a_i`:

- If `a_i>X`, then `X` cannot be formed.
- If `a_i<= X`, the reachable interval expands.

Before adding `a_i`:


```text
[1,X-1]
```


is reachable.

Using `a_i`, we can additionally reach:


```text
[a_i,a_i+X-1].
```


When `a_i<= X`, these intervals touch or overlap, giving:


```text
[1,X+a_i-1].
```


Therefore update:


```text
X<=ftarrow X+a_i.
```


#### Step-by-Step Dry Run

Input:

```text
5
1 2 2 7 9
```

Already sorted:

```text
[1, 2, 2, 7, 9]
```

Start:

```text
X = 1
reachable = empty
```

| Coin | Current `X` | Check | New reachable interval | New `X` |
|---:|---:|---|---|---:|
| `1` | `1` | `1<=1` | `[1,1]` | `2` |
| `2` | `2` | `2<=2` | `[1,3]` | `4` |
| `2` | `4` | `2<=4` | `[1,5]` | `6` |
| `7` | `6` | `7>6` | gap at `6` | stop |

Answer:


```text
6.
```


Why can `6` not be formed?

The processed coins total only:


```text
1+2+2=5.
```


The next coin is already `7`, so there is no way to bridge the gap.

#### Edge Cases

If the smallest coin is greater than `1`:

```text
coins = [2, 3, 10]
```

then immediately:


```text
X=1
```


and


```text
2>1.
```


Answer:


```text
1.
```


If no gap occurs during processing, final `X` is the answer.

#### Algorithm

1. Sort the coins.
2. Set `X=1`.
3. For each coin:
   - if `a_i>X`, stop and output `X`;
   - otherwise set `X=X+a_i`.
4. Output `X`.

#### Pseudocode

```text
read N
read array A

sort A

X = 1

for coin in A:
    if coin > X:
        break

    X = X + coin

print X
```

#### Complexity

Sorting dominates:


```text
T(N)=O(N\log N).
```


The scan is:


```text
O(N).
```


If the array is stored:


```text
S(N)=O(N).
```


#### Complete C++ Implementation

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<long long> coins(n);

    for (long long &coin : coins) {
        cin >> coin;
    }

    sort(coins.begin(), coins.end());

    // All values in [1, missing - 1] are currently constructible.
    long long missing = 1;

    for (long long coin : coins) {
        // A gap appears: 'missing' cannot be formed.
        if (coin > missing) {
            break;
        }

        // Extend constructible interval:
        // [1, missing - 1] -> [1, missing + coin - 1].
        missing += coin;
    }

    cout << missing << '\n';

    return 0;
}
```

#### Recognition Trigger

```text
smallest impossible subset sum
        ↓
sort
        ↓
maintain continuous reachable interval [1, X-1]
        ↓
ai <= X -> extend
ai > X  -> X is first gap
```

---

### Problem 4: Multiple Powers of Two

**Problem Title:** Multiple Powers of Two  
**Source Link:** https://www.hackerrank.com/contests/dcc-lab-30-jan/challenges/multiple-powers-of-two/problem

#### Problem Overview

The lecture studies repeated queries involving powers of two and array elements, with the key transformation based on numbers divisible by `2^x` and adding:


```text
2^{x-1}.
```


A direct implementation can repeatedly scan all `N` elements for every query, producing roughly `O(NQ)` work. The lecture develops a bit-pattern optimization.

> The supplied PDF is diagram-heavy and does not contain a clean machine-readable full statement. The notes below preserve the lecture's supported transformation and optimization rather than inventing omitted input constraints.

#### Core Bit Observation

Divisibility by:


```text
2^x
```


means the binary representation has at least `x` trailing zero bits.

Example:

```text
x = 3

divisible by 2^3 = 8
        ↓
binary ends in at least 3 zeroes

...xxxxx000
```

The update uses:


```text
2^{x-1}.
```


Adding this value to a number divisible by `2^x` changes the trailing pattern:

```text
before: ...0000
add:    ...0100    (example x = 3)
after:  ...0100
```

After this update, the resulting number is no longer divisible by the same or any larger relevant power in the same way.

#### Query-Dominance Observation

The lecture maintains a threshold such as the smallest processed exponent.

If a new query exponent is not smaller than an already effective exponent, it may have no new effect and can be skipped.

Example query sequence from the lecture style:

```text
29 30 31 4 6 2
✓  ×  ×  ✓ × ✓
```

The useful exponents form a strictly decreasing sequence.

Why?

After processing exponent `x`, affected values gain the bit:


```text
2^{x-1}.
```


Therefore later queries with exponent at least `x` do not newly satisfy the required divisibility condition for those already transformed values.

This collapses the number of effective queries to at most the number of bit positions.

#### Bit-by-Bit Example

Suppose:

```text
value = 24
x = 3
```

Binary:

```text
24 = 11000₂
```

It has at least three trailing zeroes:

```text
11000
   ^^^
```

so it is divisible by:


```text
2^3=8.
```


Add:


```text
2^{3-1}=4.
```


Binary:

```text
11000
00100
-----
11100
```

Decimal:


```text
24+4=28.
```


Now the lower-bit structure has changed, which explains why subsequent larger/equal exponent queries can become redundant.

#### Optimized Algorithm

1. Read the array.
2. Process query exponents in order.
3. Keep `minProcessed`, initially larger than every possible exponent.
4. If `x>=minProcessed}`, skip the query.
5. Otherwise:
   - for every array value divisible by `2^x`,
   - add `2^{x-1}`;
   - set `minProcessed = x`.
6. Print the final array.

The divisibility test can be written as:


```text
a_imod 2^x=0
```


or via an appropriate low-bit mask.

#### Pseudocode

```text
read N, Q
read A
read queries

minProcessed = infinity

for x in queries:

    if x >= minProcessed:
        continue

    add = 2^(x - 1)
    divisor = 2^x

    for i = 0 to N - 1:
        if A[i] is divisible by divisor:
            A[i] = A[i] + add

    minProcessed = x

print A
```

#### Complexity

If the integer domain contains `B` relevant bits, only a strictly decreasing sequence of exponents is processed.

Thus the expensive scan occurs at most `B` times:


```text
T=O(NB+Q).
```


For 32-bit-style exponents, `B` is a small constant around `31`.

Extra space:


```text
O(1)
```


apart from the input arrays.

#### Complete C++ Implementation

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int T;
    cin >> T;

    while (T--) {
        int n, q;
        cin >> n >> q;

        vector<long long> a(n);

        for (long long &x : a) {
            cin >> x;
        }

        // Any exponent >= minProcessed is redundant.
        int minProcessed = 31;

        while (q--) {
            int x;
            cin >> x;

            if (x >= minProcessed) {
                continue;
            }

            long long divisor = 1LL << x;
            long long add = 1LL << (x - 1);

            for (long long &value : a) {
                if (value % divisor == 0) {
                    value += add;
                }
            }

            minProcessed = x;
        }

        for (int i = 0; i < n; ++i) {
            if (i) {
                cout << ' ';
            }

            cout << a[i];
        }

        cout << '\n';
    }

    return 0;
}
```

#### Recognition Trigger

```text
queries use powers of two
        ↓
divisible by 2^x
        ↓
look at trailing zeroes
        ↓
update inserts bit x-1
        ↓
many future exponents become redundant
        ↓
keep only strictly decreasing effective x values
```

---

### Problem 5: Stick Lengths

**Problem Title:** Stick Lengths  
**Source Link:** https://cses.fi/problemset/task/1074

#### Problem Overview

Given `N` stick lengths, one operation changes a stick length by `1` at cost `1`. Make all sticks equal while minimizing total cost.

For target length `x`, the cost is:


```text
C(x)=sum_{i=1}^{N}|a_i-x|.
```


#### Core Observation / Pattern

The value minimizing the sum of absolute deviations is a **median**.

Therefore:

1. sort the values;
2. choose a median;
3. sum absolute distances to it.

For odd `N`, the median is unique.

For even `N`, every integer between the two middle values minimizes the `L_1` cost. Choosing `a[n/2]` is sufficient.

#### Why Median?

Imagine every value pulls the target toward itself with unit force.

Moving `x` one step to the right:

- distance to every point on the left increases by `1`;
- distance to every point on the right decreases by `1`.

The minimum occurs around the equilibrium where neither side has more than half the points — precisely the median region.

#### Step-by-Step Dry Run

Input:

```text
5
2 3 1 5 2
```

Sort:

```text
[1, 2, 2, 3, 5]
```

Median:

```text
index = 5 / 2 = 2
median = 2
```

Distances:

| Stick | `|a_i-2|` |
|---:|---:|
| `1` | `1` |
| `2` | `0` |
| `2` | `0` |
| `3` | `1` |
| `5` | `3` |

Total:


```text
1+0+0+1+3=5.
```


#### Compare With a Bad Target

Choose `x=3`:


```text
|1-3|+|2-3|+|2-3|+|3-3|+|5-3|
```



```text
=2+1+1+0+2=6.
```


Median gives the lower cost.

#### Even-Length Example

```text
[1, 2, 10, 12]
```

Middle values:

```text
2 and 10
```

Any `xin[2,10]` has the same minimum total absolute-distance cost.

So selecting:

```text
a[n / 2] = 10
```

is valid.

#### Pseudocode

```text
read N
read A

sort A

median = A[N / 2]

answer = 0

for value in A:
    answer += abs(value - median)

print answer
```

#### Complexity

Sorting:


```text
O(N\log N).
```


Distance accumulation:


```text
O(N).
```


Total:


```text
O(N\log N).
```


Array storage:


```text
O(N).
```


#### Complete C++ Implementation

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<long long> a(n);

    for (long long &x : a) {
        cin >> x;
    }

    sort(a.begin(), a.end());

    // Any median minimizes Σ|ai - x|.
    long long median = a[n / 2];

    long long cost = 0;

    for (long long x : a) {
        cost += llabs(x - median);
    }

    cout << cost << '\n';

    return 0;
}
```

#### Recognition Trigger

```text
choose one value x
minimize Σ |ai - x|
        ↓
L1 / absolute distance
        ↓
MEDIAN
```

---

### Problem 6: Odd Grasshopper

**Problem Title:** Odd Grasshopper  
**Source Link:** https://codeforces.com/problemset/problem/1607/B

#### Problem Overview

A grasshopper starts at coordinate `x_0`. During jump number `i`:

- if its current coordinate is even, it moves left by `i`;
- if its current coordinate is odd, it moves right by `i`.

Find its coordinate after `n` jumps.

The constraints are large, so simulating every jump is unnecessary.

#### Core Observation / Pattern

The movement has a period of `4`.

Start by understanding the relative displacement for a convenient base such as `x_0=0`.

Since `0` is even:

```text
jump 1: 0 -> -1
jump 2: -1 is odd  -> +2 => 1
jump 3: 1 is odd   -> +3 => 4
jump 4: 4 is even  -> -4 => 0
```

After four jumps:

```text
0 -> -1 -> 1 -> 4 -> 0
```

The relative position returns to the start.

Thus only:


```text
nmod4
```


matters.

#### Relative Displacement for Even Start

Let `r=nmod4`.

The lecture's cycle gives:


```text
d(n)=

0,&r=0\\
-n,&r=1\\
1,&r=2\\
n+1,&r=3

```


for an even initial coordinate.

For an odd initial coordinate, directions reverse, so the displacement is negated.

Therefore:


```text
answer}=

x_0+d(n),&x_0 even}\\
x_0-d(n),&x_0 odd}.

```


#### Step-by-Step Dry Run 1

```text
x0 = 0
n  = 7
```

Compute:


```text
7mod4=3.
```


For an even start and remainder `3`:


```text
d=n+1=8.
```


Therefore:


```text
x=0+8=8.
```


Direct check:

```text
start = 0

1: even -> 0 - 1 = -1
2: odd  -> -1 + 2 = 1
3: odd  -> 1 + 3 = 4
4: even -> 4 - 4 = 0

5: even -> 0 - 5 = -5
6: odd  -> -5 + 6 = 1
7: odd  -> 1 + 7 = 8
```

Correct:


```text
8.
```


#### Step-by-Step Dry Run 2 — Odd Start

```text
x0 = 5
n  = 3
```

Since:


```text
3mod4=3,
```


base displacement magnitude:


```text
d=n+1=4.
```


But `x_0` is odd, so reverse the even-start direction:


```text
x=5-4=1.
```


Direct simulation:

```text
start = 5

jump 1:
5 is odd
5 + 1 = 6

jump 2:
6 is even
6 - 2 = 4

jump 3:
4 is even
4 - 3 = 1
```

Matches the formula.

#### Remainder Table

| `nmod4` | Relative displacement for even `x_0` |
|---:|---:|
| `0` | `0` |
| `1` | `-n` |
| `2` | `+1` |
| `3` | `+(n+1)` |

For odd `x_0`, flip the sign.

#### Pseudocode

```text
read x0, n

r = n mod 4

if r == 0:
    d = 0
else if r == 1:
    d = -n
else if r == 2:
    d = 1
else:
    d = n + 1

if x0 is even:
    answer = x0 + d
else:
    answer = x0 - d

print answer
```

#### Complexity

Each test case uses a constant number of arithmetic operations:


```text
T=O(1).
```


Extra space:


```text
S=O(1).
```


#### Complete C++ Implementation

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int T;
    cin >> T;

    while (T--) {
        long long x0, n;
        cin >> x0 >> n;

        long long displacement = 0;

        switch (n % 4) {
            case 0:
                displacement = 0;
                break;

            case 1:
                displacement = -n;
                break;

            case 2:
                displacement = 1;
                break;

            case 3:
                displacement = n + 1;
                break;
        }

        // Odd starting coordinates reverse the direction pattern.
        if (x0 & 1LL) {
            displacement = -displacement;
        }

        cout << x0 + displacement << '\n';
    }

    return 0;
}
```

#### Recognition Trigger

```text
huge number of deterministic jumps
direction depends on parity
        ↓
write first few transitions
        ↓
look for periodicity
        ↓
cycle length = 4
        ↓
reduce n using n % 4
```

---

## Final Pattern Recognition Sheet

| Problem | Main Pattern | Contest Recognition Signal |
|---|---|---|
| Modulo Summation | Simultaneous modulo maximum | Each `mmod a_i` is bounded by `a_i-1` |
| Rectangle Filling | Boundary invariant / impossibility | Rectangle operations + binary-colored grid |
| Missing Coin Sum | Greedy reachable interval | Smallest positive subset sum that cannot be formed |
| Multiple Powers of Two | Trailing-zero / redundant-query pattern | Divisibility by `2^x` under repeated queries |
| Stick Lengths | Median / `L_1` minimization | Minimize `sum|a_i-x|` |
| Odd Grasshopper | Periodicity / modulo classes | Huge deterministic process with parity-dependent moves |

### Fast Mental Checklist

```text
MODULO
Can every term reach its individual maximum simultaneously?

GRID OPERATIONS
What boundary configuration is impossible to overcome?

SUBSET SUM — SMALLEST MISSING
Can I maintain one continuous reachable interval?

POWERS OF TWO
What does divisibility mean in binary?
Do earlier updates make later queries redundant?

ABSOLUTE DIFFERENCE
Σ|ai - x| -> think MEDIAN.

REPEATED DETERMINISTIC PROCESS
Write 4–8 steps.
Does the state repeat?
Can n be reduced modulo the cycle length?
```

---

# End of Notes
