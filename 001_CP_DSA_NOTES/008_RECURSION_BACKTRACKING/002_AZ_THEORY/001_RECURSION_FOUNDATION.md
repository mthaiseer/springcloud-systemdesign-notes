# Recursion Foundation — TLE-Style Self-Study Notes

> **Goal:** Understand recursion deeply enough to *design* recursive solutions, draw recursion trees, trace the call stack, reason about time/space, and later transition naturally into backtracking.

---

## Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [What Is Recursion?](#2-what-is-recursion)
3. [The Recursive Contract](#3-the-recursive-contract)
4. [Base Case and Recursive Case](#4-base-case-and-recursive-case)
5. [Three Ways to Visualize Recursion](#5-three-ways-to-visualize-recursion)
6. [Recursion Tree vs Recursion Stack](#6-recursion-tree-vs-recursion-stack)
7. [Tail Recursion and Tail-Call Optimization](#7-tail-recursion-and-tail-call-optimization)
8. [How to Design a Recursive Solution](#8-how-to-design-a-recursive-solution)
9. [Four Recursive Design Directions](#9-four-recursive-design-directions)
10. [Example 1 — Fibonacci](#10-example-1--fibonacci)
11. [Example 2 — Palindrome Check](#11-example-2--palindrome-check)
12. [Example 3 — nCr](#12-example-3--ncr)
13. [Example 4 — Work Before and After Recursion](#13-example-4--work-before-and-after-recursion)
14. [Example 5 — Tower of Hanoi](#14-example-5--tower-of-hanoi)
15. [How to Derive Complexity from a Recursion Tree](#15-how-to-derive-complexity-from-a-recursion-tree)
16. [Common Recursion Mistakes](#16-common-recursion-mistakes)
17. [Recursion Recognition Checklist](#17-recursion-recognition-checklist)
18. [Final Mental Model](#18-final-mental-model)

---

# 1. Prerequisites

Before writing recursive code, understand what an ordinary function call does.

---

## 1.1 A normal function call creates a stack frame

Consider:

```cpp
int square(int x) {
    int answer = x * x;
    return answer;
}

int main() {
    int value = square(5);
}
```

When `square(5)` is called, the program must remember:

```text
parameter:       x = 5
local variable:  answer
return location: where execution continues in main()
```

We can visualize the active call as:

```text
CALL STACK

+----------------------+
| square(5)            |
| x = 5                |
| answer = ?           |
| return to main       |
+----------------------+
| main()               |
+----------------------+
```

When `square()` finishes, its frame is removed.

This exact mechanism is also used by recursion.

The only difference is:

```text
ordinary call:
function A → function B

recursion:
function A → another call of function A
```

Each recursive call still receives its **own stack frame**.

---

## 1.2 Every recursive call has its own variables

Example:

```cpp
void show(int n) {
    if (n == 0) {
        return;
    }

    show(n - 1);
}
```

Calling:

```text
show(3)
```

creates different calls:

```text
show(3)
show(2)
show(1)
show(0)
```

Even though the function name is the same, these are different stack frames.

```text
+-----------+
| show(0)   |
| n = 0     |
+-----------+
| show(1)   |
| n = 1     |
+-----------+
| show(2)   |
| n = 2     |
+-----------+
| show(3)   |
| n = 3     |
+-----------+
```

That is one of the most important recursion ideas:

> **Same function does not mean same state. Each call has its own parameters and local variables.**

---

## 1.3 A problem must become smaller

A recursive call must move toward a condition that stops recursion.

Good:

```cpp
solve(n - 1);
```

when the base case is:

```cpp
if (n == 0) {
    return;
}
```

Progress:

```text
5 → 4 → 3 → 2 → 1 → 0
```

Bad:

```cpp
solve(n);
```

Nothing changes:

```text
5 → 5 → 5 → 5 → ...
```

The base case is never reached.

---

## 1.4 Recurrence relation

A recurrence describes a problem using smaller versions of itself.

Example:

```text
factorial(n) = n × factorial(n - 1)
```

with:

```text
factorial(0) = 1
```

For `factorial(4)`:

```text
factorial(4)
= 4 × factorial(3)
= 4 × 3 × factorial(2)
= 4 × 3 × 2 × factorial(1)
= 4 × 3 × 2 × 1 × factorial(0)
= 24
```

The recurrence often becomes the recursive code almost directly.

---

# 2. What Is Recursion?

Recursion is a technique in which a function solves a problem by asking the **same function** to solve one or more smaller problems.

Conceptually:

```text
solve(n)
   ↓
solve(smaller problem)
   ↓
solve(even smaller problem)
   ↓
base case
```

A recursive solution normally contains two pieces:

```text
1. Base case
2. Recursive case
```

---

## 2.1 Base case

The base case is the smallest version of the problem that can be answered directly.

Example:

```cpp
long long factorial(int n) {
    if (n == 0) {
        return 1;
    }

    return n * factorial(n - 1);
}
```

Here:

```text
factorial(0) = 1
```

is the base case.

---

## 2.2 Recursive case

The recursive case expresses the current answer using a smaller problem.

```text
factorial(n)
=
n × factorial(n - 1)
```

The current call says:

```text
"I know how to finish the answer
 if someone gives me factorial(n - 1)."
```

This is the mindset to develop.

---

# 3. The Recursive Contract

Before writing code, define exactly what the recursive function means.

This is more important than memorizing syntax.

Examples:

```text
fib(n)
returns the nth Fibonacci number.

isPalindrome(left, right)
returns whether s[left ... right] is a palindrome.

move(n, from, to, aux)
moves n disks from rod 'from' to rod 'to'
using rod 'aux' as temporary storage.
```

Think of this as a **contract**.

When writing:

```cpp
fib(n - 1)
```

do not mentally enter all recursive calls immediately.

First trust the contract:

```text
fib(n - 1)
will correctly give the (n - 1)th Fibonacci value.
```

Then ask:

```text
How can I use that smaller answer
to solve the current state?
```

---

## 3.1 The four questions

For every recursive problem, answer these before coding:

```text
1. STATE
   What completely describes one recursive subproblem?

2. BASE CASE
   What state can be answered immediately?

3. TRANSITION
   Which smaller recursive call(s) are required?

4. COMBINATION
   How are the smaller answers used
   to construct the current answer?
```

Example — factorial:

```text
State:
    n

Meaning:
    factorial(n) returns n!

Base case:
    n == 0 → 1

Transition:
    factorial(n - 1)

Combination:
    n × factorial(n - 1)
```

---

# 4. Base Case and Recursive Case

A useful template is:

```cpp
ReturnType solve(State state) {
    if (baseCase(state)) {
        return baseAnswer;
    }

    // possibly do work before recursion

    ReturnType smallerAnswer = solve(smallerState);

    // combine current work + smallerAnswer

    return answer;
}
```

The recursive call must make progress:

```text
current state
     ↓
strictly smaller / closer state
     ↓
base case
```

Otherwise the recursion does not terminate.

---

# 5. Three Ways to Visualize Recursion

Recursion should be understood using three complementary views.

| View | What it tells us |
|---|---|
| **Intuition / recurrence** | Relationship between current problem and smaller problem(s) |
| **Recursion tree** | Every recursive call generated during the complete execution |
| **Recursion stack** | Only the calls that are active at one instant |

These are related, but they are not the same thing.

---

## 5.1 Intuition view

For Fibonacci:

```text
fib(n)
=
fib(n - 1) + fib(n - 2)
```

This tells us the mathematical relationship.

It does **not** yet show every call.

---

## 5.2 Recursion tree

For:

```cpp
int fib(int n) {
    if (n <= 1) {
        return n;
    }

    return fib(n - 1) + fib(n - 2);
}
```

`fib(4)` creates:

```text
                         fib(4)
                       /        \
                  fib(3)        fib(2)
                 /      \       /     \
            fib(2)    fib(1) fib(1) fib(0)
            /    \
       fib(1)   fib(0)
```

The recursion tree shows:

```text
root      = initial call
children  = recursive calls created by a call
leaves    = calls that hit a base case
edges     = "this call created that call"
```

It is useful for understanding:

```text
• branching
• repeated work
• number of calls
• base cases
• time complexity
```

---

## 5.3 Recursion stack

The stack shows only the currently active path.

Suppose evaluation goes left first:

```text
fib(4)
→ fib(3)
→ fib(2)
→ fib(1)
```

At that instant:

```text
TOP
+-------------------+
| fib(1)            | ← base case
+-------------------+
| fib(2)            | waiting for fib(1)
+-------------------+
| fib(3)            | waiting for fib(2)
+-------------------+
| fib(4)            | waiting for fib(3)
+-------------------+
BOTTOM
```

After `fib(1)` returns, its stack frame disappears.

Then execution resumes inside `fib(2)`.

---

# 6. Recursion Tree vs Recursion Stack

This distinction is extremely important.

For Fibonacci:

```text
Recursion tree:
contains ALL calls made during the complete execution.

Recursion stack:
contains only ONE currently active root-to-leaf path.
```

Example:

```text
                         fib(4)
                       /        \
                  fib(3)        fib(2)
                 /      \
            fib(2)      fib(1)
            /
       fib(1)
```

At one moment the active stack could be:

```text
fib(4)
fib(3)
fib(2)
fib(1)
```

The other branches exist conceptually in the tree, but they are not simultaneously active.

Therefore:

```text
time complexity
≈ total work across recursion tree

auxiliary stack space
≈ maximum recursion depth
```

Do not confuse them.

A recursion may make exponentially many calls but use only linear stack depth.

---

## 6.1 Stack space is finite

Every active recursive call consumes stack space.

A stack frame may contain:

```text
• parameters
• local variables
• bookkeeping / return address
```

So deep recursion can cause stack overflow.

Important practical rule:

```text
Very deep O(N) recursion with large N
→ inspect constraints carefully
→ consider an iterative solution
```

There is no universal safe recursion-depth number in C++.

Safe depth depends on:

```text
• platform
• process stack size
• judge environment
• compiler
• size of each stack frame
```

So values around `10^5` recursive frames should be treated as a **warning scale**, not a guaranteed cutoff.

---

# 7. Tail Recursion and Tail-Call Optimization

A function is tail-recursive when the recursive call is the final operation.

Example:

```cpp
long long sumTail(int n, long long answer) {
    if (n == 0) {
        return answer;
    }

    return sumTail(n - 1, answer + n);
}
```

There is no pending calculation after the recursive call returns.

Conceptually:

```text
sumTail(4, 0)
→ sumTail(3, 4)
→ sumTail(2, 7)
→ sumTail(1, 9)
→ sumTail(0, 10)
→ 10
```

---

## 7.1 Non-tail-recursive version

```cpp
long long sumNotTail(int n) {
    if (n == 0) {
        return 0;
    }

    return n + sumNotTail(n - 1);
}
```

Here:

```text
4 + sumNotTail(3)
```

cannot finish until `sumNotTail(3)` returns.

The `+ 4` operation is waiting.

---

## 7.2 Tail-call optimization

A compiler **may** optimize some tail calls by reusing stack frames.

But:

> **C++ does not guarantee Tail Call Optimization.**

Therefore do not claim:

```text
tail recursion = guaranteed O(1) stack
```

For portable complexity analysis, assume:

```text
recursive depth N
→ O(N) stack
```

unless optimization is explicitly guaranteed by the environment.

If constant auxiliary space is required, use a loop:

```cpp
long long sumIterative(int n) {
    long long answer = 0;

    while (n > 0) {
        answer += n;
        n--;
    }

    return answer;
}
```

---

# 8. How to Design a Recursive Solution

Use this workflow.

---

## Step 1 — Define the function in one sentence

Example:

```text
solve(n) returns the answer for a problem of size n.
```

or:

```text
isPalindrome(left, right)
returns whether s[left ... right] is a palindrome.
```

If you cannot define the function clearly, the recursive code usually becomes confusing.

---

## Step 2 — Identify the state

Ask:

```text
What information uniquely identifies a subproblem?
```

Examples:

```text
Fibonacci:
state = n

Palindrome:
state = (left, right)

nCr:
state = (n, r)

Tower of Hanoi:
state = (n, from, to, aux)
```

---

## Step 3 — Find the smallest answerable state

Examples:

```text
fib:
n <= 1

palindrome:
left >= right

nCr:
r == 0 or r == n

Tower of Hanoi:
n == 1
```

---

## Step 4 — Reduce to smaller state(s)

Examples:

```text
fib(n)
→ fib(n - 1), fib(n - 2)

palindrome(left, right)
→ palindrome(left + 1, right - 1)

nCr(n, r)
→ nCr(n - 1, r)
  + nCr(n - 1, r - 1)

Hanoi(n)
→ Hanoi(n - 1)
  + one direct move
  + Hanoi(n - 1)
```

---

## Step 5 — Decide where the work happens

There are three common positions:

```text
work BEFORE recursion

recursive call

work AFTER recursion
```

Example:

```cpp
void demo(int n) {
    cout << "before " << n << '\n';

    demo(n - 1);

    cout << "after " << n << '\n';
}
```

Work before recursion happens while going **down**.

Work after recursion happens while calls **return upward**.

This idea later becomes important in:

```text
• tree traversals
• backtracking
• divide and conquer
• post-processing
```

---

# 9. Four Recursive Design Directions

The lesson organizes recursive thinking into four broad directions.

---

## 9.1 Recursion / Divide and Conquer

Split into smaller problems, solve them recursively, combine answers.

```text
                 problem
                /       \
          smaller       smaller
             \           /
              \         /
                combine
```

Examples:

```text
• Fibonacci recurrence
• Binary Search
• Merge Sort
• Inversion Count
```

Typical structure:

```text
Split
  ↓
Solve recursively
  ↓
Combine
```

---

## 9.2 Backtracking

Backtracking explores choices.

A useful framework is:

```text
L C C M

Level
Choice
Check
Move
```

Expanded:

```text
LEVEL
Which decision are we currently making?

CHOICE
What options can we try?

CHECK
Is this choice currently valid?

MOVE
Apply choice → recurse → restore state if needed.
```

Generic recursion tree:

```text
                         state
                      /    |    \
                    C1     C2     C3
                   /       X       \
               next              next
```

`X` represents a branch rejected by a validity check.

Later, this becomes:

```cpp
choose();

recurse();

undo();
```

---

## 9.3 Kth Generation / Kth Form

Sometimes we do not want every generated answer.

We want only:

```text
the kth answer
```

If each branch contains a known number of answers, we can skip complete subtrees.

```text
                         ROOT
                  /        |        \
            3 answers   4 answers   2 answers
                X          ↓
                         kth lies here
```

Mental model:

```text
count subtree size
      ↓
is k inside it?
  /          \
no            yes
↓              ↓
skip         descend
```

Tower of Hanoi can later be revisited using exactly this idea.

---

## 9.4 Fractals

A fractal repeatedly applies the same rule at a smaller scale.

Recursion naturally expresses:

```text
same structure
     ↓
smaller copy
     ↓
smaller copy
     ↓
...
```

The important point is not graphics itself.

The transferable idea is:

> A recursive definition can describe self-similar structure.

---

# 10. Example 1 — Fibonacci

## 10.1 Problem

The Fibonacci recurrence is:

```text
F(0) = 0
F(1) = 1

F(n) = F(n - 1) + F(n - 2), for n > 1
```

---

## 10.2 Recursive contract

```text
fib(n) returns the nth Fibonacci number.
```

State:

```text
n
```

Base case:

```text
n <= 1
```

Transition:

```text
fib(n - 1)
fib(n - 2)
```

Combination:

```text
add the two returned values
```

---

## 10.3 C++17

```cpp
int fib(int n) {
    if (n <= 1) {
        return n;
    }

    return fib(n - 1) + fib(n - 2);
}
```

---

## 10.4 Recursion tree for fib(5)

```text
                              fib(5)
                       /                 \
                  fib(4)                 fib(3)
                /       \               /      \
           fib(3)      fib(2)       fib(2)    fib(1)
          /     \      /    \       /    \
     fib(2)  fib(1) fib(1) fib(0) fib(1) fib(0)
     /   \
 fib(1) fib(0)
```

Base-case leaves:

```text
fib(0)
fib(1)
```

They return immediately.

---

## 10.5 What the tree teaches us

Notice repeated states:

```text
fib(3) appears multiple times
fib(2) appears multiple times
fib(1) appears many times
```

So plain recursion repeats work.

This observation later motivates:

```text
memoization
Dynamic Programming
```

---

## 10.6 Step-by-step execution

Assume the left recursive call is evaluated first.

```text
fib(5)
→ fib(4)
→ fib(3)
→ fib(2)
→ fib(1)
```

`fib(1)` returns `1`.

Now execution resumes in `fib(2)`.

`fib(2)` still needs:

```text
fib(0)
```

So:

```text
fib(0) → 0
```

Then:

```text
fib(2)
= fib(1) + fib(0)
= 1 + 0
= 1
```

That value travels upward to `fib(3)`.

---

## 10.7 Stack snapshot

At the deepest point:

```text
TOP
fib(1)
fib(2)
fib(3)
fib(4)
fib(5)
BOTTOM
```

Maximum stack depth is proportional to `n`.

But total calls are much larger than `n`.

Therefore:

```text
Time  ≈ O(2^n) for the naive implementation
Stack ≈ O(n)
```

The exact call count is not exactly `2^n`, but `O(2^n)` is the standard upper-bound description.

---

# 11. Example 2 — Palindrome Check

## 11.1 Problem

A string is a palindrome if it reads the same from both directions.

Example:

```text
racecar
```

Compare:

```text
r == r
a == a
c == c
```

Then only the middle remains.

---

## 11.2 Recursive contract

Define:

```text
isPalindrome(s, left, right)

returns whether s[left ... right]
is a palindrome.
```

---

## 11.3 State

```text
(left, right)
```

These two indices completely identify the remaining substring.

---

## 11.4 Base case

When:

```text
left >= right
```

zero or one character remains.

That is automatically a palindrome.

So:

```text
return true
```

---

## 11.5 Failure case

If:

```text
s[left] != s[right]
```

the answer is immediately:

```text
false
```

---

## 11.6 Recursive transition

If outer characters match:

```text
check the inside substring
```

So:

```text
(left, right)
      ↓
(left + 1, right - 1)
```

ASCII:

```text
r a c e c a r
^           ^
L           R
|-----------|

outer chars equal
      ↓

r a c e c a r
  ^       ^
  L       R
```

---

## 11.7 C++17

```cpp
bool isPalindrome(const string& s, int left, int right) {
    if (left >= right) {
        return true;
    }

    if (s[left] != s[right]) {
        return false;
    }

    return isPalindrome(s, left + 1, right - 1);
}
```

Initial call:

```cpp
isPalindrome(s, 0, (int)s.size() - 1);
```

---

## 11.8 Compact dry run

For:

```text
s = "racecar"
```

```text
(0,6): 'r' == 'r' → recurse to (1,5)
(1,5): 'a' == 'a' → recurse to (2,4)
(2,4): 'c' == 'c' → recurse to (3,3)
(3,3): left >= right → true
```

Returns upward:

```text
true
→ true
→ true
→ true
```

Answer:

```text
true
```

---

## 11.9 Complexity

Each call removes two characters.

```text
number of calls ≈ n / 2
```

Therefore:

```text
Time  = O(n)
Stack = O(n)
```

Passing the string by `const reference` avoids copying it on every recursive call.

---

# 12. Example 3 — nCr

We want:

```text
C(n, r)
```

the number of ways to choose `r` objects from `n`.

---

## 12.1 The key choice

Consider one particular element.

There are exactly two possibilities.

### Do not choose it

Then we still need:

```text
r elements
```

from the remaining:

```text
n - 1
```

So:

```text
C(n - 1, r)
```

### Choose it

Now we need:

```text
r - 1
```

more elements from the remaining:

```text
n - 1
```

So:

```text
C(n - 1, r - 1)
```

Therefore:

```text
C(n, r)
=
C(n - 1, r)
+
C(n - 1, r - 1)
```

---

## 12.2 Base cases

```text
C(n, 0) = 1
C(n, n) = 1
```

Also invalid:

```text
r < 0 or r > n
→ 0
```

---

## 12.3 Recursion tree for C(4,2)

```text
                           C(4,2)
                    /                 \
             skip element          take element
                C(3,2)               C(3,1)
              /       \             /       \
          C(2,2)    C(2,1)      C(2,1)    C(2,0)
                     /  \         /  \
                 C(1,1) C(1,0) C(1,1) C(1,0)
```

Again we see repeated states such as:

```text
C(2,1)
```

This is another classic memoization/DP signal.

---

## 12.4 C++17

```cpp
long long nCr(int n, int r) {
    if (r < 0 || r > n) {
        return 0;
    }

    if (r == 0 || r == n) {
        return 1;
    }

    return nCr(n - 1, r) + nCr(n - 1, r - 1);
}
```

---

## 12.5 Dry run

For:

```text
C(3,2)
```

```text
C(3,2)
= C(2,2) + C(2,1)

C(2,2) = 1

C(2,1)
= C(1,1) + C(1,0)
= 1 + 1
= 2

Therefore:

C(3,2)
= 1 + 2
= 3
```

---

## 12.6 Pattern recognition

This is the first major appearance of:

```text
TAKE / NOT TAKE
```

That pattern later becomes central to:

```text
• subsets
• subsequences
• combination problems
• 0/1 knapsack
• backtracking
```

---

# 13. Example 4 — Work Before and After Recursion

Suppose for `n = 5` we want:

```text
5 4 3 2 1 2 3 4 5
```

Use:

```cpp
void printPattern(int n) {
    cout << n << " ";

    if (n == 1) {
        return;
    }

    printPattern(n - 1);

    cout << n << " ";
}
```

---

## 13.1 Recursion path

Calls:

```text
printPattern(5)
    ↓
printPattern(4)
    ↓
printPattern(3)
    ↓
printPattern(2)
    ↓
printPattern(1)
```

---

## 13.2 Work while going downward

The first:

```cpp
cout << n;
```

runs before recursion.

So output becomes:

```text
5 4 3 2 1
```

---

## 13.3 Base case

At:

```text
n = 1
```

we print `1`, then immediately return.

Because we return here, the second `cout` does not execute for `1`.

---

## 13.4 Work while returning upward

Now suspended calls resume in reverse order:

```text
printPattern(2) resumes → print 2
printPattern(3) resumes → print 3
printPattern(4) resumes → print 4
printPattern(5) resumes → print 5
```

Final output:

```text
5 4 3 2 1 2 3 4 5
```

---

## 13.5 Stack visualization

Downward phase:

```text
TOP
printPattern(1)
printPattern(2)
printPattern(3)
printPattern(4)
printPattern(5)
BOTTOM
```

Unwinding:

```text
1 returns
↓
2 resumes
↓
3 resumes
↓
4 resumes
↓
5 resumes
```

This is the foundation for understanding:

```text
Preorder:
work before child recursion

Postorder:
work after child recursion
```

---

# 14. Example 5 — Tower of Hanoi

Tower of Hanoi contains:

```text
• n disks
• source rod
• destination rod
• auxiliary rod
```

Rules:

```text
1. Move only one disk at a time.
2. Move only the top disk.
3. Never put a larger disk on a smaller disk.
```

---

## 14.1 Recursive contract

```text
move(n, from, to, aux)

moves the top n disks
from rod 'from'
to rod 'to'
using rod 'aux'.
```

---

## 14.2 Think about the largest disk

To move `n` disks:

```text
Step 1:
Move top n - 1 disks
FROM → AUX

Step 2:
Move largest disk
FROM → TO

Step 3:
Move n - 1 disks
AUX → TO
```

ASCII:

```text
          n disks
             |
             v

       [ move n-1 ]
FROM  ------------> AUX

       [ largest ]
FROM  ------------> TO

       [ move n-1 ]
AUX   ------------> TO
```

---

## 14.3 Recursive structure

```text
move(n, from, to, aux)

        ├── move(n-1, from, aux, to)
        │
        ├── move largest disk: from → to
        │
        └── move(n-1, aux, to, from)
```

---

## 14.4 Base case

For one disk:

```text
move it directly
```

So:

```cpp
if (n == 1) {
    cout << from << " -> " << to << '\n';
    return;
}
```

---

## 14.5 C++17

```cpp
void moveDisks(int n, int from, int to, int aux) {
    if (n == 1) {
        cout << from << " -> " << to << '\n';
        return;
    }

    moveDisks(n - 1, from, aux, to);

    cout << from << " -> " << to << '\n';

    moveDisks(n - 1, aux, to, from);
}
```

For three disks:

```cpp
moveDisks(3, 1, 3, 2);
```

---

## 14.6 Recursion tree for n = 3

We focus on calls first:

```text
                     move(3, 1→3)
                    /             \
            move(2, 1→2)       move(2, 2→3)
             /       \           /       \
        move(1)   move(1)   move(1)   move(1)
```

But remember that between the two recursive calls at each node there is also one direct disk move.

More accurately:

```text
move(3, 1→3)
│
├── move(2, 1→2)
│   ├── move(1, 1→3)
│   ├── DIRECT: 1→2
│   └── move(1, 3→2)
│
├── DIRECT: 1→3
│
└── move(2, 2→3)
    ├── move(1, 2→1)
    ├── DIRECT: 2→3
    └── move(1, 1→3)
```

---

## 14.7 Detailed dry run for n = 3

Initial:

```text
Rod 1: [3,2,1]
Rod 2: []
Rod 3: []
```

Moves:

```text
1. 1 → 3
2. 1 → 2
3. 3 → 2
4. 1 → 3
5. 2 → 1
6. 2 → 3
7. 1 → 3
```

Total:

```text
7 moves
```

---

## 14.8 Deriving the recurrence

Let:

```text
M(n) = number of moves for n disks
```

For `n` disks:

```text
move n-1 disks
+
move largest disk once
+
move n-1 disks
```

Therefore:

```text
M(n)
=
M(n - 1) + 1 + M(n - 1)

M(n)
=
2M(n - 1) + 1
```

Base:

```text
M(1) = 1
```

Expand:

```text
M(2)
= 2M(1) + 1
= 2(1) + 1
= 3

M(3)
= 2M(2) + 1
= 2(3) + 1
= 7

M(4)
= 2(7) + 1
= 15
```

Pattern:

```text
M(n) = 2^n - 1
```

Therefore:

```text
Time  = O(2^n)
Stack = O(n)
```

---

## 14.9 Connection to Kth-form recursion

Tower of Hanoi has predictable subtree sizes.

Each recursive `n - 1` branch contains:

```text
2^(n-1) - 1
```

moves.

Therefore, if a later problem asks:

```text
"What is the kth move?"
```

we may not need to generate every earlier move.

Instead:

```text
count branch size
→ decide whether kth answer is inside that branch
→ skip whole branches when possible
```

This is the same high-level idea used by many kth-generation problems.

---

# 15. How to Derive Complexity from a Recursion Tree

Never say:

```text
recursion depth = time complexity
```

They measure different things.

---

## 15.1 One recursive call

Example:

```cpp
solve(n - 1);
```

Tree:

```text
solve(n)
   |
solve(n-1)
   |
solve(n-2)
   |
...
```

About `n` nodes.

```text
Time  = O(n)
Stack = O(n)
```

---

## 15.2 Two branching recursive calls

Example:

```cpp
solve(n - 1);
solve(n - 1);
```

Tree:

```text
                  n
               /     \
            n-1       n-1
           /   \     /   \
         ...   ... ...   ...
```

Approximately doubles every level.

```text
level 0: 1
level 1: 2
level 2: 4
level 3: 8
...
```

Total work becomes exponential.

But maximum active path is still only:

```text
n
```

So it is possible to have:

```text
Time  = O(2^n)
Stack = O(n)
```

---

## 15.3 Divide and conquer example

If:

```text
T(n) = 2T(n/2) + O(n)
```

then the tree has:

```text
level 0 total work ≈ n
level 1 total work ≈ n
level 2 total work ≈ n
...
log n levels
```

Therefore:

```text
Time = O(n log n)
```

This type of reasoning becomes important for Merge Sort.

---

# 16. Common Recursion Mistakes

## Mistake 1 — No base case

```cpp
void solve(int n) {
    solve(n - 1);
}
```

Eventually causes stack overflow.

---

## Mistake 2 — State does not move toward base case

```cpp
void solve(int n) {
    if (n == 0) {
        return;
    }

    solve(n);
}
```

`n` never changes.

---

## Mistake 3 — Wrong function meaning

If you cannot state:

```text
solve(state) returns ...
```

the recursion is often being designed blindly.

Fix:

```text
define the recursive contract first
```

---

## Mistake 4 — Confusing tree with stack

Wrong:

```text
Fibonacci has exponentially many calls,
therefore it needs exponential stack space.
```

Correct:

```text
tree contains exponentially many total calls

but

only one root-to-leaf path is active at once
```

So:

```text
naive Fibonacci:
time  = exponential
stack = linear
```

---

## Mistake 5 — Forgetting work after recursion

Example:

```cpp
cout << n;
solve(n - 1);
cout << n;
```

The second print happens while recursion unwinds.

Do not mentally execute the function as if both `cout`s happen together.

---

## Mistake 6 — Copying large objects every call

Bad:

```cpp
bool solve(string s, int left, int right)
```

Every call may copy the whole string.

Better:

```cpp
bool solve(const string& s, int left, int right)
```

---

## Mistake 7 — Assuming tail recursion is automatically O(1) space

C++ does not guarantee Tail Call Optimization.

For constraints where stack depth matters, prefer a loop when possible.

---

## Mistake 8 — Ignoring repeated states

If the recursion tree contains:

```text
same state
same parameters
same answer
```

computed repeatedly, think:

```text
memoization / DP
```

Example:

```text
fib(5)

fib(3) appears more than once
fib(2) appears more than once
```

That is the signal.

---

# 17. Recursion Recognition Checklist

When you see an unseen problem, ask:

```text
1. Can the problem be defined using a smaller version of itself?

2. What does solve(state) mean?

3. What information forms the state?

4. What is the smallest state I can answer directly?

5. Does every recursive transition move toward that base case?

6. Is there one smaller call or several branches?

7. Is work performed:
   before recursion,
   after recursion,
   or both?

8. Are identical states being recomputed?

9. What does the recursion tree look like?

10. What is the maximum active depth?

11. Could the depth overflow the C++ call stack?

12. Does the problem naturally represent choices?
    If yes → backtracking may be next.
```

---

# 18. Final Mental Model

Do not think:

```text
"Recursion means a function calls itself."
```

That definition is technically true but too weak for problem solving.

Think:

```text
RECURSION
│
├── Define solve(state)
│
├── Find base case
│
├── Reduce to smaller state(s)
│
├── Trust smaller recursive answers
│
├── Combine those answers
│
├── Visualize the recursion tree
│
└── Track only one active path using the call stack
```

The core mental sequence is:

```text
CURRENT PROBLEM
      ↓
DEFINE WHAT THE FUNCTION PROMISES
      ↓
FIND SMALLER VERSION(S)
      ↓
RECURSE
      ↓
BASE CASE RETURNS
      ↓
ANSWERS FLOW BACK UP
      ↓
CURRENT ANSWER IS COMPLETED
```

And for branching recursion:

```text
                         STATE
                      /         \
                 CHOICE A      CHOICE B
                   /              \
             smaller state    smaller state
                  ...              ...
```

This tree is the bridge from basic recursion to:

```text
Backtracking
Subsets
Subsequences
Permutations
Combination Sum
N-Queens
Grid search
Tree recursion
Dynamic Programming
Divide and Conquer
Kth-generation problems
```

---

## One-page revision summary

```text
RECURSIVE CONTRACT
solve(state) = exact meaning of the function

BASE CASE
smallest state solved directly

TRANSITION
current state → smaller state(s)

COMBINATION
use smaller answers to build current answer

RECURSION TREE
all calls in the complete execution

CALL STACK
only currently active calls

TIME
depends mainly on total work / total calls

STACK SPACE
depends mainly on maximum active depth

REPEATED STATE
consider memoization / DP

CHOICES
consider backtracking

KNOWN SUBTREE SIZE + KTH ANSWER
consider branch counting and skipping
```

---

## Suggested mastery order after this chapter

```text
1. Linear recursion
2. Work before / after recursion
3. Multiple recursive calls
4. Recursion trees
5. Subsequence generation
6. Pick / not-pick
7. Backtracking + undo
8. Permutations
9. Combination problems
10. N-Queens / grid search / pruning
```
