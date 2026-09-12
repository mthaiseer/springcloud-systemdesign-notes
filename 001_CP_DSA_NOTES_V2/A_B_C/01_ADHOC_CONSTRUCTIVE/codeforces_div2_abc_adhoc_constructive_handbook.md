# Codeforces Div2 A/B/C — Ad-hoc + Constructive Pattern Recognition Handbook

**Primary goal:** train yourself to classify a Codeforces Div2 A/B/C problem in roughly **60 seconds**.

**Training philosophy**

```text
Statement
   ↓
Strip the story
   ↓
Identify output + allowed operations
   ↓
Look for invariant / extreme / parity / frequency
   ↓
Try smallest possible construction
   ↓
Prove impossible cases
   ↓
Code
```

> **Important:** “A / B / C” below means the *skill level and contest-thinking bucket* you should train.  
> Codeforces difficulty changes from round to round, so a pattern is not permanently tied to one letter.

---

# Table of Contents

1. The 60-second scanner
2. Ad-hoc vs Constructive
3. Div2 A patterns
   - A1. Parity and modulo
   - A2. Counting / frequency / presence
   - A3. Min-max / extreme observation
   - A4. Small casework
4. Div2 B patterns
   - B1. Simplest-valid construction
   - B2. Mostly-same + correction
   - B3. Alternating / cyclic / blocks
   - B4. Sort to reveal structure
5. Div2 C patterns
   - C1. Invariants under operations
   - C2. Reverse thinking
   - C3. Difference / normalization
   - C4. Greedy constructive + local repair
6. Construction templates
7. Impossibility proofs
8. Problem-decoding worksheet
9. 60-second recognition drill
10. 8-week practice roadmap
11. Contest checklist

---

# 1. The 60-Second Scanner

When you open a problem, **do not code immediately**.

## 0–10 seconds — identify the output

Ask:

```text
What exactly do I need?

YES / NO?
minimum operations?
maximum score?
a permutation?
any valid array?
number of ways?
```

If the statement says:

```text
"print any..."
"construct..."
"find a permutation..."
```

immediately activate **constructive mode**.

---

## 10–20 seconds — identify operations

Translate the story into verbs.

```text
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

```text
Story:
Alice presses a button and changes one number...

Mathematical version:
Choose i:
a[i] ← -a[i]
```

The mathematical operation is what matters.

---

## 20–30 seconds — ask what cannot change

Check:

```text
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

---

## 30–40 seconds — test tiny cases

Always try:

```text
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

---

## 40–50 seconds — try standard constructions

Before inventing anything complicated, test:

```text
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

---

## 50–60 seconds — classify

```text
ONE direct observation
    → likely A

observation + casework / simple construction
    → likely B

2–3 observations chained together
or invariant + data structure / greedy
    → likely C
```

---

# 2. Ad-hoc vs Constructive

## Ad-hoc

An ad-hoc problem normally says:

> There is no big named algorithm. Notice the right property.

Examples:

```text
Only parity matters.
Only min and max matter.
Count of negatives is enough.
The answer depends only on n mod 3.
Sort and inspect adjacent gaps.
```

### Mental model

```text
large statement
      ↓
remove irrelevant information
      ↓
one small observation
```

---

## Constructive

A constructive problem asks you to **build one valid answer**.

Instead of searching every possibility:

```text
candidate 1
candidate 2
candidate 3
...
```

you design a structure where the constraints are automatically satisfied.

Example:

```text
Need adjacent values different.

Construction:

1 2 1 2 1 2
```

Every adjacent pair differs **by design**.

---

## Why they belong together

A typical Div2 B/C constructive solution is:

```text
AD-HOC OBSERVATION
       ↓
necessary condition
       ↓
possible / impossible
       ↓
CONSTRUCTIVE TEMPLATE
       ↓
print answer
```

Example:

```text
Need n using numbers 1..k except x.

Observation:
If 1 is available, construction is trivial.

If 1 is forbidden:
    parity of n becomes important.

Construction:
even n → all 2
odd n  → one 3 + rest 2
```

---

# 3. Div2 A Patterns

---

# A1. Parity and Modulo

## 60-second signal

Look for:

```text
odd / even
pairing
split into groups
alternating turns
sum parity
number divisible by k
operation adds/subtracts fixed quantity
```

Immediately test:

```text
n % 2
sum % 2
countOdd % 2
n % k
```

---

## Core idea

Parity compresses infinitely many numbers into only two classes:

```text
EVEN = 0 mod 2
ODD  = 1 mod 2
```

Rules:

```text
E + E = E
O + O = E
E + O = O
O + E = O
```

---

## Dry run — equal parity group sums

Suppose total array sum is:

```text
S = leftSum + rightSum
```

If `leftSum` and `rightSum` have the same parity:

```text
even + even = even
odd  + odd  = even
```

Therefore:

```text
S must be even
```

ASCII reasoning:

```text
group A ─────┐
             ├── total
group B ─────┘

same parity:

E + E → E
O + O → E

Therefore total must be EVEN.
```

The huge coloring/search problem becomes:

```cpp
cout << (sum % 2 == 0 ? "YES\n" : "NO\n");
```

---

## Modulo generalization

Instead of tracking the full number:

```text
n = 1,000,000,007
```

sometimes all you need is:

```text
n mod k
```

Because operations change values by multiples of `k`.

If:

```text
a → a + k
```

then:

```text
a mod k
```

never changes.

---

## Code snippet

```cpp
long long sum = 0;

for (int x : a)
    sum += x;

if (sum % 2 == 0)
    cout << "YES\n";
else
    cout << "NO\n";
```

General:

```cpp
if (value % k == requiredRemainder) {
    // possible
}
```

