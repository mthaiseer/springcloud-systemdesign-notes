# ICPSC Season 1 — Important Techniques & Problems

This note collects the important problems and reusable techniques discussed from the AlgoZenith ICPSC Season 1 lessons.

The goal is **not to memorize code**. For every problem, revise this chain:

```text
Problem statement
      ↓
Brute-force thought
      ↓
Key observation
      ↓
Mental model
      ↓
Optimized technique
      ↓
Dry run
      ↓
Code
      ↓
Complexity
```

---

# Problem 1 — Maximum Customers in a Restaurant

## Problem

You are given the arrival and leaving times of customers in a restaurant.

Find the **maximum number of customers present at the same time**.

Example:

```text
arrival = [1, 2, 10, 5, 5]
leaving = [4, 5, 12, 9, 12]
```

---

## Observation

Every customer creates two events:

```text
arrival → +1
leaving → -1
```

So instead of thinking customer-by-customer, think **time-event by time-event**.

This converts an interval problem into a running-count problem.

---

## Mental Model

```text
Customer interval:

arrival ---------------- leaving
    +1                      -1
```

For all customers:

```text
Intervals
   ↓
Convert to events
   ↓
Sort by time
   ↓
Running prefix count
   ↓
Maximum running count
```

### Trigger

When the problem asks:

```text
How many things are active simultaneously?
```

Think:

```text
START → +1
END   → -1
```

---

## Build the Event Map

For the example:

```text
Customer 1: 1 → 4
Customer 2: 2 → 5
Customer 3: 10 → 12
Customer 4: 5 → 9
Customer 5: 5 → 12
```

Events:

```text
time 1   → +1
time 4   → -1

time 2   → +1
time 5   → -1

time 10  → +1
time 12  → -1

time 5   → +1
time 9   → -1

time 5   → +1
time 12  → -1
```

Because the map combines equal times:

```text
time       change
-----------------
1           +1
2           +1
4           -1
5           +1
9           -1
10          +1
12          -2
```

At time `5`:

```text
-1 + 1 + 1 = +1
```

---

## ASCII Timeline

```text
time →

1      2      4      5        9      10       12
|      |      |      |        |       |        |
+1     +1     -1     +1       -1      +1       -2
```

---

## Dry Run

Start:

```text
current = 0
maximum = 0
```

Now sweep in sorted time order:

```text
Time    Change    Current    Maximum
------------------------------------
1        +1          1          1
2        +1          2          2
4        -1          1          2
5        +1          2          2
9        -1          1          2
10       +1          2          2
12       -2          0          2
```

Answer:

```text
2
```

---

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    vector<int> arrival = {1, 2, 10, 5, 5};
    vector<int> leaving = {4, 5, 12, 9, 12};

    map<int, int> events;

    for (int i = 0; i < (int)arrival.size(); i++) {
        events[arrival[i]]++;
        events[leaving[i]]--;
    }

    int currentCustomers = 0;
    int maxCustomers = 0;

    for (auto [time, change] : events) {
        currentCustomers += change;
        maxCustomers = max(maxCustomers, currentCustomers);
    }

    cout << maxCustomers << '\n';
}
```

---

## Complexity

If there are `n` customers:

```text
2n map events
```

Each map operation:

```text
O(log n)
```

Total:

```text
O(n log n)
```

Space:

```text
O(n)
```

---

## One-Line Revision

```text
Maximum simultaneous intervals
→ start +1, end -1
→ sort/map events
→ running count
→ take maximum
```

---

# Problem 2 — Nearest Position on the Left With Value <= Current

## Problem

Given an integer array, for every position `i`, find the **nearest position to its left** having a value smaller than or equal to `a[i]`.

Example:

```text
arr = [1, 5, 2, 10]

index:  0  1  2   3
value:  1  5  2  10
```

Expected answer:

```text
[-1, 0, 0, 2]
```

Meaning:

```text
i = 0 → none
i = 1 → index 0, because 1 <= 5
i = 2 → index 0, because 1 <= 2
i = 3 → index 2, because 2 <= 10
```

---

## Core Observation

The condition contains **two dimensions**:

```text
a[j] <= a[i]    AND    j < i
    │                      │
 VALUE condition      POSITION condition
