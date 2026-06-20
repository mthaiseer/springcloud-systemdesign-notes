# 015_Prefix_Modulo_Problems.md — MiniPrefixSumDifferenceEngine

# Prefix Modulo Problems

> Prefix Modulo is one of the most powerful CP/FAANG techniques.
>
> Core idea:
>
> ```text
> Instead of storing full prefix sums,
> store prefix sums modulo K.
> ```
>
> This transforms many hard subarray problems into:
>
> ```text
> remainder matching
> modulo grouping
> modulo transitions
> remainder frequency counting
> ```

---

## Clickable Index

1. What Is Prefix Modulo?
2. Why Prefix Modulo Matters
3. Prefix Modulo Algebra
4. Same Remainder Rule
5. Why Modulo Reduces State Space
6. Negative Modulo Normalization
7. Prefix Modulo Frequency Pattern
8. Count vs Longest vs Shortest
9. Step-by-Step Dry Run — Divisible By K
10. Step-by-Step Dry Run — Negative Numbers
11. Problem Form 1 — Subarray Sum Divisible By K
12. Problem Form 2 — Longest Subarray Divisible By K
13. Problem Form 3 — Shortest Removal To Make Sum Divisible
14. Problem Form 4 — Binary Prefix Modulo
15. Problem Form 5 — Prefix Modulo Pair Counting
16. Real World Model 1 — Batch Processing Windows
17. Real World Model 2 — Kafka Partition Grouping
18. Real World Model 3 — Billing Bucket Aggregation
19. Real World Model 4 — Distributed Load Buckets
20. Decision Tree
21. Common Mistakes
22. Complexity
23. Reusable C++ Templates
24. CP / FAANG Problem Forms
25. Practice Checklist
26. Next Step

---

# 1. What Is Prefix Modulo?

Normally:

```text
prefix[i] = nums[0] + nums[1] + ... + nums[i]
```

Prefix modulo stores:

```text
prefix[i] % K
```

instead of full value.

Example:

```text
nums = [4, 5, 2]
K = 5
```

Prefix sums:

```text
4
9
11
```

Prefix modulo:

```text
4
4
1
```

Notice:

```text
same remainder appeared twice
```

This immediately signals:

```text
a divisible subarray exists
```

---

# 2. Why Prefix Modulo Matters

This technique appears in:

```text
Subarray divisible by K
Longest divisible subarray
Remove shortest subarray
Circular modulo problems
Modulo pairing problems
Remainder grouping
Hash bucket grouping
Partition balancing
```

It is heavily used in:

```text
Google
Meta
Amazon
Codeforces
AtCoder
ICPC
```

---

# 3. Prefix Modulo Algebra

Subarray sum:

```text
sum(i..j) = prefix[j] - prefix[i-1]
```

Need divisible by `K`:

```text
(prefix[j] - prefix[i-1]) % K == 0
```

This means:

```text
prefix[j] % K == prefix[i-1] % K
```

So:

```text
same remainder => divisible subarray
```

This is the most important identity.

---

# 4. Same Remainder Rule

If:

```text
A % K == B % K
```

then:

```text
(A - B) % K == 0
```

This creates the complete pattern.

Example:

```text
A = 14
B = 4
K = 5
```

Both:

```text
14 % 5 = 4
4 % 5 = 4
```

Difference:

```text
14 - 4 = 10
10 % 5 = 0
```

---

# 5. Why Modulo Reduces State Space

Without modulo:

```text
prefix sum can be huge
```

With modulo:

```text
only K possible remainders
```

Example:

```text
K = 5
```

Possible remainders:

```text
0 1 2 3 4
```

So many problems reduce to:

```text
grouping by remainder
```

instead of handling huge sums.

---

# 6. Negative Modulo Normalization

In C++:

```cpp
-2 % 5 = -2
```

But we need:

```text
0..K-1
```

Normalize:

```cpp
rem = ((prefix % K) + K) % K;
```

Example:

```text
prefix = -2
K = 5
```

Raw:

```text
-2
```

Normalized:

```text
((-2 % 5) + 5) % 5
= 3
```

Correct remainder:

```text
3
```

---

# 7. Prefix Modulo Frequency Pattern

Store:

```text
freq[remainder]
```

Algorithm:

```text
prefix += nums[i]
rem = normalized(prefix % K)
answer += freq[rem]
freq[rem]++
```

Why?

Every previous same remainder creates a divisible subarray.

---

# 8. Count vs Longest vs Shortest

| Goal | Store |
|---|---|
| Count subarrays | frequency |
| Longest subarray | first index |
| Shortest removal | latest index |

This distinction is extremely important.

---

# 9. Step-by-Step Dry Run — Divisible By K

Input:

```text
nums = [4,5,0,-2,-3,1]
K = 5
```

Initialize:

```text
freq[0] = 1
prefix = 0
answer = 0
```

Table:

| i | x | prefix | rem | freq before | answer |
|---|---:|---:|---:|---:|---:|
|0|4|4|4|0|0|
|1|5|9|4|1|1|
|2|0|9|4|2|3|
|3|-2|7|2|0|3|
|4|-3|4|4|3|6|
|5|1|5|0|1|7|

Final:

```text
7
```

---

# 10. Step-by-Step Dry Run — Negative Numbers

Input:

```text
nums = [-1,2,9]
K = 2
```

Initialize:

```text
freq[0] = 1
```

### i = 0

```text
prefix = -1
raw rem = -1
normalized = 1
```

### i = 1

```text
prefix = 1
rem = 1
same remainder found
```

Subarray:

```text
[2]
```

### i = 2

```text
prefix = 10
rem = 0
```

Subarray:

```text
[-1,2,9]
```

Answer:

```text
2
```

---

# 11. Problem Form 1 — Subarray Sum Divisible By K

## Recognition

```text
count subarrays divisible by K
```

---

## Pattern

```text
Prefix modulo + remainder frequency
```

---

## Step-by-Step Working

```text
1. Build prefix
2. Compute modulo
3. Match same remainder
4. Add previous frequency
5. Update frequency
```

---

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long subarraysDivByK(vector<int>& nums, int k) {

    vector<long long> freq(k, 0);

    // Empty prefix remainder.
    freq[0] = 1;

    long long prefix = 0;
    long long answer = 0;

    for (int x : nums) {

        prefix += x;

        // Normalize negative modulo.
        int rem = ((prefix % k) + k) % k;

        // Previous same remainder forms valid subarrays.
        answer += freq[rem];

        freq[rem]++;
    }

    return answer;
}

int main() {

    vector<int> nums = {4,5,0,-2,-3,1};

    cout << subarraysDivByK(nums, 5);

    return 0;
}
```

---

# 12. Problem Form 2 — Longest Subarray Divisible By K

## Recognition

```text
maximum length subarray divisible by K
```

---

## Pattern

```text
Store first occurrence of remainder
```

---

## Step-by-Step Working

If same remainder appears again:

```text
length = currentIndex - firstIndex[rem]
```

Maximize length.

---

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int longestDivisible(vector<int>& nums, int k) {

    unordered_map<int,int> firstIndex;

    firstIndex[0] = -1;

    long long prefix = 0;
    int best = 0;

    for (int i = 0; i < nums.size(); i++) {

        prefix += nums[i];

        int rem = ((prefix % k) + k) % k;

        if (firstIndex.count(rem)) {

            best = max(best, i - firstIndex[rem]);

        } else {

            firstIndex[rem] = i;
        }
    }

    return best;
}
```

---

# 13. Problem Form 3 — Shortest Removal To Make Sum Divisible

## Recognition

```text
remove shortest subarray
remaining total divisible
```

---

## Pattern

```text
Prefix modulo + latest index
```

---

## Step-by-Step Working

Need remove subarray whose modulo equals:

```text
totalSum % p
```

