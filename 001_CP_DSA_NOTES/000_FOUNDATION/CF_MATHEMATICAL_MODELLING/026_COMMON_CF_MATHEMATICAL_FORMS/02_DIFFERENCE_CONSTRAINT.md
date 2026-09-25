# Form 2 — Difference Constraint

> **Goal:** Learn to recognize, derive, visualize, and apply **Difference Constraints** in competitive programming: directed difference, absolute difference, bounded distance, range/spread, and difference-preserving transformations.

---

## 1. What is a Difference Constraint?

A **difference constraint** appears when the relationship between two values depends on how far apart they are.

```text
x - y = D
x - y <= K
x - y >= K

|x - y| = D
|x - y| <= K
|x - y| >= K
```

Typical Codeforces wording:

```text
"difference is D"
"differ by at most K"
"differ by at least K"
"distance between values"
"maximum minus minimum"
"make the values as close as possible"
"their difference does not exceed..."
```

There are two important families:

```text
DIRECTED DIFFERENCE
x - y

ABSOLUTE DIFFERENCE
|x - y|
```

The first keeps direction/sign.  
The second measures only distance.

---

## 2. Simple Example — Directed Difference

Suppose:

```text
x - y = 4
```

If:

```text
x = 11
```

then:

```text
11 - y = 4
```

Subtract `11` from both sides:

```text
-y = 4 - 11
-y = -7
```

Multiply both sides by `-1`:

```text
y = 7
```

So:

```text
x - y = D
```

can be rearranged as:

```text
x = y + D
```

or:

```text
y = x - D
```

### General derivation

Start:

```text
x - y = D
```

Add `y` to both sides:

```text
x = D + y
```

Reorder:

```text
x = y + D
```

Alternatively, solve for `y`:

```text
x - y = D

subtract x from both sides:

-y = D - x

multiply by -1:

y = x - D
```

---

## 3. Real-World Example — Temperature Difference

City A is `7°C` warmer than City B.

Let:

```text
A = temperature of City A
B = temperature of City B
```

English:

```text
A is 7 greater than B
```

Mathematics:

```text
A - B = 7
```

Therefore:

```text
A = B + 7
```

If:

```text
B = 12
```

then:

```text
A = 12 + 7
  = 19
```

Visual:

```text
B = 12                         A = 19

12----13----14----15----16----17----18----19
|<--------------- 7 --------------------->|

A - B = 7
```

Core mental model:

```text
DIFFERENCE = HOW MUCH ONE VALUE IS AHEAD OF ANOTHER
```

---

## 4. Absolute Difference — Distance

Suppose:

```text
|x - y| = 4
```

Absolute value removes direction.

Therefore there are **two possibilities**:

```text
x - y = 4
```

or:

```text
x - y = -4
```

From the first:

```text
x = y + 4
```

From the second:

```text
x = y - 4
```

Therefore:

```text
|x-y| = D

⇔

x = y+D
OR
x = y-D
```

### Numerical example

```text
|x - 10| = 3
```

means:

```text
x - 10 = 3
→ x = 13
```

or:

```text
x - 10 = -3
→ x = 7
```

Visual:

```text
7 -------- 10 -------- 13
     3          3

valid x values:
10-3 = 7
10+3 = 13
```

---

## 5. Absolute Difference — Upper Bound

Suppose the statement says:

> `x` and `y` differ by at most `K`.

Translate:

```text
difference
    ↓
distance
    ↓
|x-y|

at most K
    ↓
<= K
```

Therefore:

```text
|x-y| <= K
```

Use the absolute-value identity:

```text
|z| <= K
⇔
-K <= z <= K
```

Substitute:

```text
-K <= x-y <= K
```

Now isolate `y`.

From:

```text
x-y <= K
```

subtract `x`:

```text
-y <= K-x
```

multiply by `-1`.

**Important:** multiplying an inequality by a negative number reverses the inequality:

```text
y >= x-K
```

From:

