# MiniSTL_ABCD_Ladder_Foundation.md

## STL Foundation for Codeforces Candidate Master

> **Goal:** Build the mental operating system for using C++ STL as a competitive programming weapon.  
> **Target:** Codeforces 800 → 2200, with special focus on reaching **Candidate Master**.  
> **Language:** C++17.  
> **Core Idea:** STL is not a syntax library. STL is an **operation-selection engine**.

---

# 000. Index

```text
FOUNDATION
│
├── 001_How_To_Use_This_Book
├── 002_How_To_Think_In_STL
├── 003_STL_Operation_Mental_Model
├── 004_Constraint_To_Algorithm_To_STL
├── 005_STL_Decision_Tree
├── 006_How_To_Recognize_Patterns
├── 007_How_To_Think_During_Contest
├── 008_Contest_Debugging_Checklist
├── 009_Common_STL_Mistakes
├── 010_Time_Complexity_CheatSheet
├── 011_STL_Memory_Model
├── 012_STL_Recognition_Flowchart
├── 013_Container_Selection_Guide
├── 014_When_NOT_To_Use_STL
└── 015_CM_Mindset
```

---

# 001. How To Use This Book

## Why This Book Exists

Most STL notes teach this:

```text
vector
    push_back
    pop_back
    size
    erase

map
    insert
    find
    erase
```

That is useful, but it is not enough for Codeforces.

In contests, you do not see a problem statement saying:

```text
Please use a multiset.
```

You see:

```text
Maintain a dynamic collection.
After every update, answer nearest value / minimum / maximum / median.
```

So the real skill is not memorizing methods.

The real skill is:

```text
Problem statement
      ↓
Hidden operation
      ↓
Correct data structure
      ↓
Known pattern
      ↓
Fast implementation
```

This book teaches STL as a **pattern recognition ladder**.

---

## The Correct Way To Study

Do not read this like a dictionary.

Wrong way:

```text
Read vector.
Read map.
Read set.
Read priority_queue.
Forget everything.
```

Correct way:

```text
For each chapter:
    1. Understand the operation.
    2. Recognize the problem clue.
    3. Dry run the pattern.
    4. Write the template without looking.
    5. Solve 5–10 problems using that exact pattern.
    6. Record mistakes.
```

---

## The Learning Loop

```text
Read pattern
      ↓
Understand operation
      ↓
Dry run on tiny input
      ↓
Code from memory
      ↓
Submit practice problems
      ↓
Analyze WA/TLE
      ↓
Compress pattern into one sentence
      ↓
Use in contest
```

The final goal is not to know STL.

The final goal is to see this:

```text
"nearest greater to the left"
```

and immediately think:

```text
monotonic stack
```

or see this:

```text
"count subarrays with sum K"
```

and immediately think:

```text
prefix sum + unordered_map
```

---

## How Every Future Chapter Should Be Written

Each pattern chapter in this ladder should follow this shape:

```text
1. Rating range
2. Recognition clues
3. Mental model
4. ASCII diagram
5. Brute force
6. Why brute force fails
7. Optimal STL pattern
8. Dry run
9. C++17 template
10. Variations
11. Common bugs
12. Complexity
13. Contest forms
14. Practice ladder
15. One-picture summary
```

This structure makes notes easy to review before contests.

---

## What This Foundation Gives You

After this foundation, you should be able to answer:

```text
What operation must be fast?
What complexity is allowed?
Should I sort?
Is the problem online or offline?
Do I need order?
Do I need frequency?
Do I need nearest value?
Do I need current best repeatedly?
Do I need dynamic deletion?
Do I need window state?
```

Those questions are more important than STL syntax.

---

# 002. How To Think In STL

## STL Is A Set Of Machines

Do not think of STL as containers.

Think of STL as machines.

```text
vector          = random access machine
string          = character sequence machine
sort            = order creation machine
unordered_map   = frequency / lookup machine
map             = ordered key-value machine
set             = ordered unique machine
multiset        = ordered duplicate machine
stack           = last unresolved machine
queue           = BFS wave machine
deque           = two-ended / window machine
priority_queue  = best candidate machine
list            = O(1) move node machine
```

When you read a problem, ask:

```text
Which machine matches the hidden operation?
```

---

## Beginner Thinking vs CM Thinking

Beginner:

```text
I know vector and map.
Can I somehow solve this?
```

CM-level:

```text
The operation is nearest active value.
I need ordered dynamic search.
Use set/multiset.
```

Beginner:

```text
I need maximum in every window.
Maybe sort each window?
```

CM-level:

```text
Each element enters and leaves once.
Need max of moving window.
Use monotonic deque.
```

Beginner:

```text
I need shortest path.
Maybe BFS?
```

CM-level:

```text
Edges are weighted non-negative.
Need current smallest distance repeatedly.
Use priority_queue Dijkstra.
```

---

## The Main Question

Never start with:

```text
Which STL should I use?
```

Start with:

```text
What operation is repeated many times?
```

