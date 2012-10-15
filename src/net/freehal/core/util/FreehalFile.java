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

import java.io.File;

/**
 * A {@link FreehalFile} is a wrapper for something like a file. For example,
 * this interface is implemented by
 * {@link net.freehal.compat.sunjava.StandardFreehalFile} by using a
 * {@link java.io.File} from the java standard library, but it's possible to use
 * a relational database system or any other way to store data too.
 * 
 * @author "Tobias Schulz"
 */
public interface FreehalFile extends Comparable<FreehalFile> {

	/**
	 * Creates a new freehal file instance from a file which is a child of this
	 * freehal file instance (which must be a directory to be a parent file!)
	 * 
	 * @param path
	 *        the path
	 * @return a freehal file object
	 */
	public FreehalFile getChild(String path);

	/**
	 * Creates a new freehal file instance from a file which is a child of this
	 * freehal file instance (which must be a directory to be a parent file!)
	 * 
	 * @param path
	 *        the path
	 * @return a freehal file object
	 */
	public FreehalFile getChild(FreehalFile path);

	/**
	 * Get a {@link java.io.File} from the java standard library which has the
	 * same path as this file.
	 * 
	 * @return return an instance of {@link java.io.File}
	 */
	public File getFile();

	/**
	 * Tests whether this file's path is absolute.
	 * 
	 * @see java.io.File#isAbsolute()
	 * @return {@code true} if this file's path is absolute, {@code false}
	 *         otherwise
	 */
	public boolean isAbsolute();

	/**
	 * Returns this file's absolute path string.
	 * 
	 * @see #isAbsolute()
	 * @see java.io.File#getAbsolutePath()
	 * @return the pathname string
	 */
	public String getAbsolutePath();

	/**
	 * Returns this file's path string as it as stored in this object, so it may
	 * be relative.
	 * 
	 * @see java.io.File#getPath()
	 * @return the pathname string
	 */
	public String getPath();

	/**
	 * Returns the name of this file or directory.
	 * 
	 * @see java.io.File#getName()
	 * @return the name of this file or directory
	 */
	public String getName();

	/**
	 * Tests whether this freehal file is a normal file.
	 * 
	 * @see java.io.File#isFile()
	 * @return {@code true} if and only if this freehal file exists and is a
	 *         normal file; {@code false} otherwise
	 */
	public boolean isFile();

	/**
	 * Tests whether this freehal file is a directory.
	 * 
	 * @see java.io.File#isDirectory()
	 * @return {@code true} if and only if this freehal file exists and is a
	 *         directory; {@code false} otherwise
	 */
	public boolean isDirectory();

	/**
	 * Returns an array of freehal files contained in the directory represented
	 * by this freehal file.
	 * 
	 * @see java.io.File#listFiles()
	 * @return An array of freehal files in the directory represented by this
	 *         freehal file. The array will be empty if the directory is empty,
	 *         or if this freehal file is no directory, or if an I/O error
	 *         occurs.
	 */
	public FreehalFile[] listFiles();

	/**
	 * Returns the length of this freehal file.
	 * 
	 * @see java.io.File#length()
	 * @return the length in bytes, or 0L if the file does not exist
	 */
	public long length();

	/**
	 * Creates the directory named by this freehal file, including any necessary
	 * but nonexistent parent directories.
	 * 
	 * @return {@code true} if and only if the directory was created, along with
	 *         all necessary parent directories; {@code false} otherwise
	 */
	public boolean mkdirs();

	/**
	 * Deletes this file or directory.
	 * 
	 * @return {@code true} if and only if the file or directory is successfully
	 *         deleted; {@code false} otherwise
	 */
	public boolean delete();

	/**
	 * Returns a unique string which is - in most cases - the path name of this
	 * freehal file instance.
	 * 
	 * @return a string
	 */
	public String toString();

	/**
	 * Returns an string {@link java.lang.Iterable} which iterates over all
	 * lines from the given file.
	 * 
	 * @return the iterator
	 */
	public Iterable<String> readLines();

	/**
	 * Returns the content of the given file as a single string.
	 * 
	 * @return the iterator
	 */
	public String read();

	/**
	 * Appends the given string to the end of the given file.
	 * 
	 * @param s
	 *        the string to append
	 * @return the iterator
	 */
	public void append(String s);

	/**
	 * Writes the given string into the given file. If the file already exists,
	 * it is overridden.
	 * 
	 * @param s
	 *        the string to write
	 * @return the iterator
	 */
	public void write(String s);

	/**
	 * Returns the amount of lines in this file.
	 * 
	 * @return the count of files
	 */
	public int countLines();
}
