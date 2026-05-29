# 009_Map_KeyValue_Index_Engine.md

> MiniSTLEngine Phase 009  
> Topic: `map` as a **Key-Value Ordered Index Engine** for CP, DSA, FAANG interviews, and real-system thinking.

---

# Clickable Index

- [1. Goal](#1-goal)
- [2. Why Map Is An Engine](#2-why-map-is-an-engine)
- [3. Real-System Mental Model](#3-real-system-mental-model)
- [4. Map Core Behavior](#4-map-core-behavior)
- [5. Map Operations Cheat Sheet](#5-map-operations-cheat-sheet)
- [6. CP/DSA Recognition](#6-cpdsa-recognition)
- [7. Engine Architecture](#7-engine-architecture)
- [8. Basic Key-Value Index Engine](#8-basic-key-value-index-engine)
- [9. Dry Run: Insert, Update, Query](#9-dry-run-insert-update-query)
- [10. CP Pattern 1: Frequency Counting](#10-cp-pattern-1-frequency-counting)
- [11. CP Pattern 2: Ordered Frequency Map](#11-cp-pattern-2-ordered-frequency-map)
- [12. CP Pattern 3: Coordinate Compression](#12-cp-pattern-3-coordinate-compression)
- [13. CP Pattern 4: Active Event Counter](#13-cp-pattern-4-active-event-counter)
- [14. CP Pattern 5: Character Frequency Analytics](#14-cp-pattern-5-character-frequency-analytics)
- [15. CP Pattern 6: Sweep Line Delta Map](#15-cp-pattern-6-sweep-line-delta-map)
- [16. Map vs Unordered Map](#16-map-vs-unordered-map)
- [17. Common Mistakes](#17-common-mistakes)
- [18. Complexity Table](#18-complexity-table)
- [19. Real-World Mapping](#19-real-world-mapping)
- [20. Final Mental Model](#20-final-mental-model)
- [21. Next Step](#21-next-step)

---

# 1. Goal

Learn `map` not only as STL syntax, but as a:

```text
Key-Value Ordered Index Engine
```

It helps solve:

```text
frequency counting
ordered key lookup
event aggregation
sweep line problems
coordinate compression
analytics counters
metadata indexing
```

---

# 2. Why Map Is An Engine

A `map` stores:

```text
key -> value
```

while keeping keys:

```text
sorted
```

Normal thinking:

```cpp
map<int,int> mp;
```

Engine thinking:

```text
KeyValueIndexEngine
    stores records by key
    supports ordered queries
    supports updates
    supports aggregation
    supports dynamic indexing
```

---

# 3. Real-System Mental Model

Real systems use maps everywhere:

```text
userId -> session
API -> request count
timestamp -> active users
productId -> stock
URL -> metadata
country -> analytics
```

Architecture:

```text
Incoming Record
      |
      v
KeyValueIndexEngine
      |
      +--> insert/update value
      +--> lookup by key
      +--> aggregate metrics
      +--> ordered traversal
      |
      v
Analytics / Query Result
```

---

# 4. Map Core Behavior

Example:

```cpp
map<int, string> mp;

mp[10] = "Alice";
mp[5] = "Bob";
mp[20] = "Charlie";
```

Stored order:

```text
5  -> Bob
10 -> Alice
20 -> Charlie
```

Important:

```text
keys remain sorted automatically
```

---

# 5. Map Operations Cheat Sheet

```cpp
map<int,int> mp;

mp[5] = 10;
mp.insert({7, 20});

mp[5];               // value lookup
mp.count(5);         // key exists?
mp.erase(5);

mp.find(7);

mp.lower_bound(x);
mp.upper_bound(x);

mp.begin();
mp.rbegin();
```

---

# 6. CP/DSA Recognition

Use `map` when problem says:

```text
frequency count
ordered aggregation
dynamic counters
event timeline
coordinate compression
ordered traversal
```

Hidden mapping:

| Problem clue | Map pattern |
|---|---|
| count frequencies | map/unordered_map |
| ordered frequency traversal | map |
| dynamic counters | map |
| event deltas | map |
| sorted unique keys with values | map |
| timestamps in order | map |
| coordinate compression | map |

---

# 7. Engine Architecture

```text
MiniMapKeyValueIndexEngine
├── insert/update key
├── lookup key
├── ordered traversal
├── frequency counter
├── analytics aggregator
├── sweep line delta engine
└── coordinate compression engine
```

---

# 8. Basic Key-Value Index Engine

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class KeyValueIndexEngine {
private:
    map<int, string> storage;

public:

    void put(int key, string value) {

        // WHY:
        // map keeps keys sorted automatically.
        // Updating existing key overwrites old value.
        storage[key] = value;
    }

    bool contains(int key) {

        return storage.find(key) != storage.end();
    }

    string get(int key) {

        if (!contains(key)) {
            return "NOT_FOUND";
        }

        return storage[key];
    }

    void remove(int key) {

        storage.erase(key);
    }

    void printAll() {

        cout << "Ordered Key-Value Data:\n";

        for (auto [key, value] : storage) {

            cout << key
                 << " -> "
                 << value
                 << endl;
        }
    }
};

int main() {

    KeyValueIndexEngine engine;

    engine.put(10, "Alice");
    engine.put(5, "Bob");
    engine.put(20, "Charlie");

    engine.printAll();

    cout << engine.get(10) << endl;

    engine.remove(5);

    engine.printAll();

    return 0;
}
```

---

# 9. Dry Run: Insert, Update, Query

Operations:

```text
put(10, Alice)
put(5, Bob)
put(20, Charlie)
```

Map:

```text
5  -> Bob
10 -> Alice
20 -> Charlie
```

Update:

```text
put(10, David)
```

Map:

```text
10 -> David
```

Why?

```text
same key overwrites old value
```

---

# 10. CP Pattern 1: Frequency Counting

## Problem Type

```text
Count occurrences of elements.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    vector<int> nums = {
        5, 1, 5, 2, 1, 5
    };

    map<int, int> freq;

    for (int x : nums) {

        // Frequency counter engine.
        freq[x]++;
    }

    for (auto [value, count] : freq) {

        cout << value
             << " appears "
             << count
             << " times\n";
    }

    return 0;
}
```

Output:

```text
1 appears 2 times
2 appears 1 times
5 appears 3 times
```

---

# 11. CP Pattern 2: Ordered Frequency Map

## Problem Type

```text
Need frequencies AND sorted traversal.
```

Difference:

| Structure | Order |
|---|---|
| unordered_map | random |
| map | sorted |

Useful when:

```text
output must be sorted
need smallest/largest key
need ordered processing
```

---

# 12. CP Pattern 3: Coordinate Compression

## Problem Type

```text
Large values need compact indices.
```

Example:

```text
1000000000 -> 0
5000000000 -> 1
9000000000 -> 2
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    vector<int> values = {
        100, 500, 100, 300
    };

    set<int> uniqueValues(
        values.begin(),
        values.end()
    );

    map<int, int> compressed;

    int index = 0;

    for (int x : uniqueValues) {

        // Assign compact sorted index.
        compressed[x] = index++;
    }

    for (int x : values) {

        cout << x
             << " -> "
             << compressed[x]
             << endl;
    }

    return 0;
}
```

## Output

```text
100 -> 0
500 -> 2
100 -> 0
300 -> 1
```

---

# 13. CP Pattern 4: Active Event Counter

## Problem Type

```text
Track counts dynamically by key.
```

Examples:

```text
active users
API usage
product stock
request counts
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    map<string, int> apiHits;

    vector<string> requests = {
        "/login",
        "/orders",
        "/login",
        "/profile",
        "/login"
    };

    for (string endpoint : requests) {

        // Analytics aggregation engine.
        apiHits[endpoint]++;
    }

    for (auto [endpoint, count] : apiHits) {

        cout << endpoint
             << " -> "
             << count
             << endl;
    }

    return 0;
}
```

---

# 14. CP Pattern 5: Character Frequency Analytics

## Problem Type

```text
Character analytics with sorted output.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    string s = "banana";

    map<char, int> freq;

    for (char ch : s) {

        freq[ch]++;
    }

    for (auto [ch, count] : freq) {

        cout << ch
             << " -> "
             << count
             << endl;
    }

    return 0;
}
```

Output:

```text
a -> 3
b -> 1
n -> 2
```

---

# 15. CP Pattern 6: Sweep Line Delta Map

## Problem Type

```text
Range add / interval overlap.
```

Very important sweep line pattern.

## Core Idea

For interval:

```text
[l, r]
```

store:

```text
+1 at l
-1 at r+1
```

Then scan in sorted order.

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    vector<pair<int,int>> intervals = {
        {1, 4},
        {2, 6},
        {5, 8}
    };

    map<int, int> delta;

    for (auto [l, r] : intervals) {

        // Interval starts increasing active count.
        delta[l]++;

        // After interval ends, active count decreases.
        delta[r + 1]--;
    }

    int active = 0;

    for (auto [position, change] : delta) {

        active += change;

        cout << "position="
             << position
             << ", active="
             << active
             << endl;
    }

    return 0;
}
```

## Dry Run

Intervals:

```text
[1,4]
[2,6]
[5,8]
```

Delta map:

```text
1 -> +1
2 -> +1
5 -> -1 +1 = 0
7 -> -1
9 -> -1
```

Running active counts:

```text
1 -> 1
2 -> 2
5 -> 2
7 -> 1
9 -> 0
```

---

# 16. Map vs Unordered Map

| Feature | map | unordered_map |
|---|---|---|
| order | sorted | random |
| implementation | balanced BST | hash table |
| lookup | O(log N) | average O(1) |
| lower_bound | yes | no |
| ordered traversal | yes | no |
| ceiling/floor query | yes | no |

Rule:

```text
Need order?
→ map

Need fastest average lookup only?
→ unordered_map
```

---

# 17. Common Mistakes

## Mistake 1: Using `mp[key]` For Existence Check

Wrong:

```cpp
if (mp[key]) {
}
```

Why dangerous?

```text
mp[key] creates missing key automatically
```

Correct:

```cpp
if (mp.find(key) != mp.end()) {
}
```

---

## Mistake 2: Expecting O(1) Lookup

`map` uses balanced BST.

Complexity:

```text
O(log N)
```

Need average O(1)?

```cpp
unordered_map
```

---

## Mistake 3: Forgetting Keys Stay Sorted

Map traversal order is:

```text
sorted by key
```

not insertion order.

---

## Mistake 4: Using Map When Array Enough

If values are small:

```cpp
vector<int> freq(1000);
```

can be faster than map.

---

# 18. Complexity Table

| Operation | Complexity |
|---|---:|
| insert/update | O(log N) |
| erase | O(log N) |
| find | O(log N) |
| lower_bound | O(log N) |
| ordered traversal | O(N) |
| frequency counting | O(N log N) |

---

# 19. Real-World Mapping

| Map Concept | Real-System Meaning |
|---|---|
| key -> value | database record |
| ordered keys | index scan |
| frequency map | analytics counter |
| delta map | event timeline |
| coordinate compression | ID normalization |
| API hit counter | monitoring system |
| timestamp map | ordered event stream |
| active count scan | concurrency tracking |

---

# 20. Final Mental Model

Map is:

```text
ordered key-value analytics/index engine
```

Best for:

```text
frequency counting
ordered aggregation
event timelines
coordinate compression
dynamic analytics
```

One-line CP rule:

```text
If you need ordered key-value lookup or aggregation, think map.
```

One-line system rule:

```text
Map behaves like a small ordered in-memory database index.
```

---

# 21. Next Step

Next file:

```text
010_UnorderedMap_Hash_Index_Engine.md
```

Then:

```text
011_Multiset_Median_Engine.md
012_BinarySearch_Index_Query_Engine.md
013_Sort_Ranking_Engine.md
```
