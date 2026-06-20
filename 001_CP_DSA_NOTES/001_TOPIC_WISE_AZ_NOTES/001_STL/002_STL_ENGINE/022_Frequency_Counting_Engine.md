# 023_Frequency_Counting_Engine.md

> MiniSTLEngine Advanced Phase 023  
> Topic: Frequency Counting as a **Hash Analytics / Stream Counting / Top-K Engine** for CP, DSA, FAANG interviews, and real-system thinking.

---

# Clickable Index

- [1. Goal](#1-goal)
- [2. Why Frequency Counting Is An Engine](#2-why-frequency-counting-is-an-engine)
- [3. Real-System Mental Model](#3-real-system-mental-model)
- [4. Core Frequency Behavior](#4-core-frequency-behavior)
- [5. CP/DSA Recognition](#5-cpdsa-recognition)
- [6. Engine Architecture](#6-engine-architecture)
- [7. Basic Frequency Engine](#7-basic-frequency-engine)
- [8. Dry Run: Frequency Counting](#8-dry-run-frequency-counting)
- [9. CP Pattern 1: Count Frequencies](#9-cp-pattern-1-count-frequencies)
- [10. CP Pattern 2: Two Sum](#10-cp-pattern-2-two-sum)
- [11. CP Pattern 3: Top K Frequent Elements](#11-cp-pattern-3-top-k-frequent-elements)
- [12. CP Pattern 4: Longest Consecutive Sequence](#12-cp-pattern-4-longest-consecutive-sequence)
- [13. CP Pattern 5: Anagram Grouping](#13-cp-pattern-5-anagram-grouping)
- [14. CP Pattern 6: Prefix Frequency Map](#14-cp-pattern-6-prefix-frequency-map)
- [15. CP Pattern 7: Redis Counter Mental Model](#15-cp-pattern-7-redis-counter-mental-model)
- [16. unordered_map vs map](#16-unordered_map-vs-map)
- [17. Common Mistakes](#17-common-mistakes)
- [18. Complexity Table](#18-complexity-table)
- [19. Real-World Mapping](#19-real-world-mapping)
- [20. Final Mental Model](#20-final-mental-model)
- [21. Next Step](#21-next-step)

---

# 1. Goal

Learn frequency counting not only as:

```text
count numbers
```

but as a:

```text
Hash Analytics / Stream Counting / Top-K Engine
```

It helps solve:

```text
frequency counting
duplicate detection
top-k queries
prefix frequency
hash analytics
stream counters
cache statistics
real-time metrics
```

---

# 2. Why Frequency Counting Is An Engine

Many problems ask:

```text
How many times did something occur?
```

Brute force:

```text
For every element:
    scan whole array again
```

Complexity:

```text
O(N^2)
```

Frequency engine stores counts dynamically.

Engine thinking:

```text
FrequencyCountingEngine
    stores occurrence counts
    updates counts in O(1)
    supports fast lookup
    powers analytics systems
```

---

# 3. Real-System Mental Model

Real systems constantly count things:

```text
API request counts
user login frequency
search keyword counts
page view analytics
Redis INCR counters
rate limiting
top trending hashtags
cache hit counters
```

Architecture:

```text
Incoming Events
       |
       v
FrequencyCountingEngine
       |
       +--> increment counter
       +--> lookup frequency
       +--> compute top-k
       |
       v
Analytics / Monitoring
```

---

# 4. Core Frequency Behavior

Input:

```text
[1, 2, 1, 3, 2, 1]
```

Frequency map:

```text
1 -> 3
2 -> 2
3 -> 1
```

Core operation:

```cpp
freq[x]++;
```

This single line powers huge systems.

---

# 5. CP/DSA Recognition

Use frequency counting when problem says:

```text
count occurrences
duplicates
pairs
frequency
most common
top-k
anagram
seen before
lookup existence
```

Hidden mapping:

| Problem clue | Pattern |
|---|---|
| count duplicates | frequency map |
| seen before | hash set |
| pair sum | hash lookup |
| top-k frequent | map + heap/bucket |
| anagram | character frequency |
| longest sequence | hash existence |
| subarray sum = k | prefix frequency map |

---

# 6. Engine Architecture

```text
MiniFrequencyCountingEngine
├── frequency map
├── duplicate detector
├── top-k engine
├── stream counter
├── prefix frequency engine
├── analytics counter
└── Redis-style increment engine
```

---

# 7. Basic Frequency Engine

## Step-by-Step Approach Before Code

```text
Step 1: Create hash map.

Step 2: Traverse array.

Step 3: Increment count for each value.

Step 4: Query counts when needed.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class FrequencyCountingEngine {
public:

    unordered_map<int,int> buildFrequencyMap(
        vector<int>& nums
    ) {
        unordered_map<int,int> freq;

        for (int x : nums) {

            // Increment frequency.
            freq[x]++;
        }

        return freq;
    }
};

int main() {

    vector<int> nums = {
        1,2,1,3,2,1
    };

    FrequencyCountingEngine engine;

    auto freq =
        engine.buildFrequencyMap(nums);

    for (auto [value, count] : freq) {

        cout << value
             << " -> "
             << count
             << endl;
    }

    return 0;
}
```

---

# 8. Dry Run: Frequency Counting

Input:

```text
[1,2,1,3,2,1]
```

Initial:

```text
freq = {}
```

Read `1`:

```text
freq[1] = 1
```

Read `2`:

```text
freq[2] = 1
```

Read `1`:

```text
freq[1] = 2
```

Read `3`:

```text
freq[3] = 1
```

Read `2`:

```text
freq[2] = 2
```

Read `1`:

```text
freq[1] = 3
```

Final:

```text
1 -> 3
2 -> 2
3 -> 1
```

---

# 9. CP Pattern 1: Count Frequencies

## Problem Type

```text
Count occurrence of every number.
```

## Step-by-Step Approach Before Code

```text
Step 1: Create unordered_map.

Step 2: Traverse array.

Step 3: Increment freq[value].

Step 4: Output counts.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

unordered_map<int,int> countFrequency(
    vector<int>& nums
) {
    unordered_map<int,int> freq;

    for (int x : nums) {

        freq[x]++;
    }

    return freq;
}

int main() {

    vector<int> nums = {
        4,4,2,7,2,4
    };

    auto freq =
        countFrequency(nums);

    for (auto [value, count] : freq) {

        cout << value
             << " -> "
             << count
             << endl;
    }

    return 0;
}
```

---

# 10. CP Pattern 2: Two Sum

## Problem Type

```text
Find two indices where:
nums[i] + nums[j] = target
```

## Core Idea

Instead of checking every pair:

```text
store previously seen values
```

## Step-by-Step Approach Before Code

```text
Step 1: Create hash map:
        value -> index.

Step 2: For current value x:
        needed = target - x.

Step 3: If needed already exists:
        answer found.

Step 4: Store current value.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> twoSum(
    vector<int>& nums,
    int target
) {
    unordered_map<int,int> seen;

    for (int i = 0; i < (int)nums.size(); i++) {

        int needed = target - nums[i];

        if (seen.count(needed)) {

            return {seen[needed], i};
        }

        seen[nums[i]] = i;
    }

    return {};
}

int main() {

    vector<int> nums = {
        2,7,11,15
    };

    auto ans = twoSum(nums, 9);

    cout << ans[0]
         << " "
         << ans[1]
         << endl;

    return 0;
}
```

---

# 11. CP Pattern 3: Top K Frequent Elements

## Problem Type

```text
Find k most frequent elements.
```

## Core Idea

Two stages:

```text
1. count frequency
2. retrieve largest frequencies
```

Use:

```text
frequency map + heap
```

## Step-by-Step Approach Before Code

```text
Step 1: Build frequency map.

Step 2: Push {frequency,value} into max heap.

Step 3: Pop top K elements.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> topKFrequent(
    vector<int>& nums,
    int k
) {
    unordered_map<int,int> freq;

    for (int x : nums) {
        freq[x]++;
    }

    priority_queue<pair<int,int>> pq;

    for (auto [value, count] : freq) {

        pq.push({count, value});
    }

    vector<int> answer;

    while (k--) {

        answer.push_back(pq.top().second);

        pq.pop();
    }

    return answer;
}

int main() {

    vector<int> nums = {
        1,1,1,2,2,3
    };

    auto ans =
        topKFrequent(nums, 2);

    for (int x : ans) {
        cout << x << " ";
    }

    return 0;
}
```

---

# 12. CP Pattern 4: Longest Consecutive Sequence

## Problem Type

```text
Find longest consecutive sequence.
```

## Core Idea

Hash set gives:

```text
O(1) existence lookup
```

Start sequence only when:

```text
x-1 does not exist
```

## Step-by-Step Approach Before Code

```text
Step 1: Insert all values into hash set.

Step 2: For every value:
        if x-1 does not exist:
            x starts a sequence.

Step 3: Extend sequence forward.

Step 4: Track maximum length.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int longestConsecutive(
    vector<int>& nums
) {
    unordered_set<int> s(
        nums.begin(),
        nums.end()
    );

    int best = 0;

    for (int x : s) {

        // Sequence starts only if x-1 absent.
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

# 13. CP Pattern 5: Anagram Grouping

## Problem Type

```text
Group words having same character frequencies.
```

## Core Idea

Sorted string or frequency vector acts as signature.

## Step-by-Step Approach Before Code

```text
Step 1: For each word:
        sort characters.

Step 2: Sorted string becomes hash key.

Step 3: Store word inside map[key].
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<vector<string>> groupAnagrams(
    vector<string>& words
) {
    unordered_map<
        string,
        vector<string>
    > groups;

    for (string word : words) {

        string key = word;

        sort(key.begin(), key.end());

        groups[key].push_back(word);
    }

    vector<vector<string>> answer;

    for (auto& [key, group] : groups) {

        answer.push_back(group);
    }

    return answer;
}

int main() {

    vector<string> words = {
        "eat","tea","tan","ate","nat","bat"
    };

    auto ans =
        groupAnagrams(words);

    for (auto& group : ans) {

        for (string s : group) {
            cout << s << " ";
        }

        cout << endl;
    }

    return 0;
}
```

---

# 14. CP Pattern 6: Prefix Frequency Map

## Problem Type

```text
Count subarrays with sum = k.
```

## Core Idea

If:

```text
prefix[i] - prefix[j] = k
```

then:

```text
prefix[j] = prefix[i] - k
```

Need:

```text
frequency of previous prefix sums
```

## Step-by-Step Approach Before Code

```text
Step 1: Maintain running prefix sum.

Step 2: Store prefix frequency in map.

Step 3: For current prefix:
        needed = prefix - k.

Step 4: Add freq[needed] to answer.

Step 5: Increment current prefix frequency.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int subarraySum(
    vector<int>& nums,
    int k
) {
    unordered_map<int,int> freq;

    freq[0] = 1;

    int prefix = 0;
    int answer = 0;

    for (int x : nums) {

        prefix += x;

        int needed = prefix - k;

        if (freq.count(needed)) {

            answer += freq[needed];
        }

        freq[prefix]++;
    }

    return answer;
}

int main() {

    vector<int> nums = {
        1,1,1
    };

    cout << subarraySum(nums, 2)
         << endl;

    return 0;
}
```

---

# 15. CP Pattern 7: Redis Counter Mental Model

## Real-System Type

```text
Build page-view counters.
```

Example:

```text
user visits page
```

Operation:

```cpp
counter[pageId]++;
```

This is exactly:

```text
Redis INCR
```

mental model.

## Step-by-Step Approach Before Code

```text
Step 1: Page visit arrives.

Step 2: Increment counter for page ID.

Step 3: Query top pages periodically.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class PageViewCounter {
private:

    unordered_map<string,int> counter;

public:

    void recordVisit(string pageId) {

        counter[pageId]++;
    }

    int getCount(string pageId) {

        return counter[pageId];
    }
};

int main() {

    PageViewCounter engine;

    engine.recordVisit("/home");
    engine.recordVisit("/home");
    engine.recordVisit("/products");

    cout << engine.getCount("/home")
         << endl;

    return 0;
}
```

---

# 16. unordered_map vs map

| Feature | unordered_map | map |
|---|---|---|
| Ordering | no | sorted |
| Average lookup | O(1) | O(log N) |
| Worst-case | O(N) | O(log N) |
| Backed by | hash table | red-black tree |
| Use for | fast counting | ordered queries |

Shortcut:

```text
Need fast lookup only?
→ unordered_map

Need sorted order/range query?
→ map
```

---

# 17. Common Mistakes

## Mistake 1: Using map When Ordering Not Needed

This adds unnecessary:

```text
O(log N)
```

instead of:

```text
O(1)
```

---

## Mistake 2: Forgetting freq[0] = 1

For prefix problems:

```cpp
freq[0] = 1;
```

is critical.

It handles:

```text
subarray starting at index 0
```

---

## Mistake 3: Modifying Map During Iteration

Be careful:

```cpp
for (auto [k,v] : map)
```

copies values.

Use:

```cpp
for (auto& [k,v] : map)
```

if modification needed.

---

## Mistake 4: Ignoring Hash Collision Worst Case

Average:

```text
O(1)
```

Worst case:

```text
O(N)
```

But acceptable in most CP/interview contexts.

---

# 18. Complexity Table

| Operation | Complexity |
|---|---:|
| unordered_map insert | O(1) avg |
| unordered_map lookup | O(1) avg |
| map insert | O(log N) |
| map lookup | O(log N) |
| frequency build | O(N) |
| top-k heap | O(N log N) |

---

# 19. Real-World Mapping

| Frequency Concept | Real-System Meaning |
|---|---|
| frequency map | analytics counters |
| top-k frequent | trending topics |
| duplicate detection | fraud detection |
| prefix frequency | cumulative analytics |
| hash lookup | Redis/cache |
| page counters | monitoring dashboards |
| anagram grouping | signature indexing |
| stream counts | telemetry analytics |

---

# 20. Final Mental Model

Frequency counting is:

```text
real-time counting + lookup engine
```

Best for:

```text
analytics
counters
hash lookup
top-k
duplicates
stream processing
```

One-line CP rule:

```text
If problem repeatedly asks “how many?” or “seen before?”, think hash frequency.
```

One-line system rule:

```text
Frequency engines power counters, analytics dashboards, Redis-style metrics, and real-time monitoring.
```

---

# 21. Next Step

Next file:

```text
025_LazyDeletion_Heap_Set_Engine.md
```

This completes advanced heap + stream analytics patterns.
