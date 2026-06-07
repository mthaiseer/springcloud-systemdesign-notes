# 015_Phrase_Search.md

# MiniSearchEngine — 015 Phrase Search

## 0. Why This File Exists

Boolean search can answer:

```text
Does document contain terms?
```

But many real search queries require something stronger:

```text
Do terms appear together
in the correct order
and close to each other?
```

Example:

```text
"redis caching"
```

User expects:

```text
redis immediately followed by caching
```

NOT:

```text
redis distributed high performance caching
```

This is called:

```text
Phrase Search
```

Phrase search is extremely important in:

```text
Google Search
Elasticsearch
Lucene
Legal Search
Ecommerce Search
Autocomplete
Document Retrieval
AI Retrieval Systems
```

This file teaches:

```text
phrase queries
positional indexes
token positions
phrase matching algorithms
slop
proximity search
highlighting
Lucene PhraseQuery
Elasticsearch match_phrase
Java implementations
dry runs
production optimizations
```

Only ASCII diagrams are used.

---

# 1. One-Line Definition

```text
Phrase search matches documents where terms appear in the correct order and positions.
```

---

# 2. Biggest Mental Model

Boolean search checks:

```text
term existence
```

Phrase search checks:

```text
term existence
+
positions
+
ordering
+
distance
```

---

# 3. Phrase Search Mental Model

Query:

```text
"redis caching"
```

Phrase search asks:

```text
Does redis appear immediately
before caching?
```

---

# 4. Boolean Search vs Phrase Search

Boolean query:

```text
redis AND caching
```

Matches:

```text
redis distributed caching
redis high speed caching
redis caching
```

Phrase query:

```text
"redis caching"
```

Matches only:

```text
redis caching
```

---

# 5. Phrase Search ASCII

```text
Boolean Search:
redis .... caching

MATCH

Phrase Search:
redis caching

MATCH
```

---

# 6. Why Phrase Search Important

Phrase queries improve:

```text
precision
search quality
user intent understanding
ranking relevance
```

Without phrase search:

```text
too many irrelevant results
```

---

# 7. Example — Google Search

Query:

```text
"machine learning"
```

User expects exact phrase.

NOT:

```text
machine distributed AI learning
```

Phrase search improves quality.

---

# 8. Positional Index

Phrase search requires:

```text
token positions
```

Normal posting list stores:

```text
docIDs only
```

Phrase search posting list stores:

```text
docID
frequency
positions
```

---

# 9. Positional Posting List

Document:

```text
redis caching fast redis
```

Positions:

```text
redis   → [1,4]
caching → [2]
fast    → [3]
```

---

# 10. Positional Index ASCII

```text
redis →
[
  {doc=1, positions=[1,4]}
]

caching →
[
  {doc=1, positions=[2]}
]
```

---

# 11. Why Positions Critical

Without positions:

```text
cannot verify ordering
cannot verify adjacency
cannot verify phrase match
```

Phrase search impossible.

---

# 12. Phrase Query Example

Query:

```text
"redis caching"
```

Document:

```text
redis caching fast
```

Positions:

```text
redis   → [1]
caching → [2]
```

Check:

```text
2 = 1 + 1
```

Phrase matches.

---

# 13. Phrase Match ASCII

```text
redis position:
1

caching position:
2

2 = 1 + 1

MATCH
```

---

# 14. Negative Phrase Example

Document:

```text
redis distributed caching
```

Positions:

```text
redis → [1]
caching → [3]
```

Check:

```text
3 != 1 + 1
```

Phrase does NOT match.

---

# 15. Phrase Query Execution Pipeline

```text
Phrase Query
      ↓
Tokenizer
      ↓
Load Posting Lists
      ↓
Find Candidate Docs
      ↓
Check Positions
      ↓
Return Matches
```

---

# 16. Candidate Document Selection

Before checking positions:

```text
documents must contain all terms
```

Boolean AND used first.

---

# 17. Candidate Example

Query:

```text
"redis caching"
```

Posting lists:

```text
redis   → [1,2]
caching → [1]
```

Candidate docs:

```text
[1]
```

Then perform positional verification.

---

# 18. Phrase Matching Algorithm

Core rule:

```text
position(term2)
=
position(term1) + 1
```

---

# 19. Two-Term Phrase Dry Run

Document:

```text
redis caching fast
```

Positions:

```text
redis   → [1]
caching → [2]
```

Check:

```text
2 = 1 + 1
```

Phrase matches.

---

# 20. Three-Term Phrase Query

Query:

```text
"redis caching fast"
```