Examples:

```text
Need to count previous same values?
    -> unordered_map

Need to find smallest element >= x?
    -> set.lower_bound

Need to repeatedly take smallest deadline?
    -> priority_queue min-heap

Need to maintain median under insert/delete?
    -> two multisets

Need to merge intervals?
    -> vector + sort

Need to count active intervals?
    -> sweep line events + sort

Need to find next greater element?
    -> monotonic stack
```

---

## Operation-First Mindset

```text
Problem
  |
  v
Repeated operation?
  |
  +-- lookup/count fast ---------- unordered_map
  |
  +-- sorted search -------------- set/map
  |
  +-- sorted duplicates ---------- multiset
  |
  +-- max/min repeatedly --------- priority_queue
  |
  +-- FIFO expansion ------------- queue
  |
  +-- last opened/unresolved ----- stack
  |
  +-- both ends/window ----------- deque
  |
  +-- index/sort/prefix ---------- vector
```

---

## Example 1: Frequency

Problem clue:

```text
Count number of pairs with equal value.
```

Hidden operation:

```text
For current x, how many previous x have I seen?
```

STL:

```text
unordered_map<int, long long> freq;
```

Pattern:

```cpp
long long ans = 0;
unordered_map<int, long long> freq;

for (int x : a) {
    ans += freq[x];
    freq[x]++;
}
```

---

## Example 2: Nearest Value

Problem clue:

```text
For each query x, find the smallest active value >= x.
```

Hidden operation:

```text
ordered lower_bound
```

STL:

```cpp
set<int> s;
auto it = s.lower_bound(x);
```

---

## Example 3: Window Maximum

Problem clue:

```text
For every subarray of length k, output maximum.
```

Bad operation:

```text
sort each window -> too slow
scan each window -> O(NK)
```

Hidden operation:

```text
maintain candidates for max as window moves
```

STL:

```text
deque<int>
```

Pattern:

```text
values in deque are decreasing
front is max
remove expired index
remove useless smaller values
```

---

# 003. STL Operation Mental Model

## The Operation Table

```text
Need                                      STL / Pattern
---------------------------------------------------------------
Random access by index                    vector
Store characters                          string
Sort all data once                        vector + sort
Count frequency                           unordered_map / map
Need ordered keys                         map
Need unique sorted values                 set
Need sorted duplicates                    multiset
Need smallest/largest repeatedly          priority_queue
Need nearest >= x / <= x                  set.lower_bound / upper_bound
Need undo / nested / next greater         stack
Need BFS / level order                    queue
Need both ends / window maximum           deque
Need O(1) cache recency movement          list + unordered_map
Need interval overlap offline             vector events + sort
Need subarray sum count                   prefix + unordered_map
Need dynamic median                       two heaps / two multisets
Need graph adjacency                      vector<vector<int>>
Need weighted graph adjacency             vector<vector<pair<int,int>>>
```

---

## One Picture: STL Machines

```text
                         STL MACHINE MAP

                              Problem
                                 |
                 What operation must be fast?
                                 |
      ----------------------------------------------------------------
      |              |              |              |                  |
  Index/Order     Lookup        Dynamic Order    Best Now          Flow
      |              |              |              |                  |
  vector/sort   unordered_map   set/multiset   priority_queue   stack/queue/deque
      |
  prefix/binary search
```

---

## Container Identity vs Operation Identity

Wrong:

```text
set is a container of unique values.
```

Better:

```text
set is a dynamic sorted structure that supports:
    insert
    erase
    lower_bound
    begin
    rbegin
```

Wrong:

```text
priority_queue is a heap.
```

Better:

```text
priority_queue answers:
    What is the current best candidate?
```

Wrong:

```text
deque stores values from both sides.
```

Better:

```text
deque maintains useful candidates while old candidates expire.
```

---

## Operation Compression

Every STL pattern can be compressed into one sentence.

```text
Frequency map:
    Count previous states.

Monotonic stack:
    Keep unresolved indices.

Monotonic deque:
    Keep useful window candidates.

Priority queue:
    Repeatedly extract best.

Set:
    Maintain sorted active values.

Multiset:
    Maintain sorted active values with duplicates.

Sweep line:
    Convert intervals into events.

Coordinate compression:
    Replace huge values by sorted ranks.

Offline sorting:
    Sort queries so pointer moves once.

Dijkstra heap:
    Always expand smallest known distance.
```

If you cannot compress the pattern into one sentence, you do not own it yet.

---

# 004. Constraint To Algorithm To STL

## Why Constraints Matter

Constraints tell you which complexity is possible.

If you ignore constraints, you may choose a beautiful STL pattern and still TLE.

You must read constraints before coding.

---

## Complexity Guess Table

```text
N <= 20
    O(2^N), bitmask, backtracking

N <= 100
    O(N^3) sometimes okay

N <= 500
    O(N^3) maybe okay in optimized C++

N <= 2,000
    O(N^2) okay

N <= 5,000
    O(N^2) maybe okay with simple operations

N <= 2e5
    O(N log N) or O(N)

N <= 1e6
    O(N) or O(N log N) with care

N >= 1e9
    O(log N), math, binary search, formulas
```

