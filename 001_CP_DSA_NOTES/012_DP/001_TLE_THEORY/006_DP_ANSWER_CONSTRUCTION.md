# 007 — Answer Construction & Non-Integer DP States
## DP Intermediate — Continuation of `006_STATE_ELIMINATION_CYCLIC_DP_STATES`

> **Self-study goal:** Learn how to go beyond finding only the optimal DP value.
>
> This lesson focuses on:
>
> 1. **Answer Construction** — recover the actual choices/path/subsequence that produced the DP answer.
> 2. **Non-Integer DP States** — store DP states when one parameter is a `string`, `vector`, `map`, or another complex type.
>
> Follow the same DP construction flow:
>
> **Problem → Story Removed → State → Choices → Transition → Base Case → Final Subproblem → Complexity → Reconstruction / Storage → Dry Run → C++17**

---

# Problem Links

1. **AtCoder DP Contest F — LCS**  
   https://atcoder.jp/contests/dp/tasks/dp_f

2. **Google Kick Start — Palindrome Free Strings**  
   https://zibada.guru/gcj/ks2022a/problems/#C

3. **Codeforces 1969C — Minimising the Sum**  
   https://codeforces.com/problemset/problem/1969/C

---

# Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [Big Picture](#2-big-picture)
3. [Part A — Answer Construction](#3-part-a--answer-construction)
4. [Warm-up — Minimum Cost Grid](#4-warm-up--minimum-cost-grid)
5. [Store the Best Choice](#5-store-the-best-choice)
6. [Construct the Grid Path](#6-construct-the-grid-path)
7. [Important Note About Space Optimization](#7-important-note-about-space-optimization)
8. [Problem 1 — Longest Common Subsequence](#8-problem-1--longest-common-subsequence)
9. [Recursive LCS State](#9-recursive-lcs-state)
10. [Iterative LCS](#10-iterative-lcs)
11. [Construct the Actual LCS](#11-construct-the-actual-lcs)
12. [LCS Dry Run](#12-lcs-dry-run)
13. [Part B — Non-Integer DP Parameters](#13-part-b--non-integer-dp-parameters)
14. [Why Arrays Are Not Always Enough](#14-why-arrays-are-not-always-enough)
15. [Map-Based DP Storage](#15-map-based-dp-storage)
16. [Choosing the DP Container](#16-choosing-the-dp-container)
17. [Problem 2 — Palindrome Free Strings](#17-problem-2--palindrome-free-strings)
18. [Naive String State](#18-naive-string-state)
19. [Critical State Reduction](#19-critical-state-reduction)
20. [Checking Length-5 and Length-6 Palindromes](#20-checking-length-5-and-length-6-palindromes)
21. [Memoized Solution Pattern](#21-memoized-solution-pattern)
22. [Problem 3 — Minimising the Sum](#22-problem-3--minimising-the-sum)
23. [Recognition Checklist](#23-recognition-checklist)
24. [Final Mental Model](#24-final-mental-model)

---

# 1. Prerequisites

Before this lesson, make sure these DP ideas are comfortable.

---

## 1.1 A DP state must have a precise meaning

Never treat:

```text
dp[i][j]
```

as only an array cell.

Write its meaning first.

Example:

```text
dp[i][j]
=
minimum cost to travel
from grid cell (i,j)
to destination (n-1,m-1)
```

or:

```text
dp[i][j]
=
LCS length between
suffix s[i...]
and suffix t[j...]
```

The state meaning determines the transition.

---

## 1.2 Optimal value vs actual answer

These are different tasks.

### Task A — Find only the value

Example:

```text
minimum path cost = 17
```

### Task B — Construct the answer

Example:

```text
minimum path:

(0,0)
→ (1,0)
→ (1,1)
→ (2,1)
→ (2,2)
```

The second task requires extra information.

---

## 1.3 Every DP transition makes a choice

Example:

```text
dp[i][j]
=
grid[i][j]
+
min(
    dp[i+1][j],   // DOWN
    dp[i][j+1]    // RIGHT
)
```

The value tells us:

```text
minimum cost
```

but it does not directly tell us:

```text
which branch was chosen
```

For reconstruction we must recover or store that choice.

---

## 1.4 Parent / choice information

Typical reconstruction information:

```text
parent[state]
```

or:

```text
choice[state]
```

Examples:

```text
choice[i][j] = DOWN
choice[i][j] = RIGHT
```

or:

```text
parent[position] = previousPosition
```

or:

```text
take[i][j] = true / false
```

---

## 1.5 LCS prerequisite

A subsequence keeps relative order but may skip characters.

Example:

```text
s = "axyb"
```

Possible subsequences include:

```text
"ayb"
"axb"
"xy"
"ab"
```

A **common subsequence** appears in both strings.

The **Longest Common Subsequence** is the common subsequence with maximum length.

---

## 1.6 Map prerequisite

Arrays are natural when state parameters are integers with small known ranges.

Example:

```text
dp[1000][1000]
```

But what if one state parameter is:

```text
string
vector<int>
set<int>
```

Then ordinary direct array indexing is not natural.

The lecture introduces:

```text
map
```

as the storage structure for such states.

---

## 1.7 `map` vs `unordered_map`

Lecture-level mental model:

```text
map
→ tree-based
→ logarithmic operations
```

```text
unordered_map
→ hash-based
→ average constant-time operations
```

The exact choice depends on the key type and what operations are required.

---

## 1.8 Why state compression still matters

Even when a state is stored in a map:

```text
dp[index][stringState]
```

we should not store a huge string if only a small suffix matters.

This lecture combines:

```text
non-integer state storage
+
state reduction
```

in the Palindrome Free Strings example.

---

# 2. Big Picture

This lecture has two major topics.

```text
DP INTERMEDIATE
│
├── ANSWER CONSTRUCTION
│   │
│   ├── calculate optimal value
│   ├── store / recover best choice
│   └── walk through choices to build answer
│
└── NON-INTEGER DP STATES
    │
    ├── state contains string / complex type
    ├── use map-like storage
    └── keep only relevant part of complex state
```

---

# 3. Part A — Answer Construction

The lecture definition is:

```text
Instead of only finding the optimal value,
find the exact possibility
that generates that value.
```

Example:

```text
minimum grid-path sum
```

is not enough.

We may also need:

```text
the actual minimum-cost path
```

---

## 3.1 Core observation

At every state, DP makes an optimal choice.

Example:

```text
DOWN
or
RIGHT
```

If we store the best choice for every state, then reconstruction is easy.

---

## 3.2 Reconstruction process

```text
1. Solve the DP.

2. For every state,
   remember the best transition.

3. Start from the state
   containing the final subproblem.

4. Follow the stored choice.

5. Continue until the terminal state.

6. The visited choices form the answer.
```

---

# 4. Warm-up — Minimum Cost Grid

Consider an:

```text
n × m
```

grid.

From cell:

```text
(i,j)
```

we can move:

```text
RIGHT → (i,j+1)

DOWN  → (i+1,j)
```

Goal:

```text
reach (n-1,m-1)
with minimum possible total cost
```

---

## 4.1 STATE

Define:

```text
dp[i][j]
=
minimum cost required
to travel from cell (i,j)
to cell (n-1,m-1)
```

Visual:

```text
(i,j)
  |
  +------ RIGHT ------> (i,j+1)
  |
  |
 DOWN
  |
  v
(i+1,j)
```

---

## 4.2 CHOICES

At `(i,j)` there are at most two choices.

### Choice 1 — DOWN

```text
grid[i][j]
+
dp[i+1][j]
```

### Choice 2 — RIGHT

```text
grid[i][j]
+
dp[i][j+1]
```

---

## 4.3 TRANSITION

```text
dp[i][j]
=
grid[i][j]
+
min(
    dp[i+1][j],
    dp[i][j+1]
)
```

Boundary conditions determine whether both transitions are valid.

---

## 4.4 BASE CASE

At destination:

```text
(i,j) = (n-1,m-1)
```

there are no more moves.

So:

```text
dp[n-1][m-1]
=
grid[n-1][m-1]
```

---

## 4.5 FINAL SUBPROBLEM

Starting at top-left:

```text
answer value = dp[0][0]
```

But now we want more:

```text
the actual path
```

---

# 5. Store the Best Choice

Suppose we calculate:

```text
downCost  = dp[i+1][j]
rightCost = dp[i][j+1]
```

If:

```text
downCost <= rightCost
```

store:

```text
choice[i][j] = DOWN
```

Otherwise:

```text
choice[i][j] = RIGHT
```

The lecture's code stores both:

```text
minimum value
+
best direction
```

in the DP state.

A natural structure is:

```cpp
pair<int,int>
```

where:

```text
first  = minimum cost
second = chosen direction
```

For example:

```text
0 → DOWN
1 → RIGHT
```

---

## 5.1 Why store the choice?

Without the stored choice, during reconstruction we would again need to reason about all transitions.

With:

```text
choice[i][j]
```

we simply follow the already-decided optimal move.

---

# 6. Construct the Grid Path

Start:

```text
current = (0,0)
```

Print / record the current cell.

Then:

```text
while current != destination
```

look at:

```text
choice[current]
```

If:

```text
DOWN
```

do:

```text
row++
```

If:

```text
RIGHT
```

do:

```text
column++
```

Continue until:

```text
(n-1,m-1)
```

---

## 6.1 Visual

```text
S . . .
↓
x → x .
    ↓
    x → x
        ↓
        T
```

The path is obtained by following the stored decision from each DP state.

---

## 6.2 C++17 reconstruction pattern

```cpp
#include <bits/stdc++.h>
using namespace std;

const long long INF = (1LL << 60);

int main() {
    int n, m;
    cin >> n >> m;

    vector<vector<long long>> grid(
        n,
        vector<long long>(m)
    );

    for (auto& row : grid) {
        for (auto& value : row) {
            cin >> value;
        }
    }

    vector<vector<long long>> dp(
        n,
        vector<long long>(m, INF)
    );

    // 'D' = down
    // 'R' = right
    vector<vector<char>> choice(
        n,
        vector<char>(m, '?')
    );

    for (int i = n - 1; i >= 0; i--) {
        for (int j = m - 1; j >= 0; j--) {

            if (i == n - 1 && j == m - 1) {
                dp[i][j] = grid[i][j];
                continue;
            }

            long long downCost = INF;
            long long rightCost = INF;

            if (i + 1 < n) {
                downCost = dp[i + 1][j];
            }

            if (j + 1 < m) {
                rightCost = dp[i][j + 1];
            }

            if (downCost <= rightCost) {
                dp[i][j] = grid[i][j] + downCost;
                choice[i][j] = 'D';
            } else {
                dp[i][j] = grid[i][j] + rightCost;
                choice[i][j] = 'R';
            }
        }
    }

    cout << "Minimum cost = "
         << dp[0][0]
         << '\n';

    int row = 0;
    int col = 0;

    while (true) {
        cout << '(' << row << ',' << col << ")\n";

        if (row == n - 1 && col == m - 1) {
            break;
        }

        if (choice[row][col] == 'D') {
            row++;
        } else {
            col++;
        }
    }
}
```

---

# 7. Important Note About Space Optimization

The lecture explicitly highlights this point:

```text
When constructing the answer,
we need the DP states together with
the best choice for every state.
```

Therefore the lecture warns against discarding the information needed for reconstruction through ordinary space compression.

For this lesson, keep the mental model:

```text
value-only DP
→ may allow aggressive space optimization

answer construction
→ preserve enough state / choice information
   to reconstruct the answer
```

---

# 8. Problem 1 — Longest Common Subsequence

Lecture problem:

**AtCoder DP Contest F — LCS**

```text
https://atcoder.jp/contests/dp/tasks/dp_f
```

Goal:

```text
find the actual longest common subsequence
```

not only its length.

---

## 8.1 Example intuition

Suppose:

```text
s = "axyb"
t = "abyup"
```

We search for characters occurring in both strings in the same relative order.

Possible common subsequences include shorter strings.

The DP first finds:

```text
maximum LCS length
```

Then answer construction recovers:

```text
the actual subsequence
```

---

# 9. Recursive LCS State

A natural recursive state is:

```text
dp[i][j]
=
length of LCS between

s[i ... n-1]
and
t[j ... m-1]
```

---

## 9.1 BASE CASE

If either string is exhausted:

```text
i == n
or
j == m
```

then:

```text
dp[i][j] = 0
```

---

## 9.2 Case 1 — Characters match

If:

```text
s[i] == t[j]
```

we can include this character.

Then:

```text
dp[i][j]
=
1 + dp[i+1][j+1]
```

---

## 9.3 Case 2 — Characters do not match

If:

```text
s[i] != t[j]
```

we can skip from one side.

Choice 1:

```text
dp[i+1][j]
```

Choice 2:

```text
dp[i][j+1]
```

Therefore:

```text
dp[i][j]
=
max(
    dp[i+1][j],
    dp[i][j+1]
)
```

---

## 9.4 Important lecture observation

In recursive DP, these two branches already cover the possibilities:

```text
skip s[i]

or

skip t[j]
```

The recurrence explores all relevant suffix combinations.

---

# 10. Iterative LCS

For answer construction, the lecture also shows the standard iterative table.

Define:

```text
dp[i][j]
=
LCS length between

first i characters of s
and
first j characters of t
```

This is a prefix-style formulation.

---

## 10.1 Base row and column

If one prefix is empty:

```text
dp[0][j] = 0
dp[i][0] = 0
```

---

## 10.2 Characters match

Using 1-based DP indices:

```text
s[i-1] == t[j-1]
```

Then:

```text
dp[i][j]
=
dp[i-1][j-1] + 1
```

---

## 10.3 Characters differ

If:

```text
s[i-1] != t[j-1]
```

then:

```text
dp[i][j]
=
max(
    dp[i-1][j],
    dp[i][j-1]
)
```

---

## 10.4 Complexity

For:

```text
n = |s|
m = |t|
```

we fill:

```text
(n+1) × (m+1)
```

states.

So:

```text
Time  = O(nm)
Space = O(nm)
```

The full table is useful for reconstruction.

---

# 11. Construct the Actual LCS

After filling the DP table:

```text
x = n
y = m
```

Start from:

```text
dp[n][m]
```

and move backwards.

---

## 11.1 If characters match

If:

```text
s[x-1] == t[y-1]
```

this character belongs to the LCS.

So:

```text
answer += s[x-1]
```

Then move diagonally:

```text
x--
y--
```

---

## 11.2 If characters differ

Compare:

```text
dp[x-1][y]
```

and:

```text
dp[x][y-1]
```

If:

```text
dp[x-1][y] >= dp[x][y-1]
```

move:

```text
x--
```

Otherwise:

```text
y--
```

---

## 11.3 Why the answer is reversed

We reconstruct from:

```text
end → beginning
```

So characters are collected backwards.

Therefore after reconstruction:

```text
reverse(answer.begin(), answer.end())
```

---

## 11.4 C++17 — LCS with answer construction

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    string s, t;
    cin >> s >> t;

    int n = (int)s.size();
    int m = (int)t.size();

    vector<vector<int>> dp(
        n + 1,
        vector<int>(m + 1, 0)
    );

    for (int i = 1; i <= n; i++) {
        for (int j = 1; j <= m; j++) {

            if (s[i - 1] == t[j - 1]) {
                dp[i][j]
                    =
                    dp[i - 1][j - 1] + 1;
            } else {
                dp[i][j]
                    =
                    max(
                        dp[i - 1][j],
                        dp[i][j - 1]
                    );
            }
        }
    }

    int x = n;
    int y = m;

    string answer;

    while (x > 0 && y > 0) {

        if (s[x - 1] == t[y - 1]) {

            answer += s[x - 1];

            x--;
            y--;
        }
        else if (dp[x - 1][y] >= dp[x][y - 1]) {

            x--;
        }
        else {

            y--;
        }
    }

    reverse(answer.begin(), answer.end());

    cout << answer << '\n';

    return 0;
}
```

---

# 12. LCS Dry Run

Suppose:

```text
s = "abyb"
t = "byba"
```

The lecture illustrates reconstruction by walking through the completed DP table.

We start from:

```text
dp[n][m]
```

and repeatedly inspect the current characters / neighboring DP values.

---

## Step 1

At:

```text
x = n
y = m
```

If:

```text
s[x-1] != t[y-1]
```

move toward the neighboring cell containing the same optimal LCS length.

---

## Step 2

Eventually we reach a pair where:

```text
s[x-1] == t[y-1]
```

Then:

```text
take this character
```

and move:

```text
diagonally
```

---

## Step 3

Repeat until:

```text
x == 0
or
y == 0
```

The collected characters are reversed at the end.

---

## Recognition pattern

For LCS reconstruction:

```text
MATCH
→ take character
→ diagonal

NO MATCH
→ move toward neighbor
  with larger DP value
```

---

# 13. Part B — Non-Integer DP Parameters

The lecture now asks:

```text
What if a DP parameter is not an integer?
```

Examples:

```text
string
vector
map
complex object
```

An ordinary multidimensional array is not always convenient.

---

# 14. Why Arrays Are Not Always Enough

Suppose your state is conceptually:

```text
dp[index][currentString]
```

where:

```text
index
```

is an integer,

but:

```text
currentString
```

is a string.

We cannot naturally write:

```text
dp[index]["01001"]
```

using a normal numeric vector index.

So the lecture recommends map-based storage.

---

# 15. Map-Based DP Storage

One direct representation is:

```cpp
map<pair<int, string>, int> dp;
```

Then a state can be accessed using:

```cpp
dp[{index, currentString}]
```

---

## 15.1 Complexity idea

A tree `map` lookup depends on the number of stored states.

If there are around:

```text
n × m
```

states, lookup may cost roughly:

```text
O(log(nm))
```

The lecture then shows that we can sometimes do better structurally.

---

# 16. Choosing the DP Container

Suppose:

```text
index
```

is a normal integer:

```text
0 ... n-1
```

and only the second parameter is a string.

Instead of:

```cpp
map<pair<int,string>, int> dp;
```

we can use:

```cpp
vector<map<string,int>> dp(n);
```

Now:

```text
index
```

is handled directly by the vector.

Only:

```text
stringState
```

is looked up inside the map.

Access becomes conceptually:

```cpp
dp[index][stringState]
```

This matches the actual state structure more closely.

---

## 16.1 Mental rule

If a state has:

```text
one small integer dimension
+
one complex dimension
```

consider:

```text
vector<map<complexType, value>>
```

instead of putting everything into one large pair-key map.

---

# 17. Problem 2 — Palindrome Free Strings

Lecture example:

**Google Kick Start — Palindrome Free Strings**

The input string contains:

```text
0
1
?
```

Each:

```text
?
```

can be replaced by:

```text
0
or
1
```

The lecture focuses on avoiding palindromic substrings of the relevant lengths.

---

## 17.1 Natural recursive thinking

Process the string from left to right.

At each index:

```text
if fixed 0/1
→ append it

if ?
→ try 0
→ try 1
```

We need to know whether the prefix built so far is still valid.

---

# 18. Naive String State

A first idea is:

```text
dp[index][builtString]
```

Meaning:

```text
Can we complete the remaining positions
starting at `index`,
given the string constructed so far?
```

This is a valid non-integer DP state.

But it is too large if we keep:

```text
the entire prefix
```

---

## 18.1 Why the entire string is unnecessary

To decide whether adding the next character creates a forbidden palindrome, we only need the recent characters that could participate in a new short palindrome.

The lecture therefore shortens the state.

---

# 19. Critical State Reduction

The lecture stores only:

```text
last 6 characters
```

of the string built so far.

This is the key optimization.

Instead of:

```text
state string length = O(n)
```

we maintain:

```text
state string length <= 6
```

---

## 19.1 Helper

Conceptually:

```text
shorten(s):

if |s| <= 6
    return s

otherwise
    return last 6 characters
```

C++:

```cpp
string shorten(const string& s) {

    if ((int)s.size() <= 6) {
        return s;
    }

    return s.substr(
        s.size() - 6
    );
}
```

---

## 19.2 Why this changes the state space

Binary strings of length at most `6` are very limited.

At maximum length:

```text
2^6 = 64
```

possible binary suffixes.

So instead of remembering an arbitrary long prefix, each index only needs a small set of suffix states.

This is why the lecture's slide summarizes the state space using a small factor around `64`.

---

# 20. Checking Length-5 and Length-6 Palindromes

The lecture's helper works on a suffix of length at most `6`.

Its logic is:

```text
If length < 5:
    no forbidden palindrome yet

Check whether the whole current suffix is palindrome.

If length == 6:
    also check the last 5 characters.
```

So the recent suffix is enough to detect newly formed palindromes of the relevant lengths.

---

## 20.1 Generic palindrome helper

```cpp
bool isPalindrome(const string& s) {

    int left = 0;
    int right = (int)s.size() - 1;

    while (left < right) {

        if (s[left] != s[right]) {
            return false;
        }

        left++;
        right--;
    }

    return true;
}
```

---

## 20.2 Lecture-style validity check

```cpp
bool hasBadPalindrome(string s) {

    if ((int)s.size() < 5) {
        return false;
    }

    // Entire suffix:
    // length 5 or 6
    if (isPalindrome(s)) {
        return true;
    }

    // If we hold 6 chars,
    // also inspect the last 5.
    if ((int)s.size() == 6) {

        string lastFive = s.substr(1);

        if (isPalindrome(lastFive)) {
            return true;
        }
    }

    return false;
}
```

---

# 21. Memoized Solution Pattern

Define:

```text
solve(index, suffix)
```

Meaning:

```text
Is it possible to complete
the string from index onward
when `suffix` is the recent string state?
```

---

## 21.1 First normalize the string state

At each call:

```text
suffix = shorten(suffix)
```

This ensures the memo state never stores a long string.

---

## 21.2 Invalid state

If:

```text
suffix contains forbidden palindrome
```

return:

```text
false
```

---

## 21.3 Base case

If:

```text
index == n
```

and no invalid palindrome was created:

```text
return true
```

---

## 21.4 Fixed character

If:

```text
S[index] != '?'
```

there is one transition:

```text
solve(
    index + 1,
    suffix + S[index]
)
```

---

## 21.5 Question mark

If:

```text
S[index] == '?'
```

try both:

```text
solve(index+1, suffix+'0')

OR

solve(index+1, suffix+'1')
```

If either succeeds:

```text
true
```

---

## 21.6 Memo storage

The lecture uses the pattern:

```cpp
vector<map<string, int>> dp(n);
```

A missing key means:

```text
state not calculated yet
```

Stored values represent:

```text
possible / impossible
```

---

## 21.7 C++17 pattern

```cpp
#include <bits/stdc++.h>
using namespace std;

string inputString;
int n;

vector<map<string, int>> memo;

string shortenState(const string& s) {

    if ((int)s.size() <= 6) {
        return s;
    }

    return s.substr(
        s.size() - 6
    );
}

bool isPalindrome(const string& s) {

    int left = 0;
    int right = (int)s.size() - 1;

    while (left < right) {

        if (s[left] != s[right]) {
            return false;
        }

        left++;
        right--;
    }

    return true;
}

bool hasForbiddenPalindrome(const string& s) {

    if ((int)s.size() < 5) {
        return false;
    }

    if (isPalindrome(s)) {
        return true;
    }

    if ((int)s.size() == 6) {

        string lastFive = s.substr(1);

        if (isPalindrome(lastFive)) {
            return true;
        }
    }

    return false;
}

bool solve(int index, string suffix) {

    suffix = shortenState(suffix);

    if (hasForbiddenPalindrome(suffix)) {
        return false;
    }

    if (index == n) {
        return true;
    }

    auto it = memo[index].find(suffix);

    if (it != memo[index].end()) {
        return it->second;
    }

    bool possible = false;

    if (inputString[index] != '?') {

        possible =
            solve(
                index + 1,
                suffix + inputString[index]
            );
    }
    else {

        possible =
            solve(
                index + 1,
                suffix + '0'
            )
            ||
            solve(
                index + 1,
                suffix + '1'
            );
    }

    memo[index][suffix] = possible;

    return possible;
}
```

The important lecture pattern is:

```text
non-integer state
+
map memoization
+
keep only last 6 characters
```

---

# 21.8 Why recursive memoization is attractive here

The lecture remarks that an iterative approach may create many useless states.

Top-down recursion naturally visits only:

```text
reachable states
```

So with:

```text
index
+
recent suffix
```

memoization avoids calculating combinations that never occur.

---

# 21.9 State-count mental model

At each index, the suffix has only a limited number of possible values.

Maximum binary suffix count around length `6`:

```text
2^6 = 64
```

So the useful state space becomes roughly:

```text
O(n × 64)
```

rather than remembering an arbitrary full prefix.

This is the key state-design insight.

---

# 22. Problem 3 — Minimising the Sum

Lecture homework/problem-solving problem:

**Codeforces 1969C — Minimising the Sum**

```text
https://codeforces.com/problemset/problem/1969/C
```

The final slide shows a memoized DP with:

```text
index
opsLeft
```

as the state.

---

## 22.1 STATE

Conceptually:

```text
dp[opsLeft][index]
=
minimum achievable sum
from index onward
with `opsLeft` operations remaining
```

The lecture solution stores:

```cpp
vector dp(
    k + 1,
    vector(n, -1LL)
);
```

---

## 22.2 First option — do nothing special

Start with:

```text
answer
=
a[index]
+
solve(index+1, opsLeft)
```

This means:

```text
keep current element normally
```

---

## 22.3 Try using operations on a segment

The lecture keeps:

```text
mn = a[index]
```

Then for:

```text
i = 1 ... opsLeft
```

extend to the right.

Update:

```text
mn
=
min(
    mn,
    a[index+i]
)
```

If the segment length is:

```text
i + 1
```

and all those elements contribute using the segment minimum, candidate becomes:

```text
(i+1) * mn
+
solve(
    index+i+1,
    opsLeft-i
)
```

Take the minimum over all valid choices.

---

## 22.4 Lecture recurrence shape

```text
solve(index, opsLeft)
=
min(
    a[index] + solve(index+1, opsLeft),

    for every i <= opsLeft:
        (i+1) * min(a[index...index+i])
        +
        solve(index+i+1, opsLeft-i)
)
```

---

## 22.5 Important optimization inside the loop

Do not recompute:

```text
min(a[index ... index+i])
```

from scratch.

Maintain:

```text
mn
```

incrementally:

```cpp
mn = min(mn, a[index + i]);
```

So extending the segment costs only:

```text
O(1)
```

extra per extension.

---

## 22.6 C++17 pattern from the lecture

```cpp
vector<vector<long long>> dp(
    k + 1,
    vector<long long>(n, -1)
);

function<long long(int,int)> solve =
[&](int index, int opsLeft) -> long long {

    if (index == n) {
        return 0LL;
    }

    if (dp[opsLeft][index] != -1) {
        return dp[opsLeft][index];
    }

    long long answer =
        a[index]
        +
        solve(index + 1, opsLeft);

    long long minimumValue = a[index];

    for (int used = 1;
         used <= opsLeft;
         used++) {

        if (index + used >= n) {
            break;
        }

        minimumValue =
            min(
                minimumValue,
                a[index + used]
            );

        answer =
            min(
                answer,
                1LL * (used + 1)
                * minimumValue
                +
                solve(
                    index + used + 1,
                    opsLeft - used
                )
            );
    }

    return dp[opsLeft][index] = answer;
};
```

---

# 23. Recognition Checklist

Use this section during contests.

---

## 23.1 Answer construction smell

If the problem says:

```text
print the path
print the sequence
print the chosen indices
print the actual subsequence
```

then finding only:

```text
dp answer value
```

is not enough.

Ask:

```text
What choice produced this dp state?
```

---

## 23.2 Standard reconstruction pattern

```text
DP calculation
      |
      v
store best decision
      |
      v
start from final state
      |
      v
follow decisions
      |
      v
construct answer
```

---

## 23.3 LCS recognition

```text
s[i] == t[j]
→ take character
→ diagonal state
```

```text
s[i] != t[j]
→ skip from one string
→ take max
```

During reconstruction:

```text
match
→ append

otherwise
→ move toward neighbor
  with optimal value
```

---

## 23.4 Non-integer state smell

If state naturally looks like:

```text
dp[index][string]
```

or:

```text
dp[index][vector]
```

do not force it into a huge numeric array.

Think:

```text
map
```

---

## 23.5 Container choice

If every dimension is complex:

```text
map<pair<...>, value>
```

may be natural.

If one dimension is a small integer:

```text
vector<map<...>>
```

may represent the state more directly.

---

## 23.6 Complex-state compression

Before storing:

```text
whole string
```

ask:

```text
Does the future really depend
on the entire string?
```

Palindrome Free Strings:

```text
entire prefix
        ↓
only recent suffix
        ↓
last 6 characters
```

This is the most important optimization in that example.

---

# 23.7 Contest decoding table

| Problem smell | DP thought |
|---|---|
| Need actual optimal path | Store choice / parent |
| Need actual subsequence | Backtrack through DP table |
| `dp[i][j]` contains value + decision | Store pair / separate choice table |
| String is part of DP state | Use map-based memoization |
| String state grows with `n` | Ask which suffix/history actually matters |
| Only last few symbols affect validity | Keep bounded suffix state |
| Recursive memo visits few states | Prefer top-down over filling many useless states |
| Segment decision with limited operations | `dp[index][opsLeft]` |

---

# 24. Final Mental Model

## Answer Construction

```text
OPTIMAL DP VALUE
      |
      v
Which transition produced it?
      |
      v
Store the choice
      |
      v
Start from final state
      |
      v
Follow stored choices
      |
      v
ACTUAL ANSWER
```

Grid example:

```text
dp[i][j]
=
grid[i][j]
+
min(
    DOWN,
    RIGHT
)

store:

choice[i][j]
=
DOWN / RIGHT
```

LCS:

```text
match
→ character belongs to answer
→ diagonal

no match
→ move toward better neighbor
```

---

## Non-Integer DP States

```text
STATE HAS STRING / COMPLEX OBJECT
      |
      v
Cannot index normal array naturally
      |
      v
Use map-based storage
      |
      v
But first ask:
"Do I need the whole object?"
      |
      v
Compress to only relevant history
```

Palindrome Free Strings:

```text
whole constructed prefix
        ↓
last 6 characters
        ↓
small finite state space
```

---

# Final Revision Box

```text
ANSWER CONSTRUCTION
──────────────────────────────────

Finding value:
dp[state] = optimal value

Constructing answer:
also remember which choice gave that value

Then:

final state
→ choice
→ next state
→ choice
→ ...
→ terminal state


LCS
──────────────────────────────────

If characters match:
    take character
    go diagonal

Else:
    move toward larger neighboring DP value

Reverse answer at the end.


NON-INTEGER STATES
──────────────────────────────────

If state contains:
string / vector / complex object

Use:
map / unordered_map

But always ask:

Can I store only the relevant suffix/history?


PALINDROME FREE STRING
──────────────────────────────────

Do not store complete prefix.

Keep only:
last 6 characters

Check new length-5 / length-6 palindrome.


CORE CONTEST QUESTIONS
──────────────────────────────────

1. Does the problem ask only for value,
   or also the actual construction?

2. What decision created each optimal state?

3. Can I backtrack through the DP?

4. Is any DP parameter non-integer?

5. Can a map represent the state?

6. Can the complex state be shortened?
```