```

Use different tools for the two dimensions:

```text
VALUE condition    → SORT
POSITION condition → ORDERED SET
```

---

## Mental Model

### "Unlock by value, search by position"

Store every element as:

```text
(value, original_index)
```

Example:

```text
(1,0)
(5,1)
(2,2)
(10,3)
```

Sort by value:

```text
(1,0)
(2,2)
(5,1)
(10,3)
```

Now when processing `(value,index)`, all previously processed values satisfy:

```text
processed_value <= current_value
```

So they are all **eligible by value**.

Now maintain their original indices in an ordered set.

The set answers:

```text
Which eligible index is nearest to the left?
```

---

## Lock This Sentence

```text
SORT answers: WHO is eligible?
SET answers:  WHERE is nearest?
```

---

## ASCII Model

Original:

```text
index:    0     1     2      3
value:    1     5     2     10
```

Sorted by value:

```text
(value,index)

(1,0)
(2,2)
(5,1)
(10,3)
```

Think of processed indices as "unlocked":

```text
● = value is small enough to be used
X = current index
```

---

## Dry Run

### Process `(1,0)`

```text
0     1     2     3
X     .     .     .
```

No index on the left.

```text
ans[0] = -1
```

---

### Process `(2,2)`

Eligible positions:

```text
0     1     2     3
●     .     X     .
```

Nearest eligible position `< 2`:

```text
0
```

So:

```text
ans[2] = 0
```

---

### Process `(5,1)`

Eligible positions:

```text
0     1     2     3
●     X     ●     .
```

Nearest eligible position `< 1`:

```text
0
```

So:

```text
ans[1] = 0
```

---

### Process `(10,3)`

Eligible positions:

```text
0     1     2     3
●     ●     ●     X
```

Nearest eligible position `< 3`:

```text
2
```

So:

```text
ans[3] = 2
```

Final:

```text
[-1, 0, 0, 2]
```

---

## How `lower_bound` Helps

Suppose:

```text
positions = {0, 1, 2, 3}
current index = 3
```

Use:

```cpp
auto it = positions.lower_bound(3);
```

This gives the first index:

```text
>= 3
```

So:

```text
0   1   2   3
        ↑   ↑
      prev  lower_bound
```

Move one step backward:

```cpp
--it;
```

Now we get:

```text
2
```

which is the nearest index to the left.

---

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    vector<int> arr = {1, 5, 2, 10};

    int n = arr.size();

    vector<pair<int,int>> elements;

    for (int i = 0; i < n; i++) {
        elements.push_back({arr[i], i});
    }

    sort(elements.begin(), elements.end());

    vector<int> ans(n, -1);

    set<int> positions;

    for (auto [value, index] : elements) {

        positions.insert(index);

        auto it = positions.lower_bound(index);

        if (it != positions.begin()) {
            --it;
            ans[index] = *it;
        }
    }

    for (int x : ans) {
        cout << x << " ";
    }

    return 0;
}
```

---

## Complexity

Sorting:

```text
O(n log n)
```

For every element:

```text
insert      O(log n)
lower_bound O(log n)
```

Overall:

```text
O(n log n)
```

Space:

```text
O(n)
```

---

## Pattern Recognition

When a problem contains:

```text
value condition
+
original position condition
```

consider:

```text
(value,index)
     ↓
sort by value
     ↓
maintain ordered original indices
     ↓
binary search / lower_bound
```

---

## One-Line Revision

```text
Sort by value to unlock eligible elements,
then use ordered indices to find the nearest position.
```

---

# Problem 3 — Longest Sequence of Unique Successive Songs

## Problem

You are given a playlist containing `n` songs.

Find the longest sequence of **successive/contiguous songs** where every song is unique.

Example:

```text
songs = [1, 2, 1, 3, 2, 7, 4]
```

Answer:

```text
5
```

because:

```text
[1, 3, 2, 7, 4]
```

contains five unique consecutive songs.

---

## Trigger

The important words are:

```text
LONGEST
+
CONTIGUOUS / SUCCESSIVE
+
DISTINCT / UNIQUE
```

That should suggest:

```text
SLIDING WINDOW
```

For this version, use:

```text
map<song, last_index>
```

---

## Mental Model

Maintain a valid window:

```text
L ---------------- R
```

Every song inside the window must be unique.

Store:

```text
song → last position where it appeared
```

When the right pointer sees a duplicate:

```text
jump left directly past the old occurrence
```

instead of removing elements one by one.

---

## Lock This Chain

