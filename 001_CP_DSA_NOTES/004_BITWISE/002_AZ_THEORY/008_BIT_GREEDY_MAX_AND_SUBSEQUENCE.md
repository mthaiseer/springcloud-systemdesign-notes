# Bit Greedy — Maximum AND of a Subsequence
## Highest Bit to Lowest Bit — TLE-Style Self-Study Notes

---

# Table of Contents

- [1. Problem](#1-problem)
- [2. What Result Are We Trying to Maximize?](#2-what-result-are-we-trying-to-maximize)
- [3. Prerequisite 1 — Bitwise AND](#3-prerequisite-1--bitwise-and)
- [4. Prerequisite 2 — When Does a Bit Survive in the AND?](#4-prerequisite-2--when-does-a-bit-survive-in-the-and)
- [5. Prerequisite 3 — Why Check Higher Bits First?](#5-prerequisite-3--why-check-higher-bits-first)
- [6. Convert the Problem into a Feasibility Question](#6-convert-the-problem-into-a-feasibility-question)
- [7. The Greedy Decision](#7-the-greedy-decision)
- [8. Small Example First](#8-small-example-first)
- [9. Full Step-by-Step Dry Run](#9-full-step-by-step-dry-run)
- [10. Why the Greedy Is Correct](#10-why-the-greedy-is-correct)
- [11. Clean C++17 — Preferred Template](#11-clean-c17--preferred-template)
- [12. Candidate-Filtering Version](#12-candidate-filtering-version)
- [13. Complexity](#13-complexity)
- [14. Common Mistakes](#14-common-mistakes)
- [15. Recognition Pattern](#15-recognition-pattern)
- [16. Final Mental Model](#16-final-mental-model)

---

# 1. Problem

You are given:

- an array `A` of `N` positive integers,
- an integer `X`.

Choose a subsequence of exactly `X` elements.

For the chosen elements:

```text
b1, b2, ..., bX
```

compute:

```text
b1 & b2 & ... & bX
```

where `&` is bitwise AND.

We need to maximize this AND value.

---

## Important Interpretation

A subsequence chooses indices from the array.

For example:

```text
A = [5, 2, 7, 6]
X = 2
```

Possible choices include:

```text
[5,2]
[5,7]
[5,6]
[2,7]
[2,6]
[7,6]
```

For this problem, once the `X` elements are selected, their original order does not affect the AND result.

So mentally we can think:

```text
Choose exactly X elements
whose bitwise AND is as large as possible.
```

---

# 2. What Result Are We Trying to Maximize?

Suppose the selected elements are:

```text
a1, a2, ..., aX
```

The answer is:

```text
answer = a1 & a2 & ... & aX
```

A bit will be `1` in `answer` only when that bit is `1` in **every selected element**.

This is the central observation.

Before solving the full problem, we need to understand that property clearly.

---

# 3. Prerequisite 1 — Bitwise AND

Truth table for one bit:

| `a` | `b` | `a & b` |
|---:|---:|---:|
| 0 | 0 | 0 |
| 0 | 1 | 0 |
| 1 | 0 | 0 |
| 1 | 1 | 1 |

So:

```text
AND gives 1
only when BOTH bits are 1.
```

For many numbers:

```text
a1 & a2 & a3 & ... & ak
```

a bit is `1` only when:

```text
every one of a1, a2, ..., ak
has that bit = 1
```

---

## Tiny Example

Take:

```text
7 = 111
6 = 110
5 = 101
```

Now:

```text
7 & 6
=
111
110
---
110
=
6
```

But:

```text
7 & 6 & 5
=
111
110
101
---
100
=
4
```

Why did bit `1` disappear?

Because:

```text
5 = 101
```

has bit `1 = 0`.

One zero is enough to kill a bit in AND.

---

# 4. Prerequisite 2 — When Does a Bit Survive in the AND?

Suppose we want bit `j` to be present in the final answer.

Then every one of the `X` selected numbers must have bit `j = 1`.

Therefore:

```text
at least X array elements
must have bit j = 1
```

Otherwise it is impossible to choose `X` elements whose AND contains that bit.

---

## Example

Suppose:

```text
A = [7,5,6]
```

Binary:

```text
7 = 111
5 = 101
6 = 110
```

Look column-wise:

```text
        bit2 bit1 bit0

7         1    1    1
5         1    0    1
6         1    1    0
```

If:

```text
X = 2
```

then:

```text
bit2 appears in 3 numbers
bit1 appears in 2 numbers
bit0 appears in 2 numbers
```

At first sight, all three bits individually look possible.

But we cannot decide each bit independently.

Why?

Because the **same X elements** must support all chosen answer bits.

For example:

```text
bit2 + bit1
```

is supported by:

```text
7 = 111
6 = 110
```

But:

```text
bit2 + bit1 + bit0
```

would require two numbers containing:

```text
111
```

Only `7` does.

So after accepting one bit, future bits must be checked together with the already accepted mask.

That leads to the key feasibility test.

---

# 5. Prerequisite 3 — Why Check Higher Bits First?

Binary numbers are ordered by their highest differing bit.

Example:

```text
1000₂ = 8
0111₂ = 7
```

Even though:

```text
0111
```

has three set bits, it is still smaller than:

```text
1000
```

because the highest bit dominates all lower bits.

In general:

```text
2^j > 2^0 + 2^1 + ... + 2^(j-1)
```

because:

```text
2^0 + 2^1 + ... + 2^(j-1)
=
2^j - 1
```

Therefore:

```text
one valid higher bit
is more valuable than
all lower bits combined.
```

So when maximizing an integer mask, the natural greedy order is:

```text
highest bit
↓
...
↓
lowest bit
```

At each bit we ask:

> Can the answer keep this bit as `1` without making it impossible to choose `X` elements?

If yes, we must keep it.

---

# 6. Convert the Problem into a Feasibility Question

Suppose we have already built some bits of the answer:

```text
answer
```

Now we are considering bit `j`.

Try:

```text
candidate = answer | (1LL << j)
```

We want to know:

> Are there at least `X` array elements containing **every bit** present in `candidate`?

For one element `value`, this condition is:

```cpp
(value & candidate) == candidate
```

Why?

Because if all bits required by `candidate` are also present in `value`, ANDing them preserves the whole candidate:

```text
value & candidate = candidate
```

---

## Tiny Example

Suppose:

```text
candidate = 110₂
```

Check:

```text
7 = 111
```

Then:

```text
111 & 110 = 110
```

So `7` supports this candidate.

Check:

```text
5 = 101
```

Then:

```text
101 & 110 = 100
```

Not equal to `110`.

So `5` does not support the candidate.

---

## Feasibility Rule

Count:

```text
how many A[i] satisfy:

(A[i] & candidate) == candidate
```

If:

```text
count >= X
```

then we can choose `X` such elements, and all bits in `candidate` will survive their AND.

Therefore:

```text
candidate is feasible
```

Otherwise:

```text
candidate is impossible
```

---

# 7. The Greedy Decision

Initialize:

```text
answer = 0
```

Process bits from high to low.

For each `bit`:

```text
candidate = answer | (1LL << bit)
```

Count elements that contain all candidate bits.

Then:

```text
if count >= X:
    answer = candidate
else:
    skip this bit
```

That is the entire pattern.

---

## Generic Template

```text
answer = 0

for bit from highest to lowest:

    candidate = answer with this bit turned on

    count = number of array values
            that contain every bit in candidate

    if count >= X:
        answer = candidate
```

The key is:

```text
we never test the new bit alone
```

We test:

```text
already accepted bits
+
new bit
```

together.

---

# 8. Small Example First

Use the lecture-style example:

```text
A = [7,5,6]
X = 2
```

Binary:

```text
7 = 111
5 = 101
6 = 110
```

We process bits from high to low.

---

## Try Bit 2

```text
candidate = 100
```

Supporting elements:

```text
7 = 111 → supports 100
5 = 101 → supports 100
6 = 110 → supports 100
```

Count:

```text
3 >= 2
```

So keep bit 2:

```text
answer = 100
```

---

## Try Bit 1

Now do **not** test `010` alone.

Test:

```text
answer | 010
=
100 | 010
=
110
```

Supporting elements:

```text
7 = 111 → yes
5 = 101 → no
6 = 110 → yes
```

Count:

```text
2 >= 2
```

So keep bit 1:

```text
answer = 110
```

---

## Try Bit 0

Now test:

```text
answer | 001
=
110 | 001
=
111
```

Supporting elements:

```text
7 = 111 → yes
5 = 101 → no
6 = 110 → no
```

Count:

```text
1 < 2
```

So bit 0 cannot be kept.

Final:

```text
answer = 110₂ = 6
```

Indeed:

```text
7 & 6 = 6
```

So the maximum AND of a subsequence of size `2` is:

```text
6
```

---

# 9. Full Step-by-Step Dry Run

Take a slightly larger teaching example:

```text
A = [15, 14, 12, 10, 7]
X = 3
```

Binary:

```text
15 = 1111
14 = 1110
12 = 1100
10 = 1010
 7 = 0111
```

Start:

```text
answer = 0000
```

---

## Bit 3

Try:

```text
candidate = 1000
```

Check all numbers:

```text
15: 1111 contains 1000 → yes
14: 1110 contains 1000 → yes
12: 1100 contains 1000 → yes
10: 1010 contains 1000 → yes
 7: 0111 contains 1000 → no
```

Count:

```text
4 >= 3
```

Keep it:

```text
answer = 1000
```

---

## Bit 2

Try:

```text
candidate
=
1000 | 0100
=
1100
```

Check:

```text
15: 1111 contains 1100 → yes
14: 1110 contains 1100 → yes
12: 1100 contains 1100 → yes
10: 1010 contains 1100 → no
 7: 0111 contains 1100 → no
```

Count:

```text
3 >= 3
```

Keep it:

```text
answer = 1100
```

---

## Bit 1

Try:

```text
candidate
=
1100 | 0010
=
1110
```

Check:

```text
15: 1111 contains 1110 → yes
14: 1110 contains 1110 → yes
12: 1100 contains 1110 → no
10: 1010 contains 1110 → no
 7: 0111 contains 1110 → no
```

Count:

```text
2 < 3
```

Cannot keep bit 1.

So:

```text
answer remains 1100
```

---

## Bit 0

Try:

```text
candidate
=
1100 | 0001
=
1101
```

Check:

```text
15: 1111 contains 1101 → yes
14: 1110 contains 1101 → no
12: 1100 contains 1101 → no
10: 1010 contains 1101 → no
 7: 0111 contains 1101 → no
```

Count:

```text
1 < 3
```

Cannot keep bit 0.

Final answer:

```text
1100₂ = 12
```

We can choose:

```text
15, 14, 12
```

and:

```text
15 & 14 & 12

1111
1110
1100
----
1100
=
12
```

---

# 10. Why the Greedy Is Correct

The proof has two parts.

---

## Part 1 — If a Higher Bit Is Feasible, We Must Take It

Suppose bit `j` can be set while preserving all previously accepted higher bits.

Then there exist at least `X` numbers supporting:

```text
candidate = answer | (1LL << j)
```

So a valid subsequence exists whose AND contains that bit.

If we reject this bit, could lower bits compensate?

No.

The value of bit `j` is:

```text
2^j
```

while the maximum possible total value of all lower bits is:

```text
2^j - 1
```

Therefore any number with bit `j = 1` is larger than every number having the same higher prefix but bit `j = 0`.

So if the bit is feasible:

```text
taking it is always optimal.
```

---

## Part 2 — The Feasibility Test Is Exact

For a mask `candidate` to be contained in the AND of `X` selected elements, every selected element must contain all bits of `candidate`.

For an element `A[i]`, that is exactly:

```text
(A[i] & candidate) == candidate
```

Therefore:

```text
candidate is achievable
iff
at least X elements satisfy the condition.
```

So:

```text
count >= X
```

is neither an approximation nor a heuristic.

It is the exact feasibility condition.

---

## Greedy Invariant

After processing bits above `j`:

```text
answer
```

is the maximum feasible prefix using those processed bits.

At bit `j`:

- if adding it remains feasible, any optimum must prefer that `1`;
- if adding it is infeasible, no valid subsequence can have all already accepted bits plus this bit.

Therefore the greedy decision is safe.

Processing all bits gives the maximum possible AND.

---

# 11. Clean C++17 — Preferred Template

This version is the easiest to remember.

It does not need to explicitly maintain a candidate vector.

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll maximumAndSubsequence(const vector<ll>& arr, int x) {
    ll answer = 0;

    for (int bit = 60; bit >= 0; bit--) {
        ll candidate = answer | (1LL << bit);
        int count = 0;

        for (ll value : arr) {
            if ((value & candidate) == candidate) {
                count++;
            }
        }

        if (count >= x) {
            answer = candidate;
        }
    }

    return answer;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;

    while (t--) {
        int n, x;
        cin >> n >> x;

        vector<ll> arr(n);

        for (ll& value : arr) {
            cin >> value;
        }

        cout << maximumAndSubsequence(arr, x) << '\n';
    }

    return 0;
}
```

---

## Why This Template Is Nice

Only remember:

```cpp
ll candidate = answer | (1LL << bit);
```

then:

```cpp
if ((value & candidate) == candidate) {
    count++;
}
```

and finally:

```cpp
if (count >= x) {
    answer = candidate;
}
```

That is the entire high-bit-to-low-bit greedy.

---

# 12. Candidate-Filtering Version

The lecture also motivates another way of thinking.

Maintain only numbers that still support all bits accepted so far.

Initially:

```text
candidates = all array elements
```

For each bit from high to low:

1. collect candidate elements having this bit,
2. if at least `X` remain:
   - keep the bit,
   - permanently discard the others,
3. otherwise ignore the bit.

Example:

```text
A = [7,5,6]
X = 2
```

Initially:

```text
candidates = [7,5,6]
```

Bit 2:

```text
all three have bit2
→ keep bit2
→ candidates = [7,5,6]
```

Bit 1:

```text
7 and 6 have bit1
→ 2 elements remain
→ keep bit1
→ candidates = [7,6]
```

Bit 0:

```text
only 7 has bit0
→ only 1 element
→ cannot keep bit0
```

Final:

```text
110₂ = 6
```

---

## C++ Version

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll maximumAndSubsequence(vector<ll> candidates, int x) {
    ll answer = 0;

    for (int bit = 60; bit >= 0; bit--) {
        vector<ll> nextCandidates;

        for (ll value : candidates) {
            if ((value >> bit) & 1LL) {
                nextCandidates.push_back(value);
            }
        }

        if ((int)nextCandidates.size() >= x) {
            answer |= (1LL << bit);
            candidates = move(nextCandidates);
        }
    }

    return answer;
}
```

This version mirrors the visual lecture explanation well.

The mask-feasibility version in the previous section is usually simpler for contests.

---

# 13. Complexity

Let:

```text
B = number of relevant bits
```

For every bit, we scan all `N` values.

Therefore:

```text
Time = O(B * N)
```

For 64-bit integers:

```text
B ≈ 61
```

So in practice:

```text
O(61N)
```

which behaves like linear time in `N`.

The preferred mask-check version uses:

```text
O(1)
```

extra algorithmic space, excluding the input array.

The filtering version may use:

```text
O(N)
```

extra space.

---

# 14. Common Mistakes

## Mistake 1 — Counting Each Bit Independently

Wrong idea:

```text
If at least X values contain bit j,
put bit j in answer.
```

Why wrong?

Because the same `X` values must support **all answer bits together**.

Example:

```text
A = [7,5,6]
X = 2
```

Both bit1 and bit0 individually appear twice.

But:

```text
111
```

is supported by only one value.

So the correct check is:

```cpp
(value & candidate) == candidate
```

where `candidate` contains all accepted bits plus the new bit.

---

## Mistake 2 — Going Lowest Bit to Highest

We are maximizing an integer.

Higher bits dominate lower bits.

Use:

```text
highest → lowest
```

---

## Mistake 3 — Testing Only the Current Bit

Wrong:

```cpp
if (value & (1LL << bit))
```

as the complete feasibility test.

That checks only the current bit.

Correct:

```cpp
if ((value & candidate) == candidate)
```

because the value must contain:

```text
all previously chosen bits
+
current proposed bit.
```

---

## Mistake 4 — Confusing AND with OR

For AND:

```text
a result bit survives only if
EVERY selected number has 1 there.
```

For OR:

```text
one selected number having 1 is enough.
```

The logic is completely different.

---

## Mistake 5 — Forgetting Exactly X Elements

The feasibility condition is:

```text
count >= X
```

not necessarily:

```text
count == X
```

If 10 elements support the candidate and we need only 3, we simply choose any 3 of them.

---

## Mistake 6 — Using `1 << bit` for Large Bits

Use:

```cpp
1LL << bit
```

when working with `long long`.

---

# 15. Recognition Pattern

Use this pattern when you see:

```text
choose K / X elements
maximize their bitwise AND
```

Immediately think:

```text
AND bit survives
⇔
every selected element contains that bit
```

Then:

```text
maximize integer
⇔
decide bits high → low
```

For each proposed mask:

```text
count elements containing the full mask
```

Feasibility:

```text
count >= X
```

So the full recognition chain is:

```text
MAXIMUM AND OF K ELEMENTS
          ↓
AND requires common 1-bits
          ↓
maximize value by highest bit first
          ↓
candidate mask
          ↓
count elements covering candidate
          ↓
count >= K ?
     /           \
   YES           NO
 keep bit      skip bit
```

---

# 16. Final Mental Model

Do not memorize a long implementation.

Remember these four lines:

```text
1. AND keeps a bit only if EVERY chosen number has it.

2. Higher bits are more important than all lower bits combined.

3. Try bits from high to low.

4. A candidate mask is possible iff at least X values contain the whole mask.
```

Code skeleton:

```cpp
ll answer = 0;

for (int bit = 60; bit >= 0; bit--) {
    ll candidate = answer | (1LL << bit);
    int count = 0;

    for (ll value : arr) {
        if ((value & candidate) == candidate) {
            count++;
        }
    }

    if (count >= x) {
        answer = candidate;
    }
}
```

Mental picture:

```text
                 Can X numbers support this bit
                 together with everything chosen before?
                               |
                               v

bit 60 → bit 59 → bit 58 → ... → bit 1 → bit 0
   |         |         |
  keep?     keep?     keep?

answer grows only when feasibility survives.
```

That is the reusable **highest-bit-to-lowest-bit greedy for maximum AND**.
