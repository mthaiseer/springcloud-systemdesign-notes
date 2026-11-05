# 011 — DP Advanced Problem Solving 2
## Counting Tiles → K Balanced Teams → Digit DP with Position Parity

> **Self-study goal:** Combine earlier advanced-DP ideas:
>
> - **Bitmask/Profile DP + transition precomputation**
> - **Sorting + two pointers + 2D DP**
> - **Digit DP + `tight` + leading-zero handling**
>
> Follow the same flow:
>
> **Problem Asking → Story Removed → Observation → State → Choices → Transition → Base Case → Final Subproblem → Complexity → Dry Run → C++17**
>
> **Formatting used in this note:** compact C++17, 4-space indentation, same-line braces, related variables together, short logical blocks, and transitions kept visually easy to scan.

---

# Problem Links

1. **CSES 2181 — Counting Tilings**  
   https://cses.fi/problemset/task/2181

2. **Codeforces 1133E — K Balanced Teams**  
   https://codeforces.com/problemset/problem/1133/E

3. **Digit DP lecture problem**  
   Count numbers in `[L,R]` whose odd-position digits are odd and even-position digits are even.

4. **Homework — LeetCode: Count the Number of Powerful Integers**  
   https://leetcode.com/problems/count-the-number-of-powerful-integers/

---

# 1. Prerequisites

## 1.1 Bitmask refresher

For `n` cells, use an `n`-bit integer:

```text
0 → cell is free
1 → cell is already occupied
```

Check bit `i`:

```cpp
if (mask & (1 << i)) {
    // occupied
}
```

Set bit `i`:

```cpp
int nextMask = mask | (1 << i);
```

There are:

```text
2^n
```

possible masks.

For Counting Tilings:

```text
n <= 10
```

so:

```text
2^10 = 1024
```

is manageable.

---

## 1.2 Profile / row DP

If a grid is very long but one dimension is small, do not store the whole board.

Store only the boundary between:

```text
processed area
and
unprocessed area
```

For domino tiling, one slice has at most `n` cells, so one bitmask is enough to represent the boundary.

---

## 1.3 Domino orientation

A `2 × 1` domino can be placed in two orientations:

```text
[]
[]
```

or:

```text
[][]
```

In profile DP:

```text
one placement can stay inside the current slice
```

while another can:

```text
cross into the next slice
```

Crossing into the next slice creates bits in:

```text
nextMask
```

---

## 1.4 Sorting + two pointers

Sorting can turn:

```text
max(group) - min(group) <= 5
```

into a contiguous interval condition.

Two-pointer template:

```cpp
for (int i = 0, j = 0; i < n; i++) {
    while (j < n && condition) {
        j++;
    }

    idx[i] = j - 1;
}
```

Even with a nested `while`, this can be `O(n)` because `j` only moves forward.

---

## 1.5 2D take/skip DP

Common state:

```text
dp[index][used]
=
best answer from `index` onward
after using `used` resources
```

Typical choices:

```text
skip current index

or

take a structure starting here
```

K Balanced Teams follows this pattern.

---

## 1.6 Digit DP refresher

Digit DP builds a number:

```text
MSD → LSD
```

Typical state includes:

```text
index
tight
property information
```

`tight` tells us whether the prefix is still equal to the upper-bound prefix.

---

## 1.7 Why `started` can be necessary

For a 5-digit bound, the number `143` is represented as:

```text
00143
```

The leading zeros are not real digits of the number.

If the property depends on actual digit positions, we must know:

```text
has the number really started?
```

That state is:

```text
started
```

---

# 2. Problem-Solving Map

```text
Counting Tilings
    ↓
small width
    ↓
bitmask/profile DP
    ↓
precompute mask transitions

K Balanced Teams
    ↓
sort
    ↓
valid team = contiguous interval
    ↓
two-pointer endpoint preprocessing
    ↓
dp[index][teams]

Digit parity problem
    ↓
huge range
    ↓
Digit DP
    ↓
tight + started + parity
```

---

# 3. Problem 1 — Counting Tilings

## CSES 2181

We have an:

```text
n × m
```