---

## Constraint → STL Mapping

```text
Need O(N)
    vector scan
    prefix sum
    two pointers
    monotonic stack
    monotonic deque
    queue BFS

Need O(N log N)
    sort
    set/map operations
    priority_queue
    sweep line
    multiset

Need average O(N)
    unordered_map
    unordered_set

Need O(log N) per query
    set/map lower_bound
    binary search on vector
    Fenwick/segment tree later
```

---

## Online vs Offline

This is one of the most important contest distinctions.

### Online

You must answer while updates happen.

```text
insert x
delete y
query current min
query nearest >= k
```

Use:

```text
set
multiset
map
priority_queue
deque
```

### Offline

You can read all data first.

```text
given all intervals
given all queries
given all values
answer later
```

Use:

```text
vector + sort
coordinate compression
sweep line
offline queries
```

---

## Online vs Offline Diagram

```text
ONLINE
input arrives step by step
must answer now

operation stream:
insert 5
query min
delete 5
query nearest

Need dynamic STL:
set / multiset / heap / map
```

```text
OFFLINE
see everything first
reorder safely

data:
intervals
queries
events

Need sorting:
vector + sort + pointer
```

---

## Example: Count Values <= X

If queries are independent:

```cpp
sort(a.begin(), a.end());
answer = upper_bound(a.begin(), a.end(), x) - a.begin();
```

If queries have updates:

```text
Need dynamic structure.
Use Fenwick / segment tree later.
```

---

## The Constraint Pipeline

```text
Read N and Q
      ↓
Compute expected operations
      ↓
Pick target complexity
      ↓
Decide online/offline
      ↓
Choose STL pattern
```

Example:

```text
N = 2e5, Q = 2e5
Need answer per query

O(NQ) impossible.
Need O((N+Q)logN) or O(NlogN + QlogN).
```

---

# 005. STL Decision Tree

## Master Decision Tree

```text
Need index access?
    -> vector / string

Need sort once?
    -> vector + sort

Need binary search after sorting?
    -> vector + lower_bound

Need frequency count?
    -> unordered_map / map

Need ordered traversal of keys?
    -> map

Need unique sorted values?
    -> set

Need duplicates sorted?
    -> multiset

Need nearest value >= x?
    -> set.lower_bound / multiset.lower_bound

Need repeatedly smallest/largest?
    -> priority_queue

Need BFS / first discovered first processed?
    -> queue

Need last opened / parser / next greater?
    -> stack

Need window max/min?
    -> deque

Need median under insert/delete?
    -> two multisets

Need top K?
    -> heap size K

Need interval overlap?
    -> sweep line

Need merge intervals?
    -> sort intervals

Need cache design?
    -> list + unordered_map
```

---

## Visual Decision Tree

```text
                         PROBLEM
                            |
                   What repeats many times?
                            |
        -------------------------------------------------
        |                  |              |             |
      Lookup             Order          Best          Window
        |                  |              |             |
 unordered_map        set/map        priority_queue   deque
        |                  |
 Frequency           nearest/lower_bound

        -------------------------------------------------
        |                  |              |             |
      Sequence           Boundary        Intervals     Graph
        |                  |              |             |
      vector             stack        sort/sweep      vector adjacency
```

---

## Decision Tree By Keywords

```text
"count"
"frequency"
"same"
"previous occurrence"
    -> unordered_map

"sorted"
"nearest"
"lower_bound"
"smallest >= x"
"largest <= x"
    -> set / multiset

"top"
"largest repeatedly"
"smallest repeatedly"
"minimum cost each step"
    -> priority_queue

"window"
"subarray of size k"
"maximum in each window"
    -> deque / multiset

"next greater"
"previous smaller"
"span"
"histogram"
    -> monotonic stack

"interval"
"overlap"
"meeting"
"active"
    -> sort + sweep line / heap

"median"
"middle value"
"dynamic kth"
    -> two heaps / two multisets

"shortest path"
"weighted graph"
    -> priority_queue Dijkstra

"unweighted shortest"
"minimum steps"
    -> queue BFS
```

---

# 006. How To Recognize Patterns

## Recognition Is The Real Skill

Syntax is easy.

Recognition is hard.

The gap between 1200 and 1900 is mostly:

```text
seeing the hidden pattern faster
```

---

## Pattern Recognition Method

When reading a problem, ask five questions:

```text
1. What is the repeated operation?
2. Is the data static or dynamic?
3. Do I need order?
4. Do I need previous state?
5. Can I sort/offline?
```

---

## Question 1: What Is Repeated?

If the problem repeatedly asks:

```text
find count
```

Think:

```text
map / unordered_map
```

If it repeatedly asks:

```text
find min/max
```

Think:

```text
heap / set / deque
```

If it repeatedly asks:

```text
nearest value
```

Think:

```text
set.lower_bound
```

If it repeatedly asks:

```text
range/window maximum
```

Think:

```text
deque / multiset
```

---

## Question 2: Static Or Dynamic?

Static:

```text
read all values once
no updates
```

Use:

```text
sort
binary search
prefix
vector
```

Dynamic:

```text
insert/delete/update
```

Use:

```text
set
multiset
map
heap
Fenwick later
```

---

## Question 3: Need Order?

No order:

```text
frequency
existence
same value
```

Use:

```text
unordered_map / unordered_set
```

Need order:

```text
smallest
largest
nearest
rank
sorted traversal
```

Use:

```text
set / map / multiset / vector+sort
```

---

## Question 4: Need Previous State?

Examples:

```text
previous same prefix
previous same remainder
previous occurrence
previous smaller
previous greater
```

Mappings:

```text
previous same value       -> unordered_map
previous same prefix      -> unordered_map
previous smaller/greater  -> stack
previous index            -> unordered_map<int,int>
```

---

## Question 5: Can I Sort?

Sorting creates structure.

Before designing complex logic, ask:

```text
Can sorting make the greedy obvious?
Can sorting let two pointers work?
Can sorting let lower_bound answer?
Can sorting turn intervals into events?
Can sorting allow offline queries?
```

If yes, `vector + sort` may be the core STL.

---

## Recognition Examples

### Example A

```text
Given array. Count pairs with same remainder modulo k.
```

Hidden operation:

```text
count previous same remainder
```

STL:

```text
unordered_map<int,int>
```

Pattern:

```text
frequency
```

---

### Example B

```text
For each element, find next greater element to the right.
```

Hidden operation:

```text
unresolved previous indices waiting for greater value
```

STL:

```text
stack
```

Pattern:

```text
monotonic stack
```

---

### Example C

```text
Given intervals, find maximum overlap.
```

Hidden operation:

```text
active count changes at endpoints
```

STL:

```text
vector<pair<int,int>> events + sort
```

Pattern:

```text
sweep line
```

---

### Example D

```text
Maintain numbers. Query median after each insertion.
```

Hidden operation:

```text
balanced lower half and upper half
```

STL:

```text
two heaps / two multisets
```

Pattern:

```text
median maintenance
```

---

# 007. How To Think During Contest

## Contest Thinking Pipeline

```text
Read problem
      ↓
Ignore implementation first
      ↓
Extract input/output and constraints
      ↓
Find repeated operation
      ↓
Guess complexity
      ↓
Try brute force
      ↓
Find bottleneck
      ↓
Replace bottleneck with STL pattern
      ↓
Dry run
      ↓
Code
      ↓
Submit
```

---

## The 3-Pass Reading Method

### Pass 1: Story Removal

Remove story and keep facts.

```text
Monsters, spells, coins, friends
```

becomes:

```text
array
queries
updates
constraints
objective
```

---

### Pass 2: Operation Extraction

Look for verbs:

```text
count
find
choose
remove
insert
merge
maximize
minimize
nearest
first
last
```

Each verb points to operation.

---

### Pass 3: Constraint Filter

Use constraints to eliminate bad ideas.

```text
N = 2e5
O(N^2) dead

Q = 2e5
Per query O(N) dead

Values = 1e9
Need compression / map / sorting
```

---

## Contest Scratchpad Template

Before code, write this mentally or in comments:

```cpp
/*
N =
Q =
Need =
Brute =
Bottleneck =
Pattern =
STL =
Complexity =
Edge cases =
*/
```

Example:

```cpp
/*
N=2e5
Need max in every window size k
Brute O(NK)
Bottleneck scanning max
Pattern monotonic deque
STL deque<int> indices
Complexity O(N)
Edge k=1, duplicates
*/
```

---

## When You Are Stuck

Ask:

```text
Can I sort?
Can I use prefix?
Can I use frequency?
Can I use binary search?
Can I maintain active set?
Can I process events?
Can I process offline?
Can I convert to graph?
Can I use monotonicity?
```

This checklist often unlocks Div2 C/D.

---

## Implementation Order

Do not start with full code.

Use this:

```text
1. Read input.
2. Build data structure.
3. Write core operation.
4. Add answer update.
5. Add output.
6. Add edge cases.
```

For complex STL:

```text
Write helper functions:
    add()
    remove()
    rebalance()
    clean()
```

Example for two multisets:

```cpp
void add(int x);
void removeOne(int x);
void rebalance();
```

This reduces bugs.

---

# 008. Contest Debugging Checklist

## Before Submit Checklist

```text
[ ] Did I read constraints correctly?
[ ] Is complexity safe?
[ ] Did I sort before binary search?
[ ] Did I use long long for sums/products?
[ ] Did I handle empty containers before top/front/back?
[ ] Did I handle duplicates?
[ ] Did I erase one copy from multiset, not all copies?
[ ] Did I use set.lower_bound, not std::lower_bound on set?
[ ] Is comparator strict?
[ ] Did I avoid iterator invalidation?
[ ] Did I clean stale heap entries?
[ ] Did I handle 0-index vs 1-index?
[ ] Did I handle negative modulo?
[ ] Did I output newline?
[ ] Did I test smallest case?
```

