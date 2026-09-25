# Constructive Algorithms + Mathematical Modelling --- Visual CP Notes

This note covers the four problems discussed in the lecture/session:

1.  **CF 1862E --- Kolya and Movie Theatre**
2.  **CF 1844D --- Row Major**
3.  **CF 1873G --- ABBC or BACB**
4.  **CF 1809C --- Sum on Subarrays**

The goal is not only to memorize solutions. For each problem we identify
the **signal**, the **constructive/modelling technique**, turn the
statement into mathematics, prove the observation, dry-run an example,
and finish with C++ code.

------------------------------------------------------------------------

# 0. Master Constructive / Modelling Toolbox

When reading a long Codeforces problem, use this pipeline:

``` text
LONG STATEMENT
      |
      v
1. What objects do I have?
      |
      v
2. What can I choose / change?
      |
      v
3. What does ONE operation really do?
      |
      v
4. What quantity changes / stays invariant?
      |
      v
5. Write the objective mathematically
      |
      v
6. Simplify / telescope / group / reduce dimension
      |
      v
7. Identify the constructive signal
      |
      v
8. Build an easy bulk contribution
      |
      v
9. Engineer remainder / boundary
      |
      v
10. Prove no accidental cases occur
```

## Signals seen in these four problems

  -----------------------------------------------------------------------
  Problem                 Main signal             Technique
  ----------------------- ----------------------- -----------------------
  1862E                   repeated distance       **Telescoping + fix
                          penalty                 endpoint + maintain
                                                  best prefix choices**

  1844D                   string must work for    **Dimension reduction +
                          every grid dimension    divisibility + periodic
                                                  construction**

  1873G                   local replacement       **Operation decoding +
                          operations              resource consumption +
                                                  group compression +
                                                  greedy sacrifice**

  1809C                   need exactly `k`        **Known-count building
                          objects/subarrays       block + target
                                                  decomposition +
                                                  engineer remainder +
                                                  barrier**
  -----------------------------------------------------------------------

------------------------------------------------------------------------

# 1. CF 1862E --- Kolya and Movie Theatre

Problem: https://codeforces.com/problemset/problem/1862/E

Tags: constructive algorithms, data structures, greedy.

## 1.1 Statement stripped to mathematics

There are `n` days.

Movie `i` has entertainment value:

``` text
a[i]
```

We may visit at most `m` movies.

If the previous visit was on day `prev` and the current visit is on day
`i`, this visit contributes:

``` text
a[i] - d * (i - prev)
```

Initially:

``` text
prev = 0
```

Goal:

``` text
maximize total entertainment
```

------------------------------------------------------------------------

## 1.2 Signal

The suspicious part is the repeated penalty:

``` text
(i1 - 0)
(i2 - i1)
(i3 - i2)
...
```

Whenever consecutive differences are being added, test for
**telescoping**.

``` text
SIGNAL:
"cost depends on distance from previous selected position"

TRY:
write the complete sum before designing an algorithm
```

------------------------------------------------------------------------

## 1.3 Mathematical modelling

Suppose we choose:

``` text
x1 < x2 < x3 < ... < xk
```

where:

``` text
k <= m
```

Total score:

``` text
(a[x1] - d(x1 - 0))
+
(a[x2] - d(x2 - x1))
+
...
+
(a[xk] - d(xk - x{k-1}))
```

Separate values and penalties:

``` text
a[x1] + a[x2] + ... + a[xk]

-

d * [
    (x1 - 0)
  + (x2 - x1)
  + ...
  + (xk - x{k-1})
]
```

Now telescope:

``` text
(x1 - 0)
+ (x2 - x1)
+ (x3 - x2)
+ ...
+ (xk - x{k-1})

= xk
```

Everything in the middle cancels:

``` text
+x1 -x1
+x2 -x2
+x3 -x3
...
```

Therefore:

``` text
Score = sum(selected a[i]) - d * lastSelectedDay
```

This is the major simplification.

------------------------------------------------------------------------

## 1.4 Fix the last selected movie

Suppose movie `i` is the last selected movie.

Then we definitely take:

``` text
a[i]
```

and pay:

``` text
d * i
```

