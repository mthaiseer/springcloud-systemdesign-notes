# Part 13 — Frequency Modeling

> **Goal:** replace repeated values and unnecessary order with counts, then model the answer from those counts.
>
> **Contest workflow:** `Sequence → Does order matter? → freq[value] → Contribution of frequency f → Combine`
>
> **Core question:** **Do I need the original sequence, or only how many times each value occurs?**

## Table of Contents

- [13.0 Frequency Modeling Mental Model](#130-frequency-modeling-mental-model)
- [13.1 Sequence to Frequency](#131-sequence-to-frequency)
- [13.2 Presence vs Frequency](#132-presence-vs-frequency)
- [13.3 Equal Pairs](#133-equal-pairs)
- [13.4 Unequal Pairs](#134-unequal-pairs)
- [13.5 Triples and General Combinations](#135-triples-and-general-combinations)
- [13.6 Unique and Duplicate Values](#136-unique-and-duplicate-values)
- [13.7 Frequency Thresholds and Majority](#137-frequency-thresholds-and-majority)
- [13.8 Frequency Modulo k](#138-frequency-modulo-k)
- [13.9 Pair Cancellation](#139-pair-cancellation)
- [13.10 Comparing Two Multisets](#1310-comparing-two-multisets)
- [13.11 Frequency Contribution](#1311-frequency-contribution)
- [13.12 String Frequencies](#1312-string-frequencies)
- [13.13 Prefix Frequency](#1313-prefix-frequency)
- [13.14 Sliding-Window Frequency](#1314-sliding-window-frequency)
- [13.15 Coordinate Compression plus Frequency](#1315-coordinate-compression-plus-frequency)
- [13.16 Frequency plus Sorting](#1316-frequency-plus-sorting)
- [13.17 Choosing a Frequency Data Structure](#1317-choosing-a-frequency-data-structure)
- [13.18 When Frequency Modeling Is Invalid](#1318-when-frequency-modeling-is-invalid)
- [13.19 60-Second Discovery Workflow](#1319-60-second-discovery-workflow)
- [13.20 Codeforces Recognition Map](#1320-codeforces-recognition-map)
- [13.21 Common Mistakes](#1321-common-mistakes)
- [13.22 Fast Revision Card](#1322-fast-revision-card)

---

## 13.0 Frequency Modeling Mental Model

### Real-world example — restaurant kitchen

Orders:

```text
pizza, burger, pizza, salad, burger, pizza
```

Kitchen needs:

```text
pizza  = 3
burger = 2
salad  = 1
```

Order no longer matters.

### Mathematical transformation

```text
sequence
   |
   v
frequency[value]
```

For any value `x`:

```text
freq[x] = number of indices i where A[i] = x
```

Sanity check:

```text
sum of all frequencies = n
```

### Visual

```text
[pizza, burger, pizza, salad, burger, pizza]
                     |
                     v
            pizza  -> 3
            burger -> 2
            salad  -> 1
```

### Memory hook

```text
If shuffling the array does not change the answer,
frequency modeling should be one of your first thoughts.
```

---

## 13.1 Sequence to Frequency

Array:

```text
A = [4,2,4,1,2,4]
```

Scan:

```text
4 -> 1
2 -> 1
4 -> 2
1 -> 1
2 -> 2
4 -> 3
```

Final:

```text
1 -> 1
2 -> 2
4 -> 3
```

### Real-world example — supermarket inventory scan

```text
milk, bread, milk, eggs, bread, milk
```

becomes:

```text
milk  -> 3
bread -> 2
eggs  -> 1
```

### Complexity

```text
build frequencies: O(n)
```

Afterward, questions about multiplicity operate on counts rather than the original sequence.

---

## 13.2 Presence vs Frequency

Presence asks:

```text
Does x occur?
```

Frequency asks:

```text
How many times does x occur?
```

### Real-world example — event

Security:

```text
"Is Alice on the guest list?"
```

Only presence is needed.

Catering:

```text
"How many vegetarian meals?"
```

Frequency is needed.

### Modeling

```text
presence[x] = freq[x] > 0
```

Use the weakest representation sufficient for the problem.

---

## 13.3 Equal Pairs

If a value occurs `f` times, choose any two occurrences:

```text
pairs = C(f,2)
      = f(f-1)/2
```

### Step-by-step derivation

For `f=4` burger orders:

```text
B1 B2 B3 B4
```

Pairs:

```text
B1-B2
B1-B3
B1-B4
B2-B3
B2-B4
B3-B4
```

Total:

```text
6
```

Formula:

```text
4*3/2 = 6
```

### Important distinction

```text
burger frequency = 4
burger equal-pair count = 6
```

These are different quantities.

### Whole-array formula

```text
equal pairs
= sum over x of C(freq[x],2)
```

### Why divide by 2?

```text
f(f-1)
```

counts:

```text
(B1,B2)
(B2,B1)
```

as different ordered pairs.

For unordered pairs, each pair was counted twice:

```text
f(f-1)/2
```

---

## 13.4 Unequal Pairs

Count everything, then subtract equal pairs.

```text
total pairs = C(n,2)
equal pairs = sum C(freq[x],2)

unequal pairs
= C(n,2) - sum C(freq[x],2)
```

### Real-world example — shirt colors

```text
R R B G
```

Total people:

```text
n=4
```

All pairs:

```text
C(4,2)=6
```

Equal-color pairs:

```text
R -> C(2,2)=1
B -> 0
G -> 0
```

Different-color pairs:

```text
6-1=5
```

### Pattern

```text
desired = total - bad
```

Frequency often makes the "bad" equal cases easy to count.

---

## 13.5 Triples and General Combinations

If a value occurs `f` times:

```text
equal triples = C(f,3)
              = f(f-1)(f-2)/6
```

More generally:

```text
choose r equal occurrences = C(f,r)
```

### Real-world example — choose 3 same-size boxes

There are `5` medium boxes.

```text
C(5,3)
= 5*4*3 / (3*2*1)
= 10
```

### Frequency model

```text
answer = sum over x of C(freq[x],r)
```

when each value class can be processed independently.

---

## 13.6 Unique and Duplicate Values

```text
unique:
freq[x] = 1

duplicated:
freq[x] >= 2
```

### Real-world example — badge scans

```text
A17, B20, C11, A17, D09
```

Frequency:

```text
A17 -> 2
B20 -> 1
C11 -> 1
D09 -> 1
```

So:

```text
unique values = 3
duplicated values = 1
```

### Duplicates beyond the first

For frequency `f`:

```text
extra copies = max(0,f-1)
```

Globally:

```text
extra copies
= n - number_of_distinct_values
```

---

## 13.7 Frequency Thresholds and Majority

Threshold:

```text
freq[x] >= k
```

### Real-world example — bulk discount

Discount requires at least `5` units.

```text
coffee -> 7 -> qualifies
tea    -> 3 -> no
milk   -> 5 -> qualifies
```

### Strict majority

A value is a strict majority if:

```text
freq[x] > n/2
```

Safer integer comparison:

```text
2*freq[x] > n
```

### Real-world example — vote

```text
A A B A C A A
```

Here:

```text
n=7
freq[A]=5

2*5 > 7
```

So A has a strict majority.

### Warning

Most frequent does not automatically mean majority.

---

## 13.8 Frequency Modulo k

If groups of `k` equal objects can be removed:

```text
freq[x] = q*k + r
```

Only:

```text
r = freq[x] mod k
```

remains after removing all complete groups.

### Real-world example — boxes of 3 socks

Black socks:

```text
8
```

Pack in groups of 3:

```text
8 = 2*3 + 2
```

Therefore:

```text
complete groups = 2
leftovers = 2
```

### CF trigger

```text
remove k equal values
make groups of k
frequency divisible by k
leftover copies
```

---

## 13.9 Pair Cancellation

For cancellation in pairs:

```text
pairs = floor(f/2)
leftover = f mod 2
```

### Real-world example — matching socks

```text
black = 7
white = 4
blue  = 3
```

Black:

```text
7 = 3*2 + 1
3 pairs, 1 leftover
```

White:

```text
4 = 2*2 + 0
2 pairs, 0 leftover
```

Blue:

```text
3 = 1*2 + 1
1 pair, 1 leftover
```

### Mathematical compression

After all equal-pair cancellation, only:

```text
freq[x] mod 2
```

matters.

This is the bridge between frequency modeling and parity/XOR reasoning.

---

## 13.10 Comparing Two Multisets

For collections A and B:

```text
delta[x] = freqA[x] - freqB[x]
```

Interpret:

```text
delta[x] > 0 -> surplus
delta[x] < 0 -> deficit
delta[x] = 0 -> balanced
```

### Real-world example — warehouse vs order

Stock:

```text
apple  = 5
banana = 2
orange = 4
```

Required:

```text
apple  = 3
banana = 4
orange = 4
```

Difference:

```text
apple  -> +2 surplus
banana -> -2 deficit
orange ->  0 balanced
```

### Visual

```text
freqStock[x] - freqNeed[x]
             |
       +-----+-----+
       |           |
    positive     negative
    surplus      deficit
```

No occurrence-by-occurrence matching is necessary.

---

## 13.11 Frequency Contribution

Many answers have the form:

```text
answer = sum over distinct x of g(freq[x])
```

### Real-world example — reward by sales volume

Counts:

```text
A -> 3
B -> 2
C -> 1
```

Suppose reward is `frequency^2`.

```text
A -> 3^2 = 9
B -> 2^2 = 4
C -> 1^2 = 1
```

Total:

```text
9+4+1=14
```

### Common CF contribution functions

```text
f
f-1
f/2
f%2
C(f,2)
C(f,3)
f*f
min(f,k)
max(0,f-k)
```

### Recognition question

```text
"What does ONE value occurring f times contribute?"
```

Solve that, then sum over values.

---

## 13.12 String Frequencies

For lowercase letters:

```text
freq[26]
```

Index:

```text
c-'a'
```

### Real-world example — letter tiles

```text
BANANA
```

Frequency:

```text
A -> 3
B -> 1
N -> 2
```

### Anagram model

Two strings are anagrams exactly when every character count matches.

```text
freqS[c] = freqT[c]
for every character c
```

Example:

```text
LISTEN
SILENT
```

Order changes; frequency does not.

---

## 13.13 Prefix Frequency

For many range-count queries:

```text
pref[i][x]
= number of x values in first i elements
```

For 0-based inclusive range `[L,R]`:

```text
count(x,L,R)
= pref[R+1][x] - pref[L][x]
```

### Real-world example — attendance

```text
P A P P A P
```

Prefix count of `P`:

```text
0 1 1 2 3 3 4
```

Query human days 2..5 = indices `[1,4]`:

```text
pref[5]-pref[1]
=3-1
=2
```

### Complexity

```text
preprocessing: O(n)
query: O(1)
```

for a fixed tracked value / bounded alphabet.

---

## 13.14 Sliding-Window Frequency

When a fixed-size window moves, update counts incrementally.

```text
freq[outgoing]--
freq[incoming]++
```

### Real-world example — last 3 orders

```text
P B P S B
```

First window:

```text
[P B P]

P=2
B=1
```

Slide:

```text
remove P
add S
```

New window:

```text
[B P S]

P=1
B=1
S=1
```

### Complexity transformation

```text
rebuild each window: O(n*k)
             ↓
update two counts: O(n)
```

---

## 13.15 Coordinate Compression plus Frequency

Large values may make direct frequency arrays impossible.

Example:

```text
[1000000000,5,1000000000,42]
```

Distinct sorted:

```text
[5,42,1000000000]
```

Compress:

```text
5          -> 0
42         -> 1
1000000000 -> 2
```

Array becomes:

```text
[2,0,2,1]
```

Frequency:

```text
0 -> 1
1 -> 1
2 -> 2
```

### Real-world analogy

Huge customer IDs are replaced by compact internal IDs only for customers that actually exist.

---

## 13.16 Frequency plus Sorting

Frequency removes repeated occurrences; sorting preserves value order among distinct keys.

### Real-world example — inventory report

Scans:

```text
50,10,50,20,10,50
```

Frequency:

```text
10 -> 2
20 -> 1
50 -> 3
```

Sorted representation:

```text
(10,2)
(20,1)
(50,3)
```

### Use when

The answer depends on:

```text
frequency
+
relative value order
```

Examples include processing smallest/largest distinct values or neighboring distinct values.

---

## 13.17 Choosing a Frequency Data Structure

```text
Are values small and bounded?
       |
   +---+---+
  YES      NO
   |        |
vector    Need sorted keys?
            |
         +--+--+
        YES    NO
         |      |
        map  unordered_map
```

### Real-world analogy

Apartment numbers `1..100`:

```text
fixed array is natural
```

Sparse customer IDs such as:

```text
42
1000000007
900000000000
```

should not allocate every possible ID.

### Typical choices

```text
vector<int> freq(M+1)
map<long long,int>
unordered_map<long long,int>
```

---

## 13.18 When Frequency Modeling Is Invalid

Frequency discards positions and order.

### Real-world example — queue order

```text
A B A
A A B
```

Both have:

```text
A=2
B=1
```

But the service order differs.

### CP example

```text
[1,2,1]
[1,1,2]
```

Same frequency, different:

```text
adjacency
prefixes
inversions
subarrays
positions
```

### Warning signs

Frequency alone is insufficient for:

```text
subarrays
adjacent relationships
first/last occurrence
distance between equal values
inversions
position-constrained subsequences
```

### Best sanity test

```text
"If I arbitrarily shuffle the input,
does the answer remain unchanged?"
```

If no, preserve positional information.

---

## 13.19 60-Second Discovery Workflow

```text
PROBLEM
   |
   v
Does order matter?
   |
 +---+---+
YES     NO
 |       |
keep    freq[value]
position    |
info        v
       What does one
       frequency f do?
          / | | \
         /  | |  \
      C(f,2) f%2 f/k threshold
           \ | /
             v
          combine
```

### Fast questions

```text
1. Can I shuffle the array without changing the answer?
2. Are equal values interchangeable?
3. Do I need counts rather than positions?
4. What does one value with frequency f contribute?
5. Is only f mod 2 or f mod k relevant?
6. Do I need frequencies inside ranges/windows?
7. Are values too large for a direct frequency array?
```

---

## 13.20 Codeforces Recognition Map

| Statement clue | First frequency model |
|---|---|
| count occurrences | `freq[x]` |
| does value exist | presence |
| equal pairs | `C(freq[x],2)` |
| unequal pairs | `C(n,2)-sum C(freq[x],2)` |
| equal triples | `C(freq[x],3)` |
| duplicates | `freq[x]>=2` |
| unique | `freq[x]==1` |
| majority | `2*freq[x]>n` |
| groups of k | `freq[x]/k`, `freq[x]%k` |
| cancel pairs | `freq[x]/2`, `freq[x]%2` |
| compare multisets | `freqA[x]-freqB[x]` |
| anagram | character frequencies |
| range count queries | prefix frequency |
| moving fixed window | sliding-window frequency |
| huge sparse values | map/compression |
| frequency + value order | map/sort distinct keys |

---

## 13.21 Common Mistakes

### Mistake 1 — frequency is not pair count

```text
f=4
```

means four occurrences.

Equal pairs:

```text
C(4,2)=6
```

### Mistake 2 — forgetting `/2`

```text
f(f-1)
```

counts ordered pairs.

Unordered:

```text
f(f-1)/2
```

### Mistake 3 — integer overflow

For large `f`, use:

```text
long long
```

for formulas such as:

```text
f(f-1)/2
```

### Mistake 4 — discarding needed order

Same frequencies do not imply same subarrays, inversions, adjacency, or positions.

### Mistake 5 — giant direct array

For values near `10^9`, use a map/hash map or coordinate compression.

---

## 13.22 Fast Revision Card

```text
========================================================
PART 13 — FREQUENCY MODELING
========================================================

CORE
sequence -> freq[value]

DEFINITION
freq[x] = number of occurrences of x

CHECK
sum freq[x] = n

PRESENCE
freq[x] > 0

UNIQUE
freq[x] = 1

DUPLICATE
freq[x] >= 2

EQUAL PAIRS
C(f,2)=f(f-1)/2

UNEQUAL PAIRS
C(n,2)-sum C(freq[x],2)

EQUAL TRIPLES
C(f,3)=f(f-1)(f-2)/6

GROUPS OF k
groups=f/k
leftover=f%k

PAIR CANCELLATION
pairs=f/2
leftover=f%2

MAJORITY
2*f > n

MULTISET DIFFERENCE
delta[x]=freqA[x]-freqB[x]

CONTRIBUTION
answer=sum g(freq[x])

PREFIX FREQUENCY
count(x,L,R)=pref[R+1][x]-pref[L][x]

SLIDING WINDOW
freq[out]--
freq[in]++

KEY TEST
"If I shuffle the array,
does the answer stay the same?"

YES -> frequency modeling is promising.
NO  -> preserve positional information.

CORE QUESTION
"Do I need the sequence,
or only how many of each value?"
========================================================
```