```text
x-y >= -K
```

subtract `x`:

```text
-y >= -K-x
```

multiply by `-1` and reverse the inequality:

```text
y <= x+K
```

Combine:

```text
x-K <= y <= x+K
```

So:

```text
|x-y| <= K

⇔

x-K <= y <= x+K
```

### Geometric meaning

```text
               allowed y values
             <------------------->
------ x-K ---------- x ---------- x+K ------
```

Example:

```text
x = 10
K = 3

|10-y| <= 3

⇔

7 <= y <= 13
```

---

## 6. Absolute Difference — Lower Bound

Suppose:

```text
|x-y| >= K
```

This means the values must be at least `K` apart.

Unlike the `<=` form, the valid region is **outside** the interval.

```text
|x-y| >= K

⇔

x-y >= K
OR
x-y <= -K
```

First branch:

```text
x-y >= K

-y >= K-x

multiply by -1 and reverse:

y <= x-K
```

Second branch:

```text
x-y <= -K

-y <= -K-x

multiply by -1 and reverse:

y >= x+K
```

Therefore:

```text
|x-y| >= K

⇔

y <= x-K
OR
y >= x+K
```

Visual:

```text
valid                 invalid                 valid
<---------|--------------------------------|--------->
        x-K               x              x+K
```

---

## 7. Range / Spread Difference

Another extremely common form is:

```text
maximum - minimum
```

For a set/array:

```text
range = max(A) - min(A)
```

This is also a difference constraint.

Example:

```text
A = [4, 10, 7, 6]

min = 4
max = 10

range = 10 - 4
      = 6
```

If the problem asks to make selected values as close as possible, it often becomes:

```text
minimize(max - min)
```

After sorting, the minimum and maximum of a chosen contiguous sorted block are simply its endpoints.

This is the key idea behind **CF 337A — Puzzles**.

---

## 8. Difference-Preserving Algebraic Transformation

Sometimes the difference is hidden inside a larger equation.

Example:

```text
a[j] - a[i] = j - i
```

We want to group terms belonging to the same index.

Start:

```text
a[j] - a[i] = j - i
```

Subtract `j` from both sides:

```text
a[j] - a[i] - j = -i
```

Add `a[i]` to both sides:

```text
a[j] - j = a[i] - i
```

Now define:

```text
key[i] = a[i] - i
```

Then the original pair condition becomes:

```text
key[j] = key[i]
```

This is a major CP transformation:

```text
PAIR RELATION
     ↓
move index/value terms
     ↓
same expression on both sides
     ↓
frequency / hashmap grouping
```

---

## 9. Algorithmic Reduction Matrix

| Mathematical Condition | Meaning | Typical Technique | Complexity |
|---|---|---|---:|
| `x-y=D` | Directed fixed difference | Hash set / frequency | `O(n)` avg. |
| `|x-y|=D` | Distance exactly `D` | Check `x-D`, `x+D` | `O(n)` avg. |
| `|x-y|<=K` | Partner lies in `[x-K,x+K]` | Sort + two pointers / binary search | `O(n log n)` |
| `|x-y|>=K` | Partner lies outside center interval | Sort + lower_bound / two pointers | `O(n log n)` |
| `max-min` | Spread/range | Sort / min-max | `O(n log n)` or `O(n)` |
| minimize `max-min` for `k` chosen values | Tightest width-`k` sorted block | Sort + sliding window | `O(n log n)` |
| Adjacent differences after sorting | Detect gaps / reachability | Sort + scan | `O(n log n)` |
| `a[j]-a[i]=j-i` | Same transformed key | Hash map | `O(n)` avg. |

---

## 10. Codeforces Mental Triggers

### Trigger 1 — “Difference at most K”

```text
"differ by at most K"
        ↓
|x-y| <= K
        ↓
-K <= x-y <= K
        ↓
x-K <= y <= x+K
        ↓
partner lies in an interval
```

**Contest question:**

```text
Can sorting let me count/query this interval quickly?
```

