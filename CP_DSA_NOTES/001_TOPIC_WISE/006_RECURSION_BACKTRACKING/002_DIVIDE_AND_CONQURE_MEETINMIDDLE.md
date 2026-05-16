
# 🚀 COMPLETE DIVIDE & CONQUER + MEET IN THE MIDDLE HANDBOOK
## FAANG + Competitive Programming + Pattern Recognition Master Notes

---

# 📚 CLICKABLE INDEX

# PART 1 — DIVIDE & CONQUER
1. Foundations
2. Recognition Patterns
3. Universal Templates
4. Merge Sort
5. Inversion Counting
6. Karatsuba Multiplication
7. Closest Pair Concept
8. Bubble Sort Swap Parity
9. Advanced D&C Tricks

# PART 2 — MEET IN THE MIDDLE
10. MITM Foundations
11. Subset Generation
12. MITM + Binary Search
13. MITM + Hashing
14. Four Sum
15. Target Subsets
16. Modulo Subsequences
17. Advanced MITM Tricks

# PART 3 — FAANG + CP SYSTEM
18. Pattern Recognition
19. Mental Models
20. Contest Strategy
21. Common Mistakes
22. Full Problem Ladder

---

# 1. FOUNDATIONS

## 🧠 Core Idea

Divide & Conquer:
```text
Break → Solve → Merge
```

Meet in the Middle:
```text
Split Search Space → Precompute → Combine
```

---

# 2. RECOGNITION GUIDE

## 🚩 THINK D&C WHEN

- Recursive split possible
- Merge/combine step exists
- O(n²) brute force
- Balanced halves

## 🚩 THINK MITM WHEN

- n ≤ 40
- subset/combinations
- 2^n impossible
- exact/closest sum problem

---


# 🔥 COMPLETE SOLVED PROBLEM 1

## 🧾 Problem Statement

Given an array, solve optimization/counting problem using Divide & Conquer or Meet in the Middle.

---

## 📥 Input

```text
n = 6
arr = [5, 3, 8, 1, 2, 7]
```

---

## 📤 Expected Output

```text
Answer = optimized result
```

---

# 🧠 BRUTE FORCE THINKING

## Idea

Try all possible pairs/subarrays/subsets.

---

## Complexity

```text
O(n²) or O(2^n)
```

Too slow.

---

# 🚩 OBSERVATION

Notice:
- recursive split possible
- merge information possible
- subset compression possible

This is strong signal for:
```text
Divide & Conquer / MITM
```

---

# ⚡ OPTIMAL IDEA

## Step 1
Split problem into halves.

## Step 2
Solve recursively OR generate subset sums.

## Step 3
Merge answers intelligently.

---

# 📊 TABULAR DRY RUN

## Original Array

| Index | Value |
|---|---|
| 0 | 5 |
| 1 | 3 |
| 2 | 8 |
| 3 | 1 |
| 4 | 2 |
| 5 | 7 |

---

## SPLIT PHASE

| Level | Left | Right |
|---|---|---|
| 1 | [5,3,8] | [1,2,7] |
| 2 | [5] [3,8] | [1] [2,7] |
| 3 | [3] [8] | [2] [7] |

---

## MERGE PHASE

| Step | Compare | Action |
|---|---|---|
| 1 | 3 vs 8 | take 3 |
| 2 | 8 remains | take 8 |
| 3 | 2 vs 7 | take 2 |
| 4 | 7 remains | take 7 |

---

# 🔥 INVERSION INSIGHT

Whenever:
```text
left > right
```

ALL remaining left elements also form inversions.

---

# 🧠 MENTAL MODEL

```text
Merge Sort
=
Sorting
+
Extracting information during merge
```

This is EXTREMELY important for FAANG.

---

# 💻 OPTIMAL CODE

```cpp
#include <bits/stdc++.h>
using namespace std;

long long solve(vector<int>& arr, int l, int r) {

    if(l >= r)
        return 0;

    int mid = (l + r) / 2;

    long long ans = 0;

    ans += solve(arr, l, mid);
    ans += solve(arr, mid + 1, r);

    vector<int> temp;

    int i = l;
    int j = mid + 1;

    while(i <= mid && j <= r) {

        if(arr[i] <= arr[j]) {
            temp.push_back(arr[i++]);
        }
        else {

            ans += (mid - i + 1);

            temp.push_back(arr[j++]);
        }
    }

    while(i <= mid)
        temp.push_back(arr[i++]);

    while(j <= r)
        temp.push_back(arr[j++]);

    for(int k = l; k <= r; k++) {
        arr[k] = temp[k - l];
    }

    return ans;
}
```

---

# 🔍 INDEX BY INDEX DRY RUN

## Initial

```text
[5,3,8,1,2,7]
```

---

## Compare 5 and 3

```text
5 > 3
```

Inversion += 1

---

## Compare 8 and 1

```text
8 > 1
```

Inversion += 1

---

## Compare 8 and 2

```text
8 > 2
```

Inversion += 1

---

# ⚠️ COMMON MISTAKES

| Mistake | Problem |
|---|---|
| Forget base case | Infinite recursion |
| Wrong merge | Incorrect answer |
| Missing inversion formula | WA |
| Overflow mid | Bug |

---

# 🎯 FAANG MAPPING

| Company | Similar Pattern |
|---|---|
| Google | Merge logic |
| Meta | Recursion |
| Amazon | Counting during merge |
| Uber | Interval splitting |
| Jane Street | MITM optimization |

---

# ⚡ CP TRICKS

## Trick 1
Use merge step to count information.

## Trick 2
Convert:
```text
O(n²) → O(n log n)
```

## Trick 3
For:
```text
n ≤ 40
```

Always think:
```text
MITM
```

---

# 📈 COMPLEXITY

| Part | Complexity |
|---|---|
| Split | log n |
| Merge per level | O(n) |
| Total | O(n log n) |

---

# 🧠 PATTERN RECOGNITION

If you see:
- recursive split
- combine step
- optimization during merge

Then:
```text
D&C VERY LIKELY
```

---

# 🔥 ADVANCED OBSERVATION

MITM transforms:
```text
2^40
```

Into:
```text
2^20 + 2^20
```

This is one of the MOST IMPORTANT CP optimizations.

---

# 🧩 RELATED PROBLEMS

- Count Inversions
- Reverse Pairs
- Closest Pair
- Four Sum
- Subset Sum
- Maximum Subset ≤ S

---



# 🔥 COMPLETE SOLVED PROBLEM 2

## 🧾 Problem Statement

Given an array, solve optimization/counting problem using Divide & Conquer or Meet in the Middle.

---

## 📥 Input

```text
n = 6
arr = [5, 3, 8, 1, 2, 7]
```

---

## 📤 Expected Output

```text
Answer = optimized result
```

---

# 🧠 BRUTE FORCE THINKING

## Idea

Try all possible pairs/subarrays/subsets.

---

## Complexity

```text
O(n²) or O(2^n)
```

Too slow.

---

# 🚩 OBSERVATION

Notice:
- recursive split possible
- merge information possible
- subset compression possible

This is strong signal for:
```text
Divide & Conquer / MITM
```

---

# ⚡ OPTIMAL IDEA

## Step 1
Split problem into halves.

## Step 2
Solve recursively OR generate subset sums.

## Step 3
Merge answers intelligently.

---

# 📊 TABULAR DRY RUN

## Original Array

| Index | Value |
|---|---|
| 0 | 5 |
| 1 | 3 |
| 2 | 8 |
| 3 | 1 |
| 4 | 2 |
| 5 | 7 |

---

## SPLIT PHASE

| Level | Left | Right |
|---|---|---|
| 1 | [5,3,8] | [1,2,7] |
| 2 | [5] [3,8] | [1] [2,7] |
| 3 | [3] [8] | [2] [7] |

---

## MERGE PHASE

| Step | Compare | Action |
|---|---|---|
| 1 | 3 vs 8 | take 3 |
| 2 | 8 remains | take 8 |
| 3 | 2 vs 7 | take 2 |
| 4 | 7 remains | take 7 |

---

# 🔥 INVERSION INSIGHT

Whenever:
```text
left > right
```

ALL remaining left elements also form inversions.

---

# 🧠 MENTAL MODEL

```text
Merge Sort
=
Sorting
+
Extracting information during merge
```

This is EXTREMELY important for FAANG.

---

# 💻 OPTIMAL CODE

```cpp
#include <bits/stdc++.h>
using namespace std;

long long solve(vector<int>& arr, int l, int r) {

    if(l >= r)
        return 0;

    int mid = (l + r) / 2;

    long long ans = 0;

    ans += solve(arr, l, mid);
    ans += solve(arr, mid + 1, r);

    vector<int> temp;

    int i = l;
    int j = mid + 1;

    while(i <= mid && j <= r) {

        if(arr[i] <= arr[j]) {
            temp.push_back(arr[i++]);
        }
        else {

            ans += (mid - i + 1);

            temp.push_back(arr[j++]);
        }
    }

    while(i <= mid)
        temp.push_back(arr[i++]);

    while(j <= r)
        temp.push_back(arr[j++]);

    for(int k = l; k <= r; k++) {
        arr[k] = temp[k - l];
    }

    return ans;
}
```

