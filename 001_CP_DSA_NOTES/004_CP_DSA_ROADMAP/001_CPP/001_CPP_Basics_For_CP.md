# 001_CPP_Basics_For_CP.md

# C++ Basics for CP: Data → Operation → Control Flow

## Why This Exists

Before STL, prefix sum, binary search, graph, DP, or greedy, every competitive programming solution depends on one basic mental model:

```text
DATA → OPERATION → CONTROL FLOW → ANSWER
```

Most beginners think C++ is a list of syntax rules. That is the wrong model. For CP and FAANG DSA, C++ is a tool for moving data through operations under controlled execution.

Every problem eventually becomes:

```text
Read input
Store data
Transform data
Make decisions
Repeat work
Print/return answer
```

So this chapter teaches exactly one topic: **C++ basics for CP**, using one mental model: **Data → Operation → Control Flow**.

This is enough to start beginner Codeforces A/B problems, AtCoder beginner tasks, and simple FAANG array/string interview problems.

---

## Core Mental Model

```text
DATA        = values you store
OPERATION   = work you do on values
CONTROL     = order, choice, and repetition
ANSWER      = final output
```

In C++:

```text
Variables store data.
Operators transform data.
if/else chooses paths.
loops repeat work.
functions organize logic.
```

One-picture flow:

```text
+---------+      +-------------+      +---------------+      +--------+
|  Input  | ---> |    Data     | ---> |  Operations   | ---> | Answer |
+---------+      +-------------+      +---------------+      +--------+
                         |                    ^
                         v                    |
                   +------------+-------------+
                   |     Control Flow         |
                   | if / for / while / func  |
                   +--------------------------+
```

Example:

```text
Problem: Given n numbers, print their sum.

Data        : n, x, sum
Operation   : sum += x
Control     : repeat n times
Answer      : sum
```

This same model appears in almost every future topic.

---

## Pattern Recognition

When you read a beginner CP/DSA problem, ask these five questions:

```text
1. What data is given?
2. What data must I store?
3. What operation updates the answer?
4. What control flow repeats or chooses logic?
5. What should I print or return?
```

### Common Recognition Signals

| Problem asks for | Data | Operation | Control Flow |
|---|---|---|---|
| sum / total | numbers | addition | loop |
| count | items | condition + increment | loop + if |
| maximum | current best | comparison | loop |
| minimum | current best | comparison | loop |
| possible? | boolean flag | condition check | if / loop |
| print sequence | index | increment | loop |
| choose bigger/smaller | values | compare | if/else |

---

## Recognition Checklist

Use this before coding:

```text
[ ] Is the input one value, many values, array, or string?
[ ] Do I need int or long long?
[ ] Is the answer updated once or inside a loop?
[ ] Is there a condition like positive/even/greater/smaller?
[ ] Is the loop 0-indexed or 1-indexed?
[ ] What is the exact output format?
```

This checklist prevents many beginner WA bugs.

---

## Sub-Patterns

C++ basics show up as small CP forms.

## 1. Direct Formula Pattern

```text
Input: a, b
Answer: a + b
```

Used when there is no repeated work.

```cpp
int a, b;
cin >> a >> b;
cout << a + b << '\n';
```

Mental model:

```text
Data → operation once → answer
```

## 2. Condition Pattern

```text
Input: x
Answer: YES if x is even, otherwise NO
```

```cpp
if (x % 2 == 0) cout << "YES\n";
else cout << "NO\n";
```

Mental model:

```text
Data → test condition → choose output
```

## 3. Counting Pattern

```text
Input: n numbers
Answer: count positives
```

```cpp
int countPositive = 0;
for (int i = 0; i < n; i++) {
    int x;
    cin >> x;
    if (x > 0) countPositive++;
}
```

Mental model:

```text
Data stream → condition → increment answer
```

## 4. Best Value Pattern

```text
Input: n numbers
Answer: maximum
```

```cpp
int mx;
cin >> mx;

for (int i = 1; i < n; i++) {
    int x;
    cin >> x;
    if (x > mx) mx = x;
}
```

Mental model:

```text
Current best + new value → compare → maybe update best
```

## 5. Simulation Pattern

```text
Given instructions, update state step by step.
```

```cpp
int position = 0;
for (char move : s) {
    if (move == 'L') position--;
    else if (move == 'R') position++;
}
```

Mental model:

```text
State → operation per instruction → final state
```

---

## Codeforces Forms

## Codeforces A Form

Usually direct operation or simple condition.

```text
Input: few numbers
Logic: formula or if/else
Output: number or YES/NO
```

Example form:

```text
Given a and b, check if a is divisible by b.
```

Core C++ needed:

```text
variables + modulo + if/else
```

## Codeforces B Form

Usually loop + condition + count/update.

