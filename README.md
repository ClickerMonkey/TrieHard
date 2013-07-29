TrieHard
========

A generic [Trie](https://en.wikipedia.org/wiki/Trie) implementation in Java. String and char[] implementations are included.

### Code Example

```java
Trie<String, Boolean> t = Trie.forStrings();

// Include
t.put( "java.lang.", Boolean.TRUE );
t.put( "java.io.", Boolean.TRUE );
t.put( "java.util.concurrent.", Boolean.TRUE );

// Exclude
t.put( "java.util.", Boolean.FALSE );
t.put( "java.lang.Boolean", Boolean.FALSE );

assertTrue(  t.get( "java.lang.Integer", TrieMatch.STARTS_WITH ) );
assertTrue(  t.get( "java.lang.Long", TrieMatch.STARTS_WITH ) );
assertFalse( t.get( "java.lang.Boolean", TrieMatch.STARTS_WITH ) );
assertTrue(  t.get( "java.io.InputStream", TrieMatch.STARTS_WITH ) );
assertFalse( t.get( "java.util.ArrayList", TrieMatch.STARTS_WITH ) );
assertTrue(  t.get( "java.util.concurrent.ConcurrentHashMap", TrieMatch.STARTS_WITH ) );
```

### How does it work compared to other Tries?

Normal Trie implementations have a character per node, however TrieHard has a sequence of characters per node. This method saves memory and is just as efficient.

### What are the matching options and how do they work?

1. PARTIAL
2. STARTS_WITH
3. EXACT 

### How do I create my own Trie type?

Implement the following interface and pass it into the constructor of Trie.

```java
public interface TrieSequencer<S> 
{
   public int matches(S sequenceA, int indexA, S sequenceB, int indexB, int count);
   public int lengthOf(S sequence);
   public int hashOf(S sequence, int index);
   public S subSequence(S sequence, int start, int end);
   public S combine(S sequenceA, S sequenceB);
}
```

### TODO

Document entire library, write tests that cover 100% of code.
