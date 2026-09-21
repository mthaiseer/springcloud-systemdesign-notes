# 01 --- CF Mathematical Modeling Phase 1: Decode

> **130 guided Codeforces decoding drills.**
>
> **Primary skill:**
> `Statement → remove story → variables → mathematical condition → simplification → form → algorithm`.
>
> This workbook intentionally repeats elementary mathematical ideas
> under different Codeforces wording. The repetition is the training.

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

# How to Use This Workbook

For the first few problems in a pattern, read the entire guided decode.
As recognition improves, cover the solution sections and attempt the
**Decode → Variables → English→Math → Model** steps yourself first.

Use this order on every problem:

``` text
STATEMENT
   ↓
REMOVE STORY / DOMAIN NOUNS
   ↓
KEEP MATHEMATICAL OBJECTS
   ↓
DEFINE GIVEN / UNKNOWN / TARGET
   ↓
TRANSLATE ENGLISH → MATH
   ↓
WRITE EQUATION / INEQUALITY / STATE
   ↓
SIMPLIFY / TRANSFORM
   ↓
RECOGNIZE FORM
   ↓
ALGORITHM
   ↓
PROOF / FEASIBILITY
   ↓
PSEUDOCODE
   ↓
C++ IDEA
```

> **Important:** The statement summaries below are concise paraphrases
> for training. Use the linked Codeforces page for the full official
> statement and constraints.

# Phase-1 Decoding Pipeline

``` text
STORY NOUNS TO REMOVE:
person / candies / game / city / monster / friend / machine / etc.

MATHEMATICAL OBJECTS TO KEEP:
integers / array / string / indices / operations / constraints

GIVEN:
What values exist?

UNKNOWN:
What must be found?

TARGET:
min / max / count / existence / construction?

LANGUAGE:
"at least"      → >=
"at most"       → <=
"equal"         → =
"divisible"     → % == 0
"odd/even"      → mod 2
"minimum moves" → lower bound + achievability
"rearrange"     → order may be disposable
"any number of operations" → invariant / reachability candidate

MODEL:
Write the smallest mathematical statement that still describes the problem.

TRANSFORM:
Rearrange / normalize / define a key / take parity / take remainder / sort.

FORM:
What known mathematical shape appeared?
```

# 130-Problem Progress Tracker

Mark each problem:

-   `R1` --- recognized model immediately.
-   `R2` --- recognized after deliberate decoding.
-   `R3` --- needed a hint for the model/transformation.
-   `R4` --- model was missed; study and retry later.

**Phase-1 target:** most fresh 800--1200 problems should become `R1/R2`;
the 1300--1400 entries are stretch decoding drills.

# Pattern 1 --- Minimum Operations / Ceil Division

## Pattern Overview

Turn 'minimum moves/trips/operations' into a required change D and a
maximum useful change K. Test `m*K >= D`, then take the smallest
feasible integer m.

``` text
Statement wording
      ↓
remove domain story
      ↓
mathematical condition
      ↓
recognize: Minimum Operations / Ceil Division
```

