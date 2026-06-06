# 011_Index_Writer.md

# MiniSearchEngine — 011 Index Writer

## 0. Why This File Exists

Search engines do NOT directly search raw documents.

Before searching:

```text
documents must be indexed
```

This indexing process is handled by:

```text
Index Writer
```

Index Writer is one of the MOST important components inside:

```text
Lucene
Elasticsearch
Solr
OpenSearch
```

It is responsible for:

```text
tokenization
building posting lists
creating segments
writing inverted indexes
flush operations
segment merges
persisting search data
```

This file teaches:

```text
what Index Writer is
indexing pipeline
memory buffers
segments
flush operations
immutable segments
segment merge
Lucene indexing internals
real-time indexing
disk persistence
```

Only ASCII diagrams are used.

---

# 1. One-Line Definition

```text
Index Writer converts documents into searchable inverted indexes.
```

---

# 2. Biggest Mental Model

Search engine flow:

```text
Documents
    ↓
Index Writer
    ↓
Inverted Index
    ↓
Fast Search
```

Without Index Writer:

```text
documents are not searchable
```

---

# 3. What Index Writer Actually Does

Index Writer:

```text
receives documents
tokenizes text
builds posting lists
creates segments
writes indexes to disk
```

---

# 4. Index Writer ASCII

```text
Raw Documents
      ↓
Tokenizer
      ↓
Terms
      ↓
Posting Lists
      ↓
Segments
      ↓
Disk Index
```

---

# 5. Why Index Writer Important

Search engines may handle:

```text
millions of new documents
```

Index Writer must:

```text
index efficiently
avoid blocking search
write safely to disk
```

---

# 6. Indexing Pipeline

Full indexing flow:

```text
Document
   ↓
Analyzer
   ↓
Tokenizer
   ↓
Normalization
   ↓
Generate Terms
   ↓
Build Posting Lists
   ↓
Create Segment
   ↓
Write To Disk
```

---

# 7. Example Document

Document:

```text
Redis caching fast
```

---

# 8. Tokenization Step

Tokenizer output:

```text
redis
caching
fast
```

---

# 9. Posting List Generation

Posting lists:

```text
redis   → Doc1
caching → Doc1
fast    → Doc1
```

---

# 10. Why Segments Exist

Lucene does NOT continuously rewrite one giant index file.

Instead it creates:

```text
immutable segments
```

---

# 11. Segment Mental Model

Each segment is:

```text
small independent inverted index
```

---

# 12. Segment ASCII

```text
Index
 ├── Segment1
 ├── Segment2
 └── Segment3
```

Each segment:

```text
own posting lists
own term dictionary
own metadata
```

---

# 13. Why Immutable Segments Important

Immutable segments simplify:

```text
concurrency
search performance
safe writes
fast reads
```

Search threads can safely read immutable segments.

---

# 14. In-Memory Buffer

Index Writer first writes data into:

```text
memory buffer
```

instead of directly to disk.

---

# 15. Buffer ASCII

```text
Documents
    ↓
Memory Buffer
    ↓
Flush To Segment
```

---

# 16. Why Buffering Important

Direct disk writes for every document would be:

```text
very slow
```

Buffers improve throughput.

---

# 17. Flush Operation

When buffer becomes large:

```text
flush occurs
```

Meaning:

```text
buffer written to disk
as new segment
```

---

# 18. Flush ASCII

```text
Memory Buffer Full
        ↓
Flush
        ↓
New Segment Created
```

---

# 19. Example Flush

Buffer contains:

```text
Doc1
Doc2
Doc3
```

Flush creates:

```text
Segment-1
```

---

# 20. Real-Time Indexing

Search engines support:

```text
near real-time search
```

New segments become searchable quickly.

---

# 21. NRT Mental Model

Documents indexed:

```text
almost immediately searchable
```

without full rebuild.

---

# 22. Segment Explosion Problem

Continuous indexing creates:

```text
many tiny segments
```

Too many segments hurt performance.

---

# 23. Segment Explosion ASCII

```text
Segment1
Segment2
Segment3
Segment4
Segment5
...
```

---

# 24. Segment Merge

Lucene periodically merges segments.

Purpose:

```text
reduce segment count
improve search performance
```

---

# 25. Segment Merge ASCII

```text
Small Segments
      ↓
Merge Operation
      ↓
Larger Segment
```

---

# 26. Merge Example

Before merge:

```text
Segment1
Segment2
Segment3
```

After merge:

```text
SegmentMerged
```

---

# 27. Why Merging Important

Too many segments cause:

```text
more file handles
slower searches
higher memory usage
```

Merging improves efficiency.

---

# 28. Search During Merge

Important:

```text
search still continues during merges
```

Lucene uses immutable segments safely.

---

# 29. Write-Ahead Safety

Index Writer must avoid corruption.

Lucene carefully manages:

```text
flush ordering
commit points
safe persistence
```

---

# 30. Commit Operation

Commit means:

```text
persist index safely
```

After commit:

```text
index survives restart
```

---

# 31. Commit ASCII

```text
Buffer
   ↓
Flush
   ↓
Segment
   ↓
Commit
   ↓
Persistent Index
```

---

# 32. Why Commit Expensive

Commit involves:

```text
disk synchronization
metadata updates
safe persistence
```

Too frequent commits reduce performance.

---

# 33. Lucene Index Files

Lucene creates many files:

```text
term dictionary
posting lists
frequencies
positions
stored fields
metadata
```

---

# 34. Disk Layout Mental Model

