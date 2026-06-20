# 006_Inverted_Index.md

# MiniSearchEngine — 006 Inverted Index

## 0. Why This File Exists

The inverted index is the MOST important data structure in modern search engines.

Systems like:

- Elasticsearch
- Lucene
- Solr
- OpenSearch

all revolve around:

```text
inverted indexes
```

Without inverted index:

```text
search engine must scan every document
for every query
```

This becomes impossible at large scale.

Suppose:

```text
1 billion documents
```

Scanning every document for every search query would be extremely slow.

Inverted index solves this by converting:

```text
document → words
```

into:

```text
word → documents
```

This file teaches:

```text
what inverted index is
why it exists
term dictionary
posting lists
positions
frequencies
Boolean retrieval
phrase search
intersection algorithms
compression
skip lists
Lucene internals
Elasticsearch internals
query execution
distributed inverted indexes
```

Only ASCII diagrams are used.

---

# 1. One-Line Definition

```text
An inverted index maps searchable terms to documents containing those terms.
```

Core structure:

```text
term → posting list
```

Example:

```text
redis → [1,3]
```

Meaning:

```text
term "redis"
appears in document 1 and 3
```

---

# 2. Biggest Mental Model

Traditional storage:

```text
document → terms
```

Search engine storage:

```text
term → documents
```

The relationship becomes inverted.

That is why the structure is called:

```text
inverted index
```

---

# 3. Traditional Document Storage

Suppose documents:

```text
Doc1:
redis caching fast

Doc2:
kafka messaging

Doc3:
redis persistence
```

Traditional representation:

```text
Doc1 → redis caching fast
Doc2 → kafka messaging
Doc3 → redis persistence
```

Problem:

```text
to search "redis"
must scan every document
```

---

# 4. Naive Search Problem

Query:

```text
redis
```

Execution:

```text
read Doc1
read Doc2
read Doc3
read Doc4
read Doc5
...
```

Complexity becomes:

```text
O(number_of_documents)
```

Very expensive.

---

# 5. Naive Search ASCII

```text
Query:
redis

     ↓

Scan Doc1
Scan Doc2
Scan Doc3
Scan Doc4
Scan Doc5
...
```

Slow at scale.

---

# 6. Inverted Index Solution

Instead store:

```text
redis → [1,3]
kafka → [2]
caching → [1]
```

Now search becomes:

```text
direct lookup
```

instead of scanning all documents.

---

# 7. Inverted Index ASCII

```text
                Inverted Index

redis       → [1,3]
caching     → [1]
kafka       → [2]
messaging   → [2]
persistence → [3]
```

---

# 8. Why Inverted Index Powerful

Instead of scanning millions of documents:

```text
jump directly to matching document IDs
```

Benefits:

```text
fast retrieval
scalable search
efficient Boolean queries
phrase search
ranking
```

---

# 9. Main Components

Real inverted index contains:

```text
term dictionary
posting lists
document IDs
positions
frequencies
offsets
skip pointers
compression metadata
```

---

# 10. Term Dictionary

Term dictionary stores:

```text
all searchable terms
```

Example:

```text
redis
kafka
database
search
```

Usually stored in sorted structure.

---

# 11. Term Dictionary ASCII

```text
Term Dictionary

database
kafka
redis
search
```

Used for:

```text
fast term lookup
```

---

# 12. Posting List

Posting list stores:

```text
documents containing term
```

Example:

```text
redis → [1,3,8,10]
```

---

# 13. Posting List ASCII

```text
Term:
redis

Posting List:
[1] → [3] → [8] → [10]
```

---

# 14. Real Posting Object

Real posting contains more metadata.

Example:

```text
docID
frequency
positions
```

---

# 15. Real Posting Example

```text
redis →

[
  {doc=1, freq=2, pos=[1,5]},
  {doc=3, freq=1, pos=[2]}
]
```

Meaning:

```text
Doc1:
redis appears twice
positions 1 and 5

Doc3:
redis appears once
position 2
```

---

# 16. Why Frequencies Important

Term frequency helps ranking.

Document:

```text
redis redis caching
```

Frequency:

```text
redis → 2
```

Usually more occurrences imply stronger relevance.

---

# 17. Why Positions Important

Positions enable:

```text
phrase search
highlighting
proximity queries
```

Without positions:

```text
cannot verify exact phrase order
```

---

# 18. Phrase Search Example

Document:

```text
redis caching fast
```

Positions:

```text
redis   → 1
caching → 2
fast    → 3
```

Phrase query:

```text
"redis caching"
```

matches because:

```text
positions consecutive
```

---

# 19. Phrase Search ASCII

```text
Doc1:
redis caching fast

redis   → 1
caching → 2

Query:
"redis caching"

Check:
1 followed by 2

MATCH
```

---

# 20. Inverted Index Build Flow

```text
Document
    ↓
Tokenizer
    ↓
Normalization
    ↓
Extract Terms
    ↓
Update Posting Lists
    ↓
Build Inverted Index
```

---

# 21. Full Build Example

Documents:

```text
Doc1:
redis caching

Doc2:
kafka messaging

Doc3:
redis persistence
```

---

# 22. Step 1 — Tokenization

Doc1:

```text
redis
caching
```

Doc2:

```text
kafka
messaging
```

Doc3:

```text
redis
persistence
```

---

# 23. Step 2 — Insert Into Index

```text
redis → [1]
caching → [1]
```

Then:

```text
kafka → [2]
messaging → [2]
```

Then:

```text
redis → [1,3]
persistence → [3]
```

---

# 24. Final Inverted Index

```text
redis       → [1,3]
caching     → [1]
kafka       → [2]
messaging   → [2]
persistence → [3]
```

---

# 25. Query Execution Flow

Query:

```text
redis
```

Execution:

```text
lookup term dictionary
      ↓
find posting list
      ↓
retrieve matching docs
      ↓
rank documents
      ↓
return top results
```

---

# 26. Query Execution ASCII

```text
Query:
redis

      ↓

Term Dictionary
      ↓

Posting List:
[1,3]

      ↓

Fetch Documents
      ↓

Return Results
```

---

# 27. Multi-Term Query

Query:

```text
redis caching
```

Execution:

```text
redis   → [1,3]
caching → [1]

INTERSECTION

Result:
[1]
```

---

# 28. Boolean Retrieval

AND query:

```text
redis AND caching
```

Uses:

```text
posting list intersection
```

OR query:

```text
redis OR kafka
```

Uses:

```text
posting list union
```

NOT query:

```text
redis NOT kafka
```

Uses:

```text
difference operation
```

---

# 29. Posting List Intersection

Example:

```text
redis   → [1,3,8]
caching → [1,5,8]
```

Intersection:

```text
[1,8]
```

Documents containing BOTH terms.

---

# 30. Intersection ASCII

```text
A:
[1,3,8]

B:
[1,5,8]

      ↓

Intersection:
[1,8]
```

---

# 31. Why Posting Lists Sorted

Posting lists usually sorted by:

```text
docID
```

Benefits:

```text
fast intersection
better compression
efficient merges
```

---

# 32. Two Pointer Intersection

Algorithm:

```text
compare current docIDs
move smaller pointer
```

Complexity:

```text
O(n + m)
```

Very efficient.

---

# 33. Two Pointer Dry Run

```text
A = [1,3,8]
B = [1,5,8]
```

Step 1:

```text
1 == 1
MATCH
```

Step 2:

```text
3 < 5
move A pointer
```

Step 3:

```text
8 > 5
move B pointer
```

Step 4:

```text
8 == 8
MATCH
```

Result:

```text
[1,8]
```

---

# 34. Skip Pointers

Large posting lists use skip pointers.

