# 010_UnorderedMap_Hash_Index_Engine.md

> MiniSTLEngine Phase 010  
> Topic: `unordered_map` as a **Hash Index / O(1) Lookup Engine** for CP, DSA, FAANG interviews, and real-system thinking.

---

# Clickable Index

- [1. Goal](#1-goal)
- [2. Why Unordered Map Is An Engine](#2-why-unordered-map-is-an-engine)
- [3. Real-System Mental Model](#3-real-system-mental-model)
- [4. Hash Table Core Behavior](#4-hash-table-core-behavior)
- [5. Unordered Map Operations Cheat Sheet](#5-unordered-map-operations-cheat-sheet)
- [6. CP/DSA Recognition](#6-cpdsa-recognition)
- [7. Engine Architecture](#7-engine-architecture)
- [8. Basic Hash Index Engine](#8-basic-hash-index-engine)
- [9. Dry Run: Insert And Lookup](#9-dry-run-insert-and-lookup)
- [10. CP Pattern 1: Two Sum](#10-cp-pattern-1-two-sum)
- [11. CP Pattern 2: Frequency Counter](#11-cp-pattern-2-frequency-counter)
- [12. CP Pattern 3: Longest Consecutive Sequence](#12-cp-pattern-3-longest-consecutive-sequence)
- [13. CP Pattern 4: Group Anagrams](#13-cp-pattern-4-group-anagrams)
- [14. CP Pattern 5: Prefix Sum Frequency Engine](#14-cp-pattern-5-prefix-sum-frequency-engine)
- [15. CP Pattern 6: LRU Cache Foundation](#15-cp-pattern-6-lru-cache-foundation)
- [16. Map vs Unordered Map](#16-map-vs-unordered-map)
- [17. Common Mistakes](#17-common-mistakes)
- [18. Complexity Table](#18-complexity-table)
- [19. Real-World Mapping](#19-real-world-mapping)
- [20. Final Mental Model](#20-final-mental-model)
- [21. Next Step](#21-next-step)

---

# 1. Goal

Learn `unordered_map` not only as hash table syntax, but as a:

```text
Hash Index / Constant-Time Lookup Engine
```

It helps solve:

```text
fast lookup
frequency counting
cache systems
ID indexing
prefix sum optimization
grouping
deduplication
```

---

# 2. Why Unordered Map Is An Engine

`unordered_map` stores:

```text
key -> value
```

using:

```text
hash table
```

Normal thinking:

```cpp
unordered_map<int,int> mp;
```

Engine thinking:

```text
HashIndexEngine
    maps keys to values
    supports near O(1) lookup
    supports fast updates
    powers caching/indexing systems
```

Core question:

```text
Do I need extremely fast lookup by key?
```

If yes:

```text
unordered_map is likely useful
```

---

# 3. Real-System Mental Model

Real systems use hash maps everywhere:

```text
Redis key-value storage
session cache
API token lookup
URL shortener mapping
DNS cache
user ID index
request deduplication
frequency analytics
```

Architecture:

```text
Incoming Key
      |
      v
HashIndexEngine
      |
      +--> hash key
      +--> locate bucket
      +--> lookup/update value
      |
      v
O(1)-style Query Result
```

---

# 4. Hash Table Core Behavior

Example:

```cpp
unordered_map<int, string> mp;

mp[10] = "Alice";
mp[5] = "Bob";
mp[20] = "Charlie";
```

Unlike `map`:

```text
order is NOT guaranteed
```

Important property:

```text
very fast average lookup
```

---

# 5. Unordered Map Operations Cheat Sheet

```cpp
unordered_map<int,int> mp;

mp[5] = 10;

mp.insert({7, 20});

mp[5];

mp.count(5);

mp.erase(5);

mp.find(7);

mp.empty();

mp.size();
```

Complexity:

```text
average O(1)
```

---

# 6. CP/DSA Recognition

Use `unordered_map` when problem says:

```text
fast lookup
frequency count
seen before
store complements
cache values
group by key
```

Hidden mapping:

| Problem clue | Hash map pattern |
|---|---|
| complement lookup | unordered_map |
| frequency count | unordered_map |
| visited lookup | unordered_set/map |
| cache lookup | unordered_map |
| fast existence check | unordered_map/set |
| group by category | unordered_map |
| prefix sum counts | unordered_map |

---

# 7. Engine Architecture

```text
MiniUnorderedMapHashIndexEngine
├── fast insert
├── fast lookup
├── frequency counter
├── complement lookup engine
├── prefix analytics engine
├── grouping engine
└── cache foundation engine
```

---

# 8. Basic Hash Index Engine

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class HashIndexEngine {
private:
    unordered_map<int, string> storage;

public:

    void put(int key, string value) {

        // WHY:
        // Hash map provides near O(1) insertion/update.
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

        cout << "Hash Index Data:\n";

        for (auto [key, value] : storage) {

            cout << key
                 << " -> "
                 << value
                 << endl;
        }
    }
};

int main() {

    HashIndexEngine engine;

    engine.put(10, "Alice");
    engine.put(5, "Bob");
    engine.put(20, "Charlie");

    cout << engine.get(10) << endl;

    engine.remove(5);

    engine.printAll();

    return 0;
}
```

---

# 9. Dry Run: Insert And Lookup

Operations:

```text
put(10, Alice)
put(5, Bob)
put(20, Charlie)
```

Internal order:

```text
not guaranteed
```

Lookup:

```text
get(10) -> Alice
```

Hash engine:

```text
hash(10)
-> locate bucket
-> find key
-> return value
```

---

# 10. CP Pattern 1: Two Sum

## Problem Type

```text
Find two numbers whose sum equals target.
```

## Core Idea

For each number:

```text
needed = target - current
```

Check instantly whether needed value already exists.

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> twoSum(vector<int>& nums, int target) {

    unordered_map<int, int> indexMap;
    // value -> index

    for (int i = 0; i < (int)nums.size(); i++) {

        int needed = target - nums[i];

        // O(1)-style complement lookup.
        if (indexMap.find(needed) != indexMap.end()) {

            return {
                indexMap[needed],
                i
            };
        }

        indexMap[nums[i]] = i;
    }

    return {};
}

int main() {

    vector<int> nums = {2, 7, 11, 15};

    vector<int> ans =
        twoSum(nums, 9);

    cout << ans[0]
         << " "
         << ans[1]
         << endl;

    return 0;
}
```

## Dry Run

```text
target = 9

2 -> need 7 -> not found
store 2

7 -> need 2 -> found
answer = [0,1]
```

---

# 11. CP Pattern 2: Frequency Counter

## Problem Type

```text
Count occurrences efficiently.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    vector<int> nums = {
        1,2,1,3,2,1
    };

    unordered_map<int, int> freq;

    for (int x : nums) {

        // Frequency aggregation engine.
        freq[x]++;
    }

    for (auto [value, count] : freq) {

        cout << value
             << " -> "
             << count
             << endl;
    }

    return 0;
}
```

Complexity:

```text
average O(N)
```

instead of:

```text
O(N log N) using map
```

---

# 12. CP Pattern 3: Longest Consecutive Sequence

## Problem Type

```text
Find longest consecutive integer sequence.
```

## Core Idea

Hash set gives:

```text
O(1) existence check
```

Only start sequence when:

```text
x-1 does NOT exist
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int longestConsecutive(vector<int>& nums) {

    unordered_set<int> s(
        nums.begin(),
        nums.end()
    );

    int best = 0;

    for (int x : nums) {

        // Start sequence only if x is sequence start.
        if (!s.count(x - 1)) {

            int current = x;
            int length = 1;

            while (s.count(current + 1)) {

                current++;
                length++;
            }

            best = max(best, length);
        }
    }

    return best;
}

int main() {

    vector<int> nums = {
        100,4,200,1,3,2
    };

    cout << longestConsecutive(nums)
         << endl;

    return 0;
}
```

---

# 13. CP Pattern 4: Group Anagrams

## Problem Type

```text
Group strings with same characters.
```

## Core Idea

Sorted string acts as canonical key.

Example:

```text
eat -> aet
tea -> aet
ate -> aet
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<vector<string>> groupAnagrams(
    vector<string>& strs
) {

    unordered_map<
        string,
        vector<string>
    > groups;

    for (string s : strs) {

        string key = s;

        sort(key.begin(), key.end());

        // Same sorted key means same anagram group.
        groups[key].push_back(s);
    }

    vector<vector<string>> answer;

    for (auto& [key, group] : groups) {
        answer.push_back(group);
    }

    return answer;
}

int main() {

    vector<string> strs = {
        "eat","tea","tan","ate","nat","bat"
    };

    auto groups =
        groupAnagrams(strs);

    for (auto& group : groups) {

        for (string s : group) {
            cout << s << " ";
        }

        cout << endl;
    }

    return 0;
}
```

---

# 14. CP Pattern 5: Prefix Sum Frequency Engine

## Problem Type

```text
Count subarrays with sum K.
```

## Core Idea

If:

```text
prefixSum - k
```

already appeared before,

then subarray sum equals `k`.

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int subarraySum(vector<int>& nums, int k) {

    unordered_map<int, int> prefixFreq;

    // Empty prefix sum.
    prefixFreq[0] = 1;

    int prefix = 0;
    int answer = 0;

    for (int x : nums) {

        prefix += x;

        int needed = prefix - k;

        // All previous occurrences of 'needed'
        // create valid subarrays ending here.
        if (prefixFreq.count(needed)) {

            answer += prefixFreq[needed];
        }

        prefixFreq[prefix]++;
    }

    return answer;
}

int main() {

    vector<int> nums = {1,1,1};

    cout << subarraySum(nums, 2)
         << endl;

    return 0;
}
```

---

# 15. CP Pattern 6: LRU Cache Foundation

## Real-System Type

```text
Fast cache lookup
```

Hash map provides:

```text
key -> node pointer
```

in:

```text
O(1)
```

Real LRU architecture:

```text
unordered_map
+
doubly linked list
```

Used in:

```text
Redis
browser cache
database cache
API cache
```

---

# 16. Map vs Unordered Map

| Feature | map | unordered_map |
|---|---|---|
| implementation | balanced BST | hash table |
| order | sorted | random |
| lookup | O(log N) | average O(1) |
| lower_bound | yes | no |
| ordered traversal | yes | no |
| best for | ordered queries | fast lookup |

Rule:

```text
Need ordering?
→ map

Need fastest lookup?
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
creates missing key automatically
```

Correct:

```cpp
if (mp.find(key) != mp.end()) {
}
```

---

## Mistake 2: Expecting Sorted Order

Iteration order:

```text
NOT guaranteed
```

If order matters:

```cpp
map
```

---

## Mistake 3: Worst-Case Complexity

Average:

```text
O(1)
```

Worst-case:

```text
O(N)
```

due to collisions.

In CP this is usually acceptable.

---

## Mistake 4: Custom Key Without Hash

For custom structs/pairs:

```text
need custom hash function
```

---

# 18. Complexity Table

| Operation | Complexity |
|---|---:|
| insert/update | average O(1) |
| erase | average O(1) |
| find | average O(1) |
| frequency counting | average O(N) |
| complement lookup | average O(1) |
| iteration | O(N) |

---

# 19. Real-World Mapping

| Hash Map Concept | Real-System Meaning |
|---|---|
| key -> value | Redis-style KV lookup |
| complement lookup | recommendation matching |
| frequency engine | analytics counter |
| cache lookup | session/token cache |
| prefix frequency | streaming analytics |
| grouping | aggregation engine |
| O(1) lookup | high-speed indexing |
| hash buckets | distributed partitioning idea |

---

# 20. Final Mental Model

Unordered map is:

```text
high-speed hash index engine
```

Best for:

```text
fast lookup
frequency counting
caching
complement search
grouping
prefix analytics
```

One-line CP rule:

```text
If you repeatedly need fast lookup by key, think unordered_map.
```

One-line system rule:

```text
Hash maps power caches, indexes, analytics counters, and high-speed lookup systems.
```

---

# 21. Next Step

Next file:

```text
011_Multiset_Median_Engine.md
```

Then:

```text
012_BinarySearch_Index_Query_Engine.md
013_Sort_Ranking_Engine.md
014_TwoPointer_Stream_Merge_Engine.md
```
