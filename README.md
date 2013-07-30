TrieHard
========

A generic [Trie](https://en.wikipedia.org/wiki/Trie) implementation in Java. String and char[] implementations are included.

### Code Example

```java
Trie<String, Boolean> t = Trie.forStrings();

// Include
t.put( "java.lang.", true );
t.put( "java.io.", true );
t.put( "java.util.concurrent.", true );

// Exclude
t.put( "java.util.", false );
t.put( "java.lang.Boolean", false );

assertTrue( t.get( "java.lang.Integer" ) );
assertTrue( t.get( "java.lang.Long" ) );
assertFalse( t.get( "java.lang.Boolean" ) );
assertTrue( t.get( "java.io.InputStream" ) );
assertFalse( t.get( "java.util.ArrayList" ) );
assertTrue( t.get( "java.util.concurrent.ConcurrentHashMap" ) );
```

### How does it work compared to other Tries?

A typical Trie implementation has an element (i.e. character) per node (a non-compact structure). The Trie implementation in this library is a compact Trie which saves space and is just as efficient.

### What are the matching options and how do they work?

Given a Trie `{ "java.io." => 23 }`...

1. __EXACT__  
  Only an equivalent "java.io." will result in 23.
2. __STARTS_WITH__   
  Any superset or equivalent of "java.io." will result in 23. I.E. "java.io.InputStream" is a STARTS_WITH match to the Trie.
3. __PARTIAL__   
  Any subset, superset, or equivalent of "java.io." will result in 23. I.E. "java" is a PARTIAL match to the Trie.

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

### How can I use it as an X?

#### Auto-Complete

```java
Trie<String, Integer> t = Trie.forInsensitiveStrings();
// Add all available values to the Trie
t.put( "world", 23 );
t.put( "worm", 45 );
t.put( "worry", 76 );
t.put( "why", -89 );
t.put( "women", 123 );
...
// Given user input, what are possible values?
String userInput = "wo";
Map<String, Integer> possible = t.takeValues( userInput, TrieMatch.PARTIAL, 
                                              new HashMap<String, Integer>() );
// possible = { world=>23, worm=>45, worry=>76, women=>123 }
...
// Use possible to display full keys and their values.
```

#### To see other use cases, request one!
