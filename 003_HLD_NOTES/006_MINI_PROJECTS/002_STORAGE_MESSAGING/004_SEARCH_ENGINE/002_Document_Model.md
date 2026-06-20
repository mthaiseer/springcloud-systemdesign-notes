# 002_Document_Model.md

# MiniSearchEngine — 002 Document Model

## 0. Why This File Exists

Search engines do not store data like traditional SQL tables.

Instead of:

```text
rows
columns
joins
```

Search engines primarily work with:

```text
documents
```

Understanding document model is extremely important because:

```text
everything in search engine revolves around documents
```

This file teaches:

```text
what documents are
document structure
JSON documents
fields
metadata
document IDs
stored fields
indexed fields
searchable fields
document lifecycle
nested documents
schema mapping
```

Only ASCII diagrams are used.

---

# 1. One-Line Definition

```text
A document is the basic searchable unit inside a search engine.
```

Example:

```json
{
  "id": 101,
  "title": "Redis Basics",
  "content": "Redis is an in-memory database"
}
```

This entire JSON object is:

```text
one document
```

---

# 2. Biggest Mental Model

Database world:

```text
table → rows
```

Search engine world:

```text
index → documents
```

Search engines optimize retrieval of documents.

---

# 3. Document Mental Model

A document contains:

```text
data
+
metadata
+
searchable fields
```

---

# 4. Document Structure ASCII

```text
Document
 ├── id
 ├── title
 ├── content
 ├── tags
 ├── author
 └── timestamp
```

---

# 5. Real Search Document Example

Ecommerce product:

```json
{
  "id": 5001,
  "name": "iPhone 15",
  "brand": "Apple",
  "price": 1200,
  "description": "Latest Apple smartphone",
  "category": "mobile"
}
```

Entire product is:

```text
single searchable document
```

---

# 6. Why Document Model Powerful

Documents are flexible.

Different documents may contain:

```text
different fields
different structures
nested objects
arrays
```

This is easier than rigid SQL schemas.

---

# 7. Search Engine Index

Documents are stored inside:

```text
indexes
```

Mental model:

```text
Index
   ↓
Collection of Documents
```

---

# 8. Index ASCII

```text
products-index
 ├── document-1
 ├── document-2
 ├── document-3
```

---

# 9. Document ID

Every document requires unique ID.

Example:

```json
{
  "id": 1001
}
```

Why important:

```text
fast retrieval
updates
deletes
deduplication
routing
```

---

# 10. Internal Document Storage

Search engine internally stores:

```text
document ID
field values
tokens
metadata
```

---

# 11. Searchable vs Stored Fields

Very important concept.

Some fields:

```text
indexed/searchable
```

Some fields:

```text
stored only
```

---

# 12. Searchable Field

Example:

```json
{
  "title": "Redis caching"
}
```

Title may be tokenized and indexed.

Searchable.

---

# 13. Stored Field

Example:

```json
{
  "imageUrl": "abc.jpg"
}
```

May be stored but NOT indexed.

Cannot search directly.

---

# 14. Indexed vs Stored Mental Model

Indexed field:

```text
used for search
```

Stored field:

```text
returned in results
```

---

# 15. Indexed Field ASCII

```text
"title"
   ↓
tokenize
   ↓
inverted index
```

---

# 16. Stored Field ASCII

```text
"imageUrl"
   ↓
store as metadata
   ↓
return during retrieval
```

---

# 17. Document Lifecycle

Document lifecycle:

```text
create
update
delete
search
```

---

# 18. Document Lifecycle ASCII

```text
Insert Document
      ↓
Tokenization
      ↓
Build Inverted Index
      ↓
Search Queries
      ↓
Update/Delete
```

---

# 19. Insert Document Flow

When inserting document:

```text
store raw document
tokenize fields
normalize text
build inverted index entries
store metadata
```

---

# 20. Insert Flow ASCII

```text
Document
    ↓
Tokenizer
    ↓
Analyzer
    ↓
Inverted Index
```

