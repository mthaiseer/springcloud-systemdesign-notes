# 001_STL_Foundation.md

# Ultimate STL Foundation — CP CM + FAANG 10/10 Notes

> Scope: STL + core pattern recognition only.  
> Excluded intentionally: advanced graph algorithms, DP patterns, segment tree deep-dives, Fenwick deep-dives, tries, strings advanced algorithms. These should be covered later.

---

## How to Use This File

This is not a huge STL encyclopedia. It is a **foundation playbook**.

Use it in this order:

```text
Day 1  : Vector, string, pair, sort, binary search, prefix, frequency
Day 2  : Stack, queue, deque, sliding window, monotonic deque
Day 3  : Monotonic stack foundation + nearest boundary patterns
Day 4  : Heap / priority_queue foundation + Top K + streaming
Day 5  : set / multiset / map + dynamic median + intervals
Day 6  : Range mapping / sweep line / compression foundation
Day 7  : Full revision + implement all templates from memory
```

Daily rule:

```text
Read one pattern card
  -> write the invariant by hand
  -> dry run the example
  -> code template without looking
  -> solve 2 similar problems
```

---

# 0. Master STL Decision Engine

## 0.1 Problem Statement to STL

```text
PROBLEM STATEMENT
      |
      v
Extract operations
      |
      +--> Need indexed storage / sorting?           -> vector
      +--> Need character processing?                -> string
      +--> Need frequency / fast lookup?             -> unordered_map / map
      +--> Need sorted order / predecessor?          -> set / multiset
      +--> Need duplicate sorted values?             -> multiset
      +--> Need key -> value in sorted order?        -> map
      +--> Need last-in first-out?                   -> stack
      +--> Need first-in first-out?                  -> queue
      +--> Need both ends?                           -> deque
      +--> Need current best min/max repeatedly?     -> priority_queue
      +--> Need window min/max?                      -> monotonic deque
      +--> Need nearest greater/smaller boundary?    -> monotonic stack
      +--> Need interval coverage?                   -> endpoints / set intervals
      +--> Need too many subarrays?                  -> contribution technique
```

## 0.2 Complexity Target

```text
n <= 20          -> recursion / bitmask brute force may pass
n <= 100         -> O(n^3) sometimes okay
n <= 2000        -> O(n^2)
n <= 2e5         -> O(n log n) or O(n)
n <= 1e6         -> O(n) or O(n log n) carefully
q <= 2e5         -> precompute or data structure
values <= 1e9    -> coordinate compression often useful
sum/product huge -> long long / modulo
```

## 0.3 Contest / Interview Answer Template

```text
1. Restate operation.
2. Brute force.
3. Bottleneck.
4. Invariant.
5. STL choice.
6. C++ template.
7. Dry run.
8. Complexity.
9. Edge cases.
```

---

# 1. Core STL Toolbox

## 1.1 vector

### When to Use

```text
Need dynamic array
Need sorting
Need prefix sums
Need random access
Need store graph adjacency later
Need store events / intervals / pairs
```

### Basic Template

```cpp
vector<int> a;
a.push_back(10);
a.pop_back();
int n = a.size();
sort(a.begin(), a.end());
reverse(a.begin(), a.end());
```

### Example Problem

**Problem:** Given numbers, output them sorted and remove duplicates.

```text
input : [5, 1, 2, 5, 1]
output: [1, 2, 5]
```

### ASCII Dry Run

```text
Original:
index:  0  1  2  3  4
value: [5, 1, 2, 5, 1]

sort:
value: [1, 1, 2, 5, 5]

unique compacts:
value: [1, 2, 5, _, _]

resize:
value: [1, 2, 5]
```

### Code

```cpp
vector<int> uniqueSorted(vector<int> a) {
    sort(a.begin(), a.end());
    a.erase(unique(a.begin(), a.end()), a.end());
    return a;
}
```

### Traps

```text
vector erase in middle is O(n).
unique does not erase by itself; it returns new logical end.
Use long long vector for prefix sums if values are large.
```

---

## 1.2 string

### When to Use

```text
Parsing
Brackets
Pattern printing
Character frequency
Digit problems
Greedy stack on digits
```

### Example Problem

**Problem:** Count lowercase character frequencies.

```text
s = "abac"
a:2 b:1 c:1
```

### ASCII Diagram

```text
s:      a  b  a  c
index:  0  1  2  3
freq:  a=2, b=1, c=1
```

### Code

```cpp
vector<int> countLowercase(const string& s) {
    vector<int> freq(26, 0);
    for (char c : s) freq[c - 'a']++;
    return freq;
}
```

### Traps

```text
isdigit/islower should receive unsigned char if input may be non-ASCII.
For CP lowercase English only, direct c-'a' is fine.
```

---

## 1.3 pair / tuple

### When to Use

```text
Intervals: pair<l,r>
Heap state: tuple<dist,node,extra>
Sorting by two keys
Coordinate + index
```

### Example Problem

**Problem:** Sort intervals by start, then end.

```text
[(3,5), (1,4), (1,2)] -> [(1,2), (1,4), (3,5)]
```

### ASCII

```text
Before:
(3,5) (1,4) (1,2)

Sort lexicographically:
first key  -> start
second key -> end

After:
(1,2) (1,4) (3,5)
```

### Code

```cpp
vector<pair<int,int>> ranges = {{3,5},{1,4},{1,2}};
sort(ranges.begin(), ranges.end());
```

---

## 1.4 lower_bound / upper_bound

### Mental Model

```text
lower_bound(x) -> first >= x
upper_bound(x) -> first > x
```

### Example Problem

**Problem:** Count elements <= x in sorted array.

```text
a = [1,2,2,4,7], x = 2
answer = 3
```

### ASCII Diagram

```text
a:       1   2   2   4   7
index:   0   1   2   3   4
                 ^
upper_bound(2) returns index 3
count <= 2 = 3
```

### Code

```cpp
int countLessEqual(vector<int>& a, int x) {
    return upper_bound(a.begin(), a.end(), x) - a.begin();
}
```

### Binary Search Formula Card

```text
count < x   = lower_bound(x)
count <= x  = upper_bound(x)
count > x   = n - upper_bound(x)
count >= x  = n - lower_bound(x)
count == x  = upper_bound(x) - lower_bound(x)
```

---

## 1.5 map / unordered_map

### When to Use

```text
Frequency
Prefix sum count
Coordinate compression mapping
Mode tracking
Seen states
```

### map vs unordered_map

```text
map           -> sorted keys, O(log n), safer in CF
unordered_map -> average O(1), no order, can be hacked
```

### Example Problem

**Problem:** Count subarrays with sum `x`.

```text
a = [1,2,3,-2,5], x = 3
answer = 4
```

### ASCII Dry Run

```text
freq[prefix] starts with {0:1}

current prefix = pref
need previous = pref - x

value  pref  need  freq[need]  ans
  1      1    -2       0        0
  2      3     0       1        1
  3      6     3       1        2
 -2      4     1       1        3
  5      9     6       1        4
```

### Code

```cpp
long long countSubarraysWithSumX(vector<int>& a, long long x) {
    map<long long,long long> freq;
    freq[0] = 1;
    long long pref = 0, ans = 0;
    for (int v : a) {
        pref += v;
        ans += freq[pref - x];
        freq[pref]++;
    }
    return ans;
}
```

### Traps

```text
Always initialize freq[0] = 1.
Use long long prefix.
Sliding window fails when negatives exist.
```

---

