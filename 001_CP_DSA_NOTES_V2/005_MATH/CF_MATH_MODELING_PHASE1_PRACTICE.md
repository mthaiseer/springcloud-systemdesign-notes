# CF Mathematical Modeling --- Phase 1 Practice

> Goal: learn to turn a Codeforces story into variables, mathematics,
> and a solution.

## Table of Contents

-   [Pattern 1 --- Minimum Operations / Ceil
    Division](#pattern-1-minimum-operations-ceil-division)
    -   [CF 617A --- Elephant](#problem-001-cf-617a-elephant)
    -   [CF 1409A --- Yet Another Two Integers
        Problem](#problem-002-cf-1409a-yet-another-two-integers-problem)
    -   [CF 1353A --- Most Unstable
        Array](#problem-003-cf-1353a-most-unstable-array)
    -   [CF 1476A --- K-divisible
        Sum](#problem-004-cf-1476a-k-divisible-sum)
    -   [CF 151A --- Soft Drinking](#problem-005-cf-151a-soft-drinking)
    -   [CF 996A --- Hit the
        Lottery](#problem-006-cf-996a-hit-the-lottery)
    -   [CF 1669A --- Division?](#problem-007-cf-1669a-division)
    -   [CF 1742A --- Sum](#problem-008-cf-1742a-sum)
    -   [CF 1850A --- To My
        Critics](#problem-009-cf-1850a-to-my-critics)
    -   [CF 1878A --- How Much Does Daytona
        Cost?](#problem-010-cf-1878a-how-much-does-daytona-cost)
-   [Pattern 2 --- Algebra / Equation
    Formation](#pattern-2-algebra-equation-formation)
    -   [CF 734A --- Anton and
        Danik](#problem-011-cf-734a-anton-and-danik)
    -   [CF 677A --- Vanya and
        Fence](#problem-012-cf-677a-vanya-and-fence)
    -   [CF 71A --- Way Too Long
        Words](#problem-013-cf-71a-way-too-long-words)
    -   [CF 791A --- Bear and Big
        Brother](#problem-014-cf-791a-bear-and-big-brother)
    -   [CF 50A --- Domino piling](#problem-015-cf-50a-domino-piling)
    -   [CF 231A --- Team](#problem-016-cf-231a-team)
    -   [CF 200B --- Drinks](#problem-017-cf-200b-drinks)
    -   [CF 318A --- Even Odds](#problem-018-cf-318a-even-odds)
    -   [CF 486A --- Calculating
        Function](#problem-019-cf-486a-calculating-function)
    -   [CF 1399A --- Remove
        Smallest](#problem-020-cf-1399a-remove-smallest)
-   [Pattern 3 --- Bounds / Inequalities /
    Min-Max](#pattern-3-bounds-inequalities-min-max)
    -   [CF 1690A --- Print a
        Pedestal](#problem-021-cf-1690a-print-a-pedestal)
    -   [CF 1676A --- Lucky?](#problem-022-cf-1676a-lucky)
    -   [CF 1742B --- Increasing](#problem-023-cf-1742b-increasing)
    -   [CF 1791A --- Codeforces
        Checking](#problem-024-cf-1791a-codeforces-checking)
    -   [CF 1829A --- Love Story](#problem-025-cf-1829a-love-story)
    -   [CF 1873A --- Short Sort](#problem-026-cf-1873a-short-sort)
    -   [CF 1729A --- Two
        Elevators](#problem-027-cf-1729a-two-elevators)
    -   [CF 1805A --- We Need the
        Zero](#problem-028-cf-1805a-we-need-the-zero)
    -   [CF 1858A --- Buttons](#problem-029-cf-1858a-buttons)
    -   [CF 1899A --- Game with
        Integers](#problem-030-cf-1899a-game-with-integers)
-   [Pattern 4 --- Parity Modeling](#pattern-4-parity-modeling)
    -   [CF 4A --- Watermelon](#problem-031-cf-4a-watermelon)
    -   [CF 1296A --- Array with Odd
        Sum](#problem-032-cf-1296a-array-with-odd-sum)
    -   [CF 1857A --- Array
        Coloring](#problem-033-cf-1857a-array-coloring)
    -   [CF 1834A --- Unit Array](#problem-034-cf-1834a-unit-array)
    -   [CF 1367B --- Even Array](#problem-035-cf-1367b-even-array)
    -   [CF 1475A --- Odd Divisor](#problem-036-cf-1475a-odd-divisor)
    -   [CF 1669C --- Odd/Even
        Increments](#problem-037-cf-1669c-oddeven-increments)
    -   [CF 1624A --- Plus One on the
        Subset](#problem-038-cf-1624a-plus-one-on-the-subset)
    -   [CF 1788A --- One and Two](#problem-039-cf-1788a-one-and-two)
    -   [CF 1845A --- Forbidden
        Integer](#problem-040-cf-1845a-forbidden-integer)
-   [Pattern 5 --- Divisibility / GCD /
    LCM](#pattern-5-divisibility-gcd-lcm)
    -   [CF 1328A --- Divisibility
        Problem](#problem-041-cf-1328a-divisibility-problem)
    -   [CF 1343A --- Candies](#problem-042-cf-1343a-candies)
    -   [CF 1370A --- Maximum GCD](#problem-043-cf-1370a-maximum-gcd)
    -   [CF 1829C --- Mr. Perfectly
        Fine](#problem-044-cf-1829c-mr-perfectly-fine)
    -   [CF 1618A --- Polycarp and Sums of
        Subsequences](#problem-045-cf-1618a-polycarp-and-sums-of-subsequences)
    -   [CF 160A --- Twins](#problem-046-cf-160a-twins)
    -   [CF 1475B --- New Year's
        Number](#problem-047-cf-1475b-new-years-number)
    -   [CF 1593A --- Elections](#problem-048-cf-1593a-elections)
    -   [CF 1829B --- Blank Space](#problem-049-cf-1829b-blank-space)
    -   [CF 1877A --- Goals of
        Victory](#problem-050-cf-1877a-goals-of-victory)
-   [Pattern 6 --- Modulo / Cyclic
    Modeling](#pattern-6-modulo-cyclic-modeling)
    -   [CF 116A --- Tram](#problem-051-cf-116a-tram)
    -   [CF 266A --- Stones on the
        Table](#problem-052-cf-266a-stones-on-the-table)
    -   [CF 228A --- Is your horseshoe on the other
        hoof?](#problem-053-cf-228a-is-your-horseshoe-on-the-other-hoof)
    -   [CF 443A --- Anton and
        Letters](#problem-054-cf-443a-anton-and-letters)
    -   [CF 59A --- Word](#problem-055-cf-59a-word)
    -   [CF 236A --- Boy or Girl](#problem-056-cf-236a-boy-or-girl)
    -   [CF 785A --- Anton and
        Polyhedrons](#problem-057-cf-785a-anton-and-polyhedrons)
    -   [CF 703A --- Mishka and
        Game](#problem-058-cf-703a-mishka-and-game)
    -   [CF 734B --- Anton and
        Digits](#problem-059-cf-734b-anton-and-digits)
    -   [CF 1097A --- Gennady the Card
        Game](#problem-060-cf-1097a-gennady-the-card-game)
-   [Pattern 7 --- Counting / Frequency /
    Pairs](#pattern-7-counting-frequency-pairs)
    -   [CF 1520D --- Same
        Differences](#problem-061-cf-1520d-same-differences)
    -   [CF 1538C --- Challenging Cliffs / Number of
        Pairs](#problem-062-cf-1538c-challenging-cliffs-number-of-pairs)
    -   [CF 1669B --- Triple](#problem-063-cf-1669b-triple)
    -   [CF 1742C --- Stripes](#problem-064-cf-1742c-stripes)
    -   [CF 1791B --- Following
        Directions](#problem-065-cf-1791b-following-directions)
    -   [CF 1703B --- ICPC
        Balloons](#problem-066-cf-1703b-icpc-balloons)
    -   [CF 1722A --- Spell Check](#problem-067-cf-1722a-spell-check)
    -   [CF 1791C --- Prepend and
        Append](#problem-068-cf-1791c-prepend-and-append)
    -   [CF 1829D --- Gold Rush](#problem-069-cf-1829d-gold-rush)
    -   [CF 1878B --- Aleksa and
        Stack](#problem-070-cf-1878b-aleksa-and-stack)
-   [Pattern 8 --- Operation → Delta →
    Invariant](#pattern-8-operation-delta-invariant)
    -   [CF 1538B --- Friends and
        Candies](#problem-071-cf-1538b-friends-and-candies)
    -   [CF 1855A --- Dalton the
        Teacher](#problem-072-cf-1855a-dalton-the-teacher)
    -   [CF 1838A --- Blackboard
        List](#problem-073-cf-1838a-blackboard-list)
    -   [CF 1862B --- Sequence
        Game](#problem-074-cf-1862b-sequence-game)
    -   [CF 1798A --- Showstopper](#problem-075-cf-1798a-showstopper)
    -   [CF 660A --- Co-prime
        Array](#problem-076-cf-660a-co-prime-array)
    -   [CF 1367A --- Short
        Substrings](#problem-077-cf-1367a-short-substrings)
    -   [CF 1374A --- Required
        Remainder](#problem-078-cf-1374a-required-remainder)
    -   [CF 1551A --- Polycarp and
        Coins](#problem-079-cf-1551a-polycarp-and-coins)
    -   [CF 1818A --- Politics](#problem-080-cf-1818a-politics)
-   [Pattern 9 --- Sorting / Coordinate / Distance
    Modeling](#pattern-9-sorting-coordinate-distance-modeling)
    -   [CF 160A --- Twins](#problem-081-cf-160a-twins)
    -   [CF 1399A --- Remove
        Smallest](#problem-082-cf-1399a-remove-smallest)
    -   [CF 1760A --- Medium
        Number](#problem-083-cf-1760a-medium-number)
    -   [CF 1538A --- Stone Game](#problem-084-cf-1538a-stone-game)
    -   [CF 1729A --- Two
        Elevators](#problem-085-cf-1729a-two-elevators)
    -   [CF 1593B --- Make it Divisible by
        25](#problem-086-cf-1593b-make-it-divisible-by-25)
    -   [CF 1742F --- Smaller](#problem-087-cf-1742f-smaller)
    -   [CF 1831A --- Twin
        Permutations](#problem-088-cf-1831a-twin-permutations)
    -   [CF 1900A --- Cover in
        Water](#problem-089-cf-1900a-cover-in-water)
    -   [CF 1873B --- Good Kid](#problem-090-cf-1873b-good-kid)
-   [Pattern 10 --- Prefix / Running-State
    Modeling](#pattern-10-prefix-running-state-modeling)
    -   [CF 116A --- Tram](#problem-091-cf-116a-tram)
    -   [CF 363B --- Fence](#problem-092-cf-363b-fence)
    -   [CF 276C --- Little Girl and Problem on Trees / Little Girl and
        Maximum
        Sum](#problem-093-cf-276c-little-girl-and-problem-on-trees-little-girl-and-maximum-sum)
    -   [CF 433B --- Kuriyama Mirai's
        Stones](#problem-094-cf-433b-kuriyama-mirais-stones)
    -   [CF 313B --- Ilya and
        Queries](#problem-095-cf-313b-ilya-and-queries)
    -   [CF 327A --- Flipping Game](#problem-096-cf-327a-flipping-game)
    -   [CF 580A --- Kefa and First
        Steps](#problem-097-cf-580a-kefa-and-first-steps)
    -   [CF 702A --- Maximum
        Increase](#problem-098-cf-702a-maximum-increase)
    -   [CF 1829B --- Blank Space](#problem-099-cf-1829b-blank-space)
    -   [CF 1669F --- Eating
        Candies](#problem-100-cf-1669f-eating-candies)
-   [Pattern 11 --- Constructive / Reachability
    Modeling](#pattern-11-constructive-reachability-modeling)
    -   [CF 1690A --- Print a
        Pedestal](#problem-101-cf-1690a-print-a-pedestal)
    -   [CF 1845A --- Forbidden
        Integer](#problem-102-cf-1845a-forbidden-integer)
    -   [CF 1878B --- Aleksa and
        Stack](#problem-103-cf-1878b-aleksa-and-stack)
    -   [CF 1741A --- Compare T-Shirt
        Sizes](#problem-104-cf-1741a-compare-t-shirt-sizes)
    -   [CF 1805B --- We Need the Zero / The String Has a
        Target](#problem-105-cf-1805b-we-need-the-zero-the-string-has-a-target)
    -   [CF 1833B --- Restore the
        Weather](#problem-106-cf-1833b-restore-the-weather)
    -   [CF 1793C --- Dora and
        Search](#problem-107-cf-1793c-dora-and-search)
    -   [CF 1881A --- Don't Try to
        Count](#problem-108-cf-1881a-dont-try-to-count)
    -   [CF 1858A --- Buttons](#problem-109-cf-1858a-buttons)
    -   [CF 1899A --- Game with
        Integers](#problem-110-cf-1899a-game-with-integers)
-   [Pattern 12 --- Bitwise / XOR
    Modeling](#pattern-12-bitwise-xor-modeling)
    -   [CF 1805A --- We Need the
        Zero](#problem-111-cf-1805a-we-need-the-zero)
    -   [CF 1872A --- Two Vessels](#problem-112-cf-1872a-two-vessels)
    -   [CF 1703A --- YES or YES?](#problem-113-cf-1703a-yes-or-yes)
    -   [CF 1624A --- Plus One on the
        Subset](#problem-114-cf-1624a-plus-one-on-the-subset)
    -   [CF 1220A --- Cards](#problem-115-cf-1220a-cards)
    -   [CF 1362A --- Johnny and Ancient
        Computer](#problem-116-cf-1362a-johnny-and-ancient-computer)
    -   [CF 1095A --- Repeating
        Cipher](#problem-117-cf-1095a-repeating-cipher)
    -   [CF 1324A --- Yet Another Tetris
        Problem](#problem-118-cf-1324a-yet-another-tetris-problem)
    -   [CF 1462A --- Favorite
        Sequence](#problem-119-cf-1462a-favorite-sequence)
    -   [CF 1619A --- Polycarp and Sums of Subsequences / Square
        String?](#problem-120-cf-1619a-polycarp-and-sums-of-subsequences-square-string)
-   [Pattern 13 --- Mixed Blind
    Decoding](#pattern-13-mixed-blind-decoding)
    -   [CF 1538C --- Challenging Cliffs / Number of
        Pairs](#problem-121-cf-1538c-challenging-cliffs-number-of-pairs)
    -   [CF 1475B --- New Year's
        Number](#problem-122-cf-1475b-new-years-number)
    -   [CF 1374A --- Required
        Remainder](#problem-123-cf-1374a-required-remainder)
    -   [CF 1551A --- Polycarp and
        Coins](#problem-124-cf-1551a-polycarp-and-coins)
    -   [CF 1593B --- Make it Divisible by
        25](#problem-125-cf-1593b-make-it-divisible-by-25)
    -   [CF 1669F --- Eating
        Candies](#problem-126-cf-1669f-eating-candies)
    -   [CF 1793C --- Dora and
        Search](#problem-127-cf-1793c-dora-and-search)
    -   [CF 327A --- Flipping Game](#problem-128-cf-327a-flipping-game)
    -   [CF 1520D --- Same
        Differences](#problem-129-cf-1520d-same-differences)
    -   [CF 276C --- Little Girl and Maximum
        Sum](#problem-130-cf-276c-little-girl-and-maximum-sum)

## How to Use Each Problem

Read the official problem first. Then follow exactly this chain:

``` text
FULL STORY → REMOVE NOUNS → EXTRACT VARIABLES → FORMULA → DERIVATION → DRY RUN
```

# Pattern 1 --- Minimum Operations / Ceil Division

## Problem 001 --- CF 617A --- Elephant

**Problem:** [CF 617A --- Elephant]()\
**Topic / Rating:** Arithmetic / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives x. The task is to minimum moves to reach x with
+1..+5.

**What must we output?** minimum moves to reach x with +1..+5

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: x. Mathematical state → D=x, K=5.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: x. Useful state: D=x, K=5. Unknown: minimum moves to reach x with +1..+5.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
5m >= x
```

Now simplify / rearrange / classify it:

``` text
5m >= x
        ↓
ceil(x/5)
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `5m >= x`. Then simplify/rearrange it
to `ceil(x/5)`. This is the point where the story disappears and the
solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
5m >= x
  ↓ simplify
ceil(x/5)
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `5m >= x`, evaluate it step by
step, and verify the transformed condition `ceil(x/5)`. The final
value/condition gives minimum moves to reach x with +1..+5.

------------------------------------------------------------------------

## Problem 002 --- CF 1409A --- Yet Another Two Integers Problem

**Problem:** [CF 1409A --- Yet Another Two Integers Problem]()\
**Topic / Rating:** Arithmetic / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives a,b. The task is to minimum operations to make a=b
using ±1..10.

**What must we output?** minimum operations to make a=b using ±1..10

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: a,b. Mathematical state → D=|a-b|, K=10.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: a,b. Useful state: D=|a-b|, K=10. Unknown: minimum operations to make a=b using ±1..10.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
10m >= |a-b|
```

Now simplify / rearrange / classify it:

``` text
10m >= |a-b|
        ↓
ceil(|a-b|/10)
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `10m >= |a-b|`. Then simplify/rearrange
it to `ceil(|a-b|/10)`. This is the point where the story disappears and
the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
10m >= |a-b|
  ↓ simplify
ceil(|a-b|/10)
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `10m >= |a-b|`, evaluate it
step by step, and verify the transformed condition `ceil(|a-b|/10)`. The
final value/condition gives minimum operations to make a=b using ±1..10.

------------------------------------------------------------------------

## Problem 003 --- CF 1353A --- Most Unstable Array

**Problem:** [CF 1353A --- Most Unstable Array]()\
**Topic / Rating:** Formula / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives n,m. The task is to maximize sum of adjacent
absolute differences under bounds.

**What must we output?** maximize sum of adjacent absolute differences
under bounds

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: n,m. Mathematical state → endpoints/bounds matter.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: n,m. Useful state: endpoints/bounds matter. Unknown: maximize sum of adjacent absolute differences under bounds.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
each transition <= m
```

Now simplify / rearrange / classify it:

``` text
each transition <= m
        ↓
construct extremal arrangement
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `each transition <= m`. Then
simplify/rearrange it to `construct extremal arrangement`. This is the
point where the story disappears and the solution follows from the
transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
each transition <= m
  ↓ simplify
construct extremal arrangement
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `each transition <= m`,
evaluate it step by step, and verify the transformed condition
`construct extremal arrangement`. The final value/condition gives
maximize sum of adjacent absolute differences under bounds.

------------------------------------------------------------------------

## Problem 004 --- CF 1476A --- K-divisible Sum

**Problem:** [CF 1476A --- K-divisible Sum]()\
**Topic / Rating:** Bounds / 1000

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives n,k. The task is to minimum possible maximum element
while sum is divisible by k.

**What must we output?** minimum possible maximum element while sum is
divisible by k

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: n,k. Mathematical state → total S >= n and S multiple of k.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: n,k. Useful state: total S >= n and S multiple of k. Unknown: minimum possible maximum element while sum is divisible by k.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
S = smallest multiple of k >= n
```

Now simplify / rearrange / classify it:

``` text
S = smallest multiple of k >= n
        ↓
ceil(S/n)
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `S = smallest multiple of k >= n`. Then
simplify/rearrange it to `ceil(S/n)`. This is the point where the story
disappears and the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
S = smallest multiple of k >= n
  ↓ simplify
ceil(S/n)
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into
`S = smallest multiple of k >= n`, evaluate it step by step, and verify
the transformed condition `ceil(S/n)`. The final value/condition gives
minimum possible maximum element while sum is divisible by k.

------------------------------------------------------------------------

## Problem 005 --- CF 151A --- Soft Drinking

**Problem:** [CF 151A --- Soft Drinking]()\
**Topic / Rating:** Capacity / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives n,k,l,c,d,p,nl,np. The task is to number of toasts
per friend.

**What must we output?** number of toasts per friend

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: n,k,l,c,d,p,nl,np. Mathematical state → three resources.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: n,k,l,c,d,p,nl,np. Useful state: three resources. Unknown: number of toasts per friend.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
min(drink/nl,limes,salt/np)/n
```

Now simplify / rearrange / classify it:

``` text
min(drink/nl,limes,salt/np)/n
        ↓
limiting resource
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `min(drink/nl,limes,salt/np)/n`. Then
simplify/rearrange it to `limiting resource`. This is the point where
the story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
min(drink/nl,limes,salt/np)/n
  ↓ simplify
limiting resource
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into
`min(drink/nl,limes,salt/np)/n`, evaluate it step by step, and verify
the transformed condition `limiting resource`. The final value/condition
gives number of toasts per friend.

------------------------------------------------------------------------

## Problem 006 --- CF 996A --- Hit the Lottery

**Problem:** [CF 996A --- Hit the Lottery]()\
**Topic / Rating:** Greedy/Division / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives n. The task is to minimum notes using 100,20,10,5,1.

**What must we output?** minimum notes using 100,20,10,5,1

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: n. Mathematical state → largest denomination dominates.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: n. Useful state: largest denomination dominates. Unknown: minimum notes using 100,20,10,5,1.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
q=n/d
```

Now simplify / rearrange / classify it:

``` text
q=n/d
        ↓
sum quotients
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `q=n/d`. Then simplify/rearrange it to
`sum quotients`. This is the point where the story disappears and the
solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
q=n/d
  ↓ simplify
sum quotients
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `q=n/d`, evaluate it step by
step, and verify the transformed condition `sum quotients`. The final
value/condition gives minimum notes using 100,20,10,5,1.

------------------------------------------------------------------------

## Problem 007 --- CF 1669A --- Division?

**Problem:** [CF 1669A --- Division?]()\
**Topic / Rating:** Inequality / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives rating. The task is to classify rating into
interval.

**What must we output?** classify rating into interval

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: rating. Mathematical state → numeric boundaries.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: rating. Useful state: numeric boundaries. Unknown: classify rating into interval.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
compare rating with cutoffs
```

Now simplify / rearrange / classify it:

``` text
compare rating with cutoffs
        ↓
interval classification
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `compare rating with cutoffs`. Then
simplify/rearrange it to `interval classification`. This is the point
where the story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
compare rating with cutoffs
  ↓ simplify
interval classification
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `compare rating with cutoffs`,
evaluate it step by step, and verify the transformed condition
`interval classification`. The final value/condition gives classify
rating into interval.

------------------------------------------------------------------------

## Problem 008 --- CF 1742A --- Sum

**Problem:** [CF 1742A --- Sum]()\
**Topic / Rating:** Equation / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives a,b,c. The task is to whether one number equals sum
of other two.

**What must we output?** whether one number equals sum of other two

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: a,b,c. Mathematical state → test 3 equations.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: a,b,c. Useful state: test 3 equations. Unknown: whether one number equals sum of other two.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
a+b=c etc.
```

Now simplify / rearrange / classify it:

``` text
a+b=c etc.
        ↓
direct feasibility
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `a+b=c etc.`. Then simplify/rearrange
it to `direct feasibility`. This is the point where the story disappears
and the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
a+b=c etc.
  ↓ simplify
direct feasibility
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `a+b=c etc.`, evaluate it step
by step, and verify the transformed condition `direct feasibility`. The
final value/condition gives whether one number equals sum of other two.

------------------------------------------------------------------------

## Problem 009 --- CF 1850A --- To My Critics

**Problem:** [CF 1850A --- To My Critics]()\
**Topic / Rating:** Bounds / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives a,b,c. The task is to whether any pair sum \>=10.

**What must we output?** whether any pair sum \>=10

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: a,b,c. Mathematical state → only 3 pairs.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: a,b,c. Useful state: only 3 pairs. Unknown: whether any pair sum >=10.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
max pair sum
```

Now simplify / rearrange / classify it:

``` text
max pair sum
        ↓
sort or direct checks
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `max pair sum`. Then simplify/rearrange
it to `sort or direct checks`. This is the point where the story
disappears and the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
max pair sum
  ↓ simplify
sort or direct checks
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `max pair sum`, evaluate it
step by step, and verify the transformed condition
`sort or direct checks`. The final value/condition gives whether any
pair sum \>=10.

------------------------------------------------------------------------

## Problem 010 --- CF 1878A --- How Much Does Daytona Cost?

**Problem:** [CF 1878A --- How Much Does Daytona Cost?]()\
**Topic / Rating:** Existence / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives n,k,array. The task is to whether k appears.

**What must we output?** whether k appears

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: n,k,array. Mathematical state → target is existence.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: n,k,array. Useful state: target is existence. Unknown: whether k appears.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
∃i: a[i]=k
```

Now simplify / rearrange / classify it:

``` text
∃i: a[i]=k
        ↓
linear scan
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `∃i: a[i]=k`. Then simplify/rearrange
it to `linear scan`. This is the point where the story disappears and
the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
∃i: a[i]=k
  ↓ simplify
linear scan
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `∃i: a[i]=k`, evaluate it step
by step, and verify the transformed condition `linear scan`. The final
value/condition gives whether k appears.

------------------------------------------------------------------------

# Pattern 2 --- Algebra / Equation Formation

## Problem 011 --- CF 734A --- Anton and Danik

**Problem:** [CF 734A --- Anton and Danik]()\
**Topic / Rating:** Counting / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives n,string. The task is to who won more games.

**What must we output?** who won more games

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: n,string. Mathematical state → A=count('A'), D=count('D').
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: n,string. Useful state: A=count('A'), D=count('D'). Unknown: who won more games.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
compare A and D
```

Now simplify / rearrange / classify it:

``` text
compare A and D
        ↓
sign of A-D
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `compare A and D`. Then
simplify/rearrange it to `sign of A-D`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
compare A and D
  ↓ simplify
sign of A-D
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `compare A and D`, evaluate it
step by step, and verify the transformed condition `sign of A-D`. The
final value/condition gives who won more games.

------------------------------------------------------------------------

## Problem 012 --- CF 677A --- Vanya and Fence

**Problem:** [CF 677A --- Vanya and Fence]()\
**Topic / Rating:** Formula / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives n,h,heights. The task is to total width.

**What must we output?** total width

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: n,h,heights. Mathematical state → each person contributes 1 or 2.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: n,h,heights. Useful state: each person contributes 1 or 2. Unknown: total width.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
sum (a[i]>h ? 2:1)
```

Now simplify / rearrange / classify it:

``` text
sum (a[i]>h ? 2:1)
        ↓
contribution sum
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `sum (a[i]>h ? 2:1)`. Then
simplify/rearrange it to `contribution sum`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
sum (a[i]>h ? 2:1)
  ↓ simplify
contribution sum
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `sum (a[i]>h ? 2:1)`, evaluate
it step by step, and verify the transformed condition
`contribution sum`. The final value/condition gives total width.

------------------------------------------------------------------------

## Problem 013 --- CF 71A --- Way Too Long Words

**Problem:** [CF 71A --- Way Too Long Words]()\
**Topic / Rating:** String/Formula / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives word. The task is to abbreviate if length\>10.

**What must we output?** abbreviate if length\>10

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: word. Mathematical state → first + (len-2) + last.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: word. Useful state: first + (len-2) + last. Unknown: abbreviate if length>10.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
length condition
```

Now simplify / rearrange / classify it:

``` text
length condition
        ↓
direct construction
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `length condition`. Then
simplify/rearrange it to `direct construction`. This is the point where
the story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
length condition
  ↓ simplify
direct construction
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `length condition`, evaluate
it step by step, and verify the transformed condition
`direct construction`. The final value/condition gives abbreviate if
length\>10.

------------------------------------------------------------------------

## Problem 014 --- CF 791A --- Bear and Big Brother

**Problem:** [CF 791A --- Bear and Big Brother]()\
**Topic / Rating:** Growth / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives a,b. The task is to years until 3\^t a \> 2\^t b.

**What must we output?** years until 3\^t a \> 2\^t b

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: a,b. Mathematical state → simulate multiplicative equation.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: a,b. Useful state: simulate multiplicative equation. Unknown: years until 3^t a > 2^t b.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
a*=3,b*=2
```

Now simplify / rearrange / classify it:

``` text
a*=3,b*=2
        ↓
first t with a>b
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `a*=3,b*=2`. Then simplify/rearrange it
to `first t with a>b`. This is the point where the story disappears and
the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
a*=3,b*=2
  ↓ simplify
first t with a>b
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `a*=3,b*=2`, evaluate it step
by step, and verify the transformed condition `first t with a>b`. The
final value/condition gives years until 3\^t a \> 2\^t b.

------------------------------------------------------------------------

## Problem 015 --- CF 50A --- Domino piling

**Problem:** [CF 50A --- Domino piling]()\
**Topic / Rating:** Counting / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives m,n. The task is to max dominoes in grid.

**What must we output?** max dominoes in grid

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: m,n. Mathematical state → each domino covers 2 cells.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: m,n. Useful state: each domino covers 2 cells. Unknown: max dominoes in grid.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
2x <= mn
```

Now simplify / rearrange / classify it:

``` text
2x <= mn
        ↓
floor(mn/2)
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `2x <= mn`. Then simplify/rearrange it
to `floor(mn/2)`. This is the point where the story disappears and the
solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
2x <= mn
  ↓ simplify
floor(mn/2)
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `2x <= mn`, evaluate it step
by step, and verify the transformed condition `floor(mn/2)`. The final
value/condition gives max dominoes in grid.

------------------------------------------------------------------------

## Problem 016 --- CF 231A --- Team

**Problem:** [CF 231A --- Team]()\
**Topic / Rating:** Counting / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives triples. The task is to count problems with \>=2
yes.

**What must we output?** count problems with \>=2 yes

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: triples. Mathematical state → sum triple >=2.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: triples. Useful state: sum triple >=2. Unknown: count problems with >=2 yes.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
indicator contribution
```

Now simplify / rearrange / classify it:

``` text
indicator contribution
        ↓
count
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `indicator contribution`. Then
simplify/rearrange it to `count`. This is the point where the story
disappears and the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
indicator contribution
  ↓ simplify
count
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `indicator contribution`,
evaluate it step by step, and verify the transformed condition `count`.
The final value/condition gives count problems with \>=2 yes.

------------------------------------------------------------------------

## Problem 017 --- CF 200B --- Drinks

**Problem:** [CF 200B --- Drinks]()\
**Topic / Rating:** Average / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives n,p. The task is to orange percentage.

**What must we output?** orange percentage

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: n,p. Mathematical state → average of p.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: n,p. Useful state: average of p. Unknown: orange percentage.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
sum/n
```

Now simplify / rearrange / classify it:

``` text
sum/n
        ↓
mean
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `sum/n`. Then simplify/rearrange it to
`mean`. This is the point where the story disappears and the solution
follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
sum/n
  ↓ simplify
mean
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `sum/n`, evaluate it step by
step, and verify the transformed condition `mean`. The final
value/condition gives orange percentage.

------------------------------------------------------------------------

## Problem 018 --- CF 318A --- Even Odds

**Problem:** [CF 318A --- Even Odds]()\
**Topic / Rating:** Index Mapping / 900

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives n,k. The task is to kth in odds then evens.

**What must we output?** kth in odds then evens

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: n,k. Mathematical state → oddCount=(n+1)/2.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: n,k. Useful state: oddCount=(n+1)/2. Unknown: kth in odds then evens.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
piecewise index mapping
```

Now simplify / rearrange / classify it:

``` text
piecewise index mapping
        ↓
if k<=oddCount
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `piecewise index mapping`. Then
simplify/rearrange it to `if k<=oddCount`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
piecewise index mapping
  ↓ simplify
if k<=oddCount
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `piecewise index mapping`,
evaluate it step by step, and verify the transformed condition
`if k<=oddCount`. The final value/condition gives kth in odds then
evens.

------------------------------------------------------------------------

## Problem 019 --- CF 486A --- Calculating Function

**Problem:** [CF 486A --- Calculating Function]()\
**Topic / Rating:** Formula / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives n. The task is to alternating sum -1+2-3+....

**What must we output?** alternating sum -1+2-3+...

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: n. Mathematical state → pair terms.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: n. Useful state: pair terms. Unknown: alternating sum -1+2-3+....
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
even n -> n/2; odd -> -(n+1)/2
```

Now simplify / rearrange / classify it:

``` text
even n -> n/2; odd -> -(n+1)/2
        ↓
closed form
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `even n -> n/2; odd -> -(n+1)/2`. Then
simplify/rearrange it to `closed form`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
even n -> n/2; odd -> -(n+1)/2
  ↓ simplify
closed form
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into
`even n -> n/2; odd -> -(n+1)/2`, evaluate it step by step, and verify
the transformed condition `closed form`. The final value/condition gives
alternating sum -1+2-3+....

------------------------------------------------------------------------

## Problem 020 --- CF 1399A --- Remove Smallest

**Problem:** [CF 1399A --- Remove Smallest]()\
**Topic / Rating:** Sorting / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives array. The task is to can repeatedly remove smaller
when diff\<=1.

**What must we output?** can repeatedly remove smaller when diff\<=1

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: array. Mathematical state → sorted adjacent gaps encode feasibility.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: array. Useful state: sorted adjacent gaps encode feasibility. Unknown: can repeatedly remove smaller when diff<=1.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
max adjacent diff<=1
```

Now simplify / rearrange / classify it:

``` text
max adjacent diff<=1
        ↓
sort + check
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `max adjacent diff<=1`. Then
simplify/rearrange it to `sort + check`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
max adjacent diff<=1
  ↓ simplify
sort + check
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `max adjacent diff<=1`,
evaluate it step by step, and verify the transformed condition
`sort + check`. The final value/condition gives can repeatedly remove
smaller when diff\<=1.

------------------------------------------------------------------------

# Pattern 3 --- Bounds / Inequalities / Min-Max

## Problem 021 --- CF 1690A --- Print a Pedestal

**Problem:** [CF 1690A --- Print a Pedestal]()\
**Topic / Rating:** Construction / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives n. The task is to split n into 3 positive distinct
heights with middle ordering.

**What must we output?** split n into 3 positive distinct heights with
middle ordering

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: n. Mathematical state → x<y<z and sum n.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: n. Useful state: x<y<z and sum n. Unknown: split n into 3 positive distinct heights with middle ordering.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
near n/3 then adjust
```

Now simplify / rearrange / classify it:

``` text
near n/3 then adjust
        ↓
construct around thirds
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `near n/3 then adjust`. Then
simplify/rearrange it to `construct around thirds`. This is the point
where the story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
near n/3 then adjust
  ↓ simplify
construct around thirds
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `near n/3 then adjust`,
evaluate it step by step, and verify the transformed condition
`construct around thirds`. The final value/condition gives split n into
3 positive distinct heights with middle ordering.

------------------------------------------------------------------------

## Problem 022 --- CF 1676A --- Lucky?

**Problem:** [CF 1676A --- Lucky?]()\
**Topic / Rating:** Equation / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives 6-digit string. The task is to first 3 digit sum
equals last 3.

**What must we output?** first 3 digit sum equals last 3

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: 6-digit string. Mathematical state → S1,S2.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: 6-digit string. Useful state: S1,S2. Unknown: first 3 digit sum equals last 3.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
S1=S2
```

Now simplify / rearrange / classify it:

``` text
S1=S2
        ↓
direct compare
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `S1=S2`. Then simplify/rearrange it to
`direct compare`. This is the point where the story disappears and the
solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
S1=S2
  ↓ simplify
direct compare
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `S1=S2`, evaluate it step by
step, and verify the transformed condition `direct compare`. The final
value/condition gives first 3 digit sum equals last 3.

------------------------------------------------------------------------

## Problem 023 --- CF 1742B --- Increasing

**Problem:** [CF 1742B --- Increasing]()\
**Topic / Rating:** Distinctness / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives array. The task is to can permute to strictly
increasing.

**What must we output?** can permute to strictly increasing

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: array. Mathematical state → strictly increasing permutation iff all distinct.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: array. Useful state: strictly increasing permutation iff all distinct. Unknown: can permute to strictly increasing.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
freq<=1
```

Now simplify / rearrange / classify it:

``` text
freq<=1
        ↓
set size=n
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `freq<=1`. Then simplify/rearrange it
to `set size=n`. This is the point where the story disappears and the
solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
freq<=1
  ↓ simplify
set size=n
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `freq<=1`, evaluate it step by
step, and verify the transformed condition `set size=n`. The final
value/condition gives can permute to strictly increasing.

------------------------------------------------------------------------

## Problem 024 --- CF 1791A --- Codeforces Checking

**Problem:** [CF 1791A --- Codeforces Checking]()\
**Topic / Rating:** Membership / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives char c. The task is to whether c belongs to
'codeforces'.

**What must we output?** whether c belongs to 'codeforces'

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: char c. Mathematical state → c ∈ fixed set.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: char c. Useful state: c ∈ fixed set. Unknown: whether c belongs to 'codeforces'.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
find char
```

Now simplify / rearrange / classify it:

``` text
find char
        ↓
membership
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `find char`. Then simplify/rearrange it
to `membership`. This is the point where the story disappears and the
solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
find char
  ↓ simplify
membership
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `find char`, evaluate it step
by step, and verify the transformed condition `membership`. The final
value/condition gives whether c belongs to 'codeforces'.

------------------------------------------------------------------------

## Problem 025 --- CF 1829A --- Love Story

**Problem:** [CF 1829A --- Love Story]()\
**Topic / Rating:** Hamming Distance / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives string. The task is to positions differing from
'codeforces'.

**What must we output?** positions differing from 'codeforces'

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: string. Mathematical state → indicator [s[i]!=t[i]].
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: string. Useful state: indicator [s[i]!=t[i]]. Unknown: positions differing from 'codeforces'.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
sum indicators
```

Now simplify / rearrange / classify it:

``` text
sum indicators
        ↓
Hamming distance
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `sum indicators`. Then
simplify/rearrange it to `Hamming distance`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
sum indicators
  ↓ simplify
Hamming distance
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `sum indicators`, evaluate it
step by step, and verify the transformed condition `Hamming distance`.
The final value/condition gives positions differing from 'codeforces'.

------------------------------------------------------------------------

## Problem 026 --- CF 1873A --- Short Sort

**Problem:** [CF 1873A --- Short Sort]()\
**Topic / Rating:** Permutation / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives 3-char string. The task is to can sort with \<=1
swap.

**What must we output?** can sort with \<=1 swap

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: 3-char string. Mathematical state → target='abc'.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: 3-char string. Useful state: target='abc'. Unknown: can sort with <=1 swap.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
mismatch count 0 or 2
```

Now simplify / rearrange / classify it:

``` text
mismatch count 0 or 2
        ↓
compare permutations
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `mismatch count 0 or 2`. Then
simplify/rearrange it to `compare permutations`. This is the point where
the story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
mismatch count 0 or 2
  ↓ simplify
compare permutations
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `mismatch count 0 or 2`,
evaluate it step by step, and verify the transformed condition
`compare permutations`. The final value/condition gives can sort with
\<=1 swap.

------------------------------------------------------------------------

## Problem 027 --- CF 1729A --- Two Elevators

**Problem:** [CF 1729A --- Two Elevators]()\
**Topic / Rating:** Distance / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives a,b,c. The task is to which elevator reaches floor1
sooner.

**What must we output?** which elevator reaches floor1 sooner

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: a,b,c. Mathematical state → t1=a-1, t2=|b-c|+c-1.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: a,b,c. Useful state: t1=a-1, t2=|b-c|+c-1. Unknown: which elevator reaches floor1 sooner.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
compare times
```

Now simplify / rearrange / classify it:

``` text
compare times
        ↓
min comparison
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `compare times`. Then
simplify/rearrange it to `min comparison`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
compare times
  ↓ simplify
min comparison
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `compare times`, evaluate it
step by step, and verify the transformed condition `min comparison`. The
final value/condition gives which elevator reaches floor1 sooner.

------------------------------------------------------------------------

## Problem 028 --- CF 1805A --- We Need the Zero

**Problem:** [CF 1805A --- We Need the Zero]()\
**Topic / Rating:** XOR/Bounds / 900

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives array. The task is to find x making xor transformed
zero.

**What must we output?** find x making xor transformed zero

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: array. Mathematical state → xor(a_i xor x).
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: array. Useful state: xor(a_i xor x). Unknown: find x making xor transformed zero.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
parity of n controls x contribution
```

Now simplify / rearrange / classify it:

``` text
parity of n controls x contribution
        ↓
derive xor equation
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `parity of n controls x contribution`.
Then simplify/rearrange it to `derive xor equation`. This is the point
where the story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
parity of n controls x contribution
  ↓ simplify
derive xor equation
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into
`parity of n controls x contribution`, evaluate it step by step, and
verify the transformed condition `derive xor equation`. The final
value/condition gives find x making xor transformed zero.

------------------------------------------------------------------------

## Problem 029 --- CF 1858A --- Buttons

**Problem:** [CF 1858A --- Buttons]()\
**Topic / Rating:** Game/Counting / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives a,b,c. The task is to winner with shared buttons.

**What must we output?** winner with shared buttons

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: a,b,c. Mathematical state → shared moves alternate.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: a,b,c. Useful state: shared moves alternate. Unknown: winner with shared buttons.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
parity of c decides who gets extra
```

Now simplify / rearrange / classify it:

``` text
parity of c decides who gets extra
        ↓
compare effective counts
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `parity of c decides who gets extra`.
Then simplify/rearrange it to `compare effective counts`. This is the
point where the story disappears and the solution follows from the
transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
parity of c decides who gets extra
  ↓ simplify
compare effective counts
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into
`parity of c decides who gets extra`, evaluate it step by step, and
verify the transformed condition `compare effective counts`. The final
value/condition gives winner with shared buttons.

------------------------------------------------------------------------

## Problem 030 --- CF 1899A --- Game with Integers

**Problem:** [CF 1899A --- Game with Integers]()\
**Topic / Rating:** Modulo / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives n. The task is to winner under ±1 and divisibility
by3.

**What must we output?** winner under ±1 and divisibility by3

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: n. Mathematical state → positions mod3.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: n. Useful state: positions mod3. Unknown: winner under ±1 and divisibility by3.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
n%3==0 is losing/winning condition per rules
```

Now simplify / rearrange / classify it:

``` text
n%3==0 is losing/winning condition per rules
        ↓
reduce to residue
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into
`n%3==0 is losing/winning condition per rules`. Then simplify/rearrange
it to `reduce to residue`. This is the point where the story disappears
and the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
n%3==0 is losing/winning condition per rules
  ↓ simplify
reduce to residue
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into
`n%3==0 is losing/winning condition per rules`, evaluate it step by
step, and verify the transformed condition `reduce to residue`. The
final value/condition gives winner under ±1 and divisibility by3.

------------------------------------------------------------------------

# Pattern 4 --- Parity Modeling

## Problem 031 --- CF 4A --- Watermelon

**Problem:** [CF 4A --- Watermelon]()\
**Topic / Rating:** Parity / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives w. The task is to split into two positive even
parts.

**What must we output?** split into two positive even parts

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: w. Mathematical state → w=a+b, a,b even >=2.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: w. Useful state: w=a+b, a,b even >=2. Unknown: split into two positive even parts.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
w even and w>2
```

Now simplify / rearrange / classify it:

``` text
w even and w>2
        ↓
parity + positivity
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `w even and w>2`. Then
simplify/rearrange it to `parity + positivity`. This is the point where
the story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
w even and w>2
  ↓ simplify
parity + positivity
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `w even and w>2`, evaluate it
step by step, and verify the transformed condition
`parity + positivity`. The final value/condition gives split into two
positive even parts.

------------------------------------------------------------------------

## Problem 032 --- CF 1296A --- Array with Odd Sum

**Problem:** [CF 1296A --- Array with Odd Sum]()\
**Topic / Rating:** Parity / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives array. The task is to whether required odd-sum
selection exists.

**What must we output?** whether required odd-sum selection exists

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: array. Mathematical state → sum odd iff odd count odd.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: array. Useful state: sum odd iff odd count odd. Unknown: whether required odd-sum selection exists.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
reduce values to parity
```

Now simplify / rearrange / classify it:

``` text
reduce values to parity
        ↓
count odd/even
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `reduce values to parity`. Then
simplify/rearrange it to `count odd/even`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
reduce values to parity
  ↓ simplify
count odd/even
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `reduce values to parity`,
evaluate it step by step, and verify the transformed condition
`count odd/even`. The final value/condition gives whether required
odd-sum selection exists.

------------------------------------------------------------------------

## Problem 033 --- CF 1857A --- Array Coloring

**Problem:** [CF 1857A --- Array Coloring]()\
**Topic / Rating:** Parity / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives array. The task is to whether can split into two
groups with equal parity sums.

**What must we output?** whether can split into two groups with equal
parity sums

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: array. Mathematical state → total sum must be even.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: array. Useful state: total sum must be even. Unknown: whether can split into two groups with equal parity sums.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
sum%2=0
```

Now simplify / rearrange / classify it:

``` text
sum%2=0
        ↓
parity invariant
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `sum%2=0`. Then simplify/rearrange it
to `parity invariant`. This is the point where the story disappears and
the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
sum%2=0
  ↓ simplify
parity invariant
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `sum%2=0`, evaluate it step by
step, and verify the transformed condition `parity invariant`. The final
value/condition gives whether can split into two groups with equal
parity sums.

------------------------------------------------------------------------

## Problem 034 --- CF 1834A --- Unit Array

**Problem:** [CF 1834A --- Unit Array]()\
**Topic / Rating:** Parity/Greedy / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives ±1 array. The task is to minimum flips to satisfy
sum\>=0 and product=1.

**What must we output?** minimum flips to satisfy sum\>=0 and product=1

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: ±1 array. Mathematical state → product depends on #(-1) parity.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: ±1 array. Useful state: product depends on #(-1) parity. Unknown: minimum flips to satisfy sum>=0 and product=1.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
fix sum then parity
```

Now simplify / rearrange / classify it:

``` text
fix sum then parity
        ↓
count negatives
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `fix sum then parity`. Then
simplify/rearrange it to `count negatives`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
fix sum then parity
  ↓ simplify
count negatives
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `fix sum then parity`,
evaluate it step by step, and verify the transformed condition
`count negatives`. The final value/condition gives minimum flips to
satisfy sum\>=0 and product=1.

------------------------------------------------------------------------

## Problem 035 --- CF 1367B --- Even Array

**Problem:** [CF 1367B --- Even Array]()\
**Topic / Rating:** Parity / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives array. The task is to minimum swaps so a\[i\]%2=i%2.

**What must we output?** minimum swaps so a\[i\]%2=i%2

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: array. Mathematical state → mismatches of two types must balance.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: array. Useful state: mismatches of two types must balance. Unknown: minimum swaps so a[i]%2=i%2.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
badEven=badOdd
```

Now simplify / rearrange / classify it:

``` text
badEven=badOdd
        ↓
answer mismatches/2
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `badEven=badOdd`. Then
simplify/rearrange it to `answer mismatches/2`. This is the point where
the story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
badEven=badOdd
  ↓ simplify
answer mismatches/2
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `badEven=badOdd`, evaluate it
step by step, and verify the transformed condition
`answer mismatches/2`. The final value/condition gives minimum swaps so
a\[i\]%2=i%2.

------------------------------------------------------------------------

## Problem 036 --- CF 1475A --- Odd Divisor

**Problem:** [CF 1475A --- Odd Divisor]()\
**Topic / Rating:** Number Theory / 900

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives n. The task is to has odd divisor \>1.

**What must we output?** has odd divisor \>1

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: n. Mathematical state → n=2^k*m odd.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: n. Useful state: n=2^k*m odd. Unknown: has odd divisor >1.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
m>1
```

Now simplify / rearrange / classify it:

``` text
m>1
        ↓
not power of two
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `m>1`. Then simplify/rearrange it to
`not power of two`. This is the point where the story disappears and the
solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
m>1
  ↓ simplify
not power of two
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `m>1`, evaluate it step by
step, and verify the transformed condition `not power of two`. The final
value/condition gives has odd divisor \>1.

------------------------------------------------------------------------

## Problem 037 --- CF 1669C --- Odd/Even Increments

**Problem:** [CF 1669C --- Odd/Even Increments]()\
**Topic / Rating:** Parity / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives array. The task is to can equalize via
parity-constrained increments.

**What must we output?** can equalize via parity-constrained increments

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: array. Mathematical state → all elements need same parity class relation.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: array. Useful state: all elements need same parity class relation. Unknown: can equalize via parity-constrained increments.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
check parity consistency
```

Now simplify / rearrange / classify it:

``` text
check parity consistency
        ↓
parity only
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `check parity consistency`. Then
simplify/rearrange it to `parity only`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
check parity consistency
  ↓ simplify
parity only
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `check parity consistency`,
evaluate it step by step, and verify the transformed condition
`parity only`. The final value/condition gives can equalize via
parity-constrained increments.

------------------------------------------------------------------------

## Problem 038 --- CF 1624A --- Plus One on the Subset

**Problem:** [CF 1624A --- Plus One on the Subset]()\
**Topic / Rating:** Difference / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives array. The task is to minimum operations to equalize
by incrementing subset.

**What must we output?** minimum operations to equalize by incrementing
subset

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: array. Mathematical state → raise to max.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: array. Useful state: raise to max. Unknown: minimum operations to equalize by incrementing subset.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
answer=max-min
```

Now simplify / rearrange / classify it:

``` text
answer=max-min
        ↓
range width
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `answer=max-min`. Then
simplify/rearrange it to `range width`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
answer=max-min
  ↓ simplify
range width
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `answer=max-min`, evaluate it
step by step, and verify the transformed condition `range width`. The
final value/condition gives minimum operations to equalize by
incrementing subset.

------------------------------------------------------------------------

## Problem 039 --- CF 1788A --- One and Two

**Problem:** [CF 1788A --- One and Two]()\
**Topic / Rating:** Product/Parity / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives 1/2 array. The task is to split so products equal.

**What must we output?** split so products equal

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: 1/2 array. Mathematical state → equal #twos on both sides.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: 1/2 array. Useful state: equal #twos on both sides. Unknown: split so products equal.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
total twos even
```

Now simplify / rearrange / classify it:

``` text
total twos even
        ↓
find half twos
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `total twos even`. Then
simplify/rearrange it to `find half twos`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
total twos even
  ↓ simplify
find half twos
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `total twos even`, evaluate it
step by step, and verify the transformed condition `find half twos`. The
final value/condition gives split so products equal.

------------------------------------------------------------------------

## Problem 040 --- CF 1845A --- Forbidden Integer

**Problem:** [CF 1845A --- Forbidden Integer]()\
**Topic / Rating:** Constructive / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives n,k,x. The task is to represent n as sum of 1..k
excluding x.

**What must we output?** represent n as sum of 1..k excluding x

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: n,k,x. Mathematical state → choose repeated small allowed values.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: n,k,x. Useful state: choose repeated small allowed values. Unknown: represent n as sum of 1..k excluding x.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
cases x!=1, else 2/3
```

Now simplify / rearrange / classify it:

``` text
cases x!=1, else 2/3
        ↓
construct feasibility
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `cases x!=1, else 2/3`. Then
simplify/rearrange it to `construct feasibility`. This is the point
where the story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
cases x!=1, else 2/3
  ↓ simplify
construct feasibility
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `cases x!=1, else 2/3`,
evaluate it step by step, and verify the transformed condition
`construct feasibility`. The final value/condition gives represent n as
sum of 1..k excluding x.

------------------------------------------------------------------------

# Pattern 5 --- Divisibility / GCD / LCM

## Problem 041 --- CF 1328A --- Divisibility Problem

**Problem:** [CF 1328A --- Divisibility Problem]()\
**Topic / Rating:** Modulo / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives a,b. The task is to minimum add to make a divisible
by b.

**What must we output?** minimum add to make a divisible by b

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: a,b. Mathematical state → need a+x ≡0 mod b.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: a,b. Useful state: need a+x ≡0 mod b. Unknown: minimum add to make a divisible by b.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
x=(b-a%b)%b
```

Now simplify / rearrange / classify it:

``` text
x=(b-a%b)%b
        ↓
remainder complement
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `x=(b-a%b)%b`. Then simplify/rearrange
it to `remainder complement`. This is the point where the story
disappears and the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
x=(b-a%b)%b
  ↓ simplify
remainder complement
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `x=(b-a%b)%b`, evaluate it
step by step, and verify the transformed condition
`remainder complement`. The final value/condition gives minimum add to
make a divisible by b.

------------------------------------------------------------------------

## Problem 042 --- CF 1343A --- Candies

**Problem:** [CF 1343A --- Candies]()\
**Topic / Rating:** Geometric/Divisibility / 900

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives n. The task is to find x where n=x(2\^k-1).

**What must we output?** find x where n=x(2\^k-1)

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: n. Mathematical state → geometric sum factor.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: n. Useful state: geometric sum factor. Unknown: find x where n=x(2^k-1).
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
x=n/(2^k-1) if divisible
```

Now simplify / rearrange / classify it:

``` text
x=n/(2^k-1) if divisible
        ↓
test k
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `x=n/(2^k-1) if divisible`. Then
simplify/rearrange it to `test k`. This is the point where the story
disappears and the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
x=n/(2^k-1) if divisible
  ↓ simplify
test k
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `x=n/(2^k-1) if divisible`,
evaluate it step by step, and verify the transformed condition `test k`.
The final value/condition gives find x where n=x(2\^k-1).

------------------------------------------------------------------------

## Problem 043 --- CF 1370A --- Maximum GCD

**Problem:** [CF 1370A --- Maximum GCD]()\
**Topic / Rating:** GCD / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives n. The task is to maximize gcd(a,b), a+b=n.

**What must we output?** maximize gcd(a,b), a+b=n

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: n. Mathematical state → gcd<=floor(n/2).
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: n. Useful state: gcd<=floor(n/2). Unknown: maximize gcd(a,b), a+b=n.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
choose floor(n/2)
```

Now simplify / rearrange / classify it:

``` text
choose floor(n/2)
        ↓
tight bound
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `choose floor(n/2)`. Then
simplify/rearrange it to `tight bound`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
choose floor(n/2)
  ↓ simplify
tight bound
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `choose floor(n/2)`, evaluate
it step by step, and verify the transformed condition `tight bound`. The
final value/condition gives maximize gcd(a,b), a+b=n.

------------------------------------------------------------------------

## Problem 044 --- CF 1829C --- Mr. Perfectly Fine

**Problem:** [CF 1829C --- Mr. Perfectly Fine]()\
**Topic / Rating:** Min/Bitmask / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives items. The task is to minimum time covering skills 1
and2.

**What must we output?** minimum time covering skills 1 and2

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: items. Mathematical state → skill masks 01,10,11.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: items. Useful state: skill masks 01,10,11. Unknown: minimum time covering skills 1 and2.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
min(cost11,cost01+cost10)
```

Now simplify / rearrange / classify it:

``` text
min(cost11,cost01+cost10)
        ↓
coverage states
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `min(cost11,cost01+cost10)`. Then
simplify/rearrange it to `coverage states`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
min(cost11,cost01+cost10)
  ↓ simplify
coverage states
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `min(cost11,cost01+cost10)`,
evaluate it step by step, and verify the transformed condition
`coverage states`. The final value/condition gives minimum time covering
skills 1 and2.

------------------------------------------------------------------------

## Problem 045 --- CF 1618A --- Polycarp and Sums of Subsequences

**Problem:** [CF 1618A --- Polycarp and Sums of Subsequences]()\
**Topic / Rating:** Algebra / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives 7 subset sums. The task is to recover a,b,c.

**What must we output?** recover a,b,c

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: 7 subset sums. Mathematical state → smallest=a,b and total largest=a+b+c.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: 7 subset sums. Useful state: smallest=a,b and total largest=a+b+c. Unknown: recover a,b,c.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
c=largest-a-b
```

Now simplify / rearrange / classify it:

``` text
c=largest-a-b
        ↓
sorted sums
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `c=largest-a-b`. Then
simplify/rearrange it to `sorted sums`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
c=largest-a-b
  ↓ simplify
sorted sums
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `c=largest-a-b`, evaluate it
step by step, and verify the transformed condition `sorted sums`. The
final value/condition gives recover a,b,c.

------------------------------------------------------------------------

## Problem 046 --- CF 160A --- Twins

**Problem:** [CF 160A --- Twins]()\
**Topic / Rating:** Greedy/Sum / 900

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives coins. The task is to minimum coins with sum \>
remaining.

**What must we output?** minimum coins with sum \> remaining

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: coins. Mathematical state → chosen > total-chosen.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: coins. Useful state: chosen > total-chosen. Unknown: minimum coins with sum > remaining.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
2*chosen>total
```

Now simplify / rearrange / classify it:

``` text
2*chosen>total
        ↓
sort descending
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `2*chosen>total`. Then
simplify/rearrange it to `sort descending`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
2*chosen>total
  ↓ simplify
sort descending
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `2*chosen>total`, evaluate it
step by step, and verify the transformed condition `sort descending`.
The final value/condition gives minimum coins with sum \> remaining.

------------------------------------------------------------------------

## Problem 047 --- CF 1475B --- New Year's Number

**Problem:** [CF 1475B --- New Year's Number]()\
**Topic / Rating:** Diophantine / 900

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives n. The task is to n=2020a+2021b?.

**What must we output?** n=2020a+2021b?

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: n. Mathematical state → 2021=2020+1.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: n. Useful state: 2021=2020+1. Unknown: n=2020a+2021b?.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
choose b=n%2020 then test
```

Now simplify / rearrange / classify it:

``` text
choose b=n%2020 then test
        ↓
linear diophantine shortcut
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `choose b=n%2020 then test`. Then
simplify/rearrange it to `linear diophantine shortcut`. This is the
point where the story disappears and the solution follows from the
transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
choose b=n%2020 then test
  ↓ simplify
linear diophantine shortcut
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `choose b=n%2020 then test`,
evaluate it step by step, and verify the transformed condition
`linear diophantine shortcut`. The final value/condition gives
n=2020a+2021b?.

------------------------------------------------------------------------

## Problem 048 --- CF 1593A --- Elections

**Problem:** [CF 1593A --- Elections]()\
**Topic / Rating:** Max/Formula / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives a,b,c. The task is to increments to become strictly
largest.

**What must we output?** increments to become strictly largest

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: a,b,c. Mathematical state → need x+inc>max(other).
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: a,b,c. Useful state: need x+inc>max(other). Unknown: increments to become strictly largest.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
inc=max(0,M-x+1), except unique max
```

Now simplify / rearrange / classify it:

``` text
inc=max(0,M-x+1), except unique max
        ↓
per candidate bound
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `inc=max(0,M-x+1), except unique max`.
Then simplify/rearrange it to `per candidate bound`. This is the point
where the story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
inc=max(0,M-x+1), except unique max
  ↓ simplify
per candidate bound
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into
`inc=max(0,M-x+1), except unique max`, evaluate it step by step, and
verify the transformed condition `per candidate bound`. The final
value/condition gives increments to become strictly largest.

------------------------------------------------------------------------

## Problem 049 --- CF 1829B --- Blank Space

**Problem:** [CF 1829B --- Blank Space]()\
**Topic / Rating:** Run Length / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives binary array. The task is to longest consecutive
zeros.

**What must we output?** longest consecutive zeros

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: binary array. Mathematical state → state current run.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: binary array. Useful state: state current run. Unknown: longest consecutive zeros.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
max over runs
```

Now simplify / rearrange / classify it:

``` text
max over runs
        ↓
scan
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `max over runs`. Then
simplify/rearrange it to `scan`. This is the point where the story
disappears and the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
max over runs
  ↓ simplify
scan
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `max over runs`, evaluate it
step by step, and verify the transformed condition `scan`. The final
value/condition gives longest consecutive zeros.

------------------------------------------------------------------------

## Problem 050 --- CF 1877A --- Goals of Victory

**Problem:** [CF 1877A --- Goals of Victory]()\
**Topic / Rating:** Sum Invariant / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives n-1 values. The task is to missing value so total
sum zero.

**What must we output?** missing value so total sum zero

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: n-1 values. Mathematical state → x+sum=0.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: n-1 values. Useful state: x+sum=0. Unknown: missing value so total sum zero.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
x=-sum
```

Now simplify / rearrange / classify it:

``` text
x=-sum
        ↓
equation
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `x=-sum`. Then simplify/rearrange it to
`equation`. This is the point where the story disappears and the
solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
x=-sum
  ↓ simplify
equation
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `x=-sum`, evaluate it step by
step, and verify the transformed condition `equation`. The final
value/condition gives missing value so total sum zero.

------------------------------------------------------------------------

# Pattern 6 --- Modulo / Cyclic Modeling

## Problem 051 --- CF 116A --- Tram

**Problem:** [CF 116A --- Tram]()\
**Topic / Rating:** Prefix/Capacity / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives stops. The task is to minimum tram capacity.

**What must we output?** minimum tram capacity

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: stops. Mathematical state → current += enter-exit.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: stops. Useful state: current += enter-exit. Unknown: minimum tram capacity.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
max prefix occupancy
```

Now simplify / rearrange / classify it:

``` text
max prefix occupancy
        ↓
running state
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `max prefix occupancy`. Then
simplify/rearrange it to `running state`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
max prefix occupancy
  ↓ simplify
running state
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `max prefix occupancy`,
evaluate it step by step, and verify the transformed condition
`running state`. The final value/condition gives minimum tram capacity.

------------------------------------------------------------------------

## Problem 052 --- CF 266A --- Stones on the Table

**Problem:** [CF 266A --- Stones on the Table]()\
**Topic / Rating:** Adjacent / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives string. The task is to minimum removals so adjacent
colors differ.

**What must we output?** minimum removals so adjacent colors differ

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: string. Mathematical state → remove one from each equal adjacency.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: string. Useful state: remove one from each equal adjacency. Unknown: minimum removals so adjacent colors differ.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
count s[i]==s[i-1]
```

Now simplify / rearrange / classify it:

``` text
count s[i]==s[i-1]
        ↓
local contribution
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `count s[i]==s[i-1]`. Then
simplify/rearrange it to `local contribution`. This is the point where
the story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
count s[i]==s[i-1]
  ↓ simplify
local contribution
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `count s[i]==s[i-1]`, evaluate
it step by step, and verify the transformed condition
`local contribution`. The final value/condition gives minimum removals
so adjacent colors differ.

------------------------------------------------------------------------

## Problem 053 --- CF 228A --- Is your horseshoe on the other hoof?

**Problem:** [CF 228A --- Is your horseshoe on the other hoof?]()\
**Topic / Rating:** Distinctness / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives 4 colors. The task is to minimum replacements for
distinct.

**What must we output?** minimum replacements for distinct

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: 4 colors. Mathematical state → 4-distinctCount.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: 4 colors. Useful state: 4-distinctCount. Unknown: minimum replacements for distinct.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
set size
```

Now simplify / rearrange / classify it:

``` text
set size
        ↓
duplicates
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `set size`. Then simplify/rearrange it
to `duplicates`. This is the point where the story disappears and the
solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
set size
  ↓ simplify
duplicates
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `set size`, evaluate it step
by step, and verify the transformed condition `duplicates`. The final
value/condition gives minimum replacements for distinct.

------------------------------------------------------------------------

## Problem 054 --- CF 443A --- Anton and Letters

**Problem:** [CF 443A --- Anton and Letters]()\
**Topic / Rating:** Set / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives formatted string. The task is to number distinct
letters.

**What must we output?** number distinct letters

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: formatted string. Mathematical state → extract lowercase chars.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: formatted string. Useful state: extract lowercase chars. Unknown: number distinct letters.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
set cardinality
```

Now simplify / rearrange / classify it:

``` text
set cardinality
        ↓
distinct count
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `set cardinality`. Then
simplify/rearrange it to `distinct count`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
set cardinality
  ↓ simplify
distinct count
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `set cardinality`, evaluate it
step by step, and verify the transformed condition `distinct count`. The
final value/condition gives number distinct letters.

------------------------------------------------------------------------

## Problem 055 --- CF 59A --- Word

**Problem:** [CF 59A --- Word]()\
**Topic / Rating:** Counting / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives string. The task is to convert based on upper/lower
majority.

**What must we output?** convert based on upper/lower majority

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: string. Mathematical state → count uppercase vs lowercase.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: string. Useful state: count uppercase vs lowercase. Unknown: convert based on upper/lower majority.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
choose case
```

Now simplify / rearrange / classify it:

``` text
choose case
        ↓
frequency comparison
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `choose case`. Then simplify/rearrange
it to `frequency comparison`. This is the point where the story
disappears and the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
choose case
  ↓ simplify
frequency comparison
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `choose case`, evaluate it
step by step, and verify the transformed condition
`frequency comparison`. The final value/condition gives convert based on
upper/lower majority.

------------------------------------------------------------------------

## Problem 056 --- CF 236A --- Boy or Girl

**Problem:** [CF 236A --- Boy or Girl]()\
**Topic / Rating:** Set/Parity / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives username. The task is to output based on distinct
char count parity.

**What must we output?** output based on distinct char count parity

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: username. Mathematical state → d=|set(chars)|.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: username. Useful state: d=|set(chars)|. Unknown: output based on distinct char count parity.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
d%2
```

Now simplify / rearrange / classify it:

``` text
d%2
        ↓
parity of distinct count
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `d%2`. Then simplify/rearrange it to
`parity of distinct count`. This is the point where the story disappears
and the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
d%2
  ↓ simplify
parity of distinct count
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `d%2`, evaluate it step by
step, and verify the transformed condition `parity of distinct count`.
The final value/condition gives output based on distinct char count
parity.

------------------------------------------------------------------------

## Problem 057 --- CF 785A --- Anton and Polyhedrons

**Problem:** [CF 785A --- Anton and Polyhedrons]()\
**Topic / Rating:** Mapping / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives names. The task is to total faces.

**What must we output?** total faces

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: names. Mathematical state → name→constant.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: names. Useful state: name→constant. Unknown: total faces.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
sum contributions
```

Now simplify / rearrange / classify it:

``` text
sum contributions
        ↓
lookup
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `sum contributions`. Then
simplify/rearrange it to `lookup`. This is the point where the story
disappears and the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
sum contributions
  ↓ simplify
lookup
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `sum contributions`, evaluate
it step by step, and verify the transformed condition `lookup`. The
final value/condition gives total faces.

------------------------------------------------------------------------

## Problem 058 --- CF 703A --- Mishka and Game

**Problem:** [CF 703A --- Mishka and Game]()\
**Topic / Rating:** Comparison / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives round scores. The task is to winner by more round
wins.

**What must we output?** winner by more round wins

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: round scores. Mathematical state → count a>b and a<b.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: round scores. Useful state: count a>b and a<b. Unknown: winner by more round wins.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
compare counts
```

Now simplify / rearrange / classify it:

``` text
compare counts
        ↓
two counters
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `compare counts`. Then
simplify/rearrange it to `two counters`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
compare counts
  ↓ simplify
two counters
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `compare counts`, evaluate it
step by step, and verify the transformed condition `two counters`. The
final value/condition gives winner by more round wins.

------------------------------------------------------------------------

## Problem 059 --- CF 734B --- Anton and Digits

**Problem:** [CF 734B --- Anton and Digits]()\
**Topic / Rating:** Greedy/Counting / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives counts 2,3,5,6. The task is to maximize sum using
256 and32.

**What must we output?** maximize sum using 256 and32

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: counts 2,3,5,6. Mathematical state → make 256 first because larger.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: counts 2,3,5,6. Useful state: make 256 first because larger. Unknown: maximize sum using 256 and32.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
x=min(2,5,6), y=min(2left,3)
```

Now simplify / rearrange / classify it:

``` text
x=min(2,5,6), y=min(2left,3)
        ↓
resource allocation
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `x=min(2,5,6), y=min(2left,3)`. Then
simplify/rearrange it to `resource allocation`. This is the point where
the story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
x=min(2,5,6), y=min(2left,3)
  ↓ simplify
resource allocation
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into
`x=min(2,5,6), y=min(2left,3)`, evaluate it step by step, and verify the
transformed condition `resource allocation`. The final value/condition
gives maximize sum using 256 and32.

------------------------------------------------------------------------

## Problem 060 --- CF 1097A --- Gennady the Card Game

**Problem:** [CF 1097A --- Gennady the Card Game]()\
**Topic / Rating:** Matching / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives card + five cards. The task is to whether rank or
suit matches.

**What must we output?** whether rank or suit matches

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: card + five cards. Mathematical state → exists same first or second char.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: card + five cards. Useful state: exists same first or second char. Unknown: whether rank or suit matches.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
OR condition
```

Now simplify / rearrange / classify it:

``` text
OR condition
        ↓
scan
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `OR condition`. Then simplify/rearrange
it to `scan`. This is the point where the story disappears and the
solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
OR condition
  ↓ simplify
scan
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `OR condition`, evaluate it
step by step, and verify the transformed condition `scan`. The final
value/condition gives whether rank or suit matches.

------------------------------------------------------------------------

# Pattern 7 --- Counting / Frequency / Pairs

## Problem 061 --- CF 1520D --- Same Differences

**Problem:** [CF 1520D --- Same Differences]()\
**Topic / Rating:** Algebra/Frequency / 1200

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives array. The task is to count i\<j with
a\[j\]-a\[i\]=j-i.

**What must we output?** count i\<j with a\[j\]-a\[i\]=j-i

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: array. Mathematical state → a[j]-j=a[i]-i.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: array. Useful state: a[j]-j=a[i]-i. Unknown: count i<j with a[j]-a[i]=j-i.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
key=a[i]-i
```

Now simplify / rearrange / classify it:

``` text
key=a[i]-i
        ↓
equal-key pairs
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `key=a[i]-i`. Then simplify/rearrange
it to `equal-key pairs`. This is the point where the story disappears
and the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
key=a[i]-i
  ↓ simplify
equal-key pairs
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `key=a[i]-i`, evaluate it step
by step, and verify the transformed condition `equal-key pairs`. The
final value/condition gives count i\<j with a\[j\]-a\[i\]=j-i.

------------------------------------------------------------------------

## Problem 062 --- CF 1538C --- Challenging Cliffs / Number of Pairs

**Problem:** [CF 1538C --- Challenging Cliffs / Number of Pairs]()\
**Topic / Rating:** Two Pointers / 1300

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives array,l,r. The task is to count pairs with sum in
\[l,r\].

**What must we output?** count pairs with sum in \[l,r\]

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: array,l,r. Mathematical state → count<=r - count<l.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: array,l,r. Useful state: count<=r - count<l. Unknown: count pairs with sum in [l,r].
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
sorted pair bound
```

Now simplify / rearrange / classify it:

``` text
sorted pair bound
        ↓
two pointers
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `sorted pair bound`. Then
simplify/rearrange it to `two pointers`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
sorted pair bound
  ↓ simplify
two pointers
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `sorted pair bound`, evaluate
it step by step, and verify the transformed condition `two pointers`.
The final value/condition gives count pairs with sum in \[l,r\].

------------------------------------------------------------------------

## Problem 063 --- CF 1669B --- Triple

**Problem:** [CF 1669B --- Triple]()\
**Topic / Rating:** Frequency / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives array. The task is to find value occurring \>=3.

**What must we output?** find value occurring \>=3

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: array. Mathematical state → freq[x]>=3.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: array. Useful state: freq[x]>=3. Unknown: find value occurring >=3.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
frequency threshold
```

Now simplify / rearrange / classify it:

``` text
frequency threshold
        ↓
count
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `frequency threshold`. Then
simplify/rearrange it to `count`. This is the point where the story
disappears and the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
frequency threshold
  ↓ simplify
count
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `frequency threshold`,
evaluate it step by step, and verify the transformed condition `count`.
The final value/condition gives find value occurring \>=3.

------------------------------------------------------------------------

## Problem 064 --- CF 1742C --- Stripes

**Problem:** [CF 1742C --- Stripes]()\
**Topic / Rating:** Grid/Existence / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives 8x8 grid. The task is to determine last full stripe
color.

**What must we output?** determine last full stripe color

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: 8x8 grid. Mathematical state → full row of R is decisive.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: 8x8 grid. Useful state: full row of R is decisive. Unknown: determine last full stripe color.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
scan rows
```

Now simplify / rearrange / classify it:

``` text
scan rows
        ↓
existence
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `scan rows`. Then simplify/rearrange it
to `existence`. This is the point where the story disappears and the
solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
scan rows
  ↓ simplify
existence
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `scan rows`, evaluate it step
by step, and verify the transformed condition `existence`. The final
value/condition gives determine last full stripe color.

------------------------------------------------------------------------

## Problem 065 --- CF 1791B --- Following Directions

**Problem:** [CF 1791B --- Following Directions]()\
**Topic / Rating:** Coordinates / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives moves. The task is to whether path visits (1,1).

**What must we output?** whether path visits (1,1)

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: moves. Mathematical state → update x,y per char.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: moves. Useful state: update x,y per char. Unknown: whether path visits (1,1).
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
∃prefix=(1,1)
```

Now simplify / rearrange / classify it:

``` text
∃prefix=(1,1)
        ↓
prefix state
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `∃prefix=(1,1)`. Then
simplify/rearrange it to `prefix state`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
∃prefix=(1,1)
  ↓ simplify
prefix state
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `∃prefix=(1,1)`, evaluate it
step by step, and verify the transformed condition `prefix state`. The
final value/condition gives whether path visits (1,1).

------------------------------------------------------------------------

## Problem 066 --- CF 1703B --- ICPC Balloons

**Problem:** [CF 1703B --- ICPC Balloons]()\
**Topic / Rating:** Frequency / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives string. The task is to score first occurrence
differently.

**What must we output?** score first occurrence differently

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: string. Mathematical state → first char contributes2 else1.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: string. Useful state: first char contributes2 else1. Unknown: score first occurrence differently.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
seen set
```

Now simplify / rearrange / classify it:

``` text
seen set
        ↓
contribution
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `seen set`. Then simplify/rearrange it
to `contribution`. This is the point where the story disappears and the
solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
seen set
  ↓ simplify
contribution
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `seen set`, evaluate it step
by step, and verify the transformed condition `contribution`. The final
value/condition gives score first occurrence differently.

------------------------------------------------------------------------

## Problem 067 --- CF 1722A --- Spell Check

**Problem:** [CF 1722A --- Spell Check]()\
**Topic / Rating:** Frequency/Sorting / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives string. The task is to whether permutation equals
TimUR.

**What must we output?** whether permutation equals TimUR

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: string. Mathematical state → same multiset as 'Timur'.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: string. Useful state: same multiset as 'Timur'. Unknown: whether permutation equals TimUR.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
sort or counts
```

Now simplify / rearrange / classify it:

``` text
sort or counts
        ↓
canonical form
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `sort or counts`. Then
simplify/rearrange it to `canonical form`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
sort or counts
  ↓ simplify
canonical form
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `sort or counts`, evaluate it
step by step, and verify the transformed condition `canonical form`. The
final value/condition gives whether permutation equals TimUR.

------------------------------------------------------------------------

## Problem 068 --- CF 1791C --- Prepend and Append

**Problem:** [CF 1791C --- Prepend and Append]()\
**Topic / Rating:** Two Pointers / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives binary string. The task is to remove unequal ends.

**What must we output?** remove unequal ends

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: binary string. Mathematical state → while l<r and s[l]!=s[r].
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: binary string. Useful state: while l<r and s[l]!=s[r]. Unknown: remove unequal ends.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
remaining length
```

Now simplify / rearrange / classify it:

``` text
remaining length
        ↓
two pointers
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `remaining length`. Then
simplify/rearrange it to `two pointers`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
remaining length
  ↓ simplify
two pointers
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `remaining length`, evaluate
it step by step, and verify the transformed condition `two pointers`.
The final value/condition gives remove unequal ends.

------------------------------------------------------------------------

## Problem 069 --- CF 1829D --- Gold Rush

**Problem:** [CF 1829D --- Gold Rush]()\
**Topic / Rating:** Recursion/Reachability / 1000

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives n,m. The task is to can reach m by splitting x into
x/3 and2x/3.

**What must we output?** can reach m by splitting x into x/3 and2x/3

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: n,m. Mathematical state → only split divisible by3.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: n,m. Useful state: only split divisible by3. Unknown: can reach m by splitting x into x/3 and2x/3.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
DFS on decreasing states
```

Now simplify / rearrange / classify it:

``` text
DFS on decreasing states
        ↓
reachability
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `DFS on decreasing states`. Then
simplify/rearrange it to `reachability`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
DFS on decreasing states
  ↓ simplify
reachability
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `DFS on decreasing states`,
evaluate it step by step, and verify the transformed condition
`reachability`. The final value/condition gives can reach m by splitting
x into x/3 and2x/3.

------------------------------------------------------------------------

## Problem 070 --- CF 1878B --- Aleksa and Stack

**Problem:** [CF 1878B --- Aleksa and Stack]()\
**Topic / Rating:** Construction / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives n. The task is to construct sequence satisfying
divisibility condition.

**What must we output?** construct sequence satisfying divisibility
condition

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: n. Mathematical state → choose simple arithmetic sequence.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: n. Useful state: choose simple arithmetic sequence. Unknown: construct sequence satisfying divisibility condition.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
constant gap avoids divisibility
```

Now simplify / rearrange / classify it:

``` text
constant gap avoids divisibility
        ↓
construct
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `constant gap avoids divisibility`.
Then simplify/rearrange it to `construct`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
constant gap avoids divisibility
  ↓ simplify
construct
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into
`constant gap avoids divisibility`, evaluate it step by step, and verify
the transformed condition `construct`. The final value/condition gives
construct sequence satisfying divisibility condition.

------------------------------------------------------------------------

# Pattern 8 --- Operation → Delta → Invariant

## Problem 071 --- CF 1538B --- Friends and Candies

**Problem:** [CF 1538B --- Friends and Candies]()\
**Topic / Rating:** Invariant / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives array. The task is to equalize while preserving sum.

**What must we output?** equalize while preserving sum

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: array. Mathematical state → S=n*x.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: array. Useful state: S=n*x. Unknown: equalize while preserving sum.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
S%n=0
```

Now simplify / rearrange / classify it:

``` text
S%n=0
        ↓
average invariant
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `S%n=0`. Then simplify/rearrange it to
`average invariant`. This is the point where the story disappears and
the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
S%n=0
  ↓ simplify
average invariant
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `S%n=0`, evaluate it step by
step, and verify the transformed condition `average invariant`. The
final value/condition gives equalize while preserving sum.

------------------------------------------------------------------------

## Problem 072 --- CF 1855A --- Dalton the Teacher

**Problem:** [CF 1855A --- Dalton the Teacher]()\
**Topic / Rating:** Mismatch/Operation / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives permutation. The task is to minimum operations
fixing fixed points by pair operation.

**What must we output?** minimum operations fixing fixed points by pair
operation

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: permutation. Mathematical state → each op can fix at most2 fixed points.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: permutation. Useful state: each op can fix at most2 fixed points. Unknown: minimum operations fixing fixed points by pair operation.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
ceil(fixed/2)
```

Now simplify / rearrange / classify it:

``` text
ceil(fixed/2)
        ↓
count fixed
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `ceil(fixed/2)`. Then
simplify/rearrange it to `count fixed`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
ceil(fixed/2)
  ↓ simplify
count fixed
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `ceil(fixed/2)`, evaluate it
step by step, and verify the transformed condition `count fixed`. The
final value/condition gives minimum operations fixing fixed points by
pair operation.

------------------------------------------------------------------------

## Problem 073 --- CF 1838A --- Blackboard List

**Problem:** [CF 1838A --- Blackboard List]()\
**Topic / Rating:** Extremal / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives array. The task is to recover original special
number.

**What must we output?** recover original special number

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: array. Mathematical state → negative minimum survives construction; else maximum.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: array. Useful state: negative minimum survives construction; else maximum. Unknown: recover original special number.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
extremal invariant
```

Now simplify / rearrange / classify it:

``` text
extremal invariant
        ↓
min if negative else max
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `extremal invariant`. Then
simplify/rearrange it to `min if negative else max`. This is the point
where the story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
extremal invariant
  ↓ simplify
min if negative else max
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `extremal invariant`, evaluate
it step by step, and verify the transformed condition
`min if negative else max`. The final value/condition gives recover
original special number.

------------------------------------------------------------------------

## Problem 074 --- CF 1862B --- Sequence Game

**Problem:** [CF 1862B --- Sequence Game]()\
**Topic / Rating:** Construction / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives sequence b. The task is to construct a so filtering
rule returns b.

**What must we output?** construct a so filtering rule returns b

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: sequence b. Mathematical state → insert bridge when b[i-1]>b[i].
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: sequence b. Useful state: insert bridge when b[i-1]>b[i]. Unknown: construct a so filtering rule returns b.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
local condition
```

Now simplify / rearrange / classify it:

``` text
local condition
        ↓
construct with extra value
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `local condition`. Then
simplify/rearrange it to `construct with extra value`. This is the point
where the story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
local condition
  ↓ simplify
construct with extra value
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `local condition`, evaluate it
step by step, and verify the transformed condition
`construct with extra value`. The final value/condition gives construct
a so filtering rule returns b.

------------------------------------------------------------------------

## Problem 075 --- CF 1798A --- Showstopper

**Problem:** [CF 1798A --- Showstopper]()\
**Topic / Rating:** Invariant/Swap / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives two arrays. The task is to can swap pairs so last
elements are maxima.

**What must we output?** can swap pairs so last elements are maxima

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: two arrays. Mathematical state → each pair independently orientable.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: two arrays. Useful state: each pair independently orientable. Unknown: can swap pairs so last elements are maxima.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
need max pair endpoints fit final
```

Now simplify / rearrange / classify it:

``` text
need max pair endpoints fit final
        ↓
normalize max/min
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `need max pair endpoints fit final`.
Then simplify/rearrange it to `normalize max/min`. This is the point
where the story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
need max pair endpoints fit final
  ↓ simplify
normalize max/min
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into
`need max pair endpoints fit final`, evaluate it step by step, and
verify the transformed condition `normalize max/min`. The final
value/condition gives can swap pairs so last elements are maxima.

------------------------------------------------------------------------

## Problem 076 --- CF 660A --- Co-prime Array

**Problem:** [CF 660A --- Co-prime Array]()\
**Topic / Rating:** Construction/GCD / 900

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives array. The task is to insert minimum numbers so
adjacent gcd=1.

**What must we output?** insert minimum numbers so adjacent gcd=1

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: array. Mathematical state → if gcd(a[i],a[i+1])>1 insert coprime sentinel.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: array. Useful state: if gcd(a[i],a[i+1])>1 insert coprime sentinel. Unknown: insert minimum numbers so adjacent gcd=1.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
local repair
```

Now simplify / rearrange / classify it:

``` text
local repair
        ↓
insert 1
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `local repair`. Then simplify/rearrange
it to `insert 1`. This is the point where the story disappears and the
solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
local repair
  ↓ simplify
insert 1
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `local repair`, evaluate it
step by step, and verify the transformed condition `insert 1`. The final
value/condition gives insert minimum numbers so adjacent gcd=1.

------------------------------------------------------------------------

## Problem 077 --- CF 1367A --- Short Substrings

**Problem:** [CF 1367A --- Short Substrings]()\
**Topic / Rating:** String Reconstruction / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives string b. The task is to recover original.

**What must we output?** recover original

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: string b. Mathematical state → overlap pairs share char.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: string b. Useful state: overlap pairs share char. Unknown: recover original.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
take first then every second char
```

Now simplify / rearrange / classify it:

``` text
take first then every second char
        ↓
inverse operation
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `take first then every second char`.
Then simplify/rearrange it to `inverse operation`. This is the point
where the story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
take first then every second char
  ↓ simplify
inverse operation
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into
`take first then every second char`, evaluate it step by step, and
verify the transformed condition `inverse operation`. The final
value/condition gives recover original.

------------------------------------------------------------------------

## Problem 078 --- CF 1374A --- Required Remainder

**Problem:** [CF 1374A --- Required Remainder]()\
**Topic / Rating:** Modulo/Optimization / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives x,y,n. The task is to largest k\<=n with k%x=y.

**What must we output?** largest k\<=n with k%x=y

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: x,y,n. Mathematical state → numbers are tx+y.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: x,y,n. Useful state: numbers are tx+y. Unknown: largest k<=n with k%x=y.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
t=floor((n-y)/x)
```

Now simplify / rearrange / classify it:

``` text
t=floor((n-y)/x)
        ↓
largest feasible
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `t=floor((n-y)/x)`. Then
simplify/rearrange it to `largest feasible`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
t=floor((n-y)/x)
  ↓ simplify
largest feasible
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `t=floor((n-y)/x)`, evaluate
it step by step, and verify the transformed condition
`largest feasible`. The final value/condition gives largest k\<=n with
k%x=y.

------------------------------------------------------------------------

## Problem 079 --- CF 1551A --- Polycarp and Coins

**Problem:** [CF 1551A --- Polycarp and Coins]()\
**Topic / Rating:** Balancing / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives n. The task is to split n into 1-coin and2-coin
counts minimizing difference.

**What must we output?** split n into 1-coin and2-coin counts minimizing
difference

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: n. Mathematical state → c1+2c2=n, |c1-c2| min.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: n. Useful state: c1+2c2=n, |c1-c2| min. Unknown: split n into 1-coin and2-coin counts minimizing difference.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
near n/3
```

Now simplify / rearrange / classify it:

``` text
near n/3
        ↓
balanced equation
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `near n/3`. Then simplify/rearrange it
to `balanced equation`. This is the point where the story disappears and
the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
near n/3
  ↓ simplify
balanced equation
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `near n/3`, evaluate it step
by step, and verify the transformed condition `balanced equation`. The
final value/condition gives split n into 1-coin and2-coin counts
minimizing difference.

------------------------------------------------------------------------

## Problem 080 --- CF 1818A --- Politics

**Problem:** [CF 1818A --- Politics]()\
**Topic / Rating:** String/Counting / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives strings. The task is to count strings compatible
with reference.

**What must we output?** count strings compatible with reference

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: strings. Mathematical state → positions with reference 1 impose equality.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: strings. Useful state: positions with reference 1 impose equality. Unknown: count strings compatible with reference.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
predicate per string
```

Now simplify / rearrange / classify it:

``` text
predicate per string
        ↓
count valid
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `predicate per string`. Then
simplify/rearrange it to `count valid`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
predicate per string
  ↓ simplify
count valid
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `predicate per string`,
evaluate it step by step, and verify the transformed condition
`count valid`. The final value/condition gives count strings compatible
with reference.

------------------------------------------------------------------------

# Pattern 9 --- Sorting / Coordinate / Distance Modeling

## Problem 081 --- CF 160A --- Twins

**Problem:** [CF 160A --- Twins]()\
**Topic / Rating:** Sorting/Greedy / 900

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives coins. The task is to minimum selected sum \> rest.

**What must we output?** minimum selected sum \> rest

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: coins. Mathematical state → sort descending.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: coins. Useful state: sort descending. Unknown: minimum selected sum > rest.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
prefix until 2sum>total
```

Now simplify / rearrange / classify it:

``` text
prefix until 2sum>total
        ↓
extremal choice
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `prefix until 2sum>total`. Then
simplify/rearrange it to `extremal choice`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
prefix until 2sum>total
  ↓ simplify
extremal choice
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `prefix until 2sum>total`,
evaluate it step by step, and verify the transformed condition
`extremal choice`. The final value/condition gives minimum selected sum
\> rest.

------------------------------------------------------------------------

## Problem 082 --- CF 1399A --- Remove Smallest

**Problem:** [CF 1399A --- Remove Smallest]()\
**Topic / Rating:** Sorting / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives array. The task is to can delete until one remains
under diff\<=1.

**What must we output?** can delete until one remains under diff\<=1

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: array. Mathematical state → sort; all adjacent gaps<=1.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: array. Useful state: sort; all adjacent gaps<=1. Unknown: can delete until one remains under diff<=1.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
adjacent condition
```

Now simplify / rearrange / classify it:

``` text
adjacent condition
        ↓
check
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `adjacent condition`. Then
simplify/rearrange it to `check`. This is the point where the story
disappears and the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
adjacent condition
  ↓ simplify
check
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `adjacent condition`, evaluate
it step by step, and verify the transformed condition `check`. The final
value/condition gives can delete until one remains under diff\<=1.

------------------------------------------------------------------------

## Problem 083 --- CF 1760A --- Medium Number

**Problem:** [CF 1760A --- Medium Number]()\
**Topic / Rating:** Sorting / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives a,b,c. The task is to middle value.

**What must we output?** middle value

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: a,b,c. Mathematical state → sort three.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: a,b,c. Useful state: sort three. Unknown: middle value.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
second element
```

Now simplify / rearrange / classify it:

``` text
second element
        ↓
median
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `second element`. Then
simplify/rearrange it to `median`. This is the point where the story
disappears and the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
second element
  ↓ simplify
median
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `second element`, evaluate it
step by step, and verify the transformed condition `median`. The final
value/condition gives middle value.

------------------------------------------------------------------------

## Problem 084 --- CF 1538A --- Stone Game

**Problem:** [CF 1538A --- Stone Game]()\
**Topic / Rating:** Positions / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives permutation. The task is to min removals from ends
to remove min and max.

**What must we output?** min removals from ends to remove min and max

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: permutation. Mathematical state → positions pmin,pmax.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: permutation. Useful state: positions pmin,pmax. Unknown: min removals from ends to remove min and max.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
min of three strategies
```

Now simplify / rearrange / classify it:

``` text
min of three strategies
        ↓
distance to ends
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `min of three strategies`. Then
simplify/rearrange it to `distance to ends`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
min of three strategies
  ↓ simplify
distance to ends
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `min of three strategies`,
evaluate it step by step, and verify the transformed condition
`distance to ends`. The final value/condition gives min removals from
ends to remove min and max.

------------------------------------------------------------------------

## Problem 085 --- CF 1729A --- Two Elevators

**Problem:** [CF 1729A --- Two Elevators]()\
**Topic / Rating:** Distance / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives a,b,c. The task is to compare travel times.

**What must we output?** compare travel times

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: a,b,c. Mathematical state → t1=a-1, t2=|b-c|+c-1.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: a,b,c. Useful state: t1=a-1, t2=|b-c|+c-1. Unknown: compare travel times.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
absolute distance
```

Now simplify / rearrange / classify it:

``` text
absolute distance
        ↓
compare
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `absolute distance`. Then
simplify/rearrange it to `compare`. This is the point where the story
disappears and the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
absolute distance
  ↓ simplify
compare
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `absolute distance`, evaluate
it step by step, and verify the transformed condition `compare`. The
final value/condition gives compare travel times.

------------------------------------------------------------------------

## Problem 086 --- CF 1593B --- Make it Divisible by 25

**Problem:** [CF 1593B --- Make it Divisible by 25]()\
**Topic / Rating:** Digit Pattern / 900

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives string number. The task is to min deletions for
divisible by25.

**What must we output?** min deletions for divisible by25

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: string number. Mathematical state → last two digits in {00,25,50,75}.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: string number. Useful state: last two digits in {00,25,50,75}. Unknown: min deletions for divisible by25.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
find pair from right
```

Now simplify / rearrange / classify it:

``` text
find pair from right
        ↓
pattern search
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `find pair from right`. Then
simplify/rearrange it to `pattern search`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
find pair from right
  ↓ simplify
pattern search
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `find pair from right`,
evaluate it step by step, and verify the transformed condition
`pattern search`. The final value/condition gives min deletions for
divisible by25.

------------------------------------------------------------------------

## Problem 087 --- CF 1742F --- Smaller

**Problem:** [CF 1742F --- Smaller]()\
**Topic / Rating:** Lexicographic/Invariant / 1200

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives string append queries. The task is to whether s\<t
possible.

**What must we output?** whether s\<t possible

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: string append queries. Mathematical state → presence of char >'a' dominates.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: string append queries. Useful state: presence of char >'a' dominates. Unknown: whether s<t possible.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
track counts/flags
```

Now simplify / rearrange / classify it:

``` text
track counts/flags
        ↓
compressed state
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `track counts/flags`. Then
simplify/rearrange it to `compressed state`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
track counts/flags
  ↓ simplify
compressed state
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `track counts/flags`, evaluate
it step by step, and verify the transformed condition
`compressed state`. The final value/condition gives whether s\<t
possible.

------------------------------------------------------------------------

## Problem 088 --- CF 1831A --- Twin Permutations

**Problem:** [CF 1831A --- Twin Permutations]()\
**Topic / Rating:** Mapping / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives permutation. The task is to construct complementary
permutation.

**What must we output?** construct complementary permutation

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: permutation. Mathematical state → b[i]=n+1-a[i].
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: permutation. Useful state: b[i]=n+1-a[i]. Unknown: construct complementary permutation.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
value reflection
```

Now simplify / rearrange / classify it:

``` text
value reflection
        ↓
direct transform
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `value reflection`. Then
simplify/rearrange it to `direct transform`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
value reflection
  ↓ simplify
direct transform
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `value reflection`, evaluate
it step by step, and verify the transformed condition
`direct transform`. The final value/condition gives construct
complementary permutation.

------------------------------------------------------------------------

## Problem 089 --- CF 1900A --- Cover in Water

**Problem:** [CF 1900A --- Cover in Water]()\
**Topic / Rating:** Run Length / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives string. The task is to minimum operations to fill
dots.

**What must we output?** minimum operations to fill dots

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: string. Mathematical state → run of >=3 triggers shortcut; else count dots.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: string. Useful state: run of >=3 triggers shortcut; else count dots. Unknown: minimum operations to fill dots.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
local pattern
```

Now simplify / rearrange / classify it:

``` text
local pattern
        ↓
case split
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `local pattern`. Then
simplify/rearrange it to `case split`. This is the point where the story
disappears and the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
local pattern
  ↓ simplify
case split
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `local pattern`, evaluate it
step by step, and verify the transformed condition `case split`. The
final value/condition gives minimum operations to fill dots.

------------------------------------------------------------------------

## Problem 090 --- CF 1873B --- Good Kid

**Problem:** [CF 1873B --- Good Kid]()\
**Topic / Rating:** Product/Greedy / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives digits. The task is to increment one element to
maximize product.

**What must we output?** increment one element to maximize product

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: digits. Mathematical state → increment smallest.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: digits. Useful state: increment smallest. Unknown: increment one element to maximize product.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
exchange argument intuition
```

Now simplify / rearrange / classify it:

``` text
exchange argument intuition
        ↓
sort/min index
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `exchange argument intuition`. Then
simplify/rearrange it to `sort/min index`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
exchange argument intuition
  ↓ simplify
sort/min index
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `exchange argument intuition`,
evaluate it step by step, and verify the transformed condition
`sort/min index`. The final value/condition gives increment one element
to maximize product.

------------------------------------------------------------------------

# Pattern 10 --- Prefix / Running-State Modeling

## Problem 091 --- CF 116A --- Tram

**Problem:** [CF 116A --- Tram]()\
**Topic / Rating:** Prefix / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives enter/exit. The task is to minimum capacity.

**What must we output?** minimum capacity

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: enter/exit. Mathematical state → cur += in-out.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: enter/exit. Useful state: cur += in-out. Unknown: minimum capacity.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
max(cur)
```

Now simplify / rearrange / classify it:

``` text
max(cur)
        ↓
prefix occupancy
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `max(cur)`. Then simplify/rearrange it
to `prefix occupancy`. This is the point where the story disappears and
the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
max(cur)
  ↓ simplify
prefix occupancy
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `max(cur)`, evaluate it step
by step, and verify the transformed condition `prefix occupancy`. The
final value/condition gives minimum capacity.

------------------------------------------------------------------------

## Problem 092 --- CF 363B --- Fence

**Problem:** [CF 363B --- Fence]()\
**Topic / Rating:** Sliding Window / 1100

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives array,k. The task is to position of minimum k-length
sum.

**What must we output?** position of minimum k-length sum

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: array,k. Mathematical state → window sum.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: array,k. Useful state: window sum. Unknown: position of minimum k-length sum.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
min over contiguous k
```

Now simplify / rearrange / classify it:

``` text
min over contiguous k
        ↓
prefix/sliding
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `min over contiguous k`. Then
simplify/rearrange it to `prefix/sliding`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
min over contiguous k
  ↓ simplify
prefix/sliding
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `min over contiguous k`,
evaluate it step by step, and verify the transformed condition
`prefix/sliding`. The final value/condition gives position of minimum
k-length sum.

------------------------------------------------------------------------

## Problem 093 --- CF 276C --- Little Girl and Problem on Trees / Little Girl and Maximum Sum

**Problem:** [CF 276C --- Little Girl and Problem on Trees / Little Girl
and Maximum Sum]()\
**Topic / Rating:** Difference/Contribution / 1400

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives array,queries. The task is to maximize total query
sum by permutation.

**What must we output?** maximize total query sum by permutation

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: array,queries. Mathematical state → frequency each index used.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: array,queries. Useful state: frequency each index used. Unknown: maximize total query sum by permutation.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
sort values and frequencies same order
```

Now simplify / rearrange / classify it:

``` text
sort values and frequencies same order
        ↓
rearrangement inequality
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into
`sort values and frequencies same order`. Then simplify/rearrange it to
`rearrangement inequality`. This is the point where the story disappears
and the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
sort values and frequencies same order
  ↓ simplify
rearrangement inequality
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into
`sort values and frequencies same order`, evaluate it step by step, and
verify the transformed condition `rearrangement inequality`. The final
value/condition gives maximize total query sum by permutation.

------------------------------------------------------------------------

## Problem 094 --- CF 433B --- Kuriyama Mirai's Stones

**Problem:** [CF 433B --- Kuriyama Mirai's Stones]()\
**Topic / Rating:** Prefix Sum / 1200

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives array,queries. The task is to range sums
original/sorted.

**What must we output?** range sums original/sorted

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: array,queries. Mathematical state → pref and sortedPref.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: array,queries. Useful state: pref and sortedPref. Unknown: range sums original/sorted.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
range=p[r]-p[l-1]
```

Now simplify / rearrange / classify it:

``` text
range=p[r]-p[l-1]
        ↓
static range query
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `range=p[r]-p[l-1]`. Then
simplify/rearrange it to `static range query`. This is the point where
the story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
range=p[r]-p[l-1]
  ↓ simplify
static range query
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `range=p[r]-p[l-1]`, evaluate
it step by step, and verify the transformed condition
`static range query`. The final value/condition gives range sums
original/sorted.

------------------------------------------------------------------------

## Problem 095 --- CF 313B --- Ilya and Queries

**Problem:** [CF 313B --- Ilya and Queries]()\
**Topic / Rating:** Prefix / 1100

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives string,queries. The task is to count equal adjacent
pairs in range.

**What must we output?** count equal adjacent pairs in range

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: string,queries. Mathematical state → b[i]=[s[i]==s[i-1]].
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: string,queries. Useful state: b[i]=[s[i]==s[i-1]]. Unknown: count equal adjacent pairs in range.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
prefix b
```

Now simplify / rearrange / classify it:

``` text
prefix b
        ↓
range sum
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `prefix b`. Then simplify/rearrange it
to `range sum`. This is the point where the story disappears and the
solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
prefix b
  ↓ simplify
range sum
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `prefix b`, evaluate it step
by step, and verify the transformed condition `range sum`. The final
value/condition gives count equal adjacent pairs in range.

------------------------------------------------------------------------

## Problem 096 --- CF 327A --- Flipping Game

**Problem:** [CF 327A --- Flipping Game]()\
**Topic / Rating:** Transform/Kadane / 1200

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives binary array. The task is to maximize ones after one
flip.

**What must we output?** maximize ones after one flip

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: binary array. Mathematical state → gain: 0→+1,1→-1.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: binary array. Useful state: gain: 0→+1,1→-1. Unknown: maximize ones after one flip.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
max subarray gain
```

Now simplify / rearrange / classify it:

``` text
max subarray gain
        ↓
transform then Kadane
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `max subarray gain`. Then
simplify/rearrange it to `transform then Kadane`. This is the point
where the story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
max subarray gain
  ↓ simplify
transform then Kadane
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `max subarray gain`, evaluate
it step by step, and verify the transformed condition
`transform then Kadane`. The final value/condition gives maximize ones
after one flip.

------------------------------------------------------------------------

## Problem 097 --- CF 580A --- Kefa and First Steps

**Problem:** [CF 580A --- Kefa and First Steps]()\
**Topic / Rating:** Run Length / 900

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives array. The task is to longest nondecreasing
contiguous segment.

**What must we output?** longest nondecreasing contiguous segment

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: array. Mathematical state → current run based on a[i]>=a[i-1].
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: array. Useful state: current run based on a[i]>=a[i-1]. Unknown: longest nondecreasing contiguous segment.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
max run
```

Now simplify / rearrange / classify it:

``` text
max run
        ↓
state
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `max run`. Then simplify/rearrange it
to `state`. This is the point where the story disappears and the
solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
max run
  ↓ simplify
state
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `max run`, evaluate it step by
step, and verify the transformed condition `state`. The final
value/condition gives longest nondecreasing contiguous segment.

------------------------------------------------------------------------

## Problem 098 --- CF 702A --- Maximum Increase

**Problem:** [CF 702A --- Maximum Increase]()\
**Topic / Rating:** Run Length / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives array. The task is to longest strictly increasing
contiguous segment.

**What must we output?** longest strictly increasing contiguous segment

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: array. Mathematical state → current++ if a[i]>a[i-1].
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: array. Useful state: current++ if a[i]>a[i-1]. Unknown: longest strictly increasing contiguous segment.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
max run
```

Now simplify / rearrange / classify it:

``` text
max run
        ↓
state
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `max run`. Then simplify/rearrange it
to `state`. This is the point where the story disappears and the
solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
max run
  ↓ simplify
state
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `max run`, evaluate it step by
step, and verify the transformed condition `state`. The final
value/condition gives longest strictly increasing contiguous segment.

------------------------------------------------------------------------

## Problem 099 --- CF 1829B --- Blank Space

**Problem:** [CF 1829B --- Blank Space]()\
**Topic / Rating:** Run Length / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives binary array. The task is to longest zeros.

**What must we output?** longest zeros

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: binary array. Mathematical state → current zero run.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: binary array. Useful state: current zero run. Unknown: longest zeros.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
max
```

Now simplify / rearrange / classify it:

``` text
max
        ↓
state
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `max`. Then simplify/rearrange it to
`state`. This is the point where the story disappears and the solution
follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
max
  ↓ simplify
state
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `max`, evaluate it step by
step, and verify the transformed condition `state`. The final
value/condition gives longest zeros.

------------------------------------------------------------------------

## Problem 100 --- CF 1669F --- Eating Candies

**Problem:** [CF 1669F --- Eating Candies]()\
**Topic / Rating:** Two Pointers/Prefix / 1100

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives array. The task is to max elements eaten with equal
left/right sums.

**What must we output?** max elements eaten with equal left/right sums

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: array. Mathematical state → grow smaller side sum.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: array. Useful state: grow smaller side sum. Unknown: max elements eaten with equal left/right sums.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
two monotone prefix sums
```

Now simplify / rearrange / classify it:

``` text
two monotone prefix sums
        ↓
two pointers
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `two monotone prefix sums`. Then
simplify/rearrange it to `two pointers`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
two monotone prefix sums
  ↓ simplify
two pointers
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `two monotone prefix sums`,
evaluate it step by step, and verify the transformed condition
`two pointers`. The final value/condition gives max elements eaten with
equal left/right sums.

------------------------------------------------------------------------

# Pattern 11 --- Constructive / Reachability Modeling

## Problem 101 --- CF 1690A --- Print a Pedestal

**Problem:** [CF 1690A --- Print a Pedestal]()\
**Topic / Rating:** Constructive / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives n. The task is to three distinct positive heights
with ordering.

**What must we output?** three distinct positive heights with ordering

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: n. Mathematical state → a+b+c=n, a<b<c.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: n. Useful state: a+b+c=n, a<b<c. Unknown: three distinct positive heights with ordering.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
near thirds
```

Now simplify / rearrange / classify it:

``` text
near thirds
        ↓
construct
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `near thirds`. Then simplify/rearrange
it to `construct`. This is the point where the story disappears and the
solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
near thirds
  ↓ simplify
construct
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `near thirds`, evaluate it
step by step, and verify the transformed condition `construct`. The
final value/condition gives three distinct positive heights with
ordering.

------------------------------------------------------------------------

## Problem 102 --- CF 1845A --- Forbidden Integer

**Problem:** [CF 1845A --- Forbidden Integer]()\
**Topic / Rating:** Constructive / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives n,k,x. The task is to sum allowed integers to n.

**What must we output?** sum allowed integers to n

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: n,k,x. Mathematical state → choose 1 if allowed else 2/3.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: n,k,x. Useful state: choose 1 if allowed else 2/3. Unknown: sum allowed integers to n.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
simple basis values
```

Now simplify / rearrange / classify it:

``` text
simple basis values
        ↓
construct
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `simple basis values`. Then
simplify/rearrange it to `construct`. This is the point where the story
disappears and the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
simple basis values
  ↓ simplify
construct
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `simple basis values`,
evaluate it step by step, and verify the transformed condition
`construct`. The final value/condition gives sum allowed integers to n.

------------------------------------------------------------------------

## Problem 103 --- CF 1878B --- Aleksa and Stack

**Problem:** [CF 1878B --- Aleksa and Stack]()\
**Topic / Rating:** Constructive / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives n. The task is to build valid sequence.

**What must we output?** build valid sequence

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: n. Mathematical state → choose simple constant pattern.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: n. Useful state: choose simple constant pattern. Unknown: build valid sequence.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
satisfy local constraint by design
```

Now simplify / rearrange / classify it:

``` text
satisfy local constraint by design
        ↓
construction
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `satisfy local constraint by design`.
Then simplify/rearrange it to `construction`. This is the point where
the story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
satisfy local constraint by design
  ↓ simplify
construction
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into
`satisfy local constraint by design`, evaluate it step by step, and
verify the transformed condition `construction`. The final
value/condition gives build valid sequence.

------------------------------------------------------------------------

## Problem 104 --- CF 1741A --- Compare T-Shirt Sizes

**Problem:** [CF 1741A --- Compare T-Shirt Sizes]()\
**Topic / Rating:** Ordering / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives size strings. The task is to compare S/M/L with X
count.

**What must we output?** compare S/M/L with X count

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: size strings. Mathematical state → L: more X larger; S reverse.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: size strings. Useful state: L: more X larger; S reverse. Unknown: compare S/M/L with X count.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
map to signed scale
```

Now simplify / rearrange / classify it:

``` text
map to signed scale
        ↓
custom ordering
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `map to signed scale`. Then
simplify/rearrange it to `custom ordering`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
map to signed scale
  ↓ simplify
custom ordering
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `map to signed scale`,
evaluate it step by step, and verify the transformed condition
`custom ordering`. The final value/condition gives compare S/M/L with X
count.

------------------------------------------------------------------------

## Problem 105 --- CF 1805B --- We Need the Zero / The String Has a Target

**Problem:** [CF 1805B --- We Need the Zero / The String Has a
Target]()\
**Topic / Rating:** String/Greedy / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives string. The task is to move smallest char to front
under operation.

**What must we output?** move smallest char to front under operation

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: string. Mathematical state → global min char; choose rightmost occurrence.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: string. Useful state: global min char; choose rightmost occurrence. Unknown: move smallest char to front under operation.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
stable reconstruction
```

Now simplify / rearrange / classify it:

``` text
stable reconstruction
        ↓
greedy
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `stable reconstruction`. Then
simplify/rearrange it to `greedy`. This is the point where the story
disappears and the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
stable reconstruction
  ↓ simplify
greedy
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `stable reconstruction`,
evaluate it step by step, and verify the transformed condition `greedy`.
The final value/condition gives move smallest char to front under
operation.

------------------------------------------------------------------------

## Problem 106 --- CF 1833B --- Restore the Weather

**Problem:** [CF 1833B --- Restore the Weather]()\
**Topic / Rating:** Sorting/Matching / 1000

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives arrays a,b,k. The task is to permute b so
\|a\[i\]-b\[i\]\|\<=k.

**What must we output?** permute b so \|a\[i\]-b\[i\]\|\<=k

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: arrays a,b,k. Mathematical state → sort indices by a and b.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: arrays a,b,k. Useful state: sort indices by a and b. Unknown: permute b so |a[i]-b[i]|<=k.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
monotone matching
```

Now simplify / rearrange / classify it:

``` text
monotone matching
        ↓
pair sorted orders
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `monotone matching`. Then
simplify/rearrange it to `pair sorted orders`. This is the point where
the story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
monotone matching
  ↓ simplify
pair sorted orders
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `monotone matching`, evaluate
it step by step, and verify the transformed condition
`pair sorted orders`. The final value/condition gives permute b so
\|a\[i\]-b\[i\]\|\<=k.

------------------------------------------------------------------------

## Problem 107 --- CF 1793C --- Dora and Search

**Problem:** [CF 1793C --- Dora and Search]()\
**Topic / Rating:** Two Pointers/Extremes / 1200

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives permutation segment. The task is to find segment
whose ends are neither min nor max.

**What must we output?** find segment whose ends are neither min nor max

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: permutation segment. Mathematical state → peel if endpoint is current min/max.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: permutation segment. Useful state: peel if endpoint is current min/max. Unknown: find segment whose ends are neither min nor max.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
maintain lo,hi
```

Now simplify / rearrange / classify it:

``` text
maintain lo,hi
        ↓
two pointers
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `maintain lo,hi`. Then
simplify/rearrange it to `two pointers`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
maintain lo,hi
  ↓ simplify
two pointers
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `maintain lo,hi`, evaluate it
step by step, and verify the transformed condition `two pointers`. The
final value/condition gives find segment whose ends are neither min nor
max.

------------------------------------------------------------------------

## Problem 108 --- CF 1881A --- Don't Try to Count

**Problem:** [CF 1881A --- Don't Try to Count]()\
**Topic / Rating:** String/Doubling / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives x,s. The task is to minimum doublings until s
substring.

**What must we output?** minimum doublings until s substring

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: x,s. Mathematical state → length only needs bounded doublings.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: x,s. Useful state: length only needs bounded doublings. Unknown: minimum doublings until s substring.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
repeat x until long enough + margin
```

Now simplify / rearrange / classify it:

``` text
repeat x until long enough + margin
        ↓
simulation bound
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `repeat x until long enough + margin`.
Then simplify/rearrange it to `simulation bound`. This is the point
where the story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
repeat x until long enough + margin
  ↓ simplify
simulation bound
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into
`repeat x until long enough + margin`, evaluate it step by step, and
verify the transformed condition `simulation bound`. The final
value/condition gives minimum doublings until s substring.

------------------------------------------------------------------------

## Problem 109 --- CF 1858A --- Buttons

**Problem:** [CF 1858A --- Buttons]()\
**Topic / Rating:** Game/Constructive / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives a,b,c. The task is to winner.

**What must we output?** winner

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: a,b,c. Mathematical state → shared c allocated alternately.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: a,b,c. Useful state: shared c allocated alternately. Unknown: winner.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
parity c
```

Now simplify / rearrange / classify it:

``` text
parity c
        ↓
effective counts
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `parity c`. Then simplify/rearrange it
to `effective counts`. This is the point where the story disappears and
the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
parity c
  ↓ simplify
effective counts
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `parity c`, evaluate it step
by step, and verify the transformed condition `effective counts`. The
final value/condition gives winner.

------------------------------------------------------------------------

## Problem 110 --- CF 1899A --- Game with Integers

**Problem:** [CF 1899A --- Game with Integers]()\
**Topic / Rating:** Modulo/Game / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives n. The task is to winner.

**What must we output?** winner

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: n. Mathematical state → moves ±1; multiples of3 structure.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: n. Useful state: moves ±1; multiples of3 structure. Unknown: winner.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
n%3
```

Now simplify / rearrange / classify it:

``` text
n%3
        ↓
residue game
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `n%3`. Then simplify/rearrange it to
`residue game`. This is the point where the story disappears and the
solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
n%3
  ↓ simplify
residue game
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `n%3`, evaluate it step by
step, and verify the transformed condition `residue game`. The final
value/condition gives winner.

------------------------------------------------------------------------

# Pattern 12 --- Bitwise / XOR Modeling

## Problem 111 --- CF 1805A --- We Need the Zero

**Problem:** [CF 1805A --- We Need the Zero]()\
**Topic / Rating:** XOR / 900

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives array. The task is to find x so xor(a\[i\]\^x)=0.

**What must we output?** find x so xor(a\[i\]\^x)=0

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: array. Mathematical state → xorAll ^ (x repeated n times).
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: array. Useful state: xorAll ^ (x repeated n times). Unknown: find x so xor(a[i]^x)=0.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
if n even x cancels; else x=xorAll
```

Now simplify / rearrange / classify it:

``` text
if n even x cancels; else x=xorAll
        ↓
parity of n
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `if n even x cancels; else x=xorAll`.
Then simplify/rearrange it to `parity of n`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
if n even x cancels; else x=xorAll
  ↓ simplify
parity of n
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into
`if n even x cancels; else x=xorAll`, evaluate it step by step, and
verify the transformed condition `parity of n`. The final
value/condition gives find x so xor(a\[i\]\^x)=0.

------------------------------------------------------------------------

## Problem 112 --- CF 1872A --- Two Vessels

**Problem:** [CF 1872A --- Two Vessels]()\
**Topic / Rating:** Arithmetic / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives a,b,c. The task is to min moves balancing transfer
c.

**What must we output?** min moves balancing transfer c

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: a,b,c. Mathematical state → difference shrinks by 2c.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: a,b,c. Useful state: difference shrinks by 2c. Unknown: min moves balancing transfer c.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
m*2c>=|a-b|
```

Now simplify / rearrange / classify it:

``` text
m*2c>=|a-b|
        ↓
ceil division
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `m*2c>=|a-b|`. Then simplify/rearrange
it to `ceil division`. This is the point where the story disappears and
the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
m*2c>=|a-b|
  ↓ simplify
ceil division
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `m*2c>=|a-b|`, evaluate it
step by step, and verify the transformed condition `ceil division`. The
final value/condition gives min moves balancing transfer c.

------------------------------------------------------------------------

## Problem 113 --- CF 1703A --- YES or YES?

**Problem:** [CF 1703A --- YES or YES?]()\
**Topic / Rating:** String / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives word. The task is to case-insensitive equality to
yes.

**What must we output?** case-insensitive equality to yes

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: word. Mathematical state → normalize case.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: word. Useful state: normalize case. Unknown: case-insensitive equality to yes.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
compare
```

Now simplify / rearrange / classify it:

``` text
compare
        ↓
canonicalization
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `compare`. Then simplify/rearrange it
to `canonicalization`. This is the point where the story disappears and
the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
compare
  ↓ simplify
canonicalization
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `compare`, evaluate it step by
step, and verify the transformed condition `canonicalization`. The final
value/condition gives case-insensitive equality to yes.

------------------------------------------------------------------------

## Problem 114 --- CF 1624A --- Plus One on the Subset

**Problem:** [CF 1624A --- Plus One on the Subset]()\
**Topic / Rating:** Range / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives array. The task is to min ops equalize.

**What must we output?** min ops equalize

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: array. Mathematical state → one op can increment chosen subset.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: array. Useful state: one op can increment chosen subset. Unknown: min ops equalize.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
range max-min
```

Now simplify / rearrange / classify it:

``` text
range max-min
        ↓
potential
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `range max-min`. Then
simplify/rearrange it to `potential`. This is the point where the story
disappears and the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
range max-min
  ↓ simplify
potential
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `range max-min`, evaluate it
step by step, and verify the transformed condition `potential`. The
final value/condition gives min ops equalize.

------------------------------------------------------------------------

## Problem 115 --- CF 1220A --- Cards

**Problem:** [CF 1220A --- Cards]()\
**Topic / Rating:** Frequency / 900

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives letters. The task is to recover binary digits from
letters.

**What must we output?** recover binary digits from letters

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: letters. Mathematical state → 'z' uniquely identifies zero, 'n' one after ordering.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: letters. Useful state: 'z' uniquely identifies zero, 'n' one after ordering. Unknown: recover binary digits from letters.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
count z and n
```

Now simplify / rearrange / classify it:

``` text
count z and n
        ↓
frequency signature
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `count z and n`. Then
simplify/rearrange it to `frequency signature`. This is the point where
the story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
count z and n
  ↓ simplify
frequency signature
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `count z and n`, evaluate it
step by step, and verify the transformed condition
`frequency signature`. The final value/condition gives recover binary
digits from letters.

------------------------------------------------------------------------

## Problem 116 --- CF 1362A --- Johnny and Ancient Computer

**Problem:** [CF 1362A --- Johnny and Ancient Computer]()\
**Topic / Rating:** Powers/Ratio / 900

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives a,b. The task is to min ×2/4/8 operations to
transform.

**What must we output?** min ×2/4/8 operations to transform

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: a,b. Mathematical state → ratio must be power of2.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: a,b. Useful state: ratio must be power of2. Unknown: min ×2/4/8 operations to transform.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
exponent difference grouped by3
```

Now simplify / rearrange / classify it:

``` text
exponent difference grouped by3
        ↓
factorization
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `exponent difference grouped by3`. Then
simplify/rearrange it to `factorization`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
exponent difference grouped by3
  ↓ simplify
factorization
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into
`exponent difference grouped by3`, evaluate it step by step, and verify
the transformed condition `factorization`. The final value/condition
gives min ×2/4/8 operations to transform.

------------------------------------------------------------------------

## Problem 117 --- CF 1095A --- Repeating Cipher

**Problem:** [CF 1095A --- Repeating Cipher]()\
**Topic / Rating:** Index Pattern / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives encoded string. The task is to decode chars at
positions with jumps 1,2,3....

**What must we output?** decode chars at positions with jumps 1,2,3...

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: encoded string. Mathematical state → index += step.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: encoded string. Useful state: index += step. Unknown: decode chars at positions with jumps 1,2,3....
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
triangular positions
```

Now simplify / rearrange / classify it:

``` text
triangular positions
        ↓
simulation
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `triangular positions`. Then
simplify/rearrange it to `simulation`. This is the point where the story
disappears and the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
triangular positions
  ↓ simplify
simulation
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `triangular positions`,
evaluate it step by step, and verify the transformed condition
`simulation`. The final value/condition gives decode chars at positions
with jumps 1,2,3....

------------------------------------------------------------------------

## Problem 118 --- CF 1324A --- Yet Another Tetris Problem

**Problem:** [CF 1324A --- Yet Another Tetris Problem]()\
**Topic / Rating:** Parity / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives array. The task is to can equalize by subtracting 2.

**What must we output?** can equalize by subtracting 2

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: array. Mathematical state → differences preserve parity.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: array. Useful state: differences preserve parity. Unknown: can equalize by subtracting 2.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
all same parity
```

Now simplify / rearrange / classify it:

``` text
all same parity
        ↓
parity invariant
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `all same parity`. Then
simplify/rearrange it to `parity invariant`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
all same parity
  ↓ simplify
parity invariant
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `all same parity`, evaluate it
step by step, and verify the transformed condition `parity invariant`.
The final value/condition gives can equalize by subtracting 2.

------------------------------------------------------------------------

## Problem 119 --- CF 1462A --- Favorite Sequence

**Problem:** [CF 1462A --- Favorite Sequence]()\
**Topic / Rating:** Two Pointers / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives array. The task is to reorder alternating
left/right.

**What must we output?** reorder alternating left/right

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: array. Mathematical state → take l,r,l+1,r-1.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: array. Useful state: take l,r,l+1,r-1. Unknown: reorder alternating left/right.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
index pattern
```

Now simplify / rearrange / classify it:

``` text
index pattern
        ↓
two pointers
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `index pattern`. Then
simplify/rearrange it to `two pointers`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
index pattern
  ↓ simplify
two pointers
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `index pattern`, evaluate it
step by step, and verify the transformed condition `two pointers`. The
final value/condition gives reorder alternating left/right.

------------------------------------------------------------------------

## Problem 120 --- CF 1619A --- Polycarp and Sums of Subsequences / Square String?

**Problem:** [CF 1619A --- Polycarp and Sums of Subsequences / Square
String?]()\
**Topic / Rating:** String / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives s. The task is to is s two equal halves.

**What must we output?** is s two equal halves

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: s. Mathematical state → len even and first half=second.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: s. Useful state: len even and first half=second. Unknown: is s two equal halves.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
equation on substrings
```

Now simplify / rearrange / classify it:

``` text
equation on substrings
        ↓
direct
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `equation on substrings`. Then
simplify/rearrange it to `direct`. This is the point where the story
disappears and the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
equation on substrings
  ↓ simplify
direct
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `equation on substrings`,
evaluate it step by step, and verify the transformed condition `direct`.
The final value/condition gives is s two equal halves.

------------------------------------------------------------------------

# Pattern 13 --- Mixed Blind Decoding

## Problem 121 --- CF 1538C --- Challenging Cliffs / Number of Pairs

**Problem:** [CF 1538C --- Challenging Cliffs / Number of Pairs]()\
**Topic / Rating:** Sorting+Counting / 1300

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives array,l,r. The task is to count pair sums in
interval.

**What must we output?** count pair sums in interval

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: array,l,r. Mathematical state → F(r)-F(l-1).
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: array,l,r. Useful state: F(r)-F(l-1). Unknown: count pair sums in interval.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
sort + two pointers
```

Now simplify / rearrange / classify it:

``` text
sort + two pointers
        ↓
count bounded pairs
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `sort + two pointers`. Then
simplify/rearrange it to `count bounded pairs`. This is the point where
the story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
sort + two pointers
  ↓ simplify
count bounded pairs
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `sort + two pointers`,
evaluate it step by step, and verify the transformed condition
`count bounded pairs`. The final value/condition gives count pair sums
in interval.

------------------------------------------------------------------------

## Problem 122 --- CF 1475B --- New Year's Number

**Problem:** [CF 1475B --- New Year's Number]()\
**Topic / Rating:** Diophantine+Modulo / 900

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives n. The task is to 2020a+2021b=n.

**What must we output?** 2020a+2021b=n

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: n. Mathematical state → 2021=2020+1.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: n. Useful state: 2021=2020+1. Unknown: 2020a+2021b=n.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
b=n%2020 candidate
```

Now simplify / rearrange / classify it:

``` text
b=n%2020 candidate
        ↓
feasibility
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `b=n%2020 candidate`. Then
simplify/rearrange it to `feasibility`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
b=n%2020 candidate
  ↓ simplify
feasibility
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `b=n%2020 candidate`, evaluate
it step by step, and verify the transformed condition `feasibility`. The
final value/condition gives 2020a+2021b=n.

------------------------------------------------------------------------

## Problem 123 --- CF 1374A --- Required Remainder

**Problem:** [CF 1374A --- Required Remainder]()\
**Topic / Rating:** Modulo+Optimization / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives x,y,n. The task is to largest k\<=n with k%x=y.

**What must we output?** largest k\<=n with k%x=y

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: x,y,n. Mathematical state → k=tx+y.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: x,y,n. Useful state: k=tx+y. Unknown: largest k<=n with k%x=y.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
maximize t under bound
```

Now simplify / rearrange / classify it:

``` text
maximize t under bound
        ↓
floor
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `maximize t under bound`. Then
simplify/rearrange it to `floor`. This is the point where the story
disappears and the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
maximize t under bound
  ↓ simplify
floor
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `maximize t under bound`,
evaluate it step by step, and verify the transformed condition `floor`.
The final value/condition gives largest k\<=n with k%x=y.

------------------------------------------------------------------------

## Problem 124 --- CF 1551A --- Polycarp and Coins

**Problem:** [CF 1551A --- Polycarp and Coins]()\
**Topic / Rating:** Equation+Balancing / 800

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives n. The task is to c1+2c2=n with counts close.

**What must we output?** c1+2c2=n with counts close

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: n. Mathematical state → near n/3.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: n. Useful state: near n/3. Unknown: c1+2c2=n with counts close.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
n%3 cases
```

Now simplify / rearrange / classify it:

``` text
n%3 cases
        ↓
construct counts
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `n%3 cases`. Then simplify/rearrange it
to `construct counts`. This is the point where the story disappears and
the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
n%3 cases
  ↓ simplify
construct counts
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `n%3 cases`, evaluate it step
by step, and verify the transformed condition `construct counts`. The
final value/condition gives c1+2c2=n with counts close.

------------------------------------------------------------------------

## Problem 125 --- CF 1593B --- Make it Divisible by 25

**Problem:** [CF 1593B --- Make it Divisible by 25]()\
**Topic / Rating:** Divisibility+String / 900

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives digits. The task is to min deletions.

**What must we output?** min deletions

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: digits. Mathematical state → last2 digits pattern.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: digits. Useful state: last2 digits pattern. Unknown: min deletions.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
search from right
```

Now simplify / rearrange / classify it:

``` text
search from right
        ↓
four targets
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `search from right`. Then
simplify/rearrange it to `four targets`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
search from right
  ↓ simplify
four targets
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `search from right`, evaluate
it step by step, and verify the transformed condition `four targets`.
The final value/condition gives min deletions.

------------------------------------------------------------------------

## Problem 126 --- CF 1669F --- Eating Candies

**Problem:** [CF 1669F --- Eating Candies]()\
**Topic / Rating:** Prefix+Two Pointers / 1100

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives array. The task is to equal left/right eaten sum
maximize count.

**What must we output?** equal left/right eaten sum maximize count

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: array. Mathematical state → monotone sums.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: array. Useful state: monotone sums. Unknown: equal left/right eaten sum maximize count.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
advance smaller side
```

Now simplify / rearrange / classify it:

``` text
advance smaller side
        ↓
two pointers
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `advance smaller side`. Then
simplify/rearrange it to `two pointers`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
advance smaller side
  ↓ simplify
two pointers
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `advance smaller side`,
evaluate it step by step, and verify the transformed condition
`two pointers`. The final value/condition gives equal left/right eaten
sum maximize count.

------------------------------------------------------------------------

## Problem 127 --- CF 1793C --- Dora and Search

**Problem:** [CF 1793C --- Dora and Search]()\
**Topic / Rating:** Extremes+Two Pointers / 1200

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives permutation. The task is to find non-extreme-ended
segment.

**What must we output?** find non-extreme-ended segment

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: permutation. Mathematical state → peel min/max endpoints.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: permutation. Useful state: peel min/max endpoints. Unknown: find non-extreme-ended segment.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
lo/hi invariant
```

Now simplify / rearrange / classify it:

``` text
lo/hi invariant
        ↓
two pointers
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `lo/hi invariant`. Then
simplify/rearrange it to `two pointers`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
lo/hi invariant
  ↓ simplify
two pointers
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `lo/hi invariant`, evaluate it
step by step, and verify the transformed condition `two pointers`. The
final value/condition gives find non-extreme-ended segment.

------------------------------------------------------------------------

## Problem 128 --- CF 327A --- Flipping Game

**Problem:** [CF 327A --- Flipping Game]()\
**Topic / Rating:** Transform+Optimization / 1200

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives binary array. The task is to one flip maximize ones.

**What must we output?** one flip maximize ones

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: binary array. Mathematical state → gain map 0→+1,1→-1.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: binary array. Useful state: gain map 0→+1,1→-1. Unknown: one flip maximize ones.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
maximum subarray
```

Now simplify / rearrange / classify it:

``` text
maximum subarray
        ↓
Kadane
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `maximum subarray`. Then
simplify/rearrange it to `Kadane`. This is the point where the story
disappears and the solution follows from the transformed condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
maximum subarray
  ↓ simplify
Kadane
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `maximum subarray`, evaluate
it step by step, and verify the transformed condition `Kadane`. The
final value/condition gives one flip maximize ones.

------------------------------------------------------------------------

## Problem 129 --- CF 1520D --- Same Differences

**Problem:** [CF 1520D --- Same Differences]()\
**Topic / Rating:** Algebra+Frequency / 1200

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives array. The task is to count special pairs.

**What must we output?** count special pairs

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: array. Mathematical state → a[j]-j=a[i]-i.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: array. Useful state: a[j]-j=a[i]-i. Unknown: count special pairs.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
equal transformed keys
```

Now simplify / rearrange / classify it:

``` text
equal transformed keys
        ↓
hash frequency
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `equal transformed keys`. Then
simplify/rearrange it to `hash frequency`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
equal transformed keys
  ↓ simplify
hash frequency
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `equal transformed keys`,
evaluate it step by step, and verify the transformed condition
`hash frequency`. The final value/condition gives count special pairs.

------------------------------------------------------------------------

## Problem 130 --- CF 276C --- Little Girl and Maximum Sum

**Problem:** [CF 276C --- Little Girl and Maximum Sum]()\
**Topic / Rating:** Difference+Sorting / 1400

### 1. Full Problem Story --- What Is Actually Happening?

The statement gives array,range queries. The task is to maximize
weighted sum.

**What must we output?** maximize weighted sum

### 2. Remove the Story Nouns

``` text
Story-specific names → discard them. Keep only: array,range queries. Mathematical state → usage frequency per index.
```

The purpose of this step is not to solve yet. It is to turn the
narrative into mathematical objects.

### 3. Extract the Variables

``` text
Given: array,range queries. Useful state: usage frequency per index. Unknown: maximize weighted sum.
```

### 4. Formulate the Mathematics

Start from the rule hidden in the statement:

``` text
sort both sequences
```

Now simplify / rearrange / classify it:

``` text
sort both sequences
        ↓
rearrangement
```

### 5. How the Formula Leads to the Final Solution

Translate the decisive rule into `sort both sequences`. Then
simplify/rearrange it to `rearrangement`. This is the point where the
story disappears and the solution follows from the transformed
condition.

``` text
STORY
  ↓ remove names
VARIABLES
  ↓ translate rules
sort both sequences
  ↓ simplify
rearrangement
  ↓
FINAL SOLUTION
```

### 6. Dry Run --- See It Work

Take the smallest official/sample input. Replace the story objects by
the variables above. Substitute them into `sort both sequences`,
evaluate it step by step, and verify the transformed condition
`rearrangement`. The final value/condition gives maximize weighted sum.

------------------------------------------------------------------------
