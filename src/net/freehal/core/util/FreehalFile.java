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
public class FreehalFile implements FreehalFileImpl {

	private FreehalFileImpl impl = null;

	public FreehalFile(FreehalFileImpl other) {
		if (impl instanceof FreehalFile)
			this.impl = ((FreehalFile) other).impl;
		else
			this.impl = other;
	}

	public FreehalFile(File path) {
		this.impl = FreehalFiles.createInstance("file", path.getPath());
	}

	public FreehalFile(String path) {
		this.impl = FreehalFiles.createInstance(path);
	}

	public FreehalFile(String protocol, String path) {
		this.impl = FreehalFiles.createInstance(protocol, path);
	}

	@Override
	public FreehalFile getChild(String path) {
		return impl.getChild(path);
	}

	@Override
	public FreehalFile getChild(FreehalFileImpl path) {
		return impl.getChild(path);
	}

	@Override
	public File getFile() {
		return impl.getFile();
	}

	@Override
	public boolean isAbsolute() {
		return impl.isAbsolute();
	}

	@Override
	public String getAbsolutePath() {
		return impl.getAbsolutePath();
	}

	@Override
	public String getPath() {
		return impl.getPath();
	}

	@Override
	public String getName() {
		return impl.getName();
	}

	@Override
	public boolean isFile() {
		return impl.isFile();
	}

	@Override
	public boolean isDirectory() {
		return impl.isDirectory();
	}

	@Override
	public FreehalFile[] listFiles() {
		FreehalFileImpl[] childrenImpls = impl.listFiles();
		FreehalFile[] children = new FreehalFile[childrenImpls.length];
		int i = 0;
		for (FreehalFileImpl childrenImpl : childrenImpls) {
			children[i++] = new FreehalFile(childrenImpl);
		}
		return children;
	}

	@Override
	public long length() {
		return impl.length();
	}

	@Override
	public boolean mkdirs() {
		return impl.mkdirs();
	}

	@Override
	public boolean delete() {
		return impl.delete();
	}

	@Override
	public String toString() {
		return impl.toString();
	}

	@Override
	public Iterable<String> readLines() {
		return impl.readLines();
	}

	@Override
	public String read() {
		return impl.read();
	}

	@Override
	public void append(String s) {
		impl.append(s);
	}

	@Override
	public void write(String s) {
		impl.write(s);
	}

	@Override
	public int countLines() {
		return impl.countLines();
	}

	@Override
	public void touch() {
		impl.touch();
	}

	@Override
	public int compareTo(FreehalFileImpl o) {
		return impl.compareTo(o);
	}

	public FreehalFileImpl getImpl() {
		return impl;
	}
}
