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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;


public class TestTrie
{

   @Test
   public void testEmptyConstructor()
   {
      Trie<String, Boolean> t = Trie.forStrings();

      assertEquals( 0, t.size() );
      assertTrue( t.isEmpty() );

      assertFalse( t.has( "word", TrieMatch.PARTIAL ) );
      assertFalse( t.has( "word", TrieMatch.PARTIAL ) );
      assertFalse( t.has( "word", TrieMatch.STARTS_WITH ) );

      assertFalse( t.has( "", TrieMatch.PARTIAL ) );
      assertFalse( t.has( "", TrieMatch.PARTIAL ) );
      assertFalse( t.has( "", TrieMatch.STARTS_WITH ) );
   }

   @Test
   public void testDefaultValueConstructor()
   {
      Trie<String, Boolean> t = Trie.forStrings( Boolean.FALSE );

      assertEquals( Boolean.FALSE, t.get( "meow" ) );

      t.put( "meow", Boolean.TRUE );

      assertEquals( Boolean.TRUE, t.get( "meow" ) );
      assertEquals( Boolean.FALSE, t.get( "world" ) );
   }

   @Test
   public void testSimplePut()
   {
      Trie<String, Boolean> t = Trie.forStrings();

      assertTrue( t.isEmpty() );

      t.put( "java.lang.", Boolean.TRUE );
      t.put( "java.io.", Boolean.TRUE );
      t.put( "java.util.concurrent.", Boolean.TRUE );
      t.put( "java.util.", Boolean.FALSE );
      t.put( "java.lang.Boolean", Boolean.FALSE );

      assertEquals( 5, t.size() );
      assertFalse( t.isEmpty() );

      assertTrue( t.get( "java.lang.Integer" ) );
      assertTrue( t.get( "java.lang.Long" ) );
      assertFalse( t.get( "java.lang.Boolean" ) );
      assertTrue( t.get( "java.io.InputStream" ) );
      assertFalse( t.get( "java.util.ArrayList" ) );
      assertTrue( t.get( "java.util.concurrent.ConcurrentHashMap" ) );
   }

   @Test
   public void testHasPartialMatch()
   {
      Trie<String, Boolean> t = Trie.forStrings();

      t.put( "bookshelf", Boolean.TRUE );
      t.put( "wowza", Boolean.FALSE );

      assertTrue( t.has( "wow", TrieMatch.PARTIAL ) );
      assertFalse( t.has( "wow", TrieMatch.STARTS_WITH ) );
      assertFalse( t.has( "wow", TrieMatch.EXACT ) );

      assertTrue( t.has( "book", TrieMatch.PARTIAL ) );
      assertFalse( t.has( "book", TrieMatch.STARTS_WITH ) );
      assertFalse( t.has( "book", TrieMatch.EXACT ) );
   }

   @Test
   public void testHasStartsWithMatch()
   {
      Trie<String, Boolean> t = Trie.forStrings();

      t.put( "bookshelf", Boolean.TRUE );
      t.put( "wowza", Boolean.FALSE );

      assertTrue( t.has( "wowzacowza", TrieMatch.PARTIAL ) );
      assertTrue( t.has( "wowzacowza", TrieMatch.STARTS_WITH ) );
      assertFalse( t.has( "wowzacowza", TrieMatch.EXACT ) );

      assertTrue( t.has( "wowzacowza", TrieMatch.PARTIAL ) );
      assertTrue( t.has( "wowzacowza", TrieMatch.STARTS_WITH ) );
      assertFalse( t.has( "wowzacowza", TrieMatch.EXACT ) );
   }

   @Test
   public void testHasExactMatch()
   {
      Trie<String, Boolean> t = Trie.forStrings();

      t.put( "bookshelf", Boolean.TRUE );
      t.put( "wowza", Boolean.FALSE );

      assertTrue( t.has( "wowza", TrieMatch.PARTIAL ) );
      assertTrue( t.has( "wowza", TrieMatch.STARTS_WITH ) );
      assertTrue( t.has( "wowza", TrieMatch.EXACT ) );

      assertTrue( t.has( "wowza", TrieMatch.PARTIAL ) );
      assertTrue( t.has( "wowza", TrieMatch.STARTS_WITH ) );
      assertTrue( t.has( "wowza", TrieMatch.EXACT ) );
   }

   @Test
   public void testGetPartialMatch()
   {
      Trie<String, Boolean> t = Trie.forStrings();

      t.put( "bookshelf", Boolean.TRUE );
      t.put( "wowza", Boolean.FALSE );

      assertEquals( Boolean.FALSE, t.get( "wow", TrieMatch.PARTIAL ) );
      assertEquals( null, t.get( "wow", TrieMatch.STARTS_WITH ) );
      assertEquals( null, t.get( "wow", TrieMatch.EXACT ) );

      assertEquals( Boolean.TRUE, t.get( "book", TrieMatch.PARTIAL ) );
      assertEquals( null, t.get( "book", TrieMatch.STARTS_WITH ) );
      assertEquals( null, t.get( "book", TrieMatch.EXACT ) );
   }

