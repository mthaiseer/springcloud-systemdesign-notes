# STL Pattern Index — Deep Practice Edition

> Compact **form-wise STL mastery index** for Competitive Programming, Codeforces and FAANG-style interviews.
> Each form contains **3–5 benchmark problems**. Exact benchmark problems are not repeated across forms.
## Table of Contents

1. [Form 1 — Sorting / Basic Ordering](#form-1-sorting-basic-ordering)
2. [Form 2 — Custom Comparator / Multi-Key Sorting](#form-2-custom-comparator-multi-key-sorting)
3. [Form 3 — Lower Bound / Upper Bound](#form-3-lower-bound-upper-bound)
4. [Form 4 — Next Permutation / Arrangement Generation](#form-4-next-permutation-arrangement-generation)
5. [Form 5 — Frequency Counting / Hash Map](#form-5-frequency-counting-hash-map)
6. [Form 6 — Set / Membership / Distinct Values](#form-6-set-membership-distinct-values)
7. [Form 7 — Ordered Set / Predecessor / Successor](#form-7-ordered-set-predecessor-successor)
8. [Form 8 — Multiset / Dynamic Ordered Collection](#form-8-multiset-dynamic-ordered-collection)
9. [Form 9 — Stack: Matching / Valid Parentheses](#form-9-stack-matching-valid-parentheses)
10. [Form 10 — Stack: Cancellation / Remove Elements](#form-10-stack-cancellation-remove-elements)
11. [Form 11 — Monotonic Stack: Next Greater / Smaller](#form-11-monotonic-stack-next-greater-smaller)
12. [Form 12 — Monotonic Stack: Greedy Removal](#form-12-monotonic-stack-greedy-removal)
13. [Form 13 — Monotonic Stack + Contribution](#form-13-monotonic-stack-contribution)
14. [Form 14 — Queue / FIFO Processing](#form-14-queue-fifo-processing)
15. [Form 15 — Monotonic Deque / Sliding Window Min-Max](#form-15-monotonic-deque-sliding-window-min-max)
16. [Form 16 — Priority Queue / Repeated Best Choice](#form-16-priority-queue-repeated-best-choice)
17. [Form 17 — Top K Elements](#form-17-top-k-elements)
18. [Form 18 — Two Heaps / Running Median](#form-18-two-heaps-running-median)
19. [Form 19 — Atomic / Element Contribution](#form-19-atomic-element-contribution)
20. [Form 20 — Pivot / Left × Right Contribution](#form-20-pivot-left-right-contribution)
21. [Form 21 — Pair Contribution](#form-21-pair-contribution)
22. [Form 22 — Bit Contribution](#form-22-bit-contribution)
23. [Form 23 — Stream Mean / Variance / Running Statistics](#form-23-stream-mean-variance-running-statistics)
24. [Form 24 — Min Stack / Maintain Aggregate State](#form-24-min-stack-maintain-aggregate-state)
25. [Form 25 — Stack / Queue Transformation](#form-25-stack-queue-transformation)
26. [Form 26 — Lazy Stack Increment](#form-26-lazy-stack-increment)
27. [Form 27 — Product of Last K / Prefix State](#form-27-product-of-last-k-prefix-state)
28. [Form 28 — Snapshot / Versioned Data](#form-28-snapshot-versioned-data)
29. [Form 29 — LRU Cache](#form-29-lru-cache)
30. [Form 30 — LFU / Frequency-Based Cache](#form-30-lfu-frequency-based-cache)
31. [Form 31 — Randomized Data Structure](#form-31-randomized-data-structure)

---
## Form 1 — Sorting / Basic Ordering

**Recognition:** Ordering exposes structure or enables greedy/two-pointer processing.

**Prerequisites — know these first:**
- Arrays / vectors and basic indexing.
- Ascending vs descending order.
- Basic time complexity: `O(N)`, `O(N log N)`.
- Why sorting can reveal structure for greedy or two-pointer processing.


**Problems: 5**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Sort an Array](https://leetcode.com/problems/sort-an-array/) | LC |
| 2 | [Helpful Maths](https://codeforces.com/problemset/problem/339/A) | CF |
| 3 | [Dragons](https://codeforces.com/problemset/problem/230/A) | CF |
| 4 | [Business trip](https://codeforces.com/problemset/problem/149/A) | CF |
| 5 | [Apartments](https://cses.fi/problemset/task/1084/) | CSES |



[↑ Back to TOC](#table-of-contents)

---
## Form 2 — Custom Comparator / Multi-Key Sorting

**Recognition:** Sort by multiple keys or by a non-standard ordering relation.

**Prerequisites — know these first:**
- Form 1: basic sorting.
- Pairs / tuples and primary vs secondary keys.
- Tie-breaking rules.
- Strict ordering idea: if two items are equal under all keys, neither should come before the other.


**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Largest Number](https://leetcode.com/problems/largest-number/) | LC |
| 2 | [Sort the People](https://leetcode.com/problems/sort-the-people/) | LC |
| 3 | [Relative Sort Array](https://leetcode.com/problems/relative-sort-array/) | LC |
| 4 | [Rank List](https://codeforces.com/problemset/problem/166/A) | CF |



[↑ Back to TOC](#table-of-contents)

---
## Form 3 — Lower Bound / Upper Bound

**Recognition:** Sorted data + first/last valid position, predecessor/successor, or count.

**Prerequisites — know these first:**
- Sorted arrays / vectors.
- Standard binary-search idea.
- Difference between first valid position and last valid position.
- Predecessor / successor intuition.
- Counting values in a sorted range.


**Problems: 5**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Search Insert Position](https://leetcode.com/problems/search-insert-position/) | LC |
| 2 | [Find First and Last Position](https://leetcode.com/problems/find-first-and-last-position-of-element-in-sorted-array/) | LC |
| 3 | [Interesting Drink](https://codeforces.com/problemset/problem/706/B) | CF |
| 4 | [Fast Search](https://codeforces.com/edu/course/2/lesson/6/1/practice/contest/283911/problem/D) | CF |
| 5 | [Towers](https://cses.fi/problemset/task/1073/) | CSES |



[↑ Back to TOC](#table-of-contents)

---
## Form 4 — Next Permutation / Arrangement Generation

**Recognition:** Need next lexicographic arrangement or enumerate/order permutations.

**Prerequisites — know these first:**
- Arrays / strings.
- Lexicographic order.
- Permutation meaning.
- Basic factorial / number of arrangements intuition.
- Ability to reason about the longest suffix that is already in decreasing order.


**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Next Permutation](https://leetcode.com/problems/next-permutation/) | LC |
| 2 | [Permutations](https://leetcode.com/problems/permutations/) | LC |
| 3 | [Creating Strings](https://cses.fi/problemset/task/1622/) | CSES |
| 4 | [Permutation Sequence](https://leetcode.com/problems/permutation-sequence/) | LC |



[↑ Back to TOC](#table-of-contents)

---
## Form 5 — Frequency Counting / Hash Map

**Recognition:** Need value→count/position/state or grouping by a key.

**Prerequisites — know these first:**
- Arrays / strings.
- Key → value mapping.
- Frequency counting.
- Hashing idea and average `O(1)` lookup.
- Prefix-sum basics are useful for problems such as transformed-frequency subarray counting.


**Problems: 5**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Two Sum](https://leetcode.com/problems/two-sum/) | LC |
| 2 | [Valid Anagram](https://leetcode.com/problems/valid-anagram/) | LC |
| 3 | [Group Anagrams](https://leetcode.com/problems/group-anagrams/) | LC |
| 4 | [Registration System](https://codeforces.com/problemset/problem/4/C) | CF |
| 5 | [Good Subarrays](https://codeforces.com/problemset/problem/1398/C) | CF |



[↑ Back to TOC](#table-of-contents)

---
## Form 6 — Set / Membership / Distinct Values

**Recognition:** Need uniqueness, fast membership, or distinct-value processing.

**Prerequisites — know these first:**
- Hash map / membership lookup.
- Difference between duplicates and distinct values.
- Set semantics: one copy per value.
- Basic union / intersection ideas.


**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Contains Duplicate](https://leetcode.com/problems/contains-duplicate/) | LC |
| 2 | [Intersection of Two Arrays](https://leetcode.com/problems/intersection-of-two-arrays/) | LC |
| 3 | [Longest Consecutive Sequence](https://leetcode.com/problems/longest-consecutive-sequence/) | LC |
| 4 | [Distinct Numbers](https://cses.fi/problemset/task/1621/) | CSES |



[↑ Back to TOC](#table-of-contents)

---
## Form 7 — Ordered Set / Predecessor / Successor

**Recognition:** Need sorted unique keys plus nearest smaller/larger lookup.

**Prerequisites — know these first:**
- Form 6: set / uniqueness.
- Sorted order.
- Lower bound / upper bound.
- Predecessor = nearest smaller value.
- Successor = nearest larger value.


**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Traffic Lights](https://cses.fi/problemset/task/1163/) | CSES |
| 2 | [Contains Duplicate III](https://leetcode.com/problems/contains-duplicate-iii/) | LC |
| 3 | [My Calendar I](https://leetcode.com/problems/my-calendar-i/) | LC |
| 4 | [Room Allocation](https://cses.fi/problemset/task/1164/) | CSES |



[↑ Back to TOC](#table-of-contents)

---
## Form 8 — Multiset / Dynamic Ordered Collection

**Recognition:** Need sorted duplicates with insertion, deletion, min/max or median-like access.

**Prerequisites — know these first:**
- Ordered set.
- Duplicates and frequency.
- Iterator-based deletion of exactly one occurrence.
- Accessing minimum / maximum in ordered data.
- Basic balancing intuition for median-style problems.


**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Concert Tickets](https://cses.fi/problemset/task/1091/) | CSES |
| 2 | [Sliding Window Median](https://cses.fi/problemset/task/1076/) | CSES |
| 3 | [Sliding Window Cost](https://cses.fi/problemset/task/1077/) | CSES |
| 4 | [Multiset](https://codeforces.com/problemset/problem/1354/D) | CF |



[↑ Back to TOC](#table-of-contents)

---
## Form 9 — Stack: Matching / Valid Parentheses

**Recognition:** Latest unresolved symbol determines matching/nesting validity.

**Prerequisites — know these first:**
- Stack = LIFO.
- Push / pop / top.
- Matching pairs such as `()`, `{}`, `[]`.
- Nested structure intuition.
- “Latest unresolved item” pattern.


**Problems: 5**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Valid Parentheses](https://leetcode.com/problems/valid-parentheses/) | LC |
| 2 | [Minimum Add to Make Parentheses Valid](https://leetcode.com/problems/minimum-add-to-make-parentheses-valid/) | LC |
| 3 | [Remove Outermost Parentheses](https://leetcode.com/problems/remove-outermost-parentheses/) | LC |
| 4 | [Minimum Remove to Make Valid Parentheses](https://leetcode.com/problems/minimum-remove-to-make-valid-parentheses/) | LC |
| 5 | [Longest Valid Parentheses](https://leetcode.com/problems/longest-valid-parentheses/) | LC |



[↑ Back to TOC](#table-of-contents)

---
## Form 10 — Stack: Cancellation / Remove Elements

**Recognition:** Current item may repeatedly cancel/interact with the stack top.

**Prerequisites — know these first:**
- Form 9: basic stack operations.
- While-loop interaction with stack top.
- Simulation.
- Understanding that one incoming item may repeatedly remove previous items.


**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Remove All Adjacent Duplicates In String](https://leetcode.com/problems/remove-all-adjacent-duplicates-in-string/) | LC |
| 2 | [Backspace String Compare](https://leetcode.com/problems/backspace-string-compare/) | LC |
| 3 | [Asteroid Collision](https://leetcode.com/problems/asteroid-collision/) | LC |
| 4 | [Make The String Great](https://leetcode.com/problems/make-the-string-great/) | LC |



[↑ Back to TOC](#table-of-contents)

---
## Form 11 — Monotonic Stack: Next Greater / Smaller

**Recognition:** Nearest previous/next <, ≤, >, ≥ or boundary of influence.

**Prerequisites — know these first:**
- Basic stack.
- Previous / next element questions.
- Strict vs non-strict comparisons: `<`, `<=`, `>`, `>=`.
- Index-based distance.
- Idea of removing elements that can never be useful again.


**Problems: 5**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Next Greater Element I](https://leetcode.com/problems/next-greater-element-i/) | LC |
| 2 | [Daily Temperatures](https://leetcode.com/problems/daily-temperatures/) | LC |
| 3 | [Next Greater Element II](https://leetcode.com/problems/next-greater-element-ii/) | LC |
| 4 | [Nearest Smaller Values](https://cses.fi/problemset/task/1645/) | CSES |
| 5 | [Stock Span Problem](https://www.geeksforgeeks.org/problems/stock-span-problem-1587115621/1) | GFG |



[↑ Back to TOC](#table-of-contents)

---
## Form 12 — Monotonic Stack: Greedy Removal

**Recognition:** Pop previous choices while the current value produces a better feasible sequence.

**Prerequisites — know these first:**
- Monotonic stack basics.
- Greedy choice intuition.
- Lexicographic ordering.
- Ability to ask: “Can the current value make an earlier choice permanently worse?”
- Remaining-elements / feasibility reasoning.


**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Remove K Digits](https://leetcode.com/problems/remove-k-digits/) | LC |
| 2 | [Remove Duplicate Letters](https://leetcode.com/problems/remove-duplicate-letters/) | LC |
| 3 | [Smallest Subsequence of Distinct Characters](https://leetcode.com/problems/smallest-subsequence-of-distinct-characters/) | LC |
| 4 | [132 Pattern](https://leetcode.com/problems/132-pattern/) | LC |



[↑ Back to TOC](#table-of-contents)

---
## Form 13 — Monotonic Stack + Contribution

**Recognition:** Monotonic boundaries determine how many ranges use an element as min/max/pivot.

**Prerequisites — know these first:**
- Monotonic stack.
- Previous / next smaller or greater boundaries.
- Contribution technique.
- Counting subarrays containing a fixed index.
- Left choices × right choices.


**Problems: 5**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Sum of Subarray Minimums](https://leetcode.com/problems/sum-of-subarray-minimums/) | LC |
| 2 | [Sum of Subarray Ranges](https://leetcode.com/problems/sum-of-subarray-ranges/) | LC |
| 3 | [Largest Rectangle in Histogram](https://leetcode.com/problems/largest-rectangle-in-histogram/) | LC |
| 4 | [Maximum Subarray Min-Product](https://leetcode.com/problems/maximum-subarray-min-product/) | LC |
| 5 | [Trapping Rain Water](https://leetcode.com/problems/trapping-rain-water/) | LC |



[↑ Back to TOC](#table-of-contents)

---
## Form 14 — Queue / FIFO Processing

**Recognition:** Process or simulate items in arrival order.

**Prerequisites — know these first:**
- Queue = FIFO.
- Push / pop / front.
- Simulation in arrival order.
- Basic circular-processing intuition.


**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Number of Recent Calls](https://leetcode.com/problems/number-of-recent-calls/) | LC |
| 2 | [Time Needed to Buy Tickets](https://leetcode.com/problems/time-needed-to-buy-tickets/) | LC |
| 3 | [Dota2 Senate](https://leetcode.com/problems/dota2-senate/) | LC |
| 4 | [Reveal Cards In Increasing Order](https://leetcode.com/problems/reveal-cards-in-increasing-order/) | LC |



[↑ Back to TOC](#table-of-contents)

---
## Form 15 — Monotonic Deque / Sliding Window Min-Max

**Recognition:** Maintain useful candidates in a moving window; discard dominated elements.

**Prerequisites — know these first:**
- Queue / deque basics.
- Sliding window.
- Window expiry by index.
- Monotonic stack intuition.
- Dominated candidate idea: remove values that can never become the future min/max.


**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Sliding Window Maximum](https://leetcode.com/problems/sliding-window-maximum/) | LC |
| 2 | [Longest Continuous Subarray With Absolute Diff ≤ Limit](https://leetcode.com/problems/longest-continuous-subarray-with-absolute-difference-less-than-or-equal-to-limit/) | LC |
| 3 | [Shortest Subarray with Sum at Least K](https://leetcode.com/problems/shortest-subarray-with-sum-at-least-k/) | LC |
| 4 | [Constrained Subsequence Sum](https://leetcode.com/problems/constrained-subsequence-sum/) | LC |



[↑ Back to TOC](#table-of-contents)

---
## Form 16 — Priority Queue / Repeated Best Choice

**Recognition:** Repeatedly extract the current min/max/best candidate.

**Prerequisites — know these first:**
- Heap / priority queue idea.
- Min-heap vs max-heap.
- Repeatedly choosing the current best candidate.
- Greedy basics.
- Difference between sorting once and maintaining the best dynamically.


**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Last Stone Weight](https://leetcode.com/problems/last-stone-weight/) | LC |
| 2 | [Task Scheduler](https://leetcode.com/problems/task-scheduler/) | LC |
| 3 | [Jesse and Cookies](https://www.hackerrank.com/challenges/jesse-and-cookies/problem) | HR |
| 4 | [Minimum Cost of Ropes](https://www.geeksforgeeks.org/problems/minimum-cost-of-ropes-1587115620/1) | GFG |



[↑ Back to TOC](#table-of-contents)

---
## Form 17 — Top K Elements

**Recognition:** Keep only the K best elements/candidates instead of sorting everything.

**Prerequisites — know these first:**
- Priority queue / heap.
- Kth largest / smallest meaning.
- Maintaining only `K` useful candidates.
- Min-heap for Top-K largest and max-heap for Top-K smallest.
- Frequency counting for Top-K frequency problems.


**Problems: 5**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Kth Largest Element in an Array](https://leetcode.com/problems/kth-largest-element-in-an-array/) | LC |
| 2 | [Top K Frequent Elements](https://leetcode.com/problems/top-k-frequent-elements/) | LC |
| 3 | [K Closest Points to Origin](https://leetcode.com/problems/k-closest-points-to-origin/) | LC |
| 4 | [Kth Largest Element in a Stream](https://leetcode.com/problems/kth-largest-element-in-a-stream/) | LC |
| 5 | [Find K Pairs with Smallest Sums](https://leetcode.com/problems/find-k-pairs-with-smallest-sums/) | LC |



[↑ Back to TOC](#table-of-contents)

---
## Form 18 — Two Heaps / Running Median

**Recognition:** Maintain lower and upper halves around a dynamic median.

**Prerequisites — know these first:**
- Priority queue.
- Max-heap and min-heap.
- Median definition for odd/even sizes.
- Balancing two groups around a boundary.
- Invariant thinking: lower half ≤ upper half.


**Problems: 3**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Find Median from Data Stream](https://leetcode.com/problems/find-median-from-data-stream/) | LC |
| 2 | [Sliding Window Median](https://leetcode.com/problems/sliding-window-median/) | LC |
| 3 | [IPO](https://leetcode.com/problems/ipo/) | LC |



[↑ Back to TOC](#table-of-contents)

---
## Form 19 — Atomic / Element Contribution

**Recognition:** Fix one element and count how many generated objects contain/use it.

**Prerequisites — know these first:**
- Subarrays / subsequences.
- Basic combinatorics / counting choices.
- Fix-one-element thinking.
- Ask: “How many generated objects contain this element?”
- Multiplication principle.


**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Sum of All Subarray Sums](https://www.geeksforgeeks.org/sum-of-all-subarrays/) | GFG |
| 2 | [Sum of All Subsequences of an Array](https://www.geeksforgeeks.org/sum-of-all-subsequences-of-an-array/) | GFG |
| 3 | [Sum of All Odd Length Subarrays](https://leetcode.com/problems/sum-of-all-odd-length-subarrays/) | LC |
| 4 | [Total Appeal of A String](https://leetcode.com/problems/total-appeal-of-a-string/) | LC |



[↑ Back to TOC](#table-of-contents)

---
## Form 20 — Pivot / Left × Right Contribution

**Recognition:** Fix a pivot/occurrence and multiply valid choices on its left and right.

**Prerequisites — know these first:**
- Form 19: element contribution.
- Previous / next occurrence or boundary.
- Counting valid choices on the left and right.
- Multiplication principle: `leftChoices × rightChoices`.
- Pivot-based counting.


**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Count Unique Characters of All Substrings](https://leetcode.com/problems/count-unique-characters-of-all-substrings-of-a-given-string/) | LC |
| 2 | [Number of Subarrays With Bounded Maximum](https://leetcode.com/problems/number-of-subarrays-with-bounded-maximum/) | LC |
| 3 | [Count Number of Nice Subarrays](https://leetcode.com/problems/count-number-of-nice-subarrays/) | LC |
| 4 | [Subarray Product Less Than K](https://leetcode.com/problems/subarray-product-less-than-k/) | LC |



[↑ Back to TOC](#table-of-contents)

---
## Form 21 — Pair Contribution

**Recognition:** Aggregate over many pairs by sorting/algebra instead of enumerating O(n²) pairs.

**Prerequisites — know these first:**
- Sorting.
- Prefix sums.
- Pair counting.
- Algebraic rearrangement of pair expressions.
- Replacing `O(N²)` pair enumeration with aggregate contribution.


**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Sum of Absolute Differences in a Sorted Array](https://leetcode.com/problems/sum-of-absolute-differences-in-a-sorted-array/) | LC |
| 2 | [Minimum Absolute Difference](https://leetcode.com/problems/minimum-absolute-difference/) | LC |
| 3 | [Minimum Cost to Make Array Equal](https://leetcode.com/problems/minimum-cost-to-make-array-equal/) | LC |
| 4 | [Manhattan Distances](https://cses.fi/problemset/task/3411/) | CSES |



[↑ Back to TOC](#table-of-contents)

---
## Form 22 — Bit Contribution

**Recognition:** Treat each bit independently and count zeros/ones or pair contributions.

**Prerequisites — know these first:**
- Binary representation.
- Bit positions.
- Bitwise AND / OR / XOR.
- Counting set and unset bits.
- Independent-bit contribution idea.
- Basic powers of two.


**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Total Hamming Distance](https://leetcode.com/problems/total-hamming-distance/) | LC |
| 2 | [Sum of All Subset XOR Totals](https://leetcode.com/problems/sum-of-all-subset-xor-totals/) | LC |
| 3 | [XOR Beauty of Array](https://leetcode.com/problems/find-xor-beauty-of-array/) | LC |
| 4 | [Bitwise AND of Numbers Range](https://leetcode.com/problems/bitwise-and-of-numbers-range/) | LC |



[↑ Back to TOC](#table-of-contents)

---
## Form 23 — Stream Mean / Variance / Running Statistics

**Recognition:** Maintain statistics incrementally instead of recomputing over the whole stream.

**Prerequisites — know these first:**
- Running sum and count.
- Mean definition.
- Squares and `x²`.
- Population variance idea.
- Algebra identity `(a-b)² = a² - 2ab + b²`.
- Maintaining small aggregate state instead of storing the full stream.


**Problems: 3**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Moving Average from Data Stream](https://leetcode.com/problems/moving-average-from-data-stream/) | LC |
| 2 | [MKAverage](https://leetcode.com/problems/finding-mk-average/) | LC |
| 3 | [Stock Price Fluctuation](https://leetcode.com/problems/stock-price-fluctuation/) | LC |



[↑ Back to TOC](#table-of-contents)

---
## Form 24 — Min Stack / Maintain Aggregate State

**Recognition:** Each push/pop also maintains min/max/other aggregate state.

**Prerequisites — know these first:**
- Stack operations.
- Running minimum / maximum.
- State restoration after `pop`.
- Snapshot idea: each stack depth may need its own aggregate state.
- Duplicate minimum handling.


**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Min Stack](https://leetcode.com/problems/min-stack/) | LC |
| 2 | [Max Stack](https://leetcode.com/problems/max-stack/) | LC |
| 3 | [Maximum Frequency Stack](https://leetcode.com/problems/maximum-frequency-stack/) | LC |
| 4 | [All O`one Data Structure](https://leetcode.com/problems/all-oone-data-structure/) | LC |



[↑ Back to TOC](#table-of-contents)

---
## Form 25 — Stack / Queue Transformation

**Recognition:** Implement one access discipline using another or balance multiple queues/deques.

**Prerequisites — know these first:**
- Stack = LIFO.
- Queue = FIFO.
- Deque basics.
- Simulation of access order.
- Amortized-analysis intuition is helpful for two-stack queue designs.


**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Implement Queue using Stacks](https://leetcode.com/problems/implement-queue-using-stacks/) | LC |
| 2 | [Implement Stack using Queues](https://leetcode.com/problems/implement-stack-using-queues/) | LC |
| 3 | [Design Circular Queue](https://leetcode.com/problems/design-circular-queue/) | LC |
| 4 | [Design Front Middle Back Queue](https://leetcode.com/problems/design-front-middle-back-queue/) | LC |



[↑ Back to TOC](#table-of-contents)

---
## Form 26 — Lazy Stack Increment

**Recognition:** Delay/batch updates so a range-like stack operation stays efficient.

**Prerequisites — know these first:**
- Stack / vector indexing.
- Prefix range `[0..k-1]`.
- Lazy / deferred-update idea.
- Difference-array intuition is useful.
- Understanding that pending updates can be propagated when elements are removed.


**Problems: 3**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Range Addition](https://leetcode.com/problems/range-addition/) | LC |
| 2 | [Fancy Sequence](https://leetcode.com/problems/fancy-sequence/) | LC |
| 3 | [Corporate Flight Bookings](https://leetcode.com/problems/corporate-flight-bookings/) | LC |



[↑ Back to TOC](#table-of-contents)

---
## Form 27 — Product of Last K / Prefix State

**Recognition:** Maintain prefix-like state so recent aggregate queries are O(1) or logarithmic.

**Prerequisites — know these first:**
- Prefix sums, then generalize to prefix products.
- Multiplicative identity `1`.
- Suffix queries.
- Division as cancellation of an earlier prefix.
- Why zero breaks product division and creates a reset boundary.


**Problems: 3**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Product of the Last K Numbers](https://leetcode.com/problems/product-of-the-last-k-numbers/) | LC |
| 2 | [Range Product Queries of Powers](https://leetcode.com/problems/range-product-queries-of-powers/) | LC |
| 3 | [Range Sum Query - Immutable](https://leetcode.com/problems/range-sum-query-immutable/) | LC |



[↑ Back to TOC](#table-of-contents)

---
## Form 28 — Snapshot / Versioned Data

**Recognition:** Store only changes and query historical state by version/time.

**Prerequisites — know these first:**
- Arrays.
- Version / snapshot idea.
- Store-only-changes thinking.
- Sorted change history.
- Binary search / upper bound.
- Predecessor query: latest version `<= target`.


**Problems: 3**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Snapshot Array](https://leetcode.com/problems/snapshot-array/) | LC |
| 2 | [Time Based Key-Value Store](https://leetcode.com/problems/time-based-key-value-store/) | LC |
| 3 | [Design Underground System](https://leetcode.com/problems/design-underground-system/) | LC |



[↑ Back to TOC](#table-of-contents)

---
## Form 29 — LRU Cache

**Recognition:** O(1) lookup plus recency ordering and eviction.

**Prerequisites — know these first:**
- Hash map.
- Doubly linked list.
- Iterators / direct node references.
- MRU vs LRU.
- O(1) deletion / movement when the node is already known.


**Problems: 3**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [LRU Cache](https://leetcode.com/problems/lru-cache/) | LC |
| 2 | [Design Browser History](https://leetcode.com/problems/design-browser-history/) | LC |
| 3 | [Design Linked List](https://leetcode.com/problems/design-linked-list/) | LC |



[↑ Back to TOC](#table-of-contents)

---
## Form 30 — LFU / Frequency-Based Cache

**Recognition:** Track frequency plus recency/tie-breaking efficiently.

**Prerequisites — know these first:**
- LRU Cache.
- Frequency counting.
- Hash maps.
- Doubly linked lists / frequency buckets.
- MRU/LRU ordering inside one frequency.
- Maintaining a minimum-frequency invariant.


**Problems: 3**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [LFU Cache](https://leetcode.com/problems/lfu-cache/) | LC |
| 2 | [Design Twitter](https://leetcode.com/problems/design-twitter/) | LC |
| 3 | [Food Ratings](https://leetcode.com/problems/design-a-food-rating-system/) | LC |



[↑ Back to TOC](#table-of-contents)

---
## Form 31 — Randomized Data Structure

**Recognition:** Combine arrays/maps or probabilistic sampling for efficient random operations.

**Prerequisites — know these first:**
- Arrays / vectors.
- Hash map.
- Swapping elements.
- Uniform random choice.
- Prefix sums are needed for weighted random selection.
- Probability basics: equal probability vs weighted probability.


**Problems: 4**

| # | Problem | Platform |
|---:|---|:---:|
| 1 | [Insert Delete GetRandom O(1)](https://leetcode.com/problems/insert-delete-getrandom-o1/) | LC |
| 2 | [Random Pick with Weight](https://leetcode.com/problems/random-pick-with-weight/) | LC |
| 3 | [Shuffle an Array](https://leetcode.com/problems/shuffle-an-array/) | LC |
| 4 | [Linked List Random Node](https://leetcode.com/problems/linked-list-random-node/) | LC |



[↑ Back to TOC](#table-of-contents)

---

## How to Use the Prerequisites

For each form:

```text
Prerequisites understood?
        |
       yes
        |
        v
Read Recognition
        |
        v
Solve benchmark problems
        |
        v
Can recognize unlabeled variation?
```

If a prerequisite is weak, revise only that prerequisite first; then return to the form.  
You do **not** need to master every advanced STL topic before starting the earlier forms.

---

## Practice Rule

Use each form in this order: **learn → derive → solve independently → hidden variation → mixed practice → contest**.

> **Mastery test:** You can recognize an unlabeled variation, explain why the form applies, and implement it without opening the notes.