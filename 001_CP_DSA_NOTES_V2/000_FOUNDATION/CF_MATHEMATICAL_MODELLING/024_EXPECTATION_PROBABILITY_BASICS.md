# Part 24 — Expectation / Probability Basics

> **Goal:** model Codeforces random processes mathematically: define the sample space, events and random variables; derive probabilities; and compute expectations using conditioning, indicators, contribution counting, linearity, recurrences and probability DP.
>
> **Style:** follows the detailed Part 23 approach: every section has a concrete real-world scenario, actual numbers, mathematical translation, dry-run reasoning, visual flow, sanity checks, and a CF recognition trigger.
>
> **Core workflow:** `Story → What is random? → Sample space/state → Event or random variable → Equation → Dry run → Optimization`
>
> **Recognition question:** **Can I avoid enumerating every global random outcome by counting one event/object/pair/state at a time?**

## Table of Contents

- [24.0 Probability and Expectation Mental Model](#240-probability-and-expectation-mental-model)
- [24.1 Sample Space and Outcomes](#241-sample-space-and-outcomes)
- [24.2 Equally Likely Outcomes](#242-equally-likely-outcomes)
- [24.3 Complement Probability](#243-complement-probability)
- [24.4 Union of Events](#244-union-of-events)
- [24.5 Intersection and Independence](#245-intersection-and-independence)
- [24.6 Conditional Probability](#246-conditional-probability)
- [24.7 Without Replacement](#247-without-replacement)
- [24.8 With Replacement](#248-with-replacement)
- [24.9 Combinations in Probability](#249-combinations-in-probability)
- [24.10 Random Variables](#2410-random-variables)
- [24.11 Expectation Definition](#2411-expectation-definition)
- [24.12 Linearity of Expectation](#2412-linearity-of-expectation)
- [24.13 Indicator Variables](#2413-indicator-variables)
- [24.14 Expected Number of Successes](#2414-expected-number-of-successes)
- [24.15 Expected Contribution](#2415-expected-contribution)
- [24.16 Expected Pair Contribution](#2416-expected-pair-contribution)
- [24.17 Geometric Waiting Time](#2417-geometric-waiting-time)
- [24.18 Expectation Recurrence](#2418-expectation-recurrence)
- [24.19 Self-Referential Expectation](#2419-self-referential-expectation)
- [24.20 Law of Total Expectation](#2420-law-of-total-expectation)
- [24.21 Symmetry](#2421-symmetry)
- [24.22 Random Permutation Relative Order](#2422-random-permutation-relative-order)
- [24.23 Expected Inversions](#2423-expected-inversions)
- [24.24 At Least One Success](#2424-at-least-one-success)
- [24.25 Exactly k Successes](#2425-exactly-k-successes)
- [24.26 Probability DP](#2426-probability-dp)
- [24.27 Probability Conservation](#2427-probability-conservation)
- [24.28 Modular Probability](#2428-modular-probability)
- [24.29 Dependence vs Linearity](#2429-dependence-vs-linearity)
- [24.30 Common Probability Mistakes](#2430-common-probability-mistakes)
- [24.31 60-Second Discovery Workflow](#2431-60-second-discovery-workflow)
- [24.32 Codeforces Recognition Map](#2432-codeforces-recognition-map)
- [24.33 Fast Revision Card](#2433-fast-revision-card)

---

## 24.0 Probability and Expectation Mental Model

### Concept and Core Intuition

This section models **probability and expectation mental model** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Random Coupon

**Situation.** Coupons are 10 or 30 lei, each with probability 1/2.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
E[X]=10*(1/2)+30*(1/2)=20 lei
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

Probability asks how likely; expectation asks the long-run average numerical outcome.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.1 Sample Space and Outcomes

### Concept and Core Intuition

This section models **sample space and outcomes** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Café Buzzer

**Situation.** A café randomly gives buzzer 1,2,3,4. Event A is receiving an even buzzer.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
Ω={1,2,3,4}; A={2,4}; P(A)=2/4=1/2
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

Define all elementary outcomes before counting favorable ones.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.2 Equally Likely Outcomes

### Concept and Core Intuition

This section models **equally likely outcomes** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Locker Allocation

**Situation.** A fair die chooses locker 1..6. You want locker 5 or 6.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
P=2/6=1/3
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

favorable/total is valid only when elementary outcomes are equally likely.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.3 Complement Probability

### Concept and Core Intuition

This section models **complement probability** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Random Digit

**Situation.** A site generates a digit 0..9. Find probability it is non-zero.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
P(nonzero)=1-P(0)=1-1/10=9/10
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

For 'at least one', 'not', or 'none', test whether complement is simpler.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.4 Union of Events

### Concept and Core Intuition

This section models **union of events** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Divisibility Raffle

**Situation.** Choose uniformly from 1..12. A=divisible by 2, B=divisible by 3.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
|A|=6, |B|=4, |A∩B|=2; P(A∪B)=(6+4-2)/12=2/3
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

For A OR B, subtract overlap once.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.5 Intersection and Independence

### Concept and Core Intuition

This section models **intersection and independence** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Quality Sensors

**Situation.** Two independent sensors pass with probability 0.9 each.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
P(both)=0.9*0.9=0.81
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

Multiply only after independence is justified.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.6 Conditional Probability

### Concept and Core Intuition

This section models **conditional probability** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Premium Ticket

**Situation.** Among 10 tickets, 4 are premium; 3 premium tickets have lounge access. Given premium, find lounge probability.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
P(lounge|premium)=3/4
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

'Given that' changes the relevant universe.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.7 Without Replacement

### Concept and Core Intuition

This section models **without replacement** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Colored Balls

**Situation.** Bag: 3 red, 2 blue. Draw two without replacement. Find P(both red).

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
P=(3/5)*(2/4)=3/10
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

After a draw, update remaining counts.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.8 With Replacement

### Concept and Core Intuition

This section models **with replacement** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Colored Balls Returned

**Situation.** Same bag, but return the ball after each draw.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
P(two red)=(3/5)^2=9/25
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

Replacement restores the distribution and often independence.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.9 Combinations in Probability

### Concept and Core Intuition

This section models **combinations in probability** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Engineering Team

**Situation.** Choose 2 engineers from 5; exactly 2 are backend specialists. Find P(both backend).

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
total=C(5,2)=10; favorable=C(2,2)=1; P=1/10
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

Use combinations when selected order does not matter.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.10 Random Variables

### Concept and Core Intuition

This section models **random variables** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Delivery Penalty

**Situation.** Delivery is late with probability 0.2. Penalty X=50 if late, 0 otherwise.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
E[X]=50*0.2+0*0.8=10
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

Define exactly what numerical value X represents.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.11 Expectation Definition

### Concept and Core Intuition

This section models **expectation definition** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Game Reward

**Situation.** Reward is 0,10,40 with probabilities 1/2,1/4,1/4.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
E[X]=0/2+10/4+40/4=12.5
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

Expectation may be a value that never occurs in one trial.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.12 Linearity of Expectation

### Concept and Core Intuition

This section models **linearity of expectation** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Email Opens

**Situation.** Three emails have open probabilities 0.2,0.5,0.7. Let X be total opens.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
E[X]=0.2+0.5+0.7=1.4
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

E[ΣXi]=ΣE[Xi], even without independence.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.13 Indicator Variables

### Concept and Core Intuition

This section models **indicator variables** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Machine Failures

**Situation.** Five machines fail with marginal probabilities 0.1,0.2,0.1,0.3,0.2.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
E[failures]=0.1+0.2+0.1+0.3+0.2=0.9
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

Expected count = sum of event probabilities.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.14 Expected Number of Successes

### Concept and Core Intuition

This section models **expected number of successes** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Server Timeouts

**Situation.** 100 requests each timeout with probability 0.03.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
E[timeouts]=100*0.03=3
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

For n Bernoulli trials, expected successes=np.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.15 Expected Contribution

### Concept and Core Intuition

This section models **expected contribution** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Promoted Products

**Situation.** Products worth 100,200,500 are each selected with probability 1/2.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
E[value]=100/2+200/2+500/2=400
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

Compute each object's expected contribution and add.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.16 Expected Pair Contribution

### Concept and Core Intuition

This section models **expected pair contribution** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Meeting Attendance

**Situation.** 3 people independently attend with probability 1/2. Count expected attending pairs.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
3 pairs; each contributes with probability 1/4; E=3/4
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

For pair totals, use one indicator per pair.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.17 Geometric Waiting Time

### Concept and Core Intuition

This section models **geometric waiting time** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Payment Retry

**Situation.** Each payment attempt succeeds independently with p=0.25.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
E=1+0.75E => 0.25E=1 => E=4 attempts
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

Repeat-until-success with unchanged p gives E=1/p.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.18 Expectation Recurrence

### Concept and Core Intuition

This section models **expectation recurrence** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Random Movement

**Situation.** 2 cells remain. A move advances 1 or 2 with probability 1/2.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
E[0]=0,E[1]=1; E[2]=1+(E[1]+E[0])/2=1.5
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

Condition on the next random transition.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.19 Self-Referential Expectation

### Concept and Core Intuition

This section models **self-referential expectation** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Printer Retry

**Situation.** A print attempt takes 1 minute, succeeds with 0.6, otherwise resets to same state.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
E=1+0.4E => E=5/3 minutes
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

Returning to the same state creates an algebraic E term.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.20 Law of Total Expectation

### Concept and Core Intuition

This section models **law of total expectation** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Delivery Type

**Situation.** Delivery is urban with p=.7 and average 20 min, rural with p=.3 and average 50 min.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
E[T]=.7*20+.3*50=29 minutes
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

Partition into cases and weight each conditional expectation.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.21 Symmetry

### Concept and Core Intuition

This section models **symmetry** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Random Ranking

**Situation.** 5 symmetric contestants receive a uniform random ranking.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
P(a fixed contestant is first)=1/5
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

Uniform symmetry can avoid enumeration.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.22 Random Permutation Relative Order

### Concept and Core Intuition

This section models **random permutation relative order** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Job Shuffle

**Situation.** Jobs A and B appear in a uniformly random permutation.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
P(A before B)=1/2
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

For two distinct elements, either relative order is equally likely.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.23 Expected Inversions

### Concept and Core Intuition

This section models **expected inversions** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Shuffled Folders

**Situation.** Folders 1..4 are uniformly shuffled.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
C(4,2)=6 pairs; each inverted with p=1/2; E=3
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

Expected pair count = number of pairs × contribution probability.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.24 At Least One Success

### Concept and Core Intuition

This section models **at least one success** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Alert Delivery

**Situation.** 4 independent alerts each arrive with probability 0.8.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
P(at least one)=1-(0.2)^4=0.9984
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

At least one is often easiest via zero successes.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.25 Exactly k Successes

### Concept and Core Intuition

This section models **exactly k successes** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Api Requests

**Situation.** 5 independent requests succeed with p=.8. Find exactly 4 successes.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
C(5,4)*.8^4*.2=0.4096
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

Binomial requires independent equal-p trials.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.26 Probability DP

### Concept and Core Intuition

This section models **probability dp** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Moving Token

**Situation.** Token starts 0; each of 2 moves is +1 or +2 with probability 1/2.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
after 2 moves: P(2)=1/4,P(3)=1/2,P(4)=1/4
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

Propagate probability mass: dp[next]+=dp[cur]*transitionProbability.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.27 Probability Conservation

### Concept and Core Intuition

This section models **probability conservation** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Dp Debugging

**Situation.** Use the token distribution after two moves.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
1/4+1/2+1/4=1
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

A complete probability distribution should sum to 1.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.28 Modular Probability

### Concept and Core Intuition

This section models **modular probability** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Fair Modular Choice

**Situation.** Under M=1e9+7, represent 1/2 by modular inverse.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
inv2=2^(M-2)=500000004; 2*inv2 mod M=1
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

For prime modulus, divide by b using b^(M-2), not floating point.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.29 Dependence vs Linearity

### Concept and Core Intuition

This section models **dependence vs linearity** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Random Seating

**Situation.** Seat assignments are dependent, but Xi can indicate whether person i satisfies a property.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
E[ΣXi]=ΣP(event i)
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

Linearity of expectation does not require independence.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.30 Common Probability Mistakes

### Concept and Core Intuition

This section models **common probability mistakes** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Bag Mistake

**Situation.** Bag has 2 red,1 blue; draw 2 without replacement.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
correct P(RR)=(2/3)*(1/2)=1/3, not (2/3)^2
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

Check replacement, ordering, equal likelihood, conditioning, and independence.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.31 60-Second Discovery Workflow

### Concept and Core Intuition

This section models **60-second discovery workflow** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Random Array Index

**Situation.** Choose one index uniformly from [2,5,8]; X is selected value.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
E[X]=(2+5+8)/3=5
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

Write what is random, sample space, event/X, then choose counting/conditioning/linearity.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.32 Codeforces Recognition Map

### Concept and Core Intuition

This section models **codeforces recognition map** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Random Permutation Inversion

**Situation.** Asked expected inverted pairs in a random permutation.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
pair indicator -> P(pair inverted)=1/2 -> E=C(n,2)/2
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

Map wording to a model, but derive the formula.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

## 24.33 Fast Revision Card

### Concept and Core Intuition

This section models **fast revision card** from the random experiment first, rather than starting from a memorized formula.

### Core Mathematical Model

```text
Identify random experiment
        ↓
Define outcomes / event / random variable
        ↓
Write probability or expectation equation
        ↓
Verify on a tiny concrete example
```

### Detailed Real-World Scenario — Random Item Selection

**Situation.** 100 items each have marginal selection probability .2; X is selected count.

**Step 1 — Define what is random.**

```text
Random experiment = the choice/trial described above
Target = the event, count, value, or waiting time we want
```

**Step 2 — Translate the story mathematically.**

```text
E[X]=ΣE[Ii]=100*.2=20
```

**Step 3 — Manual dry run / interpretation.**

The numbers above are not just a formula substitution. Trace the experiment: identify the possible branch(es), attach their probabilities, calculate each branch's contribution, and combine only according to the correct rule.

```text
STORY
  |
  v
random branch / selected object
  |
  +--> probability of branch
  |
  +--> numerical contribution
  |
  v
combine probabilities or expected contributions
  |
  v
ANSWER
```

**Step 4 — Sanity checks.**

```text
Probability must be between 0 and 1.
A complete distribution should sum to 1.
Expected count should lie within the possible count range.
Changing replacement/conditioning may change later probabilities.
```

### Codeforces Recognition Trigger

Expected total/count should immediately trigger contribution + linearity.

### Fast Memory Hook

```text
What is random?
    ↓
What exactly am I measuring?
    ↓
Can I count / complement / condition / use symmetry?
    ↓
For expectation: can I split into indicators or contributions?
```


---

# Part 24 — Master Formula Sheet

```text
EQUALLY LIKELY
P(A) = favorable / total

COMPLEMENT
P(A) = 1 - P(not A)

UNION
P(A ∪ B)
= P(A)+P(B)-P(A ∩ B)

CONDITIONAL
P(A | B)
= P(A ∩ B)/P(B)

INDEPENDENT INTERSECTION
P(A ∩ B)
= P(A)P(B)

EXPECTATION
E[X]
= Σ x * P(X=x)

LINEARITY
E[X1+...+Xn]
= E[X1]+...+E[Xn]

INDICATOR
I=1 if event occurs, otherwise 0
E[I]=P(event)

EXPECTED CONTRIBUTION
E[total]
= Σ contribution_i * P(i contributes)

GEOMETRIC WAITING
E[T]=1/p

EXPECTATION RECURRENCE
E[state]
=
immediate cost
+
Σ P(next) * E[next]

BINOMIAL
P(X=k)
=
C(n,k)p^k(1-p)^(n-k)

MODULAR FRACTION
a/b mod M
=
a * b^(M-2) mod M
for prime M
```

# 60-Second Contest Flow

```text
RANDOM STORY
    |
    v
What is random?
    |
    v
What are elementary outcomes?
    |
    v
Are they equally likely?
    |
    +-------------------------------+
    |                               |
 probability                    expectation
    |                               |
 event?                         random variable?
    |                               |
    v                               v
count/complement/             expected count/sum?
conditional/symmetry           |
                                +--> indicators
                                +--> contributions
                                +--> linearity
                                |
                                v
                         random transition process?
                                |
                                +--> expectation recurrence
                                +--> probability DP
```