grid.

Cover every cell using:

```text
2 × 1 dominoes
```

Count the complete tilings.

The key lecture constraint is:

```text
n <= 10
```

So we solve slice by slice along the long dimension and represent one slice using a bitmask.

---

# 4. Meaning of the Mask

Use:

```text
1 → this cell is already occupied
0 → this cell still needs to be filled
```

Example:

```text
n = 3
mask = 001
```

means one cell of the current slice is already occupied by a domino extending from the previous slice.

We must fill every remaining zero bit.

Some placements may extend into the next slice; those positions become `1` in:

```text
nextMask
```

---

# 5. Precompute Valid Mask Transitions

For every:

```text
mask
```

precompute every valid:

```text
nextMask
```

and store:

```text
transitions[mask]
```

Why?

Because the local ways to fill one slice depend only on:

```text
n
and
mask
```

They do not depend on which row/slice we are processing.

So we should not regenerate them inside every DP state.

---

## 5.1 Transition generator

Use:

```text
cell
mask
nextMask
```

### Case 1 — current cell already occupied

```text
move to cell + 1
```

### Case 2 — current cell empty, domino crosses boundary

Mark the same position in `nextMask`:

```text
nextMask | (1 << cell)
```

Then:

```text
move to cell + 1
```

### Case 3 — current and next cells both empty

Place a domino fully inside this slice:

```text
cover cell and cell+1
```

Then:

```text
move to cell + 2
```

### Finish

When:

```text
cell == n
```

the current slice is fully filled.

Record:

```text
nextMask
```

---

## 5.2 Compact transition

```text
if cell == n:
    save nextMask

else if current cell is occupied:
    go to cell + 1

else:
    cross to next slice
    → set bit in nextMask

    if cell + 1 is also free:
        place inside current slice
        → jump by 2
```

---

# 6. Counting Tilings DP

Define:

```text
dp[row][mask]
=
number of valid tilings
from `row` onward

when current-row occupancy
is represented by `mask`
```

The hard local placement work is already inside:

```text
transitions[mask]
```

So the main recurrence is simple:

```text
dp[row][mask]
=
Σ dp[row + 1][nextMask]

for nextMask ∈ transitions[mask]
```

---

## 6.1 Base case

When:

```text
row == m
```

all slices are processed.

Valid only if:

```text
mask == 0
```

because a nonzero mask means a domino is extending beyond the board.

So:

```text
dp[m][0] = 1
dp[m][mask != 0] = 0
```

---

## 6.2 Final subproblem

Initially:

```text
row = 0
mask = 0
```

Therefore:

```text
answer = dp[0][0]
```

---

# 7. Counting Tilings Dry Run

For:

```text
n = 2
m = 2
```

start:

```text
row = 0
mask = 00
```

The local transition generator finds the valid ways to fill that slice.

Possible resulting masks include patterns equivalent to:

```text
00
11
```

depending on whether dominoes stay inside the slice or cross into the next one.

The DP explores both.

After the final slice, only paths ending with:

```text
mask = 00
```

are accepted.

Answer:

```text
2
```

---

# 8. Counting Tilings Complexity

The lecture groups all generated local transitions as roughly:

```text
O(3^n)
```

The main DP has:

```text
m × 2^n
```

states and iterates the precomputed transitions.

The lecture summary is approximately:

```text
O(m × 3^n + m × 2^n)
```

For:

```text
n <= 10
```

this is practical.

---

# 9. Counting Tilings C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll MOD = 1000000007LL;

int n, m;
vector<vector<int>> transitions;
vector<vector<ll>> dp;

void buildTransitions(int cell, int mask, int nextMask) {
    if (cell == n) {
        transitions[mask].push_back(nextMask);
        return;
    }

    if (mask & (1 << cell)) {
        buildTransitions(cell + 1, mask, nextMask);
        return;
    }

    buildTransitions(cell + 1, mask, nextMask | (1 << cell));

    if (cell + 1 < n && !(mask & (1 << (cell + 1)))) {
        buildTransitions(cell + 2, mask, nextMask);
    }
}

