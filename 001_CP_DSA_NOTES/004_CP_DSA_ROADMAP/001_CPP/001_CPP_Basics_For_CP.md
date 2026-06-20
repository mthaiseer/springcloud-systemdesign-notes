# 001_CPP_Basics_For_CP.md

# C++ Basics for Competitive Programming: Data + Operations + Control Flow

## Why This Exists

Competitive Programming and FAANG DSA interviews look scary at first because the problems talk about arrays, graphs, dynamic programming, greedy choices, binary search, trees, strings, heaps, and many other techniques. But underneath every single problem, the computer is doing only one simple thing:

```text
Take input data
    ↓
Store it in variables
    ↓
Apply operations
    ↓
Control the execution path
    ↓
Produce output
```

This chapter exists to build the foundation before STL, math, prefix sum, binary search, graph, and DP.

The goal is not to memorize C++ syntax. The goal is to build the mental model that every C++ contest program is built from three parts:

```text
DATA + OPERATIONS + CONTROL FLOW
```

Once this becomes natural, every future topic becomes easier.

Prefix Sum is data plus addition plus loop control.  
Binary Search is data plus comparison plus controlled narrowing.  
Graph BFS is data plus queue operations plus loop control.  
DP is data plus recurrence operations plus iteration order.

So this chapter teaches one core mental model only:

```text
Every CP solution is data moving through operations under control flow.
```

---

## Problem Statement

When beginners start C++ for CP, they often ask:

```text
What should I learn first?
How much C++ is enough?
Do I need OOP?
Do I need pointers?
Do I need templates?
Why does my code give WA/TLE/RE?
```

For contests and interviews, the first problem is not advanced C++. The first problem is this:

```text
Can you read input, store data, transform it correctly, and output the answer?
```

Most beginner mistakes come from weak basics:

```text
Wrong data type      → overflow
Wrong loop boundary  → off-by-one WA
Wrong condition      → incorrect branch
Wrong initialization → garbage value
Wrong complexity     → TLE
Wrong division       → runtime error
```

So the first C++ chapter must teach how a program thinks.

A C++ contest program is not magic. It is a pipeline:

```text
Input
  ↓
Variables
  ↓
Operators
  ↓
Conditions
  ↓
Loops
  ↓
Functions
  ↓
Output
```

If you understand this pipeline, you can debug most beginner problems.

---

## Mental Model

The only mental model for this chapter is:

# Data + Operations + Control Flow

```text
DATA              = what you store
OPERATIONS        = what you do to data
CONTROL FLOW      = when and how many times you do it
```

In C++:

```text
Variables store data.
Operators transform data.
if/else chooses paths.
loops repeat work.
functions organize repeated logic.
```

A full CP solution is usually:

```text
read n
read array
initialize answer
loop over data
    update answer
print answer
```

ASCII mental model:

```text
+------------------+
|      INPUT       |
+------------------+
          |
          v
+------------------+
|       DATA       |
| variables/array  |
+------------------+
          |
          v
+------------------+
|    OPERATIONS    |
| + - * / % compare|
+------------------+
          |
          v
+------------------+
|  CONTROL FLOW    |
| if / for / while |
+------------------+
          |
          v
+------------------+
|      OUTPUT      |
+------------------+
```

A problem statement is converted into this model.

Example:

```text
Given two numbers, print their sum.
```

Becomes:

```text
Data        : a, b
Operation   : a + b
Control flow: direct execution once
Output      : sum
```

Example:

```text
Given n numbers, print the maximum.
```

Becomes:

```text
Data        : n, array values, current maximum
Operation   : comparison
Control flow: loop n times
Output      : maximum
```

Example:

```text
Given n, print numbers from 1 to n.
```

Becomes:

```text
Data        : n, i
Operation   : i increases by 1
Control flow: loop while i <= n
Output      : each i
```

This is the whole beginning of C++ for CP.

---

## Real World Analogy

Imagine a restaurant kitchen.

