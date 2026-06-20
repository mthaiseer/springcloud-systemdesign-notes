# DSA / CP / FAANG — Topic → Form → Subform → Invariant → Recognition

```text
DSA / CP / FAANG
│
├── 1. STL / Data Structures
│   │
│   ├── vector / array
│   │   ├── Form: traversal / indexing
│   │   ├── Subform: prefix, suffix, two-pass
│   │   ├── Invariant: index represents position/state
│   │   └── Recognition: contiguous data
│   │
│   ├── string
│   │   ├── Form: char processing
│   │   ├── Subform: frequency, parsing, palindrome
│   │   ├── Invariant: characters processed in order
│   │   └── Recognition: substring/pattern problems
│   │
│   ├── stack
│   │   ├── Form: LIFO
│   │   ├── Subform: brackets, parsing, monotonic stack
│   │   ├── Invariant: top is most recent unresolved item
│   │   └── Recognition: next greater/smaller, undo, nested structure
│   │
│   ├── queue
│   │   ├── Form: FIFO
│   │   ├── Subform: level order, simulation
│   │   ├── Invariant: process in arrival/level order
│   │   └── Recognition: BFS, round-based simulation
│   │
│   ├── deque
│   │   ├── Form: double-ended queue
│   │   ├── Subform: monotonic deque, sliding window
│   │   ├── Invariant: front is best valid candidate
│   │   └── Recognition: sliding max/min
│   │
│   ├── priority_queue
│   │   ├── Form: heap
│   │   ├── Subform: top-k, scheduling, k-way merge
│   │   ├── Invariant: top is current best candidate
│   │   └── Recognition: kth, min/max resource, repeated best choice
│
├── 2. Array / Prefix / Range
│   │
│   ├── Frequency
│   │   ├── Subform: fixed freq, hashmap freq, bucket freq
│   │   ├── Invariant: count reflects processed/window elements
│   │   └── Recognition: duplicates, anagram, top frequency
│   │
│   ├── Prefix
│   │   ├── Subform: sum, xor, modulo, 2D prefix
│   │   ├── Invariant: range answer = prefix difference
│   │   └── Recognition: subarray/range query
│   │
│   ├── Difference Array
│   │   ├── Subform: range update, sweep accumulation
│   │   ├── Invariant: diff marks start/end of change
│   │   └── Recognition: many range increments
│
├── 3. Binary Search
│   │
│   ├── Classic Search
│   │   ├── Subform: exact, lower_bound, upper_bound
│   │   ├── Invariant: answer remains inside search range
│   │   └── Recognition: sorted data
│   │
│   ├── Binary Search on Answer
│   │   ├── Subform: minimize maximum, maximize minimum
│   │   ├── Invariant: feasible region is monotonic
│   │   └── Recognition: “minimum possible maximum”
│
├── 4. Sliding Window / Two Pointer
│   │
│   ├── Fixed Window
│   │   ├── Subform: size k, rolling sum
│   │   ├── Invariant: window size stays k
│   │   └── Recognition: exactly k length
│   │
│   ├── Variable Window
│   │   ├── Subform: longest valid, minimum valid
│   │   ├── Invariant: shrink until valid
│   │   └── Recognition: subarray/substring with constraint
│
├── 5. Recursion / Backtracking
│   │
│   ├── Pick / Not Pick
│   │   ├── Subform: subsets, combination sum
│   │   ├── Invariant: every item has choice
│   │   └── Recognition: generate all selections
│   │
│   ├── Permutation
│   │   ├── Subform: used array, swap method
│   │   ├── Invariant: each item used once
│   │   └── Recognition: generate all orders
│
├── 6. Dynamic Programming
│   │
│   ├── Linear DP
│   │   ├── Subform: house robber, climbing stairs, Kadane
│   │   ├── Invariant: dp[i] from previous states
│   │   └── Recognition: sequence optimization
│   │
│   ├── Knapsack DP
│   │   ├── Subform: 0/1, unbounded, subset sum
│   │   ├── Invariant: take/skip transition
│   │   └── Recognition: capacity/target sum
│   │
│   ├── Grid DP
│   │   ├── Subform: unique paths, min path, obstacle
│   │   ├── Invariant: cell depends on neighbors
│   │   └── Recognition: matrix path/count
│
├── 7. Graph
│   │
│   ├── BFS
│   │   ├── Subform: unweighted shortest path, level order
│   │   ├── Invariant: first visit is shortest distance
│   │   └── Recognition: minimum steps, unweighted edges
│   │
│   ├── DFS
│   │   ├── Subform: components, cycle, islands
│   │   ├── Invariant: fully explore one component
│   │   └── Recognition: connected region/group
│   │
│   ├── 0-1 BFS
│   │   ├── Subform: deque shortest path
│   │   ├── Invariant: weight 0 to front, weight 1 to back
│   │   └── Recognition: graph edges only 0/1
│   │
│   ├── Dijkstra
│   │   ├── Subform: shortest path, minimum effort
│   │   ├── Invariant: popped node has final shortest distance
│   │   └── Recognition: positive weighted graph
│
├── 8. Tree / Binary Tree / Trie
│   │
│   ├── Traversal
│   │   ├── Subform: preorder, inorder, postorder, level order
│   │   ├── Invariant: visit order controls computation
│   │   └── Recognition: process all nodes
│   │
│   ├── BST
│   │   ├── Subform: kth smallest, validate, iterator
│   │   ├── Invariant: inorder is sorted
│   │   └── Recognition: ordered binary tree
│   │
│   ├── Trie
│   │   ├── Subform: prefix search, word dictionary, XOR trie
│   │   ├── Invariant: path represents prefix/bits
│   │   └── Recognition: prefix lookup or max XOR
│
├── 9. Linked List
│   │
│   ├── Reversal
│   │   ├── Subform: full, partial, k-group
│   │   ├── Invariant: previous pointer owns reversed part
│   │   └── Recognition: reverse nodes
│
├── 10. Greedy
│   │
│   ├── Interval Greedy
│   │   ├── Subform: merge, non-overlap, meeting rooms
│   │   ├── Invariant: local interval choice preserves future
│   │   └── Recognition: choose/remove intervals
│
├── 11. Intervals / Sweep Line
│   │
│   ├── Overlap Count
│   │   ├── Subform: meeting rooms, max overlap
│   │   ├── Invariant: active count tracks current overlaps
│   │   └── Recognition: simultaneous events
│
├── 12. Bit Manipulation
│   │
│   ├── XOR
│   │   ├── Subform: single number, missing number
│   │   ├── Invariant: equal values cancel
│   │   └── Recognition: unique/missing with pairs
│
├── 13. String Algorithms
│   │
│   ├── KMP / Z
│   │   ├── Subform: prefix table, z-array
│   │   ├── Invariant: reuse matched prefix
│   │   └── Recognition: pattern search without backtracking
│
└── 14. Math / Number Theory
    │
    ├── GCD / LCM
    │   ├── Subform: Euclid, divisibility
    │   ├── Invariant: gcd(a,b)=gcd(b,a%b)
    │   └── Recognition: common factor/period
```
