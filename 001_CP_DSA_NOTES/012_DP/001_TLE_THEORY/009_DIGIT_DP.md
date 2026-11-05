# 010 — Digit DP
## DP Advanced — Digit-by-Digit Construction → Tight State → Digit Sum → Modulo State

> **Self-study goal:** Understand Digit DP from first principles instead of memorizing a template.
>
> The lecture builds the idea in this order:
>
> **Generate numbers digit by digit → control the upper bound → introduce `tight` → add the property state → observe overlapping subproblems → memoize → compress the property when possible**
>
> Use the same DP construction process:
>
> **Problem Asking → Story Removed → Observation → State → Choices → Transition → Base Case → Final Subproblem → Complexity → Optimization → Dry Run → C++17**

---

# Problem Links

1. **AtCoder Educational DP Contest S — Digit Sum**  
   https://atcoder.jp/contests/dp/tasks/dp_s

2. **Lecture warm-up**  
   Count integers in a huge range whose **sum of digits equals a target**.

3. **Lecture homework**  
   Count integers in `[L,R]` whose:
   - digit at an odd index is odd
   - digit at an even index is even

---

# Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [What Digit DP Is Used For](#2-what-digit-dp-is-used-for)
3. [Warm-up Problem](#3-warm-up-problem)
4. [Why Brute Force Fails](#4-why-brute-force-fails)
5. [First Idea — Construct Numbers Digit by Digit](#5-first-idea--construct-numbers-digit-by-digit)
6. [MSD to LSD Construction](#6-msd-to-lsd-construction)
7. [Leading Zero Representation](#7-leading-zero-representation)
8. [How to Stay Below an Upper Limit](#8-how-to-stay-below-an-upper-limit)
9. [The `tight` State](#9-the-tight-state)
10. [How `tight` Changes](#10-how-tight-changes)
11. [Warm-up State — `dp[index][tight][sum]`](#11-warm-up-state--dpindextightsum)
12. [Warm-up Transition](#12-warm-up-transition)
13. [Base Case and Final Subproblem](#13-base-case-and-final-subproblem)
14. [Why DP Works — Overlapping Subproblems](#14-why-dp-works--overlapping-subproblems)
15. [Warm-up Dry Run](#15-warm-up-dry-run)
16. [Warm-up C++17](#16-warm-up-c17)
17. [Complexity](#17-complexity)
18. [Why Top-Down Recursion Fits Digit DP](#18-why-top-down-recursion-fits-digit-dp)
19. [Problem 1 — AtCoder Digit Sum](#19-problem-1--atcoder-digit-sum)
20. [Why Storing Full Digit Sum Is Wasteful](#20-why-storing-full-digit-sum-is-wasteful)
21. [Modulo-State Compression](#21-modulo-state-compression)
22. [Digit Sum State and Transition](#22-digit-sum-state-and-transition)
23. [AtCoder Digit Sum Dry Run](#23-atcoder-digit-sum-dry-run)
24. [AtCoder Digit Sum C++17](#24-atcoder-digit-sum-c17)
25. [How to Handle a Range `[L,R]`](#25-how-to-handle-a-range-lr)
26. [Lecture Range Formula](#26-lecture-range-formula)
27. [Homework Pattern](#27-homework-pattern)
28. [Common Digit-DP State Parameters](#28-common-digit-dp-state-parameters)
29. [Common Mistakes](#29-common-mistakes)
30. [Contest Recognition Checklist](#30-contest-recognition-checklist)
31. [Final Mental Model](#31-final-mental-model)

---

# 1. Prerequisites

Digit DP becomes much easier if the following ideas are already comfortable.

---

## 1.1 Decimal digits

A decimal number is made of digits:

```text
0 ... 9
```

Example:

```text
5832
```

has digits:

```text
5, 8, 3, 2
```

---

## 1.2 MSD and LSD

**MSD**:

```text
Most Significant Digit
```

For:

```text
5832
```

MSD is:

```text
5
```

**LSD**:

```text
Least Significant Digit
```

LSD is:

```text
2
```

Digit DP usually constructs a number:

```text
MSD → LSD
```

because the upper-bound restriction is naturally decided from the prefix.

---

## 1.3 Prefix

For:

```text
N = 10987
```

possible prefixes are:

```text
1
10
109
1098
10987
```

Digit DP repeatedly asks:

```text
Is the prefix I constructed
still exactly equal to
the upper-limit prefix?
```

This becomes the `tight` state.

---

## 1.4 Recursion over positions

Suppose the number has:

```text
n digits
```

A recursive state may process:

```text
index = 0
index = 1
...
index = n-1
```

At each index, try a digit.

Generic recursion:

```text
solve(index)
    try digit 0
    try digit 1
    ...
    try digit 9
```

The difference in Digit DP is that not every digit is always allowed.

---

## 1.5 Memoization

If many recursive paths reach the same logical state:

```text
same index
same tight
same property information
```

the future answer is identical.

Store it once.

This is the DP part of Digit DP.

---

## 1.6 Counting DP

Digit DP often asks:

```text
How many numbers satisfy a property?
```

So transitions usually **sum** answers:

```text
answer
=
sum(
    answer after choosing each valid digit
)
```

---

## 1.7 Modulo arithmetic

For the AtCoder problem, the important identity from the lecture is:

```text
(a + b) % D
=
(
    (a % D)
    +
    (b % D)
)
% D
```

Therefore if we only care whether the final digit sum is divisible by `D`, we do not need the full sum.

We only need:

```text
sum % D
```

This is a major state-compression idea.

---

# 2. What Digit DP Is Used For

The lecture describes Digit DP as a technique for problems of this form:

```text
Count integers in a very large range

such that

the digits satisfy some property.
```

Typical range:

```text
up to 10^18
```

So we cannot loop through every integer.

Examples of digit properties:

```text
digit sum = K

digit sum % D = 0

specific digits are allowed

count of digit 7 = X

no equal adjacent digits

odd/even digit restrictions by position
```

---

## 2.1 Core recognition sentence

Think Digit DP when you see:

```text
huge numeric range
+
property depends on digits
+
need count / sum / optimum
```

---

# 3. Warm-up Problem

The lecture begins with:

```text
Count integers from 0 to 10^18
whose sum of digits is 150.
```

The exact upper bound is enormous.

So first ask:

```text
Can brute force work?
```

No.

---

# 4. Why Brute Force Fails

A direct loop would look conceptually like:

```cpp
for (long long number = 0; number <= upperLimit; number++) {
    if (digitSum(number) == 150) {
        answer++;
    }
}
```

For an upper limit around:

```text
10^18
```

this is impossible.

Even if checking one number were very cheap, there are too many numbers.

So we need to avoid iterating over numbers.

Instead:

```text
build all relevant possibilities
digit by digit
```

and merge equivalent states using DP.

---

# 5. First Idea — Construct Numbers Digit by Digit

Before adding any bound, imagine generating all numbers with a fixed number of positions.

For a 3-digit representation:

```text
_ _ _
```

At the first position choose:

```text
0 ... 9
```

At the second:

```text
0 ... 9
```

At the third:

```text
0 ... 9
```

The recursion tree begins like:

```text
             ___
      /       |       \
    0__      1__      2__ ...
   / | \
 00_ 01_ 02_ ...
```

The lecture first builds this mental model before introducing DP.

---

# 6. MSD to LSD Construction

Why construct from left to right?

Because when comparing with an upper limit, the first differing digit decides whether the entire number is already smaller.

Example upper limit:

```text
10987
```

Suppose current constructed prefix is:

```text
109__
```

The prefix is still exactly the same as the upper limit.

So the next digit is restricted.

But suppose prefix is:

```text
108__
```

Now we already know:

```text
108__ < 10987
```

No future suffix can make it exceed the upper limit.

So all later digits may freely be:

```text
0 ... 9
```

This observation creates `tight`.

---

# 7. Leading Zero Representation

Digit DP usually considers all numbers using the same digit length as the upper limit.

Example:

```text
UpperLimit = 999
```

Then:

```text
7
```

can be represented as:

```text
007
```

and:

```text
42
```

as:

```text
042
```

So constructing exactly `n` positions covers:

```text
0 ... UpperLimit
```

without separately handling 1-digit, 2-digit, 3-digit numbers.

---

## 7.1 Example

With 3 positions:

```text
000 → 0
001 → 1
...
042 → 42
...
999 → 999
```

This is why the recursion can allow:

```text
digit = 0
```

even at the first position.

---

## 7.2 Important warning

Leading zeros are harmless for properties like:

```text
sum of digits
```

because:

```text
0 + sum = sum
```

But for some properties, leading zeros may need an additional state such as:

```text
started
```

This is discussed later in the recognition section.

---

# 8. How to Stay Below an Upper Limit

Suppose:

```text
UpperLimit = 10987
```

and we construct from left to right.

---

## 8.1 Case 1 — Prefix is still equal

Constructed prefix:

```text
109__
```

Upper-limit prefix:

```text
109__
```

At the next position, upper-limit digit is:

```text
8
```

Therefore the next digit may only be:

```text
0 ... 8
```

Choosing:

```text
9
```

would make the number exceed the upper limit.

---

## 8.2 Case 2 — Prefix is already smaller

Constructed prefix:

```text
108__
```

Upper-limit prefix:

```text
109__
```

Since:

```text
108 < 109
```

the final number is guaranteed to be below the upper limit.

Therefore the next digit may be:

```text
0 ... 9
```

This two-case distinction is exactly what `tight` stores.

---

# 9. The `tight` State

The lecture introduces one additional state variable:

```text
tight
```

Meaning:

```text
Does the prefix constructed so far
still exactly match
the prefix of the UpperLimit?
```

A convenient implementation convention is:

```text
tight = 1
→ prefix is equal to UpperLimit prefix

tight = 0
→ prefix is already smaller
```

The lecture slide describes the two conceptual cases as:

```text
tight case 1:
prefix matches

other case:
UpperLimit prefix is already greater
```

The code uses a boolean-like `0/1` representation.

---

## 9.1 Why the word "tight"?

When:

```text
tight = 1
```

our next digit is **tightly bounded** by the corresponding upper-limit digit.

When:

```text
tight = 0
```

the restriction is released:

```text
0 ... 9
```

are allowed.

---

# 10. How `tight` Changes

Suppose:

```text
upperDigit
=
UpperLimit[index]
```

and we choose:

```text
digit
```

---

## 10.1 Current `tight = 0`

We are already smaller.

So regardless of the chosen digit:

```text
newTight = 0
```

Once loose, always loose.

---

## 10.2 Current `tight = 1`

We are still equal.

If we choose:

```text
digit == upperDigit
```

the new prefix remains equal:

```text
newTight = 1
```

If we choose:

```text
digit < upperDigit
```

the new prefix becomes smaller:

```text
newTight = 0
```

---

## 10.3 Compact formula

```cpp
bool newTight = tight && (digit == upperDigit);
```

This is one of the most important Digit-DP lines.

---

# 11. Warm-up State — `dp[index][tight][sum]`

We now return to the lecture problem:

```text
Count numbers <= UpperLimit
whose digit sum = K.
```

Three pieces of information determine the future:

```text
index
tight
sum
```

---

## 11.1 STATE

Define:

```text
dp[index][tight][sum]
=
number of valid ways
to fill positions index ... n-1

such that:

1. the final number <= UpperLimit

2. current prefix relation is represented by `tight`

3. the sum of digits chosen so far is `sum`

4. final digit sum becomes K
```

This is the lecture's central Digit-DP state.

---

## 11.2 Why each parameter is necessary

### `index`

Tells us:

```text
which digit position we are filling
```

### `tight`

Tells us:

```text
whether current digit is bounded
by UpperLimit[index]
```

### `sum`

Tells us:

```text
how much digit sum has already been accumulated
```

These three values completely describe the future subproblem.

---

# 12. Warm-up Transition

At:

```text
(index, tight, sum)
```

first determine the maximum digit we may place.

---

## 12.1 Highest allowed digit

If:

```text
tight = 1
```

then:

```text
highestAllowed
=
UpperLimit[index]
```

If:

```text
tight = 0
```

then:

```text
highestAllowed
=
9
```

In C++:

```cpp
int highestAllowed = tight ? upper[index] - '0' : 9;
```

---

## 12.2 Try every valid digit

```text
digit = 0 ... highestAllowed
```

For each digit:

```text
newSum
=
sum + digit
```

and:

```text
newTight
=
tight
AND
digit == upperDigit
```

Transition:

```text
dp[index][tight][sum]
=
Σ
dp[
    index+1
][
    newTight
][
    sum+digit
]
```

where the sum is over every allowed digit.

---

## 12.3 Optional pruning

If:

```text
sum > K
```

then we can immediately return:

```text
0
```

because future digits are non-negative.

This is also shown in the lecture code.

---

# 13. Base Case and Final Subproblem

## 13.1 BASE CASE

When:

```text
index == n
```

all digits have been filled.

If:

```text
sum == K
```

we constructed one valid number:

```text
return 1
```

Otherwise:

```text
return 0
```

Equivalent lecture notation:

```text
dp[n][anything][K] = 1
```

and:

```text
dp[n][anything][sum != K] = 0
```

---

## 13.2 FINAL SUBPROBLEM

Initially:

```text
index = 0
```

No digit has been chosen, so:

```text
sum = 0
```

At the first digit, we are still exactly constrained by the upper limit:

```text
tight = 1
```

Therefore:

```text
answer
=
dp[0][1][0]
```

This is exactly the final subproblem shown in the lecture.

---

# 14. Why DP Works — Overlapping Subproblems

This is one of the most important lecture slides.

Suppose:

```text
UpperLimit = 66245
```

Different prefixes may look like:

```text
091__
019__
082__
505__
```

The exact prefixes differ.

But the lecture points out that they can all reach the same DP information:

```text
index = same position
tight = 0
sum   = 10
```

Once:

```text
tight = 0
```

the exact earlier prefix no longer matters.

The future only depends on:

```text
how many positions remain
+
current sum
```

So all those recursion paths have the same future answer.

That is an overlapping subproblem.

Memoization computes it once.

---

## 14.1 This is the real power of Digit DP

Digit DP does **not** memoize whole numbers.

It memoizes:

```text
compressed information
about the prefix
```

such as:

```text
index
tight
sum
```

This turns a huge number-generation tree into a small DP state space.

---

# 15. Warm-up Dry Run

Use a smaller example:

```text
UpperLimit = 123
Target digit sum K = 4
```

We call:

```text
solve(index=0, tight=1, sum=0)
```

Upper-limit digits:

```text
1 2 3
```

---

## Step 1 — index 0

Since:

```text
tight = 1
```

highest allowed digit is:

```text
1
```

Choices:

```text
0
1
```

---

### Choose `0`

Prefix:

```text
0__
```

Since:

```text
0 < 1
```

now:

```text
newTight = 0
```

State:

```text
solve(1, 0, 0)
```

From now on digits may go:

```text
0 ... 9
```

---

### Choose `1`

Prefix:

```text
1__
```

Still equal to upper limit.

So:

```text
newTight = 1
newSum   = 1
```

State:

```text
solve(1, 1, 1)
```

---

## Step 2 — state `(1,1,1)`

Upper-limit digit at index 1:

```text
2
```

Allowed digits:

```text
0,1,2
```

If choose:

```text
2
```

then:

```text
newTight = 1
newSum   = 3
```

State:

```text
solve(2,1,3)
```

If choose:

```text
1
```

then prefix:

```text
11_
```

which is already less than:

```text
12_
```

So:

```text
newTight = 0
```

---

## Step 3 — finish

At:

```text
index = 3
```

check:

```text
sum == 4 ?
```

Valid numbers include examples such as:

```text
004
013
022
031
040
103
112
121
```

subject to:

```text
number <= 123
```

The recursion counts them without iterating through all integers from `0` to `123`.

---

# 16. Warm-up C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

string upperLimit;
int targetSum;

vector<vector<vector<ll>>> memo;

ll solve(int index, int tight, int sum) {
    int n = (int)upperLimit.size();

    if (sum > targetSum) {
        return 0;
    }

    if (index == n) {
        return sum == targetSum;
    }

    ll& answer = memo[index][tight][sum];

    if (answer != -1) {
        return answer;
    }

    answer = 0;

    int upperDigit = upperLimit[index] - '0';
    int highestAllowed = tight ? upperDigit : 9;

    for (int digit = 0; digit <= highestAllowed; digit++) {
        int newTight = tight && (digit == upperDigit);

        answer += solve(
            index + 1,
            newTight,
            sum + digit
        );
    }

    return answer;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> upperLimit >> targetSum;

    int n = (int)upperLimit.size();
    int maxSum = 9 * n;

    memo.assign(
        n,
        vector<vector<ll>>(
            2,
            vector<ll>(maxSum + 1, -1)
        )
    );

    cout << solve(0, 1, 0) << '\n';

    return 0;
}
```

---

# 17. Complexity

Suppose:

```text
n
=
number of digits
```

and maximum relevant digit sum is:

```text
S
```

State dimensions:

```text
index → n

tight → 2

sum   → S+1
```

So number of states:

```text
O(n × 2 × S)
```

Each state tries at most:

```text
10 digits
```

Therefore:

```text
Time
=
O(n × 2 × S × 10)
```

which simplifies to:

```text
O(n × S)
```

up to the constant digit factor.

Space:

```text
O(n × 2 × S)
```

For a number up to roughly `10^18`:

```text
n ≈ 19
```

so this is tiny compared with enumerating the numbers themselves.

---

# 18. Why Top-Down Recursion Fits Digit DP

The lecture explicitly asks:

```text
Why is recursive DP better here?
```

A useful reason is that the valid digit range changes according to:

```text
tight
```

Top-down recursion expresses this directly:

```text
highestAllowed
=
tight ? upperDigit : 9
```

Then recurse only into reachable states.

This makes the transition close to the way we naturally reason about constructing the number.

---

## 18.1 Top-down mental model

```text
At this digit:

What digits can I place?

For each valid digit:

What is the next tight state?
What is the next property state?
```

This maps cleanly to recursion.

---

# 19. Problem 1 — AtCoder Digit Sum

Lecture problem:

**AtCoder Educational DP Contest S — Digit Sum**

https://atcoder.jp/contests/dp/tasks/dp_s

The lecture describes the task as counting numbers in the relevant range whose:

```text
sum of digits
```

is divisible by:

```text
D
```

Instead of:

```text
sum == K
```

we want:

```text
sum % D == 0
```

---

# 20. Why Storing Full Digit Sum Is Wasteful

Suppose:

```text
D <= 100
```

If the upper bound were very long, full digit sum might range over many values.

But the final condition only asks:

```text
sum % D == 0
```

So two sums such as:

```text
7
```

and:

```text
107
```

are equivalent for future divisibility when:

```text
D = 100
```

because both have remainder:

```text
7
```

We do not need the original sums.

---

# 21. Modulo-State Compression

Use the identity from the lecture:

```text
(a + b) % D
=
(
    (a % D)
    +
    (b % D)
)
% D
```

Therefore instead of storing:

```text
sum
```

store:

```text
remainder
=
sum % D
```

State size changes from:

```text
all possible sums
```

to only:

```text
0 ... D-1
```

This is state compression.

---

## 21.1 Example

Let:

```text
D = 3
```

Build number:

```text
12345
```

Track only the digit-sum remainder.

Start:

```text
remainder = 0
```

Choose `1`:

```text
(0 + 1) % 3
=
1
```

Choose `2`:

```text
(1 + 2) % 3
=
0
```

Choose `3`:

```text
(0 + 3) % 3
=
0
```

Choose `4`:

```text
(0 + 4) % 3
=
1
```

Choose `5`:

```text
(1 + 5) % 3
=
0
```

Final remainder:

```text
0
```

so the digit sum is divisible by `3`.

We never needed to store the exact running sum.

---

# 22. Digit Sum State and Transition

## STATE

Define:

```text
dp[index][tight][remainder]
=
number of valid ways
to complete the number

where:

index
=
current digit position

tight
=
whether prefix still matches
the upper-limit prefix

remainder
=
digit sum so far modulo D
```

---

## TRANSITION

Try:

```text
digit = 0 ... highestAllowed
```

Then:

```text
newRemainder
=
(remainder + digit) % D
```

and:

```text
newTight
=
tight
AND
digit == upperDigit
```

So:

```text
dp[index][tight][remainder]
=
Σ
dp[
    index+1
][
    newTight
][
    (remainder+digit)%D
]
```

---

## BASE CASE

At:

```text
index == n
```

valid if:

```text
remainder == 0
```

So:

```text
return 1
```

otherwise:

```text
return 0
```

---

## FINAL SUBPROBLEM

```text
solve(
    index = 0,
    tight = 1,
    remainder = 0
)
```

---

# 23. AtCoder Digit Sum Dry Run

Use:

```text
UpperLimit = 25
D = 3
```

We want numbers whose digit sum is divisible by `3`.

Represent all numbers with 2 digits:

```text
00 ... 25
```

---

## Start

```text
solve(0,1,0)
```

Upper first digit:

```text
2
```

Choices:

```text
0
1
2
```

---

### Choose `1`

Prefix:

```text
1_
```

Since:

```text
1 < 2
```

new state:

```text
tight = 0
```

Remainder:

```text
1 % 3 = 1
```

State:

```text
solve(1,0,1)
```

At second digit we may choose:

```text
0 ... 9
```

We need final:

```text
(remainder + digit) % 3 == 0
```

So digits:

```text
2
5
8
```

work.

Numbers:

```text
12
15
18
```

---

### Choose `2`

Prefix:

```text
2_
```

still tight.

Second digit is limited by:

```text
5
```

So we may try only:

```text
0 ... 5
```

This is exactly why `tight` is necessary.

---

# 24. AtCoder Digit Sum C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll MOD = 1000000007LL;

string upperLimit;
int d;

vector<vector<vector<ll>>> memo;

ll solve(int index, int tight, int remainder) {
    int n = (int)upperLimit.size();

    if (index == n) {
        return remainder == 0;
    }

    ll& answer = memo[index][tight][remainder];

    if (answer != -1) {
        return answer;
    }

    answer = 0;

    int upperDigit = upperLimit[index] - '0';
    int highestAllowed = tight ? upperDigit : 9;

    for (int digit = 0; digit <= highestAllowed; digit++) {
        int newTight = tight && (digit == upperDigit);
        int newRemainder = (remainder + digit) % d;

        answer += solve(
            index + 1,
            newTight,
            newRemainder
        );

        answer %= MOD;
    }

    return answer;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> upperLimit >> d;

    int n = (int)upperLimit.size();

    memo.assign(
        n,
        vector<vector<ll>>(
            2,
            vector<ll>(d, -1)
        )
    );

    ll answer = solve(0, 1, 0);

    // 000...000 represents the number 0.
    // AtCoder DP S asks for positive integers,
    // so remove this one construction.
    answer = (answer - 1 + MOD) % MOD;

    cout << answer << '\n';

    return 0;
}
```

---

## 24.1 Complexity

States:

```text
n × 2 × D
```

Each state tries at most:

```text
10 digits
```

Therefore:

```text
Time
=
O(n × 2 × D × 10)
```

which is effectively:

```text
O(nD)
```

with a small constant.

Space:

```text
O(n × 2 × D)
```

This is extremely small compared with the numeric size of the upper limit.

---

# 25. How to Handle a Range `[L,R]`

Digit DP is easiest when the helper counts:

```text
numbers from 0 to X
```

Define:

```text
count(X)
=
number of valid integers
from 0 to X
```

Then range problems can be reduced using prefix-count logic.

The lecture discusses this near the end using two limit computations.

---

# 26. Lecture Range Formula

The lecture's handwritten derivation uses:

```text
count(R)
-
count(L)
+
isValid(L)
```

Why add `isValid(L)`?

Because:

```text
count(R) - count(L)
```

counts:

```text
(L, R]
```

It excludes `L`.

If `L` itself satisfies the property, add it back.

So:

```text
answer[L,R]
=
count(R)
-
count(L)
+
isValid(L)
```

This is the lecture's range-handling formulation.

---

## 26.1 Equivalent prefix interpretation

Think:

```text
[0 ... R]
minus
[0 ... L]
```

leaves:

```text
(L ... R]
```

Then:

```text
+ L
```

only if `L` is valid.

This avoids needing to manually create `L-1` inside the lecture's formulation.

---

## 26.2 Practical organization

The lecture notes suggest:

```text
one DP computation / memo structure for R

one DP computation / memo structure for L

one direct validity check for L
```

Then combine the results.

---

# 27. Homework Pattern

Lecture homework:

```text
Count numbers from L to R
such that:

odd-indexed positions contain odd digits

even-indexed positions contain even digits
```

The goal is to practice designing the **property state / transition**.

---

## 27.1 What stays the same?

We still need:

```text
index
tight
```

because:

```text
index
```

tells us the digit position,

and:

```text
tight
```

keeps the constructed number under the upper limit.

---

## 27.2 What changes?

The property is determined directly by:

```text
index parity
```

So at each position:

```text
if this is an odd index:
    allow odd digits

if this is an even index:
    allow even digits
```

No running `sum` is needed.

This is an important lesson:

```text
Digit DP does NOT always use
dp[index][tight][sum].
```

The extra state depends on the property.

---

## 27.3 Conceptual transition

At:

```text
(index, tight)
```

find:

```text
highestAllowed
```

Then try only digits satisfying the positional rule.

Example set:

```text
odd digits:
1,3,5,7,9
```

```text
even digits:
0,2,4,6,8
```

subject also to:

```text
digit <= highestAllowed
```

Then update:

```text
newTight
```

in the usual way.

---

# 28. Common Digit-DP State Parameters

Digit DP is a framework.

The state depends on the property.

---

## 28.1 Exact digit sum

```text
dp[index][tight][sum]
```

---

## 28.2 Digit sum divisible by D

```text
dp[index][tight][sumModD]
```

---

## 28.3 Number itself modulo M

If property asks:

```text
number % M
```

then when appending digit `d`:

```text
newRemainder
=
(
    oldRemainder * 10
    +
    d
)
% M
```

State:

```text
dp[index][tight][remainder]
```

---

## 28.4 Count occurrences of a digit

Example:

```text
exactly three 7s
```

State may include:

```text
count7
```

---

## 28.5 Adjacent-digit property

If the next choice depends on the previous digit:

```text
dp[index][tight][previousDigit]
```

Possibly add other property dimensions too.

---

## 28.6 Leading-zero-sensitive property

If leading zeros must not behave like actual digits, add:

```text
started
```

Meaning:

```text
have we placed a non-zero digit yet?
```

State may become:

```text
dp[index][tight][started][...]
```

This is not needed for the lecture's basic digit-sum example because leading zeros do not change the sum.

---

# 29. Common Mistakes

## Mistake 1 — Wrong highest digit

Wrong:

```text
always try 0 ... 9
```

When:

```text
tight = 1
```

you must stop at:

```text
upperLimit[index]
```

---

## Mistake 2 — Wrong `newTight`

Wrong:

```text
newTight = (digit == upperDigit)
```

This ignores whether we were already loose.

Correct:

```cpp
int newTight = tight && (digit == upperDigit);
```

---

## Mistake 3 — Memoizing by index only

Two states at the same index can have different:

```text
tight
sum
remainder
```

and therefore different answers.

Memoize every independent state component.

---

## Mistake 4 — Forgetting leading zeros

When counting:

```text
0 ... UpperLimit
```

numbers shorter than the upper-limit length are represented with leading zeros.

Do not accidentally reject them unless the property specifically requires it.

---

## Mistake 5 — Storing more property information than needed

For:

```text
digit sum divisible by D
```

do not store full:

```text
sum
```

Store:

```text
sum % D
```

This is the state-compression lesson of the AtCoder example.

---

## Mistake 6 — Range off-by-one

Be precise about whether your helper counts:

```text
[0,X]
```

or:

```text
[1,X]
```

The lecture's range formula is:

```text
count(R)
-
count(L)
+
isValid(L)
```

because its `count(X)` is inclusive through `X`.

---

# 30. Contest Recognition Checklist

Ask these questions.

---

## 30.1 Is the range enormous?

Example:

```text
R <= 10^18
```

If yes, normal enumeration is impossible.

---

## 30.2 Is the condition based on digits?

Examples:

```text
digit sum

digit frequencies

digit parity

adjacent digits

remainder formed by digits
```

If yes:

```text
Digit DP is a candidate.
```

---

## 30.3 Can I define `count(X)`?

Try to convert:

```text
[L,R]
```

into one or two:

```text
0 ... X
```

problems.

Digit DP naturally solves an upper-bound problem.

---

## 30.4 What is my position state?

Almost always:

```text
index
```

---

## 30.5 Do I need `tight`?

If constructing a number bounded by:

```text
UpperLimit
```

yes.

Mental meaning:

```text
tight = prefix still equal?
```

---

## 30.6 What property information affects the future?

Examples:

```text
sum
sum % D
remainder
count of special digit
previous digit
started
```

Only store information needed to decide the future.

---

## 30.7 Can the property state be compressed?

Ask:

```text
Do I really need the full value?
```

AtCoder Digit Sum:

```text
full digit sum
        ↓
sum % D
```

This is exactly the same DP-state-design principle used throughout advanced DP.

---

# 31. Final Mental Model

## Digit DP = construct digits under a bound

```text
UPPER LIMIT
   1 2 0 2 5
   |
   v

construct left → right

_ _ _ _ _
```

At every position:

```text
What digits are allowed?
```

That depends on:

```text
tight
```

---

## Tight

```text
tight = 1

prefix == upper-limit prefix

next digit:
0 ... upperDigit
```

```text
tight = 0

prefix already smaller

next digit:
0 ... 9
```

---

## Core state

For exact digit sum:

```text
dp[index][tight][sum]
```

For divisible digit sum:

```text
dp[index][tight][sum % D]
```

---

## Transition

```text
for each allowed digit d:

newTight
=
tight && (d == upperDigit)

newPropertyState
=
update(propertyState, d)

answer +=
dp[index+1][newTight][newPropertyState]
```

---

# Final Revision Box

```text
WHEN TO THINK DIGIT DP
────────────────────────────────────

Huge range:
up to 10^18 or beyond

AND

property depends on digits


BUILD NUMBER
────────────────────────────────────

MSD → LSD


TIGHT
────────────────────────────────────

tight = 1
prefix still equals UpperLimit prefix

digit range:
0 ... UpperLimit[index]


tight = 0
prefix already smaller

digit range:
0 ... 9


UPDATE TIGHT
────────────────────────────────────

newTight
=
tight
&&
(digit == upperDigit)


EXACT DIGIT SUM
────────────────────────────────────

State:

dp[index][tight][sum]


Transition:

for digit:
    dp(...)
    with sum + digit


Base:

index == n
→ valid iff sum == K


DIGIT SUM DIVISIBLE BY D
────────────────────────────────────

Do NOT keep full sum.

Keep:

sum % D

newRemainder
=
(remainder + digit) % D


RANGE [L,R]
────────────────────────────────────

Lecture formulation:

answer
=
count(R)
-
count(L)
+
isValid(L)


CORE DIGIT-DP QUESTIONS
────────────────────────────────────

1. What is my upper limit?

2. What does `tight` mean?

3. What is the highest digit allowed now?

4. Which property information
   affects the future?

5. Can that property state
   be compressed?

6. What does the base case mean?

7. Does my helper count 0?
   Does the problem want 0?
```