Use:

```text
needed = (current - target + p) % p
```

---

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int minSubarray(vector<int>& nums, int p) {

    long long total = 0;

    for (int x : nums) {
        total += x;
    }

    int target = total % p;

    if (target == 0) {
        return 0;
    }

    unordered_map<int,int> lastIndex;

    lastIndex[0] = -1;

    long long prefix = 0;
    int answer = nums.size();

    for (int i = 0; i < nums.size(); i++) {

        prefix += nums[i];

        int rem = prefix % p;

        int need = (rem - target + p) % p;

        if (lastIndex.count(need)) {

            answer = min(answer, i - lastIndex[need]);
        }

        lastIndex[rem] = i;
    }

    return answer == nums.size() ? -1 : answer;
}
```

---

# 14. Problem Form 4 — Binary Prefix Modulo

## Recognition

```text
binary array
modulo constraints
```

---

## Pattern

Binary prefix often combines with:

```text
modulo grouping
parity grouping
odd/even prefix
```

---

## Problem Simulation

Input:

```text
nums = [1,0,1,1]
```

Need even number of ones.

Transform to:

```text
prefix parity tracking
```

Even parity repeats create valid subarrays.

---

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countEvenOneSubarrays(vector<int>& nums) {

    long long freq[2] = {1, 0};

    int parity = 0;
    long long answer = 0;

    for (int x : nums) {

        parity ^= x;

        answer += freq[parity];

        freq[parity]++;
    }

    return answer;
}
```

---

# 15. Problem Form 5 — Prefix Modulo Pair Counting

## Recognition

```text
same remainder groups
```

---

## Pattern

If remainder appears:

```text
cnt times
```

Pairs:

```text
cnt * (cnt - 1) / 2
```

---

## Step-by-Step Working

Remainders:

```text
0 4 4 4 2 4 0
```

Counts:

```text
0 -> 2
4 -> 4
2 -> 1
```

Answer:

```text
1 + 6 + 0 = 7
```

---

## Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long pairCount(vector<long long>& freq) {

    long long answer = 0;

    for (long long cnt : freq) {

        answer += cnt * (cnt - 1) / 2;
    }

    return answer;
}
```

---

# 16. Real World Model 1 — Batch Processing Windows

## Scenario

Backend batches records in groups of `K`.

Question:

```text
Which continuous windows perfectly align with batch size?
```

---

## Problem Simulation

Events:

```text
[4,5,0,-2,-3,1]
batch = 5
```

Any window with sum divisible by `5` forms a valid batch window.

---

## System Mapping

Used in:

```text
ETL batching
message batching
stream processing
log compaction
queue flushing
```

---

## Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countBatchWindows(vector<int>& nums, int batch) {

    vector<long long> freq(batch, 0);

    freq[0] = 1;

    long long prefix = 0;
    long long answer = 0;

    for (int x : nums) {

        prefix += x;

        int rem = ((prefix % batch) + batch) % batch;

        answer += freq[rem];

        freq[rem]++;
    }

    return answer;
}
```

---

# 17. Real World Model 2 — Kafka Partition Grouping

## Scenario

Kafka partitions messages by modulo hashing.

Question:

```text
Which continuous windows align evenly with partition count?
```

---

## Problem Simulation

Messages:

```text
[10,-3,8,5,-5]
partitions = 5
```

Any window divisible by `5` aligns with partition grouping.

---

## System Mapping

Useful for:

```text
partition balancing
consumer batching
modulo bucketing
hash partition analysis
```

---

## Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

