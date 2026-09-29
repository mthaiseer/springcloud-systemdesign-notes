# AlgoZenith --- Top K Elements

> **Idea:** Maintain the current **Top K winners** and their sum instead
> of sorting all values after every update.

## TOC

-   [1. Core Model](#1-core-model)
-   [2. Insert Only --- Min Heap](#2-insert-only--min-heap)
-   [3. Dynamic --- Two Multisets](#3-dynamic--two-multisets)
-   [4. Dry Run](#4-dry-run)
-   [5. C++ Templates](#5-c-templates)
-   [6. Complexity & Mistakes](#6-complexity--mistakes)

------------------------------------------------------------------------

# 1. Core Model

Need:

``` text
Insert(x)
Remove(x)          // optional
TopKSum()
```

Maintain:

``` text
sumK = sum of largest min(K,N) occurrences
```

Duplicates count separately.

Two cases:

  Updates                     Structure
  --------------------------- --------------------------
  Insert only                 Min-heap of size `K`
  Insert + arbitrary remove   `top` + `rest` multisets

------------------------------------------------------------------------

# 2. Insert Only --- Min Heap

Keep only the largest `K` values:

``` cpp
priority_queue<int, vector<int>, greater<int>> pq;
```

Why min-heap?

``` text
pq.top()
=
smallest Top-K value
=
weakest winner
```

Insert:

``` text
push x
sumK += x

if size > K:
    sumK -= smallest
    pop smallest
```

### Dry Run

``` text
K = 3
insert: 5, 1, 8, 4
```

``` text
5 → {5}       sumK = 5
1 → {1,5}     sumK = 6
8 → {1,5,8}   sumK = 14

4 → temporary {1,4,5,8}
    sumK = 18

    remove weakest = 1

    Top K = {4,5,8}
    sumK = 17
```

------------------------------------------------------------------------

# 3. Dynamic --- Two Multisets

A heap cannot efficiently remove arbitrary `x`.

Maintain:

``` cpp
multiset<int> top;   // largest K
multiset<int> rest;  // remaining
long long sumK;
```

## Invariants

``` text
1. top.size() = min(K,N)

2. max(rest) <= min(top)

3. sumK = sum(top)
```

Visual:

``` text
rest                    top
───────────────|────────────────
non-winners    |   largest K
               ↑
             boundary
```

## Insert

Optimistically insert into `top`:

``` text
top.insert(x)
sumK += x
```

If too large:

``` text
smallest(top)
      ↓
     rest
```

``` cpp
auto it = top.begin();

sumK -= *it;
rest.insert(*it);
top.erase(it);
```

## Remove

If `x ∈ rest`:

``` text
erase x
Top K unchanged
```

If `x ∈ top`:

``` text
erase x
sumK -= x

largest(rest)
      ↓
     top
```

Promotion:

``` cpp
auto it = prev(rest.end());

sumK += *it;
top.insert(*it);
rest.erase(it);
```

------------------------------------------------------------------------

# 4. Dry Run

``` text
K = 3
```

Build:

``` text
Insert 5 → top={5}       rest={}   sumK=5
Insert 1 → top={1,5}     rest={}   sumK=6
Insert 8 → top={1,5,8}   rest={}   sumK=14
```

Insert `4`:

``` text
temporary top = {1,4,5,8}

demote smallest = 1
```

Result:

``` text
top  = {4,5,8}
rest = {1}
sumK = 17
```

Remove `8`:

``` text
top  = {4,5}
rest = {1}
sumK = 17-8 = 9
```

Top is short, so promote:

``` text
largest(rest) = 1
```

Final:

``` text
top  = {1,4,5}
rest = {}
sumK = 9+1 = 10
```

Check:

``` text
1+4+5 = 10 ✓
```

------------------------------------------------------------------------

# 5. C++ Templates

## Insert Only

``` cpp
struct TopK {
    int k;
    long long sumK = 0;

    priority_queue<
        int,
        vector<int>,
        greater<int>
    > pq;

    void insert(int x) {
        pq.push(x);
        sumK += x;

        if ((int)pq.size() > k) {
            sumK -= pq.top();
            pq.pop();
        }
    }

    long long getSum() {
        return sumK;
    }
};
```

## Insert + Remove

``` cpp
struct DynamicTopK {
    int k;
    long long sumK = 0;

    multiset<int> top, rest;

    void insert(int x) {
        top.insert(x);
        sumK += x;

        if ((int)top.size() > k) {
            auto it = top.begin();

            sumK -= *it;
            rest.insert(*it);
            top.erase(it);
        }
    }

    bool remove(int x) {

        auto it = top.find(x);

        if (it != top.end()) {
            sumK -= x;
            top.erase(it);
        }
        else {
            it = rest.find(x);

            if (it == rest.end())
                return false;

            rest.erase(it);
        }

        if ((int)top.size() < k &&
            !rest.empty()) {

            auto best = prev(rest.end());

            sumK += *best;
            top.insert(*best);
            rest.erase(best);
        }

        return true;
    }

    long long getSum() {
        return sumK;
    }
};
```

## Duplicates

Use:

``` cpp
multiset<int>
```

Remove **one occurrence**:

``` cpp
auto it = top.find(x);

if (it != top.end())
    top.erase(it);
```

Avoid:

``` cpp
top.erase(x);
```

because it removes all equal occurrences.

------------------------------------------------------------------------

# 6. Complexity & Mistakes

  Design                Insert       Remove    Query    Space
  --------------- ------------ ------------ -------- --------
  Min-heap          `O(log K)`          ---   `O(1)`   `O(K)`
  Two multisets     `O(log N)`   `O(log N)`   `O(1)`   `O(N)`

Remember:

``` text
Min Heap:
top() = weakest winner

Two Multisets:
top.begin()  = weakest winner
rest.rbegin() = strongest loser
```

Movement:

``` text
top too large
→ weakest winner → rest

top too small
→ strongest loser → top
```

And always:

``` text
value enters top → sumK += value
value leaves top → sumK -= value
```

## Final Mental Model

``` text
rest                    top
───────────────|────────────────
losers         |      Top K
               ↑
             boundary

max(rest) <= min(top)
```

> **Top K = maintain the winner boundary + cache the winner sum.**