```text
Customer Order
      ↓
Ingredients
      ↓
Cooking Steps
      ↓
Decision Checks
      ↓
Repeated Actions
      ↓
Final Dish
```

Map this to C++:

```text
Customer order  → input
Ingredients     → variables/data
Cooking steps   → operations
Decision checks → if/else
Repeated actions→ loops
Final dish      → output
```

Restaurant example:

```text
Order: Make 3 omelettes.

Data:
- eggs = 6
- salt = yes
- pan = hot

Operations:
- crack eggs
- mix
- cook

Control Flow:
- repeat for 3 omelettes
- if pan too hot, reduce heat

Output:
- 3 omelettes
```

C++ example:

```text
Problem: Read n numbers and count positives.

Data:
- n
- x
- count

Operations:
- compare x > 0
- count++

Control Flow:
- repeat n times
- if x positive, update count

Output:
- count
```

Everything is the same pattern.

---

## Contest Recognition Pattern

Whenever you read a CP or FAANG problem, immediately ask:

```text
1. What is the input data?
2. What do I need to store?
3. What operation changes the answer?
4. What condition decides the path?
5. What loop repeats the logic?
6. What should be printed or returned?
```

### Universal Contest Skeleton

```text
read input
prepare data
process data
print answer
```

### Codeforces A Form

Usually simple data + simple operation + direct control flow.

```text
Input: a, b
Operation: compare/sum/difference
Control: if/else
Output: YES/NO or number
```

### Codeforces B Form

Usually loop + condition + counting/transformation.

```text
Input: n, array/string
Operation: count/check/update
Control: loop + if
Output: answer
```

### Codeforces C Form

Usually smarter operation or optimized control.

```text
Input: array/string
Operation: prefix, greedy, sort, math
Control: loop with invariant
Output: optimized answer
```

### Codeforces D Form

Usually deeper invariant, graph, DP, binary search, or data structure.

```text
Input: large constraints
Operation: optimized state transition
Control: carefully ordered loops/search
Output: result under constraints
```

### LeetCode Form

Usually function-based instead of stdin/stdout:

```text
Input: function parameters
Operation: algorithm
Control: loops/recursion
Output: return value
```

But the mental model is the same.

---

## Core Concepts

## 1. Variables = Named Boxes for Data

A variable is a named place where data is stored.

```cpp
int age = 47;
long long score = 10000000000LL;
char grade = 'A';
bool ok = true;
```

Mental model:

```text
+---------+
| age     |
|   47    |
+---------+
```

A variable has:

```text
name  → how you refer to it
type  → what kind of data it stores
value → actual stored content
```

In CP:

```cpp
int n;
cin >> n;
```

Means:

```text
Create box named n.
Read value from input.
Store it inside n.
```

---

## 2. Data Types = Size and Meaning

Common contest types:

```text
int        → normal integer, around ±2 billion
long long  → large integer, around ±9e18
double     → decimal
char       → single character
string     → text
bool       → true/false
```

For CP, the most important rule:

```text
If multiplication or large sum is possible, use long long.
```

Bad:

```cpp
int x = 1000000000;
int y = 1000000000;
int z = x + y + y; // may overflow
```

Good:

```cpp
long long x = 1000000000LL;
long long y = 1000000000LL;
long long z = x + y + y;
```

---

## 3. Operators = Actions on Data

Arithmetic:

```text
+ addition
- subtraction
* multiplication
/ division
% remainder
```

Comparison:

```text
== equal
!= not equal
<  less than
>  greater than
<= less or equal
>= greater or equal
```

Logical:

```text
&& AND
|| OR
!  NOT
```

Example:

```cpp
if (x % 2 == 0) {
    cout << "even";
}
```

Meaning:

```text
Data: x
Operation: x % 2
Condition: result == 0
Control: choose branch
```

---

## 4. Conditions = Choose Path

```cpp
if (condition) {
    // run when true
} else {
    // run when false
}
```

Mental diagram:

```text
          condition
             |
       +-----+-----+
       |           |
     true        false
       |           |
       v           v
   do this      do that
```

Example:

```cpp
if (a > b) {
    cout << a;
} else {
    cout << b;
}
```

This chooses the maximum of two numbers.

---

## 5. Loops = Repeat Work

The most common CP loop:

```cpp
for (int i = 0; i < n; i++) {
    // work
}
```

Loop mental model:

```text
initialize i = 0
check i < n
run body
increase i
repeat
```

ASCII:

```text
+---------+
| i = 0   |
+---------+
     |
     v
+------------+
| i < n ?    |
+------------+
   | yes
   v
+------------+
| body runs  |
+------------+
   |
   v
+------------+
| i++        |
+------------+
   |
   +------ back to condition
```

---

## 6. Functions = Named Logic

Functions help avoid repeated code.

```cpp
int square(int x) {
    return x * x;
}
```

Mental model:

```text
input x
  ↓
operation x*x
  ↓
return result
```

In CP, functions are useful for:

```text
checking condition
computing answer
cleaning solve()
writing reusable logic
```

Example:

```cpp
bool isEven(int x) {
    return x % 2 == 0;
}
```

---

## Internal Working

A C++ program is converted by the compiler into machine instructions.

Simple C++:

```cpp
int a = 5;
int b = 7;
int sum = a + b;
```

Internal view:

```text
Memory:
+------+-------+
| name | value |
+------+-------+
| a    | 5     |
| b    | 7     |
| sum  | 12    |
+------+-------+

Instruction flow:
1. store 5 in a
2. store 7 in b
3. read a
4. read b
5. add
6. store result in sum
```

### Expression Evaluation

```cpp
int ans = a + b * c;
```

C++ follows operator precedence:

```text
b * c first
then a + result
```

If:

```text
a = 2, b = 3, c = 4
```

Then:

```text
ans = 2 + 3 * 4
ans = 2 + 12
ans = 14
```

### Condition Execution

```cpp
if (x > 10) {
    cout << "big";
}
```

Internal:

```text
1. read x
2. compare x with 10
3. if true, execute cout
4. if false, skip block
```

### Loop Execution

```cpp
for (int i = 1; i <= 3; i++) {
    cout << i << " ";
}
```

Internal:

```text
i = 1 → condition true → print 1 → i becomes 2
i = 2 → condition true → print 2 → i becomes 3
i = 3 → condition true → print 3 → i becomes 4
i = 4 → condition false → stop
```

---

## Rich ASCII Diagrams

### Full Contest Program Pipeline

```text
                   +----------------------+
                   |  Problem Statement   |
                   +----------+-----------+
                              |
                              v
                   +----------------------+
                   | Understand Input     |
                   +----------+-----------+
                              |
                              v
                   +----------------------+
                   | Choose Data Storage  |
                   | int, long long, vec  |
                   +----------+-----------+
                              |
                              v
                   +----------------------+
                   | Apply Operations     |
                   | + - * / % compare    |
                   +----------+-----------+
                              |
                              v
                   +----------------------+
                   | Control Flow         |
                   | if, for, while       |
                   +----------+-----------+
                              |
                              v
                   +----------------------+
                   | Output Answer        |
                   +----------------------+
```

### Variable Memory Box

```text
Declaration:

int n = 5;

Memory:

+---------+
| n       |
| int     |
| value 5 |
+---------+
```

### Update Operation

```text
Before:

+---------+
| count   |
| 3       |
+---------+

Operation:

count++

After:

+---------+
| count   |
| 4       |
+---------+
```

### If/Else Branch

```text
             x > 0 ?
          +----+----+
          |         |
        true      false
          |         |
          v         v
    count++     do nothing
```

### Loop Over Array

```text
Array:

index:  0   1   2   3   4
value:  5  -1   7   0   3

Loop pointer:

i = 0  → arr[0]
i = 1  → arr[1]
i = 2  → arr[2]
i = 3  → arr[3]
i = 4  → arr[4]
```

---

## Step-by-Step Dry Run #1: Sum of Two Numbers

Problem:

```text
Read two integers a and b. Print their sum.
```

