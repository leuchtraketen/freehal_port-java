/*******************************************************************************
 * Copyright (c) 2006 - 2012 Tobias Schulz and Contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/gpl.html>.
 ******************************************************************************/
package net.freehal.core.util;

import java.util.Collection;
import java.util.Set;

/**
 * A {@link MultiMap} is a map that can have more than one value per key.
 * 
 * @author "Tobias Schulz"
 * @param <A>
 *        the key's type
 * @param <B>
 *        the value's type
 */
public interface MultiMap<A, B> {

	/**
	 * Put a key and a value into the map.
	 * 
	 * @param a
	 *        the key
	 * @param b
	 *        the value
	 * @return the value
	 */
	public B put(A a, B b);

	/**
	 * Put a key and a collection of values into the map by merging the
	 * parameter and the current value.
	 * 
	 * @param a
	 *        the key
	 * @param bs
	 *        the collection of values
	 * @return the collection of values
	 */
	public Collection<B> put(A a, Collection<B> bs);

	/**
	 * Returns an unmodifiable set of entries.
	 * 
	 * @see java.util.Collections#unmodifiableSet(Set)
	 * @return an unmodifiable set of entries.
	 */
	public Set<java.util.Map.Entry<A, B>> entrySet();

	/**
	 * How many values does the given key have?
	 * 
	 * @param key
	 *        the key
	 * @return the amount of values
	 */
	public int count(A key);

	/**
	 * Returns an unmodifiable collection of keys.
	 * 
	 * @see java.util.Collections#unmodifiableCollection(Collection)
	 * @return an unmodifiable collection of entries.
	 */
	public Collection<? extends A> keySet();

	/**
	 * Returns the set of values that are assigned to the given key
	 * 
	 * @param key
	 *        the key
	 * @return the corresponding values
	 */
	public Set<B> get(A key);

	/**
	 * How many keys are in this map?
	 * 
	 * @return the amount of keys
	 */
	public int size();
}
