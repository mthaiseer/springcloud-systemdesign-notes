# Two Pointers — Form 3: Multisequence Type
## Subsequence Matching, Sorted-List Intersection, and K-Sequence Smallest Range
### TLE-Style Self-Study Notes

> **Source:** supplied TLE/AlgoZenith Form 3 lecture screenshots.
>
> Form 3 is different from Form 0, Form 1, and Form 2:
>
> ```text
> pointers do NOT represent the two boundaries of one window
>
> instead:
> each pointer belongs to a different sequence
> ```
>
> Core idea:
>
> ```text
> compare the current unprocessed elements
> → decide which sequence can safely advance
> → never move a pointer backward
> ```
>
> The lecture develops three reusable applications:
>
> ```text
> 1. Is T a subsequence of S?
> 2. Intersect sorted posting lists / documents containing all query words
> 3. Minimum range using one value from every sorted row
> ```

---

# Table of Contents

- [1. What Form 3 Means](#1-what-form-3-means)
- [2. Prerequisite 1 — One Pointer Per Sequence](#2-prerequisite-1--one-pointer-per-sequence)
- [3. Prerequisite 2 — Processed Prefix Invariant](#3-prerequisite-2--processed-prefix-invariant)
- [4. Prerequisite 3 — Why Forward-Only Movement Gives Linear Work](#4-prerequisite-3--why-forward-only-movement-gives-linear-work)
- [5. Core Form 3 Template](#5-core-form-3-template)
- [6. Form 2 vs Form 3](#6-form-2-vs-form-3)
- [7. How to Recognize Form 3](#7-how-to-recognize-form-3)
- [8. Problem 1 — Is T a Subsequence of S?](#8-problem-1--is-t-a-subsequence-of-s)
- [9. Prerequisite 4 — What Is a Subsequence?](#9-prerequisite-4--what-is-a-subsequence)
- [10. Greedy Matching Rule](#10-greedy-matching-rule)
- [11. Why the Earliest Match Is Safe](#11-why-the-earliest-match-is-safe)
- [12. Detailed Dry Run — Subsequence](#12-detailed-dry-run--subsequence)
- [13. Proper C++17 — Subsequence Check](#13-proper-c17--subsequence-check)
- [14. Problem 2 — Documents Containing Every Query Word](#14-problem-2--documents-containing-every-query-word)
- [15. Prerequisite 5 — Inverted Index / Posting Lists](#15-prerequisite-5--inverted-index--posting-lists)
- [16. Intersect Two Sorted Lists](#16-intersect-two-sorted-lists)
- [17. Why the Smaller ID Can Be Discarded](#17-why-the-smaller-id-can-be-discarded)
- [18. Detailed Dry Run — Sorted Intersection](#18-detailed-dry-run--sorted-intersection)
- [19. Proper C++17 — Intersect Two Sorted Lists](#19-proper-c17--intersect-two-sorted-lists)
- [20. More Than Two Query Words](#20-more-than-two-query-words)
- [21. Proper C++17 — Documents Containing All Query Words](#21-proper-c17--documents-containing-all-query-words)
- [22. Problem 3 — Minimum Range Using One Element from Every Row](#22-problem-3--minimum-range-using-one-element-from-every-row)
- [23. Prerequisite 6 — Why Sort Every Row?](#23-prerequisite-6--why-sort-every-row)
- [24. K-Sequence Generalization of Form 3](#24-k-sequence-generalization-of-form-3)
- [25. Which Pointer Should Move?](#25-which-pointer-should-move)
- [26. Why Only the Minimum Row Can Improve the Range](#26-why-only-the-minimum-row-can-improve-the-range)
- [27. Min-Heap + Current Maximum](#27-min-heap--current-maximum)
- [28. Detailed Dry Run — Smallest Range](#28-detailed-dry-run--smallest-range)
- [29. Proper C++17 — Minimum Range Across Rows](#29-proper-c17--minimum-range-across-rows)
- [30. Complexity](#30-complexity)
- [31. Two-Sequence and K-Sequence Forms](#31-two-sequence-and-k-sequence-forms)
- [32. Common Mistakes](#32-common-mistakes)
- [33. Recognition Table](#33-recognition-table)
- [34. Final Mental Model](#34-final-mental-model)

---

# 1. What Form 3 Means

In Form 3:

```text
tail belongs to one sequence
head belongs to another sequence
```

Example:

```text
Sequence A:
tail
 |
 v
[a, b, c, d, ...]

Sequence B:
head
 |
 v
[x, y, z, ...]
```

The pointers do not define one common interval.

Each pointer means:

```text
"how much of this sequence has already been processed?"
```

A comparison between current elements decides:

```text
move tail
move head
or
move both
```

---

# 2. Prerequisite 1 — One Pointer Per Sequence

Suppose:

```text
A = [1,3,5,7]
B = [2,3,6,7]
```

We can maintain:

```text
tail = index in A
head = index in B
```

ASCII:

```text
A: [1,3,5,7]
    ^
   tail

B: [2,3,6,7]
    ^
   head
```

Later:

```text
A: [1,3,5,7]
      ^
     tail

B: [2,3,6,7]
      ^
     head
```

Every pointer has its own sequence.

---

# 3. Prerequisite 2 — Processed Prefix Invariant

Central invariant:

```text
Everything before each pointer
has already been fully processed
and will never be needed again.
```

Example:

```text
A: [1,3,5,7]
       ^
      tail
```

Then:

```text
A[0], A[1]
```

have already been handled.

Likewise for the second sequence.

This is why the pointers never move backward.

---

# 4. Prerequisite 3 — Why Forward-Only Movement Gives Linear Work

Suppose:

```text
|A| = N
|B| = M
```

If:

```text
tail moves from 0 to N
head moves from 0 to M
```

then total pointer movements are:

```text
N + M
```

Therefore:

```text
O(N + M)
```

even if the code contains a loop where different pointers advance conditionally.

This is the same amortized idea seen earlier:

```text
each element is processed at most once by its pointer
```

---

# 5. Core Form 3 Template

```cpp
int tail = 0;
int head = 0;

while (tail < int(a.size()) && head < int(b.size())) {
    if (a[tail] == b[head]) {
        // Use the match.
        tail++;
        head++;
    } else if (shouldMoveTail(a[tail], b[head])) {
        tail++;
    } else {
        head++;
    }
}
```

This is only a structural template.

The problem determines:

```text
what equality means
which pointer should move
whether both should move
when the process is complete
```

---

# 6. Form 2 vs Form 3

| Property | Form 2 — Divergence | Form 3 — Multisequence |
|---|---|---|
| Pointer locations | Opposite ends of one sequence | One pointer in each sequence |
| Initial positions | `tail=0`, `head=n-1` | Usually both start at `0` |
| Movement | Toward each other | Independently left-to-right |
| Main question | Which endpoint can be eliminated? | Which sequence must advance? |
| Typical use | Container / sorted pair elimination | Subsequence / intersection / merge |

Mental distinction:

```text
FORM 2:
one sequence
two ends

FORM 3:
multiple sequences
one current position per sequence
```

---

# 7. How to Recognize Form 3

Consider Form 3 when:

```text
1. Two or more sequences must be processed together.

2. Relative order inside each sequence matters.

3. Only the current unprocessed element of each sequence
   is needed for the next decision.

4. A comparison proves that one or more current elements
   can be permanently skipped.

5. No pointer needs to move backward.
```

Common applications:

```text
subsequence matching
intersection of sorted lists
merging sorted sequences
coordinating K sorted sequences
```

---

# 8. Problem 1 — Is T a Subsequence of S?

Problem:

```text
Given strings S and T,
determine whether T occurs in S as a subsequence.
```

Example:

```text
S = "abaccabadaba"
T = "bda"
```

Possible matches:

```text
S[1] = 'b'
S[8] = 'd'
S[9] = 'a'
```

So:

```text
True
```

---

# 9. Prerequisite 4 — What Is a Subsequence?

A subsequence preserves order, but characters do not need to be adjacent.

Example:

```text
S = "abcdef"
```

Valid subsequences:

```text
"ace"
"bdf"
"ad"
```

Not a subsequence:

```text
"ca"
```

because the order was reversed.

This is different from a substring.

Substring:

```text
must be contiguous
```

Subsequence:

```text
must preserve order only
```

---

# 10. Greedy Matching Rule

Use:

```text
tail → scans source S
head → tracks required character in T
```

At each step:

```text
if S[tail] == T[head]
→ match
→ move both

else
→ current source character is useless
→ move tail only
```

The source pointer always advances.

---

## Template

```cpp
while (tail < int(s.size()) && head < int(t.size())) {
    if (s[tail] == t[head]) {
        head++;
    }

    tail++;
}
```

At the end:

```text
head == T.size()
```

means all target characters were matched.

---

# 11. Why the Earliest Match Is Safe

Suppose:

```text
S[tail] == T[head]
```

Why should we match immediately?

Because matching the current target character with the earliest possible source position leaves:

```text
the largest possible suffix of S
```

for the remaining target characters.

If we intentionally skip an available equal character and match a later copy:

```text
we only reduce future choices
```

We never gain more options.

Therefore:

```text
earliest valid match is greedy-safe
```

---

# 12. Detailed Dry Run — Subsequence

Use:

```text
S = "abaccabadaba"
T = "bda"
```

Compact trace:

```text
tail=0 S='a', head=0 need='b' → no match → tail++
tail=1 S='b', head=0 need='b' → MATCH    → tail++, head++
tail=2 S='a', head=1 need='d' → no match → tail++
tail=3 S='c', head=1 need='d' → no match → tail++
tail=4 S='c', head=1 need='d' → no match → tail++
tail=5 S='a', head=1 need='d' → no match → tail++
tail=6 S='b', head=1 need='d' → no match → tail++
tail=7 S='a', head=1 need='d' → no match → tail++
tail=8 S='d', head=1 need='d' → MATCH    → tail++, head++
tail=9 S='a', head=2 need='a' → MATCH    → tail++, head++
```

Now:

```text
head == T.size()
```

So:

```text
True
```

---

# 13. Proper C++17 — Subsequence Check

```cpp
#include <bits/stdc++.h>
using namespace std;

bool isSubsequence(const string& s, const string& t) {
    int tail = 0;
    int head = 0;

    while (tail < int(s.size()) && head < int(t.size())) {
        if (s[tail] == t[head]) {
            head++;
        }

        tail++;
    }

    return head == int(t.size());
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    string s, t;
    cin >> s >> t;

    cout << (isSubsequence(s, t) ? "True" : "False") << '\n';

    return 0;
}
```

Complexity:

```text
tail scans S once

Time  = O(|S|)
Space = O(1)
```

---

# 14. Problem 2 — Documents Containing Every Query Word

Problem:

```text
Given query words and a collection of documents,
return the documents containing every query word.
```

Lecture example:

```text
Word 1 = "Vivek"
Word 2 = "AlgoZenith"
```

Documents:

```text
D1 = "Vivek has been coding DSA"
D2 = "AlgoZenith teaches DSA and CP"
D3 = "AlgoZenith also covers Dev now"
D4 = "Vivek is the founder of AlgoZenith"
D5 = "AlgoZenith is the best thing available"
```

Only:

```text
D4
```

contains both query words.

---

# 15. Prerequisite 5 — Inverted Index / Posting Lists

Instead of scanning every document for every query:

```text
word → sorted document IDs containing that word
```

Example:

```text
Vivek      → [1,4]
AlgoZenith → [2,3,4,5]
DSA        → [1,2]
```

These are called:

```text
posting lists
```

A document contains all query words exactly when its ID occurs in:

```text
the intersection of all required posting lists
```

For:

```text
Vivek
AlgoZenith
```

we need:

```text
[1,4] ∩ [2,3,4,5]
=
[4]
```

So answer:

```text
D4
```

---

# 16. Intersect Two Sorted Lists

Let:

```text
tail → first list
head → second list
```

Three cases:

```text
first[tail] == second[head]
→ common ID
→ record it
→ move both

first[tail] < second[head]
→ move tail

first[tail] > second[head]
→ move head
```

This is a classic Form 3 sorted-sequence pattern.

---

# 17. Why the Smaller ID Can Be Discarded

Suppose:

```text
first[tail] < second[head]
```

The second list is sorted.

Everything at and after `head` is:

```text
>= second[head]
```

Therefore:

```text
first[tail]
```

is smaller than every future candidate in the second list.

It can never match later.

So it is safe to discard:

```cpp
tail++;
```

The other case is symmetric.

---

# 18. Detailed Dry Run — Sorted Intersection

Use:

```text
first  = [1,4]
second = [2,3,4,5]
```

Trace:

```text
1 vs 2 → 1<2 → tail++
4 vs 2 → 4>2 → head++
4 vs 3 → 4>3 → head++
4 vs 4 → MATCH → record 4 → move both
```

Result:

```text
[4]
```

---

# 19. Proper C++17 — Intersect Two Sorted Lists

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> intersectSorted(const vector<int>& first, const vector<int>& second) {
    int tail = 0;
    int head = 0;

    vector<int> intersection;

    while (tail < int(first.size()) && head < int(second.size())) {
        if (first[tail] == second[head]) {
            intersection.push_back(first[tail]);
            tail++;
            head++;
        } else if (first[tail] < second[head]) {
            tail++;
        } else {
            head++;
        }
    }

    return intersection;
}
```

Complexity:

```text
Time  = O(N + M)
Space = O(result)
```

---

# 20. More Than Two Query Words

If query has many words:

```text
word1
word2
word3
...
```

intersect posting lists one at a time.

Example:

```text
answer = posting[word1]

answer = intersection(answer, posting[word2])
answer = intersection(answer, posting[word3])
...
```

If at any point:

```text
answer becomes empty
```

stop early.

No document can satisfy all words.

---

# 21. Proper C++17 — Documents Containing All Query Words

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> intersectSorted(const vector<int>& first, const vector<int>& second) {
    int tail = 0;
    int head = 0;

    vector<int> intersection;

    while (tail < int(first.size()) && head < int(second.size())) {
        if (first[tail] == second[head]) {
            intersection.push_back(first[tail]);
            tail++;
            head++;
        } else if (first[tail] < second[head]) {
            tail++;
        } else {
            head++;
        }
    }

    return intersection;
}

vector<int> documentsContainingAll(
    const vector<string>& queryWords,
    const unordered_map<string, vector<int>>& postingList) {

    if (queryWords.empty()) {
        return {};
    }

    auto firstWord = postingList.find(queryWords[0]);

    if (firstWord == postingList.end()) {
        return {};
    }

    vector<int> answer = firstWord->second;

    for (int i = 1; i < int(queryWords.size()); i++) {
        auto currentWord = postingList.find(queryWords[i]);

        if (currentWord == postingList.end()) {
            return {};
        }

        answer = intersectSorted(answer, currentWord->second);

        if (answer.empty()) {
            break;
        }
    }

    return answer;
}
```

Implementation note from the lecture:

```text
When building the inverted index,
add each document ID only once per word,
even if the word appears multiple times inside that document.
```

---

# 22. Problem 3 — Minimum Range Using One Element from Every Row

Given an:

```text
N × M
```

matrix.

Choose exactly one element from each row.

Let:

```text
Vmax = maximum selected value
Vmin = minimum selected value
```

Minimize:

```text
score = Vmax - Vmin
```

Lecture example:

```text
grid = [
    [1,5,2],
    [10,3,7],
    [4,8,2]
]
```

We may choose:

```text
2 from row 1
3 from row 2
2 from row 3
```

Then:

```text
Vmax = 3
Vmin = 2
```

Score:

```text
3 - 2 = 1
```

Answer:

```text
1
```

---

# 23. Prerequisite 6 — Why Sort Every Row?

Sort each row first.

Example:

```text
Row 1: [1,2,5]
Row 2: [3,7,10]
Row 3: [2,4,8]
```

Now maintain:

```text
one pointer in every row
```

Current selected values might be:

```text
[1,3,2]
```

These define current:

```text
Vmin = 1
Vmax = 3
range = 2
```

Each row pointer can only move:

```text
left → right
```

This turns the problem into a K-sequence generalization of Form 3.

---

# 24. K-Sequence Generalization of Form 3

With two sequences:

```text
two pointers
```

With `K` sorted sequences:

```text
one pointer per sequence
```

State:

```text
row 1 pointer
row 2 pointer
...
row K pointer
```

The current chosen values are exactly the values under all pointers.

Goal:

```text
minimize max(current values) - min(current values)
```

---

# 25. Which Pointer Should Move?

Suppose current values have:

```text
Vmin
Vmax
```

Current score:

```text
Vmax - Vmin
```

To reduce the score, we need:

```text
smaller difference
```

There are two possible directions:

```text
decrease Vmax
or
increase Vmin
```

But row pointers move only forward in sorted rows.

So values can only:

```text
stay same or increase
```

We cannot decrease `Vmax`.

The only possible improvement is:

```text
increase Vmin
```

Therefore:

```text
advance the row currently contributing Vmin
```

This is the core elimination rule.

---

# 26. Why Only the Minimum Row Can Improve the Range

Suppose:

```text
current selected values = [1,3,2]
```

Then:

```text
Vmin = 1
Vmax = 3
range = 2
```

If we move the row containing `3`:

```text
3 → 7
```

new range becomes:

```text
7 - 1 = 6
```

Worse.

If we move the row containing `2`:

```text
2 → 4
```

the minimum is still:

```text
1
```

and max is at least `4`.

So we do not improve the lower endpoint.

But if we move the row containing minimum `1`:

```text
1 → 2
```

selection becomes:

```text
[2,3,2]
```

Now:

```text
Vmin = 2
Vmax = 3
range = 1
```

Improved.

So:

```text
advance the minimum-contributing row
```

is the only meaningful greedy move.

---

# 27. Min-Heap + Current Maximum

We need to repeatedly know:

```text
current minimum value
and which row produced it
```

Use a min-heap storing:

```text
(value, row, position)
```

Also maintain:

```text
currentMaximum
```

At each step:

```text
1. Heap top gives current minimum.
2. Compute range:
       currentMaximum - currentMinimum
3. Update answer.
4. Advance the pointer in the minimum's row.
5. Push the next value from that row.
6. Update currentMaximum.
```

Stop when one row is exhausted.

Why?

Because once one row has no next value:

```text
no future complete selection
with one element from every row
is possible
```

---

# 28. Detailed Dry Run — Smallest Range

Sorted rows:

```text
Row 1: [1,2,5]
Row 2: [3,7,10]
Row 3: [2,4,8]
```

Initial selection:

```text
[1,3,2]
```

So:

```text
Vmin = 1
Vmax = 3
score = 2
```

Minimum `1` comes from Row 1.

Advance Row 1:

```text
1 → 2
```

New selection:

```text
[2,3,2]
```

Now:

```text
Vmin = 2
Vmax = 3
score = 1
```

Update:

```text
answer = 1
```

From here, every next move follows the same rule:

```text
advance the row holding current minimum
```

---

# 29. Proper C++17 — Minimum Range Across Rows

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll minimumRangeScore(vector<vector<int>>& grid) {
    int rows = grid.size();

    if (rows == 0) {
        return 0;
    }

    for (auto& row : grid) {
        if (row.empty()) {
            return 0;
        }

        sort(row.begin(), row.end());
    }

    using State = tuple<int, int, int>;
    priority_queue<State, vector<State>, greater<State>> minHeap;

    int currentMaximum = INT_MIN;

    for (int row = 0; row < rows; row++) {
        minHeap.push({grid[row][0], row, 0});
        currentMaximum = max(currentMaximum, grid[row][0]);
    }

    ll answer = LLONG_MAX;

    while (!minHeap.empty()) {
        auto [currentMinimum, row, position] = minHeap.top();
        minHeap.pop();

        answer = min(answer, 1LL * currentMaximum - currentMinimum);

        int nextPosition = position + 1;

        if (nextPosition == int(grid[row].size())) {
            break;
        }

        int nextValue = grid[row][nextPosition];

        minHeap.push({nextValue, row, nextPosition});
        currentMaximum = max(currentMaximum, nextValue);
    }

    return answer;
}
```

---

# 30. Complexity

Suppose:

```text
N rows
M elements per row
```

Sorting every row:

```text
O(N * M log M)
```

Heap process:

```text
each matrix element
can be pushed/popped at most once
```

Heap size:

```text
N
```

So heap work:

```text
O(NM log N)
```

Total:

```text
O(NM log M + NM log N)
```

Extra heap / pointer space:

```text
O(N)
```

excluding the matrix.

---

# 31. Two-Sequence and K-Sequence Forms

| Variant | State | Movement Decision |
|---|---|---|
| Subsequence matching | One pointer in each string | Always move source pointer; move target on match |
| Sorted-list intersection | One pointer in each list | Move smaller ID; move both on equality |
| Minimum range across rows | One pointer in every sorted row | Advance row contributing current minimum |

The number of pointers can change.

The shared idea remains:

```text
Compare current elements,
use any valid combination,
and permanently advance the sequence
whose current element cannot help later.
```

---

# 32. Common Mistakes

## Mistake 1 — Treating Both Pointers as One Window

Form 3 pointers belong to different sequences.

Do not apply window-length formulas such as:

```text
head-tail+1
```

unless the problem actually defines such a window.

---

## Mistake 2 — Moving a Pointer Without a Proof

In sorted intersection:

```text
smaller value cannot match later
```

In subsequence:

```text
source character already examined
```

In K-range:

```text
only minimum row can possibly improve Vmin
```

Every movement needs a reason.

---

## Mistake 3 — Moving the Target Pointer on a Mismatch in Subsequence

Wrong:

```text
S[tail] != T[head]
→ head++
```

That would skip a required target character.

Correct:

```text
move only source pointer
```

---

## Mistake 4 — Forgetting Sorted Order for Intersection

The rule:

```text
smaller ID can be discarded
```

depends on both lists being sorted.

---

## Mistake 5 — Rescanning Every Document for Every Query

Build:

```text
word → sorted posting list
```

then intersect lists.

---

## Mistake 6 — Moving the Wrong Row in Smallest Range

Current range:

```text
Vmax - Vmin
```

Moving any row not contributing `Vmin` leaves the current minimum unchanged.

The only possible improving move is:

```text
advance minimum row
```

---

## Mistake 7 — Continue After a Row Is Exhausted

Once one row has no next element:

```text
a complete one-element-per-row selection is impossible
```

Stop.

---

# 33. Recognition Table

| Signal | Think |
|---|---|
| Is one string a subsequence of another? | Source pointer + target pointer |
| Two sorted arrays/lists must be intersected | Advance smaller, both on match |
| Merge sorted sequences | One pointer per sequence |
| Query words mapped to sorted document IDs | Posting-list intersection |
| One value required from every sorted row | K-sequence Form 3 |
| Minimize max-min across K sequences | Min-heap + current maximum |
| Current element in one sequence can never match/help later | Advance that sequence |

---

# 34. Final Mental Model

```text
                   FORM 3
              MULTISEQUENCE
                    |
                    v
       one pointer per sequence
                    |
                    v
       compare current elements
                    |
                    v
       which sequence is "behind"
       or no longer useful here?
                    |
                    v
          advance that pointer
                    |
                    v
             never go backward
```

---

## Subsequence

```text
SOURCE S      TARGET T

tail → scans S
head → next required T character

match:
move both

mismatch:
move source only
```

---

## Sorted Intersection

```text
A[tail] < B[head]
→ tail++

A[tail] > B[head]
→ head++

equal
→ record
→ move both
```

---

## K-Sequence Smallest Range

```text
one pointer in every sorted row

current range
=
currentMax - currentMin

only useful move:
advance row containing currentMin
```

---

## 60-Second Recognition

```text
MULTIPLE SEQUENCES
+
ORDER INSIDE EACH MATTERS
+
CURRENT ELEMENT COMPARISON TELLS
WHICH SEQUENCE CAN ADVANCE FOREVER
=
FORM 3
```

The deepest lesson is:

```text
Each pointer remembers progress in its own sequence.

Anything before that pointer is finished forever.
```
