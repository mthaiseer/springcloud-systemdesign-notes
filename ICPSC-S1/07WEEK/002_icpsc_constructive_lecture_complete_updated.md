# Lecture 2 — Constructive Algorithms

## 0. Constructive Mindset

Constructive problems ask you to **build any valid object, sequence, ordering, set of operations, or representation** that satisfies all constraints.

The main question is usually:

> What must be true in the final answer, and what simple pattern guarantees it?

Constructive is different from pure greedy:

- **Greedy:** What is the best choice right now?
- **Constructive:** What valid structure can I build, and why does it satisfy every condition?

### Common constructive signals

- "Construct an array"
- "Print any valid permutation"
- "Find a sequence of operations"
- "If impossible, print -1"
- "Produce any representation"
- "Find an ordering"
- "Reach a target state"

---

# 1. Constructive Toolbox

The toolbox is not a list of tricks to memorize. Use it as a **recognition system**:

```text
Problem signal
      ↓
What structure is hiding?
      ↓
Choose a construction tool
      ↓
Build a simple valid answer
      ↓
Prove why it works
```

Quick map:

```text
                     CONSTRUCTIVE
                          |
     ------------------------------------------------
     |          |          |         |              |
   Reverse    Sorting     Bits      Setup      Prefix/Suffix
     |          |          |         |              |
 undo ops    AB vs BA    2^i       tiny n       keep future
     |
     +---------------- Build/Spend ----------------+
                          |
                    Two Pointers
```

---

## 1.1 Think in Reverse

### Signal

Use reverse thinking when the problem says **reach a target using operations**, but forward choices branch.

```text
START
  |
  +--> choice A
  +--> choice B
  +--> choice C

Forward = many possible paths
```

Ask:

> Can I undo the operations from the target?

### Tiny example

Operations:

```text
x → x + 1
x → 2x
```

Forward from `1`:

```text
          1
        /   \
       2     2
      / \
     3   4
        / \
       5   8
       ...
```

But reverse from `13`:

```text
13 --odd--> 12 --even--> 6 --even--> 3
                                      |
                                    odd
                                      ↓
                                      2 --even--> 1
```

Why?

```text
odd target
    ↓
cannot come from ×2
    ↓
previous move MUST be +1
    ↓
reverse: -1
```

### Memory hook

```text
Forward branches?
      ↓
Undo operations.
      ↓
Reverse may become forced.
```

**Remember:** “Too many roads forward? Walk backward from the destination.”

---

## 1.2 Decode the Operation

Before designing an algorithm, translate every operation into its **real effect**.

```text
OPERATION
    |
    +--> What changes?
    +--> What stays unchanged?
    +--> Can it be undone?
    +--> Does it build/spend something?
    +--> Does it affect parity/bits/divisibility?
```

### Tiny examples

```text
x → x + 1
meaning: small growth
reverse: x → x - 1
```

```text
x → Kx
meaning: large jump
reverse: x → x/K, only if x % K == 0
```

So a problem containing `+1` and `×K` should make you notice:

```text
×K
 ↓
divisibility
 ↓
B % K
```

Another operation pair:

```text
normal attack          ultimate
     |                    |
kill 1                  kill x
combo +1                combo → 0
     |                    |
BUILD                  SPEND
```

The statement may look complicated, but decoding exposes the structure.

### Memory hook

> Don't simulate the wording. Translate each operation into mathematics first.

---

## 1.3 Greedy / Sorting — Discover the Comparator

If the answer is an **ordering**, sorting is a strong candidate.

But never guess the comparator.

```text
Need best order
      ↓
Maybe sorting
      ↓
Sort by WHAT?
      ↓
Take only A and B
      ↓
compare A→B vs B→A
```

### Tiny example

Two contest problems:

```text
A = (decay D1, time T1)
B = (decay D2, time T2)
```

Only two orders exist:

```text
A → B

0 ----- T1 -------- T1+T2
    A          B
```

or:

```text
B → A

0 ----- T2 -------- T2+T1
    B          A
```

Compare their scores. After cancelling common terms, suppose we obtain:

```text
D2*T1 <= D1*T2
```

which is:

```text
T1/D1 <= T2/D2
```

Now the comparator has been **derived**, not guessed:

```text
sort by T/D ascending
```

### Memory hook

