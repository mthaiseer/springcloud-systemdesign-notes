# Two Pointers & Sliding Window — Introduction
## Four Reusable Forms, Invariants, Movement Rules, and Recognition
### TLE-Style Self-Study Notes

> Core idea:
>
> ```text
> Stop recomputing the same pairs / subarrays / positions.
> Move one or two indices monotonically
> and reuse information from the current state.
> ```
>
> In these notes:
>
> ```text
> tail = left pointer
> head = right pointer
> ```
>
> `pointer` means an array index or iterator, not a C++ memory pointer such as `int*`.

---

# Table of Contents

- [1. Why Two Pointers Exist](#1-why-two-pointers-exist)
- [2. What Is the Two Pointer Technique?](#2-what-is-the-two-pointer-technique)
- [3. The Real Idea — Maintain an Invariant](#3-the-real-idea--maintain-an-invariant)
- [4. The Four Forms](#4-the-four-forms)
- [5. Form 0 — Fixed Window](#5-form-0--fixed-window)
- [6. Form 1 — Variable Window](#6-form-1--variable-window)
- [7. Form 2 — Divergence / Opposite Ends](#7-form-2--divergence--opposite-ends)
- [8. Form 3 — Multisequence](#8-form-3--multisequence)
- [9. Comparing the Four Forms](#9-comparing-the-four-forms)
- [10. Why Two Pointers Can Be O(N)](#10-why-two-pointers-can-be-on)
- [11. Sliding Window Monotonicity Requirement](#11-sliding-window-monotonicity-requirement)
- [12. Inclusive Window Math](#12-inclusive-window-math)
- [13. Correct Update Order](#13-correct-update-order)
- [14. Problem-Solving Checklist](#14-problem-solving-checklist)
- [15. Compact C++17 Templates](#15-compact-c17-templates)
- [16. Common Mistakes](#16-common-mistakes)
- [17. Recognition Cheat Sheet](#17-recognition-cheat-sheet)
- [18. Final Mental Model](#18-final-mental-model)
- [19. Course Roadmap](#19-course-roadmap)

---

# 1. Why Two Pointers Exist

Suppose:

```text
N = 100000
```

A brute-force pair search may do:

```text
N² = 10^5 × 10^5 = 10^10 operations
```

Example:

```cpp
for (int i = 0; i < n; i++) {
    for (int j = i + 1; j < n; j++) {
        // inspect pair (i, j)
    }
}
```

Complexity:

```text
O(N²)
```

But many problems do not require restarting from scratch for every pair or range.

Two Pointers tries to make movement monotonic:

```text
tail ---->

head ---------------->
```

If old positions never need to be revisited, repeated work disappears.

---

# 2. What Is the Two Pointer Technique?

Two Pointers uses one or two indices to explore one or more sequences in a controlled way.

Depending on the form, the pointers may represent:

```text
1. boundaries of the same window
2. opposite ends of one array
3. positions in two different arrays
4. positions moving under different conditions
```

Not every form needs both pointers.

For example, in a fixed-size window, only `head` moves and the left boundary is derived from `K`.

Two important rules:

```text
1. Each pointer has a precise movement condition.
2. Each pointer usually moves only one direction.
```

---

# 3. The Real Idea — Maintain an Invariant

Two Pointers is not just:

```text
declare tail
declare head
```

The real idea is to maintain information that always correctly describes the current state.

That maintained truth is the **invariant**.

Example:

```text
windowSum
=
sum of arr[tail...head]
```

Take:

```text
arr = [2,5,3,7,4]

tail = 1
head = 3
```

ASCII:

```text
index:  0   1   2   3   4
arr:   [2] [5] [3] [7] [4]
           ^-------^
          tail    head

window = [5,3,7]
windowSum = 15
```

If `head` moves to `4`:

```text
windowSum += arr[4]
15 + 4 = 19
```

If `tail` then moves from `1` to `2`:

```text
windowSum -= arr[1]
19 - 5 = 14
tail++
```

We update only what changed.

For every Two Pointer problem, ask:

```text
1. What does each pointer represent?
2. When should each pointer move?
3. What information must remain correct?
```

---

# 4. The Four Forms

```text
TWO POINTERS / SLIDING WINDOW
│
├── Form 0 — Fixed Window
├── Form 1 — Variable Window
├── Form 2 — Divergence / Opposite Ends
└── Form 3 — Multisequence
```

Visual:

```text
                 TWO POINTERS
                      |
      +---------------+----------------+
      |               |                |
      v               v                v
  WINDOW FORMS    OPPOSITE ENDS    MULTISEQUENCE
      |
  +---+---+
  |       |
  v       v
FIXED   VARIABLE
```

---

# 5. Form 0 — Fixed Window

## Recognition Signal

Every considered range has exactly the same size:

```text
K
```

Typical statements:

```text
maximum sum of any K consecutive elements
average of every window of size K
count something in every K-sized segment
```

Only `head` needs to move.

For a window ending at `head`:

```text
start = head - K + 1
```

ASCII:

```text
index:   0   1   2   3   4   5   6
                     [-----------]
                      start    head
```

---

## Dry Run

```text
arr = [2,1,5,1,3,2]
K = 3
```

First window:

```text
[2,1,5]
sum = 8
```

Shift right:

```text
old: [2,1,5]
new:   [1,5,1]
```

Reuse previous work:

```text
newSum
=
oldSum - leaving + entering

=
8 - 2 + 1
=
7
```

Next:

```text
7 - 1 + 3 = 9
```

So the maximum is:

```text
9
```

Brute force:

```text
O(NK)
```

Sliding Window:

```text
O(N)
```

---

## Compact C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, k;
    cin >> n >> k;

    vector<int> arr(n);

    for (int& x : arr) {
        cin >> x;
    }

    long long windowSum = 0;
    long long answer = LLONG_MIN;

    for (int head = 0; head < n; head++) {
        windowSum += arr[head];

        if (head >= k) {
            windowSum -= arr[head - k];
        }

        if (head >= k - 1) {
            answer = max(answer, windowSum);
        }
    }

    cout << answer << '\n';

    return 0;
}
```

---

# 6. Form 1 — Variable Window

## Recognition Signal

The valid range can grow and shrink.

Typical statements:

```text
longest subarray satisfying...
shortest subarray satisfying...
at most K...
sum <= X...
at most K distinct...
```

Pointer meanings:

```text
tail = left boundary
head = right boundary
```

Movement:

```text
head expands
tail shrinks conditionally
```

ASCII:

```text
tail
  |
  v
[ ... current window ... ]
                        ^
                        |
                       head
```

---

## Dry Run

```text
arr = [2,1,3,2,4]
limit = 6
```

Goal:

```text
longest subarray with sum <= 6
```

All numbers are non-negative.

Expand:

```text
head=0 → [2]       sum=2  valid
head=1 → [2,1]     sum=3  valid
head=2 → [2,1,3]   sum=6  valid
head=3 → [2,1,3,2] sum=8  invalid
```

Shrink from left:

```text
remove arr[tail] = 2
```

Now:

```text
[1,3,2]
sum = 6
```

Valid again.

ASCII:

```text
before:
[2,1,3,2]
 ^     ^
tail  head
sum = 8

after shrinking:
  [1,3,2]
   ^   ^
 tail head
sum = 6
```

Invariant when evaluating the answer:

```text
[tail, head] is valid
```

---

## Compact C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    long long limit;

    cin >> n >> limit;

    vector<int> arr(n);

    for (int& x : arr) {
        cin >> x;
    }

    int tail = 0;
    int answer = 0;
    long long windowSum = 0;

    for (int head = 0; head < n; head++) {
        windowSum += arr[head];

        while (tail <= head && windowSum > limit) {
            windowSum -= arr[tail];
            tail++;
        }

        answer = max(answer, head - tail + 1);
    }

    cout << answer << '\n';

    return 0;
}
```

---

# 7. Form 2 — Divergence / Opposite Ends

## Recognition Signal

A sorted or ordered sequence allows one comparison to eliminate one side.

Typical problems:

```text
pair sum in sorted array
two-sum sorted
closest pair
palindrome-style opposite-end scans
```

Initial positions:

```text
tail = left end
head = right end
```

ASCII:

```text
tail                         head
 |                             |
 v                             v
[1,3,4,6,8,10,15,20]
```

---

## Dry Run — Pair Sum

```text
arr = [1,3,4,6,8,10]
target = 14
```

Start:

```text
1 + 10 = 11
```

Too small.

Because the array is sorted, moving `head` left would only make the sum smaller.

So:

```text
tail++
```

Now:

```text
3 + 10 = 13
```

Still small:

```text
tail++
```

Now:

```text
4 + 10 = 14
```

Found.

---

## Why the Elimination Is Safe

If:

```text
arr[tail] + arr[head] < target
```

then for every `j <= head`:

```text
arr[tail] + arr[j]
<=
arr[tail] + arr[head]
<
target
```

So current `tail` cannot produce the target with any allowed partner.

Discard it:

```text
tail++
```

---

## Compact C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    long long target;

    cin >> n >> target;

    vector<long long> arr(n);

    for (long long& x : arr) {
        cin >> x;
    }

    int tail = 0;
    int head = n - 1;

    while (tail < head) {
        long long sum = arr[tail] + arr[head];

        if (sum == target) {
            cout << tail << ' ' << head << '\n';
            return 0;
        }

        if (sum < target) {
            tail++;
        } else {
            head--;
        }
    }

    cout << -1 << '\n';

    return 0;
}
```

---

# 8. Form 3 — Multisequence

## Recognition Signal

Two sorted / ordered sequences must be processed together.

Examples:

```text
merge two sorted arrays
intersection of sorted arrays
compare timestamps from two streams
find common values
```

One pointer belongs to each sequence.

```text
A:
tail
 |
 v
[1,3,5,9]

B:
head
 |
 v
[2,3,4,9]
```

---

## Dry Run — Intersection

```text
A = [1,3,5,9]
B = [2,3,4,9]
```

Compare:

```text
1 < 2
```

`1` cannot match anything later in `B`.

So move `A` pointer.

Now:

```text
3 == 3
```

Match → move both.

Later:

```text
5 > 4
```

Move pointer in `B`.

Finally:

```text
9 == 9
```

Answer:

```text
[3,9]
```

If lengths are:

```text
N and M
```

each pointer crosses its own sequence once:

```text
O(N + M)
```

---

## Compact C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<int> a(n);
    vector<int> b(m);

    for (int& x : a) {
        cin >> x;
    }

    for (int& x : b) {
        cin >> x;
    }

    int tail = 0;
    int head = 0;

    vector<int> common;

    while (tail < n && head < m) {
        if (a[tail] == b[head]) {
            common.push_back(a[tail]);
            tail++;
            head++;
        } else if (a[tail] < b[head]) {
            tail++;
        } else {
            head++;
        }
    }

    for (int x : common) {
        cout << x << ' ';
    }

    return 0;
}
```

---

# 9. Comparing the Four Forms

| Form | Initial Pointer Positions | Movement Pattern | Main Recognition Signal |
|---|---|---|---|
| **Form 0: Fixed Window** | Only `head` is used | `head` moves forward; start is derived using `K` | Every considered range has the same size |
| **Form 1: Variable Window** | `tail` and `head` usually start at beginning | `head` expands; `tail` shrinks conditionally | Valid range can grow or shrink |
| **Form 2: Divergence** | `tail` at left end, `head` at right end | Move toward each other | Comparison safely eliminates one side |
| **Form 3: Multisequence** | One pointer in each sequence | Advance `tail`, `head`, or both | Two sequences must be processed together |

ASCII comparison:

```text
FORM 0 — FIXED

[----K----] →
```

```text
FORM 1 — VARIABLE

tail → [ current window ] ← head expands
```

```text
FORM 2 — DIVERGENCE

tail →                 ← head
```

```text
FORM 3 — MULTISEQUENCE

A: pointer →
B: pointer →
```

---

# 10. Why Two Pointers Can Be O(N)

Nested loops do not automatically mean `O(N²)`.

Example:

```cpp
for (int head = 0; head < n; head++) {
    while (tail <= head && condition) {
        tail++;
    }
}
```

`head` moves at most:

```text
N times
```

`tail` also moves at most:

```text
N times
```

Total:

```text
N + N
=
2N
=
O(N)
```

This is amortized analysis.

Important:

```text
Two Pointers is not automatically O(N).
```

Linear complexity depends on:

```text
1. monotonic pointer movement
2. no repeated backward traversal
3. constant-time or small work per movement
```

---

# 11. Sliding Window Monotonicity Requirement

Variable windows do not work for every subarray problem.

Movement must change validity predictably.

## Good Case — Non-Negative Numbers

If:

```text
arr[i] >= 0
```

then expanding a window never decreases its sum.

So if:

```text
sum > limit
```

shrinking from the left predictably reduces or preserves the sum.

This supports sliding window.

---

## Bad Case — Negative Numbers

Example:

```text
arr = [5,-10,8]
```

As we expand:

```text
[5]       sum = 5
[5,-10]   sum = -5
[5,-10,8] sum = 3
```

The sum can move both directions.

Validity may become:

```text
valid → invalid → valid
```

unpredictably.

Standard variable sliding window can fail.

Before using Form 1, ask:

```text
If head moves right, does validity change predictably?

If invalid, does moving tail right predictably help?
```

If not, consider:

```text
prefix sums
binary search
hash map
deque
ordered structures
```

---

# 12. Inclusive Window Math

These notes use inclusive boundaries:

```text
[tail, head]
```

Window length:

```text
head - tail + 1
```

Example:

```text
tail = 2
head = 5

indices:
2,3,4,5
```

Count:

```text
5 - 2 + 1 = 4
```

The `+1` is essential.

---

# 13. Correct Update Order

If invariant is:

```text
windowSum = sum(arr[tail...head])
```

When `head` enters:

```cpp
windowSum += arr[head];
```

When `tail` leaves:

```cpp
windowSum -= arr[tail];
tail++;
```

Wrong:

```cpp
tail++;
windowSum -= arr[tail];
```

because that removes the new tail instead of the element that actually left.

Tiny example:

```text
arr = [5,2,3]

window = [5,2,3]
sum = 10
```

Remove left element:

```text
10 - 5 = 5
```

New window:

```text
[2,3]
```

Correct.

---

# 14. Problem-Solving Checklist

## Step 1 — Write the brute force

Ask:

```text
What repeated work does the O(N²) or O(NK) solution perform?
```

## Step 2 — Identify the form

```text
same-size range?
→ Form 0

range grows/shrinks?
→ Form 1

opposite ends?
→ Form 2

multiple sequences?
→ Form 3
```

## Step 3 — Define pointer meaning

Example:

```text
tail = left boundary of current valid window
head = right boundary of current valid window
```

## Step 4 — Define the invariant

Examples:

```text
window sum
frequency table
distinct count
zero count
mismatch count
current pair sum
```

## Step 5 — Define movement rules

Example:

```text
head moves every step

while invalid:
    remove arr[tail]
    tail++
```

Or:

```text
if pair sum < target:
    tail++
else:
    head--
```

## Step 6 — Prove complexity

Count pointer movements.

Do not say:

```text
nested loop = O(N²)
```

without checking whether inner-pointer movement is globally bounded.

---

# 15. Compact C++17 Templates

## Form 0 — Fixed Window

```cpp
long long state = 0;

for (int head = 0; head < n; head++) {
    state += arr[head];

    if (head >= k) {
        state -= arr[head - k];
    }

    if (head >= k - 1) {
        // process current window
    }
}
```

---

## Form 1 — Variable Window

```cpp
int tail = 0;

for (int head = 0; head < n; head++) {
    add(arr[head]);

    while (tail <= head && !valid()) {
        remove(arr[tail]);
        tail++;
    }

    // [tail, head] is valid here
}
```

---

## Form 2 — Divergence

```cpp
int tail = 0;
int head = n - 1;

while (tail < head) {
    if (found()) {
        break;
    }

    if (moveTail()) {
        tail++;
    } else {
        head--;
    }
}
```

---

## Form 3 — Multisequence

```cpp
int tail = 0;
int head = 0;

while (tail < n && head < m) {
    if (a[tail] == b[head]) {
        tail++;
        head++;
    } else if (a[tail] < b[head]) {
        tail++;
    } else {
        head++;
    }
}
```

---

# 16. Common Mistakes

## Mistake 1 — Mixing Inclusive and Exclusive Boundaries

For:

```text
[tail, head]
```

length is:

```text
head - tail + 1
```

---

## Mistake 2 — Updating State in the Wrong Order

Correct shrinking:

```cpp
remove(arr[tail]);
tail++;
```

---

## Mistake 3 — Moving Both Pointers Without Proof

Every movement needs a reason.

Ask:

```text
Why can the discarded position never produce a better answer?
```

---

## Mistake 4 — Using Variable Window Without Monotonicity

Negative numbers and non-local conditions can break the usual sliding-window logic.

---

## Mistake 5 — Recomputing the Entire Window

Update only the entering / leaving contribution.

---

## Mistake 6 — Assuming Nested Loops Mean O(N²)

If `tail` only moves forward, its total movement may still be `O(N)`.

---

## Mistake 7 — Forgetting Edge Cases

Always consider:

```text
n = 0
n = 1
no valid answer
whole sequence valid
duplicates
k = 0
k = n
```

---

# 17. Recognition Cheat Sheet

## Form 0 — Fixed

Look for:

```text
exactly K consecutive elements
window of size K
every range has the same length
```

Think:

```text
Fixed Sliding Window
```

---

## Form 1 — Variable

Look for:

```text
longest / shortest subarray
at most K
sum <= X
at most K distinct
```

and monotonic validity.

Think:

```text
Variable Sliding Window
```

---

## Form 2 — Divergence

Look for:

```text
sorted array
pair target
closest sum
left/right comparison
```

Think:

```text
Opposite-End Two Pointers
```

---

## Form 3 — Multisequence

Look for:

```text
two sorted arrays
two streams
merge
intersection
compare current elements
```

Think:

```text
Multisequence Two Pointers
```

---

# 18. Final Mental Model

```text
                   TWO POINTERS
                        |
        +---------------+----------------+
        |               |                |
        v               v                v
      WINDOW        OPPOSITE ENDS     MULTISEQUENCE
        |
    +---+---+
    |       |
    v       v
  FIXED   VARIABLE
```

Most important principle:

```text
Two Pointers
=
monotonic movement
+
invariant
+
safe elimination / reuse of work
```

Before coding, answer:

```text
What does tail mean?

What does head mean?

When does each move?

What invariant stays correct?

Why is discarded work never needed again?
```

---

# 19. Course Roadmap

Study in this order:

```text
1. Form 0 — Fixed Sliding Window
   Maintain every exact-size K range.

2. Form 1 — Variable Sliding Window
   Expand and shrink while maintaining validity.

3. Form 2 — Divergence
   Start at opposite ends and eliminate safely.

4. Form 3 — Multisequence
   Coordinate pointers across two or more sequences.
```

Do not memorize isolated solutions.

Memorize:

```text
movement pattern
+
invariant
+
proof of movement
```

Once these are clear, many Codeforces and LeetCode problems become variations of the same four forms.
