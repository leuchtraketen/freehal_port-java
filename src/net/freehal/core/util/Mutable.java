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

/**
 * A wrapper for making immutable objects mutable and for real call by reference
 * method arguments.
 * 
 * @author "Tobias Schulz"
 * @param <A>
 */
public class Mutable<A> {
	private A a;

	/**
	 * Construct a new instance without any initial wrapped object set.
	 */
	public Mutable() {
		this.a = null;
	}

	/**
	 * Construct a new instance and set the wrapped object to the given
	 * parameter.
	 * 
	 * @param a
	 *        the object to wrap
	 */
	public Mutable(A a) {
		this.a = a;
	}

	/**
	 * Set a new wrapped object.
	 * 
	 * @param a
	 *        the new object to wrap
	 * @return this instance of {@link Mutable}
	 */
	public Mutable<A> set(A a) {
		this.a = a;
		return this;
	}

	/**
	 * Returns the currently wrapped object.
	 * 
	 * @return the currently wrapped object
	 */
	public A get() {
		return this.a;
	}

	/**
	 * Runs the wrapped object's {@link Object#toString()} method, or returns
	 * {@code "null"} as a string if the wrapped object is {@code null}.
	 */
	public String toString() {
		return a != null ? a.toString() : "null";
	}

	/**
	 * Runs the wrapped object's {@link Object#equals(Object)} method.
	 */
	public boolean equals(Object o) {
		if (o instanceof Mutable)
			return ((Mutable<?>) o).a.equals(a);
		else
			return o.equals(a);
	}
}
