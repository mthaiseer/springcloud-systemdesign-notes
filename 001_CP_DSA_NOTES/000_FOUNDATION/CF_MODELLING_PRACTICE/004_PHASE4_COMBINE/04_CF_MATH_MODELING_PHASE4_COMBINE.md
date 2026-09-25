# 04 — CF Mathematical Modeling Phase 4: COMBINE

> **Goal:** combine two or more mathematical models / algorithmic ideas
> when one observation is not enough.

``` text
STATEMENT
   ↓
DECODE
   ↓
DISCOVER FIRST MODEL
   ↓
"What remains unsolved?"
   ↓
DISCOVER SECOND MODEL
   ↓
CONNECT THEM
   ↓
PROVE
   ↓
CODE
```

------------------------------------------------------------------------

## Phase Progression

``` text
PHASE 1 — LEARN
Know the forms.

PHASE 2 — RECOGNIZE
Identify the hidden form.

PHASE 3 — DISCOVER
Derive the observation yourself.

PHASE 4 — COMBINE
Connect multiple observations into one solution.
```

A Phase-4 problem often looks like:

``` text
Observation A alone → not enough
Observation B alone → not enough

A + B
  ↓
complete solution
```

------------------------------------------------------------------------

## How to Use

Before coding each problem, write:

``` text
1. Problem Asking:
2. Story → Variables:
3. First observation:
4. First model / form:
5. What remains difficult?
6. Second observation:
7. Second model / form:
8. How do the two connect?
9. Complexity:
10. Proof / why it works:
11. Final algorithm:
```

**Rule:** do not search for "the pattern." Break the problem into
smaller mathematical jobs.

------------------------------------------------------------------------

# Combination 1 — Algebra Transformation + Frequency Counting

## Recognition Shape

``` text
pair condition
     ↓
rearrange equation
     ↓
same key on both sides
     ↓
count equal keys
```

Example:

``` text
a[j] - a[i] = j - i

        ↓ rearrange

a[j] - j = a[i] - i

        ↓

key[i] = a[i] - i

        ↓

frequency / hashmap
```

## Problems