long long partitionAligned(vector<int>& nums, int partitions) {

    vector<long long> freq(partitions, 0);

    freq[0] = 1;

    long long prefix = 0;
    long long answer = 0;

    for (int x : nums) {

        prefix += x;

        int rem = ((prefix % partitions) + partitions) % partitions;

        answer += freq[rem];

        freq[rem]++;
    }

    return answer;
}
```

---

# 18. Real World Model 3 — Billing Bucket Aggregation

## Scenario

Cloud billing groups usage into fixed units.

Question:

```text
Which windows total exactly a multiple of billing unit?
```

---

## Problem Simulation

Usage:

```text
[40,60,30,70,-20,20]
unit = 100
```

Windows:

```text
[40,60]
[30,70]
```

---

## System Mapping

Used in:

```text
usage aggregation
cloud billing
quota buckets
credit accounting
```

---

## Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

long long billingAligned(vector<int>& usage, int unit) {

    vector<long long> freq(unit, 0);

    freq[0] = 1;

    long long prefix = 0;
    long long answer = 0;

    for (int x : usage) {

        prefix += x;

        int rem = ((prefix % unit) + unit) % unit;

        answer += freq[rem];

        freq[rem]++;
    }

    return answer;
}
```

---

# 19. Real World Model 4 — Distributed Load Buckets

## Scenario

Load balancer distributes requests across `K` buckets.

Question:

```text
Which request windows distribute evenly?
```

---

## Problem Simulation

Requests:

```text
[6,4,5,10,-5]
K = 5
```

Divisible windows distribute evenly across buckets.

---

## System Mapping

Useful for:

```text
bucket balancing
rate limiting
request grouping
load distribution
```

---

## Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

long long bucketBalanced(vector<int>& req, int buckets) {

    vector<long long> freq(buckets, 0);

    freq[0] = 1;

    long long prefix = 0;
    long long answer = 0;

    for (int x : req) {

        prefix += x;

        int rem = ((prefix % buckets) + buckets) % buckets;

        answer += freq[rem];

        freq[rem]++;
    }

    return answer;
}
```

---

# 20. Decision Tree

```text
Subarray problem?
|
+-- Divisible by K?
|   |
|   +-- Prefix modulo
|
+-- Count windows?
|   |
|   +-- frequency[remainder]
|
+-- Longest window?
|   |
|   +-- firstIndex[remainder]
|
+-- Shortest removal?
|   |
|   +-- latestIndex[remainder]
|
+-- Negative values exist?
    |
    +-- normalize modulo
