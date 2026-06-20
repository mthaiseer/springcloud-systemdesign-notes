# 009_TF_IDF_Ranking.md

# MiniSearchEngine — 009 TF-IDF Ranking

## 0. Why This File Exists

Search engines do NOT simply retrieve documents.

They must answer:

```text
Which matching documents are MOST relevant?
```

Suppose query:

```text
redis caching
```

Documents:

```text
Doc1:
redis redis caching fast

Doc2:
redis tutorial

Doc3:
database systems
```

All documents may partially match.

But:

```text
Doc1 is clearly more relevant
```

because:

```text
contains BOTH query terms
higher term frequency
terms more specific
```

Search engines need a ranking algorithm.

One of the most important classical ranking algorithms is:

```text
TF-IDF
```

This file teaches:

```text
TF-IDF intuition
ranking theory
vector space model
document vectors
query vectors
cosine similarity
relevance scoring
IDF mathematics
multi-term scoring
field boosts
phrase boosts
Lucene scoring intuition
BM25 transition
```

Only ASCII diagrams are used.

---

# 1. One-Line Definition

```text
TF-IDF ranks documents using:
local term importance
+
global term rarity
```

---

# 2. Biggest Mental Model

Good search results usually contain:

```text
terms appearing frequently in document
BUT
not frequently everywhere
```

---

# 3. TF-IDF Full Form

```text
TF  → Term Frequency
IDF → Inverse Document Frequency
```

Combined:

```text
TF-IDF
```

---

# 4. Why TF Alone Insufficient

Suppose query:

```text
the
```

Document:

```text
the the the the
```

High TF.

But not meaningful.

Need:

```text
global rarity measurement
```

---

# 5. Why IDF Needed

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

Very common.

Search engine should prioritize:

```text
rare meaningful terms
```

---

# 6. TF-IDF Mental Model

```text
Important term inside document
+
Rare term globally
=
Strong relevance signal
```

---

# 7. TF-IDF ASCII

```text
High TF
+
Low Document Frequency
=
High Relevance
```

---

# 8. Term Frequency Refresher

TF measures:

```text
how many times term appears in document
```

Example:

```text
redis redis caching
```

TF:

```text
redis → 2
```

---

# 9. Document Frequency Refresher

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

# 10. Inverse Document Frequency

IDF reduces weight of common terms.

Core idea:

```text
rare terms receive larger score
```

---

# 11. Simplified IDF Formula

Simplified intuition:

```text
IDF =
log(total_docs / docs_containing_term)
```

---

# 12. IDF Example

Suppose:

```text
1 million documents
```

Term:

```text
redis
```

appears in:

```text
100 docs
```

IDF becomes high.

---

# 13. Common Word Example

Term:

```text
the
```

appears in:

```text
950,000 docs
```

IDF becomes tiny.

---

# 14. Why Log Used

Without logarithm:

```text
scores explode too much
```

Logarithm smooths growth.

---

# 15. TF-IDF Formula

Simplified:

```text
TF-IDF = TF × IDF
```

---

# 16. TF-IDF Example

Suppose:

```text
TF(redis) = 3
IDF(redis) = 2
```

Score:

```text
3 × 2 = 6
```

---

# 17. Why TF-IDF Powerful

TF-IDF combines:

```text
local importance
+
global rarity
```

Very effective classical ranking signal.

---

# 18. Vector Space Model

TF-IDF introduces:

```text
vector representation of documents
```

Documents become mathematical vectors.

---

# 19. Vector Mental Model

Each term becomes a dimension.

Example vocabulary:

```text
redis
kafka
database
```

Document vector:

```text
[3,1,0]
```

Meaning:

```text
redis=3
kafka=1
database=0
```

---

# 20. Vector ASCII

```text
Vocabulary:
[redis, kafka, database]

Doc Vector:
[3,1,0]
```

---

# 21. Query Vector

Queries also become vectors.

Query:

```text
redis kafka
```

