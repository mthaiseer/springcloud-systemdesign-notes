# 021_Probability_Basics.md

# Probability Basics For Competitive Programming

---

# 1. Introduction

Probability is one of the MOST important mathematical topics in CP.

It appears heavily in:
- counting
- combinatorics
- expected value
- random processes
- games
- DP
- graphs

Strong contestants think probability as:

```text
Counting favorable outcomes
out of total outcomes
```

NOT as memorizing formulas.

This intuition is critical.

---

# 2. What Is Probability?

Probability means:

```text
Likelihood of event happening
```

Formula:

```text
Probability
=
Favorable Outcomes
/
Total Outcomes
```

---

# 3. Simple Dice Example

Roll a dice.

Probability of getting:
```text
4
```

Favorable outcomes:
```text
1
```

Total outcomes:
```text
6
```

Answer:

```text
1 / 6
```

---

# 4. Coin Toss Example

Probability of:
```text
HEAD
```

Total outcomes:

```text
HEAD
TAIL
```

Favorable:
```text
1
```

Probability:

```text
1 / 2
```

---

# 5. Core Probability Intuition

Strong contestants think:

```text
Probability
=
Counting Problem
```

Thus:
- combinatorics
- permutations
- combinations

become extremely important.

---

# 6. Probability Range

Probability always lies between:

```text
0 and 1
```

Special cases:

```text
0
→ impossible

1
→ certain
```

---

# 7. Complement Probability

Very important trick.

Instead of computing:

```text
event happens
```

compute:

```text
event does NOT happen
```

Formula:

```text
P(A)
=
1 - P(not A)
```

Massive simplification in many problems.

---

# Example

Probability at least one HEAD in:
```text
3 tosses
```

Instead compute:
```text
no HEAD
```

Only:
```text
TTT
```

Probability:

```text
1 / 8
```

Thus:

```text
1 - 1/8
=
7/8
```

---

# 8. Independent Events

Events independent if:
```text
one does not affect another
```

Formula:

```text
P(A and B)
=
P(A) × P(B)
```

---

# Example

Probability:
- coin HEAD
AND
- dice 6

Answer:

```text
1/2 × 1/6
=
1/12
```

---

# 9. OR Probability

Important formula:

```text
P(A or B)
=
P(A)
+
P(B)
-
P(A and B)
```

This is inclusion-exclusion for probability.

---

# 10. Expected Value Intuition

Expected value means:

```text
Average outcome over long run
```

Very important advanced CP concept.

Example:

Dice expected value:

```text
(1+2+3+4+5+6)/6
=
3.5
```

---

# 11. Counting + Probability

Most probability problems reduce to:

```text
1. Count total outcomes
2. Count favorable outcomes
3. Divide
```

This is the MOST important probability mindset.

---

# 12. Permutation/Combination Connection

Probability often uses:
- combinations
- arrangements
- selections

Example:
Probability of choosing 2 red balls.

Need:
- favorable combinations
- total combinations

---

# 13. Conditional Probability Intuition

Conditional probability means:

```text
restrict sample space
```

Example:

Given:
```text
number already even
```

Probability changes.

Very important advanced concept.

---

# 14. Random Process Thinking

Many CP problems involve:
- random walks
- random choices
- transitions

Probability intuition later helps:
- DP
- Markov chains
- expected value DP

---

# 15. CP / FAANG Problem Forms

---

# Form 1 — Dice Probability

## Problem

Probability of rolling:
```text
4
```

---

## Observation

Favorable:
```text
1
```

Total:
```text
6
```

---

## Step-by-Step Working

Possible outcomes:

```text
1 2 3 4 5 6
```

Only:
```text
4
```

works.

Answer:
```text
1/6
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

double diceProbability() {

    return 1.0 / 6.0;
}
```

---

# Form 2 — Coin Toss Probability

## Problem

Probability of HEAD.

---

## Observation

Favorable:
```text
1
```

Total:
```text
2
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

double coinProbability() {

    return 0.5;
}
```

---

# Form 3 — Independent Events

## Problem

Probability:
- HEAD
AND
- dice 6

---

## Observation

Independent multiplication.

---

## Step-by-Step Working

```text
1/2 × 1/6
=
1/12
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

double independentEvents() {

    return (1.0 / 2.0)
         * (1.0 / 6.0);
}
```

---

# Form 4 — Complement Probability

## Problem

Probability at least one HEAD in 3 tosses.

---

## Observation

Compute:
```text
no HEAD
```

instead.

---

## Step-by-Step Working

Probability:
```text
all TAIL
=
1/8
```

Thus:
```text
1 - 1/8
=
7/8
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

double atleastOneHead() {

    return 1.0 - (1.0 / 8.0);
}
```

---

# Form 5 — OR Probability

## Problem

Probability:
```text
A OR B
```

---

## Observation

Subtract overlap.

Formula:

```text
P(A)+P(B)-P(A∩B)
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

double probabilityOR(
    double A,
    double B,
    double overlap
) {

    return A + B - overlap;
}
```

---

# Form 6 — Combination Probability

## Problem

Choose 2 red balls.

---

## Observation

Use combinations.

Probability:

```text
favorable combinations
/
total combinations
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long nCr(int n, int r) {

    long long ans = 1;

    for (int i = 1; i <= r; i++) {

        ans =
            ans * (n - i + 1) / i;
    }

    return ans;
}

double redBallProbability(
    int red,
    int total
) {

    return (double)nCr(red, 2)
         / nCr(total, 2);
}
```

---

# Form 7 — Expected Value

## Problem

Expected dice value.

---

## Observation

Average weighted outcome.

---

## Step-by-Step Working

```text
(1+2+3+4+5+6)/6
=
3.5
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

double expectedDice() {

    return 3.5;
}
```

---

# 16. Real World Applications

| Real System | Usage |
|---|---|
| Recommendation systems | probability ranking |
| Machine learning | probabilistic models |
| Networking | packet loss probability |
| Search engines | ranking confidence |
| Finance | risk analysis |
| Gaming systems | random events |
| Distributed systems | failure probability |

---

# 17. Real Engineering Insight

Probability usually means:

```text
Count favorable outcomes
out of all possible outcomes
```

This mindset is critical.

---

# 18. Observation Recognition Signals

Look for:

```text
1. random choice
2. likelihood
3. expected value
4. independent events
5. combinations
6. random process
7. probabilities
8. favorable outcomes
9. total outcomes
10. randomness
```

---

# 19. Decision Tree

```text
Need likelihood?
→ probability

Independent events?
→ multiply

At least one?
→ complement probability

OR conditions?
→ inclusion-exclusion

Selections involved?
→ combinations

Average random outcome?
→ expected value
```

---

# 20. Common Traps

```text
1. Wrong sample space
2. Forgetting overlaps
3. Confusing independent/dependent events
4. Wrong complement calculation
5. Double counting outcomes
6. Incorrect combinations
7. Probability > 1 mistake
8. Wrong expected value averaging
```

---

# 21. Final Checklist

Before solving:

```text
1. What are total outcomes?
2. What are favorable outcomes?
3. Are events independent?
4. Can complement simplify?
5. Is overlap involved?
6. Are combinations needed?
7. Is expected value required?
8. Is probability within 0..1?
```

---

# 22. Final Mental Shortcut

```text
Probability
=
Favorable Outcomes
/
Total Outcomes
```
