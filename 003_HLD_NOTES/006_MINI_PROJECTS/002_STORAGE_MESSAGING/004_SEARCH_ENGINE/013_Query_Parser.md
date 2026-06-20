# 013_Query_Parser.md

# MiniSearchEngine — 013 Query Parser

## 0. Why This File Exists

Users do not send clean internal search commands.

They type messy queries like:

```text
redis caching
"redis caching"
redis AND kafka
redis OR kafka
redis NOT kafka
title:redis
redis^2 kafka
```

A search engine must convert this raw query text into an executable internal structure.

That component is:

```text
Query Parser
```

Query Parser is important because it converts:

```text
human search input
```

into:

```text
search engine query plan
```

This file teaches:

```text
what query parser is
why query parsing matters
tokenizing queries
Boolean queries
phrase queries
field queries
boost queries
query trees
operator precedence
Lucene query objects
Elasticsearch query DSL mental model
Java mini parser
dry runs
production tradeoffs
```

Only ASCII diagrams are used.

---

# 1. One-Line Definition

```text
Query Parser converts raw user query text into executable search query objects.
```

Example:

```text
redis AND kafka
```

becomes:

```text
BooleanQuery(
    MUST redis,
    MUST kafka
)
```

---

# 2. Biggest Mental Model

User input:

```text
string
```

Search engine needs:

```text
structured query tree
```

Flow:

```text
Raw Query
    ↓
Query Parser
    ↓
Query Tree
    ↓
Index Reader
    ↓
Search Results
```

---

# 3. Why Query Parser Needed

Raw query:

```text
redis caching
```

could mean:

```text
redis OR caching
redis AND caching
phrase match
rank docs containing either term
```

Search engine must decide how to interpret it.

---

# 4. Query Parser ASCII

```text
User Query
    ↓
Tokenizer
    ↓
Syntax Parser
    ↓
Query Tree
    ↓
Executable Search
```

---

# 5. Query Parser Responsibilities

Query Parser handles:

```text
simple terms
multi-term queries
Boolean operators
phrase queries
field queries
boosts
parentheses
syntax errors
```

---

# 6. Raw Query vs Parsed Query

Raw:

```text
redis AND kafka
```

Parsed:

```text
AND
├── redis
└── kafka
```

This tree can be executed by Index Reader.

---

# 7. Query Tree Mental Model

A query becomes a tree.

Example:

```text
redis AND kafka
```

Tree:

```text
        AND
       /   \
   redis   kafka
```

---

# 8. Simple Term Query

Raw query:

```text
redis
```

Parsed as:

```text
TermQuery(redis)
```

Execution:

```text
lookup posting list for redis
```

---

# 9. Simple Term Query ASCII

```text
redis
  ↓
TermQuery
  ↓
posting list lookup
```

---

# 10. Multi-Term Query

Raw query:

```text
redis caching
```

Common interpretation:

```text
should match both or either depending on default operator
```

Many systems default to:

```text
OR
```

or use relevance scoring across terms.

---

# 11. Multi-Term Query ASCII

```text
redis caching
      ↓
Query Parser
      ↓
BooleanQuery
├── redis
└── caching
```

---

# 12. Boolean AND Query

Raw:

```text
redis AND caching
```

Meaning:

```text
document must contain both terms
```

Parsed tree:

```text
        AND
       /   \
   redis   caching
```

---

# 13. Boolean AND Execution

Posting lists:

```text
redis   → [1,2,5]
caching → [1,5]
```

Intersection:

```text
[1,5]
```

---

# 14. Boolean OR Query

Raw:

```text
redis OR kafka
```

Meaning:

```text
document may contain either term
```

Parsed tree:

```text
        OR
       /  \
   redis  kafka
```

---

# 15. Boolean OR Execution

Posting lists:

```text
redis → [1,3]
kafka → [2,3]
```

Union:

```text
[1,2,3]
```

---

# 16. Boolean NOT Query

Raw:

```text
redis NOT kafka
```

Meaning:

```text
documents containing redis
but excluding kafka
```

Parsed tree:

```text
        NOT
       /   \
   redis   kafka
```

---

# 17. Parentheses

Raw:

```text
(redis OR kafka) AND caching
```

Parentheses control execution order.

Tree:

```text
          AND
         /   \
       OR   caching
      /  \
 redis  kafka
```

---

# 18. Operator Precedence

Common order:

```text
parentheses first
NOT
AND
OR
```

Without precedence, query meaning becomes ambiguous.

---

# 19. Precedence Example

Query:

```text
redis OR kafka AND caching
```

Usually parsed as:

```text
redis OR (kafka AND caching)
```

not:

```text
(redis OR kafka) AND caching
```

---

# 20. Phrase Query

Raw:

