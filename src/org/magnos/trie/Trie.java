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
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * An implementation of a compact Trie. <br/>
 * <br/>
 * <i>From Wikipedia:</i> <br/>
 * <br/>
 * <code>
 * an ordered tree data structure that is used to store a dynamic set or associative array where the keys are usually strings. Unlike a binary search tree, no node in the tree stores the key associated with that node; instead, its position in the tree defines the key with which it is associated. All the descendants of a node have a common prefix of the string associated with that node, and the root is associated with the empty string. Values are normally not associated with every node, only with leaves and some inner nodes that correspond to keys of interest. For the space-optimized presentation of prefix tree, see compact prefix tree.
 * </code> <br/>
 * 
 * @author Philip Diffenderfer
 * 
 * @param <S>
 *        The sequence/key type.
 * @param <T>
 *        The value type.
 */
public class Trie<S, T> implements Map<S, T>
{

   /**
    * Creates a Trie where the keys are case-sensitive Strings.
    * 
    * @return The reference to a newly instantiated Trie.
    */
   public static <T> Trie<String, T> forStrings()
   {
      return new Trie<String, T>( new TrieSequencerCharSequence<String>() );
   }

   /**
    * Creates a Trie where the keys are case-sensitive Strings.
    * 
    * @param defaultValue
    *        The default value of the Trie is the value returned when
    *        {@link #get(Object)} or {@link #get(Object, TrieMatch)} is called
    *        and no match was found.
    * @return The reference to a newly instantiated Trie.
    */
   public static <T> Trie<String, T> forStrings( T defaultValue )
   {
      return new Trie<String, T>( new TrieSequencerCharSequence<String>(), defaultValue );
   }

   /**
    * Creates a Trie where the keys are case-insensitive Strings.
    * 
    * @return The reference to a newly instantiated Trie.
    */
   public static <T> Trie<String, T> forInsensitiveStrings()
   {
      return new Trie<String, T>( new TrieSequencerCharSequenceCaseInsensitive<String>() );
   }

   /**
    * Creates a Trie where the keys are case-insensitive Strings.
    * 
    * @param defaultValue
    *        The default value of the Trie is the value returned when
    *        {@link #get(Object)} or {@link #get(Object, TrieMatch)} is called
    *        and no match was found.
    * @return The reference to a newly instantiated Trie.
    */
   public static <T> Trie<String, T> forInsensitiveStrings( T defaultValue )
   {
      return new Trie<String, T>( new TrieSequencerCharSequenceCaseInsensitive<String>(), defaultValue );
   }

   /**
    * Creates a Trie where the keys are case-sensitive character arrays.
    * 
    * @return The reference to a newly instantiated Trie.
    */
   public static <T> Trie<char[], T> forChars()
   {
      return new Trie<char[], T>( new TrieSequencerCharArray() );
   }

   /**
    * Creates a Trie where the keys are case-sensitive character arrays.
    * 
    * @param defaultValue
    *        The default value of the Trie is the value returned when
    *        {@link #get(Object)} or {@link #get(Object, TrieMatch)} is called
    *        and no match was found.
    * @return The reference to a newly instantiated Trie.
    */
   public static <T> Trie<char[], T> forChars( T defaultValue )
   {
      return new Trie<char[], T>( new TrieSequencerCharArray(), defaultValue );
   }

   /**
    * Creates a Trie where the keys are case-insensitive character arrays.
    * 
    * @return The reference to a newly instantiated Trie.
    */
   public static <T> Trie<char[], T> forInsensitiveChars()
   {
      return new Trie<char[], T>( new TrieSequencerCharArrayCaseInsensitive() );
   }

   /**
    * Creates a Trie where the keys are case-insensitive character arrays.
    * 
    * @param defaultValue
    *        The default value of the Trie is the value returned when
    *        {@link #get(Object)} or {@link #get(Object, TrieMatch)} is called
    *        and no match was found.
    * @return The reference to a newly instantiated Trie.
    */
   public static <T> Trie<char[], T> forInsensitiveChars( T defaultValue )
   {
      return new Trie<char[], T>( new TrieSequencerCharArrayCaseInsensitive(), defaultValue );
   }

   private TrieSequencer<S> sequencer;
   private TrieNode<S, T> root;
   private TrieMatch defaultMatch = TrieMatch.STARTS_WITH;
   private int size;

   private SequenceSet sequenceSet;