```text
Longest unique contiguous segment
          ↓
Sliding window
          ↓
map stores last position
          ↓
duplicate?
          ↓
jump L past previous occurrence
```

---

## Example

```text
songs = [1, 2, 1, 3, 2, 7, 4]
index    0  1  2  3  4  5  6
```

---

## Dry Run

Start:

```text
left = 0
maxLen = 0
lastSeen = {}
```

### right = 0, song = 1

Never seen before.

```text
window = [1]

lastSeen:
1 → 0

length = 1
maxLen = 1
```

---

### right = 1, song = 2

Never seen before.

```text
window = [1 2]

lastSeen:
1 → 0
2 → 1

length = 2
maxLen = 2
```

---

### right = 2, song = 1

Duplicate:

```text
1   2   1
↑       ↑
old     current
```

Previous `1`:

```text
lastSeen[1] = 0
```

Jump:

```text
left = 0 + 1 = 1
```

Now:

```text
1 [2 1]
   L R
```

Update:

```text
lastSeen[1] = 2
```

Length:

```text
2 - 1 + 1 = 2
```

---

### right = 3, song = 3

```text
1 [2 1 3]
   L   R
```

Length:

```text
3
```

Maximum becomes:

```text
3
```

---

### right = 4, song = 2

Previous `2` was at index `1`.

```text
left = 1 + 1 = 2
```

Now:

```text
1 2 [1 3 2]
     L   R
```

Length:

```text
3
```

---

### right = 5, song = 7

```text
1 2 [1 3 2 7]
     L     R
```

Length:

```text
4
```

---

### right = 6, song = 4

```text
1 2 [1 3 2 7 4]
     L       R
```

Length:

```text
5
```

Answer:

```text
5
```

---

## Important Condition

A previous occurrence matters only if it is **inside the current window**.

So use:

```cpp
if (lastSeen.count(song) && lastSeen[song] >= left)
```

Then:

```cpp
left = lastSeen[song] + 1;
```

This prevents moving `left` backward.

---

## C++ Code Using `map`

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    vector<int> songs = {1, 2, 1, 3, 2, 7, 4};

    map<int, int> lastSeen;

    int left = 0;
    int maxLen = 0;

    for (int right = 0; right < (int)songs.size(); right++) {

        int song = songs[right];

        if (lastSeen.count(song) &&
            lastSeen[song] >= left) {

            left = lastSeen[song] + 1;
        }

        lastSeen[song] = right;

        int currentLen = right - left + 1;

        maxLen = max(maxLen, currentLen);
    }

    cout << maxLen << '\n';
}
```

---

## Complexity

Using `map`:

```text
lookup/update = O(log n)
```

Overall:

```text
O(n log n)
```

Space:

```text
O(n)
```

With `unordered_map`, average time can be closer to:

```text
O(n)
```

---

## One-Line Revision

```text
Longest unique contiguous range
→ keep lastSeen[value]
→ duplicate inside window
→ left = oldIndex + 1
```

---

# Problem 4 — Maximum Number of Non-Overlapping Jobs

## Problem

You are given `N` jobs.

Each job has:

```text
start time
end time
```

Find the **maximum number of jobs that can be completed without overlapping**.

This is the classic:

```text
Activity Selection / Interval Scheduling
```

---

## Key Observation

To maximize the number of future jobs, choose the job that:

```text
FINISHES EARLIEST
```

Why?

Because it frees the timeline as early as possible.

---

## Mental Model

```text
Maximum number of non-overlapping jobs
                 ↓
Need maximum room for future jobs
                 ↓
Become free as early as possible
                 ↓
Choose minimum END time
                 ↓
Sort by end time
```

---

## Example

Jobs:

```text
A = (1, 4)
B = (3, 5)
C = (0, 6)
D = (5, 7)
E = (3, 9)
F = (5, 9)
G = (6, 10)
H = (8, 11)
```

Sort by end time:

```text
(1,4)
(3,5)
(0,6)
(5,7)
(3,9)
(5,9)
(6,10)
(8,11)
```

---

## Greedy Rule

Maintain:

```text
lastEnd
```

For every job in increasing end-time order:

```text
if start >= lastEnd:
    select it
    lastEnd = end
