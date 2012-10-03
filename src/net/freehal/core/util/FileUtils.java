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

public class FileUtils {

	public static Iterable<String> readLines(FreehalFile f) {
		return f.getFileUtilsImpl().readLines(f);
	}

	public static Iterable<String> readLines(FreehalFile d, FreehalFile f) {
		return d.getFileUtilsImpl().readLines(FreehalFiles.create(d.getAbsolutePath(), f.getPath()));
	}

	public static List<String> readLinesAsList(FreehalFile f) {
		return f.getFileUtilsImpl().readLinesAsList(f);
	}

	public static List<String> readLinesAsList(FreehalFile d, FreehalFile f) {
		return d.getFileUtilsImpl().readLinesAsList(FreehalFiles.create(d.getAbsolutePath(), f.getPath()));
	}

	public static String read(FreehalFile f) {
		return f.getFileUtilsImpl().read(f);
	}

	public static String read(FreehalFile d, FreehalFile f) {
		return d.getFileUtilsImpl().read(FreehalFiles.create(d.getAbsolutePath(), f.getPath()));
	}

	public static void append(FreehalFile f, String s) {
		f.getFileUtilsImpl().append(f, s);
	}

	public static void append(FreehalFile d, FreehalFile f, String s) {
		d.getFileUtilsImpl().append(FreehalFiles.create(d.getAbsolutePath(), f.getPath()), s);
	}

	public static void write(FreehalFile f, String s) {
		f.getFileUtilsImpl().write(f, s);
	}

	public static void write(FreehalFile d, FreehalFile f, String s) {
		d.getFileUtilsImpl().write(FreehalFiles.create(d.getAbsolutePath(), f.getPath()), s);
	}

	public static void delete(FreehalFile directory) {
		directory.getFileUtilsImpl().delete(directory);
	}

	public static void delete(FreehalFile d, FreehalFile f) {
		d.getFileUtilsImpl().delete(FreehalFiles.create(d.getAbsolutePath(), f.getPath()));
	}
}
