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
package net.freehal.core.storage;

import net.freehal.core.util.FreehalFile;

/**
 * A directory structure for freehal.
 * 
 * @author "Tobias Schulz"
 */
public interface Storage {

	/**
	 * Returns the so-called language directory which contains the database
	 * files.
	 * 
	 * @return an instance of {@link FreehalFile} representing the language
	 *         directory
	 */
	public FreehalFile getLanguageDirectory();

	/**
	 * Returns the root of the directory structure which can be used by freehal
	 * (not the system root directory!)
	 * 
	 * @return an instance of {@link FreehalFile} representing the root
	 *         directory
	 */
	public FreehalFile getPath();

	/**
	 * Returns the directory for cache files.
	 * 
	 * @return an instance of {@link FreehalFile} representing the cache
	 *         directory
	 */
	public FreehalFile getCacheDirectory();
}