C++:

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int a, b;
    cin >> a >> b;
    int sum = a + b;
    cout << sum << '\n';
    return 0;
}
```

Input:

```text
5 7
```

Execution:

```text
int a, b;        → create boxes a and b
cin >> a >> b;   → a = 5, b = 7
sum = a + b;     → sum = 12
cout << sum;     → print 12
```

Variable tracking:

| Step | a | b | sum | Output |
|---|---:|---:|---:|---|
| Declare | ? | ? | - | - |
| Read | 5 | 7 | - | - |
| Add | 5 | 7 | 12 | - |
| Print | 5 | 7 | 12 | 12 |

Mental model:

```text
Data        : a, b
Operation   : addition
Control flow: straight line
Output      : sum
```

---

## Step-by-Step Dry Run #2: Maximum of Two Numbers

Problem:

```text
Read a and b. Print the bigger number.
```

C++:

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int a, b;
    cin >> a >> b;

    if (a > b) {
        cout << a << '\n';
    } else {
        cout << b << '\n';
    }

    return 0;
}
```

Input:

```text
9 4
```

Execution:

```text
a = 9
b = 4
condition: a > b → 9 > 4 → true
print a → 9
```

Variable tracking:

| Step | a | b | Condition | Output |
|---|---:|---:|---|---|
| Read | 9 | 4 | - | - |
| Check | 9 | 4 | 9 > 4 = true | - |
| Print | 9 | 4 | true branch | 9 |

Input 2:

```text
3 8
```

Execution:

```text
a = 3
b = 8
condition: 3 > 8 → false
print b → 8
```

Mental model:

```text
Data        : a, b
Operation   : comparison
Control flow: if/else
Output      : maximum
```

---

## Step-by-Step Dry Run #3: Print 1 to N

Problem:

```text
Read n. Print numbers from 1 to n.
```

C++:

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    for (int i = 1; i <= n; i++) {
        cout << i << " ";
    }

    cout << '\n';
    return 0;
}
```

Input:

```text
5
```

Loop tracking:

| Iteration | i | Condition i <= n | Output So Far |
|---|---:|---|---|
| Start | 1 | 1 <= 5 true | |
| 1 | 1 | true | 1 |
| 2 | 2 | true | 1 2 |
| 3 | 3 | true | 1 2 3 |
| 4 | 4 | true | 1 2 3 4 |
| 5 | 5 | true | 1 2 3 4 5 |
| Stop | 6 | 6 <= 5 false | 1 2 3 4 5 |

Mental model:

```text
Data        : n, i
Operation   : print i, i++
Control flow: for loop
Output      : sequence
```

---

## Step-by-Step Dry Run #4: Count Positive Numbers

Problem:

```text
Given n integers, count how many are positive.
```

Input:

```text
5
-2 3 0 7 -1
```

C++:

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    int countPositive = 0;

    for (int i = 0; i < n; i++) {
        int x;
        cin >> x;

        if (x > 0) {
            countPositive++;
        }
    }

    cout << countPositive << '\n';
    return 0;
}
```

Tracking:

| i | x | x > 0? | countPositive |
|---:|---:|---|---:|
| 0 | -2 | false | 0 |
| 1 | 3 | true | 1 |
| 2 | 0 | false | 1 |
| 3 | 7 | true | 2 |
| 4 | -1 | false | 2 |

Output:

```text
2
```

Mental model:

```text
Data        : n, x, countPositive
Operation   : compare x > 0, increment
Control flow: loop + if
Output      : count
```

---

## Brute Force

For C++ basics, brute force means writing the most direct version of logic.

Example:

```text
Print maximum of 3 numbers.
```

Naive direct thinking:

```cpp
if (a >= b && a >= c) cout << a;
else if (b >= a && b >= c) cout << b;
else cout << c;
```

Why it works:

```text
It checks every possibility directly.
```

Why it can become messy:

```text
For 100 numbers, writing conditions manually is impossible.
```

So we need loops.

---

## Better Approach

Use repeated logic.

Maximum of n numbers:

```cpp
int mx = firstValue;

for each next value:
    if value > mx:
        mx = value
```

This uses the same mental model:

```text
Data        : current value, current maximum
Operation   : comparison and update
Control flow: loop
```

---

## Optimal Approach

For basic maximum/count/sum problems, the optimal approach is usually one pass.

```text
Read each item once.
Update answer immediately.
Do not store unnecessary data.
```

Example:

```cpp
int n;
cin >> n;

int mx;
cin >> mx;

for (int i = 1; i < n; i++) {
    int x;
    cin >> x;
    if (x > mx) mx = x;
}

cout << mx << '\n';
```

Complexity:

```text
Time  : O(n)
Memory: O(1)
```

This is optimal because every number must be seen at least once.

---

## Fully Commented C++17 Code

```cpp
#include <bits/stdc++.h>
using namespace std;

/*
    This program demonstrates the core CP mental model:

    DATA + OPERATIONS + CONTROL FLOW

    Problem:
    Given n integers, compute:
    1. sum of all numbers
    2. maximum number
    3. count of positive numbers

    This is beginner-friendly but also shows the universal CP structure:
    input -> process -> output
*/

int main() {
    // Fast enough for beginner examples.
    // Full fast I/O will be covered deeply in a later chapter.
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    // DATA: n stores how many numbers we will read.
    int n;
    cin >> n;

    // DATA: variables used to build answers.
    long long sum = 0;       // long long avoids overflow for large sums.
    int mx;                  // maximum value seen so far.
    int positiveCount = 0;   // number of positive integers.

    // Read the first number separately so mx has a real value.
    int x;
    cin >> x;

    // OPERATION: update sum with first value.
    sum += x;

    // DATA: initialize maximum using first input.
    mx = x;

    // OPERATION + CONTROL FLOW: check positivity.
    if (x > 0) {
        positiveCount++;
    }

    // CONTROL FLOW: process remaining n - 1 numbers.
    for (int i = 1; i < n; i++) {
        cin >> x;

        // OPERATION: add current number to sum.
        sum += x;

        // CONTROL FLOW: if current number is bigger, update maximum.
        if (x > mx) {
            mx = x;
        }

        // CONTROL FLOW: if current number is positive, count it.
        if (x > 0) {
            positiveCount++;
        }
    }

    // OUTPUT: print computed results.
    cout << "sum = " << sum << '\n';
    cout << "max = " << mx << '\n';
    cout << "positive = " << positiveCount << '\n';

    return 0;
}
```

Example input:

```text
5
-2 3 0 7 -1
```

Output:

```text
sum = 7
max = 7
positive = 2
```

---

## Internal Execution Walkthrough

Input:

```text
5
-2 3 0 7 -1
```

Initial:

```text
n = 5
sum = 0
positiveCount = 0
```

Read first x:

```text
x = -2
sum = -2
mx = -2
x > 0 false
positiveCount = 0
```

Loop starts from i = 1.

### i = 1

```text
x = 3
sum = -2 + 3 = 1
x > mx → 3 > -2 true → mx = 3
x > 0 true → positiveCount = 1
```

### i = 2

```text
x = 0
sum = 1 + 0 = 1
x > mx → 0 > 3 false
x > 0 false
```

### i = 3

```text
x = 7
sum = 1 + 7 = 8
x > mx → 7 > 3 true → mx = 7
x > 0 true → positiveCount = 2
```

### i = 4

```text
x = -1
sum = 8 - 1 = 7
x > mx → -1 > 7 false
x > 0 false
```

Final:

```text
sum = 7
mx = 7
positiveCount = 2
```

---

## Variable Tracking Table

| Step | i | x | sum | mx | positiveCount |
|---|---:|---:|---:|---:|---:|
| Start | - | - | 0 | ? | 0 |
| First read | - | -2 | -2 | -2 | 0 |
| Loop 1 | 1 | 3 | 1 | 3 | 1 |
| Loop 2 | 2 | 0 | 1 | 3 | 1 |
| Loop 3 | 3 | 7 | 8 | 7 | 2 |
| Loop 4 | 4 | -1 | 7 | 7 | 2 |

