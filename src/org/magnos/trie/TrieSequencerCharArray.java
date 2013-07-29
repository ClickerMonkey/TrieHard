
package org.magnos.trie;

import java.util.Arrays;

public class TrieSequencerCharArray implements TrieSequencer<char[]>
{

   @Override
   public int matches( char[] sequenceA, int indexA, char[] sequenceB, int indexB, int count )
   {
      for (int i = 0; i < count; i++)
      {
         if (sequenceA[indexA + i] != sequenceB[indexB + i])
         {
            return i;
         }
      }
      
      return count;
   }

   @Override
   public int lengthOf( char[] sequence )
   {
      return sequence.length;
   }

   @Override
   public int hashOf( char[] sequence, int i )
   {
      return sequence[i];
   }

   @Override
   public char[] subSequence( char[] sequence, int start, int end )
   {
      return Arrays.copyOfRange( sequence, start, end );
   }

   @Override
   public char[] combine( char[] sequenceA, char[] sequenceB )
   {
      char[] combined = new char[ sequenceA.length + sequenceB.length ];
      
      System.arraycopy( sequenceA, 0, combined, 0, sequenceA.length );
      System.arraycopy( sequenceB, 0, combined, sequenceA.length, sequenceB.length );
      
      return combined;
   }

}
