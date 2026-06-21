# MiniComplexityAnalysis_Master.md

## Basic Big-O → Codeforces Candidate Master → FAANG Interview Complexity Thinking

> **Core promise:** After this chapter, you should not look at `N <= 2e5` and randomly choose an algorithm.  
> You should automatically convert constraints into allowed complexity, allowed complexity into pattern family, and pattern family into code.

---

# Clickable Index

- [000. One Core Mental Model](#000-one-core-mental-model)
- [001. Why Complexity Exists](#001-why-complexity-exists)
- [002. Big-O Mental Model](#002-big-o-mental-model)
- [003. Complexity Growth Map](#003-complexity-growth-map)
- [004. Counting Operations](#004-counting-operations)
- [005. O(1) Constant Time](#005-o1-constant-time)
- [006. O(N) Linear Time](#006-on-linear-time)
- [007. O(log N) Logarithmic Time](#007-olog-n-logarithmic-time)
- [008. O(N log N) Sorting and Divide-Conquer](#008-on-log-n-sorting-and-divide-conquer)
- [009. O(N²) Pair Thinking](#009-on2-pair-thinking)
- [010. O(N³) Triple Thinking](#010-on3-triple-thinking)
- [011. O(2^N) Subset Thinking](#011-o2n-subset-thinking)
- [012. O(N!) Permutation Thinking](#012-on-factorial-permutation-thinking)
- [013. Space Complexity](#013-space-complexity)
- [014. Recursion Stack Complexity](#014-recursion-stack-complexity)
- [015. Master Theorem and Recursion Trees](#015-master-theorem-and-recursion-trees)
- [016. Amortized Analysis](#016-amortized-analysis)
- [017. Two Pointers Complexity](#017-two-pointers-complexity)
- [018. Sliding Window Complexity](#018-sliding-window-complexity)
- [019. Binary Search Complexity](#019-binary-search-complexity)
- [020. Graph Complexity](#020-graph-complexity)
- [021. DP Complexity](#021-dp-complexity)
- [022. STL Complexity Traps](#022-stl-complexity-traps)
- [023. Constraint to Algorithm Decision Tree](#023-constraint-to-algorithm-decision-tree)
- [024. Brute → Better → Optimal Playbook](#024-brute--better--optimal-playbook)
- [025. TLE Debugging Playbook](#025-tle-debugging-playbook)
- [026. Codeforces A/B/C/D/E Complexity Forms](#026-codeforces-abcde-complexity-forms)
- [027. FAANG Interview Complexity Discussion](#027-faang-interview-complexity-discussion)
- [099. Mega Cheat Sheet](#099-mega-cheat-sheet)

---

# 000. One Core Mental Model

Complexity analysis has one core mental model:

```text
Input Size
    ↓
Number of States / Visits / Operations
    ↓
Can it fit inside time and memory?
```

In CP and FAANG, complexity is not a mathematical decoration. It is a filter.

```text
Wrong beginner flow:

Problem statement
    ↓
Try remembered algorithm
    ↓
Hope it passes

Correct CM flow:

Constraints
    ↓
Allowed complexity
    ↓
Pattern family
    ↓
Implementation
    ↓
Stress/TLE check
```

## One Sentence

> Complexity analysis is the skill of predicting whether your algorithm survives the largest hidden test before you submit.

---

# 001. Why Complexity Exists

## Problem

A solution can pass samples and still fail.

Sample:

```text
N = 5
```

Hidden test:

```text
N = 200000
```

The sample only checks correctness on a tiny case. Complexity checks survival.

## Pair Sum Example

Find whether any pair sums to `X`.

### Brute Force: O(N²)

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n, x;
    cin >> n >> x;

    vector<int> a(n);
    for (int &v : a) cin >> v;

    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
            if (a[i] + a[j] == x) {
                cout << "YES\n";
                return 0;
            }
        }
    }

    cout << "NO\n";
    return 0;
}
```

Operation estimate:

```text
N = 200000
pairs ≈ N * (N - 1) / 2
      ≈ 20,000,000,000
```

This is too high.

### Better: O(N) Average With Hash Set

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n, x;
    cin >> n >> x;

    unordered_set<int> seen;

    for (int i = 0; i < n; i++) {
        int cur;
        cin >> cur;

        int need = x - cur;

        if (seen.count(need)) {
            cout << "YES\n";
            return 0;
        }

        seen.insert(cur);
    }

    cout << "NO\n";
    return 0;
}
```

Mental model:

```text
Instead of checking every pair,
remember what you have seen.
```

## One Picture To Remember

```text
Sample input checks:     "Does it work once?"
Complexity checks:       "Does it survive max N?"
```

---

# 002. Big-O Mental Model

Big-O describes the growth rate as input grows.

It ignores:

```text
small constants
machine details
lower-order terms
```

It keeps the dominant behavior.

```text
5N + 100        -> O(N)
N² + 100N       -> O(N²)
N log N + N     -> O(N log N)
```

## Why Constants Usually Disappear

For small `N`:

```text
100N may be bigger than N²
```

For large `N`:

```text
N² dominates 100N
```

ASCII:

```text
N grows →
100N:       / / / / / /
N²:         _/ grows much faster
```

## C++ Demo: Estimate Growth

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long n;
    cin >> n;

    cout << "N          = " << n << "
";
    cout << "log2(N)    = " << (long long)log2(n) << "
";
    cout << "Nlog2(N)   = " << n * (long long)log2(n) << "
";

    if (n <= 1000000) {
        cout << "N^2        = " << n * n << "
";
    } else {
        cout << "N^2        = too large to print safely here
";
    }

    return 0;
}
```

## Common Mistake

```cpp
for (int i = 1; i <= n; i *= 2) {
    for (int j = 0; j < n; j++) {
        // O(1)
    }
}
```

This is not O(N²).

```text
i values: 1, 2, 4, 8, ...
count = logN
inner = N
total = O(N log N)
```

---

# 003. Complexity Growth Map

## Growth Comparison

```text
Operations
^
|
|                                      O(N!)
|                                  *
|                              *
|                          *
|                     O(2^N)
|                  *
|              O(N^3)
|           *
|        O(N^2)
|     *
|   O(N log N)
|  *
| O(N)
|*
| O(log N)
|*
| O(1)
+-------------------------------------------------> N
```

## Practical Contest Feel

```text
O(1)        formula
O(logN)     binary search / exponentiation
O(N)        one pass
O(NlogN)    sorting / segment tree / Dijkstra
O(N²)       all pairs / 2D DP
O(N³)       Floyd / interval DP / triple choice
O(2^N)      subsets / bitmask
O(N!)       permutations
```

## Operation Budget Approximation

Most CP platforms roughly allow around:

```text
1e8 simple operations per second-ish in C++
```

But do not blindly trust this.

Heavy operations like maps, strings, modulo, hashing, recursion, and priority queues can be slower.

---

# 004. Counting Operations

## Core Rule

Count how many times the innermost meaningful work runs.

## Simple Loop

```cpp
for (int i = 0; i < n; i++) {
    work();
}
```

```text
work runs N times -> O(N)
```

## Full Nested Loop

```cpp
for (int i = 0; i < n; i++) {
    for (int j = 0; j < n; j++) {
        work();
    }
}
```

```text
N * N = N²
```

## Triangular Loop

```cpp
long long cnt = 0;

for (int i = 0; i < n; i++) {
    for (int j = i + 1; j < n; j++) {
        cnt++;
    }
}
```

Dry run for `N = 5`:

```text
i = 0: j = 1,2,3,4 -> 4
i = 1: j = 2,3,4   -> 3
i = 2: j = 3,4     -> 2
i = 3: j = 4       -> 1
i = 4: none        -> 0

Total = 4 + 3 + 2 + 1 = 10
      = N(N-1)/2
      = O(N²)
```

## Working C++ Counter

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    long long operations = 0;

    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
            operations++;
        }
    }

    cout << operations << "
";
    return 0;
}
```

## Variable Tracking Table

For `N = 4`:

```text
+---+-------------+-------+
| i | j values    | count |
+---+-------------+-------+
| 0 | 1,2,3       | 3     |
| 1 | 2,3         | 2     |
| 2 | 3           | 1     |
| 3 | none        | 0     |
+---+-------------+-------+
Total = 6
```

---

# 005. O(1) Constant Time

## Mental Model

O(1) means input size does not affect the number of operations.

Examples:

```text
array index access
simple arithmetic
formula
checking size
average hash lookup
```

## C++ Example

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {10, 20, 30, 40};

    cout << a[2] << "
";     // direct index -> O(1)

    long long x = 100, y = 50;
    cout << x + y << "
";    // arithmetic -> O(1)

    return 0;
}
```

## CP Example: Count Multiples in [L, R]

Question:

```text
How many numbers in [L, R] are divisible by k?
```

Bad:

```text
Loop from L to R
```

Good formula:

```text
floor(R / k) - floor((L - 1) / k)
```

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long L, R, k;
    cin >> L >> R >> k;

    long long ans = R / k - (L - 1) / k;

    cout << ans << "
";
    return 0;
}
```

## CM Recognition

When `N` or values go up to `1e18`, you usually need:

```text
math
formula
binary search
logarithmic loop
```

---

# 006. O(N) Linear Time

## Mental Model

Touch every element once or a constant number of times.

```text
[ a0 ][ a1 ][ a2 ][ a3 ] ... [ aN-1 ]
   ↓    ↓    ↓    ↓             ↓
 visit once each
```

## Sum Array

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    long long sum = 0;

    for (int i = 0; i < n; i++) {
        long long x;
        cin >> x;
        sum += x;
    }

    cout << sum << "
";
    return 0;
}
```

## CP Pattern Forms

```text
frequency counting
prefix construction
single-pass greedy
Kadane
scan max/min
hash-set lookup
counting characters
```

## Dry Run: Max Element

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<int> a(n);
    for (int &x : a) cin >> x;

    int mx = a[0];

    for (int i = 1; i < n; i++) {
        mx = max(mx, a[i]);
    }

    cout << mx << "
";
    return 0;
}
```

For:

```text
a = [4, 1, 9, 2]
```

```text
+---+------+-------------+
| i | a[i] | mx          |
+---+------+-------------+
| 0 | 4    | 4           |
| 1 | 1    | max(4,1)=4  |
| 2 | 9    | max(4,9)=9  |
| 3 | 2    | max(9,2)=9  |
+---+------+-------------+
```

---

# 007. O(log N) Logarithmic Time

## Mental Model

Each step removes a large chunk of possibilities.

Usually:

```text
N -> N/2 -> N/4 -> N/8 -> ... -> 1
```

ASCII:

```text
Search space:
[-------------------------------] N
[---------------]                 N/2
[-------]                         N/4
[---]                             N/8
[-]                               1
```

## Binary Search

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n, target;
    cin >> n >> target;

    vector<int> a(n);
    for (int &x : a) cin >> x;

    int l = 0, r = n - 1;

    while (l <= r) {
        int mid = l + (r - l) / 2;

        if (a[mid] == target) {
            cout << "FOUND
";
            return 0;
        } else if (a[mid] < target) {
            l = mid + 1;
        } else {
            r = mid - 1;
        }
    }

    cout << "NOT FOUND
";
    return 0;
}
```

## Dry Run

```text
a = [1, 3, 5, 7, 9, 11, 13]
target = 11

+------+---+---+-----+--------+----------+
| step | l | r | mid | a[mid] | action   |
+------+---+---+-----+--------+----------+
| 1    | 0 | 6 | 3   | 7      | go right |
| 2    | 4 | 6 | 5   | 11     | found    |
+------+---+---+-----+--------+----------+
```

## Recognition

Use logarithmic thinking for:

```text
sorted array
monotonic predicate
binary search on answer
power/exponentiation
divide by 2 loops
```

---

# 008. O(N log N) Sorting and Divide-Conquer

## Mental Model

`O(N log N)` often means:

```text
process N items
over logN levels
```

Merge sort tree:

```text
Level 0:              N work
                    [--------]

Level 1:          N/2        N/2
                 [----]     [----]       total N

Level 2:       N/4 N/4    N/4 N/4        total N

Number of levels = logN
Work per level = N
Total = N logN
```

## Sorting Example

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<int> a(n);
    for (int &x : a) cin >> x;

    sort(a.begin(), a.end());

    for (int x : a) cout << x << " ";
    cout << "
";

    return 0;
}
```

## Two Sum After Sorting

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n, x;
    cin >> n >> x;

    vector<int> a(n);
    for (int &v : a) cin >> v;

    sort(a.begin(), a.end());

    int l = 0, r = n - 1;

    while (l < r) {
        int sum = a[l] + a[r];

        if (sum == x) {
            cout << "YES
";
            return 0;
        }

        if (sum < x) l++;
        else r--;
    }

    cout << "NO
";
    return 0;
}
```

Complexity:

```text
sort          O(NlogN)
two pointers  O(N)
total         O(NlogN)
```

## CM Recognition

When `N <= 2e5`, sorting is usually allowed.

---

# 009. O(N²) Pair Thinking

## Mental Model

O(N²) usually means:

```text
all pairs
all subarrays
2D states
matrix scan
```

ASCII pair grid:

```text
      j →
    0 1 2 3 4
i 0 x x x x x
↓ 1 x x x x x
  2 x x x x x
  3 x x x x x
  4 x x x x x

N rows * N cols = N²
```

## Count Equal Pairs: O(N²)

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<int> a(n);
    for (int &x : a) cin >> x;

    long long pairs = 0;

    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
            if (a[i] == a[j]) pairs++;
        }
    }

    cout << pairs << "
";
    return 0;
}
```

## Optimize to O(N) Using Frequency

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    unordered_map<int, long long> freq;
    long long pairs = 0;

    for (int i = 0; i < n; i++) {
        int x;
        cin >> x;

        pairs += freq[x]; // all previous equal values form a pair with x
        freq[x]++;
    }

    cout << pairs << "
";
    return 0;
}
```

## When O(N²) Is Okay

```text
N <= 2000      usually okay
N <= 5000      maybe okay if simple
N >= 1e5       no, unless loops are not independent
```

---

# 010. O(N³) Triple Thinking

## Mental Model

Three independent choices.

```text
choose i
choose j
choose k
```

Common examples:

```text
all triples
all subarrays + compute sum inside
Floyd-Warshall
interval DP transitions
```

## Bad Maximum Subarray: O(N³)

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<int> a(n);
    for (int &x : a) cin >> x;

    long long best = LLONG_MIN;

    for (int l = 0; l < n; l++) {
        for (int r = l; r < n; r++) {
            long long sum = 0;

            for (int k = l; k <= r; k++) {
                sum += a[k];
            }

            best = max(best, sum);
        }
    }

    cout << best << "
";
    return 0;
}
```

## Better O(N²) With Prefix Sum

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<long long> pref(n + 1, 0);

    for (int i = 0; i < n; i++) {
        long long x;
        cin >> x;
        pref[i + 1] = pref[i] + x;
    }

    long long best = LLONG_MIN;

    for (int l = 0; l < n; l++) {
        for (int r = l; r < n; r++) {
            long long sum = pref[r + 1] - pref[l];
            best = max(best, sum);
        }
    }

    cout << best << "
";
    return 0;
}
```

## Optimal O(N) Kadane

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    long long cur = 0;
    long long best = LLONG_MIN;

    for (int i = 0; i < n; i++) {
        long long x;
        cin >> x;

        cur = max(x, cur + x);
        best = max(best, cur);
    }

    cout << best << "
";
    return 0;
}
```

---

# 011. O(2^N) Subset Thinking

## Mental Model

Each element has two choices:

```text
take
not take
```

For `N` elements:

```text
2 * 2 * 2 * ... * 2 = 2^N
```

Recursion tree:

```text
idx=0
├── skip a[0]
│   ├── skip a[1]
│   │   ├── skip a[2]
│   │   └── take a[2]
│   └── take a[1]
│       ├── skip a[2]
│       └── take a[2]
└── take a[0]
    ├── skip a[1]
    └── take a[1]
```

## Recursive Subsets

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> a;
vector<int> cur;

void dfs(int idx) {
    if (idx == (int)a.size()) {
        for (int x : cur) cout << x << " ";
        cout << "
";
        return;
    }

    dfs(idx + 1); // not take

    cur.push_back(a[idx]);
    dfs(idx + 1); // take
    cur.pop_back();
}

int main() {
    int n;
    cin >> n;

    a.resize(n);
    for (int &x : a) cin >> x;

    dfs(0);
    return 0;
}
```

## Bitmask Subsets

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<int> a(n);
    for (int &x : a) cin >> x;

    for (int mask = 0; mask < (1 << n); mask++) {
        cout << "{ ";
        for (int i = 0; i < n; i++) {
            if (mask & (1 << i)) cout << a[i] << " ";
        }
        cout << "}
";
    }

    return 0;
}
```

## CM Recognition

Use subset thinking when:

```text
N <= 20
choose/not choose
bitmask DP
meet-in-the-middle
small number of special items
```

---

# 012. O(N!) Permutation Thinking

## Mental Model

Permutation means arranging all items.

```text
position 1: N choices
position 2: N-1 choices
position 3: N-2 choices
...
total = N!
```

ASCII for `N=4`:

```text
4 choices
 ├─ 3 choices
 │   ├─ 2 choices
 │   │   └─ 1 choice
```

## next_permutation

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<int> a(n);
    for (int &x : a) cin >> x;

    sort(a.begin(), a.end());

    do {
        for (int x : a) cout << x << " ";
        cout << "
";
    } while (next_permutation(a.begin(), a.end()));

    return 0;
}
```

## Backtracking Permutations

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> a, perm, used;

void dfs() {
    if ((int)perm.size() == (int)a.size()) {
        for (int x : perm) cout << x << " ";
        cout << "
";
        return;
    }

    for (int i = 0; i < (int)a.size(); i++) {
        if (used[i]) continue;

        used[i] = 1;
        perm.push_back(a[i]);

        dfs();

        perm.pop_back();
        used[i] = 0;
    }
}

int main() {
    int n;
    cin >> n;

    a.resize(n);
    used.assign(n, 0);

    for (int &x : a) cin >> x;

    dfs();
    return 0;
}
```

## Acceptable Limits

```text
N <= 8   usually safe
N <= 10  maybe safe if simple
N > 11   usually impossible
```

---

# 013. Space Complexity

## Mental Model

Space complexity counts memory usage.

```text
Input storage
Extra arrays
Hash maps
Recursion stack
DP table
Graph adjacency
```

## O(1) Extra Space

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    long long sum = 0;

    for (int i = 0; i < n; i++) {
        long long x;
        cin >> x;
        sum += x;
    }

    cout << sum << "
";
    return 0;
}
```

## O(N) Space

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<long long> a(n);

    for (long long &x : a) cin >> x;

    cout << a.size() << "
";
    return 0;
}
```

## Memory Estimate

```text
int                4 bytes
long long          8 bytes
double             8 bytes
char               1 byte
pair<int,int>      8 bytes
pair<ll,ll>        16 bytes
```

Memory examples:

```text
vector<int>(1e7)        ≈ 40 MB
vector<long long>(1e7)  ≈ 80 MB
int dp[5000][5000]      ≈ 100 MB
long long dp[5000][5000]≈ 200 MB
```

## Memory Limit Rule

If memory limit is:

```text
256 MB
```

Avoid casually creating:

```text
long long dp[5000][5000]
```

---

# 014. Recursion Stack Complexity

## Mental Model

Each recursive call creates a stack frame.

```text
call f(5)
 └─ f(4)
     └─ f(3)
         └─ f(2)
             └─ f(1)
```

Depth = stack space.

## Factorial

```cpp
#include <bits/stdc++.h>
using namespace std;

long long fact(int n) {
    if (n <= 1) return 1;
    return n * fact(n - 1);
}

int main() {
    int n;
    cin >> n;

    cout << fact(n) << "
";
    return 0;
}
```

Complexity:

```text
time  = O(N)
stack = O(N)
```

## DFS Stack Overflow Warning

Recursive DFS with `N = 2e5` can crash.

Iterative DFS:

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n, m;
    cin >> n >> m;

    vector<vector<int>> g(n);

    for (int i = 0; i < m; i++) {
        int u, v;
        cin >> u >> v;
        --u; --v;

        g[u].push_back(v);
        g[v].push_back(u);
    }

    vector<int> vis(n, 0);
    stack<int> st;

    st.push(0);
    vis[0] = 1;

    while (!st.empty()) {
        int u = st.top();
        st.pop();

        for (int v : g[u]) {
            if (!vis[v]) {
                vis[v] = 1;
                st.push(v);
            }
        }
    }

    return 0;
}
```

---

# 015. Master Theorem and Recursion Trees

## Mental Model

Divide-and-conquer recurrence:

```text
T(N) = aT(N/b) + f(N)
```

Meaning:

```text
a      = number of recursive subproblems
N / b  = size of each subproblem
f(N)   = work outside recursion
```

## Binary Search

```text
T(N) = T(N/2) + O(1)
```

Recursion tree:

```text
N
|
N/2
|
N/4
|
N/8
|
1
```

Levels:

```text
logN
```

Total:

```text
O(logN)
```

## Merge Sort

```text
T(N) = 2T(N/2) + O(N)
```

Tree:

```text
Level 0:               N
                    [------]

Level 1:          N/2       N/2
                [---]     [---]       total N

Level 2:       N/4 N/4   N/4 N/4      total N

...
logN levels
```

Total:

```text
N work per level * logN levels = O(NlogN)
```

## Merge Sort Code

```cpp
#include <bits/stdc++.h>
using namespace std;

void mergeSort(vector<int>& a, int l, int r) {
    if (l >= r) return;

    int mid = l + (r - l) / 2;

    mergeSort(a, l, mid);
    mergeSort(a, mid + 1, r);

    vector<int> temp;
    int i = l, j = mid + 1;

    while (i <= mid && j <= r) {
        if (a[i] <= a[j]) temp.push_back(a[i++]);
        else temp.push_back(a[j++]);
    }

    while (i <= mid) temp.push_back(a[i++]);
    while (j <= r) temp.push_back(a[j++]);

    for (int k = 0; k < (int)temp.size(); k++) {
        a[l + k] = temp[k];
    }
}

int main() {
    int n;
    cin >> n;

    vector<int> a(n);
    for (int &x : a) cin >> x;

    mergeSort(a, 0, n - 1);

    for (int x : a) cout << x << " ";
    return 0;
}
```

---

# 016. Amortized Analysis

## Mental Model

Some operations are expensive occasionally, but cheap on average over many operations.

Classic examples:

```text
vector push_back
monotonic stack
monotonic queue
DSU path compression
```

## Vector Push Back

```text
push 1: capacity enough -> O(1)
push 2: capacity enough -> O(1)
push 3: resize/copy     -> O(N)
many pushes together    -> amortized O(1)
```

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> v;

    for (int i = 0; i < 20; i++) {
        v.push_back(i);

        cout << "size=" << v.size()
             << " capacity=" << v.capacity()
             << "
";
    }

    return 0;
}
```

## Monotonic Stack Proof

Even if code has `while` inside `for`, total can be O(N).

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {2, 1, 5, 3, 4};
    stack<int> st;

    for (int x : a) {
        while (!st.empty() && st.top() < x) {
            st.pop();
        }
        st.push(x);
    }

    cout << st.size() << "
";
    return 0;
}
```

Why O(N)?

```text
Each element is pushed once.
Each element is popped at most once.

Total pushes <= N
Total pops   <= N
Total        <= 2N = O(N)
```

ASCII:

```text
Element life:
push ───────────── pop

No element can pop twice.
```

---

# 017. Two Pointers Complexity

## Mental Model

Two pointers may look like nested logic, but if each pointer moves only forward, total is linear.

```text
l moves 0 → N
r moves 0 → N

total movement <= 2N
```

## Sorted Pair Sum

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n, target;
    cin >> n >> target;

    vector<int> a(n);
    for (int &x : a) cin >> x;

    sort(a.begin(), a.end());

    int l = 0, r = n - 1;

    while (l < r) {
        int sum = a[l] + a[r];

        if (sum == target) {
            cout << "YES
";
            return 0;
        }

        if (sum < target) l++;
        else r--;
    }

    cout << "NO
";
    return 0;
}
```

Complexity:

```text
sort = O(NlogN)
pointer scan = O(N)
total = O(NlogN)
```

## Pointer Movement Diagram

```text
[1, 2, 4, 7, 9, 11]
 ^                 ^
 l                 r

l only moves right
r only moves left
```

## Recognition Checklist

```text
[ ] Is array sorted or sortable?
[ ] Does moving left pointer increase/decrease predictably?
[ ] Does moving right pointer increase/decrease predictably?
[ ] Is it about pair/triplet/window?
```

---

# 018. Sliding Window Complexity

## Mental Model

A sliding window is a moving segment.

```text
[ a0 a1 a2 a3 a4 a5 ]
      L------R
```

Each index:

```text
enters window once
leaves window once
```

Total:

```text
O(N)
```

## Longest Subarray Sum <= K With Positive Values

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    long long k;
    cin >> n >> k;

    vector<int> a(n);
    for (int &x : a) cin >> x;

    int l = 0;
    long long sum = 0;
    int best = 0;

    for (int r = 0; r < n; r++) {
        sum += a[r];

        while (sum > k) {
            sum -= a[l];
            l++;
        }

        best = max(best, r - l + 1);
    }

    cout << best << "
";
    return 0;
}
```

## Dry Run

```text
a = [2,1,3,2], k = 4

+---+------+-----+-----+------+
| r | a[r] | sum | l   | best |
+---+------+-----+-----+------+
| 0 | 2    | 2   | 0   | 1    |
| 1 | 1    | 3   | 0   | 2    |
| 2 | 3    | 6   | shrink -> sum=4,l=1 | 2 |
| 3 | 2    | 6   | shrink -> sum=5,l=2; shrink -> sum=2,l=3 | 2 |
+---+------+-----+-----+------+
```

## Warning

Sliding window with sum constraints usually needs non-negative numbers. Negative numbers break monotonicity.

---

# 019. Binary Search Complexity

## Mental Model

Binary search is for monotonic truth.

```text
false false false false true true true
                    ^
              first true
```

## Binary Search On Answer

Problem form:

```text
Find minimum X such that can(X) is true.
```

Example:

```text
minimum ship capacity
```

```cpp
#include <bits/stdc++.h>
using namespace std;

bool canShip(const vector<int>& w, int days, long long cap) {
    int usedDays = 1;
    long long cur = 0;

    for (int x : w) {
        if (x > cap) return false;

        if (cur + x <= cap) {
            cur += x;
        } else {
            usedDays++;
            cur = x;
        }
    }

    return usedDays <= days;
}

int main() {
    int n, days;
    cin >> n >> days;

    vector<int> w(n);
    long long sum = 0, mx = 0;

    for (int &x : w) {
        cin >> x;
        sum += x;
        mx = max(mx, (long long)x);
    }

    long long l = mx, r = sum;
    long long ans = sum;

    while (l <= r) {
        long long mid = l + (r - l) / 2;

        if (canShip(w, days, mid)) {
            ans = mid;
            r = mid - 1;
        } else {
            l = mid + 1;
        }
    }

    cout << ans << "
";
    return 0;
}
```

Complexity:

```text
canShip = O(N)
binary search = O(log SUM)
total = O(N log SUM)
```

## CM Recognition Words

```text
minimum maximum
maximum minimum
can we do it with X?
answer space is huge
monotonic feasibility
```

---

# 020. Graph Complexity

## Mental Model

Graph complexity uses:

```text
V = number of vertices
E = number of edges
```

## BFS / DFS

```text
O(V + E)
```

Why?

```text
visit every node once
inspect every edge once/twice
```

## BFS Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n, m;
    cin >> n >> m;

    vector<vector<int>> g(n);

    for (int i = 0; i < m; i++) {
        int u, v;
        cin >> u >> v;
        --u; --v;

        g[u].push_back(v);
        g[v].push_back(u);
    }

    vector<int> dist(n, -1);
    queue<int> q;

    dist[0] = 0;
    q.push(0);

    while (!q.empty()) {
        int u = q.front();
        q.pop();

        for (int v : g[u]) {
            if (dist[v] == -1) {
                dist[v] = dist[u] + 1;
                q.push(v);
            }
        }
    }

    for (int d : dist) cout << d << " ";
    return 0;
}
```

## Dijkstra Complexity

Using priority queue:

```text
O((V + E) log V)
```

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n, m;
    cin >> n >> m;

    vector<vector<pair<int,int>>> g(n);

    for (int i = 0; i < m; i++) {
        int u, v, w;
        cin >> u >> v >> w;
        --u; --v;

        g[u].push_back({v, w});
        g[v].push_back({u, w});
    }

    const long long INF = 4e18;
    vector<long long> dist(n, INF);

    priority_queue<pair<long long,int>,
                   vector<pair<long long,int>>,
                   greater<pair<long long,int>>> pq;

    dist[0] = 0;
    pq.push({0, 0});

    while (!pq.empty()) {
        auto [d, u] = pq.top();
        pq.pop();

        if (d != dist[u]) continue;

        for (auto [v, w] : g[u]) {
            if (dist[v] > d + w) {
                dist[v] = d + w;
                pq.push({dist[v], v});
            }
        }
    }

    for (long long d : dist) cout << d << " ";
    return 0;
}
```

## Graph Complexity Table

```text
BFS/DFS              O(V + E)
Topo sort            O(V + E)
DSU union/find       almost O(1)
Dijkstra             O((V + E)logV)
Bellman-Ford         O(VE)
Floyd-Warshall       O(V³)
MST Kruskal          O(ElogE)
```

---

# 021. DP Complexity

## Mental Model

DP complexity:

```text
number of states × transitions per state
```

This is the most important DP formula.

## 1D DP

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<long long> dp(n + 2, 0);

    dp[0] = 0;
    dp[1] = 1;

    for (int i = 2; i <= n; i++) {
        dp[i] = dp[i - 1] + dp[i - 2];
    }

    cout << dp[n] << "
";
    return 0;
}
```

Complexity:

```text
states = N
transition = O(1)
total = O(N)
```

## 2D Grid DP

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n, m;
    cin >> n >> m;

    vector<vector<long long>> dp(n, vector<long long>(m, 0));
    dp[0][0] = 1;

    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            if (i > 0) dp[i][j] += dp[i - 1][j];
            if (j > 0) dp[i][j] += dp[i][j - 1];
        }
    }

    cout << dp[n - 1][m - 1] << "
";
    return 0;
}
```

```text
states = N*M
transition = 2
total = O(N*M)
```

## DP State Counting Diagram

```text
dp[i][j]

i can take N values
j can take M values

Total states = N*M

Each state checks 2 previous states
Total = N*M*2 = O(N*M)
```

## CM DP Questions

Before coding DP, ask:

```text
1. What is the state?
2. How many states exist?
3. How many transitions per state?
4. Can memory be optimized?
```

---

# 022. STL Complexity Traps

## Must-Know STL Table

```text
vector push_back             amortized O(1)
vector access by index        O(1)
vector insert middle          O(N)
vector erase middle           O(N)
sort                          O(NlogN)
lower_bound vector            O(logN)
set/map insert/find/erase     O(logN)
unordered_map avg insert/find O(1)
priority_queue push/pop       O(logN)
queue push/pop                O(1)
stack push/pop                O(1)
deque push_front/back         O(1)
```

## Hidden Trap: vector erase

Bad:

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<int> v(n);
    for (int &x : v) cin >> x;

    while (!v.empty()) {
        v.erase(v.begin()); // O(N) each time
    }

    return 0;
}
```

Total:

```text
O(N²)
```

Better:

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    queue<int> q;

    for (int i = 0; i < n; i++) {
        int x;
        cin >> x;
        q.push(x);
    }

    while (!q.empty()) {
        q.pop(); // O(1)
    }

    return 0;
}
```

## Hidden Trap: lower_bound on set

```cpp
set<int> s;
auto it = lower_bound(s.begin(), s.end(), x); // O(N) generic algorithm
```

Better:

```cpp
auto it = s.lower_bound(x); // O(logN)
```

---

# 023. Constraint to Algorithm Decision Tree

## Master Decision Tree

```text
Read N
 |
 v
N <= 10 ?
 |-- yes --> permutations / full backtracking O(N!)
 |
 no
 |
 v
N <= 20 ?
 |-- yes --> subsets / bitmask / meet-in-middle O(2^N)
 |
 no
 |
 v
N <= 500 ?
 |-- yes --> O(N^3), Floyd, interval DP
 |
 no
 |
 v
N <= 5000 ?
 |-- yes --> O(N^2), pairs, 2D DP
 |
 no
 |
 v
N <= 2e5 ?
 |-- yes --> O(NlogN) or O(N)
 |
 no
 |
 v
N up to 1e9 / 1e18 ?
 |-- yes --> O(logN), O(1), math, binary search
```

## Constraint Table

```text
N <= 10          N!, 2^N
N <= 20          2^N, N*2^N
N <= 100         N^3, maybe N^4
N <= 500         N^3
N <= 2000        N^2
N <= 1e5/2e5     NlogN, N
N <= 1e6         N or NlogN with care
N <= 1e9         logN / sqrtN / math
N <= 1e18        logN / digit / math
```

## C++ Constraint Helper

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long n;
    cin >> n;

    if (n <= 10) {
        cout << "Permutation/backtracking may pass
";
    } else if (n <= 20) {
        cout << "Subset/bitmask may pass
";
    } else if (n <= 500) {
        cout << "O(N^3) may pass
";
    } else if (n <= 5000) {
        cout << "O(N^2) may pass
";
    } else if (n <= 200000) {
        cout << "Need O(NlogN) or O(N)
";
    } else {
        cout << "Need O(logN), O(1), or math
";
    }

    return 0;
}
```

---

# 024. Brute → Better → Optimal Playbook

## Core Pattern

```text
Brute force:
    directly simulate all possibilities

Better:
    remove repeated work

Optimal:
    exploit structure / monotonicity / precomputation
```

## Common Upgrades

```text
O(N³) -> O(N²)       prefix sum
O(N²) -> O(N)        hash/frequency
O(N²) -> O(NlogN)    sorting + two pointers
O(NQ) -> O(N+Q)      prefix sum
O(QlogN)             Fenwick/segment tree
O(2^N) -> DP         memoization
O(N) search -> O(logN) binary search
```

## Example: Range Sum

Bad O(NQ):

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n, q;
    cin >> n >> q;

    vector<int> a(n);
    for (int &x : a) cin >> x;

    while (q--) {
        int l, r;
        cin >> l >> r;
        --l; --r;

        long long sum = 0;
        for (int i = l; i <= r; i++) {
            sum += a[i];
        }

        cout << sum << "
";
    }

    return 0;
}
```

Good O(N + Q):

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n, q;
    cin >> n >> q;

    vector<long long> pref(n + 1, 0);

    for (int i = 0; i < n; i++) {
        long long x;
        cin >> x;
        pref[i + 1] = pref[i] + x;
    }

    while (q--) {
        int l, r;
        cin >> l >> r;
        --l; --r;

        cout << pref[r + 1] - pref[l] << "
";
    }

    return 0;
}
```

---

# 025. TLE Debugging Playbook

## TLE Meaning

TLE means at least one is true:

```text
too many operations
wrong complexity
hidden O(N) operation inside loop
recursion explosion
slow IO
infinite loop
bad constant factor
```

## TLE Checklist

```text
[ ] Re-read max constraints.
[ ] Estimate worst-case operations.
[ ] Check nested loops.
[ ] Check sort inside loop.
[ ] Check vector erase/insert inside loop.
[ ] Check map/set overhead.
[ ] Check unordered_map worst-case/hashing issue.
[ ] Check recursion branching.
[ ] Check repeated BFS/DFS.
[ ] Check missing fast IO.
[ ] Check binary search infinite loop.
```

## Common TLE Bug: Sorting Inside Loop

```cpp
for (int i = 0; i < n; i++) {
    sort(a.begin(), a.end());
}
```

Complexity:

```text
N times * NlogN = O(N²logN)
```

## Common TLE Bug: Binary Search No Progress

Bad:

```cpp
while (l < r) {
    int mid = (l + r) / 2;
    if (ok(mid)) r = mid;
    else l = mid; // stuck when l+1=r
}
```

Good:

```cpp
while (l < r) {
    int mid = l + (r - l) / 2;
    if (ok(mid)) r = mid;
    else l = mid + 1;
}
```

---

# 026. Codeforces A/B/C/D/E Complexity Forms

## Div2 A

Usually:

```text
O(1), O(N), simple math, simulation
```

Recognition:

```text
small implementation
direct formula
parity
min/max
```

## Div2 B

Usually:

```text
O(N), O(NlogN), sorting, greedy, frequency
```

Recognition:

```text
choose order
count something
simple invariant
```

## Div2 C

Usually:

```text
O(N), O(NlogN), binary search, prefix, two pointers, greedy proof
```

Recognition:

```text
large N
hidden monotonicity
need observation
```

## Div2 D

Usually:

```text
O(NlogN), graph, DP, segment tree, advanced greedy
```

Recognition:

```text
multiple constraints
need data structure
states/transitions
```

## Div2 E / CM Zone

Often:

```text
DP optimization
graph + math
combinatorics
binary search + greedy
tree DP
DSU
bitmask
```

CM thinking:

```text
Constraint first
Then pattern family
Then proof
Then implementation
```

---

# 027. FAANG Interview Complexity Discussion

## What Interviewers Expect

They want to hear:

```text
brute force complexity
bottleneck
optimization idea
final time complexity
final space complexity
tradeoff
```

## Example Answer Style

Problem: two sum.

```text
Brute force checks every pair, so time is O(N²), space is O(1).
We can optimize by storing seen values in a hash set.
For each number x, we check whether target - x appeared earlier.
That gives average O(N) time and O(N) space.
```

## FAANG Trap

Do not only say:

```text
It is O(N)
```

Say why:

```text
Each element is inserted once and looked up once in the hash set.
Average hash operations are O(1), so total is O(N).
The set can store up to N elements, so space is O(N).
```

## Interview Complexity Template

```text
Time:
    We visit each element ___ times.
    Each operation costs ___.
    Total = ___.

Space:
    We store ___.
    Max size = ___.
    Total = ___.

Tradeoff:
    We improved time from ___ to ___ by using ___ extra space.
```

---

# 099. Mega Cheat Sheet

## Big-O Map

```text
O(1)        formula / direct access
O(logN)     divide by 2 / binary search
O(N)        one pass
O(NlogN)    sorting / divide-conquer / heap / segment tree
O(N²)       pairs / 2D DP
O(N³)       triples / Floyd / interval DP
O(2^N)      subsets
O(N!)       permutations
```

## Constraints

```text
N <= 10          permutation/backtracking
N <= 20          bitmask/subsets
N <= 500         cubic
N <= 2000        quadratic
N <= 2e5         NlogN / N
N <= 1e9         logN / math
N <= 1e18        logN / math / digit
```

## Pattern Complexity

```text
Prefix Sum             build O(N), query O(1)
Two Pointers           O(N) after sorting if needed
Sliding Window         O(N)
Binary Search          O(logN) or O(NlogX)
BFS/DFS                O(V+E)
Dijkstra               O((V+E)logV)
Floyd-Warshall         O(V³)
DP                     states * transitions
Segment Tree           build O(N), query/update O(logN)
Fenwick Tree           query/update O(logN)
DSU                    almost O(1)
Trie                   O(total characters)
```

## Final Contest Checklist

```text
Before coding:
[ ] What is max N?
[ ] What is max sum/value?
[ ] How many test cases?
[ ] Is total N bounded across tests?
[ ] What complexity is allowed?
[ ] What pattern matches?

Before submit:
[ ] Any O(N²) with N=2e5?
[ ] Any vector erase in loop?
[ ] Any sort inside loop?
[ ] Any recursion depth risk?
[ ] Any long long needed?
[ ] Any hidden O(N) STL call?
[ ] Any binary search infinite loop?
[ ] Fast IO enabled?
```

## Final CP Template

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

void solve() {
    /*
        Complexity habit:
        1. Read N.
        2. Estimate allowed complexity.
        3. Avoid hidden expensive operations.
    */

    int n;
    cin >> n;

    vector<ll> a(n);
    for (ll &x : a) cin >> x;

    ll sum = 0;
    for (ll x : a) sum += x;

    cout << sum << "
";
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t = 1;
    cin >> t;

    while (t--) {
        solve();
    }

    return 0;
}
```

## One Picture To Remember

```text
CONSTRAINTS
    ↓
ALLOWED COMPLEXITY
    ↓
PATTERN FAMILY
    ↓
CODE
    ↓
AC
```

Never start from code.

Start from constraints.
