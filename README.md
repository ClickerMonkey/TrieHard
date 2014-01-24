TrieHard
========

![Stable](http://i4.photobucket.com/albums/y123/Freaklotr4/stage_stable.png)

A generic [Trie](https://en.wikipedia.org/wiki/Trie) implementation in Java. TrieHard comes ready to create Tries of many types:  
`String`, `char[]`, `byte[]`, `int[]`, `short[]`, `long[]`, and `java.nio.ByteBuffer`

### Code Example

```java
Trie<String, Boolean> t = Tries.forStrings();

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

### Performance

You can insert __millions__ of keys/values into TrieHard in a second (average insert on all dictionary words is 300-400 nanoseconds)
as well as retrieve __millions__ of values in a second (average retrieval is 200-300 nanoseconds).

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

### Builds
- [TrieHard-1.0.jar](https://github.com/ClickerMonkey/TrieHard/raw/master/build/TrieHard-1.0.jar)
- [TrieHard-src-1.0.jar](https://github.com/ClickerMonkey/TrieHard/raw/master/build/TrieHard-1.0-src.jar) - includes source code

### Use Cases

#### Auto-Complete

```java
Trie<String, Integer> t = Tries.forInsensitiveStrings();
t.setDefaultMatch( TrieMatch.PARTIAL );
// Add all available values to the Trie
t.put( "world", 23 );
t.put( "worm", 45 );
t.put( "worry", 76 );
t.put( "why", -89 );
t.put( "women", 123 );
...
// Given user input, what are possible keys & values?
String userInput = "WO";
Set<Entry<String, Integer>> possible = t.nodes( userInput );
// possible = { world=>23, worm=>45, worry=>76, women=>123 }
...
// Use possible to display full keys and their values.
```

#### IP to Host Mapping

```java
Trie<byte[], String> mapper = Tries.forBytes();
mapper.setDefaultMatch( TrieMatch.EXACT );
...
mapper.put( socketAddress.getAddress(), "google.com" );
...

// Given an IP, get the host name
String host = mapper.get( socketAddress.getAddress() );
```

#### To see other use cases, request one!

### How do I create my own Trie type?

Implement the following interface and pass it into the constructor of Trie.

```java
public interface TrieSequencer<S> 
{
   public int matches(S sequenceA, int indexA, S sequenceB, int indexB, int count);
   public int lengthOf(S sequence);
   public int hashOf(S sequence, int index);
}
```