```text
Input: n and array/string
Logic: scan once
Output: count, max, min, transformed value
```

Core C++ needed:

```text
loop + variable update + condition
```

## Codeforces C Form

Usually the same basics plus an invariant.

```text
Input: larger n
Logic: scan with careful state
Output: optimized answer
```

Core C++ needed:

```text
loop discipline + correct initialization + long long
```

## Codeforces D Form

Usually advanced data structure/algorithm, but still built on this base.

```text
Data structures + operations + controlled loops/search
```

If basics are weak, D problems become impossible to debug.

---

## LeetCode / FAANG Forms

In FAANG interviews, you often write functions instead of full input/output programs.

Contest style:

```cpp
int n;
cin >> n;
vector<int> a(n);
for (int i = 0; i < n; i++) cin >> a[i];
```

Interview style:

```cpp
int solve(vector<int>& a) {
    // logic here
}
```

But the mental model is unchanged:

```text
Input data → process → return answer
```

Example:

```cpp
int countPositive(vector<int>& nums) {
    int ans = 0;

    for (int x : nums) {
        if (x > 0) ans++;
    }

    return ans;
}
```

---

## Brute Force

Brute force means solving directly without optimization.

Example problem:

```text
Given n numbers, find the maximum.
```

Bad beginner brute force idea for fixed small input:

```cpp
if (a >= b && a >= c) cout << a;
else if (b >= a && b >= c) cout << b;
else cout << c;
```

This works only for 3 numbers. It fails as a general method.

Why it fails:

```text
n can be 100000.
Manual conditions do not scale.
```

---

## Better Approach

Use a loop and keep the current best.

```text
Initialize best from real data.
For each new value:
    if value is better, update best.
```

This works for any n.

```cpp
int mx;
cin >> mx;

for (int i = 1; i < n; i++) {
    int x;
    cin >> x;
    if (x > mx) mx = x;
}
```

---

## Optimal Approach

For basic scan problems, one pass is optimal.

Why?

```text
To know the answer, every input value may matter.
So you must inspect each value at least once.
```

Therefore:

```text
Time  : O(n)
Memory: O(1) if no storage needed
```

This is the first optimization idea in CP:

```text
Do not store data unless you need it later.
Process streaming input when possible.
```

---

## Rich ASCII Diagrams

## Program Pipeline

```text
Problem Statement
       |
       v
+---------------+
| Read Input    |
+---------------+
       |
       v
+---------------+
| Store Data    |
| n, x, sum     |
+---------------+
       |
       v
+---------------+
| Apply Logic   |
| +, %, compare |
+---------------+
       |
       v
+---------------+
| Control Flow  |
| if / loop     |
+---------------+
       |
       v
+---------------+
| Print Answer  |
+---------------+
```

## Loop Mental Model

```text
for (int i = 0; i < n; i++)

Start i = 0
    |
    v
Check i < n ? ---- no ----> stop
    |
   yes
    |
    v
Run body
    |
    v
i++
    |
    +------ back to check
```

## Maximum Update

```text
Current mx = 4
New x      = 7

Compare:
7 > 4 ? yes

Update:
mx = 7
```

```text
+------+       +------+
| mx=4 |  -->  | mx=7 |
+------+       +------+
    ^              ^
 old best       new best
```

---

## Step-by-Step Dry Run #1: Sum of N Numbers

Problem:

```text
Read n numbers and print their sum.
```

Input:

```text
5
2 4 -1 3 6
```

Code:

```cpp
int n;
cin >> n;

long long sum = 0;

for (int i = 0; i < n; i++) {
    int x;
    cin >> x;
    sum += x;
}

cout << sum << '\n';
```

Variable tracking:

| i | x | sum before | operation | sum after |
|---:|---:|---:|---|---:|
| 0 | 2 | 0 | 0 + 2 | 2 |
| 1 | 4 | 2 | 2 + 4 | 6 |
| 2 | -1 | 6 | 6 - 1 | 5 |
| 3 | 3 | 5 | 5 + 3 | 8 |
| 4 | 6 | 8 | 8 + 6 | 14 |

Output:

```text
14
```

Mental model:

```text
Data        : n, x, sum
Operation   : sum += x
Control     : loop n times
Answer      : sum
```

---

## Step-by-Step Dry Run #2: Count Even Numbers

Problem:

```text
Given n numbers, count how many are even.
```

Input:

```text
6
2 5 8 9 10 11
```

Code:

```cpp
int n;
cin >> n;

int evenCount = 0;

for (int i = 0; i < n; i++) {
    int x;
    cin >> x;

    if (x % 2 == 0) {
        evenCount++;
    }
}

cout << evenCount << '\n';
```

Variable tracking:

| i | x | x % 2 | even? | evenCount |
|---:|---:|---:|---|---:|
| 0 | 2 | 0 | yes | 1 |
| 1 | 5 | 1 | no | 1 |
| 2 | 8 | 0 | yes | 2 |
| 3 | 9 | 1 | no | 2 |
| 4 | 10 | 0 | yes | 3 |
| 5 | 11 | 1 | no | 3 |

Output:

```text
3
```

---

## Step-by-Step Dry Run #3: Maximum Value

Problem:

```text
Given n numbers, print the maximum.
```

Input:

```text
5
-4 -10 -2 -7 -3
```

Correct code:

```cpp
int n;
cin >> n;

int mx;
cin >> mx;

for (int i = 1; i < n; i++) {
    int x;
    cin >> x;

    if (x > mx) {
        mx = x;
    }
}

cout << mx << '\n';
```

Variable tracking:

| Step | x | mx before | x > mx? | mx after |
|---|---:|---:|---|---:|
| first | -4 | ? | initialize | -4 |
| i=1 | -10 | -4 | no | -4 |
| i=2 | -2 | -4 | yes | -2 |
| i=3 | -7 | -2 | no | -2 |
| i=4 | -3 | -2 | no | -2 |

Output:

```text
-2
```

Important lesson:

```text
Do not initialize maximum as 0 when numbers can be negative.
```

---

## Fully Commented C++17 Code

```cpp
#include <bits/stdc++.h>
using namespace std;

/*
    MiniCPP Mental Model:
    DATA -> OPERATION -> CONTROL FLOW -> ANSWER

    Problem:
    For each test case, read n numbers and print:
    1. sum of all numbers
    2. maximum number
    3. count of even numbers

    This demonstrates:
    - variables
    - data types
    - operators
    - if condition
    - for loop
    - long long for safe sum
*/

void solve() {
    int n;
    cin >> n;

    long long sum = 0;  // use long long because sum may exceed int

    int mx;
    cin >> mx;          // initialize maximum from actual input
    sum += mx;

    int evenCount = 0;

    if (mx % 2 == 0) {
        evenCount++;
    }

    for (int i = 1; i < n; i++) {
        int x;
        cin >> x;

        // Operation 1: add current value to sum
        sum += x;

        // Operation 2: update maximum if current value is larger
        if (x > mx) {
            mx = x;
        }

        // Operation 3: count even values
        if (x % 2 == 0) {
            evenCount++;
        }
    }

    cout << sum << " " << mx << " " << evenCount << '\n';
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

## Internal Execution Walkthrough

Input:

```text
1
5
-4 -10 -2 -7 -3
```

Execution:

```text
t = 1
solve() starts

n = 5

read mx = -4
sum = -4
evenCount = 1 because -4 is even