Before `i`, we can select at most:

``` text
m - 1
```

movies.

Since the penalty no longer depends on which earlier days were selected,
the best earlier choices are simply:

``` text
largest positive (m - 1) values from a[1 ... i-1]
```

So:

``` text
candidate(i)
=
a[i] - d*i
+
sum(best m-1 positive previous values)
```

And:

``` text
answer = max(0, candidate(i) for every i)
```

`0` is possible because we may choose no movies.

------------------------------------------------------------------------

## 1.5 Data-structure signal

As `i` moves:

``` text
prefix grows one element at a time
```

We repeatedly need:

``` text
largest m-1 positive values in current prefix
+
their sum
```

Maintain a multiset containing those values.

``` text
insert new positive value

if size > m-1:
    remove smallest
```

Why remove smallest?

``` text
We are allowed only m-1 elements.
To maximize sum, discard the least valuable one.
```

------------------------------------------------------------------------

## 1.6 Important implementation order

At day `i`:

``` text
1. Calculate candidate using days < i
2. THEN insert a[i]
```

Not:

``` text
insert a[i]
then calculate
```

because movie `i` is already being used as the fixed last movie.

------------------------------------------------------------------------

## 1.7 Dry run

``` text
n = 5
m = 2
d = 2

a = [3, 2, 5, 4, 6]
```

Since:

``` text
m - 1 = 1
```

keep only the best one previous positive value.

### i = 1

``` text
best = {}
sum  = 0

candidate
= 0 + 3 - 2*1
= 1

ans = 1

insert 3

best = {3}
sum  = 3
```

### i = 2

``` text
candidate
= 3 + 2 - 2*2
= 1

ans = 1

insert 2

{2,3}

too many -> remove 2

best = {3}
sum = 3
```

### i = 3

``` text
candidate
= 3 + 5 - 2*3
= 2

ans = 2

insert 5

{3,5}

remove 3

best = {5}
sum = 5
```

### i = 4

``` text
candidate
= 5 + 4 - 8
= 1
```

### i = 5

``` text
candidate
= 5 + 6 - 10
= 1
```

Final:

``` text
answer = 2
```

------------------------------------------------------------------------

## 1.8 Mental model

``` text
LONG SEQUENTIAL COST
       |
       v
write complete objective
       |
       v
differences telescope
       |
       v
only LAST endpoint matters
       |
       v
fix last endpoint i
       |
       v
choose best m-1 values before i
       |
       v
maintain top values dynamically
```

### Constructive/modelling classification

``` text
PRIMARY:
Mathematical modelling / telescoping

SECONDARY:
Greedy selection

IMPLEMENTATION:
Top-k maintenance using multiset
```

------------------------------------------------------------------------

## 1.9 C++ code

``` cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;

    while (t--) {
        int n, m;
        long long d;
        cin >> n >> m >> d;

        vector<long long> a(n + 1);

        for (int i = 1; i <= n; i++) {
            cin >> a[i];
        }

        multiset<long long> best;
        long long sum = 0;
        long long ans = 0;

        for (int i = 1; i <= n; i++) {

            // best contains only values from indices < i
            long long candidate = sum + a[i] - d * i;

            ans = max(ans, candidate);

            // Add current movie for FUTURE endpoints.
            if (a[i] > 0) {
                best.insert(a[i]);
                sum += a[i];

                if ((int)best.size() > m - 1) {
                    auto it = best.begin();
                    sum -= *it;
                    best.erase(it);
                }
            }
        }

        cout << ans << '\n';
    }
}
```

Complexity:

``` text
O(n log m) per test-case collection
O(m) extra space
```

------------------------------------------------------------------------

# 2. CF 1844D --- Row Major

Problem: https://codeforces.com/problemset/problem/1844/D

Tags: constructive algorithms, greedy, math, number theory, strings.

## 2.1 Strip the statement

We need a lowercase string `s` of length `n`.

The string may be reshaped row-major into every possible:

``` text
r x c
```

grid satisfying:

``` text
r*c = n
```

A grid is bad if two edge-adjacent cells have equal characters.

We need a string that is never the row-major order of a bad grid.

