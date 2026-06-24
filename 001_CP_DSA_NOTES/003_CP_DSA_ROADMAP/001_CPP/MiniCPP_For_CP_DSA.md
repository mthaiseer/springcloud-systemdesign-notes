# MiniCPP_For_CP_DSA.md

> **Goal:** Learn only the C++ needed for Competitive Programming, DSA interviews, Codeforces, LeetCode, and FAANG-style problem solving.
>
> This is not a full C++ course. This is a **CP weapon guide**: syntax, STL, templates, debugging, and mistakes that directly affect AC/WA/TLE/RE.

---

# 1. Why C++ For CP

C++ is popular in Competitive Programming because it gives you three things together:

1. **Speed** — usually faster than Java/Python for tight time limits.
2. **Control** — you can choose exact data types, memory layout, and STL containers.
3. **STL Power** — vectors, pairs, sorting, binary search, priority queues, maps, sets, etc.

For CP, you do not need advanced C++ like templates, move semantics, smart pointers, RAII internals, or complex OOP. You need a focused subset:

```text
Input fast
Store data correctly
Avoid overflow
Use STL containers
Sort / search / iterate
Write clean functions
Debug quickly
Submit safe template
```

## Core Mental Model

```text
C++ in CP = Fast syntax layer over algorithms

Problem statement
      |
      v
Choose DSA pattern
      |
      v
Choose correct data structure
      |
      v
Implement safely in C++17
      |
      v
Avoid WA/TLE/Overflow/Runtime
```

Your C++ should become invisible. During contests, you should think about the algorithm, not syntax.

---

# 2. CP Mental Model

In CP, every solution has four layers:

```text
+---------------------------------------------------+
| Layer 4: Debugging / Edge Cases                   |
+---------------------------------------------------+
| Layer 3: Implementation in C++17                  |
+---------------------------------------------------+
| Layer 2: Data Structures                          |
+---------------------------------------------------+
| Layer 1: Algorithm / Pattern                      |
+---------------------------------------------------+
```

Most beginner WAs happen not because the idea is wrong, but because:

- `int` overflowed.
- Loop boundary was wrong.
- Input was misunderstood.
- Vector index went out of range.
- Comparator was invalid.
- Recursion base case missed.
- Used `cin/cout` without Fast IO.

## CP Implementation Checklist

Before submitting, ask:

```text
1. Did I use long long where needed?
2. Are array/vector indexes valid?
3. Did I handle n = 0, n = 1, negative values?
4. Did I reset variables for each test case?
5. Is complexity safe for constraints?
6. Is recursion depth safe?
7. Did I print exactly the required format?
```

---

# SECTION A — ABSOLUTE BASICS

---

# 3. Input Output

## Why This Exists

Every CP problem starts with input and ends with output. Wrong input reading means your algorithm never gets the correct data.

## Basic Syntax

```cpp
int n;
cin >> n;
cout << n << '\n';
```

Use `\n` instead of `endl` in contests. `endl` flushes output and can be slower.

## Working Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int a, b;
    cin >> a >> b;
    cout << a + b << '\n';
    return 0;
}
```

## Example Input

```text
3 5
```

## Output

```text
8
```

## Reading Multiple Test Cases

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int t;
    cin >> t;

    while (t--) {
        int a, b;
        cin >> a >> b;
        cout << a + b << '\n';
    }

    return 0;
}
```

## Dry Run

Input:

```text
3
1 2
5 7
10 20
```

Flow:

```text
t = 3
case 1: a=1, b=2  -> print 3
case 2: a=5, b=7  -> print 12
case 3: a=10,b=20 -> print 30
```

## Common Mistakes

```cpp
cout << ans << endl; // okay but slower
```

Better:

```cpp
cout << ans << '\n';
```

---

# 4. Variables & Data Types

## Why This Exists

Data type decides how large a number can be stored. Wrong type causes overflow and wrong answers.

## Common CP Types

