
package org.magnos.trie;

import java.nio.CharBuffer;


public class TrieSequencerCharSequence<S extends CharSequence> implements TrieSequencer<S>
{

   @Override
   public int matches( S sequenceA, int indexA, S sequenceB, int indexB, int count )
   {
      for (int i = 0; i < count; i++)
      {
         if (sequenceA.charAt( indexA + i ) != sequenceB.charAt( indexB + i ))
         {
            return i;
         }
      }

      return count;
   }

   @Override
   public int lengthOf( S sequence )
   {
      return sequence.length();
   }

   @Override
   public int hashOf( S sequence, int i )
   {
      return sequence.charAt( i );
   }

   @SuppressWarnings ("unchecked" )
   @Override
   public S subSequence( S sequence, int start, int end )
   {
      return (S)sequence.subSequence( start, end );
   }

   @SuppressWarnings ("unchecked" )
   @Override
   public S combine( S sequenceA, S sequenceB )
   {
      StringBuilder combined = new StringBuilder();
      combined.append( sequenceA );
      combined.append( sequenceB );

      Class<?> sequenceClass = sequenceB.getClass();

      if (sequenceClass == String.class)
      {
         return (S)combined.toString();
      }
      if (sequenceClass == StringBuilder.class)
      {
         return (S)combined;
      }
      if (sequenceClass == StringBuffer.class)
      {
         return (S)new StringBuffer( combined );
      }
      if (sequenceClass == CharBuffer.class)
      {
         return (S)CharBuffer.wrap( combined );
      }
      
      return null;
   }

}
