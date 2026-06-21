# MiniComplexityAnalysis.md
## From Basic Big-O to Codeforces Candidate Master / FAANG-Level Complexity Thinking

> **Goal:** Build a complete mental model for time and space complexity using C++17, CP/DSA examples, dry runs, debugging checklists, and contest recognition rules.

---

# Clickable Index

- [000. Core Mental Model](#000-core-mental-model)
- [001. Why Complexity Exists](#001-why-complexity-exists)
- [002. Big-O Mental Model](#002-big-o-mental-model)
- [003. Counting Operations](#003-counting-operations)
- [004. O(1) Constant Time](#004-o1-constant-time)
- [005. O(N) Linear Time](#005-on-linear-time)
- [006. O(log N) Logarithmic Time](#006-olog-n-logarithmic-time)
- [007. O(N log N) Sorting / Divide & Conquer](#007-on-log-n-sorting--divide--conquer)
- [008. O(N²) Nested Loops](#008-on²-nested-loops)
- [009. O(N³) Triple Loops](#009-on³-triple-loops)
- [010. O(2^N) Subsets / Backtracking](#010-o2n-subsets--backtracking)
- [011. O(N!) Permutations](#011-on-permutations)
- [012. Space Complexity](#012-space-complexity)
- [013. Recursion Stack Complexity](#013-recursion-stack-complexity)
- [014. Master Theorem](#014-master-theorem)
- [015. Amortized Analysis](#015-amortized-analysis)
- [016. Two Pointers Complexity](#016-two-pointers-complexity)
- [017. Sliding Window Complexity](#017-sliding-window-complexity)
- [018. Binary Search Complexity](#018-binary-search-complexity)
- [019. Graph Complexity](#019-graph-complexity)
- [020. DP Complexity](#020-dp-complexity)
- [021. STL Complexity](#021-stl-complexity)
- [022. Complexity From Constraints](#022-complexity-from-constraints)
- [023. TLE Debugging](#023-tle-debugging)
- [024. Optimization Playbook](#024-optimization-playbook)
- [099. Complexity Cheat Sheet](#099-complexity-cheat-sheet)

---

# 000. Core Mental Model

Complexity analysis is not about memorizing `O(N)`, `O(logN)`, or `O(N²)`.

It is about answering one question:

> **How many meaningful operations does my algorithm perform when input size grows?**

Think of your program as a machine.

```text
Input Size N
    |
    v
Algorithm Machine
    |
    v
Number of Operations
```

For CP/DSA:

```text
N <= 10        -> exponential/backtracking may pass
N <= 20        -> 2^N possible
N <= 10^3      -> O(N²) often okay
N <= 10^5      -> O(N log N) / O(N)
N <= 10^9      -> O(log N) / math
N <= 10^18     -> O(log N), digit/math/binary exponentiation
```

The best competitive programmers do this automatically:

```text
Read constraints
    ↓
Guess allowed complexity
    ↓
Choose pattern
    ↓
Implement
    ↓
Debug TLE/WA
```

---

# 001. Why Complexity Exists

Without complexity analysis, you write code blindly.

A solution may work for sample input:

```text
N = 5
```

But fail for real test:

```text
N = 200000
```

In CP, constraints are the hidden teacher. They tell you what algorithm class is expected.

## Example: Pair Sum

Given `N` numbers, find if any pair sums to `X`.

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

If `N = 2 * 10^5`:

```text
N² = 40,000,000,000 operations -> TLE
```

### Better O(N) Using Hash Set

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

## One Picture To Remember

```text
Wrong thinking:
"Does my code work for sample?"

Correct thinking:
"Will my code survive max constraints?"
```

---

# 002. Big-O Mental Model

Big-O describes growth rate.

It ignores:

```text
constants
small terms
machine details
```

It focuses on the dominant term.

```text
3N + 100       -> O(N)
N² + N + 5     -> O(N²)
N log N + N    -> O(N log N)
```

## Growth Table

```text
O(1)       instant
O(log N)   very fast
O(N)       good
O(N log N) good for sorting scale
O(N²)      okay for N up to around 5000
O(N³)      okay for N up to around 500
O(2^N)     okay for N around 20
O(N!)      okay for N around 10
```

## C++ Demo

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long n;
    cin >> n;

    cout << "O(1): about 1 operation\n";
    cout << "O(log2 N): about " << (long long)log2(n) << " operations\n";
    cout << "O(N): about " << n << " operations\n";
    cout << "O(N log N): about " << n * (long long)log2(n) << " operations\n";

    return 0;
}
```

## Common CP Mistake

Many beginners say:

```text
Two loops means O(N²)
```

Not always.

```cpp
for (int i = 1; i <= n; i *= 2) {
    for (int j = 0; j < n; j++) {
        // work
    }
}
```

Outer loop is `logN`, inner loop is `N`.

Total:

```text
O(N log N)
```

---

# 003. Counting Operations

## Mental Model

Count how many times the innermost meaningful statement runs.

```text
Loop count = number of visits
Complexity = total visits
```

## Simple Loop

```cpp
for (int i = 0; i < n; i++) {
    cout << i << "\n";
}
```

Runs `N` times -> `O(N)`.

## Nested Loop

```cpp
for (int i = 0; i < n; i++) {
    for (int j = 0; j < n; j++) {
        cout << i << " " << j << "\n";
    }
}
```

Runs:

```text
N * N = N²
```

## Triangular Loop

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

    cout << "Pair operations = " << operations << "\n";
    return 0;
}
```

Dry run for `N = 5`:

```text
i = 0 -> j = 1,2,3,4 -> 4
i = 1 -> j = 2,3,4   -> 3
i = 2 -> j = 3,4     -> 2
i = 3 -> j = 4       -> 1
i = 4 -> none        -> 0

Total = 10 = 5*4/2 = O(N²)
```

---

# 004. O(1) Constant Time

O(1) means operation count does not grow with `N`.

Examples:

```text
array access
simple arithmetic
formula
hash lookup average case
```

## C++ Example

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> a = {10, 20, 30, 40};

    cout << a[2] << "\n"; // O(1)

    int x = 100;
    int y = 50;
    cout << x + y << "\n"; // O(1)

    return 0;
}
```

## CP Example: Count Multiples In [L, R]

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long L, R, k;
    cin >> L >> R >> k;

    long long ans = R / k - (L - 1) / k;

    cout << ans << "\n";
    return 0;
}
```

Complexity:

```text
O(1)
```

This formula replaces a loop over a huge range.

---

# 005. O(N) Linear Time

Each element is touched a constant number of times.

```text
N elements
1 visit each
=> O(N)
```

## Sum Array

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<long long> a(n);
    long long sum = 0;

    for (int i = 0; i < n; i++) {
        cin >> a[i];
        sum += a[i];
    }

    cout << sum << "\n";
    return 0;
}
```

## CP Pattern Forms

```text
frequency count
prefix sum build
max/min scan
Kadane
single pass greedy
hash lookup
```

## Maximum Element

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

    cout << mx << "\n";
    return 0;
}
```

Dry run:

```text
Array: [4, 1, 9, 2]
mx = 4
i=1: max(4,1)=4
i=2: max(4,9)=9
i=3: max(9,2)=9
```

---

# 006. O(log N) Logarithmic Time

Every step removes a large fraction of the search space.

```text
N -> N/2 -> N/4 -> N/8 -> ... -> 1
```

Number of steps:

```text
log2(N)
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
            cout << "FOUND\n";
            return 0;
        } else if (a[mid] < target) {
            l = mid + 1;
        } else {
            r = mid - 1;
        }
    }

    cout << "NOT FOUND\n";
    return 0;
}
```

Dry run:

```text
a = [1,3,5,7,9,11,13]
target = 11

l=0 r=6 mid=3 a[mid]=7  -> go right
l=4 r=6 mid=5 a[mid]=11 -> found
```

Recognition:

```text
sorted array
monotonic predicate
answer space
find first true / last false
powers / exponentiation
```

---

# 007. O(N log N) Sorting / Divide & Conquer

`N log N` often means sorting or divide-and-conquer.

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
    cout << "\n";

    return 0;
}
```

Complexity:

```text
sort = O(N log N)
printing = O(N)
Total = O(N log N)
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
            cout << "YES\n";
            return 0;
        } else if (sum < x) {
            l++;
        } else {
            r--;
        }
    }

    cout << "NO\n";
    return 0;
}
```

Total:

```text
sort O(N log N)
two pointers O(N)
=> O(N log N)
```

---

# 008. O(N²) Nested Loops

Two independent dimensions of size `N`.

```text
for each i
    for each j
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

## Better O(N) Using Frequency

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

When O(N²) is acceptable:

```text
N <= 2000 usually okay
N <= 5000 sometimes okay in C++ if simple operations
N = 1e5 never okay
```

---

# 009. O(N³) Triple Loops

Often appears in all subarrays with inner sum, Floyd-Warshall, interval DP, and three-sum brute force.

## Bad Subarray Sum O(N³)

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

    vector<long long> a(n), pref(n + 1, 0);
    for (int i = 0; i < n; i++) {
        cin >> a[i];
        pref[i + 1] = pref[i] + a[i];
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

    long long best = LLONG_MIN;
    long long cur = 0;

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

---

# 010. O(2^N) Subsets / Backtracking

Every element has two choices:

```text
take
not take
```

Total subsets:

```text
2^N
```

## Generate Subsets Recursively

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> a;
vector<int> current;

void dfs(int idx) {
    if (idx == (int)a.size()) {
        for (int x : current) cout << x << " ";
        cout << "\n";
        return;
    }

    dfs(idx + 1); // skip

    current.push_back(a[idx]);
    dfs(idx + 1); // take
    current.pop_back();
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

Recursion tree:

```text
idx=0
├── skip
│   ├── skip
│   └── take
└── take
    ├── skip
    └── take
```

## Bitmask Version

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

Use when:

```text
N <= 20
subset enumeration
meet in the middle
bitmask DP
```

---

# 011. O(N!) Permutations

For permutation:

```text
first position: N choices
second: N-1 choices
third: N-2 choices
...
total = N!
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
        cout << "\n";
    } while (next_permutation(a.begin(), a.end()));

    return 0;
}
```

Acceptable limits:

```text
N <= 8  usually safe
N <= 10 maybe okay if simple
N > 11 usually impossible
```

---

# 012. Space Complexity

Space complexity asks:

> How much extra memory does the algorithm need?

## O(1) Extra Space

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    long long sum = 0;

    for (int i = 0; i < n; i++) {
        int x;
        cin >> x;
        sum += x;
    }

    cout << sum << "\n";
    return 0;
}
```

Extra space is constant.

## O(N) Space

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<int> a(n);
    for (int &x : a) cin >> x;

    reverse(a.begin(), a.end());

    for (int x : a) cout << x << " ";
    return 0;
}
```

Memory estimate:

```text
int       4 bytes
long long 8 bytes
double    8 bytes
char      1 byte

vector<int> of 1e7       ~40 MB
vector<long long> of 1e7 ~80 MB
```

---

# 013. Recursion Stack Complexity

Every recursive call stays on the call stack until it returns.

```text
depth = number of active calls
space = O(depth)
```

## Recursive Factorial

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

Time:

```text
O(N)
```

Stack:

```text
O(N)
```

## Iterative DFS To Avoid Stack Overflow

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

# 014. Master Theorem

Master theorem analyzes divide-and-conquer recurrences:

```text
T(N) = aT(N/b) + f(N)
```

Meaning:

```text
a = number of subproblems
N/b = size of each subproblem
f(N) = work outside recursion
```

## Merge Sort

```text
T(N) = 2T(N/2) + O(N)
Result = O(N log N)
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

Recognition:

```text
binary search recursion -> T(N)=T(N/2)+O(1)=O(logN)
merge sort -> T(N)=2T(N/2)+O(N)=O(NlogN)
divide into 2 and combine O(1) -> O(N)
```

---

# 015. Amortized Analysis

Some operations are occasionally expensive, but average over many operations is cheap.

Example:

```text
vector push_back
```

Most pushes are O(1). Sometimes vector resizes and copies all elements. Over many pushes:

```text
amortized O(1)
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

## Monotonic Stack Amortized O(N)

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

Even though there is a nested `while`, each element is pushed once and popped once.

Total:

```text
O(N)
```

---

# 016. Two Pointers Complexity

Two pointers look like nested logic, but each pointer moves in one direction.

```text
l moves at most N times
r moves at most N times
Total O(N)
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
sort O(NlogN)
two pointers O(N)
total O(NlogN)
```

Recognition:

```text
array is sorted
need pair/triplet condition
one pointer increase changes answer predictably
opposite movement is valid
```

---

# 017. Sliding Window Complexity

Sliding window is two pointers over a contiguous segment.

```text
expand right
shrink left
```

Each index enters once and leaves once.

Total:

```text
O(N)
```

## Longest Subarray With Sum <= K Positive Numbers

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

Warning:

```text
This works for positive numbers.
If negative numbers exist, monotonic behavior breaks.
```

---

# 018. Binary Search Complexity

Binary search is not only for arrays.

It is for any monotonic yes/no function.

```text
false false false true true true
```

Find first true.

## Binary Search On Answer

Minimum capacity to ship all weights in `D` days.

```cpp
#include <bits/stdc++.h>
using namespace std;

bool canShip(const vector<int>& w, int days, long long cap) {
    int usedDays = 1;
    long long cur = 0;

    for (int x : w) {
        if (x > cap) return false;

        if (cur + x <= cap) cur += x;
        else {
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

    long long l = mx, r = sum, ans = sum;

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
binary search range = O(log SUM)
total = O(N log SUM)
```

CM recognition:

```text
minimize maximum
maximize minimum
can(x) is monotonic
constraints too large for direct search
```

---

# 019. Graph Complexity

Graph complexity depends on:

```text
V = vertices/nodes
E = edges
```

BFS / DFS:

```text
O(V + E)
```

Because every node is visited once and every edge is checked.

## BFS

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

---

# 020. DP Complexity

DP complexity is usually:

```text
number of states * transitions per state
```

## 1D DP: Fibonacci

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

Complexity:

```text
states = N
transition = O(1)
total = O(N)
```

## 2D DP: Grid Paths

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

Complexity:

```text
states = N*M
transition = 2
total = O(N*M)
```

CM-level DP rule:

```text
What uniquely defines a subproblem?
How many values can each dimension take?
How many transitions from one state?
```

---

# 021. STL Complexity

## Must Know Table

```text
vector push_back          amortized O(1)
vector insert middle      O(N)
vector erase middle       O(N)
sort                      O(N log N)
lower_bound vector        O(log N)
set insert/find/erase     O(log N)
map insert/find/erase     O(log N)
unordered_map average     O(1)
priority_queue push/pop   O(log N)
queue push/pop            O(1)
stack push/pop            O(1)
deque push_front/back     O(1)
```

## STL Demo

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> v = {5, 1, 9, 3};

    sort(v.begin(), v.end()); // O(NlogN)

    cout << binary_search(v.begin(), v.end(), 3) << "\n"; // O(logN)

    set<int> s;
    s.insert(10); // O(logN)
    s.insert(5);

    unordered_map<int, int> freq;
    freq[10]++; // average O(1)

    priority_queue<int> pq;
    pq.push(7); // O(logN)
    pq.push(2);
    pq.push(9);

    cout << pq.top() << "\n"; // O(1)
    pq.pop();                 // O(logN)

    return 0;
}
```

Common trap:

```cpp
for (int i = 0; i < n; i++) {
    v.erase(v.begin());
}
```

Each erase is O(N). Total O(N²). Use queue/deque instead.

---

# 022. Complexity From Constraints

Before solving, translate constraints.

```text
N <= 10          O(N!), O(2^N)
N <= 20          O(2^N), O(N*2^N)
N <= 100         O(N³), O(N⁴ maybe)
N <= 500         O(N³)
N <= 2000        O(N²)
N <= 1e5         O(NlogN), O(N)
N <= 1e6         O(N)
N <= 1e9         O(logN), O(1)
N <= 1e18        O(logN), math
```

## Constraint Mental Check Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long n;
    cin >> n;

    if (n <= 10) cout << "Backtracking/permutation may work\n";
    else if (n <= 20) cout << "Bitmask/subsets may work\n";
    else if (n <= 500) cout << "O(N^3) may work\n";
    else if (n <= 5000) cout << "O(N^2) maybe\n";
    else if (n <= 200000) cout << "Need O(N log N) or O(N)\n";
    else cout << "Need O(log N), O(1), or math\n";

    return 0;
}
```

---

# 023. TLE Debugging

TLE means:

```text
operations too many
or constant factor too high
or IO too slow
or hidden infinite loop
```

## TLE Checklist

```text
1. Read max constraints again.
2. Estimate operations.
3. Check nested loops.
4. Check STL operations inside loops.
5. Check recursion branching.
6. Check map vs unordered_map.
7. Check erase from vector.
8. Check repeated sorting.
9. Check missing fast IO.
10. Check infinite loops.
```

## Bad: Repeated Sorting

```cpp
for (int i = 0; i < n; i++) {
    sort(a.begin(), a.end());
}
```

Complexity:

```text
O(N * NlogN)
```

## Better

```cpp
sort(a.begin(), a.end());

for (int i = 0; i < n; i++) {
    // use sorted array
}
```

## Binary Search Infinite Loop Bug

Bad:

```cpp
while (l < r) {
    int mid = (l + r) / 2;
    if (condition(mid)) r = mid;
    else l = mid; // may not move
}
```

Better:

```cpp
while (l < r) {
    int mid = l + (r - l) / 2;
    if (condition(mid)) r = mid;
    else l = mid + 1;
}
```

---

# 024. Optimization Playbook

## From Brute To Optimal

```text
O(N³) -> prefix sum -> O(N²)
O(N²) -> hash/frequency -> O(N)
O(N²) -> sorting + two pointers -> O(NlogN)
O(N) per query -> prefix -> O(1) per query
linear search -> binary search -> O(logN)
recursion exponential -> memoization DP
```

## Pattern Table

```text
Repeated range sum        -> Prefix Sum
All pairs                 -> Hashing / Sorting / Two Pointers
Sorted monotonic answer   -> Binary Search
Repeated min/max window   -> Monotonic Queue
Next greater/smaller      -> Monotonic Stack
Graph unweighted shortest -> BFS
Graph weighted shortest   -> Dijkstra
Overlapping recursion     -> DP
Frequent updates/query    -> Fenwick / Segment Tree
```

## Range Sum Queries

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
        for (int i = l; i <= r; i++) sum += a[i];

        cout << sum << "\n";
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

        cout << pref[r + 1] - pref[l] << "\n";
    }

    return 0;
}
```

---

# 099. Complexity Cheat Sheet

## Big-O Quick Memory

```text
O(1)        formula / direct access
O(logN)     divide by 2 / binary search
O(N)        one pass
O(NlogN)    sorting / divide conquer
O(N²)       pairs / double loop
O(N³)       triples / Floyd / interval DP
O(2^N)      subsets
O(N!)       permutations
```

## Constraint To Algorithm

```text
N <= 10      permutations
N <= 20      subsets / bitmask
N <= 100     cubic maybe
N <= 500     cubic
N <= 2000    quadratic
N <= 1e5     linear / NlogN
N <= 1e9     log / math
N <= 1e18    log / math
```

## CP Final Complexity Checklist

```text
[ ] Did I read max N?
[ ] Did I estimate worst-case operations?
[ ] Any O(N²) with N=1e5?
[ ] Any vector erase inside loop?
[ ] Any repeated sorting?
[ ] Any map where unordered_map is enough?
[ ] Any recursion depth > 1e5?
[ ] Any hidden O(N) STL call inside loop?
[ ] Any infinite loop in binary search?
[ ] Did I use fast IO?
```

## Final CP Template With Complexity Habit

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

void solve() {
    /*
        Always think:
        1. What is N?
        2. What complexity is allowed?
        3. Is this O(N), O(NlogN), O(N^2), or worse?
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

## One Picture To Remember

```text
CONSTRAINTS
    ↓
ALLOWED COMPLEXITY
    ↓
PATTERN
    ↓
CODE
    ↓
AC
```

Never start from code.

Start from constraints.
