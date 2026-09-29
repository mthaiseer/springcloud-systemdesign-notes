# STL Pattern Master Index

> Compact STL/CP pattern index: **20 core mental models**, with variations grouped inside each form rather than creating a huge list.

## Table of Contents

1. [Form 1 — Sorting + Custom Comparator](#form-1--sorting--custom-comparator)
2. [Form 2 — Binary Search + Sorted STL](#form-2--binary-search--sorted-stl)
3. [Form 3 — Frequency Map / Hashing](#form-3--frequency-map--hashing)
4. [Form 4 — Ordered Set / Map](#form-4--ordered-set--map)
5. [Form 5 — Multiset](#form-5--multiset)
6. [Form 6 — Heap / Priority Queue](#form-6--heap--priority-queue)
7. [Form 7 — Two Heaps](#form-7--two-heaps)
8. [Form 8 — Stack Matching / Cancellation](#form-8--stack-matching--cancellation)
9. [Form 9 — Monotonic Stack](#form-9--monotonic-stack)
10. [Form 10 — Queue / Ordered Processing](#form-10--queue--ordered-processing)
11. [Form 11 — Monotonic Deque](#form-11--monotonic-deque)
12. [Form 12 — Atomic / Element Contribution](#form-12--atomic--element-contribution)
13. [Form 13 — Pivot / Left × Right Contribution](#form-13--pivot--left--right-contribution)
14. [Form 14 — Pair Contribution](#form-14--pair-contribution)
15. [Form 15 — Bit Contribution](#form-15--bit-contribution)
16. [Form 16 — Coordinate Compression](#form-16--coordinate-compression)
17. [Form 17 — Cache / Recency-Frequency Design](#form-17--cache--recency-frequency-design)
18. [Form 18 — Composite STL Design](#form-18--composite-stl-design)
19. [Form 19 — Sweep Line + STL](#form-19--sweep-line--stl)
20. [Form 20 — Offline Sorting / Queries](#form-20--offline-sorting--queries)

---

## Form 1 — Sorting + Custom Comparator

**Recognition:** Ordering the data exposes the structure or enables greedy/interval processing.

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Merge Intervals](https://leetcode.com/problems/merge-intervals/) | LC |
| 2 | [Largest Number](https://leetcode.com/problems/largest-number/) | LC |
| 3 | [Dragons](https://codeforces.com/problemset/problem/230/A) | CF |
| 4 | [Non-overlapping Intervals](https://leetcode.com/problems/non-overlapping-intervals/) | LC |

## Form 2 — Binary Search + Sorted STL

**Recognition:** Data is sorted and you need an exact position, first/last valid position, or count relative to a value.

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Find First and Last Position of Element in Sorted Array](https://leetcode.com/problems/find-first-and-last-position-of-element-in-sorted-array/) | LC |
| 2 | [Search Insert Position](https://leetcode.com/problems/search-insert-position/) | LC |
| 3 | [Successful Pairs of Spells and Potions](https://leetcode.com/problems/successful-pairs-of-spells-and-potions/) | LC |
| 4 | [Interesting Drink](https://codeforces.com/problemset/problem/706/B) | CF |

## Form 3 — Frequency Map / Hashing

**Recognition:** Repeatedly need `value → count`, `value → position`, `key → state`, or grouping by a common key.

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Two Sum](https://leetcode.com/problems/two-sum/) | LC |
| 2 | [Group Anagrams](https://leetcode.com/problems/group-anagrams/) | LC |
| 3 | [Longest Consecutive Sequence](https://leetcode.com/problems/longest-consecutive-sequence/) | LC |
| 4 | [Registration System](https://codeforces.com/problemset/problem/4/C) | CF |

## Form 4 — Ordered Set / Map

**Recognition:** Need uniqueness/order together with predecessor, successor, interval, or ordered lookup operations.

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Traffic Lights](https://cses.fi/problemset/task/1163/) | CSES |
| 2 | [My Calendar I](https://leetcode.com/problems/my-calendar-i/) | LC |
| 3 | [Contains Duplicate III](https://leetcode.com/problems/contains-duplicate-iii/) | LC |
| 4 | [Train and Queries](https://codeforces.com/problemset/problem/1702/C) | CF |

## Form 5 — Multiset

**Recognition:** Need ordered values while preserving duplicates and supporting dynamic insertion/deletion.

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Concert Tickets](https://cses.fi/problemset/task/1091/) | CSES |
| 2 | [Sliding Window Median](https://cses.fi/problemset/task/1076/) | CSES |
| 3 | [Multiset](https://codeforces.com/problemset/problem/1354/D) | CF |
| 4 | [Longest Continuous Subarray With Absolute Diff ≤ Limit](https://leetcode.com/problems/longest-continuous-subarray-with-absolute-diff-less-than-or-equal-to-limit/) | LC |

## Form 6 — Heap / Priority Queue

**Recognition:** Repeatedly need the current smallest/largest/best candidate without sorting everything again.

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Kth Largest Element in an Array](https://leetcode.com/problems/kth-largest-element-in-an-array/) | LC |
| 2 | [Top K Frequent Elements](https://leetcode.com/problems/top-k-frequent-elements/) | LC |
| 3 | [Merge k Sorted Lists](https://leetcode.com/problems/merge-k-sorted-lists/) | LC |
| 4 | [Task Scheduler](https://leetcode.com/problems/task-scheduler/) | LC |

## Form 7 — Two Heaps

**Recognition:** Dynamically maintain a lower half and upper half, usually around a median or balanced partition.

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Find Median from Data Stream](https://leetcode.com/problems/find-median-from-data-stream/) | LC |
| 2 | [Sliding Window Median](https://leetcode.com/problems/sliding-window-median/) | LC |
| 3 | [IPO](https://leetcode.com/problems/ipo/) | LC |

## Form 8 — Stack Matching / Cancellation

**Recognition:** The latest unresolved item determines whether the current item matches, cancels, or interacts.

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Valid Parentheses](https://leetcode.com/problems/valid-parentheses/) | LC |
| 2 | [Remove All Adjacent Duplicates In String](https://leetcode.com/problems/remove-all-adjacent-duplicates-in-string/) | LC |
| 3 | [Asteroid Collision](https://leetcode.com/problems/asteroid-collision/) | LC |
| 4 | [Make The String Great](https://leetcode.com/problems/make-the-string-great/) | LC |

## Form 9 — Monotonic Stack

**Recognition:** For every position, find the nearest previous/next element satisfying `<`, `<=`, `>` or `>=`, or determine its boundary of influence.

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Next Greater Element I](https://leetcode.com/problems/next-greater-element-i/) | LC |
| 2 | [Daily Temperatures](https://leetcode.com/problems/daily-temperatures/) | LC |
| 3 | [Online Stock Span](https://leetcode.com/problems/online-stock-span/) | LC |
| 4 | [Largest Rectangle in Histogram](https://leetcode.com/problems/largest-rectangle-in-histogram/) | LC |

## Form 10 — Queue / Ordered Processing

**Recognition:** Items must be processed in FIFO/arrival order or rotated/simulated in queue order.

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Number of Recent Calls](https://leetcode.com/problems/number-of-recent-calls/) | LC |
| 2 | [Reverse First K elements of Queue](https://www.geeksforgeeks.org/problems/reverse-first-k-elements-of-queue/1) | GFG |
| 3 | [Dota2 Senate](https://leetcode.com/problems/dota2-senate/) | LC |
| 4 | [Reveal Cards In Increasing Order](https://leetcode.com/problems/reveal-cards-in-increasing-order/) | LC |

## Form 11 — Monotonic Deque

**Recognition:** Need window minimum/maximum while permanently discarding elements that can no longer become useful.

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Sliding Window Maximum](https://leetcode.com/problems/sliding-window-maximum/) | LC |
| 2 | [Shortest Subarray with Sum at Least K](https://leetcode.com/problems/shortest-subarray-with-sum-at-least-k/) | LC |
| 3 | [Longest Continuous Subarray With Absolute Diff ≤ Limit](https://leetcode.com/problems/longest-continuous-subarray-with-absolute-diff-less-than-or-equal-to-limit/) | LC |

## Form 12 — Atomic / Element Contribution

**Recognition:** Instead of constructing every object, fix one element and count how many times it contributes to the final answer.

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Sum of All Subarray Sums](https://www.geeksforgeeks.org/sum-of-all-subarrays/) | GFG |
| 2 | [Sum of Subsequence Sums](https://www.geeksforgeeks.org/sum-of-all-subsequences-of-an-array/) | GFG |
| 3 | [Sum of Absolute Differences in a Sorted Array](https://leetcode.com/problems/sum-of-absolute-differences-in-a-sorted-array/) | LC |
| 4 | [Count Unique Characters of All Substrings of a Given String](https://leetcode.com/problems/count-unique-characters-of-all-substrings-of-a-given-string/) | LC |

## Form 13 — Pivot / Left × Right Contribution

**Recognition:** Fix `a[i]` as a pivot/minimum/maximum/boundary and count valid choices on its left and right.

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Sum of Subarray Minimums](https://leetcode.com/problems/sum-of-subarray-minimums/) | LC |
| 2 | [Sum of Subarray Ranges](https://leetcode.com/problems/sum-of-subarray-ranges/) | LC |
| 3 | [Largest Rectangle in Histogram](https://leetcode.com/problems/largest-rectangle-in-histogram/) | LC |
| 4 | [Maximum Subarray Min-Product](https://leetcode.com/problems/maximum-subarray-min-product/) | LC |

## Form 14 — Pair Contribution

**Recognition:** Need an aggregate over many pairs; sorting/algebra lets each element contribute to many pairs at once.

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Sum of Absolute Differences in a Sorted Array](https://leetcode.com/problems/sum-of-absolute-differences-in-a-sorted-array/) | LC |
| 2 | [Minimum Absolute Difference](https://leetcode.com/problems/minimum-absolute-difference/) | LC |
| 3 | [Minimum Absolute Difference Queries](https://leetcode.com/problems/minimum-absolute-difference-queries/) | LC |
| 4 | [Count of Smaller Numbers After Self](https://leetcode.com/problems/count-of-smaller-numbers-after-self/) | LC |

## Form 15 — Bit Contribution

**Recognition:** Treat every bit position independently and count how many elements/pairs contribute `0/1` at that bit.

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Total Hamming Distance](https://leetcode.com/problems/total-hamming-distance/) | LC |
| 2 | [Sum of All Subset XOR Totals](https://leetcode.com/problems/sum-of-all-subset-xor-totals/) | LC |
| 3 | [XOR Beauty of Array](https://leetcode.com/problems/find-xor-beauty-of-array/) | LC |
| 4 | [Bitwise AND of Numbers Range](https://leetcode.com/problems/bitwise-and-of-numbers-range/) | LC |

## Form 16 — Coordinate Compression

**Recognition:** Values are huge/sparse, but only equality or relative ordering matters; map them to compact ranks.

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Rank Transform of an Array](https://leetcode.com/problems/rank-transform-of-an-array/) | LC |
| 2 | [Nested Ranges Count](https://cses.fi/problemset/task/2169/) | CSES |
| 3 | [Petya and Array](https://codeforces.com/problemset/problem/1042/D) | CF |
| 4 | [Count of Smaller Numbers After Self](https://leetcode.com/problems/count-of-smaller-numbers-after-self/) | LC |

## Form 17 — Cache / Recency-Frequency Design

**Recognition:** Need efficient lookup plus recency/frequency ordering, eviction, or time-based state.

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [LRU Cache](https://leetcode.com/problems/lru-cache/) | LC |
| 2 | [LFU Cache](https://leetcode.com/problems/lfu-cache/) | LC |
| 3 | [Time Based Key-Value Store](https://leetcode.com/problems/time-based-key-value-store/) | LC |

## Form 18 — Composite STL Design

**Recognition:** One STL structure is insufficient; combine structures to satisfy several operations efficiently.

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Min Stack](https://leetcode.com/problems/min-stack/) | LC |
| 2 | [Insert Delete GetRandom O(1)](https://leetcode.com/problems/insert-delete-getrandom-o1/) | LC |
| 3 | [Find Median from Data Stream](https://leetcode.com/problems/find-median-from-data-stream/) | LC |
| 4 | [Snapshot Array](https://leetcode.com/problems/snapshot-array/) | LC |

## Form 19 — Sweep Line + STL

**Recognition:** Convert intervals/actions into ordered events and maintain the currently active state while sweeping through time/coordinates.

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Restaurant Customers](https://cses.fi/problemset/task/1619/) | CSES |
| 2 | [Meeting Rooms II](https://leetcode.com/problems/meeting-rooms-ii/) | LC |
| 3 | [Car Pooling](https://leetcode.com/problems/car-pooling/) | LC |
| 4 | [The Skyline Problem](https://leetcode.com/problems/the-skyline-problem/) | LC |

## Form 20 — Offline Sorting / Queries

**Recognition:** Queries do not need their original processing order; sort queries/data so state can be updated incrementally.

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Most Beautiful Item for Each Query](https://leetcode.com/problems/most-beautiful-item-for-each-query/) | LC |
| 2 | [Closest Room](https://leetcode.com/problems/closest-room/) | LC |
| 3 | [Checking Existence of Edge Length Limited Paths](https://leetcode.com/problems/checking-existence-of-edge-length-limited-paths/) | LC |
| 4 | [Maximum Beauty of an Array After Applying Operation](https://leetcode.com/problems/maximum-beauty-of-an-array-after-applying-operation/) | LC |

---

## Priority Map

| Block | Forms | Priority |
|---|---|---|
| STL Core | 1–7 | Essential |
| Stack / Queue | 8–11 | Essential |
| Contribution | 12–15 | Essential |
| Transformation | 16 | Essential |
| STL Design | 17–18 | Important |
| Advanced Combination | 19–20 | After core |

## Practice Rule

For each form, use the same progression:

| Stage | Goal |
|---|---|
| Problem 1 | Learn the pattern with guidance |
| Problem 2 | Reinforce; notes allowed |
| Problem 3 | Solve independently |
| Problem 4 | Hidden/timed variation |
| Mixed practice | Recognize the form without a topic label |

> **Mastery test:** You can recognize an unlabeled variation, derive why the form applies, and implement it without opening the notes.
