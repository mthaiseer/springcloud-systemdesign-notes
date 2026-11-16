# AlgoZenith — LFU Cache (STL)
## Visual Self-Learning Version

> **Core idea:** LFU makes a two-level eviction decision:
> 1. lowest frequency first
> 2. if frequencies tie, evict the LRU key inside that frequency.

# Table of Contents

- [0. Prerequisites](#0-prerequisites)
- [1. Problem](#1-problem)
- [2. LRU vs LFU](#2-lru-vs-lfu)
- [3. Why Multiple Structures Are Needed](#3-why-multiple-structures-are-needed)
- [4. STL Structures](#4-stl-structures)
- [5. State and Invariants](#5-state-and-invariants)
- [6. Promotion — Step by Step](#6-promotion--step-by-step)
- [7. get(key) — Step by Step](#7-getkey--step-by-step)
- [8. put(key,value) — Existing Key](#8-putkeyvalue--existing-key)
- [9. put(key,value) — New Key](#9-putkeyvalue--new-key)
- [10. Eviction — Step by Step](#10-eviction--step-by-step)
- [11. Full Dry Run](#11-full-dry-run)
- [12. Complete C++17](#12-complete-c17)
- [13. Complexity](#13-complexity)
- [14. Common Mistakes](#14-common-mistakes)
- [15. Recognition Checklist](#15-recognition-checklist)
- [16. Final Mental Model](#16-final-mental-model)

---

# 0. Prerequisites

## 0.1 Frequency

Frequency means:

```text
how many times a key has been used
```

Example:

```text
A -> 3
B -> 1
C -> 2
```

LFU first prefers the smallest frequency.

---

## 0.2 Recency

If two keys have the same frequency, LFU needs a tie-break.

Use:

```text
Least Recently Used
```

So each frequency bucket must also maintain:

```text
MRU -> LRU
```

---

## 0.3 list

For one frequency:

```text
freq 2:

MRU               LRU
 |                   |
 v                   v

[A] <-> [C] <-> [B]
```

Useful operations:

```text
push_front()  -> O(1)
pop_back()    -> O(1)
erase(it)     -> O(1)
```

---

## 0.4 Iterator

An iterator points directly to a list node.

```text
[A] <-> [C] <-> [B]
         ^
         |
      iterator
```

This lets us remove a key from its old frequency bucket without scanning.

---

## 0.5 unordered_map

Need fast key lookup:

```cpp
unordered_map<int, Node> keyMap;
```

From a key we can immediately get:

```text
value
frequency
list position
```

---

## 0.6 minFreq

If we scanned all frequency buckets to find the smallest frequency, operations would not stay O(1).

So maintain:

```cpp
long long minFreq;
```

Meaning:

```text
minimum occupied frequency
```

---

# 1. Problem

Support:

```text
get(key)
put(key, value)
```

Rules:

```text
get(key)
-> return value
-> frequency++

put(existing key)
-> update value
-> frequency++

put(new key)
-> frequency = 1
```

When full:

```text
lowest frequency
      |
      v
if tie
      |
      v
least recently used
      |
      v
evict
```

Target:

```text
get -> average O(1)
put -> average O(1)
```

---

# 2. LRU vs LFU

## LRU

```text
Who was used longest ago?
```

One global recency list is enough.

## LFU

```text
1. Which frequency is smallest?
2. Inside that frequency, who is LRU?
```

So LFU needs:

```text
frequency
+
recency inside each frequency
```

ASCII:

```text
freq 1 : [B] <-> [C]
          MRU      LRU

freq 2 : [A]

freq 3 : [D]
```

Eviction:

```text
minimum frequency = 1
candidate bucket  = {B,C}
LRU in bucket     = C

evict C
```

---

# 3. Why Multiple Structures Are Needed

We need four abilities.

```text
Find key quickly
      |
      v
   keyMap


Group keys by frequency
      |
      v
   freqMap


Maintain MRU/LRU in each frequency
      |
      v
   list<int>


Find smallest occupied frequency
      |
      v
   minFreq
```

And for direct bucket removal:

```text
Node stores list iterator
```

---

# 4. STL Structures

## 4.1 Node

```cpp
struct Node {
    int key;
    int value;
    long long freq;
    list<int>::iterator position;
};
```

---

## 4.2 Key Map

```cpp
unordered_map<int, Node> keyMap;
```

Meaning:

```text
key -> Node
```

---

## 4.3 Frequency Buckets

```cpp
unordered_map<long long, list<int>> freqMap;
```

Meaning:

```text
freqMap[f]
=
keys with frequency f
```

Inside every bucket:

```text
front = MRU
back  = LRU
```

---

## 4.4 Minimum Frequency

```cpp
long long minFreq;
```

Victim is:

```cpp
freqMap[minFreq].back();
```

because:

```text
minFreq chooses bucket
back chooses LRU inside bucket
```

---

# 5. State and Invariants

Always keep these true:

```text
1. one key appears in exactly one frequency bucket

2. node.freq equals that bucket's frequency

3. node.position points to that exact list node

4. bucket front = MRU

5. bucket back = LRU

6. minFreq = lowest occupied frequency

7. keyMap.size() <= capacity
```

ASCII:

```text
                    keyMap
                key -> Node
                     |
        +------------+------------+
        |            |            |
        v            v            v
      value         freq       iterator
                                  |
                                  v

freqMap:

freq 1 : [A] <-> [B]   <- minFreq
          MRU      LRU

freq 2 : [C] <-> [D]
          MRU      LRU
```

---

# 6. Promotion — Step by Step

Promotion is the main operation in LFU.

A successful use means:

```text
frequency f
    |
    v
frequency f+1
```

Suppose:

```text
freq 1 : [B, A]
freq 2 : [C]
```

Call:

```text
get(A)
```

---

## Step 1 — Remove from old bucket

```cpp
freqMap[f].erase(node.position);
```

After:

```text
freq 1 : [B]
```

---

## Step 2 — If old bucket becomes empty, erase it

```text
empty bucket
-> remove bucket
```

---

## Step 3 — Update minFreq only when required

Only if:

```text
f == minFreq
AND
old bucket became empty
```

then:

```text
minFreq = f + 1
```

Why is `f+1` valid?

Because the promoted key itself is moving to frequency `f+1`.

---

## Step 4 — Increase frequency

```cpp
node.freq++;
```

---

## Step 5 — Insert as MRU in new bucket

```cpp
freqMap[node.freq].push_front(node.key);
```

Because the key was just used.

---

## Step 6 — Save new iterator

```cpp
node.position = freqMap[node.freq].begin();
```

The old iterator is no longer valid.

---

## ASCII Promotion

Before:

```text
freq 1:
[B] <-> [A]
          ^
          |
       use A

freq 2:
[C]
```

Remove A:

```text
freq 1:
[B]
```

Increase:

```text
A: 1 -> 2
```

Insert at front:

```text
freq 2:
[A] <-> [C]
 ^         ^
MRU       LRU
```

Final:

```text
freq 1 : [B]
freq 2 : [A,C]
```

---

# 7. get(key) — Step by Step

```text
Step 1:
find key

Step 2:
if missing -> return -1

Step 3:
promote frequency f -> f+1

Step 4:
return value
```

Flow:

```text
get(key)
   |
   v
find in keyMap
   |
   +---- missing ----> -1
   |
   v
promote
   |
   v
return value
```

C++:

```cpp
int get(int key) {
    auto it = keyMap.find(key);

    if (it == keyMap.end()) {
        return -1;
    }

    promote(it->second);

    return it->second.value;
}
```

---

# 8. put(key,value) — Existing Key

Existing `put` counts as a use.

```text
update value
    |
    v
promote frequency
```

Example:

```text
freq 1 : [B]
freq 2 : [A]

put(B,200)
```

Update value, then:

```text
B: 1 -> 2
```

New order:

```text
freq 2 : [B,A]
          MRU LRU
```

C++:

```cpp
if (it != keyMap.end()) {
    it->second.value = value;
    promote(it->second);
    return;
}
```

---

# 9. put(key,value) — New Key

A new key always starts at:

```text
frequency = 1
```

Process:

```text
Step 1:
if capacity == 0 -> return

Step 2:
if full -> evict first

Step 3:
push new key to front of freq 1 bucket

Step 4:
create Node

Step 5:
minFreq = 1
```

ASCII:

```text
new key D

before:
freq 2 : [A]
freq 3 : [C]

after:
freq 1 : [D]
freq 2 : [A]
freq 3 : [C]

minFreq = 1
```

---

# 10. Eviction — Step by Step

When full:

```text
frequency chooses bucket
recency chooses victim
```

Suppose:

```text
minFreq = 2
```

Bucket:

```text
freq 2:
[C] <-> [A] <-> [B]
 ^                 ^
MRU               LRU
```

Victim:

```text
B
```

because:

```text
frequency = minimum
and
B is LRU inside that bucket
```

Eviction flow:

```text
minFreq
   |
   v
freqMap[minFreq]
   |
   v
back()
   |
   v
victim
   |
   +--> erase from keyMap
   |
   +--> pop_back from bucket
```

---

# 11. Full Dry Run

```text
capacity = 2
```

## Step 1 — put(1,10)

```text
freq 1 : [1]
minFreq = 1
```

## Step 2 — put(2,20)

Newer key goes to front:

```text
freq 1 : [2,1]
          MRU LRU

minFreq = 1
```

## Step 3 — get(1)

Promote:

```text
1: 1 -> 2
```

State:

```text
freq 1 : [2]
freq 2 : [1]

minFreq = 1
```

Return:

```text
10
```

## Step 4 — put(3,30)

Cache full.

```text
minFreq = 1
freq 1  = [2]
```

Victim:

```text
2
```

Evict 2, insert 3:

```text
freq 1 : [3]
freq 2 : [1]

minFreq = 1
```

## Step 5 — get(3)

Promote:

```text
3: 1 -> 2
```

Frequency 1 becomes empty.

Because:

```text
oldFreq == minFreq
```

update:

```text
minFreq = 2
```

New bucket:

```text
freq 2 : [3,1]
          MRU LRU
```

## Step 6 — put(4,40)

Cache full.

Both keys have frequency 2:

```text
freq 2 : [3,1]
          MRU LRU
```

Tie-break by LRU:

```text
victim = 1
```

Insert 4:

```text
freq 1 : [4]
freq 2 : [3]

minFreq = 1
```

Final:

```text
4 -> value 40, freq 1
3 -> value 30, freq 2
```

Key sentence:

```text
Frequency chooses the bucket.
Recency chooses the victim.
```

---

# 12. Complete C++17

```cpp
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

    void promote(Node& node) {
        long long oldFreq = node.freq;

        auto bucketIt = freqMap.find(oldFreq);

        // Remove from old frequency bucket.
        bucketIt->second.erase(node.position);

        // Old bucket became empty.
        if (bucketIt->second.empty()) {
            freqMap.erase(bucketIt);

            if (minFreq == oldFreq) {
                minFreq = oldFreq + 1;
            }
        }

        // Move f -> f+1.
        node.freq++;

        auto& newBucket = freqMap[node.freq];

        // Just used -> MRU.
        newBucket.push_front(node.key);

        // Save new iterator.
        node.position = newBucket.begin();
    }

public:
    LFUCache(int cap)
        : capacity(max(0, cap)) {}

    int get(int key) {
        auto it = keyMap.find(key);

        if (it == keyMap.end()) {
            return -1;
        }

        promote(it->second);

        return it->second.value;
    }

    void put(int key, int value) {
        if (capacity == 0) {
            return;
        }

        auto it = keyMap.find(key);

        // Existing key -> update + use.
        if (it != keyMap.end()) {
            it->second.value = value;
            promote(it->second);
            return;
        }

        // Full -> LFU, then LRU tie-break.
        if ((int)keyMap.size() == capacity) {
            auto bucketIt = freqMap.find(minFreq);

            int victim = bucketIt->second.back();

            bucketIt->second.pop_back();
            keyMap.erase(victim);

            if (bucketIt->second.empty()) {
                freqMap.erase(bucketIt);
            }
        }

        // New key starts at frequency 1.
        auto& bucket = freqMap[1];

        bucket.push_front(key);

        keyMap.emplace(
            key,
            Node{key, value, 1, bucket.begin()}
        );

        minFreq = 1;
    }
};
```

---

# 13. Complexity

| Operation | Complexity |
|---|---:|
| `get` | average `O(1)` |
| `put` | average `O(1)` |
| known list erase | `O(1)` |
| `push_front / pop_back` | `O(1)` |
| Space | `O(capacity)` |

No scan is needed because:

```text
keyMap
-> find key

minFreq
-> find LFU bucket

bucket.back()
-> choose LRU victim

stored iterator
-> remove key directly
```

---

# 14. Common Mistakes

## 14.1 Evict Any Minimum-Frequency Key

Wrong.

Within the minimum-frequency bucket:

```text
evict LRU
```

So victim is:

```cpp
bucket.back();
```

---

## 14.2 Increase minFreq After Every get

Wrong.

Change it only when:

```text
oldFreq == minFreq
AND
old bucket becomes empty
```

---

## 14.3 Keep Old Iterator After Promotion

Wrong.

The old list node was erased.

Always save:

```cpp
node.position = newBucket.begin();
```

---

## 14.4 Existing put Does Not Promote

Wrong.

Existing put counts as a use:

```text
update
+
frequency++
```

---

## 14.5 Insert New Key Before Eviction

Prefer:

```text
evict first
insert second
```

when cache is full.

---

## 14.6 Forget Empty Bucket Cleanup

If a frequency list becomes empty:

```text
erase that bucket
```

---

## 14.7 Forget MRU/LRU Direction

Inside a frequency:

```text
front = MRU
back  = LRU
```

Promotion:

```text
push_front
```

Eviction:

```text
pop_back
```

---

## 14.8 Scan Frequencies to Find Minimum

That breaks the intended O(1) design.

Maintain:

```text
minFreq
```

---

# 15. Recognition Checklist

Think LFU Cache when you see:

```text
limited capacity
+
usage frequency matters
+
LRU tie-break
+
fast get/put
```

Ask:

```text
1. Fast key lookup?
   -> keyMap

2. Group equal frequencies?
   -> freqMap

3. LRU inside frequency?
   -> list

4. MRU side?
   -> front

5. LRU side?
   -> back

6. Direct removal from bucket?
   -> stored iterator

7. Lowest occupied frequency?
   -> minFreq

8. Successful get?
   -> promote

9. Existing put?
   -> update + promote

10. New put?
    -> evict if full
    -> insert at freq 1
    -> minFreq = 1
```

---

# 16. Final Mental Model

```text
                    keyMap
               key -> Node
                     |
        +------------+------------+
        |            |            |
        v            v            v
      value         freq       iterator
                                  |
                                  v

freqMap:

minFreq
   |
   v

freq 1 : [A] <-> [B]
          MRU      LRU

freq 2 : [C] <-> [D]
          MRU      LRU

freq 3 : [E]
```

Promotion:

```text
USE KEY
   |
   v
remove from bucket f
   |
   v
frequency++
   |
   v
push_front into bucket f+1
   |
   v
save new iterator
```

Eviction:

```text
minFreq
   |
   v
minimum-frequency bucket
   |
   v
back()
   |
   v
LRU inside that bucket
   |
   v
EVICT
```

The one sentence to remember:

```text
LFU = frequency chooses the bucket;
      LRU chooses the victim inside that bucket.
```
