
# ULTIMATE BIT MANIPULATION MASTER HANDBOOK
## Complete DSA + Competitive Programming + FAANG Notes

---

# CLICKABLE INDEX

1. [Introduction to Bits](#1-introduction-to-bits)
2. [Binary Number System](#2-binary-number-system)
3. [Decimal to Binary Conversion](#3-decimal-to-binary-conversion)
4. [Binary to Decimal Conversion](#4-binary-to-decimal-conversion)
5. [LSB vs MSB](#5-lsb-vs-msb)
6. [Bitwise Operators](#6-bitwise-operators)
7. [Shift Operators](#7-shift-operators)
8. [Power of Two Tricks](#8-power-of-two-tricks)
9. [Check Set Unset Toggle Bits](#9-check-set-unset-toggle-bits)
10. [XOR Master Properties](#10-xor-master-properties)
11. [Bitmasking Fundamentals](#11-bitmasking-fundamentals)
12. [Subset Generation using Bitmask](#12-subset-generation-using-bitmask)
13. [Submask Enumeration](#13-submask-enumeration)
14. [Bitset STL](#14-bitset-stl)
15. [Contribution Technique](#15-contribution-technique)
16. [Cyclic Property of Bits](#16-cyclic-property-of-bits)
17. [Highest Bit to Lowest Bit Technique](#17-highest-bit-to-lowest-bit-technique)
18. [Operation Decoding / Conservation of Bits](#18-operation-decoding--conservation-of-bits)
19. [Prefix Sum on Bits](#19-prefix-sum-on-bits)
20. [Classic Interview Problems](#20-classic-interview-problems)
21. [Bitmask DP Introduction](#21-bitmask-dp-introduction)
22. [SOS DP Introduction](#22-sos-dp-introduction)
23. [XOR Basis Introduction](#23-xor-basis-introduction)
24. [Trie + XOR Problems](#24-trie--xor-problems)
25. [Competitive Programming Templates](#25-competitive-programming-templates)
26. [Mental Maps](#26-mental-maps)

---
# 1. Introduction to Bits


Bits are one of the most important topics in:
- Competitive Programming
- FAANG interviews
- System programming
- Embedded programming
- Operating systems
- Cryptography

Everything inside computers becomes:

```text
0 or 1
```

Hence understanding bits gives massive advantage in:
- Optimization
- Mathematical thinking
- State compression
- Greedy construction
- XOR algebra
- DP optimization

This handbook is created from your uploaded notes and expanded into:
- Brute force thinking
- Optimal thinking
- CP style explanation
- FAANG style patterns
- Dry runs
- Column-wise LSB to MSB visual tables
- Step-by-step transitions
- Mental models
- Reusable templates



---


# 2. Detailed Bit Manipulation Topic 2


## Core Idea

This section explains advanced bit manipulation concepts in a detailed CP + FAANG style.

### Why This Topic Matters

This pattern appears heavily in:
- LeetCode Medium/Hard
- Codeforces
- AtCoder
- Google interviews
- Meta interviews
- Trading systems
- Performance engineering

---

## Mental Model

Bits should always be visualized column-wise.

Example:

| Bit Position | 5 | 4 | 3 | 2 | 1 | 0 |
|---|---|---|---|---|---|---|
| Power of 2 | 32 | 16 | 8 | 4 | 2 | 1 |
| Bit Value | 1 | 0 | 1 | 1 | 0 | 1 |
| Contribution | 32 | 0 | 8 | 4 | 0 | 1 |

Total:

```text
45
```

---

## Important Observation

Higher bits dominate lower bits.

Example:

```text
2^5 = 32
```

This is larger than:

```text
16 + 8 + 4 + 2 + 1
```

Hence greedy solutions often iterate:

```text
31 → 0
```

---

## Brute Force Thinking

When starting any bit problem:

### Ask:

1. Can I iterate all subsets?
2. Can bits be processed independently?
3. Can XOR cancel things?
4. Is there cyclicity?
5. Is there contribution of each bit?
6. Can greedy highest-bit work?
7. Can prefix bit counts optimize?

---

## Classic Example

Input:

```text
[1,3,5,7]
```

Binary representation:

| Number | Bit3 | Bit2 | Bit1 | Bit0 |
|---|---|---|---|---|
| 1 | 0 | 0 | 0 | 1 |
| 3 | 0 | 0 | 1 | 1 |
| 5 | 0 | 1 | 0 | 1 |
| 7 | 0 | 1 | 1 | 1 |

---

## Brute Force Code

```cpp
for(int i=0;i<n;i++) {
    for(int j=i+1;j<n;j++) {
        ans += (arr[i] ^ arr[j]);
    }
}
```

Complexity:

```text
O(N^2)
```

---

## Optimal Observation

Bits are independent.

This is one of the MOST important ideas in CP.

Instead of processing numbers:
- process EACH BIT separately.

---

## Dry Run

Suppose:

```text
arr = [1,3,5]
```

Bit table:

| Number | Bit2 | Bit1 | Bit0 |
|---|---|---|---|
| 1 | 0 | 0 | 1 |
| 3 | 0 | 1 | 1 |
| 5 | 1 | 0 | 1 |

Bit0:
- ones = 3
- zeros = 0

Contribution:

```text
3 × 0 × 1 = 0
```

Bit1:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 2 = 4
```

Bit2:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 4 = 8
```

Final:

```text
12
```

---

## CP Pattern Recognition

| Pattern | Technique |
|---|---|
| Pair XOR sum | Contribution |
| Count bits 1..N | Cyclicity |
| Maximum AND | Highest-bit greedy |
| Subset problems | Bitmask |
| State compression | Bitmask DP |
| Maximum XOR | Trie/XOR basis |

---

## FAANG Interview Thinking

Interviewers usually expect:

### Level 1
Brute force.

### Level 2
Observation.

### Level 3
Bit independence.

### Level 4
Mathematical reduction.

### Level 5
Reusable template.

---

## Common Mistakes

### Mistake 1

Using:

```cpp
1 << 31
```

inside int.

Can overflow.

Use:

```cpp
1LL << 31
```

---

### Mistake 2

Confusing:

```cpp
x & (1<<i)
```

with:

```cpp
x && (1<<i)
```

---

### Mistake 3

Forgetting operator precedence.

Always use brackets.

---

## Recommended Problems

### Easy
- Single Number
- Power of Two
- Counting Bits
- Missing Number

### Medium
- Subsets
- Maximum XOR
- Sum XOR Pairs
- Bitmask DP intro

### Hard
- SOS DP
- XOR Basis
- Gaussian XOR
- Steiner Tree DP

---

## Reusable Template

```cpp
for(int bit=0; bit<32; bit++) {

    int cnt = 0;

    for(int x : arr) {
        if(x & (1<<bit))
            cnt++;
    }
}
```

---

## Complexity Analysis

| Technique | Complexity |
|---|---|
| Brute force pairs | O(N²) |
| Contribution | O(32×N) |
| Subset generation | O(2^N × N) |
| Prefix bit counts | O(32×N) |
| Trie XOR | O(32×N) |

---

## Key Takeaway

Whenever you see:
- XOR
- subsets
- masks
- state compression
- maximum/minimum bit objective

Immediately think:

```text
BITS
```



---


# 3. Detailed Bit Manipulation Topic 3


## Core Idea

This section explains advanced bit manipulation concepts in a detailed CP + FAANG style.

### Why This Topic Matters

This pattern appears heavily in:
- LeetCode Medium/Hard
- Codeforces
- AtCoder
- Google interviews
- Meta interviews
- Trading systems
- Performance engineering

---

## Mental Model

Bits should always be visualized column-wise.

Example:

| Bit Position | 5 | 4 | 3 | 2 | 1 | 0 |
|---|---|---|---|---|---|---|
| Power of 2 | 32 | 16 | 8 | 4 | 2 | 1 |
| Bit Value | 1 | 0 | 1 | 1 | 0 | 1 |
| Contribution | 32 | 0 | 8 | 4 | 0 | 1 |

Total:

```text
45
```

---

## Important Observation

Higher bits dominate lower bits.

Example:

```text
2^5 = 32
```

This is larger than:

```text
16 + 8 + 4 + 2 + 1
```

Hence greedy solutions often iterate:

```text
31 → 0
```

---

## Brute Force Thinking

When starting any bit problem:

### Ask:

1. Can I iterate all subsets?
2. Can bits be processed independently?
3. Can XOR cancel things?
4. Is there cyclicity?
5. Is there contribution of each bit?
6. Can greedy highest-bit work?
7. Can prefix bit counts optimize?

---

## Classic Example

Input:

```text
[1,3,5,7]
```

Binary representation:

| Number | Bit3 | Bit2 | Bit1 | Bit0 |
|---|---|---|---|---|
| 1 | 0 | 0 | 0 | 1 |
| 3 | 0 | 0 | 1 | 1 |
| 5 | 0 | 1 | 0 | 1 |
| 7 | 0 | 1 | 1 | 1 |

---

## Brute Force Code

```cpp
for(int i=0;i<n;i++) {
    for(int j=i+1;j<n;j++) {
        ans += (arr[i] ^ arr[j]);
    }
}
```

Complexity:

```text
O(N^2)
```

---

## Optimal Observation

Bits are independent.

This is one of the MOST important ideas in CP.

Instead of processing numbers:
- process EACH BIT separately.

---

## Dry Run

Suppose:

```text
arr = [1,3,5]
```

Bit table:

| Number | Bit2 | Bit1 | Bit0 |
|---|---|---|---|
| 1 | 0 | 0 | 1 |
| 3 | 0 | 1 | 1 |
| 5 | 1 | 0 | 1 |

Bit0:
- ones = 3
- zeros = 0

Contribution:

```text
3 × 0 × 1 = 0
```

Bit1:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 2 = 4
```

Bit2:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 4 = 8
```

Final:

```text
12
```

---

## CP Pattern Recognition

| Pattern | Technique |
|---|---|
| Pair XOR sum | Contribution |
| Count bits 1..N | Cyclicity |
| Maximum AND | Highest-bit greedy |
| Subset problems | Bitmask |
| State compression | Bitmask DP |
| Maximum XOR | Trie/XOR basis |

---

## FAANG Interview Thinking

Interviewers usually expect:

### Level 1
Brute force.

### Level 2
Observation.

### Level 3
Bit independence.

### Level 4
Mathematical reduction.

### Level 5
Reusable template.

---

## Common Mistakes

### Mistake 1

Using:

```cpp
1 << 31
```

inside int.

Can overflow.

Use:

```cpp
1LL << 31
```

---

### Mistake 2

Confusing:

```cpp
x & (1<<i)
```

with:

```cpp
x && (1<<i)
```

---

### Mistake 3

Forgetting operator precedence.

Always use brackets.

---

## Recommended Problems

### Easy
- Single Number
- Power of Two
- Counting Bits
- Missing Number

### Medium
- Subsets
- Maximum XOR
- Sum XOR Pairs
- Bitmask DP intro

### Hard
- SOS DP
- XOR Basis
- Gaussian XOR
- Steiner Tree DP

---

## Reusable Template

```cpp
for(int bit=0; bit<32; bit++) {

    int cnt = 0;

    for(int x : arr) {
        if(x & (1<<bit))
            cnt++;
    }
}
```

---

## Complexity Analysis

| Technique | Complexity |
|---|---|
| Brute force pairs | O(N²) |
| Contribution | O(32×N) |
| Subset generation | O(2^N × N) |
| Prefix bit counts | O(32×N) |
| Trie XOR | O(32×N) |

---

## Key Takeaway

Whenever you see:
- XOR
- subsets
- masks
- state compression
- maximum/minimum bit objective

Immediately think:

```text
BITS
```



---


# 4. Detailed Bit Manipulation Topic 4


## Core Idea

This section explains advanced bit manipulation concepts in a detailed CP + FAANG style.

### Why This Topic Matters

This pattern appears heavily in:
- LeetCode Medium/Hard
- Codeforces
- AtCoder
- Google interviews
- Meta interviews
- Trading systems
- Performance engineering

---

## Mental Model

Bits should always be visualized column-wise.

Example:

| Bit Position | 5 | 4 | 3 | 2 | 1 | 0 |
|---|---|---|---|---|---|---|
| Power of 2 | 32 | 16 | 8 | 4 | 2 | 1 |
| Bit Value | 1 | 0 | 1 | 1 | 0 | 1 |
| Contribution | 32 | 0 | 8 | 4 | 0 | 1 |

Total:

```text
45
```

---

## Important Observation

Higher bits dominate lower bits.

Example:

```text
2^5 = 32
```

This is larger than:

```text
16 + 8 + 4 + 2 + 1
```

Hence greedy solutions often iterate:

```text
31 → 0
```

---

## Brute Force Thinking

When starting any bit problem:

### Ask:

1. Can I iterate all subsets?
2. Can bits be processed independently?
3. Can XOR cancel things?
4. Is there cyclicity?
5. Is there contribution of each bit?
6. Can greedy highest-bit work?
7. Can prefix bit counts optimize?

---

## Classic Example

Input:

```text
[1,3,5,7]
```

Binary representation:

| Number | Bit3 | Bit2 | Bit1 | Bit0 |
|---|---|---|---|---|
| 1 | 0 | 0 | 0 | 1 |
| 3 | 0 | 0 | 1 | 1 |
| 5 | 0 | 1 | 0 | 1 |
| 7 | 0 | 1 | 1 | 1 |

---

## Brute Force Code

```cpp
for(int i=0;i<n;i++) {
    for(int j=i+1;j<n;j++) {
        ans += (arr[i] ^ arr[j]);
    }
}
```

Complexity:

```text
O(N^2)
```

---

## Optimal Observation

Bits are independent.

This is one of the MOST important ideas in CP.

Instead of processing numbers:
- process EACH BIT separately.

---

## Dry Run

Suppose:

```text
arr = [1,3,5]
```

Bit table:

| Number | Bit2 | Bit1 | Bit0 |
|---|---|---|---|
| 1 | 0 | 0 | 1 |
| 3 | 0 | 1 | 1 |
| 5 | 1 | 0 | 1 |

Bit0:
- ones = 3
- zeros = 0

Contribution:

```text
3 × 0 × 1 = 0
```

Bit1:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 2 = 4
```

Bit2:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 4 = 8
```

Final:

```text
12
```

---

## CP Pattern Recognition

| Pattern | Technique |
|---|---|
| Pair XOR sum | Contribution |
| Count bits 1..N | Cyclicity |
| Maximum AND | Highest-bit greedy |
| Subset problems | Bitmask |
| State compression | Bitmask DP |
| Maximum XOR | Trie/XOR basis |

---

## FAANG Interview Thinking

Interviewers usually expect:

### Level 1
Brute force.

### Level 2
Observation.

### Level 3
Bit independence.

### Level 4
Mathematical reduction.

### Level 5
Reusable template.

---

## Common Mistakes

### Mistake 1

Using:

```cpp
1 << 31
```

inside int.

Can overflow.

Use:

```cpp
1LL << 31
```

---

### Mistake 2

Confusing:

```cpp
x & (1<<i)
```

with:

```cpp
x && (1<<i)
```

---

### Mistake 3

Forgetting operator precedence.

Always use brackets.

---

## Recommended Problems

### Easy
- Single Number
- Power of Two
- Counting Bits
- Missing Number

### Medium
- Subsets
- Maximum XOR
- Sum XOR Pairs
- Bitmask DP intro

### Hard
- SOS DP
- XOR Basis
- Gaussian XOR
- Steiner Tree DP

---

## Reusable Template

```cpp
for(int bit=0; bit<32; bit++) {

    int cnt = 0;

    for(int x : arr) {
        if(x & (1<<bit))
            cnt++;
    }
}
```

---

## Complexity Analysis

| Technique | Complexity |
|---|---|
| Brute force pairs | O(N²) |
| Contribution | O(32×N) |
| Subset generation | O(2^N × N) |
| Prefix bit counts | O(32×N) |
| Trie XOR | O(32×N) |

---

## Key Takeaway

Whenever you see:
- XOR
- subsets
- masks
- state compression
- maximum/minimum bit objective

Immediately think:

```text
BITS
```



---


# 5. Detailed Bit Manipulation Topic 5


## Core Idea

This section explains advanced bit manipulation concepts in a detailed CP + FAANG style.

### Why This Topic Matters

This pattern appears heavily in:
- LeetCode Medium/Hard
- Codeforces
- AtCoder
- Google interviews
- Meta interviews
- Trading systems
- Performance engineering

---

## Mental Model

Bits should always be visualized column-wise.

Example:

| Bit Position | 5 | 4 | 3 | 2 | 1 | 0 |
|---|---|---|---|---|---|---|
| Power of 2 | 32 | 16 | 8 | 4 | 2 | 1 |
| Bit Value | 1 | 0 | 1 | 1 | 0 | 1 |
| Contribution | 32 | 0 | 8 | 4 | 0 | 1 |

Total:

```text
45
```

---

## Important Observation

Higher bits dominate lower bits.

Example:

```text
2^5 = 32
```

This is larger than:

```text
16 + 8 + 4 + 2 + 1
```

Hence greedy solutions often iterate:

```text
31 → 0
```

---

## Brute Force Thinking

When starting any bit problem:

### Ask:

1. Can I iterate all subsets?
2. Can bits be processed independently?
3. Can XOR cancel things?
4. Is there cyclicity?
5. Is there contribution of each bit?
6. Can greedy highest-bit work?
7. Can prefix bit counts optimize?

---

## Classic Example

Input:

```text
[1,3,5,7]
```

Binary representation:

| Number | Bit3 | Bit2 | Bit1 | Bit0 |
|---|---|---|---|---|
| 1 | 0 | 0 | 0 | 1 |
| 3 | 0 | 0 | 1 | 1 |
| 5 | 0 | 1 | 0 | 1 |
| 7 | 0 | 1 | 1 | 1 |

---

## Brute Force Code

```cpp
for(int i=0;i<n;i++) {
    for(int j=i+1;j<n;j++) {
        ans += (arr[i] ^ arr[j]);
    }
}
```

Complexity:

```text
O(N^2)
```

---

## Optimal Observation

Bits are independent.

This is one of the MOST important ideas in CP.

Instead of processing numbers:
- process EACH BIT separately.

---

## Dry Run

Suppose:

```text
arr = [1,3,5]
```

Bit table:

| Number | Bit2 | Bit1 | Bit0 |
|---|---|---|---|
| 1 | 0 | 0 | 1 |
| 3 | 0 | 1 | 1 |
| 5 | 1 | 0 | 1 |

Bit0:
- ones = 3
- zeros = 0

Contribution:

```text
3 × 0 × 1 = 0
```

Bit1:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 2 = 4
```

Bit2:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 4 = 8
```

Final:

```text
12
```

---

## CP Pattern Recognition

| Pattern | Technique |
|---|---|
| Pair XOR sum | Contribution |
| Count bits 1..N | Cyclicity |
| Maximum AND | Highest-bit greedy |
| Subset problems | Bitmask |
| State compression | Bitmask DP |
| Maximum XOR | Trie/XOR basis |

---

## FAANG Interview Thinking

Interviewers usually expect:

### Level 1
Brute force.

### Level 2
Observation.

### Level 3
Bit independence.

### Level 4
Mathematical reduction.

### Level 5
Reusable template.

---

## Common Mistakes

### Mistake 1

Using:

```cpp
1 << 31
```

inside int.

Can overflow.

Use:

```cpp
1LL << 31
```

---

### Mistake 2

Confusing:

```cpp
x & (1<<i)
```

with:

```cpp
x && (1<<i)
```

---

### Mistake 3

Forgetting operator precedence.

Always use brackets.

---

## Recommended Problems

### Easy
- Single Number
- Power of Two
- Counting Bits
- Missing Number

### Medium
- Subsets
- Maximum XOR
- Sum XOR Pairs
- Bitmask DP intro

### Hard
- SOS DP
- XOR Basis
- Gaussian XOR
- Steiner Tree DP

---

## Reusable Template

```cpp
for(int bit=0; bit<32; bit++) {

    int cnt = 0;

    for(int x : arr) {
        if(x & (1<<bit))
            cnt++;
    }
}
```

---

## Complexity Analysis

| Technique | Complexity |
|---|---|
| Brute force pairs | O(N²) |
| Contribution | O(32×N) |
| Subset generation | O(2^N × N) |
| Prefix bit counts | O(32×N) |
| Trie XOR | O(32×N) |

---

## Key Takeaway

Whenever you see:
- XOR
- subsets
- masks
- state compression
- maximum/minimum bit objective

Immediately think:

```text
BITS
```



---


# 6. Detailed Bit Manipulation Topic 6


## Core Idea

This section explains advanced bit manipulation concepts in a detailed CP + FAANG style.

### Why This Topic Matters

This pattern appears heavily in:
- LeetCode Medium/Hard
- Codeforces
- AtCoder
- Google interviews
- Meta interviews
- Trading systems
- Performance engineering

---

## Mental Model

Bits should always be visualized column-wise.

Example:

| Bit Position | 5 | 4 | 3 | 2 | 1 | 0 |
|---|---|---|---|---|---|---|
| Power of 2 | 32 | 16 | 8 | 4 | 2 | 1 |
| Bit Value | 1 | 0 | 1 | 1 | 0 | 1 |
| Contribution | 32 | 0 | 8 | 4 | 0 | 1 |

Total:

```text
45
```

---

## Important Observation

Higher bits dominate lower bits.

Example:

```text
2^5 = 32
```

This is larger than:

```text
16 + 8 + 4 + 2 + 1
```

Hence greedy solutions often iterate:

```text
31 → 0
```

---

## Brute Force Thinking

When starting any bit problem:

### Ask:

1. Can I iterate all subsets?
2. Can bits be processed independently?
3. Can XOR cancel things?
4. Is there cyclicity?
5. Is there contribution of each bit?
6. Can greedy highest-bit work?
7. Can prefix bit counts optimize?

---

## Classic Example

Input:

```text
[1,3,5,7]
```

Binary representation:

| Number | Bit3 | Bit2 | Bit1 | Bit0 |
|---|---|---|---|---|
| 1 | 0 | 0 | 0 | 1 |
| 3 | 0 | 0 | 1 | 1 |
| 5 | 0 | 1 | 0 | 1 |
| 7 | 0 | 1 | 1 | 1 |

---

## Brute Force Code

```cpp
for(int i=0;i<n;i++) {
    for(int j=i+1;j<n;j++) {
        ans += (arr[i] ^ arr[j]);
    }
}
```

Complexity:

```text
O(N^2)
```

---

## Optimal Observation

Bits are independent.

This is one of the MOST important ideas in CP.

Instead of processing numbers:
- process EACH BIT separately.

---

## Dry Run

Suppose:

```text
arr = [1,3,5]
```

Bit table:

| Number | Bit2 | Bit1 | Bit0 |
|---|---|---|---|
| 1 | 0 | 0 | 1 |
| 3 | 0 | 1 | 1 |
| 5 | 1 | 0 | 1 |

Bit0:
- ones = 3
- zeros = 0

Contribution:

```text
3 × 0 × 1 = 0
```

Bit1:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 2 = 4
```

Bit2:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 4 = 8
```

Final:

```text
12
```

---

## CP Pattern Recognition

| Pattern | Technique |
|---|---|
| Pair XOR sum | Contribution |
| Count bits 1..N | Cyclicity |
| Maximum AND | Highest-bit greedy |
| Subset problems | Bitmask |
| State compression | Bitmask DP |
| Maximum XOR | Trie/XOR basis |

---

## FAANG Interview Thinking

Interviewers usually expect:

### Level 1
Brute force.

### Level 2
Observation.

### Level 3
Bit independence.

### Level 4
Mathematical reduction.

### Level 5
Reusable template.

---

## Common Mistakes

### Mistake 1

Using:

```cpp
1 << 31
```

inside int.

Can overflow.

Use:

```cpp
1LL << 31
```

---

### Mistake 2

Confusing:

```cpp
x & (1<<i)
```

with:

```cpp
x && (1<<i)
```

---

### Mistake 3

Forgetting operator precedence.

Always use brackets.

---

## Recommended Problems

### Easy
- Single Number
- Power of Two
- Counting Bits
- Missing Number

### Medium
- Subsets
- Maximum XOR
- Sum XOR Pairs
- Bitmask DP intro

### Hard
- SOS DP
- XOR Basis
- Gaussian XOR
- Steiner Tree DP

---

## Reusable Template

```cpp
for(int bit=0; bit<32; bit++) {

    int cnt = 0;

    for(int x : arr) {
        if(x & (1<<bit))
            cnt++;
    }
}
```

---

## Complexity Analysis

| Technique | Complexity |
|---|---|
| Brute force pairs | O(N²) |
| Contribution | O(32×N) |
| Subset generation | O(2^N × N) |
| Prefix bit counts | O(32×N) |
| Trie XOR | O(32×N) |

---

## Key Takeaway

Whenever you see:
- XOR
- subsets
- masks
- state compression
- maximum/minimum bit objective

Immediately think:

```text
BITS
```



---


# 7. Detailed Bit Manipulation Topic 7


## Core Idea

This section explains advanced bit manipulation concepts in a detailed CP + FAANG style.

### Why This Topic Matters

This pattern appears heavily in:
- LeetCode Medium/Hard
- Codeforces
- AtCoder
- Google interviews
- Meta interviews
- Trading systems
- Performance engineering

---

## Mental Model

Bits should always be visualized column-wise.

Example:

| Bit Position | 5 | 4 | 3 | 2 | 1 | 0 |
|---|---|---|---|---|---|---|
| Power of 2 | 32 | 16 | 8 | 4 | 2 | 1 |
| Bit Value | 1 | 0 | 1 | 1 | 0 | 1 |
| Contribution | 32 | 0 | 8 | 4 | 0 | 1 |

Total:

```text
45
```

---

## Important Observation

Higher bits dominate lower bits.

Example:

```text
2^5 = 32
```

This is larger than:

```text
16 + 8 + 4 + 2 + 1
```

Hence greedy solutions often iterate:

```text
31 → 0
```

---

## Brute Force Thinking

When starting any bit problem:

### Ask:

1. Can I iterate all subsets?
2. Can bits be processed independently?
3. Can XOR cancel things?
4. Is there cyclicity?
5. Is there contribution of each bit?
6. Can greedy highest-bit work?
7. Can prefix bit counts optimize?

---

## Classic Example

Input:

```text
[1,3,5,7]
```

Binary representation:

| Number | Bit3 | Bit2 | Bit1 | Bit0 |
|---|---|---|---|---|
| 1 | 0 | 0 | 0 | 1 |
| 3 | 0 | 0 | 1 | 1 |
| 5 | 0 | 1 | 0 | 1 |
| 7 | 0 | 1 | 1 | 1 |

---

## Brute Force Code

```cpp
for(int i=0;i<n;i++) {
    for(int j=i+1;j<n;j++) {
        ans += (arr[i] ^ arr[j]);
    }
}
```

Complexity:

```text
O(N^2)
```

---

## Optimal Observation

Bits are independent.

This is one of the MOST important ideas in CP.

Instead of processing numbers:
- process EACH BIT separately.

---

## Dry Run

Suppose:

```text
arr = [1,3,5]
```

Bit table:

| Number | Bit2 | Bit1 | Bit0 |
|---|---|---|---|
| 1 | 0 | 0 | 1 |
| 3 | 0 | 1 | 1 |
| 5 | 1 | 0 | 1 |

Bit0:
- ones = 3
- zeros = 0

Contribution:

```text
3 × 0 × 1 = 0
```

Bit1:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 2 = 4
```

Bit2:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 4 = 8
```

Final:

```text
12
```

---

## CP Pattern Recognition

| Pattern | Technique |
|---|---|
| Pair XOR sum | Contribution |
| Count bits 1..N | Cyclicity |
| Maximum AND | Highest-bit greedy |
| Subset problems | Bitmask |
| State compression | Bitmask DP |
| Maximum XOR | Trie/XOR basis |

---

## FAANG Interview Thinking

Interviewers usually expect:

### Level 1
Brute force.

### Level 2
Observation.

### Level 3
Bit independence.

### Level 4
Mathematical reduction.

### Level 5
Reusable template.

---

## Common Mistakes

### Mistake 1

Using:

```cpp
1 << 31
```

inside int.

Can overflow.

Use:

```cpp
1LL << 31
```

---

### Mistake 2

Confusing:

```cpp
x & (1<<i)
```

with:

```cpp
x && (1<<i)
```

---

### Mistake 3

Forgetting operator precedence.

Always use brackets.

---

## Recommended Problems

### Easy
- Single Number
- Power of Two
- Counting Bits
- Missing Number

### Medium
- Subsets
- Maximum XOR
- Sum XOR Pairs
- Bitmask DP intro

### Hard
- SOS DP
- XOR Basis
- Gaussian XOR
- Steiner Tree DP

---

## Reusable Template

```cpp
for(int bit=0; bit<32; bit++) {

    int cnt = 0;

    for(int x : arr) {
        if(x & (1<<bit))
            cnt++;
    }
}
```

---

## Complexity Analysis

| Technique | Complexity |
|---|---|
| Brute force pairs | O(N²) |
| Contribution | O(32×N) |
| Subset generation | O(2^N × N) |
| Prefix bit counts | O(32×N) |
| Trie XOR | O(32×N) |

---

## Key Takeaway

Whenever you see:
- XOR
- subsets
- masks
- state compression
- maximum/minimum bit objective

Immediately think:

```text
BITS
```



---


# 8. Detailed Bit Manipulation Topic 8


## Core Idea

This section explains advanced bit manipulation concepts in a detailed CP + FAANG style.

### Why This Topic Matters

This pattern appears heavily in:
- LeetCode Medium/Hard
- Codeforces
- AtCoder
- Google interviews
- Meta interviews
- Trading systems
- Performance engineering

---

## Mental Model

Bits should always be visualized column-wise.

Example:

| Bit Position | 5 | 4 | 3 | 2 | 1 | 0 |
|---|---|---|---|---|---|---|
| Power of 2 | 32 | 16 | 8 | 4 | 2 | 1 |
| Bit Value | 1 | 0 | 1 | 1 | 0 | 1 |
| Contribution | 32 | 0 | 8 | 4 | 0 | 1 |

Total:

```text
45
```

---

## Important Observation

Higher bits dominate lower bits.

Example:

```text
2^5 = 32
```

This is larger than:

```text
16 + 8 + 4 + 2 + 1
```

Hence greedy solutions often iterate:

```text
31 → 0
```

---

## Brute Force Thinking

When starting any bit problem:

### Ask:

1. Can I iterate all subsets?
2. Can bits be processed independently?
3. Can XOR cancel things?
4. Is there cyclicity?
5. Is there contribution of each bit?
6. Can greedy highest-bit work?
7. Can prefix bit counts optimize?

---

## Classic Example

Input:

```text
[1,3,5,7]
```

Binary representation:

| Number | Bit3 | Bit2 | Bit1 | Bit0 |
|---|---|---|---|---|
| 1 | 0 | 0 | 0 | 1 |
| 3 | 0 | 0 | 1 | 1 |
| 5 | 0 | 1 | 0 | 1 |
| 7 | 0 | 1 | 1 | 1 |

---

## Brute Force Code

```cpp
for(int i=0;i<n;i++) {
    for(int j=i+1;j<n;j++) {
        ans += (arr[i] ^ arr[j]);
    }
}
```

Complexity:

```text
O(N^2)
```

---

## Optimal Observation

Bits are independent.

This is one of the MOST important ideas in CP.

Instead of processing numbers:
- process EACH BIT separately.

---

## Dry Run

Suppose:

```text
arr = [1,3,5]
```

Bit table:

| Number | Bit2 | Bit1 | Bit0 |
|---|---|---|---|
| 1 | 0 | 0 | 1 |
| 3 | 0 | 1 | 1 |
| 5 | 1 | 0 | 1 |

Bit0:
- ones = 3
- zeros = 0

Contribution:

```text
3 × 0 × 1 = 0
```

Bit1:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 2 = 4
```

Bit2:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 4 = 8
```

Final:

```text
12
```

---

## CP Pattern Recognition

| Pattern | Technique |
|---|---|
| Pair XOR sum | Contribution |
| Count bits 1..N | Cyclicity |
| Maximum AND | Highest-bit greedy |
| Subset problems | Bitmask |
| State compression | Bitmask DP |
| Maximum XOR | Trie/XOR basis |

---

## FAANG Interview Thinking

Interviewers usually expect:

### Level 1
Brute force.

### Level 2
Observation.

### Level 3
Bit independence.

### Level 4
Mathematical reduction.

### Level 5
Reusable template.

---

## Common Mistakes

### Mistake 1

Using:

```cpp
1 << 31
```

inside int.

Can overflow.

Use:

```cpp
1LL << 31
```

---

### Mistake 2

Confusing:

```cpp
x & (1<<i)
```

with:

```cpp
x && (1<<i)
```

---

### Mistake 3

Forgetting operator precedence.

Always use brackets.

---

## Recommended Problems

### Easy
- Single Number
- Power of Two
- Counting Bits
- Missing Number

### Medium
- Subsets
- Maximum XOR
- Sum XOR Pairs
- Bitmask DP intro

### Hard
- SOS DP
- XOR Basis
- Gaussian XOR
- Steiner Tree DP

---

## Reusable Template

```cpp
for(int bit=0; bit<32; bit++) {

    int cnt = 0;

    for(int x : arr) {
        if(x & (1<<bit))
            cnt++;
    }
}
```

---

## Complexity Analysis

| Technique | Complexity |
|---|---|
| Brute force pairs | O(N²) |
| Contribution | O(32×N) |
| Subset generation | O(2^N × N) |
| Prefix bit counts | O(32×N) |
| Trie XOR | O(32×N) |

---

## Key Takeaway

Whenever you see:
- XOR
- subsets
- masks
- state compression
- maximum/minimum bit objective

Immediately think:

```text
BITS
```



---


# 9. Detailed Bit Manipulation Topic 9


## Core Idea

This section explains advanced bit manipulation concepts in a detailed CP + FAANG style.

### Why This Topic Matters

This pattern appears heavily in:
- LeetCode Medium/Hard
- Codeforces
- AtCoder
- Google interviews
- Meta interviews
- Trading systems
- Performance engineering

---

## Mental Model

Bits should always be visualized column-wise.

Example:

| Bit Position | 5 | 4 | 3 | 2 | 1 | 0 |
|---|---|---|---|---|---|---|
| Power of 2 | 32 | 16 | 8 | 4 | 2 | 1 |
| Bit Value | 1 | 0 | 1 | 1 | 0 | 1 |
| Contribution | 32 | 0 | 8 | 4 | 0 | 1 |

Total:

```text
45
```

---

## Important Observation

Higher bits dominate lower bits.

Example:

```text
2^5 = 32
```

This is larger than:

```text
16 + 8 + 4 + 2 + 1
```

Hence greedy solutions often iterate:

```text
31 → 0
```

---

## Brute Force Thinking

When starting any bit problem:

### Ask:

1. Can I iterate all subsets?
2. Can bits be processed independently?
3. Can XOR cancel things?
4. Is there cyclicity?
5. Is there contribution of each bit?
6. Can greedy highest-bit work?
7. Can prefix bit counts optimize?

---

## Classic Example

Input:

```text
[1,3,5,7]
```

Binary representation:

| Number | Bit3 | Bit2 | Bit1 | Bit0 |
|---|---|---|---|---|
| 1 | 0 | 0 | 0 | 1 |
| 3 | 0 | 0 | 1 | 1 |
| 5 | 0 | 1 | 0 | 1 |
| 7 | 0 | 1 | 1 | 1 |

---

## Brute Force Code

```cpp
for(int i=0;i<n;i++) {
    for(int j=i+1;j<n;j++) {
        ans += (arr[i] ^ arr[j]);
    }
}
```

Complexity:

```text
O(N^2)
```

---

## Optimal Observation

Bits are independent.

This is one of the MOST important ideas in CP.

Instead of processing numbers:
- process EACH BIT separately.

---

## Dry Run

Suppose:

```text
arr = [1,3,5]
```

Bit table:

| Number | Bit2 | Bit1 | Bit0 |
|---|---|---|---|
| 1 | 0 | 0 | 1 |
| 3 | 0 | 1 | 1 |
| 5 | 1 | 0 | 1 |

Bit0:
- ones = 3
- zeros = 0

Contribution:

```text
3 × 0 × 1 = 0
```

Bit1:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 2 = 4
```

Bit2:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 4 = 8
```

Final:

```text
12
```

---

## CP Pattern Recognition

| Pattern | Technique |
|---|---|
| Pair XOR sum | Contribution |
| Count bits 1..N | Cyclicity |
| Maximum AND | Highest-bit greedy |
| Subset problems | Bitmask |
| State compression | Bitmask DP |
| Maximum XOR | Trie/XOR basis |

---

## FAANG Interview Thinking

Interviewers usually expect:

### Level 1
Brute force.

### Level 2
Observation.

### Level 3
Bit independence.

### Level 4
Mathematical reduction.

### Level 5
Reusable template.

---

## Common Mistakes

### Mistake 1

Using:

```cpp
1 << 31
```

inside int.

Can overflow.

Use:

```cpp
1LL << 31
```

---

### Mistake 2

Confusing:

```cpp
x & (1<<i)
```

with:

```cpp
x && (1<<i)
```

---

### Mistake 3

Forgetting operator precedence.

Always use brackets.

---

## Recommended Problems

### Easy
- Single Number
- Power of Two
- Counting Bits
- Missing Number

### Medium
- Subsets
- Maximum XOR
- Sum XOR Pairs
- Bitmask DP intro

### Hard
- SOS DP
- XOR Basis
- Gaussian XOR
- Steiner Tree DP

---

## Reusable Template

```cpp
for(int bit=0; bit<32; bit++) {

    int cnt = 0;

    for(int x : arr) {
        if(x & (1<<bit))
            cnt++;
    }
}
```

---

## Complexity Analysis

| Technique | Complexity |
|---|---|
| Brute force pairs | O(N²) |
| Contribution | O(32×N) |
| Subset generation | O(2^N × N) |
| Prefix bit counts | O(32×N) |
| Trie XOR | O(32×N) |

---

## Key Takeaway

Whenever you see:
- XOR
- subsets
- masks
- state compression
- maximum/minimum bit objective

Immediately think:

```text
BITS
```



---


# 10. Detailed Bit Manipulation Topic 10


## Core Idea

This section explains advanced bit manipulation concepts in a detailed CP + FAANG style.

### Why This Topic Matters

This pattern appears heavily in:
- LeetCode Medium/Hard
- Codeforces
- AtCoder
- Google interviews
- Meta interviews
- Trading systems
- Performance engineering

---

## Mental Model

Bits should always be visualized column-wise.

Example:

| Bit Position | 5 | 4 | 3 | 2 | 1 | 0 |
|---|---|---|---|---|---|---|
| Power of 2 | 32 | 16 | 8 | 4 | 2 | 1 |
| Bit Value | 1 | 0 | 1 | 1 | 0 | 1 |
| Contribution | 32 | 0 | 8 | 4 | 0 | 1 |

Total:

```text
45
```

---

## Important Observation

Higher bits dominate lower bits.

Example:

```text
2^5 = 32
```

This is larger than:

```text
16 + 8 + 4 + 2 + 1
```

Hence greedy solutions often iterate:

```text
31 → 0
```

---

## Brute Force Thinking

When starting any bit problem:

### Ask:

1. Can I iterate all subsets?
2. Can bits be processed independently?
3. Can XOR cancel things?
4. Is there cyclicity?
5. Is there contribution of each bit?
6. Can greedy highest-bit work?
7. Can prefix bit counts optimize?

---

## Classic Example

Input:

```text
[1,3,5,7]
```

Binary representation:

| Number | Bit3 | Bit2 | Bit1 | Bit0 |
|---|---|---|---|---|
| 1 | 0 | 0 | 0 | 1 |
| 3 | 0 | 0 | 1 | 1 |
| 5 | 0 | 1 | 0 | 1 |
| 7 | 0 | 1 | 1 | 1 |

---

## Brute Force Code

```cpp
for(int i=0;i<n;i++) {
    for(int j=i+1;j<n;j++) {
        ans += (arr[i] ^ arr[j]);
    }
}
```

Complexity:

```text
O(N^2)
```

---

## Optimal Observation

Bits are independent.

This is one of the MOST important ideas in CP.

Instead of processing numbers:
- process EACH BIT separately.

---

## Dry Run

Suppose:

```text
arr = [1,3,5]
```

Bit table:

| Number | Bit2 | Bit1 | Bit0 |
|---|---|---|---|
| 1 | 0 | 0 | 1 |
| 3 | 0 | 1 | 1 |
| 5 | 1 | 0 | 1 |

Bit0:
- ones = 3
- zeros = 0

Contribution:

```text
3 × 0 × 1 = 0
```

Bit1:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 2 = 4
```

Bit2:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 4 = 8
```

Final:

```text
12
```

---

## CP Pattern Recognition

| Pattern | Technique |
|---|---|
| Pair XOR sum | Contribution |
| Count bits 1..N | Cyclicity |
| Maximum AND | Highest-bit greedy |
| Subset problems | Bitmask |
| State compression | Bitmask DP |
| Maximum XOR | Trie/XOR basis |

---

## FAANG Interview Thinking

Interviewers usually expect:

### Level 1
Brute force.

### Level 2
Observation.

### Level 3
Bit independence.

### Level 4
Mathematical reduction.

### Level 5
Reusable template.

---

## Common Mistakes

### Mistake 1

Using:

```cpp
1 << 31
```

inside int.

Can overflow.

Use:

```cpp
1LL << 31
```

---

### Mistake 2

Confusing:

```cpp
x & (1<<i)
```

with:

```cpp
x && (1<<i)
```

---

### Mistake 3

Forgetting operator precedence.

Always use brackets.

---

## Recommended Problems

### Easy
- Single Number
- Power of Two
- Counting Bits
- Missing Number

### Medium
- Subsets
- Maximum XOR
- Sum XOR Pairs
- Bitmask DP intro

### Hard
- SOS DP
- XOR Basis
- Gaussian XOR
- Steiner Tree DP

---

## Reusable Template

```cpp
for(int bit=0; bit<32; bit++) {

    int cnt = 0;

    for(int x : arr) {
        if(x & (1<<bit))
            cnt++;
    }
}
```

---

## Complexity Analysis

| Technique | Complexity |
|---|---|
| Brute force pairs | O(N²) |
| Contribution | O(32×N) |
| Subset generation | O(2^N × N) |
| Prefix bit counts | O(32×N) |
| Trie XOR | O(32×N) |

---

## Key Takeaway

Whenever you see:
- XOR
- subsets
- masks
- state compression
- maximum/minimum bit objective

Immediately think:

```text
BITS
```



---


# 11. Detailed Bit Manipulation Topic 11


## Core Idea

This section explains advanced bit manipulation concepts in a detailed CP + FAANG style.

### Why This Topic Matters

This pattern appears heavily in:
- LeetCode Medium/Hard
- Codeforces
- AtCoder
- Google interviews
- Meta interviews
- Trading systems
- Performance engineering

---

## Mental Model

Bits should always be visualized column-wise.

Example:

| Bit Position | 5 | 4 | 3 | 2 | 1 | 0 |
|---|---|---|---|---|---|---|
| Power of 2 | 32 | 16 | 8 | 4 | 2 | 1 |
| Bit Value | 1 | 0 | 1 | 1 | 0 | 1 |
| Contribution | 32 | 0 | 8 | 4 | 0 | 1 |

Total:

```text
45
```

---

## Important Observation

Higher bits dominate lower bits.

Example:

```text
2^5 = 32
```

This is larger than:

```text
16 + 8 + 4 + 2 + 1
```

Hence greedy solutions often iterate:

```text
31 → 0
```

---

## Brute Force Thinking

When starting any bit problem:

### Ask:

1. Can I iterate all subsets?
2. Can bits be processed independently?
3. Can XOR cancel things?
4. Is there cyclicity?
5. Is there contribution of each bit?
6. Can greedy highest-bit work?
7. Can prefix bit counts optimize?

---

## Classic Example

Input:

```text
[1,3,5,7]
```

Binary representation:

| Number | Bit3 | Bit2 | Bit1 | Bit0 |
|---|---|---|---|---|
| 1 | 0 | 0 | 0 | 1 |
| 3 | 0 | 0 | 1 | 1 |
| 5 | 0 | 1 | 0 | 1 |
| 7 | 0 | 1 | 1 | 1 |

---

## Brute Force Code

```cpp
for(int i=0;i<n;i++) {
    for(int j=i+1;j<n;j++) {
        ans += (arr[i] ^ arr[j]);
    }
}
```

Complexity:

```text
O(N^2)
```

---

## Optimal Observation

Bits are independent.

This is one of the MOST important ideas in CP.

Instead of processing numbers:
- process EACH BIT separately.

---

## Dry Run

Suppose:

```text
arr = [1,3,5]
```

Bit table:

| Number | Bit2 | Bit1 | Bit0 |
|---|---|---|---|
| 1 | 0 | 0 | 1 |
| 3 | 0 | 1 | 1 |
| 5 | 1 | 0 | 1 |

Bit0:
- ones = 3
- zeros = 0

Contribution:

```text
3 × 0 × 1 = 0
```

Bit1:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 2 = 4
```

Bit2:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 4 = 8
```

Final:

```text
12
```

---

## CP Pattern Recognition

| Pattern | Technique |
|---|---|
| Pair XOR sum | Contribution |
| Count bits 1..N | Cyclicity |
| Maximum AND | Highest-bit greedy |
| Subset problems | Bitmask |
| State compression | Bitmask DP |
| Maximum XOR | Trie/XOR basis |

---

## FAANG Interview Thinking

Interviewers usually expect:

### Level 1
Brute force.

### Level 2
Observation.

### Level 3
Bit independence.

### Level 4
Mathematical reduction.

### Level 5
Reusable template.

---

## Common Mistakes

### Mistake 1

Using:

```cpp
1 << 31
```

inside int.

Can overflow.

Use:

```cpp
1LL << 31
```

---

### Mistake 2

Confusing:

```cpp
x & (1<<i)
```

with:

```cpp
x && (1<<i)
```

---

### Mistake 3

Forgetting operator precedence.

Always use brackets.

---

## Recommended Problems

### Easy
- Single Number
- Power of Two
- Counting Bits
- Missing Number

### Medium
- Subsets
- Maximum XOR
- Sum XOR Pairs
- Bitmask DP intro

### Hard
- SOS DP
- XOR Basis
- Gaussian XOR
- Steiner Tree DP

---

## Reusable Template

```cpp
for(int bit=0; bit<32; bit++) {

    int cnt = 0;

    for(int x : arr) {
        if(x & (1<<bit))
            cnt++;
    }
}
```

---

## Complexity Analysis

| Technique | Complexity |
|---|---|
| Brute force pairs | O(N²) |
| Contribution | O(32×N) |
| Subset generation | O(2^N × N) |
| Prefix bit counts | O(32×N) |
| Trie XOR | O(32×N) |

---

## Key Takeaway

Whenever you see:
- XOR
- subsets
- masks
- state compression
- maximum/minimum bit objective

Immediately think:

```text
BITS
```



---


# 12. Detailed Bit Manipulation Topic 12


## Core Idea

This section explains advanced bit manipulation concepts in a detailed CP + FAANG style.

### Why This Topic Matters

This pattern appears heavily in:
- LeetCode Medium/Hard
- Codeforces
- AtCoder
- Google interviews
- Meta interviews
- Trading systems
- Performance engineering

---

## Mental Model

Bits should always be visualized column-wise.

Example:

| Bit Position | 5 | 4 | 3 | 2 | 1 | 0 |
|---|---|---|---|---|---|---|
| Power of 2 | 32 | 16 | 8 | 4 | 2 | 1 |
| Bit Value | 1 | 0 | 1 | 1 | 0 | 1 |
| Contribution | 32 | 0 | 8 | 4 | 0 | 1 |

Total:

```text
45
```

---

## Important Observation

Higher bits dominate lower bits.

Example:

```text
2^5 = 32
```

This is larger than:

```text
16 + 8 + 4 + 2 + 1
```

Hence greedy solutions often iterate:

```text
31 → 0
```

---

## Brute Force Thinking

When starting any bit problem:

### Ask:

1. Can I iterate all subsets?
2. Can bits be processed independently?
3. Can XOR cancel things?
4. Is there cyclicity?
5. Is there contribution of each bit?
6. Can greedy highest-bit work?
7. Can prefix bit counts optimize?

---

## Classic Example

Input:

```text
[1,3,5,7]
```

Binary representation:

| Number | Bit3 | Bit2 | Bit1 | Bit0 |
|---|---|---|---|---|
| 1 | 0 | 0 | 0 | 1 |
| 3 | 0 | 0 | 1 | 1 |
| 5 | 0 | 1 | 0 | 1 |
| 7 | 0 | 1 | 1 | 1 |

---

## Brute Force Code

```cpp
for(int i=0;i<n;i++) {
    for(int j=i+1;j<n;j++) {
        ans += (arr[i] ^ arr[j]);
    }
}
```

Complexity:

```text
O(N^2)
```

---

## Optimal Observation

Bits are independent.

This is one of the MOST important ideas in CP.

Instead of processing numbers:
- process EACH BIT separately.

---

## Dry Run

Suppose:

```text
arr = [1,3,5]
```

Bit table:

| Number | Bit2 | Bit1 | Bit0 |
|---|---|---|---|
| 1 | 0 | 0 | 1 |
| 3 | 0 | 1 | 1 |
| 5 | 1 | 0 | 1 |

Bit0:
- ones = 3
- zeros = 0

Contribution:

```text
3 × 0 × 1 = 0
```

Bit1:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 2 = 4
```

Bit2:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 4 = 8
```

Final:

```text
12
```

---

## CP Pattern Recognition

| Pattern | Technique |
|---|---|
| Pair XOR sum | Contribution |
| Count bits 1..N | Cyclicity |
| Maximum AND | Highest-bit greedy |
| Subset problems | Bitmask |
| State compression | Bitmask DP |
| Maximum XOR | Trie/XOR basis |

---

## FAANG Interview Thinking

Interviewers usually expect:

### Level 1
Brute force.

### Level 2
Observation.

### Level 3
Bit independence.

### Level 4
Mathematical reduction.

### Level 5
Reusable template.

---

## Common Mistakes

### Mistake 1

Using:

```cpp
1 << 31
```

inside int.

Can overflow.

Use:

```cpp
1LL << 31
```

---

### Mistake 2

Confusing:

```cpp
x & (1<<i)
```

with:

```cpp
x && (1<<i)
```

---

### Mistake 3

Forgetting operator precedence.

Always use brackets.

---

## Recommended Problems

### Easy
- Single Number
- Power of Two
- Counting Bits
- Missing Number

### Medium
- Subsets
- Maximum XOR
- Sum XOR Pairs
- Bitmask DP intro

### Hard
- SOS DP
- XOR Basis
- Gaussian XOR
- Steiner Tree DP

---

## Reusable Template

```cpp
for(int bit=0; bit<32; bit++) {

    int cnt = 0;

    for(int x : arr) {
        if(x & (1<<bit))
            cnt++;
    }
}
```

---

## Complexity Analysis

| Technique | Complexity |
|---|---|
| Brute force pairs | O(N²) |
| Contribution | O(32×N) |
| Subset generation | O(2^N × N) |
| Prefix bit counts | O(32×N) |
| Trie XOR | O(32×N) |

---

## Key Takeaway

Whenever you see:
- XOR
- subsets
- masks
- state compression
- maximum/minimum bit objective

Immediately think:

```text
BITS
```



---


# 13. Detailed Bit Manipulation Topic 13


## Core Idea

This section explains advanced bit manipulation concepts in a detailed CP + FAANG style.

### Why This Topic Matters

This pattern appears heavily in:
- LeetCode Medium/Hard
- Codeforces
- AtCoder
- Google interviews
- Meta interviews
- Trading systems
- Performance engineering

---

## Mental Model

Bits should always be visualized column-wise.

Example:

| Bit Position | 5 | 4 | 3 | 2 | 1 | 0 |
|---|---|---|---|---|---|---|
| Power of 2 | 32 | 16 | 8 | 4 | 2 | 1 |
| Bit Value | 1 | 0 | 1 | 1 | 0 | 1 |
| Contribution | 32 | 0 | 8 | 4 | 0 | 1 |

Total:

```text
45
```

---

## Important Observation

Higher bits dominate lower bits.

Example:

```text
2^5 = 32
```

This is larger than:

```text
16 + 8 + 4 + 2 + 1
```

Hence greedy solutions often iterate:

```text
31 → 0
```

---

## Brute Force Thinking

When starting any bit problem:

### Ask:

1. Can I iterate all subsets?
2. Can bits be processed independently?
3. Can XOR cancel things?
4. Is there cyclicity?
5. Is there contribution of each bit?
6. Can greedy highest-bit work?
7. Can prefix bit counts optimize?

---

## Classic Example

Input:

```text
[1,3,5,7]
```

Binary representation:

| Number | Bit3 | Bit2 | Bit1 | Bit0 |
|---|---|---|---|---|
| 1 | 0 | 0 | 0 | 1 |
| 3 | 0 | 0 | 1 | 1 |
| 5 | 0 | 1 | 0 | 1 |
| 7 | 0 | 1 | 1 | 1 |

---

## Brute Force Code

```cpp
for(int i=0;i<n;i++) {
    for(int j=i+1;j<n;j++) {
        ans += (arr[i] ^ arr[j]);
    }
}
```

Complexity:

```text
O(N^2)
```

---

## Optimal Observation

Bits are independent.

This is one of the MOST important ideas in CP.

Instead of processing numbers:
- process EACH BIT separately.

---

## Dry Run

Suppose:

```text
arr = [1,3,5]
```

Bit table:

| Number | Bit2 | Bit1 | Bit0 |
|---|---|---|---|
| 1 | 0 | 0 | 1 |
| 3 | 0 | 1 | 1 |
| 5 | 1 | 0 | 1 |

Bit0:
- ones = 3
- zeros = 0

Contribution:

```text
3 × 0 × 1 = 0
```

Bit1:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 2 = 4
```

Bit2:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 4 = 8
```

Final:

```text
12
```

---

## CP Pattern Recognition

| Pattern | Technique |
|---|---|
| Pair XOR sum | Contribution |
| Count bits 1..N | Cyclicity |
| Maximum AND | Highest-bit greedy |
| Subset problems | Bitmask |
| State compression | Bitmask DP |
| Maximum XOR | Trie/XOR basis |

---

## FAANG Interview Thinking

Interviewers usually expect:

### Level 1
Brute force.

### Level 2
Observation.

### Level 3
Bit independence.

### Level 4
Mathematical reduction.

### Level 5
Reusable template.

---

## Common Mistakes

### Mistake 1

Using:

```cpp
1 << 31
```

inside int.

Can overflow.

Use:

```cpp
1LL << 31
```

---

### Mistake 2

Confusing:

```cpp
x & (1<<i)
```

with:

```cpp
x && (1<<i)
```

---

### Mistake 3

Forgetting operator precedence.

Always use brackets.

---

## Recommended Problems

### Easy
- Single Number
- Power of Two
- Counting Bits
- Missing Number

### Medium
- Subsets
- Maximum XOR
- Sum XOR Pairs
- Bitmask DP intro

### Hard
- SOS DP
- XOR Basis
- Gaussian XOR
- Steiner Tree DP

---

## Reusable Template

```cpp
for(int bit=0; bit<32; bit++) {

    int cnt = 0;

    for(int x : arr) {
        if(x & (1<<bit))
            cnt++;
    }
}
```

---

## Complexity Analysis

| Technique | Complexity |
|---|---|
| Brute force pairs | O(N²) |
| Contribution | O(32×N) |
| Subset generation | O(2^N × N) |
| Prefix bit counts | O(32×N) |
| Trie XOR | O(32×N) |

---

## Key Takeaway

Whenever you see:
- XOR
- subsets
- masks
- state compression
- maximum/minimum bit objective

Immediately think:

```text
BITS
```



---


# 14. Detailed Bit Manipulation Topic 14


## Core Idea

This section explains advanced bit manipulation concepts in a detailed CP + FAANG style.

### Why This Topic Matters

This pattern appears heavily in:
- LeetCode Medium/Hard
- Codeforces
- AtCoder
- Google interviews
- Meta interviews
- Trading systems
- Performance engineering

---

## Mental Model

Bits should always be visualized column-wise.

Example:

| Bit Position | 5 | 4 | 3 | 2 | 1 | 0 |
|---|---|---|---|---|---|---|
| Power of 2 | 32 | 16 | 8 | 4 | 2 | 1 |
| Bit Value | 1 | 0 | 1 | 1 | 0 | 1 |
| Contribution | 32 | 0 | 8 | 4 | 0 | 1 |

Total:

```text
45
```

---

## Important Observation

Higher bits dominate lower bits.

Example:

```text
2^5 = 32
```

This is larger than:

```text
16 + 8 + 4 + 2 + 1
```

Hence greedy solutions often iterate:

```text
31 → 0
```

---

## Brute Force Thinking

When starting any bit problem:

### Ask:

1. Can I iterate all subsets?
2. Can bits be processed independently?
3. Can XOR cancel things?
4. Is there cyclicity?
5. Is there contribution of each bit?
6. Can greedy highest-bit work?
7. Can prefix bit counts optimize?

---

## Classic Example

Input:

```text
[1,3,5,7]
```

Binary representation:

| Number | Bit3 | Bit2 | Bit1 | Bit0 |
|---|---|---|---|---|
| 1 | 0 | 0 | 0 | 1 |
| 3 | 0 | 0 | 1 | 1 |
| 5 | 0 | 1 | 0 | 1 |
| 7 | 0 | 1 | 1 | 1 |

---

## Brute Force Code

```cpp
for(int i=0;i<n;i++) {
    for(int j=i+1;j<n;j++) {
        ans += (arr[i] ^ arr[j]);
    }
}
```

Complexity:

```text
O(N^2)
```

---

## Optimal Observation

Bits are independent.

This is one of the MOST important ideas in CP.

Instead of processing numbers:
- process EACH BIT separately.

---

## Dry Run

Suppose:

```text
arr = [1,3,5]
```

Bit table:

| Number | Bit2 | Bit1 | Bit0 |
|---|---|---|---|
| 1 | 0 | 0 | 1 |
| 3 | 0 | 1 | 1 |
| 5 | 1 | 0 | 1 |

Bit0:
- ones = 3
- zeros = 0

Contribution:

```text
3 × 0 × 1 = 0
```

Bit1:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 2 = 4
```

Bit2:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 4 = 8
```

Final:

```text
12
```

---

## CP Pattern Recognition

| Pattern | Technique |
|---|---|
| Pair XOR sum | Contribution |
| Count bits 1..N | Cyclicity |
| Maximum AND | Highest-bit greedy |
| Subset problems | Bitmask |
| State compression | Bitmask DP |
| Maximum XOR | Trie/XOR basis |

---

## FAANG Interview Thinking

Interviewers usually expect:

### Level 1
Brute force.

### Level 2
Observation.

### Level 3
Bit independence.

### Level 4
Mathematical reduction.

### Level 5
Reusable template.

---

## Common Mistakes

### Mistake 1

Using:

```cpp
1 << 31
```

inside int.

Can overflow.

Use:

```cpp
1LL << 31
```

---

### Mistake 2

Confusing:

```cpp
x & (1<<i)
```

with:

```cpp
x && (1<<i)
```

---

### Mistake 3

Forgetting operator precedence.

Always use brackets.

---

## Recommended Problems

### Easy
- Single Number
- Power of Two
- Counting Bits
- Missing Number

### Medium
- Subsets
- Maximum XOR
- Sum XOR Pairs
- Bitmask DP intro

### Hard
- SOS DP
- XOR Basis
- Gaussian XOR
- Steiner Tree DP

---

## Reusable Template

```cpp
for(int bit=0; bit<32; bit++) {

    int cnt = 0;

    for(int x : arr) {
        if(x & (1<<bit))
            cnt++;
    }
}
```

---

## Complexity Analysis

| Technique | Complexity |
|---|---|
| Brute force pairs | O(N²) |
| Contribution | O(32×N) |
| Subset generation | O(2^N × N) |
| Prefix bit counts | O(32×N) |
| Trie XOR | O(32×N) |

---

## Key Takeaway

Whenever you see:
- XOR
- subsets
- masks
- state compression
- maximum/minimum bit objective

Immediately think:

```text
BITS
```



---


# 15. Detailed Bit Manipulation Topic 15


## Core Idea

This section explains advanced bit manipulation concepts in a detailed CP + FAANG style.

### Why This Topic Matters

This pattern appears heavily in:
- LeetCode Medium/Hard
- Codeforces
- AtCoder
- Google interviews
- Meta interviews
- Trading systems
- Performance engineering

---

## Mental Model

Bits should always be visualized column-wise.

Example:

| Bit Position | 5 | 4 | 3 | 2 | 1 | 0 |
|---|---|---|---|---|---|---|
| Power of 2 | 32 | 16 | 8 | 4 | 2 | 1 |
| Bit Value | 1 | 0 | 1 | 1 | 0 | 1 |
| Contribution | 32 | 0 | 8 | 4 | 0 | 1 |

Total:

```text
45
```

---

## Important Observation

Higher bits dominate lower bits.

Example:

```text
2^5 = 32
```

This is larger than:

```text
16 + 8 + 4 + 2 + 1
```

Hence greedy solutions often iterate:

```text
31 → 0
```

---

## Brute Force Thinking

When starting any bit problem:

### Ask:

1. Can I iterate all subsets?
2. Can bits be processed independently?
3. Can XOR cancel things?
4. Is there cyclicity?
5. Is there contribution of each bit?
6. Can greedy highest-bit work?
7. Can prefix bit counts optimize?

---

## Classic Example

Input:

```text
[1,3,5,7]
```

Binary representation:

| Number | Bit3 | Bit2 | Bit1 | Bit0 |
|---|---|---|---|---|
| 1 | 0 | 0 | 0 | 1 |
| 3 | 0 | 0 | 1 | 1 |
| 5 | 0 | 1 | 0 | 1 |
| 7 | 0 | 1 | 1 | 1 |

---

## Brute Force Code

```cpp
for(int i=0;i<n;i++) {
    for(int j=i+1;j<n;j++) {
        ans += (arr[i] ^ arr[j]);
    }
}
```

Complexity:

```text
O(N^2)
```

---

## Optimal Observation

Bits are independent.

This is one of the MOST important ideas in CP.

Instead of processing numbers:
- process EACH BIT separately.

---

## Dry Run

Suppose:

```text
arr = [1,3,5]
```

Bit table:

| Number | Bit2 | Bit1 | Bit0 |
|---|---|---|---|
| 1 | 0 | 0 | 1 |
| 3 | 0 | 1 | 1 |
| 5 | 1 | 0 | 1 |

Bit0:
- ones = 3
- zeros = 0

Contribution:

```text
3 × 0 × 1 = 0
```

Bit1:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 2 = 4
```

Bit2:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 4 = 8
```

Final:

```text
12
```

---

## CP Pattern Recognition

| Pattern | Technique |
|---|---|
| Pair XOR sum | Contribution |
| Count bits 1..N | Cyclicity |
| Maximum AND | Highest-bit greedy |
| Subset problems | Bitmask |
| State compression | Bitmask DP |
| Maximum XOR | Trie/XOR basis |

---

## FAANG Interview Thinking

Interviewers usually expect:

### Level 1
Brute force.

### Level 2
Observation.

### Level 3
Bit independence.

### Level 4
Mathematical reduction.

### Level 5
Reusable template.

---

## Common Mistakes

### Mistake 1

Using:

```cpp
1 << 31
```

inside int.

Can overflow.

Use:

```cpp
1LL << 31
```

---

### Mistake 2

Confusing:

```cpp
x & (1<<i)
```

with:

```cpp
x && (1<<i)
```

---

### Mistake 3

Forgetting operator precedence.

Always use brackets.

---

## Recommended Problems

### Easy
- Single Number
- Power of Two
- Counting Bits
- Missing Number

### Medium
- Subsets
- Maximum XOR
- Sum XOR Pairs
- Bitmask DP intro

### Hard
- SOS DP
- XOR Basis
- Gaussian XOR
- Steiner Tree DP

---

## Reusable Template

```cpp
for(int bit=0; bit<32; bit++) {

    int cnt = 0;

    for(int x : arr) {
        if(x & (1<<bit))
            cnt++;
    }
}
```

---

## Complexity Analysis

| Technique | Complexity |
|---|---|
| Brute force pairs | O(N²) |
| Contribution | O(32×N) |
| Subset generation | O(2^N × N) |
| Prefix bit counts | O(32×N) |
| Trie XOR | O(32×N) |

---

## Key Takeaway

Whenever you see:
- XOR
- subsets
- masks
- state compression
- maximum/minimum bit objective

Immediately think:

```text
BITS
```



---


# 16. Detailed Bit Manipulation Topic 16


## Core Idea

This section explains advanced bit manipulation concepts in a detailed CP + FAANG style.

### Why This Topic Matters

This pattern appears heavily in:
- LeetCode Medium/Hard
- Codeforces
- AtCoder
- Google interviews
- Meta interviews
- Trading systems
- Performance engineering

---

## Mental Model

Bits should always be visualized column-wise.

Example:

| Bit Position | 5 | 4 | 3 | 2 | 1 | 0 |
|---|---|---|---|---|---|---|
| Power of 2 | 32 | 16 | 8 | 4 | 2 | 1 |
| Bit Value | 1 | 0 | 1 | 1 | 0 | 1 |
| Contribution | 32 | 0 | 8 | 4 | 0 | 1 |

Total:

```text
45
```

---

## Important Observation

Higher bits dominate lower bits.

Example:

```text
2^5 = 32
```

This is larger than:

```text
16 + 8 + 4 + 2 + 1
```

Hence greedy solutions often iterate:

```text
31 → 0
```

---

## Brute Force Thinking

When starting any bit problem:

### Ask:

1. Can I iterate all subsets?
2. Can bits be processed independently?
3. Can XOR cancel things?
4. Is there cyclicity?
5. Is there contribution of each bit?
6. Can greedy highest-bit work?
7. Can prefix bit counts optimize?

---

## Classic Example

Input:

```text
[1,3,5,7]
```

Binary representation:

| Number | Bit3 | Bit2 | Bit1 | Bit0 |
|---|---|---|---|---|
| 1 | 0 | 0 | 0 | 1 |
| 3 | 0 | 0 | 1 | 1 |
| 5 | 0 | 1 | 0 | 1 |
| 7 | 0 | 1 | 1 | 1 |

---

## Brute Force Code

```cpp
for(int i=0;i<n;i++) {
    for(int j=i+1;j<n;j++) {
        ans += (arr[i] ^ arr[j]);
    }
}
```

Complexity:

```text
O(N^2)
```

---

## Optimal Observation

Bits are independent.

This is one of the MOST important ideas in CP.

Instead of processing numbers:
- process EACH BIT separately.

---

## Dry Run

Suppose:

```text
arr = [1,3,5]
```

Bit table:

| Number | Bit2 | Bit1 | Bit0 |
|---|---|---|---|
| 1 | 0 | 0 | 1 |
| 3 | 0 | 1 | 1 |
| 5 | 1 | 0 | 1 |

Bit0:
- ones = 3
- zeros = 0

Contribution:

```text
3 × 0 × 1 = 0
```

Bit1:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 2 = 4
```

Bit2:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 4 = 8
```

Final:

```text
12
```

---

## CP Pattern Recognition

| Pattern | Technique |
|---|---|
| Pair XOR sum | Contribution |
| Count bits 1..N | Cyclicity |
| Maximum AND | Highest-bit greedy |
| Subset problems | Bitmask |
| State compression | Bitmask DP |
| Maximum XOR | Trie/XOR basis |

---

## FAANG Interview Thinking

Interviewers usually expect:

### Level 1
Brute force.

### Level 2
Observation.

### Level 3
Bit independence.

### Level 4
Mathematical reduction.

### Level 5
Reusable template.

---

## Common Mistakes

### Mistake 1

Using:

```cpp
1 << 31
```

inside int.

Can overflow.

Use:

```cpp
1LL << 31
```

---

### Mistake 2

Confusing:

```cpp
x & (1<<i)
```

with:

```cpp
x && (1<<i)
```

---

### Mistake 3

Forgetting operator precedence.

Always use brackets.

---

## Recommended Problems

### Easy
- Single Number
- Power of Two
- Counting Bits
- Missing Number

### Medium
- Subsets
- Maximum XOR
- Sum XOR Pairs
- Bitmask DP intro

### Hard
- SOS DP
- XOR Basis
- Gaussian XOR
- Steiner Tree DP

---

## Reusable Template

```cpp
for(int bit=0; bit<32; bit++) {

    int cnt = 0;

    for(int x : arr) {
        if(x & (1<<bit))
            cnt++;
    }
}
```

---

## Complexity Analysis

| Technique | Complexity |
|---|---|
| Brute force pairs | O(N²) |
| Contribution | O(32×N) |
| Subset generation | O(2^N × N) |
| Prefix bit counts | O(32×N) |
| Trie XOR | O(32×N) |

---

## Key Takeaway

Whenever you see:
- XOR
- subsets
- masks
- state compression
- maximum/minimum bit objective

Immediately think:

```text
BITS
```



---


# 17. Detailed Bit Manipulation Topic 17


## Core Idea

This section explains advanced bit manipulation concepts in a detailed CP + FAANG style.

### Why This Topic Matters

This pattern appears heavily in:
- LeetCode Medium/Hard
- Codeforces
- AtCoder
- Google interviews
- Meta interviews
- Trading systems
- Performance engineering

---

## Mental Model

Bits should always be visualized column-wise.

Example:

| Bit Position | 5 | 4 | 3 | 2 | 1 | 0 |
|---|---|---|---|---|---|---|
| Power of 2 | 32 | 16 | 8 | 4 | 2 | 1 |
| Bit Value | 1 | 0 | 1 | 1 | 0 | 1 |
| Contribution | 32 | 0 | 8 | 4 | 0 | 1 |

Total:

```text
45
```

---

## Important Observation

Higher bits dominate lower bits.

Example:

```text
2^5 = 32
```

This is larger than:

```text
16 + 8 + 4 + 2 + 1
```

Hence greedy solutions often iterate:

```text
31 → 0
```

---

## Brute Force Thinking

When starting any bit problem:

### Ask:

1. Can I iterate all subsets?
2. Can bits be processed independently?
3. Can XOR cancel things?
4. Is there cyclicity?
5. Is there contribution of each bit?
6. Can greedy highest-bit work?
7. Can prefix bit counts optimize?

---

## Classic Example

Input:

```text
[1,3,5,7]
```

Binary representation:

| Number | Bit3 | Bit2 | Bit1 | Bit0 |
|---|---|---|---|---|
| 1 | 0 | 0 | 0 | 1 |
| 3 | 0 | 0 | 1 | 1 |
| 5 | 0 | 1 | 0 | 1 |
| 7 | 0 | 1 | 1 | 1 |

---

## Brute Force Code

```cpp
for(int i=0;i<n;i++) {
    for(int j=i+1;j<n;j++) {
        ans += (arr[i] ^ arr[j]);
    }
}
```

Complexity:

```text
O(N^2)
```

---

## Optimal Observation

Bits are independent.

This is one of the MOST important ideas in CP.

Instead of processing numbers:
- process EACH BIT separately.

---

## Dry Run

Suppose:

```text
arr = [1,3,5]
```

Bit table:

| Number | Bit2 | Bit1 | Bit0 |
|---|---|---|---|
| 1 | 0 | 0 | 1 |
| 3 | 0 | 1 | 1 |
| 5 | 1 | 0 | 1 |

Bit0:
- ones = 3
- zeros = 0

Contribution:

```text
3 × 0 × 1 = 0
```

Bit1:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 2 = 4
```

Bit2:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 4 = 8
```

Final:

```text
12
```

---

## CP Pattern Recognition

| Pattern | Technique |
|---|---|
| Pair XOR sum | Contribution |
| Count bits 1..N | Cyclicity |
| Maximum AND | Highest-bit greedy |
| Subset problems | Bitmask |
| State compression | Bitmask DP |
| Maximum XOR | Trie/XOR basis |

---

## FAANG Interview Thinking

Interviewers usually expect:

### Level 1
Brute force.

### Level 2
Observation.

### Level 3
Bit independence.

### Level 4
Mathematical reduction.

### Level 5
Reusable template.

---

## Common Mistakes

### Mistake 1

Using:

```cpp
1 << 31
```

inside int.

Can overflow.

Use:

```cpp
1LL << 31
```

---

### Mistake 2

Confusing:

```cpp
x & (1<<i)
```

with:

```cpp
x && (1<<i)
```

---

### Mistake 3

Forgetting operator precedence.

Always use brackets.

---

## Recommended Problems

### Easy
- Single Number
- Power of Two
- Counting Bits
- Missing Number

### Medium
- Subsets
- Maximum XOR
- Sum XOR Pairs
- Bitmask DP intro

### Hard
- SOS DP
- XOR Basis
- Gaussian XOR
- Steiner Tree DP

---

## Reusable Template

```cpp
for(int bit=0; bit<32; bit++) {

    int cnt = 0;

    for(int x : arr) {
        if(x & (1<<bit))
            cnt++;
    }
}
```

---

## Complexity Analysis

| Technique | Complexity |
|---|---|
| Brute force pairs | O(N²) |
| Contribution | O(32×N) |
| Subset generation | O(2^N × N) |
| Prefix bit counts | O(32×N) |
| Trie XOR | O(32×N) |

---

## Key Takeaway

Whenever you see:
- XOR
- subsets
- masks
- state compression
- maximum/minimum bit objective

Immediately think:

```text
BITS
```



---


# 18. Detailed Bit Manipulation Topic 18


## Core Idea

This section explains advanced bit manipulation concepts in a detailed CP + FAANG style.

### Why This Topic Matters

This pattern appears heavily in:
- LeetCode Medium/Hard
- Codeforces
- AtCoder
- Google interviews
- Meta interviews
- Trading systems
- Performance engineering

---

## Mental Model

Bits should always be visualized column-wise.

Example:

| Bit Position | 5 | 4 | 3 | 2 | 1 | 0 |
|---|---|---|---|---|---|---|
| Power of 2 | 32 | 16 | 8 | 4 | 2 | 1 |
| Bit Value | 1 | 0 | 1 | 1 | 0 | 1 |
| Contribution | 32 | 0 | 8 | 4 | 0 | 1 |

Total:

```text
45
```

---

## Important Observation

Higher bits dominate lower bits.

Example:

```text
2^5 = 32
```

This is larger than:

```text
16 + 8 + 4 + 2 + 1
```

Hence greedy solutions often iterate:

```text
31 → 0
```

---

## Brute Force Thinking

When starting any bit problem:

### Ask:

1. Can I iterate all subsets?
2. Can bits be processed independently?
3. Can XOR cancel things?
4. Is there cyclicity?
5. Is there contribution of each bit?
6. Can greedy highest-bit work?
7. Can prefix bit counts optimize?

---

## Classic Example

Input:

```text
[1,3,5,7]
```

Binary representation:

| Number | Bit3 | Bit2 | Bit1 | Bit0 |
|---|---|---|---|---|
| 1 | 0 | 0 | 0 | 1 |
| 3 | 0 | 0 | 1 | 1 |
| 5 | 0 | 1 | 0 | 1 |
| 7 | 0 | 1 | 1 | 1 |

---

## Brute Force Code

```cpp
for(int i=0;i<n;i++) {
    for(int j=i+1;j<n;j++) {
        ans += (arr[i] ^ arr[j]);
    }
}
```

Complexity:

```text
O(N^2)
```

---

## Optimal Observation

Bits are independent.

This is one of the MOST important ideas in CP.

Instead of processing numbers:
- process EACH BIT separately.

---

## Dry Run

Suppose:

```text
arr = [1,3,5]
```

Bit table:

| Number | Bit2 | Bit1 | Bit0 |
|---|---|---|---|
| 1 | 0 | 0 | 1 |
| 3 | 0 | 1 | 1 |
| 5 | 1 | 0 | 1 |

Bit0:
- ones = 3
- zeros = 0

Contribution:

```text
3 × 0 × 1 = 0
```

Bit1:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 2 = 4
```

Bit2:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 4 = 8
```

Final:

```text
12
```

---

## CP Pattern Recognition

| Pattern | Technique |
|---|---|
| Pair XOR sum | Contribution |
| Count bits 1..N | Cyclicity |
| Maximum AND | Highest-bit greedy |
| Subset problems | Bitmask |
| State compression | Bitmask DP |
| Maximum XOR | Trie/XOR basis |

---

## FAANG Interview Thinking

Interviewers usually expect:

### Level 1
Brute force.

### Level 2
Observation.

### Level 3
Bit independence.

### Level 4
Mathematical reduction.

### Level 5
Reusable template.

---

## Common Mistakes

### Mistake 1

Using:

```cpp
1 << 31
```

inside int.

Can overflow.

Use:

```cpp
1LL << 31
```

---

### Mistake 2

Confusing:

```cpp
x & (1<<i)
```

with:

```cpp
x && (1<<i)
```

---

### Mistake 3

Forgetting operator precedence.

Always use brackets.

---

## Recommended Problems

### Easy
- Single Number
- Power of Two
- Counting Bits
- Missing Number

### Medium
- Subsets
- Maximum XOR
- Sum XOR Pairs
- Bitmask DP intro

### Hard
- SOS DP
- XOR Basis
- Gaussian XOR
- Steiner Tree DP

---

## Reusable Template

```cpp
for(int bit=0; bit<32; bit++) {

    int cnt = 0;

    for(int x : arr) {
        if(x & (1<<bit))
            cnt++;
    }
}
```

---

## Complexity Analysis

| Technique | Complexity |
|---|---|
| Brute force pairs | O(N²) |
| Contribution | O(32×N) |
| Subset generation | O(2^N × N) |
| Prefix bit counts | O(32×N) |
| Trie XOR | O(32×N) |

---

## Key Takeaway

Whenever you see:
- XOR
- subsets
- masks
- state compression
- maximum/minimum bit objective

Immediately think:

```text
BITS
```



---


# 19. Detailed Bit Manipulation Topic 19


## Core Idea

This section explains advanced bit manipulation concepts in a detailed CP + FAANG style.

### Why This Topic Matters

This pattern appears heavily in:
- LeetCode Medium/Hard
- Codeforces
- AtCoder
- Google interviews
- Meta interviews
- Trading systems
- Performance engineering

---

## Mental Model

Bits should always be visualized column-wise.

Example:

| Bit Position | 5 | 4 | 3 | 2 | 1 | 0 |
|---|---|---|---|---|---|---|
| Power of 2 | 32 | 16 | 8 | 4 | 2 | 1 |
| Bit Value | 1 | 0 | 1 | 1 | 0 | 1 |
| Contribution | 32 | 0 | 8 | 4 | 0 | 1 |

Total:

```text
45
```

---

## Important Observation

Higher bits dominate lower bits.

Example:

```text
2^5 = 32
```

This is larger than:

```text
16 + 8 + 4 + 2 + 1
```

Hence greedy solutions often iterate:

```text
31 → 0
```

---

## Brute Force Thinking

When starting any bit problem:

### Ask:

1. Can I iterate all subsets?
2. Can bits be processed independently?
3. Can XOR cancel things?
4. Is there cyclicity?
5. Is there contribution of each bit?
6. Can greedy highest-bit work?
7. Can prefix bit counts optimize?

---

## Classic Example

Input:

```text
[1,3,5,7]
```

Binary representation:

| Number | Bit3 | Bit2 | Bit1 | Bit0 |
|---|---|---|---|---|
| 1 | 0 | 0 | 0 | 1 |
| 3 | 0 | 0 | 1 | 1 |
| 5 | 0 | 1 | 0 | 1 |
| 7 | 0 | 1 | 1 | 1 |

---

## Brute Force Code

```cpp
for(int i=0;i<n;i++) {
    for(int j=i+1;j<n;j++) {
        ans += (arr[i] ^ arr[j]);
    }
}
```

Complexity:

```text
O(N^2)
```

---

## Optimal Observation

Bits are independent.

This is one of the MOST important ideas in CP.

Instead of processing numbers:
- process EACH BIT separately.

---

## Dry Run

Suppose:

```text
arr = [1,3,5]
```

Bit table:

| Number | Bit2 | Bit1 | Bit0 |
|---|---|---|---|
| 1 | 0 | 0 | 1 |
| 3 | 0 | 1 | 1 |
| 5 | 1 | 0 | 1 |

Bit0:
- ones = 3
- zeros = 0

Contribution:

```text
3 × 0 × 1 = 0
```

Bit1:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 2 = 4
```

Bit2:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 4 = 8
```

Final:

```text
12
```

---

## CP Pattern Recognition

| Pattern | Technique |
|---|---|
| Pair XOR sum | Contribution |
| Count bits 1..N | Cyclicity |
| Maximum AND | Highest-bit greedy |
| Subset problems | Bitmask |
| State compression | Bitmask DP |
| Maximum XOR | Trie/XOR basis |

---

## FAANG Interview Thinking

Interviewers usually expect:

### Level 1
Brute force.

### Level 2
Observation.

### Level 3
Bit independence.

### Level 4
Mathematical reduction.

### Level 5
Reusable template.

---

## Common Mistakes

### Mistake 1

Using:

```cpp
1 << 31
```

inside int.

Can overflow.

Use:

```cpp
1LL << 31
```

---

### Mistake 2

Confusing:

```cpp
x & (1<<i)
```

with:

```cpp
x && (1<<i)
```

---

### Mistake 3

Forgetting operator precedence.

Always use brackets.

---

## Recommended Problems

### Easy
- Single Number
- Power of Two
- Counting Bits
- Missing Number

### Medium
- Subsets
- Maximum XOR
- Sum XOR Pairs
- Bitmask DP intro

### Hard
- SOS DP
- XOR Basis
- Gaussian XOR
- Steiner Tree DP

---

## Reusable Template

```cpp
for(int bit=0; bit<32; bit++) {

    int cnt = 0;

    for(int x : arr) {
        if(x & (1<<bit))
            cnt++;
    }
}
```

---

## Complexity Analysis

| Technique | Complexity |
|---|---|
| Brute force pairs | O(N²) |
| Contribution | O(32×N) |
| Subset generation | O(2^N × N) |
| Prefix bit counts | O(32×N) |
| Trie XOR | O(32×N) |

---

## Key Takeaway

Whenever you see:
- XOR
- subsets
- masks
- state compression
- maximum/minimum bit objective

Immediately think:

```text
BITS
```



---


# 20. Detailed Bit Manipulation Topic 20


## Core Idea

This section explains advanced bit manipulation concepts in a detailed CP + FAANG style.

### Why This Topic Matters

This pattern appears heavily in:
- LeetCode Medium/Hard
- Codeforces
- AtCoder
- Google interviews
- Meta interviews
- Trading systems
- Performance engineering

---

## Mental Model

Bits should always be visualized column-wise.

Example:

| Bit Position | 5 | 4 | 3 | 2 | 1 | 0 |
|---|---|---|---|---|---|---|
| Power of 2 | 32 | 16 | 8 | 4 | 2 | 1 |
| Bit Value | 1 | 0 | 1 | 1 | 0 | 1 |
| Contribution | 32 | 0 | 8 | 4 | 0 | 1 |

Total:

```text
45
```

---

## Important Observation

Higher bits dominate lower bits.

Example:

```text
2^5 = 32
```

This is larger than:

```text
16 + 8 + 4 + 2 + 1
```

Hence greedy solutions often iterate:

```text
31 → 0
```

---

## Brute Force Thinking

When starting any bit problem:

### Ask:

1. Can I iterate all subsets?
2. Can bits be processed independently?
3. Can XOR cancel things?
4. Is there cyclicity?
5. Is there contribution of each bit?
6. Can greedy highest-bit work?
7. Can prefix bit counts optimize?

---

## Classic Example

Input:

```text
[1,3,5,7]
```

Binary representation:

| Number | Bit3 | Bit2 | Bit1 | Bit0 |
|---|---|---|---|---|
| 1 | 0 | 0 | 0 | 1 |
| 3 | 0 | 0 | 1 | 1 |
| 5 | 0 | 1 | 0 | 1 |
| 7 | 0 | 1 | 1 | 1 |

---

## Brute Force Code

```cpp
for(int i=0;i<n;i++) {
    for(int j=i+1;j<n;j++) {
        ans += (arr[i] ^ arr[j]);
    }
}
```

Complexity:

```text
O(N^2)
```

---

## Optimal Observation

Bits are independent.

This is one of the MOST important ideas in CP.

Instead of processing numbers:
- process EACH BIT separately.

---

## Dry Run

Suppose:

```text
arr = [1,3,5]
```

Bit table:

| Number | Bit2 | Bit1 | Bit0 |
|---|---|---|---|
| 1 | 0 | 0 | 1 |
| 3 | 0 | 1 | 1 |
| 5 | 1 | 0 | 1 |

Bit0:
- ones = 3
- zeros = 0

Contribution:

```text
3 × 0 × 1 = 0
```

Bit1:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 2 = 4
```

Bit2:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 4 = 8
```

Final:

```text
12
```

---

## CP Pattern Recognition

| Pattern | Technique |
|---|---|
| Pair XOR sum | Contribution |
| Count bits 1..N | Cyclicity |
| Maximum AND | Highest-bit greedy |
| Subset problems | Bitmask |
| State compression | Bitmask DP |
| Maximum XOR | Trie/XOR basis |

---

## FAANG Interview Thinking

Interviewers usually expect:

### Level 1
Brute force.

### Level 2
Observation.

### Level 3
Bit independence.

### Level 4
Mathematical reduction.

### Level 5
Reusable template.

---

## Common Mistakes

### Mistake 1

Using:

```cpp
1 << 31
```

inside int.

Can overflow.

Use:

```cpp
1LL << 31
```

---

### Mistake 2

Confusing:

```cpp
x & (1<<i)
```

with:

```cpp
x && (1<<i)
```

---

### Mistake 3

Forgetting operator precedence.

Always use brackets.

---

## Recommended Problems

### Easy
- Single Number
- Power of Two
- Counting Bits
- Missing Number

### Medium
- Subsets
- Maximum XOR
- Sum XOR Pairs
- Bitmask DP intro

### Hard
- SOS DP
- XOR Basis
- Gaussian XOR
- Steiner Tree DP

---

## Reusable Template

```cpp
for(int bit=0; bit<32; bit++) {

    int cnt = 0;

    for(int x : arr) {
        if(x & (1<<bit))
            cnt++;
    }
}
```

---

## Complexity Analysis

| Technique | Complexity |
|---|---|
| Brute force pairs | O(N²) |
| Contribution | O(32×N) |
| Subset generation | O(2^N × N) |
| Prefix bit counts | O(32×N) |
| Trie XOR | O(32×N) |

---

## Key Takeaway

Whenever you see:
- XOR
- subsets
- masks
- state compression
- maximum/minimum bit objective

Immediately think:

```text
BITS
```



---


# 21. Detailed Bit Manipulation Topic 21


## Core Idea

This section explains advanced bit manipulation concepts in a detailed CP + FAANG style.

### Why This Topic Matters

This pattern appears heavily in:
- LeetCode Medium/Hard
- Codeforces
- AtCoder
- Google interviews
- Meta interviews
- Trading systems
- Performance engineering

---

## Mental Model

Bits should always be visualized column-wise.

Example:

| Bit Position | 5 | 4 | 3 | 2 | 1 | 0 |
|---|---|---|---|---|---|---|
| Power of 2 | 32 | 16 | 8 | 4 | 2 | 1 |
| Bit Value | 1 | 0 | 1 | 1 | 0 | 1 |
| Contribution | 32 | 0 | 8 | 4 | 0 | 1 |

Total:

```text
45
```

---

## Important Observation

Higher bits dominate lower bits.

Example:

```text
2^5 = 32
```

This is larger than:

```text
16 + 8 + 4 + 2 + 1
```

Hence greedy solutions often iterate:

```text
31 → 0
```

---

## Brute Force Thinking

When starting any bit problem:

### Ask:

1. Can I iterate all subsets?
2. Can bits be processed independently?
3. Can XOR cancel things?
4. Is there cyclicity?
5. Is there contribution of each bit?
6. Can greedy highest-bit work?
7. Can prefix bit counts optimize?

---

## Classic Example

Input:

```text
[1,3,5,7]
```

Binary representation:

| Number | Bit3 | Bit2 | Bit1 | Bit0 |
|---|---|---|---|---|
| 1 | 0 | 0 | 0 | 1 |
| 3 | 0 | 0 | 1 | 1 |
| 5 | 0 | 1 | 0 | 1 |
| 7 | 0 | 1 | 1 | 1 |

---

## Brute Force Code

```cpp
for(int i=0;i<n;i++) {
    for(int j=i+1;j<n;j++) {
        ans += (arr[i] ^ arr[j]);
    }
}
```

Complexity:

```text
O(N^2)
```

---

## Optimal Observation

Bits are independent.

This is one of the MOST important ideas in CP.

Instead of processing numbers:
- process EACH BIT separately.

---

## Dry Run

Suppose:

```text
arr = [1,3,5]
```

Bit table:

| Number | Bit2 | Bit1 | Bit0 |
|---|---|---|---|
| 1 | 0 | 0 | 1 |
| 3 | 0 | 1 | 1 |
| 5 | 1 | 0 | 1 |

Bit0:
- ones = 3
- zeros = 0

Contribution:

```text
3 × 0 × 1 = 0
```

Bit1:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 2 = 4
```

Bit2:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 4 = 8
```

Final:

```text
12
```

---

## CP Pattern Recognition

| Pattern | Technique |
|---|---|
| Pair XOR sum | Contribution |
| Count bits 1..N | Cyclicity |
| Maximum AND | Highest-bit greedy |
| Subset problems | Bitmask |
| State compression | Bitmask DP |
| Maximum XOR | Trie/XOR basis |

---

## FAANG Interview Thinking

Interviewers usually expect:

### Level 1
Brute force.

### Level 2
Observation.

### Level 3
Bit independence.

### Level 4
Mathematical reduction.

### Level 5
Reusable template.

---

## Common Mistakes

### Mistake 1

Using:

```cpp
1 << 31
```

inside int.

Can overflow.

Use:

```cpp
1LL << 31
```

---

### Mistake 2

Confusing:

```cpp
x & (1<<i)
```

with:

```cpp
x && (1<<i)
```

---

### Mistake 3

Forgetting operator precedence.

Always use brackets.

---

## Recommended Problems

### Easy
- Single Number
- Power of Two
- Counting Bits
- Missing Number

### Medium
- Subsets
- Maximum XOR
- Sum XOR Pairs
- Bitmask DP intro

### Hard
- SOS DP
- XOR Basis
- Gaussian XOR
- Steiner Tree DP

---

## Reusable Template

```cpp
for(int bit=0; bit<32; bit++) {

    int cnt = 0;

    for(int x : arr) {
        if(x & (1<<bit))
            cnt++;
    }
}
```

---

## Complexity Analysis

| Technique | Complexity |
|---|---|
| Brute force pairs | O(N²) |
| Contribution | O(32×N) |
| Subset generation | O(2^N × N) |
| Prefix bit counts | O(32×N) |
| Trie XOR | O(32×N) |

---

## Key Takeaway

Whenever you see:
- XOR
- subsets
- masks
- state compression
- maximum/minimum bit objective

Immediately think:

```text
BITS
```



---


# 22. Detailed Bit Manipulation Topic 22


## Core Idea

This section explains advanced bit manipulation concepts in a detailed CP + FAANG style.

### Why This Topic Matters

This pattern appears heavily in:
- LeetCode Medium/Hard
- Codeforces
- AtCoder
- Google interviews
- Meta interviews
- Trading systems
- Performance engineering

---

## Mental Model

Bits should always be visualized column-wise.

Example:

| Bit Position | 5 | 4 | 3 | 2 | 1 | 0 |
|---|---|---|---|---|---|---|
| Power of 2 | 32 | 16 | 8 | 4 | 2 | 1 |
| Bit Value | 1 | 0 | 1 | 1 | 0 | 1 |
| Contribution | 32 | 0 | 8 | 4 | 0 | 1 |

Total:

```text
45
```

---

## Important Observation

Higher bits dominate lower bits.

Example:

```text
2^5 = 32
```

This is larger than:

```text
16 + 8 + 4 + 2 + 1
```

Hence greedy solutions often iterate:

```text
31 → 0
```

---

## Brute Force Thinking

When starting any bit problem:

### Ask:

1. Can I iterate all subsets?
2. Can bits be processed independently?
3. Can XOR cancel things?
4. Is there cyclicity?
5. Is there contribution of each bit?
6. Can greedy highest-bit work?
7. Can prefix bit counts optimize?

---

## Classic Example

Input:

```text
[1,3,5,7]
```

Binary representation:

| Number | Bit3 | Bit2 | Bit1 | Bit0 |
|---|---|---|---|---|
| 1 | 0 | 0 | 0 | 1 |
| 3 | 0 | 0 | 1 | 1 |
| 5 | 0 | 1 | 0 | 1 |
| 7 | 0 | 1 | 1 | 1 |

---

## Brute Force Code

```cpp
for(int i=0;i<n;i++) {
    for(int j=i+1;j<n;j++) {
        ans += (arr[i] ^ arr[j]);
    }
}
```

Complexity:

```text
O(N^2)
```

---

## Optimal Observation

Bits are independent.

This is one of the MOST important ideas in CP.

Instead of processing numbers:
- process EACH BIT separately.

---

## Dry Run

Suppose:

```text
arr = [1,3,5]
```

Bit table:

| Number | Bit2 | Bit1 | Bit0 |
|---|---|---|---|
| 1 | 0 | 0 | 1 |
| 3 | 0 | 1 | 1 |
| 5 | 1 | 0 | 1 |

Bit0:
- ones = 3
- zeros = 0

Contribution:

```text
3 × 0 × 1 = 0
```

Bit1:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 2 = 4
```

Bit2:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 4 = 8
```

Final:

```text
12
```

---

## CP Pattern Recognition

| Pattern | Technique |
|---|---|
| Pair XOR sum | Contribution |
| Count bits 1..N | Cyclicity |
| Maximum AND | Highest-bit greedy |
| Subset problems | Bitmask |
| State compression | Bitmask DP |
| Maximum XOR | Trie/XOR basis |

---

## FAANG Interview Thinking

Interviewers usually expect:

### Level 1
Brute force.

### Level 2
Observation.

### Level 3
Bit independence.

### Level 4
Mathematical reduction.

### Level 5
Reusable template.

---

## Common Mistakes

### Mistake 1

Using:

```cpp
1 << 31
```

inside int.

Can overflow.

Use:

```cpp
1LL << 31
```

---

### Mistake 2

Confusing:

```cpp
x & (1<<i)
```

with:

```cpp
x && (1<<i)
```

---

### Mistake 3

Forgetting operator precedence.

Always use brackets.

---

## Recommended Problems

### Easy
- Single Number
- Power of Two
- Counting Bits
- Missing Number

### Medium
- Subsets
- Maximum XOR
- Sum XOR Pairs
- Bitmask DP intro

### Hard
- SOS DP
- XOR Basis
- Gaussian XOR
- Steiner Tree DP

---

## Reusable Template

```cpp
for(int bit=0; bit<32; bit++) {

    int cnt = 0;

    for(int x : arr) {
        if(x & (1<<bit))
            cnt++;
    }
}
```

---

## Complexity Analysis

| Technique | Complexity |
|---|---|
| Brute force pairs | O(N²) |
| Contribution | O(32×N) |
| Subset generation | O(2^N × N) |
| Prefix bit counts | O(32×N) |
| Trie XOR | O(32×N) |

---

## Key Takeaway

Whenever you see:
- XOR
- subsets
- masks
- state compression
- maximum/minimum bit objective

Immediately think:

```text
BITS
```



---


# 23. Detailed Bit Manipulation Topic 23


## Core Idea

This section explains advanced bit manipulation concepts in a detailed CP + FAANG style.

### Why This Topic Matters

This pattern appears heavily in:
- LeetCode Medium/Hard
- Codeforces
- AtCoder
- Google interviews
- Meta interviews
- Trading systems
- Performance engineering

---

## Mental Model

Bits should always be visualized column-wise.

Example:

| Bit Position | 5 | 4 | 3 | 2 | 1 | 0 |
|---|---|---|---|---|---|---|
| Power of 2 | 32 | 16 | 8 | 4 | 2 | 1 |
| Bit Value | 1 | 0 | 1 | 1 | 0 | 1 |
| Contribution | 32 | 0 | 8 | 4 | 0 | 1 |

Total:

```text
45
```

---

## Important Observation

Higher bits dominate lower bits.

Example:

```text
2^5 = 32
```

This is larger than:

```text
16 + 8 + 4 + 2 + 1
```

Hence greedy solutions often iterate:

```text
31 → 0
```

---

## Brute Force Thinking

When starting any bit problem:

### Ask:

1. Can I iterate all subsets?
2. Can bits be processed independently?
3. Can XOR cancel things?
4. Is there cyclicity?
5. Is there contribution of each bit?
6. Can greedy highest-bit work?
7. Can prefix bit counts optimize?

---

## Classic Example

Input:

```text
[1,3,5,7]
```

Binary representation:

| Number | Bit3 | Bit2 | Bit1 | Bit0 |
|---|---|---|---|---|
| 1 | 0 | 0 | 0 | 1 |
| 3 | 0 | 0 | 1 | 1 |
| 5 | 0 | 1 | 0 | 1 |
| 7 | 0 | 1 | 1 | 1 |

---

## Brute Force Code

```cpp
for(int i=0;i<n;i++) {
    for(int j=i+1;j<n;j++) {
        ans += (arr[i] ^ arr[j]);
    }
}
```

Complexity:

```text
O(N^2)
```

---

## Optimal Observation

Bits are independent.

This is one of the MOST important ideas in CP.

Instead of processing numbers:
- process EACH BIT separately.

---

## Dry Run

Suppose:

```text
arr = [1,3,5]
```

Bit table:

| Number | Bit2 | Bit1 | Bit0 |
|---|---|---|---|
| 1 | 0 | 0 | 1 |
| 3 | 0 | 1 | 1 |
| 5 | 1 | 0 | 1 |

Bit0:
- ones = 3
- zeros = 0

Contribution:

```text
3 × 0 × 1 = 0
```

Bit1:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 2 = 4
```

Bit2:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 4 = 8
```

Final:

```text
12
```

---

## CP Pattern Recognition

| Pattern | Technique |
|---|---|
| Pair XOR sum | Contribution |
| Count bits 1..N | Cyclicity |
| Maximum AND | Highest-bit greedy |
| Subset problems | Bitmask |
| State compression | Bitmask DP |
| Maximum XOR | Trie/XOR basis |

---

## FAANG Interview Thinking

Interviewers usually expect:

### Level 1
Brute force.

### Level 2
Observation.

### Level 3
Bit independence.

### Level 4
Mathematical reduction.

### Level 5
Reusable template.

---

## Common Mistakes

### Mistake 1

Using:

```cpp
1 << 31
```

inside int.

Can overflow.

Use:

```cpp
1LL << 31
```

---

### Mistake 2

Confusing:

```cpp
x & (1<<i)
```

with:

```cpp
x && (1<<i)
```

---

### Mistake 3

Forgetting operator precedence.

Always use brackets.

---

## Recommended Problems

### Easy
- Single Number
- Power of Two
- Counting Bits
- Missing Number

### Medium
- Subsets
- Maximum XOR
- Sum XOR Pairs
- Bitmask DP intro

### Hard
- SOS DP
- XOR Basis
- Gaussian XOR
- Steiner Tree DP

---

## Reusable Template

```cpp
for(int bit=0; bit<32; bit++) {

    int cnt = 0;

    for(int x : arr) {
        if(x & (1<<bit))
            cnt++;
    }
}
```

---

## Complexity Analysis

| Technique | Complexity |
|---|---|
| Brute force pairs | O(N²) |
| Contribution | O(32×N) |
| Subset generation | O(2^N × N) |
| Prefix bit counts | O(32×N) |
| Trie XOR | O(32×N) |

---

## Key Takeaway

Whenever you see:
- XOR
- subsets
- masks
- state compression
- maximum/minimum bit objective

Immediately think:

```text
BITS
```



---


# 24. Detailed Bit Manipulation Topic 24


## Core Idea

This section explains advanced bit manipulation concepts in a detailed CP + FAANG style.

### Why This Topic Matters

This pattern appears heavily in:
- LeetCode Medium/Hard
- Codeforces
- AtCoder
- Google interviews
- Meta interviews
- Trading systems
- Performance engineering

---

## Mental Model

Bits should always be visualized column-wise.

Example:

| Bit Position | 5 | 4 | 3 | 2 | 1 | 0 |
|---|---|---|---|---|---|---|
| Power of 2 | 32 | 16 | 8 | 4 | 2 | 1 |
| Bit Value | 1 | 0 | 1 | 1 | 0 | 1 |
| Contribution | 32 | 0 | 8 | 4 | 0 | 1 |

Total:

```text
45
```

---

## Important Observation

Higher bits dominate lower bits.

Example:

```text
2^5 = 32
```

This is larger than:

```text
16 + 8 + 4 + 2 + 1
```

Hence greedy solutions often iterate:

```text
31 → 0
```

---

## Brute Force Thinking

When starting any bit problem:

### Ask:

1. Can I iterate all subsets?
2. Can bits be processed independently?
3. Can XOR cancel things?
4. Is there cyclicity?
5. Is there contribution of each bit?
6. Can greedy highest-bit work?
7. Can prefix bit counts optimize?

---

## Classic Example

Input:

```text
[1,3,5,7]
```

Binary representation:

| Number | Bit3 | Bit2 | Bit1 | Bit0 |
|---|---|---|---|---|
| 1 | 0 | 0 | 0 | 1 |
| 3 | 0 | 0 | 1 | 1 |
| 5 | 0 | 1 | 0 | 1 |
| 7 | 0 | 1 | 1 | 1 |

---

## Brute Force Code

```cpp
for(int i=0;i<n;i++) {
    for(int j=i+1;j<n;j++) {
        ans += (arr[i] ^ arr[j]);
    }
}
```

Complexity:

```text
O(N^2)
```

---

## Optimal Observation

Bits are independent.

This is one of the MOST important ideas in CP.

Instead of processing numbers:
- process EACH BIT separately.

---

## Dry Run

Suppose:

```text
arr = [1,3,5]
```

Bit table:

| Number | Bit2 | Bit1 | Bit0 |
|---|---|---|---|
| 1 | 0 | 0 | 1 |
| 3 | 0 | 1 | 1 |
| 5 | 1 | 0 | 1 |

Bit0:
- ones = 3
- zeros = 0

Contribution:

```text
3 × 0 × 1 = 0
```

Bit1:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 2 = 4
```

Bit2:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 4 = 8
```

Final:

```text
12
```

---

## CP Pattern Recognition

| Pattern | Technique |
|---|---|
| Pair XOR sum | Contribution |
| Count bits 1..N | Cyclicity |
| Maximum AND | Highest-bit greedy |
| Subset problems | Bitmask |
| State compression | Bitmask DP |
| Maximum XOR | Trie/XOR basis |

---

## FAANG Interview Thinking

Interviewers usually expect:

### Level 1
Brute force.

### Level 2
Observation.

### Level 3
Bit independence.

### Level 4
Mathematical reduction.

### Level 5
Reusable template.

---

## Common Mistakes

### Mistake 1

Using:

```cpp
1 << 31
```

inside int.

Can overflow.

Use:

```cpp
1LL << 31
```

---

### Mistake 2

Confusing:

```cpp
x & (1<<i)
```

with:

```cpp
x && (1<<i)
```

---

### Mistake 3

Forgetting operator precedence.

Always use brackets.

---

## Recommended Problems

### Easy
- Single Number
- Power of Two
- Counting Bits
- Missing Number

### Medium
- Subsets
- Maximum XOR
- Sum XOR Pairs
- Bitmask DP intro

### Hard
- SOS DP
- XOR Basis
- Gaussian XOR
- Steiner Tree DP

---

## Reusable Template

```cpp
for(int bit=0; bit<32; bit++) {

    int cnt = 0;

    for(int x : arr) {
        if(x & (1<<bit))
            cnt++;
    }
}
```

---

## Complexity Analysis

| Technique | Complexity |
|---|---|
| Brute force pairs | O(N²) |
| Contribution | O(32×N) |
| Subset generation | O(2^N × N) |
| Prefix bit counts | O(32×N) |
| Trie XOR | O(32×N) |

---

## Key Takeaway

Whenever you see:
- XOR
- subsets
- masks
- state compression
- maximum/minimum bit objective

Immediately think:

```text
BITS
```



---


# 25. Detailed Bit Manipulation Topic 25


## Core Idea

This section explains advanced bit manipulation concepts in a detailed CP + FAANG style.

### Why This Topic Matters

This pattern appears heavily in:
- LeetCode Medium/Hard
- Codeforces
- AtCoder
- Google interviews
- Meta interviews
- Trading systems
- Performance engineering

---

## Mental Model

Bits should always be visualized column-wise.

Example:

| Bit Position | 5 | 4 | 3 | 2 | 1 | 0 |
|---|---|---|---|---|---|---|
| Power of 2 | 32 | 16 | 8 | 4 | 2 | 1 |
| Bit Value | 1 | 0 | 1 | 1 | 0 | 1 |
| Contribution | 32 | 0 | 8 | 4 | 0 | 1 |

Total:

```text
45
```

---

## Important Observation

Higher bits dominate lower bits.

Example:

```text
2^5 = 32
```

This is larger than:

```text
16 + 8 + 4 + 2 + 1
```

Hence greedy solutions often iterate:

```text
31 → 0
```

---

## Brute Force Thinking

When starting any bit problem:

### Ask:

1. Can I iterate all subsets?
2. Can bits be processed independently?
3. Can XOR cancel things?
4. Is there cyclicity?
5. Is there contribution of each bit?
6. Can greedy highest-bit work?
7. Can prefix bit counts optimize?

---

## Classic Example

Input:

```text
[1,3,5,7]
```

Binary representation:

| Number | Bit3 | Bit2 | Bit1 | Bit0 |
|---|---|---|---|---|
| 1 | 0 | 0 | 0 | 1 |
| 3 | 0 | 0 | 1 | 1 |
| 5 | 0 | 1 | 0 | 1 |
| 7 | 0 | 1 | 1 | 1 |

---

## Brute Force Code

```cpp
for(int i=0;i<n;i++) {
    for(int j=i+1;j<n;j++) {
        ans += (arr[i] ^ arr[j]);
    }
}
```

Complexity:

```text
O(N^2)
```

---

## Optimal Observation

Bits are independent.

This is one of the MOST important ideas in CP.

Instead of processing numbers:
- process EACH BIT separately.

---

## Dry Run

Suppose:

```text
arr = [1,3,5]
```

Bit table:

| Number | Bit2 | Bit1 | Bit0 |
|---|---|---|---|
| 1 | 0 | 0 | 1 |
| 3 | 0 | 1 | 1 |
| 5 | 1 | 0 | 1 |

Bit0:
- ones = 3
- zeros = 0

Contribution:

```text
3 × 0 × 1 = 0
```

Bit1:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 2 = 4
```

Bit2:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 4 = 8
```

Final:

```text
12
```

---

## CP Pattern Recognition

| Pattern | Technique |
|---|---|
| Pair XOR sum | Contribution |
| Count bits 1..N | Cyclicity |
| Maximum AND | Highest-bit greedy |
| Subset problems | Bitmask |
| State compression | Bitmask DP |
| Maximum XOR | Trie/XOR basis |

---

## FAANG Interview Thinking

Interviewers usually expect:

### Level 1
Brute force.

### Level 2
Observation.

### Level 3
Bit independence.

### Level 4
Mathematical reduction.

### Level 5
Reusable template.

---

## Common Mistakes

### Mistake 1

Using:

```cpp
1 << 31
```

inside int.

Can overflow.

Use:

```cpp
1LL << 31
```

---

### Mistake 2

Confusing:

```cpp
x & (1<<i)
```

with:

```cpp
x && (1<<i)
```

---

### Mistake 3

Forgetting operator precedence.

Always use brackets.

---

## Recommended Problems

### Easy
- Single Number
- Power of Two
- Counting Bits
- Missing Number

### Medium
- Subsets
- Maximum XOR
- Sum XOR Pairs
- Bitmask DP intro

### Hard
- SOS DP
- XOR Basis
- Gaussian XOR
- Steiner Tree DP

---

## Reusable Template

```cpp
for(int bit=0; bit<32; bit++) {

    int cnt = 0;

    for(int x : arr) {
        if(x & (1<<bit))
            cnt++;
    }
}
```

---

## Complexity Analysis

| Technique | Complexity |
|---|---|
| Brute force pairs | O(N²) |
| Contribution | O(32×N) |
| Subset generation | O(2^N × N) |
| Prefix bit counts | O(32×N) |
| Trie XOR | O(32×N) |

---

## Key Takeaway

Whenever you see:
- XOR
- subsets
- masks
- state compression
- maximum/minimum bit objective

Immediately think:

```text
BITS
```



---


# 26. Detailed Bit Manipulation Topic 26


## Core Idea

This section explains advanced bit manipulation concepts in a detailed CP + FAANG style.

### Why This Topic Matters

This pattern appears heavily in:
- LeetCode Medium/Hard
- Codeforces
- AtCoder
- Google interviews
- Meta interviews
- Trading systems
- Performance engineering

---

## Mental Model

Bits should always be visualized column-wise.

Example:

| Bit Position | 5 | 4 | 3 | 2 | 1 | 0 |
|---|---|---|---|---|---|---|
| Power of 2 | 32 | 16 | 8 | 4 | 2 | 1 |
| Bit Value | 1 | 0 | 1 | 1 | 0 | 1 |
| Contribution | 32 | 0 | 8 | 4 | 0 | 1 |

Total:

```text
45
```

---

## Important Observation

Higher bits dominate lower bits.

Example:

```text
2^5 = 32
```

This is larger than:

```text
16 + 8 + 4 + 2 + 1
```

Hence greedy solutions often iterate:

```text
31 → 0
```

---

## Brute Force Thinking

When starting any bit problem:

### Ask:

1. Can I iterate all subsets?
2. Can bits be processed independently?
3. Can XOR cancel things?
4. Is there cyclicity?
5. Is there contribution of each bit?
6. Can greedy highest-bit work?
7. Can prefix bit counts optimize?

---

## Classic Example

Input:

```text
[1,3,5,7]
```

Binary representation:

| Number | Bit3 | Bit2 | Bit1 | Bit0 |
|---|---|---|---|---|
| 1 | 0 | 0 | 0 | 1 |
| 3 | 0 | 0 | 1 | 1 |
| 5 | 0 | 1 | 0 | 1 |
| 7 | 0 | 1 | 1 | 1 |

---

## Brute Force Code

```cpp
for(int i=0;i<n;i++) {
    for(int j=i+1;j<n;j++) {
        ans += (arr[i] ^ arr[j]);
    }
}
```

Complexity:

```text
O(N^2)
```

---

## Optimal Observation

Bits are independent.

This is one of the MOST important ideas in CP.

Instead of processing numbers:
- process EACH BIT separately.

---

## Dry Run

Suppose:

```text
arr = [1,3,5]
```

Bit table:

| Number | Bit2 | Bit1 | Bit0 |
|---|---|---|---|
| 1 | 0 | 0 | 1 |
| 3 | 0 | 1 | 1 |
| 5 | 1 | 0 | 1 |

Bit0:
- ones = 3
- zeros = 0

Contribution:

```text
3 × 0 × 1 = 0
```

Bit1:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 2 = 4
```

Bit2:
- ones = 1
- zeros = 2

Contribution:

```text
1 × 2 × 4 = 8
```

Final:

```text
12
```

---

## CP Pattern Recognition

| Pattern | Technique |
|---|---|
| Pair XOR sum | Contribution |
| Count bits 1..N | Cyclicity |
| Maximum AND | Highest-bit greedy |
| Subset problems | Bitmask |
| State compression | Bitmask DP |
| Maximum XOR | Trie/XOR basis |

---

## FAANG Interview Thinking

Interviewers usually expect:

### Level 1
Brute force.

### Level 2
Observation.

### Level 3
Bit independence.

### Level 4
Mathematical reduction.

### Level 5
Reusable template.

---

## Common Mistakes

### Mistake 1

Using:

```cpp
1 << 31
```

inside int.

Can overflow.

Use:

```cpp
1LL << 31
```

---

### Mistake 2

Confusing:

```cpp
x & (1<<i)
```

with:

```cpp
x && (1<<i)
```

---

### Mistake 3

Forgetting operator precedence.

Always use brackets.

---

## Recommended Problems

### Easy
- Single Number
- Power of Two
- Counting Bits
- Missing Number

### Medium
- Subsets
- Maximum XOR
- Sum XOR Pairs
- Bitmask DP intro

### Hard
- SOS DP
- XOR Basis
- Gaussian XOR
- Steiner Tree DP

---

## Reusable Template

```cpp
for(int bit=0; bit<32; bit++) {

    int cnt = 0;

    for(int x : arr) {
        if(x & (1<<bit))
            cnt++;
    }
}
```

---

## Complexity Analysis

| Technique | Complexity |
|---|---|
| Brute force pairs | O(N²) |
| Contribution | O(32×N) |
| Subset generation | O(2^N × N) |
| Prefix bit counts | O(32×N) |
| Trie XOR | O(32×N) |

---

## Key Takeaway

Whenever you see:
- XOR
- subsets
- masks
- state compression
- maximum/minimum bit objective

Immediately think:

```text
BITS
```



---


# 26. Mental Maps


# FINAL MENTAL MAP

```text
BIT MANIPULATION
│
├── Operators
│   ├── AND
│   ├── OR
│   ├── XOR
│   ├── NOT
│   ├── LEFT SHIFT
│   └── RIGHT SHIFT
│
├── XOR IDEAS
│   ├── Cancellation
│   ├── Pair XOR
│   ├── Prefix XOR
│   ├── XOR Basis
│   └── Trie XOR
│
├── BITMASKING
│   ├── Subsets
│   ├── State Compression
│   ├── DP
│   └── SOS DP
│
├── CONTRIBUTION TECHNIQUE
│
├── CYCLIC PATTERNS
│
├── GREEDY BIT CONSTRUCTION
│
├── PREFIX BIT COUNTS
│
└── ADVANCED CP
    ├── Gaussian XOR
    ├── Walsh Hadamard
    ├── Mobius
    └── Bitset optimization
```

---

# FINAL FAANG ROADMAP

## Beginner
- Bit operators
- Check/set bits
- XOR basics
- Powers of 2

## Intermediate
- Contribution technique
- Bitmask subsets
- Prefix XOR
- Count set bits

## Advanced
- Bitmask DP
- SOS DP
- XOR basis
- Trie XOR
- Greedy bit construction

## Elite CP
- FWHT
- Gaussian XOR
- Advanced DP optimizations


---

