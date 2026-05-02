# Ultimate CP + FAANG Interview Guide: Newbie to Candidate Master

> Pattern-wise C++ guide for competitive programming, Codeforces Candidate Master preparation, and FAANG-style interviews.  
> Goal: learn **topics → patterns → forms → tactics → code templates → related problems**.

---

## Clickable Index

### Core Foundation
- [0. How to Use This Guide](#0-how-to-use-this-guide)
- [1. Master Problem-Solving Framework](#1-master-problem-solving-framework)
- [2. Complexity and Constraint Decoder](#2-complexity-and-constraint-decoder)
- [3. C++ Base Template](#3-c-base-template)
- [4. STL Decision System](#4-stl-decision-system)

### Pattern Library
- [5. Prefix Sum and Difference Array](#5-prefix-sum-and-difference-array)
- [6. Binary Search](#6-binary-search)
- [7. Two Pointers and Sliding Window](#7-two-pointers-and-sliding-window)
- [8. Bit Manipulation and Bitmasking](#8-bit-manipulation-and-bitmasking)
- [9. Recursion and Backtracking](#9-recursion-and-backtracking)
- [10. Graph Algorithms](#10-graph-algorithms)
- [11. Trees, Binary Lifting, and DSU](#11-trees-binary-lifting-and-dsu)
- [12. Dynamic Programming](#12-dynamic-programming)
- [13. Math: Modular Arithmetic](#13-math-modular-arithmetic)
- [14. Math: Number Theory](#14-math-number-theory)
- [15. Math: Combinatorics](#15-math-combinatorics)
- [16. Greedy](#16-greedy)
- [17. Stack, Queue, Monotonic Structures](#17-stack-queue-monotonic-structures)
- [18. Heaps, Sets, Maps, Intervals](#18-heaps-sets-maps-intervals)
- [19. Segment Tree and Fenwick Tree](#19-segment-tree-and-fenwick-tree)
- [20. Strings](#20-strings)

### Practice System
- [21. Difficulty-Wise Roadmap](#21-difficulty-wise-roadmap)
- [22. Problem Tables by Topic](#22-problem-tables-by-topic)
- [23. Final Revision Checklists](#23-final-revision-checklists)

---

# 0. How to Use This Guide

For every topic, read in this order:

```text
Concept -> Pattern -> Form -> Intuition -> Template -> Tactics -> Problems
```

Do not memorize code first. First learn **when the form appears**.

```text
New problem
  -> identify clue
  -> match pattern
  -> choose form
  -> adapt template
  -> dry run sample
  -> test edge cases
```

---

# 1. Master Problem-Solving Framework

## Universal flow

```text
1. Read statement carefully.
2. Extract constraints: n, q, value range.
3. Try brute force.
4. Find bottleneck.
5. Ask: can I precompute, sort, binary search, use graph, use DP, or use math?
6. Name the pattern.
7. Write state/invariant/check function.
8. Code the smallest correct template.
9. Dry run edge cases.
```

## Pattern recognition table

| Problem clue | Think |
|---|---|
| repeated range sum | prefix sum |
| many range add updates, final array | difference array |
| sorted / monotonic / minimum possible answer | binary search |
| longest valid contiguous segment | sliding window |
| pair in sorted array | two pointers |
| all subsets, n <= 20 | bitmask / backtracking / bitmask DP |
| shortest path unweighted | BFS |
| shortest path weighted nonnegative | Dijkstra |
| dependency order | topological sort |
| connectivity with edge additions | DSU |
| path query in tree | LCA / binary lifting / HLD |
| repeated choices with overlapping states | DP |
| modulo answer | modular arithmetic |
| divisibility / prime / gcd | number theory |
| count ways | combinatorics / DP |
| nearest greater/smaller | monotonic stack |
| min/max every window | monotonic deque |
| dynamic median/top K | multiset / heap |

---

# 2. Complexity and Constraint Decoder

| Constraint | Usually acceptable |
|---:|---|
| n <= 20 | O(2^n), bitmask, backtracking |
| n <= 100 | O(n^3) sometimes |
| n <= 2,000 | O(n^2) |
| n <= 2e5 | O(n log n) or O(n) |
| n <= 1e6 | O(n) or light O(n log n) |
| q large | precompute / Fenwick / segment tree / offline |
| values up to 1e9 | coordinate compression / maps / math |

Tactic:

```text
Always let constraints reject bad ideas.
```

---

# 3. C++ Base Template

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;
const ll INF = (ll)4e18;
const int MOD = 1'000'000'007;

void solve() {
    // write solution here
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int tc = 1;
    // cin >> tc;
    while (tc--) solve();
    return 0;
}
```

---

# 4. STL Decision System

| Need | Use |
|---|---|
| dynamic array, random access | `vector` |
| string operations | `string` |
| LIFO | `stack` |
| FIFO | `queue` |
| BFS / 0-1 BFS | `queue`, `deque` |
| current min/max | `priority_queue` |
| sorted unique values | `set` |
| sorted duplicates | `multiset` |
| key-value ordered | `map` |
| average O(1) key-value | `unordered_map` |
| coordinate compression | `vector + sort + unique` |
| min/max sliding window | `deque` |
| nearest greater/smaller | stack as vector |
| dynamic median | two multisets |

## Common STL tactics

```cpp
// erase one copy from multiset
if (auto it = ms.find(x); it != ms.end()) ms.erase(it);

// coordinate compression
vector<int> vals = a;
sort(vals.begin(), vals.end());
vals.erase(unique(vals.begin(), vals.end()), vals.end());
int id = lower_bound(vals.begin(), vals.end(), x) - vals.begin();
```

---

# 5. Prefix Sum and Difference Array

## Master map

| Form | Use when | Complexity |
|---|---|---|
| 1D prefix sum | static range sum | build O(n), query O(1) |
| prefix + map | count subarrays with target property | O(n) |
| difference array | many range updates, final array | O(n + q) |
| 2D prefix | rectangle sum | build O(nm), query O(1) |
| 2D difference | rectangle add updates | O(nm + q) |

## Form 1: 1D range sum

### Intuition

`pref[i]` stores everything before index `i`. A range is big prefix minus before-range prefix.

### Template

```cpp
vector<long long> buildPrefix(const vector<long long>& a) {
    int n = a.size();
    vector<long long> pref(n + 1, 0);
    for (int i = 0; i < n; i++) pref[i + 1] = pref[i] + a[i];
    return pref;
}

long long rangeSum(const vector<long long>& pref, int l, int r) {
    return pref[r + 1] - pref[l];
}
```

### Problems

| Difficulty | Problems | Form | Tactic |
|---|---|---|---|
| Newbie | Range Sum Query Immutable, CSES Static Range Sum Queries | 1D prefix | use 1-indexed pref |
| Easy | Subarray Sum Equals K | prefix + map | need previous prefix `sum-k` |
| Medium | Count subarrays divisible by K | prefix modulo freq | normalize negative mod |
| Medium | Matrix Block Sum | 2D prefix | inclusion-exclusion |
| Hard | Max subarray with constraints | prefix + ordered set | query previous prefix bound |

## Form 2: Difference array

### Intuition

Instead of updating every element in `[l,r]`, mark only where value starts and stops changing.

```cpp
vector<long long> applyRangeAdds(int n, vector<tuple<int,int,long long>> ops) {
    vector<long long> diff(n + 1, 0), a(n, 0);
    for (auto [l, r, x] : ops) {
        diff[l] += x;
        if (r + 1 < n) diff[r + 1] -= x;
    }
    long long cur = 0;
    for (int i = 0; i < n; i++) {
        cur += diff[i];
        a[i] = cur;
    }
    return a;
}
```

### How to fit code

```text
If query says: add x to l..r many times and only final array is needed,
then do not update each index. Use diff[l]+=x and diff[r+1]-=x.
```

---

# 6. Binary Search

## Master map

| Form | Pattern | Goal |
|---|---|---|
| classic | sorted array | find value / boundary |
| first true | F F F T T | minimum valid answer |
| last true | T T T F F | maximum valid answer |
| binary search on answer | answer is monotonic | optimize value |
| real binary search | continuous answer | approximate |

## Core question

```text
Can I convert answers into false false false true true true?
```

## First true template

```cpp
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

## Binary search on answer framework

```text
1. Define answer variable x.
2. Define search bounds lo, hi.
3. Write check(x): can we achieve answer x?
4. Prove monotonicity.
5. Use firstTrue or lastTrue.
```

### Problems

| Difficulty | Problems | Form | Tactic |
|---|---|---|---|
| Newbie | Binary Search, Lower Bound | classic | sorted array |
| Easy | First Bad Version | first true | boundary |
| Medium | Koko Eating Bananas | minimize speed | check time <= h |
| Medium | Aggressive Cows | maximize minimum | greedy placement |
| Medium | Factory Machines | minimum time | count produced |
| Hard | Kth Smallest in Multiplication Table | kth by count | count <= mid |
| Hard | Split Array Largest Sum | minimize maximum | greedy partitions |

---

# 7. Two Pointers and Sliding Window

## Master map

| Form | Use when | Movement rule |
|---|---|---|
| opposite ends | sorted pair / palindrome | discard one side |
| fixed window | exactly k length | add right, remove left |
| variable window | longest/min valid segment | expand then shrink |
| count subarrays | at most K | add valid windows count |
| exact K | exact = atMost(K) - atMost(K-1) | reuse helper |
| fix one + two sum | 3Sum / triplets | sort first |

## Opposite ends template

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

## Variable sliding window template

```cpp
int longestAtMostKZeros(vector<int>& a, int k) {
    int n = a.size(), l = 0, zeros = 0, ans = 0;
    for (int r = 0; r < n; r++) {
        zeros += (a[r] == 0);
        while (zeros > k) {
            zeros -= (a[l] == 0);
            l++;
        }
        ans = max(ans, r - l + 1);
    }
    return ans;
}
```

## How to fit code

```text
If all numbers are positive and condition becomes worse as r increases,
sliding window probably works.

If array has negatives, sliding window for sum may fail; try prefix + map.
```

### Problems

| Difficulty | Problems | Form | Tactic |
|---|---|---|---|
| Newbie | Valid Palindrome | opposite ends | skip non-alnum if needed |
| Easy | Two Sum II | opposite ends | sorted pair |
| Easy | Max Average Subarray I | fixed window | keep rolling sum |
| Medium | Longest Substring Without Repeating | variable window | last seen / freq |
| Medium | Fruit Into Baskets | at most 2 distinct | freq map |
| Medium | Subarrays with K Different Integers | exact K | atMost trick |
| Hard | Minimum Window Substring | requirement window | formed count |
| Hard | 3Sum | fix one + two pointers | sort and skip duplicates |

---

# 8. Bit Manipulation and Bitmasking

## Master map

| Form | Use when |
|---|---|
| basic bit ops | check/set/clear/toggle bit |
| bitmask set | n <= 20 subset representation |
| subset enumeration | generate all subsets |
| submask enumeration | DP over subsets |
| bit contribution | sum of pair XOR/AND/OR |
| prefix XOR | range XOR / subarray XOR K |
| high-to-low greedy | max AND/OR answer |
| XOR trie | max XOR pair/query |
| bitmask DP | assignment, TSP |

## Basic helpers

```cpp
bool isSet(long long x, int i) { return (x >> i) & 1LL; }
long long setBit(long long x, int i) { return x | (1LL << i); }
long long clearBit(long long x, int i) { return x & ~(1LL << i); }
long long toggleBit(long long x, int i) { return x ^ (1LL << i); }
bool isPowerOfTwo(long long x) { return x > 0 && (x & (x - 1)) == 0; }
```

## Form 1: XOR cancellation

```cpp
int singleNumber(vector<int>& a) {
    int ans = 0;
    for (int x : a) ans ^= x;
    return ans;
}
```

Intuition:

```text
x ^ x = 0, x ^ 0 = x.
Duplicates cancel.
```

## Form 2: enumerate subsets

```cpp
for (int mask = 0; mask < (1 << n); mask++) {
    vector<int> subset;
    for (int i = 0; i < n; i++) {
        if ((mask >> i) & 1) subset.push_back(a[i]);
    }
}
```

## Form 3: bit contribution for pair XOR sum

```cpp
long long pairXorSum(vector<int>& a) {
    long long ans = 0;
    int n = a.size();
    for (int b = 0; b < 31; b++) {
        long long ones = 0;
        for (int x : a) ones += (x >> b) & 1;
        long long zeros = n - ones;
        ans += ones * zeros * (1LL << b);
    }
    return ans;
}
```

### Problems

| Difficulty | Problems | Form | Tactic |
|---|---|---|---|
| Newbie | Power of Two | bit trick | `x & (x-1)` |
| Easy | Single Number | XOR cancel | duplicates vanish |
| Medium | Subsets | bitmask | loop masks |
| Medium | Subarray XOR Equals K | prefix XOR + map | need `pref ^ k` |
| Medium | Maximum XOR of Two Numbers | XOR trie | prefer opposite bit |
| Hard | TSP | bitmask DP | `dp[mask][last]` |
| Hard | SOS DP | submask DP | sum over subsets |

---

# 9. Recursion and Backtracking

## LCCM Framework

| Part | Question |
|---|---|
| Level | What does one recursive call represent? |
| Choice | What options can I try? |
| Check | Is this choice valid? |
| Move | Apply, recurse, undo |

## Universal template

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

## Form 1: subsets include/exclude

```cpp
void genSubsets(int i, vector<int>& a, vector<int>& cur, vector<vector<int>>& ans) {
    if (i == (int)a.size()) {
        ans.push_back(cur);
        return;
    }
    genSubsets(i + 1, a, cur, ans);
    cur.push_back(a[i]);
    genSubsets(i + 1, a, cur, ans);
    cur.pop_back();
}
```

## Form 2: permutations

```cpp
void permute(vector<int>& a, vector<int>& used, vector<int>& cur) {
    if (cur.size() == a.size()) {
        // save cur
        return;
    }
    for (int i = 0; i < (int)a.size(); i++) {
        if (used[i]) continue;
        used[i] = 1;
        cur.push_back(a[i]);
        permute(a, used, cur);
        cur.pop_back();
        used[i] = 0;
    }
}
```

### Problems

| Difficulty | Problems | Form | Tactic |
|---|---|---|---|
| Newbie | Factorial, Fibonacci | basic recursion | base case first |
| Easy | Subsets | include/exclude | binary choice |
| Easy | Permutations | choose unused | used array |
| Medium | Combination Sum | choose candidate | sort + prune |
| Medium | Generate Parentheses | constrained choices | open/close count |
| Medium | Word Search | grid backtracking | mark/unmark cell |
| Hard | N Queens | board placement | columns + diagonals |
| Hard | Sudoku Solver | constraint pruning | choose empty cell |

---

# 10. Graph Algorithms

## Graph formulation checklist

```text
1. What is a node?
2. What is an edge?
3. Directed or undirected?
4. Weighted or unweighted?
5. Source and target?
6. Need shortest path, reachability, cycle, components, order, MST?
```

## Algorithm selection

| Need | Algorithm |
|---|---|
| reachability / components | DFS/BFS |
| unweighted shortest path | BFS |
| 0/1 edge weights | 0-1 BFS |
| nonnegative weighted shortest path | Dijkstra |
| negative edges | Bellman-Ford |
| all-pairs shortest path, n <= 500 | Floyd-Warshall |
| dependency order | topological sort |
| directed SCC | Kosaraju / Tarjan |
| MST | Kruskal / Prim |
| bipartite | BFS coloring |

## BFS template

```cpp
vector<int> bfs(int n, vector<vector<int>>& g, int src) {
    vector<int> dist(n + 1, -1);
    queue<int> q;
    dist[src] = 0;
    q.push(src);
    while (!q.empty()) {
        int u = q.front(); q.pop();
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

## DFS template

```cpp
void dfs(int u, vector<vector<int>>& g, vector<int>& vis) {
    vis[u] = 1;
    for (int v : g[u]) {
        if (!vis[v]) dfs(v, g, vis);
    }
}
```

## Dijkstra template

```cpp
vector<long long> dijkstra(int n, vector<vector<pair<int,int>>>& g, int src) {
    vector<long long> dist(n + 1, INF);
    priority_queue<pair<long long,int>, vector<pair<long long,int>>, greater<pair<long long,int>>> pq;
    dist[src] = 0;
    pq.push({0, src});

    while (!pq.empty()) {
        auto [d, u] = pq.top(); pq.pop();
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

### Problems

| Difficulty | Problems | Form | Tactic |
|---|---|---|---|
| Newbie | Find if Path Exists | DFS/BFS | build graph |
| Easy | Number of Islands | grid DFS/BFS | flood fill |
| Medium | Rotting Oranges | multi-source BFS | push all sources |
| Medium | Course Schedule | topo / cycle | indegree or DFS color |
| Medium | Network Delay Time | Dijkstra | weighted shortest path |
| Hard | Word Ladder | BFS state graph | transform words |
| Hard | Cheapest Flights K Stops | state shortest path | dist[node][stops] |
| Hard | Critical Connections | Tarjan bridge | low-link |

---

# 11. Trees, Binary Lifting, and DSU

## Tree recognition

```text
Tree = connected graph, n nodes, n-1 edges, no cycle.
Unique path between any two nodes.
```

## Tree patterns

| Clue | Technique |
|---|---|
| subtree size/sum | DFS |
| distance between u,v | LCA + depth |
| kth ancestor | binary lifting |
| many path min/max/gcd | binary lifting aggregate / HLD |
| subtree query/update | Euler tour + Fenwick/segment tree |
| longest path | diameter |
| dynamic connectivity add edges | DSU |
| edge removals offline | reverse + DSU |
| MST | Kruskal + DSU |

## DFS preprocessing

```cpp
vector<vector<int>> g;
vector<int> parentNode, depthNode, subtreeSize;

void dfsTree(int u, int p) {
    parentNode[u] = p;
    subtreeSize[u] = 1;
    for (int v : g[u]) {
        if (v == p) continue;
        depthNode[v] = depthNode[u] + 1;
        dfsTree(v, u);
        subtreeSize[u] += subtreeSize[v];
    }
}
```

## DSU template

```cpp
struct DSU {
    vector<int> p, sz;
    DSU(int n = 0) { init(n); }
    void init(int n) {
        p.resize(n + 1);
        sz.assign(n + 1, 1);
        iota(p.begin(), p.end(), 0);
    }
    int find(int x) {
        if (p[x] == x) return x;
        return p[x] = find(p[x]);
    }
    bool unite(int a, int b) {
        a = find(a); b = find(b);
        if (a == b) return false;
        if (sz[a] < sz[b]) swap(a, b);
        p[b] = a;
        sz[a] += sz[b];
        return true;
    }
    bool same(int a, int b) { return find(a) == find(b); }
};
```

### Problems

| Difficulty | Problems | Form | Tactic |
|---|---|---|---|
| Newbie | Maximum Depth of Binary Tree | tree DFS | return max child depth |
| Easy | Same Tree | recursive compare | base cases |
| Medium | Subtree Size Queries | rooted DFS | aggregate children |
| Medium | LCA of Binary Tree | recursive LCA | return found nodes |
| Medium | Redundant Connection | DSU | first edge same component |
| Hard | Tree Distances | rerooting DP | down + up values |
| Hard | Path Queries | HLD / binary lifting | split paths |
| Hard | Offline Dynamic Connectivity | reverse DSU | deletions become additions |

---

# 12. Dynamic Programming

## DP master checklist

```text
State:
Meaning:
Transition:
Base case:
Invalid value:
Answer:
Time complexity:
```

## DP forms

| Form | State style | Examples |
|---|---|---|
| take / not take | `dp[i][remaining]` | subset sum, knapsack |
| ending at index | `dp[i]` | LIS, Kadane-like |
| matching | `dp[i][j]` | LCS, edit distance |
| interval | `dp[l][r]` | palindrome, matrix chain |
| grid | `dp[r][c]` | paths in grid |
| game | `dp[state] win/lose` | stones game |
| digit DP | pos, tight, started, property | count numbers |
| tree DP | `dp[u][state]` | independent set |
| bitmask DP | `dp[mask]` | assignment, TSP |

## Universal memoization template

```cpp
long long memo[1005][1005];

long long dp(int i, int rem) {
    if (base_case) return base_value;
    long long &res = memo[i][rem];
    if (res != -1) return res;
    res = 0;
    // transitions
    return res;
}
```

## Form 1: 0/1 Knapsack

```cpp
int knapsack(vector<int>& wt, vector<int>& val, int W) {
    int n = wt.size();
    vector<vector<int>> dp(n + 1, vector<int>(W + 1, 0));
    for (int i = 1; i <= n; i++) {
        for (int cap = 0; cap <= W; cap++) {
            dp[i][cap] = dp[i - 1][cap];
            if (cap >= wt[i - 1]) {
                dp[i][cap] = max(dp[i][cap], dp[i - 1][cap - wt[i - 1]] + val[i - 1]);
            }
        }
    }
    return dp[n][W];
}
```

## Form 2: LIS O(n log n)

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

### Problems

| Difficulty | Problems | Form | Tactic |
|---|---|---|---|
| Newbie | Climbing Stairs | 1D DP | previous two states |
| Easy | House Robber | take/not take | no adjacent |
| Medium | Coin Change | unbounded DP | min invalid INF |
| Medium | 0/1 Knapsack | take/not take | item + capacity |
| Medium | LIS | ending at index | tail array |
| Medium | LCS | matching DP | compare chars |
| Hard | Edit Distance | matching DP | insert/delete/replace |
| Hard | Burst Balloons | interval DP | last removed |
| Hard | TSP | bitmask DP | mask + last |
| Hard | Digit DP | digit state | tight flag |

---

# 13. Math: Modular Arithmetic

## When to use

Use modular arithmetic when:

```text
answer is huge
problem asks modulo 1e9+7 / 998244353
division under modulo appears
counting ways with multiplication appears
```

## Core rules

| Operation | Formula |
|---|---|
| addition | `(a + b) % MOD` |
| subtraction | `(a - b + MOD) % MOD` |
| multiplication | `(a * b) % MOD` using long long |
| power | fast exponentiation |
| division | multiply by modular inverse |
| inverse prime MOD | `a^(MOD-2) mod MOD` |

## Fast power

```cpp
long long modPow(long long a, long long e, long long mod) {
    long long res = 1 % mod;
    a %= mod;
    while (e > 0) {
        if (e & 1) res = res * a % mod;
        a = a * a % mod;
        e >>= 1;
    }
    return res;
}
```

## Modular inverse

```cpp
long long modInv(long long a, long long mod) {
    return modPow(a, mod - 2, mod); // only if mod is prime and gcd(a, mod)=1
}
```

## nCr modulo prime

```cpp
const int MOD = 1'000'000'007;
vector<long long> fact, invFact;

void buildComb(int N) {
    fact.assign(N + 1, 1);
    invFact.assign(N + 1, 1);
    for (int i = 1; i <= N; i++) fact[i] = fact[i - 1] * i % MOD;
    invFact[N] = modPow(fact[N], MOD - 2, MOD);
    for (int i = N; i >= 1; i--) invFact[i - 1] = invFact[i] * i % MOD;
}

long long C(int n, int r) {
    if (r < 0 || r > n) return 0;
    return fact[n] * invFact[r] % MOD * invFact[n - r] % MOD;
}
```

### Forms and tactics

| Form | Intuition | Code to use |
|---|---|---|
| huge power | repeated squaring | `modPow` |
| division mod prime | divide by inverse | `a * modInv(b) % MOD` |
| many nCr queries | precompute factorials | `buildComb(N)` |
| negative mod | normalize | `(x % MOD + MOD) % MOD` |
| product overflow | use `long long` or `__int128` | cast before multiply |

### Problems

| Difficulty | Problems | Form | Tactic |
|---|---|---|---|
| Newbie | Power of Numbers | fast power | binary exponentiation |
| Easy | Count Good Numbers | modPow | independent positions |
| Medium | nCr queries | factorial inverse | precompute |
| Medium | Distribute Candies | stars and bars | combinations |
| Hard | Lucas Theorem tasks | nCr mod p large n | digit decomposition |
| Hard | CRT problems | modular system | combine congruences |

---

# 14. Math: Number Theory

## Master map

| Form | Use when |
|---|---|
| gcd/lcm | divisibility, reduce ratios |
| Euclid | fast gcd |
| extended gcd | inverse when mod not prime |
| sieve | many prime checks / factorization |
| prime factorization | divisors, phi, multiplicative functions |
| divisor enumeration | factors of n |
| Euler phi | coprime counts |
| modular equations | congruence solving |

## GCD and LCM

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

## Sieve of Eratosthenes

```cpp
vector<int> primes;
vector<bool> isPrime;

void sieve(int n) {
    isPrime.assign(n + 1, true);
    if (n >= 0) isPrime[0] = false;
    if (n >= 1) isPrime[1] = false;
    for (long long i = 2; i <= n; i++) {
        if (isPrime[i]) {
            primes.push_back((int)i);
            for (long long j = i * i; j <= n; j += i) isPrime[j] = false;
        }
    }
}
```

## Prime factorization

```cpp
vector<pair<long long,int>> factorize(long long n) {
    vector<pair<long long,int>> f;
    for (long long p = 2; p * p <= n; p++) {
        if (n % p == 0) {
            int cnt = 0;
            while (n % p == 0) n /= p, cnt++;
            f.push_back({p, cnt});
        }
    }
    if (n > 1) f.push_back({n, 1});
    return f;
}
```

## Extended GCD

```cpp
long long extgcd(long long a, long long b, long long &x, long long &y) {
    if (b == 0) {
        x = 1; y = 0;
        return a;
    }
    long long x1, y1;
    long long g = extgcd(b, a % b, x1, y1);
    x = y1;
    y = x1 - (a / b) * y1;
    return g;
}
```

## Number theory patterns

| Problem clue | Think |
|---|---|
| “divisible by” | gcd, modulo, factors |
| “coprime” | gcd == 1, phi, inclusion-exclusion |
| “prime many queries” | sieve |
| “number of divisors” | prime factor exponents |
| “sum of divisors” | multiplicative formula |
| “a x + b y = c” | extended gcd |
| “mod inverse but MOD not prime” | extended gcd |
| “LCM of many” | factor max powers |

### Problems

| Difficulty | Problems | Form | Tactic |
|---|---|---|---|
| Newbie | GCD, LCM | Euclid | loop modulo |
| Easy | Count Primes | sieve | mark multiples |
| Easy | Perfect Number | divisors | loop sqrt n |
| Medium | Prime Factorization Queries | SPF sieve | smallest prime factor |
| Medium | Number of Divisors | factor exponents | multiply `cnt+1` |
| Medium | Euler Totient | factorization | `phi -= phi/p` |
| Hard | Diophantine Equation | extended gcd | check c % g |
| Hard | Chinese Remainder Theorem | congruences | combine gradually |

---

# 15. Math: Combinatorics

## Master map

| Form | Formula / idea |
|---|---|
| permutations | `n!` |
| arrangements | `P(n,r)=n!/(n-r)!` |
| combinations | `C(n,r)=n!/(r!(n-r)!)` |
| stars and bars | nonnegative solutions sum x_i = n: `C(n+k-1,k-1)` |
| inclusion-exclusion | subtract overcounted intersections |
| pigeonhole | more objects than boxes |
| Catalan | valid parentheses / binary trees |
| derangements | permutations with no fixed point |

## Common formulas

```text
C(n,r) = C(n,n-r)
C(n,r) = C(n-1,r) + C(n-1,r-1)
Sum C(n,r) over r = 2^n
Catalan(n) = C(2n,n) / (n+1)
```

## Pascal DP nCr

```cpp
vector<vector<long long>> buildPascal(int N, long long mod) {
    vector<vector<long long>> C(N + 1, vector<long long>(N + 1, 0));
    for (int n = 0; n <= N; n++) {
        C[n][0] = C[n][n] = 1;
        for (int r = 1; r < n; r++) {
            C[n][r] = (C[n - 1][r] + C[n - 1][r - 1]) % mod;
        }
    }
    return C;
}
```

## Stars and bars

Use when:

```text
x1 + x2 + ... + xk = n, xi >= 0
answer = C(n+k-1, k-1)
```

If `xi >= 1`, convert:

```text
yi = xi - 1
sum yi = n-k
answer = C(n-1, k-1)
```

## Inclusion-exclusion template idea

```cpp
long long countNotDivisible(long long n, vector<int> primes) {
    int m = primes.size();
    long long bad = 0;
    for (int mask = 1; mask < (1 << m); mask++) {
        long long mult = 1;
        int bits = 0;
        bool overflow = false;
        for (int i = 0; i < m; i++) if ((mask >> i) & 1) {
            bits++;
            if (mult > n / primes[i]) { overflow = true; break; }
            mult *= primes[i];
        }
        if (overflow) continue;
        if (bits & 1) bad += n / mult;
        else bad -= n / mult;
    }
    return n - bad;
}
```

### Problems

| Difficulty | Problems | Form | Tactic |
|---|---|---|---|
| Newbie | Count subsets | powers of 2 | `2^n` |
| Easy | Unique Paths | combinations | choose down moves |
| Medium | Generate Parentheses count | Catalan | valid bracket structures |
| Medium | Distribute identical objects | stars and bars | nonnegative solutions |
| Medium | Count numbers not divisible | inclusion-exclusion | subsets of primes |
| Hard | Derangements | recurrence | `D[n]=(n-1)(D[n-1]+D[n-2])` |
| Hard | Burnside/Pólya basics | symmetry counting | average fixed colorings |

---

# 16. Greedy

## Greedy framework

```text
1. Sort or choose ordering.
2. Define local best choice.
3. Prove exchange argument or invariant.
4. Implement simply.
```

| Clue | Greedy tactic |
|---|---|
| intervals | sort by end time |
| minimize resources | sort events / sweep line |
| choose max profit | heap by available options |
| lexicographically smallest | stack / greedy deletion |
| maximize minimum | often binary search + greedy check |

### Problems

| Difficulty | Problems | Form | Tactic |
|---|---|---|---|
| Easy | Assign Cookies | sort two arrays | smallest sufficient |
| Medium | Non-overlapping Intervals | sort by end | keep earliest ending |
| Medium | Jump Game | farthest reach | maintain max reachable |
| Medium | Task Scheduler | counts / heap | highest frequency |
| Hard | IPO | heap available profits | sort by capital |

---

# 17. Stack, Queue, Monotonic Structures

## Monotonic stack

Use when asking:

```text
next greater / next smaller / previous greater / previous smaller
```

```cpp
vector<int> nextGreater(vector<int>& a) {
    int n = a.size();
    vector<int> ans(n, -1), st;
    for (int i = 0; i < n; i++) {
        while (!st.empty() && a[st.back()] < a[i]) {
            ans[st.back()] = i;
            st.pop_back();
        }
        st.push_back(i);
    }
    return ans;
}
```

## Monotonic deque for max window

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

### Problems

| Difficulty | Problems | Form | Tactic |
|---|---|---|---|
| Easy | Valid Parentheses | stack | match closers |
| Medium | Daily Temperatures | next greater | decreasing stack |
| Medium | Sliding Window Maximum | monotonic deque | remove expired |
| Hard | Largest Rectangle Histogram | previous smaller | stack + sentinel |
| Hard | Sum of Subarray Minimums | contribution | prev less, next less |

---

# 18. Heaps, Sets, Maps, Intervals

## Heap patterns

| Form | Use when |
|---|---|
| top K | heap size K |
| merge K sorted lists | min heap |
| scheduling | heap by end time/profit |
| Dijkstra | min heap distance |

## Interval patterns

| Form | Tactic |
|---|---|
| merge intervals | sort by start |
| minimum rooms | sweep endpoints / min heap end times |
| dynamic non-overlap | ordered set |
| coverage count | difference / sweep line |

### Problems

| Difficulty | Problems | Form | Tactic |
|---|---|---|---|
| Easy | Top K Frequent | heap / bucket | count first |
| Medium | Meeting Rooms II | min heap | earliest ending room |
| Medium | Merge Intervals | sort | merge overlapping |
| Medium | Kth Largest Stream | min heap size k | keep best k |
| Hard | Median from Data Stream | two heaps | balance sizes |

---

# 19. Segment Tree and Fenwick Tree

## Fenwick tree: point update + prefix sum

```cpp
struct Fenwick {
    int n;
    vector<long long> bit;
    Fenwick(int n=0) { init(n); }
    void init(int n_) { n = n_; bit.assign(n + 1, 0); }
    void add(int idx, long long val) {
        for (++idx; idx <= n; idx += idx & -idx) bit[idx] += val;
    }
    long long sumPrefix(int idx) {
        long long res = 0;
        for (++idx; idx > 0; idx -= idx & -idx) res += bit[idx];
        return res;
    }
    long long rangeSum(int l, int r) {
        if (r < l) return 0;
        return sumPrefix(r) - (l ? sumPrefix(l - 1) : 0);
    }
};
```

## Segment tree: range query + point update

```cpp
struct SegTree {
    int n;
    vector<long long> st;
    SegTree(vector<long long>& a) {
        n = a.size();
        st.assign(4*n, 0);
        build(1, 0, n-1, a);
    }
    void build(int p, int l, int r, vector<long long>& a) {
        if (l == r) { st[p] = a[l]; return; }
        int m = (l+r)/2;
        build(p*2, l, m, a);
        build(p*2+1, m+1, r, a);
        st[p] = st[p*2] + st[p*2+1];
    }
    long long query(int p, int l, int r, int ql, int qr) {
        if (qr < l || r < ql) return 0;
        if (ql <= l && r <= qr) return st[p];
        int m = (l+r)/2;
        return query(p*2,l,m,ql,qr) + query(p*2+1,m+1,r,ql,qr);
    }
    void update(int p, int l, int r, int idx, long long val) {
        if (l == r) { st[p] = val; return; }
        int m = (l+r)/2;
        if (idx <= m) update(p*2,l,m,idx,val);
        else update(p*2+1,m+1,r,idx,val);
        st[p] = st[p*2] + st[p*2+1];
    }
};
```

### Problems

| Difficulty | Problems | Form | Tactic |
|---|---|---|---|
| Medium | Range Sum Query Mutable | Fenwick/segment tree | point update |
| Medium | Count Smaller After Self | Fenwick + compression | process from right |
| Medium | Inversions Count | Fenwick | count previous greater |
| Hard | Range Add Range Sum | lazy segment tree | push lazy |
| Hard | Kth order statistic dynamic | Fenwick binary lifting | frequency tree |

---

# 20. Strings

## String pattern map

| Clue | Technique |
|---|---|
| substring search | KMP / rolling hash |
| palindrome | two pointers / Manacher |
| many prefix matches | trie |
| dictionary words | trie / DP |
| repeated pattern | prefix function |
| lexicographic minimal | greedy stack / suffix array advanced |

## KMP prefix function

```cpp
vector<int> prefixFunction(const string& s) {
    int n = s.size();
    vector<int> pi(n);
    for (int i = 1; i < n; i++) {
        int j = pi[i - 1];
        while (j > 0 && s[i] != s[j]) j = pi[j - 1];
        if (s[i] == s[j]) j++;
        pi[i] = j;
    }
    return pi;
}
```

## Trie template

```cpp
struct Trie {
    struct Node {
        int next[26];
        bool end = false;
        Node() { fill(next, next + 26, -1); }
    };
    vector<Node> t;
    Trie() { t.push_back(Node()); }
    void insert(const string& s) {
        int u = 0;
        for (char c : s) {
            int x = c - 'a';
            if (t[u].next[x] == -1) {
                t[u].next[x] = t.size();
                t.push_back(Node());
            }
            u = t[u].next[x];
        }
        t[u].end = true;
    }
};
```

### Problems

| Difficulty | Problems | Form | Tactic |
|---|---|---|---|
| Easy | Valid Palindrome | two pointers | clean chars |
| Medium | Implement Trie | trie | nodes + children |
| Medium | Word Break | DP + trie/set | prefix split |
| Medium | Find Pattern | KMP | prefix function |
| Hard | Palindrome Pairs | trie/hash | reverse words |
| Hard | Shortest Palindrome | KMP | pattern on s + # + rev |

---

# 21. Difficulty-Wise Roadmap

## Phase 1: Newbie to Pupil

```text
C++ basics, STL, arrays, strings, sorting, prefix sum, two pointers, basic recursion, basic math.
```

Practice target:

| Level | Focus |
|---|---|
| 800-1000 CF | implementation, math basics, greedy basics |
| LeetCode Easy | arrays, strings, hash map, stack |

## Phase 2: Pupil to Specialist

```text
Binary search, sliding window, bitwise, DFS/BFS, basic DP, DSU, number theory.
```

| Level | Focus |
|---|---|
| 1100-1400 CF | pattern recognition |
| LeetCode Medium | interview core |

## Phase 3: Specialist to Expert

```text
Dijkstra, topo, tree LCA, Fenwick/segment tree, interval DP, bitmask DP, combinatorics.
```

| Level | Focus |
|---|---|
| 1500-1800 CF | mixed patterns, proof, speed |
| FAANG | clean communication + edge cases |

## Phase 4: Expert to Candidate Master

```text
Advanced DP, graph theory, tree rerooting, HLD, SCC/bridges, math-heavy combinatorics, optimization.
```

| Level | Focus |
|---|---|
| 1900-2100+ CF | combining 2-3 patterns |
| Interview advanced | systematized templates + explanation |

---

# 22. Problem Tables by Topic

## Master recommended problem set

| Topic | Newbie | Easy | Medium | Hard |
|---|---|---|---|---|
| STL | Two Sum, Valid Parentheses | Top K Frequent | Meeting Rooms II | Median Stream |
| Prefix | Range Sum Query | Subarray Sum Equals K | 2D Prefix | Prefix + ordered set |
| Binary Search | Lower Bound | First Bad Version | Koko, Aggressive Cows | Kth Multiplication Table |
| Two Pointers | Palindrome | Two Sum II | 3Sum, Fruit Baskets | Minimum Window |
| Bitwise | Power of Two | Single Number | Max XOR Pair | SOS DP / TSP |
| Backtracking | Subsets | Permutations | Combination Sum | N Queens, Sudoku |
| Graph | DFS connected | Islands | Course Schedule, Dijkstra | Bridges, SCC |
| Tree/DSU | Depth | LCA basics | DSU, Tree Diameter | Rerooting, HLD |
| DP | Climbing Stairs | House Robber | Knapsack, LIS, LCS | Digit DP, Interval DP |
| Modular | Fast Power | Modular inverse | nCr queries | Lucas / CRT |
| Number Theory | GCD | Sieve | Factorization / phi | Diophantine / CRT |
| Combinatorics | Count subsets | Unique Paths | Stars and Bars | Inclusion-Exclusion |

---

# 23. Final Revision Checklists

## Before coding any problem

```text
[ ] What is n and q?
[ ] Static or dynamic?
[ ] Is order important?
[ ] Are values positive, negative, mixed?
[ ] Is there monotonicity?
[ ] Is there repeated range query/update?
[ ] Is graph/tree hidden?
[ ] Is count answer huge/modded?
[ ] Can I sort?
[ ] Can I process offline?
```

## Debug checklist

```text
[ ] Indexing: 0-based or 1-based?
[ ] Overflow: int vs long long?
[ ] Negative modulo normalized?
[ ] Empty array/string handled?
[ ] Single element case?
[ ] Duplicate values?
[ ] Disconnected graph?
[ ] Recursion depth too large?
[ ] Binary search infinite loop?
[ ] DP initialized correctly?
```

## Interview explanation template

```text
1. Brute force idea and complexity.
2. Bottleneck.
3. Pattern that removes bottleneck.
4. Data structure / algorithm choice.
5. Correctness intuition.
6. Complexity.
7. Edge cases.
```

---

# Final Memory Hook

```text
Range -> prefix/Fenwick/segment tree
Window -> two pointers/deque
Sorted answer -> binary search
Choice tree -> recursion/backtracking
Repeated states -> DP
Nodes/edges -> graph
Unique path -> tree/LCA
Components -> DSU
Huge count -> combinatorics/mod math
Divisibility -> number theory
Bit columns -> bitwise contribution
```

