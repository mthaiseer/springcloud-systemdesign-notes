# 03 — CF Mathematical Modeling Phase 3: DISCOVER

> **Goal:** discover the mathematical observation yourself when the form
> is not immediately visible.

``` text
STATEMENT
   ↓
REMOVE STORY
   ↓
VARIABLES + CONSTRAINT
   ↓
SMALL TEST CASES
   ↓
OBSERVE PATTERN
   ↓
FORM HYPOTHESIS
   ↓
DERIVE / PROVE
   ↓
CODE
```

------------------------------------------------------------------------

## How to Use

Phase 2 asked:

``` text
"What mathematical form is hidden here?"
```

Phase 3 asks:

``` text
"I don't see the form yet. How can I DISCOVER it?"
```

Before coding each problem, fill this:

``` text
1. Problem asking:
2. Story → variables:
3. Fix tiny values:
4. Test smallest n / boundary cases:
5. Record outputs / behavior:
6. What changes?
7. What stays invariant?
8. Try extremes / parity / modulo / difference / rearrangement:
9. Hypothesis:
10. Algebraic derivation / proof:
11. Final formula / algorithm:
```

**Rule:** do not open editorial or tags before completing the discovery
attempt.

------------------------------------------------------------------------

## Discovery Toolbox

When stuck, try these in order:

``` text
A. n = 1, 2, 3, 4, 5
B. values = 0, 1, 2, small K
C. equal values
D. minimum / maximum values
E. sorted order
F. odd vs even
G. remainder classes
H. difference between two states
I. operation delta
J. invariant
K. rewrite pair equation
L. count contribution
M. brute-force tiny cases and inspect sequence
```

------------------------------------------------------------------------

## Phase 3A — Small Cases → Formula

