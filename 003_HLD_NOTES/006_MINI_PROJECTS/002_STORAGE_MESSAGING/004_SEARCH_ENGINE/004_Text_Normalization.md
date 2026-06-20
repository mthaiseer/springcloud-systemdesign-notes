# 004_Text_Normalization.md

# MiniSearchEngine — 004 Text Normalization

## 0. Why This File Exists

Raw text is messy.

Users may type:

```text
Redis
REDIS
redis
rédis
ReDiS
```

But search engine should understand:

```text
all represent same concept
```

Without normalization:

```text
search quality becomes poor
duplicate tokens increase
matching becomes inconsistent
ranking quality drops
```

This file teaches:

```text
what normalization is
case folding
accent removal
Unicode normalization
diacritics
stemming
lemmatization
synonyms
canonical forms
normalization pipelines
search quality optimization
```

Only ASCII diagrams are used.

---

# 1. One-Line Definition

```text
Text normalization converts different textual representations into consistent searchable forms.
```

---

# 2. Biggest Mental Model

Normalization solves:

```text
same meaning
different representation
```

Core flow:

```text
Raw Text
    ↓
Normalization
    ↓
Canonical Form
    ↓
Index/Search
```

---

# 3. Why Normalization Needed

Suppose indexed token:

```text
redis
```

User searches:

```text
Redis
```

Without normalization:

```text
NO MATCH
```

With normalization:

```text
redis = Redis
```

Search succeeds.

---

# 4. Normalization ASCII

```text
REDIS
Redis
redis

    ↓ normalization

redis
```

---

# 5. Canonical Form

Normalization converts text into:

```text
canonical form
```

Meaning:

```text
single standard representation
```

---

# 6. Case Folding

Most common normalization.

Converts:

```text
UPPERCASE
MixedCase
```

into:

```text
lowercase
```

---

# 7. Case Folding ASCII

```text
"Redis FAST"

      ↓ lowercase

"redis fast"
```

---

# 8. Why Lowercase Important

Without lowercasing:

```text
Redis
REDIS
redis
```

become:

```text
3 separate tokens
```

This creates:

```text
larger index
bad matching
ranking inconsistencies
```

---

# 9. Accent / Diacritic Problem

Example:

```text
café
resume
naïve
```

Users may type:

```text
cafe
resume
naive
```

Search engine should still match.

---

# 10. Accent Removal

Normalization may remove accents.

Example:

```text
café → cafe
naïve → naive
résumé → resume
```

---

# 11. Accent Removal ASCII

```text
café
résumé

    ↓ normalize accents

cafe
resume
```

---

# 12. Unicode Problem

Unicode allows many ways to represent same character.

Example:

```text
é
```

may have multiple internal byte representations.

Search engine must normalize them.

---

# 13. Unicode Normalization

Unicode normalization converts:

```text
visually same characters
```

into:

```text
single binary representation
```

Important for multilingual search.

---

# 14. Unicode ASCII

```text
Different Unicode forms
        ↓
Unicode Normalization
        ↓
Canonical Unicode form
```

---

# 15. Whitespace Normalization

Users may type:

```text
redis     caching
```

or:

```text
redis caching
```

Normalization reduces extra spaces.

---

# 16. Whitespace ASCII

```text
"redis     caching"

        ↓

"redis caching"
```

---

# 17. Punctuation Normalization

Example:

```text
redis-cache
redis/cache
redis.cache
```

Search engine may normalize similarly.

---

# 18. Punctuation ASCII

```text
redis-cache

      ↓ normalize

redis cache
```

---

# 19. Symbol Normalization

Example:

```text
C++
C#
Node.js
```

Special symbols may require custom rules.

---

# 20. Numeric Normalization

Example:

```text
1000
1,000
1000.0
```

May represent same value.

Normalization helps consistency.

---

# 21. HTML Normalization

Input:

```html
<b>Redis</b>
```

Normalization removes tags:

```text
Redis
```

---

# 22. HTML ASCII

```text
<b>Redis</b>

      ↓ strip HTML

Redis
```

---

# 23. URL Normalization