| Type | Typical Use |
|---|---|
| `int` | small numbers up to around 2e9 |
| `long long` | large values up to around 9e18 |
| `double` | floating point |
| `char` | single character |
| `string` | text |
| `bool` | true/false |

## Working Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int age = 25;
    long long population = 8000000000LL;
    double pi = 3.14159;
    char grade = 'A';
    string name = "Codeforces";
    bool passed = true;

    cout << age << '\n';
    cout << population << '\n';
    cout << pi << '\n';
    cout << grade << '\n';
    cout << name << '\n';
    cout << passed << '\n';

    return 0;
}
```

## Mental Model

```text
Variable = named box in memory
Type     = size and rule of the box
Value    = content inside the box
```

```text
int x = 10;

+-------+
|  10   |  x
+-------+
```

---

# 5. Overflow & Type Conversion

## Why This Exists

Overflow is one of the most common CP bugs.

Example:

```cpp
int a = 1000000000;
int b = 1000000000;
int c = a + b + b; // overflow
```

`3,000,000,000` is bigger than max `int`.

## Safe Rule

When constraints contain:

```text
n up to 1e5
ai up to 1e9
sum/product/count of pairs
```

Use `long long`.

## Working Code — Overflow Bug

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int a = 1000000000;
    int b = 1000000000;

    int wrong = a + b + b;
    long long correct = 1LL * a + b + b;

    cout << "wrong = " << wrong << '\n';
    cout << "correct = " << correct << '\n';

    return 0;
}
```

## Why `1LL` Works

```text
1LL * a + b + b

1LL forces calculation to long long
```

## Bad

```cpp
long long ans = a * b; // if a and b are int, multiplication happens as int first
```

## Good

```cpp
long long ans = 1LL * a * b;
```

---

# 6. Operators

## Common Operators

| Operator | Meaning |
|---|---|
| `+` | addition |
| `-` | subtraction |
| `*` | multiplication |
| `/` | division |
| `%` | remainder |
| `==` | equal check |
| `!=` | not equal |
| `< <= > >=` | comparison |
| `&&` | logical AND |
| `||` | logical OR |
| `!` | NOT |

## Working Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int a = 17, b = 5;

    cout << a + b << '\n'; // 22
    cout << a - b << '\n'; // 12
    cout << a * b << '\n'; // 85
    cout << a / b << '\n'; // 3 integer division
    cout << a % b << '\n'; // 2 remainder

    if (a > b && a % b == 2) {
        cout << "condition true\n";
    }

    return 0;
}
```

## CP Important: Integer Division

```text
17 / 5 = 3, not 3.4
```

For ceiling division of positive numbers:

```cpp
long long ceilDiv(long long a, long long b) {
    return (a + b - 1) / b;
}
```

## Working Code — Ceiling Division

```cpp
#include <bits/stdc++.h>
using namespace std;

long long ceilDiv(long long a, long long b) {
    return (a + b - 1) / b;
}

int main() {
    cout << ceilDiv(17, 5) << '\n'; // 4
    cout << ceilDiv(20, 5) << '\n'; // 4
    return 0;
}
```

---

# SECTION B — FUNCTIONS

---

# 7. Functions

## Why This Exists

Functions reduce repetition and make logic easier to test.

## Mental Model

```text
Input -> Function -> Output
```

```text
add(3, 5)
   |
   v
returns 8
```

## Working Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int add(int a, int b) {
    return a + b;
}

int main() {
    cout << add(3, 5) << '\n';
    return 0;
}
```

## CP Use Cases

Use functions for:

- `gcd`
- `isPrime`
- `solve()`
- DFS/BFS helpers
- custom checks in binary search

## Standard CP Style

```cpp
#include <bits/stdc++.h>
using namespace std;

void solve() {
    int n;
    cin >> n;
    cout << n * 2 << '\n';
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;
    while (t--) solve();

    return 0;
}
```

---

# 8. Pass By Value

## Mental Model

Pass by value sends a copy.

```text
main x = 10
      |
      v
function receives copy y = 10
```

