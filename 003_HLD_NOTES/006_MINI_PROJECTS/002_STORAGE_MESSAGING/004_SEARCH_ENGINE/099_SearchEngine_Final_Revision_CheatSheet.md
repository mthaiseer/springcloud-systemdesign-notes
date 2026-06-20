# 099_SearchEngine_Final_Revision_CheatSheet.md

# MiniSearchEngine — FINAL REVISION CHEATSHEET

# 0. COMPLETE MENTAL MODEL

```text
Documents
    ↓
Tokenization
    ↓
Normalization
    ↓
Stopwords / Stemming
    ↓
Inverted Index
    ↓
Posting Lists
    ↓
TF / IDF / BM25
    ↓
Index Writer
    ↓
Lucene Segments
    ↓
Index Reader
    ↓
Query Parser
    ↓
Boolean / Phrase / Prefix / Fuzzy Search
    ↓
Distributed Search
    ↓
Elasticsearch Cluster
```

---

# 1. WHAT IS SEARCH ENGINE

## Mental Model

```text
Search Engine =
Fast Text Retrieval System
```

Purpose:

```text
find relevant documents quickly
```

Core components:

```text
crawler
indexer
inverted index
ranking engine
distributed cluster
```

---

# 2. DOCUMENT MODEL

## Mental Model

```text
Document =
searchable unit
```

Example:

```json
{
  "title": "Redis Tutorial",
  "body": "Redis is fast"
}
```

Fields:

```text
title
body
tags
metadata
```

---

# 3. TOKENIZATION

## Mental Model

```text
Sentence
    ↓
Tokens
```

Example:

```text
"Redis is fast"

↓

["Redis","is","fast"]
```

---

# 4. TEXT NORMALIZATION

## Goal

Convert text into consistent form.

Examples:

```text
Redis → redis
café → cafe
```

---

# 5. STOPWORDS & STEMMING

## Stopwords

Remove:

```text
the
is
a
of
```

---

## Stemming

```text
running → run
played → play
```

---

# 6. INVERTED INDEX

## Biggest Mental Model

```text
term → documents
```

Example:

```text
redis → [1,3]
kafka → [2,3]
```

---

# 7. POSTING LIST

Posting list stores:

```text
docIDs
frequencies
positions
```

Example:

```text
redis →
[
 {doc=1,freq=2,pos=[1,4]}
]
```

---

# 8. TERM FREQUENCY (TF)

Formula:

TF = termCount / totalTerms

Example:

```text
redis redis kafka redis
```

Counts:

```text
redis = 3
total = 4
```

TF:

```text
3 / 4 = 0.75
```

---

# 9. IDF

## Mental Model

Rare terms are more important.

Formula:

```text
IDF = log(N / df)
```

---

# 10. TF-IDF

Formula:

```text
TF-IDF = TF × IDF
```

Mental model:

```text
important inside document
+
rare globally
```

---

# 11. BM25

Lucene default ranking algorithm.

Uses:

```text
TF
IDF
document length normalization
```

Mental model:

```text
rare terms matter
repetition helps
long docs penalized
```

---

# 12. INDEX WRITER

Flow:

```text
Documents
    ↓
Analyzer
    ↓
Tokens
    ↓
Posting Lists
    ↓
Segments
```

Responsibilities:

```text
tokenization
segment flushing
segment merging
```

---

# 13. SEGMENTS

## Biggest Lucene Mental Model

```text
Lucene Index =
collection of immutable segments
```

Segment contains:

```text
postings
term dictionary
stored fields
doc values
```

---

# 14. REFRESH vs FLUSH

## Refresh

```text
makes docs searchable
```

## Flush

```text
persists safely to disk
```

Mental model:

```text
refresh = visibility
flush = durability
```

---

# 15. SEGMENT MERGE

```text
small segments
      ↓
merge
      ↓
large segment
```

Benefits:

```text
better compression
faster search
```

---

# 16. INDEX READER

Flow:

```text
Query
   ↓
Posting Lists
   ↓
BM25
   ↓
Top Results
```

Responsibilities:

```text
term lookup
scoring
retrieval
```

---

# 17. QUERY PARSER

Mental model:

```text
raw query
    ↓
query tree
```

Example:

```text
redis AND kafka
```

Tree:

```text
      AND
     /   \
 redis  kafka
```

---

# 18. BOOLEAN SEARCH

Core mapping:

```text
AND → intersection
OR  → union
NOT → difference
```

Example:

```text
redis → [1,3,8]
kafka → [2,3,8]
```

AND:

```text
[3,8]
```

---

# 19. PHRASE SEARCH

Mental model:

```text
Boolean matching
+
position verification
```

Example:

```text
redis → 1
caching → 2
```

Condition:

```text
2 = 1 + 1
```

Phrase matches.

---

# 20. POSITIONAL INDEX

Posting list stores:

```text
docID
frequency
positions
```

Needed for:

```text
phrase queries
highlighting
proximity search
```

---

# 21. SLOP

Allows phrase gaps.

Example:

```text
"redis caching"~2
```

---

# 22. PREFIX AUTOCOMPLETE

Mental model:

```text
typed prefix
    ↓
matching terms
```

Example:

```text
red
```

Suggestions:

```text
redis
redisson
redirect
```

---

# 23. TRIE

