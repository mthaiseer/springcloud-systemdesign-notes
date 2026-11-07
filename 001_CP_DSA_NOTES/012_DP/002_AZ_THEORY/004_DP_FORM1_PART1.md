# 015 — The Magical DP Framework: Day 2 Part 1
## N Problems + Time Limit X + At Most K Problems → Maximum Skill

> **Main lesson:** If the same configuration repeats, the answer from that configuration stays the same. Save it and reuse it.
>
> The lecture framework is:
>
> ```text
> 1. Look for the form of the problem
> 2. Decide states & meaning
> 3. Decide transitions
> 4. Check time complexity
> 5. Code
> ```
>
> All future recursive DP code in these notes follows:
>
> ```text
> 1. Pruning
> 2. Base Case
> 3. Cache Check
> 4. Compute / Transition
> 5. Save and Return
> ```
>
> **Formatting rule:** keep simple transitions on one readable line.
>
> ```cpp
> take = skill[level] + rec(level + 1, timeTaken + timeCost[level], itemTaken + 1);
> ```

---

# 1. Prerequisites

You should already know:

```text
recursion
memoization
take / not-take recursion
maximum / optimization DP
3D DP states
```

The most important DP idea from the lecture is:

```text
same state parameters
→ same future problem
→ same answer
```

Therefore:

```text
solve once
cache
reuse
```

---

# 2. Problem Statement

There are:

```text
N problems
```

Each problem `i` has:

```text
T[i] = time needed
S[i] = skill gained
```

You have:

```text
X = maximum total time

K = maximum number of problems you may solve
```

Goal:

```text
maximize total skill
```

subject to:

```text
sum of selected T[i] <= X
```

and:

```text
number of selected problems <= K
```

---

# 3. What the Lecture Is Teaching

## 3.1 Same configuration repeats

If two recursive paths reach:

```text
rec(5, 6, 2)
```

both states mean:

```text
current problem = 5
time already used = 6
problems already selected = 2
```

The future problem is identical.

So:

```text
rec(5,6,2)
```

must have one fixed answer.

That is why memoization works.

---

## 3.2 We need to design the state smartly

We do **not** need to remember the entire chosen subset.

The future only needs:

```text
where we are
how much time is already used
how many problem slots are already used
```

So these become the state parameters.

---

## 3.3 Printing is separate

The DP first finds:

```text
maximum skill
```

Then we can walk through the optimal decisions and print:

```text
which problems were selected
```

---

# 4. Step 1 — Decide the Form

At every problem there are only two choices:

```text
TAKE
or
NOT TAKE
```

So this is:

```text
Form = Choose a subset
```

Without DP, the decision tree can have:

```text
2^N
```

paths.

---

# 5. Step 2 — State Space Formulation

The lecture state is:

```text
DP(level, timeTaken, itemTaken)
```

Define:

```text
rec(level, timeTaken, itemTaken)
=
maximum additional skill
we can obtain from problems
[level ... N-1]

when:

timeTaken
=
total time already used

itemTaken
=
number of problems already selected
```

Initial state:

```text
rec(0, 0, 0)
```

---

# 6. Meaning of Each State Variable

## `level`

```text
current problem under consideration
```

Problems:

```text
[0 ... level-1]
```

are already decided.

Problems:

```text
[level ... N-1]
```

are still available.

---

## `timeTaken`

```text
total time already consumed
```

Constraint:

```text
timeTaken <= X
```

If current problem is taken:

```text
newTimeTaken = timeTaken + T[level]
```

---

## `itemTaken`

```text
number of problems already selected
```

Constraint:

```text
itemTaken <= K
```

If current problem is taken:

```text
newItemTaken = itemTaken + 1
```

---

# 7. Why This State Is Sufficient

Ask:

```text
Do we need to know exactly WHICH previous problems were selected?
```

No.

The future only depends on:

```text
remaining problems
remaining time
remaining slots
```

These are determined by:

```text
level
timeTaken
itemTaken
```

So the exact earlier history can be forgotten.

This is the state-compression idea.

---

# 8. Step 3 — Transition Design

At state:

```text
rec(level, timeTaken, itemTaken)
```

we have two choices.

---

## 8.1 Choice 1 — Do Not Take

Skip current problem.

```text
notTake = rec(level + 1, timeTaken, itemTaken)
```

No skill is added.

---

## 8.2 Choice 2 — Take

Allowed only if both constraints remain valid:

```text
timeTaken + T[level] <= X
```

and:

```text
itemTaken + 1 <= K
```

Then:

```text
take = S[level] + rec(level + 1, timeTaken + T[level], itemTaken + 1)
```

---

## 8.3 Final Recurrence

```text
notTake = rec(level + 1, timeTaken, itemTaken)

take = S[level] + rec(level + 1, timeTaken + T[level], itemTaken + 1)

answer = max(notTake, take)
```

`take` is considered only when both constraints are satisfied.

---

# 9. Pruning

For this problem, invalid states are:

```text
timeTaken > X
```

or:

```text
itemTaken > K
```

Because we maximize, invalid states should return a very bad value:

```text
NEG_INF
```

```cpp
if (timeTaken > X || itemTaken > K) return NEG_INF;
```

Even if the transition already prevents invalid calls, this pruning makes the recursion structure explicit.

---

# 10. Base Case

When:

```text
level == N
```

there are no more problems.

No additional skill can be gained.

```cpp
if (level == n) return 0;
```

---

# 11. Cache Check

Memo table:

```text
dp[level][timeTaken][itemTaken]
```

Before recomputing:

```cpp
int &ans = dp[level][timeTaken][itemTaken];
if (ans != -1) return ans;
```

This is what changes exponential recursion into DP.

---

# 12. Step 4 — Time Complexity

The lecture gives the general formula:

```text
Time Complexity
=
Number of States
×
(1 + Average Transition Cost)
```

For this problem:

```text
level      → N possibilities
timeTaken  → X + 1 possibilities
itemTaken  → K + 1 possibilities
```

So:

```text
#states
=
N × (X + 1) × (K + 1)
```

Therefore:

```text
O(NXK)
```

states.

Each state has at most:

```text
2 transitions
```

So:

```text
Time = O(NXK)
```

Memo memory:

```text
Space = O(NXK)
```

Recursion stack:

```text
O(N)
```

---

# 13. Dry Run

Lecture-style example:

```text
N = 5
X = 6
K = 2
```

Problems:

```text
index:   0      1      2      3      4

(T,S):  (3,3)  (5,4)  (4,4)  (2,3)  (1,1)
```

Start:

```text
rec(0,0,0)
```

At problem `0`:

```text
notTake = rec(1,0,0)
```

Taking is valid because:

```text
0 + 3 <= 6
0 + 1 <= 2
```

So:

```text
take = 3 + rec(1,3,1)
```

A strong valid combination is:

```text
problem 2 → (4,4)
problem 3 → (2,3)
```

Total:

```text
time  = 4 + 2 = 6
count = 2
skill = 4 + 3 = 7
```

The DP checks all valid possibilities while caching repeated states.

---

# 14. Complete C++17 — Maximum Skill

```cpp
#include <bits/stdc++.h>
using namespace std;

const int NEG_INF = -1e9;

int n, X, K;
vector<int> timeCost, skill;
vector<vector<vector<int>>> dp;

int rec(int level, int timeTaken, int itemTaken) {
    // 1. Pruning
    if (timeTaken > X || itemTaken > K) return NEG_INF;

    // 2. Base Case
    if (level == n) return 0;

    // 3. Cache Check
    int &ans = dp[level][timeTaken][itemTaken];
    if (ans != -1) return ans;

    // 4. Compute / Transition
    int notTake = rec(level + 1, timeTaken, itemTaken);
    int take = NEG_INF;

    if (timeTaken + timeCost[level] <= X && itemTaken + 1 <= K) {
        take = skill[level] + rec(level + 1, timeTaken + timeCost[level], itemTaken + 1);
    }

    ans = max(notTake, take);

    // 5. Save and Return
    return ans;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> n >> X >> K;

    timeCost.resize(n);
    skill.resize(n);

    for (int i = 0; i < n; i++) {
        cin >> timeCost[i] >> skill[i];
    }

    dp.assign(n, vector<vector<int>>(X + 1, vector<int>(K + 1, -1)));

    cout << rec(0, 0, 0) << '\n';

    return 0;
}
```


---

# 15. Printing the Chosen Problems

The lecture also asks:

```text
What about printing?
```

Finding the optimal value and printing the chosen problems are two separate tasks.

First:

```text
DP calculates the best value
```

Then:

```text
reconstruction follows the optimal decisions
```

At state:

```text
(level, timeTaken, itemTaken)
```

recalculate:

```text
notTake
```

and:

```text
take
```

using the already memoized `rec()` values.

---

## 15.1 Reconstruction Transition

```text
notTake = rec(level + 1, timeTaken, itemTaken)
```

If take is valid:

```text
take = skill[level] + rec(level + 1, timeTaken + timeCost[level], itemTaken + 1)
```

If:

```text
take > notTake
```

then problem `level` belongs to the chosen solution.

Record it and continue through the take branch.

Otherwise:

```text
skip it
```

---

## 15.2 Tie Case

If:

```text
take == notTake
```

both branches are optimal.

Choose one deterministic rule.

For example:

```text
prefer skip
```

or:

```text
prefer take
```

The optimal value stays the same.

---

# 16. C++17 — Value + Reconstruction

```cpp
#include <bits/stdc++.h>
using namespace std;

const int NEG_INF = -1e9;

int n, X, K;
vector<int> timeCost, skill, chosen;
vector<vector<vector<int>>> dp;

int rec(int level, int timeTaken, int itemTaken) {
    // 1. Pruning
    if (timeTaken > X || itemTaken > K) return NEG_INF;

    // 2. Base Case
    if (level == n) return 0;

    // 3. Cache Check
    int &ans = dp[level][timeTaken][itemTaken];
    if (ans != -1) return ans;

    // 4. Compute / Transition
    int notTake = rec(level + 1, timeTaken, itemTaken);
    int take = NEG_INF;

    if (timeTaken + timeCost[level] <= X && itemTaken + 1 <= K) {
        take = skill[level] + rec(level + 1, timeTaken + timeCost[level], itemTaken + 1);
    }

    ans = max(notTake, take);

    // 5. Save and Return
    return ans;
}

void generate(int level, int timeTaken, int itemTaken) {
    // 1. Pruning
    if (timeTaken > X || itemTaken > K) return;

    // 2. Base Case
    if (level == n) return;

    // 3. Cache Check
    // rec() already stores the answers.

    // 4. Compute / Transition
    int notTake = rec(level + 1, timeTaken, itemTaken);
    int take = NEG_INF;

    if (timeTaken + timeCost[level] <= X && itemTaken + 1 <= K) {
        take = skill[level] + rec(level + 1, timeTaken + timeCost[level], itemTaken + 1);
    }

    if (take > notTake) {
        chosen.push_back(level);
        generate(level + 1, timeTaken + timeCost[level], itemTaken + 1);
    } else {
        generate(level + 1, timeTaken, itemTaken);
    }

    // 5. Save and Return
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> n >> X >> K;

    timeCost.resize(n);
    skill.resize(n);

    for (int i = 0; i < n; i++) {
        cin >> timeCost[i] >> skill[i];
    }

    dp.assign(n, vector<vector<int>>(X + 1, vector<int>(K + 1, -1)));

    int bestSkill = rec(0, 0, 0);
    generate(0, 0, 0);

    cout << "Maximum skill = " << bestSkill << '\n';

    cout << "Chosen problems: ";
    for (int index : chosen) {
        cout << index << ' ';
    }

    cout << '\n';

    return 0;
}
```

---

# 17. Why Memoization Works

Without memoization, many different histories can reach the same state.

Example:

```text
Path A
→ rec(4,5,2)

Path B
→ rec(4,5,2)
```

Even if the selected earlier problems are different, the future is identical because:

```text
remaining problems are the same
time already used is the same
number of selected items is the same
```

So:

```text
same configuration
→ same answer
```

This is exactly the lecture's first point.

---

# 18. The Magical DP Framework

The lecture summarizes DP problem solving as:

```text
1. Look for the form of the problem

2. Decide states & meaning

3. Decide transitions

4. Check time complexity

5. Code
```

Use this order every time.

---

## 18.1 Step 1 — Look for the Form

Ask:

```text
What type of decisions are being made?
```

Examples:

```text
take / not take
choose next
partition
move left / right
select subset
```

Here:

```text
Take / Not Take
```

---

## 18.2 Step 2 — Decide States & Meaning

Ask:

```text
What information from the past changes future choices?
```

Here:

```text
level
timeTaken
itemTaken
```

Then write the English meaning.

Never start from:

```text
dp[i][j][k]
```

without first knowing exactly what the state represents.

---

## 18.3 Step 3 — Decide Transitions

Write every choice separately.

```text
notTake = rec(level + 1, timeTaken, itemTaken)
```

```text
take = skill[level] + rec(level + 1, timeTaken + timeCost[level], itemTaken + 1)
```

Then combine:

```text
answer = max(notTake, take)
```

---

## 18.4 Step 4 — Check Time Complexity

Use:

```text
Time
=
#states
×
average work per state
```

Here:

```text
#states = O(NXK)

work/state = O(1)
```

So:

```text
O(NXK)
```

