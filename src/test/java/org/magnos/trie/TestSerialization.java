package org.magnos.trie;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.jupiter.api.Test;

public class TestSerialization 
{

   @Test
   public void testSimplePut() throws Exception
   {
      Trie<String, Boolean> t = Tries.forStrings();

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

      Trie<String, Boolean> r = transfer(t);
      
      assertEquals( 5, r.size() );
      assertFalse( r.isEmpty() );

      assertTrue( r.get( "java.lang.Integer" ) );
      assertTrue( r.get( "java.lang.Long" ) );
      assertFalse( r.get( "java.lang.Boolean" ) );
      assertTrue( r.get( "java.io.InputStream" ) );
      assertFalse( r.get( "java.util.ArrayList" ) );
      assertTrue( r.get( "java.util.concurrent.ConcurrentHashMap" ) ); 
   }
   
   @SuppressWarnings("unchecked")
   protected <T> T transfer(T object) throws Exception
   {
	   ByteArrayOutputStream bos = new ByteArrayOutputStream();
	   ObjectOutputStream oos = new ObjectOutputStream(bos);
	   
	   oos.writeObject(object);
	   
	   byte[] bytes = bos.toByteArray();
	   
	   System.out.format("Transfer size: %d\n", bytes.length);
	   
	   ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
	   ObjectInputStream ois = new ObjectInputStream(bais);
	   
	   return (T)ois.readObject();
   }
	
}