Among all valid strings, minimize:

``` text
number of distinct characters
```

------------------------------------------------------------------------

## 2.2 Signal: dimension setup / dimension reduction

The statement is 2D.

The output is 1D.

Ask:

``` text
"What does 2D adjacency become
as an index difference in the string?"
```

Suppose:

``` text
c = number of columns
```

Row-major indices:

``` text
0       1       2       ... c-1
c       c+1     c+2     ... 2c-1
2c      2c+1    ...
```

Horizontal neighbors:

``` text
i ---- i+1

difference = 1
```

Vertical neighbors:

``` text
i
|
|
i+c

difference = c
```

Therefore:

``` text
2D ADJACENCY
     |
     v
1D INDEX DISTANCE

horizontal -> 1
vertical   -> c
```

------------------------------------------------------------------------

## 2.3 Bring in divisibility

A grid with `c` columns exists only when:

``` text
c | n
```

because:

``` text
r*c = n
```

Therefore dangerous distances are divisors of `n`.

Equivalent 1D requirement:

``` text
if d | n
and i+d < n

then:

s[i] != s[i+d]
```

The giant grid problem is now a divisibility problem.

------------------------------------------------------------------------

## 2.4 Find the smallest non-divisor

Let:

``` text
k = smallest positive integer such that k does NOT divide n
```

Then automatically:

``` text
1 | n
2 | n
...
(k-1) | n

but

k ∤ n
```

Example:

``` text
n = 8

1 | 8   yes
2 | 8   yes
3 | 8   no

k = 3
```

------------------------------------------------------------------------

## 2.5 Lower bound: why at least k letters?

Look at:

``` text
positions 0,1,2,...,k-1
```

Take any pair:

``` text
i < j
```

Their distance satisfies:

``` text
1 <= j-i <= k-1
```

Every number from `1` to `k-1` divides `n`.

Therefore:

``` text
s[i] != s[j]
```

for every pair among the first `k` positions.

ASCII:

``` text
0 ----- 1 ----- 2 ----- ... ----- k-1
|       |       |                  |
a       b       c                  ?

Every pair must differ.
```

Therefore we need at least `k` distinct characters (subject to the
trivial truncation when `n < k`, as happens for tiny `n`).

------------------------------------------------------------------------

## 2.6 Upper bound: periodic construction

Use:

``` text
s[i] = 'a' + (i % k)
```

Example:

``` text
k = 3

index:  0 1 2 3 4 5 6 7
char:   a b c a b c a b
```

Equal characters occur only at distances:

``` text
k, 2k, 3k, ...
```

Why are those safe?

Suppose:

``` text
q*k | n
```

Then:

``` text
k | n
```

must also be true.

But we chose:

``` text
k ∤ n
```

Contradiction.

Therefore no multiple of `k` is a dangerous divisor distance.

Construction is valid.

------------------------------------------------------------------------

## 2.7 Dry run: n = 8

``` text
divisibility:

1 | 8   yes
2 | 8   yes
3 | 8   no

k = 3
```

Construct:

``` text
abcabcab
```

ASCII:

``` text
index: 0 1 2 3 4 5 6 7
       a b c a b c a b

a:     0 ----- 3 ----- 6
b:       1 ----- 4 ----- 7
c:         2 ----- 5
```

Equal letters are separated by `3` or `6`.

``` text
3 ∤ 8
6 ∤ 8
```

Safe.

Minimum proof:

``` text
distances 1 and 2 both divide 8

positions:

0,1,2

must all be different

=> at least 3 letters
```

Our construction uses exactly 3.

Optimal.

------------------------------------------------------------------------

## 2.8 Another dry run: n = 6

``` text
1 | 6 yes
2 | 6 yes
3 | 6 yes
4 | 6 no

k = 4
```

Construct:

``` text
abcdab
```

Equal-character distance:

``` text
4
```

But:

``` text
4 ∤ 6
```

Safe.

------------------------------------------------------------------------

## 2.9 Mental model

