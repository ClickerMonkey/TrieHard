
package org.magnos.trie;

/**
 * The internal entry class that stores sequences and values.
 * 
 * @author Philip Diffenderfer
 *
 */
public class TrieNode<S, T>
{

   protected TrieNode<S, T> parent;
   protected T value;
   protected S sequence;
   protected int start;
   protected int end;
   protected PerfectHashMap<TrieNode<S, T>> children = null;

   protected TrieNode( TrieNode<S, T> parent, T value, S sequence, int start, int end, PerfectHashMap<TrieNode<S, T>> children )
   {
      this.parent = parent;
      this.value = value;
      this.sequence = sequence;
      this.start = start;
      this.end = end;
      this.children = children;
   }

   protected TrieNode<S, T> split( int atIndex, T newValue, TrieSequencer<S> sequencer )
   {
      TrieNode<S, T> c = new TrieNode<S, T>( this, value, sequence, atIndex + start, end, children );
      c.registerAsParent();

      value = newValue;
      end = atIndex + start;
      children = null;

      add( c, sequencer );

      return c;
   }

   protected void add( TrieNode<S, T> child, TrieSequencer<S> sequencer )
   {
      int hash = sequencer.hashOf( child.sequence, end );

      if (children == null)
      {
         children = new PerfectHashMap<TrieNode<S, T>>( hash, child );
      }
      else
      {
         children.put( hash, child );
      }
   }

   protected void remove( TrieSequencer<S> sequencer )
   {
      value = null;

      int childCount = (children == null ? 0 : children.size());

      if (childCount == 0)
      {
         parent.children.remove( sequencer.hashOf( sequence, start ) );

         if (parent.value == null)
         {
            parent.remove( sequencer );
         }
      }
      else if (childCount == 1)
      {
         TrieNode<S, T> child = children.valueAt( 0 );

         children = child.children;
         value = child.value;
         sequence = child.sequence;

         child.children = null;
         child.parent = null;
         child.sequence = null;
         child.value = null;

         registerAsParent();
      }
   }

   private void registerAsParent()
   {
      if (children != null)
      {
         for (int i = 0; i < children.capacity(); i++)
         {
            TrieNode<S, T> c = children.valueAt( i );

            if (c != null)
            {
               c.parent = this;
            }
         }
      }
   }
   
   public boolean hasChildren()
   {
      return children != null && children.size() > 0;
   }

   public TrieNode<S, T> getParent()
   {
      return parent;
   }

   public T getValue()
   {
      return value;
   }

   public S getSequence()
   {
      return sequence;
   }

   public int getStart()
   {
      return start;
   }

   public int getEnd()
   {
      return end;
   }

}
