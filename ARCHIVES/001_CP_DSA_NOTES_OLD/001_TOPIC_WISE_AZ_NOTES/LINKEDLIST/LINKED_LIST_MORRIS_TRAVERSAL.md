# Morris Traversal CP/DSA Master Guide

> **Goal:** Master Morris Traversal for interviews, FAANG-style tree problems, and CP-style O(1) space traversal questions.
>
> Morris Traversal = Binary Tree Traversal using **O(1) extra space** by creating temporary threaded links.

---

## Clickable Index

### 0. Master Map
- [0.1 What Is Morris Traversal?](#01-what-is-morris-traversal)
- [0.2 Why Morris Traversal Works](#02-why-morris-traversal-works)
- [0.3 Threading Mental Model](#03-threading-mental-model)
- [0.4 Complexity](#04-complexity)
- [0.5 When To Use Morris](#05-when-to-use-morris)

### 1. Core Technique
- [1.1 Inorder Predecessor](#11-inorder-predecessor)
- [1.2 Two Cases For Every Node](#12-two-cases-for-every-node)
- [1.3 Morris Inorder Template](#13-morris-inorder-template)
- [1.4 Morris Preorder Template](#14-morris-preorder-template)
- [1.5 Morris Postorder Idea](#15-morris-postorder-idea)

### 2. Phase-Wise Problems
- [Phase 1 — Foundation](#phase-1--foundation)
- [Phase 2 — BST Applications](#phase-2--bst-applications)
- [Phase 3 — Advanced Morris](#phase-3--advanced-morris)
- [Phase 4 — Interview Hard Mode](#phase-4--interview-hard-mode)

### 3. Dry Run Index
- [Dry Run A — Morris Inorder](#dry-run-a--morris-inorder)
- [Dry Run B — Morris Preorder](#dry-run-b--morris-preorder)
- [Dry Run C — Kth Smallest in BST](#dry-run-c--kth-smallest-in-bst)
- [Dry Run D — Validate BST](#dry-run-d--validate-bst)

### 4. Cheat Sheets
- [4.1 Inorder vs Preorder Difference](#41-inorder-vs-preorder-difference)
- [4.2 Pattern Recognition](#42-pattern-recognition)
- [4.3 Common Bugs](#43-common-bugs)
- [4.4 Final Practice List](#44-final-practice-list)

---

# 0. Master Map

## 0.1 What Is Morris Traversal?

Morris Traversal is a technique to traverse a binary tree without:

- recursion
- explicit stack
- extra O(H) memory

It uses temporary links called **threads**.

Normal inorder traversal needs a return path after finishing the left subtree.

Morris creates that return path manually:

```text
predecessor.right = current
```

After returning, it removes the link:

```text
predecessor.right = NULL
```

So the original tree is restored.

---

## 0.2 Why Morris Traversal Works

For inorder traversal:

```text
LEFT -> ROOT -> RIGHT
```

When we are at `current`, if it has a left child, we must visit all nodes in the left subtree first.

But after finishing the left subtree, how do we return to `current` without recursion or stack?

Answer:

```text
Find rightmost node of current.left
Connect that node's right pointer back to current
```

This rightmost node is called the **inorder predecessor**.

---

## 0.3 Threading Mental Model

Tree:

```text
        4
       / \
      2   6
     / \
    1   3
```

At node `4`:

```text
current = 4
current.left = 2
predecessor = rightmost node in left subtree = 3
```

Create temporary thread:

```text
3.right = 4
```

Diagram:

```text
        4
       / \
      2   6
     / \
    1   3
         \
          ─────► 4  temporary thread
```

Meaning:

```text
After finishing 3, return to 4
```

---

## 0.4 Complexity

| Metric | Complexity |
|---|---|
| Time | O(N) |
| Extra Space | O(1) |
| Tree Restored? | Yes, if threads are removed |

Why O(N)?

Each edge is traversed at most twice:

1. once while creating a thread
2. once while removing a thread

---

## 0.5 When To Use Morris

Use Morris when the problem says:

- traverse tree in O(1) space
- inorder without stack/recursion
- BST traversal with no extra memory
- kth smallest in BST using O(1) space
- validate BST with O(1) space
- recover BST with O(1) space

---

# 1. Core Technique

## 1.1 Inorder Predecessor

For a node `current`, its inorder predecessor is:

```text
rightmost node inside current.left subtree
```

Code:

```cpp
TreeNode* pred = curr->left;
while (pred->right != NULL && pred->right != curr) {
    pred = pred->right;
}
```

Why stop at `pred->right != curr`?

Because if a thread already exists, we must not loop forever.

---

## 1.2 Two Cases For Every Node

For every `curr`:

```text
Case 1: curr->left == NULL
    Visit curr
    Move right

Case 2: curr->left != NULL
    Find predecessor

    If predecessor->right == NULL:
        Create thread
        Move left

    Else predecessor->right == curr:
        Remove thread
        Visit curr
        Move right
```

Diagram:

```text
curr has no left?
│
├── YES
│   ├── visit curr
│   └── curr = curr.right
│
└── NO
    ├── pred = rightmost(curr.left)
    │
    ├── pred.right == NULL?
    │   ├── create thread pred.right = curr
    │   └── curr = curr.left
    │
    └── pred.right == curr?
        ├── remove thread pred.right = NULL
        ├── visit curr
        └── curr = curr.right
```

---

## 1.3 Morris Inorder Template

### Problem Statement

Given the root of a binary tree, return its inorder traversal using O(1) extra space.

### Input Example

```text
Tree:
        4
       / \
      2   6
     / \
    1   3
```

### Expected Output

```text
1 2 3 4 6
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct TreeNode {
    int val;
    TreeNode* left;
    TreeNode* right;
    TreeNode(int x) : val(x), left(NULL), right(NULL) {}
};

vector<int> morrisInorder(TreeNode* root) {
    vector<int> ans;
    TreeNode* curr = root;

    while (curr != NULL) {
        if (curr->left == NULL) {
            ans.push_back(curr->val);
            curr = curr->right;
        } else {
            TreeNode* pred = curr->left;

            while (pred->right != NULL && pred->right != curr) {
                pred = pred->right;
            }

            if (pred->right == NULL) {
                pred->right = curr;
                curr = curr->left;
            } else {
                pred->right = NULL;
                ans.push_back(curr->val);
                curr = curr->right;
            }
        }
    }

    return ans;
}
```

---

## 1.4 Morris Preorder Template

Preorder:

```text
ROOT -> LEFT -> RIGHT
```

Difference from inorder:

```text
Visit before creating thread
```

### C++ Code

```cpp
vector<int> morrisPreorder(TreeNode* root) {
    vector<int> ans;
    TreeNode* curr = root;

    while (curr != NULL) {
        if (curr->left == NULL) {
            ans.push_back(curr->val);
            curr = curr->right;
        } else {
            TreeNode* pred = curr->left;

            while (pred->right != NULL && pred->right != curr) {
                pred = pred->right;
            }

            if (pred->right == NULL) {
                ans.push_back(curr->val);
                pred->right = curr;
                curr = curr->left;
            } else {
                pred->right = NULL;
                curr = curr->right;
            }
        }
    }

    return ans;
}
```

---

## 1.5 Morris Postorder Idea

Postorder:

```text
LEFT -> RIGHT -> ROOT
```

Morris postorder is harder because root must be visited after both subtrees.

Common technique:

1. Create dummy node.
2. dummy.left = root.
3. Use Morris traversal.
4. Reverse right boundary paths temporarily.
5. Visit reversed path.
6. Restore path.

For FAANG, know the idea. Most interviews ask inorder/preorder more often.

---

# Phase 1 — Foundation

## Problem 1 — Morris Inorder Traversal

### Pattern

```text
O(1) space inorder traversal
```

### Problem Statement

Return inorder traversal of binary tree without recursion and without stack.

### Input

```text
        4
       / \
      2   6
     / \
    1   3
```

### Output

```text
[1, 2, 3, 4, 6]
```

### Code

```cpp
vector<int> inorderTraversal(TreeNode* root) {
    vector<int> ans;
    TreeNode* curr = root;

    while (curr) {
        if (!curr->left) {
            ans.push_back(curr->val);
            curr = curr->right;
        } else {
            TreeNode* pred = curr->left;
            while (pred->right && pred->right != curr) {
                pred = pred->right;
            }

            if (!pred->right) {
                pred->right = curr;
                curr = curr->left;
            } else {
                pred->right = NULL;
                ans.push_back(curr->val);
                curr = curr->right;
            }
        }
    }

    return ans;
}
```

### Index-by-Index Diagrammatic Dry Run

Tree:

```text
        4
       / \
      2   6
     / \
    1   3
```

#### Step Table

| Step | curr | curr.left? | pred | Action | ans |
|---|---:|---|---:|---|---|
| 1 | 4 | yes | 3 | create `3.right = 4`, move left | [] |
| 2 | 2 | yes | 1 | create `1.right = 2`, move left | [] |
| 3 | 1 | no | - | visit 1, move right via thread | [1] |
| 4 | 2 | yes | 1 | remove `1.right`, visit 2, move right | [1,2] |
| 5 | 3 | no | - | visit 3, move right via thread | [1,2,3] |
| 6 | 4 | yes | 3 | remove `3.right`, visit 4, move right | [1,2,3,4] |
| 7 | 6 | no | - | visit 6, move right | [1,2,3,4,6] |

#### Tree-State Diagram

```text
Start:
        4
       / \
      2   6
     / \
    1   3

Step 1: create 3.right -> 4
        4
       / \
      2   6
     / \
    1   3 ───► 4

Step 2: create 1.right -> 2
        4
       / \
      2   6
     / \
    1 ─► 2  3 ───► 4

Step 3: visit 1, follow thread to 2
ans = [1]

Step 4: remove 1.right, visit 2
ans = [1,2]

Step 5: visit 3, follow thread to 4
ans = [1,2,3]

Step 6: remove 3.right, visit 4
ans = [1,2,3,4]

Step 7: visit 6
ans = [1,2,3,4,6]
```

---

## Problem 2 — Morris Preorder Traversal

### Pattern

```text
O(1) space preorder traversal
```

### Problem Statement

Return preorder traversal of binary tree without recursion and stack.

### Input

```text
        4
       / \
      2   6
     / \
    1   3
```

### Output

```text
[4, 2, 1, 3, 6]
```

### Code

```cpp
vector<int> preorderTraversal(TreeNode* root) {
    vector<int> ans;
    TreeNode* curr = root;

    while (curr) {
        if (!curr->left) {
            ans.push_back(curr->val);
            curr = curr->right;
        } else {
            TreeNode* pred = curr->left;
            while (pred->right && pred->right != curr) {
                pred = pred->right;
            }

            if (!pred->right) {
                ans.push_back(curr->val);
                pred->right = curr;
                curr = curr->left;
            } else {
                pred->right = NULL;
                curr = curr->right;
            }
        }
    }

    return ans;
}
```

### Index-by-Index Diagrammatic Dry Run

| Step | curr | pred | Action | ans |
|---|---:|---:|---|---|
| 1 | 4 | 3 | visit 4, create `3.right=4`, move left | [4] |
| 2 | 2 | 1 | visit 2, create `1.right=2`, move left | [4,2] |
| 3 | 1 | - | visit 1, move right via thread | [4,2,1] |
| 4 | 2 | 1 | remove thread, move right | [4,2,1] |
| 5 | 3 | - | visit 3, move right via thread | [4,2,1,3] |
| 6 | 4 | 3 | remove thread, move right | [4,2,1,3] |
| 7 | 6 | - | visit 6 | [4,2,1,3,6] |

#### Why Visit Timing Changes?

In preorder:

```text
ROOT first
```

So when creating a thread for the first time, we visit `curr` immediately.

---

# Phase 2 — BST Applications

## Problem 3 — Kth Smallest Element in BST

### Pattern

```text
BST + inorder sorted order + Morris traversal
```

### Problem Statement

Given a BST and integer `k`, return the kth smallest element using O(1) extra space.

### Input

```text
BST:
        5
       / \
      3   7
     / \   \
    2   4   8

k = 3
```

### Output

```text
4
```

Because inorder is:

```text
2, 3, 4, 5, 7, 8
```

3rd smallest = 4.

### Code

```cpp
int kthSmallest(TreeNode* root, int k) {
    TreeNode* curr = root;
    int count = 0;

    while (curr) {
        if (!curr->left) {
            count++;
            if (count == k) return curr->val;
            curr = curr->right;
        } else {
            TreeNode* pred = curr->left;
            while (pred->right && pred->right != curr) {
                pred = pred->right;
            }

            if (!pred->right) {
                pred->right = curr;
                curr = curr->left;
            } else {
                pred->right = NULL;
                count++;
                if (count == k) return curr->val;
                curr = curr->right;
            }
        }
    }

    return -1;
}
```

### Index-by-Index Diagrammatic Dry Run

Input BST:

```text
        5
       / \
      3   7
     / \   \
    2   4   8
```

k = 3

| Visit Order | Node | count | Is kth? |
|---:|---:|---:|---|
| 1 | 2 | 1 | no |
| 2 | 3 | 2 | no |
| 3 | 4 | 3 | yes, return 4 |

Threading path:

```text
Step 1: curr=5, pred=4, create 4.right -> 5
Step 2: curr=3, pred=2, create 2.right -> 3
Step 3: curr=2, visit 2, count=1
Step 4: curr=3, remove 2.right, visit 3, count=2
Step 5: curr=4, visit 4, count=3 -> answer
```

---

## Problem 4 — Validate BST Using Morris Traversal

### Pattern

```text
BST inorder must be strictly increasing
```

### Problem Statement

Check whether a binary tree is a valid BST using O(1) extra space.

### Input

```text
        5
       / \
      3   7
     / \   \
    2   4   8
```

### Output

```text
true
```

### Code

```cpp
bool isValidBST(TreeNode* root) {
    TreeNode* curr = root;
    TreeNode* prev = NULL;

    while (curr) {
        if (!curr->left) {
            if (prev && prev->val >= curr->val) return false;
            prev = curr;
            curr = curr->right;
        } else {
            TreeNode* pred = curr->left;
            while (pred->right && pred->right != curr) {
                pred = pred->right;
            }

            if (!pred->right) {
                pred->right = curr;
                curr = curr->left;
            } else {
                pred->right = NULL;

                if (prev && prev->val >= curr->val) return false;
                prev = curr;

                curr = curr->right;
            }
        }
    }

    return true;
}
```

### Index-by-Index Diagrammatic Dry Run

Valid BST inorder:

```text
2 -> 3 -> 4 -> 5 -> 7 -> 8
```

| Step | Visited Node | prev before | Check | Result |
|---|---:|---:|---|---|
| 1 | 2 | NULL | ok | prev=2 |
| 2 | 3 | 2 | 2 < 3 | prev=3 |
| 3 | 4 | 3 | 3 < 4 | prev=4 |
| 4 | 5 | 4 | 4 < 5 | prev=5 |
| 5 | 7 | 5 | 5 < 7 | prev=7 |
| 6 | 8 | 7 | 7 < 8 | prev=8 |

If at any point:

```text
prev.val >= curr.val
```

then it is not a BST.

---

# Phase 3 — Advanced Morris

## Problem 5 — Recover Binary Search Tree

### Pattern

```text
BST inorder should be sorted
Two nodes are swapped
Detect inversions using Morris inorder
```

### Problem Statement

Two nodes in a BST are swapped by mistake. Recover the tree without changing its structure.

### Example

Wrong BST inorder:

```text
1, 5, 3, 4, 2, 6
```

Inversions:

```text
5 > 3
4 > 2
```

Swapped nodes:

```text
5 and 2
```

### Code

```cpp
void recoverTree(TreeNode* root) {
    TreeNode* curr = root;
    TreeNode* prev = NULL;
    TreeNode* first = NULL;
    TreeNode* second = NULL;

    auto detect = [&](TreeNode* node) {
        if (prev && prev->val > node->val) {
            if (!first) first = prev;
            second = node;
        }
        prev = node;
    };

    while (curr) {
        if (!curr->left) {
            detect(curr);
            curr = curr->right;
        } else {
            TreeNode* pred = curr->left;
            while (pred->right && pred->right != curr) {
                pred = pred->right;
            }

            if (!pred->right) {
                pred->right = curr;
                curr = curr->left;
            } else {
                pred->right = NULL;
                detect(curr);
                curr = curr->right;
            }
        }
    }

    if (first && second) {
        swap(first->val, second->val);
    }
}
```

### Index-by-Index Inversion Dry Run

Inorder stream:

```text
1, 5, 3, 4, 2, 6
```

| Step | prev | curr | Check | first | second |
|---|---:|---:|---|---:|---:|
| 1 | NULL | 1 | ok | - | - |
| 2 | 1 | 5 | 1 < 5 | - | - |
| 3 | 5 | 3 | inversion | 5 | 3 |
| 4 | 3 | 4 | 3 < 4 | 5 | 3 |
| 5 | 4 | 2 | inversion | 5 | 2 |
| 6 | 2 | 6 | 2 < 6 | 5 | 2 |

Final:

```text
swap(5, 2)
```

---

## Problem 6 — Flatten Binary Tree To Linked List

### Pattern

```text
Morris-style rewiring
```

### Problem Statement

Flatten binary tree into preorder linked list in-place.

### Input

```text
        1
       / \
      2   5
     / \   \
    3   4   6
```

### Output

```text
1 -> 2 -> 3 -> 4 -> 5 -> 6
```

### Code

```cpp
void flatten(TreeNode* root) {
    TreeNode* curr = root;

    while (curr) {
        if (curr->left) {
            TreeNode* pred = curr->left;

            while (pred->right) {
                pred = pred->right;
            }

            pred->right = curr->right;
            curr->right = curr->left;
            curr->left = NULL;
        }

        curr = curr->right;
    }
}
```

### Diagrammatic Dry Run

Initial:

```text
        1
       / \
      2   5
     / \   \
    3   4   6
```

At `1`:

```text
left subtree = 2
right subtree = 5
rightmost of left subtree = 4
```

Rewire:

```text
4.right = 5
1.right = 2
1.left = NULL
```

Now:

```text
1
 \
  2
 / \
3   4
     \
      5
       \
        6
```

At `2`:

```text
rightmost of left subtree = 3
3.right = 4
2.right = 3
2.left = NULL
```

Final:

```text
1 -> 2 -> 3 -> 4 -> 5 -> 6
```

---

# Phase 4 — Interview Hard Mode

## Problem 7 — Morris Postorder Traversal

### Pattern

```text
Dummy node + reverse right boundary
```

### Problem Statement

Return postorder traversal using O(1) extra space.

### Input

```text
        1
       / \
      2   3
     / \
    4   5
```

### Output

```text
[4, 5, 2, 3, 1]
```

### Code

```cpp
void reversePath(TreeNode* from, TreeNode* to) {
    if (from == to) return;

    TreeNode* x = from;
    TreeNode* y = from->right;
    TreeNode* z;

    while (x != to) {
        z = y->right;
        y->right = x;
        x = y;
        y = z;
    }
}

void collectReverse(TreeNode* from, TreeNode* to, vector<int>& ans) {
    reversePath(from, to);

    TreeNode* node = to;
    while (true) {
        ans.push_back(node->val);
        if (node == from) break;
        node = node->right;
    }

    reversePath(to, from);
}

vector<int> morrisPostorder(TreeNode* root) {
    vector<int> ans;
    TreeNode dummy(0);
    dummy.left = root;

    TreeNode* curr = &dummy;

    while (curr) {
        if (!curr->left) {
            curr = curr->right;
        } else {
            TreeNode* pred = curr->left;

            while (pred->right && pred->right != curr) {
                pred = pred->right;
            }

            if (!pred->right) {
                pred->right = curr;
                curr = curr->left;
            } else {
                pred->right = NULL;
                collectReverse(curr->left, pred, ans);
                curr = curr->right;
            }
        }
    }

    return ans;
}
```

### Mental Model

Postorder wants:

```text
LEFT -> RIGHT -> ROOT
```

Morris postorder delays visiting until we come back from a subtree. Then it prints the right boundary in reverse.

---

# Dry Run A — Morris Inorder

Input:

```text
        4
       / \
      2   6
     / \
    1   3
```

Execution flow:

```text
curr=4
├── left exists
├── pred=3
├── create 3.right=4
└── curr=2

curr=2
├── left exists
├── pred=1
├── create 1.right=2
└── curr=1

curr=1
├── no left
├── visit 1
└── curr=1.right(thread)=2

curr=2
├── pred=1
├── thread exists
├── remove 1.right
├── visit 2
└── curr=3

curr=3
├── no left
├── visit 3
└── curr=3.right(thread)=4

curr=4
├── pred=3
├── thread exists
├── remove 3.right
├── visit 4
└── curr=6

curr=6
├── no left
├── visit 6
└── curr=NULL
```

Output:

```text
1 2 3 4 6
```

---

# Dry Run B — Morris Preorder

Input:

```text
        4
       / \
      2   6
     / \
    1   3
```

Execution flow:

```text
curr=4
├── left exists
├── pred=3
├── visit 4
├── create 3.right=4
└── curr=2

curr=2
├── left exists
├── pred=1
├── visit 2
├── create 1.right=2
└── curr=1

curr=1
├── no left
├── visit 1
└── curr=2 via thread

curr=2
├── thread exists
├── remove thread
└── curr=3

curr=3
├── no left
├── visit 3
└── curr=4 via thread

curr=4
├── thread exists
├── remove thread
└── curr=6

curr=6
├── no left
├── visit 6
└── curr=NULL
```

Output:

```text
4 2 1 3 6
```

---

# Dry Run C — Kth Smallest in BST

Input:

```text
        5
       / \
      3   7
     / \   \
    2   4   8

k = 3
```

Execution:

```text
Morris inorder visit stream:
2 -> 3 -> 4 -> 5 -> 7 -> 8
```

Index-by-index:

```text
visit 2: count = 1
visit 3: count = 2
visit 4: count = 3 -> answer = 4
```

---

# Dry Run D — Validate BST

Input:

```text
        5
       / \
      3   7
     / \   \
    2   4   8
```

Inorder stream:

```text
2, 3, 4, 5, 7, 8
```

Check:

```text
prev=NULL, curr=2 -> ok
prev=2, curr=3 -> 2 < 3 ok
prev=3, curr=4 -> 3 < 4 ok
prev=4, curr=5 -> 4 < 5 ok
prev=5, curr=7 -> 5 < 7 ok
prev=7, curr=8 -> 7 < 8 ok
```

Answer:

```text
true
```

---

# 4. Cheat Sheets

## 4.1 Inorder vs Preorder Difference

| Traversal | Visit When No Left | Visit When Thread Created | Visit When Thread Removed |
|---|---|---|---|
| Inorder | yes | no | yes |
| Preorder | yes | yes | no |
| Postorder | no direct | no direct | collect reverse boundary |

---

## 4.2 Pattern Recognition

| Problem Clue | Technique |
|---|---|
| O(1) inorder traversal | Morris inorder |
| BST kth smallest | Morris inorder + counter |
| Validate BST O(1) space | Morris inorder + prev |
| Recover swapped BST | Morris inorder + inversion detection |
| Preorder without stack | Morris preorder |
| Flatten tree | Morris-style rewiring |
| Postorder O(1) | Dummy + reverse path |

---

## 4.3 Common Bugs

### Bug 1 — Forgetting to Restore Thread

Wrong:

```cpp
// forgot this
pred->right = NULL;
```

Effect:

```text
Tree becomes corrupted
Infinite loop possible
```

---

### Bug 2 — Wrong Predecessor Loop

Correct:

```cpp
while (pred->right && pred->right != curr) {
    pred = pred->right;
}
```

Wrong:

```cpp
while (pred->right) {
    pred = pred->right;
}
```

This can loop forever after a thread is created.

---

### Bug 3 — Visiting At Wrong Time

Inorder:

```text
visit after removing thread
```

Preorder:

```text
visit before creating thread
```

---

### Bug 4 — Returning Early Before Restoring Tree

In kth smallest, if you return immediately after finding kth, some temporary threads may remain.

For interview simplicity, many solutions return immediately, but strictly safe production-style code should restore all threads before returning.

Safe version:

```cpp
int kthSmallestSafe(TreeNode* root, int k) {
    TreeNode* curr = root;
    int count = 0;
    int ans = -1;
    bool found = false;

    while (curr) {
        if (!curr->left) {
            count++;
            if (count == k && !found) {
                ans = curr->val;
                found = true;
            }
            curr = curr->right;
        } else {
            TreeNode* pred = curr->left;
            while (pred->right && pred->right != curr) {
                pred = pred->right;
            }

            if (!pred->right) {
                pred->right = curr;
                curr = curr->left;
            } else {
                pred->right = NULL;
                count++;
                if (count == k && !found) {
                    ans = curr->val;
                    found = true;
                }
                curr = curr->right;
            }
        }
    }

    return ans;
}
```

---

## 4.4 Final Practice List

### Must Do

| # | Problem | Level | Pattern |
|---:|---|---|---|
| 1 | Binary Tree Inorder Traversal | Easy | Morris inorder |
| 2 | Binary Tree Preorder Traversal | Easy | Morris preorder |
| 3 | Kth Smallest Element in BST | Medium | Morris inorder + count |
| 4 | Validate Binary Search Tree | Medium | Morris inorder + prev |
| 5 | Recover Binary Search Tree | Medium/Hard | Morris + inversion |
| 6 | Flatten Binary Tree to Linked List | Medium | Morris-style rewiring |
| 7 | Binary Tree Postorder Traversal | Hard | Morris postorder |

---

# Final Interview Mental Model

```text
For each current node:

1. If there is no left child:
       visit current
       go right

2. If there is a left child:
       find predecessor = rightmost node in left subtree

       if predecessor.right == NULL:
            create thread to current
            go left

       else predecessor.right == current:
            remove thread
            visit current for inorder
            go right
```

---

# One-Line Summary

> Morris traversal converts recursive return paths into temporary right links from predecessor nodes back to current nodes, giving O(1) auxiliary space traversal.

