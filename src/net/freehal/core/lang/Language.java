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
package net.freehal.core.lang;

/**
 * A language.
 * 
 * @author "Tobias Schulz"
 */
public interface Language {

	/**
	 * Returns the language code of this {@link Language} instance (like
	 * {@code "en"} or {@code "de"}).
	 * 
	 * @return the language code
	 */
	public String getCode();

	/**
	 * Check whether the given language code is equal to the language code in
	 * this {@link Language} instance.
	 * 
	 * @param otherCode
	 *        the other language code
	 * @return {@code true} if they are equal, {code false} otherwise.
	 */
	public boolean isCode(String otherCode);
}
