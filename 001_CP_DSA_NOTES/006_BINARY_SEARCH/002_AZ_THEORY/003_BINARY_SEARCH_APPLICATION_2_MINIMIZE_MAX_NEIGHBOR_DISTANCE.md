# AlgoZenith — Binary Search Application 2
## Insert K Points to Minimize the Maximum Neighbor Distance
### TLE-Style Self-Study Notes

> **Source:** supplied AlgoZenith lecture PDF — **Binary Search Application 2**.
>
> The lecture studies this form:
>
> ```text
> N existing points on a number line
> + we may place K new points
> + consider distances between neighboring points
> → minimize the maximum neighboring distance
> ```
>
> This is another standard **Binary Search on Answer** problem.
>
> Code is formatted in the same clean C++17 style as the previous note.

---

# Table of Contents

- [1. What the Problem Is Asking](#1-what-the-problem-is-asking)
- [2. Small Example First](#2-small-example-first)
- [3. Why the Obvious Greedy Can Fail](#3-why-the-obvious-greedy-can-fail)
- [4. Story Removed](#4-story-removed)
- [5. Core Optimization Pattern](#5-core-optimization-pattern)
- [6. Search Space](#6-search-space)
- [7. Monotonicity](#7-monotonicity)
- [8. Math Prerequisite — How Many New Points Does One Gap Need?](#8-math-prerequisite--how-many-new-points-does-one-gap-need)
- [9. Ceiling Division Without Floating Point](#9-ceiling-division-without-floating-point)
- [10. Designing `check(maxDistance)`](#10-designing-checkmaxdistance)
- [11. Compact `check()` Dry Run](#11-compact-check-dry-run)
- [12. Compact Binary Search Dry Run](#12-compact-binary-search-dry-run)
- [13. Why the Solution Works](#13-why-the-solution-works)
- [14. Properly Formatted C++17](#14-properly-formatted-c17)
- [15. Complexity](#15-complexity)
- [16. Common Mistakes](#16-common-mistakes)
- [17. Recognition Pattern](#17-recognition-pattern)
- [18. Final Mental Model](#18-final-mental-model)

---

# 1. What the Problem Is Asking

We have `N` points on a number line.

Example:

```text
x1        x2        x3        x4
|---------|---------|---------|
```

We are allowed to place:

```text
K new points
```

After adding them, sort all old and new points and consider every neighboring distance.

We want to minimize:

```text
maximum neighboring distance
```

So the objective is:

```text
minimize the maximum
```

That is the important Binary Search hint from the lecture.

---

# 2. Small Example First

Consider one large interval:

```text
0 -------------------------------- 100
```

Suppose:

```text
K = 2
```

We can add two points.

A good placement is approximately:

```text
0 -------- 33 -------- 67 -------- 100
     33          34           33
```

The maximum neighboring distance is:

```text
34
```

So the goal is not:

```text
place each point independently at some obvious local midpoint
```

The goal is globally:

```text
make the largest final gap as small as possible
```

---

# 3. Why the Obvious Greedy Can Fail

The lecture explicitly warns against this idea:

```text
take the currently largest gap
→ place the next point at its midpoint
→ repeat
```

Consider:

```text
0 ---------------- 100
K = 2
```

### First insertion

Midpoint:

```text
0 -------- 50 -------- 100
```

Now the gaps are:

```text
50, 50
```

### Second insertion

If we split one of those gaps:

```text
0 ---- 25 ---- 50 -------- 100
```

gaps:

```text
25, 25, 50
```

Maximum:

```text
50
```

But distributing both points globally:

```text
0 -------- 33 -------- 67 -------- 100
```

gives:

```text
33, 34, 33
```

Maximum:

```text
34
```

Therefore:

```text
repeatedly splitting the current largest gap
is not a reliable global strategy
```

We need another formulation.

---

# 4. Story Removed

The problem becomes:

```text
Given:
sorted positions
x[0], x[1], ..., x[n-1]

We may insert at most K additional points.

After insertion:

find the minimum possible X
such that every neighboring distance <= X.
```

Equivalently:

```text
minimize(
    maximum adjacent gap
)
```

---

# 5. Core Optimization Pattern

The lecture's key phrase is:

```text
minimize the maximum neighbor distance
```

This suggests:

```text
Binary Search on Answer
```

Instead of directly deciding where all new points go, guess:

```text
X = maximum allowed neighboring distance
```

Then ask:

```text
Can we make every final gap <= X
using at most K new points?
```

If YES:

```text
try a smaller X
```

If NO:

```text
we need a larger X
```

---

# 6. Search Space

The lecture uses:

```text
low = 0
```

and:

```text
high = maximum original neighboring gap
```

Why is `high` valid?

Before adding any points, the maximum neighboring gap is already:

```text
max(
    x[i] - x[i-1]
)
```

Adding points can only reduce or preserve that maximum.

Therefore:

```text
answer <= maxOriginalGap
```

So:

```text
0 <= answer <= maxOriginalGap
```

Example:

```text
points = [0,35,50,75,110]
```

Original gaps:

```text
35,15,25,35
```

Therefore:

```text
high = 35
```

---

# 7. Monotonicity

Define:

```text
check(X)
=
Can we place <= K new points
so every neighboring distance <= X?
```

Suppose:

```text
X = 20
```

works.

Then:

```text
21,22,23,...
```

also work because we are allowing larger gaps.

So the answer space looks like:

```text
X:
0 1 2 3 ... 17 18 19 20 21 22 ...

check:
N N N N ...  N  N  N  Y  Y  Y ...
                     ^
                     |
                 first YES
```

We need:

```text
minimum feasible X
```

Therefore:

```text
first True
```

Binary Search rule:

```cpp
if (check(mid)) {
    answer = mid;
    high = mid - 1;
} else {
    low = mid + 1;
}
```

---

# 8. Math Prerequisite — How Many New Points Does One Gap Need?

This is the most important derivation in the lecture.

Suppose two existing neighboring points have distance:

```text
d
```

We want every final piece to have length at most:

```text
X
```

Question:

```text
How many pieces do we need?
```

If every piece has maximum length `X`, then:

```text
piecesNeeded = ceil(d / X)
```

But:

```text
1 inserted point → 2 pieces
2 inserted points → 3 pieces
3 inserted points → 4 pieces
```

So:

```text
insertedPoints = piecesNeeded - 1
```

Therefore:

```text
pointsNeededForGap
=
ceil(d / X) - 1
```

This is the core formula.

---

## Dry Run 1

```text
d = 100
X = 34
```

Pieces needed:

```text
ceil(100 / 34)
=
ceil(2.941...)
=
3
```

New points:

```text
3 - 1
=
2
```

Possible layout:

```text
0 -------- 34 -------- 68 ------ 100
     34          34          32
```

Every gap:

```text
<= 34
```

So:

```text
2 new points are enough
```

---

## Dry Run 2

```text
d = 100
X = 33
```

Pieces:

```text
ceil(100 / 33)
=
ceil(3.03...)
=
4
```

New points:

```text
4 - 1
=
3
```

If only:

```text
K = 2
```

then:

```text
X = 33 is impossible
```

This immediately explains why the optimum for this teaching example is:

```text
34
```

---

## Another Small Example

```text
d = 35
X = 15
```

Pieces:

```text
ceil(35 / 15)
=
3
```

New points:

```text
3 - 1
=
2
```

ASCII:

```text
0 ---------- 15 ---------- 30 ---- 35
      15             15        5
```

---

# 9. Ceiling Division Without Floating Point

The lecture derives the standard integer formula.

For positive integers:

```text
ceil(d / X)
=
(d + X - 1) / X
```

using integer division.

So:

```text
pointsNeeded
=
(d + X - 1) / X - 1
```

---

## Why This Works

Example:

```text
d = 35
X = 15
```

Normal division:

```text
35 / 15 = 2 remainder 5
```

We need:

```text
ceil(35 / 15) = 3
```

Integer formula:

```text
(35 + 15 - 1) / 15
=
49 / 15
=
3
```

Therefore:

```text
new points
=
3 - 1
=
2
```

---

## Exact-Division Case

Suppose:

```text
d = 30
X = 15
```

Then:

```text
ceil(30 / 15)
=
2
```

Integer formula:

```text
(30 + 15 - 1) / 15
=
44 / 15
=
2
```

New points:

```text
2 - 1
=
1
```

Layout:

```text
0 -------- 15 -------- 30
```

Correct.

---

# 10. Designing `check(maxDistance)`

The checker should answer exactly this sentence:

```text
Can all original gaps be broken
into pieces of length <= maxDistance
using at most K new points?
```

For every adjacent pair:

```text
gap = points[i] - points[i-1]
```

Required inserted points:

```text
ceil(gap / maxDistance) - 1
```

Sum over all gaps:

```text
totalPointsNeeded
=
Σ(
    ceil(gap / maxDistance) - 1
)
```

Then:

```text
check(maxDistance)
=
totalPointsNeeded <= K
```

---

## Formula in Integer Arithmetic

For each gap:

```cpp
pointsNeeded +=
    (gap + maxDistance - 1) / maxDistance - 1;
```

---

## Why `mid == 0` Needs Special Handling

The lecture explicitly handles:

```cpp
if (mid == 0) {
    return false;
}
```

because the formula contains:

```text
gap / mid
```

and division by zero is invalid.

So when the Binary Search range starts from `0`, the checker must protect this case.

---

# 11. Compact `check()` Dry Run

Teaching example:

```text
points = [0,100]
K = 2
```

---

## Check `X = 50`

Only gap:

```text
d = 100
```

Needed:

```text
ceil(100/50)-1
=
2-1
=
1
```

So:

```text
pointsNeeded = 1 <= 2
```

Therefore:

```text
check(50) = true
```

---

## Check `X = 33`

```text
ceil(100/33)-1
=
4-1
=
3
```

So:

```text
pointsNeeded = 3 > 2
```

Therefore:

```text
check(33) = false
```

---

## Check `X = 34`

```text
ceil(100/34)-1
=
3-1
=
2
```

So:

```text
pointsNeeded = 2 <= 2
```

Therefore:

```text
check(34) = true
```

Transition:

```text
33 → false
34 → true
```

Hence:

```text
answer = 34
```

---

# 12. Compact Binary Search Dry Run

Use:

```text
points = [0,100]
K = 2

low = 0
high = 100
```

Compact dry run:

```text
Iter 1: low=0,  high=100 → mid=50 → needed=1 <=2 → YES → ans=50 → high=49
Iter 2: low=0,  high=49  → mid=24 → needed=4 >2  → NO  → low=25
Iter 3: low=25, high=49  → mid=37 → needed=2 <=2 → YES → ans=37 → high=36
Iter 4: low=25, high=36  → mid=30 → needed=3 >2  → NO  → low=31
Iter 5: low=31, high=36  → mid=33 → needed=3 >2  → NO  → low=34
Iter 6: low=34, high=36  → mid=35 → needed=2 <=2 → YES → ans=35 → high=34
Iter 7: low=34, high=34  → mid=34 → needed=2 <=2 → YES → ans=34 → high=33

answer = 34
```

---

# 13. Why the Solution Works

There are two important proofs.

## 13.1 The Checker Formula Is Exact

Take one original gap:

```text
d
```

To ensure every resulting gap is at most `X`, we need enough pieces so:

```text
numberOfPieces * X >= d
```

Therefore:

```text
numberOfPieces >= d / X
```

The smallest integer number of pieces is:

```text
ceil(d / X)
```

If we need `p` pieces, we need:

```text
p - 1
```

new internal points.

Therefore minimum new points for that gap is exactly:

```text
ceil(d / X) - 1
```

Since original gaps are independent, total required points is the sum across all gaps.

---

## 13.2 Feasibility Is Monotonic

Suppose maximum distance:

```text
X
```

is feasible.

If we increase it to:

```text
X + 1
```

each existing gap needs the same or fewer inserted points.

Mathematically:

```text
ceil(d / (X+1))
<=
ceil(d / X)
```

So:

```text
pointsNeeded(X+1)
<=
pointsNeeded(X)
```

Thus once:

```text
pointsNeeded <= K
```

becomes true, it remains true for all larger values.

Hence:

```text
NO NO NO YES YES YES
```

and Binary Search is valid.

---

# 14. Properly Formatted C++17

The lecture code, rewritten only for clean formatting and descriptive names while keeping the same algorithm:

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

bool check(
    ll maxDistance,
    const vector<ll>& points,
    ll k
) {
    if (maxDistance == 0) {
        return false;
    }

    ll pointsNeeded = 0;

    for (int i = 1; i < (int)points.size(); i++) {
        ll gap = points[i] - points[i - 1];

        pointsNeeded +=
            (gap + maxDistance - 1) / maxDistance - 1;

        if (pointsNeeded > k) {
            return false;
        }
    }

    return true;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;

    while (t--) {
        int n;
        ll k;

        cin >> n >> k;

        vector<ll> points(n);

        for (ll& x : points) {
            cin >> x;
        }

        sort(points.begin(), points.end());

        ll low = 0;
        ll high = 0;

        for (int i = 1; i < n; i++) {
            high = max(
                high,
                points[i] - points[i - 1]
            );
        }

        ll answer = -1;

        while (low <= high) {
            ll mid = low + (high - low) / 2;

            if (check(mid, points, k)) {
                answer = mid;
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        cout << answer << '\n';
    }

    return 0;
}
```

---

## Same Code — Compact Competitive Programming Format

This is the same logic in the compact code style used in the earlier notes:

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

bool check(ll mid, const vector<ll>& points, ll k) {
    if (mid == 0) {
        return false;
    }

    ll pointsNeeded = 0;

    for (int i = 1; i < (int)points.size(); i++) {
        ll gap = points[i] - points[i - 1];

        pointsNeeded += (gap + mid - 1) / mid - 1;

        if (pointsNeeded > k) {
            return false;
        }
    }

    return true;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;

    while (t--) {
        int n;
        ll k;

        cin >> n >> k;

        vector<ll> points(n);

        for (ll& x : points) {
            cin >> x;
        }

        sort(points.begin(), points.end());

        ll low = 0;
        ll high = 0;

        for (int i = 1; i < n; i++) {
            high = max(high, points[i] - points[i - 1]);
        }

        ll answer = -1;

        while (low <= high) {
            ll mid = low + (high - low) / 2;

            if (check(mid, points, k)) {
                answer = mid;
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        cout << answer << '\n';
    }

    return 0;
}
```

---

# 15. Complexity

Let:

```text
D = maximum original adjacent gap
```

Sorting points:

```text
O(N log N)
```

Each `check(mid)`:

```text
O(N)
```

Binary Search over:

```text
[0,D]
```

needs:

```text
O(log D)
```

checks.

So from the operations shown:

```text
O(N log N + N log D)
```

The lecture summarizes the approach as an `O(N)` checker combined with Binary Search.

Extra working space apart from the input array:

```text
O(1)
```

---

# 16. Common Mistakes

## Mistake 1 — Greedily Split the Current Largest Gap

This may optimize the current step but not the final global answer.

Think:

```text
Binary Search on final maximum distance
```

instead.

---

## Mistake 2 — Wrong Checker Question

Wrong:

```text
Where exactly should I insert each point?
```

Better:

```text
For maximum allowed gap X,
how many points are required?
```

This removes the placement complexity.

---

## Mistake 3 — Forgetting `-1`

For one gap:

```text
pieces = ceil(d / X)
```

but inserted points are:

```text
pieces - 1
```

Example:

```text
d=100
X=50

pieces=2
new points=1
```

Not `2`.

---

## Mistake 4 — Using Floor Instead of Ceiling

Wrong:

```text
d / X - 1
```

For:

```text
d=100
X=34
```

integer floor gives:

```text
100/34 = 2
```

but we actually need:

```text
3 pieces
```

So use:

```text
ceil(d/X)
```

implemented as:

```cpp
(d + X - 1) / X
```

---

## Mistake 5 — Floating Point for Ceiling

Avoid:

```cpp
ceil((double)gap / mid)
```

Integer arithmetic is exact:

```cpp
(gap + mid - 1) / mid
```

---

## Mistake 6 — Division by Zero

If search starts at:

```text
low = 0
```

then Binary Search may test:

```text
mid = 0
```

The lecture specifically handles this case before dividing.

---

## Mistake 7 — Forgetting to Sort

The formula uses:

```text
points[i] - points[i-1]
```

as neighboring distances.

So the point coordinates must first be sorted.

---

## Mistake 8 — Searching Right After a Feasible `mid`

We want the **minimum** feasible maximum distance.

Therefore:

```text
check(mid) = true
```

means:

```text
save mid
search LEFT
```

Code:

```cpp
answer = mid;
high = mid - 1;
```

---

# 17. Recognition Pattern

When you see:

```text
positions on a line
+
you may add / place K items
+
minimize maximum distance between neighbors
```

think:

```text
Binary Search on Answer
```

Candidate:

```text
X = allowed maximum gap
```

Checker:

```text
How many new points are required
to make every original gap <= X?
```

Per-gap formula:

```text
ceil(gap / X) - 1
```

Total condition:

```text
sum(pointsNeeded) <= K
```

---

# 18. Final Mental Model

```text
N sorted points
+
K new points
        |
        v
minimize maximum adjacent distance
        |
        v
guess X
        |
        v
for each original gap d:
    pieces = ceil(d / X)
    newPoints = pieces - 1
        |
        v
sum all required new points
        |
        v
required <= K ?
        |
     NO      YES
      |        |
      v        v
increase X   save X
             search smaller
        |
        v
first feasible X
```

The one formula to remember is:

```text
new points needed in gap d
=
ceil(d / X) - 1
```

For positive integers:

```text
ceil(d / X)
=
(d + X - 1) / X
```

So the checker becomes:

```text
Σ(
    (gap + X - 1) / X - 1
)
<= K
```

And the recognition sentence is:

```text
Minimize the maximum gap
→ guess the maximum gap
→ count how many insertions that guess requires
→ Binary Search the first feasible guess.
```
