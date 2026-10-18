# TLE Eliminators — Two Pointers Level 3: Problem Solving
## Prefix Sum + Monotonic Deque, Type-1 Windows, Contribution Maintenance, and Frequency Deficit
### TLE-Style Self-Study Notes

> **Source:** supplied TLE Eliminators PDF — *Problem Solving 1 | Two Pointers | Level 3*.
>
> This lecture contains four problems:
>
> ```text
> Problem 1 — Shortest Subarray with Sum at Least K
>             negatives allowed
>             → Prefix Sum + Monotonic Deque
>
> Problem 2 — Knapsack on a Segment
>             → Type 1 Variable Window
>
> Problem 3 — Not Very Rude Substring
>             → Type 1 + Contribution Maintenance
>
> Problem 4 — Card Substrings
>             → Type 1 + Frequency Deficit
> ```
>
> The most important lesson is:
>
> ```text
> Do not memorize "Two Pointers".
>
> First ask:
> Is the condition monotonic?
>
> If YES:
>     use a variable window.
>
> If NO:
>     transform the problem
>     until a monotonic structure appears.
> ```

---

# Table of Contents

- [1. Lecture Roadmap](#1-lecture-roadmap)
- [2. Prerequisite 1 — Type 1 Good Segment](#2-prerequisite-1--type-1-good-segment)
- [3. Prerequisite 2 — Maximum-Value Type 1 Template](#3-prerequisite-2--maximum-value-type-1-template)
- [4. Prerequisite 3 — Why Negative Values Can Break Sliding Window](#4-prerequisite-3--why-negative-values-can-break-sliding-window)
- [5. Prerequisite 4 — Prefix Sums](#5-prerequisite-4--prefix-sums)
- [6. Prerequisite 5 — Monotonic Deque Dominance](#6-prerequisite-5--monotonic-deque-dominance)
- [7. Problem 1 — Shortest Subarray with Sum at Least K](#7-problem-1--shortest-subarray-with-sum-at-least-k)
- [8. Why It Looks Like Type 2 — But Is Not](#8-why-it-looks-like-type-2--but-is-not)
- [9. Prefix-Sum Transformation](#9-prefix-sum-transformation)
- [10. O(N log N) Ordered-Set Idea](#10-on-log-n-ordered-set-idea)
- [11. Upgrade to O(N) Monotonic Deque](#11-upgrade-to-on-monotonic-deque)
- [12. Detailed Dry Run — Shortest Subarray](#12-detailed-dry-run--shortest-subarray)
- [13. C++17 — Shortest Subarray with Sum at Least K](#13-c17--shortest-subarray-with-sum-at-least-k)
- [14. Problem 2 — Knapsack on a Segment](#14-problem-2--knapsack-on-a-segment)
- [15. Why This Is Type 1](#15-why-this-is-type-1)
- [16. Detailed Dry Run — Knapsack on a Segment](#16-detailed-dry-run--knapsack-on-a-segment)
- [17. C++17 — Knapsack on a Segment](#17-c17--knapsack-on-a-segment)
- [18. Prerequisite 6 — Maintaining Pair Contributions Incrementally](#18-prerequisite-6--maintaining-pair-contributions-incrementally)
- [19. Problem 3 — Not Very Rude Substring](#19-problem-3--not-very-rude-substring)
- [20. What Is Rudeness?](#20-what-is-rudeness)
- [21. Add / Remove Formulas for Rudeness](#21-add--remove-formulas-for-rudeness)
- [22. Detailed Dry Run — Rudeness](#22-detailed-dry-run--rudeness)
- [23. C++17 — Not Very Rude Substring](#23-c17--not-very-rude-substring)
- [24. Prerequisite 7 — Frequency Capacity / Deficit Window](#24-prerequisite-7--frequency-capacity--deficit-window)
- [25. Problem 4 — Card Substrings](#25-problem-4--card-substrings)
- [26. Why This Is Type 1](#26-why-this-is-type-1)
- [27. Detailed Dry Run — Card Substrings](#27-detailed-dry-run--card-substrings)
- [28. C++17 — Card Substrings](#28-c17--card-substrings)
- [29. Why `answer += right-left+1`](#29-why-answer--right-left1)
- [30. Comparing All Four Problems](#30-comparing-all-four-problems)
- [31. Common Mistakes](#31-common-mistakes)
- [32. Recognition Table](#32-recognition-table)
- [33. Final Mental Model](#33-final-mental-model)

---

# 1. Lecture Roadmap

The lecture starts with:

```text
2 types of segments
+
4 problems
```

The four problems are not four unrelated tricks.

They teach four levels of modelling:

```text
Problem 1
Normal Two Pointers fails
→ transform to Prefix Sum
→ use Monotonic Deque

Problem 2
Direct Type 1 window
→ maintain two additive sums

Problem 3
Type 1 window
→ maintained state is NOT a simple sum
→ maintain pair contribution

Problem 4
Type 1 counting window
→ maintain remaining character capacities
→ count all valid starts
```

This is exactly how harder Two Pointer problems should be studied:

```text
identify monotonicity
→ identify window state
→ derive ADD
→ derive REMOVE
→ derive QUERY
```

---

# 2. Prerequisite 1 — Type 1 Good Segment

Type 1 means:

```text
If [L,R] is good,
every enclosed subsegment is also good.
```

ASCII:

```text
GOOD:

L [-----------------------] R

    [----------------]
        [--------]
          [----]

all enclosed segments remain GOOD
```

Typical examples:

```text
sum <= K        with non-negative values

weight <= S     with non-negative weights

rudeness <= C   because deleting characters cannot create new bad pairs

character usage <= available cards
```

This property makes shrinking safe.

---

# 3. Prerequisite 2 — Maximum-Value Type 1 Template

For many Type 1 problems:

```cpp
int left = 0;

for (int right = 0; right < n; right++) {
    add(arr[right]);

    while (!valid()) {
        remove(arr[left]);
        left++;
    }

    useCurrentWindow();
}
```

For maximum length:

```cpp
answer = max(answer, right - left + 1);
```

For maximum profit:

```cpp
answer = max(answer, currentProfit);
```

For counting all valid subarrays ending at `right`:

```cpp
answer += right - left + 1;
```

The structure is the same.

Only:

```text
ADD
REMOVE
VALID
QUERY
```

change.

---

# 4. Prerequisite 3 — Why Negative Values Can Break Sliding Window

Suppose the goal is:

```text
shortest subarray with sum >= K
```

If all values are non-negative:

```text
expanding → sum never decreases
shrinking → sum never increases
```

So standard Type 2 works.

But with negative values:

```text
adding can decrease sum
removing can increase sum
```

Example:

```text
arr = [5, -10, 20]
K = 15
```

Start:

```text
[5]          sum=5
[5,-10]      sum=-5
[5,-10,20]   sum=15
```

The effect of expansion is no longer monotonic.

This is the key reason Problem 1 is harder.

---

# 5. Prerequisite 4 — Prefix Sums

Define:

```text
prefix[0] = 0
```

and:

```text
prefix[i+1]
=
prefix[i] + nums[i]
```

Then subarray sum:

```text
nums[L ... R]
```

is:

```text
prefix[R+1] - prefix[L]
```

Equivalent prefix-index form:

```text
subarray represented by prefix indices (j,i)

sum
=
prefix[i] - prefix[j]

length
=
i - j
```

---

## Tiny Example

```text
nums = [2,-3,5,4]
```

Prefix:

```text
prefix[0] = 0
prefix[1] = 2
prefix[2] = -1
prefix[3] = 4
prefix[4] = 8
```

So:

```text
prefix = [0,2,-1,4,8]
```

Subarray:

```text
nums[1...3]
=
[-3,5,4]
```

Sum:

```text
prefix[4] - prefix[1]
=
8 - 2
=
6
```

---

# 6. Prerequisite 5 — Monotonic Deque Dominance

Suppose two earlier prefix indices are:

```text
j1 < j2
```

and:

```text
prefix[j1] >= prefix[j2]
```

For any future endpoint `i`:

```text
prefix[i] - prefix[j2]
>=
prefix[i] - prefix[j1]
```

and:

```text
i-j2 < i-j1
```

So `j2` is better in both ways:

```text
larger/equal achievable subarray sum
+
shorter length
```

Therefore:

```text
j1 is dominated
```

and can be removed.

This is why we maintain increasing prefix values in a deque.

---

# 7. Problem 1 — Shortest Subarray with Sum at Least K

### 🔗 Problem Link

[LeetCode — Shortest Subarray with Sum at Least K](https://leetcode.com/problems/shortest-subarray-with-sum-at-least-k/description/)

---

## What It Asks

Given an integer array:

```text
nums
```

which may contain negative values, find the length of the shortest non-empty subarray with:

```text
sum >= K
```

If none exists:

```text
return -1
```

The lecture explicitly notes:

```text
"It might seem like Type 2,
BUT it is not."
```

The reason is the negative values.

---

# 8. Why It Looks Like Type 2 — But Is Not

At first glance:

```text
need sum >= K
```

looks like:

```text
Type 2 lower-bound window
```

For positive numbers:

```cpp
for (int right = 0; right < n; right++) {
    sum += nums[right];

    while (sum >= k) {
        answer = min(answer, right - left + 1);
        sum -= nums[left];
        left++;
    }
}
```

But with negative values:

```text
nothing is monotonic
```

The lecture therefore moves from:

```text
L / R
```

to:

```text
PREFIX SUM
```

---

# 9. Prefix-Sum Transformation

We want:

```text
prefix[i] - prefix[j] >= K
```

Rearrange:

```text
prefix[j] <= prefix[i] - K
```

For every right prefix index `i`, find an earlier `j` satisfying that inequality.

Among all valid `j`, we want:

```text
minimum i-j
```

So for each current `i`:

```text
find the latest useful earlier prefix index
whose prefix value is small enough
```

This is no longer a normal window-sum problem.

It is a problem on:

```text
prefix value + prefix index
```

---

# 10. O(N log N) Ordered-Set Idea

The lecture first sketches an ordered-set solution.

Maintain previous prefix entries:

```text
(prefixValue, index)
```

ordered by prefix value.

For current `i`, any earlier prefix satisfying:

```text
prefix[j] <= prefix[i] - K
```

forms a valid subarray.

Important observation:

```text
Once index j becomes valid for the FIRST time,
using the same j with a later endpoint
can only produce a longer subarray.
```

So after using `j`, it can be permanently removed.

Conceptually:

```text
insert previous prefixes

for current i:
    while smallest prefix value <= prefix[i]-K:
        use its index
        update minimum length
        erase it permanently
```

Each prefix entry is inserted once and erased once.

With an ordered set:

```text
O(N log N)
```

The lecture then asks:

```text
Can queue/deque optimization reduce this to O(N)?
```

Yes.

---

# 11. Upgrade to O(N) Monotonic Deque

Maintain deque of prefix indices with increasing prefix values.

For every prefix index `i`:

### Rule 1 — Pop Front When a Valid Subarray Exists

While:

```text
prefix[i] - prefix[dq.front()] >= K
```

we have a valid subarray.

Update:

```text
answer = min(answer, i-dq.front())
```

Then:

```text
pop_front
```

Why safe?

For this start index:

```text
any later endpoint gives a longer subarray
```

so it can never improve the answer later.

---

### Rule 2 — Pop Back When Current Prefix Dominates

While:

```text
prefix[i] <= prefix[dq.back()]
```

remove the old back.

Why?

Current `i` is:

```text
later
+
has smaller/equal prefix value
```

So it is strictly better for all future endpoints.

---

### Rule 3 — Push Current Prefix Index

```cpp
dq.push_back(i);
```

---

# 12. Detailed Dry Run — Shortest Subarray

Teaching example:

```text
nums = [2, -1, 2]
K = 3
```

Prefix:

```text
[0,2,1,3]
```

Start:

```text
dq = []
answer = INF
```

Trace:

```text
i=0, prefix=0
no valid front
push 0
dq=[0]

i=1, prefix=2
2-0 < 3
2 <= 0 ? no
push 1
dq=[0,1]

i=2, prefix=1
1-0 < 3

back dominance:
1 <= 2
→ pop index1

1 <= 0 ? no
push 2
dq=[0,2]

i=3, prefix=3
3-0 >= 3
→ length=3
→ answer=3
→ pop front 0

3-1 < 3
push 3
```

Answer:

```text
3
```

---

# 13. C++17 — Shortest Subarray with Sum at Least K

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

int shortestSubarray(vector<int>& nums, ll k) {
    int n = nums.size();

    vector<ll> prefix(n + 1, 0);

    for (int i = 0; i < n; i++) {
        prefix[i + 1] = prefix[i] + nums[i];
    }

    deque<int> dq;
    int answer = n + 1;

    for (int i = 0; i <= n; i++) {
        while (!dq.empty() && prefix[i] - prefix[dq.front()] >= k) {
            answer = min(answer, i - dq.front());
            dq.pop_front();
        }

        while (!dq.empty() && prefix[i] <= prefix[dq.back()]) {
            dq.pop_back();
        }

        dq.push_back(i);
    }

    return (answer == n + 1 ? -1 : answer);
}
```

Complexity:

```text
Time  = O(N)
Space = O(N)
```

Each prefix index:

```text
enters deque once
leaves deque at most once
```

---

# 14. Problem 2 — Knapsack on a Segment

### 🔗 Problem Link

[Codeforces EDU — Knapsack on a Segment](https://codeforces.com/edu/course/2/lesson/9/3/practice/contest/307094/problem/E)

---

## What It Asks

The lecture models each item with:

```text
weight w[i]
profit/cost c[i]
```

with:

```text
w[i] >= 0
c[i] >= 0
```

Choose a contiguous segment such that:

```text
sum(weight) <= S
```

and maximize:

```text
sum(profit)
```

This is a direct Type 1 problem.

---

# 15. Why This Is Type 1

Good condition:

```text
windowWeight <= S
```

Because all weights are non-negative:

```text
removing items cannot increase total weight
```

So if a segment is valid:

```text
every enclosed segment is also valid
```

That gives Type 1 monotonicity.

---

## Why Keep the Leftmost Valid `L` for Every `R`?

For fixed `right`, after shrinking until valid, `[left,right]` is the largest valid suffix ending at `right`.

Because all profits are non-negative:

```text
removing extra items cannot increase total profit
```

So among valid windows ending at this `right`, the leftmost valid `left` gives maximum profit.

Therefore:

```text
answer = max(answer, windowProfit)
```

after restoring validity.

---

# 16. Detailed Dry Run — Knapsack on a Segment

Teaching example:

```text
weights = [2,4,3,5]
profits = [3,7,4,10]
S = 7
```

Start:

```text
left=0
weight=0
profit=0
answer=0
```

Trace:

```text
right=0:
add weight2, profit3
weight=2 <=7
answer=3

right=1:
add weight4, profit7
weight=6 <=7
profit=10
answer=10

right=2:
add weight3, profit4
weight=9 >7

remove left item:
weight=7
profit=11
left=1

answer=11

right=3:
add weight5, profit10
weight=12 >7

remove item at left=1:
weight=8
profit=14
left=2

still >7
remove item at left=2:
weight=5
profit=10
left=3

answer remains 11
```

Final:

```text
11
```

---

# 17. C++17 — Knapsack on a Segment

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll maxProfitOnSegment(
    const vector<ll>& weight,
    const vector<ll>& profit,
    ll capacity) {

    int n = weight.size();

    int left = 0;

    ll currentWeight = 0;
    ll currentProfit = 0;
    ll answer = 0;

    for (int right = 0; right < n; right++) {
        currentWeight += weight[right];
        currentProfit += profit[right];

        while (currentWeight > capacity) {
            currentWeight -= weight[left];
            currentProfit -= profit[left];
            left++;
        }

        answer = max(answer, currentProfit);
    }

    return answer;
}
```

Complexity:

```text
Time  = O(N)
Space = O(1)
```

---

# 18. Prerequisite 6 — Maintaining Pair Contributions Incrementally

Some window properties are not simple sums.

Example:

```text
number of ordered pairs
(a before b)
```

If we recompute all pairs inside every window:

```text
O(windowLength²)
```

That is too slow.

Instead ask:

```text
What contribution does the entering element create?

What contribution does the leaving element destroy?
```

This is exactly the idea used in Problem 3.

---

# 19. Problem 3 — Not Very Rude Substring

### 🔗 Problem Link

[Codeforces EDU — Not Very Rude Substring](https://codeforces.com/edu/course/2/lesson/9/3/practice/contest/307094/problem/G)

---

## What It Asks

Given a string containing:

```text
'a' and 'b'
```

the **rudeness** of a substring is the number of pairs:

```text
(i,j)
```

such that:

```text
i < j
s[i] = 'a'
s[j] = 'b'
```

Given limit:

```text
C
```

find the maximum-length substring with:

```text
rudeness <= C
```

This is Type 1:

```text
deleting characters cannot create new (a,b) pairs
```

---

# 20. What Is Rudeness?

Example:

```text
s = "aabb"
```

Positions:

```text
0:a
1:a
2:b
3:b
```

Pairs:

```text
(0,2)
(0,3)
(1,2)
(1,3)
```

So:

```text
rudeness = 4
```

Another example:

```text
s = "bbab"
```

Only the `a` before the last `b` contributes.

So:

```text
rudeness = 1
```

---

# 21. Add / Remove Formulas for Rudeness

Maintain:

```text
countA
countB
rudeness
```

---

## Add `'a'` at the Right

New `'a'` appears after every existing character.

It cannot become the left member of an `(a,b)` pair with an existing `b`, because those `b`s are before it.

So:

```text
countA++
rudeness unchanged
```

---

## Add `'b'` at the Right

Every existing `'a'` is before this new `'b'`.

So the new `'b'` creates:

```text
countA
```

new rude pairs.

Update:

```text
rudeness += countA
countB++
```

---

## Remove `'b'` from the Left

If the leftmost character is `'b'`:

```text
there is no 'a' before it inside the current window
```

so it contributes no rude pair.

Update:

```text
countB--
rudeness unchanged
```

---

## Remove `'a'` from the Left

The leftmost `'a'` forms a rude pair with every `'b'` remaining in the window.

So removing it destroys:

```text
countB
```

pairs.

Update:

```text
countA--
rudeness -= countB
```

This is the crucial contribution formula.

---

# 22. Detailed Dry Run — Rudeness

Use:

```text
s = "aabb"
C = 2
```

Start:

```text
left=0
countA=0
countB=0
rudeness=0
answer=0
```

Trace:

```text
right=0, 'a':
countA=1
rudeness=0
window="a"
answer=1

right=1, 'a':
countA=2
rudeness=0
window="aa"
answer=2

right=2, 'b':
new b creates countA=2 pairs
rudeness=2
countB=1
window="aab"
answer=3

right=3, 'b':
new b creates 2 pairs
rudeness=4
countB=2
INVALID because 4>2
```

Shrink:

```text
left char = 'a'

countA: 2→1
remove its pairs with both current b's:
rudeness: 4→2

left=1
```

Now valid:

```text
window="abb"
rudeness=2
length=3
```

Final maximum:

```text
3
```

---

# 23. C++17 — Not Very Rude Substring

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

int longestNotVeryRude(const string& s, ll limit) {
    int n = s.size();

    int left = 0;
    int countA = 0;
    int countB = 0;
    int answer = 0;

    ll rudeness = 0;

    for (int right = 0; right < n; right++) {
        if (s[right] == 'a') {
            countA++;
        } else {
            rudeness += countA;
            countB++;
        }

        while (rudeness > limit) {
            if (s[left] == 'a') {
                countA--;
                rudeness -= countB;
            } else {
                countB--;
            }

            left++;
        }

        answer = max(answer, right - left + 1);
    }

    return answer;
}
```

Complexity:

```text
Time  = O(N)
Space = O(1)
```

---

# 24. Prerequisite 7 — Frequency Capacity / Deficit Window

Suppose we have available character cards.

Example:

```text
T = "abb"
```

Available:

```text
a → 1
b → 2
```

A substring is constructible if it does not consume more than available.

Instead of repeatedly comparing two frequency arrays, maintain:

```text
remaining[ch]
```

Initialize from `T`.

When a character enters:

```text
remaining[ch]--
```

If any:

```text
remaining[ch] < 0
```

then we used too many copies of that character.

Window is invalid.

When a character leaves:

```text
remaining[ch]++
```

This is a very reusable "capacity" window pattern.

---

# 25. Problem 4 — Card Substrings

### 🔗 Problem Link

[Codeforces EDU — Card Substrings](https://codeforces.com/edu/course/2/lesson/9/3/practice/contest/307094/problem/F)

---

## What It Asks

Given strings:

```text
S
T
```

count the number of substrings of `S` that can be formed using the characters/cards available in `T`.

For every character:

```text
frequencyInSubstring[ch]
<=
frequencyInT[ch]
```

The lecture classifies this as:

```text
Type 1
```

because if a substring fits inside the available card multiset, every enclosed substring also fits.

---

# 26. Why This Is Type 1

Suppose current substring is valid:

```text
used[ch] <= available[ch]
```

for every character.

Removing characters can only decrease usage.

Therefore every enclosed substring is also valid.

So after finding the smallest valid `left` for each `right`:

```text
every start from left...right is valid
```

This gives the counting formula:

```text
right-left+1
```

---

# 27. Detailed Dry Run — Card Substrings

Teaching example:

```text
S = "abbc"
T = "abb"
```

Available:

```text
a:1
b:2
c:0
```

Start:

```text
left=0
answer=0
```

---

## right = 0 → `'a'`

Consume:

```text
a: 1→0
```

Valid.

Valid substrings ending here:

```text
"a"
```

Contribution:

```text
1
```

Total:

```text
1
```

---

## right = 1 → `'b'`

Consume:

```text
b: 2→1
```

Valid.

Substrings ending here:

```text
"b"
"ab"
```

Contribution:

```text
2
```

Total:

```text
3
```

---

## right = 2 → `'b'`

Consume:

```text
b: 1→0
```

Valid.

Substrings:

```text
"b"
"bb"
"abb"
```

Contribution:

```text
3
```

Total:

```text
6
```

---

## right = 3 → `'c'`

Consume:

```text
c: 0→-1
```

Invalid.

Shrink:

```text
remove S[left]='a'
a:0→1
left=1
```

Still:

```text
c=-1
```

invalid.

Remove:

```text
'b'
b:0→1
left=2
```

Still invalid.

Remove:

```text
'b'
b:1→2
left=3
```

Still invalid.

Remove:

```text
'c'
c:-1→0
left=4
```

Now window is empty and valid.

Contribution:

```text
0
```

Final:

```text
6
```

---

# 28. C++17 — Card Substrings

The lecture shows a 26-frequency approach and checks whether some frequency goes negative.

A cleaner implementation maintains a count of currently overused character types.

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll countCardSubstrings(const string& s, const string& t) {
    vector<int> remaining(26, 0);

    for (char ch : t) {
        remaining[ch - 'a']++;
    }

    int left = 0;
    int overused = 0;

    ll answer = 0;

    for (int right = 0; right < int(s.size()); right++) {
        int index = s[right] - 'a';

        remaining[index]--;

        if (remaining[index] == -1) {
            overused++;
        }

        while (overused > 0) {
            int leftIndex = s[left] - 'a';

            if (remaining[leftIndex] == -1) {
                overused--;
            }

            remaining[leftIndex]++;
            left++;
        }

        answer += right - left + 1;
    }

    return answer;
}
```

Complexity:

```text
Time  = O(N + |T|)
Space = O(26)
```

The lecture's direct:

```text
check all 26 frequencies
```

version is also effectively linear for the fixed lowercase alphabet:

```text
O(26N)
```

but `overused` avoids rescanning all 26 entries.

---

# 29. Why `answer += right-left+1`

This formula appears in Type 1 counting problems.

After shrinking:

```text
[left,right]
```

is valid.

Because Type 1 says every enclosed segment is valid, all these substrings ending at `right` are valid:

```text
[right,right]
[right-1,right]
[right-2,right]
...
[left,right]
```

Number of choices for the start:

```text
right-left+1
```

So:

```cpp
answer += right - left + 1;
```

Each substring is counted exactly once:

```text
by its ending index
```

---

# 30. Comparing All Four Problems

| Problem | Why Normal Window? | Maintained State | Key Trick |
|---|---|---|---|
| Shortest Subarray Sum ≥ K | **Does not work directly** because negatives | Prefix sums + deque | Transform problem first |
| Knapsack on a Segment | Weight condition is Type 1 | weight sum + profit sum | Leftmost valid L maximizes non-negative profit |
| Not Very Rude Substring | Rudeness cannot increase by deleting chars | `countA`, `countB`, `rudeness` | Incremental pair contribution |
| Card Substrings | Character usage only decreases when shrinking | remaining frequency / deficit | Count all valid starts |

---

# 31. Common Mistakes

## Mistake 1 — Treat Problem 1 as Normal Type 2

Negative values destroy sum monotonicity.

Use:

```text
Prefix Sum + Monotonic Deque
```

---

## Mistake 2 — Forget Prefix Index Length

If:

```text
prefix[i] - prefix[j]
```

represents a subarray, its length is:

```text
i-j
```

not:

```text
i-j+1
```

---

## Mistake 3 — Forget Why Deque Back Candidates Are Useless

If:

```text
j1 < j2
prefix[j1] >= prefix[j2]
```

then `j2` is:

```text
later
+
smaller/equal prefix
```

so `j1` is dominated.

---

## Mistake 4 — In Knapsack, Update Profit Without Updating Weight

Both window states must describe exactly the same segment:

```text
currentWeight
currentProfit
```

ADD and REMOVE both.

---

## Mistake 5 — Recompute Rudeness from Scratch

Do not scan all `(a,b)` pairs after every move.

Use:

```text
add b:
rudeness += countA

remove a:
rudeness -= countB
```

---

## Mistake 6 — Wrong Order When Removing `'a'`

The removed leftmost `'a'` is paired with every `'b'` currently remaining.

Subtract:

```text
countB
```

before moving `left` beyond the character.

---

## Mistake 7 — Count Only the Largest Valid Card Window

Problem 4 asks:

```text
number of valid substrings
```

For each `right`, count:

```text
right-left+1
```

not just one.

---

## Mistake 8 — Use `int` for Number of Substrings

Number of substrings can reach:

```text
N(N+1)/2
```

Use:

```cpp
long long
```

---

# 32. Recognition Table

| Statement Signal | Pattern |
|---|---|
| Sum ≥ K with arbitrary negative values | Prefix Sum + Monotonic Deque |
| Contiguous segment, non-negative weight ≤ capacity, maximize non-negative profit | Type 1 |
| Window property counts ordered pairs | Maintain contribution incrementally |
| Add `'b'` creates pairs with every existing `'a'` | `rudeness += countA` |
| Remove left `'a'` destroys pairs with every current `'b'` | `rudeness -= countB` |
| Substring character counts must fit a fixed multiset | Frequency-capacity window |
| Count all valid Type 1 substrings ending at R | `R-L+1` |

---

# 33. Final Mental Model

The four problems form a useful progression:

```text
               TWO POINTERS LEVEL 3
                       |
       +---------------+----------------+
       |               |                |
       v               v                v
MONOTONICITY?      WINDOW STATE      COUNTING
       |
   +---+---+
   |       |
  YES      NO
   |       |
   v       v
Two      Transform
Pointers   problem
         (Prefix Sum)
             |
             v
       Monotonic Deque
```

---

## Problem 1

```text
negative values
→ no normal sum monotonicity

subarray
→ prefix difference

shortest
→ keep best candidate starts

dominated starts
→ monotonic deque
```

---

## Problem 2

```text
weight <= S
+
weights/profits >= 0

→ Type 1

ADD weight/profit
SHRINK while overweight
UPDATE maximum profit
```

---

## Problem 3

```text
rudeness = count of (a before b) pairs

ADD b
→ +countA

REMOVE a
→ -countB

SHRINK while rudeness > C
UPDATE max length
```

---

## Problem 4

```text
substring character usage
<=
available cards

consume right char

if some resource overused:
    release left chars

then:
all starts L...R are valid

answer += R-L+1
```

---

## 60-Second Recognition

```text
Can I prove window monotonicity?

YES
→ Two Pointers

NO
→ transform first
```

Then ask:

```text
What changes when RIGHT enters?

What changes when LEFT leaves?

Can I update that information in O(1)?
```

That is the main Level-3 Two Pointers skill.