Need:

```text
caching = redis + 1
fast = caching + 1
```

---

# 21. Three-Term Phrase ASCII

```text
redis   → 1
caching → 2
fast    → 3

MATCH
```

---

# 22. Multiple Position Example

Document:

```text
redis caching redis caching
```

Positions:

```text
redis   → [1,3]
caching → [2,4]
```

Matches:

```text
1→2
3→4
```

---

# 23. Multiple Match ASCII

```text
redis:
[1,3]

caching:
[2,4]

Matches:
1→2
3→4
```

---

# 24. Slop

Phrase search may allow gaps.

Example:

```text
"redis caching"~2
```

Meaning:

```text
allow distance up to 2
```

---

# 25. Slop Example

Document:

```text
redis distributed caching
```

Positions:

```text
redis → 1
caching → 3
```

Distance:

```text
3 - 1 = 2
```

With slop=2:

```text
MATCH
```

---

# 26. Slop ASCII

```text
redis ... caching

distance = 2

allowed slop = 2

MATCH
```

---

# 27. Why Slop Useful

Users may type:

```text
"machine learning"
```

Document:

```text
machine deep learning
```

Still semantically related.

Slop improves flexibility.

---

# 28. Proximity Search

Generalization of phrase search.

Example:

```text
redis NEAR kafka
```

Meaning:

```text
terms appear close together
```

Not necessarily exact phrase.

---

# 29. Proximity Search ASCII

```text
redis ........ kafka

distance small
     ↓
higher relevance
```

---

# 30. Phrase Search + BM25

Phrase matches usually receive:

```text
extra ranking boost
```

because exact phrases strongly indicate relevance.

---

# 31. Phrase Boost ASCII

```text
Exact Phrase Match
        ↓
Higher BM25 Score
```

---

# 32. Lucene PhraseQuery

Lucene internally uses:

```text
PhraseQuery
```

PhraseQuery stores:

```text
terms
positions
slop
```

---

# 33. PhraseQuery Mental Model

```text
PhraseQuery
 ├── terms
 ├── expected positions
 └── slop
```

---

# 34. Elasticsearch match_phrase

Elasticsearch query:

```json
{
  "match_phrase": {
    "title": "redis caching"
  }
}
```

Internally becomes:

```text
Lucene PhraseQuery
```

---

# 35. Why Phrase Search Expensive

Phrase search requires:

```text
position checking
```

More expensive than Boolean search.

---

# 36. Complexity Comparison

Boolean search:

```text
posting intersection only
```

Phrase search:

```text
intersection
+
position verification
```

Additional CPU work required.

---

# 37. Positional Merge Algorithm

For candidate docs:

```text
scan position lists
verify adjacency
```

---

# 38. Positional Merge Example

Positions:

```text
redis   → [1,5]
caching → [2,8]
```

Check:

```text
2 = 1 + 1 → MATCH
8 != 5 + 1 → NO MATCH
```

---

# 39. Highlighting

Phrase search enables:

```text
highlighting exact phrases
```

Example:

```html
<em>redis caching</em> tutorial
```

---

# 40. Highlighting ASCII

```text
Matched Phrase
      ↓
Highlight Positions
      ↓
Render Highlighted Text
```

---

# 41. Ecommerce Example

Query:

```text
"iphone 15 pro"
```

Phrase match stronger than:

```text
iphone
15
pro
```

appearing separately.

---

# 42. Legal Search Example

Legal systems require exact phrases:

```text
"breach of contract"
```

Precision extremely important.

---

# 43. Autocomplete Use Case

Phrase queries useful for:

```text
search suggestions
autocomplete
query completion
search-as-you-type
```

---

# 44. Search Pipeline Mental Model

```text
Phrase Query
      ↓
Candidate Documents
      ↓
Position Verification
      ↓
Phrase Boost
      ↓
Rank Results
```

---

# 45. Compression Of Positions

Position storage large.

Lucene compresses:

```text
positions
offsets
payloads
```

to reduce disk usage.

---

# 46. Compression ASCII

```text
Large Position Lists
         ↓
Compression
         ↓
Smaller Index
```

---

# 47. Skip Pointers

Large posting lists use:

```text
skip pointers
```

to jump efficiently.

Improves phrase search speed.

---

# 48. Skip Pointer ASCII

```text
1 → 5 → 8 → 12 → 20
      ↘
       20
```

---

# 49. Phrase Search Optimization

Production optimizations:

```text
candidate pruning
rare-term-first execution
compressed positions
skip lists
caching
```

---

# 50. Rare Term First