This table is how you should mentally debug simple CP code.

---

## Edge Cases

### Edge Case 1: n = 1

Input:

```text
1
5
```

The loop from `i = 1` to `i < n` does not run.

Still correct:

```text
sum = 5
mx = 5
positive = 1
```

### Edge Case 2: All Negative

Input:

```text
4
-8 -3 -10 -1
```

Correct maximum is `-1`, not `0`.

Mistake:

```cpp
int mx = 0; // wrong for all negative input
```

Correct:

```cpp
cin >> mx; // initialize from real data
```

### Edge Case 3: Zero Values

Zero is not positive and not negative.

```cpp
if (x > 0) positiveCount++;
```

Do not write:

```cpp
if (x >= 0) positiveCount++; // counts zero also
```

### Edge Case 4: Large Sum

If n is large and values are large:

```text
n = 200000
x = 1000000000
```

Sum can be:

```text
200000 * 1000000000 = 200000000000000
```

This does not fit in int.

Use:

```cpp
long long sum = 0;
```

---

## Failure Scenarios

## Wrong Answer

WA usually means logic is wrong.

Common examples:

```cpp
for (int i = 0; i <= n; i++) // runs n+1 times
```

Should be:

```cpp
for (int i = 0; i < n; i++)
```

Another WA:

```cpp
if (x >= 0) positiveCount++;
```

But problem asks positive, so zero should not count.

---

## Time Limit Exceeded

TLE means your code takes too long.

Beginner example:

```cpp
for (int i = 1; i <= n; i++) {
    for (int j = 1; j <= n; j++) {
        cout << i << " " << j << '\n';
    }
}
```

If n = 200000, this is impossible.

Basic rule:

```text
n up to 1e5 or 2e5 → usually O(n) or O(n log n)
n up to 1e3       → O(n^2) may be okay
n up to 20        → exponential may be okay
```

---

## Runtime Error

Runtime error means program crashes.

Common examples:

### Division by zero

```cpp
int ans = a / b; // if b = 0, crash
```

### Invalid array index

```cpp
vector<int> a(n);
cout << a[n]; // invalid, last index is n-1
```

### Infinite recursion

Will be covered later, but it can crash stack memory.

---

## Compilation Error

Compilation error means C++ cannot understand your code.

Examples:

```cpp
int x = 5
cout << x;
```

Missing semicolon.

Correct:

```cpp
int x = 5;
cout << x;
```

Another:

```cpp
cout << y;
```

But `y` was never declared.

---

## Common WA/TLE/Runtime/Overflow Bugs

## WA Bugs

```text
1. Off-by-one loop
2. Wrong comparison sign
3. Not resetting answer per test case
4. Wrong initialization
5. Misreading input
6. Confusing positive with non-negative
7. Integer division surprises
```

Example:

```cpp
int avg = sum / n;
```

If `sum` and `n` are int, result is integer division.

---

## TLE Bugs

```text
1. Nested loops when n is large
2. Printing too much
3. Recomputing same value
4. Using slow approach despite constraints
5. Infinite loop
```

Infinite loop:

```cpp
int i = 0;
while (i < n) {
    cout << i;
    // forgot i++
}
```

---

## Runtime Error Bugs

```text
1. Division by zero
2. Out-of-bounds array access
3. Negative index
4. Stack overflow
5. Accessing empty vector
```

Example:

```cpp
vector<int> v;
cout << v[0]; // runtime error or undefined behavior
```

---

## Overflow Bugs

```cpp
int a = 1000000000;
int b = 1000000000;
int c = a + b + b;
```

This may overflow.

Correct:

```cpp
long long a = 1000000000LL;
long long b = 1000000000LL;
long long c = a + b + b;
```

Important:

```cpp
long long ans = a * b;
```

If `a` and `b` are int, multiplication happens as int first.

Safer:

```cpp
long long ans = 1LL * a * b;
```

---

## Debugging Mindset

