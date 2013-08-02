
package org.magnos.trie;

import java.util.Map.Entry;


/**
 * The internal entry class that stores sequences and values.
 * 
 * @author Philip Diffenderfer
 * 
 */
public class TrieNode<S, T> implements Entry<S, T>
{

   protected TrieNode<S, T> parent;
   protected T value;
   protected S sequence;
   protected int start;
   protected int end;
   protected PerfectHashMap<TrieNode<S, T>> children = null;
   protected int size;

   protected TrieNode( TrieNode<S, T> parent, T value, S sequence, int start, int end, PerfectHashMap<TrieNode<S, T>> children )
   {
      this.parent = parent;
      this.sequence = sequence;
      this.start = start;
      this.end = end;
      this.children = children;
      this.size = calculateSize( children );
      this.setValue( value );
   }

   protected TrieNode<S, T> split( int atIndex, T newValue, TrieSequencer<S> sequencer )
   {
      TrieNode<S, T> c = new TrieNode<S, T>( this, value, sequence, atIndex + start, end, children );
      c.registerAsParent();

      setValue( newValue );
      end = atIndex + start;
      children = null;

      add( c, sequencer, false );

      return c;
   }

   protected void add( TrieNode<S, T> child, TrieSequencer<S> sequencer, boolean updateSize )
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
      setValue( null );

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

   private void addSize( int amount )
   {
      TrieNode<S, T> curr = this;

      while (curr != null)
      {
         curr.size += amount;
         curr = curr.parent;
      }
   }

   private int calculateSize( PerfectHashMap<TrieNode<S, T>> nodes )
   {
      int size = 0;

      if (nodes != null)
      {
         for (int i = nodes.capacity() - 1; i >= 0; i--)
         {
            TrieNode<S, T> n = nodes.valueAt( i );

            if (n != null)
            {
               size += n.size;
            }
         }
      }

      return size;
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

   @Override
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

   public int getSize()
   {
      return size;
   }

   public int getChildCount()
   {
      return (children == null ? 0 : children.size());
   }

   @Override
   public S getKey()
   {
      return sequence;
   }

   @Override
   public T setValue( T newValue )
   {
      T previousValue = value;

      value = newValue;

      if (previousValue == null && value != null)
      {
         addSize( 1 );
      }
      else if (previousValue != null && value == null)
      {
         addSize( -1 );
      }
      
      return previousValue;
   }
   
   @Override
   public int hashCode() 
   {
       return (sequence == null ? 0 : sequence.hashCode())
            ^ (value == null ? 0 : value.hashCode());
   }
   
   @Override
   public String toString()
   {
      return sequence + "=" + value;
   }
   
   @Override
   public boolean equals(Object o)
   {
      if (o == null || !(o instanceof TrieNode))
      {
         return false;
      }
      
      TrieNode<?, ?> node = (TrieNode<?, ?>)o;
      
      return (sequence == node.sequence || sequence.equals( node.sequence )) &&
             (value == node.value || (value != null && node.value != null && value.equals(node.value)));
   }

}