   @Test
   public void testGetStartsWithMatch()
   {
      Trie<String, Boolean> t = Trie.forStrings();

      t.put( "bookshelf", Boolean.TRUE );
      t.put( "wowza", Boolean.FALSE );

      assertEquals( Boolean.FALSE, t.get( "wowzacowza", TrieMatch.PARTIAL ) );
      assertEquals( Boolean.FALSE, t.get( "wowzacowza", TrieMatch.STARTS_WITH ) );
      assertEquals( null, t.get( "wowzacowza", TrieMatch.EXACT ) );

      assertEquals( Boolean.TRUE, t.get( "bookshelfmania", TrieMatch.PARTIAL ) );
      assertEquals( Boolean.TRUE, t.get( "bookshelfmania", TrieMatch.STARTS_WITH ) );
      assertEquals( null, t.get( "bookshelfmania", TrieMatch.EXACT ) );
   }

   @Test
   public void testGetExactMatch()
   {
      Trie<String, Boolean> t = Trie.forStrings();

      t.put( "bookshelf", Boolean.TRUE );
      t.put( "wowza", Boolean.FALSE );

      assertEquals( Boolean.FALSE, t.get( "wowza", TrieMatch.PARTIAL ) );
      assertEquals( Boolean.FALSE, t.get( "wowza", TrieMatch.STARTS_WITH ) );
      assertEquals( Boolean.FALSE, t.get( "wowza", TrieMatch.EXACT ) );

      assertEquals( Boolean.TRUE, t.get( "bookshelf", TrieMatch.PARTIAL ) );
      assertEquals( Boolean.TRUE, t.get( "bookshelf", TrieMatch.STARTS_WITH ) );
      assertEquals( Boolean.TRUE, t.get( "bookshelf", TrieMatch.EXACT ) );
   }

   @Test
   public void testTakeValues()
   {
      Trie<String, String> t = Trie.forStrings();

      t.put( "java.lang.", "LANG" );
      t.put( "java.io.", "IO" );
      t.put( "java.util.concurrent.", "CONCURRENT" );
      t.put( "java.util.", "UTIL" );
      t.put( "java.lang.Boolean", "BOOLEAN" );

      assertEquals( 5, t.size() );

      Set<String> values = t.takeValues( new HashSet<String>() );

      assertEquals( 5, values.size() );

      assertTrue( values.contains( "LANG" ) );
      assertTrue( values.contains( "IO" ) );
      assertTrue( values.contains( "CONCURRENT" ) );
      assertTrue( values.contains( "UTIL" ) );
      assertTrue( values.contains( "BOOLEAN" ) );
   }
   
   @Test
   public void testTakeValuesSubset()
   {
      Trie<String, String> t = Trie.forStrings();

      t.put( "java.lang.", "LANG" );
      t.put( "java.io.", "IO" );
      t.put( "java.util.concurrent.", "CONCURRENT" );
      t.put( "java.util.", "UTIL" );
      t.put( "java.lang.Boolean", "BOOLEAN" );
      
      assertEquals( 5, t.size() );

      Set<String> values = t.takeValues( "java.u", TrieMatch.PARTIAL, new HashSet<String>() );

      assertEquals( 2, values.size() );
      assertTrue( values.contains( "CONCURRENT" ) );
      assertTrue( values.contains( "UTIL" ) );
   }

   @Test
   public void testTakeSequences()
   {
      Trie<String, String> t = Trie.forStrings();

      t.put( "java.lang.", "LANG" );
      t.put( "java.io.", "IO" );
      t.put( "java.util.concurrent.", "CONCURRENT" );
      t.put( "java.util.", "UTIL" );
      t.put( "java.lang.Boolean", "BOOLEAN" );

      assertEquals( 5, t.size() );

      Set<String> values = t.takeSequences( new HashSet<String>() );

      assertEquals( 5, values.size() );

      assertTrue( values.contains( "java.lang." ) );
      assertTrue( values.contains( "java.io." ) );
      assertTrue( values.contains( "java.util.concurrent." ) );
      assertTrue( values.contains( "java.util." ) );
      assertTrue( values.contains( "java.lang.Boolean" ) );
   }

   @Test
   public void testTakeSequencesSubset()
   {
      Trie<String, String> t = Trie.forStrings();

      t.put( "java.lang.", "LANG" );
      t.put( "java.io.", "IO" );
      t.put( "java.util.concurrent.", "CONCURRENT" );
      t.put( "java.util.", "UTIL" );
      t.put( "java.lang.Boolean", "BOOLEAN" );

      assertEquals( 5, t.size() );

      Set<String> values = t.takeSequences( "java.u", TrieMatch.PARTIAL, new HashSet<String>() );

      assertEquals( 2, values.size() );
      assertTrue( values.contains( "java.util.concurrent." ) );
      assertTrue( values.contains( "java.util." ) );
   }

