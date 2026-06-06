# 005_Stopwords_Stemming.md

# MiniSearchEngine — 005 Stopwords & Stemming

## 0. Why This File Exists

Not all words are equally important in search.

Some words appear everywhere:

```text
the
is
a
an
of
to
```

These create:

```text
huge indexes
low search quality
poor ranking signals
```

At the same time, users search using different word forms:

```text
run
running
runs
runner
```

Search engine should often treat them similarly.

This file teaches:

```text
stopwords
stopword removal
stemming
lemmatization
Porter stemming
query expansion
recall vs precision
stemming pipelines
language analyzers
search quality tradeoffs
```

Only ASCII diagrams are used.

---

# 1. Biggest Mental Model

Search engines try to:

```text
remove low-value noise
+
normalize related words
```

Core flow:

```text
Raw Tokens
    ↓
Stopword Removal
    ↓
Stemming / Lemmatization
    ↓
Canonical Search Tokens
```

---

# 2. What Are Stopwords

Stopwords are:

```text
very common low-information words
```

Examples:

```text
the
is
a
an
of
to
in
on
for
```

These appear in almost every document.

---

# 3. Why Stopwords Problematic

Suppose:

```text
the
```

appears in:

```text
95% of documents
```

Then:

```text
huge posting lists
low ranking value
large index size
```

---

# 4. Stopword Mental Model

Useful words:

```text
redis
kafka
database
search
```

Low-value words:

```text
the
is
a
```

Search engine often removes low-value tokens.

---

# 5. Stopword Removal ASCII

```text
Input:
"Redis is fast"

Tokens:
redis
is
fast

      ↓ remove stopwords

redis
fast
```

---

# 6. Why Stopword Removal Helps

Benefits:

```text
smaller inverted index
less noise
better ranking
faster search
```

---

# 7. Stopword Tradeoff

Aggressive stopword removal may hurt meaning.

Example:

```text
"to be or not to be"
```

If stopwords removed:

```text
be not be
```

Meaning damaged.

---

# 8. Phrase Search Problem

Phrase query:

```text
"lord of the rings"
```

If stopwords removed aggressively:

```text
lord rings
```

Phrase accuracy suffers.

---

# 9. Modern Search Engine Strategy

Modern search engines often:

```text
keep stopwords
but reduce ranking importance
```

instead of removing completely.

---

# 10. Language-Specific Stopwords

Different languages use different stopwords.

English:

```text
the
is
a
```

French:

```text
le
la
de
```

Arabic:

```text
و
في
من
```

---

# 11. Stopword Pipeline ASCII

```text
Raw Tokens
     ↓
Check Stopword List
     ↓
Remove Low-Value Tokens
     ↓
Remaining Search Tokens
```

---

# 12. What Is Stemming

Stemming reduces words into root-like forms.

Example:

```text
running
runs
runner
```

may become:

```text
run
```

Goal:

```text
match related word forms
```

---

# 13. Why Stemming Important

User searches:

```text
run
```

Search engine should also match:

```text
running
runs
runner
```

Without stemming:

```text
matches become fragmented
```

---

# 14. Stemming ASCII

```text
running
runs
runner

      ↓ stemming

run
```

---

# 15. Recall vs Precision

Very important search concept.

## Recall

```text
find MORE relevant results
```

## Precision

```text
avoid incorrect results
```

---

# 16. Stemming Improves Recall

Without stemming:

```text
run ≠ running
```

With stemming:

```text
run = running
```

More matches found.

Recall improves.

---

# 17. But Precision May Drop

Suppose:

```text
universe
university
```

Bad stemming may incorrectly reduce similarly.

Then unrelated documents may match.

Precision decreases.

---

# 18. Stemming Tradeoff ASCII

```text
Aggressive stemming
       ↓
Higher Recall
       ↓
Lower Precision sometimes
```

---

# 19. Porter Stemmer

Most famous stemming algorithm.

Rules-based algorithm.

Example:

```text
running → run
studies → studi
connected → connect
```

---

# 20. Porter Stemmer Mental Model

Porter stemmer applies:

```text
suffix removal rules
```

Examples:

```text
ing
ed
es
s
```

---

# 21. Porter Stemmer ASCII

```text
running
      ↓ remove "ing"
run
```

---

# 22. Rule-Based Stemming

Example rules:

```text
remove "ing"
remove "ed"
remove plural "s"
```

Simple but imperfect.

---

# 23. Overstemming Problem

Overstemming:

```text
different words become same stem
```

Example:

```text
universe
university
```

may incorrectly collapse.

---

# 24. Understemming Problem

Understemming:

```text
related words remain separate
```

Example:

```text
run
running
```

may not normalize fully.

---

# 25. Lemmatization

Lemmatization is smarter than stemming.

Uses:

```text
dictionary
language rules
grammar
```

---

# 26. Lemmatization Example

```text
went → go
better → good
running → run
```

More semantically correct.

---

# 27. Lemmatization ASCII

```text
went
goes
going

    ↓ lemmatization

go
```

---

# 28. Stemming vs Lemmatization

| Feature | Stemming | Lemmatization |
|---|---|---|
| Speed | Faster | Slower |
| Accuracy | Lower | Higher |
| Language Awareness | Weak | Strong |
| Complexity | Simple | Complex |

---

# 29. Search-Time vs Index-Time Stemming

Documents stemmed during:

```text
index time
```

Queries stemmed during:

```text
search time
```

Both should align.

---

# 30. Stemming Pipeline ASCII

```text
Document
     ↓
Tokenizer
     ↓
Stopword Removal
     ↓
Stemmer
     ↓
Final Tokens
```

---

# 31. Example Full Pipeline

Input:

```text
"Running is FAST"
```

Step 1 — lowercase:

```text
running is fast
```

Step 2 — tokenize:

```text
running
is
fast
```

Step 3 — remove stopwords:

```text
running
fast
```

Step 4 — stemming:

```text
run
fast
```

Final tokens:

```text
run
fast
```

---

# 32. Query Expansion

Search engine may expand queries.

Example:

```text
car
```

Expanded into:

```text
car
automobile
vehicle
```

Improves recall.

---

# 33. Query Expansion ASCII

```text
Query:
car

Expanded:
car
automobile
vehicle
```

---

# 34. Synonym Expansion

Synonym filters may work during:

```text
index time
or
search time
```

---

# 35. Index-Time Synonyms

Documents expanded during indexing.

Example:

```text
car → automobile
```

Stored directly in index.

Pros:

```text
faster query time
```

Cons:

```text
larger index
```

---

# 36. Search-Time Synonyms

Query expanded during search.

Pros:

```text
smaller index
```

Cons:

```text
slower query processing
```

---

# 37. Lucene Stopword Filter

Lucene contains:

```text
StopFilter
PorterStemFilter
```

Used inside analyzers.

---

# 38. Elasticsearch Analyzer Example

Example pipeline:

```text
Standard Tokenizer
      ↓
Lowercase Filter
      ↓
Stop Filter
      ↓
Porter Stem Filter
```

---

# 39. Java Stopword Filter

```java
import java.util.Set;

public class StopwordFilter {

    private final Set<String> stopwords =
            Set.of(
                    "the",
                    "is",
                    "a",
                    "an",
                    "of",
                    "to"
            );

    public boolean isStopword(String token) {

        return stopwords.contains(token);
    }
}
```

Explanation:

```text
Stores low-value words in HashSet for O(1) lookup.
```

---

# 40. Java Stopword Removal

```java
import java.util.ArrayList;
import java.util.List;

public class StopwordRemover {

    private final StopwordFilter filter =
            new StopwordFilter();

    public List<String> remove(
            List<String> tokens) {

        List<String> result =
                new ArrayList<>();

        for (String token : tokens) {

            if (!filter.isStopword(token)) {
                result.add(token);
            }
        }

        return result;
    }
}
```

---

# 41. Java Simple Stemmer

```java
public class SimpleStemmer {

    public String stem(String token) {

        if (token.endsWith("ing")) {

            return token.substring(
                    0,
                    token.length() - 3
            );
        }

        if (token.endsWith("ed")) {

            return token.substring(
                    0,
                    token.length() - 2
            );
        }

        if (token.endsWith("s")
                && token.length() > 1) {

            return token.substring(
                    0,
                    token.length() - 1
            );
        }

        return token;
    }
}
```