---

## Debug By Symptom

### Wrong Answer

Check:

```text
indexing
duplicates
edge cases
overflow
wrong inequality
wrong tie-break
forgot sort
forgot initialization
```

### TLE

Check:

```text
nested loops
sort inside loop
vector erase from front
std::lower_bound on set/map
map where unordered_map needed
too many substr copies
heap stale entries not removed
```

### Runtime Error

Check:

```text
top/front/back on empty
decrement begin()
dereference end()
out-of-bounds vector
erase invalid iterator
recursion stack overflow
```

### MLE

Check:

```text
vector<vector<int>>(huge)
map with too many states
storing strings instead of ids
copying large vectors
unbounded heap due to lazy deletion
```

---

## Minimal Edge Tests

Always test:

```text
N = 1
all equal
strictly increasing
strictly decreasing
contains negative
contains zero
large values
duplicates
empty result
k = 1
k = n
```

---

## STL-Specific Debug Tests

### set lower_bound

```text
x smaller than all
x equal existing
x between values
x greater than all
```

### multiset erase

```text
erase one duplicate
erase missing value
erase last value
```

### monotonic stack

```text
increasing array
decreasing array
all equal
```

### deque window

```text
k=1
k=n
duplicates
old max expires
```

### heap lazy deletion

```text
deleted value at top
deleted value not at top
multiple deleted copies
```

---

# 009. Common STL Mistakes

## Mistake 1: `std::lower_bound` On Set

Bad:

```cpp
auto it = lower_bound(s.begin(), s.end(), x);
```

This is O(N) because set iterators are not random access.

Good:

```cpp
auto it = s.lower_bound(x);
```

---

## Mistake 2: Erasing All Copies From Multiset

Bad:

```cpp
ms.erase(x);
```

This removes all copies of `x`.

Good:

```cpp
auto it = ms.find(x);
if (it != ms.end()) ms.erase(it);
```

---

## Mistake 3: Dereferencing `end()`

Bad:

```cpp
auto it = s.lower_bound(x);
cout << *it;
```

Good:

```cpp
auto it = s.lower_bound(x);
if (it != s.end()) cout << *it;
```

---

## Mistake 4: Decrementing `begin()`

Bad:

```cpp
auto it = s.lower_bound(x);
--it;
```

Good:

```cpp
auto it = s.lower_bound(x);
if (it != s.begin()) {
    --it;
}
```

---

## Mistake 5: Non-Strict Comparator

Bad:

```cpp
sort(a.begin(), a.end(), [](int x, int y) {
    return x <= y;
});
```

Good:

```cpp
sort(a.begin(), a.end(), [](int x, int y) {
    return x < y;
});
```

Comparator must answer:

```text
Should x come before y?
```

For equal elements, answer must be false.

---

## Mistake 6: Overflow In Comparator

Bad:

```cpp
return a.x - b.x < 0;
```

Good:

```cpp
return a.x < b.x;
```

---

## Mistake 7: `operator[]` Creates Map Key

```cpp
if (mp[x]) {
    ...
}
```

This creates key `x` if missing.

Use:

```cpp
if (mp.count(x)) {
    ...
}
```

when checking existence.

---

## Mistake 8: Vector Erase In Loop

Bad:

```cpp
while (!v.empty()) {
    v.erase(v.begin());
}
```

This is O(N^2).

Use:

```cpp
queue<int> q;
deque<int> dq;
```

or maintain index pointer.

---

## Mistake 9: Heap Cannot Delete Arbitrarily

`priority_queue` cannot erase a random old value.

Use lazy deletion:

```text
mark deleted
clean when deleted value reaches top
```

---

## Mistake 10: String `substr` In Loop

`substr` copies.

Bad:

```cpp
for (...) {
    string t = s.substr(l, len);
}
```

May become O(N^2).

Use indices, rolling hash later, or compare carefully.

---

# 010. Time Complexity CheatSheet

## STL Complexity

```text
vector
    index                       O(1)
    push_back                   amortized O(1)
    insert/erase middle/front   O(N)
    sort                        O(N log N)

string
    index                       O(1)
    push_back                   amortized O(1)
    substr                      O(length)

deque
    push_front/back             O(1)
    pop_front/back              O(1)
    random access               O(1)

stack
    push/pop/top                O(1)

queue
    push/pop/front              O(1)

priority_queue
    top                         O(1)
    push/pop                    O(log N)

set/map/multiset
    insert/find/erase           O(log N)
    lower_bound/upper_bound     O(log N)

unordered_map/unordered_set
    average insert/find/erase   O(1)
    worst case                  O(N)

list
    insert/erase by iterator    O(1)
    search                      O(N)
```

---

## Pattern Complexity

