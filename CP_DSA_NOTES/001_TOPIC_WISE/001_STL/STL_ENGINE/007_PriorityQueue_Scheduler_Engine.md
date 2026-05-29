# 007_PriorityQueue_Scheduler_Engine.md

> MiniSTLEngine Phase 007  
> Topic: `priority_queue` as a **Scheduler / Top-K / Best Candidate Engine** for CP, DSA, FAANG interviews, and real-system thinking.

---

# Clickable Index

- [1. Goal](#1-goal)
- [2. Why Priority Queue Is An Engine](#2-why-priority-queue-is-an-engine)
- [3. Real-System Mental Model](#3-real-system-mental-model)
- [4. Heap Core Behavior](#4-heap-core-behavior)
- [5. Priority Queue Operations Cheat Sheet](#5-priority-queue-operations-cheat-sheet)
- [6. Max Heap vs Min Heap](#6-max-heap-vs-min-heap)
- [7. CP/DSA Recognition](#7-cpdsa-recognition)
- [8. Engine Architecture](#8-engine-architecture)
- [9. Basic Scheduler Engine](#9-basic-scheduler-engine)
- [10. Dry Run: Highest Priority First](#10-dry-run-highest-priority-first)
- [11. CP Pattern 1: Kth Largest Element](#11-cp-pattern-1-kth-largest-element)
- [12. CP Pattern 2: Top K Frequent Elements](#12-cp-pattern-2-top-k-frequent-elements)
- [13. CP Pattern 3: K-Way Merge](#13-cp-pattern-3-k-way-merge)
- [14. CP Pattern 4: Meeting Rooms / Resource Scheduler](#14-cp-pattern-4-meeting-rooms--resource-scheduler)
- [15. CP Pattern 5: Dijkstra Shortest Path](#15-cp-pattern-5-dijkstra-shortest-path)
- [16. CP Pattern 6: Delayed Retry Scheduler](#16-cp-pattern-6-delayed-retry-scheduler)
- [17. Priority Queue vs Queue vs Set](#17-priority-queue-vs-queue-vs-set)
- [18. Common Mistakes](#18-common-mistakes)
- [19. Complexity Table](#19-complexity-table)
- [20. Real-World Mapping](#20-real-world-mapping)
- [21. Final Mental Model](#21-final-mental-model)
- [22. Next Step](#22-next-step)

---

# 1. Goal

Learn `priority_queue` not only as heap syntax, but as a:

```text
Scheduler / Top-K / Best Candidate Engine
```

It helps solve:

```text
top K
kth largest/smallest
stream ranking
Dijkstra
meeting rooms
task scheduling
merge K sorted lists
resource allocation
retry scheduler
current best candidate problems
```

---

# 2. Why Priority Queue Is An Engine

A normal queue processes:

```text
oldest item first
```

A priority queue processes:

```text
most important item first
```

Normal thinking:

```cpp
priority_queue<int> pq;
```

Engine thinking:

```text
PriorityQueueSchedulerEngine
    stores candidates
    ranks them by priority
    always exposes current best candidate
```

Core question:

```text
Do I repeatedly need current min/max/best?
```

If yes:

```text
priority_queue is likely useful
```

---

# 3. Real-System Mental Model

Priority queues appear in:

```text
CPU scheduler
Kafka delayed retry scheduler
task execution engine
top-K analytics
Dijkstra routing
search ranking
load balancer priority
meeting room allocation
job processing system
```

Architecture:

```text
Incoming Candidates
        |
        v
PriorityQueueSchedulerEngine
        |
        +--> push candidate
        +--> get best candidate
        +--> remove processed candidate
        |
        v
Processed In Priority Order
```

---

# 4. Heap Core Behavior

Max heap:

```text
top = largest
```

Example:

```text
push 5
push 10
push 3
```

Heap top:

```text
10
```

Pop order:

```text
10 -> 5 -> 3
```

Min heap:

```text
top = smallest
```

Pop order:

```text
3 -> 5 -> 10
```

---

# 5. Priority Queue Operations Cheat Sheet

```cpp
priority_queue<int> pq;

pq.push(10);     // insert
pq.top();        // best element
pq.pop();        // remove best
pq.empty();      // check empty
pq.size();       // number of elements
```

Important:

```cpp
pq.pop();
```

does not return value.

Correct:

```cpp
int x = pq.top();
pq.pop();
```

---

# 6. Max Heap vs Min Heap

## Max Heap

```cpp
priority_queue<int> maxHeap;
```

## Min Heap

```cpp
priority_queue<int, vector<int>, greater<int>> minHeap;
```

## Min Heap Of Pairs

```cpp
priority_queue<
    pair<int,int>,
    vector<pair<int,int>>,
    greater<pair<int,int>>
> pq;
```

Pair ordering:

```text
first field first
then second field
```

So:

```cpp
{distance, node}
```

makes heap sort by distance.

---

# 7. CP/DSA Recognition

Use priority queue when problem says:

```text
kth largest
top K
current minimum
current maximum
merge sorted lists
shortest path weighted graph
meeting rooms
task scheduler
minimum cost repeatedly
best candidate
streaming rank
```

Hidden mapping:

| Problem clue | Heap pattern |
|---|---|
| repeatedly get max/min | heap |
| kth largest | min heap size K |
| kth smallest | max heap size K or ordered min heap |
| top K frequent | frequency map + heap |
| merge K sorted | min heap |
| weighted shortest path | Dijkstra min heap |
| meeting rooms | min heap by end time |
| retry later | min heap by next execution time |

---

# 8. Engine Architecture

```text
MiniPriorityQueueSchedulerEngine
├── max heap engine
├── min heap engine
├── top-K engine
├── streaming rank engine
├── resource scheduler
├── Dijkstra candidate engine
├── delayed retry engine
└── custom comparator engine
```

---

# 9. Basic Scheduler Engine

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class SchedulerEngine {
private:
    priority_queue<pair<int, string>> pq;

public:

    void addTask(int priority, string taskName) {

        // WHY:
        // priority_queue always keeps highest priority task at top.
        // pair<int,string> sorts by priority first.
        pq.push({priority, taskName});
    }

    void processNextTask() {

        if (pq.empty()) {
            cout << "No task available\n";
            return;
        }

        auto [priority, taskName] = pq.top();

        pq.pop();

        cout << "Processing task: "
             << taskName
             << " with priority "
             << priority
             << endl;
    }

    int pendingTasks() {
        return pq.size();
    }
};

int main() {

    SchedulerEngine scheduler;

    scheduler.addTask(3, "SEND_EMAIL");
    scheduler.addTask(10, "PROCESS_PAYMENT");
    scheduler.addTask(5, "GENERATE_REPORT");

    scheduler.processNextTask();
    scheduler.processNextTask();
    scheduler.processNextTask();

    return 0;
}
```

---

# 10. Dry Run: Highest Priority First

Tasks:

```text
SEND_EMAIL        priority 3
PROCESS_PAYMENT   priority 10
GENERATE_REPORT   priority 5
```

Heap ordering:

```text
top -> PROCESS_PAYMENT(10)
       GENERATE_REPORT(5)
       SEND_EMAIL(3)
```

Processing order:

```text
PROCESS_PAYMENT
GENERATE_REPORT
SEND_EMAIL
```

Key idea:

```text
priority decides order, not arrival time
```

---

# 11. CP Pattern 1: Kth Largest Element

## Problem Type

```text
Find kth largest element in array.
```

## Core Idea

Keep:

```text
min heap of size K
```

Why?

```text
Heap contains current top K largest.
Heap top = weakest among top K.
At end, heap top = kth largest.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int findKthLargest(vector<int>& nums, int k) {

    priority_queue<
        int,
        vector<int>,
        greater<int>
    > minHeap;

    for (int x : nums) {

        // Candidate enters top-K engine.
        minHeap.push(x);

        // If more than K candidates,
        // remove weakest among current top K.
        if ((int)minHeap.size() > k) {
            minHeap.pop();
        }
    }

    return minHeap.top();
}

int main() {

    vector<int> nums = {3, 2, 1, 5, 6, 4};

    int k = 2;

    cout << findKthLargest(nums, k) << endl; // 5

    return 0;
}
```

## Dry Run

```text
nums = [3,2,1,5,6,4], k = 2

push 3 -> heap [3]
push 2 -> heap [2,3]
push 1 -> heap [1,3,2] -> pop 1 -> [2,3]
push 5 -> [2,3,5] -> pop 2 -> [3,5]
push 6 -> [3,5,6] -> pop 3 -> [5,6]
push 4 -> [4,6,5] -> pop 4 -> [5,6]

answer = 5
```

---

# 12. CP Pattern 2: Top K Frequent Elements

## Problem Type

```text
Return K most frequent elements.
```

## Core Idea

```text
frequency map + min heap of size K
```

Heap stores:

```text
{frequency, value}
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> topKFrequent(vector<int>& nums, int k) {

    unordered_map<int, int> freq;

    for (int x : nums) {
        freq[x]++;
    }

    priority_queue<
        pair<int,int>,
        vector<pair<int,int>>,
        greater<pair<int,int>>
    > minHeap;

    for (auto [value, count] : freq) {

        // Store as {count, value}
        // WHY:
        // We rank by frequency.
        minHeap.push({count, value});

        if ((int)minHeap.size() > k) {
            minHeap.pop();
        }
    }

    vector<int> answer;

    while (!minHeap.empty()) {
        answer.push_back(minHeap.top().second);
        minHeap.pop();
    }

    return answer;
}

int main() {

    vector<int> nums = {1,1,1,2,2,3};

    vector<int> ans = topKFrequent(nums, 2);

    for (int x : ans) {
        cout << x << " ";
    }

    return 0;
}
```

---

# 13. CP Pattern 3: K-Way Merge

## Problem Type

```text
Merge K sorted arrays/lists.
```

## Core Idea

Use min heap containing:

```text
current head of each sorted source
```

Always extract smallest available head.

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> mergeKSortedArrays(vector<vector<int>>& arrays) {

    using State = tuple<int, int, int>;
    // {value, arrayIndex, elementIndex}

    priority_queue<
        State,
        vector<State>,
        greater<State>
    > minHeap;

    for (int i = 0; i < (int)arrays.size(); i++) {
        if (!arrays[i].empty()) {
            minHeap.push({arrays[i][0], i, 0});
        }
    }

    vector<int> result;

    while (!minHeap.empty()) {

        auto [value, arrayIndex, elementIndex] =
            minHeap.top();

        minHeap.pop();

        result.push_back(value);

        int nextIndex = elementIndex + 1;

        if (nextIndex < (int)arrays[arrayIndex].size()) {
            minHeap.push({
                arrays[arrayIndex][nextIndex],
                arrayIndex,
                nextIndex
            });
        }
    }

    return result;
}

int main() {

    vector<vector<int>> arrays = {
        {1, 4, 7},
        {2, 5, 8},
        {3, 6, 9}
    };

    vector<int> merged =
        mergeKSortedArrays(arrays);

    for (int x : merged) {
        cout << x << " ";
    }

    return 0;
}
```

---

# 14. CP Pattern 4: Meeting Rooms / Resource Scheduler

## Problem Type

```text
Minimum number of meeting rooms required.
```

## Core Idea

Sort by start time.

Min heap stores:

```text
end times of active meetings
```

If earliest ending meeting ends before current starts:

```text
reuse room
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int minMeetingRooms(vector<pair<int,int>>& meetings) {

    sort(meetings.begin(), meetings.end());

    priority_queue<
        int,
        vector<int>,
        greater<int>
    > activeEndTimes;

    for (auto [start, end] : meetings) {

        // If earliest room becomes free before current meeting,
        // reuse that room.
        if (!activeEndTimes.empty() &&
            activeEndTimes.top() <= start) {

            activeEndTimes.pop();
        }

        // Current meeting occupies a room until 'end'.
        activeEndTimes.push(end);
    }

    return activeEndTimes.size();
}

int main() {

    vector<pair<int,int>> meetings = {
        {0, 30},
        {5, 10},
        {15, 20}
    };

    cout << minMeetingRooms(meetings) << endl; // 2

    return 0;
}
```

---

# 15. CP Pattern 5: Dijkstra Shortest Path

## Problem Type

```text
Shortest path in weighted graph with non-negative edges.
```

## Core Idea

Min heap stores:

```text
{currentDistance, node}
```

Always process:

```text
currently closest candidate
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> dijkstra(
    int n,
    vector<vector<pair<int,int>>>& graph,
    int source
) {

    vector<int> dist(n + 1, INT_MAX);

    priority_queue<
        pair<int,int>,
        vector<pair<int,int>>,
        greater<pair<int,int>>
    > minHeap;

    dist[source] = 0;

    minHeap.push({0, source});

    while (!minHeap.empty()) {

        auto [currentDist, node] = minHeap.top();

        minHeap.pop();

        // Stale state check:
        // If this candidate is not equal to best known distance,
        // it is an old heap entry and should be ignored.
        if (currentDist != dist[node]) {
            continue;
        }

        for (auto [neighbor, weight] : graph[node]) {

            if (dist[neighbor] > currentDist + weight) {

                dist[neighbor] =
                    currentDist + weight;

                minHeap.push({
                    dist[neighbor],
                    neighbor
                });
            }
        }
    }

    return dist;
}

int main() {

    int n = 4;

    vector<vector<pair<int,int>>> graph(n + 1);

    graph[1].push_back({2, 5});
    graph[1].push_back({3, 2});
    graph[3].push_back({4, 1});
    graph[2].push_back({4, 2});

    vector<int> dist =
        dijkstra(n, graph, 1);

    for (int i = 1; i <= n; i++) {
        cout << "dist[1 -> " << i << "] = "
             << dist[i] << endl;
    }

    return 0;
}
```

---

# 16. CP Pattern 6: Delayed Retry Scheduler

## Real-System Type

```text
Retry failed jobs after delay.
```

Example:

```text
Kafka retry topic scheduler
payment retry
email retry
background job retry
```

## Core Idea

Min heap stores:

```text
{nextRunTime, jobId}
```

Closest scheduled job runs first.

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    using Job = pair<int, string>;
    // {nextRunTime, jobName}

    priority_queue<
        Job,
        vector<Job>,
        greater<Job>
    > retryHeap;

    retryHeap.push({10, "EMAIL_RETRY"});
    retryHeap.push({5, "PAYMENT_RETRY"});
    retryHeap.push({20, "REPORT_RETRY"});

    int currentTime = 0;

    while (!retryHeap.empty()) {

        auto [nextRunTime, jobName] =
            retryHeap.top();

        retryHeap.pop();

        currentTime = nextRunTime;

        cout << "time="
             << currentTime
             << " execute "
             << jobName
             << endl;
    }

    return 0;
}
```

---

# 17. Priority Queue vs Queue vs Set

| Need | Use |
|---|---|
| FIFO order | queue |
| current max/min | priority_queue |
| delete arbitrary item | set/multiset |
| sorted traversal | set/map |
| top-K fixed size | priority_queue |
| dynamic ordered queries | set/map |

Important:

```text
priority_queue cannot remove arbitrary middle elements efficiently.
```

For that, use:

```text
set / multiset
or lazy deletion
```

---

# 18. Common Mistakes

## Mistake 1: Wrong Heap Type

For min heap:

```cpp
priority_queue<int, vector<int>, greater<int>> pq;
```

Default is max heap.

---

## Mistake 2: Wrong Pair Order

For Dijkstra:

```cpp
{distance, node}
```

not:

```cpp
{node, distance}
```

Because heap sorts by first field.

---

## Mistake 3: Forgetting Stale State Check

In Dijkstra:

```cpp
if (currentDist != dist[node]) continue;
```

Without this, code may process outdated heap entries.

---

## Mistake 4: Using Heap When Need Arbitrary Delete

Priority queue supports:

```text
top removal only
```

Not:

```text
remove any element
```

Use:

```text
multiset
```

or:

```text
lazy deletion
```

---

# 19. Complexity Table

| Operation | Complexity |
|---|---:|
| push | O(log N) |
| pop | O(log N) |
| top | O(1) |
| size | O(1) |
| top-K over N | O(N log K) |
| Dijkstra | O((V + E) log V) |
| merge K sorted arrays | O(N log K) |

---

# 20. Real-World Mapping

| Priority Queue Concept | Real-System Meaning |
|---|---|
| heap top | current best candidate |
| max heap | highest priority task |
| min heap | earliest deadline / shortest distance |
| top-K heap | analytics leaderboard |
| Dijkstra heap | routing engine |
| meeting room heap | resource allocator |
| retry heap | delayed job scheduler |
| stale heap entry | outdated event/candidate |

---

# 21. Final Mental Model

Priority queue is:

```text
current best candidate engine
```

Best for:

```text
top-K
scheduling
Dijkstra
resource allocation
stream ranking
repeated min/max selection
```

One-line CP rule:

```text
If you repeatedly need current min/max/best candidate, think priority_queue.
```

One-line system rule:

```text
Priority queues power schedulers, retry systems, routing engines, and ranking systems.
```

---

# 22. Next Step

Next file:

```text
008_Set_Ordered_Index_Engine.md
```

Then:

```text
009_Map_KeyValue_Index_Engine.md
010_UnorderedMap_Hash_Index_Engine.md
011_Multiset_Median_Engine.md
```