Changing the copy does not change original.

## Working Code

```cpp
#include <bits/stdc++.h>
using namespace std;

void change(int x) {
    x = 100;
}

int main() {
    int a = 10;
    change(a);
    cout << a << '\n'; // still 10
    return 0;
}
```

## Dry Run

```text
a = 10
change(a)
inside function x = copy of a
x becomes 100
function ends
original a still 10
```

---

# 9. Pass By Reference

## Mental Model

Reference means another name for the same variable.

```text
main a  ----+
            | same memory
function x -+
```

## Working Code

```cpp
#include <bits/stdc++.h>
using namespace std;

void change(int &x) {
    x = 100;
}

int main() {
    int a = 10;
    change(a);
    cout << a << '\n'; // 100
    return 0;
}
```

## Use In CP

Use reference when:

- You want to modify original value.
- You want to avoid copying big vectors/strings.

## Vector Example

```cpp
#include <bits/stdc++.h>
using namespace std;

long long sumVector(const vector<int> &v) {
    long long sum = 0;
    for (int x : v) sum += x;
    return sum;
}

int main() {
    vector<int> v = {1, 2, 3, 4};
    cout << sumVector(v) << '\n';
    return 0;
}
```

`const vector<int>&` means:

```text
Do not copy vector
Do not modify vector
```

---

# 10. Recursion Basics

## Why This Exists

Recursion is needed for DFS, backtracking, tree problems, divide and conquer, and DP.

## Mental Model

```text
Function calls itself on a smaller problem
until it reaches base case
```

## Factorial Example

```text
fact(5)
= 5 * fact(4)
= 5 * 4 * fact(3)
= 5 * 4 * 3 * fact(2)
= 5 * 4 * 3 * 2 * fact(1)
= 120
```

## Working Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long fact(int n) {
    if (n == 0 || n == 1) return 1;
    return 1LL * n * fact(n - 1);
}

int main() {
    cout << fact(5) << '\n';
    return 0;
}
```

## Recursion Checklist

```text
1. What is the base case?
2. Does every call move closer to base case?
3. Is recursion depth safe?
4. Are return values combined correctly?
```

## Common Bug

```cpp
int f(int n) {
    return f(n - 1); // no base case -> stack overflow
}
```

---

# SECTION C — ARRAYS & STRINGS

---

# 11. Arrays

## Why This Exists

Arrays store multiple values in order.

## Mental Model

```text
Index:  0   1   2   3   4
Value: [5] [8] [2] [9] [1]
```

C++ indexing starts from `0`.

## Static Array Working Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int a[5] = {5, 8, 2, 9, 1};

    for (int i = 0; i < 5; i++) {
        cout << a[i] << ' ';
    }
    cout << '\n';

    return 0;
}
```

## Dynamic Input Array

For CP, prefer vector over raw arrays.

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<int> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];

    long long sum = 0;
    for (int i = 0; i < n; i++) sum += a[i];

    cout << sum << '\n';
    return 0;
}
```

## Common Mistake

```cpp
for (int i = 0; i <= n; i++) cout << a[i];
```

This is wrong. Last valid index is `n - 1`.

Correct:

```cpp
for (int i = 0; i < n; i++) cout << a[i];
```

---

# 12. Strings

## Why This Exists

Strings are arrays of characters with useful functions.

## Working Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    string s;
    cin >> s;

    cout << "length = " << s.size() << '\n';
    cout << "first = " << s[0] << '\n';
    cout << "last = " << s[s.size() - 1] << '\n';

    reverse(s.begin(), s.end());
    cout << s << '\n';

    return 0;
}
```

## Reading Full Line

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    string line;
    getline(cin, line);
    cout << line << '\n';
    return 0;
}
```

If using `cin >> n` before `getline`, consume leftover newline:

```cpp
cin.ignore();
getline(cin, line);
```

## Palindrome Working Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool isPalindrome(const string &s) {
    int l = 0, r = (int)s.size() - 1;
    while (l < r) {
        if (s[l] != s[r]) return false;
        l++;
        r--;
    }
    return true;
}

int main() {
    string s;
    cin >> s;
    cout << (isPalindrome(s) ? "YES" : "NO") << '\n';
    return 0;
}
```

