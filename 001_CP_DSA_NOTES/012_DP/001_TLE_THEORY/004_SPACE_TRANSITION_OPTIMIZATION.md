# 004 — Space & Transition Optimization in Dynamic Programming
## Continuation of `003_DP_PROBLEM_SOLVING_3`

> **Self-study goal:** Learn how to take a correct DP and make it faster or smaller **without changing the meaning of the state**.
>
> Follow the same construction style used in the previous note:
>
> **Observation → Story Removed → State → Choices → Transition → Base Case → Final Subproblem → Complexity → Optimization → Dry Run → C++17**

---

# Problem / Lecture Links

1. **CSES — Book Shop (0/1 Knapsack)**  
   https://cses.fi/problemset/task/1158

2. **Lecture topic — Space Optimization**

3. **Lecture topic — Transition Optimization**

---

# Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [What Optimization Means in DP](#2-what-optimization-means-in-dp)
3. [Part A — Book Shop / 0-1 Knapsack](#3-part-a--book-shop--0-1-knapsack)
4. [Part B — Space Optimization](#4-part-b--space-optimization)
5. [Part C — Transition Optimization](#5-part-c--transition-optimization)
6. [Example 1 — Prefix Sum Optimization](#6-example-1--prefix-sum-optimization)
7. [Example 2 — Step-k Prefix Sum Optimization](#7-example-2--step-k-prefix-sum-optimization)
8. [Example 3 — Sliding Window Minimum](#8-example-3--sliding-window-minimum)
9. [Example 4 — Dynamic Range Minimum](#9-example-4--dynamic-range-minimum)
10. [Pattern Comparison](#10-pattern-comparison)
11. [Contest Recognition Checklist](#11-contest-recognition-checklist)
12. [Final Mental Model](#12-final-mental-model)

---

# 1. Prerequisites

This note assumes you already know the DP construction process from the previous lesson.

Before starting, be comfortable with these ideas.

---

## 1.1 DP state

Every DP state must have an English meaning.

Example:

```text
dp[i][budget]
=
maximum value obtainable
using books from index i onward
with remaining budget = budget
```

The array is only storage.

The **meaning** is the real DP.

---

## 1.2 Transition

A transition answers:

```text
From the current state,
which smaller states can I move to?
```

For example:

```text
skip current item
or
take current item
```

---

## 1.3 Dependency

Before optimizing space, always ask:

```text
Which older DP states does the current state actually read?
```

Example:

```text
dp[i] = dp[i-1] + dp[i-2]
```

The current state needs only:

```text
dp[i-1]
dp[i-2]
```

It does not need the full array `dp[0...i-3]`.

---

## 1.4 Prefix sums

If repeatedly evaluating:

```text
a[0] + a[1] + ... + a[i]
```

build:

```text
prefix[i] = a[0] + a[1] + ... + a[i]
```

Then the sum is available in `O(1)`.

This idea will optimize DP transitions too.

---

## 1.5 Sliding window

A sliding window maintains information about a moving interval.

Example:

```text
[i-k ... i-1]
```

When `i` increases:

```text
remove old left element
add new right element
```

This is useful when a DP transition repeatedly asks for something like:

```text
minimum of previous k states
```

---

## 1.6 Range query data structures

Know the purpose of:

```text
multiset
segment tree
```

You do not need to memorize all implementations yet.

Just remember:

```text
multiset    -> dynamic min/max among currently stored values
segment tree -> min/max/sum over an arbitrary range
```

---

# 2. What Optimization Means in DP

After deriving a correct DP, there are two common optimization questions.

```text
DP OPTIMIZATION
│
├── Space Optimization
│   └── store fewer states
│
└── Transition Optimization
    └── evaluate each state faster
```

These are different.

---

## 2.1 Space optimization

Suppose:

```text
number of states = O(n × x)
```

but every state needs only one adjacent row.

Then we may reduce:

```text
O(n × x)
```

to:

```text
O(x)
```

Time may remain unchanged.

---

## 2.2 Transition optimization

Suppose there are `n` states.

But computing one state scans `O(n)` previous states.

Then:

```text
states              = O(n)
work per state       = O(n)
--------------------------------
total time           = O(n²)
```

If we precompute or maintain the needed information, work per state may become:

```text
O(1)
```

or:

```text
O(log n)
```

Then total time becomes:

```text
O(n)
```

or:

```text
O(n log n)
```

---

## 2.3 Golden rule

Do **not** optimize before the recurrence is correct.

Use this order:

```text
1. Define state
2. Write transition
3. Prove base case
4. Identify final subproblem
5. Calculate complexity
6. Only now optimize
```

---

# 3. Part A — Book Shop / 0-1 Knapsack

## 3.1 What is the problem asking?

We have `n` books.

Each book has:

```text
price[i]
pages[i]
```

We have maximum budget:

```text
x
```

Each book can be bought at most once.

Goal:

```text
maximize total pages
such that total price <= x
```

This is classic **0/1 Knapsack**.

---

## 3.2 Why brute force is bad

For every book:

```text
TAKE
or
SKIP
```

So there are approximately:

```text
2^n
```

subsets.

Brute force:

```text
O(2^n)
```

Too slow.

---

## 3.3 Story removed

At state `(i, budget)`:

```text
Book i
│
├── SKIP
│   └── move to (i+1, budget)
│
└── TAKE
    └── pages[i] + solve(i+1, budget-price[i])
```

Take is allowed only when:

```text
price[i] <= budget
```

---

## 3.4 STATE

Define:

```text
dp[i][budget]
=
maximum pages obtainable
using books i ... n-1
with available budget = budget
```

Visual:

```text
books

0 1 2 ... i ... n-1
          ^
          |
      solve from here

budget = b
```

---

## 3.5 CHOICES

### Choice 1 — Skip book `i`

```text
skip = dp[i+1][budget]
```

---

### Choice 2 — Take book `i`

Possible only if:

```text
price[i] <= budget
```

Then:

```text
take
=
pages[i]
+
dp[i+1][budget-price[i]]
```

---

## 3.6 TRANSITION

```text
dp[i][budget]
=
max(skip, take)
```

So:

```text
dp[i][budget]
=
max(
    dp[i+1][budget],
    pages[i] + dp[i+1][budget-price[i]]
)
```

when the book can be afforded.

Otherwise:

```text
dp[i][budget]
=
dp[i+1][budget]
```

---

## 3.7 BASE CASE

When there are no books left:

```text
dp[n][budget] = 0
```

for every budget.

Why?

```text
No books left
=> no more pages can be gained
```

---

## 3.8 FINAL SUBPROBLEM

Initially:

```text
i = 0
budget = x
```

Therefore:

```text
answer = dp[0][x]
```

---

## 3.9 Bottom-up dependency

Observe:

```text
dp[i][budget]
```

uses only:

```text
dp[i+1][budget]
dp[i+1][budget-price[i]]
```

That means:

```text
current row i
```

depends only on:

```text
next row i+1
```

It does **not** need every row simultaneously.

This observation is the key to space optimization.

---

## 3.10 2D complexity

Number of states:

```text
n × (x+1)
```

Work per state:

```text
O(1)
```

Therefore:

```text
Time  = O(n × x)
Space = O(n × x)
```

Time is good.

But space can be improved.

---

# 4. Part B — Space Optimization

The lecture's main question is:

```text
What other state does our current state depend on?
```

Then ask:

```text
Do we really need answers to all subproblems at all times?
```

If not:

```text
store only the relevant states
```

---

## 4.1 First compression — two rows

For Book Shop:

```text
current row depends only on next row
```

So instead of:

```text
dp[n+1][x+1]
```

store:

```text
next[x+1]
current[x+1]
```

Visual:

```text
full 2D table

row i+2   ----------------
row i+1   =================   <- next
row i     ----------------   <- current being built
row i-1   ----------------

Only two rows matter.
```

Space becomes:

```text
O(x)
```

instead of:

```text
O(n × x)
```

---

## 4.2 Two-row transition

For each book `i`:

```text
for budget = 0 ... x
```

we calculate:

```text
current[budget] = next[budget]
```

and when affordable:

```text
current[budget]
=
max(
    current[budget],
    pages[i] + next[budget-price[i]]
)
```

After finishing the whole row:

```text
next = current
```

---

## 4.3 Why two rows are safe

While calculating the entire current row, every transition reads only from:

```text
next[]
```

The old values are never destroyed.

So dependency is preserved.

---

## 4.4 Can we use only one row?

Yes.

But loop direction becomes extremely important.

Let:

```text
dp[budget]
=
maximum pages obtainable with this budget
using books processed so far
```

When processing book `i`:

```text
dp[budget]
=
max(
    dp[budget],
    pages[i] + dp[budget-price[i]]
)
```

But now both old and new values are inside the **same array**.

---

## 4.5 Why budget must go right → left

Suppose:

```text
price = 3
pages = 10
```

If we iterate budget left → right:

```text
budget = 3
```

updates:

```text
dp[3] = 10
```

Later:

```text
budget = 6
```

may read the already updated:

```text
dp[3] = 10
```

and produce:

```text
dp[6] = 20
```

That means the same book was used twice.

But this is **0/1 Knapsack**.

Each book may be taken only once.

Therefore iterate:

```text
budget = x ... price[i]
```

right → left.

Then `dp[budget-price[i]]` still belongs to the previous-book layer.

---

## 4.6 Visual intuition

Correct 0/1 update:

```text
budget:
0 1 2 3 4 5 6 7 8
                <----
              process
```

Why backwards?

```text
future larger budget
must not reuse value created by same item
```

---

## 4.7 One-array C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, x;
    cin >> n >> x;

    vector<int> price(n), pages(n);

    for (int &v : price) cin >> v;
    for (int &v : pages) cin >> v;

    vector<int> dp(x + 1, 0);

    for (int i = 0; i < n; i++) {
        for (int budget = x; budget >= price[i]; budget--) {
            dp[budget] = max(
                dp[budget],
                pages[i] + dp[budget - price[i]]
            );
        }
    }

    cout << dp[x] << '\n';
    return 0;
}
```

---

## 4.8 Complexity after optimization

```text
Time  = O(n × x)
Space = O(x)
```

Important:

```text
Space changed.
Time did not.
```

---

## 4.9 General space-optimization question

Whenever you see a DP table, ask:

```text
Which previous rows / columns are actually needed?
```

Examples:

### Fibonacci

```text
dp[i] = dp[i-1] + dp[i-2]
```

Need only:

```text
previous 2 values
```

Space:

```text
O(n) -> O(1)
```

---

### Grid DP

Typical transition:

```text
dp[i][j]
depends on

dp[i+1][j]
dp[i][j+1]
```

The whole `n × m` matrix may not be needed.

Often one row of size:

```text
O(m)
```

is enough.

---

## 4.10 When space optimization may NOT be appropriate

The lecture gives two important warnings.

### Case 1 — Final answer needs many states

If the problem finally asks for:

```text
all dp states
```

then deleting old states may be impossible.

---

### Case 2 — Reconstruction / backtracking

Suppose you need to output:

```text
which choices formed the optimal answer
```

Sometimes the entire DP table or parent information is needed.

So never compress automatically.

Ask first:

```text
Will I need old states later?
```

---

# 5. Part C — Transition Optimization

Space optimization reduces stored memory.

Transition optimization reduces the time required to compute each state.

The lecture gives this checklist:

```text
1. Observe the transition equation.
2. Can some repeated work be precomputed?
3. Is there a clever algebraic observation?
4. Can a range-query data structure help?
```

Common tools:

```text
prefix sum
suffix sum
sliding window
multiset
monotonic deque
Fenwick tree
segment tree
```

---

## 5.1 Why transition cost matters

Suppose:

```text
dp[i]
=
dp[i-1] + dp[i-2] + ... + dp[0]
```

For every `i`, naive calculation scans all previous states.

Cost for each state:

```text
O(i)
```

Total:

```text
1 + 2 + 3 + ... + n
```

which is:

```text
O(n²)
```

But almost the same sum is recomputed again and again.

That repeated work is the optimization opportunity.

---

# 6. Example 1 — Prefix Sum Optimization

## 6.1 Given transition

```text
dp[i]
=
dp[i-1] + dp[i-2] + ... + dp[0]
```

Base case:

```text
dp[0] = 1
```

Goal:

```text
find dp[n]
```

---

## 6.2 Naive approach

For each `i`:

```text
sum dp[0 ... i-1]
```

So:

```text
T(i) = O(i)
```

Total:

```text
O(1 + 2 + ... + n)
=
O(n²)
```

---

## 6.3 Observation

Define:

```text
prefix[i]
=
dp[0] + dp[1] + ... + dp[i]
```

Then:

```text
dp[i]
=
prefix[i-1]
```

Now update prefix:

```text
prefix[i]
=
prefix[i-1] + dp[i]
```

Both operations are `O(1)`.

---

## 6.4 Step-by-step derivation

Original:

```text
dp[i]
=
dp[i-1] + dp[i-2] + ... + dp[0]
```

But by definition:

```text
prefix[i-1]
=
dp[0] + dp[1] + ... + dp[i-1]
```

Therefore:

```text
dp[i] = prefix[i-1]
```

After computing `dp[i]`:

```text
prefix[i]
=
prefix[i-1] + dp[i]
```

---

## 6.5 Dry run

Base:

```text
dp[0] = 1
prefix[0] = 1
```

### i = 1

```text
dp[1] = prefix[0] = 1
prefix[1] = 1 + 1 = 2
```

### i = 2

```text
dp[2] = prefix[1] = 2
prefix[2] = 2 + 2 = 4
```

### i = 3

```text
dp[3] = prefix[2] = 4
prefix[3] = 4 + 4 = 8
```

So:

```text
dp = 1, 1, 2, 4, ...
```

---

## 6.6 Complexity

Before:

```text
Time = O(n²)
```

After:

```text
Time  = O(n)
Space = O(n)
```

If only `dp[n]` is required, even the stored arrays can sometimes be reduced further.

---

## 6.7 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<long long> dp(n + 1, 0);
    vector<long long> prefix(n + 1, 0);

    dp[0] = 1;
    prefix[0] = dp[0];

    for (int i = 1; i <= n; i++) {
        dp[i] = prefix[i - 1];
        prefix[i] = prefix[i - 1] + dp[i];
    }

    cout << dp[n] << '\n';
}
```

---

# 7. Example 2 — Step-k Prefix Sum Optimization

## 7.1 Given transition

```text
dp[i]
=
dp[i-k] + dp[i-2k] + dp[i-3k] + ...
```

Base values:

```text
dp[0 ... k-1]
```

are given.

Goal:

```text
find all dp[0 ... n]
```

---

## 7.2 Why ordinary prefix sum is not enough

The transition does not use every previous state.

It uses only indices:

```text
i-k
i-2k
i-3k
...
```

These indices all have the same remainder modulo `k`.

Example for:

```text
k = 3
```

Groups are:

```text
0, 3, 6, 9, ...
1, 4, 7, 10, ...
2, 5, 8, 11, ...
```

Each DP state depends only on its own group.

---

## 7.3 Observation

Define a running sum for each residue class.

For index `i`:

```text
residue = i % k
```

Maintain:

```text
sum[residue]
=
sum of earlier dp values
having the same remainder modulo k
```

Then:

```text
dp[i] = sum[i % k]
```

After computing `dp[i]`:

```text
sum[i % k] += dp[i]
```

---

## 7.4 Equivalent lecture recurrence

Another way to express the same idea is:

```text
sum[i]
=
dp[i] + dp[i-k] + dp[i-2k] + ...
```

Then:

```text
dp[i] = sum[i-k]
```

and:

```text
sum[i]
=
dp[i] + sum[i-k]
```

This is a **prefix sum with jump size `k`**.

---

## 7.5 Dry run

Let:

```text
k = 3
```

Assume:

```text
dp[0] = 1
dp[1] = 2
dp[2] = 4
```

Initialize residue sums:

```text
sum[0] = 1
sum[1] = 2
sum[2] = 4
```

### i = 3

```text
residue = 0

dp[3] = sum[0] = 1
sum[0] = 1 + 1 = 2
```

### i = 4

```text
residue = 1

dp[4] = sum[1] = 2
sum[1] = 2 + 2 = 4
```

### i = 5

```text
residue = 2

dp[5] = sum[2] = 4
sum[2] = 4 + 4 = 8
```

### i = 6

```text
residue = 0

dp[6] = sum[0] = 2
```

Which represents:

```text
dp[6]
=
dp[3] + dp[0]
=
1 + 1
=
2
```

Correct.

---

## 7.6 Complexity

Naive:

```text
approximately O(n² / k)
```

Optimized:

```text
Time  = O(n)
Space = O(k) extra for running sums
```

---

## 7.7 Recognition pattern

When transition looks like:

```text
i-k
i-2k
i-3k
...
```

think:

```text
same remainder modulo k
```

Then ask:

```text
Can I maintain one running sum per remainder class?
```

---

# 8. Example 3 — Sliding Window Minimum

## 8.1 Given transition

```text
dp[i]
=
arr[i]
+
min(
    dp[i-1],
    dp[i-2],
    ...,
    dp[i-k]
)
```

Goal:

```text
find all dp[0 ... n]
```

---

## 8.2 Naive complexity

For every `i`, scan up to `k` previous states.

Therefore:

```text
Time = O(n × k)
```

If both are large, this is too slow.

---

## 8.3 Story removed

For each position `i`, we need:

```text
minimum dp value inside window
[i-k ... i-1]
```

Visual:

```text
dp indices:

...  i-k  ...  i-2  i-1 | i
     <-------------->
        need minimum
```

When moving from `i` to `i+1`:

```text
old window: [i-k ... i-1]
new window: [i-k+1 ... i]
```

Only two changes happen:

```text
remove dp[i-k]
add    dp[i]
```

So recomputing the minimum from scratch is wasteful.

---

## 8.4 Multiset solution

A `multiset` keeps elements sorted and supports duplicates.

Operations:

```text
insert(value)          O(log k)
erase(one value)       O(log k)
minimum                O(1) using begin()
```

Maintain all DP values inside the active window.

Then:

```text
minimumPrevious = *window.begin()
```

and:

```text
dp[i] = arr[i] + minimumPrevious
```

---

## 8.5 Step-by-step algorithm

Assume `dp[0]` is already known.

Maintain:

```text
multiset<long long> window
```

Before calculating `dp[i]`, `window` contains exactly:

```text
dp[max(0, i-k) ... i-1]
```

Then:

```text
1. minimum = *window.begin()
2. dp[i] = arr[i] + minimum
3. insert dp[i]
4. remove state that leaves the next window
```

---

## 8.6 Dry run

Let:

```text
k = 3
```

Suppose current DP values are:

```text
dp[0] = 5
dp[1] = 7
dp[2] = 4
```

For `i = 3`:

```text
window = {5, 7, 4}
minimum = 4
```

If:

```text
arr[3] = 6
```

then:

```text
dp[3] = 6 + 4 = 10
```

For next position `i = 4`:

Oldest state `dp[0]` leaves.

Window becomes:

```text
{7, 4, 10}
```

Minimum is still:

```text
4
```

No full scan was needed.

---

## 8.7 C++17 using multiset

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n, k;
    cin >> n >> k;

    vector<long long> arr(n + 1);
    for (int i = 0; i <= n; i++) {
        cin >> arr[i];
    }

    vector<long long> dp(n + 1, 0);

    // Hypothetical base case for this lecture example.
    dp[0] = arr[0];

    multiset<long long> window;
    window.insert(dp[0]);

    for (int i = 1; i <= n; i++) {
        dp[i] = arr[i] + *window.begin();

        window.insert(dp[i]);

        int leavingIndex = i - k;
        if (leavingIndex >= 0) {
            auto it = window.find(dp[leavingIndex]);
            if (it != window.end()) {
                window.erase(it);
            }
        }
    }
}
```

> The exact initialization depends on the real problem. The lecture recurrence is hypothetical; the optimization pattern is the important part.

---

## 8.8 Complexity

Naive:

```text
O(n × k)
```

Multiset:

```text
O(n log k)
```

because every state is inserted and removed at most once.

---

## 8.9 Can we do even better?

Yes.

For a fixed-size sliding-window minimum, a **monotonic deque** can reduce it to:

```text
O(n)
```

But the lecture uses the multiset idea to introduce transition optimization with a range-maintenance structure.

Mental model:

```text
fixed moving range minimum
=> multiset / monotonic deque
```

---

# 9. Example 4 — Dynamic Range Minimum

## 9.1 Given transition

```text
dp[i]
=
arr[i]
+
min(
    dp[i-1],
    dp[i-2],
    ...,
    dp[i-arr[i]]
)
```

The number of previous states is no longer fixed.

It depends on:

```text
arr[i]
```

---

## 9.2 Rewrite as a range query

The minimum is over indices:

```text
i-arr[i]
...
i-1
```

So:

```text
dp[i]
=
arr[i]
+
minimumDP(
    i-arr[i],
    i-1
)
```

If the left boundary goes below zero, use:

```text
left = max(0, i-arr[i])
```

Therefore:

```text
dp[i]
=
arr[i]
+
min(dp[left ... i-1])
```

---

## 9.3 Why the previous sliding-window trick is harder

In Example 3:

```text
window size = k
```

was fixed.

Now:

```text
window size = arr[i]
```

changes for every `i`.

Example:

```text
i = 10, arr[i] = 2   -> query [8,9]
i = 11, arr[i] = 8   -> query [3,10]
i = 12, arr[i] = 1   -> query [11,11]
```

The query interval may expand and shrink irregularly.

A simple one-direction sliding window is no longer enough.

---

## 9.4 Segment tree observation

We need two operations:

```text
1. point update
   dp[i] becomes known

2. range minimum query
   min(dp[L ... R])
```

This is exactly what a segment tree can support.

Typical complexity:

```text
update = O(log n)
query  = O(log n)
```

So every DP state costs:

```text
O(log n)
```

Total:

```text
O(n log n)
```

instead of potentially:

```text
O(n²)
```

---

## 9.5 State calculation

For every `i`:

```text
left  = max(0, i-arr[i])
right = i-1
```

Query:

```text
bestPrevious
=
minimum dp value in [left, right]
```

Then:

```text
dp[i]
=
arr[i] + bestPrevious
```

Finally insert:

```text
dp[i]
```

into the segment tree.

---

## 9.6 Visual

```text
0  1  2  3  4  5  6  7  8  ... i-1 | i
            <--------------------->
              query range depends
                   on arr[i]
```

This is not a fixed-width window.

So think:

```text
arbitrary range query
=> segment tree
```

---

## 9.7 C++17 segment tree skeleton

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;
const ll INF = (1LL << 60);

struct SegmentTree {
    int n;
    vector<ll> tree;

    SegmentTree(int size) {
        n = 1;
        while (n < size) n <<= 1;
        tree.assign(2 * n, INF);
    }

    void update(int index, ll value) {
        int p = index + n;
        tree[p] = value;

        for (p >>= 1; p >= 1; p >>= 1) {
            tree[p] = min(tree[2 * p], tree[2 * p + 1]);
        }
    }

    ll query(int left, int right) {
        if (left > right) return INF;

        left += n;
        right += n;

        ll answer = INF;

        while (left <= right) {
            if (left & 1) {
                answer = min(answer, tree[left]);
                left++;
            }

            if (!(right & 1)) {
                answer = min(answer, tree[right]);
                right--;
            }

            left >>= 1;
            right >>= 1;
        }

        return answer;
    }
};

int main() {
    int n;
    cin >> n;

    vector<int> arr(n + 1);
    for (int i = 0; i <= n; i++) {
        cin >> arr[i];
    }

    vector<ll> dp(n + 1, INF);

    // Hypothetical base case from the lecture.
    dp[0] = arr[0];

    SegmentTree st(n + 1);
    st.update(0, dp[0]);

    for (int i = 1; i <= n; i++) {
        int left = max(0, i - arr[i]);
        int right = i - 1;

        ll bestPrevious = st.query(left, right);

        dp[i] = arr[i] + bestPrevious;
        st.update(i, dp[i]);
    }
}
```

> Again, the recurrence in the lecture is hypothetical. Focus on recognizing **DP + dynamic range minimum query**.

---

## 9.8 Complexity

For every state:

```text
1 range minimum query = O(log n)
1 point update        = O(log n)
```

Therefore:

```text
Time  = O(n log n)
Space = O(n)
```

---

# 10. Pattern Comparison

| Pattern | Transition shape | Naive | Optimization | Final |
|---|---|---:|---|---:|
| 0/1 Knapsack | `take / skip` with previous row | `O(n×x)` space | keep 1 row | `O(x)` space |
| Fibonacci-style | depends on last few states | `O(n)` space | rolling variables | `O(1)` space |
| Full prefix sum DP | `sum(dp[0...i-1])` | `O(n²)` | prefix sum | `O(n)` |
| Step-k sum DP | `dp[i-k] + dp[i-2k] + ...` | large | residue-class running sums | `O(n)` |
| Fixed previous-k min | `min(dp[i-k...i-1])` | `O(nk)` | multiset / deque | `O(n log k)` / `O(n)` |
| Variable range min | `min(dp[i-arr[i]...i-1])` | up to `O(n²)` | segment tree | `O(n log n)` |

---

# 11. Contest Recognition Checklist

When your DP is correct but too slow or too large, do **not** immediately invent a different DP.

Use this checklist.

---

## 11.1 Space checklist

Ask:

```text
What previous states are actually read?
```

If transition uses only:

```text
previous row
```

think:

```text
2D -> 2 rows -> maybe 1 row
```

If transition uses:

```text
last 2 / last 3 values
```

think:

```text
rolling variables
```

---

## 11.2 One-array warning

When compressing layers into one array, ask:

```text
Will an update accidentally read a value
written during the same iteration?
```

For 0/1 Knapsack:

```text
iterate budget right -> left
```

For unbounded knapsack, the direction is often opposite because reuse is allowed.

So loop direction carries DP meaning.

---

## 11.3 Transition checklist

Look at the expensive part of the recurrence.

### Repeated sum?

```text
sum over a prefix/range
```

Think:

```text
prefix sum
```

---

### Jump by fixed `k`?

```text
i-k, i-2k, i-3k, ...
```

Think:

```text
residue classes modulo k
```

---

### Minimum/maximum over fixed previous window?

```text
[i-k ... i-1]
```

Think:

```text
multiset
monotonic deque
```

---

### Minimum/maximum over changing range?

```text
[L(i) ... R(i)]
```

Think:

```text
segment tree
Fenwick tree (when supported operation fits)
```

---

## 11.4 Most important question

Do not ask:

```text
Which data structure should I use?
```

First ask:

```text
What repeated quantity does this transition need?
```

Then choose a data structure that maintains that quantity.

---

# 12. Final Mental Model

```text
                 CORRECT DP
                    |
                    v
          calculate complexity
                    |
          +---------+----------+
          |                    |
          v                    v
   too much space?        transition slow?
          |                    |
          v                    v
 inspect dependencies    inspect repeated work
          |                    |
          v                    v
  keep only needed       precompute / maintain
       states              needed quantity
          |                    |
          v                    v
 rolling array          prefix / window / tree
```

---

# Final Revision Sheet

## Space Optimization

```text
Question:
Which old states does the current state need?
```

Examples:

```text
last 2 values         -> O(1)
previous row          -> O(m)
0/1 knapsack          -> one array, right-to-left
```

Be careful when:

```text
all final states are required
or
reconstruction needs old information
```

---

## Transition Optimization

```text
Question:
What work am I repeating for every state?
```

Recognition:

```text
sum of prefix                  -> prefix sum
sum every k-th state           -> residue-class sum
min over previous fixed k      -> multiset / deque
min over dynamic range         -> segment tree
```

---

## Contest workflow

```text
1. Build correct state.
2. Build correct transition.
3. Determine base case.
4. Determine final subproblem.
5. Compute complexity.
6. Find dependency / repeated work.
7. Optimize only that part.
8. Re-check correctness after optimization.
```

---

# One-line takeaway

> **DP optimization is usually not about changing the problem — it is about storing less of the same DP or evaluating the same recurrence more intelligently.**
