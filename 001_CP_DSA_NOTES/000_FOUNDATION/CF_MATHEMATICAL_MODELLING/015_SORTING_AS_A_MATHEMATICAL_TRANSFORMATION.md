# Part 14 — Sorting as a Mathematical Transformation

> **Goal:** treat sorting as a mathematical transformation: arbitrary order becomes monotonic structure that exposes ranks, neighbors, extremes, greedy choices, binary-search boundaries, and contribution counts.
>
> **Core workflow:** `Story → Does original order matter? → Sort → Identify new invariant → Apply second pattern`
>
> **Recognition question:** **What becomes mathematically easier after `a[0] <= a[1] <= ... <= a[n-1]`?**

## Table of Contents

- [14.0 Sorting Mental Model](#140-sorting-mental-model)
- [14.1 Order Statistics](#141-order-statistics)
- [14.2 Min/Max Become Endpoints](#142-minmax-become-endpoints)
- [14.3 Adjacent-Difference Transformation](#143-adjacent-difference-transformation)
- [14.4 Minimum Pair Difference](#144-minimum-pair-difference)
- [14.5 Median Minimizes Absolute Difference](#145-median-minimizes-absolute-difference)
- [14.6 Pair Smallest with Largest](#146-pair-smallest-with-largest)
- [14.7 Sort + Greedy Cheapest First](#147-sort--greedy-cheapest-first)
- [14.8 Sort + Prefix Sum](#148-sort--prefix-sum)
- [14.9 Sort + Binary Search](#149-sort--binary-search)
- [14.10 Sort + Two Pointers](#1410-sort--two-pointers)
- [14.11 Interval Sorting and Merging](#1411-interval-sorting-and-merging)
- [14.12 Sorting Groups Equal Values](#1412-sorting-groups-equal-values)
- [14.13 Rank Transformation](#1413-rank-transformation)
- [14.14 Contribution after Sorting](#1414-contribution-after-sorting)
- [14.15 Rearrangement and Pairing](#1415-rearrangement-and-pairing)
- [14.16 Sorting by Custom Key](#1416-sorting-by-custom-key)
- [14.17 Sort Events to Transform Time](#1417-sort-events-to-transform-time)
- [14.18 Sorting Removes Irrelevant Permutations](#1418-sorting-removes-irrelevant-permutations)
- [14.19 When Sorting Is Invalid](#1419-when-sorting-is-invalid)
- [14.20 60-Second Discovery Workflow](#1420-60-second-discovery-workflow)
- [14.21 Codeforces Recognition Map](#1421-codeforces-recognition-map)
- [14.22 Common Mistakes](#1422-common-mistakes)
- [14.23 Fast Revision Card](#1423-fast-revision-card)

---

## 14.0 Sorting Mental Model

Sorting is not only an implementation step. It transforms arbitrary data into the invariant `a[0] <= a[1] <= ... <= a[n-1]`.

### Real-world example — people by height
```text
Unordered: 182, 165, 174, 190, 168
Sort:      165, 168, 174, 182, 190

shortest = first
tallest  = last
closest heights -> must occur adjacently
```

### Mathematical transformation
```text
unordered values
      ↓ sort
monotonic sequence
      ↓
rank + adjacency + endpoints + monotonic search
```

**Memory hook:** `SORTING CREATES INEQUALITIES.`

---

## 14.1 Order Statistics

After sorting, index represents rank.

```text
a[0]       = smallest
a[1]       = second smallest
a[k-1]     = k-th smallest
a[n-1]     = largest
```

### Real-world example — exam scores
```text
Scores: 78,55,91,67,84
Sort:   55,67,78,84,91

3rd smallest
= a[3-1]
= a[2]
= 78
```

**CF triggers:** `k-th smallest`, `k-th largest`, `lowest k`, `highest k`, `median`.

---

## 14.2 Min/Max Become Endpoints

For ascending sorted data:
```text
min   = a[0]
max   = a[n-1]
range = a[n-1]-a[0]
```

### Real-world example — temperature
```text
24,18,31,22,27
      ↓ sort
18,22,24,27,31

min   = 18
max   = 31
range = 31-18 = 13
```

If you need only min/max once, an `O(n)` scan is better than `O(n log n)` sorting.

---

## 14.3 Adjacent-Difference Transformation

After sorting define:
```text
gap[i] = a[i]-a[i-1]
```

### Real-world example — houses on a road
```text
Positions: 20,3,11,8
Sort:       3,8,11,20

3 ----- 8 --- 11 --------- 20
   5       3         9

gaps = [5,3,9]
```

For sorted `i<j`:
```text
a[j]-a[i]
= gap[i+1] + gap[i+2] + ... + gap[j]
```
A global distance becomes a sum of local gaps.

---

## 14.4 Minimum Pair Difference

Goal:
```text
min |a[i]-a[j]|, i != j
```
After sorting, the closest pair must be adjacent:
```text
answer = min(a[i]-a[i-1])
```

### Real-world example — appointment times
```text
600,540,615,720
      ↓ sort
540,600,615,720

gaps:
600-540 = 60
615-600 = 15
720-615 = 105

answer = 15
```

Why? If `a[i] <= a[k] <= a[j]`, then:
```text
a[j]-a[i] = (a[k]-a[i]) + (a[j]-a[k])
```
so an outer pair cannot beat every internal adjacent gap.

Complexity: `O(n log n)` instead of checking all `O(n²)` pairs.

---

## 14.5 Median Minimizes Absolute Difference

Classic model:
```text
minimize Σ |a[i]-x|
```
An optimal `x` is a median.

### Real-world example — meeting point
Homes:
```text
1,2,10,12,13
```
Median:
```text
x=10
```
Cost:
```text
|1-10|  = 9
|2-10|  = 8
|10-10| = 0
|12-10| = 2
|13-10| = 3
----------------
total    = 22
```
Try `x=2`: `1+0+8+10+11=30`.

Try `x=12`: `11+10+2+0+1=24`.

The median balances how many points pull from the left and right.

**CF trigger:** `make values equal`, `±1 cost`, `minimize sum absolute distance`.

---

## 14.6 Pair Smallest with Largest

Sorting exposes extremes:
```text
a[0] ↔ a[n-1]
a[1] ↔ a[n-2]
...
```

### Real-world example — balance team skills
```text
Skills: 2,9,4,7,5,10
Sort:   2,4,5,7,9,10

2 + 10 = 12
4 +  9 = 13
5 +  7 = 12
```

Visual:
```text
[2, 4, 5, 7, 9, 10]
 ^                 ^
    ^           ^
       ^     ^
```

Useful for balancing extremes or controlling pair sums. Always prove that the objective supports extreme pairing.

---

## 14.7 Sort + Greedy Cheapest First

If each selected item gives the same benefit, but costs differ, cheapest-first maximizes count under a budget.

### Real-world example — notebooks
```text
prices = 8,3,6,2,5
budget = 13

sort -> 2,3,5,6,8

take 2 -> remaining 11 -> count 1
take 3 -> remaining  8 -> count 2
take 5 -> remaining  3 -> count 3
6 does not fit

answer = 3
```

### Exchange proof
If chosen expensive `y` exists while cheaper unchosen `x<=y` exists:
```text
replace y by x
```
Count stays equal and cost cannot increase.

---

## 14.8 Sort + Prefix Sum

Sort defines the smallest `k`; prefix sum makes their total instant.

```text
pref[0]=0
pref[i+1]=pref[i]+a[i]
sum of smallest k = pref[k]
```

### Real-world example — cheapest products
```text
prices: 9,2,7,4,5
sort:   2,4,5,7,9
pref:   0,2,6,11,18,27

cheapest 3
= pref[3]
= 11
```

Transformation:
```text
sort -> establishes rank
prefix -> precomputes cumulative cost
```

---

## 14.9 Sort + Binary Search

Sorting creates a monotonic predicate.

### Real-world example — best ticket within budget
```text
prices: 90,20,60,40,100
sort:   20,40,60,90,100
budget: 65
```

Predicate `price <= 65`:
```text
20   40   60   90   100
Y    Y    Y    N     N
```
The boundary is monotonic. Largest affordable price is `60`.

C++:
```text
upper_bound(...,65)
```
gives first value `>65`; previous value is the answer.

**Triggers:** closest `>=x`, closest `<=x`, count `<=x`, first/last threshold.

---

## 14.10 Sort + Two Pointers

For sorted pair sums:
```text
sum too small -> increase L
sum too large -> decrease R
```

### Real-world example — two gifts totaling 10
```text
8,1,6,4,2
 ↓ sort
1,2,4,6,8

L=1, R=8
1+8=9 < 10
L++

L=2, R=8
2+8=10 -> found
```

Why is `L++` safe? If `a[L]+a[R] < target`, then every `j<R` also satisfies:
```text
a[L]+a[j] <= a[L]+a[R] < target
```
so `a[L]` cannot form the target.

Complexity after sorting: `O(n)` scan.

---

## 14.11 Interval Sorting and Merging

Sort intervals by start.

### Real-world example — meeting occupancy
```text
[5,7], [1,3], [2,6], [9,10]
             ↓ sort
[1,3], [2,6], [5,7], [9,10]
```

Step 1:
```text
current=[1,3]
next=[2,6]
2<=3 -> overlap
current=[1,max(3,6)]=[1,6]
```
Step 2:
```text
next=[5,7]
5<=6 -> overlap
current=[1,7]
```
Step 3:
```text
next=[9,10]
9>7 -> gap
```
Final:
```text
[1,7], [9,10]
```
Sorting guarantees that only the current merged interval and next interval need comparison.

---

## 14.12 Sorting Groups Equal Values

Equal values become contiguous.

### Real-world example — product IDs
```text
3,1,2,3,2,3
      ↓ sort
1,2,2,3,3,3

[1] [2,2] [3,3,3]
 1     2       3     <- frequencies
```

Run-length scan:
```text
block [i,j)
frequency = j-i
```

This gives frequencies while keeping distinct values sorted.

---

## 14.13 Rank Transformation

Replace magnitude by relative order.

### Real-world example — race times
```text
52,47,60,49
sort distinct -> 47,49,52,60

47 -> rank 1
49 -> rank 2
52 -> rank 3
60 -> rank 4
```
Original sequence:
```text
52,47,60,49
 ↓  ↓  ↓  ↓
 3, 1, 4, 2
```

This is the foundation of coordinate compression when only `<`, `>`, and `=` relationships matter.

---

## 14.14 Contribution after Sorting

For sorted:
```text
a[0] <= ... <= a[n-1]
```

### Sum of pairwise maxima
`a[j]` is maximum with each earlier value, so:
```text
count = j
contribution = a[j]*j

sumMax = Σ a[j]*j
```

### Step-by-step example
```text
[2,5,9]

pairs:
(2,5) -> max 5
(2,9) -> max 9
(5,9) -> max 9

direct = 5+9+9 = 23
```
Contribution:
```text
2*0 = 0
5*1 = 5
9*2 = 18
total = 23
```

### Sum of pairwise minima
`a[i]` is minimum with every later element:
```text
count = n-i-1

sumMin = Σ a[i]*(n-i-1)
```
For `[2,5,9]`:
```text
2*2 + 5*1 + 9*0 = 9
```
Direct:
```text
min(2,5)+min(2,9)+min(5,9)
=2+2+5
=9
```

---

## 14.15 Rearrangement and Pairing

For sorted:
```text
a1<=...<=an
b1<=...<=bn
```
same-order pairing maximizes `Σ ai*bi`; opposite-order pairing minimizes it.

### Real-world numerical example
```text
quantities = 1,2,3
prices     = 10,20,30
```
Same order:
```text
1*10 + 2*20 + 3*30
=10+40+90
=140
```
Opposite:
```text
1*30 + 2*20 + 3*10
=30+40+30
=100
```

Exchange identity for `a<=b`, `x<=y`:
```text
(ax+by)-(ay+bx)
=(b-a)(y-x)
>=0
```
This algebra explains why large-with-large increases the product sum.

---

## 14.16 Sorting by Custom Key

Sometimes the useful order is not raw numeric value.

### Real-world example — jobs by deadline
```text
A deadline 8
B deadline 3
C deadline 5
```
Sort by deadline:
```text
B 3
C 5
A 8
```

Now:
```text
deadline[0] <= deadline[1] <= deadline[2]
```

Common keys:
```text
interval start
interval end
deadline
time
pair.first / pair.second
```

The comparator must define a consistent strict ordering.

---

## 14.17 Sort Events to Transform Time

Unordered events can be transformed into a chronological sweep.

### Real-world example — bank transactions
```text
time 15 -> -20
time  3 -> +50
time 10 -> -10
```
Sort:
```text
3  -> +50
10 -> -10
15 -> -20
```
Starting balance `100`:
```text
time 3:  100+50 = 150
time 10: 150-10 = 140
time 15: 140-20 = 120
```

Transformation:
```text
unordered events
   ↓ sort by time
chronological sequence
   ↓
prefix state / sweep line
```

---

## 14.18 Sorting Removes Irrelevant Permutations

If order is irrelevant:
```text
[3,1,2]
[2,3,1]
[1,2,3]
```
all represent the same multiset.

Sort all:
```text
[1,2,3]
```

### Real-world example — coins in a pocket
```text
1,5,1,2
```
Physical order is irrelevant. Canonical state:
```text
1,1,2,5
```

Sorting can therefore convert many equivalent permutations into one canonical representation.

---

## 14.19 When Sorting Is Invalid

Sorting destroys original position.

```text
original = [3,1,2]
sorted   = [1,2,3]
```

Changed information:
```text
indices
adjacency
prefixes
subarrays
inversions
original sequence
```

### Real-world example — queue
```text
Alice -> Bob -> Carol
```
Alphabetically sorting names changes who arrived first.

If index is still needed, sort:
```text
(value, originalIndex)
```
instead of values alone.

**Safety question:** `Does original order carry information required by the answer?`

---

## 14.20 60-Second Discovery Workflow

```text
PROBLEM
   |
   v
Does original order matter?
   |
 +---+---+
YES     NO / MAYBE
 |          |
preserve    SORT?
index       |
            v
  What structure appears?
  /    |      |       \
rank adjacency monotonic extremes
 |      |       |       |
k-th  min-gap binary   pairing
median        search
        \      |      /
             solve
```

Ask:
```text
1. Do I need k-th/min/max/median?
2. Do closest values become adjacent?
3. Does sorting enable two pointers?
4. Does sorting create a binary-search boundary?
5. Can prefix sums answer smallest-k queries?
6. Are intervals/events easier chronologically?
7. Does sorted index reveal contribution count?
8. Must original indices be preserved?
```

---

## 14.21 Codeforces Recognition Map

| Statement clue | Transformation |
|---|---|
| k-th smallest/largest | sort + rank |
| closest two values | sort + adjacent gaps |
| minimize `Σ|ai-x|` | sort + median |
| balance extremes | smallest + largest pairing |
| max count under budget | cheapest-first |
| sum smallest k | sort + prefix |
| closest threshold value | sort + binary search |
| pair sum | sort + two pointers |
| overlapping intervals | sort + merge |
| duplicate groups | sort + run lengths |
| coordinate rank | sort distinct |
| pairwise min/max total | sorted contribution |
| optimize product pairing | rearrangement |
| timed events | sort + sweep |
| irrelevant permutation | canonical sorting |

---

## 14.22 Common Mistakes

1. **Destroying required indices** — store `(value,index)`.
2. **Sorting just for one min/max** — an `O(n)` scan is enough.
3. **Using adjacent-gap reasoning before sorting**.
4. **Assuming sorting proves a greedy** — the greedy still needs an exchange/invariant proof.
5. **32-bit overflow** in prefix/contribution/product formulas — use `long long`.
6. **Invalid comparator** — it must define strict consistent ordering.
7. **Removing duplicates accidentally** during compression when multiplicity matters.

---

## 14.23 Fast Revision Card

```text
====================================================
SORTING AS A MATHEMATICAL TRANSFORMATION
====================================================

unordered
   ↓ sort
a[0] <= a[1] <= ... <= a[n-1]

WHAT SORTING CREATES
- rank
- endpoints
- adjacency
- monotonicity
- canonical order

k-th smallest = a[k-1]
min = a[0]
max = a[n-1]

MIN PAIR DIFFERENCE
min adjacent gap

MIN Σ|ai-x|
x = median

CHEAPEST k / BUDGET
sort ascending + greedy/prefix

PAIR SUM
sort + two pointers

THRESHOLD SEARCH
sort + binary search

INTERVALS
sort by start + merge

PAIRWISE MAX
Σ a[j]*j

PAIRWISE MIN
Σ a[i]*(n-i-1)

REARRANGEMENT
same order     -> max product sum
opposite order -> min product sum

SAFETY
Does original order matter?
YES -> preserve index / do not destructively sort.

CORE QUESTION
"What mathematical structure appears
after a[0] <= ... <= a[n-1]?"
====================================================
```