Search engines often start with:

```text
rarest term
```

because:

```text
smaller candidate set
less positional checking
```

---

# 51. Rare Term ASCII

```text
Rare Term Posting List
        ↓
Small Candidate Docs
        ↓
Fast Phrase Search
```

---

# 52. Java PositionPosting

```java
import java.util.List;

public class PositionPosting {

    private final int docId;

    private final List<Integer> positions;

    public PositionPosting(
            int docId,
            List<Integer> positions) {

        this.docId = docId;
        this.positions = positions;
    }

    public int getDocId() {
        return docId;
    }

    public List<Integer> getPositions() {
        return positions;
    }
}
```

---

# 53. Java Phrase Matcher

```java
import java.util.List;

public class PhraseMatcher {

    public boolean matches(
            List<Integer> first,
            List<Integer> second) {

        for (Integer p1 : first) {

            for (Integer p2 : second) {

                if (p2 == p1 + 1) {
                    return true;
                }
            }
        }

        return false;
    }
}
```

---

# 54. Java Phrase Dry Run

Positions:

```text
redis   → [1,4]
caching → [2,8]
```

Execution:

```text
2 == 1 + 1 → MATCH
```

Phrase exists.

---

# 55. Java Slop Matcher

```java
public class SlopMatcher {

    public boolean matchesWithSlop(
            int p1,
            int p2,
            int slop) {

        return Math.abs(p2 - p1)
                <= slop;
    }
}
```

---

# 56. Slop Dry Run

Positions:

```text
redis → 1
caching → 3
```

Slop:

```text
2
```

Check:

```text
|3 - 1| = 2

MATCH
```

---

# 57. Java Candidate Intersection

```java
import java.util.*;

public class CandidateDocs {

    public List<Integer> intersect(
            List<Integer> a,
            List<Integer> b) {

        List<Integer> result =
                new ArrayList<>();

        int i = 0;
        int j = 0;

        while (i < a.size()
                && j < b.size()) {

            if (a.get(i).equals(b.get(j))) {

                result.add(a.get(i));
                i++;
                j++;
            }
            else if (a.get(i) < b.get(j)) {
                i++;
            }
            else {
                j++;
            }
        }

        return result;
    }
}
```

---

# 58. Full Phrase Search Dry Run

Query:

```text
"redis caching"
```

Documents:

```text
Doc1:
redis caching fast

Doc2:
redis distributed caching
```

Step 1 — candidate docs:

```text
Doc1
Doc2
```

Step 2 — position verification:

```text
Doc1:
2 = 1 + 1 → MATCH

Doc2:
3 != 1 + 1 → NO MATCH
```

Final result:

```text
Doc1
```

---

# 59. Phrase Search Tradeoffs

Phrase search improves:

```text
precision
quality
ranking
user intent understanding
```

But costs:

```text
more CPU
more memory
larger indexes
slower queries
```

---

# 60. Common Mistakes

## Mistake 1

```text
Thinking Boolean search enough for phrases
```

Need positional indexes.

---

## Mistake 2

```text
Not storing positions during indexing
```

Phrase search impossible.

---

## Mistake 3

```text
Ignoring slop
```

Slop improves flexibility.

---

## Mistake 4

```text
Checking positions before candidate pruning
```

Too expensive.

---

# 61. Production Challenges

Real systems handle:

```text
large phrase queries
multiple positions
distributed phrase matching
highlight generation
high QPS
compressed positional indexes
```

---

# 62. Interview Explanation

If interviewer asks:

```text
How does phrase search work?
```

Strong answer:

```text
Phrase search first finds candidate documents containing all terms using
posting list intersection. Then it checks token positions to verify
terms appear in the correct order and adjacency.
```

Senior addition:

```text
Lucene implements phrase search using positional indexes, PhraseQuery,
slop handling, compressed positions, and optimized positional merging.
```

---

# 63. Final Mental Model

```text
Phrase Search
      =
Boolean Matching
      +
Position Verification
```

---

# 64. What To Remember

```text
Phrase search requires positional indexes.

Posting lists store positions.

Phrase matching verifies adjacency/order.

Slop allows flexible matching.

Lucene uses PhraseQuery.

Phrase matches often receive boosts.

Phrase search more expensive than Boolean search.

Candidate pruning improves performance.
```

---

# 65. Next File

```text
016_Prefix_Autocomplete.md
```

Next you learn:

```text
autocomplete
prefix queries
tries
edge ngrams
completion suggester
search-as-you-type
Lucene PrefixQuery
Elasticsearch autocomplete internals
```
