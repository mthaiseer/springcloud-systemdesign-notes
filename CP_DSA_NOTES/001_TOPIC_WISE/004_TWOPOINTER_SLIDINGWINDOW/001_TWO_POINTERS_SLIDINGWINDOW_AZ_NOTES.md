# Two Pointers — Detailed CP + FAANG Notes

> Built from your handwritten notes: Two Pointers Form I, Form II, Form III, variations, and 3Sum.

---

## Clickable Index

- [0. Master Mental Map](#0-master-mental-map)
  - [0.1 Universal Templates I Follow](#01-universal-templates-i-follow)
- [1. Form I — Opposite Direction Pointers](#1-form-i--opposite-direction-pointers)
  - [1.1 Palindrome Check](#11-palindrome-check)
  - [1.2 Two Sum in Sorted Array](#12-two-sum-in-sorted-array)
  - [1.3 Container With Most Water](#13-container-with-most-water)
- [2. Form II — Same Direction / Variable Window](#2-form-ii--same-direction--variable-window)
  - [2.1 Longest Subarray With At Most K Zeros](#21-longest-subarray-with-at-most-k-zeros)
  - [2.2 Count Subarrays With At Most K Distinct Elements](#22-count-subarrays-with-at-most-k-distinct-elements)
  - [2.3 Count Subarrays With Exactly K Distinct Elements](#23-count-subarrays-with-exactly-k-distinct-elements)
  - [2.4 Count Subarrays With At Least K Distinct Elements](#24-count-subarrays-with-at-least-k-distinct-elements)
- [3. Form III — Multi-list Traversal](#3-form-iii--multi-list-traversal)
  - [3.1 Is Subsequence](#31-is-subsequence)
  - [3.2 Intersection of Sorted Posting Lists](#32-intersection-of-sorted-posting-lists)
- [4. 3Sum Pattern](#4-3sum-pattern)
  - [4.1 3Sum Exists / List Triplets](#41-3sum-exists--list-triplets)
  - [4.2 Count 3Sum With Duplicates](#42-count-3sum-with-duplicates)
- [5. Recognition Cheat Sheet](#5-recognition-cheat-sheet)

---

# 0. Master Mental Map

Two pointers is used when we can move one or two indices intelligently instead of trying all combinations.

```text
Two Pointers
│
├── Form I: opposite direction
│   ├── left = 0, right = n - 1
│   ├── sorted array / palindrome / pair sum
│   └── move one pointer based on comparison
│
├── Form II: same direction / variable window
│   ├── tail = left boundary
│   ├── head = right boundary
│   ├── window length is not fixed
│   └── maintain a condition using count/frequency/map
│
└── Form III: multiple lists / strings
    ├── one pointer per list/string
    ├── move pointer with smaller value or matched character
    └── useful for subsequence and intersection
```

## Sliding Window vs Two Pointers

```text
Sliding window:
  fixed length window

Two pointers / variable window:
  length is not fixed
  window expands and shrinks based on condition
```


## 0.1 Universal Templates I Follow

Use these templates as the base skeleton for every problem in this note. The problem-specific code only changes the window state, the `valid/invalid` condition, and how `ans` is updated.

### Template A — Fixed Size Sliding Window

Use this when the window length is exactly `k`.

```cpp
template <typename T, typename W>
W sliding_window_fixed(vector<T>& input, int window_size) {
    W window;
    W ans;

    // 1) Build first window: [0 .. window_size - 1]
    for (int i = 0; i < window_size; i++) {
        // append input[i] to window
    }

    ans = window; // or answer computed from first window

    // 2) Slide one step at a time
    for (int right = window_size; right < (int)input.size(); ++right) {
        int left = right - window_size;

        // remove input[left] from window
        // append input[right] to window

        ans = optimal(ans, window);
    }

    return ans;
}
```

Mental model:

```text
window size never changes
remove one from left + add one from right
```

Example problems:

```text
maximum sum of size k
first negative in every window of size k
sliding window maximum
```

---

### Template B — Flexible Window for Longest Valid Subarray

Use this when the question asks for the longest subarray/substring satisfying some condition.

```cpp
template <typename T, typename W>
W sliding_window_flexible_longest(vector<T>& input) {
    W window;
    W ans;

    int left = 0;

    for (int right = 0; right < (int)input.size(); ++right) {
        // append input[right] to window

        while (invalid(window)) {
            // remove input[left] from window
            ++left;
        }

        // window is guaranteed valid here
        ans = max(ans, window);
    }

    return ans;
}
```

Mental model:

```text
expand right first
if window becomes invalid, shrink left until valid again
update answer after shrinking
```

Example problems:

```text
longest subarray with at most k zeros
longest substring with at most k distinct characters
longest subarray with sum <= k when all numbers are non-negative
```

---

### Template C — Flexible Window for Shortest Valid Subarray

Use this when the question asks for the shortest subarray/substring satisfying a condition.

```cpp
template <typename T, typename W>
W sliding_window_flexible_shortest(vector<T>& input) {
    W window;
    W ans;

    int left = 0;

    for (int right = 0; right < (int)input.size(); ++right) {
        // append input[right] to window

        while (valid(window)) {
            // window is guaranteed valid here
            ans = min(ans, window);

            // remove input[left] from window
            ++left;
        }
    }

    return ans;
}
```

Mental model:

```text
expand until valid
once valid, shrink as much as possible
update answer before removing left
```

Example problems:

```text
minimum size subarray sum >= target
minimum window substring
shortest subarray containing all required characters
```

---

### Template D — Opposite Direction Two Pointers

Use this when the array/string is sorted, or when matching from both ends makes sense.

```cpp
void two_pointers_opposite(vector<int>& arr) {
    int left = 0;
    int right = (int)arr.size() - 1;

    while (left < right) {
        // Process current elements
        int current = process(arr[left], arr[right]);

        // Update pointers based on condition
        if (condition(arr[left], arr[right])) {
            left++;
        } else {
            right--;
        }
    }
}
```

Mental model:

```text
look at two ends
use sorted/order property to eliminate one side
```

Example problems:

```text
palindrome check
two sum in sorted array
container with most water
3Sum after fixing one element
```

---

### Template E — Same Direction Two Pointers

Use this when one pointer scans and the other pointer follows.

```cpp
void two_pointers_same(vector<int>& arr) {
    int slow = 0;
    int fast = 0;

    while (fast < (int)arr.size()) {
        // Process current elements
        int current = process(arr[slow], arr[fast]);

        // Update slow pointer based on condition
        if (condition(arr[slow], arr[fast])) {
            slow++;
        }

        // Fast pointer always moves forward
        fast++;
    }
}
```

Mental model:

```text
fast explores new elements
slow removes old elements / tracks matched progress
```

Example problems:

```text
remove duplicates
move zeros
is subsequence
merge/intersection style traversal
```

## Core Amortization Idea

Even if the code has nested `while` loops, each pointer moves only forward.

```text
head moves at most n times
tail moves at most n times
Total = O(n)
```

So this is not O(n²) if both pointers never move backward.

---

# 1. Form I — Opposite Direction Pointers

## Pattern

```text
left  = 0
right = n - 1

while left < right:
    check arr[left] and arr[right]
    move left or right depending on condition
```

## When to Use

```text
sorted array
pair sum
palindrome
container / area problems
need to eliminate impossible options
```

---

## 1.1 Palindrome Check

**Template used:** Template D — Opposite Direction Two Pointers.

### Problem Statement

Given a string `s`, check whether it is a palindrome.

A palindrome reads the same from left to right and right to left.

### Input

```text
s = "racecar"
```

### Expected Output

```text
true
```

### Brute Force Idea

Reverse the string and compare.

```text
s       = racecar
reverse = racecar
same    = true
```

### Brute Force Complexity

```text
Time  : O(n)
Space : O(n)
```

### Optimal Two Pointer Idea

Compare first and last characters.

```text
s[left] must equal s[right]
```

If equal, move both pointers inward.

If not equal, it is not a palindrome.

### Why This Works

Palindrome property is symmetric.

```text
index 0 must match index n-1
index 1 must match index n-2
index 2 must match index n-3
```

So we do not need extra space.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool isPalindrome(const string& s) {
    int left = 0;
    int right = (int)s.size() - 1;

    while (left < right) {
        if (s[left] != s[right]) {
            return false;
        }
        left++;
        right--;
    }

    return true;
}

int main() {
    string s = "racecar";
    cout << (isPalindrome(s) ? "true" : "false") << '\n';
    return 0;
}
```

### Index-by-Index Dry Run

```text
s = racecar

left right chars      result
0    6     r == r     ok, move inward
1    5     a == a     ok, move inward
2    4     c == c     ok, move inward
3    3     stop       left == right

Answer = true
```

---

## 1.2 Two Sum in Sorted Array

**Template used:** Template D — Opposite Direction Two Pointers.

### Problem Statement

Given a sorted array and a target, check whether there exists a pair whose sum is equal to target.

### Input

```text
arr = [2, 7, 11, 15, 19]
target = 18
```

### Expected Output

```text
true
Pair = 7, 11
```

### Brute Force Idea

Try every pair.

```text
2 + 7  = 9
2 + 11 = 13
2 + 15 = 17
2 + 19 = 21
7 + 11 = 18  found
```

### Brute Force Complexity

```text
Time  : O(n²)
Space : O(1)
```

### Optimal Two Pointer Idea

Because the array is sorted:

```text
left  = smallest element
right = largest element
```

Check sum:

```text
sum = arr[left] + arr[right]
```

Decision:

```text
sum == target -> found
sum > target  -> right-- because current largest is too big
sum < target  -> left++ because current smallest is too small
```

### Why `right--` When Sum Is Too Large?

Example:

```text
arr = [2, 7, 11, 15, 19]
target = 18
left = 0, right = 4
sum = 2 + 19 = 21 > 18
```

Since `2` is already the smallest number, pairing `19` with any number greater than `2` will only make the sum even larger.

```text
7 + 19  > 21
11 + 19 > 21
15 + 19 > 21
```

So `19` cannot be part of a valid pair. Discard it.

### Why `left++` When Sum Is Too Small?

```text
left = 0, right = 3
sum = 2 + 15 = 17 < 18
```

Since `15` is the current largest allowed number, pairing `2` with anything smaller than `15` will be even smaller.

So `2` cannot make target with this or any smaller right value. Discard it.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool twoSumSorted(const vector<int>& arr, int target) {
    int left = 0;
    int right = (int)arr.size() - 1;

    while (left < right) {
        int sum = arr[left] + arr[right];

        if (sum == target) {
            return true;
        } else if (sum > target) {
            right--;
        } else {
            left++;
        }
    }

    return false;
}

int main() {
    vector<int> arr = {2, 7, 11, 15, 19};
    int target = 18;

    cout << (twoSumSorted(arr, target) ? "true" : "false") << '\n';
    return 0;
}
```

### Index-by-Index Dry Run

```text
arr = [2, 7, 11, 15, 19]
target = 18

left right arr[left] arr[right] sum decision
0    4     2         19         21  sum > target, right--
0    3     2         15         17  sum < target, left++
1    3     7         15         22  sum > target, right--
1    2     7         11         18  found

Answer = true
```

---

## 1.3 Container With Most Water

**Template used:** Template D — Opposite Direction Two Pointers.

### Problem Statement

Given an array `height`, choose two lines such that together with the x-axis they form a container holding maximum water.

Area formula:

```text
area = min(height[left], height[right]) * (right - left)
```

### Input

```text
height = [3, 1, 2, 5, 1]
```

### Expected Output

```text
12
```

Explanation:

```text
left = 0, right = 4
min(3, 1) * 4 = 4

left = 0, right = 3
min(3, 5) * 3 = 9

But for example height = [3, 1, 2, 5, 1]
best can be index 0 and index 3:
min(3,5) * 3 = 9
```

For a clearer max example:

```text
height = [3, 1, 2, 5, 4]
best = index 0 and index 4
min(3,4) * 4 = 12
```

### Brute Force Idea

Try every pair `(i, j)`.

```text
for i = 0 to n-1
  for j = i+1 to n-1
    area = min(height[i], height[j]) * (j - i)
```

### Brute Force Complexity

```text
Time  : O(n²)
Space : O(1)
```

### Optimal Two Pointer Idea

Start from widest container.

```text
left = 0
right = n - 1
```

At every step:

```text
area = min(height[left], height[right]) * width
```

Move the pointer with smaller height.

### Why Move the Smaller Height?

Area is limited by the smaller line.

```text
area = smaller_height * width
```

When we move a pointer, width always decreases.

So to get a better area, we must try to increase the smaller height.

If `height[left] < height[right]`, moving `right` cannot improve the limiting height because `left` is still the bottleneck.

Therefore move `left`.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int maxArea(vector<int>& height) {
    int left = 0;
    int right = (int)height.size() - 1;
    int ans = 0;

    while (left < right) {
        int width = right - left;
        int h = min(height[left], height[right]);
        ans = max(ans, h * width);

        if (height[left] < height[right]) {
            left++;
        } else {
            right--;
        }
    }

    return ans;
}

int main() {
    vector<int> height = {3, 1, 2, 5, 4};
    cout << maxArea(height) << '\n';
    return 0;
}
```

### Index-by-Index Dry Run

```text
height = [3, 1, 2, 5, 4]

left right h[left] h[right] width area ans move
0    4     3       4        4     12   12  left++
1    4     1       4        3     3    12  left++
2    4     2       4        2     4    12  left++
3    4     5       4        1     4    12  right--
stop

Answer = 12
```

---

# 2. Form II — Same Direction / Variable Window

## Pattern

```text
head = -1
tail = 0

while tail < n:
    while head + 1 < n and adding arr[head + 1] keeps condition valid:
        head++
        add arr[head]

    use current window [tail ... head]

    if tail > head:
        tail++
        head = tail - 1
    else:
        remove arr[tail]
        tail++
```

## Important Empty Window Condition

```text
head = tail - 1
```

This means current window is empty.

Example:

```text
tail = 3
head = 2
window = empty
```

## Why This Is O(n)

```text
head only increases from -1 to n-1
tail only increases from 0 to n
```

Total pointer movement is O(n).

---

## 2.1 Longest Subarray With At Most K Zeros

**Template used:** Template B — Flexible Window for Longest Valid Subarray.

### Problem Statement

Given a binary array, flip at most `k` zeros to ones. Find the maximum length subarray containing only ones after flips.

### Input

```text
arr = [0, 1, 0, 1, 0, 1, 1, 0]
k = 2
```

### Expected Output

```text
6
```

One best subarray is:

```text
[1, 0, 1, 0, 1, 1]
```

Flip the two zeros.

### Brute Force Idea

Try every start index and extend until zeros exceed `k`.

```cpp
int brute(vector<int>& arr, int k) {
    int n = arr.size();
    int ans = 0;

    for (int i = 0; i < n; i++) {
        int zero = 0;
        for (int j = i; j < n; j++) {
            if (arr[j] == 0) zero++;
            if (zero <= k) {
                ans = max(ans, j - i + 1);
            } else {
                break;
            }
        }
    }

    return ans;
}
```

### Brute Force Result on Input

```text
start = 0 -> [0,1,0,1] length 4
start = 1 -> [1,0,1,0,1,1] length 6
start = 2 -> [0,1,0,1,1] length 5
...
Best = 6
```

### Brute Force Complexity

```text
Time  : O(n²)
Space : O(1)
```

### Optimal Idea

Maintain a window where:

```text
number_of_zeros <= k
```

Expand `head` while adding next element keeps zero count `<= k`.

Then current window is valid.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int longestOnesAfterFlips(vector<int>& arr, int k) {
    int n = arr.size();
    int left = 0;
    int zero = 0;
    int ans = 0;

    // Template B: flexible window for longest valid subarray
    for (int right = 0; right < n; right++) {
        // append arr[right] to window
        if (arr[right] == 0) zero++;

        // invalid(window): zeros are more than k
        while (zero > k) {
            // remove arr[left] from window
            if (arr[left] == 0) zero--;
            left++;
        }

        // window [left..right] is valid here
        ans = max(ans, right - left + 1);
    }

    return ans;
}
int main() {
    vector<int> arr = {0, 1, 0, 1, 0, 1, 1, 0};
    int k = 2;
    cout << longestOnesAfterFlips(arr, k) << '\n';
    return 0;
}
```

### Index-by-Index Dry Run

```text
arr = [0, 1, 0, 1, 0, 1, 1, 0]
k = 2

Start: tail = 0, head = -1, zero = 0, ans = 0

Tail = 0:
  add index 0 -> 0, zero = 1, window [0..0]
  add index 1 -> 1, zero = 1, window [0..1]
  add index 2 -> 0, zero = 2, window [0..2]
  add index 3 -> 1, zero = 2, window [0..3]
  next index 4 is 0, zero would become 3 > 2, stop
  ans = max(0, 4) = 4
  remove arr[0] = 0, zero = 1, tail = 1

Tail = 1:
  add index 4 -> 0, zero = 2, window [1..4]
  add index 5 -> 1, zero = 2, window [1..5]
  add index 6 -> 1, zero = 2, window [1..6]
  next index 7 is 0, zero would become 3 > 2, stop
  ans = max(4, 6) = 6
  remove arr[1] = 1, zero = 2, tail = 2

Tail = 2:
  next index 7 is 0, zero would become 3 > 2, stop
  current window [2..6], length = 5
  ans = 6
  remove arr[2] = 0, zero = 1, tail = 3

Tail = 3:
  add index 7 -> 0, zero = 2, window [3..7]
  ans = max(6, 5) = 6
  remove arr[3] = 1, zero = 2, tail = 4

Tail = 4:
  window [4..7], length = 4, ans = 6
  remove arr[4] = 0, zero = 1, tail = 5

Tail = 5:
  window [5..7], length = 3, ans = 6
  remove arr[5] = 1, zero = 1, tail = 6

Tail = 6:
  window [6..7], length = 2, ans = 6
  remove arr[6] = 1, zero = 1, tail = 7

Tail = 7:
  window [7..7], length = 1, ans = 6
  remove arr[7] = 0, zero = 0, tail = 8

Answer = 6
```

---

## 2.2 Count Subarrays With At Most K Distinct Elements

**Template used:** Flexible window counting variant. Same expansion/shrink idea as Template B, but answer adds `right - left + 1`.

### Problem Statement

Given an array, count the number of subarrays having at most `k` distinct elements.

### Input

```text
arr = [1, 2, 1, 3]
k = 2
```

### Expected Output

```text
8
```

Valid subarrays:

```text
[1]
[1,2]
[1,2,1]
[2]
[2,1]
[1]
[1,3]
[3]
```

### Brute Force Idea

Try all subarrays and count distinct elements using set/map.

```cpp
long long bruteAtMostKDistinct(vector<int>& arr, int k) {
    int n = arr.size();
    long long ans = 0;

    for (int i = 0; i < n; i++) {
        unordered_map<int, int> freq;
        int distinct = 0;

        for (int j = i; j < n; j++) {
            if (freq[arr[j]] == 0) distinct++;
            freq[arr[j]]++;

            if (distinct <= k) ans++;
            else break;
        }
    }

    return ans;
}
```

### Brute Force Result

```text
start 0: [1], [1,2], [1,2,1] -> 3
start 1: [2], [2,1]          -> 2
start 2: [1], [1,3]          -> 2
start 3: [3]                 -> 1
Total = 8
```

### Optimal Idea

Maintain a window `[tail ... head]` with:

```text
distinctCount <= k
```

For every `tail`, after expanding `head` as far as possible, all subarrays starting at `tail` and ending between `tail` and `head` are valid.

Number of valid subarrays starting at `tail`:

```text
head - tail + 1
```

### Why Add `head - tail + 1`?

If current valid window is:

```text
arr[tail ... head]
```

Then these are valid:

```text
arr[tail ... tail]
arr[tail ... tail+1]
arr[tail ... tail+2]
...
arr[tail ... head]
```

Count = `head - tail + 1`.

### C++ Code Using map

```cpp
#include <bits/stdc++.h>
using namespace std;

long long atMostKDistinct(vector<int>& arr, int k) {
    int n = arr.size();
    int head = -1;
    int tail = 0;
    int distinct = 0;
    long long ans = 0;
    map<int, int> freq;

    while (tail < n) {
        while (head + 1 < n && (freq[arr[head + 1]] > 0 || distinct < k)) {
            head++;
            if (freq[arr[head]] == 0) distinct++;
            freq[arr[head]]++;
        }

        ans += head - tail + 1;

        if (tail > head) {
            tail++;
            head = tail - 1;
        } else {
            freq[arr[tail]]--;
            if (freq[arr[tail]] == 0) distinct--;
            tail++;
        }
    }

    return ans;
}

int main() {
    vector<int> arr = {1, 2, 1, 3};
    int k = 2;
    cout << atMostKDistinct(arr, k) << '\n';
    return 0;
}
```

### C++ Code Using Frequency Array

Use this if values are small, for example `arr[i] <= 100000`.

```cpp
#include <bits/stdc++.h>
using namespace std;

long long atMostKDistinct(vector<int>& arr, int k) {
    int n = arr.size();
    vector<int> freq(100001, 0);
    int head = -1;
    int tail = 0;
    int distinct = 0;
    long long ans = 0;

    while (tail < n) {
        while (head + 1 < n && (freq[arr[head + 1]] > 0 || distinct < k)) {
            head++;
            if (freq[arr[head]] == 0) distinct++;
            freq[arr[head]]++;
        }

        ans += head - tail + 1;

        if (tail > head) {
            tail++;
            head = tail - 1;
        } else {
            freq[arr[tail]]--;
            if (freq[arr[tail]] == 0) distinct--;
            tail++;
        }
    }

    return ans;
}
```

### Important Note About Clearing Frequency Array

If frequency array is global and there are many test cases, usually you may think you must clear the whole array.

But in this two-pointer template, when `tail` moves, it removes elements from the window one by one.

So after one test case finishes, all frequencies that entered the window should become zero again.

Still, in interviews, using local `unordered_map` or local `vector<int>` is safer and clearer.

### Index-by-Index Dry Run

```text
arr = [1, 2, 1, 3]
k = 2

Start:
head = -1, tail = 0, distinct = 0, ans = 0
freq = {}

Tail = 0:
  add 1 -> freq[1]=1, distinct=1, head=0
  add 2 -> freq[2]=1, distinct=2, head=1
  add 1 -> freq[1]=2, distinct=2, head=2
  next 3 is new and distinct would become 3 > 2, stop
  valid subarrays starting at 0 = head-tail+1 = 2-0+1 = 3
  ans = 3
  remove arr[0]=1 -> freq[1]=1, distinct=2
  tail = 1

Tail = 1:
  next 3 is new and distinct would become 3 > 2, stop
  valid subarrays starting at 1 = 2-1+1 = 2
  ans = 5
  remove arr[1]=2 -> freq[2]=0, distinct=1
  tail = 2

Tail = 2:
  add 3 -> freq[3]=1, distinct=2, head=3
  valid subarrays starting at 2 = 3-2+1 = 2
  ans = 7
  remove arr[2]=1 -> freq[1]=0, distinct=1
  tail = 3

Tail = 3:
  valid subarrays starting at 3 = 3-3+1 = 1
  ans = 8
  remove arr[3]=3 -> freq[3]=0, distinct=0
  tail = 4

Answer = 8
```

---

## 2.3 Count Subarrays With Exactly K Distinct Elements

### Problem Statement

Given an array, count subarrays having exactly `k` distinct elements.

### Input

```text
arr = [1, 2, 1, 3]
k = 2
```

### Expected Output

```text
5
```

Valid subarrays:

```text
[1,2]
[1,2,1]
[2,1]
[1,3]
[2,1,3] is not valid because it has 3 distinct elements
```

Actually valid exactly 2 distinct:

```text
[1,2]
[1,2,1]
[2,1]
[1,3]
```

So output for this input is:

```text
4
```

### Brute Force Idea

Try all subarrays and count distinct.

```cpp
long long bruteExactlyK(vector<int>& arr, int k) {
    int n = arr.size();
    long long ans = 0;

    for (int i = 0; i < n; i++) {
        unordered_map<int, int> freq;
        int distinct = 0;

        for (int j = i; j < n; j++) {
            if (freq[arr[j]] == 0) distinct++;
            freq[arr[j]]++;

            if (distinct == k) ans++;
        }
    }

    return ans;
}
```

### Optimal Idea

Use the classic transformation:

```text
Exactly K = AtMost(K) - AtMost(K - 1)
```

### Why This Works

`AtMost(K)` counts subarrays with:

```text
1 distinct
2 distinct
...
K distinct
```

`AtMost(K - 1)` counts subarrays with:

```text
1 distinct
2 distinct
...
K-1 distinct
```

Subtracting removes all smaller cases and leaves exactly `K`.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long atMostKDistinct(vector<int>& arr, int k) {
    if (k < 0) return 0;

    int n = arr.size();
    unordered_map<int, int> freq;
    int distinct = 0;
    int left = 0;
    long long ans = 0;

    for (int right = 0; right < n; right++) {
        if (freq[arr[right]] == 0) distinct++;
        freq[arr[right]]++;

        while (distinct > k) {
            freq[arr[left]]--;
            if (freq[arr[left]] == 0) distinct--;
            left++;
        }

        ans += right - left + 1;
    }

    return ans;
}

long long exactlyKDistinct(vector<int>& arr, int k) {
    return atMostKDistinct(arr, k) - atMostKDistinct(arr, k - 1);
}

int main() {
    vector<int> arr = {1, 2, 1, 3};
    int k = 2;
    cout << exactlyKDistinct(arr, k) << '\n';
    return 0;
}
```

### Index-by-Index Dry Run

```text
arr = [1, 2, 1, 3]
k = 2

AtMost(2) = 8
AtMost(1):

right=0, add 1 -> window [1], count += 1
right=1, add 2 -> distinct=2 > 1
         remove 1 -> window [2], count += 1
right=2, add 1 -> distinct=2 > 1
         remove 2 -> window [1], count += 1
right=3, add 3 -> distinct=2 > 1
         remove 1 -> window [3], count += 1

AtMost(1) = 4

Exactly(2) = AtMost(2) - AtMost(1)
           = 8 - 4
           = 4
```

---

## 2.4 Count Subarrays With At Least K Distinct Elements

### Problem Statement

Given an array, count subarrays having at least `k` distinct elements.

### Input

```text
arr = [1, 2, 1, 3]
k = 2
```

### Expected Output

```text
6
```

### Brute Force Idea

Try all subarrays and count distinct.

### Optimal Idea

Use total subarrays minus subarrays with at most `k - 1` distinct elements.

```text
AtLeast(K) = TotalSubarrays - AtMost(K - 1)
```

Total subarrays:

```text
n * (n + 1) / 2
```

### Why This Works

Every subarray is either:

```text
has distinct < K
or
has distinct >= K
```

`AtMost(K - 1)` means distinct `< K`.

So:

```text
AtLeast(K) = all - lessThanK
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long atMostKDistinct(vector<int>& arr, int k) {
    if (k < 0) return 0;

    unordered_map<int, int> freq;
    int left = 0;
    int distinct = 0;
    long long ans = 0;

    for (int right = 0; right < (int)arr.size(); right++) {
        if (freq[arr[right]] == 0) distinct++;
        freq[arr[right]]++;

        while (distinct > k) {
            freq[arr[left]]--;
            if (freq[arr[left]] == 0) distinct--;
            left++;
        }

        ans += right - left + 1;
    }

    return ans;
}

long long atLeastKDistinct(vector<int>& arr, int k) {
    long long n = arr.size();
    long long total = n * (n + 1) / 2;
    return total - atMostKDistinct(arr, k - 1);
}

int main() {
    vector<int> arr = {1, 2, 1, 3};
    int k = 2;
    cout << atLeastKDistinct(arr, k) << '\n';
    return 0;
}
```

### Index-by-Index Dry Run

```text
arr = [1, 2, 1, 3]
n = 4
k = 2

total subarrays = 4 * 5 / 2 = 10
AtMost(1) = 4

AtLeast(2) = 10 - 4 = 6
```

---

# 3. Form III — Multi-list Traversal

## Pattern

Use one pointer for each string/list.

```text
i = pointer in first sequence
j = pointer in second sequence
```

Move pointer based on match/comparison.

---

## 3.1 Is Subsequence

**Template used:** Template E — Same Direction Two Pointers / Multi-list Traversal.

### Problem Statement

Given two strings `s` and `t`, check whether `s` is a subsequence of `t`.

A subsequence means characters appear in the same order, but not necessarily continuously.

### Input

```text
s = "abc"
t = "axbyc"
```

### Expected Output

```text
true
```

### Brute Force Idea

Try all subsequences of `t` and check if any equals `s`.

For length `m`, number of subsequences is `2^m`.

### Brute Force Complexity

```text
Time  : O(2^m)
Space : O(m)
```

### Optimal Two Pointer Idea

Use pointer `i` on `s` and pointer `j` on `t`.

```text
if s[i] == t[j], match character and move i
always move j
```

If all characters of `s` are matched, answer is true.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool isSubsequence(string s, string t) {
    int i = 0;
    int j = 0;

    while (i < (int)s.size() && j < (int)t.size()) {
        if (s[i] == t[j]) {
            i++;
        }
        j++;
    }

    return i == (int)s.size();
}

int main() {
    string s = "abc";
    string t = "axbyc";
    cout << (isSubsequence(s, t) ? "true" : "false") << '\n';
    return 0;
}
```

### Index-by-Index Dry Run

```text
s = abc
t = axbyc

i j s[i] t[j] action
0 0 a    a    match, i++, j++
1 1 b    x    no match, j++
1 2 b    b    match, i++, j++
2 3 c    y    no match, j++
2 4 c    c    match, i++, j++

i = 3 == s.length
Answer = true
```

---

## 3.2 Intersection of Sorted Posting Lists

**Template used:** Template E — Same Direction Two Pointers / Multi-list Traversal.

### Problem Statement

Given sorted document lists for words, find documents that contain all given words.

Example:

```text
"is" -> [2, 5, 8, 13, 22]
"vg" -> [1, 5, 13, 21, 22]
"AZ" -> [3, 5, 21, 22]
```

Find documents where all three words appear.

### Expected Output

```text
[5, 22]
```

### Brute Force Idea

For each document in first list, search in all other lists.

If binary search is used:

```text
O(n log m) per list
```

### Optimal Two Pointer Idea for Two Lists

For two sorted lists:

```text
if a[i] == b[j], add answer and move both
if a[i] < b[j], i++
if a[i] > b[j], j++
```

We eliminate smaller value because it cannot appear later in the other sorted list.

### C++ Code for Two Lists

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> intersectTwo(vector<int>& a, vector<int>& b) {
    int i = 0, j = 0;
    vector<int> ans;

    while (i < (int)a.size() && j < (int)b.size()) {
        if (a[i] == b[j]) {
            ans.push_back(a[i]);
            i++;
            j++;
        } else if (a[i] < b[j]) {
            i++;
        } else {
            j++;
        }
    }

    return ans;
}
```

### C++ Code for Multiple Lists

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> intersectTwo(vector<int>& a, vector<int>& b) {
    int i = 0, j = 0;
    vector<int> ans;

    while (i < (int)a.size() && j < (int)b.size()) {
        if (a[i] == b[j]) {
            ans.push_back(a[i]);
            i++;
            j++;
        } else if (a[i] < b[j]) {
            i++;
        } else {
            j++;
        }
    }

    return ans;
}

vector<int> intersectAll(vector<vector<int>>& lists) {
    if (lists.empty()) return {};

    vector<int> ans = lists[0];
    for (int i = 1; i < (int)lists.size(); i++) {
        ans = intersectTwo(ans, lists[i]);
    }

    return ans;
}

int main() {
    vector<vector<int>> lists = {
        {2, 5, 8, 13, 22},
        {1, 5, 13, 21, 22},
        {3, 5, 21, 22}
    };

    vector<int> ans = intersectAll(lists);
    for (int x : ans) cout << x << ' ';
    cout << '\n';
    return 0;
}
```

### Index-by-Index Dry Run

First intersect:

```text
A = [2, 5, 8, 13, 22]
B = [1, 5, 13, 21, 22]

i j A[i] B[j] action
0 0 2    1    B smaller, j++
0 1 2    5    A smaller, i++
1 1 5    5    match, add 5, i++, j++
2 2 8    13   A smaller, i++
3 2 13   13   match, add 13, i++, j++
4 3 22   21   B smaller, j++
4 4 22   22   match, add 22

Result = [5, 13, 22]
```

Now intersect with third list:

```text
A = [5, 13, 22]
C = [3, 5, 21, 22]

i j A[i] C[j] action
0 0 5    3    C smaller, j++
0 1 5    5    match, add 5
1 2 13   21   A smaller, i++
2 2 22   21   C smaller, j++
2 3 22   22   match, add 22

Final Answer = [5, 22]
```

---

# 4. 3Sum Pattern

## Core Problem

Find indices `i`, `j`, `k` such that:

```text
0 <= i < j < k < n
arr[i] + arr[j] + arr[k] = target
```

## Key Idea

Sort the array.

Fix one element and reduce 3Sum to 2Sum.

```text
arr[i] + arr[j] + arr[k] = target
arr[j] + arr[k] = target - arr[i]
```

Then use opposite-direction two pointers for `j` and `k`.

---

## 4.1 3Sum Exists / List Triplets

**Template used:** Fix one element, then apply Template D — Opposite Direction Two Pointers.

### Problem Statement

Given an array and target, find all unique triplets whose sum is target.

### Input

```text
arr = [2, 3, 5, 5, 7, 9, 9, 13, 15, 20]
target = 21
```

### Expected Output

```text
[3, 5, 13]
[5, 7, 9]
```

### Brute Force Idea

Try all triples.

```cpp
for i = 0 to n-1
  for j = i+1 to n-1
    for k = j+1 to n-1
      if arr[i] + arr[j] + arr[k] == target
```

### Brute Force Complexity

```text
Time  : O(n³)
Space : O(1), excluding answer
```

### Optimal Idea

Sort array.

Fix `i`.

Use `left = i + 1`, `right = n - 1`.

```text
sum = arr[i] + arr[left] + arr[right]

sum == target -> found triplet
sum < target  -> left++
sum > target  -> right--
```

### Why `right--` When Sum Is Too Large?

Array is sorted.

If sum is too large, increasing `left` will only make sum larger.

So the only useful move is to reduce the largest element:

```text
right--
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<vector<int>> threeSumTarget(vector<int> arr, int target) {
    sort(arr.begin(), arr.end());
    int n = arr.size();
    vector<vector<int>> ans;

    for (int i = 0; i < n; i++) {
        if (i > 0 && arr[i] == arr[i - 1]) continue;

        int left = i + 1;
        int right = n - 1;

        while (left < right) {
            long long sum = 1LL * arr[i] + arr[left] + arr[right];

            if (sum == target) {
                ans.push_back({arr[i], arr[left], arr[right]});

                int lv = arr[left];
                int rv = arr[right];
                while (left < right && arr[left] == lv) left++;
                while (left < right && arr[right] == rv) right--;
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }
    }

    return ans;
}

int main() {
    vector<int> arr = {2, 3, 5, 5, 7, 9, 9, 13, 15, 20};
    int target = 21;

    auto ans = threeSumTarget(arr, target);
    for (auto& triplet : ans) {
        cout << triplet[0] << ' ' << triplet[1] << ' ' << triplet[2] << '\n';
    }
    return 0;
}
```

### Index-by-Index Dry Run

```text
arr = [2, 3, 5, 5, 7, 9, 9, 13, 15, 20]
target = 21
```

Fix `i = 0`, `arr[i] = 2`:

```text
left right arr[i] arr[left] arr[right] sum decision
1    9     2      3         20         25  sum > 21, right--
1    8     2      3         15         20  sum < 21, left++
2    8     2      5         15         22  sum > 21, right--
2    7     2      5         13         20  sum < 21, left++
3    7     2      5         13         20  sum < 21, left++
4    7     2      7         13         22  sum > 21, right--
4    6     2      7         9          18  sum < 21, left++
5    6     2      9         9          20  sum < 21, left++
stop
```

Fix `i = 1`, `arr[i] = 3`:

```text
left right arr[i] arr[left] arr[right] sum decision
2    9     3      5         20         28  right--
2    8     3      5         15         23  right--
2    7     3      5         13         21  found [3,5,13]
```

Fix `i = 2`, `arr[i] = 5`:

```text
left right arr[i] arr[left] arr[right] sum decision
3    9     5      5         20         30  right--
3    8     5      5         15         25  right--
3    7     5      5         13         23  right--
3    6     5      5         9          19  left++
4    6     5      7         9          21  found [5,7,9]
```

Answer:

```text
[3, 5, 13]
[5, 7, 9]
```

---

## 4.2 Count 3Sum With Duplicates

### Problem Statement

Given an array that may contain duplicates, count number of index triplets `(i, j, k)` such that:

```text
i < j < k
arr[i] + arr[j] + arr[k] = target
```

### Input

```text
arr = [3, 3, 4, 4, 5, 6, 6, 7, 7, 7]
target = 17
```

### Expected Output

```text
10
```

Example group:

```text
4 + 6 + 7 = 17
There are two 4s, two 6s, three 7s.
Count = 2 * 2 * 3 = 12 for that value group.
```

For exact count, use the algorithm below.

### Brute Force Idea

Try all triples of indices.

```cpp
long long bruteCount3Sum(vector<int>& arr, int target) {
    int n = arr.size();
    long long ans = 0;

    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
            for (int k = j + 1; k < n; k++) {
                if (arr[i] + arr[j] + arr[k] == target) ans++;
            }
        }
    }

    return ans;
}
```

### Brute Force Complexity

```text
Time  : O(n³)
Space : O(1)
```

### Optimal Idea

Sort the array.

Fix `i`.

Use `left` and `right` for the remaining part.

When sum equals target:

Case 1: `arr[left] != arr[right]`

```text
countLeft  = number of same values at left
countRight = number of same values at right
answer += countLeft * countRight
```

Case 2: `arr[left] == arr[right]`

All elements between left and right are same.

If there are `m` elements, choose any 2:

```text
answer += m * (m - 1) / 2
```

Then break because all pairs in this range are handled.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countThreeSum(vector<int> arr, int target) {
    sort(arr.begin(), arr.end());
    int n = arr.size();
    long long ans = 0;

    for (int i = 0; i < n; i++) {
        int left = i + 1;
        int right = n - 1;

        while (left < right) {
            long long sum = 1LL * arr[i] + arr[left] + arr[right];

            if (sum < target) {
                left++;
            } else if (sum > target) {
                right--;
            } else {
                if (arr[left] != arr[right]) {
                    long long cntLeft = 1;
                    long long cntRight = 1;

                    while (left + 1 < right && arr[left] == arr[left + 1]) {
                        cntLeft++;
                        left++;
                    }

                    while (right - 1 > left && arr[right] == arr[right - 1]) {
                        cntRight++;
                        right--;
                    }

                    ans += cntLeft * cntRight;
                    left++;
                    right--;
                } else {
                    long long m = right - left + 1;
                    ans += m * (m - 1) / 2;
                    break;
                }
            }
        }
    }

    return ans;
}

int main() {
    vector<int> arr = {3, 3, 4, 4, 5, 6, 6, 7, 7, 7};
    int target = 17;
    cout << countThreeSum(arr, target) << '\n';
    return 0;
}
```

### Index-by-Index Dry Run

```text
arr = [3, 3, 4, 4, 5, 6, 6, 7, 7, 7]
target = 17
```

Fix `i = 0`, `arr[i] = 3`:

```text
Need pair sum = 14
left = 1, right = 9

left right arr[left] arr[right] pairSum total action
1    9     3         7          10      13    left++
2    9     4         7          11      14    left++
3    9     4         7          11      14    left++
4    9     5         7          12      15    left++
5    9     6         7          13      16    left++
6    9     6         7          13      16    left++
7    9     7         7          14      17    found

arr[left] == arr[right] == 7
m = 3 elements: indices 7,8,9
add C(3,2) = 3
ans = 3
```

Fix `i = 1`, `arr[i] = 3`:

```text
Same logic gives another 3 combinations with 7,7.
ans = 6
```

Fix `i = 2`, `arr[i] = 4`:

```text
Need pair sum = 13
left = 3, right = 9

4 + 6 + 7 = 17
There are two 6s and three 7s available after i.
Add 2 * 3 = 6
ans = 12
```

Fix `i = 3`, `arr[i] = 4`:

```text
After index 3, two 6s and three 7s remain.
Add 2 * 3 = 6
ans = 18
```

Final depends on counting all index triplets, not unique value triplets.

```text
Answer = 18
```

---

# 5. Recognition Cheat Sheet

## Form I — Opposite Direction

Use when:

```text
sorted array
pair/triplet sum after fixing one element
palindrome
container max area
```

Movement:

```text
sum too small -> left++
sum too large -> right--
```

## Form II — Same Direction / Variable Window

Use when:

```text
subarray
longest / shortest / count
condition is monotonic
```

Monotonic condition examples:

```text
zeros <= k
distinct <= k
sum <= k when all numbers are non-negative
```

Counting formula:

```text
For every tail:
  count valid windows starting at tail = head - tail + 1
```

Or in common left/right template:

```text
For every right:
  count valid windows ending at right = right - left + 1
```

## Exactly K Trick

```text
Exactly K = AtMost(K) - AtMost(K - 1)
```

## At Least K Trick

```text
AtLeast(K) = TotalSubarrays - AtMost(K - 1)
```

## Form III — Multi-list Traversal

Use when:

```text
subsequence
merge/intersection of sorted lists
posting lists
multiple arrays
```

Movement:

```text
match -> move both/all needed pointers
smaller value -> move smaller pointer
```

---

# Final Interview Mental Template

```text
1. Is array/string sorted or can I sort it?
   yes -> opposite two pointers may work

2. Is problem asking subarray with condition?
   yes -> variable window

3. Is condition monotonic?
   If valid [L..R], then smaller window [L+1..R] also valid?
   yes -> two pointers

4. Is it exactly K?
   convert to AtMost(K) - AtMost(K-1)

5. Is it at least K?
   convert to total - AtMost(K-1)

6. Is it 3Sum / 4Sum?
   sort + fix one/more elements + two pointers

7. Is it multiple lists / strings?
   one pointer per list/string
```
