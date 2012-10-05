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

import net.freehal.core.util.FreehalFile;
import java.util.List;

/**
 * An utility class for basic file operations (read, write, append, delete, ...)
 * which runs the corresponding method of the {@link FileUtilsImpl}
 * implementation of the given {@link FreehalFile} via
 * {@link FreehalFile#getFileUtilsImpl()}.
 * 
 * @author "Tobias Schulz"
 */
public class FileUtils {

	/**
	 * Returns an string {@link java.lang.Iterable} which iterates over all
	 * lines from the given file.
	 * 
	 * @param f
	 *        the file to read
	 * @return the iterator
	 */
	public static Iterable<String> readLines(FreehalFile f) {
		return f.getFileUtilsImpl().readLines(f);
	}

	/**
	 * Returns an string {@link java.lang.Iterable} which iterates over all
	 * lines from the given file.
	 * 
	 * @param d
	 *        the directory the file is in
	 * @param f
	 *        the file to read
	 * @return the iterator
	 */
	public static Iterable<String> readLines(FreehalFile d, FreehalFile f) {
		return d.getFileUtilsImpl().readLines(FreehalFiles.create(d.getAbsolutePath(), f.getPath()));
	}

	/**
	 * Returns an list which contains all lines from the given file.
	 * 
	 * @param f
	 *        the file to read
	 * @return the iterator
	 */
	public static List<String> readLinesAsList(FreehalFile f) {
		return f.getFileUtilsImpl().readLinesAsList(f);
	}

	/**
	 * Returns an list which contains all lines from the given file.
	 * 
	 * @param d
	 *        the directory the file is in
	 * @param f
	 *        the file to read
	 * @return the iterator
	 */
	public static List<String> readLinesAsList(FreehalFile d, FreehalFile f) {
		return d.getFileUtilsImpl().readLinesAsList(FreehalFiles.create(d.getAbsolutePath(), f.getPath()));
	}

	/**
	 * Returns the content of the given file as a single string.
	 * 
	 * @param f
	 *        the file to read
	 * @return the iterator
	 */
	public static String read(FreehalFile f) {
		return f.getFileUtilsImpl().read(f);
	}

	/**
	 * Returns the content of the given file as a single string.
	 * 
	 * @param d
	 *        the directory the file is in
	 * @param f
	 *        the file to read
	 * @return the iterator
	 */
	public static String read(FreehalFile d, FreehalFile f) {
		return d.getFileUtilsImpl().read(FreehalFiles.create(d.getAbsolutePath(), f.getPath()));
	}

	/**
	 * Appends the given string to the end of the given file.
	 * 
	 * @param f
	 *        the file to write to
	 * @param s
	 *        the string to append
	 * @return the iterator
	 */
	public static void append(FreehalFile f, String s) {
		f.getFileUtilsImpl().append(f, s);
	}

	/**
	 * Appends the given string to the end of the given file.
	 * 
	 * @param d
	 *        the directory the file is in
	 * @param f
	 *        the file to write to
	 * @param s
	 *        the string to append
	 * @return the iterator
	 */
	public static void append(FreehalFile d, FreehalFile f, String s) {
		d.getFileUtilsImpl().append(FreehalFiles.create(d.getAbsolutePath(), f.getPath()), s);
	}

	/**
	 * Writes the given string into the given file. If the file already exists,
	 * it is overridden.
	 * 
	 * @param f
	 *        the file to write to
	 * @param s
	 *        the string to write
	 * @return the iterator
	 */
	public static void write(FreehalFile f, String s) {
		f.getFileUtilsImpl().write(f, s);
	}

	/**
	 * Writes the given string into the given file. If the file already exists,
	 * it is overridden.
	 * 
	 * @param d
	 *        the directory the file is in
	 * @param f
	 *        the file to write to
	 * @param s
	 *        the string to write
	 * @return the iterator
	 */
	public static void write(FreehalFile d, FreehalFile f, String s) {
		d.getFileUtilsImpl().write(FreehalFiles.create(d.getAbsolutePath(), f.getPath()), s);
	}

	/**
	 * Deletes the given file or directory.
	 * 
	 * @param f
	 *        the file or directory to delete
	 */
	public static void delete(FreehalFile f) {
		f.getFileUtilsImpl().delete(f);
	}

	/**
	 * Deletes the given file or directory.
	 * 
	 * @param d
	 *        the directory the file or directory is in
	 * @param f
	 *        the file or directory to delete
	 */
	public static void delete(FreehalFile d, FreehalFile f) {
		d.getFileUtilsImpl().delete(FreehalFiles.create(d.getAbsolutePath(), f.getPath()));
	}
}
