package org.magnos.trie;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class TestAutoComplete
{

   public static void main(String[] args)
   {
      Scanner dictionaryInput = new Scanner( TestAutoComplete.class.getResourceAsStream( "dictionary.txt" ) );
      List<String> dictionary = new ArrayList<String>();

      long t0 = System.nanoTime();
      
      while (dictionaryInput.hasNextLine())
      {
         dictionary.add( dictionaryInput.nextLine() );
      }
      
      dictionaryInput.close();
      
      long t1 = System.nanoTime();
      
      Trie<String, Boolean> trie = Trie.forInsensitiveStrings( Boolean.FALSE );
      
      boolean had = false;
      
      for (String word : dictionary)
      {
         if (word.startsWith( "hap" ))
         {
            had = true;
         }
         else if (had && !word.startsWith("ha"))
         {
            trie.hashCode();
         }
         
         trie.put( word, Boolean.TRUE );
      }
      
      long t2 = System.nanoTime();
      
      System.out.format( "Dictionary of %d words loaded in %.9f seconds.\n", dictionary.size(), (t1 - t0) * 0.000000001 );
      System.out.format( "Trie built in %.9f seconds.\n", (t2 - t1) * 0.000000001 );
      
      Scanner in = new Scanner( System.in );
      
      while (in.hasNextLine())
      {
//         String line = in.nextLine();
         
//         System.out.println( trie.takeSequences( line, TrieMatch.STARTS_WITH, new TreeSet<String>() ) );
      }
      
      in.close();
   }
   
}