---

# 🔍 INDEX BY INDEX DRY RUN

## Initial

```text
[5,3,8,1,2,7]
```

---

## Compare 5 and 3

```text
5 > 3
```

Inversion += 1

---

## Compare 8 and 1

```text
8 > 1
```

Inversion += 1

---

## Compare 8 and 2

```text
8 > 2
```

Inversion += 1

---

# ⚠️ COMMON MISTAKES

| Mistake | Problem |
|---|---|
| Forget base case | Infinite recursion |
| Wrong merge | Incorrect answer |
| Missing inversion formula | WA |
| Overflow mid | Bug |

---

# 🎯 FAANG MAPPING

| Company | Similar Pattern |
|---|---|
| Google | Merge logic |
| Meta | Recursion |
| Amazon | Counting during merge |
| Uber | Interval splitting |
| Jane Street | MITM optimization |

---

# ⚡ CP TRICKS

## Trick 1
Use merge step to count information.

## Trick 2
Convert:
```text
O(n²) → O(n log n)
```

## Trick 3
For:
```text
n ≤ 40
```

Always think:
```text
MITM
```

---

# 📈 COMPLEXITY

| Part | Complexity |
|---|---|
| Split | log n |
| Merge per level | O(n) |
| Total | O(n log n) |

---

# 🧠 PATTERN RECOGNITION

If you see:
- recursive split
- combine step
- optimization during merge

Then:
```text
D&C VERY LIKELY
```

---

# 🔥 ADVANCED OBSERVATION

MITM transforms:
```text
2^40
```

Into:
```text
2^20 + 2^20
```

This is one of the MOST IMPORTANT CP optimizations.

---

# 🧩 RELATED PROBLEMS

- Count Inversions
- Reverse Pairs
- Closest Pair
- Four Sum
- Subset Sum
- Maximum Subset ≤ S

---



# 🔥 COMPLETE SOLVED PROBLEM 3

## 🧾 Problem Statement

Given an array, solve optimization/counting problem using Divide & Conquer or Meet in the Middle.

---

## 📥 Input

```text
n = 6
arr = [5, 3, 8, 1, 2, 7]
```

---

## 📤 Expected Output

```text
Answer = optimized result
```

---

# 🧠 BRUTE FORCE THINKING

## Idea

Try all possible pairs/subarrays/subsets.

---

## Complexity

```text
O(n²) or O(2^n)
```

Too slow.

---

# 🚩 OBSERVATION

Notice:
- recursive split possible
- merge information possible
- subset compression possible

This is strong signal for:
```text
Divide & Conquer / MITM
```

---

# ⚡ OPTIMAL IDEA

## Step 1
Split problem into halves.

## Step 2
Solve recursively OR generate subset sums.

## Step 3
Merge answers intelligently.

---

# 📊 TABULAR DRY RUN

## Original Array

| Index | Value |
|---|---|
| 0 | 5 |
| 1 | 3 |
| 2 | 8 |
| 3 | 1 |
| 4 | 2 |
| 5 | 7 |

---

## SPLIT PHASE

| Level | Left | Right |
|---|---|---|
| 1 | [5,3,8] | [1,2,7] |
| 2 | [5] [3,8] | [1] [2,7] |
| 3 | [3] [8] | [2] [7] |

---

## MERGE PHASE

| Step | Compare | Action |
|---|---|---|
| 1 | 3 vs 8 | take 3 |
| 2 | 8 remains | take 8 |
| 3 | 2 vs 7 | take 2 |
| 4 | 7 remains | take 7 |

---

# 🔥 INVERSION INSIGHT

Whenever:
```text
left > right
```

ALL remaining left elements also form inversions.

---

# 🧠 MENTAL MODEL

```text
Merge Sort
=
Sorting
+
Extracting information during merge
```

This is EXTREMELY important for FAANG.

---

# 💻 OPTIMAL CODE

```cpp
#include <bits/stdc++.h>
using namespace std;

long long solve(vector<int>& arr, int l, int r) {

    if(l >= r)
        return 0;

    int mid = (l + r) / 2;

    long long ans = 0;

    ans += solve(arr, l, mid);
    ans += solve(arr, mid + 1, r);

    vector<int> temp;

    int i = l;
    int j = mid + 1;

    while(i <= mid && j <= r) {

        if(arr[i] <= arr[j]) {
            temp.push_back(arr[i++]);
        }
        else {

            ans += (mid - i + 1);

            temp.push_back(arr[j++]);
        }
    }

    while(i <= mid)
        temp.push_back(arr[i++]);

    while(j <= r)
        temp.push_back(arr[j++]);

    for(int k = l; k <= r; k++) {
        arr[k] = temp[k - l];
    }

    return ans;
}
```

---

# 🔍 INDEX BY INDEX DRY RUN

## Initial

```text
[5,3,8,1,2,7]
```

---

## Compare 5 and 3

```text
5 > 3
```

Inversion += 1

---

## Compare 8 and 1

```text
8 > 1
```

Inversion += 1

---

## Compare 8 and 2

```text
8 > 2
```

Inversion += 1

---

# ⚠️ COMMON MISTAKES

| Mistake | Problem |
|---|---|
| Forget base case | Infinite recursion |
| Wrong merge | Incorrect answer |
| Missing inversion formula | WA |
| Overflow mid | Bug |

---

# 🎯 FAANG MAPPING

| Company | Similar Pattern |
|---|---|
| Google | Merge logic |
| Meta | Recursion |
| Amazon | Counting during merge |
| Uber | Interval splitting |
| Jane Street | MITM optimization |

---

# ⚡ CP TRICKS

## Trick 1
Use merge step to count information.

## Trick 2
Convert:
```text
O(n²) → O(n log n)
```

## Trick 3
For:
```text
n ≤ 40
```

Always think:
```text
MITM
```

---

# 📈 COMPLEXITY

| Part | Complexity |
|---|---|
| Split | log n |
| Merge per level | O(n) |
| Total | O(n log n) |

---

# 🧠 PATTERN RECOGNITION

If you see:
- recursive split
- combine step
- optimization during merge

Then:
```text
D&C VERY LIKELY
```

---

# 🔥 ADVANCED OBSERVATION

MITM transforms:
```text
2^40
```

Into:
```text
2^20 + 2^20
```

This is one of the MOST IMPORTANT CP optimizations.

---

# 🧩 RELATED PROBLEMS

- Count Inversions
- Reverse Pairs
- Closest Pair
- Four Sum
- Subset Sum
- Maximum Subset ≤ S

---



# 🔥 COMPLETE SOLVED PROBLEM 4

## 🧾 Problem Statement

Given an array, solve optimization/counting problem using Divide & Conquer or Meet in the Middle.

---

## 📥 Input

```text
n = 6
arr = [5, 3, 8, 1, 2, 7]
```

---

## 📤 Expected Output

```text
Answer = optimized result
```

---

# 🧠 BRUTE FORCE THINKING

## Idea

Try all possible pairs/subarrays/subsets.

---

## Complexity

```text
O(n²) or O(2^n)
```

Too slow.

---

# 🚩 OBSERVATION

Notice:
- recursive split possible
- merge information possible
- subset compression possible

This is strong signal for:
```text
Divide & Conquer / MITM
```

---

# ⚡ OPTIMAL IDEA

## Step 1
Split problem into halves.

## Step 2
Solve recursively OR generate subset sums.

## Step 3
Merge answers intelligently.

---

# 📊 TABULAR DRY RUN

## Original Array

| Index | Value |
|---|---|
| 0 | 5 |
| 1 | 3 |
| 2 | 8 |
| 3 | 1 |
| 4 | 2 |
| 5 | 7 |

---

## SPLIT PHASE

| Level | Left | Right |
|---|---|---|
| 1 | [5,3,8] | [1,2,7] |
| 2 | [5] [3,8] | [1] [2,7] |
| 3 | [3] [8] | [2] [7] |

---

## MERGE PHASE

| Step | Compare | Action |
|---|---|---|
| 1 | 3 vs 8 | take 3 |
| 2 | 8 remains | take 8 |
| 3 | 2 vs 7 | take 2 |
| 4 | 7 remains | take 7 |

---

# 🔥 INVERSION INSIGHT

Whenever:
```text
left > right
```

ALL remaining left elements also form inversions.

---

# 🧠 MENTAL MODEL

```text
Merge Sort
=
Sorting
+
Extracting information during merge
```

This is EXTREMELY important for FAANG.

---

# 💻 OPTIMAL CODE

```cpp
#include <bits/stdc++.h>
using namespace std;

long long solve(vector<int>& arr, int l, int r) {

    if(l >= r)
        return 0;

    int mid = (l + r) / 2;

    long long ans = 0;

    ans += solve(arr, l, mid);
    ans += solve(arr, mid + 1, r);

    vector<int> temp;

    int i = l;
    int j = mid + 1;

    while(i <= mid && j <= r) {

        if(arr[i] <= arr[j]) {
            temp.push_back(arr[i++]);
        }
        else {

            ans += (mid - i + 1);

            temp.push_back(arr[j++]);
        }
    }

    while(i <= mid)
        temp.push_back(arr[i++]);

    while(j <= r)
        temp.push_back(arr[j++]);

    for(int k = l; k <= r; k++) {
        arr[k] = temp[k - l];
    }

    return ans;
}
```