---

## 18.5 Step 5 — Code

Always use:

```text
Pruning
Base Case
Cache Check
Compute / Transition
Save and Return
```

This is now the fixed code structure for future notes.

---

# 19. State Design Lessons

## 19.1 Every state variable must affect the future

`level` answers:

```text
Which problems remain?
```

`timeTaken` answers:

```text
How much of X is already consumed?
```

`itemTaken` answers:

```text
How many of the K slots are already consumed?
```

Each parameter changes future validity or reward.

So all three belong in the state.

---

## 19.2 Do not store unnecessary history

We do not need:

```text
vector of selected problems
```

inside the state.

That would create many unnecessary configurations.

The future does not care how we reached:

```text
(level, timeTaken, itemTaken)
```

It only cares that we are there.

---

## 19.3 Same configuration test

Before memoizing, ask:

```text
If two calls have identical state parameters,
is the remaining problem exactly the same?
```

If yes:

```text
memoization is valid
```

---

# 20. Common Mistakes

## Mistake 1 — Missing one constraint

Take is valid only when:

```text
timeTaken + T[level] <= X
```

and:

```text
itemTaken + 1 <= K
```

Both must be checked.

---

## Mistake 2 — Wrong take transition

Wrong:

```text
take = S[level] + rec(level + 1, timeTaken + T[level], itemTaken)
```

Correct:

```text
take = S[level] + rec(level + 1, timeTaken + T[level], itemTaken + 1)
```

---

## Mistake 3 — Adding skill on skip

Wrong:

```text
notTake = S[level] + rec(...)
```

Skip gives:

```text
0 immediate skill
```

Correct:

```text
notTake = rec(level + 1, timeTaken, itemTaken)
```

---

## Mistake 4 — Storing the whole chosen subset

Not required.

The state already contains all future-relevant information.

---

## Mistake 5 — Forgetting the cache check

Without:

```cpp
if (ans != -1) return ans;
```

the solution remains exponential recursion.

---

## Mistake 6 — Wrong complexity calculation

After memoization, do not count all recursion-tree paths.

Count:

```text
unique states
×
work per state
```

---

# 21. Contest Checklist

Before coding, write:

```text
FORM:
Take / Not Take
```

Then:

```text
STATE:
rec(level, timeTaken, itemTaken)
```

Then its meaning:

```text
maximum additional skill
from [level ... N-1]
given current used time and count
```

Then transitions:

```text
notTake = rec(level + 1, timeTaken, itemTaken)
```

```text
take = skill[level] + rec(level + 1, timeTaken + timeCost[level], itemTaken + 1)
```

Then complexity:

```text
O(NXK)
```

Only then write code.

---

# 22. Final Revision Box

```text
MAGICAL DP FRAMEWORK
────────────────────────────────────

1. Look for the form

2. Decide states & meaning

3. Decide transitions

4. Check time complexity

5. Code


CORE DP OBSERVATION
────────────────────────────────────

Same configuration repeats
→ answer remains same
→ save the value


PROBLEM
────────────────────────────────────

N problems

T[i] = time needed
S[i] = skill gained

Limits:

total time <= X

selected problems <= K

Goal:

maximize total skill


FORM
────────────────────────────────────

Take / Not Take


STATE
────────────────────────────────────

rec(level, timeTaken, itemTaken)

=
maximum additional skill
from problems [level ... N-1]

given:

timeTaken already used

itemTaken already selected


INITIAL STATE
────────────────────────────────────

rec(0, 0, 0)


NOT TAKE
────────────────────────────────────

notTake = rec(level + 1, timeTaken, itemTaken)


TAKE
────────────────────────────────────

Allowed if:

timeTaken + T[level] <= X

itemTaken + 1 <= K

Transition:

take = S[level] + rec(level + 1, timeTaken + T[level], itemTaken + 1)


ANSWER
────────────────────────────────────

answer = max(notTake, take)


CODE STRUCTURE
────────────────────────────────────

1. Pruning

2. Base Case

3. Cache Check

4. Compute / Transition

5. Save and Return


TIME COMPLEXITY
────────────────────────────────────

States:

N × (X+1) × (K+1)

Transitions/state:

2

Time:

O(NXK)

Space:

O(NXK)


PRINTING
────────────────────────────────────

Calculate DP first.

Then compare:

take
vs
notTake

Follow the branch that creates
the optimal value.


MOST IMPORTANT STATE QUESTION
────────────────────────────────────

"What is the minimum information
needed so the future problem
is completely determined?"

Those values become
the DP state.
```
