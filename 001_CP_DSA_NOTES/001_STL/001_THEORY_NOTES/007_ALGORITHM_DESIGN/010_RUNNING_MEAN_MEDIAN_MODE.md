# AlgoZenith --- Running Mean, Median & Mode (STL)

> **Core idea:** Maintain three synchronized views of the same
> collection: **Mean → sum + count**, **Median → two balanced
> multisets**, **Mode → frequency map + ranking set**.

## TOC

-   [1. Problem](#1-problem)
-   [2. Big Picture](#2-big-picture)
-   [3. Mean](#3-mean)
-   [4. Median](#4-median)
-   [5. Mode](#5-mode)
-   [6. Insert](#6-insert)
-   [7. Remove](#7-remove)
-   [8. Dry Run](#8-dry-run)
-   [9. Complete C++](#9-complete-c)
-   [10. Complexity & Mistakes](#10-complexity--mistakes)
-   [11. Mental Model](#11-mental-model)

------------------------------------------------------------------------

# 1. Problem

``` text
insert(x)      add one occurrence
remove(x)      remove one occurrence
getMean()      average
getMedian()    middle / average of two middles
getMode()      most frequent; smaller value wins tie
```

Empty query → `-1`. Mean and median use modular fractions with
`MOD = 1e9+7`.

------------------------------------------------------------------------

# 2. Big Picture

``` text
                     COLLECTION
                         |
          +--------------+--------------+
          |              |              |
          v              v              v
        MEAN           MEDIAN          MODE
          |              |              |
     sum + count     left | right    freq + ranking
```

  Metric   Maintained state        Query
  -------- ----------------------- ---------------------
  Mean     modular sum + count     `sum * inv(count)`
  Median   two `multiset`s         inspect boundaries
  Mode     `map` + ordered `set`   first ranking entry

------------------------------------------------------------------------

# 3. Mean

Maintain:

``` text
totalSum = sum of current values modulo MOD
count    = number of occurrences
```

Insert/remove:

``` text
insert x → sum += x, count++
remove x → sum -= x, count--
```

Because division is modular:

``` text
mean = totalSum * inverse(count) mod MOD
```

Example:

``` text
[10,20,30]

sum = 60
n   = 3

mean = 60/3 = 20
```

Normalize negatives:

``` cpp
long long normalize(long long x) {
    x %= MOD;
    if (x < 0) x += MOD;
    return x;
}
```

------------------------------------------------------------------------

# 4. Median

Use:

``` cpp
multiset<int> leftSet;
multiset<int> rightSet;
```

``` text
       LEFT              RIGHT
   smaller half       larger half

  [1  2  3]      |      [7  9]
         ^                 ^
     max(left)         min(right)
```

## Invariants

``` text
max(left) <= min(right)

left.size == right.size
OR
left.size == right.size + 1
```

## Insert x

``` text
x <= max(left)?
      |
   +--+--+
  yes    no
   |      |
 left   right
    \    /
    rebalance
```

Rebalance:

``` text
left too large:
max(left) ------------> right

right larger:
left <------------ min(right)
```

## Query

Odd:

``` text
median = max(left)
```

Even:

``` text
median = (max(left) + min(right)) / 2
```

Modulo:

``` text
median = (a+b) * inverse(2) mod MOD

inverse(2) = 500000004
```

------------------------------------------------------------------------

# 5. Mode

Maintain:

``` cpp
map<int,int> freq;
set<pair<int,int>> modeTracker;
```

Store:

``` text
{-frequency, value}
```

Example:

``` text
value       freq       ranking
  5           3        (-3,5)
  8           3        (-3,8)
  2           2        (-2,2)
```

Ordered set:

``` text
(-3,5)   <- begin()
(-3,8)
(-2,2)
```

So automatically:

``` text
higher frequency first
then smaller value
```

Mode:

``` cpp
modeTracker.begin()->second;
```

When frequency changes:

``` text
erase old pair
     ↓
change frequency
     ↓
insert new pair
```

Example:

``` text
freq[5] = 2

(-2,5)
   |
 erase
   |
freq 2 -> 3
   |
insert
   v
(-3,5)
```

------------------------------------------------------------------------

# 6. Insert

For `insert(x)`:

``` text
                   INSERT x
                       |
       +---------------+---------------+
       |               |               |
       v               v               v
      MEAN           MEDIAN            MODE
       |               |               |
 sum += x         choose half      erase old rank
 count++          insert x         freq[x]++
                  rebalance        insert new rank
```

Steps:

``` text
1. Update sum.
2. Increment count.
3. Insert x into correct median half.
4. Rebalance halves.
5. Remove old {-freq[x],x}, if present.
6. Increment freq[x].
7. Insert new {-freq[x],x}.
```

------------------------------------------------------------------------

# 7. Remove

``` text
                   REMOVE x
                       |
       +---------------+---------------+
       |               |               |
       v               v               v
      MEAN           MEDIAN            MODE
       |               |               |
 sum -= x         erase ONE x      erase old rank
 count--          rebalance        freq[x]--
                                  insert new rank
                                  if freq > 0
```

For duplicates, erase **one** multiset occurrence:

``` cpp
auto it = leftSet.find(x);
if (it != leftSet.end())
    leftSet.erase(it);
```

Not:

``` cpp
leftSet.erase(x); // removes every x
```

------------------------------------------------------------------------

# 8. Dry Run

Operations:

``` text
insert(2)
insert(5)
insert(2)
remove(2)
```

### `insert(2)`

``` text
collection = [2]

sum/count = 2/1

LEFT | RIGHT
[2]  | []

freq: 2 -> 1
rank: (-1,2)

mean=2, median=2, mode=2
```

### `insert(5)`

``` text
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

median = (2+5)/2
mode   = 2
```

### `insert(2)`

``` text
collection = [2,2,5]

LEFT   | RIGHT
[2,2]  | [5]

freq:
2 -> 2
5 -> 1

rank:
(-2,2)
(-1,5)

mean   = 9/3 = 3
median = 2
mode   = 2
```

### `remove(2)`

``` text
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

------------------------------------------------------------------------

# 9. Complete C++

``` cpp
#include <bits/stdc++.h>
using namespace std;

constexpr long long MOD = 1000000007LL;
constexpr long long INV_TWO = 500000004LL;

long long normalize(long long x) {
    x %= MOD;
    if (x < 0) x += MOD;
    return x;
}

long long power(long long a, long long b) {
    long long ans = 1;
    a = normalize(a);

    while (b) {
        if (b & 1) ans = ans * a % MOD;
        a = a * a % MOD;
        b >>= 1;
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
    map<int,int> freq;
    set<pair<int,int>> modeTracker;

    void balance() {
        if (leftSet.size() > rightSet.size() + 1) {
            auto it = prev(leftSet.end());
            rightSet.insert(*it);
            leftSet.erase(it);
        }
        else if (rightSet.size() > leftSet.size()) {
            auto it = rightSet.begin();
            leftSet.insert(*it);
            rightSet.erase(it);
        }
    }

public:
    void insert(int x) {
        // Mean
        totalSum = (totalSum + normalize(x)) % MOD;
        ++count;

        // Median
        if (leftSet.empty() || x <= *leftSet.rbegin())
            leftSet.insert(x);
        else
            rightSet.insert(x);

        balance();

        // Mode
        int oldFreq = freq[x];

        if (oldFreq > 0)
            modeTracker.erase({-oldFreq, x});

        ++freq[x];
        modeTracker.insert({-freq[x], x});
    }

    void remove(int x) {
        if (!freq.count(x)) return;

        // Mean
        totalSum =
            (totalSum - normalize(x) + MOD) % MOD;
        --count;

        // Median: erase one occurrence
        auto it = leftSet.find(x);

        if (it != leftSet.end())
            leftSet.erase(it);
        else {
            it = rightSet.find(x);
            rightSet.erase(it);
        }

        balance();

        // Mode
        int oldFreq = freq[x];
        modeTracker.erase({-oldFreq, x});

        --freq[x];

        if (freq[x] == 0)
            freq.erase(x);
        else
            modeTracker.insert({-freq[x], x});
    }

    long long getMean() const {
        if (count == 0) return -1;

        return totalSum
             * modInverse(count)
             % MOD;
    }

    long long getMedian() const {
        if (count == 0) return -1;

        if (leftSet.size() > rightSet.size())
            return normalize(*leftSet.rbegin());

        long long a = *leftSet.rbegin();
        long long b = *rightSet.begin();

        return normalize(a + b)
             * INV_TWO % MOD;
    }

    int getMode() const {
        if (count == 0) return -1;
        return modeTracker.begin()->second;
    }
};
```

------------------------------------------------------------------------

# 10. Complexity & Mistakes

  Operation                     Time
  ------------- --------------------
  `insert`        `O(log N + log D)`
  `remove`        `O(log N + log D)`
  `getMean`             `O(log MOD)`
  `getMedian`                 `O(1)`
  `getMode`                   `O(1)`
  Space                       `O(N)`

`N` = occurrences, `D` = distinct values.

Common mistakes:

``` text
multiset.erase(x) removes ALL copies               ✗
balance sizes but ignore ordering                  ✗
leave old {-freq,x} in modeTracker                 ✗
keep zero-frequency values in ranking              ✗
use integer division for modular mean/median       ✗
apply modulo before median ordering                ✗
```

------------------------------------------------------------------------

# 11. Mental Model

``` text
                      ONE COLLECTION
                           |
            +--------------+--------------+
            |              |              |
            v              v              v
          MEAN           MEDIAN          MODE
            |              |              |
        sum/count      two halves     frequency rank
                           |
                     +-----+-----+
                     |           |
                    LEFT       RIGHT
                     |           |
                  max(left)  min(right)
```

Every update must keep **all three views synchronized**:

``` text
INSERT / REMOVE
       |
       +--> update sum + count
       |
       +--> update median halves
       |        |
       |        +--> rebalance
       |
       +--> update {-frequency,value}
```

Remember:

``` text
MEAN   -> sum + count

MEDIAN -> two ordered balanced multisets

MODE   -> freq[x] + set{-freq,x}
```

> **One sentence:** Maintain three synchronized summaries: aggregate
> arithmetic for the mean, two ordered halves for the median, and
> frequency ranking for the mode.
