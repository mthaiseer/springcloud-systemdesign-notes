# AlgoZenith — Custom Comparators in C++

## Table of Contents
- [1. Core Idea](#1-core-idea)
- [2. Rules](#2-rules)
- [3. Function, Lambda, Functor](#3-function-lambda-functor)
- [4. Multi-Level Comparator](#4-multi-level-comparator)
- [5. Sort by Roll Number](#5-sort-by-roll-number)
- [6. Interesting Game — Comparator Pattern](#6-interesting-game--comparator-pattern)
- [7. CF 166A — Rank List](#7-cf-166a--rank-list)
- [8. Priority Queue](#8-priority-queue)
- [9. Set / Map](#9-set--map)
- [10. Common Mistakes](#10-common-mistakes)
- [11. CP Template](#11-cp-template)

# 1. Core Idea

A comparator answers only:

```text
Should a come strictly before b?
```

```cpp
bool cmp(const T& a, const T& b) {
    return /* a should come before b */;
}
```

Examples:

```cpp
return a < b; // ascending
return a > b; // descending
```

`sort()` performs the sorting; the comparator only defines the order.

# 2. Rules

The key meaning is:

```text
cmp(a,b) == true
→ a comes before b
```

Equality must return `false`.

```cpp
return a < b;  // correct
return a > b;  // correct

return a <= b; // WRONG
return a >= b; // WRONG
```

A valid comparator must be consistent and satisfy strict weak ordering.

# 3. Function, Lambda, Functor

### Function

```cpp
bool cmp(int a, int b) {
    return a > b;
}

sort(v.begin(), v.end(), cmp);
```

### Lambda — convenient in CP

```cpp
sort(v.begin(), v.end(),
     [](int a, int b) {
         return a > b;
     });
```

### Functor

```cpp
struct Compare {
    bool operator()(int a, int b) const {
        return a > b;
    }
};
```

Functors are especially useful when the comparator type is part of a container type.

# 4. Multi-Level Comparator

Suppose:

```cpp
struct Student {
    string name;
    int marks;
};
```

Required:

```text
1. higher marks first
2. tie → alphabetically smaller name first
```

Translate priorities directly:

```cpp
bool cmp(const Student& a, const Student& b) {
    if (a.marks != b.marks)
        return a.marks > b.marks;

    return a.name < b.name;
}
```

Dry run:

```text
Alice   50
Bob    100
Charlie 50

Bob vs Alice:
100 > 50 → Bob first

Alice vs Charlie:
marks tie
"Alice" < "Charlie"
→ Alice first

Result:
Bob 100
Alice 50
Charlie 50
```

# 5. Sort by Roll Number

Problem: [Maang — Sort by Roll Number](https://maang.in/problems/Sort-by-Roll-Number-352)

## Simple Idea

When records contain several fields, write the ordering rule using the field(s) required by the problem.

If the required order is:

```text
smaller roll number first
```

then:

```cpp
return a.roll < b.roll;
```

Example:

```text
roll:
4 1 3

after sorting:
1 3 4
```

### C++

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Student {
    int roll;
    string name;
};

bool cmp(const Student& a, const Student& b) {
    return a.roll < b.roll;
}

int main() {
    int n;
    cin >> n;

    vector<Student> a(n);

    for (auto& x : a)
        cin >> x.roll >> x.name;

    sort(a.begin(), a.end(), cmp);

    for (const auto& x : a)
        cout << x.roll << ' ' << x.name << '\n';
}
```

```text
Time: O(n log n)
```

> If the Maang exercise specifies additional fields or tie-breakers, add them in the same priority order.

# 6. Interesting Game

Problem: [Maang — Interesting Game](https://maang.in/problems/Interesting-Game-76)

## Problem in Simple Words

For every index `i`:

```text
Alice gets A[i] if she chooses i
Bob   gets B[i] if he chooses i
```

Each index can be chosen once.

Players alternate:

```text
Alice → Bob → Alice → Bob ...
```

Both play optimally. Find:

```text
Alice / Bob / Tie
```

## Step 1 — What Makes an Index Important?

For index `i`:

```text
Alice values it = A[i]
Bob values it   = B[i]
```

If Alice takes it:

```text
Alice gains A[i]
AND
Bob loses the chance to gain B[i]
```

So its total importance is:

```text
A[i] + B[i]
```

Therefore sort indices by:

```text
A[i] + B[i] descending
```

## Algebraic Derivation

Compare two indices `i` and `j`.

### Order 1 — `i` before `j`

Alice takes `i`, Bob takes `j`.

Score difference:

```text
D1 = A[i] - B[j]
```

### Order 2 — `j` before `i`

Alice takes `j`, Bob takes `i`.

```text
D2 = A[j] - B[i]
```

Prefer `i` before `j` when:

```text
D1 >= D2
```

Substitute:

```text
A[i] - B[j] >= A[j] - B[i]
```

Move terms:

```text
A[i] + B[i] >= A[j] + B[j]
```

Hence the ordering key is:

```text
A[i] + B[i]
```

## Comparator

```cpp
return A[i] + B[i] > A[j] + B[j];
```

Using pairs:

```cpp
bool cmp(const pair<long long,long long>& x,
         const pair<long long,long long>& y) {

    return x.first + x.second >
           y.first + y.second;
}
```

## Dry Run — Sample 1

```text
A = [1,3,4]
B = [5,3,1]
```

| Index | A | B | A+B |
|---:|---:|---:|---:|
| 0 | 1 | 5 | 6 |
| 1 | 3 | 3 | 6 |
| 2 | 4 | 1 | 5 |

One valid sorted order:

```text
index 1 → sum 6
index 0 → sum 6
index 2 → sum 5
```

Turns:

```text
Alice takes index 1 → +3
Bob   takes index 0 → +5
Alice takes index 2 → +4
```

Final:

```text
Alice = 7
Bob   = 5

Alice wins
```

> If two indices have the same `A[i]+B[i]`, either order gives the same optimal game-value effect.

## C++

```cpp
#include <bits/stdc++.h>
using namespace std;

bool cmp(const pair<long long,long long>& x,
         const pair<long long,long long>& y) {

    return x.first + x.second >
           y.first + y.second;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int T;
    cin >> T;

    while (T--) {
        int n;
        cin >> n;

        vector<long long> A(n), B(n);

        for (auto& x : A) cin >> x;
        for (auto& x : B) cin >> x;

        vector<pair<long long,long long>> v(n);

        for (int i = 0; i < n; i++)
            v[i] = {A[i], B[i]};

        sort(v.begin(), v.end(), cmp);

        long long alice = 0;
        long long bob = 0;

        for (int turn = 0; turn < n; turn++) {
            if (turn % 2 == 0)
                alice += v[turn].first;
            else
                bob += v[turn].second;
        }

        if (alice > bob)
            cout << "Alice\n";
        else if (bob > alice)
            cout << "Bob\n";
        else
            cout << "Tie\n";
    }
}
```

```text
Time:  O(n log n)
Space: O(n)
```

---

# 7. CF 166A — Rank List

Problem: [Codeforces 166A — Rank List](https://codeforces.com/problemset/problem/166/A)

## Problem in Simple Words

Each team has:

```text
solved problems = p
penalty         = t
```

Ranking rule:

```text
more solved  → better
if tie:
less penalty → better
```

Given rank `k`, find how many teams have exactly the same result as the team occupying position `k`.

## Step 1 — Translate Ranking to Comparator

For teams `a` and `b`:

```text
if solved differs:
    larger solved comes first

otherwise:
    smaller penalty comes first
```

So:

```cpp
if (a.first != b.first)
    return a.first > b.first;

return a.second < b.second;
```

## Algebra / Ordering Key

There is no arithmetic formula to combine these two fields safely.

The order is **lexicographic by priority**:

```text
Primary   = solved descending
Secondary = penalty ascending
```

Think of the logical key as:

```text
(-solved, penalty)
```

because ascending `-solved` means descending `solved`.

## Dry Run

Suppose:

```text
(4,10)
(2,1)
(4,10)
(3,20)
(4,10)
```

After custom sorting:

```text
(4,10)
(4,10)
(4,10)
(3,20)
(2,1)
```

If:

```text
k = 2
```

the team at position `k` has:

```text
(4,10)
```

Count identical results:

```text
3
```

Answer:

```text
3
```

## C++

```cpp
#include <bits/stdc++.h>
using namespace std;

bool cmp(const pair<int,int>& a,
         const pair<int,int>& b) {

    if (a.first != b.first)
        return a.first > b.first;

    return a.second < b.second;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, k;
    cin >> n >> k;

    vector<pair<int,int>> teams(n);

    for (auto& [solved, penalty] : teams)
        cin >> solved >> penalty;

    sort(teams.begin(), teams.end(), cmp);

    pair<int,int> target = teams[k - 1];

    int ans = 0;

    for (auto team : teams)
        if (team == target)
            ans++;

    cout << ans << '\n';
}
```

```text
Time:  O(n log n)
Space: O(n)
```

This is a clean example of:

```text
PRIMARY KEY descending
+
SECONDARY KEY ascending
```

---

# 8. Priority Queue

For a min-heap:

```cpp
priority_queue<int,
               vector<int>,
               greater<int>> pq;
```

Equivalent custom functor:

```cpp
struct Compare {
    bool operator()(int a, int b) const {
        return a > b;
    }
};

priority_queue<int, vector<int>, Compare> pq;
```

Dry run:

```text
push 10 → top 10
push 2  → top 2
push 5  → top 2
```

For `priority_queue`, think:

```text
cmp(a,b) == true
→ a has lower priority than b
```

# 9. Set / Map

Descending set:

```cpp
set<int, greater<int>> s;
```

Custom object:

```cpp
struct Compare {
    bool operator()(const Student& a,
                    const Student& b) const {

        if (a.marks != b.marks)
            return a.marks > b.marks;

        return a.roll < b.roll;
    }
};

set<Student, Compare> s;
```

Important:

```text
!cmp(a,b) && !cmp(b,a)
```

means `a` and `b` are equivalent under the comparator.

For `set/map`, missing a necessary tie-breaker can therefore make distinct objects equivalent.

# 10. Common Mistakes

| Wrong | Problem | Correct |
|---|---|---|
| `a <= b` | equality returns true | `a < b` |
| `a >= b` | equality returns true | `a > b` |
| `return a.x-b.x` | integer becomes bool | `a.x < b.x` |
| missing required tie | wrong ordering/equivalence | add secondary key |
| pass large object by value | repeated copies | `const T&` |

If equal objects reach the final tie:

```cpp
return false;
```

# 11. CP Template

Most multi-key comparator problems reduce to:

```cpp
sort(a.begin(), a.end(),
     [](const auto& x, const auto& y) {

         if (x.first != y.first)
             return x.first > y.first;

         return x.second < y.second;
     });
```

Read it as:

```text
larger first key first
        ↓ tie
smaller second key first
```

Final mental model:

```text
"a comes before b when..."
          ↓
compare primary key
          ↓ tie
compare secondary key
          ↓ exact tie
return false
```
