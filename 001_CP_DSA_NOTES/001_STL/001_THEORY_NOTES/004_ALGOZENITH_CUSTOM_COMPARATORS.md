# AlgoZenith — Custom Comparators in C++
## Visual Self-Learning Version

# Table of Contents

- [0. Prerequisites](#0-prerequisites)
- [1. Core Idea](#1-core-idea)
- [2. Comparator Rules](#2-comparator-rules)
- [3. Function, Lambda, Functor](#3-function-lambda-functor)
- [4. Multi-Level Comparator](#4-multi-level-comparator)
- [5. Sort by Roll Number](#5-sort-by-roll-number)
- [6. Interesting Game — Comparator Pattern](#6-interesting-game--comparator-pattern)
- [7. CF 166A — Rank List](#7-cf-166a--rank-list)
- [8. Priority Queue Comparator](#8-priority-queue-comparator)
- [9. Set / Map Comparator](#9-set--map-comparator)
- [10. Common Mistakes](#10-common-mistakes)
- [11. CP Comparator Template](#11-cp-comparator-template)
- [12. Recognition Checklist](#12-recognition-checklist)
- [13. Final Mental Model](#13-final-mental-model)

---

# 0. Prerequisites

Before custom comparators, be comfortable with these ideas.

## 0.1 Normal Sorting

Default ascending sort:

```cpp
vector<int> a = {5, 2, 8, 1};

sort(a.begin(), a.end());
```

Result:

```text
1 2 5 8
```

Default sorting already answers:

```text
Which value should come before another?
```

For integers:

```text
smaller comes first
```

---

## 0.2 Descending Sorting

Built-in descending order:

```cpp
sort(a.begin(), a.end(), greater<int>());
```

Result:

```text
8 5 2 1
```

This corresponds to:

```text
larger comes first
```

---

## 0.3 Struct / Pair

Competitive programming often sorts records.

Example:

```cpp
struct Student {
    int roll;
    string name;
};
```

or:

```cpp
pair<int, int>
```

Now the problem may say:

```text
sort by marks
if tie, sort by name
```

Default `<` may not match the required order.

That is where a custom comparator is useful.

---

## 0.4 Primary and Secondary Keys

Suppose ranking rule is:

```text
1. higher score first
2. if score ties, smaller penalty first
```

Think:

```text
PRIMARY KEY   = score descending
SECONDARY KEY = penalty ascending
```

ASCII:

```text
compare score
     |
     +---- different ----> decide immediately
     |
     +---- equal --------> compare penalty
```

---

## 0.5 Strict Ordering

A comparator must answer:

```text
Should a come STRICTLY before b?
```

Therefore equal objects should not satisfy:

```text
cmp(a,b) = true
```

For equality:

```text
cmp(a,b) = false
cmp(b,a) = false
```

This is why:

```cpp
a < b
```

is valid, but:

```cpp
a <= b
```

is not.

---

# 1. Core Idea

A comparator answers one question only:

```text
Should a come strictly before b?
```

Generic form:

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

Important:

```text
sort() performs the sorting

comparator defines the ordering rule
```

---

## 1.1 Visual Model

Suppose:

```text
a = 7
b = 4
```

Comparator:

```cpp
return a > b;
```

Check:

```text
7 > 4
true
```

Meaning:

```text
7 should come before 4
```

So order becomes:

```text
7 4
```

---

## 1.2 Mental Translation

Whenever you read a problem statement:

```text
"higher marks first"
```

translate directly to:

```cpp
return a.marks > b.marks;
```

If it says:

```text
"smaller roll first"
```

translate to:

```cpp
return a.roll < b.roll;
```

---

# 2. Comparator Rules

## 2.1 Rule 1 — `true` Means "a Before b"

```text
cmp(a,b) == true
```

means:

```text
a must appear before b
```

ASCII:

```text
cmp(a,b)

true
 |
 v

[a] [b]
```

---

## 2.2 Rule 2 — Equality Must Return False

Correct:

```cpp
return a < b;
```

or:

```cpp
return a > b;
```

Wrong:

```cpp
return a <= b;
return a >= b;
```

Why?

Take:

```text
a = 5
b = 5
```

With:

```cpp
return a <= b;
```

we get:

```text
cmp(5,5) = true
```

This says:

```text
5 must come before itself
```

which is invalid.

---

## 2.3 Rule 3 — Order Must Be Consistent

If:

```text
a before b
b before c
```

then your comparator should behave consistently with:

```text
a before c
```

Do not create contradictory rules.

---

## 2.4 Rule 4 — Exact Tie Returns False

For a multi-key comparator:

```cpp
if (a.x != b.x) {
    return a.x < b.x;
}

if (a.y != b.y) {
    return a.y < b.y;
}

return false;
```

Exact tie means:

```text
neither must come before the other
```

---

# 3. Function, Lambda, Functor

The ordering logic is the same.

Only the syntax changes.

---

## 3.1 Function Comparator

```cpp
bool cmp(int a, int b) {
    return a > b;
}

sort(v.begin(), v.end(), cmp);
```

Use when:

```text
comparator is reusable
logic is easy to name
```

---

## 3.2 Lambda Comparator

```cpp
sort(v.begin(), v.end(),
     [](int a, int b) {
         return a > b;
     });
```

Useful in CP because it keeps logic near the sort call.

Visual:

```text
sort(...)
   |
   +--> comparator written inline
```

---

## 3.3 Functor Comparator

```cpp
struct Compare {
    bool operator()(int a, int b) const {
        return a > b;
    }
};
```

Used as:

```cpp
sort(v.begin(), v.end(), Compare());
```

More importantly, comparator type can become part of a container type:

```cpp
set<int, Compare> s;
```

or:

```cpp
priority_queue<int, vector<int>, Compare> pq;
```

---

## 3.4 Quick Comparison

```text
FUNCTION
--------
named reusable rule

LAMBDA
------
short local rule

FUNCTOR
-------
comparator becomes a TYPE
useful for set/map/priority_queue
```

---

# 4. Multi-Level Comparator

Suppose:

```cpp
struct Student {
    string name;
    int marks;
};
```

Required order:

```text
1. higher marks first
2. if marks tie:
   alphabetically smaller name first
```

---

## 4.1 Step 1 — Write Priorities in English

```text
PRIMARY:
marks descending

SECONDARY:
name ascending
```

---

## 4.2 Step 2 — Compare Primary Key

If marks differ:

```cpp
if (a.marks != b.marks) {
    return a.marks > b.marks;
}
```

Why `>`?

Because:

```text
larger marks should come first
```

---

## 4.3 Step 3 — Handle Tie

If marks are equal:

```cpp
return a.name < b.name;
```

Why `<`?

Because:

```text
alphabetically smaller name should come first
```

---

## 4.4 Final Comparator

```cpp
bool cmp(const Student& a, const Student& b) {
    if (a.marks != b.marks) {
        return a.marks > b.marks;
    }

    return a.name < b.name;
}
```

---

## 4.5 ASCII Decision Tree

```text
Compare Student a and b
        |
        v
Are marks different?
   /             \
 yes              no
 |                 |
 v                 v
higher marks    compare names
comes first     alphabetically
```

---

## 4.6 Dry Run

Input:

```text
Alice   50
Bob    100
Charlie 50
```

Compare:

```text
Bob vs Alice

100 > 50
=> Bob first
```

Then:

```text
Alice vs Charlie

marks tie

"Alice" < "Charlie"
=> Alice first
```

Result:

```text
Bob      100
Alice     50
Charlie   50
```

---

## 4.7 Recognition Signal

Think multi-key comparator when the statement says:

```text
sort by X
if tie sort by Y
if still tie sort by Z
```

Translate priorities in the same order.

---

# 5. Sort by Roll Number

Problem: [Maang — Sort by Roll Number](https://maang.in/problems/Sort-by-Roll-Number-352)

## 5.1 Problem Meaning

Each record contains several fields.

Example:

```text
roll
name
```

But sorting rule only says:

```text
smaller roll number first
```

---

## 5.2 Step-by-Step Derivation

Take two students:

```text
a.roll
b.roll
```

Question:

```text
Should a come before b?
```

Rule:

```text
yes, when a.roll < b.roll
```

Comparator:

```cpp
return a.roll < b.roll;
```

---

## 5.3 ASCII Example

Before:

```text
roll:
4   1   3
```

Comparator asks:

```text
1 < 4 ? yes
3 < 4 ? yes
1 < 3 ? yes
```

After:

```text
1   3   4
```

Visual:

```text
smaller roll
     |
     v

1 ------> 3 ------> 4
```

---

## 5.4 C++17

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

    for (auto& x : a) {
        cin >> x.roll >> x.name;
    }

    sort(a.begin(), a.end(), cmp);

    for (const auto& x : a) {
        cout << x.roll << ' ' << x.name << '\n';
    }
}
```

---

## 5.5 Complexity

```text
Time = O(n log n)
```

---

## 5.6 Common Mistake

If the real problem has extra tie-breakers, do not ignore them.

Example:

```text
roll ascending
tie -> name ascending
```

Then write both priorities.

---

# 6. Interesting Game — Comparator Pattern

Problem: [Maang — Interesting Game](https://maang.in/problems/Interesting-Game-76)

## 6.1 Problem in Simple Words

For every index `i`:

```text
Alice values index i as A[i]
Bob   values index i as B[i]
```

Players alternate selecting unused indices.

```text
Alice → Bob → Alice → Bob ...
```

Both play optimally.

We need the winner.

---

## 6.2 Why Normal Sorting Is Not Obvious

It is tempting to sort only by:

```text
A[i]
```

because Alice moves first.

Or only by:

```text
B[i]
```

because Bob also wants value.

Both are incomplete.

An index matters to both players.

---

## 6.3 Step 1 — Measure Total Importance

If Alice takes index `i`:

```text
Alice gains A[i]
```

and Bob loses the possibility of getting:

```text
B[i]
```

So the total swing/importance is:

```text
A[i] + B[i]
```

Therefore sort by:

```text
A[i] + B[i] descending
```

---

## 6.4 ASCII Intuition

For one index:

```text
             index i
            /       \
           /         \
      Alice value   Bob value
         A[i]         B[i]

Total importance:

A[i] + B[i]
```

If this index disappears early:

```text
one player gains their value
the other player loses their opportunity
```

---

## 6.5 Step-by-Step Algebra

Compare two indices:

```text
i
j
```

### Order 1

Alice takes `i`.

Bob takes `j`.

Score difference:

```text
D1 = A[i] - B[j]
```

### Order 2

Alice takes `j`.

Bob takes `i`.

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

Therefore ordering key:

```text
A[i] + B[i]
```

descending.

---

## 6.6 Comparator

```cpp
bool cmp(const pair<long long, long long>& x,
         const pair<long long, long long>& y) {
    return x.first + x.second >
           y.first + y.second;
}
```

---

## 6.7 Dry Run

```text
A = [1,3,4]
B = [5,3,1]
```

Compute importance:

```text
index 0:
1 + 5 = 6

index 1:
3 + 3 = 6

index 2:
4 + 1 = 5
```

Sorted importance:

```text
6, 6, 5
```

One valid order:

```text
index 1
index 0
index 2
```

Turns:

```text
Alice takes index 1 → +3
Bob   takes index 0 → +5
Alice takes index 2 → +4
```

Totals:

```text
Alice = 7
Bob   = 5
```

Alice wins.

---

## 6.8 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

bool cmp(const pair<long long, long long>& x,
         const pair<long long, long long>& y) {
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

        for (long long& x : A) {
            cin >> x;
        }

        for (long long& x : B) {
            cin >> x;
        }

        vector<pair<long long, long long>> v(n);

        for (int i = 0; i < n; i++) {
            v[i] = {A[i], B[i]};
        }

        sort(v.begin(), v.end(), cmp);

        long long alice = 0;
        long long bob = 0;

        for (int turn = 0; turn < n; turn++) {
            if (turn % 2 == 0) {
                alice += v[turn].first;
            } else {
                bob += v[turn].second;
            }
        }

        if (alice > bob) {
            cout << "Alice\n";
        } else if (bob > alice) {
            cout << "Bob\n";
        } else {
            cout << "Tie\n";
        }
    }
}
```

---

## 6.9 Recognition Signal

When:

```text
two players value the same item differently
taking an item both gains something
and denies the opponent something
```

try comparing:

```text
combined importance
```

and derive the ordering by swapping two adjacent choices.

---

# 7. CF 166A — Rank List

Problem: [Codeforces 166A — Rank List](https://codeforces.com/problemset/problem/166/A)

## 7.1 Problem Meaning

Each team has:

```text
solved problems = p
penalty         = t
```

Ranking rule:

```text
more solved → better
if tie:
less penalty → better
```

Given rank `k`, count how many teams have exactly the same `(p,t)` as the team at position `k`.

---

## 7.2 Step 1 — Extract Primary Key

Primary rule:

```text
more solved first
```

So:

```cpp
a.first > b.first
```

---

## 7.3 Step 2 — Extract Secondary Key

When solved count ties:

```text
smaller penalty first
```

So:

```cpp
a.second < b.second
```

---

## 7.4 Comparator

```cpp
bool cmp(const pair<int, int>& a,
         const pair<int, int>& b) {
    if (a.first != b.first) {
        return a.first > b.first;
    }

    return a.second < b.second;
}
```

---

## 7.5 ASCII Decision Tree

```text
Compare teams a and b
        |
        v
Solved counts different?
      /         \
    yes          no
     |            |
     v            v
more solved    compare penalty
comes first    smaller comes first
```

---

## 7.6 Logical Ordering Key

You can mentally think:

```text
(-solved, penalty)
```

because:

```text
ascending -solved
=
descending solved
```

This is only a mental model.

Writing the comparator directly is clearer.

---

## 7.7 Dry Run

Input:

```text
(4,10)
(2,1)
(4,10)
(3,20)
(4,10)
```

Sort:

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

target:

```text
(4,10)
```

Number of identical teams:

```text
3
```

---

## 7.8 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

bool cmp(const pair<int, int>& a,
         const pair<int, int>& b) {
    if (a.first != b.first) {
        return a.first > b.first;
    }

    return a.second < b.second;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, k;
    cin >> n >> k;

    vector<pair<int, int>> teams(n);

    for (auto& [solved, penalty] : teams) {
        cin >> solved >> penalty;
    }

    sort(teams.begin(), teams.end(), cmp);

    pair<int, int> target = teams[k - 1];

    int ans = 0;

    for (const auto& team : teams) {
        if (team == target) {
            ans++;
        }
    }

    cout << ans << '\n';
}
```

---

## 7.9 Recognition Signal

Typical statement:

```text
higher X is better
if X ties:
lower Y is better
```

Direct translation:

```text
X descending
Y ascending
```

---

# 8. Priority Queue Comparator

This is where many learners get confused because the meaning feels reversed.

For `sort()`:

```text
cmp(a,b) == true
=> a comes before b
```

For `priority_queue`:

```text
cmp(a,b) == true
=> a has LOWER priority than b
```

---

## 8.1 Min-Heap

Built-in:

```cpp
priority_queue<int,
               vector<int>,
               greater<int>> pq;
```

Smallest value stays on top.

Equivalent functor:

```cpp
struct Compare {
    bool operator()(int a, int b) const {
        return a > b;
    }
};
```

Use:

```cpp
priority_queue<int, vector<int>, Compare> pq;
```

---

## 8.2 ASCII Visual

Push:

```text
10
2
5
```

Min-heap top:

```text
      2
     / \
   10   5
```

So:

```text
top = 2
```

Dry run:

```text
push 10 → top = 10
push 2  → top = 2
push 5  → top = 2
```

---

## 8.3 Mental Rule

For priority queue:

```text
return true
```

means:

```text
"a should be below b"
```

or:

```text
"a has lower priority"
```

---

# 9. Set / Map Comparator

Example descending set:

```cpp
set<int, greater<int>> s;
```

Iteration order:

```text
largest → smallest
```

---

## 9.1 Custom Object

```cpp
struct Student {
    int marks;
    int roll;
};
```

Suppose required order:

```text
higher marks first
tie → smaller roll first
```

Comparator:

```cpp
struct Compare {
    bool operator()(const Student& a,
                    const Student& b) const {
        if (a.marks != b.marks) {
            return a.marks > b.marks;
        }

        return a.roll < b.roll;
    }
};
```

Use:

```cpp
set<Student, Compare> s;
```

---

## 9.2 Important Equivalence Rule

For `set/map`:

```text
!cmp(a,b) && !cmp(b,a)
```

means:

```text
a and b are equivalent under ordering
```

This can make two different objects behave like duplicates.

---

## 9.3 ASCII Example

Suppose comparator uses only marks:

```text
Student A:
marks = 100
roll  = 1

Student B:
marks = 100
roll  = 2
```

If comparator ignores roll:

```text
cmp(A,B) = false
cmp(B,A) = false
```

Then set sees:

```text
A ≡ B
```

Even though roll differs.

So if roll should distinguish them, add it as a tie-breaker.

---

# 10. Common Mistakes

## Mistake 1 — Using `<=`

Wrong:

```cpp
return a <= b;
```

Why?

For equal values:

```text
cmp(a,a) = true
```

Invalid.

Correct:

```cpp
return a < b;
```

---

## Mistake 2 — Using `>=`

Wrong:

```cpp
return a >= b;
```

Correct:

```cpp
return a > b;
```

---

## Mistake 3 — Returning Difference

Wrong:

```cpp
return a.x - b.x;
```

Comparator expects:

```text
boolean ordering condition
```

not numeric difference.

Correct:

```cpp
return a.x < b.x;
```

---

## Mistake 4 — Missing Tie-Breaker

Suppose rule is:

```text
higher marks first
tie -> smaller roll first
```

Wrong:

```cpp
return a.marks > b.marks;
```

Correct:

```cpp
if (a.marks != b.marks) {
    return a.marks > b.marks;
}

return a.roll < b.roll;
```

---

## Mistake 5 — Copying Large Objects

Prefer:

```cpp
const Student& a
```

instead of:

```cpp
Student a
```

to avoid unnecessary copies.

---

# 11. CP Comparator Template

Most multi-key comparator problems reduce to this:

```cpp
sort(a.begin(), a.end(),
     [](const auto& x, const auto& y) {
         if (x.first != y.first) {
             return x.first > y.first;
         }

         return x.second < y.second;
     });
```

Read it as:

```text
PRIMARY:
larger first key first

if tie:

SECONDARY:
smaller second key first
```

---

## 11.1 Three-Key Version

```cpp
sort(a.begin(), a.end(),
     [](const Item& x, const Item& y) {
         if (x.score != y.score) {
             return x.score > y.score;
         }

         if (x.penalty != y.penalty) {
             return x.penalty < y.penalty;
         }

         return x.name < y.name;
     });
```

Decision flow:

```text
score?
  |
  +-- different -> decide
  |
 penalty?
  |
  +-- different -> decide
  |
 name decides
```

---

# 12. Recognition Checklist

When a sorting problem appears, ask:

```text
1. What is the PRIMARY key?

2. Ascending or descending?

3. What happens on a tie?

4. Is there a SECONDARY key?

5. More tie-breakers?

6. Does exact equality return false?

7. Is this comparator for:
   sort?
   priority_queue?
   set/map?

8. For set/map:
   do I need another tie-breaker
   to keep distinct objects distinct?
```

---

# 13. Final Mental Model

```text
CUSTOM COMPARATOR
=================

Problem statement
      |
      v
Translate English ordering rules
      |
      v
PRIMARY key
      |
      +---- different ----> return ordering
      |
      v
SECONDARY key
      |
      +---- different ----> return ordering
      |
      v
more tie-breakers if needed
      |
      v
exact tie
      |
      v
return false
```

Most important sentence:

```text
For sort():

cmp(a,b) == true
means
"a comes before b"
```

For `priority_queue`:

```text
cmp(a,b) == true
means
"a has lower priority than b"
```

And for `set/map`:

```text
!cmp(a,b) && !cmp(b,a)
means
"a and b are equivalent under the ordering"
```

That distinction is the key to using comparators correctly in competitive programming.
