# 014_Boolean_Search.md

# MiniSearchEngine — 014 Boolean Search

## 0. Why This File Exists

Search engines must support queries like:

```text
redis AND kafka
redis OR kafka
redis NOT kafka
(redis OR kafka) AND caching
```

These are called:

```text
Boolean queries
```

Boolean search is one of the oldest and most important retrieval models.

It teaches how search engines combine posting lists using:

```text
intersection
union
difference
```

This file teaches:

```text
Boolean retrieval
AND search
OR search
NOT search
posting list intersection
posting list union
posting list difference
query trees
execution order
optimization
Lucene BooleanQuery
Elasticsearch bool query
Java implementation
dry runs
production tradeoffs
```

Only ASCII diagrams are used.

---

# 1. One-Line Definition

```text
Boolean search combines terms using logical operators like AND, OR, and NOT.
```

---

# 2. Biggest Mental Model

Boolean search is:

```text
set operations over posting lists
```

Core mapping:

```text
AND → intersection
OR  → union
NOT → difference
```

---

# 3. Boolean Search ASCII

```text
Query:
redis AND kafka

      ↓

redis posting list
kafka posting list

      ↓

intersect lists

      ↓

matching documents
```

---

# 4. Why Boolean Search Exists

Users need precise control.

Example:

```text
redis AND kafka
```

means:

```text
must contain both redis and kafka
```

Example:

```text
redis NOT kafka
```

means:

```text
contains redis but excludes kafka
```

---

# 5. Posting List Refresher

Posting list:

```text
term → documents containing term
```

Example:

```text
redis → [1,3,8]
kafka → [2,3,8]
```

---

# 6. Boolean Operators

Common operators:

```text
AND
OR
NOT
```

Advanced:

```text
MUST
SHOULD
MUST_NOT
FILTER
```

used in Lucene/Elasticsearch.

---

# 7. AND Query

Query:

```text
redis AND kafka
```

Meaning:

```text
document must contain both terms
```

Operation:

```text
intersection
```

---

# 8. AND Example

Posting lists:

```text
redis → [1,3,8]
kafka → [2,3,8]
```

Intersection:

```text
[3,8]
```

---

# 9. AND ASCII

```text
redis → [1,3,8]
kafka → [2,3,8]

common docs:
[3,8]
```

---

# 10. OR Query

Query:

```text
redis OR kafka
```

Meaning:

```text
document may contain either term
```

Operation:

```text
union
```

---

# 11. OR Example

Posting lists:

```text
redis → [1,3,8]
kafka → [2,3,8]
```

Union:

```text
[1,2,3,8]
```

---

# 12. OR ASCII

```text
redis → [1,3,8]
kafka → [2,3,8]

all unique docs:
[1,2,3,8]
```

---

# 13. NOT Query

Query:

```text
redis NOT kafka
```

Meaning:

```text
documents containing redis
but excluding kafka
```

Operation:

```text
difference
```

---

# 14. NOT Example

Posting lists:

```text
redis → [1,3,8]
kafka → [2,3,8]
```

Difference:

```text
redis - kafka = [1]
```

---

# 15. NOT ASCII

```text
redis docs:
[1,3,8]

kafka docs:
[2,3,8]

remove kafka docs:
[1]
```

---

# 16. Boolean Query Tree

Boolean queries become trees.

Query:

```text
(redis OR kafka) AND caching
```

Tree:

```text
          AND
         /   \
       OR   caching
      /  \
 redis  kafka
```

---

# 17. Why Query Tree Important

Search engine executes:

```text
children first
then parent operator
```

Tree controls execution order.

---

# 18. Operator Precedence

Common precedence:

```text
parentheses
NOT
AND
OR
```

Example:

```text
redis OR kafka AND caching
```

Usually means:

```text
redis OR (kafka AND caching)
```

---

# 19. Execution Plan Mental Model

Boolean search creates an execution plan:

```text
parse query
load posting lists
combine posting lists
score/filter documents
return results
```

---

# 20. AND Execution Flow

```text
Query:
redis AND kafka

      ↓

load redis postings
load kafka postings

      ↓

intersect

      ↓

matching docs
```

---

# 21. Sorted Posting Lists

Posting lists are sorted by docID.

Example:

```text
redis → [1,3,8,10]
kafka → [3,8,9]
```