URLs may contain:

```text
https
www
query params
trailing slash
```

Normalization reduces duplicates.

Example:

```text
https://redis.io/
http://redis.io
```

may normalize similarly.

---

# 24. Email Normalization

Example:

```text
John@gmail.com
john@gmail.com
```

May normalize to:

```text
john@gmail.com
```

---

# 25. Stemming

Stemming reduces words to root-like form.

Example:

```text
running
runs
runner
```

becomes:

```text
run
```

Goal:

```text
increase recall
```

---

# 26. Stemming ASCII

```text
running
runs
runner

      ↓ stemming

run
```

---

# 27. Stemming Problem

Stemming is approximate.

Example:

```text
university
universe
```

may incorrectly stem similarly.

Tradeoff:

```text
speed vs accuracy
```

---

# 28. Lemmatization

Lemmatization uses language knowledge.

Example:

```text
better → good
went → go
```

More accurate than stemming.

---

# 29. Lemmatization ASCII

```text
went
going
goes

    ↓ lemmatization

go
```

---

# 30. Stemming vs Lemmatization

| Feature | Stemming | Lemmatization |
|---|---|---|
| Accuracy | Lower | Higher |
| Speed | Faster | Slower |
| Language Awareness | Weak | Strong |
| Simplicity | Simple | Complex |

---

# 31. Synonym Normalization

Example:

```text
car
automobile
vehicle
```

Search engine may normalize into common concept.

---

# 32. Synonym ASCII

```text
car
automobile
vehicle

      ↓ synonym expansion

same conceptual group
```

---

# 33. Stopword Normalization

Common words may be removed.

Examples:

```text
the
is
a
an
```

Input:

```text
Redis is fast
```

Output:

```text
redis fast
```

---

# 34. Stopword ASCII

```text
redis
is
fast

   ↓ remove stopword

redis
fast
```

---

# 35. Token Canonicalization

Goal:

```text
different tokens
→
same normalized token
```

Example:

```text
Redis
REDIS
redis

→ redis
```

---

# 36. Why Canonicalization Important

Benefits:

```text
smaller index
better matching
better ranking
consistent retrieval
```

---

# 37. Search-Time vs Index-Time Normalization

Very important concept.

Documents normalized during:

```text
index time
```

Queries normalized during:

```text
search time
```

Both should align.

---

# 38. Index/Search Normalization ASCII

```text
Document Text
      ↓
Index Normalizer
      ↓
Indexed Tokens

Query Text
      ↓
Search Normalizer
      ↓
Query Tokens

Then:
matching occurs
```

---

# 39. Bad Normalization Mismatch

Index:

```text
cafe
```

Query:

```text
café
```

If query analyzer does NOT remove accents:

```text
NO MATCH
```

---

# 40. Language-Specific Normalization

Different languages need different rules.

Examples:

```text
English
Arabic
Japanese
Chinese
Hindi
```

Some languages need:

```text
special stemming
compound splitting
morphological analysis
```

---

# 41. Search Quality Tradeoffs

Aggressive normalization:

```text
better recall
more matches
```

But:

```text
lower precision
incorrect matches possible
```

Strict normalization:

```text
better precision
fewer false matches
```

But:

```text
may miss relevant results
```

---

# 42. Lucene Normalization Mental Model

Apache Lucene uses:

```text
Analyzer Pipeline
```

Pipeline:

```text
Character Filters
+
Tokenizer
+
Token Filters
```

Normalization mostly occurs in:

```text
token filters
```

---

# 43. Elasticsearch Analyzer Example

Example pipeline:

```text
Standard Tokenizer
    ↓
Lowercase Filter
    ↓
ASCIIFolding Filter
    ↓
Stopword Filter
```

---

# 44. ASCIIFolding Filter

Converts accented characters into ASCII.

Example:

```text
café → cafe
```

Very common in Elasticsearch.

---

# 45. Java Lowercase Normalization

```java
public String normalizeCase(
        String text) {

    return text.toLowerCase();
}
```

Explanation:

```text
Converts all characters into lowercase.
```

---