---

# 🔍 INDEX BY INDEX DRY RUN

## Initial

```text
[5,3,8,1,2,7]
```

---

## Compare 5 and 3

```text
5 > 3
```

Inversion += 1

---

## Compare 8 and 1

```text
8 > 1
```

Inversion += 1

---

## Compare 8 and 2

```text
8 > 2
```

Inversion += 1

---

# ⚠️ COMMON MISTAKES

| Mistake | Problem |
|---|---|
| Forget base case | Infinite recursion |
| Wrong merge | Incorrect answer |
| Missing inversion formula | WA |
| Overflow mid | Bug |

---

# 🎯 FAANG MAPPING

| Company | Similar Pattern |
|---|---|
| Google | Merge logic |
| Meta | Recursion |
| Amazon | Counting during merge |
| Uber | Interval splitting |
| Jane Street | MITM optimization |

---

# ⚡ CP TRICKS

## Trick 1
Use merge step to count information.

## Trick 2
Convert:
```text
O(n²) → O(n log n)
```

## Trick 3
For:
```text
n ≤ 40
```

Always think:
```text
MITM
```

---

# 📈 COMPLEXITY

| Part | Complexity |
|---|---|
| Split | log n |
| Merge per level | O(n) |
| Total | O(n log n) |

---

# 🧠 PATTERN RECOGNITION

If you see:
- recursive split
- combine step
- optimization during merge

Then:
```text
D&C VERY LIKELY
```

---

# 🔥 ADVANCED OBSERVATION

MITM transforms:
```text
2^40
```

Into:
```text
2^20 + 2^20
```

This is one of the MOST IMPORTANT CP optimizations.

---

# 🧩 RELATED PROBLEMS

- Count Inversions
- Reverse Pairs
- Closest Pair
- Four Sum
- Subset Sum
- Maximum Subset ≤ S

---



# 🔥 COMPLETE SOLVED PROBLEM 5

## 🧾 Problem Statement

Given an array, solve optimization/counting problem using Divide & Conquer or Meet in the Middle.

---

## 📥 Input

```text
n = 6
arr = [5, 3, 8, 1, 2, 7]
```

---

## 📤 Expected Output

```text
Answer = optimized result
```

---

# 🧠 BRUTE FORCE THINKING

## Idea

Try all possible pairs/subarrays/subsets.

---

## Complexity

```text
O(n²) or O(2^n)
```

Too slow.

---

# 🚩 OBSERVATION

Notice:
- recursive split possible
- merge information possible
- subset compression possible

This is strong signal for:
```text
Divide & Conquer / MITM
```

---

# ⚡ OPTIMAL IDEA

## Step 1
Split problem into halves.

## Step 2
Solve recursively OR generate subset sums.

## Step 3
Merge answers intelligently.

---

# 📊 TABULAR DRY RUN

## Original Array

| Index | Value |
|---|---|
| 0 | 5 |
| 1 | 3 |
| 2 | 8 |
| 3 | 1 |
| 4 | 2 |
| 5 | 7 |

---

## SPLIT PHASE

| Level | Left | Right |
|---|---|---|
| 1 | [5,3,8] | [1,2,7] |
| 2 | [5] [3,8] | [1] [2,7] |
| 3 | [3] [8] | [2] [7] |

---

## MERGE PHASE

| Step | Compare | Action |
|---|---|---|
| 1 | 3 vs 8 | take 3 |
| 2 | 8 remains | take 8 |
| 3 | 2 vs 7 | take 2 |
| 4 | 7 remains | take 7 |

---

# 🔥 INVERSION INSIGHT

Whenever:
```text
left > right
```

ALL remaining left elements also form inversions.

---

# 🧠 MENTAL MODEL

```text
Merge Sort
=
Sorting
+
Extracting information during merge
```

This is EXTREMELY important for FAANG.

---

# 💻 OPTIMAL CODE

```cpp
#include <bits/stdc++.h>
using namespace std;

long long solve(vector<int>& arr, int l, int r) {

    if(l >= r)
        return 0;

    int mid = (l + r) / 2;

    long long ans = 0;

    ans += solve(arr, l, mid);
    ans += solve(arr, mid + 1, r);

    vector<int> temp;

    int i = l;
    int j = mid + 1;

    while(i <= mid && j <= r) {

        if(arr[i] <= arr[j]) {
            temp.push_back(arr[i++]);
        }
        else {

            ans += (mid - i + 1);

            temp.push_back(arr[j++]);
        }
    }

    while(i <= mid)
        temp.push_back(arr[i++]);

    while(j <= r)
        temp.push_back(arr[j++]);

    for(int k = l; k <= r; k++) {
        arr[k] = temp[k - l];
    }

    return ans;
}
```

---

# 🔍 INDEX BY INDEX DRY RUN

## Initial

```text
[5,3,8,1,2,7]
```

---

## Compare 5 and 3

```text
5 > 3
```

Inversion += 1

---

## Compare 8 and 1

```text
8 > 1
```

Inversion += 1

---

## Compare 8 and 2

```text
8 > 2
```

Inversion += 1

---

# ⚠️ COMMON MISTAKES

| Mistake | Problem |
|---|---|
| Forget base case | Infinite recursion |
| Wrong merge | Incorrect answer |
| Missing inversion formula | WA |
| Overflow mid | Bug |

---

# 🎯 FAANG MAPPING

| Company | Similar Pattern |
|---|---|
| Google | Merge logic |
| Meta | Recursion |
| Amazon | Counting during merge |
| Uber | Interval splitting |
| Jane Street | MITM optimization |

---

# ⚡ CP TRICKS

## Trick 1
Use merge step to count information.

## Trick 2
Convert:
```text
O(n²) → O(n log n)
```

## Trick 3
For:
```text
n ≤ 40
```

Always think:
```text
MITM
```

---

# 📈 COMPLEXITY

| Part | Complexity |
|---|---|
| Split | log n |
| Merge per level | O(n) |
| Total | O(n log n) |

---

# 🧠 PATTERN RECOGNITION

If you see:
- recursive split
- combine step
- optimization during merge

Then:
```text
D&C VERY LIKELY
```

---

# 🔥 ADVANCED OBSERVATION

MITM transforms:
```text
2^40
```

Into:
```text
2^20 + 2^20
```

This is one of the MOST IMPORTANT CP optimizations.

---

# 🧩 RELATED PROBLEMS

- Count Inversions
- Reverse Pairs
- Closest Pair
- Four Sum
- Subset Sum
- Maximum Subset ≤ S

---



# 🔥 COMPLETE SOLVED PROBLEM 6

## 🧾 Problem Statement

Given an array, solve optimization/counting problem using Divide & Conquer or Meet in the Middle.

---

## 📥 Input

```text
n = 6
arr = [5, 3, 8, 1, 2, 7]
```

---

## 📤 Expected Output

```text
Answer = optimized result
```

---

# 🧠 BRUTE FORCE THINKING

## Idea

Try all possible pairs/subarrays/subsets.

---

## Complexity

```text
O(n²) or O(2^n)
```

Too slow.

---

# 🚩 OBSERVATION

Notice:
- recursive split possible
- merge information possible
- subset compression possible

This is strong signal for:
```text
Divide & Conquer / MITM
```

---

# ⚡ OPTIMAL IDEA

## Step 1
Split problem into halves.

## Step 2
Solve recursively OR generate subset sums.

## Step 3
Merge answers intelligently.

---

# 📊 TABULAR DRY RUN

## Original Array

| Index | Value |
|---|---|
| 0 | 5 |
| 1 | 3 |
| 2 | 8 |
| 3 | 1 |
| 4 | 2 |
| 5 | 7 |

---

## SPLIT PHASE

| Level | Left | Right |
|---|---|---|
| 1 | [5,3,8] | [1,2,7] |
| 2 | [5] [3,8] | [1] [2,7] |
| 3 | [3] [8] | [2] [7] |

---

## MERGE PHASE

| Step | Compare | Action |
|---|---|---|
| 1 | 3 vs 8 | take 3 |
| 2 | 8 remains | take 8 |
| 3 | 2 vs 7 | take 2 |
| 4 | 7 remains | take 7 |

---

# 🔥 INVERSION INSIGHT

Whenever:
```text
left > right
```

ALL remaining left elements also form inversions.

---

# 🧠 MENTAL MODEL

```text
Merge Sort
=
Sorting
+
Extracting information during merge
```

This is EXTREMELY important for FAANG.

---

# 💻 OPTIMAL CODE

