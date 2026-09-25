# Prefix Sum & Difference Array Pattern Roadmap — Codeforces + LeetCode (to CM)

> Goal: recognize the pattern quickly, derive the invariant/formula
> yourself, and solve without reading a solution.
>
> **Each pattern now includes a real-world recognition drill, contest
> translation, reference pseudocode, and an optimized direct practice
> set. Full solutions are intentionally omitted.**
>
> **Pseudocode formatting:** practice tables stay compact; each
> problem's pseudocode appears below its table in a fenced block so
> GitHub preserves line breaks and indentation.
>
> Renderer-safe notation is used throughout (no LaTeX dependency).

------------------------------------------------------------------------

## Table of Contents

- [How to use this sheet](#how-to-use)
- [PREFIX SUM PATTERNS](#prefix-sum-patterns)
  - [Pattern 1 — Basic Range Sum / Static Queries](#pattern-1)
  - [Pattern 2 — Prefix + Suffix / Split at i](#pattern-2)
  - [Pattern 3 — Prefix Sum + Hash Map: Subarray Sum = K](#pattern-3)
  - [Pattern 4 — Prefix Modulo / Divisibility](#pattern-4)
  - [Pattern 5 — Transform Values, Then Prefix](#pattern-5)
  - [Pattern 6 — Prefix XOR / Prefix State](#pattern-6)
  - [Pattern 7 — Weighted Prefix Sum / Index \* A\[i\]](#pattern-7)
  - [Pattern 8 — Prefix of Prefix / Double Prefix](#pattern-8)
  - [Pattern 9 — 2D Prefix Sum](#pattern-9)
- [DIFFERENCE ARRAY PATTERNS](#difference-array-patterns)
  - [Pattern 10 — Basic Range Addition](#pattern-10)
  - [Pattern 11 — Difference Array as Event / Sweep Line](#pattern-11)
  - [Pattern 12 — Difference + Prefix + Another Prefix](#pattern-12)
  - [Pattern 13 — Difference Array + Coordinate
    Compression](#pattern-13)
  - [Pattern 14 — Difference of a Prefix Array / Reconstruct
    Original](#pattern-14)
  - [Pattern 15 — AP Range Update (Linear Difference)](#pattern-15)
  - [Pattern 16 — GP / Recurrence-Based Range Update](#pattern-16)
- [Pattern 17 — Prefix Sum + Binary Search / K-th Value](#pattern-17)
- [Pattern 18 — Prefix Sum + Monotonic Stack / Boundaries](#pattern-18)
- [Pattern 19 — Prefix on Trees / Paths](#pattern-19)
- [CM-Level Recognition Map](#cm-level-recognition-map)
- [Suggested Order to CM](#suggested-order-to-cm)
  - [Stage 1 — automatic fundamentals](#stage-1-automatic-fundamentals)
  - [Stage 2 — Div2 B/C strength](#stage-2-div2-bc-strength)
  - [Stage 3 — Expert → CM](#stage-3-expert-cm)
- [60-Second Contest Checklist](#60-second-contest-checklist)
- [High-value CF core set](#high-value-cf-core-set)
- [Final rule](#final-rule)

------------------------------------------------------------------------

<a id="how-to-use"></a>

# How to use this sheet

## Visual memory method

For maximum retention, use the same four-step decoding loop for every
pattern:

``` text
PROBLEM STORY
     ↓
WHAT CHANGES / WHAT IS ASKED?
     ↓
ARRAY / PREFIX / EVENT STATE
     ↓
ONE EQUATION OR INVARIANT
     ↓
CODE
```

When reviewing, do **not** memorize the final pseudocode first. Cover it
and try to recall:

``` text
1. Recognition signal
2. Real-world picture
3. One-line invariant
4. Pseudocode
```

The diagrams are deliberately simple: the goal is to make the pattern
image appear in your head during a contest.

For every problem:

1.  Spend 3–5 minutes decoding the statement into an
    array/subarray/range model.
2.  Ask: **what does one prefix represent?**
3.  Write the algebra before coding.
4.  If stuck after ~20–30 minutes, read only Hint 1.
5.  Re-solve failed problems 2–3 days later.
6.  For CF, prioritize problems around your current level, then
    gradually push toward 1600–1900.

Core identity:

``` text
pref[0] = 0
pref[i] = A[1] + ... + A[i]

sum(L,R) = pref[R] - pref[L-1]
```

Difference-array identity:

``` text
add X to [L,R]:

diff[L]   += X
diff[R+1] -= X

A[i] = A[i-1] + diff[i]
```

------------------------------------------------------------------------

<a id="prefix-sum-patterns"></a>

# PREFIX SUM PATTERNS

<a id="pattern-1"></a>

## Pattern 1 — Basic Range Sum / Static Queries

### Recognition signal — detailed

#### Real-world decoding drill

**Scenario:** Bank statement: many questions ask spending between two
dates. Store money spent up to each day; subtract the total before L
from the total through R.

**Translate it during a contest:**

``` text
story objects/events
        ↓
array / prefix state / interval events
        ↓
what can be precomputed once?
        ↓
Precompute once, answer each static contiguous range in O(1).
```

**Reference pseudocode:**

``` text
build pref; for query(L,R): ans = pref[R] - pref[L-1]
```

**Statement clues**

``` text
- many queries ask about [L,R]
- array is static: values do not change between queries
- query asks sum / count / cost / number of marked items in a range
- N and Q are large enough that O(length of range) per query is too slow
```

**Translate the statement**

``` text
"What is inside [L,R]?"
        ↓
"Take everything up to R"
        -
"remove everything before L"

answer = pref[R] - pref[L-1]
```

**Mini dry run**

``` text
A:      3   1   4   2   5
index:  1   2   3   4   5

pref:   0   3   4   8  10  15
index:  0   1   2   3   4   5

query [2,4]

whole prefix to 4:  3 + 1 + 4 + 2 = 10
remove before L=2:  3               =  3
                                         --
answer                                  =  7
```

Visualization:

``` text
[ 3 ][ 1 ][ 4 ][ 2 ][ 5 ]
  X   |<---- target ---->|
      L                 R

pref[R]      = [3 1 4 2]
pref[L-1]    = [3]
subtract     =   [1 4 2]
```

**Real-world mapping**

Imagine daily spending. `pref[d]` is total money spent from day 1
through day `d`. Spending from day `L` to day `R` is total-through-R
minus total-before-L.

### 🧠 Retention diagram — **STORE THE PAST, SUBTRACT THE UNWANTED**

``` text
Daily spending
Day:      1    2    3    4    5
Spend:   €3   €1   €4   €2   €5

Running wallet total:
          3    4    8   10   15

Question: spending on days 2..4?

        everything through day 4
        [ 3 | 1  4  2 ] = 10
          ^ remove this     -3
                            ---
                              7

MEMORY HOOK:
RANGE = BIG PREFIX - PREFIX BEFORE RANGE
```

**Real-world trigger:** bank statements, website visits per day,
calories per day, tickets sold per hour — static data with many interval
questions.

**60-second question**

> Is the data static, and am I repeatedly asking for an additive
> quantity over a contiguous range?

If yes, basic prefix sum should be your first thought.

**C++ template**

``` cpp
vector<long long> pref(n + 1, 0);
for (int i = 1; i <= n; ++i) {
    pref[i] = pref[i - 1] + a[i];
}

auto rangeSum = [&](int L, int R) -> long long {
    return pref[R] - pref[L - 1];
};
```

### Core idea

``` text
many queries
sum/value/count over [L,R]
array does not change
```

Think:

``` text
whole prefix to R
-------------------->
          unwanted
<-------- L-1

answer = pref[R] - pref[L-1]
```

### Problems

### Optimized direct practice set

> These are intentionally filtered to problems that directly exercise
> this pattern. Warm-ups that mainly belong to another topic were
> removed from this section.
>
> **Reference pseudocode is problem-specific**: it shows how this exact
> problem uses the pattern, not merely the generic pattern template.

|  \# | Problem                                                                                 | Site  | Why it maps to this pattern                       | Approach                                                      |
|----:|-----------------------------------------------------------------------------------------|:-----:|---------------------------------------------------|---------------------------------------------------------------|
|   1 | [Range Sum Query - Immutable](https://leetcode.com/problems/range-sum-query-immutable/) |  LC   | Store one extra zero before the array.            | Precompute once, answer each static contiguous range in O(1). |
|   2 | [K Radius Subarray Averages](https://leetcode.com/problems/k-radius-subarray-averages/) |  LC   | Every valid answer is one fixed-length range sum. | Precompute once, answer each static contiguous range in O(1). |
|   3 | [Kuriyama Mirai's Stones](https://codeforces.com/problemset/problem/433/B)              |  CF   | Build prefix sums for original and sorted arrays. | Precompute once, answer each static contiguous range in O(1). |
|   4 | [Static Range Sum Queries](https://cses.fi/problemset/task/1646)                        | Bonus | Pure `pref[R]-pref[L-1]` drill.                   | Precompute once, answer each static contiguous range in O(1). |

#### Problem-specific reference pseudocode

> Try the problem first. Use these blocks only to verify the solution
> flow. Each one is problem-specific and formatted vertically for
> GitHub.

##### 1. [Range Sum Query - Immutable](https://leetcode.com/problems/range-sum-query-immutable/)

**Recognition:** Store one extra zero before the array.

``` text
pref[0]=0
build pref
sumRange(L,R)=pref[R+1]-pref[L]
```

##### 2. [K Radius Subarray Averages](https://leetcode.com/problems/k-radius-subarray-averages/)

**Recognition:** Every valid answer is one fixed-length range sum.

``` text
build pref
for i=k..n-k-1: L=i-k,R=i+k
ans[i]=(pref[R+1]-pref[L])/(2*k+1)
```

##### 3. [Kuriyama Mirai's Stones](https://codeforces.com/problemset/problem/433/B)

**Recognition:** Build prefix sums for original and sorted arrays.

``` text
p1=prefix(a)
sort b=a
p2=prefix(b)
type1 -> p1[R]-p1[L-1]
type2 -> p2[R]-p2[L-1]
```

##### 4. [Static Range Sum Queries](https://cses.fi/problemset/task/1646)

**Recognition:** Pure `pref[R]-pref[L-1]` drill.

``` text
pref[0]=0
for i=1..n: pref[i]=pref[i-1]+a[i]
query(L,R)=pref[R]-pref[L-1]
```

#### How to practice this section

1.  Read only the statement and try to name the pattern in **60
    seconds**.
2.  Write what the prefix/difference state represents in one sentence.
3.  Write the algebra or state transition before code.
4.  Use the pseudocode only after you have your own approach.
5.  Re-solve failed problems after 2–3 days without opening this sheet.

**Mastery:** You should identify this family almost immediately.

------------------------------------------------------------------------

<a id="pattern-2"></a>

## Pattern 2 — Prefix + Suffix / Split at i

### Recognition signal — detailed

#### Real-world decoding drill

**Scenario:** Balance scale / workload split: freeze a divider i.
Everything before i is the left load; everything after i is the right
load.

**Translate it during a contest:**

``` text
story objects/events
        ↓
array / prefix state / interval events
        ↓
what can be precomputed once?
        ↓
Freeze the split/index and derive both sides from cumulative totals.
```

**Reference pseudocode:**

``` text
build pref; total=pref[n]; for i: left=pref[i-1]; right=total-pref[i]; evaluate split
```

**Statement clues**

``` text
- choose / remove / split at index i
- compare left side with right side
- answer for every possible split
- contribution of i depends on everything before and/or after i
```

**Mental picture**

``` text
            i
            |
[ LEFT LEFT ][A[i]][ RIGHT RIGHT ]
<-- known -->       <-- known -->

left  = pref[i-1]
right = total - pref[i]
```

**Mini dry run**

``` text
A = [2, 5, 1, 4, 2]
total = 14

Try i = 3, A[i] = 1

left  = 2 + 5 = 7
right = 4 + 2 = 6

[ 2  5 ] [ 1 ] [ 4  2 ]
   7       i       6
```

If the problem says “find a pivot”, “split into two sides”, or “remove
one item and compare both sides”, this picture is often enough to expose
the formula.

**Real-world mapping**

A balance scale: position `i` is the fulcrum, and prefix/suffix
information tells you the total weight on each side without rescanning.

### 🧠 Retention diagram — **FREEZE THE DIVIDER**

``` text
Workers/tasks:
[ 2 ][ 5 ] | [ 1 ] | [ 4 ][ 2 ]
   LEFT        i        RIGHT
     7                    6

                 total = 14
left  = prefix before i
right = total - prefix through i

Think of moving a wall:

[LEFT] | [RIGHT]
         ^
      try every split
```

**Real-world trigger:** divide workload between two teams, balance money
before/after a date, compare traffic before/after a checkpoint.

**60-second question**

> If I freeze one index, can I describe everything on its left and right
> from cumulative information?

**C++ template**

``` cpp
vector<long long> pref(n + 1);
for (int i = 1; i <= n; ++i) pref[i] = pref[i - 1] + a[i];

long long total = pref[n];
for (int i = 1; i <= n; ++i) {
    long long left  = pref[i - 1];
    long long right = total - pref[i];
    // test/use left, a[i], right
}
```

### Core idea

``` text
left of i vs right of i
split array
remove one position
contribution from both sides
```

ASCII:

``` text
[ LEFT PART ][i][ RIGHT PART ]
     pref         total-pref
```

### Problems

### Optimized direct practice set

> These are intentionally filtered to problems that directly exercise
> this pattern. Warm-ups that mainly belong to another topic were
> removed from this section.
>
> **Reference pseudocode is problem-specific**: it shows how this exact
> problem uses the pattern, not merely the generic pattern template.

|  \# | Problem                                                                                                                       | Site | Why it maps to this pattern                                          | Approach                                                             |
|----:|-------------------------------------------------------------------------------------------------------------------------------|:----:|----------------------------------------------------------------------|----------------------------------------------------------------------|
|   1 | [Find Pivot Index](https://leetcode.com/problems/find-pivot-index/)                                                           |  LC  | Need equality of left and right sums.                                | Freeze the split/index and derive both sides from cumulative totals. |
|   2 | [Minimum Average Difference](https://leetcode.com/problems/minimum-average-difference/)                                       |  LC  | Prefix gives left sum; total-prefix gives right.                     | Freeze the split/index and derive both sides from cumulative totals. |
|   3 | [Sum of Absolute Differences in a Sorted Array](https://leetcode.com/problems/sum-of-absolute-differences-in-a-sorted-array/) |  LC  | Sorted order lets you split absolute values into left/right algebra. | Freeze the split/index and derive both sides from cumulative totals. |
|   4 | [Number of Ways to Split Array](https://leetcode.com/problems/number-of-ways-to-split-array/)                                 |  LC  | `left >= right`.                                                     | Freeze the split/index and derive both sides from cumulative totals. |
|   5 | [Array Division](https://codeforces.com/problemset/problem/808/D)                                                             |  CF  | Prefix sums + membership lookup on the opposite side.                | Freeze the split/index and derive both sides from cumulative totals. |

#### Problem-specific reference pseudocode

> Try the problem first. Use these blocks only to verify the solution
> flow. Each one is problem-specific and formatted vertically for
> GitHub.

##### 1. [Find Pivot Index](https://leetcode.com/problems/find-pivot-index/)

**Recognition:** Need equality of left and right sums.

``` text
total=sum(a)
left=0
for i: right=total-left-a[i]
if left==right return i
left+=a[i]
```

##### 2. [Minimum Average Difference](https://leetcode.com/problems/minimum-average-difference/)

**Recognition:** Prefix gives left sum; total-prefix gives right.

``` text
\`pref=0,total=sum(a)
for i: pref+=a[i]
left=pref/(i+1)
right=(i==n-1?0:(total-pref)/(n-i-1))
minimize
```

##### 3. [Sum of Absolute Differences in a Sorted Array](https://leetcode.com/problems/sum-of-absolute-differences-in-a-sorted-array/)

**Recognition:** Sorted order lets you split absolute values into
left/right algebra.

``` text
pref=prefix(nums)
for i: left=nums[i]*i-pref[i]
right=(pref[n]-pref[i+1])-nums[i]*(n-i-1)
ans[i]=left+right
```

##### 4. [Number of Ways to Split Array](https://leetcode.com/problems/number-of-ways-to-split-array/)

**Recognition:** `left >= right`.

``` text
total=sum(a)
left=0
for i=0..n-2: left+=a[i]
if left>=total-left: ans++
```

##### 5. [Array Division](https://codeforces.com/problemset/problem/808/D)

**Recognition:** Prefix sums + membership lookup on the opposite side.

``` text
\`total=sum(a)
scan split with sets of seen/unseen values
diff=
```

#### How to practice this section

1.  Read only the statement and try to name the pattern in **60
    seconds**.
2.  Write what the prefix/difference state represents in one sentence.
3.  Write the algebra or state transition before code.
4.  Use the pseudocode only after you have your own approach.
5.  Re-solve failed problems after 2–3 days without opening this sheet.

------------------------------------------------------------------------

<a id="pattern-3"></a>

## Pattern 3 — Prefix Sum + Hash Map: Subarray Sum = K

### Recognition signal — detailed

#### Real-world decoding drill

**Scenario:** Bank balance history: if the balance is P now and you need
an interval whose net change is K, search for an earlier balance P-K.

**Translate it during a contest:**

``` text
story objects/events
        ↓
array / prefix state / interval events
        ↓
what can be precomputed once?
        ↓
Turn the subarray equation into a required previous prefix and look it up.
```

**Reference pseudocode:**

``` text
freq[0]=1; pref=0; for x: pref+=x; ans+=freq[pref-K]; freq[pref]++
```

**Statement clues**

``` text
- count subarrays whose sum is exactly K
- longest subarray with a required sum/state
- values may be negative, so sliding window is unsafe
- need to know whether a previous cumulative state existed
```

Start from the equation, not the template:

``` text
sum(L..R) = K

pref[R] - pref[L-1] = K
pref[L-1] = pref[R] - K
```

So at position `R` you ask:

``` text
"How many OLD prefixes equal currentPrefix - K?"
```

**Mini dry run**

``` text
A = [1, 2, 1, 2], K = 3

prefix while scanning:
start: 0
after 1: 1
after 2: 3   need 0  -> found
after 1: 4   need 1  -> found
after 2: 6   need 3  -> found

subarrays:
[1,2]
    [2,1]
        [1,2]
```

State visualization:

``` text
old prefix ----------- current prefix
     P                     P+K
      \_____________________/
            subarray K
```

**Real-world mapping**

Your bank balance is cumulative. If your balance is `P` now and you want
an interval where net change was `K`, you need an earlier balance of
`P-K`.

### 🧠 Retention diagram — **CURRENT PREFIX LOOKS FOR AN OLD PREFIX**

``` text
Need subarray sum = K

old prefix                  current prefix
     P -------------------------- P + K
       |<------ subarray K ------>|

Equation:
current - old = K
old = current - K

At every position:
"Have I seen currentPrefix - K before?"
```

**Real-world trigger:** bank-balance history. To find a period where
your balance changed by exactly K, find an earlier balance exactly K
below the current balance.

**60-second question**

> Does a valid subarray correspond to “current cumulative value minus a
> required previous cumulative value”?

**C++ template — count subarrays**

``` cpp
unordered_map<long long, long long> freq;
freq[0] = 1;

long long pref = 0, ans = 0;
for (long long x : a) {
    pref += x;
    if (freq.count(pref - K)) ans += freq[pref - K];
    ++freq[pref];
}
```

For **longest** subarray, store the earliest index of each prefix
instead of a frequency.

This is one of the most important prefix patterns.

Derivation:

``` text
sum(L..R) = K

pref[R] - pref[L-1] = K

pref[L-1] = pref[R] - K
```

So while standing at `R`, ask:

``` text
How many previous prefixes equal pref[R] - K?
```

### Problems

### Optimized direct practice set

> These are intentionally filtered to problems that directly exercise
> this pattern. Warm-ups that mainly belong to another topic were
> removed from this section.
>
> **Reference pseudocode is problem-specific**: it shows how this exact
> problem uses the pattern, not merely the generic pattern template.

|  \# | Problem                                                                                                 | Site  | Why it maps to this pattern                      | Approach                                                                   |
|----:|---------------------------------------------------------------------------------------------------------|:-----:|--------------------------------------------------|----------------------------------------------------------------------------|
|   1 | [Subarray Sum Equals K](https://leetcode.com/problems/subarray-sum-equals-k/)                           |  LC   | Frequency map of previous prefix sums.           | Turn the subarray equation into a required previous prefix and look it up. |
|   2 | [Binary Subarrays With Sum](https://leetcode.com/problems/binary-subarrays-with-sum/)                   |  LC   | Same equation; binary values are not essential.  | Turn the subarray equation into a required previous prefix and look it up. |
|   3 | [Maximum Size Subarray Sum Equals k](https://leetcode.com/problems/maximum-size-subarray-sum-equals-k/) |  LC   | Store earliest index, not frequency.             | Turn the subarray equation into a required previous prefix and look it up. |
|   4 | [Subarray Sums II](https://cses.fi/problemset/task/1661)                                                | Bonus | Canonical arbitrary-integer version.             | Turn the subarray equation into a required previous prefix and look it up. |
|   5 | [Good Subarrays](https://codeforces.com/problemset/problem/1398/C)                                      |  CF   | Transform so required subarray sum becomes zero. | Turn the subarray equation into a required previous prefix and look it up. |

#### Problem-specific reference pseudocode

> Try the problem first. Use these blocks only to verify the solution
> flow. Each one is problem-specific and formatted vertically for
> GitHub.

##### 1. [Subarray Sum Equals K](https://leetcode.com/problems/subarray-sum-equals-k/)

**Recognition:** Frequency map of previous prefix sums.

``` text
freq[0]=1
pref=ans=0
for x: pref+=x
ans+=freq[pref-k]
freq[pref]++
```

##### 2. [Binary Subarrays With Sum](https://leetcode.com/problems/binary-subarrays-with-sum/)

**Recognition:** Same equation; binary values are not essential.

``` text
freq[0]=1
pref=ans=0
for bit: pref+=bit
ans+=freq[pref-goal]
freq[pref]++
```

##### 3. [Maximum Size Subarray Sum Equals k](https://leetcode.com/problems/maximum-size-subarray-sum-equals-k/)

**Recognition:** Store earliest index, not frequency.

``` text
first[0]=-1
pref=0
for i: pref+=a[i]
if first has pref-k: best=max(best,i-first[pref-k])
store first[pref] once
```

##### 4. [Subarray Sums II](https://cses.fi/problemset/task/1661)

**Recognition:** Canonical arbitrary-integer version.

``` text
freq[0]=1
pref=ans=0
for x: pref+=x
ans+=freq[pref-target]
freq[pref]++
```

##### 5. [Good Subarrays](https://codeforces.com/problemset/problem/1398/C)

**Recognition:** Transform so required subarray sum becomes zero.

``` text
pref=0
freq[0]=1
for i=1..n: pref += digit[i]-1
ans += freq[pref]
freq[pref]++
```

#### How to practice this section

1.  Read only the statement and try to name the pattern in **60
    seconds**.
2.  Write what the prefix/difference state represents in one sentence.
3.  Write the algebra or state transition before code.
4.  Use the pseudocode only after you have your own approach.
5.  Re-solve failed problems after 2–3 days without opening this sheet.

**CM skill:** Don't memorize `map[prefix-k]`; derive it from
`pref[R]-pref[L-1]=K`.

------------------------------------------------------------------------

<a id="pattern-4"></a>

## Pattern 4 — Prefix Modulo / Divisibility

### Recognition signal — detailed

#### Real-world decoding drill

**Scenario:** Clock arithmetic: two cumulative totals landing on the
same remainder differ by whole multiples of K.

**Translate it during a contest:**

``` text
story objects/events
        ↓
array / prefix state / interval events
        ↓
what can be precomputed once?
        ↓
Keep only the prefix remainder/state needed by the divisibility condition.
```

**Reference pseudocode:**

``` text
cnt[0]=1; pref=0; for x: pref+=x; r=normalize(pref%K); ans+=cnt[r]; cnt[r]++
```

**Statement clues**

``` text
- subarray sum divisible by K
- sum % K must equal 0 / some remainder
- count pairs of prefixes with compatible remainders
- actual prefix values are huge, but only remainder classes matter
```

**Derivation**

``` text
(pref[R] - pref[L-1]) % K = 0
        ↓
pref[R] % K = pref[L-1] % K
```

So equal prefix remainders form a valid divisible subarray.

**Mini dry run**

``` text
A = [4, 5, 0, -2, -3, 1], K = 5

prefix sums:      0  4  9  9  7  4  5
normalized rem:   0  4  4  4  2  4  0

Every pair of equal remainders encloses
a subarray whose sum is divisible by 5.
```

Visualization:

``` text
prefix state:
0 ---- 4 ---- 4 ---- 4 ---- 2 ---- 4 ---- 0
^                                          ^
same remainder 0 => middle sum % 5 == 0
```

**Real-world mapping**

Think of a clock. Two cumulative totals landing on the same clock
position differ by a whole number of rotations; modulo works the same
way.

### 🧠 Retention diagram — **SAME CLOCK POSITION = WHOLE ROTATIONS**

``` text
Modulo 5 clock:

          0
      4       1
       \     /
        3---2

prefix A lands at remainder 4
prefix B later also lands at remainder 4

4 ---- full rotations ----> 4

difference is divisible by 5
```

**Real-world trigger:** clocks, repeating weekly schedules, circular
counters. Exact totals do not matter; only the remainder state matters.

**60-second question**

> Does the condition care only about a sum modulo `K`, rather than its
> exact value?

**C++ template**

``` cpp
vector<long long> cnt(k, 0);
cnt[0] = 1;

long long pref = 0, ans = 0;
for (long long x : a) {
    pref += x;
    int r = (int)((pref % k + k) % k);
    ans += cnt[r];
    ++cnt[r];
}
```

Derivation:

``` text
sum(L..R) divisible by K

(pref[R] - pref[L-1]) % K = 0

pref[R] % K = pref[L-1] % K
```

Same remainder ⇒ divisible subarray between them.

Important normalization:

``` cpp
rem = ((sum % k) + k) % k;
```

### Problems

### Optimized direct practice set

> These are intentionally filtered to problems that directly exercise
> this pattern. Warm-ups that mainly belong to another topic were
> removed from this section.
>
> **Reference pseudocode is problem-specific**: it shows how this exact
> problem uses the pattern, not merely the generic pattern template.

|  \# | Problem                                                                                     | Site  | Why it maps to this pattern                                    | Approach                                                                   |
|----:|---------------------------------------------------------------------------------------------|:-----:|----------------------------------------------------------------|----------------------------------------------------------------------------|
|   1 | [Subarray Sums Divisible by K](https://leetcode.com/problems/subarray-sums-divisible-by-k/) |  LC   | Count equal prefix remainders.                                 | Keep only the prefix remainder/state needed by the divisibility condition. |
|   2 | [Continuous Subarray Sum](https://leetcode.com/problems/continuous-subarray-sum/)           |  LC   | Store earliest index for each remainder; enforce length \>= 2. | Keep only the prefix remainder/state needed by the divisibility condition. |
|   3 | [Make Sum Divisible by P](https://leetcode.com/problems/make-sum-divisible-by-p/)           |  LC   | What remainder must the removed subarray have?.                | Keep only the prefix remainder/state needed by the divisibility condition. |
|   4 | [Subarray Divisibility](https://cses.fi/problemset/task/1662)                               | Bonus | Handle negative modulo carefully.                              | Keep only the prefix remainder/state needed by the divisibility condition. |

#### Problem-specific reference pseudocode

> Try the problem first. Use these blocks only to verify the solution
> flow. Each one is problem-specific and formatted vertically for
> GitHub.

##### 1. [Subarray Sums Divisible by K](https://leetcode.com/problems/subarray-sums-divisible-by-k/)

**Recognition:** Count equal prefix remainders.

``` text
cnt[0]=1
pref=ans=0
for x: pref+=x
r=((pref%k)+k)%k
ans+=cnt[r]
cnt[r]++
```

##### 2. [Continuous Subarray Sum](https://leetcode.com/problems/continuous-subarray-sum/)

**Recognition:** Store earliest index for each remainder; enforce length
\>= 2.

``` text
first[0]=-1
pref=0
for i: pref+=a[i]
r=(k==0?pref:pref%k)
if seen r and i-first[r]>=2 return true
else store earliest
```

##### 3. [Make Sum Divisible by P](https://leetcode.com/problems/make-sum-divisible-by-p/)

**Recognition:** What remainder must the removed subarray have?.

``` text
need=sum(a)%p
last[0]=-1
pref=0
for i: pref=(pref+a[i])%p
want=(pref-need+p)%p
if want in last minimize i-last[want]
last[pref]=i
```

##### 4. [Subarray Divisibility](https://cses.fi/problemset/task/1662)

**Recognition:** Handle negative modulo carefully.

``` text
cnt[0]=1
pref=ans=0
for x: pref+=x
r=((pref%n)+n)%n
ans+=cnt[r]
cnt[r]++
```

#### How to practice this section

1.  Read only the statement and try to name the pattern in **60
    seconds**.
2.  Write what the prefix/difference state represents in one sentence.
3.  Write the algebra or state transition before code.
4.  Use the pseudocode only after you have your own approach.
5.  Re-solve failed problems after 2–3 days without opening this sheet.

------------------------------------------------------------------------

<a id="pattern-5"></a>

## Pattern 5 — Transform Values, Then Prefix

### Recognition signal — detailed

#### Real-world decoding drill

**Scenario:** Wins/losses: encode a win as +1 and a loss as -1. Equal
wins/losses becomes a zero-sum interval.

**Translate it during a contest:**

``` text
story objects/events
        ↓
array / prefix state / interval events
        ↓
what can be precomputed once?
        ↓
Encode each element so the awkward condition becomes a normal prefix equation.
```

**Reference pseudocode:**

``` text
for each x: v=transform(x); pref+=v; use repeated/required prefix state in map
```

**Statement clues**

``` text
- original condition mixes counts/types and looks awkward
- "equal number of X and Y"
- average / sum should equal length
- good/bad, odd/even, win/loss can be encoded as small contributions
```

The key move is:

``` text
statement property
      ↓ encode each item
numeric invariant
      ↓
normal prefix problem
```

**Mini dry run — equal 0s and 1s**

``` text
original:   0   1   0   0   1   1
transform: -1  +1  -1  -1  +1  +1

prefix:     0  -1   0  -1  -2  -1   0
            ^                         ^
same prefix => transformed sum 0
            => equal zeros and ones
```

ASCII mapping:

``` text
0 contributes -1
1 contributes +1

balanced subarray
= pushes left and right cancel
= transformed sum 0
```

**Real-world mapping**

Treat wins as `+1` and losses as `-1`. A period with equal wins and
losses has net score `0`, which is much easier to detect with prefixes
than counting two categories independently.

### 🧠 Retention diagram — **CHANGE THE LANGUAGE OF THE PROBLEM**

``` text
Story:
0  1  0  0  1  1

Translate:
0 -> -1
1 -> +1

-1 +1 -1 -1 +1 +1
 |_________________|
       sum = 0
       ↓
equal number of 0 and 1

STORY CONDITION
      ↓ encode
NUMERIC CONDITION
      ↓
PREFIX PATTERN
```

**Real-world trigger:** wins/losses, success/failure, odd/even, good/bad
days. Give opposite categories opposite numerical contributions.

**60-second question**

> Can I assign each element a contribution so the weird condition
> becomes `sum = 0`, `sum = K`, or an equal-prefix state?

**C++ template**

``` cpp
unordered_map<long long, int> first;
first[0] = 0;

long long pref = 0;
int best = 0;

for (int i = 1; i <= n; ++i) {
    long long v = transform(a[i]);   // e.g. 0 -> -1, 1 -> +1
    pref += v;

    if (first.count(pref)) {
        best = max(best, i - first[pref]);
    } else {
        first[pref] = i;
    }
}
```

Very common CF trick:

``` text
original condition looks difficult
        ↓
replace each A[i] by contribution/value
        ↓
condition becomes a normal prefix-sum condition
```

Examples:

``` text
0 -> -1
1 -> +1

equal zeros and ones
=> transformed subarray sum = 0
```

or

``` text
B[i] = A[i] - 1

sum(A[L..R]) = length
=> sum(B[L..R]) = 0
```

### Problems

### Optimized direct practice set

> These are intentionally filtered to problems that directly exercise
> this pattern. Warm-ups that mainly belong to another topic were
> removed from this section.
>
> **Reference pseudocode is problem-specific**: it shows how this exact
> problem uses the pattern, not merely the generic pattern template.

|  \# | Problem                                                                                             | Site | Why it maps to this pattern                               | Approach                                                                       |
|----:|-----------------------------------------------------------------------------------------------------|:----:|-----------------------------------------------------------|--------------------------------------------------------------------------------|
|   1 | [Contiguous Array](https://leetcode.com/problems/contiguous-array/)                                 |  LC  | Convert 0→-1. Find repeated prefix sum.                   | Encode each element so the awkward condition becomes a normal prefix equation. |
|   2 | [Count Number of Nice Subarrays](https://leetcode.com/problems/count-number-of-nice-subarrays/)     |  LC  | Odd/even becomes binary.                                  | Encode each element so the awkward condition becomes a normal prefix equation. |
|   3 | [Good Subarrays](https://codeforces.com/problemset/problem/1398/C)                                  |  CF  | Digit sum = length; subtract 1 from every digit.          | Encode each element so the awkward condition becomes a normal prefix equation. |
|   4 | [Longest Well-Performing Interval](https://leetcode.com/problems/longest-well-performing-interval/) |  LC  | \>8 hours → +1, otherwise -1. Need positive subarray sum. | Encode each element so the awkward condition becomes a normal prefix equation. |
|   5 | [Count Subarrays With Median K](https://leetcode.com/problems/count-subarrays-with-median-k/)       |  LC  | Relative to k: smaller=-1, larger=+1.                     | Encode each element so the awkward condition becomes a normal prefix equation. |

#### Problem-specific reference pseudocode

> Try the problem first. Use these blocks only to verify the solution
> flow. Each one is problem-specific and formatted vertically for
> GitHub.

##### 1. [Contiguous Array](https://leetcode.com/problems/contiguous-array/)

**Recognition:** Convert 0→-1. Find repeated prefix sum.

``` text
first[0]=-1
bal=0
for i: bal += (a[i]==1?1:-1)
if seen bal: best=max(best,i-first[bal])
else first[bal]=i
```

##### 2. [Count Number of Nice Subarrays](https://leetcode.com/problems/count-number-of-nice-subarrays/)

**Recognition:** Odd/even becomes binary.

``` text
freq[0]=1
odd=ans=0
for x: odd+=(x%2)
ans+=freq[odd-k]
freq[odd]++
```

##### 3. [Good Subarrays](https://codeforces.com/problemset/problem/1398/C)

**Recognition:** Digit sum = length; subtract 1 from every digit.

``` text
transform each digit d -> d-1
count zero-sum subarrays by repeated prefix frequencies
```

##### 4. [Longest Well-Performing Interval](https://leetcode.com/problems/longest-well-performing-interval/)

**Recognition:** \>8 hours → +1, otherwise -1. Need positive subarray
sum.

``` text
score += (hours[i]>8?1:-1)
if score>0 best=i+1
else if first has score-1 best=max(best,i-first[score-1])
store earliest score
```

##### 5. [Count Subarrays With Median K](https://leetcode.com/problems/count-subarrays-with-median-k/)

**Recognition:** Relative to k: smaller=-1, larger=+1.

``` text
map x<k -> -1, x==k -> 0, x>k -> +1
count left balances around position(k)
combine left/right balances so total is 0 or 1
```

#### How to practice this section

1.  Read only the statement and try to name the pattern in **60
    seconds**.
2.  Write what the prefix/difference state represents in one sentence.
3.  Write the algebra or state transition before code.
4.  Use the pseudocode only after you have your own approach.
5.  Re-solve failed problems after 2–3 days without opening this sheet.

------------------------------------------------------------------------

<a id="pattern-6"></a>

## Pattern 6 — Prefix XOR / Prefix State

### Recognition signal — detailed

#### Real-world decoding drill

**Scenario:** Light switches: XOR records toggle state. Applying the
same toggle twice cancels, so two prefix states isolate the interval
between them.

**Translate it during a contest:**

``` text
story objects/events
        ↓
array / prefix state / interval events
        ↓
what can be precomputed once?
        ↓
Use XOR/parity state because identical earlier state cancels the interval between.
```

**Reference pseudocode:**

``` text
px[0]=0; px[i]=px[i-1]^A[i]; xor(L,R)=px[R]^px[L-1]
```

**Statement clues**

``` text
- range XOR queries
- parity/even-odd state for several categories
- toggling a property matters more than its count
- operation is reversible/cancels with itself
```

For XOR:

``` text
px[i] = A[1] ^ ... ^ A[i]

xor(L..R) = px[R] ^ px[L-1]
```

because everything before `L` appears twice and cancels.

**Mini dry run**

``` text
A = [5, 2, 7, 2]

px:
px[0] = 0
px[1] = 5
px[2] = 5^2
px[3] = 5^2^7
px[4] = 5^2^7^2

query [2,4]:

px[4] ^ px[1]
= (5^2^7^2) ^ 5
= 2^7^2
```

Visualization:

``` text
prefix R:    [5][2][7][2]
prefix L-1:  [5]
XOR them:      [2][7][2]

5 ^ 5 = 0
```

For parity masks:

``` text
bit j = whether count of category j is odd so far
same mask twice => every category changed an even number of times
```

**Real-world mapping**

A light switch is XOR: press once = on, twice = back off. Prefix XOR
records the current collection of toggle states.

### 🧠 Retention diagram — **XOR IS A TOGGLE SWITCH**

``` text
Light:
OFF --press--> ON --press--> OFF

Same toggle twice cancels:
x ^ x = 0

prefix R:    [5][2][7][2]
prefix L-1:  [5]
              ^ cancels
result:         [2][7][2]

Range XOR = prefixR ^ prefixBeforeL
```

**Real-world trigger:** switches, parity states, "odd/even number of
occurrences", masks where only whether something was toggled matters.

**60-second question**

> Is the state based on toggles/parity, where applying the same thing
> twice cancels?

**C++ template**

``` cpp
vector<int> px(n + 1, 0);
for (int i = 1; i <= n; ++i) {
    px[i] = px[i - 1] ^ a[i];
}

auto rangeXor = [&](int L, int R) {
    return px[R] ^ px[L - 1];
};
```

Generalization:

``` text
sum: pref[R] - pref[L-1]
xor: px[R] ^ px[L-1]
```

because:

``` text
x ^ x = 0
```

### Problems

### Optimized direct practice set

> These are intentionally filtered to problems that directly exercise
> this pattern. Warm-ups that mainly belong to another topic were
> removed from this section.
>
> **Reference pseudocode is problem-specific**: it shows how this exact
> problem uses the pattern, not merely the generic pattern template.

|  \# | Problem                                                                                                                                                   | Site | Why it maps to this pattern                        | Approach                                                                           |
|----:|-----------------------------------------------------------------------------------------------------------------------------------------------------------|:----:|----------------------------------------------------|------------------------------------------------------------------------------------|
|   1 | [XOR Queries of a Subarray](https://leetcode.com/problems/xor-queries-of-a-subarray/)                                                                     |  LC  | Direct prefix XOR.                                 | Use XOR/parity state because identical earlier state cancels the interval between. |
|   2 | [Count Triplets That Can Form Two Arrays of Equal XOR](https://leetcode.com/problems/count-triplets-that-can-form-two-arrays-of-equal-xor/)               |  LC  | Equal prefix XOR means the middle split can vary.  | Use XOR/parity state because identical earlier state cancels the interval between. |
|   3 | [Find the Longest Substring Containing Vowels in Even Counts](https://leetcode.com/problems/find-the-longest-substring-containing-vowels-in-even-counts/) |  LC  | Five parity bits form the prefix state.            | Use XOR/parity state because identical earlier state cancels the interval between. |
|   4 | [Number of Wonderful Substrings](https://leetcode.com/problems/number-of-wonderful-substrings/)                                                           |  LC  | Equal mask or masks differing by one bit.          | Use XOR/parity state because identical earlier state cancels the interval between. |
|   5 | [Beautiful Subarrays](https://leetcode.com/problems/count-the-number-of-beautiful-subarrays/)                                                             |  LC  | Operation condition collapses to equal prefix XOR. | Use XOR/parity state because identical earlier state cancels the interval between. |

#### Problem-specific reference pseudocode

> Try the problem first. Use these blocks only to verify the solution
> flow. Each one is problem-specific and formatted vertically for
> GitHub.

##### 1. [XOR Queries of a Subarray](https://leetcode.com/problems/xor-queries-of-a-subarray/)

**Recognition:** Direct prefix XOR.

``` text
px[0]=0
px[i+1]=px[i]^a[i]
query(L,R)=px[R+1]^px[L]
```

##### 2. [Count Triplets That Can Form Two Arrays of Equal XOR](https://leetcode.com/problems/count-triplets-that-can-form-two-arrays-of-equal-xor/)

**Recognition:** Equal prefix XOR means the middle split can vary.

``` text
px[0]=0
build prefix XOR
for i<k with px[i]==px[k+1]: add k-i possible middle splits
```

##### 3. [Find the Longest Substring Containing Vowels in Even Counts](https://leetcode.com/problems/find-the-longest-substring-containing-vowels-in-even-counts/)

**Recognition:** Five parity bits form the prefix state.

``` text
first[0]=-1
mask=0
toggle vowel bit
if mask seen best=max(best,i-first[mask])
else first[mask]=i
```

##### 4. [Number of Wonderful Substrings](https://leetcode.com/problems/number-of-wonderful-substrings/)

**Recognition:** Equal mask or masks differing by one bit.

``` text
cnt[0]=1
mask=0
for c: mask^=1<<bit(c)
ans+=cnt[mask]+sum(cnt[mask^(1<<b)] for b=0..9)
cnt[mask]++
```

##### 5. [Beautiful Subarrays](https://leetcode.com/problems/count-the-number-of-beautiful-subarrays/)

**Recognition:** Operation condition collapses to equal prefix XOR.

``` text
cnt[0]=1
px=0
for x: px^=x
ans+=cnt[px]
cnt[px]++
```

#### How to practice this section

1.  Read only the statement and try to name the pattern in **60
    seconds**.
2.  Write what the prefix/difference state represents in one sentence.
3.  Write the algebra or state transition before code.
4.  Use the pseudocode only after you have your own approach.
5.  Re-solve failed problems after 2–3 days without opening this sheet.

------------------------------------------------------------------------

<a id="pattern-7"></a>

## Pattern 7 — Weighted Prefix Sum / Index \* A\[i\]

### Recognition signal — detailed

#### Real-world decoding drill

**Scenario:** Shipping/commission: an item's cost is value ×
position/frequency. Store both ordinary mass ΣA and weighted mass
Σ(i·A).

**Translate it during a contest:**

``` text
story objects/events
        ↓
array / prefix state / interval events
        ↓
what can be precomputed once?
        ↓
Expand the local weight algebraically into ordinary and index-weighted sums.
```

**Reference pseudocode:**

``` text
P0+=A[i]; P1+=i*A[i]; range weighted = ΔP1 - offset*ΔP0
```

**Statement clues**

``` text
- coefficient depends on position inside [L,R]
- 1*A[L] + 2*A[L+1] + ...
- contribution contains index * value
- after expanding, weight is linear in global index i
```

Local index is the clue:

``` text
local position = i-L+1
               = i-(L-1)
```

**Mini dry run**

``` text
A = [2, 4, 3, 6, 9]
query [2,4]

wanted:
1*4 + 2*3 + 3*6
= 4 + 6 + 18
= 28

global indices:
i:       2   3   4
A[i]:    4   3   6
local:   1   2   3

local = i-(L-1) = i-1
```

Therefore:

``` text
Σ A[i]*(i-(L-1))
= Σ i*A[i] - (L-1)*Σ A[i]
```

Visualization:

``` text
Need two cumulative "lenses":

P0 = Σ A[i]        -> ordinary mass
P1 = Σ i*A[i]      -> index-weighted mass
```

**Real-world mapping**

Shipping cost might be `itemWeight × shelfPosition`. If every query
re-labels the first shelf as position 1, algebra converts local shelf
numbers into global indices.

### 🧠 Retention diagram — **VALUE × HOW MUCH IT PARTICIPATES**

``` text
Query [L..R]
local weights:     1     2     3
values:            4     3     6
                   |     |     |
contribution:     1*4   2*3   3*6

local weight = i - (L-1)

Σ A[i]*(i-(L-1))
        ↓ expand
Σ i*A[i] - (L-1)ΣA[i]

Need TWO lenses:
P0 = Σ A[i]
P1 = Σ i*A[i]
```

**Real-world trigger:** shipping cost by shelf position, commission by
rank, or any situation where each value is multiplied by its
position/frequency/number of uses.

**60-second question**

> Can the coefficient be written as `c*i + d` after converting local
> position to global index?

**C++ template**

``` cpp
vector<long long> P0(n + 1), P1(n + 1);
for (int i = 1; i <= n; ++i) {
    P0[i] = P0[i - 1] + a[i];
    P1[i] = P1[i - 1] + 1LL * i * a[i];
}

auto weighted = [&](int L, int R) -> long long {
    long long s0 = P0[R] - P0[L - 1];
    long long s1 = P1[R] - P1[L - 1];
    return s1 - 1LL * (L - 1) * s0;
};
```

**Quick recap:**

``` text
A[L] + 2*A[L+1] + ... + len*A[R]
index-dependent coefficient
weighted range queries
```

Derive:

``` text
weight(i) = i-L+1 = i-(L-1)

answer
= Σ A[i]*(i-(L-1))
= Σ i*A[i] - (L-1)*Σ A[i]
```

Precompute:

``` text
P0[i] = Σ A[j]
P1[i] = Σ j*A[j]
```

Then:

``` text
S0 = P0[R]-P0[L-1]
S1 = P1[R]-P1[L-1]

answer = S1 - (L-1)*S0
```

### Problems

### Optimized direct practice set

> These are intentionally filtered to problems that directly exercise
> this pattern. Warm-ups that mainly belong to another topic were
> removed from this section.
>
> **Reference pseudocode is problem-specific**: it shows how this exact
> problem uses the pattern, not merely the generic pattern template.

|  \# | Problem                                                                                                                                   |  Site   | Why it maps to this pattern                                           | Approach                                                                     |
|----:|-------------------------------------------------------------------------------------------------------------------------------------------|:-------:|-----------------------------------------------------------------------|------------------------------------------------------------------------------|
|   1 | [Sum of Absolute Differences in a Sorted Array](https://leetcode.com/problems/sum-of-absolute-differences-in-a-sorted-array/)             |   LC    | Algebra creates `i*A[i] - prefix`.                                    | Expand the local weight algebraically into ordinary and index-weighted sums. |
|   2 | [Minimum Operations to Make All Array Elements Equal](https://leetcode.com/problems/minimum-operations-to-make-all-array-elements-equal/) |   LC    | Sort + prefix; split at lower_bound(query).                           | Expand the local weight algebraically into ordinary and index-weighted sums. |
|   3 | [Little Girl and Maximum Sum](https://codeforces.com/problemset/problem/276/C)                                                            |   CF    | Frequency is effectively a weight per position.                       | Expand the local weight algebraically into ordinary and index-weighted sums. |
|   4 | [Maximum Sum Obtained of Any Permutation](https://leetcode.com/problems/maximum-sum-obtained-of-any-permutation/)                         |   LC    | Difference array computes position weights.                           | Expand the local weight algebraically into ordinary and index-weighted sums. |
|   5 | [Sum of Total Strength of Wizards](https://leetcode.com/problems/sum-of-total-strength-of-wizards/)                                       | LC Hard | Prefix-of-prefix sums are needed for weighted interval contributions. | Expand the local weight algebraically into ordinary and index-weighted sums. |

#### Problem-specific reference pseudocode

> Try the problem first. Use these blocks only to verify the solution
> flow. Each one is problem-specific and formatted vertically for
> GitHub.

##### 1. [Sum of Absolute Differences in a Sorted Array](https://leetcode.com/problems/sum-of-absolute-differences-in-a-sorted-array/)

**Recognition:** Algebra creates `i*A[i] - prefix`.

``` text
pref=prefix(nums)
ans[i]=nums[i]*i-pref[i] + (pref[n]-pref[i+1])-nums[i]*(n-i-1)
```

##### 2. [Minimum Operations to Make All Array Elements Equal](https://leetcode.com/problems/minimum-operations-to-make-all-array-elements-equal/)

**Recognition:** Sort + prefix; split at lower_bound(query).

``` text
sort a
pref=prefix(a)
for q: p=lower_bound(a,q)
cost=q*p-pref[p] + (pref[n]-pref[p])-q*(n-p)
```

##### 3. [Little Girl and Maximum Sum](https://codeforces.com/problemset/problem/276/C)

**Recognition:** Frequency is effectively a weight per position.

``` text
for query(L,R): diff[L]++,diff[R+1]--
prefix diff -> freq
sort(freq),sort(a)
ans=sum(freq[i]*a[i])
```

##### 4. [Maximum Sum Obtained of Any Permutation](https://leetcode.com/problems/maximum-sum-obtained-of-any-permutation/)

**Recognition:** Difference array computes position weights.

``` text
range requests -> diff frequencies
prefix freq
sort nums and freq
ans=sum(nums[i]*freq[i]) mod M
```

##### 5. [Sum of Total Strength of Wizards](https://leetcode.com/problems/sum-of-total-strength-of-wizards/)

**Recognition:** Prefix-of-prefix sums are needed for weighted interval
contributions.

``` text
monotonic stack -> L,R where a[i] is minimum
build prefix and prefix-of-prefix
use double-prefix formula for sum of subarray sums containing i
add a[i]*contribution
```

#### How to practice this section

1.  Read only the statement and try to name the pattern in **60
    seconds**.
2.  Write what the prefix/difference state represents in one sentence.
3.  Write the algebra or state transition before code.
4.  Use the pseudocode only after you have your own approach.
5.  Re-solve failed problems after 2–3 days without opening this sheet.

------------------------------------------------------------------------

<a id="pattern-8"></a>

## Pattern 8 — Prefix of Prefix / Double Prefix

### Recognition signal — detailed

#### Real-world decoding drill

**Scenario:** Running business totals: A is daily sales, P is
sales-to-date, and PP is the cumulative sum of those running totals.

**Translate it during a contest:**

``` text
story objects/events
        ↓
array / prefix state / interval events
        ↓
what can be precomputed once?
        ↓
If one prefix still leaves a range sum over prefixes, prefix that prefix.
```

**Reference pseudocode:**

``` text
P[i]=P[i-1]+A[i]; PP[i]=PP[i-1]+P[i]; sumP(L,R)=PP[R]-PP[L-1]
```

**Statement clues**

``` text
- need sum of many prefix sums
- each answer itself contains range sums repeatedly
- contribution formula contains Σ pref[i]
- one prefix layer still leaves an O(N) summation
```

Think one level higher:

``` text
A  --prefix-->  P  --prefix again-->  PP
```

**Mini dry run**

``` text
A  = [2, 1, 3, 4]
P  = [2, 3, 6, 10]
PP = [2, 5, 11, 21]

Need P[2] + P[3] + P[4]
= 3 + 6 + 10
= 19

Using PP:
PP[4] - PP[1]
= 21 - 2
= 19
```

Visualization:

``` text
A:      2   1   3   4
        \___ cumulative ___/
P:      2   3   6  10
        \___ cumulative ___/
PP:     2   5  11  21
```

**Real-world mapping**

`A` could be daily sales, `P` total sales-to-date, and `PP` the sum of
all historical running totals. If queries ask about accumulated
cumulative totals, prefix the prefix.

### 🧠 Retention diagram — **PREFIX THE PREFIX**

``` text
Daily sales A
      ↓ cumulative
running sales P
      ↓ cumulative again
sum of running sales PP

A :   2   1   3   4
      ↓   ↓   ↓   ↓
P :   2   3   6  10
      ↓   ↓   ↓   ↓
PP:   2   5  11  21

If the formula still says:
P[L] + P[L+1] + ... + P[R]

then P itself needs a prefix.
```

**Real-world trigger:** daily sales → sales-to-date → total of
historical running totals; repeated accumulation is the clue.

**60-second question**

> After building a normal prefix, do I still need to sum a contiguous
> range of those prefix values?

**C++ template**

``` cpp
vector<long long> pref(n + 1), pref2(n + 1);
for (int i = 1; i <= n; ++i) {
    pref[i]  = pref[i - 1] + a[i];
    pref2[i] = pref2[i - 1] + pref[i];
}

auto sumOfPrefixes = [&](int L, int R) {
    return pref2[R] - pref2[L - 1];
};
```

Think:

``` text
A
 ↓ prefix
P
 ↓ prefix again
PP
```

Useful when the formula asks for:

``` text
P[L] + P[L+1] + ... + P[R]
```

or when contributions themselves contain range sums.

### Problems

### Optimized direct practice set

> These are intentionally filtered to problems that directly exercise
> this pattern. Warm-ups that mainly belong to another topic were
> removed from this section.
>
> **Reference pseudocode is problem-specific**: it shows how this exact
> problem uses the pattern, not merely the generic pattern template.

|  \# | Problem                                                                                             |  Site   | Why it maps to this pattern                                           | Approach                                                                  |
|----:|-----------------------------------------------------------------------------------------------------|:-------:|-----------------------------------------------------------------------|---------------------------------------------------------------------------|
|   1 | [Sum of Total Strength of Wizards](https://leetcode.com/problems/sum-of-total-strength-of-wizards/) | LC Hard | Need sums of prefix sums on left/right of each minimum.               | If one prefix still leaves a range sum over prefixes, prefix that prefix. |
|   2 | [Greg and Array](https://codeforces.com/problemset/problem/295/A)                                   |   CF    | Difference/prefix once for operation counts, again for array changes. | If one prefix still leaves a range sum over prefixes, prefix that prefix. |
|   3 | [Karen and Coffee](https://codeforces.com/problemset/problem/816/B)                                 |   CF    | Diff → prefix temperatures → prefix “good” indicator.                 | If one prefix still leaves a range sum over prefixes, prefix that prefix. |
|   4 | [Little Girl and Maximum Sum](https://codeforces.com/problemset/problem/276/C)                      |   CF    | Diff → prefix usage counts → weighted sum.                            | If one prefix still leaves a range sum over prefixes, prefix that prefix. |

#### Problem-specific reference pseudocode

> Try the problem first. Use these blocks only to verify the solution
> flow. Each one is problem-specific and formatted vertically for
> GitHub.

##### 1. [Sum of Total Strength of Wizards](https://leetcode.com/problems/sum-of-total-strength-of-wizards/)

**Recognition:** Need sums of prefix sums on left/right of each minimum.

``` text
P=prefix(a)
PP=prefix(P)
stack gives L,R
contribution uses ranges of PP on both sides of i
```

##### 2. [Greg and Array](https://codeforces.com/problemset/problem/295/A)

**Recognition:** Difference/prefix once for operation counts, again for
array changes.

``` text
opCntDiff[x]++,opCntDiff[y+1]--
prefix -> times[j]
for op j: arrDiff[l]+=d*times[j], arrDiff[r+1]-=d*times[j]
prefix arrDiff
```

##### 3. [Karen and Coffee](https://codeforces.com/problemset/problem/816/B)

**Recognition:** Diff → prefix temperatures → prefix “good” indicator.

``` text
tempDiff[l]++,tempDiff[r+1]--
prefix -> cover
good[i]=(cover[i]>=k)
prefGood=prefix(good)
query=prefGood[b]-prefGood[a-1]
```

##### 4. [Little Girl and Maximum Sum](https://codeforces.com/problemset/problem/276/C)

**Recognition:** Diff → prefix usage counts → weighted sum.

``` text
query diff -> prefix usageCount
sort usageCount and a
sum a[i]*usageCount[i]
```

#### How to practice this section

1.  Read only the statement and try to name the pattern in **60
    seconds**.
2.  Write what the prefix/difference state represents in one sentence.
3.  Write the algebra or state transition before code.
4.  Use the pseudocode only after you have your own approach.
5.  Re-solve failed problems after 2–3 days without opening this sheet.

------------------------------------------------------------------------

<a id="pattern-9"></a>

## Pattern 9 — 2D Prefix Sum

### Recognition signal — detailed

#### Real-world decoding drill

**Scenario:** Population map: every grid cell stores people. Precompute
population from (1,1) to every corner so any rectangular district is
answered by inclusion-exclusion.

**Translate it during a contest:**

``` text
story objects/events
        ↓
array / prefix state / interval events
        ↓
what can be precomputed once?
        ↓
Use 2D inclusion-exclusion: big rectangle minus two strips plus the double-removed corner.
```

**Reference pseudocode:**

``` text
build P[r][c]; rect = P[r2][c2]-P[r1-1][c2]-P[r2][c1-1]+P[r1-1][c1-1]
```

**Statement clues**

``` text
- grid / matrix / board
- many rectangle queries
- each query gives top-left and bottom-right corners
- ask sum/count inside a rectangle
- grid is static
```

**Mini dry run**

``` text
Grid:
1 0 2 1
3 1 0 2
0 2 1 1
4 0 1 0

Query rows 2..3, cols 2..4

selected:
      c2 c3 c4
r2 ->  1  0  2
r3 ->  2  1  1

sum = 7
```

Rectangle visualization:

``` text
P[r2][c2]
= big rectangle from (1,1)
- strip above target
- strip left of target
+ top-left overlap removed twice

+----------------------+
| overlap |   top      |
|---------+------------|
| left    |  TARGET    |
+----------------------+
```

Formula:

``` text
ans = P[r2][c2]
    - P[r1-1][c2]
    - P[r2][c1-1]
    + P[r1-1][c1-1]
```

**Real-world mapping**

A map divided into cells: each cell stores population. A rectangle query
asks population inside a district box. 2D prefix gives each district
total in O(1).

### 🧠 Retention diagram — **BIG RECTANGLE − TWO STRIPS + CORNER**

``` text
+-----------------------+
| CORNER |     TOP      |
|--------+--------------|
| LEFT   |    TARGET    |
|        |              |
+-----------------------+

Start with BIG rectangle.
Subtract TOP.
Subtract LEFT.
CORNER was removed twice → add it back.

TARGET = BIG - TOP - LEFT + CORNER
```

**Real-world trigger:** population maps, heat maps, pixels, seats, trees
on a grid — many static rectangle queries.

**60-second question**

> Is this a static grid with many axis-aligned rectangle sum/count
> queries?

**C++ template**

``` cpp
vector<vector<long long>> p(n + 1, vector<long long>(m + 1));

for (int r = 1; r <= n; ++r) {
    for (int c = 1; c <= m; ++c) {
        p[r][c] = a[r][c]
                + p[r - 1][c]
                + p[r][c - 1]
                - p[r - 1][c - 1];
    }
}

auto rectSum = [&](int r1, int c1, int r2, int c2) {
    return p[r2][c2]
         - p[r1 - 1][c2]
         - p[r2][c1 - 1]
         + p[r1 - 1][c1 - 1];
};
```

Definition:

``` text
P[r][c] = sum of rectangle (1,1) -> (r,c)
```

Query:

``` text
             c1        c2
          +-----------+
      r1  |  TARGET   |
          |           |
      r2  +-----------+

ans =
P[r2][c2]
- P[r1-1][c2]
- P[r2][c1-1]
+ P[r1-1][c1-1]
```

Last term restores the area subtracted twice.

### Problems

### Optimized direct practice set

> These are intentionally filtered to problems that directly exercise
> this pattern. Warm-ups that mainly belong to another topic were
> removed from this section.
>
> **Reference pseudocode is problem-specific**: it shows how this exact
> problem uses the pattern, not merely the generic pattern template.

|  \# | Problem                                                                                                             |  Site   | Why it maps to this pattern                                | Approach                                                                                   |
|----:|---------------------------------------------------------------------------------------------------------------------|:-------:|------------------------------------------------------------|--------------------------------------------------------------------------------------------|
|   1 | [Range Sum Query 2D - Immutable](https://leetcode.com/problems/range-sum-query-2d-immutable/)                       |   LC    | Canonical 2D prefix.                                       | Use 2D inclusion-exclusion: big rectangle minus two strips plus the double-removed corner. |
|   2 | [Matrix Block Sum](https://leetcode.com/problems/matrix-block-sum/)                                                 |   LC    | Every cell asks one clipped rectangle query.               | Use 2D inclusion-exclusion: big rectangle minus two strips plus the double-removed corner. |
|   3 | [Number of Submatrices That Sum to Target](https://leetcode.com/problems/number-of-submatrices-that-sum-to-target/) | LC Hard | Fix two row boundaries; collapse columns into 1D.          | Use 2D inclusion-exclusion: big rectangle minus two strips plus the double-removed corner. |
|   4 | [Forest Queries](https://cses.fi/problemset/task/1652)                                                              |  Bonus  | Pure 2D prefix drill.                                      | Use 2D inclusion-exclusion: big rectangle minus two strips plus the double-removed corner. |
|   5 | [Counting Rectangles](https://codeforces.com/problemset/problem/1722/E)                                             |   CF    | Build a 2D table by height/width, then rectangle-query it. | Use 2D inclusion-exclusion: big rectangle minus two strips plus the double-removed corner. |

#### Problem-specific reference pseudocode

> Try the problem first. Use these blocks only to verify the solution
> flow. Each one is problem-specific and formatted vertically for
> GitHub.

##### 1. [Range Sum Query 2D - Immutable](https://leetcode.com/problems/range-sum-query-2d-immutable/)

**Recognition:** Canonical 2D prefix.

``` text
build P[r+1][c+1]
rect(r1,c1,r2,c2)=P[r2+1][c2+1]-P[r1][c2+1]-P[r2+1][c1]+P[r1][c1]
```

##### 2. [Matrix Block Sum](https://leetcode.com/problems/matrix-block-sum/)

**Recognition:** Every cell asks one clipped rectangle query.

``` text
build 2D prefix
for each cell (i,j): clip r1=i-k,c1=j-k,r2=i+k,c2=j+k
answer with rectangle formula
```

##### 3. [Number of Submatrices That Sum to Target](https://leetcode.com/problems/number-of-submatrices-that-sum-to-target/)

**Recognition:** Fix two row boundaries; collapse columns into 1D.

``` text
for top=0..R-1: colSum=0
for bottom=top..R-1: add row[bottom] into colSum
count 1D subarrays of colSum with sum=target via prefix map
```

##### 4. [Forest Queries](https://cses.fi/problemset/task/1652)

**Recognition:** Pure 2D prefix drill.

``` text
build 2D prefix of tree indicator
each rectangle query uses inclusion-exclusion
```

##### 5. [Counting Rectangles](https://codeforces.com/problemset/problem/1722/E)

**Recognition:** Build a 2D table by height/width, then rectangle-query
it.

``` text
grid[h][w]+=h*w for each rectangle
build 2D prefix
query strict bounds using P[h2-1][w2-1]-P[h1][...]-...
```

#### How to practice this section

1.  Read only the statement and try to name the pattern in **60
    seconds**.
2.  Write what the prefix/difference state represents in one sentence.
3.  Write the algebra or state transition before code.
4.  Use the pseudocode only after you have your own approach.
5.  Re-solve failed problems after 2–3 days without opening this sheet.

------------------------------------------------------------------------

<a id="difference-array-patterns"></a>

# DIFFERENCE ARRAY PATTERNS

<a id="pattern-10"></a>

## Pattern 10 — Basic Range Addition

### Recognition signal — detailed

#### Real-world decoding drill

**Scenario:** Roadworks: instead of repainting every meter in \[L,R\],
record 'start +X' at L and 'stop -X' after R, then sweep once.

**Translate it during a contest:**

``` text
story objects/events
        ↓
array / prefix state / interval events
        ↓
what can be precomputed once?
        ↓
Store only where a constant range effect starts and stops; reconstruct at the end.
```

**Reference pseudocode:**

``` text
diff[L]+=x; diff[R+1]-=x; after all updates prefix diff once
```

**Statement clues**

``` text
- many offline updates
- each update adds the same X to every position in [L,R]
- only final array / final values are needed
- no need to answer arbitrary queries between updates
```

Instead of touching every cell, mark only where the effect changes.

**Mini dry run**

``` text
n = 6
update: add 5 to [2,4]

wanted:
A:   0   5   5   5   0   0

diff events:
     0  +5   0   0  -5   0
         ^           ^
         L          R+1

prefix diff:
     0   5   5   5   0   0
```

Visualization:

``` text
effect is ON:
        L==============R
        +5             |
                       stop at R+1

Store transitions, not every affected point.
```

**Real-world mapping**

Turning a water pipe on at position `L` and off after `R`: you record
the valve changes, then sweep to know the current flow at every
position.

### 🧠 Retention diagram — **TURN THE EFFECT ON, THEN OFF**

``` text
add +5 to [2..4]

position: 1   2   3   4   5   6
effect:       +5==========+5
              ON           OFF

diff:      0  +5   0   0  -5   0
               ^           ^
               L          R+1

prefix(diff):
           0   5   5   5   0   0
```

**Real-world trigger:** roadworks, discounts, temperature changes, buffs
— many offline constant updates over intervals.

**60-second question**

> Are there many constant range additions and can I postpone
> reconstruction until the end?

**C++ template**

``` cpp
vector<long long> diff(n + 2, 0);

auto addRange = [&](int L, int R, long long x) {
    diff[L] += x;
    diff[R + 1] -= x;
};

for (int i = 1; i <= n; ++i) {
    diff[i] += diff[i - 1];
    a[i] += diff[i];
}
```

**Quick recap:**

``` text
many operations:
add X to every A[L..R]

only final array is needed
```

One operation:

``` text
L                 R
|-----------------|
       +X

diff:
       +X          -X
        |           |
        L          R+1
```

Prefixing `diff` spreads X through the range.

### Problems

### Optimized direct practice set

> These are intentionally filtered to problems that directly exercise
> this pattern. Warm-ups that mainly belong to another topic were
> removed from this section.
>
> **Reference pseudocode is problem-specific**: it shows how this exact
> problem uses the pattern, not merely the generic pattern template.

|  \# | Problem                                                                               | Site | Why it maps to this pattern                  | Approach                                                                           |
|----:|---------------------------------------------------------------------------------------|:----:|----------------------------------------------|------------------------------------------------------------------------------------|
|   1 | [Range Addition](https://leetcode.com/problems/range-addition/)                       |  LC  | Canonical diff array.                        | Store only where a constant range effect starts and stops; reconstruct at the end. |
|   2 | [Corporate Flight Bookings](https://leetcode.com/problems/corporate-flight-bookings/) |  LC  | Each booking is one range addition.          | Store only where a constant range effect starts and stops; reconstruct at the end. |
|   3 | [Shifting Letters II](https://leetcode.com/problems/shifting-letters-ii/)             |  LC  | Convert each shift to +1/-1 range update.    | Store only where a constant range effect starts and stops; reconstruct at the end. |
|   4 | [Greg and Array](https://codeforces.com/problemset/problem/295/A)                     |  CF  | Two layers of difference arrays.             | Store only where a constant range effect starts and stops; reconstruct at the end. |
|   5 | [Little Girl and Maximum Sum](https://codeforces.com/problemset/problem/276/C)        |  CF  | Difference counts query frequency per index. | Store only where a constant range effect starts and stops; reconstruct at the end. |

#### Problem-specific reference pseudocode

> Try the problem first. Use these blocks only to verify the solution
> flow. Each one is problem-specific and formatted vertically for
> GitHub.

##### 1. [Range Addition](https://leetcode.com/problems/range-addition/)

**Recognition:** Canonical diff array.

``` text
for [L,R,val]: diff[L]+=val
diff[R+1]-=val
prefix diff
a[i]+=diff[i]
```

##### 2. [Corporate Flight Bookings](https://leetcode.com/problems/corporate-flight-bookings/)

**Recognition:** Each booking is one range addition.

``` text
for [first,last,seats]: diff[first]+=seats
diff[last+1]-=seats
prefix -> seats per flight
```

##### 3. [Shifting Letters II](https://leetcode.com/problems/shifting-letters-ii/)

**Recognition:** Convert each shift to +1/-1 range update.

``` text
for [L,R,dir]: d=(dir?1:-1)
diff[L]+=d
diff[R+1]-=d
prefix shift
s[i]=(s[i]+shift mod 26)
```

##### 4. [Greg and Array](https://codeforces.com/problemset/problem/295/A)

**Recognition:** Two layers of difference arrays.

``` text
first diff counts how many times each operation runs
second diff applies d*count over each operation's [l,r]
prefix to final array
```

##### 5. [Little Girl and Maximum Sum](https://codeforces.com/problemset/problem/276/C)

**Recognition:** Difference counts query frequency per index.

``` text
for each query: diff[L]++,diff[R+1]--
prefix -> use count per index
sort counts and values
dot product
```

#### How to practice this section

1.  Read only the statement and try to name the pattern in **60
    seconds**.
2.  Write what the prefix/difference state represents in one sentence.
3.  Write the algebra or state transition before code.
4.  Use the pseudocode only after you have your own approach.
5.  Re-solve failed problems after 2–3 days without opening this sheet.

------------------------------------------------------------------------

<a id="pattern-11"></a>

## Pattern 11 — Difference Array as Event / Sweep Line

### Recognition signal — detailed

#### Real-world decoding drill

**Scenario:** People entering/leaving a room: entry is +1, exit is -1.
Prefixing events gives the number currently inside.

**Translate it during a contest:**

``` text
story objects/events
        ↓
array / prefix state / interval events
        ↓
what can be precomputed once?
        ↓
Convert intervals to endpoint events and sweep cumulative active load.
```

**Reference pseudocode:**

``` text
event[start]+=x; event[end]-=x; sort/sweep; cur+=event[pos]
```

**Statement clues**

``` text
- intervals on time/position/year/temperature
- need active count/load at each point
- maximum overlap / capacity / population
- value changes only at starts and ends
```

Difference arrays and sweep lines are the same idea:

``` text
start event: +X
end event:   -X
scan in sorted coordinate order
```

**Mini dry run**

``` text
meetings:
[1,4)  +1
[2,5)  +1
[4,6)  +1

events:
1:+1
2:+1
4:-1 and +1
5:-1
6:-1

sweep active:
time 1 -> 1
time 2 -> 2
time 4 -> 2
time 5 -> 1
time 6 -> 0
```

Timeline:

``` text
1----4
  2------5
       4----6

active count is just the prefix sum of endpoint events.
```

**Real-world mapping**

People entering/leaving a room: entry is `+1`, exit is `-1`; cumulative
events give the number currently inside.

### 🧠 Retention diagram — **EVENTS CHANGE THE ACTIVE COUNT**

``` text
Customer A:   [----------)
Customer B:       [-------------)
Customer C:            [-----)

timeline:
A start  +1
B start  +1
C start  +1
A end    -1
C end    -1
B end    -1

Sweep left → right:
active += event[x]
answer = max(active)
```

**Real-world trigger:** people entering/leaving, meetings
starting/ending, cars picking up/dropping passengers, overlapping
bookings.

**60-second question**

> Does the answer change only when an interval starts or ends?

**C++ template — ordered events**

``` cpp
map<long long, long long> event;

for (auto [L, R, x] : updates) {
    event[L] += x;
    event[R] -= x; // for half-open [L,R)
}

long long cur = 0;
for (auto [pos, delta] : event) {
    cur += delta;
    // use cur on the interval starting at pos
}
```

Intervals become events:

``` text
[start] += X
[end+1] -= X
```

Then sweep from left to right.

This is conceptually the same as a difference array, even when
coordinates represent **time**, **position**, **year**, or
**temperature**.

### Problems

### Optimized direct practice set

> These are intentionally filtered to problems that directly exercise
> this pattern. Warm-ups that mainly belong to another topic were
> removed from this section.
>
> **Reference pseudocode is problem-specific**: it shows how this exact
> problem uses the pattern, not merely the generic pattern template.

|  \# | Problem                                                                           | Site  | Why it maps to this pattern                       | Approach                                                               |
|----:|-----------------------------------------------------------------------------------|:-----:|---------------------------------------------------|------------------------------------------------------------------------|
|   1 | [Car Pooling](https://leetcode.com/problems/car-pooling/)                         |  LC   | Capacity is maximum active load during the sweep. | Convert intervals to endpoint events and sweep cumulative active load. |
|   2 | [Maximum Population Year](https://leetcode.com/problems/maximum-population-year/) |  LC   | Treat lifespan endpoints as events.               | Convert intervals to endpoint events and sweep cumulative active load. |
|   3 | [Describe the Painting](https://leetcode.com/problems/describe-the-painting/)     |  LC   | Coordinate events; maintain active color sum.     | Convert intervals to endpoint events and sweep cumulative active load. |
|   4 | [My Calendar III](https://leetcode.com/problems/my-calendar-iii/)                 |  LC   | Start +1, end -1; maximum prefix is max overlap.  | Convert intervals to endpoint events and sweep cumulative active load. |
|   5 | [Restaurant Customers](https://cses.fi/problemset/task/1619)                      | Bonus | Classic +1 arrival / -1 departure sweep.          | Convert intervals to endpoint events and sweep cumulative active load. |

#### Problem-specific reference pseudocode

> Try the problem first. Use these blocks only to verify the solution
> flow. Each one is problem-specific and formatted vertically for
> GitHub.

##### 1. [Car Pooling](https://leetcode.com/problems/car-pooling/)

**Recognition:** Capacity is maximum active load during the sweep.

``` text
event[start]+=passengers
event[end]-=passengers
sweep positions
if active>capacity return false
```

##### 2. [Maximum Population Year](https://leetcode.com/problems/maximum-population-year/)

**Recognition:** Treat lifespan endpoints as events.

``` text
diff[birth]++
diff[death]--
sweep years
track earliest year with maximum population
```

##### 3. [Describe the Painting](https://leetcode.com/problems/describe-the-painting/)

**Recognition:** Coordinate events; maintain active color sum.

``` text
event[start]+=color
event[end]-=color
sweep sorted coordinates
if activeSum>0 output [prev,x,activeSum]
activeSum+=event[x]
```

##### 4. [My Calendar III](https://leetcode.com/problems/my-calendar-iii/)

**Recognition:** Start +1, end -1; maximum prefix is max overlap.

``` text
delta[start]++
delta[end]--
sweep ordered deltas after each booking
answer=max prefix overlap
```

##### 5. [Restaurant Customers](https://cses.fi/problemset/task/1619)

**Recognition:** Classic +1 arrival / -1 departure sweep.

``` text
events.push(arrival,+1), (departure,-1)
sort with departure before arrival at same time
sweep and maximize active
```

#### How to practice this section

1.  Read only the statement and try to name the pattern in **60
    seconds**.
2.  Write what the prefix/difference state represents in one sentence.
3.  Write the algebra or state transition before code.
4.  Use the pseudocode only after you have your own approach.
5.  Re-solve failed problems after 2–3 days without opening this sheet.

------------------------------------------------------------------------

<a id="pattern-12"></a>

## Pattern 12 — Difference + Prefix + Another Prefix

### Recognition signal — detailed

#### Real-world decoding drill

**Scenario:** Bus service: first compute buses covering each stop, mark
stops meeting a threshold, then prefix those good stops for fast
interval queries.

**Translate it during a contest:**

``` text
story objects/events
        ↓
array / prefix state / interval events
        ↓
what can be precomputed once?
        ↓
Treat the solution as a pipeline: materialize coverage/state, transform it, then prefix for final queries.
```

**Reference pseudocode:**

``` text
updates -> diff; prefix -> actual; transform actual -> good/value; prefix again -> queries
```

**Statement clues**

``` text
- one set of range updates creates a frequency/value array
- then queries ask over a transformed version of that result
- "how many positions are covered at least K times?"
- operations themselves are selected by ranges (Greg and Array style)
```

Look for a pipeline rather than one data structure:

``` text
range updates
   ↓ diff
frequency/value per position
   ↓ transform
0/1, cost, weight, good/bad
   ↓ prefix
fast range queries
```

**Mini dry run — coverage \>= 2**

``` text
intervals:
[1,3]
[2,4]
[2,5]

coverage after diff+prefix:
index:     1 2 3 4 5
coverage:  1 3 3 2 1

mark "good" if coverage >= 2:
good:      0 1 1 1 0

prefix good:
            0 1 2 3 3

query [2,5] -> 3 good positions
```

Visualization:

``` text
RAW UPDATES -> DIFF -> ACTUAL COUNTS -> TRANSFORM -> PREFIX -> QUERIES
```

**Real-world mapping**

First compute how many buses pass each stop. Then mark stops with at
least 3 buses. Finally answer “how many well-served stops between L and
R?”

### 🧠 Retention diagram — **UPDATE → RECONSTRUCT → QUERY**

``` text
interval updates
      ↓ difference
change markers
      ↓ prefix #1
actual coverage/value
      ↓ transform if needed
good / bad / count
      ↓ prefix #2
fast range answers

Example:
coffee temperature intervals
→ coverage count
→ is coverage >= K?
→ prefix of GOOD temperatures
→ answer [A,B]
```

**Real-world trigger:** first accumulate how many effects hit each
position, then answer many queries about the reconstructed result.

**60-second question**

> Do I need one cumulative pass to materialize an intermediate array and
> another cumulative pass to answer final queries?

**C++ template**

``` cpp
vector<long long> diff(n + 2), prefGood(n + 1);

for (auto [L, R] : ranges) {
    ++diff[L];
    --diff[R + 1];
}

long long cur = 0;
for (int i = 1; i <= n; ++i) {
    cur += diff[i];
    int good = (cur >= K);
    prefGood[i] = prefGood[i - 1] + good;
}

auto query = [&](int L, int R) {
    return prefGood[R] - prefGood[L - 1];
};
```

Pipeline:

``` text
range updates
     ↓
difference
     ↓ prefix
actual frequency/value
     ↓ transform
0/1 or weighted value
     ↓ prefix
range queries
```

This is a major CF pattern.

### Problems

### Optimized direct practice set

> These are intentionally filtered to problems that directly exercise
> this pattern. Warm-ups that mainly belong to another topic were
> removed from this section.
>
> **Reference pseudocode is problem-specific**: it shows how this exact
> problem uses the pattern, not merely the generic pattern template.

|  \# | Problem                                                                                                           | Site | Why it maps to this pattern                                                        | Approach                                                                                                   |
|----:|-------------------------------------------------------------------------------------------------------------------|:----:|------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------|
|   1 | [Karen and Coffee](https://codeforces.com/problemset/problem/816/B)                                               |  CF  | Coverage count → mark coverage\>=k → prefix again.                                 | Treat the solution as a pipeline: materialize coverage/state, transform it, then prefix for final queries. |
|   2 | [Greg and Array](https://codeforces.com/problemset/problem/295/A)                                                 |  CF  | First diff counts operation applications; second diff applies weighted operations. | Treat the solution as a pipeline: materialize coverage/state, transform it, then prefix for final queries. |
|   3 | [Little Girl and Maximum Sum](https://codeforces.com/problemset/problem/276/C)                                    |  CF  | Query diff → usage counts → sort with A.                                           | Treat the solution as a pipeline: materialize coverage/state, transform it, then prefix for final queries. |
|   4 | [Maximum Sum Obtained of Any Permutation](https://leetcode.com/problems/maximum-sum-obtained-of-any-permutation/) |  LC  | Same usage-frequency idea.                                                         | Treat the solution as a pipeline: materialize coverage/state, transform it, then prefix for final queries. |
|   5 | [Shifting Letters II](https://leetcode.com/problems/shifting-letters-ii/)                                         |  LC  | Diff shifts → prefix net shift → transform chars.                                  | Treat the solution as a pipeline: materialize coverage/state, transform it, then prefix for final queries. |

#### Problem-specific reference pseudocode

> Try the problem first. Use these blocks only to verify the solution
> flow. Each one is problem-specific and formatted vertically for
> GitHub.

##### 1. [Karen and Coffee](https://codeforces.com/problemset/problem/816/B)

**Recognition:** Coverage count → mark coverage\>=k → prefix again.

``` text
diff intervals -> coverage
good[i]=(coverage>=k)
prefGood=prefix(good)
each query = prefGood[R]-prefGood[L-1]
```

##### 2. [Greg and Array](https://codeforces.com/problemset/problem/295/A)

**Recognition:** First diff counts operation applications; second diff
applies weighted operations.

``` text
diff over operation-index ranges -> times per operation
then diff over array ranges using times*d
prefix to final values
```

##### 3. [Little Girl and Maximum Sum](https://codeforces.com/problemset/problem/276/C)

**Recognition:** Query diff → usage counts → sort with A.

``` text
query diff -> prefix frequency array -> sort frequency and values -> maximize weighted sum
```

##### 4. [Maximum Sum Obtained of Any Permutation](https://leetcode.com/problems/maximum-sum-obtained-of-any-permutation/)

**Recognition:** Same usage-frequency idea.

``` text
request diff -> prefix requestCount -> sort counts and nums -> dot product mod M
```

##### 5. [Shifting Letters II](https://leetcode.com/problems/shifting-letters-ii/)

**Recognition:** Diff shifts → prefix net shift → transform chars.

``` text
shift diff -> prefix netShift -> apply normalized shift to each character
```

#### How to practice this section

1.  Read only the statement and try to name the pattern in **60
    seconds**.
2.  Write what the prefix/difference state represents in one sentence.
3.  Write the algebra or state transition before code.
4.  Use the pseudocode only after you have your own approach.
5.  Re-solve failed problems after 2–3 days without opening this sheet.

------------------------------------------------------------------------

<a id="pattern-13"></a>

## Pattern 13 — Difference Array + Coordinate Compression

### Recognition signal — detailed

#### Real-world decoding drill

**Scenario:** Train line with huge coordinates: state changes only at
stations/endpoints, so compress those coordinates and sweep only
meaningful gaps.

**Translate it during a contest:**

``` text
story objects/events
        ↓
array / prefix state / interval events
        ↓
what can be precomputed once?
        ↓
Compress only coordinates where state changes; preserve real gap lengths during the sweep.
```

**Reference pseudocode:**

``` text
collect endpoints; sort+unique; map endpoint->id; diff on ids; sweep and use real coordinate gaps
```

**Statement clues**

``` text
- coordinates up to 1e9 / 1e18
- only O(N) interval endpoints are present
- need sweep/difference logic
- allocating one cell per coordinate is impossible
```

Core observation:

``` text
Nothing changes between consecutive important coordinates.
Therefore store only important coordinates.
```

**Mini dry run**

``` text
intervals:
[10, 1,000,000]
[500, 900]

Important coordinates:
10, 500, 901, 1,000,001

compress:
10        -> 0
500       -> 1
901       -> 2
1000001   -> 3
```

Visualization:

``` text
real line:
10 ---------------- 500 ---- 900 ---------------- 1,000,000
^                    ^       ^                    ^
only boundaries matter

compressed:
0 --------- 1 ------- 2 -------------------------- 3
```

Do not forget:

``` text
compressed-index gap 1
does NOT mean real distance 1.
```

**Real-world mapping**

A train route may span 1,000 km, but if trains only start/stop at 20
stations, the system state changes only at those stations.

### 🧠 Retention diagram — **ONLY IMPORTANT COORDINATES EXIST**

``` text
Huge coordinate line:
0 ..................................... 10^9

But events occur only at:
        12      1000       9000000
         ^        ^            ^

Compress/sort these event points:
index:   0        1            2

Sweep only where something changes.
The empty millions of coordinates need no storage.
```

**Real-world trigger:** sparse street lights, bookings over huge
timestamps, intervals on coordinates up to 1e9/1e18.

**60-second question**

> Is the coordinate universe huge but the number of change-points small?

**C++ template**

``` cpp
vector<long long> xs;
for (auto [L, R] : segs) {
    xs.push_back(L);
    xs.push_back(R + 1); // when safe / appropriate
}

sort(xs.begin(), xs.end());
xs.erase(unique(xs.begin(), xs.end()), xs.end());

auto id = [&](long long x) {
    return lower_bound(xs.begin(), xs.end(), x) - xs.begin();
};

vector<long long> diff(xs.size() + 1);
for (auto [L, R] : segs) {
    ++diff[id(L)];
    --diff[id(R + 1)];
}
```

Use when coordinates are huge:

``` text
coordinate <= 1e9 / 1e18
but only O(N) endpoints matter
```

Do **not** allocate the entire coordinate line.

Pipeline:

``` text
collect important coordinates
        ↓
sort + unique
        ↓
map each endpoint to compressed index
        ↓
difference/event sweep
        ↓
remember actual distance between coordinates
```

Important:

``` text
compressed index distance != original coordinate distance
```

### Problems

### Optimized direct practice set

> These are intentionally filtered to problems that directly exercise
> this pattern. Warm-ups that mainly belong to another topic were
> removed from this section.
>
> **Reference pseudocode is problem-specific**: it shows how this exact
> problem uses the pattern, not merely the generic pattern template.

|  \# | Problem                                                                                     | Site  | Why it maps to this pattern                                     | Approach                                                                                   |
|----:|---------------------------------------------------------------------------------------------|:-----:|-----------------------------------------------------------------|--------------------------------------------------------------------------------------------|
|   1 | [Describe the Painting](https://leetcode.com/problems/describe-the-painting/)               |  LC   | Endpoints are the only places active sum changes.               | Compress only coordinates where state changes; preserve real gap lengths during the sweep. |
|   2 | [Brightest Position on Street](https://leetcode.com/problems/brightest-position-on-street/) |  LC   | Light creates +1/-1 events.                                     | Compress only coordinates where state changes; preserve real gap lengths during the sweep. |
|   3 | [Covered Points Count](https://codeforces.com/problemset/problem/1000/C)                    |  CF   | Sweep sorted endpoints and multiply active count by gap length. | Compress only coordinates where state changes; preserve real gap lengths during the sweep. |
|   4 | [Snuke Prime](https://atcoder.jp/contests/abc188/tasks/abc188_d)                            | Bonus | Canonical coordinate-compressed difference/sweep problem.       | Compress only coordinates where state changes; preserve real gap lengths during the sweep. |

#### Problem-specific reference pseudocode

> Try the problem first. Use these blocks only to verify the solution
> flow. Each one is problem-specific and formatted vertically for
> GitHub.

##### 1. [Describe the Painting](https://leetcode.com/problems/describe-the-painting/)

**Recognition:** Endpoints are the only places active sum changes.

``` text
store ordered endpoint deltas (coordinate compression/map)
sweep consecutive coordinates
active color sum is constant on [prev,x)
```

##### 2. [Brightest Position on Street](https://leetcode.com/problems/brightest-position-on-street/)

**Recognition:** Light creates +1/-1 events.

``` text
for light(pos,r): event[pos-r]++, event[pos+r+1]--
sweep sorted events
track smallest coordinate with max active
```

##### 3. [Covered Points Count](https://codeforces.com/problemset/problem/1000/C)

**Recognition:** Sweep sorted endpoints and multiply active count by gap
length.

``` text
events[l]++,events[r+1]--
sweep sorted x
before applying event[x], add x-prev to ans[active]
active+=event[x]
```

##### 4. [Snuke Prime](https://atcoder.jp/contests/abc188/tasks/abc188_d)

**Recognition:** Canonical coordinate-compressed difference/sweep
problem.

``` text
event[a]+=c
event[b+1]-=c
sweep sorted days
add (day-prev)*min(active,C)
active+=event[day]
```

#### How to practice this section

1.  Read only the statement and try to name the pattern in **60
    seconds**.
2.  Write what the prefix/difference state represents in one sentence.
3.  Write the algebra or state transition before code.
4.  Use the pseudocode only after you have your own approach.
5.  Re-solve failed problems after 2–3 days without opening this sheet.

------------------------------------------------------------------------

<a id="pattern-14"></a>

## Pattern 14 — Difference of a Prefix Array / Reconstruct Original

### Recognition signal — detailed

#### Real-world decoding drill

**Scenario:** Odometer readings: if you know cumulative distance at
checkpoints, consecutive differences recover the distance of each
segment.

**Translate it during a contest:**

``` text
story objects/events
        ↓
array / prefix state / interval events
        ↓
what can be precomputed once?
        ↓
Differentiate cumulative information: consecutive prefix differences reveal local values.
```

**Reference pseudocode:**

``` text
A[i]=P[i]-P[i-1]; validate/reconstruct local values from consecutive cumulative states
```

**Statement clues**

``` text
- given prefix sums rather than original values
- recover/check the original sequence
- consecutive prefix values encode one local element
- one prefix may be missing/corrupted
```

Prefix sum is discrete integration; consecutive difference reverses it.

``` text
pref[i] = pref[i-1] + A[i]
A[i]    = pref[i] - pref[i-1]
```

**Mini dry run**

``` text
pref = [0, 3, 8, 10, 17]

recover:
A1 = 3  - 0  = 3
A2 = 8  - 3  = 5
A3 = 10 - 8  = 2
A4 = 17 - 10 = 7

A = [3,5,2,7]
```

Visualization:

``` text
A:       3    5    2    7
          \    \    \    \
pref: 0 --3----8---10---17
        differences recover A
```

**Real-world mapping**

If an odometer shows total distance after each day, subtract consecutive
readings to recover distance driven that day.

### 🧠 Retention diagram — **PREFIX VALUES ARE CHECKPOINTS; DIFFERENCES ARE STEPS**

``` text
Original steps:
a1   a2   a3   a4
 |    |    |    |
 v    v    v    v
P1   P2   P3   P4

Recover:
a1 = P1
a2 = P2 - P1
a3 = P3 - P2
a4 = P4 - P3

CHECKPOINT TOTALS
      ↓ subtract neighbors
INDIVIDUAL MOVES
```

**Real-world trigger:** odometer readings, cumulative revenue reports,
cumulative distances — recover each individual increment from
consecutive totals.

**60-second question**

> Am I given cumulative totals and asked about the increments that
> created them?

**C++ template**

``` cpp
vector<long long> a(n + 1);
for (int i = 1; i <= n; ++i) {
    a[i] = pref[i] - pref[i - 1];
}
```

Prefix sums can be reversed:

``` text
pref[i] = pref[i-1] + A[i]

therefore

A[i] = pref[i] - pref[i-1]
```

This is discrete differentiation.

**Quick recap:**

``` text
given prefix sums
some prefixes missing
restore/check original sequence
```

### Problems

### Optimized direct practice set

> These are intentionally filtered to problems that directly exercise
> this pattern. Warm-ups that mainly belong to another topic were
> removed from this section.
>
> **Reference pseudocode is problem-specific**: it shows how this exact
> problem uses the pattern, not merely the generic pattern template.

|  \# | Problem                                                                     | Site | Why it maps to this pattern                                             | Approach                                                                                  |
|----:|-----------------------------------------------------------------------------|:----:|-------------------------------------------------------------------------|-------------------------------------------------------------------------------------------|
|   1 | [Prefix Permutation Sums](https://codeforces.com/problemset/problem/1851/D) |  CF  | Consecutive differences should mostly be unused values 1..n.            | Differentiate cumulative information: consecutive prefix differences reveal local values. |
|   2 | [Prefix Sum Addicts](https://codeforces.com/problemset/problem/1738/B)      |  CF  | Differences of known suffix-prefix values impose monotonic constraints. | Differentiate cumulative information: consecutive prefix differences reveal local values. |
|   3 | [Array Recovery](https://codeforces.com/problemset/problem/1739/B)          |  CF  | Reconstruct local values from an encoded difference-like array.         | Differentiate cumulative information: consecutive prefix differences reveal local values. |

#### Problem-specific reference pseudocode

> Try the problem first. Use these blocks only to verify the solution
> flow. Each one is problem-specific and formatted vertically for
> GitHub.

##### 1. [Prefix Permutation Sums](https://codeforces.com/problemset/problem/1851/D)

**Recognition:** Consecutive differences should mostly be unused values
1..n.

``` text
diff consecutive given prefixes
valid diffs in [1,n] mark used
one abnormal/missing diff must equal sum of the two missing permutation values
```

##### 2. [Prefix Sum Addicts](https://codeforces.com/problemset/problem/1738/B)

**Recognition:** Differences of known suffix-prefix values impose
monotonic constraints.

``` text
known prefix suffix sums -> consecutive differences are forced elements
require these differences nondecreasing
check earliest unknown prefix can fit before first forced value
```

##### 3. [Array Recovery](https://codeforces.com/problemset/problem/1739/B)

**Recognition:** Reconstruct local values from an encoded
difference-like array.

``` text
a[0]=d[0]
for i>0: candidates=a[i-1]+d[i] and a[i-1]-d[i]
if both nonnegative and distinct -> -1
else choose valid candidate
```

#### How to practice this section

1.  Read only the statement and try to name the pattern in **60
    seconds**.
2.  Write what the prefix/difference state represents in one sentence.
3.  Write the algebra or state transition before code.
4.  Use the pseudocode only after you have your own approach.
5.  Re-solve failed problems after 2–3 days without opening this sheet.

------------------------------------------------------------------------

<a id="pattern-15"></a>

## Pattern 15 — AP Range Update (Linear Difference)

### Recognition signal — detailed

#### Real-world decoding drill

**Scenario:** Salary plan: an update gives day L a bonus a, next day
a+d, then a+2d... Track how the increment itself changes instead of
touching every day.

**Translate it during a contest:**

``` text
story objects/events
        ↓
array / prefix state / interval events
        ↓
what can be precomputed once?
        ↓
An AP is linear in position; maintain boundary changes for value and slope.
```

**Reference pseudocode:**

``` text
encode start value and slope changes at boundaries; prefix slope; prefix value; apply AP contribution
```

**Statement clues**

``` text
- range update is not constant
- it adds x, x+d, x+2d, ... across [L,R]
- update contribution changes linearly with index
- many such updates are processed offline
```

Convert local AP position into a global linear function:

``` text
value at i
= x + (i-L)*d
= d*i + (x-d*L)
= p*i + q
```

So store the two coefficients separately.

**Mini dry run**

``` text
update [2,5]
x = 3, d = 2

desired additions:
index: 2  3  4  5
add:   3  5  7  9

formula:
2*i - 1

i=2 -> 3
i=3 -> 5
i=4 -> 7
i=5 -> 9
```

Visualization:

``` text
AP inside range:
3 -> 5 -> 7 -> 9
     +2   +2   +2

global form:
P*i + Q
P = 2
Q = -1
```

**Real-world mapping**

A promotion gives day 1 = 3 points, then 2 extra points each following
day. Rather than update every day, store the slope and intercept active
over the range.

### 🧠 Retention diagram — **THE UPDATE ITSELF HAS A SLOPE**

``` text
Add AP on [L..R]:

position:  L   L+1 L+2 L+3
add:       1    2   3   4
            \   \   \   \
             +1 each step

constant update:
5 5 5 5      slope = 0

AP update:
1 2 3 4      slope = +1

So store enough information to reconstruct:
START VALUE + CHANGE PER STEP
```

**Real-world trigger:** salary/price increases that grow by a fixed
amount each day, staircase bonuses, linearly increasing range effects.

**60-second question**

> Is every update value a linear function of position?

**C++ template — offline AP additions**

``` cpp
vector<long long> dP(n + 2), dQ(n + 2);

auto addAP = [&](int L, int R, long long x, long long d) {
    long long p = d;
    long long q = x - d * L;

    dP[L] += p;       dP[R + 1] -= p;
    dQ[L] += q;       dQ[R + 1] -= q;
};

long long P = 0, Q = 0;
for (int i = 1; i <= n; ++i) {
    P += dP[i];
    Q += dQ[i];
    a[i] += P * i + Q;
}
```

Suppose every update adds:

``` text
x, x+d, x+2d, ..., x+(R-L)d
```

to `[L,R]`.

Write contribution at position i:

``` text
x + (i-L)*d
= d*i + (x-d*L)
```

So every AP update is a linear function:

``` text
p*i + q
```

Maintain two difference arrays:

``` text
diffP -> coefficient of i
diffQ -> constant
```

For update `[L,R]`:

``` text
p = d
q = x - d*L

diffP[L]   += p
diffP[R+1] -= p

diffQ[L]   += q
diffQ[R+1] -= q
```

After prefixing:

``` text
A[i] += P[i]*i + Q[i]
```

### Practice set

### Optimized direct practice set

> These are intentionally filtered to problems that directly exercise
> this pattern. Warm-ups that mainly belong to another topic were
> removed from this section.
>
> **Reference pseudocode is problem-specific**: it shows how this exact
> problem uses the pattern, not merely the generic pattern template.

|  \# | Problem                                                                                                                       |    Site    | Why it maps to this pattern                                 | Approach                                                                    |
|----:|-------------------------------------------------------------------------------------------------------------------------------|:----------:|-------------------------------------------------------------|-----------------------------------------------------------------------------|
|   1 | [Polynomial Queries](https://cses.fi/problemset/task/1736)                                                                    | Bonus Core | Range update adds `1,2,3,...`; canonical AP update problem. | An AP is linear in position; maintain boundary changes for value and slope. |
|   2 | [Greg and Array](https://codeforces.com/problemset/problem/295/A)                                                             |  CF Prep   | Layer difference arrays before adding linear coefficients.  | An AP is linear in position; maintain boundary changes for value and slope. |
|   3 | [Sum of Absolute Differences in a Sorted Array](https://leetcode.com/problems/sum-of-absolute-differences-in-a-sorted-array/) |     LC     | Derive linear terms involving index and prefix sum.         | An AP is linear in position; maintain boundary changes for value and slope. |

#### Problem-specific reference pseudocode

> Try the problem first. Use these blocks only to verify the solution
> flow. Each one is problem-specific and formatted vertically for
> GitHub.

##### 1. [Polynomial Queries](https://cses.fi/problemset/task/1736)

**Recognition:** Range update adds `1,2,3,...`; canonical AP update
problem.

``` text
lazy segment tree node stores sum
AP update [L,R] adds (i-L+1)
update overlap [l,r] by arithmetic-series sum using first=l-L+1, len=r-l+1
propagate shifted AP lazily
```

##### 2. [Greg and Array](https://codeforces.com/problemset/problem/295/A)

**Recognition:** Layer difference arrays before adding linear
coefficients.

``` text
use diff twice: operation-range diff -> execution count
array-range diff -> constant d*count updates
prefix both layers
```

##### 3. [Sum of Absolute Differences in a Sorted Array](https://leetcode.com/problems/sum-of-absolute-differences-in-a-sorted-array/)

**Recognition:** Derive linear terms involving index and prefix sum.

``` text
prefix sums + linear index algebra: left=a[i]*i-pref[i]
right=(pref[n]-pref[i+1])-a[i]*(n-i-1)
```

#### How to practice this section

1.  Read only the statement and try to name the pattern in **60
    seconds**.
2.  Write what the prefix/difference state represents in one sentence.
3.  Write the algebra or state transition before code.
4.  Use the pseudocode only after you have your own approach.
5.  Re-solve failed problems after 2–3 days without opening this sheet.

------------------------------------------------------------------------

<a id="pattern-16"></a>

## Pattern 16 — GP / Recurrence-Based Range Update

### Recognition signal — detailed

#### Real-world decoding drill

**Scenario:** Recurring payment schedule: an update follows a recurrence
such as Fibonacci. Store the small recurrence state needed to continue
the sequence across the range.

**Translate it during a contest:**

``` text
story objects/events
        ↓
array / prefix state / interval events
        ↓
what can be precomputed once?
        ↓
Represent the update by its recurrence state rather than independent per-position values.
```

**Reference pseudocode:**

``` text
store recurrence seed/state at L and cancellation state after R; sweep/segment structure propagates recurrence
```

**Statement clues**

``` text
- update adds GP/Fibonacci/another recurrence over a range
- next contribution depends on previous contribution(s)
- ordinary diff cannot describe the interior with a constant
- need to start a recurrence and cancel it after R
```

For GP:

``` text
x[i] = r*x[i-1]
```

Define recurrence deviation:

``` text
D[i] = x[i] - r*x[i-1]
```

Inside a perfect GP, `D[i] = 0`; only boundaries need explicit events.

**Mini dry run — GP**

``` text
Range [2,5], start=3, r=2

wanted:
index: 1  2  3   4   5   6
value: 0  3  6  12  24   0

start event at 2 = +3
without cancellation, recurrence would make index 6 = 48
so cancel 48 at 6
```

Reconstruction:

``` text
x[i] = D[i] + r*x[i-1]

i=1: 0
i=2: 3 + 2*0  = 3
i=3: 0 + 2*3  = 6
i=4: 0 + 2*6  = 12
i=5: 0 + 2*12 = 24
i=6: -48 + 2*24 = 0
```

Visualization:

``` text
START
  |
  3 -> 6 -> 12 -> 24 -> 48 ...
                       |
                    CANCEL
```

**Real-world mapping**

Compound growth starts on day `L`: each day doubles the previous
contribution. You only need to seed the process and later inject the
exact cancellation that stops propagation.

### 🧠 Retention diagram — **THE NEXT UPDATE VALUE DEPENDS ON PREVIOUS VALUES**

``` text
Fibonacci-like effect:

1, 1, 2, 3, 5, 8, ...
      ^  ^
      |  |
next = previous two combined

Unlike AP:
1, 2, 3, 4, 5
   +1 +1 +1

Recurrence update must remember a STATE,
not only one constant difference.
```

**Real-world trigger:** processes where tomorrow's effect depends on
earlier effects — recurrence growth rather than constant/linear growth.

**60-second question**

> Does the range update obey a recurrence instead of adding an
> independent value at each point?

**C++ template — simple GP boundary idea**

``` cpp
// Fixed ratio r for all GP updates.
// For modular problems, apply MOD to multiplication/addition.
long long r;                     // given by the problem
vector<long long> powR(n + 2, 1);
vector<long long> D(n + 2, 0);

for (int i = 1; i <= n + 1; ++i) {
    powR[i] = powR[i - 1] * r;
}

auto addGP = [&](int L, int R, long long x) {
    D[L] += x;

    // Without cancellation, the next term at R+1 would be:
    // x * r^(R-L+1)
    D[R + 1] -= x * powR[R - L + 1];
};

long long cur = 0;
for (int i = 1; i <= n; ++i) {
    cur = D[i] + r * cur;
    a[i] += cur;
}
```

> This is the boundary model for a fixed-ratio GP. Fibonacci or other
> recurrences need enough state to represent their recurrence and a
> matching cancellation at `R+1`.

GP updates look like:

``` text
x, x*r, x*r^2, ...
```

Unlike AP, the next value depends multiplicatively on the previous
value.

**Quick recap:**

``` text
range update follows a recurrence
Fibonacci / GP / linear recurrence
```

Main idea:

``` text
Do not store every term.
Store where a recurrence contribution STARTS
and where its influence must be CANCELLED.
```

For advanced problems this often becomes:

``` text
difference-like boundary state
+ recurrence propagation
```

### Practice progression

### Optimized direct practice set

> These are intentionally filtered to problems that directly exercise
> this pattern. Warm-ups that mainly belong to another topic were
> removed from this section.
>
> **Reference pseudocode is problem-specific**: it shows how this exact
> problem uses the pattern, not merely the generic pattern template.

|  \# | Problem                                                                        |    Site     | Why it maps to this pattern                                        | Approach                                                                                  |
|----:|--------------------------------------------------------------------------------|:-----------:|--------------------------------------------------------------------|-------------------------------------------------------------------------------------------|
|   1 | [Polynomial Queries](https://cses.fi/problemset/task/1736)                     | Bonus Prep  | AP is the next step before recurrence updates.                     | Represent the update by its recurrence state rather than independent per-position values. |
|   2 | [DZY Loves Fibonacci Numbers](https://codeforces.com/problemset/problem/446/C) | CF Advanced | Range updates add Fibonacci sequence; propagate recurrence lazily. | Represent the update by its recurrence state rather than independent per-position values. |
|   3 | [Fibonacci Segment](https://codeforces.com/problemset/problem/365/C)           | CF Advanced | Treat sequence update as recurrence state, not independent values. | Represent the update by its recurrence state rather than independent per-position values. |

#### Problem-specific reference pseudocode

> Try the problem first. Use these blocks only to verify the solution
> flow. Each one is problem-specific and formatted vertically for
> GitHub.

##### 1. [Polynomial Queries](https://cses.fi/problemset/task/1736)

**Recognition:** AP is the next step before recurrence updates.

``` text
AP preparation: lazy node keeps first term and common difference
shift first term when pushing to right child
```

##### 2. [DZY Loves Fibonacci Numbers](https://codeforces.com/problemset/problem/446/C)

**Recognition:** Range updates add Fibonacci sequence; propagate
recurrence lazily.

``` text
segment tree with lazy Fibonacci seeds (f1,f2)
range update adds Fibonacci sequence
combine/push seeds using Fibonacci transition
node sum updated by recurrence-sum formula
```

##### 3. [Fibonacci Segment](https://codeforces.com/problemset/problem/365/C)

**Recognition:** Treat sequence update as recurrence state, not
independent values.

``` text
represent each Fibonacci-range update by recurrence state
segment tree/lazy propagation shifts recurrence seeds by child offset and maintains range sum
```

#### How to practice this section

1.  Read only the statement and try to name the pattern in **60
    seconds**.
2.  Write what the prefix/difference state represents in one sentence.
3.  Write the algebra or state transition before code.
4.  Use the pseudocode only after you have your own approach.
5.  Re-solve failed problems after 2–3 days without opening this sheet.

------------------------------------------------------------------------

<a id="pattern-17"></a>

# Pattern 17 — Prefix Sum + Binary Search / K-th Value

### Recognition signal — detailed

#### Real-world decoding drill

**Scenario:** Warehouse inventory: prefix counts tell how many items are
available up to a value/position; binary search the first prefix that
reaches the k-th item.

**Translate it during a contest:**

``` text
story / query
        ↓
identify the cumulative state
        ↓
Use monotonic cumulative information to locate the first position/value whose count reaches K.
```

**Reference pseudocode:**

``` text
build cumulative count/value pref; answer k-th with lower_bound(pref >= K) or binary search on answer
```

**Statement clues**

``` text
- find K-th element in an implicitly repeated multiset
- each value has a frequency/count
- ask first position where cumulative count reaches K
- predicate "are there at least K items <= X?" is monotone
```

**Mini dry run**

``` text
value:       2   5   8   10
frequency:   3   2   4    1
prefix:      3   5   9   10

K = 7

positions 1..3 -> value 2
positions 4..5 -> value 5
positions 6..9 -> value 8
                ^
                K=7 lives here

first prefix >= 7 is 9
answer = 8
```

Visualization:

``` text
2 2 2 | 5 5 | 8 8 8 8 | 10
1 2 3   4 5   6 7 8 9    10
                    ^
                    K=7
```

**Real-world mapping**

Movie seats are sold in blocks by price category. If each category has a
count, the K-th customer belongs to the first category whose cumulative
capacity reaches K.

### 🧠 Retention diagram — **PREFIX COUNTS CREATE A MONOTONE ANSWER SPACE**

``` text
candidate x:
1 2 3 4 5 6 7 8 ...
      ↓
count of valid items <= x:
0 1 1 2 3 3 4 5 ...
      nondecreasing
      ───────────►

Need k-th valid item:
find FIRST x where countValid(x) >= k

false false false | true true true
                  ^
             binary search
```

**Real-world trigger:** find the k-th ticket/number/time satisfying a
rule when you can quickly count how many valid choices exist up to `x`.

**60-second question**

> Can I sort by value, accumulate frequencies, and locate K with
> `lower_bound`?

**C++ template**

``` cpp
vector<long long> pref(m);
pref[0] = freq[0];
for (int i = 1; i < m; ++i) {
    pref[i] = pref[i - 1] + freq[i];
}

int idx = lower_bound(pref.begin(), pref.end(), K) - pref.begin();
long long answer = values[idx];
```

**Quick recap:**

``` text
K-th item
cumulative frequency
first position whose prefix count >= K
```

ASCII:

``` text
value:       2    5    8    10
frequency:   3    2    4     1

prefix:      3    5    9    10

K = 7
              first prefix >= 7
                       |
                       v
                       9

answer = 8
```

Core:

``` cpp
idx = lower_bound(pref.begin(), pref.end(), K) - pref.begin();
```

### Problems

### Optimized direct practice set

> Filtered to problems that directly exercise this pattern; unrelated
> warm-ups are removed.

|  \# | Problem                                                                     |  Site  | Why it maps to this pattern                                                              | Approach                                                                                       |
|----:|-----------------------------------------------------------------------------|:------:|------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------|
|   1 | [Interesting drink](https://codeforces.com/problemset/problem/706/B)        |   CF   | Count how many values \<= query using upper_bound.                                       | Use monotonic cumulative information to locate the first position/value whose count reaches K. |
|   2 | [K-th Not Divisible by n](https://codeforces.com/problemset/problem/1352/C) |   CF   | Count valid numbers up to X / derive direct formula.                                     | Use monotonic cumulative information to locate the first position/value whose count reaches K. |
|   3 | [K-th Beautiful String](https://codeforces.com/problemset/problem/1328/B)   |   CF   | Cumulative combinatorial blocks determine the K-th object.                               | Use monotonic cumulative information to locate the first position/value whose count reaches K. |
|   4 | Kth Val (your lecture problem)                                              | Course | Diff range coverage → `(A[i],freq[i])` → sort/merge → prefix frequency → lower_bound(K). | Use monotonic cumulative information to locate the first position/value whose count reaches K. |

#### Problem-specific reference pseudocode

> Try the problem first. Use these blocks only to verify the solution
> flow. Each one is problem-specific and formatted vertically for
> GitHub.

##### 1. [Interesting drink](https://codeforces.com/problemset/problem/706/B)

**Recognition:** Count how many values \<= query using upper_bound.

``` text
sort prices
for each budget x: answer=upper_bound(prices,x)-begin
```

##### 2. [K-th Not Divisible by n](https://codeforces.com/problemset/problem/1352/C)

**Recognition:** Count valid numbers up to X / derive direct formula.

``` text
binary search smallest x with countValid(x)=x-floor(x/n) >= k
answer=x (or use closed-form k+(k-1)/(n-1))
```

##### 3. [K-th Beautiful String](https://codeforces.com/problemset/problem/1328/B)

**Recognition:** Cumulative combinatorial blocks determine the K-th
object.

``` text
for first b position from right: blockSize=#choices for second b
subtract blocks from k until containing block
place two b's accordingly
```

##### 4. Kth Val (your lecture problem)

**Recognition:** Diff range coverage → `(A[i],freq[i])` → sort/merge →
prefix frequency → lower_bound(K).

``` text
build cumulative count/value pref
answer k-th with lower_bound(pref >= K) or binary search on answer
```

#### How to practice this section

1.  Name the pattern in **60 seconds**.
2.  Define the cumulative state in one sentence.
3.  Derive the condition before coding.
4.  Check the pseudocode only after your own attempt.
5.  Re-solve failures after 2–3 days.

------------------------------------------------------------------------

<a id="pattern-18"></a>

# Pattern 18 — Prefix Sum + Monotonic Stack / Boundaries

### Recognition signal — detailed

#### Real-world decoding drill

**Scenario:** Buildings/terrain: a monotonic stack finds how far an
element remains the minimum/maximum; prefix sums instantly give the
total value inside that boundary.

**Translate it during a contest:**

``` text
story / query
        ↓
identify the cumulative state
        ↓
Use the stack for ownership boundaries and prefix sums for the aggregate inside each owned interval.
```

**Reference pseudocode:**

``` text
stack -> left/right boundary for each i; prefix -> range sum; combine boundary span with A[i] contribution
```

**Statement clues**

``` text
- score of a subarray depends on its sum AND min/max/boundary
- for each A[i], find the maximal range where it is minimum/maximum
- prefix sum can evaluate a range once another technique finds its ends
```

Separate the jobs:

``` text
monotonic stack -> WHERE can i extend?
prefix sum      -> WHAT is the sum/value there?
combine         -> score/contribution
```

**Mini dry run — min-product idea**

``` text
A = [3, 1, 5, 6, 4, 2]

For value 4 at index 5:
left smaller = index 2 (value 1)
right smaller = index 6 (value 2)

So the range where 4 is minimum is:
indices 3..5 -> [5,6,4]

prefix quickly gives:
sum(3..5) = 15

candidate score = 4 * 15
```

Visualization:

``` text
          [5  6  4]
           ^     ^
        bounded by smaller elements

stack -> [L,R]
prefix -> sum(L,R)
```

**Real-world mapping**

A bridge segment's capacity is controlled by its weakest support
(minimum), but its total load comes from all spans in the segment. One
structure finds the weakest-support boundaries; prefix computes total
load.

### 🧠 Retention diagram — **EACH ELEMENT OWNS A REGION**

``` text
          6
      4       5
   2     3
         ^
       A[i]

Monotonic stack finds:
nearest smaller on LEFT
nearest smaller on RIGHT

L <--------- i ---------> R

Inside this region, A[i] can be the minimum.
Prefix sums then measure the intervals that A[i] owns.
```

**Real-world trigger:** each building/product/value contributes to every
interval where it remains the minimum/maximum; stack finds boundaries,
prefix measures the interval.

**60-second question**

> Does one technique naturally find candidate boundaries while prefix
> sum evaluates each candidate range?

**C++ template skeleton**

``` cpp
vector<long long> pref(n + 1);
for (int i = 1; i <= n; ++i) pref[i] = pref[i - 1] + a[i];

// Compute previous/next smaller (or greater) with monotonic stacks.
vector<int> L(n + 1), R(n + 1);

// ... stack logic fills L[i], R[i] ...

for (int i = 1; i <= n; ++i) {
    long long rangeSum = pref[R[i] - 1] - pref[L[i]];
    // combine rangeSum with a[i]
}
```

At higher CF/LC levels, prefix sum often does **not** solve the whole
problem. Another technique finds boundaries, while prefix sums evaluate
the chosen range.

Typical architecture:

``` text
monotonic stack -> find L/R boundary
prefix sum      -> calculate sum(L,R)
combine         -> contribution / score
```

### Problems

### Optimized direct practice set

> Filtered to problems that directly exercise this pattern; unrelated
> warm-ups are removed.

|  \# | Problem                                                                                             |  Site   | Why it maps to this pattern                                              | Approach                                                                                             |
|----:|-----------------------------------------------------------------------------------------------------|:-------:|--------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------|
|   1 | [Maximum Subarray Min-Product](https://leetcode.com/problems/maximum-subarray-min-product/)         |   LC    | Stack finds maximal range where A\[i\] is minimum; prefix gives its sum. | Use the stack for ownership boundaries and prefix sums for the aggregate inside each owned interval. |
|   2 | [Sum of Subarray Minimums](https://leetcode.com/problems/sum-of-subarray-minimums/)                 |   LC    | Count how many subarrays choose each element as minimum.                 | Use the stack for ownership boundaries and prefix sums for the aggregate inside each owned interval. |
|   3 | [Sum of Total Strength of Wizards](https://leetcode.com/problems/sum-of-total-strength-of-wizards/) | LC Hard | Stack boundaries + double prefix.                                        | Use the stack for ownership boundaries and prefix sums for the aggregate inside each owned interval. |
|   4 | [Imbalanced Array](https://codeforces.com/problemset/problem/817/D)                                 |   CF    | Count each element's contribution as max minus min.                      | Use the stack for ownership boundaries and prefix sums for the aggregate inside each owned interval. |

#### Problem-specific reference pseudocode

> Try the problem first. Use these blocks only to verify the solution
> flow. Each one is problem-specific and formatted vertically for
> GitHub.

##### 1. [Maximum Subarray Min-Product](https://leetcode.com/problems/maximum-subarray-min-product/)

**Recognition:** Stack finds maximal range where A\[i\] is minimum;
prefix gives its sum.

``` text
prefix sums
monotonic stack finds prevSmaller and nextSmaller
sum=pref[R]-pref[L]
ans=max(ans,a[i]*sum)
```

##### 2. [Sum of Subarray Minimums](https://leetcode.com/problems/sum-of-subarray-minimums/)

**Recognition:** Count how many subarrays choose each element as
minimum.

``` text
stack finds distance to previous strictly smaller and next smaller-or-equal
contribution=a[i]*leftCount*rightCount
sum contributions
```

##### 3. [Sum of Total Strength of Wizards](https://leetcode.com/problems/sum-of-total-strength-of-wizards/)

**Recognition:** Stack boundaries + double prefix.

``` text
stack gives minimum boundaries
double prefix gives sum of all subarray sums on left/right
combine counts, multiply by strength[i]
```

##### 4. [Imbalanced Array](https://codeforces.com/problemset/problem/817/D)

**Recognition:** Count each element's contribution as max minus min.

``` text
use monotonic stacks to count subarrays where a[i] is maximum and minimum
ans += a[i]*(countMax-countMin)
```

#### How to practice this section

1.  Name the pattern in **60 seconds**.
2.  Define the cumulative state in one sentence.
3.  Derive the condition before coding.
4.  Check the pseudocode only after your own attempt.
5.  Re-solve failures after 2–3 days.

------------------------------------------------------------------------

<a id="pattern-19"></a>

# Pattern 19 — Prefix on Trees / Paths

### Recognition signal — detailed

#### Real-world decoding drill

**Scenario:** Company hierarchy: root-to-employee cumulative values act
like prefixes; subtract root-path states, often with LCA correction, to
answer path/subtree questions.

**Translate it during a contest:**

``` text
story / query
        ↓
identify the cumulative state
        ↓
Generalize array prefix to root-to-node state; use DFS/Euler/LCA depending on whether the query is path or subtree.
```

**Reference pseudocode:**

``` text
DFS: state[v]=state[parent]+value/edge; path(u,v)=state[u]+state[v]-2*state[lca] (+ node correction if needed)
```

**Statement clues**

``` text
- queries are on tree paths or subtrees
- additive/parity state from root to node
- subtree should become a contiguous interval
- path formula uses LCA plus root-prefix information
```

Two common conversions:

``` text
PATH:
root-prefix + LCA

SUBTREE:
Euler tour -> contiguous array interval
```

**Mini dry run — root prefix**

``` text
        1(5)
       /    \
    2(3)    3(4)
      |
    4(2)

root sums:
pref[1] = 5
pref[2] = 8
pref[3] = 9
pref[4] = 10

path 4 -> 3
LCA = 1

sum =
pref[4] + pref[3] - 2*pref[1] + value[1]
= 10 + 9 - 10 + 5
= 14

actual path values:
4(2) -> 2(3) -> 1(5) -> 3(4)
2+3+5+4 = 14
```

Euler visualization:

``` text
DFS entry order:
node: 1 2 4 3
time: 1 2 3 4

subtree(2) = times [2,3]
            = contiguous range
```

**Real-world mapping**

A company org chart is a tree. Root-prefix can represent accumulated
budget/permission from CEO to employee; Euler tour makes every manager's
team occupy one continuous interval.

### 🧠 Retention diagram — **A ROOT-TO-NODE PATH IS A PREFIX**

``` text
root
 |
 +-- A
 |    |
 |    +-- C
 |
 +-- B
      |
      +-- D

prefix(C) = root → A → C
prefix(D) = root → B → D

For a path:

u -------- LCA -------- v

path information
= prefix(u) + prefix(v)
  - shared root/LCA contribution
```

**Real-world trigger:** folder paths, organization trees, or roads
rooted at HQ — cumulative information from the root behaves like a 1D
prefix.

**60-second question**

> Can I convert the tree query into either root-to-node cumulative
> states or an Euler-tour array range?

**C++ template — root additive prefix**

``` cpp
vector<long long> pref(n + 1);
vector<int> tin(n + 1), tout(n + 1);
int timer = 0;

function<void(int,int)> dfs = [&](int u, int p) {
    tin[u] = ++timer;
    pref[u] = (p == 0 ? 0 : pref[p]) + value[u];

    for (int v : g[u]) {
        if (v != p) dfs(v, u);
    }
    tout[u] = timer;
};

// Path sums additionally need LCA preprocessing.
```

Prefix is not limited to arrays.

Root-to-node prefix:

``` text
root
 |
 +---- u ---- ... ---- v

path sum can often be built from root-prefix states
```

For additive tree path queries:

``` text
path(u,v)
= pref[u] + pref[v]
- 2*pref[lca]
+ value[lca]
```

### Problems

### Optimized direct practice set

> Filtered to problems that directly exercise this pattern; unrelated
> warm-ups are removed.

|  \# | Problem                                                                                                                               |    Site    | Why it maps to this pattern                              | Approach                                                                                                            |
|----:|---------------------------------------------------------------------------------------------------------------------------------------|:----------:|----------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------|
|   1 | [Path Sum III](https://leetcode.com/problems/path-sum-iii/)                                                                           |     LC     | Prefix-frequency map along current DFS path.             | Generalize array prefix to root-to-node state; use DFS/Euler/LCA depending on whether the query is path or subtree. |
|   2 | [Count Paths That Can Form a Palindrome in a Tree](https://leetcode.com/problems/count-paths-that-can-form-a-palindrome-in-a-tree/)   |  LC Hard   | Root-to-node parity masks; compare masks.                | Generalize array prefix to root-to-node state; use DFS/Euler/LCA depending on whether the query is path or subtree. |
|   3 | [Minimum Edge Weight Equilibrium Queries in a Tree](https://leetcode.com/problems/minimum-edge-weight-equilibrium-queries-in-a-tree/) |  LC Hard   | Prefix frequency vector from root to each node + LCA.    | Generalize array prefix to root-to-node state; use DFS/Euler/LCA depending on whether the query is path or subtree. |
|   4 | [Military Problem](https://codeforces.com/problemset/problem/1006/E)                                                                  |     CF     | Flatten subtree into contiguous Euler-tour interval.     | Generalize array prefix to root-to-node state; use DFS/Euler/LCA depending on whether the query is path or subtree. |
|   5 | [Tree Cutting](https://codeforces.com/problemset/problem/1118/F1)                                                                     |     CF     | Subtree prefix/count contribution.                       | Generalize array prefix to root-to-node state; use DFS/Euler/LCA depending on whether the query is path or subtree. |
|   6 | [Blood Cousins](https://codeforces.com/problemset/problem/208/E)                                                                      | CF Stretch | Euler intervals + depth-indexed prefix/count structures. | Generalize array prefix to root-to-node state; use DFS/Euler/LCA depending on whether the query is path or subtree. |

#### Problem-specific reference pseudocode

> Try the problem first. Use these blocks only to verify the solution
> flow. Each one is problem-specific and formatted vertically for
> GitHub.

##### 1. [Path Sum III](https://leetcode.com/problems/path-sum-iii/)

**Recognition:** Prefix-frequency map along current DFS path.

``` text
DFS with path prefix sum
freq[0]=1
at node: pref+=val
ans+=freq[pref-target]
freq[pref]++
recurse
freq[pref]-- on backtrack
```

##### 2. [Count Paths That Can Form a Palindrome in a Tree](https://leetcode.com/problems/count-paths-that-can-form-a-palindrome-in-a-tree/)

**Recognition:** Root-to-node parity masks; compare masks.

``` text
DFS/root mask where edge char toggles one bit
for each mask count earlier same mask and masks differing by one bit
```

##### 3. [Minimum Edge Weight Equilibrium Queries in a Tree](https://leetcode.com/problems/minimum-edge-weight-equilibrium-queries-in-a-tree/)

**Recognition:** Prefix frequency vector from root to each node + LCA.

``` text
DFS stores root->node frequency vector for edge weights + binary-lifting LCA
pathFreq=cnt[u]+cnt[v]-2*cnt[lca]
answer=pathLen-max(pathFreq)
```

##### 4. [Military Problem](https://codeforces.com/problemset/problem/1006/E)

**Recognition:** Flatten subtree into contiguous Euler-tour interval.

``` text
DFS preorder flatten tree
tin[v]=position, subtreeSize[v]
k-th node in subtree = order[tin[v]+k-1] if k<=subtreeSize[v]
```

##### 5. [Tree Cutting](https://codeforces.com/problemset/problem/1118/F1)

**Recognition:** Subtree prefix/count contribution.

``` text
DFS subtree counts of red/blue
edge above v is valid when subtree contains all of one color and none of the other
count such v
```

##### 6. [Blood Cousins](https://codeforces.com/problemset/problem/208/E)

**Recognition:** Euler intervals + depth-indexed prefix/count
structures.

``` text
DFS Euler tin/tout and depth
group tin values by depth
for query(v,p): u=p-th ancestor(v)
count nodes at depth[v] with tin in subtree(u) via two binary searches, minus v
```

#### How to practice this section

1.  Name the pattern in **60 seconds**.
2.  Define the cumulative state in one sentence.
3.  Derive the condition before coding.
4.  Check the pseudocode only after your own attempt.
5.  Re-solve failures after 2–3 days.

------------------------------------------------------------------------

<a id="cm-level-recognition-map"></a>

# CM-Level Recognition Map

``` text
QUESTION SIGNAL                           FIRST THOUGHT
---------------------------------------------------------------
many static sum queries                  prefix sum
left vs right                            prefix + total/suffix
subarray sum = K                         prefix + hashmap
subarray divisible by K                  prefix modulo
equal counts / strange balance           transform + prefix
XOR/parity state                         prefix XOR / bitmask
index-weighted range sum                 multiple weighted prefixes
sum of prefix sums                       prefix of prefix
rectangle queries                        2D prefix
many offline range additions             difference array
interval start/end events                difference / sweep line
huge coordinates                         compression + sweep/diff
given prefix, recover values              consecutive differences
AP range addition                        coefficient diff arrays
Fibonacci/GP-like update                 recurrence boundary state
K-th by frequencies                      cumulative prefix + lower_bound
range boundary + range score             stack/two pointers + prefix
tree path/subtree                         root prefix / Euler tour
```

------------------------------------------------------------------------

<a id="suggested-order-to-cm"></a>

# Suggested Order to CM

<a id="stage-1-automatic-fundamentals"></a>

## Stage 1 — automatic fundamentals

``` text
Pattern 1  Basic prefix
Pattern 2  Prefix/suffix
Pattern 3  Prefix + hashmap
Pattern 4  Prefix modulo
Pattern 10 Basic difference
```

Target: solve these without notes.

<a id="stage-2-div2-bc-strength"></a>

## Stage 2 — Div2 B/C strength

``` text
Pattern 5  Transform + prefix
Pattern 6  Prefix XOR/state
Pattern 7  Weighted prefix
Pattern 9  2D prefix
Pattern 11 Sweep/event diff
Pattern 12 Multi-stage diff/prefix
Pattern 14 Recover from prefix
Pattern 17 K-th/cumulative frequency
```

<a id="stage-3-expert-cm"></a>

## Stage 3 — Expert → CM

``` text
Pattern 8  Double prefix
Pattern 13 Compression + diff
Pattern 15 AP/polynomial updates
Pattern 16 Recurrence updates
Pattern 18 Stack + prefix
Pattern 19 Tree prefix/Euler
```

At CM level the important jump is:

``` text
NOT:
"This is a prefix-sum problem."

BUT:
"I can transform this condition into a prefix invariant,
then combine prefix with hashing / modulo / sorting /
binary search / stack / sweep / tree flattening."
```

------------------------------------------------------------------------

<a id="60-second-contest-checklist"></a>

# 60-Second Contest Checklist

When you see an array/range problem, ask in this order:

``` text
1. Is the answer about a contiguous range?

2. Can I express it as:
      prefix[R] OP prefix[L-1] ?

3. Is the condition:
      sum = K?
      modulo?
      equal counts?
      XOR/parity?

4. Can I transform A[i] first?

5. Are there many range UPDATES rather than queries?
      -> difference array

6. Are coordinates huge?
      -> coordinate compression / map events

7. Does coefficient depend on index?
      -> weighted prefixes / AP decomposition

8. Do I need K-th / first position?
      -> cumulative count + binary search

9. Does another structure find the range?
      -> prefix + stack/two pointers/binary search

10. Is the array actually a tree/grid?
      -> Euler/root prefix/2D prefix
```

------------------------------------------------------------------------

<a id="high-value-cf-core-set"></a>

# High-value CF core set

If you want a compact first pass before doing every table, prioritize:

1.  CF 433B — Kuriyama Mirai's Stones
2.  CF 276C — Little Girl and Maximum Sum
3.  CF 295A — Greg and Array
4.  CF 816B — Karen and Coffee
5.  CF 1398C — Good Subarrays
6.  CF 1738B — Prefix Sum Addicts
7.  CF 1851D — Prefix Permutation Sums
8.  CF 1000C — Covered Points Count
9.  CF 817D — Imbalanced Array
10. CF 617E — XOR and Favorite Number

Then move to the advanced combination problems.

------------------------------------------------------------------------

<a id="final-rule"></a>

# Final rule

Do not memorize 19 independent templates.

Compress them mentally into four questions:

``` text
PREFIX:
What cumulative information makes a range removable by subtraction/XOR?

TRANSFORM:
What should each element represent so the condition becomes cumulative?

DIFFERENCE:
Can I mark only where an update starts and stops?

COMBINATION:
What second technique finds the boundary/state that prefix evaluates?
```

That is the progression from basic prefix sums toward Expert/CM-level
problem solving.