```

---

## Dry Run

Start:

```text
lastEnd = -∞
count = 0
```

### Job `(1,4)`

```text
1 >= -∞
```

Select.

```text
count = 1
lastEnd = 4
```

---

### Job `(3,5)`

```text
3 < 4
```

Overlap.

Skip.

---

### Job `(0,6)`

```text
0 < 4
```

Skip.

---

### Job `(5,7)`

```text
5 >= 4
```

Select.

```text
count = 2
lastEnd = 7
```

---

### Jobs ending at 9 or 10

Their starts are `< 7`, so skip.

---

### Job `(8,11)`

```text
8 >= 7
```

Select.

```text
count = 3
```

Answer:

```text
3
```

---

# Why Earliest Ending Time Is Correct

This is proved using an **exchange argument**.

Suppose an optimal solution chooses job `O` first.

Our greedy algorithm chooses `G`, the job with the earliest ending time.

Because `G` is earliest-finishing:

```text
end(G) <= end(O)
```

Visual:

```text
Optimal starts with O:

O
|--------------|
               |---| |---| |---|
               future jobs


Greedy starts with G:

G
|-------|
        ........FREE SPACE........
               |---| |---| |---|
               same future jobs
```

Every job that can come after `O` can also come after `G`, because `G` finishes no later.

Therefore we can replace:

```text
O → X → Y → Z
```

with:

```text
G → X → Y → Z
```

without reducing the number of selected jobs.

So there exists an optimal solution starting with `G`.

Then repeat the same argument for the remaining jobs.

---

## Why Not Sort by Start Time?

Counterexample:

```text
A: [1 ---------------------------- 100]

B:    [2--3]
C:          [4--5]
D:                [6--7]
E:                      [8--9]
```

Choosing earliest start gives:

```text
A only
```

But earliest finish lets us choose:

```text
B → C → D → E
```

So:

```text
Earliest start    ✗
Shortest duration ✗
Earliest finish   ✓
```

---

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    vector<pair<int,int>> jobs = {
        {1,4},
        {3,5},
        {0,6},
        {5,7},
        {3,9},
        {5,9},
        {6,10},
        {8,11}
    };

    sort(jobs.begin(), jobs.end(),
         [](const auto &a, const auto &b) {
             return a.second < b.second;
         });

    int count = 0;
    int lastEnd = INT_MIN;

    for (auto [start, end] : jobs) {

        if (start >= lastEnd) {
            count++;
            lastEnd = end;
        }
    }

    cout << count << '\n';
}
```

---

## Complexity

Sorting:

```text
O(n log n)
```

Scan:

```text
O(n)
```

Total:

```text
O(n log n)
```

Space:

```text
O(1)
```

excluding the input container.

---

## One-Line Proof

```text
Replace the first job of any optimal solution
with the earliest-finishing compatible job.

It finishes no later,
so all future jobs still fit.
```

---

## One-Line Revision

```text
Maximum number of non-overlapping intervals
→ sort by end time
→ greedily select earliest finishing compatible job
```

---

# Quick Revision Index

## 1. Maximum Simultaneous Customers

```text
Intervals
→ start +1
→ end -1
→ sorted events
→ prefix/running count
→ maximum
```

Trigger:

```text
"How many are active at the same time?"
```

---

## 2. Nearest Left Position With Value <= Current

```text
Value condition + position condition
→ store (value,index)
→ sort by value
→ ordered set of indices
→ lower_bound(currentIndex)
→ previous position
```

Lock:

```text
SORT → WHO is eligible?
SET  → WHERE is nearest?
```

---

## 3. Longest Unique Successive Songs

```text
Longest + contiguous + unique
→ sliding window
→ map value → last position
→ duplicate?
→ jump left past old occurrence
```

Lock:

```text
lastSeen[value]
```

---

## 4. Maximum Non-Overlapping Jobs

```text
Maximum number of non-overlapping intervals
→ sort by end time
→ earliest finish leaves most room
→ greedily select compatible jobs
```

Lock:

```text
Become free as early as possible.
```

---

# Final Mental Model

These four problems train four reusable CP ideas:

```text
1. Interval events
   START +1 / END -1

2. Offline sorting
   value condition → sort
   position condition → ordered structure

3. Sliding window
   longest contiguous valid range

4. Greedy interval scheduling
   earliest finish time
```

When revising, do not start by reading the code.

Use this order:

```text
Title
 ↓
Try to recall observation
 ↓
Draw the ASCII mental model
 ↓
Dry run one example
 ↓
Write code from memory
 ↓
Only then check the saved solution
```
