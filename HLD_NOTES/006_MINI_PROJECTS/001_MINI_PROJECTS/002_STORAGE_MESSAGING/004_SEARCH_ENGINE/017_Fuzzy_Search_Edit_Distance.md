# 017_Fuzzy_Search_Edit_Distance.md

# MiniSearchEngine — 017 Fuzzy Search & Edit Distance

## 0. Why This File Exists

Real users make typing mistakes.

Examples:

```text
redsi
javva
postgress
micorservice
```

But search engines should still return:

```text
redis
java
postgres
microservice
```

This capability is called:

```text
Fuzzy Search
```

Fuzzy search is one of the MOST important features inside:

```text
Google Search
Elasticsearch
Lucene
Amazon Search
IDE Search
Spell Correction Systems
AI Retrieval Systems
```

Fuzzy search works using:

```text
Edit Distance
```

This file teaches:

```text
fuzzy search
edit distance
Levenshtein distance
insert delete replace
dynamic programming
spell correction
Lucene FuzzyQuery
Elasticsearch fuzzy search
BK Trees
trigram matching
candidate generation
ranking typo corrections
Java implementations
dry runs
production optimizations
```

Only ASCII diagrams are used.

---

# 1. One-Line Definition

```text
Fuzzy search finds terms similar to a query even when spelling mistakes exist.
```

---

# 2. Biggest Mental Model

User types:

```text
redsi
```

Search engine tries to find:

```text
closest valid word
```

Example:

```text
redis
```

---

# 3. Fuzzy Search ASCII

```text
User Query:
redsi

Closest Dictionary Word:
redis
```

---

# 4. Why Fuzzy Search Important

Without fuzzy search:

```text
small typos return zero results
```

Bad user experience.

Fuzzy search improves:

```text
tolerance
search quality
query recovery
user satisfaction
```

---

# 5. Edit Distance

Core concept behind fuzzy search:

```text
Edit Distance
```

Meaning:

```text
minimum operations needed
to convert one string into another
```

---

# 6. Allowed Operations

Classic operations:

```text
insert
delete
replace
```

---

# 7. Edit Distance Example

Convert:

```text
redsi
```

to:

```text
redis
```

Operations:

```text
swap positions conceptually
or replace/delete/insert sequence
```

Distance small.

---

# 8. Levenshtein Distance

Most famous edit distance algorithm:

```text
Levenshtein Distance
```

Measures minimum edits required.

---

# 9. Levenshtein Mental Model

Small distance:

```text
strings very similar
```

Large distance:

```text
strings very different
```

---

# 10. Distance Example

Words:

```text
redis
redsi
```

Distance:

```text
2
```

because two edits needed.

---

# 11. Insert Operation

Convert:

```text
redis
```

to:

```text
rediss
```

Operation:

```text
insert 's'
```

Distance:

```text
1
```

---

# 12. Delete Operation

Convert:

```text
redis
```

to:

```text
redi
```

Operation:

```text
delete 's'
```

Distance:

```text
1
```

---

# 13. Replace Operation

Convert:

```text
redis
```

to:

```text
radis
```

Operation:

```text
replace 'e' with 'a'
```

Distance:

```text
1
```

---

# 14. Edit Distance ASCII

```text
redis
  ↓ replace e→a
radis

distance = 1
```

---

# 15. Dynamic Programming

Levenshtein distance solved using:

```text
dynamic programming
```

Build matrix:

```text
rows = word1
columns = word2
```

---

# 16. DP Matrix Mental Model

Cell:

```text
dp[i][j]
```

means:

```text
minimum edits to convert
first i chars → first j chars
```

---

# 17. DP Matrix ASCII

```text
      r e d i s
    -------------
  | 0 1 2 3 4 5
r | 1
e | 2
d | 3
i | 4
s | 5
```

---

# 18. DP Transition

If chars equal:

```text
dp[i][j] = dp[i-1][j-1]
```

Else:

```text
1 + min(
 insert,
 delete,
 replace
)
```

---

# 19. Insert/Delete/Replace ASCII

```text
Insert  → left
Delete  → top
Replace → diagonal
```

---

# 20. Example — redis vs redsi

Compare:

```text
redis
redsi
```

Most characters same.

Distance small.

Search engine may treat:

```text
redis ≈ redsi
```

