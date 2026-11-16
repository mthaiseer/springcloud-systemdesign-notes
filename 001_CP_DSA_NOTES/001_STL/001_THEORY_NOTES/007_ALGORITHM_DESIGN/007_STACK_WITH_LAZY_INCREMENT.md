# AlgoZenith — Stack with Lazy Increments (STL)
## Visual Self-Learning Version

> **Core idea:** Do not update the bottom `k` elements immediately.  
> Store one **pending prefix increment at the highest affected index**, then carry it downward during `pop()`.

# Table of Contents

- [0. Prerequisites](#0-prerequisites)
- [1. Problem](#1-problem)
- [2. Why Naive `inc(k,val)` Is Slow](#2-why-naive-inckval-is-slow)
- [3. Core Lazy Idea](#3-core-lazy-idea)
- [4. State Design](#4-state-design)
- [5. `push(x)` — Step by Step](#5-pushx--step-by-step)
- [6. `inc(k,val)` — Step by Step](#6-inckval--step-by-step)
- [7. `pop()` — Step by Step](#7-pop--step-by-step)
- [8. Why Propagation Works](#8-why-propagation-works)
- [9. Full Dry Run](#9-full-dry-run)
- [10. Complete C++17](#10-complete-c17)
- [11. Complexity](#11-complexity)
- [12. Common Mistakes](#12-common-mistakes)
- [13. Recognition Checklist](#13-recognition-checklist)
- [14. Final Mental Model](#14-final-mental-model)

---

# 0. Prerequisites

## 0.1 Normal Stack

A stack follows:

```text
LIFO = Last In, First Out
```

Example:

```text
push 1
push 2
push 3
```

Stack:

```text
TOP
 |
 v

[3]
[2]
[1]

BOTTOM
```

`3` is removed first.

---

## 0.2 Vector as a Stack

We can use:

```cpp
vector<long long> values;
```

Useful operations:

```cpp
values.push_back(x);
values.pop_back();
values.back();
values.size();
values.empty();
```

The top index is:

```text
top = size - 1
```

---

## 0.3 Prefix

A prefix starts at index `0`.

Example:

```text
values = [1,2,3,4,5]
```

Bottom `3` elements are:

```text
indices:
0 1 2

values:
1 2 3
```

That is prefix:

```text
[0..2]
```

---

## 0.4 Lazy Update

Lazy means:

```text
do not apply the update
to every affected element now
```

Instead:

```text
store a compact marker now
and apply/propagate it later
```

This is the same general idea used in:

```text
difference arrays
lazy propagation
deferred updates
```

---

# 1. Problem

A stack has maximum capacity:

```text
maxSize
```

Support:

```text
push(x)
pop()
inc(k,val)
```

Meaning:

```text
push(x)
-> push x if stack is not full

pop()
-> remove and return top
-> -1 if empty

inc(k,val)
-> add val to bottom min(k,size) elements
```

Target:

```text
push -> O(1)
pop  -> O(1)
inc  -> O(1)
```

---

# 2. Why Naive `inc(k,val)` Is Slow

Suppose:

```text
stack = [1,2,3,4,5]
```

Call:

```text
inc(4,10)
```

Naive result:

```text
[11,12,13,14,5]
```

We touched:

```text
4 elements
```

So:

```text
inc(k,val) = O(k)
```

If many increment queries occur, this becomes too slow.

---

## 2.1 Better Question

Do we really need to physically update all bottom `k` values immediately?

No.

Because stack removal is LIFO:

```text
top is exposed first
```

We can postpone the increment.

Mental shift:

```text
NAIVE

inc(k,val)
   |
   v
update k values now


LAZY

inc(k,val)
   |
   v
store one boundary marker
   |
   v
propagate later during pop
```

---

# 3. Core Lazy Idea

Suppose:

```text
inc(k,val)
```

affects:

```text
indices 0 .. k-1
```

The highest affected index is:

```text
k-1
```

So instead of updating every affected value, store:

```text
lazy[k-1] += val
```

If `k > size`, clamp to current size:

```text
boundary = min(k,size)-1
```

Only one position changes.

---

## 3.1 Example

```text
values = [1,2,3,4]

inc(3,10)
```

Affected indices:

```text
0,1,2
```

Boundary:

```text
2
```

Store:

```text
lazy[2] += 10
```

State:

```text
values = [1,2,3,4]
lazy   = [0,0,10,0]
```

Meaning:

```text
lazy[2] = 10
```

means:

```text
+10 belongs to prefix [0..2]
```

Logical values are:

```text
[11,12,13,4]
```

even though `values` itself is unchanged.

---

## 3.2 ASCII Boundary Diagram

```text
index:    0    1    2    3
value:    1    2    3    4
                    ^
                    |
                 boundary

lazy:     0    0   +10   0

+10 applies downward:

          +10  +10  +10
           |    |    |
           v    v    v

value:    [1]  [2]  [3]  [4]
```

---

# 4. State Design

Maintain two synchronized vectors:

```cpp
vector<long long> values;
vector<long long> lazy;
```

Meaning:

```text
values[i]
=
base value

lazy[i]
=
pending increment that belongs
to prefix [0..i]
```

---

## 4.1 Example

```text
values = [1,2,3]
lazy   = [0,5,0]
```

`lazy[1] = 5` means:

```text
+5 applies to indices 0 and 1
```

Logical values:

```text
[6,7,3]
```

---

## 4.2 Important Invariant

`lazy[i]` does **not** mean only:

```text
add lazy[i] to values[i]
```

It means:

```text
this increment belongs to
every element from index 0 through i
```

That is why it must be carried downward when `i` is popped.

---

# 5. `push(x)` — Step by Step

## Step 1 — Check Capacity

If:

```text
size == maxSize
```

do nothing.

---

## Step 2 — Push Base Value

```cpp
values.push_back(x);
```

---

## Step 3 — Push Zero Lazy Marker

```cpp
lazy.push_back(0);
```

Why zero?

A newly pushed value did not exist when older increments happened.

So it must not inherit them.

---

## 5.1 Example

Before:

```text
values = [1,2]
lazy   = [0,5]
```

Logical:

```text
[6,7]
```

Now:

```text
push(3)
```

Correct:

```text
values = [1,2,3]
lazy   = [0,5,0]
```

Logical:

```text
[6,7,3]
```

Not:

```text
[6,7,8]
```

---

## 5.2 ASCII

Before:

```text
TOP

value 2
lazy +5

value 1
lazy  0

BOTTOM
```

Push `3`:

```text
TOP

value 3
lazy  0   <- no old increment

value 2
lazy +5

value 1
lazy  0

BOTTOM
```

---

# 6. `inc(k,val)` — Step by Step

## Step 1 — Invalid Cases

If:

```text
stack empty
```

or:

```text
k <= 0
```

do nothing.

---

## Step 2 — Number of Affected Elements

```text
count = min(k,size)
```

---

## Step 3 — Find Boundary

```text
boundary = count - 1
```

So:

```text
boundary = min(k,size)-1
```

---

## Step 4 — Store One Marker

```cpp
lazy[boundary] += val;
```

Done.

---

## 6.1 Example

```text
values = [1,2,3,4]
lazy   = [0,0,0,0]

inc(3,10)
```

Affected:

```text
indices 0,1,2
```

Boundary:

```text
min(3,4)-1
=
2
```

Update:

```text
lazy[2] += 10
```

State:

```text
values = [1,2,3,4]
lazy   = [0,0,10,0]
```

Logical:

```text
[11,12,13,4]
```

---

## 6.2 If `k > size`

Example:

```text
size = 3
k    = 10
```

Affected:

```text
min(10,3) = 3
```

Boundary:

```text
3-1 = 2
```

So all current elements receive the increment logically.

---

# 7. `pop()` — Step by Step

This is the key operation.

Let:

```text
top = size - 1
```

---

## Step 1 — Empty Check

If empty:

```text
return -1
```

---

## Step 2 — Compute Logical Top

```text
answer
=
values[top] + lazy[top]
```

Code:

```cpp
long long ans =
    values[top] + lazy[top];
```

---

## Step 3 — Propagate Lazy Marker Downward

If:

```text
top > 0
```

do:

```cpp
lazy[top - 1] += lazy[top];
```

Why?

Because `lazy[top]` belongs not only to the top element, but also to every lower element.

---

## Step 4 — Remove Top

```cpp
values.pop_back();
lazy.pop_back();
```

---

## Step 5 — Return Answer

```cpp
return ans;
```

---

## 7.1 ASCII Pop

Before:

```text
TOP

value 3
lazy +5
   |
   | +5 also belongs below
   v

value 2
lazy +10

value 1
lazy 0

BOTTOM
```

Logical top:

```text
3 + 5 = 8
```

Before deleting top marker:

```text
lazy below
=
10 + 5
=
15
```

After pop:

```text
TOP

value 2
lazy +15

value 1
lazy 0

BOTTOM
```

The `+5` was preserved.

---

# 8. Why Propagation Works

Suppose:

```text
lazy[i] = x
```

By definition:

```text
+x applies to prefix [0..i]
```

When `i` becomes top:

```text
logical value at i
=
values[i] + lazy[i]
```

After removing index `i`, the same `+x` still belongs to:

```text
0..i-1
```

So move it down:

```text
lazy[i-1] += lazy[i]
```

---

## 8.1 Overlapping Increments

Suppose:

```text
lazy = [0,10,5]
```

Interpretation:

```text
lazy[1] = +10
-> applies to 0..1

lazy[2] = +5
-> applies to 0..2
```

Therefore logical pending increment is:

```text
index 2 -> +5
index 1 -> +10 +5 = +15
index 0 -> +10 +5 = +15
```

When index `2` is popped:

```text
its +5 must survive for lower elements
```

So:

```text
lazy[1]
=
10 + 5
=
15
```

New state:

```text
lazy = [0,15]
```

Correct.

---

## 8.2 Why `+=` Matters

Wrong:

```cpp
lazy[top - 1] = lazy[top];
```

This would erase older pending increments.

Correct:

```cpp
lazy[top - 1] += lazy[top];
```

because lazy updates can overlap.

---

# 9. Full Dry Run

Use:

```text
maxSize = 3
```

---

## Step 1 — Push 1, 2, 3

```text
values = [1,2,3]
lazy   = [0,0,0]
```

---

## Step 2 — `inc(2,10)`

Affected:

```text
bottom 2
=
indices 0,1
```

Boundary:

```text
min(2,3)-1
=
1
```

Update:

```text
lazy[1] += 10
```

State:

```text
values = [1,2,3]
lazy   = [0,10,0]
```

Logical:

```text
[11,12,3]
```

---

## Step 3 — `inc(3,5)`

Affected:

```text
indices 0,1,2
```

Boundary:

```text
2
```

Update:

```text
lazy[2] += 5
```

State:

```text
values = [1,2,3]
lazy   = [0,10,5]
```

Logical:

```text
index 0:
1 +10 +5 = 16

index 1:
2 +10 +5 = 17

index 2:
3 +5 = 8
```

So:

```text
[16,17,8]
```

---

## Step 4 — `pop()`

Top:

```text
index = 2
```

Answer:

```text
3 + 5 = 8
```

Propagate:

```text
lazy[1]
=
10 + 5
=
15
```

Remove top:

```text
values = [1,2]
lazy   = [0,15]
```

Return:

```text
8
```

---

## Step 5 — `push(4)`

New value gets zero marker:

```text
values = [1,2,4]
lazy   = [0,15,0]
```

Logical:

```text
[16,17,4]
```

Important:

```text
4 did not exist during old increments
```

so it does not inherit `+15`.

---

## Step 6 — `pop()`

```text
4 + 0 = 4
```

Return:

```text
4
```

State:

```text
values = [1,2]
lazy   = [0,15]
```

---

## Step 7 — `pop()`

Answer:

```text
2 + 15 = 17
```

Propagate:

```text
lazy[0]
=
0 +15
=
15
```

State:

```text
values = [1]
lazy   = [15]
```

Return:

```text
17
```

---

## Step 8 — `pop()`

```text
1 +15 = 16
```

Return:

```text
16
```

Returned sequence:

```text
8, 4, 17, 16
```

---

## 9.1 State Table

| Step | Operation | `values` | `lazy` | Logical / Return |
|---:|---|---|---|---|
| 1 | push `1,2,3` | `[1,2,3]` | `[0,0,0]` | `[1,2,3]` |
| 2 | `inc(2,10)` | `[1,2,3]` | `[0,10,0]` | `[11,12,3]` |
| 3 | `inc(3,5)` | `[1,2,3]` | `[0,10,5]` | `[16,17,8]` |
| 4 | `pop()` | `[1,2]` | `[0,15]` | `8` |
| 5 | `push(4)` | `[1,2,4]` | `[0,15,0]` | `[16,17,4]` |
| 6 | `pop()` | `[1,2]` | `[0,15]` | `4` |
| 7 | `pop()` | `[1]` | `[15]` | `17` |
| 8 | `pop()` | `[]` | `[]` | `16` |

---

# 10. Complete C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

class CustomStack {
    vector<long long> values;
    vector<long long> lazy;

    int maxSize;

public:
    CustomStack(int capacity) {
        maxSize = max(0, capacity);

        values.reserve(maxSize);
        lazy.reserve(maxSize);
    }

    void push(long long x) {
        if ((int)values.size() == maxSize) {
            return;
        }

        values.push_back(x);

        // New value must not inherit old increments.
        lazy.push_back(0);
    }

    long long pop() {
        if (values.empty()) {
            return -1;
        }

        int top = (int)values.size() - 1;

        long long ans =
            values[top] + lazy[top];

        // Preserve pending increment for lower elements.
        if (top > 0) {
            lazy[top - 1] += lazy[top];
        }

        values.pop_back();
        lazy.pop_back();

        return ans;
    }

    void inc(int k, long long val) {
        if (values.empty() || k <= 0) {
            return;
        }

        int count =
            min(k, (int)values.size());

        int boundary = count - 1;

        lazy[boundary] += val;
    }
};
```

---

# 11. Complexity

| Operation | Time |
|---|---:|
| `push` | `O(1)` |
| `pop` | `O(1)` |
| `inc` | `O(1)` |
| Space | `O(maxSize)` |

Core optimization:

```text
Naive inc
-> touch k elements
-> O(k)

Lazy inc
-> touch one boundary marker
-> O(1)
```

---

# 12. Common Mistakes

## 12.1 Loop Through Bottom k Values

That makes:

```text
inc = O(k)
```

and loses the optimization.

---

## 12.2 Use `k-1` Without Clamping

Wrong when:

```text
k > size
```

Correct:

```text
boundary
=
min(k,size)-1
```

---

## 12.3 Forget `k <= 0`

No element should change.

---

## 12.4 Delete `lazy[top]` Before Propagating

Wrong.

The marker also belongs to lower elements.

Correct order:

```text
1. compute answer
2. propagate lazy[top]
3. remove top
```

---

## 12.5 Use Assignment Instead of Addition

Wrong:

```cpp
lazy[top - 1] = lazy[top];
```

Correct:

```cpp
lazy[top - 1] += lazy[top];
```

because updates can overlap.

---

## 12.6 New Push Inherits Old Increment

Wrong.

Always:

```cpp
lazy.push_back(0);
```

---

## 12.7 Use `int` for Accumulating Values

Several increments may combine.

Prefer:

```cpp
long long
```

for:

```text
values
lazy
increment value
returned value
```

---

# 13. Recognition Checklist

Think this pattern when you see:

```text
stack
+
update bottom k / prefix
+
need O(1) operations
```

Ask:

```text
1. Is the expensive operation a prefix update?

2. Can the update be stored at one boundary?

3. Highest affected index?
   -> min(k,size)-1

4. Meaning of lazy[i]?
   -> update for prefix [0..i]

5. When does marker become relevant?
   -> when i reaches top

6. Before popping marker?
   -> propagate downward

7. How?
   -> lazy[i-1] += lazy[i]

8. Does a new push inherit old updates?
   -> no

9. Can lazy updates overlap?
   -> yes, so use +=

10. Are numeric limits safe?
```

---

# 14. Final Mental Model

Think:

```text
lazy[i]
=
"this increment belongs to everyone
 from index i down to the bottom"
```

ASCII:

```text
TOP

value 3
lazy +5      ----+
                  |
value 2           |
lazy +10     --+  |
                |  |
value 1         |  |
lazy 0          |  |
                v  v

BOTTOM
```

On increment:

```text
INC

boundary = min(k,size)-1
lazy[boundary] += val
```

On pop:

```text
POP

ans = values[top] + lazy[top]

if top > 0:
    lazy[top-1] += lazy[top]

remove top
```

The one sentence to remember:

```text
Store the prefix increment at its highest affected index;
when that boundary is popped,
carry the pending increment downward.
```
