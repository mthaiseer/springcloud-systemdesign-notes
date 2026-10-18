# TLE Eliminators — Two Pointers Level 3: Problem Solving 2
## Kefa and Company, Enduring Exodus, and Xenia and Colorful Gems
### TLE-Style Self-Study Notes

> **Source note:** the supplied TLE PDF is a short problem sheet containing the three problem names plus Codeforces problem/solution links.  
> The prerequisite explanations, proofs, dry runs, and clean C++ below are **teaching expansions** built around those listed problems rather than a verbatim lecture transcript.
>
> The three problems train three different advanced Two Pointer ideas:
>
> ```text
> Problem 1 — Kefa and Company
>             Sort + Type-1 variable window + running sum
>
> Problem 2 — Enduring Exodus
>             Sliding window over selected free rooms
>             + a second monotonic pointer inside the window
>
> Problem 3 — Xenia and Colorful Gems
>             Three sorted sequences
>             + fix the middle value
>             + nearest candidate on both sides
> ```

---

# Table of Contents

- [1. Lecture Roadmap](#1-lecture-roadmap)
- [2. Prerequisite 1 — Sort First, Then Use Two Pointers](#2-prerequisite-1--sort-first-then-use-two-pointers)
- [3. Prerequisite 2 — Type-1 Maximum-Value Window](#3-prerequisite-2--type-1-maximum-value-window)
- [4. Prerequisite 3 — Why Pointer Monotonicity Matters](#4-prerequisite-3--why-pointer-monotonicity-matters)
- [5. Problem 1 — Kefa and Company](#5-problem-1--kefa-and-company)
- [6. Story Removed / Mathematical Form](#6-story-removed--mathematical-form)
- [7. Why Sorting Reveals a Window](#7-why-sorting-reveals-a-window)
- [8. Detailed Dry Run — Kefa and Company](#8-detailed-dry-run--kefa-and-company)
- [9. C++17 — Kefa and Company](#9-c17--kefa-and-company)
- [10. Prerequisite 4 — Sliding a Fixed Number of Candidates](#10-prerequisite-4--sliding-a-fixed-number-of-candidates)
- [11. Prerequisite 5 — Minimize Maximum Distance to Two Extremes](#11-prerequisite-5--minimize-maximum-distance-to-two-extremes)
- [12. Problem 2 — Enduring Exodus](#12-problem-2--enduring-exodus)
- [13. Why the Selected Empty Rooms Can Be Consecutive](#13-why-the-selected-empty-rooms-can-be-consecutive)
- [14. Why the Farmer Pointer Moves Only Right](#14-why-the-farmer-pointer-moves-only-right)
- [15. Detailed Dry Run — Enduring Exodus](#15-detailed-dry-run--enduring-exodus)
- [16. C++17 — Enduring Exodus](#16-c17--enduring-exodus)
- [17. Simpler Alternative — Binary Search Inside Each Window](#17-simpler-alternative--binary-search-inside-each-window)
- [18. Prerequisite 6 — Three Sorted Sequences](#18-prerequisite-6--three-sorted-sequences)
- [19. Prerequisite 7 — Fix the Middle Value](#19-prerequisite-7--fix-the-middle-value)
- [20. Problem 3 — Xenia and Colorful Gems](#20-problem-3--xenia-and-colorful-gems)
- [21. Why the Nearest Value on Each Side Is Best](#21-why-the-nearest-value-on-each-side-is-best)
- [22. Why We Need Six Orderings](#22-why-we-need-six-orderings)
- [23. Detailed Dry Run — Xenia and Colorful Gems](#23-detailed-dry-run--xenia-and-colorful-gems)
- [24. C++17 — Xenia and Colorful Gems](#24-c17--xenia-and-colorful-gems)
- [25. Binary-Search Alternative for Xenia](#25-binary-search-alternative-for-xenia)
- [26. Complexity Comparison](#26-complexity-comparison)
- [27. Common Mistakes](#27-common-mistakes)
- [28. Recognition Table](#28-recognition-table)
- [29. Final Mental Model](#29-final-mental-model)

---

# 1. Lecture Roadmap

The source PDF lists three Level-3 problems:

```text
1. Kefa and Company
2. Enduring Exodus
3. Xenia and Colorful Gems
```

They are useful because they show that "Two Pointers" is not one fixed template.

The common idea is:

```text
SORT / ORDER THE CANDIDATES
        ↓
FIND A MONOTONIC BOUNDARY
        ↓
REUSE THE PREVIOUS POINTER POSITION
```

The state is different in each problem:

```text
Kefa
→ current valid money interval + friendship sum

Enduring Exodus
→ current block of K+1 free rooms + best Farmer position

Xenia
→ current middle gem + closest lower/higher candidates
```

---

# 2. Prerequisite 1 — Sort First, Then Use Two Pointers

Many pair/range problems do not look monotonic in their original order.

Sorting can transform:

```text
arbitrary candidates
```

into:

```text
ordered candidates
```

Then pointer movement becomes provable.

Example:

```text
money = [10, 2, 8, 4]
```

Original order:

```text
10,2,8,4
```

does not give a useful interval.

Sort by money:

```text
2,4,8,10
```

Now a condition like:

```text
maxMoney - minMoney < D
```

depends only on the two window endpoints.

That immediately suggests:

```text
left / right sliding window
```

---

# 3. Prerequisite 2 — Type-1 Maximum-Value Window

A common Level-3 form is:

```text
condition is valid for [left,right]

and every smaller enclosed window is also valid
```

Then use:

```cpp
int left = 0;

for (int right = 0; right < n; right++) {
    add(arr[right]);

    while (!valid()) {
        remove(arr[left]);
        left++;
    }

    answer = max(answer, currentValue);
}
```

Mental flow:

```text
EXPAND RIGHT
     ↓
INVALID?
     ↓
SHRINK LEFT until valid
     ↓
USE CURRENT WINDOW
```

This is exactly the basis of **Kefa and Company**.

---

# 4. Prerequisite 3 — Why Pointer Monotonicity Matters

A Two Pointer optimization works when a pointer never needs to go backward.

Example:

```text
right increases
```

and the smallest valid `left` can only:

```text
stay the same
or
move right
```

Then:

```text
left total moves <= N
right total moves <= N
```

So after sorting:

```text
O(N)
```

pointer work is possible.

This same idea appears in Problem 2 and Problem 3, but with different pointer meanings.

---

# 5. Problem 1 — Kefa and Company

### 🔗 Problem Link

[Codeforces 580B — Kefa and Company](https://codeforces.com/problemset/problem/580/B)

### 🔗 TLE Sheet Solution Link

[Codeforces Submission 299756191](https://codeforces.com/contest/580/submission/299756191)

---

## What It Asks

Each friend has:

```text
money[i]
friendship[i]
```

Choose a group of friends such that no one feels poor.

A friend feels poor if another selected friend has at least:

```text
D
```

more money.

Therefore a selected group must satisfy:

```text
maxMoney - minMoney < D
```

Goal:

```text
maximize total friendship
```

---

# 6. Story Removed / Mathematical Form

We have pairs:

```text
(money, friendship)
```

Choose a set satisfying:

```text
maxMoney - minMoney < D
```

and maximize:

```text
sum(friendship)
```

Because friendship values are non-negative, once we know a valid money interval:

```text
take every friend inside it
```

There is no benefit in excluding someone whose friendship contribution is non-negative.

So the problem becomes:

```text
sort by money

find the valid money window
with maximum friendship sum
```

---

# 7. Why Sorting Reveals a Window

After sorting:

```text
friends[left].money
<=
...
<=
friends[right].money
```

For the window:

```text
[left,right]
```

the largest money difference is exactly:

```text
friends[right].money - friends[left].money
```

Valid iff:

```text
friends[right].money - friends[left].money < D
```

If invalid:

```text
difference >= D
```

Moving `right` farther right can only keep/increase the difference.

Therefore the only repairing move is:

```text
left++
```

This is classic Type 1.

---

## Window State

Maintain:

```text
currentFriendship
```

When `right` enters:

```cpp
currentFriendship += friends[right].friendship;
```

When `left` leaves:

```cpp
currentFriendship -= friends[left].friendship;
```

After restoring validity:

```cpp
answer = max(answer, currentFriendship);
```

---

# 8. Detailed Dry Run — Kefa and Company

Teaching example:

```text
D = 5

friends:
(money, friendship)

(1,5)
(3,4)
(6,10)
(7,2)
```

Already sorted.

Start:

```text
left=0
sum=0
answer=0
```

Trace:

```text
right=0 → add (1,5)
money diff = 1-1 = 0 <5
sum=5
answer=5

right=1 → add (3,4)
money diff = 3-1 = 2 <5
sum=9
answer=9

right=2 → add (6,10)
money diff = 6-1 = 5
INVALID because condition needs <5

remove left (1,5)
left=1
sum=14

money diff = 6-3 = 3 <5
VALID
answer=14

right=3 → add (7,2)
money diff = 7-3 = 4 <5
sum=16
answer=16
```

Final:

```text
16
```

---

# 9. C++17 — Kefa and Company

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    ll d;
    cin >> n >> d;

    vector<pair<ll, ll>> friends(n);

    for (auto& [money, friendship] : friends) {
        cin >> money >> friendship;
    }

    sort(friends.begin(), friends.end());

    int left = 0;

    ll currentFriendship = 0;
    ll answer = 0;

    for (int right = 0; right < n; right++) {
        currentFriendship += friends[right].second;

        while (friends[right].first - friends[left].first >= d) {
            currentFriendship -= friends[left].second;
            left++;
        }

        answer = max(answer, currentFriendship);
    }

    cout << answer << '\n';

    return 0;
}
```

Complexity:

```text
sorting = O(N log N)
window  = O(N)

total   = O(N log N)
space   = O(N)
```

---

# 10. Prerequisite 4 — Sliding a Fixed Number of Candidates

Sometimes the "window" is not:

```text
K consecutive array positions
```

but:

```text
K consecutive valid candidates
```

Example:

```text
hotel string:
0 1 0 0 1 0 0
```

Only free rooms matter.

Extract them:

```text
freeRooms = [0,2,3,5,6]
```

Now choosing:

```text
K+1 free rooms
```

becomes a normal fixed-length window over:

```text
freeRooms
```

This transformation is important in **Enduring Exodus**.

---

# 11. Prerequisite 5 — Minimize Maximum Distance to Two Extremes

Suppose selected rooms range from:

```text
L
```

to:

```text
R
```

Farmer stays at:

```text
P
```

where:

```text
L <= P <= R
```

Farthest distance:

```text
max(P-L, R-P)
```

ASCII:

```text
L -------- P -------- R

left distance  = P-L
right distance = R-P

score = max(left distance, right distance)
```

To minimize the maximum:

```text
P should be as balanced between L and R as possible
```

But `P` must itself be one of the available free rooms.

So we choose the available room closest to the balance point.

---

# 12. Problem 2 — Enduring Exodus

### 🔗 Problem Link

[Codeforces 645C — Enduring Exodus](https://codeforces.com/contest/645/problem/C)

### 🔗 TLE Sheet Solution Link

[Codeforces Submission 299756300](https://codeforces.com/contest/645/submission/299756300)

---

## What It Asks

Hotel has:

```text
N rooms
```

String:

```text
'0' = free
'1' = occupied
```

Farmer John and `K` cows need:

```text
K+1 free rooms
```

Farmer occupies one of the chosen free rooms.

Goal:

```text
minimize the maximum distance
from Farmer's room to any cow
```

---

# 13. Why the Selected Empty Rooms Can Be Consecutive

List all free rooms in sorted order:

```text
freeRooms[0], freeRooms[1], ...
```

Suppose an optimal choice skipped a free room between two selected free rooms.

Then that skipped room lies inside the chosen span.

Replacing a farther selected room by an interior skipped room cannot increase the range of selected positions.

Therefore an optimal solution exists among:

```text
K+1 consecutive entries
in the sorted free-room list
```

So we only need windows:

```text
freeRooms[left ... left+K]
```

This is the first pointer layer.

---

# 14. Why the Farmer Pointer Moves Only Right

For one free-room window:

```text
L = freeRooms[left]
R = freeRooms[right]
```

Farmer candidate index:

```text
center
```

Score:

```text
max(
    freeRooms[center] - L,
    R - freeRooms[center]
)
```

As the whole window slides right:

```text
L never decreases
R never decreases
```

The best balancing position can therefore stay in place or move right.

It never needs to move left.

So we maintain a second monotonic pointer:

```text
center
```

For each window, compare:

```text
current center score
vs
next center score
```

If the next free room gives a smaller maximum distance:

```text
center++
```

Continue until moving right no longer improves the score.

This gives linear work after extracting the free-room positions.

---

# 15. Detailed Dry Run — Enduring Exodus

Use the official sample:

```text
N = 7
K = 2

rooms = "0100100"
```

Free positions using 0-based indexing:

```text
[0,2,3,5,6]
```

Need:

```text
K+1 = 3
```

free rooms.

---

## Window 1

```text
[0,2,3]
```

Try Farmer at position `0`:

```text
max(0-0, 3-0)
=
3
```

Move Farmer to `2`:

```text
max(2-0, 3-2)
=
max(2,1)
=
2
```

Better.

Moving to `3`:

```text
max(3-0, 3-3)
=
3
```

Worse.

Best for this window:

```text
2
```

---

## Window 2

```text
[2,3,5]
```

Previous Farmer pointer is already around position `2`.

Try:

```text
Farmer=2
score=max(0,3)=3

Farmer=3
score=max(1,2)=2
```

Move right.

Try `5`:

```text
score=max(3,0)=3
```

Stop.

Best:

```text
2
```

---

## Window 3

```text
[3,5,6]
```

Best Farmer:

```text
5
```

Score:

```text
max(5-3, 6-5)
=
2
```

Final answer:

```text
2
```

---

# 16. C++17 — Enduring Exodus

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, k;
    cin >> n >> k;

    string rooms;
    cin >> rooms;

    vector<int> freeRooms;

    for (int i = 0; i < n; i++) {
        if (rooms[i] == '0') {
            freeRooms.push_back(i);
        }
    }

    int freeCount = freeRooms.size();
    int center = 0;
    int answer = n;

    for (int left = 0; left + k < freeCount; left++) {
        int right = left + k;

        center = max(center, left);

        while (center + 1 <= right) {
            int currentScore = max(
                freeRooms[center] - freeRooms[left],
                freeRooms[right] - freeRooms[center]
            );

            int nextScore = max(
                freeRooms[center + 1] - freeRooms[left],
                freeRooms[right] - freeRooms[center + 1]
            );

            if (nextScore >= currentScore) {
                break;
            }

            center++;
        }

        int currentScore = max(
            freeRooms[center] - freeRooms[left],
            freeRooms[right] - freeRooms[center]
        );

        answer = min(answer, currentScore);
    }

    cout << answer << '\n';

    return 0;
}
```

Complexity:

```text
extract free rooms = O(N)

left pointer        = O(number of free rooms)
center pointer      = O(number of free rooms) total

total               = O(N)
extra space         = O(number of free rooms)
```

The important point:

```text
center does NOT restart for every window
```

That is the Two Pointer optimization.

---

# 17. Simpler Alternative — Binary Search Inside Each Window

A simpler but slightly slower approach is:

```text
for every K+1 free-room window:
    target balance point ≈ (L+R)/2
    binary-search nearest free room inside the window
```

Complexity:

```text
O(N log N)
```

The Level-3 Two Pointer insight is stronger:

```text
the optimal center moves monotonically
```

so reuse it:

```text
O(N)
```

after building the free-room list.

---

# 18. Prerequisite 6 — Three Sorted Sequences

Suppose we must choose:

```text
one value from A
one value from B
one value from C
```

Brute force:

```text
O(|A| * |B| * |C|)
```

Too large.

If all three are sorted, we can exploit relative order.

For any selected triple, after ordering the values:

```text
low <= middle <= high
```

One color supplies:

```text
low
```

another:

```text
middle
```

and the third:

```text
high
```

This decomposition is the key to **Xenia and Colorful Gems**.

---

# 19. Prerequisite 7 — Fix the Middle Value

Suppose we know:

```text
x <= y <= z
```

and fix `y`.

To minimize pairwise squared differences, we want:

```text
x as large as possible while x <= y
z as small as possible while z >= y
```

So:

```text
x = closest candidate from the left
z = closest candidate from the right
```

After sorting, these candidates move monotonically as `y` increases.

That is the Two Pointer opportunity.

---

# 20. Problem 3 — Xenia and Colorful Gems

### 🔗 Problem Link

[Codeforces 1336B — Xenia and Colorful Gems](https://codeforces.com/problemset/problem/1336/B)

### 🔗 TLE Sheet Solution Link

[Codeforces Submission 299756380](https://codeforces.com/contest/1336/submission/299756380)

---

## What It Asks

There are three color groups:

```text
Red
Green
Blue
```

Pick exactly one gem from each group.

If selected weights are:

```text
x, y, z
```

minimize:

```text
(x-y)^2
+
(y-z)^2
+
(z-x)^2
```

---

## Example

From the official problem:

```text
Red   = [7,8]
Green = [6,3]
Blue  = [3,1,4]
```

Choose:

```text
7, 6, 4
```

Then:

```text
(7-6)^2
+
(6-4)^2
+
(4-7)^2

=
1 + 4 + 9

=
14
```

---

# 21. Why the Nearest Value on Each Side Is Best

Suppose:

```text
x <= y <= z
```

and `y` is fixed.

If we replace `x` by a larger valid value:

```text
x' <= y
and
x' > x
```

then both distances:

```text
y-x'
z-x'
```

are no larger than before.

So the score cannot get worse.

Therefore use:

```text
largest x <= y
```

Similarly, replacing `z` by a smaller valid value:

```text
z' >= y
and
z' < z
```

shrinks the relevant differences.

Therefore use:

```text
smallest z >= y
```

So for fixed middle `y`:

```text
best lower candidate
=
largest value <= y

best upper candidate
=
smallest value >= y
```

---

# 22. Why We Need Six Orderings

We have three colors:

```text
R, G, B
```

But the numerical order of the chosen weights is unknown.

Any color can be:

```text
LOW
MIDDLE
HIGH
```

Number of assignments:

```text
3! = 6
```

So evaluate:

```text
(R,G,B)
(R,B,G)
(G,R,B)
(G,B,R)
(B,R,G)
(B,G,R)
```

where each tuple means:

```text
(low-array, middle-array, high-array)
```

This guarantees that whichever numerical ordering the optimal triple has, one of the six passes considers it in the correct order.

---

# 23. Detailed Dry Run — Xenia and Colorful Gems

Use:

```text
R = [7,8]
G = [3,6]
B = [1,3,4]
```

Consider ordering:

```text
low    = B
middle = G
high   = R
```

All are sorted.

---

## middle y = 3

Largest `B <= 3`:

```text
x = 3
```

Smallest `R >= 3`:

```text
z = 7
```

Score:

```text
(3-3)^2
+
(3-7)^2
+
(7-3)^2

=
0 + 16 + 16

=
32
```

---

## middle y = 6

Low pointer moves right until:

```text
largest B <= 6
```

So:

```text
x = 4
```

High pointer stays at:

```text
z = 7
```

Score:

```text
(4-6)^2
+
(6-7)^2
+
(7-4)^2

=
4 + 1 + 9

=
14
```

So this ordering finds:

```text
14
```

which is optimal.

Notice:

```text
the low pointer only moved right
the high pointer only moved right
```

as middle values increased.

---

# 24. C++17 — Xenia and Colorful Gems

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll score(ll x, ll y, ll z) {
    return (x - y) * (x - y)
         + (y - z) * (y - z)
         + (z - x) * (z - x);
}

ll evaluateOrder(
    const vector<ll>& low,
    const vector<ll>& middle,
    const vector<ll>& high) {

    int lowPointer = -1;
    int highPointer = 0;

    ll answer = LLONG_MAX;

    for (ll middleValue : middle) {
        while (lowPointer + 1 < int(low.size()) &&
               low[lowPointer + 1] <= middleValue) {
            lowPointer++;
        }

        while (highPointer < int(high.size()) &&
               high[highPointer] < middleValue) {
            highPointer++;
        }

        if (lowPointer >= 0 && highPointer < int(high.size())) {
            answer = min(
                answer,
                score(
                    low[lowPointer],
                    middleValue,
                    high[highPointer]
                )
            );
        }
    }

    return answer;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;

    while (t--) {
        int nr, ng, nb;
        cin >> nr >> ng >> nb;

        vector<ll> red(nr);
        vector<ll> green(ng);
        vector<ll> blue(nb);

        for (ll& value : red) {
            cin >> value;
        }

        for (ll& value : green) {
            cin >> value;
        }

        for (ll& value : blue) {
            cin >> value;
        }

        sort(red.begin(), red.end());
        sort(green.begin(), green.end());
        sort(blue.begin(), blue.end());

        ll answer = LLONG_MAX;

        answer = min(answer, evaluateOrder(red, green, blue));
        answer = min(answer, evaluateOrder(red, blue, green));

        answer = min(answer, evaluateOrder(green, red, blue));
        answer = min(answer, evaluateOrder(green, blue, red));

        answer = min(answer, evaluateOrder(blue, red, green));
        answer = min(answer, evaluateOrder(blue, green, red));

        cout << answer << '\n';
    }

    return 0;
}
```

---

## Why This Is Still Efficient

For one ordering:

```text
middle pointer scans middle once
lowPointer scans low once
highPointer scans high once
```

So:

```text
O(|low| + |middle| + |high|)
```

There are only six orderings:

```text
constant factor 6
```

Sorting dominates:

```text
O(N log N)
```

over the total gem counts.

---

# 25. Binary-Search Alternative for Xenia

A very common solution is:

For each fixed middle value `y`:

```text
in LOW:
find largest value <= y

in HIGH:
find smallest value >= y
```

using:

```cpp
upper_bound(...)
lower_bound(...)
```

Then evaluate all six orderings.

This gives:

```text
O(N log N)
```

after sorting.

The Two Pointer version exploits the fact that `y` is processed in sorted order:

```text
candidate positions never need to move backward
```

So candidate lookup can be linear per ordering.

---

# 26. Complexity Comparison

| Problem | Main Technique | Time |
|---|---|---:|
| Kefa and Company | sort + variable window | `O(N log N)` |
| Enduring Exodus | free-room window + monotonic center pointer | `O(N)` |
| Xenia and Colorful Gems | sort + six ordered three-sequence sweeps | `O(N log N)` overall |

---

# 27. Common Mistakes

## Mistake 1 — Kefa Uses `<= D`

Wrong validity:

```text
maxMoney - minMoney <= D
```

The statement says a friend feels poor when someone has **at least D more**.

So valid condition is strictly:

```text
maxMoney - minMoney < D
```

Shrink while:

```cpp
difference >= d
```

---

## Mistake 2 — Kefa Recomputes Friendship Sum

Do not sum the whole window every time.

Maintain:

```text
ADD right friendship
REMOVE left friendship
```

---

## Mistake 3 — Enduring Exodus Uses Arbitrary Free-Room Subsets

An optimal choice can be restricted to:

```text
K+1 consecutive free rooms
```

after extracting and sorting free positions.

---

## Mistake 4 — Restart the Farmer Pointer for Every Window

That can make the solution unnecessarily slow.

The Level-3 insight is:

```text
optimal Farmer position moves only right
as the room window moves right
```

Reuse it.

---

## Mistake 5 — Enduring Exodus Chooses the Numerical Midpoint Directly

Farmer must stay in an actual free room.

The ideal geometric midpoint may be occupied.

Choose the free-room candidate that minimizes:

```text
max(P-L, R-P)
```

---

## Mistake 6 — Xenia Checks Only One Color Ordering

The optimal selected values may have any color as:

```text
low
middle
high
```

Check all:

```text
6 permutations
```

---

## Mistake 7 — Xenia Uses Arbitrary Neighbors

For fixed middle:

```text
lower side → largest <= middle
upper side → smallest >= middle
```

These are the only candidates needed for that numerical ordering.

---

## Mistake 8 — Overflow in Xenia

Weights can be large, and values are squared.

Use:

```cpp
long long
```

for:

```text
differences
products
answer
```

---

# 28. Recognition Table

| Statement Signal | Pattern |
|---|---|
| Maximize sum inside a sorted numeric-gap constraint | Sort + Type-1 window |
| Validity depends only on max-min after sorting | Endpoint window |
| Select exactly K ordered candidates | Fixed window over filtered positions |
| A best inner point changes monotonically as outer window slides | Extra moving pointer |
| Pick one value from each of 3 sorted groups | Multisequence reasoning |
| Objective prefers three values close together | Fix middle + nearest lower/upper |
| Candidate lookup boundary moves only right | Replace repeated binary search with pointer |

---

# 29. Final Mental Model

These three problems teach three increasingly subtle Two Pointer forms.

---

## Kefa and Company

```text
SORT BY MONEY
      ↓
[right money - left money < D]
      ↓
maintain friendship sum
      ↓
shrink invalid left
      ↓
maximize current sum
```

Master code shape:

```cpp
for (int right = 0; right < n; right++) {
    add(right);

    while (!valid()) {
        remove(left);
        left++;
    }

    answer = max(answer, currentValue);
}
```

---

## Enduring Exodus

```text
FILTER FREE ROOMS
      ↓
window of K+1 consecutive free positions
      ↓
for each window:
minimize max(P-L, R-P)
      ↓
best P only moves right
      ↓
reuse center pointer
```

This is:

```text
WINDOW POINTERS
+
INNER MONOTONIC POINTER
```

---

## Xenia and Colorful Gems

```text
SORT 3 ARRAYS
      ↓
choose numerical ordering:
LOW <= MIDDLE <= HIGH
      ↓
fix MIDDLE
      ↓
closest LOW from left
closest HIGH from right
      ↓
both candidate pointers move monotonically
      ↓
check all 6 color orderings
```

---

## 60-Second Recognition

```text
After sorting,
does the useful boundary only move one way?

YES
→ Two Pointers may replace repeated searching.
```

Then ask:

```text
What does each pointer mean?

What lets me prove it never moves backward?

What state must I update when the pointer moves?
```

That is the Level-3 Two Pointers skill these three problems are training.
