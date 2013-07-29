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