---

## Recognition sentence

> “If the operation/grouping only changes values by even amounts or multiples of `k`, check parity/modulo before simulating.”

---

## Practice — parity/modulo

1. [CF 959A — Mahmoud and Ehab and the even-odd game](https://codeforces.com/problemset/problem/959/A)
2. [CF 1857A — Array Coloring](https://codeforces.com/problemset/problem/1857/A)
3. [CF 1296A — Array with Odd Sum](https://codeforces.com/problemset/problem/1296/A)
4. [CF 1899A — Game with Integers](https://codeforces.com/problemset/problem/1899/A)
5. [CF 1475A — Odd Divisor](https://codeforces.com/problemset/problem/1475/A)

---

# A2. Counting / Frequency / Presence

## 60-second signal

The exact order appears irrelevant.

Examples:

```text
How many +1 / -1?
How many zeroes?
Does k occur?
How many letters of each type?
How many odd values?
```

Try replacing the entire array with counts.

---

## Compression idea

Instead of:

```text
[-1, +1, -1, -1, +1, +1]
```

store:

```text
negative = 3
positive = 3
```

ASCII:

```text
RAW ARRAY
- + - - + +

      ↓ compress

STATE
neg = 3
pos = 3
```

You have reduced `n` values to 2 integers.

---

## Dry run

Suppose operation flips one `-1` to `+1`.

Before:

```text
neg = 5
pos = 1
```

After one flip:

```text
neg = 4
pos = 2
```

After two:

```text
neg = 3
pos = 3
```

ASCII:

```text
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

---

## Presence pattern

Sometimes the whole problem reduces to:

```cpp
bool found = false;

for (int x : a)
    if (x == k)
        found = true;

cout << (found ? "YES\n" : "NO\n");
```

Do not invent extra machinery when the statement only needs existence.

---

## Frequency code template

```cpp
map<int,int> freq;

for (int x : a)
    freq[x]++;
```

For small alphabet:

```cpp
int freq[26] = {};

for (char c : s)
    freq[c - 'a']++;
```

---

## Recognition sentence

> “If positions do not matter, compress the input into counts or frequencies.”

---

## Practice — counting/frequency/presence

1. [CF 1220A — Cards](https://codeforces.com/problemset/problem/1220/A)
2. [CF 1878A — How Much Does Daytona Cost?](https://codeforces.com/problemset/problem/1878/A)
3. [CF 1877A — Goals of Victory](https://codeforces.com/problemset/problem/1877/A)
4. [CF 1791A — Codeforces Checking](https://codeforces.com/problemset/problem/1791/A)
5. [CF 1703A — YES or YES?](https://codeforces.com/problemset/problem/1703/A)

---

# A3. Min / Max / Extreme Observation

## 60-second signal

Look for:

```text
largest
smallest
range
minimum possible
maximum possible
closest to an end
all values relative to something
```

Test whether only these matter:

```text
min
max
max - min
first
last
leftmost
rightmost
```

---

## Dry run

Array:

```text
8 2 10 4 6
```

Suppose the question depends on how spread out values are.

You may not need all pairs.

```text
min = 2
max = 10

largest possible distance = 10 - 2 = 8
```

ASCII:

```text
2 ---- 4 ---- 6 ---- 8 ---- 10
^                            ^
min                         max

Everything lies inside [2,10].
```

---

## Why extremes work

For any `x,y`:

```text
min <= x,y <= max
```

so:

```text
|x-y| <= max-min
```

Therefore, if you seek maximum pair difference, the extreme pair is sufficient.

---

## Code snippet

```cpp
int mn = *min_element(a.begin(), a.end());
int mx = *max_element(a.begin(), a.end());

cout << mx - mn << '\n';
```

---

## First / last violation

A related pattern:

```text
valid valid valid BAD ... BAD valid
                  ^       ^
               first     last
```

Often only the interval from first bad to last bad matters.

---

## Recognition sentence

> “Before checking all pairs, ask whether the answer is forced by min/max or the first/last exceptional position.”

---

## Practice — extreme observation

1. [CF 1838A — Blackboard List](https://codeforces.com/problemset/problem/1838/A)
2. [CF 1805A — We Need the Zero](https://codeforces.com/problemset/problem/1805/A)
3. [CF 1848A — Vika and Her Friends](https://codeforces.com/problemset/problem/1848/A)
4. [CF 1858A — Buttons](https://codeforces.com/problemset/problem/1858/A)
5. [CF 1896A — Jagged Swaps](https://codeforces.com/problemset/problem/1896/A)

---

# A4. Small Casework + Direct Simulation

## 60-second signal

Constraints or operations behave differently when:

```text
n odd / even
x == 1 / x != 1
k == 1 / k > 1
first element special
last element special
one exceptional remainder
```

Do not fear casework.

Good casework is often the intended solution.

---

## How to make casework safely

Start with the property that fundamentally changes behavior.

Example:

```text
if x != 1:
    ...

else:
    ...
```

Then only split further when needed:

```text
x == 1
   |
   +-- n even
   |
   +-- n odd
```

Avoid random nested conditions.

---

## Dry run — repeated string doubling

Suppose an operation doubles string `x`:

```text
x ← x + x
```

Example:

```text
x = "ab"
target substring = "baba"
```

Simulation:

```text
0: ab
1: abab
2: abababab
```

ASCII growth:

```text
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

For small bounded operations, simulation is simpler than forcing a formula.

---

## Code pattern

```cpp
int ans = 0;

while (!condition() && ans <= LIMIT) {
    performOperation();
    ans++;
}
```

---

## Simulation → formula upgrade

If state changes regularly:

```text
pos += 1
neg -= 1
```

then derive instead of looping:

```text
neg - x <= pos + x
neg - pos <= 2x
x >= ceil((neg-pos)/2)
```

Integer ceiling:

```cpp
int x = max(0, (neg - pos + 1) / 2);
```

---

## Recognition sentence

> “If only 2–4 meaningful branches exist, enumerate them explicitly; if simulation changes state predictably, derive the formula.”

---

## Practice — casework/simulation

1. [CF 1881A — Don't Try to Count](https://codeforces.com/problemset/problem/1881/A)
2. [CF 1900A — Cover in Water](https://codeforces.com/problemset/problem/1900/A)
3. [CF 1862B — Sequence Game](https://codeforces.com/problemset/problem/1862/B)
4. [CF 1858A — Buttons](https://codeforces.com/problemset/problem/1858/A)
5. [CF 1845A — Forbidden Integer](https://codeforces.com/problemset/problem/1845/A)

---

# 4. Div2 B Patterns

Div2 B often looks like:

```text
A-pattern
+
one extra observation
```

Typical structure:

```text
count/parity/sort
      ↓
derive condition
      ↓
construct or greedily act
```

---

# B1. Simplest Valid Construction

## 60-second signal

The statement says:

```text
print any valid...
construct an array...
find any permutation...
```

First thought:

> What is the dumbest possible structure that satisfies everything?

Try:

```text
all same
identity permutation
reverse permutation
pair swap
constant difference
```

---

## Example — build sum `n`

Allowed:

```text
1 ... k
except x
```

Need numbers summing to `n`.

The simplest possible building block is `1`.

If `1` is allowed:

```text
n = 7

1 + 1 + 1 + 1 + 1 + 1 + 1
```

No need to optimize number of terms.

ASCII:

```text
target 7
│
├─1
├─1
├─1
├─1
├─1
├─1
└─1
```

This is a key constructive principle:

> If the problem asks for **any** solution, do not optimize something it did not ask you to optimize.

---

## Code

```cpp
if (x != 1) {
    cout << "YES\n";
    cout << n << '\n';
    for (int i = 0; i < n; ++i)
        cout << 1 << ' ';
    cout << '\n';
}
```

---

## Construction search order

Use this mental order:

```text
1. all minimum value
2. all maximum value
3. increasing sequence
4. decreasing sequence
5. alternating two values
6. pair swap
7. mostly same + one correction
8. blocks
```

---

## Recognition sentence

> “Any-answer constructive problem → try the simplest repetitive structure before searching.”

---

## Practice — simplest construction

1. [CF 1845A — Forbidden Integer](https://codeforces.com/problemset/problem/1845/A)
2. [CF 1741B — Funny Permutation](https://codeforces.com/problemset/problem/1741/B)
3. [CF 1831A — Twin Permutations](https://codeforces.com/problemset/problem/1831/A)
4. [CF 1794B — Not Dividing](https://codeforces.com/problemset/problem/1794/B)
5. [CF 1814A — Coins](https://codeforces.com/problemset/problem/1814/A)

---

# B2. Mostly Same + One Correction

This is one of the highest-value constructive patterns.

## 60-second signal

You can satisfy most of the target using one building block, but:

```text
parity
remainder
forbidden value
final sum
```

does not fit exactly.

Think:

```text
BASE CONSTRUCTION
      +
SMALL CORRECTION
```

---

## Dry run

Need:

```text
n = 11
```

`1` is forbidden, but `2` and `3` are available.

Using only 2:

```text
2 + 2 + 2 + 2 + 2 = 10
```

We cannot reach odd 11 with only even values.

Correction:

```text
3 + 2 + 2 + 2 + 2 = 11
```

ASCII:

```text
all 2s
2 2 2 2 2
─────────
   10

replace one parity block

3 2 2 2 2
─────────
   11
```

---

## Algebra

For even `n`:

```text
n = 2q
```

For odd `n >= 3`:

```text
n = 3 + 2q
```

So:

```text
if n is even:
    use n/2 copies of 2

if n is odd:
    use one 3
    use (n-3)/2 copies of 2
```

---

## Generic remainder correction

Need sum `S`, base value `b`.

```text
S = qb + r
```

Try to handle the small remainder `r`.

This is why `%` and constructive algorithms frequently appear together.

---

## Code skeleton

```cpp
if (n % 2 == 0) {
    for (int i = 0; i < n / 2; ++i)
        cout << 2 << ' ';
}
else {
    cout << 3 << ' ';
    for (int i = 0; i < (n - 3) / 2; ++i)
        cout << 2 << ' ';
}
```

---

## Recognition sentence

> “If one repeated value almost works, inspect only the leftover parity/remainder and patch it with one small element.”

---

## Practice — base + correction

1. [CF 1845A — Forbidden Integer](https://codeforces.com/problemset/problem/1845/A)
2. [CF 1814A — Coins](https://codeforces.com/problemset/problem/1814/A)
3. [CF 1794B — Not Dividing](https://codeforces.com/problemset/problem/1794/B)
4. [CF 1367B — Even Array](https://codeforces.com/problemset/problem/1367/B)
5. [CF 1352B — Same Parity Summands](https://codeforces.com/problemset/problem/1352/B)

---

# B3. Alternating / Cyclic / Block Construction

## 60-second signal

The constraints mention:

```text
adjacent elements
repeat every k
different neighbors
colors
balanced placement
permutation with local constraints
```

Try a periodic structure.

---

## Alternating

```text
A B A B A B
```

Index view:

```text
i:  0 1 2 3 4 5
    A B A B A B
```

Formula:

```cpp
a[i] = (i % 2 == 0 ? A : B);
```

---

## Cyclic

For `k = 3`:

```text
1 2 3 1 2 3 1 2 3
```

Formula:

```cpp
a[i] = i % k;
```

ASCII:

```text
remainder class:

i      0 1 2 3 4 5 6 7
i%3    0 1 2 0 1 2 0 1
       └─────┘
        repeat
```

---

## Pair-swap permutation

Identity:

```text
1 2 3 4 5 6
```

Pair swapped:

```text
2 1 4 3 6 5
```

Why useful?

```text
p[i] != i
```

becomes automatically true for every paired position.

ASCII:

```text
1 ↔ 2
3 ↔ 4
5 ↔ 6
```

---

## Blocks

Sometimes constraints should be satisfied inside chunks.

```text
1 2 3 | 4 5 6 | 7 8 9

reverse each block

3 2 1 | 6 5 4 | 9 8 7
```

---

## Code snippets

Alternating:

```cpp
for (int i = 0; i < n; ++i)
    cout << (i % 2 ? 2 : 1) << ' ';
```

Pair swap:

```cpp
for (int i = 1; i <= n; i += 2)
    cout << i + 1 << ' ' << i << ' ';
```

Cyclic:

```cpp
for (int i = 0; i < n; ++i)
    cout << (i % k) + 1 << ' ';
```

---

## Recognition sentence

> “Local/adjacent constraints often become globally easy if I choose a periodic pattern.”

---

## Practice — alternating/cyclic/permutation

1. [CF 1335B — Construct the String](https://codeforces.com/problemset/problem/1335/B)
2. [CF 1741B — Funny Permutation](https://codeforces.com/problemset/problem/1741/B)
3. [CF 1831A — Twin Permutations](https://codeforces.com/problemset/problem/1831/A)
4. [CF 1822D — Super-Permutation](https://codeforces.com/problemset/problem/1822/D)
5. [CF 1353C — K-th Not Divisible by n](https://codeforces.com/problemset/problem/1353/C)

---

# B4. Sort to Reveal Structure

## 60-second signal

Input order is arbitrary and the condition involves:

```text
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

```cpp
sort(a.begin(), a.end());
```

---

## Dry run

Original:

```text
8 1 7 3 2
```

Hard to see structure.

Sorted:

```text
1 2 3 7 8
```

Now gaps:

```text
1 → 2 : 1
2 → 3 : 1
3 → 7 : 4   ← important gap
7 → 8 : 1
```

ASCII:

```text
1--2--3----------7--8
      ^
      large separation
```

---

## Pair-extremes pattern

Sorted:

```text
1 2 3 8 9 10
```

Pair:

```text
1  ↔ 10
2  ↔ 9
3  ↔ 8
```

Useful when balancing sums/differences or maximizing separation.

---

## Adjacent sufficiency

After sorting, if you need to detect whether **any two values are too close**, you often only need adjacent pairs.

Why?

If:

```text
a[i] <= a[j] <= a[k]
```

then the closest conflict will appear among neighbors in sorted order.

---

## Code

```cpp
sort(a.begin(), a.end());

for (int i = 1; i < n; ++i) {
    int gap = a[i] - a[i - 1];
    // inspect gap
}
```

---

## Recognition sentence

> “If original positions are irrelevant, sort first; many global pair conditions turn into adjacent checks.”

---

## Practice — sorting observation

1. [CF 1798A — Showstopper](https://codeforces.com/problemset/problem/1798/A)
2. [CF 1833B — Restore the Weather](https://codeforces.com/problemset/problem/1833/B)
3. [CF 1793C — Dora and Search](https://codeforces.com/problemset/problem/1793/C)
4. [CF 1353B — Two Arrays And Swaps](https://codeforces.com/problemset/problem/1353/B)
5. [CF 1538C — Challenging Cliffs](https://codeforces.com/problemset/problem/1538/C)

---

# 5. Div2 C Patterns

A common C is not one magical algorithm.

It may be:

```text
observation #1
      ↓
transform problem
      ↓
observation #2
      ↓
known technique
```

Example:

```text
decode scoring formula
      ↓
constant part + changing part
      ↓
maintain best candidates
      ↓
greedy / multiset / heap
```

---

# C1. Invariants Under Operations

## 60-second signal

The statement allows an operation **many times**.

Immediately ask:

> What property stays unchanged no matter how many operations I perform?

Candidates:

```text
parity
sum mod k
gcd
xor
difference
sign product
relative order
frequency class
```

---

## Dry run — adding 2

Operation:

```text
a[i] ← a[i] + 2
```

Try:

```text
3 → 5 → 7 → 9
```

Always odd.

```text
4 → 6 → 8 → 10
```

Always even.

ASCII:

```text
ODD lane:
3 → 5 → 7 → 9 → ...

EVEN lane:
4 → 6 → 8 → 10 → ...

No bridge between lanes.
```

Therefore parity is invariant.

If target requires:

```text
odd → even
```

answer is impossible.

---

## Algebra

Operation:

```text
x ← x + 2k
```

Modulo 2:

```text
(x + 2k) mod 2
= x mod 2
```

So parity cannot change.

General:

```text
x ← x + mk
```

means:

```text
x mod k
```

is unchanged.

---

## Invariant workflow

```text
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

---

## Code skeleton

```cpp
if ((start & 1) != (target & 1)) {
    cout << "NO\n";
    return;
}
```

---

## Recognition sentence

> “Repeated operation problem → search for an invariant before searching for a sequence of operations.”

---

## Practice — invariants

1. [CF 1367B — Even Array](https://codeforces.com/problemset/problem/1367/B)
2. [CF 1475A — Odd Divisor](https://codeforces.com/problemset/problem/1475/A)
3. [CF 1690F — Shifting String](https://codeforces.com/problemset/problem/1690/F)
4. [CF 1669C — Odd/Even Increments](https://codeforces.com/problemset/problem/1669/C)
5. [CF 1624C — Division by Two and Permutation](https://codeforces.com/problemset/problem/1624/C)

---

# C2. Reverse Thinking

## 60-second signal

Forward process has many choices.

Example:

```text
from x you may:
x → 2x
x → x+1
x → x-1
...
```

The branching factor grows.

Ask:

> Is the previous state almost forced when I start from the target?

---

## Example

Forward:

```text
x → 2x
x → x+1
```

Suppose target is `23`.

Backward:

```text
23 odd
  ↓
probably came from 22 using +1

22 even
  ↓
could come from 11 using ×2
```

ASCII:

```text
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

---

## Why reverse can be easier

Forward:

```text
one state → many possible next states
```

Reverse:

```text
target → often one sensible predecessor
```

This converts search into greedy.

---

## Another use: construct from right to left

Constraint:

```text
a[i] depends on a[i+1]
```

Then build:

```text
a[n]
a[n-1]
...
a[1]
```

not left-to-right.

---

## Code skeleton

```cpp
while (target > start) {
    if (target % 2 == 0) {
        target /= 2;
    } else {
        target--;
    }
}
```

The exact rules depend on the problem, but the direction shift is the pattern.

---

## Recognition sentence

> “If forward choices branch heavily, start from the required final state and inspect its possible predecessors.”

---

## Practice — reverse thinking

1. [CF 727A — Transformation: from A to B](https://codeforces.com/problemset/problem/727/A)
2. [CF 1703C — Cypher](https://codeforces.com/problemset/problem/1703/C)
3. [CF 1881A — Don't Try to Count](https://codeforces.com/problemset/problem/1881/A)
4. [CF 1624C — Division by Two and Permutation](https://codeforces.com/problemset/problem/1624/C)
5. [CF 1791C — Prepend and Append](https://codeforces.com/problemset/problem/1791/C)

---

# C3. Difference Modeling / Normalization

## 60-second signal

You are comparing:

```text
two arrays
two states
current vs target
relative values
equalization
```

Try replacing absolute values with:

```text
difference
ratio
offset
normalized value
```

---

## Example — common offset

Suppose operation adds the same `x`:

```text
a[i] + x = b[i]
```

Then:

```text
x = b[i] - a[i]
```

For every relevant position, the same `x` must work.

Example:

```text
a = [3, 7, 10]
b = [8,12,15]
```

Differences:

```text
5,5,5
```

ASCII:

```text
3  ──+5──>  8
7  ──+5──> 12
10 ──+5──> 15

same transformation
```

But:

```text
b = [8,11,15]

differences:
5,4,5

inconsistent
```

---

## Normalize by subtracting minimum

```text
10 20 30
```

If translation does not matter:

```text
subtract 10

0 10 20
```

You removed irrelevant absolute position.

---

## Normalize by gcd

```text
12 18 24
```

```text
gcd = 6
```

Normalize:

```text
2 3 4
```

Sometimes reachability depends only on this relative structure.

---

## Difference array

Original:

```text
a = [5,8,11,15]
```

Difference:

```text
d = [3,3,4]
```

ASCII:

```text
5 --3--> 8 --3--> 11 --4--> 15
```

If operations affect ranges, differences may make the operation local.

---

## Code

```cpp
vector<long long> diff;

for (int i = 1; i < n; ++i)
    diff.push_back(a[i] - a[i - 1]);
```

Two arrays:

```cpp
long long need = b[0] - a[0];

for (int i = 1; i < n; ++i) {
    if (b[i] - a[i] != need) {
        // inconsistent
    }
}
```

---

## Recognition sentence

> “If absolute values look noisy, subtract one state from the other or normalize until only the relative structure remains.”

---

## Practice — difference/normalization

1. [CF 1832C — Contrast Value](https://codeforces.com/problemset/problem/1832/C)
2. [CF 1772D — Absolute Sorting](https://codeforces.com/problemset/problem/1772/D)
3. [CF 1862B — Sequence Game](https://codeforces.com/problemset/problem/1862/B)
4. [CF 1833C — Vlad Building Beautiful Array](https://codeforces.com/problemset/problem/1833/C)
5. [CF 1618C — Paint the Array](https://codeforces.com/problemset/problem/1618/C)

---

# C4. Greedy Constructive + Local Repair

## 60-second signal

You need to construct/process left-to-right, and once a prefix is valid you would like never to revisit it.

Think:

```text
find first violation
     ↓
make smallest safe correction
     ↓
continue
```

---

## Example

Suppose required:

```text
a[i] should not be divisible by a[i-1]
```

You scan:

```text
2 4 5 10 ...
```

At each bad value, apply the smallest permitted correction rather than rebuilding the entire array.

ASCII:

```text
valid prefix
[ ✓ ✓ ✓ ✓ ] [BAD] ? ? ?
              ^
          repair here

then continue

[ ✓ ✓ ✓ ✓ ✓ ] ? ? ?
```

---

## Greedy principle

A local repair is promising when:

1. the prefix already satisfies all constraints;
2. changing the current element cannot invalidate the prefix;
3. a minimal change leaves maximum flexibility for the suffix.

---

## Pairwise comparator derivation

When unsure what order is greedy-best, compare two objects `A` and `B`.

```text
Order 1: A then B
Order 2: B then A
```

Compute objective for both.

If:

```text
cost(A,B) <= cost(B,A)
```

derive the inequality.

ASCII:

```text
DON'T KNOW SORT ORDER
        ↓
take only A and B
        ↓
objective(A,B)
objective(B,A)
        ↓
compare
        ↓
cancel common terms
        ↓
comparator
```

This is a major path from ad-hoc reasoning into stronger Div2 C greedy.

---

## Code skeleton — local repair

```cpp
for (int i = 1; i < n; ++i) {
    if (bad(a[i - 1], a[i])) {
        a[i] = smallestSafeValue(a[i - 1], a[i]);
    }
}
```

---

## Recognition sentence

> “If a prefix can be finalized forever, repair the first violation with the smallest safe change.”

---

## Practice — greedy constructive/local repair

1. [CF 1794B — Not Dividing](https://codeforces.com/problemset/problem/1794/B)
2. [CF 1833B — Restore the Weather](https://codeforces.com/problemset/problem/1833/B)
3. [CF 1624C — Division by Two and Permutation](https://codeforces.com/problemset/problem/1624/C)
4. [CF 1772D — Absolute Sorting](https://codeforces.com/problemset/problem/1772/D)
5. [CF 1798A — Showstopper](https://codeforces.com/problemset/problem/1798/A)

---

# 6. Construction Templates to Memorize

Do not memorize solutions.

Memorize **shapes**.

---

## Template 1 — all same

```text
1 1 1 1 1 1
```

Use when one safe value alone satisfies the target.

Trigger:

```text
unlimited supply
any valid answer
sum construction
```

---

## Template 2 — mostly same + one correction

```text
2 2 2 2 3
```

Trigger:

```text
parity mismatch
remainder mismatch
```

---

## Template 3 — alternating

```text
1 2 1 2 1 2
```

Trigger:

```text
adjacent different
two classes
balanced layout
```

---

## Template 4 — cycle

```text
1 2 3 1 2 3
```

Trigger:

```text
period k
colors
modulo classes
```

---

## Template 5 — reverse

```text
n n-1 n-2 ... 1
```

Trigger:

```text
identity fails
need large-small inversion
```

---

## Template 6 — pair swap

```text
2 1 4 3 6 5
```

Trigger:

```text
avoid fixed points
local permutation property
```

---

## Template 7 — rotate

```text
2 3 4 5 1
```

Trigger:

```text
avoid p[i] = i
cyclic relation
```

---

## Template 8 — zig-zag extremes

Sorted numbers:

```text
1 2 3 4 5 6
```

Construct:

```text
1 6 2 5 3 4
```

Trigger:

```text
large adjacent difference
balance extremes
```

---

## Template 9 — blocks

```text
1 2 3 | 4 5 6 | 7 8 9
```

Transform:

```text
3 2 1 | 6 5 4 | 9 8 7
```

Trigger:

```text
constraint local to windows/chunks
```

---

# 7. Standard Impossibility Proofs

A constructive problem has two jobs:

```text
1. Detect impossible cases.
2. Construct all possible cases.
```

Learn these impossibility patterns.

---

## 7.1 Parity contradiction

Need odd sum but all usable numbers are even:

```text
even + even + ... + even = even
```

Impossible.

---

## 7.2 Modulo contradiction

Every usable piece:

```text
≡ 0 mod 3
```

Target:

```text
≡ 1 mod 3
```

Impossible.

---

## 7.3 Frequency dominance

Need no equal adjacent elements.

Suppose:

```text
A A A A A B C
```

Five `A`, only two non-A elements.

Slots:

```text
_ B _ C _
```

Only 3 safe slots for A if no two A may touch.

Need 5.

Impossible.

General condition for separating a dominant value:

```text
maxFreq <= others + 1
```

---

## 7.4 Invariant mismatch

Start parity:

```text
odd
```

Every operation preserves parity.

Target:

```text
even
```

Impossible.

---

## 7.5 Range contradiction

Allowed pieces are at least `L`.

Using `m` pieces:

```text
sum >= mL
```

If the target is smaller, impossible.

Likewise with upper bounds.

---

# 8. How to Decode a Long CF Statement

Use this worksheet.

## Step 1 — remove story nouns

Example:

```text
"Hero defeats monsters and receives happiness..."
```

Replace with:

```text
choose index i
gain a[i]
pay cost based on i
```

---

## Step 2 — define mathematical state

```text
array a
current index
selected set
score
```

---

## Step 3 — write operation in one line

Bad:

```text
I can perform some operation described in paragraph 4...
```

Good:

```text
choose i:
score += a[i]
```

or:

```text
a[i] ← a[i] + x
```

---

## Step 4 — write objective

```text
maximize:
Σ reward - Σ penalty

minimize:
number of operations

construct:
p satisfying conditions C1,C2,C3
```

---

## Step 5 — split constant and variable terms

If:

```text
score = a[i] + a[j] - k*i
```

ask which part depends on the current choice.

This frequently exposes:

```text
prefix maximum
top k candidates
greedy
```

---

## Step 6 — test tiny cases manually

Use:

```text
n = 1
n = 2
n = 3
```

Write actual state transitions.

Do not only stare at samples.

---

## Step 7 — classify the signal

```text
odd/even          → parity
many operations   → invariant
any construction  → simplest template
arbitrary order   → sorting
current→target    → differences
branching process → reverse
adjacency         → alternating/local repair
```

---

# 9. The 60-Second Pattern Recognition Drill

Do this without coding.

Take 10 problems.

For each problem allow exactly **60 seconds**.

Write only:

```text
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

---

## Example card

```text
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

---

# 10. Div2 A/B/C Recognition Map

```text
                        CF PROBLEM
                            |
            +---------------+---------------+
            |                               |
         FIND VALUE                    CONSTRUCT ANSWER
            |                               |
     +------+------+                  +-----+------+
     |             |                  |            |
  operation?    unordered?        local rules?   sum/range?
     |             |                  |            |
 invariant       sort/count       alternating    repeated base
 parity/mod       min/max          cycle/blocks   + correction
 reverse          frequency        permutation    parity/mod
```

---

# 11. Pattern Ladder by Contest Level

## Div2 A target

Master until near-automatic:

```text
parity
modulo
counting
presence
min/max
simple casework
small simulation
direct formula
basic string operations
```

Expected mental reaction:

```text
"I have seen the shape."
```

---

## Div2 B target

Add:

```text
sort + observation
frequency reasoning
base construction
parity correction
alternating/cyclic construction
permutation templates
simple invariant
reverse thinking
difference modeling
```

Expected reaction:

```text
"I see 2–3 possible patterns; let me kill them with tiny cases."
```

---

## Div2 C target

Add:

```text
multiple observations
strong invariant
normalization
greedy proof
local repair
prefix/suffix
two pointers
binary search
number theory
data structure after mathematical reduction
```

Expected reaction:

```text
"The story is long, but I can reduce it to a mathematical state."
```

---

# 12. Pattern Priority for Fast A/B/C Recognition

Train in this order:

```text
LEVEL 1
Parity
Counting
Casework
Min/max
    ↓

LEVEL 2
Sort observation
Simulation → formula
Base construction
Correction construction
    ↓

LEVEL 3
Alternating/cyclic
Permutation templates
Difference modeling
Invariant
    ↓

LEVEL 4
Reverse thinking
Normalization
Local greedy repair
Greedy proof
    ↓

LEVEL 5
Combine with:
prefix sums
two pointers
binary search
gcd/lcm
multiset / heap
```

---

# 13. Recommended 8-Week Training Plan

## Week 1 — Ad-hoc foundation

```text
Parity/modulo       10 problems
Counting/frequency  10
Min/max             10
Casework            10
```

Goal:

```text
40 easy observations
```

Do not spend more than 20–25 minutes per problem during first pass.

---

## Week 2 — Formula recognition

```text
Simulation → formula
difference
counts
remainders
```

For every simulation ask:

```text
What changes per operation?
Can I write state after x operations?
```

---

## Week 3 — Constructive basics

```text
all same
base + correction
alternating
cyclic
```

After every solution write:

```text
Why did this construction make the condition automatic?
```

---

## Week 4 — Permutations

Practice:

```text
identity
reverse
pair swap
rotation
zig-zag
blocks
```

Do not memorize problem-specific arrays.

Memorize why each *shape* is useful.

---

## Week 5 — Invariants + reverse

For every operation problem, fill:

```text
changes:
doesn't change:
monotonic:
reversible:
```

---

## Week 6 — Greedy constructive

Practice:

```text
sort first
pair extremes
first violation
minimal repair
smallest/largest choice
```

Attempt short proofs.

---

## Week 7 — Mixed 1000–1400

Every day:

```text
2 A
3 B
2 C/easy-C
```

Before coding, record a 60-second classification.

---

## Week 8 — Virtual Div2

Run 2–3 virtual contests.

Target:

```text
A classification: < 60 sec
A solve:           5–12 min

B classification: 1–3 min
B solve:           15–30 min

C:
decode quickly and identify
at least the first useful observation
```

---

# 14. Your Daily Problem Note Template

Use this after each useful problem.

```text
# Problem

Link:
Rating:

## 60-second signals

-

## Pattern

Ad-hoc / Constructive:
Subtype:

## Mathematical model

-

## Key observation

One sentence only:

-

## Tiny cases

n=1:
n=2:
n=3:

## Invariant

-

## Construction

-

## Why it works

-

## Impossible case

-

## What fooled me

-

## Recognition rule for next time

"When I see ________, I will test ________."
```

---

# 15. Contest Decision Tree

```text
START
 |
 |-- asks "any valid answer"?
 |       |
 |       YES → CONSTRUCTIVE MODE
 |               |
 |               |-- can all values be same?
 |               |-- base + correction?
 |               |-- alternating?
 |               |-- cyclic?
 |               |-- permutation template?
 |
 |-- many repeated operations?
 |       |
 |       YES → INVARIANT / REVERSE
 |
 |-- order irrelevant?
 |       |
 |       YES → COUNT / SORT
 |
 |-- odd/even language?
 |       |
 |       YES → PARITY
 |
 |-- two states / arrays?
 |       |
 |       YES → DIFFERENCE / NORMALIZE
 |
 |-- local adjacent condition?
 |       |
 |       YES → ALTERNATE / FIRST VIOLATION
 |
 |-- maximizing/minimizing?
         |
         |-- test min/max
         |-- sort
         |-- compare two greedy orders
```

---

# 16. One-Minute Flash Cards

## Parity

```text
SIGNAL:
odd/even, pair, split, fixed ±2 changes

ASK:
What is invariant mod 2?
```

## Counting

```text
SIGNAL:
order appears irrelevant

ASK:
Can n values become only 2–5 counters?
```

## Extremes

```text
SIGNAL:
range, farthest, minimum/maximum

ASK:
Do I only need min/max/first/last?
```

## Casework

```text
SIGNAL:
one parameter disables a basic option

ASK:
What is the highest-level split?
```

## Simplest construction

```text
SIGNAL:
"print any"

ASK:
Can I repeat one safe value?
```

## Correction

```text
SIGNAL:
repeated value almost works

ASK:
What remainder/parity is left?
```

## Alternating/cyclic

```text
SIGNAL:
local adjacency / periodic constraint

ASK:
Can repetition make it automatically true?
```

## Sorting

```text
SIGNAL:
arbitrary order + comparison

ASK:
Does sorted order expose gaps or pairing?
```

## Invariant

```text
SIGNAL:
operation repeated unlimited times

ASK:
What never changes?
```

## Reverse

```text
SIGNAL:
forward has too many choices

ASK:
Is predecessor of the target constrained?
```

## Difference

```text
SIGNAL:
current versus target

ASK:
What does b[i]-a[i] tell me?
```

## Local repair

```text
SIGNAL:
valid prefix + one bad location

ASK:
Can I minimally repair here and finalize prefix?
```

---

# 17. Final 60-Second Checklist

Memorize this:

```text
OUTPUT?
OPERATION?
CONSTRAINT?

PARITY?
MOD?
COUNT?
MIN/MAX?
SORT?
DIFFERENCE?
INVARIANT?
REVERSE?

CONSTRUCTION:
same?
correction?
alternate?
cycle?
permutation?
blocks?

IMPOSSIBLE:
parity?
mod?
frequency?
range?
invariant?

Then code.
```

---

# 18. The Real Goal

Do **not** aim for:

```text
"I remember the solution to CF 1845A."
```

Aim for:

```text
"I see a forbidden building block.
Try the smallest allowed block.
If parity fails, patch the remainder."
```

Do not aim for:

```text
"I remember CF 1367B."
```

Aim for:

```text
"Positions require parity classes.
Count mismatches between the two classes."
```

That is transferable contest skill.

Your progression should become:

```text
Problem statement
      ↓ 60 sec
Pattern hypothesis
      ↓
Tiny counterexamples
      ↓
Mathematical condition
      ↓
Proof / construction
      ↓
Implementation
```

When this becomes automatic for the 12 patterns in this handbook, you will recognize a large fraction of Div2 A/B and many early C ideas far faster.

---

# Practice Index — 60 problems

## A — Parity/modulo

- https://codeforces.com/problemset/problem/959/A
- https://codeforces.com/problemset/problem/1857/A
- https://codeforces.com/problemset/problem/1296/A
- https://codeforces.com/problemset/problem/1899/A
- https://codeforces.com/problemset/problem/1475/A

## A — Counting/frequency/presence

- https://codeforces.com/problemset/problem/1220/A
- https://codeforces.com/problemset/problem/1878/A
- https://codeforces.com/problemset/problem/1877/A
- https://codeforces.com/problemset/problem/1791/A
- https://codeforces.com/problemset/problem/1703/A

## A — Min/max/extremes

- https://codeforces.com/problemset/problem/1838/A
- https://codeforces.com/problemset/problem/1805/A
- https://codeforces.com/problemset/problem/1848/A
- https://codeforces.com/problemset/problem/1858/A
- https://codeforces.com/problemset/problem/1896/A

## A — Casework/simulation

- https://codeforces.com/problemset/problem/1881/A
- https://codeforces.com/problemset/problem/1900/A
- https://codeforces.com/problemset/problem/1862/B
- https://codeforces.com/problemset/problem/1858/A
- https://codeforces.com/problemset/problem/1845/A

## B — Simplest construction

- https://codeforces.com/problemset/problem/1845/A
- https://codeforces.com/problemset/problem/1741/B
- https://codeforces.com/problemset/problem/1831/A
- https://codeforces.com/problemset/problem/1794/B
- https://codeforces.com/problemset/problem/1814/A

## B — Base + correction

- https://codeforces.com/problemset/problem/1845/A
- https://codeforces.com/problemset/problem/1814/A
- https://codeforces.com/problemset/problem/1794/B
- https://codeforces.com/problemset/problem/1367/B
- https://codeforces.com/problemset/problem/1352/B

## B — Alternating/cyclic/permutation

- https://codeforces.com/problemset/problem/1335/B
- https://codeforces.com/problemset/problem/1741/B
- https://codeforces.com/problemset/problem/1831/A
- https://codeforces.com/problemset/problem/1822/D
- https://codeforces.com/problemset/problem/1353/C

## B — Sorting observation

- https://codeforces.com/problemset/problem/1798/A
- https://codeforces.com/problemset/problem/1833/B
- https://codeforces.com/problemset/problem/1793/C
- https://codeforces.com/problemset/problem/1353/B
- https://codeforces.com/problemset/problem/1538/C

## C — Invariants

- https://codeforces.com/problemset/problem/1367/B
- https://codeforces.com/problemset/problem/1475/A
- https://codeforces.com/problemset/problem/1690/F
- https://codeforces.com/problemset/problem/1669/C
- https://codeforces.com/problemset/problem/1624/C

## C — Reverse thinking

- https://codeforces.com/problemset/problem/727/A
- https://codeforces.com/problemset/problem/1703/C
- https://codeforces.com/problemset/problem/1881/A
- https://codeforces.com/problemset/problem/1624/C
- https://codeforces.com/problemset/problem/1791/C

## C — Difference/normalization

- https://codeforces.com/problemset/problem/1832/C
- https://codeforces.com/problemset/problem/1772/D
- https://codeforces.com/problemset/problem/1862/B
- https://codeforces.com/problemset/problem/1833/C
- https://codeforces.com/problemset/problem/1618/C

## C — Greedy constructive/local repair

- https://codeforces.com/problemset/problem/1794/B
- https://codeforces.com/problemset/problem/1833/B
- https://codeforces.com/problemset/problem/1624/C
- https://codeforces.com/problemset/problem/1772/D
- https://codeforces.com/problemset/problem/1798/A

---

# Suggested next expansion

After mastering this handbook, expand the same format with these hybrid Div2 C themes:

```text
Ad-hoc + prefix sums
Ad-hoc + two pointers
Constructive + gcd
Constructive + mex
Greedy + sorting comparator
Ad-hoc + binary search
Operation decoding + multiset
Permutation + prefix/suffix constraints
```

Those are the bridge from reliable A/B solving toward consistent Div2 C solving.