```text
Don't know what to sort by?
          ↓
        A B
         vs
        B A
          ↓
derive inequality
          ↓
sorting comparator
```

---

## 1.4 Exchange Argument — Prove the Sorting Rule

After deriving a comparator, prove that the entire sorted order is optimal.

Suppose greedy says:

```text
A should be before B
```

but an alleged optimal order contains:

```text
... B A ...
```

Exchange them:

```text
before:  P1 P2 [ B A ] P5 P6
                    ↓ swap
after:   P1 P2 [ A B ] P5 P6
```

Why can we focus only on `A` and `B`?

```text
before pair → unchanged

pair:
B then A
vs
A then B

after pair → unchanged starting time
because

TA + TB = TB + TA
```

If `A B` is at least as good as `B A`, the inversion can be removed.

```text
wrong adjacent pair
       ↓
     swap
       ↓
objective does not worsen
       ↓
repeat for every inversion
       ↓
greedy sorted order
```

### Mental model

```text
Exchange argument
       =
Bubble Sort + proof
```

### Memory hook

> To prove a global ordering, prove that every locally wrong adjacent pair can be safely swapped.

---

## 1.5 Bit-by-Bit Construction

### Signals

Immediately think about bits when you see:

```text
2^i
AND / OR / XOR
powers of two
binary coefficients
```

Mental conversion:

```text
number condition
      ↓
write binary
      ↓
look at each bit / local bit pattern
```

### Tiny example

```text
14 = 1110₂
```

Suppose adjacent non-zero coefficients are forbidden.

Normal binary contains:

```text
bits:  3 2 1 0
       -------
       1 1 1 0
       ^^^^^
       bad run
```

Use the identity:

```text
111₂ = 1000₂ - 1
```

More generally:

```text
111...111
=
1000...000 - 1
```

For `14`:

```text
14 = 8 + 4 + 2
   = 16 - 2
```

So instead of:

```text
0  1  1  1
```

we can represent it LSB-first as:

```text
index: 0   1   2   3   4
       ------------------
a[i]:  0  -1   0   0   1
```

Check:

```text
-1*2^1 + 1*2^4
= -2 + 16
= 14
```

### Pattern

```text
2^i appears
    ↓
write binary
    ↓
find forbidden local pattern
    ↓
find identity preserving value
    ↓
repair bits locally
```

### Memory hook

**Decimal:** `999 = 1000 - 1`  
**Binary:** `111₂ = 1000₂ - 1`

---

## 1.6 Dimensions / Setup

Sometimes the construction is hidden in the **shape of n**, not in a sophisticated algorithm.

Before coding, test:

```text
n = 1
n = 2
n = 3
n = 4
n = 5
n = 6
```

Look for:

```text
parity      n % k
pairing     leftovers
groups      rows/columns
```

### Tiny example — pairing

```text
n = 6

[1 2] [3 4] [5 6]
  ✓     ✓     ✓
```

But:

```text
n = 5

[1 2] [3 4] [5]
               ↑
            leftover
```

This immediately suggests:

```text
even n → perfect pairing possible
odd n  → one element needs special handling
```

### Tiny example — groups of 3

```text
n = 6
[1 2 3] [4 5 6]
```

```text
n = 7
[1 2 3] [4 5 6] [7]
                   ↑
                leftover
```

Signal:

```text
n % 3
```

### Memory hook

> Before inventing complicated logic, draw constructions for `n = 1..6`.

---

## 1.7 Prefix / Suffix Observation

When constructing left to right, a locally valid choice may destroy the possibility of completing the rest.

Always visualize:

```text
[ already fixed ][ not built yet ]
      prefix          suffix
```

Ask:

> After fixing this prefix, is the suffix still feasible?

### Tiny example

Suppose final sum must be `10`.

After some choices:

```text
prefix sum = 8

remaining positions can add at most 1

[ prefix = 8 ][ max suffix = 1 ]

8 + 1 = 9 < 10
```

So the prefix choice was impossible even if it looked locally valid.

Think of an invariant:

```text
choose next value
      ↓
prefix remains valid?
      ↓
suffix still has enough freedom?
      ↓
YES → continue
NO  → reject choice
```

### Recognition signal

Look for prefix/suffix reasoning when the statement says:

```text
for every i...
all prefixes...
all suffixes...
after every operation...
```

### Memory hook

