# 01 --- CF Mathematical Modeling Phase 1: Decode

> **130 guided Codeforces statement-decoding drills.**
>
> Only one skill is trained here:
>
> `story → remove nouns → extract variables → formulate mathematics → derive solution → dry run`

For every problem, read the official statement first. Then use the short
decode below. There is intentionally **no pseudocode, no C++ code, no
generic theory, no repeated recognition checklist, and no extra everyday
analogy**. The goal is to make the mathematical extraction itself
automatic.

## Table of Contents

-   [Pattern 1 --- Minimum Operations / Ceil
    Division](#pattern-1-minimum-operations-ceil-division)
    -   [CF 617A --- Elephant --- 800](#problem-001-cf-617a-elephant)
    -   [CF 1409A --- Yet Another Two Integers Problem ---
        800](#problem-002-cf-1409a-yet-another-two-integers-problem)
    -   [CF 1353A --- Most Unstable Array ---
        800](#problem-003-cf-1353a-most-unstable-array)
    -   [CF 1476A --- K-divisible Sum ---
        1000](#problem-004-cf-1476a-k-divisible-sum)
    -   [CF 151A --- Soft Drinking ---
        800](#problem-005-cf-151a-soft-drinking)
    -   [CF 996A --- Hit the Lottery ---
        800](#problem-006-cf-996a-hit-the-lottery)
    -   [CF 1669A --- Division? --- 800](#problem-007-cf-1669a-division)
    -   [CF 1742A --- Sum --- 800](#problem-008-cf-1742a-sum)
    -   [CF 1850A --- To My Critics ---
        800](#problem-009-cf-1850a-to-my-critics)
    -   [CF 1878A --- How Much Does Daytona Cost? ---
        800](#problem-010-cf-1878a-how-much-does-daytona-cost)
-   [Pattern 2 --- Algebra / Equation
    Formation](#pattern-2-algebra-equation-formation)
    -   [CF 734A --- Anton and Danik ---
        800](#problem-011-cf-734a-anton-and-danik)
    -   [CF 677A --- Vanya and Fence ---
        800](#problem-012-cf-677a-vanya-and-fence)
    -   [CF 71A --- Way Too Long Words ---
        800](#problem-013-cf-71a-way-too-long-words)
    -   [CF 791A --- Bear and Big Brother ---
        800](#problem-014-cf-791a-bear-and-big-brother)
    -   [CF 50A --- Domino piling ---
        800](#problem-015-cf-50a-domino-piling)
    -   [CF 231A --- Team --- 800](#problem-016-cf-231a-team)
    -   [CF 200B --- Drinks --- 800](#problem-017-cf-200b-drinks)
    -   [CF 318A --- Even Odds --- 900](#problem-018-cf-318a-even-odds)
    -   [CF 486A --- Calculating Function ---
        800](#problem-019-cf-486a-calculating-function)
    -   [CF 1399A --- Remove Smallest ---
        800](#problem-020-cf-1399a-remove-smallest)
-   [Pattern 3 --- Bounds / Inequalities /
    Min-Max](#pattern-3-bounds-inequalities-min-max)
    -   [CF 1690A --- Print a Pedestal ---
        800](#problem-021-cf-1690a-print-a-pedestal)
    -   [CF 1676A --- Lucky? --- 800](#problem-022-cf-1676a-lucky)
    -   [CF 1742B --- Increasing ---
        800](#problem-023-cf-1742b-increasing)
    -   [CF 1791A --- Codeforces Checking ---
        800](#problem-024-cf-1791a-codeforces-checking)
    -   [CF 1829A --- Love Story ---
        800](#problem-025-cf-1829a-love-story)
    -   [CF 1873A --- Short Sort ---
        800](#problem-026-cf-1873a-short-sort)
    -   [CF 1729A --- Two Elevators ---
        800](#problem-027-cf-1729a-two-elevators)
    -   [CF 1805A --- We Need the Zero ---
        900](#problem-028-cf-1805a-we-need-the-zero)
    -   [CF 1858A --- Buttons --- 800](#problem-029-cf-1858a-buttons)
    -   [CF 1899A --- Game with Integers ---
        800](#problem-030-cf-1899a-game-with-integers)
-   [Pattern 4 --- Parity Modeling](#pattern-4-parity-modeling)
    -   [CF 4A --- Watermelon --- 800](#problem-031-cf-4a-watermelon)
    -   [CF 1296A --- Array with Odd Sum ---
        800](#problem-032-cf-1296a-array-with-odd-sum)
    -   [CF 1857A --- Array Coloring ---
        800](#problem-033-cf-1857a-array-coloring)
    -   [CF 1834A --- Unit Array ---
        800](#problem-034-cf-1834a-unit-array)
    -   [CF 1367B --- Even Array ---
        800](#problem-035-cf-1367b-even-array)
    -   [CF 1475A --- Odd Divisor ---
        900](#problem-036-cf-1475a-odd-divisor)
    -   [CF 1669C --- Odd/Even Increments ---
        800](#problem-037-cf-1669c-oddeven-increments)
    -   [CF 1624A --- Plus One on the Subset ---
        800](#problem-038-cf-1624a-plus-one-on-the-subset)
    -   [CF 1788A --- One and Two ---
        800](#problem-039-cf-1788a-one-and-two)
    -   [CF 1845A --- Forbidden Integer ---
        800](#problem-040-cf-1845a-forbidden-integer)
-   [Pattern 5 --- Divisibility / GCD /
    LCM](#pattern-5-divisibility-gcd-lcm)
    -   [CF 1328A --- Divisibility Problem ---
        800](#problem-041-cf-1328a-divisibility-problem)
    -   [CF 1343A --- Candies --- 900](#problem-042-cf-1343a-candies)
    -   [CF 1370A --- Maximum GCD ---
        800](#problem-043-cf-1370a-maximum-gcd)
    -   [CF 1829C --- Mr. Perfectly Fine ---
        800](#problem-044-cf-1829c-mr-perfectly-fine)
    -   [CF 1618A --- Polycarp and Sums of Subsequences ---
        800](#problem-045-cf-1618a-polycarp-and-sums-of-subsequences)
    -   [CF 160A --- Twins --- 900](#problem-046-cf-160a-twins)
    -   [CF 1475B --- New Year's Number ---
        900](#problem-047-cf-1475b-new-years-number)
    -   [CF 1593A --- Elections ---
        800](#problem-048-cf-1593a-elections)
    -   [CF 1829B --- Blank Space ---
        800](#problem-049-cf-1829b-blank-space)
    -   [CF 1877A --- Goals of Victory ---
        800](#problem-050-cf-1877a-goals-of-victory)
-   [Pattern 6 --- Modulo / Cyclic
    Modeling](#pattern-6-modulo-cyclic-modeling)
    -   [CF 116A --- Tram --- 800](#problem-051-cf-116a-tram)
    -   [CF 266A --- Stones on the Table ---
        800](#problem-052-cf-266a-stones-on-the-table)
    -   [CF 228A --- Is your horseshoe on the other hoof? ---
        800](#problem-053-cf-228a-is-your-horseshoe-on-the-other-hoof)
    -   [CF 443A --- Anton and Letters ---
        800](#problem-054-cf-443a-anton-and-letters)
    -   [CF 59A --- Word --- 800](#problem-055-cf-59a-word)
    -   [CF 236A --- Boy or Girl ---
        800](#problem-056-cf-236a-boy-or-girl)
    -   [CF 785A --- Anton and Polyhedrons ---
        800](#problem-057-cf-785a-anton-and-polyhedrons)
    -   [CF 703A --- Mishka and Game ---
        800](#problem-058-cf-703a-mishka-and-game)
    -   [CF 734B --- Anton and Digits ---
        800](#problem-059-cf-734b-anton-and-digits)
    -   [CF 1097A --- Gennady the Card Game ---
        800](#problem-060-cf-1097a-gennady-the-card-game)
-   [Pattern 7 --- Counting / Frequency /
    Pairs](#pattern-7-counting-frequency-pairs)
    -   [CF 1520D --- Same Differences ---
        1200](#problem-061-cf-1520d-same-differences)
    -   [CF 1538C --- Challenging Cliffs / Number of Pairs ---
        1300](#problem-062-cf-1538c-challenging-cliffs-number-of-pairs)
    -   [CF 1669B --- Triple --- 800](#problem-063-cf-1669b-triple)
    -   [CF 1742C --- Stripes --- 800](#problem-064-cf-1742c-stripes)
    -   [CF 1791B --- Following Directions ---
        800](#problem-065-cf-1791b-following-directions)
    -   [CF 1703B --- ICPC Balloons ---
        800](#problem-066-cf-1703b-icpc-balloons)
    -   [CF 1722A --- Spell Check ---
        800](#problem-067-cf-1722a-spell-check)
    -   [CF 1791C --- Prepend and Append ---
        800](#problem-068-cf-1791c-prepend-and-append)
    -   [CF 1829D --- Gold Rush ---
        1000](#problem-069-cf-1829d-gold-rush)
    -   [CF 1878B --- Aleksa and Stack ---
        800](#problem-070-cf-1878b-aleksa-and-stack)
-   [Pattern 8 --- Operation → Delta →
    Invariant](#pattern-8-operation-delta-invariant)
    -   [CF 1538B --- Friends and Candies ---
        800](#problem-071-cf-1538b-friends-and-candies)
    -   [CF 1855A --- Dalton the Teacher ---
        800](#problem-072-cf-1855a-dalton-the-teacher)
    -   [CF 1838A --- Blackboard List ---
        800](#problem-073-cf-1838a-blackboard-list)
    -   [CF 1862B --- Sequence Game ---
        800](#problem-074-cf-1862b-sequence-game)
    -   [CF 1798A --- Showstopper ---
        800](#problem-075-cf-1798a-showstopper)
    -   [CF 660A --- Co-prime Array ---
        900](#problem-076-cf-660a-co-prime-array)
    -   [CF 1367A --- Short Substrings ---
        800](#problem-077-cf-1367a-short-substrings)
    -   [CF 1374A --- Required Remainder ---
        800](#problem-078-cf-1374a-required-remainder)
    -   [CF 1551A --- Polycarp and Coins ---
        800](#problem-079-cf-1551a-polycarp-and-coins)
    -   [CF 1818A --- Politics --- 800](#problem-080-cf-1818a-politics)
-   [Pattern 9 --- Sorting / Coordinate / Distance
    Modeling](#pattern-9-sorting-coordinate-distance-modeling)
    -   [CF 160A --- Twins --- 900](#problem-081-cf-160a-twins)
    -   [CF 1399A --- Remove Smallest ---
        800](#problem-082-cf-1399a-remove-smallest)
    -   [CF 1760A --- Medium Number ---
        800](#problem-083-cf-1760a-medium-number)
    -   [CF 1538A --- Stone Game ---
        800](#problem-084-cf-1538a-stone-game)
    -   [CF 1729A --- Two Elevators ---
        800](#problem-085-cf-1729a-two-elevators)
    -   [CF 1593B --- Make it Divisible by 25 ---
        900](#problem-086-cf-1593b-make-it-divisible-by-25)
    -   [CF 1742F --- Smaller --- 1200](#problem-087-cf-1742f-smaller)
    -   [CF 1831A --- Twin Permutations ---
        800](#problem-088-cf-1831a-twin-permutations)
    -   [CF 1900A --- Cover in Water ---
        800](#problem-089-cf-1900a-cover-in-water)
    -   [CF 1873B --- Good Kid --- 800](#problem-090-cf-1873b-good-kid)
-   [Pattern 10 --- Prefix / Running-State
    Modeling](#pattern-10-prefix-running-state-modeling)
    -   [CF 116A --- Tram --- 800](#problem-091-cf-116a-tram)
    -   [CF 363B --- Fence --- 1100](#problem-092-cf-363b-fence)
    -   [CF 276C --- Little Girl and Problem on Trees / Little Girl and
        Maximum Sum ---
        1400](#problem-093-cf-276c-little-girl-and-problem-on-trees-little-girl-and-maximum-sum)
    -   [CF 433B --- Kuriyama Mirai's Stones ---
        1200](#problem-094-cf-433b-kuriyama-mirais-stones)
    -   [CF 313B --- Ilya and Queries ---
        1100](#problem-095-cf-313b-ilya-and-queries)
    -   [CF 327A --- Flipping Game ---
        1200](#problem-096-cf-327a-flipping-game)
    -   [CF 580A --- Kefa and First Steps ---
        900](#problem-097-cf-580a-kefa-and-first-steps)
    -   [CF 702A --- Maximum Increase ---
        800](#problem-098-cf-702a-maximum-increase)
    -   [CF 1829B --- Blank Space ---
        800](#problem-099-cf-1829b-blank-space)
    -   [CF 1669F --- Eating Candies ---
        1100](#problem-100-cf-1669f-eating-candies)
-   [Pattern 11 --- Constructive / Reachability
    Modeling](#pattern-11-constructive-reachability-modeling)
    -   [CF 1690A --- Print a Pedestal ---
        800](#problem-101-cf-1690a-print-a-pedestal)
    -   [CF 1845A --- Forbidden Integer ---
        800](#problem-102-cf-1845a-forbidden-integer)
    -   [CF 1878B --- Aleksa and Stack ---
        800](#problem-103-cf-1878b-aleksa-and-stack)
    -   [CF 1741A --- Compare T-Shirt Sizes ---
        800](#problem-104-cf-1741a-compare-t-shirt-sizes)
    -   [CF 1805B --- We Need the Zero / The String Has a Target ---
        800](#problem-105-cf-1805b-we-need-the-zero-the-string-has-a-target)
    -   [CF 1833B --- Restore the Weather ---
        1000](#problem-106-cf-1833b-restore-the-weather)
    -   [CF 1793C --- Dora and Search ---
        1200](#problem-107-cf-1793c-dora-and-search)
    -   [CF 1881A --- Don't Try to Count ---
        800](#problem-108-cf-1881a-dont-try-to-count)
    -   [CF 1858A --- Buttons --- 800](#problem-109-cf-1858a-buttons)
    -   [CF 1899A --- Game with Integers ---
        800](#problem-110-cf-1899a-game-with-integers)
-   [Pattern 12 --- Bitwise / XOR
    Modeling](#pattern-12-bitwise-xor-modeling)
    -   [CF 1805A --- We Need the Zero ---
        900](#problem-111-cf-1805a-we-need-the-zero)

    -   [CF 1872A --- Two Vessels ---
        800](#problem-112-cf-1872a-two-vessels)

    -   [CF 1703A --- YES or YES? ---
        800](#problem-113-cf-1703a-yes-or-yes)

    -   [CF 1624A --- Plus One on the Subset ---
        800](#problem-114-cf-1624a-plus-one-on-the-subset)

    -   [CF 1220A --- Cards --- 900](#problem-115-cf-1220a-cards)

    -   [CF 1362A --- Johnny and Ancient Computer ---
        900](#problem-116-cf-1362a-johnny-and-ancient-computer)

    -   [CF 1095A --- Repeating Cipher ---
        800](#problem-117-cf-1095a-repeating-cipher)

    -   [CF 1324A --- Yet Another Tetris Problem ---
        800](#problem-118-cf-1324a-yet-another-tetris-problem)

    -   [CF 1462A --- Favorite Sequence ---
        800](#problem-119-cf-1462a-favorite-sequence)

    -   ## \[CF 1619A --- Polycarp and Sums of Subsequences / Square String?

        800\](#problem-120-cf-1619a-polycarp-and-sums-of-subsequences-square-string)
-   [Pattern 13 --- Mixed Blind
    Decoding](#pattern-13-mixed-blind-decoding)
    -   [CF 1538C --- Challenging Cliffs / Number of Pairs ---
        1300](#problem-121-cf-1538c-challenging-cliffs-number-of-pairs)
    -   [CF 1475B --- New Year's Number ---
        900](#problem-122-cf-1475b-new-years-number)
    -   [CF 1374A --- Required Remainder ---
        800](#problem-123-cf-1374a-required-remainder)
    -   [CF 1551A --- Polycarp and Coins ---
        800](#problem-124-cf-1551a-polycarp-and-coins)
    -   [CF 1593B --- Make it Divisible by 25 ---
        900](#problem-125-cf-1593b-make-it-divisible-by-25)
    -   [CF 1669F --- Eating Candies ---
        1100](#problem-126-cf-1669f-eating-candies)
    -   [CF 1793C --- Dora and Search ---
        1200](#problem-127-cf-1793c-dora-and-search)
    -   [CF 327A --- Flipping Game ---
        1200](#problem-128-cf-327a-flipping-game)
    -   [CF 1520D --- Same Differences ---
        1200](#problem-129-cf-1520d-same-differences)
    -   [CF 276C --- Little Girl and Maximum Sum ---
        1400](#problem-130-cf-276c-little-girl-and-maximum-sum)

# Pattern 1 --- Minimum Operations / Ceil Division

## Problem 001 --- CF 617A --- Elephant

**Problem Link:** [CF 617A ---
Elephant](https://codeforces.com/problemset/problem/617/A)\
**Topic / Rating:** Arithmetic / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
x

ACTUAL QUESTION:
minimum moves to reach x with +1..+5
```

### 2. Extract Variables

``` text
Given:
x

Useful mathematical state:
D=x, K=5

Unknown / target:
minimum moves to reach x with +1..+5
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
5m >= x

      ↓ simplify / transform

ceil(x/5)
```

### 4. Derive the Solution

``` text
D=x, K=5
      ↓
5m >= x
      ↓
ceil(x/5)
      ↓
(x+4)/5
```

**Final model:** `5m >= x` → **ceil(x/5)**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Example: `x = 17`

``` text
Start = 0
Target = 17
Maximum per move = 5

Need:
5m >= 17

m >= 17/5
m >= 3.4

m is integer
→ m = 4

Check:
0 → 5 → 10 → 15 → 17

4 moves are enough.
3 moves are not enough because 3×5 = 15 < 17.

Answer = 4
```

## Problem 002 --- CF 1409A --- Yet Another Two Integers Problem

**Problem Link:** [CF 1409A --- Yet Another Two Integers
Problem](https://codeforces.com/problemset/problem/1409/A)\
**Topic / Rating:** Arithmetic / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
a,b

ACTUAL QUESTION:
minimum operations to make a=b using ±1..10
```

### 2. Extract Variables

``` text
Given:
a,b

Useful mathematical state:
D=|a-b|, K=10

Unknown / target:
minimum operations to make a=b using ±1..10
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
10m >= |a-b|

      ↓ simplify / transform

ceil(|a-b|/10)
```

### 4. Derive the Solution

``` text
D=|a-b|, K=10
      ↓
10m >= |a-b|
      ↓
ceil(|a-b|/10)
      ↓
(abs(a-b)+9)/10
```

**Final model:** `10m >= |a-b|` → **ceil(\|a-b\|/10)**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Example: `a = 26, b = 9`

``` text
Required change:
D = |26 - 9| = 17

Maximum correction per operation = 10

Need:
10m >= 17

m >= 1.7
→ m = 2

Check:
26 → 16   (-10)
16 → 9    (-7)

Answer = 2
```

## Problem 003 --- CF 1353A --- Most Unstable Array

**Problem Link:** [CF 1353A --- Most Unstable
Array](https://codeforces.com/problemset/problem/1353/A)\
**Topic / Rating:** Formula / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
n,m

ACTUAL QUESTION:
maximize sum of adjacent absolute differences under bounds
```

### 2. Extract Variables

``` text
Given:
n,m

Useful mathematical state:
endpoints/bounds matter

Unknown / target:
maximize sum of adjacent absolute differences under bounds
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
each transition <= m

      ↓ simplify / transform

construct extremal arrangement
```

### 4. Derive the Solution

``` text
endpoints/bounds matter
      ↓
each transition <= m
      ↓
construct extremal arrangement
      ↓
handle n=1,2,>=3
```

**Final model:** `each transition <= m` → **construct extremal
arrangement**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
each transition <= m

     ↓ evaluate / simplify

construct extremal arrangement

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 004
--- CF 1476A --- K-divisible Sum

**Problem Link:** [CF 1476A --- K-divisible
Sum](https://codeforces.com/problemset/problem/1476/A)\
**Topic / Rating:** Bounds / 1000

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
n,k

ACTUAL QUESTION:
minimum possible maximum element while sum is divisible by k
```

### 2. Extract Variables

``` text
Given:
n,k

Useful mathematical state:
total S >= n and S multiple of k

Unknown / target:
minimum possible maximum element while sum is divisible by k
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
S = smallest multiple of k >= n

      ↓ simplify / transform

ceil(S/n)
```

### 4. Derive the Solution

``` text
total S >= n and S multiple of k
      ↓
S = smallest multiple of k >= n
      ↓
ceil(S/n)
      ↓
S=((n+k-1)/k)*k
```

**Final model:** `S = smallest multiple of k >= n` → **ceil(S/n)**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Example: `n = 5, k = 7`

``` text
Need:
S >= 5
S % 7 = 0

Multiples of 7:
7, 14, 21, ...

Smallest valid S = 7

Distribute total 7 over 5 positive elements.

minimum possible maximum
= ceil(7/5)
= 2

Answer = 2
```

## Problem 005 --- CF 151A --- Soft Drinking

**Problem Link:** [CF 151A --- Soft
Drinking](https://codeforces.com/problemset/problem/151/A)\
**Topic / Rating:** Capacity / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
n,k,l,c,d,p,nl,np

ACTUAL QUESTION:
number of toasts per friend
```

### 2. Extract Variables

``` text
Given:
n,k,l,c,d,p,nl,np

Useful mathematical state:
three resources

Unknown / target:
number of toasts per friend
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
min(drink/nl,limes,salt/np)/n

      ↓ simplify / transform

limiting resource
```

### 4. Derive the Solution

``` text
three resources
      ↓
min(drink/nl,limes,salt/np)/n
      ↓
limiting resource
      ↓
take minimum capacity
```

**Final model:** `min(drink/nl,limes,salt/np)/n` → **limiting
resource**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Example with three resources:

``` text
3 friends

drink capacity = 9 toasts
lime capacity  = 6 toasts
salt capacity  = 12 toasts

usable toasts
= min(9, 6, 12)
= 6

per friend
= 6 / 3
= 2

Answer = 2

The smallest resource capacity controls the answer.
```

## Problem 006 --- CF 996A --- Hit the Lottery

**Problem Link:** [CF 996A --- Hit the
Lottery](https://codeforces.com/problemset/problem/996/A)\
**Topic / Rating:** Greedy/Division / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
n

ACTUAL QUESTION:
minimum notes using 100,20,10,5,1
```

### 2. Extract Variables

``` text
Given:
n

Useful mathematical state:
largest denomination dominates

Unknown / target:
minimum notes using 100,20,10,5,1
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
q=n/d

      ↓ simplify / transform

sum quotients
```

### 4. Derive the Solution

``` text
largest denomination dominates
      ↓
q=n/d
      ↓
sum quotients
      ↓
repeated quotient/remainder
```

**Final model:** `q=n/d` → **sum quotients**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Example: `n = 125`

``` text
125 / 100 = 1 note
remainder = 25

25 / 20 = 1 note
remainder = 5

5 / 5 = 1 note
remainder = 0

total = 3 notes
```

The quotient tells how many of the current largest denomination to take;
the remainder becomes the next state. \## Problem 007 --- CF 1669A ---
Division?

**Problem Link:** [CF 1669A ---
Division?](https://codeforces.com/problemset/problem/1669/A)\
**Topic / Rating:** Inequality / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
rating

ACTUAL QUESTION:
classify rating into interval
```

### 2. Extract Variables

``` text
Given:
rating

Useful mathematical state:
numeric boundaries

Unknown / target:
classify rating into interval
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
compare rating with cutoffs

      ↓ simplify / transform

interval classification
```

### 4. Derive the Solution

``` text
numeric boundaries
      ↓
compare rating with cutoffs
      ↓
interval classification
      ↓
if/else
```

**Final model:** `compare rating with cutoffs` → **interval
classification**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
compare rating with cutoffs

     ↓ evaluate / simplify

interval classification

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 008
--- CF 1742A --- Sum

**Problem Link:** [CF 1742A ---
Sum](https://codeforces.com/problemset/problem/1742/A)\
**Topic / Rating:** Equation / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
a,b,c

ACTUAL QUESTION:
whether one number equals sum of other two
```

### 2. Extract Variables

``` text
Given:
a,b,c

Useful mathematical state:
test 3 equations

Unknown / target:
whether one number equals sum of other two
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
a+b=c etc.

      ↓ simplify / transform

direct feasibility
```

### 4. Derive the Solution

``` text
test 3 equations
      ↓
a+b=c etc.
      ↓
direct feasibility
      ↓
three checks
```

**Final model:** `a+b=c etc.` → **direct feasibility**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
a+b=c etc.

     ↓ evaluate / simplify

direct feasibility

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 009
--- CF 1850A --- To My Critics

**Problem Link:** [CF 1850A --- To My
Critics](https://codeforces.com/problemset/problem/1850/A)\
**Topic / Rating:** Bounds / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
a,b,c

ACTUAL QUESTION:
whether any pair sum >=10
```

### 2. Extract Variables

``` text
Given:
a,b,c

Useful mathematical state:
only 3 pairs

Unknown / target:
whether any pair sum >=10
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
max pair sum

      ↓ simplify / transform

sort or direct checks
```

### 4. Derive the Solution

``` text
only 3 pairs
      ↓
max pair sum
      ↓
sort or direct checks
      ↓
a+b>=10 || ...
```

**Final model:** `max pair sum` → **sort or direct checks**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Example:

``` text
values:
[8, 2, 6, 3]

sort:
[2, 3, 6, 8]

Now apply:
max pair sum

which reduces to:
sort or direct checks

Sorting exposes the mathematical order
that was hidden by the input arrangement.
```

## Problem 010 --- CF 1878A --- How Much Does Daytona Cost?

**Problem Link:** [CF 1878A --- How Much Does Daytona
Cost?](https://codeforces.com/problemset/problem/1878/A)\
**Topic / Rating:** Existence / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
n,k,array

ACTUAL QUESTION:
whether k appears
```

### 2. Extract Variables

``` text
Given:
n,k,array

Useful mathematical state:
target is existence

Unknown / target:
whether k appears
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
∃i: a[i]=k

      ↓ simplify / transform

linear scan
```

### 4. Derive the Solution

``` text
target is existence
      ↓
∃i: a[i]=k
      ↓
linear scan
      ↓
found flag
```

**Final model:** `∃i: a[i]=k` → **linear scan**.

------------------------------------------------------------------------

# Pattern 2 --- Algebra / Equation Formation

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
∃i: a[i]=k

     ↓ evaluate / simplify

linear scan

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 011
--- CF 734A --- Anton and Danik

**Problem Link:** [CF 734A --- Anton and
Danik](https://codeforces.com/problemset/problem/734/A)\
**Topic / Rating:** Counting / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
n,string

ACTUAL QUESTION:
who won more games
```

### 2. Extract Variables

``` text
Given:
n,string

Useful mathematical state:
A=count('A'), D=count('D')

Unknown / target:
who won more games
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
compare A and D

      ↓ simplify / transform

sign of A-D
```

### 4. Derive the Solution

``` text
A=count('A'), D=count('D')
      ↓
compare A and D
      ↓
sign of A-D
      ↓
count chars
```

**Final model:** `compare A and D` → **sign of A-D**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
compare A and D

     ↓ evaluate / simplify

sign of A-D

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 012
--- CF 677A --- Vanya and Fence

**Problem Link:** [CF 677A --- Vanya and
Fence](https://codeforces.com/problemset/problem/677/A)\
**Topic / Rating:** Formula / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
n,h,heights

ACTUAL QUESTION:
total width
```

### 2. Extract Variables

``` text
Given:
n,h,heights

Useful mathematical state:
each person contributes 1 or 2

Unknown / target:
total width
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
sum (a[i]>h ? 2:1)

      ↓ simplify / transform

contribution sum
```

### 4. Derive the Solution

``` text
each person contributes 1 or 2
      ↓
sum (a[i]>h ? 2:1)
      ↓
contribution sum
      ↓
linear scan
```

**Final model:** `sum (a[i]>h ? 2:1)` → **contribution sum**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
sum (a[i]>h ? 2:1)

     ↓ evaluate / simplify

contribution sum

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 013
--- CF 71A --- Way Too Long Words

**Problem Link:** [CF 71A --- Way Too Long
Words](https://codeforces.com/problemset/problem/71/A)\
**Topic / Rating:** String/Formula / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
word

ACTUAL QUESTION:
abbreviate if length>10
```

### 2. Extract Variables

``` text
Given:
word

Useful mathematical state:
first + (len-2) + last

Unknown / target:
abbreviate if length>10
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
length condition

      ↓ simplify / transform

direct construction
```

### 4. Derive the Solution

``` text
first + (len-2) + last
      ↓
length condition
      ↓
direct construction
      ↓
O(len)
```

**Final model:** `length condition` → **direct construction**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
length condition

     ↓ evaluate / simplify

direct construction

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 014
--- CF 791A --- Bear and Big Brother

**Problem Link:** [CF 791A --- Bear and Big
Brother](https://codeforces.com/problemset/problem/791/A)\
**Topic / Rating:** Growth / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
a,b

ACTUAL QUESTION:
years until 3^t a > 2^t b
```

### 2. Extract Variables

``` text
Given:
a,b

Useful mathematical state:
simulate multiplicative equation

Unknown / target:
years until 3^t a > 2^t b
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
a*=3,b*=2

      ↓ simplify / transform

first t with a>b
```

### 4. Derive the Solution

``` text
simulate multiplicative equation
      ↓
a*=3,b*=2
      ↓
first t with a>b
      ↓
loop
```

**Final model:** `a*=3,b*=2` → **first t with a\>b**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
a*=3,b*=2

     ↓ evaluate / simplify

first t with a\>b

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 015
--- CF 50A --- Domino piling

**Problem Link:** [CF 50A --- Domino
piling](https://codeforces.com/problemset/problem/50/A)\
**Topic / Rating:** Counting / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
m,n

ACTUAL QUESTION:
max dominoes in grid
```

### 2. Extract Variables

``` text
Given:
m,n

Useful mathematical state:
each domino covers 2 cells

Unknown / target:
max dominoes in grid
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
2x <= mn

      ↓ simplify / transform

floor(mn/2)
```

### 4. Derive the Solution

``` text
each domino covers 2 cells
      ↓
2x <= mn
      ↓
floor(mn/2)
      ↓
m*n/2
```

**Final model:** `2x <= mn` → **floor(mn/2)**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
2x <= mn

     ↓ evaluate / simplify

floor(mn/2)

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 016
--- CF 231A --- Team

**Problem Link:** [CF 231A ---
Team](https://codeforces.com/problemset/problem/231/A)\
**Topic / Rating:** Counting / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
triples

ACTUAL QUESTION:
count problems with >=2 yes
```

### 2. Extract Variables

``` text
Given:
triples

Useful mathematical state:
sum triple >=2

Unknown / target:
count problems with >=2 yes
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
indicator contribution

      ↓ simplify / transform

count
```

### 4. Derive the Solution

``` text
sum triple >=2
      ↓
indicator contribution
      ↓
count
      ↓
linear
```

**Final model:** `indicator contribution` → **count**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
indicator contribution

     ↓ evaluate / simplify

count

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 017
--- CF 200B --- Drinks

**Problem Link:** [CF 200B ---
Drinks](https://codeforces.com/problemset/problem/200/B)\
**Topic / Rating:** Average / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
n,p

ACTUAL QUESTION:
orange percentage
```

### 2. Extract Variables

``` text
Given:
n,p

Useful mathematical state:
average of p

Unknown / target:
orange percentage
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
sum/n

      ↓ simplify / transform

mean
```

### 4. Derive the Solution

``` text
average of p
      ↓
sum/n
      ↓
mean
      ↓
double sum/n
```

**Final model:** `sum/n` → **mean**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
sum/n

     ↓ evaluate / simplify

mean

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 018
--- CF 318A --- Even Odds

**Problem Link:** [CF 318A --- Even
Odds](https://codeforces.com/problemset/problem/318/A)\
**Topic / Rating:** Index Mapping / 900

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
n,k

ACTUAL QUESTION:
kth in odds then evens
```

### 2. Extract Variables

``` text
Given:
n,k

Useful mathematical state:
oddCount=(n+1)/2

Unknown / target:
kth in odds then evens
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
piecewise index mapping

      ↓ simplify / transform

if k<=oddCount
```

### 4. Derive the Solution

``` text
oddCount=(n+1)/2
      ↓
piecewise index mapping
      ↓
if k<=oddCount
      ↓
formula
```

**Final model:** `piecewise index mapping` → **if k\<=oddCount**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Use a small valid input and keep only parity:

``` text
even → 0
odd  → 1

Substitute those states into:
piecewise index mapping

Then simplify to:
if k\<=oddCount

This shows why the exact magnitudes can be discarded
when only odd/even behavior affects the answer.
```

## Problem 019 --- CF 486A --- Calculating Function

**Problem Link:** [CF 486A --- Calculating
Function](https://codeforces.com/problemset/problem/486/A)\
**Topic / Rating:** Formula / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
n

ACTUAL QUESTION:
alternating sum -1+2-3+...
```

### 2. Extract Variables

``` text
Given:
n

Useful mathematical state:
pair terms

Unknown / target:
alternating sum -1+2-3+...
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
even n -> n/2; odd -> -(n+1)/2

      ↓ simplify / transform

closed form
```

### 4. Derive the Solution

``` text
pair terms
      ↓
even n -> n/2; odd -> -(n+1)/2
      ↓
closed form
      ↓
parity branch
```

**Final model:** `even n -> n/2; odd -> -(n+1)/2` → **closed form**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Use a small valid input and keep only parity:

``` text
even → 0
odd  → 1

Substitute those states into:
even n -> n/2; odd -> -(n+1)/2

Then simplify to:
closed form

This shows why the exact magnitudes can be discarded
when only odd/even behavior affects the answer.
```

## Problem 020 --- CF 1399A --- Remove Smallest

**Problem Link:** [CF 1399A --- Remove
Smallest](https://codeforces.com/problemset/problem/1399/A)\
**Topic / Rating:** Sorting / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
array

ACTUAL QUESTION:
can repeatedly remove smaller when diff<=1
```

### 2. Extract Variables

``` text
Given:
array

Useful mathematical state:
sorted adjacent gaps encode feasibility

Unknown / target:
can repeatedly remove smaller when diff<=1
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
max adjacent diff<=1

      ↓ simplify / transform

sort + check
```

### 4. Derive the Solution

``` text
sorted adjacent gaps encode feasibility
      ↓
max adjacent diff<=1
      ↓
sort + check
      ↓
O(nlogn)
```

**Final model:** `max adjacent diff<=1` → **sort + check**.

------------------------------------------------------------------------

# Pattern 3 --- Bounds / Inequalities / Min-Max

### 5. Dry Run --- How It Works

Example:

``` text
values:
[8, 2, 6, 3]

sort:
[2, 3, 6, 8]

Now apply:
max adjacent diff<=1

which reduces to:
sort + check

Sorting exposes the mathematical order
that was hidden by the input arrangement.
```

## Problem 021 --- CF 1690A --- Print a Pedestal

**Problem Link:** [CF 1690A --- Print a
Pedestal](https://codeforces.com/problemset/problem/1690/A)\
**Topic / Rating:** Construction / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
n

ACTUAL QUESTION:
split n into 3 positive distinct heights with middle ordering
```

### 2. Extract Variables

``` text
Given:
n

Useful mathematical state:
x<y<z and sum n

Unknown / target:
split n into 3 positive distinct heights with middle ordering
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
near n/3 then adjust

      ↓ simplify / transform

construct around thirds
```

### 4. Derive the Solution

``` text
x<y<z and sum n
      ↓
near n/3 then adjust
      ↓
construct around thirds
      ↓
formula/cases
```

**Final model:** `near n/3 then adjust` → **construct around thirds**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
near n/3 then adjust

     ↓ evaluate / simplify

construct around thirds

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 022
--- CF 1676A --- Lucky?

**Problem Link:** [CF 1676A ---
Lucky?](https://codeforces.com/problemset/problem/1676/A)\
**Topic / Rating:** Equation / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
6-digit string

ACTUAL QUESTION:
first 3 digit sum equals last 3
```

### 2. Extract Variables

``` text
Given:
6-digit string

Useful mathematical state:
S1,S2

Unknown / target:
first 3 digit sum equals last 3
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
S1=S2

      ↓ simplify / transform

direct compare
```

### 4. Derive the Solution

``` text
S1,S2
      ↓
S1=S2
      ↓
direct compare
      ↓
O(1)
```

**Final model:** `S1=S2` → **direct compare**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
S1=S2

     ↓ evaluate / simplify

direct compare

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 023
--- CF 1742B --- Increasing

**Problem Link:** [CF 1742B ---
Increasing](https://codeforces.com/problemset/problem/1742/B)\
**Topic / Rating:** Distinctness / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
array

ACTUAL QUESTION:
can permute to strictly increasing
```

### 2. Extract Variables

``` text
Given:
array

Useful mathematical state:
strictly increasing permutation iff all distinct

Unknown / target:
can permute to strictly increasing
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
freq<=1

      ↓ simplify / transform

set size=n
```

### 4. Derive the Solution

``` text
strictly increasing permutation iff all distinct
      ↓
freq<=1
      ↓
set size=n
      ↓
set
```

**Final model:** `freq<=1` → **set size=n**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
freq<=1

     ↓ evaluate / simplify

set size=n

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 024
--- CF 1791A --- Codeforces Checking

**Problem Link:** [CF 1791A --- Codeforces
Checking](https://codeforces.com/problemset/problem/1791/A)\
**Topic / Rating:** Membership / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
char c

ACTUAL QUESTION:
whether c belongs to 'codeforces'
```

### 2. Extract Variables

``` text
Given:
char c

Useful mathematical state:
c ∈ fixed set

Unknown / target:
whether c belongs to 'codeforces'
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
find char

      ↓ simplify / transform

membership
```

### 4. Derive the Solution

``` text
c ∈ fixed set
      ↓
find char
      ↓
membership
      ↓
string find
```

**Final model:** `find char` → **membership**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
find char

     ↓ evaluate / simplify

membership

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 025
--- CF 1829A --- Love Story

**Problem Link:** [CF 1829A --- Love
Story](https://codeforces.com/problemset/problem/1829/A)\
**Topic / Rating:** Hamming Distance / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
string

ACTUAL QUESTION:
positions differing from 'codeforces'
```

### 2. Extract Variables

``` text
Given:
string

Useful mathematical state:
indicator [s[i]!=t[i]]

Unknown / target:
positions differing from 'codeforces'
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
sum indicators

      ↓ simplify / transform

Hamming distance
```

### 4. Derive the Solution

``` text
indicator [s[i]!=t[i]]
      ↓
sum indicators
      ↓
Hamming distance
      ↓
10 checks
```

**Final model:** `sum indicators` → **Hamming distance**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
sum indicators

     ↓ evaluate / simplify

Hamming distance

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 026
--- CF 1873A --- Short Sort

**Problem Link:** [CF 1873A --- Short
Sort](https://codeforces.com/problemset/problem/1873/A)\
**Topic / Rating:** Permutation / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
3-char string

ACTUAL QUESTION:
can sort with <=1 swap
```

### 2. Extract Variables

``` text
Given:
3-char string

Useful mathematical state:
target='abc'

Unknown / target:
can sort with <=1 swap
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
mismatch count 0 or 2

      ↓ simplify / transform

compare permutations
```

### 4. Derive the Solution

``` text
target='abc'
      ↓
mismatch count 0 or 2
      ↓
compare permutations
      ↓
direct
```

**Final model:** `mismatch count 0 or 2` → **compare permutations**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
mismatch count 0 or 2

     ↓ evaluate / simplify

compare permutations

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 027
--- CF 1729A --- Two Elevators

**Problem Link:** [CF 1729A --- Two
Elevators](https://codeforces.com/problemset/problem/1729/A)\
**Topic / Rating:** Distance / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
a,b,c

ACTUAL QUESTION:
which elevator reaches floor1 sooner
```

### 2. Extract Variables

``` text
Given:
a,b,c

Useful mathematical state:
t1=a-1, t2=|b-c|+c-1

Unknown / target:
which elevator reaches floor1 sooner
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
compare times

      ↓ simplify / transform

min comparison
```

### 4. Derive the Solution

``` text
t1=a-1, t2=|b-c|+c-1
      ↓
compare times
      ↓
min comparison
      ↓
O(1)
```

**Final model:** `compare times` → **min comparison**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
compare times

     ↓ evaluate / simplify

min comparison

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 028
--- CF 1805A --- We Need the Zero

**Problem Link:** [CF 1805A --- We Need the
Zero](https://codeforces.com/problemset/problem/1805/A)\
**Topic / Rating:** XOR/Bounds / 900

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
array

ACTUAL QUESTION:
find x making xor transformed zero
```

### 2. Extract Variables

``` text
Given:
array

Useful mathematical state:
xor(a_i xor x)

Unknown / target:
find x making xor transformed zero
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
parity of n controls x contribution

      ↓ simplify / transform

derive xor equation
```

### 4. Derive the Solution

``` text
xor(a_i xor x)
      ↓
parity of n controls x contribution
      ↓
derive xor equation
      ↓
xor all
```

**Final model:** `parity of n controls x contribution` → **derive xor
equation**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Use a small valid input and keep only parity:

``` text
even → 0
odd  → 1

Substitute those states into:
parity of n controls x contribution

Then simplify to:
derive xor equation

This shows why the exact magnitudes can be discarded
when only odd/even behavior affects the answer.
```

## Problem 029 --- CF 1858A --- Buttons

**Problem Link:** [CF 1858A ---
Buttons](https://codeforces.com/problemset/problem/1858/A)\
**Topic / Rating:** Game/Counting / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
a,b,c

ACTUAL QUESTION:
winner with shared buttons
```

### 2. Extract Variables

``` text
Given:
a,b,c

Useful mathematical state:
shared moves alternate

Unknown / target:
winner with shared buttons
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
parity of c decides who gets extra

      ↓ simplify / transform

compare effective counts
```

### 4. Derive the Solution

``` text
shared moves alternate
      ↓
parity of c decides who gets extra
      ↓
compare effective counts
      ↓
casework
```

**Final model:** `parity of c decides who gets extra` → **compare
effective counts**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Use a small valid input and keep only parity:

``` text
even → 0
odd  → 1

Substitute those states into:
parity of c decides who gets extra

Then simplify to:
compare effective counts

This shows why the exact magnitudes can be discarded
when only odd/even behavior affects the answer.
```

## Problem 030 --- CF 1899A --- Game with Integers

**Problem Link:** [CF 1899A --- Game with
Integers](https://codeforces.com/problemset/problem/1899/A)\
**Topic / Rating:** Modulo / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
n

ACTUAL QUESTION:
winner under ±1 and divisibility by3
```

### 2. Extract Variables

``` text
Given:
n

Useful mathematical state:
positions mod3

Unknown / target:
winner under ±1 and divisibility by3
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
n%3==0 is losing/winning condition per rules

      ↓ simplify / transform

reduce to residue
```

### 4. Derive the Solution

``` text
positions mod3
      ↓
n%3==0 is losing/winning condition per rules
      ↓
reduce to residue
      ↓
O(1)
```

**Final model:** `n%3==0 is losing/winning condition per rules` →
**reduce to residue**.

------------------------------------------------------------------------

# Pattern 4 --- Parity Modeling

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
n%3==0 is losing/winning condition per rules

     ↓ evaluate / simplify

reduce to residue

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 031
--- CF 4A --- Watermelon

**Problem Link:** [CF 4A ---
Watermelon](https://codeforces.com/problemset/problem/4/A)\
**Topic / Rating:** Parity / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
w

ACTUAL QUESTION:
split into two positive even parts
```

### 2. Extract Variables

``` text
Given:
w

Useful mathematical state:
w=a+b, a,b even >=2

Unknown / target:
split into two positive even parts
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
w even and w>2

      ↓ simplify / transform

parity + positivity
```

### 4. Derive the Solution

``` text
w=a+b, a,b even >=2
      ↓
w even and w>2
      ↓
parity + positivity
      ↓
w%2==0&&w>2
```

**Final model:** `w even and w>2` → **parity + positivity**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Use a small valid input and keep only parity:

``` text
even → 0
odd  → 1

Substitute those states into:
w even and w>2

Then simplify to:
parity + positivity

This shows why the exact magnitudes can be discarded
when only odd/even behavior affects the answer.
```

## Problem 032 --- CF 1296A --- Array with Odd Sum

**Problem Link:** [CF 1296A --- Array with Odd
Sum](https://codeforces.com/problemset/problem/1296/A)\
**Topic / Rating:** Parity / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
array

ACTUAL QUESTION:
whether required odd-sum selection exists
```

### 2. Extract Variables

``` text
Given:
array

Useful mathematical state:
sum odd iff odd count odd

Unknown / target:
whether required odd-sum selection exists
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
reduce values to parity

      ↓ simplify / transform

count odd/even
```

### 4. Derive the Solution

``` text
sum odd iff odd count odd
      ↓
reduce values to parity
      ↓
count odd/even
      ↓
casework
```

**Final model:** `reduce values to parity` → **count odd/even**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Use a small valid input and keep only parity:

``` text
even → 0
odd  → 1

Substitute those states into:
reduce values to parity

Then simplify to:
count odd/even

This shows why the exact magnitudes can be discarded
when only odd/even behavior affects the answer.
```

## Problem 033 --- CF 1857A --- Array Coloring

**Problem Link:** [CF 1857A --- Array
Coloring](https://codeforces.com/problemset/problem/1857/A)\
**Topic / Rating:** Parity / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
array

ACTUAL QUESTION:
whether can split into two groups with equal parity sums
```

### 2. Extract Variables

``` text
Given:
array

Useful mathematical state:
total sum must be even

Unknown / target:
whether can split into two groups with equal parity sums
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
sum%2=0

      ↓ simplify / transform

parity invariant
```

### 4. Derive the Solution

``` text
total sum must be even
      ↓
sum%2=0
      ↓
parity invariant
      ↓
sum check
```

**Final model:** `sum%2=0` → **parity invariant**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Use a small valid input and keep only parity:

``` text
even → 0
odd  → 1

Substitute those states into:
sum%2=0

Then simplify to:
parity invariant

This shows why the exact magnitudes can be discarded
when only odd/even behavior affects the answer.
```

## Problem 034 --- CF 1834A --- Unit Array

**Problem Link:** [CF 1834A --- Unit
Array](https://codeforces.com/problemset/problem/1834/A)\
**Topic / Rating:** Parity/Greedy / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
±1 array

ACTUAL QUESTION:
minimum flips to satisfy sum>=0 and product=1
```

### 2. Extract Variables

``` text
Given:
±1 array

Useful mathematical state:
product depends on #(-1) parity

Unknown / target:
minimum flips to satisfy sum>=0 and product=1
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
fix sum then parity

      ↓ simplify / transform

count negatives
```

### 4. Derive the Solution

``` text
product depends on #(-1) parity
      ↓
fix sum then parity
      ↓
count negatives
      ↓
formula/loop
```

**Final model:** `fix sum then parity` → **count negatives**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Use a small valid input and keep only parity:

``` text
even → 0
odd  → 1

Substitute those states into:
fix sum then parity

Then simplify to:
count negatives

This shows why the exact magnitudes can be discarded
when only odd/even behavior affects the answer.
```

## Problem 035 --- CF 1367B --- Even Array

**Problem Link:** [CF 1367B --- Even
Array](https://codeforces.com/problemset/problem/1367/B)\
**Topic / Rating:** Parity / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
array

ACTUAL QUESTION:
minimum swaps so a[i]%2=i%2
```

### 2. Extract Variables

``` text
Given:
array

Useful mathematical state:
mismatches of two types must balance

Unknown / target:
minimum swaps so a[i]%2=i%2
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
badEven=badOdd

      ↓ simplify / transform

answer mismatches/2
```

### 4. Derive the Solution

``` text
mismatches of two types must balance
      ↓
badEven=badOdd
      ↓
answer mismatches/2
      ↓
count
```

**Final model:** `badEven=badOdd` → **answer mismatches/2**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Use a small valid input and keep only parity:

``` text
even → 0
odd  → 1

Substitute those states into:
badEven=badOdd

Then simplify to:
answer mismatches/2

This shows why the exact magnitudes can be discarded
when only odd/even behavior affects the answer.
```

## Problem 036 --- CF 1475A --- Odd Divisor

**Problem Link:** [CF 1475A --- Odd
Divisor](https://codeforces.com/problemset/problem/1475/A)\
**Topic / Rating:** Number Theory / 900

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
n

ACTUAL QUESTION:
has odd divisor >1
```

### 2. Extract Variables

``` text
Given:
n

Useful mathematical state:
n=2^k*m odd

Unknown / target:
has odd divisor >1
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
m>1

      ↓ simplify / transform

not power of two
```

### 4. Derive the Solution

``` text
n=2^k*m odd
      ↓
m>1
      ↓
not power of two
      ↓
strip twos
```

**Final model:** `m>1` → **not power of two**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
m>1

     ↓ evaluate / simplify

not power of two

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 037
--- CF 1669C --- Odd/Even Increments

**Problem Link:** [CF 1669C --- Odd/Even
Increments](https://codeforces.com/problemset/problem/1669/C)\
**Topic / Rating:** Parity / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
array

ACTUAL QUESTION:
can equalize via parity-constrained increments
```

### 2. Extract Variables

``` text
Given:
array

Useful mathematical state:
all elements need same parity class relation

Unknown / target:
can equalize via parity-constrained increments
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
check parity consistency

      ↓ simplify / transform

parity only
```

### 4. Derive the Solution

``` text
all elements need same parity class relation
      ↓
check parity consistency
      ↓
parity only
      ↓
scan
```

**Final model:** `check parity consistency` → **parity only**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Use a small valid input and keep only parity:

``` text
even → 0
odd  → 1

Substitute those states into:
check parity consistency

Then simplify to:
parity only

This shows why the exact magnitudes can be discarded
when only odd/even behavior affects the answer.
```

## Problem 038 --- CF 1624A --- Plus One on the Subset

**Problem Link:** [CF 1624A --- Plus One on the
Subset](https://codeforces.com/problemset/problem/1624/A)\
**Topic / Rating:** Difference / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
array

ACTUAL QUESTION:
minimum operations to equalize by incrementing subset
```

### 2. Extract Variables

``` text
Given:
array

Useful mathematical state:
raise to max

Unknown / target:
minimum operations to equalize by incrementing subset
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
answer=max-min

      ↓ simplify / transform

range width
```

### 4. Derive the Solution

``` text
raise to max
      ↓
answer=max-min
      ↓
range width
      ↓
min/max
```

**Final model:** `answer=max-min` → **range width**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
answer=max-min

     ↓ evaluate / simplify

range width

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 039
--- CF 1788A --- One and Two

**Problem Link:** [CF 1788A --- One and
Two](https://codeforces.com/problemset/problem/1788/A)\
**Topic / Rating:** Product/Parity / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
1/2 array

ACTUAL QUESTION:
split so products equal
```

### 2. Extract Variables

``` text
Given:
1/2 array

Useful mathematical state:
equal #twos on both sides

Unknown / target:
split so products equal
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
total twos even

      ↓ simplify / transform

find half twos
```

### 4. Derive the Solution

``` text
equal #twos on both sides
      ↓
total twos even
      ↓
find half twos
      ↓
count
```

**Final model:** `total twos even` → **find half twos**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Use a small valid input and keep only parity:

``` text
even → 0
odd  → 1

Substitute those states into:
total twos even

Then simplify to:
find half twos

This shows why the exact magnitudes can be discarded
when only odd/even behavior affects the answer.
```

## Problem 040 --- CF 1845A --- Forbidden Integer

**Problem Link:** [CF 1845A --- Forbidden
Integer](https://codeforces.com/problemset/problem/1845/A)\
**Topic / Rating:** Constructive / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
n,k,x

ACTUAL QUESTION:
represent n as sum of 1..k excluding x
```

### 2. Extract Variables

``` text
Given:
n,k,x

Useful mathematical state:
choose repeated small allowed values

Unknown / target:
represent n as sum of 1..k excluding x
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
cases x!=1, else 2/3

      ↓ simplify / transform

construct feasibility
```

### 4. Derive the Solution

``` text
choose repeated small allowed values
      ↓
cases x!=1, else 2/3
      ↓
construct feasibility
      ↓
casework
```

**Final model:** `cases x!=1, else 2/3` → **construct feasibility**.

------------------------------------------------------------------------

# Pattern 5 --- Divisibility / GCD / LCM

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
cases x!=1, else 2/3

     ↓ evaluate / simplify

construct feasibility

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 041
--- CF 1328A --- Divisibility Problem

**Problem Link:** [CF 1328A --- Divisibility
Problem](https://codeforces.com/problemset/problem/1328/A)\
**Topic / Rating:** Modulo / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
a,b

ACTUAL QUESTION:
minimum add to make a divisible by b
```

### 2. Extract Variables

``` text
Given:
a,b

Useful mathematical state:
need a+x ≡0 mod b

Unknown / target:
minimum add to make a divisible by b
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
x=(b-a%b)%b

      ↓ simplify / transform

remainder complement
```

### 4. Derive the Solution

``` text
need a+x ≡0 mod b
      ↓
x=(b-a%b)%b
      ↓
remainder complement
      ↓
O(1)
```

**Final model:** `x=(b-a%b)%b` → **remainder complement**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
x=(b-a%b)%b

     ↓ evaluate / simplify

remainder complement

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 042
--- CF 1343A --- Candies

**Problem Link:** [CF 1343A ---
Candies](https://codeforces.com/problemset/problem/1343/A)\
**Topic / Rating:** Geometric/Divisibility / 900

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
n

ACTUAL QUESTION:
find x where n=x(2^k-1)
```

### 2. Extract Variables

``` text
Given:
n

Useful mathematical state:
geometric sum factor

Unknown / target:
find x where n=x(2^k-1)
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
x=n/(2^k-1) if divisible

      ↓ simplify / transform

test k
```

### 4. Derive the Solution

``` text
geometric sum factor
      ↓
x=n/(2^k-1) if divisible
      ↓
test k
      ↓
loop
```

**Final model:** `x=n/(2^k-1) if divisible` → **test k**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
x=n/(2^k-1) if divisible

     ↓ evaluate / simplify

test k

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 043
--- CF 1370A --- Maximum GCD

**Problem Link:** [CF 1370A --- Maximum
GCD](https://codeforces.com/problemset/problem/1370/A)\
**Topic / Rating:** GCD / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
n

ACTUAL QUESTION:
maximize gcd(a,b), a+b=n
```

### 2. Extract Variables

``` text
Given:
n

Useful mathematical state:
gcd<=floor(n/2)

Unknown / target:
maximize gcd(a,b), a+b=n
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
choose floor(n/2)

      ↓ simplify / transform

tight bound
```

### 4. Derive the Solution

``` text
gcd<=floor(n/2)
      ↓
choose floor(n/2)
      ↓
tight bound
      ↓
n/2
```

**Final model:** `choose floor(n/2)` → **tight bound**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
choose floor(n/2)

     ↓ evaluate / simplify

tight bound

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 044
--- CF 1829C --- Mr. Perfectly Fine

**Problem Link:** [CF 1829C --- Mr. Perfectly
Fine](https://codeforces.com/problemset/problem/1829/C)\
**Topic / Rating:** Min/Bitmask / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
items

ACTUAL QUESTION:
minimum time covering skills 1 and2
```

### 2. Extract Variables

``` text
Given:
items

Useful mathematical state:
skill masks 01,10,11

Unknown / target:
minimum time covering skills 1 and2
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
min(cost11,cost01+cost10)

      ↓ simplify / transform

coverage states
```

### 4. Derive the Solution

``` text
skill masks 01,10,11
      ↓
min(cost11,cost01+cost10)
      ↓
coverage states
      ↓
track minima
```

**Final model:** `min(cost11,cost01+cost10)` → **coverage states**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
min(cost11,cost01+cost10)

     ↓ evaluate / simplify

coverage states

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 045
--- CF 1618A --- Polycarp and Sums of Subsequences

**Problem Link:** [CF 1618A --- Polycarp and Sums of
Subsequences](https://codeforces.com/problemset/problem/1618/A)\
**Topic / Rating:** Algebra / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
7 subset sums

ACTUAL QUESTION:
recover a,b,c
```

### 2. Extract Variables

``` text
Given:
7 subset sums

Useful mathematical state:
smallest=a,b and total largest=a+b+c

Unknown / target:
recover a,b,c
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
c=largest-a-b

      ↓ simplify / transform

sorted sums
```

### 4. Derive the Solution

``` text
smallest=a,b and total largest=a+b+c
      ↓
c=largest-a-b
      ↓
sorted sums
      ↓
formula
```

**Final model:** `c=largest-a-b` → **sorted sums**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Example:

``` text
values:
[8, 2, 6, 3]

sort:
[2, 3, 6, 8]

Now apply:
c=largest-a-b

which reduces to:
sorted sums

Sorting exposes the mathematical order
that was hidden by the input arrangement.
```

## Problem 046 --- CF 160A --- Twins

**Problem Link:** [CF 160A ---
Twins](https://codeforces.com/problemset/problem/160/A)\
**Topic / Rating:** Greedy/Sum / 900

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
coins

ACTUAL QUESTION:
minimum coins with sum > remaining
```

### 2. Extract Variables

``` text
Given:
coins

Useful mathematical state:
chosen > total-chosen

Unknown / target:
minimum coins with sum > remaining
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
2*chosen>total

      ↓ simplify / transform

sort descending
```

### 4. Derive the Solution

``` text
chosen > total-chosen
      ↓
2*chosen>total
      ↓
sort descending
      ↓
prefix
```

**Final model:** `2*chosen>total` → **sort descending**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Example:

``` text
values:
[8, 2, 6, 3]

sort:
[2, 3, 6, 8]

Now apply:
2*chosen>total

which reduces to:
sort descending

Sorting exposes the mathematical order
that was hidden by the input arrangement.
```

## Problem 047 --- CF 1475B --- New Year's Number

**Problem Link:** [CF 1475B --- New Year's
Number](https://codeforces.com/problemset/problem/1475/B)\
**Topic / Rating:** Diophantine / 900

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
n

ACTUAL QUESTION:
n=2020a+2021b?
```

### 2. Extract Variables

``` text
Given:
n

Useful mathematical state:
2021=2020+1

Unknown / target:
n=2020a+2021b?
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
choose b=n%2020 then test

      ↓ simplify / transform

linear diophantine shortcut
```

### 4. Derive the Solution

``` text
2021=2020+1
      ↓
choose b=n%2020 then test
      ↓
linear diophantine shortcut
      ↓
condition
```

**Final model:** `choose b=n%2020 then test` → **linear diophantine
shortcut**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Use a small valid input and keep only parity:

``` text
even → 0
odd  → 1

Substitute those states into:
choose b=n%2020 then test

Then simplify to:
linear diophantine shortcut

This shows why the exact magnitudes can be discarded
when only odd/even behavior affects the answer.
```

## Problem 048 --- CF 1593A --- Elections

**Problem Link:** [CF 1593A ---
Elections](https://codeforces.com/problemset/problem/1593/A)\
**Topic / Rating:** Max/Formula / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
a,b,c

ACTUAL QUESTION:
increments to become strictly largest
```

### 2. Extract Variables

``` text
Given:
a,b,c

Useful mathematical state:
need x+inc>max(other)

Unknown / target:
increments to become strictly largest
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
inc=max(0,M-x+1), except unique max

      ↓ simplify / transform

per candidate bound
```

### 4. Derive the Solution

``` text
need x+inc>max(other)
      ↓
inc=max(0,M-x+1), except unique max
      ↓
per candidate bound
      ↓
formula
```

**Final model:** `inc=max(0,M-x+1), except unique max` → **per candidate
bound**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
inc=max(0,M-x+1), except unique max

     ↓ evaluate / simplify

per candidate bound

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 049
--- CF 1829B --- Blank Space

**Problem Link:** [CF 1829B --- Blank
Space](https://codeforces.com/problemset/problem/1829/B)\
**Topic / Rating:** Run Length / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
binary array

ACTUAL QUESTION:
longest consecutive zeros
```

### 2. Extract Variables

``` text
Given:
binary array

Useful mathematical state:
state current run

Unknown / target:
longest consecutive zeros
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
max over runs

      ↓ simplify / transform

scan
```

### 4. Derive the Solution

``` text
state current run
      ↓
max over runs
      ↓
scan
      ↓
O(n)
```

**Final model:** `max over runs` → **scan**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
max over runs

     ↓ evaluate / simplify

scan

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 050
--- CF 1877A --- Goals of Victory

**Problem Link:** [CF 1877A --- Goals of
Victory](https://codeforces.com/problemset/problem/1877/A)\
**Topic / Rating:** Sum Invariant / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
n-1 values

ACTUAL QUESTION:
missing value so total sum zero
```

### 2. Extract Variables

``` text
Given:
n-1 values

Useful mathematical state:
x+sum=0

Unknown / target:
missing value so total sum zero
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
x=-sum

      ↓ simplify / transform

equation
```

### 4. Derive the Solution

``` text
x+sum=0
      ↓
x=-sum
      ↓
equation
      ↓
O(n)
```

**Final model:** `x=-sum` → **equation**.

------------------------------------------------------------------------

# Pattern 6 --- Modulo / Cyclic Modeling

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
x=-sum

     ↓ evaluate / simplify

equation

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 051
--- CF 116A --- Tram

**Problem Link:** [CF 116A ---
Tram](https://codeforces.com/problemset/problem/116/A)\
**Topic / Rating:** Prefix/Capacity / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
stops

ACTUAL QUESTION:
minimum tram capacity
```

### 2. Extract Variables

``` text
Given:
stops

Useful mathematical state:
current += enter-exit

Unknown / target:
minimum tram capacity
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
max prefix occupancy

      ↓ simplify / transform

running state
```

### 4. Derive the Solution

``` text
current += enter-exit
      ↓
max prefix occupancy
      ↓
running state
      ↓
scan
```

**Final model:** `max prefix occupancy` → **running state**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
max prefix occupancy

     ↓ evaluate / simplify

running state

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 052
--- CF 266A --- Stones on the Table

**Problem Link:** [CF 266A --- Stones on the
Table](https://codeforces.com/problemset/problem/266/A)\
**Topic / Rating:** Adjacent / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
string

ACTUAL QUESTION:
minimum removals so adjacent colors differ
```

### 2. Extract Variables

``` text
Given:
string

Useful mathematical state:
remove one from each equal adjacency

Unknown / target:
minimum removals so adjacent colors differ
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
count s[i]==s[i-1]

      ↓ simplify / transform

local contribution
```

### 4. Derive the Solution

``` text
remove one from each equal adjacency
      ↓
count s[i]==s[i-1]
      ↓
local contribution
      ↓
scan
```

**Final model:** `count s[i]==s[i-1]` → **local contribution**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
count s[i]==s[i-1]

     ↓ evaluate / simplify

local contribution

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 053
--- CF 228A --- Is your horseshoe on the other hoof?

**Problem Link:** [CF 228A --- Is your horseshoe on the other
hoof?](https://codeforces.com/problemset/problem/228/A)\
**Topic / Rating:** Distinctness / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
4 colors

ACTUAL QUESTION:
minimum replacements for distinct
```

### 2. Extract Variables

``` text
Given:
4 colors

Useful mathematical state:
4-distinctCount

Unknown / target:
minimum replacements for distinct
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
set size

      ↓ simplify / transform

duplicates
```

### 4. Derive the Solution

``` text
4-distinctCount
      ↓
set size
      ↓
duplicates
      ↓
set
```

**Final model:** `set size` → **duplicates**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
set size

     ↓ evaluate / simplify

duplicates

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 054
--- CF 443A --- Anton and Letters

**Problem Link:** [CF 443A --- Anton and
Letters](https://codeforces.com/problemset/problem/443/A)\
**Topic / Rating:** Set / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
formatted string

ACTUAL QUESTION:
number distinct letters
```

### 2. Extract Variables

``` text
Given:
formatted string

Useful mathematical state:
extract lowercase chars

Unknown / target:
number distinct letters
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
set cardinality

      ↓ simplify / transform

distinct count
```

### 4. Derive the Solution

``` text
extract lowercase chars
      ↓
set cardinality
      ↓
distinct count
      ↓
set
```

**Final model:** `set cardinality` → **distinct count**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
set cardinality

     ↓ evaluate / simplify

distinct count

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 055
--- CF 59A --- Word

**Problem Link:** [CF 59A ---
Word](https://codeforces.com/problemset/problem/59/A)\
**Topic / Rating:** Counting / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
string

ACTUAL QUESTION:
convert based on upper/lower majority
```

### 2. Extract Variables

``` text
Given:
string

Useful mathematical state:
count uppercase vs lowercase

Unknown / target:
convert based on upper/lower majority
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
choose case

      ↓ simplify / transform

frequency comparison
```

### 4. Derive the Solution

``` text
count uppercase vs lowercase
      ↓
choose case
      ↓
frequency comparison
      ↓
transform
```

**Final model:** `choose case` → **frequency comparison**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Use a tiny transformed sequence:

``` text
keys = [2, 2, 5, 2, 5]

frequency:
2 → 3
5 → 2

Apply:
choose case

Then:
frequency comparison

The dry run tracks frequencies/keys,
not the original story objects.
```

## Problem 056 --- CF 236A --- Boy or Girl

**Problem Link:** [CF 236A --- Boy or
Girl](https://codeforces.com/problemset/problem/236/A)\
**Topic / Rating:** Set/Parity / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
username

ACTUAL QUESTION:
output based on distinct char count parity
```

### 2. Extract Variables

``` text
Given:
username

Useful mathematical state:
d=|set(chars)|

Unknown / target:
output based on distinct char count parity
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
d%2

      ↓ simplify / transform

parity of distinct count
```

### 4. Derive the Solution

``` text
d=|set(chars)|
      ↓
d%2
      ↓
parity of distinct count
      ↓
set
```

**Final model:** `d%2` → **parity of distinct count**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Use a small valid input and keep only parity:

``` text
even → 0
odd  → 1

Substitute those states into:
d%2

Then simplify to:
parity of distinct count

This shows why the exact magnitudes can be discarded
when only odd/even behavior affects the answer.
```

## Problem 057 --- CF 785A --- Anton and Polyhedrons

**Problem Link:** [CF 785A --- Anton and
Polyhedrons](https://codeforces.com/problemset/problem/785/A)\
**Topic / Rating:** Mapping / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
names

ACTUAL QUESTION:
total faces
```

### 2. Extract Variables

``` text
Given:
names

Useful mathematical state:
name→constant

Unknown / target:
total faces
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
sum contributions

      ↓ simplify / transform

lookup
```

### 4. Derive the Solution

``` text
name→constant
      ↓
sum contributions
      ↓
lookup
      ↓
map/if
```

**Final model:** `sum contributions` → **lookup**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
sum contributions

     ↓ evaluate / simplify

lookup

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 058
--- CF 703A --- Mishka and Game

**Problem Link:** [CF 703A --- Mishka and
Game](https://codeforces.com/problemset/problem/703/A)\
**Topic / Rating:** Comparison / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
round scores

ACTUAL QUESTION:
winner by more round wins
```

### 2. Extract Variables

``` text
Given:
round scores

Useful mathematical state:
count a>b and a<b

Unknown / target:
winner by more round wins
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
compare counts

      ↓ simplify / transform

two counters
```

### 4. Derive the Solution

``` text
count a>b and a<b
      ↓
compare counts
      ↓
two counters
      ↓
scan
```

**Final model:** `compare counts` → **two counters**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
compare counts

     ↓ evaluate / simplify

two counters

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 059
--- CF 734B --- Anton and Digits

**Problem Link:** [CF 734B --- Anton and
Digits](https://codeforces.com/problemset/problem/734/B)\
**Topic / Rating:** Greedy/Counting / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
counts 2,3,5,6

ACTUAL QUESTION:
maximize sum using 256 and32
```

### 2. Extract Variables

``` text
Given:
counts 2,3,5,6

Useful mathematical state:
make 256 first because larger

Unknown / target:
maximize sum using 256 and32
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
x=min(2,5,6), y=min(2left,3)

      ↓ simplify / transform

resource allocation
```

### 4. Derive the Solution

``` text
make 256 first because larger
      ↓
x=min(2,5,6), y=min(2left,3)
      ↓
resource allocation
      ↓
greedy
```

**Final model:** `x=min(2,5,6), y=min(2left,3)` → **resource
allocation**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
x=min(2,5,6), y=min(2left,3)

     ↓ evaluate / simplify

resource allocation

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 060
--- CF 1097A --- Gennady the Card Game

**Problem Link:** [CF 1097A --- Gennady the Card
Game](https://codeforces.com/problemset/problem/1097/A)\
**Topic / Rating:** Matching / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
card + five cards

ACTUAL QUESTION:
whether rank or suit matches
```

### 2. Extract Variables

``` text
Given:
card + five cards

Useful mathematical state:
exists same first or second char

Unknown / target:
whether rank or suit matches
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
OR condition

      ↓ simplify / transform

scan
```

### 4. Derive the Solution

``` text
exists same first or second char
      ↓
OR condition
      ↓
scan
      ↓
O(5)
```

**Final model:** `OR condition` → **scan**.

------------------------------------------------------------------------

# Pattern 7 --- Counting / Frequency / Pairs

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
OR condition

     ↓ evaluate / simplify

scan

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 061
--- CF 1520D --- Same Differences

**Problem Link:** [CF 1520D --- Same
Differences](https://codeforces.com/problemset/problem/1520/D)\
**Topic / Rating:** Algebra/Frequency / 1200

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
array

ACTUAL QUESTION:
count i<j with a[j]-a[i]=j-i
```

### 2. Extract Variables

``` text
Given:
array

Useful mathematical state:
a[j]-j=a[i]-i

Unknown / target:
count i<j with a[j]-a[i]=j-i
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
key=a[i]-i

      ↓ simplify / transform

equal-key pairs
```

### 4. Derive the Solution

``` text
a[j]-j=a[i]-i
      ↓
key=a[i]-i
      ↓
equal-key pairs
      ↓
hash map
```

**Final model:** `key=a[i]-i` → **equal-key pairs**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Use a tiny transformed sequence:

``` text
keys = [2, 2, 5, 2, 5]

frequency:
2 → 3
5 → 2

Apply:
key=a[i]-i

Then:
equal-key pairs

The dry run tracks frequencies/keys,
not the original story objects.
```

## Problem 062 --- CF 1538C --- Challenging Cliffs / Number of Pairs

**Problem Link:** [CF 1538C --- Challenging Cliffs / Number of
Pairs](https://codeforces.com/problemset/problem/1538/C)\
**Topic / Rating:** Two Pointers / 1300

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
array,l,r

ACTUAL QUESTION:
count pairs with sum in [l,r]
```

### 2. Extract Variables

``` text
Given:
array,l,r

Useful mathematical state:
count<=r - count<l

Unknown / target:
count pairs with sum in [l,r]
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
sorted pair bound

      ↓ simplify / transform

two pointers
```

### 4. Derive the Solution

``` text
count<=r - count<l
      ↓
sorted pair bound
      ↓
two pointers
      ↓
O(nlogn)
```

**Final model:** `sorted pair bound` → **two pointers**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Example:

``` text
values:
[8, 2, 6, 3]

sort:
[2, 3, 6, 8]

Now apply:
sorted pair bound

which reduces to:
two pointers

Sorting exposes the mathematical order
that was hidden by the input arrangement.
```

## Problem 063 --- CF 1669B --- Triple

**Problem Link:** [CF 1669B ---
Triple](https://codeforces.com/problemset/problem/1669/B)\
**Topic / Rating:** Frequency / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
array

ACTUAL QUESTION:
find value occurring >=3
```

### 2. Extract Variables

``` text
Given:
array

Useful mathematical state:
freq[x]>=3

Unknown / target:
find value occurring >=3
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
frequency threshold

      ↓ simplify / transform

count
```

### 4. Derive the Solution

``` text
freq[x]>=3
      ↓
frequency threshold
      ↓
count
      ↓
map
```

**Final model:** `frequency threshold` → **count**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
frequency threshold

     ↓ evaluate / simplify

count

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 064
--- CF 1742C --- Stripes

**Problem Link:** [CF 1742C ---
Stripes](https://codeforces.com/problemset/problem/1742/C)\
**Topic / Rating:** Grid/Existence / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
8x8 grid

ACTUAL QUESTION:
determine last full stripe color
```

### 2. Extract Variables

``` text
Given:
8x8 grid

Useful mathematical state:
full row of R is decisive

Unknown / target:
determine last full stripe color
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
scan rows

      ↓ simplify / transform

existence
```

### 4. Derive the Solution

``` text
full row of R is decisive
      ↓
scan rows
      ↓
existence
      ↓
O(64)
```

**Final model:** `scan rows` → **existence**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
scan rows

     ↓ evaluate / simplify

existence

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 065
--- CF 1791B --- Following Directions

**Problem Link:** [CF 1791B --- Following
Directions](https://codeforces.com/problemset/problem/1791/B)\
**Topic / Rating:** Coordinates / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
moves

ACTUAL QUESTION:
whether path visits (1,1)
```

### 2. Extract Variables

``` text
Given:
moves

Useful mathematical state:
update x,y per char

Unknown / target:
whether path visits (1,1)
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
∃prefix=(1,1)

      ↓ simplify / transform

prefix state
```

### 4. Derive the Solution

``` text
update x,y per char
      ↓
∃prefix=(1,1)
      ↓
prefix state
      ↓
scan
```

**Final model:** `∃prefix=(1,1)` → **prefix state**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
∃prefix=(1,1)

     ↓ evaluate / simplify

prefix state

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 066
--- CF 1703B --- ICPC Balloons

**Problem Link:** [CF 1703B --- ICPC
Balloons](https://codeforces.com/problemset/problem/1703/B)\
**Topic / Rating:** Frequency / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
string

ACTUAL QUESTION:
score first occurrence differently
```

### 2. Extract Variables

``` text
Given:
string

Useful mathematical state:
first char contributes2 else1

Unknown / target:
score first occurrence differently
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
seen set

      ↓ simplify / transform

contribution
```

### 4. Derive the Solution

``` text
first char contributes2 else1
      ↓
seen set
      ↓
contribution
      ↓
set
```

**Final model:** `seen set` → **contribution**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
seen set

     ↓ evaluate / simplify

contribution

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 067
--- CF 1722A --- Spell Check

**Problem Link:** [CF 1722A --- Spell
Check](https://codeforces.com/problemset/problem/1722/A)\
**Topic / Rating:** Frequency/Sorting / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
string

ACTUAL QUESTION:
whether permutation equals TimUR
```

### 2. Extract Variables

``` text
Given:
string

Useful mathematical state:
same multiset as 'Timur'

Unknown / target:
whether permutation equals TimUR
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
sort or counts

      ↓ simplify / transform

canonical form
```

### 4. Derive the Solution

``` text
same multiset as 'Timur'
      ↓
sort or counts
      ↓
canonical form
      ↓
sort
```

**Final model:** `sort or counts` → **canonical form**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
sort or counts

     ↓ evaluate / simplify

canonical form

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 068
--- CF 1791C --- Prepend and Append

**Problem Link:** [CF 1791C --- Prepend and
Append](https://codeforces.com/problemset/problem/1791/C)\
**Topic / Rating:** Two Pointers / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
binary string

ACTUAL QUESTION:
remove unequal ends
```

### 2. Extract Variables

``` text
Given:
binary string

Useful mathematical state:
while l<r and s[l]!=s[r]

Unknown / target:
remove unequal ends
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
remaining length

      ↓ simplify / transform

two pointers
```

### 4. Derive the Solution

``` text
while l<r and s[l]!=s[r]
      ↓
remaining length
      ↓
two pointers
      ↓
O(n)
```

**Final model:** `remaining length` → **two pointers**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
remaining length

     ↓ evaluate / simplify

two pointers

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 069
--- CF 1829D --- Gold Rush

**Problem Link:** [CF 1829D --- Gold
Rush](https://codeforces.com/problemset/problem/1829/D)\
**Topic / Rating:** Recursion/Reachability / 1000

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
n,m

ACTUAL QUESTION:
can reach m by splitting x into x/3 and2x/3
```

### 2. Extract Variables

``` text
Given:
n,m

Useful mathematical state:
only split divisible by3

Unknown / target:
can reach m by splitting x into x/3 and2x/3
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
DFS on decreasing states

      ↓ simplify / transform

reachability
```

### 4. Derive the Solution

``` text
only split divisible by3
      ↓
DFS on decreasing states
      ↓
reachability
      ↓
recursion
```

**Final model:** `DFS on decreasing states` → **reachability**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
DFS on decreasing states

     ↓ evaluate / simplify

reachability

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 070
--- CF 1878B --- Aleksa and Stack

**Problem Link:** [CF 1878B --- Aleksa and
Stack](https://codeforces.com/problemset/problem/1878/B)\
**Topic / Rating:** Construction / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
n

ACTUAL QUESTION:
construct sequence satisfying divisibility condition
```

### 2. Extract Variables

``` text
Given:
n

Useful mathematical state:
choose simple arithmetic sequence

Unknown / target:
construct sequence satisfying divisibility condition
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
constant gap avoids divisibility

      ↓ simplify / transform

construct
```

### 4. Derive the Solution

``` text
choose simple arithmetic sequence
      ↓
constant gap avoids divisibility
      ↓
construct
      ↓
formula
```

**Final model:** `constant gap avoids divisibility` → **construct**.

------------------------------------------------------------------------

# Pattern 8 --- Operation → Delta → Invariant

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
constant gap avoids divisibility

     ↓ evaluate / simplify

construct

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 071
--- CF 1538B --- Friends and Candies

**Problem Link:** [CF 1538B --- Friends and
Candies](https://codeforces.com/problemset/problem/1538/B)\
**Topic / Rating:** Invariant / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
array

ACTUAL QUESTION:
equalize while preserving sum
```

### 2. Extract Variables

``` text
Given:
array

Useful mathematical state:
S=n*x

Unknown / target:
equalize while preserving sum
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
S%n=0

      ↓ simplify / transform

average invariant
```

### 4. Derive the Solution

``` text
S=n*x
      ↓
S%n=0
      ↓
average invariant
      ↓
count >avg
```

**Final model:** `S%n=0` → **average invariant**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
S%n=0

     ↓ evaluate / simplify

average invariant

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 072
--- CF 1855A --- Dalton the Teacher

**Problem Link:** [CF 1855A --- Dalton the
Teacher](https://codeforces.com/problemset/problem/1855/A)\
**Topic / Rating:** Mismatch/Operation / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
permutation

ACTUAL QUESTION:
minimum operations fixing fixed points by pair operation
```

### 2. Extract Variables

``` text
Given:
permutation

Useful mathematical state:
each op can fix at most2 fixed points

Unknown / target:
minimum operations fixing fixed points by pair operation
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
ceil(fixed/2)

      ↓ simplify / transform

count fixed
```

### 4. Derive the Solution

``` text
each op can fix at most2 fixed points
      ↓
ceil(fixed/2)
      ↓
count fixed
      ↓
(cnt+1)/2
```

**Final model:** `ceil(fixed/2)` → **count fixed**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
ceil(fixed/2)

     ↓ evaluate / simplify

count fixed

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 073
--- CF 1838A --- Blackboard List

**Problem Link:** [CF 1838A --- Blackboard
List](https://codeforces.com/problemset/problem/1838/A)\
**Topic / Rating:** Extremal / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
array

ACTUAL QUESTION:
recover original special number
```

### 2. Extract Variables

``` text
Given:
array

Useful mathematical state:
negative minimum survives construction; else maximum

Unknown / target:
recover original special number
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
extremal invariant

      ↓ simplify / transform

min if negative else max
```

### 4. Derive the Solution

``` text
negative minimum survives construction; else maximum
      ↓
extremal invariant
      ↓
min if negative else max
      ↓
scan
```

**Final model:** `extremal invariant` → **min if negative else max**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
extremal invariant

     ↓ evaluate / simplify

min if negative else max

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 074
--- CF 1862B --- Sequence Game

**Problem Link:** [CF 1862B --- Sequence
Game](https://codeforces.com/problemset/problem/1862/B)\
**Topic / Rating:** Construction / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
sequence b

ACTUAL QUESTION:
construct a so filtering rule returns b
```

### 2. Extract Variables

``` text
Given:
sequence b

Useful mathematical state:
insert bridge when b[i-1]>b[i]

Unknown / target:
construct a so filtering rule returns b
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
local condition

      ↓ simplify / transform

construct with extra value
```

### 4. Derive the Solution

``` text
insert bridge when b[i-1]>b[i]
      ↓
local condition
      ↓
construct with extra value
      ↓
linear
```

**Final model:** `local condition` → **construct with extra value**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
local condition

     ↓ evaluate / simplify

construct with extra value

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 075
--- CF 1798A --- Showstopper

**Problem Link:** [CF 1798A ---
Showstopper](https://codeforces.com/problemset/problem/1798/A)\
**Topic / Rating:** Invariant/Swap / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
two arrays

ACTUAL QUESTION:
can swap pairs so last elements are maxima
```

### 2. Extract Variables

``` text
Given:
two arrays

Useful mathematical state:
each pair independently orientable

Unknown / target:
can swap pairs so last elements are maxima
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
need max pair endpoints fit final

      ↓ simplify / transform

normalize max/min
```

### 4. Derive the Solution

``` text
each pair independently orientable
      ↓
need max pair endpoints fit final
      ↓
normalize max/min
      ↓
check
```

**Final model:** `need max pair endpoints fit final` → **normalize
max/min**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
need max pair endpoints fit final

     ↓ evaluate / simplify

normalize max/min

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 076
--- CF 660A --- Co-prime Array

**Problem Link:** [CF 660A --- Co-prime
Array](https://codeforces.com/problemset/problem/660/A)\
**Topic / Rating:** Construction/GCD / 900

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
array

ACTUAL QUESTION:
insert minimum numbers so adjacent gcd=1
```

### 2. Extract Variables

``` text
Given:
array

Useful mathematical state:
if gcd(a[i],a[i+1])>1 insert coprime sentinel

Unknown / target:
insert minimum numbers so adjacent gcd=1
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
local repair

      ↓ simplify / transform

insert 1
```

### 4. Derive the Solution

``` text
if gcd(a[i],a[i+1])>1 insert coprime sentinel
      ↓
local repair
      ↓
insert 1
      ↓
linear
```

**Final model:** `local repair` → **insert 1**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
local repair

     ↓ evaluate / simplify

insert 1

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 077
--- CF 1367A --- Short Substrings

**Problem Link:** [CF 1367A --- Short
Substrings](https://codeforces.com/problemset/problem/1367/A)\
**Topic / Rating:** String Reconstruction / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
string b

ACTUAL QUESTION:
recover original
```

### 2. Extract Variables

``` text
Given:
string b

Useful mathematical state:
overlap pairs share char

Unknown / target:
recover original
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
take first then every second char

      ↓ simplify / transform

inverse operation
```

### 4. Derive the Solution

``` text
overlap pairs share char
      ↓
take first then every second char
      ↓
inverse operation
      ↓
construct
```

**Final model:** `take first then every second char` → **inverse
operation**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
take first then every second char

     ↓ evaluate / simplify

inverse operation

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 078
--- CF 1374A --- Required Remainder

**Problem Link:** [CF 1374A --- Required
Remainder](https://codeforces.com/problemset/problem/1374/A)\
**Topic / Rating:** Modulo/Optimization / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
x,y,n

ACTUAL QUESTION:
largest k<=n with k%x=y
```

### 2. Extract Variables

``` text
Given:
x,y,n

Useful mathematical state:
numbers are tx+y

Unknown / target:
largest k<=n with k%x=y
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
t=floor((n-y)/x)

      ↓ simplify / transform

largest feasible
```

### 4. Derive the Solution

``` text
numbers are tx+y
      ↓
t=floor((n-y)/x)
      ↓
largest feasible
      ↓
formula
```

**Final model:** `t=floor((n-y)/x)` → **largest feasible**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
t=floor((n-y)/x)

     ↓ evaluate / simplify

largest feasible

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 079
--- CF 1551A --- Polycarp and Coins

**Problem Link:** [CF 1551A --- Polycarp and
Coins](https://codeforces.com/problemset/problem/1551/A)\
**Topic / Rating:** Balancing / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
n

ACTUAL QUESTION:
split n into 1-coin and2-coin counts minimizing difference
```

### 2. Extract Variables

``` text
Given:
n

Useful mathematical state:
c1+2c2=n, |c1-c2| min

Unknown / target:
split n into 1-coin and2-coin counts minimizing difference
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
near n/3

      ↓ simplify / transform

balanced equation
```

### 4. Derive the Solution

``` text
c1+2c2=n, |c1-c2| min
      ↓
near n/3
      ↓
balanced equation
      ↓
n%3 cases
```

**Final model:** `near n/3` → **balanced equation**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
near n/3

     ↓ evaluate / simplify

balanced equation

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 080
--- CF 1818A --- Politics

**Problem Link:** [CF 1818A ---
Politics](https://codeforces.com/problemset/problem/1818/A)\
**Topic / Rating:** String/Counting / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
strings

ACTUAL QUESTION:
count strings compatible with reference
```

### 2. Extract Variables

``` text
Given:
strings

Useful mathematical state:
positions with reference 1 impose equality

Unknown / target:
count strings compatible with reference
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
predicate per string

      ↓ simplify / transform

count valid
```

### 4. Derive the Solution

``` text
positions with reference 1 impose equality
      ↓
predicate per string
      ↓
count valid
      ↓
nested scan
```

**Final model:** `predicate per string` → **count valid**.

------------------------------------------------------------------------

# Pattern 9 --- Sorting / Coordinate / Distance Modeling

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
predicate per string

     ↓ evaluate / simplify

count valid

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 081
--- CF 160A --- Twins

**Problem Link:** [CF 160A ---
Twins](https://codeforces.com/problemset/problem/160/A)\
**Topic / Rating:** Sorting/Greedy / 900

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
coins

ACTUAL QUESTION:
minimum selected sum > rest
```

### 2. Extract Variables

``` text
Given:
coins

Useful mathematical state:
sort descending

Unknown / target:
minimum selected sum > rest
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
prefix until 2sum>total

      ↓ simplify / transform

extremal choice
```

### 4. Derive the Solution

``` text
sort descending
      ↓
prefix until 2sum>total
      ↓
extremal choice
      ↓
O(nlogn)
```

**Final model:** `prefix until 2sum>total` → **extremal choice**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
prefix until 2sum>total

     ↓ evaluate / simplify

extremal choice

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 082
--- CF 1399A --- Remove Smallest

**Problem Link:** [CF 1399A --- Remove
Smallest](https://codeforces.com/problemset/problem/1399/A)\
**Topic / Rating:** Sorting / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
array

ACTUAL QUESTION:
can delete until one remains under diff<=1
```

### 2. Extract Variables

``` text
Given:
array

Useful mathematical state:
sort; all adjacent gaps<=1

Unknown / target:
can delete until one remains under diff<=1
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
adjacent condition

      ↓ simplify / transform

check
```

### 4. Derive the Solution

``` text
sort; all adjacent gaps<=1
      ↓
adjacent condition
      ↓
check
      ↓
O(nlogn)
```

**Final model:** `adjacent condition` → **check**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
adjacent condition

     ↓ evaluate / simplify

check

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 083
--- CF 1760A --- Medium Number

**Problem Link:** [CF 1760A --- Medium
Number](https://codeforces.com/problemset/problem/1760/A)\
**Topic / Rating:** Sorting / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
a,b,c

ACTUAL QUESTION:
middle value
```

### 2. Extract Variables

``` text
Given:
a,b,c

Useful mathematical state:
sort three

Unknown / target:
middle value
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
second element

      ↓ simplify / transform

median
```

### 4. Derive the Solution

``` text
sort three
      ↓
second element
      ↓
median
      ↓
O(1)
```

**Final model:** `second element` → **median**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
second element

     ↓ evaluate / simplify

median

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 084
--- CF 1538A --- Stone Game

**Problem Link:** [CF 1538A --- Stone
Game](https://codeforces.com/problemset/problem/1538/A)\
**Topic / Rating:** Positions / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
permutation

ACTUAL QUESTION:
min removals from ends to remove min and max
```

### 2. Extract Variables

``` text
Given:
permutation

Useful mathematical state:
positions pmin,pmax

Unknown / target:
min removals from ends to remove min and max
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
min of three strategies

      ↓ simplify / transform

distance to ends
```

### 4. Derive the Solution

``` text
positions pmin,pmax
      ↓
min of three strategies
      ↓
distance to ends
      ↓
formula
```

**Final model:** `min of three strategies` → **distance to ends**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
min of three strategies

     ↓ evaluate / simplify

distance to ends

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 085
--- CF 1729A --- Two Elevators

**Problem Link:** [CF 1729A --- Two
Elevators](https://codeforces.com/problemset/problem/1729/A)\
**Topic / Rating:** Distance / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
a,b,c

ACTUAL QUESTION:
compare travel times
```

### 2. Extract Variables

``` text
Given:
a,b,c

Useful mathematical state:
t1=a-1, t2=|b-c|+c-1

Unknown / target:
compare travel times
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
absolute distance

      ↓ simplify / transform

compare
```

### 4. Derive the Solution

``` text
t1=a-1, t2=|b-c|+c-1
      ↓
absolute distance
      ↓
compare
      ↓
O(1)
```

**Final model:** `absolute distance` → **compare**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
absolute distance

     ↓ evaluate / simplify

compare

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 086
--- CF 1593B --- Make it Divisible by 25

**Problem Link:** [CF 1593B --- Make it Divisible by
25](https://codeforces.com/problemset/problem/1593/B)\
**Topic / Rating:** Digit Pattern / 900

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
string number

ACTUAL QUESTION:
min deletions for divisible by25
```

### 2. Extract Variables

``` text
Given:
string number

Useful mathematical state:
last two digits in {00,25,50,75}

Unknown / target:
min deletions for divisible by25
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
find pair from right

      ↓ simplify / transform

pattern search
```

### 4. Derive the Solution

``` text
last two digits in {00,25,50,75}
      ↓
find pair from right
      ↓
pattern search
      ↓
O(n)
```

**Final model:** `find pair from right` → **pattern search**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
find pair from right

     ↓ evaluate / simplify

pattern search

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 087
--- CF 1742F --- Smaller

**Problem Link:** [CF 1742F ---
Smaller](https://codeforces.com/problemset/problem/1742/F)\
**Topic / Rating:** Lexicographic/Invariant / 1200

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
string append queries

ACTUAL QUESTION:
whether s<t possible
```

### 2. Extract Variables

``` text
Given:
string append queries

Useful mathematical state:
presence of char >'a' dominates

Unknown / target:
whether s<t possible
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
track counts/flags

      ↓ simplify / transform

compressed state
```

### 4. Derive the Solution

``` text
presence of char >'a' dominates
      ↓
track counts/flags
      ↓
compressed state
      ↓
O(q)
```

**Final model:** `track counts/flags` → **compressed state**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
track counts/flags

     ↓ evaluate / simplify

compressed state

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 088
--- CF 1831A --- Twin Permutations

**Problem Link:** [CF 1831A --- Twin
Permutations](https://codeforces.com/problemset/problem/1831/A)\
**Topic / Rating:** Mapping / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
permutation

ACTUAL QUESTION:
construct complementary permutation
```

### 2. Extract Variables

``` text
Given:
permutation

Useful mathematical state:
b[i]=n+1-a[i]

Unknown / target:
construct complementary permutation
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
value reflection

      ↓ simplify / transform

direct transform
```

### 4. Derive the Solution

``` text
b[i]=n+1-a[i]
      ↓
value reflection
      ↓
direct transform
      ↓
O(n)
```

**Final model:** `value reflection` → **direct transform**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
value reflection

     ↓ evaluate / simplify

direct transform

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 089
--- CF 1900A --- Cover in Water

**Problem Link:** [CF 1900A --- Cover in
Water](https://codeforces.com/problemset/problem/1900/A)\
**Topic / Rating:** Run Length / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
string

ACTUAL QUESTION:
minimum operations to fill dots
```

### 2. Extract Variables

``` text
Given:
string

Useful mathematical state:
run of >=3 triggers shortcut; else count dots

Unknown / target:
minimum operations to fill dots
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
local pattern

      ↓ simplify / transform

case split
```

### 4. Derive the Solution

``` text
run of >=3 triggers shortcut; else count dots
      ↓
local pattern
      ↓
case split
      ↓
scan
```

**Final model:** `local pattern` → **case split**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
local pattern

     ↓ evaluate / simplify

case split

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 090
--- CF 1873B --- Good Kid

**Problem Link:** [CF 1873B --- Good
Kid](https://codeforces.com/problemset/problem/1873/B)\
**Topic / Rating:** Product/Greedy / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
digits

ACTUAL QUESTION:
increment one element to maximize product
```

### 2. Extract Variables

``` text
Given:
digits

Useful mathematical state:
increment smallest

Unknown / target:
increment one element to maximize product
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
exchange argument intuition

      ↓ simplify / transform

sort/min index
```

### 4. Derive the Solution

``` text
increment smallest
      ↓
exchange argument intuition
      ↓
sort/min index
      ↓
O(n)
```

**Final model:** `exchange argument intuition` → **sort/min index**.

------------------------------------------------------------------------

# Pattern 10 --- Prefix / Running-State Modeling

### 5. Dry Run --- How It Works

Example:

``` text
values:
[8, 2, 6, 3]

sort:
[2, 3, 6, 8]

Now apply:
exchange argument intuition

which reduces to:
sort/min index

Sorting exposes the mathematical order
that was hidden by the input arrangement.
```

## Problem 091 --- CF 116A --- Tram

**Problem Link:** [CF 116A ---
Tram](https://codeforces.com/problemset/problem/116/A)\
**Topic / Rating:** Prefix / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
enter/exit

ACTUAL QUESTION:
minimum capacity
```

### 2. Extract Variables

``` text
Given:
enter/exit

Useful mathematical state:
cur += in-out

Unknown / target:
minimum capacity
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
max(cur)

      ↓ simplify / transform

prefix occupancy
```

### 4. Derive the Solution

``` text
cur += in-out
      ↓
max(cur)
      ↓
prefix occupancy
      ↓
scan
```

**Final model:** `max(cur)` → **prefix occupancy**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
max(cur)

     ↓ evaluate / simplify

prefix occupancy

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 092
--- CF 363B --- Fence

**Problem Link:** [CF 363B ---
Fence](https://codeforces.com/problemset/problem/363/B)\
**Topic / Rating:** Sliding Window / 1100

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
array,k

ACTUAL QUESTION:
position of minimum k-length sum
```

### 2. Extract Variables

``` text
Given:
array,k

Useful mathematical state:
window sum

Unknown / target:
position of minimum k-length sum
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
min over contiguous k

      ↓ simplify / transform

prefix/sliding
```

### 4. Derive the Solution

``` text
window sum
      ↓
min over contiguous k
      ↓
prefix/sliding
      ↓
O(n)
```

**Final model:** `min over contiguous k` → **prefix/sliding**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
min over contiguous k

     ↓ evaluate / simplify

prefix/sliding

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 093
--- CF 276C --- Little Girl and Problem on Trees / Little Girl and
Maximum Sum

**Problem Link:** [CF 276C --- Little Girl and Problem on Trees / Little
Girl and Maximum Sum](https://codeforces.com/problemset/problem/276/C)\
**Topic / Rating:** Difference/Contribution / 1400

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
array,queries

ACTUAL QUESTION:
maximize total query sum by permutation
```

### 2. Extract Variables

``` text
Given:
array,queries

Useful mathematical state:
frequency each index used

Unknown / target:
maximize total query sum by permutation
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
sort values and frequencies same order

      ↓ simplify / transform

rearrangement inequality
```

### 4. Derive the Solution

``` text
frequency each index used
      ↓
sort values and frequencies same order
      ↓
rearrangement inequality
      ↓
diff array
```

**Final model:** `sort values and frequencies same order` →
**rearrangement inequality**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
sort values and frequencies same order

     ↓ evaluate / simplify

rearrangement inequality

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 094
--- CF 433B --- Kuriyama Mirai's Stones

**Problem Link:** [CF 433B --- Kuriyama Mirai's
Stones](https://codeforces.com/problemset/problem/433/B)\
**Topic / Rating:** Prefix Sum / 1200

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
array,queries

ACTUAL QUESTION:
range sums original/sorted
```

### 2. Extract Variables

``` text
Given:
array,queries

Useful mathematical state:
pref and sortedPref

Unknown / target:
range sums original/sorted
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
range=p[r]-p[l-1]

      ↓ simplify / transform

static range query
```

### 4. Derive the Solution

``` text
pref and sortedPref
      ↓
range=p[r]-p[l-1]
      ↓
static range query
      ↓
O(1)/query
```

**Final model:** `range=p[r]-p[l-1]` → **static range query**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
range=p[r]-p[l-1]

     ↓ evaluate / simplify

static range query

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 095
--- CF 313B --- Ilya and Queries

**Problem Link:** [CF 313B --- Ilya and
Queries](https://codeforces.com/problemset/problem/313/B)\
**Topic / Rating:** Prefix / 1100

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
string,queries

ACTUAL QUESTION:
count equal adjacent pairs in range
```

### 2. Extract Variables

``` text
Given:
string,queries

Useful mathematical state:
b[i]=[s[i]==s[i-1]]

Unknown / target:
count equal adjacent pairs in range
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
prefix b

      ↓ simplify / transform

range sum
```

### 4. Derive the Solution

``` text
b[i]=[s[i]==s[i-1]]
      ↓
prefix b
      ↓
range sum
      ↓
O(n+q)
```

**Final model:** `prefix b` → **range sum**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
prefix b

     ↓ evaluate / simplify

range sum

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 096
--- CF 327A --- Flipping Game

**Problem Link:** [CF 327A --- Flipping
Game](https://codeforces.com/problemset/problem/327/A)\
**Topic / Rating:** Transform/Kadane / 1200

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
binary array

ACTUAL QUESTION:
maximize ones after one flip
```

### 2. Extract Variables

``` text
Given:
binary array

Useful mathematical state:
gain: 0→+1,1→-1

Unknown / target:
maximize ones after one flip
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
max subarray gain

      ↓ simplify / transform

transform then Kadane
```

### 4. Derive the Solution

``` text
gain: 0→+1,1→-1
      ↓
max subarray gain
      ↓
transform then Kadane
      ↓
O(n)
```

**Final model:** `max subarray gain` → **transform then Kadane**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
max subarray gain

     ↓ evaluate / simplify

transform then Kadane

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 097
--- CF 580A --- Kefa and First Steps

**Problem Link:** [CF 580A --- Kefa and First
Steps](https://codeforces.com/problemset/problem/580/A)\
**Topic / Rating:** Run Length / 900

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
array

ACTUAL QUESTION:
longest nondecreasing contiguous segment
```

### 2. Extract Variables

``` text
Given:
array

Useful mathematical state:
current run based on a[i]>=a[i-1]

Unknown / target:
longest nondecreasing contiguous segment
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
max run

      ↓ simplify / transform

state
```

### 4. Derive the Solution

``` text
current run based on a[i]>=a[i-1]
      ↓
max run
      ↓
state
      ↓
O(n)
```

**Final model:** `max run` → **state**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
max run

     ↓ evaluate / simplify

state

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 098
--- CF 702A --- Maximum Increase

**Problem Link:** [CF 702A --- Maximum
Increase](https://codeforces.com/problemset/problem/702/A)\
**Topic / Rating:** Run Length / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
array

ACTUAL QUESTION:
longest strictly increasing contiguous segment
```

### 2. Extract Variables

``` text
Given:
array

Useful mathematical state:
current++ if a[i]>a[i-1]

Unknown / target:
longest strictly increasing contiguous segment
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
max run

      ↓ simplify / transform

state
```

### 4. Derive the Solution

``` text
current++ if a[i]>a[i-1]
      ↓
max run
      ↓
state
      ↓
O(n)
```

**Final model:** `max run` → **state**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
max run

     ↓ evaluate / simplify

state

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 099
--- CF 1829B --- Blank Space

**Problem Link:** [CF 1829B --- Blank
Space](https://codeforces.com/problemset/problem/1829/B)\
**Topic / Rating:** Run Length / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
binary array

ACTUAL QUESTION:
longest zeros
```

### 2. Extract Variables

``` text
Given:
binary array

Useful mathematical state:
current zero run

Unknown / target:
longest zeros
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
max

      ↓ simplify / transform

state
```

### 4. Derive the Solution

``` text
current zero run
      ↓
max
      ↓
state
      ↓
O(n)
```

**Final model:** `max` → **state**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
max

     ↓ evaluate / simplify

state

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 100
--- CF 1669F --- Eating Candies

**Problem Link:** [CF 1669F --- Eating
Candies](https://codeforces.com/problemset/problem/1669/F)\
**Topic / Rating:** Two Pointers/Prefix / 1100

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
array

ACTUAL QUESTION:
max elements eaten with equal left/right sums
```

### 2. Extract Variables

``` text
Given:
array

Useful mathematical state:
grow smaller side sum

Unknown / target:
max elements eaten with equal left/right sums
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
two monotone prefix sums

      ↓ simplify / transform

two pointers
```

### 4. Derive the Solution

``` text
grow smaller side sum
      ↓
two monotone prefix sums
      ↓
two pointers
      ↓
O(n)
```

**Final model:** `two monotone prefix sums` → **two pointers**.

------------------------------------------------------------------------

# Pattern 11 --- Constructive / Reachability Modeling

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
two monotone prefix sums

     ↓ evaluate / simplify

two pointers

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 101
--- CF 1690A --- Print a Pedestal

**Problem Link:** [CF 1690A --- Print a
Pedestal](https://codeforces.com/problemset/problem/1690/A)\
**Topic / Rating:** Constructive / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
n

ACTUAL QUESTION:
three distinct positive heights with ordering
```

### 2. Extract Variables

``` text
Given:
n

Useful mathematical state:
a+b+c=n, a<b<c

Unknown / target:
three distinct positive heights with ordering
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
near thirds

      ↓ simplify / transform

construct
```

### 4. Derive the Solution

``` text
a+b+c=n, a<b<c
      ↓
near thirds
      ↓
construct
      ↓
cases
```

**Final model:** `near thirds` → **construct**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
near thirds

     ↓ evaluate / simplify

construct

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 102
--- CF 1845A --- Forbidden Integer

**Problem Link:** [CF 1845A --- Forbidden
Integer](https://codeforces.com/problemset/problem/1845/A)\
**Topic / Rating:** Constructive / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
n,k,x

ACTUAL QUESTION:
sum allowed integers to n
```

### 2. Extract Variables

``` text
Given:
n,k,x

Useful mathematical state:
choose 1 if allowed else 2/3

Unknown / target:
sum allowed integers to n
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
simple basis values

      ↓ simplify / transform

construct
```

### 4. Derive the Solution

``` text
choose 1 if allowed else 2/3
      ↓
simple basis values
      ↓
construct
      ↓
cases
```

**Final model:** `simple basis values` → **construct**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
simple basis values

     ↓ evaluate / simplify

construct

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 103
--- CF 1878B --- Aleksa and Stack

**Problem Link:** [CF 1878B --- Aleksa and
Stack](https://codeforces.com/problemset/problem/1878/B)\
**Topic / Rating:** Constructive / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
n

ACTUAL QUESTION:
build valid sequence
```

### 2. Extract Variables

``` text
Given:
n

Useful mathematical state:
choose simple constant pattern

Unknown / target:
build valid sequence
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
satisfy local constraint by design

      ↓ simplify / transform

construction
```

### 4. Derive the Solution

``` text
choose simple constant pattern
      ↓
satisfy local constraint by design
      ↓
construction
      ↓
formula
```

**Final model:** `satisfy local constraint by design` →
**construction**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
satisfy local constraint by design

     ↓ evaluate / simplify

construction

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 104
--- CF 1741A --- Compare T-Shirt Sizes

**Problem Link:** [CF 1741A --- Compare T-Shirt
Sizes](https://codeforces.com/problemset/problem/1741/A)\
**Topic / Rating:** Ordering / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
size strings

ACTUAL QUESTION:
compare S/M/L with X count
```

### 2. Extract Variables

``` text
Given:
size strings

Useful mathematical state:
L: more X larger; S reverse

Unknown / target:
compare S/M/L with X count
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
map to signed scale

      ↓ simplify / transform

custom ordering
```

### 4. Derive the Solution

``` text
L: more X larger; S reverse
      ↓
map to signed scale
      ↓
custom ordering
      ↓
O(len)
```

**Final model:** `map to signed scale` → **custom ordering**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
map to signed scale

     ↓ evaluate / simplify

custom ordering

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 105
--- CF 1805B --- We Need the Zero / The String Has a Target

**Problem Link:** [CF 1805B --- We Need the Zero / The String Has a
Target](https://codeforces.com/problemset/problem/1805/B)\
**Topic / Rating:** String/Greedy / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
string

ACTUAL QUESTION:
move smallest char to front under operation
```

### 2. Extract Variables

``` text
Given:
string

Useful mathematical state:
global min char; choose rightmost occurrence

Unknown / target:
move smallest char to front under operation
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
stable reconstruction

      ↓ simplify / transform

greedy
```

### 4. Derive the Solution

``` text
global min char; choose rightmost occurrence
      ↓
stable reconstruction
      ↓
greedy
      ↓
O(n)
```

**Final model:** `stable reconstruction` → **greedy**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
stable reconstruction

     ↓ evaluate / simplify

greedy

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 106
--- CF 1833B --- Restore the Weather

**Problem Link:** [CF 1833B --- Restore the
Weather](https://codeforces.com/problemset/problem/1833/B)\
**Topic / Rating:** Sorting/Matching / 1000

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
arrays a,b,k

ACTUAL QUESTION:
permute b so |a[i]-b[i]|<=k
```

### 2. Extract Variables

``` text
Given:
arrays a,b,k

Useful mathematical state:
sort indices by a and b

Unknown / target:
permute b so |a[i]-b[i]|<=k
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
monotone matching

      ↓ simplify / transform

pair sorted orders
```

### 4. Derive the Solution

``` text
sort indices by a and b
      ↓
monotone matching
      ↓
pair sorted orders
      ↓
O(nlogn)
```

**Final model:** `monotone matching` → **pair sorted orders**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Example:

``` text
values:
[8, 2, 6, 3]

sort:
[2, 3, 6, 8]

Now apply:
monotone matching

which reduces to:
pair sorted orders

Sorting exposes the mathematical order
that was hidden by the input arrangement.
```

## Problem 107 --- CF 1793C --- Dora and Search

**Problem Link:** [CF 1793C --- Dora and
Search](https://codeforces.com/problemset/problem/1793/C)\
**Topic / Rating:** Two Pointers/Extremes / 1200

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
permutation segment

ACTUAL QUESTION:
find segment whose ends are neither min nor max
```

### 2. Extract Variables

``` text
Given:
permutation segment

Useful mathematical state:
peel if endpoint is current min/max

Unknown / target:
find segment whose ends are neither min nor max
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
maintain lo,hi

      ↓ simplify / transform

two pointers
```

### 4. Derive the Solution

``` text
peel if endpoint is current min/max
      ↓
maintain lo,hi
      ↓
two pointers
      ↓
O(n)
```

**Final model:** `maintain lo,hi` → **two pointers**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
maintain lo,hi

     ↓ evaluate / simplify

two pointers

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 108
--- CF 1881A --- Don't Try to Count

**Problem Link:** [CF 1881A --- Don't Try to
Count](https://codeforces.com/problemset/problem/1881/A)\
**Topic / Rating:** String/Doubling / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
x,s

ACTUAL QUESTION:
minimum doublings until s substring
```

### 2. Extract Variables

``` text
Given:
x,s

Useful mathematical state:
length only needs bounded doublings

Unknown / target:
minimum doublings until s substring
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
repeat x until long enough + margin

      ↓ simplify / transform

simulation bound
```

### 4. Derive the Solution

``` text
length only needs bounded doublings
      ↓
repeat x until long enough + margin
      ↓
simulation bound
      ↓
few iterations
```

**Final model:** `repeat x until long enough + margin` → **simulation
bound**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
repeat x until long enough + margin

     ↓ evaluate / simplify

simulation bound

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 109
--- CF 1858A --- Buttons

**Problem Link:** [CF 1858A ---
Buttons](https://codeforces.com/problemset/problem/1858/A)\
**Topic / Rating:** Game/Constructive / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
a,b,c

ACTUAL QUESTION:
winner
```

### 2. Extract Variables

``` text
Given:
a,b,c

Useful mathematical state:
shared c allocated alternately

Unknown / target:
winner
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
parity c

      ↓ simplify / transform

effective counts
```

### 4. Derive the Solution

``` text
shared c allocated alternately
      ↓
parity c
      ↓
effective counts
      ↓
casework
```

**Final model:** `parity c` → **effective counts**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Use a small valid input and keep only parity:

``` text
even → 0
odd  → 1

Substitute those states into:
parity c

Then simplify to:
effective counts

This shows why the exact magnitudes can be discarded
when only odd/even behavior affects the answer.
```

## Problem 110 --- CF 1899A --- Game with Integers

**Problem Link:** [CF 1899A --- Game with
Integers](https://codeforces.com/problemset/problem/1899/A)\
**Topic / Rating:** Modulo/Game / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
n

ACTUAL QUESTION:
winner
```

### 2. Extract Variables

``` text
Given:
n

Useful mathematical state:
moves ±1; multiples of3 structure

Unknown / target:
winner
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
n%3

      ↓ simplify / transform

residue game
```

### 4. Derive the Solution

``` text
moves ±1; multiples of3 structure
      ↓
n%3
      ↓
residue game
      ↓
O(1)
```

**Final model:** `n%3` → **residue game**.

------------------------------------------------------------------------

# Pattern 12 --- Bitwise / XOR Modeling

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
n%3

     ↓ evaluate / simplify

residue game

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 111
--- CF 1805A --- We Need the Zero

**Problem Link:** [CF 1805A --- We Need the
Zero](https://codeforces.com/problemset/problem/1805/A)\
**Topic / Rating:** XOR / 900

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
array

ACTUAL QUESTION:
find x so xor(a[i]^x)=0
```

### 2. Extract Variables

``` text
Given:
array

Useful mathematical state:
xorAll ^ (x repeated n times)

Unknown / target:
find x so xor(a[i]^x)=0
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
if n even x cancels; else x=xorAll

      ↓ simplify / transform

parity of n
```

### 4. Derive the Solution

``` text
xorAll ^ (x repeated n times)
      ↓
if n even x cancels; else x=xorAll
      ↓
parity of n
      ↓
xor
```

**Final model:** `if n even x cancels; else x=xorAll` → **parity of n**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Use a small valid input and keep only parity:

``` text
even → 0
odd  → 1

Substitute those states into:
if n even x cancels; else x=xorAll

Then simplify to:
parity of n

This shows why the exact magnitudes can be discarded
when only odd/even behavior affects the answer.
```

## Problem 112 --- CF 1872A --- Two Vessels

**Problem Link:** [CF 1872A --- Two
Vessels](https://codeforces.com/problemset/problem/1872/A)\
**Topic / Rating:** Arithmetic / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
a,b,c

ACTUAL QUESTION:
min moves balancing transfer c
```

### 2. Extract Variables

``` text
Given:
a,b,c

Useful mathematical state:
difference shrinks by 2c

Unknown / target:
min moves balancing transfer c
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
m*2c>=|a-b|

      ↓ simplify / transform

ceil division
```

### 4. Derive the Solution

``` text
difference shrinks by 2c
      ↓
m*2c>=|a-b|
      ↓
ceil division
      ↓
formula
```

**Final model:** `m*2c>=|a-b|` → **ceil division**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
m*2c>=|a-b|

     ↓ evaluate / simplify

ceil division

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 113
--- CF 1703A --- YES or YES?

**Problem Link:** [CF 1703A --- YES or
YES?](https://codeforces.com/problemset/problem/1703/A)\
**Topic / Rating:** String / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
word

ACTUAL QUESTION:
case-insensitive equality to yes
```

### 2. Extract Variables

``` text
Given:
word

Useful mathematical state:
normalize case

Unknown / target:
case-insensitive equality to yes
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
compare

      ↓ simplify / transform

canonicalization
```

### 4. Derive the Solution

``` text
normalize case
      ↓
compare
      ↓
canonicalization
      ↓
tolower
```

**Final model:** `compare` → **canonicalization**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
compare

     ↓ evaluate / simplify

canonicalization

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 114
--- CF 1624A --- Plus One on the Subset

**Problem Link:** [CF 1624A --- Plus One on the
Subset](https://codeforces.com/problemset/problem/1624/A)\
**Topic / Rating:** Range / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
array

ACTUAL QUESTION:
min ops equalize
```

### 2. Extract Variables

``` text
Given:
array

Useful mathematical state:
one op can increment chosen subset

Unknown / target:
min ops equalize
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
range max-min

      ↓ simplify / transform

potential
```

### 4. Derive the Solution

``` text
one op can increment chosen subset
      ↓
range max-min
      ↓
potential
      ↓
min/max
```

**Final model:** `range max-min` → **potential**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
range max-min

     ↓ evaluate / simplify

potential

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 115
--- CF 1220A --- Cards

**Problem Link:** [CF 1220A ---
Cards](https://codeforces.com/problemset/problem/1220/A)\
**Topic / Rating:** Frequency / 900

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
letters

ACTUAL QUESTION:
recover binary digits from letters
```

### 2. Extract Variables

``` text
Given:
letters

Useful mathematical state:
'z' uniquely identifies zero, 'n' one after ordering

Unknown / target:
recover binary digits from letters
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
count z and n

      ↓ simplify / transform

frequency signature
```

### 4. Derive the Solution

``` text
'z' uniquely identifies zero, 'n' one after ordering
      ↓
count z and n
      ↓
frequency signature
      ↓
output
```

**Final model:** `count z and n` → **frequency signature**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Use a tiny transformed sequence:

``` text
keys = [2, 2, 5, 2, 5]

frequency:
2 → 3
5 → 2

Apply:
count z and n

Then:
frequency signature

The dry run tracks frequencies/keys,
not the original story objects.
```

## Problem 116 --- CF 1362A --- Johnny and Ancient Computer

**Problem Link:** [CF 1362A --- Johnny and Ancient
Computer](https://codeforces.com/problemset/problem/1362/A)\
**Topic / Rating:** Powers/Ratio / 900

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
a,b

ACTUAL QUESTION:
min ×2/4/8 operations to transform
```

### 2. Extract Variables

``` text
Given:
a,b

Useful mathematical state:
ratio must be power of2

Unknown / target:
min ×2/4/8 operations to transform
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
exponent difference grouped by3

      ↓ simplify / transform

factorization
```

### 4. Derive the Solution

``` text
ratio must be power of2
      ↓
exponent difference grouped by3
      ↓
factorization
      ↓
formula
```

**Final model:** `exponent difference grouped by3` → **factorization**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
exponent difference grouped by3

     ↓ evaluate / simplify

factorization

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 117
--- CF 1095A --- Repeating Cipher

**Problem Link:** [CF 1095A --- Repeating
Cipher](https://codeforces.com/problemset/problem/1095/A)\
**Topic / Rating:** Index Pattern / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
encoded string

ACTUAL QUESTION:
decode chars at positions with jumps 1,2,3...
```

### 2. Extract Variables

``` text
Given:
encoded string

Useful mathematical state:
index += step

Unknown / target:
decode chars at positions with jumps 1,2,3...
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
triangular positions

      ↓ simplify / transform

simulation
```

### 4. Derive the Solution

``` text
index += step
      ↓
triangular positions
      ↓
simulation
      ↓
O(sqrt n)
```

**Final model:** `triangular positions` → **simulation**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
triangular positions

     ↓ evaluate / simplify

simulation

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 118
--- CF 1324A --- Yet Another Tetris Problem

**Problem Link:** [CF 1324A --- Yet Another Tetris
Problem](https://codeforces.com/problemset/problem/1324/A)\
**Topic / Rating:** Parity / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
array

ACTUAL QUESTION:
can equalize by subtracting 2
```

### 2. Extract Variables

``` text
Given:
array

Useful mathematical state:
differences preserve parity

Unknown / target:
can equalize by subtracting 2
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
all same parity

      ↓ simplify / transform

parity invariant
```

### 4. Derive the Solution

``` text
differences preserve parity
      ↓
all same parity
      ↓
parity invariant
      ↓
scan
```

**Final model:** `all same parity` → **parity invariant**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Use a small valid input and keep only parity:

``` text
even → 0
odd  → 1

Substitute those states into:
all same parity

Then simplify to:
parity invariant

This shows why the exact magnitudes can be discarded
when only odd/even behavior affects the answer.
```

## Problem 119 --- CF 1462A --- Favorite Sequence

**Problem Link:** [CF 1462A --- Favorite
Sequence](https://codeforces.com/problemset/problem/1462/A)\
**Topic / Rating:** Two Pointers / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
array

ACTUAL QUESTION:
reorder alternating left/right
```

### 2. Extract Variables

``` text
Given:
array

Useful mathematical state:
take l,r,l+1,r-1

Unknown / target:
reorder alternating left/right
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
index pattern

      ↓ simplify / transform

two pointers
```

### 4. Derive the Solution

``` text
take l,r,l+1,r-1
      ↓
index pattern
      ↓
two pointers
      ↓
O(n)
```

**Final model:** `index pattern` → **two pointers**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
index pattern

     ↓ evaluate / simplify

two pointers

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 120
--- CF 1619A --- Polycarp and Sums of Subsequences / Square String?

**Problem Link:** [CF 1619A --- Polycarp and Sums of Subsequences /
Square String?](https://codeforces.com/problemset/problem/1619/A)\
**Topic / Rating:** String / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
s

ACTUAL QUESTION:
is s two equal halves
```

### 2. Extract Variables

``` text
Given:
s

Useful mathematical state:
len even and first half=second

Unknown / target:
is s two equal halves
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
equation on substrings

      ↓ simplify / transform

direct
```

### 4. Derive the Solution

``` text
len even and first half=second
      ↓
equation on substrings
      ↓
direct
      ↓
O(n)
```

**Final model:** `equation on substrings` → **direct**.

------------------------------------------------------------------------

# Pattern 13 --- Mixed Blind Decoding

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
equation on substrings

     ↓ evaluate / simplify

direct

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 121
--- CF 1538C --- Challenging Cliffs / Number of Pairs

**Problem Link:** [CF 1538C --- Challenging Cliffs / Number of
Pairs](https://codeforces.com/problemset/problem/1538/C)\
**Topic / Rating:** Sorting+Counting / 1300

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
array,l,r

ACTUAL QUESTION:
count pair sums in interval
```

### 2. Extract Variables

``` text
Given:
array,l,r

Useful mathematical state:
F(r)-F(l-1)

Unknown / target:
count pair sums in interval
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
sort + two pointers

      ↓ simplify / transform

count bounded pairs
```

### 4. Derive the Solution

``` text
F(r)-F(l-1)
      ↓
sort + two pointers
      ↓
count bounded pairs
      ↓
O(nlogn)
```

**Final model:** `sort + two pointers` → **count bounded pairs**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Use a tiny transformed sequence:

``` text
keys = [2, 2, 5, 2, 5]

frequency:
2 → 3
5 → 2

Apply:
sort + two pointers

Then:
count bounded pairs

The dry run tracks frequencies/keys,
not the original story objects.
```

## Problem 122 --- CF 1475B --- New Year's Number

**Problem Link:** [CF 1475B --- New Year's
Number](https://codeforces.com/problemset/problem/1475/B)\
**Topic / Rating:** Diophantine+Modulo / 900

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
n

ACTUAL QUESTION:
2020a+2021b=n
```

### 2. Extract Variables

``` text
Given:
n

Useful mathematical state:
2021=2020+1

Unknown / target:
2020a+2021b=n
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
b=n%2020 candidate

      ↓ simplify / transform

feasibility
```

### 4. Derive the Solution

``` text
2021=2020+1
      ↓
b=n%2020 candidate
      ↓
feasibility
      ↓
O(1)
```

**Final model:** `b=n%2020 candidate` → **feasibility**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Use a small valid input and keep only parity:

``` text
even → 0
odd  → 1

Substitute those states into:
b=n%2020 candidate

Then simplify to:
feasibility

This shows why the exact magnitudes can be discarded
when only odd/even behavior affects the answer.
```

## Problem 123 --- CF 1374A --- Required Remainder

**Problem Link:** [CF 1374A --- Required
Remainder](https://codeforces.com/problemset/problem/1374/A)\
**Topic / Rating:** Modulo+Optimization / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
x,y,n

ACTUAL QUESTION:
largest k<=n with k%x=y
```

### 2. Extract Variables

``` text
Given:
x,y,n

Useful mathematical state:
k=tx+y

Unknown / target:
largest k<=n with k%x=y
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
maximize t under bound

      ↓ simplify / transform

floor
```

### 4. Derive the Solution

``` text
k=tx+y
      ↓
maximize t under bound
      ↓
floor
      ↓
O(1)
```

**Final model:** `maximize t under bound` → **floor**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
maximize t under bound

     ↓ evaluate / simplify

floor

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 124
--- CF 1551A --- Polycarp and Coins

**Problem Link:** [CF 1551A --- Polycarp and
Coins](https://codeforces.com/problemset/problem/1551/A)\
**Topic / Rating:** Equation+Balancing / 800

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
n

ACTUAL QUESTION:
c1+2c2=n with counts close
```

### 2. Extract Variables

``` text
Given:
n

Useful mathematical state:
near n/3

Unknown / target:
c1+2c2=n with counts close
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
n%3 cases

      ↓ simplify / transform

construct counts
```

### 4. Derive the Solution

``` text
near n/3
      ↓
n%3 cases
      ↓
construct counts
      ↓
O(1)
```

**Final model:** `n%3 cases` → **construct counts**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
n%3 cases

     ↓ evaluate / simplify

construct counts

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 125
--- CF 1593B --- Make it Divisible by 25

**Problem Link:** [CF 1593B --- Make it Divisible by
25](https://codeforces.com/problemset/problem/1593/B)\
**Topic / Rating:** Divisibility+String / 900

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
digits

ACTUAL QUESTION:
min deletions
```

### 2. Extract Variables

``` text
Given:
digits

Useful mathematical state:
last2 digits pattern

Unknown / target:
min deletions
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
search from right

      ↓ simplify / transform

four targets
```

### 4. Derive the Solution

``` text
last2 digits pattern
      ↓
search from right
      ↓
four targets
      ↓
O(n)
```

**Final model:** `search from right` → **four targets**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
search from right

     ↓ evaluate / simplify

four targets

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 126
--- CF 1669F --- Eating Candies

**Problem Link:** [CF 1669F --- Eating
Candies](https://codeforces.com/problemset/problem/1669/F)\
**Topic / Rating:** Prefix+Two Pointers / 1100

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
array

ACTUAL QUESTION:
equal left/right eaten sum maximize count
```

### 2. Extract Variables

``` text
Given:
array

Useful mathematical state:
monotone sums

Unknown / target:
equal left/right eaten sum maximize count
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
advance smaller side

      ↓ simplify / transform

two pointers
```

### 4. Derive the Solution

``` text
monotone sums
      ↓
advance smaller side
      ↓
two pointers
      ↓
O(n)
```

**Final model:** `advance smaller side` → **two pointers**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
advance smaller side

     ↓ evaluate / simplify

two pointers

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 127
--- CF 1793C --- Dora and Search

**Problem Link:** [CF 1793C --- Dora and
Search](https://codeforces.com/problemset/problem/1793/C)\
**Topic / Rating:** Extremes+Two Pointers / 1200

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
permutation

ACTUAL QUESTION:
find non-extreme-ended segment
```

### 2. Extract Variables

``` text
Given:
permutation

Useful mathematical state:
peel min/max endpoints

Unknown / target:
find non-extreme-ended segment
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
lo/hi invariant

      ↓ simplify / transform

two pointers
```

### 4. Derive the Solution

``` text
peel min/max endpoints
      ↓
lo/hi invariant
      ↓
two pointers
      ↓
O(n)
```

**Final model:** `lo/hi invariant` → **two pointers**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
lo/hi invariant

     ↓ evaluate / simplify

two pointers

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 128
--- CF 327A --- Flipping Game

**Problem Link:** [CF 327A --- Flipping
Game](https://codeforces.com/problemset/problem/327/A)\
**Topic / Rating:** Transform+Optimization / 1200

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
binary array

ACTUAL QUESTION:
one flip maximize ones
```

### 2. Extract Variables

``` text
Given:
binary array

Useful mathematical state:
gain map 0→+1,1→-1

Unknown / target:
one flip maximize ones
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
maximum subarray

      ↓ simplify / transform

Kadane
```

### 4. Derive the Solution

``` text
gain map 0→+1,1→-1
      ↓
maximum subarray
      ↓
Kadane
      ↓
O(n)
```

**Final model:** `maximum subarray` → **Kadane**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
maximum subarray

     ↓ evaluate / simplify

Kadane

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story. \## Problem 129
--- CF 1520D --- Same Differences

**Problem Link:** [CF 1520D --- Same
Differences](https://codeforces.com/problemset/problem/1520/D)\
**Topic / Rating:** Algebra+Frequency / 1200

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
array

ACTUAL QUESTION:
count special pairs
```

### 2. Extract Variables

``` text
Given:
array

Useful mathematical state:
a[j]-j=a[i]-i

Unknown / target:
count special pairs
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
equal transformed keys

      ↓ simplify / transform

hash frequency
```

### 4. Derive the Solution

``` text
a[j]-j=a[i]-i
      ↓
equal transformed keys
      ↓
hash frequency
      ↓
O(n)
```

**Final model:** `equal transformed keys` → **hash frequency**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Use a tiny transformed sequence:

``` text
keys = [2, 2, 5, 2, 5]

frequency:
2 → 3
5 → 2

Apply:
equal transformed keys

Then:
hash frequency

The dry run tracks frequencies/keys,
not the original story objects.
```

## Problem 130 --- CF 276C --- Little Girl and Maximum Sum

**Problem Link:** [CF 276C --- Little Girl and Maximum
Sum](https://codeforces.com/problemset/problem/276/C)\
**Topic / Rating:** Difference+Sorting / 1400

### 1. Remove the Story

``` text
IGNORE:
story names, characters, objects, theme

KEEP:
array,range queries

ACTUAL QUESTION:
maximize weighted sum
```

### 2. Extract Variables

``` text
Given:
array,range queries

Useful mathematical state:
usage frequency per index

Unknown / target:
maximize weighted sum
```

### 3. Formulate the Mathematics

``` text
Story condition
      ↓
sort both sequences

      ↓ simplify / transform

rearrangement
```

### 4. Derive the Solution

``` text
usage frequency per index
      ↓
sort both sequences
      ↓
rearrangement
      ↓
O((n+q)logn)
```

**Final model:** `sort both sequences` → **rearrangement**.

------------------------------------------------------------------------

### 5. Dry Run --- How It Works

Take the smallest valid sample from the official problem and trace only
the mathematical state:

``` text
Sample values
     ↓
extract variables
     ↓
substitute into:
sort both sequences

     ↓ evaluate / simplify

rearrangement

     ↓
answer
```

The important point is to verify **how the extracted variables move
through the formula**, without returning to the story.