## 1.6 set / multiset

### When to Use

```text
Need sorted unique values       -> set
Need sorted duplicates          -> multiset
Need predecessor/successor      -> set/multiset
Need dynamic min/max            -> begin/rbegin
Need arbitrary erase            -> set/multiset
Need dynamic median / top K sum -> multiset
```

### Example Problem

**Problem:** Maintain window minimum using multiset.

```text
a = [4,2,1,5,3], k = 3
mins = [1,1,1]
```

### ASCII Dry Run

```text
i=0 insert 4       ms={4}       not ready
i=1 insert 2       ms={2,4}     not ready
i=2 insert 1       ms={1,2,4}   min=1
i=3 insert 5 erase 4 -> {1,2,5} min=1
i=4 insert 3 erase 2 -> {1,3,5} min=1
```

### Code

```cpp
vector<int> windowMinMultiset(vector<int>& a, int k) {
    multiset<int> ms;
    vector<int> ans;
    for (int i = 0; i < (int)a.size(); i++) {
        ms.insert(a[i]);
        if (i - k >= 0) ms.erase(ms.find(a[i-k]));
        if (i >= k - 1) ans.push_back(*ms.begin());
    }
    return ans;
}
```

### Critical Trap

```cpp
ms.erase(x);          // removes all x
ms.erase(ms.find(x)); // removes one x
```

---

# 2. Stack / Queue / Deque Foundation

## 2.1 stack — Balanced Brackets

### Recognition

```text
Nested structure
Last opened must close first
Reverse order
Expression parsing
Nearest boundary later
```

### Problem

Check whether a bracket string is valid.

```text
s = "[{()}]" -> valid
s = "[(])"  -> invalid
```

### ASCII Diagram

```text
s = [{()}]

char   action          stack
[      push [          [
{      push {          [ {
(      push (          [ { (
)      top '(' pop     [ {
}      top '{' pop     [
]      top '[' pop     empty

empty stack -> valid
```

### Code

```cpp
bool isBalanced(const string& s) {
    map<char,char> need = {{')','('},{']','['},{'}','{'}};
    stack<char> st;
    for (char c : s) {
        if (c=='(' || c=='[' || c=='{') st.push(c);
        else if (need.count(c)) {
            if (st.empty() || st.top() != need[c]) return false;
            st.pop();
        }
    }
    return st.empty();
}
```

### Edge Cases

```text
Empty string -> valid
Only closing bracket -> invalid
Mismatched type -> invalid
Unclosed opening -> invalid
```

---

## 2.2 queue — BFS / Processing Order Foundation

> Graph BFS is covered later. Here queue is only STL foundation.

### Recognition

```text
First item inserted should be processed first.
```

### ASCII

```text
push 1, push 2, push 3

front             back
  1  ->  2  ->  3

pop -> removes 1
front             back
  2  ->  3
```

### Code

```cpp
queue<int> q;
q.push(1);
q.push(2);
q.push(3);
while (!q.empty()) {
    cout << q.front() << " ";
    q.pop();
}
```

---

## 2.3 deque — Sliding Window Candidate Engine

### Recognition

```text
Need push/pop from both ends.
Window min/max in O(n).
```

### Problem

Minimum in every fixed-size window.

```text
a = [4,2,1,5,3], k = 3
answer = [1,1,1]
```

### ASCII Invariant

For minimum:

```text
Deque values are increasing.
front = minimum.

When x arrives:
    pop back while back > x
    push x
    remove expired front
```

### Dry Run

```text
insert 4 -> [4]
insert 2 -> 4 useless -> [2]
insert 1 -> 2 useless -> [1]       min=1
insert 5 -> [1,5]                  min=1
insert 3 -> 5 useless -> [1,3]     min=1
```

### Better Code Using Indices

```cpp
vector<int> slidingWindowMin(vector<int>& a, int k) {
    deque<int> dq; // indices, values increasing
    vector<int> ans;
    for (int i = 0; i < (int)a.size(); i++) {
        while (!dq.empty() && a[dq.back()] >= a[i]) dq.pop_back();
        dq.push_back(i);
        while (!dq.empty() && dq.front() <= i - k) dq.pop_front();
        if (i >= k - 1) ans.push_back(a[dq.front()]);
    }
    return ans;
}
```

### For Maximum

```text
Change >= to <=.
Deque values become decreasing.
front = maximum.
```

---

# 3. Sliding Window Patterns

## 3.1 Fixed Window Sum

### Problem

Maximum sum of any subarray of length `k`.

```text
a = [2,1,5,1,3,2], k = 3
answer = 9 from [5,1,3]
```

### ASCII

```text
[2,1,5] sum=8
  [1,5,1] sum=7  remove 2 add 1
    [5,1,3] sum=9
      [1,3,2] sum=6
```

### Code

```cpp
long long maxFixedWindowSum(vector<int>& a, int k) {
    long long sum = 0, best = LLONG_MIN;
    for (int i = 0; i < (int)a.size(); i++) {
        sum += a[i];
        if (i - k >= 0) sum -= a[i-k];
        if (i >= k - 1) best = max(best, sum);
    }
    return best;
}
```

## 3.2 Frequency / Distinct in Window

### Problem

Number of distinct elements in each window of size `k`.

```text
a = [1,2,1,3,2], k=3
windows: [1,2,1] -> 2
         [2,1,3] -> 3
         [1,3,2] -> 3
```

### ASCII

```text
Add right, remove left, maintain freq.

window [1,2,1]
freq: 1->2, 2->1   distinct=2

slide remove 1 add 3
window [2,1,3]
freq: 1->1, 2->1, 3->1 distinct=3
```

### Code

```cpp
vector<int> distinctInWindow(vector<int>& a, int k) {
    unordered_map<int,int> freq;
    vector<int> ans;
    for (int i = 0; i < (int)a.size(); i++) {
        freq[a[i]]++;
        if (i - k >= 0) {
            int old = a[i-k];
            if (--freq[old] == 0) freq.erase(old);
        }
        if (i >= k - 1) ans.push_back(freq.size());
    }
    return ans;
}
```

## 3.3 Variable Window With Positive Numbers

### Problem

Smallest length subarray with sum at least `S`, positive numbers only.

```text
a = [2,3,1,2,4,3], S=7
answer = 2 from [4,3]
```

### ASCII

```text
Expand right until sum >= S.
Then shrink left while still valid.

[2,3,1,2] sum=8 valid -> shrink
  [3,1,2] sum=6 stop
[3,1,2,4] sum=10 -> shrink
    [2,4] sum=6 stop
[2,4,3] sum=9 -> shrink
  [4,3] sum=7 best length 2
```

### Code

```cpp
int minLenAtLeastS(vector<int>& a, int S) {
    int n = a.size(), l = 0, best = INT_MAX;
    long long sum = 0;
    for (int r = 0; r < n; r++) {
        sum += a[r];
        while (sum >= S) {
            best = min(best, r - l + 1);
            sum -= a[l++];
        }
    }
    return best == INT_MAX ? 0 : best;
}
```

### Trap

```text
This fails with negative numbers.
For negatives, think prefix + map/deque depending on problem.
```

---

# 4. Prefix Sum Patterns

## 4.1 Static Range Sum

### Problem

Answer many sum(l,r) queries.

```text
a = [2,4,1,3]
query [1,3] = 4+1+3 = 8
```

### ASCII

