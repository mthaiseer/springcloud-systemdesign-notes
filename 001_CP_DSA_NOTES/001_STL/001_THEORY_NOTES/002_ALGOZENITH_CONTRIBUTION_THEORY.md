# AlgoZenith — Contribution Technique

## TOC
- [1. Atomic Contribution](#1-atomic-contribution)
- [2. Sum of All Subset Sums](#2-sum-of-all-subset-sums)
- [3. Sum of All Subarray Sums](#3-sum-of-all-subarray-sums)
- [4. Pivot-Based Contribution](#4-pivot-based-contribution)
- [5. Sum of Products of All Subarrays](#5-sum-of-products-of-all-subarrays)
- [6. CF 276C — Little Girl and Maximum Sum](#6-cf-276c--little-girl-and-maximum-sum)
- [7. LC 907 — Sum of Subarray Minimums](#7-lc-907--sum-of-subarray-minimums)

# 1. Atomic Contribution

Instead of enumerating every structure:

```text
fix a[i] → count structures containing it → value × frequency
```

```text
answer = Σ contribution(i)
```

# 2. Sum of All Subset Sums

For `a = [3,5,2]`, there are `2^n-1` non-empty subsets.

Fix `a[i]`. It must be selected. Each of the other `n-1` items has two choices:

```text
take / don't take
```

Therefore:

```text
frequency(i) = 2^(n-1)

contribution(i) = a[i] × 2^(n-1)

answer = 2^(n-1) × Σa[i]
```

### Dry Run

```text
a = [3,5,2], n = 3

frequency = 2^(3-1) = 4

3 → 3×4 = 12
5 → 5×4 = 20
2 → 2×4 = 8

answer = 40
```

For `5`, the four subsets are:

```text
[5], [3,5], [5,2], [3,5,2]
```

### C++

```cpp
long long subsetSum(vector<long long>& a) {
    int n = a.size();

    long long sum = 0;
    for (long long x : a)
        sum += x;

    long long power = 1;
    for (int i = 0; i < n - 1; i++)
        power *= 2;

    return sum * power;
}
```

# 3. Sum of All Subarray Sums

Fix index `i`.

A subarray `[L,R]` contains it iff:

```text
L <= i <= R
```

Choices:

```text
left  = i + 1
right = n - i
```

Hence:

```text
frequency(i) = (i+1)(n-i)

contribution(i)
= a[i](i+1)(n-i)

answer
= Σ a[i](i+1)(n-i)
```

### Dry Run — `[3,5,2]`

| i | a[i] | Left | Right | Frequency | Contribution |
|---:|---:|---:|---:|---:|---:|
| 0 | 3 | 1 | 3 | 3 | 9 |
| 1 | 5 | 2 | 2 | 4 | 20 |
| 2 | 2 | 3 | 1 | 3 | 6 |

```text
answer = 9 + 20 + 6 = 35
```

For `5`:

```text
[5], [3,5], [5,2], [3,5,2]

frequency = 4
contribution = 5×4 = 20
```

### C++

```cpp
long long subarraySum(vector<long long>& a) {
    long long n = a.size();
    long long ans = 0;

    for (long long i = 0; i < n; i++)
        ans += a[i] * (i + 1) * (n - i);

    return ans;
}
```

# 4. Pivot-Based Contribution

Fix the **ending index**.

For `[3,5,2]`:

```text
end 0: [3]

end 1: [5], [3,5]

end 2: [2], [5,2], [3,5,2]
```

Every structure ending at `i` is usually:

```text
start fresh at i
OR
extend a structure ending at i-1
```

# 5. Sum of Products of All Subarrays

Let:

```text
prev = sum of products of all subarrays
       ending at previous index
```

At current value `x`:

```text
start fresh = x
extend all previous = x × prev
```

Therefore:

```text
current
= x + x×prev
= x(1+prev)

ans += current
prev = current
```

### Dry Run — `[3,5,2]`

```text
i=0, x=3
current = 3 + 3×0 = 3
ans = 3
```

```text
i=1, x=5

current
= 5 + 5×3
= 20

represents:
[5]   → 5
[3,5] → 15

ans = 3 + 20 = 23
```

```text
i=2, x=2

current
= 2 + 2×20
= 42

represents:
[2]     → 2
[5,2]   → 10
[3,5,2] → 30

ans = 23 + 42 = 65
```

### C++

```cpp
long long subarrayProductSum(vector<long long>& a) {
    long long ans = 0;
    long long prev = 0;

    for (long long x : a) {
        prev = prev * x + x;
        ans += prev;
    }

    return ans;
}
```

# 6. CF 276C — Little Girl and Maximum Sum

Problem: https://codeforces.com/problemset/problem/276/C

You have an array and many `[L,R]` queries. You may reorder the array once. Maximize the total of all query sums.

Fix a **position**:

```text
freq[i] = number of queries containing i
```

A value placed there contributes:

```text
value × freq[i]
```

Thus:

```text
answer = Σ value[i] × freq[i]
```

To maximize it, pair:

```text
largest value ↔ largest frequency
```

### Algebra

For:

```text
x <= y
p <= q
```

```text
(xp + yq) - (xq + yp)
= (y-x)(q-p)
>= 0
```

So matching large with large is optimal.

### Dry Run

```text
a = [5,3,2]

queries:
[1,2]
[2,3]
[1,3]

freq = [2,3,2]
```

Sort:

```text
a    = [2,3,5]
freq = [2,2,3]
```

```text
answer
= 2×2 + 3×2 + 5×3
= 25
```

### C++

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, q;
    cin >> n >> q;

    vector<long long> a(n), diff(n + 1), freq(n);

    for (auto& x : a)
        cin >> x;

    while (q--) {
        int l, r;
        cin >> l >> r;
        --l; --r;

        diff[l]++;
        if (r + 1 < n)
            diff[r + 1]--;
    }

    long long running = 0;

    for (int i = 0; i < n; i++) {
        running += diff[i];
        freq[i] = running;
    }

    sort(a.begin(), a.end());
    sort(freq.begin(), freq.end());

    long long ans = 0;

    for (int i = 0; i < n; i++)
        ans += a[i] * freq[i];

    cout << ans << '\n';
}
```

```text
Time: O(n log n + q)
```

# 7. LC 907 — Sum of Subarray Minimums

Problem: https://leetcode.com/problems/sum-of-subarray-minimums/

For every subarray, take its minimum and add all minimums.

Fix `a[i]` and ask:

```text
How many subarrays have a[i] as their chosen minimum?
```

Find its valid extension to the left and right using a monotonic stack.

```text
left  = i - previousSmaller
right = nextSmallerOrEqual - i
```

Then:

```text
frequency(i) = left × right

contribution(i)
= a[i] × left × right
```

One side uses strict comparison and the other non-strict comparison so equal values do not own the same subarray twice.

### Dry Run — `[3,1,2]`

```text
3:
left=1, right=1
3×1×1 = 3

1:
left=2, right=2
1×2×2 = 4

2:
left=1, right=1
2×1×1 = 2

answer = 3+4+2 = 9
```

### C++

```cpp
class Solution {
public:
    int sumSubarrayMins(vector<int>& a) {
        const long long MOD = 1e9 + 7;
        int n = a.size();

        vector<long long> left(n), right(n);
        stack<int> st;

        for (int i = 0; i < n; i++) {
            while (!st.empty() && a[st.top()] >= a[i])
                st.pop();

            left[i] = st.empty() ? i + 1 : i - st.top();
            st.push(i);
        }

        while (!st.empty())
            st.pop();

        for (int i = n - 1; i >= 0; i--) {
            while (!st.empty() && a[st.top()] > a[i])
                st.pop();

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

```text
Time:  O(n)
Space: O(n)
```

# Compact Formula Summary

```text
SUBSET SUM

a[i] × 2^(n-1)
```

```text
SUBARRAY SUM

a[i] × (i+1) × (n-i)
```

```text
PIVOT SUBARRAY PRODUCT

current
= a[i] + a[i]×previous
```

```text
SUBARRAY MINIMUM

a[i] × leftChoices × rightChoices
```
