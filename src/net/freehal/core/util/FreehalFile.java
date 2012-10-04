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

public interface FreehalFile extends Comparable<FreehalFile> {

	public FreehalFile create(String path);

	public FreehalFile create(String dir, String file);

	public File getFile();

	public boolean isAbsolute();

	public String getAbsolutePath();

	public String getPath();

	public String getName();

	public boolean isFile();

	public boolean isDirectory();

	public FreehalFile[] listFiles();

	public long length();

	public boolean mkdirs();

	public boolean delete();

	public String toString();

	public FileUtilsImpl getFileUtilsImpl();
}
