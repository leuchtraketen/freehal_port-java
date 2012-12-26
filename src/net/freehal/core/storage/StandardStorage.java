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

import net.freehal.core.lang.Language;
import net.freehal.core.lang.Languages;
import net.freehal.core.util.FreehalFile;
import net.freehal.core.util.FreehalFiles;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.RuntimeUtils;

/**
 * A standard directory structure; the root directory is given to the
 * constructor, and the language and cache directories are called
 * {@code "lang_xy"} and {@code "cache_xy"} while {@code xy} is the current
 * language code from {@link Languages#getLanguage()} and
 * {@link Language#getCode()}.
 * 
 * @author "Tobias Schulz"
 */
public class StandardStorage implements Storage {
	private FreehalFile path;

	/**
	 * Construct a new directory structure with the given root directory.
	 * 
	 * @param path
	 *        the root directory
	 */
	public StandardStorage(File path) {
		check(path);
		this.path = FreehalFiles.getFile(path.getPath());
	}

	private void check(File path) {
		if (!path.exists()) {
			if (!path.mkdirs()) {
				LogUtils.e("Directory does not exist and is at a read-only location: " + path);
				RuntimeUtils.exit(1);
			}
		}

		if (path.isFile()) {
			LogUtils.e("Path is no directory: " + path);
			RuntimeUtils.exit(1);
		}
	}

	/**
	 * Construct a new directory structure with the given root directory.
	 * 
	 * @param path
	 *        the root directory
	 */
	public StandardStorage(FreehalFile path) {
		this.path = path;
	}

	/**
	 * Construct a new directory structure with the given root directory.
	 * 
	 * @param path
	 *        the root directory
	 */
	public StandardStorage(String path) {
		check(new File(path));
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