> Don't fix the present in a way that makes the future impossible.

---

## 1.8 Build / Spend Resource → Two Pointers

Some problems contain two complementary operations:

```text
Operation A → BUILD resource
Operation B → SPEND resource
```

After sorting, different ends may naturally play different roles:

```text
small ---------------------------- large
  ↑                                  ↑
build resource                    use resource
```

### Tiny example

Monster hordes:

```text
[1, 2, 3, 10]

 L           R
```

Normal attacks on small hordes build combo:

```text
kill 1  → combo = 1
kill 2  → combo = 3
kill 3  → combo = 6
```

Then the accumulated combo is valuable against a large horde:

```text
small hordes              large horde
     |                          |
     v                          v
  BUILD x  ---------------->  SPEND x
```

This suggests:

```text
sort
 ↓
L = smallest
R = largest
 ↓
small side builds
large side receives/spends
 ↓
two pointers
```

Important refinement: do not always consume the whole left side.

```text
combo = 7
left  = 5
right = 10

need = right - combo
     = 3

consume only 3 from left:

left:  5 → 2
combo: 7 → 10
```

Now use the resource immediately.

### Memory hook

```text
Small = fuel
Large = target
```

---

## 1.9 Fast Recognition Decision Tree

Use this during contests:

```text
                   CONSTRUCTIVE PROBLEM
                           |
                           v
                 What is the main signal?
                           |
        ---------------------------------------------
        |            |            |                 |
   Reach target   Need order     2^i/bits      Build + Spend
        |            |            |                 |
        v            v            v                 v
 forward messy?   compare AB    binary/local      sort values
        |          vs BA          pattern            |
        v            |            |                 v
    REVERSE           v            v          TWO POINTERS
                  comparator    transform
                      |
                      v
                exchange proof
```

And if none fits:

```text
Construction depends on n?
        ↓
try n = 1..6
        ↓
parity / modulo / grouping

Condition depends on earlier choices?
        ↓
prefix / suffix invariant
```

---

## 1.10 Toolbox Memory Card

```text
REVERSE
Forward branches → undo operations.

DECODE
Translate statement operations into mathematics.

SORTING
Need order → compare AB vs BA.

EXCHANGE
Wrong adjacent pair → swap → prove not worse.

BITS
2^i appears → binary → find bad local pattern.

SETUP
Try tiny n → parity / modulo / leftovers.

PREFIX/SUFFIX
Fix prefix → make sure suffix remains possible.

TWO POINTERS
One side builds → other side consumes.
```

The goal during a contest is:

```text
Don't remember the old solution.

Remember the SIGNAL
        ↓
recognize the TOOL
        ↓
derive the construction again.
```


# 2. General Constructive Workflow

```text
Read problem
    ↓
What exactly must I construct?
    ↓
Try tiny examples
1, 2, 3, 4, 5
    ↓
Try obvious/direct approach
    ↓
Why does it fail?
    ↓
Decode operations / constraints
    ↓
Check:
reverse?
sorting?
bits?
parity?
prefix/suffix?
two pointers?
    ↓
Find simple construction
    ↓
Prove every condition
    ↓
Dry run
    ↓
Code
```

---

# 3. Problem 1 — Minimum Moves: 0 → Y

## 3.1 Problem in Simple Words

Start from:

```text
x = 0
```

Allowed operations:

```text
x → x + 1
x → 2x
```

Given target `Y`, find the minimum number of moves needed to reach `Y`.

## 3.2 Obvious Forward Attempt

At first, we may think:

```text
always double because doubling is faster
```

But:

```text
0 × 2 = 0
```

So the first move must be:

```text
0 → 1
```

After that, forward movement branches:

```text
x
├─ x + 1
└─ 2x
```

There is no obvious safe forward rule.

## 3.3 Technique Recognition

```text
forward has many choices
        ↓
operations can be undone
        ↓
THINK IN REVERSE
```

Reverse operations:

```text
forward +1  ↔ reverse -1
forward ×2  ↔ reverse /2
```

But `/2` is only valid if the current number is even.

## 3.4 Core Observation

If current `Y` is odd:

```text
Y = 2k + 1
```

it could **not** have been produced by doubling, because doubling always gives an even number.

Therefore the last forward move must have been:

```text
Y - 1 → Y
```

So in reverse:

```text
odd Y → Y - 1
```

If `Y` is even, undo the doubling:

```text
even Y → Y / 2
```

## 3.5 Rule

```text
while Y > 0:

    if Y is even:
        Y /= 2

    else:
        Y -= 1

    moves++
```

## 3.6 Dry Run — Y = 13

Reverse:

```text
13
↓ odd
12
↓ even
6
↓ even
3
↓ odd
2
↓ even
1
↓ odd
0
```

Moves:

```text
6
```

Forward reconstruction:

```text
0
→ 1     +1
→ 2     ×2
→ 3     +1
→ 6     ×2
→ 12    ×2
→ 13    +1
```

## 3.7 Proof

### Odd case

If `Y` is odd, the previous forward move cannot be multiplication by 2. So reverse `Y → Y-1` is forced.

### Even case

If `Y` is even, division by 2 directly undoes one multiplication step and shrinks the target fastest.

## 3.8 Binary Mental Model

Multiplying by 2 means a binary left shift:

```text
101
↓ ×2
1010
```

So reverse division by 2 means removing the final zero bit.

Odd numbers end with `...1`, so subtracting 1 removes that required final `+1`.

## 3.9 C++

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long y;
    cin >> y;

    long long moves = 0;

    while (y > 0) {
        if (y % 2 == 0)
            y /= 2;
        else
            y -= 1;

        moves++;
    }

    cout << moves << '\n';
}
```

## 3.10 Complexity

```text
Time:  O(log Y)
Space: O(1)
```

## 3.11 Real-World Mental Model

Instead of asking, “Which road should I take from home?”, ask, “From the destination, what road could I possibly have come from?”

## 3.12 Contest Takeaway

```text
forward branches
      ↓
decode operations
      ↓
can they be undone?
      ↓
reverse
      ↓
forced move / simpler greedy
```

---

# 4. Practice — Minimum Moves to Reach Target Score

Start from `1`. Operations are `+1` and doubling, but doubling may be used at most `maxDoubles` times.

Reverse reasoning:

```text
target even + doubles left
    → target /= 2

target odd
    → target -= 1

no doubles left
    → remaining moves = target - 1
```

Example:

```text
target = 10
maxDoubles = 2
```

Reverse:

```text
10 → 5   /2
5  → 4   -1
4  → 2   /2
2  → 1   remaining decrement
```

Answer: `4`.

Important optimization: once doubles are exhausted, add the remaining decrements directly instead of simulating them.

---

# 5. Practice — Change A to B

Given:

```text
start = A
target = B
operations:
x → x + 1
x → Kx
```

Reverse view:

```text
B → B - 1
B → B / K   if divisible
```

Key signal:

```text
B % K
```

Important difference from `0 → Y`: the start is arbitrary `A`, so reverse division must not take us below `A`.

Useful observations:

- if `A > B`, impossible because operations only increase
- if `K = 1`, multiplication does nothing
- if `B % K != 0`, reverse decrements are forced until divisible or until reaching `A`

Pattern:

```text
reverse + divisibility + lower boundary
```

---

# 6. Problem 2 — Contest Maximum Score

## 6.1 Problem in Simple Words

There are `N` contest problems. For every problem `i`:

```text
S[i] = initial score
D[i] = score decay per second
T[i] = time needed to solve
```

If a problem finishes at time `C[i]`, its score is:

```text
S[i] - D[i] * C[i]
```

Goal:

```text
choose problem order
to maximize total score
```

## 6.2 First Ideas

Natural guesses:

```text
highest score first?
highest decay first?
shortest time first?
```

None alone is sufficient. We need to derive the comparator.

## 6.3 Technique Recognition

```text
Need optimal ORDER
      ↓
sorting is likely
      ↓
don't know what to sort by
      ↓
take only TWO problems
      ↓
compare:
1 → 2
vs
2 → 1
      ↓
derive comparator
```

## 6.4 Take Two Problems

```text
Problem 1 = (S1, D1, T1)
Problem 2 = (S2, D2, T2)
```

Compare `1 → 2` against `2 → 1`.

## 6.5 Order 1 → 2

```text
0 -------- T1 ------------- T1+T2
    P1               P2
```

Total score:

```text
Score12 =
(S1 - D1*T1)
+
(S2 - D2*(T1+T2))
```

## 6.6 Order 2 → 1

```text
0 -------- T2 ------------- T2+T1
    P2               P1