``` text
GRID / MATRIX CONDITION
       |
       v
convert coordinates to index distances
       |
       v
possible width c satisfies c | n
       |
       v
dangerous distances = divisors of n
       |
       v
find smallest safe period
       |
       v
smallest k with k ∤ n
       |
       +----------------------+
       |                      |
       v                      v
lower bound              construction
1..k-1 divide n          repeat k letters
       |                      |
first k positions        equal distance = qk
must differ                   |
       |                 qk cannot divide n
       +----------+-----------+
                  |
                  v
                OPTIMAL
```

### Constructive/modelling classification

``` text
PRIMARY:
Dimension setup / dimension reduction

SECONDARY:
Number-theory modelling using divisibility

CONSTRUCTION:
Periodic pattern

PROOF:
Lower bound + matching upper bound
```

------------------------------------------------------------------------

## 2.10 C++ code

``` cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;

    while (t--) {
        int n;
        cin >> n;

        int k = 1;

        while (n % k == 0) {
            k++;
        }

        for (int i = 0; i < n; i++) {
            cout << char('a' + (i % k));
        }

        cout << '\n';
    }
}
```

Complexity:

``` text
O(n) output work
```

------------------------------------------------------------------------

# 3. CF 1873G --- ABBC or BACB

Problem: https://codeforces.com/problemset/problem/1873/G

Tags: constructive algorithms, greedy.

## 3.1 Decode ONE operation

Operations:

``` text
AB -> BC   +1 coin
BA -> CB   +1 coin
```

Don't simulate immediately.

Ask:

``` text
What permanently changes?
```

For:

``` text
AB -> BC
```

we had:

``` text
A B
```

after operation:

``` text
B C
```

One `A` disappeared.

Likewise:

``` text
BA -> CB
```

also destroys one `A`.

So:

``` text
1 operation = exactly 1 A consumed
1 consumed A = 1 coin
```

The original objective:

``` text
maximize coins
```

becomes:

``` text
maximize number of A's consumed
```

This is **operation decoding**.

------------------------------------------------------------------------

## 3.2 Interpret B as an eater

Consider:

``` text
AAAA B
```

The `B` can repeatedly consume toward the left.

Conceptually:

``` text
A A A A B
        ^
       eater

A A A [AB]
      -> A A A B C

A A [AB] C
    -> A A B C C

A [AB] C C
  -> A B C C C

[AB] C C C
-> B C C C C
```

Four `A`s consumed.

Similarly:

``` text
B AAAA
```

can be consumed toward the right.

Important structural observation:

``` text
A B can consume an A-block on one side,
but once it starts moving away from its original
position it cannot come back and eat both sides.
```

------------------------------------------------------------------------

## 3.3 Compress the string into A-groups

Instead of thinking character by character:

``` text
AAA B AA B AAAA B A
```

compress to:

``` text
[AAA] B [AA] B [AAAA] B [A]

sizes:

3, 2, 4, 1
```

Let A-group sizes be:

``` text
g1, g2, ..., gp
```

Total number of A's:

``` text
totalA = g1 + g2 + ... + gp
```

------------------------------------------------------------------------

## 3.4 Easy case: starts with B

Example:

``` text
B AAA B AA B AAAA
```

Each B can eat the group immediately after it:

``` text
B -> AAA
      B -> AA
             B -> AAAA
```

All A's can disappear.

``` text
answer = totalA
```

------------------------------------------------------------------------

## 3.5 Easy case: ends with B

``` text
AAA B AA B AAA B
```

Each B can eat toward the left.

Again:

``` text
answer = totalA
```

------------------------------------------------------------------------

## 3.6 Easy case: BB occurs

``` text
AAA B B AAA
```

Think:

``` text
AAA <- B | B -> AAA
```

The adjacent B's provide enough freedom to consume groups on both sides.

Equivalent group view:

``` text
AAA B [empty A-group] B AAA

sizes:

3, 0, 3
```

Minimum A-group size is `0`.

Therefore losing the smallest group costs nothing.

------------------------------------------------------------------------

## 3.7 Hard case

Suppose:

``` text
AAA B AA B AAAA B A
```

A-group sizes:

``` text
3, 2, 4, 1
```

There are:

``` text
4 A-groups
3 B's
```

One A-group must survive.

To maximize consumed A's:

``` text
sacrifice the smallest group
```

So:

``` text
totalA = 3+2+4+1 = 10

minimum group = 1

answer = 10 - 1 = 9
```

This is the greedy step.

------------------------------------------------------------------------

## 3.8 Unified formula

Include zero-length A-groups around/consecutive B's naturally while
scanning.

Then:

``` text
answer = totalA - minimum_A_group
```

This also handles:

### No B

``` text
AAAA

groups = [4]

totalA = 4
minGroup = 4

answer = 4 - 4 = 0
```

Correct: no operation is possible.

### Starts with B

``` text
BAABA

groups:

0, 2, 1

totalA = 3
minGroup = 0

answer = 3
```

### Contains BB

``` text
AABBA

groups:

2, 0, 1

totalA = 3
minGroup = 0

answer = 3
```

------------------------------------------------------------------------

## 3.9 Dry run: ABA

``` text
A B A
```

Groups:

``` text
1, 1
```

Total:

``` text
totalA = 2
```

Only one B:

``` text
A <- B -> A
```

It can effectively choose one side.

Smallest group:

``` text
1
```

Therefore:

``` text
answer = 2 - 1 = 1
```

------------------------------------------------------------------------

## 3.10 Dry run: BAABA

``` text
B AA B A
```

A-groups:

``` text
0, 2, 1
```

So:

``` text
totalA = 3
minimum = 0

answer = 3
```

------------------------------------------------------------------------

## 3.11 Mental model

``` text
LOCAL REPLACEMENT RULES
       |
       v
What resource disappears?
       |
       v
each operation consumes one A
       |
       v
coins = consumed A's
       |
       v
What enables consumption?
       |
       v
B can eat adjacent A-block
       |
       v
compress string into A-runs
       |
       v
all but possibly one run can be eaten
       |
       v
if one must survive:
sacrifice the smallest
       |
       v
answer = totalA - minRun
```

### Constructive/modelling classification

``` text
PRIMARY:
Operation decoding

SECONDARY:
Run/group compression

GREEDY:
Sacrifice minimum-loss group

USEFUL SIGNAL:
Replacement operation introduces a dead symbol C.
Ask what resource is permanently consumed.
```

------------------------------------------------------------------------

## 3.12 C++ code

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

        int totalA = 0;
        int currentRun = 0;
        int minRun = INT_MAX;

        for (char ch : s) {

            if (ch == 'A') {
                totalA++;
                currentRun++;
            } else {
                // This also records a zero-length run
                // when two B's are consecutive or
                // when the string starts with B.
                minRun = min(minRun, currentRun);
                currentRun = 0;
            }
        }

        // Final A-run. This is 0 when string ends in B.
        minRun = min(minRun, currentRun);

        cout << totalA - minRun << '\n';
    }
}
```

Complexity:

``` text
O(|s|)
O(1) extra space
```

------------------------------------------------------------------------

# 4. CF 1809C --- Sum on Subarrays

Problem: https://codeforces.com/problemset/problem/1809/C

Tags: constructive algorithms, greedy, math.

## 4.1 Decode the target

Given:

``` text
n = array length
k = exact number of positive-sum subarrays
```

Total non-empty subarrays:

``` text
T = n(n+1)/2
```

Need:

``` text
positive sum = exactly k
negative sum = T-k
zero sum     = 0
```

We control every array element.

This is a strong constructive signal:

``` text
"exactly k objects must satisfy a property"
```

Try to find a building block whose contribution can be counted exactly.

------------------------------------------------------------------------

## 4.2 Building block: positive prefix

Take:

``` text
[2,2,2,...,2]
```

of length `x`.

Every subarray completely inside it is positive.

Number of subarrays:

``` text
x + (x-1) + ... + 1

= x(x+1)/2
```

These are triangular numbers.

Example `x=3`:

``` text
[2 2 2]

length 1:
[2] [2] [2]          3

length 2:
[2 2] [2 2]          2

length 3:
[2 2 2]              1

