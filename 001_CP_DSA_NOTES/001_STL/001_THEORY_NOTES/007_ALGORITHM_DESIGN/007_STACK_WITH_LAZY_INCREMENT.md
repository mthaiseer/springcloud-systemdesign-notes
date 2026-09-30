# AlgoZenith --- Stack with Lazy Increments (STL)

> **Core idea:** Do not update the bottom `k` elements immediately.
> Store one **pending prefix increment at the boundary**, then carry it
> downward during `pop()`.

## TOC

-   [1. Problem](#1-problem)
-   [2. Why Naive Is Slow](#2-why-naive-is-slow)
-   [3. Core Design](#3-core-design)
-   [4. STL Used](#4-stl-used)
-   [5. push(x)](#5-pushx)
-   [6. inc(k,val)](#6-inckval)
-   [7. pop()](#7-pop)
-   [8. Dry Run](#8-dry-run)
-   [9. Complete C++](#9-complete-c)
-   [10. Why It Works](#10-why-it-works)
-   [11. Complexity & Mistakes](#11-complexity--mistakes)
-   [12. Mental Model](#12-mental-model)

------------------------------------------------------------------------

# 1. Problem

A stack has maximum capacity `maxSize`.

``` text
push(x)      → push x if not full
pop()        → remove/return top; -1 if empty
inc(k,val)   → add val to bottom min(k,size) elements
```

Target:

``` text
push / pop / inc → O(1)
```

------------------------------------------------------------------------

# 2. Why Naive Is Slow

``` text
stack = [1,2,3,4,5]
inc(4,10)
```

Naive:

``` text
[11,12,13,14,5]
```

It touches `4` elements:

``` text
O(k)
```

Better:

``` text
record the increment now
        ↓
apply/propagate it when values are popped
```

This works because removal is LIFO.

------------------------------------------------------------------------

# 3. Core Design

Maintain two synchronized vectors:

``` cpp
vector<long long> values;
vector<long long> lazy;
```

Meaning:

``` text
values[i] = original/base value

lazy[i]   = pending +value
            for prefix [0..i]
```

Example:

``` text
values = [1,2,3]
lazy   = [0,5,0]
```

`lazy[1] = 5` means:

``` text
+5 applies to indices 0..1
```

Logical values:

``` text
[6,7,3]
```

For `inc(k,val)`:

``` text
boundary = min(k,size)-1

lazy[boundary] += val
```

Only one position changes.

------------------------------------------------------------------------

# 4. STL Used

  STL / Method          Purpose                                      Time
  --------------------- -------------------------- ----------------------
  `vector<long long>`   values + pending markers                      ---
  `reserve(n)`          reserve maximum capacity                    setup
  `push_back(x)`        add top                      `O(1)` after reserve
  `pop_back()`          remove top                                 `O(1)`
  `size()`              current size                               `O(1)`
  `empty()`             empty check                                `O(1)`

``` cpp
values.reserve(maxSize);
lazy.reserve(maxSize);
```

------------------------------------------------------------------------

# 5. push(x)

If full:

``` text
do nothing
```

Otherwise:

``` cpp
values.push_back(x);
lazy.push_back(0);
```

Why zero?

A newly pushed value must **not inherit old increments**.

Example:

``` text
before:

values = [1,2]
lazy   = [0,5]

push(3)

values = [1,2,3]
lazy   = [0,5,0]
```

------------------------------------------------------------------------

# 6. inc(k,val)

Example:

``` text
values = [1,2,3,4]

inc(3,10)
```

Affected indices:

``` text
0,1,2
```

Highest affected index:

``` text
boundary
= min(3,4)-1
= 2
```

Store only:

``` cpp
lazy[2] += 10;
```

State:

``` text
values = [1,2,3,4]
lazy   = [0,0,10,0]
```

Meaning:

``` text
+10 applies to prefix [0..2]
```

If:

``` text
k > size
```

clamp it:

``` text
boundary = size-1
```

------------------------------------------------------------------------

# 7. pop()

Let:

``` text
t = size-1
```

Actual top:

``` text
answer = values[t] + lazy[t]
```

Before deleting the top marker, transfer it downward:

``` cpp
lazy[t-1] += lazy[t];
```

Why?

``` text
lazy[t]
```

also belongs to every lower element.

Then:

``` cpp
values.pop_back();
lazy.pop_back();
```

So:

``` text
POP

base + top marker
       ↓
return answer

top marker
       ↓
add to lower marker
       ↓
remove top
```

------------------------------------------------------------------------

# 8. Dry Run

Use:

``` text
maxSize = 3
```

### Push `1,2,3`

``` text
values = [1,2,3]
lazy   = [0,0,0]
```

### `inc(2,10)`

``` text
boundary
= min(2,3)-1
= 1
```

``` text
values = [1, 2, 3]
lazy   = [0,10, 0]
```

Logical:

``` text
[11,12,3]
```

### `inc(3,5)`

``` text
boundary = 2

values = [1, 2, 3]
lazy   = [0,10,5]
```

Logical:

``` text
[16,17,8]
```

### `pop()`

``` text
answer
= values[2] + lazy[2]
= 3 + 5
= 8
```

Transfer:

``` text
lazy[1]
= 10 + 5
= 15
```

Remove top:

``` text
values = [1,2]
lazy   = [0,15]
```

Return:

``` text
8
```

### `push(4)`

New push gets zero marker:

``` text
values = [1,2,4]
lazy   = [0,15,0]
```

`4` does not inherit old increments.

### `pop()`

``` text
4 + 0 = 4
```

State:

``` text
values = [1,2]
lazy   = [0,15]
```

### `pop()`

``` text
2 + 15 = 17
```

Transfer:

``` text
lazy[0] += 15
```

State:

``` text
values = [1]
lazy   = [15]
```

### `pop()`

``` text
1 + 15 = 16
```

Returned sequence:

``` text
8, 4, 17, 16
```

------------------------------------------------------------------------

# 9. Complete C++

``` cpp
#include <bits/stdc++.h>
using namespace std;

class CustomStack {
    vector<long long> values;
    vector<long long> lazy;
    size_t maxSize;

public:
    CustomStack(int capacity)
        : maxSize(capacity > 0
                    ? (size_t)capacity
                    : 0) {

        values.reserve(maxSize);
        lazy.reserve(maxSize);
    }

    void push(long long x) {
        if (values.size() == maxSize)
            return;

        values.push_back(x);

        // New value inherits no old increment.
        lazy.push_back(0);
    }

    long long pop() {
        if (values.empty())
            return -1;

        size_t top = values.size() - 1;

        long long ans =
            values[top] + lazy[top];

        // Preserve increment for lower prefix.
        if (top > 0)
            lazy[top - 1] += lazy[top];

        values.pop_back();
        lazy.pop_back();

        return ans;
    }

    void inc(int k, long long val) {
        if (values.empty() || k <= 0)
            return;

        size_t count =
            min(values.size(), (size_t)k);

        size_t boundary = count - 1;

        lazy[boundary] += val;
    }
};
```

------------------------------------------------------------------------

# 10. Why It Works

A marker:

``` text
lazy[i] = x
```

means:

``` text
+x applies to indices 0..i
```

When `i` becomes top:

``` text
actual value
= values[i] + lazy[i]
```

After removing `i`, the increment still belongs to:

``` text
0..i-1
```

Therefore:

``` text
lazy[i-1] += lazy[i]
```

Example:

``` text
lazy = [0,10,5]
```

Pop the top:

``` text
5 must survive for lower elements

10 + 5
   ↓
  15
```

New state:

``` text
lazy = [0,15]
```

Overlapping increments are combined without scanning.

------------------------------------------------------------------------

# 11. Complexity & Mistakes

  Operation             Time
  ----------- --------------
  `push`              `O(1)`
  `pop`               `O(1)`
  `inc`               `O(1)`
  Space         `O(maxSize)`

Core optimization:

``` text
Naive inc → update k elements → O(k)

Lazy inc  → update boundary   → O(1)
```

Common mistakes:

``` text
loop through bottom k values              ✗
use k-1 without min(k,size)                ✗
forget k <= 0                              ✗
delete lazy[top] without propagation       ✗
lazy[top-1] = lazy[top]                    ✗
new push inherits an old marker            ✗
use int when increments can accumulate     ✗
```

Correct propagation:

``` cpp
lazy[top - 1] += lazy[top];
```

not assignment.

------------------------------------------------------------------------

# 12. Mental Model

Think:

``` text
lazy[i]
=
"this increment belongs to everyone
 from index i down to the bottom"
```

Example:

``` text
TOP

value 3
lazy +5      ─┐
              │ applies downward
value 2      │
lazy +10     ─┤
              │
value 1      │
lazy  0      │

BOTTOM       ↓
```

Pop `3`:

``` text
answer = 3+5
```

Before deleting its marker:

``` text
10 + 5 = 15
```

Now:

``` text
TOP

value 2
lazy +15

value 1
lazy  0

BOTTOM
```

Remember only:

``` text
INC
boundary = min(k,size)-1
lazy[boundary] += val

POP
ans = values[top] + lazy[top]
lazy[top-1] += lazy[top]
remove top
```

> **One sentence:** **Store the prefix increment at its highest affected
> index; when that boundary is popped, carry the pending increment
> downward.**
