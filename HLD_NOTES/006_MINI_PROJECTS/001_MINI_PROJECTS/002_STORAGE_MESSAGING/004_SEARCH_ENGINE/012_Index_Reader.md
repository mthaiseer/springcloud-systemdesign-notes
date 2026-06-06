# 012_Index_Reader.md

# MiniSearchEngine — 012 Index Reader

## 0. Why This File Exists

After Index Writer builds the inverted index:

```text
how are searches actually executed?
```

This is the responsibility of:

```text
Index Reader
```

Index Reader is one of the MOST important components inside:

```text
Lucene
Elasticsearch
Solr
OpenSearch
```

Index Reader is responsible for:

```text
opening index segments
reading term dictionaries
loading posting lists
executing Boolean queries
executing phrase queries
reading positions
computing BM25 scores
retrieving top-k results
loading stored fields
```

Without Index Reader:

```text
search engine cannot search anything
```

---

# 1. One-Line Definition

```text
Index Reader reads inverted indexes and executes searches efficiently.
```

---

# 2. Biggest Mental Model

Search engine architecture:

```text
Documents
    ↓
Index Writer
    ↓
Inverted Index
    ↓
Index Reader
    ↓
Search Results
```

---

# 3. Search Engine Reality

Search engines DO NOT scan raw documents during search.

That would be extremely slow.

Instead:

```text
Index Reader uses inverted indexes
```

to directly jump to matching documents.

---

# 4. Full Search Pipeline

```text
User Query
     ↓
Query Parser
     ↓
Term Lookup
     ↓
Posting Lists
     ↓
Boolean Operations
     ↓
BM25 Scoring
     ↓
Top-K Results
     ↓
Load Stored Fields
     ↓
Return Response
```

---

# 5. Query Example

Suppose documents:

```text
Doc1:
redis redis caching fast

Doc2:
redis tutorial

Doc3:
database systems
```

User query:

```text
redis caching
```

---

# 6. First Step — Query Parsing

Query Parser extracts:

```text
redis
caching
```

These become search terms.

---

# 7. Term Dictionary Lookup

Index Reader searches:

```text
term dictionary
```

to locate posting lists.

---

# 8. Term Dictionary Mental Model

Term dictionary maps:

```text
term → posting list pointer
```

Example:

```text
redis → offset 1024
kafka → offset 2048
```

---

# 9. Term Dictionary ASCII

```text
Term Dictionary
 ├── redis
 ├── kafka
 ├── caching
 ├── database
 └── search
```

---

# 10. Posting List Retrieval

Reader loads posting lists.

Example:

```text
redis →
[1,2]

caching →
[1]
```

---

# 11. Posting List Mental Model

Posting list contains:

```text
matching documents
frequencies
positions
offsets
```

---

# 12. Posting List ASCII

```text
redis →
[
  {doc=1,freq=2},
  {doc=2,freq=1}
]
```

---

# 13. Boolean AND Query

Query:

```text
redis AND caching
```

Reader performs:

```text
posting list intersection
```

---

# 14. Boolean AND ASCII

```text
redis   → [1,2]
caching → [1]

Intersection:
[1]
```

---

# 15. Two Pointer Intersection

Sorted posting lists allow efficient intersection.

Algorithm:

```text
compare doc IDs
move smaller pointer
```

Complexity:

```text
O(n + m)
```

---

# 16. Intersection Dry Run

```text
redis   → [1,2]
caching → [1]

1 == 1 → MATCH
move both

end reached

Result:
[1]
```

---

# 17. Boolean OR Query

Query:

```text
redis OR database
```

Result:

```text
[1,2,3]
```

Union of posting lists.

---

# 18. Boolean NOT Query

Query:

```text
redis NOT kafka
```

Meaning:

```text
documents containing redis
but not kafka
```

---

# 19. Phrase Query

Query:

```text
"redis caching"
```

Requires:

```text
position checking
```

---

# 20. Position Checking

Example document:

```text
redis caching fast
```

Positions:

```text
redis → 1
caching → 2
```

Phrase matches because:

```text
2 = 1 + 1
```

