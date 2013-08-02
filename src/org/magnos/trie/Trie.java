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

import java.util.AbstractCollection;
import java.util.AbstractSet;
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

   private final TrieNode<S, T> root;
   private TrieSequencer<S> sequencer;
   private TrieMatch defaultMatch = TrieMatch.STARTS_WITH;

   private SequenceSet sequences;
   private ValueCollection values;
   private EntrySet entries;
   private NodeSet nodes;

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
      this.sequences = new SequenceSet( root );
      this.values = new ValueCollection( root );
      this.entries = new EntrySet( root );
      this.nodes = new NodeSet( root );
      this.sequencer = sequencer;
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

            return null;
         }

         // full match, end of the query or node
         if (node.children == null)
         {
            // end of query, replace value
            if (queryOffset == queryLength)
            {
               node.sequence = query;
               
               return node.setValue( value );
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
      node.add( new TrieNode<S, T>( node, value, query, queryOffset, queryLength, null ), sequencer, true );

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
      return root.getSize();
   }

   /**
    * Determines whether this Trie is empty.
    * 
    * @return 0 if the Trie doesn't have any sequences-value pairs, otherwise
    *         false.
    */
   public boolean isEmpty()
   {
      return (root.getSize() == 0);
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
      Iterable<T> values = new ValueIterator( root );

      for (T v : values)
      {
         if (v == value || (v != null && value != null && v.equals( values )))
         {
            return true;
         }
      }

      return false;
   }

   @Override
   public Set<Entry<S, T>> entrySet()
   {
      return entries;
   }

   public Set<Entry<S, T>> entrySet( S sequence, TrieMatch match )
   {
      TrieNode<S, T> root = search( sequence, match );

      return (root == null ? null : new EntrySet( root ));
   }

   public Set<TrieNode<S, T>> nodeSet()
   {
      return nodes;
   }

   public Set<TrieNode<S, T>> nodeSet( S sequence, TrieMatch match )
   {
      TrieNode<S, T> root = search( sequence, match );

      return (root == null ? null : new NodeSet( root ));
   }

   @Override
   public Set<S> keySet()
   {
      return sequences;
   }

   public Set<S> keySet( S sequence, TrieMatch match )
   {
      TrieNode<S, T> root = search( sequence, match );

      return (root == null ? null : new SequenceSet( root ));
   }

   @Override
   public Collection<T> values()
   {
      return values;
   }

   public Collection<T> values( S sequence, TrieMatch match )
   {
      TrieNode<S, T> root = search( sequence, match );

      return (root == null ? null : new ValueCollection( root ));
   }

   @Override
   public void putAll( Map<? extends S, ? extends T> map )
   {
      for (Entry<? extends S, ? extends T> e : map.entrySet())
      {
         put( e.getKey(), e.getValue() );
      }
   }

   private class ValueCollection extends AbstractCollection<T>
   {

      private final TrieNode<S, T> root;

      public ValueCollection( TrieNode<S, T> root )
      {
         this.root = root;
      }

      @Override
      public Iterator<T> iterator()
      {
         return new ValueIterator( root );
      }

      @Override
      public int size()
      {
         return root.getSize();
      }
   }

   private class SequenceSet extends AbstractSet<S>
   {

      private final TrieNode<S, T> root;

      public SequenceSet( TrieNode<S, T> root )
      {
         this.root = root;
      }

      @Override
      public Iterator<S> iterator()
      {
         return new SequenceIterator( root );
      }

      @Override
      public boolean remove( Object sequence )
      {
         return Trie.this.remove( sequence ) != null;
      }

      @Override
      public int size()
      {
         return root.getSize();
      }
   }

   private class EntrySet extends AbstractSet<Entry<S, T>>
   {

      private final TrieNode<S, T> root;

      public EntrySet( TrieNode<S, T> root )
      {
         this.root = root;
      }

      @Override
      public Iterator<Entry<S, T>> iterator()
      {
         return new EntryIterator( root );
      }

      @SuppressWarnings ("unchecked" )
      @Override
      public boolean remove( Object entry )
      {
         ((TrieNode<S, T>)entry).remove( sequencer );

         return true;
      }

      @Override
      public int size()
      {
         return root.getSize();
      }
   }

   private class NodeSet extends AbstractSet<TrieNode<S, T>>
   {

      private final TrieNode<S, T> root;

      public NodeSet( TrieNode<S, T> root )
      {
         this.root = root;
      }

      @Override
      public Iterator<TrieNode<S, T>> iterator()
      {
         return new NodeIterator( root );
      }

      @SuppressWarnings ("unchecked" )
      @Override
      public boolean remove( Object entry )
      {
         ((TrieNode<S, T>)entry).remove( sequencer );

         return true;
      }

      @Override
      public int size()
      {
         return root.getSize();
      }
   }

   private class SequenceIterator extends AbstractIterator<S>
   {

      public SequenceIterator( TrieNode<S, T> root )
      {
         super( root );
      }

      @Override
      public S next()
      {
         return nextNode().sequence;
      }
   }

   private class ValueIterator extends AbstractIterator<T>
   {

      public ValueIterator( TrieNode<S, T> root )
      {
         super( root );
      }

      @Override
      public T next()
      {
         return nextNode().value;
      }
   }

   private class EntryIterator extends AbstractIterator<Entry<S, T>>
   {

      public EntryIterator( TrieNode<S, T> root )
      {
         super( root );
      }

      @Override
      public Entry<S, T> next()
      {
         return nextNode();
      }
   }

   private class NodeIterator extends AbstractIterator<TrieNode<S, T>>
   {

      public NodeIterator( TrieNode<S, T> root )
      {
         super( root );
      }

      @Override
      public TrieNode<S, T> next()
      {
         return nextNode();
      }
   }

   private abstract class AbstractIterator<K> implements Iterable<K>, Iterator<K>
   {

      private final TrieNode<S, T> root;
      private TrieNode<S, T> previous;
      private TrieNode<S, T> current;
      private int depth;
      private int[] indices = new int[32];

      public AbstractIterator( TrieNode<S, T> root )
      {
         this.root = root;
         this.reset();
      }

      public AbstractIterator<K> reset()
      {
         depth = 0;
         indices[0] = -1;
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
         if (indices[0] == root.children.capacity())
         {
            return null;
         }

         TrieNode<S, T> node = previous;
         boolean foundValue = false;

         if (node.children == null)
         {
            node = node.parent;
         }
         
         while (!foundValue)
         {
            final PerfectHashMap<TrieNode<S, T>> children = node.children;
            final int childCapacity = children.capacity();
            int id = indices[depth] + 1;

            while (id < childCapacity && children.valueAt( id ) == null)
            {
               id++;
            }

            if (id == childCapacity)
            {
               node = node.parent;
               depth--;

               if (depth == -1)
               {
                  node = null;
                  foundValue = true;
               }
            }
            else
            {
               indices[depth] = id;
               node = children.valueAt( id );

               if (node.hasChildren())
               {
                  indices[++depth] = -1;
               }

               if (node.value != null)
               {
                  foundValue = true;
               }
            }
         }

         return node;
      }

      @Override
      public Iterator<K> iterator()
      {
         return this;
      }
   }

}