ll solve(int row, int mask) {
    if (row == m) return mask == 0;

    ll &ans = dp[row][mask];
    if (ans != -1) return ans;

    ans = 0;

    for (int nextMask : transitions[mask]) {
        ans += solve(row + 1, nextMask);
        ans %= MOD;
    }

    return ans;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> n >> m;

    int totalMasks = 1 << n;
    transitions.assign(totalMasks, {});

    for (int mask = 0; mask < totalMasks; mask++) {
        buildTransitions(0, mask, 0);
    }

    dp.assign(m, vector<ll>(totalMasks, -1));

    cout << solve(0, 0) << '\n';

    return 0;
}
```

---

# 10. Problem 2 — K Balanced Teams

## Codeforces 1133E

We have skill values:

```text
a[0], a[1], ..., a[n-1]
```

We may create at most:

```text
k teams
```

Inside one team:

```text
maxSkill - minSkill <= 5
```

Goal:

```text
maximize number of selected people
```

The lecture explicitly notes:

```text
greedy alone does not work
```

so we need DP.

---

# 11. Why Sorting Changes the Problem

After sorting:

```text
a[0] <= a[1] <= ... <= a[n-1]
```

If a team starts at index `i`, every valid member lies in one contiguous block:

```text
[i ... j]
```

such that:

```text
a[j] - a[i] <= 5
```

Therefore define:

```text
idx[i]
=
furthest j
with a[j] - a[i] <= 5
```


---

# 12. Precompute the Furthest Valid Teammate

The screenshot from your notes uses this exact idea:

```cpp
vector<int> a(n), idx(n);

for (auto &x : a) cin >> x;

sort(a.begin(), a.end());

for (int i = 0, j = 0; i < n; i++) {
    while (j < n && a[j] - a[i] <= 5) {
        j++;
    }

    idx[i] = j - 1;
}
```

This formatting is the style used throughout this note.

---

## 12.1 Why two pointers work

`j` never moves backward.

As `i` increases:

```text
the furthest valid endpoint
also moves monotonically forward
```

So total pointer movement is:

```text
O(n)
```

after sorting.

---

# 13. K Balanced Teams DP State

Define:

```text
dp[i][teamsUsed]
=
maximum number of people
we can select from [i ... n-1]

after already forming
`teamsUsed` teams
```

This matches the lecture's handwritten state.

---

## 13.1 Base case

If:

```text
i == n
```

no people remain.

If:

```text
teamsUsed == k
```

no team slots remain.

So:

```cpp
if (i == n || teamsUsed == k) return 0;
```

---

# 14. K Balanced Teams Transition

At index `i`, there are two choices.

---

## Choice 1 — Skip `i`

```text
skip
=
dp[i + 1][teamsUsed]
```

---

## Choice 2 — Start a team at `i`

The maximal valid group is:

```text
[i ... idx[i]]
```

Team size:

```text
idx[i] - i + 1
```

Next unused index:

```text
idx[i] + 1
```

So:

```text
take
=
(idx[i] - i + 1)
+
dp[idx[i] + 1][teamsUsed + 1]
```

---

## Final recurrence

```text
dp[i][teamsUsed]
=
max(
    skip,
    take
)
```

Compact:

```text
skip = dp[i + 1][teamsUsed]

take =
(idx[i] - i + 1)
+
dp[idx[i] + 1][teamsUsed + 1]