|  \# | Problem                                                                            | Rating | Your Observation | Solved |
|----:|------------------------------------------------------------------------------------|-------:|------------------|:------:|
|   1 | [CF 1353A — Most Unstable Array](https://codeforces.com/problemset/problem/1353/A) |    800 |                  |   ☐    |
|   2 | [CF 617A — Elephant](https://codeforces.com/problemset/problem/617/A)              |    800 |                  |   ☐    |
|   3 | [CF 1476A — K-divisible Sum](https://codeforces.com/problemset/problem/1476/A)     |   1000 |                  |   ☐    |
|   4 | [CF 1370A — Maximum GCD](https://codeforces.com/problemset/problem/1370/A)         |    800 |                  |   ☐    |
|   5 | [CF 1551A — Polycarp and Coins](https://codeforces.com/problemset/problem/1551/A)  |    800 |                  |   ☐    |
|   6 | [CF 1343A — Candies](https://codeforces.com/problemset/problem/1343/A)             |    900 |                  |   ☐    |
|   7 | [CF 318A — Even Odds](https://codeforces.com/problemset/problem/318/A)             |    900 |                  |   ☐    |
|   8 | [CF 486A — Calculating Function](https://codeforces.com/problemset/problem/486/A)  |    800 |                  |   ☐    |

### Discovery target

``` text
small cases
→ output sequence
→ piecewise behavior
→ closed formula
```

------------------------------------------------------------------------

## Phase 3B — Extreme Values → Min / Max Observation

|  \# | Problem                                                                               | Rating | Your Observation | Solved |
|----:|---------------------------------------------------------------------------------------|-------:|------------------|:------:|
|   1 | [CF 1353A — Most Unstable Array](https://codeforces.com/problemset/problem/1353/A)    |    800 |                  |   ☐    |
|   2 | [CF 1593A — Elections](https://codeforces.com/problemset/problem/1593/A)              |    800 |                  |   ☐    |
|   3 | [CF 1624A — Plus One on the Subset](https://codeforces.com/problemset/problem/1624/A) |    800 |                  |   ☐    |
|   4 | [CF 1760A — Medium Number](https://codeforces.com/problemset/problem/1760/A)          |    800 |                  |   ☐    |
|   5 | [CF 160A — Twins](https://codeforces.com/problemset/problem/160/A)                    |    900 |                  |   ☐    |
|   6 | [CF 734B — Anton and Digits](https://codeforces.com/problemset/problem/734/B)         |    800 |                  |   ☐    |
|   7 | [CF 1838A — Blackboard List](https://codeforces.com/problemset/problem/1838/A)        |    800 |                  |   ☐    |
|   8 | [CF 1793C — Dora and Search](https://codeforces.com/problemset/problem/1793/C)        |   1200 |                  |   ☐    |

### Discovery target

``` text
maximize / minimize
→ ask what happens at extremes
→ LOW / HIGH / MIN / MAX
→ derive why middle values cannot improve answer
```

------------------------------------------------------------------------

## Phase 3C — Operation → Delta → Invariant

|  \# | Problem                                                                                    | Rating | Delta / Invariant Found | Solved |
|----:|--------------------------------------------------------------------------------------------|-------:|-------------------------|:------:|
|   1 | [CF 1834A — Unit Array](https://codeforces.com/problemset/problem/1834/A)                  |    800 |                         |   ☐    |
|   2 | [CF 1367B — Even Array](https://codeforces.com/problemset/problem/1367/B)                  |    800 |                         |   ☐    |
|   3 | [CF 1324A — Yet Another Tetris Problem](https://codeforces.com/problemset/problem/1324/A)  |    800 |                         |   ☐    |
|   4 | [CF 1855A — Dalton the Teacher](https://codeforces.com/problemset/problem/1855/A)          |    800 |                         |   ☐    |
|   5 | [CF 660A — Co-prime Array](https://codeforces.com/problemset/problem/660/A)                |    900 |                         |   ☐    |
|   6 | [CF 1829D — Gold Rush](https://codeforces.com/problemset/problem/1829/D)                   |   1000 |                         |   ☐    |
|   7 | [CF 1362A — Johnny and Ancient Computer](https://codeforces.com/problemset/problem/1362/A) |    900 |                         |   ☐    |

### Discovery target

For every operation write:

``` text
before = ?
after  = ?

delta = after - before

What changes?
What cannot change?
What states are reachable?
```

------------------------------------------------------------------------

## Phase 3D — Pair Relation → Common Key

|  \# | Problem                                                                         | Rating | Extracted Key | Solved |
|----:|---------------------------------------------------------------------------------|-------:|---------------|:------:|
|   1 | [CF 1520D — Same Differences](https://codeforces.com/problemset/problem/1520/D) |   1200 |               |   ☐    |
|   2 | [CF 1538C — Number of Pairs](https://codeforces.com/problemset/problem/1538/C)  |   1300 |               |   ☐    |
|   3 | [CF 1669F — Eating Candies](https://codeforces.com/problemset/problem/1669/F)   |   1100 |               |   ☐    |
|   4 | [CF 1543A — Exciting Bets](https://codeforces.com/problemset/problem/1543/A)    |    900 |               |   ☐    |

### Discovery target

Example transformation:

``` text
a[j] - a[i] = j - i

move same-index terms together:

a[j] - j = a[i] - i

define:

key[i] = a[i] - i

pair condition
→ equal keys
→ frequency counting
```

Do not memorize only the final key. Practice **moving the terms
yourself**.

------------------------------------------------------------------------

## Phase 3E — Parity / Modulo State Compression

|  \# | Problem                                                                                | Rating | Reduced State | Solved |
|----:|----------------------------------------------------------------------------------------|-------:|---------------|:------:|
|   1 | [CF 1296A — Array with Odd Sum](https://codeforces.com/problemset/problem/1296/A)      |    800 |               |   ☐    |
|   2 | [CF 1857A — Array Coloring](https://codeforces.com/problemset/problem/1857/A)          |    800 |               |   ☐    |
|   3 | [CF 1475A — Odd Divisor](https://codeforces.com/problemset/problem/1475/A)             |    900 |               |   ☐    |
|   4 | [CF 1669C — Odd/Even Increments](https://codeforces.com/problemset/problem/1669/C)     |    800 |               |   ☐    |
|   5 | [CF 1899A — Game with Integers](https://codeforces.com/problemset/problem/1899/A)      |    800 |               |   ☐    |
|   6 | [CF 1374A — Required Remainder](https://codeforces.com/problemset/problem/1374/A)      |    800 |               |   ☐    |
|   7 | [CF 1593B — Make it Divisible by 25](https://codeforces.com/problemset/problem/1593/B) |    900 |               |   ☐    |
|   8 | [CF 1475B — New Year's Number](https://codeforces.com/problemset/problem/1475/B)       |    900 |               |   ☐    |

### Discovery target

``` text
large values
→ exact magnitude may not matter
→ reduce state

x → x % 2
x → x % k
number → last digits
number → prime-factor exponents
```

------------------------------------------------------------------------

## Phase 3F — Prefix / Contribution Discovery

|  \# | Problem                                                                                  | Rating | Discovered Transformation | Solved |
|----:|------------------------------------------------------------------------------------------|-------:|---------------------------|:------:|
|   1 | [CF 363B — Fence](https://codeforces.com/problemset/problem/363/B)                       |   1100 |                           |   ☐    |
|   2 | [CF 313B — Ilya and Queries](https://codeforces.com/problemset/problem/313/B)            |   1100 |                           |   ☐    |
|   3 | [CF 433B — Kuriyama Mirai's Stones](https://codeforces.com/problemset/problem/433/B)     |   1200 |                           |   ☐    |
|   4 | [CF 276C — Little Girl and Maximum Sum](https://codeforces.com/problemset/problem/276/C) |   1400 |                           |   ☐    |
|   5 | [CF 327A — Flipping Game](https://codeforces.com/problemset/problem/327/A)               |   1200 |                           |   ☐    |

### Discovery target

Ask:

``` text
Am I recomputing the same range information?
        ↓
Can I store cumulative information?

OR

Instead of processing every query separately:
How many times does each element contribute?
```

------------------------------------------------------------------------

## Phase 3G — Constructive Discovery

|  \# | Problem                                                                            | Rating | Construction Rule | Solved |
|----:|------------------------------------------------------------------------------------|-------:|-------------------|:------:|
|   1 | [CF 1845A — Forbidden Integer](https://codeforces.com/problemset/problem/1845/A)   |    800 |                   |   ☐    |
|   2 | [CF 1690A — Print a Pedestal](https://codeforces.com/problemset/problem/1690/A)    |    800 |                   |   ☐    |
|   3 | [CF 1878B — Aleksa and Stack](https://codeforces.com/problemset/problem/1878/B)    |    800 |                   |   ☐    |
|   4 | [CF 1862B — Sequence Game](https://codeforces.com/problemset/problem/1862/B)       |    800 |                   |   ☐    |
|   5 | [CF 1833B — Restore the Weather](https://codeforces.com/problemset/problem/1833/B) |   1000 |                   |   ☐    |
|   6 | [CF 1551A — Polycarp and Coins](https://codeforces.com/problemset/problem/1551/A)  |    800 |                   |   ☐    |

### Discovery target

``` text
Need ANY valid answer
        ↓
derive mandatory conditions
        ↓
choose simplest values satisfying them
        ↓
prove construction always works
```

------------------------------------------------------------------------

## Phase 3H — Mixed Discovery: No Hint

Do not label these by form before solving.

|  \# | Problem                                                                                  | Rating | Your Model / Observation | Solved |
|----:|------------------------------------------------------------------------------------------|-------:|--------------------------|:------:|
|   1 | [CF 1476A — K-divisible Sum](https://codeforces.com/problemset/problem/1476/A)           |   1000 |                          |   ☐    |
|   2 | [CF 1520D — Same Differences](https://codeforces.com/problemset/problem/1520/D)          |   1200 |                          |   ☐    |
|   3 | [CF 1543A — Exciting Bets](https://codeforces.com/problemset/problem/1543/A)             |    900 |                          |   ☐    |
|   4 | [CF 1593B — Make it Divisible by 25](https://codeforces.com/problemset/problem/1593/B)   |    900 |                          |   ☐    |
|   5 | [CF 1669F — Eating Candies](https://codeforces.com/problemset/problem/1669/F)            |   1100 |                          |   ☐    |
|   6 | [CF 1793C — Dora and Search](https://codeforces.com/problemset/problem/1793/C)           |   1200 |                          |   ☐    |
|   7 | [CF 1881D — Divide and Equalize](https://codeforces.com/problemset/problem/1881/D)       |   1200 |                          |   ☐    |
|   8 | [CF 327A — Flipping Game](https://codeforces.com/problemset/problem/327/A)               |   1200 |                          |   ☐    |
|   9 | [CF 276C — Little Girl and Maximum Sum](https://codeforces.com/problemset/problem/276/C) |   1400 |                          |   ☐    |
|  10 | [CF 1805A — We Need the Zero](https://codeforces.com/problemset/problem/1805/A)          |    900 |                          |   ☐    |

------------------------------------------------------------------------

## Required Discovery Note for Every Problem

Use this compact template:

``` text
### Problem

1. Problem Asking
   →

2. Story → Variables
   →

3. Small Cases
   n=1 →
   n=2 →
   n=3 →
   n=4 →

4. Observed Pattern
   →

5. Candidate Mathematical Model
   →

6. Derivation
   →

7. Why It Works
   →

8. Final Formula / Algorithm
   →
```

If `n=1,2,3...` does not naturally apply, replace it with the smallest
meaningful input values or boundary cases.

------------------------------------------------------------------------

## Example Discovery — CF 1353A

Do not begin with:

``` text
answer = min(2,n-1) * m
```

Discover it:

``` text
fix m = 10

n=1 → [10]         → 0
n=2 → [0,10]       → 10
n=3 → [0,10,0]     → 20
n=4 → [0,10,0,0]   → 20
n=5 → [0,4,0,6,0]  → 20
```

Record:

``` text
0, m, 2m, 2m, 2m...
```

Factor out `m`:

``` text
0, 1, 2, 2, 2...
```

Recognize:

``` text
multiplier = min(2,n-1)
```

Therefore:

``` text
answer = min(2,n-1) * m
```

This is the Phase-3 skill:

``` text
UNKNOWN OBSERVATION
      ↓
tiny cases
      ↓
visible pattern
      ↓
hypothesis
      ↓
derivation / proof
```

------------------------------------------------------------------------

## Stuck Protocol — 10 Minutes

``` text
0–2 min
→ remove story + variables + target

2–4 min
→ smallest valid inputs

4–6 min
→ extremes / parity / modulo / differences

6–8 min
→ write operation delta or algebraic relation

8–10 min
→ brute-force tiny cases mentally / manually
→ inspect pattern
```

After the attempt, check the solution and record **only the missing
observation**:

``` text
I tried:
→

I missed:
→

Trigger I should recognize next time:
→
```

------------------------------------------------------------------------

## Completion Target

Phase 3 is complete when you can repeatedly do:

``` text
I DON'T SEE THE SOLUTION
        ↓
generate useful tiny cases
        ↓
find structure
        ↓
form a hypothesis
        ↓
derive / prove it
        ↓
implement
```

Move forward when you are no longer dependent on being told the
mathematical form and can discover the key observation on mixed problems
yourself.
