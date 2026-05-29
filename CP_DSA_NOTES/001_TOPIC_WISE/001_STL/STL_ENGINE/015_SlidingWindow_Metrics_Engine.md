# 015_SlidingWindow_Metrics_Engine.md

> MiniSTLEngine Phase 015  
> Topic: Sliding Window as a **Metrics / Stream Analytics / Contiguous Range Engine** for CP, DSA, FAANG interviews, and real-system thinking.

---

# Clickable Index

- [1. Goal](#1-goal)
- [2. Why Sliding Window Is An Engine](#2-why-sliding-window-is-an-engine)
- [3. Real-System Mental Model](#3-real-system-mental-model)
- [4. Sliding Window Core Behavior](#4-sliding-window-core-behavior)
- [5. Fixed Window vs Variable Window](#5-fixed-window-vs-variable-window)
- [6. CP/DSA Recognition](#6-cpdsa-recognition)
- [7. Engine Architecture](#7-engine-architecture)
- [8. Basic Fixed Window Metrics Engine](#8-basic-fixed-window-metrics-engine)
- [9. Dry Run: Fixed Window Sum](#9-dry-run-fixed-window-sum)
- [10. CP Pattern 1: Maximum Sum Subarray Of Size K](#10-cp-pattern-1-maximum-sum-subarray-of-size-k)
- [11. CP Pattern 2: First Window With Sum At Least K](#11-cp-pattern-2-first-window-with-sum-at-least-k)
- [12. CP Pattern 3: Longest Substring Without Repeating Characters](#12-cp-pattern-3-longest-substring-without-repeating-characters)
- [13. CP Pattern 4: Count Anagram Occurrences](#13-cp-pattern-4-count-anagram-occurrences)
- [14. CP Pattern 5: Minimum Size Subarray Sum](#14-cp-pattern-5-minimum-size-subarray-sum)
- [15. CP Pattern 6: Sliding Window With Frequency Map](#15-cp-pattern-6-sliding-window-with-frequency-map)
- [16. Sliding Window vs Two Pointers](#16-sliding-window-vs-two-pointers)
- [17. Common Mistakes](#17-common-mistakes)
- [18. Complexity Table](#18-complexity-table)
- [19. Real-World Mapping](#19-real-world-mapping)
- [20. Final Mental Model](#20-final-mental-model)
- [21. Next Step](#21-next-step)

---

# 1. Goal

Learn sliding window not only as:

```text
left/right over array
```

but as a:

```text
Metrics / Stream Analytics / Contiguous Range Engine
```

It helps solve:

```text
maximum sum window
minimum length subarray
longest substring
frequency window
anagram matching
real-time metrics
rate limiting
rolling analytics
```

---

# 2. Why Sliding Window Is An Engine

Brute force checks every subarray:

```text
O(N^2)
```

Sliding window reuses previous work:

```text
add new right element
remove old left element
update answer
```

Engine thinking:

```text
SlidingWindowMetricsEngine
    maintains active contiguous range
    updates state incrementally
    avoids recomputing from scratch
```

Core question:

```text
Can I maintain answer while moving a contiguous range?
```

If yes:

```text
sliding window may apply
```

---

# 3. Real-System Mental Model

Real systems use sliding windows for:

```text
requests per minute
CPU usage over last 5 minutes
error rate over rolling window
stock price moving average
fraud detection window
rate limiter window
real-time dashboards
```

Architecture:

```text
Incoming Stream
      |
      v
SlidingWindowMetricsEngine
      |
      +--> add new event
      +--> expire old event
      +--> update metric
      |
      v
Current Window Metric
```

---

# 4. Sliding Window Core Behavior

Array:

```text
[2, 1, 5, 1, 3, 2]
k = 3
```

Windows:

```text
[2,1,5]
[1,5,1]
[5,1,3]
[1,3,2]
```

Instead of recomputing each sum:

```text
newSum = oldSum - outgoing + incoming
```

---

# 5. Fixed Window vs Variable Window

## Fixed Window

Window size is fixed:

```text
exactly K elements
```

Examples:

```text
max sum of size K
average of size K
first negative in each window
```

## Variable Window

Window grows/shrinks based on condition:

```text
sum <= K
unique characters
at most K distinct
minimum length
```

Examples:

```text
longest substring without repeating
minimum size subarray sum
longest subarray with at most K distinct
```

---

# 6. CP/DSA Recognition

Use sliding window when problem says:

```text
contiguous subarray
substring
window
at most K
at least K
exactly K size
longest
shortest
maximum/minimum over range
```

Hidden mapping:

| Problem clue | Sliding window form |
|---|---|
| fixed length K | fixed window |
| longest valid substring | variable window |
| minimum length satisfying condition | variable shrink |
| frequency in substring | window frequency map |
| anagram occurrences | fixed window + frequency |
| rolling metric | fixed/variable window |

---

# 7. Engine Architecture

```text
MiniSlidingWindowMetricsEngine
├── fixed window sum engine
├── variable window condition engine
├── frequency window engine
├── substring uniqueness engine
├── minimum length engine
├── anagram matcher
└── real-time metrics engine
```

---

# 8. Basic Fixed Window Metrics Engine

## Step-by-Step Approach Before Code

```text
Step 1: Maintain currentSum for active window.

Step 2: Iterate right pointer from 0 to n-1.

Step 3: Add nums[right] to currentSum.

Step 4: If window size becomes greater than k:
        remove nums[right-k].

Step 5: If window size equals k:
        update answer using currentSum.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class FixedWindowMetricsEngine {
public:

    int maxWindowSum(vector<int>& nums, int k) {

        int currentSum = 0;
        int best = INT_MIN;

        for (int right = 0; right < (int)nums.size(); right++) {

            // Add new incoming element to active window.
            currentSum += nums[right];

            // If window exceeds size k,
            // remove expired element from left side.
            if (right >= k) {
                currentSum -= nums[right - k];
            }

            // Once first full window is formed,
            // update best metric.
            if (right >= k - 1) {
                best = max(best, currentSum);
            }
        }

        return best;
    }
};

int main() {

    vector<int> nums = {2, 1, 5, 1, 3, 2};

    int k = 3;

    FixedWindowMetricsEngine engine;

    cout << engine.maxWindowSum(nums, k)
         << endl;

    return 0;
}
```

---

# 9. Dry Run: Fixed Window Sum

Input:

```text
nums = [2,1,5,1,3,2]
k = 3
```

Step:

```text
right=0 add 2 -> sum=2, not full
right=1 add 1 -> sum=3, not full
right=2 add 5 -> sum=8, full, best=8

right=3 add 1 -> sum=9
remove nums[0]=2 -> sum=7
best=8

right=4 add 3 -> sum=10
remove nums[1]=1 -> sum=9
best=9

right=5 add 2 -> sum=11
remove nums[2]=5 -> sum=6
best=9
```

Answer:

```text
9
```

---

# 10. CP Pattern 1: Maximum Sum Subarray Of Size K

## Problem Type

```text
Find maximum sum among all subarrays of exactly size K.
```

## Step-by-Step Approach Before Code

```text
Step 1: Use fixed window.

Step 2: Add current element.

Step 3: Remove element that leaves window.

Step 4: Update max when window size is K.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int maxSumSizeK(vector<int>& nums, int k) {

    int sum = 0;
    int best = INT_MIN;

    for (int right = 0; right < (int)nums.size(); right++) {

        sum += nums[right];

        if (right >= k) {
            sum -= nums[right - k];
        }

        if (right >= k - 1) {
            best = max(best, sum);
        }
    }

    return best;
}

int main() {

    vector<int> nums = {2, 1, 5, 1, 3, 2};

    cout << maxSumSizeK(nums, 3) << endl;

    return 0;
}
```

---

# 11. CP Pattern 2: First Window With Sum At Least K

## Problem Type

```text
Find first contiguous window whose sum reaches target.
```

This is a variable window if numbers are positive.

## Step-by-Step Approach Before Code

```text
Step 1: Expand right pointer and add nums[right].

Step 2: While sum >= target:
        current window is valid.

Step 3: Record or return answer.

Step 4: Shrink from left to find smaller/earlier window.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

pair<int,int> firstWindowAtLeastTarget(
    vector<int>& nums,
    int target
) {
    int left = 0;
    int sum = 0;

    for (int right = 0; right < (int)nums.size(); right++) {

        sum += nums[right];

        while (sum >= target) {

            return {left, right};
        }
    }

    return {-1, -1};
}

int main() {

    vector<int> nums = {1, 2, 3, 4, 5};

    auto [l, r] =
        firstWindowAtLeastTarget(nums, 7);

    cout << l << " " << r << endl;

    return 0;
}
```

---

# 12. CP Pattern 3: Longest Substring Without Repeating Characters

## Problem Type

```text
Find longest substring with all unique characters.
```

## Step-by-Step Approach Before Code

```text
Step 1: Use left and right pointers over string.

Step 2: Maintain frequency of characters in current window.

Step 3: Add s[right].

Step 4: If s[right] creates duplicate:
        move left until duplicate removed.

Step 5: Update best length after window becomes valid.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int lengthOfLongestSubstring(string s) {

    vector<int> freq(256, 0);

    int left = 0;
    int best = 0;

    for (int right = 0; right < (int)s.size(); right++) {

        char ch = s[right];

        freq[ch]++;

        // If duplicate exists,
        // shrink window until valid again.
        while (freq[ch] > 1) {

            freq[s[left]]--;
            left++;
        }

        best = max(best, right - left + 1);
    }

    return best;
}

int main() {

    string s = "abcabcbb";

    cout << lengthOfLongestSubstring(s)
         << endl;

    return 0;
}
```

## Dry Run

```text
s = abcabcbb

window abc -> length 3
next a creates duplicate
shrink until old a removed
continue

answer = 3
```

---

# 13. CP Pattern 4: Count Anagram Occurrences

## Problem Type

```text
Count how many substrings of text are anagrams of pattern.
```

## Step-by-Step Approach Before Code

```text
Step 1: Build frequency of pattern.

Step 2: Use fixed window of size pattern.length.

Step 3: Maintain frequency of current window.

Step 4: Add incoming character.

Step 5: Remove outgoing character when window exceeds size.

Step 6: If window frequency equals pattern frequency:
        count one anagram.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int countAnagrams(string text, string pattern) {

    int k = pattern.size();

    vector<int> need(26, 0);
    vector<int> have(26, 0);

    for (char ch : pattern) {
        need[ch - 'a']++;
    }

    int answer = 0;

    for (int right = 0; right < (int)text.size(); right++) {

        have[text[right] - 'a']++;

        if (right >= k) {
            have[text[right - k] - 'a']--;
        }

        if (right >= k - 1 && have == need) {
            answer++;
        }
    }

    return answer;
}

int main() {

    string text = "cbaebabacd";
    string pattern = "abc";

    cout << countAnagrams(text, pattern)
         << endl;

    return 0;
}
```

---

# 14. CP Pattern 5: Minimum Size Subarray Sum

## Problem Type

```text
Find minimum length subarray with sum >= target.
```

Assumption:

```text
all numbers are positive
```

## Step-by-Step Approach Before Code

```text
Step 1: Expand right pointer and add value.

Step 2: While sum >= target:
        window is valid.

Step 3: Update best length.

Step 4: Remove nums[left] and move left.

Step 5: Continue shrinking until invalid.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int minSubArrayLen(int target, vector<int>& nums) {

    int left = 0;
    int sum = 0;
    int best = INT_MAX;

    for (int right = 0; right < (int)nums.size(); right++) {

        sum += nums[right];

        while (sum >= target) {

            best = min(best, right - left + 1);

            sum -= nums[left];
            left++;
        }
    }

    if (best == INT_MAX) {
        return 0;
    }

    return best;
}

int main() {

    vector<int> nums = {2,3,1,2,4,3};

    cout << minSubArrayLen(7, nums)
         << endl;

    return 0;
}
```

---

# 15. CP Pattern 6: Sliding Window With Frequency Map

## Problem Type

```text
Longest subarray with at most K distinct values.
```

## Step-by-Step Approach Before Code

```text
Step 1: Maintain map value -> frequency inside window.

Step 2: Expand right pointer.

Step 3: Add nums[right] frequency.

Step 4: If distinct count > K:
        shrink from left.

Step 5: Update best length when window valid.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int longestAtMostKDistinct(
    vector<int>& nums,
    int k
) {
    unordered_map<int, int> freq;

    int left = 0;
    int best = 0;

    for (int right = 0; right < (int)nums.size(); right++) {

        freq[nums[right]]++;

        while ((int)freq.size() > k) {

            freq[nums[left]]--;

            if (freq[nums[left]] == 0) {
                freq.erase(nums[left]);
            }

            left++;
        }

        best = max(best, right - left + 1);
    }

    return best;
}

int main() {

    vector<int> nums = {1, 2, 1, 2, 3};

    cout << longestAtMostKDistinct(nums, 2)
         << endl;

    return 0;
}
```

---

# 16. Sliding Window vs Two Pointers

| Feature | Sliding Window | Two Pointers |
|---|---|---|
| Main object | contiguous window | two controlled positions |
| Typical condition | window validity | pair/merge/partition |
| Data | subarray/substring | sorted arrays/streams |
| Movement | expand + shrink | move left/right/read/write |
| Examples | longest substring | two sum sorted |

Shortcut:

```text
Contiguous subarray/substring with condition?
→ sliding window

Pair matching / merging?
→ two pointers
```

---

# 17. Common Mistakes

## Mistake 1: Using Sliding Window With Negative Numbers

For sum-based variable window:

```text
usually requires positive numbers
```

With negatives, sum is not monotonic.

Use:

```text
prefix sum + hashmap
```

---

## Mistake 2: Forgetting To Shrink

Variable window must restore validity.

```cpp
while (invalid) {
    remove nums[left];
    left++;
}
```

---

## Mistake 3: Updating Answer At Wrong Time

Usually update answer:

```text
after window is valid
```

---

## Mistake 4: Confusing Fixed And Variable Window

Fixed:

```text
remove exactly when size > k
```

Variable:

```text
shrink while condition invalid
```

---

# 18. Complexity Table

| Pattern | Complexity |
|---|---:|
| fixed window sum | O(N) |
| longest unique substring | O(N) |
| min size subarray positive nums | O(N) |
| anagram fixed window | O(26 * N) or O(N) |
| at most K distinct | O(N) average |
| space | O(K) or alphabet size |

---

# 19. Real-World Mapping

| Sliding Window Concept | Real-System Meaning |
|---|---|
| fixed window sum | rolling metric |
| variable window | adaptive monitoring |
| frequency window | anomaly detection |
| longest valid window | session analysis |
| anagram matching | signature scanning |
| min valid window | alert threshold window |
| rate limiter | requests per time window |
| stream metric | real-time dashboard |

---

# 20. Final Mental Model

Sliding window is:

```text
incremental contiguous range analytics engine
```

Best for:

```text
subarrays
substrings
rolling metrics
frequency windows
real-time stream analytics
```

One-line CP rule:

```text
If problem asks about contiguous range and you can update state incrementally, think sliding window.
```

One-line system rule:

```text
Sliding windows power rolling metrics, rate limiters, monitoring dashboards, and stream analytics.
```

---

# 21. Next Step

Next file:

```text
016_MonotonicStack_Boundary_Engine.md
```

Then:

```text
017_MonotonicDeque_Window_MinMax_Engine.md
018_RangeMapping_Interval_Engine.md
019_SweepLine_Event_Engine.md
```