loop from i = 1 to 4
```

Tracking:

| i | x | sum | mx | evenCount |
|---:|---:|---:|---:|---:|
| init | -4 | -4 | -4 | 1 |
| 1 | -10 | -14 | -4 | 2 |
| 2 | -2 | -16 | -2 | 3 |
| 3 | -7 | -23 | -2 | 3 |
| 4 | -3 | -26 | -2 | 3 |

Output:

```text
-26 -2 3
```

---

## Complexity Analysis

For each test case:

```text
Time Complexity  : O(n)
Memory Complexity: O(1)
```

Why O(n)?

```text
We read each number once.
Each update is O(1).
Total work grows linearly with n.
```

Why O(1) memory?

```text
We do not store the entire array.
We only keep sum, mx, evenCount, and current x.
```

---

## Edge Cases

## 1. Single Element

```text
n = 1
value = 5
```

Loop should still work.

Output:

```text
sum = 5
max = 5
evenCount = 0
```

## 2. All Negative

```text
-5 -2 -9
```

Maximum is `-2`, not `0`.

## 3. All Zero

```text
0 0 0
```

Zero is even. If counting positive numbers, zero is not positive.

## 4. Large Sum

```text
n = 200000
each value = 1000000000
```

Sum exceeds int. Use `long long`.

## 5. Multiple Test Cases

Always reset variables inside `solve()`.

Wrong:

```cpp
long long sum = 0; // outside solve and not reset
```

Correct:

```cpp
void solve() {
    long long sum = 0;
}
```

---

## Common Mistakes

## Mistake 1: Off-by-One Loop

Wrong:

```cpp
for (int i = 0; i <= n; i++)
```

This runs `n + 1` times.

Correct:

```cpp
for (int i = 0; i < n; i++)
```

## Mistake 2: Wrong Maximum Initialization

Wrong:

```cpp
int mx = 0;
```

Fails for all negative values.

Correct:

```cpp
int mx;
cin >> mx;
```

## Mistake 3: Integer Overflow

Wrong:

```cpp
int sum = 0;
```

Correct:

```cpp
long long sum = 0;
```

## Mistake 4: Assignment Instead of Comparison

Wrong:

```cpp
if (x = 0)
```

Correct:

```cpp
if (x == 0)
```

## Mistake 5: Not Reading Input Correctly

If problem gives `n` then `n` numbers, read exactly that.

```cpp
cin >> n;
for (int i = 0; i < n; i++) cin >> x;
```

---

## Common WA/TLE/Runtime/Overflow Bugs

## WA

```text
Wrong comparison
Wrong loop boundary
Wrong initialization
Wrong output format
Forgetting test cases
Counting zero incorrectly
```

## TLE

```text
Using nested loops unnecessarily
Infinite loop
Printing too much
Not using fast I/O in large input
```

## Runtime Error

```text
Division by zero
Array out of bounds
Reading missing input
Accessing empty vector
```

## Overflow

```text
int cannot store very large sums/products.
Use long long.
Use 1LL * a * b for multiplication.
```

Example:

```cpp
long long product = 1LL * a * b;
```

---

## Debugging Checklist

Before submitting:

```text
[ ] Did I read input in the correct order?
[ ] Did I reset variables for every test case?
[ ] Did I choose long long for large answers?
[ ] Are my loop boundaries correct?
[ ] Did I handle n = 1?
[ ] Did I handle negative values?
[ ] Did I print exactly the required format?
[ ] Did I test a custom edge case?
```

When stuck, print variable states locally:

```cpp
cerr << "i=" << i << " x=" << x << " sum=" << sum << '\n';
```

Do not leave debug prints in final submission.

---

## Similar Problems

Practice these beginner forms:

```text
1. Sum of n numbers
2. Count even numbers
3. Count positive numbers
4. Find maximum
5. Find minimum
6. Check if number is divisible by k
7. Print YES/NO based on condition
8. Simulate movement L/R/U/D
9. Count vowels in a string
10. Find average using sum
```

All use the same mental model.

---

## Pattern Expansion

This chapter expands into future CP topics:

```text
C++ Basics
│
├── STL
│   └── Data becomes vector/set/map
│
├── Prefix Sum
│   └── Operation becomes cumulative addition
│
├── Two Pointers
│   └── Control flow becomes left/right movement
│
├── Binary Search
│   └── Control flow becomes halve search space
│
├── Graph BFS
│   └── Data becomes queue + visited array
│
└── DP
    └── Data becomes states and transitions
```

The base never changes:

```text
Data → Operation → Control Flow → Answer
```

---

## Cheat Sheet

## Basic Types

```cpp
int x = 10;
long long big = 10000000000LL;
double d = 3.14;
char c = 'A';
bool ok = true;
string s = "hello";
```

## Input / Output

```cpp
cin >> x;
cout << x << '\n';
```

## If / Else

```cpp
if (x > 0) {
    cout << "positive\n";
} else {
    cout << "not positive\n";
}
```

## For Loop

```cpp
for (int i = 0; i < n; i++) {
    // repeated work
}
```

## Sum Pattern

```cpp
long long sum = 0;
for (int i = 0; i < n; i++) {
    int x;
    cin >> x;
    sum += x;
}
```

## Count Pattern

```cpp
int cnt = 0;
for (int i = 0; i < n; i++) {
    int x;
    cin >> x;
    if (x > 0) cnt++;
}
```

## Max Pattern

```cpp
int mx;
cin >> mx;

for (int i = 1; i < n; i++) {
    int x;
    cin >> x;
    if (x > mx) mx = x;
}
```

## CP Template

```cpp
#include <bits/stdc++.h>
using namespace std;

void solve() {
    // logic for one test case
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

## One Picture To Remember

```text
                 EVERY CP / DSA SOLUTION

        +-------------------------------+
        |           INPUT DATA          |
        | n, array, string, queries     |
        +---------------+---------------+
                        |
                        v
        +-------------------------------+
        |             DATA              |
        | variables, counters, best     |
        +---------------+---------------+
                        |
                        v
        +-------------------------------+
        |          OPERATION            |
        | add, compare, count, update   |
        +---------------+---------------+
                        |
                        v
        +-------------------------------+
        |        CONTROL FLOW           |
        | if, for, while, function      |
        +---------------+---------------+
                        |
                        v
        +-------------------------------+
        |            ANSWER             |
        | print or return result        |
        +-------------------------------+

ONE LINE:

Data is stored, operations transform it, control flow decides when it happens.
```

---

## Final Takeaway

For CP and FAANG DSA, do not learn C++ like a full software engineering language first.

Start with this mental model:

```text
Data → Operation → Control Flow → Answer
```

Then practice recognizing small forms:

```text
sum
count
max/min
condition
simulation
```

Once this is natural, STL, prefix sum, binary search, graph, and DP become much easier because they are all built from the same foundation.
