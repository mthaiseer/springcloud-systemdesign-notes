# AlgoZenith --- LFU Cache (STL)

> **Core idea:** LFU makes a **two-level eviction decision**: lowest
> frequency first, then LRU inside that frequency.

## TOC

-   [1. Problem](#1-problem)
-   [2. LRU vs LFU](#2-lru-vs-lfu)
-   [3. STL Structures](#3-stl-structures)
-   [4. State](#4-state)
-   [5. Promote](#5-promote)
-   [6. get](#6-get)
-   [7. put](#7-put)
-   [8. Eviction](#8-eviction)
-   [9. Dry Run](#9-dry-run)
-   [10. Complete C++](#10-complete-c)
-   [11. Complexity & Mistakes](#11-complexity--mistakes)
-   [12. Mental Model](#12-mental-model)

------------------------------------------------------------------------

# 1. Problem

``` text
get(key)       → value + frequency++
put(existing)  → update value + frequency++
put(new)       → frequency = 1
```

When full:

``` text
lowest frequency
       ↓
if tie
       ↓
least recently used
       ↓
evict
```

Target: average `O(1)` for `get` and `put`.

------------------------------------------------------------------------

# 2. LRU vs LFU

``` text
LRU:
Who was used longest ago?

LFU:
1. Who has minimum frequency?
2. Among them, who is LRU?
```

Example:

``` text
A → freq 3
B → freq 1
C → freq 1
```

First choose `{B,C}`, then evict the older one.

------------------------------------------------------------------------

# 3. STL Structures

We need **three pieces of state**.

### 1. Key Map

``` cpp
unordered_map<int, Node> keyMap;
```

Each node stores:

``` text
key
value
frequency
iterator into its frequency list
```

### 2. Frequency Buckets

``` cpp
unordered_map<long long, list<int>> freqMap;
```

``` text
freqMap[f] = keys having frequency f

front = MRU
back  = LRU
```

Example:

``` text
freq 1 : [C, B]
          MRU LRU

freq 2 : [A]
```

### 3. Minimum Frequency

``` cpp
long long minFreq;
```

Victim is immediately:

``` cpp
freqMap[minFreq].back();
```

------------------------------------------------------------------------

# 4. State

``` cpp
struct Node {
    int key;
    int value;
    long long freq;
    list<int>::iterator position;
};
```

Invariants:

``` text
one key → exactly one bucket

node.freq
=
bucket frequency

node.position
=
its exact list position

bucket front = MRU
bucket back  = LRU

minFreq
=
lowest occupied frequency
```

------------------------------------------------------------------------

# 5. Promote

Suppose key `A` has frequency `f`.

A successful use means:

``` text
f → f+1
```

### Step 1 --- Remove from old bucket

``` cpp
freqMap[f].erase(node.position);
```

### Step 2 --- Old bucket empty?

Erase it.

If:

``` text
f == minFreq
```

then:

``` text
minFreq = f+1
```

Why is `f+1` guaranteed?

``` text
The promoted key itself
just moved to f+1.
```

### Step 3 --- Increase frequency

``` cpp
node.freq++;
```

### Step 4 --- Insert as MRU in new bucket

``` cpp
freqMap[node.freq].push_front(node.key);
```

### Step 5 --- Save new iterator

``` cpp
node.position = freqMap[node.freq].begin();
```

Example:

``` text
Before:

freq 1 : [B, A]
freq 2 : [C]

get(A)

A: 1 → 2

After:

freq 1 : [B]
freq 2 : [A, C]
          MRU LRU
```

------------------------------------------------------------------------

# 6. get

``` text
find key
   ↓
missing? → -1
   ↓
promote f → f+1
   ↓
return value
```

``` cpp
int get(int key) {
    auto it = keyMap.find(key);

    if (it == keyMap.end())
        return -1;

    promote(it->second);

    return it->second.value;
}
```

------------------------------------------------------------------------

# 7. put

## Existing Key

``` text
update value
   ↓
promote
```

``` cpp
if (it != keyMap.end()) {
    it->second.value = value;
    promote(it->second);
    return;
}
```

Existing `put` counts as a use.

## New Key

New key always starts:

``` text
frequency = 1
```

``` cpp
freqMap[1].push_front(key);
```

Then:

``` text
minFreq = 1
```

------------------------------------------------------------------------

# 8. Eviction

When full, **evict before inserting** the new key.

``` text
minFreq
   ↓
freqMap[minFreq]
   ↓
back()
   ↓
LFU + LRU victim
```

``` cpp
auto &bucket = freqMap[minFreq];

int victim = bucket.back();

bucket.pop_back();
keyMap.erase(victim);
```

Then insert the new key at frequency `1`.

------------------------------------------------------------------------

# 9. Dry Run

``` text
capacity = 2
```

### `put(1,10)`

``` text
freq 1 : [1]
minFreq = 1
```

### `put(2,20)`

Newer key goes to front:

``` text
freq 1 : [2,1]
          MRU LRU

minFreq = 1
```

### `get(1)`

Promote `1`:

``` text
1 : freq 1 → 2
```

Result:

``` text
freq 1 : [2]
freq 2 : [1]

minFreq = 1
return 10
```

### `put(3,30)`

Cache full.

``` text
minFreq = 1
freq 1  = [2]
```

Victim:

``` text
back() = 2
```

Evict `2`, insert `3`:

``` text
freq 1 : [3]
freq 2 : [1]

minFreq = 1
```

### `get(3)`

``` text
3 : freq 1 → 2
```

Frequency `1` becomes empty, so:

``` text
minFreq: 1 → 2
```

New bucket order:

``` text
freq 2 : [3,1]
          MRU LRU
```

### `put(4,40)`

Cache full.

Both keys have frequency `2`:

``` text
freq 2 : [3,1]
          MRU LRU
```

Tie → evict LRU:

``` text
victim = 1
```

Insert `4`:

``` text
freq 1 : [4]
freq 2 : [3]

minFreq = 1
```

Final:

``` text
4 → value 40, freq 1
3 → value 30, freq 2
```

**Frequency chooses the bucket; recency chooses the victim.**

------------------------------------------------------------------------

# 10. Complete C++

``` cpp
#include <bits/stdc++.h>
using namespace std;

class LFUCache {
    struct Node {
        int key;
        int value;
        long long freq;
        list<int>::iterator position;
    };

    int capacity;
    long long minFreq = 0;

    unordered_map<int, Node> keyMap;

    // front = MRU, back = LRU
    unordered_map<long long, list<int>> freqMap;

    void promote(Node &node) {
        long long oldFreq = node.freq;

        auto bucketIt = freqMap.find(oldFreq);

        // Remove from old frequency bucket.
        bucketIt->second.erase(node.position);

        // Remove empty bucket.
        if (bucketIt->second.empty()) {
            freqMap.erase(bucketIt);

            if (minFreq == oldFreq)
                minFreq = oldFreq + 1;
        }

        // Move f -> f+1.
        node.freq++;

        auto &newBucket = freqMap[node.freq];

        // Just used => MRU in new bucket.
        newBucket.push_front(node.key);

        // Old iterator was erased; save new one.
        node.position = newBucket.begin();
    }

public:
    LFUCache(int cap)
        : capacity(max(0, cap)) {}

    int get(int key) {
        auto it = keyMap.find(key);

        if (it == keyMap.end())
            return -1;

        promote(it->second);

        return it->second.value;
    }

    void put(int key, int value) {
        if (capacity == 0)
            return;

        auto it = keyMap.find(key);

        // Existing key: update + use.
        if (it != keyMap.end()) {
            it->second.value = value;
            promote(it->second);
            return;
        }

        // Full: evict LFU, then LRU tie-break.
        if ((int)keyMap.size() == capacity) {
            auto bucketIt = freqMap.find(minFreq);

            int victim = bucketIt->second.back();

            bucketIt->second.pop_back();
            keyMap.erase(victim);

            if (bucketIt->second.empty())
                freqMap.erase(bucketIt);
        }

        // New key starts at frequency 1.
        auto &bucket = freqMap[1];

        bucket.push_front(key);

        keyMap.emplace(
            key,
            Node{key, value, 1, bucket.begin()}
        );

        minFreq = 1;
    }
};
```

------------------------------------------------------------------------

# 11. Complexity & Mistakes

  Operation                       Complexity
  ------------------------- ----------------
  `get`                       Average `O(1)`
  `put`                       Average `O(1)`
  Known list erase                    `O(1)`
  `push_front / pop_back`             `O(1)`
  Space                        `O(capacity)`

Why no scan?

``` text
keyMap      → find key
minFreq     → find LFU bucket
bucket.back → LRU tie-break
iterator    → remove key directly
```

Common mistakes:

``` text
evict any min-frequency key          ✗
increase minFreq after every get     ✗
keep erased list iterator            ✗
existing put doesn't promote         ✗
insert new key before eviction       ✗
forget empty-bucket cleanup          ✗
```

Important rule:

``` text
minFreq changes during promotion ONLY if:

oldFreq == minFreq
AND
old bucket becomes empty
```

------------------------------------------------------------------------

# 12. Mental Model

``` text
             keyMap
        key → Node
              │
              ├── value
              ├── freq
              └── iterator
                    ↓

freqMap:

freq 1 : [A] [B]     ← minFreq
          MRU LRU

freq 2 : [C] [D]
          MRU LRU

freq 3 : [E]
```

Promotion:

``` text
USE KEY

bucket f
   ↓ erase
frequency++
   ↓
bucket f+1
   ↓
push_front
```

Eviction:

``` text
minFreq
   ↓
minimum-frequency bucket
   ↓
back()
   ↓
LRU inside that bucket
   ↓
EVICT
```

> **Remember:** **LFU = frequency chooses the bucket; LRU chooses the
> victim inside that bucket.**