---

# 13. Character Tricks

## Why This Exists

Many CP problems use lowercase letters, digit characters, frequency counting, and case conversion.

## ASCII Mental Model

```text
'a' to 'z' are continuous
'A' to 'Z' are continuous
'0' to '9' are continuous
```

## Frequency Count Working Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    string s;
    cin >> s;

    vector<int> freq(26, 0);

    for (char c : s) {
        freq[c - 'a']++;
    }

    for (int i = 0; i < 26; i++) {
        if (freq[i] > 0) {
            char c = 'a' + i;
            cout << c << " -> " << freq[i] << '\n';
        }
    }

    return 0;
}
```

## Digit Character To Number

```cpp
char c = '7';
int x = c - '0'; // 7
```

## Working Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    string s = "12345";
    int sum = 0;

    for (char c : s) {
        sum += c - '0';
    }

    cout << sum << '\n'; // 15
    return 0;
}
```

---

# SECTION D — CP ESSENTIAL SYNTAX

---

# 14. Pair

## Why This Exists

Pair stores two related values together.

Common uses:

- `(value, index)`
- `(x, y)` coordinate
- `(distance, node)` in Dijkstra
- `(start, end)` interval

## Working Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    pair<int, int> p = {10, 3};

    cout << p.first << ' ' << p.second << '\n';

    vector<pair<int, int>> v;
    v.push_back({5, 2});
    v.push_back({1, 4});
    v.push_back({5, 1});

    sort(v.begin(), v.end());

    for (auto x : v) {
        cout << x.first << ' ' << x.second << '\n';
    }

    return 0;
}
```

## Pair Sorting Rule

```text
Sort by first
If first equal, sort by second
```

---

# 15. Tuple

## Why This Exists

Tuple stores more than two values.

Useful for:

- `(weight, u, v)` edge
- `(x, y, z)` state
- `(score, id, time)` sorting

## Working Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    tuple<int, int, int> edge = {5, 1, 2};

    int w, u, v;
    tie(w, u, v) = edge;

    cout << "weight=" << w << " from=" << u << " to=" << v << '\n';

    vector<tuple<int, int, int>> edges;
    edges.push_back({10, 2, 3});
    edges.push_back({5, 1, 2});
    edges.push_back({7, 1, 3});

    sort(edges.begin(), edges.end());

    for (auto [weight, a, b] : edges) {
        cout << weight << ' ' << a << ' ' << b << '\n';
    }

    return 0;
}
```

## Sorting Rule

```text
Sort by first item
If tie, second item
If tie, third item
```

---

# 16. Struct

## Why This Exists

Struct gives names to fields. It is cleaner than tuple when meaning matters.

## Working Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Student {
    string name;
    int score;
    int age;
};

int main() {
    vector<Student> students = {
        {"Ali", 90, 20},
        {"Bob", 85, 19},
        {"Sara", 90, 18}
    };

    sort(students.begin(), students.end(), [](const Student &a, const Student &b) {
        if (a.score != b.score) return a.score > b.score; // higher score first
        return a.age < b.age; // younger first if same score
    });

    for (const Student &s : students) {
        cout << s.name << ' ' << s.score << ' ' << s.age << '\n';
    }

    return 0;
}
```

## When To Use Struct

Use struct when tuple becomes unclear:

```cpp
tuple<int,int,int,int,int> state; // hard to understand
```

Better:

```cpp
struct State {
    int row, col, steps, keys, mask;
};
```

---

# 17. Auto

## Why This Exists

`auto` lets compiler infer type. It reduces long STL syntax.

## Working Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<pair<int, string>> v = {
        {1, "one"},
        {2, "two"}
    };

    for (auto p : v) {
        cout << p.first << ' ' << p.second << '\n';
    }

    return 0;
}
```

