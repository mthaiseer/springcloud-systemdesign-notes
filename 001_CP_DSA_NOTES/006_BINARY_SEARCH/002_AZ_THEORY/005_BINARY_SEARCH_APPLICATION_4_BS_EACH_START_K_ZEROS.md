# AlgoZenith — Binary Search Application 4
## Binary Search on Each Start — Subarrays with at Most K Zeros
### TLE-Style Self-Study Notes

> **Source:** supplied AlgoZenith lecture PDF `005_Binary Search Application idea 4.pdf`.
>
> The lecture introduces another Binary Search form:
>
> ```text
> Binary Search forms
> │
> ├── Binary Search on Answer
> │
> └── Binary Search on Each Start
> ```
>
> The main binary-array problem is:
>
> ```text
> Given a binary array and K flips,
> find the largest subarray that can become all 1s.
> ```
>
> Since flipping a `0` to `1` costs one flip:
>
> ```text
> "can become all 1s using <= K flips"
>
> is exactly:
>
> "subarray contains <= K zeros"
> ```
>
> The lecture then tweaks the problem into:
>
> ```text
> Count the number of subarrays
> containing <= K zeros.
> ```
>
> Code below is formatted in the same compact C++17 style used in the earlier notes.

---

# Table of Contents

- [1. Problem Setup](#1-problem-setup)
- [2. First Key Transformation — Flips Become Zero Counts](#2-first-key-transformation--flips-become-zero-counts)
- [3. Prerequisite — Prefix Count of Zeros](#3-prerequisite--prefix-count-of-zeros)
- [4. Form 1 — Binary Search on Answer for Maximum Length](#4-form-1--binary-search-on-answer-for-maximum-length)
- [5. `check(length)` for the Maximum-Length Form](#5-checklength-for-the-maximum-length-form)
- [6. Why the Length Space Is Monotonic](#6-why-the-length-space-is-monotonic)
- [7. Compact Dry Run — Maximum Length](#7-compact-dry-run--maximum-length)
- [8. Sliding Window vs Two Pointers](#8-sliding-window-vs-two-pointers)
- [9. Problem Tweaked — Count All Valid Subarrays](#9-problem-tweaked--count-all-valid-subarrays)
- [10. Why Binary Search on Answer Is Not Enough for Counting](#10-why-binary-search-on-answer-is-not-enough-for-counting)
- [11. Form 2 — Binary Search on Each Start](#11-form-2--binary-search-on-each-start)
- [12. Monotonic Space for One Fixed Start](#12-monotonic-space-for-one-fixed-start)
- [13. Why the Answer Starts at `start - 1`](#13-why-the-answer-starts-at-start---1)
- [14. Counting Valid Subarrays from One Start](#14-counting-valid-subarrays-from-one-start)
- [15. Compact Dry Run — Binary Search on Each Start](#15-compact-dry-run--binary-search-on-each-start)
- [16. Properly Formatted C++17 — Count Subarrays with `<= K` Zeros](#16-properly-formatted-c17--count-subarrays-with--k-zeros)
- [17. Variant — Maximum Length Instead of Count](#17-variant--maximum-length-instead-of-count)
- [18. Complexity](#18-complexity)
- [19. Common Mistakes](#19-common-mistakes)
- [20. Lecture Homework](#20-lecture-homework)
- [21. Recognition Pattern](#21-recognition-pattern)
- [22. Final Mental Model](#22-final-mental-model)

---

# 1. Problem Setup

We have a binary array:

```text
arr[i] ∈ {0,1}
```

Example:

```text
arr = [1,0,1,1,0,1,0,0,1,1]
```

We are allowed:

```text
K flips
```

where one flip can change:

```text
0 → 1
```

The first lecture question is:

```text
What is the maximum-length subarray
that can be made entirely 1
using at most K flips?
```

Example idea:

```text
arr = [1,0,1,1,0,1,0,1]
K = 2
```

We may choose a subarray containing at most:

```text
2 zeros
```

because those are exactly the positions we need to flip.

---

# 2. First Key Transformation — Flips Become Zero Counts

This is the most important modelling step.

Suppose a subarray is:

```text
[1,1,0,1,0,1]
```

Zeros:

```text
2
```

To turn it into:

```text
[1,1,1,1,1,1]
```

we need exactly:

```text
2 flips
```

Therefore:

```text
subarray can become all 1s using <= K flips

⇔

number of zeros in subarray <= K
```

So the story disappears.

The problem becomes:

```text
Find the largest subarray
with at most K zeros.
```

Later, the lecture changes it to:

```text
Count all subarrays
with at most K zeros.
```

---

# 3. Prerequisite — Prefix Count of Zeros

We repeatedly need:

```text
number of zeros in arr[L...R]
```

The lecture converts the binary array so that:

```text
original 0 → 1
original 1 → 0
```

Then the transformed value itself means:

```text
1 = this position is a zero in the original array
```

For example:

```text
original:
[1,0,1,1,0,1]

zero-marker:
[0,1,0,0,1,0]
```

Build prefix sum:

```text
prefix[i]
=
number of original zeros in arr[0...i]
```

Example:

```text
zero-marker = [0,1,0,0,1,0]

prefix      = [0,1,1,1,2,2]
```

Then:

```text
zeros(L,R)
=
prefix[R] - prefix[L-1]
```

when `L > 0`.

For `L = 0`:

```text
zeros(0,R)
=
prefix[R]
```

---

## Teaching Dry Run

Using:

```text
original = [1,0,1,1,0,1]
```

Find zeros in:

```text
L = 1
R = 4
```

Subarray:

```text
[0,1,1,0]
```

Visually:

```text
zeros = 2
```

Prefix:

```text
prefix[4] = 2
prefix[0] = 0
```

So:

```text
zeros(1,4)
=
prefix[4] - prefix[0]
=
2 - 0
=
2
```

This gives:

```text
O(1)
```

zero-count queries.

---

# 4. Form 1 — Binary Search on Answer for Maximum Length

The lecture first asks:

```text
How can we find the maximum-length valid subarray?
```

Binary Search answer:

```text
x = candidate subarray length
```

Define:

```text
F(x)
=
Does there exist at least one
subarray of length x
containing <= K zeros?
```

This creates a monotonic space.

---

# 5. `check(length)` for the Maximum-Length Form

For a fixed length:

```text
x
```

every window has exactly `x` elements.

Number of windows of length `x` in an array of length `N`:

```text
N - x + 1
```

Example:

```text
N = 5
x = 3
```

Windows:

```text
[0..2]
[1..3]
[2..4]
```

Count:

```text
5 - 3 + 1
=
3
```

For each window:

```text
zeros = prefix[R] - prefix[L-1]
```

If any window has:

```text
zeros <= K
```

then:

```text
check(x) = true
```

---

## ASCII View

```text
arr:
[ 1  1  1  0  1  0  1 ]

x = 4

window 1:
[ 1  1  1  0 ] 1 0 1

window 2:
1 [ 1  1  0  1 ] 0 1

window 3:
1 1 [ 1  0  1  0 ] 1

window 4:
1 1 1 [ 0  1  0  1 ]
```

For each fixed-size window:

```text
count zeros
```

and check:

```text
zeros <= K ?
```

---

# 6. Why the Length Space Is Monotonic

Suppose some subarray of length:

```text
x
```

contains at most `K` zeros.

Then we can take a smaller subarray inside it.

Removing elements cannot increase the number of zeros.

Therefore:

```text
if length x is possible,
every smaller length is also possible
```

So the search space is:

```text
length:
0 1 2 3 4 5 6 7 ...

check:
Y Y Y Y Y Y N N ...
          ^
          |
      last YES
```

This is a:

```text
YES ... YES NO ... NO
```

monotonic space.

Therefore for the maximum valid length we search:

```text
last True
```

Binary Search direction:

```cpp
if (check(mid)) {
    answer = mid;
    low = mid + 1;
} else {
    high = mid - 1;
}
```

---

# 7. Compact Dry Run — Maximum Length

**Teaching Dry Run**

```text
arr = [1,0,1,1,0,1,1]
K = 1
```

Try:

```text
length = 5
```

Windows:

```text
[1,0,1,1,0] → 2 zeros → invalid
[0,1,1,0,1] → 2 zeros → invalid
[1,1,0,1,1] → 1 zero  → valid
```

Therefore:

```text
check(5) = true
```

Try:

```text
length = 6
```

```text
[1,0,1,1,0,1] → 2 zeros
[0,1,1,0,1,1] → 2 zeros
```

Therefore:

```text
check(6) = false
```

Transition:

```text
5 → YES
6 → NO
```

So maximum valid length is:

```text
5
```

---

# 8. Sliding Window vs Two Pointers

The lecture explicitly distinguishes the two ideas.

## Sliding Window

Use when:

```text
window size is fixed
```

Example:

```text
"Does any length-x window work?"
```

Here `x` is fixed during one call to `check(x)`.

So we can slide:

```text
[L...R]
```

while keeping:

```text
R-L+1 = x
```

---

## Two Pointers

Use when:

```text
window size changes
```

Example:

```text
for each left boundary,
extend right boundary as far as allowed
```

That is a variable-size window.

The lecture later uses Binary Search per start to find that farthest valid right endpoint.

---

# 9. Problem Tweaked — Count All Valid Subarrays

The lecture then changes the question.

Instead of:

```text
maximum length
```

ask:

```text
How many subarrays can be made all 1s
using <= K flips?
```

Using the same transformation:

```text
count subarrays containing <= K zeros
```

Example from the lecture idea:

```text
K = 1
```

We must count every subarray whose zero count is:

```text
0 or 1
```

This is no longer asking for one optimum value.

It is asking for:

```text
a total count
```

---

# 10. Why Binary Search on Answer Is Not Enough for Counting

The lecture makes an important distinction:

```text
Binary Search on Answer
works naturally for:

maximize the minimum
minimize the maximum
find a boundary value
```

But for:

```text
count all valid subarrays
```

there is no single final answer value `x` whose feasibility directly gives the count.

So we need to look at another property.

The lecture switches to:

```text
Binary Search on each start
```

---

# 11. Form 2 — Binary Search on Each Start

Fix the left endpoint:

```text
start
```

Now ask:

```text
What is the farthest end
such that arr[start...end]
contains <= K zeros?
```

For one fixed start:

```text
end = start
end = start+1
end = start+2
...
```

As `end` moves right, the number of zeros can only:

```text
stay the same
or
increase
```

So feasibility is monotonic.

This gives Binary Search on:

```text
end
```

for every fixed:

```text
start
```

---

# 12. Monotonic Space for One Fixed Start

Suppose:

```text
K = 2
```

and for:

```text
start = 0
```

the farthest valid endpoint is `5`.

Then:

```text
end:
0 1 2 3 4 5 6 7

valid:
Y Y Y Y Y Y N N
          ^
          |
     farthest valid
```

Why?

If:

```text
[start...5]
```

has at most 2 zeros, then every shorter prefix:

```text
[start...0]
[start...1]
...
[start...4]
```

also has at most 2 zeros.

Once we include too many zeros:

```text
> K
```

moving farther right cannot reduce the zero count.

So for a fixed start:

```text
YES YES YES NO NO NO
```

We want:

```text
rightmost YES
```

---

# 13. Why the Answer Starts at `start - 1`

This is a subtle but important lecture point.

Suppose:

```text
K = 0
```

and:

```text
arr[start] = 0
```

Then even the one-element subarray:

```text
[start...start]
```

is invalid.

So there may be:

```text
zero valid endings
```

for this start.

If we initialized:

```cpp
answer = start;
```

then we would incorrectly count one subarray.

Instead initialize:

```cpp
answer = start - 1;
```

Meaning:

```text
"No valid endpoint has been found yet."
```

If Binary Search never finds a valid `end`, `answer` stays:

```text
start - 1
```

and contributes:

```text
0
```

subarrays.

---

## Tiny Dry Run

```text
arr = [0,1,0,0,1]
K = 0
start = 0
```

No subarray starting at index `0` is valid because:

```text
arr[0] = 0
```

So:

```text
answer = start - 1
       = -1
```

Contribution:

```text
answer - start + 1
=
-1 - 0 + 1
=
0
```

Correct.

---

# 14. Counting Valid Subarrays from One Start

Suppose for some fixed `start`:

```text
farthest valid end = answer
```

Then every endpoint:

```text
start
start+1
...
answer
```

is valid.

How many endpoints?

```text
answer - start + 1
```

The lecture also writes it as:

```text
answer - (start - 1)
```

Same expression:

```text
answer - start + 1
```

Therefore:

```text
totalCount += answer - start + 1
```

---

## ASCII Example

Suppose:

```text
start = 2
answer = 5
```

Valid subarrays:

```text
[2..2]
[2..3]
[2..4]
[2..5]
```

Count:

```text
5 - 2 + 1
=
4
```

ASCII:

```text
index:   2   3   4   5
         |---|
         |-------|
         |-----------|
         |---------------|

total = 4
```

---

# 15. Compact Dry Run — Binary Search on Each Start

**Teaching Dry Run**

```text
arr = [1,0,1,0,1,1]
K = 1
```

Fix:

```text
start = 1
```

Subarrays beginning at `1`:

```text
[1..1] = [0]           → 1 zero  → valid
[1..2] = [0,1]         → 1 zero  → valid
[1..3] = [0,1,0]       → 2 zeros → invalid
[1..4]                 → >=2     → invalid
[1..5]                 → >=2     → invalid
```

Monotonic endpoint space:

```text
end:     1 2 3 4 5
valid:   Y Y N N N
           ^
           |
      rightmost YES = 2
```

Binary Search:

```text
low = 1
high = 5
answer = 0
```

Compact:

```text
Iter 1: low=1, high=5 → mid=3 → zeros(1,3)=2 >1 → NO  → high=2
Iter 2: low=1, high=2 → mid=1 → zeros(1,1)=1 <=1 → YES → ans=1 → low=2
Iter 3: low=2, high=2 → mid=2 → zeros(1,2)=1 <=1 → YES → ans=2 → low=3
```

Farthest valid endpoint:

```text
answer = 2
```

Number of valid subarrays starting at `1`:

```text
2 - 1 + 1
=
2
```

They are:

```text
[1..1]
[1..2]
```

---

# 16. Properly Formatted C++17 — Count Subarrays with `<= K` Zeros

The lecture implementation below is reformatted into the compact C++ style used in the earlier notes.

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

int n, k;
vector<int> zeroPrefix;

int zerosInRange(int left, int right) {
    if (left == 0) {
        return zeroPrefix[right];
    }

    return zeroPrefix[right] - zeroPrefix[left - 1];
}

bool check(int start, int end) {
    return zerosInRange(start, end) <= k;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> n >> k;

    vector<int> arr(n);
    zeroPrefix.resize(n);

    for (int i = 0; i < n; i++) {
        cin >> arr[i];

        int isZero = (arr[i] == 0);

        zeroPrefix[i] = isZero;

        if (i > 0) {
            zeroPrefix[i] += zeroPrefix[i - 1];
        }
    }

    ll totalSubarrays = 0;

    for (int start = 0; start < n; start++) {
        int low = start;
        int high = n - 1;
        int answer = start - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;

            if (check(start, mid)) {
                answer = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        totalSubarrays += answer - start + 1;
    }

    cout << totalSubarrays << '\n';

    return 0;
}
```

---

## Core Binary Search Block

This is the pattern to recognize:

```cpp
int low = start;
int high = n - 1;
int answer = start - 1;

while (low <= high) {
    int mid = low + (high - low) / 2;

    if (check(start, mid)) {
        answer = mid;
        low = mid + 1;
    } else {
        high = mid - 1;
    }
}
```

Read it as:

```text
For this fixed start:

find the rightmost end
such that zeros(start,end) <= K.
```

Then count:

```cpp
totalSubarrays += answer - start + 1;
```

---

# 17. Variant — Maximum Length Instead of Count

The same per-start framework can also recover maximum length.

For every `start`:

```text
find farthest valid end
```

Then:

```text
length = end - start + 1
```

Keep:

```text
maximum length
```

Instead of:

```text
total += length
```

we would use:

```cpp
maximumLength = max(
    maximumLength,
    answer - start + 1
);
```

The lecture's first formulation uses Binary Search on length, but this shows the connection between the two viewpoints.

---

# 18. Complexity

## Method 1 — Binary Search on Length

Binary Search over:

```text
length ∈ [0,N]
```

takes:

```text
O(log N)
```

For one length:

```text
N - length + 1
```

windows are checked.

Each zero count uses prefix sums:

```text
O(1)
```

So one checker is:

```text
O(N)
```

Total:

```text
O(N log N)
```

---

## Method 2 — Binary Search on Each Start

There are:

```text
N
```

possible starts.

For each start:

```text
Binary Search endpoint
→ O(log N)
```

Each feasibility check:

```text
zeros(start,end)
→ O(1)
```

using prefix sums.

Total:

```text
O(N log N)
```

---

## Why the Final Count Must Be `long long`

Maximum number of subarrays in an `N`-element array:

```text
N(N+1) / 2
```

For:

```text
N = 100000
```

approximately:

```text
100000 * 100001 / 2
≈ 5 * 10^9
```

This exceeds signed 32-bit `int`.

Therefore:

```cpp
long long totalSubarrays;
```

is required.

This overflow warning is explicitly highlighted in the lecture.

---

# 19. Common Mistakes

## Mistake 1 — Thinking in Terms of Flips Instead of Zero Counts

Immediately convert:

```text
<= K flips
```

into:

```text
<= K zeros
```

This makes the feasibility condition measurable.

---

## Mistake 2 — Wrong Monotonic Direction for Maximum Length

For length:

```text
YES YES YES NO NO
```

We want:

```text
last YES
```

So when `mid` works:

```cpp
answer = mid;
low = mid + 1;
```

---

## Mistake 3 — Wrong Monotonic Direction for Endpoints

For fixed `start`:

```text
valid valid valid invalid invalid
```

Again:

```text
rightmost valid
```

So when endpoint `mid` works:

```cpp
answer = mid;
low = mid + 1;
```

---

## Mistake 4 — Initialize `answer = start`

This fails when no valid endpoint exists.

Correct sentinel:

```cpp
int answer = start - 1;
```

Then:

```text
answer - start + 1 = 0
```

when nothing works.

---

## Mistake 5 — Off-by-One in Count

If valid endpoints are:

```text
start ... answer
```

count is:

```text
answer - start + 1
```

not:

```text
answer - start
```

---

## Mistake 6 — Count Zeros in O(N) Inside Each Binary Search Step

That would make the solution too slow.

Use prefix sums:

```text
zeros(L,R)
→ O(1)
```

---

## Mistake 7 — Use `int` for Total Subarrays

At:

```text
N = 10^5
```

the total can be around:

```text
5 * 10^9
```

Use:

```cpp
long long
```

---

## Mistake 8 — Confusing Fixed Window with Variable Window

Lecture distinction:

```text
Sliding Window
→ fixed window size

Two Pointers
→ variable window size
```

During `check(length)`:

```text
length is fixed
```

so fixed-size window scanning is natural.

For each-start farthest-end logic:

```text
window size varies
```

which is closer to the two-pointer viewpoint.

---

# 20. Lecture Homework

The final lecture page gives the next extension:

```text
Find the number of subarrays
with number of distinct elements <= K.
```

Constraints shown:

```text
N <= 10^5
K <= N
```

The lecture presents this as homework after solving:

```text
count subarrays with <= K zeros
```

The intended conceptual transfer is:

```text
fixed start
→ find farthest valid end
→ count all endings up to it
```

but the supplied page does not provide the full implementation for the distinct-element version, so this note leaves it as the lecture exercise.

---

# 21. Recognition Pattern

## Form A — Binary Search on Answer

Problem asks:

```text
maximum possible length
```

and you can define:

```text
check(length)
=
Does any subarray of this length satisfy the condition?
```

If:

```text
smaller lengths stay feasible
```

you get:

```text
YES YES YES NO NO
```

and search:

```text
last YES
```

---

## Form B — Binary Search on Each Start

Problem asks:

```text
count all valid subarrays
```

and for a fixed `start`:

```text
validity as end moves right
```

is monotonic.

Then:

```text
for each start:
    binary-search farthest valid end

    add:
    end - start + 1
```

This is the lecture's key application form.

---

# 22. Final Mental Model

```text
Binary array
+
<= K flips
        |
        v
convert to:
<= K zeros
        |
        v
prefix count of zeros
        |
        +-----------------------------+
        |                             |
        v                             v
MAX LENGTH                      COUNT ALL SUBARRAYS
        |                             |
        v                             v
BS on length                    fix each start
        |                             |
        v                             v
check if ANY                    BS farthest end
length-x window                 with <= K zeros
has <= K zeros                        |
        |                             v
        v                       valid endings count
YES YES YES NO                  = end-start+1
        |                             |
        v                             v
last YES                         sum over starts
```

The core recognition sentence is:

```text
If validity becomes monotonic
as an endpoint moves,
Binary Search can find the boundary.
```

For this lecture:

```text
fixed start
+
end moves right
+
zero count never decreases
→ YES YES YES NO NO
→ find rightmost YES
```

And the important counting formula is:

```text
valid subarrays starting at `start`
=
farthestEnd - start + 1
```
