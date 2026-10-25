# Recursion Level 2 — Recursion Tree + Recursive Call Stack
## TLE-Style Self-Study Notes with Prerequisites, ASCII Diagrams, Dry Runs, and C++17

> **Lecture focus:** Recursion, Recursion Tree, and Recursive Call Stack.
>
> **Self-study goal:** Do not just memorize recursive syntax. Learn to answer:
>
> 1. What does the recursive function mean?
> 2. What is the smaller subproblem?
> 3. What is the base case?
> 4. What happens while calls go **down**?
> 5. What happens while returned values come **back up**?
> 6. What does the **complete recursion tree** look like?
> 7. What does the **currently active call stack** look like?

---

# Table of Contents

1. [Lecture Roadmap](#1-lecture-roadmap)
2. [Prerequisite 1 — Ordinary Functions](#2-prerequisite-1--ordinary-functions)
3. [Prerequisite 2 — Function Parameters, Return Values, and Stack Frames](#3-prerequisite-2--function-parameters-return-values-and-stack-frames)
4. [Prerequisite 3 — Problem State and Smaller Subproblem](#4-prerequisite-3--problem-state-and-smaller-subproblem)
5. [Prerequisite 4 — Base Case and Progress](#5-prerequisite-4--base-case-and-progress)
6. [Prerequisite 5 — Recurrence Relation](#6-prerequisite-5--recurrence-relation)
7. [What Is Recursion?](#7-what-is-recursion)
8. [Example 1 — Sum of First N Natural Numbers](#8-example-1--sum-of-first-n-natural-numbers)
9. [Downward Calls vs Upward Returns](#9-downward-calls-vs-upward-returns)
10. [What Is a Recursion Tree?](#10-what-is-a-recursion-tree)
11. [Example 2 — Generate All Subsets of `[1,2,3]`](#11-example-2--generate-all-subsets-of-123)
12. [Subset Recursion Tree Step by Step](#12-subset-recursion-tree-step-by-step)
13. [General Structure of a Recursive Function](#13-general-structure-of-a-recursive-function)
14. [Example 3 — Fibonacci](#14-example-3--fibonacci)
15. [Fibonacci Recursion Tree](#15-fibonacci-recursion-tree)
16. [Repeated Work in Fibonacci](#16-repeated-work-in-fibonacci)
17. [What Is the Recursive Call Stack?](#17-what-is-the-recursive-call-stack)
18. [Example 4 — Factorial Through the Call Stack](#18-example-4--factorial-through-the-call-stack)
19. [Factorial Stack Growth — Calls Going Down](#19-factorial-stack-growth--calls-going-down)
20. [Factorial Stack Unwinding — Answers Coming Up](#20-factorial-stack-unwinding--answers-coming-up)
21. [Fibonacci Through the Call Stack](#21-fibonacci-through-the-call-stack)
22. [Recursion Tree vs Call Stack](#22-recursion-tree-vs-call-stack)
23. [How to Derive Time and Space Complexity](#23-how-to-derive-time-and-space-complexity)
24. [Work Before vs Work After the Recursive Call](#24-work-before-vs-work-after-the-recursive-call)
25. [Common Recursion Mistakes](#25-common-recursion-mistakes)
26. [How to Design a Recursive Solution in an Interview](#26-how-to-design-a-recursive-solution-in-an-interview)
27. [Recognition Checklist](#27-recognition-checklist)
28. [Final Mental Model](#28-final-mental-model)
29. [One-Minute Revision](#29-one-minute-revision)

---

# 1. Lecture Roadmap

The lecture builds recursion in this order:

```text
ordinary functions
      ↓
what recursion means
      ↓
smaller subproblem
      ↓
sum of first N numbers
      ↓
recursion tree
      ↓
subset generation
      ↓
general recursive structure
      ↓
Fibonacci
      ↓
recursive call stack
      ↓
factorial
      ↓
Fibonacci call stack
```

The most important distinction to keep in mind from the beginning is:

```text
RECURSION TREE
= every recursive call created during the complete execution

CALL STACK
= only the function calls active at one particular moment
```

We will repeatedly return to this distinction.

---

# 2. Prerequisite 1 — Ordinary Functions

Before understanding recursion, understand a normal function.

A function is a reusable block of code.

It can:

```text
receive input through parameters
perform some computation
return an output
```

Example:

```cpp
int add(int a, int b) {
    int sum = a + b;
    return sum;
}
```

Calling:

```cpp
int result = add(3, 5);
```

means:

```text
input:
a = 3
b = 5

function computes:
sum = 8

return:
8
```

---

## 2.1 Function contract

A useful way to think about a function is:

```text
function(arguments)
    promises to return some well-defined answer
```

For example:

```text
add(a, b)
returns a + b
```

Later, for recursion, we will use the exact same idea:

```text
sum(n)
returns 1 + 2 + ... + n
```

or:

```text
fib(n)
returns the Fibonacci value at index n
```

This one-sentence meaning is the **recursive contract**.

---

# 3. Prerequisite 2 — Function Parameters, Return Values, and Stack Frames

When a function is called, the program must remember information about that call.

For example:

```cpp
int add(int a, int b) {
    int sum = a + b;
    return sum;
}
```

For:

```text
add(3,5)
```

the active call needs to remember:

```text
a = 3
b = 5
sum
where execution should return after add() finishes
```

That information belongs to the call's **stack frame**.

---

## 3.1 Visualizing a normal call

Suppose:

```cpp
int main() {
    int result = add(3, 5);
}
```

While `add(3,5)` is running:

```text
CALL STACK

TOP
+----------------------+
| add(3,5)             |
| a = 3                |
| b = 5                |
| sum = ?              |
| return to main       |
+----------------------+
| main()               |
+----------------------+
BOTTOM
```

When `add()` returns:

```text
8
```

its frame is removed.

Execution resumes in `main()`.

---

## 3.2 Recursion uses the same mechanism

A recursive call is not magical.

It is still an ordinary function call.

The only difference is:

```text
the function calls another copy of itself
```

Example:

```cpp
solve(5)
    calls solve(4)
        calls solve(3)
```

These are three separate calls.

Each call has its own parameter value:

```text
solve(5): n = 5
solve(4): n = 4
solve(3): n = 3
```

---

# 4. Prerequisite 3 — Problem State and Smaller Subproblem

To design recursion, identify the information that describes the current problem.

We call this the **state**.

Example:

```text
sum(n)
```

State:

```text
n
```

Meaning:

```text
How much of the problem is still left?
```

If:

```text
sum(5)
```

is the current problem, then:

```text
sum(4)
```

is a smaller problem of the same type.

---

## 4.1 The main recursion question

Ask:

> If I somehow already knew the answer to a smaller version of this problem, how would I construct the current answer?

For sum:

```text
sum(5)
```

if we already know:

```text
sum(4)
```

then:

```text
sum(5) = sum(4) + 5
```

That is recursion.

---

# 5. Prerequisite 4 — Base Case and Progress

A recursive function must stop.

Therefore every correct recursive solution needs:

```text
1. a base case
2. recursive calls that move toward that base case
```

---

## 5.1 Good progress

```cpp
solve(n - 1);
```

with:

```cpp
if (n == 0) {
    return;
}
```

State progression:

```text
5
↓
4
↓
3
↓
2
↓
1
↓
0
```

Eventually:

```text
n == 0
```

and recursion stops.

---

## 5.2 Bad progress

```cpp
solve(n);
```

State:

```text
5
↓
5
↓
5
↓
5
...
```

The base case is never approached.

This leads to:

```text
infinite recursion
→ stack keeps growing
→ stack overflow
```

---

## 5.3 Base case mental model

The base case should answer:

> What is the smallest valid problem that I can solve immediately without another recursive call?

Examples:

```text
sum(0) = 0

factorial(1) = 1

fib(0) = 0
fib(1) = 1

subsets:
if index == n
    one complete subset has been formed
```

---

# 6. Prerequisite 5 — Recurrence Relation

A recurrence expresses a problem using smaller versions of itself.

Example:

```text
sum(n) = sum(n - 1) + n
```

Example:

```text
factorial(n) = n * factorial(n - 1)
```

Example:

```text
fib(n) = fib(n - 1) + fib(n - 2)
```

The recurrence often maps almost directly into recursive code.

---

## 6.1 Recurrence → code

Mathematics:

```text
sum(n) = sum(n - 1) + n
```

Code:

```cpp
long long sum(int n) {
    if (n == 0) {
        return 0;
    }

    return sum(n - 1) + n;
}
```

---

# 7. What Is Recursion?

Recursion happens when a function solves a problem by calling itself with a different, smaller state.

The general picture is:

```text
current problem
     ↓
smaller problem
     ↓
even smaller problem
     ↓
base case
```

Then answers return upward:

```text
base answer
     ↑
solve slightly larger problem
     ↑
solve larger problem
     ↑
original answer
```

---

## 7.1 A better definition

Do not think only:

```text
"recursion means function calls itself"
```

That definition is syntactically true but not enough for problem solving.

Think:

```text
Recursion =
define the current answer
using answers to smaller problems
of the same form.
```

---

# 8. Example 1 — Sum of First N Natural Numbers

We want:

```text
1 + 2 + 3 + ... + N
```

Suppose:

```text
N = 5
```

The answer is:

```text
1 + 2 + 3 + 4 + 5
```

Instead of solving everything at once, separate the last number:

```text
(1 + 2 + 3 + 4) + 5
```

The bracketed part is exactly:

```text
sum(4)
```

Therefore:

```text
sum(5) = sum(4) + 5
```

General form:

```text
sum(n) = sum(n - 1) + n
```

---

## 8.1 Function meaning

Before code, say:

```text
sum(n) returns the sum of all integers from 1 through n.
```

This sentence is extremely important.

---

## 8.2 Base case

What is the smallest easy problem?

```text
sum(0)
```

No positive numbers exist from 1 through 0.

Therefore:

```text
sum(0) = 0
```

---

## 8.3 Recursive case

For:

```text
n > 0
```

we use:

```text
sum(n) = sum(n - 1) + n
```

---

## 8.4 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

long long sumN(int n) {
    if (n == 0) {
        return 0;
    }

    return sumN(n - 1) + n;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    cout << sumN(n) << '\n';

    return 0;
}
```

---

## 8.5 Compact dry run — `sumN(5)`

Calls go downward first:

```text
sumN(5)
→ sumN(4) + 5

sumN(4)
→ sumN(3) + 4

sumN(3)
→ sumN(2) + 3

sumN(2)
→ sumN(1) + 2

sumN(1)
→ sumN(0) + 1

sumN(0)
→ 0
```

Now returned values travel upward:

```text
sumN(0) = 0
sumN(1) = 0 + 1 = 1
sumN(2) = 1 + 2 = 3
sumN(3) = 3 + 3 = 6
sumN(4) = 6 + 4 = 10
sumN(5) = 10 + 5 = 15
```

Answer:

```text
15
```

---

# 9. Downward Calls vs Upward Returns

This is one of the most important recursion ideas.

Consider:

```cpp
return sumN(n - 1) + n;
```

When `sumN(5)` executes this line, it **cannot add 5 yet**.

Why?

Because:

```text
sumN(4)
```

has not finished.

So the current call pauses.

---

## 9.1 Downward phase

```text
sumN(5)
  waits for sumN(4)

sumN(4)
  waits for sumN(3)

sumN(3)
  waits for sumN(2)

...
```

The stack grows.

---

## 9.2 Base case

Eventually:

```text
sumN(0) = 0
```

Now no more recursive calls are needed.

---

## 9.3 Upward phase

The waiting calculations can finally execute:

```text
sumN(1): 0 + 1
sumN(2): 1 + 2
sumN(3): 3 + 3
...
```

The stack shrinks.

---

## 9.4 Visual

```text
CALLING DOWN

sum(5)
  ↓
sum(4)
  ↓
sum(3)
  ↓
sum(2)
  ↓
sum(1)
  ↓
sum(0)
```

Then:

```text
RETURNING UP

sum(0) = 0
  ↑
sum(1) = 1
  ↑
sum(2) = 3
  ↑
sum(3) = 6
  ↑
sum(4) = 10
  ↑
sum(5) = 15
```

---

# 10. What Is a Recursion Tree?

A recursion tree is a visual map of **all recursive calls created during the complete execution**.

Each node represents:

```text
one function call / one recursive state
```

Each edge represents:

```text
one recursive call creating another call
```

---

## 10.1 One-child recursion

For:

```text
sum(n)
```

each call creates only one recursive child:

```text
sum(5)
|
sum(4)
|
sum(3)
|
sum(2)
|
sum(1)
|
sum(0)
```

This tree is really just a chain.

---

## 10.2 Branching recursion

For:

```text
fib(n)
```

one call may create two children:

```text
fib(n)
├── fib(n - 1)
└── fib(n - 2)
```

Now we get a real branching tree.

---

## 10.3 Why recursion trees are useful

A recursion tree helps us understand:

```text
1. what choices are made
2. how the problem branches
3. where base cases occur
4. how many calls are generated
5. whether the same state is recomputed
6. how to estimate time complexity
```

---

# 11. Example 2 — Generate All Subsets of `[1,2,3]`

Given:

```text
[1,2,3]
```

we want all subsets:

```text
[]
[1]
[2]
[3]
[1,2]
[1,3]
[2,3]
[1,2,3]
```

---

## 11.1 Observation

For every element, there are two choices:

```text
Choice 1:
include the element

Choice 2:
do not include the element
```

For element `1`:

```text
take 1
skip 1
```

For element `2`:

```text
take 2
skip 2
```

For element `3`:

```text
take 3
skip 3
```

This naturally creates a binary recursion tree.

---

## 11.2 Define the recursive function

Use:

```text
generate(index, currentSubset)
```

Meaning:

```text
We have already decided elements [0 ... index-1].

Now generate every subset that can be formed
using elements from index onward,
starting with currentSubset.
```

---

## 11.3 State

The recursive state contains:

```text
index
currentSubset
```

---

## 11.4 Base case

When:

```text
index == n
```

every element has been decided.

So:

```text
currentSubset
```

is one complete subset.

---

## 11.5 Choices

At position `index`:

```text
Choice 1:
take arr[index]

Choice 2:
skip arr[index]
```

---

# 12. Subset Recursion Tree Step by Step

For:

```text
arr = [1,2,3]
```

start:

```text
index = 0
current = []
```

Full recursion tree:

```text
                                  []
                             decide element 1
                           /                  \
                       TAKE 1                SKIP 1
                         /                      \
                       [1]                      []
                 decide element 2        decide element 2
                   /       \                /       \
               TAKE 2     SKIP 2        TAKE 2     SKIP 2
                 /           \             /           \
              [1,2]          [1]          [2]           []
           decide 3       decide 3     decide 3      decide 3
            /   \          /   \        /   \         /   \
           /     \        /     \      /     \       /     \
      [1,2,3]  [1,2]  [1,3]   [1]  [2,3]   [2]    [3]    []
```

The leaves are exactly the subsets.

---

## 12.1 Why there are `2^N` subsets

Each element has:

```text
2 choices
```

For `N` independent element decisions:

```text
2 × 2 × 2 × ... × 2
```

`N` times:

```text
2^N
```

So there are:

```text
2^N
```

leaf states.

---

## 12.2 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

void generateSubsets(int index, const vector<int>& arr, vector<int>& current) {
    if (index == arr.size()) {
        cout << "[ ";

        for (int value : current) {
            cout << value << ' ';
        }

        cout << "]\n";
        return;
    }

    current.push_back(arr[index]);
    generateSubsets(index + 1, arr, current);
    current.pop_back();

    generateSubsets(index + 1, arr, current);
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    vector<int> arr = {1, 2, 3};
    vector<int> current;

    generateSubsets(0, arr, current);

    return 0;
}
```

---

## 12.3 Why `pop_back()` is required

Suppose:

```text
current = []
```

We choose:

```text
take 1
```

Now:

```text
current = [1]
```

After finishing every branch containing `1`, we need to explore the branch:

```text
skip 1
```

That branch must begin from:

```text
[]
```

not:

```text
[1]
```

Therefore:

```cpp
current.pop_back();
```

restores the state.

This is the first bridge from basic recursion into **backtracking**.

---

## 12.4 LCCM connection

The same subset recursion can already be described using LCCM:

```text
Level:
index

Choice:
take arr[index]
or
skip arr[index]

Check:
both choices are valid here

Move:
apply choice
recurse
undo if shared state was modified
```

You will use this same thinking later for permutations, N-Queens, Sudoku, etc.

---

# 13. General Structure of a Recursive Function

The lecture summarizes recursive functions with four parts.

We can make them even more precise:

```text
1. Parameters / state
2. Base case
3. Recursive transition
4. Combine/process returned answers
```

Generic template:

```cpp
ReturnType solve(State state) {
    if (baseCase(state)) {
        return baseAnswer;
    }

    ReturnType smallerAnswer = solve(smallerState);

    ReturnType currentAnswer = combine(smallerAnswer, currentInformation);

    return currentAnswer;
}
```

---

## 13.1 Branching version

```cpp
ReturnType solve(State state) {
    if (baseCase(state)) {
        return baseAnswer;
    }

    ReturnType answer1 = solve(smallerState1);
    ReturnType answer2 = solve(smallerState2);

    return combine(answer1, answer2);
}
```

Fibonacci follows this form.

---

## 13.2 Generation/backtracking version

```cpp
void solve(State state) {
    if (complete(state)) {
        processSolution();
        return;
    }

    for (Choice choice : choices(state)) {
        apply(choice);
        solve(nextState);
        undo(choice);
    }
}
```

Subset generation follows this form.

---

# 14. Example 3 — Fibonacci

The Fibonacci sequence shown in the lecture is:

```text
0, 1, 1, 2, 3, 5, 8, 13, ...
```

Index-based definition:

```text
F(0) = 0
F(1) = 1
```

For:

```text
n >= 2
```

we use:

```text
F(n) = F(n - 1) + F(n - 2)
```

---

## 14.1 Important indexing clarification

There are two common ways people speak about Fibonacci.

### Zero-based index

```text
index: 0 1 2 3 4 5
value: 0 1 1 2 3 5
```

So:

```text
fib(4) = 3
fib(5) = 5
```

### Ordinal wording

If somebody says:

```text
1st Fibonacci = 0
2nd Fibonacci = 1
3rd Fibonacci = 1
4th Fibonacci = 2
5th Fibonacci = 3
```

then:

```text
5th term = fib(4)
```

The lecture uses both styles in different places.

For code, we will use the clean index-based convention:

```text
fib(0) = 0
fib(1) = 1
```

---

## 14.2 Recursive contract

Define:

```text
fib(n) returns the Fibonacci value at zero-based index n.
```

---

## 14.3 Base cases

```text
fib(0) = 0
fib(1) = 1
```

---

## 14.4 Recursive case

```text
fib(n)
=
fib(n - 1) + fib(n - 2)
```

---

## 14.5 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

long long fib(int n) {
    if (n == 0) {
        return 0;
    }

    if (n == 1) {
        return 1;
    }

    return fib(n - 1) + fib(n - 2);
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    cout << fib(n) << '\n';

    return 0;
}
```

---

# 15. Fibonacci Recursion Tree

Let us compute:

```text
fib(4)
```

The first call creates:

```text
fib(3)
fib(2)
```

Full tree:

```text
                              fib(4)
                           /          \
                      fib(3)          fib(2)
                     /      \         /     \
                fib(2)     fib(1)  fib(1)  fib(0)
                /    \
           fib(1)   fib(0)
```

Base calls:

```text
fib(1) = 1
fib(0) = 0
```

---

## 15.1 Returning values upward

Bottom-left:

```text
fib(2)
=
fib(1) + fib(0)
=
1 + 0
=
1
```

Then:

```text
fib(3)
=
fib(2) + fib(1)
=
1 + 1
=
2
```

Right child:

```text
fib(2)
=
1
```

Finally:

```text
fib(4)
=
fib(3) + fib(2)
=
2 + 1
=
3
```

---

## 15.2 Annotated value tree

```text
                              fib(4)=3
                           /            \
                    fib(3)=2            fib(2)=1
                    /      \             /      \
             fib(2)=1    fib(1)=1   fib(1)=1   fib(0)=0
              /    \
       fib(1)=1   fib(0)=0
```

---

# 16. Repeated Work in Fibonacci

Look at the recursion tree.

We compute:

```text
fib(2)
```

more than once.

For larger `n`, repeated computation becomes enormous.

Example:

```text
fib(5)
```

contains:

```text
fib(3)
```

inside both:

```text
fib(4)
```

and:

```text
fib(3)
```

branches.

---

## 16.1 Why direct Fibonacci recursion is slow

The recurrence for running time is roughly:

```text
T(n)
=
T(n - 1)
+
T(n - 2)
+
O(1)
```

This grows exponentially.

A safe upper bound is:

```text
O(2^N)
```

---

## 16.2 But stack depth is only linear

Even though the tree contains exponentially many calls, only one path is active at once.

Deepest path:

```text
fib(n)
↓
fib(n - 1)
↓
fib(n - 2)
↓
...
↓
fib(1)
```

Maximum active depth:

```text
O(N)
```

Therefore:

```text
time ≈ O(2^N)
stack space = O(N)
```

This distinction is extremely important.

---

## 16.3 Signal for Dynamic Programming

Whenever you see:

```text
same recursive state
computed repeatedly
```

think:

```text
memoization / dynamic programming
```

For Fibonacci:

```text
state = n
```

So we could cache:

```text
fib(0)
fib(1)
fib(2)
...
```

and avoid recomputation.

---

# 17. What Is the Recursive Call Stack?

When a recursive function calls itself, every active function call is placed on the call stack.

The stack follows:

```text
LIFO
=
Last In, First Out
```

That means the most recent call finishes first.

---

## 17.1 Stack ordering

Suppose:

```text
fact(4)
calls fact(3)
calls fact(2)
calls fact(1)
```

Then the stack becomes:

```text
TOP
+----------------+
| fact(1)        |
+----------------+
| fact(2)        |
+----------------+
| fact(3)        |
+----------------+
| fact(4)        |
+----------------+
BOTTOM
```

The first call:

```text
fact(4)
```

is at the bottom.

The most recent call:

```text
fact(1)
```

is at the top.

---

## 17.2 What a stack frame remembers

A recursive frame may contain:

```text
function parameters
local variables
return address
unfinished computation
```

For:

```cpp
return n * factorial(n - 1);
```

the caller must remember:

```text
my n
and
after child returns, multiply its answer by n
```

---

# 18. Example 4 — Factorial Through the Call Stack

Factorial:

```text
4!
=
4 × 3 × 2 × 1
=
24
```

General recurrence:

```text
factorial(n)
=
n × factorial(n - 1)
```

Base case:

```text
factorial(1)
=
1
```

---

## 18.1 Function contract

```text
factorial(n) returns n!.
```

---

## 18.2 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

long long factorial(int n) {
    if (n <= 1) {
        return 1;
    }

    return 1LL * n * factorial(n - 1);
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    cout << factorial(n) << '\n';

    return 0;
}
```

---

# 19. Factorial Stack Growth — Calls Going Down

Call:

```text
factorial(4)
```

The expression is:

```text
4 * factorial(3)
```

But `factorial(3)` is not known yet.

So `factorial(4)` waits.

---

## 19.1 First call

```text
factorial(4)
=
4 * factorial(3)
```

Stack:

```text
TOP
+----------------------------+
| fact(4): waiting for       |
| 4 * fact(3)                |
+----------------------------+
BOTTOM
```

---

## 19.2 Second call

`fact(3)` needs:

```text
3 * fact(2)
```

Stack:

```text
TOP
+----------------------------+
| fact(3): 3 * fact(2)       |
+----------------------------+
| fact(4): 4 * fact(3)       |
+----------------------------+
BOTTOM
```

---

## 19.3 Third call

`fact(2)` needs:

```text
2 * fact(1)
```

Stack:

```text
TOP
+----------------------------+
| fact(2): 2 * fact(1)       |
+----------------------------+
| fact(3): 3 * fact(2)       |
+----------------------------+
| fact(4): 4 * fact(3)       |
+----------------------------+
BOTTOM
```

---

## 19.4 Fourth call — base case

```text
fact(1) = 1
```

Stack reaches its maximum depth:

```text
TOP
+----------------------------+
| fact(1) → 1                |
+----------------------------+
| fact(2): 2 * fact(1)       |
+----------------------------+
| fact(3): 3 * fact(2)       |
+----------------------------+
| fact(4): 4 * fact(3)       |
+----------------------------+
BOTTOM
```

Now recursion stops going down.

The stack begins to unwind.

---

# 20. Factorial Stack Unwinding — Answers Coming Up

Base case returns:

```text
fact(1) = 1
```

That frame is popped.

---

## 20.1 Return to `fact(2)`

Waiting expression:

```text
2 * fact(1)
```

Now:

```text
2 * 1
=
2
```

So:

```text
fact(2) = 2
```

Frame pops.

---

## 20.2 Return to `fact(3)`

```text
3 * fact(2)
=
3 * 2
=
6
```

So:

```text
fact(3) = 6
```

Frame pops.

---

## 20.3 Return to `fact(4)`

```text
4 * fact(3)
=
4 * 6
=
24
```

So:

```text
fact(4) = 24
```

---

## 20.4 Compact full trace

```text
DOWNWARD CALLS

fact(4)
→ 4 * fact(3)

fact(3)
→ 3 * fact(2)

fact(2)
→ 2 * fact(1)

fact(1)
→ 1
```

Then:

```text
UPWARD RETURNS

fact(1) = 1

fact(2)
= 2 * 1
= 2

fact(3)
= 3 * 2
= 6

fact(4)
= 4 * 6
= 24
```

---

## 20.5 One visual

```text
GROW

fact(4)
   ↓
fact(3)
   ↓
fact(2)
   ↓
fact(1)

BASE CASE

fact(1)=1

UNWIND

fact(2)=2
   ↑
fact(3)=6
   ↑
fact(4)=24
```

---

# 21. Fibonacci Through the Call Stack

Fibonacci is more subtle because every non-base call creates **two recursive calls**.

Code:

```cpp
return fib(n - 1) + fib(n - 2);
```

Suppose evaluation begins with the left operand first.

For:

```text
fib(4)
```

the active path first grows as:

```text
fib(4)
↓
fib(3)
↓
fib(2)
↓
fib(1)
```

At that exact moment, the stack is:

```text
TOP
+-----------------------+
| fib(1)                |
+-----------------------+
| fib(2)                |
| still needs fib(0)    |
+-----------------------+
| fib(3)                |
| still needs fib(1)    |
+-----------------------+
| fib(4)                |
| still needs fib(2)    |
+-----------------------+
BOTTOM
```

---

## 21.1 After `fib(1)` returns

`fib(2)` is not finished yet.

It still needs:

```text
fib(0)
```

So execution returns **inside** `fib(2)`.

Then:

```text
fib(0) = 0
```

and:

```text
fib(2)
=
1 + 0
=
1
```

---

## 21.2 Important observation

The recursion tree contains every call:

```text
fib(4)
├── fib(3)
│   ├── fib(2)
│   │   ├── fib(1)
│   │   └── fib(0)
│   └── fib(1)
└── fib(2)
    ├── fib(1)
    └── fib(0)
```

But the stack never contains all these nodes simultaneously.

At one instant, the stack contains only one active path such as:

```text
fib(4)
→ fib(3)
→ fib(2)
→ fib(1)
```

---

# 22. Recursion Tree vs Call Stack

This distinction should become automatic.

| Concept | Recursion Tree | Call Stack |
|---|---|---|
| Shows | All calls in complete execution | Calls active right now |
| Shape | Can branch | Always a single stack/path |
| Useful for | Understanding choices, repeated work, time | Understanding execution and memory |
| Size used for | Total number of calls | Maximum simultaneous calls |
| Fibonacci | Exponential number of nodes | Linear maximum depth |

---

## 22.1 Visual comparison

### Recursion tree

```text
                   fib(4)
                 /        \
             fib(3)       fib(2)
             /   \         /   \
         fib(2) fib(1) fib(1) fib(0)
         /   \
     fib(1) fib(0)
```

### Call stack at one moment

```text
TOP
fib(1)
fib(2)
fib(3)
fib(4)
BOTTOM
```

The stack is one root-to-current-node path through the tree.

---

# 23. How to Derive Time and Space Complexity

Do not use recursion depth as time complexity.

Use:

```text
Time
≈ number of recursive calls
× work done per call
```

Use:

```text
Auxiliary stack space
≈ maximum number of simultaneously active calls
× frame size
```

---

## 23.1 Sum of N

Recurrence:

```text
T(n)
=
T(n - 1) + O(1)
```

Calls:

```text
n
```

Therefore:

```text
Time: O(N)
Stack: O(N)
```

---

## 23.2 Factorial

Same pattern:

```text
fact(n)
→ fact(n - 1)
```

Therefore:

```text
Time: O(N)
Stack: O(N)
```

---

## 23.3 Fibonacci

One call branches:

```text
fib(n)
├── fib(n - 1)
└── fib(n - 2)
```

Total calls grow exponentially.

Useful bound:

```text
Time: O(2^N)
```

But deepest active path is only:

```text
N
```

Therefore:

```text
Stack: O(N)
```

---

## 23.4 Subset generation

Binary tree depth:

```text
N
```

Leaves:

```text
2^N
```

If we only count recursive states:

```text
O(2^N)
```

If we must print/copy every subset and each output can contain `O(N)` elements:

```text
O(N * 2^N)
```

is the natural output-sensitive bound.

Stack depth:

```text
O(N)
```

---

# 24. Work Before vs Work After the Recursive Call

Consider:

```cpp
void demo(int n) {
    if (n == 0) {
        return;
    }

    cout << n << ' ';
    demo(n - 1);
}
```

For:

```text
n = 3
```

output:

```text
3 2 1
```

Why?

Because the print happens while recursion goes downward.

---

## 24.1 Work before recursion

```cpp
cout << n << ' ';
demo(n - 1);
```

Execution:

```text
print 3
call demo(2)

print 2
call demo(1)

print 1
call demo(0)
```

Output:

```text
3 2 1
```

---

## 24.2 Work after recursion

```cpp
void demo(int n) {
    if (n == 0) {
        return;
    }

    demo(n - 1);
    cout << n << ' ';
}
```

Now:

```text
demo(3)
waits

demo(2)
waits

demo(1)
waits

demo(0)
returns
```

Then during unwinding:

```text
print 1
print 2
print 3
```

Output:

```text
1 2 3
```

---

## 24.3 Mental rule

```text
work BEFORE recursive call
→ happens while going DOWN

work AFTER recursive call
→ happens while returning UP
```

This becomes very important in:

```text
tree preorder
tree postorder
backtracking
divide and conquer
```

---

# 25. Common Recursion Mistakes

## Mistake 1 — No base case

```cpp
void solve(int n) {
    solve(n - 1);
}
```

Eventually invalid states continue forever.

---

## Mistake 2 — State does not move toward the base case

```cpp
solve(n);
```

with base:

```cpp
if (n == 0)
```

does not progress.

---

## Mistake 3 — Wrong function meaning

If you cannot complete the sentence:

```text
solve(state) returns / does ...
```

the recursion is usually difficult to reason about.

---

## Mistake 4 — Trying to mentally simulate every call before defining the recurrence

For a problem like Fibonacci, first establish:

```text
fib(n) = fib(n - 1) + fib(n - 2)
```

Then use a tree to inspect execution.

Do not begin by staring at the runtime stack.

---

## Mistake 5 — Confusing tree size with stack depth

Fibonacci:

```text
tree nodes ≈ exponential
stack depth ≈ linear
```

These measure different things.

---

## Mistake 6 — Forgetting state restoration in generation problems

For subset generation:

```cpp
current.push_back(arr[index]);
generateSubsets(...);
```

must eventually be followed by:

```cpp
current.pop_back();
```

before exploring the sibling branch.

---

## Mistake 7 — Mixing Fibonacci indexing conventions

With:

```text
fib(0)=0
fib(1)=1
```

we have:

```text
fib(4)=3
fib(5)=5
```

If speaking in ordinals:

```text
5th term = 3
```

because the first term is index 0.

State the convention.

---

# 26. How to Design a Recursive Solution in an Interview

Before writing code, answer these questions.

---

## Step 1 — Define the function

Say:

```text
solve(state) returns ...
```

Example:

```text
sumN(n) returns 1 + 2 + ... + n.
```

---

## Step 2 — Identify the state

Ask:

```text
What information completely describes the current smaller problem?
```

Examples:

```text
sum:
n

factorial:
n

Fibonacci:
n

subsets:
index + current subset
```

---

## Step 3 — Find the base case

Ask:

```text
What state can I answer immediately?
```

---

## Step 4 — Make the problem smaller

Examples:

```text
n → n - 1

index → index + 1
```

---

## Step 5 — Decide whether recursion branches

One child:

```text
sum(n)
→ sum(n - 1)
```

Two children:

```text
fib(n)
→ fib(n - 1)
→ fib(n - 2)
```

Choice tree:

```text
subset(index)
→ take
→ skip
```

---

## Step 6 — Combine answers

For sum:

```text
smaller answer + n
```

For factorial:

```text
n * smaller answer
```

For Fibonacci:

```text
left answer + right answer
```

For subset generation:

```text
no numeric combination;
each leaf is one generated answer
```

---

## Step 7 — Draw the smallest useful tree

Use tiny input:

```text
n = 3
```

or:

```text
arr = [1,2,3]
```

Do not start with large examples.

---

# 27. Recognition Checklist

When reading a new problem, recursion is natural when one or more of these are true:

```text
[ ] Current problem can be described using smaller same-type problems
[ ] There is a natural state that becomes smaller
[ ] The problem contains repeated choose/skip decisions
[ ] We need to enumerate combinations/subsets/permutations
[ ] Tree/graph structure naturally recurses on children
[ ] Divide-and-conquer splits the problem
[ ] Backtracking must explore multiple choices
```

Then identify:

```text
STATE
BASE CASE
TRANSITION
COMBINATION / PROCESSING
```

---

# 28. Final Mental Model

The cleanest way to understand recursion is to separate three ideas.

---

## 28.1 Mathematical view

```text
What relationship connects the current problem
to smaller problems?
```

Example:

```text
sum(n)
=
sum(n - 1) + n
```

---

## 28.2 Recursion-tree view

```text
What calls are created during the complete execution?
```

Example:

```text
fib(4)
├── fib(3)
└── fib(2)
```

This view helps with:

```text
branching
repeated work
time complexity
```

---

## 28.3 Call-stack view

```text
Which calls are active right now?
```

Example:

```text
TOP
fib(1)
fib(2)
fib(3)
fib(4)
BOTTOM
```

This view helps with:

```text
execution order
waiting calculations
returned values
stack space
```

---

## 28.4 Master diagram

```text
                 RECURSION

       "solve current problem using
          smaller same problem"
                    |
        +-----------+-----------+
        |                       |
   FUNCTION LOGIC          RUNTIME VIEW
        |                       |
        |                 +-----+------+
        |                 |            |
  recurrence / state   recursion     call
  base case            tree          stack
        |                 |            |
        |            all calls     active path
        |                 |            |
        |            time / work    memory /
        |                           return order
        |
 smaller problem
        |
     answer
```

---

# 29. One-Minute Revision

```text
Recursion:
A function solves a problem using smaller problems of the same type.

Before code:
1. Define what solve(state) means.
2. Identify the state.
3. Find the base case.
4. Make every recursive call move toward it.
5. Combine returned answers.

Sum:
sum(n) = sum(n-1) + n
sum(0) = 0

Factorial:
fact(n) = n * fact(n-1)
fact(1) = 1

Fibonacci:
fib(n) = fib(n-1) + fib(n-2)
fib(0)=0
fib(1)=1

Subset generation:
Every element:
TAKE / SKIP

Recursion tree:
ALL calls generated during complete execution.

Call stack:
ONLY calls active at the current moment.

Tree size:
helps estimate TIME.

Maximum active path:
helps estimate STACK SPACE.

Factorial:
Time O(N)
Stack O(N)

Naive Fibonacci:
Time O(2^N)
Stack O(N)

Subsets:
2^N leaves
O(N * 2^N) if materializing/printing all subsets.

Golden rule:
Work before recursion happens while going DOWN.
Work after recursion happens while coming UP.
```
