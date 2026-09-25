# 016_Modulo_Cycle_Patterns.md

# Modulo Cycle Patterns For Competitive Programming

---

# 1. Introduction

One of the MOST powerful observations in CP is:

```text
Things eventually repeat.
```

This repetition is called:

```text
Cycle Pattern
```

Modulo is the foundation of cyclic behavior.

Strong contestants constantly ask:

```text
Is there a repeating pattern?
```

because cycles can reduce:
- huge computations
- infinite simulations
- exponential brute force

into:
```text
small constant-time solutions
```

---

# 2. What Is A Modulo Cycle?

A modulo cycle means:

```text
Values repeat after some length.
```

Example:

Last digit of powers of 2:

```text
2^1 = 2
2^2 = 4
2^3 = 8
2^4 = 16 → 6
2^5 = 32 → 2
2^6 = 64 → 4
```

Pattern:

```text
2 4 8 6
2 4 8 6
```

Cycle length:

```text
4
```

---

# 3. Why Cycles Matter

Without observation:

```text
compute 2^1000000
```

Impossible directly.

With cycle:

```text
1000000 % 4
```

Easy.

Huge optimization.

---

# 4. Most Common CP Cycle Types

| Cycle Type | Examples |
|---|---|
| last digit cycles | powers |
| modulo remainder cycles | arithmetic |
| circular arrays | wraparound |
| state repetition | simulation |
| graph cycles | DFS/BFS |
| string periodicity | hashing/KMP |
| game states | DP/memoization |

---

# 5. Last Digit Cycle Example

Find last digit of:

```text
2^n
```

Observation:

```text
2 4 8 6
```

repeats.

Cycle length:
```text
4
```

Thus:

```text
n % 4
```

determines answer.

---

# Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int lastDigit(long long n) {

    vector<int> cycle = {6,2,4,8};

    return cycle[n % 4];
}
```

---

# 6. Why Modulo Creates Cycles

Modulo limits states.

Example:

```text
mod 5
```

Possible values only:

```text
0 1 2 3 4
```

After enough operations:
- values repeat
- cycles appear

This is extremely important.

---

# 7. Clock Example

Clock behaves modulo 12.

Example:

```text
11 + 3
```

Normal:

```text
14
```

Clock:

```text
2
```

because:

```text
14 % 12 = 2
```

---

# 8. Circular Array

Very common CP pattern.

Example:

```text
index after i+1
```

in circular array:

```cpp
(i + 1) % n
```

---

# Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int nextIndex(int i, int n) {

    return (i + 1) % n;
}
```

---

# 9. Simulation Optimization

Huge insight:

If states repeat:

```text
future also repeats
```

This allows:
- cycle detection
- skipping computations

Very important in:
- graph problems
- game simulation
- DP

---

# 10. Fibonacci Modulo Cycle

Interesting observation:

```text
Fibonacci % m
```

also repeats.

Called:

```text
Pisano Period
```

Important advanced concept.

---

# 11. Modulo State Compression

Modulo compresses infinite numbers into:

```text
finite remainder states
```

Example:

```text
mod 3
```

Only:
```text
0 1 2
```

possible.

This compression creates cycles automatically.

---

# 12. Prefix Sum Modulo Cycles

Important observation:

If:

```text
prefix[i] % k
==
prefix[j] % k
```

then:
```text
subarray divisible by k
```

Cycle/repetition observation.

Very important pattern.

---

# 13. Binary Exponentiation And Cycles

Modulo cycles often reduce huge powers.

Example:

```text
a^(large)
```

Observation:
- powers cycle
- modulo periodicity

This is used heavily in:
- modular arithmetic
- cryptography

---

# 14. Graph Cycles

Graphs also contain cycles.

Cycle detection important in:
- DFS
- directed graph
- undirected graph
- topological sorting

Different from modulo cycles but same repeating-state intuition.

---

# 15. CP / FAANG Problem Forms

---

# Form 1 — Last Digit Of Power

## Problem

Find last digit of:

```text
2^n
```

---

## Observation

Cycle:

```text
2 4 8 6
```

Length:
```text
4
```

---

## Step-by-Step Working

Example:

```text
n = 10

10 % 4 = 2
```

2nd position in cycle:
```text
4
```

