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

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;
import java.util.function.Supplier;

/**
 * A class that helps build Trie implementations.
 * 
 * @author Philip Diffenderfer
 * 
 * @param <S>
 *        The sequence/key type.
 * @param <T>
 *        The value type.
 * @param <C>
 * 		  The collection type.
 */
public class TrieBuilder<S, T, C extends Collection<T>> 
{
	
	protected TrieSequencer<S> sequencer;
	protected TrieMatch match;
	protected Supplier<C> supplier;
	protected boolean defaultEmptyCollection;
	protected T defaultValue;

	/**
	 * Creates a new builder for an implied value type.
	 * 
	 * @return
	 * 		The reference to a new builder instance.
	 */
	public static <T> TrieBuilder<String, T, ArrayList<T>> create()
	{
		return new TrieBuilder<String, T, ArrayList<T>>( TrieSequencerCharSequence.INSTANCE, TrieMatch.STARTS_WITH, () -> new ArrayList<T>(), (T)null, false );
	}
	
	/**
	 * Creates a new builder for a given value type.
	 * 
	 * @param clazz
	 * 		The class of the value type.
	 * @return
	 * 		The reference to a new builder instance.
	 */
	public static <T> TrieBuilder<String, T, ArrayList<T>> create(Class<T> clazz)
	{
		return new TrieBuilder<String, T, ArrayList<T>>( TrieSequencerCharSequence.INSTANCE, TrieMatch.STARTS_WITH, () -> new ArrayList<T>(), (T)null, false );
	}
	
	/**
	 * Instantiates a new builder instance with the given properties.
	 * 
	 * @param sequencer
	 * 		The implementation which interprets sequences.
	 * @param match
	 * 		The default match for the Trie.
	 * @param supplier
	 * 		The supplier of collections if the built Trie is a TrieCollection.
	 * @param defaultValue
	 * 		The default value for a Trie when a search yields no results.
	 * @param defaultEmptyCollection
	 * 		If a built TrieCollection should return empty collections by default
	 * 		when a search yields no results.
	 */
	public TrieBuilder( TrieSequencer<S> sequencer, TrieMatch match, Supplier<C> supplier, T defaultValue, boolean defaultEmptyCollection )
	{
		this.sequencer = sequencer;
		this.match = match;
		this.supplier = supplier;
		this.defaultValue = defaultValue;
		this.defaultEmptyCollection = defaultEmptyCollection;
	}
	
	/**
	 * Creates a new builder with the current properties except for an 
	 * implicit sequence of characters.
	 * 
	 * @return
	 * 		The reference to the new builder.
	 */
	public <CS extends CharSequence> TrieBuilder<CS, T, C> forCharSequence()
	{
		return new TrieBuilder<CS, T, C>( new TrieSequencerCharSequence<CS>(), match, supplier, defaultValue, defaultEmptyCollection );
	}
	
	/**
	 * Creates a new builder with the current properties except for a given 
	 * sequence of characters.
	 * 
	 * @param clazz
	 * 		The class of the character sequence.
	 * @return
	 * 		The reference to the new builder.
	 */
	public <CS extends CharSequence> TrieBuilder<CS, T, C> forCharSequence(Class<CS> clazz)
	{
		return new TrieBuilder<CS, T, C>( new TrieSequencerCharSequence<CS>(), match, supplier, defaultValue, defaultEmptyCollection );
	}
	
	/**
	 * Creates a new builder with the current properties except for an 
	 * implicit sequence of case-insensitive characters.
	 * 
	 * @return
	 * 		The reference to the new builder.
	 */
	public <CS extends CharSequence> TrieBuilder<CS, T, C> forInsensitiveCharSequence()
	{
		return new TrieBuilder<CS, T, C>( new TrieSequencerCharSequenceCaseInsensitive<CS>(), match, supplier, defaultValue, defaultEmptyCollection );
	}
	
	/**
	 * Creates a new builder with the current properties except for a given 
	 * sequence of case-insensitive characters.
	 * 
	 * @param clazz
	 * 		The class of the character sequence.
	 * @return
	 * 		The reference to the new builder.
	 */
	public <CS extends CharSequence> TrieBuilder<CS, T, C> forInsensitiveCharSequence(Class<CS> clazz)
	{
		return new TrieBuilder<CS, T, C>( new TrieSequencerCharSequenceCaseInsensitive<CS>(), match, supplier, defaultValue, defaultEmptyCollection );
	}
	
