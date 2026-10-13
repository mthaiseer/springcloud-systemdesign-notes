# TLE Eliminators — Interactive Problems
## Binary Search & Interactive Problems — Level 3
### TLE-Style Self-Study Notes

> **Source:** supplied TLE Eliminators lecture **Interactive Problems** plus the supplied lecture-code screenshots.
>
> The lecture introduces:
>
> ```text
> normal problem:
> input → your program → output
>
> interactive problem:
> your program ↔ online judge / interactor
> ```
>
> Main ideas:
>
> ```text
> Problem 1 — Hidden Number
>             → Binary Search
>
> Problem 2 — Find an Index of the Majority Element
>             → Randomization
> ```
>
> It also emphasizes:
>
> ```text
> query limits
> exact query / answer protocol
> flushing after every query
> local testing with a custom interactor
> binary search / ternary search
> randomization
> ```
>
> **Teaching flow**
>
> ```text
> prerequisites
> → tiny dry run
> → what the problem asks
> → what one query tells us
> → choose strategy
> → query-count reasoning
> → detailed dry run
> → compact C++17
> → local testing
> → recognition model
> ```

---

# Clickable Table of Contents

- [0. Class Map](#0-class-map)
- [1. Prerequisites](#1-prerequisites)
  - [1.1 Normal vs Interactive Problems](#11-normal-vs-interactive-problems)
  - [1.2 The Interactor](#12-the-interactor)
  - [1.3 Query → Reply → Next Query](#13-query--reply--next-query)
  - [1.4 Query Budget](#14-query-budget)
  - [1.5 Output Flushing](#15-output-flushing)
  - [1.6 `endl` vs `\n`](#16-endl-vs-n)
  - [1.7 Query / Answer Protocol](#17-query--answer-protocol)
  - [1.8 Binary Search as an Interactive Strategy](#18-binary-search-as-an-interactive-strategy)
  - [1.9 Query Complexity of Binary Search](#19-query-complexity-of-binary-search)
  - [1.10 Randomization](#110-randomization)
  - [1.11 Majority Element Probability](#111-majority-element-probability)
  - [1.12 Repeated Random Trials](#112-repeated-random-trials)
  - [1.13 Custom Interactor for Local Testing](#113-custom-interactor-for-local-testing)
  - [1.14 Recognition Checklist](#114-recognition-checklist)
- [2. Problem 1 — Hidden Number](#2-problem-1--hidden-number)
  - [2.1 What It Asks](#21-what-it-asks)
  - [2.2 What Result Is Expected](#22-what-result-is-expected)
  - [2.3 What One Query Tells Us](#23-what-one-query-tells-us)
  - [2.4 Why Binary Search Fits](#24-why-binary-search-fits)
  - [2.5 Search-Space Update Rules](#25-search-space-update-rules)
  - [2.6 Detailed Dry Run — Hidden Number 10](#26-detailed-dry-run--hidden-number-10)
  - [2.7 Query Complexity](#27-query-complexity)
  - [2.8 Interactive C++17](#28-interactive-c17)
  - [2.9 Local Custom Interactor](#29-local-custom-interactor)
  - [2.10 Recognition Model](#210-recognition-model)
- [3. Problem 2 — Find an Index of the Majority Element](#3-problem-2--find-an-index-of-the-majority-element)
  - [3.1 What It Asks](#31-what-it-asks)
  - [3.2 Why Binary Search Does Not Apply](#32-why-binary-search-does-not-apply)
  - [3.3 Majority Guarantee](#33-majority-guarantee)
  - [3.4 Convert Frequency Into Probability](#34-convert-frequency-into-probability)
  - [3.5 Probability of Missing Repeatedly](#35-probability-of-missing-repeatedly)
  - [3.6 Why 30 / 100 Trials Become Powerful](#36-why-30--100-trials-become-powerful)
  - [3.7 Randomized Strategy](#37-randomized-strategy)
  - [3.8 Detailed Dry Run](#38-detailed-dry-run)
  - [3.9 Interactive C++17](#39-interactive-c17)
  - [3.10 Complexity and Error Probability](#310-complexity-and-error-probability)
  - [3.11 Recognition Model](#311-recognition-model)
- [4. How to Test Interactive Problems Locally](#4-how-to-test-interactive-problems-locally)
- [5. Binary Search vs Randomization](#5-binary-search-vs-randomization)
- [6. Important Pointers From the Lecture](#6-important-pointers-from-the-lecture)
- [7. Common Mistakes](#7-common-mistakes)
- [8. Final Mental Model](#8-final-mental-model)

---

# 0. Class Map

```text
INTERACTIVE PROBLEMS
│
├── Program asks a query
│       |
│       v
│   "? something"
│       |
│       v
│     FLUSH
│       |
│       v
│   Judge returns information
│       |
│       v
│   use reply to decide next query
│
├── Ordered / directional information
│       |
│       └── Binary Search / Ternary Search
│
└── No useful positional pattern
        |
        └── Randomization
```

Two lecture examples:

```text
HIDDEN NUMBER
query mid
→ judge says < / > / =
→ discard half
→ Binary Search
```

```text
MAJORITY INDEX
query random index
→ judge says 1 / 0
→ majority occupies > half the array
→ random hit probability > 1/2
→ repeat random trials
```

---

# 1. Prerequisites

## 1.1 Normal vs Interactive Problems

### Normal Problem

```text
INPUT
  |
  v
YOUR PROGRAM
  |
  v
OUTPUT
```

The complete input is known before solving.

### Interactive Problem

```text
YOUR PROGRAM
     |
     | query
     v
ONLINE JUDGE
     |
     | reply
     v
YOUR PROGRAM
     |
     | next query
     v
ONLINE JUDGE
     |
    ...
```

The reply to one query changes what you ask next.

---

## 1.2 The Interactor

Think of the Online Judge as another program storing hidden data.

Example:

```text
hidden = 10
```

Your program asks:

```text
? 6
```

Judge replies:

```text
<
```

meaning:

```text
6 < hidden
```

Your program learns only:

```text
hidden > 6
```

and uses that information for the next query.

---

## 1.3 Query → Reply → Next Query

```text
STATE
  |
  v
CHOOSE QUERY
  |
  v
PRINT QUERY
  |
  v
FLUSH
  |
  v
READ REPLY
  |
  v
UPDATE STATE
  |
  └────────────── repeat
```

### Tiny Dry Run

Hidden number:

```text
10
```

Possible range:

```text
[1,16]
```

Ask:

```text
? 8
```

Reply:

```text
<
```

Meaning:

```text
8 < 10
```

New possible range:

```text
[9,16]
```

---

## 1.4 Query Budget

Interactive statements limit the number of questions.

So always ask:

```text
How many queries does my algorithm need?
```

Example:

```text
hidden number ∈ [1,10000]
```

Linear guessing:

```text
up to 10000 queries
```

Binary search:

```text
ceil(log2(10000)) ≈ 14 queries
```

The quality of an interactive strategy is largely:

```text
information gained per query
```

---

## 1.5 Output Flushing

The lecture emphasizes:

```text
flush after every query
```

Without flushing:

```text
Your Program                 Judge
     |                         |
     | "? 50" stays buffered   |
     |                         |
     | waits for reply         |
     |                         | waits for query
```

With flushing:

```text
Your Program                 Judge
     |                         |
     | "? 50" + flush -------->|
     |                         |
     |<------------- reply '<' |
```

---

## 1.6 `endl` vs `\n`

Conceptually:

```text
'\n'
→ newline

endl
→ newline
→ flush
```

Interactive-friendly:

```cpp
cout << "? " << x << endl;
```

Equivalent explicit flush:

```cpp
cout << "? " << x << '\n' << flush;
```

---

## 1.7 Query / Answer Protocol

The lecture hidden-number protocol uses:

```text
? X
```

for a query and:

```text
! X
```

for the final answer.

Example:

```text
? 10
=
! 10
```

Keep these roles separate:

```text
QUERY:
? x

REPLY:
< / > / =

FINAL:
! answer
```

---

## 1.8 Binary Search as an Interactive Strategy

Binary search works when the reply gives direction.

```text
L ---------------- mid ---------------- R
```

If:

```text
mid < hidden
```

discard left half.

If:

```text
mid > hidden
```

discard right half.

One query removes roughly half the candidates.

---

## 1.9 Query Complexity of Binary Search

After `q` queries:

```text
remaining possibilities ≈ N / 2^q
```

Need:

```text
N / 2^q <= 1
```

So:

```text
2^q >= N
```

Hence:

```text
q = O(log N)
```

### Tiny Dry Run

```text
N = 100
```

```text
2^6 = 64
2^7 = 128
```

So about:

```text
7
```

binary-search decisions isolate one value.

---

## 1.10 Randomization

Sometimes there is no left/right pattern.

Then the lecture's second important tool is:

```text
Randomization
```

If desired positions occupy a large fraction of the array, random sampling has a strong chance to hit one.

---

## 1.11 Majority Element Probability

A majority element occurs:

```text
> N/2
```

times.

So for a uniformly random index:

```text
P(hit majority) > 1/2
```

and:

```text
P(miss) < 1/2
```

### Tiny Dry Run

If:

```text
N = 10
majority occurs 6 times
```

then:

```text
P(hit)  = 6/10 = 0.6
P(miss) = 4/10 = 0.4
```

---

## 1.12 Repeated Random Trials

Use the safe bound:

```text
P(miss one) <= 1/2
```

For `q` independent random trials:

```text
P(miss all q)
<=
(1/2)^q
```

### Tiny Dry Run

```text
q = 3
```

```text
P(fail)
<=
1/2 × 1/2 × 1/2
=
1/8
=
12.5%
```

### q = 30

```text
P(fail) <= 1 / 2^30 ≈ 9.31 × 10^-10
```

### q = 100

```text
P(fail) <= 1 / 2^100 ≈ 7.89 × 10^-31
```

Repeated random trials make failure exponentially small.

---

## 1.13 Custom Interactor for Local Testing

The lecture recommends a custom interactor.

Real:

```text
print query
→ judge
→ read reply
```

Local:

```text
reply = interactor(query)
```

### Tiny Dry Run

```text
hidden = 101
```

```text
interactor(100) → '<'
interactor(102) → '>'
interactor(101) → '='
```

This lets you test the search logic without the real judge.

---

## 1.14 Recognition Checklist

```text
1. What information is hidden?

2. What may I query?

3. What exactly does one reply tell me?

4. What is the query limit?

5. Did I flush every query?

6. Does the reply give left/right direction?
   → binary / ternary search

7. No direction, but desired states are dense?
   → randomization

8. What is the final-answer format?

9. Can I simulate the judge locally?
```

---

# 2. Problem 1 — Hidden Number

## 2.1 What It Asks

There is a hidden number.

You can query a number.

The computer tells whether your query is:

```text
less than hidden
greater than hidden
equal to hidden
```

Find the hidden number quickly.

When found:

```text
! X
```

---

## 2.2 What Result Is Expected

Suppose:

```text
hidden = 10
```

Possible interaction:

```text
? 2
<

? 11
>

? 10
=

! 10
```

Final result:

```text
10
```

---

## 2.3 What One Query Tells Us

Current range:

```text
[L,R]
```

Query:

```text
mid
```

### Reply `<`

Lecture protocol means:

```text
mid < hidden
```

So:

```text
hidden ∈ [mid+1,R]
```

### Reply `>`

Means:

```text
mid > hidden
```

So:

```text
hidden ∈ [L,mid-1]
```

### Reply `=`

Means:

```text
mid = hidden
```

Done.

---

## 2.4 Why Binary Search Fits

The response gives ordered direction:

```text
query too small
or
query too large
or
found
```

ASCII:

```text
L ---------------- mid ---------------- R

reply '<'

discard:
L -------- mid

keep:
               mid+1 ----------- R
```

and:

```text
reply '>'

keep:
L -------- mid-1

discard:
              mid ------------- R
```

Each query discards roughly half.

---

## 2.5 Search-Space Update Rules

```text
mid = left + (right-left)/2
```

Then:

```text
'<'
→ query < hidden
→ left = mid + 1
```

```text
'>'
→ query > hidden
→ right = mid - 1
```

```text
'='
→ answer = mid
→ stop
```

Compact:

```text
ask(mid)

< → go right
> → go left
= → found
```

---

## 2.6 Detailed Dry Run — Hidden Number 10

Let:

```text
hidden = 10
range = [1,16]
```

### Query 1

```text
mid = 8
```

```text
? 8
<
```

Meaning:

```text
8 < 10
```

New range:

```text
[9,16]
```

### Query 2

```text
mid = 12
```

```text
? 12
>
```

Meaning:

```text
12 > 10
```

New range:

```text
[9,11]
```

### Query 3

```text
mid = 10
```

```text
? 10
=
```

Found:

```text
! 10
```

Visual:

```text
[1----------------16]
         ?8
         <
          [9-------16]

              ?12
               >
          [9--11]

           ?10
            =
           FOUND
```

---

## 2.7 Query Complexity

Binary search uses:

```text
O(log N)
```

queries over `N` candidate values.

Examples:

```text
1...100
→ about 7 decisions

1...10000
→ about 14 decisions
```

---

## 2.8 Interactive C++17

This follows the `ask()` helper style shown in the supplied lecture screenshot.

```cpp
#include <bits/stdc++.h>
using namespace std;

char ask(int x) {
    cout << "? " << x << endl;

    char response;
    cin >> response;

    return response;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int left = 1;
    int right = 100;
    int answer = -1;

    while (left <= right) {
        int mid = left + (right - left) / 2;
        char response = ask(mid);

        if (response == '<') {
            left = mid + 1;
        } else if (response == '>') {
            right = mid - 1;
        } else {
            answer = mid;
            break;
        }
    }

    cout << "! " << answer << endl;
    return 0;
}
```

Why:

```cpp
endl
```

after each query?

```text
newline + flush
```

---

## 2.9 Local Custom Interactor

The lecture's local-testing idea can be represented as:

```cpp
#include <bits/stdc++.h>
using namespace std;

int hiddenNumber = 73;

char interactor(int query) {
    if (query == hiddenNumber) {
        return '=';
    }

    if (query < hiddenNumber) {
        return '<';
    }

    return '>';
}

int solveLocally(int left, int right) {
    while (left <= right) {
        int mid = left + (right - left) / 2;
        char response = interactor(mid);

        cout << "? " << mid << " -> " << response << '\n';

        if (response == '<') {
            left = mid + 1;
        } else if (response == '>') {
            right = mid - 1;
        } else {
            return mid;
        }
    }

    return -1;
}

int main() {
    cout << "answer = " << solveLocally(1, 100) << '\n';
    return 0;
}
```

This is local testing code, not the real interactive submission.

---

## 2.10 Recognition Model

When you see:

```text
hidden ordered value
+
reply says:
smaller / greater / equal
```

think:

```text
Binary Search
```

```text
query(mid)
      |
      +-- '<' → hidden larger → right half
      |
      +-- '>' → hidden smaller → left half
      |
      +-- '=' → found
```

---

# 3. Problem 2 — Find an Index of the Majority Element

## 3.1 What It Asks

There is a hidden array.

Find:

```text
any index containing the majority element
```

or report that no such element exists.

You may query an index.

The judge replies:

```text
1
→ that index contains the majority element

0
→ it does not
```

Lecture example:

```text
hidden = [10,11,10]

? 2
0

? 1
1
```

---

## 3.2 Why Binary Search Does Not Apply

The lecture points out that the majority positions have no ordered pattern.

Examples:

```text
5 5 5 3 3

3 3 5 5 5

3 5 5 5 3
```

The majority may occur:

```text
left
right
middle
scattered
```

If query `i` returns `0`, it does not tell us:

```text
search left
or
search right
```

So there is no positional monotonicity.

---

## 3.3 Majority Guarantee

If a majority exists:

```text
count(majority) > N/2
```

Let:

```text
G = number of majority indices
```

Then:

```text
G > N/2
```

Divide by `N`:

```text
G/N > 1/2
```

Since `G/N` is the probability that a uniformly random index is good:

```text
P(hit majority) > 1/2
```

---

## 3.4 Convert Frequency Into Probability

### Teaching Dry Run

Lecture-style hidden array:

```text
[9,10,9,9,9,10,11,10,9,9]
```

`9` appears:

```text
6 times
```

Total:

```text
10
```

So:

```text
P(hit 9) = 6/10 = 0.6
P(miss)  = 4/10 = 0.4
```

A single random query already succeeds with probability above `1/2`.

---

## 3.5 Probability of Missing Repeatedly

Use the upper bound:

```text
P(miss one) <= 1/2
```

Then:

```text
P(miss q independent trials)
<=
(1/2)^q
```

Derivation:

```text
q = 1:
1/2

q = 2:
1/2 × 1/2 = 1/4

q = 3:
1/2 × 1/2 × 1/2 = 1/8

general:
1 / 2^q
```

---

## 3.6 Why 30 / 100 Trials Become Powerful

The lecture illustrates values such as:

```text
q = 30
```

Then:

```text
P(fail) <= 1 / 2^30
≈ 9.31 × 10^-10
```

For:

```text
q = 100
```

```text
P(fail) <= 1 / 2^100
≈ 7.89 × 10^-31
```

Visual:

```text
1 trial      <= 1/2 failure

2 trials     <= 1/4

3 trials     <= 1/8

10 trials    <= 1/1024

30 trials    <= 1/2^30

100 trials   <= 1/2^100
```

---

## 3.7 Randomized Strategy

Lecture idea:

```text
repeat several times:

    choose random index

    ask judge

    if reply == 1:
        output this index
        stop

if every sampled index returns 0:
    report no majority
```

The lecture explicitly demonstrates:

```text
choose 100 random indices
```

The actual number of attempts must still respect:

```text
the problem's query limit
```

This is a probabilistic strategy.

---

## 3.8 Detailed Dry Run

Hidden array:

```text
[9,10,9,9,9,10,11,10,9,9]
```

Good indices:

```text
1,3,4,5,9,10
```

Suppose random choices:

```text
2,8,1
```

### Query 1

```text
? 2
```

Reply:

```text
0
```

### Query 2

```text
? 8
```

Reply:

```text
0
```

### Query 3

```text
? 1
```

Reply:

```text
1
```

Print:

```text
! 1
```

Done.

---

## 3.9 Interactive C++17

Lecture-style randomized skeleton:

```cpp
#include <bits/stdc++.h>
using namespace std;

int ask(int index) {
    cout << "? " << index << endl;

    int response;
    cin >> response;

    return response;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    mt19937 rng(chrono::steady_clock::now().time_since_epoch().count());

    int answer = -1;
    int trials = 100;

    for (int attempt = 0; attempt < trials; ++attempt) {
        int index = uniform_int_distribution<int>(1, n)(rng);

        if (ask(index) == 1) {
            answer = index;
            break;
        }
    }

    cout << "! " << answer << endl;
    return 0;
}
```

Important:

```text
Use the actual statement's query budget.
Use its exact required final format.
```

---

## 3.10 Complexity and Error Probability

Let:

```text
Q = number of random trials
```

Queries:

```text
O(Q)
```

Local work:

```text
O(Q)
```

Failure bound:

```text
P(fail) < (1/2)^Q
```

Examples:

```text
Q = 30
→ roughly below 10^-9

Q = 100
→ around 10^-30 scale
```

---

## 3.11 Recognition Model

When you see:

```text
desired state occupies > half
+
positions have no useful order
```

think:

```text
Random Sampling
```

```text
good fraction > 1/2
        |
        v
random hit > 1/2
        |
        v
repeat Q times
        |
        v
failure < (1/2)^Q
```

---

# 4. How to Test Interactive Problems Locally

The lecture explicitly recommends:

```text
build a Custom Interactor
which acts as the Online Judge
```

Real flow:

```text
your executable
↔
judge executable
```

Local idea:

```text
query
→ local interactor function
→ simulated reply
```

A compact harness matching the lecture idea:

```cpp
#include <bits/stdc++.h>
using namespace std;

char interact(int query, int hidden) {
    if (query == hidden) {
        return '=';
    }

    if (query < hidden) {
        return '<';
    }

    return '>';
}

int findHidden(int left, int right, int hidden) {
    while (left <= right) {
        int mid = left + (right - left) / 2;
        char response = interact(mid, hidden);

        if (response == '<') {
            left = mid + 1;
        } else if (response == '>') {
            right = mid - 1;
        } else {
            return mid;
        }
    }

    return -1;
}

int main() {
    vector<int> tests = {1, 2, 3, 4, 5, 73, 100};

    for (int hidden : tests) {
        cout << "hidden = " << hidden
             << ", found = " << findHidden(1, 100, hidden)
             << '\n';
    }

    return 0;
}
```

Purpose:

```text
test algorithm logic locally
before using the real query protocol
```

---

# 5. Binary Search vs Randomization

| Idea | Hidden Number | Majority Index |
|---|---|---|
| Hidden structure | one ordered value | hidden array positions |
| Query reply | `<`, `>`, `=` | `0` / `1` |
| Positional monotonicity | yes | no |
| Can discard half? | yes | no |
| Key guarantee | ordered answer space | majority occupies `> N/2` |
| Strategy | Binary Search | Randomization |
| Query use | `O(log N)` | chosen random trials |
| Failure | deterministic | probabilistic |

Recognition:

```text
REPLY GIVES DIRECTION?
        |
       YES
        |
        v
BINARY / TERNARY SEARCH
```

```text
NO DIRECTION,
BUT GOOD STATES ARE DENSE?
        |
       YES
        |
        v
RANDOMIZATION
```

---

# 6. Important Pointers From the Lecture

The lecture closes by highlighting:

```text
1. Binary Search / Ternary Search

2. Randomization
```

It also recommends practicing:

```text
Constructive Algorithms
```

for better interactive-problem thinking.

Interpretation:

```text
Binary Search
→ ordered / monotonic response

Ternary Search
→ lecture lists it as another search tool;
  the supplied lecture gives only a brief illustration

Randomization
→ exploit probability when positions are not ordered

Constructive practice
→ improve strategy / query-design thinking
```

The source does not provide a full ternary-search derivation here, so this note intentionally does not invent one.

---

# 7. Common Mistakes

## Mistake 1 — Forgetting to Flush

```text
print query
but keep it buffered
→ judge does not see it
```

Use:

```cpp
cout << "? " << x << endl;
```

---

## Mistake 2 — Reversing `<` and `>`

Lecture protocol:

```text
'<'
means
query < hidden
```

Therefore:

```text
hidden is larger
→ move right
```

Correct:

```cpp
left = mid + 1;
```

---

## Mistake 3 — Mixing Query and Final Answer

```text
? x
```

is a query.

```text
! x
```

is the final answer.

---

## Mistake 4 — Ignoring Query Limit

Always estimate:

```text
number of queries
```

before coding.

---

## Mistake 5 — Forcing Binary Search Without Monotonicity

In the majority-index problem:

```text
query(i) = 0
```

does not tell:

```text
left
or
right
```

So binary search has no basis.

---

## Mistake 6 — Misunderstanding Randomization

One random trial is not enough to claim certainty.

Power comes from:

```text
repeat
+
failure probability multiplication
```

---

## Mistake 7 — Calling Randomized Sampling Deterministically Certain

Even:

```text
100 trials
```

does not make failure probability mathematically zero.

It makes it extremely small.

---

## Mistake 8 — Exceeding the Query Budget

The lecture's 100-trial idea is a strategy example.

The actual statement's allowed query count controls the implementation.

---

## Mistake 9 — Testing Only With Static Input

Interactive logic is:

```text
query
→ response
→ next query
```

A local custom interactor reproduces this stateful flow.

---

# 8. Final Mental Model

## Interactive Flow

```text
HIDDEN INFORMATION
       |
       v
ASK QUERY
       |
       v
FLUSH
       |
       v
READ REPLY
       |
       v
UPDATE KNOWLEDGE
       |
       v
ASK NEXT QUERY
       |
      ...
       |
       v
PRINT FINAL ANSWER
```

---

## Pattern A — Ordered Hidden Value

```text
query(mid)
    |
    +-- '<' → hidden > mid → right half
    |
    +-- '>' → hidden < mid → left half
    |
    +-- '=' → found
```

Therefore:

```text
Binary Search
O(log N) queries
```

---

## Pattern B — Dense Good Set

```text
good positions > N/2
        |
        v
random hit > 1/2
        |
        v
repeat Q times
        |
        v
P(all misses) < (1/2)^Q
```

Therefore:

```text
Randomization
```

---

## 60-Second Recognition

```text
INTERACTIVE PROBLEM
       |
       v
WHAT DOES ONE QUERY TELL ME?
       |
       +----------------------------+
       |                            |
       v                            v
DIRECTION / ORDER?            NO DIRECTION
       |                            |
      YES                           v
       |                     GOOD STATES DENSE?
       v                            |
BINARY / TERNARY                   YES
SEARCH                              |
                                    v
                              RANDOMIZATION
```

---

## Contest Checklist

```text
[ ] Understand hidden information
[ ] Understand exact query format
[ ] Understand exact reply meaning
[ ] Understand final-answer format
[ ] Check query budget
[ ] Flush after every query
[ ] Prove search / probability logic
[ ] Estimate query count
[ ] Build a local interactor if needed
```

---

## One Sentence to Remember

```text
Every interactive query must buy useful information:
use search when replies give direction,
and randomization when good states occupy a large fraction of possibilities.
```