## Important: Copy vs Reference

```cpp
for (auto x : v)      // copy
for (auto &x : v)     // reference, can modify
for (const auto &x : v) // no copy, cannot modify
```

## Working Code — Reference

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> v = {1, 2, 3};

    for (auto &x : v) {
        x *= 2;
    }

    for (const auto &x : v) {
        cout << x << ' ';
    }
    cout << '\n';

    return 0;
}
```

---

# 18. Range Based Loop

## Why This Exists

Range loop makes iteration cleaner.

## Working Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> v = {4, 7, 1, 9};

    for (int x : v) {
        cout << x << ' ';
    }
    cout << '\n';

    return 0;
}
```

## Modify Elements

```cpp
for (int &x : v) x++;
```

## Avoid Copying Big Objects

```cpp
for (const string &s : names) {
    cout << s << '\n';
}
```

## Common Mistake

```cpp
for (auto x : v) x++; // modifies copy only
```

Correct:

```cpp
for (auto &x : v) x++;
```

---

# SECTION E — STL MUST KNOW

---

# 19. Vector

## Why This Exists

Vector is dynamic array. It is the most used CP container.

## Mental Model

```text
vector<int> v

Index:  0   1   2
Value: [5] [8] [3]
```

## Working Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> v;

    v.push_back(5);
    v.push_back(8);
    v.push_back(3);

    cout << v.size() << '\n';
    cout << v[0] << '\n';

    v.pop_back();

    for (int x : v) cout << x << ' ';
    cout << '\n';

    return 0;
}
```

## Useful Operations

| Operation | Complexity |
|---|---|
| `push_back` | amortized O(1) |
| `pop_back` | O(1) |
| `v[i]` | O(1) |
| `size()` | O(1) |
| `sort` | O(n log n) |

## Input Vector

```cpp
int n;
cin >> n;
vector<int> a(n);
for (int i = 0; i < n; i++) cin >> a[i];
```

---

# 20. Iterator Basics

## Why This Exists

STL algorithms use iterators to represent ranges.

## Mental Model

```text
v.begin() points to first element
v.end() points one position after last element

[10] [20] [30] [40]
 ^                   ^
begin               end
```

## Working Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> v = {10, 20, 30, 40};

    for (auto it = v.begin(); it != v.end(); it++) {
        cout << *it << ' ';
    }
    cout << '\n';

    return 0;
}
```

## Important Rule

STL range is usually:

```cpp
[start, end)
```

Meaning start included, end excluded.

Example:

```cpp
sort(v.begin(), v.end());
```

Sorts all elements.

```cpp
sort(v.begin(), v.begin() + 3);
```

Sorts indexes `0, 1, 2` only.

---

# 21. Basic STL Algorithms

## 21.1 sort

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> v = {5, 1, 4, 2, 3};
    sort(v.begin(), v.end());

    for (int x : v) cout << x << ' ';
    cout << '\n';
    return 0;
}
```

Output:

```text
1 2 3 4 5
```

## 21.2 reverse

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> v = {1, 2, 3, 4};
    reverse(v.begin(), v.end());

    for (int x : v) cout << x << ' ';
    cout << '\n';
    return 0;
}
```

## 21.3 max_element / min_element

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> v = {7, 2, 9, 4};

    cout << *max_element(v.begin(), v.end()) << '\n';
    cout << *min_element(v.begin(), v.end()) << '\n';

    return 0;
}
```

## 21.4 accumulate

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> v = {1000000000, 1000000000, 1000000000};

    long long sum = accumulate(v.begin(), v.end(), 0LL);

    cout << sum << '\n';
    return 0;
}
```

Important:

```cpp
0LL // makes sum long long
```

Bad:

```cpp
accumulate(v.begin(), v.end(), 0); // int sum, may overflow
```

