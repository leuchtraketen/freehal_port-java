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
 * This interface is like {@link java.util.Map}, but for maps that can have more
 * than one value per key.
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
	 * @param a the key 
	 * @param b the value
	 * @return the value
	 */
	public B put(A a, B b);

	public Set<B> put(A a, Set<B> bs);

	public Set<java.util.Map.Entry<A, B>> entrySet();

	public int count(A a);

	public Collection<? extends A> keySet();

	public Set<B> get(A key);

	public int size();
}