dp[i][teamsUsed]
=
max(skip, take)
```

---

## Final subproblem

```text
answer = dp[0][0]
```

---

# 15. K Balanced Teams Dry Run

Suppose sorted skills are:

```text
1 2 3 8 9 10
```

and:

```text
k = 2
```

For:

```text
i = 0
```

the furthest valid index is:

```text
idx[0] = 2
```

because:

```text
3 - 1 <= 5
```

but:

```text
8 - 1 > 5
```

So taking from index `0` gives:

```text
teamSize = 3
nextIndex = 3
```

Then the second team can take:

```text
8, 9, 10
```

for another:

```text
3
```

Total:

```text
6
```

---

# 16. Why Binary Search Can TLE Here

The lecture explicitly checks the constraint size:

```text
n, k <= 5000
```

A DP with about:

```text
n × k
```

states already has roughly:

```text
25,000,000
```

state positions.

If every state performs:

```text
O(log n)
```

work to find the endpoint, the extra factor is expensive.

So the lecture precomputes:

```text
idx[i]
```

once using two pointers.

Then each DP transition becomes:

```text
O(1)
```

Final intended complexity:

```text
sorting        = O(n log n)
preprocessing  = O(n)
DP             = O(nk)
```

---

# 17. K Balanced Teams C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int n, k;
vector<int> a, idx;
vector<vector<int>> dp;

int solve(int i, int teamsUsed) {
    if (i == n || teamsUsed == k) return 0;

    int &ans = dp[i][teamsUsed];
    if (ans != -1) return ans;

    int skip = solve(i + 1, teamsUsed);

    int nextIndex = idx[i] + 1;
    int teamSize = idx[i] - i + 1;
    int take = teamSize + solve(nextIndex, teamsUsed + 1);

    return ans = max(skip, take);
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> n >> k;

    a.resize(n);
    idx.resize(n);

    for (auto &x : a) cin >> x;

    sort(a.begin(), a.end());

    for (int i = 0, j = 0; i < n; i++) {
        if (j < i) j = i;

        while (j < n && a[j] - a[i] <= 5) {
            j++;
        }

        idx[i] = j - 1;
    }

    dp.assign(n, vector<int>(k, -1));

    cout << solve(0, 0) << '\n';

    return 0;
}
```

---

# 18. Problem 3 — Digit DP with Odd/Even Positions

The lecture revisits the previous Digit-DP homework:

> Count numbers in `[L,R]` such that:
>
> - odd-positioned digits are odd
> - even-positioned digits are even

The range can be as large as:

```text
10^18
```

So brute force is impossible.

We use Digit DP.

---

# 19. Why Leading Zeros Are the Hard Part

Suppose we are counting up to a 5-digit number.

The real number:

```text
143
```

appears in fixed-length Digit DP as:

```text
00143
```

But the real positions are:

```text
1st digit = 1
2nd digit = 4
3rd digit = 3
```

The leading zeros must not count as positions.

Therefore padded:

```text
index parity
```

is not enough.

We need:

```text
started
```

to know whether the actual number has begun.

---

# 20. Digit DP State

The lecture uses a state of the form:

```text
dp[index][tight][started][parity]
```

Meaning:

```text
index
=
current position in the bound string

tight
=
whether prefix still equals the bound prefix

started
=
whether a non-leading-zero digit
has appeared

parity
=
required parity of the NEXT real digit position
```

We use:

```text
parity = 0 → next real position is odd
parity = 1 → next real position is even
```

---

# 21. Digit DP Transition

At:

```text
(index, tight, started, parity)
```

first compute:

```text
boundDigit = bound[index]
limit = tight ? boundDigit : 9
```

Try:

```text
digit = 0 ... limit
```

---

## 21.1 Update `tight`

```text
newTight
=
tight
&&
(digit == boundDigit)
```

---

## 21.2 Leading zero

If:

```text
started == 0
and
digit == 0
```

this is still padding.

Do not apply odd/even position rules.

Keep:

```text
started = 0
parity = 0
```

---

## 21.3 First real digit

The first real position is odd.

So the first nonzero digit must be:

```text
1, 3, 5, 7, 9
```

Then:

```text
started = 1
```

and the next real position is even:

```text
parity = 1
```

---

## 21.4 Number already started

If:

```text
parity = 0
```

the next position is odd.

Require:

```text
digit % 2 == 1
```

If:

```text
parity = 1
```

the next position is even.

Require:

```text
digit % 2 == 0
```

After placing a valid digit:

```text
parity ^= 1
```

---

## 21.5 Base case

When all padded positions are processed:

```text
index == numberOfDigits
```

count only if:

```text
started == 1
```

because the problem range starts from `1`, not `0`.

---

# 22. Digit DP Dry Run

Take:

```text
R = 20
```

One-digit valid numbers:

```text
1
3
5
7
9
```