Purpose:

```text
jump over sections quickly
```

---

# 35. Skip Pointer ASCII

```text
[1]—[3]—[5]—[8]—[20]
   \___________/
         skip
```

Useful for faster intersections.

---

# 36. Compression Problem

Popular terms appear in huge number of documents.

Example:

```text
database
system
search
```

Posting lists become massive.

Need compression.

---

# 37. Gap Encoding

Instead of:

```text
[10,20,25]
```

store gaps:

```text
[10,10,5]
```

Smaller numbers compress better.

---

# 38. Gap Encoding ASCII

```text
Original:
10 20 25

Gaps:
10 10 5
```

---

# 39. Positional Inverted Index

Stores positions alongside document IDs.

Structure:

```text
term →
    doc →
        positions
```

---

# 40. Positional Index ASCII

```text
redis →
   Doc1 → [1,5]
   Doc3 → [2]
```

Used for:

```text
phrase queries
```

---

# 41. Phrase Query Execution

Query:

```text
"redis caching"
```

Step 1:

```text
retrieve positions for redis
retrieve positions for caching
```

Step 2:

```text
check consecutive positions
```

---

# 42. Phrase Query Dry Run

Document:

```text
redis caching fast
```

Positions:

```text
redis → [1]
caching → [2]
```

Check:

```text
2 = 1 + 1
```

Phrase matched.

---

# 43. Memory Layout Mental Model

```text
Term Dictionary
      ↓
Posting List Pointers
      ↓
Compressed Posting Blocks
```

---

# 44. Disk Layout Mental Model

```text
Segment File
 ├── Term Dictionary
 ├── Posting Lists
 ├── Frequencies
 ├── Positions
 └── Metadata
```

---

# 45. Segment Mental Model

Lucene stores index in immutable:

```text
segments
```

Each segment contains its own inverted index.

---

# 46. Segment ASCII

```text
Index
 ├── Segment-1
 ├── Segment-2
 └── Segment-3
```

Each segment:

```text
independent inverted index
```

---

# 47. Segment Merge

New documents create new segments.

Too many segments hurt performance.

Lucene periodically merges segments.

---

# 48. Segment Merge ASCII

```text
Small Segments
      ↓
Merge Operation
      ↓
Larger Segment
```

---

# 49. Distributed Search

Large systems shard indexes.

Example:

```text
Shard1
Shard2
Shard3
```

Each shard contains subset of documents.

---

# 50. Distributed Search ASCII

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

# 51. Lucene Mental Model

Lucene internally stores:

```text
term dictionary
posting lists
frequencies
positions
segments
```

Everything revolves around inverted indexes.

---

# 52. Elasticsearch Mental Model

Elasticsearch:

```text
distributed search layer
+
Lucene indexes
```

Each shard internally contains Lucene segments.

---

# 53. Why Inverted Index Core

Most search features depend on it:

```text
full-text search
phrase search
autocomplete
ranking
highlighting
fuzzy search
```

---

# 54. Java Posting Class

```java
import java.util.List;

public class Posting {

    // Document ID containing term
    private final int docId;

    // How many times term appears
    private final int frequency;

    // Exact token positions
    private final List<Integer> positions;

    public Posting(
            int docId,
            int frequency,
            List<Integer> positions) {

        this.docId = docId;
        this.frequency = frequency;
        this.positions = positions;
    }

    public int getDocId() {
        return docId;
    }

    public int getFrequency() {
        return frequency;
    }

    public List<Integer> getPositions() {
        return positions;
    }
}
```

---

# 55. Java Inverted Index Structure

```java
import java.util.*;

public class InvertedIndex {

    // term → posting list
    private final Map<String,
            List<Posting>> index =
            new HashMap<>();

    public Map<String,
            List<Posting>> getIndex() {

        return index;
    }
}
```

Mental model:

```text
term → posting list
```

---

