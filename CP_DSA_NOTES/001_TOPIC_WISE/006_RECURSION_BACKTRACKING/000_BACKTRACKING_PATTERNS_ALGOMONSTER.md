# 🧠 Backtracking Patterns — AlgoMonster PDF Master Notes
## LCCM + Pruning + Additional State + Aggregation + Deduplication

> Source PDF summary: pages show the LCCM model for combinatorial search, backtracking with pruning, additional state, aggregation, permutation state using a boolean array, word break, decode ways, coin change, deduplication for 3Sum, combination sum, and subsets. The PDF emphasizes **Level, Choices, Constraints, Move** as the coding framework.

---

# 📚 Clickable Index

## Core Concepts
- [0. Backtracking Mental Model](#0-backtracking-mental-model)
- [0.1 LCCM Template](#01-lccm-template)
- [0.2 Backtracking Pattern Decision Tree](#02-backtracking-pattern-decision-tree)
- [0.3 Complexity Cheat Sheet](#03-complexity-cheat-sheet)


## Phase 1 — Core Combinatorial Search

- [Problem 1: Generate All Strings From Choices](#problem-1-generate-all-strings-from-choices) — Easy — `LCCM: Level=index, Choice=character, Constraint=index<n, Move=add→recurse→remove`
- [Problem 2: Letter Combinations of a Phone Number](#problem-2-letter-combinations-of-a-phone-number) — Medium — `LCCM: Level=digit index, Choice=letter mapped from digit`

## Phase 2 — Backtracking With Pruning

- [Problem 3: Palindrome Partitioning](#problem-3-palindrome-partitioning) — Medium — `LCCM: Level=start index, Choice=substring start..end, Constraint=substring is palindrome`

## Phase 3 — Backtracking With Additional State

- [Problem 4: Generate Valid Parentheses](#problem-4-generate-valid-parentheses) — Medium — `Additional state: open count and close count`
- [Problem 5: Permutations](#problem-5-permutations) — Medium — `Additional state: used boolean array`

## Phase 4 — Aggregation / Return Value Backtracking

- [Problem 6: Word Break](#problem-6-word-break) — Medium — `Aggregation OR: does any branch return true?`
- [Problem 7: Number of Ways to Decode a Message](#problem-7-number-of-ways-to-decode-a-message) — Medium — `Aggregation SUM: number of valid branches`
- [Problem 8: Coin Change Minimum Coins](#problem-8-coin-change-minimum-coins) — Medium — `Aggregation MIN: take or skip coin`

## Phase 5 — Deduplication Patterns

- [Problem 9: Three Sum Without Duplicate Triplets](#problem-9-three-sum-without-duplicate-triplets) — Medium — `Sort + fixed i + two pointers + skip duplicates`

## Phase 6 — Combination Style Backtracking

- [Problem 10: Combination Sum](#problem-10-combination-sum) — Medium — `Index based take/skip, unlimited reuse`
- [Problem 11: Subsets](#problem-11-subsets) — Easy — `Include / exclude at every index`


---

# 0. Backtracking Mental Model

Backtracking means:

```text
choose
    ↓
explore recursively
    ↓
undo choice
```

The PDF repeatedly uses this movement:

```text
add
recurse(level + something)
remove
```

That is the heart of every backtracking problem.

---

# 0.1 LCCM Template

LCCM:

| Letter | Meaning | Question |
|---|---|---|
| L | Level | What does recursion level represent? |
| C | Choices | What choices can I make at this level? |
| C | Constraints | Which choices are valid? |
| M | Move | How do I apply choice, recurse, and undo? |

## Universal C++ Template

```cpp
void dfs(int level, vector<int>& path) {
    if (isBaseCase(level, path)) {
        report(path);
        return;
    }

    for (auto choice : getChoices(level, path)) {
        if (!isValid(choice, level, path)) continue;

        path.push_back(choice);

        dfs(nextLevel(level, choice), path);

        path.pop_back();
    }
}
```

---

# 0.2 Backtracking Pattern Decision Tree

```text
Do I need to output all possibilities?
        |
        +-- YES --> Backtracking
                    |
                    +-- Need to avoid invalid branches?
                    |       |
                    |       +-- YES --> Backtracking with pruning
                    |
                    +-- Need extra info like used/open/close?
                    |       |
                    |       +-- YES --> Backtracking with additional state

Do I need true/false or min/max/count?
        |
        +-- YES --> Aggregation recursion
                    |
                    +-- true/false --> OR aggregation
                    +-- count ways  --> SUM aggregation
                    +-- min/max      --> MIN/MAX aggregation
```

---

# 0.3 Complexity Cheat Sheet

| Problem | Pattern | Rough Complexity |
|---|---|---|
| Generate strings | k choices per level | O(k^n) |
| Phone combinations | 3 or 4 choices per digit | O(4^n) |
| Palindrome partitioning | choose cuts | O(n * 2^n) |
| Valid parentheses | Catalan | O(Cn) |
| Permutations | unused choice each level | O(n!) |
| Word break | aggregation + memo | O(n * words * wordLen) |
| Decode ways | aggregation + memo | O(n) |
| Coin change | DP recursion | O(n * amount) |
| 3Sum | sort + two pointers | O(n²) |
| Combination sum | take/skip recursion | exponential |
| Subsets | include/exclude | O(n * 2^n) |

---


# Problem 1: Generate All Strings From Choices

**Phase:** Phase 1 — Core Combinatorial Search  
**Difficulty:** Easy  
**Pattern:** `LCCM: Level=index, Choice=character, Constraint=index<n, Move=add→recurse→remove`

## Problem Statement

Given possible characters at each position, generate all strings. Example choices are {a,b} for each of 2 positions.

## Input

```text
choices = ['a', 'b']
length = 2
```

## Expected Output

```text
aa
ab
ba
bb
```

## Core Idea

At every level, choose one character, append it to path, recurse to next index, then remove it while backtracking.

## LCCM Summary


```text
Level = index / position
Choices = valid next elements
Constraint = depends on problem
Move = add → recurse → remove
```


## Recursion Tree

```text
rec(0, "")
├── choose 'a' -> rec(1, "a")
│   ├── choose 'a' -> rec(2, "aa") ✅ output
│   └── choose 'b' -> rec(2, "ab") ✅ output
└── choose 'b' -> rec(1, "b")
    ├── choose 'a' -> rec(2, "ba") ✅ output
    └── choose 'b' -> rec(2, "bb") ✅ output
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<string> ans;
string path;

void dfs(int level, int n) {
    if (level == n) {
        ans.push_back(path);
        return;
    }

    for (char ch : {'a', 'b'}) {
        path.push_back(ch);
        dfs(level + 1, n);
        path.pop_back();
    }
}
```

## Index-by-Index Dry Run

```text
choices = ['a', 'b']
length = 2

level=0, path=""
    choose 'a'
    path="a"
    call level=1

level=1, path="a"
    choose 'a'
    path="aa"
    call level=2
        level == length
        output "aa"

    backtrack
    path="a"

    choose 'b'
    path="ab"
    call level=2
        output "ab"

    backtrack
    path="a"

backtrack
path=""

level=0, path=""
    choose 'b'
    path="b"
    call level=1

level=1, path="b"
    choose 'a'
    path="ba"
    call level=2
        output "ba"

    backtrack
    path="b"

    choose 'b'
    path="bb"
    call level=2
        output "bb"

Final output:
    aa, ab, ba, bb
```

## Complexity

Time O(k^n * n), Space O(n) recursion path, excluding output.

## Pattern Trigger


Use this when the problem asks to generate all combinations/strings/subsets by trying choices recursively.


---


# Problem 2: Letter Combinations of a Phone Number

**Phase:** Phase 1 — Core Combinatorial Search  
**Difficulty:** Medium  
**Pattern:** `LCCM: Level=digit index, Choice=letter mapped from digit`

## Problem Statement

Given digits 2-9, return all possible letter combinations based on phone keypad mapping.

## Input

```text
digits = "23"
```

## Expected Output

```text
["ad","ae","af","bd","be","bf","cd","ce","cf"]
```

## Core Idea

For each digit, iterate over mapped letters. Add letter, recurse to next digit, then remove letter.

## LCCM Summary


```text
Level = index in digits
Choices = letters mapped from digits[level]
Constraint = level < digits.size()
Move = add letter → recurse(level+1) → remove letter
```


## Recursion Tree

```text
rec(0, "")
├── choose 'a' from digit 2 -> rec(1, "a")
│   ├── choose 'd' -> "ad" ✅
│   ├── choose 'e' -> "ae" ✅
│   └── choose 'f' -> "af" ✅
├── choose 'b' from digit 2 -> rec(1, "b")
│   ├── choose 'd' -> "bd" ✅
│   ├── choose 'e' -> "be" ✅
│   └── choose 'f' -> "bf" ✅
└── choose 'c' from digit 2 -> rec(1, "c")
    ├── choose 'd' -> "cd" ✅
    ├── choose 'e' -> "ce" ✅
    └── choose 'f' -> "cf" ✅
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<string> letterCombinations(string digits) {
    if (digits.empty()) return {};

    vector<string> mp(10);
    mp[2] = "abc";
    mp[3] = "def";
    mp[4] = "ghi";
    mp[5] = "jkl";
    mp[6] = "mno";
    mp[7] = "pqrs";
    mp[8] = "tuv";
    mp[9] = "wxyz";

    vector<string> ans;
    string path;

    function<void(int)> dfs = [&](int level) {
        if (level == (int)digits.size()) {
            ans.push_back(path);
            return;
        }

        int digit = digits[level] - '0';

        for (char ch : mp[digit]) {
            path.push_back(ch);
            dfs(level + 1);
            path.pop_back();
        }
    };

    dfs(0);
    return ans;
}
```

## Index-by-Index Dry Run

```text
digits = "23"

mapping:
    2 -> abc
    3 -> def

level=0, digit='2', path=""
    choose 'a'
    path="a"

level=1, digit='3', path="a"
    choose 'd' -> path="ad" -> output
    remove 'd' -> path="a"

    choose 'e' -> path="ae" -> output
    remove 'e' -> path="a"

    choose 'f' -> path="af" -> output
    remove 'f' -> path="a"

backtrack from 'a'
path=""

level=0
    choose 'b'
    path="b"

level=1
    choose d/e/f
    output "bd", "be", "bf"

backtrack from 'b'

level=0
    choose 'c'
    path="c"

level=1
    choose d/e/f
    output "cd", "ce", "cf"

Final:
    ad ae af bd be bf cd ce cf
```

## Complexity

Time O(4^n * n), Space O(n) recursion path, excluding output.

## Pattern Trigger


Use this when the problem asks to generate all combinations/strings/subsets by trying choices recursively.


---


# Problem 3: Palindrome Partitioning

**Phase:** Phase 2 — Backtracking With Pruning  
**Difficulty:** Medium  
**Pattern:** `LCCM: Level=start index, Choice=substring start..end, Constraint=substring is palindrome`

## Problem Statement

Given a string s, partition it so every substring is a palindrome. Return all valid partitions.

## Input

```text
s = "aab"
```

## Expected Output

```text
[["a","a","b"], ["aa","b"]]
```

## Core Idea

At each start index, try every ending index. Only recurse if s[start..end] is palindrome. Non-palindromes are pruned.

## LCCM Summary


```text
Level = start index
Choices = substring s[start..end]
Constraint = substring must be palindrome
Move = add substring → recurse(end+1) → remove substring
```


## Recursion Tree

```text
rec(start=0, path=[])
├── choose "a" ✅ palindrome -> rec(1, ["a"])
│   ├── choose "a" ✅ -> rec(2, ["a","a"])
│   │   └── choose "b" ✅ -> rec(3, ["a","a","b"]) ✅ output
│   └── choose "ab" ❌ prune
├── choose "aa" ✅ palindrome -> rec(2, ["aa"])
│   └── choose "b" ✅ -> rec(3, ["aa","b"]) ✅ output
└── choose "aab" ❌ prune
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool isPal(const string& s, int l, int r) {
    while (l < r) {
        if (s[l] != s[r]) return false;
        l++;
        r--;
    }
    return true;
}

vector<vector<string>> partition(string s) {
    vector<vector<string>> ans;
    vector<string> path;
    int n = s.size();

    function<void(int)> dfs = [&](int start) {
        if (start == n) {
            ans.push_back(path);
            return;
        }

        for (int end = start; end < n; end++) {
            if (!isPal(s, start, end)) continue;

            path.push_back(s.substr(start, end - start + 1));
            dfs(end + 1);
            path.pop_back();
        }
    };

    dfs(0);
    return ans;
}
```

## Index-by-Index Dry Run

```text
s = "aab"

start=0, path=[]
    try substring s[0..0] = "a"
    "a" is palindrome
    add "a"
    path=["a"]
    recurse start=1

start=1, path=["a"]
    try s[1..1] = "a"
    palindrome
    path=["a","a"]
    recurse start=2

start=2, path=["a","a"]
    try s[2..2] = "b"
    palindrome
    path=["a","a","b"]
    recurse start=3

start=3
    start == n
    output ["a","a","b"]

backtrack:
    remove "b" -> ["a","a"]
    remove second "a" -> ["a"]

start=1
    try s[1..2] = "ab"
    not palindrome
    prune

backtrack:
    remove first "a" -> []

start=0
    try s[0..1] = "aa"
    palindrome
    path=["aa"]
    recurse start=2

start=2
    choose "b"
    path=["aa","b"]
    start=3
    output ["aa","b"]

start=0
    try s[0..2] = "aab"
    not palindrome
    prune

Final output:
    ["a","a","b"]
    ["aa","b"]
```

## Complexity

Time O(n * 2^n), Space O(n) recursion path, excluding output.

## Pattern Trigger


Use this when some choices can be rejected immediately before recursion.


---


# Problem 4: Generate Valid Parentheses

**Phase:** Phase 3 — Backtracking With Additional State  
**Difficulty:** Medium  
**Pattern:** `Additional state: open count and close count`

## Problem Statement

Generate all valid parentheses strings with n pairs.

## Input

```text
n = 2
```

## Expected Output

```text
["(())", "()()"]
```

## Core Idea

At every position choose '(' if open<n. Choose ')' if close<open. This prunes invalid states early.

## LCCM Summary


```text
Level = position / path length
Choices = '(' or ')'
Constraint = open < n, close < open
Move = add bracket → update open/close → recurse → remove bracket
```


## Recursion Tree

```text
rec("", open=0, close=0)
└── add '(' -> rec("(", 1, 0)
    ├── add '(' -> rec("((", 2, 0)
    │   └── add ')' -> rec("(()", 2, 1)
    │       └── add ')' -> rec("(())", 2, 2) ✅
    └── add ')' -> rec("()", 1, 1)
        └── add '(' -> rec("()(", 2, 1)
            └── add ')' -> rec("()()", 2, 2) ✅
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<string> generateParenthesis(int n) {
    vector<string> ans;
    string path;

    function<void(int,int)> dfs = [&](int open, int close) {
        if ((int)path.size() == 2 * n) {
            ans.push_back(path);
            return;
        }

        if (open < n) {
            path.push_back('(');
            dfs(open + 1, close);
            path.pop_back();
        }

        if (close < open) {
            path.push_back(')');
            dfs(open, close + 1);
            path.pop_back();
        }
    };

    dfs(0, 0);
    return ans;
}
```

## Index-by-Index Dry Run

```text
n = 2

path="", open=0, close=0
    can add '(' because open < n
    path="("
    open=1, close=0

path="("
    can add '(' because open < n
    path="(("
    open=2, close=0

path="(("
    cannot add '(' because open == n
    can add ')' because close < open
    path="(()"
    open=2, close=1

path="(()"
    can add ')' because close < open
    path="(())"
    open=2, close=2
    length == 4
    output "(())"

backtrack to path="("

path="("
    can add ')' because close < open
    path="()"
    open=1, close=1

path="()"
    can add '(' because open < n
    path="()("
    open=2, close=1

path="()("
    can add ')' because close < open
    path="()()"
    open=2, close=2
    output "()()"

Final:
    (())
    ()()
```

## Complexity

Time O(Catalan(n) * n), Space O(n).

## Pattern Trigger


Use this when path alone is not enough; you must carry extra state like `used`, `open`, `close`, or counts.


---


# Problem 5: Permutations

**Phase:** Phase 3 — Backtracking With Additional State  
**Difficulty:** Medium  
**Pattern:** `Additional state: used boolean array`

## Problem Statement

Given distinct characters, generate all permutations.

## Input

```text
s = "abc"
```

## Expected Output

```text
abc
acb
bac
bca
cab
cba
```

## Core Idea

At each level choose any unused character. Mark it used, recurse, then unmark while backtracking.

## LCCM Summary


```text
Level = position in permutation
Choices = unused characters
Constraint = each character used once
Move = mark used → add → recurse → remove → unmark
```


## Recursion Tree

```text
rec(path="")
├── choose a
│   ├── choose b
│   │   └── choose c -> abc ✅
│   └── choose c
│       └── choose b -> acb ✅
├── choose b
│   ├── choose a
│   │   └── choose c -> bac ✅
│   └── choose c
│       └── choose a -> bca ✅
└── choose c
    ├── choose a
    │   └── choose b -> cab ✅
    └── choose b
        └── choose a -> cba ✅
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<string> permutations(string s) {
    int n = s.size();
    vector<string> ans;
    string path;
    vector<bool> used(n, false);

    function<void()> dfs = [&]() {
        if ((int)path.size() == n) {
            ans.push_back(path);
            return;
        }

        for (int i = 0; i < n; i++) {
            if (used[i]) continue;

            used[i] = true;
            path.push_back(s[i]);

            dfs();

            path.pop_back();
            used[i] = false;
        }
    };

    dfs();
    return ans;
}
```

## Index-by-Index Dry Run

```text
s = "abc"

path="", used=[false,false,false]

level=0:
    choose index 0 -> 'a'
    path="a"
    used=[true,false,false]

level=1:
    index 0 already used, skip

    choose index 1 -> 'b'
    path="ab"
    used=[true,true,false]

level=2:
    choose index 2 -> 'c'
    path="abc"
    used=[true,true,true]

path length == 3
output "abc"

backtrack:
    remove 'c'
    used[2]=false
    path="ab"

backtrack:
    remove 'b'
    used[1]=false
    path="a"

level=1:
    choose index 2 -> 'c'
    path="ac"

level=2:
    choose remaining index 1 -> 'b'
    path="acb"
    output "acb"

After finishing branch starting with 'a':
    output abc, acb

Then choose 'b' first:
    output bac, bca

Then choose 'c' first:
    output cab, cba
```

## Complexity

Time O(n! * n), Space O(n).

## Pattern Trigger


Use this when path alone is not enough; you must carry extra state like `used`, `open`, `close`, or counts.


---


# Problem 6: Word Break

**Phase:** Phase 4 — Aggregation / Return Value Backtracking  
**Difficulty:** Medium  
**Pattern:** `Aggregation OR: does any branch return true?`

## Problem Statement

Given a string and dictionary words, return true if string can be segmented into dictionary words.

## Input

```text
target = "algomonster"
words = ["algo", "monster"]
```

## Expected Output

```text
true
```

## Core Idea

At index start, try every dictionary word matching the prefix. If any recursive branch reaches the end, return true.

## LCCM Summary


```text
Level = start index in target
Choices = dictionary words matching prefix
Constraint = word must match target[start..]
Move = recurse(start + word.length), aggregate OR
```


## Recursion Tree

```text
rec(start=0, remaining="algomonster")
└── choose "algo" ✅ matches prefix
    rec(start=4, remaining="monster")
    └── choose "monster" ✅ matches prefix
        rec(start=11)
        start == target.length ✅ true
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool wordBreak(string target, vector<string>& words) {
    int n = target.size();
    vector<int> memo(n + 1, -1);

    function<bool(int)> dfs = [&](int start) -> bool {
        if (start == n) return true;
        if (memo[start] != -1) return memo[start];

        for (string& word : words) {
            int len = word.size();

            if (start + len <= n && target.substr(start, len) == word) {
                if (dfs(start + len)) {
                    return memo[start] = true;
                }
            }
        }

        return memo[start] = false;
    };

    return dfs(0);
}
```

## Index-by-Index Dry Run

```text
target = "algomonster"
words = ["algo", "monster"]

start=0
    remaining string = "algomonster"

    try word "algo"
        target.substr(0,4) = "algo"
        match found
        recurse start = 0 + 4 = 4

start=4
    remaining string = "monster"

    try word "algo"
        target.substr(4,4) = "mons"
        not match

    try word "monster"
        target.substr(4,7) = "monster"
        match found
        recurse start = 4 + 7 = 11

start=11
    start == target.length
    return true

Aggregation:
    dfs(11) returns true
    dfs(4) returns true
    dfs(0) returns true

Answer = true
```

## Complexity

Without memo can be exponential. With memo: O(n * number_of_words * word_length).

## Pattern Trigger


Use this when recursion must **return a value** instead of only printing paths: true/false, count, min, or max.


---


# Problem 7: Number of Ways to Decode a Message

**Phase:** Phase 4 — Aggregation / Return Value Backtracking  
**Difficulty:** Medium  
**Pattern:** `Aggregation SUM: number of valid branches`

## Problem Statement

Given a digit string, count how many ways it can be decoded where 1=A, 2=B, ..., 26=Z.

## Input

```text
s = "123"
```

## Expected Output

```text
3
```

## Core Idea

At each index choose one digit if valid, and choose two digits if valid between 10 and 26. Sum results from both choices.

## LCCM Summary


```text
Level = current index i
Choices = 1 digit or 2 digits
Constraint = valid number 1..26, no leading zero
Move = recurse next index, aggregate SUM
```


## Recursion Tree

```text
rec(0, "123")
├── take "1" -> rec(1, "23")
│   ├── take "2" -> rec(2, "3")
│   │   └── take "3" -> rec(3) ✅ 1 way: A B C
│   └── take "23" -> rec(3) ✅ 1 way: A W
└── take "12" -> rec(2, "3")
    └── take "3" -> rec(3) ✅ 1 way: L C

Total = 3
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int numDecodings(string s) {
    int n = s.size();
    vector<int> memo(n + 1, -1);

    function<int(int)> dfs = [&](int i) -> int {
        if (i == n) return 1;
        if (s[i] == '0') return 0;
        if (memo[i] != -1) return memo[i];

        int ways = 0;

        ways += dfs(i + 1);

        if (i + 1 < n) {
            int val = (s[i] - '0') * 10 + (s[i + 1] - '0');
            if (val >= 10 && val <= 26) {
                ways += dfs(i + 2);
            }
        }

        return memo[i] = ways;
    };

    return dfs(0);
}
```

## Index-by-Index Dry Run

```text
s = "123"

i=0, char='1'
    one digit "1" is valid
    ways += dfs(1)

    two digits "12" is valid
    ways += dfs(2)

dfs(1), remaining="23"
    one digit "2" valid
    ways += dfs(2)

    two digits "23" valid
    ways += dfs(3)

dfs(2), remaining="3"
    one digit "3" valid
    ways += dfs(3)

    no two-digit choice

dfs(3)
    i == n
    return 1

Now aggregate:
    dfs(2) = 1
        represents "3" -> C

    dfs(1) = dfs(2) + dfs(3)
           = 1 + 1
           = 2
        represents "2","3" -> B C
        and "23" -> W

    dfs(0) = dfs(1) + dfs(2)
           = 2 + 1
           = 3

Answer = 3
Decodings:
    1 2 3 -> A B C
    1 23  -> A W
    12 3  -> L C
```

## Complexity

With memo O(n), Space O(n).

## Pattern Trigger


Use this when recursion must **return a value** instead of only printing paths: true/false, count, min, or max.


---


# Problem 8: Coin Change Minimum Coins

**Phase:** Phase 4 — Aggregation / Return Value Backtracking  
**Difficulty:** Medium  
**Pattern:** `Aggregation MIN: take or skip coin`

## Problem Statement

Given coin denominations and amount, find minimum number of coins needed. Coins can be used unlimited times.

## Input

```text
coins = [1, 2, 5]
amount = 11
```

## Expected Output

```text
3  // 5 + 5 + 1
```

## Core Idea

At each coin index, either take current coin and stay at same index, or skip it and move to next index. Aggregate with min.

## LCCM Summary


```text
Level = coin index
Choices = take coin or skip coin
Constraint = remaining sum must not go below zero
Move = take → same index, skip → next index, aggregate MIN
```


## Recursion Tree

```text
rec(index=0, sum=11)
├── take coin 1 -> rec(0, 10)
│   └── ...
└── skip coin 1 -> rec(1, 11)
    ├── take coin 2 -> rec(1, 9)
    └── skip coin 2 -> rec(2, 11)
        ├── take coin 5 -> rec(2, 6)
        │   ├── take coin 5 -> rec(2, 1)
        │   └── ...
        └── skip coin 5 -> invalid
Valid best path:
    take 5 -> take 5 -> take 1 = 3 coins
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int coinChange(vector<int>& coins, int amount) {
    const int INF = 1e9;
    int n = coins.size();

    vector<vector<int>> memo(n + 1, vector<int>(amount + 1, -1));

    function<int(int,int)> dfs = [&](int idx, int rem) -> int {
        if (rem == 0) return 0;
        if (idx == n) return INF;
        if (rem < 0) return INF;

        if (memo[idx][rem] != -1) return memo[idx][rem];

        int take = INF;
        if (rem >= coins[idx]) {
            take = 1 + dfs(idx, rem - coins[idx]);
        }

        int skip = dfs(idx + 1, rem);

        return memo[idx][rem] = min(take, skip);
    };

    int ans = dfs(0, amount);
    return ans >= INF ? -1 : ans;
}
```

## Index-by-Index Dry Run

```text
coins = [1, 2, 5]
amount = 11

Goal:
    minimum coins to make 11

rec(index=0, rem=11), coin=1
    Choice 1: take coin 1
        rem becomes 10
        coins used +1
        stay index=0 because unlimited use

    Choice 2: skip coin 1
        move index=1
        rem still 11

Important valid path:
    skip coin 1 initially
    skip coin 2 initially
    take coin 5

State:
    rec(index=2, rem=11), coin=5
        take 5
        rem=6
        coins=1

    rec(index=2, rem=6)
        take 5
        rem=1
        coins=2

    rec(index=2, rem=1)
        cannot take 5
        skip coin 5
        invalid at end

So pure 5s cannot finish.

Another valid path:
    take 5
    take 5
    then use coin 1

Path:
    11 -> 6 by taking 5
    6  -> 1 by taking 5
    1  -> 0 by taking 1

Total coins = 3

Aggregation:
    return minimum among all valid branches

Answer = 3
```

## Complexity

With memo O(n * amount), Space O(n * amount).

## Pattern Trigger


Use this when recursion must **return a value** instead of only printing paths: true/false, count, min, or max.


---


# Problem 9: Three Sum Without Duplicate Triplets

**Phase:** Phase 5 — Deduplication Patterns  
**Difficulty:** Medium  
**Pattern:** `Sort + fixed i + two pointers + skip duplicates`

## Problem Statement

Given nums, return unique triplets [a,b,c] such that a+b+c=0.

## Input

```text
nums = [-1, 0, 1, 2, -1, -4]
```

## Expected Output

```text
[[-1,-1,2], [-1,0,1]]
```

## Core Idea

Sort. Fix i. Then run two-sum using left/right. Skip duplicate i, duplicate left, and duplicate right.

## LCCM Summary


```text
Level = fixed index i
Choices = left/right movement
Constraint = skip duplicate values
Move = if sum too small left++, if too large right--, if equal record and skip duplicates
```


## Recursion Tree

```text
Sorted nums = [-4, -1, -1, 0, 1, 2]

i = 0 -> -4
    twoSum target = 4 -> no pair

i = 1 -> -1
    twoSum target = 1
    find (-1, 2) -> [-1, -1, 2]
    find (0, 1)  -> [-1, 0, 1]

i = 2 -> -1 duplicate of previous i -> skip
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<vector<int>> threeSum(vector<int>& nums) {
    sort(nums.begin(), nums.end());
    int n = nums.size();

    vector<vector<int>> res;

    for (int i = 0; i < n; i++) {
        if (i > 0 && nums[i] == nums[i - 1]) continue;

        int left = i + 1;
        int right = n - 1;

        while (left < right) {
            int sum = nums[i] + nums[left] + nums[right];

            if (sum == 0) {
                res.push_back({nums[i], nums[left], nums[right]});

                left++;
                right--;

                while (left < right && nums[left] == nums[left - 1]) left++;
                while (left < right && nums[right] == nums[right + 1]) right--;
            } else if (sum < 0) {
                left++;
            } else {
                right--;
            }
        }
    }

    return res;
}
```

## Index-by-Index Dry Run

```text
nums = [-1, 0, 1, 2, -1, -4]

Sort:
    [-4, -1, -1, 0, 1, 2]

i=0, nums[i]=-4
    left=1 (-1), right=5 (2)
    sum = -4 + -1 + 2 = -3
    sum < 0, move left++

    left=2 (-1), right=5 (2)
    sum=-3
    move left++

    left=3 (0), right=5 (2)
    sum=-2
    move left++

    left=4 (1), right=5 (2)
    sum=-1
    move left++

    stop, no triplet for i=0

i=1, nums[i]=-1
    left=2 (-1), right=5 (2)
    sum = -1 + -1 + 2 = 0
    output [-1,-1,2]

    move left++, right--
    left=3 (0), right=4 (1)

    sum = -1 + 0 + 1 = 0
    output [-1,0,1]

    move left++, right--
    stop

i=2, nums[i]=-1
    nums[i] == nums[i-1]
    duplicate fixed value
    skip

Final:
    [-1,-1,2]
    [-1,0,1]
```

## Complexity

Time O(n²), Space O(1) excluding output.

## Pattern Trigger


Use this when sorted input has duplicates and output must avoid duplicate combinations/triplets.


---


# Problem 10: Combination Sum

**Phase:** Phase 6 — Combination Style Backtracking  
**Difficulty:** Medium  
**Pattern:** `Index based take/skip, unlimited reuse`

## Problem Statement

Given candidates and target, return all unique combinations where candidates sum to target. Same number can be reused unlimited times.

## Input

```text
candidates = [2, 3, 6, 7]
target = 7
```

## Expected Output

```text
[[2,2,3], [7]]
```

## Core Idea

At index i, either take candidates[i] and stay at i, or skip it and move to i+1.

## LCCM Summary


```text
Level = candidate index
Choices = take current candidate or skip it
Constraint = remaining target >= 0
Move = take → same index, skip → index+1
```


## Recursion Tree

```text
rec(i=0, rem=7, path=[])
├── take 2 -> rec(0, rem=5, [2])
│   ├── take 2 -> rec(0, rem=3, [2,2])
│   │   ├── take 2 -> rec(0, rem=1, [2,2,2])
│   │   │   └── take 2 -> rem=-1 ❌
│   │   └── skip 2 -> rec(1, rem=3, [2,2])
│   │       └── take 3 -> rec(1, rem=0, [2,2,3]) ✅
│   └── ...
└── skip 2 -> rec(1, rem=7, [])
    └── skip 3 -> skip 6 -> take 7 -> [7] ✅
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<vector<int>> combinationSum(vector<int>& candidates, int target) {
    vector<vector<int>> ans;
    vector<int> path;
    int n = candidates.size();

    function<void(int,int)> dfs = [&](int idx, int rem) {
        if (rem == 0) {
            ans.push_back(path);
            return;
        }

        if (idx == n || rem < 0) return;

        path.push_back(candidates[idx]);
        dfs(idx, rem - candidates[idx]);
        path.pop_back();

        dfs(idx + 1, rem);
    };

    dfs(0, target);
    return ans;
}
```

## Index-by-Index Dry Run

```text
candidates = [2, 3, 6, 7]
target = 7

rec(idx=0, rem=7, path=[])

Take 2:
    path=[2]
    rem=5
    stay idx=0 because 2 can be reused

Take 2 again:
    path=[2,2]
    rem=3
    stay idx=0

Try taking 2 again:
    path=[2,2,2]
    rem=1

Try taking 2 again:
    rem=-1
    invalid
    backtrack

Skip 2:
    idx=1, rem=3, path=[2,2]

Take 3:
    path=[2,2,3]
    rem=0
    valid combination found
    output [2,2,3]

Backtrack to root and skip 2:
    idx=1, rem=7, path=[]

Try 3 branches:
    3 + 3 = 6, remaining 1 -> cannot finish
    skip 3

Try 6:
    remaining 1 -> cannot finish
    skip 6

Try 7:
    path=[7]
    rem=0
    output [7]

Final:
    [2,2,3]
    [7]
```

## Complexity

Exponential in target/min_candidate, Space O(target/min_candidate).

## Pattern Trigger


Use this when the problem asks to generate all combinations/strings/subsets by trying choices recursively.


---


# Problem 11: Subsets

**Phase:** Phase 6 — Combination Style Backtracking  
**Difficulty:** Easy  
**Pattern:** `Include / exclude at every index`

## Problem Statement

Given distinct numbers, return all subsets.

## Input

```text
nums = [1, 2, 3]
```

## Expected Output

```text
[[], [1], [2], [3], [1,2], [1,3], [2,3], [1,2,3]]
```

## Core Idea

At every index, choose to include nums[i] or exclude nums[i].

## LCCM Summary


```text
Level = index in nums
Choices = include nums[i] or exclude nums[i]
Constraint = none
Move = include → recurse → remove → exclude
```


## Recursion Tree

```text
rec(i=0, path=[])
├── include 1 -> rec(1, [1])
│   ├── include 2 -> rec(2, [1,2])
│   │   ├── include 3 -> [1,2,3] ✅
│   │   └── exclude 3 -> [1,2] ✅
│   └── exclude 2 -> rec(2, [1])
│       ├── include 3 -> [1,3] ✅
│       └── exclude 3 -> [1] ✅
└── exclude 1 -> rec(1, [])
    ├── include 2 -> rec(2, [2])
    │   ├── include 3 -> [2,3] ✅
    │   └── exclude 3 -> [2] ✅
    └── exclude 2 -> rec(2, [])
        ├── include 3 -> [3] ✅
        └── exclude 3 -> [] ✅
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<vector<int>> subsets(vector<int>& nums) {
    vector<vector<int>> ans;
    vector<int> path;
    int n = nums.size();

    function<void(int)> dfs = [&](int idx) {
        if (idx == n) {
            ans.push_back(path);
            return;
        }

        path.push_back(nums[idx]);
        dfs(idx + 1);
        path.pop_back();

        dfs(idx + 1);
    };

    dfs(0);
    return ans;
}
```

## Index-by-Index Dry Run

```text
nums = [1, 2, 3]

idx=0, path=[]
    include 1
    path=[1]
    idx=1

idx=1, path=[1]
    include 2
    path=[1,2]
    idx=2

idx=2, path=[1,2]
    include 3
    path=[1,2,3]
    idx=3
    output [1,2,3]

    backtrack remove 3
    path=[1,2]

    exclude 3
    idx=3
    output [1,2]

backtrack remove 2
path=[1]

idx=1, path=[1]
    exclude 2
    idx=2

idx=2, path=[1]
    include 3 -> output [1,3]
    exclude 3 -> output [1]

backtrack remove 1
path=[]

idx=0
    exclude 1

Now solve remaining [2,3]:
    include 2, include 3 -> [2,3]
    include 2, exclude 3 -> [2]
    exclude 2, include 3 -> [3]
    exclude 2, exclude 3 -> []

Final subsets:
    [1,2,3], [1,2], [1,3], [1], [2,3], [2], [3], []
```

## Complexity

Time O(n * 2^n), Space O(n) recursion path, excluding output.

## Pattern Trigger


Use this when the problem asks to generate all combinations/strings/subsets by trying choices recursively.


---


# Final Backtracking Revision Sheet

## Universal Backtracking Checklist

```text
1. What is LEVEL?
2. What are CHOICES at this level?
3. What CONSTRAINT rejects bad choices?
4. What is MOVE?
       add
       recurse
       remove
5. Is this:
       all results?
       true/false?
       count ways?
       min/max?
6. Do I need extra state?
       used[]
       open/close
       remaining sum
       start index
7. Do I need deduplication?
       sort
       skip same value
```

---

# Fast Pattern Recognition

| Problem Shape | Pattern |
|---|---|
| Generate all strings | Basic combinatorial search |
| Phone digits | Choices depend on digit |
| Palindrome cuts | Backtracking with pruning |
| Parentheses | Additional state open/close |
| Permutations | used[] state |
| Word break | OR aggregation |
| Decode ways | SUM aggregation |
| Coin change min | MIN aggregation |
| 3Sum | Sort + two pointers + dedup |
| Combination sum | Take/skip with reuse |
| Subsets | Include/exclude |

---

# Interview One-Liners

```text
Backtracking:
At each level, try every valid choice, recurse, then undo the choice.

Pruning:
Do not recurse into branches that can never lead to a valid answer.

Additional state:
When path alone is not enough, carry extra variables like used[], open, close, or remaining sum.

Aggregation:
If recursion returns true/count/min/max, combine child answers using OR, SUM, MIN, or MAX.

Deduplication:
Sort first, then skip repeated values at the same decision level.
```

---

🔥 End of handbook.