```

---

# 21. Common Mistakes

## Mistake 1

Not normalizing modulo.

Wrong:

```cpp
prefix % k
```

Correct:

```cpp
((prefix % k) + k) % k
```

---

## Mistake 2

Forgetting:

```cpp
freq[0] = 1
```

---

## Mistake 3

Confusing target sum with divisible condition.

Need:

```text
same remainder
```

not:

```text
prefix - K
```

---

## Mistake 4

Using wrong storage.

```text
count -> frequency
longest -> first index
shortest -> latest index
```

---

# 22. Complexity

Count version:

```text
Time  : O(N)
Space : O(K)
```

---

# 23. Reusable C++ Templates

## Template 1 — Count Divisible

```cpp
long long countDivisible(vector<int>& nums, int k) {

    vector<long long> freq(k, 0);

    freq[0] = 1;

    long long prefix = 0;
    long long answer = 0;

    for (int x : nums) {

        prefix += x;

        int rem = ((prefix % k) + k) % k;

        answer += freq[rem];

        freq[rem]++;
    }

    return answer;
}
```

---

## Template 2 — Longest Divisible

```cpp
int longestDivisible(vector<int>& nums, int k) {

    unordered_map<int,int> firstIndex;

    firstIndex[0] = -1;

    long long prefix = 0;
    int best = 0;

    for (int i = 0; i < nums.size(); i++) {

        prefix += nums[i];

        int rem = ((prefix % k) + k) % k;

        if (firstIndex.count(rem)) {

            best = max(best, i - firstIndex[rem]);

        } else {

            firstIndex[rem] = i;
        }
    }

    return best;
}
```

---

## Template 3 — Shortest Removal

```cpp
int shortestRemoval(vector<int>& nums, int p) {

    long long total = 0;

    for (int x : nums) total += x;

    int target = total % p;

    if (target == 0) return 0;

    unordered_map<int,int> lastIndex;

    lastIndex[0] = -1;

    long long prefix = 0;
    int answer = nums.size();

    for (int i = 0; i < nums.size(); i++) {

        prefix += nums[i];

        int rem = prefix % p;

        int need = (rem - target + p) % p;

        if (lastIndex.count(need)) {

            answer = min(answer, i - lastIndex[need]);
        }

        lastIndex[rem] = i;
    }

    return answer == nums.size() ? -1 : answer;
}
```

---

# 24. CP / FAANG Problem Forms

## Problem 1 — Subarray Divisible By K

### Recognition

```text
count subarrays divisible by K
```

### Pattern

```text
same remainder frequency
```

### Commented C++ Code

```cpp
long long solve(vector<int>& nums, int k) {

    vector<long long> freq(k, 0);

    freq[0] = 1;

    long long prefix = 0;
    long long ans = 0;

    for (int x : nums) {

        prefix += x;

        int rem = ((prefix % k) + k) % k;

        ans += freq[rem];

        freq[rem]++;
    }

    return ans;
}
```

---

## Problem 2 — Longest Divisible Subarray

### Recognition

```text
maximum length divisible by K
```

### Pattern

```text
first occurrence remainder
```

### Commented C++ Code

```cpp
int solve(vector<int>& nums, int k) {

    unordered_map<int,int> first;

    first[0] = -1;

    long long prefix = 0;
    int best = 0;

    for (int i = 0; i < nums.size(); i++) {

        prefix += nums[i];

        int rem = ((prefix % k) + k) % k;

        if (first.count(rem)) {

            best = max(best, i - first[rem]);

        } else {

            first[rem] = i;
        }
    }

    return best;
}
```

---

## Problem 3 — Shortest Removal

### Recognition

```text
remove shortest subarray
```

### Pattern

```text
latest index remainder
```

### Commented C++ Code

```cpp
int solve(vector<int>& nums, int p) {

    long long total = 0;

    for (int x : nums) total += x;

    int target = total % p;

    unordered_map<int,int> last;

    last[0] = -1;

    long long prefix = 0;
    int ans = nums.size();

    for (int i = 0; i < nums.size(); i++) {

        prefix += nums[i];

        int rem = prefix % p;

        int need = (rem - target + p) % p;

        if (last.count(need)) {

            ans = min(ans, i - last[need]);
        }

        last[rem] = i;
    }

    return ans == nums.size() ? -1 : ans;
}
```

---

## Problem 4 — Even/Odd Prefix Parity

### Recognition

```text
even odd grouping
```

### Pattern

```text
prefix modulo 2
```

### Commented C++ Code

```cpp
long long evenSum(vector<int>& nums) {

    long long freq[2] = {1,0};

    int parity = 0;
    long long ans = 0;

    for (int x : nums) {

        parity = (parity + x) % 2;

        ans += freq[parity];

        freq[parity]++;
    }

    return ans;
}
```

---

## Problem 5 — Modulo Bucket Pairing

### Recognition

```text
group by remainder
```

### Pattern

```text
combination counting
```

### Commented C++ Code

```cpp
long long pairCount(vector<long long>& freq) {

    long long ans = 0;

    for (long long cnt : freq) {

        ans += cnt * (cnt - 1) / 2;
    }

    return ans;
}
```

---

# 25. Practice Checklist

```text
1. Is it divisible/modulo based?
2. Can same remainder help?
3. Count or longest or shortest?
4. Need frequency or index?
5. Did I normalize modulo?
6. Did I initialize remainder 0?
7. Is modulo logic correct for negatives?
8. Can I reduce state space using modulo?
9. Is this pairing/grouping by remainder?
10. Should I use vector or hashmap?
```

---

# 26. Next Step

```text
016_Prefix_XOR.md
```

Next file covers:

```text
prefix xor
xor subarray problems
xor hashmap
bitwise prefix logic
zero xor patterns