Answer:
```text
4
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int lastDigit(long long n) {

    vector<int> cycle = {6,2,4,8};

    return cycle[n % 4];
}
```

---

# Form 2 — Circular Array

## Problem

Move in circular array.

---

## Observation

Use modulo wraparound.

---

## Step-by-Step Working

Example:

```text
n = 5
i = 4

(4+1)%5 = 0
```

Wraps around.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int nextPos(int i, int n) {

    return (i + 1) % n;
}
```

---

# Form 3 — Repeated Simulation

## Problem

Simulation repeats states.

---

## Observation

Store visited states.

If repeated:
```text
cycle detected
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool hasCycle(vector<int>& next) {

    unordered_set<int> vis;

    int node = 0;

    while (true) {

        if (vis.count(node))
            return true;

        vis.insert(node);

        node = next[node];
    }
}
```

---

# Form 4 — Prefix Modulo Repeat

## Problem

Count subarrays divisible by k.

---

## Observation

Equal prefix modulo values.

---

## Step-by-Step Working

If:

```text
prefix[i]%k
=
prefix[j]%k
```

then:
```text
subarray divisible
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int subarraysDivByK(
    vector<int>& a,
    int k
) {

    unordered_map<int,int> freq;

    freq[0] = 1;

    int prefix = 0;

    int ans = 0;

    for (int x : a) {

        prefix =
            ((prefix + x)%k + k)%k;

        ans += freq[prefix];

        freq[prefix]++;
    }

    return ans;
}
```

---

# Form 5 — Clock Arithmetic

## Problem

Find time after k hours.

---

## Observation

Modulo wraparound.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int clockTime(int start, int k) {

    return (start + k) % 12;
}
```

---

# Form 6 — Cycle In String Pattern

## Problem

Repeated string pattern.

---

## Observation

Periodicity.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool repeatedPattern(string s) {

    string t = s + s;

    return t.substr(1,
                    t.size()-2)
            .find(s)
            != string::npos;
}
```

---

# Form 7 — Binary Exponentiation

## Problem

Compute huge powers modulo.

---

## Observation

Modulo periodicity + binary decomposition.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long modPow(
    long long a,
    long long b,
    long long MOD
) {

    long long ans = 1;

    a %= MOD;

    while (b > 0) {

        if (b & 1) {

            ans =
                (ans * a) % MOD;
        }

        a =
            (a * a) % MOD;

        b >>= 1;
    }

    return ans;
}
```

---

# 16. Real World Applications

| Real System | Usage |
|---|---|
| Distributed systems | consistent hashing |
| Networking | cyclic buffers |
| Databases | hash partitioning |
| Game engines | repeating states |
| Cryptography | modular exponentiation |
| Operating systems | circular scheduling |
| Streaming systems | ring buffers |

---

# 17. Real Engineering Insight

Cycles usually mean:

```text
Finite state repetition
```

This is fundamental in:
- caching
- distributed systems
- compression
- simulation engines

---

# 18. Observation Recognition Signals

Look for:

```text
1. repeating values
2. periodicity
3. circular behavior
4. wraparound
5. huge powers
6. repeated states
7. modulo arithmetic
8. finite state space
```

---

# 19. Decision Tree

```text
Repeating pattern?
→ cycle

Circular movement?
→ modulo wraparound

Huge powers?
→ modulo periodicity

Repeated states?
→ cycle detection

Prefix modulo repeat?
→ divisible subarray

Finite state system?
→ cycles eventually appear
```

---

# 20. Common Traps

```text
1. Missing cycle observation
2. Simulating too long
3. Wrong cycle indexing
4. Off-by-one in modulo
5. Negative modulo bug
6. Infinite loops
7. Incorrect wraparound
8. Forgetting state repetition
```

---

# 21. Final Checklist

Before solving:

```text
1. Is there repeating pattern?
2. Does modulo create cycle?
3. Can simulation repeat?
4. Is state space finite?
5. Can wraparound simplify?
6. Is periodicity useful?
7. Is there cycle detection?
8. Can modulo reduce complexity?
```

---

# 22. Final Mental Shortcut

```text
Modulo Cycle Patterns
=
Finite States
→ Repetition
→ Periodicity
→ Optimization
```
