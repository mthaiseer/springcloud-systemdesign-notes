# 015_Catalan_Numbers.md — MiniMathEngine

# Catalan Numbers

> One of the most important recursive combinatorics patterns in CP and FAANG interviews.

---

# Clickable Index

1. [What Are Catalan Numbers?](#1-what-are-catalan-numbers)
2. [Why This Topic Matters](#2-why-this-topic-matters)
3. [Core Formula](#3-core-formula)
4. [Alternative Formula](#4-alternative-formula)
5. [Recursive DP Formula](#5-recursive-dp-formula)
6. [Core Intuition](#6-core-intuition)
7. [Catalan Sequence](#7-catalan-sequence)
8. [When Catalan Appears](#8-when-catalan-appears)
9. [Problem Form 1 — Balanced Parentheses](#9-problem-form-1--balanced-parentheses)
10. [Problem Form 2 — Unique BST Count](#10-problem-form-2--unique-bst-count)
11. [Problem Form 3 — Valid Stack Permutations](#11-problem-form-3--valid-stack-permutations)
12. [Problem Form 4 — Mountain Ranges](#12-problem-form-4--mountain-ranges)
13. [Problem Form 5 — Non-Crossing Chords](#13-problem-form-5--non-crossing-chords)
14. [Dry Run](#14-dry-run)
15. [Common Mistakes](#15-common-mistakes)
16. [Decision Tree](#16-decision-tree)
17. [Real World Mapping](#17-real-world-mapping)
18. [Complexity](#18-complexity)
19. [Reusable C++ Template](#19-reusable-c-template)
20. [CP / FAANG Problem Forms](#20-cp--faang-problem-forms)
21. [Practice Checklist](#21-practice-checklist)
22. [Next Step](#22-next-step)

---

# 1. What Are Catalan Numbers?

Catalan numbers count recursive balanced structures.

Most famous interpretation:

```text
Number of valid balanced parentheses
```

Example:

```text
n = 3
```

Valid sequences:

```text
((()))
(()())
(())()
()(())
()()()
```

Answer:

```text
5
```

---

# 2. Why This Topic Matters

Catalan appears everywhere in:

- recursion
- tree counting
- parsing
- stack validity
- recursive DP
- combinatorics
- grammar parsing
- divide-and-conquer counting

Very common in:

- Google
- Meta
- Codeforces
- AtCoder
- ICPC
- LeetCode Hard

---

# 3. Core Formula

Catalan number:

```text
Cn = (1/(n+1)) * C(2n,n)
```

---

# 4. Alternative Formula

Equivalent formula:

```text
Cn = C(2n,n) - C(2n,n+1)
```

---

# 5. Recursive DP Formula

Catalan recurrence:

```text
C0 = 1
```

```text
Cn =
Σ Ci * C(n-1-i)
```

for:

```text
i = 0 to n-1
```

Meaning:

```text
choose root
multiply left possibilities
with right possibilities
```

---

# 6. Core Intuition

Catalan usually appears when:

```text
A structure recursively splits into:
left part + right part
```

Example:

```text
BST
```

Choose root:

```text
left subtree
right subtree
```

Both independent.

Multiply possibilities.

Then sum over all roots.

---

# 7. Catalan Sequence

```text
n : Catalan

0 : 1
1 : 1
2 : 2
3 : 5
4 : 14
5 : 42
6 : 132
7 : 429
8 : 1430
9 : 4862
```

---

# 8. When Catalan Appears

Signals:

```text
balanced structures
recursive splits
valid stack operations
binary trees
non-crossing structures
nested structures
```

---

# 9. Problem Form 1 — Balanced Parentheses

## Problem

Count valid parentheses sequences using n pairs.

---

## Step-by-Step Working

Example:

```text
n = 3
```

Formula:

```text
C3 =
(1/4) * C(6,3)
```

Compute:

```text
C(6,3)=20
```

Then:

```text
20/4 = 5
```

Answer:

```text
5
```

---

## Valid Structures

```text
((()))
(()())
(())()
()(())
()()()
```

---

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long nCr(int n,int r){

    if(r<0 || r>n) return 0;

    r=min(r,n-r);

    long long ans=1;

    for(int i=1;i<=r;i++){
        ans=ans*(n-r+i)/i;
    }

    return ans;
}

long long catalan(int n){

    return nCr(2*n,n)/(n+1);
}

int main(){

    cout<<catalan(3)<<endl;

    return 0;
}
```

---

# 10. Problem Form 2 — Unique BST Count

## Problem

How many unique BSTs can be formed using:

```text
1...n
```

---

## Core Idea

Choose root.

Everything smaller:

```text
left subtree
```

Everything larger:

```text
right subtree
```

Recursive multiplication.

---

## Step-by-Step Working

Example:

```text
n = 3
```

Possible roots:

```text
1
2
3
```

Root 1:

```text
left=0 nodes
right=2 nodes
ways = C0 * C2
```

Root 2:

```text
left=1
right=1
ways = C1 * C1
```

Root 3:

```text
left=2
right=0
ways = C2 * C0
```

Total:

```text
1*2 + 1*1 + 2*1
=
5
```

---

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){

    int n=10;

    vector<long long> dp(n+1);

    dp[0]=1;

    for(int nodes=1; nodes<=n; nodes++){

        for(int left=0; left<nodes; left++){

            int right=nodes-left-1;

            // choose root
            dp[nodes]+=dp[left]*dp[right];
        }
    }

    cout<<dp[3]<<endl;

    return 0;
}
```

---

# 11. Problem Form 3 — Valid Stack Permutations

## Problem

Count valid push/pop sequences.

---

## Example

Push sequence:

```text
1 2 3
```

How many valid pop sequences exist?

Answer:

```text
Catalan(3)=5
```

---

## Why?

At no point:

```text
pop count > push count
```

Balanced recursive structure.

---

# 12. Problem Form 4 — Mountain Ranges

## Problem

Count mountain ranges with:

```text
n upstrokes
n downstrokes
```

Never go below ground.

---

## Example

```text
UUDD
UDUD
```

These map directly to:

```text
balanced parentheses
```

---

# 13. Problem Form 5 — Non-Crossing Chords

## Problem

Given:

```text
2n points on circle
```

Connect points without intersections.

---

## Example

```text
n = 3
```

Answer:

```text
Catalan(3)=5
```

---

# 14. Dry Run

Compute:

```text
Catalan(4)
```

Formula:

```text
C4 =
(1/5) * C(8,4)
```

Step 1:

```text
C(8,4)=70
```

Step 2:

```text
70/5=14
```

Answer:

```text
14
```

---

# 15. Common Mistakes

## Mistake 1 — Using permutation formula

Catalan is not:

```text
n!
```

---

## Mistake 2 — Forgetting recursive split

Catalan usually involves:

```text
left structure
right structure
```

---

## Mistake 3 — Wrong recurrence

Correct:

```text
Ci * C(n-1-i)
```

NOT:

```text
Ci + C(n-1-i)
```

---

## Mistake 4 — Integer overflow

Catalan grows fast.

Use:

```text
long long
```

or modulo.

---

# 16. Decision Tree

```text
Recursive counting problem?
|
+-- Balanced structure?
|   |
|   +-- Yes
|       |
|       +-- Recursive left/right split?
|           |
|           +-- Yes
|               |
|               +-- Catalan likely
|
+-- Valid parentheses?
|
+-- BST counting?
|
+-- Stack-valid sequence?
|
+-- Non-crossing structures?
|
+-- Mountain ranges?
```

---

# 17. Real World Mapping

| System | Catalan Mapping |
|---|---|
| Compiler parsing | balanced grammar |
| XML parsing | nested tags |
| Expression trees | recursive parsing |
| AST generation | binary tree structures |
| Stack machines | valid push/pop |
| Protocol parsing | nested packet structures |
| Query planners | recursive tree generation |

---

# 18. Complexity

Formula method:

```text
O(n)
```

DP recurrence:

```text
O(n²)
```

---

# 19. Reusable C++ Template

```cpp
#include <bits/stdc++.h>
using namespace std;

long long nCr(int n,int r){

    if(r<0 || r>n) return 0;

    r=min(r,n-r);

    long long ans=1;

    for(int i=1;i<=r;i++){
        ans=ans*(n-r+i)/i;
    }

    return ans;
}

long long catalanFormula(int n){

    return nCr(2*n,n)/(n+1);
}

vector<long long> catalanDP(int n){

    vector<long long> dp(n+1);

    dp[0]=1;

    for(int nodes=1; nodes<=n; nodes++){

        for(int left=0; left<nodes; left++){

            int right=nodes-left-1;

            dp[nodes]+=dp[left]*dp[right];
        }
    }

    return dp;
}

int main(){

    cout<<catalanFormula(4)<<endl;

    vector<long long> dp=catalanDP(10);

    cout<<dp[4]<<endl;

    return 0;
}
```

---

# 20. CP / FAANG Problem Forms

## Problem 1 — Count Balanced Parentheses

### Pattern

```text
Catalan direct formula
```

### Complexity

```text
O(n)
```

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long nCr(int n,int r){

    if(r<0 || r>n) return 0;

    r=min(r,n-r);

    long long ans=1;

    for(int i=1;i<=r;i++)
        ans=ans*(n-r+i)/i;

    return ans;
}

int main(){

    int n=4;

    // Catalan formula
    long long ans=nCr(2*n,n)/(n+1);

    cout<<ans<<endl;

    return 0;
}
```

---

## Problem 2 — Unique BST

### Pattern

```text
Recursive Catalan DP
```

### Transition

```text
dp[nodes]+=dp[left]*dp[right]
```

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main(){

    int n=5;

    vector<long long> dp(n+1);

    dp[0]=1;

    for(int nodes=1; nodes<=n; nodes++){

        for(int left=0; left<nodes; left++){

            int right=nodes-left-1;

            // choose root position
            dp[nodes]+=dp[left]*dp[right];
        }
    }

    cout<<dp[n]<<endl;

    return 0;
}
```

---

## Problem 3 — Non Crossing Chords

### Pattern

```text
Catalan geometry interpretation
```

### Step-by-Step Working

```text
2n points on circle
```

Recursive partitioning.

Answer:

```text
Catalan(n)
```

---

# 21. Practice Checklist

Before solving:

```text
1. Is structure balanced?
2. Is recursion splitting left/right?
3. Is nesting involved?
4. Is stack validity involved?
5. Is BST counting involved?
6. Is non-crossing involved?
7. Can recurrence be:
   left * right ?
8. Does answer match Catalan sequence?
```

---

# 22. Next Step

```text
016_Derangements.md
```

Used in:

```text
Secret Santa
forbidden positions
permutation restrictions
probability problems
```