	/**
	 * Creates a new builder with the current properties except for sequences 
	 * of ByteBuffers.
	 * 
	 * @return
	 * 		The reference to the new builder.
	 */
	public TrieBuilder<ByteBuffer, T, C> forByteBuffers()
	{
		return new TrieBuilder<ByteBuffer, T, C>( TrieSequencerByteBuffer.INSTANCE, match, supplier, defaultValue, defaultEmptyCollection );
	}
	
	/**
	 * Creates a new builder with the current properties except for sequences 
	 * of Strings.
	 * 
	 * @return
	 * 		The reference to the new builder.
	 */
	public TrieBuilder<String, T, C> forStrings()
	{
		return new TrieBuilder<String, T, C>( TrieSequencerCharSequence.INSTANCE, match, supplier, defaultValue, defaultEmptyCollection );
	}
	
	/**
	 * Creates a new builder with the current properties except for sequences 
	 * of case-insentive Strings.
	 * 
	 * @return
	 * 		The reference to the new builder.
	 */
	public TrieBuilder<String, T, C> forInsensitiveStrings()
	{
		return new TrieBuilder<String, T, C>( TrieSequencerCharSequenceCaseInsensitive.INSTANCE, match, supplier, defaultValue, defaultEmptyCollection );
	}
	
	/**
	 * Creates a new builder with the current properties except for sequences 
	 * of byte[].
	 * 
	 * @return
	 * 		The reference to the new builder.
	 */
	public TrieBuilder<byte[], T, C> forBytes()
	{
		return new TrieBuilder<byte[], T, C>( TrieSequencerByteArray.INSTANCE, match, supplier, defaultValue, defaultEmptyCollection );
	}
	
	/**
	 * Creates a new builder with the current properties except for sequences 
	 * of char[].
	 * 
	 * @return
	 * 		The reference to the new builder.
	 */
	public TrieBuilder<char[], T, C> forChars()
	{
		return new TrieBuilder<char[], T, C>( TrieSequencerCharArray.INSTANCE, match, supplier, defaultValue, defaultEmptyCollection );
	}
	
	/**
	 * Creates a new builder with the current properties except for sequences 
	 * of case-insensitive char[].
	 * 
	 * @return
	 * 		The reference to the new builder.
	 */
	public TrieBuilder<char[], T, C> forInsensitiveChars()
	{
		return new TrieBuilder<char[], T, C>( TrieSequencerCharArrayCaseInsensitive.INSTANCE, match, supplier, defaultValue, defaultEmptyCollection );
	}
	
	/**
	 * Creates a new builder with the current properties except for sequences 
	 * of int[].
	 * 
	 * @return
	 * 		The reference to the new builder.
	 */
	public TrieBuilder<int[], T, C> forInts()
	{
		return new TrieBuilder<int[], T, C>( TrieSequencerIntArray.INSTANCE, match, supplier, defaultValue, defaultEmptyCollection );
	}
	
	/**
	 * Creates a new builder with the current properties except for sequences 
	 * of long[].
	 * 
	 * @return
	 * 		The reference to the new builder.
	 */
	public TrieBuilder<long[], T, C> forLongs()
	{
		return new TrieBuilder<long[], T, C>( TrieSequencerLongArray.INSTANCE, match, supplier, defaultValue, defaultEmptyCollection );
	}
	
	/**
	 * Creates a new builder with the current properties except for sequences 
	 * of short[].
	 * 
	 * @return
	 * 		The reference to the new builder.
	 */
	public TrieBuilder<short[], T, C> forShorts()
	{
		return new TrieBuilder<short[], T, C>( TrieSequencerShortArray.INSTANCE, match, supplier, defaultValue, defaultEmptyCollection );
	}

	/**
	 * Creates a new builder with the current properties except with a 
	 * sequencer of the given type.
	 * 
	 * @return
	 * 		The reference to the new builder.
	 */
	public <NS, NQ extends TrieSequencer<NS>> TrieBuilder<NS, T, C> withSequencer( NQ sequencer )
	{
		return new TrieBuilder<NS, T, C>( sequencer, match, supplier, defaultValue, defaultEmptyCollection );
	}
	
	/**
	 * Creates a new builder with the current properties except for a new type
	 * and a collection type of ArrayList.
	 * 
	 * @return
	 * 		The reference to the new builder.
	 */
	public <V> TrieBuilder<S, V, ArrayList<V>> forType( Class<V> clazz )
	{
		return new TrieBuilder<S, V, ArrayList<V>>( sequencer, match, () -> new ArrayList<V>(), (V)null, defaultEmptyCollection );
	}
	