---

### Trigger 2 — “Largest minus smallest”

```text
"make the difference between largest and smallest minimum"
        ↓
minimize(max - min)
        ↓
sort values
        ↓
candidate group endpoints determine the answer
```

**Contest question:**

```text
After sorting, can the optimal selected set be represented by a contiguous window?
```

---

### Trigger 3 — “Difference between two expressions”

```text
a[j] - a[i] = j - i
        ↓
move j to left
        ↓
a[j] - j - a[i] = -i
        ↓
add a[i]
        ↓
a[j] - j = a[i] - i
        ↓
equal transformed keys
```

**Contest question:**

```text
Can I rearrange the equation so each side depends on only one index?
```

---

## 11. Standard C++ Snippets

### A. Exact directed difference

Mathematics:

```text
x - y = D
    ↓
y = x-D
```

```cpp
bool hasDifference(const vector<long long>& a, long long D) {
    unordered_set<long long> seen;

    for (long long x : a) {
        if (seen.count(x - D) || seen.count(x + D))
            return true;
        seen.insert(x);
    }

    return false;
}
```

Average:

```text
Time:  O(n)
Space: O(n)
```

---

### B. Minimum range among `k` selected values

Mathematics:

```text
minimize(max-min)
        ↓
sort
        ↓
window [i ... i+k-1]
        ↓
difference = a[i+k-1] - a[i]
```

```cpp
long long minimumRange(vector<long long> a, int k) {
    sort(a.begin(), a.end());

    long long ans = LLONG_MAX;

    for (int i = 0; i + k - 1 < (int)a.size(); ++i) {
        ans = min(ans, a[i + k - 1] - a[i]);
    }

    return ans;
}
```

```text
Time:  O(n log n)
Space: O(1) auxiliary
```

---

# 12. Curated Codeforces Benchmarks

These variants follow the same learning style as Form 1: remove nouns, formulate variables, derive the mathematics, extract the observation, then choose the algorithm. fileciteturn1file0L654-L656

| Problem | Rating | Difference Variant | Key Observation | Technique | Link |
|---|---:|---|---|---|---|
| CF 1399A — Remove Smallest | 800 | `|x-y|<=1` | After sorting, every adjacent gap must be `<=1` | Sort + scan | https://codeforces.com/problemset/problem/1399/A |
| CF 337A — Puzzles | 900 | minimize `max-min` | After sorting, any optimal `n` chosen values form a candidate contiguous block | Sort + fixed window | https://codeforces.com/problemset/problem/337/A |
| CF 1244E — Minimizing Difference | 2000 | minimize `max-min` under `k` unit operations | Spend operations on the cheaper side; sorted extremes move inward | Sort + greedy / two pointers | https://codeforces.com/problemset/problem/1244/E |

> The third benchmark intentionally goes beyond the usual 1000–1600 range because it shows the advanced evolution of the exact same `max-min` form.

---

# 13. Variant 1 — CF 1399A: Remove Smallest

Problem: https://codeforces.com/problemset/problem/1399/A

The operation permits choosing two elements only when:

```text
|a[i] - a[j]| <= 1
```

and removes the smaller one. citeturn0search5

## A. Remove Story Nouns

```text
array elements     → values
legal removal      → pair with distance <= 1
remove smaller     → smaller value disappears
goal               → reduce all values to one survivor
```

Abstract problem:

```text
Given:
array a

Allowed:
remove smaller x from pair (x,y)

only if:
|x-y| <= 1

Need:
determine whether repeated operations can leave one value
```

---

## B. Define Variables

```text
n       = number of values
a[i]    = value
x,y     = values chosen for one operation
```

Core condition:

```text
|x-y| <= 1
```

---

## C. Algebraic Derivation

Start:

```text
|x-y| <= 1
```

Use:

```text
|z| <= K
⇔
-K <= z <= K
```

Therefore:

```text
-1 <= x-y <= 1
```

