# MiniComplexityAnalysis_Ultimate.md

## Beginner-Friendly Complexity Analysis for CP, Codeforces CM, and FAANG Interviews

> **Mission:** Turn complexity analysis from “I memorize Big-O names” into “I can read constraints, predict the required algorithm, avoid TLE, explain tradeoffs in interviews, and debug performance under pressure.”

---

# Clickable Index

- [000. The One Mental Model](#000-the-one-mental-model)
- [001. Why Complexity Exists](#001-why-complexity-exists)
- [002. Big-O Without Fear](#002-big-o-without-fear)
- [003. ASCII Growth Map](#003-ascii-growth-map)
- [004. Counting Operations Like a CM](#004-counting-operations-like-a-cm)
- [005. O(1) Constant Time](#005-o1-constant-time)
- [006. O(N) Linear Time](#006-on-linear-time)
- [007. O(log N) Logarithmic Time](#007-olog-n-logarithmic-time)
- [008. O(N log N)](#008-on-log-n)
- [009. O(N²)](#009-on2)
- [010. O(N³)](#010-on3)
- [011. O(2^N)](#011-o2n)
- [012. O(N!)](#012-on-factorial)
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
- [023. Constraint → Algorithm Decision Tree](#023-constraint--algorithm-decision-tree)
- [024. Brute → Better → Optimal Transformation](#024-brute--better--optimal-transformation)
- [025. TLE Debugging Playbook](#025-tle-debugging-playbook)
- [026. Codeforces A/B/C/D/E Complexity Forms](#026-codeforces-abcde-complexity-forms)
- [027. FAANG Complexity Explanation Framework](#027-faang-complexity-explanation-framework)
- [028. Eleven ASCII Diagrams To Remember](#028-eleven-ascii-diagrams-to-remember)
- [099. Mega Cheat Sheet](#099-mega-cheat-sheet)

---

# 000. The One Mental Model

Complexity analysis has one master idea:

```text
INPUT SIZE
    ↓
HOW MANY THINGS CAN MY CODE TOUCH?
    ↓
CAN THAT MANY TOUCHES FIT IN TIME + MEMORY?
```

A beginner asks:

```text
Does my code work for sample?
```

A strong CP/FAANG engineer asks:

```text
Will my code survive the maximum hidden test?
```

A Candidate Master-level competitor asks:

```text
What is the intended complexity from constraints?
```

## The Real Flow

```text
Problem Statement
      ↓
Constraints
      ↓
Allowed Complexity
      ↓
Possible Pattern Families
      ↓
Proof / Invariant
      ↓
Implementation
      ↓
TLE / WA / Overflow Check
```

## Why This Is Powerful

Complexity analysis saves you from wasting time. If `N = 2e5`, then a double loop over all pairs is almost certainly dead. You should immediately think:

```text
O(N)
O(N log N)
maybe O(N sqrtN)
maybe O(log Value)
```

That constraint already removes many wrong solutions.

## Beginner Translation

Big-O is not “advanced math.” It is just a shortcut for saying:

```text
When input gets bigger, how quickly does work grow?
```

---

# 001. Why Complexity Exists

## Story

Imagine you write a pair-sum solution. It passes:

```text
N = 5
```

But hidden tests contain:

```text
N = 200000
```

Your code is logically correct, but still fails because it performs too much work.

This is why complexity exists.

```text
Correctness answers: "Will it produce the right answer?"
Complexity answers:  "Will it produce the right answer fast enough?"
```

## Example: Pair Sum

Given `N` numbers, check if any pair sums to `X`.

### Brute Force O(N²)

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

### Operation Count

```text
For each i, j checks all later elements.

N = 5:
i=0 -> 4 checks
i=1 -> 3 checks
i=2 -> 2 checks
i=3 -> 1 check
i=4 -> 0 checks

Total = 10
```

Formula:

```text
N(N-1)/2 = O(N²)
```

For `N = 200000`:

```text
200000 * 199999 / 2 ≈ 20,000,000,000 checks
```

This is far too much.

### Better O(N) Average

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

## Mental Upgrade

```text
Brute force:
    "Try every pair."

Optimized:
    "Remember previous values so I do not search again."
```

## One Picture To Remember

```text
Sample Test
   ↓
Checks correctness lightly

Max Constraint
   ↓
Checks algorithm quality
```

---

# 002. Big-O Without Fear

Big-O tells us how runtime grows.

It does not care about exact milliseconds. It cares about growth shape.

```text
O(1)        does not grow
O(log N)    grows very slowly
O(N)        grows directly with input
O(N log N)  grows a bit more than linear
O(N²)       grows very fast
O(2^N)      explodes
O(N!)       explodes harder
```

## Why We Drop Constants

Suppose:

```text
Algorithm A = 100N
Algorithm B = N²
```

For small `N`, `100N` may look worse.

For large `N`, `N²` destroys it.

```text
N = 10:
100N = 1000
N²   = 100

N = 1,000,000:
100N = 100,000,000
N²   = 1,000,000,000,000
```

So Big-O keeps the dominant long-term growth.

## Common Simplifications

```text
O(5N)          -> O(N)
O(N + 100)     -> O(N)
O(N² + N)      -> O(N²)
O(N logN + N)  -> O(N logN)
O(3 * 2^N)     -> O(2^N)
```

## C++ Working Demo: Compare Estimates

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long n;
    cin >> n;

    cout << "N = " << n << "\n";
    cout << "log2(N) = " << (long long)log2(n) << "\n";
    cout << "N log2(N) = " << n * (long long)log2(n) << "\n";

    if (n <= 1000000) {
        cout << "N^2 = " << n * n << "\n";
    } else {
        cout << "N^2 is too large for normal contest runtime\n";
    }

    return 0;
}
```

## Beginner Mistake

Do not say:

```text
There are two loops, so it is O(N²).
```

Check how the loops move.

Example:

```cpp
for (int i = 1; i <= n; i *= 2) {
    for (int j = 0; j < n; j++) {
        // O(1)
    }
}
```

Outer loop:

```text
1, 2, 4, 8, 16, ...
```

So outer loop is `O(log N)`, inner is `O(N)`, total is:

```text
O(N log N)
```

---

# 003. ASCII Growth Map

## Diagram 1: Big-O Growth Curve

```text
Operations
^
|
|                                            O(N!)
|                                      ******
|                                  ****
|                              ****
|                         O(2^N)
|                      ***
|                  ***
|              O(N^3)
|           ***
|        O(N^2)
|     ***
|   O(N logN)
|  **
| O(N)
|*
| O(logN)
|*
| O(1)
+------------------------------------------------------> N
```

## Diagram 2: Practical Contest Scale

```text
N value        Good complexity
--------------------------------------------
10             O(N!), O(2^N)
20             O(2^N), O(N*2^N)
500            O(N^3)
2,000          O(N^2)
200,000        O(N logN), O(N)
1,000,000      O(N), careful O(NlogN)
1e9            O(logN), O(sqrtN), O(1)
1e18           O(logN), math, digit tricks
```

## Diagram 3: Algorithm Choice Funnel

```text
All possible algorithms
        |
        v
Remove impossible complexities using constraints
        |
        v
Keep only feasible families
        |
        v
Use problem keywords to select pattern
        |
        v
Prove and code
```

---

# 004. Counting Operations Like a CM

The most important skill is counting “visits.”

## Rule

```text
Complexity = total number of meaningful visits/states/transitions
```

## Simple Loop

```cpp
for (int i = 0; i < n; i++) {
    cout << i << "\n";
}
```

`cout` runs `N` times.

```text
O(N)
```

## Full Grid Loop

```cpp
for (int i = 0; i < n; i++) {
    for (int j = 0; j < n; j++) {
        cout << i << " " << j << "\n";
    }
}
```

ASCII:

```text
      j →
      0 1 2 3
i=0   x x x x
i=1   x x x x
i=2   x x x x
i=3   x x x x

4 * 4 = 16 visits
N * N = O(N²)
```

## Triangular Loop

```cpp
long long operations = 0;

for (int i = 0; i < n; i++) {
    for (int j = i + 1; j < n; j++) {
        operations++;
    }
}
```

ASCII:

```text
      j →
      0 1 2 3 4
i=0   . x x x x
i=1   . . x x x
i=2   . . . x x
i=3   . . . . x
i=4   . . . . .

Only upper triangle is visited.
Still O(N²).
```

## Working Counter

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

    cout << operations << "\n";
    return 0;
}
```

## Dry Run Table for N = 5

```text
+---+----------------+-------+
| i | j values       | count |
+---+----------------+-------+
| 0 | 1,2,3,4        | 4     |
| 1 | 2,3,4          | 3     |
| 2 | 3,4            | 2     |
| 3 | 4              | 1     |
| 4 | none           | 0     |
+---+----------------+-------+
Total = 10
```

## CM Trick

If a loop pointer never moves backward, count movement, not nesting.

Example:

```cpp
int r = 0;
for (int l = 0; l < n; l++) {
    while (r < n && some_condition(l, r)) {
        r++;
    }
}
```

This is often `O(N)`, not `O(N²)`, because `r` moves from `0` to `N` only once.

---

# 005. O(1) Constant Time

## Mental Model

O(1) means work does not grow with input size.

```text
N = 10       -> same number of operations
N = 1e9      -> same number of operations
```

Examples:

```text
array index access
simple arithmetic
formula
checking vector size
average unordered_set lookup
```

## Working C++ Example

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {10, 20, 30, 40};

    cout << a[2] << "\n"; // O(1)

    long long x = 100, y = 50;
    cout << x + y << "\n"; // O(1)

    return 0;
}
```

## CP Example: Count Multiples in [L, R]

Problem:

```text
Count numbers divisible by k in range [L, R].
```

Bad:

```cpp
long long ans = 0;
for (long long x = L; x <= R; x++) {
    if (x % k == 0) ans++;
}
```

If `R - L` is huge, this dies.

Good:

```text
multiples up to R      = floor(R/k)
multiples before L     = floor((L-1)/k)
multiples in [L, R]    = floor(R/k) - floor((L-1)/k)
```

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long L, R, k;
    cin >> L >> R >> k;

    cout << R / k - (L - 1) / k << "\n";
    return 0;
}
```

## FAANG Explanation

> This is O(1) because the algorithm performs a constant number of arithmetic operations regardless of the size of the interval.

---

# 006. O(N) Linear Time

## Mental Model

Every element is touched once or a constant number of times.

```text
Array:
[ a0 ][ a1 ][ a2 ][ a3 ][ a4 ]
  ↓     ↓     ↓     ↓     ↓
one visit each
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

    cout << sum << "\n";
    return 0;
}
```

## Linear Pattern Forms

```text
frequency count
prefix build
max/min scan
Kadane
single-pass greedy
one BFS layer scan
hash lookup over array
```

## Example: Frequency Count

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    string s;
    cin >> s;

    vector<int> freq(26, 0);

    for (char c : s) {
        freq[c - 'a']++;
    }

    for (int i = 0; i < 26; i++) {
        if (freq[i] > 0) {
            cout << char('a' + i) << " " << freq[i] << "\n";
        }
    }

    return 0;
}
```

Complexity:

```text
scan string = O(N)
print 26 letters = O(1)
total = O(N)
```

## Dry Run

```text
s = "abca"

freq initially all 0

read a -> freq[a]=1
read b -> freq[b]=1
read c -> freq[c]=1
read a -> freq[a]=2
```

---

# 007. O(log N) Logarithmic Time

## Mental Model

At each step, remove a large fraction of search space.

Usually half.

```text
N
N/2
N/4
N/8
...
1
```

## ASCII Shrinking

```text
[--------------------------------] 32
[----------------]                 16
[--------]                         8
[----]                             4
[--]                               2
[-]                                1
```

It takes about `log2(N)` steps.

## Binary Search Code

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
            cout << "FOUND\n";
            return 0;
        }

        if (a[mid] < target) l = mid + 1;
        else r = mid - 1;
    }

    cout << "NOT FOUND\n";
    return 0;
}
```

## Dry Run

```text
Array:  [1, 3, 5, 7, 9, 11, 13]
Target: 11

+------+---+---+-----+--------+----------+
| step | l | r | mid | a[mid] | action   |
+------+---+---+-----+--------+----------+
| 1    | 0 | 6 | 3   | 7      | go right |
| 2    | 4 | 6 | 5   | 11     | found    |
+------+---+---+-----+--------+----------+
```

## Recognition

```text
sorted array
monotonic condition
answer is in huge range
find first true
find last false
exponentiation by squaring
```

---

# 008. O(N log N)

## Mental Model

`N log N` usually means:

```text
N work across logN levels
```

or:

```text
N items, each doing logN work
```

Examples:

```text
sorting
merge sort
heap operations for N elements
Dijkstra
segment tree queries
balanced BST operations
```

## Merge Sort ASCII

```text
Level 0:                 [8 elements]             work = N

Level 1:          [4 elements] [4 elements]       work = N

Level 2:       [2][2]       [2][2]                work = N

Level 3:     [1][1][1][1][1][1][1][1]            work = N

Number of levels = logN
Total = N logN
```

## Sorting Code

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
    cout << "\n";

    return 0;
}
```

## Why Sorting Is Important in CP

Sorting creates order. Order creates structure. Structure enables:

```text
two pointers
binary search
greedy
interval merging
sweep line
```

## Example: Sorting + Two Pointers

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
            cout << "YES\n";
            return 0;
        }

        if (sum < target) l++;
        else r--;
    }

    cout << "NO\n";
    return 0;
}
```

Complexity:

```text
sort = O(N logN)
two pointers = O(N)
total = O(N logN)
```

---

# 009. O(N²)

## Mental Model

O(N²) means two independent dimensions.

```text
For each i:
    try many j
```

## Pair Grid ASCII

```text
      j →
      0 1 2 3 4
i=0   x x x x x
i=1   x x x x x
i=2   x x x x x
i=3   x x x x x
i=4   x x x x x

Total cells = N²
```

## Upper Triangle Still O(N²)

```text
      j →
      0 1 2 3 4
i=0   . x x x x
i=1   . . x x x
i=2   . . . x x
i=3   . . . . x
i=4   . . . . .

About N²/2 cells -> still O(N²)
```

## Count Equal Pairs O(N²)

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

    cout << pairs << "\n";
    return 0;
}
```

## Optimized O(N)

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

        pairs += freq[x];
        freq[x]++;
    }

    cout << pairs << "\n";
    return 0;
}
```

## CM Recognition

O(N²) is acceptable when:

```text
N <= 2000
N <= 5000 sometimes
```

O(N²) is dangerous when:

```text
N >= 1e5
```

---

# 010. O(N³)

## Mental Model

Three independent choices:

```text
choose left
choose right
scan inside
```

or:

```text
choose i, j, k
```

## Cube ASCII

```text
Dimension 1: i
Dimension 2: j
Dimension 3: k

N choices * N choices * N choices = N³
```

## Bad Max Subarray O(N³)

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

    cout << best << "\n";
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

    cout << best << "\n";
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

    cout << best << "\n";
    return 0;
}
```

## Transformation Picture

```text
O(N³)
  |
  | precompute range sum
  v
O(N²)
  |
  | observe best subarray ending here
  v
O(N)
```

---

# 011. O(2^N)

## Mental Model

Each item has two choices:

```text
take
not take
```

For `N` items:

```text
2 * 2 * 2 * ... * 2 = 2^N
```

## Subset Tree ASCII

```text
idx=0
├── skip
│   ├── skip
│   │   ├── skip
│   │   └── take
│   └── take
│       ├── skip
│       └── take
└── take
    ├── skip
    │   ├── skip
    │   └── take
    └── take
        ├── skip
        └── take
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
        cout << "\n";
        return;
    }

    dfs(idx + 1);

    cur.push_back(a[idx]);
    dfs(idx + 1);
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
        cout << "}\n";
    }

    return 0;
}
```

## When It Passes

```text
N <= 20 usually okay
N <= 25 sometimes with pruning or meet-in-middle
N >= 40 usually not direct 2^N
```

---

# 012. O(N Factorial)

## Mental Model

Permutation means arranging everything.

```text
First position:  N choices
Second position: N-1 choices
Third position:  N-2 choices
...
Total = N!
```

## Permutation Tree

```text
For N = 4

level 0: choose 1 of 4
level 1: choose 1 of 3
level 2: choose 1 of 2
level 3: choose 1 of 1

4 * 3 * 2 * 1 = 24
```

## next_permutation Code

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
        cout << "\n";
    } while (next_permutation(a.begin(), a.end()));

    return 0;
}
```

## Backtracking Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> a, perm, used;

void dfs() {
    if ((int)perm.size() == (int)a.size()) {
        for (int x : perm) cout << x << " ";
        cout << "\n";
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

## Limit Sense

```text
8!  = 40320
10! = 3628800
12! = 479001600
```

So permutation brute force is only for very small `N`.

---

# 013. Space Complexity

## Mental Model

Space complexity is memory growth.

Count:

```text
arrays
vectors
maps
sets
DP tables
graph adjacency
recursion stack
```

## Memory Units

```text
int       4 bytes
long long 8 bytes
double    8 bytes
char      1 byte
pair<int,int> 8 bytes
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

    cout << sum << "\n";
    return 0;
}
```

Only a few variables.

```text
O(1)
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

    cout << a.size() << "\n";
    return 0;
}
```

The vector grows with `N`.

```text
O(N)
```

## Memory Table

```text
vector<int>(1e6)          ≈ 4 MB
vector<int>(1e7)          ≈ 40 MB
vector<long long>(1e7)    ≈ 80 MB
int dp[5000][5000]        ≈ 100 MB
long long dp[5000][5000]  ≈ 200 MB
```

## CM Memory Habit

Before creating DP table:

```text
states * bytes per state <= memory limit
```

Example:

```text
N = 5000
dp[N][N] as long long

5000 * 5000 * 8 = 200,000,000 bytes ≈ 200 MB
```

If memory limit is 256 MB, this is risky with overhead.

---

# 014. Recursion Stack Complexity

## Mental Model

Every recursive call waits on stack.

```text
f(5)
└── f(4)
    └── f(3)
        └── f(2)
            └── f(1)
```

Depth is `O(N)`.

## Factorial Code

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

    cout << fact(n) << "\n";
    return 0;
}
```

```text
time  = O(N)
stack = O(N)
```

## DFS Stack Overflow

Recursive DFS can fail for deep graphs.

Bad risk:

```text
N = 200000
tree is a chain
recursive DFS depth = 200000
stack overflow possible
```

## Iterative DFS

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

Divide-and-conquer problems follow:

```text
T(N) = aT(N/b) + f(N)
```

Meaning:

```text
a     = number of recursive calls
N/b   = size of each call
f(N)  = work done outside recursion
```

## Binary Search

```text
T(N) = T(N/2) + O(1)
```

ASCII:

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

Number of levels:

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

ASCII:

```text
                      N
                /           \
             N/2             N/2
           /     \         /     \
        N/4     N/4     N/4     N/4
       /  \     /  \    /  \    /  \
      1    1   1    1  1    1  1    1
```

Work per level:

```text
Level 0: N
Level 1: N/2 + N/2 = N
Level 2: N/4 + N/4 + N/4 + N/4 = N
```

Levels:

```text
logN
```

Total:

```text
O(N logN)
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

Amortized means:

```text
Some operations are expensive,
but the total cost over many operations is still good.
```

## Vector push_back

Most pushes are O(1). Sometimes vector resizes.

```text
capacity 1 full -> resize to 2
capacity 2 full -> resize to 4
capacity 4 full -> resize to 8
```

ASCII:

```text
push push resize-copy
push push resize-copy
push push push push resize-copy

Expensive events are rare.
Average per push is O(1).
```

## Vector Capacity Demo

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> v;

    for (int i = 0; i < 20; i++) {
        v.push_back(i);

        cout << "size=" << v.size()
             << " capacity=" << v.capacity()
             << "\n";
    }

    return 0;
}
```

## Monotonic Stack O(N)

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

    cout << st.size() << "\n";
    return 0;
}
```

Why O(N)?

```text
Each element is pushed once.
Each element is popped at most once.
Total stack operations <= 2N.
```

---

# 017. Two Pointers Complexity

## Mental Model

Two pointers is linear when each pointer moves in one direction.

```text
l moves right at most N times
r moves left/right at most N times
total movement <= 2N
```

## ASCII

```text
[1, 2, 4, 7, 9, 11]
 ^                 ^
 l                 r

l → → →
r ← ← ←
```

## Pair Sum Sorted

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
            cout << "YES\n";
            return 0;
        }

        if (sum < target) l++;
        else r--;
    }

    cout << "NO\n";
    return 0;
}
```

Complexity:

```text
sorting = O(NlogN)
pointer movement = O(N)
total = O(NlogN)
```

## Recognition Checklist

```text
[ ] Pair/triplet/subarray?
[ ] Sorted or sortable?
[ ] Moving pointer changes value predictably?
[ ] Each pointer moves monotonically?
```

---

# 018. Sliding Window Complexity

## Mental Model

Sliding window is two pointers for contiguous segments.

```text
Array:
[ a0 a1 a2 a3 a4 a5 ]
      L------R
```

Each element:

```text
enters once
leaves once
```

Total:

```text
O(N)
```

## Longest Subarray Sum <= K, Positive Numbers

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

    cout << best << "\n";
    return 0;
}
```

## Dry Run

```text
a = [2, 1, 3, 2], k = 4

Start: l=0, sum=0, best=0

r=0 add 2 -> sum=2  window [2]     best=1
r=1 add 1 -> sum=3  window [2,1]   best=2
r=2 add 3 -> sum=6  too big
    remove a[l]=2 -> sum=4, l=1
    window [1,3] best=2
r=3 add 2 -> sum=6 too big
    remove a[l]=1 -> sum=5, l=2
    remove a[l]=3 -> sum=2, l=3
    window [2] best=2
```

## Important Warning

Sliding window sum works cleanly when values are non-negative.

With negative numbers:

```text
expanding right may decrease sum
shrinking left may increase sum
```

So monotonicity breaks.

---

# 019. Binary Search Complexity

## Mental Model

Binary search needs monotonic truth.

```text
false false false false true true true
                    ^
                answer
```

## Binary Search on Answer

Question form:

```text
Find minimum X such that can(X) is true.
```

Common phrases:

```text
minimum maximum
maximum minimum
can we do with X?
at most K
at least K
largest possible minimum
smallest possible maximum
```

## Example: Minimum Ship Capacity

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

    cout << ans << "\n";
    return 0;
}
```

Complexity:

```text
canShip = O(N)
binary search range = O(log sum)
total = O(N log sum)
```

## Infinite Loop Warning

Bad:

```cpp
while (l < r) {
    int mid = (l + r) / 2;
    if (ok(mid)) r = mid;
    else l = mid; // can get stuck
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

# 020. Graph Complexity

## Mental Model

Graphs use:

```text
V = vertices
E = edges
```

## BFS / DFS

```text
O(V + E)
```

Reason:

```text
each node visited once
each edge inspected once/twice
```

## ASCII Graph

```text
0 --- 1 --- 2
|     |
3 --- 4

V = 5
E = 5
BFS/DFS = O(V + E)
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

## Dijkstra

```text
O((V + E) log V)
```

Because priority queue operations cost `logV`.

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

## Graph Table

```text
BFS / DFS             O(V + E)
Topological sort      O(V + E)
Dijkstra              O((V + E) log V)
Bellman-Ford          O(VE)
Floyd-Warshall        O(V³)
Kruskal MST           O(E log E)
DSU operations        almost O(1)
```

---

# 021. DP Complexity

## Mental Model

DP complexity is:

```text
number of states × transitions per state
```

This is the formula to remember.

## State Counting Diagram

```text
dp[i][j]

i has N possibilities
j has M possibilities

Total states = N * M
```

If each state checks 2 transitions:

```text
O(N * M * 2) = O(NM)
```

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

    cout << dp[n] << "\n";
    return 0;
}
```

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

    cout << dp[n - 1][m - 1] << "\n";
    return 0;
}
```

```text
states = N*M
transition = O(1)
total = O(N*M)
```

## CM DP Questions

```text
1. What uniquely defines a subproblem?
2. How many values can each dimension take?
3. How many transitions does each state try?
4. Can I reduce memory?
5. Is there repeated work that memoization removes?
```

---

# 022. STL Complexity Traps

## Must-Know Table

```text
vector push_back             amortized O(1)
vector access                O(1)
vector insert/erase middle   O(N)
sort                         O(NlogN)
lower_bound on vector        O(logN)
set/map find/insert/erase    O(logN)
unordered_map average        O(1)
priority_queue push/pop      O(logN)
queue push/pop               O(1)
stack push/pop               O(1)
deque push_front/back        O(1)
```

## Trap 1: vector erase from front

```cpp
while (!v.empty()) {
    v.erase(v.begin());
}
```

Each erase shifts all elements.

```text
N + (N-1) + ... + 1 = O(N²)
```

Use queue/deque.

## Trap 2: Generic lower_bound on set

Bad:

```cpp
auto it = lower_bound(s.begin(), s.end(), x); // O(N)
```

Good:

```cpp
auto it = s.lower_bound(x); // O(logN)
```

## Trap 3: Repeated sort

Bad:

```cpp
for (int i = 0; i < n; i++) {
    sort(a.begin(), a.end());
}
```

Complexity:

```text
O(N² logN)
```

---

# 023. Constraint → Algorithm Decision Tree

## Master Tree

```text
Read N
 |
 v
N <= 10?
 |-- yes --> permutations / heavy backtracking
 |
 no
 |
 v
N <= 20?
 |-- yes --> subsets / bitmask / meet-in-the-middle
 |
 no
 |
 v
N <= 500?
 |-- yes --> O(N³)
 |
 no
 |
 v
N <= 5000?
 |-- yes --> O(N²)
 |
 no
 |
 v
N <= 2e5?
 |-- yes --> O(NlogN) or O(N)
 |
 no
 |
 v
N up to 1e9 or 1e18?
 |-- yes --> O(logN), O(1), math
```

## Constraint Table

```text
N <= 10          N!, 2^N
N <= 20          2^N, N*2^N
N <= 100         N^3 or N^4 sometimes
N <= 500         N^3
N <= 2000        N^2
N <= 2e5         NlogN / N
N <= 1e6         N, careful NlogN
N <= 1e9         logN / sqrtN / math
N <= 1e18        logN / math
```

## Beginner Rule

Before writing code, say:

```text
N is ___.
Allowed complexity is probably ___.
So I should look for ___ pattern.
```

This one habit improves contest performance massively.

---

# 024. Brute → Better → Optimal Transformation

## Core Idea

Almost every optimization is one of these:

```text
avoid repeated work
store previous results
sort to create order
use monotonicity
precompute answers
use data structure
```

## Upgrade Table

```text
O(N³) -> O(N²)       prefix sum
O(N²) -> O(N)        hash / frequency
O(N²) -> O(NlogN)    sort + two pointers
O(NQ) -> O(N+Q)      prefix sum
O(N) query -> O(logN) segment tree / Fenwick
O(2^N) recursion -> DP memoization
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

        cout << sum << "\n";
    }

    return 0;
}
```

Good O(N+Q):

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

        cout << pref[r + 1] - pref[l] << "\n";
    }

    return 0;
}
```

---

# 025. TLE Debugging Playbook

## What TLE Means

```text
TLE = your code did too much work
```

Possible causes:

```text
wrong complexity
hidden expensive STL call
sort inside loop
vector erase in loop
recursion explosion
too many test cases
slow IO
infinite loop
bad constant factor
```

## TLE Checklist

```text
[ ] What is max N?
[ ] What is sum of N over test cases?
[ ] Did I accidentally write O(N²) for N=2e5?
[ ] Is there a hidden O(N) inside a loop?
[ ] Am I sorting repeatedly?
[ ] Am I erasing from vector front?
[ ] Is recursion branching exponential?
[ ] Is binary search stuck?
[ ] Did I use fast IO?
```

## Fast IO Template

```cpp
ios::sync_with_stdio(false);
cin.tie(nullptr);
```

## Binary Search Infinite Loop

Bad:

```cpp
while (l < r) {
    int mid = (l + r) / 2;
    if (ok(mid)) r = mid;
    else l = mid;
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
O(1)
O(N)
simple math
direct simulation
```

## Div2 B

Usually:

```text
O(N)
O(NlogN)
sorting
frequency
simple greedy
```

## Div2 C

Usually:

```text
O(N)
O(NlogN)
prefix/suffix
binary search
two pointers
greedy with proof
```

## Div2 D

Usually:

```text
O(NlogN)
graph
DP
segment tree
DSU
advanced greedy
```

## Div2 E / CM Zone

Often:

```text
tree DP
graph + math
combinatorics
binary search + greedy
bitmask
DP optimization
```

## CM Mindset

```text
I do not start by coding.
I start by eliminating impossible complexities.
```

---

# 027. FAANG Complexity Explanation Framework

## Interviewers Want

```text
brute force
bottleneck
optimization
time complexity
space complexity
tradeoff
```

## Good Explanation Template

```text
The brute force approach is ___ because ___.
The bottleneck is ___.
We can optimize by ___.
Now each element/state is processed ___ times.
So time complexity is ___.
We store ___, so space complexity is ___.
```

## Example: Two Sum

```text
Brute force checks every pair, so time is O(N²) and space is O(1).
We can use a hash set to remember numbers we have already seen.
For each number x, we check whether target-x exists.
Each lookup and insert is average O(1), and we do it N times.
So time is O(N) average and space is O(N).
```

## Senior-Level Add-on

Mention tradeoff:

```text
We improved time from O(N²) to O(N) by using O(N) extra memory.
```

---

# 028. Eleven ASCII Diagrams To Remember

## Diagram 1: Complexity Pipeline

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

## Diagram 2: Big-O Growth

```text
O(1) < O(logN) < O(N) < O(NlogN) < O(N²) < O(2^N) < O(N!)
```

## Diagram 3: Full Pair Grid

```text
    0 1 2 3
0   x x x x
1   x x x x
2   x x x x
3   x x x x
```

## Diagram 4: Upper Triangle

```text
    0 1 2 3
0   . x x x
1   . . x x
2   . . . x
3   . . . .
```

## Diagram 5: Binary Search Shrink

```text
[----------------]
[--------]
[----]
[--]
[-]
```

## Diagram 6: Merge Sort Tree

```text
        N
      /   \
    N/2   N/2
   / \     / \
 N/4 N/4 N/4 N/4
```

## Diagram 7: Subset Tree

```text
        item
       /    \
   skip      take
   / \       /  \
skip take  skip take
```

## Diagram 8: Sliding Window

```text
[a0 a1 a2 a3 a4 a5]
     L------R
```

## Diagram 9: Two Pointers

```text
[1 2 4 7 9 11]
 ^           ^
 l           r
```

## Diagram 10: DP State Grid

```text
dp[i][j]

      j →
i  x x x x
↓  x x x x
   x x x x
```

## Diagram 11: Recursion Stack

```text
f(5)
└─ f(4)
   └─ f(3)
      └─ f(2)
         └─ f(1)
```

---

# 099. Mega Cheat Sheet

## Big-O Meaning

```text
O(1)        constant
O(logN)     halve search space
O(N)        one pass
O(NlogN)    sort / divide levels / heap
O(N²)       pairs / 2D states
O(N³)       triples / Floyd / interval DP
O(2^N)      subsets
O(N!)       permutations
```

## Constraints → Complexity

```text
N <= 10          O(N!), O(2^N)
N <= 20          O(2^N)
N <= 500         O(N³)
N <= 2000        O(N²)
N <= 2e5         O(NlogN), O(N)
N <= 1e9         O(logN), O(sqrtN), O(1)
N <= 1e18        O(logN), math
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
[ ] What is max value?
[ ] How many test cases?
[ ] Is total N bounded?
[ ] What complexity is allowed?
[ ] What pattern family matches?

Before submit:
[ ] Any O(N²) with N=2e5?
[ ] Any vector erase inside loop?
[ ] Any sort inside loop?
[ ] Any recursion depth > 1e5?
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
        1. Read constraints.
        2. Estimate allowed complexity.
        3. Pick pattern family.
        4. Avoid hidden expensive operations.
    */

    int n;
    cin >> n;

    vector<ll> a(n);
    for (ll &x : a) cin >> x;

    ll sum = 0;
    for (ll x : a) sum += x;

    cout << sum << "\n";
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

## Final One Picture

```text
Do not start from code.

Start from constraints.

CONSTRAINTS → COMPLEXITY → PATTERN → PROOF → CODE → AC
```
