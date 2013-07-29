package org.magnos.trie;

import static org.junit.Assert.*;

import org.junit.Test;


public class TestTrieSequencerCharArray
{

   public static final char[] SEQ1 = "HELLO".toCharArray();
   public static final char[] SEQ2 = "HE".toCharArray();
   public static final char[] SEQ3 = "WORLD".toCharArray();
   public static final char[] SEQ4 = "HELLOWORLD".toCharArray();
   public static final char[] SEQ5 = "WOW".toCharArray();
   
   @Test
   public void testMatches()
   {
      TrieSequencer<char[]> seq = new TrieSequencerCharArray();
      
      assertEquals( 2, seq.matches( SEQ1, 0, SEQ2, 0, 2 ) );
      assertEquals( 0, seq.matches( SEQ1, 1, SEQ2, 0, 2 ) );
      
      assertEquals( 5, seq.matches( SEQ3, 0, SEQ4, 5, 5 ) );
      assertEquals( 2, seq.matches( SEQ5, 0, SEQ4, 5, 2 ) );
   }
   
   @Test
   public void testLengthOf()
   {
      TrieSequencer<char[]> seq = new TrieSequencerCharArray();
      
      assertEquals( 5, seq.lengthOf( SEQ1 ) );
      assertEquals( 2, seq.lengthOf( SEQ2 ) );
      assertEquals( 5, seq.lengthOf( SEQ3 ) );
      assertEquals( 10, seq.lengthOf( SEQ4 ) );
      assertEquals( 3, seq.lengthOf( SEQ5 ) );
   }
   
   @Test
   public void testHashOf()
   {
      TrieSequencer<char[]> seq = new TrieSequencerCharArray();
      
      assertEquals( 'W', seq.hashOf( SEQ3, 0 ) );
      assertEquals( 'O', seq.hashOf( SEQ3, 1 ) );
      assertEquals( 'R', seq.hashOf( SEQ3, 2 ) );
      assertEquals( 'L', seq.hashOf( SEQ3, 3 ) );
      assertEquals( 'D', seq.hashOf( SEQ3, 4 ) );
   }
   
   @Test
   public void testSubSequence()
   {
      TrieSequencer<char[]> seq = new TrieSequencerCharArray();
      
      assertArrayEquals( "HELL".toCharArray(), seq.subSequence( SEQ1, 0, 4 ) );
      assertArrayEquals( "LO".toCharArray(), seq.subSequence( SEQ1, 3, 5 ) );
      assertArrayEquals( "E".toCharArray(), seq.subSequence( SEQ1, 1, 2 ) );
   }
   
   @Test
   public void testCombine()
   {
      TrieSequencer<char[]> seq = new TrieSequencerCharArray();
      
      assertArrayEquals( SEQ4, seq.combine( SEQ1, SEQ3 ) );
   }
   
}
