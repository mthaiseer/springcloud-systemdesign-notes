# 🌳 Binary Tree CP + DSA Master Guide

> Complete phase-wise Binary Tree handbook for **FAANG DSA + Competitive Programming**.
>
> Style: **Clickable Index → Concept → Problem Statement → Input/Output → C++ Code → Index-by-Index / Node-by-Node Dry Run**

---

# 📌 Clickable Master Index

## 0. Core Mental Model
- [0.1 Binary Tree Node Structure](#01-binary-tree-node-structure)
- [0.2 Tree Terminology](#02-tree-terminology)
- [0.3 Universal Tree Thinking Framework](#03-universal-tree-thinking-framework)
- [0.4 DFS vs BFS Decision Map](#04-dfs-vs-bfs-decision-map)
- [0.5 Recursion Template for Trees](#05-recursion-template-for-trees)

## Phase 1 — Implementation + Traversal Ideas
- [1. Binary Tree Implementation](#1-binary-tree-implementation)
- [2. Recursive Inorder Traversal](#2-recursive-inorder-traversal)
- [3. Recursive Preorder Traversal](#3-recursive-preorder-traversal)
- [4. Recursive Postorder Traversal](#4-recursive-postorder-traversal)
- [5. Level Order Traversal](#5-level-order-traversal)
- [6. ZigZag Level Order Traversal](#6-zigzag-level-order-traversal)

## Phase 2 — Minimal Required Problems
- [7. Is BST](#7-is-bst)
- [8. Mirror Tree](#8-mirror-tree)
- [9. Root to Leaf Path Sum](#9-root-to-leaf-path-sum)
- [10. Diameter of Binary Tree](#10-diameter-of-binary-tree)
- [11. LCA of Binary Tree](#11-lca-of-binary-tree)
- [12. K Sum Path](#12-k-sum-path)

## Phase 3 — Standard Problems
- [13. Construct Tree from Preorder + Inorder](#13-construct-tree-from-preorder--inorder)
- [14. Construct Tree from Inorder + Postorder](#14-construct-tree-from-inorder--postorder)
- [15. Vertical Order Traversal](#15-vertical-order-traversal)
- [16. Diagonal Traversal](#16-diagonal-traversal)
- [17. Boundary Traversal](#17-boundary-traversal)
- [18. Kth Element in BST](#18-kth-element-in-bst)
- [19. Median of BST](#19-median-of-bst)
- [20. Inorder Successor in BST](#20-inorder-successor-in-bst)

## Phase 4 — Achiever / Advanced
- [21. BST LCA](#21-bst-lca)
- [22. Merge Two Binary Trees](#22-merge-two-binary-trees)
- [23. Isomorphic Tree](#23-isomorphic-tree)
- [24. Shortest Range in BST](#24-shortest-range-in-bst)
- [25. Balanced BST / Self Balancing Idea](#25-balanced-bst--self-balancing-idea)
- [26. Leaves Removal](#26-leaves-removal)
- [27. K Distance Nodes](#27-k-distance-nodes)

## Phase 5 — Special Techniques
- [28. Expression Tree](#28-expression-tree)
- [29. Tree to DLL Conversion](#29-tree-to-dll-conversion)
- [30. DLL to Balanced BST](#30-dll-to-balanced-bst)
- [31. Morris Inorder Traversal](#31-morris-inorder-traversal)
- [32. Flip a Binary Tree](#32-flip-a-binary-tree)

## Final Revision
- [Pattern Recognition Table](#pattern-recognition-table)
- [FAANG Priority List](#faang-priority-list)
- [CP Priority List](#cp-priority-list)
- [Common Edge Cases](#common-edge-cases)

---

# 0. Core Mental Model

## 0.1 Binary Tree Node Structure

```cpp
struct Node {
    int val;
    Node* left;
    Node* right;

    Node(int x) {
        val = x;
        left = NULL;
        right = NULL;
    }
};
```

Visual:

```text
        1
      /   \
     2     3
    / \
   4   5
```

Each node has:

```text
value
left child pointer
right child pointer
```

---

## 0.2 Tree Terminology

| Term | Meaning |
|---|---|
| Root | Top node |
| Leaf | Node with no children |
| Height | Longest downward path length |
| Depth | Distance from root |
| Subtree | Tree rooted at any node |
| BST | Left values smaller, right values greater |
| Balanced Tree | Height difference controlled |

---

## 0.3 Universal Tree Thinking Framework

Whenever you see a tree problem, ask:

```text
1. Do I need to visit every node?
   -> DFS / BFS

2. Do I need left-root-right sorted order?
   -> Inorder, usually BST

3. Do I need root before children?
   -> Preorder

4. Do I need children before root?
   -> Postorder

5. Do I need level-wise answer?
   -> BFS queue

6. Do I need ancestor/path information?
   -> DFS with path / parent map

7. Do I need subtree answer?
   -> Postorder

8. Do I need O(1) space traversal?
   -> Morris traversal
```

---

## 0.4 DFS vs BFS Decision Map

```text
Need level order / shortest distance from root?
    YES -> BFS
    NO  -> DFS

Need subtree height / diameter / balance?
    YES -> Postorder DFS

Need sorted BST values?
    YES -> Inorder DFS

Need clone/build/serialize style root-first logic?
    YES -> Preorder DFS
```

---

## 0.5 Recursion Template for Trees

```cpp
void dfs(Node* root) {
    if (root == NULL) return;

    // work before children: preorder

    dfs(root->left);

    // work between children: inorder

    dfs(root->right);

    // work after children: postorder
}
```

Mental model:

```text
dfs(node):
    solve left subtree
    solve right subtree
    combine answer at current node
```

---

# Phase 1 — Implementation + Traversal Ideas

---

# 1. Binary Tree Implementation

## Problem Statement

Create a binary tree manually and print its nodes using inorder traversal.

## Example Tree

```text
        1
      /   \
     2     3
    / \
   4   5
```

## Expected Output

```text
4 2 5 1 3
```

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Node {
    int val;
    Node* left;
    Node* right;

    Node(int x) {
        val = x;
        left = NULL;
        right = NULL;
    }
};

void inorder(Node* root) {
    if (root == NULL) return;

    inorder(root->left);
    cout << root->val << " ";
    inorder(root->right);
}

int main() {
    Node* root = new Node(1);
    root->left = new Node(2);
    root->right = new Node(3);
    root->left->left = new Node(4);
    root->left->right = new Node(5);

    inorder(root);
}
```

## Node-by-Node Dry Run

```text
Call inorder(1)

1. Go left to 2
   2. Go left to 4
      3. Go left to NULL -> return
      4. Print 4
      5. Go right to NULL -> return
   6. Print 2
   7. Go right to 5
      8. Go left to NULL -> return
      9. Print 5
      10. Go right to NULL -> return
11. Print 1
12. Go right to 3
    13. Go left to NULL -> return
    14. Print 3
    15. Go right to NULL -> return

Output = 4 2 5 1 3
```

---

# 2. Recursive Inorder Traversal

## Pattern

```text
LEFT -> ROOT -> RIGHT
```

For BST, inorder gives sorted order.

## Problem Statement

Given a binary tree, print inorder traversal.

## Input Tree

```text
        10
       /  \
      5    15
     / \     \
    2   7     20
```

## Output

```text
2 5 7 10 15 20
```

## C++ Code

```cpp
void inorder(Node* root) {
    if (root == NULL) return;

    inorder(root->left);
    cout << root->val << " ";
    inorder(root->right);
}
```

## Diagrammatic Dry Run

```text
inorder(10)
├── inorder(5)
│   ├── inorder(2)
│   │   ├── NULL
│   │   ├── print 2
│   │   └── NULL
│   ├── print 5
│   └── inorder(7)
│       ├── NULL
│       ├── print 7
│       └── NULL
├── print 10
└── inorder(15)
    ├── NULL
    ├── print 15
    └── inorder(20)
        ├── NULL
        ├── print 20
        └── NULL
```

---

# 3. Recursive Preorder Traversal

## Pattern

```text
ROOT -> LEFT -> RIGHT
```

Used for:
- tree cloning
- serialization
- build tree logic
- expression tree prefix

## Output for Same Tree

```text
10 5 2 7 15 20
```

## C++ Code

```cpp
void preorder(Node* root) {
    if (root == NULL) return;

    cout << root->val << " ";
    preorder(root->left);
    preorder(root->right);
}
```

## Dry Run

```text
preorder(10)
├── print 10
├── preorder(5)
│   ├── print 5
│   ├── preorder(2)
│   │   ├── print 2
│   │   ├── NULL
│   │   └── NULL
│   └── preorder(7)
│       ├── print 7
│       ├── NULL
│       └── NULL
└── preorder(15)
    ├── print 15
    ├── NULL
    └── preorder(20)
        ├── print 20
        ├── NULL
        └── NULL
```

---

# 4. Recursive Postorder Traversal

## Pattern

```text
LEFT -> RIGHT -> ROOT
```

Used for:
- delete tree
- diameter
- height
- balance check
- subtree DP

## Output

```text
2 7 5 20 15 10
```

## C++ Code

```cpp
void postorder(Node* root) {
    if (root == NULL) return;

    postorder(root->left);
    postorder(root->right);
    cout << root->val << " ";
}
```

## Dry Run

```text
postorder(10)
├── postorder(5)
│   ├── postorder(2)
│   │   ├── NULL
│   │   ├── NULL
│   │   └── print 2
│   ├── postorder(7)
│   │   ├── NULL
│   │   ├── NULL
│   │   └── print 7
│   └── print 5
├── postorder(15)
│   ├── NULL
│   ├── postorder(20)
│   │   ├── NULL
│   │   ├── NULL
│   │   └── print 20
│   └── print 15
└── print 10
```

---

# 5. Level Order Traversal

## Problem Statement

Print nodes level by level.

## Pattern

Use queue.

```text
push root
while queue not empty:
    pop front
    print node
    push left
    push right
```

## Input Tree

```text
        1
      /   \
     2     3
    / \     \
   4   5     6
```

## Output

```text
1 2 3 4 5 6
```

## C++ Code

```cpp
vector<int> levelOrder(Node* root) {
    vector<int> ans;
    if (root == NULL) return ans;

    queue<Node*> q;
    q.push(root);

    while (!q.empty()) {
        Node* cur = q.front();
        q.pop();

        ans.push_back(cur->val);

        if (cur->left) q.push(cur->left);
        if (cur->right) q.push(cur->right);
    }

    return ans;
}
```

## Queue Dry Run

```text
Initial:
queue = [1]
ans = []

Step 1:
pop 1
push 2, 3
queue = [2, 3]
ans = [1]

Step 2:
pop 2
push 4, 5
queue = [3, 4, 5]
ans = [1, 2]

Step 3:
pop 3
push 6
queue = [4, 5, 6]
ans = [1, 2, 3]

Step 4:
pop 4
queue = [5, 6]
ans = [1, 2, 3, 4]

Step 5:
pop 5
queue = [6]
ans = [1, 2, 3, 4, 5]

Step 6:
pop 6
queue = []
ans = [1, 2, 3, 4, 5, 6]
```

---

# 6. ZigZag Level Order Traversal

## Problem Statement

Print levels alternatively left-to-right and right-to-left.

## Input Tree

```text
        1
      /   \
     2     3
    / \   / \
   4   5 6   7
```

## Output

```text
1
3 2
4 5 6 7
```

## C++ Code

```cpp
vector<vector<int>> zigzagLevelOrder(Node* root) {
    vector<vector<int>> ans;
    if (root == NULL) return ans;

    queue<Node*> q;
    q.push(root);
    bool leftToRight = true;

    while (!q.empty()) {
        int sz = q.size();
        vector<int> level(sz);

        for (int i = 0; i < sz; i++) {
            Node* cur = q.front();
            q.pop();

            int index = leftToRight ? i : sz - 1 - i;
            level[index] = cur->val;

            if (cur->left) q.push(cur->left);
            if (cur->right) q.push(cur->right);
        }

        leftToRight = !leftToRight;
        ans.push_back(level);
    }

    return ans;
}
```

## Index-by-Index Dry Run

```text
Level 0:
sz = 1, leftToRight = true
node = 1, i = 0, index = 0
level = [1]

Level 1:
queue = [2, 3]
sz = 2, leftToRight = false

node = 2, i = 0, index = 1
level = [_, 2]

node = 3, i = 1, index = 0
level = [3, 2]

Level 2:
queue = [4, 5, 6, 7]
sz = 4, leftToRight = true

node = 4, i = 0, index = 0 -> [4, _, _, _]
node = 5, i = 1, index = 1 -> [4, 5, _, _]
node = 6, i = 2, index = 2 -> [4, 5, 6, _]
node = 7, i = 3, index = 3 -> [4, 5, 6, 7]
```

---

# Phase 2 — Minimal Required Problems

---

# 7. Is BST

## Problem Statement

Check whether a binary tree is a valid BST.

BST rule:

```text
all nodes in left subtree < root
all nodes in right subtree > root
```

Important: It is not enough to check only immediate children.

## Example

```text
        10
       /  \
      5    15
          /  \
         6    20
```

This is NOT BST because `6` is in right subtree of `10`, but `6 < 10`.

## Correct Idea

Pass valid range to each node.

```text
root 10 can be (-inf, +inf)
left child 5 must be (-inf, 10)
right child 15 must be (10, +inf)
node 6 under right subtree must be (10, 15)
6 violates because 6 <= 10
```

## C++ Code

```cpp
bool isBSTUtil(Node* root, long long low, long long high) {
    if (root == NULL) return true;

    if (root->val <= low || root->val >= high) return false;

    return isBSTUtil(root->left, low, root->val) &&
           isBSTUtil(root->right, root->val, high);
}

bool isBST(Node* root) {
    return isBSTUtil(root, LLONG_MIN, LLONG_MAX);
}
```

## Dry Run

```text
isBST(10, -inf, +inf)
├── 10 valid
├── left: isBST(5, -inf, 10)
│   └── 5 valid
└── right: isBST(15, 10, +inf)
    ├── 15 valid
    ├── left: isBST(6, 10, 15)
    │   └── 6 invalid because 6 <= 10
    └── final answer = false
```

---

# 8. Mirror Tree

## Problem Statement

Convert a tree into its mirror by swapping left and right children at every node.

## Input

```text
        1
      /   \
     2     3
    / \
   4   5
```

## Output

```text
        1
      /   \
     3     2
          / \
         5   4
```

## C++ Code

```cpp
void mirror(Node* root) {
    if (root == NULL) return;

    swap(root->left, root->right);

    mirror(root->left);
    mirror(root->right);
}
```

## Dry Run

```text
mirror(1)
├── swap children of 1: left=3, right=2
├── mirror(3)
│   └── no children, return
└── mirror(2)
    ├── swap children of 2: left=5, right=4
    ├── mirror(5) return
    └── mirror(4) return
```

---

# 9. Root to Leaf Path Sum

## Problem Statement

Given a binary tree and target sum, check if there exists a root-to-leaf path whose sum equals target.

## Input

```text
        5
       / \
      4   8
     /   / \
    11  13  4
   /  \
  7    2

target = 22
```

## Output

```text
true
```

Path:

```text
5 -> 4 -> 11 -> 2 = 22
```

## C++ Code

```cpp
bool hasPathSum(Node* root, int target) {
    if (root == NULL) return false;

    if (root->left == NULL && root->right == NULL) {
        return target == root->val;
    }

    return hasPathSum(root->left, target - root->val) ||
           hasPathSum(root->right, target - root->val);
}
```

## Dry Run

```text
hasPathSum(5, 22)
├── go left with target 17
│   hasPathSum(4, 17)
│   ├── go left with target 13
│   │   hasPathSum(11, 13)
│   │   ├── go left with target 2
│   │   │   hasPathSum(7, 2) -> leaf, 7 != 2 -> false
│   │   └── go right with target 2
│   │       hasPathSum(2, 2) -> leaf, 2 == 2 -> true
│   └── final true
└── no need to check more due to OR
```

---

# 10. Diameter of Binary Tree

## Problem Statement

Find the longest path between any two nodes.

Path may or may not pass through root.

## Key Idea

At each node:

```text
diameter through node = left height + right height
answer = max(answer, diameter through node)
```

## C++ Code

```cpp
int diameterAns = 0;

int height(Node* root) {
    if (root == NULL) return 0;

    int lh = height(root->left);
    int rh = height(root->right);

    diameterAns = max(diameterAns, lh + rh);

    return 1 + max(lh, rh);
}

int diameter(Node* root) {
    diameterAns = 0;
    height(root);
    return diameterAns;
}
```

## Dry Run

Tree:

```text
        1
       / \
      2   3
     / \
    4   5
```

```text
height(4):
lh=0, rh=0, diameter=0, return 1

height(5):
lh=0, rh=0, diameter=0, return 1

height(2):
lh=1, rh=1, diameter through 2 = 2
answer = 2
return 2

height(3):
return 1

height(1):
lh=2, rh=1, diameter through 1 = 3
answer = 3
return 3

Final diameter = 3 edges
Path = 4 -> 2 -> 1 -> 3
```

---

# 11. LCA of Binary Tree

## Problem Statement

Find Lowest Common Ancestor of two nodes in a binary tree.

## Idea

For every node:

```text
if node is NULL -> return NULL
if node is p or q -> return node
search left
search right
if both sides return non-null -> current node is LCA
else return non-null side
```

## C++ Code

```cpp
Node* lca(Node* root, Node* p, Node* q) {
    if (root == NULL) return NULL;
    if (root == p || root == q) return root;

    Node* left = lca(root->left, p, q);
    Node* right = lca(root->right, p, q);

    if (left != NULL && right != NULL) return root;
    if (left != NULL) return left;
    return right;
}
```

## Dry Run

Tree:

```text
        3
       / \
      5   1
     / \ / \
    6  2 0  8
      / \
     7   4

p = 5, q = 4
```

```text
lca(3)
├── left = lca(5)
│   └── root is p, return 5
└── right = lca(1)
    └── q not found, return NULL

At 3:
left = 5, right = NULL
return 5

Answer = 5
```

Why 5?

```text
5 is ancestor of itself and also ancestor of 4.
```

---

# 12. K Sum Path

## Problem Statement

Count number of downward paths whose sum equals K.

Path can start and end anywhere, but must go downward.

## Example

```text
        10
       /  \
      5   -3
     / \    \
    3   2    11
   / \   \
  3  -2   1

K = 8
```

Valid paths:

```text
5 -> 3
5 -> 2 -> 1
-3 -> 11
```

Answer = 3

## Prefix Sum Idea

Maintain prefix sum from root to current node.

If current prefix = S, need previous prefix:

```text
S - K
```

## C++ Code

```cpp
int countKSumPaths(Node* root, int k, long long sum, unordered_map<long long, int>& freq) {
    if (root == NULL) return 0;

    sum += root->val;

    int ans = 0;
    if (freq.count(sum - k)) ans += freq[sum - k];

    freq[sum]++;

    ans += countKSumPaths(root->left, k, sum, freq);
    ans += countKSumPaths(root->right, k, sum, freq);

    freq[sum]--;

    return ans;
}

int pathSum(Node* root, int k) {
    unordered_map<long long, int> freq;
    freq[0] = 1;
    return countKSumPaths(root, k, 0, freq);
}
```

## Dry Run Snapshot

```text
At node 10:
sum = 10
need = 10 - 8 = 2
freq[2] = 0
insert freq[10]

At node 5:
sum = 15
need = 15 - 8 = 7
freq[7] = 0
insert freq[15]

At node 3:
sum = 18
need = 18 - 8 = 10
freq[10] = 1
found one path: 5 -> 3

At node 2:
sum = 17
need = 9
not found

At node 1:
sum = 18
need = 10
freq[10] = 1
found path: 5 -> 2 -> 1

At node -3:
sum = 7
need = -1
not found

At node 11:
sum = 18
need = 10
freq[10] = 1
found path: -3 -> 11
```

---

# Phase 3 — Standard Problems

---

# 13. Construct Tree from Preorder + Inorder

## Problem Statement

Build binary tree from preorder and inorder arrays.

## Key Properties

```text
Preorder = ROOT LEFT RIGHT
Inorder  = LEFT ROOT RIGHT
```

So:

```text
first preorder element = root
root splits inorder into left subtree and right subtree
```

## Example

```text
preorder = [3, 9, 20, 15, 7]
inorder  = [9, 3, 15, 20, 7]
```

Tree:

```text
        3
       / \
      9   20
         /  \
        15   7
```

## C++ Code

```cpp
Node* buildPreIn(vector<int>& preorder, int preL, int preR,
                 vector<int>& inorder, int inL, int inR,
                 unordered_map<int, int>& pos) {
    if (preL > preR || inL > inR) return NULL;

    int rootVal = preorder[preL];
    Node* root = new Node(rootVal);

    int idx = pos[rootVal];
    int leftSize = idx - inL;

    root->left = buildPreIn(preorder, preL + 1, preL + leftSize,
                            inorder, inL, idx - 1, pos);

    root->right = buildPreIn(preorder, preL + leftSize + 1, preR,
                             inorder, idx + 1, inR, pos);

    return root;
}

Node* buildTree(vector<int>& preorder, vector<int>& inorder) {
    unordered_map<int, int> pos;
    for (int i = 0; i < (int)inorder.size(); i++) {
        pos[inorder[i]] = i;
    }

    return buildPreIn(preorder, 0, preorder.size() - 1,
                      inorder, 0, inorder.size() - 1, pos);
}
```

## Index-by-Index Dry Run

```text
preorder = [3, 9, 20, 15, 7]
inorder  = [9, 3, 15, 20, 7]

Call 1:
preL=0, preR=4
inL=0, inR=4
root = preorder[0] = 3
idx of 3 in inorder = 1
leftSize = 1

Left subtree:
preorder range = [1..1] -> [9]
inorder range  = [0..0] -> [9]

Right subtree:
preorder range = [2..4] -> [20,15,7]
inorder range  = [2..4] -> [15,20,7]

Call right:
root = 20
idx of 20 = 3
leftSize = 1

Left of 20:
preorder [3..3] -> [15]
inorder [2..2] -> [15]

Right of 20:
preorder [4..4] -> [7]
inorder [4..4] -> [7]
```

---

# 14. Construct Tree from Inorder + Postorder

## Key Properties

```text
Postorder = LEFT RIGHT ROOT
Inorder   = LEFT ROOT RIGHT
```

So:

```text
last postorder element = root
root splits inorder
```

## C++ Code

```cpp
Node* buildInPost(vector<int>& inorder, int inL, int inR,
                  vector<int>& postorder, int postL, int postR,
                  unordered_map<int, int>& pos) {
    if (inL > inR || postL > postR) return NULL;

    int rootVal = postorder[postR];
    Node* root = new Node(rootVal);

    int idx = pos[rootVal];
    int leftSize = idx - inL;

    root->left = buildInPost(inorder, inL, idx - 1,
                             postorder, postL, postL + leftSize - 1, pos);

    root->right = buildInPost(inorder, idx + 1, inR,
                              postorder, postL + leftSize, postR - 1, pos);

    return root;
}
```

## Dry Run

```text
inorder   = [9, 3, 15, 20, 7]
postorder = [9, 15, 7, 20, 3]

root = last postorder = 3
inorder split:
left = [9]
right = [15,20,7]

left postorder size = 1 -> [9]
right postorder = [15,7,20]

right subtree root = 20
inorder split around 20:
left = [15]
right = [7]
```

---

# 15. Vertical Order Traversal

## Problem Statement

Print nodes column by column.

## Idea

Assign horizontal distance:

```text
root = 0
left child = hd - 1
right child = hd + 1
```

Use BFS to preserve top-down order.

## C++ Code

```cpp
vector<vector<int>> verticalOrder(Node* root) {
    vector<vector<int>> ans;
    if (root == NULL) return ans;

    map<int, vector<int>> mp;
    queue<pair<Node*, int>> q;
    q.push({root, 0});

    while (!q.empty()) {
        auto [node, hd] = q.front();
        q.pop();

        mp[hd].push_back(node->val);

        if (node->left) q.push({node->left, hd - 1});
        if (node->right) q.push({node->right, hd + 1});
    }

    for (auto &it : mp) {
        ans.push_back(it.second);
    }

    return ans;
}
```

## Dry Run

Tree:

```text
        1(0)
       /    \
    2(-1)   3(+1)
    /  \      \
 4(-2) 5(0)   6(+2)
```

```text
queue = [(1,0)]
mp = {}

pop (1,0) -> mp[0] = [1]
push (2,-1), (3,1)

pop (2,-1) -> mp[-1] = [2]
push (4,-2), (5,0)

pop (3,1) -> mp[1] = [3]
push (6,2)

pop (4,-2) -> mp[-2] = [4]
pop (5,0)  -> mp[0] = [1,5]
pop (6,2)  -> mp[2] = [6]

Final:
hd -2: [4]
hd -1: [2]
hd  0: [1,5]
hd  1: [3]
hd  2: [6]
```

---

# 16. Diagonal Traversal

## Idea

For diagonal traversal:

```text
right child stays on same diagonal
left child goes to next diagonal
```

## C++ Code

```cpp
vector<int> diagonalTraversal(Node* root) {
    vector<int> ans;
    if (root == NULL) return ans;

    queue<Node*> q;
    q.push(root);

    while (!q.empty()) {
        Node* cur = q.front();
        q.pop();

        while (cur != NULL) {
            ans.push_back(cur->val);

            if (cur->left) q.push(cur->left);
            cur = cur->right;
        }
    }

    return ans;
}
```

## Dry Run

```text
        8
       / \
      3   10
     / \    \
    1   6    14
       / \   /
      4   7 13

Diagonal 0: 8, 10, 14
Diagonal 1: 3, 6, 7, 13
Diagonal 2: 1, 4
```

Queue dry run:

```text
q = [8]
cur = 8 -> print 8, push left 3, move right 10
cur = 10 -> print 10, move right 14
cur = 14 -> print 14, push left 13

q = [3,13]
cur = 3 -> print 3, push 1, move right 6
cur = 6 -> print 6, push 4, move right 7
cur = 7 -> print 7

q = [13,1,4]
cur = 13 -> print 13
cur = 1 -> print 1
cur = 4 -> print 4
```

---

# 17. Boundary Traversal

## Problem Statement

Print boundary of binary tree anti-clockwise.

Order:

```text
root
left boundary excluding leaves
all leaves left to right
right boundary excluding leaves in reverse
```

## C++ Code

```cpp
bool isLeaf(Node* node) {
    return node != NULL && node->left == NULL && node->right == NULL;
}

void addLeftBoundary(Node* root, vector<int>& ans) {
    Node* cur = root->left;
    while (cur) {
        if (!isLeaf(cur)) ans.push_back(cur->val);
        if (cur->left) cur = cur->left;
        else cur = cur->right;
    }
}

void addLeaves(Node* root, vector<int>& ans) {
    if (root == NULL) return;
    if (isLeaf(root)) {
        ans.push_back(root->val);
        return;
    }
    addLeaves(root->left, ans);
    addLeaves(root->right, ans);
}

void addRightBoundary(Node* root, vector<int>& ans) {
    vector<int> temp;
    Node* cur = root->right;

    while (cur) {
        if (!isLeaf(cur)) temp.push_back(cur->val);
        if (cur->right) cur = cur->right;
        else cur = cur->left;
    }

    reverse(temp.begin(), temp.end());
    for (int x : temp) ans.push_back(x);
}

vector<int> boundaryTraversal(Node* root) {
    vector<int> ans;
    if (root == NULL) return ans;

    if (!isLeaf(root)) ans.push_back(root->val);

    addLeftBoundary(root, ans);
    addLeaves(root, ans);
    addRightBoundary(root, ans);

    return ans;
}
```

## Dry Run

```text
        1
      /   \
     2     3
    / \   / \
   4   5 6   7

root = 1
left boundary = 2
leaves = 4,5,6,7
right boundary reverse = 3

answer = 1 2 4 5 6 7 3
```

---

# 18. Kth Element in BST

## Problem Statement

Find kth smallest element in BST.

## Idea

BST inorder traversal gives sorted order.

```text
inorder count 1, 2, 3...
when count == k, answer found
```

## C++ Code

```cpp
void kthSmallestDFS(Node* root, int k, int& cnt, int& ans) {
    if (root == NULL) return;

    kthSmallestDFS(root->left, k, cnt, ans);

    cnt++;
    if (cnt == k) {
        ans = root->val;
        return;
    }

    kthSmallestDFS(root->right, k, cnt, ans);
}

int kthSmallest(Node* root, int k) {
    int cnt = 0;
    int ans = -1;
    kthSmallestDFS(root, k, cnt, ans);
    return ans;
}
```

## Dry Run

Tree:

```text
        5
       / \
      3   7
     / \ / \
    2  4 6  8

k = 3
```

Inorder:

```text
2, 3, 4, 5, 6, 7, 8
```

```text
visit 2 -> cnt = 1
visit 3 -> cnt = 2
visit 4 -> cnt = 3 -> answer = 4
```

---

# 19. Median of BST

## Problem Statement

Find median of BST.

## Simple Idea

Use inorder array.

## C++ Code

```cpp
void inorderCollect(Node* root, vector<int>& arr) {
    if (root == NULL) return;
    inorderCollect(root->left, arr);
    arr.push_back(root->val);
    inorderCollect(root->right, arr);
}

double medianBST(Node* root) {
    vector<int> arr;
    inorderCollect(root, arr);

    int n = arr.size();
    if (n % 2 == 1) return arr[n / 2];
    return (arr[n / 2 - 1] + arr[n / 2]) / 2.0;
}
```

## Morris Optimization

For O(1) extra space, use Morris inorder traversal twice:

```text
1. Count nodes
2. Traverse again and pick middle node(s)
```

---

# 20. Inorder Successor in BST

## Problem Statement

Find the next greater node of a given value in BST.

## Idea

```text
If root value > key:
    root can be successor
    move left
Else:
    move right
```

## C++ Code

```cpp
Node* inorderSuccessor(Node* root, int key) {
    Node* succ = NULL;

    while (root != NULL) {
        if (root->val > key) {
            succ = root;
            root = root->left;
        } else {
            root = root->right;
        }
    }

    return succ;
}
```

## Dry Run

```text
BST:
        20
       /  \
      10   30
        \
         15

key = 15

root = 20
20 > 15, succ = 20, move left to 10

root = 10
10 <= 15, move right to 15

root = 15
15 <= 15, move right NULL

answer = 20
```

---

# Phase 4 — Achiever / Advanced

---

# 21. BST LCA

## Problem Statement

Find LCA of two nodes in BST.

## Idea

Use BST ordering.

```text
if both values < root -> go left
if both values > root -> go right
otherwise root is split point -> LCA
```

## C++ Code

```cpp
Node* lcaBST(Node* root, int a, int b) {
    while (root != NULL) {
        if (a < root->val && b < root->val) {
            root = root->left;
        } else if (a > root->val && b > root->val) {
            root = root->right;
        } else {
            return root;
        }
    }
    return NULL;
}
```

## Dry Run

```text
        20
       /  \
      10   30
     /  \
    5   15

a = 5, b = 15

root = 20
both < 20 -> go left

root = 10
5 < 10 but 15 > 10
split point found
answer = 10
```

---

# 22. Merge Two Binary Trees

## Problem Statement

Merge two binary trees by summing overlapping nodes.

## C++ Code

```cpp
Node* mergeTrees(Node* a, Node* b) {
    if (a == NULL) return b;
    if (b == NULL) return a;

    a->val += b->val;
    a->left = mergeTrees(a->left, b->left);
    a->right = mergeTrees(a->right, b->right);

    return a;
}
```

## Dry Run

```text
Tree A:       Tree B:
   1            2
  / \          / \
 3   2        1   3

merge root:
1 + 2 = 3

merge left:
3 + 1 = 4

merge right:
2 + 3 = 5

Result:
   3
  / \
 4   5
```

---

# 23. Isomorphic Tree

## Problem Statement

Two trees are isomorphic if one can be transformed into the other by swapping left and right children of some nodes.

## C++ Code

```cpp
bool isIsomorphic(Node* a, Node* b) {
    if (a == NULL && b == NULL) return true;
    if (a == NULL || b == NULL) return false;
    if (a->val != b->val) return false;

    bool noSwap = isIsomorphic(a->left, b->left) &&
                  isIsomorphic(a->right, b->right);

    bool swapCase = isIsomorphic(a->left, b->right) &&
                    isIsomorphic(a->right, b->left);

    return noSwap || swapCase;
}
```

## Dry Run

```text
At every node:
try two possibilities:

1. Children are already aligned
2. Children are swapped

If either works, subtree is isomorphic.
```

---

# 24. Shortest Range in BST

## Common Interpretation

Given multiple BSTs or sorted sources, find the smallest range that contains at least one value from each BST.

## Idea

BST inorder gives sorted list.
Then problem becomes:

```text
Smallest range covering K sorted lists
```

Use min-heap.

## C++ Skeleton

```cpp
struct Item {
    int val;
    int listId;
    int index;

    bool operator>(const Item& other) const {
        return val > other.val;
    }
};

pair<int,int> smallestRange(vector<vector<int>>& lists) {
    priority_queue<Item, vector<Item>, greater<Item>> pq;
    int currentMax = INT_MIN;

    for (int i = 0; i < (int)lists.size(); i++) {
        pq.push({lists[i][0], i, 0});
        currentMax = max(currentMax, lists[i][0]);
    }

    int bestL = 0, bestR = INT_MAX;

    while (true) {
        auto cur = pq.top();
        pq.pop();

        if (currentMax - cur.val < bestR - bestL) {
            bestL = cur.val;
            bestR = currentMax;
        }

        int nextIndex = cur.index + 1;
        if (nextIndex == (int)lists[cur.listId].size()) break;

        int nextVal = lists[cur.listId][nextIndex];
        pq.push({nextVal, cur.listId, nextIndex});
        currentMax = max(currentMax, nextVal);
    }

    return {bestL, bestR};
}
```

---

# 25. Balanced BST / Self Balancing Idea

## Balanced BST Meaning

A BST is balanced when height is approximately logarithmic.

```text
search/insert/delete = O(log n)
```

## Common Self Balancing Trees

| Tree | Key Idea |
|---|---|
| AVL | Strict height balance |
| Red Black Tree | Color rules, less strict |
| Treap | BST + heap priority |
| Splay Tree | Recently accessed nodes near root |

## Build Balanced BST from Sorted Array

```cpp
Node* buildBalanced(vector<int>& arr, int l, int r) {
    if (l > r) return NULL;

    int mid = l + (r - l) / 2;
    Node* root = new Node(arr[mid]);

    root->left = buildBalanced(arr, l, mid - 1);
    root->right = buildBalanced(arr, mid + 1, r);

    return root;
}
```

## Dry Run

```text
arr = [1,2,3,4,5,6,7]

mid = 3 -> root = 4
left arr = [1,2,3]
right arr = [5,6,7]

left mid = 2
right mid = 6

Tree:
        4
      /   \
     2     6
    / \   / \
   1   3 5   7
```

---

# 26. Leaves Removal

## Problem Statement

Remove leaves layer by layer and return groups.

## Idea

Use height from bottom.

```text
leaf height = 0
parent of leaf height = 1
...
```

## C++ Code

```cpp
int collectLeaves(Node* root, vector<vector<int>>& ans) {
    if (root == NULL) return -1;

    int lh = collectLeaves(root->left, ans);
    int rh = collectLeaves(root->right, ans);

    int h = 1 + max(lh, rh);

    if ((int)ans.size() == h) ans.push_back({});
    ans[h].push_back(root->val);

    return h;
}

vector<vector<int>> findLeaves(Node* root) {
    vector<vector<int>> ans;
    collectLeaves(root, ans);
    return ans;
}
```

## Dry Run

```text
        1
       / \
      2   3
     / \
    4   5

height(4)=0 -> group 0: [4]
height(5)=0 -> group 0: [4,5]
height(3)=0 -> group 0: [4,5,3]
height(2)=1 -> group 1: [2]
height(1)=2 -> group 2: [1]

Answer:
[ [4,5,3], [2], [1] ]
```

---

# 27. K Distance Nodes

## Problem Statement

Given a binary tree, target node, and integer K, return all nodes at distance K from target.

## Idea

Tree has no parent pointer, so:

```text
1. Build parent map using BFS/DFS
2. BFS from target in 3 directions:
   - left
   - right
   - parent
```

## C++ Code

```cpp
void buildParent(Node* root, unordered_map<Node*, Node*>& parent) {
    queue<Node*> q;
    q.push(root);

    while (!q.empty()) {
        Node* cur = q.front();
        q.pop();

        if (cur->left) {
            parent[cur->left] = cur;
            q.push(cur->left);
        }
        if (cur->right) {
            parent[cur->right] = cur;
            q.push(cur->right);
        }
    }
}

vector<int> distanceK(Node* root, Node* target, int k) {
    unordered_map<Node*, Node*> parent;
    buildParent(root, parent);

    unordered_set<Node*> visited;
    queue<Node*> q;
    q.push(target);
    visited.insert(target);

    int dist = 0;

    while (!q.empty()) {
        int sz = q.size();
        if (dist == k) break;

        for (int i = 0; i < sz; i++) {
            Node* cur = q.front();
            q.pop();

            vector<Node*> next = {cur->left, cur->right, parent[cur]};
            for (Node* node : next) {
                if (node != NULL && !visited.count(node)) {
                    visited.insert(node);
                    q.push(node);
                }
            }
        }
        dist++;
    }

    vector<int> ans;
    while (!q.empty()) {
        ans.push_back(q.front()->val);
        q.pop();
    }
    return ans;
}
```

## Dry Run

```text
        3
       / \
      5   1
     / \ / \
    6  2 0  8
      / \
     7   4

target = 5, k = 2

Distance 0:
queue = [5]

Distance 1 neighbors of 5:
6, 2, 3
queue = [6,2,3]

Distance 2:
from 6 -> none useful
from 2 -> 7,4
from 3 -> 1
queue = [7,4,1]

Answer = [7,4,1]
```

---

# Phase 5 — Special Techniques

---

# 28. Expression Tree

## Problem Statement

Build/evaluate expression tree.

Example:

```text
        +
       / \
      *   5
     / \
    3   2
```

Expression:

```text
(3 * 2) + 5 = 11
```

## C++ Node

```cpp
struct ExprNode {
    string val;
    ExprNode* left;
    ExprNode* right;

    ExprNode(string x) {
        val = x;
        left = right = NULL;
    }
};
```

## Evaluate Code

```cpp
int eval(ExprNode* root) {
    if (root->left == NULL && root->right == NULL) {
        return stoi(root->val);
    }

    int a = eval(root->left);
    int b = eval(root->right);

    if (root->val == "+") return a + b;
    if (root->val == "-") return a - b;
    if (root->val == "*") return a * b;
    return a / b;
}
```

## Dry Run

```text
eval(+)
├── eval(*)
│   ├── eval(3) -> 3
│   ├── eval(2) -> 2
│   └── 3 * 2 = 6
├── eval(5) -> 5
└── 6 + 5 = 11
```

---

# 29. Tree to DLL Conversion

## Problem Statement

Convert binary tree to doubly linked list using inorder order.

## Idea

Inorder traversal:

```text
left -> root -> right
```

Maintain:

```text
prev = previous visited node
head = first visited node
```

## C++ Code

```cpp
void treeToDLLUtil(Node* root, Node*& prev, Node*& head) {
    if (root == NULL) return;

    treeToDLLUtil(root->left, prev, head);

    if (prev == NULL) {
        head = root;
    } else {
        prev->right = root;
        root->left = prev;
    }

    prev = root;

    treeToDLLUtil(root->right, prev, head);
}

Node* treeToDLL(Node* root) {
    Node* prev = NULL;
    Node* head = NULL;
    treeToDLLUtil(root, prev, head);
    return head;
}
```

## Dry Run

```text
Tree:
        4
       / \
      2   5
     / \
    1   3

Inorder = 1,2,3,4,5

visit 1:
prev=NULL -> head=1, prev=1

visit 2:
prev=1
1.right=2
2.left=1
prev=2

visit 3:
2.right=3
3.left=2
prev=3

visit 4:
3.right=4
4.left=3
prev=4

visit 5:
4.right=5
5.left=4
prev=5

DLL:
1 <-> 2 <-> 3 <-> 4 <-> 5
```

---

# 30. DLL to Balanced BST

## Problem Statement

Convert sorted doubly linked list to balanced BST.

## Idea

Use inorder construction.

```text
Build left subtree of size n/2
Current DLL node becomes root
Move DLL pointer forward
Build right subtree
```

## C++ Code

```cpp
Node* sortedDLLToBST(Node*& head, int n) {
    if (n <= 0) return NULL;

    Node* left = sortedDLLToBST(head, n / 2);

    Node* root = head;
    root->left = left;

    head = head->right;

    root->right = sortedDLLToBST(head, n - n / 2 - 1);

    return root;
}
```

## Dry Run

```text
DLL = 1 <-> 2 <-> 3 <-> 4 <-> 5
n = 5

Build left with n=2:
    Build left with n=1:
        root = 1
    root = 2

Main root = 3

Build right with n=2:
    root = 4
    right = 5

Balanced BST:
        3
       / \
      2   5
     /   /
    1   4
```

---

# 31. Morris Inorder Traversal

## Problem Statement

Print inorder traversal without recursion and without stack.

## Key Idea

Temporarily create thread:

```text
predecessor.right = current
```

Then remove it when returning.

## C++ Code

```cpp
vector<int> morrisInorder(Node* root) {
    vector<int> ans;
    Node* cur = root;

    while (cur != NULL) {
        if (cur->left == NULL) {
            ans.push_back(cur->val);
            cur = cur->right;
        } else {
            Node* pred = cur->left;

            while (pred->right != NULL && pred->right != cur) {
                pred = pred->right;
            }

            if (pred->right == NULL) {
                pred->right = cur;
                cur = cur->left;
            } else {
                pred->right = NULL;
                ans.push_back(cur->val);
                cur = cur->right;
            }
        }
    }

    return ans;
}
```

## Dry Run

Tree:

```text
        4
       / \
      2   5
     / \
    1   3
```

```text
cur = 4
left exists
pred = rightmost of left subtree = 3
3.right is NULL -> create thread 3.right = 4
cur = 2

cur = 2
left exists
pred = 1
1.right is NULL -> create thread 1.right = 2
cur = 1

cur = 1
left NULL -> print 1
cur = 1.right = 2 using thread

cur = 2
left exists
pred = 1
1.right == cur -> remove thread
print 2
cur = 3

cur = 3
left NULL -> print 3
cur = 3.right = 4 using thread

cur = 4
left exists
pred = 3
3.right == cur -> remove thread
print 4
cur = 5

cur = 5
left NULL -> print 5
cur = NULL

Output = 1 2 3 4 5
```

---

# 32. Flip a Binary Tree

## Problem Statement

Flip a binary tree upside down.

For each node:

```text
old left child becomes new root
old root becomes right child
old right child becomes left child
```

## Example

Input:

```text
        1
       / \
      2   3
     / \
    4   5
```

Output:

```text
        4
       / \
      5   2
         / \
        3   1
```

## C++ Code

```cpp
Node* flipTree(Node* root) {
    if (root == NULL || root->left == NULL) return root;

    Node* newRoot = flipTree(root->left);

    root->left->left = root->right;
    root->left->right = root;

    root->left = NULL;
    root->right = NULL;

    return newRoot;
}
```

## Dry Run

```text
flip(1)
└── flip(2)
    └── flip(4)
        └── root 4 has no left, return 4 as newRoot

Back at node 2:
2.left = 4
4.left = 2.right = 5
4.right = 2
2.left = NULL
2.right = NULL

Back at node 1:
1.left = 2
2.left = 1.right = 3
2.right = 1
1.left = NULL
1.right = NULL

Final root = 4
```

---

# Pattern Recognition Table

| Problem Type | Pattern | Traversal |
|---|---|---|
| Print sorted BST | Inorder | DFS |
| Root first processing | Preorder | DFS |
| Subtree answer | Postorder | DFS |
| Level by level | Queue | BFS |
| Diameter | Height + global answer | Postorder |
| Validate BST | Range passing | DFS |
| Kth BST | Inorder count | DFS |
| LCA Binary Tree | Return found node | DFS |
| LCA BST | Compare values | Iterative |
| Path sum | Prefix sum / DFS | DFS |
| Distance K | Parent map + BFS | BFS |
| Vertical traversal | Horizontal distance | BFS + map |
| Boundary | Left + leaves + right | DFS |
| Build tree | Pre/In or Post/In split | Recursion |
| O(1) traversal | Morris threading | Iterative |
| Tree to DLL | Inorder + prev | DFS |

---

# FAANG Priority List

## Must Master

```text
1. DFS traversals
2. BFS level order
3. Validate BST
4. Diameter
5. LCA Binary Tree
6. LCA BST
7. Path Sum I/II/III
8. Kth Smallest in BST
9. Build Tree from Preorder/Inorder
10. Serialize/Deserialize idea
11. Boundary / Vertical Traversal
12. Morris Traversal basics
13. Balanced Binary Tree
14. Maximum Path Sum
15. Distance K
```

---

# CP Priority List

For CP, binary tree is less common than graph/tree-on-N-nodes, but these matter:

```text
1. DFS recursion
2. subtree DP
3. rerooting basics
4. LCA idea
5. path sums
6. binary lifting for general trees
7. centroid decomposition later
8. Euler tour later
```

This guide focuses on binary tree interview + AlgoZenith-style CP basics.

---

# Common Edge Cases

Always test:

```text
1. root = NULL
2. single node tree
3. only left chain
4. only right chain
5. duplicate values if BST rules allow/disallow duplicates
6. p or q not present in LCA
7. k = 0 in K distance problem
8. negative values in path sum
9. skewed tree recursion depth
10. very large tree causing stack overflow
```

---

# Final Mental Checklist

Before coding any binary tree problem:

```text
Need answer from children?     -> Postorder
Need process root first?       -> Preorder
Need sorted BST order?         -> Inorder
Need levels/distance?          -> BFS
Need ancestors?                -> DFS path or parent map
Need range validity?           -> pass min/max
Need kth/sorted?               -> inorder count
Need O(1) space?               -> Morris
Need construct tree?           -> root splits inorder
```

---

# Recommended Practice Order

```text
Phase 1:
Inorder, Preorder, Postorder, Level Order, Zigzag

Phase 2:
IsBST, Mirror, Root-to-leaf Path Sum, Diameter, LCA, KSUM Path

Phase 3:
Build Tree Pre/In, Build Tree In/Post, Vertical, Diagonal, Boundary, Kth BST, Successor

Phase 4:
BST LCA, Merge Trees, Isomorphic, Balanced BST, Leaves Removal, K Distance

Phase 5:
Expression Tree, Tree-DLL, DLL-BST, Morris Traversal, Flip Tree
```

---

# End of Binary Tree CP + DSA Master Guide
