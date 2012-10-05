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

import java.io.File;

import net.freehal.core.lang.Languages;
import net.freehal.core.util.FreehalFile;
import net.freehal.core.util.FreehalFiles;

public class StandardStorage implements Storage {
	private FreehalFile path;

	public StandardStorage(File path) {
		this.path = FreehalFiles.getFile(path.getPath());
	}

	public StandardStorage(FreehalFile path) {
		this.path = path;
	}

	public StandardStorage(String path) {
		this.path = FreehalFiles.getFile(path);
	}

	@Override
	public FreehalFile getPath() {
		return path;
	}

	@Override
	public FreehalFile getLanguageDirectory() {
		return path.getChild("lang_" + Languages.getLanguage().getCode());
	}

	@Override
	public FreehalFile getCacheDirectory() {
		return path.getChild("cache_" + Languages.getLanguage().getCode());
	}
}
