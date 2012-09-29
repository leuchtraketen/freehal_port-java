/*******************************************************************************
 * Copyright (c) 2006 - 2012 Tobias Schulz and Contributors.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl.html>.
 ******************************************************************************/
package net.freehal.core.util;

import java.io.File;
import java.util.List;

public class FileUtils {

	private static FileUtilsImpl instance = null;

	public static void set(FileUtilsImpl instance) {
		FileUtils.instance = instance;
	}
	
	public static Iterable<String> readLines(File f) {
		return instance.readLines(f);
	}
	
	public static Iterable<String> readLines(File d, File f) {
		return instance.readLines(new File(d.getAbsoluteFile(), f.getPath()));
	}

	public static List<String> readLinesAsList(File f) {
		return instance.readLinesAsList(f);
	}

	public static List<String> readLinesAsList(File d, File f) {
		return instance.readLinesAsList(new File(d.getAbsoluteFile(), f.getPath()));
	}

	public static String read(File f) {
		return instance.read(f);
	}

	public static String read(File d, File f) {
		return instance.read(new File(d.getAbsoluteFile(), f.getPath()));
	}

	public static void append(File f,
			String s) {
		instance.append(f, s);
	}

	public static void append(File d, File f,
			String s) {
		instance.append(new File(d.getAbsoluteFile(), f.getPath()), s);
	}

	public static void write(File f,
			String s) {
		instance.write(f, s);
	}

	public static void write(File d, File f,
			String s) {
		instance.write(new File(d.getAbsoluteFile(), f.getPath()), s);
	}
	
	public static void delete(File directory) {
		instance.delete(directory);
	}
	
	public static void delete(File d, File f) {
		instance.delete(new File(d.getAbsoluteFile(), f.getPath()));
	}
}
