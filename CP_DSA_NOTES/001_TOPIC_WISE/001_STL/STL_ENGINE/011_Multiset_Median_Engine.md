# 011_Multiset_Median_Engine.md

> MiniSTLEngine Phase 011  
> Topic: `multiset` as a **Duplicate-Aware Ordered Analytics / Median Engine** for CP, DSA, FAANG interviews, and real-system thinking.

---

# Clickable Index

- [1. Goal](#1-goal)
- [2. Why Multiset Is An Engine](#2-why-multiset-is-an-engine)
- [3. Real-System Mental Model](#3-real-system-mental-model)
- [4. Multiset Core Behavior](#4-multiset-core-behavior)
- [5. Multiset Operations Cheat Sheet](#5-multiset-operations-cheat-sheet)
- [6. CP/DSA Recognition](#6-cpdsa-recognition)
- [7. Engine Architecture](#7-engine-architecture)
- [8. Basic Median Engine](#8-basic-median-engine)
- [9. Dry Run: Insert And Remove Duplicates](#9-dry-run-insert-and-remove-duplicates)
- [10. CP Pattern 1: Running Median](#10-cp-pattern-1-running-median)
- [11. CP Pattern 2: Sliding Window Median](#11-cp-pattern-2-sliding-window-median)
- [12. CP Pattern 3: Dynamic Leaderboard](#12-cp-pattern-3-dynamic-leaderboard)
- [13. CP Pattern 4: Nearest Value Query](#13-cp-pattern-4-nearest-value-query)
- [14. CP Pattern 5: Ticket Allocation](#14-cp-pattern-5-ticket-allocation)
- [15. CP Pattern 6: Order Statistics Thinking](#15-cp-pattern-6-order-statistics-thinking)
- [16. Set vs Multiset](#16-set-vs-multiset)
- [17. Common Mistakes](#17-common-mistakes)
- [18. Complexity Table](#18-complexity-table)
- [19. Real-World Mapping](#19-real-world-mapping)
- [20. Final Mental Model](#20-final-mental-model)
- [21. Next Step](#21-next-step)

---

# 1. Goal

Learn `multiset` not only as STL syntax, but as a:

```text
Duplicate-Aware Ordered Analytics Engine
```

It helps solve:

```text
running median
sliding window median
dynamic ranking
duplicate-aware ordering
nearest value queries
ticket allocation
```

---

# 2. Why Multiset Is An Engine

`set` stores:

```text
unique sorted values
```

`multiset` stores:

```text
sorted values WITH duplicates
```

Normal thinking:

```cpp
multiset<int> ms;
```

Engine thinking:

```text
DuplicateAwareAnalyticsEngine
    stores ordered values
    preserves duplicates
    supports dynamic insertion/removal
    supports nearest/ranking queries
```

---

# 3. Real-System Mental Model

Real systems needing duplicates:

```text
leaderboards
stock prices
latency distributions
CPU metrics
ticket inventories
streaming medians
active request durations
```

Architecture:

```text
Incoming Value
      |
      v
MultisetAnalyticsEngine
      |
      +--> insert value
      +--> remove value
      +--> keep sorted duplicates
      +--> query median/min/max
      |
      v
Analytics Result
```

---

# 4. Multiset Core Behavior

Example:

```cpp
multiset<int> ms;

ms.insert(5);
ms.insert(1);
ms.insert(5);
ms.insert(3);
```

Stored:

```text
1 3 5 5
```

Important:

```text
duplicates are preserved
```

Unlike `set`.

---

# 5. Multiset Operations Cheat Sheet

```cpp
multiset<int> ms;

ms.insert(10);

ms.erase(ms.find(10));   // erase ONE occurrence

ms.count(10);

ms.find(10);

ms.lower_bound(x);

ms.upper_bound(x);

ms.begin();

ms.rbegin();
```

Important:

```cpp
ms.erase(10);
```

removes ALL occurrences.

To remove one:

```cpp
ms.erase(ms.find(10));
```

---

# 6. CP/DSA Recognition

Use `multiset` when problem says:

```text
duplicates matter
running median
dynamic sorted values
nearest greater/smaller
remove exact occurrence
sliding window order
dynamic ranking
```

Hidden mapping:

| Problem clue | Multiset pattern |
|---|---|
| duplicates + ordering | multiset |
| sliding median | multiset |
| remove one occurrence | multiset |
| nearest value | multiset |
| ticket allocation | multiset |
| dynamic ordered window | multiset |

---

# 7. Engine Architecture

```text
MiniMultisetMedianEngine
├── duplicate-aware ordering
├── insert/remove one occurrence
├── running median engine
├── sliding window analytics
├── nearest value engine
├── ranking engine
└── dynamic allocation engine
```

---

# 8. Basic Median Engine

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class MedianEngine {
private:
    multiset<int> values;

public:

    void addValue(int x) {

        // WHY:
        // multiset keeps all values sorted,
        // including duplicates.
        values.insert(x);
    }

    void removeValue(int x) {

        auto it = values.find(x);

        if (it != values.end()) {

            // Remove only ONE occurrence.
            values.erase(it);
        }
    }

    int minimum() {

        return *values.begin();
    }

    int maximum() {

        return *values.rbegin();
    }

    void printValues() {

        cout << "Ordered values: ";

        for (int x : values) {
            cout << x << " ";
        }

        cout << endl;
    }
};

int main() {

    MedianEngine engine;

    engine.addValue(5);
    engine.addValue(1);
    engine.addValue(5);
    engine.addValue(3);

    engine.printValues();

    engine.removeValue(5);

    engine.printValues();

    return 0;
}
```

---

# 9. Dry Run: Insert And Remove Duplicates

Operations:

```text
insert 5
insert 1
insert 5
insert 3
```

Multiset:

```text
1 3 5 5
```

Remove one `5`:

```cpp
erase(find(5))
```

Result:

```text
1 3 5
```

Important:

```cpp
erase(5)
```

would remove BOTH `5`s.

---

# 10. CP Pattern 1: Running Median

## Problem Type

```text
Maintain median dynamically.
```

Most famous approach:

```text
two heaps
```

But multiset also works.

## Core Idea

Since multiset is sorted:

```text
middle iterator gives median
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class RunningMedian {

private:
    multiset<int> ms;

public:

    void add(int x) {

        ms.insert(x);
    }

    double median() {

        int n = ms.size();

        auto it = next(ms.begin(), n / 2);

        if (n % 2 == 1) {

            return *it;
        }

        auto left = prev(it);

        return (*left + *it) / 2.0;
    }
};

int main() {

    RunningMedian rm;

    rm.add(1);
    cout << rm.median() << endl;

    rm.add(5);
    cout << rm.median() << endl;

    rm.add(3);
    cout << rm.median() << endl;

    return 0;
}
```

---

# 11. CP Pattern 2: Sliding Window Median

## Problem Type

```text
Median for every sliding window.
```

## Core Idea

Window engine:

```text
insert new value
remove expired value
query median
```

Multiset handles:

```text
ordered duplicates
```

very naturally.

---

# 12. CP Pattern 3: Dynamic Leaderboard

## Problem Type

```text
Maintain live rankings.
```

Example:

```text
game scores
streaming rankings
contest standings
```

Multiset supports:

```text
duplicate scores
sorted ranking
dynamic updates
```

---

# 13. CP Pattern 4: Nearest Value Query

## Problem Type

```text
Find closest value to x.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    multiset<int> ms = {
        1, 4, 6, 8, 10
    };

    int x = 7;

    auto it = ms.lower_bound(x);

    int best = INT_MAX;

    if (it != ms.end()) {

        best = min(best, abs(*it - x));
    }

    if (it != ms.begin()) {

        best = min(
            best,
            abs(*prev(it) - x)
        );
    }

    cout << best << endl;

    return 0;
}
```

## Key Insight

Closest value must be:

```text
predecessor or successor
```

in sorted order.

---

# 14. CP Pattern 5: Ticket Allocation

## Problem Type

```text
Find largest ticket price <= customer budget.
```

Classic CSES problem.

## Core Idea

Use:

```text
upper_bound
```

then move one step left.

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    multiset<int> tickets = {
        5, 10, 15, 20
    };

    vector<int> customers = {
        12, 5, 17
    };

    for (int budget : customers) {

        auto it =
            tickets.upper_bound(budget);

        if (it == tickets.begin()) {

            cout << -1 << endl;

        } else {

            --it;

            cout << *it << endl;

            // Remove allocated ticket.
            tickets.erase(it);
        }
    }

    return 0;
}
```

## Dry Run

Tickets:

```text
5 10 15 20
```

Customer:

```text
budget = 12
```

Process:

```text
upper_bound(12) -> 15
previous -> 10
allocate 10
```

---

# 15. CP Pattern 6: Order Statistics Thinking

Multiset helps simulate:

```text
ordered dynamic data
```

Useful for:

```text
top K
medians
percentiles
nearest queries
ranking
```

Advanced versions become:

```text
policy based data structures
Fenwick tree
segment tree
balanced BST
```

---

# 16. Set vs Multiset

| Feature | set | multiset |
|---|---|---|
| duplicates | no | yes |
| sorted | yes | yes |
| lower_bound | yes | yes |
| nearest query | yes | yes |
| frequency aware | weak | strong |
| median windows | limited | strong |

Rule:

```text
Need duplicates?
→ multiset
```

---

# 17. Common Mistakes

## Mistake 1: `erase(value)` Removes ALL

Wrong:

```cpp
ms.erase(5);
```

Removes all `5`s.

Correct:

```cpp
ms.erase(ms.find(5));
```

---

## Mistake 2: Dereferencing Invalid Iterator

Wrong:

```cpp
auto it = ms.lower_bound(x);
cout << *it;
```

Correct:

```cpp
if (it != ms.end()) {
    cout << *it;
}
```

---

## Mistake 3: Forgetting Duplicates Exist

Example:

```text
1 2 2 2 5
```

`find(2)` gives:

```text
some occurrence
```

not necessarily all.

---

## Mistake 4: Median Access Complexity

`next(begin, k)`:

```text
O(k)
```

not O(1).

Advanced solutions use:

```text
two heaps
policy-based DS
```

for faster medians.

---

# 18. Complexity Table

| Operation | Complexity |
|---|---:|
| insert | O(log N) |
| erase one occurrence | O(log N) |
| find | O(log N) |
| lower_bound | O(log N) |
| ordered traversal | O(N) |
| median via iterator move | O(N) worst-case |

---

# 19. Real-World Mapping

| Multiset Concept | Real-System Meaning |
|---|---|
| duplicate-aware ordering | repeated metrics/scores |
| running median | latency analytics |
| sliding median | monitoring systems |
| nearest value | recommendation threshold |
| ticket allocation | inventory allocation |
| dynamic leaderboard | ranking engine |
| ordered duplicates | repeated events/metrics |

---

# 20. Final Mental Model

Multiset is:

```text
duplicate-aware ordered analytics engine
```

Best for:

```text
running median
dynamic ranking
nearest queries
duplicate-aware ordering
ticket allocation
```

One-line CP rule:

```text
If you need sorted values WITH duplicates, think multiset.
```

One-line system rule:

```text
Multiset powers duplicate-aware ranking and analytics systems.
```

---

# 21. Next Step

Next file:

```text
012_BinarySearch_Index_Query_Engine.md
```

Then:

```text
013_Sort_Ranking_Engine.md
014_TwoPointer_Stream_Merge_Engine.md
015_SlidingWindow_Metrics_Engine.md
```
