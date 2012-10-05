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
package net.freehal.compat.sunjava;

import java.io.File;
import java.util.ArrayList;

import net.freehal.core.util.AbstractFreehalFile;
import net.freehal.core.util.FileUtilsImpl;
import net.freehal.core.util.FreehalFile;

public class StandardFreehalFile extends AbstractFreehalFile {

	private static final FileUtilsImpl utils = new StandardFileUtils();

	public StandardFreehalFile(File file) {
		super(file);
	}

	@Override
	public FreehalFile create(String path) {
		return new StandardFreehalFile(new File(path));
	}

	@Override
	public FreehalFile create(String dir, String file) {
		return new StandardFreehalFile(new File(dir, file));
	}

	@Override
	public boolean isFile() {
		return file.isFile();
	}

	@Override
	public boolean isDirectory() {
		return file.isDirectory();
	}

	@Override
	public FreehalFile[] listFiles() {
		ArrayList<FreehalFile> files = new ArrayList<FreehalFile>();
		File[] realFiles = file.listFiles();
		if (realFiles != null) {
			for (File realFile : realFiles) {
				files.add(this.create(realFile.getPath()));
			}
		}
		return files.toArray(new FreehalFile[realFiles.length]);
	}

	@Override
	public long length() {
		return file.length();
	}

	@Override
	public boolean mkdirs() {
		return file.mkdirs();
	}

	@Override
	public boolean delete() {
		return file.delete();
	}

	@Override
	public FileUtilsImpl getFileUtilsImpl() {
		return utils;
	}

	@Override
	public String toString() {
		return "{" + super.toString() + "}";
	}
}