For integer values, the allowed differences are:

```text
x-y ∈ {-1, 0, 1}
```

So the two selected values must be equal or consecutive integers.

---

## D. Key Observation

Sort:

```text
a[0] <= a[1] <= ... <= a[n-1]
```

Suppose there is a gap:

```text
a[i+1] - a[i] > 1
```

Example:

```text
1  2  |  4  4
      gap = 2
```

Values on the left cannot directly bridge to values on the right because the nearest cross-gap pair already differs by more than `1`.

The smaller side cannot eliminate itself upward across that gap.

Therefore such a gap makes the answer impossible.

Conversely, if every adjacent sorted gap is at most `1`, smaller values can be removed progressively.

Contest compression:

```text
operation requires distance <=1
        ↓
sort
        ↓
closest bridge between value levels = adjacent gap
        ↓
any gap >1 blocks progress
```

---

## E. Solution

```text
1. Sort the array.
2. Check every adjacent difference.
3. If any a[i]-a[i-1] > 1:
      NO
4. Otherwise:
      YES
```

---

## F. Horizontal Dry Run

Possible:

```text
original:  1   2   2
sorted:    1   2   2
gap:           1   0
valid?:       YES YES

answer = YES
```

Impossible:

```text
original:  1   2   4
sorted:    1   2   4
gap:           1   2
valid?:       YES  NO

                    ↑
                blocking gap

answer = NO
```

---

## G. Pseudocode

```text
READ n
READ a

SORT a

FOR i = 1 ... n-1:

    IF a[i] - a[i-1] > 1:
        PRINT NO
        stop

PRINT YES
```

---

## H. Complexity

```text
Sorting: O(n log n)
Scan:    O(n)

Total:   O(n log n)
Space:   O(1) auxiliary
```

---

## I. Variant Lesson

```text
BASE FORM:
|x-y| <= K

HERE:
K = 1

TRANSFORMATION:
|x-y| <= 1
→ -1 <= x-y <= 1

EXTRA STRUCTURE:
elements can be removed progressively

OBSERVATION:
after sorting, adjacent gaps reveal whether value levels can connect

ALGORITHM:
sort + adjacent-difference scan
```

---

# 14. Variant 2 — CF 337A: Puzzles

Problem: https://codeforces.com/problemset/problem/337/A

The task is to choose `n` values from `m` so that the difference between the largest selected value `A` and smallest selected value `B` is minimum. citeturn0search7

## A. Remove Story Nouns

```text
students            → number of values to choose = n
puzzles             → candidate values f[i]
pieces               → numeric value
largest puzzle       → maximum selected value
smallest puzzle      → minimum selected value
unfairness           → max - min
```

Abstract problem:

```text
Given:
m values

Choose:
exactly n values

Minimize:
maximum(selected) - minimum(selected)
```

---

## B. Define Variables

```text
n       = number of values to choose
m       = number of available values
f[i]    = candidate value

mn      = minimum selected value
mx      = maximum selected value

answer  = min(mx-mn)
```

---

## C. Core Mathematical Constraint

Objective:

```text
minimize:

mx - mn
```

This is a directed difference because:

```text
mx >= mn
```

so:

```text
|mx-mn| = mx-mn
```

---

## D. Algebraic / Structural Derivation

Sort:

```text
f[0] <= f[1] <= ... <= f[m-1]
```

Suppose we choose `n` values whose minimum is:

```text
f[i]
```

and maximum is:

```text
f[j]
```

Their spread is:

```text
f[j] - f[i]
```

If there are at least `n` values inside `[i,j]`, choosing values outside this interval cannot reduce the spread.

For an optimal set of exactly `n` sorted values, we only need to test contiguous blocks:

```text
f[i], f[i+1], ..., f[i+n-1]
```

For each block:

```text
minimum = f[i]
maximum = f[i+n-1]
```

Therefore:

```text
difference(i)
=
f[i+n-1] - f[i]
```

and:

```text
answer
=
min over i of
f[i+n-1] - f[i]
```

---

## E. Key Observation

> What should I notice during a contest?

```text
minimize max-min
      ↓
only endpoints determine cost
      ↓
sort values
      ↓
for exactly n chosen values,
test every length-n sorted window
```

This converts subset selection into a simple fixed-size window scan.

---

## F. Solution

```text
1. Sort all m values.
2. Consider every contiguous block of n values.
3. Its minimum is the left endpoint.
4. Its maximum is the right endpoint.
5. Compute right-left.
6. Take the minimum.
```

---

## G. Horizontal Dry Run

Sample:

```text
n = 4
f = [10, 12, 10, 7, 5, 22]

sorted:
5    7    10    10    12    22

window:
[5    7    10    10]   diff = 10-5  = 5
     [7    10    10    12] diff = 12-7  = 5
          [10   10    12    22] diff = 22-10 = 12

answer = 5
```

---

## H. Pseudocode

```text
READ n, m
READ f

SORT f

answer = INF

FOR left = 0 ... m-n:

    right = left+n-1

    difference = f[right] - f[left]

    answer = min(answer, difference)

PRINT answer
```

---

## I. Complexity

```text
Sorting: O(m log m)
Scan:    O(m)

Total:   O(m log m)
Space:   O(1) auxiliary
```

---

## J. Variant Lesson

```text
BASE FORM:
difference = max-min

OBJECTIVE:
minimize difference

EXTRA CONSTRAINT:
choose exactly n out of m values

OBSERVATION:
after sorting, only endpoints matter;
optimal candidates are length-n windows

ALGORITHM:
sort + fixed-size window
```

---

# 15. Variant 3 — CF 1244E: Minimizing Difference

Problem: https://codeforces.com/problemset/problem/1244/E

The problem allows at most `k` unit increments/decrements and asks for the minimum possible difference between the array maximum and minimum. Its constraints are `n <= 10^5` and `k <= 10^14`. citeturn0search4

## A. Remove Story Nouns

```text
sequence             → array a
increase by one      → +1 operation
decrease by one      → -1 operation
at most k operations → total movement budget
difference           → max(a)-min(a)
goal                 → minimize final range
```

Abstract problem:

```text
Given:
array a
budget k

Operation:
change any one value by ±1
cost = 1 per unit

Minimize:
max(a) - min(a)
```

---

## B. Define Variables

```text
n       = number of values
k       = operation budget
a[i]    = value

lo      = current minimum side
hi      = current maximum side

cntLo   = number of values currently at/below lo frontier
cntHi   = number of values currently at/above hi frontier

answer  = minimum possible hi-lo
```

---

## C. Core Mathematical Constraint

Initial spread:

```text
D = max(a) - min(a)
```

Each unit operation moves one element by one.

To raise `c` equal low-side values by distance `d`:

```text
cost = c * d
```

Why?

```text
each value needs d operations
number of values = c

total = c × d
```

Similarly, lowering `c` high-side values by distance `d` costs:

```text
c * d
```

---

## D. Algebraic Derivation

After sorting:

```text
a[0] <= a[1] <= ... <= a[n-1]
```

Suppose the first `cntLo` values have effectively been raised to `a[L]`.

To raise all of them to the next distinct level `a[L+1]`:

```text
distance
=
a[L+1] - a[L]
```

There are:

```text
cntLo
```

values to move.

Therefore:

```text
costLeft
=
(a[L+1] - a[L]) * cntLo
```

Similarly on the right:

```text
distance
=
a[R] - a[R-1]

costRight
=
(a[R] - a[R-1]) * cntHi
```

Now compare:

```text
costLeft
vs
costRight
```

The cheaper side gives more range reduction per available operation at that stage.

---

## E. Key Observation

> What unlocks the problem?

```text
objective = max-min
       ↓
only extremes determine current answer
       ↓
changing interior values does not immediately shrink max-min
       ↓
spend operations on current extreme groups
       ↓
after sorting, move cheaper extreme group toward next level
```