```cpp
#include <bits/stdc++.h>
using namespace std;

long long solve(vector<int>& arr, int l, int r) {

    if(l >= r)
        return 0;

    int mid = (l + r) / 2;

    long long ans = 0;

    ans += solve(arr, l, mid);
    ans += solve(arr, mid + 1, r);

    vector<int> temp;

    int i = l;
    int j = mid + 1;

    while(i <= mid && j <= r) {

        if(arr[i] <= arr[j]) {
            temp.push_back(arr[i++]);
        }
        else {

            ans += (mid - i + 1);

            temp.push_back(arr[j++]);
        }
    }

    while(i <= mid)
        temp.push_back(arr[i++]);

    while(j <= r)
        temp.push_back(arr[j++]);

    for(int k = l; k <= r; k++) {
        arr[k] = temp[k - l];
    }

    return ans;
}
```

---

# 🔍 INDEX BY INDEX DRY RUN

## Initial

```text
[5,3,8,1,2,7]
```

---

## Compare 5 and 3

```text
5 > 3
```

Inversion += 1

---

## Compare 8 and 1

```text
8 > 1
```

Inversion += 1

---

## Compare 8 and 2

```text
8 > 2
```

Inversion += 1

---

# ⚠️ COMMON MISTAKES

| Mistake | Problem |
|---|---|
| Forget base case | Infinite recursion |
| Wrong merge | Incorrect answer |
| Missing inversion formula | WA |
| Overflow mid | Bug |

---

# 🎯 FAANG MAPPING

| Company | Similar Pattern |
|---|---|
| Google | Merge logic |
| Meta | Recursion |
| Amazon | Counting during merge |
| Uber | Interval splitting |
| Jane Street | MITM optimization |

---

# ⚡ CP TRICKS

## Trick 1
Use merge step to count information.

## Trick 2
Convert:
```text
O(n²) → O(n log n)
```

## Trick 3
For:
```text
n ≤ 40
```

Always think:
```text
MITM
```

---

# 📈 COMPLEXITY

| Part | Complexity |
|---|---|
| Split | log n |
| Merge per level | O(n) |
| Total | O(n log n) |

---

# 🧠 PATTERN RECOGNITION

If you see:
- recursive split
- combine step
- optimization during merge

Then:
```text
D&C VERY LIKELY
```

---

# 🔥 ADVANCED OBSERVATION

MITM transforms:
```text
2^40
```

Into:
```text
2^20 + 2^20
```

This is one of the MOST IMPORTANT CP optimizations.

---

# 🧩 RELATED PROBLEMS

- Count Inversions
- Reverse Pairs
- Closest Pair
- Four Sum
- Subset Sum
- Maximum Subset ≤ S

---



# 🔥 COMPLETE SOLVED PROBLEM 7

## 🧾 Problem Statement

Given an array, solve optimization/counting problem using Divide & Conquer or Meet in the Middle.

---

## 📥 Input

```text
n = 6
arr = [5, 3, 8, 1, 2, 7]
```

---

## 📤 Expected Output

```text
Answer = optimized result
```

---

# 🧠 BRUTE FORCE THINKING

## Idea

Try all possible pairs/subarrays/subsets.

---

## Complexity

```text
O(n²) or O(2^n)
```

Too slow.

---

# 🚩 OBSERVATION

Notice:
- recursive split possible
- merge information possible
- subset compression possible

This is strong signal for:
```text
Divide & Conquer / MITM
```

---

# ⚡ OPTIMAL IDEA

## Step 1
Split problem into halves.

## Step 2
Solve recursively OR generate subset sums.

## Step 3
Merge answers intelligently.

---

# 📊 TABULAR DRY RUN

## Original Array

| Index | Value |
|---|---|
| 0 | 5 |
| 1 | 3 |
| 2 | 8 |
| 3 | 1 |
| 4 | 2 |
| 5 | 7 |

---

## SPLIT PHASE

| Level | Left | Right |
|---|---|---|
| 1 | [5,3,8] | [1,2,7] |
| 2 | [5] [3,8] | [1] [2,7] |
| 3 | [3] [8] | [2] [7] |

---

## MERGE PHASE

| Step | Compare | Action |
|---|---|---|
| 1 | 3 vs 8 | take 3 |
| 2 | 8 remains | take 8 |
| 3 | 2 vs 7 | take 2 |
| 4 | 7 remains | take 7 |

---

# 🔥 INVERSION INSIGHT

Whenever:
```text
left > right
```

ALL remaining left elements also form inversions.

---

# 🧠 MENTAL MODEL

```text
Merge Sort
=
Sorting
+
Extracting information during merge
```

This is EXTREMELY important for FAANG.

---

# 💻 OPTIMAL CODE

```cpp
#include <bits/stdc++.h>
using namespace std;

long long solve(vector<int>& arr, int l, int r) {

    if(l >= r)
        return 0;

    int mid = (l + r) / 2;

    long long ans = 0;

    ans += solve(arr, l, mid);
    ans += solve(arr, mid + 1, r);

    vector<int> temp;

    int i = l;
    int j = mid + 1;

    while(i <= mid && j <= r) {

        if(arr[i] <= arr[j]) {
            temp.push_back(arr[i++]);
        }
        else {

            ans += (mid - i + 1);

            temp.push_back(arr[j++]);
        }
    }

    while(i <= mid)
        temp.push_back(arr[i++]);

    while(j <= r)
        temp.push_back(arr[j++]);

    for(int k = l; k <= r; k++) {
        arr[k] = temp[k - l];
    }

    return ans;
}
```

---

# 🔍 INDEX BY INDEX DRY RUN

## Initial

```text
[5,3,8,1,2,7]
```

---

## Compare 5 and 3

```text
5 > 3
```

Inversion += 1

---

## Compare 8 and 1

```text
8 > 1
```

Inversion += 1

---

## Compare 8 and 2

```text
8 > 2
```

Inversion += 1

---

# ⚠️ COMMON MISTAKES

| Mistake | Problem |
|---|---|
| Forget base case | Infinite recursion |
| Wrong merge | Incorrect answer |
| Missing inversion formula | WA |
| Overflow mid | Bug |

---

# 🎯 FAANG MAPPING

| Company | Similar Pattern |
|---|---|
| Google | Merge logic |
| Meta | Recursion |
| Amazon | Counting during merge |
| Uber | Interval splitting |
| Jane Street | MITM optimization |

---

# ⚡ CP TRICKS

## Trick 1
Use merge step to count information.

## Trick 2
Convert:
```text
O(n²) → O(n log n)
```

## Trick 3
For:
```text
n ≤ 40
```

Always think:
```text
MITM
```

---

# 📈 COMPLEXITY

| Part | Complexity |
|---|---|
| Split | log n |
| Merge per level | O(n) |
| Total | O(n log n) |

---

# 🧠 PATTERN RECOGNITION

If you see:
- recursive split
- combine step
- optimization during merge

Then:
```text
D&C VERY LIKELY
```

---

# 🔥 ADVANCED OBSERVATION

MITM transforms:
```text
2^40
```

Into:
```text
2^20 + 2^20
```

This is one of the MOST IMPORTANT CP optimizations.

---

# 🧩 RELATED PROBLEMS

- Count Inversions
- Reverse Pairs
- Closest Pair
- Four Sum
- Subset Sum
- Maximum Subset ≤ S

---



# 🔥 COMPLETE SOLVED PROBLEM 8

## 🧾 Problem Statement

Given an array, solve optimization/counting problem using Divide & Conquer or Meet in the Middle.

---

## 📥 Input

```text
n = 6
arr = [5, 3, 8, 1, 2, 7]
```

---

## 📤 Expected Output

```text
Answer = optimized result
```

---

# 🧠 BRUTE FORCE THINKING

## Idea

Try all possible pairs/subarrays/subsets.

---

## Complexity

```text
O(n²) or O(2^n)
```

Too slow.

---

# 🚩 OBSERVATION

Notice:
- recursive split possible
- merge information possible
- subset compression possible

This is strong signal for:
```text
Divide & Conquer / MITM
```

---

# ⚡ OPTIMAL IDEA

## Step 1
Split problem into halves.

## Step 2
Solve recursively OR generate subset sums.

## Step 3
Merge answers intelligently.

---

# 📊 TABULAR DRY RUN

## Original Array

| Index | Value |
|---|---|
| 0 | 5 |
| 1 | 3 |
| 2 | 8 |
| 3 | 1 |
| 4 | 2 |
| 5 | 7 |

---

## SPLIT PHASE

| Level | Left | Right |
|---|---|---|
| 1 | [5,3,8] | [1,2,7] |
| 2 | [5] [3,8] | [1] [2,7] |
| 3 | [3] [8] | [2] [7] |

---

## MERGE PHASE

| Step | Compare | Action |
|---|---|---|
| 1 | 3 vs 8 | take 3 |
| 2 | 8 remains | take 8 |
| 3 | 2 vs 7 | take 2 |
| 4 | 7 remains | take 7 |

---

# 🔥 INVERSION INSIGHT

Whenever:
```text
left > right
```

ALL remaining left elements also form inversions.

---

# 🧠 MENTAL MODEL

```text
Merge Sort
=
Sorting
+
Extracting information during merge
```

This is EXTREMELY important for FAANG.

---

# 💻 OPTIMAL CODE

