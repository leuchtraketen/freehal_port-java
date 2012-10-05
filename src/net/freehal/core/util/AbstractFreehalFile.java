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
 * An abstract basic implementation of a {@link FreehalFile} which uses
 * {@link java.io.File} from the standard library for the name/path resolution
 * and management. The place where the data is stored must be specified by a
 * subclass.
 * 
 * @author "Tobias Schulz"
 */
public abstract class AbstractFreehalFile implements FreehalFile {
	
	/**
	 * The corresponding standard {@link java.io.File} instance from the
	 * standard library. It is at least used for the name/path management.
	 */
	protected File file;

	@SuppressWarnings("unused")
	private AbstractFreehalFile() {
		this.file = null;
	}

	/**
	 * Creates a new freehal file instance from a given {@link java.io.File}
	 * from the standard library.
	 * 
	 * @param file
	 *        the file
	 */
	public AbstractFreehalFile(File file) {
		this.file = file;
	}

	@Override
	public String toString() {
		return file.toString();
	}

	@Override
	public int compareTo(FreehalFile o) {
		return file.getAbsolutePath().compareTo(o.getAbsolutePath());
	}

	@Override
	public int hashCode() {
		return getAbsolutePath().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof FreehalFile)
			return getAbsolutePath().equals(((FreehalFile) o).getAbsolutePath());
		else
			return false;
	}

	@Override
	public abstract FreehalFile create(String path);

	@Override
	public abstract FreehalFile create(String dir, String file);

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public boolean isAbsolute() {
		return file.isAbsolute();
	}

	@Override
	public String getAbsolutePath() {
		return file.getAbsolutePath();
	}

	@Override
	public String getPath() {
		return file.getPath();
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public abstract boolean isFile();

	@Override
	public abstract boolean isDirectory();

	@Override
	public abstract FreehalFile[] listFiles();

	@Override
	public abstract long length();

	@Override
	public abstract boolean mkdirs();

	@Override
	public abstract boolean delete();

	@Override
	public abstract FileUtilsImpl getFileUtilsImpl();
}