This is the advanced form of the same idea seen in Puzzles:

```text
DIFFERENCE
=
right extreme - left extreme
```

but now we are allowed to **move the endpoints** using a budget.

---

## F. Solution

One greedy/two-pointer view:

```text
1. Sort a.
2. Start L at the minimum and R at the maximum.
3. Track how many values belong to the current left and right extreme groups.
4. Compute the cost to move each extreme group to its next distinct level.
5. Spend budget on the cheaper side.
6. If the whole jump is affordable, merge that group with the next level.
7. Otherwise partially move that side using k / count.
8. Continue until budget is exhausted or both sides meet.
9. Return rightLevel-leftLevel.
```

---

## G. Horizontal Dry Run

Example:

```text
a = [1, 3, 5, 7]
k = 5

sorted:
1    3    5    7

initial range:
7-1 = 6
```

Move left extreme:

```text
1 → 3

count = 1
distance = 2
cost = 1*2 = 2

k: 5 → 3

state conceptually:
3    3    5    7
```

Now left extreme group has `2` values at `3`.

Right side:

```text
7 → 5

count = 1
distance = 2
cost = 2

k: 3 → 1

state:
3    3    5    5
```

Current range:

```text
5-3 = 2
```

One remaining operation cannot make the range smaller than `2` because moving one of the duplicated extremes does not remove that extreme level.

Final:

```text
answer = 2
```

which matches the problem's first sample. citeturn0search4

---

## H. Pseudocode

```text
SORT a

L = 0
R = n-1

leftCount  = 1
rightCount = 1

WHILE L < R AND k > 0:

    skip/merge equal values at the left
    skip/merge equal values at the right

    leftGap  = a[L+1] - a[L]
    rightGap = a[R] - a[R-1]

    leftCost  = leftGap  * leftCount
    rightCost = rightGap * rightCount

    IF leftCost <= rightCost:

        IF leftCost <= k:
            k -= leftCost
            move left frontier to next level
            increase leftCount
        ELSE:
            raise left frontier by floor(k / leftCount)
            k = 0

    ELSE:

        IF rightCost <= k:
            k -= rightCost
            move right frontier to previous level
            increase rightCount
        ELSE:
            lower right frontier by floor(k / rightCount)
            k = 0

RETURN max(0, rightLevel-leftLevel)
```

---

## I. Complexity

```text
Sorting:       O(n log n)
Two-pointer:   O(n)

Total:         O(n log n)
Space:         O(1) auxiliary
```

---

## J. Variant Lesson

```text
BASE FORM:
D = max-min

OBJECTIVE:
minimize D

EXTRA CONSTRAINT:
up to k unit changes are allowed

COST FORM:
moving c values by d units costs c*d

OBSERVATION:
only current extreme groups matter for shrinking the range

ALGORITHM:
sort + greedy/two-pointer extreme compression
```

---

# 16. Compare the Three Difference-Constraint Variants

| Problem | Base Form | Extra Constraint | Transformation / Structure | Observation | Algorithm |
|---|---|---|---|---|---|
| CF 1399A | `|x-y|<=1` | Repeated deletion | Integer distance is only `0` or `1` | Sorted gap `>1` blocks progress | Sort + scan |
| CF 337A | `max-min` | Choose exactly `n` values | Window cost `a[i+n-1]-a[i]` | Only sorted window endpoints matter | Sort + fixed window |
| CF 1244E | `max-min` | `k` unit modifications | Move cost = `count × distance` | Compress cheaper extreme group | Sort + greedy / two pointers |

---

# 17. Difference Constraint — Variant Recognition Map

```text
                       DIFFERENCE CONSTRAINT
                               |
        +----------------------+----------------------+
        |                      |                      |
    DIRECTED               ABSOLUTE                 RANGE
     x-y                    |x-y|                  max-min
        |                      |                      |
   +----+----+          +------+------+          +----+----+
   |         |          |             |          |         |
 = D       <= K       = D           <= K      evaluate   minimize
   |         |          |             |          |         |
y=x-D    y>=x-K     y=x±D      x-K<=y<=x+K   extremes   sort/window
```

