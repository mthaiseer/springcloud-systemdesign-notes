# Codeforces Div2 A/B/C --- Ad-hoc Pattern Recognition Handbook

**Separated from the original combined handbook.**

**Primary goal:** recognize observation-driven Div2 A/B/C problems
quickly and reduce long statements to a small state, invariant, count,
extreme, difference, or case split.

## Table of Contents

1.  Ad-hoc mental model
2.  60-second scanner
3.  Parity and modulo
4.  Counting / frequency / presence
5.  Min-max / extreme observation
6.  Small casework + direct simulation
7.  Sort to reveal structure
8.  Invariants under operations
9.  Reverse thinking
10. Difference modeling / normalization
11. Decode a long CF statement
12. 60-second recognition drill
13. Ad-hoc learning order

------------------------------------------------------------------------

# 1. Ad-hoc Mental Model

## Ad-hoc

An ad-hoc problem normally says:

> There is no big named algorithm. Notice the right property.

Examples:

``` text
Only parity matters.
Only min and max matter.
Count of negatives is enough.
The answer depends only on n mod 3.
Sort and inspect adjacent gaps.
```

### Mental model

``` text
large statement
      ↓
remove irrelevant information
      ↓
one small observation
```

------------------------------------------------------------------------

------------------------------------------------------------------------

# 1. The 60-Second Scanner

When you open a problem, **do not code immediately**.

## 0--10 seconds --- identify the output

Ask:

``` text
What exactly do I need?

YES / NO?
minimum operations?
maximum score?
a permutation?
any valid array?
number of ways?
```

If the statement says:

``` text
"print any..."
"construct..."
"find a permutation..."
```

immediately activate **constructive mode**.

------------------------------------------------------------------------

## 10--20 seconds --- identify operations

Translate the story into verbs.

``` text
swap
add
subtract
flip
delete
replace
choose
reorder
repeat
```

Example:

``` text
Story:
Alice presses a button and changes one number...

Mathematical version:
Choose i:
a[i] ← -a[i]
```

The mathematical operation is what matters.

------------------------------------------------------------------------

## 20--30 seconds --- ask what cannot change

Check:

``` text
parity?
sum mod k?
gcd?
xor?
number of odd elements?
relative order?
frequency?
sign count?
difference?
```

This is where many B/C problems collapse.

------------------------------------------------------------------------

## 30--40 seconds --- test tiny cases

Always try:

``` text
n = 1
n = 2
n = 3

all equal
already valid
all odd
all even
one exceptional value
minimum possible input
maximum-looking configuration
```

Tiny cases expose casework.

------------------------------------------------------------------------

## 40--50 seconds --- try standard constructions

Before inventing anything complicated, test:

``` text
all 1
all 2
1 2 1 2 ...
1 2 3 1 2 3 ...
1 2 ... n
n ... 2 1
2 1 4 3 6 5 ...
small + large pairing
blocks
mostly same + one correction
```

------------------------------------------------------------------------

## 50--60 seconds --- classify

``` text
ONE direct observation
    → likely A

observation + casework / simple construction
    → likely B

2–3 observations chained together
or invariant + data structure / greedy
    → likely C
```

------------------------------------------------------------------------

------------------------------------------------------------------------

# 3. Core Div2 A Ad-hoc Patterns

# A1. Parity and Modulo

## 60-second signal

Look for:

``` text
odd / even
pairing
split into groups
alternating turns
sum parity
number divisible by k
operation adds/subtracts fixed quantity
```

Immediately test:

``` text
n % 2
sum % 2
countOdd % 2
n % k
```

------------------------------------------------------------------------

## Core idea

Parity compresses infinitely many numbers into only two classes:

``` text
EVEN = 0 mod 2
ODD  = 1 mod 2
```

Rules:

``` text
E + E = E
O + O = E
E + O = O
O + E = O
```

------------------------------------------------------------------------

## Dry run --- equal parity group sums

Suppose total array sum is:

``` text
S = leftSum + rightSum
```

If `leftSum` and `rightSum` have the same parity:

``` text
even + even = even
odd  + odd  = even
```

Therefore:

``` text
S must be even
```

ASCII reasoning:

``` text
group A ─────┐
             ├── total
group B ─────┘

same parity:

E + E → E
O + O → E

Therefore total must be EVEN.
```

The huge coloring/search problem becomes:

``` cpp
cout << (sum % 2 == 0 ? "YES\n" : "NO\n");
```

------------------------------------------------------------------------

## Modulo generalization

Instead of tracking the full number:

``` text
n = 1,000,000,007
```

sometimes all you need is:

``` text
n mod k
```

Because operations change values by multiples of `k`.

If:

``` text
a → a + k
```

then:

``` text
a mod k
```

never changes.

------------------------------------------------------------------------

## Code snippet

``` cpp
long long sum = 0;

for (int x : a)
    sum += x;

if (sum % 2 == 0)
    cout << "YES\n";
else
    cout << "NO\n";
```

General:

``` cpp
if (value % k == requiredRemainder) {
    // possible
}
```

------------------------------------------------------------------------

## Recognition sentence

> "If the operation/grouping only changes values by even amounts or
> multiples of `k`, check parity/modulo before simulating."

------------------------------------------------------------------------

## Practice --- parity/modulo

1.  [CF 959A --- Mahmoud and Ehab and the even-odd
    game](https://codeforces.com/problemset/problem/959/A)
2.  [CF 1857A --- Array
    Coloring](https://codeforces.com/problemset/problem/1857/A)
3.  [CF 1296A --- Array with Odd
    Sum](https://codeforces.com/problemset/problem/1296/A)
4.  [CF 1899A --- Game with
    Integers](https://codeforces.com/problemset/problem/1899/A)
5.  [CF 1475A --- Odd
    Divisor](https://codeforces.com/problemset/problem/1475/A)

------------------------------------------------------------------------

# A2. Counting / Frequency / Presence

## 60-second signal

The exact order appears irrelevant.

Examples:

``` text
How many +1 / -1?
How many zeroes?
Does k occur?
How many letters of each type?
How many odd values?
```

Try replacing the entire array with counts.

------------------------------------------------------------------------

## Compression idea

Instead of:

``` text
[-1, +1, -1, -1, +1, +1]
```

store:

``` text
negative = 3
positive = 3
```

ASCII:

``` text
RAW ARRAY
- + - - + +

      ↓ compress

STATE
neg = 3
pos = 3
```

You have reduced `n` values to 2 integers.

------------------------------------------------------------------------

## Dry run

Suppose operation flips one `-1` to `+1`.

Before:

``` text
neg = 5
pos = 1
```

After one flip:

``` text
neg = 4
pos = 2
```

After two:

``` text
neg = 3
pos = 3
```

ASCII:

``` text
flip #0    pos=1   neg=5
               \   /
difference = 4

flip #1    pos=2   neg=4
difference = 2

flip #2    pos=3   neg=3
difference = 0
```

Notice each flip changes the difference by `2`.

That can later become a formula.

------------------------------------------------------------------------

## Presence pattern

Sometimes the whole problem reduces to:

``` cpp
bool found = false;

for (int x : a)
    if (x == k)
        found = true;

cout << (found ? "YES\n" : "NO\n");
```

Do not invent extra machinery when the statement only needs existence.

------------------------------------------------------------------------

## Frequency code template

``` cpp
map<int,int> freq;

for (int x : a)
    freq[x]++;
```

For small alphabet:

``` cpp
int freq[26] = {};

for (char c : s)
    freq[c - 'a']++;
```

------------------------------------------------------------------------

## Recognition sentence

> "If positions do not matter, compress the input into counts or
> frequencies."

------------------------------------------------------------------------

## Practice --- counting/frequency/presence

1.  [CF 1220A ---
    Cards](https://codeforces.com/problemset/problem/1220/A)
2.  [CF 1878A --- How Much Does Daytona
    Cost?](https://codeforces.com/problemset/problem/1878/A)
3.  [CF 1877A --- Goals of
    Victory](https://codeforces.com/problemset/problem/1877/A)
4.  [CF 1791A --- Codeforces
    Checking](https://codeforces.com/problemset/problem/1791/A)
5.  [CF 1703A --- YES or
    YES?](https://codeforces.com/problemset/problem/1703/A)

------------------------------------------------------------------------

# A3. Min / Max / Extreme Observation

## 60-second signal

Look for:

``` text
largest
smallest
range
minimum possible
maximum possible
closest to an end
all values relative to something
```

Test whether only these matter:

``` text
min
max
max - min
first
last
leftmost
rightmost
```

------------------------------------------------------------------------

## Dry run

Array:

``` text
8 2 10 4 6
```

Suppose the question depends on how spread out values are.

You may not need all pairs.

``` text
min = 2
max = 10

largest possible distance = 10 - 2 = 8
```

ASCII:

``` text
2 ---- 4 ---- 6 ---- 8 ---- 10
^                            ^
min                         max

Everything lies inside [2,10].
```

------------------------------------------------------------------------

## Why extremes work

For any `x,y`:

``` text
min <= x,y <= max
```

so:

``` text
|x-y| <= max-min
```

Therefore, if you seek maximum pair difference, the extreme pair is
sufficient.

------------------------------------------------------------------------

## Code snippet

``` cpp
int mn = *min_element(a.begin(), a.end());
int mx = *max_element(a.begin(), a.end());

cout << mx - mn << '\n';
```

------------------------------------------------------------------------

## First / last violation

A related pattern:

``` text
valid valid valid BAD ... BAD valid
                  ^       ^
               first     last
```

Often only the interval from first bad to last bad matters.

------------------------------------------------------------------------

## Recognition sentence

> "Before checking all pairs, ask whether the answer is forced by
> min/max or the first/last exceptional position."

------------------------------------------------------------------------

## Practice --- extreme observation

1.  [CF 1838A --- Blackboard
    List](https://codeforces.com/problemset/problem/1838/A)
2.  [CF 1805A --- We Need the
    Zero](https://codeforces.com/problemset/problem/1805/A)
3.  [CF 1848A --- Vika and Her
    Friends](https://codeforces.com/problemset/problem/1848/A)
4.  [CF 1858A ---
    Buttons](https://codeforces.com/problemset/problem/1858/A)
5.  [CF 1896A --- Jagged
    Swaps](https://codeforces.com/problemset/problem/1896/A)

------------------------------------------------------------------------

# A4. Small Casework + Direct Simulation

## 60-second signal

Constraints or operations behave differently when:

``` text
n odd / even
x == 1 / x != 1
k == 1 / k > 1
first element special
last element special
one exceptional remainder
```

Do not fear casework.

Good casework is often the intended solution.

------------------------------------------------------------------------

## How to make casework safely

Start with the property that fundamentally changes behavior.

Example:

``` text
if x != 1:
    ...

else:
    ...
```

Then only split further when needed:

``` text
x == 1
   |
   +-- n even
   |
   +-- n odd
```

Avoid random nested conditions.

------------------------------------------------------------------------

## Dry run --- repeated string doubling

Suppose an operation doubles string `x`:

``` text
x ← x + x
```

Example:

``` text
x = "ab"
target substring = "baba"
```

Simulation:

``` text
0: ab
1: abab
2: abababab
```

ASCII growth:

``` text
ab
││
└┴── + ab
 ↓
abab
│  │
└──┴── + abab
   ↓
abababab
```

For small bounded operations, simulation is simpler than forcing a
formula.

------------------------------------------------------------------------

## Code pattern

``` cpp
int ans = 0;

while (!condition() && ans <= LIMIT) {
    performOperation();
    ans++;
}
```

------------------------------------------------------------------------

## Simulation → formula upgrade

If state changes regularly:

``` text
pos += 1
neg -= 1
```

then derive instead of looping:

``` text
neg - x <= pos + x
neg - pos <= 2x
x >= ceil((neg-pos)/2)
```

Integer ceiling:

``` cpp
int x = max(0, (neg - pos + 1) / 2);
```

------------------------------------------------------------------------

## Recognition sentence

> "If only 2--4 meaningful branches exist, enumerate them explicitly; if
> simulation changes state predictably, derive the formula."

------------------------------------------------------------------------

## Practice --- casework/simulation

1.  [CF 1881A --- Don't Try to
    Count](https://codeforces.com/problemset/problem/1881/A)
2.  [CF 1900A --- Cover in
    Water](https://codeforces.com/problemset/problem/1900/A)
3.  [CF 1862B --- Sequence
    Game](https://codeforces.com/problemset/problem/1862/B)
4.  [CF 1858A ---
    Buttons](https://codeforces.com/problemset/problem/1858/A)
5.  [CF 1845A --- Forbidden
    Integer](https://codeforces.com/problemset/problem/1845/A)

------------------------------------------------------------------------

------------------------------------------------------------------------

# 4. Div2 B Ad-hoc Bridge

# B4. Sort to Reveal Structure

## 60-second signal

Input order is arbitrary and the condition involves:

``` text
differences
closest/farthest
pairing
groups
intervals
relative size
```

Ask:

> Does the original order matter?

If not:

``` cpp
sort(a.begin(), a.end());
```

------------------------------------------------------------------------

## Dry run

Original:

``` text
8 1 7 3 2
```

Hard to see structure.

Sorted:

``` text
1 2 3 7 8
```

Now gaps:

``` text
1 → 2 : 1
2 → 3 : 1
3 → 7 : 4   ← important gap
7 → 8 : 1
```

ASCII:

``` text
1--2--3----------7--8
      ^
      large separation
```

------------------------------------------------------------------------

## Pair-extremes pattern

Sorted:

``` text
1 2 3 8 9 10
```

Pair:

``` text
1  ↔ 10
2  ↔ 9
3  ↔ 8
```

Useful when balancing sums/differences or maximizing separation.

------------------------------------------------------------------------

## Adjacent sufficiency

After sorting, if you need to detect whether **any two values are too
close**, you often only need adjacent pairs.

Why?

If:

``` text
a[i] <= a[j] <= a[k]
```

then the closest conflict will appear among neighbors in sorted order.

------------------------------------------------------------------------

## Code

``` cpp
sort(a.begin(), a.end());

for (int i = 1; i < n; ++i) {
    int gap = a[i] - a[i - 1];
    // inspect gap
}
```

------------------------------------------------------------------------

## Recognition sentence

> "If original positions are irrelevant, sort first; many global pair
> conditions turn into adjacent checks."

------------------------------------------------------------------------

## Practice --- sorting observation

1.  [CF 1798A ---
    Showstopper](https://codeforces.com/problemset/problem/1798/A)
2.  [CF 1833B --- Restore the
    Weather](https://codeforces.com/problemset/problem/1833/B)
3.  [CF 1793C --- Dora and
    Search](https://codeforces.com/problemset/problem/1793/C)
4.  [CF 1353B --- Two Arrays And
    Swaps](https://codeforces.com/problemset/problem/1353/B)
5.  [CF 1538C --- Challenging
    Cliffs](https://codeforces.com/problemset/problem/1538/C)

------------------------------------------------------------------------

------------------------------------------------------------------------

# 5. Div2 C Ad-hoc Bridge

# C1. Invariants Under Operations

## 60-second signal

The statement allows an operation **many times**.

Immediately ask:

> What property stays unchanged no matter how many operations I perform?

Candidates:

``` text
parity
sum mod k
gcd
xor
difference
sign product
relative order
frequency class
```

------------------------------------------------------------------------

## Dry run --- adding 2

Operation:

``` text
a[i] ← a[i] + 2
```

Try:

``` text
3 → 5 → 7 → 9
```

Always odd.

``` text
4 → 6 → 8 → 10
```

Always even.

ASCII:

``` text
ODD lane:
3 → 5 → 7 → 9 → ...

EVEN lane:
4 → 6 → 8 → 10 → ...

No bridge between lanes.
```

Therefore parity is invariant.

If target requires:

``` text
odd → even
```

answer is impossible.

------------------------------------------------------------------------

## Algebra

Operation:

``` text
x ← x + 2k
```

Modulo 2:

``` text
(x + 2k) mod 2
= x mod 2
```

So parity cannot change.

General:

``` text
x ← x + mk
```

means:

``` text
x mod k
```

is unchanged.

------------------------------------------------------------------------

## Invariant workflow

``` text
operation
   ↓
write algebra
   ↓
test:
  parity?
  modulo?
  gcd?
  xor?
   ↓
necessary condition
   ↓
if violated → impossible
```

------------------------------------------------------------------------

## Code skeleton

``` cpp
if ((start & 1) != (target & 1)) {
    cout << "NO\n";
    return;
}
```

------------------------------------------------------------------------

## Recognition sentence

> "Repeated operation problem → search for an invariant before searching
> for a sequence of operations."

------------------------------------------------------------------------

## Practice --- invariants

1.  [CF 1367B --- Even
    Array](https://codeforces.com/problemset/problem/1367/B)
2.  [CF 1475A --- Odd
    Divisor](https://codeforces.com/problemset/problem/1475/A)
3.  [CF 1690F --- Shifting
    String](https://codeforces.com/problemset/problem/1690/F)
4.  [CF 1669C --- Odd/Even
    Increments](https://codeforces.com/problemset/problem/1669/C)
5.  [CF 1624C --- Division by Two and
    Permutation](https://codeforces.com/problemset/problem/1624/C)

------------------------------------------------------------------------

# C2. Reverse Thinking

## 60-second signal

Forward process has many choices.

Example:

``` text
from x you may:
x → 2x
x → x+1
x → x-1
...
```

The branching factor grows.

Ask:

> Is the previous state almost forced when I start from the target?

------------------------------------------------------------------------

## Example

Forward:

``` text
x → 2x
x → x+1
```

Suppose target is `23`.

Backward:

``` text
23 odd
  ↓
probably came from 22 using +1

22 even
  ↓
could come from 11 using ×2
```

ASCII:

``` text
FORWARD

        22
       /  \
     44   23
    / \   / \
   ...

branches explode


BACKWARD

23
↓ -1
22
↓ /2
11
↓ -1
10
...
```

------------------------------------------------------------------------

## Why reverse can be easier

Forward:

``` text
one state → many possible next states
```

Reverse:

``` text
target → often one sensible predecessor
```

This converts search into greedy.

------------------------------------------------------------------------

## Another use: construct from right to left

Constraint:

``` text
a[i] depends on a[i+1]
```

Then build:

``` text
a[n]
a[n-1]
...
a[1]
```

not left-to-right.

------------------------------------------------------------------------

## Code skeleton

``` cpp
while (target > start) {
    if (target % 2 == 0) {
        target /= 2;
    } else {
        target--;
    }
}
```

The exact rules depend on the problem, but the direction shift is the
pattern.

------------------------------------------------------------------------

## Recognition sentence

> "If forward choices branch heavily, start from the required final
> state and inspect its possible predecessors."

------------------------------------------------------------------------

## Practice --- reverse thinking

1.  [CF 727A --- Transformation: from A to
    B](https://codeforces.com/problemset/problem/727/A)
2.  [CF 1703C ---
    Cypher](https://codeforces.com/problemset/problem/1703/C)
3.  [CF 1881A --- Don't Try to
    Count](https://codeforces.com/problemset/problem/1881/A)
4.  [CF 1624C --- Division by Two and
    Permutation](https://codeforces.com/problemset/problem/1624/C)
5.  [CF 1791C --- Prepend and
    Append](https://codeforces.com/problemset/problem/1791/C)

------------------------------------------------------------------------

# C3. Difference Modeling / Normalization

## 60-second signal

You are comparing:

``` text
two arrays
two states
current vs target
relative values
equalization
```

Try replacing absolute values with:

``` text
difference
ratio
offset
normalized value
```

------------------------------------------------------------------------

## Example --- common offset

Suppose operation adds the same `x`:

``` text
a[i] + x = b[i]
```

Then:

``` text
x = b[i] - a[i]
```

For every relevant position, the same `x` must work.

Example:

``` text
a = [3, 7, 10]
b = [8,12,15]
```

Differences:

``` text
5,5,5
```

ASCII:

``` text
3  ──+5──>  8
7  ──+5──> 12
10 ──+5──> 15

same transformation
```

But:

``` text
b = [8,11,15]

differences:
5,4,5

inconsistent
```

------------------------------------------------------------------------

## Normalize by subtracting minimum

``` text
10 20 30
```

If translation does not matter:

``` text
subtract 10

0 10 20
```

You removed irrelevant absolute position.

------------------------------------------------------------------------

## Normalize by gcd

``` text
12 18 24
```

``` text
gcd = 6
```

Normalize:

``` text
2 3 4
```

Sometimes reachability depends only on this relative structure.

------------------------------------------------------------------------

## Difference array

Original:

``` text
a = [5,8,11,15]
```

Difference:

``` text
d = [3,3,4]
```

ASCII:

``` text
5 --3--> 8 --3--> 11 --4--> 15
```

If operations affect ranges, differences may make the operation local.

------------------------------------------------------------------------

## Code

``` cpp
vector<long long> diff;

for (int i = 1; i < n; ++i)
    diff.push_back(a[i] - a[i - 1]);
```

Two arrays:

``` cpp
long long need = b[0] - a[0];

for (int i = 1; i < n; ++i) {
    if (b[i] - a[i] != need) {
        // inconsistent
    }
}
```

------------------------------------------------------------------------

## Recognition sentence

> "If absolute values look noisy, subtract one state from the other or
> normalize until only the relative structure remains."

------------------------------------------------------------------------

## Practice --- difference/normalization

1.  [CF 1832C --- Contrast
    Value](https://codeforces.com/problemset/problem/1832/C)
2.  [CF 1772D --- Absolute
    Sorting](https://codeforces.com/problemset/problem/1772/D)
3.  [CF 1862B --- Sequence
    Game](https://codeforces.com/problemset/problem/1862/B)
4.  [CF 1833C --- Vlad Building Beautiful
    Array](https://codeforces.com/problemset/problem/1833/C)
5.  [CF 1618C --- Paint the
    Array](https://codeforces.com/problemset/problem/1618/C)

------------------------------------------------------------------------

------------------------------------------------------------------------

# 8. How to Decode a Long CF Statement

Use this worksheet.

## Step 1 --- remove story nouns

Example:

``` text
"Hero defeats monsters and receives happiness..."
```

Replace with:

``` text
choose index i
gain a[i]
pay cost based on i
```

------------------------------------------------------------------------

## Step 2 --- define mathematical state

``` text
array a
current index
selected set
score
```

------------------------------------------------------------------------

## Step 3 --- write operation in one line

Bad:

``` text
I can perform some operation described in paragraph 4...
```

Good:

``` text
choose i:
score += a[i]
```

or:

``` text
a[i] ← a[i] + x
```

------------------------------------------------------------------------

## Step 4 --- write objective

``` text
maximize:
Σ reward - Σ penalty

minimize:
number of operations

construct:
p satisfying conditions C1,C2,C3
```

------------------------------------------------------------------------

## Step 5 --- split constant and variable terms

If:

``` text
score = a[i] + a[j] - k*i
```

ask which part depends on the current choice.

This frequently exposes:

``` text
prefix maximum
top k candidates
greedy
```

------------------------------------------------------------------------

## Step 6 --- test tiny cases manually

Use:

``` text
n = 1
n = 2
n = 3
```

Write actual state transitions.

Do not only stare at samples.

------------------------------------------------------------------------

## Step 7 --- classify the signal

``` text
odd/even          → parity
many operations   → invariant
any construction  → simplest template
arbitrary order   → sorting
current→target    → differences
branching process → reverse
adjacency         → alternating/local repair
```

------------------------------------------------------------------------

------------------------------------------------------------------------

# 9. The 60-Second Pattern Recognition Drill

Do this without coding.

Take 10 problems.

For each problem allow exactly **60 seconds**.

Write only:

``` text
Problem:
Likely pattern:
Trigger:
Invariant:
Construction candidate:
Complexity guess:
```

Then stop.

Do not solve.

After all 10, go back and actually solve them.

This trains **classification separately from implementation**.

------------------------------------------------------------------------

## Example card

``` text
Problem:
Forbidden Integer

Trigger:
"take arbitrary amount"
"cannot use x"
"sum = n"

Likely pattern:
constructive + parity

First construction:
all 1s

Failure:
x = 1

Fallback:
all 2s

Failure:
n odd

Correction:
one 3 + remaining 2s

Pattern label:
mostly-same + parity correction
```

That is exactly the kind of mental compression you want.

------------------------------------------------------------------------

------------------------------------------------------------------------

# 13. Ad-hoc Learning Order

``` text
LEVEL 1
parity / modulo
counting / frequency
min-max / extremes
small casework
direct simulation
        ↓
LEVEL 2
sorting observation
simulation → formula
first/last exceptional position
difference modeling
        ↓
LEVEL 3
invariants
reverse thinking
normalization
        ↓
LEVEL 4
combine observation with:
prefix/suffix
two pointers
binary search
number theory
maps/sets/frequencies
greedy
```

## Ad-hoc recognition question

Before searching for an algorithm, ask:

``` text
Can I throw away most of the information?

Does only...
parity?
remainder?
frequency?
min/max?
first/last?
difference?
an invariant?
...matter?
```

The goal is not to memorize an "ad-hoc algorithm."\
The goal is to become faster at discovering the **one observation that
simplifies the problem**.
