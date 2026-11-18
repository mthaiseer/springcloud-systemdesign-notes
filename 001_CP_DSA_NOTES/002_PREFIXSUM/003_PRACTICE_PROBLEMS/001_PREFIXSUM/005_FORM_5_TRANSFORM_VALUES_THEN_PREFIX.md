# PREFIX SUM PATTERNS

## Pattern 5 --- Transform Values, Then Prefix

------------------------------------------------------------------------

## Table of Contents

-   [Pattern Overview](#pattern-overview)
-   [Core Transformation Model](#core-transformation-model)
-   [Generic Dry Run](#generic-dry-run)
-   [Problem 1 --- Contiguous Array](#problem-1--contiguous-array)
-   [Problem 2 --- Count Number of Nice
    Subarrays](#problem-2--count-number-of-nice-subarrays)
-   [Problem 3 --- Good Subarrays](#problem-3--good-subarrays)
-   [Problem 4 --- Longest Well-Performing
    Interval](#problem-4--longest-well-performing-interval)
-   [Problem 5 --- Count Subarrays With Median
    K](#problem-5--count-subarrays-with-median-k)
-   [Fast Revision Model](#fast-revision-model)

------------------------------------------------------------------------

## Pattern Overview

### What kind of problem is this?

Use this pattern when the original values are **not directly useful for
prefix sum**, but the condition becomes a simple sum after replacing
each value with a meaningful contribution.

``` text
ORIGINAL CONDITION
       ↓
transform each value
       ↓
+1 / -1 / 0 / indicator / adjusted value
       ↓
SUBARRAY SUM CONDITION
       ↓
PREFIX SUM + HASH MAP
```

The important question is:

``` text
What should each element contribute
so that the original condition becomes
a simple equation on a subarray sum?
```

### Common Transformations

``` text
equal 0 and 1
0 -> -1
1 -> +1
Need transformed sum = 0

exactly k odd numbers
even -> 0
odd  -> 1
Need transformed sum = k

sum = length
a[i] -> a[i] - 1
Need transformed sum = 0

more tiring than non-tiring
hours > 8  -> +1
hours <= 8 -> -1
Need transformed sum > 0

median = k
value < k -> -1
value = k -> 0
value > k -> +1
Need balance 0 or 1 and subarray must contain k
```

### Recognition Signals

``` text
equal number of two categories
count exactly k elements with property X
subarray sum equals its length
more A than B
balance between smaller and larger values
median condition
```

### Complexity

Most problems in this form become:

``` text
Time:  O(n)
Space: O(n)
```

------------------------------------------------------------------------

## Core Transformation Model

Suppose the original condition depends on a property of every element.

Define:

``` text
b[i] = contribution made by a[i]
```

Then:

``` text
original condition on a[l..r]

        ↓ transform

sum of b[l..r] has a simple target
```

Now prefix algebra applies:

``` text
sumB(l,r)
= prefB[r] - prefB[l-1]
```

Example:

``` text
Need equal number of 0 and 1.

0 contributes -1
1 contributes +1
```

If a subarray contains:

``` text
zeros = Z
ones  = O
```

its transformed sum is:

``` text
(+1)*O + (-1)*Z
= O - Z
```

Equal counts mean:

``` text
O = Z

O - Z = 0
```

Therefore:

``` text
equal 0/1
   ↓
transform
   ↓
subarray sum = 0
```

This is the central idea of Form 5.

------------------------------------------------------------------------

## Generic Dry Run

Original:

``` text
a = [0,1,1,0]
```

Transform:

``` text
0 -> -1
1 -> +1

b = [-1,+1,+1,-1]
```

Prefix:

``` text
position:   empty   0   1   2   3
prefix:       0    -1   0   1   0
```

The same prefix `0` appears multiple times.

Why?

``` text
prefix[j] = prefix[i]

pref[j] - pref[i] = 0
```

So the transformed elements between them sum to `0`.

That means:

``` text
number of +1
=
number of -1
```

which maps back to:

``` text
number of original 1s
=
number of original 0s
```

------------------------------------------------------------------------

# Problem 1 --- Contiguous Array

Problem Link: [Contiguous
Array](https://leetcode.com/problems/contiguous-array/)

### What is the problem asking?

Given a binary array, find the **maximum length contiguous subarray
containing the same number of `0`s and `1`s**.

### Remove Story Nouns → Variables

``` text
0 -> one category
1 -> opposite category

Need:
count(0) = count(1)
```

### Observation

Raw prefix sum counts only `1`s, so equal `0`/`1` is awkward.

Transform:

``` text
0 -> -1
1 -> +1
```

Then:

``` text
equal zeros and ones
        ↓
transformed subarray sum = 0
        ↓
same prefix value at two positions
```

For maximum length, store the **earliest index** of every prefix.

### Compact Algebra Derivation

Let:

``` text
Z = number of zeros
O = number of ones
```

After transformation:

``` text
subarray sum
= O*(+1) + Z*(-1)

= O - Z
```

Need:

``` text
O = Z
```

Therefore:

``` text
O - Z = 0
```

Using prefix sums:

``` text
pref[r] - pref[l-1] = 0

pref[r] = pref[l-1]
```

So two equal prefix values identify a balanced subarray.

For maximum length:

``` text
length = currentIndex - earliestIndex[prefix]
```

### Simple Dry Run

``` text
nums = [0,1,0]

transform:
[-1,+1,-1]

start:
first[0] = -1
prefix = 0
best = 0

i = 0
value = 0 -> -1
prefix = -1
first[-1] = 0

i = 1
value = 1 -> +1
prefix = 0

prefix 0 was seen at -1

length
= 1 - (-1)
= 2

best = 2

i = 2
value = 0 -> -1
prefix = -1

prefix -1 was first seen at 0

length
= 2 - 0
= 2

answer = 2
```

### Pseudocode

``` text
first[0] = -1
prefix = 0
best = 0

for i = 0..n-1:

    if nums[i] == 0:
        prefix -= 1
    else:
        prefix += 1

    if prefix already exists:
        best = max(best, i - first[prefix])
    else:
        first[prefix] = i

return best
```

### C++

``` cpp
class Solution {
public:
    int findMaxLength(vector<int>& nums) {
        unordered_map<int, int> first;
        first[0] = -1;

        int prefix = 0;
        int best = 0;

        for (int i = 0; i < (int)nums.size(); ++i) {
            prefix += (nums[i] == 0 ? -1 : 1);

            if (first.count(prefix)) {
                best = max(best, i - first[prefix]);
            } else {
                first[prefix] = i;
            }
        }

        return best;
    }
};
```

### Complexity

``` text
Time:  O(n) expected
Space: O(n)
```

------------------------------------------------------------------------

# Problem 2 --- Count Number of Nice Subarrays

Problem Link: [Count Number of Nice
Subarrays](https://leetcode.com/problems/count-number-of-nice-subarrays/)

### What is the problem asking?

Count contiguous subarrays containing **exactly `k` odd numbers**.

The actual magnitudes of the numbers do not matter. Only whether each
value is odd or even matters.

### Remove Story Nouns → Variables

``` text
odd  -> important event
even -> contributes nothing

Need:
number of odd elements in [l..r] = k
```

### Observation

Transform:

``` text
odd  -> 1
even -> 0
```

Now the problem becomes:

``` text
count subarrays with transformed sum = k
```

This is ordinary Prefix Sum + Hash Map.

### Compact Algebra Derivation

Define:

``` text
b[i] = nums[i] % 2
```

Therefore:

``` text
odd  -> 1
even -> 0
```

For a subarray:

``` text
sum(b[l..r])
=
number of odd values in nums[l..r]
```

Need exactly `k` odds:

``` text
pref[r] - pref[l-1] = k
```

Rearrange:

``` text
pref[l-1]
=
pref[r] - k
```

At current prefix `P`:

``` text
need = P - k
```

So:

``` text
answer += freq[P-k]
```

### Simple Dry Run

``` text
nums = [1,1,2,1,1]
k = 3

transform parity:

[1,1,0,1,1]

start:
freq[0] = 1
prefix = 0
answer = 0

x=1
prefix=1
need=-2
+0

x=1
prefix=2
need=-1
+0

x=0
prefix=2
need=-1
+0
freq[2] becomes 2

x=1
prefix=3
need=0
+1
answer=1

x=1
prefix=4
need=1
freq[1]=1
+1

answer=2
```

### Pseudocode

``` text
freq[0] = 1
prefix = 0
answer = 0

for x in nums:
    value = x % 2
    prefix += value

    answer += freq[prefix - k]

    freq[prefix]++

return answer
```

### C++

``` cpp
class Solution {
public:
    int numberOfSubarrays(vector<int>& nums, int k) {
        unordered_map<int, int> freq;
        freq[0] = 1;

        int prefix = 0;
        int ans = 0;

        for (int x : nums) {
            prefix += (x & 1);

            ans += freq[prefix - k];
            freq[prefix]++;
        }

        return ans;
    }
};
```

### Complexity

``` text
Time:  O(n) expected
Space: O(n)
```

------------------------------------------------------------------------

# Problem 3 --- Good Subarrays

Problem Link: [Good
Subarrays](https://codeforces.com/problemset/problem/1398/C)

### What is the problem asking?

Given digits, count contiguous subarrays where:

``` text
sum of elements
=
length of subarray
```

Example:

``` text
[2,0]

sum    = 2
length = 2

GOOD
```

### Remove Story Nouns → Variables

For `[l..r]`:

``` text
sum(l,r) = r-l+1
```

We want to convert the **sum = length** condition into a zero-sum
condition.

### Observation

A subarray of length `L` contains exactly `L` elements.

Subtract `1` from every element:

``` text
b[i] = a[i] - 1
```

Then:

``` text
sum(b[l..r])
=
sum(a[l..r]) - length
```

Therefore a good subarray becomes:

``` text
sum(b[l..r]) = 0
```

Equivalent prefix form:

``` text
pref[i] - i
```

must repeat.

### Compact Algebra Derivation

Start:

``` text
pref[r] - pref[l-1]
=
r-l+1
```

Rewrite length:

``` text
r-l+1
=
r-(l-1)
```

So:

``` text
pref[r] - pref[l-1]
=
r-(l-1)
```

Move matching index terms:

``` text
pref[r] - r
=
pref[l-1] - (l-1)
```

Define:

``` text
key[i]
=
pref[i] - i
```

Then:

``` text
key[r]
=
key[l-1]
```

Equivalent element transformation:

``` text
b[i] = a[i] - 1
```

because:

``` text
sum(a[i]-1)
=
sum(a[i]) - length
```

Good subarray:

``` text
sum(a) = length

        ⇕

sum(a[i]-1) = 0
```

### Simple Dry Run

``` text
a = [1,2,0]

transform a[i]-1:

b = [0,1,-1]

prefix of b:

empty -> 0

i=1:
prefix = 0
previous 0 count = 1
answer = 1

i=2:
prefix = 1
previous 1 count = 0
answer = 1

i=3:
prefix = 0
previous 0 count = 2
answer = 3

valid original subarrays:

[1]
[2,0]
[1,2,0]
```

### Pseudocode

``` text
freq[0] = 1
prefix = 0
answer = 0

for each digit x:
    transformed = x - 1
    prefix += transformed

    answer += freq[prefix]
    freq[prefix]++

print answer
```

### C++

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
        string s;
        cin >> n >> s;

        unordered_map<long long, long long> freq;
        freq[0] = 1;

        long long prefix = 0;
        long long ans = 0;

        for (char c : s) {
            int x = c - '0';

            prefix += x - 1;

            ans += freq[prefix];
            freq[prefix]++;
        }

        cout << ans << '\n';
    }
}
```

### Complexity

``` text
Time:  O(n) expected per test-case total
Space: O(n)
```

------------------------------------------------------------------------

# Problem 4 --- Longest Well-Performing Interval

Problem Link: [Longest Well-Performing
Interval](https://leetcode.com/problems/longest-well-performing-interval/)

### What is the problem asking?

A day is:

``` text
tiring     if hours[i] > 8
non-tiring if hours[i] <= 8
```

Find the longest contiguous interval where:

``` text
number of tiring days
>
number of non-tiring days
```

### Remove Story Nouns → Variables

``` text
T = tiring count
N = non-tiring count

Need:
T > N
```

### Observation

Transform:

``` text
tiring     -> +1
non-tiring -> -1
```

Then subarray sum becomes:

``` text
T - N
```

Need:

``` text
T - N > 0
```

So the story becomes:

``` text
find longest transformed subarray
whose sum > 0
```

### Compact Algebra Derivation

After transformation:

``` text
sum(l,r)
=
(+1)*T + (-1)*N

=
T-N
```

Original requirement:

``` text
T > N
```

Subtract `N`:

``` text
T-N > 0
```

Therefore:

``` text
well-performing interval
⇕

transformed subarray sum > 0
```

Let current prefix be `P`.

If:

``` text
P > 0
```

then the whole prefix `[0..i]` is valid.

Otherwise, we need an earlier prefix strictly smaller than `P`.

Because prefix changes only by `+1/-1`, checking:

``` text
P-1
```

at its earliest position is sufficient.

### Simple Dry Run

``` text
hours = [9,9,6,0,6,6,9]

transform:

[+1,+1,-1,-1,-1,-1,+1]

prefix:

i=0 -> 1
prefix > 0
length = 1

i=1 -> 2
prefix > 0
length = 2

i=2 -> 1
prefix > 0
length = 3

i=3 -> 0
not positive

i=4 -> -1
not positive

i=5 -> -2
not positive

i=6 -> -1

need earlier prefix:
-1 - 1 = -2

prefix -2 first occurred at i=5

length = 6-5 = 1

best remains 3

answer = 3
```

### Pseudocode

``` text
first = empty map
prefix = 0
best = 0

for i = 0..n-1:

    if hours[i] > 8:
        prefix += 1
    else:
        prefix -= 1

    if prefix > 0:
        best = i + 1
    else:
        if prefix-1 exists:
            best = max(best, i - first[prefix-1])

    if prefix not stored:
        first[prefix] = i

return best
```

### C++

``` cpp
class Solution {
public:
    int longestWPI(vector<int>& hours) {
        unordered_map<int, int> first;

        int prefix = 0;
        int best = 0;

        for (int i = 0; i < (int)hours.size(); ++i) {
            prefix += (hours[i] > 8 ? 1 : -1);

            if (prefix > 0) {
                best = i + 1;
            } else if (first.count(prefix - 1)) {
                best = max(best, i - first[prefix - 1]);
            }

            if (!first.count(prefix)) {
                first[prefix] = i;
            }
        }

        return best;
    }
};
```

### Complexity

``` text
Time:  O(n) expected
Space: O(n)
```

------------------------------------------------------------------------

# Problem 5 --- Count Subarrays With Median K

Problem Link: [Count Subarrays With Median
K](https://leetcode.com/problems/count-subarrays-with-median-k/)

### What is the problem asking?

The array contains distinct values.

Count subarrays whose median is exactly `k`.

A valid subarray must contain `k`.

### Remove Story Nouns → Variables

Relative to `k`, the exact values are not important.

Only three states matter:

``` text
nums[i] < k
nums[i] = k
nums[i] > k
```

Transform them into balance contributions.

### Observation

Transform:

``` text
nums[i] < k -> -1
nums[i] = k ->  0
nums[i] > k -> +1
```

For any subarray containing `k`, let:

``` text
small = count(values < k)
large = count(values > k)
```

Transformed sum:

``` text
balance
=
large - small
```

For the median convention in this problem, a subarray containing `k` is
valid when:

``` text
balance = 0
or
balance = 1
```

So the median problem becomes a **balance matching** problem around the
position of `k`.

### Compact Algebra Derivation

Define:

``` text
small = # values < k
large = # values > k
```

After transformation:

``` text
sum
=
(+1)*large
+
(-1)*small
+
0

=
large-small
```

For odd-length valid subarray:

``` text
small = large

large-small = 0
```

For even-length valid subarray under the problem's left-middle median
definition:

``` text
large = small + 1

large-small = 1
```

Therefore:

``` text
balance ∈ {0,1}
```

Split around the position of `k`.

If:

``` text
leftBalance + rightBalance = 0
```

then:

``` text
leftBalance = -rightBalance
```

If:

``` text
leftBalance + rightBalance = 1
```

then:

``` text
leftBalance = 1-rightBalance
```

Therefore for each right balance `R`, count left balances:

``` text
-R
and
1-R
```

### Simple Dry Run

``` text
nums = [3,2,1,4,5]
k = 4

transform relative to 4:

3 -> -1
2 -> -1
1 -> -1
4 ->  0
5 -> +1

b = [-1,-1,-1,0,+1]

position of k = 3

Build balances by extending LEFT from k:

l=3:
balance = 0
freq[0] = 1

l=2:
balance = -1
freq[-1] = 1

l=1:
balance = -2
freq[-2] = 1

l=0:
balance = -3
freq[-3] = 1

Now extend RIGHT from k.

r=3:
rightBalance = 0

need:
-R   = 0
1-R  = 1

freq[0] = 1
freq[1] = 0

answer += 1

subarray:
[4]

r=4:
rightBalance = +1

need:
-R   = -1
1-R  = 0

freq[-1] = 1
freq[0]  = 1

answer += 2

subarrays:
[1,4,5]
[4,5]

final answer = 3
```

### Pseudocode

``` text
find position pos where nums[pos] = k

freq = empty map
balance = 0

for i = pos down to 0:

    if nums[i] < k:
        balance--
    else if nums[i] > k:
        balance++

    freq[balance]++

answer = 0
balance = 0

for i = pos to n-1:

    if nums[i] < k:
        balance--
    else if nums[i] > k:
        balance++

    answer += freq[-balance]
    answer += freq[1-balance]

return answer
```

### C++

``` cpp
class Solution {
public:
    int countSubarrays(vector<int>& nums, int k) {
        int n = nums.size();

        int pos = find(nums.begin(), nums.end(), k) - nums.begin();

        unordered_map<int, int> freq;

        int balance = 0;

        for (int i = pos; i >= 0; --i) {
            if (nums[i] < k) {
                --balance;
            } else if (nums[i] > k) {
                ++balance;
            }

            freq[balance]++;
        }

        long long ans = 0;
        balance = 0;

        for (int i = pos; i < n; ++i) {
            if (nums[i] < k) {
                --balance;
            } else if (nums[i] > k) {
                ++balance;
            }

            ans += freq[-balance];
            ans += freq[1 - balance];
        }

        return static_cast<int>(ans);
    }
};
```

### Complexity

``` text
Time:  O(n) expected
Space: O(n)
```

------------------------------------------------------------------------

# Fast Revision Model

## Core Pattern

``` text
ORIGINAL VALUES
      ↓
throw away irrelevant magnitude
      ↓
encode useful contribution
      ↓
PREFIX SUM / BALANCE
      ↓
HASH MAP
```

## What changes between the problems?

  -----------------------------------------------------------------------
  Problem                 Transformation          New mathematical
                                                  condition
  ----------------------- ----------------------- -----------------------
  Contiguous Array        `0 -> -1`, `1 -> +1`    transformed sum `= 0`

  Count Number of Nice    even `-> 0`, odd `-> 1` transformed sum `= k`
  Subarrays                                       

  Good Subarrays          `a[i] -> a[i]-1`        transformed sum `= 0`

  Longest Well-Performing `>8 -> +1`, `<=8 -> -1` transformed sum `> 0`
  Interval                                        

  Count Subarrays With    `<k -> -1`, `k -> 0`,   balance `= 0 or 1`,
  Median K                `>k -> +1`              containing `k`
  -----------------------------------------------------------------------

## Three Important Transformation Families

### 1. Category Balance

``` text
A -> +1
B -> -1

equal A and B
⇕

sum = 0
```

Example:

``` text
Contiguous Array
```

### 2. Property Counter

``` text
has property    -> 1
does not        -> 0

exactly K occurrences
⇕

sum = K
```

Example:

``` text
Nice Subarrays
```

### 3. Subtract the Expected Contribution

If condition is:

``` text
sum(a[l..r])
=
C * length
```

transform:

``` text
b[i] = a[i] - C
```

Then:

``` text
sum(b[l..r]) = 0
```

For Good Subarrays:

``` text
C = 1

b[i] = a[i]-1
```

## 5-Minute Recognition

``` text
1. WHAT?
   What property must the subarray satisfy?

2. IGNORE MAGNITUDE?
   Do exact values matter,
   or only category/property?

3. ASSIGN CONTRIBUTION
   +1 / -1 / 0 / a[i]-C

4. REWRITE
   Convert original condition into:
   sum = 0
   sum = K
   sum > 0
   balance condition

5. APPLY PREFIX
   Use frequency / earliest index /
   balance map as required.
```

## Recognition Rule

``` text
COMPLICATED SUBARRAY CONDITION
          ↓
CAN EACH ELEMENT BE REPLACED
BY ITS EFFECT ON THE CONDITION?
          ↓
YES
          ↓
TRANSFORM VALUES
          ↓
PREFIX SUM / BALANCE
```

The main contest question to remember is:

``` text
Can I replace each value with a small contribution
so the original condition becomes a simple
subarray-sum equation?
```