# 46. Java Accent Removal

```java
import java.text.Normalizer;

public String removeAccents(
        String text) {

    String normalized =
            Normalizer.normalize(
                    text,
                    Normalizer.Form.NFD
            );

    return normalized.replaceAll(
            "\\p{M}",
            ""
    );
}
```

---

# 47. Accent Removal Dry Run

Input:

```text
café
```

Step 1:

```text
Unicode decomposition
```

Step 2:

```text
remove accent marks
```

Final:

```text
cafe
```

---

# 48. Java Whitespace Normalization

```java
public String normalizeWhitespace(
        String text) {

    return text.trim()
               .replaceAll("\\s+", " ");
}
```

Example:

```text
"redis     caching"
```

becomes:

```text
"redis caching"
```

---

# 49. Java Full Normalizer

```java
import java.text.Normalizer;

public class TextNormalizer {

    public String normalize(String text) {

        text = text.toLowerCase();

        text = Normalizer.normalize(
                text,
                Normalizer.Form.NFD
        );

        text = text.replaceAll(
                "\\p{M}",
                ""
        );

        text = text.replaceAll(
                "[^a-z0-9 ]",
                " "
        );

        text = text.replaceAll(
                "\\s+",
                " "
        );

        return text.trim();
    }
}
```

---

# 50. Full Dry Run

Input:

```text
"  Café, REDIS!!!  "
```

Step 1 — lowercase:

```text
"  café, redis!!!  "
```

Step 2 — accent removal:

```text
"  cafe, redis!!!  "
```

Step 3 — punctuation cleanup:

```text
"  cafe  redis     "
```

Step 4 — whitespace normalization:

```text
"cafe redis"
```

Final normalized text:

```text
cafe redis
```

---

# 51. Production Search Example

User query:

```text
café redis
```

Indexed document:

```text
Cafe Redis Tutorial
```

Normalization converts both into:

```text
cafe redis tutorial
```

Search now matches correctly.

---

# 52. Why Search Engines Normalize Aggressively

Without normalization:

```text
duplicate tokens increase
index size increases
query matching becomes inconsistent
```

Normalization improves:

```text
retrieval consistency
ranking quality
search recall
```

---

# 53. Production Challenges

Real systems handle:

```text
multi-language text
emoji
Unicode complexity
accent variations
mixed languages
typos
special characters
```

Normalization rules become complex.

---

# 54. Common Mistakes

## Mistake 1

```text
Only lowercase normalization
```

Real systems need Unicode + accent normalization too.

---

## Mistake 2

```text
Over-aggressive stemming
```

May create wrong matches.

---

## Mistake 3

```text
Different index/search normalization rules
```

Can break search matching.

---

## Mistake 4

```text
Ignoring multilingual text
```

English-only normalization breaks global search.

---

## Mistake 5

```text
Removing all punctuation blindly
```

Can hurt:
C++
Node.js
C#
version numbers

---

# 55. Interview Explanation

If interviewer asks:

```text
What is text normalization?
```

Strong answer:

```text
Text normalization converts different textual representations into
consistent canonical forms before indexing and searching. It includes
lowercasing, accent removal, Unicode normalization, stemming, whitespace
cleanup, and synonym handling to improve retrieval consistency.
```

---

# 56. Final Mental Model

```text
Raw Text
    ↓
Normalization Pipeline
    ↓
Canonical Tokens
    ↓
Index/Search
```

Goal:

```text
same meaning
→
same searchable representation
```

---

# 57. What To Remember

```text
Normalization creates canonical text forms.

Lowercasing is critical.

Accent removal improves multilingual matching.

Unicode normalization prevents binary mismatches.

Stemming improves recall.

Lemmatization improves semantic accuracy.

Search-time and index-time normalization must align.

Aggressive normalization improves recall but may reduce precision.
```

---

# 58. Next File

```text
005_Stopwords_Stemming.md
```

Next you learn:

```text
deep stopword processing
stemming algorithms
Porter stemmer
lemmatization deeper
recall vs precision
stemming tradeoffs
query expansion
language analyzers
```
