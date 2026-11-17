# AlgoZenith — Running Mean, Median & Mode (STL)
## Visual Self-Learning Version

> **Core idea:** Maintain three synchronized views of the same collection:
> - **Mean** → `sum + count`
> - **Median** → two balanced ordered halves
> - **Mode** → frequency map + ordered ranking

# Table of Contents

- [0. Prerequisites](#0-prerequisites)
- [1. Problem](#1-problem)
- [2. Big Picture](#2-big-picture)
- [3. Mean — Step by Step](#3-mean--step-by-step)
- [4. Median — Why Two Halves](#4-median--why-two-halves)
- [5. Median Insert — Step by Step](#5-median-insert--step-by-step)
- [6. Median Rebalance — Step by Step](#6-median-rebalance--step-by-step)
- [7. Median Remove — Step by Step](#7-median-remove--step-by-step)
- [8. Median Query — Step by Step](#8-median-query--step-by-step)
- [9. Mode — Why Frequency Ranking](#9-mode--why-frequency-ranking)
- [10. Mode Update — Step by Step](#10-mode-update--step-by-step)
- [11. Full insert(x) Flow](#11-full-insertx-flow)
- [12. Full remove(x) Flow](#12-full-removex-flow)
- [13. Full Dry Run](#13-full-dry-run)
- [14. Complete C++17](#14-complete-c17)
- [15. Complexity](#15-complexity)
- [16. Common Mistakes](#16-common-mistakes)
- [17. Recognition Checklist](#17-recognition-checklist)
- [18. Final Mental Model](#18-final-mental-model)

---

# 0. Prerequisites

## 0.1 Mean

For:

```text
[10, 20, 30]
```

Mean:

```text
sum / count
= 60 / 3
= 20
```

So a dynamic mean only needs:

```text
totalSum
count
```

---

## 0.2 Median

Median is the middle value after sorting.

Odd count:

```text
[1, 3, 7]
median = 3
```

Even count:

```text
[1, 3, 7, 9]
middle values = 3 and 7
median = (3 + 7) / 2
```

---

## 0.3 Mode

Mode is the most frequent value.

```text
[2, 2, 5, 7]

freq:
2 -> 2
5 -> 1
7 -> 1

mode = 2
```

Tie rule in this note:

```text
same frequency
-> smaller value wins
```

---

## 0.4 multiset

Use `multiset` when we need:

```text
sorted values
+
duplicates
+
erase one occurrence
```

Useful operations:

```cpp
*s.begin();          // minimum
*s.rbegin();         // maximum
s.find(x);           // one occurrence
s.erase(it);         // erase one occurrence
```

---

## 0.5 Ordered Pair Ranking

Pairs compare by:

```text
first
then second
```

So storing:

```text
{-frequency, value}
```

gives:

```text
higher frequency first
smaller value first on tie
```

Example:

```text
(-3,2)
(-3,5)
(-2,1)
```

The first pair is the mode candidate.

---

## 0.6 Modular Division

The problem uses:

```text
MOD = 1e9+7
```

So:

```text
a / b
```

is represented as:

```text
a × inverse(b) mod MOD
```

For `2`:

```text
inverse(2) = 500000004
```

---

# 1. Problem

Support:

```text
insert(x)
remove(x)
getMean()
getMedian()
getMode()
```

Empty collection query:

```text
-1
```

---

# 2. Big Picture

```text
                       COLLECTION
                           |
             +-------------+-------------+
             |             |             |
             v             v             v
           MEAN          MEDIAN         MODE
             |             |             |
             v             v             v
       sum + count     two halves    freq + ranking
```

Every update must keep all three summaries synchronized.

---

# 3. Mean — Step by Step

Maintain:

```text
totalSum
count
```

Insert:

```text
totalSum += x
count++
```

Remove:

```text
totalSum -= x
count--
```

Query:

```text
mean = totalSum / count
```

Modulo:

```text
mean = totalSum × inverse(count) mod MOD
```

ASCII:

```text
INSERT x
   |
   +--> sum += x
   +--> count++

REMOVE x
   |
   +--> sum -= x
   +--> count--

QUERY
   |
   v
sum / count
```

---

# 4. Median — Why Two Halves

Split values into:

```text
LEFT  = smaller half
RIGHT = larger half
```

Use:

```cpp
multiset<int> leftSet;
multiset<int> rightSet;
```

Visual:

```text
LEFT                          RIGHT

[1, 2, 3]        |          [7, 9]
       ^                        ^
       |                        |
   max(left)                min(right)
```

Maintain two invariants:

```text
1. max(left) <= min(right)

2. left.size() == right.size()
   OR
   left.size() == right.size() + 1
```

---

# 5. Median Insert — Step by Step

For `insert(x)`:

```text
Step 1:
if left is empty
    insert into left

Step 2:
else if x <= max(left)
    insert into left

Step 3:
else
    insert into right

Step 4:
rebalance
```

Flow:

```text
insert x
   |
   v
x <= max(left)?
 /            \
yes            no
 |              |
 v              v
LEFT           RIGHT
  \            /
   \          /
      rebalance
```

---

# 6. Median Rebalance — Step by Step

If left is too large:

```text
left.size() > right.size() + 1
```

move:

```text
max(left) -> right
```

ASCII:

```text
LEFT                    RIGHT
[1,2,3,4]      |        [7,9]
       ^
       |
    move 4 -------------------->

Result:
[1,2,3]        |        [4,7,9]
```

If right is larger:

```text
right.size() > left.size()
```

move:

```text
min(right) -> left
```

ASCII:

```text
LEFT                     RIGHT
[1,2]          |         [3,7,9]
                          ^
                          |
                  <-------+ move 3

Result:
[1,2,3]        |         [7,9]
```

---

# 7. Median Remove — Step by Step

Remove one occurrence only.

```text
Step 1:
try leftSet.find(x)

Step 2:
if found -> erase iterator

Step 3:
otherwise erase one from right

Step 4:
rebalance
```

Correct duplicate removal:

```cpp
auto it = leftSet.find(x);
if (it != leftSet.end()) {
    leftSet.erase(it);
}
```

Do not use:

```cpp
leftSet.erase(x);
```

because it removes all copies.

---

# 8. Median Query — Step by Step

Odd count:

```text
left has one extra
median = max(left)
```

ASCII:

```text
LEFT            RIGHT
[1,2,3]    |    [7,9]
      ^
      |
    median
```

Even count:

```text
a = max(left)
b = min(right)
median = (a+b)/2
```

ASCII:

```text
LEFT            RIGHT
[1,2]      |    [3,7]
   ^             ^
   |             |
   +------++-----+
          ||
          \/
      average
```

Modulo:

```text
median = (a+b) × inverse(2) mod MOD
```

---

# 9. Mode — Why Frequency Ranking

Maintain:

```cpp
map<int,int> freq;
set<pair<int,int>> modeTracker;
```

Store each active value as:

```text
{-freq[x], x}
```

Example:

```text
value   freq   pair
5        3     (-3,5)
8        3     (-3,8)
2        2     (-2,2)
```

Set order:

```text
(-3,5)  <- begin()
(-3,8)
(-2,2)
```

So automatically:

```text
higher frequency wins
then smaller value wins
```

---

# 10. Mode Update — Step by Step

Suppose:

```text
freq[5] = 2
```

Current rank:

```text
(-2,5)
```

Insert another `5`:

```text
Step 1: erase (-2,5)
Step 2: freq 2 -> 3
Step 3: insert (-3,5)
```

ASCII:

```text
(-2,5)
   |
 erase
   |
   v
freq 2 -> 3
   |
   v
insert (-3,5)
```

For remove:

```text
erase old rank
freq--
if freq > 0 -> insert new rank
else erase value completely
```

---

# 11. Full insert(x) Flow

```text
                        INSERT x
                           |
             +-------------+-------------+
             |             |             |
             v             v             v
           MEAN          MEDIAN         MODE
             |             |             |
             v             v             v
        sum += x       choose half    erase old rank
        count++        insert x       freq[x]++
                       rebalance      insert new rank
```

Steps:

```text
1. Update totalSum
2. Increment count
3. Insert x into correct median half
4. Rebalance halves
5. Erase old mode rank if present
6. Increment freq[x]
7. Insert new mode rank
```

---

# 12. Full remove(x) Flow

```text
                        REMOVE x
                           |
             +-------------+-------------+
             |             |             |
             v             v             v
           MEAN          MEDIAN         MODE
             |             |             |
             v             v             v
        sum -= x       erase one x    erase old rank
        count--        rebalance      freq[x]--
                                     reinsert if > 0
```

Steps:

```text
1. Check x exists
2. Update totalSum
3. Decrement count
4. Erase one x from median sets
5. Rebalance
6. Erase old mode rank
7. Decrement freq[x]
8. Reinsert new rank if freq > 0
9. Otherwise erase x from freq
```

---

# 13. Full Dry Run

Operations:

```text
insert(2)
insert(5)
insert(2)
remove(2)
```

## insert(2)

```text
collection = [2]

sum/count = 2/1

LEFT | RIGHT
[2]  | []

freq: 2 -> 1
rank: (-1,2)

mean=2, median=2, mode=2
```

## insert(5)

```text
collection = [2,5]

sum/count = 7/2

LEFT | RIGHT
[2]  | [5]

freq:
2 -> 1
5 -> 1

rank:
(-1,2)  <- smaller wins tie
(-1,5)

median = 7/2
mode   = 2
```

## insert(2)

```text
collection = [2,2,5]

LEFT   | RIGHT
[2,2]  | [5]

freq:
2 -> 2
5 -> 1

rank:
(-2,2)
(-1,5)

mean   = 3
median = 2
mode   = 2
```

## remove(2)

```text
collection = [2,5]

LEFT | RIGHT
[2]  | [5]

freq:
2 -> 1
5 -> 1

rank:
(-1,2)
(-1,5)

mean   = 7/2
median = 7/2
mode   = 2
```

---

# 14. Complete C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

constexpr long long MOD = 1000000007LL;
constexpr long long INV_TWO = 500000004LL;

long long normalize(long long x) {
    x %= MOD;

    if (x < 0) {
        x += MOD;
    }

    return x;
}

long long power(long long a, long long b) {
    long long ans = 1;
    a = normalize(a);

    while (b > 0) {
        if (b & 1LL) {
            ans = ans * a % MOD;
        }

        a = a * a % MOD;
        b >>= 1LL;
    }

    return ans;
}

long long modInverse(long long x) {
    return power(x, MOD - 2);
}

class MetricsEngine {
    long long totalSum = 0;
    int count = 0;

    multiset<int> leftSet, rightSet;
    map<int, int> freq;
    set<pair<int, int>> modeTracker;

    void balance() {
        if (leftSet.size() > rightSet.size() + 1) {
            auto it = prev(leftSet.end());
            rightSet.insert(*it);
            leftSet.erase(it);
        } else if (rightSet.size() > leftSet.size()) {
            auto it = rightSet.begin();
            leftSet.insert(*it);
            rightSet.erase(it);
        }
    }

public:
    void insert(int x) {
        totalSum = (totalSum + normalize(x)) % MOD;
        count++;

        if (leftSet.empty() || x <= *leftSet.rbegin()) {
            leftSet.insert(x);
        } else {
            rightSet.insert(x);
        }

        balance();

        int oldFreq = freq[x];

        if (oldFreq > 0) {
            modeTracker.erase({-oldFreq, x});
        }

        freq[x]++;
        modeTracker.insert({-freq[x], x});
    }

    void remove(int x) {
        if (!freq.count(x)) {
            return;
        }

        totalSum = (totalSum - normalize(x) + MOD) % MOD;
        count--;

        auto it = leftSet.find(x);

        if (it != leftSet.end()) {
            leftSet.erase(it);
        } else {
            it = rightSet.find(x);
            rightSet.erase(it);
        }

        balance();

        int oldFreq = freq[x];
        modeTracker.erase({-oldFreq, x});

        freq[x]--;

        if (freq[x] == 0) {
            freq.erase(x);
        } else {
            modeTracker.insert({-freq[x], x});
        }
    }

    long long getMean() const {
        if (count == 0) {
            return -1;
        }

        return totalSum * modInverse(count) % MOD;
    }

    long long getMedian() const {
        if (count == 0) {
            return -1;
        }

        if (leftSet.size() > rightSet.size()) {
            return normalize(*leftSet.rbegin());
        }

        long long a = *leftSet.rbegin();
        long long b = *rightSet.begin();

        return normalize(a + b) * INV_TWO % MOD;
    }

    int getMode() const {
        if (count == 0) {
            return -1;
        }

        return modeTracker.begin()->second;
    }
};
```

---

# 15. Complexity

Let:

```text
N = number of active occurrences
D = number of distinct values
```

| Operation | Time |
|---|---:|
| `insert` | `O(log N + log D)` |
| `remove` | `O(log N + log D)` |
| `getMean` | `O(log MOD)` |
| `getMedian` | `O(1)` |
| `getMode` | `O(1)` |
| Space | `O(N)` |

---

# 16. Common Mistakes

## 16.1 Remove All Copies from multiset

Wrong:

```cpp
leftSet.erase(x);
```

Correct:

```cpp
auto it = leftSet.find(x);
if (it != leftSet.end()) {
    leftSet.erase(it);
}
```

---

## 16.2 Forget Median Ordering

Need:

```text
max(left) <= min(right)
```

Size balancing alone is not enough.

---

## 16.3 Leave Old Mode Rank

Always:

```text
erase old {-freq,x}
change freq
insert new {-freq,x}
```

---

## 16.4 Keep Zero-Frequency Values

If:

```text
freq[x] == 0
```

remove `x` from the active frequency state.

---

## 16.5 Use Normal Integer Division

This note uses modular fractions.

Use modular inverse.

---

## 16.6 Apply Modulo Before Median Ordering

Median sets must store the original values.

Modulo is only for the final fractional answer.

---

## 16.7 Forget Synchronization

Every update must change:

```text
MEAN
MEDIAN
MODE
```

---

# 17. Recognition Checklist

Think of this pattern when you see:

```text
dynamic multiset
+
insert/remove
+
mean/median/mode queries
```

Ask:

```text
1. Mean state?
   -> sum + count

2. Median state?
   -> two ordered halves

3. Median ordering?
   -> max(left) <= min(right)

4. Median size invariant?
   -> left == right
      OR left == right + 1

5. Remove duplicate?
   -> erase one iterator

6. Mode state?
   -> freq[x]

7. Mode tie-break?
   -> {-freq, value}

8. Frequency changed?
   -> erase old rank first

9. Frequency became zero?
   -> remove from ranking

10. Did all three views update?
```

---

# 18. Final Mental Model

```text
                       ONE COLLECTION
                           |
             +-------------+-------------+
             |             |             |
             v             v             v
           MEAN          MEDIAN         MODE
             |             |             |
             v             v             v
       sum + count     two halves     freq ranking
```

Median:

```text
LEFT                         RIGHT
smaller values             larger values

max(left)       <=        min(right)
```

Mode:

```text
value x
   |
   v
freq[x]
   |
   v
{-freq[x], x}
   |
   v
ordered set
   |
   v
begin() = mode
```

Every update:

```text
INSERT / REMOVE
       |
       +--> update sum + count
       |
       +--> update median halves
       |         |
       |         +--> rebalance
       |
       +--> erase old mode rank
                 |
                 +--> change frequency
                 |
                 +--> insert new rank
```

Remember:

```text
MEAN   -> sum + count
MEDIAN -> two balanced ordered halves
MODE   -> freq[x] + {-freq[x],x}
```

The one sentence to remember:

```text
Maintain three synchronized summaries:
arithmetic state for mean,
two ordered halves for median,
and frequency ranking for mode.
```
