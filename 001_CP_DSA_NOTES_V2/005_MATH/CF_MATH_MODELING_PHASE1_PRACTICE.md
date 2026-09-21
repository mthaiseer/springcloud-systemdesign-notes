# 01 --- CF Mathematical Modeling Phase 1: Decode

> **130 guided Codeforces statement-decoding drills.**
>
> The purpose is not merely to solve the problems. It is to repeatedly
> practice:
>
> `actual statement → remove nouns/story → extract variables → write formula → transform → recognize form → solve`

## Table of Contents

-   [How to Use This Workbook](#how-to-use-this-workbook)
-   [Phase-1 Decoding Pipeline](#phase-1-decoding-pipeline)
-   [130-Problem Progress Tracker](#130-problem-progress-tracker)
-   [Pattern 1 --- Minimum Operations / Ceil
    Division](#pattern-1--minimum-operations-ceil-division)
    -   [CF 617A ---
        Elephant](#cf-617a--elephant-arithmetic--codeforces--800)
    -   [CF 1409A --- Yet Another Two Integers
        Problem](#cf-1409a--yet-another-two-integers-problem-arithmetic--codeforces--800)
    -   [CF 1353A --- Most Unstable
        Array](#cf-1353a--most-unstable-array-formula--codeforces--800)
    -   [CF 1476A --- K-divisible
        Sum](#cf-1476a--k-divisible-sum-bounds--codeforces--1000)
    -   [CF 151A --- Soft
        Drinking](#cf-151a--soft-drinking-capacity--codeforces--800)
    -   [CF 996A --- Hit the
        Lottery](#cf-996a--hit-the-lottery-greedy-division--codeforces--800)
    -   [CF 1669A ---
        Division?](#cf-1669a--division-inequality--codeforces--800)
    -   [CF 1742A --- Sum](#cf-1742a--sum-equation--codeforces--800)
    -   [CF 1850A --- To My
        Critics](#cf-1850a--to-my-critics-bounds--codeforces--800)
    -   [CF 1878A --- How Much Does Daytona
        Cost?](#cf-1878a--how-much-does-daytona-cost-existence--codeforces--800)
-   [Pattern 2 --- Algebra / Equation
    Formation](#pattern-2--algebra-equation-formation)
    -   [CF 734A --- Anton and
        Danik](#cf-734a--anton-and-danik-counting--codeforces--800)
    -   [CF 677A --- Vanya and
        Fence](#cf-677a--vanya-and-fence-formula--codeforces--800)
    -   [CF 71A --- Way Too Long
        Words](#cf-71a--way-too-long-words-string-formula--codeforces--800)
    -   [CF 791A --- Bear and Big
        Brother](#cf-791a--bear-and-big-brother-growth--codeforces--800)
    -   [CF 50A --- Domino
        piling](#cf-50a--domino-piling-counting--codeforces--800)
    -   [CF 231A --- Team](#cf-231a--team-counting--codeforces--800)
    -   [CF 200B --- Drinks](#cf-200b--drinks-average--codeforces--800)
    -   [CF 318A --- Even
        Odds](#cf-318a--even-odds-index-mapping--codeforces--900)
    -   [CF 486A --- Calculating
        Function](#cf-486a--calculating-function-formula--codeforces--800)
    -   [CF 1399A --- Remove
        Smallest](#cf-1399a--remove-smallest-sorting--codeforces--800)
-   [Pattern 3 --- Bounds / Inequalities /
    Min-Max](#pattern-3--bounds-inequalities-min-max)
    -   [CF 1690A --- Print a
        Pedestal](#cf-1690a--print-a-pedestal-construction--codeforces--800)
    -   [CF 1676A ---
        Lucky?](#cf-1676a--lucky-equation--codeforces--800)
    -   [CF 1742B ---
        Increasing](#cf-1742b--increasing-distinctness--codeforces--800)
    -   [CF 1791A --- Codeforces
        Checking](#cf-1791a--codeforces-checking-membership--codeforces--800)
    -   [CF 1829A --- Love
        Story](#cf-1829a--love-story-hamming-distance--codeforces--800)
    -   [CF 1873A --- Short
        Sort](#cf-1873a--short-sort-permutation--codeforces--800)
    -   [CF 1729A --- Two
        Elevators](#cf-1729a--two-elevators-distance--codeforces--800)
    -   [CF 1805A --- We Need the
        Zero](#cf-1805a--we-need-the-zero-xor-bounds--codeforces--900)
    -   [CF 1858A ---
        Buttons](#cf-1858a--buttons-game-counting--codeforces--800)
    -   [CF 1899A --- Game with
        Integers](#cf-1899a--game-with-integers-modulo--codeforces--800)
-   [Pattern 4 --- Parity Modeling](#pattern-4--parity-modeling)
    -   [CF 4A ---
        Watermelon](#cf-4a--watermelon-parity--codeforces--800)
    -   [CF 1296A --- Array with Odd
        Sum](#cf-1296a--array-with-odd-sum-parity--codeforces--800)
    -   [CF 1857A --- Array
        Coloring](#cf-1857a--array-coloring-parity--codeforces--800)
    -   [CF 1834A --- Unit
        Array](#cf-1834a--unit-array-parity-greedy--codeforces--800)
    -   [CF 1367B --- Even
        Array](#cf-1367b--even-array-parity--codeforces--800)
    -   [CF 1475A --- Odd
        Divisor](#cf-1475a--odd-divisor-number-theory--codeforces--900)
    -   [CF 1669C --- Odd/Even
        Increments](#cf-1669c--odd-even-increments-parity--codeforces--800)
    -   [CF 1624A --- Plus One on the
        Subset](#cf-1624a--plus-one-on-the-subset-difference--codeforces--800)
    -   [CF 1788A --- One and
        Two](#cf-1788a--one-and-two-product-parity--codeforces--800)
    -   [CF 1845A --- Forbidden
        Integer](#cf-1845a--forbidden-integer-constructive--codeforces--800)
-   [Pattern 5 --- Divisibility / GCD /
    LCM](#pattern-5--divisibility-gcd-lcm)
    -   [CF 1328A --- Divisibility
        Problem](#cf-1328a--divisibility-problem-modulo--codeforces--800)
    -   [CF 1343A ---
        Candies](#cf-1343a--candies-geometric-divisibility--codeforces--900)
    -   [CF 1370A --- Maximum
        GCD](#cf-1370a--maximum-gcd-gcd--codeforces--800)
    -   [CF 1829C --- Mr. Perfectly
        Fine](#cf-1829c--mr-perfectly-fine-min-bitmask--codeforces--800)
    -   [CF 1618A --- Polycarp and Sums of
        Subsequences](#cf-1618a--polycarp-and-sums-of-subsequences-algebra--codeforces--800)
    -   [CF 160A --- Twins](#cf-160a--twins-greedy-sum--codeforces--900)
    -   [CF 1475B --- New Year's
        Number](#cf-1475b--new-years-number-diophantine--codeforces--900)
    -   [CF 1593A ---
        Elections](#cf-1593a--elections-max-formula--codeforces--800)
    -   [CF 1829B --- Blank
        Space](#cf-1829b--blank-space-run-length--codeforces--800)
    -   [CF 1877A --- Goals of
        Victory](#cf-1877a--goals-of-victory-sum-invariant--codeforces--800)
-   [Pattern 6 --- Modulo / Cyclic
    Modeling](#pattern-6--modulo-cyclic-modeling)
    -   [CF 116A ---
        Tram](#cf-116a--tram-prefix-capacity--codeforces--800)
    -   [CF 266A --- Stones on the
        Table](#cf-266a--stones-on-the-table-adjacent--codeforces--800)
    -   [CF 228A --- Is your horseshoe on the other
        hoof?](#cf-228a--is-your-horseshoe-on-the-other-hoof-distinctness--codeforces--800)
    -   [CF 443A --- Anton and
        Letters](#cf-443a--anton-and-letters-set--codeforces--800)
    -   [CF 59A --- Word](#cf-59a--word-counting--codeforces--800)
    -   [CF 236A --- Boy or
        Girl](#cf-236a--boy-or-girl-set-parity--codeforces--800)
    -   [CF 785A --- Anton and
        Polyhedrons](#cf-785a--anton-and-polyhedrons-mapping--codeforces--800)
    -   [CF 703A --- Mishka and
        Game](#cf-703a--mishka-and-game-comparison--codeforces--800)
    -   [CF 734B --- Anton and
        Digits](#cf-734b--anton-and-digits-greedy-counting--codeforces--800)
    -   [CF 1097A --- Gennady the Card
        Game](#cf-1097a--gennady-the-card-game-matching--codeforces--800)
-   [Pattern 7 --- Counting / Frequency /
    Pairs](#pattern-7--counting-frequency-pairs)
    -   [CF 1520D --- Same
        Differences](#cf-1520d--same-differences-algebra-frequency--codeforces--1200)
    -   [CF 1538C --- Challenging Cliffs / Number of
        Pairs](#cf-1538c--challenging-cliffs-number-of-pairs-two-pointers--codeforces--1300)
    -   [CF 1669B ---
        Triple](#cf-1669b--triple-frequency--codeforces--800)
    -   [CF 1742C ---
        Stripes](#cf-1742c--stripes-grid-existence--codeforces--800)
    -   [CF 1791B --- Following
        Directions](#cf-1791b--following-directions-coordinates--codeforces--800)
    -   [CF 1703B --- ICPC
        Balloons](#cf-1703b--icpc-balloons-frequency--codeforces--800)
    -   [CF 1722A --- Spell
        Check](#cf-1722a--spell-check-frequency-sorting--codeforces--800)
    -   [CF 1791C --- Prepend and
        Append](#cf-1791c--prepend-and-append-two-pointers--codeforces--800)
    -   [CF 1829D --- Gold
        Rush](#cf-1829d--gold-rush-recursion-reachability--codeforces--1000)
    -   [CF 1878B --- Aleksa and
        Stack](#cf-1878b--aleksa-and-stack-construction--codeforces--800)
-   [Pattern 8 --- Operation → Delta →
    Invariant](#pattern-8--operation-delta-invariant)
    -   [CF 1538B --- Friends and
        Candies](#cf-1538b--friends-and-candies-invariant--codeforces--800)
    -   [CF 1855A --- Dalton the
        Teacher](#cf-1855a--dalton-the-teacher-mismatch-operation--codeforces--800)
    -   [CF 1838A --- Blackboard
        List](#cf-1838a--blackboard-list-extremal--codeforces--800)
    -   [CF 1862B --- Sequence
        Game](#cf-1862b--sequence-game-construction--codeforces--800)
    -   [CF 1798A ---
        Showstopper](#cf-1798a--showstopper-invariant-swap--codeforces--800)
    -   [CF 660A --- Co-prime
        Array](#cf-660a--co-prime-array-construction-gcd--codeforces--900)
    -   [CF 1367A --- Short
        Substrings](#cf-1367a--short-substrings-string-reconstruction--codeforces--800)
    -   [CF 1374A --- Required
        Remainder](#cf-1374a--required-remainder-modulo-optimization--codeforces--800)
    -   [CF 1551A --- Polycarp and
        Coins](#cf-1551a--polycarp-and-coins-balancing--codeforces--800)
    -   [CF 1818A ---
        Politics](#cf-1818a--politics-string-counting--codeforces--800)
-   [Pattern 9 --- Sorting / Coordinate / Distance
    Modeling](#pattern-9--sorting-coordinate-distance-modeling)
    -   [CF 160A ---
        Twins](#cf-160a--twins-sorting-greedy--codeforces--900)
    -   [CF 1399A --- Remove
        Smallest](#cf-1399a--remove-smallest-sorting--codeforces--800)
    -   [CF 1760A --- Medium
        Number](#cf-1760a--medium-number-sorting--codeforces--800)
    -   [CF 1538A --- Stone
        Game](#cf-1538a--stone-game-positions--codeforces--800)
    -   [CF 1729A --- Two
        Elevators](#cf-1729a--two-elevators-distance--codeforces--800)
    -   [CF 1593B --- Make it Divisible by
        25](#cf-1593b--make-it-divisible-by-25-digit-pattern--codeforces--900)
    -   [CF 1742F ---
        Smaller](#cf-1742f--smaller-lexicographic-invariant--codeforces--1200)
    -   [CF 1831A --- Twin
        Permutations](#cf-1831a--twin-permutations-mapping--codeforces--800)
    -   [CF 1900A --- Cover in
        Water](#cf-1900a--cover-in-water-run-length--codeforces--800)
    -   [CF 1873B --- Good
        Kid](#cf-1873b--good-kid-product-greedy--codeforces--800)
-   [Pattern 10 --- Prefix / Running-State
    Modeling](#pattern-10--prefix-running-state-modeling)
    -   [CF 116A --- Tram](#cf-116a--tram-prefix--codeforces--800)
    -   [CF 363B ---
        Fence](#cf-363b--fence-sliding-window--codeforces--1100)
    -   [CF 276C --- Little Girl and Problem on Trees / Little Girl and
        Maximum
        Sum](#cf-276c--little-girl-and-problem-on-trees-little-girl-and-maximum-sum-difference-contribution--codeforces--1400)
    -   [CF 433B --- Kuriyama Mirai's
        Stones](#cf-433b--kuriyama-mirais-stones-prefix-sum--codeforces--1200)
    -   [CF 313B --- Ilya and
        Queries](#cf-313b--ilya-and-queries-prefix--codeforces--1100)
    -   [CF 327A --- Flipping
        Game](#cf-327a--flipping-game-transform-kadane--codeforces--1200)
    -   [CF 580A --- Kefa and First
        Steps](#cf-580a--kefa-and-first-steps-run-length--codeforces--900)
    -   [CF 702A --- Maximum
        Increase](#cf-702a--maximum-increase-run-length--codeforces--800)
    -   [CF 1829B --- Blank
        Space](#cf-1829b--blank-space-run-length--codeforces--800)
    -   [CF 1669F --- Eating
        Candies](#cf-1669f--eating-candies-two-pointers-prefix--codeforces--1100)
-   [Pattern 11 --- Constructive / Reachability
    Modeling](#pattern-11--constructive-reachability-modeling)
    -   [CF 1690A --- Print a
        Pedestal](#cf-1690a--print-a-pedestal-constructive--codeforces--800)
    -   [CF 1845A --- Forbidden
        Integer](#cf-1845a--forbidden-integer-constructive--codeforces--800)
    -   [CF 1878B --- Aleksa and
        Stack](#cf-1878b--aleksa-and-stack-constructive--codeforces--800)
    -   [CF 1741A --- Compare T-Shirt
        Sizes](#cf-1741a--compare-t-shirt-sizes-ordering--codeforces--800)
    -   [CF 1805B --- We Need the Zero / The String Has a
        Target](#cf-1805b--we-need-the-zero-the-string-has-a-target-string-greedy--codeforces--800)
    -   [CF 1833B --- Restore the
        Weather](#cf-1833b--restore-the-weather-sorting-matching--codeforces--1000)
    -   [CF 1793C --- Dora and
        Search](#cf-1793c--dora-and-search-two-pointers-extremes--codeforces--1200)
    -   [CF 1881A --- Don't Try to
        Count](#cf-1881a--dont-try-to-count-string-doubling--codeforces--800)
    -   [CF 1858A ---
        Buttons](#cf-1858a--buttons-game-constructive--codeforces--800)
    -   [CF 1899A --- Game with
        Integers](#cf-1899a--game-with-integers-modulo-game--codeforces--800)
-   [Pattern 12 --- Bitwise / XOR
    Modeling](#pattern-12--bitwise-xor-modeling)
    -   [CF 1805A --- We Need the
        Zero](#cf-1805a--we-need-the-zero-xor--codeforces--900)
    -   [CF 1872A --- Two
        Vessels](#cf-1872a--two-vessels-arithmetic--codeforces--800)
    -   [CF 1703A --- YES or
        YES?](#cf-1703a--yes-or-yes-string--codeforces--800)
    -   [CF 1624A --- Plus One on the
        Subset](#cf-1624a--plus-one-on-the-subset-range--codeforces--800)
    -   [CF 1220A ---
        Cards](#cf-1220a--cards-frequency--codeforces--900)
    -   [CF 1362A --- Johnny and Ancient
        Computer](#cf-1362a--johnny-and-ancient-computer-powers-ratio--codeforces--900)
    -   [CF 1095A --- Repeating
        Cipher](#cf-1095a--repeating-cipher-index-pattern--codeforces--800)
    -   [CF 1324A --- Yet Another Tetris
        Problem](#cf-1324a--yet-another-tetris-problem-parity--codeforces--800)
    -   [CF 1462A --- Favorite
        Sequence](#cf-1462a--favorite-sequence-two-pointers--codeforces--800)
    -   [CF 1619A --- Polycarp and Sums of Subsequences / Square
        String?](#cf-1619a--polycarp-and-sums-of-subsequences-square-string-string--codeforces--800)
-   [Pattern 13 --- Mixed Blind
    Decoding](#pattern-13--mixed-blind-decoding)
    -   [CF 1538C --- Challenging Cliffs / Number of
        Pairs](#cf-1538c--challenging-cliffs-number-of-pairs-sortingcounting--codeforces--1300)
    -   [CF 1475B --- New Year's
        Number](#cf-1475b--new-years-number-diophantinemodulo--codeforces--900)
    -   [CF 1374A --- Required
        Remainder](#cf-1374a--required-remainder-modulooptimization--codeforces--800)
    -   [CF 1551A --- Polycarp and
        Coins](#cf-1551a--polycarp-and-coins-equationbalancing--codeforces--800)
    -   [CF 1593B --- Make it Divisible by
        25](#cf-1593b--make-it-divisible-by-25-divisibilitystring--codeforces--900)
    -   [CF 1669F --- Eating
        Candies](#cf-1669f--eating-candies-prefixtwo-pointers--codeforces--1100)
    -   [CF 1793C --- Dora and
        Search](#cf-1793c--dora-and-search-extremestwo-pointers--codeforces--1200)
    -   [CF 327A --- Flipping
        Game](#cf-327a--flipping-game-transformoptimization--codeforces--1200)
    -   [CF 1520D --- Same
        Differences](#cf-1520d--same-differences-algebrafrequency--codeforces--1200)
    -   [CF 276C --- Little Girl and Maximum
        Sum](#cf-276c--little-girl-and-maximum-sum-differencesorting--codeforces--1400)
-   [Phase-1 Recognition Checklist](#phase-1-recognition-checklist)
-   [Reusable Problem Template](#reusable-problem-template)

## Added Learning Layer --- Everyday-Life Mapping

Every problem is now separated into its own visual block and includes an
**Everyday-Life Scenario Map**.

Use the analogy in this direction:

``` text
EVERYDAY SITUATION
      ↓
identify quantities
      ↓
write relationship
      ↓
MATHEMATICAL MODEL
      ↓
map the same model back to Codeforces
```

The analogy is **not another story to memorize**. Its purpose is to make
the underlying formula intuitive enough that different Codeforces
stories collapse to the same model.

# How to Use This Workbook

For every problem, first read the linked Codeforces statement. Before
looking at the guided decode, spend 2--5 minutes trying to produce:

``` text
1. WHAT IS ACTUALLY GIVEN?
2. WHAT IS ACTUALLY ASKED?
3. WHICH NOUNS ARE ONLY STORY?
4. WHICH NUMERICAL OBJECTS REMAIN?
5. WHAT VARIABLES REPRESENT THEM?
6. WHICH SENTENCE DEFINES THE CORE CONDITION?
7. HOW DO I WRITE THAT SENTENCE AS MATH?
8. CAN I SIMPLIFY / REARRANGE IT?
9. WHAT STANDARD FORM APPEARS?
10. WHICH ALGORITHM DIRECTLY IMPLEMENTS THAT FORM?
```

The workbook then shows those steps explicitly.

# Phase-1 Decoding Pipeline

``` text
CODEFORCES STATEMENT
        ↓
WHAT IS THE QUESTION REALLY ASKING?
        ↓
CROSS OUT STORY NOUNS
        ↓
KEEP NUMBERS / ARRAYS / INDICES / OPERATIONS
        ↓
NAME VARIABLES
        ↓
WRITE TARGET
        ↓
ENGLISH CONDITION → SYMBOLS
        ↓
EQUATION / INEQUALITY / MOD / PARITY / STATE
        ↓
SIMPLIFY / TRANSFORM
        ↓
KNOWN MATHEMATICAL FORM
        ↓
ALGORITHM
```

# 130-Problem Progress Tracker

Use `R1/R2/R3/R4`:

-   **R1:** decoded and modeled without help.
-   **R2:** needed deliberate thinking but found the model.
-   **R3:** needed a hint for the equation/transformation.
-   **R4:** could not model it; study the guided decode and retry later.

# Pattern 1 --- Minimum Operations / Ceil Division

## Pattern Overview

Turn 'minimum moves/trips/operations' into a required change D and a
maximum useful change K. Test `m*K >= D`, then take the smallest
feasible integer m.

``` text
Different CF stories
       ↓ remove nouns
Same mathematical family
       ↓
Minimum Operations / Ceil Division
```

```{=html}
<!-- ============================================================ -->
```
# Problem 001 --- Problem 1

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 617A ---
Elephant](https://codeforces.com/problemset/problem/617/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`x`**. After removing the story/context, the real
task is:

``` text
INPUT / GIVEN:
x

WHAT MUST BE FOUND:
minimum moves to reach x with +1..+5

MATHEMATICAL OBJECTS THAT MATTER:
D=x, K=5

CORE RELATION:
5m >= x

FINAL MATHEMATICAL VIEW:
ceil(x/5)
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **5m \>= x**, after which the useful form is **ceil(x/5)**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 617A --- Elephant (Arithmetic / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `5m >= x`. Once the story is stripped away, recognize
    **ceil(x/5)**, then implement **(x+4)/5**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
x

QUESTION:
minimum moves to reach x with +1..+5
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
D=x, K=5
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
x

DERIVED QUANTITIES / USEFUL STATE:
D=x, K=5

UNKNOWN / ANSWER:
minimum moves to reach x with +1..+5
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
5m >= x
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
5m >= x
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
5m >= x

        ↓ simplify / rearrange / classify

ceil(x/5)
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
D=x, K=5
      ↓
EXTRACT TARGET
minimum moves to reach x with +1..+5
      ↓
WRITE FORMULA
5m >= x
      ↓
TRANSFORM
ceil(x/5)
      ↓
ALGORITHM
(x+4)/5
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`x`**.
2.  Ignore narrative names and rewrite the requirement using
    **`D=x, K=5`**.
3.  Express the requirement mathematically as **`5m >= x`**.
4.  Simplify it until the recognizable form **ceil(x/5)** appears.
5.  Implement **(x+4)/5** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          x

Target:         minimum moves to reach x with +1..+5

Variables:      D=x, K=5

Formula:        5m >= x

Transform:      ceil(x/5)

Algorithm:      (x+4)/5

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    D=x, K=5

DEFINE the target:
    minimum moves to reach x with +1..+5

TRANSLATE the condition:
    5m >= x

SIMPLIFY / TRANSFORM:
    ceil(x/5)

APPLY:
    (x+4)/5

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`5m >= x`** and the derived method **(x+4)/5**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `5m >= x`, test **ceil(x/5)** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 002 --- Problem 2

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1409A --- Yet Another Two Integers
Problem](https://codeforces.com/problemset/problem/1409/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`a,b`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
a,b

WHAT MUST BE FOUND:
minimum operations to make a=b using ±1..10

MATHEMATICAL OBJECTS THAT MATTER:
D=|a-b|, K=10

CORE RELATION:
10m >= |a-b|

FINAL MATHEMATICAL VIEW:
ceil(|a-b|/10)
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **10m \>= \|a-b\|**, after which the useful form is
**ceil(\|a-b\|/10)**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1409A --- Yet Another Two Integers Problem (Arithmetic / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `10m >= |a-b|`. Once the story is stripped away,
    recognize **ceil(\|a-b\|/10)**, then implement **(abs(a-b)+9)/10**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
a,b

QUESTION:
minimum operations to make a=b using ±1..10
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
D=|a-b|, K=10
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
a,b

DERIVED QUANTITIES / USEFUL STATE:
D=|a-b|, K=10

UNKNOWN / ANSWER:
minimum operations to make a=b using ±1..10
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
10m >= |a-b|
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
10m >= |a-b|
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
10m >= |a-b|

        ↓ simplify / rearrange / classify

ceil(|a-b|/10)
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
D=|a-b|, K=10
      ↓
EXTRACT TARGET
minimum operations to make a=b using ±1..10
      ↓
WRITE FORMULA
10m >= |a-b|
      ↓
TRANSFORM
ceil(|a-b|/10)
      ↓
ALGORITHM
(abs(a-b)+9)/10
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`a,b`**.
2.  Ignore narrative names and rewrite the requirement using
    **`D=|a-b|, K=10`**.
3.  Express the requirement mathematically as **`10m >= |a-b|`**.
4.  Simplify it until the recognizable form **ceil(\|a-b\|/10)**
    appears.
5.  Implement **(abs(a-b)+9)/10** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          a,b

Target:         minimum operations to make a=b using ±1..10

Variables:      D=|a-b|, K=10

Formula:        10m >= |a-b|

Transform:      ceil(|a-b|/10)

Algorithm:      (abs(a-b)+9)/10

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    D=|a-b|, K=10

DEFINE the target:
    minimum operations to make a=b using ±1..10

TRANSLATE the condition:
    10m >= |a-b|

SIMPLIFY / TRANSFORM:
    ceil(|a-b|/10)

APPLY:
    (abs(a-b)+9)/10

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`10m >= |a-b|`** and the derived method **(abs(a-b)+9)/10**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `10m >= |a-b|`, test **ceil(\|a-b\|/10)** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 003 --- Problem 3

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1353A --- Most Unstable
Array](https://codeforces.com/problemset/problem/1353/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`n,m`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
n,m

WHAT MUST BE FOUND:
maximize sum of adjacent absolute differences under bounds

MATHEMATICAL OBJECTS THAT MATTER:
endpoints/bounds matter

CORE RELATION:
each transition <= m

FINAL MATHEMATICAL VIEW:
construct extremal arrangement
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **each transition \<= m**, after which the useful form is **construct
extremal arrangement**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1353A --- Most Unstable Array (Formula / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `each transition <= m`. Once the story is stripped
    away, recognize **construct extremal arrangement**, then implement
    **handle n=1,2,\>=3**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
n,m

QUESTION:
maximize sum of adjacent absolute differences under bounds
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
endpoints/bounds matter
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
n,m

DERIVED QUANTITIES / USEFUL STATE:
endpoints/bounds matter

UNKNOWN / ANSWER:
maximize sum of adjacent absolute differences under bounds
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
each transition <= m
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
each transition <= m
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
each transition <= m

        ↓ simplify / rearrange / classify

construct extremal arrangement
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
endpoints/bounds matter
      ↓
EXTRACT TARGET
maximize sum of adjacent absolute differences under bounds
      ↓
WRITE FORMULA
each transition <= m
      ↓
TRANSFORM
construct extremal arrangement
      ↓
ALGORITHM
handle n=1,2,>=3
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`n,m`**.
2.  Ignore narrative names and rewrite the requirement using
    **`endpoints/bounds matter`**.
3.  Express the requirement mathematically as
    **`each transition <= m`**.
4.  Simplify it until the recognizable form **construct extremal
    arrangement** appears.
5.  Implement **handle n=1,2,\>=3** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          n,m

Target:         maximize sum of adjacent absolute differences under bounds

Variables:      endpoints/bounds matter

Formula:        each transition <= m

Transform:      construct extremal arrangement

Algorithm:      handle n=1,2,>=3

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    endpoints/bounds matter

DEFINE the target:
    maximize sum of adjacent absolute differences under bounds

TRANSLATE the condition:
    each transition <= m

SIMPLIFY / TRANSFORM:
    construct extremal arrangement

APPLY:
    handle n=1,2,>=3

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`each transition <= m`** and the derived method **handle
    n=1,2,\>=3**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `each transition <= m`, test **construct extremal arrangement**
    immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 004 --- Problem 4

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1476A --- K-divisible
Sum](https://codeforces.com/problemset/problem/1476/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`n,k`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
n,k

WHAT MUST BE FOUND:
minimum possible maximum element while sum is divisible by k

MATHEMATICAL OBJECTS THAT MATTER:
total S >= n and S multiple of k

CORE RELATION:
S = smallest multiple of k >= n

FINAL MATHEMATICAL VIEW:
ceil(S/n)
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **S = smallest multiple of k \>= n**, after which the useful form is
**ceil(S/n)**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1476A --- K-divisible Sum (Bounds / Codeforces / 1000)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `S = smallest multiple of k >= n`. Once the story is
    stripped away, recognize **ceil(S/n)**, then implement
    \*\*S=((n+k-1)/k)\*k\*\*.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
n,k

QUESTION:
minimum possible maximum element while sum is divisible by k
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
total S >= n and S multiple of k
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
n,k

DERIVED QUANTITIES / USEFUL STATE:
total S >= n and S multiple of k

UNKNOWN / ANSWER:
minimum possible maximum element while sum is divisible by k
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
S = smallest multiple of k >= n
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
S = smallest multiple of k >= n
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
S = smallest multiple of k >= n

        ↓ simplify / rearrange / classify

ceil(S/n)
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
total S >= n and S multiple of k
      ↓
EXTRACT TARGET
minimum possible maximum element while sum is divisible by k
      ↓
WRITE FORMULA
S = smallest multiple of k >= n
      ↓
TRANSFORM
ceil(S/n)
      ↓
ALGORITHM
S=((n+k-1)/k)*k
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`n,k`**.
2.  Ignore narrative names and rewrite the requirement using
    **`total S >= n and S multiple of k`**.
3.  Express the requirement mathematically as
    **`S = smallest multiple of k >= n`**.
4.  Simplify it until the recognizable form **ceil(S/n)** appears.
5.  Implement \*\*S=((n+k-1)/k)\*k\*\* and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          n,k

Target:         minimum possible maximum element while sum is divisible by k

Variables:      total S >= n and S multiple of k

Formula:        S = smallest multiple of k >= n

Transform:      ceil(S/n)

Algorithm:      S=((n+k-1)/k)*k

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    total S >= n and S multiple of k

DEFINE the target:
    minimum possible maximum element while sum is divisible by k

TRANSLATE the condition:
    S = smallest multiple of k >= n

SIMPLIFY / TRANSFORM:
    ceil(S/n)

APPLY:
    S=((n+k-1)/k)*k

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`S = smallest multiple of k >= n`** and the derived method
    \*\*S=((n+k-1)/k)\*k\*\*.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `S = smallest multiple of k >= n`, test **ceil(S/n)** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 005 --- Problem 5

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 151A --- Soft
Drinking](https://codeforces.com/problemset/problem/151/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`n,k,l,c,d,p,nl,np`**. After removing the
story/context, the real task is:

``` text
INPUT / GIVEN:
n,k,l,c,d,p,nl,np

WHAT MUST BE FOUND:
number of toasts per friend

MATHEMATICAL OBJECTS THAT MATTER:
three resources

CORE RELATION:
min(drink/nl,limes,salt/np)/n

FINAL MATHEMATICAL VIEW:
limiting resource
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **min(drink/nl,limes,salt/np)/n**, after which the useful form is
**limiting resource**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 151A --- Soft Drinking (Capacity / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `min(drink/nl,limes,salt/np)/n`. Once the story is
    stripped away, recognize **limiting resource**, then implement
    **take minimum capacity**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
n,k,l,c,d,p,nl,np

QUESTION:
number of toasts per friend
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
three resources
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
n,k,l,c,d,p,nl,np

DERIVED QUANTITIES / USEFUL STATE:
three resources

UNKNOWN / ANSWER:
number of toasts per friend
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
min(drink/nl,limes,salt/np)/n
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
min(drink/nl,limes,salt/np)/n
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
min(drink/nl,limes,salt/np)/n

        ↓ simplify / rearrange / classify

limiting resource
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
three resources
      ↓
EXTRACT TARGET
number of toasts per friend
      ↓
WRITE FORMULA
min(drink/nl,limes,salt/np)/n
      ↓
TRANSFORM
limiting resource
      ↓
ALGORITHM
take minimum capacity
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`n,k,l,c,d,p,nl,np`**.
2.  Ignore narrative names and rewrite the requirement using
    **`three resources`**.
3.  Express the requirement mathematically as
    **`min(drink/nl,limes,salt/np)/n`**.
4.  Simplify it until the recognizable form **limiting resource**
    appears.
5.  Implement **take minimum capacity** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          n,k,l,c,d,p,nl,np

Target:         number of toasts per friend

Variables:      three resources

Formula:        min(drink/nl,limes,salt/np)/n

Transform:      limiting resource

Algorithm:      take minimum capacity

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    three resources

DEFINE the target:
    number of toasts per friend

TRANSLATE the condition:
    min(drink/nl,limes,salt/np)/n

SIMPLIFY / TRANSFORM:
    limiting resource

APPLY:
    take minimum capacity

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`min(drink/nl,limes,salt/np)/n`** and the derived method **take
    minimum capacity**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `min(drink/nl,limes,salt/np)/n`, test **limiting resource**
    immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 006 --- Problem 6

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 996A --- Hit the
Lottery](https://codeforces.com/problemset/problem/996/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`n`**. After removing the story/context, the real
task is:

``` text
INPUT / GIVEN:
n

WHAT MUST BE FOUND:
minimum notes using 100,20,10,5,1

MATHEMATICAL OBJECTS THAT MATTER:
largest denomination dominates

CORE RELATION:
q=n/d

FINAL MATHEMATICAL VIEW:
sum quotients
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **q=n/d**, after which the useful form is **sum quotients**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 996A --- Hit the Lottery (Greedy/Division / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `q=n/d`. Once the story is stripped away, recognize
    **sum quotients**, then implement **repeated quotient/remainder**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
n

QUESTION:
minimum notes using 100,20,10,5,1
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
largest denomination dominates
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
n

DERIVED QUANTITIES / USEFUL STATE:
largest denomination dominates

UNKNOWN / ANSWER:
minimum notes using 100,20,10,5,1
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
q=n/d
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
q=n/d
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
q=n/d

        ↓ simplify / rearrange / classify

sum quotients
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
largest denomination dominates
      ↓
EXTRACT TARGET
minimum notes using 100,20,10,5,1
      ↓
WRITE FORMULA
q=n/d
      ↓
TRANSFORM
sum quotients
      ↓
ALGORITHM
repeated quotient/remainder
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`n`**.
2.  Ignore narrative names and rewrite the requirement using
    **`largest denomination dominates`**.
3.  Express the requirement mathematically as **`q=n/d`**.
4.  Simplify it until the recognizable form **sum quotients** appears.
5.  Implement **repeated quotient/remainder** and output the requested
    answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          n

Target:         minimum notes using 100,20,10,5,1

Variables:      largest denomination dominates

Formula:        q=n/d

Transform:      sum quotients

Algorithm:      repeated quotient/remainder

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    largest denomination dominates

DEFINE the target:
    minimum notes using 100,20,10,5,1

TRANSLATE the condition:
    q=n/d

SIMPLIFY / TRANSFORM:
    sum quotients

APPLY:
    repeated quotient/remainder

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`q=n/d`** and the derived method **repeated quotient/remainder**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `q=n/d`, test **sum quotients** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 007 --- Problem 7

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1669A ---
Division?](https://codeforces.com/problemset/problem/1669/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`rating`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
rating

WHAT MUST BE FOUND:
classify rating into interval

MATHEMATICAL OBJECTS THAT MATTER:
numeric boundaries

CORE RELATION:
compare rating with cutoffs

FINAL MATHEMATICAL VIEW:
interval classification
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **compare rating with cutoffs**, after which the useful form is
**interval classification**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1669A --- Division? (Inequality / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `compare rating with cutoffs`. Once the story is
    stripped away, recognize **interval classification**, then implement
    **if/else**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
rating

QUESTION:
classify rating into interval
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
numeric boundaries
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
rating

DERIVED QUANTITIES / USEFUL STATE:
numeric boundaries

UNKNOWN / ANSWER:
classify rating into interval
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
compare rating with cutoffs
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
compare rating with cutoffs
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
compare rating with cutoffs

        ↓ simplify / rearrange / classify

interval classification
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
numeric boundaries
      ↓
EXTRACT TARGET
classify rating into interval
      ↓
WRITE FORMULA
compare rating with cutoffs
      ↓
TRANSFORM
interval classification
      ↓
ALGORITHM
if/else
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`rating`**.
2.  Ignore narrative names and rewrite the requirement using
    **`numeric boundaries`**.
3.  Express the requirement mathematically as
    **`compare rating with cutoffs`**.
4.  Simplify it until the recognizable form **interval classification**
    appears.
5.  Implement **if/else** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          rating

Target:         classify rating into interval

Variables:      numeric boundaries

Formula:        compare rating with cutoffs

Transform:      interval classification

Algorithm:      if/else

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    numeric boundaries

DEFINE the target:
    classify rating into interval

TRANSLATE the condition:
    compare rating with cutoffs

SIMPLIFY / TRANSFORM:
    interval classification

APPLY:
    if/else

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`compare rating with cutoffs`** and the derived method
    **if/else**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `compare rating with cutoffs`, test **interval classification**
    immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 008 --- Problem 8

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1742A ---
Sum](https://codeforces.com/problemset/problem/1742/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`a,b,c`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
a,b,c

WHAT MUST BE FOUND:
whether one number equals sum of other two

MATHEMATICAL OBJECTS THAT MATTER:
test 3 equations

CORE RELATION:
a+b=c etc.

FINAL MATHEMATICAL VIEW:
direct feasibility
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **a+b=c etc.**, after which the useful form is **direct
feasibility**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1742A --- Sum (Equation / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `a+b=c etc.`. Once the story is stripped away,
    recognize **direct feasibility**, then implement **three checks**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
a,b,c

QUESTION:
whether one number equals sum of other two
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
test 3 equations
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
a,b,c

DERIVED QUANTITIES / USEFUL STATE:
test 3 equations

UNKNOWN / ANSWER:
whether one number equals sum of other two
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
a+b=c etc.
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
a+b=c etc.
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
a+b=c etc.

        ↓ simplify / rearrange / classify

direct feasibility
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
test 3 equations
      ↓
EXTRACT TARGET
whether one number equals sum of other two
      ↓
WRITE FORMULA
a+b=c etc.
      ↓
TRANSFORM
direct feasibility
      ↓
ALGORITHM
three checks
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`a,b,c`**.
2.  Ignore narrative names and rewrite the requirement using
    **`test 3 equations`**.
3.  Express the requirement mathematically as **`a+b=c etc.`**.
4.  Simplify it until the recognizable form **direct feasibility**
    appears.
5.  Implement **three checks** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          a,b,c

Target:         whether one number equals sum of other two

Variables:      test 3 equations

Formula:        a+b=c etc.

Transform:      direct feasibility

Algorithm:      three checks

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    test 3 equations

DEFINE the target:
    whether one number equals sum of other two

TRANSLATE the condition:
    a+b=c etc.

SIMPLIFY / TRANSFORM:
    direct feasibility

APPLY:
    three checks

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`a+b=c etc.`** and the derived method **three checks**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `a+b=c etc.`, test **direct feasibility** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 009 --- Problem 9

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1850A --- To My
Critics](https://codeforces.com/problemset/problem/1850/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`a,b,c`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
a,b,c

WHAT MUST BE FOUND:
whether any pair sum >=10

MATHEMATICAL OBJECTS THAT MATTER:
only 3 pairs

CORE RELATION:
max pair sum

FINAL MATHEMATICAL VIEW:
sort or direct checks
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **max pair sum**, after which the useful form is **sort or direct
checks**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1850A --- To My Critics (Bounds / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `max pair sum`. Once the story is stripped away,
    recognize **sort or direct checks**, then implement **a+b\>=10 \|\|
    ...**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
a,b,c

QUESTION:
whether any pair sum >=10
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
only 3 pairs
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
a,b,c

DERIVED QUANTITIES / USEFUL STATE:
only 3 pairs

UNKNOWN / ANSWER:
whether any pair sum >=10
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
max pair sum
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
max pair sum
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
max pair sum

        ↓ simplify / rearrange / classify

sort or direct checks
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
only 3 pairs
      ↓
EXTRACT TARGET
whether any pair sum >=10
      ↓
WRITE FORMULA
max pair sum
      ↓
TRANSFORM
sort or direct checks
      ↓
ALGORITHM
a+b>=10 || ...
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`a,b,c`**.
2.  Ignore narrative names and rewrite the requirement using
    **`only 3 pairs`**.
3.  Express the requirement mathematically as **`max pair sum`**.
4.  Simplify it until the recognizable form **sort or direct checks**
    appears.
5.  Implement **a+b\>=10 \|\| ...** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          a,b,c

Target:         whether any pair sum >=10

Variables:      only 3 pairs

Formula:        max pair sum

Transform:      sort or direct checks

Algorithm:      a+b>=10 || ...

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    only 3 pairs

DEFINE the target:
    whether any pair sum >=10

TRANSLATE the condition:
    max pair sum

SIMPLIFY / TRANSFORM:
    sort or direct checks

APPLY:
    a+b>=10 || ...

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`max pair sum`** and the derived method **a+b\>=10 \|\| ...**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `max pair sum`, test **sort or direct checks** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 010 --- Problem 10

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1878A --- How Much Does Daytona
Cost?](https://codeforces.com/problemset/problem/1878/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`n,k,array`**. After removing the story/context,
the real task is:

``` text
INPUT / GIVEN:
n,k,array

WHAT MUST BE FOUND:
whether k appears

MATHEMATICAL OBJECTS THAT MATTER:
target is existence

CORE RELATION:
∃i: a[i]=k

FINAL MATHEMATICAL VIEW:
linear scan
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **∃i: a\[i\]=k**, after which the useful form is **linear scan**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1878A --- How Much Does Daytona Cost? (Existence / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `∃i: a[i]=k`. Once the story is stripped away,
    recognize **linear scan**, then implement **found flag**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
n,k,array

QUESTION:
whether k appears
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
target is existence
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
n,k,array

DERIVED QUANTITIES / USEFUL STATE:
target is existence

UNKNOWN / ANSWER:
whether k appears
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
∃i: a[i]=k
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
∃i: a[i]=k
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
∃i: a[i]=k

        ↓ simplify / rearrange / classify

linear scan
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
target is existence
      ↓
EXTRACT TARGET
whether k appears
      ↓
WRITE FORMULA
∃i: a[i]=k
      ↓
TRANSFORM
linear scan
      ↓
ALGORITHM
found flag
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`n,k,array`**.
2.  Ignore narrative names and rewrite the requirement using
    **`target is existence`**.
3.  Express the requirement mathematically as **`∃i: a[i]=k`**.
4.  Simplify it until the recognizable form **linear scan** appears.
5.  Implement **found flag** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          n,k,array

Target:         whether k appears

Variables:      target is existence

Formula:        ∃i: a[i]=k

Transform:      linear scan

Algorithm:      found flag

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    target is existence

DEFINE the target:
    whether k appears

TRANSLATE the condition:
    ∃i: a[i]=k

SIMPLIFY / TRANSFORM:
    linear scan

APPLY:
    found flag

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`∃i: a[i]=k`** and the derived method **found flag**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `∃i: a[i]=k`, test **linear scan** immediately.

# Pattern 2 --- Algebra / Equation Formation

## Pattern Overview

Delete the story and introduce unknowns. Translate totals, differences,
and equalities into equations; isolate the unknown or test feasibility.

``` text
Different CF stories
       ↓ remove nouns
Same mathematical family
       ↓
Algebra / Equation Formation
```

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 011 --- Problem 11

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 734A --- Anton and
Danik](https://codeforces.com/problemset/problem/734/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`n,string`**. After removing the story/context,
the real task is:

``` text
INPUT / GIVEN:
n,string

WHAT MUST BE FOUND:
who won more games

MATHEMATICAL OBJECTS THAT MATTER:
A=count('A'), D=count('D')

CORE RELATION:
compare A and D

FINAL MATHEMATICAL VIEW:
sign of A-D
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **compare A and D**, after which the useful form is **sign of A-D**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 734A --- Anton and Danik (Counting / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `compare A and D`. Once the story is stripped away,
    recognize **sign of A-D**, then implement **count chars**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
n,string

QUESTION:
who won more games
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
A=count('A'), D=count('D')
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
n,string

DERIVED QUANTITIES / USEFUL STATE:
A=count('A'), D=count('D')

UNKNOWN / ANSWER:
who won more games
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
compare A and D
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
compare A and D
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
compare A and D

        ↓ simplify / rearrange / classify

sign of A-D
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
A=count('A'), D=count('D')
      ↓
EXTRACT TARGET
who won more games
      ↓
WRITE FORMULA
compare A and D
      ↓
TRANSFORM
sign of A-D
      ↓
ALGORITHM
count chars
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`n,string`**.
2.  Ignore narrative names and rewrite the requirement using
    **`A=count('A'), D=count('D')`**.
3.  Express the requirement mathematically as **`compare A and D`**.
4.  Simplify it until the recognizable form **sign of A-D** appears.
5.  Implement **count chars** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          n,string

Target:         who won more games

Variables:      A=count('A'), D=count('D')

Formula:        compare A and D

Transform:      sign of A-D

Algorithm:      count chars

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    A=count('A'), D=count('D')

DEFINE the target:
    who won more games

TRANSLATE the condition:
    compare A and D

SIMPLIFY / TRANSFORM:
    sign of A-D

APPLY:
    count chars

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`compare A and D`** and the derived method **count chars**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `compare A and D`, test **sign of A-D** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 012 --- Problem 12

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 677A --- Vanya and
Fence](https://codeforces.com/problemset/problem/677/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`n,h,heights`**. After removing the story/context,
the real task is:

``` text
INPUT / GIVEN:
n,h,heights

WHAT MUST BE FOUND:
total width

MATHEMATICAL OBJECTS THAT MATTER:
each person contributes 1 or 2

CORE RELATION:
sum (a[i]>h ? 2:1)

FINAL MATHEMATICAL VIEW:
contribution sum
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **sum (a\[i\]\>h ? 2:1)**, after which the useful form is
**contribution sum**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 677A --- Vanya and Fence (Formula / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `sum (a[i]>h ? 2:1)`. Once the story is stripped away,
    recognize **contribution sum**, then implement **linear scan**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
n,h,heights

QUESTION:
total width
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
each person contributes 1 or 2
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
n,h,heights

DERIVED QUANTITIES / USEFUL STATE:
each person contributes 1 or 2

UNKNOWN / ANSWER:
total width
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
sum (a[i]>h ? 2:1)
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
sum (a[i]>h ? 2:1)
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
sum (a[i]>h ? 2:1)

        ↓ simplify / rearrange / classify

contribution sum
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
each person contributes 1 or 2
      ↓
EXTRACT TARGET
total width
      ↓
WRITE FORMULA
sum (a[i]>h ? 2:1)
      ↓
TRANSFORM
contribution sum
      ↓
ALGORITHM
linear scan
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`n,h,heights`**.
2.  Ignore narrative names and rewrite the requirement using
    **`each person contributes 1 or 2`**.
3.  Express the requirement mathematically as **`sum (a[i]>h ? 2:1)`**.
4.  Simplify it until the recognizable form **contribution sum**
    appears.
5.  Implement **linear scan** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          n,h,heights

Target:         total width

Variables:      each person contributes 1 or 2

Formula:        sum (a[i]>h ? 2:1)

Transform:      contribution sum

Algorithm:      linear scan

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    each person contributes 1 or 2

DEFINE the target:
    total width

TRANSLATE the condition:
    sum (a[i]>h ? 2:1)

SIMPLIFY / TRANSFORM:
    contribution sum

APPLY:
    linear scan

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`sum (a[i]>h ? 2:1)`** and the derived method **linear scan**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `sum (a[i]>h ? 2:1)`, test **contribution sum** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 013 --- Problem 13

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 71A --- Way Too Long
Words](https://codeforces.com/problemset/problem/71/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`word`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
word

WHAT MUST BE FOUND:
abbreviate if length>10

MATHEMATICAL OBJECTS THAT MATTER:
first + (len-2) + last

CORE RELATION:
length condition

FINAL MATHEMATICAL VIEW:
direct construction
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **length condition**, after which the useful form is **direct
construction**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 71A --- Way Too Long Words (String/Formula / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `length condition`. Once the story is stripped away,
    recognize **direct construction**, then implement **O(len)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
word

QUESTION:
abbreviate if length>10
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
first + (len-2) + last
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
word

DERIVED QUANTITIES / USEFUL STATE:
first + (len-2) + last

UNKNOWN / ANSWER:
abbreviate if length>10
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
length condition
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
length condition
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
length condition

        ↓ simplify / rearrange / classify

direct construction
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
first + (len-2) + last
      ↓
EXTRACT TARGET
abbreviate if length>10
      ↓
WRITE FORMULA
length condition
      ↓
TRANSFORM
direct construction
      ↓
ALGORITHM
O(len)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`word`**.
2.  Ignore narrative names and rewrite the requirement using
    **`first + (len-2) + last`**.
3.  Express the requirement mathematically as **`length condition`**.
4.  Simplify it until the recognizable form **direct construction**
    appears.
5.  Implement **O(len)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          word

Target:         abbreviate if length>10

Variables:      first + (len-2) + last

Formula:        length condition

Transform:      direct construction

Algorithm:      O(len)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    first + (len-2) + last

DEFINE the target:
    abbreviate if length>10

TRANSLATE the condition:
    length condition

SIMPLIFY / TRANSFORM:
    direct construction

APPLY:
    O(len)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`length condition`** and the derived method **O(len)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `length condition`, test **direct construction** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 014 --- Problem 14

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 791A --- Bear and Big
Brother](https://codeforces.com/problemset/problem/791/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`a,b`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
a,b

WHAT MUST BE FOUND:
years until 3^t a > 2^t b

MATHEMATICAL OBJECTS THAT MATTER:
simulate multiplicative equation

CORE RELATION:
a*=3,b*=2

FINAL MATHEMATICAL VIEW:
first t with a>b
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **a*=3,b*=2**, after which the useful form is **first t with a\>b**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 791A --- Bear and Big Brother (Growth / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `a*=3,b*=2`. Once the story is stripped away, recognize
    **first t with a\>b**, then implement **loop**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
a,b

QUESTION:
years until 3^t a > 2^t b
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
simulate multiplicative equation
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
a,b

DERIVED QUANTITIES / USEFUL STATE:
simulate multiplicative equation

UNKNOWN / ANSWER:
years until 3^t a > 2^t b
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
a*=3,b*=2
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
a*=3,b*=2
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
a*=3,b*=2

        ↓ simplify / rearrange / classify

first t with a>b
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
simulate multiplicative equation
      ↓
EXTRACT TARGET
years until 3^t a > 2^t b
      ↓
WRITE FORMULA
a*=3,b*=2
      ↓
TRANSFORM
first t with a>b
      ↓
ALGORITHM
loop
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`a,b`**.
2.  Ignore narrative names and rewrite the requirement using
    **`simulate multiplicative equation`**.
3.  Express the requirement mathematically as **`a*=3,b*=2`**.
4.  Simplify it until the recognizable form **first t with a\>b**
    appears.
5.  Implement **loop** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          a,b

Target:         years until 3^t a > 2^t b

Variables:      simulate multiplicative equation

Formula:        a*=3,b*=2

Transform:      first t with a>b

Algorithm:      loop

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    simulate multiplicative equation

DEFINE the target:
    years until 3^t a > 2^t b

TRANSLATE the condition:
    a*=3,b*=2

SIMPLIFY / TRANSFORM:
    first t with a>b

APPLY:
    loop

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`a*=3,b*=2`** and the derived method **loop**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `a*=3,b*=2`, test **first t with a\>b** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 015 --- Problem 15

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 50A --- Domino
piling](https://codeforces.com/problemset/problem/50/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`m,n`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
m,n

WHAT MUST BE FOUND:
max dominoes in grid

MATHEMATICAL OBJECTS THAT MATTER:
each domino covers 2 cells

CORE RELATION:
2x <= mn

FINAL MATHEMATICAL VIEW:
floor(mn/2)
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **2x \<= mn**, after which the useful form is **floor(mn/2)**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 50A --- Domino piling (Counting / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `2x <= mn`. Once the story is stripped away, recognize
    **floor(mn/2)**, then implement \*\*m\*n/2\*\*.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
m,n

QUESTION:
max dominoes in grid
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
each domino covers 2 cells
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
m,n

DERIVED QUANTITIES / USEFUL STATE:
each domino covers 2 cells

UNKNOWN / ANSWER:
max dominoes in grid
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
2x <= mn
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
2x <= mn
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
2x <= mn

        ↓ simplify / rearrange / classify

floor(mn/2)
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
each domino covers 2 cells
      ↓
EXTRACT TARGET
max dominoes in grid
      ↓
WRITE FORMULA
2x <= mn
      ↓
TRANSFORM
floor(mn/2)
      ↓
ALGORITHM
m*n/2
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`m,n`**.
2.  Ignore narrative names and rewrite the requirement using
    **`each domino covers 2 cells`**.
3.  Express the requirement mathematically as **`2x <= mn`**.
4.  Simplify it until the recognizable form **floor(mn/2)** appears.
5.  Implement \*\*m\*n/2\*\* and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          m,n

Target:         max dominoes in grid

Variables:      each domino covers 2 cells

Formula:        2x <= mn

Transform:      floor(mn/2)

Algorithm:      m*n/2

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    each domino covers 2 cells

DEFINE the target:
    max dominoes in grid

TRANSLATE the condition:
    2x <= mn

SIMPLIFY / TRANSFORM:
    floor(mn/2)

APPLY:
    m*n/2

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`2x <= mn`** and the derived method \*\*m\*n/2\*\*.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `2x <= mn`, test **floor(mn/2)** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 016 --- Problem 16

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 231A ---
Team](https://codeforces.com/problemset/problem/231/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`triples`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
triples

WHAT MUST BE FOUND:
count problems with >=2 yes

MATHEMATICAL OBJECTS THAT MATTER:
sum triple >=2

CORE RELATION:
indicator contribution

FINAL MATHEMATICAL VIEW:
count
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **indicator contribution**, after which the useful form is **count**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 231A --- Team (Counting / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `indicator contribution`. Once the story is stripped
    away, recognize **count**, then implement **linear**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
triples

QUESTION:
count problems with >=2 yes
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
sum triple >=2
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
triples

DERIVED QUANTITIES / USEFUL STATE:
sum triple >=2

UNKNOWN / ANSWER:
count problems with >=2 yes
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
indicator contribution
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
indicator contribution
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
indicator contribution

        ↓ simplify / rearrange / classify

count
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
sum triple >=2
      ↓
EXTRACT TARGET
count problems with >=2 yes
      ↓
WRITE FORMULA
indicator contribution
      ↓
TRANSFORM
count
      ↓
ALGORITHM
linear
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`triples`**.
2.  Ignore narrative names and rewrite the requirement using
    **`sum triple >=2`**.
3.  Express the requirement mathematically as
    **`indicator contribution`**.
4.  Simplify it until the recognizable form **count** appears.
5.  Implement **linear** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          triples

Target:         count problems with >=2 yes

Variables:      sum triple >=2

Formula:        indicator contribution

Transform:      count

Algorithm:      linear

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    sum triple >=2

DEFINE the target:
    count problems with >=2 yes

TRANSLATE the condition:
    indicator contribution

SIMPLIFY / TRANSFORM:
    count

APPLY:
    linear

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`indicator contribution`** and the derived method **linear**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `indicator contribution`, test **count** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 017 --- Problem 17

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 200B ---
Drinks](https://codeforces.com/problemset/problem/200/B)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`n,p`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
n,p

WHAT MUST BE FOUND:
orange percentage

MATHEMATICAL OBJECTS THAT MATTER:
average of p

CORE RELATION:
sum/n

FINAL MATHEMATICAL VIEW:
mean
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **sum/n**, after which the useful form is **mean**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 200B --- Drinks (Average / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `sum/n`. Once the story is stripped away, recognize
    **mean**, then implement **double sum/n**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
n,p

QUESTION:
orange percentage
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
average of p
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
n,p

DERIVED QUANTITIES / USEFUL STATE:
average of p

UNKNOWN / ANSWER:
orange percentage
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
sum/n
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
sum/n
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
sum/n

        ↓ simplify / rearrange / classify

mean
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
average of p
      ↓
EXTRACT TARGET
orange percentage
      ↓
WRITE FORMULA
sum/n
      ↓
TRANSFORM
mean
      ↓
ALGORITHM
double sum/n
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`n,p`**.
2.  Ignore narrative names and rewrite the requirement using
    **`average of p`**.
3.  Express the requirement mathematically as **`sum/n`**.
4.  Simplify it until the recognizable form **mean** appears.
5.  Implement **double sum/n** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          n,p

Target:         orange percentage

Variables:      average of p

Formula:        sum/n

Transform:      mean

Algorithm:      double sum/n

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    average of p

DEFINE the target:
    orange percentage

TRANSLATE the condition:
    sum/n

SIMPLIFY / TRANSFORM:
    mean

APPLY:
    double sum/n

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`sum/n`** and the derived method **double sum/n**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `sum/n`, test **mean** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 018 --- Problem 18

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 318A --- Even
Odds](https://codeforces.com/problemset/problem/318/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`n,k`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
n,k

WHAT MUST BE FOUND:
kth in odds then evens

MATHEMATICAL OBJECTS THAT MATTER:
oddCount=(n+1)/2

CORE RELATION:
piecewise index mapping

FINAL MATHEMATICAL VIEW:
if k<=oddCount
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **piecewise index mapping**, after which the useful form is **if
k\<=oddCount**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 318A --- Even Odds (Index Mapping / Codeforces / 900)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `piecewise index mapping`. Once the story is stripped
    away, recognize **if k\<=oddCount**, then implement **formula**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
n,k

QUESTION:
kth in odds then evens
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
oddCount=(n+1)/2
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
n,k

DERIVED QUANTITIES / USEFUL STATE:
oddCount=(n+1)/2

UNKNOWN / ANSWER:
kth in odds then evens
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
piecewise index mapping
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
piecewise index mapping
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
piecewise index mapping

        ↓ simplify / rearrange / classify

if k<=oddCount
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
oddCount=(n+1)/2
      ↓
EXTRACT TARGET
kth in odds then evens
      ↓
WRITE FORMULA
piecewise index mapping
      ↓
TRANSFORM
if k<=oddCount
      ↓
ALGORITHM
formula
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`n,k`**.
2.  Ignore narrative names and rewrite the requirement using
    **`oddCount=(n+1)/2`**.
3.  Express the requirement mathematically as
    **`piecewise index mapping`**.
4.  Simplify it until the recognizable form **if k\<=oddCount** appears.
5.  Implement **formula** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          n,k

Target:         kth in odds then evens

Variables:      oddCount=(n+1)/2

Formula:        piecewise index mapping

Transform:      if k<=oddCount

Algorithm:      formula

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    oddCount=(n+1)/2

DEFINE the target:
    kth in odds then evens

TRANSLATE the condition:
    piecewise index mapping

SIMPLIFY / TRANSFORM:
    if k<=oddCount

APPLY:
    formula

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`piecewise index mapping`** and the derived method **formula**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `piecewise index mapping`, test **if k\<=oddCount** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 019 --- Problem 19

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 486A --- Calculating
Function](https://codeforces.com/problemset/problem/486/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`n`**. After removing the story/context, the real
task is:

``` text
INPUT / GIVEN:
n

WHAT MUST BE FOUND:
alternating sum -1+2-3+...

MATHEMATICAL OBJECTS THAT MATTER:
pair terms

CORE RELATION:
even n -> n/2; odd -> -(n+1)/2

FINAL MATHEMATICAL VIEW:
closed form
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **even n -\> n/2; odd -\> -(n+1)/2**, after which the useful form is
**closed form**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 486A --- Calculating Function (Formula / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `even n -> n/2; odd -> -(n+1)/2`. Once the story is
    stripped away, recognize **closed form**, then implement **parity
    branch**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
n

QUESTION:
alternating sum -1+2-3+...
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
pair terms
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
n

DERIVED QUANTITIES / USEFUL STATE:
pair terms

UNKNOWN / ANSWER:
alternating sum -1+2-3+...
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
even n -> n/2; odd -> -(n+1)/2
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
even n -> n/2; odd -> -(n+1)/2
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
even n -> n/2; odd -> -(n+1)/2

        ↓ simplify / rearrange / classify

closed form
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
pair terms
      ↓
EXTRACT TARGET
alternating sum -1+2-3+...
      ↓
WRITE FORMULA
even n -> n/2; odd -> -(n+1)/2
      ↓
TRANSFORM
closed form
      ↓
ALGORITHM
parity branch
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`n`**.
2.  Ignore narrative names and rewrite the requirement using
    **`pair terms`**.
3.  Express the requirement mathematically as
    **`even n -> n/2; odd -> -(n+1)/2`**.
4.  Simplify it until the recognizable form **closed form** appears.
5.  Implement **parity branch** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          n

Target:         alternating sum -1+2-3+...

Variables:      pair terms

Formula:        even n -> n/2; odd -> -(n+1)/2

Transform:      closed form

Algorithm:      parity branch

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    pair terms

DEFINE the target:
    alternating sum -1+2-3+...

TRANSLATE the condition:
    even n -> n/2; odd -> -(n+1)/2

SIMPLIFY / TRANSFORM:
    closed form

APPLY:
    parity branch

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`even n -> n/2; odd -> -(n+1)/2`** and the derived method **parity
    branch**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `even n -> n/2; odd -> -(n+1)/2`, test **closed form** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 020 --- Problem 20

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1399A --- Remove
Smallest](https://codeforces.com/problemset/problem/1399/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`array`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
array

WHAT MUST BE FOUND:
can repeatedly remove smaller when diff<=1

MATHEMATICAL OBJECTS THAT MATTER:
sorted adjacent gaps encode feasibility

CORE RELATION:
max adjacent diff<=1

FINAL MATHEMATICAL VIEW:
sort + check
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **max adjacent diff\<=1**, after which the useful form is **sort +
check**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1399A --- Remove Smallest (Sorting / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `max adjacent diff<=1`. Once the story is stripped
    away, recognize **sort + check**, then implement **O(nlogn)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
array

QUESTION:
can repeatedly remove smaller when diff<=1
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
sorted adjacent gaps encode feasibility
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
array

DERIVED QUANTITIES / USEFUL STATE:
sorted adjacent gaps encode feasibility

UNKNOWN / ANSWER:
can repeatedly remove smaller when diff<=1
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
max adjacent diff<=1
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
max adjacent diff<=1
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
max adjacent diff<=1

        ↓ simplify / rearrange / classify

sort + check
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
sorted adjacent gaps encode feasibility
      ↓
EXTRACT TARGET
can repeatedly remove smaller when diff<=1
      ↓
WRITE FORMULA
max adjacent diff<=1
      ↓
TRANSFORM
sort + check
      ↓
ALGORITHM
O(nlogn)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`array`**.
2.  Ignore narrative names and rewrite the requirement using
    **`sorted adjacent gaps encode feasibility`**.
3.  Express the requirement mathematically as
    **`max adjacent diff<=1`**.
4.  Simplify it until the recognizable form **sort + check** appears.
5.  Implement **O(nlogn)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          array

Target:         can repeatedly remove smaller when diff<=1

Variables:      sorted adjacent gaps encode feasibility

Formula:        max adjacent diff<=1

Transform:      sort + check

Algorithm:      O(nlogn)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    sorted adjacent gaps encode feasibility

DEFINE the target:
    can repeatedly remove smaller when diff<=1

TRANSLATE the condition:
    max adjacent diff<=1

SIMPLIFY / TRANSFORM:
    sort + check

APPLY:
    O(nlogn)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`max adjacent diff<=1`** and the derived method **O(nlogn)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `max adjacent diff<=1`, test **sort + check** immediately.

# Pattern 3 --- Bounds / Inequalities / Min-Max

## Pattern Overview

Translate 'at least', 'at most', 'minimum possible', and range wording
into inequalities. Look for a lower/upper bound and whether it is
achievable.

``` text
Different CF stories
       ↓ remove nouns
Same mathematical family
       ↓
Bounds / Inequalities / Min-Max
```

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 021 --- Problem 21

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1690A --- Print a
Pedestal](https://codeforces.com/problemset/problem/1690/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`n`**. After removing the story/context, the real
task is:

``` text
INPUT / GIVEN:
n

WHAT MUST BE FOUND:
split n into 3 positive distinct heights with middle ordering

MATHEMATICAL OBJECTS THAT MATTER:
x<y<z and sum n

CORE RELATION:
near n/3 then adjust

FINAL MATHEMATICAL VIEW:
construct around thirds
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **near n/3 then adjust**, after which the useful form is **construct
around thirds**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1690A --- Print a Pedestal (Construction / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `near n/3 then adjust`. Once the story is stripped
    away, recognize **construct around thirds**, then implement
    **formula/cases**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
n

QUESTION:
split n into 3 positive distinct heights with middle ordering
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
x<y<z and sum n
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
n

DERIVED QUANTITIES / USEFUL STATE:
x<y<z and sum n

UNKNOWN / ANSWER:
split n into 3 positive distinct heights with middle ordering
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
near n/3 then adjust
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
near n/3 then adjust
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
near n/3 then adjust

        ↓ simplify / rearrange / classify

construct around thirds
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
x<y<z and sum n
      ↓
EXTRACT TARGET
split n into 3 positive distinct heights with middle ordering
      ↓
WRITE FORMULA
near n/3 then adjust
      ↓
TRANSFORM
construct around thirds
      ↓
ALGORITHM
formula/cases
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`n`**.
2.  Ignore narrative names and rewrite the requirement using
    **`x<y<z and sum n`**.
3.  Express the requirement mathematically as
    **`near n/3 then adjust`**.
4.  Simplify it until the recognizable form **construct around thirds**
    appears.
5.  Implement **formula/cases** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          n

Target:         split n into 3 positive distinct heights with middle ordering

Variables:      x<y<z and sum n

Formula:        near n/3 then adjust

Transform:      construct around thirds

Algorithm:      formula/cases

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    x<y<z and sum n

DEFINE the target:
    split n into 3 positive distinct heights with middle ordering

TRANSLATE the condition:
    near n/3 then adjust

SIMPLIFY / TRANSFORM:
    construct around thirds

APPLY:
    formula/cases

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`near n/3 then adjust`** and the derived method **formula/cases**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `near n/3 then adjust`, test **construct around thirds**
    immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 022 --- Problem 22

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1676A ---
Lucky?](https://codeforces.com/problemset/problem/1676/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`6-digit string`**. After removing the
story/context, the real task is:

``` text
INPUT / GIVEN:
6-digit string

WHAT MUST BE FOUND:
first 3 digit sum equals last 3

MATHEMATICAL OBJECTS THAT MATTER:
S1,S2

CORE RELATION:
S1=S2

FINAL MATHEMATICAL VIEW:
direct compare
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **S1=S2**, after which the useful form is **direct compare**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1676A --- Lucky? (Equation / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `S1=S2`. Once the story is stripped away, recognize
    **direct compare**, then implement **O(1)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
6-digit string

QUESTION:
first 3 digit sum equals last 3
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
S1,S2
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
6-digit string

DERIVED QUANTITIES / USEFUL STATE:
S1,S2

UNKNOWN / ANSWER:
first 3 digit sum equals last 3
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
S1=S2
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
S1=S2
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
S1=S2

        ↓ simplify / rearrange / classify

direct compare
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
S1,S2
      ↓
EXTRACT TARGET
first 3 digit sum equals last 3
      ↓
WRITE FORMULA
S1=S2
      ↓
TRANSFORM
direct compare
      ↓
ALGORITHM
O(1)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`6-digit string`**.
2.  Ignore narrative names and rewrite the requirement using
    **`S1,S2`**.
3.  Express the requirement mathematically as **`S1=S2`**.
4.  Simplify it until the recognizable form **direct compare** appears.
5.  Implement **O(1)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          6-digit string

Target:         first 3 digit sum equals last 3

Variables:      S1,S2

Formula:        S1=S2

Transform:      direct compare

Algorithm:      O(1)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    S1,S2

DEFINE the target:
    first 3 digit sum equals last 3

TRANSLATE the condition:
    S1=S2

SIMPLIFY / TRANSFORM:
    direct compare

APPLY:
    O(1)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`S1=S2`** and the derived method **O(1)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `S1=S2`, test **direct compare** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 023 --- Problem 23

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1742B ---
Increasing](https://codeforces.com/problemset/problem/1742/B)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`array`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
array

WHAT MUST BE FOUND:
can permute to strictly increasing

MATHEMATICAL OBJECTS THAT MATTER:
strictly increasing permutation iff all distinct

CORE RELATION:
freq<=1

FINAL MATHEMATICAL VIEW:
set size=n
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **freq\<=1**, after which the useful form is **set size=n**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1742B --- Increasing (Distinctness / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `freq<=1`. Once the story is stripped away, recognize
    **set size=n**, then implement **set**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
array

QUESTION:
can permute to strictly increasing
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
strictly increasing permutation iff all distinct
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
array

DERIVED QUANTITIES / USEFUL STATE:
strictly increasing permutation iff all distinct

UNKNOWN / ANSWER:
can permute to strictly increasing
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
freq<=1
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
freq<=1
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
freq<=1

        ↓ simplify / rearrange / classify

set size=n
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
strictly increasing permutation iff all distinct
      ↓
EXTRACT TARGET
can permute to strictly increasing
      ↓
WRITE FORMULA
freq<=1
      ↓
TRANSFORM
set size=n
      ↓
ALGORITHM
set
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`array`**.
2.  Ignore narrative names and rewrite the requirement using
    **`strictly increasing permutation iff all distinct`**.
3.  Express the requirement mathematically as **`freq<=1`**.
4.  Simplify it until the recognizable form **set size=n** appears.
5.  Implement **set** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          array

Target:         can permute to strictly increasing

Variables:      strictly increasing permutation iff all distinct

Formula:        freq<=1

Transform:      set size=n

Algorithm:      set

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    strictly increasing permutation iff all distinct

DEFINE the target:
    can permute to strictly increasing

TRANSLATE the condition:
    freq<=1

SIMPLIFY / TRANSFORM:
    set size=n

APPLY:
    set

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`freq<=1`** and the derived method **set**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `freq<=1`, test **set size=n** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 024 --- Problem 24

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1791A --- Codeforces
Checking](https://codeforces.com/problemset/problem/1791/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`char c`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
char c

WHAT MUST BE FOUND:
whether c belongs to 'codeforces'

MATHEMATICAL OBJECTS THAT MATTER:
c ∈ fixed set

CORE RELATION:
find char

FINAL MATHEMATICAL VIEW:
membership
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **find char**, after which the useful form is **membership**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1791A --- Codeforces Checking (Membership / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `find char`. Once the story is stripped away, recognize
    **membership**, then implement **string find**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
char c

QUESTION:
whether c belongs to 'codeforces'
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
c ∈ fixed set
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
char c

DERIVED QUANTITIES / USEFUL STATE:
c ∈ fixed set

UNKNOWN / ANSWER:
whether c belongs to 'codeforces'
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
find char
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
find char
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
find char

        ↓ simplify / rearrange / classify

membership
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
c ∈ fixed set
      ↓
EXTRACT TARGET
whether c belongs to 'codeforces'
      ↓
WRITE FORMULA
find char
      ↓
TRANSFORM
membership
      ↓
ALGORITHM
string find
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`char c`**.
2.  Ignore narrative names and rewrite the requirement using
    **`c ∈ fixed set`**.
3.  Express the requirement mathematically as **`find char`**.
4.  Simplify it until the recognizable form **membership** appears.
5.  Implement **string find** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          char c

Target:         whether c belongs to 'codeforces'

Variables:      c ∈ fixed set

Formula:        find char

Transform:      membership

Algorithm:      string find

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    c ∈ fixed set

DEFINE the target:
    whether c belongs to 'codeforces'

TRANSLATE the condition:
    find char

SIMPLIFY / TRANSFORM:
    membership

APPLY:
    string find

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`find char`** and the derived method **string find**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `find char`, test **membership** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 025 --- Problem 25

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1829A --- Love
Story](https://codeforces.com/problemset/problem/1829/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`string`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
string

WHAT MUST BE FOUND:
positions differing from 'codeforces'

MATHEMATICAL OBJECTS THAT MATTER:
indicator [s[i]!=t[i]]

CORE RELATION:
sum indicators

FINAL MATHEMATICAL VIEW:
Hamming distance
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **sum indicators**, after which the useful form is **Hamming
distance**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1829A --- Love Story (Hamming Distance / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `sum indicators`. Once the story is stripped away,
    recognize **Hamming distance**, then implement **10 checks**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
string

QUESTION:
positions differing from 'codeforces'
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
indicator [s[i]!=t[i]]
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
string

DERIVED QUANTITIES / USEFUL STATE:
indicator [s[i]!=t[i]]

UNKNOWN / ANSWER:
positions differing from 'codeforces'
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
sum indicators
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
sum indicators
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
sum indicators

        ↓ simplify / rearrange / classify

Hamming distance
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
indicator [s[i]!=t[i]]
      ↓
EXTRACT TARGET
positions differing from 'codeforces'
      ↓
WRITE FORMULA
sum indicators
      ↓
TRANSFORM
Hamming distance
      ↓
ALGORITHM
10 checks
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`string`**.
2.  Ignore narrative names and rewrite the requirement using
    **`indicator [s[i]!=t[i]]`**.
3.  Express the requirement mathematically as **`sum indicators`**.
4.  Simplify it until the recognizable form **Hamming distance**
    appears.
5.  Implement **10 checks** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          string

Target:         positions differing from 'codeforces'

Variables:      indicator [s[i]!=t[i]]

Formula:        sum indicators

Transform:      Hamming distance

Algorithm:      10 checks

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    indicator [s[i]!=t[i]]

DEFINE the target:
    positions differing from 'codeforces'

TRANSLATE the condition:
    sum indicators

SIMPLIFY / TRANSFORM:
    Hamming distance

APPLY:
    10 checks

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`sum indicators`** and the derived method **10 checks**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `sum indicators`, test **Hamming distance** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 026 --- Problem 26

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1873A --- Short
Sort](https://codeforces.com/problemset/problem/1873/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`3-char string`**. After removing the
story/context, the real task is:

``` text
INPUT / GIVEN:
3-char string

WHAT MUST BE FOUND:
can sort with <=1 swap

MATHEMATICAL OBJECTS THAT MATTER:
target='abc'

CORE RELATION:
mismatch count 0 or 2

FINAL MATHEMATICAL VIEW:
compare permutations
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **mismatch count 0 or 2**, after which the useful form is **compare
permutations**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1873A --- Short Sort (Permutation / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `mismatch count 0 or 2`. Once the story is stripped
    away, recognize **compare permutations**, then implement **direct**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
3-char string

QUESTION:
can sort with <=1 swap
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
target='abc'
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
3-char string

DERIVED QUANTITIES / USEFUL STATE:
target='abc'

UNKNOWN / ANSWER:
can sort with <=1 swap
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
mismatch count 0 or 2
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
mismatch count 0 or 2
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
mismatch count 0 or 2

        ↓ simplify / rearrange / classify

compare permutations
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
target='abc'
      ↓
EXTRACT TARGET
can sort with <=1 swap
      ↓
WRITE FORMULA
mismatch count 0 or 2
      ↓
TRANSFORM
compare permutations
      ↓
ALGORITHM
direct
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`3-char string`**.
2.  Ignore narrative names and rewrite the requirement using
    **`target='abc'`**.
3.  Express the requirement mathematically as
    **`mismatch count 0 or 2`**.
4.  Simplify it until the recognizable form **compare permutations**
    appears.
5.  Implement **direct** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          3-char string

Target:         can sort with <=1 swap

Variables:      target='abc'

Formula:        mismatch count 0 or 2

Transform:      compare permutations

Algorithm:      direct

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    target='abc'

DEFINE the target:
    can sort with <=1 swap

TRANSLATE the condition:
    mismatch count 0 or 2

SIMPLIFY / TRANSFORM:
    compare permutations

APPLY:
    direct

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`mismatch count 0 or 2`** and the derived method **direct**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `mismatch count 0 or 2`, test **compare permutations** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 027 --- Problem 27

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1729A --- Two
Elevators](https://codeforces.com/problemset/problem/1729/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`a,b,c`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
a,b,c

WHAT MUST BE FOUND:
which elevator reaches floor1 sooner

MATHEMATICAL OBJECTS THAT MATTER:
t1=a-1, t2=|b-c|+c-1

CORE RELATION:
compare times

FINAL MATHEMATICAL VIEW:
min comparison
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **compare times**, after which the useful form is **min comparison**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1729A --- Two Elevators (Distance / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `compare times`. Once the story is stripped away,
    recognize **min comparison**, then implement **O(1)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
a,b,c

QUESTION:
which elevator reaches floor1 sooner
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
t1=a-1, t2=|b-c|+c-1
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
a,b,c

DERIVED QUANTITIES / USEFUL STATE:
t1=a-1, t2=|b-c|+c-1

UNKNOWN / ANSWER:
which elevator reaches floor1 sooner
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
compare times
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
compare times
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
compare times

        ↓ simplify / rearrange / classify

min comparison
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
t1=a-1, t2=|b-c|+c-1
      ↓
EXTRACT TARGET
which elevator reaches floor1 sooner
      ↓
WRITE FORMULA
compare times
      ↓
TRANSFORM
min comparison
      ↓
ALGORITHM
O(1)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`a,b,c`**.
2.  Ignore narrative names and rewrite the requirement using
    **`t1=a-1, t2=|b-c|+c-1`**.
3.  Express the requirement mathematically as **`compare times`**.
4.  Simplify it until the recognizable form **min comparison** appears.
5.  Implement **O(1)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          a,b,c

Target:         which elevator reaches floor1 sooner

Variables:      t1=a-1, t2=|b-c|+c-1

Formula:        compare times

Transform:      min comparison

Algorithm:      O(1)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    t1=a-1, t2=|b-c|+c-1

DEFINE the target:
    which elevator reaches floor1 sooner

TRANSLATE the condition:
    compare times

SIMPLIFY / TRANSFORM:
    min comparison

APPLY:
    O(1)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`compare times`** and the derived method **O(1)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `compare times`, test **min comparison** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 028 --- Problem 28

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1805A --- We Need the
Zero](https://codeforces.com/problemset/problem/1805/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`array`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
array

WHAT MUST BE FOUND:
find x making xor transformed zero

MATHEMATICAL OBJECTS THAT MATTER:
xor(a_i xor x)

CORE RELATION:
parity of n controls x contribution

FINAL MATHEMATICAL VIEW:
derive xor equation
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **parity of n controls x contribution**, after which the useful form
is **derive xor equation**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1805A --- We Need the Zero (XOR/Bounds / Codeforces / 900)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `parity of n controls x contribution`. Once the story
    is stripped away, recognize **derive xor equation**, then implement
    **xor all**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
array

QUESTION:
find x making xor transformed zero
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
xor(a_i xor x)
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
array

DERIVED QUANTITIES / USEFUL STATE:
xor(a_i xor x)

UNKNOWN / ANSWER:
find x making xor transformed zero
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
parity of n controls x contribution
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
parity of n controls x contribution
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
parity of n controls x contribution

        ↓ simplify / rearrange / classify

derive xor equation
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
xor(a_i xor x)
      ↓
EXTRACT TARGET
find x making xor transformed zero
      ↓
WRITE FORMULA
parity of n controls x contribution
      ↓
TRANSFORM
derive xor equation
      ↓
ALGORITHM
xor all
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`array`**.
2.  Ignore narrative names and rewrite the requirement using
    **`xor(a_i xor x)`**.
3.  Express the requirement mathematically as
    **`parity of n controls x contribution`**.
4.  Simplify it until the recognizable form **derive xor equation**
    appears.
5.  Implement **xor all** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          array

Target:         find x making xor transformed zero

Variables:      xor(a_i xor x)

Formula:        parity of n controls x contribution

Transform:      derive xor equation

Algorithm:      xor all

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    xor(a_i xor x)

DEFINE the target:
    find x making xor transformed zero

TRANSLATE the condition:
    parity of n controls x contribution

SIMPLIFY / TRANSFORM:
    derive xor equation

APPLY:
    xor all

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`parity of n controls x contribution`** and the derived method
    **xor all**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `parity of n controls x contribution`, test **derive xor equation**
    immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 029 --- Problem 29

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1858A ---
Buttons](https://codeforces.com/problemset/problem/1858/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`a,b,c`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
a,b,c

WHAT MUST BE FOUND:
winner with shared buttons

MATHEMATICAL OBJECTS THAT MATTER:
shared moves alternate

CORE RELATION:
parity of c decides who gets extra

FINAL MATHEMATICAL VIEW:
compare effective counts
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **parity of c decides who gets extra**, after which the useful form
is **compare effective counts**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1858A --- Buttons (Game/Counting / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `parity of c decides who gets extra`. Once the story is
    stripped away, recognize **compare effective counts**, then
    implement **casework**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
a,b,c

QUESTION:
winner with shared buttons
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
shared moves alternate
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
a,b,c

DERIVED QUANTITIES / USEFUL STATE:
shared moves alternate

UNKNOWN / ANSWER:
winner with shared buttons
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
parity of c decides who gets extra
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
parity of c decides who gets extra
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
parity of c decides who gets extra

        ↓ simplify / rearrange / classify

compare effective counts
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
shared moves alternate
      ↓
EXTRACT TARGET
winner with shared buttons
      ↓
WRITE FORMULA
parity of c decides who gets extra
      ↓
TRANSFORM
compare effective counts
      ↓
ALGORITHM
casework
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`a,b,c`**.
2.  Ignore narrative names and rewrite the requirement using
    **`shared moves alternate`**.
3.  Express the requirement mathematically as
    **`parity of c decides who gets extra`**.
4.  Simplify it until the recognizable form **compare effective counts**
    appears.
5.  Implement **casework** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          a,b,c

Target:         winner with shared buttons

Variables:      shared moves alternate

Formula:        parity of c decides who gets extra

Transform:      compare effective counts

Algorithm:      casework

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    shared moves alternate

DEFINE the target:
    winner with shared buttons

TRANSLATE the condition:
    parity of c decides who gets extra

SIMPLIFY / TRANSFORM:
    compare effective counts

APPLY:
    casework

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`parity of c decides who gets extra`** and the derived method
    **casework**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `parity of c decides who gets extra`, test **compare effective
    counts** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 030 --- Problem 30

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1899A --- Game with
Integers](https://codeforces.com/problemset/problem/1899/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`n`**. After removing the story/context, the real
task is:

``` text
INPUT / GIVEN:
n

WHAT MUST BE FOUND:
winner under ±1 and divisibility by3

MATHEMATICAL OBJECTS THAT MATTER:
positions mod3

CORE RELATION:
n%3==0 is losing/winning condition per rules

FINAL MATHEMATICAL VIEW:
reduce to residue
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **n%3==0 is losing/winning condition per rules**, after which the
useful form is **reduce to residue**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1899A --- Game with Integers (Modulo / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `n%3==0 is losing/winning condition per rules`. Once
    the story is stripped away, recognize **reduce to residue**, then
    implement **O(1)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
n

QUESTION:
winner under ±1 and divisibility by3
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
positions mod3
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
n

DERIVED QUANTITIES / USEFUL STATE:
positions mod3

UNKNOWN / ANSWER:
winner under ±1 and divisibility by3
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
n%3==0 is losing/winning condition per rules
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
n%3==0 is losing/winning condition per rules
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
n%3==0 is losing/winning condition per rules

        ↓ simplify / rearrange / classify

reduce to residue
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
positions mod3
      ↓
EXTRACT TARGET
winner under ±1 and divisibility by3
      ↓
WRITE FORMULA
n%3==0 is losing/winning condition per rules
      ↓
TRANSFORM
reduce to residue
      ↓
ALGORITHM
O(1)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`n`**.
2.  Ignore narrative names and rewrite the requirement using
    **`positions mod3`**.
3.  Express the requirement mathematically as
    **`n%3==0 is losing/winning condition per rules`**.
4.  Simplify it until the recognizable form **reduce to residue**
    appears.
5.  Implement **O(1)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          n

Target:         winner under ±1 and divisibility by3

Variables:      positions mod3

Formula:        n%3==0 is losing/winning condition per rules

Transform:      reduce to residue

Algorithm:      O(1)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    positions mod3

DEFINE the target:
    winner under ±1 and divisibility by3

TRANSLATE the condition:
    n%3==0 is losing/winning condition per rules

SIMPLIFY / TRANSFORM:
    reduce to residue

APPLY:
    O(1)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`n%3==0 is losing/winning condition per rules`** and the derived
    method **O(1)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `n%3==0 is losing/winning condition per rules`, test **reduce to
    residue** immediately.

# Pattern 4 --- Parity Modeling

## Pattern Overview

Replace values by `x % 2` whenever only odd/even behavior matters. For
sums, the parity is determined by the number of odd addends.

``` text
Different CF stories
       ↓ remove nouns
Same mathematical family
       ↓
Parity Modeling
```

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 031 --- Problem 31

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 4A ---
Watermelon](https://codeforces.com/problemset/problem/4/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`w`**. After removing the story/context, the real
task is:

``` text
INPUT / GIVEN:
w

WHAT MUST BE FOUND:
split into two positive even parts

MATHEMATICAL OBJECTS THAT MATTER:
w=a+b, a,b even >=2

CORE RELATION:
w even and w>2

FINAL MATHEMATICAL VIEW:
parity + positivity
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **w even and w\>2**, after which the useful form is **parity +
positivity**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 4A --- Watermelon (Parity / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `w even and w>2`. Once the story is stripped away,
    recognize **parity + positivity**, then implement **w%2==0&&w\>2**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
w

QUESTION:
split into two positive even parts
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
w=a+b, a,b even >=2
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
w

DERIVED QUANTITIES / USEFUL STATE:
w=a+b, a,b even >=2

UNKNOWN / ANSWER:
split into two positive even parts
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
w even and w>2
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
w even and w>2
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
w even and w>2

        ↓ simplify / rearrange / classify

parity + positivity
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
w=a+b, a,b even >=2
      ↓
EXTRACT TARGET
split into two positive even parts
      ↓
WRITE FORMULA
w even and w>2
      ↓
TRANSFORM
parity + positivity
      ↓
ALGORITHM
w%2==0&&w>2
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`w`**.
2.  Ignore narrative names and rewrite the requirement using
    **`w=a+b, a,b even >=2`**.
3.  Express the requirement mathematically as **`w even and w>2`**.
4.  Simplify it until the recognizable form **parity + positivity**
    appears.
5.  Implement **w%2==0&&w\>2** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          w

Target:         split into two positive even parts

Variables:      w=a+b, a,b even >=2

Formula:        w even and w>2

Transform:      parity + positivity

Algorithm:      w%2==0&&w>2

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    w=a+b, a,b even >=2

DEFINE the target:
    split into two positive even parts

TRANSLATE the condition:
    w even and w>2

SIMPLIFY / TRANSFORM:
    parity + positivity

APPLY:
    w%2==0&&w>2

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`w even and w>2`** and the derived method **w%2==0&&w\>2**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `w even and w>2`, test **parity + positivity** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 032 --- Problem 32

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1296A --- Array with Odd
Sum](https://codeforces.com/problemset/problem/1296/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`array`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
array

WHAT MUST BE FOUND:
whether required odd-sum selection exists

MATHEMATICAL OBJECTS THAT MATTER:
sum odd iff odd count odd

CORE RELATION:
reduce values to parity

FINAL MATHEMATICAL VIEW:
count odd/even
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **reduce values to parity**, after which the useful form is **count
odd/even**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1296A --- Array with Odd Sum (Parity / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `reduce values to parity`. Once the story is stripped
    away, recognize **count odd/even**, then implement **casework**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
array

QUESTION:
whether required odd-sum selection exists
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
sum odd iff odd count odd
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
array

DERIVED QUANTITIES / USEFUL STATE:
sum odd iff odd count odd

UNKNOWN / ANSWER:
whether required odd-sum selection exists
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
reduce values to parity
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
reduce values to parity
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
reduce values to parity

        ↓ simplify / rearrange / classify

count odd/even
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
sum odd iff odd count odd
      ↓
EXTRACT TARGET
whether required odd-sum selection exists
      ↓
WRITE FORMULA
reduce values to parity
      ↓
TRANSFORM
count odd/even
      ↓
ALGORITHM
casework
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`array`**.
2.  Ignore narrative names and rewrite the requirement using
    **`sum odd iff odd count odd`**.
3.  Express the requirement mathematically as
    **`reduce values to parity`**.
4.  Simplify it until the recognizable form **count odd/even** appears.
5.  Implement **casework** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          array

Target:         whether required odd-sum selection exists

Variables:      sum odd iff odd count odd

Formula:        reduce values to parity

Transform:      count odd/even

Algorithm:      casework

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    sum odd iff odd count odd

DEFINE the target:
    whether required odd-sum selection exists

TRANSLATE the condition:
    reduce values to parity

SIMPLIFY / TRANSFORM:
    count odd/even

APPLY:
    casework

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`reduce values to parity`** and the derived method **casework**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `reduce values to parity`, test **count odd/even** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 033 --- Problem 33

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1857A --- Array
Coloring](https://codeforces.com/problemset/problem/1857/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`array`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
array

WHAT MUST BE FOUND:
whether can split into two groups with equal parity sums

MATHEMATICAL OBJECTS THAT MATTER:
total sum must be even

CORE RELATION:
sum%2=0

FINAL MATHEMATICAL VIEW:
parity invariant
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **sum%2=0**, after which the useful form is **parity invariant**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1857A --- Array Coloring (Parity / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `sum%2=0`. Once the story is stripped away, recognize
    **parity invariant**, then implement **sum check**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
array

QUESTION:
whether can split into two groups with equal parity sums
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
total sum must be even
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
array

DERIVED QUANTITIES / USEFUL STATE:
total sum must be even

UNKNOWN / ANSWER:
whether can split into two groups with equal parity sums
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
sum%2=0
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
sum%2=0
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
sum%2=0

        ↓ simplify / rearrange / classify

parity invariant
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
total sum must be even
      ↓
EXTRACT TARGET
whether can split into two groups with equal parity sums
      ↓
WRITE FORMULA
sum%2=0
      ↓
TRANSFORM
parity invariant
      ↓
ALGORITHM
sum check
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`array`**.
2.  Ignore narrative names and rewrite the requirement using
    **`total sum must be even`**.
3.  Express the requirement mathematically as **`sum%2=0`**.
4.  Simplify it until the recognizable form **parity invariant**
    appears.
5.  Implement **sum check** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          array

Target:         whether can split into two groups with equal parity sums

Variables:      total sum must be even

Formula:        sum%2=0

Transform:      parity invariant

Algorithm:      sum check

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    total sum must be even

DEFINE the target:
    whether can split into two groups with equal parity sums

TRANSLATE the condition:
    sum%2=0

SIMPLIFY / TRANSFORM:
    parity invariant

APPLY:
    sum check

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`sum%2=0`** and the derived method **sum check**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `sum%2=0`, test **parity invariant** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 034 --- Problem 34

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1834A --- Unit
Array](https://codeforces.com/problemset/problem/1834/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`±1 array`**. After removing the story/context,
the real task is:

``` text
INPUT / GIVEN:
±1 array

WHAT MUST BE FOUND:
minimum flips to satisfy sum>=0 and product=1

MATHEMATICAL OBJECTS THAT MATTER:
product depends on #(-1) parity

CORE RELATION:
fix sum then parity

FINAL MATHEMATICAL VIEW:
count negatives
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **fix sum then parity**, after which the useful form is **count
negatives**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1834A --- Unit Array (Parity/Greedy / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `fix sum then parity`. Once the story is stripped away,
    recognize **count negatives**, then implement **formula/loop**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
±1 array

QUESTION:
minimum flips to satisfy sum>=0 and product=1
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
product depends on #(-1) parity
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
±1 array

DERIVED QUANTITIES / USEFUL STATE:
product depends on #(-1) parity

UNKNOWN / ANSWER:
minimum flips to satisfy sum>=0 and product=1
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
fix sum then parity
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
fix sum then parity
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
fix sum then parity

        ↓ simplify / rearrange / classify

count negatives
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
product depends on #(-1) parity
      ↓
EXTRACT TARGET
minimum flips to satisfy sum>=0 and product=1
      ↓
WRITE FORMULA
fix sum then parity
      ↓
TRANSFORM
count negatives
      ↓
ALGORITHM
formula/loop
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`±1 array`**.
2.  Ignore narrative names and rewrite the requirement using
    **`product depends on #(-1) parity`**.
3.  Express the requirement mathematically as **`fix sum then parity`**.
4.  Simplify it until the recognizable form **count negatives** appears.
5.  Implement **formula/loop** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          ±1 array

Target:         minimum flips to satisfy sum>=0 and product=1

Variables:      product depends on #(-1) parity

Formula:        fix sum then parity

Transform:      count negatives

Algorithm:      formula/loop

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    product depends on #(-1) parity

DEFINE the target:
    minimum flips to satisfy sum>=0 and product=1

TRANSLATE the condition:
    fix sum then parity

SIMPLIFY / TRANSFORM:
    count negatives

APPLY:
    formula/loop

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`fix sum then parity`** and the derived method **formula/loop**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `fix sum then parity`, test **count negatives** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 035 --- Problem 35

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1367B --- Even
Array](https://codeforces.com/problemset/problem/1367/B)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`array`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
array

WHAT MUST BE FOUND:
minimum swaps so a[i]%2=i%2

MATHEMATICAL OBJECTS THAT MATTER:
mismatches of two types must balance

CORE RELATION:
badEven=badOdd

FINAL MATHEMATICAL VIEW:
answer mismatches/2
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **badEven=badOdd**, after which the useful form is **answer
mismatches/2**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1367B --- Even Array (Parity / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `badEven=badOdd`. Once the story is stripped away,
    recognize **answer mismatches/2**, then implement **count**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
array

QUESTION:
minimum swaps so a[i]%2=i%2
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
mismatches of two types must balance
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
array

DERIVED QUANTITIES / USEFUL STATE:
mismatches of two types must balance

UNKNOWN / ANSWER:
minimum swaps so a[i]%2=i%2
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
badEven=badOdd
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
badEven=badOdd
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
badEven=badOdd

        ↓ simplify / rearrange / classify

answer mismatches/2
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
mismatches of two types must balance
      ↓
EXTRACT TARGET
minimum swaps so a[i]%2=i%2
      ↓
WRITE FORMULA
badEven=badOdd
      ↓
TRANSFORM
answer mismatches/2
      ↓
ALGORITHM
count
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`array`**.
2.  Ignore narrative names and rewrite the requirement using
    **`mismatches of two types must balance`**.
3.  Express the requirement mathematically as **`badEven=badOdd`**.
4.  Simplify it until the recognizable form **answer mismatches/2**
    appears.
5.  Implement **count** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          array

Target:         minimum swaps so a[i]%2=i%2

Variables:      mismatches of two types must balance

Formula:        badEven=badOdd

Transform:      answer mismatches/2

Algorithm:      count

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    mismatches of two types must balance

DEFINE the target:
    minimum swaps so a[i]%2=i%2

TRANSLATE the condition:
    badEven=badOdd

SIMPLIFY / TRANSFORM:
    answer mismatches/2

APPLY:
    count

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`badEven=badOdd`** and the derived method **count**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `badEven=badOdd`, test **answer mismatches/2** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 036 --- Problem 36

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1475A --- Odd
Divisor](https://codeforces.com/problemset/problem/1475/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`n`**. After removing the story/context, the real
task is:

``` text
INPUT / GIVEN:
n

WHAT MUST BE FOUND:
has odd divisor >1

MATHEMATICAL OBJECTS THAT MATTER:
n=2^k*m odd

CORE RELATION:
m>1

FINAL MATHEMATICAL VIEW:
not power of two
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **m\>1**, after which the useful form is **not power of two**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1475A --- Odd Divisor (Number Theory / Codeforces / 900)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `m>1`. Once the story is stripped away, recognize **not
    power of two**, then implement **strip twos**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
n

QUESTION:
has odd divisor >1
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
n=2^k*m odd
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
n

DERIVED QUANTITIES / USEFUL STATE:
n=2^k*m odd

UNKNOWN / ANSWER:
has odd divisor >1
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
m>1
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
m>1
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
m>1

        ↓ simplify / rearrange / classify

not power of two
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
n=2^k*m odd
      ↓
EXTRACT TARGET
has odd divisor >1
      ↓
WRITE FORMULA
m>1
      ↓
TRANSFORM
not power of two
      ↓
ALGORITHM
strip twos
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`n`**.
2.  Ignore narrative names and rewrite the requirement using
    **`n=2^k*m odd`**.
3.  Express the requirement mathematically as **`m>1`**.
4.  Simplify it until the recognizable form **not power of two**
    appears.
5.  Implement **strip twos** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          n

Target:         has odd divisor >1

Variables:      n=2^k*m odd

Formula:        m>1

Transform:      not power of two

Algorithm:      strip twos

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    n=2^k*m odd

DEFINE the target:
    has odd divisor >1

TRANSLATE the condition:
    m>1

SIMPLIFY / TRANSFORM:
    not power of two

APPLY:
    strip twos

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`m>1`** and the derived method **strip twos**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `m>1`, test **not power of two** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 037 --- Problem 37

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1669C --- Odd/Even
Increments](https://codeforces.com/problemset/problem/1669/C)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`array`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
array

WHAT MUST BE FOUND:
can equalize via parity-constrained increments

MATHEMATICAL OBJECTS THAT MATTER:
all elements need same parity class relation

CORE RELATION:
check parity consistency

FINAL MATHEMATICAL VIEW:
parity only
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **check parity consistency**, after which the useful form is **parity
only**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1669C --- Odd/Even Increments (Parity / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `check parity consistency`. Once the story is stripped
    away, recognize **parity only**, then implement **scan**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
array

QUESTION:
can equalize via parity-constrained increments
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
all elements need same parity class relation
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
array

DERIVED QUANTITIES / USEFUL STATE:
all elements need same parity class relation

UNKNOWN / ANSWER:
can equalize via parity-constrained increments
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
check parity consistency
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
check parity consistency
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
check parity consistency

        ↓ simplify / rearrange / classify

parity only
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
all elements need same parity class relation
      ↓
EXTRACT TARGET
can equalize via parity-constrained increments
      ↓
WRITE FORMULA
check parity consistency
      ↓
TRANSFORM
parity only
      ↓
ALGORITHM
scan
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`array`**.
2.  Ignore narrative names and rewrite the requirement using
    **`all elements need same parity class relation`**.
3.  Express the requirement mathematically as
    **`check parity consistency`**.
4.  Simplify it until the recognizable form **parity only** appears.
5.  Implement **scan** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          array

Target:         can equalize via parity-constrained increments

Variables:      all elements need same parity class relation

Formula:        check parity consistency

Transform:      parity only

Algorithm:      scan

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    all elements need same parity class relation

DEFINE the target:
    can equalize via parity-constrained increments

TRANSLATE the condition:
    check parity consistency

SIMPLIFY / TRANSFORM:
    parity only

APPLY:
    scan

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`check parity consistency`** and the derived method **scan**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `check parity consistency`, test **parity only** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 038 --- Problem 38

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1624A --- Plus One on the
Subset](https://codeforces.com/problemset/problem/1624/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`array`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
array

WHAT MUST BE FOUND:
minimum operations to equalize by incrementing subset

MATHEMATICAL OBJECTS THAT MATTER:
raise to max

CORE RELATION:
answer=max-min

FINAL MATHEMATICAL VIEW:
range width
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **answer=max-min**, after which the useful form is **range width**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1624A --- Plus One on the Subset (Difference / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `answer=max-min`. Once the story is stripped away,
    recognize **range width**, then implement **min/max**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
array

QUESTION:
minimum operations to equalize by incrementing subset
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
raise to max
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
array

DERIVED QUANTITIES / USEFUL STATE:
raise to max

UNKNOWN / ANSWER:
minimum operations to equalize by incrementing subset
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
answer=max-min
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
answer=max-min
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
answer=max-min

        ↓ simplify / rearrange / classify

range width
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
raise to max
      ↓
EXTRACT TARGET
minimum operations to equalize by incrementing subset
      ↓
WRITE FORMULA
answer=max-min
      ↓
TRANSFORM
range width
      ↓
ALGORITHM
min/max
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`array`**.
2.  Ignore narrative names and rewrite the requirement using
    **`raise to max`**.
3.  Express the requirement mathematically as **`answer=max-min`**.
4.  Simplify it until the recognizable form **range width** appears.
5.  Implement **min/max** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          array

Target:         minimum operations to equalize by incrementing subset

Variables:      raise to max

Formula:        answer=max-min

Transform:      range width

Algorithm:      min/max

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    raise to max

DEFINE the target:
    minimum operations to equalize by incrementing subset

TRANSLATE the condition:
    answer=max-min

SIMPLIFY / TRANSFORM:
    range width

APPLY:
    min/max

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`answer=max-min`** and the derived method **min/max**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `answer=max-min`, test **range width** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 039 --- Problem 39

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1788A --- One and
Two](https://codeforces.com/problemset/problem/1788/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`1/2 array`**. After removing the story/context,
the real task is:

``` text
INPUT / GIVEN:
1/2 array

WHAT MUST BE FOUND:
split so products equal

MATHEMATICAL OBJECTS THAT MATTER:
equal #twos on both sides

CORE RELATION:
total twos even

FINAL MATHEMATICAL VIEW:
find half twos
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **total twos even**, after which the useful form is **find half
twos**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1788A --- One and Two (Product/Parity / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `total twos even`. Once the story is stripped away,
    recognize **find half twos**, then implement **count**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
1/2 array

QUESTION:
split so products equal
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
equal #twos on both sides
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
1/2 array

DERIVED QUANTITIES / USEFUL STATE:
equal #twos on both sides

UNKNOWN / ANSWER:
split so products equal
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
total twos even
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
total twos even
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
total twos even

        ↓ simplify / rearrange / classify

find half twos
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
equal #twos on both sides
      ↓
EXTRACT TARGET
split so products equal
      ↓
WRITE FORMULA
total twos even
      ↓
TRANSFORM
find half twos
      ↓
ALGORITHM
count
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`1/2 array`**.
2.  Ignore narrative names and rewrite the requirement using
    **`equal #twos on both sides`**.
3.  Express the requirement mathematically as **`total twos even`**.
4.  Simplify it until the recognizable form **find half twos** appears.
5.  Implement **count** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          1/2 array

Target:         split so products equal

Variables:      equal #twos on both sides

Formula:        total twos even

Transform:      find half twos

Algorithm:      count

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    equal #twos on both sides

DEFINE the target:
    split so products equal

TRANSLATE the condition:
    total twos even

SIMPLIFY / TRANSFORM:
    find half twos

APPLY:
    count

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`total twos even`** and the derived method **count**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `total twos even`, test **find half twos** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 040 --- Problem 40

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1845A --- Forbidden
Integer](https://codeforces.com/problemset/problem/1845/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`n,k,x`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
n,k,x

WHAT MUST BE FOUND:
represent n as sum of 1..k excluding x

MATHEMATICAL OBJECTS THAT MATTER:
choose repeated small allowed values

CORE RELATION:
cases x!=1, else 2/3

FINAL MATHEMATICAL VIEW:
construct feasibility
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **cases x!=1, else 2/3**, after which the useful form is **construct
feasibility**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1845A --- Forbidden Integer (Constructive / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `cases x!=1, else 2/3`. Once the story is stripped
    away, recognize **construct feasibility**, then implement
    **casework**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
n,k,x

QUESTION:
represent n as sum of 1..k excluding x
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
choose repeated small allowed values
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
n,k,x

DERIVED QUANTITIES / USEFUL STATE:
choose repeated small allowed values

UNKNOWN / ANSWER:
represent n as sum of 1..k excluding x
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
cases x!=1, else 2/3
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
cases x!=1, else 2/3
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
cases x!=1, else 2/3

        ↓ simplify / rearrange / classify

construct feasibility
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
choose repeated small allowed values
      ↓
EXTRACT TARGET
represent n as sum of 1..k excluding x
      ↓
WRITE FORMULA
cases x!=1, else 2/3
      ↓
TRANSFORM
construct feasibility
      ↓
ALGORITHM
casework
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`n,k,x`**.
2.  Ignore narrative names and rewrite the requirement using
    **`choose repeated small allowed values`**.
3.  Express the requirement mathematically as
    **`cases x!=1, else 2/3`**.
4.  Simplify it until the recognizable form **construct feasibility**
    appears.
5.  Implement **casework** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          n,k,x

Target:         represent n as sum of 1..k excluding x

Variables:      choose repeated small allowed values

Formula:        cases x!=1, else 2/3

Transform:      construct feasibility

Algorithm:      casework

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    choose repeated small allowed values

DEFINE the target:
    represent n as sum of 1..k excluding x

TRANSLATE the condition:
    cases x!=1, else 2/3

SIMPLIFY / TRANSFORM:
    construct feasibility

APPLY:
    casework

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`cases x!=1, else 2/3`** and the derived method **casework**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `cases x!=1, else 2/3`, test **construct feasibility** immediately.

# Pattern 5 --- Divisibility / GCD / LCM

## Pattern Overview

Translate `b divides a` into `a % b = 0` or `a=bk`. For common
divisors/multiples, test GCD/LCM or factor structure.

``` text
Different CF stories
       ↓ remove nouns
Same mathematical family
       ↓
Divisibility / GCD / LCM
```

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 041 --- Problem 41

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1328A --- Divisibility
Problem](https://codeforces.com/problemset/problem/1328/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`a,b`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
a,b

WHAT MUST BE FOUND:
minimum add to make a divisible by b

MATHEMATICAL OBJECTS THAT MATTER:
need a+x ≡0 mod b

CORE RELATION:
x=(b-a%b)%b

FINAL MATHEMATICAL VIEW:
remainder complement
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **x=(b-a%b)%b**, after which the useful form is **remainder
complement**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1328A --- Divisibility Problem (Modulo / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `x=(b-a%b)%b`. Once the story is stripped away,
    recognize **remainder complement**, then implement **O(1)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
a,b

QUESTION:
minimum add to make a divisible by b
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
need a+x ≡0 mod b
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
a,b

DERIVED QUANTITIES / USEFUL STATE:
need a+x ≡0 mod b

UNKNOWN / ANSWER:
minimum add to make a divisible by b
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
x=(b-a%b)%b
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
x=(b-a%b)%b
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
x=(b-a%b)%b

        ↓ simplify / rearrange / classify

remainder complement
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
need a+x ≡0 mod b
      ↓
EXTRACT TARGET
minimum add to make a divisible by b
      ↓
WRITE FORMULA
x=(b-a%b)%b
      ↓
TRANSFORM
remainder complement
      ↓
ALGORITHM
O(1)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`a,b`**.
2.  Ignore narrative names and rewrite the requirement using
    **`need a+x ≡0 mod b`**.
3.  Express the requirement mathematically as **`x=(b-a%b)%b`**.
4.  Simplify it until the recognizable form **remainder complement**
    appears.
5.  Implement **O(1)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          a,b

Target:         minimum add to make a divisible by b

Variables:      need a+x ≡0 mod b

Formula:        x=(b-a%b)%b

Transform:      remainder complement

Algorithm:      O(1)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    need a+x ≡0 mod b

DEFINE the target:
    minimum add to make a divisible by b

TRANSLATE the condition:
    x=(b-a%b)%b

SIMPLIFY / TRANSFORM:
    remainder complement

APPLY:
    O(1)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`x=(b-a%b)%b`** and the derived method **O(1)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `x=(b-a%b)%b`, test **remainder complement** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 042 --- Problem 42

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1343A ---
Candies](https://codeforces.com/problemset/problem/1343/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`n`**. After removing the story/context, the real
task is:

``` text
INPUT / GIVEN:
n

WHAT MUST BE FOUND:
find x where n=x(2^k-1)

MATHEMATICAL OBJECTS THAT MATTER:
geometric sum factor

CORE RELATION:
x=n/(2^k-1) if divisible

FINAL MATHEMATICAL VIEW:
test k
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **x=n/(2\^k-1) if divisible**, after which the useful form is **test
k**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1343A --- Candies (Geometric/Divisibility / Codeforces / 900)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `x=n/(2^k-1) if divisible`. Once the story is stripped
    away, recognize **test k**, then implement **loop**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
n

QUESTION:
find x where n=x(2^k-1)
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
geometric sum factor
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
n

DERIVED QUANTITIES / USEFUL STATE:
geometric sum factor

UNKNOWN / ANSWER:
find x where n=x(2^k-1)
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
x=n/(2^k-1) if divisible
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
x=n/(2^k-1) if divisible
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
x=n/(2^k-1) if divisible

        ↓ simplify / rearrange / classify

test k
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
geometric sum factor
      ↓
EXTRACT TARGET
find x where n=x(2^k-1)
      ↓
WRITE FORMULA
x=n/(2^k-1) if divisible
      ↓
TRANSFORM
test k
      ↓
ALGORITHM
loop
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`n`**.
2.  Ignore narrative names and rewrite the requirement using
    **`geometric sum factor`**.
3.  Express the requirement mathematically as
    **`x=n/(2^k-1) if divisible`**.
4.  Simplify it until the recognizable form **test k** appears.
5.  Implement **loop** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          n

Target:         find x where n=x(2^k-1)

Variables:      geometric sum factor

Formula:        x=n/(2^k-1) if divisible

Transform:      test k

Algorithm:      loop

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    geometric sum factor

DEFINE the target:
    find x where n=x(2^k-1)

TRANSLATE the condition:
    x=n/(2^k-1) if divisible

SIMPLIFY / TRANSFORM:
    test k

APPLY:
    loop

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`x=n/(2^k-1) if divisible`** and the derived method **loop**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `x=n/(2^k-1) if divisible`, test **test k** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 043 --- Problem 43

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1370A --- Maximum
GCD](https://codeforces.com/problemset/problem/1370/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`n`**. After removing the story/context, the real
task is:

``` text
INPUT / GIVEN:
n

WHAT MUST BE FOUND:
maximize gcd(a,b), a+b=n

MATHEMATICAL OBJECTS THAT MATTER:
gcd<=floor(n/2)

CORE RELATION:
choose floor(n/2)

FINAL MATHEMATICAL VIEW:
tight bound
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **choose floor(n/2)**, after which the useful form is **tight
bound**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1370A --- Maximum GCD (GCD / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `choose floor(n/2)`. Once the story is stripped away,
    recognize **tight bound**, then implement **n/2**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
n

QUESTION:
maximize gcd(a,b), a+b=n
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
gcd<=floor(n/2)
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
n

DERIVED QUANTITIES / USEFUL STATE:
gcd<=floor(n/2)

UNKNOWN / ANSWER:
maximize gcd(a,b), a+b=n
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
choose floor(n/2)
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
choose floor(n/2)
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
choose floor(n/2)

        ↓ simplify / rearrange / classify

tight bound
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
gcd<=floor(n/2)
      ↓
EXTRACT TARGET
maximize gcd(a,b), a+b=n
      ↓
WRITE FORMULA
choose floor(n/2)
      ↓
TRANSFORM
tight bound
      ↓
ALGORITHM
n/2
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`n`**.
2.  Ignore narrative names and rewrite the requirement using
    **`gcd<=floor(n/2)`**.
3.  Express the requirement mathematically as **`choose floor(n/2)`**.
4.  Simplify it until the recognizable form **tight bound** appears.
5.  Implement **n/2** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          n

Target:         maximize gcd(a,b), a+b=n

Variables:      gcd<=floor(n/2)

Formula:        choose floor(n/2)

Transform:      tight bound

Algorithm:      n/2

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    gcd<=floor(n/2)

DEFINE the target:
    maximize gcd(a,b), a+b=n

TRANSLATE the condition:
    choose floor(n/2)

SIMPLIFY / TRANSFORM:
    tight bound

APPLY:
    n/2

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`choose floor(n/2)`** and the derived method **n/2**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `choose floor(n/2)`, test **tight bound** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 044 --- Problem 44

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1829C --- Mr. Perfectly
Fine](https://codeforces.com/problemset/problem/1829/C)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`items`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
items

WHAT MUST BE FOUND:
minimum time covering skills 1 and2

MATHEMATICAL OBJECTS THAT MATTER:
skill masks 01,10,11

CORE RELATION:
min(cost11,cost01+cost10)

FINAL MATHEMATICAL VIEW:
coverage states
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **min(cost11,cost01+cost10)**, after which the useful form is
**coverage states**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1829C --- Mr. Perfectly Fine (Min/Bitmask / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `min(cost11,cost01+cost10)`. Once the story is stripped
    away, recognize **coverage states**, then implement **track
    minima**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
items

QUESTION:
minimum time covering skills 1 and2
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
skill masks 01,10,11
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
items

DERIVED QUANTITIES / USEFUL STATE:
skill masks 01,10,11

UNKNOWN / ANSWER:
minimum time covering skills 1 and2
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
min(cost11,cost01+cost10)
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
min(cost11,cost01+cost10)
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
min(cost11,cost01+cost10)

        ↓ simplify / rearrange / classify

coverage states
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
skill masks 01,10,11
      ↓
EXTRACT TARGET
minimum time covering skills 1 and2
      ↓
WRITE FORMULA
min(cost11,cost01+cost10)
      ↓
TRANSFORM
coverage states
      ↓
ALGORITHM
track minima
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`items`**.
2.  Ignore narrative names and rewrite the requirement using
    **`skill masks 01,10,11`**.
3.  Express the requirement mathematically as
    **`min(cost11,cost01+cost10)`**.
4.  Simplify it until the recognizable form **coverage states** appears.
5.  Implement **track minima** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          items

Target:         minimum time covering skills 1 and2

Variables:      skill masks 01,10,11

Formula:        min(cost11,cost01+cost10)

Transform:      coverage states

Algorithm:      track minima

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    skill masks 01,10,11

DEFINE the target:
    minimum time covering skills 1 and2

TRANSLATE the condition:
    min(cost11,cost01+cost10)

SIMPLIFY / TRANSFORM:
    coverage states

APPLY:
    track minima

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`min(cost11,cost01+cost10)`** and the derived method **track
    minima**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `min(cost11,cost01+cost10)`, test **coverage states** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 045 --- Problem 45

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1618A --- Polycarp and Sums of
Subsequences](https://codeforces.com/problemset/problem/1618/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`7 subset sums`**. After removing the
story/context, the real task is:

``` text
INPUT / GIVEN:
7 subset sums

WHAT MUST BE FOUND:
recover a,b,c

MATHEMATICAL OBJECTS THAT MATTER:
smallest=a,b and total largest=a+b+c

CORE RELATION:
c=largest-a-b

FINAL MATHEMATICAL VIEW:
sorted sums
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **c=largest-a-b**, after which the useful form is **sorted sums**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1618A --- Polycarp and Sums of Subsequences (Algebra / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `c=largest-a-b`. Once the story is stripped away,
    recognize **sorted sums**, then implement **formula**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
7 subset sums

QUESTION:
recover a,b,c
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
smallest=a,b and total largest=a+b+c
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
7 subset sums

DERIVED QUANTITIES / USEFUL STATE:
smallest=a,b and total largest=a+b+c

UNKNOWN / ANSWER:
recover a,b,c
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
c=largest-a-b
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
c=largest-a-b
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
c=largest-a-b

        ↓ simplify / rearrange / classify

sorted sums
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
smallest=a,b and total largest=a+b+c
      ↓
EXTRACT TARGET
recover a,b,c
      ↓
WRITE FORMULA
c=largest-a-b
      ↓
TRANSFORM
sorted sums
      ↓
ALGORITHM
formula
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`7 subset sums`**.
2.  Ignore narrative names and rewrite the requirement using
    **`smallest=a,b and total largest=a+b+c`**.
3.  Express the requirement mathematically as **`c=largest-a-b`**.
4.  Simplify it until the recognizable form **sorted sums** appears.
5.  Implement **formula** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          7 subset sums

Target:         recover a,b,c

Variables:      smallest=a,b and total largest=a+b+c

Formula:        c=largest-a-b

Transform:      sorted sums

Algorithm:      formula

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    smallest=a,b and total largest=a+b+c

DEFINE the target:
    recover a,b,c

TRANSLATE the condition:
    c=largest-a-b

SIMPLIFY / TRANSFORM:
    sorted sums

APPLY:
    formula

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`c=largest-a-b`** and the derived method **formula**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `c=largest-a-b`, test **sorted sums** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 046 --- Problem 46

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 160A ---
Twins](https://codeforces.com/problemset/problem/160/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`coins`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
coins

WHAT MUST BE FOUND:
minimum coins with sum > remaining

MATHEMATICAL OBJECTS THAT MATTER:
chosen > total-chosen

CORE RELATION:
2*chosen>total

FINAL MATHEMATICAL VIEW:
sort descending
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to \*\*2\*chosen\>total**, after which the useful form is **sort
descending\*\*.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 160A --- Twins (Greedy/Sum / Codeforces / 900)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `2*chosen>total`. Once the story is stripped away,
    recognize **sort descending**, then implement **prefix**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
coins

QUESTION:
minimum coins with sum > remaining
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
chosen > total-chosen
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
coins

DERIVED QUANTITIES / USEFUL STATE:
chosen > total-chosen

UNKNOWN / ANSWER:
minimum coins with sum > remaining
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
2*chosen>total
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
2*chosen>total
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
2*chosen>total

        ↓ simplify / rearrange / classify

sort descending
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
chosen > total-chosen
      ↓
EXTRACT TARGET
minimum coins with sum > remaining
      ↓
WRITE FORMULA
2*chosen>total
      ↓
TRANSFORM
sort descending
      ↓
ALGORITHM
prefix
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`coins`**.
2.  Ignore narrative names and rewrite the requirement using
    **`chosen > total-chosen`**.
3.  Express the requirement mathematically as **`2*chosen>total`**.
4.  Simplify it until the recognizable form **sort descending** appears.
5.  Implement **prefix** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          coins

Target:         minimum coins with sum > remaining

Variables:      chosen > total-chosen

Formula:        2*chosen>total

Transform:      sort descending

Algorithm:      prefix

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    chosen > total-chosen

DEFINE the target:
    minimum coins with sum > remaining

TRANSLATE the condition:
    2*chosen>total

SIMPLIFY / TRANSFORM:
    sort descending

APPLY:
    prefix

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`2*chosen>total`** and the derived method **prefix**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `2*chosen>total`, test **sort descending** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 047 --- Problem 47

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1475B --- New Year's
Number](https://codeforces.com/problemset/problem/1475/B)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`n`**. After removing the story/context, the real
task is:

``` text
INPUT / GIVEN:
n

WHAT MUST BE FOUND:
n=2020a+2021b?

MATHEMATICAL OBJECTS THAT MATTER:
2021=2020+1

CORE RELATION:
choose b=n%2020 then test

FINAL MATHEMATICAL VIEW:
linear diophantine shortcut
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **choose b=n%2020 then test**, after which the useful form is
**linear diophantine shortcut**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1475B --- New Year's Number (Diophantine / Codeforces / 900)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `choose b=n%2020 then test`. Once the story is stripped
    away, recognize **linear diophantine shortcut**, then implement
    **condition**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
n

QUESTION:
n=2020a+2021b?
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
2021=2020+1
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
n

DERIVED QUANTITIES / USEFUL STATE:
2021=2020+1

UNKNOWN / ANSWER:
n=2020a+2021b?
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
choose b=n%2020 then test
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
choose b=n%2020 then test
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
choose b=n%2020 then test

        ↓ simplify / rearrange / classify

linear diophantine shortcut
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
2021=2020+1
      ↓
EXTRACT TARGET
n=2020a+2021b?
      ↓
WRITE FORMULA
choose b=n%2020 then test
      ↓
TRANSFORM
linear diophantine shortcut
      ↓
ALGORITHM
condition
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`n`**.
2.  Ignore narrative names and rewrite the requirement using
    **`2021=2020+1`**.
3.  Express the requirement mathematically as
    **`choose b=n%2020 then test`**.
4.  Simplify it until the recognizable form **linear diophantine
    shortcut** appears.
5.  Implement **condition** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          n

Target:         n=2020a+2021b?

Variables:      2021=2020+1

Formula:        choose b=n%2020 then test

Transform:      linear diophantine shortcut

Algorithm:      condition

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    2021=2020+1

DEFINE the target:
    n=2020a+2021b?

TRANSLATE the condition:
    choose b=n%2020 then test

SIMPLIFY / TRANSFORM:
    linear diophantine shortcut

APPLY:
    condition

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`choose b=n%2020 then test`** and the derived method
    **condition**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `choose b=n%2020 then test`, test **linear diophantine shortcut**
    immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 048 --- Problem 48

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1593A ---
Elections](https://codeforces.com/problemset/problem/1593/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`a,b,c`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
a,b,c

WHAT MUST BE FOUND:
increments to become strictly largest

MATHEMATICAL OBJECTS THAT MATTER:
need x+inc>max(other)

CORE RELATION:
inc=max(0,M-x+1), except unique max

FINAL MATHEMATICAL VIEW:
per candidate bound
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **inc=max(0,M-x+1), except unique max**, after which the useful form
is **per candidate bound**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1593A --- Elections (Max/Formula / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `inc=max(0,M-x+1), except unique max`. Once the story
    is stripped away, recognize **per candidate bound**, then implement
    **formula**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
a,b,c

QUESTION:
increments to become strictly largest
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
need x+inc>max(other)
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
a,b,c

DERIVED QUANTITIES / USEFUL STATE:
need x+inc>max(other)

UNKNOWN / ANSWER:
increments to become strictly largest
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
inc=max(0,M-x+1), except unique max
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
inc=max(0,M-x+1), except unique max
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
inc=max(0,M-x+1), except unique max

        ↓ simplify / rearrange / classify

per candidate bound
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
need x+inc>max(other)
      ↓
EXTRACT TARGET
increments to become strictly largest
      ↓
WRITE FORMULA
inc=max(0,M-x+1), except unique max
      ↓
TRANSFORM
per candidate bound
      ↓
ALGORITHM
formula
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`a,b,c`**.
2.  Ignore narrative names and rewrite the requirement using
    **`need x+inc>max(other)`**.
3.  Express the requirement mathematically as
    **`inc=max(0,M-x+1), except unique max`**.
4.  Simplify it until the recognizable form **per candidate bound**
    appears.
5.  Implement **formula** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          a,b,c

Target:         increments to become strictly largest

Variables:      need x+inc>max(other)

Formula:        inc=max(0,M-x+1), except unique max

Transform:      per candidate bound

Algorithm:      formula

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    need x+inc>max(other)

DEFINE the target:
    increments to become strictly largest

TRANSLATE the condition:
    inc=max(0,M-x+1), except unique max

SIMPLIFY / TRANSFORM:
    per candidate bound

APPLY:
    formula

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`inc=max(0,M-x+1), except unique max`** and the derived method
    **formula**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `inc=max(0,M-x+1), except unique max`, test **per candidate bound**
    immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 049 --- Problem 49

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1829B --- Blank
Space](https://codeforces.com/problemset/problem/1829/B)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`binary array`**. After removing the
story/context, the real task is:

``` text
INPUT / GIVEN:
binary array

WHAT MUST BE FOUND:
longest consecutive zeros

MATHEMATICAL OBJECTS THAT MATTER:
state current run

CORE RELATION:
max over runs

FINAL MATHEMATICAL VIEW:
scan
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **max over runs**, after which the useful form is **scan**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1829B --- Blank Space (Run Length / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `max over runs`. Once the story is stripped away,
    recognize **scan**, then implement **O(n)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
binary array

QUESTION:
longest consecutive zeros
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
state current run
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
binary array

DERIVED QUANTITIES / USEFUL STATE:
state current run

UNKNOWN / ANSWER:
longest consecutive zeros
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
max over runs
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
max over runs
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
max over runs

        ↓ simplify / rearrange / classify

scan
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
state current run
      ↓
EXTRACT TARGET
longest consecutive zeros
      ↓
WRITE FORMULA
max over runs
      ↓
TRANSFORM
scan
      ↓
ALGORITHM
O(n)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`binary array`**.
2.  Ignore narrative names and rewrite the requirement using
    **`state current run`**.
3.  Express the requirement mathematically as **`max over runs`**.
4.  Simplify it until the recognizable form **scan** appears.
5.  Implement **O(n)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          binary array

Target:         longest consecutive zeros

Variables:      state current run

Formula:        max over runs

Transform:      scan

Algorithm:      O(n)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    state current run

DEFINE the target:
    longest consecutive zeros

TRANSLATE the condition:
    max over runs

SIMPLIFY / TRANSFORM:
    scan

APPLY:
    O(n)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`max over runs`** and the derived method **O(n)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `max over runs`, test **scan** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 050 --- Problem 50

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1877A --- Goals of
Victory](https://codeforces.com/problemset/problem/1877/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`n-1 values`**. After removing the story/context,
the real task is:

``` text
INPUT / GIVEN:
n-1 values

WHAT MUST BE FOUND:
missing value so total sum zero

MATHEMATICAL OBJECTS THAT MATTER:
x+sum=0

CORE RELATION:
x=-sum

FINAL MATHEMATICAL VIEW:
equation
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **x=-sum**, after which the useful form is **equation**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1877A --- Goals of Victory (Sum Invariant / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `x=-sum`. Once the story is stripped away, recognize
    **equation**, then implement **O(n)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
n-1 values

QUESTION:
missing value so total sum zero
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
x+sum=0
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
n-1 values

DERIVED QUANTITIES / USEFUL STATE:
x+sum=0

UNKNOWN / ANSWER:
missing value so total sum zero
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
x=-sum
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
x=-sum
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
x=-sum

        ↓ simplify / rearrange / classify

equation
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
x+sum=0
      ↓
EXTRACT TARGET
missing value so total sum zero
      ↓
WRITE FORMULA
x=-sum
      ↓
TRANSFORM
equation
      ↓
ALGORITHM
O(n)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`n-1 values`**.
2.  Ignore narrative names and rewrite the requirement using
    **`x+sum=0`**.
3.  Express the requirement mathematically as **`x=-sum`**.
4.  Simplify it until the recognizable form **equation** appears.
5.  Implement **O(n)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          n-1 values

Target:         missing value so total sum zero

Variables:      x+sum=0

Formula:        x=-sum

Transform:      equation

Algorithm:      O(n)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    x+sum=0

DEFINE the target:
    missing value so total sum zero

TRANSLATE the condition:
    x=-sum

SIMPLIFY / TRANSFORM:
    equation

APPLY:
    O(n)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`x=-sum`** and the derived method **O(n)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `x=-sum`, test **equation** immediately.

# Pattern 6 --- Modulo / Cyclic Modeling

## Pattern Overview

When behavior repeats after a fixed number of states, replace large
counts with a remainder. Normalize positions into residue classes.

``` text
Different CF stories
       ↓ remove nouns
Same mathematical family
       ↓
Modulo / Cyclic Modeling
```

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 051 --- Problem 51

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 116A ---
Tram](https://codeforces.com/problemset/problem/116/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`stops`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
stops

WHAT MUST BE FOUND:
minimum tram capacity

MATHEMATICAL OBJECTS THAT MATTER:
current += enter-exit

CORE RELATION:
max prefix occupancy

FINAL MATHEMATICAL VIEW:
running state
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **max prefix occupancy**, after which the useful form is **running
state**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 116A --- Tram (Prefix/Capacity / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `max prefix occupancy`. Once the story is stripped
    away, recognize **running state**, then implement **scan**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
stops

QUESTION:
minimum tram capacity
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
current += enter-exit
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
stops

DERIVED QUANTITIES / USEFUL STATE:
current += enter-exit

UNKNOWN / ANSWER:
minimum tram capacity
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
max prefix occupancy
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
max prefix occupancy
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
max prefix occupancy

        ↓ simplify / rearrange / classify

running state
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
current += enter-exit
      ↓
EXTRACT TARGET
minimum tram capacity
      ↓
WRITE FORMULA
max prefix occupancy
      ↓
TRANSFORM
running state
      ↓
ALGORITHM
scan
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`stops`**.
2.  Ignore narrative names and rewrite the requirement using
    **`current += enter-exit`**.
3.  Express the requirement mathematically as
    **`max prefix occupancy`**.
4.  Simplify it until the recognizable form **running state** appears.
5.  Implement **scan** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          stops

Target:         minimum tram capacity

Variables:      current += enter-exit

Formula:        max prefix occupancy

Transform:      running state

Algorithm:      scan

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    current += enter-exit

DEFINE the target:
    minimum tram capacity

TRANSLATE the condition:
    max prefix occupancy

SIMPLIFY / TRANSFORM:
    running state

APPLY:
    scan

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`max prefix occupancy`** and the derived method **scan**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `max prefix occupancy`, test **running state** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 052 --- Problem 52

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 266A --- Stones on the
Table](https://codeforces.com/problemset/problem/266/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`string`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
string

WHAT MUST BE FOUND:
minimum removals so adjacent colors differ

MATHEMATICAL OBJECTS THAT MATTER:
remove one from each equal adjacency

CORE RELATION:
count s[i]==s[i-1]

FINAL MATHEMATICAL VIEW:
local contribution
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **count s\[i\]==s\[i-1\]**, after which the useful form is **local
contribution**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 266A --- Stones on the Table (Adjacent / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `count s[i]==s[i-1]`. Once the story is stripped away,
    recognize **local contribution**, then implement **scan**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
string

QUESTION:
minimum removals so adjacent colors differ
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
remove one from each equal adjacency
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
string

DERIVED QUANTITIES / USEFUL STATE:
remove one from each equal adjacency

UNKNOWN / ANSWER:
minimum removals so adjacent colors differ
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
count s[i]==s[i-1]
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
count s[i]==s[i-1]
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
count s[i]==s[i-1]

        ↓ simplify / rearrange / classify

local contribution
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
remove one from each equal adjacency
      ↓
EXTRACT TARGET
minimum removals so adjacent colors differ
      ↓
WRITE FORMULA
count s[i]==s[i-1]
      ↓
TRANSFORM
local contribution
      ↓
ALGORITHM
scan
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`string`**.
2.  Ignore narrative names and rewrite the requirement using
    **`remove one from each equal adjacency`**.
3.  Express the requirement mathematically as **`count s[i]==s[i-1]`**.
4.  Simplify it until the recognizable form **local contribution**
    appears.
5.  Implement **scan** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          string

Target:         minimum removals so adjacent colors differ

Variables:      remove one from each equal adjacency

Formula:        count s[i]==s[i-1]

Transform:      local contribution

Algorithm:      scan

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    remove one from each equal adjacency

DEFINE the target:
    minimum removals so adjacent colors differ

TRANSLATE the condition:
    count s[i]==s[i-1]

SIMPLIFY / TRANSFORM:
    local contribution

APPLY:
    scan

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`count s[i]==s[i-1]`** and the derived method **scan**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `count s[i]==s[i-1]`, test **local contribution** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 053 --- Problem 53

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 228A --- Is your horseshoe on the other
hoof?](https://codeforces.com/problemset/problem/228/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`4 colors`**. After removing the story/context,
the real task is:

``` text
INPUT / GIVEN:
4 colors

WHAT MUST BE FOUND:
minimum replacements for distinct

MATHEMATICAL OBJECTS THAT MATTER:
4-distinctCount

CORE RELATION:
set size

FINAL MATHEMATICAL VIEW:
duplicates
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **set size**, after which the useful form is **duplicates**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 228A --- Is your horseshoe on the other hoof? (Distinctness / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `set size`. Once the story is stripped away, recognize
    **duplicates**, then implement **set**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
4 colors

QUESTION:
minimum replacements for distinct
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
4-distinctCount
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
4 colors

DERIVED QUANTITIES / USEFUL STATE:
4-distinctCount

UNKNOWN / ANSWER:
minimum replacements for distinct
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
set size
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
set size
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
set size

        ↓ simplify / rearrange / classify

duplicates
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
4-distinctCount
      ↓
EXTRACT TARGET
minimum replacements for distinct
      ↓
WRITE FORMULA
set size
      ↓
TRANSFORM
duplicates
      ↓
ALGORITHM
set
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`4 colors`**.
2.  Ignore narrative names and rewrite the requirement using
    **`4-distinctCount`**.
3.  Express the requirement mathematically as **`set size`**.
4.  Simplify it until the recognizable form **duplicates** appears.
5.  Implement **set** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          4 colors

Target:         minimum replacements for distinct

Variables:      4-distinctCount

Formula:        set size

Transform:      duplicates

Algorithm:      set

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    4-distinctCount

DEFINE the target:
    minimum replacements for distinct

TRANSLATE the condition:
    set size

SIMPLIFY / TRANSFORM:
    duplicates

APPLY:
    set

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`set size`** and the derived method **set**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `set size`, test **duplicates** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 054 --- Problem 54

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 443A --- Anton and
Letters](https://codeforces.com/problemset/problem/443/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`formatted string`**. After removing the
story/context, the real task is:

``` text
INPUT / GIVEN:
formatted string

WHAT MUST BE FOUND:
number distinct letters

MATHEMATICAL OBJECTS THAT MATTER:
extract lowercase chars

CORE RELATION:
set cardinality

FINAL MATHEMATICAL VIEW:
distinct count
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **set cardinality**, after which the useful form is **distinct
count**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 443A --- Anton and Letters (Set / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `set cardinality`. Once the story is stripped away,
    recognize **distinct count**, then implement **set**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
formatted string

QUESTION:
number distinct letters
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
extract lowercase chars
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
formatted string

DERIVED QUANTITIES / USEFUL STATE:
extract lowercase chars

UNKNOWN / ANSWER:
number distinct letters
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
set cardinality
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
set cardinality
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
set cardinality

        ↓ simplify / rearrange / classify

distinct count
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
extract lowercase chars
      ↓
EXTRACT TARGET
number distinct letters
      ↓
WRITE FORMULA
set cardinality
      ↓
TRANSFORM
distinct count
      ↓
ALGORITHM
set
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`formatted string`**.
2.  Ignore narrative names and rewrite the requirement using
    **`extract lowercase chars`**.
3.  Express the requirement mathematically as **`set cardinality`**.
4.  Simplify it until the recognizable form **distinct count** appears.
5.  Implement **set** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          formatted string

Target:         number distinct letters

Variables:      extract lowercase chars

Formula:        set cardinality

Transform:      distinct count

Algorithm:      set

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    extract lowercase chars

DEFINE the target:
    number distinct letters

TRANSLATE the condition:
    set cardinality

SIMPLIFY / TRANSFORM:
    distinct count

APPLY:
    set

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`set cardinality`** and the derived method **set**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `set cardinality`, test **distinct count** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 055 --- Problem 55

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 59A ---
Word](https://codeforces.com/problemset/problem/59/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`string`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
string

WHAT MUST BE FOUND:
convert based on upper/lower majority

MATHEMATICAL OBJECTS THAT MATTER:
count uppercase vs lowercase

CORE RELATION:
choose case

FINAL MATHEMATICAL VIEW:
frequency comparison
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **choose case**, after which the useful form is **frequency
comparison**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 59A --- Word (Counting / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `choose case`. Once the story is stripped away,
    recognize **frequency comparison**, then implement **transform**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
string

QUESTION:
convert based on upper/lower majority
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
count uppercase vs lowercase
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
string

DERIVED QUANTITIES / USEFUL STATE:
count uppercase vs lowercase

UNKNOWN / ANSWER:
convert based on upper/lower majority
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
choose case
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
choose case
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
choose case

        ↓ simplify / rearrange / classify

frequency comparison
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
count uppercase vs lowercase
      ↓
EXTRACT TARGET
convert based on upper/lower majority
      ↓
WRITE FORMULA
choose case
      ↓
TRANSFORM
frequency comparison
      ↓
ALGORITHM
transform
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`string`**.
2.  Ignore narrative names and rewrite the requirement using
    **`count uppercase vs lowercase`**.
3.  Express the requirement mathematically as **`choose case`**.
4.  Simplify it until the recognizable form **frequency comparison**
    appears.
5.  Implement **transform** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          string

Target:         convert based on upper/lower majority

Variables:      count uppercase vs lowercase

Formula:        choose case

Transform:      frequency comparison

Algorithm:      transform

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    count uppercase vs lowercase

DEFINE the target:
    convert based on upper/lower majority

TRANSLATE the condition:
    choose case

SIMPLIFY / TRANSFORM:
    frequency comparison

APPLY:
    transform

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`choose case`** and the derived method **transform**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `choose case`, test **frequency comparison** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 056 --- Problem 56

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 236A --- Boy or
Girl](https://codeforces.com/problemset/problem/236/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`username`**. After removing the story/context,
the real task is:

``` text
INPUT / GIVEN:
username

WHAT MUST BE FOUND:
output based on distinct char count parity

MATHEMATICAL OBJECTS THAT MATTER:
d=|set(chars)|

CORE RELATION:
d%2

FINAL MATHEMATICAL VIEW:
parity of distinct count
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **d%2**, after which the useful form is **parity of distinct count**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 236A --- Boy or Girl (Set/Parity / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `d%2`. Once the story is stripped away, recognize
    **parity of distinct count**, then implement **set**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
username

QUESTION:
output based on distinct char count parity
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
d=|set(chars)|
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
username

DERIVED QUANTITIES / USEFUL STATE:
d=|set(chars)|

UNKNOWN / ANSWER:
output based on distinct char count parity
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
d%2
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
d%2
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
d%2

        ↓ simplify / rearrange / classify

parity of distinct count
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
d=|set(chars)|
      ↓
EXTRACT TARGET
output based on distinct char count parity
      ↓
WRITE FORMULA
d%2
      ↓
TRANSFORM
parity of distinct count
      ↓
ALGORITHM
set
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`username`**.
2.  Ignore narrative names and rewrite the requirement using
    **`d=|set(chars)|`**.
3.  Express the requirement mathematically as **`d%2`**.
4.  Simplify it until the recognizable form **parity of distinct count**
    appears.
5.  Implement **set** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          username

Target:         output based on distinct char count parity

Variables:      d=|set(chars)|

Formula:        d%2

Transform:      parity of distinct count

Algorithm:      set

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    d=|set(chars)|

DEFINE the target:
    output based on distinct char count parity

TRANSLATE the condition:
    d%2

SIMPLIFY / TRANSFORM:
    parity of distinct count

APPLY:
    set

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`d%2`** and the derived method **set**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `d%2`, test **parity of distinct count** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 057 --- Problem 57

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 785A --- Anton and
Polyhedrons](https://codeforces.com/problemset/problem/785/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`names`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
names

WHAT MUST BE FOUND:
total faces

MATHEMATICAL OBJECTS THAT MATTER:
name→constant

CORE RELATION:
sum contributions

FINAL MATHEMATICAL VIEW:
lookup
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **sum contributions**, after which the useful form is **lookup**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 785A --- Anton and Polyhedrons (Mapping / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `sum contributions`. Once the story is stripped away,
    recognize **lookup**, then implement **map/if**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
names

QUESTION:
total faces
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
name→constant
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
names

DERIVED QUANTITIES / USEFUL STATE:
name→constant

UNKNOWN / ANSWER:
total faces
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
sum contributions
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
sum contributions
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
sum contributions

        ↓ simplify / rearrange / classify

lookup
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
name→constant
      ↓
EXTRACT TARGET
total faces
      ↓
WRITE FORMULA
sum contributions
      ↓
TRANSFORM
lookup
      ↓
ALGORITHM
map/if
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`names`**.
2.  Ignore narrative names and rewrite the requirement using
    **`name→constant`**.
3.  Express the requirement mathematically as **`sum contributions`**.
4.  Simplify it until the recognizable form **lookup** appears.
5.  Implement **map/if** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          names

Target:         total faces

Variables:      name→constant

Formula:        sum contributions

Transform:      lookup

Algorithm:      map/if

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    name→constant

DEFINE the target:
    total faces

TRANSLATE the condition:
    sum contributions

SIMPLIFY / TRANSFORM:
    lookup

APPLY:
    map/if

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`sum contributions`** and the derived method **map/if**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `sum contributions`, test **lookup** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 058 --- Problem 58

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 703A --- Mishka and
Game](https://codeforces.com/problemset/problem/703/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`round scores`**. After removing the
story/context, the real task is:

``` text
INPUT / GIVEN:
round scores

WHAT MUST BE FOUND:
winner by more round wins

MATHEMATICAL OBJECTS THAT MATTER:
count a>b and a<b

CORE RELATION:
compare counts

FINAL MATHEMATICAL VIEW:
two counters
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **compare counts**, after which the useful form is **two counters**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 703A --- Mishka and Game (Comparison / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `compare counts`. Once the story is stripped away,
    recognize **two counters**, then implement **scan**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
round scores

QUESTION:
winner by more round wins
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
count a>b and a<b
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
round scores

DERIVED QUANTITIES / USEFUL STATE:
count a>b and a<b

UNKNOWN / ANSWER:
winner by more round wins
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
compare counts
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
compare counts
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
compare counts

        ↓ simplify / rearrange / classify

two counters
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
count a>b and a<b
      ↓
EXTRACT TARGET
winner by more round wins
      ↓
WRITE FORMULA
compare counts
      ↓
TRANSFORM
two counters
      ↓
ALGORITHM
scan
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`round scores`**.
2.  Ignore narrative names and rewrite the requirement using
    **`count a>b and a<b`**.
3.  Express the requirement mathematically as **`compare counts`**.
4.  Simplify it until the recognizable form **two counters** appears.
5.  Implement **scan** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          round scores

Target:         winner by more round wins

Variables:      count a>b and a<b

Formula:        compare counts

Transform:      two counters

Algorithm:      scan

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    count a>b and a<b

DEFINE the target:
    winner by more round wins

TRANSLATE the condition:
    compare counts

SIMPLIFY / TRANSFORM:
    two counters

APPLY:
    scan

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`compare counts`** and the derived method **scan**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `compare counts`, test **two counters** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 059 --- Problem 59

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 734B --- Anton and
Digits](https://codeforces.com/problemset/problem/734/B)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`counts 2,3,5,6`**. After removing the
story/context, the real task is:

``` text
INPUT / GIVEN:
counts 2,3,5,6

WHAT MUST BE FOUND:
maximize sum using 256 and32

MATHEMATICAL OBJECTS THAT MATTER:
make 256 first because larger

CORE RELATION:
x=min(2,5,6), y=min(2left,3)

FINAL MATHEMATICAL VIEW:
resource allocation
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **x=min(2,5,6), y=min(2left,3)**, after which the useful form is
**resource allocation**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 734B --- Anton and Digits (Greedy/Counting / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `x=min(2,5,6), y=min(2left,3)`. Once the story is
    stripped away, recognize **resource allocation**, then implement
    **greedy**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
counts 2,3,5,6

QUESTION:
maximize sum using 256 and32
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
make 256 first because larger
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
counts 2,3,5,6

DERIVED QUANTITIES / USEFUL STATE:
make 256 first because larger

UNKNOWN / ANSWER:
maximize sum using 256 and32
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
x=min(2,5,6), y=min(2left,3)
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
x=min(2,5,6), y=min(2left,3)
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
x=min(2,5,6), y=min(2left,3)

        ↓ simplify / rearrange / classify

resource allocation
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
make 256 first because larger
      ↓
EXTRACT TARGET
maximize sum using 256 and32
      ↓
WRITE FORMULA
x=min(2,5,6), y=min(2left,3)
      ↓
TRANSFORM
resource allocation
      ↓
ALGORITHM
greedy
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`counts 2,3,5,6`**.
2.  Ignore narrative names and rewrite the requirement using
    **`make 256 first because larger`**.
3.  Express the requirement mathematically as
    **`x=min(2,5,6), y=min(2left,3)`**.
4.  Simplify it until the recognizable form **resource allocation**
    appears.
5.  Implement **greedy** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          counts 2,3,5,6

Target:         maximize sum using 256 and32

Variables:      make 256 first because larger

Formula:        x=min(2,5,6), y=min(2left,3)

Transform:      resource allocation

Algorithm:      greedy

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    make 256 first because larger

DEFINE the target:
    maximize sum using 256 and32

TRANSLATE the condition:
    x=min(2,5,6), y=min(2left,3)

SIMPLIFY / TRANSFORM:
    resource allocation

APPLY:
    greedy

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`x=min(2,5,6), y=min(2left,3)`** and the derived method
    **greedy**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `x=min(2,5,6), y=min(2left,3)`, test **resource allocation**
    immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 060 --- Problem 60

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1097A --- Gennady the Card
Game](https://codeforces.com/problemset/problem/1097/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`card + five cards`**. After removing the
story/context, the real task is:

``` text
INPUT / GIVEN:
card + five cards

WHAT MUST BE FOUND:
whether rank or suit matches

MATHEMATICAL OBJECTS THAT MATTER:
exists same first or second char

CORE RELATION:
OR condition

FINAL MATHEMATICAL VIEW:
scan
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **OR condition**, after which the useful form is **scan**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1097A --- Gennady the Card Game (Matching / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `OR condition`. Once the story is stripped away,
    recognize **scan**, then implement **O(5)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
card + five cards

QUESTION:
whether rank or suit matches
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
exists same first or second char
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
card + five cards

DERIVED QUANTITIES / USEFUL STATE:
exists same first or second char

UNKNOWN / ANSWER:
whether rank or suit matches
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
OR condition
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
OR condition
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
OR condition

        ↓ simplify / rearrange / classify

scan
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
exists same first or second char
      ↓
EXTRACT TARGET
whether rank or suit matches
      ↓
WRITE FORMULA
OR condition
      ↓
TRANSFORM
scan
      ↓
ALGORITHM
O(5)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`card + five cards`**.
2.  Ignore narrative names and rewrite the requirement using
    **`exists same first or second char`**.
3.  Express the requirement mathematically as **`OR condition`**.
4.  Simplify it until the recognizable form **scan** appears.
5.  Implement **O(5)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          card + five cards

Target:         whether rank or suit matches

Variables:      exists same first or second char

Formula:        OR condition

Transform:      scan

Algorithm:      O(5)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    exists same first or second char

DEFINE the target:
    whether rank or suit matches

TRANSLATE the condition:
    OR condition

SIMPLIFY / TRANSFORM:
    scan

APPLY:
    O(5)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`OR condition`** and the derived method **O(5)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `OR condition`, test **scan** immediately.

# Pattern 7 --- Counting / Frequency / Pairs

## Pattern Overview

Replace pair enumeration with frequency counting whenever validity
depends only on a key. Equal-key pairs contribute `f(f-1)/2`.

``` text
Different CF stories
       ↓ remove nouns
Same mathematical family
       ↓
Counting / Frequency / Pairs
```

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 061 --- Problem 61

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1520D --- Same
Differences](https://codeforces.com/problemset/problem/1520/D)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`array`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
array

WHAT MUST BE FOUND:
count i<j with a[j]-a[i]=j-i

MATHEMATICAL OBJECTS THAT MATTER:
a[j]-j=a[i]-i

CORE RELATION:
key=a[i]-i

FINAL MATHEMATICAL VIEW:
equal-key pairs
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **key=a\[i\]-i**, after which the useful form is **equal-key pairs**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1520D --- Same Differences (Algebra/Frequency / Codeforces / 1200)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `key=a[i]-i`. Once the story is stripped away,
    recognize **equal-key pairs**, then implement **hash map**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
array

QUESTION:
count i<j with a[j]-a[i]=j-i
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
a[j]-j=a[i]-i
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
array

DERIVED QUANTITIES / USEFUL STATE:
a[j]-j=a[i]-i

UNKNOWN / ANSWER:
count i<j with a[j]-a[i]=j-i
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
key=a[i]-i
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
key=a[i]-i
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
key=a[i]-i

        ↓ simplify / rearrange / classify

equal-key pairs
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
a[j]-j=a[i]-i
      ↓
EXTRACT TARGET
count i<j with a[j]-a[i]=j-i
      ↓
WRITE FORMULA
key=a[i]-i
      ↓
TRANSFORM
equal-key pairs
      ↓
ALGORITHM
hash map
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`array`**.
2.  Ignore narrative names and rewrite the requirement using
    **`a[j]-j=a[i]-i`**.
3.  Express the requirement mathematically as **`key=a[i]-i`**.
4.  Simplify it until the recognizable form **equal-key pairs** appears.
5.  Implement **hash map** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          array

Target:         count i<j with a[j]-a[i]=j-i

Variables:      a[j]-j=a[i]-i

Formula:        key=a[i]-i

Transform:      equal-key pairs

Algorithm:      hash map

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    a[j]-j=a[i]-i

DEFINE the target:
    count i<j with a[j]-a[i]=j-i

TRANSLATE the condition:
    key=a[i]-i

SIMPLIFY / TRANSFORM:
    equal-key pairs

APPLY:
    hash map

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`key=a[i]-i`** and the derived method **hash map**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `key=a[i]-i`, test **equal-key pairs** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 062 --- Problem 62

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1538C --- Challenging Cliffs / Number of
Pairs](https://codeforces.com/problemset/problem/1538/C)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`array,l,r`**. After removing the story/context,
the real task is:

``` text
INPUT / GIVEN:
array,l,r

WHAT MUST BE FOUND:
count pairs with sum in [l,r]

MATHEMATICAL OBJECTS THAT MATTER:
count<=r - count<l

CORE RELATION:
sorted pair bound

FINAL MATHEMATICAL VIEW:
two pointers
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **sorted pair bound**, after which the useful form is **two
pointers**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1538C --- Challenging Cliffs / Number of Pairs (Two Pointers / Codeforces / 1300)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `sorted pair bound`. Once the story is stripped away,
    recognize **two pointers**, then implement **O(nlogn)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
array,l,r

QUESTION:
count pairs with sum in [l,r]
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
count<=r - count<l
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
array,l,r

DERIVED QUANTITIES / USEFUL STATE:
count<=r - count<l

UNKNOWN / ANSWER:
count pairs with sum in [l,r]
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
sorted pair bound
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
sorted pair bound
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
sorted pair bound

        ↓ simplify / rearrange / classify

two pointers
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
count<=r - count<l
      ↓
EXTRACT TARGET
count pairs with sum in [l,r]
      ↓
WRITE FORMULA
sorted pair bound
      ↓
TRANSFORM
two pointers
      ↓
ALGORITHM
O(nlogn)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`array,l,r`**.
2.  Ignore narrative names and rewrite the requirement using
    **`count<=r - count<l`**.
3.  Express the requirement mathematically as **`sorted pair bound`**.
4.  Simplify it until the recognizable form **two pointers** appears.
5.  Implement **O(nlogn)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          array,l,r

Target:         count pairs with sum in [l,r]

Variables:      count<=r - count<l

Formula:        sorted pair bound

Transform:      two pointers

Algorithm:      O(nlogn)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    count<=r - count<l

DEFINE the target:
    count pairs with sum in [l,r]

TRANSLATE the condition:
    sorted pair bound

SIMPLIFY / TRANSFORM:
    two pointers

APPLY:
    O(nlogn)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`sorted pair bound`** and the derived method **O(nlogn)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `sorted pair bound`, test **two pointers** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 063 --- Problem 63

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1669B ---
Triple](https://codeforces.com/problemset/problem/1669/B)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`array`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
array

WHAT MUST BE FOUND:
find value occurring >=3

MATHEMATICAL OBJECTS THAT MATTER:
freq[x]>=3

CORE RELATION:
frequency threshold

FINAL MATHEMATICAL VIEW:
count
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **frequency threshold**, after which the useful form is **count**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1669B --- Triple (Frequency / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `frequency threshold`. Once the story is stripped away,
    recognize **count**, then implement **map**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
array

QUESTION:
find value occurring >=3
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
freq[x]>=3
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
array

DERIVED QUANTITIES / USEFUL STATE:
freq[x]>=3

UNKNOWN / ANSWER:
find value occurring >=3
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
frequency threshold
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
frequency threshold
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
frequency threshold

        ↓ simplify / rearrange / classify

count
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
freq[x]>=3
      ↓
EXTRACT TARGET
find value occurring >=3
      ↓
WRITE FORMULA
frequency threshold
      ↓
TRANSFORM
count
      ↓
ALGORITHM
map
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`array`**.
2.  Ignore narrative names and rewrite the requirement using
    **`freq[x]>=3`**.
3.  Express the requirement mathematically as **`frequency threshold`**.
4.  Simplify it until the recognizable form **count** appears.
5.  Implement **map** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          array

Target:         find value occurring >=3

Variables:      freq[x]>=3

Formula:        frequency threshold

Transform:      count

Algorithm:      map

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    freq[x]>=3

DEFINE the target:
    find value occurring >=3

TRANSLATE the condition:
    frequency threshold

SIMPLIFY / TRANSFORM:
    count

APPLY:
    map

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`frequency threshold`** and the derived method **map**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `frequency threshold`, test **count** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 064 --- Problem 64

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1742C ---
Stripes](https://codeforces.com/problemset/problem/1742/C)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`8x8 grid`**. After removing the story/context,
the real task is:

``` text
INPUT / GIVEN:
8x8 grid

WHAT MUST BE FOUND:
determine last full stripe color

MATHEMATICAL OBJECTS THAT MATTER:
full row of R is decisive

CORE RELATION:
scan rows

FINAL MATHEMATICAL VIEW:
existence
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **scan rows**, after which the useful form is **existence**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1742C --- Stripes (Grid/Existence / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `scan rows`. Once the story is stripped away, recognize
    **existence**, then implement **O(64)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
8x8 grid

QUESTION:
determine last full stripe color
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
full row of R is decisive
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
8x8 grid

DERIVED QUANTITIES / USEFUL STATE:
full row of R is decisive

UNKNOWN / ANSWER:
determine last full stripe color
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
scan rows
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
scan rows
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
scan rows

        ↓ simplify / rearrange / classify

existence
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
full row of R is decisive
      ↓
EXTRACT TARGET
determine last full stripe color
      ↓
WRITE FORMULA
scan rows
      ↓
TRANSFORM
existence
      ↓
ALGORITHM
O(64)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`8x8 grid`**.
2.  Ignore narrative names and rewrite the requirement using
    **`full row of R is decisive`**.
3.  Express the requirement mathematically as **`scan rows`**.
4.  Simplify it until the recognizable form **existence** appears.
5.  Implement **O(64)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          8x8 grid

Target:         determine last full stripe color

Variables:      full row of R is decisive

Formula:        scan rows

Transform:      existence

Algorithm:      O(64)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    full row of R is decisive

DEFINE the target:
    determine last full stripe color

TRANSLATE the condition:
    scan rows

SIMPLIFY / TRANSFORM:
    existence

APPLY:
    O(64)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`scan rows`** and the derived method **O(64)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `scan rows`, test **existence** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 065 --- Problem 65

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1791B --- Following
Directions](https://codeforces.com/problemset/problem/1791/B)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`moves`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
moves

WHAT MUST BE FOUND:
whether path visits (1,1)

MATHEMATICAL OBJECTS THAT MATTER:
update x,y per char

CORE RELATION:
∃prefix=(1,1)

FINAL MATHEMATICAL VIEW:
prefix state
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **∃prefix=(1,1)**, after which the useful form is **prefix state**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1791B --- Following Directions (Coordinates / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `∃prefix=(1,1)`. Once the story is stripped away,
    recognize **prefix state**, then implement **scan**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
moves

QUESTION:
whether path visits (1,1)
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
update x,y per char
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
moves

DERIVED QUANTITIES / USEFUL STATE:
update x,y per char

UNKNOWN / ANSWER:
whether path visits (1,1)
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
∃prefix=(1,1)
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
∃prefix=(1,1)
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
∃prefix=(1,1)

        ↓ simplify / rearrange / classify

prefix state
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
update x,y per char
      ↓
EXTRACT TARGET
whether path visits (1,1)
      ↓
WRITE FORMULA
∃prefix=(1,1)
      ↓
TRANSFORM
prefix state
      ↓
ALGORITHM
scan
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`moves`**.
2.  Ignore narrative names and rewrite the requirement using
    **`update x,y per char`**.
3.  Express the requirement mathematically as **`∃prefix=(1,1)`**.
4.  Simplify it until the recognizable form **prefix state** appears.
5.  Implement **scan** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          moves

Target:         whether path visits (1,1)

Variables:      update x,y per char

Formula:        ∃prefix=(1,1)

Transform:      prefix state

Algorithm:      scan

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    update x,y per char

DEFINE the target:
    whether path visits (1,1)

TRANSLATE the condition:
    ∃prefix=(1,1)

SIMPLIFY / TRANSFORM:
    prefix state

APPLY:
    scan

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`∃prefix=(1,1)`** and the derived method **scan**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `∃prefix=(1,1)`, test **prefix state** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 066 --- Problem 66

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1703B --- ICPC
Balloons](https://codeforces.com/problemset/problem/1703/B)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`string`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
string

WHAT MUST BE FOUND:
score first occurrence differently

MATHEMATICAL OBJECTS THAT MATTER:
first char contributes2 else1

CORE RELATION:
seen set

FINAL MATHEMATICAL VIEW:
contribution
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **seen set**, after which the useful form is **contribution**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1703B --- ICPC Balloons (Frequency / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `seen set`. Once the story is stripped away, recognize
    **contribution**, then implement **set**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
string

QUESTION:
score first occurrence differently
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
first char contributes2 else1
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
string

DERIVED QUANTITIES / USEFUL STATE:
first char contributes2 else1

UNKNOWN / ANSWER:
score first occurrence differently
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
seen set
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
seen set
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
seen set

        ↓ simplify / rearrange / classify

contribution
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
first char contributes2 else1
      ↓
EXTRACT TARGET
score first occurrence differently
      ↓
WRITE FORMULA
seen set
      ↓
TRANSFORM
contribution
      ↓
ALGORITHM
set
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`string`**.
2.  Ignore narrative names and rewrite the requirement using
    **`first char contributes2 else1`**.
3.  Express the requirement mathematically as **`seen set`**.
4.  Simplify it until the recognizable form **contribution** appears.
5.  Implement **set** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          string

Target:         score first occurrence differently

Variables:      first char contributes2 else1

Formula:        seen set

Transform:      contribution

Algorithm:      set

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    first char contributes2 else1

DEFINE the target:
    score first occurrence differently

TRANSLATE the condition:
    seen set

SIMPLIFY / TRANSFORM:
    contribution

APPLY:
    set

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`seen set`** and the derived method **set**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `seen set`, test **contribution** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 067 --- Problem 67

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1722A --- Spell
Check](https://codeforces.com/problemset/problem/1722/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`string`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
string

WHAT MUST BE FOUND:
whether permutation equals TimUR

MATHEMATICAL OBJECTS THAT MATTER:
same multiset as 'Timur'

CORE RELATION:
sort or counts

FINAL MATHEMATICAL VIEW:
canonical form
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **sort or counts**, after which the useful form is **canonical
form**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1722A --- Spell Check (Frequency/Sorting / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `sort or counts`. Once the story is stripped away,
    recognize **canonical form**, then implement **sort**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
string

QUESTION:
whether permutation equals TimUR
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
same multiset as 'Timur'
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
string

DERIVED QUANTITIES / USEFUL STATE:
same multiset as 'Timur'

UNKNOWN / ANSWER:
whether permutation equals TimUR
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
sort or counts
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
sort or counts
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
sort or counts

        ↓ simplify / rearrange / classify

canonical form
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
same multiset as 'Timur'
      ↓
EXTRACT TARGET
whether permutation equals TimUR
      ↓
WRITE FORMULA
sort or counts
      ↓
TRANSFORM
canonical form
      ↓
ALGORITHM
sort
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`string`**.
2.  Ignore narrative names and rewrite the requirement using
    **`same multiset as 'Timur'`**.
3.  Express the requirement mathematically as **`sort or counts`**.
4.  Simplify it until the recognizable form **canonical form** appears.
5.  Implement **sort** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          string

Target:         whether permutation equals TimUR

Variables:      same multiset as 'Timur'

Formula:        sort or counts

Transform:      canonical form

Algorithm:      sort

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    same multiset as 'Timur'

DEFINE the target:
    whether permutation equals TimUR

TRANSLATE the condition:
    sort or counts

SIMPLIFY / TRANSFORM:
    canonical form

APPLY:
    sort

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`sort or counts`** and the derived method **sort**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `sort or counts`, test **canonical form** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 068 --- Problem 68

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1791C --- Prepend and
Append](https://codeforces.com/problemset/problem/1791/C)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`binary string`**. After removing the
story/context, the real task is:

``` text
INPUT / GIVEN:
binary string

WHAT MUST BE FOUND:
remove unequal ends

MATHEMATICAL OBJECTS THAT MATTER:
while l<r and s[l]!=s[r]

CORE RELATION:
remaining length

FINAL MATHEMATICAL VIEW:
two pointers
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **remaining length**, after which the useful form is **two
pointers**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1791C --- Prepend and Append (Two Pointers / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `remaining length`. Once the story is stripped away,
    recognize **two pointers**, then implement **O(n)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
binary string

QUESTION:
remove unequal ends
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
while l<r and s[l]!=s[r]
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
binary string

DERIVED QUANTITIES / USEFUL STATE:
while l<r and s[l]!=s[r]

UNKNOWN / ANSWER:
remove unequal ends
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
remaining length
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
remaining length
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
remaining length

        ↓ simplify / rearrange / classify

two pointers
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
while l<r and s[l]!=s[r]
      ↓
EXTRACT TARGET
remove unequal ends
      ↓
WRITE FORMULA
remaining length
      ↓
TRANSFORM
two pointers
      ↓
ALGORITHM
O(n)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`binary string`**.
2.  Ignore narrative names and rewrite the requirement using
    **`while l<r and s[l]!=s[r]`**.
3.  Express the requirement mathematically as **`remaining length`**.
4.  Simplify it until the recognizable form **two pointers** appears.
5.  Implement **O(n)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          binary string

Target:         remove unequal ends

Variables:      while l<r and s[l]!=s[r]

Formula:        remaining length

Transform:      two pointers

Algorithm:      O(n)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    while l<r and s[l]!=s[r]

DEFINE the target:
    remove unequal ends

TRANSLATE the condition:
    remaining length

SIMPLIFY / TRANSFORM:
    two pointers

APPLY:
    O(n)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`remaining length`** and the derived method **O(n)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `remaining length`, test **two pointers** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 069 --- Problem 69

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1829D --- Gold
Rush](https://codeforces.com/problemset/problem/1829/D)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`n,m`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
n,m

WHAT MUST BE FOUND:
can reach m by splitting x into x/3 and2x/3

MATHEMATICAL OBJECTS THAT MATTER:
only split divisible by3

CORE RELATION:
DFS on decreasing states

FINAL MATHEMATICAL VIEW:
reachability
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **DFS on decreasing states**, after which the useful form is
**reachability**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1829D --- Gold Rush (Recursion/Reachability / Codeforces / 1000)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `DFS on decreasing states`. Once the story is stripped
    away, recognize **reachability**, then implement **recursion**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
n,m

QUESTION:
can reach m by splitting x into x/3 and2x/3
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
only split divisible by3
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
n,m

DERIVED QUANTITIES / USEFUL STATE:
only split divisible by3

UNKNOWN / ANSWER:
can reach m by splitting x into x/3 and2x/3
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
DFS on decreasing states
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
DFS on decreasing states
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
DFS on decreasing states

        ↓ simplify / rearrange / classify

reachability
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
only split divisible by3
      ↓
EXTRACT TARGET
can reach m by splitting x into x/3 and2x/3
      ↓
WRITE FORMULA
DFS on decreasing states
      ↓
TRANSFORM
reachability
      ↓
ALGORITHM
recursion
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`n,m`**.
2.  Ignore narrative names and rewrite the requirement using
    **`only split divisible by3`**.
3.  Express the requirement mathematically as
    **`DFS on decreasing states`**.
4.  Simplify it until the recognizable form **reachability** appears.
5.  Implement **recursion** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          n,m

Target:         can reach m by splitting x into x/3 and2x/3

Variables:      only split divisible by3

Formula:        DFS on decreasing states

Transform:      reachability

Algorithm:      recursion

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    only split divisible by3

DEFINE the target:
    can reach m by splitting x into x/3 and2x/3

TRANSLATE the condition:
    DFS on decreasing states

SIMPLIFY / TRANSFORM:
    reachability

APPLY:
    recursion

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`DFS on decreasing states`** and the derived method **recursion**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `DFS on decreasing states`, test **reachability** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 070 --- Problem 70

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1878B --- Aleksa and
Stack](https://codeforces.com/problemset/problem/1878/B)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`n`**. After removing the story/context, the real
task is:

``` text
INPUT / GIVEN:
n

WHAT MUST BE FOUND:
construct sequence satisfying divisibility condition

MATHEMATICAL OBJECTS THAT MATTER:
choose simple arithmetic sequence

CORE RELATION:
constant gap avoids divisibility

FINAL MATHEMATICAL VIEW:
construct
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **constant gap avoids divisibility**, after which the useful form is
**construct**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1878B --- Aleksa and Stack (Construction / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `constant gap avoids divisibility`. Once the story is
    stripped away, recognize **construct**, then implement **formula**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
n

QUESTION:
construct sequence satisfying divisibility condition
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
choose simple arithmetic sequence
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
n

DERIVED QUANTITIES / USEFUL STATE:
choose simple arithmetic sequence

UNKNOWN / ANSWER:
construct sequence satisfying divisibility condition
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
constant gap avoids divisibility
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
constant gap avoids divisibility
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
constant gap avoids divisibility

        ↓ simplify / rearrange / classify

construct
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
choose simple arithmetic sequence
      ↓
EXTRACT TARGET
construct sequence satisfying divisibility condition
      ↓
WRITE FORMULA
constant gap avoids divisibility
      ↓
TRANSFORM
construct
      ↓
ALGORITHM
formula
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`n`**.
2.  Ignore narrative names and rewrite the requirement using
    **`choose simple arithmetic sequence`**.
3.  Express the requirement mathematically as
    **`constant gap avoids divisibility`**.
4.  Simplify it until the recognizable form **construct** appears.
5.  Implement **formula** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          n

Target:         construct sequence satisfying divisibility condition

Variables:      choose simple arithmetic sequence

Formula:        constant gap avoids divisibility

Transform:      construct

Algorithm:      formula

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    choose simple arithmetic sequence

DEFINE the target:
    construct sequence satisfying divisibility condition

TRANSLATE the condition:
    constant gap avoids divisibility

SIMPLIFY / TRANSFORM:
    construct

APPLY:
    formula

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`constant gap avoids divisibility`** and the derived method
    **formula**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `constant gap avoids divisibility`, test **construct** immediately.

# Pattern 8 --- Operation → Delta → Invariant

## Pattern Overview

Write one operation as BEFORE → AFTER. Compute what changes and what
remains invariant: sum, parity, difference, GCD, XOR, or a monotone
quantity.

``` text
Different CF stories
       ↓ remove nouns
Same mathematical family
       ↓
Operation → Delta → Invariant
```

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 071 --- Problem 71

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1538B --- Friends and
Candies](https://codeforces.com/problemset/problem/1538/B)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`array`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
array

WHAT MUST BE FOUND:
equalize while preserving sum

MATHEMATICAL OBJECTS THAT MATTER:
S=n*x

CORE RELATION:
S%n=0

FINAL MATHEMATICAL VIEW:
average invariant
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **S%n=0**, after which the useful form is **average invariant**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1538B --- Friends and Candies (Invariant / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `S%n=0`. Once the story is stripped away, recognize
    **average invariant**, then implement **count \>avg**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
array

QUESTION:
equalize while preserving sum
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
S=n*x
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
array

DERIVED QUANTITIES / USEFUL STATE:
S=n*x

UNKNOWN / ANSWER:
equalize while preserving sum
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
S%n=0
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
S%n=0
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
S%n=0

        ↓ simplify / rearrange / classify

average invariant
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
S=n*x
      ↓
EXTRACT TARGET
equalize while preserving sum
      ↓
WRITE FORMULA
S%n=0
      ↓
TRANSFORM
average invariant
      ↓
ALGORITHM
count >avg
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`array`**.
2.  Ignore narrative names and rewrite the requirement using
    **`S=n*x`**.
3.  Express the requirement mathematically as **`S%n=0`**.
4.  Simplify it until the recognizable form **average invariant**
    appears.
5.  Implement **count \>avg** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          array

Target:         equalize while preserving sum

Variables:      S=n*x

Formula:        S%n=0

Transform:      average invariant

Algorithm:      count >avg

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    S=n*x

DEFINE the target:
    equalize while preserving sum

TRANSLATE the condition:
    S%n=0

SIMPLIFY / TRANSFORM:
    average invariant

APPLY:
    count >avg

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`S%n=0`** and the derived method **count \>avg**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `S%n=0`, test **average invariant** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 072 --- Problem 72

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1855A --- Dalton the
Teacher](https://codeforces.com/problemset/problem/1855/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`permutation`**. After removing the story/context,
the real task is:

``` text
INPUT / GIVEN:
permutation

WHAT MUST BE FOUND:
minimum operations fixing fixed points by pair operation

MATHEMATICAL OBJECTS THAT MATTER:
each op can fix at most2 fixed points

CORE RELATION:
ceil(fixed/2)

FINAL MATHEMATICAL VIEW:
count fixed
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **ceil(fixed/2)**, after which the useful form is **count fixed**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1855A --- Dalton the Teacher (Mismatch/Operation / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `ceil(fixed/2)`. Once the story is stripped away,
    recognize **count fixed**, then implement **(cnt+1)/2**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
permutation

QUESTION:
minimum operations fixing fixed points by pair operation
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
each op can fix at most2 fixed points
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
permutation

DERIVED QUANTITIES / USEFUL STATE:
each op can fix at most2 fixed points

UNKNOWN / ANSWER:
minimum operations fixing fixed points by pair operation
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
ceil(fixed/2)
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
ceil(fixed/2)
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
ceil(fixed/2)

        ↓ simplify / rearrange / classify

count fixed
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
each op can fix at most2 fixed points
      ↓
EXTRACT TARGET
minimum operations fixing fixed points by pair operation
      ↓
WRITE FORMULA
ceil(fixed/2)
      ↓
TRANSFORM
count fixed
      ↓
ALGORITHM
(cnt+1)/2
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`permutation`**.
2.  Ignore narrative names and rewrite the requirement using
    **`each op can fix at most2 fixed points`**.
3.  Express the requirement mathematically as **`ceil(fixed/2)`**.
4.  Simplify it until the recognizable form **count fixed** appears.
5.  Implement **(cnt+1)/2** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          permutation

Target:         minimum operations fixing fixed points by pair operation

Variables:      each op can fix at most2 fixed points

Formula:        ceil(fixed/2)

Transform:      count fixed

Algorithm:      (cnt+1)/2

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    each op can fix at most2 fixed points

DEFINE the target:
    minimum operations fixing fixed points by pair operation

TRANSLATE the condition:
    ceil(fixed/2)

SIMPLIFY / TRANSFORM:
    count fixed

APPLY:
    (cnt+1)/2

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`ceil(fixed/2)`** and the derived method **(cnt+1)/2**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `ceil(fixed/2)`, test **count fixed** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 073 --- Problem 73

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1838A --- Blackboard
List](https://codeforces.com/problemset/problem/1838/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`array`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
array

WHAT MUST BE FOUND:
recover original special number

MATHEMATICAL OBJECTS THAT MATTER:
negative minimum survives construction; else maximum

CORE RELATION:
extremal invariant

FINAL MATHEMATICAL VIEW:
min if negative else max
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **extremal invariant**, after which the useful form is **min if
negative else max**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1838A --- Blackboard List (Extremal / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `extremal invariant`. Once the story is stripped away,
    recognize **min if negative else max**, then implement **scan**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
array

QUESTION:
recover original special number
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
negative minimum survives construction; else maximum
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
array

DERIVED QUANTITIES / USEFUL STATE:
negative minimum survives construction; else maximum

UNKNOWN / ANSWER:
recover original special number
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
extremal invariant
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
extremal invariant
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
extremal invariant

        ↓ simplify / rearrange / classify

min if negative else max
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
negative minimum survives construction; else maximum
      ↓
EXTRACT TARGET
recover original special number
      ↓
WRITE FORMULA
extremal invariant
      ↓
TRANSFORM
min if negative else max
      ↓
ALGORITHM
scan
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`array`**.
2.  Ignore narrative names and rewrite the requirement using
    **`negative minimum survives construction; else maximum`**.
3.  Express the requirement mathematically as **`extremal invariant`**.
4.  Simplify it until the recognizable form **min if negative else max**
    appears.
5.  Implement **scan** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          array

Target:         recover original special number

Variables:      negative minimum survives construction; else maximum

Formula:        extremal invariant

Transform:      min if negative else max

Algorithm:      scan

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    negative minimum survives construction; else maximum

DEFINE the target:
    recover original special number

TRANSLATE the condition:
    extremal invariant

SIMPLIFY / TRANSFORM:
    min if negative else max

APPLY:
    scan

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`extremal invariant`** and the derived method **scan**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `extremal invariant`, test **min if negative else max** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 074 --- Problem 74

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1862B --- Sequence
Game](https://codeforces.com/problemset/problem/1862/B)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`sequence b`**. After removing the story/context,
the real task is:

``` text
INPUT / GIVEN:
sequence b

WHAT MUST BE FOUND:
construct a so filtering rule returns b

MATHEMATICAL OBJECTS THAT MATTER:
insert bridge when b[i-1]>b[i]

CORE RELATION:
local condition

FINAL MATHEMATICAL VIEW:
construct with extra value
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **local condition**, after which the useful form is **construct with
extra value**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1862B --- Sequence Game (Construction / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `local condition`. Once the story is stripped away,
    recognize **construct with extra value**, then implement **linear**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
sequence b

QUESTION:
construct a so filtering rule returns b
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
insert bridge when b[i-1]>b[i]
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
sequence b

DERIVED QUANTITIES / USEFUL STATE:
insert bridge when b[i-1]>b[i]

UNKNOWN / ANSWER:
construct a so filtering rule returns b
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
local condition
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
local condition
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
local condition

        ↓ simplify / rearrange / classify

construct with extra value
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
insert bridge when b[i-1]>b[i]
      ↓
EXTRACT TARGET
construct a so filtering rule returns b
      ↓
WRITE FORMULA
local condition
      ↓
TRANSFORM
construct with extra value
      ↓
ALGORITHM
linear
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`sequence b`**.
2.  Ignore narrative names and rewrite the requirement using
    **`insert bridge when b[i-1]>b[i]`**.
3.  Express the requirement mathematically as **`local condition`**.
4.  Simplify it until the recognizable form **construct with extra
    value** appears.
5.  Implement **linear** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          sequence b

Target:         construct a so filtering rule returns b

Variables:      insert bridge when b[i-1]>b[i]

Formula:        local condition

Transform:      construct with extra value

Algorithm:      linear

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    insert bridge when b[i-1]>b[i]

DEFINE the target:
    construct a so filtering rule returns b

TRANSLATE the condition:
    local condition

SIMPLIFY / TRANSFORM:
    construct with extra value

APPLY:
    linear

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`local condition`** and the derived method **linear**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `local condition`, test **construct with extra value** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 075 --- Problem 75

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1798A ---
Showstopper](https://codeforces.com/problemset/problem/1798/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`two arrays`**. After removing the story/context,
the real task is:

``` text
INPUT / GIVEN:
two arrays

WHAT MUST BE FOUND:
can swap pairs so last elements are maxima

MATHEMATICAL OBJECTS THAT MATTER:
each pair independently orientable

CORE RELATION:
need max pair endpoints fit final

FINAL MATHEMATICAL VIEW:
normalize max/min
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **need max pair endpoints fit final**, after which the useful form is
**normalize max/min**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1798A --- Showstopper (Invariant/Swap / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `need max pair endpoints fit final`. Once the story is
    stripped away, recognize **normalize max/min**, then implement
    **check**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
two arrays

QUESTION:
can swap pairs so last elements are maxima
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
each pair independently orientable
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
two arrays

DERIVED QUANTITIES / USEFUL STATE:
each pair independently orientable

UNKNOWN / ANSWER:
can swap pairs so last elements are maxima
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
need max pair endpoints fit final
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
need max pair endpoints fit final
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
need max pair endpoints fit final

        ↓ simplify / rearrange / classify

normalize max/min
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
each pair independently orientable
      ↓
EXTRACT TARGET
can swap pairs so last elements are maxima
      ↓
WRITE FORMULA
need max pair endpoints fit final
      ↓
TRANSFORM
normalize max/min
      ↓
ALGORITHM
check
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`two arrays`**.
2.  Ignore narrative names and rewrite the requirement using
    **`each pair independently orientable`**.
3.  Express the requirement mathematically as
    **`need max pair endpoints fit final`**.
4.  Simplify it until the recognizable form **normalize max/min**
    appears.
5.  Implement **check** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          two arrays

Target:         can swap pairs so last elements are maxima

Variables:      each pair independently orientable

Formula:        need max pair endpoints fit final

Transform:      normalize max/min

Algorithm:      check

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    each pair independently orientable

DEFINE the target:
    can swap pairs so last elements are maxima

TRANSLATE the condition:
    need max pair endpoints fit final

SIMPLIFY / TRANSFORM:
    normalize max/min

APPLY:
    check

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`need max pair endpoints fit final`** and the derived method
    **check**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `need max pair endpoints fit final`, test **normalize max/min**
    immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 076 --- Problem 76

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 660A --- Co-prime
Array](https://codeforces.com/problemset/problem/660/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`array`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
array

WHAT MUST BE FOUND:
insert minimum numbers so adjacent gcd=1

MATHEMATICAL OBJECTS THAT MATTER:
if gcd(a[i],a[i+1])>1 insert coprime sentinel

CORE RELATION:
local repair

FINAL MATHEMATICAL VIEW:
insert 1
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **local repair**, after which the useful form is **insert 1**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 660A --- Co-prime Array (Construction/GCD / Codeforces / 900)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `local repair`. Once the story is stripped away,
    recognize **insert 1**, then implement **linear**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
array

QUESTION:
insert minimum numbers so adjacent gcd=1
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
if gcd(a[i],a[i+1])>1 insert coprime sentinel
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
array

DERIVED QUANTITIES / USEFUL STATE:
if gcd(a[i],a[i+1])>1 insert coprime sentinel

UNKNOWN / ANSWER:
insert minimum numbers so adjacent gcd=1
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
local repair
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
local repair
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
local repair

        ↓ simplify / rearrange / classify

insert 1
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
if gcd(a[i],a[i+1])>1 insert coprime sentinel
      ↓
EXTRACT TARGET
insert minimum numbers so adjacent gcd=1
      ↓
WRITE FORMULA
local repair
      ↓
TRANSFORM
insert 1
      ↓
ALGORITHM
linear
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`array`**.
2.  Ignore narrative names and rewrite the requirement using
    **`if gcd(a[i],a[i+1])>1 insert coprime sentinel`**.
3.  Express the requirement mathematically as **`local repair`**.
4.  Simplify it until the recognizable form **insert 1** appears.
5.  Implement **linear** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          array

Target:         insert minimum numbers so adjacent gcd=1

Variables:      if gcd(a[i],a[i+1])>1 insert coprime sentinel

Formula:        local repair

Transform:      insert 1

Algorithm:      linear

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    if gcd(a[i],a[i+1])>1 insert coprime sentinel

DEFINE the target:
    insert minimum numbers so adjacent gcd=1

TRANSLATE the condition:
    local repair

SIMPLIFY / TRANSFORM:
    insert 1

APPLY:
    linear

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`local repair`** and the derived method **linear**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `local repair`, test **insert 1** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 077 --- Problem 77

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1367A --- Short
Substrings](https://codeforces.com/problemset/problem/1367/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`string b`**. After removing the story/context,
the real task is:

``` text
INPUT / GIVEN:
string b

WHAT MUST BE FOUND:
recover original

MATHEMATICAL OBJECTS THAT MATTER:
overlap pairs share char

CORE RELATION:
take first then every second char

FINAL MATHEMATICAL VIEW:
inverse operation
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **take first then every second char**, after which the useful form is
**inverse operation**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1367A --- Short Substrings (String Reconstruction / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `take first then every second char`. Once the story is
    stripped away, recognize **inverse operation**, then implement
    **construct**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
string b

QUESTION:
recover original
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
overlap pairs share char
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
string b

DERIVED QUANTITIES / USEFUL STATE:
overlap pairs share char

UNKNOWN / ANSWER:
recover original
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
take first then every second char
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
take first then every second char
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
take first then every second char

        ↓ simplify / rearrange / classify

inverse operation
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
overlap pairs share char
      ↓
EXTRACT TARGET
recover original
      ↓
WRITE FORMULA
take first then every second char
      ↓
TRANSFORM
inverse operation
      ↓
ALGORITHM
construct
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`string b`**.
2.  Ignore narrative names and rewrite the requirement using
    **`overlap pairs share char`**.
3.  Express the requirement mathematically as
    **`take first then every second char`**.
4.  Simplify it until the recognizable form **inverse operation**
    appears.
5.  Implement **construct** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          string b

Target:         recover original

Variables:      overlap pairs share char

Formula:        take first then every second char

Transform:      inverse operation

Algorithm:      construct

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    overlap pairs share char

DEFINE the target:
    recover original

TRANSLATE the condition:
    take first then every second char

SIMPLIFY / TRANSFORM:
    inverse operation

APPLY:
    construct

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`take first then every second char`** and the derived method
    **construct**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `take first then every second char`, test **inverse operation**
    immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 078 --- Problem 78

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1374A --- Required
Remainder](https://codeforces.com/problemset/problem/1374/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`x,y,n`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
x,y,n

WHAT MUST BE FOUND:
largest k<=n with k%x=y

MATHEMATICAL OBJECTS THAT MATTER:
numbers are tx+y

CORE RELATION:
t=floor((n-y)/x)

FINAL MATHEMATICAL VIEW:
largest feasible
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **t=floor((n-y)/x)**, after which the useful form is **largest
feasible**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1374A --- Required Remainder (Modulo/Optimization / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `t=floor((n-y)/x)`. Once the story is stripped away,
    recognize **largest feasible**, then implement **formula**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
x,y,n

QUESTION:
largest k<=n with k%x=y
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
numbers are tx+y
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
x,y,n

DERIVED QUANTITIES / USEFUL STATE:
numbers are tx+y

UNKNOWN / ANSWER:
largest k<=n with k%x=y
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
t=floor((n-y)/x)
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
t=floor((n-y)/x)
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
t=floor((n-y)/x)

        ↓ simplify / rearrange / classify

largest feasible
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
numbers are tx+y
      ↓
EXTRACT TARGET
largest k<=n with k%x=y
      ↓
WRITE FORMULA
t=floor((n-y)/x)
      ↓
TRANSFORM
largest feasible
      ↓
ALGORITHM
formula
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`x,y,n`**.
2.  Ignore narrative names and rewrite the requirement using
    **`numbers are tx+y`**.
3.  Express the requirement mathematically as **`t=floor((n-y)/x)`**.
4.  Simplify it until the recognizable form **largest feasible**
    appears.
5.  Implement **formula** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          x,y,n

Target:         largest k<=n with k%x=y

Variables:      numbers are tx+y

Formula:        t=floor((n-y)/x)

Transform:      largest feasible

Algorithm:      formula

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    numbers are tx+y

DEFINE the target:
    largest k<=n with k%x=y

TRANSLATE the condition:
    t=floor((n-y)/x)

SIMPLIFY / TRANSFORM:
    largest feasible

APPLY:
    formula

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`t=floor((n-y)/x)`** and the derived method **formula**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `t=floor((n-y)/x)`, test **largest feasible** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 079 --- Problem 79

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1551A --- Polycarp and
Coins](https://codeforces.com/problemset/problem/1551/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`n`**. After removing the story/context, the real
task is:

``` text
INPUT / GIVEN:
n

WHAT MUST BE FOUND:
split n into 1-coin and2-coin counts minimizing difference

MATHEMATICAL OBJECTS THAT MATTER:
c1+2c2=n, |c1-c2| min

CORE RELATION:
near n/3

FINAL MATHEMATICAL VIEW:
balanced equation
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **near n/3**, after which the useful form is **balanced equation**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1551A --- Polycarp and Coins (Balancing / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `near n/3`. Once the story is stripped away, recognize
    **balanced equation**, then implement **n%3 cases**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
n

QUESTION:
split n into 1-coin and2-coin counts minimizing difference
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
c1+2c2=n, |c1-c2| min
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
n

DERIVED QUANTITIES / USEFUL STATE:
c1+2c2=n, |c1-c2| min

UNKNOWN / ANSWER:
split n into 1-coin and2-coin counts minimizing difference
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
near n/3
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
near n/3
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
near n/3

        ↓ simplify / rearrange / classify

balanced equation
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
c1+2c2=n, |c1-c2| min
      ↓
EXTRACT TARGET
split n into 1-coin and2-coin counts minimizing difference
      ↓
WRITE FORMULA
near n/3
      ↓
TRANSFORM
balanced equation
      ↓
ALGORITHM
n%3 cases
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`n`**.
2.  Ignore narrative names and rewrite the requirement using
    **`c1+2c2=n, |c1-c2| min`**.
3.  Express the requirement mathematically as **`near n/3`**.
4.  Simplify it until the recognizable form **balanced equation**
    appears.
5.  Implement **n%3 cases** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          n

Target:         split n into 1-coin and2-coin counts minimizing difference

Variables:      c1+2c2=n, |c1-c2| min

Formula:        near n/3

Transform:      balanced equation

Algorithm:      n%3 cases

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    c1+2c2=n, |c1-c2| min

DEFINE the target:
    split n into 1-coin and2-coin counts minimizing difference

TRANSLATE the condition:
    near n/3

SIMPLIFY / TRANSFORM:
    balanced equation

APPLY:
    n%3 cases

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`near n/3`** and the derived method **n%3 cases**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `near n/3`, test **balanced equation** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 080 --- Problem 80

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1818A ---
Politics](https://codeforces.com/problemset/problem/1818/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`strings`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
strings

WHAT MUST BE FOUND:
count strings compatible with reference

MATHEMATICAL OBJECTS THAT MATTER:
positions with reference 1 impose equality

CORE RELATION:
predicate per string

FINAL MATHEMATICAL VIEW:
count valid
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **predicate per string**, after which the useful form is **count
valid**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1818A --- Politics (String/Counting / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `predicate per string`. Once the story is stripped
    away, recognize **count valid**, then implement **nested scan**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
strings

QUESTION:
count strings compatible with reference
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
positions with reference 1 impose equality
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
strings

DERIVED QUANTITIES / USEFUL STATE:
positions with reference 1 impose equality

UNKNOWN / ANSWER:
count strings compatible with reference
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
predicate per string
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
predicate per string
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
predicate per string

        ↓ simplify / rearrange / classify

count valid
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
positions with reference 1 impose equality
      ↓
EXTRACT TARGET
count strings compatible with reference
      ↓
WRITE FORMULA
predicate per string
      ↓
TRANSFORM
count valid
      ↓
ALGORITHM
nested scan
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`strings`**.
2.  Ignore narrative names and rewrite the requirement using
    **`positions with reference 1 impose equality`**.
3.  Express the requirement mathematically as
    **`predicate per string`**.
4.  Simplify it until the recognizable form **count valid** appears.
5.  Implement **nested scan** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          strings

Target:         count strings compatible with reference

Variables:      positions with reference 1 impose equality

Formula:        predicate per string

Transform:      count valid

Algorithm:      nested scan

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    positions with reference 1 impose equality

DEFINE the target:
    count strings compatible with reference

TRANSLATE the condition:
    predicate per string

SIMPLIFY / TRANSFORM:
    count valid

APPLY:
    nested scan

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`predicate per string`** and the derived method **nested scan**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `predicate per string`, test **count valid** immediately.

# Pattern 9 --- Sorting / Coordinate / Distance Modeling

## Pattern Overview

Use sorting to remove irrelevant order and expose adjacency, extremes,
or matching. Use `|x-y|` for number-line distance.

``` text
Different CF stories
       ↓ remove nouns
Same mathematical family
       ↓
Sorting / Coordinate / Distance Modeling
```

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 081 --- Problem 81

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 160A ---
Twins](https://codeforces.com/problemset/problem/160/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`coins`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
coins

WHAT MUST BE FOUND:
minimum selected sum > rest

MATHEMATICAL OBJECTS THAT MATTER:
sort descending

CORE RELATION:
prefix until 2sum>total

FINAL MATHEMATICAL VIEW:
extremal choice
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **prefix until 2sum\>total**, after which the useful form is
**extremal choice**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 160A --- Twins (Sorting/Greedy / Codeforces / 900)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `prefix until 2sum>total`. Once the story is stripped
    away, recognize **extremal choice**, then implement **O(nlogn)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
coins

QUESTION:
minimum selected sum > rest
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
sort descending
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
coins

DERIVED QUANTITIES / USEFUL STATE:
sort descending

UNKNOWN / ANSWER:
minimum selected sum > rest
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
prefix until 2sum>total
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
prefix until 2sum>total
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
prefix until 2sum>total

        ↓ simplify / rearrange / classify

extremal choice
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
sort descending
      ↓
EXTRACT TARGET
minimum selected sum > rest
      ↓
WRITE FORMULA
prefix until 2sum>total
      ↓
TRANSFORM
extremal choice
      ↓
ALGORITHM
O(nlogn)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`coins`**.
2.  Ignore narrative names and rewrite the requirement using
    **`sort descending`**.
3.  Express the requirement mathematically as
    **`prefix until 2sum>total`**.
4.  Simplify it until the recognizable form **extremal choice** appears.
5.  Implement **O(nlogn)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          coins

Target:         minimum selected sum > rest

Variables:      sort descending

Formula:        prefix until 2sum>total

Transform:      extremal choice

Algorithm:      O(nlogn)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    sort descending

DEFINE the target:
    minimum selected sum > rest

TRANSLATE the condition:
    prefix until 2sum>total

SIMPLIFY / TRANSFORM:
    extremal choice

APPLY:
    O(nlogn)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`prefix until 2sum>total`** and the derived method **O(nlogn)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `prefix until 2sum>total`, test **extremal choice** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 082 --- Problem 82

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1399A --- Remove
Smallest](https://codeforces.com/problemset/problem/1399/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`array`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
array

WHAT MUST BE FOUND:
can delete until one remains under diff<=1

MATHEMATICAL OBJECTS THAT MATTER:
sort; all adjacent gaps<=1

CORE RELATION:
adjacent condition

FINAL MATHEMATICAL VIEW:
check
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **adjacent condition**, after which the useful form is **check**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1399A --- Remove Smallest (Sorting / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `adjacent condition`. Once the story is stripped away,
    recognize **check**, then implement **O(nlogn)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
array

QUESTION:
can delete until one remains under diff<=1
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
sort; all adjacent gaps<=1
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
array

DERIVED QUANTITIES / USEFUL STATE:
sort; all adjacent gaps<=1

UNKNOWN / ANSWER:
can delete until one remains under diff<=1
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
adjacent condition
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
adjacent condition
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
adjacent condition

        ↓ simplify / rearrange / classify

check
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
sort; all adjacent gaps<=1
      ↓
EXTRACT TARGET
can delete until one remains under diff<=1
      ↓
WRITE FORMULA
adjacent condition
      ↓
TRANSFORM
check
      ↓
ALGORITHM
O(nlogn)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`array`**.
2.  Ignore narrative names and rewrite the requirement using
    **`sort; all adjacent gaps<=1`**.
3.  Express the requirement mathematically as **`adjacent condition`**.
4.  Simplify it until the recognizable form **check** appears.
5.  Implement **O(nlogn)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          array

Target:         can delete until one remains under diff<=1

Variables:      sort; all adjacent gaps<=1

Formula:        adjacent condition

Transform:      check

Algorithm:      O(nlogn)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    sort; all adjacent gaps<=1

DEFINE the target:
    can delete until one remains under diff<=1

TRANSLATE the condition:
    adjacent condition

SIMPLIFY / TRANSFORM:
    check

APPLY:
    O(nlogn)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`adjacent condition`** and the derived method **O(nlogn)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `adjacent condition`, test **check** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 083 --- Problem 83

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1760A --- Medium
Number](https://codeforces.com/problemset/problem/1760/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`a,b,c`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
a,b,c

WHAT MUST BE FOUND:
middle value

MATHEMATICAL OBJECTS THAT MATTER:
sort three

CORE RELATION:
second element

FINAL MATHEMATICAL VIEW:
median
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **second element**, after which the useful form is **median**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1760A --- Medium Number (Sorting / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `second element`. Once the story is stripped away,
    recognize **median**, then implement **O(1)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
a,b,c

QUESTION:
middle value
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
sort three
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
a,b,c

DERIVED QUANTITIES / USEFUL STATE:
sort three

UNKNOWN / ANSWER:
middle value
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
second element
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
second element
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
second element

        ↓ simplify / rearrange / classify

median
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
sort three
      ↓
EXTRACT TARGET
middle value
      ↓
WRITE FORMULA
second element
      ↓
TRANSFORM
median
      ↓
ALGORITHM
O(1)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`a,b,c`**.
2.  Ignore narrative names and rewrite the requirement using
    **`sort three`**.
3.  Express the requirement mathematically as **`second element`**.
4.  Simplify it until the recognizable form **median** appears.
5.  Implement **O(1)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          a,b,c

Target:         middle value

Variables:      sort three

Formula:        second element

Transform:      median

Algorithm:      O(1)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    sort three

DEFINE the target:
    middle value

TRANSLATE the condition:
    second element

SIMPLIFY / TRANSFORM:
    median

APPLY:
    O(1)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`second element`** and the derived method **O(1)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `second element`, test **median** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 084 --- Problem 84

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1538A --- Stone
Game](https://codeforces.com/problemset/problem/1538/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`permutation`**. After removing the story/context,
the real task is:

``` text
INPUT / GIVEN:
permutation

WHAT MUST BE FOUND:
min removals from ends to remove min and max

MATHEMATICAL OBJECTS THAT MATTER:
positions pmin,pmax

CORE RELATION:
min of three strategies

FINAL MATHEMATICAL VIEW:
distance to ends
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **min of three strategies**, after which the useful form is
**distance to ends**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1538A --- Stone Game (Positions / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `min of three strategies`. Once the story is stripped
    away, recognize **distance to ends**, then implement **formula**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
permutation

QUESTION:
min removals from ends to remove min and max
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
positions pmin,pmax
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
permutation

DERIVED QUANTITIES / USEFUL STATE:
positions pmin,pmax

UNKNOWN / ANSWER:
min removals from ends to remove min and max
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
min of three strategies
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
min of three strategies
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
min of three strategies

        ↓ simplify / rearrange / classify

distance to ends
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
positions pmin,pmax
      ↓
EXTRACT TARGET
min removals from ends to remove min and max
      ↓
WRITE FORMULA
min of three strategies
      ↓
TRANSFORM
distance to ends
      ↓
ALGORITHM
formula
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`permutation`**.
2.  Ignore narrative names and rewrite the requirement using
    **`positions pmin,pmax`**.
3.  Express the requirement mathematically as
    **`min of three strategies`**.
4.  Simplify it until the recognizable form **distance to ends**
    appears.
5.  Implement **formula** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          permutation

Target:         min removals from ends to remove min and max

Variables:      positions pmin,pmax

Formula:        min of three strategies

Transform:      distance to ends

Algorithm:      formula

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    positions pmin,pmax

DEFINE the target:
    min removals from ends to remove min and max

TRANSLATE the condition:
    min of three strategies

SIMPLIFY / TRANSFORM:
    distance to ends

APPLY:
    formula

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`min of three strategies`** and the derived method **formula**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `min of three strategies`, test **distance to ends** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 085 --- Problem 85

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1729A --- Two
Elevators](https://codeforces.com/problemset/problem/1729/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`a,b,c`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
a,b,c

WHAT MUST BE FOUND:
compare travel times

MATHEMATICAL OBJECTS THAT MATTER:
t1=a-1, t2=|b-c|+c-1

CORE RELATION:
absolute distance

FINAL MATHEMATICAL VIEW:
compare
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **absolute distance**, after which the useful form is **compare**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1729A --- Two Elevators (Distance / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `absolute distance`. Once the story is stripped away,
    recognize **compare**, then implement **O(1)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
a,b,c

QUESTION:
compare travel times
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
t1=a-1, t2=|b-c|+c-1
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
a,b,c

DERIVED QUANTITIES / USEFUL STATE:
t1=a-1, t2=|b-c|+c-1

UNKNOWN / ANSWER:
compare travel times
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
absolute distance
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
absolute distance
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
absolute distance

        ↓ simplify / rearrange / classify

compare
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
t1=a-1, t2=|b-c|+c-1
      ↓
EXTRACT TARGET
compare travel times
      ↓
WRITE FORMULA
absolute distance
      ↓
TRANSFORM
compare
      ↓
ALGORITHM
O(1)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`a,b,c`**.
2.  Ignore narrative names and rewrite the requirement using
    **`t1=a-1, t2=|b-c|+c-1`**.
3.  Express the requirement mathematically as **`absolute distance`**.
4.  Simplify it until the recognizable form **compare** appears.
5.  Implement **O(1)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          a,b,c

Target:         compare travel times

Variables:      t1=a-1, t2=|b-c|+c-1

Formula:        absolute distance

Transform:      compare

Algorithm:      O(1)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    t1=a-1, t2=|b-c|+c-1

DEFINE the target:
    compare travel times

TRANSLATE the condition:
    absolute distance

SIMPLIFY / TRANSFORM:
    compare

APPLY:
    O(1)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`absolute distance`** and the derived method **O(1)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `absolute distance`, test **compare** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 086 --- Problem 86

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1593B --- Make it Divisible by
25](https://codeforces.com/problemset/problem/1593/B)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`string number`**. After removing the
story/context, the real task is:

``` text
INPUT / GIVEN:
string number

WHAT MUST BE FOUND:
min deletions for divisible by25

MATHEMATICAL OBJECTS THAT MATTER:
last two digits in {00,25,50,75}

CORE RELATION:
find pair from right

FINAL MATHEMATICAL VIEW:
pattern search
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **find pair from right**, after which the useful form is **pattern
search**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1593B --- Make it Divisible by 25 (Digit Pattern / Codeforces / 900)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `find pair from right`. Once the story is stripped
    away, recognize **pattern search**, then implement **O(n)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
string number

QUESTION:
min deletions for divisible by25
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
last two digits in {00,25,50,75}
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
string number

DERIVED QUANTITIES / USEFUL STATE:
last two digits in {00,25,50,75}

UNKNOWN / ANSWER:
min deletions for divisible by25
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
find pair from right
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
find pair from right
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
find pair from right

        ↓ simplify / rearrange / classify

pattern search
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
last two digits in {00,25,50,75}
      ↓
EXTRACT TARGET
min deletions for divisible by25
      ↓
WRITE FORMULA
find pair from right
      ↓
TRANSFORM
pattern search
      ↓
ALGORITHM
O(n)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`string number`**.
2.  Ignore narrative names and rewrite the requirement using
    **`last two digits in {00,25,50,75}`**.
3.  Express the requirement mathematically as
    **`find pair from right`**.
4.  Simplify it until the recognizable form **pattern search** appears.
5.  Implement **O(n)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          string number

Target:         min deletions for divisible by25

Variables:      last two digits in {00,25,50,75}

Formula:        find pair from right

Transform:      pattern search

Algorithm:      O(n)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    last two digits in {00,25,50,75}

DEFINE the target:
    min deletions for divisible by25

TRANSLATE the condition:
    find pair from right

SIMPLIFY / TRANSFORM:
    pattern search

APPLY:
    O(n)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`find pair from right`** and the derived method **O(n)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `find pair from right`, test **pattern search** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 087 --- Problem 87

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1742F ---
Smaller](https://codeforces.com/problemset/problem/1742/F)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`string append queries`**. After removing the
story/context, the real task is:

``` text
INPUT / GIVEN:
string append queries

WHAT MUST BE FOUND:
whether s<t possible

MATHEMATICAL OBJECTS THAT MATTER:
presence of char >'a' dominates

CORE RELATION:
track counts/flags

FINAL MATHEMATICAL VIEW:
compressed state
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **track counts/flags**, after which the useful form is **compressed
state**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1742F --- Smaller (Lexicographic/Invariant / Codeforces / 1200)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `track counts/flags`. Once the story is stripped away,
    recognize **compressed state**, then implement **O(q)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
string append queries

QUESTION:
whether s<t possible
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
presence of char >'a' dominates
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
string append queries

DERIVED QUANTITIES / USEFUL STATE:
presence of char >'a' dominates

UNKNOWN / ANSWER:
whether s<t possible
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
track counts/flags
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
track counts/flags
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
track counts/flags

        ↓ simplify / rearrange / classify

compressed state
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
presence of char >'a' dominates
      ↓
EXTRACT TARGET
whether s<t possible
      ↓
WRITE FORMULA
track counts/flags
      ↓
TRANSFORM
compressed state
      ↓
ALGORITHM
O(q)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`string append queries`**.
2.  Ignore narrative names and rewrite the requirement using
    **`presence of char >'a' dominates`**.
3.  Express the requirement mathematically as **`track counts/flags`**.
4.  Simplify it until the recognizable form **compressed state**
    appears.
5.  Implement **O(q)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          string append queries

Target:         whether s<t possible

Variables:      presence of char >'a' dominates

Formula:        track counts/flags

Transform:      compressed state

Algorithm:      O(q)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    presence of char >'a' dominates

DEFINE the target:
    whether s<t possible

TRANSLATE the condition:
    track counts/flags

SIMPLIFY / TRANSFORM:
    compressed state

APPLY:
    O(q)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`track counts/flags`** and the derived method **O(q)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `track counts/flags`, test **compressed state** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 088 --- Problem 88

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1831A --- Twin
Permutations](https://codeforces.com/problemset/problem/1831/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`permutation`**. After removing the story/context,
the real task is:

``` text
INPUT / GIVEN:
permutation

WHAT MUST BE FOUND:
construct complementary permutation

MATHEMATICAL OBJECTS THAT MATTER:
b[i]=n+1-a[i]

CORE RELATION:
value reflection

FINAL MATHEMATICAL VIEW:
direct transform
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **value reflection**, after which the useful form is **direct
transform**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1831A --- Twin Permutations (Mapping / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `value reflection`. Once the story is stripped away,
    recognize **direct transform**, then implement **O(n)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
permutation

QUESTION:
construct complementary permutation
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
b[i]=n+1-a[i]
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
permutation

DERIVED QUANTITIES / USEFUL STATE:
b[i]=n+1-a[i]

UNKNOWN / ANSWER:
construct complementary permutation
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
value reflection
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
value reflection
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
value reflection

        ↓ simplify / rearrange / classify

direct transform
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
b[i]=n+1-a[i]
      ↓
EXTRACT TARGET
construct complementary permutation
      ↓
WRITE FORMULA
value reflection
      ↓
TRANSFORM
direct transform
      ↓
ALGORITHM
O(n)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`permutation`**.
2.  Ignore narrative names and rewrite the requirement using
    **`b[i]=n+1-a[i]`**.
3.  Express the requirement mathematically as **`value reflection`**.
4.  Simplify it until the recognizable form **direct transform**
    appears.
5.  Implement **O(n)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          permutation

Target:         construct complementary permutation

Variables:      b[i]=n+1-a[i]

Formula:        value reflection

Transform:      direct transform

Algorithm:      O(n)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    b[i]=n+1-a[i]

DEFINE the target:
    construct complementary permutation

TRANSLATE the condition:
    value reflection

SIMPLIFY / TRANSFORM:
    direct transform

APPLY:
    O(n)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`value reflection`** and the derived method **O(n)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `value reflection`, test **direct transform** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 089 --- Problem 89

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1900A --- Cover in
Water](https://codeforces.com/problemset/problem/1900/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`string`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
string

WHAT MUST BE FOUND:
minimum operations to fill dots

MATHEMATICAL OBJECTS THAT MATTER:
run of >=3 triggers shortcut; else count dots

CORE RELATION:
local pattern

FINAL MATHEMATICAL VIEW:
case split
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **local pattern**, after which the useful form is **case split**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1900A --- Cover in Water (Run Length / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `local pattern`. Once the story is stripped away,
    recognize **case split**, then implement **scan**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
string

QUESTION:
minimum operations to fill dots
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
run of >=3 triggers shortcut; else count dots
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
string

DERIVED QUANTITIES / USEFUL STATE:
run of >=3 triggers shortcut; else count dots

UNKNOWN / ANSWER:
minimum operations to fill dots
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
local pattern
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
local pattern
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
local pattern

        ↓ simplify / rearrange / classify

case split
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
run of >=3 triggers shortcut; else count dots
      ↓
EXTRACT TARGET
minimum operations to fill dots
      ↓
WRITE FORMULA
local pattern
      ↓
TRANSFORM
case split
      ↓
ALGORITHM
scan
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`string`**.
2.  Ignore narrative names and rewrite the requirement using
    **`run of >=3 triggers shortcut; else count dots`**.
3.  Express the requirement mathematically as **`local pattern`**.
4.  Simplify it until the recognizable form **case split** appears.
5.  Implement **scan** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          string

Target:         minimum operations to fill dots

Variables:      run of >=3 triggers shortcut; else count dots

Formula:        local pattern

Transform:      case split

Algorithm:      scan

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    run of >=3 triggers shortcut; else count dots

DEFINE the target:
    minimum operations to fill dots

TRANSLATE the condition:
    local pattern

SIMPLIFY / TRANSFORM:
    case split

APPLY:
    scan

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`local pattern`** and the derived method **scan**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `local pattern`, test **case split** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 090 --- Problem 90

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1873B --- Good
Kid](https://codeforces.com/problemset/problem/1873/B)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`digits`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
digits

WHAT MUST BE FOUND:
increment one element to maximize product

MATHEMATICAL OBJECTS THAT MATTER:
increment smallest

CORE RELATION:
exchange argument intuition

FINAL MATHEMATICAL VIEW:
sort/min index
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **exchange argument intuition**, after which the useful form is
**sort/min index**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1873B --- Good Kid (Product/Greedy / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `exchange argument intuition`. Once the story is
    stripped away, recognize **sort/min index**, then implement
    **O(n)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
digits

QUESTION:
increment one element to maximize product
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
increment smallest
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
digits

DERIVED QUANTITIES / USEFUL STATE:
increment smallest

UNKNOWN / ANSWER:
increment one element to maximize product
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
exchange argument intuition
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
exchange argument intuition
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
exchange argument intuition

        ↓ simplify / rearrange / classify

sort/min index
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
increment smallest
      ↓
EXTRACT TARGET
increment one element to maximize product
      ↓
WRITE FORMULA
exchange argument intuition
      ↓
TRANSFORM
sort/min index
      ↓
ALGORITHM
O(n)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`digits`**.
2.  Ignore narrative names and rewrite the requirement using
    **`increment smallest`**.
3.  Express the requirement mathematically as
    **`exchange argument intuition`**.
4.  Simplify it until the recognizable form **sort/min index** appears.
5.  Implement **O(n)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          digits

Target:         increment one element to maximize product

Variables:      increment smallest

Formula:        exchange argument intuition

Transform:      sort/min index

Algorithm:      O(n)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    increment smallest

DEFINE the target:
    increment one element to maximize product

TRANSLATE the condition:
    exchange argument intuition

SIMPLIFY / TRANSFORM:
    sort/min index

APPLY:
    O(n)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`exchange argument intuition`** and the derived method **O(n)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `exchange argument intuition`, test **sort/min index** immediately.

# Pattern 10 --- Prefix / Running-State Modeling

## Pattern Overview

When the statement asks about a process over prefixes or accumulated
totals, define a running state or prefix quantity instead of recomputing
from scratch.

``` text
Different CF stories
       ↓ remove nouns
Same mathematical family
       ↓
Prefix / Running-State Modeling
```

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 091 --- Problem 91

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 116A ---
Tram](https://codeforces.com/problemset/problem/116/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`enter/exit`**. After removing the story/context,
the real task is:

``` text
INPUT / GIVEN:
enter/exit

WHAT MUST BE FOUND:
minimum capacity

MATHEMATICAL OBJECTS THAT MATTER:
cur += in-out

CORE RELATION:
max(cur)

FINAL MATHEMATICAL VIEW:
prefix occupancy
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **max(cur)**, after which the useful form is **prefix occupancy**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 116A --- Tram (Prefix / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `max(cur)`. Once the story is stripped away, recognize
    **prefix occupancy**, then implement **scan**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
enter/exit

QUESTION:
minimum capacity
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
cur += in-out
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
enter/exit

DERIVED QUANTITIES / USEFUL STATE:
cur += in-out

UNKNOWN / ANSWER:
minimum capacity
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
max(cur)
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
max(cur)
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
max(cur)

        ↓ simplify / rearrange / classify

prefix occupancy
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
cur += in-out
      ↓
EXTRACT TARGET
minimum capacity
      ↓
WRITE FORMULA
max(cur)
      ↓
TRANSFORM
prefix occupancy
      ↓
ALGORITHM
scan
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`enter/exit`**.
2.  Ignore narrative names and rewrite the requirement using
    **`cur += in-out`**.
3.  Express the requirement mathematically as **`max(cur)`**.
4.  Simplify it until the recognizable form **prefix occupancy**
    appears.
5.  Implement **scan** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          enter/exit

Target:         minimum capacity

Variables:      cur += in-out

Formula:        max(cur)

Transform:      prefix occupancy

Algorithm:      scan

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    cur += in-out

DEFINE the target:
    minimum capacity

TRANSLATE the condition:
    max(cur)

SIMPLIFY / TRANSFORM:
    prefix occupancy

APPLY:
    scan

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`max(cur)`** and the derived method **scan**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `max(cur)`, test **prefix occupancy** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 092 --- Problem 92

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 363B ---
Fence](https://codeforces.com/problemset/problem/363/B)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`array,k`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
array,k

WHAT MUST BE FOUND:
position of minimum k-length sum

MATHEMATICAL OBJECTS THAT MATTER:
window sum

CORE RELATION:
min over contiguous k

FINAL MATHEMATICAL VIEW:
prefix/sliding
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **min over contiguous k**, after which the useful form is
**prefix/sliding**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 363B --- Fence (Sliding Window / Codeforces / 1100)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `min over contiguous k`. Once the story is stripped
    away, recognize **prefix/sliding**, then implement **O(n)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
array,k

QUESTION:
position of minimum k-length sum
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
window sum
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
array,k

DERIVED QUANTITIES / USEFUL STATE:
window sum

UNKNOWN / ANSWER:
position of minimum k-length sum
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
min over contiguous k
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
min over contiguous k
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
min over contiguous k

        ↓ simplify / rearrange / classify

prefix/sliding
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
window sum
      ↓
EXTRACT TARGET
position of minimum k-length sum
      ↓
WRITE FORMULA
min over contiguous k
      ↓
TRANSFORM
prefix/sliding
      ↓
ALGORITHM
O(n)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`array,k`**.
2.  Ignore narrative names and rewrite the requirement using
    **`window sum`**.
3.  Express the requirement mathematically as
    **`min over contiguous k`**.
4.  Simplify it until the recognizable form **prefix/sliding** appears.
5.  Implement **O(n)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          array,k

Target:         position of minimum k-length sum

Variables:      window sum

Formula:        min over contiguous k

Transform:      prefix/sliding

Algorithm:      O(n)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    window sum

DEFINE the target:
    position of minimum k-length sum

TRANSLATE the condition:
    min over contiguous k

SIMPLIFY / TRANSFORM:
    prefix/sliding

APPLY:
    O(n)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`min over contiguous k`** and the derived method **O(n)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `min over contiguous k`, test **prefix/sliding** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 093 --- Problem 93

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 276C --- Little Girl and Problem on Trees / Little
Girl and Maximum Sum](https://codeforces.com/problemset/problem/276/C)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`array,queries`**. After removing the
story/context, the real task is:

``` text
INPUT / GIVEN:
array,queries

WHAT MUST BE FOUND:
maximize total query sum by permutation

MATHEMATICAL OBJECTS THAT MATTER:
frequency each index used

CORE RELATION:
sort values and frequencies same order

FINAL MATHEMATICAL VIEW:
rearrangement inequality
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **sort values and frequencies same order**, after which the useful
form is **rearrangement inequality**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 276C --- Little Girl and Problem on Trees / Little Girl and Maximum Sum (Difference/Contribution / Codeforces / 1400)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `sort values and frequencies same order`. Once the
    story is stripped away, recognize **rearrangement inequality**, then
    implement **diff array**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
array,queries

QUESTION:
maximize total query sum by permutation
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
frequency each index used
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
array,queries

DERIVED QUANTITIES / USEFUL STATE:
frequency each index used

UNKNOWN / ANSWER:
maximize total query sum by permutation
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
sort values and frequencies same order
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
sort values and frequencies same order
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
sort values and frequencies same order

        ↓ simplify / rearrange / classify

rearrangement inequality
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
frequency each index used
      ↓
EXTRACT TARGET
maximize total query sum by permutation
      ↓
WRITE FORMULA
sort values and frequencies same order
      ↓
TRANSFORM
rearrangement inequality
      ↓
ALGORITHM
diff array
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`array,queries`**.
2.  Ignore narrative names and rewrite the requirement using
    **`frequency each index used`**.
3.  Express the requirement mathematically as
    **`sort values and frequencies same order`**.
4.  Simplify it until the recognizable form **rearrangement inequality**
    appears.
5.  Implement **diff array** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          array,queries

Target:         maximize total query sum by permutation

Variables:      frequency each index used

Formula:        sort values and frequencies same order

Transform:      rearrangement inequality

Algorithm:      diff array

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    frequency each index used

DEFINE the target:
    maximize total query sum by permutation

TRANSLATE the condition:
    sort values and frequencies same order

SIMPLIFY / TRANSFORM:
    rearrangement inequality

APPLY:
    diff array

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`sort values and frequencies same order`** and the derived method
    **diff array**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `sort values and frequencies same order`, test **rearrangement
    inequality** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 094 --- Problem 94

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 433B --- Kuriyama Mirai's
Stones](https://codeforces.com/problemset/problem/433/B)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`array,queries`**. After removing the
story/context, the real task is:

``` text
INPUT / GIVEN:
array,queries

WHAT MUST BE FOUND:
range sums original/sorted

MATHEMATICAL OBJECTS THAT MATTER:
pref and sortedPref

CORE RELATION:
range=p[r]-p[l-1]

FINAL MATHEMATICAL VIEW:
static range query
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **range=p\[r\]-p\[l-1\]**, after which the useful form is **static
range query**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 433B --- Kuriyama Mirai's Stones (Prefix Sum / Codeforces / 1200)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `range=p[r]-p[l-1]`. Once the story is stripped away,
    recognize **static range query**, then implement **O(1)/query**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
array,queries

QUESTION:
range sums original/sorted
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
pref and sortedPref
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
array,queries

DERIVED QUANTITIES / USEFUL STATE:
pref and sortedPref

UNKNOWN / ANSWER:
range sums original/sorted
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
range=p[r]-p[l-1]
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
range=p[r]-p[l-1]
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
range=p[r]-p[l-1]

        ↓ simplify / rearrange / classify

static range query
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
pref and sortedPref
      ↓
EXTRACT TARGET
range sums original/sorted
      ↓
WRITE FORMULA
range=p[r]-p[l-1]
      ↓
TRANSFORM
static range query
      ↓
ALGORITHM
O(1)/query
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`array,queries`**.
2.  Ignore narrative names and rewrite the requirement using
    **`pref and sortedPref`**.
3.  Express the requirement mathematically as **`range=p[r]-p[l-1]`**.
4.  Simplify it until the recognizable form **static range query**
    appears.
5.  Implement **O(1)/query** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          array,queries

Target:         range sums original/sorted

Variables:      pref and sortedPref

Formula:        range=p[r]-p[l-1]

Transform:      static range query

Algorithm:      O(1)/query

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    pref and sortedPref

DEFINE the target:
    range sums original/sorted

TRANSLATE the condition:
    range=p[r]-p[l-1]

SIMPLIFY / TRANSFORM:
    static range query

APPLY:
    O(1)/query

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`range=p[r]-p[l-1]`** and the derived method **O(1)/query**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `range=p[r]-p[l-1]`, test **static range query** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 095 --- Problem 95

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 313B --- Ilya and
Queries](https://codeforces.com/problemset/problem/313/B)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`string,queries`**. After removing the
story/context, the real task is:

``` text
INPUT / GIVEN:
string,queries

WHAT MUST BE FOUND:
count equal adjacent pairs in range

MATHEMATICAL OBJECTS THAT MATTER:
b[i]=[s[i]==s[i-1]]

CORE RELATION:
prefix b

FINAL MATHEMATICAL VIEW:
range sum
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **prefix b**, after which the useful form is **range sum**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 313B --- Ilya and Queries (Prefix / Codeforces / 1100)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `prefix b`. Once the story is stripped away, recognize
    **range sum**, then implement **O(n+q)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
string,queries

QUESTION:
count equal adjacent pairs in range
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
b[i]=[s[i]==s[i-1]]
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
string,queries

DERIVED QUANTITIES / USEFUL STATE:
b[i]=[s[i]==s[i-1]]

UNKNOWN / ANSWER:
count equal adjacent pairs in range
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
prefix b
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
prefix b
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
prefix b

        ↓ simplify / rearrange / classify

range sum
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
b[i]=[s[i]==s[i-1]]
      ↓
EXTRACT TARGET
count equal adjacent pairs in range
      ↓
WRITE FORMULA
prefix b
      ↓
TRANSFORM
range sum
      ↓
ALGORITHM
O(n+q)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`string,queries`**.
2.  Ignore narrative names and rewrite the requirement using
    **`b[i]=[s[i]==s[i-1]]`**.
3.  Express the requirement mathematically as **`prefix b`**.
4.  Simplify it until the recognizable form **range sum** appears.
5.  Implement **O(n+q)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          string,queries

Target:         count equal adjacent pairs in range

Variables:      b[i]=[s[i]==s[i-1]]

Formula:        prefix b

Transform:      range sum

Algorithm:      O(n+q)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    b[i]=[s[i]==s[i-1]]

DEFINE the target:
    count equal adjacent pairs in range

TRANSLATE the condition:
    prefix b

SIMPLIFY / TRANSFORM:
    range sum

APPLY:
    O(n+q)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`prefix b`** and the derived method **O(n+q)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `prefix b`, test **range sum** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 096 --- Problem 96

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 327A --- Flipping
Game](https://codeforces.com/problemset/problem/327/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`binary array`**. After removing the
story/context, the real task is:

``` text
INPUT / GIVEN:
binary array

WHAT MUST BE FOUND:
maximize ones after one flip

MATHEMATICAL OBJECTS THAT MATTER:
gain: 0→+1,1→-1

CORE RELATION:
max subarray gain

FINAL MATHEMATICAL VIEW:
transform then Kadane
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **max subarray gain**, after which the useful form is **transform
then Kadane**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 327A --- Flipping Game (Transform/Kadane / Codeforces / 1200)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `max subarray gain`. Once the story is stripped away,
    recognize **transform then Kadane**, then implement **O(n)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
binary array

QUESTION:
maximize ones after one flip
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
gain: 0→+1,1→-1
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
binary array

DERIVED QUANTITIES / USEFUL STATE:
gain: 0→+1,1→-1

UNKNOWN / ANSWER:
maximize ones after one flip
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
max subarray gain
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
max subarray gain
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
max subarray gain

        ↓ simplify / rearrange / classify

transform then Kadane
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
gain: 0→+1,1→-1
      ↓
EXTRACT TARGET
maximize ones after one flip
      ↓
WRITE FORMULA
max subarray gain
      ↓
TRANSFORM
transform then Kadane
      ↓
ALGORITHM
O(n)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`binary array`**.
2.  Ignore narrative names and rewrite the requirement using
    **`gain: 0→+1,1→-1`**.
3.  Express the requirement mathematically as **`max subarray gain`**.
4.  Simplify it until the recognizable form **transform then Kadane**
    appears.
5.  Implement **O(n)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          binary array

Target:         maximize ones after one flip

Variables:      gain: 0→+1,1→-1

Formula:        max subarray gain

Transform:      transform then Kadane

Algorithm:      O(n)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    gain: 0→+1,1→-1

DEFINE the target:
    maximize ones after one flip

TRANSLATE the condition:
    max subarray gain

SIMPLIFY / TRANSFORM:
    transform then Kadane

APPLY:
    O(n)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`max subarray gain`** and the derived method **O(n)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `max subarray gain`, test **transform then Kadane** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 097 --- Problem 97

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 580A --- Kefa and First
Steps](https://codeforces.com/problemset/problem/580/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`array`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
array

WHAT MUST BE FOUND:
longest nondecreasing contiguous segment

MATHEMATICAL OBJECTS THAT MATTER:
current run based on a[i]>=a[i-1]

CORE RELATION:
max run

FINAL MATHEMATICAL VIEW:
state
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **max run**, after which the useful form is **state**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 580A --- Kefa and First Steps (Run Length / Codeforces / 900)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `max run`. Once the story is stripped away, recognize
    **state**, then implement **O(n)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
array

QUESTION:
longest nondecreasing contiguous segment
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
current run based on a[i]>=a[i-1]
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
array

DERIVED QUANTITIES / USEFUL STATE:
current run based on a[i]>=a[i-1]

UNKNOWN / ANSWER:
longest nondecreasing contiguous segment
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
max run
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
max run
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
max run

        ↓ simplify / rearrange / classify

state
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
current run based on a[i]>=a[i-1]
      ↓
EXTRACT TARGET
longest nondecreasing contiguous segment
      ↓
WRITE FORMULA
max run
      ↓
TRANSFORM
state
      ↓
ALGORITHM
O(n)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`array`**.
2.  Ignore narrative names and rewrite the requirement using
    **`current run based on a[i]>=a[i-1]`**.
3.  Express the requirement mathematically as **`max run`**.
4.  Simplify it until the recognizable form **state** appears.
5.  Implement **O(n)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          array

Target:         longest nondecreasing contiguous segment

Variables:      current run based on a[i]>=a[i-1]

Formula:        max run

Transform:      state

Algorithm:      O(n)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    current run based on a[i]>=a[i-1]

DEFINE the target:
    longest nondecreasing contiguous segment

TRANSLATE the condition:
    max run

SIMPLIFY / TRANSFORM:
    state

APPLY:
    O(n)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`max run`** and the derived method **O(n)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `max run`, test **state** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 098 --- Problem 98

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 702A --- Maximum
Increase](https://codeforces.com/problemset/problem/702/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`array`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
array

WHAT MUST BE FOUND:
longest strictly increasing contiguous segment

MATHEMATICAL OBJECTS THAT MATTER:
current++ if a[i]>a[i-1]

CORE RELATION:
max run

FINAL MATHEMATICAL VIEW:
state
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **max run**, after which the useful form is **state**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 702A --- Maximum Increase (Run Length / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `max run`. Once the story is stripped away, recognize
    **state**, then implement **O(n)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
array

QUESTION:
longest strictly increasing contiguous segment
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
current++ if a[i]>a[i-1]
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
array

DERIVED QUANTITIES / USEFUL STATE:
current++ if a[i]>a[i-1]

UNKNOWN / ANSWER:
longest strictly increasing contiguous segment
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
max run
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
max run
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
max run

        ↓ simplify / rearrange / classify

state
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
current++ if a[i]>a[i-1]
      ↓
EXTRACT TARGET
longest strictly increasing contiguous segment
      ↓
WRITE FORMULA
max run
      ↓
TRANSFORM
state
      ↓
ALGORITHM
O(n)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`array`**.
2.  Ignore narrative names and rewrite the requirement using
    **`current++ if a[i]>a[i-1]`**.
3.  Express the requirement mathematically as **`max run`**.
4.  Simplify it until the recognizable form **state** appears.
5.  Implement **O(n)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          array

Target:         longest strictly increasing contiguous segment

Variables:      current++ if a[i]>a[i-1]

Formula:        max run

Transform:      state

Algorithm:      O(n)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    current++ if a[i]>a[i-1]

DEFINE the target:
    longest strictly increasing contiguous segment

TRANSLATE the condition:
    max run

SIMPLIFY / TRANSFORM:
    state

APPLY:
    O(n)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`max run`** and the derived method **O(n)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `max run`, test **state** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 099 --- Problem 99

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1829B --- Blank
Space](https://codeforces.com/problemset/problem/1829/B)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`binary array`**. After removing the
story/context, the real task is:

``` text
INPUT / GIVEN:
binary array

WHAT MUST BE FOUND:
longest zeros

MATHEMATICAL OBJECTS THAT MATTER:
current zero run

CORE RELATION:
max

FINAL MATHEMATICAL VIEW:
state
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **max**, after which the useful form is **state**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1829B --- Blank Space (Run Length / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `max`. Once the story is stripped away, recognize
    **state**, then implement **O(n)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
binary array

QUESTION:
longest zeros
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
current zero run
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
binary array

DERIVED QUANTITIES / USEFUL STATE:
current zero run

UNKNOWN / ANSWER:
longest zeros
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
max
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
max
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
max

        ↓ simplify / rearrange / classify

state
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
current zero run
      ↓
EXTRACT TARGET
longest zeros
      ↓
WRITE FORMULA
max
      ↓
TRANSFORM
state
      ↓
ALGORITHM
O(n)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`binary array`**.
2.  Ignore narrative names and rewrite the requirement using
    **`current zero run`**.
3.  Express the requirement mathematically as **`max`**.
4.  Simplify it until the recognizable form **state** appears.
5.  Implement **O(n)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          binary array

Target:         longest zeros

Variables:      current zero run

Formula:        max

Transform:      state

Algorithm:      O(n)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    current zero run

DEFINE the target:
    longest zeros

TRANSLATE the condition:
    max

SIMPLIFY / TRANSFORM:
    state

APPLY:
    O(n)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`max`** and the derived method **O(n)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `max`, test **state** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 100 --- Problem 100

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1669F --- Eating
Candies](https://codeforces.com/problemset/problem/1669/F)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`array`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
array

WHAT MUST BE FOUND:
max elements eaten with equal left/right sums

MATHEMATICAL OBJECTS THAT MATTER:
grow smaller side sum

CORE RELATION:
two monotone prefix sums

FINAL MATHEMATICAL VIEW:
two pointers
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **two monotone prefix sums**, after which the useful form is **two
pointers**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1669F --- Eating Candies (Two Pointers/Prefix / Codeforces / 1100)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `two monotone prefix sums`. Once the story is stripped
    away, recognize **two pointers**, then implement **O(n)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
array

QUESTION:
max elements eaten with equal left/right sums
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
grow smaller side sum
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
array

DERIVED QUANTITIES / USEFUL STATE:
grow smaller side sum

UNKNOWN / ANSWER:
max elements eaten with equal left/right sums
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
two monotone prefix sums
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
two monotone prefix sums
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
two monotone prefix sums

        ↓ simplify / rearrange / classify

two pointers
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
grow smaller side sum
      ↓
EXTRACT TARGET
max elements eaten with equal left/right sums
      ↓
WRITE FORMULA
two monotone prefix sums
      ↓
TRANSFORM
two pointers
      ↓
ALGORITHM
O(n)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`array`**.
2.  Ignore narrative names and rewrite the requirement using
    **`grow smaller side sum`**.
3.  Express the requirement mathematically as
    **`two monotone prefix sums`**.
4.  Simplify it until the recognizable form **two pointers** appears.
5.  Implement **O(n)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          array

Target:         max elements eaten with equal left/right sums

Variables:      grow smaller side sum

Formula:        two monotone prefix sums

Transform:      two pointers

Algorithm:      O(n)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    grow smaller side sum

DEFINE the target:
    max elements eaten with equal left/right sums

TRANSLATE the condition:
    two monotone prefix sums

SIMPLIFY / TRANSFORM:
    two pointers

APPLY:
    O(n)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`two monotone prefix sums`** and the derived method **O(n)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `two monotone prefix sums`, test **two pointers** immediately.

# Pattern 11 --- Constructive / Reachability Modeling

## Pattern Overview

For 'print any valid answer' problems, turn requirements into
equations/constraints and build the simplest object satisfying them.
Separate necessity from sufficiency.

``` text
Different CF stories
       ↓ remove nouns
Same mathematical family
       ↓
Constructive / Reachability Modeling
```

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 101 --- Problem 101

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1690A --- Print a
Pedestal](https://codeforces.com/problemset/problem/1690/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`n`**. After removing the story/context, the real
task is:

``` text
INPUT / GIVEN:
n

WHAT MUST BE FOUND:
three distinct positive heights with ordering

MATHEMATICAL OBJECTS THAT MATTER:
a+b+c=n, a<b<c

CORE RELATION:
near thirds

FINAL MATHEMATICAL VIEW:
construct
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **near thirds**, after which the useful form is **construct**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1690A --- Print a Pedestal (Constructive / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `near thirds`. Once the story is stripped away,
    recognize **construct**, then implement **cases**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
n

QUESTION:
three distinct positive heights with ordering
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
a+b+c=n, a<b<c
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
n

DERIVED QUANTITIES / USEFUL STATE:
a+b+c=n, a<b<c

UNKNOWN / ANSWER:
three distinct positive heights with ordering
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
near thirds
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
near thirds
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
near thirds

        ↓ simplify / rearrange / classify

construct
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
a+b+c=n, a<b<c
      ↓
EXTRACT TARGET
three distinct positive heights with ordering
      ↓
WRITE FORMULA
near thirds
      ↓
TRANSFORM
construct
      ↓
ALGORITHM
cases
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`n`**.
2.  Ignore narrative names and rewrite the requirement using
    **`a+b+c=n, a<b<c`**.
3.  Express the requirement mathematically as **`near thirds`**.
4.  Simplify it until the recognizable form **construct** appears.
5.  Implement **cases** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          n

Target:         three distinct positive heights with ordering

Variables:      a+b+c=n, a<b<c

Formula:        near thirds

Transform:      construct

Algorithm:      cases

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    a+b+c=n, a<b<c

DEFINE the target:
    three distinct positive heights with ordering

TRANSLATE the condition:
    near thirds

SIMPLIFY / TRANSFORM:
    construct

APPLY:
    cases

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`near thirds`** and the derived method **cases**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `near thirds`, test **construct** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 102 --- Problem 102

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1845A --- Forbidden
Integer](https://codeforces.com/problemset/problem/1845/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`n,k,x`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
n,k,x

WHAT MUST BE FOUND:
sum allowed integers to n

MATHEMATICAL OBJECTS THAT MATTER:
choose 1 if allowed else 2/3

CORE RELATION:
simple basis values

FINAL MATHEMATICAL VIEW:
construct
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **simple basis values**, after which the useful form is
**construct**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1845A --- Forbidden Integer (Constructive / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `simple basis values`. Once the story is stripped away,
    recognize **construct**, then implement **cases**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
n,k,x

QUESTION:
sum allowed integers to n
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
choose 1 if allowed else 2/3
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
n,k,x

DERIVED QUANTITIES / USEFUL STATE:
choose 1 if allowed else 2/3

UNKNOWN / ANSWER:
sum allowed integers to n
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
simple basis values
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
simple basis values
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
simple basis values

        ↓ simplify / rearrange / classify

construct
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
choose 1 if allowed else 2/3
      ↓
EXTRACT TARGET
sum allowed integers to n
      ↓
WRITE FORMULA
simple basis values
      ↓
TRANSFORM
construct
      ↓
ALGORITHM
cases
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`n,k,x`**.
2.  Ignore narrative names and rewrite the requirement using
    **`choose 1 if allowed else 2/3`**.
3.  Express the requirement mathematically as **`simple basis values`**.
4.  Simplify it until the recognizable form **construct** appears.
5.  Implement **cases** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          n,k,x

Target:         sum allowed integers to n

Variables:      choose 1 if allowed else 2/3

Formula:        simple basis values

Transform:      construct

Algorithm:      cases

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    choose 1 if allowed else 2/3

DEFINE the target:
    sum allowed integers to n

TRANSLATE the condition:
    simple basis values

SIMPLIFY / TRANSFORM:
    construct

APPLY:
    cases

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`simple basis values`** and the derived method **cases**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `simple basis values`, test **construct** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 103 --- Problem 103

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1878B --- Aleksa and
Stack](https://codeforces.com/problemset/problem/1878/B)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`n`**. After removing the story/context, the real
task is:

``` text
INPUT / GIVEN:
n

WHAT MUST BE FOUND:
build valid sequence

MATHEMATICAL OBJECTS THAT MATTER:
choose simple constant pattern

CORE RELATION:
satisfy local constraint by design

FINAL MATHEMATICAL VIEW:
construction
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **satisfy local constraint by design**, after which the useful form
is **construction**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1878B --- Aleksa and Stack (Constructive / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `satisfy local constraint by design`. Once the story is
    stripped away, recognize **construction**, then implement
    **formula**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
n

QUESTION:
build valid sequence
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
choose simple constant pattern
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
n

DERIVED QUANTITIES / USEFUL STATE:
choose simple constant pattern

UNKNOWN / ANSWER:
build valid sequence
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
satisfy local constraint by design
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
satisfy local constraint by design
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
satisfy local constraint by design

        ↓ simplify / rearrange / classify

construction
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
choose simple constant pattern
      ↓
EXTRACT TARGET
build valid sequence
      ↓
WRITE FORMULA
satisfy local constraint by design
      ↓
TRANSFORM
construction
      ↓
ALGORITHM
formula
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`n`**.
2.  Ignore narrative names and rewrite the requirement using
    **`choose simple constant pattern`**.
3.  Express the requirement mathematically as
    **`satisfy local constraint by design`**.
4.  Simplify it until the recognizable form **construction** appears.
5.  Implement **formula** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          n

Target:         build valid sequence

Variables:      choose simple constant pattern

Formula:        satisfy local constraint by design

Transform:      construction

Algorithm:      formula

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    choose simple constant pattern

DEFINE the target:
    build valid sequence

TRANSLATE the condition:
    satisfy local constraint by design

SIMPLIFY / TRANSFORM:
    construction

APPLY:
    formula

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`satisfy local constraint by design`** and the derived method
    **formula**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `satisfy local constraint by design`, test **construction**
    immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 104 --- Problem 104

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1741A --- Compare T-Shirt
Sizes](https://codeforces.com/problemset/problem/1741/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`size strings`**. After removing the
story/context, the real task is:

``` text
INPUT / GIVEN:
size strings

WHAT MUST BE FOUND:
compare S/M/L with X count

MATHEMATICAL OBJECTS THAT MATTER:
L: more X larger; S reverse

CORE RELATION:
map to signed scale

FINAL MATHEMATICAL VIEW:
custom ordering
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **map to signed scale**, after which the useful form is **custom
ordering**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1741A --- Compare T-Shirt Sizes (Ordering / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `map to signed scale`. Once the story is stripped away,
    recognize **custom ordering**, then implement **O(len)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
size strings

QUESTION:
compare S/M/L with X count
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
L: more X larger; S reverse
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
size strings

DERIVED QUANTITIES / USEFUL STATE:
L: more X larger; S reverse

UNKNOWN / ANSWER:
compare S/M/L with X count
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
map to signed scale
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
map to signed scale
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
map to signed scale

        ↓ simplify / rearrange / classify

custom ordering
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
L: more X larger; S reverse
      ↓
EXTRACT TARGET
compare S/M/L with X count
      ↓
WRITE FORMULA
map to signed scale
      ↓
TRANSFORM
custom ordering
      ↓
ALGORITHM
O(len)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`size strings`**.
2.  Ignore narrative names and rewrite the requirement using
    **`L: more X larger; S reverse`**.
3.  Express the requirement mathematically as **`map to signed scale`**.
4.  Simplify it until the recognizable form **custom ordering** appears.
5.  Implement **O(len)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          size strings

Target:         compare S/M/L with X count

Variables:      L: more X larger; S reverse

Formula:        map to signed scale

Transform:      custom ordering

Algorithm:      O(len)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    L: more X larger; S reverse

DEFINE the target:
    compare S/M/L with X count

TRANSLATE the condition:
    map to signed scale

SIMPLIFY / TRANSFORM:
    custom ordering

APPLY:
    O(len)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`map to signed scale`** and the derived method **O(len)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `map to signed scale`, test **custom ordering** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 105 --- Problem 105

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1805B --- We Need the Zero / The String Has a
Target](https://codeforces.com/problemset/problem/1805/B)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`string`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
string

WHAT MUST BE FOUND:
move smallest char to front under operation

MATHEMATICAL OBJECTS THAT MATTER:
global min char; choose rightmost occurrence

CORE RELATION:
stable reconstruction

FINAL MATHEMATICAL VIEW:
greedy
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **stable reconstruction**, after which the useful form is **greedy**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1805B --- We Need the Zero / The String Has a Target (String/Greedy / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `stable reconstruction`. Once the story is stripped
    away, recognize **greedy**, then implement **O(n)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
string

QUESTION:
move smallest char to front under operation
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
global min char; choose rightmost occurrence
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
string

DERIVED QUANTITIES / USEFUL STATE:
global min char; choose rightmost occurrence

UNKNOWN / ANSWER:
move smallest char to front under operation
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
stable reconstruction
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
stable reconstruction
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
stable reconstruction

        ↓ simplify / rearrange / classify

greedy
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
global min char; choose rightmost occurrence
      ↓
EXTRACT TARGET
move smallest char to front under operation
      ↓
WRITE FORMULA
stable reconstruction
      ↓
TRANSFORM
greedy
      ↓
ALGORITHM
O(n)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`string`**.
2.  Ignore narrative names and rewrite the requirement using
    **`global min char; choose rightmost occurrence`**.
3.  Express the requirement mathematically as
    **`stable reconstruction`**.
4.  Simplify it until the recognizable form **greedy** appears.
5.  Implement **O(n)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          string

Target:         move smallest char to front under operation

Variables:      global min char; choose rightmost occurrence

Formula:        stable reconstruction

Transform:      greedy

Algorithm:      O(n)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    global min char; choose rightmost occurrence

DEFINE the target:
    move smallest char to front under operation

TRANSLATE the condition:
    stable reconstruction

SIMPLIFY / TRANSFORM:
    greedy

APPLY:
    O(n)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`stable reconstruction`** and the derived method **O(n)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `stable reconstruction`, test **greedy** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 106 --- Problem 106

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1833B --- Restore the
Weather](https://codeforces.com/problemset/problem/1833/B)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`arrays a,b,k`**. After removing the
story/context, the real task is:

``` text
INPUT / GIVEN:
arrays a,b,k

WHAT MUST BE FOUND:
permute b so |a[i]-b[i]|<=k

MATHEMATICAL OBJECTS THAT MATTER:
sort indices by a and b

CORE RELATION:
monotone matching

FINAL MATHEMATICAL VIEW:
pair sorted orders
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **monotone matching**, after which the useful form is **pair sorted
orders**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1833B --- Restore the Weather (Sorting/Matching / Codeforces / 1000)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `monotone matching`. Once the story is stripped away,
    recognize **pair sorted orders**, then implement **O(nlogn)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
arrays a,b,k

QUESTION:
permute b so |a[i]-b[i]|<=k
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
sort indices by a and b
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
arrays a,b,k

DERIVED QUANTITIES / USEFUL STATE:
sort indices by a and b

UNKNOWN / ANSWER:
permute b so |a[i]-b[i]|<=k
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
monotone matching
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
monotone matching
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
monotone matching

        ↓ simplify / rearrange / classify

pair sorted orders
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
sort indices by a and b
      ↓
EXTRACT TARGET
permute b so |a[i]-b[i]|<=k
      ↓
WRITE FORMULA
monotone matching
      ↓
TRANSFORM
pair sorted orders
      ↓
ALGORITHM
O(nlogn)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`arrays a,b,k`**.
2.  Ignore narrative names and rewrite the requirement using
    **`sort indices by a and b`**.
3.  Express the requirement mathematically as **`monotone matching`**.
4.  Simplify it until the recognizable form **pair sorted orders**
    appears.
5.  Implement **O(nlogn)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          arrays a,b,k

Target:         permute b so |a[i]-b[i]|<=k

Variables:      sort indices by a and b

Formula:        monotone matching

Transform:      pair sorted orders

Algorithm:      O(nlogn)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    sort indices by a and b

DEFINE the target:
    permute b so |a[i]-b[i]|<=k

TRANSLATE the condition:
    monotone matching

SIMPLIFY / TRANSFORM:
    pair sorted orders

APPLY:
    O(nlogn)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`monotone matching`** and the derived method **O(nlogn)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `monotone matching`, test **pair sorted orders** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 107 --- Problem 107

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1793C --- Dora and
Search](https://codeforces.com/problemset/problem/1793/C)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`permutation segment`**. After removing the
story/context, the real task is:

``` text
INPUT / GIVEN:
permutation segment

WHAT MUST BE FOUND:
find segment whose ends are neither min nor max

MATHEMATICAL OBJECTS THAT MATTER:
peel if endpoint is current min/max

CORE RELATION:
maintain lo,hi

FINAL MATHEMATICAL VIEW:
two pointers
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **maintain lo,hi**, after which the useful form is **two pointers**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1793C --- Dora and Search (Two Pointers/Extremes / Codeforces / 1200)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `maintain lo,hi`. Once the story is stripped away,
    recognize **two pointers**, then implement **O(n)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
permutation segment

QUESTION:
find segment whose ends are neither min nor max
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
peel if endpoint is current min/max
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
permutation segment

DERIVED QUANTITIES / USEFUL STATE:
peel if endpoint is current min/max

UNKNOWN / ANSWER:
find segment whose ends are neither min nor max
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
maintain lo,hi
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
maintain lo,hi
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
maintain lo,hi

        ↓ simplify / rearrange / classify

two pointers
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
peel if endpoint is current min/max
      ↓
EXTRACT TARGET
find segment whose ends are neither min nor max
      ↓
WRITE FORMULA
maintain lo,hi
      ↓
TRANSFORM
two pointers
      ↓
ALGORITHM
O(n)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`permutation segment`**.
2.  Ignore narrative names and rewrite the requirement using
    **`peel if endpoint is current min/max`**.
3.  Express the requirement mathematically as **`maintain lo,hi`**.
4.  Simplify it until the recognizable form **two pointers** appears.
5.  Implement **O(n)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          permutation segment

Target:         find segment whose ends are neither min nor max

Variables:      peel if endpoint is current min/max

Formula:        maintain lo,hi

Transform:      two pointers

Algorithm:      O(n)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    peel if endpoint is current min/max

DEFINE the target:
    find segment whose ends are neither min nor max

TRANSLATE the condition:
    maintain lo,hi

SIMPLIFY / TRANSFORM:
    two pointers

APPLY:
    O(n)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`maintain lo,hi`** and the derived method **O(n)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `maintain lo,hi`, test **two pointers** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 108 --- Problem 108

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1881A --- Don't Try to
Count](https://codeforces.com/problemset/problem/1881/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`x,s`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
x,s

WHAT MUST BE FOUND:
minimum doublings until s substring

MATHEMATICAL OBJECTS THAT MATTER:
length only needs bounded doublings

CORE RELATION:
repeat x until long enough + margin

FINAL MATHEMATICAL VIEW:
simulation bound
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **repeat x until long enough + margin**, after which the useful form
is **simulation bound**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1881A --- Don't Try to Count (String/Doubling / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `repeat x until long enough + margin`. Once the story
    is stripped away, recognize **simulation bound**, then implement
    **few iterations**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
x,s

QUESTION:
minimum doublings until s substring
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
length only needs bounded doublings
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
x,s

DERIVED QUANTITIES / USEFUL STATE:
length only needs bounded doublings

UNKNOWN / ANSWER:
minimum doublings until s substring
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
repeat x until long enough + margin
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
repeat x until long enough + margin
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
repeat x until long enough + margin

        ↓ simplify / rearrange / classify

simulation bound
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
length only needs bounded doublings
      ↓
EXTRACT TARGET
minimum doublings until s substring
      ↓
WRITE FORMULA
repeat x until long enough + margin
      ↓
TRANSFORM
simulation bound
      ↓
ALGORITHM
few iterations
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`x,s`**.
2.  Ignore narrative names and rewrite the requirement using
    **`length only needs bounded doublings`**.
3.  Express the requirement mathematically as
    **`repeat x until long enough + margin`**.
4.  Simplify it until the recognizable form **simulation bound**
    appears.
5.  Implement **few iterations** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          x,s

Target:         minimum doublings until s substring

Variables:      length only needs bounded doublings

Formula:        repeat x until long enough + margin

Transform:      simulation bound

Algorithm:      few iterations

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    length only needs bounded doublings

DEFINE the target:
    minimum doublings until s substring

TRANSLATE the condition:
    repeat x until long enough + margin

SIMPLIFY / TRANSFORM:
    simulation bound

APPLY:
    few iterations

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`repeat x until long enough + margin`** and the derived method
    **few iterations**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `repeat x until long enough + margin`, test **simulation bound**
    immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 109 --- Problem 109

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1858A ---
Buttons](https://codeforces.com/problemset/problem/1858/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`a,b,c`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
a,b,c

WHAT MUST BE FOUND:
winner

MATHEMATICAL OBJECTS THAT MATTER:
shared c allocated alternately

CORE RELATION:
parity c

FINAL MATHEMATICAL VIEW:
effective counts
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **parity c**, after which the useful form is **effective counts**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1858A --- Buttons (Game/Constructive / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `parity c`. Once the story is stripped away, recognize
    **effective counts**, then implement **casework**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
a,b,c

QUESTION:
winner
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
shared c allocated alternately
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
a,b,c

DERIVED QUANTITIES / USEFUL STATE:
shared c allocated alternately

UNKNOWN / ANSWER:
winner
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
parity c
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
parity c
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
parity c

        ↓ simplify / rearrange / classify

effective counts
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
shared c allocated alternately
      ↓
EXTRACT TARGET
winner
      ↓
WRITE FORMULA
parity c
      ↓
TRANSFORM
effective counts
      ↓
ALGORITHM
casework
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`a,b,c`**.
2.  Ignore narrative names and rewrite the requirement using
    **`shared c allocated alternately`**.
3.  Express the requirement mathematically as **`parity c`**.
4.  Simplify it until the recognizable form **effective counts**
    appears.
5.  Implement **casework** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          a,b,c

Target:         winner

Variables:      shared c allocated alternately

Formula:        parity c

Transform:      effective counts

Algorithm:      casework

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    shared c allocated alternately

DEFINE the target:
    winner

TRANSLATE the condition:
    parity c

SIMPLIFY / TRANSFORM:
    effective counts

APPLY:
    casework

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`parity c`** and the derived method **casework**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `parity c`, test **effective counts** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 110 --- Problem 110

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1899A --- Game with
Integers](https://codeforces.com/problemset/problem/1899/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`n`**. After removing the story/context, the real
task is:

``` text
INPUT / GIVEN:
n

WHAT MUST BE FOUND:
winner

MATHEMATICAL OBJECTS THAT MATTER:
moves ±1; multiples of3 structure

CORE RELATION:
n%3

FINAL MATHEMATICAL VIEW:
residue game
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **n%3**, after which the useful form is **residue game**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1899A --- Game with Integers (Modulo/Game / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `n%3`. Once the story is stripped away, recognize
    **residue game**, then implement **O(1)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
n

QUESTION:
winner
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
moves ±1; multiples of3 structure
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
n

DERIVED QUANTITIES / USEFUL STATE:
moves ±1; multiples of3 structure

UNKNOWN / ANSWER:
winner
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
n%3
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
n%3
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
n%3

        ↓ simplify / rearrange / classify

residue game
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
moves ±1; multiples of3 structure
      ↓
EXTRACT TARGET
winner
      ↓
WRITE FORMULA
n%3
      ↓
TRANSFORM
residue game
      ↓
ALGORITHM
O(1)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`n`**.
2.  Ignore narrative names and rewrite the requirement using
    **`moves ±1; multiples of3 structure`**.
3.  Express the requirement mathematically as **`n%3`**.
4.  Simplify it until the recognizable form **residue game** appears.
5.  Implement **O(1)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          n

Target:         winner

Variables:      moves ±1; multiples of3 structure

Formula:        n%3

Transform:      residue game

Algorithm:      O(1)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    moves ±1; multiples of3 structure

DEFINE the target:
    winner

TRANSLATE the condition:
    n%3

SIMPLIFY / TRANSFORM:
    residue game

APPLY:
    O(1)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`n%3`** and the derived method **O(1)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `n%3`, test **residue game** immediately.

# Pattern 12 --- Bitwise / XOR Modeling

## Pattern Overview

Translate XOR statements bitwise. Use cancellation `x^x=0`, `x^0=x`, and
remember that XOR is associative/commutative.

``` text
Different CF stories
       ↓ remove nouns
Same mathematical family
       ↓
Bitwise / XOR Modeling
```

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 111 --- Problem 111

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1805A --- We Need the
Zero](https://codeforces.com/problemset/problem/1805/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`array`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
array

WHAT MUST BE FOUND:
find x so xor(a[i]^x)=0

MATHEMATICAL OBJECTS THAT MATTER:
xorAll ^ (x repeated n times)

CORE RELATION:
if n even x cancels; else x=xorAll

FINAL MATHEMATICAL VIEW:
parity of n
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **if n even x cancels; else x=xorAll**, after which the useful form
is **parity of n**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1805A --- We Need the Zero (XOR / Codeforces / 900)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `if n even x cancels; else x=xorAll`. Once the story is
    stripped away, recognize **parity of n**, then implement **xor**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
array

QUESTION:
find x so xor(a[i]^x)=0
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
xorAll ^ (x repeated n times)
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
array

DERIVED QUANTITIES / USEFUL STATE:
xorAll ^ (x repeated n times)

UNKNOWN / ANSWER:
find x so xor(a[i]^x)=0
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
if n even x cancels; else x=xorAll
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
if n even x cancels; else x=xorAll
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
if n even x cancels; else x=xorAll

        ↓ simplify / rearrange / classify

parity of n
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
xorAll ^ (x repeated n times)
      ↓
EXTRACT TARGET
find x so xor(a[i]^x)=0
      ↓
WRITE FORMULA
if n even x cancels; else x=xorAll
      ↓
TRANSFORM
parity of n
      ↓
ALGORITHM
xor
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`array`**.
2.  Ignore narrative names and rewrite the requirement using
    **`xorAll ^ (x repeated n times)`**.
3.  Express the requirement mathematically as
    **`if n even x cancels; else x=xorAll`**.
4.  Simplify it until the recognizable form **parity of n** appears.
5.  Implement **xor** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          array

Target:         find x so xor(a[i]^x)=0

Variables:      xorAll ^ (x repeated n times)

Formula:        if n even x cancels; else x=xorAll

Transform:      parity of n

Algorithm:      xor

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    xorAll ^ (x repeated n times)

DEFINE the target:
    find x so xor(a[i]^x)=0

TRANSLATE the condition:
    if n even x cancels; else x=xorAll

SIMPLIFY / TRANSFORM:
    parity of n

APPLY:
    xor

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`if n even x cancels; else x=xorAll`** and the derived method
    **xor**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `if n even x cancels; else x=xorAll`, test **parity of n**
    immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 112 --- Problem 112

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1872A --- Two
Vessels](https://codeforces.com/problemset/problem/1872/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`a,b,c`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
a,b,c

WHAT MUST BE FOUND:
min moves balancing transfer c

MATHEMATICAL OBJECTS THAT MATTER:
difference shrinks by 2c

CORE RELATION:
m*2c>=|a-b|

FINAL MATHEMATICAL VIEW:
ceil division
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to \*\*m\*2c\>=\|a-b\|**, after which the useful form is **ceil
division\*\*.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1872A --- Two Vessels (Arithmetic / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `m*2c>=|a-b|`. Once the story is stripped away,
    recognize **ceil division**, then implement **formula**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
a,b,c

QUESTION:
min moves balancing transfer c
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
difference shrinks by 2c
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
a,b,c

DERIVED QUANTITIES / USEFUL STATE:
difference shrinks by 2c

UNKNOWN / ANSWER:
min moves balancing transfer c
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
m*2c>=|a-b|
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
m*2c>=|a-b|
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
m*2c>=|a-b|

        ↓ simplify / rearrange / classify

ceil division
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
difference shrinks by 2c
      ↓
EXTRACT TARGET
min moves balancing transfer c
      ↓
WRITE FORMULA
m*2c>=|a-b|
      ↓
TRANSFORM
ceil division
      ↓
ALGORITHM
formula
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`a,b,c`**.
2.  Ignore narrative names and rewrite the requirement using
    **`difference shrinks by 2c`**.
3.  Express the requirement mathematically as **`m*2c>=|a-b|`**.
4.  Simplify it until the recognizable form **ceil division** appears.
5.  Implement **formula** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          a,b,c

Target:         min moves balancing transfer c

Variables:      difference shrinks by 2c

Formula:        m*2c>=|a-b|

Transform:      ceil division

Algorithm:      formula

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    difference shrinks by 2c

DEFINE the target:
    min moves balancing transfer c

TRANSLATE the condition:
    m*2c>=|a-b|

SIMPLIFY / TRANSFORM:
    ceil division

APPLY:
    formula

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`m*2c>=|a-b|`** and the derived method **formula**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `m*2c>=|a-b|`, test **ceil division** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 113 --- Problem 113

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1703A --- YES or
YES?](https://codeforces.com/problemset/problem/1703/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`word`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
word

WHAT MUST BE FOUND:
case-insensitive equality to yes

MATHEMATICAL OBJECTS THAT MATTER:
normalize case

CORE RELATION:
compare

FINAL MATHEMATICAL VIEW:
canonicalization
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **compare**, after which the useful form is **canonicalization**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1703A --- YES or YES? (String / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `compare`. Once the story is stripped away, recognize
    **canonicalization**, then implement **tolower**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
word

QUESTION:
case-insensitive equality to yes
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
normalize case
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
word

DERIVED QUANTITIES / USEFUL STATE:
normalize case

UNKNOWN / ANSWER:
case-insensitive equality to yes
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
compare
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
compare
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
compare

        ↓ simplify / rearrange / classify

canonicalization
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
normalize case
      ↓
EXTRACT TARGET
case-insensitive equality to yes
      ↓
WRITE FORMULA
compare
      ↓
TRANSFORM
canonicalization
      ↓
ALGORITHM
tolower
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`word`**.
2.  Ignore narrative names and rewrite the requirement using
    **`normalize case`**.
3.  Express the requirement mathematically as **`compare`**.
4.  Simplify it until the recognizable form **canonicalization**
    appears.
5.  Implement **tolower** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          word

Target:         case-insensitive equality to yes

Variables:      normalize case

Formula:        compare

Transform:      canonicalization

Algorithm:      tolower

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    normalize case

DEFINE the target:
    case-insensitive equality to yes

TRANSLATE the condition:
    compare

SIMPLIFY / TRANSFORM:
    canonicalization

APPLY:
    tolower

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`compare`** and the derived method **tolower**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `compare`, test **canonicalization** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 114 --- Problem 114

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1624A --- Plus One on the
Subset](https://codeforces.com/problemset/problem/1624/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`array`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
array

WHAT MUST BE FOUND:
min ops equalize

MATHEMATICAL OBJECTS THAT MATTER:
one op can increment chosen subset

CORE RELATION:
range max-min

FINAL MATHEMATICAL VIEW:
potential
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **range max-min**, after which the useful form is **potential**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1624A --- Plus One on the Subset (Range / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `range max-min`. Once the story is stripped away,
    recognize **potential**, then implement **min/max**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
array

QUESTION:
min ops equalize
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
one op can increment chosen subset
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
array

DERIVED QUANTITIES / USEFUL STATE:
one op can increment chosen subset

UNKNOWN / ANSWER:
min ops equalize
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
range max-min
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
range max-min
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
range max-min

        ↓ simplify / rearrange / classify

potential
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
one op can increment chosen subset
      ↓
EXTRACT TARGET
min ops equalize
      ↓
WRITE FORMULA
range max-min
      ↓
TRANSFORM
potential
      ↓
ALGORITHM
min/max
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`array`**.
2.  Ignore narrative names and rewrite the requirement using
    **`one op can increment chosen subset`**.
3.  Express the requirement mathematically as **`range max-min`**.
4.  Simplify it until the recognizable form **potential** appears.
5.  Implement **min/max** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          array

Target:         min ops equalize

Variables:      one op can increment chosen subset

Formula:        range max-min

Transform:      potential

Algorithm:      min/max

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    one op can increment chosen subset

DEFINE the target:
    min ops equalize

TRANSLATE the condition:
    range max-min

SIMPLIFY / TRANSFORM:
    potential

APPLY:
    min/max

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`range max-min`** and the derived method **min/max**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `range max-min`, test **potential** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 115 --- Problem 115

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1220A ---
Cards](https://codeforces.com/problemset/problem/1220/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`letters`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
letters

WHAT MUST BE FOUND:
recover binary digits from letters

MATHEMATICAL OBJECTS THAT MATTER:
'z' uniquely identifies zero, 'n' one after ordering

CORE RELATION:
count z and n

FINAL MATHEMATICAL VIEW:
frequency signature
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **count z and n**, after which the useful form is **frequency
signature**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1220A --- Cards (Frequency / Codeforces / 900)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `count z and n`. Once the story is stripped away,
    recognize **frequency signature**, then implement **output**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
letters

QUESTION:
recover binary digits from letters
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
'z' uniquely identifies zero, 'n' one after ordering
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
letters

DERIVED QUANTITIES / USEFUL STATE:
'z' uniquely identifies zero, 'n' one after ordering

UNKNOWN / ANSWER:
recover binary digits from letters
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
count z and n
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
count z and n
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
count z and n

        ↓ simplify / rearrange / classify

frequency signature
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
'z' uniquely identifies zero, 'n' one after ordering
      ↓
EXTRACT TARGET
recover binary digits from letters
      ↓
WRITE FORMULA
count z and n
      ↓
TRANSFORM
frequency signature
      ↓
ALGORITHM
output
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`letters`**.
2.  Ignore narrative names and rewrite the requirement using
    **`'z' uniquely identifies zero, 'n' one after ordering`**.
3.  Express the requirement mathematically as **`count z and n`**.
4.  Simplify it until the recognizable form **frequency signature**
    appears.
5.  Implement **output** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          letters

Target:         recover binary digits from letters

Variables:      'z' uniquely identifies zero, 'n' one after ordering

Formula:        count z and n

Transform:      frequency signature

Algorithm:      output

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    'z' uniquely identifies zero, 'n' one after ordering

DEFINE the target:
    recover binary digits from letters

TRANSLATE the condition:
    count z and n

SIMPLIFY / TRANSFORM:
    frequency signature

APPLY:
    output

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`count z and n`** and the derived method **output**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `count z and n`, test **frequency signature** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 116 --- Problem 116

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1362A --- Johnny and Ancient
Computer](https://codeforces.com/problemset/problem/1362/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`a,b`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
a,b

WHAT MUST BE FOUND:
min ×2/4/8 operations to transform

MATHEMATICAL OBJECTS THAT MATTER:
ratio must be power of2

CORE RELATION:
exponent difference grouped by3

FINAL MATHEMATICAL VIEW:
factorization
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **exponent difference grouped by3**, after which the useful form is
**factorization**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1362A --- Johnny and Ancient Computer (Powers/Ratio / Codeforces / 900)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `exponent difference grouped by3`. Once the story is
    stripped away, recognize **factorization**, then implement
    **formula**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
a,b

QUESTION:
min ×2/4/8 operations to transform
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
ratio must be power of2
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
a,b

DERIVED QUANTITIES / USEFUL STATE:
ratio must be power of2

UNKNOWN / ANSWER:
min ×2/4/8 operations to transform
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
exponent difference grouped by3
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
exponent difference grouped by3
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
exponent difference grouped by3

        ↓ simplify / rearrange / classify

factorization
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
ratio must be power of2
      ↓
EXTRACT TARGET
min ×2/4/8 operations to transform
      ↓
WRITE FORMULA
exponent difference grouped by3
      ↓
TRANSFORM
factorization
      ↓
ALGORITHM
formula
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`a,b`**.
2.  Ignore narrative names and rewrite the requirement using
    **`ratio must be power of2`**.
3.  Express the requirement mathematically as
    **`exponent difference grouped by3`**.
4.  Simplify it until the recognizable form **factorization** appears.
5.  Implement **formula** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          a,b

Target:         min ×2/4/8 operations to transform

Variables:      ratio must be power of2

Formula:        exponent difference grouped by3

Transform:      factorization

Algorithm:      formula

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    ratio must be power of2

DEFINE the target:
    min ×2/4/8 operations to transform

TRANSLATE the condition:
    exponent difference grouped by3

SIMPLIFY / TRANSFORM:
    factorization

APPLY:
    formula

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`exponent difference grouped by3`** and the derived method
    **formula**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `exponent difference grouped by3`, test **factorization**
    immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 117 --- Problem 117

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1095A --- Repeating
Cipher](https://codeforces.com/problemset/problem/1095/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`encoded string`**. After removing the
story/context, the real task is:

``` text
INPUT / GIVEN:
encoded string

WHAT MUST BE FOUND:
decode chars at positions with jumps 1,2,3...

MATHEMATICAL OBJECTS THAT MATTER:
index += step

CORE RELATION:
triangular positions

FINAL MATHEMATICAL VIEW:
simulation
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **triangular positions**, after which the useful form is
**simulation**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1095A --- Repeating Cipher (Index Pattern / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `triangular positions`. Once the story is stripped
    away, recognize **simulation**, then implement **O(sqrt n)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
encoded string

QUESTION:
decode chars at positions with jumps 1,2,3...
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
index += step
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
encoded string

DERIVED QUANTITIES / USEFUL STATE:
index += step

UNKNOWN / ANSWER:
decode chars at positions with jumps 1,2,3...
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
triangular positions
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
triangular positions
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
triangular positions

        ↓ simplify / rearrange / classify

simulation
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
index += step
      ↓
EXTRACT TARGET
decode chars at positions with jumps 1,2,3...
      ↓
WRITE FORMULA
triangular positions
      ↓
TRANSFORM
simulation
      ↓
ALGORITHM
O(sqrt n)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`encoded string`**.
2.  Ignore narrative names and rewrite the requirement using
    **`index += step`**.
3.  Express the requirement mathematically as
    **`triangular positions`**.
4.  Simplify it until the recognizable form **simulation** appears.
5.  Implement **O(sqrt n)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          encoded string

Target:         decode chars at positions with jumps 1,2,3...

Variables:      index += step

Formula:        triangular positions

Transform:      simulation

Algorithm:      O(sqrt n)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    index += step

DEFINE the target:
    decode chars at positions with jumps 1,2,3...

TRANSLATE the condition:
    triangular positions

SIMPLIFY / TRANSFORM:
    simulation

APPLY:
    O(sqrt n)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`triangular positions`** and the derived method **O(sqrt n)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `triangular positions`, test **simulation** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 118 --- Problem 118

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1324A --- Yet Another Tetris
Problem](https://codeforces.com/problemset/problem/1324/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`array`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
array

WHAT MUST BE FOUND:
can equalize by subtracting 2

MATHEMATICAL OBJECTS THAT MATTER:
differences preserve parity

CORE RELATION:
all same parity

FINAL MATHEMATICAL VIEW:
parity invariant
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **all same parity**, after which the useful form is **parity
invariant**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1324A --- Yet Another Tetris Problem (Parity / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `all same parity`. Once the story is stripped away,
    recognize **parity invariant**, then implement **scan**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
array

QUESTION:
can equalize by subtracting 2
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
differences preserve parity
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
array

DERIVED QUANTITIES / USEFUL STATE:
differences preserve parity

UNKNOWN / ANSWER:
can equalize by subtracting 2
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
all same parity
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
all same parity
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
all same parity

        ↓ simplify / rearrange / classify

parity invariant
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
differences preserve parity
      ↓
EXTRACT TARGET
can equalize by subtracting 2
      ↓
WRITE FORMULA
all same parity
      ↓
TRANSFORM
parity invariant
      ↓
ALGORITHM
scan
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`array`**.
2.  Ignore narrative names and rewrite the requirement using
    **`differences preserve parity`**.
3.  Express the requirement mathematically as **`all same parity`**.
4.  Simplify it until the recognizable form **parity invariant**
    appears.
5.  Implement **scan** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          array

Target:         can equalize by subtracting 2

Variables:      differences preserve parity

Formula:        all same parity

Transform:      parity invariant

Algorithm:      scan

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    differences preserve parity

DEFINE the target:
    can equalize by subtracting 2

TRANSLATE the condition:
    all same parity

SIMPLIFY / TRANSFORM:
    parity invariant

APPLY:
    scan

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`all same parity`** and the derived method **scan**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `all same parity`, test **parity invariant** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 119 --- Problem 119

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1462A --- Favorite
Sequence](https://codeforces.com/problemset/problem/1462/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`array`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
array

WHAT MUST BE FOUND:
reorder alternating left/right

MATHEMATICAL OBJECTS THAT MATTER:
take l,r,l+1,r-1

CORE RELATION:
index pattern

FINAL MATHEMATICAL VIEW:
two pointers
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **index pattern**, after which the useful form is **two pointers**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1462A --- Favorite Sequence (Two Pointers / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `index pattern`. Once the story is stripped away,
    recognize **two pointers**, then implement **O(n)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
array

QUESTION:
reorder alternating left/right
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
take l,r,l+1,r-1
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
array

DERIVED QUANTITIES / USEFUL STATE:
take l,r,l+1,r-1

UNKNOWN / ANSWER:
reorder alternating left/right
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
index pattern
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
index pattern
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
index pattern

        ↓ simplify / rearrange / classify

two pointers
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
take l,r,l+1,r-1
      ↓
EXTRACT TARGET
reorder alternating left/right
      ↓
WRITE FORMULA
index pattern
      ↓
TRANSFORM
two pointers
      ↓
ALGORITHM
O(n)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`array`**.
2.  Ignore narrative names and rewrite the requirement using
    **`take l,r,l+1,r-1`**.
3.  Express the requirement mathematically as **`index pattern`**.
4.  Simplify it until the recognizable form **two pointers** appears.
5.  Implement **O(n)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          array

Target:         reorder alternating left/right

Variables:      take l,r,l+1,r-1

Formula:        index pattern

Transform:      two pointers

Algorithm:      O(n)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    take l,r,l+1,r-1

DEFINE the target:
    reorder alternating left/right

TRANSLATE the condition:
    index pattern

SIMPLIFY / TRANSFORM:
    two pointers

APPLY:
    O(n)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`index pattern`** and the derived method **O(n)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `index pattern`, test **two pointers** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 120 --- Problem 120

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1619A --- Polycarp and Sums of Subsequences / Square
String?](https://codeforces.com/problemset/problem/1619/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`s`**. After removing the story/context, the real
task is:

``` text
INPUT / GIVEN:
s

WHAT MUST BE FOUND:
is s two equal halves

MATHEMATICAL OBJECTS THAT MATTER:
len even and first half=second

CORE RELATION:
equation on substrings

FINAL MATHEMATICAL VIEW:
direct
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **equation on substrings**, after which the useful form is
**direct**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1619A --- Polycarp and Sums of Subsequences / Square String? (String / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `equation on substrings`. Once the story is stripped
    away, recognize **direct**, then implement **O(n)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
s

QUESTION:
is s two equal halves
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
len even and first half=second
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
s

DERIVED QUANTITIES / USEFUL STATE:
len even and first half=second

UNKNOWN / ANSWER:
is s two equal halves
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
equation on substrings
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
equation on substrings
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
equation on substrings

        ↓ simplify / rearrange / classify

direct
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
len even and first half=second
      ↓
EXTRACT TARGET
is s two equal halves
      ↓
WRITE FORMULA
equation on substrings
      ↓
TRANSFORM
direct
      ↓
ALGORITHM
O(n)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`s`**.
2.  Ignore narrative names and rewrite the requirement using
    **`len even and first half=second`**.
3.  Express the requirement mathematically as
    **`equation on substrings`**.
4.  Simplify it until the recognizable form **direct** appears.
5.  Implement **O(n)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          s

Target:         is s two equal halves

Variables:      len even and first half=second

Formula:        equation on substrings

Transform:      direct

Algorithm:      O(n)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    len even and first half=second

DEFINE the target:
    is s two equal halves

TRANSLATE the condition:
    equation on substrings

SIMPLIFY / TRANSFORM:
    direct

APPLY:
    O(n)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`equation on substrings`** and the derived method **O(n)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `equation on substrings`, test **direct** immediately.

# Pattern 13 --- Mixed Blind Decoding

## Pattern Overview

These mix two basic ideas. The goal is to practice the full pipeline
without relying on a topic label.

``` text
Different CF stories
       ↓ remove nouns
Same mathematical family
       ↓
Mixed Blind Decoding
```

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 121 --- Problem 121

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1538C --- Challenging Cliffs / Number of
Pairs](https://codeforces.com/problemset/problem/1538/C)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`array,l,r`**. After removing the story/context,
the real task is:

``` text
INPUT / GIVEN:
array,l,r

WHAT MUST BE FOUND:
count pair sums in interval

MATHEMATICAL OBJECTS THAT MATTER:
F(r)-F(l-1)

CORE RELATION:
sort + two pointers

FINAL MATHEMATICAL VIEW:
count bounded pairs
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **sort + two pointers**, after which the useful form is **count
bounded pairs**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1538C --- Challenging Cliffs / Number of Pairs (Sorting+Counting / Codeforces / 1300)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `sort + two pointers`. Once the story is stripped away,
    recognize **count bounded pairs**, then implement **O(nlogn)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
array,l,r

QUESTION:
count pair sums in interval
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
F(r)-F(l-1)
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
array,l,r

DERIVED QUANTITIES / USEFUL STATE:
F(r)-F(l-1)

UNKNOWN / ANSWER:
count pair sums in interval
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
sort + two pointers
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
sort + two pointers
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
sort + two pointers

        ↓ simplify / rearrange / classify

count bounded pairs
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
F(r)-F(l-1)
      ↓
EXTRACT TARGET
count pair sums in interval
      ↓
WRITE FORMULA
sort + two pointers
      ↓
TRANSFORM
count bounded pairs
      ↓
ALGORITHM
O(nlogn)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`array,l,r`**.
2.  Ignore narrative names and rewrite the requirement using
    **`F(r)-F(l-1)`**.
3.  Express the requirement mathematically as **`sort + two pointers`**.
4.  Simplify it until the recognizable form **count bounded pairs**
    appears.
5.  Implement **O(nlogn)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          array,l,r

Target:         count pair sums in interval

Variables:      F(r)-F(l-1)

Formula:        sort + two pointers

Transform:      count bounded pairs

Algorithm:      O(nlogn)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    F(r)-F(l-1)

DEFINE the target:
    count pair sums in interval

TRANSLATE the condition:
    sort + two pointers

SIMPLIFY / TRANSFORM:
    count bounded pairs

APPLY:
    O(nlogn)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`sort + two pointers`** and the derived method **O(nlogn)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `sort + two pointers`, test **count bounded pairs** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 122 --- Problem 122

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1475B --- New Year's
Number](https://codeforces.com/problemset/problem/1475/B)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`n`**. After removing the story/context, the real
task is:

``` text
INPUT / GIVEN:
n

WHAT MUST BE FOUND:
2020a+2021b=n

MATHEMATICAL OBJECTS THAT MATTER:
2021=2020+1

CORE RELATION:
b=n%2020 candidate

FINAL MATHEMATICAL VIEW:
feasibility
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **b=n%2020 candidate**, after which the useful form is
**feasibility**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1475B --- New Year's Number (Diophantine+Modulo / Codeforces / 900)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `b=n%2020 candidate`. Once the story is stripped away,
    recognize **feasibility**, then implement **O(1)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
n

QUESTION:
2020a+2021b=n
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
2021=2020+1
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
n

DERIVED QUANTITIES / USEFUL STATE:
2021=2020+1

UNKNOWN / ANSWER:
2020a+2021b=n
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
b=n%2020 candidate
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
b=n%2020 candidate
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
b=n%2020 candidate

        ↓ simplify / rearrange / classify

feasibility
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
2021=2020+1
      ↓
EXTRACT TARGET
2020a+2021b=n
      ↓
WRITE FORMULA
b=n%2020 candidate
      ↓
TRANSFORM
feasibility
      ↓
ALGORITHM
O(1)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`n`**.
2.  Ignore narrative names and rewrite the requirement using
    **`2021=2020+1`**.
3.  Express the requirement mathematically as **`b=n%2020 candidate`**.
4.  Simplify it until the recognizable form **feasibility** appears.
5.  Implement **O(1)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          n

Target:         2020a+2021b=n

Variables:      2021=2020+1

Formula:        b=n%2020 candidate

Transform:      feasibility

Algorithm:      O(1)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    2021=2020+1

DEFINE the target:
    2020a+2021b=n

TRANSLATE the condition:
    b=n%2020 candidate

SIMPLIFY / TRANSFORM:
    feasibility

APPLY:
    O(1)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`b=n%2020 candidate`** and the derived method **O(1)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `b=n%2020 candidate`, test **feasibility** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 123 --- Problem 123

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1374A --- Required
Remainder](https://codeforces.com/problemset/problem/1374/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`x,y,n`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
x,y,n

WHAT MUST BE FOUND:
largest k<=n with k%x=y

MATHEMATICAL OBJECTS THAT MATTER:
k=tx+y

CORE RELATION:
maximize t under bound

FINAL MATHEMATICAL VIEW:
floor
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **maximize t under bound**, after which the useful form is **floor**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1374A --- Required Remainder (Modulo+Optimization / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `maximize t under bound`. Once the story is stripped
    away, recognize **floor**, then implement **O(1)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
x,y,n

QUESTION:
largest k<=n with k%x=y
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
k=tx+y
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
x,y,n

DERIVED QUANTITIES / USEFUL STATE:
k=tx+y

UNKNOWN / ANSWER:
largest k<=n with k%x=y
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
maximize t under bound
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
maximize t under bound
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
maximize t under bound

        ↓ simplify / rearrange / classify

floor
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
k=tx+y
      ↓
EXTRACT TARGET
largest k<=n with k%x=y
      ↓
WRITE FORMULA
maximize t under bound
      ↓
TRANSFORM
floor
      ↓
ALGORITHM
O(1)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`x,y,n`**.
2.  Ignore narrative names and rewrite the requirement using
    **`k=tx+y`**.
3.  Express the requirement mathematically as
    **`maximize t under bound`**.
4.  Simplify it until the recognizable form **floor** appears.
5.  Implement **O(1)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          x,y,n

Target:         largest k<=n with k%x=y

Variables:      k=tx+y

Formula:        maximize t under bound

Transform:      floor

Algorithm:      O(1)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    k=tx+y

DEFINE the target:
    largest k<=n with k%x=y

TRANSLATE the condition:
    maximize t under bound

SIMPLIFY / TRANSFORM:
    floor

APPLY:
    O(1)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`maximize t under bound`** and the derived method **O(1)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `maximize t under bound`, test **floor** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 124 --- Problem 124

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1551A --- Polycarp and
Coins](https://codeforces.com/problemset/problem/1551/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`n`**. After removing the story/context, the real
task is:

``` text
INPUT / GIVEN:
n

WHAT MUST BE FOUND:
c1+2c2=n with counts close

MATHEMATICAL OBJECTS THAT MATTER:
near n/3

CORE RELATION:
n%3 cases

FINAL MATHEMATICAL VIEW:
construct counts
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **n%3 cases**, after which the useful form is **construct counts**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1551A --- Polycarp and Coins (Equation+Balancing / Codeforces / 800)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `n%3 cases`. Once the story is stripped away, recognize
    **construct counts**, then implement **O(1)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
n

QUESTION:
c1+2c2=n with counts close
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
near n/3
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
n

DERIVED QUANTITIES / USEFUL STATE:
near n/3

UNKNOWN / ANSWER:
c1+2c2=n with counts close
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
n%3 cases
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
n%3 cases
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
n%3 cases

        ↓ simplify / rearrange / classify

construct counts
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
near n/3
      ↓
EXTRACT TARGET
c1+2c2=n with counts close
      ↓
WRITE FORMULA
n%3 cases
      ↓
TRANSFORM
construct counts
      ↓
ALGORITHM
O(1)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`n`**.
2.  Ignore narrative names and rewrite the requirement using
    **`near n/3`**.
3.  Express the requirement mathematically as **`n%3 cases`**.
4.  Simplify it until the recognizable form **construct counts**
    appears.
5.  Implement **O(1)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          n

Target:         c1+2c2=n with counts close

Variables:      near n/3

Formula:        n%3 cases

Transform:      construct counts

Algorithm:      O(1)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    near n/3

DEFINE the target:
    c1+2c2=n with counts close

TRANSLATE the condition:
    n%3 cases

SIMPLIFY / TRANSFORM:
    construct counts

APPLY:
    O(1)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`n%3 cases`** and the derived method **O(1)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `n%3 cases`, test **construct counts** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 125 --- Problem 125

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1593B --- Make it Divisible by
25](https://codeforces.com/problemset/problem/1593/B)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`digits`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
digits

WHAT MUST BE FOUND:
min deletions

MATHEMATICAL OBJECTS THAT MATTER:
last2 digits pattern

CORE RELATION:
search from right

FINAL MATHEMATICAL VIEW:
four targets
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **search from right**, after which the useful form is **four
targets**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1593B --- Make it Divisible by 25 (Divisibility+String / Codeforces / 900)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `search from right`. Once the story is stripped away,
    recognize **four targets**, then implement **O(n)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
digits

QUESTION:
min deletions
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
last2 digits pattern
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
digits

DERIVED QUANTITIES / USEFUL STATE:
last2 digits pattern

UNKNOWN / ANSWER:
min deletions
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
search from right
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
search from right
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
search from right

        ↓ simplify / rearrange / classify

four targets
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
last2 digits pattern
      ↓
EXTRACT TARGET
min deletions
      ↓
WRITE FORMULA
search from right
      ↓
TRANSFORM
four targets
      ↓
ALGORITHM
O(n)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`digits`**.
2.  Ignore narrative names and rewrite the requirement using
    **`last2 digits pattern`**.
3.  Express the requirement mathematically as **`search from right`**.
4.  Simplify it until the recognizable form **four targets** appears.
5.  Implement **O(n)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          digits

Target:         min deletions

Variables:      last2 digits pattern

Formula:        search from right

Transform:      four targets

Algorithm:      O(n)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    last2 digits pattern

DEFINE the target:
    min deletions

TRANSLATE the condition:
    search from right

SIMPLIFY / TRANSFORM:
    four targets

APPLY:
    O(n)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`search from right`** and the derived method **O(n)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `search from right`, test **four targets** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 126 --- Problem 126

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1669F --- Eating
Candies](https://codeforces.com/problemset/problem/1669/F)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`array`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
array

WHAT MUST BE FOUND:
equal left/right eaten sum maximize count

MATHEMATICAL OBJECTS THAT MATTER:
monotone sums

CORE RELATION:
advance smaller side

FINAL MATHEMATICAL VIEW:
two pointers
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **advance smaller side**, after which the useful form is **two
pointers**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1669F --- Eating Candies (Prefix+Two Pointers / Codeforces / 1100)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `advance smaller side`. Once the story is stripped
    away, recognize **two pointers**, then implement **O(n)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
array

QUESTION:
equal left/right eaten sum maximize count
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
monotone sums
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
array

DERIVED QUANTITIES / USEFUL STATE:
monotone sums

UNKNOWN / ANSWER:
equal left/right eaten sum maximize count
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
advance smaller side
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
advance smaller side
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
advance smaller side

        ↓ simplify / rearrange / classify

two pointers
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
monotone sums
      ↓
EXTRACT TARGET
equal left/right eaten sum maximize count
      ↓
WRITE FORMULA
advance smaller side
      ↓
TRANSFORM
two pointers
      ↓
ALGORITHM
O(n)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`array`**.
2.  Ignore narrative names and rewrite the requirement using
    **`monotone sums`**.
3.  Express the requirement mathematically as
    **`advance smaller side`**.
4.  Simplify it until the recognizable form **two pointers** appears.
5.  Implement **O(n)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          array

Target:         equal left/right eaten sum maximize count

Variables:      monotone sums

Formula:        advance smaller side

Transform:      two pointers

Algorithm:      O(n)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    monotone sums

DEFINE the target:
    equal left/right eaten sum maximize count

TRANSLATE the condition:
    advance smaller side

SIMPLIFY / TRANSFORM:
    two pointers

APPLY:
    O(n)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`advance smaller side`** and the derived method **O(n)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `advance smaller side`, test **two pointers** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 127 --- Problem 127

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1793C --- Dora and
Search](https://codeforces.com/problemset/problem/1793/C)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`permutation`**. After removing the story/context,
the real task is:

``` text
INPUT / GIVEN:
permutation

WHAT MUST BE FOUND:
find non-extreme-ended segment

MATHEMATICAL OBJECTS THAT MATTER:
peel min/max endpoints

CORE RELATION:
lo/hi invariant

FINAL MATHEMATICAL VIEW:
two pointers
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **lo/hi invariant**, after which the useful form is **two pointers**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1793C --- Dora and Search (Extremes+Two Pointers / Codeforces / 1200)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `lo/hi invariant`. Once the story is stripped away,
    recognize **two pointers**, then implement **O(n)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
permutation

QUESTION:
find non-extreme-ended segment
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
peel min/max endpoints
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
permutation

DERIVED QUANTITIES / USEFUL STATE:
peel min/max endpoints

UNKNOWN / ANSWER:
find non-extreme-ended segment
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
lo/hi invariant
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
lo/hi invariant
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
lo/hi invariant

        ↓ simplify / rearrange / classify

two pointers
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
peel min/max endpoints
      ↓
EXTRACT TARGET
find non-extreme-ended segment
      ↓
WRITE FORMULA
lo/hi invariant
      ↓
TRANSFORM
two pointers
      ↓
ALGORITHM
O(n)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`permutation`**.
2.  Ignore narrative names and rewrite the requirement using
    **`peel min/max endpoints`**.
3.  Express the requirement mathematically as **`lo/hi invariant`**.
4.  Simplify it until the recognizable form **two pointers** appears.
5.  Implement **O(n)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          permutation

Target:         find non-extreme-ended segment

Variables:      peel min/max endpoints

Formula:        lo/hi invariant

Transform:      two pointers

Algorithm:      O(n)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    peel min/max endpoints

DEFINE the target:
    find non-extreme-ended segment

TRANSLATE the condition:
    lo/hi invariant

SIMPLIFY / TRANSFORM:
    two pointers

APPLY:
    O(n)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`lo/hi invariant`** and the derived method **O(n)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `lo/hi invariant`, test **two pointers** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 128 --- Problem 128

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 327A --- Flipping
Game](https://codeforces.com/problemset/problem/327/A)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`binary array`**. After removing the
story/context, the real task is:

``` text
INPUT / GIVEN:
binary array

WHAT MUST BE FOUND:
one flip maximize ones

MATHEMATICAL OBJECTS THAT MATTER:
gain map 0→+1,1→-1

CORE RELATION:
maximum subarray

FINAL MATHEMATICAL VIEW:
Kadane
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **maximum subarray**, after which the useful form is **Kadane**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 327A --- Flipping Game (Transform+Optimization / Codeforces / 1200)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `maximum subarray`. Once the story is stripped away,
    recognize **Kadane**, then implement **O(n)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
binary array

QUESTION:
one flip maximize ones
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
gain map 0→+1,1→-1
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
binary array

DERIVED QUANTITIES / USEFUL STATE:
gain map 0→+1,1→-1

UNKNOWN / ANSWER:
one flip maximize ones
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
maximum subarray
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
maximum subarray
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
maximum subarray

        ↓ simplify / rearrange / classify

Kadane
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
gain map 0→+1,1→-1
      ↓
EXTRACT TARGET
one flip maximize ones
      ↓
WRITE FORMULA
maximum subarray
      ↓
TRANSFORM
Kadane
      ↓
ALGORITHM
O(n)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`binary array`**.
2.  Ignore narrative names and rewrite the requirement using
    **`gain map 0→+1,1→-1`**.
3.  Express the requirement mathematically as **`maximum subarray`**.
4.  Simplify it until the recognizable form **Kadane** appears.
5.  Implement **O(n)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          binary array

Target:         one flip maximize ones

Variables:      gain map 0→+1,1→-1

Formula:        maximum subarray

Transform:      Kadane

Algorithm:      O(n)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    gain map 0→+1,1→-1

DEFINE the target:
    one flip maximize ones

TRANSLATE the condition:
    maximum subarray

SIMPLIFY / TRANSFORM:
    Kadane

APPLY:
    O(n)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`maximum subarray`** and the derived method **O(n)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `maximum subarray`, test **Kadane** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 129 --- Problem 129

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 1520D --- Same
Differences](https://codeforces.com/problemset/problem/1520/D)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`array`**. After removing the story/context, the
real task is:

``` text
INPUT / GIVEN:
array

WHAT MUST BE FOUND:
count special pairs

MATHEMATICAL OBJECTS THAT MATTER:
a[j]-j=a[i]-i

CORE RELATION:
equal transformed keys

FINAL MATHEMATICAL VIEW:
hash frequency
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **equal transformed keys**, after which the useful form is **hash
frequency**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 1520D --- Same Differences (Algebra+Frequency / Codeforces / 1200)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `equal transformed keys`. Once the story is stripped
    away, recognize **hash frequency**, then implement **O(n)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
array

QUESTION:
count special pairs
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
a[j]-j=a[i]-i
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
array

DERIVED QUANTITIES / USEFUL STATE:
a[j]-j=a[i]-i

UNKNOWN / ANSWER:
count special pairs
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
equal transformed keys
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
equal transformed keys
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
equal transformed keys

        ↓ simplify / rearrange / classify

hash frequency
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
a[j]-j=a[i]-i
      ↓
EXTRACT TARGET
count special pairs
      ↓
WRITE FORMULA
equal transformed keys
      ↓
TRANSFORM
hash frequency
      ↓
ALGORITHM
O(n)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`array`**.
2.  Ignore narrative names and rewrite the requirement using
    **`a[j]-j=a[i]-i`**.
3.  Express the requirement mathematically as
    **`equal transformed keys`**.
4.  Simplify it until the recognizable form **hash frequency** appears.
5.  Implement **O(n)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          array

Target:         count special pairs

Variables:      a[j]-j=a[i]-i

Formula:        equal transformed keys

Transform:      hash frequency

Algorithm:      O(n)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    a[j]-j=a[i]-i

DEFINE the target:
    count special pairs

TRANSLATE the condition:
    equal transformed keys

SIMPLIFY / TRANSFORM:
    hash frequency

APPLY:
    O(n)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`equal transformed keys`** and the derived method **O(n)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `equal transformed keys`, test **hash frequency** immediately.

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 130 --- Problem 130

```{=html}
<!-- ============================================================ -->
```
Problem Link: [CF 276C --- Little Girl and Maximum
Sum](https://codeforces.com/problemset/problem/276/C)

**Problem Summary --- What is the problem actually asking?**

The statement gives **`array,range queries`**. After removing the
story/context, the real task is:

``` text
INPUT / GIVEN:
array,range queries

WHAT MUST BE FOUND:
maximize weighted sum

MATHEMATICAL OBJECTS THAT MATTER:
usage frequency per index

CORE RELATION:
sort both sequences

FINAL MATHEMATICAL VIEW:
rearrangement
```

In other words, do **not** try to remember the characters, objects,
game, or story. Ask: *"What numerical state is given, what condition
must the answer satisfy, and what quantity am I
minimizing/maximizing/counting/testing?"* For this problem that reduces
to **sort both sequences**, after which the useful form is
**rearrangement**.

**Everyday-Life Scenario Map --- Repeated Trips / Loads**

Imagine you must move `D` kg of groceries upstairs and can carry at most
`K` kg per trip.

``` text
Real world:
Required work = D
Capacity per trip = K
Number of trips = m

After m trips:
maximum work completed = m × K

Need:
m × K >= D

Therefore:
m = ceil(D / K)
```

The nouns changed---elephant, moves, vessels, trips---but the
mathematical model is **required amount ÷ maximum useful amount per
operation**.

### CF 276C --- Little Girl and Maximum Sum (Difference+Sorting / Codeforces / 1400)

-   **Core Invariant / Key Insight:** The decisive mathematical
    statement is `sort both sequences`. Once the story is stripped away,
    recognize **rearrangement**, then implement **O((n+q)logn)**.

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics:**

``` text
STORY / DOMAIN WORDS:
People, named objects, game/theme-specific nouns, narrative actions
        ↓
Usually discard unless they define a numeric rule.

KEEP:
array,range queries

QUESTION:
maximize weighted sum
```

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical
    Roles:**

``` text
story object / name
        ↓ replace by
number / value / index / array / string / operation / state

For this problem keep:
usage frequency per index
```

The goal is to make the statement readable even if every story-specific
noun is renamed.

-   **Step 3 --- Extract the Variables:**

``` text
GIVEN VARIABLES / STATE:
array,range queries

DERIVED QUANTITIES / USEFUL STATE:
usage frequency per index

UNKNOWN / ANSWER:
maximize weighted sum
```

-   **Step 4 --- Translate the Important English into Mathematics:**

``` text
English requirement from the statement
        ↓ remove narrative wording

Mathematical requirement:
sort both sequences
```

Ask which symbols naturally express the wording:

``` text
equal / same       → =
at least           → >=
at most            → <=
divisible          → % == 0
odd / even         → mod 2
distance           → absolute difference
minimum operations → lower bound + achievability
count pairs        → frequency / contribution candidate
rearrange          → order may be disposable
```

-   **Step 5 --- Write the Mathematical Formula / Model:**

``` text
sort both sequences
```

This is the point where the original Codeforces story should largely
disappear.

-   **Step 6 --- Simplify / Transform the Formula:**

``` text
Original mathematical condition:
sort both sequences

        ↓ simplify / rearrange / classify

rearrangement
```

-   **Step 7 --- Mathematical Collapse:**

``` text
FULL STATEMENT
      ↓
REMOVE STORY NOUNS
      ↓
usage frequency per index
      ↓
EXTRACT TARGET
maximize weighted sum
      ↓
WRITE FORMULA
sort both sequences
      ↓
TRANSFORM
rearrangement
      ↓
ALGORITHM
O((n+q)logn)
```

-   **Step-by-Step Solution Logic:**

1.  Read the input and identify only the quantities represented by
    **`array,range queries`**.
2.  Ignore narrative names and rewrite the requirement using
    **`usage frequency per index`**.
3.  Express the requirement mathematically as **`sort both sequences`**.
4.  Simplify it until the recognizable form **rearrangement** appears.
5.  Implement **O((n+q)logn)** and output the requested answer.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Statement
   │
   ├─ story words / names ───────────────→ discard
   │
   └─ numerical rules ───────────────────→ keep
                                             │
                                             ▼
Given:          array,range queries

Target:         maximize weighted sum

Variables:      usage frequency per index

Formula:        sort both sequences

Transform:      rearrangement

Algorithm:      O((n+q)logn)

Final mental model:
Statement → Variables → Formula → Transform → Algorithm
```

-   **Pseudocode:**

``` text
READ the required input

REMOVE the story mentally

EXTRACT:
    usage frequency per index

DEFINE the target:
    maximize weighted sum

TRANSLATE the condition:
    sort both sequences

SIMPLIFY / TRANSFORM:
    rearrangement

APPLY:
    O((n+q)logn)

PRINT the answer
```

-   **C++17 Implementation Note:** Do not memorize code from the story.
    Reconstruct the implementation from the final mathematical model
    **`sort both sequences`** and the derived method **O((n+q)logn)**.

-   **Recognition Trigger:** When a new statement can be rewritten as
    `sort both sequences`, test **rearrangement** immediately.

# Phase-1 Recognition Checklist

``` text
□ What is the problem actually asking me to output?
□ Which nouns can be deleted without changing the rules?
□ What numbers / arrays / strings / indices remain?
□ What symbols should represent them?
□ What is the unknown?
□ What is the target: min / max / count / yes-no / construct?
□ What exact English sentence defines validity?
□ Can I translate it to =, <=, >=, %, parity, gcd, xor, sum, difference?
□ What does one operation do mathematically?
□ What changes? What stays invariant?
□ Can I rearrange the equation?
□ Can I define a transformed key?
□ Can I replace values by parity / remainder / frequency?
□ Can sorting expose the structure?
□ What standard form appears?
□ What algorithm is now almost forced by the model?
```

# Reusable Problem Template

------------------------------------------------------------------------

```{=html}
<!-- ============================================================ -->
```
# Problem 131 --- Problem 131

```{=html}
<!-- ============================================================ -->
```
Problem Link: `[link]`

**Problem Summary --- What is the problem actually asking?**

``` text
INPUT / GIVEN:
...

WHAT MUST BE FOUND:
...

MATHEMATICAL OBJECTS:
...

CORE RELATION:
...

FINAL MATHEMATICAL VIEW:
...
```

### \[Problem Name\] (\[Topic\] / Codeforces / \[Rating\])

-   **Core Invariant / Key Insight:** ...

-   **Step 1 --- Read the Statement and Separate Story from
    Mathematics**

-   **Step 2 --- Remove Nouns and Replace Them with Mathematical Roles**

-   **Step 3 --- Extract the Variables**

-   **Step 4 --- Translate Important English into Mathematics**

-   **Step 5 --- Write the Mathematical Formula / Model**

-   **Step 6 --- Simplify / Transform the Formula**

-   **Step 7 --- Mathematical Collapse**

-   **Step-by-Step Solution Logic**

-   **ASCII Execution Trace / Visual Dry Run**

-   **Pseudocode**

-   **C++17 Implementation Note**

-   **Recognition Trigger**

## End Goal

After enough repetitions, the mental process should compress to:

``` text
"long CF story..."
       ↓
n, a[i], i, j, k
       ↓
condition
       ↓
equation
       ↓
transformation
       ↓
pattern
       ↓
solution
```

------------------------------------------------------------------------