Vector:

```text
[1,1,0]
```

---

# 22. Why Vector Model Useful

Now search becomes:

```text
vector similarity problem
```

instead of plain keyword matching.

---

# 23. Cosine Similarity

Most famous similarity metric:

```text
cosine similarity
```

Measures angle between vectors.

---

# 24. Cosine Similarity Mental Model

Similar vectors:

```text
small angle
high similarity
```

Different vectors:

```text
large angle
low similarity
```

---

# 25. Cosine Similarity ASCII

```text
Similar Direction
       ↓
Higher Similarity
```

---

# 26. Search Ranking Flow

```text
Query
   ↓
Convert Query To Vector
   ↓
Convert Docs To Vectors
   ↓
Compute Similarity
   ↓
Rank Documents
```

---

# 27. Multi-Term Query Example

Query:

```text
redis caching
```

Documents:

```text
Doc1:
redis redis caching

Doc2:
redis tutorial

Doc3:
database systems
```

Likely ranking:

```text
Doc1
Doc2
Doc3
```

because:

```text
Doc1 matches both terms
```

---

# 28. Multi-Term Scoring

Total score roughly combines:

```text
TF-IDF(redis)
+
TF-IDF(caching)
```

---

# 29. Multi-Term ASCII

```text
Final Score =
redis score
+
caching score
```

---

# 30. Why TF-IDF Better Than Plain TF

Plain TF:

```text
common words dominate
```

TF-IDF:

```text
rare meaningful terms prioritized
```

---

# 31. Field Weighting

Different fields may have different importance.

Example:

```text
title
body
tags
description
```

Title matches often weighted higher.

---

# 32. Field Weight ASCII

```text
Title Match
    ↓
Higher Score

Body Match
    ↓
Lower Score
```

---

# 33. Phrase Matching Bonus

Phrase queries may receive additional score.

Example:

```text
"redis caching"
```

Phrase match:

```text
higher relevance
```

---

# 34. Phrase Bonus ASCII

```text
Exact Phrase Match
        ↓
Higher Score
```

---

# 35. Query Boosting

Users may boost query terms.

Example:

```text
redis^2 kafka
```

Meaning:

```text
redis more important
```

---

# 36. Ranking Pipeline

```text
Query
   ↓
Tokenizer
   ↓
Retrieve Posting Lists
   ↓
Read TF values
   ↓
Compute IDF
   ↓
Generate TF-IDF Scores
   ↓
Sort Results
```

---

# 37. Posting Lists Store TF

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

# 38. Why Precompute Statistics

Without precomputed TF:

```text
must rescan documents during queries
```

Too expensive.

Search engines store statistics during indexing.

---

# 39. Lucene Scoring Mental Model

Older Lucene scoring heavily used:

```text
TF-IDF inspired ranking
```

Modern Lucene mainly uses:

```text
BM25
```

---

# 40. Why BM25 Replaced TF-IDF

TF-IDF has limitations:

```text
keyword stuffing sensitivity
poor normalization
```

BM25 improves:

```text
TF saturation
document normalization
```

---

# 41. TF-IDF Still Important

Even though BM25 dominates:

```text
TF-IDF remains foundational
```

because it teaches:

```text
core search ranking intuition
```

---

# 42. Memory Layout Mental Model

```text
Posting List
 ├── DocIDs
 ├── Frequencies
 ├── Positions
 └── Metadata
```

TF values stored inside postings.

---

# 43. Segment Mental Model

Lucene stores indexes as:

```text
immutable segments
```

Each segment stores:

```text
its own term statistics
```

---

# 44. Distributed Search

Elasticsearch distributes indexes across:

```text
shards
```

Each shard computes local scores.

Coordinator merges results.

---

# 45. Distributed Search ASCII

```text
Query
   ↓
Coordinator
   ↓
Search Shard1
Search Shard2
Search Shard3
   ↓
Merge Ranked Results
```

---

