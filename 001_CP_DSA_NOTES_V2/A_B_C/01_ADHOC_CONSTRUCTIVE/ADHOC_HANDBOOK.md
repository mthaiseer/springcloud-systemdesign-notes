# Codeforces Div2 A/B/C --- Ad-hoc Pattern Recognition Handbook

**Separated from the original combined handbook.**

**Primary goal:** recognize observation-driven Div2 A/B/C problems

**Detailed worked-example edition:** every main A/B/C ad-hoc pattern now
includes a deeper step-by-step example with ASCII diagrams, state
compression, failed/naive thinking, the key observation, algebra where
useful, and the reusable recognition pipeline. quickly and reduce long
statements to a small state, invariant, count, extreme, difference, or
case split.

## Table of Contents

- [1. Ad-hoc Mental Model](#1-ad-hoc-mental-model)
- [2. The 60-Second Scanner](#2-the-60-second-scanner)
- [3. Div2 A Ad-hoc Patterns](#3-core-div2-a-ad-hoc-patterns)
  - [A1. Parity and Modulo](#a1-parity-and-modulo)
  - [A2. Counting / Frequency / Presence](#a2-counting--frequency--presence)
  - [A3. Min / Max / Extreme Observation](#a3-min--max--extreme-observation)
  - [A4. Small Casework + Direct Simulation](#a4-small-casework--direct-simulation)
- [4. Div2 B Ad-hoc Bridge](#4-div2-b-ad-hoc-bridge)
  - [B4. Sort to Reveal Structure](#b4-sort-to-reveal-structure)
- [5. Div2 C Ad-hoc Bridge](#5-div2-c-ad-hoc-bridge)
  - [C1. Invariants Under Operations](#c1-invariants-under-operations)
  - [C2. Reverse Thinking](#c2-reverse-thinking)
  - [C3. Difference Modeling / Normalization](#c3-difference-modeling--normalization)
- [8. How to Decode a Long CF Statement](#8-how-to-decode-a-long-cf-statement)
- [9. The 60-Second Pattern Recognition Drill](#9-the-60-second-pattern-recognition-drill)
- [13. Ad-hoc Learning Order](#13-ad-hoc-learning-order)

> Learn each pattern as **picture -> compressed state -> algebra -> code decision**. Do not memorize the final `if`.

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

# 2. The 60-Second Scanner

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

## Visual Problem-Solving Worksheet

Use this before opening a hint:

```text
+----------------------------------------------------------+
| 1. OUTPUT: What exactly must I print/find?               |
| 2. OPERATION: Rewrite the story in ONE math line.        |
| 3. THROW AWAY: order? exact values? positions?           |
| 4. COMPRESS: parity/count/min-max/remainder/difference?  |
| 5. DRAW: number line / buckets / state transition.       |
| 6. TINY CASES: n=1,2,3; all same; one exceptional.       |
| 7. ALGEBRA: after x operations, what is the new state?   |
| 8. CLASSIFY: parity/count/extreme/sort/invariant/reverse? |
+----------------------------------------------------------+

STATEMENT -> MATH OPERATION -> PICTURE -> TINY STATE
          -> CONDITION/FORMULA -> CODE
```

------------------------------------------------------------------------

# 3. Core Div2 A Ad-hoc Patterns

# A1. Parity and Modulo

## Visual Learning Map

Think of parity as **two train tracks**:

```text
EVEN TRACK: ... -4 -2  0  2  4  6  8 ...
ODD  TRACK: ... -3 -1  1  3  5  7  9 ...

1,000,002 --compress--> E
999,999   --compress--> O
```

**Real world:** every number wears only an EVEN or ODD team shirt. If the question only cares about parity, the printed number is irrelevant.

For two group sums `L` and `R` to have the same parity:

```text
L=E, R=E -> L+R=E
L=O, R=O -> L+R=E
                 |
                 +--> total must be EVEN
```

Algebra:

```text
S = L + R
L % 2 == R % 2
=> S % 2 == 0
```

Recognition:

```text
odd/even/divisibility/fixed +/-k
              |
              v
      discard exact values
              |
              v
       keep x%2 or x%k
              |
              v
       derive condition
```


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

## Detailed Worked Example --- A1 Parity and Modulo

Suppose the problem asks:

``` text
Can we split the array into two groups
whose sums have the same parity?
```

Take:

``` text
a = [3, 5, 2, 4]
```

### Step 1 --- Ignore exact values for a moment

Convert each number to parity:

``` text
3 -> O
5 -> O
2 -> E
4 -> E
```

So:

``` text
original: 3 5 2 4
parity:   O O E E
```

Already the problem is smaller.

### Step 2 --- What does "same parity group sums" imply?

Let:

``` text
leftSum  = L
rightSum = R
```

We need:

``` text
L and R have same parity
```

Only two possibilities:

``` text
L = E and R = E
or
L = O and R = O
```

Now add the group sums:

``` text
total = L + R
```

Case 1:

``` text
E + E = E
```

Case 2:

``` text
O + O = E
```

So in both cases:

``` text
total sum MUST be even
```

ASCII:

``` text
Group 1 ----\
             >---- total sum
Group 2 ----/

same parity:

E + E = E
O + O = E

therefore total = EVEN
```

### Step 3 --- Test the sample array

``` text
3 + 5 + 2 + 4 = 14
```

`14` is even.

So the necessary parity condition is satisfied.

### Step 4 --- Why this observation matters

A naive thought might be:

``` text
Try every possible split
```

That can be exponential.

But parity reduces the whole problem to:

``` text
sum % 2
```

The huge search collapses into one bit of information.

### Step 5 --- Modulo generalization

Suppose operation is:

``` text
x -> x + 5
```

Take:

``` text
x = 12
```

Sequence:

``` text
12 -> 17 -> 22 -> 27 -> ...
```

Remainders mod 5:

``` text
12 % 5 = 2
17 % 5 = 2
22 % 5 = 2
27 % 5 = 2
```

ASCII:

``` text
12 ----+5----> 17 ----+5----> 22 ----+5----> 27
 |               |               |               |
 mod 5 = 2       mod 5 = 2       mod 5 = 2       mod 5 = 2
```

So:

``` text
adding multiples of 5
        ↓
remainder mod 5 does not change
```

### Recognition Pipeline

``` text
operation / condition mentions
odd-even / multiples / divisibility
        ↓
throw away exact values
        ↓
keep only remainder class
        ↓
derive invariant / necessary condition
```

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

### Hints - open only after your own attempt

1. 959A: Test only n parity.
2. 1857A: Same-parity group sums force a condition on the total sum.
3. 1296A: An odd sum needs an odd number of odd elements.
4. 1899A: Tabulate n mod 3 for tiny n.
5. 1475A: Repeatedly divide by 2; identify the one special number class.

------------------------------------------------------------------------

# A2. Counting / Frequency / Presence

## Visual Learning Map

If positions do not matter, pour equal objects into **buckets**:

```text
- + - - + + -
      |
      v
NEG bucket: o o o o   neg=4
POS bucket: o o o     pos=3
```

**Real world:** a flip `-1 -> +1` is one person changing teams.

```text
before: NEG=5  POS=1  gap=4
after : NEG=4  POS=2  gap=2
```

Why did the gap fall by 2?

```text
newGap = (neg-1) - (pos+1)
       = neg-pos-2
```

After `x` flips:

```text
neg' = neg-x
pos' = pos+x

need pos' >= neg'
pos+x >= neg-x
2x >= neg-pos
x >= ceil((neg-pos)/2)
```

Recognition test: **If I randomly shuffle the array, does the answer stay the same?** If yes, counts/frequencies are strong candidates.


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

## Detailed Worked Example --- A2 Counting / Frequency / Presence

Suppose:

``` text
a = [-1, -1, -1, -1, -1, +1]
```

An operation flips one `-1` into `+1`.

We want eventually:

``` text
positive count >= negative count
```

### Step 1 --- Do positions matter?

No.

Whether the array is:

``` text
- - - - - +
```

or:

``` text
- + - - - -
```

the operation only changes the counts.

So compress:

``` text
neg = 5
pos = 1
```

ASCII:

``` text
RAW ARRAY
- - - - - +

      ↓ throw away positions

STATE
neg = 5
pos = 1
```

### Step 2 --- Simulate one flip on COUNTS

One flip:

``` text
-1 -> +1
```

Therefore:

``` text
neg decreases by 1
pos increases by 1
```

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

After two flips:

``` text
neg = 3
pos = 3
```

ASCII:

``` text
start:
pos = 1
neg = 5
gap = 4

flip one:
pos = 2
neg = 4
gap = 2

flip two:
pos = 3
neg = 3
gap = 0
```

### Step 3 --- Notice the gap changes by 2

Define:

``` text
gap = neg - pos
```

Initially:

``` text
gap = 5 - 1 = 4
```

After one flip:

``` text
gap = 4 - 2 = 2
```

Why minus 2?

Because:

``` text
neg -= 1
pos += 1
```

So:

``` text
newGap
= (neg - 1) - (pos + 1)
= neg - pos - 2
```

Thus every flip reduces the gap by exactly `2`.

### Step 4 --- Derive instead of simulating

We need:

``` text
neg - x <= pos + x
```

Move terms:

``` text
neg - pos <= 2x
```

So:

``` text
x >= (neg - pos)/2
```

For integers:

``` text
x = ceil((neg-pos)/2)
```

This is the ad-hoc progression:

``` text
array
  ↓
counts
  ↓
count transition
  ↓
regular change
  ↓
formula
```

### Presence sub-pattern

Suppose the problem only asks:

``` text
Does value k appear?
```

Then do not sort, count everything, or use DP.

Example:

``` text
a = [7, 2, 9, 4]
k = 9
```

Scan:

``` text
7 != 9
2 != 9
9 == 9  -> found
```

Stop.

The observation is:

``` text
Question asks existence
        ↓
state needed = one boolean
```

That is classic ad-hoc compression.

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

### Hints - open only after your own attempt

1. 1220A: Which letters uniquely identify one and zero?
2. 1878A: Existence needs only one boolean.
3. 1877A: Write the total-sum equation.
4. 1791A: Membership in a fixed tiny string is enough.
5. 1703A: Normalize case, then compare.

------------------------------------------------------------------------

# A3. Min / Max / Extreme Observation

## Visual Learning Map

Draw a number line:

```text
2 ---- 4 ---- 6 ---- 8 ---- 10
^                             ^
MIN                           MAX

largest separation = MAX-MIN = 8
```

**Real world:** the farthest two houses on one straight road must be at the two extreme occupied positions.

Algebra:

```text
min <= x,y <= max
=> |x-y| <= max-min
```

Boundary variant:

```text
G G G B B B G G
      ^   ^
    first last

outside [first,last] may already be irrelevant
```

Recognition: maximum/minimum/spread/boundary -> draw a number line -> test min/max or first/last before checking all pairs.


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

## Detailed Worked Example --- A3 Min / Max / Extreme Observation

Take:

``` text
a = [8, 2, 10, 4, 6]
```

Suppose we want the maximum possible difference between two values.

### Step 1 --- Naive idea

Check every pair:

``` text
|8-2|  = 6
|8-10| = 2
|8-4|  = 4
|8-6|  = 2
|2-10| = 8
...
```

This is unnecessary.

### Step 2 --- Sort mentally

``` text
2 4 6 8 10
```

Number line:

``` text
2 ---- 4 ---- 6 ---- 8 ---- 10
^                              ^
smallest                     largest
```

Every value lies inside:

``` text
[min, max] = [2,10]
```

### Step 3 --- Bound any pair

For any two values `x,y`:

``` text
2 <= x,y <= 10
```

Therefore:

``` text
|x-y| <= 10-2 = 8
```

So the theoretical maximum is:

``` text
max - min
```

And the pair `(2,10)` achieves it.

Thus:

``` text
answer = 10 - 2 = 8
```

### Why this is an ad-hoc observation

The input contains `n` values.

But the answer needs only:

``` text
minimum
maximum
```

So:

``` text
n values
  ↓
2 extreme values
  ↓
answer
```

### First / Last Bad Position Variant

Suppose:

``` text
good good good BAD BAD BAD good good
               ^       ^
             first    last
```

Example indices:

``` text
1 2 3 4 5 6 7 8
G G G B B B G G
      ^   ^
      4   6
```

If the operation only needs to fix the invalid middle region, then
positions outside `[4,6]` may be irrelevant.

So another extreme pattern is:

``` text
first exceptional position
last exceptional position
```

Recognition:

``` text
global-looking array
      ↓
ask whether only boundaries/extremes matter
      ↓
min/max or first/last
```

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

### Hints - open only after your own attempt

1. 1838A: Inspect what an extreme value tells you about possible differences.
2. 1805A: XOR all elements and examine n parity.
3. 1848A: Convert coordinates to parity classes.
4. 1858A: Consume the shared resource first, then compare turns.
5. 1896A: Ask which element must be movable to the front.

------------------------------------------------------------------------

# A4. Small Casework + Direct Simulation

## Visual Learning Map

Good casework is a small **decision tree**:

```text
              special condition?
                /          \
              YES          NO
               |            |
          special world   normal world
```

**Real world:** an elevator may have a special rule only on floor 1; separating that case is clearer than forcing one giant formula.

Simulation example `x <- x+x`:

```text
step 0: ab                len=2
          | double
step 1: abab              len=4
          | double
step 2: abababab          len=8
```

If a transition repeats regularly, upgrade simulation to algebra:

```text
pos' = pos+x
neg' = neg-x
need pos' >= neg'
=> 2x >= neg-pos
```


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

## Detailed Worked Example --- A4 Small Casework + Direct Simulation

Suppose a process repeatedly doubles a string:

``` text
x <- x + x
```

Start:

``` text
x = "ab"
```

Target substring:

``` text
"baba"
```

### Step 1 --- Simulate literally

Initial:

``` text
ab
```

Does `"baba"` occur?

``` text
ab
XX
```

No.

After one operation:

``` text
ab + ab = abab
```

Check:

``` text
abab
```

Still no `"baba"`.

After two operations:

``` text
abab + abab = abababab
```

Now:

``` text
abababab
 ^^^^
 baba
```

Yes.

ASCII growth:

``` text
ab
│
├── append ab
↓
abab
│
├── append abab
↓
abababab
```

### Step 2 --- Why simulation is enough

Length doubles every time:

``` text
2
4
8
16
32
...
```

So the state grows very quickly.

If constraints guarantee only a few doublings are needed, brute
simulation is simpler and safer than deriving a complex formula.

### Step 3 --- Casework example

Suppose behavior depends on whether `x == 1`.

Instead of random nested `if`s:

``` text
if x == 1:
    special world
else:
    normal world
```

Then refine only if necessary:

``` text
x == 1
   |
   +-- n even
   |
   +-- n odd
```

ASCII decision tree:

``` text
          x == 1 ?
          /      \
       yes        no
       |           |
   check n       normal
    /   \
 even   odd
```

### Simulation → Formula Upgrade

Suppose every step does:

``` text
pos += 1
neg -= 1
```

After `x` steps:

``` text
pos' = pos + x
neg' = neg - x
```

Need:

``` text
pos' >= neg'
```

Substitute:

``` text
pos + x >= neg - x
```

So:

``` text
2x >= neg - pos
```

and:

``` text
x >= ceil((neg-pos)/2)
```

This is a common ad-hoc evolution:

``` text
simulate tiny cases
      ↓
spot regular transition
      ↓
derive algebra
```

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

### Hints - open only after your own attempt

1. 1881A: Double the string a bounded number of times.
2. 1900A: Find the structural condition that immediately fixes the answer.
3. 1862B: Compare adjacent pairs and repair only violations.
4. 1858A: Split by who receives the extra move.
5. 1845A: Try all 1s; repair the x=1 failure with 2/3.

------------------------------------------------------------------------

------------------------------------------------------------------------

# 4. Div2 B Ad-hoc Bridge

# B4. Sort to Reveal Structure

## Visual Learning Map

Sorting turns scattered values into a visible number line:

```text
raw:    8  1  7  3  2
sort
  |
  v
        1--2--3----------7--8
        gap1 gap1  gap4   gap1
```

**Real world:** line people up by height before looking for the closest heights.

For sorted `a[i] <= a[j] <= a[k]`:

```text
a[k]-a[i] = (a[j]-a[i]) + (a[k]-a[j])
```

So many closest-pair/conflict questions become adjacent checks.

Recognition: original order irrelevant -> sort -> inspect adjacent gaps, blocks, or extreme pairings. **Do not sort if original indices/order matter.**


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

## Detailed Worked Example --- B4 Sort to Reveal Structure

Input:

``` text
8 1 7 3 2
```

Suppose the problem asks about close values or large gaps.

### Step 1 --- Raw order hides the structure

``` text
8 1 7 3 2
```

Adjacent differences in original order:

``` text
|8-1| = 7
|1-7| = 6
|7-3| = 4
|3-2| = 1
```

But these adjacencies may be meaningless if original positions are
irrelevant.

### Step 2 --- Ask the key question

``` text
Does the original order matter?
```

If NO, sort:

``` text
1 2 3 7 8
```

### Step 3 --- Now inspect gaps

``` text
1 -> 2 : 1
2 -> 3 : 1
3 -> 7 : 4
7 -> 8 : 1
```

ASCII number line:

``` text
1--2--3----------7--8
      ^^^^^^^^^^
        big gap
```

The important structure is now obvious.

### Step 4 --- Why adjacent checks can be enough

Suppose we need to know whether any two values are within distance
`< d`.

Sorted:

``` text
a[i] <= a[j] <= a[k]
```

If `a[i]` and `a[k]` are close, then at least one neighboring gap inside
that interval is also small.

So after sorting, many pairwise conditions collapse to:

``` text
check consecutive values only
```

### Pair Extremes Variant

Sorted:

``` text
1 2 3 8 9 10
```

If we want maximum separation:

``` text
1 <-> 10
2 <-> 9
3 <-> 8
```

ASCII:

``` text
1  2  3  8  9  10
|                 |
+-----------------+
   biggest spread
```

Recognition pipeline:

``` text
order irrelevant
      ↓
sort
      ↓
structure appears
      ↓
adjacent gaps / extreme pairs / groups
```

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

### Hints - open only after your own attempt

1. 1798A: Normalize each swappable pair into small/large roles.
2. 1833B: Sort while preserving the output indices that matter.
3. 1793C: Track current min/max and inspect the two ends.
4. 1353B: Pair smallest candidates with largest replacements.
5. 1538C: The smallest adjacent gap after sorting is central.

------------------------------------------------------------------------

------------------------------------------------------------------------

# 5. Div2 C Ad-hoc Bridge

# C1. Invariants Under Operations

## Visual Learning Map

An invariant is a label the operation cannot erase.

For `x <- x+2`:

```text
ODD ROAD : 1 -> 3 -> 5 -> 7 -> 9 -> ...
EVEN ROAD: 2 -> 4 -> 6 -> 8 -> 10 -> ...

             NO BRIDGE
```

**Real world:** two train lines with no interchange. Riding longer cannot move you to the other line.

Algebra:

```text
x' = x+2
x' mod 2 = (x+2) mod 2 = x mod 2
```

General:

```text
x' = x + m*k
=> x' mod k = x mod k
```

Recognition: repeated operation -> write one operation algebraically -> ask what cannot change -> parity/mod/gcd/xor/sign/order.


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

## Detailed Worked Example --- C1 Invariants Under Operations

Operation:

``` text
x <- x + 2
```

Suppose we ask whether `3` can become `10`.

### Step 1 --- Simulate a few steps

From `3`:

``` text
3 -> 5 -> 7 -> 9 -> 11 -> ...
```

All are odd.

From `4`:

``` text
4 -> 6 -> 8 -> 10 -> ...
```

All are even.

ASCII lanes:

``` text
ODD LANE:
3 -> 5 -> 7 -> 9 -> 11 -> ...

EVEN LANE:
4 -> 6 -> 8 -> 10 -> 12 -> ...

No edge connects the lanes.
```

### Step 2 --- Identify what never changes

Parity never changes.

Why?

``` text
x' = x + 2
```

Modulo 2:

``` text
x' % 2
= (x + 2) % 2
= x % 2
```

So parity is invariant.

### Step 3 --- Apply to reachability

Start:

``` text
3 -> odd
```

Target:

``` text
10 -> even
```

Invariant mismatch:

``` text
odd != even
```

Therefore impossible.

No simulation depth matters anymore.

### Generalize to modulo k

Operation:

``` text
x <- x + 6
```

Then modulo 6:

``` text
x mod 6
```

never changes.

Example:

``` text
5 -> 11 -> 17 -> 23
```

Remainders:

``` text
5 5 5 5
```

### Invariant Search Checklist

For repeated operations ask:

``` text
Does parity stay?
Does sum mod k stay?
Does gcd stay?
Does xor stay?
Does sign product stay?
Does relative order stay?
Does frequency class stay?
```

This turns:

``` text
"Which sequence of operations should I try?"
```

into:

``` text
"What is impossible to change?"
```

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

### Hints - open only after your own attempt

1. 1367B: Count parity mismatches between values and indices.
2. 1475A: Powers of two form the special invariant class.
3. 1690F: Work cycle-by-cycle and combine periods with LCM.
4. 1669C: Check parity consistency inside index-parity classes.
5. 1624C: Repeated /2 shrinks values toward assignable targets.

------------------------------------------------------------------------

# C2. Reverse Thinking

## Visual Learning Map

Forward may be a tree; backward may be a path:

```text
FORWARD from 5
             5
          /     \
        10       6
       /  \     / \
     20   11   12   7
          ...branches...

BACKWARD from 23
23 --(-1)--> 22 --(/2)--> 11 --(-1)--> 10 --(/2)--> 5
```

**Real world:** reconstruct the last move of a puzzle instead of listing every possible future move.

For forward rules `x->2x` and `x->x+1`:

```text
target t is odd
=> t cannot be 2*x
=> last move was +1
=> predecessor = t-1
```

Recognition: forward choices explode -> inspect target -> ask **what could the last move be?**


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

## Detailed Worked Example --- C2 Reverse Thinking

Forward operations:

``` text
x -> 2x
x -> x+1
```

Suppose target is:

``` text
23
```

### Step 1 --- Forward view branches

From `5`:

``` text
          5
        /   \
      10     6
     / \    / \
   20 11  12  7
   ...
```

Every state creates more choices.

### Step 2 --- Start from target

Target:

``` text
23
```

Ask:

``` text
How could the LAST move create 23?
```

Since 23 is odd:

``` text
23 cannot be 2x
```

So last step must be:

``` text
22 -> 23
```

Backward:

``` text
23
↓ -1
22
```

Now `22` is even.

Possible predecessor via doubling:

``` text
11 -> 22
```

So:

``` text
23
↓ -1
22
↓ /2
11
```

Now `11` is odd:

``` text
10 -> 11
```

Continue:

``` text
23
↓
22
↓
11
↓
10
↓
5
```

ASCII comparison:

``` text
FORWARD:
one node
  / \
 /   \
many branches

BACKWARD:
target
  |
few predecessors
  |
almost forced path
```

### Why this works conceptually

Forward operation may be many-to-many.

But target properties often constrain the last move strongly.

Typical signals:

``` text
doubling
halving
append digit
remove digit
grow string
build array from end
```

Recognition:

``` text
forward branches too much
      ↓
inspect target
      ↓
ask "what could the last move be?"
```

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

### Hints - open only after your own attempt

1. 727A: Start at b; undo append-1 or doubling.
2. 1703C: Reverse U/D or accumulate net movement mod 10.
3. 1881A: Doubling gives a tiny search depth.
4. 1624C: Shrink values by /2 instead of trying to grow targets.
5. 1791C: Peel compatible boundary characters from both ends.

------------------------------------------------------------------------

# C3. Difference Modeling / Normalization

## Visual Learning Map

When two arrays look noisy, draw **movement arrows**:

```text
A:  3       7       10
    |       |        |
   +5      +5       +5
    |       |        |
B:  8      12       15
```

Hidden state:

```text
D[i] = B[i]-A[i] = [5,5,5]
```

**Real world:** three houses were moved east by the same 5 km. One different movement would break the common translation.

Algebra:

```text
A[i] + x = B[i]
=> x = B[i]-A[i]
```

Same `x` everywhere means all differences must agree.

Normalization removes irrelevant absolute position:

```text
10 ---- 20 ---- 30
subtract 10
0  ---- 10 ---- 20
```

Recognition: current vs target -> subtract/divide/normalize -> compare relative structure.


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

## Detailed Worked Example --- C3 Difference Modeling / Normalization

Suppose:

``` text
a = [3, 7, 10]
b = [8, 12, 15]
```

Question:

``` text
Can one common value x be added to every a[i]
to obtain b[i]?
```

### Step 1 --- Write equation per position

``` text
3  + x = 8
7  + x = 12
10 + x = 15
```

Solve each:

``` text
x = 5
x = 5
x = 5
```

So all positions agree.

ASCII:

``` text
3  ----+5---->  8
7  ----+5----> 12
10 ----+5----> 15

same offset everywhere
```

Therefore possible.

### Step 2 --- Try an inconsistent target

``` text
b = [8, 11, 15]
```

Differences:

``` text
8  - 3  = 5
11 - 7  = 4
15 - 10 = 5
```

Now:

``` text
5, 4, 5
```

No single common `x` exists.

ASCII:

``` text
3  --+5--> 8
7  --+4--> 11   X different
10 --+5--> 15
```

### Step 3 --- Why subtraction helps

Absolute numbers may look unrelated:

``` text
3,7,10
8,12,15
```

But subtraction reveals the hidden state:

``` text
b[i] - a[i]
```

So:

``` text
two arrays
   ↓ subtract
one difference array
   ↓
check consistency
```

### Normalization by Reference

Suppose:

``` text
10 20 30
```

If only relative distances matter, subtract the first/minimum:

``` text
0 10 20
```

You removed irrelevant absolute offset.

### Normalize by GCD

``` text
12 18 24
gcd = 6
```

Divide:

``` text
2 3 4
```

This exposes ratio/relative structure.

### Difference Array Variant

Original:

``` text
5 8 11 15
```

Differences:

``` text
3 3 4
```

ASCII:

``` text
5 --3--> 8 --3--> 11 --4--> 15
```

For range operations, these differences often make a global change
local.

Recognition:

``` text
absolute values noisy
      ↓
compare relative movement
      ↓
difference / offset / gcd normalization
```

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

### Hints - open only after your own attempt

1. 1832C: Remove duplicates, then keep only direction changes.
2. 1772D: Each adjacent relation imposes a bound on one common x.
3. 1862B: Model a local adjacent violation and repair it locally.
4. 1833C: Subtract the minimum mentally and inspect parity.
5. 1618C: Separate position groups and test a GCD candidate.

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