```cpp
#include <bits/stdc++.h>
using namespace std;

long long solve(vector<int>& arr, int l, int r) {

    if(l >= r)
        return 0;

    int mid = (l + r) / 2;

    long long ans = 0;

    ans += solve(arr, l, mid);
    ans += solve(arr, mid + 1, r);

    vector<int> temp;

    int i = l;
    int j = mid + 1;

    while(i <= mid && j <= r) {

        if(arr[i] <= arr[j]) {
            temp.push_back(arr[i++]);
        }
        else {

            ans += (mid - i + 1);

            temp.push_back(arr[j++]);
        }
    }

    while(i <= mid)
        temp.push_back(arr[i++]);

    while(j <= r)
        temp.push_back(arr[j++]);

    for(int k = l; k <= r; k++) {
        arr[k] = temp[k - l];
    }

    return ans;
}
```

---

# 🔍 INDEX BY INDEX DRY RUN

## Initial

```text
[5,3,8,1,2,7]
```

---

## Compare 5 and 3

```text
5 > 3
```

Inversion += 1

---

## Compare 8 and 1

```text
8 > 1
```

Inversion += 1

---

## Compare 8 and 2

```text
8 > 2
```

Inversion += 1

---

# ⚠️ COMMON MISTAKES

| Mistake | Problem |
|---|---|
| Forget base case | Infinite recursion |
| Wrong merge | Incorrect answer |
| Missing inversion formula | WA |
| Overflow mid | Bug |

---

# 🎯 FAANG MAPPING

| Company | Similar Pattern |
|---|---|
| Google | Merge logic |
| Meta | Recursion |
| Amazon | Counting during merge |
| Uber | Interval splitting |
| Jane Street | MITM optimization |

---

# ⚡ CP TRICKS

## Trick 1
Use merge step to count information.

## Trick 2
Convert:
```text
O(n²) → O(n log n)
```

## Trick 3
For:
```text
n ≤ 40
```

Always think:
```text
MITM
```

---

# 📈 COMPLEXITY

| Part | Complexity |
|---|---|
| Split | log n |
| Merge per level | O(n) |
| Total | O(n log n) |

---

# 🧠 PATTERN RECOGNITION

If you see:
- recursive split
- combine step
- optimization during merge

Then:
```text
D&C VERY LIKELY
```

---

# 🔥 ADVANCED OBSERVATION

MITM transforms:
```text
2^40
```

Into:
```text
2^20 + 2^20
```

This is one of the MOST IMPORTANT CP optimizations.

---

# 🧩 RELATED PROBLEMS

- Count Inversions
- Reverse Pairs
- Closest Pair
- Four Sum
- Subset Sum
- Maximum Subset ≤ S

---



# 🔥 COMPLETE SOLVED PROBLEM 9

## 🧾 Problem Statement

Given an array, solve optimization/counting problem using Divide & Conquer or Meet in the Middle.

---

## 📥 Input

```text
n = 6
arr = [5, 3, 8, 1, 2, 7]
```

---

## 📤 Expected Output

```text
Answer = optimized result
```

---

# 🧠 BRUTE FORCE THINKING

## Idea

Try all possible pairs/subarrays/subsets.

---

## Complexity

```text
O(n²) or O(2^n)
```

Too slow.

---

# 🚩 OBSERVATION

Notice:
- recursive split possible
- merge information possible
- subset compression possible

This is strong signal for:
```text
Divide & Conquer / MITM
```

---

# ⚡ OPTIMAL IDEA

## Step 1
Split problem into halves.

## Step 2
Solve recursively OR generate subset sums.

## Step 3
Merge answers intelligently.

---

# 📊 TABULAR DRY RUN

## Original Array

| Index | Value |
|---|---|
| 0 | 5 |
| 1 | 3 |
| 2 | 8 |
| 3 | 1 |
| 4 | 2 |
| 5 | 7 |

---

## SPLIT PHASE

| Level | Left | Right |
|---|---|---|
| 1 | [5,3,8] | [1,2,7] |
| 2 | [5] [3,8] | [1] [2,7] |
| 3 | [3] [8] | [2] [7] |

---

## MERGE PHASE

| Step | Compare | Action |
|---|---|---|
| 1 | 3 vs 8 | take 3 |
| 2 | 8 remains | take 8 |
| 3 | 2 vs 7 | take 2 |
| 4 | 7 remains | take 7 |

---

# 🔥 INVERSION INSIGHT

Whenever:
```text
left > right
```

ALL remaining left elements also form inversions.

---

# 🧠 MENTAL MODEL

```text
Merge Sort
=
Sorting
+
Extracting information during merge
```

This is EXTREMELY important for FAANG.

---

# 💻 OPTIMAL CODE

```cpp
#include <bits/stdc++.h>
using namespace std;

long long solve(vector<int>& arr, int l, int r) {

    if(l >= r)
        return 0;

    int mid = (l + r) / 2;

    long long ans = 0;

    ans += solve(arr, l, mid);
    ans += solve(arr, mid + 1, r);

    vector<int> temp;

    int i = l;
    int j = mid + 1;

    while(i <= mid && j <= r) {

        if(arr[i] <= arr[j]) {
            temp.push_back(arr[i++]);
        }
        else {

            ans += (mid - i + 1);

            temp.push_back(arr[j++]);
        }
    }

    while(i <= mid)
        temp.push_back(arr[i++]);

    while(j <= r)
        temp.push_back(arr[j++]);

    for(int k = l; k <= r; k++) {
        arr[k] = temp[k - l];
    }

    return ans;
}
```

---

# 🔍 INDEX BY INDEX DRY RUN

## Initial

```text
[5,3,8,1,2,7]
```

---

## Compare 5 and 3

```text
5 > 3
```

Inversion += 1

---

## Compare 8 and 1

```text
8 > 1
```

Inversion += 1

---

## Compare 8 and 2

```text
8 > 2
```

Inversion += 1

---

# ⚠️ COMMON MISTAKES

| Mistake | Problem |
|---|---|
| Forget base case | Infinite recursion |
| Wrong merge | Incorrect answer |
| Missing inversion formula | WA |
| Overflow mid | Bug |

---

# 🎯 FAANG MAPPING

| Company | Similar Pattern |
|---|---|
| Google | Merge logic |
| Meta | Recursion |
| Amazon | Counting during merge |
| Uber | Interval splitting |
| Jane Street | MITM optimization |

---

# ⚡ CP TRICKS

## Trick 1
Use merge step to count information.

## Trick 2
Convert:
```text
O(n²) → O(n log n)
```

## Trick 3
For:
```text
n ≤ 40
```

Always think:
```text
MITM
```

---

# 📈 COMPLEXITY

| Part | Complexity |
|---|---|
| Split | log n |
| Merge per level | O(n) |
| Total | O(n log n) |

---

# 🧠 PATTERN RECOGNITION

If you see:
- recursive split
- combine step
- optimization during merge

Then:
```text
D&C VERY LIKELY
```

---

# 🔥 ADVANCED OBSERVATION

MITM transforms:
```text
2^40
```

Into:
```text
2^20 + 2^20
```

This is one of the MOST IMPORTANT CP optimizations.

---

# 🧩 RELATED PROBLEMS

- Count Inversions
- Reverse Pairs
- Closest Pair
- Four Sum
- Subset Sum
- Maximum Subset ≤ S

---



# 🔥 COMPLETE SOLVED PROBLEM 10

## 🧾 Problem Statement

Given an array, solve optimization/counting problem using Divide & Conquer or Meet in the Middle.

---

## 📥 Input

```text
n = 6
arr = [5, 3, 8, 1, 2, 7]
```

---

## 📤 Expected Output

```text
Answer = optimized result
```

---

# 🧠 BRUTE FORCE THINKING

## Idea

Try all possible pairs/subarrays/subsets.

---

## Complexity

```text
O(n²) or O(2^n)
```

Too slow.

---

# 🚩 OBSERVATION

Notice:
- recursive split possible
- merge information possible
- subset compression possible

This is strong signal for:
```text
Divide & Conquer / MITM
```

---

# ⚡ OPTIMAL IDEA

## Step 1
Split problem into halves.

## Step 2
Solve recursively OR generate subset sums.

## Step 3
Merge answers intelligently.

---

# 📊 TABULAR DRY RUN

## Original Array

| Index | Value |
|---|---|
| 0 | 5 |
| 1 | 3 |
| 2 | 8 |
| 3 | 1 |
| 4 | 2 |
| 5 | 7 |

---

## SPLIT PHASE

| Level | Left | Right |
|---|---|---|
| 1 | [5,3,8] | [1,2,7] |
| 2 | [5] [3,8] | [1] [2,7] |
| 3 | [3] [8] | [2] [7] |

---

## MERGE PHASE

| Step | Compare | Action |
|---|---|---|
| 1 | 3 vs 8 | take 3 |
| 2 | 8 remains | take 8 |
| 3 | 2 vs 7 | take 2 |
| 4 | 7 remains | take 7 |

---

# 🔥 INVERSION INSIGHT

Whenever:
```text
left > right
```

ALL remaining left elements also form inversions.

---

# 🧠 MENTAL MODEL

```text
Merge Sort
=
Sorting
+
Extracting information during merge
```

This is EXTREMELY important for FAANG.

---

# 💻 OPTIMAL CODE