A strong CP debugger asks:

```text
1. What data do I have?
2. What operation am I applying?
3. Which control path is running?
4. How does the variable change after each step?
5. Which edge case breaks my assumption?
```

### Debugging Checklist

Before submitting:

```text
[ ] Did I use long long where needed?
[ ] Are loop bounds correct?
[ ] Did I handle n = 1?
[ ] Did I handle all negative values?
[ ] Did I reset variables inside each test case?
[ ] Did I avoid division by zero?
[ ] Did I print exactly what problem asks?
[ ] Did I test sample manually?
```

### Manual Dry Run Habit

For every beginner problem, dry run at least one custom case:

```text
smallest input
largest-looking input
all equal values
negative values
zero values
mixed values
```

---

## Contest Tips

### Tip 1: First Convert Problem to Pipeline

```text
Input → Data → Operation → Control → Output
```

Do not jump to code immediately.

### Tip 2: Name Variables Clearly

Bad:

```cpp
int a,b,c,d;
```

Good for learning:

```cpp
int n;
int positiveCount;
long long sum;
```

In contests, short names are okay after you understand.

### Tip 3: Use long long Often

For sums, products, counts, and answers:

```cpp
long long ans = 0;
```

### Tip 4: Keep solve() Clean

Later you will use:

```cpp
void solve() {
    // one test case logic
}
```

For now, understand main flow first.

### Tip 5: Read Constraints

Constraints decide control flow complexity.

```text
n <= 100       → many approaches work
n <= 2e5       → need efficient loop/sort
n <= 1e9       → cannot loop over all n
```

---

## FAANG Interview Discussion

In FAANG interviews, you usually do not write full stdin/stdout code. You write a function. But the mental model is identical.

Example interview problem:

```text
Given an array, return the maximum value.
```

Function version:

```cpp
int findMax(vector<int>& nums) {
    int mx = nums[0];

    for (int x : nums) {
        if (x > mx) {
            mx = x;
        }
    }

    return mx;
}
```

Mental model:

```text
Data        : nums, mx, x
Operation   : comparison
Control flow: loop
Output      : return mx
```

Interviewers care about basics because advanced algorithms are built from them.

They check:

```text
Can you reason about variables?
Can you explain loop invariants?
Can you handle edge cases?
Can you avoid overflow?
Can you write clean logic?
Can you dry run?
```

A candidate who cannot dry run a simple loop will struggle with binary search and DP.

---

## Interview Q&A

### Q1. What is the core structure of a CP solution?

Strong answer:

```text
A CP solution usually reads input, stores necessary data, applies operations under control flow such as loops and conditions, then prints the answer. Internally, most algorithms are data plus operations plus control flow.
```

### Q2. Why use long long in contests?

Strong answer:

```text
Because constraints often allow values or sums beyond the int range. int is around 2e9, while long long supports around 9e18. For sums, products, and final answers, long long prevents overflow.
```

### Q3. What causes off-by-one errors?

Strong answer:

```text
Off-by-one errors happen when loop boundaries do not match valid indices or required count. For 0-indexed arrays of size n, valid indices are 0 to n-1, so the loop should usually be i < n, not i <= n.
```

### Q4. Why initialize maximum from the first value?

Strong answer:

```text
If all values are negative, initializing maximum as 0 gives the wrong answer. Initializing from real input ensures the answer always belongs to the data.
```

### Q5. What is the difference between compile error and runtime error?

Strong answer:

```text
A compile error means the code syntax or declarations are invalid and the program cannot be built. A runtime error happens after the program starts, usually due to invalid memory access, division by zero, or similar crashes.
```

### Q6. How do you debug a wrong answer?

Strong answer:

```text
I dry run small cases, track variable values, verify loop boundaries, test edge cases, and compare expected output with actual output. I specifically check initialization, conditions, and overflow.
```

---

## Similar Problem Patterns

These beginner patterns all use the same mental model:

### Sum Pattern

```text
Data: numbers
Operation: addition
Control: loop
```

### Count Pattern