Problem Link: [CF 617A ---
Elephant](https://codeforces.com/problemset/problem/617/A)

**Problem Summary:** Given `x`, decode the statement into mathematics
and determine `minimum moves to reach x with +1..+5`.

### CF 617A --- Elephant (Arithmetic / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to `5m >= x`.
    The key Phase-1 move is to recognize **ceil(x/5)**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
x
target: minimum moves to reach x with +1..+5
```

-   **Define Variables:**

``` text
Given:
x

Mathematical objects:
D=x, K=5

Target:
minimum moves to reach x with +1..+5
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
5m >= x

Question
        ↓
minimum moves to reach x with +1..+5
```

-   **Mathematical Model:**

``` text
5m >= x
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
D=x, K=5
  ↓ write condition
5m >= x
  ↓ simplify / recognize
ceil(x/5)
  ↓
(x+4)/5
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `D=x, K=5`.
2.  Express the requirement as `5m >= x` and simplify it to
    **ceil(x/5)**.
3.  Apply `(x+4)/5` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: x

Decode:         Ignore story nouns.
                Keep: D=x, K=5

Model:          5m >= x

Collapse:       ceil(x/5)

Algorithm:      (x+4)/5

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    D=x, K=5

MODEL:
    5m >= x

SIMPLIFY / TRANSFORM:
    ceil(x/5)

APPLY:
    (x+4)/5

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **(x+4)/5**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `5m >= x`, think **ceil(x/5)** before implementation.

Problem Link: [CF 1409A --- Yet Another Two Integers
Problem](https://codeforces.com/problemset/problem/1409/A)

**Problem Summary:** Given `a,b`, decode the statement into mathematics
and determine `minimum operations to make a=b using ±1..10`.

### CF 1409A --- Yet Another Two Integers Problem (Arithmetic / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `10m >= |a-b|`. The key Phase-1 move is to recognize
    **ceil(\|a-b\|/10)**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
a,b
target: minimum operations to make a=b using ±1..10
```

-   **Define Variables:**

``` text
Given:
a,b

Mathematical objects:
D=|a-b|, K=10

Target:
minimum operations to make a=b using ±1..10
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
10m >= |a-b|

Question
        ↓
minimum operations to make a=b using ±1..10
```

-   **Mathematical Model:**

``` text
10m >= |a-b|
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
D=|a-b|, K=10
  ↓ write condition
10m >= |a-b|
  ↓ simplify / recognize
ceil(|a-b|/10)
  ↓
(abs(a-b)+9)/10
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `D=|a-b|, K=10`.
2.  Express the requirement as `10m >= |a-b|` and simplify it to
    **ceil(\|a-b\|/10)**.
3.  Apply `(abs(a-b)+9)/10` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: a,b

Decode:         Ignore story nouns.
                Keep: D=|a-b|, K=10

Model:          10m >= |a-b|

Collapse:       ceil(|a-b|/10)

Algorithm:      (abs(a-b)+9)/10

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    D=|a-b|, K=10

MODEL:
    10m >= |a-b|

SIMPLIFY / TRANSFORM:
    ceil(|a-b|/10)

APPLY:
    (abs(a-b)+9)/10

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **(abs(a-b)+9)/10**; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `10m >= |a-b|`, think **ceil(\|a-b\|/10)** before implementation.

Problem Link: [CF 1353A --- Most Unstable
Array](https://codeforces.com/problemset/problem/1353/A)

**Problem Summary:** Given `n,m`, decode the statement into mathematics
and determine
`maximize sum of adjacent absolute differences under bounds`.

### CF 1353A --- Most Unstable Array (Formula / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `each transition <= m`. The key Phase-1 move is to recognize
    **construct extremal arrangement**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
n,m
target: maximize sum of adjacent absolute differences under bounds
```

-   **Define Variables:**

``` text
Given:
n,m

Mathematical objects:
endpoints/bounds matter

Target:
maximize sum of adjacent absolute differences under bounds
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
each transition <= m

Question
        ↓
maximize sum of adjacent absolute differences under bounds
```

-   **Mathematical Model:**

``` text
each transition <= m
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
endpoints/bounds matter
  ↓ write condition
each transition <= m
  ↓ simplify / recognize
construct extremal arrangement
  ↓
handle n=1,2,>=3
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `endpoints/bounds matter`.
2.  Express the requirement as `each transition <= m` and simplify it to
    **construct extremal arrangement**.
3.  Apply `handle n=1,2,>=3` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: n,m

Decode:         Ignore story nouns.
                Keep: endpoints/bounds matter

Model:          each transition <= m

Collapse:       construct extremal arrangement

Algorithm:      handle n=1,2,>=3

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    endpoints/bounds matter

MODEL:
    each transition <= m

SIMPLIFY / TRANSFORM:
    construct extremal arrangement

APPLY:
    handle n=1,2,>=3

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **handle n=1,2,\>=3**; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `each transition <= m`, think **construct extremal arrangement**
    before implementation.

Problem Link: [CF 1476A --- K-divisible
Sum](https://codeforces.com/problemset/problem/1476/A)

**Problem Summary:** Given `n,k`, decode the statement into mathematics
and determine
`minimum possible maximum element while sum is divisible by k`.

### CF 1476A --- K-divisible Sum (Bounds / Codeforces / 1000)

-   **Core Invariant / Key Insight:** The story collapses to
    `S = smallest multiple of k >= n`. The key Phase-1 move is to
    recognize **ceil(S/n)**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
n,k
target: minimum possible maximum element while sum is divisible by k
```

-   **Define Variables:**

``` text
Given:
n,k

Mathematical objects:
total S >= n and S multiple of k

Target:
minimum possible maximum element while sum is divisible by k
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
S = smallest multiple of k >= n

Question
        ↓
minimum possible maximum element while sum is divisible by k
```

-   **Mathematical Model:**

``` text
S = smallest multiple of k >= n
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
total S >= n and S multiple of k
  ↓ write condition
S = smallest multiple of k >= n
  ↓ simplify / recognize
ceil(S/n)
  ↓
S=((n+k-1)/k)*k
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `total S >= n and S multiple of k`.
2.  Express the requirement as `S = smallest multiple of k >= n` and
    simplify it to **ceil(S/n)**.
3.  Apply `S=((n+k-1)/k)*k` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: n,k

Decode:         Ignore story nouns.
                Keep: total S >= n and S multiple of k

Model:          S = smallest multiple of k >= n

Collapse:       ceil(S/n)

Algorithm:      S=((n+k-1)/k)*k

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    total S >= n and S multiple of k

MODEL:
    S = smallest multiple of k >= n

SIMPLIFY / TRANSFORM:
    ceil(S/n)

APPLY:
    S=((n+k-1)/k)*k

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode \*\*S=((n+k-1)/k)\*k\*\*;
    avoid memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `S = smallest multiple of k >= n`, think **ceil(S/n)** before
    implementation.

Problem Link: [CF 151A --- Soft
Drinking](https://codeforces.com/problemset/problem/151/A)

**Problem Summary:** Given `n,k,l,c,d,p,nl,np`, decode the statement
into mathematics and determine `number of toasts per friend`.

### CF 151A --- Soft Drinking (Capacity / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `min(drink/nl,limes,salt/np)/n`. The key Phase-1 move is to
    recognize **limiting resource**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
n,k,l,c,d,p,nl,np
target: number of toasts per friend
```

-   **Define Variables:**

``` text
Given:
n,k,l,c,d,p,nl,np

Mathematical objects:
three resources

Target:
number of toasts per friend
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
min(drink/nl,limes,salt/np)/n

Question
        ↓
number of toasts per friend
```

-   **Mathematical Model:**

``` text
min(drink/nl,limes,salt/np)/n
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
three resources
  ↓ write condition
min(drink/nl,limes,salt/np)/n
  ↓ simplify / recognize
limiting resource
  ↓
take minimum capacity
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `three resources`.
2.  Express the requirement as `min(drink/nl,limes,salt/np)/n` and
    simplify it to **limiting resource**.
3.  Apply `take minimum capacity` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: n,k,l,c,d,p,nl,np

Decode:         Ignore story nouns.
                Keep: three resources

Model:          min(drink/nl,limes,salt/np)/n

Collapse:       limiting resource

Algorithm:      take minimum capacity

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    three resources

MODEL:
    min(drink/nl,limes,salt/np)/n

SIMPLIFY / TRANSFORM:
    limiting resource

APPLY:
    take minimum capacity

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **take minimum capacity**;
    avoid memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `min(drink/nl,limes,salt/np)/n`, think **limiting resource** before
    implementation.

Problem Link: [CF 996A --- Hit the
Lottery](https://codeforces.com/problemset/problem/996/A)

**Problem Summary:** Given `n`, decode the statement into mathematics
and determine `minimum notes using 100,20,10,5,1`.

### CF 996A --- Hit the Lottery (Greedy/Division / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to `q=n/d`.
    The key Phase-1 move is to recognize **sum quotients**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
n
target: minimum notes using 100,20,10,5,1
```

-   **Define Variables:**

``` text
Given:
n

Mathematical objects:
largest denomination dominates

Target:
minimum notes using 100,20,10,5,1
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
q=n/d

Question
        ↓
minimum notes using 100,20,10,5,1
```

-   **Mathematical Model:**

``` text
q=n/d
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
largest denomination dominates
  ↓ write condition
q=n/d
  ↓ simplify / recognize
sum quotients
  ↓
repeated quotient/remainder
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `largest denomination dominates`.
2.  Express the requirement as `q=n/d` and simplify it to **sum
    quotients**.
3.  Apply `repeated quotient/remainder` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: n

Decode:         Ignore story nouns.
                Keep: largest denomination dominates

Model:          q=n/d

Collapse:       sum quotients

Algorithm:      repeated quotient/remainder

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    largest denomination dominates

MODEL:
    q=n/d

SIMPLIFY / TRANSFORM:
    sum quotients

APPLY:
    repeated quotient/remainder

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **repeated
    quotient/remainder**; avoid memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to `q=n/d`,
    think **sum quotients** before implementation.

Problem Link: [CF 1669A ---
Division?](https://codeforces.com/problemset/problem/1669/A)

**Problem Summary:** Given `rating`, decode the statement into
mathematics and determine `classify rating into interval`.

### CF 1669A --- Division? (Inequality / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `compare rating with cutoffs`. The key Phase-1 move is to recognize
    **interval classification**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
rating
target: classify rating into interval
```

-   **Define Variables:**

``` text
Given:
rating

Mathematical objects:
numeric boundaries

Target:
classify rating into interval
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
compare rating with cutoffs

Question
        ↓
classify rating into interval
```

-   **Mathematical Model:**

``` text
compare rating with cutoffs
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
numeric boundaries
  ↓ write condition
compare rating with cutoffs
  ↓ simplify / recognize
interval classification
  ↓
if/else
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `numeric boundaries`.
2.  Express the requirement as `compare rating with cutoffs` and
    simplify it to **interval classification**.
3.  Apply `if/else` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: rating

Decode:         Ignore story nouns.
                Keep: numeric boundaries

Model:          compare rating with cutoffs

Collapse:       interval classification

Algorithm:      if/else

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    numeric boundaries

MODEL:
    compare rating with cutoffs

SIMPLIFY / TRANSFORM:
    interval classification

APPLY:
    if/else

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **if/else**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `compare rating with cutoffs`, think **interval classification**
    before implementation.

Problem Link: [CF 1742A ---
Sum](https://codeforces.com/problemset/problem/1742/A)

**Problem Summary:** Given `a,b,c`, decode the statement into
mathematics and determine `whether one number equals sum of other two`.

### CF 1742A --- Sum (Equation / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `a+b=c etc.`. The key Phase-1 move is to recognize **direct
    feasibility**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
a,b,c
target: whether one number equals sum of other two
```

-   **Define Variables:**

``` text
Given:
a,b,c

Mathematical objects:
test 3 equations

Target:
whether one number equals sum of other two
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
a+b=c etc.

Question
        ↓
whether one number equals sum of other two
```

-   **Mathematical Model:**

``` text
a+b=c etc.
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
test 3 equations
  ↓ write condition
a+b=c etc.
  ↓ simplify / recognize
direct feasibility
  ↓
three checks
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `test 3 equations`.
2.  Express the requirement as `a+b=c etc.` and simplify it to **direct
    feasibility**.
3.  Apply `three checks` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: a,b,c

Decode:         Ignore story nouns.
                Keep: test 3 equations

Model:          a+b=c etc.

Collapse:       direct feasibility

Algorithm:      three checks

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    test 3 equations

MODEL:
    a+b=c etc.

SIMPLIFY / TRANSFORM:
    direct feasibility

APPLY:
    three checks

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **three checks**; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `a+b=c etc.`, think **direct feasibility** before implementation.

Problem Link: [CF 1850A --- To My
Critics](https://codeforces.com/problemset/problem/1850/A)

**Problem Summary:** Given `a,b,c`, decode the statement into
mathematics and determine `whether any pair sum >=10`.

### CF 1850A --- To My Critics (Bounds / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `max pair sum`. The key Phase-1 move is to recognize **sort or
    direct checks**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
a,b,c
target: whether any pair sum >=10
```

-   **Define Variables:**

``` text
Given:
a,b,c

Mathematical objects:
only 3 pairs

Target:
whether any pair sum >=10
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
max pair sum

Question
        ↓
whether any pair sum >=10
```

-   **Mathematical Model:**

``` text
max pair sum
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
only 3 pairs
  ↓ write condition
max pair sum
  ↓ simplify / recognize
sort or direct checks
  ↓
a+b>=10 || ...
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `only 3 pairs`.
2.  Express the requirement as `max pair sum` and simplify it to **sort
    or direct checks**.
3.  Apply `a+b>=10 || ...` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: a,b,c

Decode:         Ignore story nouns.
                Keep: only 3 pairs

Model:          max pair sum

Collapse:       sort or direct checks

Algorithm:      a+b>=10 || ...

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    only 3 pairs

MODEL:
    max pair sum

SIMPLIFY / TRANSFORM:
    sort or direct checks

APPLY:
    a+b>=10 || ...

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **a+b\>=10 \|\| ...**; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `max pair sum`, think **sort or direct checks** before
    implementation.

Problem Link: [CF 1878A --- How Much Does Daytona
Cost?](https://codeforces.com/problemset/problem/1878/A)

**Problem Summary:** Given `n,k,array`, decode the statement into
mathematics and determine `whether k appears`.

### CF 1878A --- How Much Does Daytona Cost? (Existence / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `∃i: a[i]=k`. The key Phase-1 move is to recognize **linear scan**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
n,k,array
target: whether k appears
```

-   **Define Variables:**

``` text
Given:
n,k,array

Mathematical objects:
target is existence

Target:
whether k appears
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
∃i: a[i]=k

Question
        ↓
whether k appears
```

-   **Mathematical Model:**

``` text
∃i: a[i]=k
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
target is existence
  ↓ write condition
∃i: a[i]=k
  ↓ simplify / recognize
linear scan
  ↓
found flag
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `target is existence`.
2.  Express the requirement as `∃i: a[i]=k` and simplify it to **linear
    scan**.
3.  Apply `found flag` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: n,k,array

Decode:         Ignore story nouns.
                Keep: target is existence

Model:          ∃i: a[i]=k

Collapse:       linear scan

Algorithm:      found flag

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    target is existence

MODEL:
    ∃i: a[i]=k

SIMPLIFY / TRANSFORM:
    linear scan

APPLY:
    found flag

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **found flag**; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `∃i: a[i]=k`, think **linear scan** before implementation.

# Pattern 2 --- Algebra / Equation Formation

## Pattern Overview

Delete the story and introduce unknowns. Translate totals, differences,
and equalities into equations; isolate the unknown or test feasibility.

``` text
Statement wording
      ↓
remove domain story
      ↓
mathematical condition
      ↓
recognize: Algebra / Equation Formation
```

Problem Link: [CF 734A --- Anton and
Danik](https://codeforces.com/problemset/problem/734/A)

**Problem Summary:** Given `n,string`, decode the statement into
mathematics and determine `who won more games`.

### CF 734A --- Anton and Danik (Counting / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `compare A and D`. The key Phase-1 move is to recognize **sign of
    A-D**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
n,string
target: who won more games
```

-   **Define Variables:**

``` text
Given:
n,string

Mathematical objects:
A=count('A'), D=count('D')

Target:
who won more games
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
compare A and D

Question
        ↓
who won more games
```

-   **Mathematical Model:**

``` text
compare A and D
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
A=count('A'), D=count('D')
  ↓ write condition
compare A and D
  ↓ simplify / recognize
sign of A-D
  ↓
count chars
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `A=count('A'), D=count('D')`.
2.  Express the requirement as `compare A and D` and simplify it to
    **sign of A-D**.
3.  Apply `count chars` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: n,string

Decode:         Ignore story nouns.
                Keep: A=count('A'), D=count('D')

Model:          compare A and D

Collapse:       sign of A-D

Algorithm:      count chars

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    A=count('A'), D=count('D')

MODEL:
    compare A and D

SIMPLIFY / TRANSFORM:
    sign of A-D

APPLY:
    count chars

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **count chars**; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `compare A and D`, think **sign of A-D** before implementation.

Problem Link: [CF 677A --- Vanya and
Fence](https://codeforces.com/problemset/problem/677/A)

**Problem Summary:** Given `n,h,heights`, decode the statement into
mathematics and determine `total width`.

### CF 677A --- Vanya and Fence (Formula / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `sum (a[i]>h ? 2:1)`. The key Phase-1 move is to recognize
    **contribution sum**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
n,h,heights
target: total width
```

-   **Define Variables:**

``` text
Given:
n,h,heights

Mathematical objects:
each person contributes 1 or 2

Target:
total width
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
sum (a[i]>h ? 2:1)

Question
        ↓
total width
```

-   **Mathematical Model:**

``` text
sum (a[i]>h ? 2:1)
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
each person contributes 1 or 2
  ↓ write condition
sum (a[i]>h ? 2:1)
  ↓ simplify / recognize
contribution sum
  ↓
linear scan
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `each person contributes 1 or 2`.
2.  Express the requirement as `sum (a[i]>h ? 2:1)` and simplify it to
    **contribution sum**.
3.  Apply `linear scan` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: n,h,heights

Decode:         Ignore story nouns.
                Keep: each person contributes 1 or 2

Model:          sum (a[i]>h ? 2:1)

Collapse:       contribution sum

Algorithm:      linear scan

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    each person contributes 1 or 2

MODEL:
    sum (a[i]>h ? 2:1)

SIMPLIFY / TRANSFORM:
    contribution sum

APPLY:
    linear scan

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **linear scan**; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `sum (a[i]>h ? 2:1)`, think **contribution sum** before
    implementation.

Problem Link: [CF 71A --- Way Too Long
Words](https://codeforces.com/problemset/problem/71/A)

**Problem Summary:** Given `word`, decode the statement into mathematics
and determine `abbreviate if length>10`.

### CF 71A --- Way Too Long Words (String/Formula / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `length condition`. The key Phase-1 move is to recognize **direct
    construction**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
word
target: abbreviate if length>10
```

-   **Define Variables:**

``` text
Given:
word

Mathematical objects:
first + (len-2) + last

Target:
abbreviate if length>10
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
length condition

Question
        ↓
abbreviate if length>10
```

-   **Mathematical Model:**

``` text
length condition
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
first + (len-2) + last
  ↓ write condition
length condition
  ↓ simplify / recognize
direct construction
  ↓
O(len)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `first + (len-2) + last`.
2.  Express the requirement as `length condition` and simplify it to
    **direct construction**.
3.  Apply `O(len)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: word

Decode:         Ignore story nouns.
                Keep: first + (len-2) + last

Model:          length condition

Collapse:       direct construction

Algorithm:      O(len)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    first + (len-2) + last

MODEL:
    length condition

SIMPLIFY / TRANSFORM:
    direct construction

APPLY:
    O(len)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(len)**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `length condition`, think **direct construction** before
    implementation.

Problem Link: [CF 791A --- Bear and Big
Brother](https://codeforces.com/problemset/problem/791/A)

**Problem Summary:** Given `a,b`, decode the statement into mathematics
and determine `years until 3^t a > 2^t b`.

### CF 791A --- Bear and Big Brother (Growth / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `a*=3,b*=2`. The key Phase-1 move is to recognize **first t with
    a\>b**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
a,b
target: years until 3^t a > 2^t b
```

-   **Define Variables:**

``` text
Given:
a,b

Mathematical objects:
simulate multiplicative equation

Target:
years until 3^t a > 2^t b
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
a*=3,b*=2

Question
        ↓
years until 3^t a > 2^t b
```

-   **Mathematical Model:**

``` text
a*=3,b*=2
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
simulate multiplicative equation
  ↓ write condition
a*=3,b*=2
  ↓ simplify / recognize
first t with a>b
  ↓
loop
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `simulate multiplicative equation`.
2.  Express the requirement as `a*=3,b*=2` and simplify it to **first t
    with a\>b**.
3.  Apply `loop` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: a,b

Decode:         Ignore story nouns.
                Keep: simulate multiplicative equation

Model:          a*=3,b*=2

Collapse:       first t with a>b

Algorithm:      loop

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    simulate multiplicative equation

MODEL:
    a*=3,b*=2

SIMPLIFY / TRANSFORM:
    first t with a>b

APPLY:
    loop

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **loop**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `a*=3,b*=2`, think **first t with a\>b** before implementation.

Problem Link: [CF 50A --- Domino
piling](https://codeforces.com/problemset/problem/50/A)

**Problem Summary:** Given `m,n`, decode the statement into mathematics
and determine `max dominoes in grid`.

### CF 50A --- Domino piling (Counting / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to `2x <= mn`.
    The key Phase-1 move is to recognize **floor(mn/2)**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
m,n
target: max dominoes in grid
```

-   **Define Variables:**

``` text
Given:
m,n

Mathematical objects:
each domino covers 2 cells

Target:
max dominoes in grid
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
2x <= mn

Question
        ↓
max dominoes in grid
```

-   **Mathematical Model:**

``` text
2x <= mn
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
each domino covers 2 cells
  ↓ write condition
2x <= mn
  ↓ simplify / recognize
floor(mn/2)
  ↓
m*n/2
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `each domino covers 2 cells`.
2.  Express the requirement as `2x <= mn` and simplify it to
    **floor(mn/2)**.
3.  Apply `m*n/2` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: m,n

Decode:         Ignore story nouns.
                Keep: each domino covers 2 cells

Model:          2x <= mn

Collapse:       floor(mn/2)

Algorithm:      m*n/2

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    each domino covers 2 cells

MODEL:
    2x <= mn

SIMPLIFY / TRANSFORM:
    floor(mn/2)

APPLY:
    m*n/2

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode \*\*m\*n/2\*\*; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `2x <= mn`, think **floor(mn/2)** before implementation.

Problem Link: [CF 231A ---
Team](https://codeforces.com/problemset/problem/231/A)

**Problem Summary:** Given `triples`, decode the statement into
mathematics and determine `count problems with >=2 yes`.

### CF 231A --- Team (Counting / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `indicator contribution`. The key Phase-1 move is to recognize
    **count**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
triples
target: count problems with >=2 yes
```

-   **Define Variables:**

``` text
Given:
triples

Mathematical objects:
sum triple >=2

Target:
count problems with >=2 yes
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
indicator contribution

Question
        ↓
count problems with >=2 yes
```

-   **Mathematical Model:**

``` text
indicator contribution
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
sum triple >=2
  ↓ write condition
indicator contribution
  ↓ simplify / recognize
count
  ↓
linear
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `sum triple >=2`.
2.  Express the requirement as `indicator contribution` and simplify it
    to **count**.
3.  Apply `linear` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: triples

Decode:         Ignore story nouns.
                Keep: sum triple >=2

Model:          indicator contribution

Collapse:       count

Algorithm:      linear

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    sum triple >=2

MODEL:
    indicator contribution

SIMPLIFY / TRANSFORM:
    count

APPLY:
    linear

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **linear**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `indicator contribution`, think **count** before implementation.

Problem Link: [CF 200B ---
Drinks](https://codeforces.com/problemset/problem/200/B)

**Problem Summary:** Given `n,p`, decode the statement into mathematics
and determine `orange percentage`.

### CF 200B --- Drinks (Average / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to `sum/n`.
    The key Phase-1 move is to recognize **mean**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
n,p
target: orange percentage
```

-   **Define Variables:**

``` text
Given:
n,p

Mathematical objects:
average of p

Target:
orange percentage
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
sum/n

Question
        ↓
orange percentage
```

-   **Mathematical Model:**

``` text
sum/n
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
average of p
  ↓ write condition
sum/n
  ↓ simplify / recognize
mean
  ↓
double sum/n
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `average of p`.
2.  Express the requirement as `sum/n` and simplify it to **mean**.
3.  Apply `double sum/n` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: n,p

Decode:         Ignore story nouns.
                Keep: average of p

Model:          sum/n

Collapse:       mean

Algorithm:      double sum/n

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    average of p

MODEL:
    sum/n

SIMPLIFY / TRANSFORM:
    mean

APPLY:
    double sum/n

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **double sum/n**; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to `sum/n`,
    think **mean** before implementation.

Problem Link: [CF 318A --- Even
Odds](https://codeforces.com/problemset/problem/318/A)

**Problem Summary:** Given `n,k`, decode the statement into mathematics
and determine `kth in odds then evens`.

### CF 318A --- Even Odds (Index Mapping / Codeforces / 900)

-   **Core Invariant / Key Insight:** The story collapses to
    `piecewise index mapping`. The key Phase-1 move is to recognize **if
    k\<=oddCount**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
n,k
target: kth in odds then evens
```

-   **Define Variables:**

``` text
Given:
n,k

Mathematical objects:
oddCount=(n+1)/2

Target:
kth in odds then evens
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
piecewise index mapping

Question
        ↓
kth in odds then evens
```

-   **Mathematical Model:**

``` text
piecewise index mapping
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
oddCount=(n+1)/2
  ↓ write condition
piecewise index mapping
  ↓ simplify / recognize
if k<=oddCount
  ↓
formula
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `oddCount=(n+1)/2`.
2.  Express the requirement as `piecewise index mapping` and simplify it
    to **if k\<=oddCount**.
3.  Apply `formula` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: n,k

Decode:         Ignore story nouns.
                Keep: oddCount=(n+1)/2

Model:          piecewise index mapping

Collapse:       if k<=oddCount

Algorithm:      formula

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    oddCount=(n+1)/2

MODEL:
    piecewise index mapping

SIMPLIFY / TRANSFORM:
    if k<=oddCount

APPLY:
    formula

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **formula**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `piecewise index mapping`, think **if k\<=oddCount** before
    implementation.

Problem Link: [CF 486A --- Calculating
Function](https://codeforces.com/problemset/problem/486/A)

**Problem Summary:** Given `n`, decode the statement into mathematics
and determine `alternating sum -1+2-3+...`.

### CF 486A --- Calculating Function (Formula / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `even n -> n/2; odd -> -(n+1)/2`. The key Phase-1 move is to
    recognize **closed form**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
n
target: alternating sum -1+2-3+...
```

-   **Define Variables:**

``` text
Given:
n

Mathematical objects:
pair terms

Target:
alternating sum -1+2-3+...
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
even n -> n/2; odd -> -(n+1)/2

Question
        ↓
alternating sum -1+2-3+...
```

-   **Mathematical Model:**

``` text
even n -> n/2; odd -> -(n+1)/2
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
pair terms
  ↓ write condition
even n -> n/2; odd -> -(n+1)/2
  ↓ simplify / recognize
closed form
  ↓
parity branch
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `pair terms`.
2.  Express the requirement as `even n -> n/2; odd -> -(n+1)/2` and
    simplify it to **closed form**.
3.  Apply `parity branch` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: n

Decode:         Ignore story nouns.
                Keep: pair terms

Model:          even n -> n/2; odd -> -(n+1)/2

Collapse:       closed form

Algorithm:      parity branch

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    pair terms

MODEL:
    even n -> n/2; odd -> -(n+1)/2

SIMPLIFY / TRANSFORM:
    closed form

APPLY:
    parity branch

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **parity branch**; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `even n -> n/2; odd -> -(n+1)/2`, think **closed form** before
    implementation.

Problem Link: [CF 1399A --- Remove
Smallest](https://codeforces.com/problemset/problem/1399/A)

**Problem Summary:** Given `array`, decode the statement into
mathematics and determine `can repeatedly remove smaller when diff<=1`.

### CF 1399A --- Remove Smallest (Sorting / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `max adjacent diff<=1`. The key Phase-1 move is to recognize
    **sort + check**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
array
target: can repeatedly remove smaller when diff<=1
```

-   **Define Variables:**

``` text
Given:
array

Mathematical objects:
sorted adjacent gaps encode feasibility

Target:
can repeatedly remove smaller when diff<=1
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
max adjacent diff<=1

Question
        ↓
can repeatedly remove smaller when diff<=1
```

-   **Mathematical Model:**

``` text
max adjacent diff<=1
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
sorted adjacent gaps encode feasibility
  ↓ write condition
max adjacent diff<=1
  ↓ simplify / recognize
sort + check
  ↓
O(nlogn)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `sorted adjacent gaps encode feasibility`.
2.  Express the requirement as `max adjacent diff<=1` and simplify it to
    **sort + check**.
3.  Apply `O(nlogn)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: array

Decode:         Ignore story nouns.
                Keep: sorted adjacent gaps encode feasibility

Model:          max adjacent diff<=1

Collapse:       sort + check

Algorithm:      O(nlogn)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    sorted adjacent gaps encode feasibility

MODEL:
    max adjacent diff<=1

SIMPLIFY / TRANSFORM:
    sort + check

APPLY:
    O(nlogn)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(nlogn)**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `max adjacent diff<=1`, think **sort + check** before
    implementation.

# Pattern 3 --- Bounds / Inequalities / Min-Max

## Pattern Overview

Translate 'at least', 'at most', 'minimum possible', and range wording
into inequalities. Look for a lower/upper bound and whether it is
achievable.

``` text
Statement wording
      ↓
remove domain story
      ↓
mathematical condition
      ↓
recognize: Bounds / Inequalities / Min-Max
```

Problem Link: [CF 1690A --- Print a
Pedestal](https://codeforces.com/problemset/problem/1690/A)

**Problem Summary:** Given `n`, decode the statement into mathematics
and determine
`split n into 3 positive distinct heights with middle ordering`.

### CF 1690A --- Print a Pedestal (Construction / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `near n/3 then adjust`. The key Phase-1 move is to recognize
    **construct around thirds**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
n
target: split n into 3 positive distinct heights with middle ordering
```

-   **Define Variables:**

``` text
Given:
n

Mathematical objects:
x<y<z and sum n

Target:
split n into 3 positive distinct heights with middle ordering
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
near n/3 then adjust

Question
        ↓
split n into 3 positive distinct heights with middle ordering
```

-   **Mathematical Model:**

``` text
near n/3 then adjust
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
x<y<z and sum n
  ↓ write condition
near n/3 then adjust
  ↓ simplify / recognize
construct around thirds
  ↓
formula/cases
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `x<y<z and sum n`.
2.  Express the requirement as `near n/3 then adjust` and simplify it to
    **construct around thirds**.
3.  Apply `formula/cases` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: n

Decode:         Ignore story nouns.
                Keep: x<y<z and sum n

Model:          near n/3 then adjust

Collapse:       construct around thirds

Algorithm:      formula/cases

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    x<y<z and sum n

MODEL:
    near n/3 then adjust

SIMPLIFY / TRANSFORM:
    construct around thirds

APPLY:
    formula/cases

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **formula/cases**; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `near n/3 then adjust`, think **construct around thirds** before
    implementation.

Problem Link: [CF 1676A ---
Lucky?](https://codeforces.com/problemset/problem/1676/A)

**Problem Summary:** Given `6-digit string`, decode the statement into
mathematics and determine `first 3 digit sum equals last 3`.

### CF 1676A --- Lucky? (Equation / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to `S1=S2`.
    The key Phase-1 move is to recognize **direct compare**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
6-digit string
target: first 3 digit sum equals last 3
```

-   **Define Variables:**

``` text
Given:
6-digit string

Mathematical objects:
S1,S2

Target:
first 3 digit sum equals last 3
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
S1=S2

Question
        ↓
first 3 digit sum equals last 3
```

-   **Mathematical Model:**

``` text
S1=S2
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
S1,S2
  ↓ write condition
S1=S2
  ↓ simplify / recognize
direct compare
  ↓
O(1)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `S1,S2`.
2.  Express the requirement as `S1=S2` and simplify it to **direct
    compare**.
3.  Apply `O(1)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: 6-digit string

Decode:         Ignore story nouns.
                Keep: S1,S2

Model:          S1=S2

Collapse:       direct compare

Algorithm:      O(1)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    S1,S2

MODEL:
    S1=S2

SIMPLIFY / TRANSFORM:
    direct compare

APPLY:
    O(1)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(1)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to `S1=S2`,
    think **direct compare** before implementation.

Problem Link: [CF 1742B ---
Increasing](https://codeforces.com/problemset/problem/1742/B)

**Problem Summary:** Given `array`, decode the statement into
mathematics and determine `can permute to strictly increasing`.

### CF 1742B --- Increasing (Distinctness / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to `freq<=1`.
    The key Phase-1 move is to recognize **set size=n**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
array
target: can permute to strictly increasing
```

-   **Define Variables:**

``` text
Given:
array

Mathematical objects:
strictly increasing permutation iff all distinct

Target:
can permute to strictly increasing
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
freq<=1

Question
        ↓
can permute to strictly increasing
```

-   **Mathematical Model:**

``` text
freq<=1
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
strictly increasing permutation iff all distinct
  ↓ write condition
freq<=1
  ↓ simplify / recognize
set size=n
  ↓
set
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `strictly increasing permutation iff all distinct`.
2.  Express the requirement as `freq<=1` and simplify it to **set
    size=n**.
3.  Apply `set` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: array

Decode:         Ignore story nouns.
                Keep: strictly increasing permutation iff all distinct

Model:          freq<=1

Collapse:       set size=n

Algorithm:      set

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    strictly increasing permutation iff all distinct

MODEL:
    freq<=1

SIMPLIFY / TRANSFORM:
    set size=n

APPLY:
    set

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **set**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `freq<=1`, think **set size=n** before implementation.

Problem Link: [CF 1791A --- Codeforces
Checking](https://codeforces.com/problemset/problem/1791/A)

**Problem Summary:** Given `char c`, decode the statement into
mathematics and determine `whether c belongs to 'codeforces'`.

### CF 1791A --- Codeforces Checking (Membership / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `find char`. The key Phase-1 move is to recognize **membership**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
char c
target: whether c belongs to 'codeforces'
```

-   **Define Variables:**

``` text
Given:
char c

Mathematical objects:
c ∈ fixed set

Target:
whether c belongs to 'codeforces'
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
find char

Question
        ↓
whether c belongs to 'codeforces'
```

-   **Mathematical Model:**

``` text
find char
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
c ∈ fixed set
  ↓ write condition
find char
  ↓ simplify / recognize
membership
  ↓
string find
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `c ∈ fixed set`.
2.  Express the requirement as `find char` and simplify it to
    **membership**.
3.  Apply `string find` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: char c

Decode:         Ignore story nouns.
                Keep: c ∈ fixed set

Model:          find char

Collapse:       membership

Algorithm:      string find

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    c ∈ fixed set

MODEL:
    find char

SIMPLIFY / TRANSFORM:
    membership

APPLY:
    string find

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **string find**; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `find char`, think **membership** before implementation.

Problem Link: [CF 1829A --- Love
Story](https://codeforces.com/problemset/problem/1829/A)

**Problem Summary:** Given `string`, decode the statement into
mathematics and determine `positions differing from 'codeforces'`.

### CF 1829A --- Love Story (Hamming Distance / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `sum indicators`. The key Phase-1 move is to recognize **Hamming
    distance**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
string
target: positions differing from 'codeforces'
```

-   **Define Variables:**

``` text
Given:
string

Mathematical objects:
indicator [s[i]!=t[i]]

Target:
positions differing from 'codeforces'
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
sum indicators

Question
        ↓
positions differing from 'codeforces'
```

-   **Mathematical Model:**

``` text
sum indicators
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
indicator [s[i]!=t[i]]
  ↓ write condition
sum indicators
  ↓ simplify / recognize
Hamming distance
  ↓
10 checks
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `indicator [s[i]!=t[i]]`.
2.  Express the requirement as `sum indicators` and simplify it to
    **Hamming distance**.
3.  Apply `10 checks` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: string

Decode:         Ignore story nouns.
                Keep: indicator [s[i]!=t[i]]

Model:          sum indicators

Collapse:       Hamming distance

Algorithm:      10 checks

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    indicator [s[i]!=t[i]]

MODEL:
    sum indicators

SIMPLIFY / TRANSFORM:
    Hamming distance

APPLY:
    10 checks

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **10 checks**; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `sum indicators`, think **Hamming distance** before implementation.

Problem Link: [CF 1873A --- Short
Sort](https://codeforces.com/problemset/problem/1873/A)

**Problem Summary:** Given `3-char string`, decode the statement into
mathematics and determine `can sort with <=1 swap`.

### CF 1873A --- Short Sort (Permutation / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `mismatch count 0 or 2`. The key Phase-1 move is to recognize
    **compare permutations**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
3-char string
target: can sort with <=1 swap
```

-   **Define Variables:**

``` text
Given:
3-char string

Mathematical objects:
target='abc'

Target:
can sort with <=1 swap
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
mismatch count 0 or 2

Question
        ↓
can sort with <=1 swap
```

-   **Mathematical Model:**

``` text
mismatch count 0 or 2
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
target='abc'
  ↓ write condition
mismatch count 0 or 2
  ↓ simplify / recognize
compare permutations
  ↓
direct
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `target='abc'`.
2.  Express the requirement as `mismatch count 0 or 2` and simplify it
    to **compare permutations**.
3.  Apply `direct` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: 3-char string

Decode:         Ignore story nouns.
                Keep: target='abc'

Model:          mismatch count 0 or 2

Collapse:       compare permutations

Algorithm:      direct

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    target='abc'

MODEL:
    mismatch count 0 or 2

SIMPLIFY / TRANSFORM:
    compare permutations

APPLY:
    direct

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **direct**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `mismatch count 0 or 2`, think **compare permutations** before
    implementation.

Problem Link: [CF 1729A --- Two
Elevators](https://codeforces.com/problemset/problem/1729/A)

**Problem Summary:** Given `a,b,c`, decode the statement into
mathematics and determine `which elevator reaches floor1 sooner`.

### CF 1729A --- Two Elevators (Distance / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `compare times`. The key Phase-1 move is to recognize **min
    comparison**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
a,b,c
target: which elevator reaches floor1 sooner
```

-   **Define Variables:**

``` text
Given:
a,b,c

Mathematical objects:
t1=a-1, t2=|b-c|+c-1

Target:
which elevator reaches floor1 sooner
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
compare times

Question
        ↓
which elevator reaches floor1 sooner
```

-   **Mathematical Model:**

``` text
compare times
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
t1=a-1, t2=|b-c|+c-1
  ↓ write condition
compare times
  ↓ simplify / recognize
min comparison
  ↓
O(1)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `t1=a-1, t2=|b-c|+c-1`.
2.  Express the requirement as `compare times` and simplify it to **min
    comparison**.
3.  Apply `O(1)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: a,b,c

Decode:         Ignore story nouns.
                Keep: t1=a-1, t2=|b-c|+c-1

Model:          compare times

Collapse:       min comparison

Algorithm:      O(1)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    t1=a-1, t2=|b-c|+c-1

MODEL:
    compare times

SIMPLIFY / TRANSFORM:
    min comparison

APPLY:
    O(1)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(1)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `compare times`, think **min comparison** before implementation.

Problem Link: [CF 1805A --- We Need the
Zero](https://codeforces.com/problemset/problem/1805/A)

**Problem Summary:** Given `array`, decode the statement into
mathematics and determine `find x making xor transformed zero`.

### CF 1805A --- We Need the Zero (XOR/Bounds / Codeforces / 900)

-   **Core Invariant / Key Insight:** The story collapses to
    `parity of n controls x contribution`. The key Phase-1 move is to
    recognize **derive xor equation**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
array
target: find x making xor transformed zero
```

-   **Define Variables:**

``` text
Given:
array

Mathematical objects:
xor(a_i xor x)

Target:
find x making xor transformed zero
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
parity of n controls x contribution

Question
        ↓
find x making xor transformed zero
```

-   **Mathematical Model:**

``` text
parity of n controls x contribution
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
xor(a_i xor x)
  ↓ write condition
parity of n controls x contribution
  ↓ simplify / recognize
derive xor equation
  ↓
xor all
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `xor(a_i xor x)`.
2.  Express the requirement as `parity of n controls x contribution` and
    simplify it to **derive xor equation**.
3.  Apply `xor all` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: array

Decode:         Ignore story nouns.
                Keep: xor(a_i xor x)

Model:          parity of n controls x contribution

Collapse:       derive xor equation

Algorithm:      xor all

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    xor(a_i xor x)

MODEL:
    parity of n controls x contribution

SIMPLIFY / TRANSFORM:
    derive xor equation

APPLY:
    xor all

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **xor all**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `parity of n controls x contribution`, think **derive xor equation**
    before implementation.

Problem Link: [CF 1858A ---
Buttons](https://codeforces.com/problemset/problem/1858/A)

**Problem Summary:** Given `a,b,c`, decode the statement into
mathematics and determine `winner with shared buttons`.

### CF 1858A --- Buttons (Game/Counting / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `parity of c decides who gets extra`. The key Phase-1 move is to
    recognize **compare effective counts**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
a,b,c
target: winner with shared buttons
```

-   **Define Variables:**

``` text
Given:
a,b,c

Mathematical objects:
shared moves alternate

Target:
winner with shared buttons
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
parity of c decides who gets extra

Question
        ↓
winner with shared buttons
```

-   **Mathematical Model:**

``` text
parity of c decides who gets extra
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
shared moves alternate
  ↓ write condition
parity of c decides who gets extra
  ↓ simplify / recognize
compare effective counts
  ↓
casework
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `shared moves alternate`.
2.  Express the requirement as `parity of c decides who gets extra` and
    simplify it to **compare effective counts**.
3.  Apply `casework` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: a,b,c

Decode:         Ignore story nouns.
                Keep: shared moves alternate

Model:          parity of c decides who gets extra

Collapse:       compare effective counts

Algorithm:      casework

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    shared moves alternate

MODEL:
    parity of c decides who gets extra

SIMPLIFY / TRANSFORM:
    compare effective counts

APPLY:
    casework

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **casework**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `parity of c decides who gets extra`, think **compare effective
    counts** before implementation.

Problem Link: [CF 1899A --- Game with
Integers](https://codeforces.com/problemset/problem/1899/A)

**Problem Summary:** Given `n`, decode the statement into mathematics
and determine `winner under ±1 and divisibility by3`.

### CF 1899A --- Game with Integers (Modulo / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `n%3==0 is losing/winning condition per rules`. The key Phase-1 move
    is to recognize **reduce to residue**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
n
target: winner under ±1 and divisibility by3
```

-   **Define Variables:**

``` text
Given:
n

Mathematical objects:
positions mod3

Target:
winner under ±1 and divisibility by3
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
n%3==0 is losing/winning condition per rules

Question
        ↓
winner under ±1 and divisibility by3
```

-   **Mathematical Model:**

``` text
n%3==0 is losing/winning condition per rules
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
positions mod3
  ↓ write condition
n%3==0 is losing/winning condition per rules
  ↓ simplify / recognize
reduce to residue
  ↓
O(1)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `positions mod3`.
2.  Express the requirement as
    `n%3==0 is losing/winning condition per rules` and simplify it to
    **reduce to residue**.
3.  Apply `O(1)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: n

Decode:         Ignore story nouns.
                Keep: positions mod3

Model:          n%3==0 is losing/winning condition per rules

Collapse:       reduce to residue

Algorithm:      O(1)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    positions mod3

MODEL:
    n%3==0 is losing/winning condition per rules

SIMPLIFY / TRANSFORM:
    reduce to residue

APPLY:
    O(1)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(1)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `n%3==0 is losing/winning condition per rules`, think **reduce to
    residue** before implementation.

# Pattern 4 --- Parity Modeling

## Pattern Overview

Replace values by `x % 2` whenever only odd/even behavior matters. For
sums, the parity is determined by the number of odd addends.

``` text
Statement wording
      ↓
remove domain story
      ↓
mathematical condition
      ↓
recognize: Parity Modeling
```

Problem Link: [CF 4A ---
Watermelon](https://codeforces.com/problemset/problem/4/A)

**Problem Summary:** Given `w`, decode the statement into mathematics
and determine `split into two positive even parts`.

### CF 4A --- Watermelon (Parity / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `w even and w>2`. The key Phase-1 move is to recognize **parity +
    positivity**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
w
target: split into two positive even parts
```

-   **Define Variables:**

``` text
Given:
w

Mathematical objects:
w=a+b, a,b even >=2

Target:
split into two positive even parts
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
w even and w>2

Question
        ↓
split into two positive even parts
```

-   **Mathematical Model:**

``` text
w even and w>2
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
w=a+b, a,b even >=2
  ↓ write condition
w even and w>2
  ↓ simplify / recognize
parity + positivity
  ↓
w%2==0&&w>2
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `w=a+b, a,b even >=2`.
2.  Express the requirement as `w even and w>2` and simplify it to
    **parity + positivity**.
3.  Apply `w%2==0&&w>2` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: w

Decode:         Ignore story nouns.
                Keep: w=a+b, a,b even >=2

Model:          w even and w>2

Collapse:       parity + positivity

Algorithm:      w%2==0&&w>2

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    w=a+b, a,b even >=2

MODEL:
    w even and w>2

SIMPLIFY / TRANSFORM:
    parity + positivity

APPLY:
    w%2==0&&w>2

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **w%2==0&&w\>2**; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `w even and w>2`, think **parity + positivity** before
    implementation.

Problem Link: [CF 1296A --- Array with Odd
Sum](https://codeforces.com/problemset/problem/1296/A)

**Problem Summary:** Given `array`, decode the statement into
mathematics and determine `whether required odd-sum selection exists`.

### CF 1296A --- Array with Odd Sum (Parity / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `reduce values to parity`. The key Phase-1 move is to recognize
    **count odd/even**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
array
target: whether required odd-sum selection exists
```

-   **Define Variables:**

``` text
Given:
array

Mathematical objects:
sum odd iff odd count odd

Target:
whether required odd-sum selection exists
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
reduce values to parity

Question
        ↓
whether required odd-sum selection exists
```

-   **Mathematical Model:**

``` text
reduce values to parity
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
sum odd iff odd count odd
  ↓ write condition
reduce values to parity
  ↓ simplify / recognize
count odd/even
  ↓
casework
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `sum odd iff odd count odd`.
2.  Express the requirement as `reduce values to parity` and simplify it
    to **count odd/even**.
3.  Apply `casework` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: array

Decode:         Ignore story nouns.
                Keep: sum odd iff odd count odd

Model:          reduce values to parity

Collapse:       count odd/even

Algorithm:      casework

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    sum odd iff odd count odd

MODEL:
    reduce values to parity

SIMPLIFY / TRANSFORM:
    count odd/even

APPLY:
    casework

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **casework**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `reduce values to parity`, think **count odd/even** before
    implementation.

Problem Link: [CF 1857A --- Array
Coloring](https://codeforces.com/problemset/problem/1857/A)

**Problem Summary:** Given `array`, decode the statement into
mathematics and determine
`whether can split into two groups with equal parity sums`.

### CF 1857A --- Array Coloring (Parity / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to `sum%2=0`.
    The key Phase-1 move is to recognize **parity invariant**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
array
target: whether can split into two groups with equal parity sums
```

-   **Define Variables:**

``` text
Given:
array

Mathematical objects:
total sum must be even

Target:
whether can split into two groups with equal parity sums
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
sum%2=0

Question
        ↓
whether can split into two groups with equal parity sums
```

-   **Mathematical Model:**

``` text
sum%2=0
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
total sum must be even
  ↓ write condition
sum%2=0
  ↓ simplify / recognize
parity invariant
  ↓
sum check
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `total sum must be even`.
2.  Express the requirement as `sum%2=0` and simplify it to **parity
    invariant**.
3.  Apply `sum check` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: array

Decode:         Ignore story nouns.
                Keep: total sum must be even

Model:          sum%2=0

Collapse:       parity invariant

Algorithm:      sum check

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    total sum must be even

MODEL:
    sum%2=0

SIMPLIFY / TRANSFORM:
    parity invariant

APPLY:
    sum check

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **sum check**; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `sum%2=0`, think **parity invariant** before implementation.

Problem Link: [CF 1834A --- Unit
Array](https://codeforces.com/problemset/problem/1834/A)

**Problem Summary:** Given `±1 array`, decode the statement into
mathematics and determine
`minimum flips to satisfy sum>=0 and product=1`.

### CF 1834A --- Unit Array (Parity/Greedy / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `fix sum then parity`. The key Phase-1 move is to recognize **count
    negatives**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
±1 array
target: minimum flips to satisfy sum>=0 and product=1
```

-   **Define Variables:**

``` text
Given:
±1 array

Mathematical objects:
product depends on #(-1) parity

Target:
minimum flips to satisfy sum>=0 and product=1
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
fix sum then parity

Question
        ↓
minimum flips to satisfy sum>=0 and product=1
```

-   **Mathematical Model:**

``` text
fix sum then parity
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
product depends on #(-1) parity
  ↓ write condition
fix sum then parity
  ↓ simplify / recognize
count negatives
  ↓
formula/loop
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `product depends on #(-1) parity`.
2.  Express the requirement as `fix sum then parity` and simplify it to
    **count negatives**.
3.  Apply `formula/loop` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: ±1 array

Decode:         Ignore story nouns.
                Keep: product depends on #(-1) parity

Model:          fix sum then parity

Collapse:       count negatives

Algorithm:      formula/loop

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    product depends on #(-1) parity

MODEL:
    fix sum then parity

SIMPLIFY / TRANSFORM:
    count negatives

APPLY:
    formula/loop

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **formula/loop**; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `fix sum then parity`, think **count negatives** before
    implementation.

Problem Link: [CF 1367B --- Even
Array](https://codeforces.com/problemset/problem/1367/B)

**Problem Summary:** Given `array`, decode the statement into
mathematics and determine `minimum swaps so a[i]%2=i%2`.

### CF 1367B --- Even Array (Parity / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `badEven=badOdd`. The key Phase-1 move is to recognize **answer
    mismatches/2**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
array
target: minimum swaps so a[i]%2=i%2
```

-   **Define Variables:**

``` text
Given:
array

Mathematical objects:
mismatches of two types must balance

Target:
minimum swaps so a[i]%2=i%2
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
badEven=badOdd

Question
        ↓
minimum swaps so a[i]%2=i%2
```

-   **Mathematical Model:**

``` text
badEven=badOdd
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
mismatches of two types must balance
  ↓ write condition
badEven=badOdd
  ↓ simplify / recognize
answer mismatches/2
  ↓
count
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `mismatches of two types must balance`.
2.  Express the requirement as `badEven=badOdd` and simplify it to
    **answer mismatches/2**.
3.  Apply `count` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: array

Decode:         Ignore story nouns.
                Keep: mismatches of two types must balance

Model:          badEven=badOdd

Collapse:       answer mismatches/2

Algorithm:      count

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    mismatches of two types must balance

MODEL:
    badEven=badOdd

SIMPLIFY / TRANSFORM:
    answer mismatches/2

APPLY:
    count

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **count**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `badEven=badOdd`, think **answer mismatches/2** before
    implementation.

Problem Link: [CF 1475A --- Odd
Divisor](https://codeforces.com/problemset/problem/1475/A)

**Problem Summary:** Given `n`, decode the statement into mathematics
and determine `has odd divisor >1`.

### CF 1475A --- Odd Divisor (Number Theory / Codeforces / 900)

-   **Core Invariant / Key Insight:** The story collapses to `m>1`. The
    key Phase-1 move is to recognize **not power of two**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
n
target: has odd divisor >1
```

-   **Define Variables:**

``` text
Given:
n

Mathematical objects:
n=2^k*m odd

Target:
has odd divisor >1
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
m>1

Question
        ↓
has odd divisor >1
```

-   **Mathematical Model:**

``` text
m>1
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
n=2^k*m odd
  ↓ write condition
m>1
  ↓ simplify / recognize
not power of two
  ↓
strip twos
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `n=2^k*m odd`.
2.  Express the requirement as `m>1` and simplify it to **not power of
    two**.
3.  Apply `strip twos` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: n

Decode:         Ignore story nouns.
                Keep: n=2^k*m odd

Model:          m>1

Collapse:       not power of two

Algorithm:      strip twos

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    n=2^k*m odd

MODEL:
    m>1

SIMPLIFY / TRANSFORM:
    not power of two

APPLY:
    strip twos

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **strip twos**; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to `m>1`,
    think **not power of two** before implementation.

Problem Link: [CF 1669C --- Odd/Even
Increments](https://codeforces.com/problemset/problem/1669/C)

**Problem Summary:** Given `array`, decode the statement into
mathematics and determine
`can equalize via parity-constrained increments`.

### CF 1669C --- Odd/Even Increments (Parity / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `check parity consistency`. The key Phase-1 move is to recognize
    **parity only**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
array
target: can equalize via parity-constrained increments
```

-   **Define Variables:**

``` text
Given:
array

Mathematical objects:
all elements need same parity class relation

Target:
can equalize via parity-constrained increments
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
check parity consistency

Question
        ↓
can equalize via parity-constrained increments
```

-   **Mathematical Model:**

``` text
check parity consistency
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
all elements need same parity class relation
  ↓ write condition
check parity consistency
  ↓ simplify / recognize
parity only
  ↓
scan
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `all elements need same parity class relation`.
2.  Express the requirement as `check parity consistency` and simplify
    it to **parity only**.
3.  Apply `scan` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: array

Decode:         Ignore story nouns.
                Keep: all elements need same parity class relation

Model:          check parity consistency

Collapse:       parity only

Algorithm:      scan

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    all elements need same parity class relation

MODEL:
    check parity consistency

SIMPLIFY / TRANSFORM:
    parity only

APPLY:
    scan

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **scan**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `check parity consistency`, think **parity only** before
    implementation.

Problem Link: [CF 1624A --- Plus One on the
Subset](https://codeforces.com/problemset/problem/1624/A)

**Problem Summary:** Given `array`, decode the statement into
mathematics and determine
`minimum operations to equalize by incrementing subset`.

### CF 1624A --- Plus One on the Subset (Difference / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `answer=max-min`. The key Phase-1 move is to recognize **range
    width**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
array
target: minimum operations to equalize by incrementing subset
```

-   **Define Variables:**

``` text
Given:
array

Mathematical objects:
raise to max

Target:
minimum operations to equalize by incrementing subset
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
answer=max-min

Question
        ↓
minimum operations to equalize by incrementing subset
```

-   **Mathematical Model:**

``` text
answer=max-min
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
raise to max
  ↓ write condition
answer=max-min
  ↓ simplify / recognize
range width
  ↓
min/max
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `raise to max`.
2.  Express the requirement as `answer=max-min` and simplify it to
    **range width**.
3.  Apply `min/max` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: array

Decode:         Ignore story nouns.
                Keep: raise to max

Model:          answer=max-min

Collapse:       range width

Algorithm:      min/max

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    raise to max

MODEL:
    answer=max-min

SIMPLIFY / TRANSFORM:
    range width

APPLY:
    min/max

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **min/max**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `answer=max-min`, think **range width** before implementation.

Problem Link: [CF 1788A --- One and
Two](https://codeforces.com/problemset/problem/1788/A)

**Problem Summary:** Given `1/2 array`, decode the statement into
mathematics and determine `split so products equal`.

### CF 1788A --- One and Two (Product/Parity / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `total twos even`. The key Phase-1 move is to recognize **find half
    twos**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
1/2 array
target: split so products equal
```

-   **Define Variables:**

``` text
Given:
1/2 array

Mathematical objects:
equal #twos on both sides

Target:
split so products equal
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
total twos even

Question
        ↓
split so products equal
```

-   **Mathematical Model:**

``` text
total twos even
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
equal #twos on both sides
  ↓ write condition
total twos even
  ↓ simplify / recognize
find half twos
  ↓
count
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `equal #twos on both sides`.
2.  Express the requirement as `total twos even` and simplify it to
    **find half twos**.
3.  Apply `count` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: 1/2 array

Decode:         Ignore story nouns.
                Keep: equal #twos on both sides

Model:          total twos even

Collapse:       find half twos

Algorithm:      count

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    equal #twos on both sides

MODEL:
    total twos even

SIMPLIFY / TRANSFORM:
    find half twos

APPLY:
    count

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **count**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `total twos even`, think **find half twos** before implementation.

Problem Link: [CF 1845A --- Forbidden
Integer](https://codeforces.com/problemset/problem/1845/A)

**Problem Summary:** Given `n,k,x`, decode the statement into
mathematics and determine `represent n as sum of 1..k excluding x`.

### CF 1845A --- Forbidden Integer (Constructive / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `cases x!=1, else 2/3`. The key Phase-1 move is to recognize
    **construct feasibility**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
n,k,x
target: represent n as sum of 1..k excluding x
```

-   **Define Variables:**

``` text
Given:
n,k,x

Mathematical objects:
choose repeated small allowed values

Target:
represent n as sum of 1..k excluding x
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
cases x!=1, else 2/3

Question
        ↓
represent n as sum of 1..k excluding x
```

-   **Mathematical Model:**

``` text
cases x!=1, else 2/3
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
choose repeated small allowed values
  ↓ write condition
cases x!=1, else 2/3
  ↓ simplify / recognize
construct feasibility
  ↓
casework
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `choose repeated small allowed values`.
2.  Express the requirement as `cases x!=1, else 2/3` and simplify it to
    **construct feasibility**.
3.  Apply `casework` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: n,k,x

Decode:         Ignore story nouns.
                Keep: choose repeated small allowed values

Model:          cases x!=1, else 2/3

Collapse:       construct feasibility

Algorithm:      casework

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    choose repeated small allowed values

MODEL:
    cases x!=1, else 2/3

SIMPLIFY / TRANSFORM:
    construct feasibility

APPLY:
    casework

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **casework**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `cases x!=1, else 2/3`, think **construct feasibility** before
    implementation.

# Pattern 5 --- Divisibility / GCD / LCM

## Pattern Overview

Translate `b divides a` into `a % b = 0` or `a=bk`. For common
divisors/multiples, test GCD/LCM or factor structure.

``` text
Statement wording
      ↓
remove domain story
      ↓
mathematical condition
      ↓
recognize: Divisibility / GCD / LCM
```

Problem Link: [CF 1328A --- Divisibility
Problem](https://codeforces.com/problemset/problem/1328/A)

**Problem Summary:** Given `a,b`, decode the statement into mathematics
and determine `minimum add to make a divisible by b`.

### CF 1328A --- Divisibility Problem (Modulo / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `x=(b-a%b)%b`. The key Phase-1 move is to recognize **remainder
    complement**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
a,b
target: minimum add to make a divisible by b
```

-   **Define Variables:**

``` text
Given:
a,b

Mathematical objects:
need a+x ≡0 mod b

Target:
minimum add to make a divisible by b
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
x=(b-a%b)%b

Question
        ↓
minimum add to make a divisible by b
```

-   **Mathematical Model:**

``` text
x=(b-a%b)%b
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
need a+x ≡0 mod b
  ↓ write condition
x=(b-a%b)%b
  ↓ simplify / recognize
remainder complement
  ↓
O(1)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `need a+x ≡0 mod b`.
2.  Express the requirement as `x=(b-a%b)%b` and simplify it to
    **remainder complement**.
3.  Apply `O(1)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: a,b

Decode:         Ignore story nouns.
                Keep: need a+x ≡0 mod b

Model:          x=(b-a%b)%b

Collapse:       remainder complement

Algorithm:      O(1)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    need a+x ≡0 mod b

MODEL:
    x=(b-a%b)%b

SIMPLIFY / TRANSFORM:
    remainder complement

APPLY:
    O(1)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(1)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `x=(b-a%b)%b`, think **remainder complement** before implementation.

Problem Link: [CF 1343A ---
Candies](https://codeforces.com/problemset/problem/1343/A)

**Problem Summary:** Given `n`, decode the statement into mathematics
and determine `find x where n=x(2^k-1)`.

### CF 1343A --- Candies (Geometric/Divisibility / Codeforces / 900)

-   **Core Invariant / Key Insight:** The story collapses to
    `x=n/(2^k-1) if divisible`. The key Phase-1 move is to recognize
    **test k**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
n
target: find x where n=x(2^k-1)
```

-   **Define Variables:**

``` text
Given:
n

Mathematical objects:
geometric sum factor

Target:
find x where n=x(2^k-1)
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
x=n/(2^k-1) if divisible

Question
        ↓
find x where n=x(2^k-1)
```

-   **Mathematical Model:**

``` text
x=n/(2^k-1) if divisible
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
geometric sum factor
  ↓ write condition
x=n/(2^k-1) if divisible
  ↓ simplify / recognize
test k
  ↓
loop
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `geometric sum factor`.
2.  Express the requirement as `x=n/(2^k-1) if divisible` and simplify
    it to **test k**.
3.  Apply `loop` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: n

Decode:         Ignore story nouns.
                Keep: geometric sum factor

Model:          x=n/(2^k-1) if divisible

Collapse:       test k

Algorithm:      loop

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    geometric sum factor

MODEL:
    x=n/(2^k-1) if divisible

SIMPLIFY / TRANSFORM:
    test k

APPLY:
    loop

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **loop**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `x=n/(2^k-1) if divisible`, think **test k** before implementation.

Problem Link: [CF 1370A --- Maximum
GCD](https://codeforces.com/problemset/problem/1370/A)

**Problem Summary:** Given `n`, decode the statement into mathematics
and determine `maximize gcd(a,b), a+b=n`.

### CF 1370A --- Maximum GCD (GCD / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `choose floor(n/2)`. The key Phase-1 move is to recognize **tight
    bound**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
n
target: maximize gcd(a,b), a+b=n
```

-   **Define Variables:**

``` text
Given:
n

Mathematical objects:
gcd<=floor(n/2)

Target:
maximize gcd(a,b), a+b=n
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
choose floor(n/2)

Question
        ↓
maximize gcd(a,b), a+b=n
```

-   **Mathematical Model:**

``` text
choose floor(n/2)
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
gcd<=floor(n/2)
  ↓ write condition
choose floor(n/2)
  ↓ simplify / recognize
tight bound
  ↓
n/2
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `gcd<=floor(n/2)`.
2.  Express the requirement as `choose floor(n/2)` and simplify it to
    **tight bound**.
3.  Apply `n/2` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: n

Decode:         Ignore story nouns.
                Keep: gcd<=floor(n/2)

Model:          choose floor(n/2)

Collapse:       tight bound

Algorithm:      n/2

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    gcd<=floor(n/2)

MODEL:
    choose floor(n/2)

SIMPLIFY / TRANSFORM:
    tight bound

APPLY:
    n/2

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **n/2**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `choose floor(n/2)`, think **tight bound** before implementation.

Problem Link: [CF 1829C --- Mr. Perfectly
Fine](https://codeforces.com/problemset/problem/1829/C)

**Problem Summary:** Given `items`, decode the statement into
mathematics and determine `minimum time covering skills 1 and2`.

### CF 1829C --- Mr. Perfectly Fine (Min/Bitmask / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `min(cost11,cost01+cost10)`. The key Phase-1 move is to recognize
    **coverage states**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
items
target: minimum time covering skills 1 and2
```

-   **Define Variables:**

``` text
Given:
items

Mathematical objects:
skill masks 01,10,11

Target:
minimum time covering skills 1 and2
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
min(cost11,cost01+cost10)

Question
        ↓
minimum time covering skills 1 and2
```

-   **Mathematical Model:**

``` text
min(cost11,cost01+cost10)
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
skill masks 01,10,11
  ↓ write condition
min(cost11,cost01+cost10)
  ↓ simplify / recognize
coverage states
  ↓
track minima
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `skill masks 01,10,11`.
2.  Express the requirement as `min(cost11,cost01+cost10)` and simplify
    it to **coverage states**.
3.  Apply `track minima` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: items

Decode:         Ignore story nouns.
                Keep: skill masks 01,10,11

Model:          min(cost11,cost01+cost10)

Collapse:       coverage states

Algorithm:      track minima

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    skill masks 01,10,11

MODEL:
    min(cost11,cost01+cost10)

SIMPLIFY / TRANSFORM:
    coverage states

APPLY:
    track minima

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **track minima**; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `min(cost11,cost01+cost10)`, think **coverage states** before
    implementation.

Problem Link: [CF 1618A --- Polycarp and Sums of
Subsequences](https://codeforces.com/problemset/problem/1618/A)

**Problem Summary:** Given `7 subset sums`, decode the statement into
mathematics and determine `recover a,b,c`.

### CF 1618A --- Polycarp and Sums of Subsequences (Algebra / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `c=largest-a-b`. The key Phase-1 move is to recognize **sorted
    sums**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
7 subset sums
target: recover a,b,c
```

-   **Define Variables:**

``` text
Given:
7 subset sums

Mathematical objects:
smallest=a,b and total largest=a+b+c

Target:
recover a,b,c
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
c=largest-a-b

Question
        ↓
recover a,b,c
```

-   **Mathematical Model:**

``` text
c=largest-a-b
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
smallest=a,b and total largest=a+b+c
  ↓ write condition
c=largest-a-b
  ↓ simplify / recognize
sorted sums
  ↓
formula
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `smallest=a,b and total largest=a+b+c`.
2.  Express the requirement as `c=largest-a-b` and simplify it to
    **sorted sums**.
3.  Apply `formula` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: 7 subset sums

Decode:         Ignore story nouns.
                Keep: smallest=a,b and total largest=a+b+c

Model:          c=largest-a-b

Collapse:       sorted sums

Algorithm:      formula

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    smallest=a,b and total largest=a+b+c

MODEL:
    c=largest-a-b

SIMPLIFY / TRANSFORM:
    sorted sums

APPLY:
    formula

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **formula**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `c=largest-a-b`, think **sorted sums** before implementation.

Problem Link: [CF 160A ---
Twins](https://codeforces.com/problemset/problem/160/A)

**Problem Summary:** Given `coins`, decode the statement into
mathematics and determine `minimum coins with sum > remaining`.

### CF 160A --- Twins (Greedy/Sum / Codeforces / 900)

-   **Core Invariant / Key Insight:** The story collapses to
    `2*chosen>total`. The key Phase-1 move is to recognize **sort
    descending**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
coins
target: minimum coins with sum > remaining
```

-   **Define Variables:**

``` text
Given:
coins

Mathematical objects:
chosen > total-chosen

Target:
minimum coins with sum > remaining
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
2*chosen>total

Question
        ↓
minimum coins with sum > remaining
```

-   **Mathematical Model:**

``` text
2*chosen>total
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
chosen > total-chosen
  ↓ write condition
2*chosen>total
  ↓ simplify / recognize
sort descending
  ↓
prefix
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `chosen > total-chosen`.
2.  Express the requirement as `2*chosen>total` and simplify it to
    **sort descending**.
3.  Apply `prefix` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: coins

Decode:         Ignore story nouns.
                Keep: chosen > total-chosen

Model:          2*chosen>total

Collapse:       sort descending

Algorithm:      prefix

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    chosen > total-chosen

MODEL:
    2*chosen>total

SIMPLIFY / TRANSFORM:
    sort descending

APPLY:
    prefix

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **prefix**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `2*chosen>total`, think **sort descending** before implementation.

Problem Link: [CF 1475B --- New Year's
Number](https://codeforces.com/problemset/problem/1475/B)

**Problem Summary:** Given `n`, decode the statement into mathematics
and determine `n=2020a+2021b?`.

### CF 1475B --- New Year's Number (Diophantine / Codeforces / 900)

-   **Core Invariant / Key Insight:** The story collapses to
    `choose b=n%2020 then test`. The key Phase-1 move is to recognize
    **linear diophantine shortcut**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
n
target: n=2020a+2021b?
```

-   **Define Variables:**

``` text
Given:
n

Mathematical objects:
2021=2020+1

Target:
n=2020a+2021b?
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
choose b=n%2020 then test

Question
        ↓
n=2020a+2021b?
```

-   **Mathematical Model:**

``` text
choose b=n%2020 then test
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
2021=2020+1
  ↓ write condition
choose b=n%2020 then test
  ↓ simplify / recognize
linear diophantine shortcut
  ↓
condition
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `2021=2020+1`.
2.  Express the requirement as `choose b=n%2020 then test` and simplify
    it to **linear diophantine shortcut**.
3.  Apply `condition` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: n

Decode:         Ignore story nouns.
                Keep: 2021=2020+1

Model:          choose b=n%2020 then test

Collapse:       linear diophantine shortcut

Algorithm:      condition

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    2021=2020+1

MODEL:
    choose b=n%2020 then test

SIMPLIFY / TRANSFORM:
    linear diophantine shortcut

APPLY:
    condition

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **condition**; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `choose b=n%2020 then test`, think **linear diophantine shortcut**
    before implementation.

Problem Link: [CF 1593A ---
Elections](https://codeforces.com/problemset/problem/1593/A)

**Problem Summary:** Given `a,b,c`, decode the statement into
mathematics and determine `increments to become strictly largest`.

### CF 1593A --- Elections (Max/Formula / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `inc=max(0,M-x+1), except unique max`. The key Phase-1 move is to
    recognize **per candidate bound**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
a,b,c
target: increments to become strictly largest
```

-   **Define Variables:**

``` text
Given:
a,b,c

Mathematical objects:
need x+inc>max(other)

Target:
increments to become strictly largest
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
inc=max(0,M-x+1), except unique max

Question
        ↓
increments to become strictly largest
```

-   **Mathematical Model:**

``` text
inc=max(0,M-x+1), except unique max
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
need x+inc>max(other)
  ↓ write condition
inc=max(0,M-x+1), except unique max
  ↓ simplify / recognize
per candidate bound
  ↓
formula
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `need x+inc>max(other)`.
2.  Express the requirement as `inc=max(0,M-x+1), except unique max` and
    simplify it to **per candidate bound**.
3.  Apply `formula` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: a,b,c

Decode:         Ignore story nouns.
                Keep: need x+inc>max(other)

Model:          inc=max(0,M-x+1), except unique max

Collapse:       per candidate bound

Algorithm:      formula

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    need x+inc>max(other)

MODEL:
    inc=max(0,M-x+1), except unique max

SIMPLIFY / TRANSFORM:
    per candidate bound

APPLY:
    formula

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **formula**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `inc=max(0,M-x+1), except unique max`, think **per candidate bound**
    before implementation.

Problem Link: [CF 1829B --- Blank
Space](https://codeforces.com/problemset/problem/1829/B)

**Problem Summary:** Given `binary array`, decode the statement into
mathematics and determine `longest consecutive zeros`.

### CF 1829B --- Blank Space (Run Length / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `max over runs`. The key Phase-1 move is to recognize **scan**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
binary array
target: longest consecutive zeros
```

-   **Define Variables:**

``` text
Given:
binary array

Mathematical objects:
state current run

Target:
longest consecutive zeros
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
max over runs

Question
        ↓
longest consecutive zeros
```

-   **Mathematical Model:**

``` text
max over runs
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
state current run
  ↓ write condition
max over runs
  ↓ simplify / recognize
scan
  ↓
O(n)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `state current run`.
2.  Express the requirement as `max over runs` and simplify it to
    **scan**.
3.  Apply `O(n)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: binary array

Decode:         Ignore story nouns.
                Keep: state current run

Model:          max over runs

Collapse:       scan

Algorithm:      O(n)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    state current run

MODEL:
    max over runs

SIMPLIFY / TRANSFORM:
    scan

APPLY:
    O(n)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(n)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `max over runs`, think **scan** before implementation.

Problem Link: [CF 1877A --- Goals of
Victory](https://codeforces.com/problemset/problem/1877/A)

**Problem Summary:** Given `n-1 values`, decode the statement into
mathematics and determine `missing value so total sum zero`.

### CF 1877A --- Goals of Victory (Sum Invariant / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to `x=-sum`.
    The key Phase-1 move is to recognize **equation**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
n-1 values
target: missing value so total sum zero
```

-   **Define Variables:**

``` text
Given:
n-1 values

Mathematical objects:
x+sum=0

Target:
missing value so total sum zero
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
x=-sum

Question
        ↓
missing value so total sum zero
```

-   **Mathematical Model:**

``` text
x=-sum
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
x+sum=0
  ↓ write condition
x=-sum
  ↓ simplify / recognize
equation
  ↓
O(n)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `x+sum=0`.
2.  Express the requirement as `x=-sum` and simplify it to **equation**.
3.  Apply `O(n)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: n-1 values

Decode:         Ignore story nouns.
                Keep: x+sum=0

Model:          x=-sum

Collapse:       equation

Algorithm:      O(n)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    x+sum=0

MODEL:
    x=-sum

SIMPLIFY / TRANSFORM:
    equation

APPLY:
    O(n)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(n)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to `x=-sum`,
    think **equation** before implementation.

# Pattern 6 --- Modulo / Cyclic Modeling

## Pattern Overview

When behavior repeats after a fixed number of states, replace large
counts with a remainder. Normalize positions into residue classes.

``` text
Statement wording
      ↓
remove domain story
      ↓
mathematical condition
      ↓
recognize: Modulo / Cyclic Modeling
```

Problem Link: [CF 116A ---
Tram](https://codeforces.com/problemset/problem/116/A)

**Problem Summary:** Given `stops`, decode the statement into
mathematics and determine `minimum tram capacity`.

### CF 116A --- Tram (Prefix/Capacity / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `max prefix occupancy`. The key Phase-1 move is to recognize
    **running state**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
stops
target: minimum tram capacity
```

-   **Define Variables:**

``` text
Given:
stops

Mathematical objects:
current += enter-exit

Target:
minimum tram capacity
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
max prefix occupancy

Question
        ↓
minimum tram capacity
```

-   **Mathematical Model:**

``` text
max prefix occupancy
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
current += enter-exit
  ↓ write condition
max prefix occupancy
  ↓ simplify / recognize
running state
  ↓
scan
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `current += enter-exit`.
2.  Express the requirement as `max prefix occupancy` and simplify it to
    **running state**.
3.  Apply `scan` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: stops

Decode:         Ignore story nouns.
                Keep: current += enter-exit

Model:          max prefix occupancy

Collapse:       running state

Algorithm:      scan

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    current += enter-exit

MODEL:
    max prefix occupancy

SIMPLIFY / TRANSFORM:
    running state

APPLY:
    scan

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **scan**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `max prefix occupancy`, think **running state** before
    implementation.

Problem Link: [CF 266A --- Stones on the
Table](https://codeforces.com/problemset/problem/266/A)

**Problem Summary:** Given `string`, decode the statement into
mathematics and determine `minimum removals so adjacent colors differ`.

### CF 266A --- Stones on the Table (Adjacent / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `count s[i]==s[i-1]`. The key Phase-1 move is to recognize **local
    contribution**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
string
target: minimum removals so adjacent colors differ
```

-   **Define Variables:**

``` text
Given:
string

Mathematical objects:
remove one from each equal adjacency

Target:
minimum removals so adjacent colors differ
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
count s[i]==s[i-1]

Question
        ↓
minimum removals so adjacent colors differ
```

-   **Mathematical Model:**

``` text
count s[i]==s[i-1]
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
remove one from each equal adjacency
  ↓ write condition
count s[i]==s[i-1]
  ↓ simplify / recognize
local contribution
  ↓
scan
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `remove one from each equal adjacency`.
2.  Express the requirement as `count s[i]==s[i-1]` and simplify it to
    **local contribution**.
3.  Apply `scan` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: string

Decode:         Ignore story nouns.
                Keep: remove one from each equal adjacency

Model:          count s[i]==s[i-1]

Collapse:       local contribution

Algorithm:      scan

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    remove one from each equal adjacency

MODEL:
    count s[i]==s[i-1]

SIMPLIFY / TRANSFORM:
    local contribution

APPLY:
    scan

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **scan**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `count s[i]==s[i-1]`, think **local contribution** before
    implementation.

Problem Link: [CF 228A --- Is your horseshoe on the other
hoof?](https://codeforces.com/problemset/problem/228/A)

**Problem Summary:** Given `4 colors`, decode the statement into
mathematics and determine `minimum replacements for distinct`.

### CF 228A --- Is your horseshoe on the other hoof? (Distinctness / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to `set size`.
    The key Phase-1 move is to recognize **duplicates**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
4 colors
target: minimum replacements for distinct
```

-   **Define Variables:**

``` text
Given:
4 colors

Mathematical objects:
4-distinctCount

Target:
minimum replacements for distinct
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
set size

Question
        ↓
minimum replacements for distinct
```

-   **Mathematical Model:**

``` text
set size
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
4-distinctCount
  ↓ write condition
set size
  ↓ simplify / recognize
duplicates
  ↓
set
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `4-distinctCount`.
2.  Express the requirement as `set size` and simplify it to
    **duplicates**.
3.  Apply `set` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: 4 colors

Decode:         Ignore story nouns.
                Keep: 4-distinctCount

Model:          set size

Collapse:       duplicates

Algorithm:      set

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    4-distinctCount

MODEL:
    set size

SIMPLIFY / TRANSFORM:
    duplicates

APPLY:
    set

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **set**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `set size`, think **duplicates** before implementation.

Problem Link: [CF 443A --- Anton and
Letters](https://codeforces.com/problemset/problem/443/A)

**Problem Summary:** Given `formatted string`, decode the statement into
mathematics and determine `number distinct letters`.

### CF 443A --- Anton and Letters (Set / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `set cardinality`. The key Phase-1 move is to recognize **distinct
    count**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
formatted string
target: number distinct letters
```

-   **Define Variables:**

``` text
Given:
formatted string

Mathematical objects:
extract lowercase chars

Target:
number distinct letters
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
set cardinality

Question
        ↓
number distinct letters
```

-   **Mathematical Model:**

``` text
set cardinality
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
extract lowercase chars
  ↓ write condition
set cardinality
  ↓ simplify / recognize
distinct count
  ↓
set
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `extract lowercase chars`.
2.  Express the requirement as `set cardinality` and simplify it to
    **distinct count**.
3.  Apply `set` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: formatted string

Decode:         Ignore story nouns.
                Keep: extract lowercase chars

Model:          set cardinality

Collapse:       distinct count

Algorithm:      set

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    extract lowercase chars

MODEL:
    set cardinality

SIMPLIFY / TRANSFORM:
    distinct count

APPLY:
    set

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **set**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `set cardinality`, think **distinct count** before implementation.

Problem Link: [CF 59A ---
Word](https://codeforces.com/problemset/problem/59/A)

**Problem Summary:** Given `string`, decode the statement into
mathematics and determine `convert based on upper/lower majority`.

### CF 59A --- Word (Counting / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `choose case`. The key Phase-1 move is to recognize **frequency
    comparison**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
string
target: convert based on upper/lower majority
```

-   **Define Variables:**

``` text
Given:
string

Mathematical objects:
count uppercase vs lowercase

Target:
convert based on upper/lower majority
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
choose case

Question
        ↓
convert based on upper/lower majority
```

-   **Mathematical Model:**

``` text
choose case
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
count uppercase vs lowercase
  ↓ write condition
choose case
  ↓ simplify / recognize
frequency comparison
  ↓
transform
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `count uppercase vs lowercase`.
2.  Express the requirement as `choose case` and simplify it to
    **frequency comparison**.
3.  Apply `transform` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: string

Decode:         Ignore story nouns.
                Keep: count uppercase vs lowercase

Model:          choose case

Collapse:       frequency comparison

Algorithm:      transform

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    count uppercase vs lowercase

MODEL:
    choose case

SIMPLIFY / TRANSFORM:
    frequency comparison

APPLY:
    transform

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **transform**; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `choose case`, think **frequency comparison** before implementation.

Problem Link: [CF 236A --- Boy or
Girl](https://codeforces.com/problemset/problem/236/A)

**Problem Summary:** Given `username`, decode the statement into
mathematics and determine `output based on distinct char count parity`.

### CF 236A --- Boy or Girl (Set/Parity / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to `d%2`. The
    key Phase-1 move is to recognize **parity of distinct count**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
username
target: output based on distinct char count parity
```

-   **Define Variables:**

``` text
Given:
username

Mathematical objects:
d=|set(chars)|

Target:
output based on distinct char count parity
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
d%2

Question
        ↓
output based on distinct char count parity
```

-   **Mathematical Model:**

``` text
d%2
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
d=|set(chars)|
  ↓ write condition
d%2
  ↓ simplify / recognize
parity of distinct count
  ↓
set
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `d=|set(chars)|`.
2.  Express the requirement as `d%2` and simplify it to **parity of
    distinct count**.
3.  Apply `set` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: username

Decode:         Ignore story nouns.
                Keep: d=|set(chars)|

Model:          d%2

Collapse:       parity of distinct count

Algorithm:      set

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    d=|set(chars)|

MODEL:
    d%2

SIMPLIFY / TRANSFORM:
    parity of distinct count

APPLY:
    set

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **set**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to `d%2`,
    think **parity of distinct count** before implementation.

Problem Link: [CF 785A --- Anton and
Polyhedrons](https://codeforces.com/problemset/problem/785/A)

**Problem Summary:** Given `names`, decode the statement into
mathematics and determine `total faces`.

### CF 785A --- Anton and Polyhedrons (Mapping / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `sum contributions`. The key Phase-1 move is to recognize
    **lookup**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
names
target: total faces
```

-   **Define Variables:**

``` text
Given:
names

Mathematical objects:
name→constant

Target:
total faces
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
sum contributions

Question
        ↓
total faces
```

-   **Mathematical Model:**

``` text
sum contributions
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
name→constant
  ↓ write condition
sum contributions
  ↓ simplify / recognize
lookup
  ↓
map/if
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `name→constant`.
2.  Express the requirement as `sum contributions` and simplify it to
    **lookup**.
3.  Apply `map/if` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: names

Decode:         Ignore story nouns.
                Keep: name→constant

Model:          sum contributions

Collapse:       lookup

Algorithm:      map/if

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    name→constant

MODEL:
    sum contributions

SIMPLIFY / TRANSFORM:
    lookup

APPLY:
    map/if

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **map/if**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `sum contributions`, think **lookup** before implementation.

Problem Link: [CF 703A --- Mishka and
Game](https://codeforces.com/problemset/problem/703/A)

**Problem Summary:** Given `round scores`, decode the statement into
mathematics and determine `winner by more round wins`.

### CF 703A --- Mishka and Game (Comparison / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `compare counts`. The key Phase-1 move is to recognize **two
    counters**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
round scores
target: winner by more round wins
```

-   **Define Variables:**

``` text
Given:
round scores

Mathematical objects:
count a>b and a<b

Target:
winner by more round wins
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
compare counts

Question
        ↓
winner by more round wins
```

-   **Mathematical Model:**

``` text
compare counts
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
count a>b and a<b
  ↓ write condition
compare counts
  ↓ simplify / recognize
two counters
  ↓
scan
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `count a>b and a<b`.
2.  Express the requirement as `compare counts` and simplify it to **two
    counters**.
3.  Apply `scan` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: round scores

Decode:         Ignore story nouns.
                Keep: count a>b and a<b

Model:          compare counts

Collapse:       two counters

Algorithm:      scan

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    count a>b and a<b

MODEL:
    compare counts

SIMPLIFY / TRANSFORM:
    two counters

APPLY:
    scan

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **scan**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `compare counts`, think **two counters** before implementation.

Problem Link: [CF 734B --- Anton and
Digits](https://codeforces.com/problemset/problem/734/B)

**Problem Summary:** Given `counts 2,3,5,6`, decode the statement into
mathematics and determine `maximize sum using 256 and32`.

### CF 734B --- Anton and Digits (Greedy/Counting / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `x=min(2,5,6), y=min(2left,3)`. The key Phase-1 move is to recognize
    **resource allocation**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
counts 2,3,5,6
target: maximize sum using 256 and32
```

-   **Define Variables:**

``` text
Given:
counts 2,3,5,6

Mathematical objects:
make 256 first because larger

Target:
maximize sum using 256 and32
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
x=min(2,5,6), y=min(2left,3)

Question
        ↓
maximize sum using 256 and32
```

-   **Mathematical Model:**

``` text
x=min(2,5,6), y=min(2left,3)
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
make 256 first because larger
  ↓ write condition
x=min(2,5,6), y=min(2left,3)
  ↓ simplify / recognize
resource allocation
  ↓
greedy
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `make 256 first because larger`.
2.  Express the requirement as `x=min(2,5,6), y=min(2left,3)` and
    simplify it to **resource allocation**.
3.  Apply `greedy` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: counts 2,3,5,6

Decode:         Ignore story nouns.
                Keep: make 256 first because larger

Model:          x=min(2,5,6), y=min(2left,3)

Collapse:       resource allocation

Algorithm:      greedy

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    make 256 first because larger

MODEL:
    x=min(2,5,6), y=min(2left,3)

SIMPLIFY / TRANSFORM:
    resource allocation

APPLY:
    greedy

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **greedy**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `x=min(2,5,6), y=min(2left,3)`, think **resource allocation** before
    implementation.

Problem Link: [CF 1097A --- Gennady the Card
Game](https://codeforces.com/problemset/problem/1097/A)

**Problem Summary:** Given `card + five cards`, decode the statement
into mathematics and determine `whether rank or suit matches`.

### CF 1097A --- Gennady the Card Game (Matching / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `OR condition`. The key Phase-1 move is to recognize **scan**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
card + five cards
target: whether rank or suit matches
```

-   **Define Variables:**

``` text
Given:
card + five cards

Mathematical objects:
exists same first or second char

Target:
whether rank or suit matches
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
OR condition

Question
        ↓
whether rank or suit matches
```

-   **Mathematical Model:**

``` text
OR condition
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
exists same first or second char
  ↓ write condition
OR condition
  ↓ simplify / recognize
scan
  ↓
O(5)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `exists same first or second char`.
2.  Express the requirement as `OR condition` and simplify it to
    **scan**.
3.  Apply `O(5)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: card + five cards

Decode:         Ignore story nouns.
                Keep: exists same first or second char

Model:          OR condition

Collapse:       scan

Algorithm:      O(5)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    exists same first or second char

MODEL:
    OR condition

SIMPLIFY / TRANSFORM:
    scan

APPLY:
    O(5)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(5)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `OR condition`, think **scan** before implementation.

# Pattern 7 --- Counting / Frequency / Pairs

## Pattern Overview

Replace pair enumeration with frequency counting whenever validity
depends only on a key. Equal-key pairs contribute `f(f-1)/2`.

``` text
Statement wording
      ↓
remove domain story
      ↓
mathematical condition
      ↓
recognize: Counting / Frequency / Pairs
```

Problem Link: [CF 1520D --- Same
Differences](https://codeforces.com/problemset/problem/1520/D)

**Problem Summary:** Given `array`, decode the statement into
mathematics and determine `count i<j with a[j]-a[i]=j-i`.

### CF 1520D --- Same Differences (Algebra/Frequency / Codeforces / 1200)

-   **Core Invariant / Key Insight:** The story collapses to
    `key=a[i]-i`. The key Phase-1 move is to recognize **equal-key
    pairs**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
array
target: count i<j with a[j]-a[i]=j-i
```

-   **Define Variables:**

``` text
Given:
array

Mathematical objects:
a[j]-j=a[i]-i

Target:
count i<j with a[j]-a[i]=j-i
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
key=a[i]-i

Question
        ↓
count i<j with a[j]-a[i]=j-i
```

-   **Mathematical Model:**

``` text
key=a[i]-i
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
a[j]-j=a[i]-i
  ↓ write condition
key=a[i]-i
  ↓ simplify / recognize
equal-key pairs
  ↓
hash map
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `a[j]-j=a[i]-i`.
2.  Express the requirement as `key=a[i]-i` and simplify it to
    **equal-key pairs**.
3.  Apply `hash map` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: array

Decode:         Ignore story nouns.
                Keep: a[j]-j=a[i]-i

Model:          key=a[i]-i

Collapse:       equal-key pairs

Algorithm:      hash map

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    a[j]-j=a[i]-i

MODEL:
    key=a[i]-i

SIMPLIFY / TRANSFORM:
    equal-key pairs

APPLY:
    hash map

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **hash map**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `key=a[i]-i`, think **equal-key pairs** before implementation.

Problem Link: [CF 1538C --- Challenging Cliffs / Number of
Pairs](https://codeforces.com/problemset/problem/1538/C)

**Problem Summary:** Given `array,l,r`, decode the statement into
mathematics and determine `count pairs with sum in [l,r]`.

### CF 1538C --- Challenging Cliffs / Number of Pairs (Two Pointers / Codeforces / 1300)

-   **Core Invariant / Key Insight:** The story collapses to
    `sorted pair bound`. The key Phase-1 move is to recognize **two
    pointers**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
array,l,r
target: count pairs with sum in [l,r]
```

-   **Define Variables:**

``` text
Given:
array,l,r

Mathematical objects:
count<=r - count<l

Target:
count pairs with sum in [l,r]
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
sorted pair bound

Question
        ↓
count pairs with sum in [l,r]
```

-   **Mathematical Model:**

``` text
sorted pair bound
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
count<=r - count<l
  ↓ write condition
sorted pair bound
  ↓ simplify / recognize
two pointers
  ↓
O(nlogn)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `count<=r - count<l`.
2.  Express the requirement as `sorted pair bound` and simplify it to
    **two pointers**.
3.  Apply `O(nlogn)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: array,l,r

Decode:         Ignore story nouns.
                Keep: count<=r - count<l

Model:          sorted pair bound

Collapse:       two pointers

Algorithm:      O(nlogn)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    count<=r - count<l

MODEL:
    sorted pair bound

SIMPLIFY / TRANSFORM:
    two pointers

APPLY:
    O(nlogn)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(nlogn)**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `sorted pair bound`, think **two pointers** before implementation.

Problem Link: [CF 1669B ---
Triple](https://codeforces.com/problemset/problem/1669/B)

**Problem Summary:** Given `array`, decode the statement into
mathematics and determine `find value occurring >=3`.

### CF 1669B --- Triple (Frequency / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `frequency threshold`. The key Phase-1 move is to recognize
    **count**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
array
target: find value occurring >=3
```

-   **Define Variables:**

``` text
Given:
array

Mathematical objects:
freq[x]>=3

Target:
find value occurring >=3
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
frequency threshold

Question
        ↓
find value occurring >=3
```

-   **Mathematical Model:**

``` text
frequency threshold
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
freq[x]>=3
  ↓ write condition
frequency threshold
  ↓ simplify / recognize
count
  ↓
map
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `freq[x]>=3`.
2.  Express the requirement as `frequency threshold` and simplify it to
    **count**.
3.  Apply `map` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: array

Decode:         Ignore story nouns.
                Keep: freq[x]>=3

Model:          frequency threshold

Collapse:       count

Algorithm:      map

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    freq[x]>=3

MODEL:
    frequency threshold

SIMPLIFY / TRANSFORM:
    count

APPLY:
    map

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **map**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `frequency threshold`, think **count** before implementation.

Problem Link: [CF 1742C ---
Stripes](https://codeforces.com/problemset/problem/1742/C)

**Problem Summary:** Given `8x8 grid`, decode the statement into
mathematics and determine `determine last full stripe color`.

### CF 1742C --- Stripes (Grid/Existence / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `scan rows`. The key Phase-1 move is to recognize **existence**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
8x8 grid
target: determine last full stripe color
```

-   **Define Variables:**

``` text
Given:
8x8 grid

Mathematical objects:
full row of R is decisive

Target:
determine last full stripe color
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
scan rows

Question
        ↓
determine last full stripe color
```

-   **Mathematical Model:**

``` text
scan rows
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
full row of R is decisive
  ↓ write condition
scan rows
  ↓ simplify / recognize
existence
  ↓
O(64)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `full row of R is decisive`.
2.  Express the requirement as `scan rows` and simplify it to
    **existence**.
3.  Apply `O(64)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: 8x8 grid

Decode:         Ignore story nouns.
                Keep: full row of R is decisive

Model:          scan rows

Collapse:       existence

Algorithm:      O(64)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    full row of R is decisive

MODEL:
    scan rows

SIMPLIFY / TRANSFORM:
    existence

APPLY:
    O(64)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(64)**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `scan rows`, think **existence** before implementation.

Problem Link: [CF 1791B --- Following
Directions](https://codeforces.com/problemset/problem/1791/B)

**Problem Summary:** Given `moves`, decode the statement into
mathematics and determine `whether path visits (1,1)`.

### CF 1791B --- Following Directions (Coordinates / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `∃prefix=(1,1)`. The key Phase-1 move is to recognize **prefix
    state**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
moves
target: whether path visits (1,1)
```

-   **Define Variables:**

``` text
Given:
moves

Mathematical objects:
update x,y per char

Target:
whether path visits (1,1)
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
∃prefix=(1,1)

Question
        ↓
whether path visits (1,1)
```

-   **Mathematical Model:**

``` text
∃prefix=(1,1)
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
update x,y per char
  ↓ write condition
∃prefix=(1,1)
  ↓ simplify / recognize
prefix state
  ↓
scan
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `update x,y per char`.
2.  Express the requirement as `∃prefix=(1,1)` and simplify it to
    **prefix state**.
3.  Apply `scan` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: moves

Decode:         Ignore story nouns.
                Keep: update x,y per char

Model:          ∃prefix=(1,1)

Collapse:       prefix state

Algorithm:      scan

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    update x,y per char

MODEL:
    ∃prefix=(1,1)

SIMPLIFY / TRANSFORM:
    prefix state

APPLY:
    scan

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **scan**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `∃prefix=(1,1)`, think **prefix state** before implementation.

Problem Link: [CF 1703B --- ICPC
Balloons](https://codeforces.com/problemset/problem/1703/B)

**Problem Summary:** Given `string`, decode the statement into
mathematics and determine `score first occurrence differently`.

### CF 1703B --- ICPC Balloons (Frequency / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to `seen set`.
    The key Phase-1 move is to recognize **contribution**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
string
target: score first occurrence differently
```

-   **Define Variables:**

``` text
Given:
string

Mathematical objects:
first char contributes2 else1

Target:
score first occurrence differently
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
seen set

Question
        ↓
score first occurrence differently
```

-   **Mathematical Model:**

``` text
seen set
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
first char contributes2 else1
  ↓ write condition
seen set
  ↓ simplify / recognize
contribution
  ↓
set
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `first char contributes2 else1`.
2.  Express the requirement as `seen set` and simplify it to
    **contribution**.
3.  Apply `set` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: string

Decode:         Ignore story nouns.
                Keep: first char contributes2 else1

Model:          seen set

Collapse:       contribution

Algorithm:      set

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    first char contributes2 else1

MODEL:
    seen set

SIMPLIFY / TRANSFORM:
    contribution

APPLY:
    set

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **set**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `seen set`, think **contribution** before implementation.

Problem Link: [CF 1722A --- Spell
Check](https://codeforces.com/problemset/problem/1722/A)

**Problem Summary:** Given `string`, decode the statement into
mathematics and determine `whether permutation equals TimUR`.

### CF 1722A --- Spell Check (Frequency/Sorting / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `sort or counts`. The key Phase-1 move is to recognize **canonical
    form**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
string
target: whether permutation equals TimUR
```

-   **Define Variables:**

``` text
Given:
string

Mathematical objects:
same multiset as 'Timur'

Target:
whether permutation equals TimUR
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
sort or counts

Question
        ↓
whether permutation equals TimUR
```

-   **Mathematical Model:**

``` text
sort or counts
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
same multiset as 'Timur'
  ↓ write condition
sort or counts
  ↓ simplify / recognize
canonical form
  ↓
sort
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `same multiset as 'Timur'`.
2.  Express the requirement as `sort or counts` and simplify it to
    **canonical form**.
3.  Apply `sort` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: string

Decode:         Ignore story nouns.
                Keep: same multiset as 'Timur'

Model:          sort or counts

Collapse:       canonical form

Algorithm:      sort

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    same multiset as 'Timur'

MODEL:
    sort or counts

SIMPLIFY / TRANSFORM:
    canonical form

APPLY:
    sort

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **sort**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `sort or counts`, think **canonical form** before implementation.

Problem Link: [CF 1791C --- Prepend and
Append](https://codeforces.com/problemset/problem/1791/C)

**Problem Summary:** Given `binary string`, decode the statement into
mathematics and determine `remove unequal ends`.

### CF 1791C --- Prepend and Append (Two Pointers / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `remaining length`. The key Phase-1 move is to recognize **two
    pointers**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
binary string
target: remove unequal ends
```

-   **Define Variables:**

``` text
Given:
binary string

Mathematical objects:
while l<r and s[l]!=s[r]

Target:
remove unequal ends
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
remaining length

Question
        ↓
remove unequal ends
```

-   **Mathematical Model:**

``` text
remaining length
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
while l<r and s[l]!=s[r]
  ↓ write condition
remaining length
  ↓ simplify / recognize
two pointers
  ↓
O(n)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `while l<r and s[l]!=s[r]`.
2.  Express the requirement as `remaining length` and simplify it to
    **two pointers**.
3.  Apply `O(n)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: binary string

Decode:         Ignore story nouns.
                Keep: while l<r and s[l]!=s[r]

Model:          remaining length

Collapse:       two pointers

Algorithm:      O(n)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    while l<r and s[l]!=s[r]

MODEL:
    remaining length

SIMPLIFY / TRANSFORM:
    two pointers

APPLY:
    O(n)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(n)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `remaining length`, think **two pointers** before implementation.

Problem Link: [CF 1829D --- Gold
Rush](https://codeforces.com/problemset/problem/1829/D)

**Problem Summary:** Given `n,m`, decode the statement into mathematics
and determine `can reach m by splitting x into x/3 and2x/3`.

### CF 1829D --- Gold Rush (Recursion/Reachability / Codeforces / 1000)

-   **Core Invariant / Key Insight:** The story collapses to
    `DFS on decreasing states`. The key Phase-1 move is to recognize
    **reachability**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
n,m
target: can reach m by splitting x into x/3 and2x/3
```

-   **Define Variables:**

``` text
Given:
n,m

Mathematical objects:
only split divisible by3

Target:
can reach m by splitting x into x/3 and2x/3
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
DFS on decreasing states

Question
        ↓
can reach m by splitting x into x/3 and2x/3
```

-   **Mathematical Model:**

``` text
DFS on decreasing states
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
only split divisible by3
  ↓ write condition
DFS on decreasing states
  ↓ simplify / recognize
reachability
  ↓
recursion
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `only split divisible by3`.
2.  Express the requirement as `DFS on decreasing states` and simplify
    it to **reachability**.
3.  Apply `recursion` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: n,m

Decode:         Ignore story nouns.
                Keep: only split divisible by3

Model:          DFS on decreasing states

Collapse:       reachability

Algorithm:      recursion

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    only split divisible by3

MODEL:
    DFS on decreasing states

SIMPLIFY / TRANSFORM:
    reachability

APPLY:
    recursion

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **recursion**; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `DFS on decreasing states`, think **reachability** before
    implementation.

Problem Link: [CF 1878B --- Aleksa and
Stack](https://codeforces.com/problemset/problem/1878/B)

**Problem Summary:** Given `n`, decode the statement into mathematics
and determine `construct sequence satisfying divisibility condition`.

### CF 1878B --- Aleksa and Stack (Construction / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `constant gap avoids divisibility`. The key Phase-1 move is to
    recognize **construct**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
n
target: construct sequence satisfying divisibility condition
```

-   **Define Variables:**

``` text
Given:
n

Mathematical objects:
choose simple arithmetic sequence

Target:
construct sequence satisfying divisibility condition
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
constant gap avoids divisibility

Question
        ↓
construct sequence satisfying divisibility condition
```

-   **Mathematical Model:**

``` text
constant gap avoids divisibility
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
choose simple arithmetic sequence
  ↓ write condition
constant gap avoids divisibility
  ↓ simplify / recognize
construct
  ↓
formula
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `choose simple arithmetic sequence`.
2.  Express the requirement as `constant gap avoids divisibility` and
    simplify it to **construct**.
3.  Apply `formula` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: n

Decode:         Ignore story nouns.
                Keep: choose simple arithmetic sequence

Model:          constant gap avoids divisibility

Collapse:       construct

Algorithm:      formula

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    choose simple arithmetic sequence

MODEL:
    constant gap avoids divisibility

SIMPLIFY / TRANSFORM:
    construct

APPLY:
    formula

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **formula**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `constant gap avoids divisibility`, think **construct** before
    implementation.

# Pattern 8 --- Operation → Delta → Invariant

## Pattern Overview

Write one operation as BEFORE → AFTER. Compute what changes and what
remains invariant: sum, parity, difference, GCD, XOR, or a monotone
quantity.

``` text
Statement wording
      ↓
remove domain story
      ↓
mathematical condition
      ↓
recognize: Operation → Delta → Invariant
```

Problem Link: [CF 1538B --- Friends and
Candies](https://codeforces.com/problemset/problem/1538/B)

**Problem Summary:** Given `array`, decode the statement into
mathematics and determine `equalize while preserving sum`.

### CF 1538B --- Friends and Candies (Invariant / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to `S%n=0`.
    The key Phase-1 move is to recognize **average invariant**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
array
target: equalize while preserving sum
```

-   **Define Variables:**

``` text
Given:
array

Mathematical objects:
S=n*x

Target:
equalize while preserving sum
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
S%n=0

Question
        ↓
equalize while preserving sum
```

-   **Mathematical Model:**

``` text
S%n=0
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
S=n*x
  ↓ write condition
S%n=0
  ↓ simplify / recognize
average invariant
  ↓
count >avg
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `S=n*x`.
2.  Express the requirement as `S%n=0` and simplify it to **average
    invariant**.
3.  Apply `count >avg` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: array

Decode:         Ignore story nouns.
                Keep: S=n*x

Model:          S%n=0

Collapse:       average invariant

Algorithm:      count >avg

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    S=n*x

MODEL:
    S%n=0

SIMPLIFY / TRANSFORM:
    average invariant

APPLY:
    count >avg

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **count \>avg**; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to `S%n=0`,
    think **average invariant** before implementation.

Problem Link: [CF 1855A --- Dalton the
Teacher](https://codeforces.com/problemset/problem/1855/A)

**Problem Summary:** Given `permutation`, decode the statement into
mathematics and determine
`minimum operations fixing fixed points by pair operation`.

### CF 1855A --- Dalton the Teacher (Mismatch/Operation / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `ceil(fixed/2)`. The key Phase-1 move is to recognize **count
    fixed**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
permutation
target: minimum operations fixing fixed points by pair operation
```

-   **Define Variables:**

``` text
Given:
permutation

Mathematical objects:
each op can fix at most2 fixed points

Target:
minimum operations fixing fixed points by pair operation
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
ceil(fixed/2)

Question
        ↓
minimum operations fixing fixed points by pair operation
```

-   **Mathematical Model:**

``` text
ceil(fixed/2)
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
each op can fix at most2 fixed points
  ↓ write condition
ceil(fixed/2)
  ↓ simplify / recognize
count fixed
  ↓
(cnt+1)/2
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `each op can fix at most2 fixed points`.
2.  Express the requirement as `ceil(fixed/2)` and simplify it to
    **count fixed**.
3.  Apply `(cnt+1)/2` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: permutation

Decode:         Ignore story nouns.
                Keep: each op can fix at most2 fixed points

Model:          ceil(fixed/2)

Collapse:       count fixed

Algorithm:      (cnt+1)/2

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    each op can fix at most2 fixed points

MODEL:
    ceil(fixed/2)

SIMPLIFY / TRANSFORM:
    count fixed

APPLY:
    (cnt+1)/2

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **(cnt+1)/2**; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `ceil(fixed/2)`, think **count fixed** before implementation.

Problem Link: [CF 1838A --- Blackboard
List](https://codeforces.com/problemset/problem/1838/A)

**Problem Summary:** Given `array`, decode the statement into
mathematics and determine `recover original special number`.

### CF 1838A --- Blackboard List (Extremal / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `extremal invariant`. The key Phase-1 move is to recognize **min if
    negative else max**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
array
target: recover original special number
```

-   **Define Variables:**

``` text
Given:
array

Mathematical objects:
negative minimum survives construction; else maximum

Target:
recover original special number
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
extremal invariant

Question
        ↓
recover original special number
```

-   **Mathematical Model:**

``` text
extremal invariant
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
negative minimum survives construction; else maximum
  ↓ write condition
extremal invariant
  ↓ simplify / recognize
min if negative else max
  ↓
scan
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `negative minimum survives construction; else maximum`.
2.  Express the requirement as `extremal invariant` and simplify it to
    **min if negative else max**.
3.  Apply `scan` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: array

Decode:         Ignore story nouns.
                Keep: negative minimum survives construction; else maximum

Model:          extremal invariant

Collapse:       min if negative else max

Algorithm:      scan

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    negative minimum survives construction; else maximum

MODEL:
    extremal invariant

SIMPLIFY / TRANSFORM:
    min if negative else max

APPLY:
    scan

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **scan**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `extremal invariant`, think **min if negative else max** before
    implementation.

Problem Link: [CF 1862B --- Sequence
Game](https://codeforces.com/problemset/problem/1862/B)

**Problem Summary:** Given `sequence b`, decode the statement into
mathematics and determine `construct a so filtering rule returns b`.

### CF 1862B --- Sequence Game (Construction / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `local condition`. The key Phase-1 move is to recognize **construct
    with extra value**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
sequence b
target: construct a so filtering rule returns b
```

-   **Define Variables:**

``` text
Given:
sequence b

Mathematical objects:
insert bridge when b[i-1]>b[i]

Target:
construct a so filtering rule returns b
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
local condition

Question
        ↓
construct a so filtering rule returns b
```

-   **Mathematical Model:**

``` text
local condition
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
insert bridge when b[i-1]>b[i]
  ↓ write condition
local condition
  ↓ simplify / recognize
construct with extra value
  ↓
linear
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `insert bridge when b[i-1]>b[i]`.
2.  Express the requirement as `local condition` and simplify it to
    **construct with extra value**.
3.  Apply `linear` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: sequence b

Decode:         Ignore story nouns.
                Keep: insert bridge when b[i-1]>b[i]

Model:          local condition

Collapse:       construct with extra value

Algorithm:      linear

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    insert bridge when b[i-1]>b[i]

MODEL:
    local condition

SIMPLIFY / TRANSFORM:
    construct with extra value

APPLY:
    linear

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **linear**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `local condition`, think **construct with extra value** before
    implementation.

Problem Link: [CF 1798A ---
Showstopper](https://codeforces.com/problemset/problem/1798/A)

**Problem Summary:** Given `two arrays`, decode the statement into
mathematics and determine `can swap pairs so last elements are maxima`.

### CF 1798A --- Showstopper (Invariant/Swap / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `need max pair endpoints fit final`. The key Phase-1 move is to
    recognize **normalize max/min**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
two arrays
target: can swap pairs so last elements are maxima
```

-   **Define Variables:**

``` text
Given:
two arrays

Mathematical objects:
each pair independently orientable

Target:
can swap pairs so last elements are maxima
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
need max pair endpoints fit final

Question
        ↓
can swap pairs so last elements are maxima
```

-   **Mathematical Model:**

``` text
need max pair endpoints fit final
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
each pair independently orientable
  ↓ write condition
need max pair endpoints fit final
  ↓ simplify / recognize
normalize max/min
  ↓
check
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `each pair independently orientable`.
2.  Express the requirement as `need max pair endpoints fit final` and
    simplify it to **normalize max/min**.
3.  Apply `check` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: two arrays

Decode:         Ignore story nouns.
                Keep: each pair independently orientable

Model:          need max pair endpoints fit final

Collapse:       normalize max/min

Algorithm:      check

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    each pair independently orientable

MODEL:
    need max pair endpoints fit final

SIMPLIFY / TRANSFORM:
    normalize max/min

APPLY:
    check

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **check**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `need max pair endpoints fit final`, think **normalize max/min**
    before implementation.

Problem Link: [CF 660A --- Co-prime
Array](https://codeforces.com/problemset/problem/660/A)

**Problem Summary:** Given `array`, decode the statement into
mathematics and determine `insert minimum numbers so adjacent gcd=1`.

### CF 660A --- Co-prime Array (Construction/GCD / Codeforces / 900)

-   **Core Invariant / Key Insight:** The story collapses to
    `local repair`. The key Phase-1 move is to recognize **insert 1**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
array
target: insert minimum numbers so adjacent gcd=1
```

-   **Define Variables:**

``` text
Given:
array

Mathematical objects:
if gcd(a[i],a[i+1])>1 insert coprime sentinel

Target:
insert minimum numbers so adjacent gcd=1
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
local repair

Question
        ↓
insert minimum numbers so adjacent gcd=1
```

-   **Mathematical Model:**

``` text
local repair
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
if gcd(a[i],a[i+1])>1 insert coprime sentinel
  ↓ write condition
local repair
  ↓ simplify / recognize
insert 1
  ↓
linear
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `if gcd(a[i],a[i+1])>1 insert coprime sentinel`.
2.  Express the requirement as `local repair` and simplify it to
    **insert 1**.
3.  Apply `linear` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: array

Decode:         Ignore story nouns.
                Keep: if gcd(a[i],a[i+1])>1 insert coprime sentinel

Model:          local repair

Collapse:       insert 1

Algorithm:      linear

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    if gcd(a[i],a[i+1])>1 insert coprime sentinel

MODEL:
    local repair

SIMPLIFY / TRANSFORM:
    insert 1

APPLY:
    linear

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **linear**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `local repair`, think **insert 1** before implementation.

Problem Link: [CF 1367A --- Short
Substrings](https://codeforces.com/problemset/problem/1367/A)

**Problem Summary:** Given `string b`, decode the statement into
mathematics and determine `recover original`.

### CF 1367A --- Short Substrings (String Reconstruction / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `take first then every second char`. The key Phase-1 move is to
    recognize **inverse operation**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
string b
target: recover original
```

-   **Define Variables:**

``` text
Given:
string b

Mathematical objects:
overlap pairs share char

Target:
recover original
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
take first then every second char

Question
        ↓
recover original
```

-   **Mathematical Model:**

``` text
take first then every second char
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
overlap pairs share char
  ↓ write condition
take first then every second char
  ↓ simplify / recognize
inverse operation
  ↓
construct
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `overlap pairs share char`.
2.  Express the requirement as `take first then every second char` and
    simplify it to **inverse operation**.
3.  Apply `construct` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: string b

Decode:         Ignore story nouns.
                Keep: overlap pairs share char

Model:          take first then every second char

Collapse:       inverse operation

Algorithm:      construct

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    overlap pairs share char

MODEL:
    take first then every second char

SIMPLIFY / TRANSFORM:
    inverse operation

APPLY:
    construct

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **construct**; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `take first then every second char`, think **inverse operation**
    before implementation.

Problem Link: [CF 1374A --- Required
Remainder](https://codeforces.com/problemset/problem/1374/A)

**Problem Summary:** Given `x,y,n`, decode the statement into
mathematics and determine `largest k<=n with k%x=y`.

### CF 1374A --- Required Remainder (Modulo/Optimization / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `t=floor((n-y)/x)`. The key Phase-1 move is to recognize **largest
    feasible**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
x,y,n
target: largest k<=n with k%x=y
```

-   **Define Variables:**

``` text
Given:
x,y,n

Mathematical objects:
numbers are tx+y

Target:
largest k<=n with k%x=y
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
t=floor((n-y)/x)

Question
        ↓
largest k<=n with k%x=y
```

-   **Mathematical Model:**

``` text
t=floor((n-y)/x)
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
numbers are tx+y
  ↓ write condition
t=floor((n-y)/x)
  ↓ simplify / recognize
largest feasible
  ↓
formula
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `numbers are tx+y`.
2.  Express the requirement as `t=floor((n-y)/x)` and simplify it to
    **largest feasible**.
3.  Apply `formula` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: x,y,n

Decode:         Ignore story nouns.
                Keep: numbers are tx+y

Model:          t=floor((n-y)/x)

Collapse:       largest feasible

Algorithm:      formula

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    numbers are tx+y

MODEL:
    t=floor((n-y)/x)

SIMPLIFY / TRANSFORM:
    largest feasible

APPLY:
    formula

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **formula**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `t=floor((n-y)/x)`, think **largest feasible** before
    implementation.

Problem Link: [CF 1551A --- Polycarp and
Coins](https://codeforces.com/problemset/problem/1551/A)

**Problem Summary:** Given `n`, decode the statement into mathematics
and determine
`split n into 1-coin and2-coin counts minimizing difference`.

### CF 1551A --- Polycarp and Coins (Balancing / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to `near n/3`.
    The key Phase-1 move is to recognize **balanced equation**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
n
target: split n into 1-coin and2-coin counts minimizing difference
```

-   **Define Variables:**

``` text
Given:
n

Mathematical objects:
c1+2c2=n, |c1-c2| min

Target:
split n into 1-coin and2-coin counts minimizing difference
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
near n/3

Question
        ↓
split n into 1-coin and2-coin counts minimizing difference
```

-   **Mathematical Model:**

``` text
near n/3
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
c1+2c2=n, |c1-c2| min
  ↓ write condition
near n/3
  ↓ simplify / recognize
balanced equation
  ↓
n%3 cases
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `c1+2c2=n, |c1-c2| min`.
2.  Express the requirement as `near n/3` and simplify it to **balanced
    equation**.
3.  Apply `n%3 cases` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: n

Decode:         Ignore story nouns.
                Keep: c1+2c2=n, |c1-c2| min

Model:          near n/3

Collapse:       balanced equation

Algorithm:      n%3 cases

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    c1+2c2=n, |c1-c2| min

MODEL:
    near n/3

SIMPLIFY / TRANSFORM:
    balanced equation

APPLY:
    n%3 cases

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **n%3 cases**; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `near n/3`, think **balanced equation** before implementation.

Problem Link: [CF 1818A ---
Politics](https://codeforces.com/problemset/problem/1818/A)

**Problem Summary:** Given `strings`, decode the statement into
mathematics and determine `count strings compatible with reference`.

### CF 1818A --- Politics (String/Counting / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `predicate per string`. The key Phase-1 move is to recognize **count
    valid**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
strings
target: count strings compatible with reference
```

-   **Define Variables:**

``` text
Given:
strings

Mathematical objects:
positions with reference 1 impose equality

Target:
count strings compatible with reference
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
predicate per string

Question
        ↓
count strings compatible with reference
```

-   **Mathematical Model:**

``` text
predicate per string
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
positions with reference 1 impose equality
  ↓ write condition
predicate per string
  ↓ simplify / recognize
count valid
  ↓
nested scan
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `positions with reference 1 impose equality`.
2.  Express the requirement as `predicate per string` and simplify it to
    **count valid**.
3.  Apply `nested scan` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: strings

Decode:         Ignore story nouns.
                Keep: positions with reference 1 impose equality

Model:          predicate per string

Collapse:       count valid

Algorithm:      nested scan

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    positions with reference 1 impose equality

MODEL:
    predicate per string

SIMPLIFY / TRANSFORM:
    count valid

APPLY:
    nested scan

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **nested scan**; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `predicate per string`, think **count valid** before implementation.

# Pattern 9 --- Sorting / Coordinate / Distance Modeling

## Pattern Overview

Use sorting to remove irrelevant order and expose adjacency, extremes,
or matching. Use `|x-y|` for number-line distance.

``` text
Statement wording
      ↓
remove domain story
      ↓
mathematical condition
      ↓
recognize: Sorting / Coordinate / Distance Modeling
```

Problem Link: [CF 160A ---
Twins](https://codeforces.com/problemset/problem/160/A)

**Problem Summary:** Given `coins`, decode the statement into
mathematics and determine `minimum selected sum > rest`.

### CF 160A --- Twins (Sorting/Greedy / Codeforces / 900)

-   **Core Invariant / Key Insight:** The story collapses to
    `prefix until 2sum>total`. The key Phase-1 move is to recognize
    **extremal choice**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
coins
target: minimum selected sum > rest
```

-   **Define Variables:**

``` text
Given:
coins

Mathematical objects:
sort descending

Target:
minimum selected sum > rest
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
prefix until 2sum>total

Question
        ↓
minimum selected sum > rest
```

-   **Mathematical Model:**

``` text
prefix until 2sum>total
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
sort descending
  ↓ write condition
prefix until 2sum>total
  ↓ simplify / recognize
extremal choice
  ↓
O(nlogn)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `sort descending`.
2.  Express the requirement as `prefix until 2sum>total` and simplify it
    to **extremal choice**.
3.  Apply `O(nlogn)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: coins

Decode:         Ignore story nouns.
                Keep: sort descending

Model:          prefix until 2sum>total

Collapse:       extremal choice

Algorithm:      O(nlogn)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    sort descending

MODEL:
    prefix until 2sum>total

SIMPLIFY / TRANSFORM:
    extremal choice

APPLY:
    O(nlogn)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(nlogn)**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `prefix until 2sum>total`, think **extremal choice** before
    implementation.

Problem Link: [CF 1399A --- Remove
Smallest](https://codeforces.com/problemset/problem/1399/A)

**Problem Summary:** Given `array`, decode the statement into
mathematics and determine `can delete until one remains under diff<=1`.

### CF 1399A --- Remove Smallest (Sorting / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `adjacent condition`. The key Phase-1 move is to recognize
    **check**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
array
target: can delete until one remains under diff<=1
```

-   **Define Variables:**

``` text
Given:
array

Mathematical objects:
sort; all adjacent gaps<=1

Target:
can delete until one remains under diff<=1
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
adjacent condition

Question
        ↓
can delete until one remains under diff<=1
```

-   **Mathematical Model:**

``` text
adjacent condition
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
sort; all adjacent gaps<=1
  ↓ write condition
adjacent condition
  ↓ simplify / recognize
check
  ↓
O(nlogn)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `sort; all adjacent gaps<=1`.
2.  Express the requirement as `adjacent condition` and simplify it to
    **check**.
3.  Apply `O(nlogn)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: array

Decode:         Ignore story nouns.
                Keep: sort; all adjacent gaps<=1

Model:          adjacent condition

Collapse:       check

Algorithm:      O(nlogn)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    sort; all adjacent gaps<=1

MODEL:
    adjacent condition

SIMPLIFY / TRANSFORM:
    check

APPLY:
    O(nlogn)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(nlogn)**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `adjacent condition`, think **check** before implementation.

Problem Link: [CF 1760A --- Medium
Number](https://codeforces.com/problemset/problem/1760/A)

**Problem Summary:** Given `a,b,c`, decode the statement into
mathematics and determine `middle value`.

### CF 1760A --- Medium Number (Sorting / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `second element`. The key Phase-1 move is to recognize **median**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
a,b,c
target: middle value
```

-   **Define Variables:**

``` text
Given:
a,b,c

Mathematical objects:
sort three

Target:
middle value
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
second element

Question
        ↓
middle value
```

-   **Mathematical Model:**

``` text
second element
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
sort three
  ↓ write condition
second element
  ↓ simplify / recognize
median
  ↓
O(1)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `sort three`.
2.  Express the requirement as `second element` and simplify it to
    **median**.
3.  Apply `O(1)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: a,b,c

Decode:         Ignore story nouns.
                Keep: sort three

Model:          second element

Collapse:       median

Algorithm:      O(1)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    sort three

MODEL:
    second element

SIMPLIFY / TRANSFORM:
    median

APPLY:
    O(1)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(1)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `second element`, think **median** before implementation.

Problem Link: [CF 1538A --- Stone
Game](https://codeforces.com/problemset/problem/1538/A)

**Problem Summary:** Given `permutation`, decode the statement into
mathematics and determine
`min removals from ends to remove min and max`.

### CF 1538A --- Stone Game (Positions / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `min of three strategies`. The key Phase-1 move is to recognize
    **distance to ends**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
permutation
target: min removals from ends to remove min and max
```

-   **Define Variables:**

``` text
Given:
permutation

Mathematical objects:
positions pmin,pmax

Target:
min removals from ends to remove min and max
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
min of three strategies

Question
        ↓
min removals from ends to remove min and max
```

-   **Mathematical Model:**

``` text
min of three strategies
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
positions pmin,pmax
  ↓ write condition
min of three strategies
  ↓ simplify / recognize
distance to ends
  ↓
formula
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `positions pmin,pmax`.
2.  Express the requirement as `min of three strategies` and simplify it
    to **distance to ends**.
3.  Apply `formula` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: permutation

Decode:         Ignore story nouns.
                Keep: positions pmin,pmax

Model:          min of three strategies

Collapse:       distance to ends

Algorithm:      formula

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    positions pmin,pmax

MODEL:
    min of three strategies

SIMPLIFY / TRANSFORM:
    distance to ends

APPLY:
    formula

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **formula**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `min of three strategies`, think **distance to ends** before
    implementation.

Problem Link: [CF 1729A --- Two
Elevators](https://codeforces.com/problemset/problem/1729/A)

**Problem Summary:** Given `a,b,c`, decode the statement into
mathematics and determine `compare travel times`.

### CF 1729A --- Two Elevators (Distance / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `absolute distance`. The key Phase-1 move is to recognize
    **compare**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
a,b,c
target: compare travel times
```

-   **Define Variables:**

``` text
Given:
a,b,c

Mathematical objects:
t1=a-1, t2=|b-c|+c-1

Target:
compare travel times
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
absolute distance

Question
        ↓
compare travel times
```

-   **Mathematical Model:**

``` text
absolute distance
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
t1=a-1, t2=|b-c|+c-1
  ↓ write condition
absolute distance
  ↓ simplify / recognize
compare
  ↓
O(1)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `t1=a-1, t2=|b-c|+c-1`.
2.  Express the requirement as `absolute distance` and simplify it to
    **compare**.
3.  Apply `O(1)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: a,b,c

Decode:         Ignore story nouns.
                Keep: t1=a-1, t2=|b-c|+c-1

Model:          absolute distance

Collapse:       compare

Algorithm:      O(1)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    t1=a-1, t2=|b-c|+c-1

MODEL:
    absolute distance

SIMPLIFY / TRANSFORM:
    compare

APPLY:
    O(1)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(1)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `absolute distance`, think **compare** before implementation.

Problem Link: [CF 1593B --- Make it Divisible by
25](https://codeforces.com/problemset/problem/1593/B)

**Problem Summary:** Given `string number`, decode the statement into
mathematics and determine `min deletions for divisible by25`.

### CF 1593B --- Make it Divisible by 25 (Digit Pattern / Codeforces / 900)

-   **Core Invariant / Key Insight:** The story collapses to
    `find pair from right`. The key Phase-1 move is to recognize
    **pattern search**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
string number
target: min deletions for divisible by25
```

-   **Define Variables:**

``` text
Given:
string number

Mathematical objects:
last two digits in {00,25,50,75}

Target:
min deletions for divisible by25
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
find pair from right

Question
        ↓
min deletions for divisible by25
```

-   **Mathematical Model:**

``` text
find pair from right
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
last two digits in {00,25,50,75}
  ↓ write condition
find pair from right
  ↓ simplify / recognize
pattern search
  ↓
O(n)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `last two digits in {00,25,50,75}`.
2.  Express the requirement as `find pair from right` and simplify it to
    **pattern search**.
3.  Apply `O(n)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: string number

Decode:         Ignore story nouns.
                Keep: last two digits in {00,25,50,75}

Model:          find pair from right

Collapse:       pattern search

Algorithm:      O(n)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    last two digits in {00,25,50,75}

MODEL:
    find pair from right

SIMPLIFY / TRANSFORM:
    pattern search

APPLY:
    O(n)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(n)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `find pair from right`, think **pattern search** before
    implementation.

Problem Link: [CF 1742F ---
Smaller](https://codeforces.com/problemset/problem/1742/F)

**Problem Summary:** Given `string append queries`, decode the statement
into mathematics and determine `whether s<t possible`.

### CF 1742F --- Smaller (Lexicographic/Invariant / Codeforces / 1200)

-   **Core Invariant / Key Insight:** The story collapses to
    `track counts/flags`. The key Phase-1 move is to recognize
    **compressed state**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
string append queries
target: whether s<t possible
```

-   **Define Variables:**

``` text
Given:
string append queries

Mathematical objects:
presence of char >'a' dominates

Target:
whether s<t possible
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
track counts/flags

Question
        ↓
whether s<t possible
```

-   **Mathematical Model:**

``` text
track counts/flags
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
presence of char >'a' dominates
  ↓ write condition
track counts/flags
  ↓ simplify / recognize
compressed state
  ↓
O(q)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `presence of char >'a' dominates`.
2.  Express the requirement as `track counts/flags` and simplify it to
    **compressed state**.
3.  Apply `O(q)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: string append queries

Decode:         Ignore story nouns.
                Keep: presence of char >'a' dominates

Model:          track counts/flags

Collapse:       compressed state

Algorithm:      O(q)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    presence of char >'a' dominates

MODEL:
    track counts/flags

SIMPLIFY / TRANSFORM:
    compressed state

APPLY:
    O(q)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(q)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `track counts/flags`, think **compressed state** before
    implementation.

Problem Link: [CF 1831A --- Twin
Permutations](https://codeforces.com/problemset/problem/1831/A)

**Problem Summary:** Given `permutation`, decode the statement into
mathematics and determine `construct complementary permutation`.

### CF 1831A --- Twin Permutations (Mapping / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `value reflection`. The key Phase-1 move is to recognize **direct
    transform**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
permutation
target: construct complementary permutation
```

-   **Define Variables:**

``` text
Given:
permutation

Mathematical objects:
b[i]=n+1-a[i]

Target:
construct complementary permutation
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
value reflection

Question
        ↓
construct complementary permutation
```

-   **Mathematical Model:**

``` text
value reflection
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
b[i]=n+1-a[i]
  ↓ write condition
value reflection
  ↓ simplify / recognize
direct transform
  ↓
O(n)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `b[i]=n+1-a[i]`.
2.  Express the requirement as `value reflection` and simplify it to
    **direct transform**.
3.  Apply `O(n)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: permutation

Decode:         Ignore story nouns.
                Keep: b[i]=n+1-a[i]

Model:          value reflection

Collapse:       direct transform

Algorithm:      O(n)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    b[i]=n+1-a[i]

MODEL:
    value reflection

SIMPLIFY / TRANSFORM:
    direct transform

APPLY:
    O(n)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(n)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `value reflection`, think **direct transform** before
    implementation.

Problem Link: [CF 1900A --- Cover in
Water](https://codeforces.com/problemset/problem/1900/A)

**Problem Summary:** Given `string`, decode the statement into
mathematics and determine `minimum operations to fill dots`.

### CF 1900A --- Cover in Water (Run Length / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `local pattern`. The key Phase-1 move is to recognize **case
    split**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
string
target: minimum operations to fill dots
```

-   **Define Variables:**

``` text
Given:
string

Mathematical objects:
run of >=3 triggers shortcut; else count dots

Target:
minimum operations to fill dots
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
local pattern

Question
        ↓
minimum operations to fill dots
```

-   **Mathematical Model:**

``` text
local pattern
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
run of >=3 triggers shortcut; else count dots
  ↓ write condition
local pattern
  ↓ simplify / recognize
case split
  ↓
scan
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `run of >=3 triggers shortcut; else count dots`.
2.  Express the requirement as `local pattern` and simplify it to **case
    split**.
3.  Apply `scan` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: string

Decode:         Ignore story nouns.
                Keep: run of >=3 triggers shortcut; else count dots

Model:          local pattern

Collapse:       case split

Algorithm:      scan

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    run of >=3 triggers shortcut; else count dots

MODEL:
    local pattern

SIMPLIFY / TRANSFORM:
    case split

APPLY:
    scan

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **scan**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `local pattern`, think **case split** before implementation.

Problem Link: [CF 1873B --- Good
Kid](https://codeforces.com/problemset/problem/1873/B)

**Problem Summary:** Given `digits`, decode the statement into
mathematics and determine `increment one element to maximize product`.

### CF 1873B --- Good Kid (Product/Greedy / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `exchange argument intuition`. The key Phase-1 move is to recognize
    **sort/min index**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
digits
target: increment one element to maximize product
```

-   **Define Variables:**

``` text
Given:
digits

Mathematical objects:
increment smallest

Target:
increment one element to maximize product
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
exchange argument intuition

Question
        ↓
increment one element to maximize product
```

-   **Mathematical Model:**

``` text
exchange argument intuition
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
increment smallest
  ↓ write condition
exchange argument intuition
  ↓ simplify / recognize
sort/min index
  ↓
O(n)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `increment smallest`.
2.  Express the requirement as `exchange argument intuition` and
    simplify it to **sort/min index**.
3.  Apply `O(n)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: digits

Decode:         Ignore story nouns.
                Keep: increment smallest

Model:          exchange argument intuition

Collapse:       sort/min index

Algorithm:      O(n)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    increment smallest

MODEL:
    exchange argument intuition

SIMPLIFY / TRANSFORM:
    sort/min index

APPLY:
    O(n)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(n)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `exchange argument intuition`, think **sort/min index** before
    implementation.

# Pattern 10 --- Prefix / Running-State Modeling

## Pattern Overview

When the statement asks about a process over prefixes or accumulated
totals, define a running state or prefix quantity instead of recomputing
from scratch.

``` text
Statement wording
      ↓
remove domain story
      ↓
mathematical condition
      ↓
recognize: Prefix / Running-State Modeling
```

Problem Link: [CF 116A ---
Tram](https://codeforces.com/problemset/problem/116/A)

**Problem Summary:** Given `enter/exit`, decode the statement into
mathematics and determine `minimum capacity`.

### CF 116A --- Tram (Prefix / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to `max(cur)`.
    The key Phase-1 move is to recognize **prefix occupancy**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
enter/exit
target: minimum capacity
```

-   **Define Variables:**

``` text
Given:
enter/exit

Mathematical objects:
cur += in-out

Target:
minimum capacity
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
max(cur)

Question
        ↓
minimum capacity
```

-   **Mathematical Model:**

``` text
max(cur)
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
cur += in-out
  ↓ write condition
max(cur)
  ↓ simplify / recognize
prefix occupancy
  ↓
scan
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `cur += in-out`.
2.  Express the requirement as `max(cur)` and simplify it to **prefix
    occupancy**.
3.  Apply `scan` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: enter/exit

Decode:         Ignore story nouns.
                Keep: cur += in-out

Model:          max(cur)

Collapse:       prefix occupancy

Algorithm:      scan

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    cur += in-out

MODEL:
    max(cur)

SIMPLIFY / TRANSFORM:
    prefix occupancy

APPLY:
    scan

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **scan**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `max(cur)`, think **prefix occupancy** before implementation.

Problem Link: [CF 363B ---
Fence](https://codeforces.com/problemset/problem/363/B)

**Problem Summary:** Given `array,k`, decode the statement into
mathematics and determine `position of minimum k-length sum`.

### CF 363B --- Fence (Sliding Window / Codeforces / 1100)

-   **Core Invariant / Key Insight:** The story collapses to
    `min over contiguous k`. The key Phase-1 move is to recognize
    **prefix/sliding**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
array,k
target: position of minimum k-length sum
```

-   **Define Variables:**

``` text
Given:
array,k

Mathematical objects:
window sum

Target:
position of minimum k-length sum
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
min over contiguous k

Question
        ↓
position of minimum k-length sum
```

-   **Mathematical Model:**

``` text
min over contiguous k
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
window sum
  ↓ write condition
min over contiguous k
  ↓ simplify / recognize
prefix/sliding
  ↓
O(n)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `window sum`.
2.  Express the requirement as `min over contiguous k` and simplify it
    to **prefix/sliding**.
3.  Apply `O(n)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: array,k

Decode:         Ignore story nouns.
                Keep: window sum

Model:          min over contiguous k

Collapse:       prefix/sliding

Algorithm:      O(n)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    window sum

MODEL:
    min over contiguous k

SIMPLIFY / TRANSFORM:
    prefix/sliding

APPLY:
    O(n)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(n)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `min over contiguous k`, think **prefix/sliding** before
    implementation.

Problem Link: [CF 276C --- Little Girl and Problem on Trees / Little
Girl and Maximum Sum](https://codeforces.com/problemset/problem/276/C)

**Problem Summary:** Given `array,queries`, decode the statement into
mathematics and determine `maximize total query sum by permutation`.

### CF 276C --- Little Girl and Problem on Trees / Little Girl and Maximum Sum (Difference/Contribution / Codeforces / 1400)

-   **Core Invariant / Key Insight:** The story collapses to
    `sort values and frequencies same order`. The key Phase-1 move is to
    recognize **rearrangement inequality**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
array,queries
target: maximize total query sum by permutation
```

-   **Define Variables:**

``` text
Given:
array,queries

Mathematical objects:
frequency each index used

Target:
maximize total query sum by permutation
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
sort values and frequencies same order

Question
        ↓
maximize total query sum by permutation
```

-   **Mathematical Model:**

``` text
sort values and frequencies same order
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
frequency each index used
  ↓ write condition
sort values and frequencies same order
  ↓ simplify / recognize
rearrangement inequality
  ↓
diff array
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `frequency each index used`.
2.  Express the requirement as `sort values and frequencies same order`
    and simplify it to **rearrangement inequality**.
3.  Apply `diff array` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: array,queries

Decode:         Ignore story nouns.
                Keep: frequency each index used

Model:          sort values and frequencies same order

Collapse:       rearrangement inequality

Algorithm:      diff array

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    frequency each index used

MODEL:
    sort values and frequencies same order

SIMPLIFY / TRANSFORM:
    rearrangement inequality

APPLY:
    diff array

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **diff array**; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `sort values and frequencies same order`, think **rearrangement
    inequality** before implementation.

Problem Link: [CF 433B --- Kuriyama Mirai's
Stones](https://codeforces.com/problemset/problem/433/B)

**Problem Summary:** Given `array,queries`, decode the statement into
mathematics and determine `range sums original/sorted`.

### CF 433B --- Kuriyama Mirai's Stones (Prefix Sum / Codeforces / 1200)

-   **Core Invariant / Key Insight:** The story collapses to
    `range=p[r]-p[l-1]`. The key Phase-1 move is to recognize **static
    range query**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
array,queries
target: range sums original/sorted
```

-   **Define Variables:**

``` text
Given:
array,queries

Mathematical objects:
pref and sortedPref

Target:
range sums original/sorted
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
range=p[r]-p[l-1]

Question
        ↓
range sums original/sorted
```

-   **Mathematical Model:**

``` text
range=p[r]-p[l-1]
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
pref and sortedPref
  ↓ write condition
range=p[r]-p[l-1]
  ↓ simplify / recognize
static range query
  ↓
O(1)/query
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `pref and sortedPref`.
2.  Express the requirement as `range=p[r]-p[l-1]` and simplify it to
    **static range query**.
3.  Apply `O(1)/query` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: array,queries

Decode:         Ignore story nouns.
                Keep: pref and sortedPref

Model:          range=p[r]-p[l-1]

Collapse:       static range query

Algorithm:      O(1)/query

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    pref and sortedPref

MODEL:
    range=p[r]-p[l-1]

SIMPLIFY / TRANSFORM:
    static range query

APPLY:
    O(1)/query

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(1)/query**; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `range=p[r]-p[l-1]`, think **static range query** before
    implementation.

Problem Link: [CF 313B --- Ilya and
Queries](https://codeforces.com/problemset/problem/313/B)

**Problem Summary:** Given `string,queries`, decode the statement into
mathematics and determine `count equal adjacent pairs in range`.

### CF 313B --- Ilya and Queries (Prefix / Codeforces / 1100)

-   **Core Invariant / Key Insight:** The story collapses to `prefix b`.
    The key Phase-1 move is to recognize **range sum**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
string,queries
target: count equal adjacent pairs in range
```

-   **Define Variables:**

``` text
Given:
string,queries

Mathematical objects:
b[i]=[s[i]==s[i-1]]

Target:
count equal adjacent pairs in range
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
prefix b

Question
        ↓
count equal adjacent pairs in range
```

-   **Mathematical Model:**

``` text
prefix b
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
b[i]=[s[i]==s[i-1]]
  ↓ write condition
prefix b
  ↓ simplify / recognize
range sum
  ↓
O(n+q)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `b[i]=[s[i]==s[i-1]]`.
2.  Express the requirement as `prefix b` and simplify it to **range
    sum**.
3.  Apply `O(n+q)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: string,queries

Decode:         Ignore story nouns.
                Keep: b[i]=[s[i]==s[i-1]]

Model:          prefix b

Collapse:       range sum

Algorithm:      O(n+q)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    b[i]=[s[i]==s[i-1]]

MODEL:
    prefix b

SIMPLIFY / TRANSFORM:
    range sum

APPLY:
    O(n+q)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(n+q)**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `prefix b`, think **range sum** before implementation.

Problem Link: [CF 327A --- Flipping
Game](https://codeforces.com/problemset/problem/327/A)

**Problem Summary:** Given `binary array`, decode the statement into
mathematics and determine `maximize ones after one flip`.

### CF 327A --- Flipping Game (Transform/Kadane / Codeforces / 1200)

-   **Core Invariant / Key Insight:** The story collapses to
    `max subarray gain`. The key Phase-1 move is to recognize
    **transform then Kadane**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
binary array
target: maximize ones after one flip
```

-   **Define Variables:**

``` text
Given:
binary array

Mathematical objects:
gain: 0→+1,1→-1

Target:
maximize ones after one flip
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
max subarray gain

Question
        ↓
maximize ones after one flip
```

-   **Mathematical Model:**

``` text
max subarray gain
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
gain: 0→+1,1→-1
  ↓ write condition
max subarray gain
  ↓ simplify / recognize
transform then Kadane
  ↓
O(n)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `gain: 0→+1,1→-1`.
2.  Express the requirement as `max subarray gain` and simplify it to
    **transform then Kadane**.
3.  Apply `O(n)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: binary array

Decode:         Ignore story nouns.
                Keep: gain: 0→+1,1→-1

Model:          max subarray gain

Collapse:       transform then Kadane

Algorithm:      O(n)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    gain: 0→+1,1→-1

MODEL:
    max subarray gain

SIMPLIFY / TRANSFORM:
    transform then Kadane

APPLY:
    O(n)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(n)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `max subarray gain`, think **transform then Kadane** before
    implementation.

Problem Link: [CF 580A --- Kefa and First
Steps](https://codeforces.com/problemset/problem/580/A)

**Problem Summary:** Given `array`, decode the statement into
mathematics and determine `longest nondecreasing contiguous segment`.

### CF 580A --- Kefa and First Steps (Run Length / Codeforces / 900)

-   **Core Invariant / Key Insight:** The story collapses to `max run`.
    The key Phase-1 move is to recognize **state**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
array
target: longest nondecreasing contiguous segment
```

-   **Define Variables:**

``` text
Given:
array

Mathematical objects:
current run based on a[i]>=a[i-1]

Target:
longest nondecreasing contiguous segment
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
max run

Question
        ↓
longest nondecreasing contiguous segment
```

-   **Mathematical Model:**

``` text
max run
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
current run based on a[i]>=a[i-1]
  ↓ write condition
max run
  ↓ simplify / recognize
state
  ↓
O(n)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `current run based on a[i]>=a[i-1]`.
2.  Express the requirement as `max run` and simplify it to **state**.
3.  Apply `O(n)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: array

Decode:         Ignore story nouns.
                Keep: current run based on a[i]>=a[i-1]

Model:          max run

Collapse:       state

Algorithm:      O(n)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    current run based on a[i]>=a[i-1]

MODEL:
    max run

SIMPLIFY / TRANSFORM:
    state

APPLY:
    O(n)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(n)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `max run`, think **state** before implementation.

Problem Link: [CF 702A --- Maximum
Increase](https://codeforces.com/problemset/problem/702/A)

**Problem Summary:** Given `array`, decode the statement into
mathematics and determine
`longest strictly increasing contiguous segment`.

### CF 702A --- Maximum Increase (Run Length / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to `max run`.
    The key Phase-1 move is to recognize **state**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
array
target: longest strictly increasing contiguous segment
```

-   **Define Variables:**

``` text
Given:
array

Mathematical objects:
current++ if a[i]>a[i-1]

Target:
longest strictly increasing contiguous segment
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
max run

Question
        ↓
longest strictly increasing contiguous segment
```

-   **Mathematical Model:**

``` text
max run
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
current++ if a[i]>a[i-1]
  ↓ write condition
max run
  ↓ simplify / recognize
state
  ↓
O(n)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `current++ if a[i]>a[i-1]`.
2.  Express the requirement as `max run` and simplify it to **state**.
3.  Apply `O(n)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: array

Decode:         Ignore story nouns.
                Keep: current++ if a[i]>a[i-1]

Model:          max run

Collapse:       state

Algorithm:      O(n)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    current++ if a[i]>a[i-1]

MODEL:
    max run

SIMPLIFY / TRANSFORM:
    state

APPLY:
    O(n)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(n)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `max run`, think **state** before implementation.

Problem Link: [CF 1829B --- Blank
Space](https://codeforces.com/problemset/problem/1829/B)

**Problem Summary:** Given `binary array`, decode the statement into
mathematics and determine `longest zeros`.

### CF 1829B --- Blank Space (Run Length / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to `max`. The
    key Phase-1 move is to recognize **state**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
binary array
target: longest zeros
```

-   **Define Variables:**

``` text
Given:
binary array

Mathematical objects:
current zero run

Target:
longest zeros
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
max

Question
        ↓
longest zeros
```

-   **Mathematical Model:**

``` text
max
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
current zero run
  ↓ write condition
max
  ↓ simplify / recognize
state
  ↓
O(n)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `current zero run`.
2.  Express the requirement as `max` and simplify it to **state**.
3.  Apply `O(n)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: binary array

Decode:         Ignore story nouns.
                Keep: current zero run

Model:          max

Collapse:       state

Algorithm:      O(n)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    current zero run

MODEL:
    max

SIMPLIFY / TRANSFORM:
    state

APPLY:
    O(n)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(n)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to `max`,
    think **state** before implementation.

Problem Link: [CF 1669F --- Eating
Candies](https://codeforces.com/problemset/problem/1669/F)

**Problem Summary:** Given `array`, decode the statement into
mathematics and determine
`max elements eaten with equal left/right sums`.

### CF 1669F --- Eating Candies (Two Pointers/Prefix / Codeforces / 1100)

-   **Core Invariant / Key Insight:** The story collapses to
    `two monotone prefix sums`. The key Phase-1 move is to recognize
    **two pointers**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
array
target: max elements eaten with equal left/right sums
```

-   **Define Variables:**

``` text
Given:
array

Mathematical objects:
grow smaller side sum

Target:
max elements eaten with equal left/right sums
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
two monotone prefix sums

Question
        ↓
max elements eaten with equal left/right sums
```

-   **Mathematical Model:**

``` text
two monotone prefix sums
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
grow smaller side sum
  ↓ write condition
two monotone prefix sums
  ↓ simplify / recognize
two pointers
  ↓
O(n)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `grow smaller side sum`.
2.  Express the requirement as `two monotone prefix sums` and simplify
    it to **two pointers**.
3.  Apply `O(n)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: array

Decode:         Ignore story nouns.
                Keep: grow smaller side sum

Model:          two monotone prefix sums

Collapse:       two pointers

Algorithm:      O(n)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    grow smaller side sum

MODEL:
    two monotone prefix sums

SIMPLIFY / TRANSFORM:
    two pointers

APPLY:
    O(n)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(n)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `two monotone prefix sums`, think **two pointers** before
    implementation.

# Pattern 11 --- Constructive / Reachability Modeling

## Pattern Overview

For 'print any valid answer' problems, turn requirements into
equations/constraints and build the simplest object satisfying them.
Separate necessity from sufficiency.

``` text
Statement wording
      ↓
remove domain story
      ↓
mathematical condition
      ↓
recognize: Constructive / Reachability Modeling
```

Problem Link: [CF 1690A --- Print a
Pedestal](https://codeforces.com/problemset/problem/1690/A)

**Problem Summary:** Given `n`, decode the statement into mathematics
and determine `three distinct positive heights with ordering`.

### CF 1690A --- Print a Pedestal (Constructive / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `near thirds`. The key Phase-1 move is to recognize **construct**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
n
target: three distinct positive heights with ordering
```

-   **Define Variables:**

``` text
Given:
n

Mathematical objects:
a+b+c=n, a<b<c

Target:
three distinct positive heights with ordering
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
near thirds

Question
        ↓
three distinct positive heights with ordering
```

-   **Mathematical Model:**

``` text
near thirds
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
a+b+c=n, a<b<c
  ↓ write condition
near thirds
  ↓ simplify / recognize
construct
  ↓
cases
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `a+b+c=n, a<b<c`.
2.  Express the requirement as `near thirds` and simplify it to
    **construct**.
3.  Apply `cases` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: n

Decode:         Ignore story nouns.
                Keep: a+b+c=n, a<b<c

Model:          near thirds

Collapse:       construct

Algorithm:      cases

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    a+b+c=n, a<b<c

MODEL:
    near thirds

SIMPLIFY / TRANSFORM:
    construct

APPLY:
    cases

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **cases**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `near thirds`, think **construct** before implementation.

Problem Link: [CF 1845A --- Forbidden
Integer](https://codeforces.com/problemset/problem/1845/A)

**Problem Summary:** Given `n,k,x`, decode the statement into
mathematics and determine `sum allowed integers to n`.

### CF 1845A --- Forbidden Integer (Constructive / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `simple basis values`. The key Phase-1 move is to recognize
    **construct**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
n,k,x
target: sum allowed integers to n
```

-   **Define Variables:**

``` text
Given:
n,k,x

Mathematical objects:
choose 1 if allowed else 2/3

Target:
sum allowed integers to n
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
simple basis values

Question
        ↓
sum allowed integers to n
```

-   **Mathematical Model:**

``` text
simple basis values
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
choose 1 if allowed else 2/3
  ↓ write condition
simple basis values
  ↓ simplify / recognize
construct
  ↓
cases
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `choose 1 if allowed else 2/3`.
2.  Express the requirement as `simple basis values` and simplify it to
    **construct**.
3.  Apply `cases` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: n,k,x

Decode:         Ignore story nouns.
                Keep: choose 1 if allowed else 2/3

Model:          simple basis values

Collapse:       construct

Algorithm:      cases

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    choose 1 if allowed else 2/3

MODEL:
    simple basis values

SIMPLIFY / TRANSFORM:
    construct

APPLY:
    cases

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **cases**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `simple basis values`, think **construct** before implementation.

Problem Link: [CF 1878B --- Aleksa and
Stack](https://codeforces.com/problemset/problem/1878/B)

**Problem Summary:** Given `n`, decode the statement into mathematics
and determine `build valid sequence`.

### CF 1878B --- Aleksa and Stack (Constructive / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `satisfy local constraint by design`. The key Phase-1 move is to
    recognize **construction**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
n
target: build valid sequence
```

-   **Define Variables:**

``` text
Given:
n

Mathematical objects:
choose simple constant pattern

Target:
build valid sequence
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
satisfy local constraint by design

Question
        ↓
build valid sequence
```

-   **Mathematical Model:**

``` text
satisfy local constraint by design
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
choose simple constant pattern
  ↓ write condition
satisfy local constraint by design
  ↓ simplify / recognize
construction
  ↓
formula
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `choose simple constant pattern`.
2.  Express the requirement as `satisfy local constraint by design` and
    simplify it to **construction**.
3.  Apply `formula` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: n

Decode:         Ignore story nouns.
                Keep: choose simple constant pattern

Model:          satisfy local constraint by design

Collapse:       construction

Algorithm:      formula

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    choose simple constant pattern

MODEL:
    satisfy local constraint by design

SIMPLIFY / TRANSFORM:
    construction

APPLY:
    formula

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **formula**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `satisfy local constraint by design`, think **construction** before
    implementation.

Problem Link: [CF 1741A --- Compare T-Shirt
Sizes](https://codeforces.com/problemset/problem/1741/A)

**Problem Summary:** Given `size strings`, decode the statement into
mathematics and determine `compare S/M/L with X count`.

### CF 1741A --- Compare T-Shirt Sizes (Ordering / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `map to signed scale`. The key Phase-1 move is to recognize **custom
    ordering**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
size strings
target: compare S/M/L with X count
```

-   **Define Variables:**

``` text
Given:
size strings

Mathematical objects:
L: more X larger; S reverse

Target:
compare S/M/L with X count
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
map to signed scale

Question
        ↓
compare S/M/L with X count
```

-   **Mathematical Model:**

``` text
map to signed scale
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
L: more X larger; S reverse
  ↓ write condition
map to signed scale
  ↓ simplify / recognize
custom ordering
  ↓
O(len)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `L: more X larger; S reverse`.
2.  Express the requirement as `map to signed scale` and simplify it to
    **custom ordering**.
3.  Apply `O(len)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: size strings

Decode:         Ignore story nouns.
                Keep: L: more X larger; S reverse

Model:          map to signed scale

Collapse:       custom ordering

Algorithm:      O(len)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    L: more X larger; S reverse

MODEL:
    map to signed scale

SIMPLIFY / TRANSFORM:
    custom ordering

APPLY:
    O(len)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(len)**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `map to signed scale`, think **custom ordering** before
    implementation.

Problem Link: [CF 1805B --- We Need the Zero / The String Has a
Target](https://codeforces.com/problemset/problem/1805/B)

**Problem Summary:** Given `string`, decode the statement into
mathematics and determine `move smallest char to front under operation`.

### CF 1805B --- We Need the Zero / The String Has a Target (String/Greedy / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `stable reconstruction`. The key Phase-1 move is to recognize
    **greedy**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
string
target: move smallest char to front under operation
```

-   **Define Variables:**

``` text
Given:
string

Mathematical objects:
global min char; choose rightmost occurrence

Target:
move smallest char to front under operation
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
stable reconstruction

Question
        ↓
move smallest char to front under operation
```

-   **Mathematical Model:**

``` text
stable reconstruction
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
global min char; choose rightmost occurrence
  ↓ write condition
stable reconstruction
  ↓ simplify / recognize
greedy
  ↓
O(n)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `global min char; choose rightmost occurrence`.
2.  Express the requirement as `stable reconstruction` and simplify it
    to **greedy**.
3.  Apply `O(n)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: string

Decode:         Ignore story nouns.
                Keep: global min char; choose rightmost occurrence

Model:          stable reconstruction

Collapse:       greedy

Algorithm:      O(n)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    global min char; choose rightmost occurrence

MODEL:
    stable reconstruction

SIMPLIFY / TRANSFORM:
    greedy

APPLY:
    O(n)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(n)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `stable reconstruction`, think **greedy** before implementation.

Problem Link: [CF 1833B --- Restore the
Weather](https://codeforces.com/problemset/problem/1833/B)

**Problem Summary:** Given `arrays a,b,k`, decode the statement into
mathematics and determine `permute b so |a[i]-b[i]|<=k`.

### CF 1833B --- Restore the Weather (Sorting/Matching / Codeforces / 1000)

-   **Core Invariant / Key Insight:** The story collapses to
    `monotone matching`. The key Phase-1 move is to recognize **pair
    sorted orders**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
arrays a,b,k
target: permute b so |a[i]-b[i]|<=k
```

-   **Define Variables:**

``` text
Given:
arrays a,b,k

Mathematical objects:
sort indices by a and b

Target:
permute b so |a[i]-b[i]|<=k
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
monotone matching

Question
        ↓
permute b so |a[i]-b[i]|<=k
```

-   **Mathematical Model:**

``` text
monotone matching
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
sort indices by a and b
  ↓ write condition
monotone matching
  ↓ simplify / recognize
pair sorted orders
  ↓
O(nlogn)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `sort indices by a and b`.
2.  Express the requirement as `monotone matching` and simplify it to
    **pair sorted orders**.
3.  Apply `O(nlogn)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: arrays a,b,k

Decode:         Ignore story nouns.
                Keep: sort indices by a and b

Model:          monotone matching

Collapse:       pair sorted orders

Algorithm:      O(nlogn)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    sort indices by a and b

MODEL:
    monotone matching

SIMPLIFY / TRANSFORM:
    pair sorted orders

APPLY:
    O(nlogn)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(nlogn)**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `monotone matching`, think **pair sorted orders** before
    implementation.

Problem Link: [CF 1793C --- Dora and
Search](https://codeforces.com/problemset/problem/1793/C)

**Problem Summary:** Given `permutation segment`, decode the statement
into mathematics and determine
`find segment whose ends are neither min nor max`.

### CF 1793C --- Dora and Search (Two Pointers/Extremes / Codeforces / 1200)

-   **Core Invariant / Key Insight:** The story collapses to
    `maintain lo,hi`. The key Phase-1 move is to recognize **two
    pointers**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
permutation segment
target: find segment whose ends are neither min nor max
```

-   **Define Variables:**

``` text
Given:
permutation segment

Mathematical objects:
peel if endpoint is current min/max

Target:
find segment whose ends are neither min nor max
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
maintain lo,hi

Question
        ↓
find segment whose ends are neither min nor max
```

-   **Mathematical Model:**

``` text
maintain lo,hi
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
peel if endpoint is current min/max
  ↓ write condition
maintain lo,hi
  ↓ simplify / recognize
two pointers
  ↓
O(n)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `peel if endpoint is current min/max`.
2.  Express the requirement as `maintain lo,hi` and simplify it to **two
    pointers**.
3.  Apply `O(n)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: permutation segment

Decode:         Ignore story nouns.
                Keep: peel if endpoint is current min/max

Model:          maintain lo,hi

Collapse:       two pointers

Algorithm:      O(n)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    peel if endpoint is current min/max

MODEL:
    maintain lo,hi

SIMPLIFY / TRANSFORM:
    two pointers

APPLY:
    O(n)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(n)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `maintain lo,hi`, think **two pointers** before implementation.

Problem Link: [CF 1881A --- Don't Try to
Count](https://codeforces.com/problemset/problem/1881/A)

**Problem Summary:** Given `x,s`, decode the statement into mathematics
and determine `minimum doublings until s substring`.

### CF 1881A --- Don't Try to Count (String/Doubling / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `repeat x until long enough + margin`. The key Phase-1 move is to
    recognize **simulation bound**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
x,s
target: minimum doublings until s substring
```

-   **Define Variables:**

``` text
Given:
x,s

Mathematical objects:
length only needs bounded doublings

Target:
minimum doublings until s substring
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
repeat x until long enough + margin

Question
        ↓
minimum doublings until s substring
```

-   **Mathematical Model:**

``` text
repeat x until long enough + margin
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
length only needs bounded doublings
  ↓ write condition
repeat x until long enough + margin
  ↓ simplify / recognize
simulation bound
  ↓
few iterations
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `length only needs bounded doublings`.
2.  Express the requirement as `repeat x until long enough + margin` and
    simplify it to **simulation bound**.
3.  Apply `few iterations` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: x,s

Decode:         Ignore story nouns.
                Keep: length only needs bounded doublings

Model:          repeat x until long enough + margin

Collapse:       simulation bound

Algorithm:      few iterations

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    length only needs bounded doublings

MODEL:
    repeat x until long enough + margin

SIMPLIFY / TRANSFORM:
    simulation bound

APPLY:
    few iterations

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **few iterations**; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `repeat x until long enough + margin`, think **simulation bound**
    before implementation.

Problem Link: [CF 1858A ---
Buttons](https://codeforces.com/problemset/problem/1858/A)

**Problem Summary:** Given `a,b,c`, decode the statement into
mathematics and determine `winner`.

### CF 1858A --- Buttons (Game/Constructive / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to `parity c`.
    The key Phase-1 move is to recognize **effective counts**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
a,b,c
target: winner
```

-   **Define Variables:**

``` text
Given:
a,b,c

Mathematical objects:
shared c allocated alternately

Target:
winner
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
parity c

Question
        ↓
winner
```

-   **Mathematical Model:**

``` text
parity c
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
shared c allocated alternately
  ↓ write condition
parity c
  ↓ simplify / recognize
effective counts
  ↓
casework
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `shared c allocated alternately`.
2.  Express the requirement as `parity c` and simplify it to **effective
    counts**.
3.  Apply `casework` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: a,b,c

Decode:         Ignore story nouns.
                Keep: shared c allocated alternately

Model:          parity c

Collapse:       effective counts

Algorithm:      casework

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    shared c allocated alternately

MODEL:
    parity c

SIMPLIFY / TRANSFORM:
    effective counts

APPLY:
    casework

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **casework**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `parity c`, think **effective counts** before implementation.

Problem Link: [CF 1899A --- Game with
Integers](https://codeforces.com/problemset/problem/1899/A)

**Problem Summary:** Given `n`, decode the statement into mathematics
and determine `winner`.

### CF 1899A --- Game with Integers (Modulo/Game / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to `n%3`. The
    key Phase-1 move is to recognize **residue game**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
n
target: winner
```

-   **Define Variables:**

``` text
Given:
n

Mathematical objects:
moves ±1; multiples of3 structure

Target:
winner
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
n%3

Question
        ↓
winner
```

-   **Mathematical Model:**

``` text
n%3
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
moves ±1; multiples of3 structure
  ↓ write condition
n%3
  ↓ simplify / recognize
residue game
  ↓
O(1)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `moves ±1; multiples of3 structure`.
2.  Express the requirement as `n%3` and simplify it to **residue
    game**.
3.  Apply `O(1)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: n

Decode:         Ignore story nouns.
                Keep: moves ±1; multiples of3 structure

Model:          n%3

Collapse:       residue game

Algorithm:      O(1)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    moves ±1; multiples of3 structure

MODEL:
    n%3

SIMPLIFY / TRANSFORM:
    residue game

APPLY:
    O(1)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(1)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to `n%3`,
    think **residue game** before implementation.

# Pattern 12 --- Bitwise / XOR Modeling

## Pattern Overview

Translate XOR statements bitwise. Use cancellation `x^x=0`, `x^0=x`, and
remember that XOR is associative/commutative.

``` text
Statement wording
      ↓
remove domain story
      ↓
mathematical condition
      ↓
recognize: Bitwise / XOR Modeling
```

Problem Link: [CF 1805A --- We Need the
Zero](https://codeforces.com/problemset/problem/1805/A)

**Problem Summary:** Given `array`, decode the statement into
mathematics and determine `find x so xor(a[i]^x)=0`.

### CF 1805A --- We Need the Zero (XOR / Codeforces / 900)

-   **Core Invariant / Key Insight:** The story collapses to
    `if n even x cancels; else x=xorAll`. The key Phase-1 move is to
    recognize **parity of n**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
array
target: find x so xor(a[i]^x)=0
```

-   **Define Variables:**

``` text
Given:
array

Mathematical objects:
xorAll ^ (x repeated n times)

Target:
find x so xor(a[i]^x)=0
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
if n even x cancels; else x=xorAll

Question
        ↓
find x so xor(a[i]^x)=0
```

-   **Mathematical Model:**

``` text
if n even x cancels; else x=xorAll
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
xorAll ^ (x repeated n times)
  ↓ write condition
if n even x cancels; else x=xorAll
  ↓ simplify / recognize
parity of n
  ↓
xor
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `xorAll ^ (x repeated n times)`.
2.  Express the requirement as `if n even x cancels; else x=xorAll` and
    simplify it to **parity of n**.
3.  Apply `xor` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: array

Decode:         Ignore story nouns.
                Keep: xorAll ^ (x repeated n times)

Model:          if n even x cancels; else x=xorAll

Collapse:       parity of n

Algorithm:      xor

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    xorAll ^ (x repeated n times)

MODEL:
    if n even x cancels; else x=xorAll

SIMPLIFY / TRANSFORM:
    parity of n

APPLY:
    xor

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **xor**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `if n even x cancels; else x=xorAll`, think **parity of n** before
    implementation.

Problem Link: [CF 1872A --- Two
Vessels](https://codeforces.com/problemset/problem/1872/A)

**Problem Summary:** Given `a,b,c`, decode the statement into
mathematics and determine `min moves balancing transfer c`.

### CF 1872A --- Two Vessels (Arithmetic / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `m*2c>=|a-b|`. The key Phase-1 move is to recognize **ceil
    division**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
a,b,c
target: min moves balancing transfer c
```

-   **Define Variables:**

``` text
Given:
a,b,c

Mathematical objects:
difference shrinks by 2c

Target:
min moves balancing transfer c
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
m*2c>=|a-b|

Question
        ↓
min moves balancing transfer c
```

-   **Mathematical Model:**

``` text
m*2c>=|a-b|
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
difference shrinks by 2c
  ↓ write condition
m*2c>=|a-b|
  ↓ simplify / recognize
ceil division
  ↓
formula
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `difference shrinks by 2c`.
2.  Express the requirement as `m*2c>=|a-b|` and simplify it to **ceil
    division**.
3.  Apply `formula` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: a,b,c

Decode:         Ignore story nouns.
                Keep: difference shrinks by 2c

Model:          m*2c>=|a-b|

Collapse:       ceil division

Algorithm:      formula

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    difference shrinks by 2c

MODEL:
    m*2c>=|a-b|

SIMPLIFY / TRANSFORM:
    ceil division

APPLY:
    formula

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **formula**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `m*2c>=|a-b|`, think **ceil division** before implementation.

Problem Link: [CF 1703A --- YES or
YES?](https://codeforces.com/problemset/problem/1703/A)

**Problem Summary:** Given `word`, decode the statement into mathematics
and determine `case-insensitive equality to yes`.

### CF 1703A --- YES or YES? (String / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to `compare`.
    The key Phase-1 move is to recognize **canonicalization**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
word
target: case-insensitive equality to yes
```

-   **Define Variables:**

``` text
Given:
word

Mathematical objects:
normalize case

Target:
case-insensitive equality to yes
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
compare

Question
        ↓
case-insensitive equality to yes
```

-   **Mathematical Model:**

``` text
compare
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
normalize case
  ↓ write condition
compare
  ↓ simplify / recognize
canonicalization
  ↓
tolower
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `normalize case`.
2.  Express the requirement as `compare` and simplify it to
    **canonicalization**.
3.  Apply `tolower` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: word

Decode:         Ignore story nouns.
                Keep: normalize case

Model:          compare

Collapse:       canonicalization

Algorithm:      tolower

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    normalize case

MODEL:
    compare

SIMPLIFY / TRANSFORM:
    canonicalization

APPLY:
    tolower

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **tolower**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `compare`, think **canonicalization** before implementation.

Problem Link: [CF 1624A --- Plus One on the
Subset](https://codeforces.com/problemset/problem/1624/A)

**Problem Summary:** Given `array`, decode the statement into
mathematics and determine `min ops equalize`.

### CF 1624A --- Plus One on the Subset (Range / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `range max-min`. The key Phase-1 move is to recognize **potential**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
array
target: min ops equalize
```

-   **Define Variables:**

``` text
Given:
array

Mathematical objects:
one op can increment chosen subset

Target:
min ops equalize
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
range max-min

Question
        ↓
min ops equalize
```

-   **Mathematical Model:**

``` text
range max-min
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
one op can increment chosen subset
  ↓ write condition
range max-min
  ↓ simplify / recognize
potential
  ↓
min/max
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `one op can increment chosen subset`.
2.  Express the requirement as `range max-min` and simplify it to
    **potential**.
3.  Apply `min/max` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: array

Decode:         Ignore story nouns.
                Keep: one op can increment chosen subset

Model:          range max-min

Collapse:       potential

Algorithm:      min/max

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    one op can increment chosen subset

MODEL:
    range max-min

SIMPLIFY / TRANSFORM:
    potential

APPLY:
    min/max

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **min/max**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `range max-min`, think **potential** before implementation.

Problem Link: [CF 1220A ---
Cards](https://codeforces.com/problemset/problem/1220/A)

**Problem Summary:** Given `letters`, decode the statement into
mathematics and determine `recover binary digits from letters`.

### CF 1220A --- Cards (Frequency / Codeforces / 900)

-   **Core Invariant / Key Insight:** The story collapses to
    `count z and n`. The key Phase-1 move is to recognize **frequency
    signature**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
letters
target: recover binary digits from letters
```

-   **Define Variables:**

``` text
Given:
letters

Mathematical objects:
'z' uniquely identifies zero, 'n' one after ordering

Target:
recover binary digits from letters
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
count z and n

Question
        ↓
recover binary digits from letters
```

-   **Mathematical Model:**

``` text
count z and n
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
'z' uniquely identifies zero, 'n' one after ordering
  ↓ write condition
count z and n
  ↓ simplify / recognize
frequency signature
  ↓
output
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `'z' uniquely identifies zero, 'n' one after ordering`.
2.  Express the requirement as `count z and n` and simplify it to
    **frequency signature**.
3.  Apply `output` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: letters

Decode:         Ignore story nouns.
                Keep: 'z' uniquely identifies zero, 'n' one after ordering

Model:          count z and n

Collapse:       frequency signature

Algorithm:      output

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    'z' uniquely identifies zero, 'n' one after ordering

MODEL:
    count z and n

SIMPLIFY / TRANSFORM:
    frequency signature

APPLY:
    output

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **output**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `count z and n`, think **frequency signature** before
    implementation.

Problem Link: [CF 1362A --- Johnny and Ancient
Computer](https://codeforces.com/problemset/problem/1362/A)

**Problem Summary:** Given `a,b`, decode the statement into mathematics
and determine `min ×2/4/8 operations to transform`.

### CF 1362A --- Johnny and Ancient Computer (Powers/Ratio / Codeforces / 900)

-   **Core Invariant / Key Insight:** The story collapses to
    `exponent difference grouped by3`. The key Phase-1 move is to
    recognize **factorization**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
a,b
target: min ×2/4/8 operations to transform
```

-   **Define Variables:**

``` text
Given:
a,b

Mathematical objects:
ratio must be power of2

Target:
min ×2/4/8 operations to transform
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
exponent difference grouped by3

Question
        ↓
min ×2/4/8 operations to transform
```

-   **Mathematical Model:**

``` text
exponent difference grouped by3
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
ratio must be power of2
  ↓ write condition
exponent difference grouped by3
  ↓ simplify / recognize
factorization
  ↓
formula
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `ratio must be power of2`.
2.  Express the requirement as `exponent difference grouped by3` and
    simplify it to **factorization**.
3.  Apply `formula` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: a,b

Decode:         Ignore story nouns.
                Keep: ratio must be power of2

Model:          exponent difference grouped by3

Collapse:       factorization

Algorithm:      formula

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    ratio must be power of2

MODEL:
    exponent difference grouped by3

SIMPLIFY / TRANSFORM:
    factorization

APPLY:
    formula

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **formula**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `exponent difference grouped by3`, think **factorization** before
    implementation.

Problem Link: [CF 1095A --- Repeating
Cipher](https://codeforces.com/problemset/problem/1095/A)

**Problem Summary:** Given `encoded string`, decode the statement into
mathematics and determine
`decode chars at positions with jumps 1,2,3...`.

### CF 1095A --- Repeating Cipher (Index Pattern / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `triangular positions`. The key Phase-1 move is to recognize
    **simulation**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
encoded string
target: decode chars at positions with jumps 1,2,3...
```

-   **Define Variables:**

``` text
Given:
encoded string

Mathematical objects:
index += step

Target:
decode chars at positions with jumps 1,2,3...
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
triangular positions

Question
        ↓
decode chars at positions with jumps 1,2,3...
```

-   **Mathematical Model:**

``` text
triangular positions
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
index += step
  ↓ write condition
triangular positions
  ↓ simplify / recognize
simulation
  ↓
O(sqrt n)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `index += step`.
2.  Express the requirement as `triangular positions` and simplify it to
    **simulation**.
3.  Apply `O(sqrt n)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: encoded string

Decode:         Ignore story nouns.
                Keep: index += step

Model:          triangular positions

Collapse:       simulation

Algorithm:      O(sqrt n)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    index += step

MODEL:
    triangular positions

SIMPLIFY / TRANSFORM:
    simulation

APPLY:
    O(sqrt n)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(sqrt n)**; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `triangular positions`, think **simulation** before implementation.

Problem Link: [CF 1324A --- Yet Another Tetris
Problem](https://codeforces.com/problemset/problem/1324/A)

**Problem Summary:** Given `array`, decode the statement into
mathematics and determine `can equalize by subtracting 2`.

### CF 1324A --- Yet Another Tetris Problem (Parity / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `all same parity`. The key Phase-1 move is to recognize **parity
    invariant**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
array
target: can equalize by subtracting 2
```

-   **Define Variables:**

``` text
Given:
array

Mathematical objects:
differences preserve parity

Target:
can equalize by subtracting 2
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
all same parity

Question
        ↓
can equalize by subtracting 2
```

-   **Mathematical Model:**

``` text
all same parity
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
differences preserve parity
  ↓ write condition
all same parity
  ↓ simplify / recognize
parity invariant
  ↓
scan
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `differences preserve parity`.
2.  Express the requirement as `all same parity` and simplify it to
    **parity invariant**.
3.  Apply `scan` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: array

Decode:         Ignore story nouns.
                Keep: differences preserve parity

Model:          all same parity

Collapse:       parity invariant

Algorithm:      scan

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    differences preserve parity

MODEL:
    all same parity

SIMPLIFY / TRANSFORM:
    parity invariant

APPLY:
    scan

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **scan**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `all same parity`, think **parity invariant** before implementation.

Problem Link: [CF 1462A --- Favorite
Sequence](https://codeforces.com/problemset/problem/1462/A)

**Problem Summary:** Given `array`, decode the statement into
mathematics and determine `reorder alternating left/right`.

### CF 1462A --- Favorite Sequence (Two Pointers / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `index pattern`. The key Phase-1 move is to recognize **two
    pointers**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
array
target: reorder alternating left/right
```

-   **Define Variables:**

``` text
Given:
array

Mathematical objects:
take l,r,l+1,r-1

Target:
reorder alternating left/right
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
index pattern

Question
        ↓
reorder alternating left/right
```

-   **Mathematical Model:**

``` text
index pattern
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
take l,r,l+1,r-1
  ↓ write condition
index pattern
  ↓ simplify / recognize
two pointers
  ↓
O(n)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `take l,r,l+1,r-1`.
2.  Express the requirement as `index pattern` and simplify it to **two
    pointers**.
3.  Apply `O(n)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: array

Decode:         Ignore story nouns.
                Keep: take l,r,l+1,r-1

Model:          index pattern

Collapse:       two pointers

Algorithm:      O(n)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    take l,r,l+1,r-1

MODEL:
    index pattern

SIMPLIFY / TRANSFORM:
    two pointers

APPLY:
    O(n)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(n)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `index pattern`, think **two pointers** before implementation.

Problem Link: [CF 1619A --- Polycarp and Sums of Subsequences / Square
String?](https://codeforces.com/problemset/problem/1619/A)

**Problem Summary:** Given `s`, decode the statement into mathematics
and determine `is s two equal halves`.

### CF 1619A --- Polycarp and Sums of Subsequences / Square String? (String / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `equation on substrings`. The key Phase-1 move is to recognize
    **direct**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
s
target: is s two equal halves
```

-   **Define Variables:**

``` text
Given:
s

Mathematical objects:
len even and first half=second

Target:
is s two equal halves
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
equation on substrings

Question
        ↓
is s two equal halves
```

-   **Mathematical Model:**

``` text
equation on substrings
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
len even and first half=second
  ↓ write condition
equation on substrings
  ↓ simplify / recognize
direct
  ↓
O(n)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `len even and first half=second`.
2.  Express the requirement as `equation on substrings` and simplify it
    to **direct**.
3.  Apply `O(n)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: s

Decode:         Ignore story nouns.
                Keep: len even and first half=second

Model:          equation on substrings

Collapse:       direct

Algorithm:      O(n)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    len even and first half=second

MODEL:
    equation on substrings

SIMPLIFY / TRANSFORM:
    direct

APPLY:
    O(n)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(n)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `equation on substrings`, think **direct** before implementation.

# Pattern 13 --- Mixed Blind Decoding

## Pattern Overview

These mix two basic ideas. The goal is to practice the full pipeline
without relying on a topic label.

``` text
Statement wording
      ↓
remove domain story
      ↓
mathematical condition
      ↓
recognize: Mixed Blind Decoding
```

Problem Link: [CF 1538C --- Challenging Cliffs / Number of
Pairs](https://codeforces.com/problemset/problem/1538/C)

**Problem Summary:** Given `array,l,r`, decode the statement into
mathematics and determine `count pair sums in interval`.

### CF 1538C --- Challenging Cliffs / Number of Pairs (Sorting+Counting / Codeforces / 1300)

-   **Core Invariant / Key Insight:** The story collapses to
    `sort + two pointers`. The key Phase-1 move is to recognize **count
    bounded pairs**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
array,l,r
target: count pair sums in interval
```

-   **Define Variables:**

``` text
Given:
array,l,r

Mathematical objects:
F(r)-F(l-1)

Target:
count pair sums in interval
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
sort + two pointers

Question
        ↓
count pair sums in interval
```

-   **Mathematical Model:**

``` text
sort + two pointers
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
F(r)-F(l-1)
  ↓ write condition
sort + two pointers
  ↓ simplify / recognize
count bounded pairs
  ↓
O(nlogn)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `F(r)-F(l-1)`.
2.  Express the requirement as `sort + two pointers` and simplify it to
    **count bounded pairs**.
3.  Apply `O(nlogn)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: array,l,r

Decode:         Ignore story nouns.
                Keep: F(r)-F(l-1)

Model:          sort + two pointers

Collapse:       count bounded pairs

Algorithm:      O(nlogn)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    F(r)-F(l-1)

MODEL:
    sort + two pointers

SIMPLIFY / TRANSFORM:
    count bounded pairs

APPLY:
    O(nlogn)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(nlogn)**; avoid memorizing
    the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `sort + two pointers`, think **count bounded pairs** before
    implementation.

Problem Link: [CF 1475B --- New Year's
Number](https://codeforces.com/problemset/problem/1475/B)

**Problem Summary:** Given `n`, decode the statement into mathematics
and determine `2020a+2021b=n`.

### CF 1475B --- New Year's Number (Diophantine+Modulo / Codeforces / 900)

-   **Core Invariant / Key Insight:** The story collapses to
    `b=n%2020 candidate`. The key Phase-1 move is to recognize
    **feasibility**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
n
target: 2020a+2021b=n
```

-   **Define Variables:**

``` text
Given:
n

Mathematical objects:
2021=2020+1

Target:
2020a+2021b=n
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
b=n%2020 candidate

Question
        ↓
2020a+2021b=n
```

-   **Mathematical Model:**

``` text
b=n%2020 candidate
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
2021=2020+1
  ↓ write condition
b=n%2020 candidate
  ↓ simplify / recognize
feasibility
  ↓
O(1)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `2021=2020+1`.
2.  Express the requirement as `b=n%2020 candidate` and simplify it to
    **feasibility**.
3.  Apply `O(1)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: n

Decode:         Ignore story nouns.
                Keep: 2021=2020+1

Model:          b=n%2020 candidate

Collapse:       feasibility

Algorithm:      O(1)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    2021=2020+1

MODEL:
    b=n%2020 candidate

SIMPLIFY / TRANSFORM:
    feasibility

APPLY:
    O(1)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(1)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `b=n%2020 candidate`, think **feasibility** before implementation.

Problem Link: [CF 1374A --- Required
Remainder](https://codeforces.com/problemset/problem/1374/A)

**Problem Summary:** Given `x,y,n`, decode the statement into
mathematics and determine `largest k<=n with k%x=y`.

### CF 1374A --- Required Remainder (Modulo+Optimization / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `maximize t under bound`. The key Phase-1 move is to recognize
    **floor**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
x,y,n
target: largest k<=n with k%x=y
```

-   **Define Variables:**

``` text
Given:
x,y,n

Mathematical objects:
k=tx+y

Target:
largest k<=n with k%x=y
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
maximize t under bound

Question
        ↓
largest k<=n with k%x=y
```

-   **Mathematical Model:**

``` text
maximize t under bound
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
k=tx+y
  ↓ write condition
maximize t under bound
  ↓ simplify / recognize
floor
  ↓
O(1)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `k=tx+y`.
2.  Express the requirement as `maximize t under bound` and simplify it
    to **floor**.
3.  Apply `O(1)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: x,y,n

Decode:         Ignore story nouns.
                Keep: k=tx+y

Model:          maximize t under bound

Collapse:       floor

Algorithm:      O(1)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    k=tx+y

MODEL:
    maximize t under bound

SIMPLIFY / TRANSFORM:
    floor

APPLY:
    O(1)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(1)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `maximize t under bound`, think **floor** before implementation.

Problem Link: [CF 1551A --- Polycarp and
Coins](https://codeforces.com/problemset/problem/1551/A)

**Problem Summary:** Given `n`, decode the statement into mathematics
and determine `c1+2c2=n with counts close`.

### CF 1551A --- Polycarp and Coins (Equation+Balancing / Codeforces / 800)

-   **Core Invariant / Key Insight:** The story collapses to
    `n%3 cases`. The key Phase-1 move is to recognize **construct
    counts**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
n
target: c1+2c2=n with counts close
```

-   **Define Variables:**

``` text
Given:
n

Mathematical objects:
near n/3

Target:
c1+2c2=n with counts close
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
n%3 cases

Question
        ↓
c1+2c2=n with counts close
```

-   **Mathematical Model:**

``` text
n%3 cases
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
near n/3
  ↓ write condition
n%3 cases
  ↓ simplify / recognize
construct counts
  ↓
O(1)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `near n/3`.
2.  Express the requirement as `n%3 cases` and simplify it to
    **construct counts**.
3.  Apply `O(1)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: n

Decode:         Ignore story nouns.
                Keep: near n/3

Model:          n%3 cases

Collapse:       construct counts

Algorithm:      O(1)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    near n/3

MODEL:
    n%3 cases

SIMPLIFY / TRANSFORM:
    construct counts

APPLY:
    O(1)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(1)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `n%3 cases`, think **construct counts** before implementation.

Problem Link: [CF 1593B --- Make it Divisible by
25](https://codeforces.com/problemset/problem/1593/B)

**Problem Summary:** Given `digits`, decode the statement into
mathematics and determine `min deletions`.

### CF 1593B --- Make it Divisible by 25 (Divisibility+String / Codeforces / 900)

-   **Core Invariant / Key Insight:** The story collapses to
    `search from right`. The key Phase-1 move is to recognize **four
    targets**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
digits
target: min deletions
```

-   **Define Variables:**

``` text
Given:
digits

Mathematical objects:
last2 digits pattern

Target:
min deletions
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
search from right

Question
        ↓
min deletions
```

-   **Mathematical Model:**

``` text
search from right
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
last2 digits pattern
  ↓ write condition
search from right
  ↓ simplify / recognize
four targets
  ↓
O(n)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `last2 digits pattern`.
2.  Express the requirement as `search from right` and simplify it to
    **four targets**.
3.  Apply `O(n)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: digits

Decode:         Ignore story nouns.
                Keep: last2 digits pattern

Model:          search from right

Collapse:       four targets

Algorithm:      O(n)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    last2 digits pattern

MODEL:
    search from right

SIMPLIFY / TRANSFORM:
    four targets

APPLY:
    O(n)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(n)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `search from right`, think **four targets** before implementation.

Problem Link: [CF 1669F --- Eating
Candies](https://codeforces.com/problemset/problem/1669/F)

**Problem Summary:** Given `array`, decode the statement into
mathematics and determine `equal left/right eaten sum maximize count`.

### CF 1669F --- Eating Candies (Prefix+Two Pointers / Codeforces / 1100)

-   **Core Invariant / Key Insight:** The story collapses to
    `advance smaller side`. The key Phase-1 move is to recognize **two
    pointers**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
array
target: equal left/right eaten sum maximize count
```

-   **Define Variables:**

``` text
Given:
array

Mathematical objects:
monotone sums

Target:
equal left/right eaten sum maximize count
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
advance smaller side

Question
        ↓
equal left/right eaten sum maximize count
```

-   **Mathematical Model:**

``` text
advance smaller side
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
monotone sums
  ↓ write condition
advance smaller side
  ↓ simplify / recognize
two pointers
  ↓
O(n)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `monotone sums`.
2.  Express the requirement as `advance smaller side` and simplify it to
    **two pointers**.
3.  Apply `O(n)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: array

Decode:         Ignore story nouns.
                Keep: monotone sums

Model:          advance smaller side

Collapse:       two pointers

Algorithm:      O(n)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    monotone sums

MODEL:
    advance smaller side

SIMPLIFY / TRANSFORM:
    two pointers

APPLY:
    O(n)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(n)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `advance smaller side`, think **two pointers** before
    implementation.

Problem Link: [CF 1793C --- Dora and
Search](https://codeforces.com/problemset/problem/1793/C)

**Problem Summary:** Given `permutation`, decode the statement into
mathematics and determine `find non-extreme-ended segment`.

### CF 1793C --- Dora and Search (Extremes+Two Pointers / Codeforces / 1200)

-   **Core Invariant / Key Insight:** The story collapses to
    `lo/hi invariant`. The key Phase-1 move is to recognize **two
    pointers**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
permutation
target: find non-extreme-ended segment
```

-   **Define Variables:**

``` text
Given:
permutation

Mathematical objects:
peel min/max endpoints

Target:
find non-extreme-ended segment
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
lo/hi invariant

Question
        ↓
find non-extreme-ended segment
```

-   **Mathematical Model:**

``` text
lo/hi invariant
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
peel min/max endpoints
  ↓ write condition
lo/hi invariant
  ↓ simplify / recognize
two pointers
  ↓
O(n)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `peel min/max endpoints`.
2.  Express the requirement as `lo/hi invariant` and simplify it to
    **two pointers**.
3.  Apply `O(n)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: permutation

Decode:         Ignore story nouns.
                Keep: peel min/max endpoints

Model:          lo/hi invariant

Collapse:       two pointers

Algorithm:      O(n)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    peel min/max endpoints

MODEL:
    lo/hi invariant

SIMPLIFY / TRANSFORM:
    two pointers

APPLY:
    O(n)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(n)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `lo/hi invariant`, think **two pointers** before implementation.

Problem Link: [CF 327A --- Flipping
Game](https://codeforces.com/problemset/problem/327/A)

**Problem Summary:** Given `binary array`, decode the statement into
mathematics and determine `one flip maximize ones`.

### CF 327A --- Flipping Game (Transform+Optimization / Codeforces / 1200)

-   **Core Invariant / Key Insight:** The story collapses to
    `maximum subarray`. The key Phase-1 move is to recognize **Kadane**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
binary array
target: one flip maximize ones
```

-   **Define Variables:**

``` text
Given:
binary array

Mathematical objects:
gain map 0→+1,1→-1

Target:
one flip maximize ones
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
maximum subarray

Question
        ↓
one flip maximize ones
```

-   **Mathematical Model:**

``` text
maximum subarray
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
gain map 0→+1,1→-1
  ↓ write condition
maximum subarray
  ↓ simplify / recognize
Kadane
  ↓
O(n)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `gain map 0→+1,1→-1`.
2.  Express the requirement as `maximum subarray` and simplify it to
    **Kadane**.
3.  Apply `O(n)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: binary array

Decode:         Ignore story nouns.
                Keep: gain map 0→+1,1→-1

Model:          maximum subarray

Collapse:       Kadane

Algorithm:      O(n)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    gain map 0→+1,1→-1

MODEL:
    maximum subarray

SIMPLIFY / TRANSFORM:
    Kadane

APPLY:
    O(n)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(n)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `maximum subarray`, think **Kadane** before implementation.

Problem Link: [CF 1520D --- Same
Differences](https://codeforces.com/problemset/problem/1520/D)

**Problem Summary:** Given `array`, decode the statement into
mathematics and determine `count special pairs`.

### CF 1520D --- Same Differences (Algebra+Frequency / Codeforces / 1200)

-   **Core Invariant / Key Insight:** The story collapses to
    `equal transformed keys`. The key Phase-1 move is to recognize
    **hash frequency**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
array
target: count special pairs
```

-   **Define Variables:**

``` text
Given:
array

Mathematical objects:
a[j]-j=a[i]-i

Target:
count special pairs
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
equal transformed keys

Question
        ↓
count special pairs
```

-   **Mathematical Model:**

``` text
equal transformed keys
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
a[j]-j=a[i]-i
  ↓ write condition
equal transformed keys
  ↓ simplify / recognize
hash frequency
  ↓
O(n)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only `a[j]-j=a[i]-i`.
2.  Express the requirement as `equal transformed keys` and simplify it
    to **hash frequency**.
3.  Apply `O(n)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: array

Decode:         Ignore story nouns.
                Keep: a[j]-j=a[i]-i

Model:          equal transformed keys

Collapse:       hash frequency

Algorithm:      O(n)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    a[j]-j=a[i]-i

MODEL:
    equal transformed keys

SIMPLIFY / TRANSFORM:
    hash frequency

APPLY:
    O(n)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O(n)**; avoid memorizing the
    original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `equal transformed keys`, think **hash frequency** before
    implementation.

Problem Link: [CF 276C --- Little Girl and Maximum
Sum](https://codeforces.com/problemset/problem/276/C)

**Problem Summary:** Given `array,range queries`, decode the statement
into mathematics and determine `maximize weighted sum`.

### CF 276C --- Little Girl and Maximum Sum (Difference+Sorting / Codeforces / 1400)

-   **Core Invariant / Key Insight:** The story collapses to
    `sort both sequences`. The key Phase-1 move is to recognize
    **rearrangement**.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
story-specific people / objects / names       ❌

KEEP:
array,range queries
target: maximize weighted sum
```

-   **Define Variables:**

``` text
Given:
array,range queries

Mathematical objects:
usage frequency per index

Target:
maximize weighted sum
```

-   **English → Mathematics:**

``` text
Statement requirement
        ↓
sort both sequences

Question
        ↓
maximize weighted sum
```

-   **Mathematical Model:**

``` text
sort both sequences
```

-   **Mathematical Collapse:**

``` text
Story
  ↓ remove nouns
usage frequency per index
  ↓ write condition
sort both sequences
  ↓ simplify / recognize
rearrangement
  ↓
O((n+q)logn)
```

-   **Step-by-Step Logic:**

1.  Remove the story vocabulary and retain only
    `usage frequency per index`.
2.  Express the requirement as `sort both sequences` and simplify it to
    **rearrangement**.
3.  Apply `O((n+q)logn)` and output the required result.

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        Read: array,range queries

Decode:         Ignore story nouns.
                Keep: usage frequency per index

Model:          sort both sequences

Collapse:       rearrangement

Algorithm:      O((n+q)logn)

Final State:    Story → standard mathematical form.
```

-   **Pseudocode:**

``` text
READ required input

EXTRACT:
    usage frequency per index

MODEL:
    sort both sequences

SIMPLIFY / TRANSFORM:
    rearrangement

APPLY:
    O((n+q)logn)

PRINT answer
```

-   **C++17 Implementation Note:** Re-solve from the model first. The
    implementation should directly encode **O((n+q)logn)**; avoid
    memorizing the original story.

-   **Recognition Trigger:** When a fresh statement reduces to
    `sort both sequences`, think **rearrangement** before
    implementation.

# Phase-1 Recognition Checklist

Before writing C++, answer these in order:

``` text
1. Which nouns are only story?
2. What mathematical objects remain?
3. What is GIVEN?
4. What is UNKNOWN?
5. What is the exact TARGET?
6. Which phrases translate to =, <, >, <=, >=, %, gcd, xor, parity?
7. If there is an operation: BEFORE → AFTER?
8. What changes?
9. What cannot change?
10. Can I write one equation / inequality?
11. Can I rearrange or normalize it?
12. Can I replace values by parity / remainder / frequency / key?
13. Does sorting remove irrelevant order?
14. Is there a lower or upper bound?
15. Is that bound achievable?
16. What standard mathematical form appeared?
17. Only now: what algorithm/data structure implements it?
```

# Reusable Problem Template

Problem Link: `[Clickable Problem Link]`

**Problem Summary:** Explain what is given and what must be found in
1--2 sentences.

### \[Problem Name\] (\[Topic\] / Codeforces / \[Rating\])

-   **Core Invariant / Key Insight:** State the mathematical collapse.

-   **Decode the Statement --- Remove Nouns:**

``` text
REMOVE:
... ❌

KEEP:
...
```

-   **Define Variables:**

``` text
Given:
...

Unknown:
...

Target:
...
```

-   **English → Mathematics:**

``` text
"important phrase"
        ↓
equation / inequality / state
```

-   **Mathematical Model:**

``` text
...
```

-   **Mathematical Collapse:**

``` text
Story
  ↓
Variables
  ↓
Condition
  ↓
Transformation
  ↓
Known form
```

-   **Step-by-Step Logic:**

1.  ...
2.  ...
3.  ...

-   **ASCII Execution Trace / Visual Dry Run:**

``` text
Initial:        ...
Decode:         ...
Model:          ...
Transform:      ...
Final Answer:   ...
```

-   **Pseudocode:**

``` text
READ ...
MODEL ...
TRANSFORM ...
COMPUTE ...
PRINT ...
```

-   **Recognition Trigger:** When you see `________`, test `________`.

# End of Phase 1

Do not measure success only by AC count. Measure whether a fresh
statement becomes:

``` text
NOUNS REMOVED
      ↓
VARIABLES
      ↓
TARGET
      ↓
MATHEMATICAL CONDITION
      ↓
TRANSFORMATION
      ↓
KNOWN FORM
```

without tags or editorial.
