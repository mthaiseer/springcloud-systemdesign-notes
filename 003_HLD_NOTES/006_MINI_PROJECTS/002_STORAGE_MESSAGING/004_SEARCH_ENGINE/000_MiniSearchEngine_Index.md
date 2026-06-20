
# MiniSearchEngine — Index

# MiniSearchEngine

Goal:

```text
Understand HOW search engines work internally
instead of only using APIs.
```

This mini covers:

```text
tokenization
inverted indexes
posting lists
ranking
TF-IDF
BM25
autocomplete
fuzzy matching
query parsing
distributed search
```

---

# Full Learning Flow

```text
Document
    ↓
Tokenization
    ↓
Normalization
    ↓
Inverted Index
    ↓
Posting Lists
    ↓
Query Parsing
    ↓
Ranking
    ↓
Top-K Retrieval
    ↓
Distributed Search
```

---

# MiniSearchEngine Tree

```text
MiniSearchEngine/
│
├── 001_What_Is_Search_Engine.md
├── 002_Document_Model.md
├── 003_Tokenization.md
├── 004_Text_Normalization.md
├── 005_Stopwords_Stemming.md
├── 006_Inverted_Index.md
├── 007_Posting_List.md
├── 008_Term_Frequency.md
├── 009_TF_IDF_Ranking.md
├── 010_BM25_Ranking.md
├── 011_Index_Writer.md
├── 012_Index_Reader.md
├── 013_Query_Parser.md
├── 014_Boolean_Search.md
├── 015_Phrase_Search.md
├── 016_Prefix_Autocomplete.md
├── 017_Fuzzy_Search_Edit_Distance.md
├── 018_Sharding_Replication_Search.md
├── 019_Elasticsearch_Lucene_Internals.md
└── 020_Production_Grade_Search_Engine.md
```

---

# Core Mental Model

```text
word → list of documents
```

Example:

```text
"redis"
    → [doc1, doc5]

"search"
    → [doc1, doc3]
```

---

# Highest ROI Topics

```text
Tokenization
Inverted Index
Posting Lists
TF-IDF
BM25
Query Parsing
Autocomplete
Lucene Internals
```

---

# Technologies This Helps Understand

- Elasticsearch
- Lucene
- OpenSearch
- Solr

---

# Final Goal

By the end you should understand:

```text
How search engines index documents
How queries retrieve results
How ranking works
How distributed search scales
How Elasticsearch works internally
```