```text
"redis caching"
```

Parsed as:

```text
PhraseQuery(redis, caching)
```

Execution requires:

```text
position checking
```

---

# 21. Phrase Query ASCII

```text
"redis caching"
        ↓
PhraseQuery
        ↓
check redis position followed by caching position
```

---

# 22. Phrase Query Execution

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
2 = 1 + 1
```

---

# 23. Field Query

Raw:

```text
title:redis
```

Meaning:

```text
search redis only in title field
```

Parsed as:

```text
FieldQuery(field=title, term=redis)
```

---

# 24. Field Query ASCII

```text
title:redis
     ↓
field = title
term = redis
```

---

# 25. Field Query Use Case

Documents have fields:

```text
title
body
tags
author
```

Query:

```text
title:redis
```

should not search body.

Only title index is checked.

---

# 26. Boost Query

Raw:

```text
redis^2 kafka
```

Meaning:

```text
redis is twice as important as kafka
```

Parsed as:

```text
Boost(redis, 2.0)
kafka normal
```

---

# 27. Boost Query ASCII

```text
redis^2
   ↓
term redis
boost 2x
```

---

# 28. Wildcard Query

Raw:

```text
redi*
```

May match:

```text
redis
redirect
redirection
```

Useful but expensive.

---

# 29. Prefix Query

Raw:

```text
red*
```

Parsed as:

```text
PrefixQuery(red)
```

Used for:

```text
autocomplete
prefix search
```

---

# 30. Fuzzy Query

Raw:

```text
redsi~1
```

Meaning:

```text
match terms within edit distance 1
```

May match:

```text
redis
```

---

# 31. Range Query

Raw:

```text
price:[100 TO 500]
```

Meaning:

```text
price between 100 and 500
```

Common in product search.

---

# 32. Query Parsing Pipeline

```text
Raw Query String
      ↓
Lexical Tokenization
      ↓
Syntax Parsing
      ↓
Query Tree
      ↓
Query Rewriting
      ↓
Executable Query
```

---

# 33. Lexical Tokenization

Break raw query into tokens.

Example:

```text
redis AND kafka
```

Tokens:

```text
redis
AND
kafka
```

---

# 34. Syntax Parsing

Convert tokens into structure.

Tokens:

```text
redis AND kafka
```

Tree:

```text
AND(redis,kafka)
```

---

# 35. Query Rewriting

Some queries are rewritten internally.

Example:

```text
red*
```

may rewrite into:

```text
redis OR redisson OR redirect
```

---

# 36. Query Rewrite ASCII

```text
PrefixQuery(red)
      ↓
Term expansion
      ↓
BooleanQuery(redis OR redirect OR redisson)
```

---

# 37. Analyzer At Search Time

Before parsing terms, query text may be analyzed:

```text
lowercase
stopword removal
stemming
synonyms
```

Example:

```text
Running
```

becomes:

```text
run
```

---

# 38. Query Analyzer ASCII

```text
User Query
    ↓
Search Analyzer
    ↓
Normalized Tokens
    ↓
Query Parser
```

---

# 39. Search-Time Analyzer Must Match Index-Time Analyzer

If index stores:

```text
run
```

but query produces:

```text
running
```

match may fail.

Rule:

```text
index analyzer and query analyzer must be compatible
```

---

# 40. Lucene Query Objects

Lucene represents parsed queries using objects like:

```text
TermQuery
BooleanQuery
PhraseQuery
PrefixQuery
FuzzyQuery
RangeQuery
```

---

# 41. Elasticsearch Query DSL Mental Model

Elasticsearch JSON DSL is also structured query form.

Example:

```json
{
  "match": {
    "title": "redis"
  }
}
```

This becomes internal Lucene query objects.

---

# 42. Query Parser vs Query Executor

Query Parser:

```text
understands query syntax
builds query tree
```

Index Reader / Searcher:

```text
executes query tree
reads posting lists
scores documents
```

---

# 43. Parser Error Handling

Bad query:

```text
redis AND
```

Parser should return:

```text
syntax error
```

or recover gracefully.

---

# 44. Production Query Parser Challenges

Real systems handle:

```text
syntax errors
escaping quotes
nested parentheses
field aliases
user typos
large Boolean queries
security limits
```

---

# 45. Java QueryNode Interface

```java
public interface QueryNode {

    boolean matches(
            java.util.Set<String> documentTerms
    );
}
```

Meaning:

```text
every query object can check if document matches
```

---

# 46. Java TermQuery

```java
import java.util.Set;

public class TermQuery implements QueryNode {

    private final String term;

    public TermQuery(String term) {
        this.term = term.toLowerCase();
    }