```text
Segment
 ├── Terms
 ├── Posting Lists
 ├── Frequencies
 ├── Positions
 ├── Stored Fields
 └── Metadata
```

---

# 35. Analyzer Pipeline

Index Writer uses analyzers.

Example:

```text
Tokenizer
Lowercase filter
Stopword filter
Stemmer
```

---

# 36. Analyzer ASCII

```text
Raw Text
    ↓
Tokenizer
    ↓
Normalization
    ↓
Final Terms
```

---

# 37. Posting List Storage

Index Writer builds:

```text
term → posting lists
```

Example:

```text
redis → [1,3]
```

---

# 38. Why Compression Needed

Posting lists become huge.

Lucene compresses:

```text
docIDs
frequencies
positions
```

to reduce disk usage.

---

# 39. Compression ASCII

```text
Large Posting Lists
        ↓
Compression
        ↓
Smaller Index Files
```

---

# 40. Concurrent Indexing

Multiple threads may index simultaneously.

Index Writer handles:

```text
thread safety
buffer coordination
safe flushes
```

---

# 41. Concurrent Flow ASCII

```text
Thread1 → Documents
Thread2 → Documents
Thread3 → Documents
           ↓
       Index Writer
```

---

# 42. Search vs Index Separation

Important architecture:

```text
Search threads
≠
Index Writer threads
```

Both operate independently.

---

# 43. Elasticsearch Index Writer

Elasticsearch uses Lucene internally.

Each shard contains:

```text
Lucene Index Writer
```

---

# 44. Distributed Indexing

Documents distributed across:

```text
Shard1
Shard2
Shard3
```

Each shard independently indexes data.

---

# 45. Distributed Indexing ASCII

```text
Documents
    ↓
Routing
    ↓
Shard Selection
    ↓
Local Index Writer
```

---

# 46. Java Document Model

```java
public class Document {

    private final int id;

    private final String text;

    public Document(
            int id,
            String text) {

        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
```

---

# 47. Java Tokenizer

```java
import java.util.Arrays;
import java.util.List;

public class Tokenizer {

    public List<String> tokenize(
            String text) {

        return Arrays.asList(
                text.toLowerCase()
                    .split("\\s+")
        );
    }
}
```

---

# 48. Java Index Writer Skeleton

```java
import java.util.*;

public class MiniIndexWriter {

    // term → posting list
    private final Map<String,
            List<Integer>> index =
            new HashMap<>();

    private final Tokenizer tokenizer =
            new Tokenizer();

    public void addDocument(
            Document doc) {

        List<String> tokens =
                tokenizer.tokenize(
                        doc.getText()
                );

        for (String token : tokens) {

            index.computeIfAbsent(
                    token,
                    k -> new ArrayList<>()
            );

            index.get(token)
                 .add(doc.getId());
        }
    }

    public Map<String,
            List<Integer>> getIndex() {

        return index;
    }
}
```

---

# 49. Java Dry Run

Document:

```text
Doc1:
redis caching fast
```

Tokenizer output:

```text
redis
caching
fast
```

Posting lists built:

```text
redis → [1]
caching → [1]
fast → [1]
```

---

# 50. Java Flush Mental Model

Real Lucene flow:

```text
documents buffered
      ↓
flush threshold reached
      ↓
segment created
```

---

# 51. Why Immutable Segments Brilliant

Immutable design simplifies:

```text
safe concurrent search
cache efficiency
merge operations
recovery
```

Huge architectural advantage.

---

# 52. Recovery Mental Model

After crash:

```text
committed segments remain safe
```

Index can recover.

---

# 53. Production Challenges

Real systems handle:

```text
billions of documents
concurrent indexing
real-time updates
large merges
disk IO pressure
```

---

# 54. Merge Tradeoffs

Frequent merges:

```text
better search performance
higher indexing cost
```

Rare merges:

```text
faster indexing
slower searches
```

Need balance.

---

# 55. Why Lucene Famous

Lucene architecture is powerful because:

```text
immutable segments
fast posting lists
efficient merges
excellent relevance
```

---

# 56. Common Mistakes

## Mistake 1

```text
Thinking documents searchable immediately
```

Need indexing first.

---

## Mistake 2

```text
Ignoring segment merges
```

Too many segments hurt performance.

---

## Mistake 3

```text
Direct disk write per document
```

Very slow.

Need buffers.

---

## Mistake 4

```text
Thinking Elasticsearch built indexing itself
```

Lucene handles core indexing.

---

# 57. Interview Explanation

If interviewer asks:

```text
What does Index Writer do?
```

Strong answer:

```text
Index Writer converts documents into searchable inverted indexes.
It tokenizes text, builds posting lists, creates immutable segments,
flushes data to disk, and manages segment merges.
```

Senior addition:

```text
Lucene Index Writer enables near real-time indexing while maintaining
safe concurrent search through immutable segment architecture.
```

---

# 58. Final Mental Model

```text
Documents
    ↓
Analyzer
    ↓
Terms
    ↓
Posting Lists
    ↓
Segments
    ↓
Disk Index
```

Index Writer is the HEART of search indexing.

---

# 59. What To Remember

```text
Index Writer builds searchable indexes.

Lucene uses immutable segments.

Documents first enter memory buffers.

Flush creates segments.

Merge combines small segments.

Search continues during merges.

Elasticsearch shards contain Lucene Index Writers.

Near real-time indexing enabled by segment architecture.
```

---

# 60. Next File

```text
012_Index_Reader.md
```

Next you learn:

```text
how searches actually read indexes
term lookup
posting list traversal
segment readers
query execution
Lucene search internals
search caches
```
