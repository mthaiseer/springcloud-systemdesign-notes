# 001_Vector_Dynamic_Array_Engine.md

> MiniSTLEngine Phase 001  
> Topic: `vector` as a **Dynamic Array Engine** for CP, DSA, and real-system thinking.

---

## Clickable Index

- [1. Goal](#1-goal)
- [2. Why Vector Is The First STL Engine](#2-why-vector-is-the-first-stl-engine)
- [3. Real-System Mental Model](#3-real-system-mental-model)
- [4. Internal Working Of Vector](#4-internal-working-of-vector)
- [5. Vector Operations Cheat Sheet](#5-vector-operations-cheat-sheet)
- [6. CP/DSA Recognition](#6-cpdsa-recognition)
- [7. Engine Architecture](#7-engine-architecture)
- [8. Basic Dynamic Array Engine](#8-basic-dynamic-array-engine)
- [9. Dry Run: Push Back And Resize](#9-dry-run-push-back-and-resize)
- [10. CP Pattern 1: Store And Sort](#10-cp-pattern-1-store-and-sort)
- [11. CP Pattern 2: Prefix Sum Buffer](#11-cp-pattern-2-prefix-sum-buffer)
- [12. CP Pattern 3: Frequency Array](#12-cp-pattern-3-frequency-array)
- [13. CP Pattern 4: Adjacency List For Graph](#13-cp-pattern-4-adjacency-list-for-graph)
- [14. CP Pattern 5: 2D Vector Grid](#14-cp-pattern-5-2d-vector-grid)
- [15. Common Mistakes](#15-common-mistakes)
- [16. Complexity Table](#16-complexity-table)
- [17. Real-World Mapping](#17-real-world-mapping)
- [18. Final Mental Model](#18-final-mental-model)
- [19. Next Step](#19-next-step)

---

# 1. Goal

Learn `vector` not only as C++ syntax, but as a:

```text
Dynamic Array Engine
```

It helps you build:

```text
in-memory buffers
event logs
metric arrays
graph adjacency lists
prefix sum indexes
DP tables
grid systems
```

---

# 2. Why Vector Is The First STL Engine

In CP/DSA, `vector` is the most used STL container.

You use it when you need:

```text
contiguous storage
fast index access
dynamic size
easy iteration
sorting
binary search
prefix sum
DP arrays
graph lists
```

Normal thinking:

```text
vector<int> v;
```

Engine thinking:

```text
DynamicArrayEngine
    stores records
    grows automatically
    supports O(1) indexed reads
    supports amortized O(1) append
```

---

# 3. Real-System Mental Model

Think of `vector` like a small in-memory data store.

```text
Client Code
    |
    v
DynamicArrayEngine
    |
    +--> append(value)
    +--> get(index)
    +--> update(index, value)
    +--> sort()
    +--> scan()
    +--> buildPrefixIndex()
```

Real systems use this idea in:

```text
log buffers
metric samples
request batches
in-memory event queues
time-series chunks
graph adjacency storage
```

---

# 4. Internal Working Of Vector

A vector internally has:

```text
size     = number of used elements
capacity = allocated memory slots
array    = contiguous memory block
```

Example:

```text
size = 3
capacity = 4

index:     0   1   2   3
memory:   [5, 10, 20, _]
used:      yes yes yes no
```

When vector becomes full:

```text
push_back needs more capacity
    ↓
allocate bigger memory block
    ↓
copy/move old elements
    ↓
insert new element
```

That is why `push_back` is:

```text
amortized O(1)
```

not always pure O(1).

---

# 5. Vector Operations Cheat Sheet

```cpp
vector<int> v;

v.push_back(10);       // add at end
v.pop_back();          // remove last
v.size();              // number of elements
v.empty();             // check empty
v.clear();             // remove all
v[i];                  // direct access
v.front();             // first element
v.back();              // last element

sort(v.begin(), v.end());
reverse(v.begin(), v.end());
```

---

# 6. CP/DSA Recognition

Use `vector` when problem says:

```text
array
list of numbers
store all values
sort values
prefix sum
DP table
graph nodes
grid/matrix
answers for each index
```

Avoid only when:

```text
many insert/delete in middle        -> list/deque/set
need current min/max repeatedly     -> priority_queue
need key-value fast lookup          -> unordered_map
need sorted unique dynamic values   -> set
```

---

# 7. Engine Architecture

```text
MiniVectorEngine
├── raw storage
├── append API
├── indexed read API
├── update API
├── scan API
├── sorting API
├── prefix index builder
└── analytics helpers
```

---

# 8. Basic Dynamic Array Engine

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class DynamicArrayEngine {
private:
    vector<int> data;

public:
    void append(int value) {
        data.push_back(value);
    }

    int get(int index) {
        if (index < 0 || index >= (int)data.size()) {
            throw out_of_range("Invalid index");
        }

        return data[index];
    }

    void update(int index, int value) {
        if (index < 0 || index >= (int)data.size()) {
            throw out_of_range("Invalid index");
        }

        data[index] = value;
    }

    void removeLast() {
        if (!data.empty()) {
            data.pop_back();
        }
    }

    int size() {
        return data.size();
    }

    void sortAscending() {
        sort(data.begin(), data.end());
    }

    void print() {
        cout << "Data: ";

        for (int x : data) {
            cout << x << " ";
        }

        cout << endl;
    }
};

int main() {
    DynamicArrayEngine engine;

    engine.append(30);
    engine.append(10);
    engine.append(20);

    engine.print();

    cout << "Value at index 1: ";
    cout << engine.get(1) << endl;

    engine.update(1, 99);

    engine.print();

    engine.sortAscending();

    engine.print();

    return 0;
}
```

---

# 9. Dry Run: Push Back And Resize

Suppose vector starts empty.

```text
data = []
size = 0
capacity = 0
```

After:

```cpp
push_back(10)
```

```text
data = [10]
size = 1
capacity maybe = 1
```

After:

```cpp
push_back(20)
```

If capacity is full:

```text
old memory: [10]
new memory: [10, 20]
size = 2
capacity maybe = 2
```

After:

```cpp
push_back(30)
```

Again capacity may grow:

```text
old memory: [10, 20]
new memory: [10, 20, 30, _]
size = 3
capacity maybe = 4
```

Important:

```text
push_back is usually O(1)
but sometimes O(N) during reallocation
overall amortized O(1)
```

---

# 10. CP Pattern 1: Store And Sort

## Problem Type

```text
Given N numbers, sort them and answer something.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> nums = {5, 1, 9, 3, 2};

    sort(nums.begin(), nums.end());

    for (int x : nums) {
        cout << x << " ";
    }

    return 0;
}
```

Output:

```text
1 2 3 5 9
```

## Pattern Recognition

Use when problem says:

```text
minimum difference
pair after sorting
ranking
order statistics
merge intervals
greedy after sorting
```

---

# 11. CP Pattern 2: Prefix Sum Buffer

Vector is commonly used to store prefix sums.

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {2, 4, 1, 6, 3};

    int n = a.size();

    vector<long long> prefix(n + 1, 0);

    for (int i = 0; i < n; i++) {
        prefix[i + 1] = prefix[i] + a[i];
    }

    int l = 1;
    int r = 3;

    long long sum = prefix[r + 1] - prefix[l];

    cout << sum << endl; // 4 + 1 + 6 = 11

    return 0;
}
```

## Why `n + 1` Prefix Is Cleaner

```text
prefix[0] = 0
prefix[i] = sum of first i elements
sum(l, r) = prefix[r + 1] - prefix[l]
```

No special case for `l = 0`.

---

# 12. CP Pattern 3: Frequency Array

When values are small, vector can act like a frequency table.

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> nums = {1, 3, 2, 3, 1, 3};

    int maxValue = 3;

    vector<int> freq(maxValue + 1, 0);

    for (int x : nums) {
        freq[x]++;
    }

    for (int value = 0; value <= maxValue; value++) {
        cout << value << " appears " << freq[value] << " times\n";
    }

    return 0;
}
```

## Use When

```text
values are small
need count of each value
need O(1) frequency lookup
```

If values are huge:

```text
use unordered_map
```

---

# 13. CP Pattern 4: Adjacency List For Graph

Vector is the standard way to store graphs.

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n = 5;

    vector<vector<int>> graph(n + 1);

    auto addEdge = [&](int u, int v) {
        graph[u].push_back(v);
        graph[v].push_back(u);
    };

    addEdge(1, 2);
    addEdge(1, 3);
    addEdge(2, 4);
    addEdge(3, 5);

    for (int node = 1; node <= n; node++) {
        cout << node << ": ";

        for (int nei : graph[node]) {
            cout << nei << " ";
        }

        cout << endl;
    }

    return 0;
}
```

Graph:

```text
1
├── 2
│   └── 4
└── 3
    └── 5
```

Adjacency list:

```text
1: 2 3
2: 1 4
3: 1 5
4: 2
5: 3
```

---

# 14. CP Pattern 5: 2D Vector Grid

Use `vector<vector<int>>` for matrix/grid problems.

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int rows = 3;
    int cols = 4;

    vector<vector<int>> grid(rows, vector<int>(cols, 0));

    grid[0][1] = 5;
    grid[2][3] = 9;

    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            cout << grid[r][c] << " ";
        }
        cout << endl;
    }

    return 0;
}
```

Use for:

```text
BFS grid
DFS island
2D DP
2D prefix sum
matrix traversal
visited array
```

---

# 15. Common Mistakes

## Mistake 1: Accessing Empty Vector

Wrong:

```cpp
vector<int> v;
cout << v[0];
```

Correct:

```cpp
if (!v.empty()) {
    cout << v[0];
}
```

---

## Mistake 2: Forgetting Size Before Direct Assignment

Wrong:

```cpp
vector<int> v;
v[0] = 10;
```

Correct:

```cpp
vector<int> v(1);
v[0] = 10;
```

or:

```cpp
vector<int> v;
v.push_back(10);
```

---

## Mistake 3: Erase In Middle Too Often

```cpp
v.erase(v.begin() + i);
```

This is:

```text
O(N)
```

because elements shift.

If many middle deletes are needed, consider:

```text
set
list
deque
lazy deletion
```

---

## Mistake 4: Iterator Invalid After Push Back

When vector reallocates, old references/iterators may become invalid.

Bad mental model:

```text
pointer to vector element is always safe
```

Correct:

```text
after push_back, reallocation may move memory
```

---

# 16. Complexity Table

| Operation | Complexity |
|---|---:|
| `v[i]` | O(1) |
| `push_back(x)` | amortized O(1) |
| `pop_back()` | O(1) |
| iterate all | O(N) |
| sort | O(N log N) |
| insert middle | O(N) |
| erase middle | O(N) |
| binary search on sorted vector | O(log N) |

---

# 17. Real-World Mapping

| Vector Concept | Real-System Meaning |
|---|---|
| contiguous storage | memory-efficient data block |
| push_back | append-only log/event buffer |
| index access | direct offset lookup |
| sorting vector | ranking/index build |
| prefix vector | precomputed aggregation index |
| vector of vectors | graph/grid storage |
| capacity growth | memory allocation strategy |
| erase middle cost | write amplification / shifting cost |

---

# 18. Final Mental Model

Vector is:

```text
Dynamic contiguous memory engine
```

Best for:

```text
fast reads
append-heavy workloads
scan-heavy workloads
sort-heavy workloads
index-based DP
graph/grid representation
```

Not best for:

```text
frequent middle insertion
frequent middle deletion
ordered dynamic queries
key-value lookup
```

One-line CP rule:

```text
If data is array-like and index matters, start with vector.
```

One-line system rule:

```text
Vector is the base in-memory storage block behind many higher-level engines.
```

---

# 19. Next Step

Next file:

```text
002_String_Buffer_Engine.md
```

Then:

```text
003_Pair_Tuple_Record_Engine.md
004_Stack_Parser_Engine.md
005_Queue_Event_Buffer_Engine.md
```
