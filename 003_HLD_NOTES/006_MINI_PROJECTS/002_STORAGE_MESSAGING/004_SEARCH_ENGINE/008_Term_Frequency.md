# 008_Term_Frequency.md

# MiniSearchEngine — 008 Term Frequency

## 0. Why This File Exists

Search engines do NOT return documents randomly.

They try to answer:

```text
Which documents are MOST relevant?
```

Suppose user searches:

```text
redis
```

Documents:

```text
Doc1:
redis redis redis caching fast

Doc2:
redis tutorial

Doc3:
database systems
```

All documents are not equally relevant.

Search engine assumes:

```text
more occurrences of query term
=
document talks more about topic
```

This idea is called:

```text
Term Frequency (TF)
```

---

# 1. One-Line Definition

```text
Term Frequency measures how many times a term appears inside a document.
```

---

# 2. Biggest Mental Model

Search engine mental model:

```text
If document repeatedly mentions something,
it is probably important in that document.
```

Example:

```text
redis redis redis caching
```

Document likely about:

```text
redis
```

---

# 3. Why Ranking Needed

Without ranking:

```text
all matching documents appear equal
```

Bad search experience.

Search engines need:

```text
relevance scoring
```

---

# 4. Ranking ASCII Diagram

```text
Query:
redis

      ↓

Doc1 → redis redis redis caching
Doc2 → redis tutorial
Doc3 → database systems

      ↓

Compute Scores

      ↓

Rank Results
```

---

# 5. Raw Term Frequency

Simplest formula:

```text
TF(term, doc)
=
count of term inside document
```

---

# 6. Raw TF Example

Document:

```text
redis redis kafka redis
```

Term counts:

```text
redis → 3
kafka → 1
```

---

# 7. Why Raw TF Useful

Repeated terms usually indicate:

```text
higher topical importance
```

Example:

```text
redis redis redis caching
```

is more related to:

```text
redis
```

than:

```text
redis tutorial
```

---

# 8. Problem With Raw TF

Long documents naturally contain more words.

Example:

```text
10000-word article
```

may contain many repetitions simply because:

```text
document is huge
```

Need normalization.

---

# 9. Normalized TF

Common normalization:

```text
TF =
term_count / total_terms
```

Balances:

```text
term importance
vs
document length
```

---

# 10. Normalized TF Example

Document:

```text
redis redis kafka redis
```

Values:

```text
redis count = 3
total terms = 4
```

Normalized TF:

```text
3 / 4 = 0.75
```

---

# 11. Normalization ASCII

```text
redis redis kafka redis

redis count:
3

total terms:
4

TF:
3 / 4 = 0.75
```

---

# 12. Keyword Stuffing Problem

Bad webpages may repeat:

```text
redis redis redis redis redis
```

just to manipulate rankings.

This is called:

```text
keyword stuffing
```

---

# 13. TF Saturation

Modern search engines use:

```text
diminishing returns
```

Meaning:

```text
10 occurrences
is NOT 10x more useful than 1 occurrence
```

---

# 14. TF Saturation ASCII

```text
TF increases
      ↓
Score increases slower
```

---

# 15. Why TF Alone Insufficient

High TF does NOT always mean quality.

Spam pages may abuse repetition.

Modern systems combine TF with:

```text
IDF
BM25
PageRank
click signals
quality signals
```

---

# 16. Document Frequency (DF)

Document Frequency means:

```text
how many documents contain term
```

Example:

```text
redis → 100 docs
the → 100 million docs
```

---

# 17. Why Rare Terms Important

Rare terms are more informative.

Example:

```text
redis
```

Very specific.

Example:

```text
the
```

Not useful.

---

# 18. Inverse Document Frequency (IDF)

IDF reduces importance of common terms.

Core idea:

```text
rare terms get higher weight
common terms get lower weight
```

---

# 19. TF-IDF Intuition

Classic ranking formula:

```text
TF-IDF
```

Meaning:

```text
high local importance
+
high global rarity
```

---

# 20. TF-IDF ASCII

```text
High TF
+
Low DF
=
High Relevance
```

---

# 21. Query Execution Flow

```text
Query
   ↓
Load Posting Lists
   ↓
Read Frequencies
   ↓
Compute TF
   ↓
Apply IDF
   ↓
Generate Scores
   ↓
Sort Results
```

---

# 22. Posting Lists Store TF

Posting lists contain frequencies directly.

Example:

```text
redis →

[
  {doc=1, freq=3},
  {doc=2, freq=1}
]
```

---

# 23. Why TF Stored In Posting Lists

Without stored TF:

```text
search engine must rescan documents
```

Too expensive.

Instead:

```text
frequencies precomputed during indexing
```

---

# 24. Lucene Mental Model

Lucene stores:

```text
docIDs
frequencies
positions
offsets
```

inside postings.

---

# 25. BM25

Modern Lucene primarily uses:

```text
BM25 ranking
```

BM25 improves TF-IDF by adding:

```text
TF saturation
document normalization
better relevance behavior
```

---

# 26. Multi-Term Query

Query:

```text
redis caching
```

Documents:

```text
Doc1:
redis redis caching fast

Doc2:
redis tutorial
```

Doc1 ranks higher because:

```text
contains both terms
+
higher TF
```

---

# 27. Multi-Term ASCII

```text
Query:
redis caching

Score =
redis score
+
caching score
```

---

# 28. Field Weighting

Different fields have different importance.

Example:

```text
title
body
tags
description
```

Title matches usually weighted higher.

---

# 29. Field Weight ASCII

```text
Title Match
    ↓
Higher Score

Body Match
    ↓
Lower Score
```

---

# 30. Phrase Matching Bonus

Exact phrase queries often receive bonus score.

Example:

```text
"redis caching"
```

Phrase match:

```text
higher relevance
```

---

# 31. Search Ranking Pipeline

```text
Query
   ↓
Retrieve Posting Lists
   ↓
Read TF values
   ↓
Apply Ranking Formula
   ↓
Generate Scores
   ↓
Sort Results
```

---

# 32. Memory Layout Mental Model

```text
Posting List
 ├── DocIDs
 ├── Frequencies
 ├── Positions
 └── Metadata
```

---

# 33. Segment Mental Model

Lucene stores indexes as:

```text
immutable segments
```

Each segment contains:

```text
its own TF statistics
```

---

# 34. Segment ASCII

```text
Index
 ├── Segment1
 ├── Segment2
 └── Segment3
```

---

# 35. Distributed Search

Elasticsearch distributes indexes into:

```text
shards
```

Each shard computes local scores.

Coordinator merges ranked results.

---

# 36. Distributed Search ASCII

```text
Query
   ↓
Coordinator
   ↓
Search Shard1
Search Shard2
Search Shard3
   ↓
Merge Results
```

---

# 37. Java TF Counter

```java
import java.util.HashMap;
import java.util.Map;

public class TermFrequencyCounter {

    public Map<String, Integer>
    computeTF(String text) {

        Map<String, Integer> tf =
                new HashMap<>();

        String[] tokens =
                text.toLowerCase()
                    .split("\\s+");

        for (String token : tokens) {

            tf.put(
                    token,
                    tf.getOrDefault(token, 0) + 1
            );
        }

        return tf;
    }
}
```

---

# 38. Java TF Dry Run

Input:

```text
redis redis kafka redis
```

Execution:

Step 1:

```text
redis → 1
```

Step 2:

```text
redis → 2
```

Step 3:

```text
kafka → 1
```

Step 4:

```text
redis → 3
```

Final map:

```text
redis → 3
kafka → 1
```

---

# 39. Java Normalized TF

```java
public double normalizedTF(
        int termCount,
        int totalTerms) {

    return (double) termCount
            / totalTerms;
}
```

---

# 40. Normalized TF Dry Run

Document:

```text
redis redis kafka redis
```

Values:

```text
redis count = 3
total terms = 4
```

Result:

```text
3 / 4 = 0.75
```

---

# 41. Java TF-IDF Example

```java
public double tfidf(
        double tf,
        double idf) {

    return tf * idf;
}
```

---

# 42. TF-IDF Dry Run

Suppose:

```text
TF = 0.75
IDF = 2.0
```

Score:

```text
0.75 × 2.0 = 1.5
```

Higher score:

```text
higher relevance
```

---

# 43. Production Ranking Signals

Modern systems use MUCH more than TF.

Signals include:

```text
TF
IDF
BM25
freshness
PageRank
click data
semantic embeddings
personalization
```

---

# 44. Performance Considerations

Search engines precompute:

```text
frequencies
document statistics
```

during indexing.

This avoids expensive recalculation during search.

---

# 45. Common Mistakes

## Mistake 1

```text
Using raw TF only
```

Long documents dominate unfairly.

---

## Mistake 2

```text
Ignoring document frequency
```

Common terms become overweighted.

---

## Mistake 3

```text
Assuming repetition always better
```

Can cause spam ranking.

---

## Mistake 4

```text
Ignoring phrase relevance
```

Phrase matches often stronger.

---

# 46. Interview Explanation

If interviewer asks:

```text
Why is term frequency important?
```

Strong answer:

```text
Term Frequency measures how often a term appears inside a document.
Higher TF often indicates stronger topical relevance, so search engines
use TF as one of the core ranking signals.
```

Senior addition:

```text
Modern systems combine TF with IDF or BM25 to avoid bias toward long
documents and keyword stuffing.
```

---

# 47. Final Mental Model

```text
More Relevant Mentions
         ↓
Higher TF
         ↓
Higher Score
```

Balanced with:

```text
document normalization
IDF
BM25 saturation
```

---

# 48. What To Remember

```text
TF measures occurrences inside document.

Higher TF often means higher relevance.

Raw TF alone is insufficient.

Normalization matters.

Rare terms are more useful.

TF-IDF combines local importance + global rarity.

BM25 improves TF-IDF.

Lucene stores TF inside posting lists.
```

---

# 49. Next File

```text
009_TF_IDF_Ranking.md
```

Next you learn:

```text
vector space model
cosine similarity
document vectors
query vectors
ranking mathematics
Lucene scoring intuition
BM25 deeper
```