```text
Data: numbers/items
Operation: condition check + increment
Control: loop + if
```

### Maximum Pattern

```text
Data: current maximum and current value
Operation: comparison + update
Control: loop
```

### Minimum Pattern

```text
Data: current minimum and current value
Operation: comparison + update
Control: loop
```

### YES/NO Pattern

```text
Data: input condition
Operation: logical check
Control: if/else
```

### Simulation Pattern

```text
Data: state
Operation: update state
Control: repeat steps
```

---

## Pattern Expansion

This chapter expands into every future MiniCPP and MiniDSA chapter.

```text
C++ Basics
   ↓
STL
   ↓
Arrays / Strings
   ↓
Prefix Sum
   ↓
Two Pointers
   ↓
Binary Search
   ↓
Graphs
   ↓
DP
```

Examples:

### Prefix Sum

```text
Data        : array, prefix array
Operation   : prefix[i] = prefix[i-1] + a[i]
Control flow: loop
```

### Binary Search

```text
Data        : low, high, mid
Operation   : compare mid condition
Control flow: while loop halves search space
```

### BFS

```text
Data        : graph, queue, visited
Operation   : push/pop/mark
Control flow: while queue not empty
```

### DP

```text
Data        : dp states
Operation   : recurrence
Control flow: correct iteration order
```

Same mental model. More advanced data and operations.

---

## Complexity Cheat Sheet

| Code Shape | Complexity | Example |
|---|---:|---|
| Single statement | O(1) | `x++` |
| One loop over n | O(n) | sum array |
| Two nested loops | O(n²) | all pairs |
| Loop halves each time | O(log n) | binary search |
| Sort | O(n log n) | `sort(v.begin(), v.end())` |
| Loop + sort | O(n log n) | sort then scan |

Beginner contest rule:

```text
Always compare your loops with constraints.
```

Rough guide:

```text
1e8 operations may be too much.
1e6 to 1e7 is usually safer.
```

---

## Recognition Cheat Sheet

When reading a problem:

```text
If problem asks total       → sum variable
If problem asks how many    → count variable
If problem asks largest     → max variable
If problem asks smallest    → min variable
If problem asks possible    → bool flag
If problem asks repeat      → loop
If problem asks choose      → if/else
If problem has many tests   → test case loop
```

C++ starter template:

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    // code here

    return 0;
}
```

Multi-test template:

```cpp
#include <bits/stdc++.h>
using namespace std;

void solve() {
    // one test case
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
                 COMPETITIVE PROGRAMMING PROGRAM

        +------------------------------------------------+
        |                 PROBLEM STATEMENT              |
        +-----------------------+------------------------+
                                |
                                v
        +------------------------------------------------+
        |                    INPUT DATA                  |
        |        n, array, string, graph, queries         |
        +-----------------------+------------------------+
                                |
                                v
        +------------------------------------------------+
        |                 STORE AS DATA                  |
        |        variables, arrays, vectors, strings      |
        +-----------------------+------------------------+
                                |
                                v
        +------------------------------------------------+
        |                 APPLY OPERATIONS               |
        |       +, -, *, /, %, compare, update, count     |
        +-----------------------+------------------------+
                                |
                                v
        +------------------------------------------------+
        |                 CONTROL FLOW                   |
        |       if/else, for, while, function calls       |
        +-----------------------+------------------------+
                                |
                                v
        +------------------------------------------------+
        |                   ANSWER                       |
        |              print / return result              |
        +------------------------------------------------+

        ONE SENTENCE:

        Every C++ CP solution is data transformed by operations
        under control flow until the answer appears.
```

---

## Final MiniCPP Takeaway

Do not memorize C++ as random syntax.

Remember this:

```text
Variables hold data.
Operators change data.
Conditions choose paths.
Loops repeat work.
Functions organize logic.
Algorithms are built from these pieces.
```

When stuck, ask:

```text
What data do I need?
What operation updates the answer?
What control flow repeats or chooses the logic?
```

This is the first mental model of C++ for CP and FAANG interviews.