```text
frequency count                 O(N)
prefix sum                      O(N)
sort + scan                     O(N log N)
two pointers                    O(N) after sorting if needed
binary search per query         O(log N)
monotonic stack                 O(N)
monotonic deque                 O(N)
sweep line                      O(N log N)
heap top-k                      O(N log K)
Dijkstra                        O((V+E) log V)
two multiset median             O(log N) per operation
LRU                             O(1)
```

---

## Complexity Smell Table

```text
N = 2e5 and O(N^2)
    TLE

Q = 2e5 and each query scans array
    TLE

sort inside loop
    usually TLE

erase from vector front repeatedly
    TLE

nested map operations with large N
    maybe TLE

unordered_map with adversarial keys
    possible TLE; use custom hash if needed
```

---

## Quick Contest Rule

```text
If N <= 2e5:
    Try to reach O(N log N) or O(N).

If Q <= 2e5:
    Per query should usually be O(log N) or O(1).
```

---

# 011. STL Memory Model

## Why Memory Model Matters

Big-O does not tell the full story.

Two algorithms can both be O(N), but one is much faster because of cache locality.

---

## Vector Memory

```text
vector<int> v

memory:
[10][20][30][40][50]
 0   1   2   3   4
```

Vector is contiguous.

Advantages:

```text
fast indexing
cache-friendly
best default container
works well with sort/binary search
```

Disadvantages:

```text
insert/erase at front or middle is slow
reallocation may invalidate iterators
```

---

## List Memory

```text
[10] -> [20] -> [30] -> [40]
```

Each node is separate.

Advantages:

```text
O(1) erase/move if iterator known
good for LRU
```

Disadvantages:

```text
poor cache locality
no random access
search is O(N)
usually bad in CP unless truly needed
```

Important CM lesson:

```text
list is not automatically faster because erase is O(1).
If you need to find the element first, search is O(N).
```

---

## Set/Map Memory

```text
        20
       /  \
     10    30
```

Usually tree-based.

Advantages:

```text
ordered
lower_bound
min/max
dynamic insert/delete
```

Disadvantages:

```text
O(log N)
pointer-heavy
slower than vector when offline sorting is enough
```

---

## Unordered Map Memory

```text
buckets:
0 -> [key,value] -> [key,value]
1 -> empty
2 -> [key,value]
3 -> [key,value]
```

Advantages:

```text
average O(1)
great for frequency/prefix states
```

Disadvantages:

```text
no order
worst-case collisions
higher memory
operator[] creates keys
```

---

## Deque Memory

Deque is not one big array like vector.

It is usually blocks of memory.

```text
block1: [ ][ ][ ]
block2: [ ][ ][ ]
block3: [ ][ ][ ]
```

Advantages:

```text
push/pop both ends O(1)
random access O(1)
great for monotonic window
```

---

## Priority Queue Memory

Usually backed by vector heap.

```text
array heap:
[100][50][80][10][20]

tree view:
        100
       /   \
     50     80
    /  \
  10   20
```

It gives fast top, not sorted order.

Important:

```text
priority_queue is not a sorted container.
Only top is guaranteed.
```

---

## Cache Friendliness Rough Order

```text
Fastest usually:
vector / array

then:
deque

then:
priority_queue backed by vector

then:
unordered_map

then:
set/map/multiset

then:
list
```

Use `vector + sort` when possible before choosing tree structures.

---

# 012. STL Recognition Flowchart

## Full Flowchart

```text
START
  |
  v
Do I need to access by index?
  |-- yes --> vector/string
  |
  no
  |
  v
Can I process all data offline?
  |-- yes --> Can sorting help?
  |             |-- yes --> vector + sort
  |             |-- no  --> prefix / frequency / binary search
  |
  no
  |
  v
Do I need fast lookup/count?
  |-- yes --> unordered_map / unordered_set
  |
  no
  |
  v
Do I need ordered search / nearest?
  |-- yes --> set / map / multiset
  |
  no
  |
  v
Do I need current best repeatedly?
  |-- yes --> priority_queue
  |
  no
  |
  v
Do I need last unresolved boundary?
  |-- yes --> stack
  |
  no
  |
  v
Do I need window max/min?
  |-- yes --> deque / multiset
  |
  no
  |
  v
Do I need BFS wave?
  |-- yes --> queue
  |
  no
  |
  v
Need deeper algorithm:
    DP / graph / segment tree / math
```

---

## Mini Flowchart: Subarray Problems

```text
Subarray problem
  |
  v
Need sum over range?
  -> prefix sum

Need count subarrays with sum K?
  -> prefix + unordered_map

All numbers positive and need window condition?
  -> sliding window

Need max/min in every fixed window?
  -> monotonic deque

Need next greater boundary?
  -> monotonic stack

Need median in window?
  -> two multisets
```

---

## Mini Flowchart: Interval Problems

