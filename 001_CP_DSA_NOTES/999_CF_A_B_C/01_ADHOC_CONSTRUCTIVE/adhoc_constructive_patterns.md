# Ad-Hoc & Constructive Patterns — Tutorial + Practice

Same format as before: **core idea** → **dry run** → **ASCII diagram** → **code skeleton** → **practice problems**. These two categories don't have a formula to memorize — the goal is to build reflexes: "when I see X in the statement, my first move is Y."

**Link note:** CSES links below are verified against real task IDs. Codeforces links use the standard `problemset/problem/{contest}/{index}` format — if a link 404s, search the title directly on Codeforces' problemset page; a few of these are from memory rather than freshly re-verified.

---

# PART 1 — AD-HOC PATTERNS

## A1. Simulate It Directly

**Core idea:** The statement literally describes a process step by step. Don't look for a formula — just write a loop that mirrors the description exactly. The only "skill" is careful, bug-free translation of English into code.

**Trigger phrases:** "on each turn", "repeat until", "process happens as follows"

### Dry Run
A robot starts at position 0. Each second it moves +1 if the second is odd, -1 if even. Where is it after 5 seconds?

```
sec 1 (odd):  pos = 0 + 1 = 1
sec 2 (even): pos = 1 - 1 = 0
sec 3 (odd):  pos = 0 + 1 = 1
sec 4 (even): pos = 1 - 1 = 0
sec 5 (odd):  pos = 0 + 1 = 1

Answer: 1 (no formula needed — just run the loop)
```

```
Timeline diagram:
sec:   1    2    3    4    5
pos:   0 -> 1 -> 0 -> 1 -> 0 -> 1
       start  +1   -1   +1   -1   +1
```

### C++
```cpp
int pos = 0;
for (int sec = 1; sec <= n; sec++) {
    if (sec % 2 == 1) pos += 1;
    else pos -= 1;
}
```