```

Total score:

```text
Score21 =
(S2 - D2*T2)
+
(S1 - D1*(T1+T2))
```

## 6.7 Derive the Comparator

We want:

```text
Score12 >= Score21
```

So:

```text
(S1 - D1*T1)
+
(S2 - D2*(T1+T2))

>=

(S2 - D2*T2)
+
(S1 - D1*(T1+T2))
```

Expand:

```text
S1 - D1*T1 + S2 - D2*T1 - D2*T2
>=
S2 - D2*T2 + S1 - D1*T1 - D1*T2
```

Cancel:

```text
S1       cancels
S2       cancels
-D1*T1   cancels
-D2*T2   cancels
```

Remaining:

```text
-D2*T1 >= -D1*T2
```

Multiply by `-1`:

```text
D2*T1 <= D1*T2
```

Therefore:

```text
T1/D1 <= T2/D2
```

So the lecturer's rule is:

> Sort by `T / D` ascending.

Equivalent:

> Sort by `D / T` descending.

## 6.8 Why Only Two Problems Are Enough?

Suppose the full order is:

```text
P1 P2 P3 [ A B ] P6 P7
          ↑ ↑
```

Swap only `A B` to `B A`.

Everything before the pair is unchanged. Everything after the pair also starts at the same time because:

```text
TA + TB = TB + TA
```

Therefore we only need to compare those two adjacent problems.

## 6.9 Exchange Argument

```text
wrong adjacent pair
       ↓
A B violates comparator
       ↓
swap
       ↓
score does not decrease
       ↓
repeat
       ↓
sorted by T/D ascending
       ↓
optimal
```

Mental model:

```text
Bubble Sort + proof
```

## 6.10 Real-World Mental Model

Each problem is “bleeding points.” A task with high decay and short solving time is urgent.

Smaller `T/D` means more urgent, so solve it earlier.

## 6.11 Avoid Floating Point

Instead of comparing:

```text
T1/D1 < T2/D2
```

cross multiply:

```text
T1*D2 < T2*D1
```

Comparator:

```cpp
return a.time * b.decay
     < b.time * a.decay;
```

Use `long long`.

## 6.12 C++

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Problem {
    long long score;
    long long decay;
    long long time;
};

int main() {
    int n;
    cin >> n;

    vector<Problem> p(n);

    for (int i = 0; i < n; i++)
        cin >> p[i].score;

    for (int i = 0; i < n; i++)
        cin >> p[i].decay;

    for (int i = 0; i < n; i++)
        cin >> p[i].time;

    sort(p.begin(), p.end(),
         [](const Problem& a, const Problem& b) {
             return a.time * b.decay
                  < b.time * a.decay;
         });

    long long currentTime = 0;
    long long totalScore = 0;

    for (auto &pr : p) {
        currentTime += pr.time;
        totalScore += pr.score - pr.decay * currentTime;
    }

    cout << totalScore << '\n';
}
```

## 6.13 Complexity

```text
Sorting: O(N log N)
Scan:    O(N)
Total:   O(N log N)
```

## 6.14 Pattern Card

```text
Problem signal:
Need optimal ordering

Trigger:
Maybe sorting

Discovery tool:
Compare A→B vs B→A

Proof:
Exchange argument

Comparator:
T/D ascending

Implementation:
Cross multiplication
```

## 6.15 Contest Takeaway

```text
DON'T KNOW WHAT TO SORT BY?
            ↓
Take A and B
            ↓
compute objective(A,B)
compute objective(B,A)
            ↓
set one >= the other
            ↓
cancel common terms
            ↓
derive inequality
            ↓
that inequality becomes comparator
```

---

# 7. Problem 3 — Binary Colouring

## 7.1 Problem in Simple Words

Given positive integer `x`, construct an array:

```text
a0, a1, ..., a(n-1)
```

such that:

```text
a[i] ∈ {-1, 0, 1}
```

and:

```text
x = Σ a[i] * 2^i
```

Also, no two adjacent coefficients may both be non-zero.

Invalid:

```text
[1, 1]
[1,-1]
[-1,1]
```

Valid:

```text
[1,0,1]
[-1,0,1]
```

## 7.2 Start With Normal Binary

Example:

```text
x = 14
14 = 1110₂
```

LSB-first coefficients:

```text
index:  0 1 2 3
a:      0 1 1 1
```

Numerically correct, but invalid because `1 1 1` contains adjacent non-zero coefficients.

## 7.3 Technique Recognition

```text
formula uses 2^i
      ↓
think in bits
      ↓
normal binary satisfies VALUE
      ↓
but violates adjacency condition
      ↓
find forbidden local pattern
      ↓
consecutive 1s
```

Question:

> How can we replace a run of `1`s without changing the value?

## 7.4 Key Identity

```text
111₂ = 1 + 2 + 4 = 8 - 1 = 1000₂ - 1
```

General identity:

```text
2^l + 2^(l+1) + ... + 2^r
=
2^(r+1) - 2^l
```

## 7.5 ASCII Transformation

```text
111111
```

becomes:

```text
1000000
-
0000001
```

So:

```text
111...111
=
1000...000 - 1
```

This is the binary version of:

```text
999 = 1000 - 1
```

## 7.6 Apply to x = 14

```text
14 = 1110₂
```

The run occupies bits `1,2,3`:

```text
2^1 + 2^2 + 2^3
=
2^4 - 2^1
```

Therefore:

```text
14 = 16 - 2
```

Coefficients:

```text
index:  0   1  2  3  4
a:      0  -1  0  0  1
```

Check:

```text
-1*2^1 + 1*2^4
= -2 + 16
= 14
```

## 7.7 Another Example — 15

```text
15 = 1111₂
```

Use:

```text
1 + 2 + 4 + 8 = 16 - 1
```

So:

```text
15 = 2^4 - 2^0
```

Construction:

```text
[-1, 0, 0, 0, 1]
```

## 7.8 Why Is -1 Allowed?

`-1` lets us convert many adjacent `+1` bits into one `-1` at the start and one `+1` after the run, preserving the total value while removing adjacency.

## 7.9 Construction Idea

1. Write normal binary digits of `x`.
2. Find runs of consecutive `1`s.
3. For each run `[l..r]`:

```text
a[l]       = -1
a[l+1..r]  = 0
a[r+1]     = +1
```

because:

```text
2^l + ... + 2^r
=
2^(r+1) - 2^l
```

## 7.10 C++

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int tc;
    cin >> tc;

    while (tc--) {
        long long x;
        cin >> x;

        vector<int> a(32, 0);

        for (int i = 0; i < 30; i++) {
            if (x & (1LL << i))
                a[i] = 1;
        }

        for (int i = 0; i < 31; i++) {
            if (a[i] == 1 && a[i + 1] == 1) {

                int j = i;

                while (j < 31 && a[j] == 1)
                    j++;

                a[i] = -1;

                for (int k = i + 1; k < j; k++)
                    a[k] = 0;

                a[j] = 1;
            }
        }

        int n = 32;

        while (n > 1 && a[n - 1] == 0)
            n--;

        cout << n << '\n';

        for (int i = 0; i < n; i++)
            cout << a[i] << ' ';

        cout << '\n';
    }
}
```

## 7.11 Complexity

```text
Time:  O(log x)
Space: O(log x)
```

For `x < 2^30`, this is effectively constant.

## 7.12 Real-World Mental Model

Remember:

```text
999 = 1000 - 1
```

Binary equivalent:

```text
111 = 1000₂ - 1
```

## 7.13 Pattern Card

```text
Problem signal:
2^i + coefficients

Trigger:
Think bits

Bad pattern:
11

Identity:
111...111 = 1000...000 - 1

Construction:
replace run with
-1 ... 0 ... +1
```

## 7.14 Contest Takeaway

```text
2^i appears
   ↓
write binary
   ↓
what condition does normal binary violate?
   ↓
identify bad local bit pattern
   ↓
find identity that preserves value
   ↓
construct bit by bit
```

---

# 8. Problem 4 — Smilo and Monsters

## 8.1 Problem in Simple Words

There are multiple hordes of monsters.

Let:

```text
x = combo counter
```

Initially `x = 0`.

### Type 1

Kill one monster:

```text
horde -= 1
x += 1
```

Cost: `1 attack`.

### Type 2 — Ultimate

If a horde has at least `x` monsters:

```text
kill x monsters
x = 0
```

Cost: `1 attack`.

Goal: destroy all monsters using the minimum number of attacks.

## 8.2 Decode the Operations

Type 1:

```text
kills 1
builds +1 combo
```

Type 2:

```text
spends combo
kills x monsters at once
```

This is a classic:

```text
BUILD RESOURCE
+
SPEND RESOURCE
```

problem.

## 8.3 Main Observation

After sorting:

```text
small -------------------------- large
  ↑                                ↑
