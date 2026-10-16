# Two Pointers — Form 1: Variable-Size Sliding Window
## Expand → Shrink Until Valid → Use the Window
### TLE-Style Self-Study Notes

> **Source:** supplied Form 1 lecture.
>
> Preferred maximum-length template:
>
> ```cpp
> int tail = 0;
> int answer = 0;
>
> for (int head = 0; head < n; head++) {
>     add(arr[head]);
>
>     while (!valid()) {
>         remove(arr[tail]);
>         tail++;
>     }
>
>     answer = max(answer, head - tail + 1);
> }
> ```

---

# Table of Contents

- [1. What Form 1 Means](#1-what-form-1-means)
- [2. Prerequisite 1 — Variable Window](#2-prerequisite-1--variable-window)
- [3. Prerequisite 2 — Inclusive Window Length](#3-prerequisite-2--inclusive-window-length)
- [4. Prerequisite 3 — Window State / Invariant](#4-prerequisite-3--window-state--invariant)
- [5. Prerequisite 4 — Expand, Shrink, Use](#5-prerequisite-4--expand-shrink-use)
- [6. Prerequisite 5 — Why `while`, Not `if`](#6-prerequisite-5--why-while-not-if)
- [7. Prerequisite 6 — Monotonicity Requirement](#7-prerequisite-6--monotonicity-requirement)
- [8. Prerequisite 7 — Why Complexity Is O(N)](#8-prerequisite-7--why-complexity-is-on)
- [9. Master Template — Maximum Length](#9-master-template--maximum-length)
- [10. Problem 1 — Maximum Consecutive Ones After Flipping At Most K Zeros](#10-problem-1--maximum-consecutive-ones-after-flipping-at-most-k-zeros)
- [11. Problem 2 — Count Subarrays With At Most K Distinct](#11-problem-2--count-subarrays-with-at-most-k-distinct)
- [12. Problem 3 — Count Subarrays With Exactly K Distinct](#12-problem-3--count-subarrays-with-exactly-k-distinct)
- [13. Problem 4 — Shortest Subarray With At Least K Distinct](#13-problem-4--shortest-subarray-with-at-least-k-distinct)
- [14. Two Modes of Form 1](#14-two-modes-of-form-1)
- [15. When This Template Fails](#15-when-this-template-fails)
- [16. Common Mistakes](#16-common-mistakes)
- [17. Recognition Table](#17-recognition-table)
- [18. Final Mental Model](#18-final-mental-model)

---

# 1. What Form 1 Means

Form 0 had a fixed size. Form 1 discovers the window size while processing.

```text
tail = left boundary
head = right boundary
```

ASCII:

```text
index:  0   1   2   3   4   5   6
arr:   [a] [b] [c] [d] [e] [f] [g]
             ^-----------^
            tail        head

current window = [tail, head]
```

The window can grow and shrink according to a condition.

---

# 2. Prerequisite 1 — Variable Window

Example:

```text
arr = [2,1,3,2,4]
limit = 6
```

Rule:

```text
window sum <= 6
```

Expansion:

```text
[2]         sum=2  valid
[2,1]       sum=3  valid
[2,1,3]     sum=6  valid
[2,1,3,2]   sum=8  invalid
```

Shrink from the left:

```text
remove 2
→ [1,3,2]
→ sum=6
→ valid
```

Visual:

```text
before:
[2,1,3,2]
 ^     ^
tail  head

after:
  [1,3,2]
   ^   ^
 tail head
```

---

# 3. Prerequisite 2 — Inclusive Window Length

The window is:

```text
[tail, head]
```

Both endpoints are included.

Therefore:

```text
length = head - tail + 1
```

Example:

```text
tail = 2
head = 5
```

Indices are:

```text
2,3,4,5
```

Count:

```text
5 - 2 + 1 = 4
```

---

# 4. Prerequisite 3 — Window State / Invariant

The maintained state must exactly describe:

```text
arr[tail...head]
```

Common states:

```text
zeroCount
windowSum
distinctCount
frequency[value]
number of bad elements
```

Example:

```text
arr = [1,0,1,0,1]
tail = 1
head = 4

window = [0,1,0,1]
zeroCount = 2
```

When `head` enters:

```text
add(arr[head])
```

When `tail` leaves:

```text
remove(arr[tail])
tail++
```

This preserves the invariant.

---

# 5. Prerequisite 4 — Expand, Shrink, Use

For maximum-length upper-bound problems:

```text
1. EXPAND with head
2. SHRINK while invalid
3. USE the valid window
```

ASCII:

```text
HEAD moves right
      |
      v
  add new element
      |
      v
   invalid?
    /   \
  no     yes
  |       |
  |    remove tail
  |       |
  |      tail++
  |       |
  +-------+
      |
      v
update maximum length
```

This is the main template to memorize.

---

# 6. Prerequisite 5 — Why `while`, Not `if`

Suppose:

```text
limit = 5
window = [4,4,4]
sum = 12
```

Remove one element:

```text
sum = 8
```

Still invalid.

Remove another:

```text
sum = 4
```

Now valid.

So:

```cpp
while (!valid()) {
    remove(arr[tail]);
    tail++;
}
```

is necessary.

A single `if` may stop too early.

---

# 7. Prerequisite 6 — Monotonicity Requirement

The common upper-bound window works when shrinking from the left predictably helps.

Good examples:

```text
zeroCount <= K

distinctCount <= K

windowSum <= K
when all values are non-negative
```

Why?

Removing one element cannot increase:

```text
number of zeros
number of distinct values
non-negative sum
```

So an invalid window can be repaired by moving `tail`.

This is the critical applicability rule.

---

# 8. Prerequisite 7 — Why Complexity Is O(N)

Even though we have:

```cpp
for (...) {
    while (...) {
        ...
    }
}
```

the algorithm can still be linear.

`head` moves only forward:

```text
0 → 1 → 2 → ... → n-1
```

`tail` also moves only forward:

```text
0 → 1 → 2 → ... → n
```

Total pointer movements:

```text
N + N = 2N = O(N)
```

This is amortized analysis.

---

# 9. Master Template — Maximum Length

Use this when:

```text
Goal = maximum length

Condition = upper bound / monotonic validity

Invalid window can be repaired by moving tail
```

```cpp
int tail = 0;
int answer = 0;

for (int head = 0; head < n; head++) {
    add(arr[head]);

    while (!valid()) {
        remove(arr[tail]);
        tail++;
    }

    answer = max(answer, head - tail + 1);
}
```

Mental model:

```text
EXPAND
  ↓
INVALID?
  ↓
SHRINK UNTIL VALID
  ↓
UPDATE MAX
```

---

# 10. Problem 1 — Maximum Consecutive Ones After Flipping At Most K Zeros

## What It Asks

Given a binary array and integer `K`, flip at most `K` zeros into ones.

Find:

```text
maximum consecutive ones
```

Story removed:

```text
find longest subarray containing at most K zeros
```

Example:

```text
arr = [0,1,0,1,0,0,1,1,0]
K = 2
```

Expected result:

```text
5
```

A valid length-5 window is:

```text
[1,0,0,1,1]
```

It contains two zeros.

---

## State and Validity

State:

```text
zeroCount
```

Valid:

```text
zeroCount <= K
```

Map to template:

```text
ADD:
if arr[head] == 0
    zeroCount++

INVALID:
zeroCount > K

REMOVE:
if arr[tail] == 0
    zeroCount--

ANSWER:
max(answer, head-tail+1)
```

---

## Compact Dry Run

```text
arr = [0,1,0,1,0]
K = 2
```

```text
head  added  zeros  action             window       answer
0     0      1      valid              [0]          1
1     1      1      valid              [0,1]        2
2     0      2      valid              [0,1,0]      3
3     1      2      valid              [0,1,0,1]    4
4     0      3      remove left 0      [1,0,1,0]    4
```

At `head=4`:

```text
zeroCount = 3 > 2
```

Shrink:

```text
remove arr[0] = 0
zeroCount = 2
tail = 1
```

Valid again.

---

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int longestOnesAfterFlips(const vector<int>& arr, int k) {
    int n = arr.size();

    int tail = 0;
    int zeroCount = 0;
    int answer = 0;

    for (int head = 0; head < n; head++) {
        if (arr[head] == 0) {
            zeroCount++;
        }

        while (zeroCount > k) {
            if (arr[tail] == 0) {
                zeroCount--;
            }

            tail++;
        }

        answer = max(answer, head - tail + 1);
    }

    return answer;
}

int main() {
    vector<int> arr = {0, 1, 0, 1, 0, 0, 1, 1, 0};
    int k = 2;

    cout << longestOnesAfterFlips(arr, k) << '\n';

    return 0;
}
```

Complexity:

```text
Time  = O(N)
Space = O(1)
```

---

# 11. Problem 2 — Count Subarrays With At Most K Distinct

## What It Asks

Count subarrays containing at most `K` distinct values.

Lecture example:

```text
arr = [2,3,2,5,2,5,2]
K = 2
```

Expected answer:

```text
20
```

---

## State

Maintain:

```text
frequency[value]
distinctCount
```

Adding `x`:

```text
if frequency[x] == 0:
    distinctCount++

frequency[x]++
```

Removing `x`:

```text
frequency[x]--

if frequency[x] == 0:
    erase x
    distinctCount--
```

Valid:

```text
distinctCount <= K
```

---

## Why Add `head-tail+1`?

Once `[tail, head]` is valid, every suffix ending at `head` is also valid:

```text
[head, head]
[head-1, head]
...
[tail, head]
```

Number of valid starts:

```text
head - tail + 1
```

So:

```cpp
answer += head - tail + 1;
```

Tiny example:

```text
arr = [2,3,2]
K = 2
```

At `head=2`, valid subarrays ending at `2` are:

```text
[2]
[3,2]
[2,3,2]
```

Count:

```text
3 = 2-0+1
```

---

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countAtMostKDistinct(const vector<int>& arr, int k) {
    if (k < 0) {
        return 0;
    }

    int n = arr.size();

    int tail = 0;
    int distinctCount = 0;

    long long answer = 0;

    unordered_map<int, int> frequency;

    for (int head = 0; head < n; head++) {
        if (frequency[arr[head]] == 0) {
            distinctCount++;
        }

        frequency[arr[head]]++;

        while (distinctCount > k) {
            frequency[arr[tail]]--;

            if (frequency[arr[tail]] == 0) {
                frequency.erase(arr[tail]);
                distinctCount--;
            }

            tail++;
        }

        answer += head - tail + 1;
    }

    return answer;
}

int main() {
    vector<int> arr = {2, 3, 2, 5, 2, 5, 2};
    int k = 2;

    cout << countAtMostKDistinct(arr, k) << '\n';

    return 0;
}
```

Complexity:

```text
Expected Time = O(N)
Space         = O(K)
```

---

# 12. Problem 3 — Count Subarrays With Exactly K Distinct

## What It Asks

Count subarrays containing exactly `K` distinct values.

Direct condition:

```text
distinctCount == K
```

is inconvenient for this upper-bound window.

Instead use:

```text
exactly(K)
=
atMost(K)
-
atMost(K-1)
```

Reason:

```text
atMost(K)
=
atMost(K-1)
+
exactly(K)
```

So:

```text
exactly(K)
=
atMost(K)-atMost(K-1)
```

Lecture example:

```text
atMost(2) = 20
atMost(1) = 7

exactly(2) = 13
```

Code:

```cpp
long long countExactlyKDistinct(const vector<int>& arr, int k) {
    return countAtMostKDistinct(arr, k)
         - countAtMostKDistinct(arr, k - 1);
}
```

Reusable recognition:

```text
EXACTLY K
→ try AT MOST K - AT MOST K-1
```

---

# 13. Problem 4 — Shortest Subarray With At Least K Distinct

This objective is different.

Goal:

```text
minimum length
```

Condition:

```text
distinctCount >= K
```

For minimum length, use the mirrored template:

```cpp
int tail = 0;
int answer = INF;

for (int head = 0; head < n; head++) {
    add(arr[head]);

    while (valid()) {
        answer = min(answer, head - tail + 1);

        remove(arr[tail]);
        tail++;
    }
}
```

Notice:

```text
MAX upper-bound:
shrink while INVALID

MIN lower-bound:
shrink while VALID
```

---

## Dry Run

```text
arr = [1,2,1,3]
K = 3
```

Expansion:

```text
[1]       distinct=1
[1,2]     distinct=2
[1,2,1]   distinct=2
[1,2,1,3] distinct=3  valid
```

Candidate:

```text
length = 4
```

Shrink:

```text
remove first 1

[2,1,3]
distinct=3
length=3
```

Still valid → update:

```text
answer=3
```

Shrink again:

```text
remove 2

[1,3]
distinct=2
```

Invalid → stop shrinking.

Best:

```text
3
```

---

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int shortestAtLeastKDistinct(const vector<int>& arr, int k) {
    if (k <= 0) {
        return 0;
    }

    int n = arr.size();

    int tail = 0;
    int distinctCount = 0;
    int answer = n + 1;

    unordered_map<int, int> frequency;

    for (int head = 0; head < n; head++) {
        if (frequency[arr[head]] == 0) {
            distinctCount++;
        }

        frequency[arr[head]]++;

        while (distinctCount >= k) {
            answer = min(answer, head - tail + 1);

            frequency[arr[tail]]--;

            if (frequency[arr[tail]] == 0) {
                frequency.erase(arr[tail]);
                distinctCount--;
            }

            tail++;
        }
    }

    return (answer == n + 1 ? -1 : answer);
}
```

---

# 14. Two Modes of Form 1

| Goal | Validity | Shrink When | Answer |
|---|---|---|---|
| Maximum length with upper bound | `state <= K` | invalid | `max(length)` after repair |
| Count with upper bound | `state <= K` | invalid | `+= length` after repair |
| Minimum length with lower bound | `state >= K` | valid | `min(length)` before shrink |

### Maximum-length mode

```cpp
for (int head = 0; head < n; head++) {
    add(arr[head]);

    while (!valid()) {
        remove(arr[tail]);
        tail++;
    }

    answer = max(answer, head - tail + 1);
}
```

### Minimum-length mode

```cpp
for (int head = 0; head < n; head++) {
    add(arr[head]);

    while (valid()) {
        answer = min(answer, head - tail + 1);

        remove(arr[tail]);
        tail++;
    }
}
```

---

# 15. When This Template Fails

The lecture gives an important signed-number counterexample.

```text
arr = [-5,4,5,-10]
K = 0
```

Goal:

```text
longest subarray with sum <= 0
```

Whole array:

```text
-5 + 4 + 5 - 10
=
-6
```

So correct answer is:

```text
4
```

But standard sliding window can fail.

At one point:

```text
[-5,4]
sum = -1
valid
```

Add `5`:

```text
[-5,4,5]
sum = 4
invalid
```

A normal upper-bound window would shrink.

But expanding further gives:

```text
[-5,4,5,-10]
sum = -6
valid again
```

So:

```text
invalid now
does not imply
all larger windows are invalid
```

Also:

```text
[-5,4]
sum=-1
valid
```

Remove `-5`:

```text
[4]
sum=4
invalid
```

So removing from the left can make a valid window invalid.

This destroys the monotonic property required by the standard template.

For arbitrary signed values, consider other techniques such as:

```text
prefix sums
monotonic deque
hash map
binary search
ordered structures
```

depending on the problem.

---

# 16. Common Mistakes

## 1. Using `if` instead of `while`

Wrong:

```cpp
if (!valid()) {
    remove(arr[tail]);
    tail++;
}
```

Correct:

```cpp
while (!valid()) {
    remove(arr[tail]);
    tail++;
}
```

---

## 2. Updating max before restoring validity

For maximum upper-bound problems:

```text
repair first
then update answer
```

Correct order:

```cpp
while (!valid()) {
    remove(arr[tail]);
    tail++;
}

answer = max(answer, head - tail + 1);
```

---

## 3. Moving tail before removing its contribution

Wrong:

```cpp
tail++;
remove(arr[tail]);
```

Correct:

```cpp
remove(arr[tail]);
tail++;
```

---

## 4. Recomputing the whole state

Maintain incrementally:

```text
add entering element
remove leaving element
```

Do not rebuild the entire window every time.

---

## 5. Using `exactly K` directly for counting

Prefer:

```text
exactly(K)
=
atMost(K)-atMost(K-1)
```

when the `atMost` version is monotonic.

---

## 6. Ignoring monotonicity

A sliding-window-looking statement does not guarantee that standard Form 1 works.

Check whether:

```text
moving head and tail changes validity predictably
```

---

# 17. Recognition Table

| Signal | State | Validity | Action |
|---|---|---|---|
| Longest ones after ≤ `K` flips | `zeroCount` | `zeroCount <= K` | Max-length template |
| Longest subarray with ≤ `K` distinct | frequency + `distinctCount` | `distinctCount <= K` | Max-length template |
| Count subarrays with ≤ `K` distinct | frequency + `distinctCount` | `distinctCount <= K` | Add `head-tail+1` |
| Count exactly `K` distinct | reuse `atMost` | transformation | `atMost(K)-atMost(K-1)` |
| Shortest subarray with ≥ `K` distinct | frequency + `distinctCount` | `distinctCount >= K` | Min-length template |
| Sum ≤ `K`, values non-negative | `windowSum` | `windowSum <= K` | Max-length template |
| Sum ≤ `K`, arbitrary signed values | not monotonic | unsafe | Different technique |

---

# 18. Final Mental Model

## Maximum Length

```text
HEAD expands
     |
     v
ADD arr[head]
     |
     v
window invalid?
     |
    YES
     |
     v
REMOVE arr[tail]
tail++
     |
     +---- repeat until valid
     |
     v
answer = max(answer, head-tail+1)
```

Master code:

```cpp
int tail = 0;
int answer = 0;

for (int head = 0; head < n; head++) {
    add(arr[head]);

    while (!valid()) {
        remove(arr[tail]);
        tail++;
    }

    answer = max(answer, head - tail + 1);
}
```

## Minimum Length

```text
HEAD expands
     |
     v
window becomes valid
     |
     v
record minimum
     |
     v
remove tail
tail++
     |
     +---- continue while still valid
```

Master code:

```cpp
int tail = 0;
int answer = INF;

for (int head = 0; head < n; head++) {
    add(arr[head]);

    while (valid()) {
        answer = min(answer, head - tail + 1);

        remove(arr[tail]);
        tail++;
    }
}
```

### 60-second recognition

```text
MAXIMUM length
+
upper-bound monotonic condition

→ EXPAND
→ SHRINK WHILE INVALID
→ UPDATE MAX
```

```text
MINIMUM length
+
lower-bound monotonic condition

→ EXPAND
→ WHILE VALID:
     UPDATE MIN
     SHRINK
```

Always ask:

```text
Can moving tail in one direction safely restore / improve validity?

If yes:
Sliding Window / Two Pointers is a strong candidate.
```
