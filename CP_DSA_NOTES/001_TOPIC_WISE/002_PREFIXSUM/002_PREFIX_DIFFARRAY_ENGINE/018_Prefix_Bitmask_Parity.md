# 018_Prefix_Bitmask_Parity.md — MiniPrefixSumDifferenceEngine

# Prefix Bitmask Parity

> Prefix Bitmask Parity is a powerful extension of Prefix XOR.
>
> It tracks whether each feature/character/count is:
>
> ```text
> even or odd
> ```
>
> using bits.
>
> Core idea:
>
> ```text
> bit = 0 means even count
> bit = 1 means odd count
> ```

---

## Clickable Index

1. [What Is Prefix Bitmask Parity?](#1-what-is-prefix-bitmask-parity)
2. [Why This Topic Matters](#2-why-this-topic-matters)
3. [Core Mental Model](#3-core-mental-model)
4. [Bitmask Parity Formula](#4-bitmask-parity-formula)
5. [Why XOR Toggles Parity](#5-why-xor-toggles-parity)
6. [Palindrome-Anagram Rule](#6-palindrome-anagram-rule)
7. [Same Mask Rule](#7-same-mask-rule)
8. [One-Bit-Different Rule](#8-one-bit-different-rule)
9. [Step-by-Step Dry Run — Build Prefix Mask](#9-step-by-step-dry-run--build-prefix-mask)
10. [Step-by-Step Dry Run — Wonderful Substrings](#10-step-by-step-dry-run--wonderful-substrings)
11. [Problem Form 1 — Count Wonderful Substrings](#11-problem-form-1--count-wonderful-substrings)
12. [Problem Form 2 — Longest Awesome Substring](#12-problem-form-2--longest-awesome-substring)
13. [Problem Form 3 — Pseudo-Palindromic Paths](#13-problem-form-3--pseudo-palindromic-paths)
14. [Problem Form 4 — Count Even-Vowel Substrings](#14-problem-form-4--count-even-vowel-substrings)
15. [Problem Form 5 — State Compression With Toggles](#15-problem-form-5--state-compression-with-toggles)
16. [Real World Model 1 — Feature Toggle Parity](#16-real-world-model-1--feature-toggle-parity)
17. [Real World Model 2 — Permission Change Audit](#17-real-world-model-2--permission-change-audit)
18. [Real World Model 3 — Event Parity Validation](#18-real-world-model-3--event-parity-validation)
19. [Real World Model 4 — Session State Compression](#19-real-world-model-4--session-state-compression)
20. [Decision Tree](#20-decision-tree)
21. [Common Mistakes](#21-common-mistakes)
22. [Complexity](#22-complexity)
23. [Reusable C++ Templates](#23-reusable-c-templates)
24. [CP / FAANG Problem Forms](#24-cp--faang-problem-forms)
25. [Practice Checklist](#25-practice-checklist)
26. [Next Step](#26-next-step)

---

## 1. What Is Prefix Bitmask Parity?

Prefix Bitmask Parity stores odd/even count information inside an integer mask.

Example with characters:

```text
a b c
```

Use bits:

```text
a -> bit 0
b -> bit 1
c -> bit 2
```

If a character appears odd number of times:

```text
bit = 1
```

If even number of times:

```text
bit = 0
```

Example substring:

```text
"aab"
```

Counts:

```text
a = 2 even
b = 1 odd
```

Mask:

```text
010
```

Only `b` is odd.

---

## 2. Why This Topic Matters

This pattern appears in:

```text
wonderful substrings
longest awesome substring
pseudo-palindromic paths
even-vowel substring
palindrome permutation substring
state compression
toggle parity
bitmask DP
```

It is very useful because many problems ask:

```text
Can this substring/path be rearranged into a palindrome?
```

That depends only on:

```text
odd/even frequency
```

not actual counts.

---

## 3. Core Mental Model

Instead of storing full frequency:

```text
count[a], count[b], count[c]...
```

we store only parity:

```text
count[ch] % 2
```

This becomes a bitmask.

When a character appears:

```text
toggle its bit
```

Using XOR:

```cpp
mask ^= (1 << bit);
```

---

## 4. Bitmask Parity Formula

For each character:

```cpp
int bit = ch - 'a';
mask ^= (1 << bit);
```

Meaning:

```text
if bit was 0 -> becomes 1
if bit was 1 -> becomes 0
```

This tracks odd/even occurrence.

---

## 5. Why XOR Toggles Parity

XOR with `1` flips a bit:

```text
0 ^ 1 = 1
1 ^ 1 = 0
```

So:

```cpp
mask ^= (1 << bit);
```

toggles that character's parity.

Example:

```text
a appears first time  -> odd  -> bit 1
a appears second time -> even -> bit 0
a appears third time  -> odd  -> bit 1
```

---

## 6. Palindrome-Anagram Rule

A string can be rearranged into a palindrome if:

```text
at most one character has odd count
```

Examples:

```text
"aabb"  -> yes, all even
"aabbc" -> yes, only c odd
"aabc"  -> no, a and b? actually a=2, b=1, c=1 -> two odds
```

In bitmask terms:

```text
mask has at most one set bit
```

Check:

```cpp
mask == 0 || (mask & (mask - 1)) == 0
```

---

## 7. Same Mask Rule

If two prefix masks are the same:

```text
prefixMask[i] == prefixMask[j]
```

then substring between them has:

```text
all even counts
```

because same parities cancel.

This is like zero XOR subarray.

---

## 8. One-Bit-Different Rule

If two prefix masks differ by exactly one bit:

```text
prefixMask[i] ^ prefixMask[j] has exactly one set bit
```

then substring between them has:

```text
exactly one odd character
```

So it can be rearranged into a palindrome.

For each current mask:

```text
same mask -> all even
mask ^ (1 << b) -> one odd bit
```

---

## 9. Step-by-Step Dry Run — Build Prefix Mask

String:

```text
s = "aba"
```

Bits:

```text
a -> bit 0
b -> bit 1
```

Initial:

```text
mask = 0
```

### char = a

```text
mask ^= 1 << 0
mask = 001
```

### char = b

```text
mask ^= 1 << 1
mask = 011
```

### char = a

```text
mask ^= 1 << 0
mask = 010
```

Final prefix masks:

```text
0, 1, 3, 2
```

Substring `"aba"` has mask:

```text
2
```

Only `b` is odd, so it can form palindrome:

```text
"aba"
```

---

## 10. Step-by-Step Dry Run — Wonderful Substrings

Wonderful substring rule:

```text
at most one character has odd count
```

Usually characters are:

```text
'a' to 'j'
```

so only 10 bits.

Input:

```text
word = "aba"
```

Initialize:

```text
freq[0] = 1
mask = 0
answer = 0
```

### i = 0, char = a

```text
mask = 1
same mask count = freq[1] = 0
one-bit flip:
mask ^ 1 = 0 -> freq[0] = 1
answer = 1
freq[1]++
```

Substring counted:

```text
"a"
```

### i = 1, char = b

```text
mask = 3
same = freq[3] = 0
one-bit flips:
3^1 = 2 -> 0
3^2 = 1 -> freq[1] = 1
answer = 2
freq[3]++
```

Substring counted:

```text
"b"
```

### i = 2, char = a

```text
mask = 2
same = freq[2] = 0
one-bit flips:
2^1 = 3 -> freq[3] = 1
2^2 = 0 -> freq[0] = 1
answer += 2
```

Total:

```text
4
```

Wonderful substrings:

```text
"a", "b", "a", "aba"
```

---

## 11. Problem Form 1 — Count Wonderful Substrings

### Problem

A wonderful substring has at most one character with odd frequency.

Given a word containing only characters:

```text
'a' to 'j'
```

count wonderful substrings.

Input:

```text
word = "aba"
```

Output:

```text
4
```

---

### Pattern Recognition

Use when:

```text
substring
odd/even character counts
at most one odd count
small alphabet
```

Pattern:

```text
Prefix Bitmask Parity + Frequency
```

---

### Step-by-Step Working

At each position:

```text
toggle current character bit
```

Count:

```text
1. previous same mask      -> all even
2. previous mask diff by 1 -> exactly one odd
```

So:

```text
answer += freq[mask]
for bit in 0..9:
    answer += freq[mask ^ (1 << bit)]
freq[mask]++
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long wonderfulSubstrings(string word) {
    vector<long long> freq(1 << 10, 0);

    // Empty prefix mask.
    freq[0] = 1;

    int mask = 0;
    long long answer = 0;

    for (char ch : word) {
        int bit = ch - 'a';

        // Toggle parity of this character.
        mask ^= (1 << bit);

        // Case 1: same mask -> all even counts.
        answer += freq[mask];

        // Case 2: masks differ by one bit -> one odd count.
        for (int b = 0; b < 10; b++) {
            int previousMask = mask ^ (1 << b);
            answer += freq[previousMask];
        }

        freq[mask]++;
    }

    return answer;
}

int main() {
    string word = "aba";

    cout << wonderfulSubstrings(word) << "\n";

    return 0;
}
```

---

### Complexity

```text
Time  : O(10 * N)
Space : O(2^10)
```

---

## 12. Problem Form 2 — Longest Awesome Substring

### Problem

Find longest substring of digits that can be rearranged into a palindrome.

Digit characters:

```text
'0' to '9'
```

A substring is awesome if:

```text
at most one digit has odd count
```

---

### Pattern Recognition

Use:

```text
Prefix Bitmask Parity + First Index
```

For longest:

```text
store earliest index of each mask
```

---

### Step-by-Step Working

At each index:

```text
toggle digit bit
```

Check:

```text
same mask -> all even
mask ^ (1 << b) -> one odd
```

Length:

```text
i - firstIndex[previousMask]
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int longestAwesome(string s) {
    unordered_map<int, int> firstIndex;

    // Empty prefix mask before string starts.
    firstIndex[0] = -1;

    int mask = 0;
    int best = 0;

    for (int i = 0; i < s.size(); i++) {
        int bit = s[i] - '0';

        mask ^= (1 << bit);

        // Same mask: all digit counts even.
        if (firstIndex.count(mask)) {
            best = max(best, i - firstIndex[mask]);
        } else {
            firstIndex[mask] = i;
        }

        // One odd digit allowed.
        for (int b = 0; b < 10; b++) {
            int need = mask ^ (1 << b);

            if (firstIndex.count(need)) {
                best = max(best, i - firstIndex[need]);
            }
        }
    }

    return best;
}

int main() {
    string s = "3242415";

    cout << longestAwesome(s) << "\n";

    return 0;
}
```

---

### Complexity

```text
Time  : O(10 * N)
Space : O(2^10) or O(N)
```

---

## 13. Problem Form 3 — Pseudo-Palindromic Paths

### Problem

Given a binary tree where each node has digit `1..9`, count root-to-leaf paths that can be rearranged into a palindrome.

A path is pseudo-palindromic if:

```text
at most one digit appears odd number of times
```

---

### Pattern Recognition

Use:

```text
DFS + Bitmask Parity
```

At each node:

```text
mask ^= (1 << value)
```

At leaf:

```text
check mask has at most one set bit
```

---

### Step-by-Step Working

Path:

```text
2 -> 3 -> 3
```

Counts:

```text
2 appears once
3 appears twice
```

Mask has only digit `2` odd.

So path is valid.

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct TreeNode {
    int val;
    TreeNode* left;
    TreeNode* right;

    TreeNode(int x) : val(x), left(nullptr), right(nullptr) {}
};

class Solution {
public:
    int dfs(TreeNode* node, int mask) {
        if (!node) return 0;

        // Toggle parity of current digit.
        mask ^= (1 << node->val);

        // Leaf node.
        if (!node->left && !node->right) {
            // Valid if at most one bit is set.
            if ((mask & (mask - 1)) == 0) {
                return 1;
            }
            return 0;
        }

        return dfs(node->left, mask) + dfs(node->right, mask);
    }

    int pseudoPalindromicPaths(TreeNode* root) {
        return dfs(root, 0);
    }
};

int main() {
    TreeNode* root = new TreeNode(2);
    root->left = new TreeNode(3);
    root->right = new TreeNode(1);
    root->left->left = new TreeNode(3);
    root->left->right = new TreeNode(1);
    root->right->right = new TreeNode(1);

    Solution sol;

    cout << sol.pseudoPalindromicPaths(root) << "\n";

    return 0;
}
```

---

## 14. Problem Form 4 — Count Even-Vowel Substrings

### Problem

Given a string, find/count substrings where every vowel appears even number of times.

Vowels:

```text
a e i o u
```

---

### Pattern Recognition

Track only 5 vowel parity bits.

Consonants do not affect mask.

Same prefix mask means substring has all vowel counts even.

---

### Step-by-Step Working

String:

```text
"eleetminicoworoep"
```

Track mask for vowels only.

When same mask repeats:

```text
substring between them has even counts of all vowels
```

---

### Commented C++ Code — Count Even-Vowel Substrings

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countEvenVowelSubstrings(string s) {
    unordered_map<int, long long> freq;

    freq[0] = 1;

    unordered_map<char, int> bit = {
        {'a', 0},
        {'e', 1},
        {'i', 2},
        {'o', 3},
        {'u', 4}
    };

    int mask = 0;
    long long answer = 0;

    for (char ch : s) {
        if (bit.count(ch)) {
            mask ^= (1 << bit[ch]);
        }

        answer += freq[mask];

        freq[mask]++;
    }

    return answer;
}

int main() {
    string s = "eleetminicoworoep";

    cout << countEvenVowelSubstrings(s) << "\n";

    return 0;
}
```

---

### Commented C++ Code — Longest Even-Vowel Substring

```cpp
#include <bits/stdc++.h>
using namespace std;

int longestEvenVowelSubstring(string s) {
    unordered_map<int, int> firstIndex;

    firstIndex[0] = -1;

    unordered_map<char, int> bit = {
        {'a', 0},
        {'e', 1},
        {'i', 2},
        {'o', 3},
        {'u', 4}
    };

    int mask = 0;
    int best = 0;

    for (int i = 0; i < s.size(); i++) {
        if (bit.count(s[i])) {
            mask ^= (1 << bit[s[i]]);
        }

        if (firstIndex.count(mask)) {
            best = max(best, i - firstIndex[mask]);
        } else {
            firstIndex[mask] = i;
        }
    }

    return best;
}
```

---

## 15. Problem Form 5 — State Compression With Toggles

### Problem

You have multiple boolean states.

Each event toggles one state.

Need to count or find windows where final state satisfies a condition.

---

### Pattern Recognition

Boolean toggle state is naturally represented by:

```text
bitmask
```

Each event:

```text
mask ^= (1 << state)
```

---

### Problem Simulation

States:

```text
A, B, C
```

Events:

```text
A, B, A, C
```

Mask evolution:

```text
000
001
011
010
110
```

Final active states:

```text
B and C
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> events = {0, 1, 0, 2};

    int mask = 0;

    for (int state : events) {
        mask ^= (1 << state);
    }

    cout << mask << "\n";

    return 0;
}
```

---

## 16. Real World Model 1 — Feature Toggle Parity

### Scenario

A feature flag system stores toggle events:

```text
enable/disable feature A
enable/disable feature B
enable/disable feature C
```

Each toggle flips the feature state.

---

### Problem Simulation

Events:

```text
A, B, A, C
```

State evolution:

```text
000
001
011
010
110
```

Final enabled:

```text
B and C
```

---

### System Mapping

Used in:

```text
feature rollout auditing
configuration replay
state transition debugging
permission toggling
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

int replayFeatureToggles(vector<int>& events) {
    int mask = 0;

    for (int feature : events) {
        mask ^= (1 << feature);
    }

    return mask;
}
```

---

## 17. Real World Model 2 — Permission Change Audit

### Scenario

A permission system records changes:

```text
grant permission X
revoke permission X
```

If a permission changes twice, it returns to original state.

---

### Problem Simulation

Permissions:

```text
READ = bit 0
WRITE = bit 1
ADMIN = bit 2
```

Events:

```text
READ, WRITE, READ
```

Final mask:

```text
WRITE only
```

---

### System Mapping

Used for:

```text
permission audit replay
access control state compression
security event analysis
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

int auditPermissionChanges(vector<int>& changes) {
    int mask = 0;

    for (int perm : changes) {
        mask ^= (1 << perm);
    }

    return mask;
}

int main() {
    vector<int> changes = {0, 1, 0};

    cout << auditPermissionChanges(changes) << "\n";

    return 0;
}
```

---

## 18. Real World Model 3 — Event Parity Validation

### Scenario

Some events must occur in pairs.

Example:

```text
open/close
lock/unlock
start/stop
login/logout
```

If all events are paired, parity mask becomes:

```text
0
```

---

### Problem Simulation

Events:

```text
open, close, lock, unlock
```

Mask:

```text
0
```

Valid.

Events:

```text
open, lock, unlock
```

Mask:

```text
open remains odd
```

Invalid.

---

### System Mapping

Used for:

```text
audit log validation
workflow consistency
resource lifecycle checking
session state validation
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

bool allEventsPaired(vector<int>& events) {
    int mask = 0;

    for (int e : events) {
        mask ^= (1 << e);
    }

    return mask == 0;
}
```

---

## 19. Real World Model 4 — Session State Compression

### Scenario

A user session has multiple binary states:

```text
logged in
cart active
payment started
coupon applied
support chat open
```

Events toggle these states.

A bitmask compactly tracks current session parity.

---

### Problem Simulation

Events:

```text
login
cart
coupon
coupon
payment
```

Coupon toggled twice, so it cancels.

Final active states:

```text
login, cart, payment
```

---

### System Mapping

Used for:

```text
session replay
state compression
behavior analytics
debugging toggle-heavy flows
```

---

### Commented C++ Model

```cpp
#include <bits/stdc++.h>
using namespace std;

int sessionState(vector<int>& events) {
    int mask = 0;

    for (int e : events) {
        mask ^= (1 << e);
    }

    return mask;
}
```

---

## 20. Decision Tree

```text
Substring/path problem?
|
+-- Need odd/even counts?
|   |
|   +-- Use bitmask parity
|
+-- At most one odd count?
|   |
|   +-- same mask + one-bit-different masks
|
+-- All even counts?
|   |
|   +-- same mask only
|
+-- Longest substring?
|   |
|   +-- earliest index of mask
|
+-- Count substrings?
    |
    +-- frequency of mask
```

---

## 21. Common Mistakes

### Mistake 1 — Storing Full Counts

For parity problems, full counts are unnecessary.

Use:

```text
count % 2
```

---

### Mistake 2 — Forgetting Empty Mask

For counting:

```cpp
freq[0] = 1;
```

For longest:

```cpp
firstIndex[0] = -1;
```

---

### Mistake 3 — Checking Only Same Mask For Palindrome-Anagram

At most one odd count means:

```text
same mask OR one-bit-different mask
```

---

### Mistake 4 — Too Many Bits

Bitmask works when alphabet/state count is small enough.

Example:

```text
10 digits -> OK
26 lowercase letters -> 2^26 too large for frequency vector, but unordered_map may work depending on problem
```

---

### Mistake 5 — Confusing Prefix XOR With Bitmask Parity

Bitmask parity uses XOR to toggle bits, but each bit represents odd/even count.

---

## 22. Complexity

For alphabet size `B`:

Wonderful substring:

```text
Time  : O(B * N)
Space : O(2^B)
```

For longest same-mask only:

```text
Time  : O(N)
Space : O(2^B) or O(N)
```

---

## 23. Reusable C++ Templates

### Template 1 — Toggle Character Bit

```cpp
int toggleChar(int mask, char ch) {
    int bit = ch - 'a';
    return mask ^ (1 << bit);
}
```

---

### Template 2 — At Most One Bit Set

```cpp
bool atMostOneBitSet(int mask) {
    return (mask & (mask - 1)) == 0;
}
```

---

### Template 3 — Count Wonderful Style Substrings

```cpp
long long countAtMostOneOdd(string s, int B) {
    unordered_map<int, long long> freq;

    freq[0] = 1;

    int mask = 0;
    long long ans = 0;

    for (char ch : s) {
        int bit = ch - 'a';

        mask ^= (1 << bit);

        ans += freq[mask];

        for (int b = 0; b < B; b++) {
            ans += freq[mask ^ (1 << b)];
        }

        freq[mask]++;
    }

    return ans;
}
```

---

### Template 4 — Longest All-Even State

```cpp
int longestAllEvenState(string s, unordered_map<char,int>& bit) {
    unordered_map<int, int> firstIndex;

    firstIndex[0] = -1;

    int mask = 0;
    int best = 0;

    for (int i = 0; i < s.size(); i++) {
        if (bit.count(s[i])) {
            mask ^= (1 << bit[s[i]]);
        }

        if (firstIndex.count(mask)) {
            best = max(best, i - firstIndex[mask]);
        } else {
            firstIndex[mask] = i;
        }
    }

    return best;
}
```

---

## 24. CP / FAANG Problem Forms

---

### Problem 1 — Wonderful Substrings

#### Recognition

```text
at most one character has odd count
characters limited to a-j
count substrings
```

#### Pattern

```text
prefix bitmask parity + freq
```

#### Step-by-Step Working

```text
toggle current char bit
answer += freq[same mask]
for each bit:
    answer += freq[mask ^ (1 << bit)]
freq[mask]++
```

#### Commented C++ Code

```cpp
long long wonderfulSubstrings(string word) {
    vector<long long> freq(1 << 10, 0);
    freq[0] = 1;

    int mask = 0;
    long long ans = 0;

    for (char ch : word) {
        int bit = ch - 'a';

        mask ^= (1 << bit);

        ans += freq[mask];

        for (int b = 0; b < 10; b++) {
            ans += freq[mask ^ (1 << b)];
        }

        freq[mask]++;
    }

    return ans;
}
```

---

### Problem 2 — Longest Awesome Substring

#### Recognition

```text
digits string
can rearrange into palindrome
longest substring
```

#### Pattern

```text
prefix bitmask + earliest index
```

#### Commented C++ Code

```cpp
int longestAwesome(string s) {
    unordered_map<int, int> first;
    first[0] = -1;

    int mask = 0;
    int best = 0;

    for (int i = 0; i < s.size(); i++) {
        mask ^= (1 << (s[i] - '0'));

        if (first.count(mask)) {
            best = max(best, i - first[mask]);
        } else {
            first[mask] = i;
        }

        for (int b = 0; b < 10; b++) {
            int need = mask ^ (1 << b);

            if (first.count(need)) {
                best = max(best, i - first[need]);
            }
        }
    }

    return best;
}
```

---

### Problem 3 — Even Vowels

#### Recognition

```text
every vowel appears even number of times
```

#### Pattern

```text
5-bit prefix mask
same mask
```

#### Commented C++ Code

```cpp
int findTheLongestSubstring(string s) {
    unordered_map<int, int> first;
    first[0] = -1;

    unordered_map<char, int> bit = {
        {'a', 0}, {'e', 1}, {'i', 2}, {'o', 3}, {'u', 4}
    };

    int mask = 0;
    int best = 0;

    for (int i = 0; i < s.size(); i++) {
        if (bit.count(s[i])) {
            mask ^= (1 << bit[s[i]]);
        }

        if (first.count(mask)) {
            best = max(best, i - first[mask]);
        } else {
            first[mask] = i;
        }
    }

    return best;
}
```

---

### Problem 4 — Pseudo-Palindromic Paths

#### Recognition

```text
root-to-leaf path
at most one digit odd
```

#### Pattern

```text
DFS + bitmask parity
```

#### Commented C++ Code

```cpp
int dfs(TreeNode* node, int mask) {
    if (!node) return 0;

    mask ^= (1 << node->val);

    if (!node->left && !node->right) {
        return (mask & (mask - 1)) == 0;
    }

    return dfs(node->left, mask) + dfs(node->right, mask);
}
```

---

### Problem 5 — Toggle State Window

#### Recognition

```text
events toggle states
need parity condition
```

#### Pattern

```text
bitmask state compression
```

#### Commented C++ Code

```cpp
int replayToggles(vector<int>& events) {
    int mask = 0;

    for (int e : events) {
        mask ^= (1 << e);
    }

    return mask;
}
```

---

## 25. Practice Checklist

Before solving:

```text
1. Does the problem care about odd/even counts?
2. Can counts be reduced to parity?
3. Is alphabet/state count small?
4. Can I represent state as bitmask?
5. Do I need count or longest?
6. Count -> frequency map
7. Longest -> earliest index
8. All even -> same mask
9. At most one odd -> same mask + one-bit flip
10. Did I initialize empty mask?
```

---

## 26. Next Step

```text
019_Balanced_0_1_Subarray.md
```

Next file covers:

```text
0 -> -1 transformation
balanced binary arrays
equal 0 and 1
prefix balance
count and longest variants