   /**
    * Instantiates a new Trie.
    * 
    * @param sequencer
    *        The TrieSequencer which handles the necessary sequence operations.
    */
   public Trie( TrieSequencer<S> sequencer )
   {
      this( sequencer, null );
   }

   /**
    * Instantiates a new Trie.
    * 
    * @param sequencer
    *        The TrieSequencer which handles the necessary sequence operations.
    * @param defaultValue
    *        The default value of the Trie is the value returned when
    *        {@link #get(Object)} or {@link #get(Object, TrieMatch)} is called
    *        and no match was found.
    */
   public Trie( TrieSequencer<S> sequencer, T defaultValue )
   {
      this.root = new TrieNode<S, T>( null, defaultValue, null, 0, 0, new PerfectHashMap<TrieNode<S, T>>() );
      this.sequencer = sequencer;
      this.sequenceSet = new SequenceSet();
   }

   /**
    * Puts the value in the Trie with the given sequence.
    * 
    * @param query
    *        The sequence.
    * @param value
    *        The value to place in the Trie.
    * @return
    *         The previous value in the Trie with the same sequence if one
    *         existed, otherwise null.
    */
   public T put( S query, T value )
   {
      final int queryLength = sequencer.lengthOf( query );

      if (value == null || queryLength == 0)
      {
         return null;
      }

      int queryOffset = 0;
      TrieNode<S, T> node = root.children.get( sequencer.hashOf( query, 0 ) );

      // The root doesn't have a child that starts with the given sequence...
      if (node == null)
      {
         // Add the sequence and value directly to root!
         return putReturnNull( root, value, query, queryOffset, queryLength );
      }

      while (node != null)
      {
         final S nodeSequence = node.sequence;
         final int nodeLength = node.end - node.start;
         final int max = Math.min( nodeLength, queryLength - queryOffset );
         final int matches = sequencer.matches( nodeSequence, node.start, query, queryOffset, max );

         queryOffset += matches;

         // mismatch in current node
         if (matches != max)
         {
            node.split( matches, null, sequencer );

            return putReturnNull( node, value, query, queryOffset, queryLength );
         }

         // partial match to the current node
         if (max < nodeLength)
         {
            node.split( max, value, sequencer );
            size++;

            return null;
         }

         // full match, end of the query or node
         if (node.children == null)
         {
            // end of query, replace value
            if (queryOffset == queryLength)
            {
               T previousValue = node.value;

               node.value = value;
               node.sequence = query;

               return previousValue;
            }
            // end of node, add children and node
            else
            {
               return putReturnNull( node, value, query, queryOffset, queryLength );
            }
         }

         // full match, end of node
         TrieNode<S, T> next = node.children.get( sequencer.hashOf( query, queryOffset ) );

         if (next == null)
         {
            return putReturnNull( node, value, query, queryOffset, queryLength );
         }

         // full match, query or node remaining
         node = next;
      }

      return null;
   }

   /**
    * Adds a new TrieNode to the given node with the given sequence subset.
    * 
    * @param node
    *        The node to add to; the parent of the created node.
    * @param value
    *        The value of the node.
    * @param query
    *        The sequence that was put.
    * @param queryOffset
    *        The offset into that sequence where the node (subset sequence)
    *        should begin.
    * @param queryLength
    *        The length of the subset sequence in elements.
    * @return null
    */
   private T putReturnNull( TrieNode<S, T> node, T value, S query, int queryOffset, int queryLength )
   {
      node.add( new TrieNode<S, T>( node, value, query, queryOffset, queryLength, null ), sequencer );

      size++;

      return null;
   }

   /**
    * Gets the value that matches the given sequence.
    * 
    * @param sequence
    *        The sequence to match.
    * @param match
    *        The matching logic to use.
    * @return The value for the given sequence, or the default value of the Trie
    *         if no match was found. The default value of a Trie is by default
    *         null.
    * 
    */
   public T get( S sequence, TrieMatch match )
   {
      TrieNode<S, T> n = search( sequence, match );

      return (n != null ? n.value : root.value);
   }

   /**
    * Gets the value that matches the given sequence using the default
    * TrieMatch.
    * 
    * @param sequence
    *        The sequence to match.
    * @return The value for the given sequence, or the default value of the Trie
    *         if no match was found. The default value of a Trie is by default
    *         null.
    */
   @SuppressWarnings ("unchecked" )
   public T get( Object sequence )
   {
      return get( (S)sequence, defaultMatch );
   }