# 46. Java TF Counter

```java
import java.util.HashMap;
import java.util.Map;

public class TFCalculator {

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

# 47. Java TF Dry Run

Input:

```text
redis redis kafka redis
```

Execution:

```text
redis → 1
redis → 2
kafka → 1
redis → 3
```

Final:

```text
redis → 3
kafka → 1
```

---

# 48. Java Simplified IDF

```java
public double computeIDF(
        int totalDocs,
        int docsContainingTerm) {

    return Math.log(
            (double) totalDocs
            / docsContainingTerm
    );
}
```

---

# 49. Java IDF Dry Run

Suppose:

```text
totalDocs = 1000
docsContainingTerm = 10
```

Calculation:

```text
log(1000 / 10)
=
log(100)
```

High IDF.

---

# 50. Java TF-IDF Formula

```java
public double tfidf(
        double tf,
        double idf) {

    return tf * idf;
}
```

---

# 51. TF-IDF Dry Run

Suppose:

```text
TF = 3
IDF = 2
```

Score:

```text
3 × 2 = 6
```

---

# 52. Java Document Vector

```java
import java.util.Map;

public class DocumentVector {

    private final Map<String, Double>
            weights;

    public DocumentVector(
            Map<String, Double> weights) {

        this.weights = weights;
    }

    public Map<String, Double> getWeights() {

        return weights;
    }
}
```

---

# 53. Query Vector Example

Vocabulary:

```text
redis
kafka
database
```

Query:

```text
redis kafka
```

Vector:

```text
[1,1,0]
```

---

# 54. Production Ranking Signals

Modern search engines use MANY signals.

Examples:

```text
TF-IDF
BM25
PageRank
freshness
click-through rate
semantic vectors
personalization
quality signals
```

---

# 55. Search Quality Tradeoffs

Aggressive TF weighting:

```text
spam influence increases
```

Weak TF weighting:

```text
important documents missed
```

Need balance.

---

# 56. Why Semantic Search Emerging

TF-IDF relies on:

```text
exact keyword matching
```

Modern AI search also uses:

```text
embeddings
semantic similarity
vector databases
```

---

# 57. TF-IDF Limitations

TF-IDF cannot understand:

```text
meaning
synonyms
context
semantics
```

Example:

```text
car
automobile
```

Different terms.

---

# 58. BM25 Transition Mental Model

Evolution:

```text
TF
   ↓
TF-IDF
   ↓
BM25
   ↓
Semantic Search
```

---

# 59. Common Mistakes

## Mistake 1

```text
Using raw TF only
```

Long docs dominate unfairly.

---

## Mistake 2

```text
Ignoring document rarity
```

Common terms overweighted.

---

## Mistake 3

```text
Assuming TF-IDF understands semantics
```

It only understands keyword statistics.

---

## Mistake 4

```text
Ignoring phrase matching
```

Phrase queries often stronger.

---

# 60. Interview Explanation

If interviewer asks:

```text
What is TF-IDF?
```

Strong answer:

```text
TF-IDF is a classical ranking algorithm that scores documents using
local term importance (TF) and global term rarity (IDF). It helps
search engines rank documents more relevant to the query.
```

Senior addition:

```text
TF-IDF forms the foundation of vector-space retrieval models and
inspired modern ranking systems like BM25.
```

---

# 61. Final Mental Model

```text
Important Inside Document
+
Rare Across Documents
=
High Relevance
```

---

# 62. What To Remember

```text
TF measures local importance.

IDF measures global rarity.

TF-IDF combines both signals.

Documents become vectors.

Queries become vectors.

Cosine similarity compares vectors.

Lucene evolved from TF-IDF to BM25.

TF-IDF is foundational for search ranking.
```

---

# 63. Next File

```text
010_BM25_Ranking.md
```

Next you learn:

```text
BM25 internals
TF saturation
document length normalization
real Lucene ranking
modern search scoring
why BM25 dominates production search
```
