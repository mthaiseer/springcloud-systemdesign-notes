# Binary Search CP + FAANG Phase-Wise Handbook

> Goal: learn binary search by **forms**, not by memorizing problems. For every problem: recognition signal → brute force → binary search idea → C++ → index-by-index dry run.

## Clickable Index

- [Phase 1 — Easy Foundation: Index Boundary](#phase-1--easy-foundation-index-boundary)
  - [First 1 in Binary Array](#first-1-in-binary-array)
  - [Lower Bound](#lower-bound)
  - [Upper Bound](#upper-bound)
  - [Search Insert Position](#search-insert-position)
- [Phase 2 — Medium: Classic Array Binary Search](#phase-2--medium-classic-array-binary-search)
  - [Search in Rotated Sorted Array](#search-in-rotated-sorted-array)
  - [Find Minimum in Rotated Sorted Array](#find-minimum-in-rotated-sorted-array)
  - [Peak Element](#peak-element)
- [Phase 3 — Medium/Hard: Binary Search on Answer](#phase-3--mediumhard-binary-search-on-answer)
  - [Split Array Largest Sum / Painter Partition](#split-array-largest-sum--painter-partition)
  - [Factory Machines / Minimum Time](#factory-machines--minimum-time)
  - [Aggressive Cows](#aggressive-cows)
  - [Minimize Maximum Gap After Adding K Points](#minimize-maximum-gap-after-adding-k-points)
- [Phase 4 — Hard: Kth / Counting Problems](#phase-4--hard-kth--counting-problems)
  - [Kth Smallest Pair Sum](#kth-smallest-pair-sum)
  - [Kth Smallest in Multiplication Table](#kth-smallest-in-multiplication-table)
- [Phase 5 — CP Form: Binary Search on Every Start](#phase-5--cp-form-binary-search-on-every-start)
  - [Count Subarrays with At Most K Zeros](#count-subarrays-with-at-most-k-zeros)
  - [Maximum Ones After At Most K Flips](#maximum-ones-after-at-most-k-flips)
- [Phase 6 — Real Domain Binary Search](#phase-6--real-domain-binary-search)
  - [Square Root with Precision](#square-root-with-precision)
- [Phase 7 — Ternary Search / Unimodal Function](#phase-7--ternary-search--unimodal-function)
  - [Freefall / Minimum Time Function](#freefall--minimum-time-function)

---

# 0. Binary Search Master Map


## 0.1 Binary Search Core Idea
Binary search works when the search space has a monotonic structure. Usually the answer pattern looks like one of these:

```text
false false false true true true   -> first true
true true true false false false   -> last true
```

## 0.2 First True vs Last True

| Form | Meaning | Move when condition true | Typical Use |
|---|---|---|---|
| First True | smallest value that works | `ans=mid, r=mid-1` | minimum capacity/time/value |
| Last True | largest value that works | `ans=mid, l=mid+1` | maximum distance/length/value |

## 0.3 Forms of Binary Search

```text
1. Index boundary       -> lower_bound / upper_bound / first 1
2. Sorted array decision -> rotated array / peak / minimum
3. Binary search answer -> minimum possible max / minimum time / maximum distance
4. Kth counting         -> count <= x, then find first count >= k
5. Per-start BS         -> for every l, find farthest valid r
6. Real BS              -> precision answer
7. Ternary search       -> unimodal function minimum/maximum
```

## 0.4 Universal Checklist

```text
1. What is the search space? index / answer / value / real number
2. What is the predicate? can(x), count(x), valid(mid)
3. Is it monotonic? false->true or true->false
4. Need minimum working value? first true
5. Need maximum working value? last true
6. How to calculate predicate efficiently? greedy / prefix / counting / upper_bound
```

---

# Phase 1 — Easy Foundation: Index Boundary


## First 1 in Binary Array

**Problem link:** [First 1 in Binary Array](https://leetcode.com/problems/find-first-and-last-position-of-element-in-sorted-array/)


### Problem Description
Given a binary sorted array like [0,0,0,1,1], find the first index containing 1.


### Input
```text
n=5, a=[0,0,0,1,1]
```

### Output
```text
3
```


### Brute Force Idea
Scan left to right and return first 1. O(n).


### Binary Search Idea
Predicate: a[i] == 1 becomes false,false,true,true. Find first true.


### Recognition Signal

```text
Sorted/monotonic index decision.
```


### C++ Code
```cpp
int firstOne(vector<int>& a) {
    int n = a.size();
    int l = 0, r = n - 1, ans = n;
    while (l <= r) {
        int mid = l + (r - l) / 2;
        if (a[mid] == 1) {
            ans = mid;
            r = mid - 1;
        } else {
            l = mid + 1;
        }
    }
    return ans == n ? -1 : ans;
}
```


### Step-by-Step Dry Run
| Step | l | r | mid | a[mid] | Decision | ans |
|---|---:|---:|---:|---:|---|---:|
| 1 | 0 | 4 | 2 | 0 | false, move right `l=3` | n |
| 2 | 3 | 4 | 3 | 1 | true, save `ans=3`, move left `r=2` | 3 |
Final answer = 3.


### Mental Map
```text
Identify monotonic condition -> choose first true / last true -> update l,r carefully -> prove answer remains inside range.
```

---


## Lower Bound

**Problem link:** [Lower Bound](https://leetcode.com/problems/search-insert-position/)


### Problem Description
Find first index i such that a[i] >= target.


### Input
```text
a=[1,3,5,6], target=5
```

### Output
```text
2
```


### Brute Force Idea
Scan and return first a[i] >= target. O(n).


### Binary Search Idea
Predicate a[i] >= target is monotonic. Find first true.


### Recognition Signal

```text
Sorted/monotonic index decision.
```


### C++ Code
```cpp
int lowerBound(vector<int>& a, int target) {
    int l = 0, r = a.size();
    while (l < r) {
        int mid = l + (r - l) / 2;
        if (a[mid] >= target) r = mid;
        else l = mid + 1;
    }
    return l;
}
```


### Step-by-Step Dry Run
| Step | l | r | mid | a[mid] | Decision |
|---|---:|---:|---:|---:|---|
| 1 | 0 | 4 | 2 | 5 | `5 >= 5`, r=2 |
| 2 | 0 | 2 | 1 | 3 | `3 < 5`, l=2 |
Stop: l=r=2. Lower bound index = 2.


### Mental Map
```text
Identify monotonic condition -> choose first true / last true -> update l,r carefully -> prove answer remains inside range.
```

---


## Upper Bound

**Problem link:** [Upper Bound](https://cplusplus.com/reference/algorithm/upper_bound/)


### Problem Description
Find first index i such that a[i] > target.


### Input
```text
a=[1,2,2,2,4], target=2
```

### Output
```text
4
```


### Brute Force Idea
Scan and return first a[i] > target. O(n).


### Binary Search Idea
Predicate a[i] > target is monotonic. Find first true.


### Recognition Signal

```text
Sorted/monotonic index decision.
```


### C++ Code
```cpp
int upperBound(vector<int>& a, int target) {
    int l = 0, r = a.size();
    while (l < r) {
        int mid = l + (r - l) / 2;
        if (a[mid] > target) r = mid;
        else l = mid + 1;
    }
    return l;
}
```


### Step-by-Step Dry Run
| Step | l | r | mid | a[mid] | Decision |
|---|---:|---:|---:|---:|---|
| 1 | 0 | 5 | 2 | 2 | `2 > 2` false, l=3 |
| 2 | 3 | 5 | 4 | 4 | true, r=4 |
| 3 | 3 | 4 | 3 | 2 | false, l=4 |
Stop: l=r=4. Upper bound index = 4.


### Mental Map
```text
Identify monotonic condition -> choose first true / last true -> update l,r carefully -> prove answer remains inside range.
```

---


## Search Insert Position

**Problem link:** [Search Insert Position](https://leetcode.com/problems/search-insert-position/)


### Problem Description
Return index where target exists, or where it should be inserted to keep array sorted.


### Input
```text
a=[1,3,5,6], target=2
```

### Output
```text
1
```


### Brute Force Idea
Scan until first a[i] >= target. O(n).


### Binary Search Idea
Same as lower_bound(target).


### Recognition Signal

```text
Sorted/monotonic index decision.
```


### C++ Code
```cpp
int searchInsert(vector<int>& a, int target) {
    int l = 0, r = a.size();
    while (l < r) {
        int mid = l + (r - l) / 2;
        if (a[mid] >= target) r = mid;
        else l = mid + 1;
    }
    return l;
}
```


### Step-by-Step Dry Run
For `a=[1,3,5,6], target=2`: mid=2 value 5 => go left; mid=1 value 3 => go left; mid=0 value 1 => go right. Stop at index 1.


### Mental Map
```text
Identify monotonic condition -> choose first true / last true -> update l,r carefully -> prove answer remains inside range.
```

---

# Phase 2 — Medium: Classic Array Binary Search


## Search in Rotated Sorted Array

**Problem link:** [Search in Rotated Sorted Array](https://leetcode.com/problems/search-in-rotated-sorted-array/)


### Problem Description
Sorted array rotated at unknown pivot. Find target index or -1.


### Input
```text
a=[4,5,6,7,0,1,2], target=0
```

### Output
```text
4
```


### Brute Force Idea
Linear search every index. O(n).


### Binary Search Idea
At each mid, one side is sorted. Decide whether target lies inside sorted half.


### Recognition Signal

```text
Sorted/monotonic index decision.
```


### C++ Code
```cpp
int search(vector<int>& a, int target) {
    int l = 0, r = (int)a.size() - 1;
    while (l <= r) {
        int mid = l + (r - l) / 2;
        if (a[mid] == target) return mid;

        if (a[l] <= a[mid]) { // left half sorted
            if (a[l] <= target && target < a[mid]) r = mid - 1;
            else l = mid + 1;
        } else { // right half sorted
            if (a[mid] < target && target <= a[r]) l = mid + 1;
            else r = mid - 1;
        }
    }
    return -1;
}
```


### Step-by-Step Dry Run
| Step | l | r | mid | a[mid] | Sorted side | Decision |
|---|---:|---:|---:|---:|---|---|
| 1 | 0 | 6 | 3 | 7 | left `[4..7]` | target 0 not inside, l=4 |
| 2 | 4 | 6 | 5 | 1 | left `[0..1]` | target 0 inside, r=4 |
| 3 | 4 | 4 | 4 | 0 | found | return 4 |


### Mental Map
```text
Identify monotonic condition -> choose first true / last true -> update l,r carefully -> prove answer remains inside range.
```

---


## Find Minimum in Rotated Sorted Array

**Problem link:** [Find Minimum in Rotated Sorted Array](https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/)


### Problem Description
Find minimum element in rotated sorted unique array.


### Input
```text
a=[4,5,6,7,0,1,2]
```

### Output
```text
0
```


### Brute Force Idea
Scan and take min. O(n).


### Binary Search Idea
Compare a[mid] with a[r]. If a[mid] > a[r], minimum is right. Else left including mid.


### Recognition Signal

```text
Sorted/monotonic index decision.
```


### C++ Code
```cpp
int findMin(vector<int>& a) {
    int l = 0, r = (int)a.size() - 1;
    while (l < r) {
        int mid = l + (r - l) / 2;
        if (a[mid] > a[r]) l = mid + 1;
        else r = mid;
    }
    return a[l];
}
```


### Step-by-Step Dry Run
| Step | l | r | mid | a[mid] | a[r] | Decision |
|---|---:|---:|---:|---:|---:|---|
| 1 | 0 | 6 | 3 | 7 | 2 | `7>2`, min right, l=4 |
| 2 | 4 | 6 | 5 | 1 | 2 | `1<=2`, min left incl mid, r=5 |
| 3 | 4 | 5 | 4 | 0 | 1 | `0<=1`, r=4 |
Stop: l=4, min=0.


### Mental Map
```text
Identify monotonic condition -> choose first true / last true -> update l,r carefully -> prove answer remains inside range.
```

---


## Peak Element

**Problem link:** [Peak Element](https://leetcode.com/problems/find-peak-element/)


### Problem Description
Find any index i such that nums[i] is greater than its neighbors.


### Input
```text
a=[1,2,1,3,5,6,4]
```

### Output
```text
1 or 5
```


### Brute Force Idea
Scan and test both neighbors. O(n).


### Binary Search Idea
If a[mid] < a[mid+1], slope goes up, peak exists right. Else peak exists left including mid.


### Recognition Signal

```text
Sorted/monotonic index decision.
```


### C++ Code
```cpp
int findPeakElement(vector<int>& a) {
    int l = 0, r = (int)a.size() - 1;
    while (l < r) {
        int mid = l + (r - l) / 2;
        if (a[mid] < a[mid + 1]) l = mid + 1;
        else r = mid;
    }
    return l;
}
```


### Step-by-Step Dry Run
For `[1,2,1,3,5,6,4]`: mid=3 value 3, next 5 => rising, go right. mid=5 value 6, next 4 => falling, go left including mid. mid=4 value 5, next 6 => rising, go right. Stop at 5.


### Mental Map
```text
Identify monotonic condition -> choose first true / last true -> update l,r carefully -> prove answer remains inside range.
```

---

# Phase 3 — Medium/Hard: Binary Search on Answer


## Split Array Largest Sum / Painter Partition

**Problem link:** [Split Array Largest Sum / Painter Partition](https://leetcode.com/problems/split-array-largest-sum/)


### Problem Description
Split array into k non-empty parts minimizing maximum part sum.


### Input
```text
a=[7,2,5,10,8], k=2
```

### Output
```text
18
```


### Brute Force Idea
Try all partitions. Exponential / O(n*k*sum) DP possible.


### Binary Search Idea
Answer x = max allowed segment sum. If can split using <=k parts, x works. Find minimum x.


### Recognition Signal

```text
Minimum value that satisfies `can(x)` => first true.
```


### C++ Code
```cpp
bool canSplit(vector<int>& a, int k, long long limit) {
    int parts = 1;
    long long sum = 0;
    for (int x : a) {
        if (sum + x > limit) {
            parts++;
            sum = x;
        } else sum += x;
    }
    return parts <= k;
}

long long splitArray(vector<int>& a, int k) {
    long long l = *max_element(a.begin(), a.end());
    long long r = accumulate(a.begin(), a.end(), 0LL);
    while (l < r) {
        long long mid = l + (r - l) / 2;
        if (canSplit(a, k, mid)) r = mid;
        else l = mid + 1;
    }
    return l;
}
```


### Step-by-Step Dry Run
Check limit 17: partitions `[7,2,5]`, `[10]`, `[8]` => 3 parts > 2, not possible. Check 18: `[7,2,5]`, `[10,8]` => 2 parts, possible. Minimum becomes 18.


### Mental Map
```text
Identify monotonic condition -> choose first true / last true -> update l,r carefully -> prove answer remains inside range.
```

---


## Factory Machines / Minimum Time

**Problem link:** [Factory Machines / Minimum Time](https://cses.fi/problemset/task/1620)


### Problem Description
Given machine times, find minimum time to produce at least target products.


### Input
```text
machines=[2,3,7], target=10
```

### Output
```text
12
```


### Brute Force Idea
Simulate time one by one. Too slow.


### Binary Search Idea
For time x, products=sum(x/t[i]). If products>=target, x works. Find first true.


### Recognition Signal

```text
Minimum value that satisfies `can(x)` => first true.
```


### C++ Code
```cpp
bool canMake(vector<long long>& t, long long need, long long time) {
    long long made = 0;
    for (long long machine : t) {
        made += time / machine;
        if (made >= need) return true;
    }
    return false;
}

long long minimumTime(vector<long long>& t, long long need) {
    long long l = 0, r = *min_element(t.begin(), t.end()) * need;
    while (l < r) {
        long long mid = l + (r - l) / 2;
        if (canMake(t, need, mid)) r = mid;
        else l = mid + 1;
    }
    return l;
}
```


### Step-by-Step Dry Run
For time 11: `11/2 + 11/3 + 11/7 = 5+3+1=9`, not enough. For time 12: `6+4+1=11`, enough. First enough time = 12.


### Mental Map
```text
Identify monotonic condition -> choose first true / last true -> update l,r carefully -> prove answer remains inside range.
```

---


## Aggressive Cows

**Problem link:** [Aggressive Cows](https://www.spoj.com/problems/AGGRCOW/)


### Problem Description
Place cows in stalls maximizing minimum distance between any two cows.


### Input
```text
stalls=[1,2,4,8,9], cows=3
```

### Output
```text
3
```


### Brute Force Idea
Try all cow placements. Combinational.


### Binary Search Idea
For distance d, greedily place cows. If possible, d works. Find last true.


### Recognition Signal

```text
Maximum value that still satisfies `can(x)` => last true.
```


### C++ Code
```cpp
bool canPlace(vector<int>& stalls, int cows, int dist) {
    int placed = 1;
    int last = stalls[0];
    for (int i = 1; i < stalls.size(); i++) {
        if (stalls[i] - last >= dist) {
            placed++;
            last = stalls[i];
        }
    }
    return placed >= cows;
}

int aggressiveCows(vector<int>& stalls, int cows) {
    sort(stalls.begin(), stalls.end());
    int l = 0, r = stalls.back() - stalls.front(), ans = 0;
    while (l <= r) {
        int mid = l + (r - l) / 2;
        if (canPlace(stalls, cows, mid)) {
            ans = mid;
            l = mid + 1;
        } else r = mid - 1;
    }
    return ans;
}
```


### Step-by-Step Dry Run
Try distance 4: place at 1, then 8 only => 2 cows, fail. Try distance 3: place at 1, 4, 8 => 3 cows, success. Last successful distance = 3.


### Mental Map
```text
Identify monotonic condition -> choose first true / last true -> update l,r carefully -> prove answer remains inside range.
```

---


## Minimize Maximum Gap After Adding K Points

**Problem link:** [Minimize Maximum Gap After Adding K Points](https://www.geeksforgeeks.org/minimize-the-maximum-difference-between-adjacent-elements-after-adding-k-elements/)


### Problem Description
Add at most k points between sorted positions to minimize maximum adjacent gap.


### Input
```text
pos=[0,50], k=1
```

### Output
```text
25
```


### Brute Force Idea
Try all placements. Hard continuous search.


### Binary Search Idea
For max gap x, points needed in gap d is ceil(d/x)-1. If total needed <= k, x works. Find minimum x over real numbers.


### Recognition Signal

```text
Minimum value that satisfies `can(x)` => first true.
```


### C++ Code
```cpp
bool can(vector<int>& pos, int k, double x) {
    long long need = 0;
    for (int i = 1; i < pos.size(); i++) {
        double d = pos[i] - pos[i - 1];
        need += (long long)ceil(d / x) - 1;
    }
    return need <= k;
}

double minimizeMaxGap(vector<int>& pos, int k) {
    double l = 0, r = pos.back() - pos.front();
    for (int it = 0; it < 80; it++) {
        double mid = (l + r) / 2.0;
        if (can(pos, k, mid)) r = mid;
        else l = mid;
    }
    return r;
}
```


### Step-by-Step Dry Run
`pos=[0,50], k=1`. For x=25, d=50, need=ceil(50/25)-1=2-1=1, possible. For x=20, need=ceil(2.5)-1=3-1=2, not possible. So answer moves toward 25.


### Mental Map
```text
Identify monotonic condition -> choose first true / last true -> update l,r carefully -> prove answer remains inside range.
```

---

# Phase 4 — Hard: Kth / Counting Problems


## Kth Smallest Pair Sum

**Problem link:** [Kth Smallest Pair Sum](https://leetcode.com/problems/find-k-pairs-with-smallest-sums/)


### Problem Description
Given sorted A and B, find kth smallest value among all A[i]+B[j].


### Input
```text
A=[1,7,11], B=[2,4,6], k=5
```

### Output
```text
13
```


### Brute Force Idea
Generate all n*m sums, sort, return kth. O(nm log nm).


### Binary Search Idea
For sum x, count pairs with sum <= x using upper_bound/two pointers. Find first x with count>=k.


### Recognition Signal

```text
Kth smallest by value => count values `<= x`, then first x where count >= k.
```


### C++ Code
```cpp
long long countPairsLE(vector<long long>& A, vector<long long>& B, long long x) {
    long long cnt = 0;
    for (long long a : A) {
        cnt += upper_bound(B.begin(), B.end(), x - a) - B.begin();
    }
    return cnt;
}

long long kthPairSum(vector<long long>& A, vector<long long>& B, long long k) {
    sort(A.begin(), A.end());
    sort(B.begin(), B.end());
    long long l = A[0] + B[0], r = A.back() + B.back();
    while (l < r) {
        long long mid = l + (r - l) / 2;
        if (countPairsLE(A, B, mid) >= k) r = mid;
        else l = mid + 1;
    }
    return l;
}
```


### Step-by-Step Dry Run
For x=11 with A=[1,7,11], B=[2,4,6]: a=1 allows B<=10 => 3 pairs; a=7 allows B<=4 => 2 pairs; a=11 allows B<=0 => 0 pairs. Count=5, so kth<=11? Actually 5th pair sum is 11 for this input. For other k, binary search adjusts by count.


### Mental Map
```text
Identify monotonic condition -> choose first true / last true -> update l,r carefully -> prove answer remains inside range.
```

---


## Kth Smallest in Multiplication Table

**Problem link:** [Kth Smallest in Multiplication Table](https://leetcode.com/problems/kth-smallest-number-in-multiplication-table/)


### Problem Description
In m x n multiplication table, find kth smallest number.


### Input
```text
m=3,n=3,k=5
```

### Output
```text
3
```


### Brute Force Idea
Generate all m*n values and sort. O(mn log mn).


### Binary Search Idea
For value x, count numbers <=x: sum min(n, x/i). Find first x with count>=k.


### Recognition Signal

```text
Kth smallest by value => count values `<= x`, then first x where count >= k.
```


### C++ Code
```cpp
long long countLE(int m, int n, int x) {
    long long cnt = 0;
    for (int row = 1; row <= m; row++) {
        cnt += min(n, x / row);
    }
    return cnt;
}

int findKthNumber(int m, int n, int k) {
    int l = 1, r = m * n;
    while (l < r) {
        int mid = l + (r - l) / 2;
        if (countLE(m, n, mid) >= k) r = mid;
        else l = mid + 1;
    }
    return l;
}
```


### Step-by-Step Dry Run
For m=3,n=3,x=3: row1 has min(3,3/1)=3, row2 has 1, row3 has 1. Count=5. Since k=5, value 3 works. Smaller x=2 gives count 3, so answer is 3.


### Mental Map
```text
Identify monotonic condition -> choose first true / last true -> update l,r carefully -> prove answer remains inside range.
```

---

# Phase 5 — CP Form: Binary Search on Every Start


## Count Subarrays with At Most K Zeros

**Problem link:** [Count Subarrays with At Most K Zeros](https://leetcode.com/problems/max-consecutive-ones-iii/)


### Problem Description
Count subarrays where number of zeros <= k.


### Input
```text
a=[1,0,1,0,1], k=1
```

### Output
```text
12
```


### Brute Force Idea
Check every subarray and count zeros. O(n^2).


### Binary Search Idea
For each start l, binary search maximum r such that zeros(l..r)<=k using prefix zeros.


### Recognition Signal

```text
For each left boundary, valid right boundary is monotonic.
```


### C++ Code
```cpp
long long countSubarraysAtMostKZeros(vector<int>& a, int k) {
    int n = a.size();
    vector<int> pref(n + 1, 0);
    for (int i = 0; i < n; i++) pref[i + 1] = pref[i] + (a[i] == 0);

    long long ans = 0;
    for (int l = 0; l < n; l++) {
        int lo = l, hi = n - 1, best = l - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int zeros = pref[mid + 1] - pref[l];
            if (zeros <= k) {
                best = mid;
                lo = mid + 1;
            } else hi = mid - 1;
        }
        ans += best - l + 1;
    }
    return ans;
}
```


### Step-by-Step Dry Run
For l=0 in `[1,0,1,0,1]`, k=1: farthest r is 2 because `[1,0,1]` has one zero, but `[1,0,1,0]` has two zeros. Add 3 subarrays starting at 0. Repeat for every l.


### Mental Map
```text
Identify monotonic condition -> choose first true / last true -> update l,r carefully -> prove answer remains inside range.
```

---


## Maximum Ones After At Most K Flips

**Problem link:** [Maximum Ones After At Most K Flips](https://leetcode.com/problems/max-consecutive-ones-iii/)


### Problem Description
Find longest subarray containing at most k zeros.


### Input
```text
a=[1,1,0,0,1,1,1,0], k=2
```

### Output
```text
7
```


### Brute Force Idea
Try every subarray. O(n^2).


### Binary Search Idea
For each start l, binary search farthest valid r using prefix zeros. Take max length.


### Recognition Signal

```text
For each left boundary, valid right boundary is monotonic.
```


### C++ Code
```cpp
int longestOnes(vector<int>& a, int k) {
    int n = a.size();
    vector<int> pref(n + 1, 0);
    for (int i = 0; i < n; i++) pref[i + 1] = pref[i] + (a[i] == 0);

    int ans = 0;
    for (int l = 0; l < n; l++) {
        int lo = l, hi = n - 1, best = l - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int zeros = pref[mid + 1] - pref[l];
            if (zeros <= k) {
                best = mid;
                lo = mid + 1;
            } else hi = mid - 1;
        }
        ans = max(ans, best - l + 1);
    }
    return ans;
}
```


### Step-by-Step Dry Run
For l=0 in `[1,1,0,0,1,1,1,0]`, k=2: farthest r=6, length=7. For r=7 zeros become 3, invalid. Current best=7.


### Mental Map
```text
Identify monotonic condition -> choose first true / last true -> update l,r carefully -> prove answer remains inside range.
```

---

# Phase 6 — Real Domain Binary Search


## Square Root with Precision

**Problem link:** [Square Root with Precision](https://leetcode.com/problems/sqrtx/)


### Problem Description
Find square root of number with required precision.


### Input
```text
x=10
```

### Output
```text
3.162277...
```


### Brute Force Idea
Increase answer by tiny step. Slow.


### Binary Search Idea
Binary search real value mid. If mid*mid <= x, move right, else left.


### Recognition Signal

```text
Minimum value that satisfies `can(x)` => first true.
```


### C++ Code
```cpp
double sqrtPrecision(double x) {
    double l = 0, r = max(1.0, x);
    for (int it = 0; it < 100; it++) {
        double mid = (l + r) / 2.0;
        if (mid * mid <= x) l = mid;
        else r = mid;
    }
    return l;
}
```


### Step-by-Step Dry Run
For x=10: mid=5 => 25 too high, r=5. mid=2.5 => 6.25 valid, l=2.5. mid=3.75 => 14.06 high. It keeps shrinking until 3.162277...


### Mental Map
```text
Identify monotonic condition -> choose first true / last true -> update l,r carefully -> prove answer remains inside range.
```

---

# Phase 7 — Ternary Search / Unimodal Function


## Freefall / Minimum Time Function

**Problem link:** [Freefall / Minimum Time Function](https://atcoder.jp/contests/abc279/tasks/abc279_d)


### Problem Description
Minimize a function that decreases then increases, e.g. A/sqrt(g+1)+B*g.


### Input
```text
A=10, B=1
```

### Output
```text
minimum value
```


### Brute Force Idea
Try all possible integer g in range. Too slow if range is huge.


### Binary Search Idea
Function is unimodal. Use ternary search or binary search on slope/discrete derivative.


### Recognition Signal

```text
Function decreases then increases => unimodal, use ternary search.
```


### C++ Code
```cpp
double f(double A, double B, long long g) {
    return A / sqrt(g + 1.0) + B * g;
}

double solveFreefall(double A, double B) {
    long long l = 0, r = (long long)4e18;
    while (r - l > 5) {
        long long m1 = l + (r - l) / 3;
        long long m2 = r - (r - l) / 3;
        if (f(A, B, m1) <= f(A, B, m2)) r = m2;
        else l = m1;
    }
    double ans = f(A, B, l);
    for (long long g = l; g <= r; g++) ans = min(ans, f(A, B, g));
    return ans;
}
```


### Step-by-Step Dry Run
Check two middle points m1 and m2. If f(m1) <= f(m2), the minimum is not after m2, so move r=m2. Otherwise the minimum is not before m1, so move l=m1. Finish by brute checking last few integer g values.


### Mental Map
```text
Identify monotonic condition -> choose first true / last true -> update l,r carefully -> prove answer remains inside range.
```

---

# Final Revision Plan

```text
Day 1-2  : Phase 1 + templates
Day 3-5  : Phase 2 classic array BS
Day 6-10 : Phase 3 answer BS
Day 11-13: Phase 4 kth/counting
Day 14   : Phase 5 per-start BS
Day 15   : Real BS + ternary search
Repeat   : Solve mixed random problems until you identify the form within 5 seconds.
```
