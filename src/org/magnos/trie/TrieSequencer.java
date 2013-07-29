package org.magnos.trie;

public interface TrieSequencer<S> 
{
   public int matches(S sequenceA, int indexA, S sequenceB, int indexB, int count);
   public int lengthOf(S sequence);
   public int hashOf(S sequence, int index);
   public S subSequence(S sequence, int start, int end);
   public S combine(S sequenceA, S sequenceB);
}
