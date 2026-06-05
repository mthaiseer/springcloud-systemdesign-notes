# 001_What_Is_Search_Engine.md

# MiniSearchEngine — 001 What Is Search Engine

## 0. Why This File Exists

Modern applications contain huge amounts of data:

```text
emails
documents
products
messages
articles
videos
logs
web pages
```

Users need:

```text
fast search
relevant results
autocomplete
fuzzy matching
ranking
```

A search engine solves this.

This file teaches:

```text
what search engine is
why databases are not enough
how search engines work internally
documents
tokens
inverted indexes
ranking
retrieval pipeline
distributed search basics
```

Only ASCII diagrams are used.

---

# 1. One-Line Definition

```text
A search engine is a system that quickly finds relevant documents
for a user query.
```

Example:

User searches:

```text
"redis caching"
```

Search engine returns:

```text
documents related to Redis caching
```

---

# 2. Biggest Mental Model

Search engine is NOT just database lookup.

It is:

```text
Document Retrieval System
+
Ranking System
```

Main job:

```text
find relevant documents fast
```

---

# 3. Search Engine Core Flow

```text
Documents
    ↓
Tokenization
    ↓
Build Inverted Index
    ↓
User Query
    ↓
Find Matching Documents
    ↓
Rank Documents
    ↓
Return Top Results
```

---

# 4. What Is A Document

Search engines work with:

```text
documents
```

Document can be:

```text
web page
email
PDF
product
blog
message
JSON object
```

Example:

```json
{
  "id": 101,
  "title": "Redis Caching Basics",
  "content": "Redis is an in-memory database"
}
```

This entire object is:

```text
one searchable document
```

---

# 5. Why Database Search Is Not Enough

Suppose SQL query:

```sql
SELECT *
FROM articles
WHERE title LIKE '%redis%';
```

Problems:

```text
slow on huge data
poor ranking
bad fuzzy search
no relevance scoring
weak autocomplete
```

Databases optimized for:

```text
transactions
consistency
CRUD
```

Search engines optimized for:

```text
text retrieval
ranking
search relevance
```

---

# 6. Database vs Search Engine

| Feature | Database | Search Engine |
|---|---|---|
| Goal | Store data | Find relevant data |
| Optimization | Transactions | Retrieval |
| Queries | Exact match | Relevance search |
| Ranking | Weak | Strong |
| Full-text search | Limited | Excellent |
| Autocomplete | Weak | Excellent |

---

# 7. Example Search Problem

Suppose documents:

```text
Doc1:
Redis caching tutorial

Doc2:
Kafka distributed messaging

Doc3:
Redis persistence explained
```

User searches:

```text
redis
```

Search engine should quickly return:

```text
Doc1
Doc3
```

without scanning every document each time.

---

# 8. Naive Search Problem

Without indexing:

```text
scan every document
for every query
```

This is expensive.

---

# 9. Naive Search ASCII

```text
Query
   ↓
Read Doc1
Read Doc2
Read Doc3
Read Doc4
Read Doc5
...
```

Slow for millions of documents.

---

# 10. Core Search Engine Idea

Instead of:

```text
document → words
```

Search engine stores:

```text
word → documents
```

This is:

```text
Inverted Index
```

---

# 11. Inverted Index Mental Model

Example:

Documents:

```text
Doc1:
redis caching

Doc2:
kafka messaging

Doc3:
redis persistence
```

Index:

```text
redis → [Doc1, Doc3]
caching → [Doc1]
kafka → [Doc2]
```

---

# 12. Why Called Inverted Index

Normal document storage:

```text
document → words
```

Search engine storage:

```text
word → documents
```

Direction inverted.

---

# 13. Search Query Flow

User query:

```text
redis caching
```

Execution:

```text
find "redis"
find "caching"
intersect results
rank results
return best documents
```

---

# 14. Query Flow ASCII

```text
User Query
    ↓
Tokenizer
    ↓
Lookup Inverted Index
    ↓
Candidate Documents
    ↓
Ranking
    ↓
Top Results
```

---

# 15. What Is Tokenization

Search engine splits text into:

```text
tokens
```

Example:

```text
"Redis is Fast"
```

Tokens:

```text
redis
is
fast
```

---

# 16. Normalization

Search engines normalize text.

Example:

```text
REDIS
Redis
redis
```

All become:

```text
redis
```

---

# 17. Why Ranking Needed

Suppose query:

```text
redis
```

10000 documents match.

Which should come first?

Need:

```text
ranking algorithm
```

---

# 18. Ranking Mental Model

Search engine scores documents.

Higher score:

```text
more relevant
```

Factors:

```text
term frequency
rarity
field importance
phrase match
freshness
popularity
```

---

# 19. TF-IDF Idea

Important concept.

```text
TF = term frequency
IDF = inverse document frequency
```

Idea:

```text
common words less important
rare words more important
```

---

# 20. Example TF-IDF

Suppose:

```text
"the"
```

appears in every document.

Not useful.

But:

```text
"redis"
```

appears rarely.

More useful for ranking.

---

# 21. BM25

Modern ranking algorithm.

Used in:

- Elasticsearch
- Apache Lucene

Improves TF-IDF.

Considers:

```text
document length
term saturation
relevance normalization
```

---

# 22. Search Features

Modern search engines support:

```text
full-text search
autocomplete
fuzzy search
phrase search
boolean search
ranking
distributed search
```

---

# 23. Autocomplete

Example:

User types:

```text
red
```

Suggestions:

```text
redis
redis caching
redis cluster
```

---

# 24. Fuzzy Search

Handles typos.

Example:

```text
redsi
```

Search engine still finds:

```text
redis
```