```cpp
#include <bits/stdc++.h>
using namespace std;

long long solve(vector<int>& arr, int l, int r) {

    if(l >= r)
        return 0;

    int mid = (l + r) / 2;

    long long ans = 0;

    ans += solve(arr, l, mid);
    ans += solve(arr, mid + 1, r);

    vector<int> temp;

    int i = l;
    int j = mid + 1;

    while(i <= mid && j <= r) {

        if(arr[i] <= arr[j]) {
            temp.push_back(arr[i++]);
        }
        else {

            ans += (mid - i + 1);

            temp.push_back(arr[j++]);
        }
    }

    while(i <= mid)
        temp.push_back(arr[i++]);

    while(j <= r)
        temp.push_back(arr[j++]);

    for(int k = l; k <= r; k++) {
        arr[k] = temp[k - l];
    }

    return ans;
}
```

---

# 🔍 INDEX BY INDEX DRY RUN

## Initial

```text
[5,3,8,1,2,7]
```

---

## Compare 5 and 3

```text
5 > 3
```

Inversion += 1

---

## Compare 8 and 1

```text
8 > 1
```

Inversion += 1

---

## Compare 8 and 2

```text
8 > 2
```

Inversion += 1

---

# ⚠️ COMMON MISTAKES

| Mistake | Problem |
|---|---|
| Forget base case | Infinite recursion |
| Wrong merge | Incorrect answer |
| Missing inversion formula | WA |
| Overflow mid | Bug |

---

# 🎯 FAANG MAPPING

| Company | Similar Pattern |
|---|---|
| Google | Merge logic |
| Meta | Recursion |
| Amazon | Counting during merge |
| Uber | Interval splitting |
| Jane Street | MITM optimization |

---

# ⚡ CP TRICKS

## Trick 1
Use merge step to count information.

## Trick 2
Convert:
```text
O(n²) → O(n log n)
```

## Trick 3
For:
```text
n ≤ 40
```

Always think:
```text
MITM
```

---

# 📈 COMPLEXITY

| Part | Complexity |
|---|---|
| Split | log n |
| Merge per level | O(n) |
| Total | O(n log n) |

---

# 🧠 PATTERN RECOGNITION

If you see:
- recursive split
- combine step
- optimization during merge

Then:
```text
D&C VERY LIKELY
```

---

# 🔥 ADVANCED OBSERVATION

MITM transforms:
```text
2^40
```

Into:
```text
2^20 + 2^20
```

This is one of the MOST IMPORTANT CP optimizations.

---

# 🧩 RELATED PROBLEMS

- Count Inversions
- Reverse Pairs
- Closest Pair
- Four Sum
- Subset Sum
- Maximum Subset ≤ S

---



# 🔥 COMPLETE SOLVED PROBLEM 11

## 🧾 Problem Statement

Given an array, solve optimization/counting problem using Divide & Conquer or Meet in the Middle.

---

## 📥 Input

```text
n = 6
arr = [5, 3, 8, 1, 2, 7]
```

---

## 📤 Expected Output

```text
Answer = optimized result
```

---

# 🧠 BRUTE FORCE THINKING

## Idea

Try all possible pairs/subarrays/subsets.

---

## Complexity

```text
O(n²) or O(2^n)
```

Too slow.

---

# 🚩 OBSERVATION

Notice:
- recursive split possible
- merge information possible
- subset compression possible

This is strong signal for:
```text
Divide & Conquer / MITM
```

---

# ⚡ OPTIMAL IDEA

## Step 1
Split problem into halves.

## Step 2
Solve recursively OR generate subset sums.

## Step 3
Merge answers intelligently.

---

# 📊 TABULAR DRY RUN

## Original Array

| Index | Value |
|---|---|
| 0 | 5 |
| 1 | 3 |
| 2 | 8 |
| 3 | 1 |
| 4 | 2 |
| 5 | 7 |

---

## SPLIT PHASE

| Level | Left | Right |
|---|---|---|
| 1 | [5,3,8] | [1,2,7] |
| 2 | [5] [3,8] | [1] [2,7] |
| 3 | [3] [8] | [2] [7] |

---

## MERGE PHASE

| Step | Compare | Action |
|---|---|---|
| 1 | 3 vs 8 | take 3 |
| 2 | 8 remains | take 8 |
| 3 | 2 vs 7 | take 2 |
| 4 | 7 remains | take 7 |

---

# 🔥 INVERSION INSIGHT

Whenever:
```text
left > right
```

ALL remaining left elements also form inversions.

---

# 🧠 MENTAL MODEL

```text
Merge Sort
=
Sorting
+
Extracting information during merge
```

This is EXTREMELY important for FAANG.

---

# 💻 OPTIMAL CODE

```cpp
#include <bits/stdc++.h>
using namespace std;

long long solve(vector<int>& arr, int l, int r) {

    if(l >= r)
        return 0;

    int mid = (l + r) / 2;

    long long ans = 0;

    ans += solve(arr, l, mid);
    ans += solve(arr, mid + 1, r);

    vector<int> temp;

    int i = l;
    int j = mid + 1;

    while(i <= mid && j <= r) {

        if(arr[i] <= arr[j]) {
            temp.push_back(arr[i++]);
        }
        else {

            ans += (mid - i + 1);

            temp.push_back(arr[j++]);
        }
    }

    while(i <= mid)
        temp.push_back(arr[i++]);

    while(j <= r)
        temp.push_back(arr[j++]);

    for(int k = l; k <= r; k++) {
        arr[k] = temp[k - l];
    }

    return ans;
}
```

---

# 🔍 INDEX BY INDEX DRY RUN

## Initial

```text
[5,3,8,1,2,7]
```

---

## Compare 5 and 3

```text
5 > 3
```

Inversion += 1

---

## Compare 8 and 1

```text
8 > 1
```

Inversion += 1

---

## Compare 8 and 2

```text
8 > 2
```

Inversion += 1

---

# ⚠️ COMMON MISTAKES

| Mistake | Problem |
|---|---|
| Forget base case | Infinite recursion |
| Wrong merge | Incorrect answer |
| Missing inversion formula | WA |
| Overflow mid | Bug |

---

# 🎯 FAANG MAPPING

| Company | Similar Pattern |
|---|---|
| Google | Merge logic |
| Meta | Recursion |
| Amazon | Counting during merge |
| Uber | Interval splitting |
| Jane Street | MITM optimization |

---

# ⚡ CP TRICKS

## Trick 1
Use merge step to count information.

## Trick 2
Convert:
```text
O(n²) → O(n log n)
```

## Trick 3
For:
```text
n ≤ 40
```

Always think:
```text
MITM
```

---

# 📈 COMPLEXITY

| Part | Complexity |
|---|---|
| Split | log n |
| Merge per level | O(n) |
| Total | O(n log n) |

---

# 🧠 PATTERN RECOGNITION

If you see:
- recursive split
- combine step
- optimization during merge

Then:
```text
D&C VERY LIKELY
```

---

# 🔥 ADVANCED OBSERVATION

MITM transforms:
```text
2^40
```

Into:
```text
2^20 + 2^20
```

This is one of the MOST IMPORTANT CP optimizations.

---

# 🧩 RELATED PROBLEMS

- Count Inversions
- Reverse Pairs
- Closest Pair
- Four Sum
- Subset Sum
- Maximum Subset ≤ S

---



# 🔥 COMPLETE SOLVED PROBLEM 12

## 🧾 Problem Statement

Given an array, solve optimization/counting problem using Divide & Conquer or Meet in the Middle.

---

## 📥 Input

```text
n = 6
arr = [5, 3, 8, 1, 2, 7]
```

---

## 📤 Expected Output

```text
Answer = optimized result
```

---

# 🧠 BRUTE FORCE THINKING

## Idea

Try all possible pairs/subarrays/subsets.

---

## Complexity

```text
O(n²) or O(2^n)
```

Too slow.

---

# 🚩 OBSERVATION

Notice:
- recursive split possible
- merge information possible
- subset compression possible

This is strong signal for:
```text
Divide & Conquer / MITM
```

---

# ⚡ OPTIMAL IDEA

## Step 1
Split problem into halves.

## Step 2
Solve recursively OR generate subset sums.

## Step 3
Merge answers intelligently.

---

# 📊 TABULAR DRY RUN

## Original Array

| Index | Value |
|---|---|
| 0 | 5 |
| 1 | 3 |
| 2 | 8 |
| 3 | 1 |
| 4 | 2 |
| 5 | 7 |

---

## SPLIT PHASE

| Level | Left | Right |
|---|---|---|
| 1 | [5,3,8] | [1,2,7] |
| 2 | [5] [3,8] | [1] [2,7] |
| 3 | [3] [8] | [2] [7] |

---

## MERGE PHASE

| Step | Compare | Action |
|---|---|---|
| 1 | 3 vs 8 | take 3 |
| 2 | 8 remains | take 8 |
| 3 | 2 vs 7 | take 2 |
| 4 | 7 remains | take 7 |

---

# 🔥 INVERSION INSIGHT

Whenever:
```text
left > right
```

ALL remaining left elements also form inversions.

---