|  \# | Problem                                                                         | Rating | Models Found | Solved |
|----:|---------------------------------------------------------------------------------|-------:|--------------|:------:|
|   1 | [CF 1520D — Same Differences](https://codeforces.com/problemset/problem/1520/D) |   1200 |              |   ☐    |
|   2 | [CF 1669F — Eating Candies](https://codeforces.com/problemset/problem/1669/F)   |   1100 |              |   ☐    |

------------------------------------------------------------------------

# Combination 2 — Sorting + Two Pointers / Pair Counting

## Recognition Shape

``` text
pair condition
     ↓
sorting creates order
     ↓
valid region becomes monotonic
     ↓
two pointers / binary search / counting
```

## Problems

|  \# | Problem                                                                                             | Rating | Models Found | Solved |
|----:|-----------------------------------------------------------------------------------------------------|-------:|--------------|:------:|
|   1 | [CF 1538C — Challenging Cliffs / Number of Pairs](https://codeforces.com/problemset/problem/1538/C) |   1300 |              |   ☐    |
|   2 | [CF 1669F — Eating Candies](https://codeforces.com/problemset/problem/1669/F)                       |   1100 |              |   ☐    |
|   3 | [CF 1793C — Dora and Search](https://codeforces.com/problemset/problem/1793/C)                      |   1200 |              |   ☐    |

------------------------------------------------------------------------

# Combination 3 — Difference Array + Frequency + Greedy Rearrangement

## Recognition Shape

``` text
many range operations / queries
        ↓
difference array
        ↓
frequency of each index
        ↓
sort frequencies
        +
sort values
        ↓
pair large with large
```

## Problems

|  \# | Problem                                                                                  | Rating | Models Found | Solved |
|----:|------------------------------------------------------------------------------------------|-------:|--------------|:------:|
|   1 | [CF 276C — Little Girl and Maximum Sum](https://codeforces.com/problemset/problem/276/C) |   1400 |              |   ☐    |
|   2 | [CF 433B — Kuriyama Mirai's Stones](https://codeforces.com/problemset/problem/433/B)     |   1200 |              |   ☐    |

------------------------------------------------------------------------

# Combination 4 — Prefix Sum + Optimization

## Recognition Shape

``` text
many candidate ranges
      ↓
range sum needed repeatedly
      ↓
prefix sum
      ↓
compare / minimize / maximize
```

## Problems

|  \# | Problem                                                                              | Rating | Models Found | Solved |
|----:|--------------------------------------------------------------------------------------|-------:|--------------|:------:|
|   1 | [CF 363B — Fence](https://codeforces.com/problemset/problem/363/B)                   |   1100 |              |   ☐    |
|   2 | [CF 433B — Kuriyama Mirai's Stones](https://codeforces.com/problemset/problem/433/B) |   1200 |              |   ☐    |
|   3 | [CF 313B — Ilya and Queries](https://codeforces.com/problemset/problem/313/B)        |   1100 |              |   ☐    |

------------------------------------------------------------------------

# Combination 5 — Transformation + Maximum Subarray

## Recognition Shape

``` text
operation changes contribution
        ↓
convert each position to gain/loss
        ↓
original answer + best extra gain
        ↓
maximum subarray
```

## Problems

|  \# | Problem                                                                    | Rating | Models Found | Solved |
|----:|----------------------------------------------------------------------------|-------:|--------------|:------:|
|   1 | [CF 327A — Flipping Game](https://codeforces.com/problemset/problem/327/A) |   1200 |              |   ☐    |

### Discovery Prompt

``` text
original 0 → flip gives +1
original 1 → flip gives -1

array
   ↓
gain array
   ↓
best segment gain
   ↓
Kadane
```

------------------------------------------------------------------------

# Combination 6 — Divisibility + Factorization + Counting

## Recognition Shape

``` text
all values must become equal
        ↓
look at global product
        ↓
prime factorization
        ↓
count exponent of each prime
        ↓
each exponent must split equally
```

## Problems

|  \# | Problem                                                                            | Rating | Models Found | Solved |
|----:|------------------------------------------------------------------------------------|-------:|--------------|:------:|
|   1 | [CF 1881D — Divide and Equalize](https://codeforces.com/problemset/problem/1881/D) |   1200 |              |   ☐    |
|   2 | [CF 1475B — New Year's Number](https://codeforces.com/problemset/problem/1475/B)   |    900 |              |   ☐    |

------------------------------------------------------------------------

# Combination 7 — Modulo / Divisibility + String / Digit Structure

## Recognition Shape

``` text
number must satisfy divisibility
        ↓
full numeric value may not matter
        ↓
divisibility depends on small digit pattern
        ↓
search / count required suffix
```

## Problems

|  \# | Problem                                                                                | Rating | Models Found | Solved |
|----:|----------------------------------------------------------------------------------------|-------:|--------------|:------:|
|   1 | [CF 1593B — Make it Divisible by 25](https://codeforces.com/problemset/problem/1593/B) |    900 |              |   ☐    |
|   2 | [CF 1343A — Candies](https://codeforces.com/problemset/problem/1343/A)                 |    900 |              |   ☐    |

------------------------------------------------------------------------

# Combination 8 — Parity + Greedy / Operation Counting

## Recognition Shape

``` text
operation changes signs / values
        ↓
track count or sum
        ↓
parity gives feasibility
        ↓
greedy fixes remaining condition
```

## Problems

|  \# | Problem                                                                           | Rating | Models Found | Solved |
|----:|-----------------------------------------------------------------------------------|-------:|--------------|:------:|
|   1 | [CF 1834A — Unit Array](https://codeforces.com/problemset/problem/1834/A)         |    800 |              |   ☐    |
|   2 | [CF 1367B — Even Array](https://codeforces.com/problemset/problem/1367/B)         |    800 |              |   ☐    |
|   3 | [CF 1296A — Array with Odd Sum](https://codeforces.com/problemset/problem/1296/A) |    800 |              |   ☐    |

------------------------------------------------------------------------

# Combination 9 — Min / Max + Constructive

## Recognition Shape

``` text
need ANY valid construction
        ↓
find extremal requirement
        ↓
place smallest / largest useful values
        ↓
construct remaining values
```

## Problems

|  \# | Problem                                                                            | Rating | Models Found | Solved |
|----:|------------------------------------------------------------------------------------|-------:|--------------|:------:|
|   1 | [CF 1690A — Print a Pedestal](https://codeforces.com/problemset/problem/1690/A)    |    800 |              |   ☐    |
|   2 | [CF 1845A — Forbidden Integer](https://codeforces.com/problemset/problem/1845/A)   |    800 |              |   ☐    |
|   3 | [CF 1878B — Aleksa and Stack](https://codeforces.com/problemset/problem/1878/B)    |    800 |              |   ☐    |
|   4 | [CF 1833B — Restore the Weather](https://codeforces.com/problemset/problem/1833/B) |   1000 |              |   ☐    |

------------------------------------------------------------------------

# Combination 10 — XOR Algebra + Parity

## Recognition Shape

``` text
XOR expression
     ↓
expand repeated x
     ↓
number of occurrences matters
     ↓
odd/even n changes cancellation
```

## Problems

|  \# | Problem                                                                         | Rating | Models Found | Solved |
|----:|---------------------------------------------------------------------------------|-------:|--------------|:------:|
|   1 | [CF 1805A — We Need the Zero](https://codeforces.com/problemset/problem/1805/A) |    900 |              |   ☐    |
|   2 | [CF 1475A — Odd Divisor](https://codeforces.com/problemset/problem/1475/A)      |    900 |              |   ☐    |

------------------------------------------------------------------------

# Combination 11 — Binary Search + Monotonic Mathematical Condition

## Recognition Shape

``` text
find minimum / maximum answer
        ↓
guess answer = X
        ↓
can(X)?
        ↓
false false false true true true

or

true true true false false
        ↓
binary search boundary
```

## Practice Goal

Before binary searching, you must be able to write:

``` text
1. What is X?
2. What is the search range?
3. What exactly does can(X) mean?
4. Why is can(X) monotonic?
```

This section is a bridge from pure modeling into algorithm selection.

------------------------------------------------------------------------

# Combination 12 — Contribution + Counting

## Recognition Shape

Instead of:

``` text
enumerate every object / subarray / pair
```

ask:

``` text
How many times does one element contribute?
```

For a 1-indexed element `a[i]`:

``` text
left endpoint  = 1..i       → i choices
right endpoint = i..n       → n-i+1 choices

subarrays containing a[i]
= i * (n-i+1)
```

Therefore:

``` text
contribution of a[i]
= a[i] * i * (n-i+1)
```

This is the mental bridge:

``` text
GLOBAL SUM
   ↓
reverse viewpoint
   ↓
CONTRIBUTION OF ONE ELEMENT
   ↓
count occurrences
```

------------------------------------------------------------------------

# Mixed Combination Drill — No Labels

Do not look at the sections above while solving.

|  \# | Problem                                                                                  | Rating | Model 1 | Model 2 | Solved |
|----:|------------------------------------------------------------------------------------------|-------:|---------|---------|:------:|
|   1 | [CF 1520D — Same Differences](https://codeforces.com/problemset/problem/1520/D)          |   1200 |         |         |   ☐    |
|   2 | [CF 1538C — Number of Pairs](https://codeforces.com/problemset/problem/1538/C)           |   1300 |         |         |   ☐    |
|   3 | [CF 276C — Little Girl and Maximum Sum](https://codeforces.com/problemset/problem/276/C) |   1400 |         |         |   ☐    |
|   4 | [CF 327A — Flipping Game](https://codeforces.com/problemset/problem/327/A)               |   1200 |         |         |   ☐    |
|   5 | [CF 1881D — Divide and Equalize](https://codeforces.com/problemset/problem/1881/D)       |   1200 |         |         |   ☐    |
|   6 | [CF 1593B — Make it Divisible by 25](https://codeforces.com/problemset/problem/1593/B)   |    900 |         |         |   ☐    |
|   7 | [CF 1669F — Eating Candies](https://codeforces.com/problemset/problem/1669/F)            |   1100 |         |         |   ☐    |
|   8 | [CF 1793C — Dora and Search](https://codeforces.com/problemset/problem/1793/C)           |   1200 |         |         |   ☐    |
|   9 | [CF 1833B — Restore the Weather](https://codeforces.com/problemset/problem/1833/B)       |   1000 |         |         |   ☐    |
|  10 | [CF 1805A — We Need the Zero](https://codeforces.com/problemset/problem/1805/A)          |    900 |         |         |   ☐    |

------------------------------------------------------------------------

# Combination Worksheet

Use this for every Phase-4 problem:

``` text
### Problem

1. Problem Asking
   →

2. Story → Variables
   →

3. First Observation
   →

4. Model 1
   →

5. What does Model 1 solve?
   →

6. What is still unsolved?
   →

7. Second Observation
   →

8. Model 2
   →

9. Connection
   Model 1 output
        ↓
   becomes input / condition for
        ↓
   Model 2

10. Algebra / Derivation
    →

11. Complexity
    →

12. Why It Works
    →

13. Final Algorithm
    →
```

------------------------------------------------------------------------

# The Most Important Phase-4 Question

When you have found one useful observation but still cannot solve the
problem:

``` text
DON'T THROW AWAY THE OBSERVATION.
```

Ask:

``` text
What part of the problem did this observation remove?

What smaller problem remains?
```

Example:

``` text
range queries
     ↓
difference array solves:
"how often is each index used?"

still unsolved:
"which values should receive high frequencies?"

     ↓
sorting + greedy rearrangement

difference array
     +
sorting / rearrangement
     ↓
complete solution
```

This is **combination thinking**.

------------------------------------------------------------------------

# Stuck Protocol — 15 Minutes

``` text
0–3 min
→ decode story
→ variables + target

3–6 min
→ discover first mathematical model

6–9 min
→ write exactly what remains unsolved

9–12 min
→ search for second model:
   sorting?
   prefix?
   frequency?
   parity?
   modulo?
   greedy?
   two pointers?
   binary search?
   contribution?
   invariant?

12–15 min
→ connect Model 1 → Model 2
→ prove
```

After checking a solution, record:

``` text
Model 1 I found:
→

Model 2 I missed:
→

Why they connect:
→

Trigger for next time:
→
```

------------------------------------------------------------------------

# Completion Target

Phase 4 is complete when your thinking changes from:

``` text
"What ONE pattern is this?"
```

to:

``` text
"What sequence of transformations solves this?"

story
  ↓
math model
  ↓
transformed problem
  ↓
algorithmic model
  ↓
answer
```

Target skill:

``` text
UNFAMILIAR PROBLEM
       ↓
discover observation A
       ↓
reduce problem
       ↓
discover observation B
       ↓
combine A + B
       ↓
derive + prove
       ↓
implement
```