	/**
	 * Creates a new builder with the current properties except for a new 
	 * default match.
	 * 
	 * @return
	 * 		The reference to the new builder.
	 */
	public TrieBuilder<S, T, C> withMatch(TrieMatch match)
	{
		return new TrieBuilder<S, T, C>( sequencer, match, supplier, defaultValue, defaultEmptyCollection );
	}
	
	/**
	 * Creates a new builder with the current properties except with a new 
	 * collection supplier for TrieCollections.
	 * 
	 * @return
	 * 		The reference to the new builder.
	 */
	public <NC extends Collection<T>> TrieBuilder<S, T, NC> withCollections( Supplier<NC> supplier )
	{
		return new TrieBuilder<S, T, NC>( sequencer, match, supplier, defaultValue, defaultEmptyCollection );
	}
	
	/**
	 * Creates a new builder with the current properties except for Sets for 
	 * TrieCollections.
	 * 
	 * @return
	 * 		The reference to the new builder.
	 */
	public TrieBuilder<S, T, Set<T>> forSets()
	{
		return new TrieBuilder<S, T, Set<T>>( sequencer, match, () -> new HashSet<T>(), defaultValue, defaultEmptyCollection );
	}

	/**
	 * Creates a new builder with the current properties except for LinkedLists 
	 * for TrieCollections.
	 * 
	 * @return
	 * 		The reference to the new builder.
	 */
	public TrieBuilder<S, T, LinkedList<T>> forLinkedLists()
	{
		return new TrieBuilder<S, T, LinkedList<T>>( sequencer, match, () -> new LinkedList<T>(), defaultValue, defaultEmptyCollection );
	}

	/**
	 * Creates a new builder with the current properties except for ArrayLists 
	 * for TrieCollections.
	 * 
	 * @return
	 * 		The reference to the new builder.
	 */
	public TrieBuilder<S, T, ArrayList<T>> forArrayLists()
	{
		return new TrieBuilder<S, T, ArrayList<T>>( sequencer, match, () -> new ArrayList<T>(), defaultValue, defaultEmptyCollection );
	}

	/**
	 * Creates a new builder with the current properties except for Vectors 
	 * for TrieCollections.
	 * 
	 * @return
	 * 		The reference to the new builder.
	 */
	public TrieBuilder<S, T, Vector<T>> forVectors()
	{
		return new TrieBuilder<S, T, Vector<T>>( sequencer, match, () -> new Vector<T>(), defaultValue, defaultEmptyCollection );
	}

	/**
	 * Creates a new builder with the current properties except when a query 
	 * has no results it returns an empty collection for TrieCollections.
	 * 
	 * @return
	 * 		The reference to the new builder.
	 */
	public TrieBuilder<S, T, C> withDefaultEmptyCollection()
	{
		return new TrieBuilder<S, T, C>( sequencer, match, supplier, defaultValue, true );
	}

	/**
	 * Creates a new builder with the current properties except with the 
	 * given default value when a query finds no match.
	 * 
	 * @return
	 * 		The reference to the new builder.
	 */
	public TrieBuilder<S, T, C> withDefaultValue(T defaultValue)
	{
		return new TrieBuilder<S, T, C>( sequencer, match, supplier, defaultValue, defaultEmptyCollection );
	}
	
	/**
	 * Builds a Trie with the current Trie properties on the builder.
	 * 
	 * @return
	 * 		The reference to a new Trie with the given sequencer, default 
	 * 		value, and default match.
	 */
	public Trie<S, T> build()
	{
		final Trie<S, T> trie = new Trie<S, T>( sequencer, defaultValue );
		trie.defaultMatch = match;
		return trie;
	}
	
	/**
	 * Builds a TrieCollection with the current TrieCollection properties on 
	 * the builder.
	 * 
	 * @return
	 * 		The reference to a new TrieBuilder with the given sequencer,
	 * 		supplier, default empty collection, and default match.
	 */
	public TrieCollection<S, T, C> buildForCollection()
	{
		final TrieCollection<S, T, C> trie = new TrieCollection<S, T, C>( sequencer, supplier, defaultEmptyCollection );
		trie.defaultMatch = match;
		return trie;
	}
	
}