Uses:

```text
edit distance
Levenshtein distance
```

---

# 25. Phrase Search

Query:

```text
"redis caching"
```

Must match exact phrase order.

Different from:

```text
redis AND caching
```

---

# 26. Boolean Search

Examples:

```text
redis AND kafka
redis OR kafka
redis NOT kafka
```

---

# 27. Distributed Search

Large search systems distribute data across nodes.

Example:

```text
Shard-1
Shard-2
Shard-3
```

Each shard stores subset of index.

---

# 28. Distributed Search ASCII

```text
User Query
      ↓
Coordinator Node
      ↓
Search Shard-1
Search Shard-2
Search Shard-3
      ↓
Merge Results
      ↓
Return Top-K
```

---

# 29. Why Search Engines Scale Well

Search engines optimized for:

```text
read-heavy workloads
distributed retrieval
parallel query execution
compressed indexes
```

---

# 30. Search Engine Internal Components

Core components:

```text
Document Store
Tokenizer
Analyzer
Inverted Index
Posting Lists
Query Parser
Ranking Engine
Shard Manager
```

---

# 31. Search Engine Architecture

```text
Documents
     ↓
Analyzer Pipeline
     ↓
Inverted Index
     ↓
Search Queries
     ↓
Retrieval Engine
     ↓
Ranking Engine
     ↓
Top Results
```

---

# 32. Elasticsearch Mental Model

Elasticsearch internally uses:

```text
Lucene indexes
shards
replicas
BM25 ranking
inverted indexes
```

Distributed search layer on top of Lucene.

---

# 33. Lucene Mental Model

Apache Lucene is:

```text
core indexing and retrieval engine
```

Elasticsearch built on top of Lucene.

---

# 34. Google Search Mental Model

Simplified Google Search flow:

```text
crawl web pages
index documents
build link graph
process queries
rank pages
return best results
```

---

# 35. Search Engine vs Database Flow

Database:

```text
exact lookup
```

Search Engine:

```text
best relevant retrieval
```

Database asks:

```text
Which row matches?
```

Search engine asks:

```text
Which documents are most relevant?
```

---

# 36. Java Mini Search Example

Simple document:

```java
public class Document {

    int id;
    String content;

    public Document(int id, String content) {
        this.id = id;
        this.content = content;
    }
}
```

---

# 37. Java Naive Search

```java
public List<Document> search(
        List<Document> docs,
        String keyword) {

    List<Document> result =
            new ArrayList<>();

    for (Document doc : docs) {

        if (doc.content.contains(keyword)) {
            result.add(doc);
        }
    }

    return result;
}
```

Problem:

```text
scans all documents every query
```

---

# 38. Java Inverted Index Mental Model

```java
Map<String, List<Integer>> index =
        new HashMap<>();
```

Example:

```text
redis → [1, 3]
kafka → [2]
```

Fast lookup.

---

# 39. Full Search Dry Run

Documents:

```text
Doc1:
redis caching

Doc2:
kafka messaging

Doc3:
redis persistence
```

Build index:

```text
redis → [1,3]
caching → [1]
kafka → [2]
persistence → [3]
```

User query:

```text
redis
```

Execution:

```text
lookup "redis"
→ [1,3]
```

Return:

```text
Doc1
Doc3
```

---

# 40. Full Ranking Dry Run

Suppose:

```text
Doc1:
redis redis caching

Doc2:
redis basics
```

Query:

```text
redis
```

Doc1 contains:

```text
redis twice
```

Higher relevance score.

Doc1 ranked higher.

---

# 41. Production Search Challenges

Real systems must handle:

```text
billions of documents
high QPS
distributed indexing
replication
ranking quality
freshness
typo tolerance
autocomplete
```

---

# 42. Search Engine Tradeoffs

Better ranking:

```text
more CPU
more memory
```

More shards:

```text
better scale
more coordination overhead
```

---

# 43. Search Engine Use Cases

Used in:

```text
ecommerce search
log search
website search
document retrieval
chat search
email search
video search
recommendation systems
```

---

# 44. Common Mistakes

## Mistake 1

```text
Thinking search engine is just SQL LIKE
```

Search engines are specialized retrieval systems.

---

## Mistake 2

```text
Ignoring ranking
```

Ranking quality is core feature.

---

## Mistake 3

```text
Scanning documents per query
```

Need inverted indexes.

---

## Mistake 4

```text
Ignoring tokenization/normalization
```

Bad preprocessing hurts search quality.

---

## Mistake 5

```text
Thinking Elasticsearch stores only text
```

It stores structured JSON documents too.

---

# 45. Interview Explanation

If interviewer asks:

```text
What is search engine?
```

Strong answer:

```text
A search engine is a document retrieval system that indexes documents
using structures like inverted indexes and retrieves the most relevant
documents for a user query using ranking algorithms such as BM25.
```

---

# 46. Final Mental Model

```text
Search Engine
    =
Document Retrieval System
+
Ranking Engine
```

Core flow:

```text
Documents
    ↓
Tokenization
    ↓
Inverted Index
    ↓
Query Retrieval
    ↓
Ranking
    ↓
Top Results
```

---

# 47. What To Remember

```text
Search engines optimize retrieval, not transactions.

Core structure is inverted index.

Search engines map:
word → documents

Ranking determines relevance.

Tokenization and normalization are critical.

BM25 is modern ranking algorithm.

Elasticsearch uses Lucene internally.

Distributed search uses shards and replicas.
```

---

# 48. Next File

```text
002_Document_Model.md
```

Next you learn:

```text
how documents are represented
JSON documents
fields
metadata
schema
document IDs
stored fields
searchable fields
document lifecycle
```