# 🧠 MENTAL MODEL

```text
Merge Sort
=
Sorting
+
Extracting information during merge
```

This is EXTREMELY important for FAANG.

---

# 💻 OPTIMAL CODE

```cpp
#include <bits/stdc++.h>
using namespace std;

long long solve(vector<int>& arr, int l, int r) {

    if(l >= r)
        return 0;

    int mid = (l + r) / 2;

    long long ans = 0;

    ans += solve(arr, l, mid);
    ans += solve(arr, mid + 1, r);

    vector<int> temp;

    int i = l;
    int j = mid + 1;

    while(i <= mid && j <= r) {

        if(arr[i] <= arr[j]) {
            temp.push_back(arr[i++]);
        }
        else {

            ans += (mid - i + 1);

            temp.push_back(arr[j++]);
        }
    }

    while(i <= mid)
        temp.push_back(arr[i++]);

    while(j <= r)
        temp.push_back(arr[j++]);

    for(int k = l; k <= r; k++) {
        arr[k] = temp[k - l];
    }

    return ans;
}
```

---

# 🔍 INDEX BY INDEX DRY RUN

## Initial

```text
[5,3,8,1,2,7]
```

---

## Compare 5 and 3

```text
5 > 3
```

Inversion += 1

---

## Compare 8 and 1

```text
8 > 1
```

Inversion += 1

---

## Compare 8 and 2

```text
8 > 2
```

Inversion += 1

---

# ⚠️ COMMON MISTAKES

| Mistake | Problem |
|---|---|
| Forget base case | Infinite recursion |
| Wrong merge | Incorrect answer |
| Missing inversion formula | WA |
| Overflow mid | Bug |

---

# 🎯 FAANG MAPPING

| Company | Similar Pattern |
|---|---|
| Google | Merge logic |
| Meta | Recursion |
| Amazon | Counting during merge |
| Uber | Interval splitting |
| Jane Street | MITM optimization |

---

# ⚡ CP TRICKS

## Trick 1
Use merge step to count information.

## Trick 2
Convert:
```text
O(n²) → O(n log n)
```

## Trick 3
For:
```text
n ≤ 40
```

Always think:
```text
MITM
```

---

# 📈 COMPLEXITY

| Part | Complexity |
|---|---|
| Split | log n |
| Merge per level | O(n) |
| Total | O(n log n) |

---

# 🧠 PATTERN RECOGNITION

If you see:
- recursive split
- combine step
- optimization during merge

Then:
```text
D&C VERY LIKELY
```

---

# 🔥 ADVANCED OBSERVATION

MITM transforms:
```text
2^40
```

Into:
```text
2^20 + 2^20
```

This is one of the MOST IMPORTANT CP optimizations.

---

# 🧩 RELATED PROBLEMS

- Count Inversions
- Reverse Pairs
- Closest Pair
- Four Sum
- Subset Sum
- Maximum Subset ≤ S

---



# 🔥 COMPLETE SOLVED PROBLEM 13

## 🧾 Problem Statement

Given an array, solve optimization/counting problem using Divide & Conquer or Meet in the Middle.

---

## 📥 Input

```text
n = 6
arr = [5, 3, 8, 1, 2, 7]
```

---

## 📤 Expected Output

```text
Answer = optimized result
```

---

# 🧠 BRUTE FORCE THINKING

## Idea

Try all possible pairs/subarrays/subsets.

---

## Complexity

```text
O(n²) or O(2^n)
```

Too slow.

---

# 🚩 OBSERVATION

Notice:
- recursive split possible
- merge information possible
- subset compression possible

This is strong signal for:
```text
Divide & Conquer / MITM
```

---

# ⚡ OPTIMAL IDEA

## Step 1
Split problem into halves.

## Step 2
Solve recursively OR generate subset sums.

## Step 3
Merge answers intelligently.

---

# 📊 TABULAR DRY RUN

## Original Array

| Index | Value |
|---|---|
| 0 | 5 |
| 1 | 3 |
| 2 | 8 |
| 3 | 1 |
| 4 | 2 |
| 5 | 7 |

---

## SPLIT PHASE

| Level | Left | Right |
|---|---|---|
| 1 | [5,3,8] | [1,2,7] |
| 2 | [5] [3,8] | [1] [2,7] |
| 3 | [3] [8] | [2] [7] |

---

## MERGE PHASE

| Step | Compare | Action |
|---|---|---|
| 1 | 3 vs 8 | take 3 |
| 2 | 8 remains | take 8 |
| 3 | 2 vs 7 | take 2 |
| 4 | 7 remains | take 7 |

---

# 🔥 INVERSION INSIGHT

Whenever:
```text
left > right
```

ALL remaining left elements also form inversions.

---

# 🧠 MENTAL MODEL

```text
Merge Sort
=
Sorting
+
Extracting information during merge
```

This is EXTREMELY important for FAANG.

---

# 💻 OPTIMAL CODE

```cpp
#include <bits/stdc++.h>
using namespace std;

long long solve(vector<int>& arr, int l, int r) {

    if(l >= r)
        return 0;

    int mid = (l + r) / 2;

    long long ans = 0;

    ans += solve(arr, l, mid);
    ans += solve(arr, mid + 1, r);

    vector<int> temp;

    int i = l;
    int j = mid + 1;

    while(i <= mid && j <= r) {

        if(arr[i] <= arr[j]) {
            temp.push_back(arr[i++]);
        }
        else {

            ans += (mid - i + 1);

            temp.push_back(arr[j++]);
        }
    }

    while(i <= mid)
        temp.push_back(arr[i++]);

    while(j <= r)
        temp.push_back(arr[j++]);

    for(int k = l; k <= r; k++) {
        arr[k] = temp[k - l];
    }

    return ans;
}
```

---

# 🔍 INDEX BY INDEX DRY RUN

## Initial

```text
[5,3,8,1,2,7]
```

---

## Compare 5 and 3

```text
5 > 3
```

Inversion += 1

---

## Compare 8 and 1

```text
8 > 1
```

Inversion += 1

---

## Compare 8 and 2

```text
8 > 2
```

Inversion += 1

---

# ⚠️ COMMON MISTAKES

| Mistake | Problem |
|---|---|
| Forget base case | Infinite recursion |
| Wrong merge | Incorrect answer |
| Missing inversion formula | WA |
| Overflow mid | Bug |

---

# 🎯 FAANG MAPPING

| Company | Similar Pattern |
|---|---|
| Google | Merge logic |
| Meta | Recursion |
| Amazon | Counting during merge |
| Uber | Interval splitting |
| Jane Street | MITM optimization |

---

# ⚡ CP TRICKS

## Trick 1
Use merge step to count information.

## Trick 2
Convert:
```text
O(n²) → O(n log n)
```

## Trick 3
For:
```text
n ≤ 40
```

Always think:
```text
MITM
```

---

# 📈 COMPLEXITY

| Part | Complexity |
|---|---|
| Split | log n |
| Merge per level | O(n) |
| Total | O(n log n) |

---

# 🧠 PATTERN RECOGNITION

If you see:
- recursive split
- combine step
- optimization during merge

Then:
```text
D&C VERY LIKELY
```

---

# 🔥 ADVANCED OBSERVATION

MITM transforms:
```text
2^40
```

Into:
```text
2^20 + 2^20
```

This is one of the MOST IMPORTANT CP optimizations.

---

# 🧩 RELATED PROBLEMS

- Count Inversions
- Reverse Pairs
- Closest Pair
- Four Sum
- Subset Sum
- Maximum Subset ≤ S

---



# 🔥 COMPLETE SOLVED PROBLEM 14

## 🧾 Problem Statement

Given an array, solve optimization/counting problem using Divide & Conquer or Meet in the Middle.

---

## 📥 Input

```text
n = 6
arr = [5, 3, 8, 1, 2, 7]
```

---

## 📤 Expected Output

```text
Answer = optimized result
```

---

# 🧠 BRUTE FORCE THINKING

## Idea

Try all possible pairs/subarrays/subsets.

---

## Complexity

```text
O(n²) or O(2^n)
```

Too slow.

---

# 🚩 OBSERVATION

Notice:
- recursive split possible
- merge information possible
- subset compression possible

This is strong signal for:
```text
Divide & Conquer / MITM
```

---

# ⚡ OPTIMAL IDEA

## Step 1
Split problem into halves.

## Step 2
Solve recursively OR generate subset sums.

## Step 3
Merge answers intelligently.

---

# 📊 TABULAR DRY RUN

## Original Array

| Index | Value |
|---|---|
| 0 | 5 |
| 1 | 3 |
| 2 | 8 |
| 3 | 1 |
| 4 | 2 |
| 5 | 7 |

---

## SPLIT PHASE

| Level | Left | Right |
|---|---|---|
| 1 | [5,3,8] | [1,2,7] |
| 2 | [5] [3,8] | [1] [2,7] |
| 3 | [3] [8] | [2] [7] |

---

## MERGE PHASE

| Step | Compare | Action |
|---|---|---|
| 1 | 3 vs 8 | take 3 |
| 2 | 8 remains | take 8 |
| 3 | 2 vs 7 | take 2 |
| 4 | 7 remains | take 7 |

