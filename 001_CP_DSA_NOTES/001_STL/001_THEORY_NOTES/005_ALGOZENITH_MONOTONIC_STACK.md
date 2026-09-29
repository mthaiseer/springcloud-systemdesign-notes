# AlgoZenith — Stack & Monotonic Stack Mastery

> **Goal:** Learn the stack patterns that repeatedly appear in CP: matching, nearest greater/smaller, greedy deletion, boundary finding, histogram area, and rain water.

## Table of Contents

1. [Stack Core](#1-stack-core)
2. [Valid Parentheses](#2-valid-parentheses)
3. [Next Greater Element](#3-next-greater-element)
4. [Smallest Number After Removing K Digits](#4-smallest-number-after-removing-k-digits)
5. [Monotonic Stack Core](#5-monotonic-stack-core)
6. [Nearest Smaller Values AZ101](#6-nearest-smaller-values-az101)
7. [Largest Rectangle](#7-largest-rectangle)
8. [Rain Water](#8-rain-water)
9. [Height of Soldiers](#9-height-of-soldiers)
10. [Templates & Complexity](#10-templates--complexity)

---

# 1. Stack Core

A stack is:

```text
LIFO = Last In, First Out
```

Useful when the **latest unresolved item** must be processed first.

| Operation | C++ | Cost |
|---|---|---:|
| insert | `st.push(x)` | `O(1)` |
| remove top | `st.pop()` | `O(1)` |
| read top | `st.top()` | `O(1)` |
| empty? | `st.empty()` | `O(1)` |
| size | `st.size()` | `O(1)` |

Typical stack thinking:

```text
current item arrives
        ↓
does it resolve / invalidate stack top?
        ↓
YES → pop
NO  → push current
```

---

# 2. Valid Parentheses

Problem: [LeetCode 20 — Valid Parentheses](https://leetcode.com/problems/valid-parentheses/)

## Problem

Check whether every closing bracket matches the **latest unmatched opening bracket**.

```text
()[]{} → valid
([)]   → invalid
```

## Why Stack?

For:

```text
([ ])
```

when `]` arrives, it must match the most recent opening bracket:

```text
[
```

That is exactly LIFO.

## Dry Run — `([])`

| char | action | stack |
|---|---|---|
| `(` | push | `(` |
| `[` | push | `([` |
| `]` | match `[` → pop | `(` |
| `)` | match `(` → pop | empty |

```text
stack empty → valid
```

## C++

```cpp
class Solution {
public:
    bool isValid(string s) {
        stack<char> st;

        for (char c : s) {
            if (c == '(' || c == '[' || c == '{') {
                st.push(c);
            } else {
                if (st.empty())
                    return false;

                char x = st.top();
                st.pop();

                if ((c == ')' && x != '(') ||
                    (c == ']' && x != '[') ||
                    (c == '}' && x != '{'))
                    return false;
            }
        }

        return st.empty();
    }
};
```

```text
Time  = O(n)
Space = O(n)
```

---

# 3. Next Greater Element

Problem: [LeetCode 496 — Next Greater Element I](https://leetcode.com/problems/next-greater-element-i/)

## Problem

For every value, find the first greater value on its right.

Example:

```text
a = [2,1,4,3]

NGE:
2 → 4
1 → 4
4 → -1
3 → -1
```

## Monotonic Idea

Scan right → left.

Keep only useful greater candidates.

For current `a[i]`:

```text
while top <= a[i]:
    pop
```

After popping:

```text
top = nearest greater
```

Then push `a[i]`.

Stack stays:

```text
strictly decreasing from top toward bottom
```

## Dry Run — `[2,1,4,3]`

| i | x | pops | NGE | stack after |
|---:|---:|---|---:|---|
| 3 | 3 | — | -1 | `3` |
| 2 | 4 | `3` | -1 | `4` |
| 1 | 1 | — | 4 | `4,1` |
| 0 | 2 | `1` | 4 | `4,2` |

## C++

```cpp
vector<int> nextGreater(vector<int>& a) {
    int n = a.size();
    vector<int> ans(n, -1);
    stack<int> st;

    for (int i = n - 1; i >= 0; i--) {
        while (!st.empty() && st.top() <= a[i])
            st.pop();

        if (!st.empty())
            ans[i] = st.top();

        st.push(a[i]);
    }

    return ans;
}
```

### Why `O(n)`?

An element is:

```text
pushed once
popped at most once
```

Therefore total stack work is:

```text
O(n)
```

not `O(n²)`.

---

# 4. Smallest Number After Removing K Digits

Problem: [LeetCode 402 — Remove K Digits](https://leetcode.com/problems/remove-k-digits/)

## Problem

Remove exactly `k` digits so the remaining number is minimum.

Example:

```text
1432219, k=3
→ 1219
```

## Greedy + Monotonic Stack

A large digit before a smaller digit hurts the number most.

So when digit `d` arrives:

```text
while:
    k > 0
    AND stack.top > d

pop stack.top
```

This maintains increasing digits as much as the deletion budget allows.

## Algebra / Greedy Reason

Compare:

```text
... x y ...
```

where:

```text
x > y
```

If one of them must be deleted:

```text
delete x → ... y ...
delete y → ... x ...
```

At the first differing position:

```text
y < x
```

so deleting `x` gives the smaller number.

## Dry Run — `1432219`, `k=3`

```text
1 → [1]

4 → [1,4]

3:
4 > 3 → pop 4, k=2
push 3
[1,3]

2:
3 > 2 → pop 3, k=1
[1,2]

2 → [1,2,2]

1:
2 > 1 → pop 2, k=0
[1,2,1]

9 → [1,2,1,9]
```

Result:

```text
1219
```

## C++

```cpp
class Solution {
public:
    string removeKdigits(string num, int k) {
        string st;

        for (char d : num) {
            while (!st.empty() && k > 0 && st.back() > d) {
                st.pop_back();
                k--;
            }

            st.push_back(d);
        }

        while (k > 0 && !st.empty()) {
            st.pop_back();
            k--;
        }

        int i = 0;
        while (i < (int)st.size() && st[i] == '0')
            i++;

        string ans = st.substr(i);

        return ans.empty() ? "0" : ans;
    }
};
```

```text
Time  = O(n)
Space = O(n)
```

---

# 5. Monotonic Stack Core

A monotonic stack keeps elements in sorted order **inside the stack**.

Two major forms:

```text
Increasing stack
→ useful for smaller-element / minimum-boundary problems

Decreasing stack
→ useful for greater-element / maximum-boundary problems
```

## Four Core Queries

| Need | Typical pop condition |
|---|---|
| previous smaller | `while top >= current → pop` |
| next smaller | `while top >= current → pop` |
| previous greater | `while top <= current → pop` |
| next greater | `while top <= current → pop` |

Use **indices** when you need:

```text
distance
width
boundary
```

Use values only when you need the answer value itself.

Core boundary form:

```text
leftChoices  = i - L
rightChoices = R - i
```

where `L/R` are blocking indices.

---

# 6. Nearest Smaller Values AZ101

Problem: [Maang — Nearest Smaller Values AZ101](https://maang.in/problems/Nearest-Smaller-Values-AZ101-378)

> The exact Maang statement was not available from the provided material/search, so this section uses the standard **nearest smaller to the left** monotonic-stack formulation associated with this title.

## Problem

For every `a[i]`, find the nearest index/value on its left that is strictly smaller.

Example:

```text
a = [2,5,1,4,8,3]
```

## Idea

Before using the stack top:

```text
remove everything >= a[i]
```

Why?

```text
If top >= current,
it cannot be the smaller answer for current.

Current is also closer and no larger,
so that top is useless for future relevant smaller queries.
```

After popping:

```text
top = nearest smaller on left
```

## Dry Run

```text
a = [2,5,1,4]
```

| i | x | action | nearest smaller |
|---:|---:|---|---:|
| 0 | 2 | stack empty | -1 |
| 1 | 5 | `2 < 5` | 2 |
| 2 | 1 | pop 5, pop 2 | -1 |
| 3 | 4 | `1 < 4` | 1 |

## C++

```cpp
vector<int> nearestSmallerLeft(vector<int>& a) {
    int n = a.size();
    vector<int> ans(n, -1);
    stack<int> st; // indices

    for (int i = 0; i < n; i++) {
        while (!st.empty() && a[st.top()] >= a[i])
            st.pop();

        if (!st.empty())
            ans[i] = a[st.top()];

        st.push(i);
    }

    return ans;
}
```

```text
Time  = O(n)
Space = O(n)
```

---

# 7. Largest Rectangle

Problem: [Maang — Largest Rectangle](https://maang.in/problems/Largest-Rectangle-461)

Equivalent standard problem: [LeetCode 84 — Largest Rectangle in Histogram](https://leetcode.com/problems/largest-rectangle-in-histogram/)

## Problem

Each `a[i]` is a histogram bar height.

Find the maximum rectangle completely inside the histogram.

## Fix One Bar

Assume:

```text
height = a[i]
```

is the minimum height of a rectangle.

It can extend until a smaller bar blocks it.

Let:

```text
L = previous smaller index
R = next smaller index
```

Valid rectangle:

```text
L+1 ... R-1
```

Width:

```text
width
= (R-1) - (L+1) + 1
= R-L-1
```

Area contributed by choosing `a[i]` as height:

```text
area(i)
= a[i] × (R-L-1)
```

Answer:

```text
max area(i)
```

## Dry Run — `[2,1,5,6,2,3]`

Focus on height `5`, index `2`.

```text
previous smaller = index 1 → height 1
next smaller     = index 4 → height 2
```

Thus:

```text
L = 1
R = 4

width
= 4-1-1
= 2

area
= 5×2
= 10
```

Rectangle uses:

```text
[5,6]
```

and the maximum answer is:

```text
10
```

## C++

```cpp
class Solution {
public:
    int largestRectangleArea(vector<int>& h) {
        int n = h.size();

        vector<int> left(n), right(n);
        stack<int> st;

        // Previous smaller
        for (int i = 0; i < n; i++) {
            while (!st.empty() && h[st.top()] >= h[i])
                st.pop();

            left[i] = st.empty() ? -1 : st.top();
            st.push(i);
        }

        while (!st.empty())
            st.pop();

        // Next smaller
        for (int i = n - 1; i >= 0; i--) {
            while (!st.empty() && h[st.top()] >= h[i])
                st.pop();

            right[i] = st.empty() ? n : st.top();
            st.push(i);
        }

        long long ans = 0;

        for (int i = 0; i < n; i++) {
            long long width = right[i] - left[i] - 1;
            ans = max(ans, 1LL * h[i] * width);
        }

        return (int)ans;
    }
};
```

```text
Time  = O(n)
Space = O(n)
```

---

# 8. Rain Water

Problem: [Maang — Rain Water](https://maang.in/problems/Rain-Water-462)

Equivalent standard problem: [LeetCode 42 — Trapping Rain Water](https://leetcode.com/problems/trapping-rain-water/)

## Problem

Bars represent heights.

Find how much water remains trapped after rain.

## Water Formula

For a valley:

```text
left wall     = h[L]
bottom        = h[mid]
right wall    = h[R]
```

Water level is limited by the shorter wall:

```text
waterHeight
= min(h[L], h[R]) - h[mid]
```

Horizontal width between the walls:

```text
width = R-L-1
```

So:

```text
addedWater
= waterHeight × width
```

The monotonic stack discovers the valley when a taller right boundary arrives.

## Dry Run — `[3,0,2]`

Stack initially empty.

```text
3 → push index 0
0 → push index 1
```

Current `2` is greater than stack-top height `0`:

```text
mid = 1
pop it

left = 0
right = 2
```

Now:

```text
boundedHeight
= min(3,2) - 0
= 2

width
= 2-0-1
= 1

water
= 2×1
= 2
```

Answer:

```text
2
```

## C++

```cpp
class Solution {
public:
    int trap(vector<int>& h) {
        int n = h.size();
        stack<int> st;

        long long water = 0;

        for (int i = 0; i < n; i++) {

            while (!st.empty() && h[i] > h[st.top()]) {

                int mid = st.top();
                st.pop();

                if (st.empty())
                    break;

                int left = st.top();

                long long width = i - left - 1;

                long long boundedHeight =
                    min(h[left], h[i]) - h[mid];

                water += width * boundedHeight;
            }

            st.push(i);
        }

        return (int)water;
    }
};
```

```text
Time  = O(n)
Space = O(n)
```

---

# 9. Height of Soldiers

Problem: [Maang — Height of Soldiers](https://maang.in/problems/Height-of-Soldiers-88)

> The exact Maang statement was not available from the provided material/search. Do **not** memorize an invented solution from the title alone. Use the following monotonic-stack decision framework once you read the statement.

## Convert the Statement

If it asks for:

```text
nearest taller soldier
```

use a decreasing stack:

```cpp
while (!st.empty() && h[st.top()] <= h[i])
    st.pop();
```

If it asks for:

```text
nearest shorter soldier
```

use an increasing stack:

```cpp
while (!st.empty() && h[st.top()] >= h[i])
    st.pop();
```

If it asks for distance:

```text
distance = i - st.top();
```

If it asks for the index:

```text
answer = st.top();
```

If it asks for height:

```text
answer = h[st.top()];
```

## Example — Nearest Taller on Left

```text
h = [4,2,5,3]
```

Dry run:

```text
4 → none

2 → nearest taller = 4

5 → pop 2, pop 4
     none

3 → nearest taller = 5
```

### C++ Pattern

```cpp
vector<int> nearestTallerLeft(vector<int>& h) {
    int n = h.size();

    vector<int> ans(n, -1);
    stack<int> st;

    for (int i = 0; i < n; i++) {

        while (!st.empty() && h[st.top()] <= h[i])
            st.pop();

        if (!st.empty())
            ans[i] = st.top();

        st.push(i);
    }

    return ans;
}
```

```text
Time  = O(n)
Space = O(n)
```

---

# 10. Templates & Complexity

## Previous Smaller

```cpp
for (int i = 0; i < n; i++) {
    while (!st.empty() && a[st.top()] >= a[i])
        st.pop();

    ans[i] = st.empty() ? -1 : st.top();

    st.push(i);
}
```

## Next Smaller

```cpp
for (int i = n - 1; i >= 0; i--) {
    while (!st.empty() && a[st.top()] >= a[i])
        st.pop();

    ans[i] = st.empty() ? n : st.top();

    st.push(i);
}
```

## Previous Greater

```cpp
for (int i = 0; i < n; i++) {
    while (!st.empty() && a[st.top()] <= a[i])
        st.pop();

    ans[i] = st.empty() ? -1 : st.top();

    st.push(i);
}
```

## Next Greater

```cpp
for (int i = n - 1; i >= 0; i--) {
    while (!st.empty() && a[st.top()] <= a[i])
        st.pop();

    ans[i] = st.empty() ? n : st.top();

    st.push(i);
}
```

## The One Formula to Remember

For a fixed index `i` bounded by:

```text
L = blocker on left
R = blocker on right
```

choices are often:

```text
leftChoices  = i-L
rightChoices = R-i
```

For full interval width:

```text
width = R-L-1
```

Examples:

```text
Subarray minimum contribution:
a[i] × (i-L) × (R-i)

Histogram rectangle:
a[i] × (R-L-1)
```

## Why Monotonic Stack Is `O(n)`

Even with a nested `while`:

```text
each index is pushed once
each index is popped at most once
```

Therefore:

```text
total pushes ≤ n
total pops   ≤ n

Time = O(n)
```

## Final Pattern Map

| Problem | Stack Pattern | Core Formula / Action |
|---|---|---|
| Valid Parentheses | normal stack | latest opening matches closing |
| Next Greater | decreasing | pop `<= current` |
| Remove K Digits | increasing greedy | pop larger previous digit |
| Nearest Smaller | increasing | pop `>= current` |
| Largest Rectangle | increasing boundaries | `h[i](R-L-1)` |
| Rain Water | decreasing valley | `(min(L,R)-bottom)×width` |
| Soldier taller query | decreasing | pop `<= current` |
| Soldier shorter query | increasing | pop `>= current` |

> **Main question:** What elements become permanently useless when the current element arrives?  
> Those are exactly the elements you pop.