# 56. Java Simple Index Builder

```java
import java.util.*;

public class IndexBuilder {

    public Map<String, List<Integer>>
    buildIndex(Map<Integer, String> docs) {

        Map<String, List<Integer>> index =
                new HashMap<>();

        for (Map.Entry<Integer, String> entry
                : docs.entrySet()) {

            int docId = entry.getKey();

            String text =
                    entry.getValue()
                         .toLowerCase();

            String[] tokens =
                    text.split("\\s+");

            for (String token : tokens) {

                index.computeIfAbsent(
                        token,
                        k -> new ArrayList<>()
                );

                index.get(token).add(docId);
            }
        }

        return index;
    }
}
```

---

# 57. Java Build Dry Run

Documents:

```text
1 → "redis caching"
2 → "kafka messaging"
3 → "redis persistence"
```

Step 1:

```text
tokenize documents
```

Step 2:

```text
redis → [1]
caching → [1]
```

Step 3:

```text
kafka → [2]
messaging → [2]
```

Step 4:

```text
redis → [1,3]
persistence → [3]
```

Final index:

```text
redis → [1,3]
caching → [1]
kafka → [2]
messaging → [2]
persistence → [3]
```

---

# 58. Java Search Example

```java
import java.util.*;

public class SearchEngine {

    public List<Integer> search(
            Map<String, List<Integer>> index,
            String token) {

        return index.getOrDefault(
                token.toLowerCase(),
                Collections.emptyList()
        );
    }
}
```

---

# 59. Java Search Dry Run

Query:

```text
redis
```

Execution:

```text
lookup map key:
redis
```

Result:

```text
[1,3]
```

Matching documents:

```text
Doc1
Doc3
```

---

# 60. Production Challenges

Real search engines handle:

```text
billions of documents
compressed indexes
distributed shards
real-time indexing
segment merges
concurrent writes
large posting lists
```

---

# 61. Real-Time Indexing Problem

Search engines continuously update indexes.

Challenges:

```text
concurrent writes
memory flushing
segment creation
replication
refresh latency
```

---

# 62. Search Quality Dependence

Search quality depends heavily on:

```text
good tokenization
good normalization
good inverted index design
```

Bad tokens produce bad search results.

---

# 63. Common Mistakes

## Mistake 1

```text
Scanning all docs per query
```

Need inverted indexes.

---

## Mistake 2

```text
Ignoring positions
```

Breaks phrase queries.

---

## Mistake 3

```text
Unsorted posting lists
```

Intersection becomes slower.

---

## Mistake 4

```text
No compression
```

Posting lists become huge.

---

## Mistake 5

```text
Ignoring frequencies
```

Ranking quality suffers.

---

# 64. Interview Explanation

If interviewer asks:

```text
What is inverted index?
```

Strong answer:

```text
An inverted index is a search data structure mapping terms to posting
lists containing documents where those terms appear. It enables fast
full-text retrieval without scanning all documents.
```

Senior addition:

```text
Posting lists may also contain term frequencies and positions to support
ranking and phrase search efficiently.
```

---

# 65. Final Mental Model

```text
Documents
    ↓
Tokenization
    ↓
Terms
    ↓
Posting Lists
    ↓
Inverted Index
    ↓
Fast Retrieval
```

Core structure:

```text
term → documents
```

This is the HEART of modern search engines.

---

# 66. What To Remember

```text
Inverted index maps terms to documents.

Posting lists contain matching docIDs.

Positions enable phrase search.

Frequencies enable ranking.

Sorted posting lists improve intersections.

Compression reduces storage cost.

Lucene revolves around inverted indexes.

Elasticsearch shards Lucene indexes.
```

---

# 67. Next File

```text
007_Posting_List.md
```

Next you learn:

```text
posting list internals
document frequencies
skip lists
compression
delta encoding
positional indexes
Boolean retrieval optimization
fast intersection algorithms
```