```text
Interval problem
  |
  v
Need merge?
  -> sort by start

Need max overlap?
  -> sweep line

Need minimum rooms?
  -> sort + min heap

Need active intervals online?
  -> set/multiset/map

Need range add offline?
  -> difference array / events
```

---

## Mini Flowchart: Query Problems

```text
Queries
  |
  v
Can reorder queries?
  -> offline sort

Need dynamic insert/delete?
  -> set/multiset/map

Need frequency states?
  -> unordered_map

Need range sum?
  -> prefix if static
  -> Fenwick/segment tree later if dynamic

Need nearest value?
  -> set.lower_bound
```

---

# 013. Container Selection Guide

## Vector

Use when:

```text
input array
sorting
prefix sum
binary search
DP
graph adjacency
coordinate compression
events
intervals
```

Avoid when:

```text
frequent erase front
frequent insert middle
dynamic ordered search
```

---

## String

Use when:

```text
character scan
frequency
palindrome
anagram
parsing
substring/window
```

Be careful:

```text
substr copies
getline after cin
char indexing
```

---

## Set

Use when:

```text
unique sorted active values
lower_bound
nearest >= x
largest <= x
dynamic min/max
```

Avoid when:

```text
duplicates matter
frequency matters
offline vector+sort is enough
```

---

## Multiset

Use when:

```text
duplicates matter
window min/max with deletion
dynamic median
ordered multiset of active values
```

Be careful:

```text
erase(value) deletes all copies
```

---

## Map

Use when:

```text
ordered keys
need lower_bound by key
frequency with sorted traversal
coordinate/event map
```

Avoid when:

```text
only need fast count and no order
```

Then use `unordered_map`.

---

## Unordered Map

Use when:

```text
frequency
prefix states
two sum
count previous values
memo sparse states
```

Be careful:

```text
operator[] creates keys
worst-case collisions
no order
```

---

## Priority Queue

Use when:

```text
current min/max repeatedly
top K
Dijkstra
scheduling
merge k lists
greedy pick best
```

Avoid when:

```text
need arbitrary delete without lazy deletion
need full sorted traversal
need update key directly
```

---

## Stack

Use when:

```text
valid parentheses
nested parsing
next greater/smaller
previous greater/smaller
histogram
DFS iterative
```

Core idea:

```text
last unresolved candidate
```

---

## Queue

Use when:

```text
BFS
level order
multi-source expansion
simulation FIFO
```

Core idea:

```text
first discovered, first processed
```

---

## Deque

Use when:

```text
push/pop both ends
sliding window max/min
0-1 BFS
two-ended simulation
```

Core idea:

```text
window candidates + expiration
```

---

## List

Use when:

```text
LRU cache
need O(1) move/erase by known iterator
```

Avoid in most CP array problems.

---

# 014. When NOT To Use STL

## Do Not Use Set If Sorting Once Is Enough

If data is static:

```text
read all numbers
answer sorted queries
```

Prefer:

```cpp
vector<int> a;
sort(a.begin(), a.end());
```

over:

```cpp
set<int> s;
```

Why?

```text
vector is faster
less memory
better cache
simpler
```

---

## Do Not Use Map If Unordered Map Is Enough

If you only need counts:

```cpp
unordered_map<int,int> freq;
```

Use `map` only if:

```text
ordered traversal
lower_bound
min/max key
```

---

## Do Not Use Priority Queue If You Need Arbitrary Deletion

If you need:

```text
insert x
delete x
query max
```

Use:

```text
multiset
```

or heap with lazy deletion if appropriate.

---

## Do Not Use Vector Erase For Queue Behavior

Bad:

```cpp
v.erase(v.begin());
```

Good:

```cpp
queue<int> q;
deque<int> dq;
```

or:

```cpp
int head = 0;
while (head < v.size()) {
    process(v[head++]);
}
```

---

## Do Not Use List Just Because Erase Is O(1)

If you do not already have iterator, finding node is O(N).

Bad for:

```text
normal traversal
random access
sorting-like problems
```

Good for:

```text
LRU cache with map to iterator
```

---

## Do Not Use `substr` Repeatedly In Large Loops

Bad:

```cpp
for (int i = 0; i < n; i++) {
    string t = s.substr(i, k);
}
```

This copies O(k) each time.

Use:

```text
indices
hashing
sliding window
```

---

## Do Not Over-Engineer Div2 A/B

Sometimes the correct answer is:

```text
sort
count
min/max
simple map
```

Do not force advanced STL.

CM thinking includes choosing simple tools when enough.

---

## The Simplicity Rule

```text
If vector + sort solves it, prefer vector + sort.
If unordered_map solves it, do not use map.
If stack solves it, do not use segment tree.
If prefix solves it, do not use Fenwick.
```

Use the simplest structure that satisfies constraints.

---

# 015. CM Mindset

## What Candidate Master Actually Means

Candidate Master is not about knowing every data structure.

It is about seeing patterns quickly and implementing them with low bug rate.

You need three skills:

```text
1. Recognition speed
2. Complexity discipline
3. Implementation reliability
```

---