```text
index:  0  1  2  3
a:      2  4  1  3
pref:   0  2  6  7 10
        |  |  |  |  |
        0  1  2  3  4

sum(l,r) = pref[r+1] - pref[l]
sum(1,3) = pref[4] - pref[1] = 10 - 2 = 8
```

### Code

```cpp
vector<long long> buildPrefix(vector<int>& a) {
    vector<long long> pref(a.size()+1, 0);
    for (int i = 0; i < (int)a.size(); i++) pref[i+1] = pref[i] + a[i];
    return pref;
}

long long rangeSum(vector<long long>& pref, int l, int r) {
    return pref[r+1] - pref[l];
}
```

## 4.2 Subarray Sum Equals X

Already shown in map section, but remember:

```text
pref[r] - pref[l-1] = x
pref[l-1] = pref[r] - x
```

### ASCII

```text
[ prefix before l ][ subarray sum x ]
0 ............ l-1 l ........... r

current prefix at r = previous prefix + x
previous prefix = current - x
```

## 4.3 Prefix Mod Pattern

### Problem

Count subarrays with sum divisible by `m`.

### Invariant

```text
pref[r] % m == pref[l-1] % m
```

### ASCII

```text
same remainder means middle part divisible by m

pref[l-1] = 8  -> rem 2
pref[r]   = 14 -> rem 2
subarray  = 6  -> divisible by 3
```

### Code

```cpp
long long countDivisibleByM(vector<int>& a, int m) {
    vector<long long> freq(m, 0);
    freq[0] = 1;
    long long pref = 0, ans = 0;
    for (int x : a) {
        pref = (pref + x) % m;
        if (pref < 0) pref += m;
        ans += freq[pref];
        freq[pref]++;
    }
    return ans;
}
```

---

# 5. Contribution Technique Foundation

## 5.1 Sum of All Subarrays

### Problem

Find sum of sums of all subarrays.

```text
a = [1,2,3]
all subarray sums total = 20
```

### ASCII

Element `a[i]` appears in:

```text
left choices  = i + 1
right choices = n - i
contribution  = a[i] * left * right
```

```text
a = [1, 2, 3]

value  i  left  right  contribution
  1    0    1     3         3
  2    1    2     2         8
  3    2    3     1         9
                         total 20
```

### Code

```cpp
long long sumOfAllSubarrays(vector<int>& a) {
    long long ans = 0;
    int n = a.size();
    for (int i = 0; i < n; i++) {
        ans += 1LL * a[i] * (i + 1) * (n - i);
    }
    return ans;
}
```

## 5.2 Sum of Subarray Minimums

### Problem

Sum minimum of every subarray.

```text
a = [3,1,2,4]
subarray minimum sum = 17
```

### Key Idea

For each element, count subarrays where it is the minimum.

```text
previous less element boundary
next less or equal boundary
```

### ASCII

```text
a = [3, 1, 2, 4]
         ^
       value 1

1 is minimum for many subarrays:
[3,1], [3,1,2], [3,1,2,4]
[1], [1,2], [1,2,4]

left choices * right choices
```

### Duplicate Rule

```text
To avoid double counting duplicates:
previous strictly less
next less or equal
```

### Code Skeleton

```cpp
long long sumSubarrayMins(vector<int>& a) {
    const long long MOD = 1e9 + 7;
    int n = a.size();
    vector<int> left(n), right(n);
    stack<int> st;

    for (int i = 0; i < n; i++) {
        while (!st.empty() && a[st.top()] > a[i]) st.pop();
        left[i] = st.empty() ? i + 1 : i - st.top();
        st.push(i);
    }

    while (!st.empty()) st.pop();

    for (int i = n - 1; i >= 0; i--) {
        while (!st.empty() && a[st.top()] >= a[i]) st.pop();
        right[i] = st.empty() ? n - i : st.top() - i;
        st.push(i);
    }

    long long ans = 0;
    for (int i = 0; i < n; i++) {
        ans = (ans + 1LL * a[i] * left[i] * right[i]) % MOD;
    }
    return ans;
}
```

---

# 6. Monotonic Stack Foundation

## 6.1 Master Rule

```text
Monotonic stack stores unresolved candidates.
Current element either:
  1. waits for future answer, or
  2. resolves previous waiting elements.
```

## 6.2 Direction Table

```text
Next greater on right:
    scan left -> right, pop while current > top

Next smaller on right:
    scan left -> right, pop while current < top

Previous greater:
    scan left -> right, pop while top <= current, answer is top

Previous smaller:
    scan left -> right, pop while top >= current, answer is top
```

---

## 6.3 Next Greater Element

### Problem

```text
nums = [2,1,5,3]
ans  = [5,5,-1,-1]
```

### ASCII

```text
[2, 1, 5, 3]
 ^  ^  |
 |  |  +-- current 5 resolves 1 and 2
 |  +----- next greater of 1 is 5
 +-------- next greater of 2 is 5
```

### Dry Run

```text
i=0 x=2 stack=[]        push 0 -> [0:2]
i=1 x=1 stack=[0:2]     push 1 -> [0:2,1:1]
i=2 x=5
    5 > 1 -> ans[1]=5 pop
    5 > 2 -> ans[0]=5 pop
    push 2 -> [2:5]
i=3 x=3 push -> [2:5,3:3]
```

### Code

```cpp
vector<int> nextGreater(vector<int>& a) {
    int n = a.size();
    vector<int> ans(n, -1);
    stack<int> st;
    for (int i = 0; i < n; i++) {
        while (!st.empty() && a[i] > a[st.top()]) {
            ans[st.top()] = a[i];
            st.pop();
        }
        st.push(i);
    }
    return ans;
}
```

---

## 6.4 Daily Temperatures

### Problem

```text
temp = [73,74,75,71,69,72,76,73]
ans  = [1,1,4,2,1,1,0,0]
```

### ASCII

```text
current warmer day resolves colder previous days

[73,74,75,71,69,72,76,73]
             ^  ^  |
             |  |  +-- 72 resolves 71 and 69
             |  +----- wait = 1
             +-------- wait = 2
```

### Code

```cpp
vector<int> dailyTemperatures(vector<int>& t) {
    int n = t.size();
    vector<int> ans(n, 0);
    stack<int> st;
    for (int i = 0; i < n; i++) {
        while (!st.empty() && t[i] > t[st.top()]) {
            int j = st.top(); st.pop();
            ans[j] = i - j;
        }
        st.push(i);
    }
    return ans;
}
```

---

## 6.5 Final Prices With Discount

### Problem

```text
prices = [8,4,6,2,3]
ans    = [4,2,4,2,3]
```

### ASCII

```text
Each price waits for first smaller/equal price on right.

8 sees 4 -> discount 4 -> final 4
4 sees 2 -> discount 2 -> final 2
6 sees 2 -> discount 2 -> final 4
2 sees none -> final 2
3 sees none -> final 3
```

### Code

```cpp
vector<int> finalPrices(vector<int>& p) {
    vector<int> ans = p;
    stack<int> st;
    for (int i = 0; i < (int)p.size(); i++) {
        while (!st.empty() && p[i] <= p[st.top()]) {
            ans[st.top()] = p[st.top()] - p[i];
            st.pop();
        }
        st.push(i);
    }
    return ans;
}
```

---

## 6.6 Stock Span

### Problem

For each day, count consecutive previous days with price <= current price.

```text
prices = [100,80,60,70,60,75,85]
span   = [1,1,1,2,1,4,6]
```

### ASCII

