# 012 — DP Advanced Problem Solving 3
## Road Optimization → Clear the String → Cunning Gena

> **Self-study goal:** Learn three advanced DP-design patterns from the lecture:
>
> 1. **Road Optimization** — choose the next kept checkpoint and account for skipped states.
> 2. **Clear the String** — interval DP where equal characters can be merged into one removal chain.
> 3. **Cunning Gena** — bitmask DP + sorting + rolling-array space optimization.
>
> Follow the same process:
>
> **Problem Asking → Story Removed → Observation → State → Choices → Transition → Base Case → Final Subproblem → Complexity → Optimization → Dry Run → C++17**
>
> **Code / transition format:** compact C++17, 4-space indentation, same-line braces, related variables together, short logical blocks, and simple recurrences kept visually compact.

---

# Problem Links

1. **Codeforces 1625C — Road Optimization**  
   https://codeforces.com/problemset/problem/1625/C

2. **Codeforces 1132F — Clear the String**  
   https://codeforces.com/contest/1132/problem/F

3. **Codeforces 417D — Cunning Gena**  
   https://codeforces.com/problemset/problem/417/D

4. **Additional lecture exercise**  
   Remove any palindromic subarray in one move. Find the minimum moves needed to make the array empty.

---

# Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [Problem-Solving Map](#2-problem-solving-map)
3. [Problem 1 — Road Optimization](#3-problem-1--road-optimization)
4. [Road Cost Model](#4-road-cost-model)
5. [Why the Next Kept Sign Is the Choice](#5-why-the-next-kept-sign-is-the-choice)
6. [Road Optimization State](#6-road-optimization-state)
7. [Road Optimization Transition](#7-road-optimization-transition)
8. [Virtual Destination Sign](#8-virtual-destination-sign)
9. [Road Optimization Dry Run](#9-road-optimization-dry-run)
10. [Road Optimization Complexity](#10-road-optimization-complexity)
11. [Road Optimization C++17](#11-road-optimization-c17)
12. [Lecture Rolling-Layer Observation](#12-lecture-rolling-layer-observation)
13. [Problem 2 — Clear the String](#13-problem-2--clear-the-string)
14. [Why Interval DP Fits](#14-why-interval-dp-fits)
15. [Clear the String State](#15-clear-the-string-state)
16. [Baseline Transition](#16-baseline-transition)
17. [Merge Equal Characters](#17-merge-equal-characters)
18. [Final Clear-the-String Recurrence](#18-final-clear-the-string-recurrence)
19. [Clear the String Dry Run](#19-clear-the-string-dry-run)
20. [Clear the String Complexity](#20-clear-the-string-complexity)
21. [Clear the String C++17](#21-clear-the-string-c17)
22. [Problem 3 — Cunning Gena](#22-problem-3--cunning-gena)
23. [Bitmask Representation](#23-bitmask-representation)
24. [Why Sorting by Monitor Requirement Helps](#24-why-sorting-by-monitor-requirement-helps)
25. [Cunning Gena State](#25-cunning-gena-state)
26. [Take / Skip Transition](#26-take--skip-transition)
27. [Why the Recursive Table MLEs](#27-why-the-recursive-table-mles)
28. [Rolling-Array Space Optimization](#28-rolling-array-space-optimization)
29. [Cunning Gena Dry Run](#29-cunning-gena-dry-run)
30. [Cunning Gena C++17](#30-cunning-gena-c17)
31. [Additional Lecture Exercise](#31-additional-lecture-exercise)
32. [Pattern Comparison](#32-pattern-comparison)
33. [Contest Recognition Checklist](#33-contest-recognition-checklist)
34. [Final Mental Model](#34-final-mental-model)

---

# 1. Prerequisites

## 1.1 Minimum-cost DP

If the problem asks:

```text
minimum cost
minimum time
minimum moves
```

the recurrence usually takes:

```text
min(...)
```

Impossible states should use:

```text
INF
```

so they are never accidentally selected.

---

## 1.2 "Choose the next important position" DP

Sometimes the input contains many positions, but only some remain active after deletions.

Instead of deciding:

```text
remove / keep every position one by one
```

we can directly choose:

```text
which position is the next kept one?
```

This often gives a simpler recurrence.

Road Optimization uses this idea.

---

## 1.3 Interval DP

If the operation changes a contiguous substring/subarray, try:

```text
dp[left][right]
```

Meaning:

```text
answer for interval [left ... right]
```

Typical transitions:

```text
remove one boundary

or

connect / merge equal values
across the interval
```

Clear the String is an interval DP.

---

## 1.4 Empty interval base case

For interval DP it is convenient to define:

```text
left > right
→ empty interval
→ cost 0
```

Then:

```text
one character
```

naturally costs:

```text
1
```

---

## 1.5 Bitmask DP refresher

If there are:

```text
m <= 20
```

binary requirements/problems, represent solved problems by an `m`-bit mask.

```text
bit j = 1
→ problem j is already solved
```

Full mask:

```cpp
int fullMask = (1 << m) - 1;
```

Combine capabilities:

```cpp
int newMask = mask | personMask;
```

---

## 1.6 Take / skip DP

For each candidate/item:

```text
skip it

or

take it
```

Generic recurrence:

```text
dp[i][state]
=
min(
    dp[i+1][state],
    cost[i] + dp[i+1][updatedState]
)
```

Cunning Gena uses exactly this structure.

---

## 1.7 Rolling-array optimization

If:

```text
dp[i][state]
```

depends only on:

```text
dp[i+1][...]
```

we do not need all `n` index layers.

Keep only:

```text
next layer
current layer
```

Then:

```text
O(n × states)
```

memory becomes:

```text
O(states)
```

This is the key memory fix in Cunning Gena.

---

## 1.8 `long long`

Use:

```cpp
long long
```

for:

```text
travel times
total costs
large INF values
```

No `__int128` is needed in these lecture patterns.

---

# 2. Problem-Solving Map

```text
ROAD OPTIMIZATION
    ↓
can delete at most k signs
    ↓
choose next kept sign
    ↓
dp[index][skipsLeft]


CLEAR THE STRING
    ↓
operation affects substring
    ↓
equal characters can merge removals
    ↓
dp[left][right]


CUNNING GENA
    ↓
m <= 20 problems
    ↓
mask solved problems
    ↓
sort by monitor requirement
    ↓
take / skip DP
    ↓
roll index dimension to avoid MLE
```

---

# 3. Problem 1 — Road Optimization

## Codeforces 1625C

The road has sign positions:

```text
pos[0], pos[1], ..., pos[n-1]
```

Each sign has a speed value:

```text
speed[0], speed[1], ..., speed[n-1]
```

Road length:

```text
L
```

We may remove at most:

```text
K
```

signs.

Goal:

```text
minimum total travel cost/time
to reach L
```

The lecture's key idea is:

```text
fix the current kept sign
and choose the next kept sign
```

---

# 4. Road Cost Model

Suppose sign `i` is currently active and the next kept sign is `j`.

We travel:

```text
pos[j] - pos[i]
```

distance using:

```text
speed[i]
```

So segment cost is:

```text
(pos[j] - pos[i]) * speed[i]
```

This is the cost formula used throughout the lecture.

---

# 5. Why the Next Kept Sign Is the Choice

Suppose:

```text
i < j
```

and we keep sign `j` as the next active sign.

Then every sign strictly between them is skipped:

```text
i+1, i+2, ..., j-1
```

Number skipped:

```text
j - i - 1
```

So if we currently have:

```text
skipsLeft
```

we may choose `j` only when:

```text
j - i - 1 <= skipsLeft
```

After choosing `j`:

```text
newSkipsLeft
=
skipsLeft - (j - i - 1)
```

---

# 6. Road Optimization State

The lecture first sketches a larger state and then reaches the compact state used in code.

Define:

```text
dp[index][skipsLeft]
=
minimum cost to reach L

when:
- sign `index` is currently kept
- we may still skip at most `skipsLeft` signs
```

This state is enough because once `index` is fixed:

```text
speed[index]
```

determines the travel cost until the next kept sign.

---

# 7. Road Optimization Transition

From current sign:

```text
index
```

try every possible next kept sign:

```text
next = index + 1 ... n
```

For each `next`:

```text
skipped
=
next - index - 1
```

If:

```text
skipped > skipsLeft
```

the transition is invalid.

Otherwise:

```text
travelCost
=
(pos[next] - pos[index])
*
speed[index]
```

and:

```text
candidate
=
travelCost
+
dp[next][skipsLeft - skipped]
```

Take the minimum.

---

## 7.1 Compact recurrence

```text
dp[index][skipsLeft]
=
min over valid next:

(pos[next] - pos[index]) * speed[index]
+
dp[next][skipsLeft - (next-index-1)]
```

This is the main lecture recurrence.

---

# 8. Virtual Destination Sign

The lecture adds a virtual final element:

```text
pos[n] = L
```

This lets the destination behave like one more checkpoint.

Then every transition has the same shape:

```text
current sign
→ next sign / destination
```

No special "travel from last sign to L" case is needed inside the loop.

We may set:

```text
speed[n] = 0
```

because no travel occurs after reaching `L`.

---

## 8.1 Base case

When:

```text
index == n
```

we are already at the destination.

Return:

```text
0
```

---

## 8.2 Final subproblem

We begin at the first sign with all deletions available:

```text
answer = dp[0][K]
```

---

# 9. Road Optimization Dry Run

Suppose:

```text
current sign = i
next kept sign = j
```

with:

```text
i = 1
j = 4
```

Signs skipped:

```text
2, 3
```

Count:

```text
4 - 1 - 1
=
2
```

If:

```text
skipsLeft = 3
```

this is valid.

New skips:

```text
3 - 2
=
1
```

Segment cost:

```text
(pos[4] - pos[1]) * speed[1]
```

Future:

```text
dp[4][1]
```

Candidate:

```text
(pos[4] - pos[1]) * speed[1]
+
dp[4][1]
```

The DP tries every valid `j` and keeps the minimum.

---

# 10. Road Optimization Complexity

States:

```text
n × (K+1)
```

For every state, try up to:

```text
O(n)
```

next signs.

Therefore:

```text
Time
=
O(n²K)
```

Since `K` can be `O(n)`, the lecture summarizes this as:

```text
O(n³)
```

Space:

```text
O(nK)
```

which becomes:

```text
O(n²)
```

in the worst case.

---

# 11. Road Optimization C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll INF = (1LL << 60);

int n, L, K;
vector<int> pos, speedValue;
vector<vector<ll>> dp;

ll solve(int index, int skipsLeft) {
    if (index == n) return 0;

    ll &ans = dp[index][skipsLeft];
    if (ans != -1) return ans;

    ans = INF;

    for (int next = index + 1; next <= n; next++) {
        int skipped = next - index - 1;
        if (skipped > skipsLeft) break;

        ll travelCost = 1LL * (pos[next] - pos[index]) * speedValue[index];
        ll candidate = travelCost + solve(next, skipsLeft - skipped);

        ans = min(ans, candidate);
    }

    return ans;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> n >> L >> K;

    pos.resize(n + 1);
    speedValue.resize(n + 1);

    for (int i = 0; i < n; i++) cin >> pos[i];
    for (int i = 0; i < n; i++) cin >> speedValue[i];

    pos[n] = L;
    speedValue[n] = 0;

    dp.assign(n, vector<ll>(K + 1, -1));

    cout << solve(0, K) << '\n';

    return 0;
}
```

---

# 12. Lecture Rolling-Layer Observation

The lecture also sketches another formulation where a current scanning index is processed iteratively and the DP keeps information such as:

```text
previous kept sign
skips used / left
```

In that formulation, the current scan-index dimension can be rolled because only the next layer is needed.

Visual idea from the lecture:

```text
dp[j][k]
=
already calculated layer

nextDp[j][k]
=
layer currently being calculated
```

The important lesson is:

```text
If one DP dimension is only "time / iteration"
and transitions use only the previous layer,
keep two layers instead of all layers.
```

For Road Optimization, the direct "choose next kept sign" memoization above is the cleanest lecture-derived state.

---

# 13. Problem 2 — Clear the String

## Codeforces 1132F

We have a string.

In one move, we may remove:

```text
a contiguous substring
whose characters are all equal
```

After removal, the remaining pieces join together.

Goal:

```text
minimum moves to erase the whole string
```

Example from the lecture:

```text
abaca
```

can be removed in:

```text
3 moves
```

The key difficulty is:

```text
characters that are separated now
may become adjacent after deleting
the characters between them
```

That makes interval DP natural.

---

# 14. Why Interval DP Fits

A deletion acts on a contiguous part of the current string.

The effect of removing the middle can connect:

```text
left-side character
with
a same character later
```

So define the problem on:

```text
substring [left ... right]
```

---

# 15. Clear the String State

Define:

```text
dp[left][right]
=
minimum moves required
to erase s[left ... right]
```

---

## 15.1 Base cases

Empty interval:

```text
left > right
→ 0
```

One character:

```text
left == right
→ 1
```

The recurrence below naturally handles the one-character case.

---

# 16. Baseline Transition

The simplest strategy is:

```text
remove s[left] by itself
```

That costs:

```text
1 move
```

and leaves:

```text
[left+1 ... right]
```

So:

```text
answer
=
1 + dp[left+1][right]
```

This is always valid.

It gives us an initial upper bound.

---

# 17. Merge Equal Characters

Now comes the important observation.

Suppose:

```text
s[left] == s[i]
```

for some:

```text
i > left
```

If we completely remove:

```text
[left+1 ... i-1]
```

then:

```text
s[left]
```

and:

```text
s[i]
```

become adjacent.

We can then remove `s[left]` together with the operation that eventually removes `s[i]`.

So we do **not** need an extra `+1` for `s[left]`.

---

## 17.1 Cost when matching `left` with `i`

First erase the middle:

```text
dp[left+1][i-1]
```

Then solve:

```text
dp[i][right]
```

where `s[i]` represents the removal chain that can also absorb `s[left]`.

Candidate:

```text
dp[left+1][i-1]
+
dp[i][right]
```

---

# 18. Final Clear-the-String Recurrence

Start with:

```text
answer
=
1 + dp[left+1][right]
```

Then for every:

```text
i = left+1 ... right
```

if:

```text
s[i] == s[left]
```

try:

```text
candidate
=
dp[left+1][i-1]
+
dp[i][right]
```

Final:

```text
dp[left][right]
=
min(
    1 + dp[left+1][right],

    dp[left+1][i-1] + dp[i][right]
    for every i with s[i] == s[left]
)
```

This is the final recurrence shown in the lecture.

---

## 18.1 Lecture strategy progression

The handwritten notes consider ways of associating equal characters:

```text
1. first character with the last equal character

2. last character with the first equal character

3. first character with the next equal character in the chain
```

The final recurrence effectively tries every valid next equal character:

```text
s[left] == s[i]
```

and chooses the minimum.

---

# 19. Clear the String Dry Run

Use:

```text
s = "aba"
```

We want:

```text
dp[0][2]
```

---

## Baseline

Remove first `'a'` alone:

```text
1 + dp[1][2]
```

For `"ba"`:

```text
dp[1][2] = 2
```

So baseline:

```text
3
```

---

## Merge the two `'a'` characters

We have:

```text
s[0] == s[2]
```

Choose:

```text
i = 2
```

Middle:

```text
dp[1][1] = 1
```

Right chain:

```text
dp[2][2] = 1
```

Candidate:

```text
1 + 1
=
2
```

Interpretation:

```text
aba
↓ remove b
aa
↓ remove aa
empty
```

So:

```text
dp[0][2] = 2
```

---

# 20. Clear the String Complexity

Number of intervals:

```text
O(n²)
```

For every interval, try up to:

```text
O(n)
```

matching positions.

Therefore:

```text
Time
=
O(n³)
```

Space:

```text
O(n²)
```

---

# 21. Clear the String C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

string s;
vector<vector<int>> dp;

int solve(int left, int right) {
    if (left > right) return 0;

    int &ans = dp[left][right];
    if (ans != -1) return ans;

    ans = 1 + solve(left + 1, right);

    for (int i = left + 1; i <= right; i++) {
        if (s[i] != s[left]) continue;

        int middle = solve(left + 1, i - 1);
        int remaining = solve(i, right);

        ans = min(ans, middle + remaining);
    }

    return ans;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n >> s;

    dp.assign(n, vector<int>(n, -1));

    cout << solve(0, n - 1) << '\n';

    return 0;
}
```

---

# 22. Problem 3 — Cunning Gena

## Codeforces 417D

The lecture's third problem combines:

```text
sorting
+
bitmask DP
+
space optimization
```

There are up to roughly:

```text
m <= 20
```

problems/skills to cover.

Each person has lecture fields equivalent to:

```text
cost
monitor requirement
set of problems they can solve
```

The set of solvable problems becomes a bitmask.

The final objective also includes the monitor term, so the lecture sorts people by monitor requirement.


---

# 23. Bitmask Representation

Suppose:

```text
m = 5
```

and a person can solve problems:

```text
1, 3, 5
```

Then the person's capability can be represented by:

```text
10101
```

depending on bit order.

Use:

```text
bit j = 1
→ this person can solve problem j
```

If current solved-problem state is:

```text
mask
```

and the person can solve:

```text
personMask
```

then after taking this person:

```text
newMask
=
mask | personMask
```

The goal is:

```text
fullMask
=
(1 << m) - 1
```

---

# 24. Why Sorting by Monitor Requirement Helps

The lecture sorts people by monitor requirement.

Its handwritten notes show:

```text
descending order of monitor requirement
```

and then process the array from the back.

That means the iterative DP effectively introduces people in:

```text
increasing monitor requirement
```

order.

Why is that useful?

Because after processing everyone with monitor requirement at most some value:

```text
currentMonitor
```

if we can already cover all problems, then:

```text
currentMonitor
```

is enough as the monitor level for that candidate solution.

So after each new monitor threshold, evaluate:

```text
minimum hiring cost
+
currentMonitor * b
```

The lecture therefore avoids solving a completely separate DP for every possible monitor threshold.

---

# 25. Cunning Gena State

The lecture first writes a recursive state:

```text
dp[i][mask]
=
minimum hiring cost needed
using people [i ... n-1]

when problems already solved
are represented by `mask`
```

---

## 25.1 Base case

When:

```text
i == n
```

if:

```text
mask == fullMask
```

all problems are covered:

```text
return 0
```

Otherwise:

```text
return INF
```

---

# 26. Take / Skip Transition

At person `i`, we have two choices.

---

## Choice 1 — Do not take this person

```text
skip
=
dp[i + 1][mask]
```

---

## Choice 2 — Take this person

Pay:

```text
cost[i]
```

and update solved problems:

```text
newMask
=
mask | personMask[i]
```

So:

```text
take
=
cost[i]
+
dp[i + 1][newMask]
```

---

## Final recurrence

```text
dp[i][mask]
=
min(
    skip,
    take
)
```

Compact:

```text
skip = dp[i + 1][mask]

take =
cost[i]
+
dp[i + 1][mask | personMask[i]]

dp[i][mask]
=
min(skip, take)
```

This is the exact take / not-take structure drawn in the lecture.

---

# 27. Why the Recursive Table MLEs

The direct memo table has:

```text
n × 2^m
```

states.

The lecture highlights a case around:

```text
n ≈ 100
m <= 20
```

Then:

```text
2^20
≈
1,048,576
```

So:

```text
100 × 2^20
```

is around:

```text
10^8
```

DP entries.

That is too much memory.

This is why the lecture marks the recursive table as:

```text
MLE
```

---

# 28. Rolling-Array Space Optimization

Observe:

```text
dp[i][mask]
```

depends only on:

```text
dp[i + 1][...]
```

So the complete `i` dimension is unnecessary.

Keep only:

```text
dpNext[mask]
dpCurr[mask]
```

---

## 28.1 Base layer

After all people have been processed:

```text
dpNext[fullMask] = 0
```

Every incomplete mask is impossible:

```text
dpNext[mask != fullMask] = INF
```

---

## 28.2 Build one layer

For each mask:

```text
skip
=
dpNext[mask]
```

```text
take
=
cost[i]
+
dpNext[mask | personMask[i]]
```

Then:

```text
dpCurr[mask]
=
min(skip, take)
```

After finishing the layer:

```text
swap(dpCurr, dpNext)
```

Now `dpNext` stores the newly calculated layer.

---

## 28.3 Space

Before:

```text
O(n × 2^m)
```

After:

```text
O(2^m)
```

This is the lecture's main memory optimization.

---

# 29. Cunning Gena Dry Run

Suppose there are:

```text
m = 3
```

problems.

Full mask:

```text
111
```

Suppose one person has:

```text
personMask = 101
cost = 5
```

Current solved mask:

```text
010
```

If we take this person:

```text
010
OR
101
=
111
```

So:

```text
newMask = 111
```

and all problems become solved.

The take candidate is:

```text
5
+
dpNext[111]
```

If:

```text
dpNext[111] = 0
```

then:

```text
take = 5
```

Compare with:

```text
skip = dpNext[010]
```

and take the minimum.

---

## 29.1 Monitor-threshold idea

Suppose people are sorted by monitor requirement:

```text
5, 3, 2, 1, 0
```

and we iterate backward:

```text
0
1
2
3
5
```

After processing requirement `2`, the current DP knows the cheapest way to solve problems using only people with monitor requirement up to `2`.

If all problems can be solved:

```text
hireCost = dpCurrent[0]
```

Candidate total:

```text
hireCost
+
2 * b
```

Then continue to larger monitor thresholds and keep the minimum.

---

# 30. Cunning Gena C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll INF = (1LL << 60);

struct Person {
    ll cost;
    int monitor;
    int mask;
};

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    ll b;

    cin >> n >> m >> b;

    vector<Person> people(n);

    for (auto &person : people) {
        cin >> person.cost >> person.monitor;

        int count;
        cin >> count;

        person.mask = 0;

        while (count--) {
            int problem;
            cin >> problem;
            problem--;

            person.mask |= (1 << problem);
        }
    }

    sort(people.begin(), people.end(), [](const Person &a, const Person &b) {
        return a.monitor > b.monitor;
    });

    int fullMask = (1 << m) - 1;
    int totalMasks = 1 << m;

    vector<ll> dpNext(totalMasks, INF);
    vector<ll> dpCurr(totalMasks, INF);

    dpNext[fullMask] = 0;

    ll answer = INF;

    for (int i = n - 1; i >= 0; i--) {
        for (int mask = 0; mask < totalMasks; mask++) {
            ll skip = dpNext[mask];
            int newMask = mask | people[i].mask;

            ll take = INF;

            if (dpNext[newMask] != INF) {
                take = people[i].cost + dpNext[newMask];
            }

            dpCurr[mask] = min(skip, take);
        }

        if (dpCurr[0] != INF) {
            ll totalCost = dpCurr[0] + 1LL * people[i].monitor * b;
            answer = min(answer, totalCost);
        }

        swap(dpCurr, dpNext);
    }

    if (answer == INF) {
        cout << -1 << '\n';
    } else {
        cout << answer << '\n';
    }

    return 0;
}
```

---

## 30.1 Complexity

For every person:

```text
n
```

iterate all masks:

```text
2^m
```

Each transition is:

```text
O(1)
```

Therefore:

```text
Time
=
O(n × 2^m)
```

Space:

```text
O(2^m)
```

This is the optimization target emphasized in the lecture.

---

# 31. Additional Lecture Exercise

The lecture begins with an additional task:

> In one move, remove any subarray that is a palindrome. Find the minimum number of moves required to make the array empty.

The notes label this as:

```text
Additional Task
GOOD Try!!
```

but do not provide its full derivation or solution in this lecture file.

So keep it as a practice problem.

Useful questions to ask:

```text
1. Is this an interval DP?

2. Can equal boundary values
   be merged after removing the middle?

3. What should dp[left][right] mean?

4. What is the baseline move?

5. What split / merge transitions
   reduce the number of moves?
```

Do not memorize a solution here; the source only presents it as an exercise.

---

# 32. Pattern Comparison

| Problem | Main state | Core choice | Key optimization |
|---|---|---|---|
| Road Optimization | `dp[index][skipsLeft]` | choose next kept sign | Virtual destination + direct jump |
| Clear the String | `dp[left][right]` | remove first alone or merge with equal char | Interval merge recurrence |
| Cunning Gena | `dp[i][mask]` → rolling `dp[mask]` | take / skip person | Sort + rolling arrays |

---

# 33. Contest Recognition Checklist

## 33.1 Road Optimization smell

If you see:

```text
ordered checkpoints
can remove up to K
cost between kept checkpoints
depends on earlier kept checkpoint
```

think:

```text
choose the next kept checkpoint
```

State:

```text
current kept index
+
skips left
```

Transition cost:

```text
distance
×
current speed/cost
```

---

## 33.2 Count skipped elements carefully

If jumping:

```text
i → j
```

the skipped positions are:

```text
i+1 ... j-1
```

Count:

```text
j - i - 1
```

This small formula is central to the Road Optimization transition.

---

## 33.3 Add a virtual endpoint when

The final destination behaves like:

```text
one more transition target
```

Then add:

```text
pos[n] = destination
```

to avoid a separate final case.

---

## 33.4 Clear-the-String smell

If:

```text
deletions make separated equal characters become adjacent
```

think:

```text
interval DP
```

Baseline:

```text
remove one boundary alone
```

Improvement:

```text
merge it with a later equal character
```

---

## 33.5 Key equal-character recurrence

If:

```text
s[left] == s[i]
```

try:

```text
dp[left+1][i-1]
+
dp[i][right]
```

The important insight is:

```text
no extra +1
```

for `s[left]`, because it can be removed together with the chain containing `s[i]`.

---

## 33.6 Cunning Gena smell

If:

```text
m <= 20 requirements
```

and every candidate covers a subset:

```text
use a bitmask
```

If each candidate also has:

```text
cost
+
threshold/maximum-dependent charge
```

look for:

```text
sorting by the threshold
```

so the threshold can be fixed while the subset DP handles the remaining cost.

---

## 33.7 Memory check before coding

For:

```text
dp[n][1<<m]
```

calculate the size first.

Example:

```text
n = 100
m = 20
```

gives:

```text
~10^8 states
```

That is a warning sign.

Ask:

```text
Does layer i depend only on layer i+1?
```

If yes:

```text
roll the dimension
```

---

# 34. Final Mental Model

## Road Optimization

```text
CURRENT KEPT SIGN i
        |
        v
choose next kept sign j
        |
        v
skip j-i-1 signs
        |
        v
cost:
(pos[j]-pos[i]) * speed[i]
        |
        v
dp[j][remaining skips]
```

Core recurrence:

```text
dp[i][k]
=
min over valid j:

(pos[j]-pos[i]) * speed[i]
+
dp[j][k-(j-i-1)]
```

---

## Clear the String

```text
substring [L ... R]

baseline:
remove s[L] alone
→ 1 + dp[L+1][R]

if s[L] == s[i]:
remove middle first
then merge L with i

→ dp[L+1][i-1] + dp[i][R]
```

Core recurrence:

```text
dp[L][R]
=
min(
    1 + dp[L+1][R],

    dp[L+1][i-1] + dp[i][R]
    when s[L] == s[i]
)
```

---

## Cunning Gena

```text
m <= 20
   ↓
problem set = bitmask
   ↓
sort by monitor requirement
   ↓
take / skip each person
   ↓
dp[i][mask]
   ↓
index layer only depends on next layer
   ↓
roll to:
dpNext[mask]
dpCurr[mask]
```

Core transition:

```text
skip = dpNext[mask]

take =
cost[i]
+
dpNext[mask | personMask[i]]

dpCurr[mask]
=
min(skip, take)
```

---

# Final Revision Box

```text
ROAD OPTIMIZATION
────────────────────────────────

State:
dp[index][skipsLeft]

Jump:
index → next

Skipped:
next - index - 1

Cost:
(pos[next] - pos[index])
*
speed[index]

Transition:
min over valid next

Virtual destination:
pos[n] = L


CLEAR THE STRING
────────────────────────────────

State:
dp[left][right]

Baseline:
1 + dp[left+1][right]

If:
s[left] == s[i]

Try:
dp[left+1][i-1]
+
dp[i][right]

Time:
O(n^3)

Space:
O(n^2)


CUNNING GENA
────────────────────────────────

m <= 20
→ bitmask

State:
dp[i][mask]

Skip:
dp[i+1][mask]

Take:
cost[i]
+
dp[i+1][mask | personMask[i]]

Problem:
n × 2^m memory can MLE

Fix:
rolling arrays

dpNext[mask]
dpCurr[mask]

Time:
O(n × 2^m)

Space:
O(2^m)


CODE STYLE
────────────────────────────────

Keep simple code compact:

int skipped = next - index - 1;
ll travelCost = 1LL * (pos[next] - pos[index]) * speedValue[index];

Keep related declarations together.
Use same-line braces.
Avoid splitting simple expressions
into visually crowded fragments.


CORE ADVANCED-DP QUESTION
────────────────────────────────

What information truly affects
the future subproblem,

and which dimension can be
eliminated, jumped over,
or rolled away?
```
