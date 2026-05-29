# 023_Conditional_Probability_Basics.md

# Conditional Probability Basics For Competitive Programming

---

# 1. Introduction

Conditional Probability is one of the MOST important probability concepts in CP.

It appears heavily in:
- probability problems
- Bayesian thinking
- random processes
- games
- DP
- expected value
- graph probability

Strong contestants think conditional probability as:

```text
Probability after restricting
possible outcomes
```

This intuition is critical.

---

# 2. What Is Conditional Probability?

Conditional probability means:

```text
Probability of A
given B already happened
```

Notation:

```text
P(A | B)
```

Read as:

```text
Probability of A given B
```

---

# 3. Core Intuition

VERY IMPORTANT IDEA:

Conditional probability changes:

```text
sample space
```

We no longer consider:
```text
all outcomes
```

Only:
```text
outcomes satisfying B
```

remain valid.

---

# 4. Conditional Probability Formula

Formula:

```text
P(A | B)
=
P(A ∩ B)
/
P(B)
```

Meaning:

```text
Among B outcomes,
how many also satisfy A?
```

---

# 5. Dice Example

Roll dice.

Given:
```text
number is even
```

Probability number is:
```text
4
```

---

# Original Sample Space

```text
1 2 3 4 5 6
```

---

# Restricted Sample Space

Only even outcomes:

```text
2 4 6
```

Total:
```text
3
```

Favorable:
```text
4
```

Count:
```text
1
```

Probability:

```text
1 / 3
```

NOT:
```text
1 / 6
```

Very important intuition.

---

# 6. Why Conditional Probability Matters

Conditional probability helps:
- update probabilities
- restrict states
- analyze dependencies
- solve Bayesian problems
- simplify random processes

Very important advanced concept.

---

# 7. Independent Events

If events independent:

```text
P(A | B)
=
P(A)
```

because:
```text
B does not affect A
```

Very important observation.

---

# Example

Dice roll independent of:
- coin toss

Thus:
```text
P(dice=6 | HEAD)
=
1/6
```

unchanged.

---

# 8. Dependent Events

Dependent events:
```text
change probabilities
```

Example:

Drawing cards:
- without replacement

First draw affects:
- second draw

Very important CP probability pattern.

---

# 9. Card Example

Deck:
```text
52 cards
```

Given:
```text
first card was ACE
```

Probability second card ACE:

Remaining:
```text
3 aces
51 cards
```

Probability:

```text
3 / 51
```

NOT:
```text
4 / 52
```

---

# 10. Bayesian Thinking

Conditional probability forms foundation of:
- Bayes theorem
- machine learning
- AI probabilities

Very important real-world concept.

---

# 11. Restricting Sample Space

Strong contestants constantly ask:

```text
What outcomes remain possible?
```

after conditions appear.

This is core conditional probability thinking.

---

# 12. Conditional Probability And DP

Many advanced DP problems use:
- conditional states
- transition probabilities
- restricted future states

Very important later.

---

# 13. Probability Trees

Useful visualization:

```text
start
├── event A
└── event B
```

Conditional probabilities often represented as:
- branching processes
- state transitions

---

# 14. CP Observation Mindset

Strong contestants think:

```text
Condition
=
Shrink sample space
```

This is the MOST important intuition.

---

# 15. CP / FAANG Problem Forms

---

# Form 1 — Even Dice Condition

## Problem

Given dice result even.

Probability result is:
```text
4
```

---

## Observation

Restrict sample space.

---

## Step-by-Step Working

Even outcomes:

```text
2 4 6
```

Only:
```text
4
```

works.

Answer:
```text
1 / 3
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

double conditionalDice() {

    return 1.0 / 3.0;
}
```

---

# Form 2 — Card Draw Without Replacement

## Problem

Given first card ACE.

Probability second card ACE.

---

## Observation

Dependent events.

---

## Step-by-Step Working

Remaining:
```text
3 aces
51 cards
```

Answer:
```text
3 / 51
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

double aceAfterAce() {

    return 3.0 / 51.0;
}
```

---

# Form 3 — Independent Events

## Problem

Probability dice=6 given HEAD.

---

## Observation

Independent events unchanged.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

double independentConditional() {

    return 1.0 / 6.0;
}
```

---

# Form 4 — Formula Application

## Problem

Compute:
```text
P(A|B)
```

---

## Observation

Use formula:

```text
P(A∩B)/P(B)
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

double conditionalProbability(
    double intersection,
    double B
) {

    return intersection / B;
}
```

---

# Form 5 — Restricted Sample Space

## Problem

Choose random even number from:
```text
1..10
```

Probability divisible by 4.

---

## Observation

Only even numbers remain.

---

## Step-by-Step Working

Even numbers:

```text
2 4 6 8 10
```

Divisible by 4:

```text
4 8
```

Probability:

```text
2 / 5
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

double divisibleBy4() {

    return 2.0 / 5.0;
}
```

---

# Form 6 — Bayes Style Update

## Problem

Update probability after evidence.

---

## Observation

Condition changes belief.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

double bayesUpdate(
    double prior,
    double likelihood,
    double evidence
) {

    return (prior * likelihood)
            / evidence;
}
```

---

# Form 7 — Random Process Transition

## Problem

Probability next state given current state.

---

## Observation

Conditional transition probability.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

double transitionProbability(
    double success,
    double total
) {

    return success / total;
}
```

---

# 16. Real World Applications

| Real System | Usage |
|---|---|
| Machine learning | Bayesian models |
| Recommendation systems | conditional ranking |
| Search engines | relevance prediction |
| Networking | conditional failure analysis |
| Finance | conditional risk |
| AI systems | probabilistic inference |
| Medical diagnosis | Bayesian probability |

---

# 17. Real Engineering Insight

Conditional probability usually means:

```text
Restrict possible outcomes
```

This is extremely important intuition.

---

# 18. Observation Recognition Signals

Look for:

```text
1. given condition
2. after event occurs
3. restricted choices
4. dependent events
5. conditional transitions
6. probability update
7. evidence
8. reduced sample space
```

---

# 19. Decision Tree

```text
Condition given?
→ conditional probability

Sample space reduced?
→ recompute denominator

Events independent?
→ probability unchanged

Without replacement?
→ dependent events

Need probability update?
→ Bayes thinking
```

---

# 20. Common Traps

```text
1. Using original sample space
2. Forgetting restriction
3. Confusing independent/dependent
4. Wrong denominator
5. Ignoring updated probabilities
6. Double counting outcomes
7. Wrong card counting
8. Missing conditional transitions
```

---

# 21. Final Checklist

Before solving:

```text
1. Has sample space changed?
2. What outcomes remain possible?
3. Are events dependent?
4. Is denominator updated?
5. Are probabilities conditional?
6. Is replacement involved?
7. Is Bayes reasoning needed?
8. Can conditions simplify counting?
```

---

# 22. Final Mental Shortcut

```text
Conditional Probability
=
Restrict Sample Space
Then Recompute Probability
```
