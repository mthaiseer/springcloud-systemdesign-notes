# 017_Counting_Basics.md

# Counting Basics For Competitive Programming

---

# 1. Introduction

Counting is one of the MOST important skills in CP.

Many problems are actually:

```text
counting problems in disguise
```

Strong contestants constantly ask:

```text
How many ways?
How many choices?
Can counting replace brute force?
```

Counting appears heavily in:
- combinatorics
- DP
- probability
- graphs
- greedy
- constructive
- bitmasking

---

# 2. Why Counting Matters

Weak thinking:

```text
generate everything
```

Strong thinking:

```text
count mathematically
```

Huge optimization.

Example:

Instead of generating all pairs:

```text
count directly
```

---

# 3. Basic Counting Principle

MOST IMPORTANT RULE:

If one thing can happen in:
```text
a ways
```

and another in:
```text
b ways
```

then together:

```text
a × b ways
```

Called:

```text
Multiplication Principle
```

---

# 4. Example — Shirt And Pant

Choices:
- 3 shirts
- 2 pants

Total outfits:

```text
3 × 2 = 6
```

---

# 5. Addition Principle

If choices are mutually exclusive:

```text
a ways
OR
b ways
```

Total:

```text
a + b
```

---

# Example

Travel:
- 3 bus routes
- 2 train routes

Total ways:

```text
5
```

---

# 6. Counting Pairs

VERY common CP pattern.

Number of unordered pairs from n elements:

genui{"math_block_widget_always_prefetch_v2":{"content":"\\frac{n(n-1)}{2}"}}

---

# Example

```text
n = 4
```

Pairs:

```text
(1,2)
(1,3)
(1,4)
(2,3)
(2,4)
(3,4)
```

Total:
```text
6
```

---

# Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long pairCount(long long n) {

    return n * (n - 1) / 2;
}
```

---

# 7. Counting Subarrays

Number of subarrays:

genui{"math_block_widget_always_prefetch_v2":{"content":"\\frac{n(n+1)}{2}"}}

because:
- choose start
- choose end

---

# Example

```text
n = 3
```

Subarrays:

```text
[1]
[2]
[3]
[1,2]
[2,3]
[1,2,3]
```

Total:
```text
6
```

---

# Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long subarrayCount(long long n) {

    return n * (n + 1) / 2;
}
```

---

# 8. Counting Subsets

Each element:
- take
- not take

2 choices.

Thus total subsets:

genui{"math_block_widget_always_prefetch_v2":{"content":"2^n"}}

---

# Example

```text
n = 3
```

Subsets:

```text
{}
{1}
{2}
{3}
{1,2}
{1,3}
{2,3}
{1,2,3}
```

Total:
```text
8
```

---

# Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long subsetCount(int n) {

    return 1LL << n;
}
```

---

# 9. Counting Divisors

Observation:

Divisors come in pairs.

Example:

```text
36

1×36
2×18
3×12
4×9
6×6
```

Need check only till:

genui{"math_block_widget_always_prefetch_v2":{"content":"\\sqrt{n}"}}

---

# Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int divisorCount(int n) {

    int count = 0;

    for (int d = 1;
         d * d <= n;
         d++) {

        if (n % d == 0) {

            count++;

            if (d != n / d)
                count++;
        }
    }

    return count;
}
```

---

# 10. Counting Frequencies

Very common pattern.

Instead of:
```text
checking repeatedly
```

store counts.

Used in:
- hashing
- prefix sums
- DP
- greedy

---

# Code

```cpp
#include <bits/stdc++.h>
using namespace std;

unordered_map<int,int>
frequency(vector<int>& a) {

    unordered_map<int,int> freq;

    for (int x : a) {

        freq[x]++;
    }

    return freq;
}
```

---

# 11. Counting With Prefix Frequency

Very important CP observation.

Example:

Count subarrays with sum k.

Observation:

```text
prefix[j] - prefix[i] = k
```

Rearrange:

```text
prefix[i] = prefix[j] - k
```

Use frequency map.

Huge optimization.

---

# 12. Counting In Graphs

Common patterns:
- degree count
- edge count
- connected components
- path counts

Example:
Undirected graph edges:

```text
sum(degrees) = 2 × edges
```

Very important observation.

---

# 13. Counting And Probability

Probability often reduces to:

```text
favorable count
/
total count
```

Thus counting skill directly helps probability.

---

# 14. Counting And DP

DP often means:

```text
count ways
```

Example:
- stair climbing
- paths
- subsets
- sequences