   @Test
   public void testTakeEntries()
   {
      Trie<String, Boolean> t = Trie.forStrings();

      t.put( "java.lang.", Boolean.TRUE );
      t.put( "java.io.", Boolean.TRUE );
      t.put( "java.util.concurrent.", Boolean.TRUE );
      t.put( "java.util.", Boolean.FALSE );
      t.put( "java.lang.Boolean", Boolean.FALSE );

      assertEquals( 5, t.size() );

      Map<String, Boolean> map = t.takeEntries( new HashMap<String, Boolean>() );

      assertEquals( 5, map.size() );

      assertEquals( Boolean.TRUE, map.get( "java.lang." ) );
      assertEquals( Boolean.TRUE, map.get( "java.io." ) );
      assertEquals( Boolean.TRUE, map.get( "java.util.concurrent." ) );
      assertEquals( Boolean.FALSE, map.get( "java.util." ) );
      assertEquals( Boolean.FALSE, map.get( "java.lang.Boolean" ) );
   }

   @Test
   public void testTakeEntriesSubset()
   {
      Trie<String, Boolean> t = Trie.forStrings();

      t.put( "java.lang.", Boolean.TRUE );
      t.put( "java.io.", Boolean.TRUE );
      t.put( "java.util.concurrent.", Boolean.TRUE );
      t.put( "java.util.", Boolean.FALSE );
      t.put( "java.lang.Boolean", Boolean.FALSE );

      assertEquals( 5, t.size() );

      Map<String, Boolean> map = t.takeEntries( "java.u", TrieMatch.PARTIAL, new HashMap<String, Boolean>() );

      assertEquals( 2, map.size() );

      assertEquals( Boolean.TRUE, map.get( "java.util.concurrent." ) );
      assertEquals( Boolean.FALSE, map.get( "java.util." ) );
   }

   @Test
   public void testRemoveBack()
   {
      Trie<String, Integer> t = Trie.forStrings();

      t.put( "hello", 0 );
      t.put( "hello world", 1 );

      assertEquals( 2, t.size() );
      assertEquals( 0, t.get( "hello" ).intValue() );
      assertEquals( 1, t.get( "hello world" ).intValue() );

      Integer r1 = t.remove( "hello world" );

      assertNotNull( r1 );
      assertEquals( 1, r1.intValue() );
      
      assertEquals( 1, t.size() );
      assertEquals( 0, t.get( "hello" ).intValue() );
      assertEquals( 0, t.get( "hello world" ).intValue() );
   }

   @Test
   public void testRemoveFront()
   {
      Trie<String, Integer> t = Trie.forStrings();

      t.put( "hello", 0 );
      t.put( "hello world", 1 );

      assertEquals( 2, t.size() );
      assertEquals( 0, t.get( "hello" ).intValue() );
      assertEquals( 1, t.get( "hello world" ).intValue() );

      Integer r0 = t.remove( "hello" );

      assertNotNull( r0 );
      assertEquals( 0, r0.intValue() );
      
      assertEquals( 1, t.size() );
      assertEquals( 1, t.get( "hello", TrieMatch.PARTIAL ).intValue() );
      assertEquals( 1, t.get( "hello world" ).intValue() );
   }

   @Test
   public void testRemoveFrontManyChildren()
   {
      Trie<String, Integer> t = Trie.forStrings();

      t.put( "hello", 0 );
      t.put( "hello world", 1 );
      t.put( "hello, clarice", 2 );

      assertEquals( 3, t.size() );
      assertEquals( 0, t.get( "hello" ).intValue() );
      assertEquals( 1, t.get( "hello world" ).intValue() );
      assertEquals( 2, t.get( "hello, clarice" ).intValue() );

      Integer r0 = t.remove( "hello" );

      assertNotNull( r0 );
      assertEquals( 0, r0.intValue() );
      
      assertEquals( 2, t.size() );
      assertNull( t.get( "hello", TrieMatch.PARTIAL ) );
      assertEquals( 1, t.get( "hello world" ).intValue() );
      assertEquals( 2, t.get( "hello, clarice" ).intValue() );
   }

   @Test
   public void testIterate()
   {
      Trie<String, Boolean> t = Trie.forStrings();

      t.put( "java.lang.", Boolean.TRUE );
      t.put( "java.io.", Boolean.TRUE );
      t.put( "java.util.concurrent.", Boolean.TRUE );
      t.put( "java.util.", Boolean.FALSE );
      t.put( "java.lang.Boolean", Boolean.FALSE );

      print( t );
   }

   public static <S, T> void print( Trie<S, T> trie )
   {
      trie.iterator( new TrieIterator<S, T>() {

         public void onEntry( S sequence, int index, T value, int depth )
         {
            for (int i = 0; i < index; i++)
            {
               System.out.print( ' ' );
            }
            System.out.print( sequence );
            if (value != null)
            {
               System.out.print( " = " );
               System.out.println( value );
            }
            else
            {
               System.out.println();
            }
         }
      } );
   }

}