## 21.5 count

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> v = {1, 2, 2, 3, 2};
    cout << count(v.begin(), v.end(), 2) << '\n';
    return 0;
}
```

## 21.6 find

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> v = {4, 7, 1, 9};

    auto it = find(v.begin(), v.end(), 7);

    if (it != v.end()) {
        cout << "found at index " << it - v.begin() << '\n';
    } else {
        cout << "not found\n";
    }

    return 0;
}
```

## 21.7 binary_search

Only use on sorted range.

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> v = {1, 3, 5, 7, 9};

    cout << binary_search(v.begin(), v.end(), 5) << '\n'; // 1
    cout << binary_search(v.begin(), v.end(), 6) << '\n'; // 0

    return 0;
}
```

## 21.8 lower_bound / upper_bound

These are extremely important for CP.

```text
lower_bound(x) = first position >= x
upper_bound(x) = first position > x
```

## Working Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> v = {1, 2, 2, 2, 5, 7};

    int x = 2;

    int lb = lower_bound(v.begin(), v.end(), x) - v.begin();
    int ub = upper_bound(v.begin(), v.end(), x) - v.begin();

    cout << "first >= x at index " << lb << '\n';
    cout << "first > x at index " << ub << '\n';
    cout << "count of x = " << ub - lb << '\n';

    return 0;
}
```

## Dry Run

```text
v = [1, 2, 2, 2, 5, 7]
          ^        ^
          lb       ub

count = ub - lb = 4 - 1 = 3
```

---

# SECTION F — CONTEST POWER UPS

---

# 22. Lambda Functions

## Why This Exists

Lambda is an inline function. In CP, mainly used for custom sorting or local helper logic.

## Mental Model

```text
lambda = small unnamed function written where needed
```

## Working Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    auto square = [](int x) {
        return x * x;
    };

    cout << square(5) << '\n';
    return 0;
}
```

## Lambda For Sorting

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<pair<int, int>> v = {{1, 5}, {2, 3}, {3, 8}};

    sort(v.begin(), v.end(), [](pair<int, int> a, pair<int, int> b) {
        return a.second < b.second;
    });

    for (auto p : v) {
        cout << p.first << ' ' << p.second << '\n';
    }

    return 0;
}
```

---

# 23. Custom Comparator

## Why This Exists

Default sorting is not always enough.

Examples:

- Sort descending.
- Sort by second value.
- Sort by length of string.
- Sort intervals by end time.

## Comparator Mental Model

```text
return true means:
"a should come before b"
```

## Sort Descending

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> v = {5, 1, 9, 3};

    sort(v.begin(), v.end(), [](int a, int b) {
        return a > b;
    });

    for (int x : v) cout << x << ' ';
    cout << '\n';

    return 0;
}
```

## Sort Intervals By End Time

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<pair<int, int>> intervals = {{1, 5}, {2, 3}, {4, 6}};

    sort(intervals.begin(), intervals.end(), [](auto a, auto b) {
        return a.second < b.second;
    });

    for (auto [l, r] : intervals) {
        cout << l << ' ' << r << '\n';
    }

    return 0;
}
```

## Dangerous Comparator Bug

Wrong:

```cpp
return a <= b;
```

Correct:

```cpp
return a < b;
```

Comparator must be strict. Do not use `<=`.

---

# 24. Fast IO

## Why This Exists

Large input can make normal `cin/cout` slow.

## CP Standard

```cpp
ios::sync_with_stdio(false);
cin.tie(nullptr);
```