   /**
    * Determines whether a value exists for the given sequence.
    * 
    * @param sequence
    *        The sequence to match.
    * @param match
    *        The matching logic to use.
    * @return True if a value exists for the given sequence, otherwise false.
    */
   public boolean has( S sequence, TrieMatch match )
   {
      return (search( sequence, match ) != null);
   }

   /**
    * Determines whether a value exists for the given sequence using the default
    * TrieMatch.
    * 
    * @param sequence
    *        The sequence to match.
    * @return True if a value exists for the given sequence, otherwise false.
    */
   public boolean has( S sequence )
   {
      return has( sequence, defaultMatch );
   }

   /**
    * Removes the sequence from the Trie and returns it's value. The sequence
    * must be an exact match, otherwise nothing will be removed.
    * 
    * @param sequence
    *        The sequence to remove.
    * @return The value of the removed sequence, or null if no sequence was
    *         removed.
    */
   @SuppressWarnings ("unchecked" )
   public T remove( Object sequence )
   {
      TrieNode<S, T> n = search( (S)sequence, TrieMatch.EXACT );

      if (n == null)
      {
         return null;
      }

      size--;

      T value = n.value;

      n.remove( sequencer );

      return value;
   }

   /**
    * Searches in the Trie based on the sequence query and the matching logic.
    * 
    * @param query
    *        The query sequence.
    * @param match
    *        The matching logic.
    * @return The node that best matched the query based on the logic.
    */
   private TrieNode<S, T> search( S query, TrieMatch match )
   {
      final int queryLength = sequencer.lengthOf( query );

      // If the query is empty or matching logic is not given, return null.
      if (queryLength == 0 || match == null)
      {
         return null;
      }

      int queryOffset = 0;
      TrieNode<S, T> node = root.children.get( sequencer.hashOf( query, 0 ) );

      while (node != null)
      {
         final S nodeSequence = node.sequence;
         final int nodeLength = node.end - node.start;
         final int max = Math.min( nodeLength, queryLength - queryOffset );
         final int matches = sequencer.matches( nodeSequence, node.start, query, queryOffset, max );

         queryOffset += matches;

         // Not found
         if (matches != max)
         {
            return null;
         }

         // Potentially PARTIAL match
         if (max != nodeLength && matches == max)
         {
            return (match != TrieMatch.PARTIAL ? null : node);
         }

         // Either EXACT or STARTS_WITH match
         if (queryOffset == queryLength || node.children == null)
         {
            break;
         }

         TrieNode<S, T> next = node.children.get( sequencer.hashOf( query, queryOffset ) );

         // If there is no next, node could be a STARTS_WITH match
         if (next == null)
         {
            break;
         }

         node = next;
      }

      // EXACT matches
      if (node != null && match == TrieMatch.EXACT)
      {
         // Check length of last node against query
         if (node.value == null || node.end != queryLength)
         {
            return null;
         }

         // Check actual sequence values
         if (sequencer.matches( node.sequence, 0, query, 0, node.end ) != node.end)
         {
            return null;
         }
      }

      return node;
   }

   /**
    * Returns the number of sequences-value pairs in this Trie.
    * 
    * @return The number of sequences-value pairs in this Trie.
    */
   public int size()
   {
      return size;
   }

   /**
    * Determines whether this Trie is empty.
    * 
    * @return 0 if the Trie doesn't have any sequences-value pairs, otherwise
    *         false.
    */
   public boolean isEmpty()
   {
      return (size == 0);
   }

   /**
    * Returns the default TrieMatch used for {@link #has(Object)} and
    * {@link #get(Object)}.
    * 
    * @return The default TrieMatch set on this Trie.
    */
   public TrieMatch getDefaultMatch()
   {
      return defaultMatch;
   }

   /**
    * Sets the default TrieMatch used for {@link #has(Object)} and
    * {@link #get(Object)}.
    * 
    * @param match
    *        The new default TrieMatch to set on this Trie.
    */
   public void setDefaultMatch( TrieMatch match )
   {
      defaultMatch = match;
   }

   @Override
   public void clear()
   {
      root.children.clear();
   }

   @SuppressWarnings ("unchecked" )
   @Override
   public boolean containsKey( Object key )
   {
      return has( (S)key );
   }

