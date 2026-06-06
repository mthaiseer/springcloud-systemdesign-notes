# 007_Posting_List.md

# MiniSearchEngine — 007 Posting List

## 0. Why This File Exists

The posting list is one of the MOST important internal structures inside an inverted index.

Search engines do not directly store:

```text
term → documents
```

as simple arrays only.

Real systems store:

```text
document IDs
term frequencies
positions
offsets
skip pointers
compression metadata
```

inside posting lists.

Posting lists power:

```text
Boolean search
phrase search
ranking
highlighting
fast retrieval
distributed search
```

This file teaches:

```text
what posting list is
posting list internals
document frequencies
positions
skip pointers
compression
delta encoding
Boolean retrieval
intersection algorithms
phrase matching
Lucene posting structures
```

Only ASCII diagrams are used.

---

# 1. One-Line Definition

```text
A posting list stores documents containing a specific term.
```

Core structure:

```text
term → posting list
```

Example:

```text
redis → [1,3,8]
```

Meaning:

```text
term "redis"
appears in documents 1,3,8
```

---

# 2. Biggest Mental Model

Inverted index contains:

```text
term dictionary
+
posting lists
```

Term dictionary tells:

```text
which terms exist
```

Posting list tells:

```text
where those terms appear
```

---

# 3. Posting List Mental Model

Suppose documents:

```text
Doc1:
redis caching

Doc2:
kafka messaging

Doc3:
redis persistence
```

Posting list:

```text
redis → [1,3]
```

---

# 4. Posting List ASCII

```text
Term:
redis

Posting List:
[1] → [3]
```

---

# 5. Why Posting Lists Important

Without posting lists:

```text
must scan all documents
```

With posting lists:

```text
jump directly to matching documents
```

Huge performance improvement.

---

# 6. Real Posting List Structure

Real posting lists store more metadata.

Example:

```text
docID
term frequency
positions
offsets
payloads
```

---

# 7. Real Posting Example

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

Doc3:
redis appears once
```

---

# 8. Document Frequency

Document frequency:

```text
number of documents containing term
```

Example:

```text
redis → [1,3,8]
```

Document frequency:

```text
3
```

Used heavily in ranking.

---

# 9. Why Document Frequency Important

Rare terms are more important.

Example:

```text
redis
```

appears in few documents.

More useful.

Common words:

```text
the
is
a
```

appear everywhere.

Less useful.

---

# 10. Term Frequency

Term frequency:

```text
how many times term appears inside document
```

Example:

```text
Doc1:
redis redis caching
```

Frequency:

```text
redis → 2
```

Higher frequency usually increases relevance.

---

# 11. Term Frequency ASCII

```text
Doc1:
redis redis caching

redis count:
2
```

---

# 12. Positions

Positions store exact token locations.

Example:

```text
redis caching fast
```

Positions:

```text
redis   → 1
caching → 2
fast    → 3
```

---

# 13. Why Positions Important

Positions enable:

```text
phrase search
highlighting
proximity queries
```

Without positions:

```text
cannot verify phrase order
```

---

# 14. Phrase Search Example

Phrase query:

```text
"redis caching"
```

Document:

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
positions consecutive
```

---

# 15. Phrase Search ASCII

```text
Doc:
redis caching fast

redis   → 1
caching → 2

Check:
2 = 1 + 1

MATCH
```

---

# 16. Positional Posting List

Structure:

```text
term →
   doc →
      positions
```

---

# 17. Positional Posting ASCII

```text
redis →
   Doc1 → [1,5]
   Doc3 → [2]
```

---

# 18. Offsets

Offsets store character positions.

Example:

```text
Redis caching fast
```

Offset:

```text
redis → start=0 end=5
```

Useful for:

```text
highlighting
snippet generation
```

---

# 19. Highlighting Example

Search query:

```text
redis
```

Result snippet:

```html
<b>Redis</b> caching fast
```

Offsets help locate exact substring.

---

# 20. Posting List Construction Flow

```text
Document
    ↓
Tokenizer
    ↓
Extract Tokens
    ↓
Generate Postings
    ↓
Append To Posting Lists
```

---

# 21. Posting List Build Example

Documents:

```text
Doc1:
redis caching

Doc2:
redis persistence
```

Step 1:

```text
Doc1 tokens:
redis
caching
```

Step 2:

```text
redis → [1]
caching → [1]
```

Step 3:

```text
Doc2 tokens:
redis
persistence
```

Step 4:

```text
redis → [1,2]
persistence → [2]
```

---

# 22. Sorted Posting Lists

Posting lists usually sorted by:

```text
document ID
```

Example:

```text
redis → [1,3,8,10]
```

Benefits:

```text
fast intersections
compression
merge efficiency
```

---

# 23. Boolean AND Query

Example:

```text
redis AND caching
```

Posting lists:

```text
redis   → [1,3,8]
caching → [1,5,8]
```

Intersection:

```text
[1,8]
```

---

# 24. Boolean AND ASCII

```text
redis   → [1,3,8]
caching → [1,5,8]

Intersect:
[1,8]
```

---

# 25. Boolean OR Query

Example:

```text
redis OR kafka
```

Posting lists:

```text
redis → [1,3]
kafka → [2]
```

Union:

```text
[1,2,3]
```

---

# 26. Boolean NOT Query

Example:

```text
redis NOT kafka
```

Meaning:

```text
documents containing redis
but not kafka
```

---

# 27. Two Pointer Intersection

Sorted posting lists allow efficient intersection.

Algorithm:

```text
compare current docIDs
move smaller pointer
```

Complexity:

```text
O(n + m)
```

---

# 28. Two Pointer Dry Run

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
move A
```

Step 3:

```text
8 > 5
move B
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

# 29. Skip Pointers

Large posting lists use skip pointers.

Purpose:

```text
jump ahead quickly
```

instead of scanning every posting.

---

# 30. Skip Pointer ASCII

```text
[1]—[3]—[5]—[8]—[20]
   \___________/
         skip
```

---

# 31. Why Skip Pointers Important

Without skips:

```text
linear scan
```

With skips:

```text
large sections skipped quickly
```

Improves intersection speed.

---

# 32. Compression Problem

Popular terms appear in huge number of documents.

Example:

```text
database
search
system
```

Posting lists become enormous.

Need compression.

---

# 33. Delta Encoding

Instead of storing:

```text
[10,20,25]
```

store gaps:

```text
[10,10,5]
```

Smaller numbers compress better.

---

# 34. Delta Encoding ASCII

```text
Original:
10 20 25

Gaps:
10 10 5
```

---

# 35. Why Compression Important

Benefits:

```text
smaller disk usage
smaller memory usage
better cache locality
faster IO
```

---

# 36. Posting List Memory Layout

```text
Posting List
 ├── DocIDs
 ├── Frequencies
 ├── Positions
 ├── Skip Data
 └── Compression Metadata
```

---

# 37. Query Retrieval Flow

```text
Query
    ↓
Lookup Term Dictionary
    ↓
Load Posting Lists
    ↓
Boolean Operations
    ↓
Rank Documents
    ↓
Return Results
```

---

# 38. Query Execution Example

Query:

```text
redis caching
```

Step 1:

```text
redis → [1,3,8]
caching → [1,5,8]
```

Step 2:

```text
intersect lists
```

Step 3:

```text
result → [1,8]
```

---

# 39. Phrase Query Execution

Query:

```text
"redis caching"
```

Step 1:

```text
retrieve positions
```

Step 2:

```text
check consecutive positions
```

Step 3:

```text
return matching docs
```

---

# 40. Phrase Query ASCII

```text
redis   → [1]
caching → [2]

Check:
2 = 1 + 1

MATCH
```

---

# 41. Lucene Posting Lists

Apache Lucene stores:

```text
compressed posting lists
positions
frequencies
offsets
skip data
```

Highly optimized.

---

# 42. Elasticsearch Mental Model

Elasticsearch shards contain:

```text
Lucene segments
```

Each segment contains:

```text
term dictionary
posting lists
```

---

# 43. Segment Posting Lists

Each segment maintains independent posting lists.

Example:

```text
Segment1:
redis → [1,3]

Segment2:
redis → [7,9]
```

Query searches all segments.

---

# 44. Segment ASCII