---

# 21. Example Document Tokenization

Document:

```json
{
  "title": "Redis Caching Basics"
}
```

Tokens:

```text
redis
caching
basics
```

---

# 22. Field Types

Search engines support multiple field types.

Examples:

```text
text
keyword
integer
date
boolean
nested
geo-point
```

---

# 23. Text Field

Text field:

```text
tokenized
analyzed
full-text searchable
```

Example:

```json
{
  "title": "Redis caching"
}
```

---

# 24. Keyword Field

Keyword field:

```text
NOT tokenized
exact matching only
```

Example:

```json
{
  "country": "Romania"
}
```

Good for:

```text
filters
sorting
aggregation
```

---

# 25. Text vs Keyword

| Feature | Text | Keyword |
|---|---|---|
| Tokenized | Yes | No |
| Full-text search | Yes | No |
| Exact match | Weak | Excellent |
| Sorting | Weak | Good |

---

# 26. Numeric Fields

Examples:

```json
{
  "price": 1200,
  "rating": 4.8
}
```

Used for:

```text
range filters
sorting
aggregation
```

---

# 27. Date Fields

Example:

```json
{
  "createdAt": "2026-06-05"
}
```

Used for:

```text
time filtering
sorting
analytics
```

---

# 28. Nested Objects

Documents may contain nested objects.

Example:

```json
{
  "name": "iPhone",
  "reviews": [
    {
      "user": "A",
      "rating": 5
    }
  ]
}
```

---

# 29. Nested Document ASCII

```text
Product
 ├── name
 └── reviews
      ├── user
      └── rating
```

---

# 30. Arrays

Documents support arrays.

Example:

```json
{
  "tags": [
    "redis",
    "database",
    "cache"
  ]
}
```

Each element may become searchable token.

---

# 31. Schema Mapping

Search engines define:

```text
field type
indexing behavior
analyzer
storage rules
```

This is called:

```text
mapping
schema
```

---

# 32. Elasticsearch Mapping Example

Example mapping:

```json
{
  "title": {
    "type": "text"
  },
  "country": {
    "type": "keyword"
  }
}
```

---

# 33. Dynamic Mapping

Some search engines auto-detect field types.

Example:

```text
number → integer
string → text
date string → date
```

Convenient but may create mistakes.

---

# 34. Document Retrieval

When search query matches:

```text
search engine retrieves matching document
```

along with metadata.

---

# 35. Retrieval Flow ASCII

```text
Query
   ↓
Inverted Index
   ↓
Matching Document IDs
   ↓
Load Stored Fields
   ↓
Return Results
```

---

# 36. Document Score

Each document receives relevance score.

Example:

```json
{
  "id": 1,
  "_score": 9.2
}
```

Higher score:

```text
more relevant
```

---

# 37. Metadata Fields

Search engines maintain metadata.

Examples:

```text
document ID
score
timestamp
routing
version
```

---

# 38. Versioning

Documents often have versions.

Useful for:

```text
concurrency control
updates
replication
conflict detection
```

---

# 39. Update Document Problem

Search engines usually do NOT update tokens in-place.

Instead:

```text
mark old document deleted
insert new document version
```

Very important mental model.

---

# 40. Update Flow ASCII

```text
Old Document
      ↓
mark deleted
      ↓
insert updated document
      ↓
re-index tokens
```

---

# 41. Delete Document

Deletes usually:

```text
mark document deleted
cleanup later
```

Similar to MVCC style systems.

---

# 42. Soft Delete

Many search engines use:

```text
soft deletes
```

Meaning:

```text
document hidden first
physical cleanup later
```

---

# 43. Why Search Engines Prefer Documents

Documents naturally represent:

```text
products
articles
profiles
posts
messages
events
```

Flexible and scalable.

---

# 44. Document vs Relational Row

