# CF Mathematical Modeling — Phase 1 Practice

> Goal: decode the statement before thinking about code.

```text
PROBLEM STORY → REMOVE STORY NOUNS → VARIABLES → PLAIN-ENGLISH RULES → QUICK LOGIC → DRY RUN
```

## Table of Contents

- [Pattern 1 — Minimum Operations / Ceil Division](#pattern-1-minimum-operations-ceil-division)
  - [CF 617A — Elephant](#cf-617a-elephant)
  - [CF 1409A — Yet Another Two Integers Problem](#cf-1409a-yet-another-two-integers-problem)
  - [CF 1353A — Most Unstable Array](#cf-1353a-most-unstable-array)
  - [CF 1476A — K-divisible Sum](#cf-1476a-k-divisible-sum)
  - [CF 151A — Soft Drinking](#cf-151a-soft-drinking)
  - [CF 996A — Hit the Lottery](#cf-996a-hit-the-lottery)
  - [CF 1669A — Division?](#cf-1669a-division)
  - [CF 1742A — Sum](#cf-1742a-sum)
  - [CF 1850A — To My Critics](#cf-1850a-to-my-critics)
  - [CF 1878A — How Much Does Daytona Cost?](#cf-1878a-how-much-does-daytona-cost)
- [Pattern 2 — Algebra / Equation Formation](#pattern-2-algebra-equation-formation)
  - [CF 734A — Anton and Danik](#cf-734a-anton-and-danik)
  - [CF 677A — Vanya and Fence](#cf-677a-vanya-and-fence)
  - [CF 71A — Way Too Long Words](#cf-71a-way-too-long-words)
  - [CF 791A — Bear and Big Brother](#cf-791a-bear-and-big-brother)
  - [CF 50A — Domino piling](#cf-50a-domino-piling)
  - [CF 231A — Team](#cf-231a-team)
  - [CF 200B — Drinks](#cf-200b-drinks)
  - [CF 318A — Even Odds](#cf-318a-even-odds)
  - [CF 486A — Calculating Function](#cf-486a-calculating-function)
  - [CF 1399A — Remove Smallest](#cf-1399a-remove-smallest)
- [Pattern 3 — Bounds / Inequalities / Min-Max](#pattern-3-bounds-inequalities-min-max)
  - [CF 1690A — Print a Pedestal](#cf-1690a-print-a-pedestal)
  - [CF 1676A — Lucky?](#cf-1676a-lucky)
  - [CF 1742B — Increasing](#cf-1742b-increasing)
  - [CF 1791A — Codeforces Checking](#cf-1791a-codeforces-checking)
  - [CF 1829A — Love Story](#cf-1829a-love-story)
  - [CF 1873A — Short Sort](#cf-1873a-short-sort)
  - [CF 1729A — Two Elevators](#cf-1729a-two-elevators)
  - [CF 1805A — We Need the Zero](#cf-1805a-we-need-the-zero)
  - [CF 1858A — Buttons](#cf-1858a-buttons)
  - [CF 1899A — Game with Integers](#cf-1899a-game-with-integers)
- [Pattern 4 — Parity Modeling](#pattern-4-parity-modeling)
  - [CF 4A — Watermelon](#cf-4a-watermelon)
  - [CF 1296A — Array with Odd Sum](#cf-1296a-array-with-odd-sum)
  - [CF 1857A — Array Coloring](#cf-1857a-array-coloring)
  - [CF 1834A — Unit Array](#cf-1834a-unit-array)
  - [CF 1367B — Even Array](#cf-1367b-even-array)
  - [CF 1475A — Odd Divisor](#cf-1475a-odd-divisor)
  - [CF 1669C — Odd/Even Increments](#cf-1669c-oddeven-increments)
  - [CF 1624A — Plus One on the Subset](#cf-1624a-plus-one-on-the-subset)
  - [CF 1788A — One and Two](#cf-1788a-one-and-two)
  - [CF 1845A — Forbidden Integer](#cf-1845a-forbidden-integer)
- [Pattern 5 — Divisibility / GCD / LCM](#pattern-5-divisibility-gcd-lcm)
  - [CF 1328A — Divisibility Problem](#cf-1328a-divisibility-problem)
  - [CF 1343A — Candies](#cf-1343a-candies)
  - [CF 1370A — Maximum GCD](#cf-1370a-maximum-gcd)
  - [CF 1829C — Mr. Perfectly Fine](#cf-1829c-mr-perfectly-fine)
  - [CF 1618A — Polycarp and Sums of Subsequences](#cf-1618a-polycarp-and-sums-of-subsequences)
  - [CF 160A — Twins](#cf-160a-twins)
  - [CF 1475B — New Year's Number](#cf-1475b-new-years-number)
  - [CF 1593A — Elections](#cf-1593a-elections)
  - [CF 1829B — Blank Space](#cf-1829b-blank-space)
  - [CF 1877A — Goals of Victory](#cf-1877a-goals-of-victory)
- [Pattern 6 — Modulo / Cyclic Modeling](#pattern-6-modulo-cyclic-modeling)
  - [CF 116A — Tram](#cf-116a-tram)
  - [CF 266A — Stones on the Table](#cf-266a-stones-on-the-table)
  - [CF 228A — Is your horseshoe on the other hoof?](#cf-228a-is-your-horseshoe-on-the-other-hoof)
  - [CF 443A — Anton and Letters](#cf-443a-anton-and-letters)
  - [CF 59A — Word](#cf-59a-word)
  - [CF 236A — Boy or Girl](#cf-236a-boy-or-girl)
  - [CF 785A — Anton and Polyhedrons](#cf-785a-anton-and-polyhedrons)
  - [CF 703A — Mishka and Game](#cf-703a-mishka-and-game)
  - [CF 734B — Anton and Digits](#cf-734b-anton-and-digits)
  - [CF 1097A — Gennady the Card Game](#cf-1097a-gennady-the-card-game)
- [Pattern 7 — Counting / Frequency / Pairs](#pattern-7-counting-frequency-pairs)
  - [CF 1520D — Same Differences](#cf-1520d-same-differences)
  - [CF 1538C — Challenging Cliffs / Number of Pairs](#cf-1538c-challenging-cliffs-number-of-pairs)
  - [CF 1669B — Triple](#cf-1669b-triple)
  - [CF 1742C — Stripes](#cf-1742c-stripes)
  - [CF 1791B — Following Directions](#cf-1791b-following-directions)
  - [CF 1703B — ICPC Balloons](#cf-1703b-icpc-balloons)
  - [CF 1722A — Spell Check](#cf-1722a-spell-check)
  - [CF 1791C — Prepend and Append](#cf-1791c-prepend-and-append)
  - [CF 1829D — Gold Rush](#cf-1829d-gold-rush)
  - [CF 1878B — Aleksa and Stack](#cf-1878b-aleksa-and-stack)
- [Pattern 8 — Operation → Delta → Invariant](#pattern-8-operation-delta-invariant)
  - [CF 1538B — Friends and Candies](#cf-1538b-friends-and-candies)
  - [CF 1855A — Dalton the Teacher](#cf-1855a-dalton-the-teacher)
  - [CF 1838A — Blackboard List](#cf-1838a-blackboard-list)
  - [CF 1862B — Sequence Game](#cf-1862b-sequence-game)
  - [CF 1798A — Showstopper](#cf-1798a-showstopper)
  - [CF 660A — Co-prime Array](#cf-660a-co-prime-array)
  - [CF 1367A — Short Substrings](#cf-1367a-short-substrings)
  - [CF 1374A — Required Remainder](#cf-1374a-required-remainder)
  - [CF 1551A — Polycarp and Coins](#cf-1551a-polycarp-and-coins)
  - [CF 1818A — Politics](#cf-1818a-politics)
- [Pattern 9 — Sorting / Coordinate / Distance Modeling](#pattern-9-sorting-coordinate-distance-modeling)
  - [CF 160A — Twins](#cf-160a-twins)
  - [CF 1399A — Remove Smallest](#cf-1399a-remove-smallest)
  - [CF 1760A — Medium Number](#cf-1760a-medium-number)
  - [CF 1538A — Stone Game](#cf-1538a-stone-game)
  - [CF 1729A — Two Elevators](#cf-1729a-two-elevators)
  - [CF 1593B — Make it Divisible by 25](#cf-1593b-make-it-divisible-by-25)
  - [CF 1742F — Smaller](#cf-1742f-smaller)
  - [CF 1831A — Twin Permutations](#cf-1831a-twin-permutations)
  - [CF 1900A — Cover in Water](#cf-1900a-cover-in-water)
  - [CF 1873B — Good Kid](#cf-1873b-good-kid)
- [Pattern 10 — Prefix / Running-State Modeling](#pattern-10-prefix-running-state-modeling)
  - [CF 116A — Tram](#cf-116a-tram)
  - [CF 363B — Fence](#cf-363b-fence)
  - [CF 276C — Little Girl and Problem on Trees / Little Girl and Maximum Sum](#cf-276c-little-girl-and-problem-on-trees-little-girl-and-maximum-sum)
  - [CF 433B — Kuriyama Mirai's Stones](#cf-433b-kuriyama-mirais-stones)
  - [CF 313B — Ilya and Queries](#cf-313b-ilya-and-queries)
  - [CF 327A — Flipping Game](#cf-327a-flipping-game)
  - [CF 580A — Kefa and First Steps](#cf-580a-kefa-and-first-steps)
  - [CF 702A — Maximum Increase](#cf-702a-maximum-increase)
  - [CF 1829B — Blank Space](#cf-1829b-blank-space)
  - [CF 1669F — Eating Candies](#cf-1669f-eating-candies)
- [Pattern 11 — Constructive / Reachability Modeling](#pattern-11-constructive-reachability-modeling)
  - [CF 1690A — Print a Pedestal](#cf-1690a-print-a-pedestal)
  - [CF 1845A — Forbidden Integer](#cf-1845a-forbidden-integer)
  - [CF 1878B — Aleksa and Stack](#cf-1878b-aleksa-and-stack)
  - [CF 1741A — Compare T-Shirt Sizes](#cf-1741a-compare-t-shirt-sizes)
  - [CF 1805B — We Need the Zero / The String Has a Target](#cf-1805b-we-need-the-zero-the-string-has-a-target)
  - [CF 1833B — Restore the Weather](#cf-1833b-restore-the-weather)
  - [CF 1793C — Dora and Search](#cf-1793c-dora-and-search)
  - [CF 1881A — Don't Try to Count](#cf-1881a-dont-try-to-count)
  - [CF 1858A — Buttons](#cf-1858a-buttons)
  - [CF 1899A — Game with Integers](#cf-1899a-game-with-integers)
- [Pattern 12 — Bitwise / XOR Modeling](#pattern-12-bitwise-xor-modeling)
  - [CF 1805A — We Need the Zero](#cf-1805a-we-need-the-zero)
  - [CF 1872A — Two Vessels](#cf-1872a-two-vessels)
  - [CF 1703A — YES or YES?](#cf-1703a-yes-or-yes)
  - [CF 1624A — Plus One on the Subset](#cf-1624a-plus-one-on-the-subset)
  - [CF 1220A — Cards](#cf-1220a-cards)
  - [CF 1362A — Johnny and Ancient Computer](#cf-1362a-johnny-and-ancient-computer)
  - [CF 1095A — Repeating Cipher](#cf-1095a-repeating-cipher)
  - [CF 1324A — Yet Another Tetris Problem](#cf-1324a-yet-another-tetris-problem)
  - [CF 1462A — Favorite Sequence](#cf-1462a-favorite-sequence)
  - [CF 1619A — Polycarp and Sums of Subsequences / Square String?](#cf-1619a-polycarp-and-sums-of-subsequences-square-string)
- [Pattern 13 — Mixed Blind Decoding](#pattern-13-mixed-blind-decoding)
  - [CF 1538C — Challenging Cliffs / Number of Pairs](#cf-1538c-challenging-cliffs-number-of-pairs)
  - [CF 1475B — New Year's Number](#cf-1475b-new-years-number)
  - [CF 1374A — Required Remainder](#cf-1374a-required-remainder)
  - [CF 1551A — Polycarp and Coins](#cf-1551a-polycarp-and-coins)
  - [CF 1593B — Make it Divisible by 25](#cf-1593b-make-it-divisible-by-25)
  - [CF 1669F — Eating Candies](#cf-1669f-eating-candies)
  - [CF 1793C — Dora and Search](#cf-1793c-dora-and-search)
  - [CF 327A — Flipping Game](#cf-327a-flipping-game)
  - [CF 1520D — Same Differences](#cf-1520d-same-differences)
  - [CF 276C — Little Girl and Maximum Sum](#cf-276c-little-girl-and-maximum-sum)

# Pattern 1 — Minimum Operations / Ceil Division

## CF 617A — Elephant

**Problem Link:** [Codeforces — CF 617A — Elephant](https://codeforces.com/problemset/problem/617/A)  
**Rating:** 800

### 1. The Problem Story

An elephant starts at position 0 and wants to reach his friend's house at position x. In one move he can advance by 1, 2, 3, 4, or 5 positions. Find the minimum number of moves needed to reach x.

**Target / Goal**
- minimum moves to reach x with +1..+5

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- elephant → moving point
- friend's house → target coordinate x
- step → one operation
- 1..5 positions → move size ≤ 5

**Essential variables**

```text
Input:  x
State:  D=x, K=5
Target: minimum moves to reach x with +1..+5
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: D=x, K=5.
- Translate the decisive condition into: `5m >= x`.
- Simplify/recognize it as: `ceil(x/5)`.
- The required output is: minimum moves to reach x with +1..+5.

**Mathematical form**

```text
5m >= x
    ↓
ceil(x/5)
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: D=x, K=5.
2. Apply the condition `5m >= x`.
3. Use `ceil(x/5)` to obtain minimum moves to reach x with +1..+5.

### 5. Step 4: Quick Dry Run Example

```text
Example: x = 17

Start at 0. Maximum useful move = 5.

m=3 → maximum distance = 3×5 = 15 < 17  ❌
m=4 → maximum distance = 4×5 = 20 ≥ 17  ✅

One valid movement:
0 → 5 → 10 → 15 → 17

Answer = ceil(17/5) = 4.
```

---

## CF 1409A — Yet Another Two Integers Problem

**Problem Link:** [Codeforces — CF 1409A — Yet Another Two Integers Problem](https://codeforces.com/problemset/problem/1409/A)  
**Rating:** 800

### 1. The Problem Story

You are given two integers a and b. In one operation you may add or subtract any integer from 1 to 10 from a. Find the minimum number of operations needed to make a equal to b.

**Target / Goal**
- minimum operations to make a=b using ±1..10

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- integer a → current value
- integer b → target value
- one operation → change a toward b
- 1..10 → maximum useful change is 10

**Essential variables**

```text
Input:  a,b
State:  D=|a-b|, K=10
Target: minimum operations to make a=b using ±1..10
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: D=|a-b|, K=10.
- Translate the decisive condition into: `10m >= |a-b|`.
- Simplify/recognize it as: `ceil(|a-b|/10)`.
- The required output is: minimum operations to make a=b using ±1..10.

**Mathematical form**

```text
10m >= |a-b|
    ↓
ceil(|a-b|/10)
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: D=|a-b|, K=10.
2. Apply the condition `10m >= |a-b|`.
3. Use `ceil(|a-b|/10)` to obtain minimum operations to make a=b using ±1..10.

### 5. Step 4: Quick Dry Run Example

```text
Example: a=26, b=9

D = |26-9| = 17

After 1 move: reduce by 10 → remaining D=7
After 2 moves: reduce by 7  → remaining D=0

Answer = ceil(17/10) = 2.
```

---

## CF 1353A — Most Unstable Array

**Problem Link:** [Codeforces — CF 1353A — Most Unstable Array](https://codeforces.com/problemset/problem/1353/A)  
**Rating:** 800

### 1. The Problem Story

You are given n,m. The problem asks you to maximize sum of adjacent absolute differences under bounds. The story can be reduced to the mathematical state: endpoints/bounds matter.

**Target / Goal**
- maximize sum of adjacent absolute differences under bounds

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → n,m
- keep mathematical state → endpoints/bounds matter
- target → maximize sum of adjacent absolute differences under bounds

**Essential variables**

```text
Input:  n,m
State:  endpoints/bounds matter
Target: maximize sum of adjacent absolute differences under bounds
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: endpoints/bounds matter.
- Translate the decisive condition into: `each transition <= m`.
- Simplify/recognize it as: `construct extremal arrangement`.
- The required output is: maximize sum of adjacent absolute differences under bounds.

**Mathematical form**

```text
each transition <= m
    ↓
construct extremal arrangement
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: endpoints/bounds matter.
2. Apply the condition `each transition <= m`.
3. Use `construct extremal arrangement` to obtain maximize sum of adjacent absolute differences under bounds.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
endpoints/bounds matter

At each step evaluate:
each transition <= m

Then apply:
construct extremal arrangement

Stop when the target is determined:
maximize sum of adjacent absolute differences under bounds.
```

---

## CF 1476A — K-divisible Sum

**Problem Link:** [Codeforces — CF 1476A — K-divisible Sum](https://codeforces.com/problemset/problem/1476/A)  
**Rating:** 1000

### 1. The Problem Story

You are given n,k. The problem asks you to minimum possible maximum element while sum is divisible by k. The story can be reduced to the mathematical state: total S >= n and S multiple of k.

**Target / Goal**
- minimum possible maximum element while sum is divisible by k

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → n,k
- keep mathematical state → total S >= n and S multiple of k
- target → minimum possible maximum element while sum is divisible by k

**Essential variables**

```text
Input:  n,k
State:  total S >= n and S multiple of k
Target: minimum possible maximum element while sum is divisible by k
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: total S >= n and S multiple of k.
- Translate the decisive condition into: `S = smallest multiple of k >= n`.
- Simplify/recognize it as: `ceil(S/n)`.
- The required output is: minimum possible maximum element while sum is divisible by k.

**Mathematical form**

```text
S = smallest multiple of k >= n
    ↓
ceil(S/n)
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: total S >= n and S multiple of k.
2. Apply the condition `S = smallest multiple of k >= n`.
3. Use `ceil(S/n)` to obtain minimum possible maximum element while sum is divisible by k.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
total S >= n and S multiple of k

At each step evaluate:
S = smallest multiple of k >= n

Then apply:
ceil(S/n)

Stop when the target is determined:
minimum possible maximum element while sum is divisible by k.
```

---

## CF 151A — Soft Drinking

**Problem Link:** [Codeforces — CF 151A — Soft Drinking](https://codeforces.com/problemset/problem/151/A)  
**Rating:** 800

### 1. The Problem Story

A group of friends has drink, lime slices, and salt. Every toast consumes a fixed amount of each resource, and all friends must get the same number of toasts. Find the maximum number of toasts each friend can make.

**Target / Goal**
- number of toasts per friend

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- friends → n equal recipients
- drink → resource 1
- lime slices → resource 2
- salt → resource 3
- one toast → consumes fixed amounts of all resources

**Essential variables**

```text
Input:  n,k,l,c,d,p,nl,np
State:  three resources
Target: number of toasts per friend
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: three resources.
- Translate the decisive condition into: `min(drink/nl,limes,salt/np)/n`.
- Simplify/recognize it as: `limiting resource`.
- The required output is: number of toasts per friend.

**Mathematical form**

```text
min(drink/nl,limes,salt/np)/n
    ↓
limiting resource
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: three resources.
2. Apply the condition `min(drink/nl,limes,salt/np)/n`.
3. Use `limiting resource` to obtain number of toasts per friend.

### 5. Step 4: Quick Dry Run Example

```text
Example:
n=3, k=4, l=5, c=10, d=8, p=100, nl=3, np=1

Drink = k·l = 20 ml → floor(20/3)=6 toasts
Limes = c·d = 80 slices → 80 toasts
Salt = floor(100/1)=100 toasts

Total possible = min(6,80,100)=6
Per friend = floor(6/3)=2

Answer = 2.
```

---

## CF 996A — Hit the Lottery

**Problem Link:** [Codeforces — CF 996A — Hit the Lottery](https://codeforces.com/problemset/problem/996/A)  
**Rating:** 800

### 1. The Problem Story

You must pay an amount n using banknotes of values 1, 5, 10, 20, and 100. Find the minimum number of banknotes needed.

**Target / Goal**
- minimum notes using 100,20,10,5,1

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 
- keep mathematical state → largest denomination dominates
- target → minimum notes using 100,20,10,5,1

**Essential variables**

```text
Input:  
State:  largest denomination dominates
Target: minimum notes using 100,20,10,5,1
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: largest denomination dominates.
- Translate the decisive condition into: `q=n/d`.
- Simplify/recognize it as: `sum quotients`.
- The required output is: minimum notes using 100,20,10,5,1.

**Mathematical form**

```text
q=n/d
    ↓
sum quotients
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: largest denomination dominates.
2. Apply the condition `q=n/d`.
3. Use `sum quotients` to obtain minimum notes using 100,20,10,5,1.

### 5. Step 4: Quick Dry Run Example

```text
Example: n=125

125/100 = 1 note, remainder 25
25/20   = 1 note, remainder 5
5/5     = 1 note, remainder 0

Total notes = 3.
```

---

## CF 1669A — Division?

**Problem Link:** [Codeforces — CF 1669A — Division?](https://codeforces.com/problemset/problem/1669/A)  
**Rating:** 800

### 1. The Problem Story

A Codeforces rating belongs to one of four divisions. Given the rating, determine which division interval contains it.

**Target / Goal**
- classify rating into interval

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → rating
- keep mathematical state → numeric boundaries
- target → classify rating into interval

**Essential variables**

```text
Input:  rating
State:  numeric boundaries
Target: classify rating into interval
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: numeric boundaries.
- Translate the decisive condition into: `compare rating with cutoffs`.
- Simplify/recognize it as: `interval classification`.
- The required output is: classify rating into interval.

**Mathematical form**

```text
compare rating with cutoffs
    ↓
interval classification
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: numeric boundaries.
2. Apply the condition `compare rating with cutoffs`.
3. Use `interval classification` to obtain classify rating into interval.

### 5. Step 4: Quick Dry Run Example

```text
Example: rating=1750

1750 < 1900
1750 ≥ 1600

So rating lies in [1600,1899].
Answer = Division 2.
```

---

## CF 1742A — Sum

**Problem Link:** [Codeforces — CF 1742A — Sum](https://codeforces.com/problemset/problem/1742/A)  
**Rating:** 800

### 1. The Problem Story

You are given three integers a, b, and c. Determine whether one of the three numbers is equal to the sum of the other two.

**Target / Goal**
- whether one number equals sum of other two

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 
- keep mathematical state → test 3 equations
- target → whether one number equals sum of other two

**Essential variables**

```text
Input:  
State:  test 3 equations
Target: whether one number equals sum of other two
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: test 3 equations.
- Translate the decisive condition into: `a+b=c etc.`.
- Simplify/recognize it as: `direct feasibility`.
- The required output is: whether one number equals sum of other two.

**Mathematical form**

```text
a+b=c etc.
    ↓
direct feasibility
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: test 3 equations.
2. Apply the condition `a+b=c etc.`.
3. Use `direct feasibility` to obtain whether one number equals sum of other two.

### 5. Step 4: Quick Dry Run Example

```text
Example: a=3, b=5, c=2

Check:
a+b=c → 3+5=2 ❌
a+c=b → 3+2=5 ✅

Therefore one number equals the sum of the other two.
Answer = YES.
```

---

## CF 1850A — To My Critics

**Problem Link:** [Codeforces — CF 1850A — To My Critics](https://codeforces.com/problemset/problem/1850/A)  
**Rating:** 800

### 1. The Problem Story

Three scores a, b, and c are given. Determine whether some pair of scores has sum at least 10.

**Target / Goal**
- whether any pair sum >=10

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 
- keep mathematical state → only 3 pairs
- target → whether any pair sum >=10

**Essential variables**

```text
Input:  
State:  only 3 pairs
Target: whether any pair sum >=10
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: only 3 pairs.
- Translate the decisive condition into: `max pair sum`.
- Simplify/recognize it as: `sort or direct checks`.
- The required output is: whether any pair sum >=10.

**Mathematical form**

```text
max pair sum
    ↓
sort or direct checks
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: only 3 pairs.
2. Apply the condition `max pair sum`.
3. Use `sort or direct checks` to obtain whether any pair sum >=10.

### 5. Step 4: Quick Dry Run Example

```text
Example: a=4,b=7,c=1

Pair sums:
4+7=11 ≥ 10 ✅
4+1=5
7+1=8

At least one pair works.
Answer = YES.
```

---

## CF 1878A — How Much Does Daytona Cost?

**Problem Link:** [Codeforces — CF 1878A — How Much Does Daytona Cost?](https://codeforces.com/problemset/problem/1878/A)  
**Rating:** 800

### 1. The Problem Story

An array of n integers and a target value k are given. Determine whether k occurs anywhere in the array.

**Target / Goal**
- whether k appears

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → n,k,array
- keep mathematical state → target is existence
- target → whether k appears

**Essential variables**

```text
Input:  n,k,array
State:  target is existence
Target: whether k appears
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: target is existence.
- Translate the decisive condition into: `∃i: a[i]=k`.
- Simplify/recognize it as: `linear scan`.
- The required output is: whether k appears.

**Mathematical form**

```text
∃i: a[i]=k
    ↓
linear scan
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: target is existence.
2. Apply the condition `∃i: a[i]=k`.
3. Use `linear scan` to obtain whether k appears.

### 5. Step 4: Quick Dry Run Example

```text
Example: n=5, k=7
a=[1,4,7,2,9]

scan:
1≠7
4≠7
7=7 ✅

So ∃i with a[i]=k.
Answer = YES.
```

---

# Pattern 2 — Algebra / Equation Formation

## CF 734A — Anton and Danik

**Problem Link:** [Codeforces — CF 734A — Anton and Danik](https://codeforces.com/problemset/problem/734/A)  
**Rating:** 800

### 1. The Problem Story

Anton and Danik play n chess games; each character in the result string records who won a game. Count each player's wins and report Anton, Danik, or Friendship if tied.

**Target / Goal**
- who won more games

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → n,string
- keep mathematical state → A=count('A'), D=count('D')
- target → who won more games

**Essential variables**

```text
Input:  n,string
State:  A=count('A'), D=count('D')
Target: who won more games
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: A=count('A'), D=count('D').
- Translate the decisive condition into: `compare A and D`.
- Simplify/recognize it as: `sign of A-D`.
- The required output is: who won more games.

**Mathematical form**

```text
compare A and D
    ↓
sign of A-D
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: A=count('A'), D=count('D').
2. Apply the condition `compare A and D`.
3. Use `sign of A-D` to obtain who won more games.

### 5. Step 4: Quick Dry Run Example

```text
Example: s="ADAAA"

A-count: 1→1, 2→1, 3→2, 4→3, 5→4
D-count: 0→1→1→1→1

A=4, D=1
A>D → Anton wins.
```

---

## CF 677A — Vanya and Fence

**Problem Link:** [Codeforces — CF 677A — Vanya and Fence](https://codeforces.com/problemset/problem/677/A)  
**Rating:** 800

### 1. The Problem Story

n friends walk along a fence of height h. A friend of height at most h needs width 1, while a taller friend bends down and needs width 2. Find the total width required.

**Target / Goal**
- total width

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → n,h,heights
- keep mathematical state → each person contributes 1 or 2
- target → total width

**Essential variables**

```text
Input:  n,h,heights
State:  each person contributes 1 or 2
Target: total width
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: each person contributes 1 or 2.
- Translate the decisive condition into: `sum (a[i]>h ? 2:1)`.
- Simplify/recognize it as: `contribution sum`.
- The required output is: total width.

**Mathematical form**

```text
sum (a[i]>h ? 2:1)
    ↓
contribution sum
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: each person contributes 1 or 2.
2. Apply the condition `sum (a[i]>h ? 2:1)`.
3. Use `contribution sum` to obtain total width.

### 5. Step 4: Quick Dry Run Example

```text
Example: h=5, heights=[4,7,5]

4≤5 → width +1 → total=1
7>5 → width +2 → total=3
5≤5 → width +1 → total=4

Answer=4.
```

---

## CF 71A — Way Too Long Words

**Problem Link:** [Codeforces — CF 71A — Way Too Long Words](https://codeforces.com/problemset/problem/71/A)  
**Rating:** 800

### 1. The Problem Story

For each word, words of length at most 10 stay unchanged. Longer words are abbreviated using the first letter, the number of omitted middle letters, and the last letter.

**Target / Goal**
- abbreviate if length>10

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → word
- keep mathematical state → first + (len-2) + last
- target → abbreviate if length>10

**Essential variables**

```text
Input:  word
State:  first + (len-2) + last
Target: abbreviate if length>10
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: first + (len-2) + last.
- Translate the decisive condition into: `length condition`.
- Simplify/recognize it as: `direct construction`.
- The required output is: abbreviate if length>10.

**Mathematical form**

```text
length condition
    ↓
direct construction
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: first + (len-2) + last.
2. Apply the condition `length condition`.
3. Use `direct construction` to obtain abbreviate if length>10.

### 5. Step 4: Quick Dry Run Example

```text
Example: word="localization", length=12

12>10, so abbreviate:
first='l'
middle count=12-2=10
last='n'

Result = "l10n".
```

---

## CF 791A — Bear and Big Brother

**Problem Link:** [Codeforces — CF 791A — Bear and Big Brother](https://codeforces.com/problemset/problem/791/A)  
**Rating:** 800

### 1. The Problem Story

Limak initially weighs a and Bob weighs b, with a ≤ b. Each year Limak triples his weight and Bob doubles his. Find the first year when Limak becomes strictly heavier.

**Target / Goal**
- years until 3^t a > 2^t b

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → a,b
- keep mathematical state → simulate multiplicative equation
- target → years until 3^t a > 2^t b

**Essential variables**

```text
Input:  a,b
State:  simulate multiplicative equation
Target: years until 3^t a > 2^t b
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: simulate multiplicative equation.
- Translate the decisive condition into: `a*=3,b*=2`.
- Simplify/recognize it as: `first t with a>b`.
- The required output is: years until 3^t a > 2^t b.

**Mathematical form**

```text
a*=3,b*=2
    ↓
first t with a>b
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: simulate multiplicative equation.
2. Apply the condition `a*=3,b*=2`.
3. Use `first t with a>b` to obtain years until 3^t a > 2^t b.

### 5. Step 4: Quick Dry Run Example

```text
Example: a=4,b=7

year 0: 4,7
year 1: 12,14
year 2: 36,28

Now 36>28.
Answer=2 years.
```

---

## CF 50A — Domino piling

**Problem Link:** [Codeforces — CF 50A — Domino piling](https://codeforces.com/problemset/problem/50/A)  
**Rating:** 800

### 1. The Problem Story

An m×n board must be covered with 2×1 dominoes without overlap. Find the maximum number of dominoes that can be placed.

**Target / Goal**
- max dominoes in grid

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 
- keep mathematical state → each domino covers 2 cells
- target → max dominoes in grid

**Essential variables**

```text
Input:  
State:  each domino covers 2 cells
Target: max dominoes in grid
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: each domino covers 2 cells.
- Translate the decisive condition into: `2x <= mn`.
- Simplify/recognize it as: `floor(mn/2)`.
- The required output is: max dominoes in grid.

**Mathematical form**

```text
2x <= mn
    ↓
floor(mn/2)
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: each domino covers 2 cells.
2. Apply the condition `2x <= mn`.
3. Use `floor(mn/2)` to obtain max dominoes in grid.

### 5. Step 4: Quick Dry Run Example

```text
Example: m=2,n=4

cells = 2×4 = 8
each domino covers 2 cells

x ≤ 8/2 = 4

A full tiling with 4 dominoes exists.
Answer=4.
```

---

## CF 231A — Team

**Problem Link:** [Codeforces — CF 231A — Team](https://codeforces.com/problemset/problem/231/A)  
**Rating:** 800

### 1. The Problem Story

For each contest problem, three friends say 0 or 1 depending on whether they know the solution. They implement a problem if at least two friends are sure. Count how many problems they will implement.

**Target / Goal**
- count problems with >=2 yes

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → triples
- keep mathematical state → sum triple >=2
- target → count problems with >=2 yes

**Essential variables**

```text
Input:  triples
State:  sum triple >=2
Target: count problems with >=2 yes
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: sum triple >=2.
- Translate the decisive condition into: `indicator contribution`.
- Simplify/recognize it as: `count`.
- The required output is: count problems with >=2 yes.

**Mathematical form**

```text
indicator contribution
    ↓
count
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: sum triple >=2.
2. Apply the condition `indicator contribution`.
3. Use `count` to obtain count problems with >=2 yes.

### 5. Step 4: Quick Dry Run Example

```text
Example triples:
1 1 0 → sum=2 → solve → count=1
1 0 0 → sum=1 → skip
1 1 1 → sum=3 → solve → count=2

Answer=2.
```

---

## CF 200B — Drinks

**Problem Link:** [Codeforces — CF 200B — Drinks](https://codeforces.com/problemset/problem/200/B)  
**Rating:** 800

### 1. The Problem Story

n drinks contain given percentages of orange juice. Equal amounts of all drinks are mixed together. Find the percentage of orange juice in the final mixture.

**Target / Goal**
- orange percentage

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 
- keep mathematical state → average of p
- target → orange percentage

**Essential variables**

```text
Input:  
State:  average of p
Target: orange percentage
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: average of p.
- Translate the decisive condition into: `sum/n`.
- Simplify/recognize it as: `mean`.
- The required output is: orange percentage.

**Mathematical form**

```text
sum/n
    ↓
mean
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: average of p.
2. Apply the condition `sum/n`.
3. Use `mean` to obtain orange percentage.

### 5. Step 4: Quick Dry Run Example

```text
Example: p=[50,100,0]

sum=150
n=3
average=150/3=50

Answer=50%.
```

---

## CF 318A — Even Odds

**Problem Link:** [Codeforces — CF 318A — Even Odds](https://codeforces.com/problemset/problem/318/A)  
**Rating:** 900

### 1. The Problem Story

Write the numbers 1..n with all odd numbers first and all even numbers second. Find the value at position k in this reordered sequence.

**Target / Goal**
- kth in odds then evens

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → n,k
- keep mathematical state → oddCount=(n+1)/2
- target → kth in odds then evens

**Essential variables**

```text
Input:  n,k
State:  oddCount=(n+1)/2
Target: kth in odds then evens
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: oddCount=(n+1)/2.
- Translate the decisive condition into: `piecewise index mapping`.
- Simplify/recognize it as: `if k<=oddCount`.
- The required output is: kth in odds then evens.

**Mathematical form**

```text
piecewise index mapping
    ↓
if k<=oddCount
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: oddCount=(n+1)/2.
2. Apply the condition `piecewise index mapping`.
3. Use `if k<=oddCount` to obtain kth in odds then evens.

### 5. Step 4: Quick Dry Run Example

```text
Example: n=7,k=5

Odds first: 1,3,5,7
Evens:      2,4,6

oddCount=4.
k=5 is the 1st even number.
value=2×1=2.

Answer=2.
```

---

## CF 486A — Calculating Function

**Problem Link:** [Codeforces — CF 486A — Calculating Function](https://codeforces.com/problemset/problem/486/A)  
**Rating:** 800

### 1. The Problem Story

Compute f(n) = -1 + 2 - 3 + 4 - ... with signs alternating up to n. Return the value without iterating through every term.

**Target / Goal**
- alternating sum -1+2-3+...

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 
- keep mathematical state → pair terms
- target → alternating sum -1+2-3+...

**Essential variables**

```text
Input:  
State:  pair terms
Target: alternating sum -1+2-3+...
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: pair terms.
- Translate the decisive condition into: `even n -> n/2; odd -> -(n+1)/2`.
- Simplify/recognize it as: `closed form`.
- The required output is: alternating sum -1+2-3+....

**Mathematical form**

```text
even n -> n/2; odd -> -(n+1)/2
    ↓
closed form
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: pair terms.
2. Apply the condition `even n -> n/2; odd -> -(n+1)/2`.
3. Use `closed form` to obtain alternating sum -1+2-3+....

### 5. Step 4: Quick Dry Run Example

```text
Example: n=5

-1+2-3+4-5
=(-1+2)+(-3+4)-5
=1+1-5
=-3

Formula for odd n:
-(n+1)/2 = -6/2 = -3.
```

---

## CF 1399A — Remove Smallest

**Problem Link:** [Codeforces — CF 1399A — Remove Smallest](https://codeforces.com/problemset/problem/1399/A)  
**Rating:** 800

### 1. The Problem Story

You may repeatedly choose two array elements whose difference is at most 1 and remove the smaller one. Determine whether the array can be reduced to one element.

**Target / Goal**
- can repeatedly remove smaller when diff<=1

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → array
- keep mathematical state → sorted adjacent gaps encode feasibility
- target → can repeatedly remove smaller when diff<=1

**Essential variables**

```text
Input:  array
State:  sorted adjacent gaps encode feasibility
Target: can repeatedly remove smaller when diff<=1
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: sorted adjacent gaps encode feasibility.
- Translate the decisive condition into: `max adjacent diff<=1`.
- Simplify/recognize it as: `sort + check`.
- The required output is: can repeatedly remove smaller when diff<=1.

**Mathematical form**

```text
max adjacent diff<=1
    ↓
sort + check
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: sorted adjacent gaps encode feasibility.
2. Apply the condition `max adjacent diff<=1`.
3. Use `sort + check` to obtain can repeatedly remove smaller when diff<=1.

### 5. Step 4: Quick Dry Run Example

```text
Example: [1,2,2,3]

Sort → [1,2,2,3]
adjacent gaps: 1,0,1
all gaps ≤1 ✅

The smaller elements can be removed successively.
Answer=YES.
```

---

# Pattern 3 — Bounds / Inequalities / Min-Max

## CF 1690A — Print a Pedestal

**Problem Link:** [Codeforces — CF 1690A — Print a Pedestal](https://codeforces.com/problemset/problem/1690/A)  
**Rating:** 800

### 1. The Problem Story

You are given . The problem asks you to split n into 3 positive distinct heights with middle ordering. The story can be reduced to the mathematical state: x<y<z and sum n.

**Target / Goal**
- split n into 3 positive distinct heights with middle ordering

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 
- keep mathematical state → x<y<z and sum n
- target → split n into 3 positive distinct heights with middle ordering

**Essential variables**

```text
Input:  
State:  x<y<z and sum n
Target: split n into 3 positive distinct heights with middle ordering
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: x<y<z and sum n.
- Translate the decisive condition into: `near n/3 then adjust`.
- Simplify/recognize it as: `construct around thirds`.
- The required output is: split n into 3 positive distinct heights with middle ordering.

**Mathematical form**

```text
near n/3 then adjust
    ↓
construct around thirds
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: x<y<z and sum n.
2. Apply the condition `near n/3 then adjust`.
3. Use `construct around thirds` to obtain split n into 3 positive distinct heights with middle ordering.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
x<y<z and sum n

At each step evaluate:
near n/3 then adjust

Then apply:
construct around thirds

Stop when the target is determined:
split n into 3 positive distinct heights with middle ordering.
```

---

## CF 1676A — Lucky?

**Problem Link:** [Codeforces — CF 1676A — Lucky?](https://codeforces.com/problemset/problem/1676/A)  
**Rating:** 800

### 1. The Problem Story

You are given 6-digit string. The problem asks you to first 3 digit sum equals last 3. The story can be reduced to the mathematical state: S1,S2.

**Target / Goal**
- first 3 digit sum equals last 3

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 6-digit string
- keep mathematical state → S1,S2
- target → first 3 digit sum equals last 3

**Essential variables**

```text
Input:  6-digit string
State:  S1,S2
Target: first 3 digit sum equals last 3
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: S1,S2.
- Translate the decisive condition into: `S1=S2`.
- Simplify/recognize it as: `direct compare`.
- The required output is: first 3 digit sum equals last 3.

**Mathematical form**

```text
S1=S2
    ↓
direct compare
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: S1,S2.
2. Apply the condition `S1=S2`.
3. Use `direct compare` to obtain first 3 digit sum equals last 3.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
S1,S2

At each step evaluate:
S1=S2

Then apply:
direct compare

Stop when the target is determined:
first 3 digit sum equals last 3.
```

---

## CF 1742B — Increasing

**Problem Link:** [Codeforces — CF 1742B — Increasing](https://codeforces.com/problemset/problem/1742/B)  
**Rating:** 800

### 1. The Problem Story

You are given array. The problem asks you to can permute to strictly increasing. The story can be reduced to the mathematical state: strictly increasing permutation iff all distinct.

**Target / Goal**
- can permute to strictly increasing

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → array
- keep mathematical state → strictly increasing permutation iff all distinct
- target → can permute to strictly increasing

**Essential variables**

```text
Input:  array
State:  strictly increasing permutation iff all distinct
Target: can permute to strictly increasing
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: strictly increasing permutation iff all distinct.
- Translate the decisive condition into: `freq<=1`.
- Simplify/recognize it as: `set size=n`.
- The required output is: can permute to strictly increasing.

**Mathematical form**

```text
freq<=1
    ↓
set size=n
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: strictly increasing permutation iff all distinct.
2. Apply the condition `freq<=1`.
3. Use `set size=n` to obtain can permute to strictly increasing.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
strictly increasing permutation iff all distinct

At each step evaluate:
freq<=1

Then apply:
set size=n

Stop when the target is determined:
can permute to strictly increasing.
```

---

## CF 1791A — Codeforces Checking

**Problem Link:** [Codeforces — CF 1791A — Codeforces Checking](https://codeforces.com/problemset/problem/1791/A)  
**Rating:** 800

### 1. The Problem Story

You are given . The problem asks you to whether c belongs to 'codeforces'. The story can be reduced to the mathematical state: c ∈ fixed set.

**Target / Goal**
- whether c belongs to 'codeforces'

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 
- keep mathematical state → c ∈ fixed set
- target → whether c belongs to 'codeforces'

**Essential variables**

```text
Input:  
State:  c ∈ fixed set
Target: whether c belongs to 'codeforces'
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: c ∈ fixed set.
- Translate the decisive condition into: `find char`.
- Simplify/recognize it as: `membership`.
- The required output is: whether c belongs to 'codeforces'.

**Mathematical form**

```text
find char
    ↓
membership
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: c ∈ fixed set.
2. Apply the condition `find char`.
3. Use `membership` to obtain whether c belongs to 'codeforces'.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
c ∈ fixed set

At each step evaluate:
find char

Then apply:
membership

Stop when the target is determined:
whether c belongs to 'codeforces'.
```

---

## CF 1829A — Love Story

**Problem Link:** [Codeforces — CF 1829A — Love Story](https://codeforces.com/problemset/problem/1829/A)  
**Rating:** 800

### 1. The Problem Story

You are given string. The problem asks you to positions differing from 'codeforces'. The story can be reduced to the mathematical state: indicator [s[i]!=t[i]].

**Target / Goal**
- positions differing from 'codeforces'

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → string
- keep mathematical state → indicator [s[i]!=t[i]]
- target → positions differing from 'codeforces'

**Essential variables**

```text
Input:  string
State:  indicator [s[i]!=t[i]]
Target: positions differing from 'codeforces'
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: indicator [s[i]!=t[i]].
- Translate the decisive condition into: `sum indicators`.
- Simplify/recognize it as: `Hamming distance`.
- The required output is: positions differing from 'codeforces'.

**Mathematical form**

```text
sum indicators
    ↓
Hamming distance
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: indicator [s[i]!=t[i]].
2. Apply the condition `sum indicators`.
3. Use `Hamming distance` to obtain positions differing from 'codeforces'.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
indicator [s[i]!=t[i]]

At each step evaluate:
sum indicators

Then apply:
Hamming distance

Stop when the target is determined:
positions differing from 'codeforces'.
```

---

## CF 1873A — Short Sort

**Problem Link:** [Codeforces — CF 1873A — Short Sort](https://codeforces.com/problemset/problem/1873/A)  
**Rating:** 800

### 1. The Problem Story

You are given 3-char string. The problem asks you to can sort with <=1 swap. The story can be reduced to the mathematical state: target='abc'.

**Target / Goal**
- can sort with <=1 swap

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 3-char string
- keep mathematical state → target='abc'
- target → can sort with <=1 swap

**Essential variables**

```text
Input:  3-char string
State:  target='abc'
Target: can sort with <=1 swap
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: target='abc'.
- Translate the decisive condition into: `mismatch count 0 or 2`.
- Simplify/recognize it as: `compare permutations`.
- The required output is: can sort with <=1 swap.

**Mathematical form**

```text
mismatch count 0 or 2
    ↓
compare permutations
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: target='abc'.
2. Apply the condition `mismatch count 0 or 2`.
3. Use `compare permutations` to obtain can sort with <=1 swap.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
target='abc'

At each step evaluate:
mismatch count 0 or 2

Then apply:
compare permutations

Stop when the target is determined:
can sort with <=1 swap.
```

---

## CF 1729A — Two Elevators

**Problem Link:** [Codeforces — CF 1729A — Two Elevators](https://codeforces.com/problemset/problem/1729/A)  
**Rating:** 800

### 1. The Problem Story

You are given . The problem asks you to which elevator reaches floor1 sooner. The story can be reduced to the mathematical state: t1=a-1, t2=|b-c|+c-1.

**Target / Goal**
- which elevator reaches floor1 sooner

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 
- keep mathematical state → t1=a-1, t2=|b-c|+c-1
- target → which elevator reaches floor1 sooner

**Essential variables**

```text
Input:  
State:  t1=a-1, t2=|b-c|+c-1
Target: which elevator reaches floor1 sooner
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: t1=a-1, t2=|b-c|+c-1.
- Translate the decisive condition into: `compare times`.
- Simplify/recognize it as: `min comparison`.
- The required output is: which elevator reaches floor1 sooner.

**Mathematical form**

```text
compare times
    ↓
min comparison
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: t1=a-1, t2=|b-c|+c-1.
2. Apply the condition `compare times`.
3. Use `min comparison` to obtain which elevator reaches floor1 sooner.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
t1=a-1, t2=|b-c|+c-1

At each step evaluate:
compare times

Then apply:
min comparison

Stop when the target is determined:
which elevator reaches floor1 sooner.
```

---

## CF 1805A — We Need the Zero

**Problem Link:** [Codeforces — CF 1805A — We Need the Zero](https://codeforces.com/problemset/problem/1805/A)  
**Rating:** 900

### 1. The Problem Story

You are given array. The problem asks you to find x making xor transformed zero. The story can be reduced to the mathematical state: xor(a_i xor x).

**Target / Goal**
- find x making xor transformed zero

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → array
- keep mathematical state → xor(a_i xor x)
- target → find x making xor transformed zero

**Essential variables**

```text
Input:  array
State:  xor(a_i xor x)
Target: find x making xor transformed zero
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: xor(a_i xor x).
- Translate the decisive condition into: `parity of n controls x contribution`.
- Simplify/recognize it as: `derive xor equation`.
- The required output is: find x making xor transformed zero.

**Mathematical form**

```text
parity of n controls x contribution
    ↓
derive xor equation
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: xor(a_i xor x).
2. Apply the condition `parity of n controls x contribution`.
3. Use `derive xor equation` to obtain find x making xor transformed zero.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
xor(a_i xor x)

At each step evaluate:
parity of n controls x contribution

Then apply:
derive xor equation

Stop when the target is determined:
find x making xor transformed zero.
```

---

## CF 1858A — Buttons

**Problem Link:** [Codeforces — CF 1858A — Buttons](https://codeforces.com/problemset/problem/1858/A)  
**Rating:** 800

### 1. The Problem Story

Anna has a private buttons, Katie has b private buttons, and c buttons can be pressed by either player. They alternate turns starting with Anna; a used button disappears, and a player who cannot move loses. Determine the winner under optimal play.

**Target / Goal**
- winner with shared buttons

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- Anna → first player
- Katie → second player
- a → first player's private moves
- b → second player's private moves
- c → shared moves
- press a button → consume one available move

**Essential variables**

```text
Input:  
State:  shared moves alternate
Target: winner with shared buttons
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: shared moves alternate.
- Translate the decisive condition into: `parity of c decides who gets extra`.
- Simplify/recognize it as: `compare effective counts`.
- The required output is: winner with shared buttons.

**Mathematical form**

```text
parity of c decides who gets extra
    ↓
compare effective counts
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: shared moves alternate.
2. Apply the condition `parity of c decides who gets extra`.
3. Use `compare effective counts` to obtain winner with shared buttons.

### 5. Step 4: Quick Dry Run Example

```text
Example: a=1, b=2, c=3

Shared buttons are used alternately:
Anna gets 2 shared turns, Katie gets 1.

Effective moves:
Anna = a + 2 = 3
Katie = b + 1 = 3

Anna moves first. With equal effective move counts, Katie makes the last move.
Anna then has no move → Katie wins.

For odd c, Anna needs a ≥ b to win.
Here 1 ≥ 2 is false → Second wins.
```

---

## CF 1899A — Game with Integers

**Problem Link:** [Codeforces — CF 1899A — Game with Integers](https://codeforces.com/problemset/problem/1899/A)  
**Rating:** 800

### 1. The Problem Story

You are given . The problem asks you to winner under ±1 and divisibility by3. The story can be reduced to the mathematical state: positions mod3.

**Target / Goal**
- winner under ±1 and divisibility by3

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 
- keep mathematical state → positions mod3
- target → winner under ±1 and divisibility by3

**Essential variables**

```text
Input:  
State:  positions mod3
Target: winner under ±1 and divisibility by3
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: positions mod3.
- Translate the decisive condition into: `n%3==0 is losing/winning condition per rules`.
- Simplify/recognize it as: `reduce to residue`.
- The required output is: winner under ±1 and divisibility by3.

**Mathematical form**

```text
n%3==0 is losing/winning condition per rules
    ↓
reduce to residue
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: positions mod3.
2. Apply the condition `n%3==0 is losing/winning condition per rules`.
3. Use `reduce to residue` to obtain winner under ±1 and divisibility by3.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
positions mod3

At each step evaluate:
n%3==0 is losing/winning condition per rules

Then apply:
reduce to residue

Stop when the target is determined:
winner under ±1 and divisibility by3.
```

---

# Pattern 4 — Parity Modeling

## CF 4A — Watermelon

**Problem Link:** [Codeforces — CF 4A — Watermelon](https://codeforces.com/problemset/problem/4/A)  
**Rating:** 800

### 1. The Problem Story

A watermelon of weight w must be split into two positive pieces. Both pieces must have even weight. Determine whether such a split is possible.

**Target / Goal**
- split into two positive even parts

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → w
- keep mathematical state → w=a+b, a,b even >=2
- target → split into two positive even parts

**Essential variables**

```text
Input:  w
State:  w=a+b, a,b even >=2
Target: split into two positive even parts
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: w=a+b, a,b even >=2.
- Translate the decisive condition into: `w even and w>2`.
- Simplify/recognize it as: `parity + positivity`.
- The required output is: split into two positive even parts.

**Mathematical form**

```text
w even and w>2
    ↓
parity + positivity
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: w=a+b, a,b even >=2.
2. Apply the condition `w even and w>2`.
3. Use `parity + positivity` to obtain split into two positive even parts.

### 5. Step 4: Quick Dry Run Example

```text
Example: w=8

Need x+y=8 with x>0,y>0 and both even.
Choose 2+6=8.

Both pieces are positive and even.
Answer=YES.

For w=2, only 1+1 is possible, so NO.
```

---

## CF 1296A — Array with Odd Sum

**Problem Link:** [Codeforces — CF 1296A — Array with Odd Sum](https://codeforces.com/problemset/problem/1296/A)  
**Rating:** 800

### 1. The Problem Story

You may choose a subset of array elements whose sum must be odd. Determine whether the array contains a combination of odd/even elements that makes an odd sum possible.

**Target / Goal**
- whether required odd-sum selection exists

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → array
- keep mathematical state → sum odd iff odd count odd
- target → whether required odd-sum selection exists

**Essential variables**

```text
Input:  array
State:  sum odd iff odd count odd
Target: whether required odd-sum selection exists
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: sum odd iff odd count odd.
- Translate the decisive condition into: `reduce values to parity`.
- Simplify/recognize it as: `count odd/even`.
- The required output is: whether required odd-sum selection exists.

**Mathematical form**

```text
reduce values to parity
    ↓
count odd/even
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: sum odd iff odd count odd.
2. Apply the condition `reduce values to parity`.
3. Use `count odd/even` to obtain whether required odd-sum selection exists.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
sum odd iff odd count odd

At each step evaluate:
reduce values to parity

Then apply:
count odd/even

Stop when the target is determined:
whether required odd-sum selection exists.
```

---

## CF 1857A — Array Coloring

**Problem Link:** [Codeforces — CF 1857A — Array Coloring](https://codeforces.com/problemset/problem/1857/A)  
**Rating:** 800

### 1. The Problem Story

An array must be split into two groups so that the parity condition required by the problem is satisfied. The key information is how many odd elements exist.

**Target / Goal**
- whether can split into two groups with equal parity sums

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → array
- keep mathematical state → total sum must be even
- target → whether can split into two groups with equal parity sums

**Essential variables**

```text
Input:  array
State:  total sum must be even
Target: whether can split into two groups with equal parity sums
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: total sum must be even.
- Translate the decisive condition into: `sum%2=0`.
- Simplify/recognize it as: `parity invariant`.
- The required output is: whether can split into two groups with equal parity sums.

**Mathematical form**

```text
sum%2=0
    ↓
parity invariant
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: total sum must be even.
2. Apply the condition `sum%2=0`.
3. Use `parity invariant` to obtain whether can split into two groups with equal parity sums.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
total sum must be even

At each step evaluate:
sum%2=0

Then apply:
parity invariant

Stop when the target is determined:
whether can split into two groups with equal parity sums.
```

---

## CF 1834A — Unit Array

**Problem Link:** [Codeforces — CF 1834A — Unit Array](https://codeforces.com/problemset/problem/1834/A)  
**Rating:** 800

### 1. The Problem Story

You are given ±1 array. The problem asks you to minimum flips to satisfy sum>=0 and product=1. The story can be reduced to the mathematical state: product depends on #(-1) parity.

**Target / Goal**
- minimum flips to satisfy sum>=0 and product=1

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → ±1 array
- keep mathematical state → product depends on #(-1) parity
- target → minimum flips to satisfy sum>=0 and product=1

**Essential variables**

```text
Input:  ±1 array
State:  product depends on #(-1) parity
Target: minimum flips to satisfy sum>=0 and product=1
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: product depends on #(-1) parity.
- Translate the decisive condition into: `fix sum then parity`.
- Simplify/recognize it as: `count negatives`.
- The required output is: minimum flips to satisfy sum>=0 and product=1.

**Mathematical form**

```text
fix sum then parity
    ↓
count negatives
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: product depends on #(-1) parity.
2. Apply the condition `fix sum then parity`.
3. Use `count negatives` to obtain minimum flips to satisfy sum>=0 and product=1.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
product depends on #(-1) parity

At each step evaluate:
fix sum then parity

Then apply:
count negatives

Stop when the target is determined:
minimum flips to satisfy sum>=0 and product=1.
```

---

## CF 1367B — Even Array

**Problem Link:** [Codeforces — CF 1367B — Even Array](https://codeforces.com/problemset/problem/1367/B)  
**Rating:** 800

### 1. The Problem Story

You are given array. The problem asks you to minimum swaps so a\[i\]%2=i%2. The story can be reduced to the mathematical state: mismatches of two types must balance.

**Target / Goal**
- minimum swaps so a\[i\]%2=i%2

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → array
- keep mathematical state → mismatches of two types must balance
- target → minimum swaps so a\[i\]%2=i%2

**Essential variables**

```text
Input:  array
State:  mismatches of two types must balance
Target: minimum swaps so a\[i\]%2=i%2
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: mismatches of two types must balance.
- Translate the decisive condition into: `badEven=badOdd`.
- Simplify/recognize it as: `answer mismatches/2`.
- The required output is: minimum swaps so a\[i\]%2=i%2.

**Mathematical form**

```text
badEven=badOdd
    ↓
answer mismatches/2
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: mismatches of two types must balance.
2. Apply the condition `badEven=badOdd`.
3. Use `answer mismatches/2` to obtain minimum swaps so a\[i\]%2=i%2.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
mismatches of two types must balance

At each step evaluate:
badEven=badOdd

Then apply:
answer mismatches/2

Stop when the target is determined:
minimum swaps so a\[i\]%2=i%2.
```

---

## CF 1475A — Odd Divisor

**Problem Link:** [Codeforces — CF 1475A — Odd Divisor](https://codeforces.com/problemset/problem/1475/A)  
**Rating:** 900

### 1. The Problem Story

You are given . The problem asks you to has odd divisor >1. The story can be reduced to the mathematical state: n=2^k*m odd.

**Target / Goal**
- has odd divisor >1

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 
- keep mathematical state → n=2^k*m odd
- target → has odd divisor >1

**Essential variables**

```text
Input:  
State:  n=2^k*m odd
Target: has odd divisor >1
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: n=2^k*m odd.
- Translate the decisive condition into: `m>1`.
- Simplify/recognize it as: `not power of two`.
- The required output is: has odd divisor >1.

**Mathematical form**

```text
m>1
    ↓
not power of two
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: n=2^k*m odd.
2. Apply the condition `m>1`.
3. Use `not power of two` to obtain has odd divisor >1.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
n=2^k*m odd

At each step evaluate:
m>1

Then apply:
not power of two

Stop when the target is determined:
has odd divisor >1.
```

---

## CF 1669C — Odd/Even Increments

**Problem Link:** [Codeforces — CF 1669C — Odd/Even Increments](https://codeforces.com/problemset/problem/1669/C)  
**Rating:** 800

### 1. The Problem Story

You are given array. The problem asks you to can equalize via parity-constrained increments. The story can be reduced to the mathematical state: all elements need same parity class relation.

**Target / Goal**
- can equalize via parity-constrained increments

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → array
- keep mathematical state → all elements need same parity class relation
- target → can equalize via parity-constrained increments

**Essential variables**

```text
Input:  array
State:  all elements need same parity class relation
Target: can equalize via parity-constrained increments
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: all elements need same parity class relation.
- Translate the decisive condition into: `check parity consistency`.
- Simplify/recognize it as: `parity only`.
- The required output is: can equalize via parity-constrained increments.

**Mathematical form**

```text
check parity consistency
    ↓
parity only
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: all elements need same parity class relation.
2. Apply the condition `check parity consistency`.
3. Use `parity only` to obtain can equalize via parity-constrained increments.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
all elements need same parity class relation

At each step evaluate:
check parity consistency

Then apply:
parity only

Stop when the target is determined:
can equalize via parity-constrained increments.
```

---

## CF 1624A — Plus One on the Subset

**Problem Link:** [Codeforces — CF 1624A — Plus One on the Subset](https://codeforces.com/problemset/problem/1624/A)  
**Rating:** 800

### 1. The Problem Story

You are given array. The problem asks you to minimum operations to equalize by incrementing subset. The story can be reduced to the mathematical state: raise to max.

**Target / Goal**
- minimum operations to equalize by incrementing subset

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → array
- keep mathematical state → raise to max
- target → minimum operations to equalize by incrementing subset

**Essential variables**

```text
Input:  array
State:  raise to max
Target: minimum operations to equalize by incrementing subset
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: raise to max.
- Translate the decisive condition into: `answer=max-min`.
- Simplify/recognize it as: `range width`.
- The required output is: minimum operations to equalize by incrementing subset.

**Mathematical form**

```text
answer=max-min
    ↓
range width
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: raise to max.
2. Apply the condition `answer=max-min`.
3. Use `range width` to obtain minimum operations to equalize by incrementing subset.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
raise to max

At each step evaluate:
answer=max-min

Then apply:
range width

Stop when the target is determined:
minimum operations to equalize by incrementing subset.
```

---

## CF 1788A — One and Two

**Problem Link:** [Codeforces — CF 1788A — One and Two](https://codeforces.com/problemset/problem/1788/A)  
**Rating:** 800

### 1. The Problem Story

You are given 1/2 array. The problem asks you to split so products equal. The story can be reduced to the mathematical state: equal #twos on both sides.

**Target / Goal**
- split so products equal

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 1/2 array
- keep mathematical state → equal #twos on both sides
- target → split so products equal

**Essential variables**

```text
Input:  1/2 array
State:  equal #twos on both sides
Target: split so products equal
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: equal #twos on both sides.
- Translate the decisive condition into: `total twos even`.
- Simplify/recognize it as: `find half twos`.
- The required output is: split so products equal.

**Mathematical form**

```text
total twos even
    ↓
find half twos
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: equal #twos on both sides.
2. Apply the condition `total twos even`.
3. Use `find half twos` to obtain split so products equal.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
equal #twos on both sides

At each step evaluate:
total twos even

Then apply:
find half twos

Stop when the target is determined:
split so products equal.
```

---

## CF 1845A — Forbidden Integer

**Problem Link:** [Codeforces — CF 1845A — Forbidden Integer](https://codeforces.com/problemset/problem/1845/A)  
**Rating:** 800

### 1. The Problem Story

You are given n,k,x. The problem asks you to represent n as sum of 1..k excluding x. The story can be reduced to the mathematical state: choose repeated small allowed values.

**Target / Goal**
- represent n as sum of 1..k excluding x

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → n,k,x
- keep mathematical state → choose repeated small allowed values
- target → represent n as sum of 1..k excluding x

**Essential variables**

```text
Input:  n,k,x
State:  choose repeated small allowed values
Target: represent n as sum of 1..k excluding x
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: choose repeated small allowed values.
- Translate the decisive condition into: `cases x!=1, else 2/3`.
- Simplify/recognize it as: `construct feasibility`.
- The required output is: represent n as sum of 1..k excluding x.

**Mathematical form**

```text
cases x!=1, else 2/3
    ↓
construct feasibility
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: choose repeated small allowed values.
2. Apply the condition `cases x!=1, else 2/3`.
3. Use `construct feasibility` to obtain represent n as sum of 1..k excluding x.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
choose repeated small allowed values

At each step evaluate:
cases x!=1, else 2/3

Then apply:
construct feasibility

Stop when the target is determined:
represent n as sum of 1..k excluding x.
```

---

# Pattern 5 — Divisibility / GCD / LCM

## CF 1328A — Divisibility Problem

**Problem Link:** [Codeforces — CF 1328A — Divisibility Problem](https://codeforces.com/problemset/problem/1328/A)  
**Rating:** 800

### 1. The Problem Story

Given a and b, you may increase a by 1 per move. Find the minimum number of moves needed to make a divisible by b.

**Target / Goal**
- minimum add to make a divisible by b

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → a,b
- keep mathematical state → need a+x ≡0 mod b
- target → minimum add to make a divisible by b

**Essential variables**

```text
Input:  a,b
State:  need a+x ≡0 mod b
Target: minimum add to make a divisible by b
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: need a+x ≡0 mod b.
- Translate the decisive condition into: `x=(b-a%b)%b`.
- Simplify/recognize it as: `remainder complement`.
- The required output is: minimum add to make a divisible by b.

**Mathematical form**

```text
x=(b-a%b)%b
    ↓
remainder complement
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: need a+x ≡0 mod b.
2. Apply the condition `x=(b-a%b)%b`.
3. Use `remainder complement` to obtain minimum add to make a divisible by b.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
need a+x ≡0 mod b

At each step evaluate:
x=(b-a%b)%b

Then apply:
remainder complement

Stop when the target is determined:
minimum add to make a divisible by b.
```

---

## CF 1343A — Candies

**Problem Link:** [Codeforces — CF 1343A — Candies](https://codeforces.com/problemset/problem/1343/A)  
**Rating:** 900

### 1. The Problem Story

You are given . The problem asks you to find x where n=x(2^k-1). The story can be reduced to the mathematical state: geometric sum factor.

**Target / Goal**
- find x where n=x(2^k-1)

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 
- keep mathematical state → geometric sum factor
- target → find x where n=x(2^k-1)

**Essential variables**

```text
Input:  
State:  geometric sum factor
Target: find x where n=x(2^k-1)
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: geometric sum factor.
- Translate the decisive condition into: `x=n/(2^k-1) if divisible`.
- Simplify/recognize it as: `test k`.
- The required output is: find x where n=x(2^k-1).

**Mathematical form**

```text
x=n/(2^k-1) if divisible
    ↓
test k
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: geometric sum factor.
2. Apply the condition `x=n/(2^k-1) if divisible`.
3. Use `test k` to obtain find x where n=x(2^k-1).

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
geometric sum factor

At each step evaluate:
x=n/(2^k-1) if divisible

Then apply:
test k

Stop when the target is determined:
find x where n=x(2^k-1).
```

---

## CF 1370A — Maximum GCD

**Problem Link:** [Codeforces — CF 1370A — Maximum GCD](https://codeforces.com/problemset/problem/1370/A)  
**Rating:** 800

### 1. The Problem Story

You are given . The problem asks you to maximize gcd(a,b), a+b=n. The story can be reduced to the mathematical state: gcd<=floor(n/2).

**Target / Goal**
- maximize gcd(a,b), a+b=n

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 
- keep mathematical state → gcd<=floor(n/2)
- target → maximize gcd(a,b), a+b=n

**Essential variables**

```text
Input:  
State:  gcd<=floor(n/2)
Target: maximize gcd(a,b), a+b=n
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: gcd<=floor(n/2).
- Translate the decisive condition into: `choose floor(n/2)`.
- Simplify/recognize it as: `tight bound`.
- The required output is: maximize gcd(a,b), a+b=n.

**Mathematical form**

```text
choose floor(n/2)
    ↓
tight bound
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: gcd<=floor(n/2).
2. Apply the condition `choose floor(n/2)`.
3. Use `tight bound` to obtain maximize gcd(a,b), a+b=n.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
gcd<=floor(n/2)

At each step evaluate:
choose floor(n/2)

Then apply:
tight bound

Stop when the target is determined:
maximize gcd(a,b), a+b=n.
```

---

## CF 1829C — Mr. Perfectly Fine

**Problem Link:** [Codeforces — CF 1829C — Mr. Perfectly Fine](https://codeforces.com/problemset/problem/1829/C)  
**Rating:** 800

### 1. The Problem Story

You are given items. The problem asks you to minimum time covering skills 1 and2. The story can be reduced to the mathematical state: skill masks 01,10,11.

**Target / Goal**
- minimum time covering skills 1 and2

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → items
- keep mathematical state → skill masks 01,10,11
- target → minimum time covering skills 1 and2

**Essential variables**

```text
Input:  items
State:  skill masks 01,10,11
Target: minimum time covering skills 1 and2
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: skill masks 01,10,11.
- Translate the decisive condition into: `min(cost11,cost01+cost10)`.
- Simplify/recognize it as: `coverage states`.
- The required output is: minimum time covering skills 1 and2.

**Mathematical form**

```text
min(cost11,cost01+cost10)
    ↓
coverage states
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: skill masks 01,10,11.
2. Apply the condition `min(cost11,cost01+cost10)`.
3. Use `coverage states` to obtain minimum time covering skills 1 and2.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
skill masks 01,10,11

At each step evaluate:
min(cost11,cost01+cost10)

Then apply:
coverage states

Stop when the target is determined:
minimum time covering skills 1 and2.
```

---

## CF 1618A — Polycarp and Sums of Subsequences

**Problem Link:** [Codeforces — CF 1618A — Polycarp and Sums of Subsequences](https://codeforces.com/problemset/problem/1618/A)  
**Rating:** 800

### 1. The Problem Story

You are given 7 subset sums. The problem asks you to recover a,b,c. The story can be reduced to the mathematical state: smallest=a,b and total largest=a+b+c.

**Target / Goal**
- recover a,b,c

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 7 subset sums
- keep mathematical state → smallest=a,b and total largest=a+b+c
- target → recover a,b,c

**Essential variables**

```text
Input:  7 subset sums
State:  smallest=a,b and total largest=a+b+c
Target: recover a,b,c
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: smallest=a,b and total largest=a+b+c.
- Translate the decisive condition into: `c=largest-a-b`.
- Simplify/recognize it as: `sorted sums`.
- The required output is: recover a,b,c.

**Mathematical form**

```text
c=largest-a-b
    ↓
sorted sums
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: smallest=a,b and total largest=a+b+c.
2. Apply the condition `c=largest-a-b`.
3. Use `sorted sums` to obtain recover a,b,c.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
smallest=a,b and total largest=a+b+c

At each step evaluate:
c=largest-a-b

Then apply:
sorted sums

Stop when the target is determined:
recover a,b,c.
```

---

## CF 160A — Twins

**Problem Link:** [Codeforces — CF 160A — Twins](https://codeforces.com/problemset/problem/160/A)  
**Rating:** 900

### 1. The Problem Story

You are given coins. The problem asks you to minimum coins with sum > remaining. The story can be reduced to the mathematical state: chosen > total-chosen.

**Target / Goal**
- minimum coins with sum > remaining

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → coins
- keep mathematical state → chosen > total-chosen
- target → minimum coins with sum > remaining

**Essential variables**

```text
Input:  coins
State:  chosen > total-chosen
Target: minimum coins with sum > remaining
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: chosen > total-chosen.
- Translate the decisive condition into: `2*chosen>total`.
- Simplify/recognize it as: `sort descending`.
- The required output is: minimum coins with sum > remaining.

**Mathematical form**

```text
2*chosen>total
    ↓
sort descending
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: chosen > total-chosen.
2. Apply the condition `2*chosen>total`.
3. Use `sort descending` to obtain minimum coins with sum > remaining.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
chosen > total-chosen

At each step evaluate:
2*chosen>total

Then apply:
sort descending

Stop when the target is determined:
minimum coins with sum > remaining.
```

---

## CF 1475B — New Year's Number

**Problem Link:** [Codeforces — CF 1475B — New Year's Number](https://codeforces.com/problemset/problem/1475/B)  
**Rating:** 900

### 1. The Problem Story

You are given . The problem asks you to n=2020a+2021b?. The story can be reduced to the mathematical state: 2021=2020+1.

**Target / Goal**
- n=2020a+2021b?

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 
- keep mathematical state → 2021=2020+1
- target → n=2020a+2021b?

**Essential variables**

```text
Input:  
State:  2021=2020+1
Target: n=2020a+2021b?
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: 2021=2020+1.
- Translate the decisive condition into: `choose b=n%2020 then test`.
- Simplify/recognize it as: `linear diophantine shortcut`.
- The required output is: n=2020a+2021b?.

**Mathematical form**

```text
choose b=n%2020 then test
    ↓
linear diophantine shortcut
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: 2021=2020+1.
2. Apply the condition `choose b=n%2020 then test`.
3. Use `linear diophantine shortcut` to obtain n=2020a+2021b?.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
2021=2020+1

At each step evaluate:
choose b=n%2020 then test

Then apply:
linear diophantine shortcut

Stop when the target is determined:
n=2020a+2021b?.
```

---

## CF 1593A — Elections

**Problem Link:** [Codeforces — CF 1593A — Elections](https://codeforces.com/problemset/problem/1593/A)  
**Rating:** 800

### 1. The Problem Story

You are given . The problem asks you to increments to become strictly largest. The story can be reduced to the mathematical state: need x+inc>max(other).

**Target / Goal**
- increments to become strictly largest

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 
- keep mathematical state → need x+inc>max(other)
- target → increments to become strictly largest

**Essential variables**

```text
Input:  
State:  need x+inc>max(other)
Target: increments to become strictly largest
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: need x+inc>max(other).
- Translate the decisive condition into: `inc=max(0,M-x+1), except unique max`.
- Simplify/recognize it as: `per candidate bound`.
- The required output is: increments to become strictly largest.

**Mathematical form**

```text
inc=max(0,M-x+1), except unique max
    ↓
per candidate bound
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: need x+inc>max(other).
2. Apply the condition `inc=max(0,M-x+1), except unique max`.
3. Use `per candidate bound` to obtain increments to become strictly largest.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
need x+inc>max(other)

At each step evaluate:
inc=max(0,M-x+1), except unique max

Then apply:
per candidate bound

Stop when the target is determined:
increments to become strictly largest.
```

---

## CF 1829B — Blank Space

**Problem Link:** [Codeforces — CF 1829B — Blank Space](https://codeforces.com/problemset/problem/1829/B)  
**Rating:** 800

### 1. The Problem Story

You are given binary array. The problem asks you to longest consecutive zeros. The story can be reduced to the mathematical state: state current run.

**Target / Goal**
- longest consecutive zeros

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → binary array
- keep mathematical state → state current run
- target → longest consecutive zeros

**Essential variables**

```text
Input:  binary array
State:  state current run
Target: longest consecutive zeros
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: state current run.
- Translate the decisive condition into: `max over runs`.
- Simplify/recognize it as: `scan`.
- The required output is: longest consecutive zeros.

**Mathematical form**

```text
max over runs
    ↓
scan
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: state current run.
2. Apply the condition `max over runs`.
3. Use `scan` to obtain longest consecutive zeros.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
state current run

At each step evaluate:
max over runs

Then apply:
scan

Stop when the target is determined:
longest consecutive zeros.
```

---

## CF 1877A — Goals of Victory

**Problem Link:** [Codeforces — CF 1877A — Goals of Victory](https://codeforces.com/problemset/problem/1877/A)  
**Rating:** 800

### 1. The Problem Story

You are given n-1 values. The problem asks you to missing value so total sum zero. The story can be reduced to the mathematical state: x+sum=0.

**Target / Goal**
- missing value so total sum zero

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → n-1 values
- keep mathematical state → x+sum=0
- target → missing value so total sum zero

**Essential variables**

```text
Input:  n-1 values
State:  x+sum=0
Target: missing value so total sum zero
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: x+sum=0.
- Translate the decisive condition into: `x=-sum`.
- Simplify/recognize it as: `equation`.
- The required output is: missing value so total sum zero.

**Mathematical form**

```text
x=-sum
    ↓
equation
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: x+sum=0.
2. Apply the condition `x=-sum`.
3. Use `equation` to obtain missing value so total sum zero.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
x+sum=0

At each step evaluate:
x=-sum

Then apply:
equation

Stop when the target is determined:
missing value so total sum zero.
```

---

# Pattern 6 — Modulo / Cyclic Modeling

## CF 116A — Tram

**Problem Link:** [Codeforces — CF 116A — Tram](https://codeforces.com/problemset/problem/116/A)  
**Rating:** 800

### 1. The Problem Story

You are given stops. The problem asks you to minimum tram capacity. The story can be reduced to the mathematical state: current += enter-exit.

**Target / Goal**
- minimum tram capacity

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → stops
- keep mathematical state → current += enter-exit
- target → minimum tram capacity

**Essential variables**

```text
Input:  stops
State:  current += enter-exit
Target: minimum tram capacity
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: current += enter-exit.
- Translate the decisive condition into: `max prefix occupancy`.
- Simplify/recognize it as: `running state`.
- The required output is: minimum tram capacity.

**Mathematical form**

```text
max prefix occupancy
    ↓
running state
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: current += enter-exit.
2. Apply the condition `max prefix occupancy`.
3. Use `running state` to obtain minimum tram capacity.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
current += enter-exit

At each step evaluate:
max prefix occupancy

Then apply:
running state

Stop when the target is determined:
minimum tram capacity.
```

---

## CF 266A — Stones on the Table

**Problem Link:** [Codeforces — CF 266A — Stones on the Table](https://codeforces.com/problemset/problem/266/A)  
**Rating:** 800

### 1. The Problem Story

You are given string. The problem asks you to minimum removals so adjacent colors differ. The story can be reduced to the mathematical state: remove one from each equal adjacency.

**Target / Goal**
- minimum removals so adjacent colors differ

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → string
- keep mathematical state → remove one from each equal adjacency
- target → minimum removals so adjacent colors differ

**Essential variables**

```text
Input:  string
State:  remove one from each equal adjacency
Target: minimum removals so adjacent colors differ
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: remove one from each equal adjacency.
- Translate the decisive condition into: `count s[i]==s[i-1]`.
- Simplify/recognize it as: `local contribution`.
- The required output is: minimum removals so adjacent colors differ.

**Mathematical form**

```text
count s[i]==s[i-1]
    ↓
local contribution
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: remove one from each equal adjacency.
2. Apply the condition `count s[i]==s[i-1]`.
3. Use `local contribution` to obtain minimum removals so adjacent colors differ.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
remove one from each equal adjacency

At each step evaluate:
count s[i]==s[i-1]

Then apply:
local contribution

Stop when the target is determined:
minimum removals so adjacent colors differ.
```

---

## CF 228A — Is your horseshoe on the other hoof?

**Problem Link:** [Codeforces — CF 228A — Is your horseshoe on the other hoof?](https://codeforces.com/problemset/problem/228/A)  
**Rating:** 800

### 1. The Problem Story

You are given 4 colors. The problem asks you to minimum replacements for distinct. The story can be reduced to the mathematical state: 4-distinctCount.

**Target / Goal**
- minimum replacements for distinct

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 4 colors
- keep mathematical state → 4-distinctCount
- target → minimum replacements for distinct

**Essential variables**

```text
Input:  4 colors
State:  4-distinctCount
Target: minimum replacements for distinct
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: 4-distinctCount.
- Translate the decisive condition into: `set size`.
- Simplify/recognize it as: `duplicates`.
- The required output is: minimum replacements for distinct.

**Mathematical form**

```text
set size
    ↓
duplicates
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: 4-distinctCount.
2. Apply the condition `set size`.
3. Use `duplicates` to obtain minimum replacements for distinct.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
4-distinctCount

At each step evaluate:
set size

Then apply:
duplicates

Stop when the target is determined:
minimum replacements for distinct.
```

---

## CF 443A — Anton and Letters

**Problem Link:** [Codeforces — CF 443A — Anton and Letters](https://codeforces.com/problemset/problem/443/A)  
**Rating:** 800

### 1. The Problem Story

You are given formatted string. The problem asks you to number distinct letters. The story can be reduced to the mathematical state: extract lowercase chars.

**Target / Goal**
- number distinct letters

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → formatted string
- keep mathematical state → extract lowercase chars
- target → number distinct letters

**Essential variables**

```text
Input:  formatted string
State:  extract lowercase chars
Target: number distinct letters
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: extract lowercase chars.
- Translate the decisive condition into: `set cardinality`.
- Simplify/recognize it as: `distinct count`.
- The required output is: number distinct letters.

**Mathematical form**

```text
set cardinality
    ↓
distinct count
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: extract lowercase chars.
2. Apply the condition `set cardinality`.
3. Use `distinct count` to obtain number distinct letters.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
extract lowercase chars

At each step evaluate:
set cardinality

Then apply:
distinct count

Stop when the target is determined:
number distinct letters.
```

---

## CF 59A — Word

**Problem Link:** [Codeforces — CF 59A — Word](https://codeforces.com/problemset/problem/59/A)  
**Rating:** 800

### 1. The Problem Story

You are given string. The problem asks you to convert based on upper/lower majority. The story can be reduced to the mathematical state: count uppercase vs lowercase.

**Target / Goal**
- convert based on upper/lower majority

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → string
- keep mathematical state → count uppercase vs lowercase
- target → convert based on upper/lower majority

**Essential variables**

```text
Input:  string
State:  count uppercase vs lowercase
Target: convert based on upper/lower majority
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: count uppercase vs lowercase.
- Translate the decisive condition into: `choose case`.
- Simplify/recognize it as: `frequency comparison`.
- The required output is: convert based on upper/lower majority.

**Mathematical form**

```text
choose case
    ↓
frequency comparison
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: count uppercase vs lowercase.
2. Apply the condition `choose case`.
3. Use `frequency comparison` to obtain convert based on upper/lower majority.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
count uppercase vs lowercase

At each step evaluate:
choose case

Then apply:
frequency comparison

Stop when the target is determined:
convert based on upper/lower majority.
```

---

## CF 236A — Boy or Girl

**Problem Link:** [Codeforces — CF 236A — Boy or Girl](https://codeforces.com/problemset/problem/236/A)  
**Rating:** 800

### 1. The Problem Story

You are given username. The problem asks you to output based on distinct char count parity. The story can be reduced to the mathematical state: d=|set(chars)|.

**Target / Goal**
- output based on distinct char count parity

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → username
- keep mathematical state → d=|set(chars)|
- target → output based on distinct char count parity

**Essential variables**

```text
Input:  username
State:  d=|set(chars)|
Target: output based on distinct char count parity
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: d=|set(chars)|.
- Translate the decisive condition into: `d%2`.
- Simplify/recognize it as: `parity of distinct count`.
- The required output is: output based on distinct char count parity.

**Mathematical form**

```text
d%2
    ↓
parity of distinct count
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: d=|set(chars)|.
2. Apply the condition `d%2`.
3. Use `parity of distinct count` to obtain output based on distinct char count parity.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
d=|set(chars)|

At each step evaluate:
d%2

Then apply:
parity of distinct count

Stop when the target is determined:
output based on distinct char count parity.
```

---

## CF 785A — Anton and Polyhedrons

**Problem Link:** [Codeforces — CF 785A — Anton and Polyhedrons](https://codeforces.com/problemset/problem/785/A)  
**Rating:** 800

### 1. The Problem Story

You are given names. The problem asks you to total faces. The story can be reduced to the mathematical state: name→constant.

**Target / Goal**
- total faces

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → names
- keep mathematical state → name→constant
- target → total faces

**Essential variables**

```text
Input:  names
State:  name→constant
Target: total faces
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: name→constant.
- Translate the decisive condition into: `sum contributions`.
- Simplify/recognize it as: `lookup`.
- The required output is: total faces.

**Mathematical form**

```text
sum contributions
    ↓
lookup
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: name→constant.
2. Apply the condition `sum contributions`.
3. Use `lookup` to obtain total faces.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
name→constant

At each step evaluate:
sum contributions

Then apply:
lookup

Stop when the target is determined:
total faces.
```

---

## CF 703A — Mishka and Game

**Problem Link:** [Codeforces — CF 703A — Mishka and Game](https://codeforces.com/problemset/problem/703/A)  
**Rating:** 800

### 1. The Problem Story

You are given round scores. The problem asks you to winner by more round wins. The story can be reduced to the mathematical state: count a>b and a<b.

**Target / Goal**
- winner by more round wins

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → round scores
- keep mathematical state → count a>b and a<b
- target → winner by more round wins

**Essential variables**

```text
Input:  round scores
State:  count a>b and a<b
Target: winner by more round wins
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: count a>b and a<b.
- Translate the decisive condition into: `compare counts`.
- Simplify/recognize it as: `two counters`.
- The required output is: winner by more round wins.

**Mathematical form**

```text
compare counts
    ↓
two counters
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: count a>b and a<b.
2. Apply the condition `compare counts`.
3. Use `two counters` to obtain winner by more round wins.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
count a>b and a<b

At each step evaluate:
compare counts

Then apply:
two counters

Stop when the target is determined:
winner by more round wins.
```

---

## CF 734B — Anton and Digits

**Problem Link:** [Codeforces — CF 734B — Anton and Digits](https://codeforces.com/problemset/problem/734/B)  
**Rating:** 800

### 1. The Problem Story

You are given counts 2,3,5,6. The problem asks you to maximize sum using 256 and32. The story can be reduced to the mathematical state: make 256 first because larger.

**Target / Goal**
- maximize sum using 256 and32

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → counts 2,3,5,6
- keep mathematical state → make 256 first because larger
- target → maximize sum using 256 and32

**Essential variables**

```text
Input:  counts 2,3,5,6
State:  make 256 first because larger
Target: maximize sum using 256 and32
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: make 256 first because larger.
- Translate the decisive condition into: `x=min(2,5,6), y=min(2left,3)`.
- Simplify/recognize it as: `resource allocation`.
- The required output is: maximize sum using 256 and32.

**Mathematical form**

```text
x=min(2,5,6), y=min(2left,3)
    ↓
resource allocation
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: make 256 first because larger.
2. Apply the condition `x=min(2,5,6), y=min(2left,3)`.
3. Use `resource allocation` to obtain maximize sum using 256 and32.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
make 256 first because larger

At each step evaluate:
x=min(2,5,6), y=min(2left,3)

Then apply:
resource allocation

Stop when the target is determined:
maximize sum using 256 and32.
```

---

## CF 1097A — Gennady the Card Game

**Problem Link:** [Codeforces — CF 1097A — Gennady the Card Game](https://codeforces.com/problemset/problem/1097/A)  
**Rating:** 800

### 1. The Problem Story

You are given card + five cards. The problem asks you to whether rank or suit matches. The story can be reduced to the mathematical state: exists same first or second char.

**Target / Goal**
- whether rank or suit matches

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → card + five cards
- keep mathematical state → exists same first or second char
- target → whether rank or suit matches

**Essential variables**

```text
Input:  card + five cards
State:  exists same first or second char
Target: whether rank or suit matches
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: exists same first or second char.
- Translate the decisive condition into: `OR condition`.
- Simplify/recognize it as: `scan`.
- The required output is: whether rank or suit matches.

**Mathematical form**

```text
OR condition
    ↓
scan
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: exists same first or second char.
2. Apply the condition `OR condition`.
3. Use `scan` to obtain whether rank or suit matches.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
exists same first or second char

At each step evaluate:
OR condition

Then apply:
scan

Stop when the target is determined:
whether rank or suit matches.
```

---

# Pattern 7 — Counting / Frequency / Pairs

## CF 1520D — Same Differences

**Problem Link:** [Codeforces — CF 1520D — Same Differences](https://codeforces.com/problemset/problem/1520/D)  
**Rating:** 1200

### 1. The Problem Story

Count index pairs i<j satisfying a[j]-a[i]=j-i. Rearranging the equation turns each index into a key, so the task becomes counting equal transformed keys.

**Target / Goal**
- count i<j with a\[j\]-a\[i\]=j-i

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- array positions → indices i,j
- pair condition → equation
- a[i]-i → transformed key

**Essential variables**

```text
Input:  array
State:  a[j]-j=a[i]-i
Target: count i<j with a\[j\]-a\[i\]=j-i
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: a[j]-j=a[i]-i.
- Translate the decisive condition into: `key=a[i]-i`.
- Simplify/recognize it as: `equal-key pairs`.
- The required output is: count i<j with a\[j\]-a\[i\]=j-i.

**Mathematical form**

```text
key=a[i]-i
    ↓
equal-key pairs
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: a[j]-j=a[i]-i.
2. Apply the condition `key=a[i]-i`.
3. Use `equal-key pairs` to obtain count i<j with a\[j\]-a\[i\]=j-i.

### 5. Step 4: Quick Dry Run Example

```text
Example: a=[3,4,4,7], using 1-based indices

key[i]=a[i]-i:
i=1 → 3-1=2
i=2 → 4-2=2
i=3 → 4-3=1
i=4 → 7-4=3

key 2 appears twice → C(2,2)=1 valid pair.
Answer=1.
```

---

## CF 1538C — Challenging Cliffs / Number of Pairs

**Problem Link:** [Codeforces — CF 1538C — Challenging Cliffs / Number of Pairs](https://codeforces.com/problemset/problem/1538/C)  
**Rating:** 1300

### 1. The Problem Story

You are given array,l,r. The problem asks you to count pairs with sum in \[l,r\]. The story can be reduced to the mathematical state: count<=r - count<l.

**Target / Goal**
- count pairs with sum in \[l,r\]

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → array,l,r
- keep mathematical state → count<=r - count<l
- target → count pairs with sum in \[l,r\]

**Essential variables**

```text
Input:  array,l,r
State:  count<=r - count<l
Target: count pairs with sum in \[l,r\]
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: count<=r - count<l.
- Translate the decisive condition into: `sorted pair bound`.
- Simplify/recognize it as: `two pointers`.
- The required output is: count pairs with sum in \[l,r\].

**Mathematical form**

```text
sorted pair bound
    ↓
two pointers
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: count<=r - count<l.
2. Apply the condition `sorted pair bound`.
3. Use `two pointers` to obtain count pairs with sum in \[l,r\].

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
count<=r - count<l

At each step evaluate:
sorted pair bound

Then apply:
two pointers

Stop when the target is determined:
count pairs with sum in \[l,r\].
```

---

## CF 1669B — Triple

**Problem Link:** [Codeforces — CF 1669B — Triple](https://codeforces.com/problemset/problem/1669/B)  
**Rating:** 800

### 1. The Problem Story

You are given array. The problem asks you to find value occurring >=3. The story can be reduced to the mathematical state: freq[x]>=3.

**Target / Goal**
- find value occurring >=3

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → array
- keep mathematical state → freq[x]>=3
- target → find value occurring >=3

**Essential variables**

```text
Input:  array
State:  freq[x]>=3
Target: find value occurring >=3
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: freq[x]>=3.
- Translate the decisive condition into: `frequency threshold`.
- Simplify/recognize it as: `count`.
- The required output is: find value occurring >=3.

**Mathematical form**

```text
frequency threshold
    ↓
count
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: freq[x]>=3.
2. Apply the condition `frequency threshold`.
3. Use `count` to obtain find value occurring >=3.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
freq[x]>=3

At each step evaluate:
frequency threshold

Then apply:
count

Stop when the target is determined:
find value occurring >=3.
```

---

## CF 1742C — Stripes

**Problem Link:** [Codeforces — CF 1742C — Stripes](https://codeforces.com/problemset/problem/1742/C)  
**Rating:** 800

### 1. The Problem Story

You are given 8x8 grid. The problem asks you to determine last full stripe color. The story can be reduced to the mathematical state: full row of R is decisive.

**Target / Goal**
- determine last full stripe color

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 8x8 grid
- keep mathematical state → full row of R is decisive
- target → determine last full stripe color

**Essential variables**

```text
Input:  8x8 grid
State:  full row of R is decisive
Target: determine last full stripe color
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: full row of R is decisive.
- Translate the decisive condition into: `scan rows`.
- Simplify/recognize it as: `existence`.
- The required output is: determine last full stripe color.

**Mathematical form**

```text
scan rows
    ↓
existence
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: full row of R is decisive.
2. Apply the condition `scan rows`.
3. Use `existence` to obtain determine last full stripe color.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
full row of R is decisive

At each step evaluate:
scan rows

Then apply:
existence

Stop when the target is determined:
determine last full stripe color.
```

---

## CF 1791B — Following Directions

**Problem Link:** [Codeforces — CF 1791B — Following Directions](https://codeforces.com/problemset/problem/1791/B)  
**Rating:** 800

### 1. The Problem Story

You are given moves. The problem asks you to whether path visits (1,1). The story can be reduced to the mathematical state: update x,y per char.

**Target / Goal**
- whether path visits (1,1)

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → moves
- keep mathematical state → update x,y per char
- target → whether path visits (1,1)

**Essential variables**

```text
Input:  moves
State:  update x,y per char
Target: whether path visits (1,1)
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: update x,y per char.
- Translate the decisive condition into: `∃prefix=(1,1)`.
- Simplify/recognize it as: `prefix state`.
- The required output is: whether path visits (1,1).

**Mathematical form**

```text
∃prefix=(1,1)
    ↓
prefix state
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: update x,y per char.
2. Apply the condition `∃prefix=(1,1)`.
3. Use `prefix state` to obtain whether path visits (1,1).

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
update x,y per char

At each step evaluate:
∃prefix=(1,1)

Then apply:
prefix state

Stop when the target is determined:
whether path visits (1,1).
```

---

## CF 1703B — ICPC Balloons

**Problem Link:** [Codeforces — CF 1703B — ICPC Balloons](https://codeforces.com/problemset/problem/1703/B)  
**Rating:** 800

### 1. The Problem Story

You are given string. The problem asks you to score first occurrence differently. The story can be reduced to the mathematical state: first char contributes2 else1.

**Target / Goal**
- score first occurrence differently

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → string
- keep mathematical state → first char contributes2 else1
- target → score first occurrence differently

**Essential variables**

```text
Input:  string
State:  first char contributes2 else1
Target: score first occurrence differently
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: first char contributes2 else1.
- Translate the decisive condition into: `seen set`.
- Simplify/recognize it as: `contribution`.
- The required output is: score first occurrence differently.

**Mathematical form**

```text
seen set
    ↓
contribution
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: first char contributes2 else1.
2. Apply the condition `seen set`.
3. Use `contribution` to obtain score first occurrence differently.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
first char contributes2 else1

At each step evaluate:
seen set

Then apply:
contribution

Stop when the target is determined:
score first occurrence differently.
```

---

## CF 1722A — Spell Check

**Problem Link:** [Codeforces — CF 1722A — Spell Check](https://codeforces.com/problemset/problem/1722/A)  
**Rating:** 800

### 1. The Problem Story

You are given string. The problem asks you to whether permutation equals TimUR. The story can be reduced to the mathematical state: same multiset as 'Timur'.

**Target / Goal**
- whether permutation equals TimUR

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → string
- keep mathematical state → same multiset as 'Timur'
- target → whether permutation equals TimUR

**Essential variables**

```text
Input:  string
State:  same multiset as 'Timur'
Target: whether permutation equals TimUR
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: same multiset as 'Timur'.
- Translate the decisive condition into: `sort or counts`.
- Simplify/recognize it as: `canonical form`.
- The required output is: whether permutation equals TimUR.

**Mathematical form**

```text
sort or counts
    ↓
canonical form
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: same multiset as 'Timur'.
2. Apply the condition `sort or counts`.
3. Use `canonical form` to obtain whether permutation equals TimUR.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
same multiset as 'Timur'

At each step evaluate:
sort or counts

Then apply:
canonical form

Stop when the target is determined:
whether permutation equals TimUR.
```

---

## CF 1791C — Prepend and Append

**Problem Link:** [Codeforces — CF 1791C — Prepend and Append](https://codeforces.com/problemset/problem/1791/C)  
**Rating:** 800

### 1. The Problem Story

You are given binary string. The problem asks you to remove unequal ends. The story can be reduced to the mathematical state: while l<r and s[l]!=s[r].

**Target / Goal**
- remove unequal ends

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → binary string
- keep mathematical state → while l<r and s[l]!=s[r]
- target → remove unequal ends

**Essential variables**

```text
Input:  binary string
State:  while l<r and s[l]!=s[r]
Target: remove unequal ends
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: while l<r and s[l]!=s[r].
- Translate the decisive condition into: `remaining length`.
- Simplify/recognize it as: `two pointers`.
- The required output is: remove unequal ends.

**Mathematical form**

```text
remaining length
    ↓
two pointers
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: while l<r and s[l]!=s[r].
2. Apply the condition `remaining length`.
3. Use `two pointers` to obtain remove unequal ends.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
while l<r and s[l]!=s[r]

At each step evaluate:
remaining length

Then apply:
two pointers

Stop when the target is determined:
remove unequal ends.
```

---

## CF 1829D — Gold Rush

**Problem Link:** [Codeforces — CF 1829D — Gold Rush](https://codeforces.com/problemset/problem/1829/D)  
**Rating:** 1000

### 1. The Problem Story

You are given n,m. The problem asks you to can reach m by splitting x into x/3 and2x/3. The story can be reduced to the mathematical state: only split divisible by3.

**Target / Goal**
- can reach m by splitting x into x/3 and2x/3

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → n,m
- keep mathematical state → only split divisible by3
- target → can reach m by splitting x into x/3 and2x/3

**Essential variables**

```text
Input:  n,m
State:  only split divisible by3
Target: can reach m by splitting x into x/3 and2x/3
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: only split divisible by3.
- Translate the decisive condition into: `DFS on decreasing states`.
- Simplify/recognize it as: `reachability`.
- The required output is: can reach m by splitting x into x/3 and2x/3.

**Mathematical form**

```text
DFS on decreasing states
    ↓
reachability
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: only split divisible by3.
2. Apply the condition `DFS on decreasing states`.
3. Use `reachability` to obtain can reach m by splitting x into x/3 and2x/3.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
only split divisible by3

At each step evaluate:
DFS on decreasing states

Then apply:
reachability

Stop when the target is determined:
can reach m by splitting x into x/3 and2x/3.
```

---

## CF 1878B — Aleksa and Stack

**Problem Link:** [Codeforces — CF 1878B — Aleksa and Stack](https://codeforces.com/problemset/problem/1878/B)  
**Rating:** 800

### 1. The Problem Story

You are given . The problem asks you to construct sequence satisfying divisibility condition. The story can be reduced to the mathematical state: choose simple arithmetic sequence.

**Target / Goal**
- construct sequence satisfying divisibility condition

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 
- keep mathematical state → choose simple arithmetic sequence
- target → construct sequence satisfying divisibility condition

**Essential variables**

```text
Input:  
State:  choose simple arithmetic sequence
Target: construct sequence satisfying divisibility condition
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: choose simple arithmetic sequence.
- Translate the decisive condition into: `constant gap avoids divisibility`.
- Simplify/recognize it as: `construct`.
- The required output is: construct sequence satisfying divisibility condition.

**Mathematical form**

```text
constant gap avoids divisibility
    ↓
construct
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: choose simple arithmetic sequence.
2. Apply the condition `constant gap avoids divisibility`.
3. Use `construct` to obtain construct sequence satisfying divisibility condition.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
choose simple arithmetic sequence

At each step evaluate:
constant gap avoids divisibility

Then apply:
construct

Stop when the target is determined:
construct sequence satisfying divisibility condition.
```

---

# Pattern 8 — Operation → Delta → Invariant

## CF 1538B — Friends and Candies

**Problem Link:** [Codeforces — CF 1538B — Friends and Candies](https://codeforces.com/problemset/problem/1538/B)  
**Rating:** 800

### 1. The Problem Story

n friends have some numbers of candies. In one move candies can be redistributed; determine the minimum number of friends who must give candies so everyone can end with the average, or -1 if equal distribution is impossible.

**Target / Goal**
- equalize while preserving sum

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → array
- keep mathematical state → S=n*x
- target → equalize while preserving sum

**Essential variables**

```text
Input:  array
State:  S=n*x
Target: equalize while preserving sum
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: S=n*x.
- Translate the decisive condition into: `S%n=0`.
- Simplify/recognize it as: `average invariant`.
- The required output is: equalize while preserving sum.

**Mathematical form**

```text
S%n=0
    ↓
average invariant
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: S=n*x.
2. Apply the condition `S%n=0`.
3. Use `average invariant` to obtain equalize while preserving sum.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
S=n*x

At each step evaluate:
S%n=0

Then apply:
average invariant

Stop when the target is determined:
equalize while preserving sum.
```

---

## CF 1855A — Dalton the Teacher

**Problem Link:** [Codeforces — CF 1855A — Dalton the Teacher](https://codeforces.com/problemset/problem/1855/A)  
**Rating:** 800

### 1. The Problem Story

You are given permutation. The problem asks you to minimum operations fixing fixed points by pair operation. The story can be reduced to the mathematical state: each op can fix at most2 fixed points.

**Target / Goal**
- minimum operations fixing fixed points by pair operation

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → permutation
- keep mathematical state → each op can fix at most2 fixed points
- target → minimum operations fixing fixed points by pair operation

**Essential variables**

```text
Input:  permutation
State:  each op can fix at most2 fixed points
Target: minimum operations fixing fixed points by pair operation
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: each op can fix at most2 fixed points.
- Translate the decisive condition into: `ceil(fixed/2)`.
- Simplify/recognize it as: `count fixed`.
- The required output is: minimum operations fixing fixed points by pair operation.

**Mathematical form**

```text
ceil(fixed/2)
    ↓
count fixed
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: each op can fix at most2 fixed points.
2. Apply the condition `ceil(fixed/2)`.
3. Use `count fixed` to obtain minimum operations fixing fixed points by pair operation.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
each op can fix at most2 fixed points

At each step evaluate:
ceil(fixed/2)

Then apply:
count fixed

Stop when the target is determined:
minimum operations fixing fixed points by pair operation.
```

---

## CF 1838A — Blackboard List

**Problem Link:** [Codeforces — CF 1838A — Blackboard List](https://codeforces.com/problemset/problem/1838/A)  
**Rating:** 800

### 1. The Problem Story

You are given array. The problem asks you to recover original special number. The story can be reduced to the mathematical state: negative minimum survives construction; else maximum.

**Target / Goal**
- recover original special number

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → array
- keep mathematical state → negative minimum survives construction; else maximum
- target → recover original special number

**Essential variables**

```text
Input:  array
State:  negative minimum survives construction; else maximum
Target: recover original special number
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: negative minimum survives construction; else maximum.
- Translate the decisive condition into: `extremal invariant`.
- Simplify/recognize it as: `min if negative else max`.
- The required output is: recover original special number.

**Mathematical form**

```text
extremal invariant
    ↓
min if negative else max
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: negative minimum survives construction; else maximum.
2. Apply the condition `extremal invariant`.
3. Use `min if negative else max` to obtain recover original special number.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
negative minimum survives construction; else maximum

At each step evaluate:
extremal invariant

Then apply:
min if negative else max

Stop when the target is determined:
recover original special number.
```

---

## CF 1862B — Sequence Game

**Problem Link:** [Codeforces — CF 1862B — Sequence Game](https://codeforces.com/problemset/problem/1862/B)  
**Rating:** 800

### 1. The Problem Story

You are given sequence b. The problem asks you to construct a so filtering rule returns b. The story can be reduced to the mathematical state: insert bridge when b[i-1]>b[i].

**Target / Goal**
- construct a so filtering rule returns b

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → sequence b
- keep mathematical state → insert bridge when b[i-1]>b[i]
- target → construct a so filtering rule returns b

**Essential variables**

```text
Input:  sequence b
State:  insert bridge when b[i-1]>b[i]
Target: construct a so filtering rule returns b
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: insert bridge when b[i-1]>b[i].
- Translate the decisive condition into: `local condition`.
- Simplify/recognize it as: `construct with extra value`.
- The required output is: construct a so filtering rule returns b.

**Mathematical form**

```text
local condition
    ↓
construct with extra value
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: insert bridge when b[i-1]>b[i].
2. Apply the condition `local condition`.
3. Use `construct with extra value` to obtain construct a so filtering rule returns b.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
insert bridge when b[i-1]>b[i]

At each step evaluate:
local condition

Then apply:
construct with extra value

Stop when the target is determined:
construct a so filtering rule returns b.
```

---

## CF 1798A — Showstopper

**Problem Link:** [Codeforces — CF 1798A — Showstopper](https://codeforces.com/problemset/problem/1798/A)  
**Rating:** 800

### 1. The Problem Story

You are given two arrays. The problem asks you to can swap pairs so last elements are maxima. The story can be reduced to the mathematical state: each pair independently orientable.

**Target / Goal**
- can swap pairs so last elements are maxima

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → two arrays
- keep mathematical state → each pair independently orientable
- target → can swap pairs so last elements are maxima

**Essential variables**

```text
Input:  two arrays
State:  each pair independently orientable
Target: can swap pairs so last elements are maxima
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: each pair independently orientable.
- Translate the decisive condition into: `need max pair endpoints fit final`.
- Simplify/recognize it as: `normalize max/min`.
- The required output is: can swap pairs so last elements are maxima.

**Mathematical form**

```text
need max pair endpoints fit final
    ↓
normalize max/min
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: each pair independently orientable.
2. Apply the condition `need max pair endpoints fit final`.
3. Use `normalize max/min` to obtain can swap pairs so last elements are maxima.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
each pair independently orientable

At each step evaluate:
need max pair endpoints fit final

Then apply:
normalize max/min

Stop when the target is determined:
can swap pairs so last elements are maxima.
```

---

## CF 660A — Co-prime Array

**Problem Link:** [Codeforces — CF 660A — Co-prime Array](https://codeforces.com/problemset/problem/660/A)  
**Rating:** 900

### 1. The Problem Story

You are given array. The problem asks you to insert minimum numbers so adjacent gcd=1. The story can be reduced to the mathematical state: if gcd(a[i],a[i+1])>1 insert coprime sentinel.

**Target / Goal**
- insert minimum numbers so adjacent gcd=1

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → array
- keep mathematical state → if gcd(a[i],a[i+1])>1 insert coprime sentinel
- target → insert minimum numbers so adjacent gcd=1

**Essential variables**

```text
Input:  array
State:  if gcd(a[i],a[i+1])>1 insert coprime sentinel
Target: insert minimum numbers so adjacent gcd=1
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: if gcd(a[i],a[i+1])>1 insert coprime sentinel.
- Translate the decisive condition into: `local repair`.
- Simplify/recognize it as: `insert 1`.
- The required output is: insert minimum numbers so adjacent gcd=1.

**Mathematical form**

```text
local repair
    ↓
insert 1
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: if gcd(a[i],a[i+1])>1 insert coprime sentinel.
2. Apply the condition `local repair`.
3. Use `insert 1` to obtain insert minimum numbers so adjacent gcd=1.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
if gcd(a[i],a[i+1])>1 insert coprime sentinel

At each step evaluate:
local repair

Then apply:
insert 1

Stop when the target is determined:
insert minimum numbers so adjacent gcd=1.
```

---

## CF 1367A — Short Substrings

**Problem Link:** [Codeforces — CF 1367A — Short Substrings](https://codeforces.com/problemset/problem/1367/A)  
**Rating:** 800

### 1. The Problem Story

You are given string b. The problem asks you to recover original. The story can be reduced to the mathematical state: overlap pairs share char.

**Target / Goal**
- recover original

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → string b
- keep mathematical state → overlap pairs share char
- target → recover original

**Essential variables**

```text
Input:  string b
State:  overlap pairs share char
Target: recover original
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: overlap pairs share char.
- Translate the decisive condition into: `take first then every second char`.
- Simplify/recognize it as: `inverse operation`.
- The required output is: recover original.

**Mathematical form**

```text
take first then every second char
    ↓
inverse operation
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: overlap pairs share char.
2. Apply the condition `take first then every second char`.
3. Use `inverse operation` to obtain recover original.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
overlap pairs share char

At each step evaluate:
take first then every second char

Then apply:
inverse operation

Stop when the target is determined:
recover original.
```

---

## CF 1374A — Required Remainder

**Problem Link:** [Codeforces — CF 1374A — Required Remainder](https://codeforces.com/problemset/problem/1374/A)  
**Rating:** 800

### 1. The Problem Story

You are given . The problem asks you to largest k<=n with k%x=y. The story can be reduced to the mathematical state: numbers are tx+y.

**Target / Goal**
- largest k<=n with k%x=y

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 
- keep mathematical state → numbers are tx+y
- target → largest k<=n with k%x=y

**Essential variables**

```text
Input:  
State:  numbers are tx+y
Target: largest k<=n with k%x=y
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: numbers are tx+y.
- Translate the decisive condition into: `t=floor((n-y)/x)`.
- Simplify/recognize it as: `largest feasible`.
- The required output is: largest k<=n with k%x=y.

**Mathematical form**

```text
t=floor((n-y)/x)
    ↓
largest feasible
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: numbers are tx+y.
2. Apply the condition `t=floor((n-y)/x)`.
3. Use `largest feasible` to obtain largest k<=n with k%x=y.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
numbers are tx+y

At each step evaluate:
t=floor((n-y)/x)

Then apply:
largest feasible

Stop when the target is determined:
largest k<=n with k%x=y.
```

---

## CF 1551A — Polycarp and Coins

**Problem Link:** [Codeforces — CF 1551A — Polycarp and Coins](https://codeforces.com/problemset/problem/1551/A)  
**Rating:** 800

### 1. The Problem Story

You are given . The problem asks you to split n into 1-coin and2-coin counts minimizing difference. The story can be reduced to the mathematical state: c1+2c2=n, |c1-c2| min.

**Target / Goal**
- split n into 1-coin and2-coin counts minimizing difference

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 
- keep mathematical state → c1+2c2=n, |c1-c2| min
- target → split n into 1-coin and2-coin counts minimizing difference

**Essential variables**

```text
Input:  
State:  c1+2c2=n, |c1-c2| min
Target: split n into 1-coin and2-coin counts minimizing difference
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: c1+2c2=n, |c1-c2| min.
- Translate the decisive condition into: `near n/3`.
- Simplify/recognize it as: `balanced equation`.
- The required output is: split n into 1-coin and2-coin counts minimizing difference.

**Mathematical form**

```text
near n/3
    ↓
balanced equation
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: c1+2c2=n, |c1-c2| min.
2. Apply the condition `near n/3`.
3. Use `balanced equation` to obtain split n into 1-coin and2-coin counts minimizing difference.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
c1+2c2=n, |c1-c2| min

At each step evaluate:
near n/3

Then apply:
balanced equation

Stop when the target is determined:
split n into 1-coin and2-coin counts minimizing difference.
```

---

## CF 1818A — Politics

**Problem Link:** [Codeforces — CF 1818A — Politics](https://codeforces.com/problemset/problem/1818/A)  
**Rating:** 800

### 1. The Problem Story

You are given strings. The problem asks you to count strings compatible with reference. The story can be reduced to the mathematical state: positions with reference 1 impose equality.

**Target / Goal**
- count strings compatible with reference

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → strings
- keep mathematical state → positions with reference 1 impose equality
- target → count strings compatible with reference

**Essential variables**

```text
Input:  strings
State:  positions with reference 1 impose equality
Target: count strings compatible with reference
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: positions with reference 1 impose equality.
- Translate the decisive condition into: `predicate per string`.
- Simplify/recognize it as: `count valid`.
- The required output is: count strings compatible with reference.

**Mathematical form**

```text
predicate per string
    ↓
count valid
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: positions with reference 1 impose equality.
2. Apply the condition `predicate per string`.
3. Use `count valid` to obtain count strings compatible with reference.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
positions with reference 1 impose equality

At each step evaluate:
predicate per string

Then apply:
count valid

Stop when the target is determined:
count strings compatible with reference.
```

---

# Pattern 9 — Sorting / Coordinate / Distance Modeling

## CF 160A — Twins

**Problem Link:** [Codeforces — CF 160A — Twins](https://codeforces.com/problemset/problem/160/A)  
**Rating:** 900

### 1. The Problem Story

You are given coins. The problem asks you to minimum selected sum > rest. The story can be reduced to the mathematical state: sort descending.

**Target / Goal**
- minimum selected sum > rest

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → coins
- keep mathematical state → sort descending
- target → minimum selected sum > rest

**Essential variables**

```text
Input:  coins
State:  sort descending
Target: minimum selected sum > rest
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: sort descending.
- Translate the decisive condition into: `prefix until 2sum>total`.
- Simplify/recognize it as: `extremal choice`.
- The required output is: minimum selected sum > rest.

**Mathematical form**

```text
prefix until 2sum>total
    ↓
extremal choice
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: sort descending.
2. Apply the condition `prefix until 2sum>total`.
3. Use `extremal choice` to obtain minimum selected sum > rest.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
sort descending

At each step evaluate:
prefix until 2sum>total

Then apply:
extremal choice

Stop when the target is determined:
minimum selected sum > rest.
```

---

## CF 1399A — Remove Smallest

**Problem Link:** [Codeforces — CF 1399A — Remove Smallest](https://codeforces.com/problemset/problem/1399/A)  
**Rating:** 800

### 1. The Problem Story

You may repeatedly choose two array elements whose difference is at most 1 and remove the smaller one. Determine whether the array can be reduced to one element.

**Target / Goal**
- can delete until one remains under diff<=1

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → array
- keep mathematical state → sort; all adjacent gaps<=1
- target → can delete until one remains under diff<=1

**Essential variables**

```text
Input:  array
State:  sort; all adjacent gaps<=1
Target: can delete until one remains under diff<=1
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: sort; all adjacent gaps<=1.
- Translate the decisive condition into: `adjacent condition`.
- Simplify/recognize it as: `check`.
- The required output is: can delete until one remains under diff<=1.

**Mathematical form**

```text
adjacent condition
    ↓
check
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: sort; all adjacent gaps<=1.
2. Apply the condition `adjacent condition`.
3. Use `check` to obtain can delete until one remains under diff<=1.

### 5. Step 4: Quick Dry Run Example

```text
Example: [1,2,2,3]

Sort → [1,2,2,3]
adjacent gaps: 1,0,1
all gaps ≤1 ✅

The smaller elements can be removed successively.
Answer=YES.
```

---

## CF 1760A — Medium Number

**Problem Link:** [Codeforces — CF 1760A — Medium Number](https://codeforces.com/problemset/problem/1760/A)  
**Rating:** 800

### 1. The Problem Story

You are given . The problem asks you to middle value. The story can be reduced to the mathematical state: sort three.

**Target / Goal**
- middle value

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 
- keep mathematical state → sort three
- target → middle value

**Essential variables**

```text
Input:  
State:  sort three
Target: middle value
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: sort three.
- Translate the decisive condition into: `second element`.
- Simplify/recognize it as: `median`.
- The required output is: middle value.

**Mathematical form**

```text
second element
    ↓
median
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: sort three.
2. Apply the condition `second element`.
3. Use `median` to obtain middle value.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
sort three

At each step evaluate:
second element

Then apply:
median

Stop when the target is determined:
middle value.
```

---

## CF 1538A — Stone Game

**Problem Link:** [Codeforces — CF 1538A — Stone Game](https://codeforces.com/problemset/problem/1538/A)  
**Rating:** 800

### 1. The Problem Story

You are given permutation. The problem asks you to min removals from ends to remove min and max. The story can be reduced to the mathematical state: positions pmin,pmax.

**Target / Goal**
- min removals from ends to remove min and max

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → permutation
- keep mathematical state → positions pmin,pmax
- target → min removals from ends to remove min and max

**Essential variables**

```text
Input:  permutation
State:  positions pmin,pmax
Target: min removals from ends to remove min and max
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: positions pmin,pmax.
- Translate the decisive condition into: `min of three strategies`.
- Simplify/recognize it as: `distance to ends`.
- The required output is: min removals from ends to remove min and max.

**Mathematical form**

```text
min of three strategies
    ↓
distance to ends
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: positions pmin,pmax.
2. Apply the condition `min of three strategies`.
3. Use `distance to ends` to obtain min removals from ends to remove min and max.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
positions pmin,pmax

At each step evaluate:
min of three strategies

Then apply:
distance to ends

Stop when the target is determined:
min removals from ends to remove min and max.
```

---

## CF 1729A — Two Elevators

**Problem Link:** [Codeforces — CF 1729A — Two Elevators](https://codeforces.com/problemset/problem/1729/A)  
**Rating:** 800

### 1. The Problem Story

You are given . The problem asks you to compare travel times. The story can be reduced to the mathematical state: t1=a-1, t2=|b-c|+c-1.

**Target / Goal**
- compare travel times

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 
- keep mathematical state → t1=a-1, t2=|b-c|+c-1
- target → compare travel times

**Essential variables**

```text
Input:  
State:  t1=a-1, t2=|b-c|+c-1
Target: compare travel times
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: t1=a-1, t2=|b-c|+c-1.
- Translate the decisive condition into: `absolute distance`.
- Simplify/recognize it as: `compare`.
- The required output is: compare travel times.

**Mathematical form**

```text
absolute distance
    ↓
compare
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: t1=a-1, t2=|b-c|+c-1.
2. Apply the condition `absolute distance`.
3. Use `compare` to obtain compare travel times.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
t1=a-1, t2=|b-c|+c-1

At each step evaluate:
absolute distance

Then apply:
compare

Stop when the target is determined:
compare travel times.
```

---

## CF 1593B — Make it Divisible by 25

**Problem Link:** [Codeforces — CF 1593B — Make it Divisible by 25](https://codeforces.com/problemset/problem/1593/B)  
**Rating:** 900

### 1. The Problem Story

You are given string number. The problem asks you to min deletions for divisible by25. The story can be reduced to the mathematical state: last two digits in {00,25,50,75}.

**Target / Goal**
- min deletions for divisible by25

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → string number
- keep mathematical state → last two digits in {00,25,50,75}
- target → min deletions for divisible by25

**Essential variables**

```text
Input:  string number
State:  last two digits in {00,25,50,75}
Target: min deletions for divisible by25
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: last two digits in {00,25,50,75}.
- Translate the decisive condition into: `find pair from right`.
- Simplify/recognize it as: `pattern search`.
- The required output is: min deletions for divisible by25.

**Mathematical form**

```text
find pair from right
    ↓
pattern search
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: last two digits in {00,25,50,75}.
2. Apply the condition `find pair from right`.
3. Use `pattern search` to obtain min deletions for divisible by25.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
last two digits in {00,25,50,75}

At each step evaluate:
find pair from right

Then apply:
pattern search

Stop when the target is determined:
min deletions for divisible by25.
```

---

## CF 1742F — Smaller

**Problem Link:** [Codeforces — CF 1742F — Smaller](https://codeforces.com/problemset/problem/1742/F)  
**Rating:** 1200

### 1. The Problem Story

You are given string append queries. The problem asks you to whether s<t possible. The story can be reduced to the mathematical state: presence of char >'a' dominates.

**Target / Goal**
- whether s<t possible

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → string append queries
- keep mathematical state → presence of char >'a' dominates
- target → whether s<t possible

**Essential variables**

```text
Input:  string append queries
State:  presence of char >'a' dominates
Target: whether s<t possible
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: presence of char >'a' dominates.
- Translate the decisive condition into: `track counts/flags`.
- Simplify/recognize it as: `compressed state`.
- The required output is: whether s<t possible.

**Mathematical form**

```text
track counts/flags
    ↓
compressed state
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: presence of char >'a' dominates.
2. Apply the condition `track counts/flags`.
3. Use `compressed state` to obtain whether s<t possible.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
presence of char >'a' dominates

At each step evaluate:
track counts/flags

Then apply:
compressed state

Stop when the target is determined:
whether s<t possible.
```

---

## CF 1831A — Twin Permutations

**Problem Link:** [Codeforces — CF 1831A — Twin Permutations](https://codeforces.com/problemset/problem/1831/A)  
**Rating:** 800

### 1. The Problem Story

You are given permutation. The problem asks you to construct complementary permutation. The story can be reduced to the mathematical state: b[i]=n+1-a[i].

**Target / Goal**
- construct complementary permutation

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → permutation
- keep mathematical state → b[i]=n+1-a[i]
- target → construct complementary permutation

**Essential variables**

```text
Input:  permutation
State:  b[i]=n+1-a[i]
Target: construct complementary permutation
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: b[i]=n+1-a[i].
- Translate the decisive condition into: `value reflection`.
- Simplify/recognize it as: `direct transform`.
- The required output is: construct complementary permutation.

**Mathematical form**

```text
value reflection
    ↓
direct transform
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: b[i]=n+1-a[i].
2. Apply the condition `value reflection`.
3. Use `direct transform` to obtain construct complementary permutation.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
b[i]=n+1-a[i]

At each step evaluate:
value reflection

Then apply:
direct transform

Stop when the target is determined:
construct complementary permutation.
```

---

## CF 1900A — Cover in Water

**Problem Link:** [Codeforces — CF 1900A — Cover in Water](https://codeforces.com/problemset/problem/1900/A)  
**Rating:** 800

### 1. The Problem Story

You are given string. The problem asks you to minimum operations to fill dots. The story can be reduced to the mathematical state: run of >=3 triggers shortcut; else count dots.

**Target / Goal**
- minimum operations to fill dots

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → string
- keep mathematical state → run of >=3 triggers shortcut; else count dots
- target → minimum operations to fill dots

**Essential variables**

```text
Input:  string
State:  run of >=3 triggers shortcut; else count dots
Target: minimum operations to fill dots
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: run of >=3 triggers shortcut; else count dots.
- Translate the decisive condition into: `local pattern`.
- Simplify/recognize it as: `case split`.
- The required output is: minimum operations to fill dots.

**Mathematical form**

```text
local pattern
    ↓
case split
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: run of >=3 triggers shortcut; else count dots.
2. Apply the condition `local pattern`.
3. Use `case split` to obtain minimum operations to fill dots.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
run of >=3 triggers shortcut; else count dots

At each step evaluate:
local pattern

Then apply:
case split

Stop when the target is determined:
minimum operations to fill dots.
```

---

## CF 1873B — Good Kid

**Problem Link:** [Codeforces — CF 1873B — Good Kid](https://codeforces.com/problemset/problem/1873/B)  
**Rating:** 800

### 1. The Problem Story

You are given digits. The problem asks you to increment one element to maximize product. The story can be reduced to the mathematical state: increment smallest.

**Target / Goal**
- increment one element to maximize product

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → digits
- keep mathematical state → increment smallest
- target → increment one element to maximize product

**Essential variables**

```text
Input:  digits
State:  increment smallest
Target: increment one element to maximize product
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: increment smallest.
- Translate the decisive condition into: `exchange argument intuition`.
- Simplify/recognize it as: `sort/min index`.
- The required output is: increment one element to maximize product.

**Mathematical form**

```text
exchange argument intuition
    ↓
sort/min index
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: increment smallest.
2. Apply the condition `exchange argument intuition`.
3. Use `sort/min index` to obtain increment one element to maximize product.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
increment smallest

At each step evaluate:
exchange argument intuition

Then apply:
sort/min index

Stop when the target is determined:
increment one element to maximize product.
```

---

# Pattern 10 — Prefix / Running-State Modeling

## CF 116A — Tram

**Problem Link:** [Codeforces — CF 116A — Tram](https://codeforces.com/problemset/problem/116/A)  
**Rating:** 800

### 1. The Problem Story

You are given enter/exit. The problem asks you to minimum capacity. The story can be reduced to the mathematical state: cur += in-out.

**Target / Goal**
- minimum capacity

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → enter/exit
- keep mathematical state → cur += in-out
- target → minimum capacity

**Essential variables**

```text
Input:  enter/exit
State:  cur += in-out
Target: minimum capacity
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: cur += in-out.
- Translate the decisive condition into: `max(cur)`.
- Simplify/recognize it as: `prefix occupancy`.
- The required output is: minimum capacity.

**Mathematical form**

```text
max(cur)
    ↓
prefix occupancy
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: cur += in-out.
2. Apply the condition `max(cur)`.
3. Use `prefix occupancy` to obtain minimum capacity.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
cur += in-out

At each step evaluate:
max(cur)

Then apply:
prefix occupancy

Stop when the target is determined:
minimum capacity.
```

---

## CF 363B — Fence

**Problem Link:** [Codeforces — CF 363B — Fence](https://codeforces.com/problemset/problem/363/B)  
**Rating:** 1100

### 1. The Problem Story

There are n fence planks with given heights. Choose k consecutive planks whose total height is minimum and output the starting position.

**Target / Goal**
- position of minimum k-length sum

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- fence planks → array values
- k consecutive planks → fixed-size window
- total height → window sum

**Essential variables**

```text
Input:  array,k
State:  window sum
Target: position of minimum k-length sum
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: window sum.
- Translate the decisive condition into: `min over contiguous k`.
- Simplify/recognize it as: `prefix/sliding`.
- The required output is: position of minimum k-length sum.

**Mathematical form**

```text
min over contiguous k
    ↓
prefix/sliding
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: window sum.
2. Apply the condition `min over contiguous k`.
3. Use `prefix/sliding` to obtain position of minimum k-length sum.

### 5. Step 4: Quick Dry Run Example

```text
Example: a=[5,2,1,4,3], k=3

window 1: 5+2+1=8
window 2: 2+1+4=7  ← minimum
window 3: 1+4+3=8

Answer=start index 2.
```

---

## CF 276C — Little Girl and Problem on Trees / Little Girl and Maximum Sum

**Problem Link:** [Codeforces — CF 276C — Little Girl and Problem on Trees / Little Girl and Maximum Sum](https://codeforces.com/problemset/problem/276/C)  
**Rating:** 1400

### 1. The Problem Story

An array is queried by many ranges; each range adds the sum of its covered elements to the total score. Rearrange the array to maximize the final score by pairing large values with positions used most often.

**Target / Goal**
- maximize total query sum by permutation

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → array,queries
- keep mathematical state → frequency each index used
- target → maximize total query sum by permutation

**Essential variables**

```text
Input:  array,queries
State:  frequency each index used
Target: maximize total query sum by permutation
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: frequency each index used.
- Translate the decisive condition into: `sort values and frequencies same order`.
- Simplify/recognize it as: `rearrangement inequality`.
- The required output is: maximize total query sum by permutation.

**Mathematical form**

```text
sort values and frequencies same order
    ↓
rearrangement inequality
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: frequency each index used.
2. Apply the condition `sort values and frequencies same order`.
3. Use `rearrangement inequality` to obtain maximize total query sum by permutation.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
frequency each index used

At each step evaluate:
sort values and frequencies same order

Then apply:
rearrangement inequality

Stop when the target is determined:
maximize total query sum by permutation.
```

---

## CF 433B — Kuriyama Mirai's Stones

**Problem Link:** [Codeforces — CF 433B — Kuriyama Mirai's Stones](https://codeforces.com/problemset/problem/433/B)  
**Rating:** 1200

### 1. The Problem Story

Given stone values, answer range-sum queries on both the original array and the sorted array. Precompute two prefix-sum arrays so each query is answered by subtraction.

**Target / Goal**
- range sums original/sorted

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → array,queries
- keep mathematical state → pref and sortedPref
- target → range sums original/sorted

**Essential variables**

```text
Input:  array,queries
State:  pref and sortedPref
Target: range sums original/sorted
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: pref and sortedPref.
- Translate the decisive condition into: `range=p[r]-p[l-1]`.
- Simplify/recognize it as: `static range query`.
- The required output is: range sums original/sorted.

**Mathematical form**

```text
range=p[r]-p[l-1]
    ↓
static range query
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: pref and sortedPref.
2. Apply the condition `range=p[r]-p[l-1]`.
3. Use `static range query` to obtain range sums original/sorted.

### 5. Step 4: Quick Dry Run Example

```text
Example: a=[3,1,4,2]

Original prefix: [0,3,4,8,10]
Sorted array: [1,2,3,4]
Sorted prefix: [0,1,3,6,10]

Query original [2,4]:
pref[4]-pref[1]=10-3=7.
```

---

## CF 313B — Ilya and Queries

**Problem Link:** [Codeforces — CF 313B — Ilya and Queries](https://codeforces.com/problemset/problem/313/B)  
**Rating:** 1100

### 1. The Problem Story

For a string, each query asks how many adjacent equal-character pairs occur inside a substring. Precompute a prefix count of positions where s[i]=s[i-1].

**Target / Goal**
- count equal adjacent pairs in range

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → string,queries
- keep mathematical state → b[i]=[s[i]==s[i-1]]
- target → count equal adjacent pairs in range

**Essential variables**

```text
Input:  string,queries
State:  b[i]=[s[i]==s[i-1]]
Target: count equal adjacent pairs in range
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: b[i]=[s[i]==s[i-1]].
- Translate the decisive condition into: `prefix b`.
- Simplify/recognize it as: `range sum`.
- The required output is: count equal adjacent pairs in range.

**Mathematical form**

```text
prefix b
    ↓
range sum
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: b[i]=[s[i]==s[i-1]].
2. Apply the condition `prefix b`.
3. Use `range sum` to obtain count equal adjacent pairs in range.

### 5. Step 4: Quick Dry Run Example

```text
Example: s="AABBA"

equal-adjacent markers:
AA →1
AB →0
BB →1
BA →0

prefix=[0,1,1,2,2]

Query [1,4] counts markers 1..3:
2 equal adjacent pairs.
```

---

## CF 327A — Flipping Game

**Problem Link:** [Codeforces — CF 327A — Flipping Game](https://codeforces.com/problemset/problem/327/A)  
**Rating:** 1200

### 1. The Problem Story

A binary array allows exactly one segment to be flipped: 0 becomes 1 and 1 becomes 0. Choose the segment that maximizes the final number of ones.

**Target / Goal**
- maximize ones after one flip

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → binary array
- keep mathematical state → gain: 0→+1,1→-1
- target → maximize ones after one flip

**Essential variables**

```text
Input:  binary array
State:  gain: 0→+1,1→-1
Target: maximize ones after one flip
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: gain: 0→+1,1→-1.
- Translate the decisive condition into: `max subarray gain`.
- Simplify/recognize it as: `transform then Kadane`.
- The required output is: maximize ones after one flip.

**Mathematical form**

```text
max subarray gain
    ↓
transform then Kadane
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: gain: 0→+1,1→-1.
2. Apply the condition `max subarray gain`.
3. Use `transform then Kadane` to obtain maximize ones after one flip.

### 5. Step 4: Quick Dry Run Example

```text
Example: [1,0,0,1]

Initial ones=2.
For flip gain use:
0 → +1
1 → -1
gain array=[-1,+1,+1,-1]

Best segment is positions 2..3, gain=2.
Final ones=2+2=4.
```

---

## CF 580A — Kefa and First Steps

**Problem Link:** [Codeforces — CF 580A — Kefa and First Steps](https://codeforces.com/problemset/problem/580/A)  
**Rating:** 900

### 1. The Problem Story

You are given array. The problem asks you to longest nondecreasing contiguous segment. The story can be reduced to the mathematical state: current run based on a[i]>=a[i-1].

**Target / Goal**
- longest nondecreasing contiguous segment

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → array
- keep mathematical state → current run based on a[i]>=a[i-1]
- target → longest nondecreasing contiguous segment

**Essential variables**

```text
Input:  array
State:  current run based on a[i]>=a[i-1]
Target: longest nondecreasing contiguous segment
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: current run based on a[i]>=a[i-1].
- Translate the decisive condition into: `max run`.
- Simplify/recognize it as: `state`.
- The required output is: longest nondecreasing contiguous segment.

**Mathematical form**

```text
max run
    ↓
state
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: current run based on a[i]>=a[i-1].
2. Apply the condition `max run`.
3. Use `state` to obtain longest nondecreasing contiguous segment.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
current run based on a[i]>=a[i-1]

At each step evaluate:
max run

Then apply:
state

Stop when the target is determined:
longest nondecreasing contiguous segment.
```

---

## CF 702A — Maximum Increase

**Problem Link:** [Codeforces — CF 702A — Maximum Increase](https://codeforces.com/problemset/problem/702/A)  
**Rating:** 800

### 1. The Problem Story

You are given array. The problem asks you to longest strictly increasing contiguous segment. The story can be reduced to the mathematical state: current++ if a[i]>a[i-1].

**Target / Goal**
- longest strictly increasing contiguous segment

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → array
- keep mathematical state → current++ if a[i]>a[i-1]
- target → longest strictly increasing contiguous segment

**Essential variables**

```text
Input:  array
State:  current++ if a[i]>a[i-1]
Target: longest strictly increasing contiguous segment
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: current++ if a[i]>a[i-1].
- Translate the decisive condition into: `max run`.
- Simplify/recognize it as: `state`.
- The required output is: longest strictly increasing contiguous segment.

**Mathematical form**

```text
max run
    ↓
state
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: current++ if a[i]>a[i-1].
2. Apply the condition `max run`.
3. Use `state` to obtain longest strictly increasing contiguous segment.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
current++ if a[i]>a[i-1]

At each step evaluate:
max run

Then apply:
state

Stop when the target is determined:
longest strictly increasing contiguous segment.
```

---

## CF 1829B — Blank Space

**Problem Link:** [Codeforces — CF 1829B — Blank Space](https://codeforces.com/problemset/problem/1829/B)  
**Rating:** 800

### 1. The Problem Story

You are given binary array. The problem asks you to longest zeros. The story can be reduced to the mathematical state: current zero run.

**Target / Goal**
- longest zeros

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → binary array
- keep mathematical state → current zero run
- target → longest zeros

**Essential variables**

```text
Input:  binary array
State:  current zero run
Target: longest zeros
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: current zero run.
- Translate the decisive condition into: `max`.
- Simplify/recognize it as: `state`.
- The required output is: longest zeros.

**Mathematical form**

```text
max
    ↓
state
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: current zero run.
2. Apply the condition `max`.
3. Use `state` to obtain longest zeros.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
current zero run

At each step evaluate:
max

Then apply:
state

Stop when the target is determined:
longest zeros.
```

---

## CF 1669F — Eating Candies

**Problem Link:** [Codeforces — CF 1669F — Eating Candies](https://codeforces.com/problemset/problem/1669/F)  
**Rating:** 1100

### 1. The Problem Story

Alice eats candies from the left and Bob from the right. Find the maximum total number of candies they can eat while the sums eaten by both sides are equal.

**Target / Goal**
- max elements eaten with equal left/right sums

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → array
- keep mathematical state → grow smaller side sum
- target → max elements eaten with equal left/right sums

**Essential variables**

```text
Input:  array
State:  grow smaller side sum
Target: max elements eaten with equal left/right sums
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: grow smaller side sum.
- Translate the decisive condition into: `two monotone prefix sums`.
- Simplify/recognize it as: `two pointers`.
- The required output is: max elements eaten with equal left/right sums.

**Mathematical form**

```text
two monotone prefix sums
    ↓
two pointers
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: grow smaller side sum.
2. Apply the condition `two monotone prefix sums`.
3. Use `two pointers` to obtain max elements eaten with equal left/right sums.

### 5. Step 4: Quick Dry Run Example

```text
Example: [1,2,1,1,1,2]

Alice sum=0, Bob sum=0
Alice takes 1 → A=1
Bob takes 2 → B=2
Alice takes 2 → A=3
Bob takes 1 → B=3  ✅ equal

4 candies consumed so far; continue similarly while pointers do not cross.
```

---

# Pattern 11 — Constructive / Reachability Modeling

## CF 1690A — Print a Pedestal

**Problem Link:** [Codeforces — CF 1690A — Print a Pedestal](https://codeforces.com/problemset/problem/1690/A)  
**Rating:** 800

### 1. The Problem Story

You are given . The problem asks you to three distinct positive heights with ordering. The story can be reduced to the mathematical state: a+b+c=n, a<b<c.

**Target / Goal**
- three distinct positive heights with ordering

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 
- keep mathematical state → a+b+c=n, a<b<c
- target → three distinct positive heights with ordering

**Essential variables**

```text
Input:  
State:  a+b+c=n, a<b<c
Target: three distinct positive heights with ordering
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: a+b+c=n, a<b<c.
- Translate the decisive condition into: `near thirds`.
- Simplify/recognize it as: `construct`.
- The required output is: three distinct positive heights with ordering.

**Mathematical form**

```text
near thirds
    ↓
construct
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: a+b+c=n, a<b<c.
2. Apply the condition `near thirds`.
3. Use `construct` to obtain three distinct positive heights with ordering.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
a+b+c=n, a<b<c

At each step evaluate:
near thirds

Then apply:
construct

Stop when the target is determined:
three distinct positive heights with ordering.
```

---

## CF 1845A — Forbidden Integer

**Problem Link:** [Codeforces — CF 1845A — Forbidden Integer](https://codeforces.com/problemset/problem/1845/A)  
**Rating:** 800

### 1. The Problem Story

You are given n,k,x. The problem asks you to sum allowed integers to n. The story can be reduced to the mathematical state: choose 1 if allowed else 2/3.

**Target / Goal**
- sum allowed integers to n

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → n,k,x
- keep mathematical state → choose 1 if allowed else 2/3
- target → sum allowed integers to n

**Essential variables**

```text
Input:  n,k,x
State:  choose 1 if allowed else 2/3
Target: sum allowed integers to n
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: choose 1 if allowed else 2/3.
- Translate the decisive condition into: `simple basis values`.
- Simplify/recognize it as: `construct`.
- The required output is: sum allowed integers to n.

**Mathematical form**

```text
simple basis values
    ↓
construct
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: choose 1 if allowed else 2/3.
2. Apply the condition `simple basis values`.
3. Use `construct` to obtain sum allowed integers to n.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
choose 1 if allowed else 2/3

At each step evaluate:
simple basis values

Then apply:
construct

Stop when the target is determined:
sum allowed integers to n.
```

---

## CF 1878B — Aleksa and Stack

**Problem Link:** [Codeforces — CF 1878B — Aleksa and Stack](https://codeforces.com/problemset/problem/1878/B)  
**Rating:** 800

### 1. The Problem Story

You are given . The problem asks you to build valid sequence. The story can be reduced to the mathematical state: choose simple constant pattern.

**Target / Goal**
- build valid sequence

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 
- keep mathematical state → choose simple constant pattern
- target → build valid sequence

**Essential variables**

```text
Input:  
State:  choose simple constant pattern
Target: build valid sequence
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: choose simple constant pattern.
- Translate the decisive condition into: `satisfy local constraint by design`.
- Simplify/recognize it as: `construction`.
- The required output is: build valid sequence.

**Mathematical form**

```text
satisfy local constraint by design
    ↓
construction
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: choose simple constant pattern.
2. Apply the condition `satisfy local constraint by design`.
3. Use `construction` to obtain build valid sequence.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
choose simple constant pattern

At each step evaluate:
satisfy local constraint by design

Then apply:
construction

Stop when the target is determined:
build valid sequence.
```

---

## CF 1741A — Compare T-Shirt Sizes

**Problem Link:** [Codeforces — CF 1741A — Compare T-Shirt Sizes](https://codeforces.com/problemset/problem/1741/A)  
**Rating:** 800

### 1. The Problem Story

You are given size strings. The problem asks you to compare S/M/L with X count. The story can be reduced to the mathematical state: L: more X larger; S reverse.

**Target / Goal**
- compare S/M/L with X count

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → size strings
- keep mathematical state → L: more X larger; S reverse
- target → compare S/M/L with X count

**Essential variables**

```text
Input:  size strings
State:  L: more X larger; S reverse
Target: compare S/M/L with X count
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: L: more X larger; S reverse.
- Translate the decisive condition into: `map to signed scale`.
- Simplify/recognize it as: `custom ordering`.
- The required output is: compare S/M/L with X count.

**Mathematical form**

```text
map to signed scale
    ↓
custom ordering
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: L: more X larger; S reverse.
2. Apply the condition `map to signed scale`.
3. Use `custom ordering` to obtain compare S/M/L with X count.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
L: more X larger; S reverse

At each step evaluate:
map to signed scale

Then apply:
custom ordering

Stop when the target is determined:
compare S/M/L with X count.
```

---

## CF 1805B — We Need the Zero / The String Has a Target

**Problem Link:** [Codeforces — CF 1805B — We Need the Zero / The String Has a Target](https://codeforces.com/problemset/problem/1805/B)  
**Rating:** 800

### 1. The Problem Story

You are given string. The problem asks you to move smallest char to front under operation. The story can be reduced to the mathematical state: global min char; choose rightmost occurrence.

**Target / Goal**
- move smallest char to front under operation

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → string
- keep mathematical state → global min char; choose rightmost occurrence
- target → move smallest char to front under operation

**Essential variables**

```text
Input:  string
State:  global min char; choose rightmost occurrence
Target: move smallest char to front under operation
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: global min char; choose rightmost occurrence.
- Translate the decisive condition into: `stable reconstruction`.
- Simplify/recognize it as: `greedy`.
- The required output is: move smallest char to front under operation.

**Mathematical form**

```text
stable reconstruction
    ↓
greedy
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: global min char; choose rightmost occurrence.
2. Apply the condition `stable reconstruction`.
3. Use `greedy` to obtain move smallest char to front under operation.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
global min char; choose rightmost occurrence

At each step evaluate:
stable reconstruction

Then apply:
greedy

Stop when the target is determined:
move smallest char to front under operation.
```

---

## CF 1833B — Restore the Weather

**Problem Link:** [Codeforces — CF 1833B — Restore the Weather](https://codeforces.com/problemset/problem/1833/B)  
**Rating:** 1000

### 1. The Problem Story

You are given arrays a,b,k. The problem asks you to permute b so \|a\[i\]-b\[i\]\|<=k. The story can be reduced to the mathematical state: sort indices by a and b.

**Target / Goal**
- permute b so \|a\[i\]-b\[i\]\|<=k

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → arrays a,b,k
- keep mathematical state → sort indices by a and b
- target → permute b so \|a\[i\]-b\[i\]\|<=k

**Essential variables**

```text
Input:  arrays a,b,k
State:  sort indices by a and b
Target: permute b so \|a\[i\]-b\[i\]\|<=k
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: sort indices by a and b.
- Translate the decisive condition into: `monotone matching`.
- Simplify/recognize it as: `pair sorted orders`.
- The required output is: permute b so \|a\[i\]-b\[i\]\|<=k.

**Mathematical form**

```text
monotone matching
    ↓
pair sorted orders
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: sort indices by a and b.
2. Apply the condition `monotone matching`.
3. Use `pair sorted orders` to obtain permute b so \|a\[i\]-b\[i\]\|<=k.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
sort indices by a and b

At each step evaluate:
monotone matching

Then apply:
pair sorted orders

Stop when the target is determined:
permute b so \|a\[i\]-b\[i\]\|<=k.
```

---

## CF 1793C — Dora and Search

**Problem Link:** [Codeforces — CF 1793C — Dora and Search](https://codeforces.com/problemset/problem/1793/C)  
**Rating:** 1200

### 1. The Problem Story

You are given permutation segment. The problem asks you to find segment whose ends are neither min nor max. The story can be reduced to the mathematical state: peel if endpoint is current min/max.

**Target / Goal**
- find segment whose ends are neither min nor max

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → permutation segment
- keep mathematical state → peel if endpoint is current min/max
- target → find segment whose ends are neither min nor max

**Essential variables**

```text
Input:  permutation segment
State:  peel if endpoint is current min/max
Target: find segment whose ends are neither min nor max
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: peel if endpoint is current min/max.
- Translate the decisive condition into: `maintain lo,hi`.
- Simplify/recognize it as: `two pointers`.
- The required output is: find segment whose ends are neither min nor max.

**Mathematical form**

```text
maintain lo,hi
    ↓
two pointers
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: peel if endpoint is current min/max.
2. Apply the condition `maintain lo,hi`.
3. Use `two pointers` to obtain find segment whose ends are neither min nor max.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
peel if endpoint is current min/max

At each step evaluate:
maintain lo,hi

Then apply:
two pointers

Stop when the target is determined:
find segment whose ends are neither min nor max.
```

---

## CF 1881A — Don't Try to Count

**Problem Link:** [Codeforces — CF 1881A — Don't Try to Count](https://codeforces.com/problemset/problem/1881/A)  
**Rating:** 800

### 1. The Problem Story

You are given x,s. The problem asks you to minimum doublings until s substring. The story can be reduced to the mathematical state: length only needs bounded doublings.

**Target / Goal**
- minimum doublings until s substring

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → x,s
- keep mathematical state → length only needs bounded doublings
- target → minimum doublings until s substring

**Essential variables**

```text
Input:  x,s
State:  length only needs bounded doublings
Target: minimum doublings until s substring
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: length only needs bounded doublings.
- Translate the decisive condition into: `repeat x until long enough + margin`.
- Simplify/recognize it as: `simulation bound`.
- The required output is: minimum doublings until s substring.

**Mathematical form**

```text
repeat x until long enough + margin
    ↓
simulation bound
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: length only needs bounded doublings.
2. Apply the condition `repeat x until long enough + margin`.
3. Use `simulation bound` to obtain minimum doublings until s substring.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
length only needs bounded doublings

At each step evaluate:
repeat x until long enough + margin

Then apply:
simulation bound

Stop when the target is determined:
minimum doublings until s substring.
```

---

## CF 1858A — Buttons

**Problem Link:** [Codeforces — CF 1858A — Buttons](https://codeforces.com/problemset/problem/1858/A)  
**Rating:** 800

### 1. The Problem Story

Anna has a private buttons, Katie has b private buttons, and c buttons can be pressed by either player. They alternate turns starting with Anna; a used button disappears, and a player who cannot move loses. Determine the winner under optimal play.

**Target / Goal**
- winner

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- Anna → first player
- Katie → second player
- a → first player's private moves
- b → second player's private moves
- c → shared moves
- press a button → consume one available move

**Essential variables**

```text
Input:  
State:  shared c allocated alternately
Target: winner
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: shared c allocated alternately.
- Translate the decisive condition into: `parity c`.
- Simplify/recognize it as: `effective counts`.
- The required output is: winner.

**Mathematical form**

```text
parity c
    ↓
effective counts
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: shared c allocated alternately.
2. Apply the condition `parity c`.
3. Use `effective counts` to obtain winner.

### 5. Step 4: Quick Dry Run Example

```text
Example: a=1, b=2, c=3

Shared buttons are used alternately:
Anna gets 2 shared turns, Katie gets 1.

Effective moves:
Anna = a + 2 = 3
Katie = b + 1 = 3

Anna moves first. With equal effective move counts, Katie makes the last move.
Anna then has no move → Katie wins.

For odd c, Anna needs a ≥ b to win.
Here 1 ≥ 2 is false → Second wins.
```

---

## CF 1899A — Game with Integers

**Problem Link:** [Codeforces — CF 1899A — Game with Integers](https://codeforces.com/problemset/problem/1899/A)  
**Rating:** 800

### 1. The Problem Story

You are given . The problem asks you to winner. The story can be reduced to the mathematical state: moves ±1; multiples of3 structure.

**Target / Goal**
- winner

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 
- keep mathematical state → moves ±1; multiples of3 structure
- target → winner

**Essential variables**

```text
Input:  
State:  moves ±1; multiples of3 structure
Target: winner
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: moves ±1; multiples of3 structure.
- Translate the decisive condition into: `n%3`.
- Simplify/recognize it as: `residue game`.
- The required output is: winner.

**Mathematical form**

```text
n%3
    ↓
residue game
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: moves ±1; multiples of3 structure.
2. Apply the condition `n%3`.
3. Use `residue game` to obtain winner.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
moves ±1; multiples of3 structure

At each step evaluate:
n%3

Then apply:
residue game

Stop when the target is determined:
winner.
```

---

# Pattern 12 — Bitwise / XOR Modeling

## CF 1805A — We Need the Zero

**Problem Link:** [Codeforces — CF 1805A — We Need the Zero](https://codeforces.com/problemset/problem/1805/A)  
**Rating:** 900

### 1. The Problem Story

You are given array. The problem asks you to find x so xor(a\[i\]^x)=0. The story can be reduced to the mathematical state: xorAll ^ (x repeated n times).

**Target / Goal**
- find x so xor(a\[i\]^x)=0

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → array
- keep mathematical state → xorAll ^ (x repeated n times)
- target → find x so xor(a\[i\]^x)=0

**Essential variables**

```text
Input:  array
State:  xorAll ^ (x repeated n times)
Target: find x so xor(a\[i\]^x)=0
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: xorAll ^ (x repeated n times).
- Translate the decisive condition into: `if n even x cancels; else x=xorAll`.
- Simplify/recognize it as: `parity of n`.
- The required output is: find x so xor(a\[i\]^x)=0.

**Mathematical form**

```text
if n even x cancels; else x=xorAll
    ↓
parity of n
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: xorAll ^ (x repeated n times).
2. Apply the condition `if n even x cancels; else x=xorAll`.
3. Use `parity of n` to obtain find x so xor(a\[i\]^x)=0.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
xorAll ^ (x repeated n times)

At each step evaluate:
if n even x cancels; else x=xorAll

Then apply:
parity of n

Stop when the target is determined:
find x so xor(a\[i\]^x)=0.
```

---

## CF 1872A — Two Vessels

**Problem Link:** [Codeforces — CF 1872A — Two Vessels](https://codeforces.com/problemset/problem/1872/A)  
**Rating:** 800

### 1. The Problem Story

You are given . The problem asks you to min moves balancing transfer c. The story can be reduced to the mathematical state: difference shrinks by 2c.

**Target / Goal**
- min moves balancing transfer c

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 
- keep mathematical state → difference shrinks by 2c
- target → min moves balancing transfer c

**Essential variables**

```text
Input:  
State:  difference shrinks by 2c
Target: min moves balancing transfer c
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: difference shrinks by 2c.
- Translate the decisive condition into: `m*2c>=|a-b|`.
- Simplify/recognize it as: `ceil division`.
- The required output is: min moves balancing transfer c.

**Mathematical form**

```text
m*2c>=|a-b|
    ↓
ceil division
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: difference shrinks by 2c.
2. Apply the condition `m*2c>=|a-b|`.
3. Use `ceil division` to obtain min moves balancing transfer c.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
difference shrinks by 2c

At each step evaluate:
m*2c>=|a-b|

Then apply:
ceil division

Stop when the target is determined:
min moves balancing transfer c.
```

---

## CF 1703A — YES or YES?

**Problem Link:** [Codeforces — CF 1703A — YES or YES?](https://codeforces.com/problemset/problem/1703/A)  
**Rating:** 800

### 1. The Problem Story

You are given word. The problem asks you to case-insensitive equality to yes. The story can be reduced to the mathematical state: normalize case.

**Target / Goal**
- case-insensitive equality to yes

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → word
- keep mathematical state → normalize case
- target → case-insensitive equality to yes

**Essential variables**

```text
Input:  word
State:  normalize case
Target: case-insensitive equality to yes
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: normalize case.
- Translate the decisive condition into: `compare`.
- Simplify/recognize it as: `canonicalization`.
- The required output is: case-insensitive equality to yes.

**Mathematical form**

```text
compare
    ↓
canonicalization
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: normalize case.
2. Apply the condition `compare`.
3. Use `canonicalization` to obtain case-insensitive equality to yes.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
normalize case

At each step evaluate:
compare

Then apply:
canonicalization

Stop when the target is determined:
case-insensitive equality to yes.
```

---

## CF 1624A — Plus One on the Subset

**Problem Link:** [Codeforces — CF 1624A — Plus One on the Subset](https://codeforces.com/problemset/problem/1624/A)  
**Rating:** 800

### 1. The Problem Story

You are given array. The problem asks you to min ops equalize. The story can be reduced to the mathematical state: one op can increment chosen subset.

**Target / Goal**
- min ops equalize

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → array
- keep mathematical state → one op can increment chosen subset
- target → min ops equalize

**Essential variables**

```text
Input:  array
State:  one op can increment chosen subset
Target: min ops equalize
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: one op can increment chosen subset.
- Translate the decisive condition into: `range max-min`.
- Simplify/recognize it as: `potential`.
- The required output is: min ops equalize.

**Mathematical form**

```text
range max-min
    ↓
potential
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: one op can increment chosen subset.
2. Apply the condition `range max-min`.
3. Use `potential` to obtain min ops equalize.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
one op can increment chosen subset

At each step evaluate:
range max-min

Then apply:
potential

Stop when the target is determined:
min ops equalize.
```

---

## CF 1220A — Cards

**Problem Link:** [Codeforces — CF 1220A — Cards](https://codeforces.com/problemset/problem/1220/A)  
**Rating:** 900

### 1. The Problem Story

You are given letters. The problem asks you to recover binary digits from letters. The story can be reduced to the mathematical state: 'z' uniquely identifies zero, 'n' one after ordering.

**Target / Goal**
- recover binary digits from letters

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → letters
- keep mathematical state → 'z' uniquely identifies zero, 'n' one after ordering
- target → recover binary digits from letters

**Essential variables**

```text
Input:  letters
State:  'z' uniquely identifies zero, 'n' one after ordering
Target: recover binary digits from letters
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: 'z' uniquely identifies zero, 'n' one after ordering.
- Translate the decisive condition into: `count z and n`.
- Simplify/recognize it as: `frequency signature`.
- The required output is: recover binary digits from letters.

**Mathematical form**

```text
count z and n
    ↓
frequency signature
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: 'z' uniquely identifies zero, 'n' one after ordering.
2. Apply the condition `count z and n`.
3. Use `frequency signature` to obtain recover binary digits from letters.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
'z' uniquely identifies zero, 'n' one after ordering

At each step evaluate:
count z and n

Then apply:
frequency signature

Stop when the target is determined:
recover binary digits from letters.
```

---

## CF 1362A — Johnny and Ancient Computer

**Problem Link:** [Codeforces — CF 1362A — Johnny and Ancient Computer](https://codeforces.com/problemset/problem/1362/A)  
**Rating:** 900

### 1. The Problem Story

You are given a,b. The problem asks you to min ×2/4/8 operations to transform. The story can be reduced to the mathematical state: ratio must be power of2.

**Target / Goal**
- min ×2/4/8 operations to transform

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → a,b
- keep mathematical state → ratio must be power of2
- target → min ×2/4/8 operations to transform

**Essential variables**

```text
Input:  a,b
State:  ratio must be power of2
Target: min ×2/4/8 operations to transform
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: ratio must be power of2.
- Translate the decisive condition into: `exponent difference grouped by3`.
- Simplify/recognize it as: `factorization`.
- The required output is: min ×2/4/8 operations to transform.

**Mathematical form**

```text
exponent difference grouped by3
    ↓
factorization
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: ratio must be power of2.
2. Apply the condition `exponent difference grouped by3`.
3. Use `factorization` to obtain min ×2/4/8 operations to transform.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
ratio must be power of2

At each step evaluate:
exponent difference grouped by3

Then apply:
factorization

Stop when the target is determined:
min ×2/4/8 operations to transform.
```

---

## CF 1095A — Repeating Cipher

**Problem Link:** [Codeforces — CF 1095A — Repeating Cipher](https://codeforces.com/problemset/problem/1095/A)  
**Rating:** 800

### 1. The Problem Story

You are given encoded string. The problem asks you to decode chars at positions with jumps 1,2,3.... The story can be reduced to the mathematical state: index += step.

**Target / Goal**
- decode chars at positions with jumps 1,2,3...

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → encoded string
- keep mathematical state → index += step
- target → decode chars at positions with jumps 1,2,3...

**Essential variables**

```text
Input:  encoded string
State:  index += step
Target: decode chars at positions with jumps 1,2,3...
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: index += step.
- Translate the decisive condition into: `triangular positions`.
- Simplify/recognize it as: `simulation`.
- The required output is: decode chars at positions with jumps 1,2,3....

**Mathematical form**

```text
triangular positions
    ↓
simulation
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: index += step.
2. Apply the condition `triangular positions`.
3. Use `simulation` to obtain decode chars at positions with jumps 1,2,3....

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
index += step

At each step evaluate:
triangular positions

Then apply:
simulation

Stop when the target is determined:
decode chars at positions with jumps 1,2,3....
```

---

## CF 1324A — Yet Another Tetris Problem

**Problem Link:** [Codeforces — CF 1324A — Yet Another Tetris Problem](https://codeforces.com/problemset/problem/1324/A)  
**Rating:** 800

### 1. The Problem Story

You are given array. The problem asks you to can equalize by subtracting 2. The story can be reduced to the mathematical state: differences preserve parity.

**Target / Goal**
- can equalize by subtracting 2

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → array
- keep mathematical state → differences preserve parity
- target → can equalize by subtracting 2

**Essential variables**

```text
Input:  array
State:  differences preserve parity
Target: can equalize by subtracting 2
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: differences preserve parity.
- Translate the decisive condition into: `all same parity`.
- Simplify/recognize it as: `parity invariant`.
- The required output is: can equalize by subtracting 2.

**Mathematical form**

```text
all same parity
    ↓
parity invariant
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: differences preserve parity.
2. Apply the condition `all same parity`.
3. Use `parity invariant` to obtain can equalize by subtracting 2.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
differences preserve parity

At each step evaluate:
all same parity

Then apply:
parity invariant

Stop when the target is determined:
can equalize by subtracting 2.
```

---

## CF 1462A — Favorite Sequence

**Problem Link:** [Codeforces — CF 1462A — Favorite Sequence](https://codeforces.com/problemset/problem/1462/A)  
**Rating:** 800

### 1. The Problem Story

You are given array. The problem asks you to reorder alternating left/right. The story can be reduced to the mathematical state: take l,r,l+1,r-1.

**Target / Goal**
- reorder alternating left/right

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → array
- keep mathematical state → take l,r,l+1,r-1
- target → reorder alternating left/right

**Essential variables**

```text
Input:  array
State:  take l,r,l+1,r-1
Target: reorder alternating left/right
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: take l,r,l+1,r-1.
- Translate the decisive condition into: `index pattern`.
- Simplify/recognize it as: `two pointers`.
- The required output is: reorder alternating left/right.

**Mathematical form**

```text
index pattern
    ↓
two pointers
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: take l,r,l+1,r-1.
2. Apply the condition `index pattern`.
3. Use `two pointers` to obtain reorder alternating left/right.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
take l,r,l+1,r-1

At each step evaluate:
index pattern

Then apply:
two pointers

Stop when the target is determined:
reorder alternating left/right.
```

---

## CF 1619A — Polycarp and Sums of Subsequences / Square String?

**Problem Link:** [Codeforces — CF 1619A — Polycarp and Sums of Subsequences / Square String?](https://codeforces.com/problemset/problem/1619/A)  
**Rating:** 800

### 1. The Problem Story

You are given s. The problem asks you to is s two equal halves. The story can be reduced to the mathematical state: len even and first half=second.

**Target / Goal**
- is s two equal halves

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → s
- keep mathematical state → len even and first half=second
- target → is s two equal halves

**Essential variables**

```text
Input:  s
State:  len even and first half=second
Target: is s two equal halves
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: len even and first half=second.
- Translate the decisive condition into: `equation on substrings`.
- Simplify/recognize it as: `direct`.
- The required output is: is s two equal halves.

**Mathematical form**

```text
equation on substrings
    ↓
direct
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: len even and first half=second.
2. Apply the condition `equation on substrings`.
3. Use `direct` to obtain is s two equal halves.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
len even and first half=second

At each step evaluate:
equation on substrings

Then apply:
direct

Stop when the target is determined:
is s two equal halves.
```

---

# Pattern 13 — Mixed Blind Decoding

## CF 1538C — Challenging Cliffs / Number of Pairs

**Problem Link:** [Codeforces — CF 1538C — Challenging Cliffs / Number of Pairs](https://codeforces.com/problemset/problem/1538/C)  
**Rating:** 1300

### 1. The Problem Story

You are given array,l,r. The problem asks you to count pair sums in interval. The story can be reduced to the mathematical state: F(r)-F(l-1).

**Target / Goal**
- count pair sums in interval

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → array,l,r
- keep mathematical state → F(r)-F(l-1)
- target → count pair sums in interval

**Essential variables**

```text
Input:  array,l,r
State:  F(r)-F(l-1)
Target: count pair sums in interval
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: F(r)-F(l-1).
- Translate the decisive condition into: `sort + two pointers`.
- Simplify/recognize it as: `count bounded pairs`.
- The required output is: count pair sums in interval.

**Mathematical form**

```text
sort + two pointers
    ↓
count bounded pairs
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: F(r)-F(l-1).
2. Apply the condition `sort + two pointers`.
3. Use `count bounded pairs` to obtain count pair sums in interval.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
F(r)-F(l-1)

At each step evaluate:
sort + two pointers

Then apply:
count bounded pairs

Stop when the target is determined:
count pair sums in interval.
```

---

## CF 1475B — New Year's Number

**Problem Link:** [Codeforces — CF 1475B — New Year's Number](https://codeforces.com/problemset/problem/1475/B)  
**Rating:** 900

### 1. The Problem Story

You are given . The problem asks you to 2020a+2021b=n. The story can be reduced to the mathematical state: 2021=2020+1.

**Target / Goal**
- 2020a+2021b=n

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 
- keep mathematical state → 2021=2020+1
- target → 2020a+2021b=n

**Essential variables**

```text
Input:  
State:  2021=2020+1
Target: 2020a+2021b=n
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: 2021=2020+1.
- Translate the decisive condition into: `b=n%2020 candidate`.
- Simplify/recognize it as: `feasibility`.
- The required output is: 2020a+2021b=n.

**Mathematical form**

```text
b=n%2020 candidate
    ↓
feasibility
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: 2021=2020+1.
2. Apply the condition `b=n%2020 candidate`.
3. Use `feasibility` to obtain 2020a+2021b=n.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
2021=2020+1

At each step evaluate:
b=n%2020 candidate

Then apply:
feasibility

Stop when the target is determined:
2020a+2021b=n.
```

---

## CF 1374A — Required Remainder

**Problem Link:** [Codeforces — CF 1374A — Required Remainder](https://codeforces.com/problemset/problem/1374/A)  
**Rating:** 800

### 1. The Problem Story

You are given . The problem asks you to largest k<=n with k%x=y. The story can be reduced to the mathematical state: k=tx+y.

**Target / Goal**
- largest k<=n with k%x=y

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 
- keep mathematical state → k=tx+y
- target → largest k<=n with k%x=y

**Essential variables**

```text
Input:  
State:  k=tx+y
Target: largest k<=n with k%x=y
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: k=tx+y.
- Translate the decisive condition into: `maximize t under bound`.
- Simplify/recognize it as: `floor`.
- The required output is: largest k<=n with k%x=y.

**Mathematical form**

```text
maximize t under bound
    ↓
floor
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: k=tx+y.
2. Apply the condition `maximize t under bound`.
3. Use `floor` to obtain largest k<=n with k%x=y.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
k=tx+y

At each step evaluate:
maximize t under bound

Then apply:
floor

Stop when the target is determined:
largest k<=n with k%x=y.
```

---

## CF 1551A — Polycarp and Coins

**Problem Link:** [Codeforces — CF 1551A — Polycarp and Coins](https://codeforces.com/problemset/problem/1551/A)  
**Rating:** 800

### 1. The Problem Story

You are given . The problem asks you to c1+2c2=n with counts close. The story can be reduced to the mathematical state: near n/3.

**Target / Goal**
- c1+2c2=n with counts close

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → 
- keep mathematical state → near n/3
- target → c1+2c2=n with counts close

**Essential variables**

```text
Input:  
State:  near n/3
Target: c1+2c2=n with counts close
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: near n/3.
- Translate the decisive condition into: `n%3 cases`.
- Simplify/recognize it as: `construct counts`.
- The required output is: c1+2c2=n with counts close.

**Mathematical form**

```text
n%3 cases
    ↓
construct counts
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: near n/3.
2. Apply the condition `n%3 cases`.
3. Use `construct counts` to obtain c1+2c2=n with counts close.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
near n/3

At each step evaluate:
n%3 cases

Then apply:
construct counts

Stop when the target is determined:
c1+2c2=n with counts close.
```

---

## CF 1593B — Make it Divisible by 25

**Problem Link:** [Codeforces — CF 1593B — Make it Divisible by 25](https://codeforces.com/problemset/problem/1593/B)  
**Rating:** 900

### 1. The Problem Story

You are given digits. The problem asks you to min deletions. The story can be reduced to the mathematical state: last2 digits pattern.

**Target / Goal**
- min deletions

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → digits
- keep mathematical state → last2 digits pattern
- target → min deletions

**Essential variables**

```text
Input:  digits
State:  last2 digits pattern
Target: min deletions
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: last2 digits pattern.
- Translate the decisive condition into: `search from right`.
- Simplify/recognize it as: `four targets`.
- The required output is: min deletions.

**Mathematical form**

```text
search from right
    ↓
four targets
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: last2 digits pattern.
2. Apply the condition `search from right`.
3. Use `four targets` to obtain min deletions.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
last2 digits pattern

At each step evaluate:
search from right

Then apply:
four targets

Stop when the target is determined:
min deletions.
```

---

## CF 1669F — Eating Candies

**Problem Link:** [Codeforces — CF 1669F — Eating Candies](https://codeforces.com/problemset/problem/1669/F)  
**Rating:** 1100

### 1. The Problem Story

Alice eats candies from the left and Bob from the right. Find the maximum total number of candies they can eat while the sums eaten by both sides are equal.

**Target / Goal**
- equal left/right eaten sum maximize count

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → array
- keep mathematical state → monotone sums
- target → equal left/right eaten sum maximize count

**Essential variables**

```text
Input:  array
State:  monotone sums
Target: equal left/right eaten sum maximize count
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: monotone sums.
- Translate the decisive condition into: `advance smaller side`.
- Simplify/recognize it as: `two pointers`.
- The required output is: equal left/right eaten sum maximize count.

**Mathematical form**

```text
advance smaller side
    ↓
two pointers
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: monotone sums.
2. Apply the condition `advance smaller side`.
3. Use `two pointers` to obtain equal left/right eaten sum maximize count.

### 5. Step 4: Quick Dry Run Example

```text
Example: [1,2,1,1,1,2]

Alice sum=0, Bob sum=0
Alice takes 1 → A=1
Bob takes 2 → B=2
Alice takes 2 → A=3
Bob takes 1 → B=3  ✅ equal

4 candies consumed so far; continue similarly while pointers do not cross.
```

---

## CF 1793C — Dora and Search

**Problem Link:** [Codeforces — CF 1793C — Dora and Search](https://codeforces.com/problemset/problem/1793/C)  
**Rating:** 1200

### 1. The Problem Story

You are given permutation. The problem asks you to find non-extreme-ended segment. The story can be reduced to the mathematical state: peel min/max endpoints.

**Target / Goal**
- find non-extreme-ended segment

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → permutation
- keep mathematical state → peel min/max endpoints
- target → find non-extreme-ended segment

**Essential variables**

```text
Input:  permutation
State:  peel min/max endpoints
Target: find non-extreme-ended segment
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: peel min/max endpoints.
- Translate the decisive condition into: `lo/hi invariant`.
- Simplify/recognize it as: `two pointers`.
- The required output is: find non-extreme-ended segment.

**Mathematical form**

```text
lo/hi invariant
    ↓
two pointers
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: peel min/max endpoints.
2. Apply the condition `lo/hi invariant`.
3. Use `two pointers` to obtain find non-extreme-ended segment.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
peel min/max endpoints

At each step evaluate:
lo/hi invariant

Then apply:
two pointers

Stop when the target is determined:
find non-extreme-ended segment.
```

---

## CF 327A — Flipping Game

**Problem Link:** [Codeforces — CF 327A — Flipping Game](https://codeforces.com/problemset/problem/327/A)  
**Rating:** 1200

### 1. The Problem Story

A binary array allows exactly one segment to be flipped: 0 becomes 1 and 1 becomes 0. Choose the segment that maximizes the final number of ones.

**Target / Goal**
- one flip maximize ones

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → binary array
- keep mathematical state → gain map 0→+1,1→-1
- target → one flip maximize ones

**Essential variables**

```text
Input:  binary array
State:  gain map 0→+1,1→-1
Target: one flip maximize ones
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: gain map 0→+1,1→-1.
- Translate the decisive condition into: `maximum subarray`.
- Simplify/recognize it as: `Kadane`.
- The required output is: one flip maximize ones.

**Mathematical form**

```text
maximum subarray
    ↓
Kadane
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: gain map 0→+1,1→-1.
2. Apply the condition `maximum subarray`.
3. Use `Kadane` to obtain one flip maximize ones.

### 5. Step 4: Quick Dry Run Example

```text
Example: [1,0,0,1]

Initial ones=2.
For flip gain use:
0 → +1
1 → -1
gain array=[-1,+1,+1,-1]

Best segment is positions 2..3, gain=2.
Final ones=2+2=4.
```

---

## CF 1520D — Same Differences

**Problem Link:** [Codeforces — CF 1520D — Same Differences](https://codeforces.com/problemset/problem/1520/D)  
**Rating:** 1200

### 1. The Problem Story

Count index pairs i<j satisfying a[j]-a[i]=j-i. Rearranging the equation turns each index into a key, so the task becomes counting equal transformed keys.

**Target / Goal**
- count special pairs

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- array positions → indices i,j
- pair condition → equation
- a[i]-i → transformed key

**Essential variables**

```text
Input:  array
State:  a[j]-j=a[i]-i
Target: count special pairs
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: a[j]-j=a[i]-i.
- Translate the decisive condition into: `equal transformed keys`.
- Simplify/recognize it as: `hash frequency`.
- The required output is: count special pairs.

**Mathematical form**

```text
equal transformed keys
    ↓
hash frequency
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: a[j]-j=a[i]-i.
2. Apply the condition `equal transformed keys`.
3. Use `hash frequency` to obtain count special pairs.

### 5. Step 4: Quick Dry Run Example

```text
Example: a=[3,4,4,7], using 1-based indices

key[i]=a[i]-i:
i=1 → 3-1=2
i=2 → 4-2=2
i=3 → 4-3=1
i=4 → 7-4=3

key 2 appears twice → C(2,2)=1 valid pair.
Answer=1.
```

---

## CF 276C — Little Girl and Maximum Sum

**Problem Link:** [Codeforces — CF 276C — Little Girl and Maximum Sum](https://codeforces.com/problemset/problem/276/C)  
**Rating:** 1400

### 1. The Problem Story

An array is queried by many ranges; each range adds the sum of its covered elements to the total score. Rearrange the array to maximize the final score by pairing large values with positions used most often.

**Target / Goal**
- maximize weighted sum

### 2. Step 1: Remove Story Nouns & Extract Variables

**Remove the narrative:**

- story objects → discard labels
- keep inputs → array,range queries
- keep mathematical state → usage frequency per index
- target → maximize weighted sum

**Essential variables**

```text
Input:  array,range queries
State:  usage frequency per index
Target: maximize weighted sum
```

### 3. Step 2: Formulate Rules in Plain English

- Keep only this mathematical state: usage frequency per index.
- Translate the decisive condition into: `sort both sequences`.
- Simplify/recognize it as: `rearrangement`.
- The required output is: maximize weighted sum.

**Mathematical form**

```text
sort both sequences
    ↓
rearrangement
```

### 4. Step 3: Quick Step-by-Step Logic

1. Extract the variables/state: usage frequency per index.
2. Apply the condition `sort both sequences`.
3. Use `rearrangement` to obtain maximize weighted sum.

### 5. Step 4: Quick Dry Run Example

```text
Use a small valid input from the statement.

Track only:
usage frequency per index

At each step evaluate:
sort both sequences

Then apply:
rearrangement

Stop when the target is determined:
maximize weighted sum.
```

---