build combo                    ultimate target
```

So:

```text
small hordes → fuel
large hordes → target
```

This suggests:

```text
sort + two pointers
```

## 8.4 Why Small for Build and Large for Spend?

Example:

```text
[1, 2, 3, 10]
```

Kill the small hordes normally:

```text
1 → x = 1
2 → x = 3
3 → x = 6
```

Now use that combo against the large horde. The ultimate is most valuable when it saves many one-by-one attacks on a large group.

## 8.5 Two-Pointer Setup

After sorting:

```text
L → smallest
R → largest
```

Maintain:

```text
x   = current combo
ans = attacks
```

## 8.6 Case 1 — Left Horde Is Not Enough

If:

```text
x + a[L] < a[R]
```

even consuming all of `a[L]` cannot make combo large enough to match the right horde.

So:

```text
x += a[L]
ans += a[L]
L++
```

## 8.7 Case 2 — Left Horde Can Complete the Combo

If:

```text
x + a[L] >= a[R]
```

we only need:

```text
need = a[R] - x
```

normal attacks.

Then:

```text
a[L] -= need
x += need
ans += need
```

Now `x = a[R]`, so use ultimate:

```text
ans++
x = 0
R--
```

If left horde became zero, increment `L`.

## 8.8 Dry Run

Take:

```text
[1, 1, 1, 3]
```

Start:

```text
x = 0
```

Consume:

```text
1 → x = 1
1 → x = 2
1 → x = 3
```

Now ultimate the horde of `3`.

Total:

```text
3 normal + 1 ultimate = 4 attacks
```

## 8.9 Important Optimization

Do not always consume the whole left horde.

Example:

```text
x = 7
left = 5
right = 10
```

Need only:

```text
10 - 7 = 3
```

So:

```text
left: 5 → 2
x:    7 → 10
```

Then ultimate the right horde.

## 8.10 Last Remaining Horde

When `L == R`, let:

```text
m = monsters left
x = current combo
```

Suppose we perform `y` normal attacks.

Then:

```text
combo = x + y
remaining = m - y
```

To use ultimate:

```text
x + y <= m - y
```

Therefore:

```text
2y <= m - x
```

This explains the roughly-half split behavior.

## 8.11 Final Horde Example

```text
m = 6
x = 2
```

Do 2 normal attacks:

```text
m: 6 → 4
x: 2 → 4
```

Now ultimate kills all 4.

Additional attacks:

```text
2 normal + 1 ultimate = 3
```

## 8.12 Odd Example

```text
m = 5
x = 0
```

Do 2 normal attacks:

```text
m = 3
x = 2
```

Ultimate kills 2:

```text
m = 1
x = 0
```

One final normal attack.

Total:

```text
2 + 1 + 1 = 4
```

## 8.13 C++

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;

    while (t--) {
        int n;
        cin >> n;

        vector<ll> a(n);

        for (ll &v : a)
            cin >> v;

        sort(a.begin(), a.end());

        int l = 0;
        int r = n - 1;

        ll x = 0;
        ll ans = 0;

        while (l < r) {
            ll need = a[r] - x;

            if (a[l] < need) {
                x += a[l];
                ans += a[l];
                l++;
            } else {
                a[l] -= need;
                ans += need;
                x += need;

                // ultimate on right horde
                ans++;
                x = 0;
                r--;

                if (a[l] == 0)
                    l++;
            }
        }

        if (l == r) {
            ll m = a[l];

            if (m == 1) {
                ans++;
            } else {
                ll gap = m - x;
                ans += (gap + 1) / 2;
                ans++;
            }
        }

        cout << ans << '\n';
    }
}
```

## 8.14 Complexity

```text
Sorting:      O(N log N)
Two pointers: O(N)
Total:        O(N log N)
```

## 8.15 Real-World Mental Model

Think of the combo as a battery:

```text
small hordes
   ↓
charge battery

large horde
   ↓
discharge battery
for a large payoff
```