---

# 21. Phrase Query ASCII

```text
redis   → [1]
caching → [2]

Check:
2 = 1 + 1

MATCH
```

---

# 22. Segment Reader

Lucene indexes are divided into:

```text
immutable segments
```

Each segment has:

```text
its own Index Reader
```

---

# 23. Segment Reader ASCII

```text
Index Reader
    ↓
Segment Readers
 ├── Segment1
 ├── Segment2
 └── Segment3
```

---

# 24. Why Segments Important

Segments allow:

```text
safe concurrent reads
fast searching
parallel execution
efficient caching
```

---

# 25. Search Across Segments

Suppose:

```text
Segment1:
redis → [1]

Segment2:
redis → [5]
```

Combined results:

```text
[1,5]
```

---

# 26. Multi-Segment ASCII

```text
Segment1
   ↓
Posting Lists

Segment2
   ↓
Posting Lists

Merge Results
```

---

# 27. BM25 Scoring Phase

After matching documents:

```text
compute BM25 relevance scores
```

---

# 28. BM25 Read Flow

```text
Posting Lists
     ↓
Read TF
     ↓
Read Doc Length
     ↓
Compute BM25
     ↓
Generate Score
```

---

# 29. Why Frequencies Important

Posting lists store:

```text
term frequency
```

Reader uses frequency for:

```text
BM25 scoring
```

---

# 30. Why Positions Important

Positions enable:

```text
phrase search
proximity search
highlighting
```

---

# 31. Stored Fields

Search results often return:

```text
title
description
metadata
```

These are called:

```text
stored fields
```

---

# 32. Stored Field Retrieval

```text
Matching DocIDs
      ↓
Load Stored Fields
      ↓
Build Search Response
```

---

# 33. Top-K Retrieval

Search engines usually return:

```text
top 10
top 20
top 100
```

NOT all matching documents.

---

# 34. Priority Queue Mental Model

Reader maintains:

```text
highest scoring documents
```

using:

```text
priority queue / heap
```

---

# 35. Top-K ASCII

```text
Matching Docs
      ↓
Compute Scores
      ↓
Top-K Heap
      ↓
Best Results
```

---

# 36. Why Heap Important

Without heap:

```text
must fully sort all matches
```

Too expensive.

Heap keeps:

```text
best scoring docs only
```

---

# 37. Search Cache

Readers cache:

```text
frequent queries
posting lists
filter results
```

to improve performance.

---

# 38. Cache ASCII

```text
Repeated Query
      ↓
Cache Hit
      ↓
Fast Response
```

---

# 39. Near Real-Time Search

Lucene supports:

```text
near real-time search
```

New segments become searchable quickly.

---

# 40. Reader Refresh

New segments require:

```text
reader refresh
```

before becoming visible.

---

# 41. Refresh ASCII

```text
New Segment
     ↓
Refresh
     ↓
Searchable
```

---

# 42. Distributed Search

Elasticsearch distributes queries across:

```text
shards
```

Each shard has:

```text
its own Index Reader
```

---

# 43. Distributed Search ASCII

```text
Query
   ↓
Coordinator Node
   ↓
Shard1 Reader
Shard2 Reader
Shard3 Reader
   ↓
Merge Results
```

---

# 44. Query Coordination

Coordinator node:

```text
collects shard results
merges rankings
returns final top-k
```

---

# 45. Compression

Posting lists compressed to reduce:

```text
disk usage
memory usage
IO cost
```

Reader decompresses during search.

---

# 46. Compression ASCII

```text
Compressed Postings
         ↓
Decompression
         ↓
Query Execution
```

---

# 47. Concurrent Readers

Thousands of queries may run simultaneously.

Immutable segments make concurrent reads safe.

---

# 48. Concurrent Search ASCII

```text
User1 Query
User2 Query
User3 Query
      ↓
Index Reader
```

---

# 49. Java Posting Class

```java
public class Posting {

    private final int docId;

    private final int frequency;

    public Posting(
            int docId,
            int frequency) {

        this.docId = docId;
        this.frequency = frequency;
    }

    public int getDocId() {
        return docId;
    }

    public int getFrequency() {
        return frequency;
    }
}
```

