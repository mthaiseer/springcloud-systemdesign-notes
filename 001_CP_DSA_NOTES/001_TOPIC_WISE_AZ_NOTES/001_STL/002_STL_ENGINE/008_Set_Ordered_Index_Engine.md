# 008_Set_Ordered_Index_Engine.md

> MiniSTLEngine Phase 008  
> Topic: `set` as an **Ordered Index / Unique Sorted Data Engine** for CP, DSA, FAANG interviews, and real-system thinking.

---

# Clickable Index

- [1. Goal](#1-goal)
- [2. Why Set Is An Engine](#2-why-set-is-an-engine)
- [3. Real-System Mental Model](#3-real-system-mental-model)
- [4. Set Core Behavior](#4-set-core-behavior)
- [5. Set Operations Cheat Sheet](#5-set-operations-cheat-sheet)
- [6. CP/DSA Recognition](#6-cpdsa-recognition)
- [7. Engine Architecture](#7-engine-architecture)
- [8. Basic Ordered Index Engine](#8-basic-ordered-index-engine)
- [9. Dry Run: Insert, Find, Erase](#9-dry-run-insert-find-erase)
- [10. CP Pattern 1: Unique Sorted Values](#10-cp-pattern-1-unique-sorted-values)
- [11. CP Pattern 2: Lower Bound / Ceiling Query](#11-cp-pattern-2-lower-bound--ceiling-query)
- [12. CP Pattern 3: Floor Query](#12-cp-pattern-3-floor-query)
- [13. CP Pattern 4: Dynamic Minimum Difference](#13-cp-pattern-4-dynamic-minimum-difference)
- [14. CP Pattern 5: Online Interval Start Index](#14-cp-pattern-5-online-interval-start-index)
- [15. CP Pattern 6: Remove While Iterating](#15-cp-pattern-6-remove-while-iterating)
- [16. Set vs Unordered Set vs Multiset](#16-set-vs-unordered-set-vs-multiset)
- [17. Common Mistakes](#17-common-mistakes)
- [18. Complexity Table](#18-complexity-table)
- [19. Real-World Mapping](#19-real-world-mapping)
- [20. Final Mental Model](#20-final-mental-model)
- [21. Next Step](#21-next-step)

---

# 1. Goal

Learn `set` not only as sorted unique container, but as an:

```text
Ordered Index Engine
```

It helps solve:

```text
unique sorted values
dynamic lookup
ceiling/floor query
nearest value query
online interval checks
ordered active set
dynamic index problems
```

---

# 2. Why Set Is An Engine

A `set` keeps data:

```text
unique
sorted
dynamic
searchable
```

Normal thinking:

```cpp
set<int> s;
```

Engine thinking:

```text
OrderedIndexEngine
    stores unique keys
    keeps keys sorted
    supports insert/delete/search in O(log N)
    supports lower_bound / upper_bound queries
```

This is like a small database index.

---

# 3. Real-System Mental Model

Real systems need ordered indexes:

```text
database B-tree index
calendar sorted events
range index
leaderboard unique scores
active sessions by timestamp
ordered user IDs
interval starts
```

Architecture:

```text
Incoming Key
    |
    v
OrderedIndexEngine
    |
    +--> insert key
    +--> delete key
    +--> check existence
    +--> find first key >= x
    +--> find last key <= x
    |
    v
Ordered Query Result
```

---

# 4. Set Core Behavior

Example:

```cpp
set<int> s;
s.insert(5);
s.insert(1);
s.insert(3);
s.insert(5);
```

Stored result:

```text
1 3 5
```

Important:

```text
duplicate 5 is ignored
data remains sorted
```

---

# 5. Set Operations Cheat Sheet

```cpp
set<int> s;

s.insert(10);       // O(log N)
s.erase(10);        // O(log N)
s.find(10);         // O(log N)
s.count(10);        // O(log N)

s.begin();          // smallest
s.rbegin();         // largest

s.lower_bound(x);   // first >= x
s.upper_bound(x);   // first > x

s.empty();
s.size();
```

---

# 6. CP/DSA Recognition

Use `set` when problem says:

```text
unique sorted
dynamic insert/delete
find nearest greater
find nearest smaller
ceil/floor query
active ordered values
online queries
```

Hidden mapping:

| Problem clue | Set pattern |
|---|---|
| maintain sorted unique values | set |
| find first value >= x | lower_bound |
| find first value > x | upper_bound |
| find predecessor | prev(lower_bound/upper_bound) |
| dynamic insert/delete + query | set |
| need duplicates | multiset |
| only fast lookup, no order | unordered_set |

---

# 7. Engine Architecture

```text
MiniSetOrderedIndexEngine
├── insert key
├── delete key
├── exact lookup
├── ceiling query
├── floor query
├── predecessor/successor query
├── active ordered set
└── interval start index
```

---

# 8. Basic Ordered Index Engine

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class OrderedIndexEngine {
private:
    set<int> index;

public:

    void addKey(int key) {

        // WHY:
        // set automatically keeps keys unique and sorted.
        index.insert(key);
    }

    void removeKey(int key) {

        // Safe even if key does not exist.
        index.erase(key);
    }

    bool contains(int key) {

        // find() returns end() if key not found.
        return index.find(key) != index.end();
    }

    int smallest() {

        if (index.empty()) {
            throw runtime_error("Index is empty");
        }

        return *index.begin();
    }

    int largest() {

        if (index.empty()) {
            throw runtime_error("Index is empty");
        }

        return *index.rbegin();
    }

    void printIndex() {

        cout << "Ordered Index: ";

        for (int key : index) {
            cout << key << " ";
        }

        cout << endl;
    }
};

int main() {

    OrderedIndexEngine engine;

    engine.addKey(5);
    engine.addKey(1);
    engine.addKey(3);
    engine.addKey(5);

    engine.printIndex();

    cout << engine.contains(3) << endl;
    cout << engine.smallest() << endl;
    cout << engine.largest() << endl;

    return 0;
}
```

---

# 9. Dry Run: Insert, Find, Erase

Operations:

```text
insert 5
insert 1
insert 3
insert 5
```

Set:

```text
1 3 5
```

Why only one `5`?

```text
set stores unique keys
```

Find:

```text
find 3 -> found
find 9 -> not found
```

Erase:

```text
erase 3
set = 1 5
```

---

# 10. CP Pattern 1: Unique Sorted Values

## Problem Type

```text
Remove duplicates and output values in sorted order.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    vector<int> nums = {5, 1, 3, 5, 2, 1};

    set<int> uniqueSorted;

    for (int x : nums) {
        // Duplicates are automatically ignored.
        uniqueSorted.insert(x);
    }

    for (int x : uniqueSorted) {
        cout << x << " ";
    }

    return 0;
}
```

Output:

```text
1 2 3 5
```

---

# 11. CP Pattern 2: Lower Bound / Ceiling Query

## Problem Type

```text
Find smallest value >= x.
```

This is called:

```text
ceiling
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    set<int> s = {2, 5, 8, 13};

    int x = 6;

    auto it = s.lower_bound(x);

    if (it != s.end()) {
        cout << "Ceiling of "
             << x
             << " is "
             << *it
             << endl;
    } else {
        cout << "No value >= x\n";
    }

    return 0;
}
```

## Dry Run

```text
set = 2 5 8 13
x = 6

lower_bound(6) -> 8
```

---

# 12. CP Pattern 3: Floor Query

## Problem Type

```text
Find largest value <= x.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    set<int> s = {2, 5, 8, 13};

    int x = 6;

    auto it = s.upper_bound(x);

    if (it == s.begin()) {
        cout << "No floor exists\n";
    } else {
        --it;

        cout << "Floor of "
             << x
             << " is "
             << *it
             << endl;
    }

    return 0;
}
```

## Why `upper_bound`?

```text
upper_bound(x) gives first value > x
previous of that is <= x
```

Dry run:

```text
set = 2 5 8 13
x = 6

upper_bound(6) -> 8
previous -> 5
floor = 5
```

---

# 13. CP Pattern 4: Dynamic Minimum Difference

## Problem Type

```text
Insert numbers one by one.
After each insert, find nearest neighbor difference.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    vector<int> nums = {10, 3, 20, 15};

    set<int> s;

    int bestDiff = INT_MAX;

    for (int x : nums) {

        auto right = s.lower_bound(x);

        if (right != s.end()) {
            bestDiff = min(bestDiff, abs(*right - x));
        }

        if (right != s.begin()) {
            auto left = prev(right);
            bestDiff = min(bestDiff, abs(*left - x));
        }

        s.insert(x);

        cout << "after insert "
             << x
             << ", bestDiff = "
             << bestDiff
             << endl;
    }

    return 0;
}
```

## Key Insight

In sorted order, closest value to `x` must be:

```text
predecessor or successor
```

No need to scan all elements.

---

# 14. CP Pattern 5: Online Interval Start Index

## Problem Type

```text
Store interval starts and find next interval after x.
```

Useful for:

```text
range mapping
calendar
booking systems
coverage queries
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    set<int> starts = {2, 10, 20, 35};

    int query = 12;

    auto it = starts.lower_bound(query);

    if (it != starts.end()) {
        cout << "Next interval start >= "
             << query
             << " is "
             << *it
             << endl;
    } else {
        cout << "No next interval\n";
    }

    return 0;
}
```

Real-system mapping:

```text
calendar next event lookup
next scheduled task
next range start
```

---

# 15. CP Pattern 6: Remove While Iterating

## Problem Type

```text
Delete elements satisfying condition.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    set<int> s = {1, 2, 3, 4, 5, 6};

    for (auto it = s.begin(); it != s.end(); ) {

        if (*it % 2 == 0) {
            // erase returns iterator to next element.
            it = s.erase(it);
        } else {
            ++it;
        }
    }

    for (int x : s) {
        cout << x << " ";
    }

    return 0;
}
```

Output:

```text
1 3 5
```

---

# 16. Set vs Unordered Set vs Multiset

| Need | Use |
|---|---|
| sorted unique values | set |
| fast lookup only | unordered_set |
| duplicates + sorted | multiset |
| ceiling/floor query | set/multiset |
| order not needed | unordered_set |
| frequency count | map/unordered_map |

---

# 17. Common Mistakes

## Mistake 1: Expecting Duplicates

```cpp
set<int> s;
s.insert(5);
s.insert(5);
```

Stored:

```text
5
```

Need duplicates?

```cpp
multiset<int> ms;
```

---

## Mistake 2: Dereferencing `end()`

Wrong:

```cpp
auto it = s.lower_bound(x);
cout << *it;
```

Correct:

```cpp
if (it != s.end()) {
    cout << *it;
}
```

---

## Mistake 3: `prev(begin())`

Wrong:

```cpp
auto it = s.begin();
cout << *prev(it);
```

Correct:

```cpp
if (it != s.begin()) {
    cout << *prev(it);
}
```

---

## Mistake 4: Using Set For Frequency

`set` only stores existence.

For frequency use:

```cpp
map<int,int>
```

or:

```cpp
unordered_map<int,int>
```

---

# 18. Complexity Table

| Operation | Complexity |
|---|---:|
| insert | O(log N) |
| erase by key | O(log N) |
| erase by iterator | amortized O(1) plus tree adjustment |
| find | O(log N) |
| lower_bound | O(log N) |
| upper_bound | O(log N) |
| begin/rbegin | O(1) |
| iteration sorted order | O(N) |

---

# 19. Real-World Mapping

| Set Concept | Real-System Meaning |
|---|---|
| sorted unique keys | database index |
| lower_bound | next matching key lookup |
| floor query | previous event lookup |
| insert/delete | dynamic index update |
| begin/rbegin | min/max key query |
| active set | currently active sessions/events |
| interval starts | calendar/range index |
| ordered traversal | index scan |

---

# 20. Final Mental Model

Set is:

```text
dynamic ordered unique index
```

Best for:

```text
sorted uniqueness
dynamic lookup
ceiling/floor
predecessor/successor
active ordered values
online index queries
```

One-line CP rule:

```text
If you need sorted dynamic values with lower_bound, think set.
```

One-line system rule:

```text
Set behaves like a small in-memory ordered database index.
```

---

# 21. Next Step

Next file:

```text
009_Map_KeyValue_Index_Engine.md
```

Then:

```text
010_UnorderedMap_Hash_Index_Engine.md
011_Multiset_Median_Engine.md
012_BinarySearch_Index_Query_Engine.md
```