So:

```text
small = fuel
large = target
```

## 8.16 Pattern Card

```text
Problem signal:
one operation builds resource
another operation spends it

Trigger:
sort + two pointers

Construction:
small → build
large → spend

Greedy question:
how much of left side do I need?

Answer:
only enough to activate/use the right side
```

## 8.17 Contest Takeaway

```text
one operation BUILDS x
another SPENDS x
        ↓
ask:
what should build?
what should be target?
        ↓
sort
        ↓
small → resource
large → target
        ↓
two pointers
```

---

# 9. Final Pattern Summary

| Problem | Main Signal | Technique | Key Idea |
|---|---|---|---|
| Minimum Moves `0 → Y` | Operations / target state | Reverse thinking | Odd forces `-1`, even undo doubling |
| Target Score | Limited doubling | Reverse + batching | Divide when possible, batch decrements when doubles end |
| Change `A → B` | `+1`, `×K` | Reverse + divisibility | `B % K` indicates forced decrements |
| Contest Maximum Score | Optimal order | Greedy + exchange argument | Compare `AB` vs `BA`, derive `T/D` comparator |
| Binary Colouring | `2^i`, coefficients | Bit-by-bit construction | `111...111 = 1000...000 - 1` |
| Smilo and Monsters | Build/spend resource | Sort + two pointers | Small builds combo, large receives ultimate |

---

# 10. High-Value Recognition Rules

## If You Need an Optimal Ordering

```text
Need order
   ↓
Maybe sort
   ↓
Don't know comparator?
   ↓
Take A and B
   ↓
Compare AB vs BA
   ↓
derive inequality
```

## If Forward Simulation Branches

```text
many forward choices
       ↓
can operations be undone?
       ↓
reverse
```

## If Powers of Two Appear

```text
2^i
 ↓
bits
 ↓
normal binary representation
 ↓
what condition is violated?
 ↓
modify local bit patterns
```

## If One Operation Builds and Another Spends

```text
resource build
+
resource spend
      ↓
who should build?
who should receive?
      ↓
sorting
      ↓
two pointers
```

---

# 11. Reusable Proof Styles

## Exchange Argument

Use when the answer is an ordering:

```text
assume adjacent pair is wrong
        ↓
swap them
        ↓
show objective does not worsen
        ↓
repeat
        ↓
greedy order is optimal
```

## Forced-Move Proof

Use in reverse problems:

```text
if target state has property X,
only one previous operation could produce it
```

Example:

```text
odd number cannot come from doubling
```

## Algebraic Identity

Use when representation needs to change but value must remain equal.

Example:

```text
2^l + ... + 2^r
=
2^(r+1) - 2^l
```

## Invariant / Resource Argument

Use when operations create and consume a quantity.

Example:

```text
normal attacks build combo
ultimate spends combo
```

---

# 12. Final Contest Checklist

When you see a constructive problem, ask:

```text
1. What must the final answer satisfy?

2. Can I start with an obvious valid representation?

3. What condition does that representation violate?

4. Can I repair local violations?

5. Do operations become simpler in reverse?

6. Is this an ordering problem?

7. If sorting is likely:
   can I derive comparator using two items?

8. Is there a build/spend resource?

9. Are powers of 2 or bit operations present?

10. What proof fits naturally?
    - exchange
    - forced move
    - algebraic identity
    - invariant
    - greedy safety
```

---

# 13. One-Page Mental Model

```text
                  CONSTRUCTIVE
                       |
       --------------------------------
       |              |               |
     REVERSE        SORTING           BITS
       |              |               |
decode ops      compare 2 items   local patterns
       |              |               |
forced move     exchange proof    identity transform
       |
       -------------------------------
                       |
                  TWO POINTERS
                       |
            small builds / large uses
```

---

# 14. Final Takeaway

Constructive problems become much easier when you stop asking:

> What exact trick do I need?

and instead ask:

```text
What must be true?
       ↓
What simple structure guarantees it?
       ↓
What technique exposes that structure?
       ↓
How do I prove it?
```

The four major patterns from this lecture are:

```text
1. Forward branches → think reverse

2. Need optimal order → compare 2 elements
   → derive comparator → exchange argument

3. Powers of two → think bit by bit

4. One operation builds, another spends
   → sort + two pointers
```
