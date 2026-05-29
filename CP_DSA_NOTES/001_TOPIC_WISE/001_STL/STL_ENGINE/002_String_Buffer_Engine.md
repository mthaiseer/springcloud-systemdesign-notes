# 002_String_Buffer_Engine.md

> MiniSTLEngine Phase 002  
> Topic: `string` as a **String Buffer Engine** for CP, DSA, parsing, search, and real-system thinking.

---

## Clickable Index

- [1. Goal](#1-goal)
- [2. Why String Is An Engine](#2-why-string-is-an-engine)
- [3. Real-System Mental Model](#3-real-system-mental-model)
- [4. Internal Working Of String](#4-internal-working-of-string)
- [5. String Operations Cheat Sheet](#5-string-operations-cheat-sheet)
- [6. CP/DSA Recognition](#6-cpdsa-recognition)
- [7. Engine Architecture](#7-engine-architecture)
- [8. Basic String Buffer Engine](#8-basic-string-buffer-engine)
- [9. Dry Run: Append, Reverse, Substring](#9-dry-run-append-reverse-substring)
- [10. CP Pattern 1: Palindrome Check](#10-cp-pattern-1-palindrome-check)
- [11. CP Pattern 2: Frequency Count Of Characters](#11-cp-pattern-2-frequency-count-of-characters)
- [12. CP Pattern 3: Two Pointer String Scan](#12-cp-pattern-3-two-pointer-string-scan)
- [13. CP Pattern 4: Substring Search Basics](#13-cp-pattern-4-substring-search-basics)
- [14. CP Pattern 5: Tokenization / Parsing](#14-cp-pattern-5-tokenization--parsing)
- [15. CP Pattern 6: Build Answer Efficiently](#15-cp-pattern-6-build-answer-efficiently)
- [16. Common Mistakes](#16-common-mistakes)
- [17. Complexity Table](#17-complexity-table)
- [18. Real-World Mapping](#18-real-world-mapping)
- [19. Final Mental Model](#19-final-mental-model)
- [20. Next Step](#20-next-step)

---

# 1. Goal

Learn `string` not only as text storage, but as a:

```text
String Buffer Engine
```

It helps you build:

```text
parsers
tokenizers
search utilities
log processors
command interpreters
URL routers
serialization formats
text matching systems
```

In CP/DSA, string problems often hide:

```text
two pointers
frequency counting
stack
hashing
sliding window
trie
KMP
rolling hash
DP
```

---

# 2. Why String Is An Engine

Normal thinking:

```cpp
string s = "hello";
```

Engine thinking:

```text
StringBufferEngine
    stores characters
    supports indexed access
    supports append
    supports scan
    supports substring extraction
    supports parsing
    supports transformation
```

A string is basically:

```text
dynamic array of characters
```

with special text-related operations.

---

# 3. Real-System Mental Model

Many backend systems are string-heavy:

```text
HTTP request line
JSON payload
URL path
log line
SQL query
Kafka message key
JWT token
config file
command input
```

Architecture:

```text
Raw Text Input
      |
      v
StringBufferEngine
      |
      +--> scan characters
      +--> split tokens
      +--> validate format
      +--> search pattern
      +--> transform output
```

Example:

```text
GET /users/123/orders HTTP/1.1
```

A web framework must parse:

```text
method = GET
path = /users/123/orders
protocol = HTTP/1.1
```

That starts from string processing.

---

# 4. Internal Working Of String

`std::string` behaves like:

```text
vector<char>
```

It has:

```text
size
capacity
contiguous memory
index access
append support
```

Example:

```text
s = "code"

index:  0   1   2   3
char:   c   o   d   e
```

Access:

```cpp
s[0] = 'c';
s[3] = 'e';
```

---

# 5. String Operations Cheat Sheet

```cpp
string s = "hello";

s.size();              // length
s.empty();             // check empty
s[0];                  // indexed access
s.back();              // last character
s += " world";         // append
s.push_back('!');      // append char
s.pop_back();          // remove last char
s.substr(0, 5);        // substring
s.find("ll");          // search
reverse(s.begin(), s.end());
sort(s.begin(), s.end());
```

---

# 6. CP/DSA Recognition

Use string patterns when problem says:

```text
characters
words
substring
subsequence
palindrome
anagram
brackets
path
URL
binary string
lexicographically smallest/largest
remove characters
pattern matching
```

Hidden pattern mapping:

| Problem clue | Likely pattern |
|---|---|
| palindrome | two pointers |
| anagram | frequency count |
| valid brackets | stack |
| longest substring | sliding window |
| find pattern | KMP / rolling hash |
| lexicographically smallest after removals | greedy + stack |
| binary string operations | prefix / greedy / bit thinking |
| edit distance / LCS | DP |

---

# 7. Engine Architecture

```text
MiniStringBufferEngine
├── raw string storage
├── append API
├── character access API
├── reverse API
├── frequency API
├── palindrome checker
├── tokenizer
├── substring search
└── output builder
```

---

# 8. Basic String Buffer Engine

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class StringBufferEngine {
private:
    string buffer;

public:
    StringBufferEngine(string initial = "") {
        buffer = initial;
    }

    void appendText(const string& text) {
        // WHY:
        // Append is used when we build output gradually.
        // Example: answer construction, log building, parser output.
        buffer += text;
    }

    void appendChar(char ch) {
        // push_back is efficient for adding one character.
        buffer.push_back(ch);
    }

    char getChar(int index) {
        // Always validate index in engine-style code.
        // In CP, direct s[i] is common, but bugs happen easily.
        if (index < 0 || index >= (int)buffer.size()) {
            throw out_of_range("Invalid string index");
        }

        return buffer[index];
    }

    void updateChar(int index, char ch) {
        if (index < 0 || index >= (int)buffer.size()) {
            throw out_of_range("Invalid string index");
        }

        buffer[index] = ch;
    }

    string substring(int start, int length) {
        // substr(start, length)
        // Useful for parsing and extracting parts of text.
        if (start < 0 || start >= (int)buffer.size()) {
            throw out_of_range("Invalid substring start");
        }

        return buffer.substr(start, length);
    }

    void reverseBuffer() {
        // Pattern:
        // Reversal is common in palindrome, string transformation,
        // and stack-like output problems.
        reverse(buffer.begin(), buffer.end());
    }

    int size() {
        return buffer.size();
    }

    string value() {
        return buffer;
    }

    void print() {
        cout << buffer << endl;
    }
};

int main() {
    StringBufferEngine engine("code");

    engine.appendText("forces");
    engine.appendChar('!');

    engine.print(); // codeforces!

    cout << "char at index 4 = ";
    cout << engine.getChar(4) << endl;

    cout << "substring(0,4) = ";
    cout << engine.substring(0, 4) << endl;

    engine.reverseBuffer();

    engine.print();

    return 0;
}
```

---

# 9. Dry Run: Append, Reverse, Substring

Initial:

```text
buffer = "code"
```

Append:

```text
appendText("forces")
buffer = "codeforces"
```

Append char:

```text
appendChar('!')
buffer = "codeforces!"
```

Substring:

```text
substring(0,4) = "code"
```

Reverse:

```text
"codeforces!" -> "!secrofedoc"
```

---

# 10. CP Pattern 1: Palindrome Check

## Problem Type

```text
Check whether a string reads same forward and backward.
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool isPalindrome(const string& s) {
    int left = 0;
    int right = (int)s.size() - 1;

    while (left < right) {
        // WHY:
        // Two pointers compare symmetric characters.
        // If any pair mismatches, palindrome property breaks.
        if (s[left] != s[right]) {
            return false;
        }

        left++;
        right--;
    }

    return true;
}

int main() {
    string s = "racecar";

    cout << isPalindrome(s) << endl; // 1

    return 0;
}
```

## Dry Run

```text
s = racecar

left=0, right=6 -> r == r
left=1, right=5 -> a == a
left=2, right=4 -> c == c
left=3, right=3 -> stop

answer = true
```

## Pattern Recognition

Use when problem says:

```text
palindrome
mirror
symmetric
can become palindrome
remove at most one char
```

---

# 11. CP Pattern 2: Frequency Count Of Characters

## Problem Type

```text
Check anagram
count characters
detect duplicates
rearrange string
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> charFrequency(const string& s) {
    // For lowercase English letters.
    vector<int> freq(26, 0);

    for (char ch : s) {
        // Convert character to index:
        // 'a' -> 0, 'b' -> 1, ..., 'z' -> 25
        freq[ch - 'a']++;
    }

    return freq;
}

bool areAnagrams(string a, string b) {
    if (a.size() != b.size()) {
        return false;
    }

    return charFrequency(a) == charFrequency(b);
}

int main() {
    string a = "listen";
    string b = "silent";

    cout << areAnagrams(a, b) << endl; // 1

    return 0;
}
```

## Dry Run

```text
listen:
l=1, i=1, s=1, t=1, e=1, n=1

silent:
s=1, i=1, l=1, e=1, n=1, t=1

same frequency => anagram
```

## Pattern Recognition

Use when problem says:

```text
anagram
permutation
same characters
rearrange
character count
```

---

# 12. CP Pattern 3: Two Pointer String Scan

## Problem Type

```text
Reverse vowels
valid palindrome ignoring spaces
merge strings
compare from both ends
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool isAlphaNum(char ch) {
    return isalnum((unsigned char)ch);
}

bool validPalindromeClean(string s) {
    int left = 0;
    int right = (int)s.size() - 1;

    while (left < right) {
        // Skip non-alphanumeric characters.
        while (left < right && !isAlphaNum(s[left])) {
            left++;
        }

        while (left < right && !isAlphaNum(s[right])) {
            right--;
        }

        // Compare lowercase version.
        if (tolower(s[left]) != tolower(s[right])) {
            return false;
        }

        left++;
        right--;
    }

    return true;
}

int main() {
    string s = "A man, a plan, a canal: Panama";

    cout << validPalindromeClean(s) << endl; // 1

    return 0;
}
```

## Mental Model

```text
left pointer searches from beginning
right pointer searches from end
both meet in middle
```

---

# 13. CP Pattern 4: Substring Search Basics

## Problem Type

```text
Find whether pattern exists inside text.
```

For basic use:

```cpp
s.find(pattern)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    string text = "hello codeforces";
    string pattern = "code";

    size_t pos = text.find(pattern);

    if (pos != string::npos) {
        cout << "Pattern found at index " << pos << endl;
    } else {
        cout << "Pattern not found\n";
    }

    return 0;
}
```

## CP Upgrade Path

For large constraints:

```text
single search small input    -> find()
many searches                -> trie / hashing
pattern matching             -> KMP
substring equality queries   -> rolling hash
```

---

# 14. CP Pattern 5: Tokenization / Parsing

## Problem Type

```text
Split sentence into words
Parse command
Parse CSV-like input
Extract numbers
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<string> splitBySpace(const string& line) {
    vector<string> tokens;

    stringstream ss(line);

    string word;

    while (ss >> word) {
        // Each extracted token is one word separated by spaces.
        tokens.push_back(word);
    }

    return tokens;
}

int main() {
    string line = "GET /users/123 HTTP/1.1";

    vector<string> tokens = splitBySpace(line);

    for (string token : tokens) {
        cout << token << endl;
    }

    return 0;
}
```

Output:

```text
GET
/users/123
HTTP/1.1
```

## Real-System Mapping

This is the start of:

```text
HTTP parser
command parser
log parser
config parser
```

---

# 15. CP Pattern 6: Build Answer Efficiently

## Problem Type

```text
Construct output string
remove characters
build transformed text
```

## Bad Approach

```cpp
answer = ch + answer;
```

This can become expensive because inserting at front shifts/copies.

## Better Approach

Append and reverse if needed.

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

string buildReverseEfficient(const string& s) {
    string ans;

    for (char ch : s) {
        // Append is efficient.
        ans.push_back(ch);
    }

    reverse(ans.begin(), ans.end());

    return ans;
}

int main() {
    string s = "abcde";

    cout << buildReverseEfficient(s) << endl; // edcba

    return 0;
}
```

## Mental Model

```text
append at back = efficient
insert at front repeatedly = usually expensive
```

---

# 16. Common Mistakes

## Mistake 1: Out Of Bound Access

Wrong:

```cpp
string s = "";
cout << s[0];
```

Correct:

```cpp
if (!s.empty()) {
    cout << s[0];
}
```

---

## Mistake 2: Confusing `substr`

```cpp
s.substr(start, length)
```

Second parameter is length, not end index.

Example:

```cpp
string s = "abcdef";
cout << s.substr(2, 3); // cde
```

---

## Mistake 3: Comparing `find()` Result Incorrectly

Wrong:

```cpp
if (s.find("abc") == -1)
```

Correct:

```cpp
if (s.find("abc") == string::npos)
```

---

## Mistake 4: Repeated Front Insert

Bad:

```cpp
ans = ch + ans;
```

Can become:

```text
O(N^2)
```

Better:

```cpp
ans.push_back(ch);
reverse(ans.begin(), ans.end());
```

---

## Mistake 5: `tolower` With Signed Char

Safer:

```cpp
tolower((unsigned char)ch)
```

Usually CP accepts simple `tolower(ch)`, but robust code should be careful.

---

# 17. Complexity Table

| Operation | Complexity |
|---|---:|
| `s[i]` | O(1) |
| `push_back(ch)` | amortized O(1) |
| `pop_back()` | O(1) |
| `s += t` | O(length of t) amortized |
| `substr(l, len)` | O(len) |
| `find(pattern)` | implementation-dependent, commonly efficient but not always linear guaranteed |
| `sort(s.begin(), s.end())` | O(N log N) |
| `reverse(s.begin(), s.end())` | O(N) |
| scan all chars | O(N) |

---

# 18. Real-World Mapping

| String Concept | Real-System Meaning |
|---|---|
| string buffer | request/log/message buffer |
| character scan | parser/tokenizer |
| substring | field extraction |
| frequency count | analytics/security/anomaly check |
| palindrome/two pointers | symmetric validation |
| find pattern | text search |
| append output | response builder |
| tokenization | protocol parsing |

---

# 19. Final Mental Model

String is:

```text
Dynamic character buffer + parsing/search engine
```

Best for:

```text
text storage
scanning
parsing
pattern matching
answer construction
character frequency
```

One-line CP rule:

```text
If problem is about characters, first decide: frequency, two pointers, stack, sliding window, hashing, or DP.
```

One-line system rule:

```text
Most protocols start as strings, then become structured data after parsing.
```

---

# 20. Next Step

Next file:

```text
003_Pair_Tuple_Record_Engine.md
```

Then:

```text
004_Stack_Parser_Engine.md
005_Queue_Event_Buffer_Engine.md
006_Deque_Sliding_Window_Engine.md
```