```text
Index
 ├── Segment1
 ├── Segment2
 └── Segment3
```

Each segment:

```text
own posting lists
```

---

# 45. Segment Merge

Too many small segments hurt performance.

Lucene merges segments periodically.

---

# 46. Segment Merge ASCII

```text
Small Segments
      ↓
Merge
      ↓
Larger Segment
```

---

# 47. Distributed Posting Lists

Large search engines distribute indexes across shards.

Example:

```text
Shard1
Shard2
Shard3
```

Each shard contains subset of documents.

---

# 48. Distributed Search ASCII

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

# 49. Java Posting Class

```java
import java.util.List;

public class Posting {

    // Document containing term
    private final int docId;

    // Number of occurrences
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

# 50. Java Posting List Structure

```java
import java.util.*;

public class PostingList {

    private final List<Posting> postings =
            new ArrayList<>();

    public void addPosting(
            Posting posting) {

        postings.add(posting);
    }

    public List<Posting> getPostings() {

        return postings;
    }
}
```

Mental model:

```text
posting list = collection of postings
```

---

# 51. Java Inverted Index

```java
import java.util.*;

public class InvertedIndex {

    // term → posting list
    private final Map<String,
            PostingList> index =
            new HashMap<>();

    public void add(
            String token,
            Posting posting) {

        index.computeIfAbsent(
                token,
                k -> new PostingList()
        );

        index.get(token)
             .addPosting(posting);
    }

    public PostingList search(
            String token) {

        return index.get(token);
    }
}
```

---

# 52. Java Build Dry Run

Document:

```text
Doc1:
redis redis caching
```

Tokens:

```text
redis
redis
caching
```

Computed metadata:

```text
redis →
    freq=2
    positions=[1,2]

caching →
    freq=1
    positions=[3]
```

Inserted into posting lists.

---

# 53. Java Boolean Intersection

```java
import java.util.*;

public class PostingIntersection {

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

# 54. Intersection Dry Run

```text
A = [1,3,8]
B = [1,5,8]
```

Step 1:

```text
1 == 1
add 1
```

Step 2:

```text
3 < 5
move A
```

Step 3:

```text
8 > 5
move B
```

Step 4:

```text
8 == 8
add 8
```

Final:

```text
[1,8]
```

---

# 55. Production Challenges

Real search systems handle:

```text
billions of postings
compressed indexes
real-time updates
segment merges
distributed retrieval
large phrase queries
```

---

# 56. Performance Tradeoffs

More metadata:

```text
better search quality
```

But:

```text
larger posting lists
more memory
more disk usage
```

Positions improve phrase search but increase storage cost.

---

# 57. Common Mistakes

## Mistake 1

```text
Unsorted posting lists
```

Breaks fast intersections.

---

## Mistake 2

```text
Ignoring positions
```

Breaks phrase queries.

---

## Mistake 3

```text
No compression
```

Huge memory usage.

---

## Mistake 4

```text
Linear scanning huge posting lists
```

Need skip pointers and optimized intersections.

---

## Mistake 5

```text
Ignoring frequencies
```

Ranking quality suffers.

---

# 58. Interview Explanation

If interviewer asks:

```text
What is posting list?
```

Strong answer:

```text
A posting list is the list of documents associated with a term inside
an inverted index. Posting lists may also store frequencies, positions,
offsets, and skip data to support ranking and phrase queries efficiently.
```

---

# 59. Final Mental Model

```text
Term Dictionary
      ↓
Posting Lists
      ↓
Document Retrieval
      ↓
Boolean Queries
      ↓
Ranking
```

Posting list is the core retrieval structure inside search engines.

---

# 60. What To Remember

```text
Posting lists store matching documents for terms.

Real postings store frequencies and positions.

Positions enable phrase queries.

Sorted posting lists allow fast intersections.

Skip pointers accelerate retrieval.

Compression reduces storage cost.

Lucene posting lists are highly optimized.

Elasticsearch shards contain Lucene posting lists.
```

---

# 61. Next File

```text
008_Term_Frequency.md
```

Next you learn:

```text
TF calculation
document frequency
inverse document frequency
ranking signals
relevance scoring
TF-IDF basics
why some documents rank higher
```
