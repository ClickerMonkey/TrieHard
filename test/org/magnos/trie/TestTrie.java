
package org.magnos.trie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;


public class TestTrie
{

   public static void main( String[] args )
   {
      Trie<String, Boolean> t = Trie.forStrings();

      // Include
      t.put( "java.lang.", Boolean.TRUE );
      t.put( "java.io.", Boolean.TRUE );
      t.put( "java.util.concurrent.", Boolean.TRUE );

      print( t );

      // Exclude
      t.put( "java.util.", Boolean.FALSE );
      t.put( "java.lang.Boolean", Boolean.FALSE );

      // Display
      print( t );

      System.out.println( t.get( "java.lang.Integer", TrieMatch.STARTS_WITH ) );
      System.out.println( t.get( "java.lang.Long", TrieMatch.STARTS_WITH ) );
      System.out.println( t.get( "java.lang.Boolean", TrieMatch.STARTS_WITH ) );
      System.out.println( t.get( "java.io.InputStream", TrieMatch.STARTS_WITH ) );
      System.out.println( t.get( "java.util.ArrayList", TrieMatch.STARTS_WITH ) );
      System.out.println( t.get( "java.util.concurrent.ConcurrentHashMap", TrieMatch.STARTS_WITH ) );

      System.out.println();
      for (String s : t.takeSequences( new HashSet<String>() ))
      {
         System.out.println( s );
      }

      System.out.println();
      for (Boolean v : t.takeValues( new ArrayList<Boolean>() ))
      {
         System.out.println( v );
      }
      
      for (Entry<String, Boolean> e : t.takeEntries( new HashMap<String, Boolean>() ).entrySet() )
      {
         System.out.println( e.getKey() + " => " + e.getValue() );
      }
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
