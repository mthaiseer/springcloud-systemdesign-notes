# 003_Tokenization.md

# MiniSearchEngine — 003 Tokenization

## 0. Why This File Exists

Search engines cannot directly search raw text efficiently.

Before building an inverted index, raw text must be converted into:

```text
searchable tokens
```

This conversion is called:

```text
tokenization
```

Tokenization is one of the most important search engine internals because it decides:

```text
what words are searchable
what words match queries
how phrase search works
how autocomplete works
how fuzzy search works
how ranking counts terms
```

Bad tokenization causes:

```text
missing results
wrong results
bad ranking
poor autocomplete
bad multilingual search
```

Only ASCII diagrams are used.

---

# 1. One-Line Definition

```text
Tokenization is the process of converting raw text into small searchable units called tokens.
```

Example:

```text
"Redis is Fast"
```

becomes:

```text
redis
is
fast
```

---

# 2. Biggest Mental Model

Search engines do not search raw paragraphs.

They search:

```text
tokens
```

Core flow:

```text
Raw Text
    ↓
Tokenizer
    ↓
Token Stream
    ↓
Inverted Index
```

---

# 3. Why Tokenization Is Needed

Raw document:

```text
Redis caching is fast
```

If stored as one string:

```text
"Redis caching is fast"
```

then query:

```text
redis
```

cannot be matched efficiently.

Search engine needs:

```text
redis
caching
is
fast
```

so each word can be mapped to documents.

---

# 4. Tokenization ASCII

```text
Raw Text:
"Redis caching is fast"

        ↓ tokenizer

Tokens:
redis
caching
is
fast
```

---

# 5. What Is A Token

A token is:

```text
a searchable text unit
```

Depending on tokenizer rules, a token can be:

```text
word
number
email part
URL part
symbol
emoji
ngram
prefix
```

Example:

```text
"iPhone 15 Pro"
```

Tokens:

```text
iphone
15
pro
```

---

# 6. Token Stream

Tokenizer produces a stream of tokens.

```text
Input:
"Redis is fast"

Token Stream:
[redis] → [is] → [fast]
```

Search engine later stores these tokens in inverted index.

---

# 7. Token Stream With Position

Search engines often store token position.

Text:

```text
redis caching fast
```

Token stream:

```text
redis    position=1
caching  position=2
fast     position=3
```

Positions are important for:

```text
phrase search
highlighting
proximity search
```

---

# 8. Tokenizer vs Analyzer

Tokenizer:

```text
splits text into tokens
```

Analyzer:

```text
full pipeline that may include tokenizer + filters
```

Analyzer flow:

```text
Raw Text
    ↓
Character Filters
    ↓
Tokenizer
    ↓
Token Filters
    ↓
Final Tokens
```

---

# 9. Simple Whitespace Tokenizer

Whitespace tokenizer splits by spaces.

Input:

```text
Redis caching fast
```

Output:

```text
redis
caching
fast
```

ASCII:

```text
"Redis caching fast"
          ↓ split by space
redis | caching | fast
```

---

# 10. Problem With Whitespace Tokenizer

Input:

```text
Redis, Kafka & Elasticsearch!
```

Whitespace tokenizer output:

```text
Redis,
Kafka
&
Elasticsearch!
```

Bad tokens:

```text
Redis,
Elasticsearch!
```

Problem:

```text
punctuation remains attached
```

So real tokenizers need punctuation handling.

---

# 11. Punctuation Handling

Common punctuation:

```text
, . ! ? : ; ( ) [ ] { } " '
```

Usually removed or used as token boundary.

Example:

```text
"Redis, Kafka!"
```

Final tokens:

```text
redis
kafka
```

ASCII:

```text
Redis, Kafka!
     ↓ remove punctuation
Redis Kafka
     ↓ tokenize
redis
kafka
```

---

# 12. Lowercase Normalization

Case normalization is critical.

Without lowercasing:

```text
Redis
REDIS
redis
```

become different tokens.

With lowercasing:

```text
redis
redis
redis
```

---

# 13. Lowercase ASCII

```text
"Redis FAST"
      ↓ lowercase
"redis fast"
      ↓ tokenize
redis
fast
```

---

# 14. Numbers Handling

Input:

```text
iPhone 15 Pro Max
```

Possible tokens:

```text
iphone
15
pro
max
```

Numbers matter in search:

```text
iphone 15
java 17
postgres 16
```

---

# 15. Email Tokenization

Input:

```text
john@gmail.com
```

Possible tokens:

```text
john
gmail
com
```

But sometimes full email must be searchable as keyword:

```text
john@gmail.com
```

So field type matters:

```text
text field → tokenize
keyword field → exact match
```

---

# 16. URL Tokenization

Input:

```text
https://redis.io/docs
```

Possible tokens:

```text
https
redis
io
docs
```

Search engine analyzer decides.

---

# 17. Unicode Problem

