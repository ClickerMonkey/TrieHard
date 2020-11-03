package org.magnos.trie;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;


public class TestTrieCollection 
{

	@Test
	public void testAdd()
	{
		TrieCollection<String, Integer, ArrayList<Integer>> tc = TrieBuilder
			.create(Integer.class)
			.forStrings()
			.forArrayLists()
			.withDefaultEmptyCollection()
			.buildForCollection();
		
		tc.add("h", 23);
		tc.add("h", 11);

		assertEquals(Arrays.toString(tc.get("h").toArray(new Integer[0])), "[23, 11]");
		assertEquals(Arrays.toString(tc.get("i").toArray(new Integer[0])), "[]");
	}
	
}
