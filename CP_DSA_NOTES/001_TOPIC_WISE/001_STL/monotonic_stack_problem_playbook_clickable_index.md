# Monotonic Stack Problem Playbook

## Clickable Index

- [Universal Thinking Template](#universal-thinking-template)
- [Stack Types](#stack-types)
- [How to Read Array Dry Runs](#how-to-read-array-dry-runs)
- [1. Next Greater Element](#1-next-greater-element)
- [2. Previous Smaller Element](#2-previous-smaller-element)
- [3. Daily Temperatures](#3-daily-temperatures)
- [4. Stock Span](#4-stock-span)
- [5. Largest Rectangle in Histogram](#5-largest-rectangle-in-histogram)
- [6. Maximal Rectangle](#6-maximal-rectangle)
- [7. Sum of Subarray Minimums](#7-sum-of-subarray-minimums)
- [8. Trapping Rain Water](#8-trapping-rain-water)
- [9. Remove K Digits](#9-remove-k-digits)
- [Final Recognition Table](#final-recognition-table)
- [Master Sentence](#master-sentence)
- [What to Ask in Any Problem](#what-to-ask-in-any-problem)

---

Use this guide to tackle common monotonic stack problems quickly.

## Universal Thinking Template

```text
1. What is each element waiting for?
2. Which future/current element resolves it?
3. Do I need greater or smaller?
4. Do I need previous, next, or both?
5. What should the stack store?
   Usually index, not value.
6. When current element breaks monotonic order, pop and calculate answer.
```

## Stack Types

```text
Next Greater     -> decreasing stack
Next Smaller     -> increasing stack
Previous Smaller -> increasing stack
Previous Greater -> decreasing stack
```

## How to Read Array Dry Runs

```text
Array row     = original input
Pointer row   = current index i
Stack row     = stack content from bottom to top
Action row    = push / pop / calculate
Answer row    = current answer state
```

---

# 1. Next Greater Element

## Problem

For each element, find the first greater element on its right.

Example:

```text
nums   = [2, 1, 5, 3]
answer = [5, 5, -1, -1]
```

## Idea

Each element waits for a greater element in the future.

When current value is greater than stack top, it resolves stack top.

```text
current greater means:
previous smaller values found their answer
```

## Dry Run — Array Diagram

```text
nums = [2, 1, 5, 3]
stack stores index:value
```

### i = 0, current = 2

```text
index:   0   1   2   3
nums:   [2,  1,  5,  3]
         ^

stack before: []
action: push 0
stack after : [0:2]
answer      : [-1, -1, -1, -1]
```

### i = 1, current = 1

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

### i = 2, current = 5

```text
index:   0   1   2   3
nums:   [2,  1,  5,  3]
                 ^

stack before: [0:2, 1:1]

5 > 1 -> pop 1 -> answer[1] = 5
5 > 2 -> pop 0 -> answer[0] = 5

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

### i = 3, current = 3

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

## Code

```cpp
vector<int> nextGreater(vector<int>& nums) {
    int n = nums.size();
    vector<int> ans(n, -1);
    stack<int> st; // indices

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

---

# 2. Previous Smaller Element

## Problem

For each element, find the nearest smaller element on its left.

Example:

```text
nums   = [4, 2, 5, 1, 3]
answer = [-1, -1, 2, -1, 1]
```

## Idea

Before inserting current element, remove all elements that are greater or equal because they can never be previous smaller.

After popping, stack top is previous smaller.

## Dry Run — Array Diagram

```text
nums = [4, 2, 5, 1, 3]
stack stores values
```

### i = 0, current = 4

```text
index:   0   1   2   3   4
nums:   [4,  2,  5,  1,  3]
         ^

stack before: []
previous smaller = -1
push 4
stack after : [4]
answer      : [-1, _, _, _, _]
```

### i = 1, current = 2

```text
index:   0   1   2   3   4
nums:   [4,  2,  5,  1,  3]
             ^

stack before: [4]
4 >= 2 -> pop 4
stack empty -> previous smaller = -1
push 2
stack after : [2]
answer      : [-1, -1, _, _, _]
```

### i = 2, current = 5

```text
index:   0   1   2   3   4
nums:   [4,  2,  5,  1,  3]
                 ^

stack before: [2]
2 < 5 -> previous smaller = 2
push 5
stack after : [2, 5]
answer      : [-1, -1, 2, _, _]
```

### i = 3, current = 1

```text
index:   0   1   2   3   4
nums:   [4,  2,  5,  1,  3]
                     ^

stack before: [2, 5]
5 >= 1 -> pop 5
2 >= 1 -> pop 2
stack empty -> previous smaller = -1
push 1
stack after : [1]
answer      : [-1, -1, 2, -1, _]
```

### i = 4, current = 3

```text
index:   0   1   2   3   4
nums:   [4,  2,  5,  1,  3]
                         ^

stack before: [1]
1 < 3 -> previous smaller = 1
push 3
stack after : [1, 3]
answer      : [-1, -1, 2, -1, 1]
```

## Code

```cpp
vector<int> previousSmaller(vector<int>& nums) {
    int n = nums.size();
    vector<int> ans(n, -1);
    stack<int> st; // values

    for (int i = 0; i < n; i++) {
        while (!st.empty() && st.top() >= nums[i]) {
            st.pop();
        }

        if (!st.empty()) ans[i] = st.top();
        st.push(nums[i]);
    }

    return ans;
}
```

---

# 3. Daily Temperatures

## Problem

For each day, find how many days until a warmer temperature.

Example:

```text
t      = [73, 74, 75, 71, 69, 72, 76, 73]
answer = [1, 1, 4, 2, 1, 1, 0, 0]
```

## Idea

Same as Next Greater Element, but answer is distance instead of value.

## Dry Run — Array Diagram

```text
t = [73, 74, 75, 71, 69, 72, 76, 73]
stack stores unresolved day index:temperature
```

### i = 0, current = 73

```text
index: [0,  1,  2,  3,  4,  5,  6,  7]
temp : [73, 74, 75, 71, 69, 72, 76, 73]
        ^

stack before: []
action      : push 0
stack after : [0:73]
answer      : [0,0,0,0,0,0,0,0]
```

### i = 1, current = 74

```text
index: [0,  1,  2,  3,  4,  5,  6,  7]
temp : [73, 74, 75, 71, 69, 72, 76, 73]
            ^

stack before: [0:73]
74 > 73 -> pop 0 -> answer[0] = 1 - 0 = 1
push 1
stack after : [1:74]
answer      : [1,0,0,0,0,0,0,0]
```

### i = 2, current = 75

```text
index: [0,  1,  2,  3,  4,  5,  6,  7]
temp : [73, 74, 75, 71, 69, 72, 76, 73]
                ^

75 > 74 -> pop 1 -> answer[1] = 1
push 2
stack after : [2:75]
answer      : [1,1,0,0,0,0,0,0]
```

### i = 5, current = 72

State before i = 5:

```text
index: [0,  1,  2,  3,  4,  5,  6,  7]
temp : [73, 74, 75, 71, 69, 72, 76, 73]
                            ^

stack before: [2:75, 3:71, 4:69]

72 > 69 -> pop 4 -> answer[4] = 5 - 4 = 1
72 > 71 -> pop 3 -> answer[3] = 5 - 3 = 2
72 > 75? no
push 5

stack after : [2:75, 5:72]
answer      : [1,1,0,2,1,0,0,0]
```

Visual:

```text
[73, 74, 75, 71, 69, 72, 76, 73]
              ^   ^   |
              |   |   +-- 72 resolves 71 and 69
              |   +------ answer[4] = 1
              +---------- answer[3] = 2
```

### i = 6, current = 76

```text
index: [0,  1,  2,  3,  4,  5,  6,  7]
temp : [73, 74, 75, 71, 69, 72, 76, 73]
                                ^

stack before: [2:75, 5:72]
76 > 72 -> pop 5 -> answer[5] = 1
76 > 75 -> pop 2 -> answer[2] = 4
push 6

stack after : [6:76]
answer      : [1,1,4,2,1,1,0,0]
```

## Code

```cpp
vector<int> dailyTemperatures(vector<int>& t) {
    int n = t.size();
    vector<int> ans(n, 0);
    stack<int> st; // indices waiting for warmer day

    for (int i = 0; i < n; i++) {
        while (!st.empty() && t[i] > t[st.top()]) {
            int j = st.top();
            st.pop();
            ans[j] = i - j;
        }
        st.push(i);
    }

    return ans;
}
```

---

# 4. Stock Span

## Problem

For each day, find number of consecutive previous days including today where price was <= current price.

Example:

```text
prices = [100, 80, 60, 70, 60, 75, 85]
answer = [1, 1, 1, 2, 1, 4, 6]
```

## Idea

Current price absorbs previous smaller or equal prices.

Stack keeps previous greater prices as blockers.

## Dry Run — Array Diagram

```text
prices = [100, 80, 60, 70, 60, 75, 85]
stack stores index:price of previous greater blockers
```

### i = 0, price = 100

```text
index : [0,   1,  2,  3,  4,  5,  6]
price : [100, 80, 60, 70, 60, 75, 85]
         ^

stack before: []
previous greater index = -1
span = 0 - (-1) = 1
push 0
stack after : [0:100]
answer      : [1,_,_,_,_,_,_]
```

### i = 3, price = 70

State before i = 3:

```text
index : [0,   1,  2,  3,  4,  5,  6]
price : [100, 80, 60, 70, 60, 75, 85]
                     ^

stack before: [0:100, 1:80, 2:60]
70 >= 60 -> pop 2
previous greater index = 1
span = 3 - 1 = 2
push 3
stack after : [0:100, 1:80, 3:70]
answer      : [1,1,1,2,_,_,_]
```

Visual:

```text
[100, 80, 60, 70, 60, 75, 85]
          <--- span --->
              60  70
blocked by 80
```

### i = 5, price = 75

```text
index : [0,   1,  2,  3,  4,  5,  6]
price : [100, 80, 60, 70, 60, 75, 85]
                             ^

stack before: [0:100, 1:80, 3:70, 4:60]
75 >= 60 -> pop 4
75 >= 70 -> pop 3
75 >= 80? no
previous greater index = 1
span = 5 - 1 = 4
push 5
stack after : [0:100, 1:80, 5:75]
answer      : [1,1,1,2,1,4,_]
```

Visual:

```text
[100, 80, 60, 70, 60, 75, 85]
          |-----------|
             span = 4
blocked by 80
```

### i = 6, price = 85

```text
index : [0,   1,  2,  3,  4,  5,  6]
price : [100, 80, 60, 70, 60, 75, 85]
                                 ^

stack before: [0:100, 1:80, 5:75]
85 >= 75 -> pop 5
85 >= 80 -> pop 1
85 >= 100? no
previous greater index = 0
span = 6 - 0 = 6
answer = [1,1,1,2,1,4,6]
```

## Code

```cpp
vector<int> stockSpan(vector<int>& prices) {
    int n = prices.size();
    vector<int> ans(n);
    stack<int> st; // indices of previous greater prices

    for (int i = 0; i < n; i++) {
        while (!st.empty() && prices[st.top()] <= prices[i]) {
            st.pop();
        }

        int prevGreater = st.empty() ? -1 : st.top();
        ans[i] = i - prevGreater;

        st.push(i);
    }

    return ans;
}
```

---

# 5. Largest Rectangle in Histogram

## Problem

Find largest rectangle area inside histogram.

Example:

```text
heights = [2, 1, 5, 6, 2, 3]
answer = 10
```

## Idea

Each bar wants to be the minimum height of a rectangle.

For each bar:

```text
width = nextSmallerIndex - previousSmallerIndex - 1
area = height * width
```

When a smaller bar appears, taller bars are finalized.

## Dry Run — Array Diagram

```text
heights = [2, 1, 5, 6, 2, 3]
add sentinel 0 at end
extended = [2, 1, 5, 6, 2, 3, 0]
stack stores index:height
```

### i = 0, current = 2

```text
index : [0, 1, 2, 3, 4, 5, 6]
height: [2, 1, 5, 6, 2, 3, 0]
         ^

stack before: []
action: push 0
stack after : [0:2]
ans = 0
```

### i = 1, current = 1

```text
index : [0, 1, 2, 3, 4, 5, 6]
height: [2, 1, 5, 6, 2, 3, 0]
            ^

stack before: [0:2]
1 < 2 -> pop 0

popped height = 2
right boundary = i = 1
left boundary = -1
width = 1 - (-1) - 1 = 1
area = 2 * 1 = 2
ans = 2

push 1
stack after: [1:1]
```

Visual:

```text
[2, 1, 5, 6, 2, 3]
 ^  |
 |  +-- right smaller boundary
 +----- finalized bar height 2
```

### i = 4, current = 2

State before i = 4:

```text
index : [0, 1, 2, 3, 4, 5, 6]
height: [2, 1, 5, 6, 2, 3, 0]
                     ^

stack before: [1:1, 2:5, 3:6]
```

Pop 6:

```text
2 < 6 -> pop index 3
height = 6
right = 4
left = 2
width = 4 - 2 - 1 = 1
area = 6 * 1 = 6
ans = 6

array view:
[2, 1, 5, 6, 2, 3]
          ^  |
          |  +-- right smaller
          +----- rectangle width 1
```

Pop 5:

```text
2 < 5 -> pop index 2
height = 5
right = 4
left = 1
width = 4 - 1 - 1 = 2
area = 5 * 2 = 10
ans = 10

array view:
[2, 1, 5, 6, 2, 3]
       |-----|  |
       5  6     right smaller

rectangle height = 5
width = 2
area = 10
```

Then push current:

```text
stack after: [1:1, 4:2]
ans = 10
```

### i = 6, current = 0 sentinel

State before sentinel:

```text
index : [0, 1, 2, 3, 4, 5, 6]
height: [2, 1, 5, 6, 2, 3, 0]
                              ^

stack before: [1:1, 4:2, 5:3]
```

Pop remaining bars:

```text
0 < 3 -> pop 5
height = 3, left = 4, right = 6
width = 1, area = 3
ans = 10

0 < 2 -> pop 4
height = 2, left = 1, right = 6
width = 4, area = 8
ans = 10

0 < 1 -> pop 1
height = 1, left = -1, right = 6
width = 6, area = 6
ans = 10
```

Final:

```text
largest area = 10
```

## Code

```cpp
int largestRectangleArea(vector<int>& h) {
    int n = h.size();
    stack<int> st;
    long long ans = 0;

    for (int i = 0; i <= n; i++) {
        int curr = (i == n ? 0 : h[i]);

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

    return ans;
}
```

---

# 6. Maximal Rectangle

## Problem

Given binary matrix, find largest rectangle containing only 1s.

Example:

```text
matrix:
1 0 1 0 0
1 0 1 1 1
1 1 1 1 1
1 0 0 1 0
```

Answer is 6.

## Idea

Convert each row into histogram heights.

If matrix cell is 1:

```text
height[col] += 1
```

If matrix cell is 0:

```text
height[col] = 0
```

Then apply Largest Rectangle in Histogram for every row.

## Dry Run — Matrix to Array Diagram

### Row 0

```text
matrix row: [1, 0, 1, 0, 0]
height   : [1, 0, 1, 0, 0]
max histogram area = 1
ans = 1
```

Visual:

```text
col    :  0  1  2  3  4
row 0  :  1  0  1  0  0
height : [1, 0, 1, 0, 0]
```

### Row 1

```text
previous height: [1, 0, 1, 0, 0]
row 1          : [1, 0, 1, 1, 1]
new height     : [2, 0, 2, 1, 1]
max histogram area = 3
ans = 3
```

Visual:

```text
matrix until row 1:
1 0 1 0 0
1 0 1 1 1

height array:
[2, 0, 2, 1, 1]
          |-----|
          width 3 with height 1 -> area 3
```

### Row 2

```text
previous height: [2, 0, 2, 1, 1]
row 2          : [1, 1, 1, 1, 1]
new height     : [3, 1, 3, 2, 2]
max histogram area = 6
ans = 6
```

Visual:

```text
matrix until row 2:
1 0 1 0 0
1 0 1 1 1
1 1 1 1 1

height array:
[3, 1, 3, 2, 2]
       |--------|
       columns 2,3,4
       min height = 2
       width = 3
       area = 6
```

### Row 3

```text
previous height: [3, 1, 3, 2, 2]
row 3          : [1, 0, 0, 1, 0]
new height     : [4, 0, 0, 3, 0]
max histogram area = 4
ans = 6
```

## Code

```cpp
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

    int m = matrix.size();
    int n = matrix[0].size();
    vector<int> height(n, 0);
    int ans = 0;

    for (int r = 0; r < m; r++) {
        for (int c = 0; c < n; c++) {
            if (matrix[r][c] == '1') height[c]++;
            else height[c] = 0;
        }

        ans = max(ans, largestRectangleArea(height));
    }

    return ans;
}
```

---

# 7. Sum of Subarray Minimums

## Problem

Find sum of minimum value of every subarray.

Example:

```text
arr = [3, 1, 2, 4]
subarray minimum sum = 17
```

## Idea

Instead of generating all subarrays, calculate each element's contribution.

For each `arr[i]`, count how many subarrays have `arr[i]` as minimum.

```text
contribution = arr[i] * choicesOnLeft * choicesOnRight
```

Need:

```text
previous strictly smaller
next smaller or equal
```

This tie handling avoids duplicate counting.

## Dry Run — Array Contribution Diagram

```text
arr = [3, 1, 2, 4]
```

### Element 3 at index 0

```text
index: [0, 1, 2, 3]
arr  : [3, 1, 2, 4]
        ^

previous smaller index = -1
next smaller/equal index = 1

left choices  = 0 - (-1) = 1
right choices = 1 - 0 = 1
contribution = 3 * 1 * 1 = 3
```

Visual:

```text
[3, 1, 2, 4]
 ^  |
 |  +-- smaller 1 stops 3
 +----- only subarray [3]
```

### Element 1 at index 1

```text
index: [0, 1, 2, 3]
arr  : [3, 1, 2, 4]
           ^

previous smaller index = -1
next smaller/equal index = 4

left choices  = 1 - (-1) = 2
right choices = 4 - 1 = 3
contribution = 1 * 2 * 3 = 6
```

Visual:

```text
[3, 1, 2, 4]
 |-----------|
    1 is minimum in this whole zone

left choices : start at index 0 or 1  -> 2 choices
right choices: end at index 1, 2, or 3 -> 3 choices

subarrays where 1 is minimum:
[1]
[3,1]
[1,2]
[1,2,4]
[3,1,2]
[3,1,2,4]
```

### Element 2 at index 2

```text
index: [0, 1, 2, 3]
arr  : [3, 1, 2, 4]
              ^

previous smaller index = 1
next smaller/equal index = 4

left choices  = 2 - 1 = 1
right choices = 4 - 2 = 2
contribution = 2 * 1 * 2 = 4
```

Visual:

```text
[3, 1, 2, 4]
     |  |-- can extend right to 4
     |----- blocked left by 1
```

### Element 4 at index 3

```text
index: [0, 1, 2, 3]
arr  : [3, 1, 2, 4]
                 ^

previous smaller index = 2
next smaller/equal index = 4

left choices  = 3 - 2 = 1
right choices = 4 - 3 = 1
contribution = 4 * 1 * 1 = 4
```

Final:

```text
sum = 3 + 6 + 4 + 4 = 17
```

## Code

```cpp
int sumSubarrayMins(vector<int>& arr) {
    const int MOD = 1e9 + 7;
    int n = arr.size();
    vector<int> left(n), right(n);
    stack<int> st;

    // previous strictly smaller
    for (int i = 0; i < n; i++) {
        while (!st.empty() && arr[st.top()] >= arr[i]) {
            st.pop();
        }
        left[i] = st.empty() ? i + 1 : i - st.top();
        st.push(i);
    }

    while (!st.empty()) st.pop();

    // next smaller or equal
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

---

# 8. Trapping Rain Water

## Problem

Given bar heights, compute trapped water.

Example:

```text
height = [0,1,0,2,1,0,1,3,2,1,2,1]
answer = 6
```

## Idea

Water is trapped when current bar forms a right boundary for previous lower bars.

Stack stores indices of decreasing heights.

When current height is greater than stack top, we found a container.

## Dry Run — Array Diagram

Use smaller example first:

```text
height = [2, 0, 2]
```

### i = 0, current = 2

```text
index : [0, 1, 2]
height: [2, 0, 2]
         ^

stack before: []
action: push 0
stack after: [0:2]
water = 0
```

### i = 1, current = 0

```text
index : [0, 1, 2]
height: [2, 0, 2]
            ^

stack before: [0:2]
0 > 2? no
push 1
stack after: [0:2, 1:0]
water = 0
```

### i = 2, current = 2

```text
index : [0, 1, 2]
height: [2, 0, 2]
               ^

stack before: [0:2, 1:0]
2 > 0 -> pop bottom index 1
left boundary = index 0, height 2
right boundary = index 2, height 2

width = 2 - 0 - 1 = 1
bounded height = min(2, 2) - 0 = 2
water added = 1 * 2 = 2

stack after push 2: [0:2, 2:2]
total water = 2
```

Visual:

```text
index :  0   1   2
height: [2,  0,  2]
         |~~~~~~~|
             water

left wall  = 2
right wall = 2
bottom     = 0
water height = 2
```

Bigger visual idea:

```text
height = [0,1,0,2,1,0,1,3]

current 3 at index 7 becomes a strong right wall.
It resolves multiple lower bars before it.
```

## Code

```cpp
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
            int right = i;

            int width = right - left - 1;
            int boundedHeight = min(height[left], height[right]) - height[bottom];

            water += width * boundedHeight;
        }

        st.push(i);
    }

    return water;
}
```

---

# 9. Remove K Digits

## Problem

Remove `k` digits from number string to make the smallest possible number.

Example:

```text
num = "1432219", k = 3
answer = "1219"
```

## Idea

If previous digit is greater than current digit, removing previous digit makes number smaller.

Use increasing stack.

## Dry Run — Array/String Diagram

```text
num = "1432219"
k = 3
```

### Read 1

```text
num chars: [1, 4, 3, 2, 2, 1, 9]
            ^

stack before: ""
action: push 1
stack after : "1"
k = 3
```

### Read 4

```text
num chars: [1, 4, 3, 2, 2, 1, 9]
               ^

stack before: "1"
1 > 4? no
action: push 4
stack after : "14"
k = 3
```

### Read 3

```text
num chars: [1, 4, 3, 2, 2, 1, 9]
                  ^

stack before: "14"
4 > 3 and k > 0 -> pop 4
push 3
stack after : "13"
k = 2
```

Visual:

```text
1 4 3 2 2 1 9
  ^ |
  | +-- smaller 3 arrives
  +---- remove 4
```

### Read 2

```text
num chars: [1, 4, 3, 2, 2, 1, 9]
                     ^

stack before: "13"
3 > 2 and k > 0 -> pop 3
push 2
stack after : "12"
k = 1
```

### Read second 2

```text
num chars: [1, 4, 3, 2, 2, 1, 9]
                        ^

stack before: "12"
2 > 2? no
push 2
stack after : "122"
k = 1
```

### Read 1

```text
num chars: [1, 4, 3, 2, 2, 1, 9]
                           ^

stack before: "122"
2 > 1 and k > 0 -> pop last 2
push 1
stack after : "121"
k = 0
```

Visual:

```text
current stack before 1:
"122"
   ^
   remove this 2 because smaller 1 arrived

stack becomes:
"121"
```

### Read 9

```text
num chars: [1, 4, 3, 2, 2, 1, 9]
                              ^

k = 0, cannot remove more
push 9
stack after: "1219"
```

Final:

```text
answer = "1219"
```

## Code

```cpp
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

---

# Final Recognition Table

| Problem               | Stack Type        | Pop When                 | Meaning                            |
| --------------------- | ----------------- | ------------------------ | ---------------------------------- |
| Next Greater Element  | Decreasing        | current > top            | current resolves smaller values    |
| Previous Smaller      | Increasing        | top >= current           | remove useless bigger values       |
| Daily Temperatures    | Decreasing        | current temp > top temp  | warmer day found                   |
| Stock Span            | Decreasing        | top price <= current     | current absorbs smaller/equal days |
| Histogram             | Increasing        | current < top            | right boundary found               |
| Maximal Rectangle     | Histogram per row | current < top            | rectangle boundary found           |
| Sum Subarray Minimums | Increasing        | depends on tie           | count contribution                 |
| Trapping Rain Water   | Decreasing        | current > top            | right wall found                   |
| Remove K Digits       | Increasing        | previous digit > current | remove bad larger digit            |

---

# Master Sentence

```text
A monotonic stack keeps unresolved elements.
The current element resolves previous elements when it breaks the stack order.
```

# What to Ask in Any Problem

```text
1. What is unresolved?
2. What kind of future element resolves it?
3. Do I pop smaller or greater values?
4. When I pop, what do I calculate?
5. What remains on stack after popping?
```
