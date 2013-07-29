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

/**
 * A simple map implementation where the keys are integers and are used
 * as direct indices into the map. The minimum and maximum key values
 * define the size of the underlying table and thus should be as
 * near to each other as possible. 
 * 
 * @author Philip Diffenderfer
 *
 * @param <T>
 *      The value type.
 */
@SuppressWarnings ("unchecked" )
public class PerfectHashMap<T>
{

   private int min;
   private int size;
   private T[] values;

   public PerfectHashMap()
   {
      clear();
   }
   
   public PerfectHashMap(int firstKey, T firstValue)
   {
      putFirst( firstKey, firstValue );
   }

   public boolean exists(int key)
   {
      final int i = relativeIndex( key );
      
      return (i >= 0 && i < values.length && values[i] != null);
   }
   
   public T get(int key)
   {
      final int i = relativeIndex( key );
      
      return (i < 0 || i >= values.length ? null : values[i]);
   }

   public void put(int key, T value)
   {
      if (size == 0)
      {
         putFirst( key, value );
         return;
      }
      
      final int i = relativeIndex( key );
      
      if (i < 0) 
      {
         prepend( -i );
         values[0] = value;
         min = key;
         size++;
      } 
      else if (i >= values.length) 
      {
         resize( i + 1 );
         values[i] = value;
         size++;
      }
      else
      {
         if (values[i] == null)
         {
            size++;
         }
         
         values[i] = value;
      }
   }

   private void prepend(int spaces)
   {
      final int length = values.length;
      
      values = Arrays.copyOf( values, length + spaces );
      
      System.arraycopy( values, 0, values, spaces, length );
   }

   private void resize(int size)
   {
      values = Arrays.copyOf( values, size );
   }
   
   private void putFirst(int firstKey, T firstValue)
   {
      min = firstKey;
      values = (T[])new Object[1];
      values[0] = firstValue;
      size = 1;
   }
   
   public void clear()
   {
      min = 0;
      values = (T[])new Object[0];
      size = 0;
   }
   
   public boolean remove(int key)
   {
      int i = relativeIndex( key );
      
      if (size == 1)
      {
         boolean match = (i == 0);
         
         if (match)
         {
            clear();
         }
         
         return match;
      }

      final int valuesMax = values.length - 1;

      if (i < 0 || i > valuesMax)
      {
         return false;
      }

      if (i == 0)
      {
         do 
         {
            i++;   
         }
         while (i <= valuesMax && values[i] == null);
         
         values = Arrays.copyOfRange( values, i, values.length );
         min += i;
      }
      else if (i == valuesMax)
      {
         do
         {
            i--;
         }
         while (i > 0 && values[i] == null);
         
         values = Arrays.copyOf( values, i + 1 );
      }
      else
      {
         if (values[i] == null)
         {
            return false;
         }
         
         values[i] = null;
      }
      
      size--;
      
      return true;
   }
   
   private final int relativeIndex(int key)
   {
      return (key - min);
   }
   
   public int getMin()
   {
      return min;
   }
   
   public int getMax()
   {
      return min + values.length - 1;
   }
   
   public boolean isEmpty()
   {
      return (size == 0);
   }
   
   public int size()
   {
      return size;
   }
   
   public int capacity()
   {
      return values.length;
   }
   
   public T valueAt(int index)
   {
      return values[index];
   }
   
}
