# AlgoZenith — Min Stack (STL)
## Visual Self-Learning Version

> **Core idea:** `push()` can calculate a new minimum easily. The real challenge is `pop()` — after removing the current minimum, restore the **previous minimum in O(1)**.

# Table of Contents

- [0. Prerequisites](#0-prerequisites)
- [1. Problem](#1-problem)
- [2. Why One Minimum Variable Fails](#2-why-one-minimum-variable-fails)
- [3. Core State-Restoration Idea](#3-core-state-restoration-idea)
- [4. Approach 1 — Pair Snapshot](#4-approach-1--pair-snapshot)
- [5. Pair Snapshot Dry Run](#5-pair-snapshot-dry-run)
- [6. Pair Snapshot C++17](#6-pair-snapshot-c17)
- [7. Approach 2 — Two Stacks](#7-approach-2--two-stacks)
- [8. Two-Stack Dry Run](#8-two-stack-dry-run)
- [9. Two-Stack C++17](#9-two-stack-c17)
- [10. Approach 3 — Arithmetic Encoding](#10-approach-3--arithmetic-encoding)
- [11. Encoding Algebra — Step by Step](#11-encoding-algebra--step-by-step)
- [12. Encoding Dry Run](#12-encoding-dry-run)
- [13. Encoding C++17](#13-encoding-c17)
- [14. Complexity Comparison](#14-complexity-comparison)
- [15. Common Mistakes](#15-common-mistakes)
- [16. Recognition Checklist](#16-recognition-checklist)
- [17. Final Mental Model](#17-final-mental-model)

---

# 0. Prerequisites

## 0.1 Normal Stack

A stack follows:

```text
LIFO = Last In, First Out
```

Example:

```text
push 5
push 7
push 3
```

Stack:

```text
top
 |
 v

[3]
[7]
[5]
```

`3` is removed first.

---

## 0.2 Stack Operations

```cpp
stack<int> st;

st.push(x);
st.pop();
st.top();
st.empty();
```

Typical complexity:

```text
push -> O(1)
pop  -> O(1)
top  -> O(1)
```

Min Stack adds:

```text
getMin() -> O(1)
```

---

## 0.3 Running Minimum

For:

```text
5, 7, 3, 8
```

running minimum is:

```text
after 5 -> 5
after 7 -> 5
after 3 -> 3
after 8 -> 3
```

Visual:

```text
value:        5   7   3   8
min so far:   5   5   3   3
```

---

## 0.4 Why Pop Is Harder Than Push

On push:

```text
newMin = min(x, oldMin)
```

Easy.

But on pop:

```text
if current minimum disappears,
what was the previous minimum?
```

That old information must be saved or reconstructed.

---

# 1. Problem

Support:

```text
push(x)
pop()
top()
getMin()
```

Target:

```text
push   -> O(1)
pop    -> O(1)
top    -> O(1)
getMin -> O(1)
```

Main question:

```text
push smaller value
      |
      v
minimum changes

later pop that value
      |
      v
how do we restore old minimum?
```

---

# 2. Why One Minimum Variable Fails

Suppose:

```text
stack = [5,7,3]
min   = 3
```

Visual:

```text
top
 |
 v

[3]  <- current min
[7]
[5]
```

Pop `3`.

Now:

```text
stack = [5,7]
```

Correct minimum is:

```text
5
```

But one variable:

```text
min = 3
```

has forgotten the old minimum.

So we need:

```text
minimum history
```

---

# 3. Core State-Restoration Idea

Each stack depth has its own valid minimum.

```text
depth 1:
[5]
min = 5

depth 2:
[5,7]
min = 5

depth 3:
[5,7,3]
min = 3
```

Pop once:

```text
depth 3 -> depth 2
```

So minimum must restore:

```text
3 -> 5
```

All three approaches solve the same question:

```text
How do we remember the minimum
for the previous stack depth?
```

---

# 4. Approach 1 — Pair Snapshot

Store:

```text
{actual value, minimum up to this depth}
```

Use:

```cpp
vector<pair<int,int>> st;
```

---

## 4.1 Push Logic

If empty:

```text
newMin = x
```

Otherwise:

```text
newMin = min(x, previousMin)
```

Store:

```text
{x, newMin}
```

---

## 4.2 ASCII Example

Push:

```text
5, 7, 3, 8
```

Stored pairs:

```text
push 5 -> {5,5}
push 7 -> {7,5}
push 3 -> {3,3}
push 8 -> {8,3}
```

Stack:

```text
top
 |
 v

{8,3}
{3,3}
{7,5}
{5,5}
```

Meaning:

```text
first  = actual value
second = minimum up to this depth
```

---

## 4.3 Why Pop Is Easy

Pop:

```text
{8,3}
```

Now top:

```text
{3,3}
```

Minimum still:

```text
3
```

Pop again:

```text
{3,3}
```

Now top:

```text
{7,5}
```

Minimum automatically restores to:

```text
5
```

No scan.

---

# 5. Pair Snapshot Dry Run

Push:

```text
5, 7, 3, 8
```

### push(5)

```text
newMin = 5

stack:
{5,5}
```

### push(7)

```text
newMin = min(7,5) = 5

stack:
{5,5} {7,5}
```

### push(3)

```text
newMin = min(3,5) = 3

stack:
{5,5} {7,5} {3,3}
```

### push(8)

```text
newMin = min(8,3) = 3

stack:
{5,5} {7,5} {3,3} {8,3}
```

Now:

```text
top    = 8
getMin = 3
```

Pop `8`:

```text
{5,5} {7,5} {3,3}

getMin = 3
```

Pop `3`:

```text
{5,5} {7,5}

getMin = 5
```

Previous snapshot becomes current automatically.

---

# 6. Pair Snapshot C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

class MinStack {
    // {value, minimum up to this depth}
    vector<pair<int, int>> st;

public:
    void push(int x) {
        int mn;

        if (st.empty()) {
            mn = x;
        } else {
            mn = min(x, st.back().second);
        }

        st.push_back({x, mn});
    }

    void pop() {
        if (!st.empty()) {
            st.pop_back();
        }
    }

    int top() const {
        if (st.empty()) {
            return -1;
        }

        return st.back().first;
    }

    int getMin() const {
        if (st.empty()) {
            return -1;
        }

        return st.back().second;
    }
};
```

---

# 7. Approach 2 — Two Stacks

Maintain:

```text
st       -> actual values
minStack -> minimum history
```

Push every value into:

```text
st
```

Push into `minStack` only when:

```text
minStack empty
OR
x <= minStack.top()
```

---

## 7.1 Why `<=`?

Duplicate minima must be remembered.

Example:

```text
push 3
push 3
```

Correct:

```text
st       = [3,3]
minStack = [3,3]
```

After one pop:

```text
st       = [3]
minStack = [3]
```

Minimum remains:

```text
3
```

Using only `<` would lose this history.

---

## 7.2 Pop Logic

Before popping main stack:

```text
if st.top() == minStack.top()
    pop minStack
```

Then:

```text
pop st
```

---

# 8. Two-Stack Dry Run

Push:

```text
5, 3, 4, 3
```

### push(5)

```text
st       = [5]
minStack = [5]
```

### push(3)

```text
3 <= 5
```

Push to both:

```text
st       = [5,3]
minStack = [5,3]
```

### push(4)

```text
4 > 3
```

Push only to `st`:

```text
st       = [5,3,4]
minStack = [5,3]
```

### push(3)

```text
3 <= 3
```

Push to both:

```text
st       = [5,3,4,3]
minStack = [5,3,3]
```

Pop:

```text
st.top() == minStack.top() == 3
```

So pop both:

```text
st       = [5,3,4]
minStack = [5,3]

getMin() = 3
```

---

## 8.1 ASCII Synchronization

```text
actual values         minimum history

     st                  minStack

      3  <----------->      3
      4
      3  <----------->      3
      5  <----------->      5
```

Only new or duplicate minima appear in `minStack`.

---

# 9. Two-Stack C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

class MinStack {
    stack<int> st;
    stack<int> minStack;

public:
    void push(int x) {
        st.push(x);

        if (minStack.empty() ||
            x <= minStack.top()) {

            minStack.push(x);
        }
    }

    void pop() {
        if (st.empty()) {
            return;
        }

        if (st.top() == minStack.top()) {
            minStack.pop();
        }

        st.pop();
    }

    int top() const {
        if (st.empty()) {
            return -1;
        }

        return st.top();
    }

    int getMin() const {
        if (minStack.empty()) {
            return -1;
        }

        return minStack.top();
    }
};
```

---

# 10. Approach 3 — Arithmetic Encoding

Maintain:

```text
one stack
+
one current minimum
```

```cpp
stack<long long> st;
long long minEle;
```

If:

```text
x >= minEle
```

push normally.

If:

```text
x < minEle
```

store an encoded marker:

```text
encoded = 2*x - oldMin
```

then:

```text
minEle = x
```

The marker secretly stores enough information to restore `oldMin`.

---

# 11. Encoding Algebra — Step by Step

Suppose:

```text
x < oldMin
```

Store:

```text
encoded = 2*x - oldMin
```

Then:

```text
minEle = x
```

---

## 11.1 Why Is the Marker Detectable?

Since:

```text
x < oldMin
```

we get:

```text
2*x - oldMin < x
```

But:

```text
x = new minEle
```

Therefore:

```text
encoded < minEle
```

So later:

```text
stored < minEle
```

means:

```text
stored value is an encoded marker
```

---

## 11.2 Restore the Previous Minimum

We know:

```text
encoded = 2*x - oldMin
```

Rearrange:

```text
oldMin = 2*x - encoded
```

And because:

```text
x = current minEle
```

final formula:

```text
+--------------------------------+
| oldMin = 2*minEle - encoded    |
+--------------------------------+
```

---

## 11.3 ASCII Flow

New minimum arrives:

```text
oldMin
   |
   | x < oldMin
   v
encoded = 2*x - oldMin
   |
   v
push encoded
   |
   v
minEle = x
```

Later pop marker:

```text
encoded < minEle
      |
      v
logical value = minEle
      |
      v
oldMin = 2*minEle - encoded
```

---

# 12. Encoding Dry Run

Push:

```text
5, 3, 7
```

### push(5)

```text
st     = [5]
minEle = 5
```

### push(3)

Since:

```text
3 < 5
```

encode:

```text
encoded = 2*3 - 5
        = 1
```

Store:

```text
st     = [5,1]
minEle = 3
```

Notice:

```text
1 < 3
```

so `1` is recognizable as encoded.

### push(7)

```text
7 >= 3
```

Push normally:

```text
st     = [5,1,7]
minEle = 3
```

### pop()

Top is:

```text
7
```

Normal value.

After pop:

```text
st     = [5,1]
minEle = 3
```

### top()

Stored top:

```text
1
```

Since:

```text
1 < minEle
```

it is a marker.

Logical top is:

```text
minEle = 3
```

### pop() logical 3

Restore:

```text
oldMin = 2*3 - 1
       = 5
```

Final:

```text
st     = [5]
minEle = 5
```

---

# 13. Encoding C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

class MinStack {
    stack<long long> st;
    long long minEle = 0;

public:
    void push(long long x) {
        if (st.empty()) {
            st.push(x);
            minEle = x;
            return;
        }

        if (x >= minEle) {
            st.push(x);
        } else {
            long long encoded =
                2LL * x - minEle;

            st.push(encoded);
            minEle = x;
        }
    }

    void pop() {
        if (st.empty()) {
            return;
        }

        long long stored = st.top();
        st.pop();

        if (stored < minEle) {
            minEle = 2LL * minEle - stored;
        }
    }

    long long top() const {
        if (st.empty()) {
            return -1;
        }

        long long stored = st.top();

        if (stored < minEle) {
            return minEle;
        }

        return stored;
    }

    long long getMin() const {
        if (st.empty()) {
            return -1;
        }

        return minEle;
    }
};
```

Important:

```text
2*x - minEle
```

can exceed the original integer range.

Use a sufficiently wide type and respect the problem constraints.

---

# 14. Complexity Comparison

| Approach | Push | Pop | Top | getMin | Extra Minimum State |
|---|---:|---:|---:|---:|---:|
| Pair snapshot | amortized `O(1)` | `O(1)` | `O(1)` | `O(1)` | `O(N)` |
| Two stacks | `O(1)` | `O(1)` | `O(1)` | `O(1)` | worst-case `O(N)` |
| Encoding | `O(1)` | `O(1)` | `O(1)` | `O(1)` | `O(1)` bookkeeping |

Note:

```text
all approaches still store the actual stack content
```

The encoding approach only reduces extra minimum-history state.

---

# 15. Common Mistakes

## 15.1 Store Only Current Minimum

Not enough.

After popping that minimum, previous minimum is lost.

---

## 15.2 Recalculate Minimum After Pop

That would require scanning:

```text
O(N)
```

and breaks the O(1) requirement.

---

## 15.3 Use `<` Instead of `<=` in Two Stacks

Wrong with duplicate minima.

Correct:

```cpp
x <= minStack.top()
```

---

## 15.4 Treat Encoded Marker as Real Top

If:

```text
stored < minEle
```

then logical top is:

```text
minEle
```

not the stored marker.

---

## 15.5 Forget Restoration Formula

```text
oldMin = 2*minEle - encoded
```

---

## 15.6 Ignore Overflow

Encoding arithmetic can overflow narrow integer types.

Use:

```cpp
long long
```

and check constraints.

---

# 16. Recognition Checklist

Think Min Stack when you see:

```text
stack operations
+
minimum query in O(1)
```

Ask:

```text
1. What happens when a new minimum is pushed?

2. What happens when that minimum is popped?

3. Where is the previous minimum stored?

4. Simplest design?
   -> pair snapshot

5. Separate minimum history?
   -> two stacks

6. Duplicate minima?
   -> use <=

7. O(1) extra minimum bookkeeping?
   -> arithmetic encoding

8. Encoding marker test?
   -> stored < minEle

9. Restore previous minimum?
   -> 2*minEle - encoded

10. Are numeric limits safe?
```

---

# 17. Final Mental Model

All approaches solve the same state-restoration problem.

```text
NEW MINIMUM ARRIVES
       |
       v
old minimum must be remembered
       |
       v
later pop
       |
       v
restore old minimum
```

### Pair Snapshot

```text
push x
  |
  v
newMin = min(x, oldMin)
  |
  v
store {x,newMin}

pop
 |
 v
previous snapshot becomes visible
```

### Two Stacks

```text
actual values        minimum history

     st   <------->    minStack

x <= current minimum
      |
      v
record x in both
```

### Arithmetic Encoding

```text
x >= min
-> push x

x < min
-> encode old minimum
-> push marker
-> min = x
```

Encoded pop:

```text
stored < min
      |
      v
actual top = min
      |
      v
oldMin = 2*min - stored
```

The one sentence to remember:

```text
Min Stack is a state-restoration problem:
a pop must expose or reconstruct
the minimum that was valid before
the popped value arrived.
```