    @Override
    public boolean matches(
            Set<String> documentTerms) {

        return documentTerms.contains(term);
    }
}
```

---

# 47. Java Boolean AND Query

```java
import java.util.Set;

public class AndQuery implements QueryNode {

    private final QueryNode left;
    private final QueryNode right;

    public AndQuery(
            QueryNode left,
            QueryNode right) {

        this.left = left;
        this.right = right;
    }

    @Override
    public boolean matches(
            Set<String> documentTerms) {

        return left.matches(documentTerms)
                && right.matches(documentTerms);
    }
}
```

---

# 48. Java Boolean OR Query

```java
import java.util.Set;

public class OrQuery implements QueryNode {

    private final QueryNode left;
    private final QueryNode right;

    public OrQuery(
            QueryNode left,
            QueryNode right) {

        this.left = left;
        this.right = right;
    }

    @Override
    public boolean matches(
            Set<String> documentTerms) {

        return left.matches(documentTerms)
                || right.matches(documentTerms);
    }
}
```

---

# 49. Java Simple Parser

```java
public class SimpleQueryParser {

    public QueryNode parse(String query) {

        String[] parts =
                query.toLowerCase()
                     .split("\\s+");

        if (parts.length == 1) {
            return new TermQuery(parts[0]);
        }

        if (parts.length == 3
                && parts[1].equals("and")) {

            return new AndQuery(
                    new TermQuery(parts[0]),
                    new TermQuery(parts[2])
            );
        }

        if (parts.length == 3
                && parts[1].equals("or")) {

            return new OrQuery(
                    new TermQuery(parts[0]),
                    new TermQuery(parts[2])
            );
        }

        throw new IllegalArgumentException(
                "Unsupported query: " + query
        );
    }
}
```

---

# 50. Java Parser Dry Run

Input:

```text
redis AND kafka
```

Tokens:

```text
redis
and
kafka
```

Parser output:

```text
AndQuery(
    TermQuery(redis),
    TermQuery(kafka)
)
```

---

# 51. Execution Dry Run

Document terms:

```text
redis
kafka
cache
```

Query:

```text
redis AND kafka
```

Execution:

```text
TermQuery(redis) → true
TermQuery(kafka) → true
AND → true
```

Document matches.

---

# 52. Negative Dry Run

Document terms:

```text
redis
cache
```

Query:

```text
redis AND kafka
```

Execution:

```text
TermQuery(redis) → true
TermQuery(kafka) → false
AND → false
```

Document does not match.

---

# 53. Query Tree ASCII Dry Run

```text
Raw:
redis AND kafka

Tree:

        AND
       /   \
   redis   kafka
```

---

# 54. Parser vs Analyzer Example

Raw query:

```text
Running AND Fast
```

Analyzer converts:

```text
running → run
fast → fast
```

Parser builds:

```text
AND(run, fast)
```

---

# 55. Performance Considerations

Complex queries cost more:

```text
large Boolean queries
wildcard queries
fuzzy queries
deep nested parentheses
```

Search engines often enforce query limits.

---

# 56. Security Considerations

Query parser must protect against:

```text
very expensive wildcard queries
query explosion
deep recursion
large Boolean clauses
```

---

# 57. Common Mistakes

## Mistake 1

```text
Treating query string as simple text always
```

Search queries may contain syntax.

---

## Mistake 2

```text
Ignoring operator precedence
```

Changes meaning.

---

## Mistake 3

```text
Not handling phrase queries
```

Phrase search requires special parsing.

---

## Mistake 4

```text
Allowing unlimited wildcard/fuzzy queries
```

Can overload search cluster.

---

# 58. Interview Explanation

If interviewer asks:

```text
What does Query Parser do?
```

Strong answer:

```text
Query Parser converts raw user query text into structured query objects
such as term, Boolean, phrase, prefix, fuzzy, and range queries. These
objects are then executed by the Index Reader against posting lists.
```

Senior addition:

```text
A production query parser also handles analyzer compatibility, operator
precedence, query rewriting, syntax errors, and expensive-query limits.
```

---

# 59. Final Mental Model

```text
Raw User Query
      ↓
Analyze Text
      ↓
Parse Syntax
      ↓
Build Query Tree
      ↓
Execute Against Index
```

---

# 60. What To Remember

```text
Query Parser converts strings into query objects.

Boolean queries become query trees.

Phrase queries require positions.

Field queries restrict search to fields.

Boosts change scoring weight.

Wildcard/fuzzy queries may be expensive.

Lucene executes parsed query objects.

Elasticsearch DSL becomes Lucene queries internally.
```

---

# 61. Next File

```text
014_Boolean_Search.md
```

Next you learn:

```text
AND OR NOT queries
posting list intersection
union
difference
query execution optimization
Boolean retrieval model
Lucene BooleanQuery internals
```