## Working Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    long long sum = 0;
    for (int i = 0; i < n; i++) {
        int x;
        cin >> x;
        sum += x;
    }

    cout << sum << '\n';
    return 0;
}
```

## Rule

After disabling sync, avoid mixing:

```cpp
scanf/printf
```

with:

```cpp
cin/cout
```

Use one style only.

---

# SECTION G — DEBUGGING

---

# 25. Common WA

WA means Wrong Answer.

Common causes:

```text
1. Forgot edge case
2. Wrong loop boundary
3. int overflow
4. Not resetting per test case
5. Wrong sorting comparator
6. Used binary search on unsorted array
7. Printed wrong format
```

## Working Bug Example

Wrong:

```cpp
for (int i = 0; i <= n; i++) sum += a[i];
```

Correct:

```cpp
for (int i = 0; i < n; i++) sum += a[i];
```

## Debug Print Style

```cpp
cerr << "i=" << i << " value=" << a[i] << '\n';
```

`cerr` prints to error stream. It does not affect normal output in most local debugging.

---

# 26. Common TLE

TLE means Time Limit Exceeded.

## Causes

```text
1. O(n^2) when n = 1e5
2. Using endl repeatedly
3. Not using Fast IO
4. Infinite loop
5. Expensive copying of vectors/strings
```

## Complexity Rules

| n | Usually Safe Complexity |
|---|---|
| 1e2 | O(n^3) sometimes okay |
| 1e3 | O(n^2) okay |
| 1e5 | O(n log n) or O(n) |
| 1e6 | O(n) preferred |
| 1e9 | O(log n) or O(1) |

## Bad Copy Example

```cpp
long long sumVector(vector<int> v) { // copies vector
    long long sum = 0;
    for (int x : v) sum += x;
    return sum;
}
```

Better:

```cpp
long long sumVector(const vector<int> &v) {
    long long sum = 0;
    for (int x : v) sum += x;
    return sum;
}
```

---

# 27. Common Runtime Errors

Runtime Error means program crashed.

Common causes:

```text
1. Out of bounds vector access
2. Division by zero
3. Stack overflow from deep recursion
4. Accessing empty vector back/front
5. Negative index
```

## Out Of Bounds Example

Wrong:

```cpp
vector<int> v(3);
cout << v[3]; // invalid, indexes are 0,1,2
```

## Safe Check

```cpp
if (!v.empty()) {
    cout << v.back() << '\n';
}
```

## Division By Zero

```cpp
if (b != 0) cout << a / b << '\n';
```

---

# 28. Overflow Bugs

## Classic Pair Count Bug

If `n = 200000`, number of pairs can be around:

```text
n * (n - 1) / 2 ≈ 20,000,000,000
```

This does not fit in int.

## Working Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n = 200000;

    long long pairs = 1LL * n * (n - 1) / 2;

    cout << pairs << '\n';
    return 0;
}
```

## Rule

Use `long long` for:

```text
sum
product
answer count
number of pairs
prefix sums
large coordinates
```

---

# 29. Infinite Loop Bugs

## Common Binary Search Bug

Wrong:

```cpp
while (l < r) {
    int mid = (l + r) / 2;
    if (ok(mid)) l = mid; // may not move
    else r = mid - 1;
}
```

If `mid == l`, loop may never end.

## Safer Pattern For First True

```cpp
int l = 0, r = n - 1, ans = -1;
while (l <= r) {
    int mid = l + (r - l) / 2;
    if (ok(mid)) {
        ans = mid;
        r = mid - 1;
    } else {
        l = mid + 1;
    }
}
```

## Simple Infinite Loop Example

Wrong:

```cpp
int i = 0;
while (i < n) {
    cout << i << '\n';
    // forgot i++
}
```

Correct:

```cpp
int i = 0;
while (i < n) {
    cout << i << '\n';
    i++;
}
```

---

# SECTION H — CP TEMPLATE

---

# 30. Final CP Template

Use this for Codeforces/AtCoder/LeetCode-style local practice.

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;
using pii = pair<int, int>;
using pll = pair<long long, long long>;

#define all(x) (x).begin(), (x).end()
#define sz(x) (int)(x).size()