---

# 21. Why Exact Match Still Better

Exact matches should usually rank higher.

Example:

```text
redis
```

should rank above:

```text
redsi correction
```

---

# 22. Fuzzy Search Pipeline

```text
User Query
     ↓
Generate Candidate Terms
     ↓
Compute Edit Distance
     ↓
Keep Similar Terms
     ↓
Rank Suggestions
     ↓
Return Results
```

---

# 23. Candidate Generation Problem

Dictionary may contain:

```text
millions of terms
```

Cannot compute distance against ALL words.

Too expensive.

---

# 24. Why Brute Force Bad

Suppose:

```text
10 million dictionary terms
```

Query:

```text
redsi
```

Brute force:

```text
compare against all 10 million words
```

Too slow.

---

# 25. BK Tree

Famous fuzzy search structure:

```text
BK Tree
```

Optimized for edit-distance lookup.

---

# 26. BK Tree Mental Model

Each node stores:

```text
word
distance edges
```

Nearby words grouped together.

---

# 27. BK Tree ASCII

```text
redis
  |
 distance=1
  |
redsi
```

---

# 28. BK Tree Search

Query:

```text
redsi
```

Search nearby nodes only.

Avoid scanning entire dictionary.

---

# 29. Trigram Matching

Another fuzzy search optimization:

```text
trigrams
```

Break words into:

```text
3-character chunks
```

---

# 30. Trigram Example

Word:

```text
redis
```

Trigrams:

```text
red
edi
dis
```

---

# 31. Trigram Matching Mental Model

Words sharing many trigrams likely similar.

Example:

```text
redis
redsi
```

share:

```text
red
```

---

# 32. Lucene FuzzyQuery

Lucene internally supports:

```text
FuzzyQuery
```

Example:

```text
redsi~2
```

Meaning:

```text
max edit distance 2
```

---

# 33. Fuzzy Query ASCII

```text
redsi~2
   ↓
terms within distance 2
```

---

# 34. Elasticsearch Fuzzy Search

Elasticsearch DSL:

```json
{
  "fuzzy": {
    "title": {
      "value": "redsi",
      "fuzziness": 2
    }
  }
}
```

Internally uses Lucene fuzzy search.

---

# 35. Why Fuzzy Search Expensive

Fuzzy matching requires:

```text
candidate generation
distance calculation
ranking
```

Much more expensive than exact search.

---

# 36. Production Limits

Search systems often limit:

```text
max fuzziness
max expansions
query complexity
```

to avoid overload.

---

# 37. Fuzziness Levels

Common fuzziness:

```text
0 → exact match
1 → one edit allowed
2 → two edits allowed
```

Higher fuzziness:

```text
more tolerant
but slower/noisier
```

---

# 38. Spell Correction

Fuzzy search often used for:

```text
spell correction
```

Example:

```text
javva
```

Suggest:

```text
java
```

---

# 39. Spell Correction ASCII

```text
Misspelled Query
       ↓
Candidate Terms
       ↓
Edit Distance Ranking
       ↓
Best Correction
```

---

# 40. Ranking Corrections

Corrections ranked using:

```text
edit distance
term popularity
query frequency
language models
click data
```

---

# 41. Why Popularity Important

Suppose:

```text
jvaa
```

Possible corrections:

```text
java
jvm
```

More popular term may rank higher.

---

# 42. Fuzzy Search + BM25

Fuzzy matching often combined with:

```text
BM25 ranking
```

Exact matches still receive stronger score.

---

# 43. Fuzzy Matching ASCII

```text
Candidate Terms
      ↓
Edit Distance Filter
      ↓
BM25 Ranking
      ↓
Top Results
```

---

# 44. Search-As-You-Type + Fuzzy

Autocomplete may support typo tolerance.

User types:

```text
redsi
```

Suggestions:

```text
redis
redis cluster
redis docker
```

---

# 45. Multi-Language Challenges

Fuzzy search harder for:

```text
unicode
accented chars
asian languages
arabic morphology
```

Requires language-aware normalization.

---

# 46. Java Levenshtein Distance

