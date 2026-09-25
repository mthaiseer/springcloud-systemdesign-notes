# 013_Sort_Ranking_Engine.md

> MiniSTLEngine Phase 013  
> Topic: `sort` as a **Ranking / Ordering / Preprocessing Engine** for CP, DSA, FAANG interviews, and real-system thinking.

---

# Clickable Index

- [1. Goal](#1-goal)
- [2. Why Sort Is An Engine](#2-why-sort-is-an-engine)
- [3. Real-System Mental Model](#3-real-system-mental-model)
- [4. Sort Core Behavior](#4-sort-core-behavior)
- [5. C++ Sort Cheat Sheet](#5-c-sort-cheat-sheet)
- [6. CP/DSA Recognition](#6-cpdsa-recognition)
- [7. Engine Architecture](#7-engine-architecture)
- [8. Basic Ranking Engine](#8-basic-ranking-engine)
- [9. Dry Run: Sort And Rank](#9-dry-run-sort-and-rank)
- [10. CP Pattern 1: Sort And Greedy](#10-cp-pattern-1-sort-and-greedy)
- [11. CP Pattern 2: Sort With Original Index](#11-cp-pattern-2-sort-with-original-index)
- [12. CP Pattern 3: Custom Comparator](#12-cp-pattern-3-custom-comparator)
- [13. CP Pattern 4: Merge Intervals](#13-cp-pattern-4-merge-intervals)
- [14. CP Pattern 5: Minimum Difference Pair](#14-cp-pattern-5-minimum-difference-pair)
- [15. CP Pattern 6: Sort + Two Pointers](#15-cp-pattern-6-sort--two-pointers)
- [16. CP Pattern 7: Sort Events For Sweep Line](#16-cp-pattern-7-sort-events-for-sweep-line)
- [17. Common Mistakes](#17-common-mistakes)
- [18. Complexity Table](#18-complexity-table)
- [19. Real-World Mapping](#19-real-world-mapping)
- [20. Final Mental Model](#20-final-mental-model)
- [21. Next Step](#21-next-step)

---

# 1. Goal

Learn `sort` not only as:

```text
arrange values
```

but as a:

```text
Ranking / Ordering / Preprocessing Engine
```

Sorting often converts a hard problem into an easy one.

It helps solve:

```text
greedy problems
ranking
interval merge
two pointers
minimum difference
sweep line
coordinate compression
duplicate detection
event processing
```

---

# 2. Why Sort Is An Engine

Normal thinking:

```cpp
sort(v.begin(), v.end());
```

Engine thinking:

```text
SortRankingEngine
    orders data
    exposes neighborhood relationships
    enables greedy decisions
    enables binary search
    enables two pointers
    enables sweep line
```

Sorting creates structure.

Before sorting:

```text
random values
```

After sorting:

```text
ordered search space
nearby values become meaningful
```

---

# 3. Real-System Mental Model

Real systems sort/rank data everywhere:

```text
leaderboards
search result ranking
database ORDER BY
log ordering by timestamp
event timeline processing
job priority ranking
analytics top reports
recommendation ranking
```

Architecture:

```text
Raw Records
    |
    v
SortRankingEngine
    |
    +--> order by key
    +--> apply greedy
    +--> merge ranges
    +--> scan neighbors
    +--> build index
    |
    v
Ranked / Optimized Result
```

---

# 4. Sort Core Behavior

Example:

```text
nums = [5, 1, 4, 2]
```

After sort:

```text
[1, 2, 4, 5]
```

Now we can:

```text
binary search
two pointer
find adjacent minimum difference
remove duplicates easily
rank values
```

---

# 5. C++ Sort Cheat Sheet

```cpp
vector<int> v = {5, 1, 4};

sort(v.begin(), v.end());              // ascending

sort(v.rbegin(), v.rend());            // descending

sort(v.begin(), v.end(), greater<int>());

sort(records.begin(), records.end());  // pair/tuple default sort
```

Custom comparator:

```cpp
sort(v.begin(), v.end(), [](int a, int b) {
    return a > b;
});
```

---

# 6. CP/DSA Recognition

Use sorting when problem says:

```text
minimum difference
ranking
order
greedy
intervals
merge
two sum variant
three sum
schedule
events
closest pair
top results
```

Hidden mapping:

| Problem clue | Sorting pattern |
|---|---|
| choose smallest/largest first | sort + greedy |
| compare neighboring values | sort |
| merge intervals | sort by start |
| two sum / three sum | sort + two pointers |
| event timeline | sort events |
| original index needed | sort pairs |
| custom priority | custom comparator |

---

# 7. Engine Architecture

```text
MiniSortRankingEngine
├── ascending sort
├── descending sort
├── record sort
├── custom comparator
├── rank assignment
├── interval merge
├── neighbor scan
├── sort + two pointer
└── event timeline sort
```

---

# 8. Basic Ranking Engine

## Step-by-Step Approach Before Code

```text
Step 1: Store raw values.

Step 2: Sort values in ascending order.

Step 3: Iterate sorted values.

Step 4: Assign rank based on sorted position.

Step 5: Use sorted order for easier decisions.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class SortRankingEngine {
private:
    vector<int> values;

public:

    void addValue(int x) {

        // Store raw value before ranking.
        values.push_back(x);
    }

    void sortAscending() {

        // WHY:
        // Sorting creates ordered structure.
        // Many greedy/two-pointer/binary-search patterns need this.
        sort(values.begin(), values.end());
    }

    void sortDescending() {

        sort(values.begin(), values.end(), greater<int>());
    }

    void printValues() {

        for (int x : values) {
            cout << x << " ";
        }

        cout << endl;
    }
};

int main() {

    SortRankingEngine engine;

    engine.addValue(50);
    engine.addValue(10);
    engine.addValue(30);

    cout << "Before sort:\n";
    engine.printValues();

    engine.sortAscending();

    cout << "After ascending sort:\n";
    engine.printValues();

    engine.sortDescending();

    cout << "After descending sort:\n";
    engine.printValues();

    return 0;
}
```

---

# 9. Dry Run: Sort And Rank

Input:

```text
values = [50, 10, 30]
```

Ascending sort:

```text
[10, 30, 50]
```

Ranks:

```text
10 -> rank 1
30 -> rank 2
50 -> rank 3
```

Descending sort:

```text
[50, 30, 10]
```

Leaderboard thinking:

```text
50 -> rank 1
30 -> rank 2
10 -> rank 3
```

---

# 10. CP Pattern 1: Sort And Greedy

## Problem Type

```text
Choose items optimally after ordering.
```

Example:

```text
maximize number of children satisfied
assign cookies
minimum platforms
activity selection
```

## Step-by-Step Approach Before Code

```text
Step 1: Identify what should be processed first:
        smallest, largest, earliest end, lowest cost, etc.

Step 2: Sort based on that key.

Step 3: Scan sorted data.

Step 4: Make locally optimal greedy decision.

Step 5: Prove why sorted order makes greedy safe.
```

## C++ Code: Assign Cookies

```cpp
#include <bits/stdc++.h>
using namespace std;

int findContentChildren(
    vector<int>& greed,
    vector<int>& cookies
) {
    sort(greed.begin(), greed.end());
    sort(cookies.begin(), cookies.end());

    int child = 0;
    int cookie = 0;
    int answer = 0;

    while (child < (int)greed.size() &&
           cookie < (int)cookies.size()) {

        if (cookies[cookie] >= greed[child]) {

            // Smallest possible cookie satisfies current least greedy child.
            answer++;
            child++;
            cookie++;

        } else {

            // Cookie too small, try bigger cookie.
            cookie++;
        }
    }

    return answer;
}

int main() {

    vector<int> greed = {1, 2, 3};
    vector<int> cookies = {1, 1};

    cout << findContentChildren(greed, cookies)
         << endl;

    return 0;
}
```

---

# 11. CP Pattern 2: Sort With Original Index

## Problem Type

```text
Sort values but still need original position.
```

## Step-by-Step Approach Before Code

```text
Step 1: Convert nums[i] into pair {value, originalIndex}.

Step 2: Sort pairs by value.

Step 3: Process sorted values.

Step 4: Use originalIndex when writing answer.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    vector<int> nums = {40, 10, 30};

    vector<pair<int,int>> records;

    for (int i = 0; i < (int)nums.size(); i++) {

        // Keep original index before sorting.
        records.push_back({nums[i], i});
    }

    sort(records.begin(), records.end());

    for (auto [value, originalIndex] : records) {

        cout << "value="
             << value
             << ", originalIndex="
             << originalIndex
             << endl;
    }

    return 0;
}
```

---

# 12. CP Pattern 3: Custom Comparator

## Problem Type

```text
Sort records by custom priority.
```

Example:

```text
sort by score descending
if tie, name ascending
```

## Step-by-Step Approach Before Code

```text
Step 1: Decide primary sort key.

Step 2: Decide tie-breaker key.

Step 3: Write comparator:
        return true if a should come before b.

Step 4: Be careful:
        comparator must be strict.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Student {
    string name;
    int score;
};

int main() {

    vector<Student> students = {
        {"Alice", 90},
        {"Bob", 95},
        {"Charlie", 90}
    };

    sort(
        students.begin(),
        students.end(),
        [](const Student& a, const Student& b) {

            if (a.score != b.score) {

                // Higher score first.
                return a.score > b.score;
            }

            // If score tie, smaller name first.
            return a.name < b.name;
        }
    );

    for (auto s : students) {

        cout << s.name
             << " "
             << s.score
             << endl;
    }

    return 0;
}
```

---

# 13. CP Pattern 4: Merge Intervals

## Problem Type

```text
Merge overlapping intervals.
```

## Step-by-Step Approach Before Code

```text
Step 1: Sort intervals by start.

Step 2: Create empty merged list.

Step 3: For each interval:
        if merged is empty or no overlap:
            add new interval.
        else:
            extend previous interval end.

Step 4: Return merged list.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<pair<int,int>> mergeIntervals(
    vector<pair<int,int>>& intervals
) {
    sort(intervals.begin(), intervals.end());

    vector<pair<int,int>> merged;

    for (auto [start, end] : intervals) {

        if (merged.empty() ||
            start > merged.back().second) {

            // No overlap.
            merged.push_back({start, end});

        } else {

            // Overlap exists.
            merged.back().second =
                max(merged.back().second, end);
        }
    }

    return merged;
}

int main() {

    vector<pair<int,int>> intervals = {
        {1,3}, {2,6}, {8,10}, {9,12}
    };

    auto merged = mergeIntervals(intervals);

    for (auto [l, r] : merged) {
        cout << "[" << l << "," << r << "] ";
    }

    return 0;
}
```

## Dry Run

```text
input:
[1,3], [2,6], [8,10], [9,12]

after sort:
[1,3], [2,6], [8,10], [9,12]

[1,3] -> add
[2,6] -> overlap, merge to [1,6]
[8,10] -> no overlap, add
[9,12] -> overlap, merge to [8,12]

answer:
[1,6], [8,12]
```

---

# 14. CP Pattern 5: Minimum Difference Pair

## Problem Type

```text
Find minimum absolute difference between any two elements.
```

## Step-by-Step Approach Before Code

```text
Step 1: Sort the array.

Step 2: Closest pair must be adjacent after sorting.

Step 3: Scan adjacent pairs.

Step 4: Track minimum difference.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int minimumDifference(vector<int>& nums) {

    sort(nums.begin(), nums.end());

    int best = INT_MAX;

    for (int i = 1; i < (int)nums.size(); i++) {

        best = min(
            best,
            nums[i] - nums[i - 1]
        );
    }

    return best;
}

int main() {

    vector<int> nums = {8, 1, 5, 3};

    cout << minimumDifference(nums)
         << endl;

    return 0;
}
```

## Why Adjacent Only?

After sorting:

```text
[1, 3, 5, 8]
```

Closest numbers must be neighbors.

Non-neighbors have larger or equal gap.

---

# 15. CP Pattern 6: Sort + Two Pointers

## Problem Type

```text
Find pair/triplet with condition.
```

Examples:

```text
two sum sorted
three sum
boats to save people
pair with sum closest to target
```

## Step-by-Step Approach Before Code

```text
Step 1: Sort array.

Step 2: Put left at start and right at end.

Step 3: Compute current sum.

Step 4: If sum too small:
        move left rightward.

Step 5: If sum too large:
        move right leftward.

Step 6: Continue until pointers meet.
```

## C++ Code: Two Sum Existence

```cpp
#include <bits/stdc++.h>
using namespace std;

bool twoSumExists(vector<int>& nums, int target) {

    sort(nums.begin(), nums.end());

    int left = 0;
    int right = (int)nums.size() - 1;

    while (left < right) {

        int sum = nums[left] + nums[right];

        if (sum == target) {
            return true;
        }

        if (sum < target) {

            // Need larger sum.
            left++;

        } else {

            // Need smaller sum.
            right--;
        }
    }

    return false;
}

int main() {

    vector<int> nums = {4, 1, 9, 7};

    cout << twoSumExists(nums, 8)
         << endl;

    return 0;
}
```

---

# 16. CP Pattern 7: Sort Events For Sweep Line

## Problem Type

```text
Process starts/ends in chronological order.
```

Examples:

```text
meeting rooms
maximum overlap
calendar booking
active intervals
```

## Step-by-Step Approach Before Code

```text
Step 1: Convert intervals to events.

Step 2: For each interval [l, r]:
        add start event at l.
        add end event at r.

Step 3: Sort events by time.

Step 4: Scan events in order.

Step 5: Maintain active count.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int maxOverlap(vector<pair<int,int>>& intervals) {

    vector<pair<int,int>> events;

    for (auto [l, r] : intervals) {

        events.push_back({l, +1});
        events.push_back({r, -1});
    }

    sort(events.begin(), events.end());

    int active = 0;
    int best = 0;

    for (auto [time, delta] : events) {

        active += delta;

        best = max(best, active);
    }

    return best;
}

int main() {

    vector<pair<int,int>> intervals = {
        {1,5}, {2,6}, {4,7}
    };

    cout << maxOverlap(intervals)
         << endl;

    return 0;
}
```

---

# 17. Common Mistakes

## Mistake 1: Forgetting Sort Changes Original Order

If original index matters, store:

```cpp
{value, index}
```

before sorting.

---

## Mistake 2: Bad Comparator

Wrong:

```cpp
return a <= b;
```

Comparator must be strict:

```cpp
return a < b;
```

---

## Mistake 3: Sorting When HashMap Is Better

If only lookup needed:

```text
unordered_map may be faster
```

Sorting is useful when order matters.

---

## Mistake 4: Wrong Interval Tie Handling

For sweep line, same time start/end ordering matters depending on problem.

Example:

```text
closed interval vs half-open interval
```

Think carefully.

---

# 18. Complexity Table

| Operation / Pattern | Complexity |
|---|---:|
| sort vector | O(N log N) |
| sort pairs/tuples | O(N log N) |
| custom comparator sort | O(N log N) |
| merge intervals | O(N log N) |
| sort + two pointers | O(N log N) |
| neighbor scan after sort | O(N log N) |
| event sweep after sort | O(N log N) |

---

# 19. Real-World Mapping

| Sort Concept | Real-System Meaning |
|---|---|
| sort by score | leaderboard |
| sort by timestamp | log/event timeline |
| sort intervals | calendar processing |
| sort + merge | range compaction |
| sort + scan neighbors | anomaly/closest match |
| sort + two pointers | matching engine |
| custom comparator | ranking policy |
| sorted events | sweep line scheduler |

---

# 20. Final Mental Model

Sort is:

```text
ordering engine that creates structure
```

Best for:

```text
ranking
greedy
intervals
two pointers
neighbor comparison
event processing
coordinate compression
```

One-line CP rule:

```text
If random order blocks insight, sort and look for structure.
```

One-line system rule:

```text
Sorting turns raw events into ordered timelines, rankings, indexes, and compacted ranges.
```

---

# 21. Next Step

Next file:

```text
014_TwoPointer_Stream_Merge_Engine.md
```

Then:

```text
015_SlidingWindow_Metrics_Engine.md
016_MonotonicStack_Boundary_Engine.md
017_MonotonicDeque_Window_MinMax_Engine.md
```