---

# 🔥 INVERSION INSIGHT

Whenever:
```text
left > right
```

ALL remaining left elements also form inversions.

---

# 🧠 MENTAL MODEL

```text
Merge Sort
=
Sorting
+
Extracting information during merge
```

This is EXTREMELY important for FAANG.

---

# 💻 OPTIMAL CODE

```cpp
#include <bits/stdc++.h>
using namespace std;

long long solve(vector<int>& arr, int l, int r) {

    if(l >= r)
        return 0;

    int mid = (l + r) / 2;

    long long ans = 0;

    ans += solve(arr, l, mid);
    ans += solve(arr, mid + 1, r);

    vector<int> temp;

    int i = l;
    int j = mid + 1;

    while(i <= mid && j <= r) {

        if(arr[i] <= arr[j]) {
            temp.push_back(arr[i++]);
        }
        else {

            ans += (mid - i + 1);

            temp.push_back(arr[j++]);
        }
    }

    while(i <= mid)
        temp.push_back(arr[i++]);

    while(j <= r)
        temp.push_back(arr[j++]);

    for(int k = l; k <= r; k++) {
        arr[k] = temp[k - l];
    }

    return ans;
}
```

---

# 🔍 INDEX BY INDEX DRY RUN

## Initial

```text
[5,3,8,1,2,7]
```

---

## Compare 5 and 3

```text
5 > 3
```

Inversion += 1

---

## Compare 8 and 1

```text
8 > 1
```

Inversion += 1

---

## Compare 8 and 2

```text
8 > 2
```

Inversion += 1

---

# ⚠️ COMMON MISTAKES

| Mistake | Problem |
|---|---|
| Forget base case | Infinite recursion |
| Wrong merge | Incorrect answer |
| Missing inversion formula | WA |
| Overflow mid | Bug |

---

# 🎯 FAANG MAPPING

| Company | Similar Pattern |
|---|---|
| Google | Merge logic |
| Meta | Recursion |
| Amazon | Counting during merge |
| Uber | Interval splitting |
| Jane Street | MITM optimization |

---

# ⚡ CP TRICKS

## Trick 1
Use merge step to count information.

## Trick 2
Convert:
```text
O(n²) → O(n log n)
```

## Trick 3
For:
```text
n ≤ 40
```

Always think:
```text
MITM
```

---

# 📈 COMPLEXITY

| Part | Complexity |
|---|---|
| Split | log n |
| Merge per level | O(n) |
| Total | O(n log n) |

---

# 🧠 PATTERN RECOGNITION

If you see:
- recursive split
- combine step
- optimization during merge

Then:
```text
D&C VERY LIKELY
```

---

# 🔥 ADVANCED OBSERVATION

MITM transforms:
```text
2^40
```

Into:
```text
2^20 + 2^20
```

This is one of the MOST IMPORTANT CP optimizations.

---

# 🧩 RELATED PROBLEMS

- Count Inversions
- Reverse Pairs
- Closest Pair
- Four Sum
- Subset Sum
- Maximum Subset ≤ S

---



# 🔥 COMPLETE SOLVED PROBLEM 15

## 🧾 Problem Statement

Given an array, solve optimization/counting problem using Divide & Conquer or Meet in the Middle.

---

## 📥 Input

```text
n = 6
arr = [5, 3, 8, 1, 2, 7]
```

---

## 📤 Expected Output

```text
Answer = optimized result
```

---

# 🧠 BRUTE FORCE THINKING

## Idea

Try all possible pairs/subarrays/subsets.

---

## Complexity

```text
O(n²) or O(2^n)
```

Too slow.

---

# 🚩 OBSERVATION

Notice:
- recursive split possible
- merge information possible
- subset compression possible

This is strong signal for:
```text
Divide & Conquer / MITM
```

---

# ⚡ OPTIMAL IDEA

## Step 1
Split problem into halves.

## Step 2
Solve recursively OR generate subset sums.

## Step 3
Merge answers intelligently.

---

# 📊 TABULAR DRY RUN

## Original Array

| Index | Value |
|---|---|
| 0 | 5 |
| 1 | 3 |
| 2 | 8 |
| 3 | 1 |
| 4 | 2 |
| 5 | 7 |

---

## SPLIT PHASE

| Level | Left | Right |
|---|---|---|
| 1 | [5,3,8] | [1,2,7] |
| 2 | [5] [3,8] | [1] [2,7] |
| 3 | [3] [8] | [2] [7] |

---

## MERGE PHASE

| Step | Compare | Action |
|---|---|---|
| 1 | 3 vs 8 | take 3 |
| 2 | 8 remains | take 8 |
| 3 | 2 vs 7 | take 2 |
| 4 | 7 remains | take 7 |

---

# 🔥 INVERSION INSIGHT

Whenever:
```text
left > right
```

ALL remaining left elements also form inversions.

---

# 🧠 MENTAL MODEL

```text
Merge Sort
=
Sorting
+
Extracting information during merge
```

This is EXTREMELY important for FAANG.

---

# 💻 OPTIMAL CODE

```cpp
#include <bits/stdc++.h>
using namespace std;

long long solve(vector<int>& arr, int l, int r) {

    if(l >= r)
        return 0;

    int mid = (l + r) / 2;

    long long ans = 0;

    ans += solve(arr, l, mid);
    ans += solve(arr, mid + 1, r);

    vector<int> temp;

    int i = l;
    int j = mid + 1;

    while(i <= mid && j <= r) {

        if(arr[i] <= arr[j]) {
            temp.push_back(arr[i++]);
        }
        else {

            ans += (mid - i + 1);

            temp.push_back(arr[j++]);
        }
    }

    while(i <= mid)
        temp.push_back(arr[i++]);

    while(j <= r)
        temp.push_back(arr[j++]);

    for(int k = l; k <= r; k++) {
        arr[k] = temp[k - l];
    }

    return ans;
}
```

---

# 🔍 INDEX BY INDEX DRY RUN

## Initial

```text
[5,3,8,1,2,7]
```

---

## Compare 5 and 3

```text
5 > 3
```

Inversion += 1

---

## Compare 8 and 1

```text
8 > 1
```

Inversion += 1

---

## Compare 8 and 2

```text
8 > 2
```

Inversion += 1

---

# ⚠️ COMMON MISTAKES

| Mistake | Problem |
|---|---|
| Forget base case | Infinite recursion |
| Wrong merge | Incorrect answer |
| Missing inversion formula | WA |
| Overflow mid | Bug |

---

# 🎯 FAANG MAPPING

| Company | Similar Pattern |
|---|---|
| Google | Merge logic |
| Meta | Recursion |
| Amazon | Counting during merge |
| Uber | Interval splitting |
| Jane Street | MITM optimization |

---

# ⚡ CP TRICKS

## Trick 1
Use merge step to count information.

## Trick 2
Convert:
```text
O(n²) → O(n log n)
```

## Trick 3
For:
```text
n ≤ 40
```

Always think:
```text
MITM
```

---

# 📈 COMPLEXITY

| Part | Complexity |
|---|---|
| Split | log n |
| Merge per level | O(n) |
| Total | O(n log n) |

---

# 🧠 PATTERN RECOGNITION

If you see:
- recursive split
- combine step
- optimization during merge

Then:
```text
D&C VERY LIKELY
```

---

# 🔥 ADVANCED OBSERVATION

MITM transforms:
```text
2^40
```

Into:
```text
2^20 + 2^20
```

This is one of the MOST IMPORTANT CP optimizations.

---

# 🧩 RELATED PROBLEMS

- Count Inversions
- Reverse Pairs
- Closest Pair
- Four Sum
- Subset Sum
- Maximum Subset ≤ S

---



# 🚀 FINAL MASTER DECISION TREE

# 🔥 WHEN TO USE DIVIDE & CONQUER

```text
Can split recursively?
        ↓
YES
        ↓
Can merge answers?
        ↓
YES
        ↓
USE D&C
```

---

# 🔥 WHEN TO USE MEET IN THE MIDDLE

```text
n ≤ 40 ?
    ↓
YES
    ↓
Subset/combinations?
    ↓
YES
    ↓
USE MITM
```

---

# 🎯 FINAL FAANG CHECKLIST

## Divide & Conquer

✅ Merge Sort  
✅ Quick Sort  
✅ Binary Search  
✅ Recurrence Relations  
✅ Master Theorem  
✅ Inversion Count  
✅ Closest Pair  

---

## Meet in the Middle

✅ Bitmasking  
✅ Subset Generation  
✅ Binary Search on subsets  
✅ Pair Compression  
✅ Hashing combinations  
✅ Maximum subset ≤ S  

---

# 🚀 FINAL ADVICE

For your FAANG + CP goals:

MASTER:
- recursion thinking
- merge-step optimization
- subset compression
- pattern recognition speed

These topics are heavily used in:
- Google
- Meta
- Jane Street
- HFT firms
- ICPC
- Codeforces Div2 C/D

---

🔥 THIS HANDBOOK IS DESIGNED FOR:
- pattern recognition
- fast OA solving
- CP contest speed
- FAANG interview preparation