because position 1 is odd.

Two-digit numbers up to `20`:

```text
10
12
14
16
18
```

because:

```text
1st position → odd digit
2nd position → even digit
```

Total:

```text
10
```

This matches the lecture's handwritten enumeration.

---

# 23. Range `[L,R]`

Define:

```text
count(X)
=
number of valid positive integers
from 1 to X
```

Following the lecture's previous range formulation:

```text
answer
=
count(R)
-
count(L)
+
isValid(L)
```

This gives an inclusive `[L,R]` answer.

---

# 24. Digit DP C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

string boundValue;
ll memo[20][2][2][2];
bool seen[20][2][2][2];

ll solve(int index, int tight, int started, int parity) {
    if (index == (int)boundValue.size()) {
        return started;
    }

    ll &ans = memo[index][tight][started][parity];
    if (seen[index][tight][started][parity]) return ans;

    seen[index][tight][started][parity] = true;
    ans = 0;

    int boundDigit = boundValue[index] - '0';
    int limit = tight ? boundDigit : 9;

    for (int digit = 0; digit <= limit; digit++) {
        int newTight = tight && (digit == boundDigit);

        if (!started && digit == 0) {
            ans += solve(index + 1, newTight, 0, 0);
            continue;
        }

        if (!started) {
            if (digit % 2 == 0) continue;

            ans += solve(index + 1, newTight, 1, 1);
            continue;
        }

        int expectedParity = parity == 0 ? 1 : 0;
        if (digit % 2 != expectedParity) continue;

        ans += solve(index + 1, newTight, 1, parity ^ 1);
    }

    return ans;
}

ll countUpTo(const string &x) {
    boundValue = x;
    memset(seen, false, sizeof(seen));

    return solve(0, 1, 0, 0);
}

bool isValidNumber(const string &s) {
    for (int i = 0; i < (int)s.size(); i++) {
        int digit = s[i] - '0';
        int position = i + 1;

        if (position % 2 == 1 && digit % 2 == 0) return false;
        if (position % 2 == 0 && digit % 2 == 1) return false;
    }

    return true;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    string L, R;
    cin >> L >> R;

    ll answer = countUpTo(R) - countUpTo(L);

    if (isValidNumber(L)) {
        answer++;
    }

    cout << answer << '\n';

    return 0;
}
```

---

# 25. Digit DP Complexity

State dimensions:

```text
index   → at most about 19
tight   → 2
started → 2
parity  → 2
```

Each state tries at most:

```text
10 digits
```

So the lecture estimates roughly:

```text
digits × 2 × 2 × 2 × 10
```

or:

```text
80 × digits
```

which is tiny.


---

# 26. Homework — Powerful Integers

The lecture gives this only as homework:

**LeetCode — Count the Number of Powerful Integers**

https://leetcode.com/problems/count-the-number-of-powerful-integers/

The lecture file does **not** provide its full derivation or solution.

So use it as a practice problem for:

```text
Digit DP
tight
started
range counting
digit restrictions
suffix restrictions
```

Before solving, answer:

```text
1. What does count(X) mean?

2. Which digits are constrained?

3. Do leading zeros matter?

4. What state information affects future choices?

5. What does tight control?