Real systems handle:

```text
English
Arabic
Hindi
Japanese
Chinese
Korean
Emoji
```

Tokenization differs by language.

Example:

```text
hello world
```

space-separated.

But some languages do not use spaces the same way.

So production search engines use language-specific analyzers.

---

# 18. Stopwords

Stopwords are common words with low search value.

Examples:

```text
the
is
a
an
of
to
in
```

Input:

```text
Redis is fast
```

After stopword removal:

```text
redis
fast
```

---

# 19. Stopword ASCII

```text
redis
is
fast
    ↓ remove "is"
redis
fast
```

---

# 20. Stopword Tradeoff

Removing stopwords improves:

```text
smaller index
less noise
better ranking sometimes
```

But can hurt phrase queries.

Example:

```text
"to be or not to be"
```

If stopwords removed aggressively, meaning may break.

---

# 21. Stemming

Stemming reduces words to root-like form.

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

# 22. Stemming ASCII

```text
running
runs
runner
      ↓ stemming
run
```

---

# 23. Lemmatization

Lemmatization is smarter than stemming.

Example:

```text
better → good
went   → go
```

It uses language knowledge.

Stemming is simpler and faster.

Lemmatization is more accurate but heavier.

---

# 24. Synonyms

Search engine may expand synonyms.

Example:

```text
car
automobile
vehicle
```

If user searches:

```text
car
```

engine may also match:

```text
automobile
vehicle
```

---

# 25. Synonym ASCII

```text
query token:
car

expanded tokens:
car
automobile
vehicle
```

---

# 26. Edge N-Gram Tokenization

Used for autocomplete.

Input:

```text
redis
```

Edge n-grams:

```text
r
re
red
redi
redis
```

When user types:

```text
red
```

it can match:

```text
redis
```

---

# 27. Edge N-Gram ASCII

```text
redis
  ↓
r
re
red
redi
redis
```

---

# 28. N-Gram Tokenization

N-gram splits word into fixed-size chunks.

Input:

```text
redis
```

3-grams:

```text
red
edi
dis
```

Useful for:

```text
fuzzy search
partial matching
typo tolerance
```

---

# 29. Token Positions For Phrase Search

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
redis   → 1
caching → 2
fast    → 3
```

Phrase matches because:

```text
redis position 1
caching position 2
```

consecutive order.

---

# 30. Phrase Search ASCII

```text
Doc tokens:
1: redis
2: caching
3: fast

Query:
"redis caching"

Check:
redis at 1
caching at 2

Result:
phrase matched
```

---

# 31. Index-Time vs Search-Time Analysis

Search engine analyzes text twice:

```text
index time
search time
```

Index time:

```text
documents are tokenized and stored
```

Search time:

```text
query is tokenized and matched
```

---

# 32. Index/Search Analyzer ASCII

```text
Document Text
     ↓
Index Analyzer
     ↓
Indexed Tokens

Query Text
     ↓
Search Analyzer
     ↓
Query Tokens

Then:
query tokens match indexed tokens
```

---

# 33. Bad Analyzer Mismatch

Index analyzer:

```text
running → run
```

Search analyzer:

```text
running → running
```

Now query token may not match indexed token.

Rule:

```text
index-time and search-time analysis must be compatible
```

---

# 34. Analyzer Pipeline Deep View

```text
Raw Text
    ↓
Character Filter
    ↓
Tokenizer
    ↓
Lowercase Filter
    ↓
Stopword Filter
    ↓
Stemmer Filter
    ↓
Synonym Filter
    ↓
Final Tokens
```

---

# 35. Character Filter

Character filter modifies raw text before tokenization.

Examples:

```text
remove HTML
replace symbols
normalize Unicode
```

Example:

```text
<b>Redis</b>
```

becomes:

```text
Redis
```

---

# 36. Token Filter

Token filter modifies tokens after tokenization.

Examples:

```text
lowercase
remove stopwords
apply stemming
expand synonyms
```

---

# 37. Lucene Analyzer Mental Model

Lucene analyzer is:

```text
Character Filters
+
Tokenizer
+
Token Filters
```

Elasticsearch analyzers are built on top of this idea.

---

# 38. Elasticsearch Standard Analyzer

Typical flow:

```text
Standard Tokenizer
    ↓
Lowercase Filter
    ↓
Stopword Filter
```

Input:

```text
Redis is FAST!
```

Final:

```text
redis
fast
```

---

# 39. Java Token Class

```java
public class Token {

    private final String text;
    private final int position;

    public Token(String text, int position) {
        this.text = text;
        this.position = position;
    }

    public String getText() {
        return text;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return text + "@" + position;
    }
}
```

Explanation:

```text
Each token stores:
actual token text
+
position inside document
```

Position helps phrase search later.

---

# 40. Java Basic Tokenizer

```java
import java.util.ArrayList;
import java.util.List;

public class BasicTokenizer {