---

# 42. Java Stemmer Dry Run

Input:

```text
running
```

Execution:

```text
endsWith("ing")
       ↓
remove suffix
       ↓
run
```

---

# 43. Java Full Pipeline

```java
import java.util.ArrayList;
import java.util.List;

public class SearchAnalyzer {

    private final StopwordRemover remover =
            new StopwordRemover();

    private final SimpleStemmer stemmer =
            new SimpleStemmer();

    public List<String> analyze(
            List<String> tokens) {

        List<String> filtered =
                remover.remove(tokens);

        List<String> finalTokens =
                new ArrayList<>();

        for (String token : filtered) {

            finalTokens.add(
                    stemmer.stem(token)
            );
        }

        return finalTokens;
    }
}
```

---

# 44. Full Dry Run

Input:

```text
"Running is FAST"
```

Tokenizer output:

```text
running
is
fast
```

Stopword removal:

```text
running
fast
```

Stemming:

```text
run
fast
```

Final tokens:

```text
run
fast
```

---

# 45. Inverted Index Dry Run

Document:

```text
"Running is FAST"
```

Final analyzed tokens:

```text
run
fast
```

Inverted index:

```text
run  → [Doc1]
fast → [Doc1]
```

Query:

```text
run
```

Search engine finds:

```text
Doc1
```

even though original text contained:

```text
running
```

---

# 46. Search Quality Tradeoffs

Aggressive stemming:

```text
better recall
more matches
```

But:

```text
lower precision
more false positives
```

Conservative stemming:

```text
higher precision
```

But:

```text
may miss relevant documents
```

---

# 47. Production Challenges

Real systems handle:

```text
multi-language stemming
custom stopwords
domain-specific synonyms
medical terminology
code search
product search
```

Different domains need different analyzers.

---

# 48. Ecommerce Example

User searches:

```text
running shoe
```

Documents:

```text
runner shoes
running sneakers
run shoes
```

Stemming helps retrieve all relevant products.

---

# 49. Log Search Example

Log systems often disable stemming because:

```text
exact terms matter
```

Example:

```text
ERROR_500
ERROR_404
```

Should not be normalized aggressively.

---

# 50. Common Mistakes

## Mistake 1

```text
Removing all stopwords blindly
```

Can damage phrase meaning.

---

## Mistake 2

```text
Over-aggressive stemming
```

Can reduce precision badly.

---

## Mistake 3

```text
Using English stemmer for all languages
```

Different languages need different analyzers.

---

## Mistake 4

```text
Ignoring domain-specific vocabulary
```

Medical/legal/code search often need custom analyzers.

---

## Mistake 5

```text
Applying stemming to IDs or keywords
```

Exact identifiers should remain unchanged.

---

# 51. Interview Explanation

If interviewer asks:

```text
Why use stemming in search engines?
```

Strong answer:

```text
Stemming reduces related word forms into common roots so queries can
match documents containing variations of the same concept. It improves
recall by allowing terms like "running", "runs", and "runner" to match
queries for "run".
```

Senior addition:

```text
However aggressive stemming can reduce precision by causing unrelated
words to collapse into the same root.
```

---

# 52. Final Mental Model

```text
Raw Tokens
    ↓
Stopword Removal
    ↓
Stemming / Lemmatization
    ↓
Canonical Search Tokens
```

Goal:

```text
reduce noise
+
normalize related terms
```

---

# 53. What To Remember

```text
Stopwords are low-information common words.

Removing stopwords reduces noise and index size.

Stemming improves recall.

Lemmatization is more accurate than stemming.

Aggressive stemming may reduce precision.

Porter stemmer is classic rule-based stemmer.

Search-time and index-time stemming must align.

Different languages require different analyzers.
```

---

# 54. Next File

```text
006_Inverted_Index.md
```

Next you learn:

```text
core heart of search engines
word → documents mapping
posting lists
term dictionaries
document IDs
fast retrieval
index construction
compressed posting lists
```
