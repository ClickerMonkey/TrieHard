package org.magnos.trie;


public interface TrieIterator<S, T>
{
   public void onEntry(S sequence, int index, T value, int depth);
}
