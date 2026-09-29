# STL Pattern Index — Deep Practice Edition

> Compact **form-wise STL mastery index** for Competitive Programming, Codeforces and FAANG-style interviews.
> Each form contains **3–5 benchmark problems**. Exact benchmark problems are not repeated across forms.

<a id="toc"></a>
## Table of Contents

1. [Form 1 — Sorting / Basic Ordering](#form-1)
2. [Form 2 — Custom Comparator / Multi-Key Sorting](#form-2)
3. [Form 3 — Lower Bound / Upper Bound](#form-3)
4. [Form 4 — Next Permutation / Arrangement Generation](#form-4)
5. [Form 5 — Frequency Counting / Hash Map](#form-5)
6. [Form 6 — Set / Membership / Distinct Values](#form-6)
7. [Form 7 — Ordered Set / Predecessor / Successor](#form-7)
8. [Form 8 — Multiset / Dynamic Ordered Collection](#form-8)
9. [Form 9 — Stack: Matching / Valid Parentheses](#form-9)
10. [Form 10 — Stack: Cancellation / Remove Elements](#form-10)
11. [Form 11 — Monotonic Stack: Next Greater / Smaller](#form-11)
12. [Form 12 — Monotonic Stack: Greedy Removal](#form-12)
13. [Form 13 — Monotonic Stack + Contribution](#form-13)
14. [Form 14 — Queue / FIFO Processing](#form-14)
15. [Form 15 — Monotonic Deque / Sliding Window Min-Max](#form-15)
16. [Form 16 — Priority Queue / Repeated Best Choice](#form-16)
17. [Form 17 — Top K Elements](#form-17)
18. [Form 18 — Two Heaps / Running Median](#form-18)
19. [Form 19 — Atomic / Element Contribution](#form-19)
20. [Form 20 — Pivot / Left × Right Contribution](#form-20)
21. [Form 21 — Pair Contribution](#form-21)
22. [Form 22 — Bit Contribution](#form-22)
23. [Form 23 — Stream Mean / Variance / Running Statistics](#form-23)
24. [Form 24 — Min Stack / Maintain Aggregate State](#form-24)
25. [Form 25 — Stack / Queue Transformation](#form-25)
26. [Form 26 — Lazy Stack Increment](#form-26)
27. [Form 27 — Product of Last K / Prefix State](#form-27)
28. [Form 28 — Snapshot / Versioned Data](#form-28)
29. [Form 29 — LRU Cache](#form-29)
30. [Form 30 — LFU / Frequency-Based Cache](#form-30)
31. [Form 31 — Randomized Data Structure](#form-31)

---

<a id="form-1"></a>
## Form 1 — Sorting / Basic Ordering

**Recognition:** Ordering exposes structure or enables greedy/two-pointer processing.

**Problems: 5**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Sort an Array](https://leetcode.com/problems/sort-an-array/) | LC |
| 2 | [Helpful Maths](https://codeforces.com/problemset/problem/339/A) | CF |
| 3 | [Dragons](https://codeforces.com/problemset/problem/230/A) | CF |
| 4 | [Business trip](https://codeforces.com/problemset/problem/149/A) | CF |
| 5 | [Apartments](https://cses.fi/problemset/task/1084/) | CSES |

[↑ Back to TOC](#toc)

---

<a id="form-2"></a>
## Form 2 — Custom Comparator / Multi-Key Sorting

**Recognition:** Sort by multiple keys or by a non-standard ordering relation.

**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Largest Number](https://leetcode.com/problems/largest-number/) | LC |
| 2 | [Sort the People](https://leetcode.com/problems/sort-the-people/) | LC |
| 3 | [Relative Sort Array](https://leetcode.com/problems/relative-sort-array/) | LC |
| 4 | [Rank List](https://codeforces.com/problemset/problem/166/A) | CF |

[↑ Back to TOC](#toc)

---

<a id="form-3"></a>
## Form 3 — Lower Bound / Upper Bound

**Recognition:** Sorted data + first/last valid position, predecessor/successor, or count.

**Problems: 5**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Search Insert Position](https://leetcode.com/problems/search-insert-position/) | LC |
| 2 | [Find First and Last Position](https://leetcode.com/problems/find-first-and-last-position-of-element-in-sorted-array/) | LC |
| 3 | [Interesting Drink](https://codeforces.com/problemset/problem/706/B) | CF |
| 4 | [Fast Search](https://codeforces.com/edu/course/2/lesson/6/1/practice/contest/283911/problem/D) | CF |
| 5 | [Towers](https://cses.fi/problemset/task/1073/) | CSES |

[↑ Back to TOC](#toc)

---

<a id="form-4"></a>
## Form 4 — Next Permutation / Arrangement Generation

**Recognition:** Need next lexicographic arrangement or enumerate/order permutations.

**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Next Permutation](https://leetcode.com/problems/next-permutation/) | LC |
| 2 | [Permutations](https://leetcode.com/problems/permutations/) | LC |
| 3 | [Creating Strings](https://cses.fi/problemset/task/1622/) | CSES |
| 4 | [Permutation Sequence](https://leetcode.com/problems/permutation-sequence/) | LC |

[↑ Back to TOC](#toc)

---

<a id="form-5"></a>
## Form 5 — Frequency Counting / Hash Map

**Recognition:** Need value→count/position/state or grouping by a key.

**Problems: 5**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Two Sum](https://leetcode.com/problems/two-sum/) | LC |
| 2 | [Valid Anagram](https://leetcode.com/problems/valid-anagram/) | LC |
| 3 | [Group Anagrams](https://leetcode.com/problems/group-anagrams/) | LC |
| 4 | [Registration System](https://codeforces.com/problemset/problem/4/C) | CF |
| 5 | [Good Subarrays](https://codeforces.com/problemset/problem/1398/C) | CF |

[↑ Back to TOC](#toc)

---

<a id="form-6"></a>
## Form 6 — Set / Membership / Distinct Values

**Recognition:** Need uniqueness, fast membership, or distinct-value processing.

**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Contains Duplicate](https://leetcode.com/problems/contains-duplicate/) | LC |
| 2 | [Intersection of Two Arrays](https://leetcode.com/problems/intersection-of-two-arrays/) | LC |
| 3 | [Longest Consecutive Sequence](https://leetcode.com/problems/longest-consecutive-sequence/) | LC |
| 4 | [Distinct Numbers](https://cses.fi/problemset/task/1621/) | CSES |

[↑ Back to TOC](#toc)

---

<a id="form-7"></a>
## Form 7 — Ordered Set / Predecessor / Successor

**Recognition:** Need sorted unique keys plus nearest smaller/larger lookup.

**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Traffic Lights](https://cses.fi/problemset/task/1163/) | CSES |
| 2 | [Contains Duplicate III](https://leetcode.com/problems/contains-duplicate-iii/) | LC |
| 3 | [My Calendar I](https://leetcode.com/problems/my-calendar-i/) | LC |
| 4 | [Room Allocation](https://cses.fi/problemset/task/1164/) | CSES |

[↑ Back to TOC](#toc)

---

<a id="form-8"></a>
## Form 8 — Multiset / Dynamic Ordered Collection

**Recognition:** Need sorted duplicates with insertion, deletion, min/max or median-like access.

**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Concert Tickets](https://cses.fi/problemset/task/1091/) | CSES |
| 2 | [Sliding Window Median](https://cses.fi/problemset/task/1076/) | CSES |
| 3 | [Sliding Window Cost](https://cses.fi/problemset/task/1077/) | CSES |
| 4 | [Multiset](https://codeforces.com/problemset/problem/1354/D) | CF |

[↑ Back to TOC](#toc)

---

<a id="form-9"></a>
## Form 9 — Stack: Matching / Valid Parentheses

**Recognition:** Latest unresolved symbol determines matching/nesting validity.

**Problems: 5**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Valid Parentheses](https://leetcode.com/problems/valid-parentheses/) | LC |
| 2 | [Minimum Add to Make Parentheses Valid](https://leetcode.com/problems/minimum-add-to-make-parentheses-valid/) | LC |
| 3 | [Remove Outermost Parentheses](https://leetcode.com/problems/remove-outermost-parentheses/) | LC |
| 4 | [Minimum Remove to Make Valid Parentheses](https://leetcode.com/problems/minimum-remove-to-make-valid-parentheses/) | LC |
| 5 | [Longest Valid Parentheses](https://leetcode.com/problems/longest-valid-parentheses/) | LC |

[↑ Back to TOC](#toc)

---

<a id="form-10"></a>
## Form 10 — Stack: Cancellation / Remove Elements

**Recognition:** Current item may repeatedly cancel/interact with the stack top.

**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Remove All Adjacent Duplicates In String](https://leetcode.com/problems/remove-all-adjacent-duplicates-in-string/) | LC |
| 2 | [Backspace String Compare](https://leetcode.com/problems/backspace-string-compare/) | LC |
| 3 | [Asteroid Collision](https://leetcode.com/problems/asteroid-collision/) | LC |
| 4 | [Make The String Great](https://leetcode.com/problems/make-the-string-great/) | LC |

[↑ Back to TOC](#toc)

---

<a id="form-11"></a>
## Form 11 — Monotonic Stack: Next Greater / Smaller

**Recognition:** Nearest previous/next <, ≤, >, ≥ or boundary of influence.

**Problems: 5**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Next Greater Element I](https://leetcode.com/problems/next-greater-element-i/) | LC |
| 2 | [Daily Temperatures](https://leetcode.com/problems/daily-temperatures/) | LC |
| 3 | [Next Greater Element II](https://leetcode.com/problems/next-greater-element-ii/) | LC |
| 4 | [Nearest Smaller Values](https://cses.fi/problemset/task/1645/) | CSES |
| 5 | [Stock Span Problem](https://www.geeksforgeeks.org/problems/stock-span-problem-1587115621/1) | GFG |

[↑ Back to TOC](#toc)

---

<a id="form-12"></a>
## Form 12 — Monotonic Stack: Greedy Removal

**Recognition:** Pop previous choices while the current value produces a better feasible sequence.

**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Remove K Digits](https://leetcode.com/problems/remove-k-digits/) | LC |
| 2 | [Remove Duplicate Letters](https://leetcode.com/problems/remove-duplicate-letters/) | LC |
| 3 | [Smallest Subsequence of Distinct Characters](https://leetcode.com/problems/smallest-subsequence-of-distinct-characters/) | LC |
| 4 | [132 Pattern](https://leetcode.com/problems/132-pattern/) | LC |

[↑ Back to TOC](#toc)

---

<a id="form-13"></a>
## Form 13 — Monotonic Stack + Contribution

**Recognition:** Monotonic boundaries determine how many ranges use an element as min/max/pivot.

**Problems: 5**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Sum of Subarray Minimums](https://leetcode.com/problems/sum-of-subarray-minimums/) | LC |
| 2 | [Sum of Subarray Ranges](https://leetcode.com/problems/sum-of-subarray-ranges/) | LC |
| 3 | [Largest Rectangle in Histogram](https://leetcode.com/problems/largest-rectangle-in-histogram/) | LC |
| 4 | [Maximum Subarray Min-Product](https://leetcode.com/problems/maximum-subarray-min-product/) | LC |
| 5 | [Trapping Rain Water](https://leetcode.com/problems/trapping-rain-water/) | LC |

[↑ Back to TOC](#toc)

---

<a id="form-14"></a>
## Form 14 — Queue / FIFO Processing

**Recognition:** Process or simulate items in arrival order.

**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Number of Recent Calls](https://leetcode.com/problems/number-of-recent-calls/) | LC |
| 2 | [Time Needed to Buy Tickets](https://leetcode.com/problems/time-needed-to-buy-tickets/) | LC |
| 3 | [Dota2 Senate](https://leetcode.com/problems/dota2-senate/) | LC |
| 4 | [Reveal Cards In Increasing Order](https://leetcode.com/problems/reveal-cards-in-increasing-order/) | LC |

[↑ Back to TOC](#toc)

---

<a id="form-15"></a>
## Form 15 — Monotonic Deque / Sliding Window Min-Max

**Recognition:** Maintain useful candidates in a moving window; discard dominated elements.

**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Sliding Window Maximum](https://leetcode.com/problems/sliding-window-maximum/) | LC |
| 2 | [Longest Continuous Subarray With Absolute Diff ≤ Limit](https://leetcode.com/problems/longest-continuous-subarray-with-absolute-difference-less-than-or-equal-to-limit/) | LC |
| 3 | [Shortest Subarray with Sum at Least K](https://leetcode.com/problems/shortest-subarray-with-sum-at-least-k/) | LC |
| 4 | [Constrained Subsequence Sum](https://leetcode.com/problems/constrained-subsequence-sum/) | LC |

[↑ Back to TOC](#toc)

---

<a id="form-16"></a>
## Form 16 — Priority Queue / Repeated Best Choice

**Recognition:** Repeatedly extract the current min/max/best candidate.

**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Last Stone Weight](https://leetcode.com/problems/last-stone-weight/) | LC |
| 2 | [Task Scheduler](https://leetcode.com/problems/task-scheduler/) | LC |
| 3 | [Jesse and Cookies](https://www.hackerrank.com/challenges/jesse-and-cookies/problem) | HR |
| 4 | [Minimum Cost of Ropes](https://www.geeksforgeeks.org/problems/minimum-cost-of-ropes-1587115620/1) | GFG |

[↑ Back to TOC](#toc)

---

<a id="form-17"></a>
## Form 17 — Top K Elements

**Recognition:** Keep only the K best elements/candidates instead of sorting everything.

**Problems: 5**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Kth Largest Element in an Array](https://leetcode.com/problems/kth-largest-element-in-an-array/) | LC |
| 2 | [Top K Frequent Elements](https://leetcode.com/problems/top-k-frequent-elements/) | LC |
| 3 | [K Closest Points to Origin](https://leetcode.com/problems/k-closest-points-to-origin/) | LC |
| 4 | [Kth Largest Element in a Stream](https://leetcode.com/problems/kth-largest-element-in-a-stream/) | LC |
| 5 | [Find K Pairs with Smallest Sums](https://leetcode.com/problems/find-k-pairs-with-smallest-sums/) | LC |

[↑ Back to TOC](#toc)

---

<a id="form-18"></a>
## Form 18 — Two Heaps / Running Median

**Recognition:** Maintain lower and upper halves around a dynamic median.

**Problems: 3**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Find Median from Data Stream](https://leetcode.com/problems/find-median-from-data-stream/) | LC |
| 2 | [Sliding Window Median](https://leetcode.com/problems/sliding-window-median/) | LC |
| 3 | [IPO](https://leetcode.com/problems/ipo/) | LC |

[↑ Back to TOC](#toc)

---

<a id="form-19"></a>
## Form 19 — Atomic / Element Contribution

**Recognition:** Fix one element and count how many generated objects contain/use it.

**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Sum of All Subarray Sums](https://www.geeksforgeeks.org/sum-of-all-subarrays/) | GFG |
| 2 | [Sum of All Subsequences of an Array](https://www.geeksforgeeks.org/sum-of-all-subsequences-of-an-array/) | GFG |
| 3 | [Sum of All Odd Length Subarrays](https://leetcode.com/problems/sum-of-all-odd-length-subarrays/) | LC |
| 4 | [Total Appeal of A String](https://leetcode.com/problems/total-appeal-of-a-string/) | LC |

[↑ Back to TOC](#toc)

---

<a id="form-20"></a>
## Form 20 — Pivot / Left × Right Contribution

**Recognition:** Fix a pivot/occurrence and multiply valid choices on its left and right.

**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Count Unique Characters of All Substrings](https://leetcode.com/problems/count-unique-characters-of-all-substrings-of-a-given-string/) | LC |
| 2 | [Number of Subarrays With Bounded Maximum](https://leetcode.com/problems/number-of-subarrays-with-bounded-maximum/) | LC |
| 3 | [Count Number of Nice Subarrays](https://leetcode.com/problems/count-number-of-nice-subarrays/) | LC |
| 4 | [Subarray Product Less Than K](https://leetcode.com/problems/subarray-product-less-than-k/) | LC |

[↑ Back to TOC](#toc)

---

<a id="form-21"></a>
## Form 21 — Pair Contribution

**Recognition:** Aggregate over many pairs by sorting/algebra instead of enumerating O(n²) pairs.

**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Sum of Absolute Differences in a Sorted Array](https://leetcode.com/problems/sum-of-absolute-differences-in-a-sorted-array/) | LC |
| 2 | [Minimum Absolute Difference](https://leetcode.com/problems/minimum-absolute-difference/) | LC |
| 3 | [Minimum Cost to Make Array Equal](https://leetcode.com/problems/minimum-cost-to-make-array-equal/) | LC |
| 4 | [Manhattan Distances](https://cses.fi/problemset/task/3411/) | CSES |

[↑ Back to TOC](#toc)

---

<a id="form-22"></a>
## Form 22 — Bit Contribution

**Recognition:** Treat each bit independently and count zeros/ones or pair contributions.

**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Total Hamming Distance](https://leetcode.com/problems/total-hamming-distance/) | LC |
| 2 | [Sum of All Subset XOR Totals](https://leetcode.com/problems/sum-of-all-subset-xor-totals/) | LC |
| 3 | [XOR Beauty of Array](https://leetcode.com/problems/find-xor-beauty-of-array/) | LC |
| 4 | [Bitwise AND of Numbers Range](https://leetcode.com/problems/bitwise-and-of-numbers-range/) | LC |

[↑ Back to TOC](#toc)

---

<a id="form-23"></a>
## Form 23 — Stream Mean / Variance / Running Statistics

**Recognition:** Maintain statistics incrementally instead of recomputing over the whole stream.

**Problems: 3**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Moving Average from Data Stream](https://leetcode.com/problems/moving-average-from-data-stream/) | LC |
| 2 | [MKAverage](https://leetcode.com/problems/finding-mk-average/) | LC |
| 3 | [Stock Price Fluctuation](https://leetcode.com/problems/stock-price-fluctuation/) | LC |

[↑ Back to TOC](#toc)

---

<a id="form-24"></a>
## Form 24 — Min Stack / Maintain Aggregate State

**Recognition:** Each push/pop also maintains min/max/other aggregate state.

**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Min Stack](https://leetcode.com/problems/min-stack/) | LC |
| 2 | [Max Stack](https://leetcode.com/problems/max-stack/) | LC |
| 3 | [Maximum Frequency Stack](https://leetcode.com/problems/maximum-frequency-stack/) | LC |
| 4 | [All O`one Data Structure](https://leetcode.com/problems/all-oone-data-structure/) | LC |

[↑ Back to TOC](#toc)

---

<a id="form-25"></a>
## Form 25 — Stack / Queue Transformation

**Recognition:** Implement one access discipline using another or balance multiple queues/deques.

**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Implement Queue using Stacks](https://leetcode.com/problems/implement-queue-using-stacks/) | LC |
| 2 | [Implement Stack using Queues](https://leetcode.com/problems/implement-stack-using-queues/) | LC |
| 3 | [Design Circular Queue](https://leetcode.com/problems/design-circular-queue/) | LC |
| 4 | [Design Front Middle Back Queue](https://leetcode.com/problems/design-front-middle-back-queue/) | LC |

[↑ Back to TOC](#toc)

---

<a id="form-26"></a>
## Form 26 — Lazy Stack Increment

**Recognition:** Delay/batch updates so a range-like stack operation stays efficient.

**Problems: 3**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Range Addition](https://leetcode.com/problems/range-addition/) | LC |
| 2 | [Fancy Sequence](https://leetcode.com/problems/fancy-sequence/) | LC |
| 3 | [Corporate Flight Bookings](https://leetcode.com/problems/corporate-flight-bookings/) | LC |

[↑ Back to TOC](#toc)

---

<a id="form-27"></a>
## Form 27 — Product of Last K / Prefix State

**Recognition:** Maintain prefix-like state so recent aggregate queries are O(1) or logarithmic.

**Problems: 3**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Product of the Last K Numbers](https://leetcode.com/problems/product-of-the-last-k-numbers/) | LC |
| 2 | [Range Product Queries of Powers](https://leetcode.com/problems/range-product-queries-of-powers/) | LC |
| 3 | [Range Sum Query - Immutable](https://leetcode.com/problems/range-sum-query-immutable/) | LC |

[↑ Back to TOC](#toc)

---

<a id="form-28"></a>
## Form 28 — Snapshot / Versioned Data

**Recognition:** Store only changes and query historical state by version/time.

**Problems: 3**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Snapshot Array](https://leetcode.com/problems/snapshot-array/) | LC |
| 2 | [Time Based Key-Value Store](https://leetcode.com/problems/time-based-key-value-store/) | LC |
| 3 | [Design Underground System](https://leetcode.com/problems/design-underground-system/) | LC |

[↑ Back to TOC](#toc)

---

<a id="form-29"></a>
## Form 29 — LRU Cache

**Recognition:** O(1) lookup plus recency ordering and eviction.

**Problems: 3**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [LRU Cache](https://leetcode.com/problems/lru-cache/) | LC |
| 2 | [Design Browser History](https://leetcode.com/problems/design-browser-history/) | LC |
| 3 | [Design Linked List](https://leetcode.com/problems/design-linked-list/) | LC |

[↑ Back to TOC](#toc)

---

<a id="form-30"></a>
## Form 30 — LFU / Frequency-Based Cache

**Recognition:** Track frequency plus recency/tie-breaking efficiently.

**Problems: 3**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [LFU Cache](https://leetcode.com/problems/lfu-cache/) | LC |
| 2 | [Design Twitter](https://leetcode.com/problems/design-twitter/) | LC |
| 3 | [Food Ratings](https://leetcode.com/problems/design-a-food-rating-system/) | LC |

[↑ Back to TOC](#toc)

---

<a id="form-31"></a>
## Form 31 — Randomized Data Structure

**Recognition:** Combine arrays/maps or probabilistic sampling for efficient random operations.

**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Insert Delete GetRandom O(1)](https://leetcode.com/problems/insert-delete-getrandom-o1/) | LC |
| 2 | [Random Pick with Weight](https://leetcode.com/problems/random-pick-with-weight/) | LC |
| 3 | [Shuffle an Array](https://leetcode.com/problems/shuffle-an-array/) | LC |
| 4 | [Linked List Random Node](https://leetcode.com/problems/linked-list-random-node/) | LC |

[↑ Back to TOC](#toc)

---

## Practice Rule

Use each form in this order: **learn → derive → solve independently → hidden variation → mixed practice → contest**.

> **Mastery test:** You can recognize an unlabeled variation, explain why the form applies, and implement it without opening the notes.
