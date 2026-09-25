# 012_Permutations_Repetition.md

# MiniMathEngine — Permutations With Repetition

> Goal: understand how to count and generate ordered arrangements when the same item can be reused multiple times.

---

## Clickable Index

1. [What Is Permutation With Repetition?](#1-what-is-permutation-with-repetition)
2. [Core Formula](#2-core-formula)
3. [Why Order Matters](#3-why-order-matters)
4. [Permutation Without Repetition vs With Repetition](#4-permutation-without-repetition-vs-with-repetition)
5. [Mental Model](#5-mental-model)
6. [Decision Tree](#6-decision-tree)
7. [Basic Examples](#7-basic-examples)
8. [Password / PIN Example](#8-password--pin-example)
9. [String Generation Example](#9-string-generation-example)
10. [Counting Using Multiplication Principle](#10-counting-using-multiplication-principle)
11. [Relation With Cartesian Product](#11-relation-with-cartesian-product)
12. [Generating All Repeated Permutations](#12-generating-all-repeated-permutations)
13. [Backtracking Template](#13-backtracking-template)
14. [Iterative Base-n Counting Trick](#14-iterative-base-n-counting-trick)
15. [Modulo Counting](#15-modulo-counting)
16. [Common Mistakes](#16-common-mistakes)
17. [Interview Pattern Recognition](#17-interview-pattern-recognition)
18. [Complexity](#18-complexity)
19. [Real World Applications](#19-real-world-applications)
20. [MiniMathEngine Implementation Notes](#20-minimathengine-implementation-notes)
21. [CP / FAANG Problem Forms](#21-cp--faang-problem-forms)
22. [Final Summary](#22-final-summary)

---

## 1. What Is Permutation With Repetition?

Permutation with repetition means:

- We arrange items.
- Order matters.
- The same item can be used again and again.

Example:

```text
choices = {1, 2, 3}
length  = 2
```

All possible ordered sequences:

```text
11 12 13
21 22 23
31 32 33
```

Total sequences:

```text
3 * 3 = 9
```

Because each position has 3 choices.

---

## 2. Core Formula

If:

```text
n = number of available choices
r = number of positions
```

Then:

```text
Total permutations with repetition = n^r
```

Example:

```text
n = 10 digits: 0..9
r = 4 positions

Total 4-digit PINs = 10^4 = 10000
```

---

## 3. Why Order Matters

In permutation, order matters.

```text
AB != BA
12 != 21
cat != act
```

So `{A, B}` of length 2 gives:

```text
AA
AB
BA
BB
```

Total:

```text
2^2 = 4
```

---

## 4. Permutation Without Repetition vs With Repetition

| Type | Reuse Allowed? | Formula |
|---|---:|---:|
| Permutation without repetition | No | nPr = n! / (n-r)! |
| Permutation with repetition | Yes | n^r |

Example with `{A, B, C}`, length `2`:

### Without repetition

```text
AB AC BA BC CA CB
Total = 6
```

### With repetition

```text
AA AB AC
BA BB BC
CA CB CC
Total = 9
```

---

## 5. Mental Model

Think of `r` empty boxes.

```text
Box 1   Box 2   Box 3   ...   Box r
  n       n       n             n
```

Each box independently has `n` choices.

So:

```text
n * n * n * ... r times = n^r
```

---

## 6. Decision Tree

Use this decision tree in CP / FAANG problems:

```text
Problem asks to arrange/select ordered sequence?
|
+-- No  -> Maybe combination / subset / counting problem
|
+-- Yes
    |
    +-- Can reuse same item?
        |
        +-- Yes -> Permutation with repetition -> n^r
        |
        +-- No  -> Permutation without repetition -> nPr
```

Another fast check:

```text
Length fixed?        yes
Order important?     yes
Reuse allowed?       yes
Answer pattern:      n^r
```

---

## 7. Basic Examples

### Example 1

How many strings of length 3 can be formed using `{a, b}`?

```text
n = 2
r = 3
answer = 2^3 = 8
```

Strings:

```text
aaa
aab
aba
abb
baa
bab
bba
bbb
```

### Example 2

How many 5-digit PINs are possible using digits `0..9`?

```text
n = 10
r = 5
answer = 10^5 = 100000
```

---

## 8. Password / PIN Example

A password has length 6.
Each character can be lowercase English letter.

```text
n = 26
r = 6
answer = 26^6
```

If uppercase + lowercase + digits are allowed:

```text
n = 26 + 26 + 10 = 62
r = 6
answer = 62^6
```

---

## 9. String Generation Example

Given characters:

```text
chars = ['a', 'b', 'c']
length = 2
```

Generate all strings:

```text
aa ab ac
ba bb bc
ca cb cc
```

This is exactly permutation with repetition.

---

## 10. Counting Using Multiplication Principle

The multiplication principle says:

If one task has `a` ways and another independent task has `b` ways, total ways:

```text
a * b
```

For repeated permutation:

```text
Position 1 -> n ways
Position 2 -> n ways
Position 3 -> n ways
...
Position r -> n ways
```

Total:

```text
n * n * n * ... * n = n^r
```

---

## 11. Relation With Cartesian Product

Permutation with repetition is same as Cartesian product repeated `r` times.

```text
A = {0, 1}
A x A x A
```

Result:

```text
000
001
010
011
100
101
110
111
```

Total:

```text
|A|^3 = 2^3 = 8
```

---

## 12. Generating All Repeated Permutations

For generation, use recursion/backtracking.

At each index:

```text
try every possible character
place it
move to next index
undo if needed
```

Because repetition is allowed, we do not need a `used[]` array.

---

## 13. Backtracking Template

```cpp
void dfs(int pos) {
    if (pos == r) {
        print current sequence;
        return;
    }

    for each choice in choices {
        current[pos] = choice;
        dfs(pos + 1);
    }
}
```

Important point:

```text
No used[] array because same choice can be reused.
```

---

## 14. Iterative Base-n Counting Trick

If choices size is `n` and length is `r`, each sequence can be represented like a number in base `n`.

Example:

```text
choices = {a, b}
n = 2
r = 3
```

Numbers from `0` to `2^3 - 1`:

```text
0 -> 000 -> aaa
1 -> 001 -> aab
2 -> 010 -> aba
3 -> 011 -> abb
4 -> 100 -> baa
5 -> 101 -> bab
6 -> 110 -> bba
7 -> 111 -> bbb
```

This is useful when you want iterative generation.

---

## 15. Modulo Counting

In CP, answer can be huge.

Use fast power:

```text
answer = n^r mod MOD
```

Common MOD:

```text
1e9 + 7 = 1000000007
```

---

## 16. Common Mistakes

| Mistake | Why Wrong |
|---|---|
| Using nPr | nPr is for no repetition |
| Using combination formula | Combination ignores order |
| Using used[] in generation | used[] blocks repetition |
| Forgetting leading zero | PINs/passwords usually allow leading zero |
| Not using modulo | n^r can overflow |

---

## 17. Interview Pattern Recognition

Look for words like:

```text
password
PIN
code
string of length k
sequence of length k
can reuse
replacement allowed
each position has choices
```

Then ask:

```text
Does every position have same independent choices?
```

If yes:

```text
answer = n^r
```

---

## 18. Complexity

### Counting only

```text
O(log r) using binary exponentiation
```

### Generating all sequences

```text
O(n^r * r)
```

Why `* r`?

Because printing/copying each sequence costs length `r`.

---

## 19. Real World Applications

| Area | Example |
|---|---|
| Security | PIN/password search space |
| Testing | Generate all combinations of configs |
| Distributed systems | Generate retry policy states |
| Compilers | Generate token sequences |
| Search systems | Query expansion candidates |
| CP/DSA | Generate strings, numbers, bitmasks |

---

## 20. MiniMathEngine Implementation Notes

For MiniMathEngine, we need two operations:

```text
1. Count permutations with repetition
2. Generate permutations with repetition
```

Recommended functions:

```cpp
long long countPermutationsWithRepetition(long long n, long long r, long long mod);
vector<string> generateRepeatedPermutations(vector<char>& choices, int r);
```

Use:

```text
binary exponentiation for counting
DFS/backtracking for generation
```

---

# 21. CP / FAANG Problem Forms

## Form 1: Count Number of Passwords

### Problem

You are given `n` possible characters and password length `r`.
Each character can be reused.
Find the number of possible passwords modulo `1e9+7`.

### Pattern

```text
Order matters + repetition allowed + fixed length
=> n^r
```

### Step-by-Step Working

Example:

```text
n = 3
r = 4
choices = {a, b, c}
```

Each position has 3 choices:

```text
Position 1: 3 choices
Position 2: 3 choices
Position 3: 3 choices
Position 4: 3 choices
```

Total:

```text
3 * 3 * 3 * 3 = 3^4 = 81
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

const long long MOD = 1000000007LL;

// Fast binary exponentiation: computes base^exp % MOD
long long modPow(long long base, long long exp) {
    long long result = 1;
    base %= MOD;

    while (exp > 0) {
        // If current bit is 1, multiply result by base
        if (exp & 1) {
            result = (result * base) % MOD;
        }

        // Square the base for next bit
        base = (base * base) % MOD;

        // Move to next bit
        exp >>= 1;
    }

    return result;
}

int main() {
    long long n, r;
    cin >> n >> r;

    // Since repetition is allowed, answer is n^r
    cout << modPow(n, r) << "\n";

    return 0;
}
```

---

## Form 2: Generate All Strings of Length K

### Problem

Given a set of characters, generate all strings of length `k` where characters can repeat.

Example:

```text
chars = {a, b}
k = 3
```

Output:

```text
aaa
aab
aba
abb
baa
bab
bba
bbb
```

### Pattern

```text
Generate all ordered sequences with repetition
=> DFS/backtracking without used[]
```

### Step-by-Step Working

For each index:

```text
index 0 -> choose a or b
index 1 -> choose a or b
index 2 -> choose a or b
```

Decision tree:

```text
              ""
          /        \
        a            b
      /   \        /   \
    aa    ab     ba     bb
   / \   / \    / \    / \
aaa aab aba abb baa bab bba bbb
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

void generate(int pos, int k, const vector<char>& chars, string& current) {
    // Base case: string length becomes k
    if (pos == k) {
        cout << current << "\n";
        return;
    }

    // Try every character at current position
    for (char ch : chars) {
        current[pos] = ch;

        // Move to next position
        generate(pos + 1, k, chars, current);
    }
}

int main() {
    vector<char> chars = {'a', 'b'};
    int k = 3;

    // Pre-allocate string of size k
    string current(k, ' ');

    generate(0, k, chars, current);

    return 0;
}
```

---

## Form 3: Count Binary Strings of Length N

### Problem

How many binary strings of length `n` are possible?

Example:

```text
n = 4
```

Binary strings:

```text
0000, 0001, ..., 1111
```

### Pattern

```text
Each position has 2 choices: 0 or 1
=> 2^n
```

### Step-by-Step Working

For `n = 4`:

```text
position 1 -> 2 choices
position 2 -> 2 choices
position 3 -> 2 choices
position 4 -> 2 choices
```

Total:

```text
2 * 2 * 2 * 2 = 16
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countBinaryStrings(int n) {
    // 1LL << n is equal to 2^n for small n
    // Only safe when n < 63 for long long
    return 1LL << n;
}

int main() {
    int n;
    cin >> n;

    cout << countBinaryStrings(n) << "\n";

    return 0;
}
```

For large `n`, use modulo fast power.

```cpp
#include <bits/stdc++.h>
using namespace std;

const long long MOD = 1000000007LL;

long long modPow(long long base, long long exp) {
    long long ans = 1;
    base %= MOD;

    while (exp > 0) {
        if (exp & 1) ans = (ans * base) % MOD;
        base = (base * base) % MOD;
        exp >>= 1;
    }

    return ans;
}

int main() {
    long long n;
    cin >> n;

    // Binary string has 2 choices per position
    cout << modPow(2, n) << "\n";

    return 0;
}
```

---

## Form 4: Generate All Numbers of Length K Using Digits

### Problem

Generate all length `k` numbers using digits `0..9`. Repetition is allowed.

### Important Edge Case

If it is a PIN/code, leading zero is allowed.

```text
0001 is valid PIN
```

If it is a normal number, leading zero may not be allowed.

```text
0001 is usually not considered a 4-digit number
```

### Step-by-Step Working

For PIN length `2`:

```text
00 01 02 ... 09
10 11 12 ... 19
...
90 91 92 ... 99
```

Total:

```text
10^2 = 100
```

### C++ Code: PIN Generation With Leading Zero

```cpp
#include <bits/stdc++.h>
using namespace std;

void generatePIN(int pos, int k, string& current) {
    if (pos == k) {
        cout << current << "\n";
        return;
    }

    // For PIN, leading zero is allowed
    for (char digit = '0'; digit <= '9'; digit++) {
        current[pos] = digit;
        generatePIN(pos + 1, k, current);
    }
}

int main() {
    int k;
    cin >> k;

    string current(k, '0');
    generatePIN(0, k, current);

    return 0;
}
```

### C++ Code: Normal Number Generation Without Leading Zero

```cpp
#include <bits/stdc++.h>
using namespace std;

void generateNumber(int pos, int k, string& current) {
    if (pos == k) {
        cout << current << "\n";
        return;
    }

    char startDigit = '0';

    // First digit cannot be zero for a normal k-digit number
    if (pos == 0) {
        startDigit = '1';
    }

    for (char digit = startDigit; digit <= '9'; digit++) {
        current[pos] = digit;
        generateNumber(pos + 1, k, current);
    }
}

int main() {
    int k;
    cin >> k;

    string current(k, '0');
    generateNumber(0, k, current);

    return 0;
}
```

---

## Form 5: Count Strings With Different Choices Per Position

### Problem

A code has 3 positions:

```text
Position 1: 26 lowercase letters
Position 2: 10 digits
Position 3: 26 lowercase letters
```

How many codes are possible?

### Pattern

This is not simply `n^r` because choices differ per position.
But it is still multiplication principle.

### Step-by-Step Working

```text
Total = 26 * 10 * 26 = 6760
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<long long> choices = {26, 10, 26};

    long long total = 1;

    // Multiply choices of all independent positions
    for (long long x : choices) {
        total *= x;
    }

    cout << total << "\n";

    return 0;
}
```

Modulo version:

```cpp
#include <bits/stdc++.h>
using namespace std;

const long long MOD = 1000000007LL;

int main() {
    int r;
    cin >> r;

    vector<long long> choices(r);
    for (int i = 0; i < r; i++) cin >> choices[i];

    long long answer = 1;

    for (long long x : choices) {
        answer = (answer * (x % MOD)) % MOD;
    }

    cout << answer << "\n";

    return 0;
}
```

---

## Form 6: Brute Force Search Space Size

### Problem

A password uses 62 characters and length is 8.
How many possibilities must a brute-force attacker try?

### Pattern

```text
62 choices per position
8 positions
=> 62^8
```

### Step-by-Step Working

```text
Position 1 -> 62 choices
Position 2 -> 62 choices
...
Position 8 -> 62 choices

Answer = 62^8
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long double fastPowerLongDouble(long double base, long long exp) {
    long double result = 1.0;

    while (exp > 0) {
        if (exp & 1) result *= base;
        base *= base;
        exp >>= 1;
    }

    return result;
}

int main() {
    long long choices = 62;
    long long length = 8;

    cout << fixed << setprecision(0);
    cout << fastPowerLongDouble(choices, length) << "\n";

    return 0;
}
```

---

## Form 7: Iterative Generation Using Base-n Conversion

### Problem

Generate all strings of length `k` using given characters without recursion.

### Pattern

```text
Each sequence maps to a number from 0 to n^k - 1
Convert number to base n
Map digits to characters
```

### Step-by-Step Working

For:

```text
chars = {a, b}
k = 3
```

```text
0 -> 000 -> aaa
1 -> 001 -> aab
2 -> 010 -> aba
3 -> 011 -> abb
4 -> 100 -> baa
5 -> 101 -> bab
6 -> 110 -> bba
7 -> 111 -> bbb
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long powerLL(long long base, int exp) {
    long long result = 1;
    while (exp--) result *= base;
    return result;
}

int main() {
    vector<char> chars = {'a', 'b'};
    int k = 3;

    int n = chars.size();
    long long total = powerLL(n, k);

    for (long long num = 0; num < total; num++) {
        long long x = num;
        string s(k, ' ');

        // Fill from right to left using base-n digits
        for (int pos = k - 1; pos >= 0; pos--) {
            int digit = x % n;
            s[pos] = chars[digit];
            x /= n;
        }

        cout << s << "\n";
    }

    return 0;
}
```

---

## Form 8: Constraint Added on Top of Repetition

### Problem

Count binary strings of length `n` with no two adjacent `1`s.

### Why This Is Different

Without constraint:

```text
2^n
```

With constraint:

```text
Cannot freely choose every position
```

Now it becomes DP, not simple permutation with repetition.

### Step-by-Step Working

For `n = 3`, all binary strings:

```text
000
001
010
011 invalid
100
101
110 invalid
111 invalid
```

Valid:

```text
000, 001, 010, 100, 101
```

Answer:

```text
5
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    // dp0 = number of valid strings ending with 0
    // dp1 = number of valid strings ending with 1
    long long dp0 = 1;
    long long dp1 = 1;

    for (int len = 2; len <= n; len++) {
        long long newDp0 = dp0 + dp1; // can place 0 after both 0 and 1
        long long newDp1 = dp0;       // can place 1 only after 0

        dp0 = newDp0;
        dp1 = newDp1;
    }

    cout << dp0 + dp1 << "\n";

    return 0;
}
```

### Key Interview Insight

```text
If choices are independent -> n^r
If choices depend on previous position -> DP / graph / automaton
```

---

# 22. Final Summary

Permutation with repetition is one of the easiest but most useful counting patterns.

Remember:

```text
Fixed length r
Order matters
Reuse allowed
Independent choices

Answer = n^r
```

Generation pattern:

```text
Backtracking without used[]
```

Counting pattern:

```text
Fast power / modular exponentiation
```

CP/FAANG recognition:

```text
password / PIN / code / string length k / sequence length k / reuse allowed
```

