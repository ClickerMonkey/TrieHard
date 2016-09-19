/* 
 * NOTICE OF LICENSE
 * 
 * This source file is subject to the Open Software License (OSL 3.0) that is 
 * bundled with this package in the file LICENSE.txt. It is also available 
 * through the world-wide-web at http://opensource.org/licenses/osl-3.0.php
 * If you did not receive a copy of the license and are unable to obtain it 
 * through the world-wide-web, please send an email to magnos.software@gmail.com 
 * so we can send you a copy immediately. If you use any of this software please
 * notify me via our website or email, your feedback is much appreciated. 
 * 
 * @copyright   Copyright (c) 2011 Magnos Software (http://www.magnos.org)
 * @license     http://opensource.org/licenses/osl-3.0.php
 *              Open Software License (OSL 3.0)
 */

package org.magnos.trie;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class TestTrieSequencerCharSequenceCaseInsensitive
{

   public static final String SEQ1 = "HeLlO";
   public static final String SEQ2 = "he";
   public static final String SEQ3 = "WORLd";
   public static final String SEQ4 = "helloWORLD";
   public static final String SEQ5 = "woW";
   
   @Test
   public void testMatches()
   {
      TrieSequencer<String> seq = new TrieSequencerCharSequenceCaseInsensitive<String>();
      
      assertEquals( 2, seq.matches( SEQ1, 0, SEQ2, 0, 2 ) );
      assertEquals( 0, seq.matches( SEQ1, 1, SEQ2, 0, 2 ) );
      
      assertEquals( 5, seq.matches( SEQ3, 0, SEQ4, 5, 5 ) );
      assertEquals( 2, seq.matches( SEQ5, 0, SEQ4, 5, 2 ) );
   }
   
   @Test
   public void testLengthOf()
   {
      TrieSequencer<String> seq = new TrieSequencerCharSequenceCaseInsensitive<String>();
      
      assertEquals( 5, seq.lengthOf( SEQ1 ) );
      assertEquals( 2, seq.lengthOf( SEQ2 ) );
      assertEquals( 5, seq.lengthOf( SEQ3 ) );
      assertEquals( 10, seq.lengthOf( SEQ4 ) );
      assertEquals( 3, seq.lengthOf( SEQ5 ) );
   }
   
   @Test
   public void testHashOf()
   {
      TrieSequencer<String> seq = new TrieSequencerCharSequenceCaseInsensitive<String>();
      
      assertEquals( 'w', seq.hashOf( SEQ3, 0 ) );
      assertEquals( 'o', seq.hashOf( SEQ3, 1 ) );
      assertEquals( 'r', seq.hashOf( SEQ3, 2 ) );
      assertEquals( 'l', seq.hashOf( SEQ3, 3 ) );
      assertEquals( 'd', seq.hashOf( SEQ3, 4 ) );
   }
   
}