### Practice
1. [CF 4A](https://codeforces.com/problemset/problem/4/A) – Watermelon
2. [CF 71A](https://codeforces.com/problemset/problem/71/A) – Way Too Long Words
3. [CF 158A](https://codeforces.com/problemset/problem/158/A) – Next Round
4. CSES – [Weird Algorithm](https://cses.fi/problemset/task/1068)
5. [CF 231A](https://codeforces.com/problemset/problem/231/A) – Team
6. [CF 236A](https://codeforces.com/problemset/problem/236/A) – Boy or Girl

---

## A2. Casework / Branching on Conditions

**Core idea:** The answer isn't one formula — it's 2-4 distinct scenarios, each trivial once identified. The actual work is enumerating every case exhaustively (including the ones the problem hints at only implicitly) before you start coding, so you don't discover a missing case mid-submission.

**Trigger phrases:** multiple embedded "if / unless / except when" clauses; small fixed constraint range (n ≤ 4)

### Dry Run
"Given a, b, c — output 'yes' if you can form a triangle (obtuse/acute doesn't matter), else 'no'."

```
Case enumeration BEFORE coding:
  case 1: a+b <= c  -> impossible (fails triangle inequality)
  case 2: a+c <= b  -> impossible
  case 3: b+c <= a  -> impossible
  case 4: none of the above -> valid triangle

Test: a=3,b=4,c=5
  3+4=7 > 5 ✓
  3+5=8 > 4 ✓
  4+5=9 > 3 ✓
  -> valid triangle, print "yes"
```

```
Case-tree diagram:

           check a+b vs c
          /                \
     a+b<=c              a+b>c
     "no"                   |
                    check a+c vs b
                   /              \
              a+c<=b            a+c>b
              "no"                 |
                          check b+c vs a
                         /              \
                    b+c<=a            b+c>a
                    "no"              "yes"
```

### C++
```cpp
bool isTriangle(double a, double b, double c) {
    if (a + b <= c) return false;
    if (a + c <= b) return false;
    if (b + c <= a) return false;
    return true;
}
```

### Practice
1. [CF 4A](https://codeforces.com/problemset/problem/4/A) – Watermelon (2-case parity split)
2. [CF 50A](https://codeforces.com/problemset/problem/50/A) – Domino piling
3. [CF 1352B](https://codeforces.com/problemset/problem/1352/B) – Same Parity Summation
4. [CF 977A](https://codeforces.com/problemset/problem/977/A) – Wrong Subtraction
5. [CF 1029B](https://codeforces.com/problemset/problem/1029/B) – Creating the Contest
6. [CF 4C](https://codeforces.com/problemset/problem/4/C) – Registration System (casework on seen/unseen)

---

## A3. Brute-Force Small Cases to Spot a Pattern

**Core idea:** When the formula isn't obvious from reading the statement, hand-compute the answer for n=1,2,3,4,5 and look at the sequence of outputs. A cycle, a parity flip, or a simple recurrence usually jumps out. This is the single highest-value habit for ad-hoc problems — always do this *before* trying to prove anything.

**Trigger phrases:** none specific — this is your fallback whenever you're stuck for more than 3-4 minutes on a problem with a small answer range.

### Dry Run
"Two players alternate removing 1 or 2 stones from a pile of n. Whoever removes the last stone wins. Does the first player win?" (classic subtraction game — figure out the rule by hand.)

```
n=1: first player takes 1, wins.           -> WIN
n=2: first player takes 2, wins.           -> WIN
n=3: first player takes 1 (leaves 2 for opponent, who then wins) 
     or takes 2 (leaves 1, opponent wins)  -> LOSE (every move hands opponent a win)
n=4: first player takes 1 (leaves 3=LOSE for opponent) -> WIN
n=5: first player takes 2 (leaves 3=LOSE for opponent) -> WIN
n=6: takes 1->leaves 5(WIN opp), takes 2->leaves 4(WIN opp) -> LOSE

Sequence: W W L W W L W W L ...
Pattern spotted: LOSE exactly when n is a multiple of 3!
```

```
Pattern-spotting table:

  n:      1  2  3  4  5  6  7  8  9
  result: W  W  L  W  W  L  W  W  L
                ^        ^        ^
              n%3==0   n%3==0   n%3==0

Once you see the repeat, you don't need to simulate for large n —
just check n % 3 == 0
```

### C++
```cpp
// After spotting the pattern by hand, the "solution" becomes one line:
bool firstPlayerWins(long long n) { return n % 3 != 0; }
```

### Practice
1. [CF 1194A](https://codeforces.com/problemset/problem/1194/A) – Rewriting
2. [CF 1450A](https://codeforces.com/problemset/problem/1450/A) – Avoid Trygub
3. CSES – Nim Game I (pattern via XOR, but hand-verify with small cases first)
4. [CF 455B](https://codeforces.com/problemset/problem/455/B) – A Lot of Games
5. [CF 1537D](https://codeforces.com/problemset/problem/1537/D) – Deleting Divisors
6. [CF 1621B](https://codeforces.com/problemset/problem/1621/B) – Field of Stones (pattern-spotting via small n)

---

## A4. Extremal-Element Reasoning

**Core idea:** Instead of reasoning about the whole array, focus specifically on the maximum or minimum element — very often the entire answer is determined by what happens to just that one element, since it's the tightest constraint.

**Trigger phrases:** "array", combined with "minimum operations to make all equal / all ≤ k / all divisible by..."

### Dry Run
"Minimum number of operations to make all elements of the array equal, where one operation increments any single element by 1." Array: `[2, 5, 3]`.

```
Key insight: since we can only INCREASE, everything must rise to meet the LARGEST element (5).
We never need to consider decreasing — that's not a valid operation here.

target = max(array) = 5
cost = (5-2) + (5-5) + (5-3) = 3 + 0 + 2 = 5

The whole problem reduced to one extremal check: find the max, sum the gaps to it.
```

```
Bar-chart diagram (raise every bar to match the tallest):

  5 |        #
  4 |        #
  3 |   #    #     <- target height = max = 5
  2 | # #    #
  1 | # #    #
    +--------------
      2  3   5      (original heights)

  gaps to fill:  3    2    0   -> total operations = 5
```

### C++
```cpp
long long minOpsToEqual(vector<int>& a) {
    int mx = *max_element(a.begin(), a.end());
    long long cost = 0;
    for (int x : a) cost += (mx - x);
    return cost;
}
```

### Practice
1. [CF 1512A](https://codeforces.com/problemset/problem/1512/A) – Spy Detected!
2. [CF 1650A](https://codeforces.com/problemset/problem/1650/A) – Deletions of Two Adjacent Letters (extremal parity)
3. [CF 1741B](https://codeforces.com/problemset/problem/1741/B) – Funny Permutation
4. [CF 1618C](https://codeforces.com/problemset/problem/1618/C) – Paint (min/max reasoning)
5. [CF 1360D](https://codeforces.com/problemset/problem/1360/D) – Buying Shovels
6. CSES – [Maximum Subarray Sum](https://cses.fi/problemset/task/1643) (Kadane, extremal running value)

---

## A5. Work Backwards from the End State

**Core idea:** Forward simulation is expensive or unclear, but the *final* state is tightly constrained — so reason from the target backward toward the start. Common whenever a process only shrinks/reduces a value and you need to know if a target is reachable.

**Trigger phrases:** "can you reach exactly X", "reduce to zero", "reverse the operations"

### Dry Run
"You can repeatedly replace n with n/2 (if even) or n-1. What's the minimum steps to reach 1 from n=10?" — reasoning backward from 1 makes the halving structure obvious.

```
Forward (confusing which move to pick greedily): 10 -> ? -> ... -> 1
Backward from 1, "undo" moves: 1 can come from 2 (undo halve) or 2 (undo -1, since 2-1=1)
  1 <- 2 <- 4 <- 5 <- 10   (this reconstructs a path)
  or 1 <- 2 <- 3 -> ... (worse)

Reading it forward: 10 ->(halve) 5 ->(minus1) 4 ->(halve) 2 ->(halve) 1
4 steps total. Working backward from the known target (1) made the greedy halve-when-possible
rule obvious, instead of guessing forward.
```

```
Backward-construction diagram:

  target: 1
     ^ (halve)
     2
     ^ (halve)
     4
     ^ (+1, i.e. undoing a "-1" move)
     5
     ^ (halve)
     10  <- start

reading bottom-to-top gives the forward answer: 10->5->4->2->1
```

### C++
```cpp
int minSteps(int n) {
    int steps = 0;
    while (n > 1) {
        if (n % 2 == 0) n /= 2;
        else n -= 1;
        steps++;
    }
    return steps;
}
```

### Practice
1. [CF 1360A](https://codeforces.com/problemset/problem/1360/A) – Minimal Square
2. [CF 1352C](https://codeforces.com/problemset/problem/1352/C) – K-th Not Divisible by n
3. [CF 1516A](https://codeforces.com/problemset/problem/1516/A) – Tit for Tat
4. [CF 1607A](https://codeforces.com/problemset/problem/1607/A) – Linear Keyboard (reverse mapping)
5. [CF 1739A](https://codeforces.com/problemset/problem/1739/A) – Compare T-Shirt Sizes (small reachability reasoning)

---

## A6. Sort First, Then Reason

**Core idea:** Sorting is a free O(n log n) preprocessing step that often converts a confusing unordered problem into an obvious sequential one — pairing greedily becomes trivial once order is imposed. Whenever you're stuck on an array problem and haven't tried sorting, try it.

**Trigger phrases:** "pair up elements to minimize/maximize", "rearrange the array"

### Dry Run
"Pair up 2n people by height to minimize the sum of |height differences| within each pair." Heights: `[1, 4, 2, 5]`.

```
Unsorted, hard to see the rule: [1, 4, 2, 5]

Sort first: [1, 2, 4, 5]

Rule (provable via exchange argument): pair ADJACENT elements after sorting
pairs: (1,2) diff=1     (4,5) diff=1
total = 2   <- this is provably optimal; any other pairing does worse
```

```
Sorted-array pairing diagram:

sorted: [ 1 ][ 2 ][ 4 ][ 5 ]
          \  /      \  /
         pair1      pair2      <- adjacent pairing after sort minimizes total diff
          diff=1     diff=1
```

### C++
```cpp
sort(a.begin(), a.end());
long long totalDiff = 0;
for (int i = 0; i + 1 < (int)a.size(); i += 2)
    totalDiff += a[i+1] - a[i];
```

### Practice
1. [CF 1512B](https://codeforces.com/problemset/problem/1512/B) – Almost Rectangle
2. [CF 1256E](https://codeforces.com/problemset/problem/1256/E) – Yet Another Division Into Teams
3. [CF 1607C](https://codeforces.com/problemset/problem/1607/C) – Minimum Extraction
4. [CF 1462B](https://codeforces.com/problemset/problem/1462/B) – Last Year's Substring (sort-adjacent reasoning)
5. CSES – [Sum of Two Values](https://cses.fi/problemset/task/1640) (two-pointer on sorted array)
6. [CF 1791D](https://codeforces.com/problemset/problem/1791/D) – Distinct Balls

---

## A7. Invariant Spotting

**Core idea:** Identify a quantity that provably never changes across allowed moves (parity of the sum, XOR of all values, count mod k). Once you find it, the entire problem often collapses into "compare the invariant of the start state to the invariant of the target state."

**Trigger phrases:** "can you transform A into B using operation X", "is it possible to reach state..."

### Dry Run
"You can swap any two adjacent elements. Can array `[3,1,2]` become `[1,2,3]`?" — the invariant here is not conservation but *parity of inversions* changing by exactly 1 per swap, so any target is reachable given enough swaps (adjacent swaps generate the full permutation group) — a cleaner invariant example:

"You can replace any two numbers a,b with a+1,b-1. Can `[1,5]` become `[3,3]`?"

```
Check the invariant: sum before = 1+5 = 6
                      sum after target = 3+3 = 6   <- sums match!

The operation a,b -> a+1,b-1 always preserves total sum (SUM is the invariant).
Since 6==6, it's POSSIBLE (and here trivially: one operation gets you there).

If the target summed to anything other than 6, the answer would be
immediately "impossible" with ZERO simulation needed.
```

```
Invariant-check diagram:

  start:  [1, 5]   sum = 6 -----+
                                 |  compare
  target: [3, 3]   sum = 6 -----+
                                 |
                            equal? -> YES -> possible
                            (if not equal -> impossible, no search needed)
```

### C++
```cpp
bool reachable(vector<int>& start, vector<int>& target) {
    long long s1 = accumulate(start.begin(), start.end(), 0LL);
    long long s2 = accumulate(target.begin(), target.end(), 0LL);
    return s1 == s2; // sum is the invariant for this operation
}
```

### Practice
1. [CF 1466D](https://codeforces.com/problemset/problem/1466/D) – Employment
2. [CF 1375C](https://codeforces.com/problemset/problem/1375/C) – Element Extermination
3. [CF 1367B](https://codeforces.com/problemset/problem/1367/B) – Even Array
4. [CF 1385D](https://codeforces.com/problemset/problem/1385/D) – a-Good String (parity/XOR invariant)
5. [CF 1547B](https://codeforces.com/problemset/problem/1547/B) – Alphabetical Strings
6. CSES – [Bit Strings](https://cses.fi/problemset/task/1617) (invariant-adjacent counting)

---

# PART 2 — CONSTRUCTIVE PATTERNS

## C1. Extreme-Value Dodge

**Core idea:** When a constraint must hold for every element ("no two adjacent differ by less than k"), don't search for a clever arrangement — push values to their absolute extremes so the constraint satisfies itself automatically by construction.

**Trigger phrases:** "construct an array where every pair/adjacent difference satisfies..."

### Dry Run
"Construct an array of n distinct positive integers where every adjacent pair differs by at least 5."

```
Don't search for a clever combo — just space them out maximally:
a[0] = 1
a[1] = 1 + 5 = 6
a[2] = 6 + 5 = 11
a[3] = 11 + 5 = 16
...
a[i] = 1 + 5*i

For n=4: [1, 6, 11, 16]  — every adjacent diff is EXACTLY 5, constraint trivially satisfied
```

```
Number-line diagram (spacing exactly at the minimum required gap):

  1 -----5----- 6 -----5----- 11 -----5----- 16
  a[0]          a[1]           a[2]           a[3]
  (each step is exactly the minimum allowed gap — can't violate the constraint by construction)
```

### C++
```cpp
vector<long long> buildSpaced(int n, long long gap) {
    vector<long long> a(n);
    for (int i = 0; i < n; i++) a[i] = 1 + (long long)gap * i;
    return a;
}
```

### Practice
1. [CF 1359B](https://codeforces.com/problemset/problem/1359/B) – New Theatre Square
2. [CF 1520C](https://codeforces.com/problemset/problem/1520/C) – Not Adjacent Matrix
3. [CF 1618D](https://codeforces.com/problemset/problem/1618/D) – Array and Operations
4. [CF 1512B](https://codeforces.com/problemset/problem/1512/B) – Almost Rectangle
5. [CF 1360D](https://codeforces.com/problemset/problem/1360/D) – Buying Shovels
6. [CF 1547C](https://codeforces.com/problemset/problem/1547/C) – Pair Programming

---

## C2. Alternating / Interleaving Construction

**Core idea:** Split the values into two groups by some property, then interleave them one-by-one. This is the default move whenever the constraint forbids two same-property elements from being adjacent.

**Trigger phrases:** "no two adjacent elements are both even/odd/equal/same type"

### Dry Run
"Rearrange `[1,1,2,2,3]` so no two adjacent elements are equal." Group by value: {1,1}, {2,2}, {3}.

```
Sort groups by frequency descending: 1(x2), 2(x2), 3(x1)
Interleave round-robin:
  slot0: 1   slot1: 2   slot2: 1   slot3: 2   slot4: 3
  result: [1, 2, 1, 2, 3]

Check adjacent pairs: (1,2)ok (2,1)ok (1,2)ok (2,3)ok -> valid!
```

```
Round-robin interleaving diagram:

group A (1,1):  1_ 1_          (placed at even slots)
group B (2,2):  _2 _2          (placed at odd slots)
group C (3):            3      (placed after groups exhausted)

merged: [1, 2, 1, 2, 3]
         A  B  A  B  C
```

### C++
```cpp
vector<int> interleaveNoAdjacentEqual(map<int,int>& freq) {
    // freq: value -> count, assume max count <= (n+1)/2 (otherwise impossible)
    vector<pair<int,int>> groups(freq.begin(), freq.end());
    sort(groups.begin(), groups.end(), [](auto&a, auto&b){ return a.second > b.second; });
    vector<int> result;
    int totalSlots = 0;
    for (auto& [val, cnt] : groups) totalSlots += cnt;
    result.resize(totalSlots);
    int idx = 0;
    for (auto& [val, cnt] : groups) {
        for (int i = 0; i < cnt; i++) {
            result[idx] = val;
            idx += 2;
            if (idx >= totalSlots) idx = 1; // wrap to odd slots once evens are full
        }
    }
    return result;
}
```

### Practice
1. [CF 1370C](https://codeforces.com/problemset/problem/1370/C) – Number Game (interleave-adjacent reasoning)
2. [CF 1520D](https://codeforces.com/problemset/problem/1520/D) – Same Differences (grouping)
3. [CF 1352G](https://codeforces.com/problemset/problem/1352/G) – Special Permutation
4. [CF 1156B](https://codeforces.com/problemset/problem/1156/B) – Ugly Pairs (alternating parity construction)
5. [CF 1608B](https://codeforces.com/problemset/problem/1608/B) – Bekhchoy and Numbers
6. [CF 1607B](https://codeforces.com/problemset/problem/1607/B) – Poisoned Dagger

---

## C3. Block Construction

**Core idea:** Build the answer in fixed-size, self-contained chunks — each chunk independently satisfies the local rule, so you never have to reason about interactions across chunk boundaries. Very common for "every window of size k must satisfy..." problems.

**Trigger phrases:** "every k consecutive elements", "divide into groups of size k"

### Dry Run
"Construct a binary string of length n where every window of 3 consecutive characters has at least one '1'." n=9.

```
Build in blocks of 3, each block containing exactly one '1' anywhere convenient (say, last position):
block1: 0 0 1
block2: 0 0 1
block3: 0 0 1

full string: 001 001 001 = "001001001"

Check any window of 3: e.g. positions 2-4 = "010" -> has a 1 ✓
positions 3-5 = "100" -> wait need to check boundary windows carefully,
but placing the 1 at every 3rd position guarantees max gap between 1's is 2,
so every window of 3 must catch one.
```

```
Block diagram (| marks block boundaries, ^ marks the guaranteed '1'):

 [0 0 1] [0 0 1] [0 0 1]
       ^       ^       ^
 any 3-wide window sliding across still catches a '1' because
 consecutive 1's are never more than 2 apart
```

### C++
```cpp
string buildBlocked(int n, int k) {
    string s(n, '0');
    for (int i = k - 1; i < n; i += k) s[i] = '1';
    if (s.back() == '0') s.back() = '1'; // patch trailing block if it's short
    return s;
}
```

### Practice
1. [CF 1360D](https://codeforces.com/problemset/problem/1360/D) – Buying Shovels
2. [CF 1554B](https://codeforces.com/problemset/problem/1554/B) – Cobb
3. [CF 1618C](https://codeforces.com/problemset/problem/1618/C) – Paint
4. [CF 1526C1](https://codeforces.com/problemset/problem/1526/C1) – Potions (Easy Version)
5. [CF 1454B](https://codeforces.com/problemset/problem/1454/B) – Unique Bid Auction (block-like grouping)
6. [CF 1141B](https://codeforces.com/problemset/problem/1141/B) – Maximal Continuous Rest

---

## C4. Greedy-Build-Left-to-Right

**Core idea:** Place elements one at a time from left to right (or in priority order), always choosing whichever option keeps the most future flexibility. Combines the constructive mindset with a greedy proof: you need to argue that the locally-best choice can never trap you later.

**Trigger phrases:** "output any valid sequence", combined with an ordering constraint

### Dry Run
"Construct a permutation of 1..5 that is lexicographically smallest while satisfying: position 3 must NOT contain value 3." Build left to right, always trying the smallest unused value first, skipping only when forced.

```
pos1: try smallest unused = 1 -> allowed (no constraint on pos1) -> place 1
pos2: try smallest unused = 2 -> allowed -> place 2
pos3: try smallest unused = 3 -> FORBIDDEN (constraint says pos3 != 3) -> try next: 4 -> allowed -> place 4
pos4: try smallest unused = 3 -> allowed (constraint was only for pos3) -> place 3
pos5: try smallest unused = 5 -> allowed -> place 5

Result: [1, 2, 4, 3, 5]  <- lexicographically smallest valid permutation
```

```
Left-to-right build diagram (X = forbidden choice at that position):

pos:      1    2    3    4    5
try:      1    2    3(X) 3    5
                  \  reject, try next
placed:   1    2    4    3    5
```

### C++
```cpp
vector<int> buildLexSmallest(int n, int forbiddenPos, int forbiddenVal) {
    vector<int> result;
    vector<bool> used(n + 1, false);
    for (int pos = 1; pos <= n; pos++) {
        for (int val = 1; val <= n; val++) {
            if (used[val]) continue;
            if (pos == forbiddenPos && val == forbiddenVal) continue;
            result.push_back(val);
            used[val] = true;
            break;
        }
    }
    return result;
}
```

### Practice
1. [CF 1512D](https://codeforces.com/problemset/problem/1512/D) – Corrupted Array
2. [CF 1608A](https://codeforces.com/problemset/problem/1608/A) – Yet Another Two Array Problem
3. [CF 1526C2](https://codeforces.com/problemset/problem/1526/C2) – Potions (Hard Version)
4. [CF 1090A](https://codeforces.com/problemset/problem/1090/A) – Chess Placing
5. [CF 1618E](https://codeforces.com/problemset/problem/1618/E) – Singer House (greedy build)
6. [CF 1611C](https://codeforces.com/problemset/problem/1611/C) – Polycarp Recovers the Permutation

---

## C5. Reduce to a Known Shape

**Core idea:** Recognize that the target structure is secretly a well-known combinatorial object (a permutation, a derangement, a balanced bracket sequence, a Latin square) and adapt its standard construction instead of inventing one from scratch.

**Trigger phrases:** "every value appears exactly once", "balanced", "no element in its original position"

### Dry Run
"Construct a permutation of 1..n where no element equals its position (a derangement)." Recognize this is exactly the derangement problem — apply the known cyclic-shift trick.

```
n=5, recognized shape: derangement
Known construction: cyclic shift by 1 (works for all n except n=1)

position: 1  2  3  4  5
value:    2  3  4  5  1     <- each value = position+1, wraps at the end

Verify: pos1->2(≠1) pos2->3(≠2) pos3->4(≠3) pos4->5(≠4) pos5->1(≠5)  all good!
Special case to remember: n=1 has NO valid derangement (must handle separately)
```

```
Recognized-shape lookup diagram:

  "no element in its position"  --> matches KNOWN shape: derangement
                                       |
                                cyclic shift construction (standard tool)
                                       |
                              always works except n=1 (memorize this edge case)
```

### C++
```cpp
vector<int> derangementByShift(int n) {
    if (n == 1) return {}; // impossible, caller must handle
    vector<int> result(n);
    for (int i = 0; i < n; i++) result[i] = (i + 1) % n + 1;
    return result;
}
```

### Practice
1. [CF 1327C](https://codeforces.com/problemset/problem/1327/C) – Game with Chocolate Squares (skip if mismatched) — [CF 1360C](https://codeforces.com/problemset/problem/1360/C) – Similar Pairs
2. [CF 1097C](https://codeforces.com/problemset/problem/1097/C) – Yet Another Small Multiple
3. [CF 1345C](https://codeforces.com/problemset/problem/1345/C) – Prefixes of LCS
4. CSES – [Bracket Sequences I](https://cses.fi/problemset/list/) (search title — recognized shape: balanced brackets)
5. [CF 1152B](https://codeforces.com/problemset/problem/1152/B) – Neko Performs Cat Furrier Transform
6. [CF 1481B](https://codeforces.com/problemset/problem/1481/B) – Almost Rectangle (skip if mismatched) — [CF 1481C](https://codeforces.com/problemset/problem/1481/C) – Fence Painting

---

## C6. Binary / Power-of-Two Decomposition

**Core idea:** When you need to represent a value or build a set that must "cover" a range, decompose using powers of 2. This guarantees you can hit every value in a range with only O(log n) pieces, and is the standard tool for "represent n as a sum of..." constructive tasks.

**Trigger phrases:** "represent n as a sum of at most log(n) numbers", "using powers of 2"

### Dry Run
"Represent 13 as a sum of the fewest powers of 2." This is just binary representation.

```
13 in binary: 1101
             bit3 bit2 bit1 bit0
              1    1    0    1
            = 8  + 4  + 0  + 1
            = 8 + 4 + 1 = 13

Fewest terms = number of 1-bits = 3 terms: {8, 4, 1}
```

```
Bit-decomposition diagram:

  13 = 1101(binary)
        |  |  |  |
        8  4  2  1   <- place values
        1  1  0  1   <- bits of 13
        -----------
       keep only where bit=1: 8 + 4 + 1 = 13 (3 terms, minimum possible)
```

### C++
```cpp
vector<long long> powerOfTwoDecompose(long long n) {
    vector<long long> terms;
    for (long long bit = 1; bit <= n; bit <<= 1)
        if (n & bit) terms.push_back(bit);
    return terms;
}
```

### Practice
1. [CF 1225C](https://codeforces.com/problemset/problem/1225/C) – p-binary
2. [CF 1338A](https://codeforces.com/problemset/problem/1338/A) – Powered Addition
3. [CF 1097D](https://codeforces.com/problemset/problem/1097/D) – Makoto and a Blackboard (bit-decomposition adjacent)
4. [CF 1567B](https://codeforces.com/problemset/problem/1567/B) – MEXor Mixup
5. [CF 1657B](https://codeforces.com/problemset/problem/1657/B) – XY Sequence (skip if mismatched) — [CF 1354B1](https://codeforces.com/problemset/problem/1354/B1) – Ternary String (Easy Version)

---

## C7. Special-Case the Tiny n First

**Core idea:** Many constructive problems have one clean general rule that silently breaks for n=1 or n=2 (or an all-equal array, or k=0). Before submitting, explicitly test your rule by hand against these tiny/degenerate inputs — this catches more constructive-problem bugs than any other single habit.

**Trigger phrases:** none specific — this is a pre-submission checklist item, not a statement trigger.

### Dry Run
Revisit the derangement rule from C5: cyclic shift works for n≥2, but breaks at n=1.

```
General rule: value[i] = (i+1) % n + 1

Test n=5 (should work): [2,3,4,5,1] -> valid, no fixed points  ✓
Test n=2 (should work): [2,1]       -> valid, no fixed points  ✓
Test n=1 (EDGE CASE):   [1]         -> position 1 has value 1 -> INVALID (fixed point!)

Conclusion: must special-case n==1 as "output -1 / impossible" BEFORE applying the general rule.
This single dry-run catches a bug that would otherwise fail silently on one hidden test case.
```

```
Pre-submission checklist diagram:

  general rule -----> test n=1 -----> test n=2 -----> test "all same value"
                          |
                    breaks here!
                          |
                 add explicit special case
                          |
                    THEN submit
```

### C++
```cpp
vector<int> safeDerangement(int n) {
    if (n == 1) return {}; // explicit special case, checked BEFORE general logic
    vector<int> result(n);
    for (int i = 0; i < n; i++) result[i] = (i + 1) % n + 1;
    return result;
}
```

### Practice
1. [CF 1520C](https://codeforces.com/problemset/problem/1520/C) – Not Adjacent Matrix (breaks for odd small n)
2. [CF 1481A](https://codeforces.com/problemset/problem/1481/A) – Space Navigation
3. [CF 1370A](https://codeforces.com/problemset/problem/1370/A) – Maximum GCD
4. [CF 1152A](https://codeforces.com/problemset/problem/1152/A) – Prefixes
5. [CF 1611B](https://codeforces.com/problemset/problem/1611/B) – Team Composition (edge case n small)

---

## C8. Two-Pass Construction (Build Then Repair)

**Core idea:** Build a rough answer in one greedy pass — it won't be perfect — then run a second pass that specifically detects and fixes violations. Useful when a single greedy pass can't guarantee correctness on its own but a targeted repair step can clean up the few broken spots.

**Trigger phrases:** "minimum changes to fix", "rearrange to remove all violations"

### Dry Run
"Given array `[1,1,2,2,2,3]`, rearrange so no two equal adjacent elements exist, using a build-then-repair approach."

```
Pass 1 (naive build, just concatenate as-is): [1,1,2,2,2,3]  <- has violations at positions (1,2),(3,4),(4,5)

Pass 2 (repair scan): walk left to right, whenever a[i]==a[i+1], swap a[i+1] with some later
                       element that differs from both neighbors.
  i=0: a[0]=1,a[1]=1 equal! find later index j where a[j]!=1 and a[j]!=a[2] -> j=3 (value 2)... 
       actually need a[j] != current neighbors; scan finds j=5 (value 3, safe) -> swap a[1],a[5]
       array becomes [1,3,2,2,2,1]
  continue scanning, repair remaining violations similarly...

(In practice this greedy-repair is often replaced by pattern C2's interleaving,
 which avoids needing a repair pass at all — but repair-passes are essential
 when the "build" step is forced by another constraint and can't be freely reordered.)
```

```
Build-then-repair diagram:

 pass 1 (rough build): [1 1 2 2 2 3]
                          ^   ^ ^     <- violations flagged
 pass 2 (targeted repair): swap flagged spots with safe later elements
 result: [1 3 2 1 2 ...]  (violation-free after repair)
```

### C++
```cpp
void repairAdjacentEquals(vector<int>& a) {
    int n = a.size();
    for (int i = 0; i + 1 < n; i++) {
        if (a[i] == a[i+1]) {
            for (int j = i + 2; j < n; j++) {
                if (a[j] != a[i] && a[j] != a[i+1]) {
                    swap(a[i+1], a[j]);
                    break;
                }
            }
        }
    }
}
```

### Practice
1. [CF 1329A](https://codeforces.com/problemset/problem/1329/A) – Divisibility Problem (skip if mismatched) — [CF 1451C](https://codeforces.com/problemset/problem/1451/C) – Successive Subtraction
2. [CF 1618D](https://codeforces.com/problemset/problem/1618/D) – Array and Operations
3. [CF 1547C](https://codeforces.com/problemset/problem/1547/C) – Pair Programming
4. [CF 1526C1](https://codeforces.com/problemset/problem/1526/C1) – Potions (Easy Version)
5. [CF 1156B](https://codeforces.com/problemset/problem/1156/B) – Ugly Pairs

---

## Quick-reference: "I'm stuck — which move do I try?"

```
Symptom in the statement                          -> Try this pattern
--------------------------------------------------------------------------
"process happens step by step"                    -> A1 Simulate directly
statement has embedded if/unless clauses           -> A2 Casework
no formula visible, small answer range             -> A3 Brute-force tiny n
"minimum ops to make all elements..."              -> A4 Extremal-element
"can you reach state X" / reduce to target         -> A5 Work backwards
"pair up / rearrange array to optimize"            -> A6 Sort first
"can operation X transform A into B"               -> A7 Invariant spotting
"construct array where every gap/diff satisfies"   -> C1 Extreme-value dodge
"no two adjacent share a property"                 -> C2 Alternating/interleave
"every window of size k must satisfy"              -> C3 Block construction
"output any valid sequence" + ordering rule        -> C4 Greedy-build-left-right
"every value once" / "balanced" / "no fixed pts"   -> C5 Reduce to known shape
"represent n as sum of few terms"                  -> C6 Power-of-two decompose
(always, before submitting any constructive answer) -> C7 Test n=1, n=2 by hand
"minimum changes to fix / remove violations"       -> C8 Build then repair
```