Advanced hidden form:

```text
a[j]-a[i] = j-i
       ↓
a[j]-j = a[i]-i
       ↓
same transformed key
       ↓
frequency / hashmap
```

---

# 18. Instant Recognition Drill

| Statement phrase | Mathematical translation |
|---|---|
| “`x` is `D` greater than `y`” | `x-y=D` |
| “values differ by exactly `D`” | `|x-y|=D` |
| “difference is at most `K`” | `|x-y|<=K` |
| “difference is at least `K`” | `|x-y|>=K` |
| “spread / unfairness” | `max-min` |
| “make values as close as possible” | minimize a difference/range |

### Mental drills

```text
1. |x-10| <= 3
   → valid x interval?

2. |x-10| = 3
   → possible x values?

3. |x-10| >= 3
   → valid regions?

4. a[j]-a[i] = j-i
   → transformed key?

5. choose k values minimizing max-min
   → first structural move?
```

### Answers

```text
1.
|x-10| <= 3
→ -3 <= x-10 <= 3
→ 7 <= x <= 13

2.
|x-10| = 3
→ x = 7 or 13

3.
|x-10| >= 3
→ x <= 7 or x >= 13

4.
a[j]-a[i] = j-i
→ a[j]-j = a[i]-i
→ key[i] = a[i]-i

5.
Sort.
Then inspect contiguous size-k windows.
```

---

# 19. Mathematical Form to Memorize

```text
FORM:
Difference Constraint

DIRECTED:
x-y = D
→ y = x-D

ABSOLUTE EXACT:
|x-y| = D
→ y = x-D OR y = x+D

ABSOLUTE UPPER:
|x-y| <= K
→ -K <= x-y <= K
→ x-K <= y <= x+K

ABSOLUTE LOWER:
|x-y| >= K
→ y <= x-K OR y >= x+K

RANGE:
D = max-min

HIDDEN EQUALITY:
a[j]-a[i] = j-i
→ a[j]-j = a[i]-i
```

### Core Mental Model

```text
DIRECTED DIFFERENCE
=
relative offset

ABSOLUTE DIFFERENCE
=
distance

MAX-MIN
=
spread / range
```

---

# 20. Pattern Recognition

### SIGNAL

```text
"difference"
"distance"
"gap"
"at most K apart"
"at least K apart"
"largest minus smallest"
"make values closer"
```

### MATH

```text
x-y
|x-y|
max-min
```

### THINK

```text
Is direction important?

If not:
    use absolute difference.

Can I turn |x-y| into an interval?

Can sorting expose adjacent gaps or endpoints?

Can I rearrange the equation so each side depends on one index?
```

### TYPICAL SOLUTIONS

```text
sorting
two pointers
binary search
sliding/fixed window
hash set / frequency map
greedy extreme compression
```

---

# 21. Contest Mental Compression

```text
READ ENGLISH
     ↓
REMOVE STORY NOUNS
     ↓
identify x, y, difference/bound
     ↓
ASK: directed or absolute?
     ↓
WRITE DIFFERENCE FORM
     ↓
x-y=D
|x-y|=D
|x-y|<=K
max-min
     ↓
EXPAND / REARRANGE ALGEBRA
     ↓
CHECK:
interval?
sorted gap?
range endpoints?
same transformed key?
     ↓
OBSERVATION
     ↓
hash / sort / window / 2ptr / binary search / greedy
```

Ultra-compressed:

```text
"difference" → x-y or |x-y| → expand/rearrange → distance/interval/gap → algorithm
```

---

# 22. Final One-Line Takeaway

**Treat directed difference as an offset, absolute difference as distance, and `max-min` as range—then algebra or sorting usually exposes the algorithm.**