    public List<Token> tokenize(String input) {

        List<Token> tokens =
                new ArrayList<>();

        String cleaned =
                input.toLowerCase()
                     .replaceAll("[^a-z0-9 ]", " ");

        String[] parts =
                cleaned.split("\\s+");

        int position = 0;

        for (String part : parts) {

            if (part.isBlank()) {
                continue;
            }

            position++;

            tokens.add(
                    new Token(part, position)
            );
        }

        return tokens;
    }
}
```

Code explanation:

```text
1. lowercase text
2. replace punctuation with spaces
3. split by whitespace
4. assign token positions
5. return token stream
```

---

# 41. Java Stopword Filter

```java
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StopwordFilter {

    private final Set<String> stopwords =
            Set.of("the", "is", "a", "an", "of", "to");

    public List<Token> filter(List<Token> tokens) {

        return tokens.stream()
                .filter(token ->
                        !stopwords.contains(token.getText()))
                .collect(Collectors.toList());
    }
}
```

Explanation:

```text
Removes very common low-value words.
```

---

# 42. Java Simple Analyzer

```java
import java.util.List;

public class SimpleAnalyzer {

    private final BasicTokenizer tokenizer =
            new BasicTokenizer();

    private final StopwordFilter stopwordFilter =
            new StopwordFilter();

    public List<Token> analyze(String text) {

        List<Token> tokens =
                tokenizer.tokenize(text);

        return stopwordFilter.filter(tokens);
    }
}
```

Analyzer mental model:

```text
tokenizer
+
filters
```

---

# 43. Java Dry Run

Input:

```text
"Redis is FAST!"
```

Step 1 — lowercase:

```text
redis is fast!
```

Step 2 — punctuation cleanup:

```text
redis is fast
```

Step 3 — split:

```text
redis@1
is@2
fast@3
```

Step 4 — stopword filter removes:

```text
is
```

Final tokens:

```text
redis@1
fast@3
```

---

# 44. Full Search Index Dry Run

Document:

```text
Doc1 = "Redis is FAST!"
```

Analyzer output:

```text
redis
fast
```

Inverted index:

```text
redis → [Doc1]
fast  → [Doc1]
```

Query:

```text
redis
```

Analyzer output:

```text
redis
```

Lookup:

```text
redis → [Doc1]
```

Result:

```text
Doc1
```

---

# 45. Autocomplete Dry Run With Edge N-Gram

Word:

```text
redis
```

Index-time tokens:

```text
r
re
red
redi
redis
```

User types:

```text
red
```

Search token:

```text
red
```

Lookup:

```text
red → redis document
```

Suggestion:

```text
redis
```

---

# 46. Performance Tradeoffs

More token filters mean:

```text
better search quality
```

but also:

```text
more CPU
slower indexing
larger token processing pipeline
```

N-grams improve autocomplete but create:

```text
many more tokens
larger index
more memory usage
```

---

# 47. Production Mapping

In Elasticsearch:

```text
Analyzer
    =
Tokenizer + Token Filters
```

In Lucene:

```text
Analyzer produces TokenStream
```

In search backend:

```text
TokenStream builds inverted index
```

---

# 48. Common Mistakes

## Mistake 1

```text
Using only whitespace split
```

Real text contains punctuation, URLs, emails, Unicode.

---

## Mistake 2

```text
Ignoring lowercase normalization
```

Redis and redis become different tokens.

---

## Mistake 3

```text
Removing stopwords blindly
```

May break phrase meaning.

---

## Mistake 4

```text
Using different index/search analyzers incorrectly
```

Tokens may not match.

---

## Mistake 5

```text
Using n-grams on every field
```

Index size explodes.

---

# 49. Interview Explanation

If interviewer asks:

```text
What is tokenization in search engines?
```

Strong answer:

```text
Tokenization converts raw text into searchable tokens. Search engines
use analyzers made of character filters, tokenizers, and token filters
to normalize text before storing it in inverted indexes.
```

Senior addition:

```text
Token positions are also stored so the engine can support phrase search,
highlighting, and proximity queries.
```

---

# 50. Final Mental Model

```text
Raw Text
    ↓
Character Filters
    ↓
Tokenizer
    ↓
Token Filters
    ↓
Token Stream
    ↓
Inverted Index
```

Search engines search:

```text
tokens
```

not raw sentences.

---

# 51. What To Remember

```text
Tokenization converts text into searchable tokens.

Analyzer = tokenizer + filters.

Lowercase normalization improves matching.

Stopwords reduce noise.

Stemming improves recall.

Token positions enable phrase search.

N-grams enable autocomplete and fuzzy matching.

Index-time and search-time analyzers must be compatible.
```

---

# 52. Next File

```text
004_Text_Normalization.md
```

Next you learn:

```text
case folding
accent removal
Unicode normalization
diacritics
stemming vs lemmatization
synonyms
normalization tradeoffs
search quality optimization
```