```text
price 75 at index 5:
previous <= 75: 60,70,60 -> plus current 75
span = 4

[100,80,60,70,60,75,85]
              <--- 4 --->
```

### Code

```cpp
vector<int> stockSpan(vector<int>& p) {
    int n = p.size();
    vector<int> span(n);
    stack<int> st;
    for (int i = 0; i < n; i++) {
        while (!st.empty() && p[st.top()] <= p[i]) st.pop();
        span[i] = st.empty() ? i + 1 : i - st.top();
        st.push(i);
    }
    return span;
}
```

---

## 6.7 Largest Rectangle in Histogram

### Problem

```text
h = [2,1,5,6,2,3]
answer = 10
```

### Mental Model

```text
For every bar:
height = h[i]
width  = distance between previous smaller and next smaller
area   = height * width
```

### ASCII

```text
heights:
        6
      5 █
      █ █
      █ █   3
2     █ █ 2 █
█ 1   █ █ █ █
█ █ █ █ █ █ █
0 1 2 3 4 5 index

Bars 5 and 6 form rectangle height 5 width 2 = 10
```

### Code

```cpp
int largestRectangleArea(vector<int>& h) {
    stack<int> st;
    int n = h.size();
    long long ans = 0;
    for (int i = 0; i <= n; i++) {
        int cur = (i == n ? 0 : h[i]);
        while (!st.empty() && h[st.top()] > cur) {
            int height = h[st.top()];
            st.pop();
            int left = st.empty() ? -1 : st.top();
            int width = i - left - 1;
            ans = max(ans, 1LL * height * width);
        }
        st.push(i);
    }
    return (int)ans;
}
```

### Trap

```text
The final sentinel 0 forces all bars to be processed.
```

---

## 6.8 Maximal Rectangle

### Problem

Given a binary matrix, find largest rectangle of 1s.

### Foundation Idea

Convert each row into histogram heights, then apply largest rectangle.

### ASCII

```text
matrix row by row:
1 0 1 0 0      heights: 1 0 1 0 0
1 0 1 1 1      heights: 2 0 2 1 1
1 1 1 1 1      heights: 3 1 3 2 2
1 0 0 1 0      heights: 4 0 0 3 0

Each row = histogram base.
```

### Code Skeleton

```cpp
int maximalRectangle(vector<vector<char>>& mat) {
    if (mat.empty()) return 0;
    int n = mat.size(), m = mat[0].size();
    vector<int> h(m, 0);
    int ans = 0;
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            h[j] = (mat[i][j] == '1') ? h[j] + 1 : 0;
        }
        ans = max(ans, largestRectangleArea(h));
    }
    return ans;
}
```

---

## 6.9 Maximum of Minimum for Every Window Size

### Problem

For each window size, find maximum among all window minimums.

```text
a = [10,20,30,50,10,70,30]
```

### Idea

For each element, find largest window where it is minimum.

```text
len = next_smaller_index - prev_smaller_index - 1
answer[len] = max(answer[len], a[i])
```

### ASCII

```text
prev smaller     i      next smaller
     |           |          |
-----+-----------+----------+----
        window where a[i] is minimum
```

### Code Skeleton

```cpp
vector<int> maxOfMinForWindow(vector<int>& a) {
    int n = a.size();
    vector<int> left(n), right(n), ans(n+1, 0);
    stack<int> st;

    for (int i = 0; i < n; i++) {
        while (!st.empty() && a[st.top()] >= a[i]) st.pop();
        left[i] = st.empty() ? -1 : st.top();
        st.push(i);
    }
    while (!st.empty()) st.pop();
    for (int i = n-1; i >= 0; i--) {
        while (!st.empty() && a[st.top()] >= a[i]) st.pop();
        right[i] = st.empty() ? n : st.top();
        st.push(i);
    }
    for (int i = 0; i < n; i++) {
        int len = right[i] - left[i] - 1;
        ans[len] = max(ans[len], a[i]);
    }
    for (int len = n-1; len >= 1; len--) ans[len] = max(ans[len], ans[len+1]);
    ans.erase(ans.begin());
    return ans;
}
```

---

## 6.10 Remove K Digits

### Problem

Remove `k` digits to make smallest possible number.

```text
num = "1432219", k = 3
answer = "1219"
```

### ASCII

```text
To make number small, remove bigger digit before smaller digit.

1 4 3 2 2 1 9
  ^ ^
4 before 3 is bad -> remove 4
3 before 2 is bad -> remove 3
2 before 1 is bad -> remove 2
```

### Code

```cpp
string removeKdigits(string num, int k) {
    string st;
    for (char c : num) {
        while (!st.empty() && k > 0 && st.back() > c) {
            st.pop_back();
            k--;
        }
        st.push_back(c);
    }
    while (k-- > 0 && !st.empty()) st.pop_back();

    int i = 0;
    while (i < (int)st.size() && st[i] == '0') i++;
    string ans = st.substr(i);
    return ans.empty() ? "0" : ans;
}
```

---

## 6.11 Remove Duplicate Letters

### Problem

Remove duplicates so every letter appears once and result is lexicographically smallest.

```text
s = "cbacdcbc"
answer = "acdb"
```

### Invariant

```text
Stack result is increasing when possible.
Can pop a character only if it appears later again.
```

### ASCII

```text
c b a c d c b c
c before b before a is lexicographically bad,
but pop only if that character appears later.
```

### Code

```cpp
string removeDuplicateLetters(string s) {
    vector<int> last(26);
    vector<int> used(26, 0);
    for (int i = 0; i < (int)s.size(); i++) last[s[i]-'a'] = i;

    string st;
    for (int i = 0; i < (int)s.size(); i++) {
        int c = s[i] - 'a';
        if (used[c]) continue;
        while (!st.empty() && st.back() > s[i] && last[st.back()-'a'] > i) {
            used[st.back()-'a'] = 0;
            st.pop_back();
        }
        st.push_back(s[i]);
        used[c] = 1;
    }
    return st;
}
```

---

## 6.12 Trapping Rain Water

### Problem

```text
h = [3,0,2,0,4]
answer = 7
```

### ASCII

```text
height:
4                 █
3  █              █
2  █      █       █
1  █  ~   █  ~    █
0  █  _   █  _    █
   3  0   2  0    4

water = 3 + 1 + 3 = 7
```

### Stack Idea

```text
current bar = right wall
popped bar  = bottom
new top     = left wall
```

### Code

```cpp
int trap(vector<int>& h) {
    int ans = 0;
    stack<int> st;
    for (int i = 0; i < (int)h.size(); i++) {
        while (!st.empty() && h[i] > h[st.top()]) {
            int bottom = st.top(); st.pop();
            if (st.empty()) break;
            int left = st.top();
            int width = i - left - 1;
            int bounded = min(h[left], h[i]) - h[bottom];
            ans += width * bounded;
        }
        st.push(i);
    }
    return ans;
}
```

---

## 6.13 Maximum Width Ramp

### Problem

Find max `j - i` such that `i < j` and `a[i] <= a[j]`.

```text
a = [6,0,8,2,1,5]
answer = 4  from i=1, j=5
```

### ASCII

```text
Build decreasing stack of possible left indices:
index: 0 1 2 3 4 5
value: 6 0 8 2 1 5
left candidates: [0:6, 1:0]

Scan from right, resolve farthest j.
```

### Code