Classic autocomplete structure.

Complexity:

```text
O(prefix length)
```

---

# 24. EDGE NGRAM

Word:

```text
redis
```

Generated prefixes:

```text
r
re
red
redi
redis
```

---

# 25. FST

Lucene uses:

```text
Finite State Transducer
```

Benefits:

```text
compressed dictionary
shared prefixes
fast lookup
```

---

# 26. FUZZY SEARCH

Mental model:

```text
find closest valid term
```

Example:

```text
redsi → redis
```

---

# 27. LEVENSHTEIN DISTANCE

Operations:

```text
insert
delete
replace
```

Meaning:

```text
minimum edits between strings
```

---

# 28. BK TREE

Efficient fuzzy lookup structure.

Avoids brute-force scans.

---

# 29. SHARDING

Mental model:

```text
large index
    ↓
split into shards
```

Each shard:

```text
mini Lucene index
```

---

# 30. ROUTING

Formula:

```text
hash(docId) % shardCount
```

Determines target shard.

---

# 31. SCATTER-GATHER

Distributed search flow:

```text
Coordinator
    ↓
Shard Searches
    ↓
Merge Results
```

---

# 32. REPLICATION

Mental model:

```text
multiple shard copies
```

Provides:

```text
fault tolerance
read scaling
availability
```

---

# 33. PRIMARY vs REPLICA

Primary:

```text
handles writes
```

Replica:

```text
handles reads/failover
```

---

# 34. ELASTICSEARCH vs LUCENE

## Lucene

Actual search engine.

Handles:

```text
segments
postings
BM25
search execution
```

---

## Elasticsearch

Distributed layer.

Handles:

```text
cluster
REST API
sharding
replication
coordination
```

---

# 35. DOC VALUES

Columnar storage for:

```text
sorting
aggregations
analytics
```

---

# 36. TRANSLOG

Temporary durability log.

Used before full segment commit.

---

# 37. SEGMENT IMMUTABILITY

Benefits:

```text
safe concurrency
fast reads
simple locking
```

---

# 38. SEARCH EXECUTION PIPELINE

```text
User Query
    ↓
Query Parser
    ↓
Posting Lists
    ↓
BM25 Ranking
    ↓
Top-K Heap
    ↓
Results
```

---

# 39. TOP-K RETRIEVAL

Uses:

```text
priority queue / heap
```

Returns:

```text
best K documents
```

---

# 40. CACHING

Important caches:

```text
query cache
filter cache
segment cache
```

---

# 41. HOT SHARDS

Problem:

```text
one shard gets most traffic
```

Causes:

```text
imbalance
high latency
```

---

# 42. VECTOR SEARCH

Modern search supports:

```text
semantic embeddings
ANN search
vector similarity
```

---

# 43. HYBRID SEARCH

```text
BM25
+
Vector Similarity
```

---

# 44. COMPLETE ELASTICSEARCH FLOW

```text
Document
    ↓
Analyzer
    ↓
Tokens
    ↓
Lucene Segment
    ↓
Shard
    ↓
Replica
    ↓
Cluster
```

Search:

```text
User Query
    ↓
Coordinator
    ↓
Shards
    ↓
Lucene Readers
    ↓
BM25
    ↓
Merge Results
```

---

# 45. IMPORTANT DATA STRUCTURES

```text
Inverted Index
Posting List
Trie
FST
BK Tree
Priority Queue
Segment
HashMap
```

---

# 46. IMPORTANT ALGORITHMS

```text
TF-IDF
BM25
Levenshtein Distance
Posting Intersection
Top-K Heap
Delta Compression
```

---

# 47. IMPORTANT MENTAL MODELS

## Inverted Index

```text
term → docs
```

## BM25

```text
rare important terms
```

## Phrase Search

```text
positions matter
```

## Autocomplete

```text
prefix traversal
```

## Fuzzy Search

```text
edit distance similarity
```

## Lucene

```text
immutable segments
```

## Elasticsearch

```text
distributed Lucene
```

---

# 48. INTERVIEW QUICK ANSWERS

## What is inverted index?

```text
Maps terms to documents for fast retrieval.
```

## Why BM25 better than TF-IDF?

```text
Handles document length normalization better.
```

## Why immutable segments?

```text
Simplifies concurrency and improves reads.
```

## Why phrase search needs positions?

```text
Need adjacency verification.
```

## Why replicas needed?

```text
Fault tolerance and read scaling.
```

---

# 49. COMPLETE PRODUCTION MENTAL MODEL

```text
Google/Elasticsearch Search

Documents
    ↓
Distributed Ingestion
    ↓
Lucene Segments
    ↓
Shards
    ↓
Replicas
    ↓
Distributed Query Execution
    ↓
BM25 + Vector Ranking
    ↓
Top-K Results
```

---

# 50. FINAL WHAT TO REMEMBER

```text
Search engines revolve around inverted indexes.

Lucene is the real search engine core.

Elasticsearch provides distributed architecture.

Posting lists power fast retrieval.

BM25 is modern keyword ranking.

Phrase search needs positions.

Autocomplete uses tries/FST.

Fuzzy search uses edit distance.

Sharding scales horizontally.

Replication provides availability.

Refresh = searchable.
Flush = durable.

Modern search combines BM25 + vectors.
```
