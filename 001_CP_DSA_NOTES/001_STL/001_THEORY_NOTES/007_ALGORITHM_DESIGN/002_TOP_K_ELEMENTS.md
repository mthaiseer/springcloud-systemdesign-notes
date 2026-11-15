# AlgoZenith — Top K Elements
## Visual Self-Learning Version

> **Core idea:** Maintain the current **Top K winners** and their sum instead of sorting every value after every update.

# Table of Contents

- [0. Prerequisites](#0-prerequisites)
- [1. Core Model](#1-core-model)
- [2. Insert Only — Min Heap](#2-insert-only--min-heap)
- [3. Dynamic Top K — Two Multisets](#3-dynamic-top-k--two-multisets)
- [4. Invariants](#4-invariants)
- [5. Insert — Step by Step](#5-insert--step-by-step)
- [6. Remove — Step by Step](#6-remove--step-by-step)
- [7. Full Dry Run](#7-full-dry-run)
- [8. C++17 Templates](#8-c17-templates)
- [9. Duplicates](#9-duplicates)
- [10. Complexity](#10-complexity)
- [11. Common Mistakes](#11-common-mistakes)
- [12. Recognition Checklist](#12-recognition-checklist)
- [13. Final Mental Model](#13-final-mental-model)

---

# 0. Prerequisites

## 0.1 What does Top K mean?

```text
values = [5,1,8,4,9,2]
K = 3
```

Sorted descending:

```text
9 8 5 4 2 1
```

Top 3:

```text
9 8 5
```

Top K sum:

```text
9 + 8 + 5 = 22
```

Duplicates count separately:

```text
values = [8,8,5,3]
K = 3

Top K = [8,8,5]
```

---

## 0.2 Min Heap

For largest K values, we want quick access to the **smallest value among the winners**.

```text
Top K = {4,5,8}

weakest winner = 4
```

A min heap gives exactly that:

```cpp
priority_queue<int, vector<int>, greater<int>> pq;
```

```text
pq.top() = weakest winner
```

---

## 0.3 Multiset

A `multiset` is:

```text
sorted
+
duplicates allowed
```

Example:

```text
{1,4,4,8}
```

Useful operations:

```cpp
*s.begin();             // smallest
*prev(s.end());         // largest
s.find(x);              // one occurrence
s.erase(iterator);      // erase one occurrence
```

---

## 0.4 Cached Sum

If we repeatedly ask:

```text
sum of Top K
```

do not recompute the sum every time.

Maintain:

```text
sumK
```

Rule:

```text
value enters top
-> sumK += value

value leaves top
-> sumK -= value
```

Then:

```text
TopKSum() = O(1)
```

---

# 1. Core Model

Operations:

```text
Insert(x)
Remove(x)       // optional
TopKSum()
```

Maintain:

```text
sumK
=
sum of largest min(K,N) occurrences
```

Two cases:

```text
INSERT ONLY
    |
    v
min heap of size K


INSERT + ARBITRARY REMOVE
    |
    v
two multisets:
rest + top
```

Why two designs?

```text
heap:
great at removing weakest winner

but:
bad at arbitrary Remove(x)
```

So dynamic removal uses multisets.

---

## 1.1 Winner Boundary

Imagine all values sorted:

```text
1 2 4 | 5 8 9
      ^
      |
   boundary
```

For `K = 3`:

```text
rest             top
------------|-------------
1 2 4       | 5 8 9
losers      | winners
```

The whole problem is:

```text
maintain this boundary after every update
```

---

# 2. Insert Only — Min Heap

## 2.1 Invariant

Keep only:

```text
largest min(K,N) values seen so far
```

and:

```text
sumK = sum(heap)
```

---

## 2.2 Why min heap?

Suppose:

```text
Top K = {4,5,8}
```

New value:

```text
7
```

Temporary winners:

```text
{4,5,7,8}
```

One must leave.

Which one?

```text
4
```

So we need the smallest winner quickly.

That is:

```text
min heap top
```

---

## 2.3 Step-by-Step Insert

```text
Insert(x)

Step 1:
push x

Step 2:
sumK += x

Step 3:
if size > K
    weakest = pq.top()

Step 4:
sumK -= weakest

Step 5:
pop weakest
```

---

## 2.4 ASCII Dry Run

```text
K = 3

insert:
5, 1, 8, 4
```

### Insert 5

```text
heap = {5}
sumK = 5
```

### Insert 1

```text
heap = {1,5}
sumK = 6
```

### Insert 8

```text
heap = {1,5,8}
sumK = 14
```

### Insert 4

Temporary:

```text
{1,4,5,8}
sumK = 18
```

Too many winners:

```text
size = 4 > K
```

Weakest winner:

```text
1
```

Remove it:

```text
Top K = {4,5,8}
sumK  = 17
```

Visual:

```text
temporary winners:

1   4   5   8
^
|
weakest

remove 1

4   5   8
```

---

## 2.5 C++17

```cpp
struct TopK {
    int k;
    long long sumK = 0;

    priority_queue<
        int,
        vector<int>,
        greater<int>
    > pq;

    TopK(int k) : k(k) {}

    void insert(int x) {
        pq.push(x);
        sumK += x;

        if ((int)pq.size() > k) {
            sumK -= pq.top();
            pq.pop();
        }
    }

    long long getSum() const {
        return sumK;
    }
};
```

Recognition:

```text
insert only
+
largest K
+
Top K sum
```

Think:

```text
min heap of size K
```

---

# 3. Dynamic Top K — Two Multisets

For:

```text
Insert(x)
Remove(x)
TopKSum()
```

maintain:

```cpp
multiset<int> top;
multiset<int> rest;
long long sumK = 0;
```

Meaning:

```text
top
=
largest K occurrences

rest
=
all remaining occurrences
```

Visual:

```text
rest                         top
----------------------|------------------
non-winners           | largest K winners
                      ^
                      |
                   boundary
```

---

# 4. Invariants

Always preserve:

```text
1. top.size() = min(K,N)

2. max(rest) <= min(top)

3. sumK = sum(top)
```

---

## 4.1 Boundary Values

```text
top.begin()
=
weakest winner
=
smallest element in top
```

```text
prev(rest.end())
=
strongest loser
=
largest element in rest
```

ASCII:

```text
rest                    top

1  2  4              5  8  9
      ^              ^
      |              |
strongest loser   weakest winner
```

Correct boundary:

```text
4 <= 5
```

---

## 4.2 Movement Rules

If top too large:

```text
weakest winner
top.begin()
      |
      v
     rest
```

If top too small:

```text
strongest loser
prev(rest.end())
      |
      v
      top
```

Every crossing updates `sumK`.

---

# 5. Insert — Step by Step

Use a simple strategy:

```text
optimistically insert x into top
```

Then repair if top is too large.

---

## 5.1 Algorithm

```text
Step 1:
top.insert(x)

Step 2:
sumK += x

Step 3:
if top.size() > K
    weakest = top.begin()

Step 4:
move weakest to rest

Step 5:
sumK -= weakest
```

---

## 5.2 Example — Insert a Strong Value

Before:

```text
K = 3

rest = {1}
top  = {4,5,8}
sumK = 17
```

Insert:

```text
6
```

Temporary:

```text
top = {4,5,6,8}
sumK = 23
```

Weakest:

```text
4
```

Demote:

```text
rest = {1,4}
top  = {5,6,8}
sumK = 19
```

Check:

```text
5 + 6 + 8 = 19
```

---

## 5.3 Example — Insert a Weak Value

Before:

```text
rest = {1}
top  = {4,5,8}
```

Insert:

```text
2
```

Temporary:

```text
top = {2,4,5,8}
```

Weakest:

```text
2
```

Demote immediately.

Final:

```text
rest = {1,2}
top  = {4,5,8}
```

Top K unchanged.

---

## 5.4 C++17 Insert

```cpp
void insert(int x) {
    top.insert(x);
    sumK += x;

    if ((int)top.size() > k) {
        auto it = top.begin();

        sumK -= *it;
        rest.insert(*it);
        top.erase(it);
    }
}
```

---

# 6. Remove — Step by Step

There are two cases.

---

## 6.1 Case A — x is in rest

Example:

```text
rest = {1,2}
top  = {5,6,8}
```

Remove:

```text
2
```

Result:

```text
rest = {1}
top  = {5,6,8}
```

Top K and `sumK` do not change.

---

## 6.2 Case B — x is in top

Example:

```text
rest = {1,4}
top  = {5,8,9}
```

Remove:

```text
8
```

After erase:

```text
rest = {1,4}
top  = {5,9}
```

Top is short.

Promote strongest loser:

```text
4
```

Final:

```text
rest = {1}
top  = {4,5,9}
```

---

## 6.3 Algorithm

```text
Step 1:
search x in top

Step 2:
if found:
    erase one occurrence
    sumK -= x

Step 3:
otherwise search x in rest

Step 4:
if not found:
    return false

Step 5:
if top.size() < K and rest not empty:
    promote largest(rest)

Step 6:
sumK += promoted value

Step 7:
return true
```

---

## 6.4 ASCII Promotion

```text
before:

rest                    top
1   4                 5   9
    ^
    |
strongest loser

top is short
      |
      v

promote 4

after:

rest                    top
1                     4 5 9
```

---

## 6.5 C++17 Remove

```cpp
bool remove(int x) {
    auto it = top.find(x);

    if (it != top.end()) {
        sumK -= x;
        top.erase(it);
    } else {
        it = rest.find(x);

        if (it == rest.end()) {
            return false;
        }

        rest.erase(it);
    }

    if ((int)top.size() < k && !rest.empty()) {
        auto best = prev(rest.end());

        sumK += *best;
        top.insert(*best);
        rest.erase(best);
    }

    return true;
}
```

---

# 7. Full Dry Run

```text
K = 3
```

Start:

```text
top  = {}
rest = {}
sumK = 0
```

### Insert 5

```text
top  = {5}
rest = {}
sumK = 5
```

### Insert 1

```text
top  = {1,5}
rest = {}
sumK = 6
```

### Insert 8

```text
top  = {1,5,8}
rest = {}
sumK = 14
```

### Insert 4

Temporary:

```text
top = {1,4,5,8}
sumK = 18
```

Demote weakest:

```text
1
```

Result:

```text
top  = {4,5,8}
rest = {1}
sumK = 17
```

### Remove 8

Remove from top:

```text
top  = {4,5}
rest = {1}
sumK = 9
```

Promote strongest loser:

```text
1
```

Final:

```text
top  = {1,4,5}
rest = {}
sumK = 10
```

Check:

```text
1 + 4 + 5 = 10
```

---

## 7.1 State Table

| Step | Operation | `rest` | `top` | `sumK` |
|---:|---|---|---|---:|
| 0 | Start | `{}` | `{}` | 0 |
| 1 | `Insert(5)` | `{}` | `{5}` | 5 |
| 2 | `Insert(1)` | `{}` | `{1,5}` | 6 |
| 3 | `Insert(8)` | `{}` | `{1,5,8}` | 14 |
| 4 | `Insert(4)` | `{1}` | `{4,5,8}` | 17 |
| 5 | `Remove(8)` | `{}` | `{1,4,5}` | 10 |

---

# 8. C++17 Templates

## 8.1 Insert Only

```cpp
#include <bits/stdc++.h>
using namespace std;

struct TopK {
    int k;
    long long sumK = 0;

    priority_queue<
        int,
        vector<int>,
        greater<int>
    > pq;

    TopK(int k) : k(k) {}

    void insert(int x) {
        pq.push(x);
        sumK += x;

        if ((int)pq.size() > k) {
            sumK -= pq.top();
            pq.pop();
        }
    }

    long long getSum() const {
        return sumK;
    }
};
```

---

## 8.2 Insert + Remove

```cpp
#include <bits/stdc++.h>
using namespace std;

struct DynamicTopK {
    int k;
    long long sumK = 0;

    multiset<int> top;
    multiset<int> rest;

    DynamicTopK(int k) : k(k) {}

    void insert(int x) {
        top.insert(x);
        sumK += x;

        if ((int)top.size() > k) {
            auto it = top.begin();

            sumK -= *it;
            rest.insert(*it);
            top.erase(it);
        }
    }

    bool remove(int x) {
        auto it = top.find(x);

        if (it != top.end()) {
            sumK -= x;
            top.erase(it);
        } else {
            it = rest.find(x);

            if (it == rest.end()) {
                return false;
            }

            rest.erase(it);
        }

        if ((int)top.size() < k && !rest.empty()) {
            auto best = prev(rest.end());

            sumK += *best;
            top.insert(*best);
            rest.erase(best);
        }

        return true;
    }

    long long getSum() const {
        return sumK;
    }
};
```

---

# 9. Duplicates

Use:

```cpp
multiset<int>
```

because duplicates count separately.

Example:

```text
values = [8,8,5,3]
K = 3

top = {5,8,8}
```

To remove **one occurrence**:

```cpp
auto it = top.find(x);

if (it != top.end()) {
    top.erase(it);
}
```

Avoid:

```cpp
top.erase(x);
```

because that removes all equal occurrences.

ASCII:

```text
top:
5 8 8

erase one iterator to 8:

5 8
```

---

# 10. Complexity

| Design | Insert | Remove | Query | Space |
|---|---:|---:|---:|---:|
| Min heap | `O(log K)` | — | `O(1)` | `O(K)` |
| Two multisets | `O(log N)` | `O(log N)` | `O(1)` | `O(N)` |

Why:

```text
min heap contains at most K values
-> push/pop = O(log K)

multiset is ordered tree
-> insert/find/erase = O(log N)
```

---

# 11. Common Mistakes

## 11.1 Using a max heap

For largest K values, we need:

```text
smallest winner
```

quickly.

So use:

```text
min heap
```

---

## 11.2 Forgetting sumK updates

Always:

```text
enters top
-> sumK += value

leaves top
-> sumK -= value
```

---

## 11.3 Removing all duplicates

Bad:

```cpp
top.erase(x);
```

Correct:

```cpp
top.erase(top.find(x));
```

after checking the iterator.

---

## 11.4 Forgetting promotion

If a winner is removed:

```text
top may become too small
```

Then:

```text
largest(rest)
must move to top
```

---

## 11.5 Wrong boundary element

Remember:

```text
top.begin()
=
weakest winner
```

```text
prev(rest.end())
=
strongest loser
```

---

## 11.6 Assuming top always has exactly K elements

Correct invariant:

```text
top.size()
=
min(K,N)
```

If:

```text
N < K
```

all active values belong to top.

---

# 12. Recognition Checklist

When you see a Top K problem, ask:

```text
1. Need largest K or smallest K?

2. Insert only?
   -> heap may be enough

3. Arbitrary remove?
   -> two multisets

4. What is the weakest winner?

5. What is the strongest loser?

6. Are duplicates allowed?

7. Is Top K sum queried often?
   -> cache sumK

8. What if top is too large?
   -> demote weakest winner

9. What if top is too small?
   -> promote strongest loser

10. Is the boundary valid?
    max(rest) <= min(top)
```

---

# 13. Final Mental Model

```text
ALL ACTIVE VALUES
        |
        v
split at winner boundary
        |
        +--------------------------+
        |                          |
        v                          v
      REST                        TOP
     losers                    largest K

small ... large            weak ... strong
```

Boundary:

```text
max(rest)
<=
min(top)
```

Repair:

```text
top too large
    |
    v
weakest winner
    |
    v
rest


top too small
    |
    v
strongest loser
    |
    v
top
```

Cached sum:

```text
value enters top
-> sumK += value

value leaves top
-> sumK -= value
```

The one sentence to remember:

```text
Top K
=
maintain the winner boundary
+
cache the winner sum.
```
