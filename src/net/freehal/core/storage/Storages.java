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
 * An utility class for holding the currently used database directory structure.
 * 
 * @author "Tobias Schulz"
 */
public class Storages {

	private static Storage storage = new IllegalStorage();

	private Storages() {}

	/**
	 * Set the directory structure to use.
	 * 
	 * @param storage
	 *        an instance of a class implementing the {@link Storage} interface
	 */
	public static void setStorage(Storage storage) {
		Storages.storage = storage;
	}

	/**
	 * Returns the so-called language directory which contains the database
	 * files.
	 * 
	 * @return the language directory
	 */
	public static FreehalFile getLanguageDirectory() {
		return storage.getLanguageDirectory();
	}

	/**
	 * Returns the root of the directory structure used by freehal.
	 * 
	 * @return the root directory
	 */
	public static FreehalFile getPath() {
		return storage.getPath();
	}

	/**
	 * Returns the cache directory.
	 * 
	 * @return the cache directory
	 */
	public static FreehalFile getCacheDirectory() {
		return storage.getCacheDirectory();
	}

	/**
	 * Returns a file object for a file inside the language directory.
	 * 
	 * @see FreehalFile#getChild(FreehalFile)
	 * @param f
	 *        the relative path inside the language directory
	 * @return an instance of {@link FreehalFile} representing the resulting
	 *         file
	 */
	public static FreehalFile inLanguageDirectory(FreehalFile f) {
		return storage.getLanguageDirectory().getChild(f);
	}

	/**
	 * Returns a file object for a file inside the language directory.
	 * 
	 * @see FreehalFile#getChild(String)
	 * @param f
	 *        the relative path inside the language directory
	 * @return an instance of {@link FreehalFile} representing the resulting
	 *         file
	 */
	public static FreehalFile inLanguageDirectory(String f) {
		return storage.getLanguageDirectory().getChild(f);
	}

	/**
	 * Returns a file object for a file inside the root directory.
	 * 
	 * @see FreehalFile#getChild(FreehalFile)
	 * @param f
	 *        the relative path inside the root directory
	 * @return an instance of {@link FreehalFile} representing the resulting
	 *         file
	 */
	public static FreehalFile inPath(FreehalFile f) {
		return storage.getPath().getChild(f);
	}

	/**
	 * Returns a file object for a file inside the root directory.
	 * 
	 * @see FreehalFile#getChild(String)
	 * @param f
	 *        the relative path inside the root directory
	 * @return an instance of {@link FreehalFile} representing the resulting
	 *         file
	 */
	public static FreehalFile inPath(String f) {
		return storage.getPath().getChild(f);
	}

	/**
	 * Returns a file object for a file inside the cache directory.
	 * 
	 * @see FreehalFile#getChild(FreehalFile)
	 * @param f
	 *        the relative path inside the cache directory
	 * @return an instance of {@link FreehalFile} representing the resulting
	 *         file
	 */
	public static FreehalFile inCacheDirectory(FreehalFile f) {
		return storage.getCacheDirectory().getChild(f);
	}

	/**
	 * Returns a file object for a file inside the cache directory.
	 * 
	 * @see FreehalFile#getChild(String)
	 * @param f
	 *        the relative path inside the cache directory
	 * @return an instance of {@link FreehalFile} representing the resulting
	 *         file
	 */
	public static FreehalFile inCacheDirectory(String f) {
		return storage.getCacheDirectory().getChild(f);
	}

	private static class IllegalStorage implements Storage {
		@Override
		public FreehalFile getLanguageDirectory() {
			throw new UnsupportedOperationException();
		}

		@Override
		public FreehalFile getPath() {
			throw new UnsupportedOperationException();
		}

		@Override
		public FreehalFile getCacheDirectory() {
			throw new UnsupportedOperationException();
		}
	}
}