```cpp
int maxWidthRamp(vector<int>& a) {
    int n = a.size();
    stack<int> st;
    for (int i = 0; i < n; i++) {
        if (st.empty() || a[i] < a[st.top()]) st.push(i);
    }
    int ans = 0;
    for (int j = n-1; j >= 0; j--) {
        while (!st.empty() && a[st.top()] <= a[j]) {
            ans = max(ans, j - st.top());
            st.pop();
        }
    }
    return ans;
}
```

---

## 6.14 Visible People in Queue

### Problem

For each person, count visible people to the right.

### ASCII

```text
heights = [10,6,8,5,11,9]

10 sees 6, 8, 11 -> 3
6 sees 8 -> 1
8 sees 5, 11 -> 2
```

### Code

```cpp
vector<int> canSeePersonsCount(vector<int>& h) {
    int n = h.size();
    vector<int> ans(n, 0);
    stack<int> st;
    for (int i = n-1; i >= 0; i--) {
        while (!st.empty() && st.top() < h[i]) {
            ans[i]++;
            st.pop();
        }
        if (!st.empty()) ans[i]++;
        st.push(h[i]);
    }
    return ans;
}
```

---

# 7. Heap / Priority Queue Foundation

## 7.1 Master Heap Recognition

```text
Need current maximum repeatedly?       -> max heap
Need current minimum repeatedly?       -> min heap
Need top K largest?                    -> min heap of size K
Need top K smallest?                   -> max heap of size K
Need kth largest?                      -> min heap size K
Need streaming median?                 -> two heaps
Need merge K sorted sources?           -> min heap of heads
Need schedule rooms/resources?         -> min heap by end time
Need cooldown / frequency scheduling?  -> max heap by frequency
Need delete from heap but cannot?      -> lazy deletion
```

## 7.2 C++ Templates

```cpp
priority_queue<int> maxHeap;
priority_queue<int, vector<int>, greater<int>> minHeap;

priority_queue<pair<int,int>> maxPair;
priority_queue<pair<int,int>, vector<pair<int,int>>, greater<pair<int,int>>> minPair;

using T = tuple<int,int,int>;
priority_queue<T, vector<T>, greater<T>> minTuple;
```

---

## 7.3 Kth Largest Element

### Problem

```text
nums = [3,2,1,5,6,4], k = 2
answer = 5
```

### ASCII

```text
min heap size k = strongest k survivors
heap top = weakest survivor = kth largest

process 3 -> [3]
process 2 -> [2,3]
process 1 -> [1,3,2] pop 1 -> [2,3]
process 5 -> [2,3,5] pop 2 -> [3,5]
process 6 -> [3,5,6] pop 3 -> [5,6]
process 4 -> [4,6,5] pop 4 -> [5,6]
answer = 5
```

### Code

```cpp
int findKthLargest(vector<int>& nums, int k) {
    priority_queue<int, vector<int>, greater<int>> pq;
    for (int x : nums) {
        pq.push(x);
        if ((int)pq.size() > k) pq.pop();
    }
    return pq.top();
}
```

---

## 7.4 Last Stone Weight

### Problem

```text
stones = [2,7,4,1,8,1]
answer = 1
```

### ASCII

```text
max heap gives two heaviest each round

[8,7,4,2,1,1]
pop 8,7 -> push 1
[4,2,1,1,1]
pop 4,2 -> push 2
[2,1,1,1]
pop 2,1 -> push 1
[1,1,1]
pop 1,1 -> vanish
[1]
```

### Code

```cpp
int lastStoneWeight(vector<int>& stones) {
    priority_queue<int> pq(stones.begin(), stones.end());
    while (pq.size() > 1) {
        int a = pq.top(); pq.pop();
        int b = pq.top(); pq.pop();
        if (a != b) pq.push(a - b);
    }
    return pq.empty() ? 0 : pq.top();
}
```

---

## 7.5 Top K Frequent Elements

### Problem

```text
nums = [1,1,1,2,2,3], k=2
answer = [1,2]
```

### ASCII

```text
freq:
1 -> 3
2 -> 2
3 -> 1

min heap size k by frequency:
[3:1]
[2:2, 3:1]
insert [1:3] -> pop weakest [1:3]? actually heap by freq:
keep [2:2], [3:1]? Let's process cleanly:

push (3,1) -> [(3,1)]
push (2,2) -> [(2,2),(3,1)]
push (1,3) -> [(1,3),(3,1),(2,2)] size 3 -> pop (1,3)
remain elements 1 and 2 by value: (3,1),(2,2)
```

### Code

```cpp
vector<int> topKFrequent(vector<int>& nums, int k) {
    unordered_map<int,int> freq;
    for (int x : nums) freq[x]++;

    priority_queue<pair<int,int>, vector<pair<int,int>>, greater<pair<int,int>>> pq;
    for (auto [x,c] : freq) {
        pq.push({c,x});
        if ((int)pq.size() > k) pq.pop();
    }

    vector<int> ans;
    while (!pq.empty()) {
        ans.push_back(pq.top().second);
        pq.pop();
    }
    return ans;
}
```

---

## 7.6 K Closest Points to Origin

### Problem

Return `k` points closest to origin.

```text
points = [(1,3),(-2,2)], k=1
answer = [(-2,2)]
```

### ASCII

```text
distance squared:
(1,3)  -> 1 + 9 = 10
(-2,2) -> 4 + 4 = 8

Need K smallest distances -> max heap size K.
Weakest among kept = largest distance at top.
```

### Code

```cpp
vector<vector<int>> kClosest(vector<vector<int>>& points, int k) {
    priority_queue<pair<int,int>> pq; // {dist, index}
    for (int i = 0; i < (int)points.size(); i++) {
        int x = points[i][0], y = points[i][1];
        int d = x*x + y*y;
        pq.push({d, i});
        if ((int)pq.size() > k) pq.pop();
    }
    vector<vector<int>> ans;
    while (!pq.empty()) {
        ans.push_back(points[pq.top().second]);
        pq.pop();
    }
    return ans;
}
```

---

## 7.7 Kth Largest in Stream

### Invariant

```text
min heap size k always stores top k largest seen so far.
```

### ASCII

```text
stream: 4,5,8,2 then add 3,5,10,9,4
k=3

heap after init [4,5,8] -> top 4
add 3 -> [4,5,8]        -> kth 4
add 5 -> [5,5,8]        -> kth 5
add 10 -> [5,10,8]      -> kth 5
add 9 -> [8,9,10]       -> kth 8
```

### Code

```cpp
class KthLargest {
    int k;
    priority_queue<int, vector<int>, greater<int>> pq;
public:
    KthLargest(int k, vector<int>& nums) : k(k) {
        for (int x : nums) add(x);
    }
    int add(int val) {
        pq.push(val);
        if ((int)pq.size() > k) pq.pop();
        return pq.top();
    }
};
```

---

## 7.8 Median from Data Stream

### Invariant

```text
left max heap  = smaller half
right min heap = larger half

left.size() == right.size()
or left.size() == right.size() + 1

median:
odd  -> left.top()
even -> average(left.top(), right.top())
```

### ASCII

```text
numbers sorted: [1,2,3,4,5]
left  = [1,2,3] top=3
right = [4,5]   top=4
median=3
```

### Code

```cpp
class MedianFinder {
    priority_queue<int> left;
    priority_queue<int, vector<int>, greater<int>> right;
public:
    void addNum(int num) {
        if (left.empty() || num <= left.top()) left.push(num);
        else right.push(num);

        if (left.size() > right.size() + 1) {
            right.push(left.top()); left.pop();
        } else if (right.size() > left.size()) {
            left.push(right.top()); right.pop();
        }
    }

    double findMedian() {
        if (left.size() == right.size()) return (left.top() + right.top()) / 2.0;
        return left.top();
    }
};
```

