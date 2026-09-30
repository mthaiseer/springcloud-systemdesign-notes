# AlgoZenith --- Min Stack (STL)

> **Core idea:** `push()` can calculate a new minimum easily. The
> challenge is `pop()` --- after removing the current minimum, restore
> the **previous minimum in O(1)**.

## TOC

-   [1. Problem](#1-problem)
-   [2. Why One Minimum Fails](#2-why-one-minimum-fails)
-   [3. Approach 1 --- Pair Snapshot](#3-approach-1--pair-snapshot)
-   [4. Pair Dry Run](#4-pair-dry-run)
-   [5. Pair C++](#5-pair-c)
-   [6. Approach 2 --- Two Stacks](#6-approach-2--two-stacks)
-   [7. Two-Stack Dry Run](#7-two-stack-dry-run)
-   [8. Approach 3 --- Arithmetic
    Encoding](#8-approach-3--arithmetic-encoding)
-   [9. Encoding Algebra](#9-encoding-algebra)
-   [10. Encoding Dry Run](#10-encoding-dry-run)
-   [11. Encoding C++](#11-encoding-c)
-   [12. Complexity & Mistakes](#12-complexity--mistakes)
-   [13. Mental Model](#13-mental-model)

------------------------------------------------------------------------

# 1. Problem

Support:

``` text
push(x)
pop()
top()
getMin()
```

Target:

``` text
push / pop / top / getMin → O(1)
```

Main question:

``` text
push smaller value → minimum changes
pop that value     → how do we restore old minimum?
```

------------------------------------------------------------------------

# 2. Why One Minimum Fails

``` text
stack = [5,7,3]
min   = 3
```

Pop `3`:

``` text
stack = [5,7]
```

New minimum is `5`, but a single `min = 3` variable has forgotten it.

So:

> **A pop returns us to an earlier stack depth. Save enough state to
> restore the answer for that depth.**

------------------------------------------------------------------------

# 3. Approach 1 --- Pair Snapshot

Store:

``` text
{actual value, minimum up to this depth}
```

``` cpp
vector<pair<int,int>> st;
```

Push:

``` text
newMin = min(x, previousMin)
store {x,newMin}
```

Example:

``` text
push 5 → {5,5}
push 7 → {7,5}
push 3 → {3,3}
push 8 → {8,3}
```

Stack:

``` text
top    {8,3}
       {3,3}
       {7,5}
bottom {5,5}
```

Queries:

``` text
top()    = st.back().first
getMin() = st.back().second
```

Pop:

``` text
remove top pair
→ previous minimum snapshot is automatically exposed
```

------------------------------------------------------------------------

# 4. Pair Dry Run

Push `5,7,3,8`.

``` text
push(5)
newMin = 5
stack  = {5,5}

push(7)
newMin = min(7,5) = 5
stack  = {5,5} {7,5}

push(3)
newMin = min(3,5) = 3
stack  = {5,5} {7,5} {3,3}

push(8)
newMin = min(8,3) = 3
stack  = {5,5} {7,5} {3,3} {8,3}
```

Now:

``` text
top = 8
min = 3
```

Pop `8`:

``` text
{5,5} {7,5} {3,3}
min = 3
```

Pop `3`:

``` text
{5,5} {7,5}
min = 5
```

No scan. The previous snapshot becomes current.

------------------------------------------------------------------------

# 5. Pair C++

``` cpp
#include <bits/stdc++.h>
using namespace std;

class MinStack {
    // {value, minimum up to this depth}
    vector<pair<int,int>> st;

public:
    void push(int x) {
        int mn = st.empty()
               ? x
               : min(x, st.back().second);

        st.push_back({x, mn});
    }

    void pop() {
        if (!st.empty())
            st.pop_back();
    }

    int top() const {
        return st.empty() ? -1
                          : st.back().first;
    }

    int getMin() const {
        return st.empty() ? -1
                          : st.back().second;
    }
};
```

------------------------------------------------------------------------

# 6. Approach 2 --- Two Stacks

Maintain:

``` text
st       → all values
minStack → minimum history
```

Push into `minStack` when:

``` text
minStack empty
OR
x <= minStack.top()
```

Why `<=`?

Duplicate minima must be remembered.

Example:

``` text
st       = [3,3]
minStack = [3,3]
```

After one `3` is popped:

``` text
st       = [3]
minStack = [3]
```

Minimum is still `3`.

C++:

``` cpp
class MinStack {
    stack<int> st, minStack;

public:
    void push(int x) {
        st.push(x);

        if (minStack.empty() ||
            x <= minStack.top())
            minStack.push(x);
    }

    void pop() {
        if (st.empty()) return;

        if (st.top() == minStack.top())
            minStack.pop();

        st.pop();
    }

    int top() const {
        return st.empty() ? -1 : st.top();
    }

    int getMin() const {
        return minStack.empty()
             ? -1
             : minStack.top();
    }
};
```

------------------------------------------------------------------------

# 7. Two-Stack Dry Run

Push `5,3,4,3`.

``` text
push 5
st       = [5]
minStack = [5]

push 3
3 <= 5
st       = [5,3]
minStack = [5,3]

push 4
4 > 3
st       = [5,3,4]
minStack = [5,3]

push 3
3 <= 3
st       = [5,3,4,3]
minStack = [5,3,3]
```

Pop:

``` text
top == minStack.top == 3
→ pop both

st       = [5,3,4]
minStack = [5,3]

getMin() = 3
```

------------------------------------------------------------------------

# 8. Approach 3 --- Arithmetic Encoding

Maintain:

``` text
stack<long long> st
long long minEle
```

If:

``` text
x >= minEle
```

push normally.

If:

``` text
x < minEle
```

store a marker:

``` text
encoded = 2*x - oldMin
```

then:

``` text
minEle = x
```

Why can we detect the marker?

Since:

``` text
x < oldMin
```

then:

``` text
2x - oldMin < x
```

So:

``` text
encoded < new minEle
```

Therefore:

``` text
st.top() < minEle
→ encoded marker
```

------------------------------------------------------------------------

# 9. Encoding Algebra

When a new minimum `x` arrives:

``` text
encoded = 2*x - oldMin
```

Later, during pop, we know:

``` text
encoded
current minEle = x
```

Recover `oldMin`.

Start:

``` text
encoded = 2*x - oldMin
```

Move terms:

``` text
oldMin = 2*x - encoded
```

Since:

``` text
x = current minEle
```

final restoration formula:

``` text
┌──────────────────────────────┐
│ oldMin = 2*minEle - encoded  │
└──────────────────────────────┘
```

That is the entire trick.

------------------------------------------------------------------------

# 10. Encoding Dry Run

Push `5,3,7`.

### `push(5)`

``` text
st     = [5]
minEle = 5
```

### `push(3)`

`3 < 5`, so encode:

``` text
encoded
= 2*3 - 5
= 1
```

Store:

``` text
st     = [5,1]
minEle = 3
```

Notice:

``` text
1 < 3
```

so `1` is recognizable as encoded.

### `push(7)`

``` text
7 >= 3
→ push normally

st     = [5,1,7]
minEle = 3
```

### `pop()` → `7`

Normal value:

``` text
st     = [5,1]
minEle = 3
```

### `top()`

Stored top:

``` text
1 < minEle
```

So it is encoded.

Actual top is:

``` text
minEle = 3
```

### `pop()` → logical value `3`

Restore old minimum:

``` text
oldMin
= 2*minEle - encoded

= 2*3 - 1
= 5
```

Final:

``` text
st     = [5]
minEle = 5
```

------------------------------------------------------------------------

# 11. Encoding C++

``` cpp
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
        }
        else {
            long long encoded =
                2LL * x - minEle;

            st.push(encoded);
            minEle = x;
        }
    }

    void pop() {
        if (st.empty()) return;

        long long y = st.top();
        st.pop();

        if (y < minEle)
            minEle = 2LL * minEle - y;
    }

    long long top() const {
        if (st.empty()) return -1;

        long long y = st.top();

        return (y < minEle)
             ? minEle
             : y;
    }

    long long getMin() const {
        return st.empty() ? -1 : minEle;
    }
};
```

Use a sufficiently wide type because:

``` text
2*x - minEle
```

may overflow the original integer type.

------------------------------------------------------------------------

# 12. Complexity & Mistakes

  ------------------------------------------------------------------------------
  Approach             Push          Pop          Top          Min Extra Minimum
                                                                           State
  ------------ ------------ ------------ ------------ ------------ -------------
  Pair            amortized       `O(1)`       `O(1)`       `O(1)`        `O(N)`
  snapshot           `O(1)`                                        
  (`vector`)                                                       

  Two stacks         `O(1)`       `O(1)`       `O(1)`       `O(1)`    worst-case
                                                                          `O(N)`

  Encoding           `O(1)`       `O(1)`       `O(1)`       `O(1)`        `O(1)`
                                                                     bookkeeping
  ------------------------------------------------------------------------------

Common mistakes:

``` text
store only current minimum                   ✗
recalculate minimum after pop                ✗
use < instead of <= in two-stack approach    ✗
treat encoded marker as real top             ✗
forget oldMin = 2*minEle - encoded           ✗
ignore overflow in 2*x - minEle              ✗
```

------------------------------------------------------------------------

# 13. Mental Model

### Pair Snapshot

``` text
push x
  ↓
newMin = min(x, oldMin)
  ↓
store {x,newMin}

pop
 ↓
remove pair
 ↓
old snapshot returns automatically
```

### Two Stacks

``` text
actual values      minimum history

     st    ←sync→    minStack

x <= current min
→ record x in both
```

### Encoding

``` text
x >= min
→ push x

x < min
→ encoded = 2*x - oldMin
→ push encoded
→ min = x
```

Pop encoded:

``` text
encoded < min
      ↓
actual top = min
      ↓
oldMin = 2*min - encoded
```

> **Remember:** **Min Stack is a state-restoration problem: a pop must
> expose or reconstruct the minimum that was valid before the popped
> value arrived.**