Sorted order enables:

```text
two-pointer intersection
```

---

# 22. Two-Pointer Intersection

Algorithm:

```text
compare current docIDs
if equal → match
move smaller pointer
```

Complexity:

```text
O(n + m)
```

---

# 23. Two-Pointer Dry Run

```text
A = [1,3,8]
B = [2,3,8]
```

Step 1:

```text
1 < 2
move A
```

Step 2:

```text
3 > 2
move B
```

Step 3:

```text
3 == 3
match 3
```

Step 4:

```text
8 == 8
match 8
```

Result:

```text
[3,8]
```

---

# 24. Union Algorithm

For OR query:

```text
merge two sorted lists
avoid duplicates
```

Example:

```text
A = [1,3,8]
B = [2,3,8]
```

Union:

```text
[1,2,3,8]
```

---

# 25. Difference Algorithm

For NOT query:

```text
scan include list
skip docs present in exclude list
```

Example:

```text
A = [1,3,8]
B = [2,3,8]
```

Difference:

```text
[1]
```

---

# 26. Why Shortest Posting List First

For AND queries, search engines usually start with:

```text
shortest posting list
```

Reason:

```text
fewer candidate documents
faster intersection
```

---

# 27. Optimization ASCII

```text
rare term postings:
[5,9]

common term postings:
[1,2,3,4,5,6,7,8,9]

Start with rare list
      ↓
less work
```

---

# 28. Boolean Filter vs Scoring Query

Boolean clauses may be:

```text
scoring clauses
filter clauses
```

Filter clauses:

```text
must match
but do not affect score
```

Scoring clauses:

```text
affect relevance score
```

---

# 29. Elasticsearch Bool Query Mapping

Elasticsearch uses:

```text
must
should
must_not
filter
```

Mapping:

```text
must     → AND + scoring
should   → OR + scoring
must_not → NOT
filter   → AND without scoring
```

---

# 30. Elasticsearch Bool Mental Model

```text
bool
 ├── must
 ├── should
 ├── must_not
 └── filter
```

---

# 31. Lucene BooleanQuery

Lucene internally uses:

```text
BooleanQuery
BooleanClause
```

Clauses:

```text
MUST
SHOULD
MUST_NOT
FILTER
```

---

# 32. MUST Clause

MUST means:

```text
required match
```

Equivalent to:

```text
AND
```

---

# 33. SHOULD Clause

SHOULD means:

```text
optional match
```

Often increases score.

Equivalent to:

```text
OR-like scoring
```

---

# 34. MUST_NOT Clause

MUST_NOT means:

```text
exclude matching documents
```

Equivalent to:

```text
NOT
```

---

# 35. FILTER Clause

FILTER means:

```text
must match
but no score impact
```

Common for:

```text
category filters
price filters
date filters
security filters
```

---

# 36. Search Example — Ecommerce

Query:

```text
laptop AND dell NOT refurbished
```

Meaning:

```text
must contain laptop
must contain dell
must not contain refurbished
```

---

# 37. Ecommerce ASCII

```text
laptop      → [1,2,3,4]
dell        → [2,3]
refurbished → [3]

laptop AND dell:
[2,3]

remove refurbished:
[2]
```

---

# 38. Filter Example

Query:

```text
search term = laptop
filter category = electronics
filter price < 1000
```

Search:

```text
laptop affects score
```

Filters:

```text
category and price reduce candidates
```

---

# 39. Filter ASCII

```text
Search Query
      ↓
Candidate Docs
      ↓
Apply Filters
      ↓
Score Remaining Docs
```

---

# 40. Why Filters Are Fast

Filters can be cached because:

```text
they do not depend on scoring
```

Example:

```text
category:electronics
```

may be reused across many queries.

---

# 41. Boolean Query With Ranking

Boolean search can be combined with BM25.

Example:

```text
redis OR kafka
```

Documents matching both terms usually score higher.

---

# 42. Boolean + BM25 ASCII

```text
Boolean Retrieval
      ↓
Candidate Docs
      ↓
BM25 Scoring
      ↓
Ranked Results
```

---

# 43. Phrase + Boolean

Query:

```text
"redis caching" AND java
```

Requires:

```text
phrase match
+
term match
```

---

# 44. Query Explosion Problem

Large OR queries can become expensive.

Example:

```text
term1 OR term2 OR term3 OR ... term10000
```

Problem:

```text
too many posting lists
too much memory
slow search
```

---

# 45. Production Limits

Search systems enforce:

```text
max boolean clauses
timeout
query complexity limits
```

to protect cluster.

---

# 46. Java QueryNode Interface

```java
import java.util.Set;

public interface QueryNode {

    boolean matches(
            Set<String> documentTerms
    );
}
```

---

# 47. Java TermQuery

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

# 48. Java AndQuery

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

# 49. Java OrQuery

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

# 50. Java NotQuery

```java
import java.util.Set;

public class NotQuery implements QueryNode {

    private final QueryNode include;
    private final QueryNode exclude;

    public NotQuery(
            QueryNode include,
            QueryNode exclude) {

        this.include = include;
        this.exclude = exclude;
    }

    @Override
    public boolean matches(
            Set<String> documentTerms) {

        return include.matches(documentTerms)
                && !exclude.matches(documentTerms);
    }
}
```

---

# 51. Java Boolean Dry Run

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

# 52. Java Negative Dry Run

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
redis → true
kafka → false
AND → false
```

Document does not match.

---

# 53. Java Posting Intersection

```java
import java.util.*;

public class PostingOperations {

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

# 54. Java Union Operation

```java
import java.util.*;

public List<Integer> union(
        List<Integer> a,
        List<Integer> b) {

    Set<Integer> set =
            new TreeSet<>();

    set.addAll(a);
    set.addAll(b);

    return new ArrayList<>(set);
}
```

---

# 55. Java Difference Operation

```java
import java.util.*;

public List<Integer> difference(
        List<Integer> include,
        List<Integer> exclude) {

    Set<Integer> excludeSet =
            new HashSet<>(exclude);

    List<Integer> result =
            new ArrayList<>();

    for (Integer docId : include) {

        if (!excludeSet.contains(docId)) {
            result.add(docId);
        }
    }

    return result;
}
```

---

# 56. Full Boolean Search Dry Run

Posting lists:

```text
laptop      → [1,2,3,4]
dell        → [2,3]
refurbished → [3]
```

Query:

```text
laptop AND dell NOT refurbished
```

Step 1:

```text
laptop AND dell
=
[2,3]
```

Step 2:

```text
remove refurbished
=
[2]
```

Final result:

```text
Doc2
```

---

# 57. Production Challenges

Real Boolean search handles:

```text
large posting lists
large OR clauses
nested query trees
filters
security constraints
query timeouts
distributed execution
```

---

# 58. Performance Tradeoffs

AND queries:

```text
usually faster with rare terms first
```

OR queries:

```text
can be expensive with many terms
```

NOT queries:

```text
require careful candidate filtering
```

---

# 59. Common Mistakes

## Mistake 1

```text
Executing AND lists in random order
```

Use shortest/rarest posting list first.

---

## Mistake 2

```text
Treating filters as scoring queries
```

Filters should often be cached and non-scoring.

---

## Mistake 3

```text
Allowing unlimited Boolean clauses
```

Can overload system.

---

## Mistake 4

```text
Ignoring operator precedence
```

Changes query meaning.

---

# 60. Interview Explanation

If interviewer asks:

```text
How does Boolean search work?
```

Strong answer:

```text
Boolean search combines posting lists using set operations. AND uses
intersection, OR uses union, and NOT uses difference. Search engines
optimize execution using sorted posting lists, rare-term-first execution,
filters, and cached bitsets.
```

Senior addition:

```text
Lucene implements this through BooleanQuery with clauses like MUST,
SHOULD, MUST_NOT, and FILTER.
```

---

# 61. Final Mental Model

```text
Boolean Search
    =
Set Operations On Posting Lists
```

Mapping:

```text
AND → intersection
OR  → union
NOT → difference
```

---

# 62. What To Remember

```text
Boolean search combines posting lists.

AND means intersection.

OR means union.

NOT means difference.

Lucene uses BooleanQuery.

Elasticsearch bool query maps to Lucene clauses.

Filters do not affect score.

Rare terms should be executed first.

Large Boolean queries can be expensive.
```

---

# 63. Next File

```text
015_Phrase_Search.md
```

Next you learn:

```text
phrase queries
positional indexes
token positions
slop
proximity search
highlighting
Lucene PhraseQuery internals
```
