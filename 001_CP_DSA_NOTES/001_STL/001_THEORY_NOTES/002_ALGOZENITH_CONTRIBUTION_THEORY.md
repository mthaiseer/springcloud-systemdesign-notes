# AlgoZenith — Contribution Technique

> Goal: stop enumerating every subset/subarray/query result.  
> Instead, **fix one element / position / pivot**, count how many valid structures use it, and add its contribution.

---

## TOC

- [0. Prerequisites](#0-prerequisites)
- [1. Contribution Technique — Core Idea](#1-contribution-technique--core-idea)
- [2. Sum of All Subset Sums](#2-sum-of-all-subset-sums)
- [3. Sum of All Subarray Sums](#3-sum-of-all-subarray-sums)
- [4. Pivot / Ending-Index Contribution](#4-pivot--ending-index-contribution)
- [5. Sum of Products of All Subarrays](#5-sum-of-products-of-all-subarrays)
- [6. CF 276C — Little Girl and Maximum Sum](#6-cf-276c--little-girl-and-maximum-sum)
- [7. LC 907 — Sum of Subarray Minimums](#7-lc-907--sum-of-subarray-minimums)
- [8. Recognition Guide](#8-recognition-guide)
- [9. Compact Formula Summary](#9-compact-formula-summary)

---

# 0. Prerequisites

Before contribution technique, be comfortable with these small ideas.

## 0.1 Subset vs Subarray

### Subset

You may choose elements from anywhere.

```text
Array: [3, 5, 2]

Some subsets:

[]
[3]
[5]
[2]
[3,5]
[3,2]
[5,2]
[3,5,2]
```

Order/contiguity is not important.

### Subarray

A subarray must be contiguous.

```text
Array: [3, 5, 2]

Valid subarrays:

[3]
[5]
[2]
[3,5]
[5,2]
[3,5,2]

Not a subarray:
[3,2]
```

## 0.2 Counting Independent Choices

If one choice has `A` possibilities and another independent choice has `B` possibilities:

```text
total possibilities = A × B
```

Example:

```text
left endpoint choices  = 2
right endpoint choices = 3

total subarrays = 2 × 3 = 6
```

This multiplication principle is used everywhere in contribution problems.

## 0.3 Basic Powers of Two

If every element has two choices:

```text
take
don't take
```

then for `k` independent elements:

```text
number of possibilities = 2^k
```

Example:

```text
2 elements:

00
01
10
11

total = 4 = 2^2
```

## 0.4 Prefix / Difference Array

For many range queries `[L,R]`, instead of updating every position:

```text
diff[L]     += 1
diff[R + 1] -= 1
```

Then prefix-sum `diff` to recover the frequency of every position.

Example:

```text
queries:
[1,3]
[2,4]

diff updates:

1: +1
4: -1

2: +1
5: -1

prefix frequency:
[1,2,2,1]
```

This is needed for CF 276C.

## 0.5 Monotonic Stack

A monotonic stack helps find nearest smaller/greater elements.

For subarray minimum contribution we need boundaries such as:

```text
previous smaller
next smaller or equal
```

Why?

Because once we know the valid left and right boundary around `a[i]`, we can count how many subarrays choose `a[i]` as their minimum.

## 0.6 Modulo Arithmetic

Some contribution answers become very large.

Typical pattern:

```cpp
ans = (ans + contribution) % MOD;
```

For LeetCode 907:

```cpp
const long long MOD = 1e9 + 7;
```

---

# 1. Contribution Technique — Core Idea

## 1.1 What is the usual brute force?

A common brute-force approach is:

```text
generate every structure
    ↓
calculate its value
    ↓
add to answer
```

For example:

```text
all subsets
all subarrays
all query ranges
all paths
```

But the number of structures may be huge.

## 1.2 Reverse the thinking

Instead of asking:

```text
What is the value of every structure?
```

ask:

```text
How much does one fixed object contribute to the final answer?
```

General template:

```text
fix object
    ↓
count how many valid structures contain/use it
    ↓
contribution = object value × frequency
    ↓
sum contribution of every object
```

Formula:

```text
answer = Σ contribution(i)
```

## 1.3 Visual Model

Suppose:

```text
a = [a0, a1, a2, a3]
```

Instead of:

```text
structure 1
structure 2
structure 3
...
```

we think:

```text
a0 appears F0 times → contribution = a0 × F0
a1 appears F1 times → contribution = a1 × F1
a2 appears F2 times → contribution = a2 × F2
a3 appears F3 times → contribution = a3 × F3
```

Then:

```text
answer =
a0×F0 + a1×F1 + a2×F2 + a3×F3
```

## 1.4 Recognition Signal

Think contribution when the problem says something similar to:

```text
sum over all subsets
sum over all subarrays
sum over all paths
sum over all queries
sum of minimums / maximums
total frequency
how many times each element participates
```

Mental trigger:

```text
"Can I fix one element and count how many answers contain it?"
```

---

# 2. Sum of All Subset Sums

## 2.1 Problem

Given an array, find:

```text
sum of sums of all subsets
```

Example:

```text
a = [3,5,2]
```

## 2.2 Brute Force

Generate every subset:

```text
[]
[3]
[5]
[2]
[3,5]
[3,2]
[5,2]
[3,5,2]
```

Then add every subset sum.

This takes exponential time.

```text
O(2^n)
```

## 2.3 Contribution Observation

Fix one element, say `5`.

```text
a = [3, 5, 2]
        ^
        fixed
```

We force `5` to be present.

What about the other elements?

```text
3 → take / don't take
2 → take / don't take
```

Two independent choices for each remaining element.

Therefore:

```text
frequency of 5
= 2^(n-1)
```

The same is true for every array element.

## 2.4 ASCII Visual

For fixed `5`:

```text
                 5 must be taken
                       |
             +---------+---------+
             |                   |
          take 3              don't take 3
             |                   |
       +-----+-----+       +-----+-----+
       |           |       |           |
    take 2     don't 2   take 2     don't 2

subsets containing 5:

[3,5,2]
[3,5]
[5,2]
[5]
```

Count:

```text
4 = 2^(3-1)
```

## 2.5 Step-by-Step Derivation

```text
Step 1:
Fix a[i].

Step 2:
a[i] must be selected.

Step 3:
There are n-1 remaining elements.

Step 4:
Each remaining element has 2 choices:
take / don't take.

Step 5:
frequency(i) = 2^(n-1)

Step 6:
contribution(i) = a[i] × 2^(n-1)

Step 7:
answer = Σ a[i] × 2^(n-1)
```

Since the frequency is identical for every element:

```text
answer = 2^(n-1) × Σa[i]
```

## 2.6 Dry Run

```text
a = [3,5,2]
n = 3

frequency of every element
= 2^(3-1)
= 4
```

Contribution:

```text
3 → 3×4 = 12
5 → 5×4 = 20
2 → 2×4 =  8

answer = 12 + 20 + 8
       = 40
```

## 2.7 Time Complexity

Brute force:

```text
O(2^n)
```

Contribution:

```text
sum array      → O(n)
power compute  → O(n)
total          → O(n)
```

## 2.8 C++17

```cpp
long long subsetSum(vector<long long>& a) {
    int n = a.size();

    long long sum = 0;
    for (long long x : a) {
        sum += x;
    }

    long long power = 1;
    for (int i = 0; i < n - 1; i++) {
        power *= 2;
    }

    return sum * power;
}
```

## 2.9 Common Mistakes

```text
Mistake 1:
Using 2^n instead of 2^(n-1).

Mistake 2:
Forgetting that the fixed element is already forced to be selected.

Mistake 3:
Confusing subset with subarray.
```

---

# 3. Sum of All Subarray Sums

## 3.1 Problem

Find:

```text
sum of sums of all subarrays
```

Example:

```text
a = [3,5,2]
```

## 3.2 Brute Force

Enumerate all `[L,R]`:

```text
[3]
[5]
[2]
[3,5]
[5,2]
[3,5,2]
```

A naive implementation may take:

```text
O(n^2)
```

or even:

```text
O(n^3)
```

if every subarray sum is recomputed.

## 3.3 Contribution Observation

Fix index `i`.

A subarray `[L,R]` contains `i` iff:

```text
L <= i <= R
```

So we only need to count:

```text
how many choices for L?
how many choices for R?
```

## 3.4 ASCII Visual

For:

```text
a = [3, 5, 2, 7]
        ^
        i = 1
```

Possible left endpoints:

```text
L = 0
L = 1

count = i + 1 = 2
```

Possible right endpoints:

```text
R = 1
R = 2
R = 3

count = n - i = 3
```

Visual:

```text
        fixed i

index:  0    1    2    3
        [3] [5]  [2]  [7]
             ^

L choices:
        |----|
             |

R choices:
             |
             |----|
             |---------|

Total combinations:

2 left choices × 3 right choices
= 6 subarrays containing a[1]
```

## 3.5 Step-by-Step Derivation

```text
Step 1:
Fix index i.

Step 2:
Choose L from [0 ... i].

Number of choices:
i + 1

Step 3:
Choose R from [i ... n-1].

Number of choices:
n - i

Step 4:
Every L choice can pair with every R choice.

frequency(i)
= (i+1)(n-i)

Step 5:
contribution(i)
= a[i] × (i+1) × (n-i)

Step 6:
sum over all i.
```

Final formula:

```text
answer = Σ a[i](i+1)(n-i)
```

## 3.6 Dry Run — `[3,5,2]`

```text
i = 0
left  = 1
right = 3
freq  = 3
contribution = 3×3 = 9

i = 1
left  = 2
right = 2
freq  = 4
contribution = 5×4 = 20

i = 2
left  = 3
right = 1
freq  = 3
contribution = 2×3 = 6
```

Therefore:

```text
answer = 9 + 20 + 6
       = 35
```

For `5`, the four containing subarrays are:

```text
[5]
[3,5]
[5,2]
[3,5,2]
```

## 3.7 Time Complexity

```text
One loop over all indices.

Time  = O(n)
Space = O(1)
```

## 3.8 C++17

```cpp
long long subarraySum(vector<long long>& a) {
    long long n = a.size();
    long long ans = 0;

    for (long long i = 0; i < n; i++) {
        long long leftChoices = i + 1;
        long long rightChoices = n - i;

        ans += a[i] * leftChoices * rightChoices;
    }

    return ans;
}
```

## 3.9 Recognition Signal

If the question says:

```text
sum over all subarrays
```

immediately ask:

```text
How many subarrays contain index i?
```

That count is very often:

```text
(i+1)(n-i)
```

---

# 4. Pivot / Ending-Index Contribution

Sometimes fixing only the element is not enough.

A very common alternative is:

```text
group all structures by where they end
```

## 4.1 Example

For:

```text
a = [3,5,2]
```

Group subarrays by ending index.

```text
end at 0:
[3]

end at 1:
[5]
[3,5]

end at 2:
[2]
[5,2]
[3,5,2]
```

## 4.2 Core Observation

Every structure ending at `i` is usually either:

```text
1. start fresh at i

OR

2. extend a structure ending at i-1
```

ASCII:

```text
previous ending structures
        |
        v

[..., i-1]

add a[i]
   |
   +----------------------+
   |                      |
start new              extend old
[a[i]]            [old structure + a[i]]
```

## 4.3 Why This Matters

This produces recurrences such as:

```text
current = new structure contribution
        + extended previous contribution
```

This is especially useful for:

```text
sum of products of all subarrays
sum of some ending-at-i score
subarray DP
prefix-style DP
```

---

# 5. Sum of Products of All Subarrays

## 5.1 Problem

For every subarray, compute its product and add all products.

Example:

```text
a = [3,5,2]
```

Subarrays:

```text
[3]       → 3
[5]       → 5
[2]       → 2
[3,5]     → 15
[5,2]     → 10
[3,5,2]   → 30

answer = 65
```

## 5.2 Brute Force

Enumerate all subarrays and multiply each one.

Too slow for large `n`.

## 5.3 Decide the Form

This is a natural:

```text
ending-at-i form
```

Define:

```text
prev =
sum of products of all subarrays ending at i-1
```

At current value `x = a[i]`, every subarray ending at `i` is:

```text
[x]

or

(previous ending subarray) + x
```

## 5.4 ASCII Transition

Suppose previous ending subarrays are:

```text
[A]
[B]
[C]
```

Their product-sum is:

```text
prev = product(A) + product(B) + product(C)
```

Append `x`:

```text
[A,x] → product(A) × x
[B,x] → product(B) × x
[C,x] → product(C) × x
```

Plus new one:

```text
[x] → x
```

Therefore:

```text
current
= x + x×prev

= x(1 + prev)
```

## 5.5 Step-by-Step Derivation

```text
Step 1:
Group subarrays by ending index.

Step 2:
At index i, start one new subarray:
[a[i]]

Contribution:
a[i]

Step 3:
Every subarray ending at i-1 can be extended by a[i].

If their product-sum is prev:

extended contribution
= a[i] × prev

Step 4:
current
= a[i] + a[i]×prev

Step 5:
Add current to global answer.

Step 6:
prev = current
```

## 5.6 Dry Run — `[3,5,2]`

### i = 0

```text
x = 3

current
= 3 + 3×0
= 3

represents:
[3]

ans = 3
prev = 3
```

### i = 1

```text
x = 5

current
= 5 + 5×3
= 20
```

Represents:

```text
[5]   → 5
[3,5] → 15

total = 20
```

Now:

```text
ans = 3 + 20 = 23
prev = 20
```

### i = 2

```text
x = 2

current
= 2 + 2×20
= 42
```

Represents:

```text
[2]     → 2
[5,2]   → 10
[3,5,2] → 30

total = 42
```

Therefore:

```text
ans = 23 + 42
    = 65
```

## 5.7 Time Complexity

```text
Time  = O(n)
Space = O(1)
```

## 5.8 C++17

```cpp
long long subarrayProductSum(vector<long long>& a) {
    long long ans = 0;
    long long prev = 0;

    for (long long x : a) {
        long long current = x + x * prev;

        ans += current;
        prev = current;
    }

    return ans;
}
```

## 5.9 Recognition Signal

Look for:

```text
all subarrays
operation can be extended when a new element is appended
group by ending position
```

Mental question:

```text
Can I summarize all subarrays ending at i-1
and extend them using a[i]?
```

---

# 6. CF 276C — Little Girl and Maximum Sum

Problem: https://codeforces.com/problemset/problem/276/C

## 6.1 Prerequisites

You need:

```text
1. Contribution technique
2. Difference array
3. Prefix sum
4. Sorting
5. Rearrangement idea:
   large value should get large frequency
```

## 6.2 What Is the Problem Asking?

You have:

```text
array a[]
many queries [L,R]
```

Each query adds:

```text
sum(a[L...R])
```

You may reorder the array once.

Goal:

```text
maximize total sum over all queries
```

## 6.3 Brute Force Thinking

For every query:

```text
iterate from L to R
add all values
```

But if there are many long ranges, this repeats the same positions many times.

Better question:

```text
How many queries contain position i?
```

## 6.4 Contribution Model

Fix position `i`.

Let:

```text
freq[i] =
number of queries containing position i
```

If value `x` is placed at this position:

```text
contribution = x × freq[i]
```

Therefore:

```text
answer = Σ value[i] × freq[i]
```

Now the problem becomes:

```text
How should values be assigned to frequencies?
```

## 6.5 ASCII Example

```text
positions:   1   2   3   4   5
             |   |   |   |   |

query 1:    [---------]
query 2:        [---------]
query 3:        [-------------]

frequency:
             1   3   3   2   1
```

A high-frequency position is used more often.

So:

```text
large value should be placed at large frequency
```

## 6.6 Why Sort Both?

Suppose:

```text
x <= y
p <= q
```

Compare two pairings.

Large-with-large:

```text
xp + yq
```

Crossed:

```text
xq + yp
```

Difference:

```text
(xp + yq) - (xq + yp)

= xp + yq - xq - yp

= y(q-p) - x(q-p)

= (y-x)(q-p)
```

Since:

```text
y-x >= 0
q-p >= 0
```

we get:

```text
(y-x)(q-p) >= 0
```

Therefore:

```text
xp + yq >= xq + yp
```

So:

```text
small ↔ small
large ↔ large
```

is optimal.

## 6.7 Build Frequency with Difference Array

For query `[L,R]`:

```text
diff[L] += 1

if R+1 exists:
    diff[R+1] -= 1
```

Then prefix-sum:

```text
freq[i] = freq[i-1] + diff[i]
```

## 6.8 Dry Run

```text
a = [5,3,2]

queries:
[1,2]
[2,3]
[1,3]
```

Frequency:

```text
position 1 → 2 queries
position 2 → 3 queries
position 3 → 2 queries

freq = [2,3,2]
```

Sort:

```text
a    = [2,3,5]
freq = [2,2,3]
```

Multiply:

```text
2×2 = 4
3×2 = 6
5×3 = 15

answer = 25
```

## 6.9 Time Complexity

```text
Read queries / diff updates → O(q)
Build frequencies          → O(n)
Sort values                → O(n log n)
Sort frequencies           → O(n log n)

Total:
O(q + n log n)
```

## 6.10 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, q;
    cin >> n >> q;

    vector<long long> a(n), diff(n + 1), freq(n);

    for (long long& x : a) {
        cin >> x;
    }

    while (q--) {
        int l, r;
        cin >> l >> r;

        --l;
        --r;

        diff[l]++;

        if (r + 1 < n) {
            diff[r + 1]--;
        }
    }

    long long running = 0;

    for (int i = 0; i < n; i++) {
        running += diff[i];
        freq[i] = running;
    }

    sort(a.begin(), a.end());
    sort(freq.begin(), freq.end());

    long long ans = 0;

    for (int i = 0; i < n; i++) {
        ans += a[i] * freq[i];
    }

    cout << ans << '\n';
}
```

## 6.11 Recognition Signal

When you see:

```text
many ranges
same position participates many times
reordering/permutation is allowed
maximize total contribution
```

think:

```text
1. count frequency of every position
2. sort frequencies
3. sort values
4. pair large with large
```

---

# 7. LC 907 — Sum of Subarray Minimums

Problem: https://leetcode.com/problems/sum-of-subarray-minimums/

## 7.1 Prerequisites

You need:

```text
1. Contribution technique
2. Subarray boundaries
3. Previous smaller element
4. Next smaller / next smaller-or-equal
5. Monotonic stack
6. Duplicate-value tie handling
7. Modulo arithmetic
```

## 7.2 What Is the Problem Asking?

For every subarray:

```text
find its minimum
```

Then add all those minimums.

Brute force would enumerate many subarrays.

Instead:

```text
fix a[i]
```

and ask:

```text
For how many subarrays is a[i] the chosen minimum?
```

## 7.3 Boundary Idea

Suppose:

```text
            a[i]
              |
              v

... smaller ... [ valid >= region ] ... smaller/equal ...
```

We can extend left until a blocking smaller element.

We can extend right until a blocking smaller/equal element.

That gives:

```text
leftChoices
rightChoices
```

Then:

```text
frequency(i)
= leftChoices × rightChoices
```

## 7.4 ASCII Visual

Example:

```text
a = [3, 1, 2, 4]
         ^
         i = 1
```

For value `1`:

```text
left boundary:
none smaller before it

right boundary:
none smaller/equal after it

indices:
0   1   2   3
3   1   2   4
    ^
```

Possible starts:

```text
0 or 1

leftChoices = 2
```

Possible ends:

```text
1, 2, 3

rightChoices = 3
```

So:

```text
frequency of 1 as minimum
= 2 × 3
= 6
```

The six subarrays are:

```text
[1]
[1,2]
[1,2,4]
[3,1]
[3,1,2]
[3,1,2,4]
```

Contribution:

```text
1 × 6 = 6
```

## 7.5 General Formula

Let:

```text
previousSmaller = index of previous strictly smaller element

nextSmallerOrEqual =
index of next smaller-or-equal element
```

Then:

```text
left  = i - previousSmaller
right = nextSmallerOrEqual - i
```

Therefore:

```text
frequency(i) = left × right

contribution(i)
= a[i] × left × right
```

## 7.6 Why Strict on One Side and Non-Strict on the Other?

Duplicates create ambiguity.

Example:

```text
[2,2]
```

If both `2`s are allowed to own the same subarray `[2,2]`, we double-count.

So choose one consistent ownership rule.

Example:

```text
left side  → previous strictly smaller
right side → next smaller or equal
```

Equivalent reverse conventions also work if used consistently.

## 7.7 Duplicate ASCII Example

```text
indices: 0   1
values:  2   2
         A   B
```

Subarrays:

```text
[2 at A]
[2 at B]
[2,2]
```

We need `[2,2]` to belong to only one occurrence.

For example:

```text
A owns [A]
B owns [B] and [A,B]
```

or the reverse.

The strict/non-strict rule creates exactly one owner.

## 7.8 Dry Run — `[3,1,2]`

### Value 3

```text
left  = 1
right = 1

contribution
= 3×1×1
= 3
```

### Value 1

```text
left  = 2
right = 2

contribution
= 1×2×2
= 4
```

### Value 2

```text
left  = 1
right = 1

contribution
= 2×1×1
= 2
```

Total:

```text
answer = 3 + 4 + 2
       = 9
```

## 7.9 Time Complexity

Each index is pushed and popped from the stack at most once per pass.

```text
Time  = O(n)
Space = O(n)
```

## 7.10 C++17

```cpp
class Solution {
public:
    int sumSubarrayMins(vector<int>& a) {
        const long long MOD = 1e9 + 7;
        int n = a.size();

        vector<long long> left(n), right(n);
        stack<int> st;

        for (int i = 0; i < n; i++) {
            while (!st.empty() && a[st.top()] >= a[i]) {
                st.pop();
            }

            left[i] = st.empty() ? i + 1 : i - st.top();
            st.push(i);
        }

        while (!st.empty()) {
            st.pop();
        }

        for (int i = n - 1; i >= 0; i--) {
            while (!st.empty() && a[st.top()] > a[i]) {
                st.pop();
            }

            right[i] = st.empty() ? n - i : st.top() - i;
            st.push(i);
        }

        long long ans = 0;

        for (int i = 0; i < n; i++) {
            long long contribution =
                1LL * a[i] * left[i] % MOD * right[i] % MOD;

            ans = (ans + contribution) % MOD;
        }

        return ans;
    }
};
```

## 7.11 Recognition Signal

When the problem says:

```text
sum of subarray minimums
sum of subarray maximums
sum of ranges
each element acts as minimum / maximum
```

think:

```text
fix a[i]
    ↓
find valid left boundary
find valid right boundary
    ↓
leftChoices × rightChoices
    ↓
value × frequency
```

Usually this leads to a monotonic stack.

---

# 8. Recognition Guide

Use this as the contest-time decision flow.

```text
Problem asks aggregate over many structures
                |
                v
Can I fix one element / position / pivot?
                |
          +-----+-----+
          |           |
         yes          no
          |           |
          v           v
Count how many      Try another
structures use it  DP / prefix /
          |         combinatorics form
          v
Does frequency depend only on index?
          |
     +----+----+
     |         |
    yes        no
     |         |
     v         v
derive direct   Need extra structure:
formula         diff array / stack /
                DP / binary search
```

## 8.1 Pattern Map

### Pattern A — Same Frequency for Every Element

Example:

```text
sum of all subset sums
```

Formula:

```text
a[i] × 2^(n-1)
```

### Pattern B — Left Choices × Right Choices

Example:

```text
sum of all subarray sums
```

Formula:

```text
a[i] × (i+1) × (n-i)
```

### Pattern C — Ending-at-i DP

Example:

```text
sum of products of all subarrays
```

Transition:

```text
current = startFresh + extendPrevious
```

### Pattern D — Query Frequency

Example:

```text
CF 276C
```

Tools:

```text
difference array
prefix sum
sorting
```

### Pattern E — Boundary Contribution

Example:

```text
LC 907
```

Tools:

```text
previous smaller
next smaller/equal
monotonic stack
```

Formula:

```text
value × leftChoices × rightChoices
```

---

# 9. Compact Formula Summary

## Subset Sum

```text
frequency(i)
= 2^(n-1)

contribution(i)
= a[i] × 2^(n-1)
```

## Subarray Sum

```text
leftChoices  = i + 1
rightChoices = n - i

contribution(i)
= a[i] × (i+1) × (n-i)
```

## Pivot / Ending-at-i Product

```text
current
= a[i] + a[i]×previous

answer += current
previous = current
```

## Range Query Contribution

```text
freq[i]
= number of queries containing i

answer
= Σ value[i] × freq[i]
```

For maximum pairing:

```text
sort(values)
sort(freq)
pair corresponding positions
```

## Subarray Minimum

```text
left  = i - previousSmaller
right = nextSmallerOrEqual - i

contribution(i)
= a[i] × left × right
```

---

# Final Mental Model

Whenever you see a huge number of subsets/subarrays/ranges:

```text
DON'T:
enumerate every structure

DO:
fix one object
    ↓
count how many structures use it
    ↓
derive contribution
    ↓
sum all contributions
```

The most important contest question is:

```text
"If I fix this element, how many valid answers contain it?"
```
