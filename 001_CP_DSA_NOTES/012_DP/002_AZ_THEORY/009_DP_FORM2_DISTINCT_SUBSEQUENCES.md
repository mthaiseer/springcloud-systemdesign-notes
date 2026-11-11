# 020 — DP Form 2 Practice: Distinct Subsequences
## Ending Form + Duplicate Removal with `last[]` and Prefix DP

# Table of Contents

- [1. Prerequisites](#1-prerequisites)
- [2. Problem Statement](#2-problem-statement)
- [3. Why This Problem Is Tricky](#3-why-this-problem-is-tricky)
- [4. Small Examples First](#4-small-examples-first)
- [5. Step 1 — Decide the Form](#5-step-1--decide-the-form)
- [6. Step 2 — State Meaning](#6-step-2--state-meaning)
- [7. What Does `dp[i]` Mean?](#7-what-does-dpi-mean)
- [8. What Does `prefix[i]` Mean?](#8-what-does-prefixi-mean)
- [9. First Case — Character Never Seen Before](#9-first-case--character-never-seen-before)
- [10. Duplicate Problem — Character Seen Before](#10-duplicate-problem--character-seen-before)
- [11. The `last[]` Idea](#11-the-last-idea)
- [12. Step 3 — Transition Design](#12-step-3--transition-design)
- [13. Full Recurrence](#13-full-recurrence)
- [14. Dry Run — `"abac"`](#14-dry-run--abac)
- [15. Why `"aaa"` Gives Only 3 Non-Empty Subsequences](#15-why-aaa-gives-only-3-non-empty-subsequences)
- [16. SPOJ vs LeetCode Answer](#16-spoj-vs-leetcode-answer)
- [17. Time and Space Complexity](#17-time-and-space-complexity)
- [18. C++17 — Lecture-Style Bottom-Up DP](#18-c17--lecture-style-bottom-up-dp)
- [19. C++17 — LeetCode 940 Version](#19-c17--leetcode-940-version)
- [20. C++17 — SPOJ DSUBSEQ Version](#20-c17--spoj-dsubseq-version)
- [21. Mapping to Your DP Framework](#21-mapping-to-your-dp-framework)
- [22. Common Mistakes](#22-common-mistakes)
- [23. Recognition Pattern](#23-recognition-pattern)
- [24. Final Revision Box](#24-final-revision-box)

---

# 1. Prerequisites

Before this note, know:

```text
Subsequence
Distinct values
Dynamic Programming
Form 2 / Ending Form
Prefix sum idea
Array indexing
Last occurrence tracking
Modulo arithmetic
```

Also remember:

```text
subsequence != substring
```

A subsequence may skip characters, but order must remain the same.

Example:

```text
string = "abc"

subsequences include:

""
"a"
"b"
"c"
"ab"
"ac"
"bc"
"abc"
```

---

# 2. Problem Statement

Given a string:

```text
s
```

count the number of:

```text
distinct subsequences
```

The screenshots reference both:

```text
SPOJ — DSUBSEQ
```

and:

```text
LeetCode 940 — Distinct Subsequences II
```

Important difference:

```text
SPOJ DSUBSEQ
→ includes the empty subsequence

LeetCode 940
→ counts only non-empty subsequences
```

We will build one DP idea that supports both.

---

# 3. Why This Problem Is Tricky

For a string of length `N`, every character has two choices:

```text
take
don't take
```

So there are:

```text
2^N
```

index-based subsequences.

But we need:

```text
DISTINCT strings
```

Different index choices can generate the same subsequence.

Example:

```text
s = "aaa"
```

Index choices generate many subsequences.

But distinct non-empty subsequences are only:

```text
"a"
"aa"
"aaa"
```

So:

```text
answer = 3
```

The main problem is therefore:

```text
How do we count new subsequences
without counting duplicates again?
```

---

# 4. Small Examples First

## Example 1 — `"abc"`

Every character is different.

Distinct subsequences including empty:

```text
""
"a"
"b"
"c"
"ab"
"ac"
"bc"
"abc"
```

Count:

```text
8
```

Non-empty count:

```text
7
```

Because no character repeats, every extension creates something new.

---

## Example 2 — `"aaa"`

Distinct subsequences:

```text
""
"a"
"aa"
"aaa"
```

Including empty:

```text
4
```

Non-empty:

```text
3
```

ASCII:

```text
s = a a a

after first 'a':

""
"a"

after second 'a':

""
"a"
"aa"

after third 'a':

""
"a"
"aa"
"aaa"
```

Many naive combinations create duplicates, so we need to remove them.

---

## Example 3 — `"abac"`

Distinct non-empty subsequences are:

```text
a
b
c
ab
aa
ac
ba
bc
abc
aba
aac
bac
abac
```

Count:

```text
13
```

Including empty:

```text
14
```

This is the example reflected in the lecture screenshots.

---

# 5. Step 1 — Decide the Form

This lecture treats the problem using:

```text
Form 2 — Ending Form
```

The useful question is:

```text
How many NEW distinct subsequences
end at the current character?
```

ASCII:

```text
s = a b a c
        ↑
        i

Focus:

all distinct subsequences
whose LAST character is s[i]
```

This is similar in spirit to LIS Form 2:

```text
dp(i)
=
best / count of solutions
ending at i
```

Here we count newly created distinct subsequences ending at position `i`.

---

# 6. Step 2 — State Meaning

The lecture implementation uses two arrays:

```text
dp[i]
prefix[i]
```

with `i` representing the first `i` characters of the string.

We use:

```text
s[i - 1]
```

as the current character.

---

# 7. What Does `dp[i]` Mean?

Define:

```text
dp[i]
=
number of NEW distinct subsequences
created when processing s[i - 1]
```

Equivalently:

```text
number of distinct subsequences
whose newest ending is formed
using the current occurrence
```

For:

```text
s = "abac"
```

when processing the final:

```text
'c'
```

`dp[4]` counts the new subsequences created by appending this `c`.

---

# 8. What Does `prefix[i]` Mean?

Define:

```text
prefix[i]
=
total number of distinct subsequences
using the first i characters
```

including:

```text
empty subsequence
```

Therefore:

```text
prefix[0] = 1
```

because before reading any character, we have exactly:

```text
""
```

the empty subsequence.

And:

```text
prefix[i]
=
prefix[i - 1] + dp[i]
```

ASCII:

```text
prefix[i - 1]
     +
new subsequences ending at current character
     ↓
   dp[i]
     =
prefix[i]
```

---

# 9. First Case — Character Never Seen Before

Suppose current character has never appeared earlier.

Example:

```text
prefix before 'c':

""
"a"
"b"
"ab"
...
```

If current character is:

```text
c
```

then every old subsequence can append `c`.

ASCII:

```text
old subsequence      append 'c'

""              ->   "c"
"a"             ->   "ac"
"b"             ->   "bc"
"ab"            ->   "abc"
...
```

All of these are new because this character has never been used as an ending before.

So:

```text
dp[i] = prefix[i - 1]
```

Then:

```text
prefix[i]
=
prefix[i - 1] + dp[i]
```

Therefore, when there is no duplicate character:

```text
prefix[i]
=
2 × prefix[i - 1]
```

---

# 10. Duplicate Problem — Character Seen Before

Now suppose current character already appeared.

Example:

```text
s = "aba"
```

Current character:

```text
'a'
```

has appeared before at index:

```text
0
```

If we blindly do:

```text
dp[i] = prefix[i - 1]
```

we create some subsequences that were already generated using the previous `'a'`.

That causes duplicate counting.

---

## Visual Idea

Before current `'a'`:

```text
prefix:
""
"a"
"b"
"ab"
```

Append current `'a'`:

```text
""    + a  -> "a"    duplicate
"a"   + a  -> "aa"   new
"b"   + a  -> "ba"   new
"ab"  + a  -> "aba"  new
```

The subsequence:

```text
"a"
```

already existed because of the earlier `'a'`.

So we must subtract exactly the group that the previous occurrence had already generated.

---

# 11. The `last[]` Idea

Maintain:

```text
last[ch]
=
last index where character ch appeared
```

Initially:

```text
last[ch] = -1
```

for every character.

Example:

```text
s = a b a c
    0 1 2 3
```

While processing index `2`:

```text
current = 'a'

last['a'] = 0
```

So we know exactly where the previous `'a'` occurred.

---

# 12. Step 3 — Transition Design

Start by assuming all old subsequences can append the current character:

```text
dp[i] = prefix[i - 1]
```

Now check whether the character appeared before.

Let:

```text
previousIndex = last[s[i - 1]]
```

If:

```text
previousIndex != -1
```

then duplicates equal:

```text
prefix[previousIndex]
```

So:

```text
dp[i]
=
prefix[i - 1]
-
prefix[previousIndex]
```

Then update:

```text
last[s[i - 1]] = i - 1
```

Finally:

```text
prefix[i]
=
prefix[i - 1] + dp[i]
```

---

# 13. Full Recurrence

For current prefix length:

```text
i = 1 ... N
```

current character:

```text
s[i - 1]
```

Start:

```text
dp[i] = prefix[i - 1]
```

If current character was seen before:

```text
idx = last[s[i - 1]]
```

then:

```text
dp[i] -= prefix[idx]
```

Update last occurrence:

```text
last[s[i - 1]] = i - 1
```

And:

```text
prefix[i] = prefix[i - 1] + dp[i]
```

With modulo:

```text
dp[i]
=
(prefix[i - 1] - duplicate + MOD) % MOD
```

and:

```text
prefix[i]
=
(prefix[i - 1] + dp[i]) % MOD
```

---

# 14. Dry Run — `"abac"`

Use:

```text
s = "abac"
```

Initial:

```text
prefix[0] = 1
```

because:

```text
only ""
```

Also:

```text
last[a..z] = -1
```

---

## Step 1 — Process `'a'`

```text
i = 1
current = 'a'
```

Never seen before.

So:

```text
dp[1] = prefix[0]
      = 1
```

Update:

```text
prefix[1]
=
prefix[0] + dp[1]
=
1 + 1
=
2
```

Subsequences:

```text
""
"a"
```

Update:

```text
last['a'] = 0
```

---

## Step 2 — Process `'b'`

```text
i = 2
current = 'b'
```

Never seen before.

```text
dp[2] = prefix[1]
      = 2
```

New subsequences:

```text
"b"
"ab"
```

So:

```text
prefix[2]
=
2 + 2
=
4
```

Now:

```text
""
"a"
"b"
"ab"
```

Update:

```text
last['b'] = 1
```

---

## Step 3 — Process second `'a'`

```text
i = 3
current = 'a'
```

Previous occurrence:

```text
last['a'] = 0
```

Without duplicate removal:

```text
dp[3]
=
prefix[2]
=
4
```

Potential extensions:

```text
""   -> "a"
"a"  -> "aa"
"b"  -> "ba"
"ab" -> "aba"
```

But:

```text
"a"
```

was already counted from the first `'a'`.

Duplicate count:

```text
prefix[0] = 1
```

Therefore:

```text
dp[3]
=
prefix[2] - prefix[0]
=
4 - 1
=
3
```

New subsequences:

```text
"aa"
"ba"
"aba"
```

Then:

```text
prefix[3]
=
prefix[2] + dp[3]
=
4 + 3
=
7
```

Update:

```text
last['a'] = 2
```

---

## Step 4 — Process `'c'`

```text
i = 4
current = 'c'
```

`'c'` has never appeared before.

So:

```text
dp[4]
=
prefix[3]
=
7
```

Then:

```text
prefix[4]
=
7 + 7
=
14
```

Therefore:

```text
distinct subsequences including empty = 14
```

So:

```text
non-empty distinct subsequences
=
14 - 1
=
13
```

Exactly the lecture example.

---

## Dry-Run Table

| `i` | char | previous index | `dp[i]` | `prefix[i]` |
|---:|:---:|---:|---:|---:|
| 0 | — | — | — | 1 |
| 1 | a | -1 | 1 | 2 |
| 2 | b | -1 | 2 | 4 |
| 3 | a | 0 | `4 - 1 = 3` | 7 |
| 4 | c | -1 | 7 | 14 |

Final:

```text
SPOJ-style including empty = 14

LeetCode-style non-empty = 13
```

---

# 15. Why `"aaa"` Gives Only 3 Non-Empty Subsequences

Use:

```text
s = "aaa"
```

Initial:

```text
prefix[0] = 1
```

---

## First `'a'`

```text
dp[1] = 1
prefix[1] = 2
```

Subsequences:

```text
""
"a"
```

---

## Second `'a'`

Previous `'a'`:

```text
index 0
```

So:

```text
dp[2]
=
prefix[1] - prefix[0]
=
2 - 1
=
1
```

Only new subsequence:

```text
"aa"
```

Thus:

```text
prefix[2] = 3
```

---

## Third `'a'`

Previous `'a'` is now index:

```text
1
```

So:

```text
dp[3]
=
prefix[2] - prefix[1]
=
3 - 2
=
1
```

Only new subsequence:

```text
"aaa"
```

Thus:

```text
prefix[3] = 4
```

Including empty:

```text
4
```

Non-empty:

```text
3
```

ASCII:

```text
"aaa"

distinct:

""
 |
 +-- "a"
      |
      +-- "aa"
           |
           +-- "aaa"

Only one genuinely new length
appears with each repeated 'a'.
```

---

# 16. SPOJ vs LeetCode Answer

The DP naturally keeps:

```text
empty subsequence
```

inside:

```text
prefix[0] = 1
```

Therefore:

## SPOJ DSUBSEQ

The screenshot states that the empty subsequence is included.

Return:

```text
prefix[n]
```

---

## LeetCode 940

LeetCode asks for:

```text
non-empty distinct subsequences
```

Return:

```text
prefix[n] - 1
```

with modulo safety:

```cpp
(prefix[n] - 1 + MOD) % MOD
```

---

# 17. Time and Space Complexity

Each character is processed once.

For every character we do:

```text
O(1)
```

work:

```text
lookup last occurrence
calculate duplicate removal
update dp
update prefix
update last
```

Therefore:

```text
Time = O(N)
```

Arrays:

```text
dp      = O(N)
prefix  = O(N)
last    = O(Alphabet)
```

For lowercase English letters:

```text
last = 26
```

Total:

```text
Space = O(N)
```

The lecture implementation keeps the full arrays because it uses earlier prefix values for duplicate removal.

---

# 18. C++17 — Lecture-Style Bottom-Up DP

```cpp
#include <bits/stdc++.h>
using namespace std;

const long long MOD = 1000000007LL;

long long countDistinctIncludingEmpty(const string &s) {
    int n = (int)s.size();

    vector<long long> dp(n + 1, 0);
    vector<long long> prefix(n + 1, 0);
    vector<int> last(26, -1);

    prefix[0] = 1;

    for (int i = 1; i <= n; i++) {
        int currentChar = s[i - 1] - 'a';

        dp[i] = prefix[i - 1];

        if (last[currentChar] != -1) {
            int previousIndex = last[currentChar];
            dp[i] = (dp[i] - prefix[previousIndex] + MOD) % MOD;
        }

        last[currentChar] = i - 1;
        prefix[i] = (prefix[i - 1] + dp[i]) % MOD;
    }

    return prefix[n];
}
```

---

# 19. C++17 — LeetCode 940 Version

LeetCode 940 asks for:

```text
distinct NON-EMPTY subsequences
```

```cpp
class Solution {
public:
    int distinctSubseqII(string s) {
        const long long MOD = 1000000007LL;

        int n = (int)s.size();

        vector<long long> dp(n + 1, 0);
        vector<long long> prefix(n + 1, 0);
        vector<int> last(26, -1);

        prefix[0] = 1;

        for (int i = 1; i <= n; i++) {
            int currentChar = s[i - 1] - 'a';

            dp[i] = prefix[i - 1];

            if (last[currentChar] != -1) {
                int previousIndex = last[currentChar];
                dp[i] = (dp[i] - prefix[previousIndex] + MOD) % MOD;
            }

            last[currentChar] = i - 1;
            prefix[i] = (prefix[i - 1] + dp[i]) % MOD;
        }

        return (int)((prefix[n] - 1 + MOD) % MOD);
    }
};
```

For:

```text
s = "abc"
```

result:

```text
7
```

For:

```text
s = "aba"
```

result:

```text
6
```

For:

```text
s = "aaa"
```

result:

```text
3
```

---

# 20. C++17 — SPOJ DSUBSEQ Version

SPOJ DSUBSEQ includes the empty subsequence.

```cpp
#include <bits/stdc++.h>
using namespace std;

const long long MOD = 1000000007LL;

void solve() {
    string s;
    cin >> s;

    int n = (int)s.size();

    vector<long long> dp(n + 1, 0);
    vector<long long> prefix(n + 1, 0);
    vector<int> last(26, -1);

    prefix[0] = 1;

    for (int i = 1; i <= n; i++) {
        int currentChar = s[i - 1] - 'a';

        dp[i] = prefix[i - 1];

        if (last[currentChar] != -1) {
            int previousIndex = last[currentChar];
            dp[i] = (dp[i] - prefix[previousIndex] + MOD) % MOD;
        }

        last[currentChar] = i - 1;
        prefix[i] = (prefix[i - 1] + dp[i]) % MOD;
    }

    cout << prefix[n] << '\n';
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;

    while (t--) {
        solve();
    }

    return 0;
}
```

---

# 21. Mapping to Your DP Framework

This particular lecture implementation is:

```text
bottom-up DP
```

rather than recursive memoization.

So your usual recursive structure:

```text
Pruning
Base Case
Cache Check
Compute / Transition
Save and Return
```

maps conceptually like this:

```text
Pruning
→ not required for this iterative formulation

Base Case
→ prefix[0] = 1

Cache Check
→ not required because states are filled once in order

Compute / Transition
→ dp[i] = prefix[i-1]
→ subtract duplicate contribution if character repeated

Save
→ prefix[i] = prefix[i-1] + dp[i]
→ update last[currentChar]
```

The important point is to preserve the lecture's actual bottom-up solution rather than forcing an unnecessary recursive version.

---

# 22. Common Mistakes

## Mistake 1 — Counting index choices instead of distinct strings

Naive:

```text
2^N
```

counts all choose/skip combinations.

It does not remove equal subsequences generated from repeated characters.

---

## Mistake 2 — Forgetting the empty subsequence

The base is:

```text
prefix[0] = 1
```

because empty subsequence is needed to generate one-character subsequences.

Example:

```text
"" + 'a' = "a"
```

---

## Mistake 3 — Subtracting the wrong prefix

If previous occurrence is:

```text
previousIndex
```

the duplicate group is:

```text
prefix[previousIndex]
```

with this exact indexing convention.

Be consistent about:

```text
0-based string indices

vs

1-based prefix length
```

---

## Mistake 4 — Updating `last[]` too early

Correct order:

```text
1. calculate dp[i]
2. subtract using OLD last occurrence
3. update last[currentChar]
4. update prefix[i]
```

If `last[]` is overwritten too early, the duplicate calculation becomes wrong.

---

## Mistake 5 — Negative modulo

After subtraction:

```cpp
dp[i] = (dp[i] - prefix[previousIndex] + MOD) % MOD;
```

The:

```text
+ MOD
```

prevents a negative result.

---

## Mistake 6 — Returning the same final value for SPOJ and LeetCode

Remember:

```text
SPOJ
→ prefix[n]

LeetCode
→ prefix[n] - 1
```

because LeetCode excludes empty subsequence.

---

# 23. Recognition Pattern

When you see:

```text
Count DISTINCT subsequences
```

with repeated characters, think:

```text
1. Count what current character can newly generate.

2. Every previous subsequence can append current char.

3. If current char appeared before,
   some of those generated strings are duplicates.

4. Find the previous occurrence.

5. Subtract the exact duplicate contribution.

6. Update the last occurrence.
```

Generic mental model:

```text
NEW
=
ALL POSSIBLE EXTENSIONS
-
ALREADY CREATED EXTENSIONS
```

For this problem:

```text
dp[i]
=
prefix[i-1]
-
prefix[last[currentChar]]
```

when the character was seen before.

---

# 24. Final Revision Box

```text
DISTINCT SUBSEQUENCES — FORM 2
────────────────────────────────────

Goal:

Count distinct subsequences
of a string.


MAIN DIFFICULTY
────────────────────────────────────

Repeated characters create
duplicate subsequences.


STATE IDEA
────────────────────────────────────

dp[i]

=

number of NEW distinct subsequences
created when processing s[i-1]


PREFIX
────────────────────────────────────

prefix[i]

=

total distinct subsequences
using first i characters

including empty


BASE
────────────────────────────────────

prefix[0] = 1

because:

""


CHARACTER NEVER SEEN
────────────────────────────────────

dp[i]
=
prefix[i-1]

Every old subsequence can
append current character.


CHARACTER SEEN BEFORE
────────────────────────────────────

previousIndex
=
last[currentChar]

dp[i]
=
prefix[i-1]
-
prefix[previousIndex]


WHY SUBTRACT?
────────────────────────────────────

Those subsequences were already
generated using the previous
occurrence of the same character.


UPDATE
────────────────────────────────────

last[currentChar] = i-1

prefix[i]
=
prefix[i-1] + dp[i]


EXAMPLE
────────────────────────────────────

s = "abac"

prefix values:

1 → 2 → 4 → 7 → 14

Including empty:

14

Non-empty:

13


"aaa"
────────────────────────────────────

Distinct:

""
"a"
"aa"
"aaa"

Including empty:

4

Non-empty:

3


COMPLEXITY
────────────────────────────────────

Time:

O(N)

Space:

O(N)


SPOJ DSUBSEQ
────────────────────────────────────

Return:

prefix[n]

Includes empty.


LEETCODE 940
────────────────────────────────────

Return:

prefix[n] - 1

Excludes empty.


CORE IDEA
────────────────────────────────────

NEW DISTINCT

=

ALL EXTENSIONS

-

DUPLICATE EXTENSIONS
```