| Feature | SQL Row | Search Document |
|---|---|---|
| Schema | Rigid | Flexible |
| Joins | Strong | Weak |
| Nested Data | Hard | Easy |
| Full-text Search | Weak | Excellent |
| Scaling | Moderate | Excellent |

---

# 45. Denormalization

Search engines prefer:

```text
denormalized documents
```

Instead of joins.

Example:

```text
duplicate author name inside article document
```

to avoid join cost.

---

# 46. Why Denormalization Useful

Search systems optimize:

```text
fast reads
fast retrieval
distributed search
```

Joins are expensive in distributed systems.

---

# 47. Java Document Model Example

```java
public class ProductDocument {

    private int id;

    private String name;

    private String description;

    private List<String> tags;

    private double price;
}
```

---

# 48. Java Searchable Fields Example

```java
Map<String, Object> document =
        new HashMap<>();

document.put("title",
        "Redis Basics");

document.put("price", 1200);

document.put("tags",
        List.of("redis", "database"));
```

---

# 49. Full Document Insert Dry Run

Document:

```json
{
  "id": 1,
  "title": "Redis Basics"
}
```

Execution:

```text
Store document
      ↓
Extract fields
      ↓
Tokenize title
      ↓
Build inverted index
      ↓
Store metadata
```

Tokens:

```text
redis
basics
```

Index:

```text
redis → [1]
basics → [1]
```

---

# 50. Full Search Dry Run

Documents:

```text
Doc1:
Redis Basics

Doc2:
Kafka Messaging
```

Query:

```text
redis
```

Execution:

```text
lookup token redis
      ↓
find document ID 1
      ↓
load stored fields
      ↓
return Doc1
```

---

# 51. Production Search Challenges

Real systems handle:

```text
billions of documents
dynamic mappings
large nested objects
schema evolution
distributed indexing
high write throughput
```

---

# 52. Search Engine Tradeoffs

Flexible documents:

```text
easy ingestion
easy scaling
```

But:

```text
duplicate data
weaker joins
larger indexes
```

---

# 53. Elasticsearch Document Mental Model

Elasticsearch internally stores:

```text
JSON documents
```

Each document:

```text
analyzed
tokenized
indexed
stored
replicated
```

---

# 54. Lucene Document Mental Model

Lucene internally represents documents as:

```text
collection of fields
```

Each field may be:

```text
stored
indexed
tokenized
```

---

# 55. Common Mistakes

## Mistake 1

```text
Thinking search documents are SQL rows
```

Documents are more flexible.

---

## Mistake 2

```text
Indexing every field
```

Creates huge indexes.

---

## Mistake 3

```text
Using text field for exact filters
```

Use keyword field.

---

## Mistake 4

```text
Heavy joins in search systems
```

Search engines prefer denormalized documents.

---

## Mistake 5

```text
Ignoring nested document complexity
```

Nested objects affect indexing and query cost.

---

# 56. Interview Explanation

If interviewer asks:

```text
What is document model in search engines?
```

Strong answer:

```text
Search engines organize data as documents, usually JSON-like objects
containing searchable and stored fields. Documents are tokenized,
indexed, and retrieved through inverted indexes instead of relational joins.
```

---

# 57. Final Mental Model

```text
Search Engine
    =
Collection of Documents
+
Inverted Index
+
Retrieval Engine
```

Document flow:

```text
JSON Document
      ↓
Field Extraction
      ↓
Tokenization
      ↓
Inverted Index
      ↓
Search Retrieval
```

---

# 58. What To Remember

```text
Document is the basic searchable unit.

Documents are usually JSON-like objects.

Fields may be searchable or stored.

Text fields are tokenized.

Keyword fields support exact match.

Documents are inserted into indexes.

Updates usually create new document versions.

Search engines prefer denormalized documents.
```

---

# 59. Next File

```text
003_Tokenization.md
```

Next you learn:

```text
how raw text becomes searchable tokens
splitting rules
token streams
whitespace tokenizer
punctuation handling
Unicode handling
analyzer pipelines
```
