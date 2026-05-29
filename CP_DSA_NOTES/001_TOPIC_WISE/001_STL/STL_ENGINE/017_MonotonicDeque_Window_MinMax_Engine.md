# 017_MonotonicDeque_Window_MinMax_Engine.md

> MiniSTLEngine Phase 017  
> Topic: Monotonic Deque as a **Window Min/Max / Stream Candidate Engine** for CP, DSA, FAANG interviews, and real-system thinking.

---

# Clickable Index

- [1. Goal](#1-goal)
- [2. Why Monotonic Deque Is An Engine](#2-why-monotonic-deque-is-an-engine)
- [3. Real-System Mental Model](#3-real-system-mental-model)
- [4. Core Monotonic Deque Behavior](#4-core-monotonic-deque-behavior)
- [5. Increasing vs Decreasing Deque](#5-increasing-vs-decreasing-deque)
- [6. CP/DSA Recognition](#6-cpdsa-recognition)
- [7. Engine Architecture](#7-engine-architecture)
- [8. Basic Window Max Engine](#8-basic-window-max-engine)
- [9. Dry Run: Sliding Window Maximum](#9-dry-run-sliding-window-maximum)
- [10. CP Pattern 1: Sliding Window Maximum](#10-cp-pattern-1-sliding-window-maximum)
- [11. CP Pattern 2: Sliding Window Minimum](#11-cp-pattern-2-sliding-window-minimum)
- [12. CP Pattern 3: First Negative In Every Window](#12-cp-pattern-3-first-negative-in-every-window)
- [13. CP Pattern 4: Shortest Subarray With Sum At Least K](#13-cp-pattern-4-shortest-subarray-with-sum-at-least-k)
- [14. CP Pattern 5: Constrained Subsequence Sum](#14-cp-pattern-5-constrained-subsequence-sum)
- [15. Monotonic Deque vs Monotonic Stack](#15-monotonic-deque-vs-monotonic-stack)
- [16. Common Mistakes](#16-common-mistakes)
- [17. Complexity Table](#17-complexity-table)
- [18. Real-World Mapping](#18-real-world-mapping)
- [19. Final Mental Model](#19-final-mental-model)
- [20. Next Step](#20-next-step)

---

# 1. Goal

Learn monotonic deque not only as a CP trick, but as a:

```text
Window Min/Max / Stream Candidate Engine
```

It helps solve:

```text
sliding window maximum
sliding window minimum
rolling metrics
stream max/min
window candidate pruning
shortest subarray with prefix sums
DP optimization with window max
```

---

# 2. Why Monotonic Deque Is An Engine

A deque supports operations at both ends.

A monotonic deque adds an order rule:

```text
keep candidates in increasing or decreasing order
```

Normal thinking:

```cpp
deque<int> dq;
```

Engine thinking:

```text
MonotonicDequeWindowEngine
    stores only useful candidates
    removes expired indices from front
    removes dominated candidates from back
    answers min/max in O(1)
```

Core question:

```text
Do I need min/max over every moving window?
```

If yes:

```text
monotonic deque may apply
```

---

# 3. Real-System Mental Model

Real systems need rolling min/max:

```text
CPU max over last 5 minutes
API latency p95 window helper
stock high/low over last K ticks
sensor highest temperature window
network traffic spike detection
monitoring dashboards
```

Architecture:

```text
Incoming Stream
      |
      v
MonotonicDequeWindowEngine
      |
      +--> remove expired event
      +--> remove dominated candidates
      +--> expose current max/min
      |
      v
Rolling Metric Result
```

---

# 4. Core Monotonic Deque Behavior

For sliding maximum:

```text
deque stores indices
values are decreasing
front is maximum
```

Why store indices?

```text
1. to know when index expires from window
2. to return value nums[dq.front()]
3. to maintain window boundaries
```

---

# 5. Increasing vs Decreasing Deque

## Decreasing Deque

Used for:

```text
sliding window maximum
window best score
DP max in range
```

Pop from back while:

```text
nums[dq.back()] <= nums[i]
```

because smaller/equal old values are dominated.

## Increasing Deque

Used for:

```text
sliding window minimum
shortest subarray prefix optimization
```

Pop from back while:

```text
nums[dq.back()] >= nums[i]
```

because larger/equal old values are dominated.

---

# 6. CP/DSA Recognition

Use monotonic deque when problem says:

```text
max in every window
min in every window
rolling max/min
window candidate
range DP with max/min
shortest subarray with prefix condition
```

Hidden mapping:

| Problem clue | Deque form |
|---|---|
| max every K window | decreasing deque |
| min every K window | increasing deque |
| first negative | normal deque of negative indices |
| shortest subarray sum >= K | increasing prefix deque |
| DP max in last K | decreasing deque |
| rolling high/low | monotonic deque |

---

# 7. Engine Architecture

```text
MiniMonotonicDequeWindowEngine
├── sliding maximum engine
├── sliding minimum engine
├── first negative window engine
├── prefix sum deque engine
├── DP max window engine
└── stream monitoring engine
```

---

# 8. Basic Window Max Engine

## Step-by-Step Approach Before Code

```text
Step 1: Use deque to store indices, not values.

Step 2: For every index i:
        remove indices from front if they are outside current window.

Step 3: Remove dominated candidates from back:
        while nums[dq.back()] <= nums[i], pop back.

Step 4: Push current index i.

Step 5: When first full window is formed:
        nums[dq.front()] is the maximum.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class WindowMaxEngine {
public:

    vector<int> maxSlidingWindow(
        vector<int>& nums,
        int k
    ) {
        deque<int> dq;

        vector<int> answer;

        for (int i = 0; i < (int)nums.size(); i++) {

            // Step 1:
            // Remove expired indices.
            // They are no longer inside [i-k+1, i].
            while (!dq.empty() &&
                   dq.front() <= i - k) {

                dq.pop_front();
            }

            // Step 2:
            // Maintain decreasing deque.
            // Any smaller/equal value behind current
            // can never become max in future windows.
            while (!dq.empty() &&
                   nums[dq.back()] <= nums[i]) {

                dq.pop_back();
            }

            // Step 3:
            // Current index is a candidate for future windows.
            dq.push_back(i);

            // Step 4:
            // Once window size reaches k,
            // front contains index of maximum.
            if (i >= k - 1) {

                answer.push_back(
                    nums[dq.front()]
                );
            }
        }

        return answer;
    }
};

int main() {

    vector<int> nums = {
        1,3,-1,-3,5,3,6,7
    };

    int k = 3;

    WindowMaxEngine engine;

    vector<int> ans =
        engine.maxSlidingWindow(nums, k);

    for (int x : ans) {
        cout << x << " ";
    }

    return 0;
}
```

---

# 9. Dry Run: Sliding Window Maximum

Input:

```text
nums = [1, 3, -1, -3, 5]
k = 3
```

Initial:

```text
dq = []
answer = []
```

i = 0:

```text
current = 1
push index 0
dq = [0:1]
```

i = 1:

```text
current = 3

3 >= 1 -> pop 0
push 1

dq = [1:3]
```

i = 2:

```text
current = -1

-1 >= 3? no
push 2

dq = [1:3, 2:-1]

window [1,3,-1]
max = nums[1] = 3

answer = [3]
```

i = 3:

```text
current = -3

expired?
dq.front = 1, i-k = 0
1 <= 0? no

-3 >= -1? no
push 3

dq = [1:3, 2:-1, 3:-3]

window [3,-1,-3]
max = 3

answer = [3,3]
```

i = 4:

```text
current = 5

expired?
dq.front = 1, i-k = 1
1 <= 1 -> pop 1

5 >= -3 -> pop 3
5 >= -1 -> pop 2

push 4

dq = [4:5]

window [-1,-3,5]
max = 5

answer = [3,3,5]
```

---

# 10. CP Pattern 1: Sliding Window Maximum

## Problem Type

```text
Find maximum in every window of size K.
```

## Step-by-Step Approach Before Code

```text
Step 1: Store indices in deque.

Step 2: Remove expired indices from front.

Step 3: Maintain decreasing values:
        pop back while nums[dq.back()] <= nums[i].

Step 4: Push current index.

Step 5: Window max is nums[dq.front()].
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> slidingWindowMaximum(
    vector<int>& nums,
    int k
) {
    deque<int> dq;
    vector<int> answer;

    for (int i = 0; i < (int)nums.size(); i++) {

        while (!dq.empty() &&
               dq.front() <= i - k) {
            dq.pop_front();
        }

        while (!dq.empty() &&
               nums[dq.back()] <= nums[i]) {
            dq.pop_back();
        }

        dq.push_back(i);

        if (i >= k - 1) {
            answer.push_back(nums[dq.front()]);
        }
    }

    return answer;
}

int main() {

    vector<int> nums = {
        1,3,-1,-3,5,3,6,7
    };

    vector<int> ans =
        slidingWindowMaximum(nums, 3);

    for (int x : ans) {
        cout << x << " ";
    }

    return 0;
}
```

---

# 11. CP Pattern 2: Sliding Window Minimum

## Problem Type

```text
Find minimum in every window of size K.
```

## Step-by-Step Approach Before Code

```text
Step 1: Store indices in deque.

Step 2: Remove expired indices from front.

Step 3: Maintain increasing values:
        pop back while nums[dq.back()] >= nums[i].

Step 4: Push current index.

Step 5: Window min is nums[dq.front()].
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> slidingWindowMinimum(
    vector<int>& nums,
    int k
) {
    deque<int> dq;
    vector<int> answer;

    for (int i = 0; i < (int)nums.size(); i++) {

        while (!dq.empty() &&
               dq.front() <= i - k) {
            dq.pop_front();
        }

        while (!dq.empty() &&
               nums[dq.back()] >= nums[i]) {
            dq.pop_back();
        }

        dq.push_back(i);

        if (i >= k - 1) {
            answer.push_back(nums[dq.front()]);
        }
    }

    return answer;
}

int main() {

    vector<int> nums = {
        4,2,12,3,-1,6
    };

    vector<int> ans =
        slidingWindowMinimum(nums, 3);

    for (int x : ans) {
        cout << x << " ";
    }

    return 0;
}
```

---

# 12. CP Pattern 3: First Negative In Every Window

## Problem Type

```text
For every window of size K, find first negative number.
```

## Step-by-Step Approach Before Code

```text
Step 1: Store indices of negative values in deque.

Step 2: For every i:
        if nums[i] is negative, push i.

Step 3: Remove expired negative indices from front.

Step 4: When window is full:
        if deque empty -> no negative.
        else nums[dq.front()] is first negative.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> firstNegativeInWindow(
    vector<int>& nums,
    int k
) {
    deque<int> dq;
    vector<int> answer;

    for (int i = 0; i < (int)nums.size(); i++) {

        if (nums[i] < 0) {
            dq.push_back(i);
        }

        while (!dq.empty() &&
               dq.front() <= i - k) {
            dq.pop_front();
        }

        if (i >= k - 1) {

            if (dq.empty()) {
                answer.push_back(0);
            } else {
                answer.push_back(nums[dq.front()]);
            }
        }
    }

    return answer;
}

int main() {

    vector<int> nums = {
        12,-1,-7,8,-15,30,16,28
    };

    vector<int> ans =
        firstNegativeInWindow(nums, 3);

    for (int x : ans) {
        cout << x << " ";
    }

    return 0;
}
```

---

# 13. CP Pattern 4: Shortest Subarray With Sum At Least K

## Problem Type

```text
Find shortest subarray with sum >= K.
```

This works even with negative numbers using prefix sums + deque.

## Core Idea

Use prefix:

```text
sum(l..r-1) = prefix[r] - prefix[l]
```

Need:

```text
prefix[r] - prefix[l] >= K
```

Deque stores candidate prefix indices in increasing prefix value.

## Step-by-Step Approach Before Code

```text
Step 1: Build prefix array where prefix[0] = 0.

Step 2: Traverse prefix index r from 0 to n.

Step 3: While front gives valid sum:
        prefix[r] - prefix[dq.front()] >= K
        update answer and pop front.

Step 4: Maintain increasing prefix values:
        while prefix[dq.back()] >= prefix[r]
        pop back.

Step 5: Push r as future candidate.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int shortestSubarray(vector<int>& nums, int k) {

    int n = nums.size();

    vector<long long> prefix(n + 1, 0);

    for (int i = 0; i < n; i++) {
        prefix[i + 1] = prefix[i] + nums[i];
    }

    deque<int> dq;

    int best = n + 1;

    for (int r = 0; r <= n; r++) {

        while (!dq.empty() &&
               prefix[r] - prefix[dq.front()] >= k) {

            best = min(best, r - dq.front());

            dq.pop_front();
        }

        while (!dq.empty() &&
               prefix[dq.back()] >= prefix[r]) {

            dq.pop_back();
        }

        dq.push_back(r);
    }

    if (best == n + 1) {
        return -1;
    }

    return best;
}

int main() {

    vector<int> nums = {2, -1, 2};

    cout << shortestSubarray(nums, 3)
         << endl;

    return 0;
}
```

---

# 14. CP Pattern 5: Constrained Subsequence Sum

## Problem Type

```text
dp[i] = nums[i] + max(0, dp[j])
where i-k <= j < i
```

Need max DP value in last K range.

Use monotonic deque.

## Step-by-Step Approach Before Code

```text
Step 1: Create dp array.

Step 2: Deque stores indices with decreasing dp values.

Step 3: Remove expired indices outside last K.

Step 4: dp[i] = nums[i] + max(0, dp[dq.front()]).

Step 5: Maintain decreasing dp deque:
        pop back while dp[dq.back()] <= dp[i].

Step 6: Push i.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int constrainedSubsetSum(
    vector<int>& nums,
    int k
) {
    int n = nums.size();

    vector<int> dp(n);

    deque<int> dq;

    int answer = INT_MIN;

    for (int i = 0; i < n; i++) {

        while (!dq.empty() &&
               dq.front() < i - k) {
            dq.pop_front();
        }

        int bestPrevious = 0;

        if (!dq.empty()) {
            bestPrevious = max(0, dp[dq.front()]);
        }

        dp[i] = nums[i] + bestPrevious;

        answer = max(answer, dp[i]);

        while (!dq.empty() &&
               dp[dq.back()] <= dp[i]) {
            dq.pop_back();
        }

        dq.push_back(i);
    }

    return answer;
}

int main() {

    vector<int> nums = {10,2,-10,5,20};

    cout << constrainedSubsetSum(nums, 2)
         << endl;

    return 0;
}
```

---

# 15. Monotonic Deque vs Monotonic Stack

| Feature | Monotonic Stack | Monotonic Deque |
|---|---|---|
| Main use | nearest boundary | window min/max |
| Expiry by index | usually no | yes |
| Remove front | rarely | important |
| Remove back | yes | yes |
| Example | next greater | sliding max |
| Time | O(N) | O(N) |

Shortcut:

```text
Nearest greater/smaller boundary?
→ monotonic stack

Moving window max/min?
→ monotonic deque
```

---

# 16. Common Mistakes

## Mistake 1: Storing Values Instead Of Indices

Usually wrong:

```cpp
dq.push_back(nums[i]);
```

Correct:

```cpp
dq.push_back(i);
```

Because you need:

```text
expiry check
window boundary
answer index
```

---

## Mistake 2: Wrong Expiry Condition

For window size K:

```cpp
dq.front() <= i - k
```

means index is outside current window.

---

## Mistake 3: Wrong Direction

For max:

```text
decreasing deque
```

For min:

```text
increasing deque
```

---

## Mistake 4: Thinking It Is O(NK)

Each element:

```text
enters once
leaves once
```

Total:

```text
O(N)
```

---

# 17. Complexity Table

| Pattern | Complexity |
|---|---:|
| sliding max | O(N) |
| sliding min | O(N) |
| first negative | O(N) |
| shortest subarray >= K | O(N) |
| constrained subsequence sum | O(N) |
| space | O(K) or O(N) |

---

# 18. Real-World Mapping

| Monotonic Deque Concept | Real-System Meaning |
|---|---|
| sliding maximum | rolling peak CPU usage |
| sliding minimum | rolling low latency |
| expired index removal | event TTL/window expiry |
| dominated candidate removal | candidate pruning |
| stream max/min | real-time monitoring |
| prefix deque | financial threshold detection |
| DP window max | optimization over recent history |
| rolling dashboard | metrics engine |

---

# 19. Final Mental Model

Monotonic deque is:

```text
rolling best-candidate window engine
```

Best for:

```text
sliding max/min
stream analytics
window optimization
range DP
prefix threshold problems
```

One-line CP rule:

```text
If every moving window needs fast min/max, think monotonic deque.
```

One-line system rule:

```text
Monotonic deque powers rolling metrics and real-time best-candidate analytics.
```

---

# 20. Next Step

Next file:

```text
018_RangeMapping_Interval_Engine.md
```

Then:

```text
019_SweepLine_Event_Engine.md
020_Production_STL_Pattern_Decision_Engine.md
```
