# 004_Stack_Parser_Engine.md

> MiniSTLEngine Phase 004  
> Topic: `stack` as a **Parser / Validator / Undo Engine** for CP, DSA, and real-system thinking.

---

## Clickable Index

- [1. Goal](#1-goal)
- [2. Why Stack Is An Engine](#2-why-stack-is-an-engine)
- [3. Real-System Mental Model](#3-real-system-mental-model)
- [4. Stack Core Behavior](#4-stack-core-behavior)
- [5. Stack Operations Cheat Sheet](#5-stack-operations-cheat-sheet)
- [6. CP/DSA Recognition](#6-cpdsa-recognition)
- [7. Engine Architecture](#7-engine-architecture)
- [8. Basic Stack Parser Engine](#8-basic-stack-parser-engine)
- [9. Dry Run: Push And Pop](#9-dry-run-push-and-pop)
- [10. CP Pattern 1: Balanced Parentheses](#10-cp-pattern-1-balanced-parentheses)
- [11. CP Pattern 2: Expression Evaluation](#11-cp-pattern-2-expression-evaluation)
- [12. CP Pattern 3: Remove Adjacent Duplicates](#12-cp-pattern-3-remove-adjacent-duplicates)
- [13. CP Pattern 4: Min Stack](#13-cp-pattern-4-min-stack)
- [14. CP Pattern 5: Undo Operation Engine](#14-cp-pattern-5-undo-operation-engine)
- [15. Stack vs Recursion](#15-stack-vs-recursion)
- [16. Common Mistakes](#16-common-mistakes)
- [17. Complexity Table](#17-complexity-table)
- [18. Real-World Mapping](#18-real-world-mapping)
- [19. Final Mental Model](#19-final-mental-model)
- [20. Next Step](#20-next-step)

---

# 1. Goal

Learn `stack` not only as LIFO syntax, but as a:

```text
Parser / Validator / Undo Engine
```

It helps you solve:

```text
balanced brackets
expression evaluation
undo operations
DFS simulation
nested structures
HTML/XML validation
function call simulation
monotonic stack problems
```

---

# 2. Why Stack Is An Engine

A stack follows:

```text
Last In, First Out
```

Meaning:

```text
the last unresolved thing is handled first
```

This is perfect for:

```text
nested structures
recent history
backtracking
parsing
undo
matching pairs
```

Normal thinking:

```cpp
stack<int> st;
```

Engine thinking:

```text
StackParserEngine
    remembers unresolved items
    checks newest item first
    resolves nested structures
    supports undo/backtracking
```

---

# 3. Real-System Mental Model

Stacks appear in real systems:

```text
browser back button
undo in editor
function call stack
compiler parser
JSON/XML parser
HTML tag validator
transaction rollback stack
DFS traversal
```

Architecture:

```text
Input Stream
    |
    v
StackParserEngine
    |
    +--> push open/unresolved item
    +--> pop when resolved
    +--> validate nesting
    +--> detect mismatch
    |
    v
Valid / Invalid / Processed Result
```

---

# 4. Stack Core Behavior

Example:

```text
push 10
push 20
push 30
```

Stack:

```text
top
30
20
10
bottom
```

Pop order:

```text
30 -> 20 -> 10
```

That is why stack is useful when:

```text
latest item must be processed first
```

---

# 5. Stack Operations Cheat Sheet

```cpp
stack<int> st;

st.push(10);     // insert at top
st.top();        // read top
st.pop();        // remove top
st.empty();      // check empty
st.size();       // number of elements
```

Important:

```cpp
st.pop();
```

does not return value.

Correct:

```cpp
int x = st.top();
st.pop();
```

---

# 6. CP/DSA Recognition

Use stack when problem says:

```text
balanced brackets
nearest greater/smaller
undo
remove adjacent
nested expression
path simplification
DFS iterative
evaluate expression
valid parser
```

Hidden mapping:

| Problem clue | Stack pattern |
|---|---|
| matching pairs | normal stack |
| nested structure | parser stack |
| remove previous based on current | stack as history |
| next greater/smaller | monotonic stack |
| undo operation | operation stack |
| recursion simulation | explicit stack |
| valid path | stack of directories |

---

# 7. Engine Architecture

```text
MiniStackParserEngine
├── push unresolved item
├── top inspection
├── pop resolved item
├── bracket validator
├── expression evaluator
├── duplicate remover
├── min stack helper
└── undo operation engine
```

---

# 8. Basic Stack Parser Engine

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class StackParserEngine {
private:
    stack<char> st;

public:
    void pushItem(char ch) {
        // WHY:
        // Push means "this item is unresolved".
        // Example: opening bracket waiting for closing bracket.
        st.push(ch);
    }

    char topItem() {
        if (st.empty()) {
            throw runtime_error("Stack is empty");
        }

        return st.top();
    }

    void popItem() {
        if (!st.empty()) {
            // Pop means "latest unresolved item is now resolved".
            st.pop();
        }
    }

    bool isEmpty() {
        return st.empty();
    }

    int size() {
        return st.size();
    }
};

int main() {
    StackParserEngine engine;

    engine.pushItem('(');
    engine.pushItem('{');
    engine.pushItem('[');

    cout << "Top item: " << engine.topItem() << endl;

    engine.popItem();

    cout << "Top after pop: " << engine.topItem() << endl;

    return 0;
}
```

---

# 9. Dry Run: Push And Pop

Operations:

```text
push '('
push '{'
push '['
```

Stack:

```text
top
[
{
(
bottom
```

Then:

```text
pop
```

Stack:

```text
top
{
(
bottom
```

Key idea:

```text
latest unresolved item is always on top
```

---

# 10. CP Pattern 1: Balanced Parentheses

## Problem Type

```text
Given string with brackets, check if valid.
```

Example:

```text
s = "[{()}]"
answer = true
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool isBalanced(string s) {
    stack<char> st;

    unordered_map<char, char> closeToOpen = {
        {')', '('},
        {'}', '{'},
        {']', '['}
    };

    for (char ch : s) {
        if (ch == '(' || ch == '{' || ch == '[') {
            // Opening bracket is unresolved.
            // Store it until a matching closing bracket appears.
            st.push(ch);
        } else if (closeToOpen.count(ch)) {
            // Closing bracket must resolve the latest opening bracket.
            if (st.empty()) {
                return false;
            }

            if (st.top() != closeToOpen[ch]) {
                return false;
            }

            // Matched pair resolved.
            st.pop();
        }
    }

    // All openings must be resolved.
    return st.empty();
}

int main() {
    string s = "[{()}]";

    cout << isBalanced(s) << endl; // 1

    return 0;
}
```

## Dry Run

```text
s = [{()}]

read [ -> push [
stack = [

read { -> push {
stack = [ {

read ( -> push (
stack = [ { (

read ) -> top is (, pop
stack = [ {

read } -> top is {, pop
stack = [

read ] -> top is [, pop
stack = empty

answer = valid
```

## Pattern Recognition

Use when:

```text
latest opening must match current closing
nested validity matters
```

Real mapping:

```text
HTML/XML parser
JSON parser
compiler syntax checker
```

---

# 11. CP Pattern 2: Expression Evaluation

## Problem Type

```text
Evaluate simple Reverse Polish Notation expression.
```

Example:

```text
tokens = ["2", "1", "+", "3", "*"]
meaning = (2 + 1) * 3
answer = 9
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int evalRPN(vector<string>& tokens) {
    stack<int> st;

    for (string token : tokens) {
        if (token == "+" || token == "-" || token == "*" || token == "/") {
            // Operator resolves the latest two numbers.
            int b = st.top(); st.pop();
            int a = st.top(); st.pop();

            int result = 0;

            if (token == "+") result = a + b;
            if (token == "-") result = a - b;
            if (token == "*") result = a * b;
            if (token == "/") result = a / b;

            // Push result back because it may be used by future operators.
            st.push(result);
        } else {
            // Number is waiting for future operator.
            st.push(stoi(token));
        }
    }

    return st.top();
}

int main() {
    vector<string> tokens = {"2", "1", "+", "3", "*"};

    cout << evalRPN(tokens) << endl; // 9

    return 0;
}
```

## Dry Run

```text
2 -> push 2
stack = [2]

1 -> push 1
stack = [2,1]

+ -> pop 1 and 2 -> 2 + 1 = 3 -> push 3
stack = [3]

3 -> push 3
stack = [3,3]

* -> pop 3 and 3 -> 3 * 3 = 9 -> push 9
stack = [9]

answer = 9
```

---

# 12. CP Pattern 3: Remove Adjacent Duplicates

## Problem Type

```text
Remove adjacent equal characters repeatedly.
```

Example:

```text
s = "abbaca"
answer = "ca"
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

string removeDuplicates(string s) {
    string st;

    for (char ch : s) {
        // Here string is used as stack.
        // back() is top.
        if (!st.empty() && st.back() == ch) {
            // Current char cancels latest same char.
            st.pop_back();
        } else {
            // Current char is unresolved, keep it.
            st.push_back(ch);
        }
    }

    return st;
}

int main() {
    string s = "abbaca";

    cout << removeDuplicates(s) << endl; // ca

    return 0;
}
```

## Dry Run

```text
s = abbaca

a -> push       stack = a
b -> push       stack = ab
b -> duplicate  stack = a
a -> duplicate  stack = empty
c -> push       stack = c
a -> push       stack = ca

answer = ca
```

## Key Insight

```text
If current character can cancel previous character,
stack is a natural fit.
```

---

# 13. CP Pattern 4: Min Stack

## Problem Type

Design stack that supports:

```text
push
pop
top
getMin
```

all in:

```text
O(1)
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class MinStack {
private:
    stack<int> values;
    stack<int> minimums;

public:
    void push(int x) {
        values.push(x);

        // Store current minimum at every stack level.
        // This makes getMin O(1).
        if (minimums.empty()) {
            minimums.push(x);
        } else {
            minimums.push(min(x, minimums.top()));
        }
    }

    void pop() {
        values.pop();
        minimums.pop();
    }

    int top() {
        return values.top();
    }

    int getMin() {
        return minimums.top();
    }
};

int main() {
    MinStack st;

    st.push(5);
    st.push(2);
    st.push(7);

    cout << st.getMin() << endl; // 2

    st.pop();

    cout << st.getMin() << endl; // 2

    st.pop();

    cout << st.getMin() << endl; // 5

    return 0;
}
```

## Mental Model

```text
values stack   = actual data
minimums stack = minimum snapshot at each level
```

This is like:

```text
versioned metadata
```

---

# 14. CP Pattern 5: Undo Operation Engine

## Problem Type

```text
Support undo last operation.
```

Real systems:

```text
text editor undo
database rollback
browser back
command history
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

class UndoEngine {
private:
    string document;
    stack<string> history;

public:
    void typeText(string text) {
        // Save current state before modifying.
        // This allows rollback.
        history.push(document);

        document += text;
    }

    void undo() {
        if (!history.empty()) {
            document = history.top();
            history.pop();
        }
    }

    string getDocument() {
        return document;
    }
};

int main() {
    UndoEngine editor;

    editor.typeText("Hello");
    editor.typeText(" World");

    cout << editor.getDocument() << endl; // Hello World

    editor.undo();

    cout << editor.getDocument() << endl; // Hello

    editor.undo();

    cout << editor.getDocument() << endl; // empty

    return 0;
}
```

## Key Idea

```text
Before every change, save old state.
Undo = restore latest saved state.
```

---

# 15. Stack vs Recursion

Recursion internally uses call stack.

Example recursive DFS:

```text
dfs(node)
    dfs(child)
```

Can be rewritten using explicit stack:

```cpp
stack<int> st;
st.push(start);
```

Use explicit stack when:

```text
recursion depth may overflow
need iterative control
want to simulate backtracking
```

---

# 16. Common Mistakes

## Mistake 1: Calling top On Empty Stack

Wrong:

```cpp
if (st.top() == '(') {
}
```

Correct:

```cpp
if (!st.empty() && st.top() == '(') {
}
```

---

## Mistake 2: Thinking pop Returns Value

Wrong:

```cpp
int x = st.pop();
```

Correct:

```cpp
int x = st.top();
st.pop();
```

---

## Mistake 3: Wrong Order In Expression Evaluation

For subtraction/division:

```cpp
int b = st.top(); st.pop();
int a = st.top(); st.pop();

result = a - b;
```

not:

```cpp
b - a
```

---

## Mistake 4: Using Stack When Queue Is Needed

Stack:

```text
LIFO
```

Queue:

```text
FIFO
```

For BFS, use queue, not stack.

---

# 17. Complexity Table

| Operation | Complexity |
|---|---:|
| push | O(1) |
| pop | O(1) |
| top | O(1) |
| empty | O(1) |
| size | O(1) |
| scan string with stack | O(N) |
| stack space | O(N) |

---

# 18. Real-World Mapping

| Stack Concept | Real-System Meaning |
|---|---|
| push unresolved item | open tag / open transaction |
| pop resolved item | close tag / commit step |
| top check | latest active context |
| balanced brackets | syntax validation |
| RPN evaluation | expression engine |
| undo stack | editor/database rollback |
| DFS stack | traversal engine |
| min stack metadata | versioned aggregate state |

---

# 19. Final Mental Model

Stack is:

```text
latest unresolved item engine
```

Best for:

```text
nested structures
matching pairs
undo/history
expression evaluation
DFS simulation
monotonic boundary problems
```

One-line CP rule:

```text
If current item resolves the latest previous item, think stack.
```

One-line system rule:

```text
Stack stores active context and lets systems return to the previous state.
```

---

# 20. Next Step

Next file:

```text
005_Queue_Event_Buffer_Engine.md
```

Then:

```text
006_Deque_Sliding_Window_Engine.md
007_PriorityQueue_Scheduler_Engine.md
008_Set_Ordered_Index_Engine.md
```