---

## 7.9 Merge K Sorted Lists / Arrays

### Pattern

```text
Min heap with one current head from each sorted source.
```

### ASCII

```text
A: 1 -> 4 -> 7
B: 2 -> 5 -> 8
C: 3 -> 6 -> 9

heap initially: 1(A),2(B),3(C)
pop 1, push 4
pop 2, push 5
pop 3, push 6
...
```

### Code for Arrays

```cpp
vector<int> mergeKSortedArrays(vector<vector<int>>& arr) {
    using T = tuple<int,int,int>; // value, array index, position
    priority_queue<T, vector<T>, greater<T>> pq;
    for (int i = 0; i < (int)arr.size(); i++) {
        if (!arr[i].empty()) pq.push({arr[i][0], i, 0});
    }
    vector<int> ans;
    while (!pq.empty()) {
        auto [val, i, j] = pq.top(); pq.pop();
        ans.push_back(val);
        if (j + 1 < (int)arr[i].size()) pq.push({arr[i][j+1], i, j+1});
    }
    return ans;
}
```

---

## 7.10 K Pairs With Smallest Sums

### Problem

Given sorted arrays, return k pairs with smallest sums.

### ASCII

```text
nums1 = [1,7,11]
nums2 = [2,4,6]

Start first column pairs:
(1,2), (7,2), (11,2)

When pop (1,2), push next with same nums1:
(1,4)
```

### Code

```cpp
vector<vector<int>> kSmallestPairs(vector<int>& a, vector<int>& b, int k) {
    using T = tuple<int,int,int>; // sum, i, j
    priority_queue<T, vector<T>, greater<T>> pq;
    for (int i = 0; i < (int)a.size() && i < k; i++) pq.push({a[i]+b[0], i, 0});
    vector<vector<int>> ans;
    while (k-- && !pq.empty()) {
        auto [sum,i,j] = pq.top(); pq.pop();
        ans.push_back({a[i], b[j]});
        if (j + 1 < (int)b.size()) pq.push({a[i]+b[j+1], i, j+1});
    }
    return ans;
}
```

---

## 7.11 Meeting Rooms II

### Problem

Minimum rooms needed for meetings.

```text
intervals = [[0,30],[5,10],[15,20]]
answer = 2
```

### ASCII

```text
Sort by start:
[0,30]
[5,10]
[15,20]

min heap stores end times of active rooms.

start 0 -> room ends 30      heap [30]
start 5 -> 5 < 30 new room   heap [10,30]
start 15 -> 15 >= 10 reuse   pop 10 push 20 -> [20,30]
rooms = 2
```

### Code

```cpp
int minMeetingRooms(vector<vector<int>>& intervals) {
    sort(intervals.begin(), intervals.end());
    priority_queue<int, vector<int>, greater<int>> pq;
    int ans = 0;
    for (auto& in : intervals) {
        int s = in[0], e = in[1];
        if (!pq.empty() && pq.top() <= s) pq.pop();
        pq.push(e);
        ans = max(ans, (int)pq.size());
    }
    return ans;
}
```

---

## 7.12 Task Scheduler

### Problem

Tasks with cooldown `n`; find minimum intervals.

### ASCII

```text
tasks: A A A B B B, cooldown=2

Use most frequent task first:
A _ _ A _ _ A
Fill B:
A B _ A B _ A B
answer 8
```

### Code Formula

```cpp
int leastInterval(vector<char>& tasks, int n) {
    vector<int> cnt(26,0);
    for (char c : tasks) cnt[c-'A']++;
    int mx = *max_element(cnt.begin(), cnt.end());
    int same = count(cnt.begin(), cnt.end(), mx);
    return max((int)tasks.size(), (mx - 1) * (n + 1) + same);
}
```

---

## 7.13 Minimum Cost to Connect Sticks

### Pattern

```text
Repeatedly combine two smallest -> min heap.
```

### ASCII

```text
sticks = [2,4,3]
heap [2,3,4]
pop 2,3 -> cost 5, push 5
heap [4,5]
pop 4,5 -> cost 9
total = 5 + 9 = 14
```

### Code

```cpp
int connectSticks(vector<int>& sticks) {
    priority_queue<int, vector<int>, greater<int>> pq(sticks.begin(), sticks.end());
    int cost = 0;
    while (pq.size() > 1) {
        int a = pq.top(); pq.pop();
        int b = pq.top(); pq.pop();
        cost += a + b;
        pq.push(a + b);
    }
    return cost;
}
```

---

## 7.14 IPO / Max Profit With Capital Constraint

### Pattern

```text
Sort projects by required capital.
Use max heap for profits currently affordable.
```

### ASCII

```text
capital available = W

projects sorted by capital:
(cap=0, profit=1)
(cap=1, profit=2)
(cap=1, profit=3)

Add all affordable projects to max profit heap.
Pick highest profit.
Increase W.
Repeat k times.
```

### Code

```cpp
int findMaximizedCapital(int k, int W, vector<int>& profits, vector<int>& capital) {
    vector<pair<int,int>> p;
    for (int i = 0; i < (int)profits.size(); i++) p.push_back({capital[i], profits[i]});
    sort(p.begin(), p.end());
    priority_queue<int> pq;
    int i = 0, n = p.size();
    while (k--) {
        while (i < n && p[i].first <= W) pq.push(p[i++].second);
        if (pq.empty()) break;
        W += pq.top(); pq.pop();
    }
    return W;
}
```

---

## 7.15 Lazy Deletion Heap

### When Needed

```text
Heap cannot erase arbitrary old values.
Use delayed map to mark invalid values.
When invalid value reaches top, pop it.
```

### ASCII

```text
heap:    [9, 7, 7, 3]
delayed: 7 -> 1

top 9 valid -> use
later top 7 invalid -> pop and delayed[7]--
```

### Template

```cpp
void prune(priority_queue<int>& pq, unordered_map<int,int>& delayed) {
    while (!pq.empty() && delayed[pq.top()] > 0) {
        delayed[pq.top()]--;
        pq.pop();
    }
}
```

---

# 8. Dynamic Median / Cost With Multiset

## 8.1 Sliding Window Median / Cost

### Problem

For each window, maintain median and/or cost to make all equal.

```text
window = [1,2,10,20,30]
median = 10
cost = 47
```

### ASCII

```text
lo = smaller half, contains median
hi = larger half

lo: [1, 2, 10]  sum=13
hi: [20, 30]    sum=50

median = max(lo) = 10
left cost  = 10*3 - 13 = 17
right cost = 50 - 10*2 = 30
total = 47
```

### Code

```cpp
struct SlidingCost {
    multiset<long long> lo, hi;
    long long leftSum = 0, rightSum = 0;

    long long median() const { return *lo.rbegin(); }

    void rebalance() {
        while (lo.size() < hi.size()) {
            auto it = hi.begin(); long long x = *it;
            hi.erase(it); rightSum -= x;
            lo.insert(x); leftSum += x;
        }
        while (lo.size() > hi.size() + 1) {
            auto it = prev(lo.end()); long long x = *it;
            lo.erase(it); leftSum -= x;
            hi.insert(x); rightSum += x;
        }
    }

    void insert(long long x) {
        if (lo.empty() || x <= median()) lo.insert(x), leftSum += x;
        else hi.insert(x), rightSum += x;
        rebalance();
    }

    void erase(long long x) {
        auto it = lo.find(x);
        if (it != lo.end()) lo.erase(it), leftSum -= x;
        else {
            it = hi.find(x);
            hi.erase(it); rightSum -= x;
        }
        rebalance();
    }

    long long cost() const {
        long long m = median();
        return m * (long long)lo.size() - leftSum + rightSum - m * (long long)hi.size();
    }
};
```