## The CM STL Formula

```text
Problem Statement
      ↓
Operation Extraction
      ↓
Constraint Filter
      ↓
Pattern Recognition
      ↓
STL Selection
      ↓
Template Recall
      ↓
Dry Run
      ↓
Bug-Free Implementation
      ↓
Accepted
```

---

## The 5-Second Recognition Goal

For common forms, your brain should respond instantly.

```text
next greater
    -> monotonic stack

window maximum
    -> monotonic deque

count subarray sum K
    -> prefix + map

active intervals
    -> sweep line

dynamic nearest
    -> set.lower_bound

top K
    -> priority_queue

dynamic median
    -> two heaps / two multisets

merge intervals
    -> sort

Dijkstra
    -> priority_queue

LRU
    -> list + unordered_map
```

---

## The 30-Second Plan Goal

Before coding, you should be able to say:

```text
N is 2e5.
Need O(N log N).
Brute force O(N^2) fails.
The repeated operation is nearest active value.
Use set.lower_bound.
Handle begin/end.
Complexity O(N log N).
```

If you cannot explain the plan in 30 seconds, do not code yet.

---

## CM Debugging Mindset

Do not debug randomly.

Debug by category:

```text
WA:
    logic, edge case, duplicates, overflow, inequality

TLE:
    complexity, hidden O(N), bad container, repeated sort

RE:
    invalid iterator, empty access, out of bounds

MLE:
    too much storage, accidental copies
```

---

## The Mistake Journal

After every contest, record:

```text
Problem:
Pattern missed:
Correct STL:
Why I missed it:
Bug:
Fix:
Recognition sentence:
```

Example:

```text
Problem: window max
Pattern missed: monotonic deque
Why missed: tried multiset and got TLE due to bug
Recognition sentence: fixed-size window max -> deque indices
```

This converts failure into rating.

---

## Rating Ladder Mindset

```text
800–1000:
    Implementation + basic STL

1100–1400:
    Sort, map, set, binary search, two pointers

1500–1800:
    Monotonic stack/deque, heap, prefix map, sweep line

1900–2200:
    Lazy deletion, two multisets, offline queries, graph STL, optimization
```

Your job is not to read more.

Your job is to convert each rating band into automatic recognition.

---

## The One Big Picture

```text
                         CODEFORCES CM STL MINDSET

                                  Problem
                                     |
                                     v
                             Remove Story
                                     |
                                     v
                           Extract Operation
                                     |
              ------------------------------------------------
              |                   |              |             |
          Count/Lookup          Order        Best Now       Window
              |                   |              |             |
        unordered_map         set/map      priority_queue    deque
              |
              v

       Previous State?
              |
              +-- prefix map
              +-- frequency
              +-- occurrence index

              ------------------------------------------------
              |                   |              |             |
          Boundary             Interval        Graph         Design
              |                   |              |             |
            stack              sweep line       queue/heap    list+map

                                     |
                                     v
                              Check Constraints
                                     |
                                     v
                              Write Template
                                     |
                                     v
                                Dry Run
                                     |
                                     v
                               Submit AC
```

---

# Final Foundation Cheat Sheet

## Ask These Questions First

```text
1. What operation repeats?
2. Is data static or dynamic?
3. Do I need order?
4. Do I need count/frequency?
5. Do I need nearest?
6. Do I need min/max repeatedly?
7. Do I need window state?
8. Can I sort?
9. Can I process offline?
10. What complexity is allowed?
```

---

## Operation → STL

```text
random access                 vector
characters                    string
frequency                     unordered_map
ordered keys                  map
unique sorted                 set
duplicates sorted             multiset
nearest value                 set.lower_bound
current best                  priority_queue
BFS                           queue
next greater                  stack
window max/min                deque
interval overlap              sweep line
subarray count                prefix + unordered_map
dynamic median                two heaps / two multisets
LRU                           list + unordered_map
```

---

## Constraint → Complexity

```text
N <= 20       exponential possible
N <= 2e3      O(N^2)
N <= 2e5      O(N log N) / O(N)
N >= 1e9      O(log N) / math
```

---

## Pre-Submit Mini Checklist

```text
[ ] complexity safe
[ ] sorted before binary search
[ ] lower_bound correct container
[ ] long long used
[ ] empty checks done
[ ] duplicates handled
[ ] iterator safe
[ ] comparator strict
[ ] stale heap cleaned
[ ] edge cases tested
```

---

# End Of Foundation

This foundation is the base of the full `MiniSTL_ABCD_Ladder.md`.

Next sections should build from this mental model:

```text
PART_A_BEGINNER_800_1000
PART_B_INTERMEDIATE_1100_1400
PART_C_ADVANCED_1500_1800
PART_D_CM_1900_2200
PATTERN_RECOGNITION_ENGINE
BUG_ATLAS
SPEED_ENGINE
099_Ultimate_STL_CheatSheet
```

Remember:

```text
STL mastery = operation recognition + correct container + safe implementation.
```
