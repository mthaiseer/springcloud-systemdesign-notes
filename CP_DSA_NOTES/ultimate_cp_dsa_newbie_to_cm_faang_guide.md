# Ultimate CP + DSA Guide: Newbie → Candidate Master + FAANG Interview

> Pattern-wise roadmap with C++ templates, intuition, when to use each pattern, and difficulty-wise problem lists.

## Clickable Index

1. [How to Use This Guide](#1-how-to-use-this-guide)
2. [Level Roadmap: Newbie → CM + Interview Ready](#2-level-roadmap-newbie--cm--interview-ready)
3. [Master Pattern Recognition Table](#3-master-pattern-recognition-table)
4. [C++ Base Template](#4-c-base-template)
5. [STL + Implementation Patterns](#5-stl--implementation-patterns)
6. [Prefix Sum + Difference Array](#6-prefix-sum--difference-array)
7. [Two Pointers + Sliding Window](#7-two-pointers--sliding-window)
8. [Binary Search](#8-binary-search)
9. [Sorting + Greedy + Intervals](#9-sorting--greedy--intervals)
10. [Stack + Monotonic Stack + Queue + Deque](#10-stack--monotonic-stack--queue--deque)
11. [Bit Manipulation + Bitmasking](#11-bit-manipulation--bitmasking)
12. [Recursion + Backtracking](#12-recursion--backtracking)
13. [Number Theory](#13-number-theory)
14. [Modular Arithmetic](#14-modular-arithmetic)
15. [Combinatorics](#15-combinatorics)
16. [Graph Algorithms](#16-graph-algorithms)
17. [Tree + LCA + DSU](#17-tree--lca--dsu)
18. [Dynamic Programming](#18-dynamic-programming)
19. [Advanced CP Topics for CM](#19-advanced-cp-topics-for-cm)
20. [FAANG Interview Track](#20-faang-interview-track)
21. [Difficulty-Wise Practice Tables](#21-difficulty-wise-practice-tables)
22. [Final Revision Checklist](#22-final-revision-checklist)

---

# 1. How to Use This Guide

For every new problem:

```text
1. Read statement.
2. Extract n, q, constraints.
3. Write brute force.
4. Find bottleneck.
5. Match problem clue to pattern.
6. Pick template.
7. Dry run.
8. Code.
9. Test edge cases.
```

## Core mental model

```text
Problem clue -> Pattern -> State/invariant -> Template -> Edge cases
```

---

# 2. Level Roadmap: Newbie → CM + Interview Ready

| Stage | Rating / Goal | What to Master | Target |
|---|---:|---|---|
| Stage 0 | Newbie | C++, STL, loops, arrays, strings | Solve easy implementation |
| Stage 1 | 800-1000 | Prefix, sorting, maps, sets, two pointers | Solve A/B |
| Stage 2 | 1000-1300 | Binary search, greedy, stack, basic graph | Solve B/C |
| Stage 3 | 1300-1600 | DP basics, DSU, shortest path, math basics | Stable C/D |
| Stage 4 | 1600-1900 | Tree DP, bitmask, combinatorics, advanced BS | Expert path |
| Stage 5 | 1900-2200 | Segment tree, lazy, SCC, flows basics, harder DP | CM path |
| Stage 6 | FAANG | Clean communication, arrays, strings, trees, graphs, DP | Interview ready |

---

# 3. Master Pattern Recognition Table

| Problem clue | Think |
|---|---|
| Range sum query, static array | Prefix sum |
| Many range add updates, final array | Difference array |
| Subarray sum equals K | Prefix sum + hash map |
| Subarray sum divisible by K | Prefix modulo frequency |
| Fixed size subarray | Sliding window |
| Longest/shortest valid contiguous segment | Variable sliding window |
| Sorted pair/triplet | Two pointers |
| Minimum possible maximum / maximum possible minimum | Binary search on answer |
| `false false true true` condition | First true binary search |
| Next greater/smaller | Monotonic stack |
| Window min/max | Monotonic deque |
| Top K / current best | Heap / priority_queue |
| Dynamic ordered elements | set / multiset |
| Connectivity | DSU / DFS / BFS |
| Shortest path unweighted | BFS |
| Shortest path weighted nonnegative | Dijkstra |
| Dependency ordering | Topological sort |
| Tree path query | LCA / binary lifting |
| Count ways / max/min over choices | DP |
| Generate all valid answers | Backtracking |
| Subset over small n | Bitmask |
| Mod prime division | Fermat inverse |
| Count combinations | nCr precomputation |
| Multiples/divisors/gcd | Number theory |

---

# 4. C++ Base Template

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;
const long long INF = 4e18;
const int MOD = 1'000'000'007;

void solve() {
    // code here
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int T = 1;
    // cin >> T;
    while (T--) solve();

    return 0;
}
```

---

# 5. STL + Implementation Patterns

## Intuition

STL is your toolbox. Choose container by operations, not by habit.

| Need | Use |
|---|---|
| Dynamic array | `vector` |
| String processing | `string` |
| Key-frequency | `unordered_map` / `map` |
| Sorted unique | `set` |
| Sorted duplicates + erase one | `multiset` |
| LIFO | `stack` |
| FIFO | `queue` |
| Best max/min | `priority_queue` |
| Window max/min | `deque` |
| Sort/search | `sort`, `lower_bound`, `upper_bound` |

## Pattern: frequency map

```cpp
unordered_map<int,int> freq;
for (int x : a) freq[x]++;

for (auto [value, count] : freq) {
    // use value and count
}
```

## Pattern: erase one copy from multiset

```cpp
auto it = ms.find(x);
if (it != ms.end()) ms.erase(it);
```

## Related practice

| Difficulty | Problems |
|---|---|
| Easy | Two Sum, Valid Anagram, Contains Duplicate |
| Medium | Group Anagrams, Top K Frequent Elements, Merge Intervals |
| Hard | Median from Data Stream, LRU Cache, Sliding Window Median |

---

# 6. Prefix Sum + Difference Array

## When to use

Use prefix sum when queries ask repeated range information on static data.

```text
sum(l, r) = pref[r + 1] - pref[l]
```

## 1D prefix sum template

```cpp
vector<long long> buildPrefix(const vector<int>& a) {
    int n = a.size();
    vector<long long> pref(n + 1, 0);

    for (int i = 0; i < n; i++) {
        pref[i + 1] = pref[i] + a[i];
    }

    return pref;
}

long long rangeSum(const vector<long long>& pref, int l, int r) {
    return pref[r + 1] - pref[l];
}
```

## Difference array template

Use when many range updates happen, then final array is needed.

```cpp
vector<long long> rangeAddFinal(int n, vector<array<int,3>>& updates) {
    vector<long long> diff(n + 1, 0);

    for (auto [l, r, x] : updates) {
        diff[l] += x;
        if (r + 1 < n) diff[r + 1] -= x;
    }

    vector<long long> a(n);
    long long cur = 0;
    for (int i = 0; i < n; i++) {
        cur += diff[i];
        a[i] = cur;
    }

    return a;
}
```

## Prefix + hash map: subarray sum equals K

```cpp
long long countSubarraysSumK(vector<int>& a, long long k) {
    unordered_map<long long,long long> cnt;
    cnt[0] = 1;

    long long pref = 0, ans = 0;

    for (int x : a) {
        pref += x;
        ans += cnt[pref - k];
        cnt[pref]++;
    }

    return ans;
}
```

## Problems

| Difficulty | Problems |
|---|---|
| Easy | Running Sum, Range Sum Query Immutable |
| Medium | Subarray Sum Equals K, Product Except Self, Range Addition |
| Hard | Count Subarrays With Median K, Maximum Subarray Min-Product |

---

# 7. Two Pointers + Sliding Window

## Pattern A: opposite ends

Use when sorted array allows safe discard.

```cpp
bool twoSumSorted(vector<int>& a, int target) {
    int l = 0, r = (int)a.size() - 1;

    while (l < r) {
        long long sum = 1LL * a[l] + a[r];

        if (sum == target) return true;
        if (sum < target) l++;
        else r--;
    }

    return false;
}
```

## Pattern B: fixed window

```cpp
long long maxSumSizeK(vector<int>& a, int k) {
    long long sum = 0, best = LLONG_MIN;

    for (int r = 0; r < (int)a.size(); r++) {
        sum += a[r];

        if (r >= k) sum -= a[r - k];

        if (r >= k - 1) best = max(best, sum);
    }

    return best;
}
```

## Pattern C: variable window

```cpp
int longestAtMostKDistinct(vector<int>& a, int k) {
    unordered_map<int,int> freq;
    int l = 0, best = 0;

    for (int r = 0; r < (int)a.size(); r++) {
        freq[a[r]]++;

        while ((int)freq.size() > k) {
            freq[a[l]]--;
            if (freq[a[l]] == 0) freq.erase(a[l]);
            l++;
        }

        best = max(best, r - l + 1);
    }

    return best;
}
```

## Problems

| Difficulty | Problems |
|---|---|
| Easy | Valid Palindrome, Move Zeroes, Merge Sorted Array |
| Medium | 3Sum, Container With Most Water, Longest Substring Without Repeating |
| Hard | Minimum Window Substring, Sliding Window Median |

---

# 8. Binary Search

## Intuition

Binary search works when the answer space is monotonic.

```text
false false false true true true
```

## First true template

```cpp
bool check(long long x) {
    // return true if x works
    return true;
}

long long firstTrue(long long lo, long long hi) {
    long long ans = hi + 1;

    while (lo <= hi) {
        long long mid = lo + (hi - lo) / 2;

        if (check(mid)) {
            ans = mid;
            hi = mid - 1;
        } else {
            lo = mid + 1;
        }
    }

    return ans;
}
```

## Last true template

```cpp
long long lastTrue(long long lo, long long hi) {
    long long ans = lo - 1;

    while (lo <= hi) {
        long long mid = lo + (hi - lo) / 2;

        if (check(mid)) {
            ans = mid;
            lo = mid + 1;
        } else {
            hi = mid - 1;
        }
    }

    return ans;
}
```

## Problems

| Difficulty | Problems |
|---|---|
| Easy | Binary Search, First Bad Version |
| Medium | Search Rotated Sorted Array, Koko Eating Bananas, Capacity To Ship Packages |
| Hard | Median of Two Sorted Arrays, Split Array Largest Sum, Aggressive Cows |

---

# 9. Sorting + Greedy + Intervals

## Greedy intuition

Greedy works when a local best choice can be proven safe.

## Interval merge

```cpp
vector<vector<int>> mergeIntervals(vector<vector<int>>& intervals) {
    sort(intervals.begin(), intervals.end());

    vector<vector<int>> ans;

    for (auto cur : intervals) {
        if (ans.empty() || ans.back()[1] < cur[0]) {
            ans.push_back(cur);
        } else {
            ans.back()[1] = max(ans.back()[1], cur[1]);
        }
    }

    return ans;
}
```

## Activity selection

Sort by ending time.

```cpp
int maxNonOverlapping(vector<pair<int,int>>& seg) {
    sort(seg.begin(), seg.end(), [](auto a, auto b) {
        return a.second < b.second;
    });

    int ans = 0;
    int lastEnd = INT_MIN;

    for (auto [l, r] : seg) {
        if (l >= lastEnd) {
            ans++;
            lastEnd = r;
        }
    }

    return ans;
}
```

## Problems

| Difficulty | Problems |
|---|---|
| Easy | Assign Cookies, Can Place Flowers |
| Medium | Merge Intervals, Non-overlapping Intervals, Gas Station |
| Hard | Minimum Number of Arrows, Candy, Course Schedule III |

---

# 10. Stack + Monotonic Stack + Queue + Deque

## Valid parentheses

```cpp
bool isValid(string s) {
    stack<char> st;

    for (char c : s) {
        if (c == '(' || c == '[' || c == '{') {
            st.push(c);
        } else {
            if (st.empty()) return false;
            char o = st.top();
            st.pop();

            if (c == ')' && o != '(') return false;
            if (c == ']' && o != '[') return false;
            if (c == '}' && o != '{') return false;
        }
    }

    return st.empty();
}
```

## Next greater element

```cpp
vector<int> nextGreater(vector<int>& a) {
    int n = a.size();
    vector<int> ans(n, -1);
    stack<int> st; // indices

    for (int i = n - 1; i >= 0; i--) {
        while (!st.empty() && a[st.top()] <= a[i]) st.pop();

        if (!st.empty()) ans[i] = a[st.top()];

        st.push(i);
    }

    return ans;
}
```

## Sliding window maximum

```cpp
vector<int> maxSlidingWindow(vector<int>& a, int k) {
    deque<int> dq;
    vector<int> ans;

    for (int i = 0; i < (int)a.size(); i++) {
        while (!dq.empty() && dq.front() <= i - k) dq.pop_front();

        while (!dq.empty() && a[dq.back()] <= a[i]) dq.pop_back();

        dq.push_back(i);

        if (i >= k - 1) ans.push_back(a[dq.front()]);
    }

    return ans;
}
```

## Problems

| Difficulty | Problems |
|---|---|
| Easy | Valid Parentheses, Min Stack |
| Medium | Daily Temperatures, Next Greater Element II, Asteroid Collision |
| Hard | Largest Rectangle in Histogram, Sliding Window Maximum |

---

# 11. Bit Manipulation + Bitmasking

## Core operations

| Operation | Code |
|---|---|
| Check bit i | `(x >> i) & 1` |
| Set bit i | `x | (1LL << i)` |
| Clear bit i | `x & ~(1LL << i)` |
| Toggle bit i | `x ^ (1LL << i)` |
| Lowbit | `x & -x` |

## Generate subsets

```cpp
vector<vector<int>> subsets(vector<int>& a) {
    int n = a.size();
    vector<vector<int>> ans;

    for (int mask = 0; mask < (1 << n); mask++) {
        vector<int> cur;

        for (int i = 0; i < n; i++) {
            if ((mask >> i) & 1) cur.push_back(a[i]);
        }

        ans.push_back(cur);
    }

    return ans;
}
```

## Count pair XOR contribution

For each bit, count zeros and ones.

```cpp
long long sumPairXor(vector<int>& a) {
    long long ans = 0;
    int n = a.size();

    for (int b = 0; b < 31; b++) {
        long long ones = 0;

        for (int x : a) {
            if ((x >> b) & 1) ones++;
        }

        long long zeros = n - ones;
        ans += ones * zeros * (1LL << b);
    }

    return ans;
}
```

## Problems

| Difficulty | Problems |
|---|---|
| Easy | Single Number, Power of Two, Counting Bits |
| Medium | Subsets, Bitwise AND Range, Maximum XOR for Each Query |
| Hard | Maximum XOR Pair, Minimum XOR Sum, TSP Bitmask DP |

---

# 12. Recursion + Backtracking

## LCCM framework

```text
L = Level: where am I?
C = Choice: what options can I try?
C = Check: is the choice valid?
M = Move: apply -> recurse -> undo
```

## Universal backtracking template

```cpp
void dfs(int level) {
    if (base_case) {
        save_answer();
        return;
    }

    for (auto choice : choices) {
        if (!valid(choice)) continue;

        apply(choice);
        dfs(level + 1);
        undo(choice);
    }
}
```

## Permutations

```cpp
void permuteDfs(vector<int>& a, vector<int>& path, vector<int>& used,
                vector<vector<int>>& ans) {
    if ((int)path.size() == (int)a.size()) {
        ans.push_back(path);
        return;
    }

    for (int i = 0; i < (int)a.size(); i++) {
        if (used[i]) continue;

        used[i] = 1;
        path.push_back(a[i]);

        permuteDfs(a, path, used, ans);

        path.pop_back();
        used[i] = 0;
    }
}
```

## Problems

| Difficulty | Problems |
|---|---|
| Easy | Generate Parentheses basic, Subsets |
| Medium | Permutations, Combination Sum, Word Search |
| Hard | N-Queens, Sudoku Solver, Palindrome Partitioning II |

---

# 13. Number Theory

## 13.1 GCD / LCM

Use when problem has divisibility, repeated reduction, or common factors.

```cpp
long long gcdll(long long a, long long b) {
    while (b) {
        long long t = a % b;
        a = b;
        b = t;
    }
    return a;
}

long long lcmll(long long a, long long b) {
    return a / gcdll(a, b) * b;
}
```

## 13.2 Prime check

```cpp
bool isPrime(long long n) {
    if (n < 2) return false;

    for (long long d = 2; d * d <= n; d++) {
        if (n % d == 0) return false;
    }

    return true;
}
```

## 13.3 Sieve of Eratosthenes

```cpp
vector<int> sieve(int n) {
    vector<int> isPrime(n + 1, true);
    vector<int> primes;

    if (n >= 0) isPrime[0] = false;
    if (n >= 1) isPrime[1] = false;

    for (int i = 2; i <= n; i++) {
        if (isPrime[i]) {
            primes.push_back(i);
            if (1LL * i * i <= n) {
                for (long long j = 1LL * i * i; j <= n; j += i) {
                    isPrime[j] = false;
                }
            }
        }
    }

    return primes;
}
```

## 13.4 Prime factorization

```cpp
vector<pair<long long,int>> factorize(long long n) {
    vector<pair<long long,int>> f;

    for (long long p = 2; p * p <= n; p++) {
        if (n % p == 0) {
            int cnt = 0;
            while (n % p == 0) {
                n /= p;
                cnt++;
            }
            f.push_back({p, cnt});
        }
    }

    if (n > 1) f.push_back({n, 1});

    return f;
}
```

## 13.5 Divisor generation

```cpp
vector<long long> divisors(long long n) {
    vector<long long> d;

    for (long long x = 1; x * x <= n; x++) {
        if (n % x == 0) {
            d.push_back(x);
            if (x != n / x) d.push_back(n / x);
        }
    }

    sort(d.begin(), d.end());
    return d;
}
```

## 13.6 Extended Euclid

Use for modular inverse when mod is not prime and gcd(a, m) = 1.

```cpp
long long extgcd(long long a, long long b, long long& x, long long& y) {
    if (b == 0) {
        x = 1;
        y = 0;
        return a;
    }

    long long x1, y1;
    long long g = extgcd(b, a % b, x1, y1);

    x = y1;
    y = x1 - y1 * (a / b);

    return g;
}
```

## Problems

| Difficulty | Problems |
|---|---|
| Easy | GCD of Array, Count Divisors, Prime Number |
| Medium | Sieve Problems, Number of Divisors, Coprime Pairs |
| Hard | Chinese Remainder, Mobius/Inclusion, Diophantine Equations |

---

# 14. Modular Arithmetic

## Rules

```text
(a + b) mod M = ((a mod M) + (b mod M)) mod M
(a - b) mod M = ((a mod M) - (b mod M) + M) mod M
(a * b) mod M = ((a mod M) * (b mod M)) mod M
```

## Fast power

```cpp
long long modPow(long long a, long long e, long long mod) {
    long long res = 1 % mod;
    a %= mod;

    while (e > 0) {
        if (e & 1) res = (__int128)res * a % mod;
        a = (__int128)a * a % mod;
        e >>= 1;
    }

    return res;
}
```

## Modular inverse under prime mod

```cpp
long long modInversePrime(long long a, long long mod) {
    return modPow(a, mod - 2, mod);
}
```

## Safe modular division

```cpp
long long modDiv(long long a, long long b, long long mod) {
    return (a % mod) * modInversePrime(b, mod) % mod;
}
```

## Problems

| Difficulty | Problems |
|---|---|
| Easy | Big exponent mod, Modular sum |
| Medium | Count paths modulo MOD, nCr modulo prime |
| Hard | Modular inverse with non-prime mod, CRT, Lucas theorem |

---

# 15. Combinatorics

## 15.1 Counting basics

| Pattern | Formula |
|---|---|
| Choose k from n | `C(n, k)` |
| Arrange k from n | `P(n, k)` |
| Subsets of n elements | `2^n` |
| Permutations of n | `n!` |
| Stars and bars | `C(n + k - 1, k - 1)` |

## 15.2 nCr with factorials modulo prime

```cpp
const int MODC = 1'000'000'007;
const int MAXN = 1'000'000;

long long fact[MAXN + 1], invFact[MAXN + 1];

long long powerMod(long long a, long long e) {
    long long res = 1;
    while (e) {
        if (e & 1) res = res * a % MODC;
        a = a * a % MODC;
        e >>= 1;
    }
    return res;
}

void initComb() {
    fact[0] = 1;
    for (int i = 1; i <= MAXN; i++) fact[i] = fact[i - 1] * i % MODC;

    invFact[MAXN] = powerMod(fact[MAXN], MODC - 2);
    for (int i = MAXN - 1; i >= 0; i--) {
        invFact[i] = invFact[i + 1] * (i + 1) % MODC;
    }
}

long long C(int n, int r) {
    if (r < 0 || r > n) return 0;
    return fact[n] * invFact[r] % MODC * invFact[n - r] % MODC;
}
```

## 15.3 Inclusion-exclusion

Use when counting objects that avoid forbidden conditions.

```text
answer = total
       - count(bad condition 1)
       - count(bad condition 2)
       + count(both bad)
```

## 15.4 Pigeonhole principle

If `n + 1` items go into `n` boxes, some box has at least 2 items.

Common CP use:

```text
If prefix sums modulo n repeat, subarray sum divisible by n exists.
```

## Problems

| Difficulty | Problems |
|---|---|
| Easy | Count paths in grid, simple nCr |
| Medium | Distribute candies, inclusion-exclusion basics |
| Hard | Derangements, Catalan numbers, Lucas theorem, Burnside basics |

---

# 16. Graph Algorithms

## Graph representation

```cpp
int n, m;
vector<vector<int>> g;

void readGraph() {
    cin >> n >> m;
    g.assign(n + 1, {});

    for (int i = 0; i < m; i++) {
        int u, v;
        cin >> u >> v;
        g[u].push_back(v);
        g[v].push_back(u);
    }
}
```

## DFS

```cpp
void dfs(int u, vector<vector<int>>& g, vector<int>& vis) {
    vis[u] = 1;

    for (int v : g[u]) {
        if (!vis[v]) dfs(v, g, vis);
    }
}
```

## BFS shortest path in unweighted graph

```cpp
vector<int> bfs(int src, vector<vector<int>>& g) {
    int n = g.size() - 1;
    vector<int> dist(n + 1, -1);
    queue<int> q;

    dist[src] = 0;
    q.push(src);

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

    return dist;
}
```

## Dijkstra

```cpp
vector<long long> dijkstra(int src, vector<vector<pair<int,int>>>& g) {
    int n = g.size() - 1;
    vector<long long> dist(n + 1, INF);
    priority_queue<pair<long long,int>, vector<pair<long long,int>>, greater<pair<long long,int>>> pq;

    dist[src] = 0;
    pq.push({0, src});

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

    return dist;
}
```

## Topological sort

```cpp
vector<int> topoSort(int n, vector<vector<int>>& g) {
    vector<int> indeg(n + 1, 0);

    for (int u = 1; u <= n; u++) {
        for (int v : g[u]) indeg[v]++;
    }

    queue<int> q;
    for (int i = 1; i <= n; i++) {
        if (indeg[i] == 0) q.push(i);
    }

    vector<int> order;

    while (!q.empty()) {
        int u = q.front();
        q.pop();
        order.push_back(u);

        for (int v : g[u]) {
            indeg[v]--;
            if (indeg[v] == 0) q.push(v);
        }
    }

    return order;
}
```

## Problems

| Difficulty | Problems |
|---|---|
| Easy | Find if Path Exists, Number of Islands |
| Medium | Course Schedule, Clone Graph, Rotting Oranges, Network Delay |
| Hard | Word Ladder, Alien Dictionary, Minimum Cost to Connect Points, Shortest Path with Obstacles |

---

# 17. Tree + LCA + DSU

## Tree DFS preprocessing

```cpp
vector<vector<int>> tree;
vector<int> parent, depth, subtree;

void dfsTree(int u, int p) {
    parent[u] = p;
    subtree[u] = 1;

    for (int v : tree[u]) {
        if (v == p) continue;

        depth[v] = depth[u] + 1;
        dfsTree(v, u);
        subtree[u] += subtree[v];
    }
}
```

## DSU

```cpp
struct DSU {
    vector<int> parent, sz;

    DSU(int n) {
        parent.resize(n + 1);
        sz.assign(n + 1, 1);
        iota(parent.begin(), parent.end(), 0);
    }

    int find(int x) {
        if (parent[x] == x) return x;
        return parent[x] = find(parent[x]);
    }

    bool unite(int a, int b) {
        a = find(a);
        b = find(b);

        if (a == b) return false;

        if (sz[a] < sz[b]) swap(a, b);

        parent[b] = a;
        sz[a] += sz[b];

        return true;
    }
};
```

## Binary lifting LCA

```cpp
const int LOG = 20;
vector<array<int, LOG>> up;
vector<int> dep;
vector<vector<int>> t;

void dfsLca(int u, int p) {
    up[u][0] = p;

    for (int j = 1; j < LOG; j++) {
        up[u][j] = up[up[u][j - 1]][j - 1];
    }

    for (int v : t[u]) {
        if (v == p) continue;
        dep[v] = dep[u] + 1;
        dfsLca(v, u);
    }
}

int lift(int u, int k) {
    for (int j = 0; j < LOG; j++) {
        if ((k >> j) & 1) u = up[u][j];
    }
    return u;
}

int lca(int a, int b) {
    if (dep[a] < dep[b]) swap(a, b);

    a = lift(a, dep[a] - dep[b]);

    if (a == b) return a;

    for (int j = LOG - 1; j >= 0; j--) {
        if (up[a][j] != up[b][j]) {
            a = up[a][j];
            b = up[b][j];
        }
    }

    return up[a][0];
}
```

## Problems

| Difficulty | Problems |
|---|---|
| Easy | Maximum Depth, Same Tree, Flood Fill |
| Medium | Lowest Common Ancestor, Validate BST, Number of Provinces |
| Hard | Tree Diameter, Binary Tree Maximum Path Sum, Accounts Merge, Kruskal MST |

---

# 18. Dynamic Programming

## DP thinking framework

```text
1. State: what does dp[...] mean?
2. Transition: how to move?
3. Base case.
4. Order of computation.
5. Answer location.
```

## 1D DP: stairs

```cpp
int climbStairs(int n) {
    vector<int> dp(n + 1, 0);
    dp[0] = 1;

    for (int i = 1; i <= n; i++) {
        dp[i] += dp[i - 1];
        if (i >= 2) dp[i] += dp[i - 2];
    }

    return dp[n];
}
```

## Knapsack

```cpp
int knapsack01(vector<int>& wt, vector<int>& val, int W) {
    vector<int> dp(W + 1, 0);

    for (int i = 0; i < (int)wt.size(); i++) {
        for (int cap = W; cap >= wt[i]; cap--) {
            dp[cap] = max(dp[cap], dp[cap - wt[i]] + val[i]);
        }
    }

    return dp[W];
}
```

## LIS O(n log n)

```cpp
int lengthOfLIS(vector<int>& a) {
    vector<int> tail;

    for (int x : a) {
        auto it = lower_bound(tail.begin(), tail.end(), x);

        if (it == tail.end()) tail.push_back(x);
        else *it = x;
    }

    return tail.size();
}
```

## Grid DP

```cpp
long long uniquePaths(int n, int m) {
    vector<vector<long long>> dp(n, vector<long long>(m, 0));
    dp[0][0] = 1;

    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            if (i > 0) dp[i][j] += dp[i - 1][j];
            if (j > 0) dp[i][j] += dp[i][j - 1];
        }
    }

    return dp[n - 1][m - 1];
}
```

## Problems

| Difficulty | Problems |
|---|---|
| Easy | Climbing Stairs, House Robber |
| Medium | Coin Change, LIS, Unique Paths, Longest Common Subsequence |
| Hard | Edit Distance, Burst Balloons, Regular Expression Matching, DP on Trees |

---

# 19. Advanced CP Topics for CM

| Topic | When to Learn | Key Patterns |
|---|---|---|
| Fenwick Tree | After prefix sums | point update/range query |
| Segment Tree | After Fenwick | range query + updates |
| Lazy Segment Tree | After segment tree | range update/range query |
| Sparse Table | Static idempotent queries | range min/max/gcd |
| Heavy-Light Decomposition | Tree path queries | path split into chains |
| SCC | Directed graph components | Kosaraju/Tarjan |
| Max Flow | Matching/cut problems | Dinic |
| String Algorithms | Pattern matching | KMP, Z-function, Trie |
| Rolling Hash | Substring comparison | polynomial hash |
| Geometry | Coordinates | orientation, convex hull |
| Advanced DP Optimization | Hard DP | divide-conquer, convex hull trick |

## Fenwick tree

```cpp
struct Fenwick {
    int n;
    vector<long long> bit;

    Fenwick(int n) : n(n), bit(n + 1, 0) {}

    void add(int idx, long long val) {
        for (++idx; idx <= n; idx += idx & -idx) {
            bit[idx] += val;
        }
    }

    long long sumPrefix(int idx) {
        long long res = 0;
        for (++idx; idx > 0; idx -= idx & -idx) {
            res += bit[idx];
        }
        return res;
    }

    long long rangeSum(int l, int r) {
        return sumPrefix(r) - (l ? sumPrefix(l - 1) : 0);
    }
};
```

---

# 20. FAANG Interview Track

## What FAANG checks

```text
1. Can you explain brute force?
2. Can you optimize from constraints?
3. Can you maintain clean invariants?
4. Can you code bug-free?
5. Can you test edge cases?
```

## Must-master topics

| Topic | Interview priority |
|---|---|
| Arrays + strings | Very high |
| Hash map/set | Very high |
| Two pointers/sliding window | Very high |
| Binary search | High |
| Stack/queue | High |
| Trees | Very high |
| Graph BFS/DFS | High |
| Heap | High |
| DP basics | High |
| Backtracking | Medium-high |
| Trie | Medium |
| Union Find | Medium |

## Communication template

```text
Brute force:
    I can try all possibilities...

Bottleneck:
    This is O(n^2), but n is large.

Optimization:
    I notice we only need to maintain...

Invariant:
    At every step, my window contains...

Complexity:
    Time O(...), space O(...).

Edge cases:
    empty input, single element, duplicates, negatives, overflow
```

---

# 21. Difficulty-Wise Practice Tables

## Newbie / Easy

| Topic | Problems |
|---|---|
| STL | Reverse Array, Frequency Count, Valid Anagram |
| Prefix | Running Sum, Range Sum Query |
| Two Pointers | Valid Palindrome, Merge Sorted Array |
| Binary Search | Search Insert Position |
| Stack | Valid Parentheses |
| Graph | Flood Fill |
| DP | Climbing Stairs |
| Math | GCD, Prime Check |

## Medium

| Topic | Problems |
|---|---|
| Prefix | Subarray Sum Equals K |
| Sliding Window | Longest Substring Without Repeating |
| Binary Search | Koko Eating Bananas |
| Greedy | Merge Intervals, Gas Station |
| Monotonic Stack | Daily Temperatures |
| Bitmask | Subsets |
| Graph | Course Schedule, Rotting Oranges |
| Tree | LCA, Validate BST |
| DP | Coin Change, LIS, LCS |
| Number Theory | Sieve, Factorization |
| Combinatorics | nCr modulo prime |

## Hard / CM-building

| Topic | Problems |
|---|---|
| Prefix + Data Structures | Count Range Sum |
| Binary Search | Split Array Largest Sum |
| Monotonic Stack | Largest Rectangle in Histogram |
| Graph | Word Ladder, SCC, 0-1 BFS |
| Tree | Tree Diameter, LCA path queries |
| DP | Edit Distance, Digit DP, Tree DP |
| Bitmask DP | TSP, Assignment DP |
| Math | CRT, Mobius basics, Lucas theorem |
| Segment Tree | Range update/range query |
| Strings | KMP, Z-function, Rolling Hash |

---

# 22. Final Revision Checklist

Before solving:

```text
[ ] Did I read constraints?
[ ] Did I write brute force idea?
[ ] Is data static or dynamic?
[ ] Is order important?
[ ] Is there a range/window/subarray?
[ ] Is there a monotonic answer?
[ ] Is graph implicit?
[ ] Can math simplify it?
[ ] What is the invariant?
[ ] What are edge cases?
```

Before submitting:

```text
[ ] Empty/single input
[ ] Duplicates
[ ] Negative values
[ ] Modulo negative fix
[ ] Long long overflow
[ ] 0-index vs 1-index
[ ] Graph directed vs undirected
[ ] Multiple test cases reset
[ ] Recursion depth
[ ] Binary search infinite loop
```

---

## The Core Rule

```text
Do not memorize problems.
Memorize patterns, templates, invariants, and failure cases.
```