---

# 9. Range Mapping / Interval Foundation

## 9.1 Core Interval Model

```text
Interval [l,r] covers x if:
l <= x <= r

Does not cover x if:
r < x  OR  l > x
```

### ASCII

```text
      [2---------5]
        [3----------------10]
                            [13-----16]
----1---2---3---4---5---6---10---12---13---16---->
                ^ covered                ^ not covered
                4                        12
```

---

## 9.2 Offline Point Coverage

### Problem

Given intervals and point queries, check if a point is covered.

```text
ranges = [[2,5],[3,10],[13,16]]
x=4  -> covered
x=12 -> not covered
```

### Formula

```text
covered = total - count(r < x) - count(l > x)
```

### ASCII Dry Run

```text
L = [2,3,13]
R = [5,10,16]

x = 4
l > 4 : [13]       count 1
r < 4 : []         count 0
covered = 3 - 1 - 0 = 2

x = 12
l > 12: [13]       count 1
r < 12: [5,10]     count 2
covered = 3 - 1 - 2 = 0
```

### Code

```cpp
struct OfflineCoverage {
    vector<int> L, R;
    int n;
    OfflineCoverage(vector<pair<int,int>>& ranges) {
        n = ranges.size();
        for (auto [l,r] : ranges) L.push_back(l), R.push_back(r);
        sort(L.begin(), L.end());
        sort(R.begin(), R.end());
    }
    bool covered(int x) {
        int startAfter = L.end() - upper_bound(L.begin(), L.end(), x);
        int endBefore = lower_bound(R.begin(), R.end(), x) - R.begin();
        return n - startAfter - endBefore > 0;
    }
};
```

---

## 9.3 Count Intervals Covering Point

### Formula

```text
covering(x) = count(l <= x) - count(r < x)
```

### ASCII

```text
At x:
started intervals  = l <= x
ended intervals    = r < x
active/covering    = started - ended
```

### Code

```cpp
int countCovering(vector<int>& L, vector<int>& R, int x) {
    int started = upper_bound(L.begin(), L.end(), x) - L.begin();
    int ended = lower_bound(R.begin(), R.end(), x) - R.begin();
    return started - ended;
}
```

---

## 9.4 Online Merged Intervals With set

### Problem

Support insert range and query point coverage.

### Invariant

```text
set stores disjoint sorted intervals only.

[1,3] [6,8]
insert [2,7]
result [1,8]
```

### ASCII

```text
Before:
[1---3]   [6---8]
   [2---------7]

Merge all overlapping:
[1-----------8]
```

### Code

```cpp
struct RangeCover {
    set<pair<int,int>> ranges;

    bool covered(int x) {
        auto it = ranges.upper_bound({x, INT_MAX});
        if (it == ranges.begin()) return false;
        --it;
        return it->second >= x;
    }

    void insertRange(int l, int r) {
        auto it = ranges.lower_bound({l, INT_MIN});
        if (it != ranges.begin()) {
            auto p = prev(it);
            if (p->second >= l - 1) it = p;
        }
        while (it != ranges.end() && it->first <= r + 1) {
            l = min(l, it->first);
            r = max(r, it->second);
            it = ranges.erase(it);
        }
        ranges.insert({l,r});
    }
};
```

---

## 9.5 Remove Range From Coverage

### Problem

Given disjoint covered intervals, remove `[l,r]`.

```text
covered: [1,10]
remove : [3,6]
result : [1,2] [7,10]
```

### ASCII

```text
Before:
[1----------------10]
      [3------6]
After:
[1--2]          [7--10]
```

### Code

```cpp
void removeRange(set<pair<int,int>>& ranges, int l, int r) {
    auto it = ranges.lower_bound({l, INT_MIN});
    if (it != ranges.begin()) --it;

    vector<pair<int,int>> add;
    while (it != ranges.end() && it->first <= r) {
        auto [a,b] = *it;
        if (b < l) { ++it; continue; }
        it = ranges.erase(it);
        if (a < l) add.push_back({a, l-1});
        if (b > r) add.push_back({r+1, b});
    }
    for (auto p : add) ranges.insert(p);
}
```

---

## 9.6 Check Full Range Covered

### Problem

Check if every point in `[l,r]` is covered by merged intervals.

### ASCII

```text
ranges: [1,10] [20,30]
query [3,8]  -> covered
query [8,22] -> not covered
```

### Code

```cpp
bool fullCovered(set<pair<int,int>>& ranges, int l, int r) {
    auto it = ranges.upper_bound({l, INT_MAX});
    if (it == ranges.begin()) return false;
    --it;
    return it->first <= l && it->second >= r;
}
```

---

## 9.7 Overlap Check

### Problem

Check whether new interval overlaps existing intervals.

### ASCII

```text
existing: [1,5] [10,15]
new [6,9]   -> no overlap
new [4,8]   -> overlaps [1,5]
new [8,12]  -> overlaps [10,15]
```

### Code

```cpp
bool overlaps(set<pair<int,int>>& ranges, int l, int r) {
    auto it = ranges.lower_bound({l, INT_MIN});
    if (it != ranges.end() && it->first <= r) return true;
    if (it != ranges.begin()) {
        --it;
        if (it->second >= l) return true;
    }
    return false;
}
```

---

## 9.8 Sweep Line Maximum Overlap

### Problem

Find maximum number of overlapping intervals.

```text
intervals = [1,4], [2,5], [7,9]
answer = 2
```

### ASCII

```text
+1 at start, -1 after end

1: +1 active=1
2: +1 active=2  max=2
5: -1 active=1
6: -1 active=0
7: +1 active=1
10:-1 active=0
```

### Code

```cpp
int maxOverlap(vector<pair<int,int>>& ranges) {
    map<int,int> event;
    for (auto [l,r] : ranges) {
        event[l]++;
        event[r+1]--;
    }
    int cur = 0, ans = 0;
    for (auto [x,delta] : event) {
        cur += delta;
        ans = max(ans, cur);
    }
    return ans;
}
```

---

## 9.9 Difference Array for Range Add

### Problem

Apply many range increments on small/medium coordinate array.

```text
n=5
add [1,3] +2
add [2,4] +1
result = [0,2,3,3,1]
```

### ASCII

```text
diff starts all 0
add [l,r] val:
diff[l] += val
diff[r+1] -= val

prefix diff gives final array.
```

### Code

```cpp
vector<long long> rangeAdd(int n, vector<tuple<int,int,int>>& ops) {
    vector<long long> diff(n+1, 0), ans(n, 0);
    for (auto [l,r,val] : ops) {
        diff[l] += val;
        if (r+1 < n) diff[r+1] -= val;
    }
    long long cur = 0;
    for (int i = 0; i < n; i++) {
        cur += diff[i];
        ans[i] = cur;
    }
    return ans;
}
```

---

## 9.10 Coordinate Compression Foundation

### When to Use

```text
Coordinates up to 1e9 but only 2e5 important points.
```

### ASCII

```text
real coordinates:  10, 1000000000, 50
sorted unique:     10, 50, 1000000000
compressed index:   0,  1,      2
```