total = 3+2+1 = 6
```

So we can cheaply create:

``` text
T_x = x(x+1)/2
```

positive subarrays.

------------------------------------------------------------------------

## 4.3 Decompose target k

Choose the largest `x` such that:

``` text
x(x+1)/2 <= k
```

Then:

``` text
k = x(x+1)/2 + r
```

where:

``` text
0 <= r <= x
```

Now the huge problem becomes:

``` text
create triangular bulk
+
create exactly r more
```

This is **target decomposition**.

------------------------------------------------------------------------

## 4.4 Engineer the remainder

After the positive prefix:

``` text
[2,2,2,...,2]
 <---- x ---->
```

append one special negative value:

``` text
-y
```

Consider only subarrays ending at this special element.

If such a subarray contains `j` twos, its sum is:

``` text
2j - y
```

As `j` increases:

``` text
-y
2-y
4-y
6-y
...
2x-y
```

This is a monotonic threshold.

We want exactly `r` of them positive.

Choose:

``` text
y = 2(x-r) + 1
```

Therefore special value is:

``` text
-(2(x-r)+1)
```

Check boundary.

For:

``` text
j = x-r
```

sum:

``` text
2(x-r) - [2(x-r)+1]

= -1
```

negative.

For the next:

``` text
j = x-r+1
```

sum:

``` text
2(x-r+1) - [2(x-r)+1]

= +1
```

positive.

Therefore exactly the final `r` suffixes become positive.

The `+1` is important:

``` text
it avoids a zero-sum subarray
```

------------------------------------------------------------------------

## 4.5 Kill everything afterward

We have already created exactly `k` positives.

Every remaining position can be:

``` text
-1000
```

Why?

`n <= 30`, and our earlier values are small, so `-1000` dominates any
sum containing it.

Thus no later subarray accidentally becomes positive.

This is the **barrier technique**.

------------------------------------------------------------------------

## 4.6 Full dry run: n = 5, k = 8

Triangular numbers:

``` text
T1 = 1
T2 = 3
T3 = 6
T4 = 10
```

Largest not exceeding `8`:

``` text
x = 3
```

Base:

``` text
T3 = 6
```

Remainder:

``` text
r = 8 - 6 = 2
```

Positive block:

``` text
[2,2,2]
```

Special magnitude:

``` text
y
= 2(x-r)+1
= 2(3-2)+1
= 3
```

So:

``` text
[2,2,2,-3]
```

Subarrays ending at `-3`:

``` text
[-3]             = -3
[2,-3]           = -1
[2,2,-3]         = +1   <-- positive
[2,2,2,-3]       = +3   <-- positive
```

Exactly:

``` text
r = 2
```

additional positives.

Now fill:

``` text
[2,2,2,-3,-1000]
```

Count:

``` text
inside [2,2,2] = 6
ending at -3   = 2
with -1000     = 0

TOTAL          = 8
```

Exactly `k`.

------------------------------------------------------------------------

## 4.7 Edge case k = 0

``` text
[-1000,-1000,...,-1000]
```

Every subarray is negative.

------------------------------------------------------------------------

## 4.8 Edge case r = 0

Example:

``` text
k = 6

6 = 3*4/2
```

No special element is needed.

``` text
[2,2,2,-1000,-1000,...]
```

Exactly six positive subarrays.

------------------------------------------------------------------------

## 4.9 Edge case all subarrays positive

If:

``` text
k = n(n+1)/2
```

then:

``` text
[2,2,2,...,2]
```

works.

------------------------------------------------------------------------

## 4.10 Mental model

``` text
NEED EXACTLY k
      |
      v
find structure with known contribution
      |
      v
positive block of length x
      |
      v
contribution = x(x+1)/2
      |
      v
decompose

k = triangular part + remainder
      |
      v
engineer one special value
      |
      v
suffix sums create a threshold

..., -3, -1, +1, +3, ...
             ^
      exact transition
      |
      v
exactly r extra positives
      |
      v
use -1000 barrier
      |
      v
no accidental positives
```

### Constructive/modelling classification

``` text
PRIMARY:
Target decomposition

BUILDING BLOCK:
Known-count triangular contribution

REMAINDER:
Threshold engineering

