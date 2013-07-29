package org.magnos.trie;

import static org.junit.Assert.*;

import org.junit.Test;


public class TestTrieSequencerCharSequence
{

   public static final String SEQ1 = "HELLO";
   public static final String SEQ2 = "HE";
   public static final String SEQ3 = "WORLD";
   public static final String SEQ4 = "HELLOWORLD";
   public static final String SEQ5 = "WOW";
   
   @Test
   public void testMatches()
   {
      TrieSequencer<String> seq = new TrieSequencerCharSequence<String>();
      
      assertEquals( 2, seq.matches( SEQ1, 0, SEQ2, 0, 2 ) );
      assertEquals( 0, seq.matches( SEQ1, 1, SEQ2, 0, 2 ) );
      
      assertEquals( 5, seq.matches( SEQ3, 0, SEQ4, 5, 5 ) );
      assertEquals( 2, seq.matches( SEQ5, 0, SEQ4, 5, 2 ) );
   }
   
   @Test
   public void testLengthOf()
   {
      TrieSequencer<String> seq = new TrieSequencerCharSequence<String>();
      
      assertEquals( 5, seq.lengthOf( SEQ1 ) );
      assertEquals( 2, seq.lengthOf( SEQ2 ) );
      assertEquals( 5, seq.lengthOf( SEQ3 ) );
      assertEquals( 10, seq.lengthOf( SEQ4 ) );
      assertEquals( 3, seq.lengthOf( SEQ5 ) );
   }
   
   @Test
   public void testHashOf()
   {
      TrieSequencer<String> seq = new TrieSequencerCharSequence<String>();
      
      assertEquals( 'W', seq.hashOf( SEQ3, 0 ) );
      assertEquals( 'O', seq.hashOf( SEQ3, 1 ) );
      assertEquals( 'R', seq.hashOf( SEQ3, 2 ) );
      assertEquals( 'L', seq.hashOf( SEQ3, 3 ) );
      assertEquals( 'D', seq.hashOf( SEQ3, 4 ) );
   }
   
   @Test
   public void testSubSequence()
   {
      TrieSequencer<String> seq = new TrieSequencerCharSequence<String>();
      
      assertEquals( "HELL", seq.subSequence( SEQ1, 0, 4 ) );
      assertEquals( "LO", seq.subSequence( SEQ1, 3, 5 ) );
      assertEquals( "E", seq.subSequence( SEQ1, 1, 2 ) );
   }
   
   @Test
   public void testCombine()
   {
      TrieSequencer<String> seq = new TrieSequencerCharSequence<String>();
      
      assertEquals( SEQ4, seq.combine( SEQ1, SEQ3 ) );
   }
   
}
