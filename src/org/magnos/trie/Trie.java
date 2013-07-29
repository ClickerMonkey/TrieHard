
package org.magnos.trie;

import java.util.Collection;
import java.util.Map;


public class Trie<S, T>
{

   public static <T> Trie<String, T> forStrings()
   {
      return new Trie<String, T>( new TrieSequencerCharSequence<String>() );
   }

   public static <T> Trie<String, T> forStrings( T defaultValue )
   {
      return new Trie<String, T>( new TrieSequencerCharSequence<String>(), defaultValue );
   }

   public static <T> Trie<char[], T> forChars()
   {
      return new Trie<char[], T>( new TrieSequencerCharArray() );
   }

   public static <T> Trie<char[], T> forChars( T defaultValue )
   {
      return new Trie<char[], T>( new TrieSequencerCharArray(), defaultValue );
   }

   private TrieSequencer<S> sequencer;
   private TrieNode root;

   public Trie( TrieSequencer<S> sequencer )
   {
      this( sequencer, null );
   }

   public Trie( TrieSequencer<S> sequencer, T defaultValue )
   {
      this.root = new TrieNode( null, defaultValue, null, 0, new PerfectHashMap<TrieNode>() );
      this.sequencer = sequencer;
   }

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

   private T putReturnNull( TrieNode node, T value, S query, int queryOffset, int queryLength )
   {
      node.add( new TrieNode( node, value, sequencer.subSequence( query, queryOffset, queryLength ), queryOffset, null ) );

      return null;
   }

   public T get( S sequence, TrieMatch match )
   {
      TrieNode n = search( sequence, match );

      return (n != null ? n.value : root.value);
   }

   public boolean has( S sequence, TrieMatch match )
   {
      return (search( sequence, match ) != null);
   }

   public boolean remove( S sequence )
   {
      TrieNode n = search( sequence, TrieMatch.EXACT );

      if (n == null)
      {
         return false;
      }

      n.remove();

      return true;
   }

   private TrieNode search( S query, TrieMatch match )
   {
      final int queryLength = sequencer.lengthOf( query );
      int queryOffset = 0;
      TrieNode node = root.children.get( sequencer.hashOf( query, 0 ) );

      while (node != null)
      {
         final S nodeSequence = node.sequence;
         final int nodeLength = sequencer.lengthOf( nodeSequence );
         final int max = Math.min( nodeLength, queryLength - queryOffset );
         final int matches = sequencer.matches( nodeSequence, 0, query, queryOffset, max );

         queryOffset += matches;

         // Potentially PARTIAL match or not found
         if (matches != max)
         {
            return (match != TrieMatch.PARTIAL || queryOffset != queryLength ? null : node);
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

   public <C extends Collection<T>> C takeValues( C destination )
   {
      root.takeValues( destination );

      return destination;
   }

   public <C extends Collection<S>> C takeSequences( C destination )
   {
      root.takeSequences( null, destination );

      return destination;
   }

   public <M extends Map<S, T>> M takeEntries( M destination )
   {
      root.takeEntries( null, destination );

      return destination;
   }

   public void iterator( TrieIterator<S, T> iterator )
   {
      root.iterator( 0, iterator );
   }

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

         if (children.size() == 0)
         {
            parent.children.remove( sequencer.hashOf( sequence, 0 ) );

            if (parent.value == null)
            {
               parent.remove();
            }
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