void solve() {
    // Read input here
    int n;
    cin >> n;

    vector<ll> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];

    ll sum = 0;
    for (ll x : a) sum += x;

    cout << sum << '\n';
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t = 1;
    cin >> t;

    while (t--) {
        solve();
    }

    return 0;
}
```

## When Problem Has No Test Cases

Change:

```cpp
int t = 1;
cin >> t;
while (t--) solve();
```

To:

```cpp
solve();
```

## Personal Rule

Do not use too many macros early. Macros can hide bugs. Use only:

```cpp
#define all(x) (x).begin(), (x).end()
```

---

# SECTION I — CHEAT SHEET

---

# 31. STL Cheat Sheet

## Vector

```cpp
vector<int> v;
v.push_back(x);
v.pop_back();
v.size();
v.empty();
v[i];
v.front();
v.back();
```

## Sort

```cpp
sort(v.begin(), v.end());
sort(v.rbegin(), v.rend());
sort(all(v));
```

## Algorithms

```cpp
reverse(all(v));
*max_element(all(v));
*min_element(all(v));
accumulate(all(v), 0LL);
count(all(v), x);
find(all(v), x);
binary_search(all(v), x);
lower_bound(all(v), x);
upper_bound(all(v), x);
```

## Pair

```cpp
pair<int,int> p = {1, 2};
p.first;
p.second;
```

## Tuple

```cpp
tuple<int,int,int> t = {1,2,3};
auto [x, y, z] = t;
```

---

# 32. Complexity Cheat Sheet

```text
O(1)        constant
O(log n)    binary search, balanced tree operation
O(n)        single loop
O(n log n)  sorting
O(n^2)      nested loops
O(2^n)      subsets/backtracking
O(n!)       permutations
```

## Constraint Mapping

```text
n <= 20       -> bitmask/subsets possible
n <= 100      -> O(n^3) maybe possible
n <= 1000     -> O(n^2)
n <= 1e5      -> O(n log n) / O(n)
n <= 1e6      -> O(n)
n <= 1e9      -> O(log n) / math
```

---

# 33. Syntax Cheat Sheet

## Loops

```cpp
for (int i = 0; i < n; i++) {}
for (int x : v) {}
for (auto &x : v) {}
while (condition) {}
```

## Conditions

```cpp
if (x > 0) {}
else if (x == 0) {}
else {}
```

## Functions

```cpp
int add(int a, int b) {
    return a + b;
}
```

## References

```cpp
void change(int &x) {
    x = 100;
}
```

## Const Reference

```cpp
long long sum(const vector<int> &v) {}
```

---

# 34. Contest Checklist

Before Submit:

```text
[ ] Read constraints carefully
[ ] Decide complexity before coding
[ ] Use long long for sums/products/counts
[ ] Handle all test cases
[ ] Reset variables inside solve()
[ ] Check n=0/n=1
[ ] Check sorted requirement before binary_search/lower_bound
[ ] Check vector indexes
[ ] Avoid endl
[ ] Remove debug prints
[ ] Match output format exactly
```

## Final One Picture To Remember

```text
                    CP C++17 Survival Model

        +------------------------------------------+
        |              Problem Input              |
        +--------------------+---------------------+
                             |
                             v
        +------------------------------------------+
        | Choose DSA Pattern                       |
        | prefix / two pointer / binary / graph    |
        +--------------------+---------------------+
                             |
                             v
        +------------------------------------------+
        | Choose Data Structure                    |
        | vector / pair / string / set / map       |
        +--------------------+---------------------+
                             |
                             v
        +------------------------------------------+
        | Implement With Safe C++                  |
        | long long / refs / STL / fast IO         |
        +--------------------+---------------------+
                             |
                             v
        +------------------------------------------+
        | Debug Against Bugs                       |
        | WA / TLE / RE / overflow / loops         |
        +--------------------+---------------------+
                             |
                             v
        +------------------------------------------+
        |                  AC                      |
        +------------------------------------------+
```

---

# Recommended Next Step

After finishing this file, do not study more C++ theory. Start MiniDSA in this order:

```text
001 STL Patterns
002 Math Basics
003 Prefix Sum
004 Two Pointers
005 Sliding Window
006 Binary Search
007 Recursion
008 Backtracking
009 Bit Manipulation
010 Greedy
```

C++ is the language weapon. DSA patterns are the actual fight.