SAFETY:
Dominating negative barrier
```

------------------------------------------------------------------------

## 4.11 C++ code

``` cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;

    while (t--) {
        int n, k;
        cin >> n >> k;

        vector<int> ans;

        // Largest x with x(x+1)/2 <= k.
        int x = 0;

        while ((x + 1) * (x + 2) / 2 <= k) {
            x++;
        }

        int base = x * (x + 1) / 2;
        int r = k - base;

        // Bulk contribution.
        for (int i = 0; i < x; i++) {
            ans.push_back(2);
        }

        // Engineer exactly r additional positive subarrays.
        if (r > 0) {
            int special = -(2 * (x - r) + 1);
            ans.push_back(special);
        }

        // Barrier.
        while ((int)ans.size() < n) {
            ans.push_back(-1000);
        }

        for (int value : ans) {
            cout << value << ' ';
        }

        cout << '\n';
    }
}
```

Complexity:

``` text
O(n)
```

per test case to output the construction.

------------------------------------------------------------------------

# 5. Compare All Four Techniques

``` text
+-------------------------------------------------------------+
| 1862E — KOLYA AND MOVIE THEATRE                             |
+-------------------------------------------------------------+
| Signal: repeated distance-from-previous cost                |
|                                                             |
| Full objective                                              |
|      |                                                      |
|      v                                                      |
| telescope differences                                       |
|      |                                                      |
|      v                                                      |
| only final endpoint matters                                 |
|      |                                                      |
|      v                                                      |
| fix endpoint + choose best prefix values                    |
+-------------------------------------------------------------+


+-------------------------------------------------------------+
| 1844D — ROW MAJOR                                           |
+-------------------------------------------------------------+
| Signal: same 1D object tested under many 2D dimensions      |
|                                                             |
| grid adjacency                                              |
|      |                                                      |
|      v                                                      |
| index distance                                              |
|      |                                                      |
|      v                                                      |
| dimension must divide n                                     |
|      |                                                      |
|      v                                                      |
| smallest non-divisor                                        |
|      |                                                      |
|      v                                                      |
| periodic construction                                       |
+-------------------------------------------------------------+


+-------------------------------------------------------------+
| 1873G — ABBC OR BACB                                        |
+-------------------------------------------------------------+
| Signal: local operation/replacement                         |
|                                                             |
| decode one operation                                        |
|      |                                                      |
|      v                                                      |
| one A permanently disappears                               |
|      |                                                      |
|      v                                                      |
| coins = consumed A's                                        |
|      |                                                      |
|      v                                                      |
| compress into A-runs                                        |
|      |                                                      |
|      v                                                      |
| sacrifice smallest unavoidable run                          |
+-------------------------------------------------------------+


+-------------------------------------------------------------+
| 1809C — SUM ON SUBARRAYS                                    |
+-------------------------------------------------------------+
| Signal: construct EXACTLY k                                 |
|                                                             |
| find known-count block                                      |
|      |                                                      |
|      v                                                      |
| triangular contribution                                     |
|      |                                                      |
|      v                                                      |
| k = bulk + remainder                                        |
|      |                                                      |
|      v                                                      |
| engineer remainder using threshold                          |
|      |                                                      |
|      v                                                      |
| isolate using huge negative barrier                         |
+-------------------------------------------------------------+
```

------------------------------------------------------------------------

# 6. Constructive Signal Checklist

When you see a new constructive problem, ask these in order.

## A. Does the operation hide a simpler resource change?

Examples:

``` text
AB -> BC

looks like string mutation

but actually:

one A disappears
```

Think:

``` text
OPERATION DECODING
```

------------------------------------------------------------------------

## B. Does a complicated sum contain consecutive differences?

Example:

``` text
(x1-0) + (x2-x1) + (x3-x2)
```

Try:

``` text
TELESCOPING
```

------------------------------------------------------------------------

## C. Is a grid/matrix flattened into a sequence?

Ask:

``` text
What index difference represents
horizontal / vertical / diagonal relations?
```

Think:

``` text
DIMENSION REDUCTION
```

------------------------------------------------------------------------

## D. Does the problem ask for EXACTLY k?

Try:

``` text
known contribution
+
remainder
```

Examples of known-count structures:

``` text
prefix of x elements
pairs
subarrays
triangular numbers
powers of two
blocks
```

Think:

``` text
TARGET DECOMPOSITION
```

------------------------------------------------------------------------

## E. Can I use extreme values?

If allowed values are large relative to `n`, try:

``` text
+BIG
-BIG
```

to force behavior.

Think:

``` text
BARRIER / DOMINATION
```

------------------------------------------------------------------------

## F. Is the same local pattern repeated?

Try:

``` text
periodic construction