Counting intuition extremely important for DP.

---

# 15. CP / FAANG Problem Forms

---

# Form 1 — Pair Counting

## Problem

Count unordered pairs.

---

## Observation

Choose any 2 elements.

Formula:

genui{"math_block_widget_always_prefetch_v2":{"content":"\\frac{n(n-1)}{2}"}}

---

## Step-by-Step Working

Example:

```text
n = 5
```

Total:

```text
5×4/2
=
10
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long pairs(long long n) {

    return n * (n - 1) / 2;
}
```

---

# Form 2 — Subarray Count

## Problem

Count subarrays.

---

## Observation

Choose:
- start
- end

Formula:

genui{"math_block_widget_always_prefetch_v2":{"content":"\\frac{n(n+1)}{2}"}}

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long subarrays(long long n) {

    return n * (n + 1) / 2;
}
```

---

# Form 3 — Subset Count

## Problem

Count subsets.

---

## Observation

Each element:
- take
- not take

2 choices.

Formula:

genui{"math_block_widget_always_prefetch_v2":{"content":"2^n"}}

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long subsets(int n) {

    return 1LL << n;
}
```

---

# Form 4 — Divisor Counting

## Problem

Count divisors.

---

## Observation

Divisors appear in pairs.

Check till:

genui{"math_block_widget_always_prefetch_v2":{"content":"\\sqrt{n}"}}

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int countDivisors(int n) {

    int ans = 0;

    for (int d = 1;
         d * d <= n;
         d++) {

        if (n % d == 0) {

            ans++;

            if (d != n / d)
                ans++;
        }
    }

    return ans;
}
```

---

# Form 5 — Frequency Counting

## Problem

Count occurrences.

---

## Observation

Use hashmap frequency.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

unordered_map<int,int>
countFreq(vector<int>& a) {

    unordered_map<int,int> freq;

    for (int x : a) {

        freq[x]++;
    }

    return freq;
}
```

---

# Form 6 — Prefix Frequency Counting

## Problem

Count subarrays with target sum.

---

## Observation

Use prefix frequencies.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int subarraySum(
    vector<int>& a,
    int k
) {

    unordered_map<int,int> freq;

    freq[0] = 1;

    int prefix = 0;

    int ans = 0;

    for (int x : a) {

        prefix += x;

        ans += freq[prefix - k];

        freq[prefix]++;
    }

    return ans;
}
```

---

# Form 7 — Graph Edge Counting

## Problem

Find edges from degrees.

---

## Observation

Sum of degrees:

genui{"math_block_widget_always_prefetch_v2":{"content":"2E"}}

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int edgeCount(vector<int>& degree) {

    int sum = 0;

    for (int d : degree) {

        sum += d;
    }

    return sum / 2;
}
```

---

# 16. Real World Applications

| Real System | Counting Usage |
|---|---|
| Databases | aggregation |
| Search engines | frequency ranking |
| Analytics | event counting |
| Distributed systems | request counting |
| Networking | packet statistics |
| Recommendation systems | occurrence frequencies |
| Monitoring systems | metrics counting |

---

# 17. Real Engineering Insight

Counting often means:

```text
Avoid generation
→ derive mathematically
```

This is a huge optimization mindset.

---

# 18. Observation Recognition Signals

Look for:

```text
1. number of ways
2. combinations
3. frequency
4. pair counting
5. subarray count
6. repeated values
7. choices
8. grouping
9. arrangements
10. paths
```

---

# 19. Decision Tree

```text
Need count ways?
→ combinatorics

Need pair count?
→ n(n-1)/2

Need subset count?
→ 2^n

Need subarray count?
→ n(n+1)/2

Need occurrences?
→ hashmap frequency

Need path/sequence count?
→ DP counting
```

---

# 20. Common Traps

```text
1. Generating instead of counting
2. Overflow in formulas
3. Ordered vs unordered confusion
4. Duplicate counting
5. Wrong subset formula
6. Missing frequency optimization
7. Off-by-one in subarray count
8. Forgetting divisor pairs
```

---

# 21. Final Checklist

Before solving:

```text
1. Can counting replace brute force?
2. Is frequency useful?
3. Is formula derivation possible?
4. Are choices independent?
5. Is multiplication principle useful?
6. Is subset counting involved?
7. Are pairs unordered?
8. Is there combinatorial simplification?
```

---

# 22. Final Mental Shortcut

```text
Counting
=
Choices
+
Frequency
+
Combinatorics
+
Avoiding Brute Force
```
