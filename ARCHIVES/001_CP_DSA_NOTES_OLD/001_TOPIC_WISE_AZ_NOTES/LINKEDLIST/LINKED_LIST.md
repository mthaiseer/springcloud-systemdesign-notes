# Linked List CP + DSA Master Guide

> Goal: master linked list patterns for FAANG interviews and CP-style implementation problems.
>
> Style: phase-wise learning, clickable index, problem statement, input/output, C++ code, and index-by-index dry run diagrams.

---

# Clickable Index

## 0. Master Map
- [0.1 Why Linked List Matters](#01-why-linked-list-matters)
- [0.2 Core Node Structure](#02-core-node-structure)
- [0.3 Universal Linked List Checklist](#03-universal-linked-list-checklist)
- [0.4 Pattern Recognition Table](#04-pattern-recognition-table)

## Phase 1 — Foundation
- [1. Basic Traversal](#1-basic-traversal)
- [2. Find Middle of Linked List](#2-find-middle-of-linked-list)
- [3. Reverse Linked List](#3-reverse-linked-list)
- [4. Remove Nth Node From End](#4-remove-nth-node-from-end)
- [5. Detect Cycle](#5-detect-cycle)
- [6. Find Cycle Start](#6-find-cycle-start)

## Phase 2 — Core Interview Patterns
- [7. Merge Two Sorted Lists](#7-merge-two-sorted-lists)
- [8. Reverse Linked List II](#8-reverse-linked-list-ii)
- [9. Palindrome Linked List](#9-palindrome-linked-list)
- [10. Reorder List](#10-reorder-list)
- [11. Odd Even Linked List](#11-odd-even-linked-list)
- [12. Partition List](#12-partition-list)

## Phase 3 — Advanced Linked List
- [13. Reverse Nodes in K Group](#13-reverse-nodes-in-k-group)
- [14. Merge K Sorted Lists](#14-merge-k-sorted-lists)
- [15. Sort List Using Merge Sort](#15-sort-list-using-merge-sort)
- [16. Copy List With Random Pointer](#16-copy-list-with-random-pointer)
- [17. LRU Cache](#17-lru-cache)
- [18. Flatten Multilevel Doubly Linked List](#18-flatten-multilevel-doubly-linked-list)

## Final Revision
- [Linked List Templates](#linked-list-templates)
- [FAANG Must-Do Problem List](#faang-must-do-problem-list)
- [CP Relevance](#cp-relevance)
- [Final Checklist](#final-checklist)

---

# 0. Master Map

## 0.1 Why Linked List Matters

Linked list is not huge in CP, but it is very important in interviews because it tests:

- pointer handling
- edge cases
- in-place manipulation
- dummy node usage
- slow/fast pointer thinking
- recursion and reversal
- design problems like LRU cache

For FAANG, linked list is usually about implementation precision.

---

## 0.2 Core Node Structure

```cpp
struct ListNode {
    int val;
    ListNode* next;

    ListNode(int x) {
        val = x;
        next = nullptr;
    }
};
```

Doubly linked list node:

```cpp
struct Node {
    int key, val;
    Node* prev;
    Node* next;

    Node(int k, int v) {
        key = k;
        val = v;
        prev = next = nullptr;
    }
};
```

Random pointer node:

```cpp
class Node {
public:
    int val;
    Node* next;
    Node* random;

    Node(int _val) {
        val = _val;
        next = nullptr;
        random = nullptr;
    }
};
```

---

## 0.3 Universal Linked List Checklist

When solving any linked list problem, ask:

```text
1. Do I need dummy node?
2. Can head change?
3. Do I need prev/curr/next?
4. Do I need slow/fast?
5. Do I need split + reverse + merge?
6. Do I need hashmap?
7. Do I need heap?
8. Are there cycles?
9. Are there less than 2 nodes?
10. Am I losing next pointer before rewiring?
```

---

## 0.4 Pattern Recognition Table

| Problem Signal | Pattern |
|---|---|
| middle / half | slow + fast |
| nth from end | fast lead gap |
| reverse | prev/curr/next |
| remove near head | dummy node |
| sorted lists | merge |
| k sorted lists | min heap |
| palindrome | middle + reverse second half |
| reorder | middle + reverse + merge |
| cycle | Floyd slow/fast |
| copy random | hashmap or interweaving |
| cache design | hashmap + doubly linked list |

---

# Phase 1 — Foundation

---

# 1. Basic Traversal

## Problem Statement

Given the head of a linked list, print all values and count the number of nodes.

## Input

```text
1 -> 2 -> 3 -> 4 -> NULL
```

## Output

```text
Values: 1 2 3 4
Length: 4
```

## Idea

Move one pointer from head to NULL.

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

struct ListNode {
    int val;
    ListNode* next;
    ListNode(int x) : val(x), next(nullptr) {}
};

int lengthAndPrint(ListNode* head) {
    int len = 0;
    ListNode* curr = head;

    while (curr != nullptr) {
        cout << curr->val << " ";
        len++;
        curr = curr->next;
    }

    return len;
}
```

## Index-by-Index Dry Run

```text
List:
index:  0    1    2    3
value:  1 -> 2 -> 3 -> 4 -> NULL

Start:
curr = index 0, len = 0

Step 1:
curr at index 0, value = 1
print 1
len = 1
curr moves to index 1

Step 2:
curr at index 1, value = 2
print 2
len = 2
curr moves to index 2

Step 3:
curr at index 2, value = 3
print 3
len = 3
curr moves to index 3

Step 4:
curr at index 3, value = 4
print 4
len = 4
curr moves to NULL

Stop because curr == NULL
Final length = 4
```

---

# 2. Find Middle of Linked List

## Pattern

Slow and fast pointer.

## Problem Statement

Given a linked list, return the middle node. If there are two middle nodes, return the second middle.

## Input

```text
1 -> 2 -> 3 -> 4 -> 5 -> NULL
```

## Output

```text
3
```

## Input 2

```text
1 -> 2 -> 3 -> 4 -> 5 -> 6 -> NULL
```

## Output 2

```text
4
```

## Why This Works

Fast moves 2 steps. Slow moves 1 step.

When fast reaches end, slow is at middle.

## C++ Code

```cpp
ListNode* middleNode(ListNode* head) {
    ListNode* slow = head;
    ListNode* fast = head;

    while (fast != nullptr && fast->next != nullptr) {
        slow = slow->next;
        fast = fast->next->next;
    }

    return slow;
}
```

## Index-by-Index Dry Run

```text
List:
index:  0    1    2    3    4
value:  1 -> 2 -> 3 -> 4 -> 5 -> NULL

Initial:
slow = 0
fast = 0

Round 1:
slow moves 0 -> 1
fast moves 0 -> 2

State:
slow at value 2
fast at value 3

Round 2:
slow moves 1 -> 2
fast moves 2 -> 4

State:
slow at value 3
fast at value 5

Check next:
fast->next == NULL
Stop

Answer = slow = value 3
```

## Pointer Movement Diagram

```text
Round 0:
S/F
 1 -> 2 -> 3 -> 4 -> 5

Round 1:
      S    F
 1 -> 2 -> 3 -> 4 -> 5

Round 2:
           S         F
 1 -> 2 -> 3 -> 4 -> 5

Middle = 3
```

---

# 3. Reverse Linked List

## Pattern

Three pointer pattern: prev, curr, nextNode.

## Problem Statement

Reverse a singly linked list.

## Input

```text
1 -> 2 -> 3 -> 4 -> NULL
```

## Output

```text
4 -> 3 -> 2 -> 1 -> NULL
```

## Core Idea

For every node:

```text
save next
reverse current pointer
move prev forward
move curr forward
```

## C++ Code

```cpp
ListNode* reverseList(ListNode* head) {
    ListNode* prev = nullptr;
    ListNode* curr = head;

    while (curr != nullptr) {
        ListNode* nextNode = curr->next;
        curr->next = prev;
        prev = curr;
        curr = nextNode;
    }

    return prev;
}
```

## Index-by-Index Dry Run

```text
Original:
NULL <- prev    curr
              index 0
                1 -> 2 -> 3 -> 4 -> NULL

Step 1: curr = 1
nextNode = 2
1->next = NULL
prev = 1
curr = 2

Now:
NULL <- 1    2 -> 3 -> 4 -> NULL
       prev  curr

Step 2: curr = 2
nextNode = 3
2->next = 1
prev = 2
curr = 3

Now:
NULL <- 1 <- 2    3 -> 4 -> NULL
            prev  curr

Step 3: curr = 3
nextNode = 4
3->next = 2
prev = 3
curr = 4

Now:
NULL <- 1 <- 2 <- 3    4 -> NULL
                 prev  curr

Step 4: curr = 4
nextNode = NULL
4->next = 3
prev = 4
curr = NULL

Now:
NULL <- 1 <- 2 <- 3 <- 4
                      prev
curr = NULL

Return prev = 4
Final:
4 -> 3 -> 2 -> 1 -> NULL
```

## Visual Pointer Table

| Step | curr | nextNode | Reversed Part | Remaining Part |
|---|---:|---:|---|---|
| Start | 1 | - | NULL | 1->2->3->4 |
| 1 | 1 | 2 | 1->NULL | 2->3->4 |
| 2 | 2 | 3 | 2->1->NULL | 3->4 |
| 3 | 3 | 4 | 3->2->1->NULL | 4 |
| 4 | 4 | NULL | 4->3->2->1->NULL | NULL |

---

# 4. Remove Nth Node From End

## Pattern

Fast pointer keeps `n` gap from slow pointer.

## Problem Statement

Remove the nth node from the end of list.

## Input

```text
head = 1 -> 2 -> 3 -> 4 -> 5
n = 2
```

## Output

```text
1 -> 2 -> 3 -> 5
```

## Why Dummy Node Is Used

If the removed node is head, dummy handles it cleanly.

## C++ Code

```cpp
ListNode* removeNthFromEnd(ListNode* head, int n) {
    ListNode* dummy = new ListNode(0);
    dummy->next = head;

    ListNode* fast = dummy;
    ListNode* slow = dummy;

    for (int i = 0; i < n; i++) {
        fast = fast->next;
    }

    while (fast->next != nullptr) {
        fast = fast->next;
        slow = slow->next;
    }

    ListNode* nodeToDelete = slow->next;
    slow->next = slow->next->next;
    delete nodeToDelete;

    return dummy->next;
}
```

## Index-by-Index Dry Run

```text
List with dummy:
index: D    0    1    2    3    4
value: 0 -> 1 -> 2 -> 3 -> 4 -> 5 -> NULL
n = 2

Initial:
fast = D
slow = D

Create gap of 2:
i = 0: fast moves D -> 0(value 1)
i = 1: fast moves 0 -> 1(value 2)

State:
slow = D
fast = index 1(value 2)

Move both until fast->next == NULL:

Round 1:
fast: 1 -> 2(value 3)
slow: D -> 0(value 1)

Round 2:
fast: 2 -> 3(value 4)
slow: 0 -> 1(value 2)

Round 3:
fast: 3 -> 4(value 5)
slow: 1 -> 2(value 3)

Stop because fast->next == NULL

slow is before node to delete.
slow = value 3
slow->next = value 4

Delete 4:
3->next = 5

Final:
1 -> 2 -> 3 -> 5 -> NULL
```

---

# 5. Detect Cycle

## Pattern

Floyd cycle detection.

## Problem Statement

Return true if linked list has a cycle.

## Input

```text
1 -> 2 -> 3 -> 4
     ^         |
     |_________|
```

## Output

```text
true
```

## Why This Works

If there is a cycle, fast pointer eventually catches slow pointer.

## C++ Code

```cpp
bool hasCycle(ListNode* head) {
    ListNode* slow = head;
    ListNode* fast = head;

    while (fast != nullptr && fast->next != nullptr) {
        slow = slow->next;
        fast = fast->next->next;

        if (slow == fast) return true;
    }

    return false;
}
```

## Index-by-Index Dry Run

```text
Cycle list:
0:1 -> 1:2 -> 2:3 -> 3:4
       ^              |
       |______________|

Initial:
slow = 0
fast = 0

Round 1:
slow = 1
fast = 2

Round 2:
slow = 2
fast = 1
Because fast moves: 2 -> 3 -> 1

Round 3:
slow = 3
fast = 3

slow == fast
Cycle exists
```

---

# 6. Find Cycle Start

## Pattern

Floyd cycle detection + reset one pointer to head.

## Problem Statement

Given a linked list with a cycle, return the node where the cycle begins.

## Input

```text
1 -> 2 -> 3 -> 4 -> 5
     ^              |
     |______________|
```

## Output

```text
2
```

## C++ Code

```cpp
ListNode* detectCycle(ListNode* head) {
    ListNode* slow = head;
    ListNode* fast = head;

    while (fast != nullptr && fast->next != nullptr) {
        slow = slow->next;
        fast = fast->next->next;

        if (slow == fast) {
            ListNode* p1 = head;
            ListNode* p2 = slow;

            while (p1 != p2) {
                p1 = p1->next;
                p2 = p2->next;
            }

            return p1;
        }
    }

    return nullptr;
}
```

## Index-by-Index Dry Run

```text
List:
0:1 -> 1:2 -> 2:3 -> 3:4 -> 4:5
       ^                         |
       |_________________________|

Cycle starts at index 1.

Phase 1: Find meeting point

Round 0:
slow = 0
fast = 0

Round 1:
slow = 1
fast = 2

Round 2:
slow = 2
fast = 4

Round 3:
slow = 3
fast = 2

Round 4:
slow = 4
fast = 4
Meeting point = index 4

Phase 2: Find cycle start

p1 = head = index 0
p2 = meeting = index 4

Move both 1 step:

Step 1:
p1: 0 -> 1
p2: 4 -> 1

p1 == p2 == index 1
Cycle start = value 2
```

---

# Phase 2 — Core Interview Patterns

---

# 7. Merge Two Sorted Lists

## Pattern

Dummy node + two pointers.

## Problem Statement

Merge two sorted linked lists into one sorted linked list.

## Input

```text
l1 = 1 -> 3 -> 5
l2 = 2 -> 4 -> 6
```

## Output

```text
1 -> 2 -> 3 -> 4 -> 5 -> 6
```

## C++ Code

```cpp
ListNode* mergeTwoLists(ListNode* l1, ListNode* l2) {
    ListNode* dummy = new ListNode(0);
    ListNode* tail = dummy;

    while (l1 != nullptr && l2 != nullptr) {
        if (l1->val <= l2->val) {
            tail->next = l1;
            l1 = l1->next;
        } else {
            tail->next = l2;
            l2 = l2->next;
        }
        tail = tail->next;
    }

    if (l1 != nullptr) tail->next = l1;
    if (l2 != nullptr) tail->next = l2;

    return dummy->next;
}
```

## Index-by-Index Dry Run

```text
l1: 1 -> 3 -> 5
l2: 2 -> 4 -> 6
result: dummy

Compare l1=1, l2=2
Pick 1
result: 1
l1 moves to 3

Compare l1=3, l2=2
Pick 2
result: 1 -> 2
l2 moves to 4

Compare l1=3, l2=4
Pick 3
result: 1 -> 2 -> 3
l1 moves to 5

Compare l1=5, l2=4
Pick 4
result: 1 -> 2 -> 3 -> 4
l2 moves to 6

Compare l1=5, l2=6
Pick 5
result: 1 -> 2 -> 3 -> 4 -> 5
l1 becomes NULL

Attach remaining l2:
result: 1 -> 2 -> 3 -> 4 -> 5 -> 6
```

---

# 8. Reverse Linked List II

## Pattern

Dummy node + local reverse.

## Problem Statement

Reverse nodes from position `left` to `right`.

## Input

```text
head = 1 -> 2 -> 3 -> 4 -> 5
left = 2, right = 4
```

## Output

```text
1 -> 4 -> 3 -> 2 -> 5
```

## C++ Code

```cpp
ListNode* reverseBetween(ListNode* head, int left, int right) {
    if (head == nullptr || left == right) return head;

    ListNode* dummy = new ListNode(0);
    dummy->next = head;

    ListNode* before = dummy;
    for (int i = 1; i < left; i++) {
        before = before->next;
    }

    ListNode* curr = before->next;

    for (int i = 0; i < right - left; i++) {
        ListNode* moveNode = curr->next;
        curr->next = moveNode->next;
        moveNode->next = before->next;
        before->next = moveNode;
    }

    return dummy->next;
}
```

## Index-by-Index Dry Run

```text
Input:
1 -> 2 -> 3 -> 4 -> 5
left = 2, right = 4

Before reversal:
before = node 1
curr = node 2

Initial:
1 -> 2 -> 3 -> 4 -> 5
     C
B = 1

Iteration 1:
moveNode = 3
Remove 3 after curr:
1 -> 2 -> 4 -> 5
Insert 3 after before:
1 -> 3 -> 2 -> 4 -> 5

Iteration 2:
moveNode = 4
Remove 4 after curr:
1 -> 3 -> 2 -> 5
Insert 4 after before:
1 -> 4 -> 3 -> 2 -> 5

Final:
1 -> 4 -> 3 -> 2 -> 5
```

---

# 9. Palindrome Linked List

## Pattern

Middle + reverse second half + compare.

## Problem Statement

Check whether a linked list is palindrome.

## Input

```text
1 -> 2 -> 2 -> 1
```

## Output

```text
true
```

## C++ Code

```cpp
bool isPalindrome(ListNode* head) {
    if (head == nullptr || head->next == nullptr) return true;

    ListNode* slow = head;
    ListNode* fast = head;

    while (fast != nullptr && fast->next != nullptr) {
        slow = slow->next;
        fast = fast->next->next;
    }

    ListNode* second = reverseList(slow);
    ListNode* first = head;

    while (second != nullptr) {
        if (first->val != second->val) return false;
        first = first->next;
        second = second->next;
    }

    return true;
}
```

## Index-by-Index Dry Run

```text
Input:
index: 0    1    2    3
value: 1 -> 2 -> 2 -> 1

Step 1: Find middle
slow/fast start at index 0

Round 1:
slow = index 1(value 2)
fast = index 2(value 2)

Round 2:
slow = index 2(value 2)
fast = NULL

Middle starts at index 2.

Step 2: Reverse second half
Original second half:
2 -> 1

Reversed:
1 -> 2

Step 3: Compare
first:  1 -> 2 -> 2 -> 1
second: 1 -> 2

Compare 1 == 1 yes
Compare 2 == 2 yes
second becomes NULL

Answer = true
```

---

# 10. Reorder List

## Pattern

Find middle + reverse second half + merge alternately.

## Problem Statement

Reorder linked list:

```text
L0 -> L1 -> ... -> Ln-1 -> Ln
```

into:

```text
L0 -> Ln -> L1 -> Ln-1 -> L2 -> Ln-2
```

## Input

```text
1 -> 2 -> 3 -> 4 -> 5
```

## Output

```text
1 -> 5 -> 2 -> 4 -> 3
```

## C++ Code

```cpp
void reorderList(ListNode* head) {
    if (head == nullptr || head->next == nullptr) return;

    ListNode* slow = head;
    ListNode* fast = head;

    while (fast->next != nullptr && fast->next->next != nullptr) {
        slow = slow->next;
        fast = fast->next->next;
    }

    ListNode* second = slow->next;
    slow->next = nullptr;
    second = reverseList(second);

    ListNode* first = head;

    while (second != nullptr) {
        ListNode* temp1 = first->next;
        ListNode* temp2 = second->next;

        first->next = second;
        second->next = temp1;

        first = temp1;
        second = temp2;
    }
}
```

## Index-by-Index Dry Run

```text
Input:
1 -> 2 -> 3 -> 4 -> 5

Step 1: Find first half end
slow stops at 3

Split:
first half:  1 -> 2 -> 3
second half: 4 -> 5

Step 2: Reverse second half
second half becomes:
5 -> 4

Step 3: Merge alternately

Initial:
first = 1 -> 2 -> 3
second = 5 -> 4

Merge round 1:
temp1 = 2
temp2 = 4
1->next = 5
5->next = 2

Now:
1 -> 5 -> 2 -> 3
second remaining: 4

Merge round 2:
first = 2
second = 4
temp1 = 3
temp2 = NULL
2->next = 4
4->next = 3

Final:
1 -> 5 -> 2 -> 4 -> 3
```

---

# 11. Odd Even Linked List

## Pattern

Separate odd index chain and even index chain.

## Problem Statement

Group odd-indexed nodes first, then even-indexed nodes. Indexing is 1-based.

## Input

```text
1 -> 2 -> 3 -> 4 -> 5
```

## Output

```text
1 -> 3 -> 5 -> 2 -> 4
```

## C++ Code

```cpp
ListNode* oddEvenList(ListNode* head) {
    if (head == nullptr) return nullptr;

    ListNode* odd = head;
    ListNode* even = head->next;
    ListNode* evenHead = even;

    while (even != nullptr && even->next != nullptr) {
        odd->next = even->next;
        odd = odd->next;

        even->next = odd->next;
        even = even->next;
    }

    odd->next = evenHead;
    return head;
}
```

## Index-by-Index Dry Run

```text
Input:
position: 1    2    3    4    5
value:    1 -> 2 -> 3 -> 4 -> 5

Initial:
odd = 1
even = 2
evenHead = 2

Round 1:
odd->next = 3
odd moves to 3
even->next = 4
even moves to 4

Odd chain:  1 -> 3
Even chain: 2 -> 4

Round 2:
odd->next = 5
odd moves to 5
even->next = NULL
even moves to NULL

Odd chain:  1 -> 3 -> 5
Even chain: 2 -> 4

Attach:
odd->next = evenHead

Final:
1 -> 3 -> 5 -> 2 -> 4
```

---

# 12. Partition List

## Pattern

Two dummy lists.

## Problem Statement

Given linked list and value `x`, partition list so that nodes less than `x` come before nodes greater than or equal to `x`.

## Input

```text
head = 1 -> 4 -> 3 -> 2 -> 5 -> 2
x = 3
```

## Output

```text
1 -> 2 -> 2 -> 4 -> 3 -> 5
```

## C++ Code

```cpp
ListNode* partition(ListNode* head, int x) {
    ListNode* smallDummy = new ListNode(0);
    ListNode* bigDummy = new ListNode(0);

    ListNode* small = smallDummy;
    ListNode* big = bigDummy;

    while (head != nullptr) {
        if (head->val < x) {
            small->next = head;
            small = small->next;
        } else {
            big->next = head;
            big = big->next;
        }
        head = head->next;
    }

    big->next = nullptr;
    small->next = bigDummy->next;

    return smallDummy->next;
}
```

## Index-by-Index Dry Run

```text
Input:
1 -> 4 -> 3 -> 2 -> 5 -> 2
x = 3

Process 1:
1 < 3
small: 1
big: empty

Process 4:
4 >= 3
small: 1
big: 4

Process 3:
3 >= 3
small: 1
big: 4 -> 3

Process 2:
2 < 3
small: 1 -> 2
big: 4 -> 3

Process 5:
5 >= 3
small: 1 -> 2
big: 4 -> 3 -> 5

Process 2:
2 < 3
small: 1 -> 2 -> 2
big: 4 -> 3 -> 5

Attach small + big:
1 -> 2 -> 2 -> 4 -> 3 -> 5
```

---

# Phase 3 — Advanced Linked List

---

# 13. Reverse Nodes in K Group

## Pattern

Group validation + local reverse.

## Problem Statement

Reverse nodes in groups of size `k`. If remaining nodes are fewer than k, leave them unchanged.

## Input

```text
head = 1 -> 2 -> 3 -> 4 -> 5
k = 2
```

## Output

```text
2 -> 1 -> 4 -> 3 -> 5
```

## C++ Code

```cpp
ListNode* reverseKGroup(ListNode* head, int k) {
    ListNode* dummy = new ListNode(0);
    dummy->next = head;

    ListNode* groupPrev = dummy;

    while (true) {
        ListNode* kth = groupPrev;
        for (int i = 0; i < k && kth != nullptr; i++) {
            kth = kth->next;
        }

        if (kth == nullptr) break;

        ListNode* groupNext = kth->next;

        ListNode* prev = groupNext;
        ListNode* curr = groupPrev->next;

        while (curr != groupNext) {
            ListNode* nextNode = curr->next;
            curr->next = prev;
            prev = curr;
            curr = nextNode;
        }

        ListNode* oldGroupStart = groupPrev->next;
        groupPrev->next = kth;
        groupPrev = oldGroupStart;
    }

    return dummy->next;
}
```

## Index-by-Index Dry Run

```text
Input:
1 -> 2 -> 3 -> 4 -> 5
k = 2

Add dummy:
D -> 1 -> 2 -> 3 -> 4 -> 5

groupPrev = D

Group 1:
Find kth from D by 2 steps:
step 1: kth = 1
step 2: kth = 2

groupNext = 3
Reverse [1,2]

Before:
D -> 1 -> 2 -> 3
     curr  kth groupNext

Reverse internal:
1->next = 3
2->next = 1

After group 1:
D -> 2 -> 1 -> 3 -> 4 -> 5
          groupPrev moves to old start = 1

Group 2:
groupPrev = 1
Find kth by 2 steps:
step 1: kth = 3
step 2: kth = 4

groupNext = 5
Reverse [3,4]

After group 2:
D -> 2 -> 1 -> 4 -> 3 -> 5
                    groupPrev = 3

Group 3:
Try to find 2 nodes from groupPrev=3
step 1: kth = 5
step 2: kth = NULL
Not enough nodes.
Stop.

Final:
2 -> 1 -> 4 -> 3 -> 5
```

---

# 14. Merge K Sorted Lists

## Pattern

Min heap.

## Problem Statement

Merge k sorted linked lists into one sorted linked list.

## Input

```text
lists = [
  1 -> 4 -> 5,
  1 -> 3 -> 4,
  2 -> 6
]
```

## Output

```text
1 -> 1 -> 2 -> 3 -> 4 -> 4 -> 5 -> 6
```

## C++ Code

```cpp
struct Compare {
    bool operator()(ListNode* a, ListNode* b) {
        return a->val > b->val;
    }
};

ListNode* mergeKLists(vector<ListNode*>& lists) {
    priority_queue<ListNode*, vector<ListNode*>, Compare> pq;

    for (ListNode* node : lists) {
        if (node != nullptr) pq.push(node);
    }

    ListNode* dummy = new ListNode(0);
    ListNode* tail = dummy;

    while (!pq.empty()) {
        ListNode* smallest = pq.top();
        pq.pop();

        tail->next = smallest;
        tail = tail->next;

        if (smallest->next != nullptr) {
            pq.push(smallest->next);
        }
    }

    return dummy->next;
}
```

## Index-by-Index Dry Run

```text
Lists:
A: 1 -> 4 -> 5
B: 1 -> 3 -> 4
C: 2 -> 6

Initial heap contains:
A1, B1, C2

Step 1:
pop A1
result: 1
push A4
heap: B1, C2, A4

Step 2:
pop B1
result: 1 -> 1
push B3
heap: C2, B3, A4

Step 3:
pop C2
result: 1 -> 1 -> 2
push C6
heap: B3, A4, C6

Step 4:
pop B3
result: 1 -> 1 -> 2 -> 3
push B4
heap: A4, B4, C6

Step 5:
pop A4
result: 1 -> 1 -> 2 -> 3 -> 4
push A5
heap: B4, A5, C6

Step 6:
pop B4
result: 1 -> 1 -> 2 -> 3 -> 4 -> 4
B has no next
heap: A5, C6

Step 7:
pop A5
result: 1 -> 1 -> 2 -> 3 -> 4 -> 4 -> 5
heap: C6

Step 8:
pop C6
result: 1 -> 1 -> 2 -> 3 -> 4 -> 4 -> 5 -> 6
heap empty

Final done
```

---

# 15. Sort List Using Merge Sort

## Pattern

Slow/fast split + recursive merge sort.

## Problem Statement

Sort linked list in O(n log n) time.

## Input

```text
4 -> 2 -> 1 -> 3
```

## Output

```text
1 -> 2 -> 3 -> 4
```

## C++ Code

```cpp
ListNode* sortList(ListNode* head) {
    if (head == nullptr || head->next == nullptr) return head;

    ListNode* slow = head;
    ListNode* fast = head->next;

    while (fast != nullptr && fast->next != nullptr) {
        slow = slow->next;
        fast = fast->next->next;
    }

    ListNode* second = slow->next;
    slow->next = nullptr;

    ListNode* left = sortList(head);
    ListNode* right = sortList(second);

    return mergeTwoLists(left, right);
}
```

## Recursive Tree Dry Run

```text
sort(4 -> 2 -> 1 -> 3)

Split into:
left  = 4 -> 2
right = 1 -> 3

Tree:

sort(4,2,1,3)
├── sort(4,2)
│   ├── sort(4) = 4
│   └── sort(2) = 2
│   └── merge(4,2) = 2 -> 4
│
└── sort(1,3)
    ├── sort(1) = 1
    └── sort(3) = 3
    └── merge(1,3) = 1 -> 3

Final merge:
merge(2 -> 4, 1 -> 3)

Compare 2 and 1: pick 1
Compare 2 and 3: pick 2
Compare 4 and 3: pick 3
Attach 4

Final:
1 -> 2 -> 3 -> 4
```

---

# 16. Copy List With Random Pointer

## Pattern

Hashmap node mapping.

## Problem Statement

Each node has `next` and `random` pointer. Deep copy the list.

## Input

```text
Node values: 7 -> 13 -> 11
Random:
7.random = NULL
13.random = 7
11.random = 13
```

## Output

```text
Deep copied list with same next/random structure.
```

## C++ Code

```cpp
class Node {
public:
    int val;
    Node* next;
    Node* random;

    Node(int _val) {
        val = _val;
        next = nullptr;
        random = nullptr;
    }
};

Node* copyRandomList(Node* head) {
    if (head == nullptr) return nullptr;

    unordered_map<Node*, Node*> mp;

    Node* curr = head;
    while (curr != nullptr) {
        mp[curr] = new Node(curr->val);
        curr = curr->next;
    }

    curr = head;
    while (curr != nullptr) {
        mp[curr]->next = mp[curr->next];
        mp[curr]->random = mp[curr->random];
        curr = curr->next;
    }

    return mp[head];
}
```

## Index-by-Index Dry Run

```text
Original:
index 0: val 7,  random NULL
index 1: val 13, random index 0
index 2: val 11, random index 1

Pass 1: Create copy nodes
mp[original 7]  = copy 7
mp[original 13] = copy 13
mp[original 11] = copy 11

Pass 2: Connect pointers

At original 7:
copy7.next = copy13
copy7.random = NULL

At original 13:
copy13.next = copy11
copy13.random = copy7

At original 11:
copy11.next = NULL
copy11.random = copy13

Return copy7
```

---

# 17. LRU Cache

## Pattern

Hashmap + doubly linked list.

## Problem Statement

Design LRU Cache with O(1) get and put.

## Operations

```text
LRUCache cache(2)
put(1,1)
put(2,2)
get(1)    returns 1
put(3,3)  evicts key 2
get(2)    returns -1
```

## Idea

- hashmap gives O(1) key to node
- doubly linked list keeps recent order
- front = most recently used
- back = least recently used

## C++ Code

```cpp
class LRUCache {
private:
    struct Node {
        int key, val;
        Node* prev;
        Node* next;
        Node(int k, int v) : key(k), val(v), prev(nullptr), next(nullptr) {}
    };

    int cap;
    unordered_map<int, Node*> mp;
    Node* head;
    Node* tail;

    void removeNode(Node* node) {
        Node* p = node->prev;
        Node* n = node->next;
        p->next = n;
        n->prev = p;
    }

    void insertAfterHead(Node* node) {
        Node* first = head->next;
        head->next = node;
        node->prev = head;
        node->next = first;
        first->prev = node;
    }

public:
    LRUCache(int capacity) {
        cap = capacity;
        head = new Node(-1, -1);
        tail = new Node(-1, -1);
        head->next = tail;
        tail->prev = head;
    }

    int get(int key) {
        if (mp.find(key) == mp.end()) return -1;

        Node* node = mp[key];
        removeNode(node);
        insertAfterHead(node);

        return node->val;
    }

    void put(int key, int value) {
        if (mp.find(key) != mp.end()) {
            Node* node = mp[key];
            node->val = value;
            removeNode(node);
            insertAfterHead(node);
            return;
        }

        if ((int)mp.size() == cap) {
            Node* lru = tail->prev;
            removeNode(lru);
            mp.erase(lru->key);
            delete lru;
        }

        Node* node = new Node(key, value);
        mp[key] = node;
        insertAfterHead(node);
    }
};
```

## Operation-by-Operation Dry Run

```text
Capacity = 2
List format:
HEAD <-> most recent ... least recent <-> TAIL

Operation 1: put(1,1)
Cache:
HEAD <-> 1 <-> TAIL
Map: {1}

Operation 2: put(2,2)
Insert 2 after head
Cache:
HEAD <-> 2 <-> 1 <-> TAIL
Map: {1,2}
Most recent = 2
Least recent = 1

Operation 3: get(1)
1 exists.
Move 1 to front.
Cache before:
HEAD <-> 2 <-> 1 <-> TAIL
Cache after:
HEAD <-> 1 <-> 2 <-> TAIL
Return 1

Operation 4: put(3,3)
Capacity full.
Least recent = tail->prev = 2
Evict 2.
Insert 3 at front.
Cache:
HEAD <-> 3 <-> 1 <-> TAIL
Map: {1,3}

Operation 5: get(2)
2 not in map.
Return -1
```

---

# 18. Flatten Multilevel Doubly Linked List

## Pattern

DFS traversal.

## Problem Statement

Each node has `next`, `prev`, and `child`. Flatten into single doubly linked list.

## Input

```text
1 - 2 - 3 - 4
        |
        7 - 8
```

## Output

```text
1 - 2 - 3 - 7 - 8 - 4
```

## C++ Code

```cpp
class DNode {
public:
    int val;
    DNode* prev;
    DNode* next;
    DNode* child;
};

class Solution {
public:
    DNode* flatten(DNode* head) {
        if (head == nullptr) return nullptr;

        DNode* curr = head;

        while (curr != nullptr) {
            if (curr->child == nullptr) {
                curr = curr->next;
                continue;
            }

            DNode* child = curr->child;
            DNode* nextNode = curr->next;

            DNode* tail = child;
            while (tail->next != nullptr) {
                tail = tail->next;
            }

            curr->next = child;
            child->prev = curr;
            curr->child = nullptr;

            tail->next = nextNode;
            if (nextNode != nullptr) nextNode->prev = tail;

            curr = curr->next;
        }

        return head;
    }
};
```

## Index-by-Index Dry Run

```text
Input:
Main:  1 - 2 - 3 - 4
              |
Child:        7 - 8

curr = 1
No child. Move to 2.

curr = 2
No child. Move to 3.

curr = 3
Has child 7.
Save nextNode = 4.
Find child tail = 8.

Connect:
3.next = 7
7.prev = 3
3.child = NULL
8.next = 4
4.prev = 8

Now list:
1 - 2 - 3 - 7 - 8 - 4

Continue curr = 7, then 8, then 4.
No more child.
Final flattened list:
1 - 2 - 3 - 7 - 8 - 4
```

---

# Linked List Templates

## Template 1: Basic Traversal

```cpp
ListNode* curr = head;
while (curr != nullptr) {
    // use curr->val
    curr = curr->next;
}
```

## Template 2: Reverse List

```cpp
ListNode* prev = nullptr;
ListNode* curr = head;

while (curr != nullptr) {
    ListNode* nextNode = curr->next;
    curr->next = prev;
    prev = curr;
    curr = nextNode;
}

return prev;
```

## Template 3: Dummy Node

```cpp
ListNode* dummy = new ListNode(0);
dummy->next = head;

// manipulate using dummy

return dummy->next;
```

## Template 4: Slow Fast Pointer

```cpp
ListNode* slow = head;
ListNode* fast = head;

while (fast != nullptr && fast->next != nullptr) {
    slow = slow->next;
    fast = fast->next->next;
}
```

## Template 5: Merge Two Lists

```cpp
ListNode* dummy = new ListNode(0);
ListNode* tail = dummy;

while (a != nullptr && b != nullptr) {
    if (a->val <= b->val) {
        tail->next = a;
        a = a->next;
    } else {
        tail->next = b;
        b = b->next;
    }
    tail = tail->next;
}

tail->next = (a != nullptr ? a : b);
return dummy->next;
```

## Template 6: Split + Reverse + Merge

```text
Used in:
- palindrome list
- reorder list

Steps:
1. find middle
2. split list
3. reverse second half
4. compare or merge
```

---

# FAANG Must-Do Problem List

## Easy

| Problem | Pattern |
|---|---|
| Reverse Linked List | reverse |
| Middle of Linked List | slow/fast |
| Linked List Cycle | slow/fast |
| Merge Two Sorted Lists | dummy + merge |
| Remove Duplicates from Sorted List | traversal |
| Intersection of Two Linked Lists | two pointer |

## Medium

| Problem | Pattern |
|---|---|
| Remove Nth Node From End | fast gap |
| Add Two Numbers | dummy + carry |
| Reverse Linked List II | local reverse |
| Reorder List | split + reverse + merge |
| Palindrome Linked List | middle + reverse |
| Odd Even Linked List | chain separation |
| Partition List | two dummy lists |
| Copy List with Random Pointer | hashmap |
| Sort List | merge sort |
| LRU Cache | DLL + hashmap |

## Hard

| Problem | Pattern |
|---|---|
| Reverse Nodes in K Group | group reverse |
| Merge K Sorted Lists | heap |
| LFU Cache | hashmap + frequency list |
| Flatten Multilevel Doubly Linked List | DFS |

---

# CP Relevance

Linked list is less common in CP than arrays, graphs, DP, segment tree, or binary indexed tree.

For CP, linked list appears in:

- custom data structure simulation
- Josephus-like deletion simulation
- LRU/LFU cache design
- editor/cursor problems
- memory-efficient node manipulation
- intrusive list style problems

For CP contests, you usually do not need 100 linked list problems. You need strong implementation confidence.

---

# Final Checklist

Before saying you mastered linked list, you should be able to code these without looking:

```text
[ ] reverse linked list
[ ] find middle
[ ] detect cycle
[ ] find cycle start
[ ] remove nth from end
[ ] merge two sorted lists
[ ] reverse between left and right
[ ] palindrome linked list
[ ] reorder list
[ ] reverse k group
[ ] merge k lists
[ ] sort linked list
[ ] copy random pointer
[ ] LRU cache
```

---

# Final Mental Model

```text
Linked List Problem?

├── Head may change?
│   └── Use dummy node
│
├── Need middle / cycle / nth from end?
│   └── Use slow-fast pointer
│
├── Need reverse?
│   └── Use prev-curr-next
│
├── Need reorder / palindrome?
│   └── Find middle + reverse second half
│
├── Need sorted merge?
│   └── Dummy + tail pointer
│
├── Need k lists?
│   └── Min heap
│
├── Need random pointer copy?
│   └── Hashmap old node -> new node
│
└── Need O(1) cache?
    └── Hashmap + doubly linked list
```

---

# One-Month FAANG Linked List Plan

## Day 1
- traversal
- middle
- reverse
- cycle detection

## Day 2
- remove nth
- merge two lists
- intersection
- add two numbers

## Day 3
- reverse between
- palindrome
- reorder
- odd even list

## Day 4
- reverse k group
- merge k lists
- sort list

## Day 5
- copy random pointer
- LRU cache
- LFU cache overview

## Day 6+
- random mixed practice
- dry run pointer movements
- solve without hints