---

# 50. Java Posting List

```java
import java.util.List;

public class PostingList {

    private final List<Posting> postings;

    public PostingList(
            List<Posting> postings) {

        this.postings = postings;
    }

    public List<Posting> getPostings() {

        return postings;
    }
}
```

---

# 51. Java Inverted Index

```java
import java.util.*;

public class InvertedIndex {

    private final Map<String,
            PostingList> index =
            new HashMap<>();

    public void add(
            String term,
            PostingList postingList) {

        index.put(term, postingList);
    }

    public PostingList getPostingList(
            String term) {

        return index.get(term);
    }
}
```

---

# 52. Java Index Reader

```java
public class MiniIndexReader {

    private final InvertedIndex index;

    public MiniIndexReader(
            InvertedIndex index) {

        this.index = index;
    }

    public PostingList search(
            String term) {

        return index.getPostingList(term);
    }
}
```

---

# 53. Java Search Dry Run

Index:

```text
redis →
[
 {doc=1,freq=2},
 {doc=2,freq=1}
]
```

Query:

```text
redis
```

Reader retrieves:

```text
Doc1
Doc2
```

---

# 54. Java Boolean AND

```java
import java.util.*;

public class BooleanAndSearch {

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

# 55. Boolean AND Dry Run

```text
redis   → [1,2]
caching → [1]

1 == 1 → MATCH

Result:
[1]
```

---

# 56. Java Top-K Heap

```java
import java.util.PriorityQueue;

public class TopKRetriever {

    public void demo() {

        PriorityQueue<Double> heap =
                new PriorityQueue<>();

        heap.add(2.5);
        heap.add(7.2);
        heap.add(1.1);
    }
}
```

---

# 57. Why Lucene Fast

Lucene performance comes from:

```text
compressed postings
immutable segments
efficient readers
fast BM25 scoring
segment caching
```

---

# 58. Production Challenges

Real systems handle:

```text
billions of documents
distributed queries
large posting lists
cache coordination
concurrent readers
```

---

# 59. Reader Tradeoffs

Aggressive caching:

```text
faster queries
higher memory usage
```

Weak caching:

```text
slower searches
less memory
```

Need balance.

---

# 60. Lucene + Elasticsearch Mental Model

```text
Elasticsearch
      ↓
Distributed Coordination
      ↓
Lucene Readers
      ↓
Posting Lists
      ↓
BM25 Ranking
```

---

# 61. Common Mistakes

## Mistake 1

```text
Thinking search scans raw documents
```

Reader uses inverted indexes.

---

## Mistake 2

```text
Ignoring segment readers
```

Lucene searches segment-by-segment.

---

## Mistake 3

```text
Ignoring top-k optimization
```

Heap retrieval critical.

---

## Mistake 4

```text
Thinking Elasticsearch implements search engine itself
```

Lucene performs core search execution.

---

# 62. Interview Explanation

If interviewer asks:

```text
What does Index Reader do?
```

Strong answer:

```text
Index Reader executes searches by reading term dictionaries,
posting lists, frequencies, and positions from Lucene segments.
It retrieves matching documents and computes relevance scores.
```

Senior addition:

```text
Lucene uses segment readers and immutable segments to support
highly concurrent low-latency search execution.
```

---

# 63. Final Mental Model

```text
Query
   ↓
Term Lookup
   ↓
Posting Lists
   ↓
BM25 Scoring
   ↓
Top Results
```

Index Reader is the HEART of search execution.

---

# 64. What To Remember

```text
Index Reader executes searches.

Readers load posting lists.

Lucene uses segment readers.

Boolean queries use posting intersections.

Phrase search uses positions.

BM25 scoring happens during reads.

Top-k retrieval uses heaps.

Elasticsearch shards contain Lucene readers.
```

---

# 65. Next File

```text
013_Query_Parser.md
```

Next you learn:

```text
how raw user queries become executable queries
Boolean parsing
phrase parsing
query trees
Lucene query objects
search syntax internals
```