   @Override
   public boolean containsValue( Object value )
   {

      return false;
   }

   @Override
   public Set<Entry<S, T>> entrySet()
   {
      return null;
   }
   
   public Set<Entry<S, T>> entrySet(S sequence, TrieMatch match)
   {
      return null;
   }

   @Override
   public Set<S> keySet()
   {
      return sequenceSet;
   }
   
   public Set<S> keySet(S sequence, TrieMatch match)
   {
      return null;
   }

   @Override
   public void putAll( Map<? extends S, ? extends T> map )
   {
      for (Entry<? extends S, ? extends T> e : map.entrySet())
      {
         put( e.getKey(), e.getValue() );
      }
   }

   @Override
   public Collection<T> values()
   {
      return null;
   }
   
   public Collection<T> values( S sequence, TrieMatch match )
   {
      return null;
   }

   private class SequenceSet implements Set<S>
   {

      @Override
      public boolean add( S arg0 )
      {
         return false;
      }

      @Override
      public boolean addAll( Collection<? extends S> arg0 )
      {
         return false;
      }

      @Override
      public void clear()
      {
         Trie.this.clear();
      }

      @Override
      public boolean contains( Object sequence )
      {
         return containsKey( sequence );
      }

      @Override
      public boolean containsAll( Collection<?> sequences )
      {
         for (Object s : sequences)
         {
            if (!containsKey( s ))
            {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean isEmpty()
      {
         return Trie.this.isEmpty();
      }

      @Override
      public Iterator<S> iterator()
      {
         return null;
      }

      @Override
      public boolean remove( Object sequence )
      {
         return Trie.this.remove( sequence ) != null;
      }

      @Override
      public boolean removeAll( Collection<?> sequences )
      {
         for (Object s : sequences)
         {
            if (!remove( s ))
            {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean retainAll( Collection<?> arg0 )
      {
         return false;
      }

      @Override
      public int size()
      {
         return Trie.this.size();
      }

      @Override
      public Object[] toArray()
      {
         Object[] sequences = new Object[size()];
         
         int i = 0;
         for (S sequence : this)
         {
            sequences[i++] = sequence;
         }
         
         return sequences;
      }

      @SuppressWarnings ({ "unchecked", "hiding" } )
      @Override
      public <T> T[] toArray( T[] array )
      {
         final int size = size();
         
         if (array == null || array.length != size)
         {
            array = Arrays.copyOf( array, size );
         }
         
         int i = 0;
         for (S sequence : this)
         {
            array[i++] = (T)sequence;
         }
         
         return array;
      }

   }
   
   private class AbstractIterator
   {

      private TrieNode<S, T> root;
      private TrieNode<S, T> previous;
      private TrieNode<S, T> current;
      private int depth;
      private int[] indices = new int[32];
      
      public AbstractIterator reset()
      {
         depth = 0;
         indices[0] = 0;
         previous = root;
         current = findNext();
         
         return this;
      }
      
      public boolean hasNext()
      {
         return (current != null);
      }

      public TrieNode<S, T> nextNode()
      {
         previous = current;
         current = findNext();
         return previous;
      }

      public void remove()
      {
         previous.remove( sequencer );
      }

      private TrieNode<S, T> findNext()
      {
         if (depth == 0 && indices[0] > root.children.capacity())
         {
            return null;
         }
         
         TrieNode<S, T> node = previous;
         
         for (;;) 
         {
            final PerfectHashMap<TrieNode<S, T>> children = node.children;
            int id = indices[depth] + 1;
            
            while (id < children.capacity() && children.valueAt( id ) == null)
            {
               id++;
            }
            
            if (id == children.capacity())
            {
               node = node.parent;
               depth--;
            }
            else
            {
               indices[depth] = id;
               previous = children.valueAt( id );
               
               if (previous.hasChildren())
               {
                  indices[++depth] = -1;
               }
               
               if (previous.value != null)
               {
                  
               }
            }   
         }
      }
      
   }

   private class SequenceIterator implements Iterable<S>, Iterator<S>
   {
      private int index;
      private S last;
      
      @Override
      public boolean hasNext()
      {
         return (index < size);
      }

      @Override
      public S next()
      {
         return last;
      }

      @Override
      public void remove()
      {
         Trie.this.remove( last );
      }

      @Override
      public Iterator<S> iterator()
      {
         return this;
      }
      
   }
   
}
