# 016_MonotonicStack_Boundary_Engine.md

> MiniSTLEngine Phase 016  
> Topic: Monotonic Stack as a **Boundary Detection / Range Contribution Engine** for CP, DSA, FAANG interviews, and real-system thinking.

---

# Clickable Index

- [1. Goal](#1-goal)
- [2. Why Monotonic Stack Is An Engine](#2-why-monotonic-stack-is-an-engine)
- [3. Real-System Mental Model](#3-real-system-mental-model)
- [4. Core Monotonic Stack Behavior](#4-core-monotonic-stack-behavior)
- [5. Increasing vs Decreasing Stack](#5-increasing-vs-decreasing-stack)
- [6. CP/DSA Recognition](#6-cpdsa-recognition)
- [7. Engine Architecture](#7-engine-architecture)
- [8. Basic Boundary Engine](#8-basic-boundary-engine)
- [9. Dry Run: Next Greater Element](#9-dry-run-next-greater-element)
- [10. CP Pattern 1: Next Greater Element](#10-cp-pattern-1-next-greater-element)
- [11. CP Pattern 2: Daily Temperatures](#11-cp-pattern-2-daily-temperatures)
- [12. CP Pattern 3: Next Smaller Element](#12-cp-pattern-3-next-smaller-element)
- [13. CP Pattern 4: Largest Rectangle In Histogram](#13-cp-pattern-4-largest-rectangle-in-histogram)
- [14. CP Pattern 5: Sum Of Subarray Minimums](#14-cp-pattern-5-sum-of-subarray-minimums)
- [15. CP Pattern 6: Remove K Digits](#15-cp-pattern-6-remove-k-digits)
- [16. Monotonic Stack vs Normal Stack](#16-monotonic-stack-vs-normal-stack)
- [17. Common Mistakes](#17-common-mistakes)
- [18. Complexity Table](#18-complexity-table)
- [19. Real-World Mapping](#19-real-world-mapping)
- [20. Final Mental Model](#20-final-mental-model)
- [21. Next Step](#21-next-step)

---

# 1. Goal

Learn monotonic stack not only as a CP trick, but as a:

```text
Boundary Detection / Range Contribution Engine
```

It helps solve:

```text
next greater element
next smaller element
daily temperatures
stock span
largest rectangle
subarray minimum contribution
remove K digits
visible people
```

---

# 2. Why Monotonic Stack Is An Engine

A normal stack stores history.

A monotonic stack stores:

```text
useful unresolved history
```

It removes values that can no longer be useful.

Normal thinking:

```text
stack of indices
```

Engine thinking:

```text
BoundaryDetectionEngine
    stores unresolved candidates
    current element resolves previous elements
    maintains increasing/decreasing order
    finds nearest boundary in O(N)
```

Core question:

```text
Does current element resolve previous elements?
```

If yes:

```text
monotonic stack may apply
```

---

# 3. Real-System Mental Model

Monotonic stack appears as a general idea in:

```text
signal boundary detection
price span analysis
temperature alert systems
histogram compression
visibility/line-of-sight systems
range contribution analytics
```

Architecture:

```text
Incoming Stream
      |
      v
MonotonicStackBoundaryEngine
      |
      +--> keep unresolved candidates
      +--> pop dominated candidates
      +--> assign boundary/result
      |
      v
Nearest Boundary / Contribution Result
```

---

# 4. Core Monotonic Stack Behavior

Example:

```text
nums = [2, 1, 5, 3]
```

For next greater:

```text
2 waits for greater
1 waits for greater
5 resolves both 1 and 2
3 waits for greater
```

Stack stores:

```text
indices of unresolved elements
```

Why indices?

```text
to update answer[index]
to calculate distance
to calculate width
```

---

# 5. Increasing vs Decreasing Stack

## Decreasing Stack

Used for:

```text
next greater
daily temperatures
stock span variants
```

Property:

```text
values from bottom to top are decreasing
```

Pop when:

```text
current > stack top value
```

## Increasing Stack

Used for:

```text
next smaller
largest rectangle
subarray minimums
```

Property:

```text
values from bottom to top are increasing
```

Pop when:

```text
current < stack top value
```

---

# 6. CP/DSA Recognition

Use monotonic stack when problem says:

```text
next greater
next smaller
previous greater
previous smaller
nearest taller
nearest warmer
span
histogram
visible people
contribution as min/max
remove digits lexicographically
```

Hidden mapping:

| Problem clue | Stack form |
|---|---|
| first greater on right | decreasing stack |
| first smaller on right | increasing stack |
| wait until warmer | decreasing stack |
| rectangle boundary | increasing stack |
| contribution as minimum | previous/next smaller |
| remove larger previous digit | increasing greedy stack |

---

# 7. Engine Architecture

```text
MiniMonotonicStackBoundaryEngine
├── next greater engine
├── next smaller engine
├── distance resolver
├── histogram boundary engine
├── contribution engine
├── greedy removal engine
└── visibility engine
```

---

# 8. Basic Boundary Engine

## Step-by-Step Approach Before Code

```text
Step 1: Decide what each index is waiting for.
        Example: waiting for next greater.

Step 2: Store unresolved indices in stack.

Step 3: For each current index i:
        while stack is not empty and current resolves stack top:
            pop top index
            set answer[top] = current result.

Step 4: Push current index because it is now unresolved.

Step 5: Remaining stack elements have no answer.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class MonotonicStackBoundaryEngine {
public:

    vector<int> nextGreater(vector<int>& nums) {

        int n = nums.size();

        vector<int> answer(n, -1);

        stack<int> st;
        // Stack stores indices.
        // Values are decreasing from bottom to top.

        for (int i = 0; i < n; i++) {

            // Current value resolves all smaller previous values.
            while (!st.empty() &&
                   nums[i] > nums[st.top()]) {

                int index = st.top();
                st.pop();

                answer[index] = nums[i];
            }

            // Current index waits for future greater value.
            st.push(i);
        }

        return answer;
    }
};

int main() {

    vector<int> nums = {2, 1, 5, 3};

    MonotonicStackBoundaryEngine engine;

    vector<int> ans =
        engine.nextGreater(nums);

    for (int x : ans) {
        cout << x << " ";
    }

    return 0;
}
```

---

# 9. Dry Run: Next Greater Element

Input:

```text
nums = [2, 1, 5, 3]
```

Initial:

```text
answer = [-1, -1, -1, -1]
stack = []
```

i = 0:

```text
current = 2
stack empty
push index 0

stack = [0:2]
```

i = 1:

```text
current = 1
1 > 2? no
push index 1

stack = [0:2, 1:1]
```

i = 2:

```text
current = 5

5 > 1 -> pop index 1 -> answer[1] = 5
5 > 2 -> pop index 0 -> answer[0] = 5

push index 2

stack = [2:5]
answer = [5, 5, -1, -1]
```

i = 3:

```text
current = 3
3 > 5? no
push index 3

stack = [2:5, 3:3]
answer = [5, 5, -1, -1]
```

---

# 10. CP Pattern 1: Next Greater Element

## Problem Type

```text
For each element, find first greater element on right.
```

## Step-by-Step Approach Before Code

```text
Step 1: Create answer array initialized with -1.

Step 2: Create stack of unresolved indices.

Step 3: Traverse from left to right.

Step 4: While current value is greater than stack top value:
        current is next greater for stack top.
        update answer and pop.

Step 5: Push current index.

Step 6: Remaining indices have no greater element.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> nextGreaterElement(vector<int>& nums) {

    int n = nums.size();

    vector<int> answer(n, -1);

    stack<int> st;

    for (int i = 0; i < n; i++) {

        while (!st.empty() &&
               nums[i] > nums[st.top()]) {

            answer[st.top()] = nums[i];

            st.pop();
        }

        st.push(i);
    }

    return answer;
}

int main() {

    vector<int> nums = {2, 1, 5, 3};

    vector<int> ans =
        nextGreaterElement(nums);

    for (int x : ans) {
        cout << x << " ";
    }

    return 0;
}
```

---

# 11. CP Pattern 2: Daily Temperatures

## Problem Type

```text
For each day, find number of days until warmer temperature.
```

## Step-by-Step Approach Before Code

```text
Step 1: Create answer array initialized with 0.

Step 2: Stack stores indices of days waiting for warmer day.

Step 3: Traverse temperatures.

Step 4: If current temperature is warmer than stack top:
        pop old day and answer = currentIndex - oldIndex.

Step 5: Push current day index.

Step 6: Days left in stack have no warmer future day.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> dailyTemperatures(vector<int>& temp) {

    int n = temp.size();

    vector<int> answer(n, 0);

    stack<int> st;

    for (int i = 0; i < n; i++) {

        while (!st.empty() &&
               temp[i] > temp[st.top()]) {

            int oldIndex = st.top();
            st.pop();

            answer[oldIndex] = i - oldIndex;
        }

        st.push(i);
    }

    return answer;
}

int main() {

    vector<int> temp = {
        73,74,75,71,69,72,76,73
    };

    vector<int> ans =
        dailyTemperatures(temp);

    for (int x : ans) {
        cout << x << " ";
    }

    return 0;
}
```

---

# 12. CP Pattern 3: Next Smaller Element

## Problem Type

```text
For each element, find first smaller element on right.
```

## Step-by-Step Approach Before Code

```text
Step 1: Use increasing stack.

Step 2: Stack stores unresolved indices.

Step 3: Current smaller value resolves larger previous values.

Step 4: Pop while current < stack top value.

Step 5: Set answer of popped index to current value.

Step 6: Push current index.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> nextSmallerElement(vector<int>& nums) {

    int n = nums.size();

    vector<int> answer(n, -1);

    stack<int> st;

    for (int i = 0; i < n; i++) {

        while (!st.empty() &&
               nums[i] < nums[st.top()]) {

            answer[st.top()] = nums[i];

            st.pop();
        }

        st.push(i);
    }

    return answer;
}

int main() {

    vector<int> nums = {4, 8, 5, 2, 25};

    vector<int> ans =
        nextSmallerElement(nums);

    for (int x : ans) {
        cout << x << " ";
    }

    return 0;
}
```

---

# 13. CP Pattern 4: Largest Rectangle In Histogram

## Problem Type

```text
Given histogram heights, find largest rectangle area.
```

## Core Idea

For each bar:

```text
height = heights[i]
width = distance between previous smaller and next smaller
area = height * width
```

Monotonic increasing stack finds boundary.

## Step-by-Step Approach Before Code

```text
Step 1: Use increasing stack of indices.

Step 2: Traverse i from 0 to n.

Step 3: Treat i == n as height 0 sentinel
        to flush remaining bars.

Step 4: While current height < height at stack top:
        popped bar's right boundary is i - 1.

Step 5: After popping:
        left boundary is stack.top() + 1
        or 0 if stack empty.

Step 6: Calculate width and area.

Step 7: Push current index.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int largestRectangleArea(vector<int>& heights) {

    int n = heights.size();

    stack<int> st;

    int best = 0;

    for (int i = 0; i <= n; i++) {

        int currentHeight =
            (i == n ? 0 : heights[i]);

        while (!st.empty() &&
               currentHeight < heights[st.top()]) {

            int height = heights[st.top()];
            st.pop();

            int leftBoundary =
                st.empty() ? 0 : st.top() + 1;

            int rightBoundary = i - 1;

            int width =
                rightBoundary - leftBoundary + 1;

            best = max(best, height * width);
        }

        st.push(i);
    }

    return best;
}

int main() {

    vector<int> heights = {2,1,5,6,2,3};

    cout << largestRectangleArea(heights)
         << endl;

    return 0;
}
```

---

# 14. CP Pattern 5: Sum Of Subarray Minimums

## Problem Type

```text
Find sum of minimum value of every subarray.
```

## Core Idea

Each element contributes as minimum for some subarrays.

Contribution:

```text
arr[i] * countLeft * countRight
```

Where:

```text
countLeft  = number of choices on left where arr[i] remains minimum
countRight = number of choices on right where arr[i] remains minimum
```

Use monotonic stack to find boundaries.

## Step-by-Step Approach Before Code

```text
Step 1: For each index i, find previous strictly smaller element.

Step 2: For each index i, find next smaller or equal element.

Step 3: leftCount = i - previousSmallerIndex.

Step 4: rightCount = nextSmallerOrEqualIndex - i.

Step 5: contribution = arr[i] * leftCount * rightCount.

Step 6: Sum all contributions.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long sumSubarrayMinimums(vector<int>& arr) {

    int n = arr.size();

    vector<int> prevSmaller(n);
    vector<int> nextSmallerOrEqual(n);

    stack<int> st;

    for (int i = 0; i < n; i++) {

        while (!st.empty() &&
               arr[st.top()] > arr[i]) {

            st.pop();
        }

        prevSmaller[i] =
            st.empty() ? -1 : st.top();

        st.push(i);
    }

    while (!st.empty()) {
        st.pop();
    }

    for (int i = n - 1; i >= 0; i--) {

        while (!st.empty() &&
               arr[st.top()] >= arr[i]) {

            st.pop();
        }

        nextSmallerOrEqual[i] =
            st.empty() ? n : st.top();

        st.push(i);
    }

    long long answer = 0;

    for (int i = 0; i < n; i++) {

        long long leftCount =
            i - prevSmaller[i];

        long long rightCount =
            nextSmallerOrEqual[i] - i;

        answer +=
            1LL * arr[i] * leftCount * rightCount;
    }

    return answer;
}

int main() {

    vector<int> arr = {3,1,2,4};

    cout << sumSubarrayMinimums(arr)
         << endl;

    return 0;
}
```

---

# 15. CP Pattern 6: Remove K Digits

## Problem Type

```text
Remove k digits to make smallest possible number.
```

## Core Idea

Use stack as greedy monotonic increasing structure.

If previous digit is larger than current digit:

```text
remove previous digit
```

because smaller current digit should come earlier.

## Step-by-Step Approach Before Code

```text
Step 1: Create string as stack.

Step 2: Traverse each digit.

Step 3: While stack not empty,
        k > 0,
        and stack.back() > current digit:
        pop stack.back().

Step 4: Push current digit.

Step 5: If k remains, remove from end.

Step 6: Remove leading zeroes.

Step 7: If empty, return "0".
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

string removeKdigits(string num, int k) {

    string st;

    for (char digit : num) {

        while (!st.empty() &&
               k > 0 &&
               st.back() > digit) {

            st.pop_back();
            k--;
        }

        st.push_back(digit);
    }

    while (k > 0 && !st.empty()) {
        st.pop_back();
        k--;
    }

    int start = 0;

    while (start < (int)st.size() &&
           st[start] == '0') {

        start++;
    }

    string answer = st.substr(start);

    if (answer.empty()) {
        return "0";
    }

    return answer;
}

int main() {

    string num = "1432219";
    int k = 3;

    cout << removeKdigits(num, k)
         << endl;

    return 0;
}
```

---

# 16. Monotonic Stack vs Normal Stack

| Feature | Normal Stack | Monotonic Stack |
|---|---|---|
| Main idea | LIFO | LIFO + order invariant |
| Stores | history | useful unresolved candidates |
| Pops when | manually needed | current violates monotonic order |
| Used for | parsing/undo | boundaries/ranges |
| Examples | brackets | next greater/histogram |

Shortcut:

```text
Latest unresolved item?
→ stack

Nearest greater/smaller boundary?
→ monotonic stack
```

---

# 17. Common Mistakes

## Mistake 1: Storing Values Instead Of Indices

Usually store indices because you need:

```text
answer[index]
distance
width
left/right boundary
```

---

## Mistake 2: Wrong Comparison Direction

Next greater:

```text
pop while current > top
```

Next smaller:

```text
pop while current < top
```

---

## Mistake 3: Duplicate Handling

For contribution problems, use strict on one side and non-strict on other side.

Example:

```text
previous smaller: >
next smaller or equal: >=
```

This avoids double counting.

---

## Mistake 4: Forgetting Sentinel In Histogram

Use:

```text
i == n with height 0
```

to flush remaining bars.

---

# 18. Complexity Table

| Pattern | Complexity |
|---|---:|
| next greater | O(N) |
| daily temperatures | O(N) |
| next smaller | O(N) |
| largest rectangle | O(N) |
| sum subarray minimums | O(N) |
| remove K digits | O(N) |

Why O(N)?

```text
Each index is pushed once and popped once.
```

---

# 19. Real-World Mapping

| Monotonic Stack Concept | Real-System Meaning |
|---|---|
| next greater | next threshold crossing |
| daily temperatures | alert waiting time |
| histogram rectangle | capacity block analysis |
| boundary detection | signal processing |
| contribution counting | analytics attribution |
| remove K digits | greedy optimization |
| unresolved stack | pending candidates |
| dominated candidate removal | pruning engine |

---

# 20. Final Mental Model

Monotonic stack is:

```text
nearest boundary detection engine
```

Best for:

```text
next greater/smaller
range boundary
histogram
contribution
visibility
greedy removal
```

One-line CP rule:

```text
If current element resolves previous elements by greater/smaller relation, think monotonic stack.
```

One-line system rule:

```text
Monotonic stack prunes dominated candidates and finds nearest useful boundaries in streams.
```

---

# 21. Next Step

Next file:

```text
017_MonotonicDeque_Window_MinMax_Engine.md
```

Then:

```text
018_RangeMapping_Interval_Engine.md
019_SweepLine_Event_Engine.md
020_Production_STL_Pattern_Decision_Engine.md
```
