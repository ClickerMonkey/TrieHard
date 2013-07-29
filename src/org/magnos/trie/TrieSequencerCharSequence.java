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

import java.nio.CharBuffer;


/**
 * A {@link TrieSequencer} implementation where any subclass of CharSequence
 * (i.e. String) is the sequence type. This implementation is case-sensitive.
 * 
 * @author Philip Diffenderfer
 * 
 */
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