```java
public class LevenshteinDistance {

    public int distance(
            String a,
            String b) {

        int[][] dp =
                new int[a.length() + 1]
                       [b.length() + 1];

        for (int i = 0;
             i <= a.length();
             i++) {

            dp[i][0] = i;
        }

        for (int j = 0;
             j <= b.length();
             j++) {

            dp[0][j] = j;
        }

        for (int i = 1;
             i <= a.length();
             i++) {

            for (int j = 1;
                 j <= b.length();
                 j++) {

                if (a.charAt(i - 1)
                        == b.charAt(j - 1)) {

                    dp[i][j] =
                            dp[i - 1][j - 1];
                }
                else {

                    dp[i][j] =
                            1 + Math.min(
                                    dp[i - 1][j],
                                    Math.min(
                                            dp[i][j - 1],
                                            dp[i - 1][j - 1]
                                    )
                            );
                }
            }
        }

        return dp[a.length()][b.length()];
    }
}
```

---

# 47. Java Dry Run

Compare:

```text
redis
redsi
```

Execution builds DP matrix.

Final distance:

```text
2
```

---

# 48. Insert/Delete/Replace Dry Run

Convert:

```text
redis
```

to:

```text
radis
```

Operations:

```text
replace e → a
```

Distance:

```text
1
```

---

# 49. Java Candidate Filtering

```java
import java.util.*;

public class FuzzyMatcher {

    public List<String> filter(
            List<String> dictionary,
            String query,
            int maxDistance) {

        List<String> result =
                new ArrayList<>();

        LevenshteinDistance ld =
                new LevenshteinDistance();

        for (String word : dictionary) {

            if (ld.distance(word, query)
                    <= maxDistance) {

                result.add(word);
            }
        }

        return result;
    }
}
```

---

# 50. Candidate Filter Dry Run

Dictionary:

```text
redis
kafka
postgres
```

Query:

```text
redsi
```

Distances:

```text
redis → 2
kafka → large
postgres → large
```

Result:

```text
redis
```

---

# 51. BK Tree Optimization

Instead of scanning all words:

```text
search nearby edit-distance regions only
```

Huge performance improvement.

---

# 52. BK Tree ASCII

```text
Query:
redsi

Search nearby nodes only
```

---

# 53. Trigram Optimization

Instead of full DP on all words:

```text
first find trigram overlaps
then compute edit distance
```

Reduces candidate set.

---

# 54. Trigram ASCII

```text
redis → red edi dis
redsi → red eds dsi

shared trigram:
red
```

---

# 55. Production Challenges

Real fuzzy systems handle:

```text
billions of queries
multiple languages
keyboard typos
mobile typing errors
large dictionaries
ranking quality
latency constraints
```

---

# 56. Performance Tradeoffs

Higher fuzziness:

```text
better typo tolerance
more CPU
more false positives
```

Lower fuzziness:

```text
faster
more precise
less tolerant
```

---

# 57. Common Mistakes

## Mistake 1

```text
Brute force compare against all words
```

Too slow.

---

## Mistake 2

```text
Using high fuzziness everywhere
```

Too noisy.

---

## Mistake 3

```text
Ignoring popularity ranking
```

Corrections become poor.

---

## Mistake 4

```text
Treating fuzzy match same as exact match
```

Exact matches should score higher.

---

# 58. Interview Explanation

If interviewer asks:

```text
How does fuzzy search work?
```

Strong answer:

```text
Fuzzy search finds similar terms using edit distance algorithms like
Levenshtein distance. Search engines generate candidate terms, compute
distances, and return matches within a maximum allowed edit distance.
```

Senior addition:

```text
Production systems optimize fuzzy search using BK trees, trigrams,
candidate pruning, and popularity-based ranking.
```

---

# 59. Final Mental Model

```text
Fuzzy Search
      =
Find Closest Valid Terms
Using Edit Distance
```

---

# 60. What To Remember

```text
Fuzzy search handles typos.

Levenshtein distance measures edits.

Operations:
insert delete replace.

Lucene supports FuzzyQuery.

Elasticsearch supports fuzzy queries.

BK Trees optimize candidate lookup.

Trigrams reduce search space.

Exact matches should rank higher.
```

---

# 61. Next File

```text
018_Sharding_Replication_Search.md
```

Next you learn:

```text
distributed search
search sharding
replication
scatter-gather
distributed ranking
query coordination
Elasticsearch cluster internals
fault tolerance
production search architecture
```
