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

import java.util.Collection;
import java.util.Map;


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
public class Trie<S, T>
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
   public static <T> Trie<String, T> forInensitiveStrings()
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
   private TrieNode root;
   private TrieMatch defaultMatch = TrieMatch.STARTS_WITH;
   private int size;

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
      this.root = new TrieNode( null, defaultValue, null, 0, new PerfectHashMap<TrieNode>() );
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
      TrieNode node = root.children.get( sequencer.hashOf( query, 0 ) );

      // The root doesn't have a child that starts with the given sequence...
      if (node == null)
      {
         // Add the sequence and value directly to root!
         return putReturnNull( root, value, query, queryOffset, queryLength );
      }

      while (node != null)
      {
         final S nodeSequence = node.sequence;
         final int nodeLength = sequencer.lengthOf( nodeSequence );
         final int max = Math.min( nodeLength, queryLength - queryOffset );
         final int matches = sequencer.matches( nodeSequence, 0, query, queryOffset, max );

         queryOffset += matches;

         // mismatch in current node
         if (matches != max)
         {
            node.split( matches, null );

            return putReturnNull( node, value, query, queryOffset, queryLength );
         }

         // partial match to the current node
         if (max < nodeLength)
         {
            node.split( max, value );
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

               return previousValue;
            }
            // end of node, add children and node
            else
            {
               return putReturnNull( node, value, query, queryOffset, queryLength );
            }
         }

         // full match, end of node
         TrieNode next = node.children.get( sequencer.hashOf( query, queryOffset ) );

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
   private T putReturnNull( TrieNode node, T value, S query, int queryOffset, int queryLength )
   {
      node.add( new TrieNode( node, value, sequencer.subSequence( query, queryOffset, queryLength ), queryOffset, null ) );

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
      TrieNode n = search( sequence, match );

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
   public T get( S sequence )
   {
      return get( sequence, defaultMatch );
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
   public T remove( S sequence )
   {
      TrieNode n = search( sequence, TrieMatch.EXACT );

      if (n == null)
      {
         return null;
      }

      size--;

      T value = n.value;

      n.remove();

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
   private TrieNode search( S query, TrieMatch match )
   {
      final int queryLength = sequencer.lengthOf( query );

      // If the query is empty or matching logic is not given, return null.
      if (queryLength == 0 || match == null)
      {
         return null;
      }

      int queryOffset = 0;
      TrieNode node = root.children.get( sequencer.hashOf( query, 0 ) );

      while (node != null)
      {
         final S nodeSequence = node.sequence;
         final int nodeLength = sequencer.lengthOf( nodeSequence );
         final int max = Math.min( nodeLength, queryLength - queryOffset );
         final int matches = sequencer.matches( nodeSequence, 0, query, queryOffset, max );

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

         TrieNode next = node.children.get( sequencer.hashOf( query, queryOffset ) );

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
         final int nodeLength = sequencer.lengthOf( node.sequence );

         // Check length of last node against query
         if (node.value == null || node.index + nodeLength != queryLength)
         {
            return null;
         }

         // Check actual sequence values
         if (sequencer.matches( node.sequence, 0, query, node.index, nodeLength ) != nodeLength)
         {
            return null;
         }
      }

      return node;
   }

   /**
    * Takes all values that exist in this Trie and add them to the given
    * destination collection.
    * 
    * @param destination
    *        The collection to add all values to.
    * @return The reference to the given collection.
    */
   public <C extends Collection<T>> C takeValues( C destination )
   {
      root.takeValues( destination );

      return destination;
   }

   /**
    * Takes all values that match the given sequence query and add them to the
    * given destination collection.
    * 
    * @param query
    *        The sequence to query the Trie.
    * @param match
    *        The matching logic.
    * @param destination
    *        The collection to add all matched values to.
    * @return The reference to the given collection.
    */
   public <C extends Collection<T>> C takeValues( S query, TrieMatch match, C destination )
   {
      TrieNode n = search( query, match );

      if (n != null)
      {
         n.takeValues( destination );
      }

      return destination;
   }

   /**
    * Takes all sequences that exist in this Trie and add them to the given
    * destination collection.
    * 
    * @param destination
    *        The collection to add all sequences to.
    * @return The reference to the given collection.
    */
   public <C extends Collection<S>> C takeSequences( C destination )
   {
      root.takeSequences( null, destination );

      return destination;
   }

   /**
    * Takes all sequences that match the given sequence query and add them to
    * the given destination collection.
    * 
    * @param query
    *        The sequence to query the Trie.
    * @param match
    *        The matching logic.
    * @param destination
    *        The collection to add all matched sequences to.
    * @return The reference to the given collection.
    */
   public <C extends Collection<S>> C takeSequences( S query, TrieMatch match, C destination )
   {
      TrieNode n = search( query, match );

      if (n != null)
      {
         S parentSequence = null;

         if (n.parent != null)
         {
            TrieNode p = n.parent;
            parentSequence = p.sequence;

            while (p.parent != null && p.parent.sequence != null)
            {
               parentSequence = sequencer.combine( p.parent.sequence, parentSequence );
               p = p.parent;
            }
         }

         n.takeSequences( parentSequence, destination );
      }

      return destination;
   }

   /**
    * Takes all entries that exist in this Trie and add them to the given
    * destination Map.
    * 
    * @param destination
    *        The Map to add all entries to.
    * @return The reference to the given Map.
    */
   public <M extends Map<S, T>> M takeEntries( M destination )
   {
      root.takeEntries( null, destination );

      return destination;
   }

   /**
    * Takes all entries that match the given sequence query and add them to
    * the given destination Map.
    * 
    * @param query
    *        The sequence to query the Trie.
    * @param match
    *        The matching logic.
    * @param destination
    *        The Map to put all matched entries to.
    * @return The reference to the given Map.
    */
   public <M extends Map<S, T>> M takeEntries( S query, TrieMatch match, M destination )
   {
      TrieNode n = search( query, match );

      if (n != null)
      {
         S parentSequence = null;

         if (n.parent != null)
         {
            TrieNode p = n.parent;
            parentSequence = p.sequence;

            while (p.parent != null && p.parent.sequence != null)
            {
               parentSequence = sequencer.combine( p.parent.sequence, parentSequence );
               p = p.parent;
            }
         }

         n.takeEntries( parentSequence, destination );
      }

      return destination;
   }

   /**
    * Iterates over all entries in this Trie.
    * 
    * @param iterator
    *        The iterator to invoke for each entry.
    */
   public void iterator( TrieIterator<S, T> iterator )
   {
      root.iterator( 0, iterator );
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

   /**
    * The internal entry class that stores sequences and values.
    * 
    * @author Philip Diffenderfer
    *
    */
   private class TrieNode
   {

      private TrieNode parent;
      private T value;
      private S sequence;
      private int index;
      private PerfectHashMap<TrieNode> children = null;

      private TrieNode( TrieNode parent, T value, S sequence, int index, PerfectHashMap<TrieNode> children )
      {
         this.parent = parent;
         this.value = value;
         this.sequence = sequence;
         this.index = index;
         this.children = children;
      }

      private TrieNode split( int atIndex, T newValue )
      {
         S remainingSequence = sequencer.subSequence( sequence, atIndex, sequencer.lengthOf( sequence ) );

         TrieNode c = new TrieNode( this, value, remainingSequence, atIndex + index, children );

         value = newValue;
         sequence = sequencer.subSequence( sequence, 0, atIndex );
         children = null;

         add( c );

         return c;
      }

      private void add( TrieNode child )
      {
         int hash = sequencer.hashOf( child.sequence, 0 );

         if (children == null)
         {
            children = new PerfectHashMap<TrieNode>( hash, child );
         }
         else
         {
            children.put( hash, child );
         }
      }

      private void remove()
      {
         value = null;

         int childCount = (children == null ? 0 : children.size());

         if (childCount == 0)
         {
            parent.children.remove( sequencer.hashOf( sequence, 0 ) );

            if (parent.value == null)
            {
               parent.remove();
            }
         }
         else if (childCount == 1)
         {
            TrieNode child = children.valueAt( 0 );

            children = child.children;
            value = child.value;
            sequence = sequencer.combine( sequence, child.sequence );

            child.children = null;
            child.parent = null;
            child.sequence = null;
            child.value = null;
         }
      }

      private void takeValues( Collection<T> values )
      {
         if (value != null)
         {
            values.add( value );
         }

         if (children == null)
         {
            return;
         }

         for (int i = 0; i < children.capacity(); i++)
         {
            TrieNode c = children.valueAt( i );

            if (c != null)
            {
               c.takeValues( values );
            }
         }
      }

      private void takeSequences( S parentSequence, Collection<S> sequences )
      {
         if (parentSequence == null)
         {
            parentSequence = sequence;
         }
         else
         {
            parentSequence = sequencer.combine( parentSequence, sequence );
         }

         if (value != null && parentSequence != null)
         {
            sequences.add( parentSequence );
         }

         if (children == null)
         {
            return;
         }

         for (int i = 0; i < children.capacity(); i++)
         {
            TrieNode c = children.valueAt( i );

            if (c != null)
            {
               c.takeSequences( parentSequence, sequences );
            }
         }
      }

      private void takeEntries( S parentSequence, Map<S, T> map )
      {
         if (parentSequence == null)
         {
            parentSequence = sequence;
         }
         else
         {
            parentSequence = sequencer.combine( parentSequence, sequence );
         }

         if (value != null && parentSequence != null)
         {
            map.put( parentSequence, value );
         }

         if (children == null)
         {
            return;
         }

         for (int i = 0; i < children.capacity(); i++)
         {
            TrieNode c = children.valueAt( i );

            if (c != null)
            {
               c.takeEntries( parentSequence, map );
            }
         }
      }

      private void iterator( int depth, TrieIterator<S, T> iterator )
      {
         if (depth != 0)
         {
            iterator.onEntry( sequence, index, value, depth );
         }

         if (children != null)
         {
            final int childCount = children.capacity();

            depth++;

            for (int i = 0; i < childCount; i++)
            {
               final TrieNode child = children.valueAt( i );

               if (child != null)
               {
                  child.iterator( depth, iterator );
               }
            }
         }
      }
   }

}