### Code

```cpp
vector<int> compress(vector<int> values) {
    sort(values.begin(), values.end());
    values.erase(unique(values.begin(), values.end()), values.end());
    return values;
}

int getId(vector<int>& comp, int x) {
    return lower_bound(comp.begin(), comp.end(), x) - comp.begin();
}
```

---

# 10. Parser / Nested Structure Foundation

## 10.1 Molecular Formula Parser

### Problem

Parse formula like:

```text
Mg(OH)2 -> H2MgO2
K4(ON(SO3)2)2 -> K4N2O14S4
```

### ASCII

```text
Mg(OH)2

Mg -> add Mg:1
(OH) -> parse inside:
    O:1
    H:1
multiplier 2 -> O:2 H:2
merge -> H:2 Mg:1 O:2
```

### Code

```cpp
class FormulaParser {
public:
    string s; int n;

    int readNumber(int& i) {
        int num = 0;
        while (i < n && isdigit(s[i])) num = num * 10 + (s[i++] - '0');
        return num == 0 ? 1 : num;
    }

    string readElement(int& i) {
        string name;
        name += s[i++];
        while (i < n && islower(s[i])) name += s[i++];
        return name;
    }

    map<string,int> parse(int& i) {
        map<string,int> res;
        while (i < n && s[i] != ')') {
            if (isupper(s[i])) {
                string e = readElement(i);
                int c = readNumber(i);
                res[e] += c;
            } else if (s[i] == '(') {
                i++;
                auto inside = parse(i);
                i++;
                int mul = readNumber(i);
                for (auto [e,c] : inside) res[e] += c * mul;
            }
        }
        return res;
    }

    string countOfAtoms(string formula) {
        s = formula; n = s.size();
        int i = 0;
        auto mp = parse(i);
        string ans;
        for (auto [e,c] : mp) {
            ans += e;
            if (c > 1) ans += to_string(c);
        }
        return ans;
    }
};
```

---

# 11. Pattern Printing Foundation

## 11.1 Coordinate Thinking

### Rule

```text
Stop thinking in spaces.
Think in coordinates.
At cell (i,j), should I print star?
```

### ASCII

Main diagonal for n=5:

```text
(i,j) star when i == j

*....
.*...
..*..
...*.
....*
```

Border:

```text
star when i==0 or j==0 or i==n-1 or j==m-1

*****
*...*
*...*
*****
```

### Code

```cpp
bool star(int i, int j, int n, int m) {
    return i == 0 || j == 0 || i == n-1 || j == m-1;
}

void printPattern(int n, int m) {
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            cout << (star(i,j,n,m) ? '*' : '.');
        }
        cout << '\n';
    }
}
```

---

# 12. Master Cheat Sheets

## 12.1 STL Container Decision Table

| Need | Use |
|---|---|
| Random access + sorting | `vector` |
| Character processing | `string` |
| LIFO | `stack` |
| FIFO | `queue` |
| Both ends | `deque` |
| Current max/min only | `priority_queue` |
| Sorted unique | `set` |
| Sorted duplicates | `multiset` |
| Key-value sorted | `map` |
| Fast average lookup | `unordered_map` |
| Window min/max | monotonic `deque` |
| Nearest boundary | monotonic `stack` |
| Interval merge | `set<pair<int,int>>` |

## 12.2 Pattern Recognition Table

| Signal | Pattern | STL |
|---|---|---|
| every window of size k | fixed sliding window | deque / multiset / map |
| window min/max | monotonic deque | deque |
| subarray sum x with negatives | prefix + freq | map/unordered_map |
| nearest greater/smaller | monotonic stack | stack/vector |
| histogram rectangle | nearest smaller boundary | stack |
| sum of subarray mins | contribution + stack | stack |
| kth largest | bounded heap | min heap |
| top k frequent | freq + bounded heap | unordered_map + heap |
| streaming median | two heaps | max heap + min heap |
| dynamic median/cost with erase | two multisets | multiset |
| interval point coverage offline | endpoints | vector + binary search |
| dynamic interval insert/delete | merged intervals | set<pair<int,int>> |
| range add many ops | difference array | vector/map |
| huge coordinates | compression | vector + lower_bound |
| nested parentheses/parser | recursion/stack | stack/map |

## 12.3 Monotonic Stack Equality Rules

```text
Next greater strict:
    pop while current > top

Next greater or equal:
    pop while current >= top

Previous smaller strict:
    pop while top >= current

Contribution with duplicates:
    one side strict, other side non-strict
```

## 12.4 Multiset Erase Rule

```cpp
ms.erase(x);          // all copies of x
ms.erase(ms.find(x)); // one copy of x
```

## 12.5 Heap Inversion Rule

```text
Top K largest  -> min heap size K
Top K smallest -> max heap size K
Current max    -> max heap
Current min    -> min heap
```

## 12.6 Range Boundary Rules

```text
Interval [l,r] covers x:
    l <= x <= r

Count covering x:
    count(l <= x) - count(r < x)

Not covering x:
    r < x OR l > x

Merged interval query:
    find interval with start <= x using upper_bound({x, INF}) then --it
```

---

# 13. Final 7-Day Practice Ladder

## Day 1 — STL Basics

```text
vector sort unique
lower_bound / upper_bound
map frequency
prefix sum
subarray sum x
```

## Day 2 — Window

```text
fixed window sum
variable positive window
window distinct
window min with deque
window min with multiset
```

## Day 3 — Stack

```text
balanced brackets
next greater
daily temperatures
stock span
final prices
```

## Day 4 — Boundary Stack

```text
histogram
maximal rectangle
sum subarray minimums
max of mins every window
trapping rain water
```

## Day 5 — Heap

```text
kth largest
top k frequent
k closest points
stream kth largest
median stream
merge k sorted arrays
```

## Day 6 — Intervals

```text
offline point coverage
count covering point
merged insert
remove range
full coverage
sweep line max overlap
difference array
compression
```

## Day 7 — Revision

```text
Write all templates from memory.
Do 3 dry runs per pattern.
Solve mixed problems without seeing tags.
```

---

# 14. Final Submission Checklist

```text
[ ] Did I choose STL from operations, not from memory?
[ ] Is complexity enough for constraints?
[ ] long long for prefix/contribution/cost?
[ ] multiset erase one occurrence?
[ ] lower_bound/upper_bound inclusive boundary correct?
[ ] monotonic stack equality correct for duplicates?
[ ] heap stale states pruned if lazy deletion used?
[ ] window size exactly k before answer?
[ ] coordinate compression includes all required endpoints?
[ ] empty input / k=1 / k=n handled?
```

---

# 15. One-Picture Memory

```text
                         STL FOUNDATION MAP

                              Problem
                                 |
                          Extract operation
                                 |
   ----------------------------------------------------------------
   |          |          |          |          |          |        |
 sort       count      window     nearest    current    interval  nested
 vector     map        deque      stack      heap       set       stack
   |          |          |          |          |          |        |
 unique     freq       min/max    boundary   top-k      coverage parser
 prefix     prefix     median     hist       stream     sweep     formula
 binary     lookup     cost       contrib    merge      diff      brackets
 search
```

---

## End Note

This file is the foundation. After this, create separate files:

```text
002_STL_ABCD_Ladder.md          -> rating-wise problem ladder
003_STL_RealWorld_Problems.md   -> backend/product mapping
004_Graph_Foundation.md         -> graph later
005_DP_Foundation.md            -> DP later
```