6. Can any state parameter be eliminated?
```

---

# 27. Pattern Comparison

| Problem | Main transformation | DP state | Important optimization |
|---|---|---|---|
| Counting Tilings | Grid → boundary profile | `dp[row][mask]` | Precompute `transitions[mask]` |
| K Balanced Teams | Sort → valid interval | `dp[i][teamsUsed]` | Two-pointer `idx[i]` |
| Digit parity | Huge range → digit construction | `dp[index][tight][started][parity]` | Handle leading zeros correctly |

---

# 28. Contest Recognition Checklist

## 28.1 Counting Tilings

If you see:

```text
grid is long
one dimension <= 10
local pieces cross the boundary
```

think:

```text
profile / bitmask DP
```

Ask:

```text
What information from the processed part
affects the next slice?
```

That becomes:

```text
mask
```

---

## 28.2 Precompute transitions when

The transition depends only on:

```text
local state / mask
```

and the same local transition is reused for many:

```text
rows
columns
positions
```

Then precompute:

```text
transitions[state]
```

once.

---

## 28.3 K Balanced Teams

If the condition depends on:

```text
max - min
```

ask:

```text
Does sorting make each valid group contiguous?
```

If yes:

```text
sort
→ precompute endpoint
→ DP
```

---

## 28.4 Two pointers vs binary search

If for every sorted `i` you need:

```text
furthest valid j
```

and `j` moves monotonically:

```text
two pointers
```

can remove an unnecessary logarithmic factor.

The lecture's central optimization is:

```text
do not repeatedly binary-search
inside a huge DP state space
```

---

## 28.5 Digit DP

If:

```text
range is huge
```

and:

```text
condition depends on digits
```

think:

```text
Digit DP
```

Then identify:

```text
index
tight
started?
property state
```

---

## 28.6 When `started` is required

Use `started` if leading zeros would incorrectly change:

```text
digit position
digit count
previous digit
number length
```

This lecture's odd/even-position problem is exactly such a case.

---

# 29. Code / Transition Formatting Standard

Use this style in future notes.

### Good

```cpp
vector<int> a(n), idx(n);

for (auto &x : a) cin >> x;

sort(a.begin(), a.end());

for (int i = 0, j = 0; i < n; i++) {
    while (j < n && a[j] - a[i] <= 5) {
        j++;
    }

    idx[i] = j - 1;
}
```

### Transition formatting

```text
skip = dp[i + 1][teamsUsed]

take =
(idx[i] - i + 1)
+
dp[idx[i] + 1][teamsUsed + 1]

dp[i][teamsUsed]
=
max(skip, take)
```

Keep transitions:

```text
short
aligned
visually separated
```

Do not split every operator and function argument onto separate lines unless the expression is genuinely complex.

---

# 30. Final Mental Model

## Counting Tilings

```text
BIG GRID
   ↓
small width n <= 10
   ↓
one slice = bitmask
   ↓
precompute:
mask → nextMask list
   ↓
DP across slices
```

Core recurrence:

```text
dp[row][mask]
=
Σ dp[row + 1][nextMask]
```

---

## K Balanced Teams

```text
skills
   ↓
sort
   ↓
for every i:
compute idx[i]
   ↓
skip i
or
take [i ... idx[i]]
```

Core recurrence:

```text
skip = dp[i + 1][teams]

take =
idx[i] - i + 1
+
dp[idx[i] + 1][teams + 1]

dp[i][teams]
=
max(skip, take)
```

---

## Digit DP with Position Parity

```text
number <= X
   ↓
construct digit by digit
   ↓
tight controls bound
   ↓
started ignores leading zeros
   ↓
parity tracks REAL digit position
```

Core state:

```text
dp[index][tight][started][parity]
```

---

# Final Revision Box

```text
COUNTING TILINGS
────────────────────────────────

n <= 10
→ bitmask the small dimension

Precompute:
transitions[mask]

State:
dp[row][mask]

Transition:
sum dp[row+1][nextMask]

Base:
row == m
valid only if mask == 0


K BALANCED TEAMS
────────────────────────────────

Sort.

Precompute:

idx[i]
=
furthest j with
a[j] - a[i] <= 5

Use two pointers.

State:
dp[i][teamsUsed]

Skip:
dp[i+1][teamsUsed]

Take:
(idx[i]-i+1)
+
dp[idx[i]+1][teamsUsed+1]

Answer:
dp[0][0]


DIGIT DP POSITION PARITY
────────────────────────────────

Odd position:
odd digit

Even position:
even digit

Need:
index
tight
started
parity

Why started?
Leading zeros are not real digits.

State:
dp[index][tight][started][parity]


CORE ADVANCED-DP QUESTIONS
────────────────────────────────

1. Can I compress the boundary into a mask?

2. Can I precompute expensive transitions?

3. Does sorting create a contiguous structure?

4. Can two pointers remove repeated searches?

5. Do leading zeros change the meaning
   of digit positions?

6. What is the smallest correct DP state?
```