s[i] = pattern[i % k]
```

Then prove what distances produce equality.

Think:

``` text
PERIODICITY
```

------------------------------------------------------------------------

## G. Must the answer be optimal/minimal?

Try proving:

``` text
LOWER BOUND
+
CONSTRUCTION MATCHING LOWER BOUND
```

Example from 1844D:

``` text
at least k characters required
+
construct with exactly k
=
optimal
```

------------------------------------------------------------------------

# 7. Mathematical Modelling Template for Future CF Problems

Before coding, fill this out:

``` text
PROBLEM:
----------------------------------

1. OBJECTS
What is given?

2. CHOICE
What can I choose / modify?

3. OPERATION
What does one operation literally do?

4. INVARIANT / RESOURCE
What stays unchanged?
What permanently increases/decreases?

5. OBJECTIVE
maximize / minimize / construct exactly what?

6. MATHEMATICAL FORM
Write the complete expression.

7. SIMPLIFICATION
Can I:
- telescope?
- factor?
- use prefix sums?
- convert dimensions?
- convert to divisibility?
- group equal behavior?
- find a threshold?

8. CONSTRUCTIVE SIGNAL
Which type is this?

[ ] operation decoding
[ ] think in reverse
[ ] greedy / sorting
[ ] bit-by-bit
[ ] dimension setup
[ ] prefix/suffix observation
[ ] target decomposition
[ ] periodic pattern
[ ] barrier / domination
[ ] lower-bound + matching construction

9. BUILDING BLOCK
What simple structure has predictable behavior?

10. REMAINDER
How do I create exactly what remains?

11. SAFETY PROOF
Why are there no accidental extra cases?

12. COMPLEXITY
Does it fit constraints?
```

------------------------------------------------------------------------

# 8. One-Line Memory Hooks

``` text
1862E:
"Write all penalties -> telescope -> fix last day."

1844D:
"Grid adjacency -> index distance -> divisors -> smallest non-divisor -> repeat."

1873G:
"Don't simulate AB/BA -> every coin consumes one A -> group A-runs -> lose minimum."

1809C:
"Exactly k -> triangular bulk + engineered remainder + negative barrier."
```

------------------------------------------------------------------------

# 9. Sources / Problem Links

-   Codeforces 1862E --- Kolya and Movie Theatre:
    https://codeforces.com/problemset/problem/1862/E

-   Codeforces Round #894 editorial:
    https://codeforces.com/blog/entry/119715

-   Codeforces 1844D --- Row Major:
    https://codeforces.com/problemset/problem/1844/D

-   Codeforces Round #884 editorial:
    https://codeforces.com/blog/entry/118128

-   Codeforces 1873G --- ABBC or BACB:
    https://codeforces.com/problemset/problem/1873/G

-   Codeforces 1809C --- Sum on Subarrays:
    https://codeforces.com/problemset/problem/1809/C

------------------------------------------------------------------------

# 10. Final Pattern Map

``` text
                    CONSTRUCTIVE PROBLEM
                            |
        +-------------------+-------------------+
        |                   |                   |
        v                   v                   v
   operations?          dimensions?          exactly k?
        |                   |                   |
        v                   v                   v
decode resource      convert to indices    known-count block
        |                   |                   |
        v                   v                   v
group/compress       derive arithmetic      bulk + remainder
        |             condition                  |
        v                   |                   v
greedy choice              v              engineer threshold
                      periodic pattern            |
                            |                    v
                            v                 barrier
                     prove optimality             |
                                                 v
                                             prove exact
```

The key habit is:

``` text
DO NOT ASK FIRST:
"What algorithm should I use?"

ASK FIRST:
"What simpler mathematical object is hiding
inside the statement?"
```
