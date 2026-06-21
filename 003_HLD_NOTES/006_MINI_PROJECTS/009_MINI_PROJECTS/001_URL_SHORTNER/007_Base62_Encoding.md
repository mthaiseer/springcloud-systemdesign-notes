# 007_Base62_Encoding.md
# MiniURLShortener — Base62 Encoding

> Core mental model: **Base62 encoding is not compression or encryption. It is a compact way to represent a number using 62 URL-safe characters so numeric IDs can become short, readable URL codes.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. What Base62 Is](#4-what-base62-is)
- [5. Why Base62 Fits URL Shorteners](#5-why-base62-fits-url-shorteners)
- [6. Number Systems Mental Model](#6-number-systems-mental-model)
- [7. Base10 vs Base2 vs Base16 vs Base62](#7-base10-vs-base2-vs-base16-vs-base62)
- [8. Base62 Alphabet Design](#8-base62-alphabet-design)
- [9. Encoding Mental Model](#9-encoding-mental-model)
- [10. Decoding Mental Model](#10-decoding-mental-model)
- [11. Step-by-Step Encoding Dry Runs](#11-step-by-step-encoding-dry-runs)
- [12. Step-by-Step Decoding Dry Runs](#12-step-by-step-decoding-dry-runs)
- [13. Java Implementation](#13-java-implementation)
- [14. Internal Execution Walkthrough](#14-internal-execution-walkthrough)
- [15. Base62 In URL Shortener Flow](#15-base62-in-url-shortener-flow)
- [16. ID-Based Base62 vs Random Base62](#16-id-based-base62-vs-random-base62)
- [17. Capacity Planning](#17-capacity-planning)
- [18. Security And Guessability](#18-security-and-guessability)
- [19. Edge Cases](#19-edge-cases)
- [20. Testing Strategy](#20-testing-strategy)
- [21. Production Failure Stories](#21-production-failure-stories)
- [22. Debugging Mindset](#22-debugging-mindset)
- [23. Common Mistakes](#23-common-mistakes)
- [24. Interview-Ready Explanation](#24-interview-ready-explanation)
- [25. Senior Engineer Checklist](#25-senior-engineer-checklist)
- [26. One-Page Cheat Sheet](#26-one-page-cheat-sheet)
- [27. One Picture To Remember](#27-one-picture-to-remember)

---

## 1. Why This Exists

A URL shortener needs short codes like:

```text
aB91xZ
k9Lm2Q
java17
```

Instead of long identifiers like:

```text
1000000000001
982374982374
```

A database may naturally produce numeric IDs:

```text
id = 1
id = 2
id = 3
id = 1000000
```

But public URLs should be compact:

```text
https://mini.ly/1
https://mini.ly/2
https://mini.ly/G8
https://mini.ly/4c92
```

Base62 helps convert a large number into a shorter string.

Example idea:

```text
Number: 125
Base62: 21
```

This is similar to how decimal, binary, and hexadecimal represent the same value using different alphabets.

Important:

```text
Base62 does not make data secret.
Base62 does not compress the original long URL.
Base62 does not prevent guessing if IDs are sequential.
```

It only changes representation:

```text
number -> compact string
```

For URL shorteners, this is useful because:

```text
small code
URL-safe characters
human-readable
large capacity
easy to store and index
```

---

## 2. The One Core Mental Model

Base62 is:

```text
NUMBER REPRESENTATION USING 62 SYMBOLS
```

Decimal uses 10 symbols:

```text
0 1 2 3 4 5 6 7 8 9
```

Binary uses 2 symbols:

```text
0 1
```

Hexadecimal uses 16 symbols:

```text
0 1 2 3 4 5 6 7 8 9 A B C D E F
```

Base62 uses 62 symbols:

```text
0-9
a-z
A-Z
```

One-line memory:

```text
Base62 is like decimal, but each digit has 62 possible values instead of 10.
```

ASCII:

```text
Same value, different representations:

Decimal/Base10:     125
Binary/Base2:       1111101
Hex/Base16:         7D
Base62:             21   depending on alphabet
```

Why shorter?

Because each Base62 digit carries more information than each decimal digit.

```text
Base10 digit: 10 choices
Base62 digit: 62 choices
```

More choices per character means fewer characters needed.

---

## 3. Problem Statement

Implement and understand Base62 encoding for URL shortener short codes.

We need to support:

```text
1. Convert positive numeric ID to Base62 string.
2. Convert Base62 string back to numeric ID.
3. Understand why repeated division works.
4. Understand why decoding uses multiplication.
5. Handle zero.
6. Reject invalid characters.
7. Avoid overflow surprises.
8. Understand capacity limits.
9. Understand how Base62 differs from random code generation.
```

Out of scope for this chapter:

```text
1. Full distributed ID generation.
2. Snowflake ID design.
3. Hash-based code generation.
4. Redis caching.
5. Sharding.
6. Security token generation.
```

This chapter focuses exactly on Base62 as a mental model and utility.

---

## 4. What Base62 Is

Base62 represents a number using 62 characters.

A common alphabet:

```text
0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ
```

Index mapping:

```text
0  -> '0'
1  -> '1'
2  -> '2'
...
9  -> '9'
10 -> 'a'
11 -> 'b'
...
35 -> 'z'
36 -> 'A'
37 -> 'B'
...
61 -> 'Z'
```

ASCII table:

```text
+-------------+-------------+
| Value       | Character   |
+-------------+-------------+
| 0           | 0           |
| 1           | 1           |
| 9           | 9           |
| 10          | a           |
| 11          | b           |
| 35          | z           |
| 36          | A           |
| 61          | Z           |
+-------------+-------------+
```

Base62 code is a sequence of these characters.

Example:

```text
21
```

Means:

```text
2 * 62 + 1 = 125
```

So:

```text
125 decimal = 21 base62
```

depending on alphabet ordering.

---

## 5. Why Base62 Fits URL Shorteners

A URL path should avoid problematic characters.

Problematic characters:

```text
/
?
#
&
%
=
+
space
```

They have special meaning in URLs.

Base62 characters are safe:

```text
0-9
a-z
A-Z
```

Example:

```text
https://mini.ly/aB91xZ
```

No escaping needed.

Base62 advantages:

```text
1. Compact.
2. URL-safe.
3. Human-readable enough.
4. Easy to index.
5. Easy to generate from numeric IDs.
6. Large capacity with few characters.
```

Base62 capacity:

```text
1 char  = 62
2 chars = 62^2 = 3,844
3 chars = 62^3 = 238,328
4 chars = 62^4 = 14,776,336
5 chars = 62^5 = 916,132,832
6 chars = 62^6 = 56,800,235,584
7 chars = 62^7 = 3,521,614,606,208
8 chars = 62^8 = 218,340,105,584,896
```

This means:

```text
7 Base62 characters can represent over 3.5 trillion values.
```

That is enough for many URL shortener systems.

---

## 6. Number Systems Mental Model

A number system has:

```text
base
digits
place values
```

Decimal has base 10.

Example:

```text
125
```

Means:

```text
1 * 10^2 + 2 * 10^1 + 5 * 10^0
= 100 + 20 + 5
= 125
```

Binary has base 2.

Example:

```text
101
```

Means:

```text
1 * 2^2 + 0 * 2^1 + 1 * 2^0
= 4 + 0 + 1
= 5
```

Base62 works the same.

Example:

```text
21
```

Means:

```text
2 * 62^1 + 1 * 62^0
= 124 + 1
= 125
```

ASCII place-value model:

```text
Decimal 125:

[1] [2] [5]
 |   |   |
100 10   1


Base62 21:

[2] [1]
 |   |
62  1
```

Key insight:

```text
Changing base changes the value of each position.
```

---

## 7. Base10 vs Base2 vs Base16 vs Base62

Same number:

```text
125
```

Representations:

```text
Base10: 125
Base2 : 1111101
Base16: 7D
Base62: 21
```

Why Base62 is shorter:

```text
Base2 uses only 2 symbols, so it needs many digits.
Base10 uses 10 symbols.
Base16 uses 16 symbols.
Base62 uses 62 symbols, so it needs fewer digits.
```

ASCII:

```text
Number: 125

Base2:
1 1 1 1 1 0 1
7 chars

Base10:
1 2 5
3 chars

Base16:
7 D
2 chars

Base62:
2 1
2 chars
```

But shorter does not mean encrypted.

Anyone who knows the alphabet can decode it.

```text
Base62 is representation, not secrecy.
```

---

## 8. Base62 Alphabet Design

Alphabet choice matters.

Common alphabet:

```text
0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ
```

Alternative:

```text
0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz
```

Another:

```text
abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789
```

Important rule:

```text
Use one alphabet consistently forever.
```

If you encode with one alphabet and decode with another, results break.

Example:

```text
Alphabet A:
10 -> a

Alphabet B:
10 -> A
```

Same number produces different code.

Production warning:

```text
Changing alphabet after launch breaks existing short URLs unless migration is handled.
```

ASCII:

```text
Number 10

Alphabet 1:
0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ
          |
          v
          a

Alphabet 2:
0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz
          |
          v
          A
```

Recommendation for this project:

```text
0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ
```

Why?

```text
simple
common
easy to reason about
digits first
lowercase before uppercase
```

---

## 9. Encoding Mental Model

Encoding means:

```text
decimal number -> Base62 string
```

Core algorithm:

```text
while number > 0:
    remainder = number % 62
    character = alphabet[remainder]
    append character
    number = number / 62

reverse result
```

Why reverse?

Because division gives least significant digit first.

Example decimal 125:

```text
125 / 62 = 2 remainder 1
2 / 62   = 0 remainder 2
```

Remainders collected:

```text
1, 2
```

These are from right to left.

Reverse:

```text
2, 1
```

Code:

```text
21
```

ASCII:

```text
Encoding 125

125 ÷ 62 = 2 remainder 1  -> rightmost digit
  2 ÷ 62 = 0 remainder 2  -> leftmost digit

Collected: 1 2
Reverse:   2 1
Base62:    21
```

Mental model:

```text
Modulo gives current digit.
Division moves to the next place.
```

This is the same idea used when converting decimal to binary.

---

## 10. Decoding Mental Model

Decoding means:

```text
Base62 string -> decimal number
```

Core algorithm:

```text
value = 0

for each character:
    digit = index of character in alphabet
    value = value * 62 + digit
```

Example:

```text
Base62: 21
```

Steps:

```text
value = 0

char '2':
value = 0 * 62 + 2 = 2

char '1':
value = 2 * 62 + 1 = 125
```

ASCII:

```text
Decoding "21"

Start value = 0

[2]
value = 0 * 62 + 2
value = 2

[1]
value = 2 * 62 + 1
value = 125
```

Why multiplication?

Because moving left to right shifts previous digits one place higher.

Decimal example:

```text
"125"

value = 0
value = 0 * 10 + 1 = 1
value = 1 * 10 + 2 = 12
value = 12 * 10 + 5 = 125
```

Base62 is identical, but base is 62.

---

## 11. Step-by-Step Encoding Dry Runs

### Dry Run 1: Encode 0

Special case:

```text
0 -> "0"
```

Why special?

The loop:

```text
while number > 0
```

does not run for zero.

So we explicitly return:

```text
"0"
```

---

### Dry Run 2: Encode 1

```text
1 / 62 = 0 remainder 1
```

Character:

```text
alphabet[1] = '1'
```

Result:

```text
1 -> "1"
```

---

### Dry Run 3: Encode 61

```text
61 / 62 = 0 remainder 61
```

Character:

```text
alphabet[61] = 'Z'
```

Result:

```text
61 -> "Z"
```

---

### Dry Run 4: Encode 62

```text
62 / 62 = 1 remainder 0
1 / 62  = 0 remainder 1
```

Collected:

```text
0, 1
```

Reverse:

```text
1, 0
```

Result:

```text
62 -> "10"
```

This is exactly like decimal:

```text
9 + 1 = 10
```

In Base62:

```text
Z + 1 = 10
```

---

### Dry Run 5: Encode 125

```text
125 / 62 = 2 remainder 1
2 / 62   = 0 remainder 2
```

Collected:

```text
1, 2
```

Reverse:

```text
2, 1
```

Result:

```text
125 -> "21"
```

Variable tracking table:

```text
+---------+----------+-----------+-----------+----------------+
| Step    | number   | quotient  | remainder | char           |
+---------+----------+-----------+-----------+----------------+
| 1       | 125      | 2         | 1         | '1'            |
| 2       | 2        | 0         | 2         | '2'            |
+---------+----------+-----------+-----------+----------------+

Collected reversed result:
"21"
```

---

### Dry Run 6: Encode 1000000

Use repeated division:

```text
1000000 / 62 = 16129 remainder 2
16129   / 62 = 260   remainder 9
260     / 62 = 4     remainder 12
4       / 62 = 0     remainder 4
```

Remainders:

```text
2, 9, 12, 4
```

Characters:

```text
2  -> '2'
9  -> '9'
12 -> 'c'
4  -> '4'
```

Reverse:

```text
4 c 9 2
```

Result:

```text
1000000 -> "4c92"
```

ASCII:

```text
1000000
   |
   +-- rem 2  -> '2'
16129
   |
   +-- rem 9  -> '9'
260
   |
   +-- rem 12 -> 'c'
4
   |
   +-- rem 4  -> '4'

Reverse: 4 c 9 2
```

---

## 12. Step-by-Step Decoding Dry Runs

### Dry Run 1: Decode "0"

```text
value = 0
char '0' -> digit 0
value = 0 * 62 + 0 = 0
```

Result:

```text
"0" -> 0
```

---

### Dry Run 2: Decode "Z"

```text
'Z' -> 61
value = 0 * 62 + 61 = 61
```

Result:

```text
"Z" -> 61
```

---

### Dry Run 3: Decode "10"

```text
value = 0

'1':
value = 0 * 62 + 1 = 1

'0':
value = 1 * 62 + 0 = 62
```

Result:

```text
"10" -> 62
```

---

### Dry Run 4: Decode "21"

```text
value = 0

'2':
value = 0 * 62 + 2 = 2

'1':
value = 2 * 62 + 1 = 125
```

Result:

```text
"21" -> 125
```

---

### Dry Run 5: Decode "4c92"

Character values:

```text
'4' = 4
'c' = 12
'9' = 9
'2' = 2
```

Steps:

```text
value = 0

'4':
value = 0 * 62 + 4 = 4

'c':
value = 4 * 62 + 12 = 260

'9':
value = 260 * 62 + 9 = 16129

'2':
value = 16129 * 62 + 2 = 1000000
```

Result:

```text
"4c92" -> 1000000
```

Variable tracking table:

```text
+------+---------+-------+--------------------+
| Step | char    | digit | value              |
+------+---------+-------+--------------------+
| 1    | '4'     | 4     | 4                  |
| 2    | 'c'     | 12    | 260                |
| 3    | '9'     | 9     | 16129              |
| 4    | '2'     | 2     | 1000000            |
+------+---------+-------+--------------------+
```

---

## 13. Java Implementation

Create:

```text
src/main/java/com/miniurl/shortener/url/service/Base62Encoder.java
```

Code:

```java
package com.miniurl.shortener.url.service;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Base62Encoder {

    private static final String ALPHABET =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final int BASE = ALPHABET.length();

    private final Map<Character, Integer> reverseLookup = new HashMap<>();

    public Base62Encoder() {
        for (int i = 0; i < ALPHABET.length(); i++) {
            reverseLookup.put(ALPHABET.charAt(i), i);
        }
    }

    public String encode(long number) {
        if (number < 0) {
            throw new IllegalArgumentException("number must be non-negative");
        }

        if (number == 0) {
            return "0";
        }

        StringBuilder result = new StringBuilder();

        long current = number;

        while (current > 0) {
            int remainder = (int) (current % BASE);
            result.append(ALPHABET.charAt(remainder));
            current = current / BASE;
        }

        return result.reverse().toString();
    }

    public long decode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("code must not be blank");
        }

        long result = 0;

        for (int i = 0; i < code.length(); i++) {
            char ch = code.charAt(i);

            Integer digit = reverseLookup.get(ch);

            if (digit == null) {
                throw new IllegalArgumentException("invalid Base62 character: " + ch);
            }

            result = result * BASE + digit;
        }

        return result;
    }
}
```

Why use reverse lookup map?

Without map:

```text
For each char, search alphabet string.
```

With map:

```text
Character -> digit in O(1)
```

For small strings it is not huge, but it is cleaner and scalable.

Important:

```text
decode can overflow long if code is too large.
```

For this project, short codes are small enough. For defensive production code, add overflow checks.

---

## 14. Internal Execution Walkthrough

Encoding:

```java
encode(125)
```

Execution:

```text
number = 125
current = 125
BASE = 62
result = ""

Loop 1:
remainder = 125 % 62 = 1
append alphabet[1] = '1'
current = 125 / 62 = 2
result = "1"

Loop 2:
remainder = 2 % 62 = 2
append alphabet[2] = '2'
current = 2 / 62 = 0
result = "12"

Loop ends.
reverse "12" -> "21"
return "21"
```

Decoding:

```java
decode("21")
```

Execution:

```text
result = 0

char '2':
digit = 2
result = 0 * 62 + 2 = 2

char '1':
digit = 1
result = 2 * 62 + 1 = 125

return 125
```

ASCII:

```text
ENCODE:
number -> divide by 62 -> remainders -> reverse -> code

DECODE:
code -> scan chars -> multiply by 62 -> number
```

Core symmetry:

```text
encode(decode(code)) = code
decode(encode(number)) = number
```

assuming valid input and consistent alphabet.

---

## 15. Base62 In URL Shortener Flow

There are two common ways to use Base62 in URL shorteners.

### Flow A: Database ID -> Base62

```text
1. Insert row and get numeric id.
2. Encode id using Base62.
3. Use encoded value as shortCode.
```

ASCII:

```text
Long URL
   |
   v
Insert DB row
   |
   v
id = 1000000
   |
   v
Base62.encode(id)
   |
   v
shortCode = 4c92
   |
   v
https://mini.ly/4c92
```

Pros:

```text
No random collision.
Compact.
Simple.
```

Cons:

```text
Sequential and guessable.
May require insert then update.
Can reveal growth pattern.
```

### Flow B: Random Base62 code

```text
1. Generate random Base62 characters.
2. Insert with UNIQUE constraint.
3. Retry on collision.
```

ASCII:

```text
Generate random chars
   |
   v
shortCode = aB91xZ
   |
   v
INSERT with UNIQUE
   |
   +-- success
   |
   +-- duplicate -> retry
```

Pros:

```text
Less guessable.
No sequential exposure.
No need to encode DB ID.
```

Cons:

```text
Collision possible.
Need retry logic.
```

This project can learn both.

Chapter 005 used random Base62-like generation.
This chapter teaches numeric Base62 encoding.

Chapter 008 will compare ID generation strategies deeply.

---

## 16. ID-Based Base62 vs Random Base62

Comparison:

```text
+----------------------+------------------------+------------------------+
| Feature              | ID-Based Base62        | Random Base62          |
+----------------------+------------------------+------------------------+
| Input                | numeric id             | random generator       |
| Collision            | no, if id unique       | possible               |
| Guessability         | high if sequential     | lower                  |
| Simplicity           | simple algorithm       | simple but retry needed|
| DB dependency        | often needs id first   | can generate before DB |
| Public pattern       | sequential-ish         | random-looking         |
+----------------------+------------------------+------------------------+
```

ID-based example:

```text
id 1  -> 1
id 2  -> 2
id 61 -> Z
id 62 -> 10
```

Problem:

```text
User can guess /1, /2, /3, /4.
```

Random example:

```text
k9Lm2Q
xP81aa
0Zt92B
```

Harder to enumerate.

But random still needs:

```text
UNIQUE(short_code)
```

Senior decision:

```text
Use ID-based Base62 for simplicity.
Use random/non-sequential IDs for reduced guessability.
Use distributed ID generation at large scale.
```

---

## 17. Capacity Planning

Capacity formula:

```text
capacity = base^length
```

For Base62:

```text
capacity = 62^length
```

Table:

```text
+--------+---------------------+
| Length | Capacity            |
+--------+---------------------+
| 1      | 62                  |
| 2      | 3,844               |
| 3      | 238,328             |
| 4      | 14,776,336          |
| 5      | 916,132,832         |
| 6      | 56,800,235,584      |
| 7      | 3,521,614,606,208   |
| 8      | 218,340,105,584,896 |
+--------+---------------------+
```

If you create:

```text
100 million URLs
```

5 characters may theoretically fit:

```text
62^5 = 916 million
```

But for random generation, you should not fill space too much because collisions increase.

For random codes:

```text
bigger code length reduces collision risk.
```

For ID-based codes:

```text
length grows naturally as ID grows.
```

Example:

```text
id up to 61       -> 1 char
id up to 3843     -> 2 chars
id up to 238327   -> 3 chars
```

Capacity mental model:

```text
Each extra Base62 character multiplies capacity by 62.
```

---

## 18. Security And Guessability

Base62 is not security.

If codes are generated from sequential IDs:

```text
1
2
3
4
5
```

Users can enumerate links.

Even Base62:

```text
1
2
3
...
Z
10
11
```

Still enumerable.

If private/unlisted links matter, use:

```text
random codes
longer codes
unguessable IDs
access control
rate limiting
abuse detection
```

Bad assumption:

```text
Base62 hides IDs securely.
```

Correct:

```text
Base62 makes IDs shorter, not secret.
```

ASCII:

```text
Sequential ID:
1 -> 1
2 -> 2
3 -> 3

Attacker:
GET /1
GET /2
GET /3
```

Mitigation:

```text
1. Random short codes.
2. Minimum 7-8 chars.
3. Rate limiting.
4. Monitoring 404 scan patterns.
5. Authentication for private links.
```

For public URL shortener:

```text
Guessability may be acceptable if links are public.
```

For private documents:

```text
Base62 sequential IDs are not enough.
```

---

## 19. Edge Cases

### Edge Case 1: Zero

```text
0 -> "0"
```

Must handle explicitly.

### Edge Case 2: Negative number

Reject:

```text
-1
```

Because URL IDs should not be negative.

### Edge Case 3: Invalid decode character

Reject:

```text
abc$12
```

because `$` is not Base62.

### Edge Case 4: Blank code

Reject:

```text
""
"   "
null
```

### Edge Case 5: Leading zeroes

Input:

```text
"0001"
```

Decodes to:

```text
1
```

But encode(1) returns:

```text
"1"
```

So:

```text
encode(decode("0001")) = "1"
```

This is normal canonicalization.

For short URLs, you may reject leading zeroes if you want canonical codes.

### Edge Case 6: Overflow

A very long code can exceed `long`.

Example:

```text
ZZZZZZZZZZZZZZZZZZZZ
```

May overflow.

For v1, code length is limited to 32 by schema, but decode to long should either:

```text
1. add overflow checks
2. use BigInteger
3. restrict max length
```

### Edge Case 7: Alphabet change

Existing code:

```text
"21"
```

Meaning depends on alphabet.

Changing alphabet breaks decoding.

Rule:

```text
Alphabet is part of permanent data contract.
```

---

## 20. Testing Strategy

### Encode tests

```text
0 -> "0"
1 -> "1"
61 -> "Z"
62 -> "10"
125 -> "21"
1000000 -> "4c92"
```

JUnit example:

```java
@Test
void shouldEncodeKnownNumbers() {
    Base62Encoder encoder = new Base62Encoder();

    assertThat(encoder.encode(0)).isEqualTo("0");
    assertThat(encoder.encode(1)).isEqualTo("1");
    assertThat(encoder.encode(61)).isEqualTo("Z");
    assertThat(encoder.encode(62)).isEqualTo("10");
    assertThat(encoder.encode(125)).isEqualTo("21");
    assertThat(encoder.encode(1_000_000)).isEqualTo("4c92");
}
```

### Decode tests

```java
@Test
void shouldDecodeKnownCodes() {
    Base62Encoder encoder = new Base62Encoder();

    assertThat(encoder.decode("0")).isEqualTo(0);
    assertThat(encoder.decode("1")).isEqualTo(1);
    assertThat(encoder.decode("Z")).isEqualTo(61);
    assertThat(encoder.decode("10")).isEqualTo(62);
    assertThat(encoder.decode("21")).isEqualTo(125);
    assertThat(encoder.decode("4c92")).isEqualTo(1_000_000);
}
```

### Round-trip tests

```java
@Test
void shouldRoundTripNumbers() {
    Base62Encoder encoder = new Base62Encoder();

    long[] numbers = {0, 1, 62, 125, 999, 1_000_000, Long.MAX_VALUE / 1000};

    for (long number : numbers) {
        String code = encoder.encode(number);
        long decoded = encoder.decode(code);

        assertThat(decoded).isEqualTo(number);
    }
}
```

### Invalid tests

```text
encode(-1) -> error
decode(null) -> error
decode("") -> error
decode("abc$") -> error
```

Testing rule:

```text
Known examples prove correctness.
Round-trip tests prove symmetry.
Invalid tests prove safety.
```

---

## 21. Production Failure Stories

### Failure Story 1: Alphabet changed after launch

Team starts with:

```text
0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ
```

Later changes to:

```text
0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz
```

Old code:

```text
a
```

Now decodes differently.

Result:

```text
Existing short URLs break or redirect to wrong IDs.
```

Root cause:

```text
Alphabet treated as implementation detail instead of permanent contract.
```

Fix:

```text
Never change alphabet for existing codes.
If required, version encoding scheme.
```

Lesson:

```text
Encoding alphabet is production data contract.
```

---

### Failure Story 2: Sequential IDs expose private links

Short URLs:

```text
/1
/2
/3
/4
```

Attacker scans.

Result:

```text
Private or sensitive links discovered.
```

Root cause:

```text
Base62 was assumed to be secure.
```

Fix:

```text
Use random longer codes and rate limiting for unlisted/private links.
```

Lesson:

```text
Encoding is not authorization.
```

---

### Failure Story 3: Missing zero handling

Code:

```java
while (number > 0) { ... }
return result.reverse().toString();
```

For zero:

```text
returns ""
```

Bad URL:

```text
https://mini.ly/
```

Root cause:

```text
Zero special case missing.
```

Fix:

```java
if (number == 0) return "0";
```

Lesson:

```text
Boundary values matter.
```

---

### Failure Story 4: Overflow during decode

System accepts very long code.

Decode overflows long.

Result:

```text
negative value
wrong lookup
unexpected error
```

Root cause:

```text
No max length or overflow check.
```

Fix:

```text
Limit code length.
Reject invalid length.
Use BigInteger if needed.
```

Lesson:

```text
Parsing external input must be defensive.
```

---

## 22. Debugging Mindset

When Base62 output looks wrong, ask:

```text
Which alphabet is used?
Is encode reversing the collected characters?
Is zero handled?
Is decode multiplying before adding?
Are invalid characters rejected?
Is long overflow possible?
Are tests based on same alphabet?
```

Debug encode:

```text
number = ?
base = 62
remainder sequence = ?
characters = ?
reversed result = ?
```

Debug decode:

```text
code = ?
each char digit = ?
value after each step = ?
```

Example debug table for encode 125:

```text
+------+---------+-----------+-----------+------+
| Step | current | quotient  | remainder | char |
+------+---------+-----------+-----------+------+
| 1    | 125     | 2         | 1         | 1    |
| 2    | 2       | 0         | 2         | 2    |
+------+---------+-----------+-----------+------+

Reverse -> 21
```

Example debug table for decode 21:

```text
+------+------|-------|-------+
| Step | char | digit | value |
+------+------|-------|-------+
| 1    | 2    | 2     | 2     |
| 2    | 1    | 1     | 125   |
+------+------|-------|-------+
```

Golden debugging rule:

```text
If encode/decode disagree, check alphabet and reversal first.
```

---

## 23. Common Mistakes

### Mistake 1: Thinking Base62 compresses the long URL

Wrong:

```text
longUrl -> Base62
```

Correct:

```text
numeric ID -> Base62
```

or:

```text
random Base62 chars -> shortCode
```

The long URL is stored in database.

---

### Mistake 2: Thinking Base62 is encryption

Wrong:

```text
Base62 hides the ID securely.
```

Correct:

```text
Base62 is reversible representation.
```

---

### Mistake 3: Forgetting to reverse during encoding

Wrong:

```text
125 -> "12"
```

Correct:

```text
125 -> "21"
```

because remainders are collected from least significant to most significant.

---

### Mistake 4: Changing alphabet later

Wrong:

```text
Alphabet is harmless implementation detail.
```

Correct:

```text
Alphabet is permanent data contract.
```

---

### Mistake 5: No invalid character handling

Wrong:

```text
decode("abc$") silently returns something.
```

Correct:

```text
Reject invalid characters.
```

---

### Mistake 6: Sequential Base62 for private URLs

Wrong:

```text
Base62 sequential IDs are private enough.
```

Correct:

```text
Use random unguessable codes or authorization.
```

---

### Mistake 7: Not testing boundary values

Must test:

```text
0
1
61
62
large number
invalid input
```

---

## 24. Interview-Ready Explanation

If interviewer asks:

```text
How do you generate short codes for a URL shortener?
```

Strong Base62 answer:

```text
One common approach is to generate a numeric ID and encode it using Base62.
Base62 uses 62 URL-safe characters, usually digits, lowercase letters, and uppercase
letters. The encoding repeatedly divides the number by 62, maps each remainder to
a character, and reverses the collected characters. Decoding scans the string from
left to right and repeatedly multiplies the current value by 62 before adding the
digit value. This gives compact codes: for example, 62 becomes 10 and 125 becomes
21 depending on the alphabet. Base62 is not encryption; if IDs are sequential, the
codes are guessable. For public short links that may be acceptable, but for less
guessable links I would use random Base62 codes or a non-sequential ID strategy,
still enforcing uniqueness in the database.
```

Why this is strong:

```text
1. Explains what Base62 is.
2. Explains encoding algorithm.
3. Explains decoding algorithm.
4. Mentions URL-safe alphabet.
5. Mentions compactness.
6. Mentions not encryption.
7. Mentions guessability.
8. Connects to DB uniqueness.
```

Senior one-liner:

```text
Base62 is a compact URL-safe representation for numeric IDs, but uniqueness and security depend on the ID generation strategy, not on Base62 itself.
```

---

## 25. Senior Engineer Checklist

Before moving to ID generation strategies, confirm:

```text
[ ] You know Base62 is representation, not compression.
[ ] You know Base62 is not encryption.
[ ] You can explain why modulo gives the next digit.
[ ] You can explain why encoding reverses result.
[ ] You can explain why decoding multiplies by 62.
[ ] You can encode 62 as 10.
[ ] You can encode 125 as 21.
[ ] You can decode 4c92 as 1000000.
[ ] You know alphabet must not change after launch.
[ ] You handle zero.
[ ] You reject negative numbers.
[ ] You reject invalid decode characters.
[ ] You understand sequential ID guessability.
[ ] You understand random Base62 collision risk.
[ ] You know DB uniqueness is still required.
```

If all are checked, Base62 is clear.

---

## 26. One-Page Cheat Sheet

```text
Base62:
number representation using 62 symbols

Alphabet:
0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ

Base:
62

Encode:
number -> Base62 string

Encode algorithm:
while number > 0:
    remainder = number % 62
    append alphabet[remainder]
    number = number / 62
reverse result

Decode:
Base62 string -> number

Decode algorithm:
value = 0
for char in code:
    digit = alphabet index
    value = value * 62 + digit

Important examples:
0  -> 0
1  -> 1
61 -> Z
62 -> 10
125 -> 21
1000000 -> 4c92

Capacity:
62^6 = 56.8 billion
62^7 = 3.5 trillion
62^8 = 218 trillion

Base62 is:
compact
URL-safe
reversible representation

Base62 is not:
compression of long URL
encryption
authorization
collision prevention

ID-based Base62:
no collision if ID unique
guessable if sequential

Random Base62:
less guessable
collision possible
needs DB UNIQUE + retry

Production rule:
Alphabet is permanent data contract.
```

---

## 27. One Picture To Remember

```text
                       BASE62 ENCODING MENTAL MODEL

                         "A bigger digit alphabet"

Decimal digits:
0 1 2 3 4 5 6 7 8 9
base = 10

Base62 digits:
0 1 2 ... 9 a b c ... z A B C ... Z
base = 62


ENCODE NUMBER -> CODE

number = 125

125 ÷ 62 = 2 remainder 1  -> '1'
  2 ÷ 62 = 0 remainder 2  -> '2'

Collected remainders:
1 2

Reverse:
2 1

Base62 code:
"21"


DECODE CODE -> NUMBER

code = "21"

start value = 0

'2':
value = 0 * 62 + 2 = 2

'1':
value = 2 * 62 + 1 = 125


URL SHORTENER CONNECTION

Database ID
    |
    v
Base62 encode
    |
    v
shortCode
    |
    v
https://mini.ly/{shortCode}


FINAL MEMORY:

Base62 does not shorten the original URL.
It shortens the representation of a number.
The database still stores:
shortCode -> longUrl
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. Base62 is a number representation using 62 URL-safe characters.
2. Encoding uses repeated division by 62 and reverses the remainders.
3. Decoding multiplies by 62 and adds each character value.
4. Base62 is not encryption; sequential IDs remain guessable.
5. Base62 helps create compact short codes, but the database still enforces uniqueness.
```

After this chapter, the next natural step is:

```text
008_Id_Generation_Strategies.md
```

Because Base62 only explains representation. The next question is: where does the number or token come from?
