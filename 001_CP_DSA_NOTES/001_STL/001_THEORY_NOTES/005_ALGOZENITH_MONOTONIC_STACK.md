# AlgoZenith — Stack & Monotonic Stack Mastery
## Visual Self-Learning Version

> **Goal:** Learn the stack patterns that repeatedly appear in CP:
> matching, nearest greater/smaller, greedy deletion, boundary finding,
> histogram area, rain water, and nearest taller/shorter queries.

# Table of Contents

- [0. Prerequisites](#0-prerequisites)
- [1. Stack Core](#1-stack-core)
- [2. Valid Parentheses](#2-valid-parentheses)
- [3. Next Greater Element](#3-next-greater-element)
- [4. Smallest Number After Removing K Digits](#4-smallest-number-after-removing-k-digits)
- [5. Monotonic Stack Core](#5-monotonic-stack-core)
- [6. Nearest Smaller Values AZ101](#6-nearest-smaller-values-az101)
- [7. Largest Rectangle](#7-largest-rectangle)
- [8. Rain Water](#8-rain-water)
- [9. Height of Soldiers](#9-height-of-soldiers)
- [10. Four Core Templates](#10-four-core-templates)
- [11. Recognition Checklist](#11-recognition-checklist)
- [12. Complexity](#12-complexity)
- [13. Final Pattern Map](#13-final-pattern-map)

---

# 0. Prerequisites

Before monotonic stack, make sure these ideas are clear.

## 0.1 What Is a Stack?

A stack follows:

```text
LIFO

Last In
First Out
```

Think of plates:

```text
        top
         |
         v

       [ 8 ]   <- newest plate
       [ 5 ]
       [ 2 ]
       -----
```

If we remove one:

```text
8 is removed first
```

C++:

```cpp
stack<int> st;

st.push(2);
st.push(5);
st.push(8);

cout << st.top(); // 8

st.pop();

cout << st.top(); // 5
```

---

## 0.2 Stack Operations

| Operation | C++ | Complexity |
|---|---|---:|
| push | `st.push(x)` | `O(1)` |
| pop | `st.pop()` | `O(1)` |
| top | `st.top()` | `O(1)` |
| empty | `st.empty()` | `O(1)` |
| size | `st.size()` | `O(1)` |

---

## 0.3 Index vs Value

This distinction is very important.

Use **values** when you only need:

```text
nearest greater value
nearest smaller value
```

Use **indices** when you need:

```text
distance
width
left boundary
right boundary
number of choices
```

Example:

```text
index:   0   1   2   3
value:   2   5   1   4
```

If nearest smaller to index `3` is index `2`:

```text
value answer    = 1
index answer    = 2
distance answer = 3 - 2 = 1
```

Same stack idea, different output.

---

## 0.4 What Does "Nearest" Mean?

Example:

```text
a = [2, 7, 4, 6]
```

For `6`:

```text
smaller values on left:

2
4

nearest smaller:
4
```

We care about the closest valid element, not the smallest value globally.

---

## 0.5 Brute Force Boundary Search

For each index `i`, we could scan:

```text
left until condition is satisfied
right until condition is satisfied
```

Example:

```text
for each i:
    walk left
    walk right
```

Worst case:

```text
O(n^2)
```

Monotonic stack compresses all these repeated scans into:

```text
O(n)
```

---

## 0.6 Monotonic Means Ordered Inside the Stack

Two important forms:

```text
INCREASING STACK
----------------
small -> large

useful for:
smaller-element boundaries
minimum problems
histogram


DECREASING STACK
----------------
large -> small

useful for:
greater-element boundaries
maximum problems
next greater
```

The exact view from top-to-bottom or bottom-to-top depends on implementation.

More important than memorizing wording is remembering the pop condition.

---

## 0.7 Strict vs Non-Strict Comparison

Duplicates matter.

These are different:

```cpp
a[st.top()] >= a[i]
```

and:

```cpp
a[st.top()] > a[i]
```

When counting subarray ownership, using strict on one side and non-strict on the other avoids double counting equal values.

For simple nearest-smaller queries, follow the exact statement:

```text
strictly smaller?
smaller-or-equal?
```

---

# 1. Stack Core

A normal stack is useful when:

```text
the latest unresolved item
must be processed first
```

Mental flow:

```text
current item arrives
        |
        v
does it resolve / invalidate top?
      /       \
    yes        no
     |          |
    pop        push
```

This same pattern appears in:

```text
parentheses
next greater/smaller
greedy deletion
histogram boundaries
rain water
```

---

## 1.1 Why Stack Works

Suppose unresolved items are:

```text
A
B
C
```

and `C` is newest.

If the next event must interact with the newest unresolved item first:

```text
A
B
C <- check this first
```

that is exactly stack behavior.

---

# 2. Valid Parentheses

Problem: [LeetCode 20 — Valid Parentheses](https://leetcode.com/problems/valid-parentheses/)

## 2.1 What Is the Problem Asking?

We need to verify:

```text
every closing bracket
matches the latest unmatched opening bracket
```

Examples:

```text
()[]{}  -> valid

([)]    -> invalid
```

---

## 2.2 Why Stack?

Take:

```text
([ ])
```

When `]` arrives:

```text
latest unmatched opening bracket = [
```

It must match that one first.

ASCII:

```text
read '('
stack:
(

read '['
stack:
(
[   <- top

read ']'
must match top '['
pop

stack:
(

read ')'
must match top '('
pop

empty -> valid
```

This is pure LIFO.

---

## 2.3 Step-by-Step Algorithm

```text
Step 1:
Create empty stack.

Step 2:
Scan string left to right.

Step 3:
If opening bracket:
push it.

Step 4:
If closing bracket:
stack must not be empty.

Step 5:
Top opening bracket must match closing bracket.

Step 6:
If it matches:
pop.

Step 7:
After scanning:
stack must be empty.
```

---

## 2.4 Dry Run — `([])`

| char | action | stack |
|---|---|---|
| `(` | push | `(` |
| `[` | push | `([` |
| `]` | pop `[` | `(` |
| `)` | pop `(` | empty |

Result:

```text
valid
```

---

## 2.5 ASCII Invalid Example — `([)]`

```text
read '('
stack:
(

read '['
stack:
(
[ <- top

read ')'

expected top '('

but top is '['

mismatch
=> invalid
```

---

## 2.6 C++17

```cpp
class Solution {
public:
    bool isValid(string s) {
        stack<char> st;

        for (char c : s) {
            if (c == '(' || c == '[' || c == '{') {
                st.push(c);
            } else {
                if (st.empty()) {
                    return false;
                }

                char open = st.top();
                st.pop();

                if ((c == ')' && open != '(') ||
                    (c == ']' && open != '[') ||
                    (c == '}' && open != '{')) {
                    return false;
                }
            }
        }

        return st.empty();
    }
};
```

---

## 2.7 Recognition Signal

Think normal stack when:

```text
latest unmatched thing
must be resolved first
```

Common clues:

```text
brackets
nested structure
undo
latest unfinished operation
```

---

## 2.8 Common Mistakes

```text
1. Calling top() before checking empty().
2. Forgetting unmatched opening brackets at the end.
3. Matching by bracket type incorrectly.
```

---

# 3. Next Greater Element

Problem: [LeetCode 496 — Next Greater Element I](https://leetcode.com/problems/next-greater-element-i/)

## 3.1 What Is the Problem Asking?

For each value:

```text
find the first greater value on its right
```

Example:

```text
a = [2,1,4,3]

2 -> 4
1 -> 4
4 -> -1
3 -> -1
```

---

## 3.2 Brute Force

For every `i`:

```text
scan i+1, i+2, ...
until greater element appears
```

Worst case:

```text
O(n^2)
```

---

## 3.3 Key Observation

Scan:

```text
right -> left
```

Why?

Because when processing `a[i]`, we want information about everything on its right.

We maintain only useful greater candidates.

---

## 3.4 What Becomes Useless?

Suppose current value is:

```text
x = a[i]
```

If stack top is:

```text
<= x
```

then top cannot be next greater for `x`.

It also becomes useless as a candidate behind `x` for relevant future positions because `x` is closer and at least as large.

So:

```cpp
while (!st.empty() && st.top() <= a[i]) {
    st.pop();
}
```

After popping:

```text
top is the nearest greater value
```

---

## 3.5 ASCII Example

```text
a = [2,1,4,3]

scan right -> left
```

Start:

```text
i=3, x=3

stack empty

NGE = -1

push 3

stack:
3
```

Next:

```text
i=2, x=4

top 3 <= 4
pop 3

stack empty

NGE = -1

push 4

stack:
4
```

Next:

```text
i=1, x=1

top 4 > 1

NGE = 4

push 1

stack:
4
1 <- top
```

Next:

```text
i=0, x=2

top 1 <= 2
pop 1

top 4 > 2

NGE = 4

push 2
```

Final:

```text
[4,4,-1,-1]
```

---

## 3.6 Stack Picture

At `x = 2`:

```text
stack before:

4
1 <- top

1 is useless because:
1 <= 2

pop 1

stack:

4 <- top

4 is greater than 2
and is nearest valid candidate

answer = 4
```

---

## 3.7 C++17

```cpp
vector<int> nextGreater(vector<int>& a) {
    int n = a.size();

    vector<int> ans(n, -1);
    stack<int> st;

    for (int i = n - 1; i >= 0; i--) {
        while (!st.empty() && st.top() <= a[i]) {
            st.pop();
        }

        if (!st.empty()) {
            ans[i] = st.top();
        }

        st.push(a[i]);
    }

    return ans;
}
```

---

## 3.8 Complexity

Even though we have:

```cpp
for (...)
    while (...)
```

the total is still:

```text
O(n)
```

because every element is:

```text
pushed once
popped at most once
```

---

## 3.9 Recognition Signal

Think next greater when:

```text
first greater on right
nearest greater on right
next warmer day
next higher price
```

---

# 4. Smallest Number After Removing K Digits

Problem: [LeetCode 402 — Remove K Digits](https://leetcode.com/problems/remove-k-digits/)

## 4.1 What Is the Problem Asking?

Remove exactly `k` digits so the remaining number is minimum.

Example:

```text
1432219
k = 3

answer = 1219
```

---

## 4.2 Greedy Observation

Consider:

```text
... x y ...
```

with:

```text
x > y
```

If we must delete one of them:

```text
delete x:
... y ...

delete y:
... x ...
```

The first differing digit becomes:

```text
y vs x
```

Since:

```text
y < x
```

deleting `x` gives the smaller number.

Therefore:

```text
when smaller digit arrives,
remove larger previous digits
while deletion budget remains
```

---

## 4.3 Monotonic Stack Rule

For current digit `d`:

```text
while:

k > 0
AND stack not empty
AND stack.top > d

pop
```

This tries to maintain increasing digits.

---

## 4.4 ASCII Example — `1432219`

Start:

```text
k = 3
```

Read `1`:

```text
[1]
```

Read `4`:

```text
1 <= 4
push

[1,4]
```

Read `3`:

```text
4 > 3

delete 4
k = 2

[1]

push 3

[1,3]
```

Read `2`:

```text
3 > 2

delete 3
k = 1

[1]

push 2

[1,2]
```

Read next `2`:

```text
top 2 > 2 ? no

[1,2,2]
```

Read `1`:

```text
top 2 > 1

delete one 2
k = 0

[1,2]

push 1

[1,2,1]
```

Read `9`:

```text
[1,2,1,9]
```

Answer:

```text
1219
```

---

## 4.5 Why Remove from End if k Remains?

Example:

```text
12345
k = 2
```

No decreasing pair appears.

To minimize:

```text
remove largest suffix digits
```

So:

```text
12345
remove 5
remove 4
=> 123
```

Hence after scan:

```cpp
while (k > 0) {
    pop_back();
}
```

---

## 4.6 Leading Zeros

Example:

```text
10200
remove 1
```

Result may start with zeros:

```text
0200
```

Normalize to:

```text
200
```

If everything disappears:

```text
return "0"
```

---

## 4.7 C++17

```cpp
class Solution {
public:
    string removeKdigits(string num, int k) {
        string st;

        for (char digit : num) {
            while (!st.empty() && k > 0 && st.back() > digit) {
                st.pop_back();
                k--;
            }

            st.push_back(digit);
        }

        while (k > 0 && !st.empty()) {
            st.pop_back();
            k--;
        }

        int firstNonZero = 0;

        while (firstNonZero < (int)st.size() &&
               st[firstNonZero] == '0') {
            firstNonZero++;
        }

        string ans = st.substr(firstNonZero);

        return ans.empty() ? "0" : ans;
    }
};
```

---

## 4.8 Recognition Signal

Think greedy monotonic stack when:

```text
remove k items
make sequence lexicographically / numerically smallest
```

Key clue:

```text
earlier positions matter more
```

So remove a bad earlier large item when a smaller one arrives.

---

# 5. Monotonic Stack Core

A monotonic stack is not just "a sorted stack."

Its deeper purpose is:

```text
remove elements that can never be useful again
```

Main question:

```text
What becomes permanently useless
when current element arrives?
```

Those are the elements you pop.

---

## 5.1 Four Core Queries

| Need | Typical Pop Condition |
|---|---|
| previous smaller | `top >= current` |
| next smaller | `top >= current` |
| previous greater | `top <= current` |
| next greater | `top <= current` |

Direction determines previous vs next.

```text
left -> right
often gives PREVIOUS information

right -> left
often gives NEXT information
```

---

## 5.2 Previous Smaller

Need:

```text
nearest index j < i
with a[j] < a[i]
```

Scan left -> right.

Pop:

```cpp
while (!st.empty() && a[st.top()] >= a[i]) {
    st.pop();
}
```

Then:

```text
top = previous smaller
```

---

## 5.3 Next Smaller

Need:

```text
nearest index j > i
with a[j] < a[i]
```

Scan right -> left.

Same pop idea:

```cpp
while (!st.empty() && a[st.top()] >= a[i]) {
    st.pop();
}
```

Then:

```text
top = next smaller
```

---

## 5.4 Previous Greater

Scan left -> right.

Pop:

```cpp
while (!st.empty() && a[st.top()] <= a[i]) {
    st.pop();
}
```

---

## 5.5 Next Greater

Scan right -> left.

Pop:

```cpp
while (!st.empty() && a[st.top()] <= a[i]) {
    st.pop();
}
```

---

## 5.6 Boundary Formula

If index `i` is bounded by:

```text
L = blocker on left
R = blocker on right
```

then common formulas are:

```text
leftChoices  = i - L
rightChoices = R - i
```

ASCII:

```text
        valid ownership region

L          i                 R
|----------|-----------------|
           ^
        fixed index
```

For full span width:

```text
width = R - L - 1
```

---

# 6. Nearest Smaller Values AZ101

Problem title from the source:
[Maang — Nearest Smaller Values AZ101](https://maang.in/problems/Nearest-Smaller-Values-AZ101-378)

> The provided source explicitly says the exact Maang statement was unavailable.
> This section therefore keeps the same source framing and explains the standard
> **nearest smaller to the left** formulation associated with the title.
> Do not treat this as a guaranteed reconstruction of the original Maang statement.

## 6.1 Standard Formulation

For each `a[i]`, find the nearest value/index on the left that is strictly smaller.

Example:

```text
a = [2,5,1,4]
```

Answers by value:

```text
2 -> -1
5 -> 2
1 -> -1
4 -> 1
```

---

## 6.2 Brute Force

For every `i`:

```text
j = i-1

while j >= 0:
    if a[j] < a[i]:
        answer found
        break

    j--
```

Worst case:

```text
O(n^2)
```

---

## 6.3 Monotonic Observation

Before using stack top for current `a[i]`:

```text
remove everything >= a[i]
```

Why?

If:

```text
top >= current
```

then top cannot answer:

```text
"nearest STRICTLY smaller"
```

So pop it.

After all invalid values disappear:

```text
top = nearest smaller on left
```

---

## 6.4 ASCII Dry Run — `[2,5,1,4]`

### i = 0, x = 2

```text
stack empty

answer = -1

push index 0

stack values:
2
```

### i = 1, x = 5

```text
top 2 < 5

answer = 2

push 5

stack:
2
5 <- top
```

### i = 2, x = 1

```text
top 5 >= 1
pop 5

top 2 >= 1
pop 2

empty

answer = -1

push 1
```

### i = 3, x = 4

```text
top 1 < 4

answer = 1
```

Final:

```text
[-1, 2, -1, 1]
```

---

## 6.5 C++17

```cpp
vector<int> nearestSmallerLeft(vector<int>& a) {
    int n = a.size();

    vector<int> ans(n, -1);
    stack<int> st;

    for (int i = 0; i < n; i++) {
        while (!st.empty() && a[st.top()] >= a[i]) {
            st.pop();
        }

        if (!st.empty()) {
            ans[i] = a[st.top()];
        }

        st.push(i);
    }

    return ans;
}
```

---

## 6.6 Recognition Signal

Think this form when the statement asks:

```text
nearest smaller on left
previous smaller
closest earlier smaller
```

---

# 7. Largest Rectangle

Problem:
[Maang — Largest Rectangle](https://maang.in/problems/Largest-Rectangle-461)

Equivalent standard problem:
[LeetCode 84 — Largest Rectangle in Histogram](https://leetcode.com/problems/largest-rectangle-in-histogram/)

## 7.1 What Is the Problem Asking?

Histogram:

```text
each a[i] = bar height
```

Find maximum rectangle fully contained in the histogram.

Example:

```text
heights = [2,1,5,6,2,3]
```

---

## 7.2 Brute Force Idea

For every possible interval:

```text
find minimum height in interval

area =
minimumHeight × width
```

Too slow.

Contribution-style reversal:

```text
fix one bar as the rectangle's minimum height
```

Then ask:

```text
how far can this height extend?
```

---

## 7.3 Boundary Observation

Fix bar:

```text
height = h[i]
```

It can extend left until:

```text
a smaller bar blocks it
```

and right until:

```text
a smaller bar blocks it
```

Let:

```text
L = previous smaller index
R = next smaller index
```

Then valid bars are:

```text
L+1 ... R-1
```

Width:

```text
(R-1) - (L+1) + 1
=
R - L - 1
```

Area:

```text
h[i] × (R-L-1)
```

---

## 7.4 ASCII Visual

For:

```text
[2,1,5,6,2,3]
     ^ ^
     5 6
```

Focus on height `5` at index `2`.

Previous smaller:

```text
index 1
height 1
```

Next smaller:

```text
index 4
height 2
```

Picture:

```text
index:   0   1   2   3   4   5
height:  2   1   5   6   2   3
             ^   ^   ^
             L   i   R

valid rectangle span:

            [5][6]

width = 2
height = 5

area = 10
```

More explicit boundary view:

```text
      smaller            smaller
         |                  |
         v                  v

... [1] [5] [6] [2] ...

        |<---->|
        width 2
```

---

## 7.5 Step-by-Step Derivation

```text
Step 1:
Fix bar i as rectangle height.

Step 2:
Move left while bars are >= h[i].

Step 3:
Stop at first smaller bar L.

Step 4:
Move right while bars are >= h[i].

Step 5:
Stop at first smaller bar R.

Step 6:
Valid width = R-L-1.

Step 7:
Area = h[i] × width.

Step 8:
Take maximum over all i.
```

---

## 7.6 Why Use Indices?

We need:

```text
width
```

So values are insufficient.

We need boundary positions:

```text
L
R
```

Hence stack stores:

```text
indices
```

---

## 7.7 C++17

```cpp
class Solution {
public:
    int largestRectangleArea(vector<int>& h) {
        int n = h.size();

        vector<int> left(n), right(n);
        stack<int> st;

        for (int i = 0; i < n; i++) {
            while (!st.empty() && h[st.top()] >= h[i]) {
                st.pop();
            }

            left[i] = st.empty() ? -1 : st.top();
            st.push(i);
        }

        while (!st.empty()) {
            st.pop();
        }

        for (int i = n - 1; i >= 0; i--) {
            while (!st.empty() && h[st.top()] >= h[i]) {
                st.pop();
            }

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

---

## 7.8 Recognition Signal

Think histogram-boundary stack when:

```text
fix value as minimum height
need maximum span where all values >= it
```

Formula:

```text
height × width
```

with:

```text
width = R-L-1
```

---

# 8. Rain Water

Problem:
[Maang — Rain Water](https://maang.in/problems/Rain-Water-462)

Equivalent standard problem:
[LeetCode 42 — Trapping Rain Water](https://leetcode.com/problems/trapping-rain-water/)

## 8.1 What Is the Problem Asking?

Bars form elevations.

We need total trapped water.

Example:

```text
[3,0,2]
```

Water:

```text
2 units
```

---

## 8.2 Valley Model

A trapped-water region needs:

```text
left wall
bottom
right wall
```

ASCII:

```text
left wall       right wall
    |               |
    v               v

    █               █
    █ ~ ~ ~ ~ ~ ~   █
    █     bottom    █
    █       █       █
```

Water level is limited by the shorter wall.

---

## 8.3 Formula

Let:

```text
L   = left wall index
mid = valley bottom index
R   = right wall index
```

Bounded water height:

```text
min(h[L], h[R]) - h[mid]
```

Width:

```text
R - L - 1
```

Added water:

```text
boundedHeight × width
```

---

## 8.4 Why Monotonic Stack?

While scanning left -> right:

```text
a taller current bar
may close a valley
```

So while:

```text
current height > stack top height
```

we pop the valley bottom.

After popping:

```text
new top = left wall
current = right wall
popped item = bottom
```

---

## 8.5 ASCII Dry Run — `[3,0,2]`

Start:

```text
i=0, h=3
push 0
```

Stack:

```text
3
```

Next:

```text
i=1, h=0
push 1
```

Stack values:

```text
3
0 <- top
```

Current:

```text
i=2, h=2
```

Since:

```text
2 > 0
```

pop bottom:

```text
mid = 1
height = 0
```

Now stack top:

```text
left = index 0
height = 3
```

Current is right wall:

```text
right = index 2
height = 2
```

Water height:

```text
min(3,2) - 0
= 2
```

Width:

```text
2 - 0 - 1
= 1
```

Water:

```text
2 × 1
= 2
```

---

## 8.6 Visual

```text
index:   0   1   2
height:  3   0   2

         █
         █ ~ █
         █ ~ █
         -----

water above index 1:
2 units
```

---

## 8.7 C++17

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

                if (st.empty()) {
                    break;
                }

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

---

## 8.8 Recognition Signal

Think valley stack when:

```text
a new higher right boundary
can close a basin formed by previous bars
```

Mental picture:

```text
left wall
bottom
right wall arrives
```

---

# 9. Height of Soldiers

Problem title from source:
[Maang — Height of Soldiers](https://maang.in/problems/Height-of-Soldiers-88)

> The provided source explicitly says the exact Maang statement was unavailable.
> Therefore this section preserves the source's **decision framework** instead of
> inventing a specific solution.

## 9.1 First Decode the Statement

Ask:

```text
Is it asking for nearest taller?

or

nearest shorter?
```

Then ask:

```text
left or right?
```

Then decide:

```text
greater stack
or
smaller stack
```

---

## 9.2 Nearest Taller on Left

Need:

```text
nearest previous value > h[i]
```

Scan:

```text
left -> right
```

Pop useless values:

```cpp
while (!st.empty() && h[st.top()] <= h[i]) {
    st.pop();
}
```

After popping:

```text
top = nearest taller on left
```

---

## 9.3 ASCII Example

```text
h = [4,2,5,3]
```

For `2`:

```text
4 is on left
4 > 2

nearest taller = 4
```

For `5`:

```text
stack has 4,2

2 <= 5 -> pop
4 <= 5 -> pop

none taller
```

For `3`:

```text
5 remains

5 > 3

nearest taller = 5
```

Answers:

```text
[-1, 4, -1, 5]
```

---

## 9.4 If Statement Asks Distance

If top index is answer boundary:

```text
distance = i - st.top()
```

Example:

```text
index:   0   1   2   3
height:  4   2   5   3
                 ^   ^
                 2   3

nearest taller left of index 3
= index 2

distance
= 3 - 2
= 1
```

---

## 9.5 If Statement Asks Index

Return:

```cpp
st.top()
```

---

## 9.6 If Statement Asks Height

Return:

```cpp
h[st.top()]
```

---

## 9.7 C++ Pattern — Nearest Taller Left

```cpp
vector<int> nearestTallerLeft(vector<int>& h) {
    int n = h.size();

    vector<int> ans(n, -1);
    stack<int> st;

    for (int i = 0; i < n; i++) {
        while (!st.empty() && h[st.top()] <= h[i]) {
            st.pop();
        }

        if (!st.empty()) {
            ans[i] = st.top();
        }

        st.push(i);
    }

    return ans;
}
```

---

# 10. Four Core Templates

These are the four forms worth memorizing after understanding why they work.

## 10.1 Previous Smaller

```cpp
stack<int> st;

for (int i = 0; i < n; i++) {
    while (!st.empty() && a[st.top()] >= a[i]) {
        st.pop();
    }

    ans[i] = st.empty() ? -1 : st.top();

    st.push(i);
}
```

Visual:

```text
scan:
left -> right

want:
previous smaller

remove:
>= current
```

---

## 10.2 Next Smaller

```cpp
stack<int> st;

for (int i = n - 1; i >= 0; i--) {
    while (!st.empty() && a[st.top()] >= a[i]) {
        st.pop();
    }

    ans[i] = st.empty() ? n : st.top();

    st.push(i);
}
```

Visual:

```text
scan:
right -> left

want:
next smaller

remove:
>= current
```

---

## 10.3 Previous Greater

```cpp
stack<int> st;

for (int i = 0; i < n; i++) {
    while (!st.empty() && a[st.top()] <= a[i]) {
        st.pop();
    }

    ans[i] = st.empty() ? -1 : st.top();

    st.push(i);
}
```

Visual:

```text
scan:
left -> right

want:
previous greater

remove:
<= current
```

---

## 10.4 Next Greater

```cpp
stack<int> st;

for (int i = n - 1; i >= 0; i--) {
    while (!st.empty() && a[st.top()] <= a[i]) {
        st.pop();
    }

    ans[i] = st.empty() ? n : st.top();

    st.push(i);
}
```

Visual:

```text
scan:
right -> left

want:
next greater

remove:
<= current
```

---

## 10.5 Tiny Memory Table

| Need | Direction | Pop |
|---|---|---|
| Previous Smaller | left → right | `>= current` |
| Next Smaller | right → left | `>= current` |
| Previous Greater | left → right | `<= current` |
| Next Greater | right → left | `<= current` |

---

# 11. Recognition Checklist

When a stack-looking problem appears, ask:

```text
1. Is this normal LIFO matching?
   -> normal stack

2. Do I need nearest smaller/greater?
   -> monotonic stack

3. Is answer on LEFT or RIGHT?
   -> choose scan direction

4. Need SMALLER or GREATER?
   -> choose pop condition

5. Need VALUE or INDEX?
   -> decide what to store/output

6. Need WIDTH / DISTANCE?
   -> store indices

7. Are duplicates important?
   -> decide strict vs non-strict carefully

8. Does a new element permanently make old candidates useless?
   -> those old candidates should be popped

9. Is this a boundary contribution problem?
   -> compute L and R

10. Is this a valley problem?
    -> popped item may be valley bottom
```

---

# 12. Complexity

The most important monotonic-stack complexity fact:

```text
every index is pushed once
every index is popped at most once
```

Even if code looks like:

```cpp
for (...) {
    while (...) {
        pop();
    }

    push();
}
```

it is not:

```text
O(n^2)
```

Total pushes:

```text
<= n
```

Total pops:

```text
<= n
```

Therefore:

```text
Time = O(n)
```

Typical stack space:

```text
O(n)
```

---

# 13. Final Pattern Map

| Problem | Stack Type | Main Idea |
|---|---|---|
| Valid Parentheses | normal stack | latest unmatched opening |
| Next Greater | monotonic decreasing candidates | pop `<= current` |
| Remove K Digits | greedy increasing sequence | pop larger previous digit |
| Nearest Smaller | monotonic increasing candidates | pop `>= current` |
| Largest Rectangle | smaller boundaries | `height × (R-L-1)` |
| Rain Water | valley detection | right wall closes basin |
| Nearest Taller | greater candidates | pop `<= current` |
| Nearest Shorter | smaller candidates | pop `>= current` |

---

## Final Visual Mental Model

```text
CURRENT ELEMENT ARRIVES
          |
          v
Which old elements are now
PERMANENTLY USELESS?
          |
          v
        POP THEM
          |
          v
What remains at stack top?
          |
          +-----------------------------+
          |                             |
          v                             v
   nearest useful                left/right boundary
     candidate                           |
                                         v
                                contribution / width
```

And the one question to remember:

```text
"What becomes useless when current arrives?"
```

That is the heart of monotonic-stack thinking.
