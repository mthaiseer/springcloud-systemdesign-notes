# Monotonic Stack Phase-Wise Practice Guide

## Clickable Index

- [How to Use This](#how-to-use-this)
- [Phase 1 — Recognition Problems](#phase-1--recognition-problems)
  - [1. Next Greater Element](#1-next-greater-element)
  - [2. Daily Temperatures](#2-daily-temperatures)
  - [3. Final Prices With a Special Discount](#3-final-prices-with-a-special-discount)
  - [4. Stock Span](#4-stock-span)
- [Phase 2 — Boundary / Range Thinking](#phase-2--boundary--range-thinking)
  - [5. Largest Rectangle in Histogram](#5-largest-rectangle-in-histogram)
  - [6. Maximal Rectangle](#6-maximal-rectangle)
  - [7. Height of Soldiers / Maximum of Minimum for Every Window Size](#7-height-of-soldiers--maximum-of-minimum-for-every-window-size)
- [Phase 3 — Contribution Thinking](#phase-3--contribution-thinking)
  - [8. Sum of Subarray Minimums](#8-sum-of-subarray-minimums)
  - [9. Maximum Subarray Min-Product](#9-maximum-subarray-min-product)
- [Phase 4 — Greedy + Monotonic Stack](#phase-4--greedy--monotonic-stack)
  - [10. Remove K Digits](#10-remove-k-digits)
  - [11. Remove Duplicate Letters](#11-remove-duplicate-letters)
- [Phase 5 — Advanced / Hidden Monotonic Stack](#phase-5--advanced--hidden-monotonic-stack)
  - [12. Trapping Rain Water](#12-trapping-rain-water)
  - [13. Maximum Width Ramp](#13-maximum-width-ramp)
  - [14. Number of Visible People in a Queue](#14-number-of-visible-people-in-a-queue)
  - [15. Steps to Make Array Non-Decreasing](#15-steps-to-make-array-non-decreasing)
- [Final Phase Summary](#final-phase-summary)
- [Master Rule](#master-rule)
- [Final Checklist Before Coding](#final-checklist-before-coding)

---

## How to Use This

```text
Phase 1 -> learn stack direction and pop condition
Phase 2 -> learn boundary/range thinking
Phase 3 -> learn contribution thinking
Phase 4 -> learn greedy + stack
Phase 5 -> learn advanced hidden stack forms
```

---

# Phase 1 — Recognition Problems

Goal:

```text
Recognize when current element resolves previous unresolved elements.
```

Core question:

```text
What is the stack waiting for?
```

---

## 1. Next Greater Element

### Problem Idea

For every element, find the first greater element on the right.

Example:

```text
nums = [2, 1, 5, 3]
ans  = [5, 5, -1, -1]
```

### Pattern

```text
Stack type: decreasing stack
Pop when : current > stack top
Meaning  : current is next greater for popped elements
```

### Thinking

```text
Small values are waiting for a bigger value.
When bigger value comes, it resolves them.
```

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> nextGreater(vector<int>& nums) {
    int n = nums.size();
    vector<int> ans(n, -1);
    stack<int> st; // store indices

    for (int i = 0; i < n; i++) {
        while (!st.empty() && nums[i] > nums[st.top()]) {
            ans[st.top()] = nums[i];
            st.pop();
        }
        st.push(i);
    }

    return ans;
}
```
### Dry Run — Array Diagram

```text
nums = [2, 1, 5, 3]
stack stores index:value
```

#### i = 0, current = 2

```text
index:   0   1   2   3
nums:   [2,  1,  5,  3]
         ^

stack before: []
action      : push 0
stack after : [0:2]
answer      : [-1, -1, -1, -1]
```

#### i = 1, current = 1

```text
index:   0   1   2   3
nums:   [2,  1,  5,  3]
             ^

stack before: [0:2]
condition   : 1 > 2? no
action      : push 1
stack after : [0:2, 1:1]
answer      : [-1, -1, -1, -1]
```

#### i = 2, current = 5

```text
index:   0   1   2   3
nums:   [2,  1,  5,  3]
                 ^

stack before: [0:2, 1:1]

5 > 1 -> pop 1 -> answer[1] = 5
5 > 2 -> pop 0 -> answer[0] = 5

action      : push 2
stack after : [2:5]
answer      : [5, 5, -1, -1]
```

Visual:

```text
[2, 1, 5, 3]
 ^  ^  |
 |  |  +-- resolves both 2 and 1
 +-- answer = 5
    +-- answer = 5
```

#### i = 3, current = 3

```text
index:   0   1   2   3
nums:   [2,  1,  5,  3]
                     ^

stack before: [2:5]
condition   : 3 > 5? no
action      : push 3
stack after : [2:5, 3:3]
answer      : [5, 5, -1, -1]
```

### Dry Run After Code

```text
nums = [2, 1, 5, 3]
ans  = [-1, -1, -1, -1]
stack stores indices, values are decreasing from bottom to top

i = 0, nums[i] = 2
stack = []
push 0
stack = [0:2]
ans = [-1, -1, -1, -1]

i = 1, nums[i] = 1
1 > 2? no
push 1
stack = [0:2, 1:1]
ans = [-1, -1, -1, -1]

i = 2, nums[i] = 5
5 > 1 -> pop index 1 -> ans[1] = 5
5 > 2 -> pop index 0 -> ans[0] = 5
push 2
stack = [2:5]
ans = [5, 5, -1, -1]

i = 3, nums[i] = 3
3 > 5? no
push 3
stack = [2:5, 3:3]
ans = [5, 5, -1, -1]
```


---

## 2. Daily Temperatures

### Problem Idea

For each day, find how many days until a warmer temperature.

Example:

```text
temp = [73, 74, 75, 71, 69, 72, 76, 73]
ans  = [1, 1, 4, 2, 1, 1, 0, 0]
```

### Pattern

```text
Stack type: decreasing stack
Pop when : current temperature > stack top temperature
Meaning  : warmer day found
```

### Thinking

Same as Next Greater Element, but answer is distance:

```text
answer[index] = currentIndex - oldIndex
```

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> dailyTemperatures(vector<int>& temp) {
    int n = temp.size();
    vector<int> ans(n, 0);
    stack<int> st; // unresolved indices

    for (int i = 0; i < n; i++) {
        while (!st.empty() && temp[i] > temp[st.top()]) {
            int j = st.top();
            st.pop();
            ans[j] = i - j;
        }
        st.push(i);
    }

    return ans;
}
```
### Dry Run — Array Diagram

```text
temp = [73, 74, 75, 71, 69, 72, 76, 73]
stack stores index:temperature
```

#### i = 0, current = 73

```text
index: [0,  1,  2,  3,  4,  5,  6,  7]
temp : [73, 74, 75, 71, 69, 72, 76, 73]
        ^

stack before: []
action      : push 0
stack after : [0:73]
answer      : [0, 0, 0, 0, 0, 0, 0, 0]
```

#### i = 1, current = 74

```text
index: [0,  1,  2,  3,  4,  5,  6,  7]
temp : [73, 74, 75, 71, 69, 72, 76, 73]
            ^

stack before: [0:73]
74 > 73 -> pop 0 -> answer[0] = 1 - 0 = 1
action      : push 1
stack after : [1:74]
answer      : [1, 0, 0, 0, 0, 0, 0, 0]
```

#### i = 2, current = 75

```text
index: [0,  1,  2,  3,  4,  5,  6,  7]
temp : [73, 74, 75, 71, 69, 72, 76, 73]
                ^

stack before: [1:74]
75 > 74 -> pop 1 -> answer[1] = 2 - 1 = 1
action      : push 2
stack after : [2:75]
answer      : [1, 1, 0, 0, 0, 0, 0, 0]
```

#### i = 3, current = 71

```text
index: [0,  1,  2,  3,  4,  5,  6,  7]
temp : [73, 74, 75, 71, 69, 72, 76, 73]
                    ^

stack before: [2:75]
condition   : 71 > 75? no
action      : push 3
stack after : [2:75, 3:71]
answer      : [1, 1, 0, 0, 0, 0, 0, 0]
```

#### i = 4, current = 69

```text
index: [0,  1,  2,  3,  4,  5,  6,  7]
temp : [73, 74, 75, 71, 69, 72, 76, 73]
                        ^

stack before: [2:75, 3:71]
condition   : 69 > 71? no
action      : push 4
stack after : [2:75, 3:71, 4:69]
answer      : [1, 1, 0, 0, 0, 0, 0, 0]
```

#### i = 5, current = 72

```text
index: [0,  1,  2,  3,  4,  5,  6,  7]
temp : [73, 74, 75, 71, 69, 72, 76, 73]
                            ^

stack before: [2:75, 3:71, 4:69]

72 > 69 -> pop 4 -> answer[4] = 5 - 4 = 1
72 > 71 -> pop 3 -> answer[3] = 5 - 3 = 2
72 > 75? no

action      : push 5
stack after : [2:75, 5:72]
answer      : [1, 1, 0, 2, 1, 0, 0, 0]
```

Visual:

```text
[73, 74, 75, 71, 69, 72, 76, 73]
              ^   ^   |
              |   |   +-- 72 resolves 71 and 69
              |   +------ answer[4] = 1
              +---------- answer[3] = 2
```

#### i = 6, current = 76

```text
index: [0,  1,  2,  3,  4,  5,  6,  7]
temp : [73, 74, 75, 71, 69, 72, 76, 73]
                                ^

stack before: [2:75, 5:72]

76 > 72 -> pop 5 -> answer[5] = 6 - 5 = 1
76 > 75 -> pop 2 -> answer[2] = 6 - 2 = 4

action      : push 6
stack after : [6:76]
answer      : [1, 1, 4, 2, 1, 1, 0, 0]
```

#### i = 7, current = 73

```text
index: [0,  1,  2,  3,  4,  5,  6,  7]
temp : [73, 74, 75, 71, 69, 72, 76, 73]
                                    ^

stack before: [6:76]
condition   : 73 > 76? no
action      : push 7
stack after : [6:76, 7:73]
answer      : [1, 1, 4, 2, 1, 1, 0, 0]
```

### Dry Run After Code

```text
temp = [73, 74, 75, 71, 69, 72, 76, 73]
ans  = [0, 0, 0, 0, 0, 0, 0, 0]
stack stores unresolved day indices

i = 0, 73 -> push 0
stack = [0:73]

i = 1, 74
74 > 73 -> pop 0 -> ans[0] = 1 - 0 = 1
push 1
stack = [1:74]
ans = [1,0,0,0,0,0,0,0]

i = 2, 75
75 > 74 -> pop 1 -> ans[1] = 2 - 1 = 1
push 2
stack = [2:75]
ans = [1,1,0,0,0,0,0,0]

i = 3, 71 -> push 3
stack = [2:75, 3:71]

i = 4, 69 -> push 4
stack = [2:75, 3:71, 4:69]

i = 5, 72
72 > 69 -> pop 4 -> ans[4] = 1
72 > 71 -> pop 3 -> ans[3] = 2
72 > 75? no -> push 5
stack = [2:75, 5:72]
ans = [1,1,0,2,1,0,0,0]

i = 6, 76
76 > 72 -> pop 5 -> ans[5] = 1
76 > 75 -> pop 2 -> ans[2] = 4
push 6
ans = [1,1,4,2,1,1,0,0]

i = 7, 73 -> no pop -> push 7
final ans = [1,1,4,2,1,1,0,0]
```


---

## 3. Final Prices With a Special Discount

### Problem Idea

For each price, subtract the first smaller or equal price on the right.

Example:

```text
prices = [8, 4, 6, 2, 3]
ans    = [4, 2, 4, 2, 3]
```

Because:

```text
8 gets discount 4
4 gets discount 2
6 gets discount 2
2 gets no discount
3 gets no discount
```

### Pattern

```text
Stack type: increasing stack
Pop when : current <= stack top
Meaning  : current is next smaller/equal discount
```

### Thinking

Each price waits for a smaller or equal price after it.

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> finalPrices(vector<int>& prices) {
    int n = prices.size();
    vector<int> ans = prices;
    stack<int> st; // indices waiting for discount

    for (int i = 0; i < n; i++) {
        while (!st.empty() && prices[i] <= prices[st.top()]) {
            ans[st.top()] = prices[st.top()] - prices[i];
            st.pop();
        }
        st.push(i);
    }

    return ans;
}
```
### Dry Run — Array Diagram

```text
prices = [8, 4, 6, 2, 3]
stack stores index:price
answer starts as original prices
```

#### i = 0, current = 8

```text
index :   0   1   2   3   4
prices:  [8,  4,  6,  2,  3]
          ^

stack before: []
action      : push 0
stack after : [0:8]
answer      : [8, 4, 6, 2, 3]
```

#### i = 1, current = 4

```text
index :   0   1   2   3   4
prices:  [8,  4,  6,  2,  3]
              ^

stack before: [0:8]
4 <= 8 -> pop 0 -> answer[0] = 8 - 4 = 4
action      : push 1
stack after : [1:4]
answer      : [4, 4, 6, 2, 3]
```

#### i = 2, current = 6

```text
index :   0   1   2   3   4
prices:  [8,  4,  6,  2,  3]
                  ^

stack before: [1:4]
condition   : 6 <= 4? no
action      : push 2
stack after : [1:4, 2:6]
answer      : [4, 4, 6, 2, 3]
```

#### i = 3, current = 2

```text
index :   0   1   2   3   4
prices:  [8,  4,  6,  2,  3]
                      ^

stack before: [1:4, 2:6]
2 <= 6 -> pop 2 -> answer[2] = 6 - 2 = 4
2 <= 4 -> pop 1 -> answer[1] = 4 - 2 = 2
action      : push 3
stack after : [3:2]
answer      : [4, 2, 4, 2, 3]
```

Visual:

```text
[8, 4, 6, 2, 3]
    ^  ^  |
    |  |  +-- current 2 gives discount to 6 and 4
    |  +----- answer[2] = 4
    +-------- answer[1] = 2
```

#### i = 4, current = 3

```text
index :   0   1   2   3   4
prices:  [8,  4,  6,  2,  3]
                          ^

stack before: [3:2]
condition   : 3 <= 2? no
action      : push 4
stack after : [3:2, 4:3]
answer      : [4, 2, 4, 2, 3]
```

### Dry Run After Code

```text
prices = [8, 4, 6, 2, 3]
ans    = [8, 4, 6, 2, 3]
stack stores indices waiting for discount

i = 0, price = 8
push 0
stack = [0:8]

i = 1, price = 4
4 <= 8 -> pop 0 -> ans[0] = 8 - 4 = 4
push 1
stack = [1:4]
ans = [4,4,6,2,3]

i = 2, price = 6
6 <= 4? no
push 2
stack = [1:4, 2:6]

i = 3, price = 2
2 <= 6 -> pop 2 -> ans[2] = 6 - 2 = 4
2 <= 4 -> pop 1 -> ans[1] = 4 - 2 = 2
push 3
stack = [3:2]
ans = [4,2,4,2,3]

i = 4, price = 3
3 <= 2? no -> push 4
final ans = [4,2,4,2,3]
```


---

## 4. Stock Span

### Problem Idea

For each day, find how many consecutive days before it had price less than or equal to current price.

Example:

```text
prices = [100, 80, 60, 70, 60, 75, 85]
ans    = [1,   1,  1,  2,  1,  4,  6]
```

### Pattern

```text
Stack type: decreasing stack
Pop when : stack top price <= current price
Meaning  : current price absorbs smaller/equal previous prices
```

### Thinking

Find previous greater element.

```text
span = currentIndex - previousGreaterIndex
```

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> stockSpan(vector<int>& prices) {
    int n = prices.size();
    vector<int> ans(n);
    stack<int> st; // indices of previous greater prices

    for (int i = 0; i < n; i++) {
        while (!st.empty() && prices[st.top()] <= prices[i]) {
            st.pop();
        }

        int previousGreater = st.empty() ? -1 : st.top();
        ans[i] = i - previousGreater;

        st.push(i);
    }

    return ans;
}
```
### Dry Run — Array Diagram

```text
prices = [100, 80, 60, 70, 60, 75, 85]
stack stores index:price of previous greater blockers
```

#### i = 0, current = 100

```text
index : [0,   1,  2,  3,  4,  5,  6]
price : [100, 80, 60, 70, 60, 75, 85]
         ^

stack before          : []
previous greater index: -1
span                  : 0 - (-1) = 1
action                : push 0
stack after           : [0:100]
answer                : [1, _, _, _, _, _, _]
```

#### i = 1, current = 80

```text
index : [0,   1,  2,  3,  4,  5,  6]
price : [100, 80, 60, 70, 60, 75, 85]
              ^

stack before          : [0:100]
condition             : 80 >= 100? no
previous greater index: 0
span                  : 1 - 0 = 1
action                : push 1
stack after           : [0:100, 1:80]
answer                : [1, 1, _, _, _, _, _]
```

#### i = 2, current = 60

```text
index : [0,   1,  2,  3,  4,  5,  6]
price : [100, 80, 60, 70, 60, 75, 85]
                  ^

stack before          : [0:100, 1:80]
condition             : 60 >= 80? no
previous greater index: 1
span                  : 2 - 1 = 1
action                : push 2
stack after           : [0:100, 1:80, 2:60]
answer                : [1, 1, 1, _, _, _, _]
```

#### i = 3, current = 70

```text
index : [0,   1,  2,  3,  4,  5,  6]
price : [100, 80, 60, 70, 60, 75, 85]
                      ^

stack before: [0:100, 1:80, 2:60]
70 >= 60 -> pop 2
70 >= 80? no

previous greater index: 1
span                  : 3 - 1 = 2
action                : push 3
stack after           : [0:100, 1:80, 3:70]
answer                : [1, 1, 1, 2, _, _, _]
```

Visual:

```text
[100, 80, 60, 70, 60, 75, 85]
          <--- span --->
              60  70
blocked by 80
```

#### i = 4, current = 60

```text
index : [0,   1,  2,  3,  4,  5,  6]
price : [100, 80, 60, 70, 60, 75, 85]
                          ^

stack before          : [0:100, 1:80, 3:70]
condition             : 60 >= 70? no
previous greater index: 3
span                  : 4 - 3 = 1
action                : push 4
stack after           : [0:100, 1:80, 3:70, 4:60]
answer                : [1, 1, 1, 2, 1, _, _]
```

#### i = 5, current = 75

```text
index : [0,   1,  2,  3,  4,  5,  6]
price : [100, 80, 60, 70, 60, 75, 85]
                              ^

stack before: [0:100, 1:80, 3:70, 4:60]
75 >= 60 -> pop 4
75 >= 70 -> pop 3
75 >= 80? no

previous greater index: 1
span                  : 5 - 1 = 4
action                : push 5
stack after           : [0:100, 1:80, 5:75]
answer                : [1, 1, 1, 2, 1, 4, _]
```

#### i = 6, current = 85

```text
index : [0,   1,  2,  3,  4,  5,  6]
price : [100, 80, 60, 70, 60, 75, 85]
                                  ^

stack before: [0:100, 1:80, 5:75]
85 >= 75 -> pop 5
85 >= 80 -> pop 1
85 >= 100? no

previous greater index: 0
span                  : 6 - 0 = 6
action                : push 6
stack after           : [0:100, 6:85]
answer                : [1, 1, 1, 2, 1, 4, 6]
```

### Dry Run After Code

```text
prices = [100, 80, 60, 70, 60, 75, 85]
stack keeps previous greater blockers

i = 0, price = 100
prevGreater = -1 -> span = 1
stack = [0:100]
ans = [1]

i = 1, price = 80
80 >= 100? no
prevGreater = 0 -> span = 1
stack = [0:100, 1:80]

i = 2, price = 60
60 >= 80? no
prevGreater = 1 -> span = 1
stack = [0:100, 1:80, 2:60]

i = 3, price = 70
70 >= 60 -> pop 2
70 >= 80? no
prevGreater = 1 -> span = 3 - 1 = 2
stack = [0:100, 1:80, 3:70]

i = 4, price = 60
prevGreater = 3 -> span = 1
stack = [0:100, 1:80, 3:70, 4:60]

i = 5, price = 75
75 >= 60 -> pop 4
75 >= 70 -> pop 3
75 >= 80? no
prevGreater = 1 -> span = 5 - 1 = 4
stack = [0:100, 1:80, 5:75]

i = 6, price = 85
85 >= 75 -> pop 5
85 >= 80 -> pop 1
85 >= 100? no
prevGreater = 0 -> span = 6
final ans = [1,1,1,2,1,4,6]
```


---

# Phase 2 — Boundary / Range Thinking

Goal:

```text
For each element, find the maximum range where it remains useful/minimum/maximum.
```

Core question:

```text
How far can this element expand before being blocked?
```

---

## 5. Largest Rectangle in Histogram

### Problem Idea

Find the largest rectangle inside histogram bars.

Example:

```text
heights = [2, 1, 5, 6, 2, 3]
answer = 10
```

### Pattern

```text
Stack type: increasing stack
Pop when : current height < stack top height
Meaning  : current is right smaller boundary
```

### Thinking

Each bar wants to be the minimum height of a rectangle.

For every bar:

```text
width = nextSmallerIndex - previousSmallerIndex - 1
area  = height * width
```

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int largestRectangleArea(vector<int>& h) {
    int n = h.size();
    stack<int> st; // indices
    long long ans = 0;

    for (int i = 0; i <= n; i++) {
        int curr = (i == n ? 0 : h[i]); // sentinel 0

        while (!st.empty() && h[st.top()] > curr) {
            int height = h[st.top()];
            st.pop();

            int right = i;
            int left = st.empty() ? -1 : st.top();
            int width = right - left - 1;

            ans = max(ans, 1LL * height * width);
        }

        st.push(i);
    }

    return (int)ans;
}
```
### Dry Run — Array Diagram

```text
heights = [2, 1, 5, 6, 2, 3]
add sentinel 0 at end
extended = [2, 1, 5, 6, 2, 3, 0]
stack stores index:height
```

#### i = 0, current = 2

```text
index : [0, 1, 2, 3, 4, 5, 6]
height: [2, 1, 5, 6, 2, 3, 0]
         ^

stack before: []
action      : push 0
stack after : [0:2]
answer      : 0
```

#### i = 1, current = 1

```text
index : [0, 1, 2, 3, 4, 5, 6]
height: [2, 1, 5, 6, 2, 3, 0]
            ^

stack before: [0:2]
1 < 2 -> pop 0

height = 2
right  = 1
left   = -1
width  = 1 - (-1) - 1 = 1
area   = 2 * 1 = 2

action      : push 1
stack after : [1:1]
answer      : 2
```

#### i = 2, current = 5

```text
index : [0, 1, 2, 3, 4, 5, 6]
height: [2, 1, 5, 6, 2, 3, 0]
               ^

stack before: [1:1]
condition   : 5 < 1? no
action      : push 2
stack after : [1:1, 2:5]
answer      : 2
```

#### i = 3, current = 6

```text
index : [0, 1, 2, 3, 4, 5, 6]
height: [2, 1, 5, 6, 2, 3, 0]
                  ^

stack before: [1:1, 2:5]
condition   : 6 < 5? no
action      : push 3
stack after : [1:1, 2:5, 3:6]
answer      : 2
```

#### i = 4, current = 2

```text
index : [0, 1, 2, 3, 4, 5, 6]
height: [2, 1, 5, 6, 2, 3, 0]
                     ^

stack before: [1:1, 2:5, 3:6]

2 < 6 -> pop 3
height = 6, right = 4, left = 2, width = 1, area = 6
answer = 6

2 < 5 -> pop 2
height = 5, right = 4, left = 1, width = 2, area = 10
answer = 10

2 < 1? no
action      : push 4
stack after : [1:1, 4:2]
answer      : 10
```

Visual:

```text
[2, 1, 5, 6, 2, 3]
       |-----|  |
       5  6     +-- current 2 is right smaller boundary
height = 5, width = 2, area = 10
```

#### i = 5, current = 3

```text
index : [0, 1, 2, 3, 4, 5, 6]
height: [2, 1, 5, 6, 2, 3, 0]
                        ^

stack before: [1:1, 4:2]
condition   : 3 < 2? no
action      : push 5
stack after : [1:1, 4:2, 5:3]
answer      : 10
```

#### i = 6, current = 0 sentinel

```text
index : [0, 1, 2, 3, 4, 5, 6]
height: [2, 1, 5, 6, 2, 3, 0]
                           ^

stack before: [1:1, 4:2, 5:3]

0 < 3 -> pop 5 -> height=3, left=4, right=6, width=1, area=3
0 < 2 -> pop 4 -> height=2, left=1, right=6, width=4, area=8
0 < 1 -> pop 1 -> height=1, left=-1, right=6, width=6, area=6

action      : push 6
stack after : [6:0]
answer      : 10
```

### Dry Run After Code

```text
heights = [2, 1, 5, 6, 2, 3]
scan with sentinel 0 at i = 6
stack stores increasing heights by index

i = 0, curr = 2 -> push 0 -> stack [0:2]

i = 1, curr = 1
1 < 2 -> pop 0
height = 2, right = 1, left = -1, width = 1, area = 2
ans = 2
push 1 -> stack [1:1]

i = 2, curr = 5 -> push 2 -> stack [1:1, 2:5]
i = 3, curr = 6 -> push 3 -> stack [1:1, 2:5, 3:6]

i = 4, curr = 2
2 < 6 -> pop 3
height = 6, right = 4, left = 2, width = 1, area = 6
ans = 6
2 < 5 -> pop 2
height = 5, right = 4, left = 1, width = 2, area = 10
ans = 10
push 4 -> stack [1:1, 4:2]

i = 5, curr = 3 -> push 5 -> stack [1:1, 4:2, 5:3]

i = 6, curr = 0
pop 5 -> area = 3 * 1 = 3
pop 4 -> area = 2 * 4 = 8
pop 1 -> area = 1 * 6 = 6
final ans = 10
```


---

## 6. Maximal Rectangle

### Problem Idea

Given a binary matrix, find the largest rectangle containing only `1`s.

Example:

```text
1 0 1 0 0
1 0 1 1 1
1 1 1 1 1
1 0 0 1 0

answer = 6
```

### Pattern

```text
Convert each row into histogram.
Then apply Largest Rectangle in Histogram.
```

### Thinking

For each row:

```text
if matrix[r][c] == '1': height[c]++
else height[c] = 0
```

Then solve histogram for that height array.

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int largestRectangleArea(vector<int>& h) {
    int n = h.size();
    stack<int> st;
    int ans = 0;

    for (int i = 0; i <= n; i++) {
        int curr = (i == n ? 0 : h[i]);

        while (!st.empty() && h[st.top()] > curr) {
            int height = h[st.top()];
            st.pop();

            int left = st.empty() ? -1 : st.top();
            int width = i - left - 1;

            ans = max(ans, height * width);
        }

        st.push(i);
    }

    return ans;
}

int maximalRectangle(vector<vector<char>>& matrix) {
    if (matrix.empty()) return 0;

    int rows = matrix.size();
    int cols = matrix[0].size();
    vector<int> height(cols, 0);
    int ans = 0;

    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            if (matrix[r][c] == '1') height[c]++;
            else height[c] = 0;
        }

        ans = max(ans, largestRectangleArea(height));
    }

    return ans;
}
```
### Dry Run — Matrix to Array Diagram

```text
matrix:
1 0 1 0 0
1 0 1 1 1
1 1 1 1 1
1 0 0 1 0

height starts as [0, 0, 0, 0, 0]
```

#### row = 0

```text
row 0 : [1, 0, 1, 0, 0]
height before: [0, 0, 0, 0, 0]

c = 0 -> 1 -> height[0] = 1
c = 1 -> 0 -> height[1] = 0
c = 2 -> 1 -> height[2] = 1
c = 3 -> 0 -> height[3] = 0
c = 4 -> 0 -> height[4] = 0

height after : [1, 0, 1, 0, 0]
histogram max area = 1
answer = 1
```

#### row = 1

```text
row 1 : [1, 0, 1, 1, 1]
height before: [1, 0, 1, 0, 0]

c = 0 -> 1 -> height[0] = 2
c = 1 -> 0 -> height[1] = 0
c = 2 -> 1 -> height[2] = 2
c = 3 -> 1 -> height[3] = 1
c = 4 -> 1 -> height[4] = 1

height after : [2, 0, 2, 1, 1]
histogram max area = 3
answer = 3
```

Visual:

```text
height:
[2, 0, 2, 1, 1]
          |-----|
          min height = 1, width = 3, area = 3
```

#### row = 2

```text
row 2 : [1, 1, 1, 1, 1]
height before: [2, 0, 2, 1, 1]

height after : [3, 1, 3, 2, 2]
histogram max area = 6
answer = 6
```

Visual:

```text
height:
[3, 1, 3, 2, 2]
       |--------|
       columns 2,3,4
       min height = 2
       width = 3
       area = 6
```

#### row = 3

```text
row 3 : [1, 0, 0, 1, 0]
height before: [3, 1, 3, 2, 2]

height after : [4, 0, 0, 3, 0]
histogram max area = 4
answer remains 6
```

### Dry Run After Code

```text
matrix:
1 0 1 0 0
1 0 1 1 1
1 1 1 1 1
1 0 0 1 0

Start height = [0,0,0,0,0]

row 0 = [1,0,1,0,0]
height = [1,0,1,0,0]
largest histogram area = 1
ans = 1

row 1 = [1,0,1,1,1]
height = [2,0,2,1,1]
largest histogram area = 3
ans = 3

row 2 = [1,1,1,1,1]
height = [3,1,3,2,2]
largest histogram area = 6
ans = 6

row 3 = [1,0,0,1,0]
height = [4,0,0,3,0]
largest histogram area = 4
ans remains 6

final answer = 6
```


---

## 7. Height of Soldiers / Maximum of Minimum for Every Window Size

### Problem Idea

For every window size `x`, find the maximum among all window minimums.

Example:

```text
H = [10, 20, 30, 50, 10, 70, 30]
```

For each window size:

```text
size 1 -> max of minimums among all windows of size 1
size 2 -> max of minimums among all windows of size 2
...
```

### Pattern

```text
Same as histogram range boundary.
For each element, find the largest window where it is minimum.
```

### Thinking

For each `H[i]`:

```text
left  = previous smaller index
right = next smaller index
length = right - left - 1

ans[length] = max(ans[length], H[i])
```

Then fill missing values from right to left:

```text
ans[i] = max(ans[i], ans[i + 1])
```

Why?

If a height can be minimum for a bigger window, it can also be candidate for smaller window.

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> maxOfMinimums(vector<int>& a) {
    int n = a.size();
    vector<int> left(n), right(n);
    stack<int> st;

    // previous smaller
    for (int i = 0; i < n; i++) {
        while (!st.empty() && a[st.top()] >= a[i]) {
            st.pop();
        }
        left[i] = st.empty() ? -1 : st.top();
        st.push(i);
    }

    while (!st.empty()) st.pop();

    // next smaller
    for (int i = n - 1; i >= 0; i--) {
        while (!st.empty() && a[st.top()] >= a[i]) {
            st.pop();
        }
        right[i] = st.empty() ? n : st.top();
        st.push(i);
    }

    vector<int> ans(n + 1, 0);

    for (int i = 0; i < n; i++) {
        int len = right[i] - left[i] - 1;
        ans[len] = max(ans[len], a[i]);
    }

    for (int len = n - 1; len >= 1; len--) {
        ans[len] = max(ans[len], ans[len + 1]);
    }

    vector<int> result;
    for (int len = 1; len <= n; len++) {
        result.push_back(ans[len]);
    }

    return result;
}
```
### Dry Run — Array Diagram

```text
a = [10, 20, 30, 50, 10, 70, 30]
stack stores index:value
```

#### Build previous smaller

```text
index:  0   1   2   3   4   5   6
a    : [10, 20, 30, 50, 10, 70, 30]
```

```text
i=0 current=10 -> stack [] -> left[0]=-1 -> push 0
stack after: [0:10]

i=1 current=20 -> 20 > 10 -> left[1]=0 -> push 1
stack after: [0:10, 1:20]

i=2 current=30 -> 30 > 20 -> left[2]=1 -> push 2
stack after: [0:10, 1:20, 2:30]

i=3 current=50 -> 50 > 30 -> left[3]=2 -> push 3
stack after: [0:10, 1:20, 2:30, 3:50]

i=4 current=10
50 >= 10 -> pop 3
30 >= 10 -> pop 2
20 >= 10 -> pop 1
10 >= 10 -> pop 0
left[4] = -1 -> push 4

i=5 current=70 -> 70 > 10 -> left[5]=4 -> push 5
i=6 current=30 -> 70 >= 30 pop 5 -> left[6]=4 -> push 6

left = [-1, 0, 1, 2, -1, 4, 4]
```

#### Build next smaller

```text
scan from right to left

i=6 current=30 -> right[6]=7 -> push 6
i=5 current=70 -> right[5]=6 -> push 5
i=4 current=10 -> pop 5, pop 6 -> right[4]=7 -> push 4
i=3 current=50 -> right[3]=4 -> push 3
i=2 current=30 -> pop 3 -> right[2]=4 -> push 2
i=1 current=20 -> pop 2 -> right[1]=4 -> push 1
i=0 current=10 -> pop 1, pop 4 -> right[0]=7 -> push 0

right = [7, 4, 4, 4, 7, 6, 7]
```

#### Map each value to its maximum window length

```text
i=0 value=10 -> len = 7 - (-1) - 1 = 7 -> ans[7]=10
i=1 value=20 -> len = 4 - 0 - 1 = 3 -> ans[3]=20
i=2 value=30 -> len = 4 - 1 - 1 = 2 -> ans[2]=30
i=3 value=50 -> len = 4 - 2 - 1 = 1 -> ans[1]=50
i=4 value=10 -> len = 7 - (-1) - 1 = 7 -> ans[7]=10
i=5 value=70 -> len = 6 - 4 - 1 = 1 -> ans[1]=70
i=6 value=30 -> len = 7 - 4 - 1 = 2 -> ans[2]=30

before fill:
ans by len = [70, 30, 20, 0, 0, 0, 10]
```

#### Fill missing lengths

```text
len 6 -> max(0,10)=10
len 5 -> max(0,10)=10
len 4 -> max(0,10)=10
len 3 -> max(20,10)=20

final result = [70, 30, 20, 10, 10, 10, 10]
```

### Dry Run After Code

```text
a = [10, 20, 30, 50, 10, 70, 30]

previous smaller left indices:
left = [-1, 0, 1, 2, -1, 4, 4]

next smaller right indices:
right = [7, 4, 4, 4, 7, 6, 7]

For each i, len = right[i] - left[i] - 1:
i=0, value=10, len=7 -> ans[7] = 10
i=1, value=20, len=3 -> ans[3] = 20
i=2, value=30, len=2 -> ans[2] = 30
i=3, value=50, len=1 -> ans[1] = 50
i=4, value=10, len=7 -> ans[7] = 10
i=5, value=70, len=1 -> ans[1] = 70
i=6, value=30, len=2 -> ans[2] = 30

before fill:
ans by len = [70, 30, 20, 0, 0, 0, 10]

fill right to left:
len 6 -> max(0,10)=10
len 5 -> 10
len 4 -> 10
len 3 -> max(20,10)=20

final result = [70, 30, 20, 10, 10, 10, 10]
```


---

# Phase 3 — Contribution Thinking

Goal:

```text
Instead of finding answer for every subarray, count how much each element contributes.
```

Core question:

```text
For how many subarrays is this element the minimum/maximum?
```

---

## 8. Sum of Subarray Minimums

### Problem Idea

Find sum of minimum value of every subarray.

Example:

```text
arr = [3, 1, 2, 4]
answer = 17
```

### Pattern

```text
Previous smaller + next smaller/equal
Contribution = value * leftChoices * rightChoices
```

### Thinking

For each element `arr[i]`:

```text
leftChoices  = number of choices to extend left
rightChoices = number of choices to extend right
contribution = arr[i] * leftChoices * rightChoices
```

Tie handling:

```text
previous strictly smaller -> pop >=
next smaller or equal     -> pop >
```

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int sumSubarrayMins(vector<int>& arr) {
    const int MOD = 1e9 + 7;
    int n = arr.size();
    vector<int> left(n), right(n);
    stack<int> st;

    // previous strictly smaller distance
    for (int i = 0; i < n; i++) {
        while (!st.empty() && arr[st.top()] >= arr[i]) {
            st.pop();
        }
        left[i] = st.empty() ? i + 1 : i - st.top();
        st.push(i);
    }

    while (!st.empty()) st.pop();

    // next smaller or equal distance
    for (int i = n - 1; i >= 0; i--) {
        while (!st.empty() && arr[st.top()] > arr[i]) {
            st.pop();
        }
        right[i] = st.empty() ? n - i : st.top() - i;
        st.push(i);
    }

    long long ans = 0;
    for (int i = 0; i < n; i++) {
        ans = (ans + 1LL * arr[i] * left[i] * right[i]) % MOD;
    }

    return ans;
}
```
### Dry Run — Array Diagram

```text
arr = [3, 1, 2, 4]
stack stores index:value
```

#### Build left distances

```text
index:  0   1   2   3
arr  : [3,  1,  2,  4]
```

```text
i = 0, current = 3
stack before: []
left[0] = 1
stack after : [0:3]
left        : [1, _, _, _]

i = 1, current = 1
stack before: [0:3]
3 >= 1 -> pop 0
left[1] = 2
stack after : [1:1]
left        : [1, 2, _, _]

i = 2, current = 2
stack before: [1:1]
1 >= 2? no
left[2] = 2 - 1 = 1
stack after : [1:1, 2:2]
left        : [1, 2, 1, _]

i = 3, current = 4
stack before: [1:1, 2:2]
2 >= 4? no
left[3] = 3 - 2 = 1
stack after : [1:1, 2:2, 3:4]
left        : [1, 2, 1, 1]
```

#### Build right distances

```text
i = 3, current = 4 -> right[3] = 1 -> stack [3:4]
i = 2, current = 2 -> 4 > 2 pop -> right[2] = 2 -> stack [2:2]
i = 1, current = 1 -> 2 > 1 pop -> right[1] = 3 -> stack [1:1]
i = 0, current = 3 -> 1 > 3? no -> right[0] = 1 -> stack [1:1, 0:3]

right = [1, 3, 2, 1]
```

#### Contribution

```text
index:      0   1   2   3
arr:       [3,  1,  2,  4]
left:      [1,  2,  1,  1]
right:     [1,  3,  2,  1]

i = 0 -> 3 * 1 * 1 = 3
i = 1 -> 1 * 2 * 3 = 6
i = 2 -> 2 * 1 * 2 = 4
i = 3 -> 4 * 1 * 1 = 4

answer = 17
```

Visual:

```text
[3, 1, 2, 4]
 |-----------|
      1 is minimum for 2 left choices * 3 right choices
```

### Dry Run After Code

```text
arr = [3, 1, 2, 4]

left distance using previous strictly smaller:
i=0, 3 -> left[0]=1
i=1, 1 -> pop 3 -> left[1]=2
i=2, 2 -> previous smaller is 1 -> left[2]=1
i=3, 4 -> previous smaller is 2 -> left[3]=1
left = [1,2,1,1]

right distance using next smaller/equal:
i=3, 4 -> right[3]=1
i=2, 2 -> pop 4 -> right[2]=2
i=1, 1 -> pop 2 -> right[1]=3
i=0, 3 -> next smaller/equal is 1 -> right[0]=1
right = [1,3,2,1]

contribution:
3 * 1 * 1 = 3
1 * 2 * 3 = 6
2 * 1 * 2 = 4
4 * 1 * 1 = 4

answer = 3 + 6 + 4 + 4 = 17
```


---

## 9. Maximum Subarray Min-Product

### Problem Idea

For every subarray:

```text
min(subarray) * sum(subarray)
```

Return maximum value.

Example:

```text
nums = [1, 2, 3, 2]
answer = 14
```

Subarray `[2,3,2]`:

```text
minimum = 2
sum = 7
product = 14
```

### Pattern

```text
Histogram boundary + prefix sum
```

### Thinking

For each element as minimum:

```text
find max range where nums[i] is minimum
range sum using prefix sum
candidate = nums[i] * rangeSum
```

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int maxSumMinProduct(vector<int>& nums) {
    const int MOD = 1e9 + 7;
    int n = nums.size();

    vector<long long> pref(n + 1, 0);
    for (int i = 0; i < n; i++) {
        pref[i + 1] = pref[i] + nums[i];
    }

    stack<int> st;
    long long ans = 0;

    for (int i = 0; i <= n; i++) {
        long long curr = (i == n ? 0 : nums[i]);

        while (!st.empty() && nums[st.top()] > curr) {
            int idx = st.top();
            st.pop();

            int left = st.empty() ? -1 : st.top();
            int right = i;

            long long rangeSum = pref[right] - pref[left + 1];
            ans = max(ans, rangeSum * nums[idx]);
        }

        st.push(i);
    }

    return ans % MOD;
}
```
### Dry Run — Array Diagram

```text
nums = [1, 2, 3, 2]
prefix = [0, 1, 3, 6, 8]
stack stores index:value
sentinel 0 is used at the end
```

#### i = 0, current = 1

```text
index:  0   1   2   3   4
nums : [1,  2,  3,  2,  0]
        ^

stack before: []
action      : push 0
stack after : [0:1]
answer      : 0
```

#### i = 1, current = 2

```text
stack before: [0:1]
condition   : 2 < 1? no
action      : push 1
stack after : [0:1, 1:2]
answer      : 0
```

#### i = 2, current = 3

```text
stack before: [0:1, 1:2]
condition   : 3 < 2? no
action      : push 2
stack after : [0:1, 1:2, 2:3]
answer      : 0
```

#### i = 3, current = 2

```text
index:  0   1   2   3   4
nums : [1,  2,  3,  2,  0]
                    ^

stack before: [0:1, 1:2, 2:3]
2 < 3 -> pop 2

left     = 1
right    = 3
rangeSum = prefix[3] - prefix[2] = 6 - 3 = 3
product  = 3 * 3 = 9

action      : push 3
stack after : [0:1, 1:2, 3:2]
answer      : 9
```

#### i = 4, current = 0 sentinel

```text
stack before: [0:1, 1:2, 3:2]

0 < 2 -> pop 3
rangeSum = prefix[4] - prefix[2] = 8 - 3 = 5
product  = 2 * 5 = 10
answer   = 10

0 < 2 -> pop 1
rangeSum = prefix[4] - prefix[1] = 8 - 1 = 7
product  = 2 * 7 = 14
answer   = 14

0 < 1 -> pop 0
rangeSum = prefix[4] - prefix[0] = 8
product  = 1 * 8 = 8
answer   = 14
```

Visual:

```text
[1, 2, 3, 2]
    |------|
    min = 2, sum = 7, product = 14
```

### Dry Run After Code

```text
nums = [1, 2, 3, 2]
prefix = [0, 1, 3, 6, 8]

Use increasing stack. When current is smaller, finalize popped value as minimum.

i = 0, curr = 1 -> push 0 -> stack [0:1]
i = 1, curr = 2 -> push 1 -> stack [0:1, 1:2]
i = 2, curr = 3 -> push 2 -> stack [0:1, 1:2, 2:3]

i = 3, curr = 2
2 < 3 -> pop index 2
left = 1, right = 3
rangeSum = prefix[3] - prefix[2] = 6 - 3 = 3
product = 3 * nums[2] = 3 * 3 = 9
ans = 9
push 3 -> stack [0:1, 1:2, 3:2]

i = 4, sentinel curr = 0
pop index 3 value 2
left = 1, right = 4
rangeSum = prefix[4] - prefix[2] = 8 - 3 = 5
product = 5 * 2 = 10
ans = 10

pop index 1 value 2
left = 0, right = 4
rangeSum = prefix[4] - prefix[1] = 8 - 1 = 7
product = 7 * 2 = 14
ans = 14

pop index 0 value 1
rangeSum = prefix[4] - prefix[0] = 8
product = 8 * 1 = 8

final answer = 14
```


---

# Phase 4 — Greedy + Monotonic Stack

Goal:

```text
Use stack to remove bad previous decisions when a better current option appears.
```

Core question:

```text
Can current element improve answer by removing previous elements?
```

---

## 10. Remove K Digits

### Problem Idea

Remove `k` digits to make the smallest possible number.

Example:

```text
num = "1432219", k = 3
answer = "1219"
```

### Pattern

```text
Stack type: increasing stack
Pop when : previous digit > current digit and k > 0
Meaning  : remove bad larger previous digit
```

### Thinking

A bigger digit before a smaller digit makes the number larger.

So remove it if allowed.

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

string removeKdigits(string num, int k) {
    string st;

    for (char c : num) {
        while (!st.empty() && k > 0 && st.back() > c) {
            st.pop_back();
            k--;
        }
        st.push_back(c);
    }

    while (k > 0 && !st.empty()) {
        st.pop_back();
        k--;
    }

    int i = 0;
    while (i < (int)st.size() && st[i] == '0') i++;

    string ans = st.substr(i);
    return ans.empty() ? "0" : ans;
}
```
### Dry Run — String Diagram

```text
num = "1432219"
k = 3
stack stores chosen digits as a string
```

#### read char = '1'

```text
chars: [1, 4, 3, 2, 2, 1, 9]
        ^

stack before: ""
action      : push 1
stack after : "1"
k           : 3
```

#### read char = '4'

```text
chars: [1, 4, 3, 2, 2, 1, 9]
           ^

stack before: "1"
condition   : 1 > 4? no
action      : push 4
stack after : "14"
k           : 3
```

#### read char = '3'

```text
chars: [1, 4, 3, 2, 2, 1, 9]
              ^

stack before: "14"
4 > 3 and k > 0 -> pop 4
k becomes 2
action      : push 3
stack after : "13"
```

Visual:

```text
1 4 3 2 2 1 9
  ^ |
  | +-- smaller 3 arrives
  +---- remove 4
```

#### read char = '2'

```text
stack before: "13"
3 > 2 and k > 0 -> pop 3
k becomes 1
action      : push 2
stack after : "12"
```

#### read char = second '2'

```text
stack before: "12"
condition   : 2 > 2? no
action      : push 2
stack after : "122"
k           : 1
```

#### read char = '1'

```text
stack before: "122"
2 > 1 and k > 0 -> pop last 2
k becomes 0
action      : push 1
stack after : "121"
```

#### read char = '9'

```text
stack before: "121"
condition   : k == 0, cannot pop
action      : push 9
stack after : "1219"
```

Final:

```text
answer = "1219"
```

### Dry Run After Code

```text
num = "1432219", k = 3
stack string keeps digits increasing

read 1 -> stack = "1", k = 3
read 4 -> 1 > 4? no -> stack = "14", k = 3
read 3 -> 4 > 3 -> pop 4 -> stack = "1", k = 2 -> push 3 -> "13"
read 2 -> 3 > 2 -> pop 3 -> stack = "1", k = 1 -> push 2 -> "12"
read 2 -> 2 > 2? no -> push -> "122"
read 1 -> 2 > 1 -> pop last 2 -> stack = "12", k = 0 -> push 1 -> "121"
read 9 -> k = 0, push -> "1219"

remove leading zeros: none
answer = "1219"
```


---

## 11. Remove Duplicate Letters

### Problem Idea

Remove duplicate letters so every letter appears once and result is lexicographically smallest.

Example:

```text
s = "cbacdcbc"
answer = "acdb"
```

### Pattern

```text
Stack type: increasing lexicographic stack
Pop when : stack top > current char AND stack top appears later
Meaning  : remove bad bigger char safely
```

### Thinking

You can remove a previous bigger character only if it appears again later.

Need:

```text
frequency count
visited array
stack string
```

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

string removeDuplicateLetters(string s) {
    vector<int> freq(26, 0), used(26, 0);

    for (char c : s) freq[c - 'a']++;

    string st;

    for (char c : s) {
        int id = c - 'a';
        freq[id]--;

        if (used[id]) continue;

        while (!st.empty() && st.back() > c && freq[st.back() - 'a'] > 0) {
            used[st.back() - 'a'] = 0;
            st.pop_back();
        }

        st.push_back(c);
        used[id] = 1;
    }

    return st;
}
```
### Dry Run — String Diagram

```text
s = "cbacdcbc"
initial frequency:
c = 4, b = 2, a = 1, d = 1

stack stores chosen letters
```

#### i = 0, current = c

```text
chars: [c, b, a, c, d, c, b, c]
        ^

freq[c] after decrement: 3
stack before: ""
used[c]     : false
action      : push c
stack after : "c"
```

#### i = 1, current = b

```text
chars: [c, b, a, c, d, c, b, c]
           ^

freq[b] after decrement: 1
stack before: "c"

c > b and freq[c] > 0 -> pop c
action      : push b
stack after : "b"
```

#### i = 2, current = a

```text
chars: [c, b, a, c, d, c, b, c]
              ^

freq[a] after decrement: 0
stack before: "b"

b > a and freq[b] > 0 -> pop b
action      : push a
stack after : "a"
```

#### i = 3, current = c

```text
stack before: "a"
condition   : a > c? no
action      : push c
stack after : "ac"
```

#### i = 4, current = d

```text
stack before: "ac"
condition   : c > d? no
action      : push d
stack after : "acd"
```

#### i = 5, current = c

```text
used[c] = true
action  : skip
stack   : "acd"
```

#### i = 6, current = b

```text
stack before: "acd"
d > b? yes
freq[d] > 0? no
cannot pop d because d does not appear later
action      : push b
stack after : "acdb"
```

#### i = 7, current = c

```text
used[c] = true
action  : skip
answer  : "acdb"
```

### Dry Run After Code

```text
s = "cbacdcbc"
initial freq: c=4, b=2, a=1, d=1
stack = ""

read c: freq[c]=3, used[c]=false -> push c
stack = "c"

read b: freq[b]=1
c > b and c appears later -> pop c
push b
stack = "b"

read a: freq[a]=0
b > a and b appears later -> pop b
push a
stack = "a"

read c: freq[c]=2 -> push c
stack = "ac"

read d: freq[d]=0 -> c > d? no -> push d
stack = "acd"

read c: already used -> skip
stack = "acd"

read b: freq[b]=0
d > b but d does not appear later -> cannot pop d
push b
stack = "acdb"

read c: already used -> skip
answer = "acdb"
```


---

# Phase 5 — Advanced / Hidden Monotonic Stack

Goal:

```text
Solve problems where monotonic stack is not obvious from statement.
```

Core question:

```text
Who blocks whom?
Who removes whom?
Who becomes useless?
```

---

## 12. Trapping Rain Water

### Problem Idea

Given heights, calculate trapped rain water.

Example:

```text
height = [0,1,0,2,1,0,1,3,2,1,2,1]
answer = 6
```

### Pattern

```text
Stack type: decreasing stack
Pop when : current height > stack top height
Meaning  : current is right wall
```

### Thinking

When current bar is higher, it may form a container with a previous left wall.

```text
water = width * boundedHeight
boundedHeight = min(leftWall, rightWall) - bottom
```

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int trap(vector<int>& height) {
    int n = height.size();
    stack<int> st;
    int water = 0;

    for (int i = 0; i < n; i++) {
        while (!st.empty() && height[i] > height[st.top()]) {
            int bottom = st.top();
            st.pop();

            if (st.empty()) break;

            int left = st.top();
            int width = i - left - 1;
            int boundedHeight = min(height[left], height[i]) - height[bottom];

            water += width * boundedHeight;
        }

        st.push(i);
    }

    return water;
}
```
### Dry Run — Array Diagram

```text
height = [2, 0, 2]
stack stores index:height
```

#### i = 0, current = 2

```text
index :   0   1   2
height:  [2,  0,  2]
          ^

stack before: []
action      : push 0
stack after : [0:2]
water       : 0
```

#### i = 1, current = 0

```text
index :   0   1   2
height:  [2,  0,  2]
              ^

stack before: [0:2]
condition   : 0 > 2? no
action      : push 1
stack after : [0:2, 1:0]
water       : 0
```

#### i = 2, current = 2

```text
index :   0   1   2
height:  [2,  0,  2]
                  ^

stack before: [0:2, 1:0]

2 > 0 -> pop bottom index 1

left wall index  = 0
right wall index = 2
bottom height    = 0

width         = 2 - 0 - 1 = 1
boundedHeight = min(2, 2) - 0 = 2
water added   = 1 * 2 = 2

2 > height[0]? 2 > 2? no
action      : push 2
stack after : [0:2, 2:2]
water       : 2
```

Visual:

```text
index :  0   1   2
height: [2,  0,  2]
         |~~~~~~~|
             water
```

### Dry Run After Code

```text
Use small example: height = [2, 0, 2]
stack stores indices of decreasing bars

i = 0, height = 2
stack empty -> push 0
stack = [0:2], water = 0

i = 1, height = 0
0 > 2? no -> push 1
stack = [0:2, 1:0], water = 0

i = 2, height = 2
2 > 0 -> pop bottom index 1
left wall index = 0
right wall index = 2
width = 2 - 0 - 1 = 1
boundedHeight = min(2,2) - 0 = 2
water += 1 * 2 = 2

2 > height[0]? no because equal
push 2
stack = [0:2, 2:2]
final water = 2
```


---

## 13. Maximum Width Ramp

### Problem Idea

Find maximum `j - i` such that:

```text
i < j and nums[i] <= nums[j]
```

Example:

```text
nums = [6, 0, 8, 2, 1, 5]
answer = 4
```

Ramp:

```text
i = 1, nums[i] = 0
j = 5, nums[j] = 5
width = 4
```

### Pattern

```text
Build decreasing stack of candidate left indices.
Scan from right and resolve candidates.
```

### Thinking

Only keep useful left candidates.

If a previous value is smaller, bigger previous values are not useful as left boundary.

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int maxWidthRamp(vector<int>& nums) {
    int n = nums.size();
    stack<int> st;

    // candidate left indices with decreasing values
    for (int i = 0; i < n; i++) {
        if (st.empty() || nums[i] < nums[st.top()]) {
            st.push(i);
        }
    }

    int ans = 0;

    // scan from right to maximize width
    for (int j = n - 1; j >= 0; j--) {
        while (!st.empty() && nums[st.top()] <= nums[j]) {
            ans = max(ans, j - st.top());
            st.pop();
        }
    }

    return ans;
}
```
### Dry Run — Array Diagram

```text
nums = [6, 0, 8, 2, 1, 5]
stack stores candidate left index:value
```

#### Build decreasing candidate stack

```text
index:   0   1   2   3   4   5
nums :  [6,  0,  8,  2,  1,  5]
```

```text
i = 0, current = 6 -> push 0 -> stack [0:6]
i = 1, current = 0 -> 0 < 6 -> push 1 -> stack [0:6, 1:0]
i = 2, current = 8 -> 8 < 0? no -> skip
i = 3, current = 2 -> 2 < 0? no -> skip
i = 4, current = 1 -> 1 < 0? no -> skip
i = 5, current = 5 -> 5 < 0? no -> skip

candidate stack = [0:6, 1:0]
```

#### Scan from right

```text
j = 5, current = 5
index:   0   1   2   3   4   5
nums :  [6,  0,  8,  2,  1,  5]
                          ^

stack before: [0:6, 1:0]
nums[1] = 0 <= 5 -> width = 5 - 1 = 4
answer = 4
pop 1
nums[0] = 6 <= 5? no

stack after : [0:6]
answer      : 4
```

Visual:

```text
[6, 0, 8, 2, 1, 5]
    |------------|
    i = 1        j = 5
    0 <= 5
    width = 4
```

```text
j = 4, current = 1 -> nums[0]=6 <= 1? no
j = 3, current = 2 -> nums[0]=6 <= 2? no
j = 2, current = 8 -> nums[0]=6 <= 8 yes
width = 2 - 0 = 2, answer remains 4, pop 0

stack empty
final answer = 4
```

### Dry Run After Code

```text
nums = [6, 0, 8, 2, 1, 5]

Build decreasing candidate-left stack:
i=0, 6 -> push 0 -> [0:6]
i=1, 0 -> 0 < 6 -> push 1 -> [0:6, 1:0]
i=2, 8 -> not smaller -> skip
i=3, 2 -> not smaller than 0 -> skip
i=4, 1 -> skip
i=5, 5 -> skip

Scan from right:
j=5, nums[j]=5
nums[1]=0 <= 5 -> width = 5 - 1 = 4, ans = 4, pop 1
nums[0]=6 <= 5? no

j=4, nums[j]=1 -> 6 <= 1? no
j=3, nums[j]=2 -> 6 <= 2? no
j=2, nums[j]=8
nums[0]=6 <= 8 -> width = 2 - 0 = 2, ans remains 4, pop 0

stack empty -> final answer = 4
```


---

## 14. Number of Visible People in a Queue

### Problem Idea

Each person can see people to the right until a taller or equal person blocks the view.

Example:

```text
heights = [10, 6, 8, 5, 11, 9]
answer  = [3, 1, 2, 1, 1, 0]
```

### Pattern

```text
Stack type: decreasing stack from right to left
Pop when : current height > stack top
Meaning  : current can see popped shorter people
```

### Thinking

From right to left:

```text
Pop all shorter people: current can see them.
If one taller/equal remains: current can also see that blocker.
```

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> canSeePersonsCount(vector<int>& heights) {
    int n = heights.size();
    vector<int> ans(n, 0);
    stack<int> st; // heights to the right

    for (int i = n - 1; i >= 0; i--) {
        while (!st.empty() && heights[i] > st.top()) {
            ans[i]++;
            st.pop();
        }

        if (!st.empty()) ans[i]++; // sees first taller/equal blocker

        st.push(heights[i]);
    }

    return ans;
}
```
### Dry Run — Array Diagram

```text
heights = [10, 6, 8, 5, 11, 9]
scan from right to left
stack stores visible blocker heights
```

#### i = 5, current = 9

```text
index  :   0   1  2  3   4   5
heights: [10, 6, 8, 5, 11, 9]
                              ^

stack before: []
action      : push 9
stack after : [9]
answer      : [0, 0, 0, 0, 0, 0]
```

#### i = 4, current = 11

```text
stack before: [9]
11 > 9 -> see 9, pop 9
answer[4] = 1
action      : push 11
stack after : [11]
answer      : [0, 0, 0, 0, 1, 0]
```

#### i = 3, current = 5

```text
stack before: [11]
5 > 11? no
sees blocker 11 -> answer[3] = 1
action      : push 5
stack after : [11, 5]
answer      : [0, 0, 0, 1, 1, 0]
```

#### i = 2, current = 8

```text
stack before: [11, 5]
8 > 5 -> see 5, pop 5
8 > 11? no
sees blocker 11 -> answer[2] = 2
action      : push 8
stack after : [11, 8]
answer      : [0, 0, 2, 1, 1, 0]
```

#### i = 1, current = 6

```text
stack before: [11, 8]
6 > 8? no
sees blocker 8 -> answer[1] = 1
action      : push 6
stack after : [11, 8, 6]
answer      : [0, 1, 2, 1, 1, 0]
```

#### i = 0, current = 10

```text
stack before: [11, 8, 6]
10 > 6 -> see 6, pop 6
10 > 8 -> see 8, pop 8
10 > 11? no
sees blocker 11 -> answer[0] = 3
action      : push 10
stack after : [11, 10]
answer      : [3, 1, 2, 1, 1, 0]
```

Visual:

```text
[10, 6, 8, 5, 11, 9]
  |  |  |      |
  |  |  |      +-- 11 blocks
  |  |  +--------- sees 8
  |  +------------ sees 6
  +--------------- answer[0] = 3
```

### Dry Run After Code

```text
heights = [10, 6, 8, 5, 11, 9]
scan right to left
stack stores visible blockers to the right

i = 5, h = 9
stack empty -> ans[5] = 0 -> push 9
stack = [9]

i = 4, h = 11
11 > 9 -> see 9, pop, ans[4] = 1
stack empty -> push 11
stack = [11]

i = 3, h = 5
5 > 11? no
sees blocker 11 -> ans[3] = 1
push 5 -> stack = [11,5]

i = 2, h = 8
8 > 5 -> see 5, pop, ans[2] = 1
8 > 11? no
sees blocker 11 -> ans[2] = 2
push 8 -> stack = [11,8]

i = 1, h = 6
6 > 8? no
sees blocker 8 -> ans[1] = 1
push 6 -> stack = [11,8,6]

i = 0, h = 10
10 > 6 -> see 6, pop, ans[0] = 1
10 > 8 -> see 8, pop, ans[0] = 2
10 > 11? no
sees blocker 11 -> ans[0] = 3

final ans = [3,1,2,1,1,0]
```


---

## 15. Steps to Make Array Non-Decreasing

### Problem Idea

In one step, remove every element `nums[i]` where:

```text
nums[i - 1] > nums[i]
```

Return number of steps until array becomes non-decreasing.

Example:

```text
nums = [5, 3, 4, 4, 7, 3, 6, 11, 8, 5, 11]
answer = 3
```

### Pattern

```text
Monotonic stack + DP state
```

### Thinking

Each element may die after some number of rounds.

Stack stores:

```text
(value, stepsToDie)
```

When current is greater/equal, it removes smaller/equal previous dependencies.

### Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int totalSteps(vector<int>& nums) {
    int ans = 0;
    stack<pair<int,int>> st; // {value, steps needed to remove}

    for (int x : nums) {
        int steps = 0;

        while (!st.empty() && x >= st.top().first) {
            steps = max(steps, st.top().second);
            st.pop();
        }

        if (st.empty()) {
            steps = 0;
        } else {
            steps += 1;
        }

        ans = max(ans, steps);
        st.push({x, steps});
    }

    return ans;
}
```
### Dry Run — Array Diagram

```text
nums = [5, 3, 4, 4, 7, 3, 6, 11, 8, 5, 11]
stack stores value:stepsToDie
```

#### x = 5

```text
processed: [5]
            ^

stack before: []
steps       : 0
action      : push {5,0}
stack after : [5:0]
answer      : 0
```

#### x = 3

```text
processed: [5, 3]
               ^

stack before: [5:0]
3 >= 5? no
steps       : 1
action      : push {3,1}
stack after : [5:0, 3:1]
answer      : 1
```

#### x = 4

```text
processed: [5, 3, 4]
                  ^

stack before: [5:0, 3:1]
4 >= 3 -> pop {3,1}
steps = max(0, 1) = 1
4 >= 5? no
bigger left exists -> steps = 1 + 1 = 2

action      : push {4,2}
stack after : [5:0, 4:2]
answer      : 2
```

#### x = 4

```text
stack before: [5:0, 4:2]
4 >= 4 -> pop {4,2}
steps = 2
4 >= 5? no
bigger left exists -> steps = 3
action      : push {4,3}
stack after : [5:0, 4:3]
answer      : 3
```

#### x = 7

```text
stack before: [5:0, 4:3]
7 >= 4 -> pop {4,3}, steps = 3
7 >= 5 -> pop {5,0}, steps = 3
stack empty -> steps = 0
action      : push {7,0}
stack after : [7:0]
answer      : 3
```

#### x = 3

```text
stack before: [7:0]
3 >= 7? no
steps       : 1
action      : push {3,1}
stack after : [7:0, 3:1]
answer      : 3
```

#### x = 6

```text
stack before: [7:0, 3:1]
6 >= 3 -> pop {3,1}, steps = 1
6 >= 7? no
bigger left exists -> steps = 2
action      : push {6,2}
stack after : [7:0, 6:2]
answer      : 3
```

#### x = 11

```text
stack before: [7:0, 6:2]
11 >= 6 -> pop {6,2}, steps = 2
11 >= 7 -> pop {7,0}, steps = 2
stack empty -> steps = 0
action      : push {11,0}
stack after : [11:0]
answer      : 3
```

#### remaining values

```text
x = 8
stack before: [11:0]
8 >= 11? no -> steps = 1
push {8,1}

x = 5
stack before: [11:0, 8:1]
5 >= 8? no -> steps = 1
push {5,1}

x = 11
stack before: [11:0, 8:1, 5:1]
11 >= 5 -> pop {5,1}
11 >= 8 -> pop {8,1}
11 >= 11 -> pop {11,0}
stack empty -> steps = 0

final answer = 3
```

### Dry Run After Code

```text
nums = [5, 3, 4, 4, 7, 3, 6, 11, 8, 5, 11]
stack stores {value, stepsToDie}

x = 5
stack empty -> steps = 0
push {5,0}, ans = 0

x = 3
3 >= 5? no
left bigger exists, so steps = 1
push {3,1}, ans = 1

x = 4
4 >= 3 -> steps = max(0,1)=1, pop {3,1}
4 >= 5? no
left bigger exists -> steps = 1 + 1 = 2
push {4,2}, ans = 2

x = 4
4 >= 4 -> steps = max(0,2)=2, pop {4,2}
4 >= 5? no
left bigger exists -> steps = 2 + 1 = 3
push {4,3}, ans = 3

x = 7
7 >= 4 -> steps = 3, pop
7 >= 5 -> steps = 3, pop
stack empty -> steps = 0
push {7,0}, ans = 3

x = 3
3 >= 7? no -> steps = 1
push {3,1}, ans = 3

x = 6
6 >= 3 -> steps = 1, pop
6 >= 7? no -> steps = 2
push {6,2}, ans = 3

x = 11
pop {6,2}, pop {7,0}, stack empty -> steps = 0
push {11,0}, ans = 3

x = 8 -> blocked by 11 -> steps = 1
x = 5 -> blocked by 8 -> steps = 1
x = 11 -> pops 5 and 8, blocked by 11 -> steps = 2

final answer = 3
```


---

# Final Phase Summary

| Phase   | Goal                    | Problems                                                                         |
| ------- | ----------------------- | -------------------------------------------------------------------------------- |
| Phase 1 | Recognize pop condition | Next Greater, Daily Temperatures, Final Prices, Stock Span                       |
| Phase 2 | Boundary/range thinking | Histogram, Maximal Rectangle, Height of Soldiers                                 |
| Phase 3 | Contribution thinking   | Sum of Subarray Minimums, Max Subarray Min Product                               |
| Phase 4 | Greedy stack            | Remove K Digits, Remove Duplicate Letters                                        |
| Phase 5 | Hidden stack            | Trapping Rain Water, Maximum Width Ramp, Visible People, Steps to Non-decreasing |

---

# Master Rule

```text
If current element makes previous elements useless,
monotonic stack is probably the pattern.
```

# Final Checklist Before Coding

```text
1. Stack stores index or value?
2. Increasing or decreasing?
3. Pop condition?
4. What to calculate when popping?
5. What remains after popping?
6. Need tie handling: <, <=, >, >= ?
```
